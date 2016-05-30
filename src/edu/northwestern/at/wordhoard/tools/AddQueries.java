package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;

import javax.swing.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.tree.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Add default queries.
 *
 *	<p>
 *	This creates and persists queries directly to the main
 *	WordHoard database without going through the WordHoard server.
 *	This means the database account must have full privileges to
 *	read, update, and delete objects from the WordHoard database.
 *	</p>
 *
 *	<p>Usage:</p>
 *
 *	<p><code>AddQueries dbname username password</code></p>
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */

public class AddQueries
{
	/**	Persistence manager used throughout.
	 */

	private static PersistenceManager pm	= null;

	/**	Initializes the program.
	 *
	 *	@param	args		Arguments.
	 *
	 *	@throws	Exception			Something bad happened.
	 */

	private static void init(String[] args)
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

	/**	Make a query.
	 *
	 *	@param	title		Query title.
	 *	@param	queryText	Query text.
	 */

	private static void makeQuery
	(
		String title ,
		String description ,
		int queryType ,
		String queryText
	)
	{
		System.out.println( "===== " + title + " =====" );

		try
		{
			WHQuery query	=
				new WHQuery
				(
					title ,
					description ,
					"" ,
					"system" ,
					true ,
					queryType ,
					queryText
				);

			pm.save( query );
		}
		catch ( PersistenceException e )
		{
			new ErrorMessage
			(
				"Error creating query: " + title
			);
		}
	}

	/** Make all the default queries.
	 */

	private static void makeQueries()
	{
		long startTime	= System.currentTimeMillis();

		makeQuery
		(
			"Words spoken by males" ,
			"Words spoken by male speakers" ,
			WHQuery.WORDQUERY ,
			"spg(M)"
		);

		makeQuery
		(
			"Words spoken by females" ,
			"Words spoken by female speakers" ,
			WHQuery.WORDQUERY ,
			"spg(F)"
		);

		makeQuery
		(
			"Words spoken by mortals" ,
			"Words spoken by mortal speakers" ,
			WHQuery.WORDQUERY ,
			"spm(M)"
		);

		makeQuery
		(
			"Words spoken by immortals" ,
			"Words spoken by immortal speakers" ,
			WHQuery.WORDQUERY ,
			"spm(I)"
		);

		long endTime	=
			( ( System.currentTimeMillis() - startTime ) + 999 ) / 1000;

		System.out.println(
			"*** Time to create all queries: " + endTime + " seconds");
	}

	/**	Delete all queries for system account.
	 */

	private static boolean deleteAllQueries()
	{
		boolean result			= false;
		java.util.List queries	= null;

		try
		{
			queries	=
				pm.query
				(
					"from WHQuery whq where owner='system' and " +
					"queryType=" + WHQuery.WORDQUERY
				);
        }
        catch ( Exception e )
        {
        	System.out.println(
        		"Error retrieving queries." );

        	return result;
        }

		if ( queries != null )
		{
			System.out.println(
				"There are " + queries.size() + " queries to delete." );

			try
			{
				pm.delete( queries );

				result	= true;

				System.out.println( "Queries deleted." );
			}
			catch ( Exception e )
			{
				System.out.println(
					"Error deleting existing queries." );
			}
		}
		else
		{
			System.out.println(
				"There are no queries to delete." );
		}

		return result;
	}

	/**	Main program.
	 *
	 *	@param	args	Command line arguments (unused).
	 */

	public static void main( String args[] )
	{
		try
		{
			init(args);

			if ( deleteAllQueries() )
			{
				makeQueries();
			}

			term();
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}

		System.exit( 0 );
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

