package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import java.util.*;

import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	Displays word frequencies for selected works.
 */

public class WordFrequencies
	extends FrequencyAnalysisRunnerBase
	implements AnalysisRunner
{
	/**	Column containing word classes in output table.
	 */

	protected static final int WORDCLASSCOLUMN	= 1;

	/**	Column containing major word classes in output table.
	 */

	protected static final int MAJORWORDCLASSCOLUMN	= 2;

    /**	Create a word form frequency object.
     */

	public WordFrequencies()
	{
		super
		(
			AnalysisDialog.WORDFORMFREQUENCIES
		);
	}

	/**	Run frequencies analysis.
	 */

	public void runAnalysis
	(
		JFrame parentWindow ,
		ProgressReporter progressReporter
	)
	{
//		this.parentWindow		= parentWindow;
		this.progressReporter	= progressReporter;

								//	Reset progress dialog.

		if ( progressReporter != null )
		{
			progressReporter.setIndeterminate( true );
			progressReporter.updateProgress
			(
				0 ,
				WordHoardSettings.getString
				(
					"Computingfrequenciesinanalysistext" ,
					"Computing frequencies in analysis text"
				)
			);
		}
								//	Get word forms and associated counts.
		Map[] counts;

		if ( analysisText == null )
		{
			resultsPanel	= null;
			return;
		}

		if ( analysisText.isPhraseSet() && showPhraseFrequencies )
		{
			counts	=
				analysisText.getPhrasesAndCounts( wordForm );
		}
		else
		{
			counts	=
				analysisText.getWordsAndCounts( wordForm );
        }

		Map wordsAndCounts	= counts[ 0 ];

								//	Get number of works in which each
								//	word form appears.

		Map workCounts		= counts[ 1 ];

								//	Get total words in analysis and
								//	reference.

		int analysisTotalCount	=
			(int)CountMapUtils.getSummaryCountsFromCountMap(
				wordsAndCounts )[ 0 ];

								//	Get column titles for output display.

		String[] columnNames	=
			new String[]
			{
				getColTitleWordFormString( wordFormString ) ,
				WordHoardSettings.getString(
					"coltitleWordclass" ,
					"<html><br>Word class</html>" ),
				WordHoardSettings.getString(
					"coltitleMajorwordclass" ,
					"<html>Major<br>word class</html>" ),
				WordHoardSettings.getString(
					"coltitleWordformcount" ,
					"<html>Word form<br>count</html>" ),
				WordHoardSettings.getString(
					"coltitleWorkcount" ,
					"<html>Work<br>count</html>" )
			};
								//	Create sorted table model to hold
								//	the output.
		model		=
			new WordHoardSortedTableModel( columnNames , 0 , true );

								//	Create table entries.  The columns
								//	are the word form text, the word class
								//	(spelling and lemma only),
								//	the major word class (spelling, lemma,
								//	and word class only), the count
								//	of the word form in the selected
								//	corpus/text, and the number of texts
								//	in the selected corpus/text in which
								//	this word form occurs.

		Set keyset	= wordsAndCounts.keySet();

		int maxLabelWidth	= 0;
		int maxLabelWidth2	= 0;
		int maxLabelWidth3	= 0;

		String maxLabel		= "";
		String maxLabel2	= "";
		String maxLabel3	= "";

		for	(	Iterator iterator	= keyset.iterator();
				iterator.hasNext();
			)
		{
			Spelling wordToAnalyze		= (Spelling)iterator.next();

			Integer wordCount	=
				(Integer)wordsAndCounts.get( wordToAnalyze );

			if ( wordCount == null ) wordCount	= Integer.valueOf( 0 );

			Object[] freqAnal		= new Object[ 3 ];
			StringBuffer sb			= new StringBuffer();

			switch ( wordForm )
			{
				case WordForms.SPELLING	:
				case WordForms.LEMMA	:

					String[] wordClasses	=
						WordUtils.extractWordClassTags(
							wordToAnalyze.getString() );

					for ( int i = 0 ; i < wordClasses.length ; i++ )
					{
						int k = wordClasses[ i ].indexOf( ' ' );

						if ( k > 0 )
						{
							wordClasses[ i ] =
								wordClasses[ i ].substring( 0 , k ).trim();
						}

						if ( sb.length() > 0 ) sb.append( " " );

						sb.append
						(
							WordUtils.getMajorWordClassForWordClass(
								wordClasses[ i ] )
						);
					}

					break;

				case WordForms.WORDCLASS	:
					sb.append
					(
						WordUtils.getMajorWordClassForWordClass(
							wordToAnalyze.getString() )
					);

					break;
			}

			freqAnal[ 0 ]	= sb.toString();
			freqAnal[ 1 ]	= Double.valueOf( wordCount.intValue() );
			freqAnal[ 2 ]	= Double.valueOf( 0.0D );

			if ( workCounts.containsKey( wordToAnalyze ) )
			{
				freqAnal[ 2 ]	=
					new Double
					(
						((Integer)workCounts.get( wordToAnalyze )).doubleValue()
					);
			}

			String displayableWord	=
				WordUtils.getDisplayableWordText
				(
					wordToAnalyze ,
					wordForm
				);

			String displayableWordClass	=
				WordUtils.stripSpelling( displayableWord );

			int wordLength	= displayableWord.length();

			if ( wordLength > maxLabelWidth	)
			{
				maxLabelWidth	= wordLength;
				maxLabel		= displayableWord;
            }

			int wordLength2	= displayableWordClass.length();

			if ( wordLength2 > maxLabelWidth2 )
			{
				maxLabelWidth2	= wordLength2;
				maxLabel2		= displayableWordClass;
            }

			int wordLength3	= freqAnal[ 0 ].toString().length();

			if ( wordLength3 > maxLabelWidth3 )
			{
				maxLabelWidth3	= wordLength3;
				maxLabel3		= freqAnal[ 0 ].toString();
            }

			model.add
			(
				new FrequencyAnalysisDataRow
				(
					WordUtils.stripWordClass( displayableWord ) ,
					displayableWordClass ,
					freqAnal
				)
			);
		}
										//	Generate the results.
										//	Only show the word
										//	class column for spellings
										//	and lemmata.

		boolean showWordClass	=
			( wordForm	== WordForms.SPELLING ) ||
			( wordForm	== WordForms.LEMMA );

										//	Only show the major word
										//	class column for spellings,
										//	lemmata, and word classes.

		boolean showMajorWordClass	=
			( wordForm	== WordForms.SPELLING ) ||
			( wordForm	== WordForms.LEMMA ) ||
			( wordForm	== WordForms.WORDCLASS );

		resultsPanel	=
			generateResults
			(
				model ,
				maxLabel ,
				maxLabel2 ,
				maxLabel3 ,
				analysisTotalCount ,
				showWordClass ,
				showMajorWordClass
			);
	}

	/**	Displays results of frequency analysis in a sorted table.
	 *
	 *	@param	model				Table model holding data to display.
	 *	@param	maxLabel			Maximum width value for first label
	 *								column.
	 *	@param	maxLabel2			Maximum width value for second label
	 *								column.
	 *	@param	maxLabel3			Maximum width value for third label
	 *								column.
	 *	@param	totalWords			Total count of word forms.
	 *	@param	showWordClass		True to display word class.
	 *	@param	showMajorWordClass	True to display major word class.
	 *	@return	The results panel.
	 */

	protected ResultsPanel generateResults
	(
		WordHoardSortedTableModel model ,
		String maxLabel ,
		String maxLabel2 ,
		String maxLabel3 ,
		int totalWords ,
		boolean showWordClass ,
		boolean showMajorWordClass
	)
	{
		int rowCount	= model.getRowCount();

		String lowerCaseWordFormString	=
			( rowCount == 1 ) ?
				wordFormString : pluralWordFormString;

		lowerCaseWordFormString	= lowerCaseWordFormString.toLowerCase();

		String title;
		String shortTitle;

		if ( wordForm != WordForms.ISVERSE )
		{
			PrintfFormat titleFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString(
						"wordFormFrequenciesTitle" +
							( ( rowCount == 1 ) ? "S" : "P" ) ,
			    		"Frequencies of %s %s (total %s) in %s." )
				);

			title	=
				titleFormat.sprintf
				(
					new Object[]
					{
						StringUtils.formatNumberWithCommas( rowCount ) ,
						lowerCaseWordFormString ,
						StringUtils.formatNumberWithCommas( totalWords ) ,
						analysisText.toString(
							useShortWorkTitlesInHeaders )
					}
				);

			PrintfFormat shortTitleFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"Wordformsintext" ,
						"%s in \"%s\""
					)
				);

			shortTitle	=
				shortTitleFormat.sprintf
				(
					new Object[]
					{
						pluralWordFormString ,
						analysisText.toString(
							useShortWorkTitlesInWindowTitles )
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
						"WordFormFrequenciesPoetryAndProseTitle" ,
			    		"Frequencies of words in poetry and prose " +
			    			"(total %s) in %s."
			    	)
				);

			title	=
				titleFormat.sprintf
				(
					new Object[]
					{
						StringUtils.formatNumberWithCommas( totalWords ) ,
						analysisText.toString(
							useShortWorkTitlesInHeaders )
					}
				);

			PrintfFormat shortTitleFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"Wordsinpoetryandproseintext" ,
						"Words in poetry and prose in \"%s\""
					)
				);

			shortTitle	=
				shortTitleFormat.sprintf
				(
					new Object[]
					{
						analysisText.toString(
							useShortWorkTitlesInWindowTitles )
					}
				);
		}

		showWordClasses	= true;

		ResultsPanel results	=
			super.generateResults
			(
				new Spelling( "" , TextParams.ROMAN ) ,
				title ,
				shortTitle ,
				model.getColumnNames() ,
				new String[]
				{
					"%s" ,
					"%s" ,
					"%s" ,
					getDoubleFormat( 0 ) ,
					getDoubleFormat( 0 )
				} ,
				3 ,
				-1 ,
				-1 ,
				model ,
				new String[]{ maxLabel , maxLabel2 , maxLabel3 }
			);

		resultsTable.setColumnRenderer
		(
			0 ,
			new WordTableCellRenderer
			(
				SwingConstants.CENTER ,
				false ,
				false
			)
		);

		if ( !showWordClass )
		{
			resultsTable.hideColumn( WORDCLASSCOLUMN );
		}

		resultsTable.setColumnRenderer
		(
			2 ,
			new WordTableCellRenderer
			(
				SwingConstants.CENTER ,
				false ,
				false
			)
		);

		if ( !showMajorWordClass )
		{
			resultsTable.hideColumn( MAJORWORDCLASSCOLUMN );
		}

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


