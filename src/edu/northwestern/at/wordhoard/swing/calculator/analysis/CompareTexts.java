package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	Generates measures of similarity between two text sets.
 */

public class CompareTexts
	extends FrequencyAnalysisRunnerBase
	implements AnalysisRunner
{
    /**	Create a multiple word form frequency profile object.
     */

	public CompareTexts()
	{
		super
		(
			AnalysisDialog.COMPARETEXTS
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

									//	Update progress display.

		if ( displayProgress && ( progressReporter != null ) )
		{
			progressReporter.setTitle
			(
				WordHoardSettings.getString
				(
					"Computingsimilaritystatistics" ,
					"Computing similarity statistics"
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
								//	Set output column names.

    	String[] columnNames	=
    		new String[]
	    	{
				WordHoardSettings.getString
				(
					"coltitleWorkscompared" ,
					"<html>Works<br>compared<br></html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleCosineSimilarity" ,
					"<html>Cosine<br>similarity</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleBinaryCosineSimilarity" ,
					"<html>Binary<br>cosine<br>similarity</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleBinaryDiceCoefficient" ,
					"<html>Binary<br>Dice<br>coefficient</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleBinaryJaccardCoefficient" ,
					"<html>Binary<br>Jaccard<br>coefficient</html>"
				) ,
				WordHoardSettings.getString
				(
					"coltitleBinaryOverlapCoefficient" ,
					"<html>Binary<br>overlap<br>coefficient</html>"
				)
    		};
								//	Create table model to hold results.
		model		=
			new WordHoardSortedTableModel( columnNames , 0 , true );

								//	Load analysis text word maps.
								//	What gets loaded will depend upon
								//	how we are breaking down the
								//	analysis text.

		WordCounter[] analysisParts;

		switch ( analysisTextBreakdownBy )
		{
			case AnalysisDialog.AGGREGATEALLWORKPARTS	:
				analysisParts		= new WordCounter[ 1 ];
				analysisParts[ 0 ]	= analysisText;
				break;

			case AnalysisDialog.BREAKDOWNBYWORKPARTS	:
				WorkPart[] workParts	=
					WorkUtils.expandWorkPartsToArray
					(
						analysisText.getActualWorkParts()
					);

				analysisParts			= new WordCounter[ workParts.length ];

				for ( int i = 0 ; i < workParts.length ; i++ )
				{
					analysisParts[ i ]	= new WordCounter( workParts[ i ] );
				}

				break;

			default     				:
				Work[] works			= analysisText.getWorks();

				analysisParts			= new WordCounter[ works.length ];

				for ( int i = 0 ; i < works.length ; i++ )
				{
					analysisParts[ i ]	= new WordCounter( works[ i ] );
				}

				break;
		}
								//	Load reference text word maps.
								//	What gets loaded will depend upon
								//	how we are breaking down the
								//	reference text.

		WordCounter[] referenceParts;

		switch ( referenceTextBreakdownBy )
		{
			case AnalysisDialog.AGGREGATEALLWORKPARTS	:
				referenceParts		= new WordCounter[ 1 ];
				referenceParts[ 0 ]	= referenceText;
				break;

			case AnalysisDialog.BREAKDOWNBYWORKPARTS	:
				WorkPart[] workParts	=
					WorkUtils.expandWorkPartsToArray
					(
						referenceText.getActualWorkParts()
					);

				referenceParts			= new WordCounter[ workParts.length ];

				for ( int i = 0 ; i < workParts.length ; i++ )
				{
					referenceParts[ i ]	= new WordCounter( workParts[ i ] );
				}

				break;

			default     				:
				Work[] works			= referenceText.getWorks();

				referenceParts			= new WordCounter[ works.length ];

				for ( int i = 0 ; i < works.length ; i++ )
				{
					referenceParts[ i ]	= new WordCounter( works[ i ] );
				}

				break;
		}
								//	Number of count maps to create.

		int mapsToCreate		=
			analysisParts.length + referenceParts.length;

		int reportingInterval	= mapsToCreate / 100;

		if ( reportingInterval < 10 ) reportingInterval = 10;

		if ( displayProgress )
		{
			progressReporter.setMaximumBarValue( mapsToCreate );

			progressReporter.setIndeterminate( false );
		}
								//	Load word count maps.

		Map[] analysisMaps		= new Map[ analysisParts.length ];
		String[] analysisLabels	= new String[ analysisParts.length ];

		Map[] referenceMaps			= new Map[ referenceParts.length ];
		String[] referenceLabels	= new String[ referenceParts.length ];

		int k	= 0;

		for ( int i = 0 ; i < analysisParts.length ; i++ )
		{
			WordCounter wc		= analysisParts[ i ];
			analysisMaps[ i ]	= wc.getWordsAndCounts( wordForm )[ 0 ];
//			analysisLabels[ i ]	= wc.getTag();
			analysisLabels[ i ]	=
				getWordCounterLabel( wc , analysisTextBreakdownBy );

			if ( displayProgress &&
				( ( k++ % reportingInterval ) == 0 ) )
			{
				progressReporter.updateProgress( k );

				if ( progressReporter.isCancelled() ) break;
			}
		}

		if ( !isCancelled( progressReporter ) )
		{
			for ( int i = 0 ; i < referenceParts.length ; i++ )
			{
				WordCounter wc			= referenceParts[ i ];

				referenceMaps[ i ]		=
					wc.getWordsAndCounts( wordForm )[ 0 ];

//				referenceLabels[ i ]	= wc.getTag();
				referenceLabels[ i ]	=
					getWordCounterLabel( wc , referenceTextBreakdownBy );

				if ( displayProgress &&
					( ( k++ % reportingInterval ) == 0 ) )
				{
					progressReporter.updateProgress( k );

					if ( progressReporter.isCancelled() ) break;
				}
			}
		}
								//	Number of comparisons to perform.

		int comparisonsToDo		=
			analysisParts.length * referenceParts.length;

		reportingInterval		= comparisonsToDo / 100;

		if ( reportingInterval < 10 ) reportingInterval = 10;

								//	Update progress bar limits with
								//	number of comparisons.

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
				if ( comparisonsToDo < 2000 )
				{
					progressReporter.updateProgress
					(
						WordHoardSettings.getString
						(
							"Computingsimilaritiesbetweenanalysisandreferencetext" ,
							"Computing similarities between analysis and reference texts"
						)
					);
				}
				else
				{
					progressReporter.updateProgress
					(
						WordHoardSettings.getString
						(
							"Computingsimilaritiesbetweenanalysisandreferencetextmaytakeawhile" ,
							"Computing similarities between analysis and reference texts"
						)
					);
				}

				progressReporter.updateProgress( 0 );
				progressReporter.setMaximumBarValue( comparisonsToDo );

				progressReporter.setIndeterminate( false );
			}
		}
								//	No comparisons done yet.

		int comparedCount		= 0;

								//	Tracks comparisons done to avoid
								//	duplicate output.

		HashMap comparisonsDone	= new HashMap();

								//	Keep track of maximum label width.

		String maxLabel			= "";
		int maxLabelWidth		= 0;

								//	Remember if analysis text or reference text
								//	is a word set or phrase set.

		boolean analysisIsWordSet	=
			analysisText.isWordSet() || analysisText.isPhraseSet();

		boolean referenceIsWordSet	=
			referenceText.isWordSet() || referenceText.isPhraseSet();

		boolean haveWordSet			= analysisIsWordSet || referenceIsWordSet;

								//	Format for table row labels.

		PrintfFormat labelFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"SimilaritiesLabel" ,
					"%s with %s"
				)
			);
								//	Loop over each work in analysis text and
								//	compute similarity statistics against
								//	each work in reference text.

		for ( int i = 0 ; i < analysisMaps.length ; i++ )
		{
			String analysisTag		= analysisLabels[ i ];

			if ( analysisIsWordSet )
			{
				analysisTag	= analysisTag + "(a)";
			}

			Map analysisMap			= analysisMaps[ i ];

			for ( int j = 0 ; j < referenceMaps.length ; j++ )
			{
				String referenceTag	= referenceLabels[ j ];

				if ( referenceIsWordSet )
				{
					referenceTag	= referenceTag + "(r)";
				}

				Map referenceMap	= referenceMaps[ j ];

				String label			=
					labelFormat.sprintf
					(
						new Object[]
						{
							analysisTag  ,
							referenceTag
						}
					);

				String reversedLabel	=
					labelFormat.sprintf
					(
						new Object[]
						{
							referenceTag ,
							analysisTag
						}
					);

				int labelLength		= label.length();

				if ( labelLength > maxLabelWidth )
				{
					maxLabelWidth	= labelLength;
					maxLabel		= label;
				}
								//	Compute similarity measures.

				boolean doit	= ( comparisonsDone.get( label ) == null );

				if ( !haveWordSet )
				{
					doit	=
						doit &&
						!analysisParts[ i ].equals( referenceParts[ j ] );
				}

				if ( doit )
				{
	                double[] similarities	=
    	            	computeDocumentSimilarities(
    	            		analysisMap , referenceMap );

								//	If the similarities consists solely of
								//	NAN and 0 (at least one NAN present),
								//	do not store them.  Most likely this
								//	comes from a work part with no
								//	word occurrences, e.g., a cast list.
						        //	Otherwise, add similarities
						        //	to report table.

					if ( similaritiesAreOK( similarities ) )
					{
						model.add
						(
							new FrequencyAnalysisDataRow(
								label , similarities )
						);
					}
								//	Remember this comparison done.

					comparisonsDone.put( label , "1" );
					comparisonsDone.put( reversedLabel , "1" );
				}
								//	Update count of comparisons done.

				comparedCount++;

								//	Update progress dialog.

				if ( displayProgress &&
					( ( comparedCount % reportingInterval ) == 0 ) )
				{
					progressReporter.updateProgress( comparedCount );

					if ( progressReporter.isCancelled() ) break;
				}
			}
		}
								//	Close progress dialog, if any.

		boolean cancelled	= closeProgressReporter();

								//	Return results.
		resultsPanel	=
			cancelled ?
				null :
				generateResults( model , maxLabel + 20 );
	}

	/**	Get label for word counter.
	 *
	 *	@param	wordCounter		The word counter for the table entry.
	 *	@param	breakdownBy		The method of breaking down the texts.
	 *
	 *	@return						The label for the word counter.
	 */

	public static String getWordCounterLabel
	(
		WordCounter wordCounter ,
		int breakdownBy
	)
	{
		String result	= wordCounter.getTag();

		switch ( breakdownBy )
		{
			case AnalysisDialog.AGGREGATEALLWORKPARTS	:
				if ( wordCounter.isCorpus() )
				{
					result	= wordCounter.toString();
				}

				break;

			case AnalysisDialog.BREAKDOWNBYWORKPARTS	:
				break;

			default     				:
				if ( wordCounter.isWork() )
				{
					result	= wordCounter.toString();
				}

				break;
		}

		return result;
	}

	/**	Determine if similarity values are OK.
	 *
	 *	@param	similarities	The similarities to check.
	 *
	 *	@return					true if the similarities appear OK,
	 *							false if they consist solely of NANs
	 *							and 0.0 values, with at least one NAN.
	 */

	public boolean similaritiesAreOK( double[] similarities )
	{
		int nanCount		= 0;
		int zeroCount		= 0;
		int nonZeroCount	= 0;

		for ( int i = 0 ; i < similarities.length ; i++ )
		{
			double similarity	= similarities[ i ];

			if ( Double.isNaN( similarity ) )
			{
				nanCount++;
			}
			else if ( similarity == 0.0D )
			{
				zeroCount++;
			}
			else
			{
				nonZeroCount++;
			}
		}

		return !(( nanCount > 0 ) && ( nonZeroCount == 0 ));
	}

	/**	Compute document similarity measures given two count maps.
	 *
	 *	@param	countMap1	First count map.
	 *	@param	countMap2	Second count map.
	 *
	 *	@return				Array with four doubles.
	 *						[0]	= cosine similarity
	 *						[1]	= Dice similarity
	 *						[2]	= Jaccard similarity
	 *						[3]	= Overlap similarity.
	 */

	public static double[] computeDocumentSimilarities
	(
		Map countMap1 ,
		Map countMap2
	)
	{
		double[] results			= new double[ 5 ];

		double[] map1Counts			=
			CountMapUtils.getSummaryCountsFromCountMap( countMap1 );

		double[] map2Counts			=
			CountMapUtils.getSummaryCountsFromCountMap( countMap2 );

		Map weightedMap1			=
			CountMapUtils.scaleCountMap(
				countMap1 , 1.0D / map1Counts[ 0 ] );

		Map weightedMap2			=
			CountMapUtils.scaleCountMap(
				countMap2 ,  1.0D / map2Counts[ 0 ] );

		map1Counts					=
			CountMapUtils.getSummaryCountsFromCountMap( weightedMap1 );

		map2Counts					=
			CountMapUtils.getSummaryCountsFromCountMap( weightedMap2 );

		double sumOfCrossProducts	=
			CountMapUtils.getSumOfCrossProducts(
				weightedMap1 , weightedMap2 );

		results[ 0 ]	=
			sumOfCrossProducts /
				Math.sqrt
				(
					(double)map1Counts[ 1 ] *
					(double)map2Counts[ 1 ]
				);

		weightedMap1			=
			CountMapUtils.booleanizeCountMap( countMap1 );

		weightedMap2			=
			CountMapUtils.booleanizeCountMap( countMap2 );

		map1Counts					=
			CountMapUtils.getSummaryCountsFromCountMap( weightedMap1 );

		map2Counts					=
			CountMapUtils.getSummaryCountsFromCountMap( weightedMap2 );

		sumOfCrossProducts	=
			CountMapUtils.getSumOfCrossProducts(
				weightedMap1 , weightedMap2 );

		results[ 1 ]	=
			sumOfCrossProducts /
				Math.sqrt
				(
					(double)map1Counts[ 1 ] *
					(double)map2Counts[ 1 ]
				);


		results[ 2 ]	=
			( 2.0D * sumOfCrossProducts ) /
				(
					(double)map1Counts[ 1 ] +
					(double)map2Counts[ 1 ]
				);

		results[ 3 ]	=
			sumOfCrossProducts /
			(
				(double)map1Counts[ 1 ] +
				(double)map2Counts[ 1 ] -
				(double)sumOfCrossProducts
			);

		results[ 4 ]	=
			sumOfCrossProducts /
				(
					Math.min
					(
						(double)map1Counts[ 1 ] ,
						(double)map2Counts[ 1 ]
					)
				);

		return results;
	}

	/**	Displays results of text comparison in a sorted table.
	 *
	 *	@param	model			Table model holding data to display.
	 *	@param	maxLabel		Maximum width value for first table column.
	 *
	 *	@return					ResultsPanel with table and title.
	 */

	protected ResultsPanel generateResults
	(
		WordHoardSortedTableModel model ,
		String maxLabel
	)
	{
		int rowCount	= model.getRowCount();

		String titleFormatString	=
			WordHoardSettings.getString
			(
				"SimilarityofTextsTitle" ,
		    	"Similarity of \"%s\" %s to \"%s\" %s using %s."
		    );

		PrintfFormat titleFormat	=
			new PrintfFormat
			(
				titleFormatString
			);

		String wordFormStringForDisplay	= pluralWordFormString.toLowerCase();

		String title	=
			titleFormat.sprintf
			(
				new Object[]
				{
					analysisText.toString( useShortWorkTitlesInHeaders ) ,
					AnalysisDialog.getTextBreakdownMethod(
						analysisTextBreakdownBy ) ,
					referenceText.toString( useShortWorkTitlesInHeaders ) ,
					AnalysisDialog.getTextBreakdownMethod(
						referenceTextBreakdownBy ) ,
					wordFormStringForDisplay
				}
			);

		PrintfFormat shortTitleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"Similarityofsands" ,
					"Similarity of \"%s\" and \"%s\""
				)
			);

		String shortTitle	=
			shortTitleFormat.sprintf
			(
				new Object[]
				{
					analysisText.toString(
						useShortWorkTitlesInWindowTitles ),
					referenceText.toString(
						useShortWorkTitlesInWindowTitles )
				}
			);

		ResultsPanel results	=
			super.generateResults
			(
				new Spelling() ,
				title ,
				shortTitle ,
				model.getColumnNames() ,
				new String[]
				{
					"%s" ,
					getDoubleFormat( 4 ) ,
					getDoubleFormat( 4 ) ,
					getDoubleFormat( 4 ) ,
					getDoubleFormat( 4 ) ,
					getDoubleFormat( 4 )
				} ,
				0 ,
				-1 ,
				-1 ,
				model ,
				new String[]{ maxLabel }
			);

		resultsScrollPane.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );

		return results;
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


