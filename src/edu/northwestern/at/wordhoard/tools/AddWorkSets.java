package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	Creates the default work sets.
 *
 *	<p>This tool computes and creates all of the initial (default)
 *	{@link edu.northwestern.at.wordhoard.model.userdata.WorkSet work set}
 *	objects and makes them persistent in the MySQL database. It must be run
 *	after all of the other build programs have constructed and populated
 *	the rest of the object model.</p>
 *
 *	<p>Usage:</p>
 *
 *	<p><code>AddWorkSets dbname username password</code></p>
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */

public class AddWorkSets
{
	/**	Persistence manager.
	 */

	private static PersistenceManager pm	= null;

	/**	Tags for tragedies. */

	private static SortedArrayList tragedies	=
		new SortedArrayList
		(
			new String[]
			{
				"sha-tit",
				"sha-roj",
				"sha-juc",
				"sha-ham",
				"sha-oth",
				"sha-kil",
				"sha-mac",
				"sha-ant",
				"sha-cor",
				"sha-tim"
			}
		);

	/**	Tags for histories. */

	private static SortedArrayList histories	=
		new SortedArrayList
		(
			new String[]
			{
				"sha-1h6",
				"sha-2h6",
				"sha-3h6",
				"sha-ri3",
				"sha-kij",
				"sha-ri2",
				"sha-1h4",
				"sha-2h4",
				"sha-he5",
				"sha-he8"
			}
		);

	/**	Tags for comedies. */

	private static SortedArrayList comedies	=
		new SortedArrayList
		(
			new String[]
			{
				"sha-tgv",
				"sha-tam",
				"sha-com",
				"sha-lll",
				"sha-mnd",
				"sha-mev",
				"sha-mww",
				"sha-man",
				"sha-ayl",
				"sha-twn",
				"sha-tro",
				"sha-aww",
				"sha-mem"
			}
		);

	/**	Tags for romances. */

	private static SortedArrayList romances	=
		new SortedArrayList
		(
			new String[]
			{
				"sha-per",
				"sha-cym",
				"sha-win",
				"sha-tem"
			}
		);

	/**	Tags for poems. */

	private static SortedArrayList poems	=
		new SortedArrayList
		(
			new String[]
			{
				"sha-ven",
				"sha-rap",
				"sha-son",
				"sha-pht",
				"sha-lov"
			}
		);

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

	/**	Delete all existing work sets owned by "system".
	 */

	private static void deleteWorkSets()
	{
		try
		{
								//	Get list of work sets belonging
								//	to the system account.

			java.util.List systemWorkSets	=
				pm.query( "from WorkSet where owner='system'" );

			int nWorkSets	= 0;

			if ( systemWorkSets != null )
			{
				nWorkSets	= systemWorkSets.size();
			}
								//	Delete the work sets belonging to
								//	the system account.

			if ( nWorkSets > 0 )
			{
				pm.delete( systemWorkSets );

				if ( nWorkSets == 1 )
				{
					System.out.println(
						"1 existing work set deleted." );
				}
				else
				{
					System.out.println(
						nWorkSets +
						" existing work sets deleted." );
				}
			}
			else
			{
				System.out.println(
					"No existing work sets to delete." );
			}
		}
		catch ( Exception e )
		{
			System.out.println(
				"Unable to delete work sets." );

			System.exit(1);
		}
	}

	/**	Create a Chaucer work set from a work part and its children.
	 *
	 *	@param	workPart	The Chaucer work part.
	 *
	 *	@return				A work set for the Chaucer work part and its
	 *						descendant parts.
	 */

	private static void createChaucerWorkSet( WorkPart workPart )
	{
								//	Generate nothing for the "Title"
								//	section, which is empty.

		if ( workPart.getShortTitle().equals( "Title" ) ) return;

		WorkPart childParts[]	= new WorkPart[ 1 ];
		childParts[ 0 ]			= workPart;

								//	Add the work set.
		WorkSet workSet	=
			addWorkSet
			(
				"ct: " + workPart.getShortTitle() ,
				"" ,
				"" ,
				true ,
				"" ,
				childParts
			);

		if ( workSet == null )
		{
			System.out.println(
				"   Could not create work set for " + workPart );
		}
		else
		{
			System.out.println(
				"   Created work set for " + workPart );
		}
	}

	/**	Add a new work set with specified work parts.
	 *
	 *	@param	title		Title for the new work set.
	 *	@param	description	Description for the new work set.
	 *	@param	webPageURL	Web page URL for the new work set.
	 *	@param	isPublic	True if work set to be public.
	 *	@param	query		CQL query which generates work set.
	 *	@param	workParts	Collection of WorkParts to add to work set.
	 *
	 *	@return				WorkSet object if work set added, else null.
	 */

	private static WorkSet addWorkSet
	(
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		String query ,
		Collection workParts
	)
	{
								//	Create new work set object.
		WorkSet workSet	=
			new WorkSet
			(
				title ,
				description ,
				webPageURL ,
				"system" ,
				isPublic ,
				query ,
				workParts
			);
								//	Persist the work set object.
		try
		{
			pm.save( workSet );

			if ( workSet.getId() == null ) workSet = null;
		}
		catch ( Exception e )
		{
			workSet	= null;
		}
								//	Return new WorkSet object to caller.
		return workSet;
	}

	/**	Add a new work set with specified work parts.
	 *
	 *	@param	title		Title for the new work set.
	 *	@param	description	Description for the new work set.
	 *	@param	webPageURL	Web page URL for the new work set.
	 *	@param	isPublic	True if work set to be public.
	 *	@param	query		CQL query which generates work set.
	 *	@param	workParts	Array of WorkPart entries to add to work set.
	 *
	 *	@return				WorkSet object if work set added, else null.
	 */

	private static WorkSet addWorkSet
	(
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		String query ,
		WorkPart[] workParts
	)
	{
		return addWorkSet
		(
			title ,
			description ,
			webPageURL ,
			isPublic ,
			query ,
			Arrays.asList( workParts )
		);
	}

	/**	Create work sets for Chaucer.
	 *
	 *	<p>
	 *	We create a separate work set for each of the Canterbury Tales.
	 *	</p>
	 */

	private static void createChaucerSets()
	{
		System.out.println( "Chaucer" );

								//	Get main Canterbury Tales work.

		Work canterburyTales	=
			WorkUtils.getWork( "The Canterbury Tales" );
		if (canterburyTales == null) return;

								//	Get work parts.

		WorkPart[] tales		=
			(WorkPart[])canterburyTales.getChildren().toArray(
				new WorkPart[]{} );

								//	Create work set for each tale.

		for ( int i = 0 ; i < tales.length ; i++ )
		{
			createChaucerWorkSet( tales[ i ] );
		}
	}

	/**	Create work sets for Shakespeare, grouping works by category.
	 */

	private static void createShakespeareSets()
	{
		System.out.println( "Shakespeare" );

								//	Create lists to hold works for
								//	new work sets.

		ArrayList tragediesList	= new ArrayList();
		ArrayList comediesList	= new ArrayList();
		ArrayList historiesList	= new ArrayList();
		ArrayList romancesList	= new ArrayList();
		ArrayList poemsList		= new ArrayList();

								//	Get all works in Shakespeare corpus.

		Corpus corpus	= CorpusUtils.getCorpus( "Shakespeare" );
		if (corpus == null) return;

		Work[] works	= (Work[])corpus.getWorks().toArray( new Work[]{} );

		for ( int i = 0 ; i < works.length ; i++ )
		{
			Work work	= works[ i ];

			String tag	= work.getTag();

								//	See which work set, if any,
								//	this work belongs to.

			if ( tragedies.contains( tag ) )
			{
				tragediesList.add( work );
			}
			else if ( comedies.contains( tag ) )
			{
				comediesList.add( work );
			}
			else if ( histories.contains( tag ) )
			{
				historiesList.add( work );
            }
            else if ( romances.contains( tag ) )
            {
            	romancesList.add( work );
            }
            else if ( poems.contains( tag ) )
            {
            	poemsList.add( work );
            }
        }
                                //	Persist the work sets.

		addWorkSet(
			"Shakespeare Tragedies" ,
			"" ,
			"" ,
			true ,
			"" ,
			tragediesList );

		System.out.println(
			"   Created work set for " + tragediesList.size() +
			" Shakespeare tragedies." );

		addWorkSet(
			"Shakespeare Comedies" ,
			"" ,
			"" ,
			true ,
			"" ,
			comediesList );

		System.out.println(
			"   Created work set for " + comediesList.size() +
			" Shakespeare comedies." );

		addWorkSet(
			"Shakespeare Histories" ,
			"" ,
			"" ,
			true ,
			"",
			historiesList );

		System.out.println(
			"   Created work set for " + historiesList.size() +
			" Shakespeare histories." );

		addWorkSet(
			"Shakespeare Romances" ,
			"" ,
			"" ,
			true ,
			"",
			romancesList );

		System.out.println(
			"   Created work set for " + romancesList.size() +
			" Shakespeare romances." );

		addWorkSet(
			"Shakespeare Poems" ,
			"" ,
			"" ,
			true ,
			"",
			poemsList );

		System.out.println(
			"   Created work set for " + poemsList.size() +
			" Shakespeare poems." );
	}

	/**	Create a Spenser work set from a work part and its children.
	 *
	 *	@param	workPart	The Spenser work part.
	 *
	 *	@param	label		Label for use in constructing work set title.
	 *
	 *	@return				A work set for the Spenser work part and
	 *						its descendant parts.
	 */

	private static void createSpenserWorkSet( WorkPart workPart, String label )
	{
								//	Only create work sets for the books.

		if ( workPart.getShortTitle().equals( "Title" ) ) return;
		if ( workPart.getShortTitle().equals( "Dedication" ) ) return;

		WorkPart childParts[]	= new WorkPart[ 1 ];
		childParts[ 0 ]			= workPart;

								//	Add the work set.
		WorkSet workSet	=
			addWorkSet
			(
				"Spenser: " + label + workPart.getShortTitle() ,
				"" ,
				"" ,
				true ,
				"" ,
				childParts
			);

		if ( workSet == null )
		{
			System.out.println(
				"   Could not create work set for " + workPart );
		}
		else
		{
			System.out.println( "   Created work set for " + workPart );
		}
	}

	/**	Create work sets for Spenser.
	 *
	 *	<p>
	 *	We create a separate work set for each book of the Faerie Queene, a
	 *	a work set for everything but the Faerie Queene, and a work set for
	 *	everything but the Faerie Queene and the Shepheards Calender.
	 *	</p>
	 */

	private static void createSpenserSets()
	{
		System.out.println( "Spenser" );

								//	Get main Faerie Queene work.

		Work faerieQueene	=
			WorkUtils.getWork( "The Faerie Queene" );
		if (faerieQueene == null) return;

								//	Get work parts.

		WorkPart[] books	=
			(WorkPart[])faerieQueene.getChildren().toArray(
				new WorkPart[]{} );

								//	Create work set for each book.

		for ( int i = 0 ; i < books.length ; i++ )
		{
			createSpenserWorkSet( books[ i ], "FQ " );
		}

		// 
		ArrayList shorterPoems			= new ArrayList();
		ArrayList shorterPoemsExcludingSC	= new ArrayList();
		
		Corpus corpus	= CorpusUtils.getCorpus( "Spenser" );
		if (corpus == null) return;

		Work[] works	= (Work[])corpus.getWorks().toArray( new Work[]{} );

		for ( int i = 0 ; i < works.length ; i++ )
		{
			Work work	= works[ i ];

			String tag	= work.getTag();

			if (! tag.equals("spe-faq" ) )
			{
				shorterPoems.add( work );
			}
			if (! tag.equals("spe-faq" ) && ! tag.equals("spe-sc") )
			{
				shorterPoemsExcludingSC.add( work );
			}
		}
		addWorkSet(
			"Spenser: Shorter Poems" ,
			"" ,
			"" ,
			true ,
			"",
			shorterPoems );

		System.out.println(
			"   Created work set for " + shorterPoems.size() +
			" Spenser: Shorter Poems." );

		addWorkSet(
			"Spenser: Shorter Poems (excluding SC)" ,
			"" ,
			"" ,
			true ,
			"",
			shorterPoemsExcludingSC );

		System.out.println(
			"   Created work set for " + shorterPoemsExcludingSC.size() +
			" Spenser: Shorter Poems (excluding SC)." );


	}

	/**	Create default work sets.
	 */

	private static void createWorkSets()
	{
								//	Remember current time so we can compute
								//	work set creation time.

		long startTime		= System.currentTimeMillis();

								//	Remove existing work sets.

		deleteWorkSets();
								//	Create work sets for Chaucer

		createChaucerSets();

								//	Create work sets for Shakespeare.

		createShakespeareSets();

								//	Create work sets for Spenser.

		createSpenserSets();
		
								//	Report time needed to create work sets.

		long timeDiff	= System.currentTimeMillis() - startTime;
		timeDiff		= (int)( ( timeDiff + 999 ) / 1000 );
	}

	/**	Main program.
	 *
	 *	@param	args	Command line arguments (ignored).
	 */

	public static void main( String args[] )
	{
		try
		{
			init(args);
			createWorkSets();
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

