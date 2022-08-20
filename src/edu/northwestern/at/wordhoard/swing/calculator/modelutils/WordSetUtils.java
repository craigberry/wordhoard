package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.db.mysql.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.swing.calculator.cql.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;

/**	Word set utilities.
 *
 *	<p>
 *	Methods for adding, deleting, and updating word sets.  Word sets are
 *	unstructured collections of words which may span many different
 *	corpora, works, and work parts.
 *	</p>
 *
 *	<p>
 *	To add a word set, call {@link #addWordSetUsingQuery addWordSetUsingQuery}.
 *	The protected versions of addWordSet are used to perform portions of the
 *	process and should not be used on their own.  Word sets are marked as
 *	owned by the currently logged-in user as stored in
 *	{@link WordHoardSettings}.
 *	</p>
 */

public class WordSetUtils implements Serializable
{
	/**	Serial version UID. */

	protected static final long serialVersionUID	= -8111068868527566772L;
//	protected static final long serialVersionUID	= 8993514347836319525L;

								//	Set true to enable debugging here.

	private static boolean debug	= false;

	/**	Copy UserDataObject array to WordSet array.
	 *
	 *	@param	udos	Array of user data objects, all actually
	 *					WordSet objects.
	 *
	 *	@return			Array of WordSet objects.
	 */

	protected static WordSet[] udosToWordSets( UserDataObject[] udos )
	{
		WordSet[] result;

		if ( udos != null )
		{
			result	= new WordSet[ udos.length ];

			for ( int i = 0 ; i < udos.length ; i++ )
			{
				result[ i ]	= (WordSet)udos[ i ];
			}
		}
		else
		{
			result	= new WordSet[ 0 ];
		}

		return result;
	}

	/**	Perform batch of inserts.
	 *
	 *	@param	inserts		String array of database insert requests.
	 *
	 *	@return				Number of objects inserted.
	 */

	protected static int performBatchInserts( String[] inserts )
	{
		int result				= 0;
		PersistenceManager pm	= null;

		try
		{
			if ( WordHoardSettings.getBuildProgramRunning() )
			{
				pm	= PMUtils.getPM();

				if ( pm != null )
				{
					result	= pm.performBatchInserts( inserts );
				}
			}
			else
			{
				result	=
					WordHoard.getSession().performBatchInserts( inserts );
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			result	= 0;
		}
		finally
		{
//			PMUtils.closePM( pm );
		}

		return result;
	}

	/**	Persist counts for word set map.
	 *
	 *	@param	wordSet				The word set.
	 *	@param	wordForm			The word form type.
	 *	@param	wordCountMap		The word count map.
	 *	@param	totalWordCountMap	The total word count map.
	 *	@param	persistingPhrases	True if the counts are for phrases.
	 *	@param	progressReporter	The progress display.  May be null.
	 *
	 *	@return						True if all counts persisted.
	 */

	protected static boolean persistCounts
	(
		WordSet wordSet ,
		int wordForm ,
		Map wordCountMap ,
		Map totalWordCountMap ,
		boolean persistingPhrases ,
		ProgressReporter progressReporter
	)
	{
		boolean result			= false;

		long startTime			= System.currentTimeMillis();

								//	Determine if we're persisting
								//	individual word data or phrase data.

		persistingPhrases		= wordSet instanceof PhraseSet;

								//	MySQL insert statement generator
								//	for word set word counts.

		MySQLInsertGenerator countGenerator	=
			new MySQLInsertGenerator
			(
				persistingPhrases ?
					"wordhoard.phrasesetphrasecount" : "wordhoard.wordsetwordcount" ,
				new String[]
				{
					"word_string" ,
					"word_charset" ,
					"wordForm" ,
					"wordSet" ,
					"workPartTag" ,
					"wordCount"
				} ,
				new boolean[]
				{
					false ,
					true ,
					true ,
					true ,
					false ,
					true
				}
			);
								//	MySQL insert statement generator
								//	for word set total word counts.

		MySQLInsertGenerator totalCountGenerator	=
			new MySQLInsertGenerator
			(
				persistingPhrases ?
					"wordhoard.phrasesettotalwordformcount" : "wordhoard.wordsettotalwordformcount" ,
				new String[]
				{
					"wordForm" ,
					"wordSet" ,
					"workPartTag" ,
					"wordFormCount"
				} ,
				new boolean[]
				{
					true ,
					true ,
					false ,
					true
				}
			);

		Integer wordFormInt	= new Integer( wordForm );
		Long wordSetID		= wordSet.getId();

								//	Iterate over the list of
								//	works in the word count map.

		Iterator workIterator	= wordCountMap.keySet().iterator();

		while ( workIterator.hasNext() )
		{
			Long workID			= (Long)workIterator.next();

			String workPartTag	=
				WorkUtils.getWorkPartTagById( workID );

								//	Get word count map for this work.

			Map wordMap			=
				(Map)wordCountMap.get( workID );

								//	Loop over the words in this
								//	word count map and create
								//	word set word count objects.

			Iterator iterator	= wordMap.keySet().iterator();

			while ( iterator.hasNext() )
			{
				Spelling word		= (Spelling)iterator.next();
				Integer wordCount	= (Integer)wordMap.get( word );

				countGenerator.addRow
				(
					new Object[]
					{
						word.getString() ,
						new Byte( word.getCharset() ) ,
						wordFormInt ,
						wordSetID ,
						workPartTag ,
						wordCount
					}
				);
			}
        }
								//	Get MySQL insert for word counts.

		String insertCountsSQL	= countGenerator.getInsert();

								//	Iterate over the works in
								//	the total word count map.

		workIterator			= totalWordCountMap.keySet().iterator();

		while ( workIterator.hasNext() )
		{
			Long workID			= (Long)workIterator.next();
			Integer totalCount	= (Integer)totalWordCountMap.get( workID );
			String workPartTag	=
				WorkUtils.getWorkPartTagById( workID );

								//	Create word set total word form count
								//	object.

			totalCountGenerator.addRow
			(
				new Object[]
				{
					wordFormInt ,
					wordSetID ,
					workPartTag ,
					totalCount
				}
			);
        }
								//	Get MySQL insert for total word counts.

		String insertTotalCountsSQL	= totalCountGenerator.getInsert();

		boolean cancelled	= false;
		int insertCount		= 0;
		int currentProgress	= 0;

								//	Send inserts to server for execution.
		try
		{
			if ( progressReporter != null )
			{
				currentProgress	= progressReporter.getCurrentBarValue();
				cancelled 		= cancelled || progressReporter.isCancelled();

				if ( debug )
				{
					System.out.println( "" );
					System.out.println(
						"persistCounts: (1) currentProgress=" +
						currentProgress +
						", maxBarValue=" +
						progressReporter.getMaximumBarValue() );
				}
			}
								//	Insert the counts.

			if ( !cancelled )
			{
				insertCount	=
					performBatchInserts
					(
						new String[]
						{
							insertCountsSQL
						}
					);
								//	Update the progress display.

				if ( progressReporter != null )
				{
					currentProgress	=
						progressReporter.getCurrentBarValue() + insertCount;

					progressReporter.updateProgress( currentProgress );

					cancelled 	= cancelled || progressReporter.isCancelled();

					if ( debug )
					{
						System.out.println(
							"persistCounts: (2) currentProgress=" +
								currentProgress +
							", maxBarValue=" +
								progressReporter.getMaximumBarValue() +
							", insertCount=" + insertCount );
					}
				}
			}
								//	Insert the total counts.

			if ( !cancelled )
			{
				insertCount	=
					performBatchInserts
					(
						new String[]
						{
							insertTotalCountsSQL ,
						}
					);
								//	Update the progress display.

				if ( progressReporter != null )
				{
					currentProgress	=
						progressReporter.getCurrentBarValue() + insertCount;

					progressReporter.updateProgress( currentProgress );

					cancelled 	= cancelled || progressReporter.isCancelled();

					if ( debug )
					{
						System.out.println(
							"persistCounts: (3) currentProgress=" +
								currentProgress +
							", maxBarValue=" +
								progressReporter.getMaximumBarValue() +
							", insertCount=" + insertCount );
					}
				}
			}

			result = !cancelled;
		}
		catch ( Exception e )
		{
			deleteWordSet( wordSet );
			wordSet = null;
			Err.err( e );
		}

		if ( !result )
		{
			deleteWordSet( wordSet );
			wordSet = null;
		}

		long endTime	= System.currentTimeMillis() - startTime;

		return result;
	}

	/**	Create count entries for a word set.
	 *
	 *	@param	wordCounter			Countable word data counter.
	 *
	 *	@return						Three element object array.
	 *								First element is word count maps.
	 *								Second element is total count maps.
	 *								Third element is total number of entries
	 *								in all maps.
	 */

	public static Object[] createCounts
	(
		CountableWordDataCounter wordCounter
	)
	{
		Map[] wordCountMaps		= wordCounter.getWordCountMaps();
		Map[] totalCountMaps	= wordCounter.getTotalWordCountMaps();

								//	Determine number of entries in all
								//	count maps.
		int nCounts	= 0;

		if ( debug )
		{
			System.out.println(
				"createCounts: there are " + wordCountMaps.length +
				" word maps." );
		}

		for ( int i = 0 ; i < wordCountMaps.length ; i++ )
		{
			Map map	= wordCountMaps[ i ];

			if ( map != null )
			{
				Iterator iterator	= map.keySet().iterator();

				while ( iterator.hasNext() )
				{
					Long id		=	(Long)iterator.next();
					Map map2	=	(Map)map.get( id );
					nCounts		+=	map2.keySet().size();

					if ( debug )
					{
						System.out.println(
							"createCounts: word map " + i + " has " +
							map2.keySet().size() + " entries." );
					}
				}
			}
		}

        if ( debug )
        {
			System.out.println(
				"createCounts: there are " + totalCountMaps.length +
				" total count maps." );
        }

		for ( int i = 0 ; i < totalCountMaps.length ; i++ )
		{
			Map map	= totalCountMaps[ i ];

			if ( map != null )
			{
				nCounts	+=	map.keySet().size();

				if ( debug )
				{
					System.out.println(
						"createCounts: total count map " + i + " has " +
						map.keySet().size() + " entries." );
				}
			}
		}

		if ( debug )
		{
			System.out.println(
				"countMaps: total counts in all maps is " + nCounts );
		}
								//	Return results.
		return
			new Object[]
			{
				wordCountMaps ,
				totalCountMaps ,
				new Integer( nCounts )
			};
	}

	/**	Persist word set counts.
	 *
	 *	@param	wordSet				The word set.
	 *	@param	wordCountMaps		The word count maps.
	 *	@param	totalCountMaps		The total count maps.
	 *	@param	persistingPhrases	true if persisting phrase counts.
	 *	@param	progressReporter	The progress display to update.
	 *
	 *	@return						true if all counts persisted.
	 */

	 protected static boolean persistWordSetCounts
	 (
	 	WordSet wordSet ,
		Map[] wordCountMaps ,
		Map[] totalCountMaps ,
		boolean persistingPhrases ,
		ProgressReporter progressReporter
	 )
	 {
								//	Persist counts.
		boolean result	=
			persistCounts
			(
				wordSet ,
				WordForms.SPELLING  ,
				wordCountMaps[ WordForms.SPELLING ] ,
				totalCountMaps[ WordForms.SPELLING ] ,
				persistingPhrases ,
				progressReporter
			);

		if ( !result ) return result;

		result	=
			persistCounts
			(
				wordSet ,
				WordForms.LEMMA ,
				wordCountMaps[ WordForms.LEMMA ] ,
				totalCountMaps[ WordForms.LEMMA ] ,
				persistingPhrases ,
				progressReporter
			);

		if ( !result ) return result;

		result	=
			persistCounts
			(
				wordSet ,
				WordForms.WORDCLASS ,
				wordCountMaps[ WordForms.WORDCLASS ] ,
				totalCountMaps[ WordForms.WORDCLASS ] ,
				persistingPhrases ,
				progressReporter
			);

		if ( !result ) return result;

		result	=
			persistCounts
			(
				wordSet ,
				WordForms.SPEAKERGENDER ,
				wordCountMaps[ WordForms.SPEAKERGENDER ] ,
				totalCountMaps[ WordForms.SPEAKERGENDER ] ,
				persistingPhrases ,
				progressReporter
			);

		if ( !result ) return result;

		result	=
			persistCounts
			(
				wordSet ,
				WordForms.SPEAKERMORTALITY ,
				wordCountMaps[ WordForms.SPEAKERMORTALITY ] ,
				totalCountMaps[ WordForms.SPEAKERMORTALITY ] ,
				persistingPhrases ,
				progressReporter
			);

		if ( !result ) return result;

		result	=
			persistCounts
			(
				wordSet ,
				WordForms.ISVERSE ,
				wordCountMaps[ WordForms.ISVERSE ] ,
				totalCountMaps[ WordForms.ISVERSE ] ,
				persistingPhrases ,
				progressReporter
			);

		if ( !result ) return result;

		result	=
			persistCounts
			(
				wordSet ,
				WordForms.METRICALSHAPE ,
				wordCountMaps[ WordForms.METRICALSHAPE ] ,
				totalCountMaps[ WordForms.METRICALSHAPE ] ,
				persistingPhrases ,
				progressReporter
			);

		return result;
	}

	/**	Add a new word set with specified words.
	 *
	 *	@param	title				Title for the new word set.
	 *	@param	description			Description for the new word set.
	 *	@param	webPageURL			Web page URL for the new word set.
	 *	@param	isPublic			True if word set to be public.
	 *	@param	query				Query text which generated the words.
	 *	@param	words				Array of CountableWordData entries to add.
	 *	@param	progressReporter	Progress reporter.
	 *
	 *	@return						WordSet object if word set added, else null.
	 *
	 *	@throws						DuplicateWordSetException if (title,owner)
	 *								combination already exists.
	 *
	 *	@throws						BadOwnerException if the owner null or empty.
	 */

	protected static WordSet addWordSet
	(
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		String query ,
		Map words ,
		ProgressReporter progressReporter
	)
		throws DuplicateWordSetException, BadOwnerException
	{
								//	Quit right away if not logged in.

		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException
			(
				WordHoardSettings.getString
				(
					"Notloggedin" ,
					"Not logged in"
				)
			);
		}
								//	Create new word set.

		WordSet wordSet	=
			addWordSet
			(
				title ,
				description ,
				webPageURL ,
				isPublic ,
				query
			);
								//	If new word set successfully created,
								//	add the words to it.

		if ( wordSet != null )
		{
			if ( addWords( wordSet , words , progressReporter ) )
			{
								//	Set the word set active.
				wordSet	=
					(WordSet)PersistenceManager.doLoad(
						WordSet.class , wordSet.getId() );

				UserDataObjectUtils.updateUserDataObject
				(
					wordSet ,
					new UserDataObjectUpdater()
					{
						public void update
						(
							UserDataObject userDataObject
						)
						{
							WordSet wordSet	= (WordSet)userDataObject;

							wordSet.setIsActive( true );
						}
					}
				);
			}
								//	If we could not add words,
								//	delete the word set.
			else
			{
				deleteWordSet( wordSet );
				wordSet	= null;
			}
		}
								//	Return new WordSet object to caller.
		return wordSet;
	}

	/**	Add a new word set.
	 *
	 *	@param	title		Title for the new word set.
	 *	@param	description	The description.
	 *	@param	webPageURL	The web page URL.
	 *	@param	isPublic	True if word set is to be public.
	 *	@param	query		The query to generate the words.
	 *
	 *	@return				WordSet object if word set added, else null.
	 *
	 *	@throws				DuplicateWordSetException if (title,owner)
	 *						combination already exists.
	 *
	 *	@throws				BadOwnerException if the owner null or empty.
	 */

	protected static WordSet addWordSet
	(
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		String query
	)
		throws DuplicateWordSetException, BadOwnerException
	{
								//	Quit right away if not logged in.

		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException
			(
				WordHoardSettings.getString
				(
					"Notloggedin" ,
					"Not logged in"
				)
			);
		}
								//	Check if word set of this title already
								//	exists.

		String owner	= WordHoardSettings.getUserID();

		WordSet wordSet	= getWordSet( title , owner );

								//	Report error if so.

		if ( wordSet != null )
		{
			throw new DuplicateWordSetException();
		}
								//	Create new word set object with
								//	specified title.
		wordSet	=
			new WordSet
			(
				title ,
				description ,
				webPageURL ,
				owner ,
				isPublic ,
				query
			);
								//	Persist the word set object.
		Long id	=
			UserDataObjectUtils.createUserDataObject( wordSet );

		if ( id.longValue() != -1 )
		{
			wordSet	=
				(WordSet)PersistenceManager.doLoad( WordSet.class , id );
		}
		else
		{
			wordSet	= null;
		}
								//	Return the newly persisted
								//	word set object.
		return wordSet;
	}

	/**	Add a new word set.
	 *
	 *	@param	title				Title for the new word set.
	 *	@param	description			The description.
	 *	@param	webPageURL			The web page URL.
	 *	@param	isPublic			True if word set is to be public.
	 *	@param	query				The query used to generate the words.
	 *	@param	words				Collection of words for the word set.
	 *	@param	progressReporter	If not null, used to display progress.
	 *
	 *	@return						WordSet object if word set added, else null.
	 *
	 *	@throws						DuplicateWordSetException if (title,owner)
	 *								combination already exists.
	 *
	 *	@throws						BadOwnerException if the owner null or empty.
	 */

	public static WordSet addWordSet
	(
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		String query ,
		Collection words ,
		ProgressReporter progressReporter
	)
		throws DuplicateWordSetException, BadOwnerException
	{
								//	Quit right away if not logged in.

		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException
			(
				WordHoardSettings.getString
				(
					"Notloggedin" ,
					"Not logged in"
				)
			);
		}
								//	Set progress display.

		if ( progressReporter != null )
		{
			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Calculatingcounts" ,
					"Calculating counts ..."
				)
            );

			progressReporter.setIndeterminate( true );
			progressReporter.makeVisible( true );
		}
								//	Create a word data counter and count
								//	the word parts.

		CountableWordDataCounter wordDataCounter	=
			new CountableWordDataCounter();

		PreloadUtils.preloadReducedWordPartsAndSpeeches( words );

		wordDataCounter.countWordParts( words , true , progressReporter );

								//	Create the word set with the
								//	specified words and counts.
		return
			addWordSet
			(
				title ,
				description ,
				webPageURL ,
				isPublic ,
				query ,
				wordDataCounter ,
				progressReporter
			);
	}

	/**	Add a new word set.
	 *
	 *	@param	title				Title for the new word set.
	 *	@param	description			The description.
	 *	@param	webPageURL			The web page URL.
	 *	@param	isPublic			True if word set is to be public.
	 *	@param	query				The query used to generate the words.
	 *	@param	words				Array of words for the word set.
	 *	@param	progressReporter	If not null, used to display progress.
	 *
	 *	@return						WordSet object if word set added, else null.
	 *
	 *	@throws						DuplicateWordSetException if (title,owner)
	 *								combination already exists.
	 *
	 *	@throws						BadOwnerException if the owner null or empty.
	 */

	public static WordSet addWordSet
	(
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		String query ,
		Word[] words ,
		ProgressReporter progressReporter
	)
		throws DuplicateWordSetException, BadOwnerException
	{
		return addWordSet
		(
			title ,
			description ,
			webPageURL ,
			isPublic ,
			query ,
			Arrays.asList( words ) ,
			progressReporter
		);
	}

	/**	Add a new word set using a specified query.
	 *
	 *	@param	title				Title for the new word set.
	 *	@param	description			Description for the new word set.
	 *	@param	webPageURL			Web page URL for the new word set.
	 *	@param	isPublic			True if word set to be public.
	 *	@param	analysisText		Text set from which to extract words.
	 *	@param	query				Query to select words in analysis text.
	 *	@param	parentWindow		Parent window.
	 *	@param	progressReporter	If not null, used to display progress.
	 *
	 *	@return						WordSet object if word set added, else null.
	 *
	 *	@throws						DuplicateWordSetException if (title,owner)
	 *								combination already exists.
	 *
	 *	@throws						BadOwnerException if the owner null or empty.
	 */

	public static WordSet addWordSetUsingQuery
	(
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		WordCounter analysisText ,
		String query ,
		Window parentWindow ,
		ProgressReporter progressReporter
	)
		throws DuplicateWordSetException, BadOwnerException
	{
								//	Quit right away if not logged in.

		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException
			(
				WordHoardSettings.getString
				(
					"Notloggedin" ,
					"Not logged in"
				)
			);
		}
								//	Set progress display.

		if ( progressReporter != null )
		{
			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Retrievingworks" ,
					"Retrieving works"
				)
            );

			progressReporter.setIndeterminate( true );
			progressReporter.makeVisible( true );
		}
								//	Get work parts containing
								//	candidate words for word set.

		Collection workParts	=
			WorkUtils.expandWorkParts( analysisText.getWorkParts() );

								//	Get words for word set.

		if ( progressReporter != null )
		{
			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Retrievingwords" ,
					"Retrieving words"
				)
			);
		}

		CQLQuery wordQuery		= new CQLQuery( query );

		String[] hqlQueries		=
			wordQuery.getHQL(
				"word.workPart in (:workParts)" , CQLQuery.WORDRESULTS );

		for ( int i = 0 ; i < hqlQueries.length ; i++ )
		{
			if ( debug )
			{
				System.out.println( "HQL" + i + ": " + hqlQueries[ i ] );
			}
		}
								//	Create a word data counter.

		CountableWordDataCounter wordDataCounter	=
			new CountableWordDataCounter();

								//	Run each query and accumulate the
								//	word counts.

		int nQueries	= hqlQueries.length;

		for ( int i = 0 ; i < nQueries ; i++ )
		{
/*
			HibernateScrollIterator wordListIterator	=
				new HibernateScrollIterator
				(
					PersistenceManager.doScrollableQuery
					(
						hqlQueries[ i ] ,
						new String[]{ "workParts" } ,
						new Object[]{ workParts }
					)
            	);

			wordDataCounter.countWordParts
			(
				wordListIterator ,
				( i >= ( nQueries - 1 ) ) ,
				progressReporter
			);

			wordListIterator.close();
*/
			java.util.List wordList		=
				PersistenceManager.doQuery
				(
					hqlQueries[ i ] ,
					new String[]{ "workParts" } ,
					new Object[]{ workParts }
				);

			Iterator wordListIterator	= wordList.iterator();

			wordDataCounter.countWordParts
			(
				wordListIterator ,
				( i >= ( nQueries - 1 ) ) ,
				progressReporter
			);

            wordList	= null;

			System.gc();
		}

		return
			addWordSet
			(
				title ,
				description ,
				webPageURL ,
				isPublic ,
				query ,
				wordDataCounter ,
				progressReporter
			);
	}

	/**	Create word set given words and counts.
	 *
	 *	@param	title				Title for the new word set.
	 *	@param	description			Description for the new word set.
	 *	@param	webPageURL			Web page URL for the new word set.
	 *	@param	isPublic			True if word set to be public.
	 *	@param	query				Query to select words in analysis text.
	 *	@param	wordDataCounter		Word data counter.
	 *	@param	progressReporter	If not null, used to display progress.
	 *
	 *	@return						WordSet object if word set added, else null.
	 *
	 *	@throws						DuplicateWordSetException if (title,owner)
	 *								combination already exists.
	 *
	 *	@throws						BadOwnerException if the owner null or empty.
	 */

	protected static WordSet addWordSet
	(
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		String query ,
		CountableWordDataCounter wordDataCounter ,
		ProgressReporter progressReporter
	)
		throws DuplicateWordSetException, BadOwnerException
	{
								//	Quit right away if not logged in.

		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException
			(
				WordHoardSettings.getString
				(
					"Notloggedin" ,
					"Not logged in"
				)
			);
		}
								//	Count the words.

		Object[] counts	= createCounts( wordDataCounter );

								//	Create word set.  Leave it inactive
								//	until word counts are added below.

		if ( progressReporter != null )
		{
			PrintfFormat savingWordSetFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"Savingwordsetcontainingnwords" ,
						"Saving word set containing %s words"
					)
				);

			String savingWordSetMessage	=
				savingWordSetFormat.sprintf
				(
					new Object[]
					{
						StringUtils.formatNumberWithCommas
						(
							wordDataCounter.getWordsCounted()
						)
					}
				);
                                //	Set maximum value for progress bar
                                //	to sum of number of words to add
                                //	and number of word count objects
                                //	to add.

			progressReporter.setMaximumBarValue
			(
				wordDataCounter.getWordsCounted() +
				wordDataCounter.getWorksCounted() +
				wordDataCounter.getWorkPartsCounted() +
				((Integer)counts[ 2 ]).intValue()
			);

			if ( debug )
			{
				System.out.println(
					"addWordSet: words counted=" +
					wordDataCounter.getWordsCounted() +
					", count entries=" + ((Integer)counts[ 2 ]).intValue() );
			}

			progressReporter.updateProgress
			(
				0 ,
				savingWordSetMessage
			);

			progressReporter.setIndeterminate( false );
		}
								//	Add the words.
		WordSet wordSet	=
			addWordSet
			(
				title ,
				description ,
				webPageURL ,
				false ,
				query ,
				wordDataCounter.getCountableWordDataMap() ,
				progressReporter
			);
								//	Add counts to database for word set
								//	words if we successfully added
								//	the words.

		if ( wordSet != null )
		{
								//	Persist word counts for word set.
								//	Delete the word set if we can't.

			if	(	!persistWordSetCounts
					(
						wordSet ,
						(Map[])counts[ 0 ] ,
						(Map[])counts[ 1 ] ,
						false ,
						progressReporter
					)
				)
			{
				deleteWordSet( wordSet );
				wordSet	= null;
			}
		}
								//	Make the word set public if requested.

		if ( ( wordSet != null ) && isPublic )
		{
			UserDataObjectUtils.updateUserDataObject
			(
				wordSet ,
				new UserDataObjectUpdater()
				{
					public void update
					(
						UserDataObject userDataObject
					)
					{
						WordSet wordSet	= (WordSet)userDataObject;

						wordSet.setIsPublic( true );
					}
				}
			);
		}
								//	Release the word counter.

		wordDataCounter	= null;
		System.gc();

								//	Return new WordSet object to caller.
		return wordSet;
	}

	/**	Add words to a word set.
	 *
	 *	@param	wordSet				The word set.
	 *	@param	words				Map with words to add to word set.
	 *								Map entries are CountableWordData objects.
	 *	@param	progressReporter	Progress display to update.  May be null.
	 *
	 *	@return						true if words added successfully, else false.
	 *
	 *	<p>
	 *	If this method returns false, the caller should delete the
	 *	word set.
	 *	</p>
	 */

	protected static boolean addWords
	(
		WordSet wordSet ,
		Map words ,
		ProgressReporter progressReporter
	)
	{
								//	Get words, works, and work parts
								//	from CountableWordData entries.

		HashSet works			= new HashSet();
		HashSet workParts		= new HashSet();
		ArrayList wordTagsList	= new ArrayList();

		for	(	Iterator iterator = words.keySet().iterator() ;
				iterator.hasNext() ; )
		{
			Long wordPartOrdinal		= (Long)iterator.next();

			CountableWordData wordData	=
				(CountableWordData)words.get( wordPartOrdinal );

			if ( wordData.getWordPartIndex() != 0 ) continue;

			wordTagsList.add( wordData.getWordTag() );
			works.add( wordData.getWorkId() );
			workParts.add( wordData.getWorkPartId() );
		}
								//	Get word tags to array.

		String[] wordTags		=
			(String[])wordTagsList.toArray( new String[]{} );

								//	Get work tags.

		String[] workTags		= WorkUtils.getWorkTagsById( works );

								//	Get work part tags.

		String[] workPartTags	= WorkUtils.getWorkPartTagsById( workParts );

								//	Add the words.
		return addWords
		(
			wordSet ,
			wordTags ,
			workTags ,
			workPartTags ,
			progressReporter
		);
	}

	/**	Add words to a word set.
	 *
	 *	@param	wordSet				The word set.
	 *	@param	wordTags			Array of word tags.
	 *	@param	workTags			Array of work tags.
	 *	@param	workPartTags		Array of work part tags.
	 *	@param	progressReporter	Progress display to update.  May be null.
	 *
	 *	@return						true if words added successfully, else false.
	 *
	 *	<p>
	 *	If this method returns false, the caller should delete the
	 *	word set.
	 *	</p>
	 */

	protected static boolean addWords
	(
		WordSet wordSet ,
		String[] wordTags ,
		String[] workTags ,
		String[] workPartTags ,
		ProgressReporter progressReporter
	)
	{
		boolean result			= false;

		Long wordSetID			= wordSet.getId();

								//	MySQL insert statement generator
								//	for word set word tags.

		MySQLInsertGenerator wordTagsGenerator	=
			new MySQLInsertGenerator
			(
				"wordhoard.wordset_wordtags" ,
				new String[]
				{
					"wordSet" ,
					"wordTag"
				} ,
				new boolean[]
				{
					true ,
					false
				}
			);
								//	MySQL insert statement generator
								//	for word set work tags.

		MySQLInsertGenerator workTagsGenerator	=
			new MySQLInsertGenerator
			(
				"wordhoard.wordset_worktags" ,
				new String[]
				{
					"wordSet" ,
					"tag"
				} ,
				new boolean[]
				{
					true ,
					false
				}
			);
								//	MySQL insert statement generator
								//	for word set work part tags.

		MySQLInsertGenerator workPartTagsGenerator	=
			new MySQLInsertGenerator
			(
				"wordhoard.wordset_workparttags" ,
				new String[]
				{
					"wordSet" ,
					"tag"
				} ,
				new boolean[]
				{
					true ,
					false
				}
			);
								//	Create MySQL insert statements for
								//	word tags, work tags, and work part tags.

		for	( int i = 0 ; i < wordTags.length ; i++ )
		{
			wordTagsGenerator.addRow
			(
				new Object[]{ wordSetID , wordTags[ i ] }
			);
		}
								//	Create MySQL insert statements for
								//	work tags.

		for	( int i = 0 ; i < workTags.length ; i++ )
		{
			workTagsGenerator.addRow
			(
				new Object[]{ wordSetID , workTags[ i ] }
			);
		}
								//	Create MySQL insert statements for
								//	work part tags.

		for	( int i = 0 ; i < workPartTags.length ; i++ )
		{
			workPartTagsGenerator.addRow
			(
				new Object[]{ wordSetID , workPartTags[ i ] }
			);
		}
								//	Send inserts to server for execution.

		boolean cancelled	= false;

		int currentProgress	= 0;

		if ( progressReporter != null )
		{
			currentProgress	= progressReporter.getCurrentBarValue();

			if ( debug )
			{
				System.out.println(
					"addWords: (1) currentProgress=" + currentProgress +
					", maxBarValue=" +
					progressReporter.getMaximumBarValue() );
			}
		}

		try
		{
			String[] inserts = new String[]
			{
				wordTagsGenerator.getInsert() ,
				workPartTagsGenerator.getInsert() ,
				workTagsGenerator.getInsert()
			};

			currentProgress	+= performBatchInserts( inserts );

								//	Update the progress display.

			if ( progressReporter != null )
			{
				progressReporter.updateProgress( currentProgress );
				cancelled	= cancelled || progressReporter.isCancelled();

				if ( debug )
				{
					System.out.println(
						"addWords: (2) currentProgress=" +
							currentProgress +
						", maxBarValue=" +
							progressReporter.getMaximumBarValue() );
				}
			}

			result	= !cancelled;
		}
		catch ( Exception e )
		{
			Err.err( e );
		}
								//	Refresh word set object.

		if ( wordSet != null )
		{
			PersistenceManager pm	= PMUtils.getPM();

			if ( pm != null )
			{
				pm.refresh( wordSet );
			}
        }
								//	If we could not add all the words,
								//	either because of an error or because
								//	the user cancelled the operation,
								//	remove all the words.
								//
								//	$$$PIB$$$	Should change to only
								//	remove the words we tried to add at
								//	some point.
		if ( !result )
		{
			if ( progressReporter != null )
			{
				progressReporter.updateProgress
				(
					WordHoardSettings.getString
					(
						"Cancellingpleasewait" ,
						"Cancelling, please wait ..."
					)
				);

				UserDataObjectUtils.updateUserDataObject
				(
					wordSet ,
					new UserDataObjectUpdater()
					{
						public void update
						(
							UserDataObject userDataObject
						)
						{
							WordSet wordSet	= (WordSet)userDataObject;

								//	Remove all the words.

							wordSet.removeWords();
						}
					}
				);
			}
		}

		return result;
	}

	/**	Delete a word set.
	 *
	 *	@param	wordSet	The word set to delete.
	 *
	 *	@return			true if word set deleted, false otherwise.
	 */

	public static boolean deleteWordSet( WordSet wordSet )
	{
		boolean result	= false;

		try
		{
			if ( WordHoardSettings.getBuildProgramRunning() )
			{
				PersistenceManager pm	= PMUtils.getPM();

				if ( pm != null )
				{
					String sid	= wordSet.getId().toString();

					try
					{
						pm.evict( wordSet );

						pm.begin();

						pm.deleteViaQuery
						(
							"delete WordSetWordCount w where w.wordSet=" +
							sid
						);

						pm.deleteViaQuery
						(
							"delete WordSetTotalWordFormCount w " +
							"where w.wordSet=" + sid
						);

						pm.deleteViaQuery
						(
							"delete WordSet w where w.id=" + sid
						);

						pm.commit();

						result	= true;
					}
					catch ( PersistenceException e2 )
					{
						try
						{
							pm.rollback();
						}
						catch ( Exception e3 )
						{
						}
					}
					finally
					{
//						PMUtils.closePM( pm );
					}
				}
			}
			else
			{
				WordHoard.getSession().deleteWordSet( wordSet );
				result	= true;
			}
		}
		catch ( Exception e )
		{
		}

		return result;
	}

	/**	Delete a word set by title.
	 *
	 *	@param	title	The title of the word set to delete.
	 *
	 *	@return			true if word set deleted, false otherwise.
	 *					If the word set didn't exist, true is returned.
	 */

	public static boolean deleteWordSet( String title )
	{
		boolean result	= true;

		WordSet wordSet	= getWordSet( title );

		if ( wordSet != null )
		{
			result	= deleteWordSet( wordSet );
		}

		return result;
	}

	/**	Delete multiple word sets.
	 *
	 *	@param	wordSets	The word sets to delete.
	 *
	 *	@return				true if word sets deleted, false otherwise.
	 */

	public static boolean deleteWordSets
	(
		WordSet[] wordSets
	)
	{
		boolean result	= true;

		for ( int i = 0 ; i < wordSets.length ; i++ )
		{
			result	= result & deleteWordSet( wordSets[ i ] );
		}

		return result;
	}

	/**	Delete multiple word sets.
	 *
	 *	@param	wordSets			The word sets to delete.
	 *	@param	progressReporter 	A progress reporter.
	 *
	 *	@return						true if word sets deleted, false otherwise.
	 */

	public static boolean deleteWordSets
	(
		WordSet[] wordSets ,
		ProgressReporter progressReporter
	)
	{
		boolean result	= true;

								//	Just return if no word sets to delete.

		if ( wordSets == null ) return result;

								//	Set progress display.

		if ( progressReporter != null )
		{
			progressReporter.setMaximumBarValue( wordSets.length );

			progressReporter.updateProgress
			(
				0 ,
				WordHoardSettings.getString
				(
					"Deleting" ,
					"Deleting"
				)
			);

			progressReporter.setIndeterminate( false );
		}
								//	Loop over word sets to delete.

		for ( int i = 0 ; i < wordSets.length ; i++ )
		{
								//	Get updated title for deleting
								//	the next word set.

			PrintfFormat deletingTitleFormat	=
            	new PrintfFormat
               	(
					WordHoardSettings.getString
					(
						"DeletingWordSetname" ,
						"Deleting word set %s"
					)
               	);

			String deletingTitle	=
				deletingTitleFormat.sprintf
				(
					new Object[]{ wordSets[ i ].toString() }
				);
								//	Update progress display with new title.

			if ( progressReporter != null )
			{
				progressReporter.updateProgress( i , deletingTitle );
            }
								//	Try deleting the next wordset.

			result	= result & deleteWordSet( wordSets[ i ] );

								//	Update progress display.

			if ( progressReporter != null )
			{
				progressReporter.updateProgress( i + 1 , deletingTitle );
			}
		}

		return result;
	}

	/**	Get a word set by title.
	 *
	 *	@param	title	The title of the word set to fetch.
	 *	@param	owner	The owner of the word set to fetch.
	 *
	 *	@return			The word set with the requested title,
	 *					or null if not found.
	 */

	public static WordSet getWordSet( String title , String owner )
	{
		return
			(WordSet)UserDataObjectUtils.getUserDataObject
			(
				WordSet.class , title , owner
			);
	}

	/**	Get a word set by title.
	 *
	 *	@param	title	The title of the word set to fetch.
	 *
	 *	@return			The word set with the requested title,
	 *					or null if not found.
	 */

	public static WordSet getWordSet( String title )
	{
		return
			(WordSet)UserDataObjectUtils.getUserDataObject(
				WordSet.class , title );
	}

	/**	Get all available public word sets as an array.
	 *
	 *	@return		All available word sets as an array of WordSet.
	 */

	public static WordSet[] getWordSets()
	{
		return udosToWordSets
		(
			UserDataObjectUtils.getUserDataObjects( WordSet.class )
		);
	}

	/**	Remove phrase sets from word set list.
	 *
	 *	@param	wordSetList		The word set list which may also
	 *							include phrase sets.
	 *	@return	The word set list with the phrase sets removed.
	 */

	public static java.util.List removePhraseSets
	(
		java.util.List wordSetList
	)
	{
		Iterator iterator	= wordSetList.iterator();

		while ( iterator.hasNext() )
		{
			Object object	= iterator.next();

			if ( object instanceof PhraseSet )
			{
				iterator.remove();
			}
		}

		return wordSetList;
	}

	/**	Get all available word sets for a specified owner as an array.
	 *
	 *	@param		owner	The owner.
	 *
	 *	@return				All available word sets as an array of WordSet.
	 */

	public static WordSet[] getWordSets( String owner )
	{
		return udosToWordSets
		(
			UserDataObjectUtils.getUserDataObjects( WordSet.class )
		);
	}

	/**	Get all word sets for the logged in user as an array.
	 *
	 *	@return				All word sets for logged in user
	 *						as an array of WordSet.
	 */

	public static WordSet[] getWordSetsForLoggedInUser()
	{
		return udosToWordSets
		(
			UserDataObjectUtils.getUserDataObjectsForLoggedInUser
			(
				WordSet.class
			)
		);
	}

	/**	Get count of word sets for a user.
	 *
	 *	@param		owner	The owner.
	 *
	 *	@return				Count of word sets owned by "owner".
	 */

	public static int getWordSetsCount( String owner )
	{
		return UserDataObjectUtils.getUserDataObjectsCount
		(
			WordSet.class ,
			owner ,
			false
		);
	}

	/**	Get count of all available word sets.
	 *
	 *	@return		Count of all available word sets.
	 */

	public static int getWordSetsCount()
	{
		return UserDataObjectUtils.getUserDataObjectsCount
		(
			WordSet.class
		);
	}

	/**	Get all available words in a word set as an array.
	 *
	 *	@param	wordSet	The word set.
	 *
	 *	@return			All available words in the word set
	 *					as an array of Word.
	 *
	 *	<p>
	 *	Returns null if word set is null.
	 *	</p>
	 */

	public static Word[] getWords( WordSet wordSet )
	{
		if ( wordSet == null ) return null;

								//	Make sure word set is completely loaded.

		if ( !PersistenceManager.doContains( wordSet ) )
		{
			wordSet	=
				(WordSet)PersistenceManager.doLoad
				(
					WordSet.class ,
					wordSet.getId()
				);

			if ( wordSet == null ) return null;
		}

		return WordUtils.getWordsByTags( wordSet.getWordTags() );
	}

	/**	Get all available words in a word set as an array of Word.
	 *
	 *	@param	wordSet	The word set.
	 *
	 *	@return			All available words in the word set
	 *					as an array of Word.
	 *
	 *	<p>
	 *	Returns null if word set is null.
	 *	</p>
	 */

	public static Word[] getWord( WordSet wordSet )
	{
		return (Word[])getWords( wordSet );
	}

	/**	Update a word set.
	 *
	 *	@param	wordSet		The word set to update.
	 *	@param	title		Title for the word set.
	 *	@param	description	Description for the word set.
	 *	@param	webPageURL	Web page URL for the word set.
	 *	@param	isPublic	True if word set is public.
	 *
	 *	@return				true if update succeed, false otherwise.
	 *
	 *	@throws				DuplicateWordSetException if new (title,owner)
	 *						combination already exists.
	 *
	 *	@throws				BadOwnerException if user is not logged in or
	 *						is not the word set owner .
	 *
	 *	<p>
	 *	We do not provide for updating the words in the word set.
	 *	You should create a new word set if the words are changed.
	 *	</p>
	 */

	public static boolean updateWordSet
	(
		WordSet wordSet ,
		final String title ,
		final String description ,
		final String webPageURL ,
		final boolean isPublic
	)
		throws DuplicateWordSetException, BadOwnerException
	{
		if	(	UserDataObjectUtils.isDuplicate
				(
					WordSet.class ,
					title ,
					wordSet.getOwner() ,
					wordSet.getId()
				)
			)
		{
			throw new DuplicateWordSetException();
		}

		return
			UserDataObjectUtils.updateUserDataObject
			(
				wordSet ,
				new UserDataObjectUpdater()
				{
					public void update
					(
						UserDataObject userDataObject
					)
					{
						WordSet wordSet	= (WordSet)userDataObject;

						wordSet.setTitle( title );
						wordSet.setDescription( description );
						wordSet.setWebPageURL( webPageURL );
						wordSet.setIsPublic( isPublic );
						wordSet.setModificationTime( new Date() );
					}
				}
			);
	}

	/**	Get array of all work parts for a word set.
	 *
	 *	@param		wordSet		The word set.
	 *
	 *	@return					Array of WorkPart for all worK parts
	 *							represented in word set.
	 */

	public static WorkPart[] getWorkParts( WordSet wordSet )
	{
		if ( wordSet == null ) return null;

								//	Make sure word set is completely loaded.

		if ( !PersistenceManager.doContains( wordSet ) )
		{
			wordSet	=
				(WordSet)PersistenceManager.doLoad
				(
					WordSet.class ,
					wordSet.getId()
				);

			if ( wordSet == null ) return null;
		}

		return WorkUtils.getWorkPartsByTag( wordSet.getWorkPartTags() );
	}

	/**	Get array of all works for a word set.
	 *
	 *	@param		wordSet		The word set.
	 *
	 *	@return					Array of Work for all works
	 *							represented in word set.
	 */

	public static Work[] getWorks( WordSet wordSet )
	{
		if ( wordSet == null ) return null;

								//	Make sure word set is completely loaded.

		if ( !PersistenceManager.doContains( wordSet ) )
		{
			wordSet	=
				(WordSet)PersistenceManager.doLoad
				(
					WordSet.class ,
					wordSet.getId()
				);

			if ( wordSet == null ) return null;
		}

		return WorkUtils.getWorksByTag( wordSet.getWorkTags() );
	}

	/**	Get total word form count for one work represented in a word set.
	 *
	 *	@param	wordSet		The word set.
	 *	@param	wordForm	The word form.
	 *	@param	work		The work.
	 *
	 *	@return				Count of the word form in the word set.
	 */

	public static int getWordFormCount
	(
		WordSet wordSet ,
		int wordForm ,
		Work work
	)
	{
		int result	= 0;

		java.util.List wordSetWordFormCounts	=
			PersistenceManager.doQuery
			(
				"from WordSetTotalWordFormCount twfc where " +
					"twfc.wordSet = :wordSet and " +
					"twfc.wordForm = :wordForm and " +
					"twfc.workPartTag = :workPartTag" ,
				new String[]
				{
					"wordSet" ,
					"wordForm" ,
					"workPartTag"
				} ,
				new Object[]
				{
					wordSet ,
					new Integer( wordForm ) ,
					work.getTag()
				} ,
				true
			);
								//	Pick up count.

		if ( wordSetWordFormCounts != null )
		{
			Iterator iterator	= wordSetWordFormCounts.iterator();

			while ( iterator.hasNext() )
			{
				WordSetTotalWordFormCount wwfc	=
					(WordSetTotalWordFormCount)iterator.next();

				result	+= wwfc.getWordFormCount();
			}
		}

		return result;
	}

	/**	Get total word form count in a word set.
	 *
	 *	@param	wordSet		The word set.
	 *	@param	wordForm	The word form.
	 *
	 *	@return				Count of the word form in the word set.
	 */

	public static int getWordFormCount
	(
		WordSet wordSet ,
		int wordForm
	)
	{
		int result	= 0;

		java.util.List wordSetWordFormCounts	=
			PersistenceManager.doQuery
			(
				"from WordSetTotalWordFormCount twfc where " +
					"twfc.wordSet = :wordSet and " +
					"twfc.wordForm = :wordForm" ,
				new String[]
				{
					"wordSet" ,
					"wordForm"
				} ,
				new Object[]
				{
					wordSet ,
					new Integer( wordForm ) ,
				} ,
				true
			);
								//	Pick up count.

		if ( wordSetWordFormCounts != null )
		{
			Iterator iterator	= wordSetWordFormCounts.iterator();

			while ( iterator.hasNext() )
			{
				WordSetTotalWordFormCount wwfc	=
					(WordSetTotalWordFormCount)iterator.next();

				result	+= wwfc.getWordFormCount();
			}
		}

		return result;
	}

	/**	Get distinct word form count in a word set.
	 *
	 *	@param	wordSet		The word set.
	 *	@param	wordForm	The word form.
	 *
	 *	@return				Count of the distinct word forms
	 *						in the word set.
	 */

	public static int getDistinctWordFormCount
	(
		WordSet wordSet ,
		int wordForm
	)
	{
		int result	= 0;

		java.util.List wordSetWordFormCounts	=
			PersistenceManager.doQuery
			(
				"select count(*) from WordSetWordCount wc where " +
					"wc.wordSet = :wordSet and " +
					"wc.wordForm = :wordForm" ,
				new String[]
				{
					"wordSet" ,
					"wordForm"
				} ,
				new Object[]
				{
					wordSet ,
					new Integer( wordForm ) ,
				} ,
				true
			);
								//	Pick up count.

		if ( wordSetWordFormCounts != null )
		{
			Iterator iterator	= wordSetWordFormCounts.iterator();

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

	/**	Get word count in a word set.
	 *
	 *	@param	wordSet	The word set.
	 *	@param	word		The word.
	 *	@param	wordForm	The word form.
	 *
	 *	@return				Count of the word form in the word set.
	 */

	public static int getWordFormCount
	(
		WordSet wordSet ,
		Spelling word ,
		int wordForm
	)
	{
		int result	= 0;

		java.util.List wordSetWordFormCounts	=
			PersistenceManager.doQuery
			(
				"from WordSetWordCount wc where " +
					"wc.wordSet = :wordSet and " +
					"wc.wordForm = :wordForm and " +
					"wc.word = :word" ,
				new String[]
				{
					"wordSet" ,
					"wordForm" ,
					"word"
				} ,
				new Object[]
				{
					wordSet ,
					new Integer( wordForm ) ,
					word
				} ,
				true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( wordSetWordFormCounts != null )
		{
			Iterator iterator	= wordSetWordFormCounts.iterator();

			while( iterator.hasNext() )
			{
				WordSetWordCount wc	=
					(WordSetWordCount)iterator.next();

				result	+= wc.getWordCount();
			}
		}

		return result;
	}

	/**	Get word count in a word set for a specific work.
	 *
	 *	@param	wordSet		The word set.
	 *	@param	word		The word.
	 *	@param	wordForm	The word form.
	 *	@param	work		The work.
	 *
	 *	@return				Count of the word form in the word set.
	 */

	public static int getWordFormCount
	(
		WordSet wordSet ,
		Spelling word ,
		int wordForm ,
		Work work
	)
	{
		int result	= 0;

		java.util.List wordSetWordFormCounts	=
			PersistenceManager.doQuery
			(
				"from WordSetWordCount wc where " +
					"wc.wordSet = :wordSet and " +
					"wc.wordForm = :wordForm and " +
					"wc.word = :word and " +
					"wc.workPartTag = :workPartTag" ,
				new String[]
				{
					"wordSet" ,
					"wordForm" ,
					"word" ,
					"workPartTag"
				} ,
				new Object[]
				{
					wordSet ,
					new Integer( wordForm ) ,
					word ,
					work.getTag()
				} ,
				true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( wordSetWordFormCounts != null )
		{
			Iterator iterator	= wordSetWordFormCounts.iterator();

			while( iterator.hasNext() )
			{
				WordSetWordCount wc	=
					(WordSetWordCount)iterator.next();

				result	+= wc.getWordCount();
			}
		}

		return result;
	}

	/**	Get word count for multiple words in a set of word sets.
	 *
	 *	@param	wordSets		The word sets.
	 *	@param	words			The words.
	 *	@param	wordForm		The word form.
	 *
	 *	@return					Map with words as keys and counts of each word
	 *							in the word sets as values.
	 */

	public static Map getWordFormCount
	(
		WordSet[] wordSets ,
		Spelling[] words ,
		int wordForm
	)
	{
		TreeMap result	= new TreeMap();

		if	(	( wordSets.length == 0 ) ||
				( words.length == 0 ) ) return result;

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
			wordStrings.add( words[ i ].getString() );
		}

		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
			"(wc.word.string, wc.word.charset, sum(wc.wordCount)) " +
				"from WordSetWordCount wc where " +
				"wc.word.string in (:words) and " +
				"wc.wordForm = :wordForm and " +
				"wc.wordSet in (:wordSets) " +
				"group by wc.word.string";

		String[] paramNames		=
			new String[]
			{
				"words" ,
				"wordForm" ,
				"wordSets"
			};

		Object[] paramValues	=
			new Object[]
			{
				wordStrings ,
				new Integer( wordForm ) ,
				Arrays.asList( wordSets )
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

				CountMapUtils.updateWordCountMap
				(
					countResult.word.toInsensitive() ,
					countResult.count ,
					result
				);
			}
		}

		return result;
	}

	/**	Get word count for multiple words in a word set.
	 *
	 *	@param	wordSet			The word set.
	 *	@param	words			The words.
	 *	@param	wordForm		The word form.
	 *
	 *	@return					Map with words as keys and counts of each word
	 *							in the word set as values.
	 */

	public static Map getWordFormCount
	(
		WordSet wordSet ,
		Spelling[] words ,
		int wordForm
	)
	{
		return getWordFormCount
		(
			new WordSet[]{ wordSet } ,
			words ,
			wordForm
		);
	}

	/**	Get word counts in a single word set.
	 *
	 *	@param	wordSet		The word set.
	 *	@param	wordForm	The word form to count.
	 *
	 *	@return				Map containing each word in the word set
	 *						as a key and the count of the word as the value.
	 */

	public static Map getWordCounts( WordSet wordSet , int wordForm )
	{
		TreeMap wordCounts	= new TreeMap();

		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
			"(wc.word , sum(wc.wordCount)) " +
			"from WordSetWordCount wc where " +
			"wc.wordSet = :wordSet and " +
			"wc.wordForm = :wordForm " +
			"group by wc.word";

		java.util.List wordSetWordCounts	=
			PersistenceManager.doQuery
			(
				queryString ,
				new String[]{ "wordSet" , "wordForm" } ,
				new Object[]{ wordSet , new Integer( wordForm ) } ,
				true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( wordSetWordCounts != null )
		{
			Iterator iterator	= wordSetWordCounts.iterator();

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

	/**	Get word form counts in a set of word sets.
	 *
	 *	@param	wordSets		The word sets.
	 *	@param	wordForm		The word form to count.
	 *	@param	getWorkCounts	if true, work counts are returned in the second
	 *							result map (see below).  If false, hashsets of
	 *							work IDs are returned in the second result map.
	 *
	 *	@return					Array of two maps.  The first map contains
	 *							each word of then specified word form
	 *							in the word sets as a key and
	 *							the count of the appearance of the word
	 *							in the word sets as a value.  The second map
	 *							also has the word as the key.  If "getWorkCounts"
	 *							is true, the values for each word are the counts
	 *							of the works in which the word appears.  If
	 *							"getWorkCounts" is false, the value is a hash set
	 *							of the word IDs for each work in which the word
	 *							appears.
	 */

	public static Map[] getWordCounts
	(
		WordSet[] wordSets ,
		int wordForm ,
		boolean getWorkCounts
	)
	{
		TreeMap wordCounts	= new TreeMap();
		TreeMap workCounts	= new TreeMap();

		if ( wordSets.length == 0 )
		{
			return new TreeMap[]{ wordCounts , workCounts };
		}

		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
			"(wc.word , wc.wordCount , wc.workPartTag) " +
			"from WordSetWordCount wc where " +
			"wc.wordForm = :wordForm and " +
			"wc.wordSet in (:wordSets)";

		String[] paramNames		=
			new String[]
			{
				"wordForm" ,
				"wordSets"
			};

		Object[] paramValues	=
			new Object[]
			{
				new Integer( wordForm ) ,
				Arrays.asList( wordSets )
			};

		java.util.List wordSetWordCounts	=
			PersistenceManager.doQuery
			(
				queryString , paramNames , paramValues , true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( wordSetWordCounts != null )
		{
			Iterator iterator	= wordSetWordCounts.iterator();

			while ( iterator.hasNext() )
			{
				CountResult countResult	= (CountResult)iterator.next();

				Spelling wordText	= countResult.word.toInsensitive();
				int newCount		= countResult.count;
				String workTag		= countResult.workTag;
				Long workID			= WorkUtils.getWorkIDByTag( workTag );

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
								//	Increment work list for this word.

				if ( workCounts.containsKey( wordText ) )
				{
					HashSet workIDs	= (HashSet)workCounts.get( wordText );
					workIDs.add( workID );
				}
				else
				{
					HashSet workIDs	= new HashSet();
					workIDs.add( workID );
					workCounts.put( wordText , workIDs );
				}
			}
		}
								//	Convert work lists to work counts for
								//	each word. if requested.

		if ( getWorkCounts ) CountMapUtils.worksToWorkCounts( workCounts );

		return new TreeMap[]{ wordCounts , workCounts };
	}

	/**	Get word form counts in a set of word sets.
	 *
	 *	@param	wordSets		The word sets.
	 *	@param	wordForm		The word form to count.
	 *
	 *	@return					Array of two maps.  The first map contains
	 *							each word of then specified word form
	 *							in the word sets as a key and
	 *							the count of the appearance of the word
	 *							in the word sets as a value.  The second map
	 *							also has the word as the key but
	 *							provides the number of parent works for the
	 *							word sets in which the word appears as a value.
	 */

	public static Map[] getWordCounts( WordSet[] wordSets , int wordForm )
	{
		return getWordCounts( wordSets , wordForm , true );
	}

	/**	Get word occurrences for a word in a specified word set.
	 *
	 *	@param	wordSet		The word set.
	 *	@param	wordForm    The word form.
	 *	@param	word		The word to look up.
	 *
	 *	@return				Array of Word entries for selected word
	 *						in word set.  The search word is converted
	 *						to case and diacritical insensitive form
	 *						before the search.
	 */

	public static Word[] getWordOccurrences
	(
		WordSet wordSet ,
		int wordForm ,
		Spelling word
	)
	{
		String queryString	= "";

		Word[] words;

		if ( wordForm == WordForms.SPELLING )
		{
			String[] spellingAndCompoundWordClass	=
				WordUtils.extractSpellingAndCompoundWordClass(
					word.getString() );

								//	spellingAndCompoundWordClass[1] is the
								//	compound part of speech.
			queryString	=
				"from Word wo " +
				"where wo.spellingInsensitive.string=:spelling and " +
				WordUtils.createCompoundWordClassQueryString(
					spellingAndCompoundWordClass[ 1 ] );

			words	=
				WordUtils.performWordQuery
				(
					queryString ,
					new String[]{ "spelling" } ,
					new Object[]
					{
						CharsetUtils.translateToInsensitive
						(
							spellingAndCompoundWordClass[ 0 ]
						)
					}
				);
		}
		else
		{
			queryString	=
				"select wo from Word wo, WordPart wp " +
				" where " +
				"wp.word = wo " +
				"and wp.lemPos.lemma.tagInsensitive.string=:lemmaTag";

			words	=
				WordUtils.performWordQuery
				(
					queryString ,
					new String[]{ "lemmaTag" } ,
					new Object[]
					{
						CharsetUtils.translateToInsensitive
						(
							word.getString()
						)
					}
				);
		}
								//	Eliminate words not in the word set.

		Set prunedWords	= pruneToWordsInWordSet( wordSet , words );

		return (Word[])prunedWords.toArray( new Word[]{} );
	}

	/**	Get surrounding words of a specified word in a word set.
	 *
	 *	@param	wordSet				The word set.
	 *	@param	word				Word for which to get span.
	 *	@param	leftSpan			# of words to left of
	 *								specified word to retrieve.
	 *	@param	rightSpan			# of words to right of
	 *								specified word to retrieve.
	 *
 	 *	@return						Span of words in the word set for
	 *								the specified word.
	 */

	public static Word[] getSpan
	(
		WordSet wordSet ,
		Word word ,
		int leftSpan ,
		int rightSpan
	)
	{
								//	Get words in the specified span.
		Word[] words	=
			WordUtils.performWordQuery
			(
				"select collocate from Word collocate," +
				" Word word where" +
				" ( word = :word ) and" +
				" collocate.colocationOrdinal between " +
				" ( word.colocationOrdinal - :leftSpan ) and" +
				" ( word.colocationOrdinal + :rightSpan )" ,
				new String[]{ "word" , "leftSpan" , "rightSpan" } ,
				new Object[]
				{
					word ,
					new Integer( leftSpan ) ,
					new Integer( rightSpan)
				}
			);
								//	Eliminate words not in the word set.

		Set prunedWords	= pruneToWordsInWordSet( wordSet , words );

		return (Word[])prunedWords.toArray( new Word[]{} );
	}

	/**	Check if word is in in a specified word set.
	 *
	 *	@param	wordSet				The word set.
	 *	@param	word				Word to look up.
	 *
 	 *	@return						true if word is in the word set, else false.
	 */

	public static boolean isWordInWordSet
	(
		WordSet wordSet ,
		Word word
	)
	{
		if ( ( wordSet == null ) || ( word == null ) ) return false;

		wordSet	= loadWordSet( wordSet );

		return wordSet.getWordTags().contains( word.getTag() );
	}

	/**	Prune words to those in a specified word set.
	 *
	 *	@param	wordSet				The word set.
	 *	@param	words				Word entries to look up in word set.
	 *
 	 *	@return						Set of words which exist in the word set.
	 */

	public static Set pruneToWordsInWordSet
	(
		WordSet wordSet ,
		Word[] words
	)
	{
		HashSet result	= new HashSet();

		if ( words.length > 0 )
		{
			wordSet	= loadWordSet( wordSet );

			Collection wordTags	= wordSet.getWordTags();

			for ( int i = 0 ; i < words.length ; i++ )
			{
				if ( wordTags.contains( words[ i ].getTag() ) )
				{
					result.add( words[ i ] );
				}
			}
		}

		return result;
	}

	/**	Import a specified word set by name from a DOM document.
	 *
	 *	@param	wordSetNode		The DOM node which is the root of the
	 *							word set to import.
	 *
	 *	@return					The imported word set, or null if the
	 *							import fails.
	 *
	 *	@throws	BadOwnerException if the user is not logged in.
	 */

	public static WordSet importFromDOMDocument
	(
		org.w3c.dom.Node wordSetNode
	)
	{
		WordSet result	= null;

           						//	If word set node is null, quit.

		if ( wordSetNode == null ) return result;

								//	If the user is not logged in,
								//	throw a BadOwnerException.

		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException
			(
				WordHoardSettings.getString
				(
					"Notloggedin" ,
					"Not logged in"
				)
			);
		}
								//	If the word set node is not "wordset",
								//	do nothing further.

		if ( !wordSetNode.getNodeName().equals( "wordset" ) )
		{
			return result;
		}
								//	Create the word set entry.

		result	= new WordSet( wordSetNode , WordHoardSettings.getUserID() );

		return result;
	}

	/**	Import one or more word sets from XML file.
	 *
	 *	@param	importDocument	The DOM document containing the word sets to import.
	 *
	 *	@return					The imported word sets.  May be empty.
	 *
	 *	<p>
	 *	Note:	The word sets are not persisted here.  That is the
	 *			responsibility of the caller.
	 *	</p>
	 */

	public static WordSet[] importWordSets
	(
		org.w3c.dom.Document importDocument
	)
	{
		WordSet[] result	= new WordSet[ 0 ];

								//	Return if document is null.

		if ( importDocument == null ) return result;

								//	Get the root node.

		org.w3c.dom.Node rootElement	= importDocument.getDocumentElement();

								//	If the root node is not "wordhoard",
								//	this is a bogus file.

		if ( !rootElement.getNodeName().equals( "wordhoard" ) )
		{
			return result;
		}
								//	Get the word set children of the root node.

		ArrayList wordSetChildren	=
			DOMUtils.getChildren( rootElement , "wordset" );

								//	If there are no word sets just return.

		if	(	( wordSetChildren == null ) ||
				( wordSetChildren.size() == 0 ) )
		{
			return result;
		}
								//	Holds imported word sets.

		ArrayList wordSets	= new ArrayList();

								//	Process each word set.

		for ( int i = 0 ; i < wordSetChildren.size() ; i++ )
		{
								//	Get next word set node.

			Node wordSetNode	= (Node)wordSetChildren.get( i );

								//	Import to word set.

			WordSet wordSet		= importFromDOMDocument( wordSetNode );

								//	If imported OK, add to list.

			if ( wordSet != null )
			{
				wordSets.add( wordSet );
			}
		}
								//	Return all imported word sets as array.

		return (WordSet[])wordSets.toArray( new WordSet[]{} );
	}

	/**	Load a word set if it isn't already loaded.
	 *
	 *	@param	wordSet		The word set to load.
	 *	@return	The loaded word set.
	 */

	public static WordSet loadWordSet( WordSet wordSet )
	{
		if ( !PersistenceManager.doContains( wordSet ) )
		{
			wordSet		=
				(WordSet)PersistenceManager.doLoad
				(
					WordSet.class ,
					wordSet.getId()
				);
		}

		return wordSet;
	}

	/**	Display create/update word set dialog with creation/update.
	 *
	 *	@param	parentWindow		Parent window for dialog.
	 *	@param	wordGetter			WordGetter to retrieve list of
	 *								words to add to word set.
	 *	@param	progressReporter	Progress reporter to display
	 *								progress of save word set operation.
	 *								May be null.
	 *
	 *	@return		The new or updated word set.
	 *
	 *	@throws		Exception if something went wrong.
	 *
	 *	<p>
	 *	If ProgressReporter is not null, you should execute this
	 *	method from a separate thread to ensure the GUI updates
	 *	while the word set is being saved.
	 *	</p>
	 */

	public static WordSet saveWordSet
	(
		JFrame parentWindow ,
		WordGetter wordGetter ,
		ProgressReporter progressReporter
	)
		throws Exception
	{
		WordSet result	= null;

								//	Display the word part creation/
								//	editing dialog.

		SaveWordSetDialog dialog	=
			new SaveWordSetDialog
			(
				WordHoardSettings.getString
				(
					"SelectWordSet" ,
					"Select Word Set"
				) ,
				parentWindow
			);

		dialog.show( parentWindow );

								//	If the dialog cancelled, return
								//	null word set.

		if ( dialog.getCancelled() ) return null;

								//	If dialog not cancelled,
								//	get the word set settings from
								//	the dialog.

		WordSet wordSet		= dialog.getSelectedItem();
		String title		= dialog.getSetTitle();
		String description	= dialog.getDescription();
		String webPageURL	= dialog.getWebPageURL();
		boolean isPublic	= dialog.getIsPublic();

								//	Get the list of words.

		java.util.List wordList	=
			wordGetter.getWords( progressReporter );

		if ( progressReporter != null )
		{
			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"SavingWordSet" ,
					"Saving Word Set"
				)
			);
    	}
								//	Delete existing word set if one
								//	was selected in the dialog.

		if ( wordSet != null )
		{
			deleteWordSet( wordSet );
		}
								//	Create new word set.
		result	=
			addWordSet
			(
				title, description, webPageURL, isPublic, "",
				wordList, progressReporter
			);

		return result;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected WordSetUtils()
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

