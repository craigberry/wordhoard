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

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	Add default word sets.
 *
 *	<p>Usage:</p>
 *
 *	<p><code>AddWordSets dbname username password</code></p>
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */

public class AddWordSets
{
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
	}

	/**	Terminates the program.
	 *
	 *	@throws	Exception	Something bad happened.
	 */

	private static void term()
		throws Exception
	{
		PersistenceManager.closePM( PersistenceManager.getPM() );
	}

	/**	Make a word set for one gender.
	 *
	 *	@param	title		Word set title.
	 *	@param	workSet		The work set.
	 *	@param	gender		The gender (M or F).
	 */

	private static void makeWordSetForOneGender
	(
		String title ,
		WorkSet workSet ,
		String gender
	)
	{
		String wordSetTitle	= "";

		if ( gender.equals( "M" ) )
		{
			wordSetTitle	= "Males in " + title;
		}
		else
		{
			wordSetTitle	= "Females in " + title;
		}

		System.out.println( "===== " + wordSetTitle + " =====" );

		long startTime	= System.currentTimeMillis();

		WordSet wordSet	=
			WordSetUtils.addWordSetUsingQuery
			(
				wordSetTitle ,
				"system" ,
				"" ,
				true ,
				new WordCounter( workSet ) ,
				"spg(\"" + gender + "\")" ,
				null ,
				null
			);

		long endTime	=
			( System.currentTimeMillis() - startTime + 999 ) / 1000;

		if ( wordSet == null )
		{
			System.out.println(
				"*** Word set creation failed for " + wordSetTitle +
				" after " + endTime + " seconds." );
		}
		else
		{
			System.out.println(
				"*** Time to create word set: " + endTime + " seconds." );
		}
	}

	/**	Make word sets for males and females.
	 *
	 *	@param	title		Word set title.
	 *	@param	workSet		The work set.
	 */

	private static void makeWordSet
	(
		String title ,
		WorkSet workSet
	)
	{
		makeWordSetForOneGender( title , workSet , "M" );
		makeWordSetForOneGender( title , workSet , "F" );
	}

	private static void makeOneWordSet()
	{
		long startTime	= System.currentTimeMillis();

								//	Get tragedies work set.

		WorkSet tragedies		=
			WorkSetUtils.getWorkSet( "Shakespeare Tragedies" );

		makeWordSetForOneGender(
			"Shakespeare Tragedies" ,
			tragedies ,
			"F" );

		long endTime	=
			( ( System.currentTimeMillis() - startTime ) + 999 ) / 1000;

		System.out.println(
			"*** Time to create word set: " + endTime + " seconds");
	}

	private static void makeBigWordSets()
	{
		long startTime	= System.currentTimeMillis();

								//	Get tragedies work set.

		WorkSet tragedies		=
			WorkSetUtils.getWorkSet( "Shakespeare Tragedies" );

								//	Get histories work set.

		WorkSet histories		=
			WorkSetUtils.getWorkSet( "Shakespeare Histories" );

		WorkSet tragediesAndHistories	=
			new WorkSet
			(
				"Shakespeare Histories and Tragedies" ,
				"system" ,
				"" ,
				"" ,
				false ,
				"" ,
				WorkSetUtils.getWorkParts( tragedies )
			);

		tragediesAndHistories.addWorkParts
		(
			WorkSetUtils.getWorkParts( histories )
		);
								//	Get word sets for combined tragedies
								//	and histories.

		makeWordSet(
			"Shakespeare Histories" ,
			histories );

		makeWordSet(
			"Shakespeare Tragedies" ,
			tragedies );

/*
		makeWordSet(
			"Shakespeare Histories and Tragedies" ,
			tragediesAndHistories );
*/
								//	Get comedies work set.

		WorkSet comedies		=
			WorkSetUtils.getWorkSet( "Shakespeare Comedies" );

								//	Get romances work set.

		WorkSet romances		=
			WorkSetUtils.getWorkSet( "Shakespeare Romances" );

		WorkSet comediesAndRomances	=
			new WorkSet
			(
				"Shakespeare Comedies and Romances" ,
				"system" ,
				"" ,
				"" ,
				false ,
				"" ,
				WorkSetUtils.getWorkParts( comedies )
			);

		comediesAndRomances.addWorkParts
		(
			WorkSetUtils.getWorkParts( romances )
		);

								//	Get word set for combined comedies
								//	and romances.
		makeWordSet(
			"Shakespeare Comedies" ,
			comedies );

		makeWordSet(
			"Shakespeare Romances" ,
			romances );
/*
		makeWordSet(
			"Shakespeare Comedies and Romances" ,
			comediesAndRomances );
*/
		long endTime	=
			( ( System.currentTimeMillis() - startTime ) + 999 ) / 1000;

		System.out.println(
			"*** Time to create all word sets: " + endTime + " seconds");
	}

	/**	Delete all word sets for system account.
	 */

	private static void deleteAllWordSets()
	{
		WordSet[] wordSets	= WordSetUtils.getWordSetsForLoggedInUser();

		if ( wordSets.length == 0 )
		{
			System.out.println( "No existing word sets to delete." );
		}
		else if ( wordSets.length == 1 )
		{
			System.out.println( "One existing word set to delete." );
		}
		else
		{
			System.out.println( wordSets.length + " word sets to delete." );
		}

		if ( wordSets.length > 0 )
		{
			WordSetUtils.deleteWordSets( wordSets );

			if ( wordSets.length == 1 )
			{
				System.out.println(
					"1 word set deleted." );
			}
			else
			{
				System.out.println(
					wordSets.length + " word sets deleted." );
			}
		}
	}

	/**	Main program.
	 *
	 *	@param	args	Command line arguments (unused).
	 */

	public static void main( String args[] )
	{
								//	Remember current time.

		long startTime		= System.currentTimeMillis();

		try
		{
								//	Initialize database access.
			init(args);
								//	Initialize WordHoard settings.

			WordHoardSettings.initializeSettings( true );

								//	Set to use local database access.

			WordHoardSettings.setBuildProgramRunning( true );

								//	Preload work parts and
								//	create work part maps.

			WorkPart[] workParts	= WorkUtils.getWorkParts();

			WorkUtils.createWorkPartMaps( Arrays.asList( workParts ) );

								//	Delete all word sets belonging
								//	to "system".

			deleteAllWordSets();

								//	Just add one word set for testing.
//			makeOneWordSet();

								//	Add the default word sets.

			makeBigWordSets();

								//	Report time needed to create word sets.

			long timeDiff	= System.currentTimeMillis() - startTime;
			timeDiff		= (int)( ( timeDiff + 999 ) / 1000 );

			System.out.println(
				"Word sets created in " + timeDiff + " seconds." );

								//	Close down database access.
			term();
		}
		catch ( Throwable e )
		{
			e.printStackTrace();
		}
                         		//	Quit.
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


