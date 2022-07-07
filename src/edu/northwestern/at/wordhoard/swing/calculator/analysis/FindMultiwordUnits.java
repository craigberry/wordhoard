package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.corpuslinguistics.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	Find multiword units.
 */

public class FindMultiwordUnits
	extends FrequencyAnalysisRunnerBase
	implements AnalysisRunner
{
	/**	Output column indices.
	 */

	protected static final int MWUTEXTCOLUMN		= 0;
	protected static final int WORDCLASSESCOLUMN	= 1;
	protected static final int MWULENGTHCOLUMN		= 2;
	protected static final int MWUCOUNTCOLUMN		= 3;
	protected static final int DICECOLUMN			= 4;
	protected static final int LOGLIKECOLUMN		= 5;
	protected static final int PHISQUAREDCOLUMN		= 6;
	protected static final int SICOLUMN				= 7;
	protected static final int SCPCOLUMN			= 8;

	/**	Count # of mwus accepted.
	 */

	protected int accepted			= 0;

	/**	Count # of mwus rejected by filters.
	 */

	protected int rejected			= 0;

	/**	Count # of mwus which occur only once.
	 */

	protected int onceOnly			= 0;

	/**	Count of mwus to report on.
	 */

	protected int mwusToReportOn	= 0;

	/**	Count of mwus rejected by word class filters.
	 */

	protected int rejectedByWordClassFilters	= 0;

	/**	Count of mwus accepted by localmaxs algorithm.
	 */

	protected int acceptedByLocalMaxs		= 0;

	/**	The column containing the association measure to use. */

	protected int sortColumn				= SCPCOLUMN;

    /**	Create a multiple word form frequency profile object.
     */

	public FindMultiwordUnits()
	{
		super
		(
			AnalysisDialog.FINDMULTIWORDUNITS
		);
	}

	/**	Run an analysis.
	 */

	public void runAnalysis
	(
		JFrame parentWindow ,
		ProgressReporter progressReporter
	)
	{
//		this.parentWindow		= parentWindow;
		this.progressReporter	= progressReporter;

									//	No results yet.

		resultsPanel			= null;

									//	Update progress display.

		if ( displayProgress && ( progressReporter != null ) )
		{
			progressReporter.setTitle
			(
				WordHoardSettings.getString
				(
					"Computingmultiwordunitfrequencies" ,
					"Computing multiword unit frequencies"
				)
			);

			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Retrievingwordsandmultiwordunitsinanalysistext" ,
					"Retrieving words and multiword units in analysis text"
				)
			);

			progressReporter.setIndeterminate( true );
		}
								//	Set output column names.

    	String[] columnNames	=
    		new String[]
	    	{
				WordHoardSettings.getString
				(
					"coltitleMWUText" ,
					"<html><br><br>Multiword Unit Text</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleWordclasses" ,
					"<html>Word<br>classes</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleMWULength" ,
					"<html><br><br>Length</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleMWUCount" ,
					"<html><br><br>Count</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleMWUDiceCoefficient" ,
					"<html><br>Dice<br>coefficient</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleMWULogLikelihood" ,
					"<html><br>Log<br>Likelihood</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleMWUPhiSquared" ,
					"<html><br>Phi<br>Squared</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleMWUMutualInformation" ,
					"<html>Specific<br>Mutual<br>Information</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleMWUSymmetricConditionalProbability" ,
					"<html>Symmetric<br>Conditional<br>Probability</html>"
				)
    		};

								//	Create table model to hold results.
		model		=
			new WordHoardSortedTableModel( columnNames , 0 , true );

								//	Create map from word class to
								//	major word class.
//		makeMap();

		if ( isCancelled( progressReporter ) )
		{
			closeProgressReporter();
			return;
		}
								//	Allocate multiword unit extractors.

		NGramExtractor[] extractors	=
			new NGramExtractor[ maximumMultiwordUnitLength + 1 ];

		java.util.HashSet mwusList	= new HashSet( 30000 );
		Map wordCountMap			= new TreeMap();

								//	Loop over works.

		Work[] works	= analysisText.getWorks();

								//	Update progress display.

		if ( displayProgress && ( progressReporter != null ) )
		{
			PrintfFormat titleFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"Retrievingwordsandmultiwordunitsins" ,
						"Retrieving words and multiword units in \"%s\""
					)
				);

			progressReporter.setTitle
			(
				titleFormat.sprintf
				(
					new Object[]{ analysisText.toString() }
				)
			);

			progressReporter.updateProgress( " " );

			progressReporter.setMaximumBarValue( works.length );
			progressReporter.updateProgress( 0 );

			if ( works.length > 1 )
			{
				progressReporter.setIndeterminate( false );
			}
		}

		for ( int iwork	= 0 ; iwork < works.length ; iwork++ )
		{
								//	Get next work.

			Work work	= works[ iwork ];

								//	Update progress display.

			if ( displayProgress && ( progressReporter != null ) )
			{
				PrintfFormat workTitleFormat	=
					new PrintfFormat
					(
						WordHoardSettings.getString
						(
							"Computingfrequenciesinwork" ,
							"Retrieving words and multiword units in \"%s\""
						)
					);

				progressReporter.updateProgress
				(
					workTitleFormat.sprintf
					(
						new Object[]{ work.getShortTitle() }
					)
				);
            }
								//	Retrieve spellings or lemmata for
								//	this work.

			java.util.List workWords;

			if ( wordForm == WordForms.SPELLING )
 			{
				workWords	= retrieveSpellings( works[ iwork ] );
			}
			else
			{
				workWords	= retrieveLemmata( works[ iwork ] );
			}

			if ( isCancelled( progressReporter ) )
			{
				closeProgressReporter();
				return;
			}
								//	Get word forms.

			String[] wordStrings;

			if ( wordForm == WordForms.SPELLING )
 			{
				wordStrings	= extractSpellings( workWords );
			}
			else
			{
				wordStrings	= extractLemmata( workWords );
			}

			if ( isCancelled( progressReporter ) )
			{
				closeProgressReporter();
				return;
			}
								//	Count the word forms.

			long startTime	= System.currentTimeMillis();

			WordCountExtractor wordCountExtractor	=
				new WordCountExtractor( wordStrings );

			long endTime	= System.currentTimeMillis() - startTime;

			if ( isCancelled( progressReporter ) )
			{
				closeProgressReporter();
				return;
			}
								//	Create the raw set of multiword unit
								//	strings and their counts.
			mwusList.addAll
			(
				createRawMWUs( wordCountExtractor , extractors )
			);
								//	Add word counts for this work to
								//	the total.

			CountMapUtils.addCountMap
			(
				wordCountMap ,
				wordCountExtractor.getWordCounts()
			);

			if ( isCancelled( progressReporter ) )
			{
				closeProgressReporter();
				return;
			}

			if ( displayProgress && ( progressReporter != null ) )
			{
				progressReporter.updateProgress( iwork + 1 );
            }

            workWords	= null;
            wordStrings	= null;

            System.gc();
		}
								//	Get total number of words across all
								//	selected works.

		int totalWordCount	=
			CountMapUtils.getTotalWordCount( wordCountMap );

								//	Create count data entries for
								//	each raw multiword unit string.

		Object[] o	= storeMWUData
		(
			mwusList ,
			wordCountMap ,
			totalWordCount ,
			extractors
		);

		if ( isCancelled( progressReporter ) )
		{
			closeProgressReporter();
			return;
		}

		java.util.List	mwuCountData	= (java.util.List)o[ 0 ];
		HashMap glueMap					= (HashMap)o[ 1 ];

								//	Filter the raw list of multiword units.

		String[] maxLabels	=
			filterMultiwordUnits
			(
				mwuCountData , glueMap , wordCountMap , extractors , model
			);

		maxLabels[ 0 ]	= maxLabels[ 0 ] + "wwwww";
		maxLabels[ 1 ]	= maxLabels[ 1 ] + "wwwww";

								//	Close progress dialog, if any.

		boolean cancelled	= closeProgressReporter();

								//	Sort on selected association measure.

		sortColumn		= 0;

		switch ( associationMeasure )
		{
			case AnalysisDialog.DICE		:
				sortColumn	= DICECOLUMN;
				break;

			case AnalysisDialog.PHISQUARED	:
				sortColumn	= PHISQUAREDCOLUMN;
				break;

			case AnalysisDialog.SI			:
				sortColumn	= SICOLUMN;
				break;

			case AnalysisDialog.SCP			:
				sortColumn	= SCPCOLUMN;
				break;

			case AnalysisDialog.LOGLIKE		:
			default							:
				sortColumn	= LOGLIKECOLUMN;
				break;
		}
								//	Return results.
		resultsPanel	=
			cancelled ?
				null :
				generateResults
				(
					model ,
					maxLabels ,
					sortColumn ,
					totalWordCount
				);
	}

	/**	Perform query and get spellings for selected work(s).
	 *
	 *	@param	work	Work from which to retrieve words.
	 *
	 *	@return	List of spellings.
	 */

	protected java.util.List retrieveSpellings( Work work )
	{
		long startTime	= System.currentTimeMillis();

		java.util.List workWords;

		StringBuffer sb	= new StringBuffer( 100 );

		sb.append( "select " );

		if ( ignoreCaseAndDiacriticalMarks )
		{
			sb.append( " word.spellingInsensitive.string, " );
		}
		else
		{
			sb.append( " word.spelling.string, " );
		}

		sb.append( " word.workOrdinal, " );
		sb.append( " wordPart.partIndex, " );
		sb.append( " wordPart.lemPos.lemma.wordClass.tag " );
		sb.append( "from Word word, WordPart wordPart " );
		sb.append( "where word.work=:work " );
		sb.append( " and wordPart.word=word " );
		sb.append( " order by word.workOrdinal asc, " );
		sb.append( " wordPart.partIndex asc" );

		workWords	=
			PersistenceManager.doQuery
			(
				sb.toString() ,
				new String[]{ "work" } ,
				new Object[]{ work }
			);

		long endTime		= System.currentTimeMillis() - startTime;

		return workWords;
	}

	/**	Perform query and get lemmata for selected work(s).
	 *
	 *	@param	work	Work from which to retrieve words.
	 *
	 *	@return	The list of lemmata.
	 */

	protected java.util.List retrieveLemmata( Work work )
	{
		long startTime	= System.currentTimeMillis();

		java.util.List workWords;

		StringBuffer sb	= new StringBuffer( 100 );

		sb.append( "select " );

		if ( ignoreCaseAndDiacriticalMarks )
		{
			sb.append(
				" wordPart.lemPos.lemma.tagInsensitive.string, " );
		}
		else
		{
			sb.append( " wordPart.lemPos.lemma.tag.string, " );
		}

		sb.append( " word.workOrdinal, " );
		sb.append( " wordPart.partIndex " );
		sb.append( "from Word word, WordPart wordPart " );
		sb.append( "where word.work=:work " );
		sb.append( " and wordPart.word=word " );
		sb.append( " order by word.workOrdinal asc, " );
		sb.append( " wordPart.partIndex asc" );

		workWords	=
			PersistenceManager.doQuery
			(
				sb.toString() ,
				new String[]{ "work" } ,
				new Object[]{ work }
			);

		long endTime		= System.currentTimeMillis() - startTime;

		return workWords;
	}

	/**	Extract spellings from retrieved data.
	 *
	 *	@param	workWords	Retrieved words.
	 *
	 *	@return				String array of spellings suitable for counting.
	 */

	protected String[] extractSpellings( java.util.List workWords )
	{
		long startTime		= System.currentTimeMillis();

		ArrayList myList	= new ArrayList();

		int lastOrdinal		= -1;
		String compoundWC	= "";
		String lastWord		= "";

		Iterator iterator	= workWords.iterator();

		while ( iterator.hasNext() )
		{
			Object[] stuff	= (Object[])iterator.next();

			String word		= (String)stuff[ 0 ];
			int ordinal		= ((Integer)stuff[ 1 ]).intValue();
			String wc		= (String)stuff[ 3 ];

			if ( lastOrdinal == -1 )
			{
				lastOrdinal = ordinal;
				lastWord	= word;
			}

			if ( ordinal != lastOrdinal )
			{
				String wordAndCWC	= lastWord + " (" + compoundWC + ")";

				myList.add( wordAndCWC );

				lastOrdinal	= ordinal;
				lastWord	= word;
				compoundWC	= wc;
			}
			else
			{
				if ( compoundWC.length() > 0 )
				{
					compoundWC	= compoundWC + "-";
				}

				compoundWC	= compoundWC + wc;
			}
    	}

		String wordAndCWC	= lastWord + " (" + compoundWC + ")";

		myList.add( wordAndCWC );

		String[] wordStrings		=
			(String[])myList.toArray( new String[]{} );

		long endTime	= System.currentTimeMillis() - startTime;

		return wordStrings;
	}

	/**	Extract lemmata from retrieved data.
	 *
	 *	@param	workWords	Retrieved words.
	 *
	 *	@return				String array of lemmata suitable for counting.
	 */

	protected String[] extractLemmata( java.util.List workWords )
	{
		long startTime		= System.currentTimeMillis();

		ArrayList myList	= new ArrayList();

		Iterator iterator	= workWords.iterator();

		while ( iterator.hasNext() )
		{
			Object[] stuff	= (Object[])iterator.next();
			String word		= (String)stuff[ 0 ];

			myList.add( word );
		}

		String[] wordStrings		=
			(String[])myList.toArray( new String[]{} );

		long endTime	= System.currentTimeMillis() - startTime;

		return wordStrings;
	}

	/**	Create raw (unfiltered) multiword unit strings.
	 *
	 *	@param	wordExtractor	The WordCountExtractor object.
	 *
	 * 	@param	extractors	The NGramExtractors to receive the raw
	 *						multiword unit strings.
	 *
	 *	@return				List of all raw multiword units to analyze.
	 */

	protected java.util.Collection createRawMWUs
	(
		WordCountExtractor wordExtractor ,
		NGramExtractor[] extractors
	)
	{
		String words[]				= wordExtractor.getWords();

		int nWords					= words.length;

		HashSet mwusList			= new HashSet( nWords );

								//	Need mwus for words of length
								//	maximumMultiwordUnitLength + 1 to
								//	filter using localmaxs.  Also
								//	need all mwus down to bigrams to get
								//	pseudo-bigram association measures.

		int maxMWULength			= maximumMultiwordUnitLength + 1;

		for ( int i = maxMWULength - 1 ; i > 0 ; i-- )
		{
			long startTime	= System.currentTimeMillis();

			NGramExtractor extractor	=
				new NGramExtractor( words , i + 1 , i + 1 );

			if ( extractors[ i ] == null )
			{
				extractors[ i ]	= extractor;
			}
			else
			{
				extractors[ i ].mergeNGramExtractor( extractor );
            }

			mwusList.addAll( Arrays.asList( extractor.getNGrams() ) );

			long endTime	= System.currentTimeMillis() - startTime;

			if ( isCancelled( progressReporter ) ) break;
		}

		return mwusList;
	}

	/**	Store multiword unit data.
	 *
	 *	@param	mwusList		Collection of all raw multiword units.
	 *
	 *	@param	wordCountMap	Map containing words and keys and
	 *							counts as value for all words in the
	 *							multiword units.
	 *
	 *	@param	totalWordCount	Total word count in word count map.
	 *
	 *	@param	extractors		The NGramExtractors holding the counts for
	 *							the raw multiword unit strings.
	 *
	 *	@return					Two item array.
	 *							[0]	=	list of all multiword unit count
	 *									data items.
	 *							[1]	=	hash map mapping mwu to selected
	 *									association measure for use by
	 *									localmaxs.
	 */

	protected Object[] storeMWUData
	(
		java.util.Collection mwusList ,
		Map wordCountMap ,
		int totalWordCount ,
		NGramExtractor[] extractors
	)
	{
								//	Modify progress display.

		if ( displayProgress && ( progressReporter != null ) )
		{
			progressReporter.setTitle
			(
				WordHoardSettings.getString
				(
					"Computingassociationmeasuresformultiwordunits" ,
					"Computing association measures for multiword units"
				)
			);

			PrintfFormat mwusFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"Computingassociationmeasuresfornmultiwordunits" ,
						"Computing association measures for %s multiword units"
					)
				);

			progressReporter.updateProgress
			(
				mwusFormat.sprintf
				(
					new Object[]
					{
						Formatters.formatIntegerWithCommas
						(
							mwusList.size()
						)
					}
				)
			);

			progressReporter.updateProgress( 0 );

			progressReporter.setMaximumBarValue( mwusList.size() );

			progressReporter.setIndeterminate( false );
		}
								//	Holds multiword unit count and
								//	association measure data.

		ArrayList mwuCountData	= new ArrayList();

								//	Maps each multiword unit to a
								//	specified "glue" (association) measure
								//	for use in the localmaxs algorithm.

		HashMap glueMap	= new HashMap();

		long startTime	= System.currentTimeMillis();

								//	Count # of mwus processed for
								//	progress display.

		int mwusDone				= 0;

								//	Update progress every 1% or so.

    	int updateInterval	= mwusList.size() / 100;

								//	Loop over all raw multiword units.

		for	(	Iterator iterator	= mwusList.iterator() ;
			 	iterator.hasNext() ;
			)
		{
								//	Create a count data entry for this
								//	multiword unit.  Also computes all
								//	the association measures for this
								//	multiword unit.

			String mwuText	= (String)iterator.next();

			MultiwordUnitData countData	=
				new MultiwordUnitData
				(
					mwuText ,
					wordCountMap ,
					totalWordCount ,
					extractors
				);
								//	Add to the list of mwu count data items.

			mwuCountData.add( countData );

								//	Store the selected "glue" measure in a
								//	hash map for fast access by the
								//	localmaxs algorithm.

			double glueMeasure;

			switch ( associationMeasure )
			{
				case AnalysisDialog.DICE		:
					glueMeasure	= countData.getDice();
					break;

				case AnalysisDialog.PHISQUARED	:
					glueMeasure	= countData.getPhiSquared();
					break;

				case AnalysisDialog.SI			:
					glueMeasure	= countData.getSI();
					break;

				case AnalysisDialog.SCP			:
					glueMeasure	= countData.getSCP();
					break;

				case AnalysisDialog.LOGLIKE		:
				default							:
					glueMeasure	= countData.getLogLikelihood();
					break;
			}

			glueMap.put( mwuText , new Double( glueMeasure ) );

								//	Increment count of mwus processed.
			mwusDone++;
								//	Update progress display.

			if ( displayProgress && ( progressReporter != null ) )
			{
				if ( ( mwusDone % updateInterval ) == 0 )
				{
					progressReporter.updateProgress( mwusDone );
				}

				if ( isCancelled( progressReporter ) ) break;
			}
		}

		long endTime	= System.currentTimeMillis() - startTime;

		return new Object[]{ mwuCountData , glueMap };
	}

	/**	Filter the raw multiword units.
	 *
	 *	@param	mwuCountData	The list of multiword unit count data.
	 *	@param	glueMap			Hash map of mwus to glue association measures.
	 *	@param	wordCountMap	Word count map.
	 *	@param	extractors		Extractors holding mwu count data.
	 *	@param	model			Table model in which to store filtered mwus.
	 *
	 *	@return					Longest mwu string in table.
	 */

	protected String[] filterMultiwordUnits
	(
		java.util.List mwuCountData ,
		HashMap glueMap ,
		Map wordCountMap ,
		NGramExtractor[] extractors ,
		SortedTableModel model
	)
	{
								//	Count # of mwus accepted.

		accepted					= 0;

								//	Count # of mwus rejected by filters.

		rejected					= 0;

								//	Count # of mwus which occur only once.

		onceOnly					= 0;

								//	Count # of mwus rejected by word class filter.

		rejectedByWordClassFilters	= 0;

								//	Count # of mwus accepted by localmaxs.

		acceptedByLocalMaxs			= 0;

								//	Count # of mwus processed for
								//	progress display.

		int mwusDone				= 0;

								//	Update progress every 1% or so.

		mwusToReportOn	=
			mwuCountData.size() -
				extractors[
					maximumMultiwordUnitLength - 1 ].getNumberOfNGrams();

    	int updateInterval	= mwusToReportOn / 100;

								//	Update progress display.

		if ( displayProgress && ( progressReporter != null ) )
		{
			progressReporter.setTitle
			(
				WordHoardSettings.getString
				(
					"Filteringmultiwordunits" ,
					"Filtering multiword units"
				)
			);

			PrintfFormat mwusFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"FilteringNmultiwordunits" ,
						"Filtering %s multiword units"
					)
				);

			progressReporter.updateProgress
			(
				mwusFormat.sprintf
				(
					new Object[]
					{
						Formatters.formatIntegerWithCommas
						(
							mwusToReportOn
						)
					}
				)
			);

			progressReporter.updateProgress( 0 );

			progressReporter.setMaximumBarValue( mwusToReportOn );

			progressReporter.setIndeterminate( false );
		}
								//	Maximum label lengths for display.

		String maxLabel		= "";
		String maxLabel2	= "";

		long startTime		= System.currentTimeMillis();

								//	Loop over all mwus and apply filters.

		for ( int i = 0 ; i < mwuCountData.size() ; i++ )
		{
								//	Get next mwu's data.

			MultiwordUnitData countData	=
				(MultiwordUnitData)mwuCountData.get( i );

								//	If length exceeds maximum requested,
								//	skip it.  This happens because we need
								//	mwus one word longer than the requested
								//	length in order to apply the localmaxs
								//	algorithm.

			if ( countData.getMWUTextLength() > maximumMultiwordUnitLength )
				continue;

								//	If length is less than minimum requested,
								//	skip it.  Again, this happens we need
								//	mwus one word shorter than specified
								//	minimum in order to apply the localmaxs
								//	algorithm.

			if ( countData.getMWUTextLength() < minimumMultiwordUnitLength )
				continue;

								//	Increment count of mwus processed.
			mwusDone++;
								//	Update progress display.

			if ( displayProgress && ( progressReporter != null ) )
			{
				if ( ( i % updateInterval ) == 0 )
				{
					progressReporter.updateProgress( mwusDone );
				}

				if ( isCancelled( progressReporter ) ) break;
			}
								//	Get multiword unit text.

			String mwuText		= countData.getMWUText();

								//	Get # of times mwu appears.

			int colCount		= countData.getMWUTextCount();

								//	Optionally filter out mwus which occur
								//	only once.

			if ( filterSingleOccurrences && ( colCount < 2 ) )
			{
				onceOnly++;
				continue;
            }
								//	Get the words, word counts,
								//	and association measures for
								//	this mwu.

			String[] colWords		= countData.getWords();
			int[] colWordsCounts	= countData.getWordCounts();
			double dice				= countData.getDice();
			double logLikelihood	= countData.getLogLikelihood();
			double phiSquared		= countData.getPhiSquared();
			double scp				= countData.getSCP();
			double si				= countData.getSI();

								//	ok will be true if the mwu passes
								//	all the remaining filters.

			boolean ok				= true;

								//	Filter using localmaxs algorithm if
								//	requested.

			if ( filterUsingLocalMaxs )
			{
				ok	= isMWU( countData , glueMap );
				if ( ok ) acceptedByLocalMaxs++;
            }
								//	Filter bigrams and trigrams using
								//	word class filters if requested.

			boolean useWordClassFilters	=
				filterBigramsByWordClass ||
				filterTrigramsByWordClass ||
				filterMultiwordUnitsContainingVerbs;

			if ( ok	&& useWordClassFilters )
			{
				ok	= passesWordClassFilters( colWords );

				if ( !ok ) rejectedByWordClassFilters++;
			}
								//	If mwu passed all filters ...
			if ( ok )
			{
								//	increment count of mwus accepted.
				accepted++;
								//	Get text for display.

				String fixedMWUText	= fixMWUText( mwuText );

								//	See if this is the new longest label.

				if ( fixedMWUText.length() > maxLabel.length() )
				{
					maxLabel	= fixedMWUText;
				}
								//	Get word classes.

				String wordClasses	=
					WordUtils.stripSpelling( mwuText );

								//	See if this is the new longest label.

				if ( wordClasses.length() > maxLabel2.length() )
				{
					maxLabel2	= wordClasses;
				}
								//	Add data for this mwu to the
								//	table model for display.
				model.add
				(
					new FrequencyAnalysisDataRow
					(
						fixedMWUText ,
						wordClasses ,
						new double[]
						{
							(double)colWords.length ,
							(double)colCount ,
							dice ,
							logLikelihood ,
							phiSquared ,
							si ,
							scp
						}
					)
				);
			}
								//	if the mwu was rejected by the filters,
								//	increment the count of mwus rejected.
			else
			{
				rejected++;
			}
		}

		long endTime	= System.currentTimeMillis() - startTime;

		return new String[]{ maxLabel , maxLabel2 };
	}

	/**	Fix multiword unit text for display.
	 *
	 *	@param	mwuText	The multiword unit text to fix.
	 *
	 *	@return			The multiword unit text suitable for display.
	 */

	protected String fixMWUText( String mwuText )
	{
		String result	= mwuText;

		result			= result.replaceAll( "\t" , " " );

//		if ( !showWordClasses )
//		{
			result	= StringUtils.deleteParenthesizedText( result );
			result	= StringUtils.compressMultipleOccurrences( result , ' ' );
			result	= result.trim();
//		}

		return result;
	}

	/**	Determine if multiword unit is a phrase using localmaxs.
	 *
	 *	@param	countData	The multiword unit data.
	 *  @param	glueMap		The glue map for all multiword units.
	 *
	 *	@return				true if multiword unit appears to be a phrase.
	 */

	protected boolean isMWU
	(
		MultiwordUnitData countData ,
		Map glueMap
	)
	{
		boolean result		= true;

		String mwuText		= countData.getMWUText();
		double glueValue	= getGlue( mwuText , glueMap );

		if ( countData.getMWUTextLength() > 2 )
		{
			result	=
				( glueValue >=
					getGlue( countData.leftAntecedent() , glueMap ) ) &&
				( glueValue >=
					getGlue( countData.rightAntecedent() , glueMap ) );
		}
								//	Check right successors.
		if ( result )
		{
			String[] successors	= countData.rightSuccessors();

			for ( int i = 0 ; i < successors.length ; i++ )
			{
				if ( glueValue <= getGlue( successors[ i ] , glueMap ) )
				{
					result	= false;
					break;
				}
			}
		}
								//	Check left successors.
		if ( result )
		{
			String[] successors	= countData.leftSuccessors();

			for ( int i = 0 ; i < successors.length ; i++ )
			{
				if ( glueValue <= getGlue( successors[ i ] , glueMap ) )
				{
					result	= false;
					break;
				}
			}
		}

		return result;
	}

	/**	Get "glue" value for a multiword unit.
	 *
	 *	@param	mwuText		The multiword unit text.
	 *	@param	glueMap		The map from multiword units to glue values.
	 *
	 *	@return				The glue value for the given multiword unit.
	 *						Returns 0 if mwu not found.
	 */

	protected double getGlue( String mwuText , Map glueMap )
	{
		double result	= 0.0D;

		try
		{
			result	= ((Double)glueMap.get( mwuText )).doubleValue();
		}
		catch ( Exception e )
		{
		}

		return result;
	}

	/**	Filter bigrams by word class.
	 *
	 *	@param	wordClasses		Major word classes for each word in bigram.
	 *	@return	True if word classes pass bigram filter, false otherwise.
	 *
	 *	<p>
	 *	The bigram filters are those suggested by
	 *	Justeson and Katz.
	 *	</p>
	 *
	 *	<ul>
	 *		<li>A N</li>
	 *		<li>N N</li>
	 *	</ul>
	 *
	 *	<pre>
	 *	A = adjective
	 *	N = noun
	 *	</pre>
	 */

	protected boolean passesBigramFilter( String[] wordClasses )
	{
		boolean result	= true;
		int nWords		= wordClasses.length;

		if ( ( nWords != 2 ) || !filterBigramsByWordClass ) return result;

		result	=
			(	wordClasses[ 0 ].equals( "adjective" ) ||
				wordClasses[ 0 ].equals( "noun" ) ) &&
				wordClasses[ 1 ].equals( "noun" );

		return result;
	}

	/**	Filter trigrams by word class.
	 *
	 *	@param	wordClasses		Major word classes for words comprising
	 *							trigram.
	 *	@return	True if word classes pass trigram filter, false otherwise.
	 * 
	 *	<p>
	 *	The trigram filters are those suggested by
	 *	Justeson and Katz.
	 *	</p>
	 *
	 *	<ul>
	 *		<li>A A N</li>
	 *		<li>A N N</li>
	 *		<li>N A N</li>
	 *		<li>N N N</li>
	 *		<li>N P N</li>
	 *  </ul>
	 *
	 *	<p>
	 *	To this we add, for trigrams:
	 *	</p>
	 *
	 *	<ul>
	 *		<li>N C N</li>
	 *  </ul>
	 *
	 *	<pre>
	 *	A = adjective
	 *	N = noun
	 *	P = preposition
	 *	C = conjunction
	 *	</pre>
	 */

	protected boolean passesTrigramFilter( String[] wordClasses )
	{
		boolean result	= true;
		int nWords		= wordClasses.length;

		if ( ( nWords != 3 ) || !filterTrigramsByWordClass ) return result;

		if ( wordClasses[ 2 ].equals( "noun" ) )
		{
			if ( wordClasses[ 0 ].equals( "adjective" ) )
			{
				result	=
					wordClasses[ 1 ].equals( "adjective" ) ||
					wordClasses[ 1 ].equals( "noun" );
			}
			else if ( wordClasses[ 0 ].equals( "noun" ) )
			{
				result	=
					wordClasses[ 1 ].equals( "adjective" ) ||
					wordClasses[ 1 ].equals( "noun" ) ||
					wordClasses[ 1 ].equals( "preposition" ) ||
					wordClasses[ 1 ].equals( "conjunction" );
			}
		}

		return result;
	}

	/**	Filter ngrams containing verbs.
	 *
	 *	@param	wordClasses		Major word classes for each word in ngram.
	 *	@return	True if any constituent word is a verb, false if not.
	 *	<p>
	 *	The ngram is filtered if any of the constituent words is a verb.
	 *	</p>
	 */

	protected boolean passesVerbFilter( String[] wordClasses )
	{
		boolean result	= true;
		int nWords		= wordClasses.length;

		if ( !filterMultiwordUnitsContainingVerbs ) return result;

		for ( int i = 0 ; i < wordClasses.length ; i++ )
		{
			result	=	result && !wordClasses[ i ].equals( "verb" );
		}

		return result;
	}

	/**	Filter multiword units using major word class.
	 *
	 *	@param	words			Major word class for each word in the ngram.
	 *
	 *	@return					true if multiword unit passes
	 *							word class filters.
	 *
	 *	<p>
	 *	The verb filter removes all multiword units containing a verb.
	 *	</p>
	 */

	public boolean passesWordClassFilters( String[] words )
	{
		boolean result	= true;
		int nWords		= words.length;

								//	Get major word class for each
								//	word in the multiword unit.

		String[] wordClasses	= new String[ words.length ];

		for ( int i = 0 ; i < words.length ; i++ )
		{
			wordClasses[ i ]	=
				StringUtils.deleteUnparenthesizedText( words[ i ] );

			wordClasses[ i ]	=
				WordUtils.getMajorWordClassForWordClass( wordClasses[ i ] );
		}

		return
			passesBigramFilter( wordClasses ) &&
			passesTrigramFilter( wordClasses ) &&
			passesVerbFilter( wordClasses );
	}

	/**	Displays results of multiword unit extraction in a sorted table.
	 *
	 *	@param	model			Table model holding data to display.
	 *	@param	maxLabels		Maximum width value for initial table
	 *							columns.
	 *	@param	sortColumn		Column on which to sort table.
	 *	@param	totalWordCount	Total number of words.
	 *
	 *	@return					ResultsPanel with table and title.
	 */

	protected ResultsPanel generateResults
	(
		WordHoardSortedTableModel model ,
		String[] maxLabels ,
		int sortColumn ,
		int totalWordCount
	)
	{
								//	Get number of rows of output data.

		int rowCount	= model.getRowCount();

								//	Construct title.

		StringBuffer sb	= new StringBuffer();
		ArrayList oo	= new ArrayList();

								//	Word type extracted and
								//	analysis text name.
		sb.append
		(
			WordHoardSettings.getString
			(
				"MultiwordUnitExtractionFrom" ,
		   		"Multiword Unit Extraction of %s from \"%s\".\n"
			)
		);
								//	Get the word form.

		String wordFormStringForDisplay	= pluralWordFormString.toLowerCase();

		oo.add( wordFormStringForDisplay );
		oo.add(
			analysisText.toString( useShortWorkTitlesInHeaders ) );

								//	Indicate if case/diacritical insensitive.

		if ( ignoreCaseAndDiacriticalMarks )
		{
			sb.append
			(
				WordHoardSettings.getString
				(
					"Thewordsarecaseanddiacriticalinsensitive" ,
					"The %s are case and diacritical insensitive.\n"
				)
			);

			oo.add( wordFormStringForDisplay );
		}
								//	Total number of multiword units extracted.
		sb.append
		(
			WordHoardSettings.getString
			(
				"unfilteredmultiwordunitswereextractedfromnwords" ,
				"%s unfiltered multiword units were extracted from %s %s.\n"
			)
		);

		oo.add
		(
			Formatters.formatIntegerWithCommas( mwusToReportOn )
		);

		oo.add
		(
			Formatters.formatIntegerWithCommas( totalWordCount )
		);

		oo.add( wordFormStringForDisplay );

								//	Number of multiword units which
								//	occurred only once and were ignored.

		if ( filterSingleOccurrences )
		{
			sb.append
			(
				WordHoardSettings.getString
				(
					"multiwordunitswhichoccurredonlyoncewereremoved" ,
					"%s multiword units which occurred only once were removed" +
					" leaving %s.\n"
				)
			);

			oo.add
			(
				Formatters.formatIntegerWithCommas( onceOnly )
			);

			oo.add
			(
				Formatters.formatIntegerWithCommas( mwusToReportOn - onceOnly )
			);
		}
								//	Number of multiword units selected
								//	by localmaxs.

		if ( filterUsingLocalMaxs )
		{
			sb.append
			(
				WordHoardSettings.getString
				(
					"Fromtheselocalmaxsselectedusing" ,
					"From these localmaxs selected %s " +
					"using %s as the measure of association.\n"
				)
			);

			oo.add
			(
				Formatters.formatIntegerWithCommas( acceptedByLocalMaxs )
			);

			oo.add
			(
				AnalysisDialog.getAssociationMeasureString(
					associationMeasure )
			);
		}

		if	(	filterBigramsByWordClass ||
				filterTrigramsByWordClass ||
				filterMultiwordUnitsContainingVerbs
			)
		{
			sb.append
			(
				WordHoardSettings.getString
				(
					"multiwordunitswereremovedbywordclassfilters" ,
					"%s multiword units were removed by the word class filters.\n"
				)
			);

			oo.add
			(
				Formatters.formatIntegerWithCommas(
					rejectedByWordClassFilters )
			);
		}
								//	Number of multiword units which passed
								//	all the filters.
		sb.append
		(
			WordHoardSettings.getString
			(
				"Atotalofmultowordunitspassedallthefilters" ,
				"A total of %s multiword units passed all the filters.\n"
			)
		);

		oo.add
		(
			Formatters.formatIntegerWithCommas( rowCount )
		);

		sb.append
		(
			WordHoardSettings.getString
			(
				"Associationmeasuresformultiwordunitswithmorethantwowords" +
					"areapproximate" ,
				"Association measures for multiword units with more than " +
					"two words are approximations computed using a " +
					"pseudo-bigram transformation.\n\n"
			)
		);

		PrintfFormat titleFormat	=
			new PrintfFormat
			(
				sb.toString()
			);

		String title	=
			titleFormat.sprintf( (Object[])oo.toArray( new Object[]{} ) );

		PrintfFormat shortTitleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"MultiwordUnitExtractionFrom" ,
		   			"Multiword Unit Extraction of %s from \"%s\"\n."
				)
			);

		String shortTitle	=
			shortTitleFormat.sprintf
			(
				new Object[]
				{
					wordFormStringForDisplay ,
					analysisText.toString(
						useShortWorkTitlesInWindowTitles )
				}
			);
								//	Set column long values.

		String[] columnLongValues	=
			(String[])model.getColumnNames().clone();

		columnLongValues[ 0 ]	= maxLabels[ 0 ];
		columnLongValues[ 1 ]	= maxLabels[ 1 ];

								//	Call super to generate output table.

		ResultsPanel results	=
			super.generateResults
			(
				new Spelling() ,
				title ,
				shortTitle ,
				columnLongValues ,
				new String[]
				{
					"%s" ,						/* mwu text */
					"%s" ,						/* word classes */
					getDoubleFormat( 0 ) ,		/* mwu length */
					getDoubleFormat( 0 ) ,		/* mwu count */
					getDoubleFormat( 4 ) ,		/* dice */
					getDoubleFormat( 2 ) ,		/* log like */
					getDoubleFormat( 4 ) ,		/* phi squared */
					getDoubleFormat( 4 ) ,		/* si */
					getDoubleFormat( 4 ) ,		/* scp */
				} ,
				sortColumn ,
				LOGLIKECOLUMN ,
				WORDCLASSESCOLUMN ,
				model ,
				maxLabels
			);

							//	Always need a horizontal scroll bar.

		resultsScrollPane.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );

		if ( !showWordClasses )
		{
			resultsTable.hideColumn( WORDCLASSESCOLUMN );
		}

		return results;
	}

	/**	Is cloud output available?
	 *
	 *	@return		true if cloud output available, false otherwise.
	 */

	public boolean isCloudAvailable()
	{
		return
			(	( resultsTable != null ) &&
				( resultsTable.getRowCount() > 1 ) );
	}

	/**	Are result options available?
	 *
	 *	@return		true if result options are available, false otherwise.
	 */

	public boolean areResultOptionsAvailable()
	{
		return true;
	}

	/**	Return result options.
	 *
	 *	@return		Result options in a LabeledColumn.
	 */

	public LabeledColumn getResultOptions()
	{
								//	Create LabeledColumn to hold options.

		LabeledColumn resultOptions	= new LabeledColumn();

								//	Add compress values for cloud option.
		resultOptions.addPair
		(
			"" ,
			createCompressValueRangeInTagCloudsCheckBox()
		);
								//	Add cloud association measure option.
		resultOptions.addPair
		(
			WordHoardSettings.getString
			(
				"Cloudassociationmeasure" ,
				"Cloud Association Measure"
			) ,
			createCloudAssociationMeasuresComboBox()
		);

		return resultOptions;
	}

	/**	Show tag cloud of Dunning's log-likelihood profile.
	 */

	public ResultsPanel getCloud()
	{
		String title	=
			WordHoardSettings.getString
			(
				"Tagclouddisplayofmultiwordunitassociationmeasures" ,
				"Tag cloud display of multiword unit association measures"
			);

		PrintfFormat header1Format	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"MultiwordUnitExtractionFrom" ,
					"Multiword Unit Extraction of %s from \"%s\"."
				)
			);

		String wordFormStringForDisplay	= pluralWordFormString.toLowerCase();

		String header1	=
			header1Format.sprintf
			(
				new Object[]
				{
					wordFormStringForDisplay ,
					analysisText.toString(
						useShortWorkTitlesInHeaders ) ,
				}
			);

		String header2	=
			WordHoardSettings.getString
			(
				"Thelargerthetag" ,
				"The larger the tag, the greater its log-likelihood value."
			);

		int cloudMeasure	= AnalysisDialog.getCloudMeasure();
		int measureColumn	= LOGLIKECOLUMN;

		switch ( cloudMeasure )
		{
			case AnalysisDialog.DICE		:
				measureColumn	= DICECOLUMN;
				break;

			case AnalysisDialog.LOGLIKE		:
				measureColumn	= LOGLIKECOLUMN;
				break;

			case AnalysisDialog.PHISQUARED	:
				measureColumn	= PHISQUAREDCOLUMN;
				break;

			case AnalysisDialog.SI			:
				measureColumn	= SICOLUMN;
				break;

			case AnalysisDialog.SCP			:
				measureColumn	= SCPCOLUMN;
				break;
		}

		PrintfFormat header3Format	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"Usingsastheassociationmeasure" ,
					"Using %s as the association measure."
				)
			);

		String cloudMeasureString	=
			AnalysisDialog.getAssociationMeasureString( cloudMeasure );

		String header3	=
			header3Format.sprintf
			(
				new Object[]{ cloudMeasureString }
			);

		String header4	= "";

		compressValueRangeInTagClouds	=
			AnalysisDialog.getCompressValueRangeInTagClouds();

		boolean compressRange			=
			compressValueRangeInTagClouds &&
			( measureColumn	== LOGLIKECOLUMN );

		if ( compressRange )
		{
			header4	=
				WordHoardSettings.getString
				(
					"Valuerangeiscompressed" ,
					"The range of log-likelihood values has been compressed."
				);
		}

		String header6	=
				WordHoardSettings.getString
				(
					"Youmayclickonatagtoremoveit" ,
					"You may click on a tag to remove it."
				);

		return getCloud
		(
			title ,
			new String[]{ header1 , header2 , header3 , header4 , header6 } ,
			compressRange ,
			measureColumn ,
			-1 ,
			WORDCLASSESCOLUMN
		);
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


