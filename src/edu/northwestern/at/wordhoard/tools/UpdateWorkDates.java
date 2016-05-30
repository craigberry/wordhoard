package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	Adds work dates for Chaucer.
 */

public class UpdateWorkDates
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

	/**	Update work dates.
	 *
	 *	@param	csvFileName		CSV format file with date data.
	 *
	 *	@throws	Exception
	 *
	 *	<p>
	 *	Each data row in the CSV file takes the form:
	 *	</p>
	 *
	 *	<code>
	 *	earlyDate;lateDate;title
	 *	</code>
	 */

	private static void updateWorkDates( String csvFileName )
		throws Exception
	{
								//	Remember current time so we can compute
								//	work set creation time.

		long startTime		= System.currentTimeMillis();

								//	Load date information into maps from data file.

		TreeMap dateMap	= new TreeMap();

		BufferedReader csvFileReader	=
			new BufferedReader( new FileReader( csvFileName ) );

		String csvLine	= "";

		while( ( csvLine = csvFileReader.readLine() ) != null )
		{
			String row[]	 = csvLine.split( ";" );

			if ( row.length >= 3 ) dateMap.put( row[ 2 ].toLowerCase() , row );
		}

		csvFileReader.close();

								//	Loop over works whose dates we are to
								//	update.

		Iterator iterator	= dateMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			String title		= (String)iterator.next();
			String[] dateData	= (String[])dateMap.get( title );

			Work work			= WorkUtils.getWork( title );

			if ( work != null )
			{
				int earlyDate		= Integer.parseInt( dateData[ 0 ] );
				int lateDate		= Integer.parseInt( dateData[ 1 ] );

//				work.setEarlyDate( new Integer( earlyDate ) );
//				work.setLateDate( new Integer( lateDate ) );

				work.setPubDate
				(
					new PubYearRange
					(
						new Integer( earlyDate )  ,
						new Integer( lateDate )
					)
				);

				PersistenceManager.doUpdate( work );

				System.out.println( "Updated dates for " + title );
			}
			else
			{
				System.out.println( "Unable to update dates for " + title );
			}
		}
								//	Report time needed to update work dates.

		long timeDiff	= System.currentTimeMillis() - startTime;
		timeDiff		= (int)( ( timeDiff + 999 ) / 1000 );

		System.out.println( "========================" );

		System.out.println(
			"Updated work dates in " + timeDiff + " seconds." );
	}

	/**	Main program.
	 *
	 *	@param	args	Command line arguments.
	 */

	public static void main( String args[] )
	{
		try
		{
			init( args );
			updateWorkDates( args[ 0 ] );
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

