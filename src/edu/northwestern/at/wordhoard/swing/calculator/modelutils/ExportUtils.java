package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.Err;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**	Utilities for exporting user-defined database objects to XML.
 */

public class ExportUtils
{
	/**	XML extension file filter. */

	protected static FileExtensionFilter xmlFilter	=
			new FileExtensionFilter( new String[]{ ".xml" } , "XML Files" );

	/**	Export a list of objects to XML.
	 *
	 *	@param	objects				The objects to export.
	 *	@param	parentWindow		Parent window for file dialog.
	 *	@param	exportedOK			Array of booleans of same length as objects.
	 *								On output each element will be set true if
	 *								the corresponding object was successfully
	 *								persisted, false otherwise.
	 *	@param	progressReporter	Progress reporter.   May be null.
	 *
	 *	@return						true if export went OK, false otherwise.
	 *
	 *	@throws
	 *		ExportException to wrap I/O, DOM, etc. errors.
	 *
	 *	<p>
	 *	Prompts for a file name to which to export the objects.
	 *	</p>
	 */

	public static boolean exportObjects
	(
		UserDataObject[] objects ,
		Window parentWindow ,
		boolean[] exportedOK ,
		ProgressReporter progressReporter
	)
		throws ExportException
	{
								//	Assume export fails.

		boolean result	= false;

								//	No objects?  Return.

		if ( ( objects == null ) || ( objects.length == 0 ) ) return result;

								//	Export OK array wrong length?  Return.

		if	(	( exportedOK == null ) ||
				( exportedOK.length != objects.length ) ) return result;

								//	Add ".xml" file filter to file dialogs.

		FileDialogs.addFileFilter( xmlFilter );

								//	Display save file dialog to get name
								//	of xml export file.

		String[] exportFile	= FileDialogs.save( parentWindow );

								//	Clear the ".xml" file filter.

		FileDialogs.clearFileFilters();

								//	If a file was specified,
								//	write the objects in xml format
								//	to that file.

		if ( exportFile != null )
		{
			File file	= new File( exportFile[ 0 ] , exportFile[ 1 ] );

			result	=
				exportObjects
				(
					objects ,
					exportedOK ,
					file.getAbsolutePath() ,
					progressReporter
				);
		}

		return result;
	}

	/**	Export a list of objects to XML.
	 *
	 *	@param	objects				Objects to export.
	 *	@param	exportedOK			Array of booleans of same length as objects.
	 *								On output each element will be set true if
	 *								the corresponding object was successfully
	 *								persisted, false otherwise.
	 *	@param	exportFileName		File to which to write the exported objects.
	 *	@param	progressReporter	Progress reporter.  May be null.
	 *
	 *
	 *	@return						true if all objects exported,
	 *								false otherwise.
	 */

	public static boolean exportObjects
	(
		UserDataObject[] objects ,
		boolean[] exportedOK ,
		String exportFileName ,
		ProgressReporter progressReporter
	)
		throws ExportException
	{
		boolean result	= false;

								//	Nothing to export?  Quit.

		if ( ( objects == null ) || ( objects.length == 0 ) ) return result;

								//	Bad file?  Quit.

		if	(	( exportFileName == null ) ||
				( exportFileName.length() == 0 ) ) return result;

								//	Bad exportedOK array?  Quit.

		if	(	( exportedOK == null ) ||
				( exportedOK.length != objects.length ) ) return result;

								//	Assume nothing exported correctly.

		for ( int i = 0 ; i < exportedOK.length ; i++ )
		{
			exportedOK[ i ]	= false;
		}
								//	Create a new DOM document to which to
								//	add each object to export.

		Document document	= newExportDocument();

								//	Bad document?  Quit.

		if ( document == null )
		{
       		throw new ExportException
       		(
				WordHoardSettings.getString
				(
					"Unabletocreateexportdocument" ,
					"Unable to create xml document for export."
				)
       		);
		}
             					//	Update progress reporter if supplied.

		if ( progressReporter != null )
		{
			progressReporter.setTitle
			(
				WordHoardSettings.getString
				(
					"Exporting" ,
					"Exporting"
				)
			);

			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Exportingdot" ,
					"Exporting ..."
				)
			);

			progressReporter.setMaximumBarValue( objects.length );
			progressReporter.setIndeterminate( false );
		}
								//	Loop over objects to export and
								//	add each to the DOM document.
		result	= true;

		for ( int i = 0 ; i < objects.length ; i++ )
		{
			UserDataObject object	= objects[ i ];

			if ( !PersistenceManager.doContains( object ) )
			{
				PersistenceManager.doLoad
				(
					object.getClass() ,
					((PersistentObject)object).getId()
				);
			}

			try
			{
				exportedOK[ i ]	=
					object.addToDOMDocument( document );
			}
			catch ( Exception e )
			{
				Err.err( e );
			}

			if ( progressReporter != null )
			{
				progressReporter.updateProgress
				(
					i + 1 ,
					new PrintfFormat
					(
						WordHoardSettings.getString
						(
							"Convertingtoxml" ,
							"Converting %s to xml"
						)
					).sprintf( object.getTitle() )
				);

				if ( progressReporter.isCancelled() )
				{
					throw new ExportException
					(
						WordHoardSettings.getString
						(
							"Exportcancelled" ,
							"Export cancelled."
						)
					);
				}
           	}

			result	= result && exportedOK[ i ];
		}
								//	If all the objects were added to the
								//	DOM documents, export the DOM document
								//	in XML form to the specified file.
		if ( result )
		{
			if ( progressReporter != null )
			{
				progressReporter.updateProgress
				(
					WordHoardSettings.getString
					(
						"Savingtoxmlfile" ,
						"Saving to xml file..."
					)
				);
    	    }
								//	Try saving the document to the
								//	specified file.
			try
			{
				DOMUtils.save( document , exportFileName );
			}
			catch ( Exception e )
			{
				result	= false;

				throw new ExportException
				(
					new PrintfFormat
					(
						WordHoardSettings.getString
						(
							"Errorwhilewritingfile" ,
							"Error while writing file: %s"
						)
					).sprintf( e.getMessage() )
				);
			}
		}
								//	Say we're done.

		if ( progressReporter != null )
		{
			progressReporter.updateProgress
			(
				WordHoardSettings.getString( "Done" , "Done" )
			);
		}

		return result;
	}

	/** Create new empty DOM document.
	 * @return	The new document.
	 */

	public static org.w3c.dom.Document newExportDocument()
	{
		org.w3c.dom.Document result	= null;

		try
		{
								//	Create a new DOM document.

			result	= DOMUtils.newDocument();

								//	Create wordhoard root element.

			org.w3c.dom.Element rootElement	=
				result.createElement( "wordhoard" );

			result.appendChild( rootElement );
		}
		catch ( Exception e )
		{
			Err.err( e );
		}

		return result;
	}

	/**	Export common header information for UserDataObject to DOM document.
	 *
	 *	@param	userDataObject	User data object to add to DOM document.
	 *	@param	document		DOM document to which to add user data object.
	 *							Must not be null.  In most cases,
	 *							this document should have a "wordhoard"
	 *							node as the root element.
	 *
	 *	@return					Root element in DOM document for
	 *							user data object.
	 */

	public static org.w3c.dom.Element addUserDataObjectHeaderToDOM
	(
		UserDataObject userDataObject ,
		org.w3c.dom.Document document
	)
	{
								//	Indicate failure if document null.

		if ( document == null ) return null;

								//	Indicate failure if user data object null.

		if ( userDataObject == null ) return null;

		org.w3c.dom.Element userDataObjectElement	= null;

		try
		{
								//	Get root element of document.
								//	We don't check if it is "wordhoard"
								//	in case we might want to use this
								//	code in another context than just
								//	exporting the work set to a WordHoard
								//	import file.

			org.w3c.dom.Node rootElement	=
				document.getDocumentElement();

								//	Create enclosing element for this
								//	user data object type.

			String userDataObjectTypeName	=
				ClassUtils.unqualifiedName
				(
					userDataObject.getClass().getName()
				).toLowerCase();

			userDataObjectElement	=
				document.createElement( userDataObjectTypeName );

								//	Create elements common to all
								//	UserDataObject types:
								//		-- title
								//		-- description
								//		-- webpageurl
								//		-- ispublic
								//		-- query

			org.w3c.dom.Attr titleAttribute		=
				document.createAttribute( "title" );

			titleAttribute.setValue( userDataObject.getTitle() );

			org.w3c.dom.Attr descriptionAttribute		=
				document.createAttribute( "description" );

			descriptionAttribute.setValue( userDataObject.getDescription() );

			org.w3c.dom.Attr webPageURLAttribute		=
				document.createAttribute( "webpageurl" );

			webPageURLAttribute.setValue( userDataObject.getWebPageURL() );

			org.w3c.dom.Attr isPublicAttribute	=
				document.createAttribute( "ispublic" );

			isPublicAttribute.setValue(
				userDataObject.getIsPublic() + "" );

			org.w3c.dom.Attr queryAttribute	=
				document.createAttribute( "query" );

			queryAttribute.setValue
			(
				StringUtils.safeString( userDataObject.getQuery() )
			);

			userDataObjectElement.setAttributeNode( titleAttribute );
			userDataObjectElement.setAttributeNode( descriptionAttribute );
			userDataObjectElement.setAttributeNode( webPageURLAttribute );
			userDataObjectElement.setAttributeNode( isPublicAttribute );
			userDataObjectElement.setAttributeNode( queryAttribute );

			rootElement.appendChild( userDataObjectElement );
		}
		catch ( Exception e )
		{
			Err.err( e );
		}
		 								//	Return root for this data object.

		return userDataObjectElement;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected ExportUtils()
	{
	}
}

/*
 * <p>
 * Copyright &copy; 2004-2011 Northwestern University.
 * </p>
 * <p>
 * This program is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * </p>
 * <p>
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more
 * details.
 * </p>
 * <p>
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA.
 * </p>
 */

