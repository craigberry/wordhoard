package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	Generates a word form frequency comparison between two sets of works.
 */

public class CompareMultipleWordFrequencies
	extends FrequencyAnalysisRunnerBase
	implements AnalysisRunner
{
	/**	Column containing word classes in output table.
	 */

	protected static final int WORDCLASSCOLUMN	= 1;

	/**	Column containing overuse values in output table.
	 */

	protected static final int OVERUSECOLUMN	= 4;

	/**	Column containing log-likelihood values in output table.
	 */

	protected static final int LOGLIKECOLUMN	= 7;

    /**	Create a multiple word form frequency profile object.
     */

	public CompareMultipleWordFrequencies()
	{
		super
		(
			AnalysisDialog.MULTIPLEWORDFORMCOMPARISON
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

									//	Create progress display.

		if ( displayProgress && ( progressReporter != null ) )
		{
			progressReporter.setTitle
			(
				WordHoardSettings.getString
				(
					"Computingfrequencystatistics" ,
					"Computing frequency statistics"
				)
			);

			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Computingfrequenciesinanalysisandreferencetext" ,
					"Computing frequencies in analysis and reference text"
				)
			);

			progressReporter.setIndeterminate( true );
		}

		long startTime			= System.currentTimeMillis();

								//	Get word forms, counts, and work counts
								//	for analysis text and reference text.

		Map[] countMaps			=
			analysisText.getWordsAndCounts( referenceText , wordForm );

		Map analysisWordCounts	= countMaps[ 0 ];
		Map referenceWordCounts	= countMaps[ 1 ];
		Map workCounts			= countMaps[ 2 ];

		long endTime			= System.currentTimeMillis() - startTime;

		startTime				= System.currentTimeMillis();

								//	Update progress bar limits with
								//	number of unique words in analysis text.

		if ( displayProgress )
		{
								//	If progress dialog cancelled,
								//	return null value.

			if ( progressReporter.isCancelled() )
			{
				closeProgressReporter();
				return;
			}
			else
			{
				progressReporter.setMaximumBarValue(
					analysisWordCounts.size() );

				progressReporter.setIndeterminate( false );
			}
		}
								//	Get total words in analysis and
								//	reference.

		int refTotalCount		=
			referenceText.getTotalWordFormCount( wordForm );

		int analysisTotalCount	=
			analysisText.getTotalWordFormCount( wordForm );

								//	Set output column names.

		String analysisPctColumnName	=
			getAnalysisPercentColumnName();

		String referencePctColumnName	=
			getReferencePercentColumnName();

		String[] columnNames	=
			new String[]
			{
				getColTitleWordFormString( wordFormString ),
				WordHoardSettings.getString
				(
					"coltitleWordclass" ,
					"<html>Word<br>class</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleAnalysiscount" ,
					"<html>Analysis<br>count</html>"
				) ,
				analysisPctColumnName ,
//				" " ,
				WordHoardSettings.getString
				(
					"coltitleRelativeuse" ,
					"<html>Relative<br>use</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleReferencecount" ,
					"<html>Reference<br>count</html>"
				) ,
				referencePctColumnName ,
				WordHoardSettings.getString
				(
					"coltitleLoglikelihood" ,
					"<html>Log<br>likelihood</html>"
				)
			};
								//	Create table model to hold results.
		model		=
			new WordHoardSortedTableModel( columnNames , 0 , true );

								//	Note if we're to skip proper names
								//	and we are processing lemma or spelling
								//	word forms.

		boolean skipProperNames	=
			filterOutProperNames &&
				(
					( wordForm == WordForms.SPELLING ) ||
					( wordForm == WordForms.LEMMA )
				);
								//	Loop over each word form and
								//	compute frequency profile statistics.

		Set keyset			= analysisWordCounts.keySet();

		int wordsDone		= 0;

		String maxLabel		= "";
		int maxLabelWidth	= 0;

		String maxLabel2	= "";
		int maxLabelWidth2	= 0;

		for	(	Iterator iterator	= keyset.iterator();
				iterator.hasNext();
			)
		{
								//	Get next word form.

			wordToAnalyze   		= (Spelling)iterator.next();
			String sWordToAnalyze	= wordToAnalyze.getString();

								//	Get count of word form in analysis text.

			Integer wordCount		=
				(Integer)analysisWordCounts.get( wordToAnalyze );

			if ( wordCount	== null ) wordCount	= new Integer( 0 );

								//	Get number of works in reference text
								//	collection in which this word form appears.

			Integer workCount		=
				(Integer)workCounts.get( wordToAnalyze );

			if ( workCount	== null ) workCount	= new Integer( 0 );

								//	Only compute statistics for word form
								//	if it appears at least the specified
								//	minimum number of times in the analysis
								//	text, and also in the specified minimum
								//	number of works in the reference text.

			if	(	( wordCount.intValue() >= minimumCount ) &&
					( workCount.intValue() >= minimumWorkCount ) )
			{
				int wordLength	= sWordToAnalyze.length();

								//	If we're filtering out probably proper
								//	names, then if the current word form
								//	is a lemma or a spelling, check if it
								//	is a proper name by seeing if its
								//	word class is "np".  If so, skip it.

				if	(	skipProperNames &&
						( sWordToAnalyze.indexOf( "(np)" ) >= 0 ) )
				{
								// Do nothing.
				}
				else
				{
					if ( wordLength > maxLabelWidth )
					{
						maxLabelWidth	= wordLength;
						maxLabel		= sWordToAnalyze;
					}

					String displayableWordClass	=
						WordUtils.stripSpelling( sWordToAnalyze );

					int wordLength2	= displayableWordClass.length();

					if ( wordLength2 > maxLabelWidth2 )
					{
						maxLabelWidth2	= wordLength2;
						maxLabel2		= displayableWordClass;
					}
								//	Get word form count in reference text.

					int refCount	= 0;

					if ( referenceWordCounts.containsKey( wordToAnalyze ) )
					{
						refCount	=
							((Integer)referenceWordCounts.get(
								wordToAnalyze )).intValue();
					}

					doFreq
					(
						wordToAnalyze ,
						wordForm ,
						wordCount.intValue() ,
						analysisTotalCount ,
						refCount ,
						refTotalCount ,
						percentReportMethod ,
						model
					);
				}
       	    }
								//	Update count of words done.
			wordsDone++;
								//	Update progress dialog.

			if ( displayProgress && ( ( wordsDone % 50 ) == 0 ) )
			{
				progressReporter.updateProgress
				(
					wordsDone ,
					WordHoardSettings.getString
					(
						"Computingfrequencystatistics" ,
						"Computing frequency statistics"
					)
				);

				if ( progressReporter.isCancelled() ) break;
			}
		}

		endTime	= System.currentTimeMillis() - startTime;

								//	Close progress dialog, if any.

		boolean cancelled	= closeProgressReporter();

								//	Return results.
		resultsPanel	=
			cancelled ?
				null :
				generateResults
				(
					wordToAnalyze ,
					model ,
					maxLabel ,
					maxLabel2 ,
					analysisWordCounts.keySet().size() ,
					analysisTotalCount ,
					referenceWordCounts.keySet().size() ,
					refTotalCount
				);
	}

	/**	Perform frequency comparison of analysis and reference works for a word.
	 *
	 *	@param	wordToAnalyze			The word to analyze.
	 *	@param	wordForm				The word form type.
	 *	@param	analysisCount			Count of word in analysis text.
	 *	@param	analysisTotalCount		Total number of words in analysis
	 *									text.
	 *	@param	refCount				Count of collocate in reference
	 *									text.
	 *	@param	refTotalCount			Total number of words in reference
	 *									text.
	 *	@param	percentReportMethod		Percent report method.
	 *	@param	model					Table model in which to store
	 *									results.
	 */

	public static void doFreq
	(
		Spelling wordToAnalyze ,
		int wordForm ,
		int analysisCount ,
		int analysisTotalCount ,
		int refCount ,
		int refTotalCount ,
		int percentReportMethod ,
		WordHoardSortedTableModel model
	)
	{
								//	Compute percents and log-likelihood value.

		double freqAnal[]	=
			Frequency.logLikelihoodFrequencyComparison
			(
            	analysisCount ,
            	refCount	,
            	analysisTotalCount ,
            	refTotalCount ,
            	false
            );
								//	Change percents, if necessary, to
								//	honor request percent report method.

		if	(	percentReportMethod ==
				AnalysisDialog.REPORTPARTSPERMILLION )
		{
			freqAnal[ 1 ]	= Math.round( freqAnal[ 1 ] * 10000.0D );
			freqAnal[ 3 ]	= Math.round( freqAnal[ 3 ] * 10000.0D );
		}
		else if	(	percentReportMethod ==
					AnalysisDialog.REPORTPARTSPER10000 )
		{
			freqAnal[ 1 ]	= freqAnal[ 1 ] * 100.0D;
			freqAnal[ 3 ]	= freqAnal[ 3 ] * 100.0D;
		}
						        //	Add data to report table.
		model.add
		(
			new FrequencyAnalysisDataRow
			(
				WordUtils.stripWordClass
				(
					WordUtils.getDisplayableWordText
					(
						wordToAnalyze ,
						wordForm
					)
				) ,
				WordUtils.stripSpelling
				(
					wordToAnalyze.toString()
				) ,
				new double[]
				{
					freqAnal[ 0 ] ,
					freqAnal[ 1 ] ,
					(double)Compare.compare(
						freqAnal[ 1 ] , freqAnal[ 3 ] ) ,
					freqAnal[ 2 ] ,
					freqAnal[ 3 ] ,
					freqAnal[ 4 ] ,
					freqAnal[ 5 ]
				}
			)
		);
	}

	/**	Displays results of frequency analysis in a sorted table.
	 *
	 *	@param	wordToAnalyze		Word form being analyzed.
	 *	@param	model				Table model holding data to display.
	 *	@param	maxLabel			Maximum width value for first
	 *								table column.
	 *	@param	maxLabel2			Maximum width value for second
	 *								table column.
	 *	@param	analysisDistinct	Distinct words in analysis text.
	 *	@param	analysisTotal		Total words in analysis text.
	 *	@param	referenceDistinct	Distinct words in reference text.
	 *	@param	referenceTotal		Total words in reference text.
	 *
	 *	@return						ResultsPanel with table and title.
	 */

	protected ResultsPanel generateResults
	(
		Spelling wordToAnalyze ,
		WordHoardSortedTableModel model ,
		String maxLabel ,
		String maxLabel2 ,
		int analysisDistinct ,
		int analysisTotal ,
		int referenceDistinct ,
		int referenceTotal
	)
	{
		int rowCount	= model.getRowCount();

		String title	= "";

		String wordFormStringForDisplay	=
			wordFormString.toLowerCase();

		String pluralWordFormStringForDisplay	=
			pluralWordFormString.toLowerCase();

		if ( wordForm != WordForms.ISVERSE )
		{
			StringBuffer sb	= new StringBuffer();

			sb.append
			(
				WordHoardSettings.getString
				(
					"multipleWordFrequencyProfileTitle" ,
					"Frequency profile of %s with respect to %s."
				)
			);

			if ( minimumCount > 1 )
			{
				sb.append
				(
					WordHoardSettings.getString
					(
						"multipleWordFrequencyProfileTitle2P" ,
						"  %s %s appeared at least %s times"
					)
				);
			}
			else
			{
				sb.append
				(
					WordHoardSettings.getString
					(
						"multipleWordFrequencyProfileTitle2S" ,
						"  %s %s appeared at least %s time"
					)
				);
			}

			sb.append( " " );

			if ( minimumWorkCount > 1 )
			{
				sb.append
				(
					WordHoardSettings.getString
					(
						"multipleWordFrequencyProfileTitle3P" ,
						" in %s works."
					)
				);
			}
			else
			{
				sb.append
				(
					WordHoardSettings.getString
					(
						"multipleWordFrequencyProfileTitle3S" ,
						" in %s work."
					)
				);
			}

			sb.append
			(
				WordHoardSettings.getString
				(
					"multipleWordFrequencyProfileTitle4" ,
					"  \"%s\" contains %s distinct % in %s %s."
				)
			);

			sb.append
			(
				WordHoardSettings.getString
				(
					"multipleWordFrequencyProfileTitle4" ,
					"  \"%s\" contains %s distinct %s in %s %s."
				)
			);

			PrintfFormat titleFormat	= new PrintfFormat( sb.toString() );

			if ( rowCount != 1 )
			{
				wordFormStringForDisplay	=
					pluralWordFormStringForDisplay;
			}

			title	=
				titleFormat.sprintf
				(
					new Object[]
					{
						analysisText.toString(
							useShortWorkTitlesInHeaders ) ,
						referenceText.toString(
							useShortWorkTitlesInHeaders ) ,
						StringUtils.formatNumberWithCommas( rowCount ) ,
						wordFormStringForDisplay ,
						StringUtils.formatNumberWithCommas(
							minimumCount ),
						StringUtils.formatNumberWithCommas(
							minimumWorkCount ) ,
						analysisText.toString(
							useShortWorkTitlesInHeaders ) ,
						StringUtils.formatNumberWithCommas(
							analysisDistinct ) ,
						wordFormStringForDisplay ,
						StringUtils.formatNumberWithCommas(
							analysisTotal ) ,
						( analysisTotal == 1 ) ?
							WordHoardSettings.getString
							(
								"occurrence" ,
								"occurrence"
							) :
							WordHoardSettings.getString
							(
								"occurrences" ,
								"occurrences"
							) ,
						referenceText.toString(
							useShortWorkTitlesInHeaders ) ,
						StringUtils.formatNumberWithCommas(
							referenceDistinct ) ,
						wordFormStringForDisplay ,
						StringUtils.formatNumberWithCommas(
							referenceTotal ) ,
						( referenceTotal == 1 ) ?
							WordHoardSettings.getString
							(
								"occurrence" ,
								"occurrence"
							) :
							WordHoardSettings.getString
							(
								"occurrences" ,
								"occurrences"
							)
					}
				);
		}
		else
		{
			PrintfFormat titleFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"multipleWordFrequencyProfileTitleForVerse" ,
						"Comparing frequencies of poetry versus prose in " +
						"\"%s\" and \"%s.\""
					)
				);

			title	=
				titleFormat.sprintf
				(
					new Object[]
					{
						analysisText.toString(
							useShortWorkTitlesInHeaders ) ,
						referenceText.toString(
							useShortWorkTitlesInHeaders )
					}
				);
		}

		PrintfFormat shortTitleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"Comparingmultiplewordfrequenciesinsands" ,
					"Comparing %s in \"%s\" and \"%s\""
				)
			);

		String shortTitle	=
			shortTitleFormat.sprintf
			(
				new Object[]
				{
					wordFormStringForDisplay ,
					analysisText.toString(
						useShortWorkTitlesInWindowTitles ) ,
					referenceText.toString(
						useShortWorkTitlesInWindowTitles ) ,
				}
			);

		ResultsPanel results	=
			super.generateResults
			(
				wordToAnalyze ,
				title ,
				shortTitle ,
				model.getColumnNames() ,
				new String[]
				{
					"%s" ,
					"%s" ,
					getDoubleFormat( 0 ) ,
					getPercentReportMethodFormat() ,
					getDoubleFormat( 0 ) ,
					getDoubleFormat( 0 ) ,
					getPercentReportMethodFormat() ,
					getDoubleFormat( 1 )
				} ,
				LOGLIKECOLUMN ,
				LOGLIKECOLUMN ,
				WORDCLASSCOLUMN ,
				model ,
				new String[]{ maxLabel , maxLabel2 }
			);

		if ( results != null )
		{
			resultsTable.setColumnRenderer
			(
				OVERUSECOLUMN ,
				new PlusOrMinusTableCellRenderer( colorCodeOveruseColumn )
			);
		}

		CompareSingleWordFrequencies.reorderCompareResultsTableColumns(
			resultsTable );
										//	Only show the word
										//	class column for spellings
		boolean showWC	=
			showWordClasses &&
			(	( wordForm	== WordForms.SPELLING ) ||
				( wordForm	== WordForms.LEMMA ) );

		if ( !showWC )
		{
			resultsTable.hideColumn( WORDCLASSCOLUMN );
		}

		return results;
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
		LabeledColumn resultOptions	= new LabeledColumn();

		resultOptions.addPair
		(
			"" ,
			createCompressValueRangeInTagCloudsCheckBox()
		);

		return resultOptions;
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

	/**	Show tag cloud of Dunning's log-likelihood profile.
	 */

	public ResultsPanel getCloud()
	{
		String title	=
			WordHoardSettings.getString
			(
				"Tagclouddisplayofloglikelihoodmeasures" ,
				"Tag cloud display of log-likelihood measures"
			);

		PrintfFormat headerFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"Comparingmultiplewordfrequenciesinsands" ,
					"Comparing %s in \"%s\" and \"%s\""
				)
			);

		String wordFormStringForDisplay	=
			wordFormString.toLowerCase();

		String header1	=
			headerFormat.sprintf
			(
				new Object[]
				{
					"frequencies" ,
					analysisText.toString(
						useShortWorkTitlesInWindowTitles ) ,
					referenceText.toString(
						useShortWorkTitlesInWindowTitles ) ,
				}
			);

		String header2	=
			WordHoardSettings.getString
			(
				"Thelargerthetag" ,
				"The larger the tag, the greater its log-likelihood value."
			);

		String header3	=
			WordHoardSettings.getString
			(
				"Tagsinblack" ,
				"Tags in black are overused in analysis text."
			);

		String header4	=
			WordHoardSettings.getString
			(
				"Tagsingrey" ,
				"Tags in grey are underrused in analysis text."
			);

		String header5	= "";

		compressValueRangeInTagClouds	=
			AnalysisDialog.getCompressValueRangeInTagClouds();

		if ( compressValueRangeInTagClouds )
		{
			header5	=
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
			new String[]
			{
				header1 , header2 , header3 , header4 , header5 , header6
			} ,
			compressValueRangeInTagClouds ,
			LOGLIKECOLUMN ,
			OVERUSECOLUMN ,
			WORDCLASSCOLUMN
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


