package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;

/**	Lemma utilities.
 */

public class LemmaUtils
{
	/**	See if specified lemma exists.
	 *
	 *	@param	lemmaText	The lemma text.
	 *
	 *	@return		true if lemma with lemma text exists, false otherwise.
	 */

	public static boolean lemmaExists( String lemmaText )
	{
		boolean result	= false;

		java.util.List lemmaCount	=
			PersistenceManager.doQuery
			(
				"select count(*) from Lemma le " +
				"where le.tag.string=:tag" ,
				new String[]{ "tag" } ,
				new Object[]{ lemmaText }
			);

		if ( lemmaCount != null )
		{
			Iterator iterator	= lemmaCount.iterator();
			Long count			= (Long)iterator.next();
			result				= ( count.longValue() > 0 );
		}

		return result;
	}

	/**	Finds lemmas by matching an initial string of characters.
	 *
	 *	@param		initialString 	The initial lemma text string.
	 *
	 *	@return		An array of matching lemma objects
	 *				whose tags begin with the specified text.
	 *				Null if none.
	 */

	public static Lemma[] getLemmataByInitialString
	(
		String initialString
	)
	{
		Lemma[] result	= new Lemma[ 0 ];

		String lowercaseInitialString	= initialString.toLowerCase() + "%";

		java.util.List lemmaList	=
			PersistenceManager.doQuery
			(
				"from Lemma le where le.tag.string like :initialString " +
				"order by le.tag.string" ,
				new String[]{ "initialString" } ,
				new Object[]{ lowercaseInitialString }
			);

		if ( lemmaList != null )
		{
			result	= (Lemma[])lemmaList.toArray( new Lemma[]{} );
		}

		return result;
	}

	/**	Get lemma from its tag.
	 *
	 *	@param	lemmaTag	The lemma tag.
	 *
	 *	@return				The lemma.
	 */

	public static Lemma getLemmaByTag( String lemmaTag )
	{
		Lemma result	= null;

		java.util.List lemmaList	=
			PersistenceManager.doQuery
			(
				"from Lemma le where le.tag.string=:tag" ,
				new String[]{ "tag" } ,
				new Object[]{ lemmaTag }
			);

		if ( lemmaList != null )
		{
			result	= (Lemma)lemmaList.get( 0 );
		}

		return result;
	}

	/**	Get lemmata for all words in a work.
	 *
	 *	@param	work	The work.
	 *
	 *	@return			Array of Lemma.
	 *
	 *	<p>
	 *	The lemmata are returned in the same order as the
	 *	words appear in the work.  In general, there will
	 *	be more lemmata than words, since each word can have
	 *	more than one associated lemma.  For example,
	 *	"can't" has two lemmata, "can" and "not".
	 *	</p>
	 */

	public static Lemma[] getLemmata( Work work )
	{
		Lemma[]	results	= new Lemma[ 0 ];

		if ( work != null )
		{
			String queryString	=
				"select le from Word wo, WordPart wp, LemPos lp, Lemma le " +
				"where wo.work = :work and " +
				"wp.word = wo and " +
				"lp = wp.lemPos and " +
				"le = lp.lemma " +
				"order by wo.workOrdinal, wp.partIndex asc";

			java.util.List lemmata	=
				PersistenceManager.doQuery
				(
					queryString ,
					new String[]{ "work" } ,
					new Object[]{ work }
				);

			if ( lemmata != null )
			{
				results	= (Lemma[])lemmata.toArray( new Lemma[]{} );
			}
		}

		return results;
	}

	/**	Get lemmata corresponding to a batch of words.
	 *
	 *	@param	words	Collection of Word entries.
	 *
	 *	@return			Array of Lemma.
	 *
	 *	<p>
	 *	The lemmata are returned in the same order as the
	 *	entries in the words collection  In general, there will
	 *	be more lemmata than words, since each word can have
	 *	more than one associated lemma.  For example,
	 *	"can't" has two lemmata, "can" and "not".
	 *	</p>
	 */

	public static Lemma[] getLemmata( Collection words )
	{
		Lemma[]	results	= new Lemma[ 0 ];

								//	No words given?  Return
								//	empty result.

		if ( ( words == null ) || ( words.size() == 0 ) ) return results;

								//	Create query to retrieve lemmata.

//		String queryString	=
//			"select wo.id, le from Word wo, WordPart wp, LemPos lp, " +
//			"Lemma le " +
//			"where wo in (:words) and " +
//			"wp.word = wo and " +
//			"lp = wp.lemPos and " +
//			"le = lp.lemma " ;

		StringBuffer queryString	= new StringBuffer();

		queryString.append(
			"select wo.id, le from Word wo, WordPart wp, " +
			"LemPos lp , Lemma le where " );

		queryString.append(
			PersistenceManager.generateInPhrase( "wo" , words ) );

		queryString.append(
			" and wp.word = wo and " +
			"lp = wp.lemPos and " +
			"le = lp.lemma " +
			"order by wo.id, wp.partIndex asc" );

								//	Perform query to get lemmata.

		java.util.List lemmata	=
			PersistenceManager.doQuery
			(
				queryString.toString()
			);

		if ( lemmata != null )
		{
								//	Copy query results to map with
								//	key=word ID and value = lemmata
								//	for the word, in word part order.
								//	This allows us to order the lemmata
								//	to match the order of the words passed
								//	in.

			Iterator iterator		= lemmata.iterator();
			TreeMap idToLemmata		= new TreeMap();
			int lemmaCount			= 0;

			while ( iterator.hasNext() )
			{
				Object[] o	= (Object[])iterator.next();

				ArrayList lemmaList;

				if ( idToLemmata.containsKey( o[ 0 ] ) )
				{
					lemmaList	= (ArrayList)idToLemmata.get( o[ 0 ] );
				}
				else
				{
					lemmaList	= new ArrayList();
				}

				lemmaList.add( o[ 1 ] );

				idToLemmata.put( o[ 0 ] , lemmaList );

				lemmaCount++;
			}
								//	Now create array of Lemma entries
								//	arranged in the same order as the
								//	input words.  This array is the
								//	result passed back to the caller.
								//	Note that the number of lemmata
								//	is usually larger than the number
								//	if words, since each word can
								//	have more than one lemma -- for
								//	instance, for contractions.

			results		= new Lemma[ lemmaCount ];
			int k		= 0;

			Iterator wordIterator	= words.iterator();

			while ( wordIterator.hasNext() )
			{
				ArrayList lemmaList	=
					(ArrayList)idToLemmata.get(
						((Word)wordIterator.next()).getId() );

				for ( int j = 0 ; j < lemmaList.size() ; j++ )
				{
					results[ k++ ]	= (Lemma)lemmaList.get( j );
				}
			}
		}

		return results;
	}

	/**	Get lemmata corresponding to a batch of words.
	 *
	 *	@param	words	Array of Word.
	 *
	 *	@return			Array of Lemma.
	 *
	 *	<p>
	 *	The lemmata are returned in the same order as the
	 *	entries in the words array.  In general, there will
	 *	be more lemmata than words, since each word can have
	 *	more than one associated lemma.  For example,
	 *	"can't" has two lemmata, "can" and "not".
	 *	</p>
	 */

	public static Lemma[] getLemmata( Word[] words )
	{
		Lemma[]	results	= new Lemma[ 0 ];

								//	No words given?  Return
								//	empty result.

		if ( ( words == null ) || ( words.length == 0 ) ) return results;

								//	Generate query string to get
								//	the count of the word parts for
								//	each word.

//		String queryString	=
//			"select wo.id, le from Word wo, WordPart wp, LemPos lp, " +
//			"Lemma le " +
//			"where wo in (:words) and " +
//			"wp.word = wo and " +
//			"lp = wp.lemPos and " +
//			"le = lp.lemma ";


		StringBuffer queryString	= new StringBuffer();

		queryString.append(
			"select wo.id, le from Word wo, WordPart wp, " +
			"LemPos lp , Lemma le where " );

		queryString.append(
			PersistenceManager.generateInPhrase( "wo" , words ) );

		queryString.append(
			" and wp.word = wo and " +
			"lp = wp.lemPos and " +
			"le = lp.lemma " +
			"order by wo.tag, wp.partIndex asc" );

								//	Perform query to get lemmata.

		java.util.List lemmata	=
			PersistenceManager.doQuery
			(
				queryString.toString()
			);

		if ( lemmata != null )
		{
								//	Copy query results to map with
								//	key=word ID and value = lemmata
								//	for the word, in word part order.
								//	This allows us to order the lemmata
								//	to match the order of the words passed
								//	in.

			Iterator iterator		= lemmata.iterator();
			TreeMap idToLemmata		= new TreeMap();
			int lemmaCount			= 0;

			while ( iterator.hasNext() )
			{
				Object[] o	= (Object[])iterator.next();

				ArrayList lemmaList;

				if ( idToLemmata.containsKey( o[ 0 ] ) )
				{
					lemmaList	= (ArrayList)idToLemmata.get( o[ 0 ] );
				}
				else
				{
					lemmaList	= new ArrayList();
				}

				lemmaList.add( o[ 1 ] );

				idToLemmata.put( o[ 0 ] , lemmaList );

				lemmaCount++;
			}
								//	Now create array of Lemma entries
								//	arranged in the same order as the
								//	input words.  This array is the
								//	result passed back to the caller.
								//	Note that the number of lemmata
								//	is usually larger than the number
								//	if words, since each word can
								//	have more than one lemma -- for
								//	instance, for contractions.

			results		= new Lemma[ lemmaCount ];
			int k		= 0;

			for ( int i = 0 ; i < words.length ; i++ )
			{
				ArrayList lemmaList	=
					(ArrayList)idToLemmata.get( words[ i ].getId() );

				for ( int j = 0 ; j < lemmaList.size() ; j++ )
				{
					results[ k++ ]	= (Lemma)lemmaList.get( j );
				}
			}
		}

		return results;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected LemmaUtils()
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


