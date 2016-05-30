package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	Exports user data objects to XML.
 *
 *	<p>Usage:</p>
 *
 *	<p><code>ExportUserData dbname username password</code></p>
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */

public class ExportUserData
{
	/**	Persistence manager.
	 */

	private static PersistenceManager pm	= null;

	/**	Initializes the program.
	 *
	 *	@param	args		Arguments.
	 *
	 *	@throws	Exception			Something bad happened.
	 */

	private static void init( String[] args )
		throws Exception
	{
								//	Initialize Hibernate.
		try
		{
			BuildUtils.initHibernate(args[0], args[1], args[2]);
		}
		catch ( Exception e )
		{
			e.printStackTrace();

			System.out.println(	"Could not initialize database access." );

			System.exit( 0 );
		}

		pm	= PersistenceManager.getPM();
	}

	/**	Terminates the program.
	 *
	 *	@throws	Exception	Something bad happened.
	 */

	private static void term()
		throws Exception
	{
		PersistenceManager.closePM( pm );
	}

	/**	Export all user defined objects to text file in XML format.
	 *
	 *	@param	xmlOutputFileName	XML output file name.
	 */

	private static void exportUserData( String xmlOutputFileName )
	{
								//	Get DOM document for output.

		Document document		= ExportUtils.newExportDocument();

								//	Get list of word query objects.

		System.out.print( "Getting list of word query objects  ..." );

		WHQuery[] wordQueries	= QueryUtils.getQueries( WHQuery.WORDQUERY );

		System.out.println( wordQueries.length + " found." );

								//	Get list of biblio query objects.

		System.out.print( "Getting list of biblio query objects  ..." );

		WHQuery[] biblioQueries	= QueryUtils.getQueries( WHQuery.WORKPARTQUERY );

		System.out.println( biblioQueries.length + " found." );

								//	Get list of word sets.

		System.out.print( "Getting list of word sets  ..." );

		WordSet[] wordSets		= WordSetUtils.getWordSets();

		System.out.println( wordSets.length + " found." );

								//	Get list of phrase sets.

		System.out.print( "Getting list of phrase sets  ..." );

		PhraseSet[] phraseSets	= PhraseSetUtils.getPhraseSets();

		System.out.println( phraseSets.length + " found." );

								//	Get list of work sets.

		System.out.print( "Getting list of work sets ..." );

		WorkSet[] workSets		= WorkSetUtils.getWorkSets();

		System.out.println( workSets.length + " found." );

								//	Report number of objects to export.

		int countOfObjectsToExport	=
			wordQueries.length + biblioQueries.length + wordSets.length +
			phraseSets.length + workSets.length;

		System.out.println(
			"Adding " + countOfObjectsToExport +
			" objects to DOM document ..." );

								//	Create array to hold all objects to
								//	export.

		UserDataObject[] objectsToExport	=
			new UserDataObject[ countOfObjectsToExport ];

								//	Copy all objects to export to array.
		int k = 0;

		for ( int i = 0 ; i < wordQueries.length ; i++ )
		{
			objectsToExport[ k++ ]	= wordQueries[ i ];
		}

		for ( int i = 0 ; i < biblioQueries.length ; i++ )
		{
			objectsToExport[ k++ ]	= biblioQueries[ i ];
		}

		for ( int i = 0 ; i < wordSets.length ; i++ )
		{
			objectsToExport[ k++ ]	= wordSets[ i ];
		}

		for ( int i = 0 ; i < phraseSets.length ; i++ )
		{
			objectsToExport[ k++ ]	= phraseSets[ i ];
		}

		for ( int i = 0 ; i < workSets.length ; i++ )
		{
			objectsToExport[ k++ ]	= workSets[ i ];
		}
								//	Flags indicating if associated object
								//	successfully exported.

		boolean[] exportedOK	= new boolean[ countOfObjectsToExport ];

								//	Add user objects to DOm document and
								//	export document to XML.

		boolean exportWentOK	=
			ExportUtils.exportObjects
			(
				objectsToExport ,
				exportedOK ,
				xmlOutputFileName ,
				null
			);
								//	Report if export OK.
		if ( exportWentOK )
		{
			System.out.println( "All objects added to export document." );
		}
		else
		{
			System.out.println( "Not all objects added to export document." );
		}
	}

	/**	Main program.
	 *
	 *	@param	args	Command line arguments.
	 */

	public static void main( String args[] )
	{
		try
		{
			init(args);
			exportUserData( args[ 0 ] );
			term();
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
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

