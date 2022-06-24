package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;

/**	Word occurrence utilities.
 */

public class WordUtils
{
	/**	Punctuation map. */

	protected static TreeMap punctuationMap	= new TreeMap();

	/**	Word class to major word class map. */

	protected static HashMap wordClassToMajorWordClassMap	= null;

	/**	Perform word query.
	 *
	 *	@param	queryString		The query string.
	 *	@param	paramNames		Parameter names used in the query.
	 *	@param	paramValues		Parameter values for each paramName.
	 *
	 *	@return					Array of Word entries.
	 */

	public static Word[] performWordQuery
	(
		String queryString ,
		String[] paramNames ,
		Object[] paramValues
	)
	{
		Word[] result	= null;

								//	Get the list of query results.

		java.util.List wordList	=
			PersistenceManager.doQuery(
				queryString , paramNames , paramValues );

								//	If we got results, move them to an array
								//	of Word.

		if ( wordList != null )
		{
			result	= (Word[])wordList.toArray( new Word[]{} );
		}

		return result;
	}

	/**	Gets word occurrences by tag.
	 *
	 *	@param	tags		Collection of word tags.
	 *
	 *	@return				The word occurrences with the specified tags,
	 *						or null if none found.
	 *
	 *	@throws	PersistenceException
	 */

	public static Word[] getWordsByTags( Collection tags )
	{
		return performWordQuery
		(
			"from Word wo where wo.tag in (:tags)",
			new String[]{ "tags" },
			new Object[]{ tags }
		);
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

	public static Word[] getSpan
	(
		Word word ,
		int leftSpan ,
		int rightSpan
	)
	{
		return
			performWordQuery
			(
				"select collocate from Word collocate, Word word where" +
				" word = :word and" +
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
	}

	/**	Get surrounding words of a specified word.
	 *
	 *	@param	word				Word for which to get span,
	 *								i.e., the anchor word.
	 *	@param	leftSpan			# of words to left of
	 *								specified word to retrieve.
	 *	@param	rightSpan			# of words to right of
	 *								specified word to retrieve.
	 *
 	 *	@return						Span of words around
	 *								specified word.
	 *
	 *	<p>
	 *	This operates like getSpan above, but uses object model
	 *	traversal instead of a database lookup.  The assumption
	 *	is that the relevant word objects have already been loaded
	 *	into the cache.
	 *	</p>
	 */

	public static Word[] getSpanFromCache
	(
		Word word ,
		int leftSpan ,
		int rightSpan
	)
	{
								//	Hold word spans to left and right of
								//	anchor word.

		ArrayList leftSpanList	= new ArrayList();
		ArrayList rightSpanList	= new ArrayList();

		if ( word != null )
		{
								//	Get word following anchor word.

			Word nextWord	= word.getNext();

								//	Add the words to the right of
								//	the anchor word to the end of
								//	the list.

			if ( nextWord != null )
			{
				for ( int j = 0 ; j < rightSpan ; j++ )
				{
					rightSpanList.add( nextWord );
					nextWord	= nextWord.getNext();
					if ( nextWord == null ) break;
				}
			}
								//	Get word preceding anchor word.

			Word prevWord	= word.getPrev();

								//	While there are previous words
								//	in the left span, add these to
								//	the front of the word list.

			if ( prevWord != null )
			{
				for ( int j = 0 ; j < leftSpan ; j++ )
				{
					leftSpanList.add( 0 , prevWord );
					prevWord	= prevWord.getPrev();
					if ( prevWord == null ) break;
				}
			}
		}
								//	The lengfth

		ArrayList spanList	= new ArrayList();

								//	Convert word list to Word[] array.

		Word[] spanWords	= new Word[ 0 ];

		if ( spanList.size() > 0 )
		{
			spanWords	= (Word[])spanList.toArray( new Word[]{} );
		}

		return spanWords;
	}

	/**	Get span of words to left of a specified word.
	 *
	 *	@param	word				Word for which to get left span,
	 *								i.e., the anchor word.
	 *	@param	leftSpan			# of words to left of
	 *								specified word to retrieve.
	 *
 	 *	@return						List of words to left of
	 *								specified word.
	 */

	public static java.util.List getLeftSpan
	(
		Word word ,
		int leftSpan
	)
	{
								//	Hold word span to left of
								//	anchor word.

		ArrayList leftSpanList	= new ArrayList();

		if ( word != null )
		{
								//	Get word preceding anchor word.

			Word prevWord	= word.getPrev();

								//	While there are previous words
								//	in the left span, add these to
								//	the front of the word list.

			if ( prevWord != null )
			{
				for ( int j = 0 ; j < leftSpan ; j++ )
				{
					leftSpanList.add( 0 , prevWord );
					prevWord	= prevWord.getPrev();
					if ( prevWord == null ) break;
				}
			}
		}

		return leftSpanList;
	}

	/**	Get span of words to right of a specified word.
	 *
	 *	@param	word				Word for which to get right span,
	 *								i.e., the anchor word.
	 *	@param	rightSpan			# of words to right of
	 *								specified word to retrieve.
	 *
 	 *	@return						List of words to right of
	 *								specified word.
	 */

	public static java.util.List getRightSpan
	(
		Word word ,
		int rightSpan
	)
	{
								//	Hold word span to right of
								//	anchor word.

		ArrayList rightSpanList	= new ArrayList();

		if ( word != null )
		{
								//	Get word following anchor word.

			Word nextWord	= word.getNext();

								//	While there are more words
								//	in the right span, add these to
								//	the end of the word list.

			if ( nextWord != null )
			{
				for ( int j = 0 ; j < rightSpan ; j++ )
				{
					rightSpanList.add( nextWord );
					nextWord	= nextWord.getNext();
					if ( nextWord == null ) break;
				}
			}
		}

		return rightSpanList;
	}

	/**	Parse spelling and compound word class string.
	 *
	 *	@param	spellingAndCompoundWordClass	The spelling and compound
	 *											word class to look up.
	 *											Must be in the form
	 *											"spelling (wordclass)" .
	 *
	 *	@return				A two element String array.  The first element
	 *						is the spelling and the second is the
	 *						compound word class string.
	 */

	public static String[] extractSpellingAndCompoundWordClass
	(
		String spellingAndCompoundWordClass
	)
	{
		String spelling	=
			stripWordClass( spellingAndCompoundWordClass );

		int iPos		= spellingAndCompoundWordClass.indexOf( '(' );
		int jPos		= spellingAndCompoundWordClass.indexOf( ')' );

		String compoundWordClass	=
			spellingAndCompoundWordClass.substring( iPos + 1 , jPos );

		return new String[]{ spelling , compoundWordClass };
	}

	/**	Create query string portion for looking up a compound word class.
	 *
	 *	@param	compoundWordClass		The compound word class.
	 *
	 *	@return							A query string portion for looking
	 *									up a matching set of word classes.
	 */

	public static String createCompoundWordClassQueryString
	(
		String compoundWordClass
	)
	{
		String[] wordClass	=
			StringUtils.makeTokenArray( compoundWordClass , "-" );

		StringBuffer queryString	= new StringBuffer();

		for ( int i = 0 ; i < wordClass.length ; i++ )
		{
			if ( queryString.length() > 0 )
			{
				queryString	= queryString.append( " and " );
			}

			queryString	=
				queryString.append
				(
					" wo.wordParts[" + i +
					"].lemPos.pos.wordClass.tag='" + wordClass[ i ] + "'"
				);
		}

		return queryString.toString();
	}

	/**	Perform a spelling query.
	 *
	 *	@param	spelling 	Spelling to look up.
	 *	@param	workParts	The work/work parts to search.
	 */

	public static Word[] getSpellingOccurrences
	(
		Spelling spelling ,
		WorkPart[] workParts
	)
	{
		String queryString		= "";

		String[] spellingAndCompoundWordClass	=
			extractSpellingAndCompoundWordClass( spelling.getString() );

//$$$PIB$$$ Remove homonym markers from word class values here?
		String[] wordClasses	=
			StringUtils.makeTokenArray(
				spellingAndCompoundWordClass[ 1 ] , "-" );

		queryString				=
			"from Word wo " +
			"where wo.spellingInsensitive.string=:spelling and " +
			"wo.wordParts[0].lemPos.pos.wordClass.tag=:wordClassTag " +
			"and ( wo.work in (:workParts) or " +
			"wo.workPart in (:workParts) )";

		java.util.List wordList	=
			PersistenceManager.doQuery
			(
				queryString ,
				new String[]{ "workParts" , "spelling" , "wordClassTag" } ,
				new Object[]
				{
					Arrays.asList( workParts ) ,
					CharsetUtils.translateToInsensitive(
						spellingAndCompoundWordClass[ 0 ] ) ,
					wordClasses[ 0 ]
				}
			);

		if ( wordClasses.length > 1 )
		{
			Iterator iterator	= wordList.iterator();

			while ( iterator.hasNext() )
			{
				Word word		= (Word)iterator.next();

				String cPos		= WordUtils.getCompoundWordClass( word );

				if ( !cPos.equals( spellingAndCompoundWordClass[ 1 ] ) )
				{
					iterator.remove();
				}
			}
		}

		return (Word[])wordList.toArray( new Word[]{} );
	}

	/**	Perform a lemma query.
	 *
	 *	@param	lemma	 	Lemma to look up.
	 *	@param	workParts	The work/work parts to search.
	 */

	public static Word[] getLemmaOccurrences
	(
		Spelling lemma ,
		WorkPart[] workParts
	)
	{
		String caseInsensitiveLemma	=
			CharsetUtils.translateToInsensitive(
				lemma.getString() );

		String queryString	=
			"select wo from Word wo, WordPart wp " +
			" where " +
			"(wo.workPart in (:workParts) or wo.work in (:workParts))" +
			"and wp.word = wo " +
			"and wp.lemPos.lemma.tagInsensitive.string=:tag";

		return
			performWordQuery
			(
				queryString ,
				new String[]{ "workParts" , "tag" } ,
				new Object[]
				{
					Arrays.asList( workParts ) ,
					caseInsensitiveLemma
				}
			);
	}

	/**	Get word occurrences for a word in specified work parts.
	 *
	 *	@param	workParts	The work parts.
	 *	@param	wordForm    The word form.
	 *	@param	word		The word to look up.
	 *
	 *	@return				Array of Word entries for word in work part.
	 */

	public static Word[] getWordOccurrences
	(
		WorkPart[] workParts ,
		int wordForm ,
		Spelling word
	)
	{
		Word[] result;

		if ( wordForm == WordForms.SPELLING )
		{
			result	= getSpellingOccurrences( word , workParts );
		}
		else
		{
			result	= getLemmaOccurrences( word , workParts );
		}

		return result;
	}

	/**	Get word occurrences for a word in a specified work part.
	 *
	 *	@param	workPart	The work part.
	 *	@param	wordForm    The word form.
	 *	@param	word		The word to look up.
	 *
	 *	@return				Array of Word entries for word in work part.
	 */

	public static Word[] getWordOccurrences
	(
		WorkPart workPart ,
		int wordForm ,
		Spelling word
	)
	{
		return getWordOccurrences
		(
			new WorkPart[]{ workPart } ,
			wordForm ,
			word
		);
	}

	/**	Get word occurrences for a word in a specified corpus.
	 *
	 *	@param	corpus		The corpus.
	 *	@param	wordForm    The word form.
	 *	@param	word		The word to look up.
	 *
	 *	@return				Array of Word entries for word in corpus.
	 */

	public static Word[] getWordOccurrences
	(
		Corpus corpus ,
		int wordForm ,
		Spelling word
	)
	{
		WorkPart[] works	=
			(WorkPart[])corpus.getWorks().toArray( new WorkPart[]{} );

		return getWordOccurrences( works , wordForm , word );
	}

	/**	Get word occurrences for a word in a specified work set.
	 *
	 *	@param	workSet		The work set.
	 *	@param	wordForm    The word form.
	 *	@param	word		The word to look up.
	 *
	 *	@return				Array of Word entries for word in work set.
	 */

	public static Word[] getWordOccurrences
	(
		WorkSet workSet ,
		int wordForm ,
		Spelling word
	)
	{
		return getWordOccurrences
		(
			(WorkPart[])WorkSetUtils.getWorkParts( workSet ) ,
			wordForm ,
			word
		);
	}

	/**	Get displayable html text for an array of adjacent Words.
	 *
	 *	@param	words					Array of Words to display.
	 *	@param	displayLemmaForms		Display lemma forms instead of spellings.
	 *	@param	eolChars				String for end of line.
	 *	@param	highlightWords  		Highlight words in this list.
	 *	@param	highlightBracket		Interval in which to highlight words.
	 *	@param	highlightsAreLemmata	True if highlight words are lemma forms.
	 *	@param	wordSet					Word set containing collection of words
	 *									which are actually part of the context.
	 *									Context words not in the word set will
	 *									display with strike-through marking.
	 *									May be null.
	 *
	 *	@return							Word occurrence text as displayable xhtml.
	 */

	public static String getDisplayableText
	(
		Word[] words ,
		boolean displayLemmaForms ,
		String eolChars ,
		String[] highlightWords ,
		int[] highlightBracket ,
		boolean highlightsAreLemmata ,
		WordSet wordSet
	)
	{
								//	Accumulates the displayable text.

		StringBuffer sb			= new StringBuffer();

								//	Holds previous line number.

		int prevLineNumber		= 1;

								//	See if we're going to use HTML.
								//	We use HTML to highlight words when
								//	provided a list of words to highlight.

		boolean useHTML			=
			( highlightWords != null ) || ( wordSet != null );

								//	Start HTML output if we're using HTML.

		if ( useHTML ) sb		= sb.append( "<html>" );

								//	If word set specified, get the list of
								//	words which actually occur in the word
								//	set.  We will use strike through display
								//	for words which do not occur in the
								// 	word set.

		Set includedWords		= null;

		if ( wordSet != null )
		{
			includedWords	=
				WordSetUtils.pruneToWordsInWordSet( wordSet , words );
		}
								//	Process each word occurrence.

		for ( int i = 0 ; i < words.length ; i++ )
		{
								//	Get the next word occurrence.

			Word word = words[ i ];

			if ( word == null ) continue;

								//	Get its line number.

			int lineNumber	= 0;

			Line wordLine	= word.getLine();

			if ( wordLine != null )
			{
				lineNumber	= wordLine.getNumber();
			}
								//	Add end of line marker to the output
								//	if the line number has changed.

			if ( lineNumber > 0 )
			{
				if ( lineNumber != prevLineNumber )
				{
					sb	= sb.append( eolChars );
				}

				prevLineNumber	= lineNumber;
			}
								//	Pick up the actual spelling and
								//	the lemmatized spelling for this
								//	word occurrence.

			String spelling			= word.getSpelling().getString();

			String spellingAndPOS	=
				WordUtils.getSpellingAndCompoundWordClass( word );

			String compoundLemma	= WordUtils.getCompoundLemma( word );

			String[] lemmata		= extractLemmata( compoundLemma );

								//	Determine if we're displaying
								//	actual spelling or the lemma spelling.

			if ( displayLemmaForms ) spelling	= compoundLemma;

								//	Add strike-through marking if
								//	this word does not appear in the
								//	included words collection.

			if ( useHTML && ( includedWords != null ) )
			{
				if ( !includedWords.contains( word ) )
				{
					spelling	= "<s>" + spelling + "</s>";
				}
			}
    							//	Append the punctuation preceding
    							//	the current word to the output.
			sb	=
				sb.append(
					getPrintablePunctuation(
						word.getPuncBefore() ) );

            					//	If we're highlighting words, see if
            					//	the current word is one we should
            					//	highlight.

			if	(	useHTML &&
					( i >= highlightBracket[ 0 ] ) &&
					( i <= highlightBracket[ 1 ] ) )
			{
				for ( int j = 0 ; j < highlightWords.length ; j++ )
				{
					if ( highlightsAreLemmata )
					{
						for ( int k = 0 ; k < lemmata.length ; k++ )
						{
							if ( lemmata[ k ].equalsIgnoreCase(
								highlightWords[ j ] ) )
							{
								spelling	=
									"<strong>" + spelling + "</strong>";
							}
						}
					}
					else
					{
						if ( spellingAndPOS.equalsIgnoreCase(
							highlightWords[ j ] ) )
						{
							spelling	=
								"<strong>" + spelling + "</strong>";
						}
					}
				}
			}
								//	Output (possibly highlighted) word.

			sb	= sb.append( spelling );

								//	Append trailing punctuation, if any.

			sb	= sb.append( word.getPuncAfter() );
		}
								//	Finish HTML output if we were using HTML.

		if ( useHTML ) sb	= sb.append( "</html>" );

								//	Return formatted string.
		return sb.toString();
	}

	/**	Get displayable text for an array of adjacent WordOccurrences.
	 *
	 *	@param	wordOccurrences 	Array of word occurrences.
	 *	@param	eolChars			String for end of line.
	 *
	 *	@return						Word occurrence text as displayable text.
	 */

	public static String getDisplayableText
	(
		Word[] wordOccurrences ,
		String eolChars
	)
	{
		return getDisplayableText(
			wordOccurrences , false , eolChars , null , null , false , null );
	}

	/**	Get displayable text for an array of adjacent WordOccurrences.
	 *
	 *	@param	wordOccurrences 	Array of word occurrences.
	 *	@param	displayLemmaForms	True to display lemma form of text.
	 *	@param	eolChars			String for end of line.
	 *
	 *	@return						Word occurrence text as displayable text.
	 */

	public static String getDisplayableText
	(
		Word[] wordOccurrences ,
		boolean displayLemmaForms ,
		String eolChars
	)
	{
		return getDisplayableText
		(
			wordOccurrences ,
			displayLemmaForms ,
			eolChars ,
			null ,
			null ,
			false ,
			null
		);
	}

	/**	Convert punctuation string into printable string.
	 *
	 *	@param	punctuation		The original punctuation string.
	 *
	 *	@return					Printable string.
	 */

	public static String getPrintablePunctuation( String punctuation )
	{
		String result	= "";

		if ( punctuationMap.containsKey( punctuation ) )
		{
			result	= (String)punctuationMap.get( punctuation );
		}

		return result;
	}

	/**	See if specified speaker gender exists.
	 *
	 *	@param	speakerGenderText	The speaker gender text.
	 *
	 *	@return		true if specified speaker gender exists, false otherwise.
	 */

	public static boolean speakerGenderExists( String speakerGenderText )
	{
		boolean result	= false;

		java.util.List speakerGenderCount	=
			PersistenceManager.doQuery
			(
				"select count(*) from WordCount wc " +
				"where wc.word.string=:speakerGender " +
				"and wc.wordForm=:wordForm",
				new String[]{ "speakerGender" , "wordForm" } ,
				new Object[]
				{
					speakerGenderText ,
					new Integer( WordForms.SPEAKERGENDER )
				}
			);

		if ( speakerGenderCount != null )
		{
			Iterator iterator	= speakerGenderCount.iterator();
			Long count		= (Long)iterator.next();
			result				= ( count.longValue() > 0 );
		}

		return result;
	}

	/**	See if specified speaker mortality exists.
	 *
	 *	@param	speakerMortalityText	The speaker mortality text.
	 *
	 *	@return		true if specified speaker mortality exists, false otherwise.
	 */

	public static boolean speakerMortalityExists( String speakerMortalityText )
	{
		boolean result	= false;

		java.util.List speakerMortalityCount	=
			PersistenceManager.doQuery
			(
				"select count(*) from WordCount wc " +
				"where wc.word.string=:speakerMortality " +
				"and wc.wordForm=:wordForm",
				new String[]{ "speakerMortality" , "wordForm" } ,
				new Object[]
				{
					speakerMortalityText ,
					new Integer( WordForms.SPEAKERMORTALITY )
				}
			);

		if ( speakerMortalityCount != null )
		{
			Iterator iterator	= speakerMortalityCount.iterator();
			Long count		= (Long)iterator.next();
			result				= ( count.longValue() > 0 );
		}

		return result;
	}

	/**	Get list of speaker gender values.
	 *
	 *	@return		String array of speaker gender values.
	 */

	public static String[] getSpeakerGenders()
	{
		java.util.List speakerGenders	=
			PersistenceManager.doQuery
			(
				"select distinct wc.word from WordCount wc " +
				"where wc.wordForm=:wordForm" ,
				new String[]{ "wordForm" } ,
				new Object[]{ new Integer( WordForms.SPEAKERGENDER ) }
			);

		SortedArrayList speakerGenderList	= new SortedArrayList();

		if ( speakerGenders != null )
		{
			Iterator iterator	= speakerGenders.iterator();

			while ( iterator.hasNext() )
			{
				speakerGenderList.add( iterator.next().toString() );
			}
		}

		return (String[])speakerGenderList.toArray( new String[]{} );
	}

	/**	Get list of speaker mortality values.
	 *
	 *	@return		String array of speaker mortality values.
	 */

	public static String[] getSpeakerMortalities()
	{
		java.util.List speakerMortalities	=
			PersistenceManager.doQuery
			(
				"select distinct wc.word from WordCount wc " +
				"where wc.wordForm=:wordForm" ,
				new String[]{ "wordForm" } ,
				new Object[]{ new Integer( WordForms.SPEAKERMORTALITY ) }
			);

		SortedArrayList speakerMortalityList	= new SortedArrayList();

		if ( speakerMortalities != null )
		{
			Iterator iterator	= speakerMortalities.iterator();

			while ( iterator.hasNext() )
			{
				speakerMortalityList.add( iterator.next().toString() );
			}
		}

		return (String[])speakerMortalityList.toArray( new String[]{} );
	}

	/**	See if specified "isVerse" value exists.
	 *
	 *	@param	isVerseText		The "isVerse" text.
	 *
	 *	@return		true if specified "isVerse" exists, false otherwise.
	 */

	public static boolean isVerseExists( String isVerseText )
	{
		boolean result	= false;

		java.util.List isVerseCount	=
			PersistenceManager.doQuery
			(
				"select count(*) from WordCount wc " +
				"where wc.word.string=:isVerseText and wc.wordForm=:wordForm" ,
				new String[]{ "isVerseText" , "wordForm" } ,
				new Object[]
				{
					isVerseText ,
					new Integer( WordForms.ISVERSE )
				}
			);

		if ( isVerseCount != null )
		{
			Iterator iterator	= isVerseCount.iterator();
			Long count		= (Long)iterator.next();
			result				= ( count.longValue() > 0 );
		}

		return result;
	}

	/**	Get list of distinct isVerse values.
	 *
	 *	@return		String array of distinct "isVerse" values.
	 */

	public static String[] getIsVerseValues()
	{
		java.util.List isVerseValues	=
			PersistenceManager.doQuery
			(
				"select distinct wc.word from WordCount wc " +
				"where wc.wordForm=:wordForm" ,
				new String[]{ "wordForm" } ,
				new Object[]{ new Integer( WordForms.ISVERSE ) }
			);

		SortedArrayList isVerseList	= new SortedArrayList();

		if ( isVerseValues != null )
		{
			Iterator iterator	= isVerseValues.iterator();

			while ( iterator.hasNext() )
			{
				isVerseList.add( iterator.next().toString() );
			}
		}

		return (String[])isVerseList.toArray( new String[]{} );
	}

	/**	See if specified metrical shape value exists.
	 *
	 *	@param	metricalShape	The metrical shape to check.
	 *
	 *	@return		true if specified metrical shape exists, false otherwise.
	 */

	public static boolean metricalShapeExists( String metricalShape )
	{
		boolean result	= false;

		java.util.List metricalShapeCount	=
			PersistenceManager.doQuery
			(
				"select count(*) from WordCount wc " +
				"where wc.word.string=:metricalShape and wc.wordForm=:wordForm",
				new String[]{ "metricalShape" , "wordForm" } ,
				new Object[]
				{
					metricalShape ,
					new Integer( WordForms.METRICALSHAPE )
				}
			);

		if ( metricalShapeCount != null )
		{
			Iterator iterator	= metricalShapeCount.iterator();
			Long count		= (Long)iterator.next();
			result				= ( count.longValue() > 0 );
		}

		return result;
	}

	/**	Get list of distinct metrical shape values.
	 *
	 *	@return		String array of distinct metrical shape strings.
	 */

	public static String[] getMetricalShapeValues()
	{
		java.util.List metricalShapeValues	=
			PersistenceManager.doQuery
			(
				"select distinct wc.word from WordCount wc " +
				"where wc.wordForm=:wordForm" ,
				new String[]{ "wordForm" } ,
				new Object[]{ new Integer( WordForms.METRICALSHAPE ) }
			);

		SortedArrayList metricalShapeList	= new SortedArrayList();

		if ( metricalShapeValues != null )
		{
			Iterator iterator	= metricalShapeValues.iterator();

			while ( iterator.hasNext() )
			{
				metricalShapeList.add( iterator.next().toString() );
			}
		}

		return (String[])metricalShapeList.toArray( new String[]{} );
	}

	/**	Extract word class tags from a spelling/compound word class.
	 *
	 *	@param	spellingAndCompoundWordClass	The combined spelling
	 *											and word class(es).
	 *
	 *	@return									String array of
	 *											word class tags.
	 */

	public static String[] extractWordClassTags
	(
		String spellingAndCompoundWordClass
	)
	{
		int iPos		= spellingAndCompoundWordClass.indexOf( '(' );
		int jPos		= spellingAndCompoundWordClass.indexOf( ')' );

//$$$PIB$$$ Need to remove homonym markers?
		return StringUtils.makeTokenArray
		(
			spellingAndCompoundWordClass.substring( iPos + 1 , jPos ) ,
			"-"
		);
	}

	/**	Extract lemmata from a compound lemma string.
	 *
	 *	@param	compoundLemma	The compound lemma string.
	 *
	 *	@return					String array of individual lemmata.
	 */

	public static String[] extractLemmata( String compoundLemma )
	{
		return StringUtils.makeTokenArray( compoundLemma , "," );
	}

	/**	See if specified spelling exists.
	 *
	 *	@param	spellingText	The spelling text.
	 *
	 *	@return					true if spelling with spelling text exists,
	 *							false otherwise.
	 */

	public static boolean spellingExists( String spellingText )
	{
		boolean result	= false;

		java.util.List spellingCount	=
			PersistenceManager.doQuery
			(
				"select count(*) from WordCount wc " +
				"where wc.word.string=:spellingText and wc.wordForm=:wordForm" ,
				new String[]{ "spellingText" , "wordForm" } ,
				new Object[]
				{
					CharsetUtils.translateToInsensitive( spellingText ) ,
					new Integer( WordForms.SPELLING )
				}
			);

		if ( spellingCount != null )
		{
			Iterator iterator	= spellingCount.iterator();
			Long count		= (Long)iterator.next();
			result				= ( count.longValue() > 0 );
		}

		return result;
	}

	/**	Finds spellings by matching an initial string of characters.
	 *
	 *	@param		initialString 	The initial spelling text string.
	 *
	 *	@return		An array of matching spelling objects
	 *				whose tags begin with the specified text.
	 *				Null if none.
	 */

	public static Spelling[] getSpellingsByInitialString
	(
		String initialString
	)
	{
		Spelling[] result	= null;

		String lowercaseInitialString	=
			CharsetUtils.translateToInsensitive( initialString ) +"%";

		java.util.List spellingList	=
			PersistenceManager.doQuery
			(
				"select distinct wc.word from WordCount wc " +
				"where wc.word.string like :initialString " +
				"and wc.wordForm=" + WordForms.SPELLING + " " +
				"order by wc.word.string" ,
				new String[]{ "initialString" } ,
				new Object[]{ lowercaseInitialString }
			);

		if ( spellingList != null )
		{
			result	=
				(Spelling[])spellingList.toArray( new Spelling[]{} );
		}

		return result;
	}

	/**	Get Spelling from string.
	 *
	 *	@param	wordText	The word text.
	 *
	 *	@return				A Spelling object for the word text.
	 */

	public static Spelling getSpellingForString( String wordText )
	{
		byte charset		= TextParams.ROMAN;
        boolean isUsedRoman	= false;
        boolean isUsedGreek	= false;

		for ( int i = 0 ; i < wordText.length() ; i++ )
		{
			char c		= wordText.charAt( i );

			isUsedRoman	= CharsUsed.isUsedRoman( c );
			isUsedGreek	= CharsUsed.isUsedGreek( c );

			if ( isUsedGreek && !isUsedRoman )
			{
				charset	= TextParams.GREEK;
				break;
			}
			else if ( isUsedRoman && !isUsedGreek )
			{
				charset	= TextParams.ROMAN;
				break;
			}
		}

		return new Spelling( wordText , charset );
	}

	/**	Removes word class tagging from a word or phrase.
	 *
	 *	@param	s		The string from which to remove any word class tags.
	 *
	 *	@return			The string with the word class tags removed.
	 *
	 *	<p>
	 *	Example:
	 *	</p>
	 *
	 *	<p>
	 *	String wc = stripWordClass( "think (v)" );
	 *	</p>
	 *
	 *	<p>
	 *	returns think in wc.
	 *	</p>
	 *
	 */

	public static String stripWordClass( String s )
	{
		return
			StringUtils.trim
			(
				StringUtils.compressMultipleOccurrences
				(
					StringUtils.deleteParenthesizedText( s ) , ' '
				)
			);
	}

	/**	Removes spelling from a tagged word.
	 *
	 *	@param	s		The string from which to remove the spelling.
	 *					Form is "spelling (wordclass)".
	 *
	 *	@return			The string with the spelling removed.
	 *
	 *	<p>
	 *	Example:
	 *	</p>
	 *
	 *	<p>
	 *	String wc = stripSpelling( "think (v)" );
	 *	</p>
	 *
	 *	<p>
	 *	returns v in wc.
	 *	</p>
	 *
	 */

	public static String stripSpelling( String s )
	{
		return
			StringUtils.compressMultipleOccurrences
			(
				StringUtils.deleteUnparenthesizedText( s ) , ' '
			);
	}

	/**	Gets the compound word class.
	 *
	 *	@param		word	The word for which to return the
	 *						compound word class.
	 *
	 *	@return				The compound word class.
	 *						Note: each compound word class tag may have
	 *						a trailing homonym index.
	 */

	public static String getCompoundWordClass( Word word )
	{
		if ( word == null ) return "";

		StringBuffer compoundWordClass	= new StringBuffer();

		ArrayList wordPartsList		=
			new ArrayList( word.getWordParts() );

		for ( int i = 0 ; i < wordPartsList.size() ; i++ )
		{
			WordPart wordPart	= (WordPart)wordPartsList.get( i );
			LemPos lemPos		= wordPart.getLemPos();
			Lemma lemma			= lemPos.getLemma();
			String lemmaTag		= lemma.getTagInsensitive().getString();

			String wordClassTag	=
				StringUtils.trim
				(
					StringUtils.deleteUnparenthesizedText( lemmaTag )
				);

			if ( compoundWordClass.length() > 0 )
			{
				compoundWordClass	= compoundWordClass.append( "-" );
			}

			compoundWordClass	= compoundWordClass.append( wordClassTag );
		}

		return compoundWordClass.toString();
	}

	/**	Gets the lower case word with the trailing compound word class.
	 *
	 *	@param		word	The word for which to return the
	 *						spelling and compound word class.
	 *
	 *	@return		The insensitive word spelling with the compound
	 *				word class in parens appended.
	 */

	public static String getSpellingAndCompoundWordClass( Word word )
	{
		String result	= "";

		if ( ( word != null ) && ( word.getSpellingInsensitive() != null ) )
		{
			result	=
				word.getSpellingInsensitive() +
				" (" + getCompoundWordClass( word ) + ")";
		}

		return result;
	}

	/**	Gets the compound lemma.
	 *
	 *	@param		word	The word for which to return the compound lemma.
	 *
	 *	@return		The compound lemma.
	 */

	public static String getCompoundLemma( Word word )
	{
		if ( word == null ) return "";

		StringBuffer compoundLemma	= new StringBuffer();

		ArrayList wordPartsList		=
			new ArrayList( word.getWordParts() );

		for ( int i = 0 ; i < wordPartsList.size() ; i++ )
		{
			WordPart wordPart	= (WordPart)wordPartsList.get( i );
			LemPos lemPos		= wordPart.getLemPos();
			Lemma lemma			= lemPos.getLemma();

			if ( compoundLemma.length() > 0 )
			{
				compoundLemma	= compoundLemma.append( "," );
			}

			compoundLemma	= compoundLemma.append( lemma.getTag() );
		}

		return compoundLemma.toString();
	}

	/**	Gets the lemma tags for a word.
	 *
	 *	@param		word	The word for which to return the lemma tags.
	 *
	 *	@return		String array of lemma tags.
	 */

	public static String[] getLemmaTags( Word word )
	{
		String[] result	= new String[ 0 ];

		if ( word == null ) return result;

		ArrayList wordPartsList		=
			new ArrayList( word.getWordParts() );

		result	= new String[ wordPartsList.size() ];

		for ( int i = 0 ; i < wordPartsList.size() ; i++ )
		{
			WordPart wordPart	= (WordPart)wordPartsList.get( i );
			LemPos lemPos		= wordPart.getLemPos();
			Lemma lemma			= lemPos.getLemma();
			result[ i ]			= lemma.getTag().getString();
		}

		return result;
	}

	/**	Gets the speaker gender.
	 *
	 *	@param		word	The word for which to return the speaker gender.
	 *
	 *	@return		The speaker gender (M=male, F=female, U=mixed/unknown).
	 *
	 *	<p>
	 *	If a word has mixed gender speakers, "U" is returned.
	 *	</p>
	 */

	public static String getSpeakerGender( Word word )
	{
		String result	= "U";

		if ( word == null ) return result;

		Speech speech	= word.getSpeech();

		if ( speech != null )
		{
			switch ( speech.getGender().getGender() )
			{
				case Gender.MALE	:
					result = "M";
					break;

				case Gender.FEMALE	:
					result = "F";
					break;

				default				:
			}
		}

		return result;
	}

	/**	Gets the speaker mortality.
	 *
	 *	@param		word	The word for which to return the speaker mortality.
	 *
	 *	@return		The speaker mortality (M=mortal, I=immortal, U=unknown).
	 *
	 *	<p>
	 *	If a word has mixed speaker mortalities, "U" is returned.
	 *	</p>
	 */

	public static String getSpeakerMortality( Word word )
	{
		String result	= "U";

		if ( word == null ) return result;

		Speech speech	= word.getSpeech();

		if ( speech != null )
		{
			switch ( speech.getMortality().getMortality() )
			{
				case Mortality.MORTAL	:
					result = "M";
					break;

				case Mortality.IMMORTAL_OR_SUPERNATURAL :
					result = "I";
					break;

				default				:
			}
		}

		return result;
	}

	/**	Gets the "is verse" flag.
	 *
	 *	@param		word	The word for which to return the is verse flag.
	 *
	 *	@return		'y' if word is verse, 'n' if prose, ' ' if not specified.
	 */

	public static String getIsVerse( Word word )
	{
		String result	= "U";

		if ( word != null )
		{
			switch ( word.getProsodic().getProsodic() )
			{
				case Prosodic.PROSE	: return "N";
				case Prosodic.VERSE	: return "Y";
				default				: return "U";
			}
		}

		return result;
	}

	/**	Gets the metrical shape.
	 *
	 *	@param		word	The word for which to return the metrical shape.
	 *
	 *	@return		Metrical shape, or "not specified" if null.
	 */

	public static String getMetricalShape( Word word )
	{
		String result	= " ";

		if ( word != null )
		{
			MetricalShape metricalShape	= word.getMetricalShape();

			if ( metricalShape != null )
			{
				result = metricalShape.getMetricalShape();
            }

			if ( result == null )
			{
				result	=
					WordHoardSettings.getString
					(
						"metricalShapeNull" ,
						"not defined"
					);
			}
		}

		return result;
	}

	/**	Get displayable word text.
	 *
	 *	@param	word		The word text.
	 *	@param	wordForm	The word form type.
	 *
	 *	@return				The displayable word text.
	 */

	public static String getDisplayableWordText
	(
		Spelling word ,
		int wordForm
	)
	{
		String result	= null;

		if ( word != null )
		{
			result	= word.getString();

			switch ( wordForm )
			{
				case WordForms.SPEAKERGENDER	:
					if ( result.equalsIgnoreCase( "M" ) )
					{
						result	=
							WordHoardSettings.getString(
								"genderMale" , "Male" );
					}
					else if ( result.equalsIgnoreCase( "F" ) )
					{
						result	=
							WordHoardSettings.getString(
								"genderFemale" , "Female" );
					}
					else if ( result.equalsIgnoreCase( "N" ) )
					{
						result	=
							WordHoardSettings.getString(
								"genderNarrative" , "Narrative" );
					}
					else if ( result.equalsIgnoreCase( "U" ) )
					{
						result	=
							WordHoardSettings.getString(
								"Unspecified" , "Unspecified" );
					}
					else if ( result.equalsIgnoreCase( "I" ) )
					{
						result	=
							WordHoardSettings.getString(
								"Indeterminate" , "Indeterminate" );
					}

					break;

				case WordForms.SPEAKERMORTALITY	:
					if ( result.equalsIgnoreCase( "M" ) )
					{
						result	=
							WordHoardSettings.getString(
								"mortalityMortal" , "Mortal" );
					}
					else if ( result.equalsIgnoreCase( "I" ) )
					{
						result	=
							WordHoardSettings.getString(
								"mortalityImmortal" ,
								"Immortal or supernatural" );
					}
					else if ( result.equalsIgnoreCase( "U" ) )
					{
						result	=
							WordHoardSettings.getString(
								"mortalityUnspecified" ,
								"Unknown or other" );
					}

					break;

				case WordForms.ISVERSE			:
					if ( result.equalsIgnoreCase( "Y" ) )
					{
						result	=
							WordHoardSettings.getString(
								"isVerseYes" , "Poetry" );
					}
					else if ( result.equalsIgnoreCase( "N" ) )
					{
						result	=
							WordHoardSettings.getString(
								"isVerseNo" , "Prose" );
					}
					else
					{
						result	=
							WordHoardSettings.getString(
								"isVerseUnknown" , "Unknown" );
					}

					break;

				case WordForms.METRICALSHAPE	:
					if ( ( result == null ) || ( result.equals( "null" ) ) )
					{
						result	=
							WordHoardSettings.getString
							(
								"metricalShapeNull" ,
								"Not specified"
							);
					}

					break;
			}
		}

		return result;
	}

	/**	Construct word class to major word class map.
	 */

	protected static void makeWordClassToMajorWordClassMap()
	{
		wordClassToMajorWordClassMap	= new HashMap();

		java.util.List wcList	=
			PersistenceManager.doQuery( "from WordClass" );

		Iterator iterator	= wcList.iterator();

		while ( iterator.hasNext() )
		{
			WordClass wordClass	= (WordClass)iterator.next();

			wordClassToMajorWordClassMap.put
			(
				wordClass.getTag() ,
				wordClass.getMajorWordClass().getMajorWordClass()
			);
		}
	}

	/**	Get major word class for a word class.
	 *
	 *	@param	wordClass	The word class.
	 *
	 *	@return				The major word class for "wordClass",
	 *						or empty string if none.
	 */

	public static String getMajorWordClassForWordClass( String wordClass )
	{
		String result	= "";

								//	Create the word class to major
								//	word class map if not already done.

		if ( wordClassToMajorWordClassMap == null )
		{
			makeWordClassToMajorWordClassMap();
		}
								//	Get the major word class for this
								//	specified word class.

		if ( wordClassToMajorWordClassMap.containsKey( wordClass ) )
		{
			result	= (String)wordClassToMajorWordClassMap.get( wordClass );
		}

		return result;
	}

	/**	Get word part counts for a batch of words.
	 *
	 *	@param	words	Array of Word.
	 *
	 *	@return			Array of int containing word part counts for each
	 *					word in "words."
	 *
	 *	<p>
	 *	The word part counts are returned in the same order as the
	 *	entries in the words array.
 	 *	</p>
	 */

	public static int[] getWordPartCounts( Word[] words )
	{
		int[] results	= new int[ 0 ];

								//	Return empty array if
								//	no words given.

		if ( ( words == null ) || ( words.length == 0 ) ) return results;

//		String queryString	=
//			"select wo.id, count(*) from Word wo, WordPart wp " +
//			"where wo in (:words) and " +
//			"wp.word = wo " +
//			"group by wo.id"

								//	Generate query string to get
								//	the count of the word parts for
								//	each word.

		StringBuffer queryString	= new StringBuffer();

		queryString.append(
			"select wo.id, count(*) from Word wo, WordPart wp" +
			" where " );

		queryString.append(
			PersistenceManager.generateInPhrase( "wo" , words ) );

		queryString.append(
			" and wp.word = wo group by wo.id" );

								//	Perform query to get word part counts.

		java.util.List wordPartCounts	=
			PersistenceManager.doQuery
			(
				queryString.toString()
			);
								//	Copy query results to map with
								//	key=word ID and value = the count of
								//	word parts for this word.  This allows
								//	us to order the counts to match
								//	the order of the words passed in.

		if ( wordPartCounts != null )
		{
			Iterator iterator	= wordPartCounts.iterator();
			TreeMap idToCount	= new TreeMap();

			while ( iterator.hasNext() )
			{
				Object[] o	= (Object[])iterator.next();

				Long longCount = (Long)o[1];
				Integer integerCount = new Integer((int)longCount.longValue());

				idToCount.put( o[ 0 ] , integerCount );
			}
								//	Now create integer array of counts
								//	arranged in the same order as the
								//	input words.  This array is the
								//	result passed back to the caller.

			results		= new int[ words.length ];
			int k		= 0;

			for ( int i = 0 ; i < words.length ; i++ )
			{
				Long id			= words[ i ].getId();

				Integer count	= (Integer)idToCount.get( id );

				results[ k++ ]	= count.intValue();
			}
		}

		return results;
	}

	/**	Get word parts for a batch of words.
	 *
	 *	@param	words	Array of Word.
	 *
	 *	@return			Array of WordPartData containing word part data
	 *					for each word in "words."
	 *
	 *	<p>
	 *	The word part counts are returned in the same order as the
	 *	entries in the words array.
 	 *	</p>
	 */

	public static WordPartData[] getWordPartData( Word[] words )
	{
		WordPartData[] results	= new WordPartData[ 0 ];

								//	No words given?  Return
								//	empty result.

		if ( ( words == null ) || ( words.length == 0 ) ) return results;

								//	Generate query string to get
								//	the word part data for
								//	each word.

		String queryString	=
			"select wo, wp, lp, po, wc, le from " +
			"Word wo, WordPart wp, LemPos lp, Pos po, " +
			" WordClass wc, Lemma le " +
			" left join fetch wo.speech " +
			"where " +
				PersistenceManager.generateInPhrase( "wo" , words ) +
			" and " +
			"wp.word=wo and " +
			"lp=wp.lemPos and " +
			"po=lp.pos and " +
			"le=lp.lemma and " +
			"wc=po.wordClass " +
			"order by wo.tag, wp.partIndex asc";

								//	Perform query to get word part data.

		java.util.List wordPartData	=
			PersistenceManager.doQuery( queryString );

								//	Copy query results to list of
								//	WordPartData objects.  Save the
								//	objects in a Map with key=word ID
								//	and value=WordPartData object
								//	for that word.  We create another
								//	map with key=word ID and value=
								//	wordPartIndex to ensure we don't
								//	get duplicate word parts.

		if ( wordPartData != null )
		{
			TreeMap idToData			= new TreeMap();
			TreeMap idToWordPartIndex	= new TreeMap();
			Iterator iterator			= wordPartData.iterator();

								//	Loop over query results.

			while ( iterator.hasNext() )
			{
								//	Copy query results into
								//	model objects.

				Object[] o			= (Object[])iterator.next();
				Word word 			= (Word)o[ 0 ];
				Long wordID			= word.getId();
				Speech speech		= word.getSpeech();
				WordPart wordPart	= (WordPart)o[ 1 ];
				int wordPartIndex	= wordPart.getPartIndex();
				LemPos lemPos		= (LemPos)o[ 2 ];
				Pos pos				= (Pos)o[ 3 ];
				WordClass wordClass	= (WordClass)o[ 4 ];
				Lemma lemma			= (Lemma)o[ 5 ];

				Gender gender		= null;
				Mortality mortality	= null;

				if ( speech != null )
				{
					gender		= speech.getGender();
					mortality	= speech.getMortality();
        		}
								//	If this word part already exists
								//	for the specified word, do nothing.
								//	We do this by storing a bitmap of
								//	the word parts already encountered
								//	in the idToWordPartIndex map.
								//	If we come across the same word part
								//	for a word, we ignore it.
								//
								//	Note: this should not happen, but
								//	it can depending upon the fetch
								//	strategy used by Hibernate for
								//	collection queries.  The code
								//	below to track duplicate word parts
								//	for a word eliminates the problem.

				boolean addWordPart	= true;

				XBitSet wordPartBits;

				if ( idToWordPartIndex.containsKey( wordID ) )
				{
					wordPartBits	=
						(XBitSet)idToWordPartIndex.get( wordID );

					addWordPart				=
						!wordPartBits.get( wordPartIndex );
				}
				else
				{
					wordPartBits	= new XBitSet();
				}

				wordPartBits.set( wordPartIndex );

				idToWordPartIndex.put( wordID , wordPartBits );

								//	See if there is already an entry
								//	in the map for this word.  If not,
								//	just create a new WordPartData
								//	entry and add it to the map.
								//	If there is an existing entry,
								//	update the current entry with this
								//	new data.

				if ( addWordPart )
				{
					if ( idToData.containsKey( wordID ) )
					{
						WordPartData wpd	=
							(WordPartData)idToData.get( wordID );

						wpd.append( lemPos , lemma , pos , wordClass );

						idToData.put( wordID , wpd );
					}
					else
					{
						WordPartData wpd	=
							new WordPartData
							(
								word ,
								gender ,
								mortality ,
								lemPos ,
								lemma ,
								pos ,
								wordClass
							);

						idToData.put( wordID , wpd );
					}
				}
			}
								//	Now create results arranged in the
								//	same order as the input words.

			results		= new WordPartData[ words.length ];

			for ( int i = 0 ; i < words.length ; i++ )
			{
				Long wordID		= words[ i ].getId();
				results[ i ]	= (WordPartData)idToData.get( wordID );
			}
		}

		return results;
	}

	/**	Create punctuation map.
	 */

	protected static void createPunctuationMap()
	{
		punctuationMap.put( "," , ", " );
		punctuationMap.put( "." , ". " );
		punctuationMap.put( ";" , "; " );
		punctuationMap.put( ":" , ": " );
		punctuationMap.put( "?" , "? " );
		punctuationMap.put( "!" , "! " );
		punctuationMap.put( "(" , " (" );
		punctuationMap.put( ")" , ") " );
		punctuationMap.put( "&mdash;" , " -- " );
		punctuationMap.put( "&ldquo;" , " \"" );
		punctuationMap.put( "&rdquo;" , "\" " );
		punctuationMap.put( "&lsquo;" , " '" );
		punctuationMap.put( "&rsquo;" , "' " );
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected WordUtils()
	{
	}

	/** Static initializer. */

	static
	{
								//	Create punctuation map.
		createPunctuationMap();
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


