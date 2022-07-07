package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.wordhoard.model.*;

/**	Preload utilities.
 *
 *	<p>
 *	The methods here preload object model entities using
 *	(hopefully) efficient queries all at once.  This allows
 *	traversing the object model graph without requiring multiple
 *	database reads during traversal.
 *	</p>
 */

public class PreloadUtils
{
	/**	Preload word part information for a set of words.
	 *
	 *	@param	pm		Persistence manager.
	 *	@param	words	The words.
	 *
	 *	@return			Collection of preloaded objects.
	 */

	public static Collection preloadWordParts
	(
		PersistenceManager pm ,
		Collection words
	)
	{
		if ( ( words == null ) || ( words.size() == 0 ) )
		{
			return new ArrayList();
		}
		else
		{
			Collection results	= null;

            try
            {
				results	=
					pm.query
					(
						"select wo, wp, lp, po, le, wk, wkp, li from " +
						"Word wo, WordPart wp, LemPos lp, Pos po, Lemma le, " +
						"Work wk, WorkPart wkp, Line li " +
						"left join fetch wo.wordParts " +
						"where " +
							PersistenceManager.generateInPhrase( "wo" , words ) +
							" and " +
						"wp.word=wo and " +
						"lp=wp.lemPos and " +
						"po=lp.pos and " +
						"le=lp.lemma and " +
						"wk=wo.work and " +
						"wkp=wo.workPart and " +
						"li=wo.line"
					);
			}
			catch ( Exception e )
			{
//				e.printStackTrace();      7
			}

			return results;
		}
	}

	/**	Preload word part information for a set of words.
	 *
	 *	@param	words	The words.
	 *
	 *	@return			Collection of preloaded objects.
	 */

	public static Collection preloadWordParts( Collection words )
	{
		return preloadWordParts( PMUtils.getPM() , words );
	}

	/**	Preload reduced word part information for a set of words.
	 *
	 *	@param	words	The words.
	 *	@return	Collection of word part information.
	 */

	public static Collection preloadReducedWordParts( Collection words )
	{
		if ( ( words == null ) || ( words.size() == 0 ) )
		{
			return new ArrayList();
		}
		else
		{
/*
			return PersistenceManager.doQuery
			(
				"select wo, wp, lp, le from " +
				"Word wo, WordPart wp, LemPos lp, Lemma le " +
				"left join fetch wo.wordParts " +
				"where wo in (:words) and " +
				"wp.word=wo and " +
				"lp=wp.lemPos and " +
				"le=lp.lemma" ,
				new String[]{ "words" } ,
				new Object[]{ words }
			);
*/
			return PersistenceManager.doQuery
			(
				"select wo, wp, lp, le from " +
				"Word wo, WordPart wp, LemPos lp, Lemma le " +
				"left join fetch wo.wordParts " +
				"where " +
					PersistenceManager.generateInPhrase( "wo" , words ) +
					" and " +
				"wp.word=wo and " +
				"lp=wp.lemPos and " +
				"le=lp.lemma"
			);
		}
	}

	/**	Preload reduced word part information and speeches for a set of words.
	 *
	 *	@param	words	The words.
	 *	@return	Collection of word part and speech information.
	 */

	public static Collection preloadReducedWordPartsAndSpeeches
	(
		Collection words
	)
	{
		if ( ( words == null ) || ( words.size() == 0 ) )
		{
			return new ArrayList();
		}
		else
		{
/*
			return PersistenceManager.doQuery
			(
				"select wo, wp, lp, le, sp from " +
				"Word wo, WordPart wp, LemPos lp, Lemma le, Speech sp " +
				"left join fetch wo.wordParts " +
				"where wo in (:words) and " +
				"wp.word=wo and " +
				"lp=wp.lemPos and " +
				"le=lp.lemma and " +
				"sp=wo.speech",
				new String[]{ "words" } ,
				new Object[]{ words }
			);
*/

			return PersistenceManager.doQuery
			(
				"select wo, wp, lp, le, sp from " +
				"Word wo, WordPart wp, LemPos lp, Lemma le, Speech sp " +
				"left join fetch wo.wordParts " +
				"where " +
					PersistenceManager.generateInPhrase( "wo" , words ) +
					" and " +
				"wp.word=wo and " +
				"lp=wp.lemPos and " +
				"le=lp.lemma and " +
				"sp=wo.speech"
			);

/*
			return PersistenceManager.doQuery
			(
				"select wo, wp, lp, le, sp from " +
				"Word wo, WordPart wp, LemPos lp, Lemma le, Speech sp " +
				"left join fetch wo.wordParts " +
				"where " +
					PersistenceManager.generateInPhrase( "wo" , words ) +
					" and " +
				"wp.word=wo and " +
				"lp=wp.lemPos and " +
				"le=lp.lemma and " +
				"sp=wo.speech" ,
				new String[]{ "words" } ,
				new Object[]{ words }
			);
*/
/*
			return PersistenceManager.doQuery
			(
				"select wo, wp, lp, le, sp from " +
				"Word wo, WordPart wp, LemPos lp, Lemma le, Speech sp " +
				"inner join fetch wo.wordParts " +
				"where wo in (:words) and " +
				"wp.word=wo and " +
				"lp=wp.lemPos and " +
				"le=lp.lemma and " +
				"sp=wo.speech",
				new String[]{ "words" } ,
				new Object[]{ words }
			);
*/
		}
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected PreloadUtils()
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

