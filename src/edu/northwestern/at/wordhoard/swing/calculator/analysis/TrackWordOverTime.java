package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.plots.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;
import edu.northwestern.at.wordhoard.swing.text.*;

/**	Generates a longitudinal profile of a word form's frequency of use.
 */

public class TrackWordOverTime
	extends FrequencyAnalysisRunnerBase
	implements AnalysisRunner
{
	/**	Normalized frequency column. */

	protected final static int FREQCOLUMN	= 5;

	/**	Chart panel. */

	protected BarChartPanel barChartPanel;

	/**	Main results panel.  */

	protected ResultsPanel mainResultsPanel;

    /**	Create a single word form historical frequency profile object.
     */

	public TrackWordOverTime()
	{
		super
		(
			AnalysisDialog.SINGLEWORDFORMHISTORY
		);
	}

	/**	Analyze a single year.
	 *
	 *	@param	yearWordCount	Count of work to analyze in this year.
	 *	@param	yearTotalCount	Total number of words in this year.
	 *	@param	year			The year.
	 *	@param	worksThisYear	The number of works in this year.
	 *	@param	refTotalCount	The count of words in the reference corpus.
	 *	@param	worksInRef		The number of works in the reference corpus.
	 *	@param	model			Table model holding results.
	 *	@param	progressReporter	The progress dialog.
	 *
	 *	@return					true if analysis successful.
	 */

	protected boolean analyzeOneYear
	(
		int yearWordCount ,
		int yearTotalCount ,
		int year ,
		int worksThisYear ,
		int refTotalCount ,
		int worksInRef ,
		WordHoardSortedTableModel model ,
		ProgressReporter progressReporter
	)
	{
								//	Perform profile analysis of
								//	yearly frequencies.
		doFreq
		(
			wordToAnalyze ,
			year ,
			worksThisYear ,
			worksInRef ,
			yearWordCount ,
			yearTotalCount ,
			refTotalCount ,
			model
		);
								//	Close any open progress dialog.

		if ( progressReporter != null )
		{
			progressReporter.updateProgress
			(
				year ,
				WordHoardSettings.getString
				(
					"Computingfrequencystatistics" ,
					"Computing frequency statistics"
				)
			);
		}

		return true;
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

								//	Create progress dialog.

		if ( displayProgress && ( progressReporter != null ) )
		{
			progressReporter.setTitle
			(
				WordHoardSettings.getString
				(
					"Calculating" ,
					"Calculating"
				)
			);

			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Calculatingfrequencyprofile" ,
					"Calculating frequency profile"
				)
			);

			progressReporter.setIndeterminate( true );
		}
								//	Get the word count maps.

		Map[] countMaps	=
			analysisText.getWordFormCountByYear
			(
				wordToAnalyze ,
				wordForm ,
				analysisText.isPhraseSet() && analyzePhraseFrequencies
			);
								//	We get back three count maps.
								//	"yearWordMap" contains the count
								//	of the selected word form for each year.
								//
								//	"yearTotalWordMap" contains the
								//	total count of all word forms for
								//	each year.
								//
								//	"yearWorksMap" contains the count
								//	of the works in a year.
								//

		Map yearWordMap			= countMaps[ 0 ];
		Map yearTotalWordMap	= countMaps[ 1 ];
		Map yearWorksMap		= countMaps[ 2 ];

								//	Get total words in the reference corpus.

//		int refTotalCount	=
//			analysisText.getTotalWordFormCount( wordForm );

		Iterator iterator	= yearWordMap.keySet().iterator();

		int refTotalCount	= 0;

		while ( iterator.hasNext() )
		{
			Integer year		=	(Integer)(iterator.next());

			refTotalCount	+=
				((Integer)yearTotalWordMap.get( year )).intValue();
		}
								//	Get next year in source map.
								//	Update progress bar limits with
								//	number of works.

		if ( displayProgress )
		{
			if ( progressReporter.isCancelled() )
			{
				closeProgressReporter();
				return;
			}
			else
			{
				progressReporter.setMaximumBarValue( yearWordMap.size() );
				progressReporter.setIndeterminate( false );
			}
		}
								//	Get column names for result table.

    	String[] columnNames	=
    		new String[]
	    	{
	    		WordHoardSettings.getString
	    		(
	    			"coltitleYear" ,
	    			"<html><br>Year</html>"
				) ,
	    		WordHoardSettings.getString
	    		(
					"coltitleWorks" ,
					"<html><br>Works</html>"
				) ,
	    		WordHoardSettings.getString
	    		(
	    			"coltitleSelectedount" ,
					"<html>Selected<br>count</html>"
				) ,
	    		WordHoardSettings.getString
	    		(
	    			"coltitleTotalcount" ,
					"<html>Total<br>count</html>"
				) ,
	    		WordHoardSettings.getString
	    		(
	    			"coltitleMeancount" ,
					"<html>Mean<br>count</html>"
				) ,
	    		WordHoardSettings.getString
	    		(
	    			"coltitleNormalizedcount" ,
					"<html>Normalized<br>count</html>"
				)
    		};
						        //	Get sorted table model to hold results.
		model	=
			new WordHoardSortedTableModel( columnNames , 0 , true );

								//	Now yearMap contains combined word counts
								//	for each year in which at least one work
								//	was published. 	Compute the frequency
								//	results for each year.

		iterator	= yearWordMap.keySet().iterator();

								//	Loop over all words in source map.

		while ( iterator.hasNext() )
		{
								//	Get next year in source map.

			Integer year		=	(Integer)(iterator.next());

								//	Get word count for this year.

			int yearWordCount	=
				((Integer)yearWordMap.get( year )).intValue();

								//	Get number of works in this year.

			int worksThisYear	=
				((Integer)yearWorksMap.get( year )).intValue();

								//	Get total number of words for this year.

			int yearTotalCount	=
				((Integer)yearTotalWordMap.get( year )).intValue();

								//	If the total number of words is less
								//	than the threshhold, ignore this year.

								//	Compute frequency statistics for this year.
			analyzeOneYear
			(
				yearWordCount ,
				yearTotalCount ,
				year.intValue() ,
				worksThisYear ,
				refTotalCount ,
				yearWordMap.size() ,
				model ,
				progressReporter
			);
								//	See if analysis cancelled.

			if ( displayProgress )
			{
				if ( progressReporter.isCancelled() ) break;
			}
		}
								//	Close progress dialog.

		boolean cancelled	= closeProgressReporter();

								//	Display the results if not cancelled.

		resultsPanel	=
			cancelled ? null : generateResults( model );
	}

	/**	Perform frequency comparison of year counts for all works in a corpus.
	 *
	 *	@param	wordToAnalyze		The word to analyze.
	 *	@param	year				The year being analyzed.
	 *	@param	worksThisYear		Number of works in this year.
	 *	@param	worksInRef			Total number of works for all years.
	 *	@param	yearWordCount		Count of word in texts for year.
	 *	@param	yearTotalCount		Total number of words in texts for year.
	 *	@param	refWorksTotalCount	Total number of words in reference corpus.
	 *	@param	model				Table model in which to store results.
	 *
	 *	<p>
	 *	Computes the following quantities for display for one year.
	 *	</p>
	 *
	 *	<ol>
	 *		<li>Number of works in the year.</li>
	 *		<li>Raw word count for selected word across all works
	 *			for the year.</li>
	 *		<li>Total word counts for all words in the year.</li>
	 *		<li>Average selected word counts for the year = (1) / (2)</li>
	 *		<li>Normalized word counts for the year.  Values depends
	 *			upon the normalization method selected in the
	 *			historical frequency profile dialog.</li>
	 *	</ol>
	 */

	protected void doFreq
	(
		Spelling wordToAnalyze ,
		int year ,
		int worksThisYear ,
		int worksInRef ,
		int yearWordCount ,
		int yearTotalCount ,
		int refWorksTotalCount ,
		WordHoardSortedTableModel model
	)
	{
		double[] freqAnal		= new double[ 5 ];

								//	Number of works in this year.

		freqAnal[ 0 ]			= worksThisYear;

								//	Count of selected word across all
								//	works in this year.

		freqAnal[ 1 ]			= yearWordCount;

								//	Total number of words in this year.

		freqAnal[ 2 ]			= yearTotalCount;

								//	Mean count of selected word for
								//	this year.

		freqAnal[ 3 ]			= freqAnal[ 1 ] / freqAnal[ 0 ];

								//	Normalized count.  If no normalization,
								//	will be the same as the mean count.

		freqAnal[ 4 ]			= freqAnal[ 3 ];

								//	Average number of words in a work in
								//	this corpus.

		double meanWorkLength	=
			(double)refWorksTotalCount / (double)worksInRef;

								//	Compute normalized count.

		switch ( frequencyNormalizationMethod )
		{
								//	Normalize to mean work length of
								//	10,000 words.

			case AnalysisDialog.NORMALIZETO10000			:
				freqAnal[ 4 ]	=
					10000.0D *
						( (double)freqAnal[ 1 ] / (double)yearTotalCount );
				break;

								//	Normalize to mean work length of all
								//	works analyzed here.

			case AnalysisDialog.NORMALIZETOMEANWORKLENGTH	:
				freqAnal[ 4 ]	=
					freqAnal[ 3 ] *
						( meanWorkLength /
							( (double)yearTotalCount / freqAnal[ 0 ] ) );
				break;

								//	Normalize to parts per million.

			case AnalysisDialog.NORMALIZETOPARTSPERMILLION	:
				freqAnal[ 4 ]	=
					freqAnal[ 3 ] *
						( 1000000.0D /
							( (double)yearTotalCount / freqAnal[ 0 ] ) );
				break;

			default	:
				break;
		}

		if ( roundNormalizedFrequencies )
		{
			freqAnal[ 4 ]	= Math.round( freqAnal[ 4 ] );
		}

		model.add(
			new FrequencyAnalysisDataRow(
				StringUtils.intToString( year ) , freqAnal ) );
	}

	/**	Displays results of frequency analysis in a sorted table.
	 *
	 *	@param	model	Table model containing results.
	 *
	 *	@return	The results panel.
	 */

	protected ResultsPanel generateResults
	(
		WordHoardSortedTableModel model
	)
	{
								//	Long title.

		StringBuffer sb	= new StringBuffer();

		PrintfFormat titleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"HistoricalFrequencyProfileTitle" ,
			    	"Historical profile of %s \"%s\" in \"%s\"."
			    )
			);

		sb.append
		(
			titleFormat.sprintf
			(
				new Object[]
				{
					wordFormString.toLowerCase() ,
					wordToAnalyze ,
					analysisText.toString(
						useShortWorkTitlesInHeaders )
				}
			)
		);
								//	Short title.

		PrintfFormat shortTitleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"HistoricalProfileofsins" ,
					"Historical profile of %s %s in \"%s\""
				)
			);

		final String shortTitle	=
			shortTitleFormat.sprintf
			(
				new Object[]
				{
					wordFormString.toLowerCase() ,
					wordToAnalyze ,
					analysisText.toString(
						useShortWorkTitlesInWindowTitles )
				}
			);
								//	If none of the works contained
								//	publication dates, say so and
								//	don't produce an empty table of
								//	word counts by year.

		if ( model.getRowCount() <= 0 )
		{
			PrintfFormat noDatesAvailableFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"Noneoftheworkshasanavailablepublicationdate" ,
						"None of the works in \"%s\" has an available" +
						" publication date."
					)
				);

			final String noDatesAvail	=
				noDatesAvailableFormat.sprintf
				(
					analysisText.toString(
						useShortWorkTitlesInHeaders )
				);

			final String title	= sb.toString();

			SwingRunner.runNow
			(
				new Runnable()
				{
					public void run()
					{
						mainResultsPanel	=
							generateEmptyResults
							(
								title ,
								shortTitle ,
								noDatesAvail
							);
					}
				}
			);
		}
		else
		{
								//	Compute log-likelihood chi-square
								//	goodness of fit for normalized
								//	frequencies across years.
								//	Must have at least two years for this.

			int numYears		= model.getRowCount();
/*
			if ( numYears > 1 )
			{
				double[] normFreqs	= new double[ numYears ];

				for ( int i = 0 ; i < numYears ; i++ )
				{
					normFreqs[ i ]	=
						((Double)model.getValueAt(
							i , FREQCOLUMN )).doubleValue();
				}

				double[] logLike	=
					OneWayTable.logLikelihoodChiSquare( normFreqs );

				PrintfFormat trackWordChiSquareFormat	=
					new PrintfFormat
					(
						WordHoardSettings.getString
						(
							"HistoricalFrequencyChiSquare" ,
				    		"\n\nLog-likelihood chi-square test for " +
				    		"homogeneity of normalized frequencies=%s, " +
				    		"p < %s ."
				    	)
					);

				sb.append
				(
					trackWordChiSquareFormat.sprintf
					(
						new Object[]
						{
							Formatters.formatDouble( logLike[ 0 ] , 4 ) ,
							Formatters.formatDouble( logLike[ 1 ] , 4 )
						}
					)
				);
			}
*/
			mainResultsPanel	=
				super.generateResults
				(
					wordToAnalyze ,
					sb.toString() ,
					shortTitle ,
					model.getColumnNames() ,
					new String[]
					{
						"%s" ,
						getDoubleFormat( 0 ) ,
						getDoubleFormat( 0 ) ,
						getDoubleFormat( 0 ) ,
						getDoubleFormat( 1 ) ,
						roundNormalizedFrequencies ?
							getDoubleFormat( 0 )	:
 							getDoubleFormat( 1 )
					} ,
					0 ,
					-1 ,
					-1 ,
					model ,
					new String[]{ "99999" }
				);
		}

		return mainResultsPanel;
	}

	/**	Generate results when publication dates available.
	 *
	 *	@param	title	The title.
	 *
	 *	@param	shortTitle	The short title.
	 *
	 *	@param	extraText	Extra text after the title.
	 *
	 *	@return		results panel.
	 */

	protected ResultsPanel generateEmptyResults
	(
		String title ,
		String shortTitle ,
		String extraText
	)
	{
								//	Get results panel.

		ResultsPanel results	= new ResultsPanel();

								//	Set title and header.

		results.setResultsTitle( shortTitle );
		results.setResultsHeader( title );

								//	Get a font manager.

		FontManager fontManager	= new FontManager();

								//	Get font for header.

		Font textFont			= fontManager.getFont( FONT_SIZE );

		StringBuffer sb			= new StringBuffer();
		sb.append( title );
		sb.append( "\n\n" );
		sb.append( extraText );

		XTextArea titleTextArea	= new XTextArea( sb.toString() );

		titleTextArea.setBackground( results.getBackground() );

		titleTextArea.setFont( textFont );

		titleTextArea.setEditable( false );

		JPanel titlePanel	= new JPanel();

		titlePanel.setLayout( new BorderLayout() );
		titlePanel.add( titleTextArea );

		results.add( titlePanel );

		return results;
	}

	/**	Show bar chart of historical frequency profile.
	 */

	public ResultsPanel getChart()
	{
		ResultsPanel chartPanel		= new ResultsPanel();

		String xTitle				=
			WordHoardSettings.getString
			(
				"PublicationYear" ,
				"Publication Year"
			);

		String yTitle				=
			WordHoardSettings.getString
			(
				"NormalizedFrequency" ,
				"Normalized Frequency"
			);

		PrintfFormat titleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"NormalizedFrequencyOfUse" ,
					"Normalized frequency of use of %s by publication year"
				)
			);

		String title	=
			titleFormat.sprintf( new Object[]{ wordToAnalyze } );

		int nYears				= model.getRowCount();

		String[] xAxisLabels	= new String[ nYears ];
		double[] yData			= new double[ nYears ];

		for ( int i = 0 ; i < nYears ; i++ )
		{
			xAxisLabels[ i ] 	=
				model.getValueAt( i , 0 ).toString();

			yData[ i ]	= ((Double)model.getValueAt( i , 5 )).doubleValue();
		}

		barChartPanel	=
			Plots.barChart
			(
				xAxisLabels ,
				yData ,
				600 ,
				350 ,
				title ,
				xTitle ,
				yTitle ,
				WordHoardSettings.getString
				(
					"NormalizedFrequency" ,
					"Normalized Frequency"
				) ,
				false ,
				false
			);

		chartPanel.add( barChartPanel );

		chartPanel.setResults( barChartPanel );
		chartPanel.setResultsHeader( title );
		chartPanel.setResultsTitle( title );

		return chartPanel;
	}

	/**	Is chart output available?
	 *
	 *	@return		true if chart output available, false otherwise.
	 */

	public boolean isChartAvailable()
	{
		return
			(	( resultsTable != null ) &&
				( resultsTable.getRowCount() > 1 ) );
	}

	/**	Saves chart. */

	public void saveChart()
	{
		Plots.saveChartToFile
		(
			WordHoardCalculatorWindow.getCalculatorWindow() ,
			barChartPanel.getChart()
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


