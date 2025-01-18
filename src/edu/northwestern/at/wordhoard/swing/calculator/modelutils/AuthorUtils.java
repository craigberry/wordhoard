package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;

/**	Author utilities.
 */

public class AuthorUtils
{
	/**	Get contemporaries of an author.
	 *
	 *	@param	author			The author for whom to find contemporaries.
	 *	@param	yearsBefore		Number of years before the author was born
	 *							to consider as a lower bound on the
	 *							birth date for a contemporary.
	 *	@param	yearsAfter		Number of years after the author died
	 *							to consider as an upper bound on the
	 *							death date for a contemporary.
	 *
	 *	@return					Array of author entries for contemporaries
	 *							of the specified author.  May be null if
	 *							author has no contemporaries.
	 *
	 *	<p>
	 *	A contemporary is defined as an author who was born up to
	 *	author's birth date - yearsBefore years before the subject author,
	 *	and died up to author's death date + yearsAfter after the
	 *	subject author.
	 *	</p>
	 */

	public static Author[] getContemporaries
	(
		Author author ,
		int yearsBefore ,
		int yearsAfter
	)
	{
								//	Quit immediately if author null.

		if ( author == null ) return null;

								//	Set up query to return all authors
								//	within a specified year range.
								//	Retrieve any matching authors.
								//	These are the contemporaries plus
								//	the current author.

		java.util.List authors	=
			PersistenceManager.doQuery
			(
				"from Author au where " +
				" !isNull(au.birthYear) and !isNull(au.deathYear) " +
				" and au.birthYear >= :firstYear and " +
				" au.deathYear <= :lastYear" ,
				new String[]
				{
					"firstYear" ,
					"lastYear"
				} ,
				new Object[]
				{
					Integer.valueOf(
						author.getBirthYear().intValue() - yearsBefore ) ,
					Integer.valueOf(
						author.getDeathYear().intValue() + yearsAfter )
				}
			);
								//	Remove the current author from the
								//	list of authors.

		ArrayList contemp	= new ArrayList();

		if ( authors != null )
		{
			for	(	Iterator iterator = authors.iterator() ;
					iterator.hasNext() ; )
			{
				Author a	= (Author)iterator.next();
				if ( a != author ) contemp.add( a );
			}
		}
								//	Convert authors collection to an array.

		return (Author[])contemp.toArray( new Author[]{} );
	}

	/**	Get contemporaries of an author.
	 *
	 *	@param	authorName		Name of author for whom to find contemporaries.
	 *	@param	yearsBefore		Number of years before the author was born
	 *							to consider as a lower bound on the
	 *							birth date for a contemporary.
	 *	@param	yearsAfter		Number of years after the author died
	 *							to consider as an upper bound on the
	 *							death date for a contemporary.
	 *
	 *	@return					Array of author entries for contemporaries
	 *							of the specified author.  May be null if
	 *							author has no contemporaries.
	 *
	 *	<p>
	 *	A contemporary is defined as an author who was born up to
	 *	author's birth date - yearsBefore years before the subject author,
	 *	and died up to author's death date + yearsAfter after the
	 *	subject author.
	 *	</p>
	 */

	public static Author[] getContemporaries
	(
		String authorName ,
		int yearsBefore ,
		int yearsAfter
	)
	{
		return
			getContemporaries(
				getAuthor( authorName ) , yearsBefore , yearsAfter );
	}

	/**	Get an author by name.
	 *
	 *	@param	authorName	The author's name.
	 *
	 *	@return				An Author object for a matching author.
	 *						If there are two or more authors with the
	 *						same name, only the first is returned.
	 *						Null is returned if no matching author
	 *						is found.
	 */

	public static Author getAuthor( String authorName )
	{
		Author result		= null;

		java.util.List list	=
			PersistenceManager.doQuery
			(
				"from Author au where au.name = :authorName" ,
				new String[]{ "authorName" } ,
				new Object[]{ authorName }
			);

		return
			( ( list != null ) && ( !list.isEmpty() ) ) ?
				(Author)list.get( 0 ) : null;
	}

	/**	Get array of all available authors.
	 *
	 *	@return		Array of Author for all available authors.
	 */

	public static Author[] getAuthors()
	{
		SortedArrayList authorList	= new SortedArrayList();

								//	Ask for all Author entries.

		java.util.List authors	=
			PersistenceManager.doQuery( "from Author" , true );

								//	Store authors in an array list.

		for ( Iterator it = authors.iterator() ; it.hasNext(); )
		{
			authorList.add( it.next() );
		}
								//	Return array of authors.

		return (Author[])authorList.toArray( new Author[]{} );
	}

	/**	Get earliest and latest works for an author.
	 *
	 *	@param	author		The author.
	 *
	 *	@return				A two-element {@link Work} array as follows.
	 *						result[0]	= earliest work by author.
	 *						result[1]	= latest work by author.
	 *
	 *	<p>
	 *	If an author exists but has no works, the result is null.
	 *	</p>
	 */

	public static Work[] getEarliestAndLatestWork( Author author )
	{
		Work[] works	= (Work[])author.getWorks().toArray( new Work[]{} );
		Work[] result	= null;

		if ( works.length > 0 )
		{
			result	= new Work[ 2 ];

			Arrays.sort
			(
				works ,
				new Comparator()
				{
					public int compare( Object o1 , Object o2 )
					{
						return
							((Work)o1).getPubDate().getStartYear().intValue() -
							((Work)o2).getPubDate().getStartYear().intValue();
					}
				}
			);

			result[ 0 ]	= works[ 0 ];
			result[ 1 ]	= works[ works.length - 1 ];
		}

		return result;
	}

	/**	Get earliest and latest work dates for an author.
	 *
	 *	@param	author		The author.
	 *
	 *	@return				A two-element integer array as follows.
	 *						result[0]	= year of earliest work by author.
	 *						result[1]	= year of latest work by author.
	 *
	 *	<p>
	 *	If an author exists but has no works, both years are returned as
	 *	Integer.MIN_VALUE .
	 *	</p>
	 */

	public static int[] getEarliestAndLatestWorkDates( Author author )
	{
		Work[] works	= getEarliestAndLatestWork( author );

		int[] result	= new int[ 2 ];

		if ( works != null )
		{
			result[ 0 ]	= works[ 0 ].getPubDate().getStartYear().intValue();
			result[ 1 ]	= works[ 1 ].getPubDate().getStartYear().intValue();
		}
		else
		{
			result[ 0 ]	= Integer.MIN_VALUE;
			result[ 1 ]	= Integer.MIN_VALUE;
		}

		return result;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected AuthorUtils()
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

