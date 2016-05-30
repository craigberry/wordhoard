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
import edu.northwestern.at.wordhoard.swing.calculator.*;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**	Utilities for importing user-defined database objects from XML.
 */

public class ImportUtils
{
	/**	XML extension file filter. */

	protected static FileExtensionFilter xmlFilter	=
			new FileExtensionFilter( new String[]{ ".xml" } , "XML Files" );

	/**	Get name of import file.
	 *
	 *	@param	parentWindow	Parent window for file dialog.
	 *
	 *	@return					Name of import XML file, or null if none given.
	 */

	public static String getImportFileName( Window parentWindow )
	{
		String result	= null;

								//	Add ".xml" file filter to file dialogs.

		FileDialogs.addFileFilter( xmlFilter );

								//	Display open file dialog to get name
								//	of xml import file.

		String[] importFile	= FileDialogs.open( parentWindow );

								//	Clear the ".xml" file filter.

		FileDialogs.clearFileFilters();

								//	If a file was specified,
								//	write the objects in xml format
								//	to that file.

		if ( importFile != null )
		{
			File file	= new File( importFile[ 0 ] , importFile[ 1 ] );
			result		= file.getAbsolutePath();
		}

		return result;
	}

	/**	Parse import xml file to DOM document.
	 *
	 *	@param	importFileName		The name of the xml file containing
	 *								the WordHoard items to import.
	 *
	 *	@return						The document.   Null if parse fails.
 	 */

	public static org.w3c.dom.Document getImportDocument
	(
		String importFileName
	)
	{
		org.w3c.dom.Document result	= null;

		try
		{
								//	Load the XML file.

			result	= DOMUtils.parse( importFileName );

								//	Get the root node.

			org.w3c.dom.Node rootElement	= result.getDocumentElement();

								//	If the root node is not "wordhoard",
								//	this is a bogus import file.

			if ( !rootElement.getNodeName().equals( "wordhoard" ) )
			{
				result	= null;
			}
		}
		catch ( Exception e )
		{
			Err.err( e );
		}

		return result;
	}

	/**	Check for a duplicate import item.
	 *
	 *	@param	object				The object to import.
	 *
	 *	@return						true if object duplicates name of
	 *								existing object.
	 */

	public static boolean isDuplicate( UserDataObject object )
	{
		boolean result			= true;

		String title			= object.getTitle();
		String userID			= WordHoardSettings.getUserID();
		UserDataObject item		= null;

		if ( ( title != null ) && ( userID != null ) )
		{
			if ( object instanceof PhraseSet )
			{
				item	=
					PhraseSetUtils.getPhraseSet( title , userID );
			}
			else if ( object instanceof WordSet )
			{
				item	=
					WordSetUtils.getWordSet( title , userID );
			}
			else if ( object instanceof WorkSet )
			{
				item	=
					WorkSetUtils.getWorkSet( title , userID );
			}
			else if ( object instanceof WHQuery )
			{
				WHQuery query	= (WHQuery)object;

				item	=
					QueryUtils.getQuery(
						title , userID , query.getQueryType() );
			}

			result	= ( item != null );
		}

		return result;
	}

	/**	Rename a duplicate import item.
	 *
	 *	@param	object				The object to import.
	 */

	public static void renameDuplicate( UserDataObject object )
	{
		if ( object == null ) return;

		int i			= 1;
		String title	= object.getTitle();

		while ( isDuplicate( object ) )
		{
			object.setTitle( title + "-" + i++ );
		}
	}

	/**	Persist an imported object.
	 *
	 *	@param	object	The imported object to persist.
	 *
	 *	<p>
	 *	Handles any special persistence needs for different types of
	 *	user data objects.
	 *	</p>
	 */

	protected static boolean persistImportedObject( UserDataObject object )
	{
		boolean result	= false;

		try
		{
			Long id	= new Long( -1 );

			if ( WordHoardSettings.getBuildProgramRunning() )
			{
				if ( PersistenceManager.doSave( object ) )
				{
					id	= object.getId();
				}
			}
			else
			{
				id	=
					WordHoard.getSession().createUserDataObject( object );
			}

			result	= ( id.longValue() >= 0 );
		}
		catch ( Exception e )
		{
		}

		return result;
	}

	/**	Import a list of objects from DOM document.
	 *
	 *	@param	objects				Objects to import.
	 *	@param	importedOK			Array of booleans of same length as objects.
	 *								On output each element will be set true if
	 *								the corresponding object was successfully
	 *								persisted, false otherwise.
	 *	@param	renameDuplicates	Rename duplicate items.
	 *								Items whose names already appear in
	 *								the database will not be persisted
	 *								unless renameDuplicates is true.
	 *	@param	progressReporter	Progress reporter.
	 *
	 *	@return						true if all objects imported,
	 *								false otherwise.
	 *
	 *	<p>
	 *	When renameDuplicates is true, the name of an import item which
	 *	duplicates an existing item name of the same type will be changed
	 *	to a non-duplicate name.  The modified item name is constructed by
	 *	appending "-n" to the existing item name, where "n" is an integer
	 *	starting at 1.
	 *	</p>
	 */

	public static boolean importObjects
	(
		UserDataObject[] objects ,
		boolean[] importedOK ,
		boolean renameDuplicates ,
		ProgressReporter progressReporter
	)
		throws ImportException
	{
		boolean result	= false;

								//	Nothing to import?  Quit.

		if ( ( objects == null ) || ( objects.length == 0 ) ) return result;

								//	Bad importedOK array?  Quit.

		if	(	( importedOK == null ) ||
				( importedOK.length != objects.length ) ) return result;

								//	Assume nothing imported correctly.

		for ( int i = 0 ; i < importedOK.length ; i++ )
		{
			importedOK[ i ]	= false;
		}
             					//	Update progress reporter if supplied.

		if ( progressReporter != null )
		{
			progressReporter.setTitle
			(
				WordHoardSettings.getString
				(
					"Importing" ,
					"Importing"
				)
			);

			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Importingdot" ,
					"Importing ..."
				)
			);

			progressReporter.setMaximumBarValue( objects.length );
			progressReporter.setIndeterminate( false );
		}
								//	Loop over objects to import and
								//	persist each.
		result	= true;

		for ( int i = 0 ; i < objects.length ; i++ )
		{
								//	Get next object to import.

			UserDataObject object	= objects[ i ];

                                //	Check is this item duplicates the name
                                //	of an existing object of the same type.
                                //	Skip import if we find one and we are not
                                //	renaming it.

			if ( isDuplicate( object ) )
			{
				if ( renameDuplicates )
				{
					renameDuplicate( object );
				}
				else
				{
					continue;
				}
			}

			if ( progressReporter.isCancelled() )
			{
				throw new ExportException
				(
					WordHoardSettings.getString
					(
						"Importcancelled" ,
						"Import cancelled."
					)
				);
			}
								//	Persist this object.
			try
			{
				importedOK[ i ]	= persistImportedObject( object );
			}
			catch ( Exception e )
			{
//				Err.err( e );
			}

			result	= result && importedOK[ i ];

								//	Update progress dialog.

			if ( progressReporter != null )
			{
				progressReporter.updateProgress
				(
					i + 1 ,
					new PrintfFormat
					(
						WordHoardSettings.getString
						(
							"Importingxml" ,
							"Importing %s from xml"
						)
					).sprintf( object.getTitle() )
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

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected ImportUtils()
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

