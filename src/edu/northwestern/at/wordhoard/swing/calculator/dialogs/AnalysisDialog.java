package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	Common base for frequency and collocation dialogs.
 */

public class AnalysisDialog extends SkeletonDialog
{
	/**	--- Dialog types. --- */

	/**	Word form frequencies across all works in corpus. */

	public static final int WORDFORMFREQUENCIES					= 0;

	/**	Compare multiple word forms in two works or corpora. */

	public static final int MULTIPLEWORDFORMCOMPARISON			= 1;

	/**	Compare single word form in two works or corpora. */

	public static final int SINGLEWORDFORMCOMPARISON			= 2;

	/**	Compare single word form across all works in corpus. */

	public static final int SINGLEWORDFORMHISTORY				= 3;

	/**	Collocation frequencies across all works in corpus. */

	public static final int COLLOCATEFREQUENCIES				= 4;

	/**	Compare single word form in one work or copus. */

	public static final int FINDCOLLOCATES						= 5;

	/**	Compare single word form in two works or corpora. */

	public static final int COMPARESINGLECOLLOCATEFREQUENCIES	= 6;

	/**	Compare single word form across all works in corpus. */

	public static final int SINGLECOLLOCATEHISTORY				= 7;

	/**	Compare texts using similarity measures. */

	public static final int COMPARETEXTS						= 8;

	/**	Find multiword units in a text. */

	public static final int FINDMULTIWORDUNITS					= 9;

	/**	Highest value for dialog type. */

	protected static final int LASTDIALOGTYPE					=
		FINDMULTIWORDUNITS;

	/**	The dialog type. */

	protected int dialogType	= COLLOCATEFREQUENCIES;

	/**	Dialog field indices.
	 *
	 *	<p>
	 *	All fields in the various frequency analysis and collocation
	 *	dialogs have unique index.
	 *	</p>
	 */

	protected final static int WORDFIELD							= 0;
	protected final static int WORDFORMFIELD						= 1;
	protected final static int FILTERBYWORDCLASSFIELD				= 2;
	protected final static int LEFTSPANFIELD						= 3;
	protected final static int RIGHTSPANFIELD						= 4;
	protected final static int CUTOFFFIELD							= 5;
	protected final static int ANALYSISTEXTFIELD					= 6;
	protected final static int REFERENCETEXTFIELD					= 7;
	protected final static int PERCENTREPORTMETHODFIELD				= 8;
	protected final static int MARKSIGLOGLIKELIHOODFIELD			= 9;
	protected final static int MINIMUMCOUNTFIELD					= 10;
	protected final static int MINIMUMWORKCOUNTFIELD				= 11;
	protected final static int FREQUENCYNORMALIZATIONFIELD			= 12;
	protected final static int ROUNDNORMALIZEDCOUNTSFIELD			= 13;
	protected final static int FILTEROUTPROPERNAMESFIELD			= 14;
	protected final static int SHOWWORDCLASSESFIELD					= 15;
	protected final static int SHOWPHRASECOUNTSFIELD				= 16;
	protected final static int ANALYZEPHRASECOUNTSFIELD				= 17;
	protected final static int ANALYSISTEXTBREAKDOWNBYFIELD			= 18;
	protected final static int REFERENCETEXTBREAKDOWNBYFIELD		= 19;
	protected final static int ASSOCIATIONMEASUREFIELD				= 20;
	protected final static int MINIMUMMULTIWORDUNITLENGTHFIELD		= 21;
	protected final static int MAXIMUMMULTIWORDUNITLENGTHFIELD		= 22;
	protected final static int FILTERBIGRAMSBYWORDCLASSFIELD		= 23;
	protected final static int FILTERTRIGRAMSBYWORDCLASSFIELD		= 24;
	protected final static int FILTERUSINGLOCALMAXSFIELD 			= 25;
	protected final static int FILTERSINGLEOCCURRENCESFIELD			= 26;
	protected final static int IGNORECASEANDDIACRITICALMARKSFIELD	= 27;
	protected final static int FILTERMULTIWORDUNITSCONTAININGVERBSFIELD	= 28;
	protected final static int ADJUSTCHISQUAREFORMULTIPLECOMPSFIELD	= 29;
	protected final static int USESHORTWORKTITLESINDIALOGS			= 30;
	protected final static int USESHORTWORKTITLESINOUTPUT			= 31;
	protected final static int USESHORTWORKTITLESINWINDOWTITLES		= 32;
	protected final static int USESHORTWORKTITLESINHEADERS			= 33;
	protected final static int COMPRESSVALUERANGEINTEXTCLOUDS		= 34;
	protected final static int MWUCLOUDMEASURE						= 35;

	/**	True if dialog type is a collocation dialog, false otherwise.
	 */

	protected final static boolean isCollocateDialog[]	=
		new boolean[]
		{
			false ,
			false ,
			false ,
			false ,
			true  ,
			true  ,
			true  ,
			true  ,
			true ,
			true
		};

	/**	True to show only corpora in works comboboxes, false to show works too.
	 */

	protected final static boolean onlyDisplayCorpora[]	=
		new boolean[]
		{
			false ,
			false ,
			false ,
			true ,
			false ,
			false ,
			false ,
			true ,
			false ,
			false
		};

	/**	Dialog title. */

	protected String dialogTitle			= "";

	/**	Initial focus fields. */

	protected JComponent initialFocusField	= null;

	/**	Fields to display in each dialog. */

	protected static ArrayList dialogFieldIndices	= new ArrayList();

	/**	Word form text to analyze. */

	protected static String wordText	= "";

	/**	Dialog field for word. */

	protected XTextField wordTextField	= new XTextField();

	/**	Word form. */

	protected static int wordForm	= WordForms.LEMMA;

	/**	Word form choices for dialog box. */

	protected JComboBox wordFormChoices	= new JComboBox();

	/**	True to filter ngrams which only occur once. */

	protected static boolean filterSingleOccurrences	= true;

	/**	True to filter collocates using word class rules. */

	protected static boolean filterByWordClass	= true;

	/**	True to filter bigrams using word class rules. */

	protected static boolean filterBigramsByWordClass	= false;

	/**	True to filter trigrams using word clas rules. */

	protected static boolean filterTrigramsByWordClass	= false;

	/**	True to filter ngrams using localmaxs */

	protected static boolean filterUsingLocalMaxs		= false;

	/**	True to ignore case and diacritical marks. */

	protected static boolean ignoreCaseAndDiacriticalMarks	= true;

	/**	True to filter multiword units containing verbs. */

	protected static boolean filterMultiwordUnitsContainingVerbs	= false;

	/**	Dialog field for filter ngrams that occur only once. */

	protected JCheckBox filterSingleOccurrencesCheckBox	= new JCheckBox();

	/**	Dialog field for filter collocates by word class flag. */

	protected JCheckBox filterByWordClassCheckBox	= new JCheckBox();

	/**	Dialog field for filter bigrams by word class flag. */

	protected JCheckBox filterBigramsByWordClassCheckBox	=
		new JCheckBox();

	/**	Dialog field for filter trigrams by word class flag. */

	protected JCheckBox filterTrigramsByWordClassCheckBox	=
		new JCheckBox();

	/**	Dialog field for filter using localmaxs. */

	protected JCheckBox filterUsingLocalMaxsCheckBox		=
		new JCheckBox();

	/**	Dialog for for ignore case and diacritical marks. */

	protected JCheckBox ignoreCaseAndDiacriticalMarksCheckBox	=
		new JCheckBox();

	/**	Dialog field for filter multiword units containing verbs. */

	protected JCheckBox filterMultiwordUnitsContainingVerbsCheckBox	=
		new JCheckBox();

	/** Left span (# of words to left to consider) */

	public static final int DEFAULTLEFTSPAN	= 1;

	protected static int leftSpan	= DEFAULTLEFTSPAN;

	/**	Left span dialog field. */

	protected XTextField leftSpanField	= new XTextField();

	/** Right span (# of words to right to consider) */

	public static final int DEFAULTRIGHTSPAN	= 1;

	protected static int rightSpan	= DEFAULTRIGHTSPAN;

	/**	Right span dialog field. */

	protected XTextField rightSpanField	= new XTextField();

	/** Minimum multiword unit length. */

	public static final int DEFAULTMINIMUMMULTIWORDUNITLENGTH	= 2;

	protected static int minimumMultiwordUnitLength	=
		DEFAULTMINIMUMMULTIWORDUNITLENGTH;

	/**	Minimum multiword unit length dialog field. */

	protected XTextField minimumMultiwordUnitLengthField
		= new XTextField();

	public static final int DEFAULTMAXIMUMMULTIWORDUNITLENGTH	= 5;

	protected static int maximumMultiwordUnitLength	=
		DEFAULTMAXIMUMMULTIWORDUNITLENGTH;

	/**	Maximum multiword unit length dialog field. */

	protected XTextField maximumMultiwordUnitLengthField	=
		new XTextField();

	/**	Frequency cutoff value. */

	public static final int DEFAULTCUTOFF	= 2;

	protected static int cutoff	= DEFAULTCUTOFF;

	/**	Frequency field dialog field. */

	protected XTextField cutoffField		= new XTextField();

	/**	Analysis corpus. */

	protected static WordCounter analysisText	= null;

	/**	Analysis corpus choices. */

	protected WordCounterTreeCombo analysisTextChoices		= null;

	/**	Reference corpus. */

	protected static WordCounter referenceText	= null;

	/**	Reference corpus choices. */

	protected WordCounterTreeCombo referenceTextChoices	= null;

	/**	Default minimum count word form must have to be displayed
	 *	when profiling multiple words.
	 */

	public static final int DEFAULTMINIMUMCOUNT	= 5;

	/**	Minimum count word form must have in analysis text to be displayed. */

	protected static int minimumCount	= DEFAULTMINIMUMCOUNT;

	/**	Edit field for minimum count. */

	protected XTextField minimumCountField	= new XTextField( 10 );

	/**	Default minimum # of works in which word form must
	 *	appear to be displayed when profiling multiple words.
	 */

	public static final int DEFAULTMINIMUMWORKCOUNT	= 1;

	/**	Minimum work count word form must appear in
	  *	in order for word form to be displayed.
	  */

	protected static int minimumWorkCount	= DEFAULTMINIMUMWORKCOUNT;

	/**	Edit field for minimum work count. */

	protected XTextField minimumWorkCountField	= new XTextField( 10 );

	/**	Count normalization methods for historical profiling.
	 */

	public static final int NORMALIZENONE				= 0;
	public static final int NORMALIZETO10000			= 1;
	public static final int NORMALIZETOMEANWORKLENGTH	= 2;
	public static final int NORMALIZETOPARTSPERMILLION	= 3;

	/**	Count normalization used for historical profiling.
	 */

	public static final int DEFAULTFREQUENCYNORMALIZATIONMETHOD	=
		NORMALIZETO10000;

	protected static int frequencyNormalizationMethod	=
		DEFAULTFREQUENCYNORMALIZATIONMETHOD;

	/**	Combobox selection field for normalization method. */

	protected JComboBox frequencyNormalizationMethodChoices	= new JComboBox();

	/**	Option to round normalized frequencies. */

	protected static boolean roundNormalizedFrequencies	= true;

	/**	Dialog field for option to round normalized frequencies. */

	protected JCheckBox roundNormalizedFrequenciesCheckBox	=
		new JCheckBox();

	/**	Option to compare phrase counts. */

	protected static boolean analyzePhraseFrequencies	= true;

	/**	Dialog field for option to compare phrase counts. */

	protected JCheckBox analyzePhraseFrequenciesCheckBox	=
		new JCheckBox();

	/**	Option to display phrase counts. */

	protected static boolean showPhraseFrequencies	= true;

	/**	Dialog field for option to display phrase counts. */

	protected JCheckBox showPhraseFrequenciesCheckBox	=
		new JCheckBox();

	/**	True if dialog cancelled. */

	protected boolean cancelled	= false;

	/**	Report methods for count percents.
	 */

	public static final int REPORTPERCENTS			= 0;
	public static final int REPORTPARTSPERMILLION	= 1;
	public static final int REPORTPARTSPER10000		= 2;

	/**	Count percent report method to use.
	 */

	public static final int DEFAULTPERCENTREPORTMETHOD	=
		REPORTPARTSPER10000;

	protected static int percentReportMethod			=
		DEFAULTPERCENTREPORTMETHOD;

	/**	Combobox selection field for percentage reporting method.
	 */

	protected JComboBox percentReportMethodChoices	= new JComboBox();

	/**	True to mark significant log-likelihood values. */

	protected static boolean markSignificantLogLikelihoodValues	= true;

	/**	Dialog field for marking significant log-likelihood values. */

	protected JCheckBox markSignificantLogLikelihoodValuesField	=
		new JCheckBox( "Mark significant log-likelihood values" );

	/** True to filter proper names. */

	protected static boolean filterOutProperNames	= false;

	/**	Dialog field for filtering out proper names. */

	protected JCheckBox filterOutProperNamesField	=
		new JCheckBox( "Filter out proper names" );

	/** True to show all word classes following spellings and lemmata. */

	protected static boolean showWordClasses	= false;

	/**	Dialog field for filtering out proper names. */

	protected JCheckBox showWordClassesField	=
		new JCheckBox( "Show word classes for all words" );

	/**	Text breakdown methods for comparing texts.
	 */

	public static final int AGGREGATEALLWORKPARTS       = 0;
	public static final int BREAKDOWNBYWORKS			= 1;
	public static final int BREAKDOWNBYWORKPARTS		= 2;

    /** Default breakdown method is by work.
     */

    public static final int DEFAULTTEXTBREAKDOWNBY      =
		BREAKDOWNBYWORKS;

	/**	Analysis text breakdown method for comparisons.
	 */

	protected static int analysisTextBreakdownBy    =
		DEFAULTTEXTBREAKDOWNBY;

	/**	Radio buttons for analysis text breakdown methods.
	 */

	protected JRadioButton analysisTextAggregateRadioButton;
	protected JRadioButton analysisTextBreakdownByWorkRadioButton;
	protected JRadioButton analysisTextBreakdownByWorkPartRadioButton;

	/**	Reference text breakdown method for comparisons.
	 */

	protected static int referenceTextBreakdownBy   =
		DEFAULTTEXTBREAKDOWNBY;

	/**	Radio buttons for reference text breakdown methods.
	 */

	protected JRadioButton referenceTextAggregateRadioButton;
	protected JRadioButton referenceTextBreakdownByWorkRadioButton;
	protected JRadioButton referenceTextBreakdownByWorkPartRadioButton;

	/**	Association measures for multiword unit extraction.
	 */

	public static final int LOGLIKE			= 0;
	public static final int DICE			= 1;
	public static final int PHISQUARED		= 2;
	public static final int SI				= 3;
	public static final int SCP				= 4;

	/**	Association measure used for multiword unit extraction.
	 */

	public static final int DEFAULTASSOCIATIONMEASURE	= SCP;

	protected static int associationMeasure		= DEFAULTASSOCIATIONMEASURE;

	/**	Combobox selection field for association method. */

	protected JComboBox associationMeasureChoices	= new JComboBox();

	/**	Cloud measure used for multiword unit extraction and collocate
	 *	extraction.
	 */

	public static final int DEFAULTCLOUDMEASURE	= LOGLIKE;

	protected static int cloudMeasure	=
		DEFAULTCLOUDMEASURE;

	/**	Combobox selection field for cloud association method. */

	protected JComboBox cloudMeasureChoices	= new JComboBox();

	/**	True to adjust chi-square values for number of comparisons.
	 */

	protected static boolean adjustChiSquareForMultipleComparisons	= true;

	/**	CheckBox for adjust chi-square field. */

	protected JCheckBox adjustChiSquareForMultipleComparisonsCheckBox	=
		new JCheckBox( "Adjust chi-square for multiple comparisons" );

	/**	True to use short work names in dialogs. */

	protected static boolean useShortWorkTitlesInDialogs	= true;

	/**	CheckBox for use short work names in dialogs field. */

	protected JCheckBox useShortWorkTitlesInDialogsCheckBox	=
		new JCheckBox( "Use short work titles in dialogs" );

	/**	True to use short work names in output. */

	protected static boolean useShortWorkTitlesInOutput	= true;

	/**	CheckBox for use short work names in output field. */

	protected JCheckBox useShortWorkTitlesInOutputCheckBox	=
		new JCheckBox( "Use short work titles in output" );

	/**	True to use short work names in titles. */

	protected static boolean useShortWorkTitlesInWindowTitles	= true;

	/**	CheckBox for use short work names in titles field. */

	protected JCheckBox useShortWorkTitlesInWindowTitlesCheckBox	=
		new JCheckBox( "Use short work titles in window titles" );

	/**	True to use short work names in headers. */

	protected static boolean useShortWorkTitlesInHeaders	= true;

	/**	CheckBox for use short work names in headers field. */

	protected JCheckBox useShortWorkTitlesInHeadersCheckBox	=
		new JCheckBox( "Use short work titles in headers" );

	/**	True to compress value range in word cloud displays. */

	protected static boolean compressValueRangeInTagClouds	= true;

	/**	CheckBox for use short work names in headers field. */

	protected JCheckBox compressValueRangeInTagCloudsCheckBox	=
		new JCheckBox( "Compress log-likelihood value range in text clouds" );

	/**	Constructs and displays a collocation dialog.
	 *
	 *	@param	parentWindow	Parent window.
	 *	@param	dialogType		The dialog type.
	 */

	public AnalysisDialog
	(
		Frame parentWindow ,
		int dialogType
	)
	{
		super
		(
			"" ,
			parentWindow ,
			WordHoardSettings.getString( "OK" , "OK" ) ,
			WordHoardSettings.getString( "Cancel" , "Cancel" ) ,
			WordHoardSettings.getString( "Revert" , "Revert" )
		 );

		this.parentWindow	= parentWindow;
		this.dialogType		= dialogType;

		buildDialog();

		setInitialFocus( wordTextField );
	}

	/**	Get dialog settings based upon dialog type.
	 */

	protected void getDialogSettings()
	{
		dialogTitle	= "";

		switch ( dialogType )
		{
			case SINGLEWORDFORMCOMPARISON	:
				dialogTitle	=
					WordHoardSettings.getString
					(
						"SingleWordFormFrequencyProfile" ,
						"Compare single word form"
					);

				break;

			case SINGLEWORDFORMHISTORY		:
				dialogTitle	=
					WordHoardSettings.getString
					(
						"SingleWordFormHistoricalProfile" ,
						"Track a word form over time"
					);
				break;

			case WORDFORMFREQUENCIES		:
				dialogTitle	=
					WordHoardSettings.getString
					(
						"Createwordlist" ,
						"Create word list"
					);

				setInitialFocus( analysisTextChoices );
				break;

			case MULTIPLEWORDFORMCOMPARISON			:
				dialogTitle	=
					WordHoardSettings.getString
					(
						"Comparemultiplewordformfrequencies" ,
						"Compare multiple word form frequencies"
					);

				setInitialFocus( analysisTextChoices );
            	break;

			case COLLOCATEFREQUENCIES				:
				dialogTitle	=
					WordHoardSettings.getString
					(
						"Createcollocatelist" ,
						"Create collocate list"
					);
            	break;

			case FINDCOLLOCATES						:
				dialogTitle	=
					WordHoardSettings.getString
					(
						"FindCollocates" ,
						"Find Collocates"
					);
            	break;

			case COMPARESINGLECOLLOCATEFREQUENCIES	:
				dialogTitle	=
					WordHoardSettings.getString
					(
						"SingleCollocateFrequencyProfile" ,
						"Compare single collocate"
					);
            	break;

			case SINGLECOLLOCATEHISTORY				:
				dialogTitle	=
					WordHoardSettings.getString
					(
						"SingleCollocateHistoricalProfile" ,
						"Track a collocate over time"
					);
            	break;

			case COMPARETEXTS					:
				dialogTitle	=
					WordHoardSettings.getString
					(
						"CompareTexts" ,
						"Compare Texts"
					);
            	break;

			case FINDMULTIWORDUNITS					:
				dialogTitle	=
					WordHoardSettings.getString
					(
						"Findmultiwordunits" ,
						"Find Multiword Units"
					);
            	break;
		}
	}

	/**	Build the dialog. */

	protected void buildDialog()
	{
								//	Get dialog title and settings.
		getDialogSettings();

								//	Set dialog title.

		setTitle( dialogTitle );

								//	Build rest of dialog.
		super.buildDialog();
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Initialize word text.  This may be
								//	a spelling, lemma, word class,
								//	speaker gender, verse flag,
								//	or metrical shape,
								//	depending upon the setting of the
								//	word form field.

		wordTextField.setText( wordText );

								//	Initialize word form field.  For
								//	collocate analyses, only allow spelling
								//	and lemma.
								//	For non-collocate analyses, allow
								//	word class, speaker gender, verse,
								//	and metrical shape as well.

		wordFormChoices.removeAllItems();

		String lemmaString	=
			WordHoardSettings.getString(
				"Lemma" , "Lemma" );

		String spellingString	=
			WordHoardSettings.getString(
				"Spelling" , "Spelling" );

		String wordClassString	=
			WordHoardSettings.getString(
				"WordClass" , "Word Class" );

		String speakerGenderString	=
			WordHoardSettings.getString(
				"SpeakerGender" , "Speaker Gender" );

		String speakerMortalityString	=
			WordHoardSettings.getString(
				"SpeakerMortality" , "Speaker Mortality" );

		String metricalShapeString	=
			WordHoardSettings.getString(
				"MetricalShape" , "Metrical Shape" );

		String proseOrPoetryString	=
			WordHoardSettings.getString(
				"Verse" , "Verse" );

		if ( isCollocateDialog[ dialogType ] )
		{
			wordFormChoices.addItem( lemmaString );
			wordFormChoices.addItem( spellingString );
		}
		else
		{
			wordFormChoices.addItem( lemmaString );
			wordFormChoices.addItem( spellingString );
			wordFormChoices.addItem( wordClassString );
			wordFormChoices.addItem( speakerGenderString );
			wordFormChoices.addItem( speakerMortalityString );
			wordFormChoices.addItem( proseOrPoetryString );
			wordFormChoices.addItem( metricalShapeString );
		}

		wordFormChoices.setSelectedItem( getWordFormString( wordForm ) );

		String filterByWordClassString	=
			WordHoardSettings.getString
			(
				"Filterbywordclass" ,
				"Filter by word class"
			);

		filterByWordClassCheckBox.setText( filterByWordClassString );
		filterByWordClassCheckBox.setSelected( false );

		String filterSingleOccurrencesString	=
			WordHoardSettings.getString
			(
				"Filtersingleoccurrences" ,
				"Filter multiword units which occur only once"
			);

		filterSingleOccurrencesCheckBox.setText(
			filterSingleOccurrencesString );

		filterSingleOccurrencesCheckBox.setSelected(
			filterSingleOccurrences );

		String filterBigramsByWordClassString	=
			WordHoardSettings.getString
			(
				"Filterbigramsbywordclass" ,
				"Filter bigrams by word class"
			);

		filterBigramsByWordClassCheckBox.setText(
			filterBigramsByWordClassString );

		filterBigramsByWordClassCheckBox.setSelected(
			filterBigramsByWordClass );

		String filterTrigramsByWordClassString	=
			WordHoardSettings.getString
			(
				"Filtertrigramsbywordclass" ,
				"Filter trigrams by word class"
			);

		filterTrigramsByWordClassCheckBox.setText(
			filterTrigramsByWordClassString );

		filterTrigramsByWordClassCheckBox.setSelected(
			filterTrigramsByWordClass );

								//

		String filterMultiwordUnitsContainingVerbsString	=
			WordHoardSettings.getString
			(
				"Filtermultiwordunitscontainingverbs" ,
				"Filter multiword units containing verbs"
			);

		filterMultiwordUnitsContainingVerbsCheckBox.setText(
			filterMultiwordUnitsContainingVerbsString );

		filterMultiwordUnitsContainingVerbsCheckBox.setSelected(
			filterMultiwordUnitsContainingVerbs );

								//	Association measures.
		String dice	=
			WordHoardSettings.getString(
				"Dice" ,
				"Dice coefficient" );

		String logLike	=
			WordHoardSettings.getString(
				"Loglike" ,
				"Log-Likelihood" );

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

		associationMeasureChoices.addItem( dice);
		associationMeasureChoices.addItem( logLike );
		associationMeasureChoices.addItem( phi );
		associationMeasureChoices.addItem( si );
		associationMeasureChoices.addItem( scp );

		associationMeasureChoices.addItemListener
		(
			new ItemListener()
			{
				public void itemStateChanged( ItemEvent event )
				{
					if ( event.getStateChange() == ItemEvent.SELECTED )
					{
						String selection	=
							(String)associationMeasureChoices.getSelectedItem();

						String logLike		=
							WordHoardSettings.getString
							(
								"Loglike" ,
								"Log-Likelihood"
							);

						compressValueRangeInTagCloudsCheckBox.setEnabled
						(
							selection.equals( logLike )
						);
					}
				}
			}
		);

		associationMeasureChoices.setSelectedItem(
			getAssociationMeasureString( associationMeasure ) );

		cloudMeasureChoices.addItem( dice);
		cloudMeasureChoices.addItem( logLike );
		cloudMeasureChoices.addItem( phi );
		cloudMeasureChoices.addItem( si );
		cloudMeasureChoices.addItem( scp );

		cloudMeasureChoices.setSelectedItem(
			getAssociationMeasureString( cloudMeasure ) );

		String filterUsingLocalMaxsString	=
			WordHoardSettings.getString
			(
				"Filterusinglocalmaxs" ,
				"Filter using localmaxs"
			);

		filterUsingLocalMaxsCheckBox.setText(
			filterUsingLocalMaxsString );

		filterUsingLocalMaxsCheckBox.setSelected(
			filterUsingLocalMaxs );

		associationMeasureChoices.setEnabled( filterUsingLocalMaxs );

		filterUsingLocalMaxsCheckBox.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					associationMeasureChoices.setEnabled(
						filterUsingLocalMaxsCheckBox.isSelected() );
				}
			}
		);

		leftSpanField.setText( StringUtils.intToString( leftSpan ) );
		rightSpanField.setText( StringUtils.intToString( rightSpan ) );
		cutoffField.setText( StringUtils.intToString( cutoff ) );

		if ( dialogType == FINDMULTIWORDUNITS )
		{
			analysisTextChoices	=
				new WordCounterTreeCombo(
					false , true , false , true , analysisText );

		}
		else if ( onlyDisplayCorpora[ dialogType ] )
		{
			analysisTextChoices	=
				new WordCounterTreeCombo(
					true , false , true , true , analysisText );
		}
		else
	    {
			analysisTextChoices	=
				new WordCounterTreeCombo(
					true , true , true , true , analysisText );
	    }

		analysisTextChoices.setMaximumRowCount( 18 );

		referenceTextChoices	=
			new WordCounterTreeCombo(
				analysisTextChoices , referenceText );

		referenceTextChoices.setMaximumRowCount( 18 );

		WordCounter analysisWordCounter		=
			analysisTextChoices.getSelectedWordCounter();

		showPhraseFrequenciesCheckBox.setEnabled
		(
			( analysisWordCounter != null ) &&
			( analysisWordCounter.isPhraseSet() )
		);

		WordCounter referenceWordCounter		=
			referenceTextChoices.getSelectedWordCounter();

		analyzePhraseFrequenciesCheckBox.setEnabled
		(
			( analysisWordCounter != null ) &&
			( analysisWordCounter.isPhraseSet() ) &&
			( referenceWordCounter != null ) &&
			( referenceWordCounter.isPhraseSet() )
		);

		analysisTextChoices.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					WordCounter analysisWordCounter		=
						analysisTextChoices.getSelectedWordCounter();

					showPhraseFrequenciesCheckBox.setEnabled
					(
						( analysisWordCounter != null ) &&
						( analysisWordCounter.isPhraseSet() )
					);

					analyzePhraseFrequenciesCheckBox.setEnabled
					(
						( analysisWordCounter != null ) &&
						( analysisWordCounter.isPhraseSet() )
					);
				}
			}
		);

		referenceTextChoices.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					WordCounter analysisWordCounter		=
						analysisTextChoices.getSelectedWordCounter();

					WordCounter referenceWordCounter		=
						referenceTextChoices.getSelectedWordCounter();

					analyzePhraseFrequenciesCheckBox.setEnabled
					(
						( analysisWordCounter != null ) &&
						( analysisWordCounter.isPhraseSet() ) &&
						( referenceWordCounter != null ) &&
						( referenceWordCounter.isPhraseSet() )
					);
				}
			}
		);
								//	Percent reporting methods.

		String reportPercents	=
			WordHoardSettings.getString(
				"ReportPercents" ,
				"Percents" );

		String reportPartsPerTenThousand	=
			WordHoardSettings.getString(
				"ReportPartsPerTenThousand" ,
				"Parts Per Ten Thousand" );

		String reportPartsPerMillion	=
			WordHoardSettings.getString(
				"ReportPartsPerMillion" ,
				"Rounded Parts Per Million" );

		percentReportMethodChoices.addItem( reportPercents );
		percentReportMethodChoices.addItem( reportPartsPerTenThousand );
		percentReportMethodChoices.addItem( reportPartsPerMillion );

		percentReportMethodChoices.setSelectedItem(
			getPercentReportMethodString( percentReportMethod ) );

		String markSignificant	=
			WordHoardSettings.getString(
				"Marksignificantloglikelihoodvalues" ,
				"Mark statistically significant log-likelihood values" );

		markSignificantLogLikelihoodValuesField.setText( markSignificant );

		markSignificantLogLikelihoodValuesField.setSelected(
			markSignificantLogLikelihoodValues );

		minimumCountField.setText(
			StringUtils.intToString( minimumCount ) );

		minimumWorkCountField.setText(
			StringUtils.intToString( minimumWorkCount ) );

								//	Count normalization methods for
								//	historical profiles.

		String normalizeNone	=
			WordHoardSettings.getString(
				"NoNormalization" , "No Normalization" );

		String normalize10000	=
			WordHoardSettings.getString(
				"NormalizeTo10000" ,
				"Normalize to counts per 10,000 words" );

		String normalizeMeanWorkLength	=
			WordHoardSettings.getString(
				"NormalizeMeanWorkLength" ,
				"Normalize to average word count in reference works" );

		String normalizeToPartsPerMillion	=
			WordHoardSettings.getString(
				"NormalizeToPartsPerMillion" ,
				"Normalize to words per million" );

		frequencyNormalizationMethodChoices.addItem( normalizeNone );
		frequencyNormalizationMethodChoices.addItem( normalize10000 );
		frequencyNormalizationMethodChoices.addItem( normalizeMeanWorkLength );
		frequencyNormalizationMethodChoices.addItem( normalizeToPartsPerMillion );

		frequencyNormalizationMethodChoices.setSelectedItem(
			getFrequencyNormalizationString( frequencyNormalizationMethod ) );

		roundNormalizedFrequenciesCheckBox.setText(
			WordHoardSettings.getString(
				"RoundNormalizedFrequencies" ,
				"Round Normalized Frequencies" ) );

		roundNormalizedFrequenciesCheckBox.setSelected(
			roundNormalizedFrequencies );

		String markSignificantString	=
			WordHoardSettings.getString(
				"Marksignificantloglikelihoodvalues" ,
				"Mark statistically significant log-likelihood values" );

		markSignificantLogLikelihoodValuesField.setText( markSignificantString );

		markSignificantLogLikelihoodValuesField.setSelected(
			markSignificantLogLikelihoodValues );

		String filterProperNamesString	=
			WordHoardSettings.getString(
				"Filteroutpropernames" ,
				"Filter out proper names" );

		filterOutProperNamesField.setText( filterProperNamesString );

		filterOutProperNamesField.setSelected(
			filterOutProperNames );

		String showWordClassesString	=
			WordHoardSettings.getString(
				"Showwordclasses" ,
				"Show word classes for all words" );

		showWordClassesField.setText( showWordClassesString );
		showWordClassesField.setSelected( showWordClasses );

		String showPhraseFrequenciesString	=
			WordHoardSettings.getString(
				"Showphrasecounts" ,
				"Show phrase counts instead of word counts" );

		showPhraseFrequenciesCheckBox.setText( showPhraseFrequenciesString );
		showPhraseFrequenciesCheckBox.setSelected( showPhraseFrequencies );

		String analyzePhraseFrequenciesString	=
			WordHoardSettings.getString(
				"Analyzephrasecounts" ,
				"Analyze phrase counts instead of word counts" );

		analyzePhraseFrequenciesCheckBox.setText( analyzePhraseFrequenciesString );
		analyzePhraseFrequenciesCheckBox.setSelected( analyzePhraseFrequencies );

		String analysisTextAggregate		=
			WordHoardSettings.getString
			(
				"AnalysisTextAggregate" ,
				"Aggregate works and work parts"
			);

		String analysisTextBreakdownByWorks	=
			WordHoardSettings.getString
			(
				"AnalysisTextBreakdownByWorks" ,
				"Break down by works"
			);

		String analysisTextBreakdownByWorkParts	=
			WordHoardSettings.getString
			(
				"AnalysisTextBreakdownByWorkParts" ,
				"Break down by work parts"
			);

		analysisTextAggregateRadioButton				=
			new JRadioButton( analysisTextAggregate );

		analysisTextBreakdownByWorkRadioButton			=
			new JRadioButton( analysisTextBreakdownByWorks );

		analysisTextBreakdownByWorkPartRadioButton		=
			new JRadioButton( analysisTextBreakdownByWorkParts );

		ButtonGroup analysisTextButtonGroup	= new ButtonGroup();

		analysisTextButtonGroup.add( analysisTextAggregateRadioButton );
		analysisTextButtonGroup.add( analysisTextBreakdownByWorkRadioButton );
		analysisTextButtonGroup.add( analysisTextBreakdownByWorkPartRadioButton );

		switch ( analysisTextBreakdownBy )
		{
			case AGGREGATEALLWORKPARTS	:
				analysisTextAggregateRadioButton.setSelected( true );
				break;

			case BREAKDOWNBYWORKPARTS	:
				analysisTextBreakdownByWorkPartRadioButton.setSelected( true );
				break;

			default     				:
				analysisTextBreakdownByWorkRadioButton.setSelected( true );
				break;
		}

		String referenceTextAggregate		=
			WordHoardSettings.getString
			(
				"ReferenceTextAggregate" ,
				"Aggregate works and work parts"
			);

		String referenceTextBreakdownByWorks	=
			WordHoardSettings.getString
			(
				"ReferenceTextBreakdownByWorks" ,
				"Break down by works"
			);

		String referenceTextBreakdownByWorkParts	=
			WordHoardSettings.getString
			(
				"ReferenceTextBreakdownByWorkParts" ,
				"Break down by work parts"
			);

		referenceTextAggregateRadioButton				=
			new JRadioButton( referenceTextAggregate );

		referenceTextBreakdownByWorkRadioButton			=
			new JRadioButton( referenceTextBreakdownByWorks );

		referenceTextBreakdownByWorkPartRadioButton		=
			new JRadioButton( referenceTextBreakdownByWorkParts );

		ButtonGroup referenceTextButtonGroup	= new ButtonGroup();

		referenceTextButtonGroup.add( referenceTextAggregateRadioButton );
		referenceTextButtonGroup.add( referenceTextBreakdownByWorkRadioButton );
		referenceTextButtonGroup.add( referenceTextBreakdownByWorkPartRadioButton );

		switch ( referenceTextBreakdownBy )
		{
			case AGGREGATEALLWORKPARTS	:
				referenceTextAggregateRadioButton.setSelected( true );
				break;

			case BREAKDOWNBYWORKPARTS	:
				referenceTextBreakdownByWorkPartRadioButton.setSelected( true );
				break;

			default     				:
				referenceTextBreakdownByWorkRadioButton.setSelected( true );
				break;
		}

		minimumMultiwordUnitLengthField.setText(
			StringUtils.intToString( minimumMultiwordUnitLength ) );

		maximumMultiwordUnitLengthField.setText(
			StringUtils.intToString( maximumMultiwordUnitLength ) );

		String ignoreCaseAndDiacriticalMarksString	=
			WordHoardSettings.getString
			(
				"Ignorecaseanddiacriticalmarks" ,
				"Ignore case and diacritical marks"
			);

		ignoreCaseAndDiacriticalMarksCheckBox.setText(
			ignoreCaseAndDiacriticalMarksString );

		ignoreCaseAndDiacriticalMarksCheckBox.setSelected(
			ignoreCaseAndDiacriticalMarks );

		String adjustChiSquareForMultipleComparisonsString	=
			WordHoardSettings.getString
			(
				"Adjustchisquareformultiplecomparisons" ,
				"Adjust chi-square for multiple comparisons"
			);

		adjustChiSquareForMultipleComparisonsCheckBox.setText(
			adjustChiSquareForMultipleComparisonsString );

		adjustChiSquareForMultipleComparisonsCheckBox.setSelected(
			adjustChiSquareForMultipleComparisons );

								//	Set up to use short or long object
								//	titles in dialogs.

		useShortWorkTitlesInDialogsCheckBox.setText
		(
			WordHoardSettings.getString
			(
				"Useshortworktitlesindialogs" ,
				"Use short work titles in dialogs"
			)
    	);

		useShortWorkTitlesInDialogsCheckBox.setSelected
		(
			useShortWorkTitlesInDialogs
    	);
								//	Set up to use short or long object
								//	titles in titles.

		useShortWorkTitlesInWindowTitlesCheckBox.setText
		(
			WordHoardSettings.getString
			(
				"Useshortworktitlesinwindowtitles" ,
				"Use short work titles in window titles"
			)
    	);

		useShortWorkTitlesInWindowTitlesCheckBox.setSelected
		(
			useShortWorkTitlesInWindowTitles
    	);
								//	Set up to use short or long object
								//	titles in headers.

		useShortWorkTitlesInHeadersCheckBox.setText
		(
			WordHoardSettings.getString
			(
				"Useshortworktitlesinheaders" ,
				"Use short work titles in headers"
			)
    	);

		useShortWorkTitlesInHeadersCheckBox.setSelected
		(
			useShortWorkTitlesInHeaders
    	);
								//	Set up to use short or long object
								//	titles in output.

		useShortWorkTitlesInOutputCheckBox.setText
		(
			WordHoardSettings.getString
			(
				"Useshortworktitlesinoutput" ,
				"Use short work titles in output"
			)
    	);

		useShortWorkTitlesInOutputCheckBox.setSelected
		(
			useShortWorkTitlesInOutput
    	);
								//	Set up to compress value range for
								//	word cloud displays.

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
			compressValueRangeInTagClouds
    	);
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		super.addFields( dialogFields );

		int[] fieldIndices	=
			(int[])dialogFieldIndices.get( dialogType );

		boolean seenAssociationMeasures	= false;

		for ( int i = 0 ; i < fieldIndices.length ; i++ )
		{
			switch( fieldIndices[ i ] )
			{
				case WORDFIELD					:
					dialogFields.addPair
					(
						WordHoardSettings.getString( "Word" , "Word" ) ,
						wordTextField
					);
					break;

				case WORDFORMFIELD				:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"WordForm" ,
							"Word Form"
						) ,
						wordFormChoices
					);
					break;

				case FILTERBYWORDCLASSFIELD	:
					dialogFields.addPair
					(
						""  ,
						filterByWordClassCheckBox
					);
					break;

				case LEFTSPANFIELD				:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"LeftSpan" ,
							"Left span"
						) ,
						leftSpanField
					);
					break;

				case RIGHTSPANFIELD				:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"RightSpan" ,
							"Right span"
						) ,
						rightSpanField
					);
					break;

				case CUTOFFFIELD				:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"Cutoff" ,
							"Cutoff"
						) ,
						cutoffField
					);
					break;

				case ANALYSISTEXTFIELD			:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"AnalysisWorks" ,
							"Analysis Work(s)"
						) ,
						analysisTextChoices
					);
					break;

				case REFERENCETEXTFIELD			:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"ReferenceWorks" ,
							"Reference Work(s)"
						) ,
						referenceTextChoices
					);
					break;

				case PERCENTREPORTMETHODFIELD	:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"ReportPercentsAs" ,
							"Report Percents As"
						) ,
						percentReportMethodChoices
					);
					break;

				case MARKSIGLOGLIKELIHOODFIELD	:
					dialogFields.addPair
					(
						"" ,
						markSignificantLogLikelihoodValuesField
					);
					break;

				case ADJUSTCHISQUAREFORMULTIPLECOMPSFIELD	:
					dialogFields.addPair
					(
						"" ,
						adjustChiSquareForMultipleComparisonsCheckBox
					);
					break;


				case MINIMUMCOUNTFIELD			:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"MinimumCount" ,
							"Minimum Count"
						) ,
						minimumCountField
					);
                    break;

				case MINIMUMWORKCOUNTFIELD		:
                    dialogFields.addPair
                    (
						WordHoardSettings.getString
						(
							"MinimumWorks" ,
							"Minimum Works"
						) ,
						minimumWorkCountField
					);
					break;

				case FREQUENCYNORMALIZATIONFIELD:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"FrequencyNormalizationMethod" ,
							"Frequency Normalization Method"
						) ,
						frequencyNormalizationMethodChoices
					);
					break;

				case ROUNDNORMALIZEDCOUNTSFIELD	:
					dialogFields.addPair
					(
						"" ,
						roundNormalizedFrequenciesCheckBox
					);
					break;

				case FILTEROUTPROPERNAMESFIELD	:
					dialogFields.addPair
					(
    					"" ,
						filterOutProperNamesField
					);
					break;

				case SHOWWORDCLASSESFIELD	:
					dialogFields.addPair
					(
						"" ,
						showWordClassesField
					);
					break;

				case SHOWPHRASECOUNTSFIELD	:
					dialogFields.addPair
					(
						"" ,
						showPhraseFrequenciesCheckBox
					);
					break;

				case ANALYZEPHRASECOUNTSFIELD	:
					dialogFields.addPair
					(
						"" ,
						analyzePhraseFrequenciesCheckBox
					);
					break;

				case ANALYSISTEXTBREAKDOWNBYFIELD :
					JPanel buttonPanel	= new JPanel();

					buttonPanel.setLayout
					(
						new BoxLayout( buttonPanel , BoxLayout.Y_AXIS )
					);

					buttonPanel.add
					(
						analysisTextAggregateRadioButton
					);

					buttonPanel.add
					(
						analysisTextBreakdownByWorkRadioButton
					);

					buttonPanel.add
					(
						analysisTextBreakdownByWorkPartRadioButton
					);

					dialogFields.addPair( "" , buttonPanel );

					break;

				case REFERENCETEXTBREAKDOWNBYFIELD:
					JPanel buttonPanel2	= new JPanel();

					buttonPanel2.setLayout
					(
						new BoxLayout( buttonPanel2 , BoxLayout.Y_AXIS )
					);

					buttonPanel2.add
					(
						referenceTextAggregateRadioButton
					);

					buttonPanel2.add
					(
						referenceTextBreakdownByWorkRadioButton
					);

					buttonPanel2.add
					(
						referenceTextBreakdownByWorkPartRadioButton
					);

					dialogFields.addPair( "" , buttonPanel2 );

					break;

				case ASSOCIATIONMEASUREFIELD			:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"AssociationMeasure" ,
							"Association Measure"
						) ,
						associationMeasureChoices
					);

					seenAssociationMeasures	= true;

					break;

				case MINIMUMMULTIWORDUNITLENGTHFIELD	:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"MinimumMWULength" ,
							"Minimum multiword unit length"
						) ,
						minimumMultiwordUnitLengthField
					);

					break;

				case MAXIMUMMULTIWORDUNITLENGTHFIELD	:
					dialogFields.addPair
					(
						WordHoardSettings.getString
						(
							"MaximumMWULength" ,
							"Maximum multiword unit length"
						) ,
						maximumMultiwordUnitLengthField
					);

					break;

				case FILTERSINGLEOCCURRENCESFIELD		:
					dialogFields.addPair
					(
						"" ,
						filterSingleOccurrencesCheckBox
					);

					break;

				case FILTERBIGRAMSBYWORDCLASSFIELD		:
					dialogFields.addPair
					(
						"" ,
						filterBigramsByWordClassCheckBox
					);

					break;

				case FILTERMULTIWORDUNITSCONTAININGVERBSFIELD	:
					dialogFields.addPair
					(
						"" ,
						filterMultiwordUnitsContainingVerbsCheckBox
					);

					break;

				case FILTERTRIGRAMSBYWORDCLASSFIELD		:
					dialogFields.addPair
					(
						"" ,
						filterTrigramsByWordClassCheckBox
					);

					break;

				case FILTERUSINGLOCALMAXSFIELD			:
					dialogFields.addPair
					(
						"" ,
						filterUsingLocalMaxsCheckBox
					);

					break;

				case IGNORECASEANDDIACRITICALMARKSFIELD	:
					dialogFields.addPair
					(
						"" ,
						ignoreCaseAndDiacriticalMarksCheckBox
					);

					break;

				case USESHORTWORKTITLESINDIALOGS		:
					dialogFields.addPair
					(
						"" ,
						useShortWorkTitlesInDialogsCheckBox
					);

					break;

				case USESHORTWORKTITLESINOUTPUT			:
					dialogFields.addPair
					(
						"" ,
						useShortWorkTitlesInOutputCheckBox
					);

					break;

				case USESHORTWORKTITLESINWINDOWTITLES			:
					dialogFields.addPair
					(
						"" ,
						useShortWorkTitlesInWindowTitlesCheckBox
					);

					break;

				case USESHORTWORKTITLESINHEADERS			:
					dialogFields.addPair
					(
						"" ,
						useShortWorkTitlesInHeadersCheckBox
					);

					break;

				case COMPRESSVALUERANGEINTEXTCLOUDS		:
					dialogFields.addPair
					(
						"" ,
						compressValueRangeInTagCloudsCheckBox
					);

					compressValueRangeInTagCloudsCheckBox.setEnabled( true );

					break;
			}
		}
								//	This ensures the
								//	CompressValueRangeInTagClouds
								//	checkbox is set correctly.

		if ( seenAssociationMeasures )
		{
			String logLike	=
				WordHoardSettings.getString(
					"Loglike" ,
					"Log-Likelihood" );

			compressValueRangeInTagCloudsCheckBox.setEnabled
			(
				associationMeasure == LOGLIKE
			);
        }
	}

	/**	Check word field for valid value.
	 *
	 *	@return		true if word field is valid.
	 */

	protected boolean validateWordField()
	{
		boolean result	= false;

								//	Don't validate phrases.
								//	$$$PIB$$$ Fix this when phrase support
								//	added.

		if	( analysisText.isPhraseSet() )
		{
			if	(	(	showPhraseFrequenciesCheckBox.isEnabled() ) ||
					(	analyzePhraseFrequenciesCheckBox.isEnabled() ) )
			{
				return true;
			}
		}
								//	Clean the word text.
		cleanWordText();

        Object[] wordFormObjects	= null;

		switch ( wordForm )
		{
			case WordForms.LEMMA	:

								//	If the specified lemma string doesn't
								//	exist, pick up list of lemmata
								//	starting with the specified text.

				if ( LemmaUtils.lemmaExists( wordText ) ) return true;

				wordFormObjects	=
					LemmaUtils.getLemmataByInitialString( wordText );

				break;

			case WordForms.SPELLING	:

								//	If the specified spelling string doesn't
								//	exist, pick up list of spellings
								//	starting with the specified text.

				if ( WordUtils.spellingExists( wordText ) ) return true;

				wordFormObjects	=
					WordUtils.getSpellingsByInitialString( wordText );

        		break;

			case WordForms.WORDCLASS				:

								//	If the specified word class string
								//	doesn't exist, pick up list of word classes
								//	starting with the specified text.

				if ( WordClassUtils.wordClassExists( wordText ) ) return true;

				wordFormObjects	= WordClassUtils.getWordClasses();

				break;

			case WordForms.SPEAKERGENDER	:

								//	If the specified speaker gender doesn't
								//	exist, pick up list of speaker genders.

				if ( WordUtils.speakerGenderExists( wordText ) ) return true;

				wordFormObjects	= WordUtils.getSpeakerGenders();

				break;

			case WordForms.SPEAKERMORTALITY :

								//	If the specified speaker mortality
								//	doesn't exist, pick up list of
								//	speaker mortalities.

				if ( WordUtils.speakerMortalityExists( wordText ) ) return true;

				wordFormObjects	= WordUtils.getSpeakerMortalities();

				break;

			case WordForms.ISVERSE			:

								//	If the specified "isVerse" string
								//	doesn't exist, pick up list of isVerse
								//	strings.

				if ( WordUtils.isVerseExists( wordText ) ) return true;

				wordFormObjects	= WordUtils.getIsVerseValues();

				break;

			case WordForms.METRICALSHAPE    :

								//	If the specified metrical shape
								//	doesn't exist, pick up list of
								//	metrical shape strings.

				if ( WordUtils.metricalShapeExists( wordText ) ) return true;

				wordFormObjects	= WordUtils.getMetricalShapeValues();

				break;
		}

		if ( wordFormObjects == null ) return result;

		switch ( wordFormObjects.length )
		{
								//	If there is no word form object that
								//	starts with the specified string,
								//	report an error.

			case 0	:
				new ErrorMessage
				(
					WordHoardSettings.getString
					(
						"Thewordformyouselecteddoesnotexist" ,
						"The word form you selected does not exist."
					)
				);
				break;

								//	If there is only one word that
								//	starts with the specified text,
								//	use that.

			case 1	:
				wordText	= wordFormObjects[ 0 ].toString();
				result		= true;
				break;

								//	When more than one word matches,
								//	display a list box of possible choices.

			default	:
				String[] wordFormObjectsForDisplay	=
					new String[ wordFormObjects.length ];

				for ( int i = 0 ; i < wordFormObjects.length ; i++ )
				{
					wordFormObjectsForDisplay[ i ]	=
						WordUtils.getDisplayableWordText
						(
							WordUtils.getSpellingForString
							(
								wordFormObjects[ i ].toString()
							) ,
							wordForm
						);
				}

				DisplayListBox wordFormsListBox	=
					new DisplayListBox
					(
						parentWindow ,
						WordHoardSettings.getString
						(
							"Choosewordform" ,
							"Choose word form"
						) ,
						new JLabel
						(
							WordHoardSettings.getString
							(
								"Pleasechoosewordform" ,
								"Please choose one of the " +
								" following:"
							)
						),
						wordFormObjectsForDisplay ,
						null ,
						false ,
						true ,
						null ,
						WordHoardSettings.getString
						(
							"OK" , "OK"
						) ,
						WordHoardSettings.getString
						(
							"Cancel" , "Cancel"
						)
					);

            	if ( !wordFormsListBox.getCancelled() )
				{
/*
					Object[] selectedWordForms	=
						wordFormsListBox.getSelected();

					if ( selectedWordForms.length > 0 )
					{
						wordText	= selectedWordForms[ 0 ].toString();
						result		= true;
					}
*/
					int selected	=
						wordFormsListBox.getMinSelectionIndex();

					if ( selected >= 0 )
					{
						wordText	= wordFormObjects[ selected ].toString();
						result		= true;
					}
				}
		}

		return result;
	}

	/**	Process OK button pressed.
	 *
	 *	@param	event	The event.
	 *
	 *	<p>
	 *	Allows overriding the OK button handling from a subclass.
	 *	</p>
	 */

	protected void handleOKButtonPressed( ActionEvent event )
	{
								//	Pick up fields used in this dialog.
		int[] fieldIndices	=
			(int[])dialogFieldIndices.get( dialogType );

								//	Perform any needed validation for
								//	each dialog field.

		boolean wordFieldUsed	= false;

		for ( int i = 0 ; i < fieldIndices.length ; i++ )
		{
			switch( fieldIndices[ i ] )
			{
				case WORDFIELD					:
					wordText		= wordTextField.getText().trim();

					cleanWordText();

        			wordFieldUsed	= true;

					if ( wordText.length() == 0 )
					{
						wordForm	=
							getWordForm(
 								(String)wordFormChoices.getSelectedItem() );

						analysisText	=
							(WordCounter)analysisTextChoices.getSelectedWordCounter();

						if	(	( wordForm == WordForms.SPELLING ) ||
								( wordForm == WordForms.LEMMA ) ||
								( wordForm == WordForms.METRICALSHAPE ) ||
								( !validateWordField() ) )
						{
							PrintfFormat errorFormat	=
								new PrintfFormat
								(
									WordHoardSettings.getString
									(
										"Youmustenteraword" ,
										"You must enter a %s"
									)
								);

							new ErrorMessage
							(
								errorFormat.sprintf
								(
									new Object[]
									{
										getWordFormString( wordForm )
									}
								)
							);

							return;
						}
					}

					break;

				case WORDFORMFIELD				:
					wordForm	=
						getWordForm(
 							(String)wordFormChoices.getSelectedItem() );

					break;

				case FILTERBYWORDCLASSFIELD	:
					filterByWordClass	=
						filterByWordClassCheckBox.isSelected();

					break;

				case LEFTSPANFIELD				:
					leftSpan	=
						StringUtils.stringToInt( leftSpanField.getText() );

					if ( leftSpan < 0 )
					{
						new ErrorMessage
						(
							WordHoardSettings.getString
							(
								"Theleftspanmustbegreaterorequaltozero" ,
								"The left span must be greater or equal to zero"
							)
						);

						return;
					}

					break;

				case RIGHTSPANFIELD				:
					rightSpan	=
						StringUtils.stringToInt( rightSpanField.getText() );

					if ( rightSpan < 0 )
					{
						new ErrorMessage
						(
							WordHoardSettings.getString
							(
								"Therightspanmustbegreaterorequaltozero" ,
								"The right span must be greater or equal to zero"
							)
						);

						return;
					}

					break;

				case CUTOFFFIELD				:
					cutoff		=
						StringUtils.stringToInt( cutoffField.getText() );

					if ( cutoff < 0 )
					{
						new ErrorMessage
						(
							WordHoardSettings.getString
							(
								"Thecutoffmustbegreaterorequaltozero" ,
								"The cutoff must be greater or equal to zero"
							)
						);

						return;
					}

					break;

				case ANALYSISTEXTFIELD			:
					analysisText	=
						(WordCounter)analysisTextChoices.getSelectedWordCounter();

					if ( analysisText == null )
					{
						new ErrorMessage
						(
							WordHoardSettings.getString
							(
								"Youmustselectacorpusworkworksetorwordsetforanalysis" ,
								"You must select a corpus, work, work set, or word set to analyze"
							)
						);

						return;
					}
								//	For multiword unit analysis, only allow
								//	work sets which contain only works
								//	and not work parts.

					if	(	( dialogType == FINDMULTIWORDUNITS ) &&
							( analysisText.isWorkSet() ) )
					{
						if	(	!WorkSetUtils.containsOnlyWorks(
									(WorkSet)analysisText.getObject() ) )
						{
							new ErrorMessage
							(
								WordHoardSettings.getString
								(
									"Youmayonlyselectaworksetcomprisedsolelyofworks" ,
									"You may only select a work set comprised solely of works"
								)
							);

							return;
						}
					}

					break;

				case REFERENCETEXTFIELD			:
					referenceText	=
						(WordCounter)referenceTextChoices.getSelectedWordCounter();

					if ( referenceText == null )
					{
						new ErrorMessage
						(
							WordHoardSettings.getString
							(
								"Youmustselectacorpusworkworksetorwordsetforreference" ,
								"You must select a corpus, work, work set, or word set as a reference"
							)
						);

						return;
					}

					break;

				case PERCENTREPORTMETHODFIELD	:
					percentReportMethod	=
						getPercentReportMethod(
							(String)percentReportMethodChoices.getSelectedItem() );

					break;

				case MARKSIGLOGLIKELIHOODFIELD	:
					markSignificantLogLikelihoodValues	=
						markSignificantLogLikelihoodValuesField.isSelected();

					break;

				case ADJUSTCHISQUAREFORMULTIPLECOMPSFIELD	:
					adjustChiSquareForMultipleComparisons	=
						adjustChiSquareForMultipleComparisonsCheckBox.isSelected();

					break;

				case MINIMUMCOUNTFIELD			:
					minimumCount	=
						StringUtils.stringToInt(
							minimumCountField.getText() , 0 );

					if ( minimumCount < 0 )
					{
						new ErrorMessage
						(
							WordHoardSettings.getString
							(
								"Theminimumcountmustbegreaterorequaltozero" ,
								"The minimum word count must be greater or equal to zero"
							)
						);

						return;
					}

                    break;

				case MINIMUMWORKCOUNTFIELD		:
					minimumWorkCount	=
						StringUtils.stringToInt(
							minimumWorkCountField.getText() , 0 );

					if ( minimumWorkCount < 0 )
					{
						new ErrorMessage
						(
							WordHoardSettings.getString
							(
								"Theminimumworkcountmustbegreaterorequaltozero" ,
								"The minimum work count must be greater or equal to zero"
							)
						);

						return;
					}

					break;

				case FREQUENCYNORMALIZATIONFIELD:
					frequencyNormalizationMethod	=
						getFrequencyNormalization(
							(String)frequencyNormalizationMethodChoices.getSelectedItem() );

					break;

				case ASSOCIATIONMEASUREFIELD:
					associationMeasure	=
						getAssociationMeasure(
							(String)associationMeasureChoices.getSelectedItem() );

					break;

				case ROUNDNORMALIZEDCOUNTSFIELD	:
					roundNormalizedFrequencies	=
						roundNormalizedFrequenciesCheckBox.isSelected();

					break;

				case FILTEROUTPROPERNAMESFIELD	:
					filterOutProperNames	=
						filterOutProperNamesField.isSelected();

					break;

				case SHOWWORDCLASSESFIELD	:
					showWordClasses	=
						showWordClassesField.isSelected();

					break;

				case COMPRESSVALUERANGEINTEXTCLOUDS	:
					compressValueRangeInTagClouds	=
						compressValueRangeInTagCloudsCheckBox.isSelected();

					break;

				case SHOWPHRASECOUNTSFIELD :
					showPhraseFrequencies	=
						showPhraseFrequenciesCheckBox.isSelected();

					if	(	showPhraseFrequenciesCheckBox.isEnabled() &&
							showPhraseFrequencies )
					{
 						boolean okWordForm;

						switch ( wordForm )
						{
							case WordForms.SPELLING :
							case WordForms.LEMMA	:
							case WordForms.WORDCLASS		:
								okWordForm	= true;
                                break;

							default 				:
								okWordForm	= false;
						}

						if	( 	( analysisText != null ) &&
								analysisText.isPhraseSet() &&
                            	!okWordForm )
						{
							new ErrorMessage
							(
								WordHoardSettings.getString
								(
									"Youmayonlyselectspellinglemmapos" ,
									"You may only select Spelling, Lemma, or " +
									"Word Class for the word form when " +
									"analyzing phrases."
								)
							);

							return;
                		}
					}

					break;

				case ANALYZEPHRASECOUNTSFIELD :
					analyzePhraseFrequencies	=
						analyzePhraseFrequenciesCheckBox.isSelected();
					break;

				case ANALYSISTEXTBREAKDOWNBYFIELD	:
					if ( analysisTextAggregateRadioButton.isSelected() )
					{
						analysisTextBreakdownBy	= AGGREGATEALLWORKPARTS;
					}
					else if ( analysisTextBreakdownByWorkPartRadioButton.isSelected() )
					{
						analysisTextBreakdownBy	= BREAKDOWNBYWORKPARTS;
					}
					else
					{
						analysisTextBreakdownBy	= BREAKDOWNBYWORKS;
					}

					break;

				case REFERENCETEXTBREAKDOWNBYFIELD	:
					if ( referenceTextAggregateRadioButton.isSelected() )
					{
						referenceTextBreakdownBy	= AGGREGATEALLWORKPARTS;
					}
					else if ( referenceTextBreakdownByWorkPartRadioButton.isSelected() )
					{
						referenceTextBreakdownBy	= BREAKDOWNBYWORKPARTS;
					}
					else
					{
						referenceTextBreakdownBy	= BREAKDOWNBYWORKS;
					}
					break;

				case FILTERSINGLEOCCURRENCESFIELD 	:
					filterSingleOccurrences	=
						filterSingleOccurrencesCheckBox.isSelected();
					break;

				case FILTERBIGRAMSBYWORDCLASSFIELD 	:
					filterBigramsByWordClass	=
						filterBigramsByWordClassCheckBox.isSelected();
					break;

				case FILTERTRIGRAMSBYWORDCLASSFIELD :
					filterTrigramsByWordClass	=
						filterTrigramsByWordClassCheckBox.isSelected();
					break;

				case FILTERMULTIWORDUNITSCONTAININGVERBSFIELD :
					filterMultiwordUnitsContainingVerbs	=
						filterMultiwordUnitsContainingVerbsCheckBox.isSelected();
					break;

				case FILTERUSINGLOCALMAXSFIELD		:
					filterUsingLocalMaxs	=
						filterUsingLocalMaxsCheckBox.isSelected();
					break;

				case IGNORECASEANDDIACRITICALMARKSFIELD	:
					ignoreCaseAndDiacriticalMarks	=
						ignoreCaseAndDiacriticalMarksCheckBox.isSelected();
					break;

				case MAXIMUMMULTIWORDUNITLENGTHFIELD	:
					maximumMultiwordUnitLength	=
						StringUtils.stringToInt(
							maximumMultiwordUnitLengthField.getText() , 0 );

					if ( maximumMultiwordUnitLength > 10 )
					{
						new ErrorMessage
						(
							WordHoardSettings.getString
							(
								"Themaximummultiwordunitlengthmustbelessthanten" ,
								"The maximum multiword unit length must be less or equal to ten"
							)
						);

						return;
					}

					if ( maximumMultiwordUnitLength < minimumMultiwordUnitLength )
					{
						new ErrorMessage
						(
							WordHoardSettings.getString
							(
								"Themaximummultiwordunitlengthmustbegreaterthantheminimummultiwordunitlength" ,
								"The maximum multiword unit length must be less or equal to ten"
							)
						);

						return;
					}

					break;

				case MINIMUMMULTIWORDUNITLENGTHFIELD	:
					minimumMultiwordUnitLength	=
						StringUtils.stringToInt(
							minimumMultiwordUnitLengthField.getText() , 0 );

					if ( minimumMultiwordUnitLength < 2 )
					{
						new ErrorMessage
						(
							WordHoardSettings.getString
							(
								"Theminimummultiwordunitlengthmustbegreaterorequaltotwo" ,
								"The minimum multiword unit length must be greater or equal to two"
							)
						);

						return;
					}

					break;

			}
		}
								//	Handle any dialog-specific consistency
								//	checks between dialog fields.

		if ( dialogType == MULTIPLEWORDFORMCOMPARISON )
		{
			int workCount	=
				analysisText.getWorkCount( referenceText );

			if ( minimumWorkCount > workCount )
			{
				PrintfFormat errorFormat	=
					new PrintfFormat
					(
						WordHoardSettings.getString
						(
							( workCount == 1 ) ?
								"singularYousettheminimumworkcount" :
								"pluralYousettheminimumworkcount" ,
								"You set the minimum work count to %i, but " +
								"there are only %i works in the combined " +
								"analysis and reference texts."
						)
					);

				new ErrorMessage
				(
					errorFormat.sprintf
					(
						new Object[]
						{
							Integer.valueOf( minimumWorkCount ) ,
							Integer.valueOf( workCount )
						}
					)
				);

				return;
			}
/*
			if	(	( minimumWorkCount > 1 ) &&
					( analysisText.equals( referenceText ) ) )
			{
				PrintfFormat errorFormat	=
					new PrintfFormat
					(
						WordHoardSettings.getString
						(
							"Whentheanalysisandreferencearethesame" ,
							"When the analysis and reference texts " +
							"are the same, the minimum work count " +
							"must be greater than one."
						)
					);

				new ErrorMessage
				(
					errorFormat.sprintf
					(
						new Object[]
						{
							getWordFormString( wordForm )
						}
					)
				);

				return;
			}
*/
		}
		             			//	Validate word field.

		if ( wordFieldUsed )
		{
			if ( !validateWordField() ) return;
		}
								//	If all fields validated OK, end the
								//	dialog.
		cancelled	= false;
		dispose();
	}

	/**	Get word form.
	 *
	 *	@param		wordFormString	Word form string.
	 *
	 *	@return						The word form.
	 */

	public int getWordForm( String wordFormString )
	{
		int result	= WordForms.SPELLING;

		if ( wordFormString.equals(
			getWordFormString( WordForms.SPELLING ) ) )
		{
			result	= WordForms.SPELLING;
		}
		else if ( wordFormString.equals(
			getWordFormString( WordForms.LEMMA ) ) )
		{
			result	= WordForms.LEMMA;
		}
		else if ( wordFormString.equals(
			getWordFormString( WordForms.WORDCLASS ) ) )
		{
			result	= WordForms.WORDCLASS;
		}
		else if ( wordFormString.equals(
			getWordFormString( WordForms.SPEAKERGENDER ) ) )
		{
			result	= WordForms.SPEAKERGENDER;
		}
		else if ( wordFormString.equals(
			getWordFormString( WordForms.SPEAKERMORTALITY ) ) )
		{
			result	= WordForms.SPEAKERMORTALITY;
		}
		else if ( wordFormString.equals(
			getWordFormString( WordForms.SEMANTICCATEGORY ) ) )
		{
			result	= WordForms.SEMANTICCATEGORY;
		}
		else if ( wordFormString.equals(
			getWordFormString( WordForms.ISVERSE ) ) )
		{
			result	= WordForms.ISVERSE;
		}
		else if ( wordFormString.equals(
			getWordFormString( WordForms.METRICALSHAPE ) ) )
		{
			result	= WordForms.METRICALSHAPE;
		}

		return result;
	}

	/**	Get percent report method from string.
	 *
	 *	@param		percentReportMethodString	Percent report method string.
	 *
	 *	@return									The percent reporting method.
	 */

	public int getPercentReportMethod
	(
		String percentReportMethodString
	)
	{
		int result	= REPORTPERCENTS;

		if ( percentReportMethodString.equals(
			getPercentReportMethodString( REPORTPERCENTS ) ) )
		{
			result	= REPORTPERCENTS;
		}
		else if ( percentReportMethodString.equals(
			getPercentReportMethodString( REPORTPARTSPERMILLION ) ) )
		{
			result	= REPORTPARTSPERMILLION;
		}
		else if ( percentReportMethodString.equals(
			getPercentReportMethodString( REPORTPARTSPER10000 ) ) )
		{
			result	= REPORTPARTSPER10000;
		}

		return result;
	}

	/**	Get percent report method string.
	 *
	 *	@param		percentReportMethod		The percent reporting method.
	 *
	 *	@return								The percent reporting method
	 *										as a string.
	 */

	public String getPercentReportMethodString
	(
		int percentReportMethod
	)
	{
		String	result	= "";

		switch ( percentReportMethod )
		{
			case REPORTPERCENTS			:
				result	=
					WordHoardSettings.getString
					(
						"ReportPercents" ,
						"Percents"
					);
				break;

			case REPORTPARTSPERMILLION	:
				result	=
					WordHoardSettings.getString
					(
						"ReportPartsPerMillion" ,
						"Rounded Parts Per Million"
					);
				break;

			case REPORTPARTSPER10000	:
				result	=
					WordHoardSettings.getString
					(
						"ReportPartsPerTenThousand" ,
						"Parts Per Ten Thousand"
					);
				break;
		}

		return result;
	}

	/**	Get word form string.
	 *
	 *	@param	wordForm	The word form type.
	 *
	 *	@return		The word form string.
	 */

	public String getWordFormString( int wordForm )
	{
		String	result	= "";

		switch ( wordForm )
		{
			case WordForms.LEMMA			:
				result	=
					WordHoardSettings.getString(
						"Lemma" , "Lemma" );
				break;

			case WordForms.SPELLING		:
				result	=
					WordHoardSettings.getString(
						"Spelling" , "Spelling" );
				break;

			case WordForms.WORDCLASS			:
				result	=
					WordHoardSettings.getString(
						"WordClass" , "Word Class" );
				break;

			case WordForms.SPEAKERGENDER	:
				result	=
					WordHoardSettings.getString(
						"SpeakerGender" , "Speaker Gender" );
				break;

			case WordForms.SPEAKERMORTALITY	:
				result	=
					WordHoardSettings.getString(
						"SpeakerMortality" , "Speaker Mortality" );
				break;

			case WordForms.SEMANTICCATEGORY	:
				result	=
					WordHoardSettings.getString(
						"SemanticTag" , "Semantic Tag" );
				break;

			case WordForms.ISVERSE			:
				result	=
					WordHoardSettings.getString(
						"Verse" , "Verse" );
				break;

			case WordForms.METRICALSHAPE	:
				result	=
					WordHoardSettings.getString(
						"MetricalShape" , "Metrical Shape" );
				break;
		}

		return result;
	}

	/**	Get plural word form string.
	 *
	 *	@param	wordForm	The word form type.
	 *
	 *	@return		The plural word form string.
	 */

	public String getPluralWordFormString( int wordForm )
	{
		String	result	= "";

		switch ( wordForm )
		{
			case WordForms.LEMMA			:
				result	=
					WordHoardSettings.getString(
						"pluralLemma" , "Lemmata" );
				break;

			case WordForms.SPELLING		:
				result	=
					WordHoardSettings.getString(
						"pluralSpelling" , "Spellings" );
				break;

			case WordForms.WORDCLASS			:
				result	=
					WordHoardSettings.getString(
						"pluralWordClass" , "Word Classes" );
				break;

			case WordForms.SPEAKERGENDER	:
				result	=
					WordHoardSettings.getString(
						"pluralSpeakerGender" , "Speaker Genders" );
				break;

			case WordForms.SPEAKERMORTALITY	:
				result	=
					WordHoardSettings.getString(
						"pluralSpeakerMortality" , "Speaker Mortalities" );
				break;


			case WordForms.SEMANTICCATEGORY	:
				result	=
					WordHoardSettings.getString(
						"pluralSemanticTags" , "Semantic Tags" );
				break;

			case WordForms.ISVERSE			:
				result	=
					WordHoardSettings.getString(
						"pluralVerse" , "Verses" );
				break;

			case WordForms.METRICALSHAPE    :
				result	=
					WordHoardSettings.getString(
						"pluralMetricalShape" , "Metrical Shapes" );
				break;
		}

		return result;
	}

	/**	Get frequency normalization from string.
	 *
	 *	@param		normalizationMethodString	Normalization method string.
	 *
	 *	@return									The normalization method.
	 */

	public int getFrequencyNormalization
	(
		String normalizationMethodString
	)
	{
		int result	= NORMALIZENONE;

		if ( normalizationMethodString.equals(
			getFrequencyNormalizationString( NORMALIZENONE ) ) )
		{
			result	= NORMALIZENONE;
		}
		else if ( normalizationMethodString.equals(
			getFrequencyNormalizationString( NORMALIZETO10000 ) ) )
		{
			result	= NORMALIZETO10000;
		}
		else if ( normalizationMethodString.equals(
			getFrequencyNormalizationString( NORMALIZETOMEANWORKLENGTH ) ) )
		{
			result	= NORMALIZETOMEANWORKLENGTH;
		}
		else if ( normalizationMethodString.equals(
			getFrequencyNormalizationString( NORMALIZETOPARTSPERMILLION ) ) )
		{
			result	= NORMALIZETOPARTSPERMILLION;
		}

		return result;
	}

	/**	Get frequency normalization string.
	 *
	 *	@param		frequencyNormalizationMethod	The normalization method.
	 *
	 *	@return										The normalization method
	 *												as a string.
	 */

	public String getFrequencyNormalizationString
	(
		int frequencyNormalizationMethod
	)
	{
		String	result	= "";

		switch ( frequencyNormalizationMethod )
		{
			case NORMALIZENONE 		:
				result	=
					WordHoardSettings.getString(
						"NoNormalization" , "No Normalization" );
				break;

			case NORMALIZETO10000	:
				result	=
					WordHoardSettings.getString(
						"NormalizeTo10000" ,
						"Normalize to counts per 10,000 words" );
				break;

			case NORMALIZETOMEANWORKLENGTH	:
				result	=
					WordHoardSettings.getString(
						"NormalizeMeanWorkLength" ,
						"Normalize to average word count in reference works" );
				break;

			case NORMALIZETOPARTSPERMILLION	:
				result	=
					WordHoardSettings.getString(
						"NormalizeToPartsPerMillion" ,
						"Normalize to parts per million" );
				break;
		}

		return result;
	}

	/**	Get string for text breakdown method.
	 *
	 *	@param	textBreakdownMethod		Text breakdown method.
	 *
	 *	@return							Displayable string for
	 *									text breakdown method.
	 */

	public static String getTextBreakdownMethod( int textBreakdownMethod )
	{
		String result	= "";

		switch ( textBreakdownMethod )
		{
			case AGGREGATEALLWORKPARTS	:
				result	=
					WordHoardSettings.getString
					(
						"aggregatedoverallworkparts" ,
						"aggregated over all work parts"
					);

				break;

			case BREAKDOWNBYWORKPARTS	:
				result	=
					WordHoardSettings.getString
					(
						"brokendownbyworkparts" ,
						"broken down by work parts"
					);
				break;

			default						:
				result	=
					WordHoardSettings.getString
					(
						"brokendownbyworks" ,
						"broken down by works"
					);
				break;
		}

		return result;
	}

	/**	Get association measure string.
	 *
	 *	@param		associationMeasure		The association measure.
	 *
	 *	@return								The association measure
	 *										as a string.
	 */

	public static String getAssociationMeasureString
	(
		int associationMeasure
	)
	{
		String	result	= "";

		switch ( associationMeasure )
		{
			case DICE		:
				result	=
					WordHoardSettings.getString(
						"Dice" , "Dice coefficient" );
				break;

			case LOGLIKE	:
				result	=
					WordHoardSettings.getString(
						"Loglike" , "Log-Likelihood" );
				break;

			case PHISQUARED	:
				result	=
					WordHoardSettings.getString(
						"PhiSquared" , "Phi Squared" );
				break;

			case SI			:
				result	=
					WordHoardSettings.getString(
						"SI" , "Specific Mutual Information" );
				break;

			case SCP		:
				result	=
					WordHoardSettings.getString(
						"SCP" , "Symmetric Conditional Probability" );
				break;
		}

		return result;
	}

	/**	Get association measure.
	 *
	 *	@param		associationMeasureString	The association measure string.
	 *
	 *	@return									The association measure index.
	 */

	public static int getAssociationMeasure
	(
		String associationMeasureString
	)
	{
		int	result	= LOGLIKE;

		if ( associationMeasureString.equals(
			getAssociationMeasureString( DICE ) ) )
		{
			result	= DICE;
		}
		else if ( associationMeasureString.equals(
			getAssociationMeasureString( PHISQUARED ) ) )
		{
			result	= PHISQUARED;
		}
		else if ( associationMeasureString.equals(
			getAssociationMeasureString( SI ) ) )
		{
			result	= SI;
		}
		else if ( associationMeasureString.equals(
			getAssociationMeasureString( SCP ) ) )
		{
			result	= SCP;
		}

		return result;
	}

	/**	Clean word text.
	 *
	 *	<p>
	 *	The "wordText" and "wordTextField" entries are cleaned as follows.
	 *	</p>
	 *
	 *	<ul>
	 *	<li>Leading and trailing whitespace is removed.</li>
	 *	<li>Runs of multiple blanks are compressed to a single blank.</li>
	 *	<li>A blank is inserted before the first left parenthesis
	 *		if necessary.</li>
	 *	<li>Greek Tonos characters are mapped to Oxia.</li>
	 *	</ul>
	 */

	protected void cleanWordText()
	{
								//	Trim whitespace from each end
								//	of word text, and remove
								//	duplicate blanks.

 		wordText			=
 			StringUtils.compressMultipleOccurrences(
 				wordText.trim() , ' ' );

								//	Make sure there is a blank before
								//	the first left parenthesis.

		int lParenPos		= wordText.indexOf( "(" );

		if ( lParenPos > 0 )
		{
			if ( wordText.charAt( lParenPos - 1 ) != ' ' )
			{
				wordText	=
					wordText.substring( 0 , lParenPos ) + " " +
					wordText.substring( lParenPos );
			}
		}
								//	Map Tonos to Oxia in Greek text.
		wordText			=
			CharsetUtils.translateTonosToOxia( wordText );

								//	Update input field with cleaned
								//	word text.

		wordTextField.setText( wordText );
	}

	/**	Get word to analyze.
	 *
	 *	@return		The word.
	 */

	public Spelling getWordText()
	{
								//	Return a Spelling object.  We
								//	determine the character set for the
								//	spelling based upon the characters
								//	in the string.
		cleanWordText();

		return WordUtils.getSpellingForString( wordText );
	}

	/**	Get word form.
	 *
	 *	@return		The word form.
	 */

	public int getWordForm()
	{
		return wordForm;
	}

	/**	Get left span value.
	 *
	 *	@return		The left span value.
	 */

	public int getLeftSpan()
	{
		return leftSpan;
	}

	/**	Get right span value.
	 *
	 *	@return		The right span value.
	 */

	public int getRightSpan()
	{
		return rightSpan;
	}

	/**	Get cutoff value.
	 *
	 *	@return		The cutoff value.
	 */

	public int getCutoff()
	{
		return cutoff;
	}

	/**	Get minimum count.
	 *
	 *	@return		The minimum count for which to display results.
	 */

	public int getMinimumCount()
	{
		return minimumCount;
	}

	/**	Get minimum work count.
	 *
	 *	@return		The minimum work count for which to display results.
	 */

	public int getMinimumWorkCount()
	{
		return minimumWorkCount;
	}

	/**	Get the frequency normalization method.
	 *
	 *	@return		The frequency normalization method.
	 */

	public int getFrequencyNormalizationMethod()
	{
		return frequencyNormalizationMethod;
	}

	/**	Get the round normalized frequencies option.
	 *
	 *	@return		true to round normalized frequencies.
	 */

	public boolean getRoundNormalizedFrequencies()
	{
		return roundNormalizedFrequencies;
	}

	/**	Get analysis corpus.
	 *
	 *	@return		The analysis text.
	 */

	public WordCounter getAnalysisText()
	{
		return analysisText;
	}

	/**	Get reference corpus.
	 *
	 *	@return		The reference text.
	 */

	public WordCounter getReferenceText()
	{
		return referenceText;
	}

	/**	Get the percent reporting method.
	 *
	 *	@return		The percent reporting method.
	 */

	public int getPercentReportMethod()
	{
		return percentReportMethod;
	}

	/**	Get the mark significant log-likelihood values flag.
	 *
	 *	@return		True to mark significant log-likelihood values.
	 */

	public boolean getMarkSignificantLogLikelihoodValues()
	{
		return markSignificantLogLikelihoodValues;
	}

	/**	Get the filter out proper names flag.
	 *
	 *	@return		True to filter out probably proper names.
	 */

	public boolean getFilterOutProperNames()
	{
		return filterOutProperNames;
	}

	/**	Get the show all word classes flag.
	 *
	 *	@return		True to show word classes for all words.
	 */

	public boolean getShowWordClasses()
	{
		return showWordClasses;
	}

	/**	Get the show phrase frequencies flag.
	 *
	 *	@return		True to show phrase frequencies.
	 */

	public boolean getShowPhraseFrequencies()
	{
		return showPhraseFrequencies;
	}

	/**	Get the compare phrase frequencies flag.
	 *
	 *	@return		True to compare phrase frequencies.
	 */

	public boolean getanalyzePhraseFrequencies()
	{
		return analyzePhraseFrequencies;
	}

	/**	Get the filter by word classes flag.
	 *
	 *	@return		True to filter collocates using word classes.
	 */

	public boolean getFilterByWordClass()
	{
		return filterByWordClass;
	}

	/**	Get the analysis text breakdown method.
	 *
	 *	@return		The analysis text breakdown method.
	 */

	public int getAnalysisTextBreakdownBy()
	{
		return analysisTextBreakdownBy;
	}

	/**	Get the reference text breakdown method.
	 *
	 *	@return		The reference text breakdown method.
	 */

	public int getReferenceTextBreakdownBy()
	{
		return referenceTextBreakdownBy;
	}

	/**	Get the association measure for localmaxs.
	 *
	 *	@return		The association measure for localmaxs.
	 */

	public static int getAssociationMeasure()
	{
		return associationMeasure;
	}

	/**	Get the cloud measure for multiword unit and collocate analysis.
	 *
	 *	@return		The cloud measure.
	 */

	public static int getCloudMeasure()
	{
		return cloudMeasure;
	}

	/**	Set the cloud measure for multiword unit and collocate analysis.
	 *
	 *	@param	theCloudMeasure	The cloud measure.
	 */

	public static void setCloudMeasure( int theCloudMeasure )
	{
		cloudMeasure	= theCloudMeasure;
	}

	/**	Set the cloud measure for multiword unit analysis.
	 *
	 *	@param	cloudMeasureString	The cloud measure for multiword unit
	 *								analysis.
	 */

	public static void setCloudMeasure( String cloudMeasureString )
	{
		cloudMeasure	= getAssociationMeasure( cloudMeasureString );
	}

	/**	Get the minimum multiword unit length.
	 *
	 *	@return		The minimum multiword unit length.
	 */

	public int getMinimumMultiwordUnitLength()
	{
		return minimumMultiwordUnitLength;
	}

	/**	Get the maximum multiword unit length.
	 *
	 *	@return		The maximum multiword unit length.
	 */

	public int getMaximumMultiwordUnitLength()
	{
		return maximumMultiwordUnitLength;
	}

	/**	Get the filter bigrams by word class flag.
	 *
	 *	@return		true to filter bigrams by word class.
	 */

	public boolean getFilterBigramsByWordClass()
	{
		return filterBigramsByWordClass;
	}

	/**	Get the filter trigrams by word class flag.
	 *
	 *	@return		true to filter trigrams by word class.
	 */

	public boolean getFilterTrigramsByWordClass()
	{
		return filterTrigramsByWordClass;
	}

	/**	Get the filter ngrams using localmaxs.
	 *
	 *	@return		true to filter ngrams using localmaxs.
	 */

	public boolean getFilterUsingLocalMaxs()
	{
		return filterUsingLocalMaxs;
	}

	/**	Get the filter multiword units containing verbs flag.
	 *
	 *	@return		true to filter multiword units containing verbs.
	 */

	public boolean getFilterMultiwordUnitsContainingVerbs()
	{
		return filterMultiwordUnitsContainingVerbs;
	}

	/**	Get the filter ngrams which only only once flag.
	 *
	 *	@return		true to filter ngrams which occur only once.
	 */

	public boolean getFilterSingleOccurrences()
	{
		return filterSingleOccurrences;
	}

	/**	Get ignore case and diacritical marks flag.
	 *
	 *	@return		true to ignore case and diacritical marks.

	 */

	public boolean getIgnoreCaseAndDiacriticalMarks()
	{
		return ignoreCaseAndDiacriticalMarks;
	}

	/**	Get adjust chi-square for multiple comparisons flag.
	 *
	 *	@return		true to adjust chi-square p values for multiple
	 *				comparisons.
	 */

	public boolean getAdjustChiSquareForMultipleComparisons()
	{
		return adjustChiSquareForMultipleComparisons;
	}

	/**	Get use short work titles in dialogs flag.
	 *
	 *	@return		true to use short work titles in dialogs.
	 */

	public static boolean getUseShortWorkTitlesInDialogs()
	{
		return useShortWorkTitlesInDialogs;
	}

	/**	Get use short work titles in headers flag.
	 *
	 *	@return		true to use short work titles in headers.
	 */

	public static boolean getUseShortWorkTitlesInHeaders()
	{
		return useShortWorkTitlesInHeaders;
	}

	/**	Get use short work titles in window titles flag.
	 *
	 *	@return		true to use short work titles in window titles.
	 */

	public static boolean getUseShortWorkTitlesInWindowTitles()
	{
		return useShortWorkTitlesInWindowTitles;
	}

	/**	Get use short work titles in output flag.
	 *
	 *	@return		true to use short work titles in output tables.
	 */

	public static boolean getUseShortWorkTitlesInOutput()
	{
		return useShortWorkTitlesInOutput;
	}

	/**	Get compress value range in word clouds flag.
	 *
	 *	@return		true to compress value range in word clouds.
	 */

	public static boolean getCompressValueRangeInTagClouds()
	{
		return compressValueRangeInTagClouds;
	}

	/**	Set compress value range in word clouds flag.
	 *
	 *	@param	doCompression	true to compress value range in word clouds.
	 */

	public static void setCompressValueRangeInTagClouds
	(
		boolean doCompression
	)
	{
		compressValueRangeInTagClouds	= doCompression;
	}

	/**	Get dialog preferences.
	 * @return	The preferences.
	 */

	public static Properties savePreferences()
	{
		Properties preferences	= new Properties();

		preferences.setProperty
		(
			"wordText" ,
			wordText
		);

		preferences.setProperty
		(
			"wordForm" ,
			wordForm + ""
		);

		preferences.setProperty
		(
			"filterByWordClass" ,
			filterByWordClass ? "1" : "0"
		);

		preferences.setProperty
		(
			"leftSpan" ,
			leftSpan + ""
		);

		preferences.setProperty
		(
			"rightSpan" ,
			rightSpan + ""
		);

		preferences.setProperty
		(
			"cutoff" ,
			cutoff + ""
		);

		if ( analysisText != null )
		{
			try
			{
				preferences.setProperty
				(
					"analysisTextType" ,
					analysisText.getObjectType() + ""
				);
    		}
    		catch ( Exception e )
    		{
				preferences.setProperty
				(
					"analysisTextType" ,
					""
				);
    		}

			try
			{
				if ( analysisText instanceof HasTag )
				{
					preferences.setProperty
					(
						"analysisTextTag" ,
						analysisText.getTag()
					);
				}
				else
				{
					preferences.setProperty
					(
						"analysisTextTag" ,
						""
					);
				}
			}
			catch ( Exception e )
			{
				preferences.setProperty
				(
					"analysisTextTag" ,
					""
				);
			}

    		try
    		{
				preferences.setProperty
				(
					"analysisTextID" ,
					analysisText.getObjectId() + ""
				);
			}
			catch ( Exception e )
			{
				preferences.setProperty
				(
					"analysisTextID" ,
					""
				);
			}
		}

		preferences.setProperty
		(
			"analysisTextBreakdownBy" ,
			analysisTextBreakdownBy + ""
		);

		preferences.setProperty
		(
			"referenceTextBreakdownBy" ,
			referenceTextBreakdownBy + ""
		);

		if ( referenceText != null )
		{
			try
			{
				preferences.setProperty
				(
					"referenceTextType" ,
					referenceText.getObjectType() + ""
				);
			}
			catch ( Exception e )
			{
				preferences.setProperty
				(
					"referenceTextType" ,
					""
				);
			}

			try
			{
				if ( referenceText instanceof HasTag )
				{
					preferences.setProperty
					(
						"referenceTextTag" ,
						referenceText.getTag()
					);
				}
				else
				{
					preferences.setProperty
					(
						"referenceTextTag" ,
						""
					);
				}
			}
			catch ( Exception e )
			{
				preferences.setProperty
				(
					"referenceTextTag" ,
					""
				);
			}

    		try
    		{
				preferences.setProperty
				(
					"referenceTextID" ,
					referenceText.getObjectId() + ""
				);
			}
			catch ( Exception e )
			{
				preferences.setProperty
				(
					"referenceTextID" ,
					""
				);
			}
		}

		preferences.setProperty
		(
			"minimumCount" ,
			minimumCount + ""
		);

		preferences.setProperty
		(
			"minimumWorkCount" ,
			minimumWorkCount + ""
		);

		preferences.setProperty
		(
			"frequencyNormalizationMethod" ,
			frequencyNormalizationMethod + ""
		);

		preferences.setProperty
		(
			"roundNormalizedFrequencies" ,
			roundNormalizedFrequencies ? "1" : "0"
		);

		preferences.setProperty
		(
			"analyzePhraseFrequencies" ,
			analyzePhraseFrequencies ? "1" : "0"
		);

		preferences.setProperty
		(
			"showPhraseFrequencies" ,
			showPhraseFrequencies ? "1" : "0"
		);

		preferences.setProperty
		(
			"percentReportMethod" ,
			percentReportMethod + ""
		);

		preferences.setProperty
		(
			"filterOutProperNames" ,
			filterOutProperNames ? "1" : "0"
		);

		preferences.setProperty
		(
			"showWordClasses" ,
			showWordClasses ? "1" : "0"
		);

		preferences.setProperty
		(
			"associationMeasure" ,
			associationMeasure + ""
		);

		preferences.setProperty
		(
			"cloudMeasure" ,
			cloudMeasure + ""
		);

		preferences.setProperty
		(
			"minimumMultiwordUnitLength" ,
			minimumMultiwordUnitLength + ""
		);

		preferences.setProperty
		(
			"maximumMultiwordUnitLength" ,
			maximumMultiwordUnitLength + ""
		);

		preferences.setProperty
		(
			"filterBigramsByWordClass" ,
			filterBigramsByWordClass ? "1" : "0"
		);

		preferences.setProperty
		(
			"filterTrigramsByWordClass" ,
			filterTrigramsByWordClass ? "1" : "0"
		);

		preferences.setProperty
		(
			"filterUsingLocalMaxs" ,
			filterUsingLocalMaxs ? "1" : "0"
		);

		preferences.setProperty
		(
			"filterSingleOccurrences" ,
			filterSingleOccurrences ? "1" : "0"
		);

		preferences.setProperty
		(
			"ignoreCaseAndDiacriticalMarks" ,
			ignoreCaseAndDiacriticalMarks ? "1" : "0"
		);

		preferences.setProperty
		(
			"filterMultiwordUnitsContainingVerbs" ,
			filterMultiwordUnitsContainingVerbs ? "1" : "0"
		);

		preferences.setProperty
		(
			"adjustChiSquareForMultipleComparisons" ,
			adjustChiSquareForMultipleComparisons ? "1" : "0"
		);

		preferences.setProperty
		(
			"useShortWorkTitlesInDialogs" ,
			useShortWorkTitlesInDialogs ? "1" : "0"
		);

		preferences.setProperty
		(
			"useShortWorkTitlesInOutput" ,
			useShortWorkTitlesInOutput ? "1" : "0"
		);

		preferences.setProperty
		(
			"useShortWorkTitlesInWindowTitles" ,
			useShortWorkTitlesInWindowTitles ? "1" : "0"
		);

		preferences.setProperty
		(
			"useShortWorkTitlesInHeaders" ,
			useShortWorkTitlesInHeaders ? "1" : "0"
		);

		preferences.setProperty
		(
			"compressValueRangeInTagClouds" ,
			compressValueRangeInTagClouds ? "1" : "0"
		);

		return preferences;
	}

	/**	Load a property string.
	 *
	 *	@param	properties		The properties.
	 *	@param	propertyName	The property name to load.
	 *
	 *	@return					The property values.
	 *							Null values are converted to empty strings.
	 */

	protected static String getProperty
	(
		Properties properties ,
		String propertyName
	)
	{
		return StringUtils.safeString(
			properties.getProperty( propertyName ) );
	}

	/**	Load dialog preferences.
	 * @param	preferences	The preference properties to be loaded.
	 */

	public static void loadPreferences( Properties preferences )
	{
		if ( preferences == null ) return;

		wordText	=
			getProperty( preferences , "wordText" );

		wordForm	=
			StringUtils.stringToInt
			(
				getProperty( preferences , "wordForm" ),
				WordForms.SPELLING
			);

		filterByWordClass	=
			getProperty( preferences , "filterByWordClass" ).equals( "1" );

		leftSpan	=
			StringUtils.stringToInt
			(
				getProperty( preferences , "leftSpan" ),
				DEFAULTLEFTSPAN
			);

		rightSpan	=
			StringUtils.stringToInt
			(
				getProperty( preferences , "rightSpan" ),
				DEFAULTRIGHTSPAN
			);

		cutoff		=
			StringUtils.stringToInt
			(
				getProperty( preferences , "cutoff" ),
				DEFAULTCUTOFF
			);

// $$$PIB$$$ Needs fixing when user objects moved to server.

        analysisText    = null;

		String analysisTextType	=
			getProperty( preferences , "analysisTextType" );

		String analysisTextID	=
			getProperty( preferences , "analysisTextID" );

		String analysisTag		=
			getProperty( preferences , "analysisTag" );

		if ( analysisTextType.length() > 0 )
		{
		    if ( analysisTag.length() > 0 )
		    {
				analysisText	=
					new WordCounter
					(
						StringUtils.stringToInt(
							analysisTextType , -1 ) ,
						analysisTag
					);
		    }
		    else if ( analysisTextID.length() > 0 )
			{
				analysisText	=
					new WordCounter
					(
						StringUtils.stringToInt(
							analysisTextType , -1 ) ,
						Long.valueOf(
							StringUtils.stringToLong(
								analysisTextID , 0 ) )
					);
			}
		}

// $$$PIB$$$ Needs fixing when user objects moved to server.

        referenceText   = null;

		String referenceTextType	=
			getProperty( preferences ,  "referenceTextType" );

		String referenceTextID	=
			getProperty( preferences ,  "referenceTextID" );

		String referenceTag		=
			getProperty( preferences , "referenceTag" );

		if ( referenceTextType.length() > 0 )
		{
			if ( referenceTag.length() > 0 )
			{
				referenceText	=
					new WordCounter
					(
						StringUtils.stringToInt(
							referenceTextType , -1 ) ,
						referenceTag
					);
			}
			else if ( referenceTextID.length() > 0  )
			{
				referenceText	=
					new WordCounter
					(
						StringUtils.stringToInt(
							referenceTextType , -1 ) ,
						Long.valueOf(
							StringUtils.stringToLong(
								referenceTextID , 0 ) )
					);
			}
		}

		analysisTextBreakdownBy	=
			StringUtils.stringToInt
			(
				getProperty( preferences , "analysisTextBreakdownBy" ) ,
				DEFAULTTEXTBREAKDOWNBY
			);

		referenceTextBreakdownBy	=
			StringUtils.stringToInt
			(
				getProperty( preferences , "referenceTextBreakdownBy" ) ,
				DEFAULTTEXTBREAKDOWNBY
			);

		minimumCount	=
			StringUtils.stringToInt
			(
				getProperty( preferences , "minimumCount" ),
				DEFAULTMINIMUMCOUNT
			);

		minimumWorkCount	=
			StringUtils.stringToInt
			(
				getProperty( preferences , "minimumWorkCount" ),
				DEFAULTMINIMUMWORKCOUNT
			);

		frequencyNormalizationMethod	=
			StringUtils.stringToInt
			(
				getProperty( preferences , "frequencyNormalizationMethod" ) ,
				DEFAULTFREQUENCYNORMALIZATIONMETHOD
			);

		roundNormalizedFrequencies	=
			getProperty( preferences , "roundNormalizedFrequencies" ).equals( "1" );

		analyzePhraseFrequencies	=
			getProperty( preferences , "analyzePhraseFrequencies" ).equals( "1" );

		showPhraseFrequencies	=
			getProperty( preferences , "showPhraseFrequencies" ).equals( "1" );

		percentReportMethod 	=
			StringUtils.stringToInt
			(
				getProperty( preferences , "percentReportMethod" ) ,
				DEFAULTPERCENTREPORTMETHOD
			);

		filterOutProperNames	=
			getProperty
				( preferences , "filterOutProperNames" ).equals( "1" );

		showWordClasses		=
			getProperty( preferences , "showWordClasses" ).equals( "1" );

		associationMeasure	=
			StringUtils.stringToInt
			(
				getProperty
				(
					preferences ,
					"associationMeasure"
				)  ,
				DEFAULTASSOCIATIONMEASURE
			);

		cloudMeasure	=
			StringUtils.stringToInt
			(
				getProperty
				(
					preferences ,
					"cloudMeasure"
				)  ,
				DEFAULTCLOUDMEASURE
			);

		minimumMultiwordUnitLength	=
			StringUtils.stringToInt
			(
				getProperty
				(
					preferences ,
					"minimumMultiwordUnitLength"
				) ,
				DEFAULTMINIMUMMULTIWORDUNITLENGTH
			);

		maximumMultiwordUnitLength	=
			StringUtils.stringToInt
			(
				getProperty
				(
					preferences ,
					"maximumMultiwordUnitLength"
				) ,
				DEFAULTMAXIMUMMULTIWORDUNITLENGTH
			);

		filterBigramsByWordClass	=
			getProperty
			(
				preferences ,
				"filterBigramsByWordClass"
			).equals( "1" );

		filterTrigramsByWordClass	=
			getProperty
			(
				preferences ,
				"filterTrigramsByWordClass"
			).equals( "1" );

		filterUsingLocalMaxs	=
			getProperty
			(
				preferences ,
				"filterUsingLocalMaxs"
			).equals( "1" );

		filterSingleOccurrences	=
			getProperty
			(
				preferences ,
				"filterSingleOccurrences"
			).equals( "1" );

		ignoreCaseAndDiacriticalMarks	=
			getProperty
			(
				preferences ,
				"ignoreCaseAndDiacriticalMarks"
			).equals( "1" );

		filterMultiwordUnitsContainingVerbs	=
			getProperty
			(
				preferences ,
				"filterMultiwordUnitsContainingVerbs"
			).equals( "1" );

		adjustChiSquareForMultipleComparisons	=
			getProperty
			(
				preferences ,
				"adjustChiSquareForMultipleComparisons"
			).equals( "1" );

		String sProperty	=
			getProperty( preferences , "useShortWorkTitlesInDialogs" );

		if ( sProperty.length() > 0 )
		{
			useShortWorkTitlesInDialogs	= sProperty.equals( "1" );
		}

		sProperty	=
			getProperty( preferences , "useShortWorkTitlesInOutput" );

		if ( sProperty.length() > 0 )
		{
			useShortWorkTitlesInOutput	= sProperty.equals( "1" );
		}

		sProperty	=
			getProperty( preferences , "useShortWorkTitlesInWindowTitles" );

		if ( sProperty.length() > 0 )
		{
			useShortWorkTitlesInWindowTitles	= sProperty.equals( "1" );
		}

		sProperty	=
			getProperty( preferences , "useShortWorkTitlesInHeaders" );

		if ( sProperty.length() > 0 )
		{
			useShortWorkTitlesInHeaders	= sProperty.equals( "1" );
		}

		sProperty	=
			getProperty( preferences , "compressValueRangeInTagClouds" );

		if ( sProperty.length() > 0 )
		{
			compressValueRangeInTagClouds	= sProperty.equals( "1" );
		}
	}

	/**	Static initializer.
	 *
	 *	<p>
	 *	Sets the indices of the dialog fields for each type of dialog.
	 *	</p>
	 */

	static
	{
		dialogFieldIndices.add
		(
			WORDFORMFREQUENCIES ,
			new int[]
			{
				WORDFORMFIELD ,
				ANALYSISTEXTFIELD
//				, SHOWPHRASECOUNTSFIELD ,
//				SHOWWORDCLASSESFIELD
			}
		);

		dialogFieldIndices.add
		(
			MULTIPLEWORDFORMCOMPARISON ,
			new int[]
			{
				WORDFORMFIELD ,
				ANALYSISTEXTFIELD ,
				REFERENCETEXTFIELD ,
				MINIMUMCOUNTFIELD ,
				MINIMUMWORKCOUNTFIELD ,
				PERCENTREPORTMETHODFIELD ,
				MARKSIGLOGLIKELIHOODFIELD ,
				ADJUSTCHISQUAREFORMULTIPLECOMPSFIELD ,
				FILTEROUTPROPERNAMESFIELD ,
				SHOWWORDCLASSESFIELD
//				, COMPRESSVALUERANGEINTEXTCLOUDS
			}
		);

		dialogFieldIndices.add
		(
			SINGLEWORDFORMCOMPARISON ,
			new int[]
			{
				WORDFIELD ,
				WORDFORMFIELD ,
				ANALYSISTEXTFIELD ,
				REFERENCETEXTFIELD ,
                PERCENTREPORTMETHODFIELD ,
				MARKSIGLOGLIKELIHOODFIELD ,
//				ADJUSTCHISQUAREFORMULTIPLECOMPSFIELD ,
				SHOWWORDCLASSESFIELD
			}
		);

		dialogFieldIndices.add
		(
			SINGLEWORDFORMHISTORY ,
			new int[]
			{
				WORDFIELD ,
				WORDFORMFIELD ,
				ANALYSISTEXTFIELD ,
//				ANALYZEPHRASECOUNTSFIELD ,
				FREQUENCYNORMALIZATIONFIELD ,
				ROUNDNORMALIZEDCOUNTSFIELD
			}
		);

		dialogFieldIndices.add
		(
			COLLOCATEFREQUENCIES ,
			new int[]
			{
			}
		);

		dialogFieldIndices.add
		(
			FINDCOLLOCATES ,
			new int[]
			{
				WORDFIELD ,
				WORDFORMFIELD ,
				LEFTSPANFIELD ,
				RIGHTSPANFIELD ,
				CUTOFFFIELD ,
				ANALYSISTEXTFIELD ,
//				PERCENTREPORTMETHODFIELD ,
				MARKSIGLOGLIKELIHOODFIELD ,
				ADJUSTCHISQUAREFORMULTIPLECOMPSFIELD ,
				SHOWWORDCLASSESFIELD
//				, COMPRESSVALUERANGEINTEXTCLOUDS
			}
		);

		dialogFieldIndices.add
		(
			COMPARESINGLECOLLOCATEFREQUENCIES ,
			new int[]
			{
				WORDFIELD ,
				WORDFORMFIELD ,
				LEFTSPANFIELD ,
				RIGHTSPANFIELD ,
				CUTOFFFIELD ,
				ANALYSISTEXTFIELD ,
				REFERENCETEXTFIELD ,
				PERCENTREPORTMETHODFIELD ,
				MARKSIGLOGLIKELIHOODFIELD ,
				ADJUSTCHISQUAREFORMULTIPLECOMPSFIELD ,
				SHOWWORDCLASSESFIELD
//				, COMPRESSVALUERANGEINTEXTCLOUDS
			}
		);

		dialogFieldIndices.add
		(
			SINGLECOLLOCATEHISTORY ,
			new int[]
			{
			}
		);

		dialogFieldIndices.add
		(
			COMPARETEXTS ,
			new int[]
			{
				WORDFORMFIELD ,
				ANALYSISTEXTFIELD ,
				ANALYSISTEXTBREAKDOWNBYFIELD ,
				REFERENCETEXTFIELD ,
				REFERENCETEXTBREAKDOWNBYFIELD
			}
		);

		dialogFieldIndices.add
		(
			FINDMULTIWORDUNITS ,
			new int[]
			{
				WORDFORMFIELD ,
				ANALYSISTEXTFIELD ,
				MINIMUMMULTIWORDUNITLENGTHFIELD	,
				MAXIMUMMULTIWORDUNITLENGTHFIELD	,
				SHOWWORDCLASSESFIELD ,
				IGNORECASEANDDIACRITICALMARKSFIELD ,
				MARKSIGLOGLIKELIHOODFIELD ,
				ADJUSTCHISQUAREFORMULTIPLECOMPSFIELD ,
				FILTERSINGLEOCCURRENCESFIELD ,
				FILTERBIGRAMSBYWORDCLASSFIELD ,
				FILTERTRIGRAMSBYWORDCLASSFIELD ,
				FILTERMULTIWORDUNITSCONTAININGVERBSFIELD ,
				FILTERUSINGLOCALMAXSFIELD ,
				ASSOCIATIONMEASUREFIELD
//				, COMPRESSVALUERANGEINTEXTCLOUDS
			}
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


