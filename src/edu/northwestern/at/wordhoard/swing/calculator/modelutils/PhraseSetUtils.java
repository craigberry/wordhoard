package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.cql.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Phrase set utilities.
 */

public class PhraseSetUtils
{
	private static boolean debug	= false;

	/**	Copy UserDataObject array to PhraseSet array.
	 *
	 *	@param	udos	Array of user data objects, all actually
	 *					PhraseSet objects.
	 *
	 *	@return			Array of PhraseSet objects.
	 */

	protected static PhraseSet[] udosToPhraseSets( UserDataObject[] udos )
	{
		PhraseSet[] result	= new PhraseSet[ udos.length ];

		for ( int i = 0 ; i < udos.length ; i++ )
		{
			result[ i ]	= (PhraseSet)udos[ i ];
		}

		return result;
	}

	/**	Add a new phrase set with specified phrases.
	 *
	 *	@param	title				Title for the new phrase set.
	 *	@param	description			Description for the new phrase set.
	 *	@param	webPageURL			Web page URL for the new phrase set.
	 *	@param	owner				The phrase set owner.
	 *	@param	isPublic			True if phrase set to be public.
	 *	@param	query				The query string generating the phrases.
	 *	@param	words				Words to add to phrase set.
	 *	@param	phrases				Phrases to add to phrase set.
	 *	@param	progressReporter	Progress display to update.  May be null.
	 *
	 *	@return						PhraseSet object if phrase set added,
	 *								else null.
	 */

	public static PhraseSet addPhraseSet
	(
		String title ,
		String description ,
		String webPageURL ,
		String owner ,
		boolean isPublic ,
		String query ,
		Map words ,
		Map phrases ,
		ProgressReporter progressReporter
	)
		throws DuplicatePhraseSetException
	{
								//	Create new phrase set.
								//	Save it initially as private
								//	so it doesn't appear for use
								//	before the phrases are stored.

		PhraseSet phraseSet	=
			addPhraseSet
			(
				title ,
				description ,
				webPageURL ,
				owner ,
				isPublic ,
				query
			);
								//	If new phrase set successfully created,
								//	add the phrases to it.

		if ( phraseSet != null )
		{
			if ( addPhrases( phraseSet , words , phrases , progressReporter ) )
            {
								//	Set the phrase set active.
				phraseSet	=
					(PhraseSet)PersistenceManager.doLoad(
						PhraseSet.class , phraseSet.getId() );

				phraseSet.setIsActive( true );
			}
								//	If we could not add phrases,
								//	delete the phrase set.
			else
			{
				deletePhraseSet( phraseSet );
				phraseSet	= null;
			}
		}
								//	Return new PhraseSet object to caller.
		return phraseSet;
	}

	/**	Add a new phrase set.
	 *
	 *	@param	title		Title for the new phrase set.
	 *	@param	description	Description for the new phrase set.
	 *	@param	webPageURL	Web page URL for the new phrase set.
	 *	@param	owner		The phrase set owner.
	 *	@param	isPublic	True if phrase set is to be public.
	 *	@param	query		The query string generating the phrases.
	 *
	 *	@return				PhraseSet object if phrase set added, else null.
	 */

	public static PhraseSet addPhraseSet
	(
		String title ,
		String description ,
		String webPageURL ,
		String owner ,
		boolean isPublic ,
		String query
	)
		throws DuplicatePhraseSetException
	{
								//	Check if phrase set of this title already
								//	exists.

		PhraseSet phraseSet	= getPhraseSet( title , owner );

								//	Report error if so.

		if ( phraseSet != null )
		{
			throw new DuplicatePhraseSetException();
		}
								//	Create new phrase set object with
								//	specified title.

		phraseSet	=
			new PhraseSet
			(
				title ,
				description ,
				webPageURL ,
				owner ,
				isPublic ,
				query
			);
								//	Persist the phrase set object.

		PersistenceManager pm	= null;

		try
		{
			pm	= PersistenceManager.getPM();

			pm.begin();

			pm.save( phraseSet );

			pm.commit();
		}
		catch ( PersistenceException e )
		{
			phraseSet	= null;

			try
			{
				if ( pm != null ) pm.rollback();
			}
			catch ( PersistenceException e2 )
			{
			}
		}
								//	Return the newly persisted phrase set object.
		return phraseSet;
	}

	/**	Add a new phrase set using a specified query.
	 *
	 *	@param	title			Title for the new phrase set.
	 *	@param	description		Description for the new phrase set.
	 *	@param	webPageURL		Web page URL for the new phrase set.
	 *	@param	owner			The phrase set owner.
	 *	@param	isPublic		True if phrase set to be public.
	 *	@param	analysisText	Text set from which to extract phrases.
	 *	@param	query			Query to select phrases in analysis text.
	 *	@param	parentWindow	Parent window.
	 *
	 *	@return					PhraseSet object if phrase set added, else null.
	 */

	public static PhraseSet addPhraseSet
	(
		String title ,
		String description ,
		String webPageURL ,
		String owner ,
		boolean isPublic ,
		WordCounter analysisText ,
		String query ,
		Window parentWindow
	)
		throws DuplicatePhraseSetException, InvalidCQLQueryException
	{

								//	Return new PhraseSet object to caller.
		return null;
	}

	/**	Add phrases to a phrase set.
	 *
	 *	@param	phraseSet			The phrase set.
	 *	@param	words				Words to add to phrase set.
	 *	@param	phrases				Phrases to add to phrase set.
	 *	@param	progressReporter	Progress display to update.  May be null.
	 *
	 *	@return						true if phrases added successfully.
	 *
	 *	<p>
	 *	If this method returns false, the caller should delete the
	 *	phrase set.
	 *	</p>
	 */

	public static boolean addPhrases
	(
		PhraseSet phraseSet ,
		Map words ,
		Map phrases ,
		ProgressReporter progressReporter
	)
	{
		return false;
	}

	/**	Delete a phrase set.
	 *
	 *	@param	phraseSet	The phrase set to delete.
	 *
	 *	@return			true if phrase set deleted, false otherwise.
	 */

	public static boolean deletePhraseSet( PhraseSet phraseSet )
	{
		boolean result			= false;
		PersistenceManager pm	= null;

		if ( phraseSet == null ) return result;

								//	Get phrase set ID.

		String sid				= phraseSet.getId().toString();

		try
		{
								//	Get persistence manager.

			pm	= PersistenceManager.getPM();

								//	Start transaction.
			pm.begin();
								//	Make sure the phrase set is loaded under
								//	the current persistence manager
								//	before deleting it.

			PhraseSet phraseSetToDelete	=
				(PhraseSet)pm.load( PhraseSet.class , phraseSet.getId() );

								//	Delete the words and phrases in the phrase set.
								//	$$$PIB$$$ Need to speed this up.
/*
			java.util.List phrases	=
				pm.query
				(
					"select ph from PhraseSet ps, Phrase ph where " +
					"ps = :phraseSet and " +
					"(ph in elements(ps.phrases))" ,
					new String[]{ "phraseSet" } ,
					new Object[]{ phraseSetToDelete }
				);

			Iterator iterator	= phrases.iterator();

			while ( iterator.hasNext() )
			{
				Phrase phrase	= (Phrase)iterator.next();
				pm.delete( phrase );
			}
*/
			Long phraseSetId	= phraseSetToDelete.getId();

								//	Use JDBC SQL to delete words in phrases
								//	and phrases.
			int count	=
				pm.deleteViaSQL
				(
					"delete from phrase, phrase_words, phraseset_phrases " +
					"using phrase, phrase_words, phraseset_phrases " +
					"where phraseset_phrases.phraseSetId=" +
						phraseSetId.longValue() +
					" and phrase_words.phraseId=phraseset_phrases.phraseId " +
					" and phrase.id=phrase_words.phraseId"
				);

			if ( debug ) System.out.println(
				"deletePhrase: deleted " + count + " phrase words and phrases." );

								//	Use JDBC SQL to delete phrase set counts.
			count	=
				pm.deleteViaSQL
				(
					"delete from wordsetwordcount where wordSet=" + sid
				);
								//	Use JDBC SQL to delete phrase set
								//	word total word form counts.
			count	=
				pm.deleteViaSQL
				(
					"delete from wordsettotalwordformcount where wordSet=" + sid
				);
								//	Delete the phrase set.

			pm.delete( phraseSetToDelete );

								//	Commit the transaction.
			pm.commit();

			result	= true;
		}
		catch ( PersistenceException e )
		{
								//	Roll back if any error occurs.
			try
			{
				if ( pm != null ) pm.rollback();
			}
			catch ( PersistenceException e2 )
			{
			}
		}

		return result;
	}

	/**	Delete a phrase set by title.
	 *
	 *	@param	title	The title of the phrase set to delete.
	 *	@param	owner	Owner of phrase set.
	 *
	 *	@return			true if phrase set deleted, false otherwise.
	 *					If the phrase set didn't exist, true is returned.
	 */

	public static boolean deletePhraseSet( String title , String owner )
	{
		boolean result	=	true;

		PhraseSet	phraseSet	= getPhraseSet( title , owner );

		if ( phraseSet != null )
		{
			result	= deletePhraseSet( phraseSet );
		}

		return result;
	}

	/**	Delete multiple phrase sets.
	 *
	 *	@param	phraseSets	The phrase sets to delete.
	 *
	 *	@return				true if phrase sets deleted, false otherwise.
	 */

	public static boolean deletePhraseSets
	(
		PhraseSet[] phraseSets
	)
	{
		boolean result	= true;

		if ( phraseSets != null )
		{
			for ( int i = 0 ; i < phraseSets.length ; i++ )
			{
				result	= result & deletePhraseSet( phraseSets[ i ] );
			}
		}

		return result;
	}

	/**	Delete multiple phrase sets.
	 *
	 *	@param	phraseSets			The phrase sets to delete.
	 *	@param	progressReporter 	A progress reporter.
	 *
	 *	@return						true if phrase sets deleted, false otherwise.
	 */

	public static boolean deletePhraseSets
	(
		PhraseSet[] phraseSets ,
		ProgressReporter progressReporter
	)
	{
		boolean result	= true;

                        		//	Just return if no phrase sets to delete.

		if ( phraseSets == null ) return false;

								//	Set progress display.

		if ( progressReporter != null )
		{
			progressReporter.setMaximumBarValue( phraseSets.length );

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

								//	Loop over phrase sets to delete.

		for ( int i = 0 ; i < phraseSets.length ; i++ )
		{
								//	Get updated title for deleting
								//	the next phrase set.

			PrintfFormat deletingTitleFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"DeletingPhraseSetname" ,
						"Deleting phrase set %s"
					)
				);

			String deletingTitle	=
				deletingTitleFormat.sprintf
				(
					new Object[]{ phraseSets[ i ].toString() }
				);
								//	Update progress display with new title.

			if ( progressReporter != null )
			{
				progressReporter.updateProgress( i , deletingTitle );
			}
								//	Try deleting the next phrase set.

			result	= result & deletePhraseSet( phraseSets[ i ] );

								//	Update progress display.

			if ( progressReporter != null )
			{
				progressReporter.updateProgress( i + 1 , deletingTitle );
			}
		}
								//	Close progress display.

		if ( progressReporter != null )
		{
			progressReporter.close();
		}

		return result;
	}

	/**	Get a phrase set by title.
	 *
	 *	@param	title	The title of the phrase set to fetch.
	 *	@param	owner	The owner of the phrase set to fetch.
	 *
	 *	@return			The phrase set with the requested title,
	 *					or null if not found.
	 */

	public static PhraseSet getPhraseSet( String title , String owner )
	{
		java.util.List list	=
			PersistenceManager.doQuery
			(
				"from PhraseSet ps where ps.title = :title and " +
				"ps.owner = :owner" ,
				new String[]{ "title" , "owner"  } ,
				new Object[]{ title , owner }
			);

		return
			( ( list != null ) && ( !list.isEmpty() ) ) ?
				(PhraseSet)list.get( 0 ) : null;
	}

	/**	Get a phrase set by title.
	 *
	 *	@param	title	The title of the phrase set to fetch.
	 *
	 *	@return			The phrase set with the requested title,
	 *					or null if not found.
	 */

	public static PhraseSet getPhraseSet( String title )
	{
		java.util.List list	=
			PersistenceManager.doQuery
			(
				"from PhraseSet ps where ps.title = :title and " +
				"ps.isPublic = :isPublic" ,
				new String[]{ "title" , "isPublic"  } ,
				new Object[]{ title , Boolean.valueOf( true ) }
			);

		return
			( ( list != null ) && ( !list.isEmpty() ) ) ?
				(PhraseSet)list.get( 0 ) : null;
	}

	/**	Get all available public phrase sets as an array.
	 *
	 *	@return		All available phrase sets as an array of PhraseSet.
	 */

	public static PhraseSet[] getPhraseSets()
	{
		String owner					= WordHoardSettings.getUserID();
		java.util.List phraseSetList	= new ArrayList();

		if ( owner == null )
		{
			phraseSetList	=
				PersistenceManager.doQuery
				(
					"from PhraseSet ps where ps.isPublic = :isPublic" ,
					new String[]{ "isPublic"  } ,
					new Object[]{ Boolean.valueOf( true ) } ,
					false
				);
		}
		else
		{
			phraseSetList	=
				PersistenceManager.doQuery
				(
					"from PhraseSet ps where ps.isPublic = :isPublic" +
					" or ps.owner=:owner" ,
					new String[]{ "isPublic" , "owner" } ,
					new Object[]{ Boolean.valueOf( true ) , owner } ,
					false
				);
		}

		SortedArrayList sortedPhraseSetList =
			new SortedArrayList( phraseSetList );

		return (PhraseSet[])sortedPhraseSetList.toArray( new PhraseSet[]{} );
	}

	/**	Get all available phrase sets for a specified owner as an array.
	 *
	 *	@param		owner			Currently logged in owner.
	 *	@param		onlyPrivate		True to get only private phrase sets for owner.
	 *	@return						All available phrase sets as an array of PhraseSet.
	 */

	public static PhraseSet[] getPhraseSets( String owner , boolean onlyPrivate )
	{
		java.util.List phraseSetList	= null;
        String queryString				= "";

		if ( ( owner == null ) || ( owner.length() == 0 ) ) return null;

		if ( onlyPrivate )
		{
			queryString	=
				"from PhraseSet ps where ps.owner = :owner and ps.isPublic=0";
		}
		else
		{
			queryString	= "from PhraseSet ps where ps.owner = :owner";
		}

		phraseSetList	=
			PersistenceManager.doQuery
			(
				queryString ,
				new String[]{ "owner"  } ,
				new Object[]{ owner } ,
				false
			);

		SortedArrayList sortedPhraseSetList =
			new SortedArrayList( phraseSetList );

		return (PhraseSet[])sortedPhraseSetList.toArray( new PhraseSet[]{} );
	}

	/**	Get all available phrase sets for a specified owner as an array.
	 *
	 *	@param		owner	Currently logged in owner.
	 *
	 *	@return		All available phrase sets as an array of PhraseSet.
	 */

	public static PhraseSet[] getPhraseSets( String owner )
	{
		return getPhraseSets( owner , false );
	}

	/**	Get count of phrase sets for a user.
	 *
	 *	@param		owner			Currently logged in owner.
	 *	@param		onlyPrivate		True to count only private phrase sets for owner.
	 *
	 *	@return						Count of phrase sets owned by "owner".
	 */

	public static int getPhraseSetsCount( String owner , boolean onlyPrivate )
	{
		if ( ( owner == null ) || ( owner.length() == 0 ) ) return 0;

        String queryString		= "";

		if ( onlyPrivate )
		{
			queryString	=
				"from PhraseSet ps " +
				"where ps.owner = :owner and ps.isPublic=0";
		}
		else
		{
			queryString	=
				"from PhraseSet ps where ps.owner = :owner";
		}

		return
			PersistenceManager.doCountQuery
			(
				queryString ,
				new String[]{ "owner"  } ,
				new Object[]{ owner }
			);
	}

	/**	Get phrase sets for logged in user.
	 *
	 *	@return				Phrase sets owned by currently logged in user.
	 */

	public static PhraseSet[] getPhraseSetsForLoggedInUser()
	{
		return udosToPhraseSets
		(
			UserDataObjectUtils.getUserDataObjectsForLoggedInUser
			(
				PhraseSet.class
			)
		);
	}

	/**	Get count of phrase sets for a user.
	 *
	 *	@param		owner	Currently logged in owner.
	 *
	 *	@return				Count of phrase sets owned by "owner".
	 */

	public static int getPhraseSetsCount( String owner )
	{
		return getPhraseSetsCount( owner , false );
	}

	/**	Get count of all available phrase sets.
	 *
	 *	@return		Count of all available phrase sets.
	 */

	public static int getPhraseSetsCount()
	{
		return UserDataObjectUtils.getUserDataObjectsCount
		(
			PhraseSet.class
		);
	}

	/**	Get all available phrases in a phrase set as an array.
	 *
	 *	@param	phraseSet	The phrase set.
	 *
	 *	@return				All available phrases in the phrase set
	 *						as an array of Phrase.
	 *
	 *	<p>
	 *	Returns null if phrase set is null.
	 *	</p>
	 */

	public static Phrase[] getPhrases( PhraseSet phraseSet )
	{
		if ( phraseSet == null ) return null;

								//	Ensure phrase set is loaded.

		if ( !PersistenceManager.doContains( phraseSet ) )
		{
			phraseSet	=
				(PhraseSet)PersistenceManager.doLoad
				(
					PhraseSet.class ,
					phraseSet.getId()
				);

			if ( phraseSet == null ) return null;
		}

		java.util.List phraseList	=
			PersistenceManager.doQuery
			(
				"select ph from PhraseSet ps, Phrase ph " +
				"where ps = :phraseSet and (ph in elements(ps.phrases))" ,
				new String[]{ "phraseSet" } ,
				new Object[]{ phraseSet }
			);

		return (Phrase[])phraseList.toArray( new Phrase[]{} );
	}

	/**	Preload phrase data.
	 *
	 *	@param	phraseSet	The phrase set.
	 *	@return	The phrase list collection.
	 *
	 *	<p>
	 *	Returns null if phrase set is null.
	 *	</p>
	 */

	public static Collection preloadPhrases( PhraseSet phraseSet )
	{
		if ( phraseSet == null ) return null;

								//	Ensure phrase set is loaded.

		if ( !PersistenceManager.doContains( phraseSet ) )
		{
			phraseSet	=
				(PhraseSet)PersistenceManager.doLoad
				(
					PhraseSet.class ,
					phraseSet.getId()
				);

			if ( phraseSet == null ) return null;
		}

		java.util.List phraseList	=
			PersistenceManager.doQuery
			(
				"select ph, wo from PhraseSet ps, Phrase ph, Word wo " +
				"inner join ps.phrases  " +
				"inner join ph.words " +
				"where ps = :phraseSet and (ph in elements(ps.phrases)) " +
				"and (wo in elements(ph.words))" ,
				new String[]{ "phraseSet" } ,
				new Object[]{ phraseSet }
			);

		return phraseList;
	}

	/**	Get all available words in a phrase set as an array.
	 *
	 *	@param	phraseSet	The phrase set.
	 *
	 *	@return				All available unique words in the phrase set
	 *						as an array of Word.
	 *
	 *	<p>
	 *	Returns null if phrase set is null.
	 *	</p>
	 */

	public static Word[] getWords( PhraseSet phraseSet )
	{
		if ( phraseSet == null ) return null;

								//	Make sure phrase set is completely loaded.

		if ( !PersistenceManager.doContains( phraseSet ) )
		{
			phraseSet	=
				(PhraseSet)PersistenceManager.doLoad
				(
					PhraseSet.class ,
					phraseSet.getId()
				);

			if ( phraseSet == null ) return null;
		}

		java.util.List wordsList	=
			PersistenceManager.doQuery
			(
				"select distinct wo from PhraseSet ps, Word wo " +
				"where ps = :phraseSet and (wo in elements(ps.words))" ,
				new String[]{ "phraseSet" } ,
				new Object[]{ phraseSet }
			);

		return (Word[])wordsList.toArray( new Word[]{} );
	}

	/**	Update a phrase set.
	 *
	 *	@param	phraseSet	The phrase set to update.
	 *	@param	title		Title for the phrase set.
	 *	@param	description	Description for the phrase set.
	 *	@param	webPageURL	Web page URL for the phrase set.
	 *	@param	isPublic	True if phrase set is public.
	 *	@param	phrases		Array of Phrase entries for phrase set.
	 *						Set to null to leave current phrase set.
	 *						Set to empty array to remove current phrases.
	 *
	 *	@return				true if update succeed, false otherwise.
	 */

	public static boolean updatePhraseSet
	(
		PhraseSet phraseSet ,
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		Phrase[] phrases
	)
		throws DuplicatePhraseSetException
	{
		boolean result			= false;
		PersistenceManager pm	= null;

		try
		{
			pm	= PersistenceManager.getPM();

			pm.begin();

			phraseSet.setTitle( title );
			phraseSet.setDescription( description );
			phraseSet.setWebPageURL( webPageURL );
			phraseSet.setModificationTime( new Date() );
			phraseSet.setIsPublic( isPublic );

			if ( phrases != null )
			{
				phraseSet.removePhrases();
				phraseSet.addPhrases( phrases );
            }

			pm.commit();

			result	= true;
		}
		catch ( PersistenceException e )
		{
			try
			{
				if ( pm != null ) pm.rollback();
			}
			catch ( PersistenceException e2 )
			{
			}
		}

		return result;
	}

	/**	Update a phrase set.
	 *
	 *	@param	phraseSet	The phrase set to update.
	 *	@param	title		Title for the phrase set.
	 *	@param	description	Description for the phrase set.
	 *	@param	webPageURL	Web page URL for the phrase set.
	 *	@param	isPublic	True if phrase set is public.
	 *	@param	phrases		Collection of Phrase entries for phrase set.
	 *
	 *	@return				true if update succeed, false otherwise.
	 */

	public static boolean updatePhraseSet
	(
		PhraseSet phraseSet ,
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		Collection phrases
	)
		throws DuplicatePhraseSetException
	{
		if ( phrases != null )
		{
			return updatePhraseSet
			(
				phraseSet ,
				title ,
				description ,
				webPageURL ,
				isPublic ,
				(Phrase[])phrases.toArray( new Phrase[]{} )
			);
		}
		else
		{
			return updatePhraseSet
			(
				phraseSet ,
				title ,
				description ,
				webPageURL ,
				isPublic ,
				(Phrase[])null
			);
		}
	}

	/**	Update a phrase set.
	 *
	 *	@param	phraseSet	The phrase set to update.
	 *	@param	title		Title for the phrase set.
	 *	@param	description	Description for the phrase set.
	 *	@param	webPageURL	Web page URL for the phrase set.
	 *	@param	isPublic	True if phrase set is public.
	 *
	 *	@return				true if update succeed, false otherwise.
	 */

	public static boolean updatePhraseSet
	(
		PhraseSet phraseSet ,
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic
	)
		throws DuplicatePhraseSetException
	{
		return updatePhraseSet
		(
			phraseSet ,
			title ,
			description ,
			webPageURL ,
			isPublic ,
			(Phrase[])null
		);
	}

	/**	Get array of all work parts for a phrase set.
	 *
	 *	@param		phraseSet	The phrase set.
	 *
	 *	@return					Array of WorkPart for all worK parts
	 *							represented in phrase set.
	 */

	public static WorkPart[] getWorkParts( PhraseSet phraseSet )
	{
		if ( phraseSet == null ) return null;

								//	Make sure phrase set is completely loaded.

		if ( !PersistenceManager.doContains( phraseSet ) )
		{
			phraseSet	=
				(PhraseSet)PersistenceManager.doLoad
				(
					PhraseSet.class ,
					phraseSet.getId()
				);

			if ( phraseSet == null ) return null;
		}

		return
			WorkUtils.getWorkPartsByTag
			(
				phraseSet.getWorkPartTags()
			);
	}

	/**	Get array of all works for a phrase set.
	 *
	 *	@param		phraseSet	The phrase set.
	 *
	 *	@return					Array of Work for all works
	 *							represented in phrase set.
	 */

	public static Work[] getWorks( PhraseSet phraseSet )
	{
		if ( phraseSet == null ) return null;

								//	Make sure phrase set is completely loaded.

		if ( !PersistenceManager.doContains( phraseSet ) )
		{
			phraseSet	=
				(PhraseSet)PersistenceManager.doLoad
				(
					PhraseSet.class ,
					phraseSet.getId()
				);

			if ( phraseSet == null ) return null;
		}

		return
			WorkUtils.getWorksByTag( phraseSet.getWorkTags() );
	}

	/**	Get total word form count for one work represented in a phrase set.
	 *
	 *	@param	phraseSet		The phrase set.
	 *	@param	wordForm		The word form.
	 *	@param	work			The work.
	 *
	 *	@return					Count of the word form in the phrase set.
	 */

	public static int getWordFormCount
	(
		PhraseSet phraseSet ,
		int wordForm ,
		Work work
	)
	{
		int result	= 0;

		java.util.List phraseSetWordFormCounts	=
			PersistenceManager.doQuery
			(
				"from PhraseSetTotalWordFormCount twfc where " +
					"twfc.phraseSet = :phraseSet and " +
					"twfc.wordForm = :wordForm and " +
					"twfc.work = :work" ,
				new String[]
				{
					"phraseSet" ,
					"wordForm" ,
					"work"
				} ,
				new Object[]
				{
					phraseSet ,
					Integer.valueOf( wordForm ) ,
					work
				} ,
				true
			);
								//	Pick up count.

		if ( phraseSetWordFormCounts != null )
		{
			Iterator iterator	= phraseSetWordFormCounts.iterator();

			while ( iterator.hasNext() )
			{
				WordSetTotalWordFormCount wwfc	=
					(WordSetTotalWordFormCount)iterator.next();

				result	+= wwfc.getWordFormCount();
			}
		}

		return result;
	}

	/**	Get total word form count in a phrase set.
	 *
	 *	@param	phraseSet		The phrase set.
	 *	@param	wordForm	The word form.
	 *
	 *	@return				Count of the word form in the phrase set.
	 */

	public static int getWordFormCount
	(
		PhraseSet phraseSet ,
		int wordForm
	)
	{
		int result	= 0;

		java.util.List phraseSetWordFormCounts	=
			PersistenceManager.doQuery
			(
				"from PhraseSetTotalWordFormCount twfc where " +
					"twfc.phraseSet = :phraseSet and " +
					"twfc.wordForm = :wordForm" ,
				new String[]
				{
					"phraseSet" ,
					"wordForm"
				} ,
				new Object[]
				{
					phraseSet ,
					Integer.valueOf( wordForm ) ,
				} ,
				true
			);
								//	Pick up count.

		if ( phraseSetWordFormCounts != null )
		{
			Iterator iterator	= phraseSetWordFormCounts.iterator();

			while ( iterator.hasNext() )
			{
				WordSetTotalWordFormCount wwfc	=
					(WordSetTotalWordFormCount)iterator.next();

				result	+= wwfc.getWordFormCount();
			}
		}

		return result;
	}

	/**	Get distinct word form count in a phrase set.
	 *
	 *	@param	phraseSet	The phrase set.
	 *	@param	wordForm	The word form.
	 *
	 *	@return				Count of the distinct word forms
	 *						in the phrase set.
	 */

	public static int getDistinctWordFormCount
	(
		PhraseSet phraseSet ,
		int wordForm
	)
	{
		int result	= 0;

		java.util.List phraseSetWordFormCounts	=
			PersistenceManager.doQuery
			(
				"select count(*) from WordSetWordCount wc where " +
					"wc.wordSet = :phraseSet and " +
					"wc.wordForm = :wordForm" ,
				new String[]
				{
					"wordSet" ,
					"wordForm"
				} ,
				new Object[]
				{
					phraseSet ,
					Integer.valueOf( wordForm ) ,
				} ,
				true
			);
								//	Pick up count.

		if ( phraseSetWordFormCounts != null )
		{
			Iterator iterator	= phraseSetWordFormCounts.iterator();

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

	/**	Get word count in a phrase set.
	 *
	 *	@param	phraseSet	The phrase set.
	 *	@param	word		The word.
	 *	@param	wordForm	The word form.
	 *
	 *	@return				Count of the word form in the phrase set.
	 */

	public static int getWordFormCount
	(
		PhraseSet phraseSet ,
		Spelling word ,
		int wordForm
	)
	{
		int result	= 0;

		java.util.List phraseSetWordFormCounts	=
			PersistenceManager.doQuery
			(
				"from WordSetWordCount wc where " +
					"wc.wordSet = :phraseSet and " +
					"wc.wordForm = :wordForm and " +
					"wc.word = :word" ,
				new String[]
				{
					"phraseSet" ,
					"wordForm" ,
					"word"
				} ,
				new Object[]
				{
					phraseSet ,
					Integer.valueOf( wordForm ) ,
					word
				} ,
				true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( phraseSetWordFormCounts != null )
		{
			Iterator iterator	= phraseSetWordFormCounts.iterator();

			while( iterator.hasNext() )
			{
				WordSetWordCount wc	=
					(WordSetWordCount)iterator.next();

				result	+= wc.getWordCount();
			}
		}

		return result;
	}

	/**	Get word count in a phrase set for a specific work.
	 *
	 *	@param	phraseSet		The phrase set.
	 *	@param	word		The word.
	 *	@param	wordForm	The word form.
	 *	@param	work		The work.
	 *
	 *	@return				Count of the word form in the phrase set.
	 */

	public static int getWordFormCount
	(
		PhraseSet phraseSet ,
		Spelling word ,
		int wordForm ,
		Work work
	)
	{
		int result	= 0;

		java.util.List phraseSetWordFormCounts	=
			PersistenceManager.doQuery
			(
				"from WordSetWordCount wc where " +
					"wc.wordSet = :phraseSet and " +
					"wc.wordForm = :wordForm and " +
					"wc.word = :word and " +
					"wc.work = :work" ,
				new String[]
				{
					"phraseSet" ,
					"wordForm" ,
					"word" ,
					"work"
				} ,
				new Object[]
				{
					phraseSet ,
					Integer.valueOf( wordForm ) ,
					word ,
					work
				} ,
				true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( phraseSetWordFormCounts != null )
		{
			Iterator iterator	= phraseSetWordFormCounts.iterator();

			while( iterator.hasNext() )
			{
				WordSetWordCount wc	=
					(WordSetWordCount)iterator.next();

				result	+= wc.getWordCount();
			}
		}

		return result;
	}

	/**	Get word count for multiple words in a phrase set.
	 *
	 *	@param	phraseSet		The phrase set.
	 *	@param	words			The words.
	 *	@param	wordForm		The word form.
	 *
	 *	@return					Map with words as keys and counts of each word
	 *							in the phrase set as values.
	 */

	public static Map getWordFormCount
	(
		PhraseSet phraseSet ,
		Spelling[] words ,
		int wordForm
	)
	{
		TreeMap result	= new TreeMap();

		if ( words.length == 0 ) return result;

		String queryString	=
		"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
		"(wc.word , sum(wc.wordCount)) " +
			"from WordSetWordCount wc where " +
			"wc.word in (:words) and " +
			"wc.wordForm = :wordForm and " +
			"wc.wordSet = :phraseSet " +
			"group by wc.word";

		String[] paramNames		=
			new String[]
			{
				"words" ,
				"wordForm" ,
				"phraseSet"
			};

		Object[] paramValues	=
			new Object[]
			{
				Arrays.asList( words ) ,
				Integer.valueOf( wordForm ) ,
				phraseSet
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

	/**	Get word count for multiple words in a set of phrase sets.
	 *
	 *	@param	phraseSets		The phrase sets.
	 *	@param	words			The words.
	 *	@param	wordForm		The word form.
	 *
	 *	@return					Map with words as keys and counts of each word
	 *							in the phrase sets as values.
	 */

	public static Map getWordFormCount
	(
		PhraseSet[] phraseSets ,
		Spelling[] words ,
		int wordForm
	)
	{
		TreeMap result	= new TreeMap();

		if ( ( phraseSets.length == 0 ) || ( words.length == 0 ) ) return result;

		String queryString	=
		"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
		"(wc.word , sum(wc.wordCount)) " +
			"from WordSetWordCount wc where " +
			"wc.word in (:words) and " +
			"wc.wordForm = :wordForm and " +
			"wc.wordSet in (:phraseSets) " +
			"group by wc.word";

		String[] paramNames		=
			new String[]
			{
				"words" ,
				"wordForm" ,
				"phraseSets"
			};

		Object[] paramValues	=
			new Object[]
			{
				Arrays.asList( words ) ,
				Integer.valueOf( wordForm ) ,
				Arrays.asList( phraseSets )
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

	/**	Get word counts in a single phrase set.
	 *
	 *	@param	phraseSet	The phrase set.
	 *	@param	wordForm	The word form to count.
	 *
	 *	@return				Map containing each word in the phrase set
	 *						as a key and the count of the word as the value.
	 */

	public static Map getWordCounts( PhraseSet phraseSet , int wordForm )
	{
		TreeMap wordCounts	= new TreeMap();

		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
			"(wc.word , sum(wc.wordCount)) " +
			"from WordSetWordCount wc where " +
			"wc.wordSet = :phraseSet and " +
			"wc.wordForm = :wordForm " +
			"group by wc.word";

		java.util.List phraseSetWordCounts	=
			PersistenceManager.doQuery
			(
				queryString ,
				new String[]{ "phraseSet" , "wordForm" } ,
				new Object[]{ phraseSet , Integer.valueOf( wordForm ) } ,
				true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( phraseSetWordCounts != null )
		{
			Iterator iterator	= phraseSetWordCounts.iterator();

			while ( iterator.hasNext() )
			{
				CountResult wordCount	= (CountResult)iterator.next();

				wordCounts.put
				(
					wordCount.word.toInsensitive() ,
					Integer.valueOf( wordCount.count )
				);
			}
		}

		return wordCounts;
	}

	/**	Get phrase counts in a single phrase set.
	 *
	 *	@param	phraseSet	The phrase set.
	 *	@param	wordForm	The word form to count.
	 *
	 *	@return				Map containing each phrase in the phrase set
	 *						as a key and the count of the phrase as the value.
	 */

	public static Map getPhraseCounts( PhraseSet phraseSet , int wordForm )
	{
		TreeMap phraseCounts	= new TreeMap();

		String queryField		= null;

		switch ( wordForm )
		{
			case WordForms.SPELLING :
				queryField	= "spellings";
				break;

			case WordForms.LEMMA :
				queryField	= "lemmata";
				break;

			case WordForms.WORDCLASS :
				queryField	= "partsOfSpeech";
				break;

			default: ;
		}

		if ( queryField == null )
		{
			return phraseCounts;
		}

		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
			"(ph." + queryField + ", count(ph." + queryField + ")) " +
			"from PhraseSet ps, Phrase ph where " +
			"ps.phraseSet = :phraseSet and " +
			"(ph in elements(ps.phrases)) " +
			"group by ph." + queryField;

		java.util.List phraseSetWordCounts	=
			PersistenceManager.doQuery
			(
				queryString ,
				new String[]{ "phraseSet" } ,
				new Object[]{ phraseSet } ,
				true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( phraseSetWordCounts != null )
		{
			Iterator iterator	= phraseSetWordCounts.iterator();

			while ( iterator.hasNext() )
			{
				CountResult phraseCount	= (CountResult)iterator.next();

				phraseCounts.put
				(
					phraseCount.word.toInsensitive() ,
					Integer.valueOf( phraseCount.count )
				);
			}
		}

		return phraseCounts;
	}

	/**	Get word form counts in a set of phrase sets.
	 *
	 *	@param	phraseSets		The phrase sets.
	 *	@param	wordForm		The word form to count.
	 *	@param	getWorkCounts	if true, work counts are returned in the second
	 *							result map (see below).  If false, hashsets of
	 *							work IDs are returned in the second result map.
	 *
	 *	@return					Array of two maps.  The first map contains
	 *							each word of then specified word form
	 *							in the phrase sets as a key and
	 *							the count of the appearance of the word
	 *							in the phrase sets as a value.  The second map
	 *							also has the word as the key.  If "getWorkCounts"
	 *							is true, the values for each word are the counts
	 *							of the works in which the word appears.  If
	 *							"getWorkCounts" is false, the value is a hash set
	 *							of the word IDs for each work in which the word
	 *							appears.
	 */

	public static Map[] getWordCounts
	(
		PhraseSet[] phraseSets ,
		int wordForm ,
		boolean getWorkCounts
	)
	{
		TreeMap wordCounts	= new TreeMap();
		TreeMap workCounts	= new TreeMap();

		if ( phraseSets.length == 0 )
		{
			return new TreeMap[]{ wordCounts , workCounts };
		}

		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
			"(wc.word , wc.wordCount , wc.work.id) " +
			"from WordSetWordCount wc where " +
			"wc.wordForm = :wordForm and " +
			"wc.wordSet in (:phraseSets)";

		String[] paramNames		=
			new String[]
			{
				"wordForm" ,
				"phraseSets"
			};

		Object[] paramValues	=
			new Object[]
			{
				Integer.valueOf( wordForm ) ,
				Arrays.asList( phraseSets )
			};

		java.util.List phraseSetWordCounts	=
			PersistenceManager.doQuery
			(
				queryString , paramNames , paramValues , true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( phraseSetWordCounts != null )
		{
			Iterator iterator	= phraseSetWordCounts.iterator();

			while ( iterator.hasNext() )
			{
				CountResult countResult	= (CountResult)iterator.next();

				Spelling wordText	= countResult.word.toInsensitive();
				int newCount		= countResult.count;
				Long workID			= countResult.work;

								//	Increment word count.

				if ( wordCounts.containsKey( wordText ) )
				{
					Integer count	= (Integer)wordCounts.get( wordText );

					wordCounts.put
					(
						wordText ,
						Integer.valueOf( newCount + count.intValue() )
					);
				}
				else
				{
					wordCounts.put( wordText , Integer.valueOf( newCount ) );
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

	public static Map[] getWordCounts( PhraseSet[] phraseSets , int wordForm )
	{
		return getWordCounts( phraseSets , wordForm , true );
	}

	/**	Get word form counts in a set of phrase sets.
	 *
	 *	@param	phraseSets		The phrase sets.
	 *	@param	wordForm		The word form to count.
	 *	@param	getWorkCounts	if true, work counts are returned in the second
	 *							result map (see below).  If false, hashsets of
	 *							work IDs are returned in the second result map.
	 *
	 *	@return					Array of two maps.  The first map contains
	 *							each phrase of the specified word form
	 *							in the phrase sets as a key and
	 *							the count of the appearance of the phrase
	 *							in the phrase sets as a value.  The second map
	 *							also has the phrase as the key.  If "getWorkCounts"
	 *							is true, the values for each phrase are the counts
	 *							of the works in which the phrase appears.  If
	 *							"getWorkCounts" is false, the value is a hash set
	 *							of the word IDs for each work in which the phrase
	 *							appears.
	 */

	public static Map[] getPhraseCounts
	(
		PhraseSet[] phraseSets ,
		int wordForm ,
		boolean getWorkCounts
	)
	{
		TreeMap wordCounts	= new TreeMap();
		TreeMap workCounts	= new TreeMap();

		if ( phraseSets.length == 0 )
		{
			return new TreeMap[]{ wordCounts , workCounts };
		}

		String queryField		= null;

		switch ( wordForm )
		{
			case WordForms.SPELLING :
				queryField	= "spellings";
				break;

			case WordForms.LEMMA :
				queryField	= "lemmata";
				break;

			case WordForms.WORDCLASS :
				queryField	= "partsOfSpeech";
				break;

			default : ;
		}

		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
			"(ph." + queryField + ", count(ph." + queryField + "), " +
			"ph.work.id) " +
			"from PhraseSet ps, Phrase ph where " +
			"ps in (:phraseSets) and " +
			"(ph in elements(ps.phrases)) " +
			"group by ph." + queryField;

		java.util.List phraseSetWordCounts	=
			PersistenceManager.doQuery
			(
				queryString ,
				new String[]{ "phraseSets" } ,
				new Object[]{ Arrays.asList( phraseSets ) } ,
				true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( phraseSetWordCounts != null )
		{
			Iterator iterator	= phraseSetWordCounts.iterator();

			while ( iterator.hasNext() )
			{
				CountResult countResult	= (CountResult)iterator.next();

				Spelling wordText	= countResult.word.toInsensitive();
				int newCount		= countResult.count;
				Long workID			= countResult.work;

								//	Increment word count.

				if ( wordCounts.containsKey( wordText ) )
				{
					Integer count	= (Integer)wordCounts.get( wordText );

					wordCounts.put
					(
						wordText ,
						Integer.valueOf( newCount + count.intValue() )
					);
				}
				else
				{
					wordCounts.put( wordText , Integer.valueOf( newCount ) );
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
								//	each word if requested.

		if ( getWorkCounts ) CountMapUtils.worksToWorkCounts( workCounts );

		return new TreeMap[]{ wordCounts , workCounts };
	}

	/**	Get word form counts in a set of phrase sets.
	 *
	 *	@param	phraseSets		The phrase sets.
	 *	@param	wordForm		The word form to count.
	 *
	 *	@return					Array of two maps.  The first map contains
	 *							each word of then specified word form
	 *							in the phrase sets as a key and
	 *							the count of the appearance of the word
	 *							in the phrase sets as a value.  The second map
	 *							also has the word as the key but
	 *							provides the number of parent works for the
	 *							phrase sets in which the word appears as a value.
	 */

	public static Map[] getPhraseCounts
	(
		PhraseSet[] phraseSets ,
		int wordForm
	)
	{
		TreeMap wordCounts	= new TreeMap();
		TreeMap workCounts	= new TreeMap();

		if ( phraseSets.length == 0 )
		{
			return new TreeMap[]{ wordCounts , workCounts };
		}

		String queryField		= null;

		switch ( wordForm )
		{
			case WordForms.SPELLING :
				queryField	= "spellings.string";
				break;

			case WordForms.LEMMA :
				queryField	= "lemmata.string";
				break;

			case WordForms.WORDCLASS :
				queryField	= "partsOfSpeech";
				break;

			default : ;
		}

		String queryString	=
			"select new edu.northwestern.at.wordhoard.swing.calculator.modelutils.CountResult" +
			"(ph." + queryField + ", count(ph." + queryField + "), " +
			"count(distinct ph.work.id)) " +
			"from PhraseSet ps, Phrase ph where " +
			"ps in (:phraseSets) and " +
			"(ph in elements(ps.phrases)) " +
			"group by ph." + queryField;

		java.util.List phraseSetWordCounts	=
			PersistenceManager.doQuery
			(
				queryString ,
				new String[]{ "phraseSets" } ,
				new Object[]{ Arrays.asList( phraseSets ) } ,
				true
			);
								//	If we got results, put them in the
								//	tree map.

		if ( phraseSetWordCounts != null )
		{
			Iterator iterator	= phraseSetWordCounts.iterator();

			while ( iterator.hasNext() )
			{
				CountResult countResult	= (CountResult)iterator.next();

				Spelling wordText		= countResult.word.toInsensitive();

				wordCounts.put
				(
					wordText ,
					Integer.valueOf( countResult.count )
				);

				workCounts.put
				(
					wordText ,
					Integer.valueOf( countResult.workCount )
				);
			}
		}

		return new TreeMap[]{ wordCounts , workCounts };
	}

	/**	Get phrase count in a phrase set for a specific phrase in a work.
	 *
	 *	@param	phraseSet		The phrase set.
	 *	@param	phrase			The phrase text.
	 *	@param	wordForm		The word form.
	 *	@param	work			The work.
	 *
	 *	@return					Count of the word form in the phrase set.
	 */

	public static int getWordFormPhraseCount
	(
		PhraseSet phraseSet ,
		Spelling phrase ,
		int wordForm ,
		Work work
	)
	{
		int result				= 0;
		String queryField		= null;

		switch ( wordForm )
		{
			case WordForms.SPELLING :
				queryField	= "spellings";
				break;

			case WordForms.LEMMA :
				queryField	= "lemmata";
				break;

			case WordForms.WORDCLASS :
				queryField	= "partsOfSpeech";
				break;

			default : ;
		}

		String queryString	=
			"select count(*) from PhraseSet ps, Phrase ph where " +
			"ps = :phraseSet and " +
			"ph." + queryField + " = :phrase and " +
			"ph.work = :work and " +
			"(ph in elements(ps.phrases)) ";

		java.util.List phraseCount	=
			PersistenceManager.doQuery
			(
				queryString ,
				new String[]{ "phraseSet" , "phrase" , "work" } ,
				new Object[]{ phraseSet , phrase , work }
			);
								//	If we got results, put them in the
								//	tree map.

		if ( phraseCount != null )
		{
			Iterator iterator	= phraseCount.iterator();

			if ( iterator.hasNext() )
			{
				result	= (int)((Long)iterator.next()).longValue();
			}
		}

		return result;
	}

	/**	Get word occurrences for a word in a specified phrase set.
	 *
	 *	@param	phraseSet	The phrase set.
	 *	@param	wordForm    The word form.
	 *	@param	word		The word to look up.
	 *
	 *	@return				Array of Word entries for selected word in phrase set.
	 */

	public static Word[] getWordOccurrences
	(
		PhraseSet phraseSet ,
		int wordForm ,
		Spelling word
	)
	{
		String queryString	= "";

		Word[] words;

		if ( wordForm == WordForms.SPELLING )
		{
			String[] spellingAndCompoundWordClass	=
				WordUtils.extractSpellingAndCompoundWordClass( word.getString() );

			queryString	=
				"from Word wo, PhraseSet ps " +
				"where (ps = :phraseSet) and " +
				"wo.spellingInsensitive.string=:spelling and " +
				WordUtils.createCompoundWordClassQueryString(
					spellingAndCompoundWordClass[ 1 ] ) +
				" and (wo in elements(ps.words))";

			words	=
				WordUtils.performWordQuery
				(
					queryString ,
					new String[]{ "phraseSet" , "spelling" } ,
					new Object[]{ phraseSet , spellingAndCompoundWordClass[ 1 ] }
				);
		}
		else
		{
/*
			queryString	=
				"select wo from Word wo, LemmaOccurrence lo, PhraseSet ps" +
				" where (ps = :phraseSet) and" +
				" (lo.tag = wo.tag) and" +
				" (lo.morphBase.lemPos.lemma.tag = :tag) and" +
				" (wo in elements(ps.words))";
*/
			queryString	=
				"select wo from Word wo, WordPart wp, PhraseSet ps " +
				" where " +
				"wp.word = wo " +
				"and wp.lemPos.lemma.tagInsensitive.string=:tag and " +
				"(wo in elements(ps.words))";

			words	=
				WordUtils.performWordQuery
				(
					queryString ,
					new String[]{ "phraseSet" , "tag" } ,
					new Object[]{ phraseSet , word.getString() }
				);
		}

		return words;
	}

	/**	Get surrounding words of a specified word in a phrase set.
	 *
	 *	@param	phraseSet				The phrase set.
	 *	@param	word				Word for which to get span.
	 *	@param	leftSpan			# of words to left of
	 *								specified word to retrieve.
	 *	@param	rightSpan			# of words to right of
	 *								specified word to retrieve.
	 *
 	 *	@return						Span of words in the phrase set for
	 *								the specified word.
	 */

	public static Word[] getSpan
	(
		PhraseSet phraseSet ,
		Word word ,
		int leftSpan ,
		int rightSpan
	)
	{
		return
			WordUtils.performWordQuery
			(
				"select collocate from Word collocate," +
				" Word word, PhraseSet ps where" +
				" (ps = :phraseSet) and" +
				" ( word = :word ) and" +
				" collocate.colocationOrdinal between " +
				" ( word.colocationOrdinal - :leftSpan ) and" +
				" ( word.colocationOrdinal + :rightSpan ) and" +
				" ( collocate in elements(ps.words))" ,
				new String[]{ "phraseSet" , "word" , "leftSpan" , "rightSpan" } ,
				new Object[]
				{
					phraseSet ,
					word ,
					Integer.valueOf( leftSpan ) ,
					Integer.valueOf( rightSpan)
				}
			);
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected PhraseSetUtils()
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

