package edu.northwestern.at.utils.corpuslinguistics;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;

/**	Extract ngrams from text.
 */

public class NGramExtractor
{
	/**	The WordCountExtractor with the list of words to analyze.
	 */

	protected WordCountExtractor wordCountExtractor;

	/**	Number of words forming an ngram. */

	int nGramSize					= 2;

	/**	Window size within which to search for ngrams. */

	int windowSize					= 2;

	/**	The list of ngrams and associated counts.
	 *
	 *	<p>
	 *	Key=ngram string<br />
	 *	Value=Integer(count)
	 *	</p>
	 *
	 *	<p>
	 *	The ngram string is two or more words with
	 *	a tab character ("\t") separating the words.
	 *	</p>
	 */

	protected TreeMap nGramCounts	= new TreeMap();

	/**	Total number of ngrams. */

	protected int numberOfNGrams	= 0;

	/**	Create NGram analysis from string array of words.
	 *
	 *	@param	words		The string array with the words.
	 *	@param	nGramSize	The number of words forming an ngram.
	 *	@param	windowSize	The window size (number of words)
	 *						within which to construct ngrams.
	 *
	 *	<ul>
	 *	<li>windowSize must be greater than or equal to windowSize.</li>
	 *	<li>if windowSize is the same as nGramSize,
	 *		all ngrams are comprised of adjacent words.
	 *	<li>if windowSize is greater than nGramSize, all non-adjacent
	 *		word sets of length nGramSize are extracted from each
	 *		set of windowSize words.
	 *	</ul>
	 *
	 *	<p>
	 *	Example: nGramSize=2, windowSize=3, text="a quick brown fox".
	 *	</p>
	 *
	 *	<p>
	 *	The first window is "a quick brown".
	 *	The ngrams are "a quick", "a brown", and "quick brown".
	 *	</p>
	 *
	 *	<p>
	 *	The second window is "quick brown fox."
	 *	The ngrams are "quick brown", "quick fox", and "brown fox".
	 *	</p>
	 */

	public NGramExtractor
	(
		String[] words ,
		int nGramSize ,
		int windowSize
	)
	{
		this.nGramSize			= nGramSize;
		this.windowSize			= windowSize;

								//	Get word counts.

		wordCountExtractor		=  new WordCountExtractor( words );

								//	Generate ngrams.
		generateNGrams();
	}

	/**	Create NGram analysis from an arraylist of words.
	 *
	 *	@param	wordList	The arraylist with the words.
	 *	@param	nGramSize	The number of adjacent words forming an ngram.
	 *	@param	windowSize	The window size (number of words)
	 *						within which to construct ngrams.
	 */

	public NGramExtractor
	(
		ArrayList wordList ,
		int nGramSize ,
		int windowSize
	)
	{
		this.nGramSize			= nGramSize;
		this.windowSize			= windowSize;

								//	Get word frequencies.

		wordCountExtractor		=  new WordCountExtractor( wordList );

								//	Generate ngrams.
		generateNGrams();
	}

	/**	Create NGram analysis of a text file.
	 *
	 *	@param	fileName	The file containing the text to analyze.
	 *	@param	encoding	The encoding for the text file (.e.g, "utf-8").
	 *	@param	nGramSize	The number of adjacent words forming an Ngram.
	 *	@param	windowSize	The window size (number of words)
	 *						within which to construct ngrams.
	 */

	public NGramExtractor
	(
		String fileName ,
		String encoding ,
		int nGramSize ,
		int windowSize
	)
	{
		this.nGramSize			= nGramSize;
		this.windowSize			= windowSize;

								//	Extract words and counts from file.

		wordCountExtractor	=
			new WordCountExtractor( fileName , encoding );

								//	Generate ngrams.
		generateNGrams();
	}

	/**	Create NGram analysis from a WordCountExtractor.
	 *
	 *	@param	wordCountExtractor 		The WordCountExtractor containing
	 *									the words to analyze.
	 *	@param	nGramSize				The number of adjacent words forming
	 *									an Ngram.
	 *	@param	windowSize				The window size (number of words)
	 *									within which to construct ngrams.
	 */

	public NGramExtractor
	(
		WordCountExtractor wordCountExtractor ,
		int nGramSize ,
		int windowSize
	)
	{
		this.nGramSize				= nGramSize;
		this.windowSize				= windowSize;
		this.wordCountExtractor		= wordCountExtractor;

								//	Generate ngrams.
		generateNGrams();
	}

	/**	Generate NGram analysis from string array of words.
	 */

	protected void generateNGrams()
	{
								//	Generate the ngrams and
								//	compute the count of each.

		for ( 	int i = nGramSize - 1 ;
				i < wordCountExtractor.words.length ;
				i++
			)
		{
			StringBuffer sb	= new StringBuffer();

			for ( int j = ( nGramSize - 1 ) ; j >= 0 ; j-- )
			{
				if ( sb.length() > 0 )
				{
					sb	= sb.append( "\t" );
				}

				sb	= sb.append( wordCountExtractor.words[ i - j ] );
			}

			String nGramString	= sb.toString();

			if ( nGramCounts.containsKey( nGramString ) )
			{
				int freq	=
					((Integer)nGramCounts.get( nGramString )).intValue();

				freq++;

				nGramCounts.put( nGramString , new Integer( freq ) );
			}
			else
			{
				nGramCounts.put( nGramString , new Integer( 1 ) );
			}
		}
								//	Compute total ngram count.
		numberOfNGrams	= 0;

		for	(	Iterator iterator = nGramCounts.keySet().iterator() ;
				iterator.hasNext() ; )
		{
			Integer count	= (Integer)nGramCounts.get( iterator.next() );
			numberOfNGrams	+= count.intValue();
		}
	}

	/**	Merge ngrams from another NGramExtractor.
	 *
	 *	@param	extractor	Merge ngrams from another extractor.
	 */

	public void mergeNGramExtractor( NGramExtractor extractor )
	{
		SortedMap otherMap	= extractor.getNGramMap();

		for	(	Iterator iterator = otherMap.keySet().iterator() ;
				iterator.hasNext() ; )
		{
			String nGramString	= (String)iterator.next();

			if ( nGramCounts.containsKey( nGramString ) )
			{
				int freq	=
					((Integer)nGramCounts.get( nGramString )).intValue();

				freq++;

				nGramCounts.put( nGramString , new Integer( freq ) );
			}
			else
			{
				nGramCounts.put( nGramString , new Integer( 1 ) );
			}
		}
								//	Compute total ngram count.
		numberOfNGrams	= 0;

		for	(	Iterator iterator = nGramCounts.keySet().iterator() ;
				iterator.hasNext() ; )
		{
			Integer count	= (Integer)nGramCounts.get( iterator.next() );
			numberOfNGrams	+= count.intValue();
		}
	}

	/**	Return count for a specific ngram.
	 *
	 *	@param	ngram	The ngram whose count is desired.
	 *
	 *	@return			The count of the ngram in the text.
	 */

	public int getNGramCount( String ngram )
	{
		int result	= 0;

		if ( nGramCounts.containsKey( ngram ) )
		{
			Integer count	= (Integer)nGramCounts.get( ngram );
			result			= count.intValue();
		}

		return result;
	}

	/**	Return NGrams.
	 *
	 *	@return			String array of ngrams.
	 */

	public String[] getNGrams()
	{
		int nNGrams			= nGramCounts.size();

		String[] nGrams		= new String[ nNGrams ];

		Set keyset			= nGramCounts.keySet();

		Iterator iterator	= keyset.iterator();

		for ( int i = 0 ; i < nNGrams ; i++ )
		{
			nGrams[ i ]	= (String)(iterator.next());
		}

		return nGrams;
	}

	/**	Return NGram map.
	 *
	 *	@return			NGram map as a sorted map.
	 */

	public SortedMap getNGramMap()
	{
		return nGramCounts;
	}

	/**	Returns the total number of ngrams.
	 *
	 *	@return		The total number of ngrams.
	 */

	public int getNumberOfNGrams()
	{
		return numberOfNGrams;
	}

	/**	Returns the number of unique ngrams.
	 *
	 *	@return		The number of unique ngrams.
	 */

	public int getNumberOfUniqueNGrams()
	{
		return nGramCounts.size();
	}

	/**	Returns the individual words comprising an ngram.
	 *
	 *	@param	ngram	The ngram to parse.
	 *
	 *	@return			String array of the individual words
	 *					(in order) comprising the ngram.
	 */

	public static String[] splitNGramIntoWords( String ngram )
	{
		return StringUtils.makeTokenArray( ngram , "\t" );
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

