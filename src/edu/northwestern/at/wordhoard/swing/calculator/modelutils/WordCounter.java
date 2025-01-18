package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	A word form counter.
 *
 *	<p>
 *	WordCounter wraps a WordHoard model object which can count "words,"
 *	specifically, spellings, lemmata, speaker gender, parts of speech,
 *	prose versus poetry flag, and (eventually) semantic category.
 *	Currently wrappable objects include the Corpus, PhraseSet, WordSet,
 *	WorkSet, Work, and WorkPart objects.  All parts of WordHoard requiring
 *	word and work counts should get them through a WordCounter object.
 *	WordCounter hides all the ugly details involved in getting counts
 *	for the different types of objects.
 *	</p>
 */

public class WordCounter
{
	/**	The word counter classes.
	 */

	public static final Class[] wordFormCounterClasses	=
		new Class[]
		{
			Corpus.class ,
			PhraseSet.class ,
			WordSet.class ,
			Work.class ,
			WorkPart.class ,
			WorkSet.class
		};

	/**	Word counter types for the classes above.
	 */

	public static final int CORPUS		= 0;
	public static final int PHRASESET	= 1;
	public static final int WORDSET		= 2;
	public static final int WORK		= 3;
	public static final int WORKPART	= 4;
	public static final int WORKSET		= 5;

	/**	The object containing countable words.
	 */

	protected Object object		= null;

	/**	The type of word form counter object.
	 */

	protected int objectType	= CORPUS;

	/**	Create a word form counter for a CanCountWords object.
	 *
	 *	@param	canCountWords		The object implementing CanCountWords.
	 */

	public WordCounter( CanCountWords canCountWords )
	{
		this.object		= canCountWords;

		if ( canCountWords instanceof Corpus )
		{
			this.objectType	= CORPUS;
		}
		else if ( canCountWords instanceof PhraseSet )
		{
			this.objectType	= PHRASESET;
		}
		else if ( canCountWords instanceof WordSet )
		{
			this.objectType	= WORDSET;
		}
		else if ( canCountWords instanceof Work )
		{
			this.objectType	= WORK;
		}
		else if ( canCountWords instanceof WorkPart )
		{
			this.objectType	= WORKPART;
		}
		else
		{
			this.objectType	= WORKSET;
		}
	}

	/**	Create a word form counter for a specified object type and ID.
	 *
	 *	@param	anObjectType	WordCounter object type.
	 *	@param	anObjectId		The object's ID.
	 */

	public WordCounter( int anObjectType , Long anObjectId )
	{
		Class objectClass	= null;

		switch ( anObjectType )
		{
			case CORPUS		:
				objectClass	= Corpus.class;
				break;

			case PHRASESET	:
				objectClass	= PhraseSet.class;
				break;

			case WORDSET	:
				objectClass	= WordSet.class;
				break;

			case WORK		:
				objectClass	= Work.class;
				break;

			case WORKPART	:
				objectClass	= WorkPart.class;
				break;

			case WORKSET	:
				objectClass	= WorkSet.class;
				break;
		}

		if ( ( anObjectId != null ) && ( objectClass != null ) )
		{
			this.objectType	= anObjectType;

			this.object		=
				PersistenceManager.doLoad( objectClass , anObjectId );
		}
    }

	/**	Create a word form counter for a specified object type and tag.
	 *
	 *	@param	anObjectType	WordCounter object type.
	 *	@param	aTag			The object's tag.
	 *
	 *	<p>
	 *	Not all WordCounter objects have permanent tags.  Only those
	 *	with permanent tags can be created using this constructor.
	 *	</p>
	 */

	public WordCounter( int anObjectType , String aTag )
	{
		Class objectClass	= null;

		if ( ( aTag != null ) && ( aTag.length() > 0 ) )
		{
			this.objectType	= anObjectType;

			switch ( anObjectType )
			{
				case CORPUS		:
					objectClass	= Corpus.class;
					object		= CorpusUtils.getCorpusByTag( aTag );
					break;

				case WORK		:
					objectClass	= Work.class;
					object		= WorkUtils.getWorkByTag( aTag );
					break;

				case WORKPART	:
					objectClass	= WorkPart.class;
					object		= WorkUtils.getWorkPartByTag( aTag );
					break;
			}
		}
    }

	/**	Get the word form counter object.
	 *
	 *	@return		The word form counter object.
	 */

	public Object getObject()
	{
		return object;
	}

	/**	Get the word form counter object type.
	 *
	 *	@return		The word form counter object type.
	 */

	public int getObjectType()
	{
		return objectType;
	}

	/**	Get the word form counter object's persistence Id.
	 *
	 *	@return		The persistence Id.
	 */

	public Long getObjectId()
	{
		return ((PersistentObject)object).getId();
	}

	/**	Get the tag for an object.
	 *
	 *	@return		The tag for the object.
	 *
	 *	<p>
	 *	Not all WordCounter objects have tags.  For those that do not,
	 *	toString() is returned as the tag.
	 *	</p>
	 */

	public String getTag()
	{
		String result;

		if ( object instanceof HasTag )
		{
			result	= ((HasTag)object).getTag();
		}
		else
		{
			result	= toString();
		}

		return result;
	}

	/**	Is word counter a Corpus?
	 *
	 *	@return 	True if word counter is a Corpus.
	 */

	public boolean isCorpus()
	{
		return ( objectType == CORPUS );
	}

	/**	Is word counter a PhraseSet?
	 *
	 *	@return 	True if word counter is a PhraseSet.
	 */

	public boolean isPhraseSet()
	{
		return ( objectType == PHRASESET );
	}

	/**	Is word counter a Work?
	 *
	 *	@return 	True if word counter is a Work.
	 */

	public boolean isWork()
	{
		return ( objectType == WORK );
	}

	/**	Is word counter a work part?
	 *
	 *	@return 	True if word counter is a WorkPart.
	 */

	public boolean isWorkPart()
	{
		return ( objectType == WORKPART );
	}

	/**	Is word counter a WordSet?
	 *
	 *	@return 	True if word counter is a WordSet.
	 */

	public boolean isWordSet()
	{
		return ( objectType == WORDSET );
	}

	/**	Is word counter a WorkSet?
	 *
	 *	@return 	True if word counter is a WorkSet.
	 */

	public boolean isWorkSet()
	{
		return ( objectType == WORKSET );
	}

	/**	Get count of a word form.
	 *
	 *  @param	word			The word form whose count is desired.
	 *	@param	wordForm		The type of word form as specified
	 *							in {@link WordForms}.
	 *
	 *	@return					The count of times the word form appears.
	 */

	public int getWordFormCount( Spelling word , int wordForm )
	{
		if ( isWordSet() )
		{
			return WordSetUtils.getWordFormCount
			(
				(WordSet)object , word , wordForm
			);
		}
		else if ( isPhraseSet() )
		{
			return PhraseSetUtils.getWordFormCount
			(
				(PhraseSet)object , word , wordForm
			);
		}
		else
		{
			return WordCountUtils.getWordFormCount
			(
				this.getWorkParts() , word , wordForm
			);
		}
	}

	/**	Get counts for several words.
	 *
	 *  @param	words			The words whose counts are desired.
	 *	@param	wordForm		The type of word form as specified
	 *							in {@link WordForms}.
	 *
	 *	@return					Map with each word as a key and count of times
	 *							each word appears as a value.
	 */

	public Map getWordFormCount( Spelling[] words , int wordForm )
	{
		if ( isWordSet() )
		{
			return WordSetUtils.getWordFormCount
			(
				(WordSet)object , words , wordForm
			);
		}
		else if ( isPhraseSet() )
		{
			return PhraseSetUtils.getWordFormCount
			(
				(PhraseSet)object , words , wordForm
			);
		}
		else
		{
			return WordCountUtils.getWordFormCount
			(
				this.getWorkParts() , words , wordForm
			);
		}
	}

	/**	Get words and their counts of a specific word form type.
	 *
	 *	@param		wordForm		The word form as specified in {@link WordForms}.
	 *	@param		getWorkCounts	true to get work counts instead of work list
	 *								in second result map (see below).
	 *
	 *	@return						Array of two maps.
	 *								<p>
	 *								The first map contains each word of the
	 *								specified word form in the first set of
	 *								work parts as a key and the count of the
	 *								appearance of the word in the first set
	 *								of work parts as a value.
	 *								</p>
	 *								<p>
	 *								The second map also has the word as the key.
	 *								If getWorkCounts is true, the value for each
	 *								word provides the number of works (NOT work parts)
	 *								in which the word appears as a value.  If
	 *								getWorkParts is false, the value is a hash set
	 *								containing the work IDs of the works in which
	 *								the word appears.
	 *								</p>
	 */

	public Map[] getWordsAndCounts( int wordForm , boolean getWorkCounts )
	{
		if ( isWordSet() )
		{
	        return WordSetUtils.getWordCounts(
	        	new WordSet[]{ (WordSet)object } , wordForm , getWorkCounts );
		}
		else if ( isPhraseSet() )
		{
	        return PhraseSetUtils.getWordCounts(
	        	new PhraseSet[]{ (PhraseSet)object } , wordForm , getWorkCounts );
		}
		else
		{
	        return WordCountUtils.getWordCounts(
	        	getWorkParts() , wordForm , getWorkCounts );
		}
	}

	/**	Get words and their counts of a specific word form type.
	 *
	 *	@param		wordForm	The word form as specified in {@link WordForms}.
	 *
	 *	@return					Array of two maps.
	 *							<p>
	 *							The first map contains each word of the
	 *							specified word form in the first set of
	 *							work parts as a key and the count of the
	 *							appearance of the word in the first set
	 *							of work parts as a value.
	 *							</p>
	 *							<p>
	 *							The second map also has the word as the key but
	 *							provides the number of works (NOT work parts)
	 *							in which the word appears as a value.
	 *							</p>
	 */

	public Map[] getWordsAndCounts( int wordForm )
	{
		if ( isWordSet() )
		{
	        return WordSetUtils.getWordCounts(
	        	new WordSet[]{ (WordSet)object } , wordForm );
		}
		else if ( isPhraseSet() )
		{
	        return PhraseSetUtils.getWordCounts(
	        	new PhraseSet[]{ (PhraseSet)object } , wordForm );
		}
		else
		{
	        return WordCountUtils.getWordCounts( getWorkParts() , wordForm );
		}
	}

	/**	Get phrases and their counts of a specific word form type.
	 *
	 *	@param		wordForm	The word form as specified in {@link WordForms}.
	 *
	 *	@return					Array of two maps.
	 *							<p>
	 *							The first map contains each phrase containing
	 *							the specified word form as a key and the count
	 *							of the appearance of the phrase as a value.
	 *							</p>
	 *							<p>
	 *							The second map also has the phrase as the key but
	 *							provides the number of works (NOT work parts)
	 *							in which the phrase appears as a value.
	 *							</p>
	 */

	public Map[] getPhrasesAndCounts( int wordForm )
	{
		if ( isPhraseSet() )
		{
	        return PhraseSetUtils.getPhraseCounts(
	        	new PhraseSet[]{ (PhraseSet)object } , wordForm );
		}
		else
		{
	        return null;
		}
	}

	/**	Get words and counts for two WordCounter objects.
	 *
	 *	@param  otherCounter	The other word counter.
	 *	@param	wordForm		The word form as specified in {@link WordForms}.
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
	 *
	 *	<p>
	 *	This method significantly reduces the query load when the two
	 *	sets of work parts have common entries.
	 *	</p>
	 */

	public Map[] getWordsAndCounts
	(
		WordCounter otherCounter ,
		int wordForm
	)
	{
		if	(	isWordSet() || otherCounter.isWordSet() ||
			    isPhraseSet() || otherCounter.isPhraseSet() )
		{
			Map[] thisCounts	=
				getWordsAndCounts( wordForm , false );

			Map[] otherCounts	=
				otherCounter.getWordsAndCounts( wordForm , false );

			Map workCountsMap	=
				CountMapUtils.worksToWorkCounts
				(
					new Map[]
					{
						thisCounts[ 1 ] ,
						otherCounts[ 1 ]
					}
				);

			return new Map[]
			{
				thisCounts[ 0 ] , otherCounts[ 0 ] , workCountsMap
			};
		}
		else
		{
			return
				WordCountUtils.getWordCounts
				(
					this.getWorkParts() ,
					otherCounter.getWorkParts() ,
					wordForm
				);
		}
    }

	/**	Get total word count for word form type.
	 *
	 *	@param	wordForm	The word form as specified in {@link WordForms}.
	 *
	 *	@return				The total count of the word form type.
	 */

	public int getTotalWordFormCount( int wordForm )
	{
		if ( isWordSet() )
		{
			return WordSetUtils.getWordFormCount
			(
				(WordSet)object ,
				wordForm
			);
		}
		else if ( isPhraseSet() )
		{
			return PhraseSetUtils.getWordFormCount
			(
				(PhraseSet)object ,
				wordForm
			);
		}
		else
		{
			return WordCountUtils.getWordFormCount
			(
				this.getWorkParts() ,
				wordForm
			);
		}
	}

	/**	Get distinct word count for word form type.
	 *
	 *	@param	wordForm	The word form as specified in {@link WordForms}.
	 *
	 *	@return				The number of distinct values of the word form
	 *						type.
	 */

	public int getDistinctWordFormCount( int wordForm )
	{
		if ( isWordSet() )
		{
			return WordSetUtils.getDistinctWordFormCount
			(
				(WordSet)object ,
				wordForm
			);
		}
		else if ( isPhraseSet() )
		{
			return PhraseSetUtils.getDistinctWordFormCount
			(
				(PhraseSet)object ,
				wordForm
			);
		}
		else
		{
			return WordCountUtils.getDistinctWordFormCount
			(
				this.getWorkParts() ,
				wordForm
			);
		}
	}

	/**	Get word form and its counts by year.
	 *
	 *  @param	word			The word form whose count is desired.
	 *	@param	wordForm		The type of word form as specified
	 *							in {@link WordForms}.
	 *	@param	usePhrases		Analyze phrase counts instead of word counts
	 *							if the current object allows this.
	 *
	 *	@return					Three maps, each with the year as a key.
	 *
	 *							The first has the word count in the year
	 *							as a value.
	 *
	 *							The second has the total word count in
	 *							the year as a value.
	 *
	 *							The third has the work count in the year
	 * 							as a value.
	 */

	public Map[] getWordFormCountByYear
	(
		Spelling word ,
		int wordForm ,
		boolean usePhrases
	)
	{
								//	Get work parts in this word counter.

		WorkPart[] workParts	= getWorkParts();

								//	Track # of works in each year.

		Map yearWorksMap		= new TreeMap();

								//	Compute combined counts for selected
								//	word in all work parts in each year.

		Map yearWordMap			= new TreeMap();

								//	Compute combined counts for all
								//	words in all work parts in each year.

		Map yearTotalWordMap	= new TreeMap();

								//	Holds works done when a word set is
								//	being analyzed.

		HashSet worksDone		= new HashSet();

								//	Compute total word count for each
								//	year.

		int missingPubDates	= 0;

		for ( int i = 0 ; i < workParts.length ; i++ )
		{
								//	Get next work part.

			WorkPart workPart	= workParts[ i ];

								//	Get work for work part.

			Work work			= workPart.getWork();

								//	Get publication year for this work.
								//	If none, add to count of works
								//	without publication dates.

			if ( work.getPubDate() == null )
			{
				missingPubDates++;
				continue;
			}

			Integer year		= work.getPubDate().getStartYear();

			int wordCount		= 0;
			int totalWordCount	= 0;
            boolean updateCount	= true;

			if ( isPhraseSet() && usePhrases )
			{
				if ( !worksDone.contains( work.getId() ) )
				{
					PhraseSet phraseSet	= (PhraseSet)object;

								//	Get the count for the specified
								//	phrase in this work part.

					wordCount	=
						PhraseSetUtils.getWordFormPhraseCount
						(
							phraseSet ,
							word ,
							wordForm ,
							work
						);

								//	Get total number of phrases for
								//	this work part in this year.
								//	This is the total number of words + 1
								//	minus the phrase length.

					totalWordCount		=
						(int)(WordSetUtils.getWordFormCount
						(
							phraseSet ,
							wordForm ,
							work
						) -
						phraseSet.getMeanPhraseLength() + 1 );

								//	Add work to list of works done.

					worksDone.add( work.getId() );
				}
				else
				{
					updateCount	= false;
				}
			}
			else if ( isWordSet() || isPhraseSet() )
			{
				if ( !worksDone.contains( work.getId() ) )
				{
								//	Get the word count for the specified
								//	word in this work part.

					wordCount	=
						WordSetUtils.getWordFormCount
						(
							(WordSet)object ,
							word ,
							wordForm ,
							work
						);

								//	Get total number of words for
								//	this work part in this year.

					totalWordCount		=
						WordSetUtils.getWordFormCount
						(
							(WordSet)object ,
							wordForm ,
							work
						);
								//	Add work to list of works done.

					worksDone.add( work.getId() );
				}
				else
				{
					updateCount	= false;
				}
			}
			else
			{
								//	Get the word count for the specified
								//	word in this work part.
				wordCount	=
					WordCountUtils.getWordFormCount
					(
						workPart ,
						word ,
						wordForm
					);
								//	Get total number of words for
								//	this work part in this year.

				totalWordCount		=
					WordCountUtils.getWordFormCount
					(
						workPart ,
						wordForm
					);
			}
								//	If there is no word count for this
								//	year yet, just add the one from this
								//	work.

			if ( updateCount )
			{
				if ( !yearWordMap.containsKey( year ) )
				{
					yearWordMap.put( year , Integer.valueOf( wordCount ) );

					yearTotalWordMap.put(
						year , Integer.valueOf( totalWordCount ) );

								//	Add this work to the set of works
								//	for this year.

					HashSet workHashSet	= new HashSet();

					workHashSet.add( work.getId() );

					yearWorksMap.put( year , workHashSet );
				}
				else
				{
								//	If the word map contains the year,
								//	add the count from this work to
								//	the existing count.

					Integer currentCount	=
						(Integer)yearWordMap.get( year );

					yearWordMap.put
            		(
            			year ,
            			Integer.valueOf( currentCount.intValue() + wordCount )
            		);

					Integer currentTotalCount	=
						(Integer)yearTotalWordMap.get( year );

					yearTotalWordMap.put
	            	(
    	        		year ,
        	    		Integer.valueOf(
        	    			currentTotalCount.intValue() + totalWordCount )
            		);
								//	Add work to hash set of works for this
								//	year.  We will use this to get the
								//	number of works in this year later on.

					if ( yearWorksMap.containsKey( year ) )
					{
						HashSet workHashSet	=
							(HashSet)yearWorksMap.get( year );

						workHashSet.add( work.getId() );
					}
					else
					{
						HashSet workHashSet	= new HashSet();

						workHashSet.add( work.getId() );

						yearWorksMap.put( year , workHashSet );
					}
				}
			}
		}
								//	Convert work hash sets to work counts.

		Iterator iterator	= yearWorksMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			Integer year		= (Integer)iterator.next();

			HashSet workHashSet	= (HashSet)yearWorksMap.get( year );

			yearWorksMap.put( year , Integer.valueOf( workHashSet.size() ) );
		}

		return new Map[]{ yearWordMap , yearTotalWordMap , yearWorksMap };
	}

	/**	Get all available works, work sets, and corpora as WordCounter objects.
	 *
	 *	@param	includeCorpora		Return corpora.
	 *	@param	includeWorks		Return works.
	 *	@param	includeWorkSets		Return work sets.
	 *	@param	includeWordSets		Return word sets.
	 *	@param	includePhraseSets	Return phrase sets.
	 *
	 *	@return						Array of WordCounter objects.
	 */

	public static WordCounter[] getWordCounters
	(
		boolean includeCorpora ,
		boolean includeWorks ,
		boolean includeWorkSets ,
		boolean includeWordSets ,
		boolean includePhraseSets
	)
	{
		SortedArrayList wordFormCountersList	= new SortedArrayList();

								//	Get corpora.
		if ( includeCorpora )
		{
	        Corpus[] corpora	= CorpusUtils.getCorpora();

								//	Add corpora to word form counters list.

			for ( int i = 0 ; i < corpora.length ; i++ )
			{
				wordFormCountersList.add( new WordCounter( corpora[ i ] ) );
			}
		}
								//	Get works.
		if ( includeWorks )
		{
	        Work[] works	= WorkUtils.getWorks();

								//	Add works to word form counters list.

			for ( int i = 0 ; i < works.length ; i++ )
			{
				wordFormCountersList.add( new WordCounter( works[ i ] ) );
			}
    	}
								//	Get work sets.
		if ( includeWorkSets )
		{
			WorkSet[] workSets	= WorkSetUtils.getWorkSets();

								//	Add work sets to word form counters list.

			for ( int i = 0 ; i < workSets.length ; i++ )
			{
				wordFormCountersList.add( new WordCounter( workSets[ i ] ) );
			}
    	}
								//	Get word sets.
		if ( includeWordSets )
		{
			WordSet[] wordSets	= WordSetUtils.getWordSets();

								//	Add word sets to word form counters list.

			for ( int i = 0 ; i < wordSets.length ; i++ )
			{
				wordFormCountersList.add( new WordCounter( wordSets[ i ] ) );
			}
    	}
								//	Get phrase sets.
		if ( includePhraseSets )
		{
			PhraseSet[] phraseSets	= PhraseSetUtils.getPhraseSets();

								//	Add phrase sets to word form counters list.

			for ( int i = 0 ; i < phraseSets.length ; i++ )
			{
				wordFormCountersList.add( new WordCounter( phraseSets[ i ] ) );
			}
    	}

		WordCounter[] wordFormCounters	= new WordCounter[ 0 ];

		if ( wordFormCountersList.size() > 0 )
		{
			wordFormCounters	=
				(WordCounter[])wordFormCountersList.toArray(
					wordFormCounters );
		}

		return wordFormCounters;
	}

	/**	Get all available word counter types as WordCounter objects.
	 *
	 *	@return			Array of WordCounter objects for all word counter
	 *					types.  Null if none.
	 */

	public static WordCounter[] getWordCounters()
	{
		return getWordCounters( true , true , true , true , true );
	}

	/**	Return all work parts in this object.
	 *
	 *	@return		Array of WorkPart.
	 *
	 *	<p>
	 *	For a work, the returned array has just one entry for the work.
	 *	For a work part, the returned array has just one entry for the work part.
	 *	For a corpus, the returned array usually has more than one entry,
	 *	one for each work in the corpus.  For both Work and Corpus, all
	 *	the returned WorkPart entries are actually Work objects.  For a
	 *	WorkSet, the returned entries may be a combination of Work and
	 *	WorkPart objects.
	 *	</p>
	 */

	public WorkPart[] getWorkParts()
	{
		WorkPart[] result	= null;

		switch ( objectType )
		{
			case CORPUS		:
				result		= CorpusUtils.getWorks( (Corpus)object );
				break;

			case WORDSET	:
				result		= WordSetUtils.getWorkParts( (WordSet)object );
				break;

			case PHRASESET	:
				result		= PhraseSetUtils.getWorkParts( (PhraseSet)object );
				break;

			case WORK		:
				result		= new Work[ 1 ];
				result[ 0 ]	= (Work)object;
//				result		= WorkUtils.getWorkParts ((Work)object );
				break;

			case WORKPART	:
				result		= new WorkPart[ 1 ];
				result[ 0 ]	= (WorkPart)object;
				break;

			case WORKSET	:
				result		= WorkSetUtils.getWorkParts( (WorkSet)object );
				break;
		}

		return result;
	}

	/**	Return all actual work parts in this object.
	 *
	 *	@return		Array of WorkPart.
	 *
	 *	<p>
	 *	This is what getWorkParts should probably do but does not for
	 *	historical reasons.
	 *	</p>
	 *
	 *	$$$PIB$$$	Need to rectify this with getWorkParts.
	 */

	public WorkPart[] getActualWorkParts()
	{
		WorkPart[] result	= null;

		switch ( objectType )
		{
			case CORPUS		:
				result		= CorpusUtils.getWorkParts( (Corpus)object );
				break;

			case WORDSET	:
				result		= WordSetUtils.getWorkParts( (WordSet)object );
				break;

			case PHRASESET	:
				result		= PhraseSetUtils.getWorkParts( (PhraseSet)object );
				break;

			case WORK		:
				result		= WorkUtils.getWorkParts ((Work)object );
				break;

			case WORKPART	:
				result		= new WorkPart[ 1 ];
				result[ 0 ]	= (WorkPart)object;
				break;

			case WORKSET	:
				result		= WorkSetUtils.getWorkParts( (WorkSet)object );
				break;
		}

		return result;
	}

	/**	Return all works represented in this object.
	 *
	 *	@return		Array of Work.
	 */

	public Work[] getWorks()
	{
		Work[] result	= null;

		switch ( objectType )
		{
			case CORPUS		:
				result		= CorpusUtils.getWorks( (Corpus)object );
				break;

			case WORDSET	:
				result		= WordSetUtils.getWorks( (WordSet)object );
				break;

			case PHRASESET	:
				result		= PhraseSetUtils.getWorks( (PhraseSet)object );
				break;

			case WORK		:
				result		= new Work[ 1 ];
				result[ 0 ]	= (Work)object;
				break;

			case WORKPART	:
				result		= new Work[ 1 ];
				result[ 0 ]	= ((WorkPart)object).getWork();
				break;

			case WORKSET	:
				result		= WorkSetUtils.getWorks( (WorkSet)object );
				break;
		}

		return result;
	}

	/**	Get the number of works represented in this word counter.
	 *
	 *	@return		The number of works.
	 */

	public int getWorkCount()
	{
		WorkPart[] workParts	= getWorkParts();

		HashMap workMap			= new HashMap();

		for ( int i = 0 ; i < workParts.length ; i++ )
		{
			WorkPart workPart	= workParts[ i ];

			Long workID			= workPart.getWork().getId();

			if ( !workMap.containsKey( workID ) )
			{
				workMap.put( workID , Long.valueOf( 1 ) );
			}
		}

		return workMap.size();
	}

	/**	Get the number of works represented in this word counter and another.
	 *
	 *	@param		otherCounter	The other counter.
	 *
	 *	@return						The number of works.
	 */

	public int getWorkCount( WordCounter otherCounter )
	{
		WorkPart[] workParts	= getWorkParts();

		HashMap workMap			= new HashMap();

		for ( int i = 0 ; i < workParts.length ; i++ )
		{
			WorkPart workPart	= workParts[ i ];

			Long workID			= workPart.getWork().getId();

			if ( !workMap.containsKey( workID ) )
			{
				workMap.put( workID , Long.valueOf( 1 ) );
			}
		}

		workParts	= otherCounter.getWorkParts();

		for ( int i = 0 ; i < workParts.length ; i++ )
		{
			WorkPart workPart	= workParts[ i ];

			Long workID			= workPart.getWork().getId();

			if ( !workMap.containsKey( workID ) )
			{
				workMap.put( workID , Long.valueOf( 1 ) );
			}
		}

		return workMap.size();
	}

	/**	Get word occurrences for a word.
	 *
	 *	@param	word				The word to look up.
	 *	@param	wordForm    		The word form.
	 *
	 *	@return						Array of Word entries.
	 */

	public Word[] getWordOccurrences
	(
		Spelling word ,
		int wordForm
	)
	{
		Word[] result	= null;

								//	Word counter is work part.
		if ( isWorkPart() )
		{
			WorkPart workPart	= (WorkPart)getObject();

			result				=
				WordUtils.getWordOccurrences( workPart , wordForm , word );
		}
								//	Word counter is work.
		else if ( isWork() )
		{
			Work work	= (Work)getObject();

			result		=
				WordUtils.getWordOccurrences( work , wordForm , word );
		}
								//	Word counter is work set.
		else if ( isWorkSet() )
		{
			WorkSet workSet	= (WorkSet)getObject();
/*
            WorkPart[] workParts	=
            	(WorkPart[])WorkSetUtils.getWorkParts( workSet );

			ArrayList wordOccsList	= new ArrayList();

            for ( int i = 0 ; i < workParts.length ; i++ )
            {
				Word[] wordOccs	=
					WordUtils.getWordOccurrences(
						workParts[ i ] , wordForm , word );

				wordOccsList.addAll( Arrays.asList( wordOccs ) );
            }

			result	= (Word[])wordOccsList.toArray( new Word[]{} );
*/
			result	=
				WordUtils.getWordOccurrences( workSet , wordForm , word );
		}
								//	Word counter is corpus.
		else if ( isCorpus() )
		{
			Corpus corpus			= (Corpus)getObject();
/*
            Work[] works			= (Work[])CorpusUtils.getWorks( corpus );

			ArrayList wordOccsList	= new ArrayList();

            for ( int i = 0 ; i < works.length ; i++ )
            {
				Word[] wordOccs	=
					WordUtils.getWordOccurrences(
						works[ i ] , wordForm , word );

				wordOccsList.addAll( Arrays.asList( wordOccs ) );
            }

			result	=
				(Word[])wordOccsList.toArray(
					new Word[]{} );
*/
			result	=
				WordUtils.getWordOccurrences( corpus , wordForm , word );
		}
								//	Word counter is word set.

		else if ( isWordSet() )
		{
			WordSet wordSet	= (WordSet)getObject();

			result	=
				(Word[])WordSetUtils.getWordOccurrences(
					wordSet , wordForm , word );
		}
								//	Word counter is phrase set.

		else if ( isPhraseSet() )
		{
			PhraseSet phraseSet	= (PhraseSet)getObject();

			result	=
				(Word[])PhraseSetUtils.getWordOccurrences(
					phraseSet , wordForm , word );
		}

		return result;
	}

	/**	Get surrounding words of a specified word.
	 *
	 *	@param	word				Word for which to get span.
	 *	@param	leftSpan			# of words to left of
	 *								specified word to retrieve.
	 *	@param	rightSpan			# of words to right of
	 *								specified word to retrieve.
	 *
 	 *	@return						Span of words around
	 *								specified word.
	 */

	public Word[] getSpan
	(
		Word word ,
		int leftSpan ,
		int rightSpan
	)
	{
		if ( isWordSet() )
		{
			return WordSetUtils.getSpan
			(
				(WordSet)getObject() ,
				word ,
				leftSpan ,
				rightSpan
			);
		}
		else if ( isPhraseSet() )
		{
			return PhraseSetUtils.getSpan
			(
				(PhraseSet)getObject() ,
				word ,
				leftSpan ,
				rightSpan
			);
		}
		else
		{
			return WordUtils.getSpan
			(
				word ,
				leftSpan ,
				rightSpan
			);
		}
	}

	/**	HTMLize an object title.
	 *
	 *	@param	title	The object title.
	 *
	 *	@return			The HTMLized title.
	 */

	protected String htmlizeTitle( String title )
	{
		String result	= title;

								//	Add owner in parenthesis.

		if ( object instanceof UserDataObject )
		{
			String owner	=
				StringUtils.safeString(
					((UserDataObject)object).getOwner() );

			if ( owner.length() > 0 )
			{
				result	= result + " (" + owner + ")";
			}
		}
								//	Add object-specific formatting.

		switch ( objectType )
		{
			case CORPUS		:
				result	= "<strong>" + result + "</strong>";
				break;

			case PHRASESET	:
			case WORDSET	:
				result	= "<em>" + result + "</em>";
				break;

			case WORK		:
			case WORKPART	:
				break;

			case WORKSET	:
				result	= "<strong><em>" + result + "</em></strong>";
				break;
		}

		result	= "<html>" + result + "</html>";

		return result;
	}

	/**	Return HTML string form of object.
	 *
	 *	@param		useShortName	True to use short object name.
	 *
	 *	@return		HTML string form (=long or short title) of object.
	 */

	public String toHTMLString( boolean useShortName )
	{
		return htmlizeTitle( toString( useShortName ) );
	}

	/**	Return HTML string form of object.
	 *
	 *	@return		HTML string form (=title) of object.
	 */

	public String toHTMLString()
	{
		return htmlizeTitle( toString() );
	}

	/**	Return string form of object.
	 *
	 *	@return		String form (=title) of object.
	 */

	public String toString()
	{
		String result	= "";

		if ( object != null )
		{
			result	= object.toString();
		}

		return result;
	}

	/**	Return string form of object.
	 *
	 *	@param		useShortString	Return short string.
	 *
	 *	@return		String form of object.
	 *
	 *	<p>
	 *	At present, we only return short strings if requested
	 *	for Work objects.
	 *	</p>
	 */

	public String toString( boolean useShortString )
	{
		String result	= "";

		if ( object != null )
		{
			if ( isWork() )
			{
				if ( useShortString )
				{
					result	= ((WorkPart)object).getShortTitle();
				}
				else
				{
					result	= ((WorkPart)object).getFullTitle();
				}
			}
			else
			{
				result	= object.toString();
			}
		}

		return result;
	}

	/**	Compare another word form object to this one for equality.
	 *
	 *	@param	object	The other word form object.
	 *
	 *	<p>
	 *	Two WordCounter objects are equal if they wrap objects with the
	 *	same id.
	 *	</p>
	 */

	public boolean equals( Object object )
	{
		return this.object.equals(
			((WordCounter)object).getObject() );
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code, based upon the id of the wrapped object.
	 */

	public int hashCode()
	{
		return ((PersistentObject)object).getId().hashCode();
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


