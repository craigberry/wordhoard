package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Countable word data counter.
 *
 *	<p>
 *	Counts the data in one or more {@link CountableWordData} objects.
 *	These are most often the results of a WordHoard CQL query.
 *	</p>
 *
 */

public class CountableWordDataCounter
{
	/**	The list of countable word data items to count.
	 */

	protected Collection wordDataList;

	/**	The word count maps.
	 */

	protected Map[] wordCountMaps;

	/**	The word total count maps.
	 */

	protected Map[] totalWordCountMaps;

	/**	Countable word data map.
	 */

	protected Map countableWordDataMap;

	/**	Word class/word class map for spellings.
	 */

	protected Map posMap;

	/**	Set of unique word ID lists for phrases.
	 */

	protected Set wordIDsForPhrasesSet;

	/**	Set of work part IDs encountered.
	 */

	protected Set workPartIDsSet;

	/**	Set of work IDs encountered.
	 */

	protected Set workIDsSet;

	/**	Set of word tags encountered.
	 */

	protected Set wordTagsSet;

	/**	Batch size for counting word data entries.
	 */

	protected final static int batchSize	= 5000;

	/**	Create countable word data counter.
	 */

	public CountableWordDataCounter()
	{
		initMaps();
	}

	/**	Create countable word data counter.
	 *
	 *	@param	wordDataList			A collection of countable word data
	 *									items to count.
	 *
	 *	<p>
	 *	Use this constructor when all the words to be counted are
	 *	present in the collection passed as an argument.
	 *	</p>
	 */

	public CountableWordDataCounter( java.util.Collection wordDataList )
	{
		this.wordDataList	= wordDataList;

								//	Initialize count maps.
		initMaps();
								//	Count word parts.

		countWordParts( wordDataList , wordCountMaps , totalWordCountMaps );

								//	Count spellings.
		countSpellings
		(
			countableWordDataMap ,
			wordCountMaps ,
			totalWordCountMaps
		);
	}

	/**	Create countable word data counter.
	 *
	 *	@param	iterator		Iterator over countable word data objects.
	 *
	 *	<p>
	 *	Use this constructor when the objects to count come from a
	 *	scrollable query result.  The word data is counted in batches
	 *	to reduce memory requirements.
	 *	</p>
	 */

	public CountableWordDataCounter( Iterator iterator )
	{
        						//	Initialize count maps.
		initMaps();
								//	Count word parts.

		countWordParts( iterator , true , null );

								//	Count spellings.
		countSpellings
		(
			countableWordDataMap ,
			wordCountMaps ,
			totalWordCountMaps
		);
	}

	/**	Initializes the maps and sets used to hold the word data and counts.
	 */

	protected void initMaps()
	{
		wordCountMaps			= new Map[ WordForms.NUMBEROFWORDFORMS ];
		totalWordCountMaps		= new Map[ WordForms.NUMBEROFWORDFORMS ];
		countableWordDataMap	= new TreeMap();
		posMap					= new TreeMap();
		wordIDsForPhrasesSet	= new HashSet( batchSize );
		workPartIDsSet			= new HashSet();
		workIDsSet				= new HashSet();
		wordTagsSet				= new HashSet( batchSize );

		for ( int i = 0 ; i < WordForms.NUMBEROFWORDFORMS ; i++ )
		{
			wordCountMaps[ i ]		= new TreeMap();
			totalWordCountMaps[ i ]	= new TreeMap();
		}
	}

	/**	Get the word count maps.
	 *
	 *	@return		The word count maps.
	 */

	public Map[] getWordCountMaps()
	{
		return wordCountMaps;
	}

	/**	Get the total count maps.
	 *
	 *	@return		The total count maps.
	 */

	public Map[] getTotalWordCountMaps()
	{
		return totalWordCountMaps;
	}

	/**	Update a count map for a specific word and word form.
	 *
	 *	@param	wordCountMap		The word counts.
	 *	@param	totalWordCountMap	The total counts.
	 *	@param	value				The word value.
	 *	@param	workId				The work ID for this word.
	 */

	protected static void countOneWordForm
	(
		Map wordCountMap ,
		Map totalWordCountMap ,
		Spelling value ,
		Long workId
	)
	{
								//	Update count for this word form in
								//	the work in which it appears.

		Map map	= (Map)wordCountMap.get( workId );

		if ( map == null )
		{
			map = new TreeMap();
			wordCountMap.put( workId , map );
		}

		CountMapUtils.updateWordCountMap( value , 1 , map );

								//	Update total count for word form.
		Integer count	=
			(Integer)totalWordCountMap.get( workId );

		if ( count == null )
		{
			totalWordCountMap.put( workId , new Integer( 1 ) );
		}
		else
		{
			totalWordCountMap.put(
				workId , new Integer( count.intValue() + 1 ) );
		}
	}

	/**	Update a count map for several word form values for a word.
	 *
	 *	@param	wordCountMap		The word counts.
	 *	@param	totalWordCountMap	The total counts.
	 *	@param	values				The word values.
	 *	@param	workId				The work ID for this word.
	 */

	protected static void countOneWordForm
	(
		Map wordCountMap ,
		Map totalWordCountMap ,
		Spelling[] values ,
		Long workId
	)
	{
								//	Update count for this word form in
								//	the work in which it appears.

		Map map	= (Map)wordCountMap.get( workId );

		if ( map == null )
		{
			map = new TreeMap();
			wordCountMap.put( workId , map );
		}
								//	Update counts for each word form value.

		for ( int i = 0 ; i < values.length ; i++ )
		{
			CountMapUtils.updateWordCountMap( values[ i ] , 1 , map );

			Integer count	= (Integer)totalWordCountMap.get( workId );

			if ( count == null )
			{
				totalWordCountMap.put( workId , new Integer( 1 ) );
			}
			else
			{
				totalWordCountMap.put(
					workId , new Integer( count.intValue() + 1 ) );
			}
		}
	}

	/**	Get word counts for a set of words.
	 *
	 *	@param	wordDataList		The countable word data for the words.
	 *	@param	wordCountMaps		The output word count maps.
	 *	@param	totalWordCountMaps	The output total word count maps.
	 *
	 *	<p>
	 *	The indices of the count maps and total counts match those of
	 *	of the word forms as given in {@link WordForms}.
	 *	</p>
	 */

	public void countWordParts
	(
		Collection wordDataList ,
		Map[] wordCountMaps ,
		Map[] totalWordCountMaps
	)
	{
								//	Get counts for each type of word form.
								//	Since the query results come back at
								//	the word part level, we need to
								//	ignore duplicate values for the
								//	word level.  We use the wordIdMap
								//	to tell if we've already counted
								//	values for a word.

		for	(	Iterator iterator	= wordDataList.iterator() ;
				iterator.hasNext() ;
			)
		{
								//	Next word's data.

			CountableWordData wordData	= (CountableWordData)iterator.next();

								//	Work ID for this word.

			Long workId					= wordData.getWorkId();

								//	Work part ID for this word.

			Long workPartId				= wordData.getWorkPartId();

								//	Save word data for this part of the
								//	current word.	If we've
								//	already encountered this word/word part
								//	combination, just go on to the
								//	next countable word object.

			if	(	!countableWordDataMap.containsKey
					(
						wordData.getWordPartOrdinal()
					)
				)
			{
				countableWordDataMap.put
				(
					wordData.getWordPartOrdinal() ,
					wordData
				);
			}
			else
			{
				continue;
			}
								//	Extract word class tags from
								//	the spelling and compound word class.
								//	The word class tags appear in
								//	parentheses following the spelling.
								//	The tags are separated by hyphens.

			countOneWordForm
			(
				(Map)wordCountMaps[ WordForms.WORDCLASS ] ,
				(Map)totalWordCountMaps[ WordForms.WORDCLASS ] ,
				new Spelling
				(
					wordData.getWordClassTag() ,
					TextParams.ROMAN
				) ,
				workId
			);
								//	Extract lemmata from the compound lemma
								//	string.  The lemmata are separated by
								//	commas in the compound lemma string.

			countOneWordForm
			(
				(Map)wordCountMaps[ WordForms.LEMMA ] ,
				(Map)totalWordCountMaps[ WordForms.LEMMA ] ,
				wordData.getLemmaTag() ,
				workId
			);
								//	Only count the word-level tags once.

			if ( wordData.wordPartIndex == 0 )
			{
                                //	Speaker gender.

				countOneWordForm
				(
					(Map)wordCountMaps[ WordForms.SPEAKERGENDER ] ,
					(Map)totalWordCountMaps[ WordForms.SPEAKERGENDER ] ,
					new Spelling
					(
						String.valueOf( wordData.getGender() ) ,
						TextParams.ROMAN
					) ,
					workId
				);
                                //	Speaker mortality.

				countOneWordForm
				(
					(Map)wordCountMaps[ WordForms.SPEAKERMORTALITY ] ,
					(Map)totalWordCountMaps[ WordForms.SPEAKERMORTALITY ] ,
					new Spelling
					(
						String.valueOf( wordData.getMortality() ) ,
						TextParams.ROMAN
					) ,
					workId
				);
								//	Poetry versus Prose.

				countOneWordForm
				(
					(Map)wordCountMaps[ WordForms.ISVERSE ] ,
					(Map)totalWordCountMaps[ WordForms.ISVERSE ] ,
					new Spelling
					(
						String.valueOf( wordData.getProsodic() ) ,
						TextParams.ROMAN
					) ,
					workId
				);
								//	Semantic category.
/*
				countOneWordForm
				(
					(Map)wordCountMaps[ WordForms.SEMANTICCATEGORY ] ,
					(Map)totalWordCountMaps[ WordForms.SEMANTICCATEGORY ] ,
					wordData.getSemanticTags() ,
					workId
				);
*/
								//	Metrical shape.

				countOneWordForm
				(
					(Map)wordCountMaps[ WordForms.METRICALSHAPE ] ,
					(Map)totalWordCountMaps[ WordForms.METRICALSHAPE ] ,
					new Spelling
					(
						wordData.getMetricalShape() ,
						TextParams.ROMAN
					) ,
					workId
				);
			}
		}
	}

	/**	Count spellings.
	 *
	 *	@param	countableWordDataMap	The countable word data.
	 *	@param	wordCountMaps			The word count maps.
	 *	@param	totalWordCountMaps		The total word count maps.
	 *
	 *	<p>
	 *	The word count and total word count maps are updated with the
	 *	spelling counts.
	 *	</p>
	 */

	public void countSpellings
	(
		Map countableWordDataMap ,
		Map[] wordCountMaps ,
		Map[] totalWordCountMaps
	)
	{
								//	Count the spelling entries when all
								//	the word data batches have been processed.

		for	(	Iterator iterator	= countableWordDataMap.keySet().iterator() ;
				iterator.hasNext() ;
			)
		{
								//	Next piece of word data.

			Long wordPartOrdinal	= (Long)iterator.next();

			CountableWordData countableWordData	=
				(CountableWordData)countableWordDataMap.get( wordPartOrdinal );

								//	Ignore it if it isn't the 0th word part.

			if ( countableWordData.getWordPartIndex() != 0 ) continue;

								//	Get word ID for this word.

			Long wordId	= countableWordData.getWordId();

								//	Construct the compound word class
								//	for this spelling.

			StringBuffer spellingAndCompoundWordClass	= new StringBuffer();

								//	Start with the spelling itself.

			spellingAndCompoundWordClass.append
			(
				countableWordData.getSpelling().getString()
			);

			spellingAndCompoundWordClass.append( " (" );

								//	Now get the list of parts of speech
								//	and append them to the spelling in
								//	parentheses with a hyphen separating
								//	each word class.

			for	(	int wordPartIndex = 0 ;
					wordPartIndex < 100 ;
					wordPartIndex++
				)
			{
								//	Get ordinal of next potential
								//	word part.

				Long wordPartOrdinal2	=
					CountableWordData.generateWordPartOrdinal
					(
						wordId ,
						wordPartIndex
					);

								//	If the word part data contains
								//	this ordinal, grab its word class
								//	and add it to the spelling.

				if ( countableWordDataMap.containsKey( wordPartOrdinal2 ) )
				{
					CountableWordData wordData	=
						(CountableWordData)countableWordDataMap.get(
							wordPartOrdinal2 );

					if ( wordPartIndex > 0 )
					{
						spellingAndCompoundWordClass.append( "-" );
					}

					spellingAndCompoundWordClass.append
					(
						wordData.getWordClassTag()
					);
				}
				else
				{
					break;
				}
			}

			spellingAndCompoundWordClass.append( ")" );

								//	Update spelling count.
								//	We actually count the combination
								//	of spelling and compound pos so that
								//	different meanings of a given spelling
								//	are not combined.  For example, we do
								//	not want to combine "love" as a verb
								//	with "love" as a noun.

			countOneWordForm
			(
				(Map)wordCountMaps[ WordForms.SPELLING ] ,
				(Map)totalWordCountMaps[ WordForms.SPELLING ] ,
				new Spelling
				(
					spellingAndCompoundWordClass.toString() ,
					countableWordData.getSpelling().getCharset()
				) ,
				countableWordData.getWorkId()
			);
		}
	}

	/**	Count spellings.
	 *
	 *	<p>
	 *	The word count and total word count maps are updated with the
	 *	spelling counts.
	 *	</p>
	 */

	public void countSpellings()
	{
		countSpellings
		(
			countableWordDataMap ,
			wordCountMaps ,
			totalWordCountMaps
		);
	}

	/**	Count words from an iterator over CountWordData objects.
	 *
	 *	@param	iterator			Iterator over CountableWordData objects.
	 *	@param	lastIterator		True if this iterator is for the last
	 *								batch of words to count.
	 *	@param	progressReporter	Progress reporter.  Can be null.
	 */

	public void countWordParts
	(
		Iterator iterator ,
		boolean lastIterator ,
		ProgressReporter progressReporter
	)
	{
								//	Holds batches of countable word data
								//	objects.

		wordDataList		= new ArrayList();

								//	True when we've processed all the words
								//	specified by the iterator.

		boolean done		= false;

								//	We will retrieve and count the word
								//	data in batches.
		while ( !done )
		{
								//	Count number of entries in current batch.
			int k	= 0;
								//	Pick up a batch of word data entries
								//	to process.

			while ( iterator.hasNext() )
			{
								//	Next word data item.

				Object[] o	= (Object[])iterator.next();

								//	There may be more than one set of
								//	word data items per row.  Split them
								//	up into individual entries.  Save the
								//	word IDs so we can generate phrases
								//	later if needed.

				IDList idList	= new IDList();

				for ( int i = 0 ; i < o.length ; i = i + 10 )
				{
					Gender gender		= null;
					Mortality mortality	= null;

					if ( o[ i + 6 ] != null )
					{
						Speech speech	=
							(Speech)PersistenceManager.doLoad
							(
								Speech.class ,
								(Long)o[ i + 6 ]
							);

						gender			= speech.getGender();
						mortality		= speech.getMortality();
					}

					CountableWordData cws	=
						new CountableWordData
						(
							(Long)o[ i ],
							(String)o[ i + 1 ],
							(Long)o[ i + 2 ],
							(Long)o[ i + 3 ],
							(Spelling)o[ i + 4 ],
							(MetricalShape)o[ i + 5 ],
							gender ,
							mortality ,
							(Prosodic)o[ i + 7 ],
							(Integer)o[ i + 8 ],
							(Spelling)o[ i + 9 ]
						);
								//	Add the word data entry to the list.

					wordDataList.add( cws );

								//	Add tags to appropriate sets.

					workPartIDsSet.add( cws.getWorkPartId() );
					workIDsSet.add( cws.getWorkId() );
					wordTagsSet.add( cws.getWordTag() );

								//	Add the word ID to the current phrase.

					idList.add( (Long)o[ i ] );

								//	If we've accumulated at least batchSize
								//	entries, count them.

					if ( ++k >= batchSize )
					{
						countWordParts
						(
							wordDataList ,
							wordCountMaps ,
							totalWordCountMaps
						);

								//	Get a fresh list to hold the next
								//	batch of word data entries.

						wordDataList	= new ArrayList();

								//	Reset the batch counter.

						k				= 0;

						PrintfFormat progressFormat	=
							new PrintfFormat
							(
								WordHoardSettings.getString
								(
									"Retrievingwordsandcounts" ,
									"Retrieving words and counts ... %s"
								)
							);

						if ( progressReporter != null )
						{
							progressReporter.updateProgress
							(
								progressFormat.sprintf
								(
									Formatters.formatIntegerWithCommas
									(
										getWordsCounted()
									)
								)
							);
						}
					}
				}
								//	Save the ID list for later use in
								//	generating phrases.

				wordIDsForPhrasesSet.add( idList );
			}
								//	Process the last batch of word data
								//	entries.  There may not be any.
			done	= true;

			countWordParts( wordDataList , wordCountMaps , totalWordCountMaps );

								//	Now count the spellings if this
								//	is really the last batch.

			if ( lastIterator )
			{
				countSpellings
				(
					countableWordDataMap ,
					wordCountMaps ,
					totalWordCountMaps
				);
			}

			PrintfFormat progressFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"Retrievingwordsandcounts" ,
						"Retrieving words and counts ... %s"
					)
				);

			if ( progressReporter != null )
			{
				progressReporter.updateProgress
				(
					progressFormat.sprintf
					(
						Formatters.formatIntegerWithCommas
						(
							getWordsCounted()
						)
					)
				);
			}
		}

		wordDataList = null;
	}

	/**	Count words from a list of Word objects.
	 *
	 *	@param	words				Array of Word objects.
	 *	@param	lastWords			True if this is the last batch of words
	 *								to count.
	 *	@param	progressReporter	Progress reporter.  Can be null.
	 */

	public void countWordParts
	(
		Word[] words ,
		boolean lastWords ,
		ProgressReporter progressReporter
	)
	{
		countWordParts( Arrays.asList( words ) , lastWords , progressReporter );
	}

	/**	Count words from a list of Word objects.
	 *
	 *	@param	words				Collection of Word objects.
	 *	@param	lastWords			True if this is the last batch of words
	 *								to count.
	 *	@param	progressReporter	Progress reporter.  Can be null.
	 */

	public void countWordParts
	(
		Collection words ,
		boolean lastWords ,
		ProgressReporter progressReporter
	)
	{
								//	Holds batches of countable word data
								//	objects.

		wordDataList		= new ArrayList();

		for	(	Iterator wordIterator = words.iterator() ;
				wordIterator.hasNext() ; )
		{
								//	Next word data item.

			Word word	= (Word)wordIterator.next();

								//	Set gender and mortality.

			Gender gender		= null;
			Mortality mortality	= null;

			if ( word.getSpeech() != null )
			{
				Speech speech	= word.getSpeech();
				gender			= speech.getGender();
				mortality		= speech.getMortality();
			}
								//	Create countable word data entry for
								//	each word part.

			java.util.List wordParts	= word.getWordParts();

			for	(	Iterator iterator	= wordParts.iterator() ;
					iterator.hasNext() ; )
			{
				WordPart wordPart	= (WordPart)iterator.next();

				CountableWordData cws	=
					new CountableWordData
					(
						word.getId() ,
						word.getTag() ,
						word.getWork().getId() ,
						word.getWorkPart().getId() ,
						word.getSpellingInsensitive() ,
						word.getMetricalShape() ,
						gender ,
						mortality ,
						word.getProsodic() ,
						new Integer( wordPart.getPartIndex() ) ,
						wordPart.getLemPos().getLemma().getTagInsensitive()
					);
								//	Add tags to appropriate sets.

					workPartIDsSet.add( word.getWorkPart().getId() );
					workIDsSet.add( word.getWork().getId() );
					wordTagsSet.add( word.getTag() );

								//	Add the word data entry to the list.

				wordDataList.add( cws );
			}
		}
								//	Count last batch of words.

		countWordParts( wordDataList , wordCountMaps , totalWordCountMaps );

								//	Now count the spellings if this
								//	is really the last batch.
		if ( lastWords )
		{
			countSpellings
			(
				countableWordDataMap ,
				wordCountMaps ,
				totalWordCountMaps
			);
		}

		PrintfFormat progressFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"Calculatingwordcounts" ,
					"Calculating word counts ..."
				)
			);

		if ( progressReporter != null )
		{
			progressReporter.updateProgress
			(
				progressFormat.sprintf( getWordsCounted() )
			);
		}

		wordDataList = null;
	}

	/**	Merge word data into phrase data.
	 *
	 *	@param	wordData	The data for a word.
	 *	@param	phraseData	The data for a phrase.
	 */

	protected void mergeWordDataIntoPhraseData
	(
		CountableWordData wordData ,
		CountableWordData phraseData
	)
	{
	}

	/**	Create phrases.
	 */

	public void createPhrases()
	{
		ArrayList phraseList		= new ArrayList();
		ArrayList phraseDataList	= new ArrayList();

		for	(	Iterator iterator = wordIDsForPhrasesSet.iterator() ;
				iterator.hasNext() ;
			)
		{
			IDList idList		= (IDList)iterator.next();
			Long[] ids			= (Long[])idList.getIDs().toArray( new Long[]{} );
			String[] wordTags	= new String[ ids.length ];
			Long workId			= null;

			CountableWordData phraseData	= new CountableWordData();

			for ( int i = 0 ; i < ids.length ; i++ )
			{
				Long wordPartOrdinal	=
					CountableWordData.generateWordPartOrdinal( ids[ i ] , 0 );

				CountableWordData wordData	=
					(CountableWordData)countableWordDataMap.get(
						wordPartOrdinal );

				wordTags[ i ]	= wordData.getWordTag();
				workId			= wordData.getWorkId();

				mergeWordDataIntoPhraseData( wordData , phraseData );
			}

			String workTag	= WorkUtils.getWorkPartTagById( workId );

			phraseList.add( new Phrase( wordTags , workTag ) );
			phraseDataList.add( phraseData );
		}
	}

	/**	Count phrases.
	 */

	public void countPhrases()
	{
	}

	/**	Get the countable word data map.
	 *
	 *	@return		The spelling map.
	 */

	public Map getCountableWordDataMap()
	{
		return countableWordDataMap;
	}

	/**	Get number of word parts counted.
	 *
	 *	@return		Number of word parts counted.
	 */

	public int getWordPartsCounted()
	{
		return countableWordDataMap.keySet().size();
	}

	/**	Get number of words counted.
	 *
	 *	@return		Number of words counted.
	 */

	public int getWordsCounted()
	{
		return wordTagsSet.size();
	}

	/**	Get number of works counted.
	 *
	 *	@return		Number of works counted.
	 */

	public int getWorksCounted()
	{
		return workIDsSet.size();
	}

	/**	Get number of work parts counted.
	 *
	 *	@return		Number of work parts counted.
	 */

	public int getWorkPartsCounted()
	{
		return workPartIDsSet.size();
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

