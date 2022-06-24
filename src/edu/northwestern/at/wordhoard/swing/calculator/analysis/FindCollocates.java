package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;
import edu.northwestern.at.wordhoard.swing.work.*;

/**	Compares a single word form's frequency in a specified analysis text.
 */

public class FindCollocates
	extends CollocateFrequencies
	implements AnalysisRunner
{
	/**	Output column indices.
	 */

	protected static final int TEXTCOLUMN			= 0;
	protected static final int WORDCLASSCOLUMN		= 1;
	protected static final int SPANCOUNTCOLUMN		= 2;
	protected static final int ANALYSISCOUNTCOLUMN	= 3;
	protected static final int DICECOLUMN			= 4;
	protected static final int LOGLIKECOLUMN		= 5;
	protected static final int PHISQUAREDCOLUMN		= 6;
	protected static final int SICOLUMN				= 7;
	protected static final int SCPCOLUMN			= 8;

    /**	Create a single word form frequency profile object.
     */

	public FindCollocates()
	{
		super
		(
			AnalysisDialog.FINDCOLLOCATES
		);
	}

	/**	Run an analysis.
	 *
	 *	@param	parentWindow		Parent window for display.
	 *	@param	progressReporter	Progress reporter.  May be null.
	 */

	public void runAnalysis
	(
		JFrame parentWindow ,
		ProgressReporter progressReporter
	)
	{
//		this.parentWindow		= parentWindow;
		this.progressReporter	= progressReporter;

		long startTime0	= System.currentTimeMillis();

								//	Initialize progress dialog.

		if ( displayProgress && ( progressReporter != null ) )
		{
			progressReporter.setTitle
			(
				WordHoardSettings.getString
				(
					"Collocates" ,
					"Collocates"
				)
			);

			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Findingwordoccurrences" ,
					"Finding word occurrences"
				)
			);

			progressReporter.setIndeterminate( true );
		}
								//	Get collocation counts in
								//	analysis text.

		resultsPanel		= null;

		long startTime		= System.currentTimeMillis();

		TreeMap[] counts	=
			getCollocateCounts
			(
				analysisText ,
				wordToAnalyze ,
				progressReporter ,
				WordHoardSettings.getString
				(
					"Findingcollocatesinanalysis" ,
					"Finding collocates in analysis"
				)
			);
								//	If operation cancelled while getting
								//	collocate counts, quit.

		if ( counts == null )
		{
			closeProgressReporter();
			return;
		}
								//	The analysis counts have been filtered
								//	by the specified cutoff value from
								//	the collocation dialog.  The raw counts
								//	are unfiltered.

		TreeMap analysisCounts			= counts[ 0 ];
		TreeMap rawCollocationCounts	= counts[ 2 ];

		long endTime					=
			System.currentTimeMillis() - startTime;

								//	Get total words in analysis and
								//	reference.

		startTime					=
			System.currentTimeMillis();

		int refTotalCount			=
			analysisText.getTotalWordFormCount( wordForm );

		int refDistinctCount		=
			analysisText.getDistinctWordFormCount( wordForm );

		endTime						=
			System.currentTimeMillis() - startTime;

								//	Set output column names.

		String[] columnNames	=
			new String[]
			{
				WordHoardSettings.getString
				(
					"coltitle3Collocate" ,
					"<html><br><br>Collocate</html>"
				) ,

				WordHoardSettings.getString
				(
					"coltitle3Wordclass" ,
					"<html><br>Word<br>class</html>"
				) ,

				WordHoardSettings.getString
				(
					"coltitle3Spancount" ,
					"<html><br>Span<br>count</html>"
				) ,

				WordHoardSettings.getString
				(
					"coltitle3analysiscount" ,
					"<html><br>Analysis<br>count</html>"
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
//
    		};
								//	Create table model to hold results.

		model	= new WordHoardSortedTableModel( columnNames , 0 , true );

		int analysisSpanCount		= wordOccs.length;

		int totalAnalysisSpanCount	=
			wordOccs.length * ( rightSpan + leftSpan + 1 );

		int maxLabelWidth	= 0;
		String maxLabel		= "";
		int collocatesDone	= 0;

		Set keyset			= analysisCounts.keySet();

								//	Reset progress dialog.

		if ( displayProgress )
		{
			if ( progressReporter.isCancelled() )
			{
				closeProgressReporter();
				return;
			}
			else
			{
				progressReporter.setMaximumBarValue(
					analysisCounts.size() );

				progressReporter.setIndeterminate( true );

				progressReporter.updateProgress
				(
					0 ,
					WordHoardSettings.getString
					(
						"Computingfrequencystatistics" ,
						"Computing frequency statistics"
					)
				);
			}
		}
								//	Get word counts for all
								//	of analysis text.

		startTime	= System.currentTimeMillis();

		Spelling[] analysisSpellings	=
			(Spelling[])keyset.toArray( new Spelling[]{} );

		Map allAnalysisCounts	=
			analysisText.getWordFormCount( analysisSpellings , wordForm );

		endTime		= System.currentTimeMillis() - startTime;

		if ( displayProgress )
		{
			progressReporter.setMaximumBarValue( keyset.size() - 1 );
			progressReporter.setIndeterminate( false );

			if ( progressReporter.isCancelled() )
			{
				closeProgressReporter();
				return;
			}
        }
								//	Compute collocation statistics.

		for	( Iterator iterator	= keyset.iterator(); iterator.hasNext(); )
		{
								//	Get next collocate.

			Spelling spCollocate	= (Spelling)iterator.next();
			String collocate		= spCollocate.getString();

								//	Get count of word form in
								//	collocate content portion of
								//	analysis text.

			Integer wordCount	=
				(Integer)analysisCounts.get( spCollocate );

			if ( wordCount	== null ) wordCount	= new Integer( 0 );

			int collocateLength	= collocate.length();

			if ( collocateLength > maxLabelWidth )
			{
				maxLabelWidth	= collocateLength;
				maxLabel		= collocate;
			}

			Integer allCount	=
				(Integer)allAnalysisCounts.get( spCollocate );

			if ( allCount == null ) allCount = new Integer( 0 );

			doFreq
			(
				collocate ,
				wordCount.intValue() ,
				allCount.intValue() ,
				analysisSpanCount ,
				refTotalCount ,
				model
			);
								//	Update count of words done.
			collocatesDone++;
								//	Update progress dialog.

			if ( displayProgress )
			{
				progressReporter.updateProgress
				(
					collocatesDone ,
					WordHoardSettings.getString
					(
						"Computingfrequencystatistics" ,
						"Computing frequency statistics"
					)
				);

				if ( progressReporter.isCancelled() ) break;
			}
		}
										//	Close progress dialog.

		boolean cancelled	= closeProgressReporter();

		endTime	= System.currentTimeMillis() - startTime0;

										//	Get results display.
		resultsPanel	=
			cancelled ?
				null :
				generateResults
				(
					model ,
					maxLabel ,
					analysisSpanCount ,
					totalAnalysisSpanCount ,
					refDistinctCount ,
					refTotalCount
				);
	}

	/**	Perform frequency comparison of analysis and reference works for a word.
	 *
	 *	@param	collocate			The collocate to analyze.
	 *	@param	analysisCount		Count of collocate in analysis text.
	 *	@param	analysisTotalCount	Total number of words in analysis text.
	 *	@param	refCount			Count of collocate in reference text.
	 *	@param	refTotalCount		Total number of words in reference text.
	 *	@param	model				Table model in which to store results.
	 */

	protected void doFreq
	(
		String collocate ,
		int analysisCount ,
		int analysisTotalCount ,
		int refCount ,
		int refTotalCount ,
		WordHoardSortedTableModel model
	)
	{
								//	Output results to "results."
								//
								//	results[0]	= span count
								//	results[1]	= analysis count
								//	results[2]	= dice
								//	results[3]	= loglikelihood
								//	results[4]	= phi squared							//	results[7]	=
								//	results[5]	= specific mutual info
								//	results[6]	= symmmetric cond. prob.

		double results[]	= new double[ 7 ];

								//	Compute frequency percents.

		results[ 0 ]		= analysisCount;
		results[ 1 ]		= analysisTotalCount;

            					//	Compute bigram association measures.
//
		double colloAnal[]	=
			Collocation.association
			(
				analysisCount ,
				analysisTotalCount ,
				refCount ,
				refTotalCount
			);
//
		results[ 2 ]	= colloAnal[ Collocation.DICE ];
		results[ 3 ]	= colloAnal[ Collocation.LOGLIKE ];
		results[ 4 ]	= colloAnal[ Collocation.PHISQUARED ];
		results[ 5 ]	= colloAnal[ Collocation.SMI ];
		results[ 6 ]	= colloAnal[ Collocation.SCP ];

								//	Add data to report table.

		model.add
		(
			new FrequencyAnalysisDataRow
			(
				WordUtils.stripWordClass( collocate ) ,
				WordUtils.stripSpelling( collocate ) ,
				results
			)
		);
	}

	/**	Displays results of frequency analysis in a sorted table.
	 *
	 *	@param	model				Table model holding data to display.
	 *	@param	maxLabel			Maximum width value for first table
	 *								column.
	 *	@param	spanCount			# of times word to collocate occurs
	 *								in span.
	 *	@param	totalSpanCount		# of words in collocation span.
	 *								Includes count of word being collocated.
	 *	@param	refDistinctCount	# of distinct words in analysis text.
	 *	@param	refTotalCount		# of words in analysis text.
	 *
	 *	@return						ResultsPanel with table and title.
	 */

	protected ResultsPanel generateResults
	(
		WordHoardSortedTableModel model ,
		String maxLabel ,
		int spanCount ,
		int totalSpanCount ,
		int refDistinctCount ,
		int refTotalCount
	)
	{
								//	Get title for results.

		int rowCount	= model.getRowCount();

		String pluralWordFormStringForDisplay	=
			pluralWordFormString.toLowerCase();

		PrintfFormat titleFormat;
		String title;

		StringBuffer titleFormatBuffer	= new StringBuffer();

		titleFormatBuffer.append
		(
			WordHoardSettings.getString
			(
				"FindcollocateTitle2" +
					( ( rowCount == 1 ) ? "S" : "P" ) ,
	    		"Found %s collocates of %s %s in %s"
	    	)
	    );

		titleFormatBuffer.append( " " );

		titleFormatBuffer.append
		(
			WordHoardSettings.getString
			(
				"searchedSpan" +
					( ( leftSpan == 1 ) ? "S" : "P" ) +
					( ( rightSpan == 1 ) ? "S" : "P" ) +
					( ( cutoff == 1 ) ? "S" : "P" ) ,
	    		"Searched left %s words and right %s words " +
	    		"for words appearing at least %s times."
	    	)
	    );

		titleFormatBuffer.append( " " );

		titleFormatBuffer.append
		(
		    WordHoardSettings.getString
		    (
		    	"Theselectedspancontainedcountwords" +
		    		( ( totalSpanCount == 1 ) ? "S" : "P" ),
	    		"The selected span contained %s words including "
	    	)
	    );

		titleFormatBuffer.append( " " );

		titleFormatBuffer.append
		(
		    WordHoardSettings.getString
		    (
		    	"includingcountappearancesofword" +
		    		( ( spanCount == 1 ) ? "S" : "P" ),
	    		"including %s appearances of %s."
	    	)
	    );

		titleFormatBuffer.append
		(
		    WordHoardSettings.getString
		    (
				"multipleWordFrequencyProfileTitle4" ,
				"  \"%s\" contains a total of %s %s %s."
	    	)
	    );

		titleFormat	= new PrintfFormat( titleFormatBuffer.toString() );

		title	=
			titleFormat.sprintf
			(
				new Object[]
				{
					StringUtils.formatNumberWithCommas( rowCount ) ,
					wordFormString.toLowerCase() ,
					wordToAnalyze ,
					analysisText.toString(
						useShortWorkTitlesInHeaders ) ,
					StringUtils.formatNumberWithCommas( leftSpan ) ,
					StringUtils.formatNumberWithCommas( rightSpan ) ,
					StringUtils.formatNumberWithCommas( cutoff ) ,
					StringUtils.formatNumberWithCommas( totalSpanCount ) ,
					StringUtils.formatNumberWithCommas( spanCount ) ,
					wordToAnalyze ,
					analysisText.toString(
						useShortWorkTitlesInHeaders ) ,
					StringUtils.formatNumberWithCommas(
						refDistinctCount ) ,
					( refDistinctCount == 1 ) ?
						wordFormString.toLowerCase() :
						pluralWordFormStringForDisplay ,
					StringUtils.formatNumberWithCommas( refTotalCount ) ,
					( refTotalCount == 1 ) ?
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

		PrintfFormat shortTitleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"Collocatesofssins" ,
				    "Collocates of %s %s in \"%s\""
				)
			);

		String shortTitle	=
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
								//	Generate formatted results.

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
					getDoubleFormat( 0 ) ,
					getDoubleFormat( 4 ) ,
					getDoubleFormat( 2 ) ,
					getDoubleFormat( 4 ) ,
					getDoubleFormat( 4 ) ,
					getDoubleFormat( 4 ) ,
				} ,
				LOGLIKECOLUMN ,
				LOGLIKECOLUMN ,
				WORDCLASSCOLUMN ,
				model ,
				new String[]{ maxLabel , maxLabel }
			);

		if ( !showWordClasses )
		{
			resultsTable.hideColumn( WORDCLASSCOLUMN );
		}

		return results;
	}

	/**	Is context output available?
	 *
	 *	@return		true since context output is available for collocation.
	 */

	public boolean isContextAvailable()
	{
		return true;
	}

	/**	Generate context results for selected collocate.
	 *
	 *	@param	parentWindow		Parent window for display.
	 *	@param	progressReporter	Progress reporter.  May be null.
	 *
	 *	@return						ResultsPanel containing the context results.
	 */

	public ResultsPanel getContext
	(
		JFrame parentWindow ,
		ProgressReporter progressReporter
	)
	{
		super.getContext( parentWindow , progressReporter );

		long startTime	= System.currentTimeMillis();

								//	Get selected table row.

		int selectedIndex		=
			resultsTable.getSelectedRow();

								//	Get collocate for this row.

		Spelling collocate		=
			WordUtils.getSpellingForString
			(
				(String)model.getValueAt( selectedIndex , 0 ) +
				" (" +
				(String)model.getValueAt( selectedIndex , 1 )
				+ ")"
			);
								//	Return nothing if collocate not found.
								//	Should never happen.

		if ( !collocationOccurrenceMap.containsKey( collocate ) )
		{
			return null;
		}
								//	Get column names for display.

		String[] columnNames	=
			new String[]
			{
				WordHoardSettings.getString( "Line" , "Line" ) ,
				WordHoardSettings.getString( "Context" , "Context" )
			};
								//	Create a table model to hold the
								//	context text results.

		WordHoardSortedTableModel model	=
			new WordHoardSortedTableModel
			(
				columnNames ,
				0 ,
				true
			);
								//	Create table to hold results.

		XTable table	= new XTable( model );

								//	Enable striping of alternate rows in the
								//	table.

		table.setStriped( true );

								//	Handle double clicks on table rows.

		table.addMouseListener
		(
			new MouseAdapter()
			{
				public void mouseReleased( MouseEvent e )
				{
					if ( e.getClickCount() >= 2 )
					{
						int clickedRow		=
							((XTable)e.getComponent()).getSelectedRow();

						openWorkPartForContext
						(
							(XTable)e.getComponent() ,
							clickedRow
						);
					}
				}
			}
		);
								//	Only line number column is sortable.

		boolean[] sortableColumns	= new boolean[ columnNames.length ];

		for ( int i = 0 ; i < columnNames.length ; i++ )
		{
			sortableColumns[ i ]	= false;
		}

		sortableColumns[ 0 ]	= true;

								//	True if context extraction cancelled.

		boolean cancelled	= false;

								//	Initialize progress display.

		if ( displayProgress && ( progressReporter != null ) )
		{
			progressReporter.setTitle
			(
				WordHoardSettings.getString
				(
					"Collocates" ,
					"Collocates"
				)
			);

			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Findingcollocatecontexts" ,
					"Finding collocate contexts"
				)
			);

			progressReporter.setIndeterminate( true );
		}
								//	Pick up list of word occurrences for
								//	this collocate.

		ArrayList wordOccsList	=
			(ArrayList)collocationOccurrenceMap.get( collocate );

		if ( displayProgress )
		{
			cancelled	= progressReporter.isCancelled();

			if ( cancelled )
			{
				closeProgressReporter();
				return null;
			}
		}
								//	Get persistence manager.
		PersistenceManager pm;

		try
		{
			pm	= PMUtils.getPM();
		}
		catch ( Exception e )
		{
			pm	= null;
			Err.err( e );
		}
								//	Convert to array for simpler handling.

		Word[] wordOccurrences	=
			(Word[])wordOccsList.toArray( new Word[]{} );

								//	Set progress dialog determinate.

		if ( displayProgress )
		{
			cancelled	= progressReporter.isCancelled();

			if ( cancelled )
			{
				closeProgressReporter();
				return null;
			}
        }
								//	Highlight word and collocate
								//	within the specified left->right span
								//	in the collocate text.

		String[] highlightWords	=
			new String[]
			{
				wordToAnalyze.getString() ,
				collocate.getString()
			};

								//	Preload word occurrences for spans.

		Collection collocateContext	=
			CollocateUtils.getColocates
			(
				pm ,
				wordOccsList ,
				Math.max( leftSpan + 7 , rightSpan + 7 )
			);

		PreloadUtils.preloadWordParts( pm , collocateContext );

		if ( displayProgress )
		{
			cancelled	= progressReporter.isCancelled();

			if ( cancelled )
			{
				closeProgressReporter();
				return null;
			}
		}
								//	For a word set, pick up the collection
								//	of words.  We need this to mark words
								//	in context as being part of the
								//	collection or not.

		WordSet wordSet	= null;

		if ( analysisText.isWordSet() )
		{
			wordSet	=
				(WordSet)PersistenceManager.doLoad
				(
					WordSet.class ,
					analysisText.getObjectId()
				);

			if ( displayProgress )
			{
				cancelled	= progressReporter.isCancelled();

				if ( cancelled )
				{
					closeProgressReporter();
					return null;
				}
			}
		}
								//	Set progress dialog determinate.

		if ( displayProgress )
		{
			progressReporter.setMaximumBarValue(
				wordOccurrences.length - 1 );

			progressReporter.updateProgress( 0 );

			progressReporter.setIndeterminate( false );

			cancelled	= progressReporter.isCancelled();

			if ( cancelled )
			{
				closeProgressReporter();
				return null;
			}
        }

		int maxLabelWidth	= 0;
		int maxTextWidth	= 0;

								//	For each occurrence of the collocate ...

		for ( int i = 0 ; i < wordOccurrences.length ; i++ )
		{
								//	Pick up the word occurrence for the
								//	word being collocated.

			Word wordOccurrence	= wordOccurrences[ i ];

			if ( ( pm != null) && !pm.contains( wordOccurrence ) )
			{
				wordOccurrence	=
					(Word)PersistenceManager.doLoad
					(
						Word.class ,
						wordOccurrence.getId()
					);
            }
								//	Get the left and right spans around
								//	the anchor word, plus a few extra
								//	words.

			ArrayList spanOccsList	= new ArrayList();

								//	Get left span.

			java.util.List leftSpanOccs		=
				WordUtils.getLeftSpan
				(
					wordOccurrence ,
					leftSpan + 7
				);
								//	Add left words to total span.

			spanOccsList.addAll( leftSpanOccs );

								//	Add anchor word to total span.

			spanOccsList.add( wordOccurrence );

								//	Get right span.

			java.util.List rightSpanOccs	=
				WordUtils.getRightSpan
				(
					wordOccurrence ,
					rightSpan + 7
				);

								//	Add right words to total span.

			spanOccsList.addAll( rightSpanOccs );

								//	No words in span (should not happen):
								//	skip this word.

			if ( spanOccsList.size() == 0 ) continue;

								//	Convert span to array of Word.

			Word[] spanOccs	= (Word[])spanOccsList.toArray( new Word[]{} );

								//	Get highlight bracket.

			int[] highlightBracket	= /* 7 , 8 + leftSpan + rightSpan */
				new int[]
				{
					leftSpanOccs.size() - 1 ,
					leftSpanOccs.size() + leftSpan + rightSpan
				};
								//	Generate the collocation context text.

			String contextString	=
				WordUtils.getDisplayableText
				(
					spanOccs ,
					false ,
					"" ,
					highlightWords ,
					highlightBracket ,
					( wordForm == WordForms.LEMMA ) ,
					wordSet
				);
								//	Update maximum widths for
								//	context line number and text.

			String wordPath	= wordOccurrence.getPath();

			if ( wordPath.length() > maxLabelWidth )
			{
				maxLabelWidth	= wordPath.length();
			}

			if ( contextString.length() > maxTextWidth )
			{
				maxTextWidth	= contextString.length();
			}
								//	Add the word path and context text
								//	to the table for display.
			model.add
			(
				new CollocateContextRow
				(
					wordOccurrence.getTag() ,
					wordPath  ,
					contextString
				)
			);
								//	Update progress dialog.

			if ( displayProgress )
			{
				progressReporter.updateProgress( i );

				cancelled	= cancelled || progressReporter.isCancelled();

				if ( cancelled ) break;
			}
		}
								//	Close progress dialog.

		closeProgressReporter();

								//	Quit if context extraction cancelled.

		if ( cancelled ) return null;

		long endTime	= System.currentTimeMillis() - startTime;

								//	Set value for first column to widest
								//	string which appeared in the data in
								//	this table.

		String[] columnLongValues	= new String[ columnNames.length ];

		columnLongValues[ 0 ]	=
			StringUtils.dupl( 'x' , maxLabelWidth + 4 );

		columnLongValues[ 1 ]	=
			StringUtils.dupl( 'x' , maxTextWidth );

								//	Set the column names and sortable status.
		model.setView
		(
			table ,
			sortableColumns ,
			columnLongValues ,
			false ,
			false
		);                      //	Set comparator.

		model.setWordHoardComparator();

								//	Create results panel to
								//	hold output table.

		ResultsPanel panel	= new ResultsPanel();

								//	Store table in results panel.

		panel.setResults( table );

								//	Get number of contexts to display.

		int nContexts	= model.getRowCount();

								//	Get title.

		PrintfFormat titleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					( ( nContexts == 1 ) ?
						"Collocationcontext" :
						"Collocationcontexts" ) ,
					( ( nContexts == 1 ) ?
						"%s collocation context for %s \"%s\" with " +
						"collocate \"%s\"" :
						"%s collocation contexts for %s \"%s\" with " +
						"collocate \"%s\"" )
				)
			);

		String title	=
			titleFormat.sprintf
			(
				new Object[]
				{
					StringUtils.formatNumberWithCommas( nContexts ) ,
					wordFormString.toLowerCase() ,
					wordToAnalyze ,
					collocate
				}
			);
								//	Store title in results panel.

		panel.setResultsHeader( title );
		panel.setResultsTitle( title );

								//	Add displayble title and table
								//	to results panel.

		panel.add( new JLabel( title ) );

								//	Wrap the results table in a scroll pane.

		XScrollPane scrollPane	=
			new XScrollPane
			(
				table ,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
			);
								//	Add scroll pane to output panel.

		panel.add( scrollPane , 10 );

								//	Allow horizonal scrolling to work.

		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );

		return panel;
	}

	/**	Open work part for selected collocate context.
	 *
	 *	@param	table		The table holding context text.
	 *	@param	clickedRow	The table row for which the corresponding
	 *						work part is desired.
	 */

	protected void openWorkPartForContext( XTable table , int clickedRow )
	{
		String wordPath	=
			(String)table.getModel().getValueAt( clickedRow , 0 );

		String wordTag	=
			(String)table.getModel().getValueAt( clickedRow , 2 );

		ArrayList wordTagList	= new ArrayList();

		wordTagList.add( wordTag );

		Word[] word		= WordUtils.getWordsByTags( wordTagList );

		Work work		= word[ 0 ].getWork();
		Corpus corpus	= work.getCorpus();

		try
		{
			WorkWindow workWindow	= new WorkWindow( corpus , work , null );
			workWindow.getWorkPanel().goTo( word[ 0 ] );
		}
		catch ( Exception e )
		{
		}
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

	/**	Show tag cloud of association measure values.
	 */

	public ResultsPanel getCloud()
	{
		String title	=
			WordHoardSettings.getString
			(
				"Tagclouddisplayofassociationmeasurevalues" ,
				"Tag cloud display of association measure values."
			);

		String wordFormStringForDisplay	=
			wordFormString.toLowerCase();

		PrintfFormat header1Format	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"Collocatesofssins" ,
					"Collocates of %s \"%s\" in \"%s\"."
				)
			);

		String header1	=
			header1Format.sprintf
			(
				new Object[]
				{
					wordFormString.toLowerCase() ,
					wordToAnalyze ,
					analysisText.toString(
						useShortWorkTitlesInHeaders ) ,
				}
			);

		String header2	=
			WordHoardSettings.getString
			(
				"Thelargerthetag" ,
				"The larger the tag, the greater its association measure value."
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

		compressValueRangeInTagClouds	=
			AnalysisDialog.getCompressValueRangeInTagClouds();

		boolean compressRange			=
			compressValueRangeInTagClouds &&
			( measureColumn	== LOGLIKECOLUMN );

		String header4	= "";

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


