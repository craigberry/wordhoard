package edu.northwestern.at.utils.corpuslinguistics;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.net.*;

import edu.northwestern.at.utils.*;

/**	Counts words in a text.
 */

public class WordCountExtractor
{
	/**	The list of words and word counts in the text.
	 *
	 *	<p>
	 *	Key=word<br />
	 *	Value=Integer(count)
	 *	</p>
	 */

	protected TreeMap wordCounts	= new TreeMap();

	/** The text parsed into a string array of words.
	 *
	 *	Package scope for the benefit of NGramExtractor.
	 */

	String[] words					= null;

	/** String array of unique words. */

	protected String[] uniqueWords	= null;

	/**	Extract word counts from a string array of words.
	 *
	 *	@param	words		The string array with the words.
	 */

	public WordCountExtractor
	(
		String[] words
	)
	{
		this.words	= (String[])words.clone();
		generateWordCountExtractor();
	}

	/**	Extract word counts from an arraylist of words.
	 *
	 *	@param	wordList	The arraylist with the words.
	 */

	public WordCountExtractor
	(
		ArrayList wordList
	)
	{
								//	Get the list of words into
								//	a string array.

		int nWords	= wordList.size();

		words		= new String[ nWords ];

		for ( int i = 0 ; i < nWords ; i++ )
		{
			words[ i ]	= (String)wordList.get( i );
		}

		generateWordCountExtractor();
	}

	/**	Extract word counts from a text file.
	 *
	 *	@param	fileName	The file containing the text to analyze.
	 *	@param	encoding	The encoding for the text file (.e.g, "utf-8").
	 */

	public WordCountExtractor
	(
		String fileName ,
		String encoding
	)
	{
								//	Holds list of words extracted
								//	from the text file in order
								//	of appearance.

		ArrayList wordList	= new ArrayList();

								//	Break up the input file into
								//	words using a FileTokenizer.
		try
		{
			FileTokenizer fileTokenizer	=
				new FileTokenizer( fileName , encoding );

			while( fileTokenizer.hasMoreTokens() )
			{
				wordList.add( fileTokenizer.getNextToken() );
			}

			fileTokenizer.close();
		}
		catch ( Exception e )
		{
//			e.printStackTrace();
		}
								//	Get the list of words into
								//	a string array.

		int nWords	= wordList.size();

		words		= new String[ nWords ];

		for ( int i = 0 ; i < nWords ; i++ )
		{
			words[ i ]	= (String)wordList.get( i );
		}

		wordList	= null;

		generateWordCountExtractor();
	}

	/**	Compute word counts from a string array of words.
	 */

	protected void generateWordCountExtractor()
	{
								//	Get the count of occurrence
								//	for each word.

		for ( int i = 0 ; i < words.length ; i++ )
		{
			String word	= words[ i ];

			if ( wordCounts.containsKey( word ) )
			{
				int freq	= ((Integer)wordCounts.get( word )).intValue();
				freq++;
				wordCounts.put( word , new Integer( freq ) );
			}
			else
			{
				wordCounts.put( word , new Integer( 1 ) );
			}
		}
		                        //	Create array of unique words.

		int nUniqueWords	= wordCounts.size();

		uniqueWords			= new String[ nUniqueWords ];

		Set keyset			= wordCounts.keySet();
		Iterator iterator	= keyset.iterator();

		for ( int i = 0 ; i < nUniqueWords ; i++ )
		{
			uniqueWords[ i ]	= (String)(iterator.next());
		}
	}

	/**	Return tokenized text words as a string array.
	 *
	 *	@return		The string array of words.
	 */

	public String[] getWords()
	{
		return words;
	}

	/**	Return the total number of words.
	 *
	 *	@return		The number of words.
	 */

	public int getNumberOfWords()
	{
		return words.length;
	}

	/**	Return unique words as a string array.
	 *
	 *	@return		The string array of unique words.
	 */

	public String[] getUniqueWords()
	{
		return uniqueWords;
	}

	/**	Return the number of unique words.
	 *
	 *	@return		The number of unique words.
	 */

	public int getNumberOfUniqueWords()
	{
		return uniqueWords.length;
	}

	/**	Return count for a specific word.
	 *
	 *	@param	word	The word whose count is desired.
	 *
	 *	@return			The count of the word in the text.
	 */

	public int getWordCount( String word )
	{
		int result	= 0;

		if ( wordCounts.containsKey( word ) )
		{
			Integer count	= (Integer)wordCounts.get( word );
			result			= count.intValue();
		}

		return result;
	}

	/**	Return word count map.
	 *
	 *	@return		Word count map.
	 */

	public Map getWordCounts()
	{
		return wordCounts;
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

