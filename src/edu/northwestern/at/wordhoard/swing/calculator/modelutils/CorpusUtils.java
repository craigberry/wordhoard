package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Corpus utilities.
 */

public class CorpusUtils
{
	/**	Get a corpus by title.
	 *
	 *	@param	title	The title of the corpus to fetch.
	 *
	 *	@return			The corpus with the requested title,
	 *					or null if not found.
	 */

	public static Corpus getCorpus( String title )
	{
		java.util.List list	=
			PersistenceManager.doQuery
			(
				"from Corpus co where co.title = :title" ,
				new String[]{ "title" } ,
				new Object[]{ title }
			);

		return
			( ( list != null ) && ( !list.isEmpty() ) ) ?
				(Corpus)list.get( 0 ) : null;
	}

	/**	Get a corpus by tag.
	 *
	 *	@param	tag		The tag of the corpus to fetch.
	 *
	 *	@return			The corpus with the requested tag,
	 *					or null if not found.
	 */

	public static Corpus getCorpusByTag( String tag )
	{
		java.util.List list	=
			PersistenceManager.doQuery
			(
				"from Corpus co where co.tag = :tag" ,
				new String[]{ "tag" } ,
				new Object[]{ tag }
			);

		return
			( ( list != null ) && ( !list.isEmpty() ) ) ?
				(Corpus)list.get( 0 ) : null;
	}

	/**	Get all available corpora as an array.
	 *
	 *	@return		All available corpora as an array of Corpus.
	 */

	public static Corpus[] getCorpora()
	{
		java.util.List corpusList = new ArrayList();
		try {
			corpusList = Arrays.asList(CachedCollections.getCorpora());
		} catch (Exception e) {
		}
		
		//java.util.List corpusList	=
		//	PersistenceManager.doQuery
		//	(
		//		"from Corpus" ,
		//		true
		//	);

		SortedArrayList sortedCorpusList =
			new SortedArrayList( corpusList );

		return (Corpus[])sortedCorpusList.toArray( new Corpus[]{} );
	}

	/**	Get all available works in a corpus as an array.
	 *
	 *	@param	corpus	The corpus.
	 *
	 *	@return			All available works in the corpus as an array of Work.
	 *
	 *	<p>
	 *	Returns null if corpus is null.
	 *	</p>
	 */

	public static Work[] getWorks( Corpus corpus )
	{
		Work[] result	= null;

		if ( corpus == null ) return result;

		Collection workList	= new ArrayList();

		PersistenceManager pm	= null;

		try
		{
			pm	= PMUtils.getPM();

			if ( !pm.contains( corpus ) )
			{
				corpus	=
					(Corpus)pm.load( Corpus.class , corpus.getId() );
			}

			workList	= corpus.getWorks();

		}
		catch ( PersistenceException e )
		{
			Err.err( e );
		}
		finally
		{
//			PMUtils.closePM( pm );
		}
								//	If we got any works, sort them and
								//	return the sorted list as an array
								//	of works.

		if ( workList != null )
		{
			SortedArrayList	sortedWorkList	=
				new SortedArrayList( workList );

			result	= (Work[])sortedWorkList.toArray( new Work[]{} );
		}

		return result;
	}

	/**	Get all available work parts in a corpus as an array.
	 *
	 *	@param	corpus	The corpus.
	 *
	 *	@return			All available work parts in the corpus as
	 *					an array of WorkPart.
	 *
	 *	<p>
	 *	Returns null if corpus is null.
	 *	</p>
	 */

	public static WorkPart[] getWorkParts( Corpus corpus )
	{
		WorkPart[] result	= null;

        						//	If corpus is null, return null.

		if ( corpus == null ) return result;

								//	Get works in corpus.

		Work[] works	= getWorks( corpus );

								//	Get work parts.

		java.util.List workPartsList	=
			PersistenceManager.doQuery
			(
				"from WorkPart wp where wp.work in (:works) and " +
				"wp.work is not null"  ,
				new String[]{ "works" } ,
				new Object[]{ Arrays.asList( works ) }
			);
								//	If we got any work parts, return them
								//	as an array of work parts.

		if ( workPartsList != null )
		{
//			SortedArrayList	sortedWorkList	=
//				new SortedArrayList( workList );

//			result	= (WorkPart[])sortedWorkList.toArray( new Work[]Part{} );
			result	= (WorkPart[])workPartsList.toArray( new WorkPart[]{} );
		}

		return result;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected CorpusUtils()
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

