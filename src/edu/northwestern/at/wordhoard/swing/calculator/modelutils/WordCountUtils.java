package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Word count utilities.
 */

public class WordCountUtils
{
	/**	Get word count for multiple words in a set of work parts.
	 *
	 *	@param	workParts		The work parts.
	 *	@param	words			The words.
	 *	@param	wordForm		The word form.
	 *
	 *	@return					Map with words as keys and counts of each word
	 *							in the work parts as values.
	 */

	public static Map getWordFormCount
	(
		WorkPart[] workParts ,
		Spelling[] words ,
		int wordForm
	)
	{
		TreeMap result	= new TreeMap();

		if ( ( workParts.length == 0 ) || ( words.length == 0 ) )
		{
			return result;
		}
								//	Extract the word strings from the
								//	spellings.
								//
								//	$$$PIB$$$ This is a kludge to get around
								//	the many problems Hibernate has
								//	with components in queries.  It would be
								//	nice to use "wc.word" directly, but
								//	this doesn't work, so we use just the
								//	string portion, which is good enough
								//	for our purposes.

		ArrayList wordStrings	= new ArrayList();

		for ( int i = 0 ; i < words.length ; i++ )
		{
			wordStrings.add(
				CharsetUtils.translateToInsensitive(
					words[ i ].getString() ) );
		}
								//	Construct query.

		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
				"(wc.word.string, wc.word.charset, sum(wc.wordCount)) " +
				"from WordCount wc where " +
				"wc.word.string in (:words) and " +
				"wc.wordForm = :wordForm and " +
				"wc.workPart in (:workParts) " +
				"group by wc.word.string";

		String[] paramNames		=
			new String[]
			{
				"words" ,
				"wordForm" ,
				"workParts"
			};

		Object[] paramValues	=
			new Object[]
			{
				wordStrings ,
				new Integer( wordForm ) ,
				Arrays.asList( workParts )
			};

		java.util.List wordCounts	=
			PersistenceManager.doQuery
			(
				queryString , paramNames , paramValues , true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( wordCounts != null )
		{
			Iterator iterator	= wordCounts.iterator();

			while ( iterator.hasNext() )
			{
				CountResult countResult	= (CountResult)iterator.next();

				if ( countResult != null )
				{
					CountMapUtils.updateWordCountMap
					(
						countResult.word.toInsensitive() ,
						countResult.count ,
						result
					);
				}
			}
		}

		return result;
	}

	/**	Get total word form count in a set of work parts.
	 *
	 *	@param	workParts	The work parts.
	 *	@param	wordForm	The word form.
	 *
	 *	@return				Count of the word form in the work parts.
	 */

	public static int getWordFormCount
	(
		WorkPart[] workParts ,
		int wordForm
	)
	{
		int result	= 0;

		if ( ( workParts == null ) || ( workParts.length == 0 ) )
		{
			return result;
		}

		java.util.List workWordFormCounts	=
			PersistenceManager.doQuery
			(
				"select sum(twfc.wordFormCount) " +
					"from TotalWordFormCount twfc where " +
					"twfc.workPart in (:workParts) and " +
					"twfc.wordForm = :wordForm" ,
				new String[]
				{
					"workParts" ,
					"wordForm"
				} ,
				new Object[]
				{
					Arrays.asList( WorkUtils.getUniqueWorkParts( workParts ) ) ,
					new Integer( wordForm )
				} ,
				true
			);
								//	Pick up count.

		if ( workWordFormCounts != null )
		{
			Iterator iterator	= workWordFormCounts.iterator();

			if ( iterator.hasNext() )
			{
				Object obj	= iterator.next();

				if ( obj != null )
				{
					result	= (int)((Long)obj).longValue();
				}
			}
		}

		return result;
	}

	/**	Get distinct word form count in a set of work parts.
	 *
	 *	@param	workParts	The work parts.
	 *	@param	wordForm	The word form.
	 *
	 *	@return				Count of the distinct word forms in the
	 *						work parts.
	 */

	public static int getDistinctWordFormCount
	(
		WorkPart[] workParts ,
		int wordForm
	)
	{
		int result	= 0;

		if ( ( workParts == null ) || ( workParts.length == 0 ) )
		{
			return result;
		}

		java.util.List workWordFormCounts	=
			PersistenceManager.doQuery
			(
				"select count(distinct wc.word.string) " +
					"from WordCount wc where " +
					"wc.workPart in (:workParts) and " +
					"wc.wordForm = :wordForm" ,
				new String[]
				{
					"workParts" ,
					"wordForm"
				} ,
				new Object[]
				{
					Arrays.asList(
						WorkUtils.getUniqueWorkParts( workParts ) ) ,
					new Integer( wordForm )
				} ,
				true
			);
								//	Pick up count.

		if ( workWordFormCounts != null )
		{
			Iterator iterator	= workWordFormCounts.iterator();

			if ( iterator.hasNext() )
			{
				Object obj	= iterator.next();

				if ( obj != null )
				{
					result	= (int)((Long)obj).longValue();
				}
			}
		}

		return result;
	}

	/**	Get total word form count in a work part.
	 *
	 *	@param	workPart	The work part.
	 *	@param	wordForm	The word form.
	 *
	 *	@return				Count of the word form in the work part.
	 */

	public static int getWordFormCount
	(
		WorkPart workPart ,
		int wordForm
	)
	{
		return getWordFormCount( new WorkPart[]{ workPart } , wordForm );
	}

	/**	Get word count in a set of work parts.
	 *
	 *	@param	workParts		The work parts.
	 *	@param	word			The word.
	 *	@param	wordForm		The word form.
	 *
	 *	@return					Count of the word form in the work parts.
	 */

	public static int getWordFormCount
	(
		WorkPart[] workParts ,
		Spelling word ,
		int wordForm
	)
	{
		int result	= 0;

		Spelling insensitiveWord	= word.toInsensitive();

		Map map	= getWordFormCount
		(
			workParts ,
			new Spelling[]{ insensitiveWord } ,
			wordForm
		);

		if ( map.containsKey( insensitiveWord ) )
		{
			result	= ((Integer)map.get( insensitiveWord )).intValue();
		}

		return result;
	}

	/**	Get word count in a work part.
	 *
	 *	@param	workPart	The work part.
	 *	@param	word		The word.
	 *	@param	wordForm	The word form.
	 *
	 *	@return				Count of the word form in the work part.
	 */

	public static int getWordFormCount
	(
		WorkPart workPart ,
		Spelling word ,
		int wordForm
	)
	{
		return getWordFormCount
		(
			new WorkPart[]{ workPart } ,
			word ,
			wordForm
		);
	}

	/**	Get word counts in a single work part.
	 *
	 *	@param	workPart	The work part.
	 *	@param	wordForm	The word form to count.
	 *
	 *	@return				Map containing each word in the work part
	 *						as a key and the count of the word as the value.
	 */

	public static Map getWordCounts( WorkPart workPart , int wordForm )
	{
		TreeMap wordCounts	= new TreeMap();

		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
			"(wc.word , wc.wordCount) " +
			"from WordCount wc where " +
			"wc.workPart = :workPart and " +
			"wc.wordForm = :wordForm";

		java.util.List workPartWordCounts	=
			PersistenceManager.doQuery
			(
				queryString ,
				new String[]{ "workPart" , "wordForm" } ,
				new Object[]{ workPart , new Integer( wordForm ) } ,
				true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( workPartWordCounts != null )
		{
			Iterator iterator	= workPartWordCounts.iterator();

			while ( iterator.hasNext() )
			{
				CountResult wordCount	= (CountResult)iterator.next();

				wordCounts.put
				(
					wordCount.word.toInsensitive() ,
					new Integer( wordCount.count )
				);
			}
		}

		return wordCounts;
	}

	/**	Get word form counts in a set of work parts.
	 *
	 *	@param	workParts		The work parts.
	 *	@param	wordForm		The word form to count.
	 *	@param	getWorkCounts	if true, work counts are returned in the second
	 *							result map (see below).  If false, hashsets of
	 *							work IDs are returned in the second result map.
	 *
	 *	@return					Array of two maps.  The first map contains
	 *							each word of the specified word form
	 *							in the work parts as a key and
	 *							the count of the appearance of the word
	 *							in the work parts as a value.  The second map
	 *							also has the word as the key.  If "getWorkCounts"
	 *							is true, the values for each word are the counts
	 *							of the works in which the word appears.  If
	 *							"getWorkCounts" is false, the value is a hash set
	 *							of the word IDs for each work in which the word
	 *							appears.
	 */

	public static Map[] getWordCounts
	(
		WorkPart[] workParts ,
		int wordForm ,
		boolean getWorkCounts
	)
	{
		TreeMap wordCounts	= new TreeMap();
		TreeMap workCounts	= new TreeMap();

		if ( workParts.length == 0 )
		{
			return new TreeMap[]{ wordCounts , workCounts };
		}

		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
			"(wc.word , wc.wordCount , wc.work.id) " +
			"from WordCount wc where " +
			"wc.wordForm = :wordForm and " +
			"wc.workPart in (:workParts)";

		String[] paramNames		=
			new String[]
			{
				"wordForm" ,
				"workParts"
			};

		Object[] paramValues	=
			new Object[]
			{
				new Integer( wordForm ) ,
				Arrays.asList( workParts )
			};

		java.util.List workPartWordCounts	=
			PersistenceManager.doQuery
			(
				queryString , paramNames , paramValues , true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( workPartWordCounts != null )
		{
			Iterator iterator	= workPartWordCounts.iterator();

			while ( iterator.hasNext() )
			{
				CountResult wordCount	= (CountResult)iterator.next();

				Spelling wordText	= wordCount.word.toInsensitive();
				int newCount		= wordCount.count;
				Long workID			= wordCount.work;

								//	Increment word count.

				if ( wordCounts.containsKey( wordText ) )
				{
					Integer count	= (Integer)wordCounts.get( wordText );

					wordCounts.put
					(
						wordText ,
						new Integer( newCount + count.intValue() )
					);
				}
				else
				{
					wordCounts.put( wordText , new Integer( newCount ) );
				}
								//	Add work to hash set of works for
								//	this word.

				if ( workCounts.containsKey( wordText ) )
				{
					HashSet workHashSet	=
						(HashSet)workCounts.get( wordText );

					workHashSet.add( workID );
				}
				else
				{
					HashSet workHashSet	= new HashSet();
					workHashSet.add( workID );

					workCounts.put( wordText , workHashSet );
				}
			}
								//	Convert work hash sets to counts.

			if ( getWorkCounts )
			{
				iterator	= workCounts.keySet().iterator();

				while ( iterator.hasNext() )
				{
					Object wordText		= iterator.next();

					HashSet workHashSet	= (HashSet)workCounts.get( wordText );

					workCounts.put( wordText , new Integer( workHashSet.size() ) );
				}
			}
		}

		return new TreeMap[]{ wordCounts , workCounts };
	}

	/**	Get word form counts in a set of work parts.
	 *
	 *	@param	workParts		The work parts.
	 *	@param	wordForm		The word form to count.
	 *
	 *	@return					Array of two maps.  The first map contains
	 *							each word of then specified word form
	 *							in the work parts as a key and
	 *							the count of the appearance of the word
	 *							in the work parts as a value.  The second map
	 *							also has the word as the key but
	 *							provides the number of parent works for the
	 *							work parts in which the word appears as a value.
	 */

	public static Map[] getWordCounts( WorkPart[] workParts , int wordForm )
	{
		return getWordCounts( workParts , wordForm , true );
	}

	/**	Get word form counts in two arrays of work parts.
	 *
	 *	@param	workParts1		The first array of work parts.
	 *	@param	workParts2		The second array of work parts.
	 *	@param	wordForm		The word form to count.
	 *
	 *	@return					Array of three maps.
	 *							<p>
	 *							The first map contains each word of the
	 *							specified word form in the first set of
	 *							work parts as a key and the count of the
	 *							appearance of the word in the first set
	 *							of work parts as a value.
	 *							</p>
	 *							<p>
	 *							The second map contains each word of the
	 *							specified word form in the second set of
	 *							work parts as a key and the count of the
	 *							appearance of the word in the second set
	 *							of work parts as a value.
	 *							</p>
	 *							<p>
	 *							The third map also has the word as the key but
	 *							provides the number of works (NOT work parts)
	 *							in which the word appears as a value in either
	 *							of the two sets of work parts.
	 *							</p>
	 */

	public static Map[] getWordCounts
	(
		WorkPart[] workParts1 ,
		WorkPart[] workParts2 ,
		int wordForm
	)
	{
								//	Create maps of the work parts
								//	with the work part ID as the
								//	key and the work part as the value.

		Map partsList1	= new HashMap( workParts1.length );

		for ( int i = 0 ; i < workParts1.length ; i++ )
		{
			partsList1.put( workParts1[ i ].getId() , workParts1[ i ] );
		}

		Map partsList2	= new HashMap( workParts2.length );

		for ( int i = 0 ; i < workParts2.length ; i++ )
		{
			partsList2.put( workParts2[ i ].getId() , workParts2[ i ] );
		}
								//	Get unique set of work parts
								//	from the two list of work parts.

//		WorkPart[] workParts		=
//			WorkUtils.getUniqueWorkParts( workParts1 , workParts2 );
//		WorkPart[] workParts		=
//			WorkUtils.getUniqueWorkPartsBad( workParts1 , workParts2 );

		WorkPart[] trimmedWorkParts1	=
			WorkUtils.getUniqueWorkParts( workParts1 );

		WorkPart[] trimmedWorkParts2	=
			WorkUtils.getUniqueWorkParts( workParts2 );

		int nWorkParts	= trimmedWorkParts1.length + trimmedWorkParts2.length;
		int l			= trimmedWorkParts1.length;

		WorkPart[] workParts	= new WorkPart[ nWorkParts ];

		for ( int i = 0 ; i < trimmedWorkParts1.length ; i++ )
		{
			workParts[ i ]	= trimmedWorkParts1[ i ];
		}

		for ( int i = 0 ; i < trimmedWorkParts2.length ; i++ )
		{
			workParts[ l + i ]	= trimmedWorkParts2[ i ];
		}
								//	Start of query string.
		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
			"(wc.word , wc.wordCount , wc.workPart.id , wc.work.id) " +
			"from WordCount wc where " +
			"wc.wordForm = :wordForm and " +
			"wc.workPart in (:workParts)";

								//	Create the portion of the query string
								//	which selects the work parts whose
								//	counts should be retrieved.

		String[] paramNames	=
			new String[]
			{
				"wordForm" ,
				"workParts"
			};

		Object[] paramValues	=
			new Object[]
			{
				new Integer( wordForm ) ,
				Arrays.asList( workParts )
			};

		HashMap wordCounts1	= new HashMap( 20000 );
		HashMap wordCounts2	= new HashMap( 20000 );
		HashMap workCounts	= new HashMap( 20000 );

								//	Get the word counts for the
								//	selected work parts.

		java.util.List workWordCounts	=
			PersistenceManager.doQuery
			(
				queryString , paramNames , paramValues , false
			);
								//	If we got results, put them in the
								//	result tree map.

		if ( workWordCounts != null )
		{
			Iterator iterator	= workWordCounts.iterator();

								//	For each retrieved word count ...

			while ( iterator.hasNext() )
			{
				CountResult wordCount	= (CountResult)iterator.next();

				Spelling wordText		= wordCount.word.toInsensitive();
				int newCount			= wordCount.count;
				Long workID				= wordCount.work;
				Long workPartID			= wordCount.workPart;

								//	Increment word count if this work part
								//	is in the first work part set.

				if ( partsList1.containsKey( workPartID ) )
				{
					if ( wordCounts1.containsKey( wordText ) )
					{
						Integer count	= (Integer)wordCounts1.get( wordText );

						wordCounts1.put
						(
							wordText ,
							new Integer( newCount + count.intValue() )
						);
					}
					else
					{
						wordCounts1.put( wordText , new Integer( newCount ) );
					}
				}
								//	Increment word count if this work
								//	is in the second work set.

				if ( partsList2.containsKey( workPartID ) )
				{
					if ( wordCounts2.containsKey( wordText ) )
					{
						Integer count	= (Integer)wordCounts2.get( wordText );

						wordCounts2.put
						(
							wordText ,
							new Integer( newCount + count.intValue() )
						);
					}
					else
					{
						wordCounts2.put( wordText , new Integer( newCount ) );
					}
				}
								//	Add work to hash set of works for
								//	this word.

				if ( workCounts.containsKey( wordText ) )
				{
					HashSet workHashSet	=
						(HashSet)workCounts.get( wordText );

					workHashSet.add( workID );
				}
				else
				{
					HashSet workHashSet	= new HashSet();
					workHashSet.add( workID );

					workCounts.put( wordText , workHashSet );
				}
			}
								//	Convert work hash sets to counts.

			iterator	= workCounts.keySet().iterator();

			while ( iterator.hasNext() )
			{
				Object wordText		= iterator.next();

				HashSet workHashSet	= (HashSet)workCounts.get( wordText );

				workCounts.put( wordText , new Integer( workHashSet.size() ) );
			}
		}

		return new Map[]{ wordCounts1 , wordCounts2 , workCounts };
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected WordCountUtils()
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

