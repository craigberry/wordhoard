package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.math.NumberOps;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Count map utilities.
 *
 *	<p>
 *	Count maps have a String or Spelling key representing items to count and
 *	Number values as the counts.  In many cases, the Number values
 *	are integers, but this is not necessarily the case.  For example,
 *	a count map may have word spellings as keys and scaled word frequencies
 *	(taking on values between 0.0 and 1.0) as Number values.
 *	</p>
 */

public class CountMapUtils
{
	/**	Get summary counts from a count map.
	 *
	 *	@param	map		The map with string keys and Number counts
	 *					as values.
	 *
	 *	@return			Three entry double array.
	 *						result[ 0 ]	= sum of counts
	 *						result[ 1 ]	= sum of squared counts
	 *						result[ 2 ]	= unique count (size of map)
	 */

	public static double[] getSummaryCountsFromCountMap( Map map )
	{
		double result[]	= new double[ 3 ];

		result[ 0 ]	= 0.0D;
		result[ 1 ]	= 0.0D;
		result[ 2 ]	= (double)map.size();

		Set keyset			= map.keySet();
		Iterator iterator	= keyset.iterator();

		while ( iterator.hasNext() )
		{
			double dCount	=
				((Number)map.get( iterator.next() ) ).doubleValue();

			result[ 0 ]		+=  dCount;
			result[ 1 ]		+=  dCount * dCount;
		}

		return result;
	}

	/**	Get total count of words in map.
	 *
	 *	@param	map		The map with string keys and Number counts
	 *					as values.
	 *
	 *	@return			Sum of counts as an integer.
	 */

	public static int getTotalWordCount( Map map )
	{
		int result			= 0;

		Set keyset			= map.keySet();
		Iterator iterator	= keyset.iterator();

		while ( iterator.hasNext() )
		{
			int count	=
				((Number)map.get( iterator.next() ) ).intValue();

			result		+=  count;
		}

		return result;
	}

	/**	Get sum of cross products for counts in two maps.
	 *
	 *	@param	countMap1	First count map.
	 *	@param	countMap2	Second count map.
	 *
	 *	@return				sum of cross products as a double.
	 */

	public static double getSumOfCrossProducts
	(
		Map countMap1 ,
		Map countMap2
	)
	{
								//	Holds cross product.

		double result	= 0.0D;

								//	Iterate over shorter map for
								//	efficiency.

		if ( countMap1.size() > countMap2.size() )
		{
			for	(	Iterator iterator = countMap2.keySet().iterator() ;
					iterator.hasNext() ; )
			{
				Object key	= iterator.next();

				if ( countMap1.containsKey( key ) )
				{
					Number count1	= (Number)countMap1.get( key );
					Number count2	= (Number)countMap2.get( key );

					result +=
						NumberOps.multiply( count1 , count2 ).doubleValue();
				}
			}
		}
		else
		{
			for	(	Iterator iterator = countMap1.keySet().iterator() ;
					iterator.hasNext() ; )
			{
				Object key	= iterator.next();

				if ( countMap2.containsKey( key ) )
				{
					Number count1	= (Number)countMap1.get( key );
					Number count2	= (Number)countMap2.get( key );

					result +=
						NumberOps.multiply( count1 , count2 ).doubleValue();
				}
			}
		}

		return result;
	}

	/**	Convert map values to integer 1 or 0.
	 *
	 *	@param	map		Count map to booleanize.
	 *
	 *	@return			New map containing booleanized count values.
	 *
	 *	<p>
	 *	Non-zero counts are converted to integer 1, 0 counts are
	 *	converted to integer 0.
	 *	</p>
	 */

	public static Map booleanizeCountMap
	(
		Map map
	)
	{
		Map resultMap		= new TreeMap();
		Set keyset			= map.keySet();
		Iterator iterator	= keyset.iterator();

		while ( iterator.hasNext() )
		{
			Object key		= iterator.next();

			double dCount	=
				((Number)map.get( key ) ).doubleValue();

			int binValue	= ( dCount == 0.0D ) ? 0 : 1;

			resultMap.put( key , new Integer( binValue ) );
		}

		return resultMap;
	}

	/**	Scale count entries in count map.
	 *
	 *	@param	map				The count map.
	 *	@param	scaleFactor		The double value by which to
	 *							multiply each count value in the count map.
	 *
	 *	@return					Map with same keys as input map and
	 *							counts scaled using scaleFactor.
	 */

	public static Map scaleCountMap
	(
		Map map ,
		double scaleFactor
	)
	{
		Map resultMap		= new TreeMap();
		Set keyset			= map.keySet();
		Iterator iterator	= keyset.iterator();

		while ( iterator.hasNext() )
		{
			Object key		= iterator.next();

			double dCount	=
				((Number)map.get( key ) ).doubleValue();

			resultMap.put( key , new Double( dCount * scaleFactor ) );
		}

		return resultMap;
	}

	/**	Get words from a map.
	 *
	 *	@param	map		The map with keys and Number counts
	 *					as values.
	 *
	 *	@return			Keys as a set.
	 */

	public static Set getWordsFromMap( Map map )
	{
		TreeSet result		= new TreeSet();

		Set keyset			= map.keySet();
		Iterator iterator	= keyset.iterator();

		while ( iterator.hasNext() )
		{
			result.add( iterator.next() );
		}

		return result;
	}

	/**	Convert work map to work count map.
	 *
	 *	@param	map		The map with string keys and HashSet of work IDs
	 *					as values.
	 *
	 *	<p>
	 *	On return, map is modified to have work counts in place of
	 *	hash set for each key, e.g., the hash set is
	 *	replaced by its size.
	 *	</p>
	 */

	public static void worksToWorkCounts( Map map )
	{
		Set keyset			= map.keySet();
		Iterator iterator	= keyset.iterator();

		while ( iterator.hasNext() )
		{
			Object key		=	iterator.next();
			HashSet workIDs	=	(HashSet)map.get( key );

			map.put( key , new Integer( workIDs.size() ) );
		}
	}

	/**	Convert multiple work maps to a single work count map.
	 *
	 *	@param	maps	The map withs string keys and HashSet of work IDs
	 *					as values.
	 *
	 *	@return			Map with words as keys and work counts as values.
	 *					The counts represent the unique work counts.
	 */

	public static Map worksToWorkCounts( Map[] maps )
	{
		TreeMap combinedWorks	= new TreeMap();

		for ( int i = 0 ; i < maps.length ; i++ )
		{
			Map map				= maps[ i ];

			Set keyset			= map.keySet();
			Iterator iterator	= keyset.iterator();

			while ( iterator.hasNext() )
			{
				Object key		=	iterator.next();
				HashSet workIDs	=	(HashSet)map.get( key );

				HashSet combinedIDs	= (HashSet)combinedWorks.get( key );

				if ( combinedIDs == null )
				{
					combinedWorks.put( key , workIDs );
				}
				else
				{
					combinedIDs.addAll( workIDs );
				}
			}
		}

		worksToWorkCounts( combinedWorks );

		return combinedWorks;
	}

	/**	Split string at tab character.
	 *
	 *	@param	s	The string to split into a key and a count.
	 *
	 *	@return		Two element string array with the key and count.
	 */

	public static String[] splitKeyedCountString( String s )
	{
		String[] tokens	= new String[ 2 ];

		tokens[ 0 ]	= "";
		tokens[ 1 ]	= "0";

		if ( s == null ) return tokens;
		if ( s.length() == 0 ) return tokens;

		int tabPos	= s.indexOf( "\t" );

		if ( tabPos < 0 )
		{
			tokens[ 0 ]	= s;
		}
		else
		{
			tokens[ 0 ]	= s.substring( 0 , tabPos ).trim();
			tokens[ 1 ]	= s.substring( tabPos + 1 ).trim();
		}

		return tokens;
	}

	/**	Get the word counts from a reader.
	 *
	 *	@param	reader	The reader.
	 */

	public static Map getCountsFromReader( Reader reader )
		throws IOException
	{
		String[] tokens;
		int count;

        BufferedReader bufferedReader	=
        	new BufferedReader( reader );

		TreeMap map						= new TreeMap();
		String countLine				= bufferedReader.readLine();

		while ( countLine != null )
		{
			tokens		= countLine.split( "\t" );

			count		= Integer.parseInt( tokens[ 1 ] );

			map.put( tokens[ 0 ] , new Integer( count ) );

			countLine	= bufferedReader.readLine();
		}

		bufferedReader.close();

		return map;
	}

	/**	Get the word counts from a file.
	 *
	 *	@param	file	The input file.
	 */

	public static Map getCountsFromFile( File file )
		throws IOException
	{
		return getCountsFromReader( new FileReader( file ) );
	}

	/**	Get the word counts from a string.
	 *
	 *	@param	countsString	The input string.
	 */

	public static Map getCountsFromString( String countsString )
		throws IOException
	{
		return getCountsFromReader( new StringReader( countsString ) );
	}

	/**	Get the word counts from a file.
	 *
	 *	@param	fileName		The input file name.
	 */

	public static Map getCountsFromFile( String fileName )
		throws IOException
	{
		return getCountsFromFile( new File( fileName ) );
	}

	/**	Save the keyed counts to a file.
	 *
	 *	@param	countsMap		The map containing keyed counts.
	 *	@param	printWriter		The printWriter specifying the output file.
	 *
	 *	<p>
	 *	Each key is output on a separate line followed by a tab and
	 *	the count.
	 *	</p>
	 *
	 *	<p>
	 *	Example:
	 *	</p>
	 *
	 *	<p>
	 *	aardvark\t25<br />
	 *	abacus\t10<br />
	 *	 ...
	 *	</p>
	 */

	public static void saveCountsToWriter
	(
		Map countsMap ,
		PrintWriter printWriter
	)
		throws IOException
	{
		Iterator iterator	= countsMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			Object key		= iterator.next();
			Number count	= ((Number)countsMap.get( key ) );

			printWriter.println( key.toString() + "\t" + count.toString() );
		}
	}

	/**	Save the word counts to a file.
	 *
	 *	@param	countsMap		The map containing keyed counts.
	 *	@param	file		The output file.
	 */

	public static void saveCountsToFile( Map countsMap , File file )
		throws IOException
	{
		PrintWriter printWriter	=
			new PrintWriter( new FileWriter( file ) );

		saveCountsToWriter( countsMap , printWriter );

		printWriter.close();
	}

	/**	Save the word counts to a file.
	 *
	 *	@param	countsMap		The map containing keyed counts.
	 *	@param	fileName		The output file name.
	 */

	public static void saveCountsToFile( Map countsMap , String fileName )
		throws IOException
	{
		saveCountsToFile( countsMap , new File( fileName ) );
	}

	/**	Save the word counts to a string.
	 *
	 *	@param	countsMap		The map containing keyed counts.
	 */

	public static String saveCountsToString( Map countsMap )
		throws IOException
	{
		StringWriter writer	= new StringWriter();

		saveCountsToWriter( countsMap , new PrintWriter( writer ) );

		return writer.toString();
	}

	/**	Add words/counts from one map to another.
	 *
	 *	@param	destinationMap		Destination map.
	 *	@param	sourceMap			Source map.
	 *
	 *	<p>
	 *	On output, the destination map is updated with words and counts
	 *	from the source map.
	 *	</p>
	 */

	public static void addCountMap
	(
		Map destinationMap ,
		Map sourceMap
	)
	{
		Iterator iterator	= sourceMap.keySet().iterator();

								//	Loop over all words in source map.

		while ( iterator.hasNext() )
		{
								//	Get next word in source map.

			Object key			=	iterator.next();

								//	Get source map count for this word.

			Number sourceCount	= ((Number)sourceMap.get( key ) );

								//	If the destination map does not contain
								//	this word, add it with the count from
								//	the source map.

			if ( !destinationMap.containsKey( key ) )
			{
				destinationMap.put( key , sourceCount );
			}
			else
			{
								//	If the destination map contains the word,
								//	pick up the current count of the word
								//	in the destination map.

				Number destCount	= (Number)destinationMap.get( key );

								//	Add the source count to the destination
								//	count in the destination map.

				destinationMap.put
				(
					key ,
					NumberOps.add( sourceCount , destCount )
				);
			}
		}
	}

	/**	Increment words/counts in one map from another.
	 *
	 *	@param	destinationMap		Destination map.
	 *	@param	sourceMap			Source map.
	 *
	 *	<p>
	 *	On output, the destination map counts are incremented by one for
	 *	each word appearing in the source map.  If a source word does not
	 *	already appear in the destination, it is added with a count of one.
	 *	</p>
	 */

	public static void incrementCountMap
	(
		Map destinationMap ,
		Map sourceMap
	)
	{
		Iterator iterator	= sourceMap.keySet().iterator();

								//	Loop over all words in source map.

		while ( iterator.hasNext() )
		{
								//	Get next word in source map.

			Object key			=	iterator.next();

								//	Get source map count for this word.

			Number sourceCount	= ((Number)sourceMap.get( key ) );

								//	If the destination map does not contain
								//	this word, add it with a count of one.

			if ( !destinationMap.containsKey( key ) )
			{
				destinationMap.put( key , new Integer( 1 ) );
			}
			else
			{
								//	If the destination map contains the word,
								//	pick up the current count of the word
								//	in the destination map.

				Number destCount	= (Number)destinationMap.get( key );

								//	Increment the count by one in the
								//	destination map.

				destinationMap.put
				(
					key ,
					NumberOps.add( destCount , new Integer( 1 ) )
				);
			}
		}
	}

	/**	Subtract words/counts in one map from another.
	 *
	 *	@param	destinationMap		Destination map.
	 *	@param	sourceMap			Source map.
	 *
	 *	<p>
	 *	On output, the destination map counts are updated by removing the
	 *	counts for matching words from the source map.  If the count goes
	 *	to zero for any word in the destination, that word is removed from
	 *	from the destination map.
	 *	</p>
	 */

	public static void subtractCountMap
	(
		Map destinationMap ,
		Map sourceMap
	)
	{
		Iterator iterator	= sourceMap.keySet().iterator();

								//	Loop over all words in source map.

		while ( iterator.hasNext() )
		{
								//	Get next word in source map.

			Object key		=	iterator.next();

								//	Get source map count for this word.

			Number sourceCount	= ((Number)sourceMap.get( key ) );

								//	If the destination map contains
								//	this word ...

			if ( destinationMap.containsKey( key ) )
			{
								//	Get the count of the word in the
								//	destination map.

				Number destCount		= (Number)destinationMap.get( key );

								//	Subtract the source count to produce
								//	the updated destination map count
								//	for this word.

				Number updatedDestCount	=
					NumberOps.subtract( destCount , sourceCount );

								//	If the count remains positive,
								//	put the updated count in the
								//	destination map.  If the count is
								//	not positive, remove the word from
								//	the destination map.

				if ( NumberOps.compareToZero( updatedDestCount ) > 0 )
				{
					destinationMap.put( key , updatedDestCount );
				}
				else
				{
					destinationMap.remove( key );
				}
			}
		}
	}

	/**	Get list of words which two count maps share.
	 *
	 *	@param	countMap1	First count map.
	 *	@param	countMap2	Second count map.
	 *
	 *	@return				List of words appearing in both maps.
	 */

	public static java.util.List getWordsInCommon
	(
		Map countMap1 ,
		Map countMap2
	)
	{
								//	Holds sorted list of shared words.

		SortedArrayList result	= new SortedArrayList();

								//	Iterate over shorter map for
								//	efficiency.

		if ( countMap1.size() > countMap2.size() )
		{
			for	(	Iterator iterator = countMap2.keySet().iterator() ;
					iterator.hasNext() ; )
			{
				Object key	= iterator.next();

				if ( countMap1.containsKey( key ) )
				{
					result.add( key );
				}
			}
		}
		else
		{
			for	(	Iterator iterator = countMap1.keySet().iterator() ;
					iterator.hasNext() ; )
			{
				Object key	= iterator.next();

				if ( countMap2.containsKey( key ) )
				{
					result.add( key );
				}
			}
		}

		return result;
	}

	/**	Get count of words which two count maps share.
	 *
	 *	@param	countMap1	First count map.
	 *	@param	countMap2	Second count map.
	 *
	 *	@return				Count of words appearing in both maps.
	 */

	public static int getCountOfWordsInCommon
	(
		Map countMap1 ,
		Map countMap2
	)
	{
								//	Holds count shared words.
		int result	= 0;
								//	Iterate over shorter map for
								//	efficiency.

		if ( countMap1.size() > countMap2.size() )
		{
			for	(	Iterator iterator = countMap2.keySet().iterator() ;
					iterator.hasNext() ; )
			{
				Object key	= iterator.next();

				if ( countMap1.containsKey( key ) )
				{
					result++;
				}
			}
		}
		else
		{
			for	(	Iterator iterator = countMap1.keySet().iterator() ;
					iterator.hasNext() ; )
			{
				Object key	= iterator.next();

				if ( countMap2.containsKey( key ) )
				{
					result++;
				}
			}
		}

		return result;
	}

	/**	Get count for a specific word form from a count map.
	 *
	 *	@param	countMap	The word count map.
	 *	@param	word		The word text.
	 *
	 *	@return				The count for the specified word.
	 *						0 if the word does not occur.
	 */

	public static int getWordCount( Map countMap , Object word )
	{
		int result	= 0;

		if ( countMap.containsKey( word ) )
		{
			result	= ((Integer)countMap.get( word )).intValue();
		}

		return result;
	}

	/**	Updates counts for a word in a map.
	 *
	 *	@param	word		The word.
	 *	@param	count		The word count.
	 *	@param	countMap	The word count map.
	 */

	public static void updateWordCountMap
	(
		Object word ,
		int count ,
		Map countMap
	)
	{
		if ( countMap.containsKey( word ) )
		{
			Number oldCount	= (Number)countMap.get( word );

			countMap.put
			(
				word ,
				NumberOps.add( oldCount , new Integer( count ) )
			);
		}
		else
		{
			countMap.put( word , new Integer( count ) );
		}
	}

	/**	Updates works for a word in a map.
	 *
	 *	@param	word		The word.
	 *	@param	workId		The work ID.
	 *	@param	workMap		The map containing work IDs for the word.
	 */

	public static void updateWorkMap
	(
		Object word ,
		Long workId ,
		Map workMap
	)
	{
		if ( workMap.containsKey( word ) )
		{
			HashSet workIDs	= (HashSet)workMap.get( word );

			workIDs.add( workId );
		}
		else
		{
			HashSet workIDs	= new HashSet();

			workIDs.add( workId );

			workMap.put( word , workIDs );
		}
	}

	/**	Get deep clone of a count map.
	 *
	 *	@param	countMap	The count map to clone.
	 *
	 *	@return				Deep clone of the count map.
	 */

	public static Map deepClone( Map countMap )
	{
								//	Create a map to hold the
								//	results of the conversion.

		Map result			= new TreeMap();

								//	Get an iterator over the keys
								//	of the source map.

		Iterator iterator	= countMap.keySet().iterator();

								//	Loop over all words in source map
								//	and copy each word and its count
								//	to the result map.

		while ( iterator.hasNext() )
		{
								//	Get the key element and
								//	associated count.

			Object key			= iterator.next();
			Number value		= (Number)countMap.get( key );

								//	Holds the new (String) key.

			Object copyKey		= null;

								//	Holds the new count value.

			Number copyValue	= null;

								//	If key is null, ignore it.
								//	A count map should never have
								//	a null key entry.

			if ( key != null )
			{
								//	If the key is a string, use that
								//	as the cloned key.

				if ( key instanceof String )
				{
					copyKey	= key;
				}
								//	If the key is a Spelling object,
								//	create a new Spelling object
								//	with the same settings for the
								//	cloned key.

				else if ( key instanceof Spelling )
				{
					Spelling spellingKey	= (Spelling)key;

					copyKey	=
						new Spelling
						(
							spellingKey.getString() ,
							spellingKey.getCharset()
						);
				}
								//	If the key is something else,
								//	use its toString() method.
								//	If that fails, this key
								//	will not be added to the result.
				else
				{
					try
					{
						copyKey	= key.toString();
					}
					catch ( Exception e )
					{
						copyKey	= null;
					}
				}
								//	Get a copy of the count value.
								//	Null counts are set to Integer( 0 ).

				if ( value != null )
				{
					copyValue	= NumberOps.cloneNumber( value );
				}
				else
				{
					copyValue	= new Integer( 0 );
				}
			}
								//	Add cloned entry to result map.

			if ( copyKey != null ) result.put( copyKey , copyValue );
		}

		return result;
	}

	/**	Convert keys in count map to plain strings.
	 *
	 *	@param	countMap	The count map whose keys should be
	 *						converted to strings.
	 *
	 *	@return				The count map with keys converted to plain
	 *						strings.
	 *
	 *	<p>
	 *	Each key element's toString() method is called to convert
	 *	the key object to a plain text string.  Key elements without a
	 *	toString() method will not be added to the result map.
	 *	The object values (counts) are left untouched.  Note that
	 *	no key should be null.  Null keys will be ignored.
	 *	</p>
	 */

	public static Map convertKeysToStrings( Map countMap )
	{
								//	Create a map to hold the
								//	results of the conversion.

		Map result			= new TreeMap();

								//	Get an iterator over the keys
								//	of the source map.

		Iterator iterator	= countMap.keySet().iterator();

								//	Loop over all words in source map
								//	and copy each word and its count
								//	to the result map.

		while ( iterator.hasNext() )
		{
								//	Get the key element and
								//	associated count.

			Object key			= iterator.next();
			Number value		= (Number)countMap.get( key );

								//	Holds the new (String) key.

			Object copyKey		= null;

								//	If key is null, we will not add
								//	it to the result.  No count map
								//	should have null key elements.

			if ( key != null )
			{
								//	If key already a string, just
								//	use that as-is.

				if ( key instanceof String )
				{
					copyKey	= key;
				}
								//	If key is a Spelling object,
								//	get the string portion as the
								//	key.

				else if ( key instanceof Spelling )
				{
					copyKey	= ((Spelling)key).getString();
				}
								//	For anything else, use the
								//	toString() method.  If that fails,
								//	this item will not be added to the
								//	result map.
				else
				{
					try
					{
						copyKey	= key.toString();
					}
					catch ( Exception e )
					{
						copyKey	= null;
					}
				}
			}
								//	Add string key and associated
								//	count value to result map.

			if ( copyKey != null ) result.put( copyKey , value );
		}

		return result;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected CountMapUtils()
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

