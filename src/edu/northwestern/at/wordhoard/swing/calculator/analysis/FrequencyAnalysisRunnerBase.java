package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.*;
import edu.northwestern.at.utils.math.*;
import edu.northwestern.at.utils.math.matrix.*;
import edu.northwestern.at.utils.math.distributions.*;
import edu.northwestern.at.utils.math.statistics.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.html.*;
import edu.northwestern.at.utils.tagcloud.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;
import edu.northwestern.at.wordhoard.swing.querytool.*;
import edu.northwestern.at.wordhoard.swing.text.*;

/**	Base class for frequency analyses.
 */

public class FrequencyAnalysisRunnerBase
	implements AnalysisRunner
{
	/**	Type of frequency analysis.
	 */

	protected int frequencyAnalysisType	=
		AnalysisDialog.MULTIPLEWORDFORMCOMPARISON;

	/**	Analysis text.
	 */

	protected WordCounter analysisText;

	/**	Reference text.
	 */

	protected WordCounter referenceText;

	/**	Minimum count for word to be analyzed.
	 */

	protected int minimumCount;

	/**	Minimum work count for word to be analyzed.
	 */

	protected int minimumWorkCount;

	/**	Holds frequency analysis results.
	 */

	protected ArrayList FrequencyProfileResults;

	/**	Word form type.
	 */

	protected int wordForm;

	/**	Displayable word form type.
	 */

	protected String wordFormString;

	/**	Displayable plural word form type.
	 */

	protected String pluralWordFormString;

	/**	Word to analyze (spelling, lemma, etc.). */

	protected Spelling wordToAnalyze;

	/**	Normalization method for frequencies. */

	protected int frequencyNormalizationMethod;

	/**	True to round normalized frequencies. */

	protected boolean roundNormalizedFrequencies;

	/**	Percent report method. */

	protected int percentReportMethod;

	/**	True to mark significant log-likelihood values in tabular display.
	 */

	protected boolean markSignificantLogLikelihoodValues;

	/**	True to filter out proper names.
	 */

	protected boolean filterOutProperNames;

	/**	True to display word classes for all words.
	 */

	protected boolean showWordClasses;

	/**	True to display phrase counts instead of word counts.
	 */

	protected boolean showPhraseFrequencies;

	/**	True to compare phrase counts instead of word counts.
	 */

	protected boolean analyzePhraseFrequencies;

	/**	Number of words to left of word to look for collocates.
	 */

	protected int leftSpan	= 1;

	/**	Number of words to right of word to look for collocates.
	 */

	protected int rightSpan	= 1;

	/**	Minimum number of times collocate word must appear.
	 */

	protected int cutoff	= 2;

	/**	Word occurrences for a collocation analysis.
	 */

	protected Word[] wordOccs	= null;

	/**	TreeMap of word occurrences for each collocate.
	 */

	protected TreeMap collocationOccurrenceMap	= new TreeMap();

	/**	Analysis text breakdown method.
	 */

	protected int analysisTextBreakdownBy    	=
		AnalysisDialog.DEFAULTTEXTBREAKDOWNBY;

	/**	Reference text breakdown method.
	 */

	protected int referenceTextBreakdownBy    	=
		AnalysisDialog.DEFAULTTEXTBREAKDOWNBY;

	/**	Association measure for localmaxs.
	 */

	protected int associationMeasure			=
		AnalysisDialog.DEFAULTASSOCIATIONMEASURE;

	/**	Minimum multiword unit length.
	 */

	protected int minimumMultiwordUnitLength	=
		AnalysisDialog.DEFAULTMINIMUMMULTIWORDUNITLENGTH;

	/**	Maximum multiword unit length.
	 */

	protected int maximumMultiwordUnitLength	=
		AnalysisDialog.DEFAULTMAXIMUMMULTIWORDUNITLENGTH;

	/**	Filter bigrams by word class flag.
	 */

	protected boolean filterBigramsByWordClass	= false;

	/**	Filter trigrams by word class flag.
	 */

	protected boolean filterTrigramsByWordClass	= false;

	/**	Filter multiword units containing verbs.
	 */

	protected boolean filterMultiwordUnitsContainingVerbs	= false;

	/**	Filter ngrams using localmaxs.
	 */

	public boolean filterUsingLocalMaxs			= false;

	/**	Filter ngrams which occur only once.
	 */

	public boolean filterSingleOccurrences		= true;

	/**	Ignore case and diacritical marks.
	 */

	public boolean ignoreCaseAndDiacriticalMarks	= true;

	/**	WordHoardSortedTableModel for holding results. */

	protected WordHoardSortedTableModel model;

	/**	True to display progress dialog. */

	protected boolean displayProgress	= true;

	/**	Progress reporter. */

	protected ProgressReporter progressReporter;

	/**	The results panel. */

	protected ResultsPanel resultsPanel	= null;

	/**	The context button, if any. */

	protected JButton contextButton	= null;

	/**	The results table. */

	protected XTable resultsTable	= null;

	/**	The scroll pane around the results table. */

	protected XScrollPane resultsScrollPane	= null;

	/**	True to use color coding for overuse table columns.
	 */

	protected boolean colorCodeOveruseColumn	= false;

	/**	True to adjust chi-square values for number of comparisons.
	 */

	protected boolean adjustChiSquareForMultipleComparisons	= true;

	/**	Font size for table. */

	protected static final int FONT_SIZE = 10;

	/**	True to use short work names in dialogs. */

	protected boolean useShortWorkTitlesInDialogs		= true;

	/**	True to use short work names in output. */

	protected boolean useShortWorkTitlesInOutput		= true;

	/**	True to use short work names in titles. */

	protected boolean useShortWorkTitlesInWindowTitles	= true;

	/**	True to use short work names in headers. */

	protected boolean useShortWorkTitlesInHeaders		= true;

	/**	True to compress value range in tag clouds. */

	protected boolean compressValueRangeInTagClouds	= true;

	/**	Blank replacement character in tag clouds.
	 *
	 *	Currently a raised dot (Unicode \u00B7).
	 */

	protected String blankReplacementCharacter	= "\u00B7";

    /**	Create a single word form frequency profile object.
     *
     *	@param	frequencyAnalysisType	Type of frequency analysis.
     */

	public FrequencyAnalysisRunnerBase
	(
		int frequencyAnalysisType
	)
	{
		this.frequencyAnalysisType	= frequencyAnalysisType;
	}

	/**	Display the frequency analysis dialog.
	 *
	 *	@return		true if OK pressed in dialog, false otherwise.
	 */

	public boolean showDialog( JFrame parentFrame )
	{
		AnalysisDialog dialog	=
			new AnalysisDialog
			(
				parentFrame ,
				frequencyAnalysisType
			);

		dialog.setVisible( true );

		if ( !dialog.getCancelled() )
		{
			wordToAnalyze					= dialog.getWordText();
			analysisText					= dialog.getAnalysisText();
			referenceText					= dialog.getReferenceText();
			wordForm						= dialog.getWordForm();

			wordFormString					=
				dialog.getWordFormString( wordForm );

			pluralWordFormString					=
				dialog.getPluralWordFormString( wordForm );

			analysisTextBreakdownBy			=
				dialog.getAnalysisTextBreakdownBy();

			referenceTextBreakdownBy			=
				dialog.getReferenceTextBreakdownBy();

			minimumCount					= dialog.getMinimumCount();

			minimumWorkCount				= dialog.getMinimumWorkCount();

			leftSpan						= dialog.getLeftSpan();
			rightSpan						= dialog.getRightSpan();
			cutoff							= dialog.getCutoff();

			frequencyNormalizationMethod	=
				dialog.getFrequencyNormalizationMethod();

			roundNormalizedFrequencies		=
				dialog.getRoundNormalizedFrequencies();

			percentReportMethod	=
				dialog.getPercentReportMethod();

			markSignificantLogLikelihoodValues	=
				dialog.getMarkSignificantLogLikelihoodValues();

			filterOutProperNames		= dialog.getFilterOutProperNames();

			showWordClasses				= dialog.getShowWordClasses();
			showPhraseFrequencies		= dialog.getShowPhraseFrequencies();
			analyzePhraseFrequencies	= dialog.getanalyzePhraseFrequencies();

			associationMeasure			= dialog.getAssociationMeasure();
			minimumMultiwordUnitLength	= dialog.getMinimumMultiwordUnitLength();
			maximumMultiwordUnitLength	= dialog.getMaximumMultiwordUnitLength();

			filterSingleOccurrences		= dialog.getFilterSingleOccurrences();
			filterBigramsByWordClass	= dialog.getFilterBigramsByWordClass();
			filterTrigramsByWordClass	= dialog.getFilterTrigramsByWordClass();
			filterUsingLocalMaxs		= dialog.getFilterUsingLocalMaxs();
			filterMultiwordUnitsContainingVerbs	=
         		dialog.getFilterMultiwordUnitsContainingVerbs();

			ignoreCaseAndDiacriticalMarks	=
				dialog.getIgnoreCaseAndDiacriticalMarks();

			adjustChiSquareForMultipleComparisons	=
				dialog.getAdjustChiSquareForMultipleComparisons();

			useShortWorkTitlesInDialogs		=
				dialog.getUseShortWorkTitlesInDialogs();

			useShortWorkTitlesInOutput		=
				dialog.getUseShortWorkTitlesInOutput();

			useShortWorkTitlesInWindowTitles	=
				dialog.getUseShortWorkTitlesInWindowTitles();

			useShortWorkTitlesInHeaders		=
				dialog.getUseShortWorkTitlesInHeaders();

			compressValueRangeInTagClouds	=
				dialog.getCompressValueRangeInTagClouds();
		}

		return !dialog.getCancelled();
	}

	/**	Run analysis. */

	public void runAnalysis
	(
		JFrame parentWindow ,
		ProgressReporter progressReporter
	)
	{
//		this.parentWindow		= parentWindow;
		this.progressReporter	= progressReporter;
	}

	/**	Close progress reporter.
	 *
	 *	@return		The value of the progress reporter's cancelled flag.
	 */

	public boolean closeProgressReporter()
	{
		boolean result	= false;

		if ( progressReporter != null )
		{
			result	= progressReporter.isCancelled();

			progressReporter.close();
			progressReporter = null;
		}

		return result;
	}

	/**	Get results. */

	public ResultsPanel getResults()
	{
		return resultsPanel;
	}

	/**	Displays results of analysis in a sorted table.
	 *
	 *	@param	wordToAnalyze		The word being analyzed.
	 *	@param	title				Long results title.
	 *	@param	shortTitle			Short results title.
	 *	@param	columnLongValues	Column long values to set column widths.
	 *	@param	columnFormats		Column formats for results.
	 *	@param	initialSortColumn	Results sorted by this column.
	 *	@param	logLikelihoodColumn	Column containing log-likelihood values.
	 *	@param	wordClassColumn		Column containing word classes.
	 *	@param	model				Table model holding results.
	 *	@param	maxColumnValues		Maximum width value for table columns.
	 *								If number of entries "k" is less than
	 *								the number of the table columns, only
	 *								the first "k" column widths are set.
	 */

	protected ResultsPanel generateResults
	(
		Spelling wordToAnalyze ,
		String title ,
		String shortTitle ,
		String[] columnLongValues ,
		String[] columnFormats ,
		int initialSortColumn ,
		int logLikelihoodColumn ,
		int wordClassColumn ,
		WordHoardSortedTableModel model ,
		String[] maxColumnValues
	)
	{
								//	Create results panel to
								//	hold output table.

		ResultsPanel panel		= new ResultsPanel();

								//	Get a font manager.

		FontManager fontManager	= new FontManager();

								//	Get font for table entries.

		Font tableFont			= fontManager.getFont( FONT_SIZE );

								//	Use WordHoard row comparator.
								//	Allows multiple sort columns
								//	to be applied in order.

		model.setWordHoardComparator();

								//	Create table to hold results.

		resultsTable			= new XTable( model );

		resultsTable.setShowGrid( true );
		resultsTable.setGridColor( Color.lightGray );
		resultsTable.setHeaderBackground( Color.lightGray );
		resultsTable.setFont( tableFont );

                                //	Set up results table for drag and drop.

		resultsTable.setTransferHandler( new WordTransferHandler() );
		resultsTable.setDragEnabled( true );

		TableDragMouseHandler mh	= new TableDragMouseHandler();

		resultsTable.addMouseListener( mh );
		resultsTable.addMouseMotionListener( mh );

								//	Store table in results panel.

		panel.setResults( resultsTable );

								//	Set all columns sortable.

		boolean[] sortableColumns	=
			new boolean[ columnFormats.length ];

		for ( int i = 0 ; i < columnFormats.length ; i++ )
		{
			sortableColumns[ i ]	= true;
		}
								//	Set values for columns to widest string
								//	which appeared in the data in this
								//	table.

		for ( int i = 0 ; i < maxColumnValues.length ; i++ )
		{
			columnLongValues[ i ]	= maxColumnValues[ i ];
        }

		if	(	( logLikelihoodColumn > 0 ) &&
				markSignificantLogLikelihoodValues )
		{
			double maxLogLike	= 0.0D;

			for ( int i = 0 ; i < model.getRowCount() ; i++ )
			{
				maxLogLike	=
					Math.max
					(
						maxLogLike ,
						((Double)model.getValueAt(
							i , logLikelihoodColumn )).doubleValue()
					);
			}

			String maxLogLikeValue	=
				Formatters.formatDouble( maxLogLike , 2 ) + "********";

			columnLongValues[ logLikelihoodColumn ]	= maxLogLikeValue;
		}
								//	Set the column names and sortable status.
		model.setView
		(
			resultsTable ,
			sortableColumns ,
			columnLongValues ,
			false ,
			true
		);
								//	Set the initial sort column, if any.

		model.sort( initialSortColumn , ( initialSortColumn == 0 ) );

								//	Set cell renderers for table entries.

								//	-- Spelling or lemma

		resultsTable.setColumnRenderer(
			0 , new WordTableCellRenderer( false , false ) );

		int chiSquareAdjustment	= LogLikelihoodTableCellRenderer.NONE;

								//	Note if we're adjusting
								//	log-likelihood significance
								//	threshholds.

		if	(	adjustChiSquareForMultipleComparisons &&
				( model.getRowCount() > 1 )  &&
				( logLikelihoodColumn > 0 )
			)
		{
								//	Use Sidak method to adjust
								//	significance threshholds.

			chiSquareAdjustment	= LogLikelihoodTableCellRenderer.SIDAK;

								//	Add note to title that we
								//	are adjusting the significance levels.

			StringBuffer sb		= new StringBuffer( title );

			char lastChar		= sb.charAt( sb.length() - 1 );

			int charType		= Character.getType( lastChar );

								//	Add a blank before the note
								//	if the last character of the
								//	title is not a line separator or
								//	control character.

			if (	( charType != Character.LINE_SEPARATOR ) &&
					( charType != Character.CONTROL ) )
			{
				sb.append( " " );
			}
								//	Add the adjustment note to the title.
			sb.append
			(
				WordHoardSettings.getString
				(
					"Thesignificancelevelsareadjusted" ,
					"The significance levels for the log-likelihood " +
					"values are adjusted for the number of comparisons."
				)
			);

			title	= sb.toString();
		}

		for ( int i = 1 ; i < columnFormats.length ; i++ )
		{
			if	(	( i == logLikelihoodColumn ) &&
					markSignificantLogLikelihoodValues )
			{
				resultsTable.setColumnRenderer
				(
					i ,
					new LogLikelihoodTableCellRenderer
					(
						columnFormats[ i ] ,
						true ,
						chiSquareAdjustment ,
						model.getRowCount()
					)
				);
			}
/*
			else if	( i == wordClassColumn )
			{
				resultsTable.setColumnRenderer
				(
					wordClassColumn ,
					new WordTableCellRenderer( false , false )
				);
			}
*/
			else
			{
				resultsTable.setColumnRenderer
				(
					i ,
					new PrintfFormatTableCellRenderer( columnFormats[ i ] )
				);
			}
		}
								//	Enable striping of alternate rows in the
								//	table.

		resultsTable.setStriped( true );

                                //	Set a list selection listener for the
                                //	table.

		ListSelectionModel tsm	=
			(ListSelectionModel)resultsTable.getSelectionModel();

		tsm.addListSelectionListener( tableSelectionListener );

								//	Add the table title to the output panel.
								//	We must do this on the AWT event thread
								//	or the output isn't properly laid out
								//	when displayed.

		final ResultsPanel finalPanel	= panel;
		final String finalShortTitle		= shortTitle;
		final String finalTitle			= title;

		SwingRunner.runNow
		(
			new Runnable()
			{
				public void run()
				{
					XTextArea titleTextArea	= new XTextArea( finalTitle );
					titleTextArea.setBackground( finalPanel.getBackground() );
					titleTextArea.setFont( resultsTable.getFont() );
					titleTextArea.setEditable( false );

					JPanel titlePanel	= new JPanel();
					titlePanel.setLayout( new BorderLayout() );
					titlePanel.add( titleTextArea );
					finalPanel.add( titlePanel );

					resultsTable.setTitle( finalTitle );

								//	Set the results title.

					finalPanel.setResultsHeader( finalTitle );
					finalPanel.setResultsTitle( finalShortTitle );

								//	Wrap the results table in a scroll pane.

					resultsScrollPane	=
						new XScrollPane
						(
							resultsTable ,
							JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
						);

					Dimension scrollPaneSize	= resultsScrollPane.getSize();

					scrollPaneSize.height		= 16384;

					resultsScrollPane.setPreferredSize( scrollPaneSize );

								//	Add scroll pane to output panel.

					finalPanel.add( resultsScrollPane , 10 );
				}
			}
		);
								//	Return output panel.
		return panel;
	}

	/**	Get table font size.
	 *
	 *	@return		table font size in pixels.
	 */

	public static int getTableFontSize()
	{
		return FONT_SIZE;
	}

	/**	Is chart output available?
	 *
	 *	@return		true if chart output available, false otherwise.
	 */

	public boolean isChartAvailable()
	{
		return false;
	}

	/**	Is cloud output available?
	 *
	 *	@return		true if cloud output available, false otherwise.
	 */

	public boolean isCloudAvailable()
	{
		return false;
	}

	/**	Is output filter available?
	 *
	 *	@return		true if output filter available, false otherwise.
	 */

	public boolean isFilterAvailable()
	{
		return false;
	}

	/**	Are result options available?
	 *
	 *	@return		true if result options are available, false otherwise.
	 */

	public boolean areResultOptionsAvailable()
	{
		return false;
	}

	/**	Is context output available?
	 *
	 *	@return		true if context output available, false otherwise.
	 */

	public boolean isContextAvailable()
	{
		return false;
	}

	/**	Chart results. */

	public ResultsPanel getChart()
	{
		return null;
	}

	/**	Cloud results. */

	public ResultsPanel getCloud()
	{
		return null;
	}

	/**	Result options. */

	public LabeledColumn getResultOptions()
	{
		return null;
	}

	/**	Create compress cloud value range checkbox result option.
	 *
	 *	@return		JCheckBox for compress values option.
	 */

	public JCheckBox createCompressValueRangeInTagCloudsCheckBox()
	{
		JCheckBox compressValueRangeInTagCloudsCheckBox	=
			new JCheckBox();

		compressValueRangeInTagCloudsCheckBox.setText
		(
			WordHoardSettings.getString
			(
				"Compressvaluerangeintagclouds" ,
				"Compress value range in tag clouds"
			)
    	);

		compressValueRangeInTagCloudsCheckBox.setSelected
		(
			AnalysisDialog.getCompressValueRangeInTagClouds()
    	);

		compressValueRangeInTagCloudsCheckBox.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed( ActionEvent actionEvent )
				{
					JCheckBox checkBox	=
						(JCheckBox)actionEvent.getSource();

			        boolean selected = checkBox.getModel().isSelected();

					AnalysisDialog.setCompressValueRangeInTagClouds( selected );
				}
			}
		);

		return compressValueRangeInTagCloudsCheckBox;
	}

	/**	Create cloud association measures combobox.
	 *
	 *	@return		JComboBox for cloud association measures.
	 */

	public JComboBox createCloudAssociationMeasuresComboBox()
	{
								//	Association measures which can
								//	appear in a tag cloud.
		String dice	=
			WordHoardSettings.getString(
				"Dice" ,
				"Dice coefficient" );

		String logLike	=
			WordHoardSettings.getString(
				"Loglike" ,
				"Log Likelihood" );

		String phi	=
			WordHoardSettings.getString(
				"PhiSquared" ,
				"Phi Squared" );

		String si =
			WordHoardSettings.getString(
				"SI" ,
				"Specific Mutual Information" );

		String scp	=
			WordHoardSettings.getString(
				"SCP" ,
				"Symmetric Conditional Probability" );

		final JComboBox cloudMeasureChoices	= new JComboBox();

		cloudMeasureChoices.addItem( dice);
		cloudMeasureChoices.addItem( logLike );
		cloudMeasureChoices.addItem( phi );
		cloudMeasureChoices.addItem( si );
		cloudMeasureChoices.addItem( scp );

		if ( frequencyAnalysisType == AnalysisDialog.FINDMULTIWORDUNITS )
		{
			cloudMeasureChoices.setSelectedItem
			(
				AnalysisDialog.getAssociationMeasureString
				(
					AnalysisDialog.getAssociationMeasure()
				)
			);
		}
		else
		{
			cloudMeasureChoices.setSelectedItem
			(
				AnalysisDialog.getAssociationMeasureString
				(
					AnalysisDialog.getCloudMeasure()
				)
			);
		}

		cloudMeasureChoices.addItemListener
		(
			new ItemListener()
			{
				public void itemStateChanged( ItemEvent event )
				{
					if ( event.getStateChange() == ItemEvent.SELECTED )
					{
						String selection	=
							(String)cloudMeasureChoices.getSelectedItem();

						AnalysisDialog.setCloudMeasure( selection );
					}
				}
			}
		);

		return cloudMeasureChoices;
	}

	/**	Get context results.
	 *
	 *	@return		ResultsPanel containing the context results.
	 */

	public ResultsPanel getContext
	(
		JFrame parentWindow ,
		ProgressReporter progressReporter
	)
	{
		this.progressReporter	= progressReporter;
//		this.parentWindow		= parentWindow;

		return null;
	}

	/**	Saves chart. */

	public void saveChart()
	{
	}

	/**	Set the context button.
	 *
	 *	@param	contextButton	The context button.
	 */

	public void setContextButton( JButton contextButton )
	{
		this.contextButton	= contextButton;

		if ( contextButton != null ) contextButton.setEnabled( false );
	}

	/**	Handle selection change in results table.
	 *
	 *	@param	event	Table selection event.
	 */

	public void handleTableSelectionChange( ListSelectionEvent event )
	{
		if ( ( contextButton != null ) && ( resultsTable != null ) )
		{
			int selectedIndex	= resultsTable.getSelectedRow();

			boolean showButton	=
				( resultsTable.getSelectedRowCount() == 1 ) &&
				( selectedIndex >= 0 );

			contextButton.setEnabled( showButton );
		}
	}

	/**	Convert word form string to column title ready string.
	 *
	 *	@param	wordFormString		The word form string
	 *								("spelling", "lemma", etc.)
	 *
	 *	@return						Version of word form string suitable
	 *								for use as JTable column title.
	 *
	 */

	public String getColTitleWordFormString( String wordFormString )
	{
		return "<html><br>" + wordFormString + "</html>";
	}

	/**	Get analysis text percent column name.
	 *
	 *	@return		Column name for analysis percent.
	 */

	public String getAnalysisPercentColumnName()
	{
		String	result	= "";

		switch ( percentReportMethod )
		{
			case AnalysisDialog.REPORTPERCENTS			:
				result	=
					WordHoardSettings.getString
					(
						"coltitleAnalysispercent" ,
						"<html>Analysis<br>%</html>"
					);
				break;

			case AnalysisDialog.REPORTPARTSPERMILLION	:
				result	=
					WordHoardSettings.getString
					(
						"coltitleAnalysisppm" ,
						"<html>Analysis parts<br>per million</html>"
					);
				break;

			case AnalysisDialog.REPORTPARTSPER10000	:
				result	=
					WordHoardSettings.getString
					(
						"coltitleAnalysispartsper10000" ,
						"<html>Analysis parts<br>per 10,000</html>"
					);
				break;
		}

		return result;
	}

	/**	Get reference text percent column name.
	 *
	 *	@return		Column name for reference percent.
	 */

	public String getReferencePercentColumnName()
	{
		String	result	= "";

		switch ( percentReportMethod )
		{
			case AnalysisDialog.REPORTPERCENTS			:
				result	=
					WordHoardSettings.getString
					(
						"coltitleReferencepercent" ,
						"<html>Reference<br>%</html>"
					);
				break;

			case AnalysisDialog.REPORTPARTSPERMILLION	:
				result	=
					WordHoardSettings.getString
					(
						"coltitleReferenceppm" ,
						"<html>Reference parts<br>per million</html>"
					);
				break;

			case AnalysisDialog.REPORTPARTSPER10000	:
				result	=
					WordHoardSettings.getString
					(
						"coltitleReferencepartsper10000" ,
						"<html>Reference parts<br>per 10,000</html>"
					);
				break;
		}

		return result;
	}

	/**	Get format for percent report method.
	 *
	 *	@return		Format string for percent report method.
	 */

	public String getPercentReportMethodFormat()
	{
		String	result	= "";

		switch ( percentReportMethod )
		{
			case AnalysisDialog.REPORTPERCENTS			:
				result	= getDoubleFormat( 4 );
				break;

			case AnalysisDialog.REPORTPARTSPERMILLION	:
				result	= getDoubleFormat( 0 );
				break;

			case AnalysisDialog.REPORTPARTSPER10000	:
				result	= getDoubleFormat( 2 );
				break;
		}

		return result;
	}

	/**	Get format for double value in table.
	 *
	 *	@param	decimalPlaces	Number of decimal places.
	 */

	public String getDoubleFormat( int decimalPlaces )
	{
		return "%'26." + decimalPlaces + "f";
	}

	/**	Get title for a word counter.
	 *
	 *	@param	wordCounter		Word Counter whose title is desired.
	 *	@param	useShortTitle	Return a short title.
	 *
	 *	@return					A long or short title.
	 */

	public String getTitle
	(
		WordCounter wordCounter ,
		boolean useShortTitle
	)
	{
		String result	= "";

		if ( wordCounter != null )
		{
			result	= wordCounter.toString( useShortTitle );
		}

		return result;
	}

	/**	Check if cancelled flag set in a progress reporter.
	 *
	 *	@param	progressReporter	Progress reporter to check for cancel.
	 */

	public boolean isCancelled( ProgressReporter progressReporter )
	{
		boolean	result	= false;

		if ( progressReporter != null )
		{
			result	= progressReporter.isCancelled();
		}

		return result;
	}

	/**	Watch for selection changes in results table.
	 */

	protected ListSelectionListener tableSelectionListener =
		new ListSelectionListener()
		{
			public void valueChanged( ListSelectionEvent event )
			{
				handleTableSelectionChange( event );
			}
		};

	/**	Show tag cloud of Dunning's log-likelihood profile.
	 *
	 *	@param	title			Cloud title.
	 *	@param	headers			Header lines for cloud.
	 *	@param	compressRange	Compress range of cloud tag values.
	 *	@param	scoreCol		Tag score column in table.
	 *	@param	overUseCol		Over/underuse column in table.
	 *							-1 if none.
	 *	@param	wordClassCol	Word class column in table.
	 *							-1 if none.
	 */

	public ResultsPanel getCloud
	(
		String title ,
		String[] headers ,
		boolean compressRange ,
		int scoreCol ,
		int overUseCol ,
		int wordClassCol
	)
	{
								//	Panel to hold cloud output.

		ResultsPanel cloudPanel		= new ResultsPanel();

								//	Create new empty tag cloud.

		final TagCloud cloud	= new TagCloud();

								//	See if we have any rows selected.
								//	If no rows selected, we generate a
								//	cloud from all the rows.  If some
								//	rows selected, we generate a cloud
								//	only from the selected rows.

		int nSelected	= resultsTable.getSelectedRowCount();

								//	Add tags with corresponding score
								//	to tag cloud.

		for ( int i = 0 ; i < model.getRowCount() ; i++ )
		{
								//	If there are rows selected,
								//	and this row is not selected,
								//	do not enter its data into the cloud.

			if	(	( nSelected > 0 ) &&
					!resultsTable.isRowSelected( i ) ) continue;

								//	Get tag text for cloud entry.

			String tag	= model.getValueAt( i , 0 ).toString();

								//	Remove word class from tag
								//	if requested.

			if ( ( wordClassCol >= 0 ) && showWordClasses )
			{
				tag	=
					tag + " (" + model.getValueAt( i , wordClassCol ) + ")";
			}
								//	Replace any blanks in tag by
								//	special blank replacement character.

			tag	= tag.trim().replaceAll( " " , blankReplacementCharacter );

								//	Get score value for the tag.

			double score		=
				((Double)model.getValueAt( i , scoreCol )).doubleValue();

								//	Apply range compression scaling
								//	if requested.

			if ( compressRange )
			{
				score	=
					Math.pow( 2.0D , ArithUtils.asinh( score ) );
			}
								//	Get over/underuse of this tag
								//	in analysis text with respect to
								//	reference text.

			int relUse		= 0;

			if ( overUseCol >= 0 )
			{
				double dRelUse	=
					((Double)model.getValueAt(
						i , overUseCol )).doubleValue();

				relUse			= (int)dRelUse;
            }
								//	If tag is overused, display its
								//	text in black.  if tag is underused,
								//	display its text in silver.

			if ( relUse >= 0 )
			{
				cloud.addTag( tag , score , "black" );
			}
			else
			{
				cloud.addTag( tag , score , "silver" );
			}
		}
								//	Create HML editor pane to display
								//	cloud.

		final XTextPane editor	=
			new XTextPane()
			{
				public String getText()
				{
					String result	= super.getText();

								//	Strip out href= text so that
								//	the invalid URL text we
								//	use to hold tag information
								//	isn't written to a saved file.

					result	=
						result.replaceAll
						(
							"<a href=\"(.*)\">" ,
							"<a href=\"\">"
						);

								//	Replace non-breaking spaces with
								//	regular spaces.

					result	= result.replaceAll( "\u00A0" , " " );

					return result;
				}
			};

		editor.setEditorKit( new HTMLEditorKit() );

								//	Convert cloud to HTML.
		editor.setText
		(
			"<html>\r\n" +
			cloud.getHTML() +
			"\r\n</html>"
		);

		editor.setCaretPosition( 0 );
		editor.setEditable( false );

								//	Add hyperlink listener so we can
								//	delete tag entries from cloud
								//	when a tag is clicked in the
								//	editor pane holding the cloud.

		editor.addHyperlinkListener
		(
			new HyperlinkListener()
			{
			    public void hyperlinkUpdate( HyperlinkEvent event )
			    {
								//	If a tag is clicked ...

					if	(	event.getEventType() ==
							HyperlinkEvent.EventType.ACTIVATED
						)
					{
								//	Save the current caret position
								//	in the cloud pane.

						int caretPosition	= editor.getCaretPosition();

								//	Remove the tag which was clicked.
								//	The href= field of the link
								//	contains the tag string followed
								//	by a tab followed by the tag score.

						String[] tagData	=
							event.getDescription().split( "\t" );

						cloud.deactivateTag
						(
							tagData[ 0 ] ,
							Double.parseDouble( tagData[ 1 ] )
						);
								//	Recreate the cloud HTML.

						editor.setText
						(
							"<html>\r\n" +
							cloud.getHTML() +
							"\r\n</html>"
						);
								//	Reset the caret position to
								//	its previous position.
						try
						{
							editor.setCaretPosition( caretPosition );
						}
						catch ( Exception e )
						{
						}
					}
				}
			}
		);
								//	Wrap editor pane in scroller.

		XScrollPane scrollPane	=
			new XScrollPane
			(
				editor ,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
			);

		Dimension scrollPaneSize	= scrollPane.getSize();
		Dimension resultsPaneSize	= resultsPanel.getSize();

		scrollPaneSize.width			=
			Math.max( scrollPaneSize.width , resultsPaneSize.width );

		scrollPaneSize.height		= 16384;

		scrollPane.setPreferredSize( scrollPaneSize );

								//	Set results and titles.

		Font titleFont	= resultsTable.getFont();

		StringBuffer headersString	= new StringBuffer();

		for ( int i = 0 ; i < headers.length ; i++ )
		{
			if ( headers[ i ].length() > 0 )
			{
				XSmallLabel labelHeader	= new XSmallLabel( headers[ i ] );
				labelHeader.setFont( titleFont );
				cloudPanel.add( labelHeader );

				if ( i > 0 )
				{
					headersString.append( "  " );
				}

				headersString.append( headers[ i ] );
			}
		}

		cloudPanel.add( scrollPane , 10 );

		cloudPanel.setResults( editor );
		cloudPanel.setResultsHeader( headersString.toString() );
		cloudPanel.setResultsTitle( title );

								//	Return panel containing cloud to caller.
		return cloudPanel;
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


