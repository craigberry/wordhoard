package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.text.TextParams;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

import org.hibernate.*;
import org.hibernate.cfg.*;

/**	Persist word occurrences related counts for WordHoard Calculator.
 *
 *	<p>Usage:</p>
 *
 *	<p><code>LoadPibCounts dbname username password</code></p>
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */

public class LoadPibCounts
{
	/**	Persistence manager for object model database.
	 */

	private static PersistenceManager pm		= null;

	/**	JDBC connection to object model database.
	 */

	private static Connection modelConnection	= null;

	/**	Maps work parts to their parents. */

	private static HashMap workPartToParentMap	= new HashMap();

	/**	Maps work parts to their works. */

	private static HashMap workPartToWorkMap	= new HashMap();

	/**	Global start time. */

	private static long globalStartTime;

	/**	Print elapsed time.
	 *
	 *	@param	startTime	Starting time.
	 *	@param	note		Note to display along with elapsed time.
	 */

	private static void elapsedTime( String note , long startTime )
	{
		long timeNow	= System.currentTimeMillis();
		long timeDiff	= timeNow - startTime;

		timeDiff		= (int)( ( timeDiff + 999 ) / 1000 );

		System.out.println( note + timeDiff + " seconds." );

		timeDiff	= timeNow - globalStartTime;
		timeDiff	= (int)( ( timeDiff + 999 ) / 1000 );

		System.out.println( "Elapsed time is " + timeDiff + " seconds." );

		System.gc();

		Runtime runTime	= Runtime.getRuntime();

		long freeMem	= runTime.freeMemory();
		long totalMem	= runTime.totalMemory();

		System.out.println
		(
			"Memory status: free memory=" +
			StringUtils.formatNumberWithCommas( freeMem ) +
			", total memory=" +
			StringUtils.formatNumberWithCommas( totalMem )
		);
	}

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
								//	Get a persistence manager.

		pm				= PersistenceManager.getPM();

								//	Get JDBC connection.

		modelConnection	= pm.getConnection();
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

	/**	Deletes all old entries in the count tables.
	 *
	 *	@throws	Exception
	 */

	private static void deleteOldEntries()
		throws Exception
	{
		long startTime		= System.currentTimeMillis();

		pm.deleteViaSQL( "delete from totalwordformcount" );
		pm.deleteViaSQL( "delete from wordcount" );

		long timeDiff	= System.currentTimeMillis() - startTime;
		timeDiff		= (int)( ( timeDiff + 999 ) / 1000 );

		elapsedTime( "   Deleted existing table entries in " , startTime );
	}

	/**	Persist word counts from a text file.
	 *
	 *	@param	countsFileName			Name of file with word counts.
	 *	@param	totalCountsFileName		Name of file with total word counts.
	 */

	private static void persistWordCounts
	(
		String countsFileName ,
		String totalCountsFileName
	)
		throws Exception
	{
		long startTime	= System.currentTimeMillis();

		System.out.println( "===== Persisting word counts =====" );

		PreparedStatement wordCountsStatement	=
			modelConnection.prepareStatement
			(
				"load data infile '" + countsFileName +
				"' into table wordcount " +
				"fields terminated by '\t' " +
				"lines starting by '' " +
				"terminated  by '\n' " +
				"(word_string, word_charset, wordForm, " +
				"workPart, work, wordCount)"
			);

		int updateCount	= wordCountsStatement.executeUpdate();
		wordCountsStatement.close();

		elapsedTime(
			"   Persisted " + updateCount + " word counts in " , startTime );

		System.out.println( "===== Persisting total word counts =====" );

		startTime	= System.currentTimeMillis();

		PreparedStatement totalWordCountsStatement	=
			modelConnection.prepareStatement
			(
				"load data infile '" + totalCountsFileName +
				"' into table totalwordformcount " +
				"fields terminated by '\t' " +
				"lines starting by '' " +
				"terminated  by '\n' "  +
				"(wordForm, workPart, work, wordFormCount)"
			);

		updateCount	= totalWordCountsStatement.executeUpdate();
		totalWordCountsStatement.close();

		elapsedTime(
			"   Persisted " + updateCount + " total word counts in " ,
			startTime );
	}

	/**	Main program.
	 *
	 *	@param	args	Command line arguments.
	 */

	public static void main( String args[] )
	{
		globalStartTime				= System.currentTimeMillis();
		long timeDiff				= 0;
		Exception savedException	= null;

		try
		{
								//	Initialize database access.
			init( args );
								//	Delete existing count entries.

			deleteOldEntries();

								//	Persist word counts.

			persistWordCounts
			(
				"j:/texts/zztemp1.txt" ,
				"j:/texts/zztemp2.txt"
			);
		}
		catch ( Exception e )
		{
			savedException	= e;
		}
		finally
		{
								//	Close down database access.
			try
			{
				term();
			}
			catch ( Exception ignored )
			{
			}
		}
	}

	/**	Hides the default no-arg constructor.
	 */

	private LoadPibCounts()
	{
		throw new UnsupportedOperationException();
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

