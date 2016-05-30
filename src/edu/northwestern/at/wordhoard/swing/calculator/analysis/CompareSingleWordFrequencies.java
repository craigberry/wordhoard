package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;

import edu.northwestern.at.utils.math.matrix.*;
import edu.northwestern.at.utils.math.distributions.*;
import edu.northwestern.at.utils.math.statistics.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.corpuslinguistics.*;
import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	Compares a single word form's frequency between two sets of works.
 */

public class CompareSingleWordFrequencies
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

    /**	Create a single word form frequency profile object.
     */

	public CompareSingleWordFrequencies()
	{
		super
		(
			AnalysisDialog.SINGLEWORDFORMCOMPARISON
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

								//	Get frequency of word to analyze
								//	in analysis text.

		int analysisCount	=
			analysisText.getWordFormCount( wordToAnalyze , wordForm );

								//	Get frequency of word to analyze
								//	in reference text.

		int refCount		=
			referenceText.getWordFormCount( wordToAnalyze , wordForm );

								//	Get total words in analysis and
								//	reference.

		int analysisTotalCount	=
			analysisText.getTotalWordFormCount( wordForm );

		int refTotalCount		=
			referenceText.getTotalWordFormCount( wordForm );

								//	Get distinct words in analysis and
								//	reference.

		int analysisDistinctCount	=
			analysisText.getDistinctWordFormCount( wordForm );

		int refDistinctCount		=
			referenceText.getDistinctWordFormCount( wordForm );

								//	Set output column names.

		String analysisPctColumnName	=
			getAnalysisPercentColumnName();

		String referencePctColumnName	=
			getReferencePercentColumnName();

    	String[] columnNames	=
    		new String[]
	    	{
				getColTitleWordFormString( wordFormString ) ,
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

		WordHoardSortedTableModel model	=
			new WordHoardSortedTableModel( columnNames , 0 , true );

								//	Compute frequency statistics.
		doFreq
		(
			wordToAnalyze ,
			analysisCount ,
			analysisTotalCount ,
			refCount ,
			refTotalCount ,
			model
		);
    								//	Display the results.
		resultsPanel	=
			generateResults
			(
				model ,
				analysisDistinctCount ,
				analysisTotalCount ,
				refDistinctCount ,
				refTotalCount
			);
	}

	/**	Perform frequency comparison of analysis and reference works for a word.
	 *
	 *	@param	wordToAnalyze		The word to analyze.
	 *	@param	analysisCount		Count of word in analysis text.
	 *	@param	analysisTotalCount	Total number of words in analysis text.
	 *	@param	refCount			Count of word in reference text.
	 *	@param	refTotalCount		Total number of words in reference text.
	 *	@param	model				Table model in which to store results.
	 */

	protected void doFreq
	(
		Spelling wordToAnalyze ,
		int analysisCount ,
		int analysisTotalCount ,
		int refCount ,
		int refTotalCount ,
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

		if ( percentReportMethod == AnalysisDialog.REPORTPARTSPERMILLION )
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
	 *	@param	model				Table model holding data to display.
	 *	@param	analysisDistinct	Distinct words in analysis text.
	 *	@param	analysisTotal		Total words in analysis text.
	 *	@param	referenceDistinct	Distinct words in reference text.
	 *	@param	referenceTotal		Total words in reference text.
	 *
	 *	@return						ResultsPanel with table and title.
	 */

	protected ResultsPanel generateResults
	(
		WordHoardSortedTableModel model ,
		int analysisDistinct ,
		int analysisTotal ,
		int referenceDistinct ,
		int referenceTotal
	)
	{
								//	Get title for results.

		StringBuffer sb	= new StringBuffer();

		sb.append
		(
			WordHoardSettings.getString
			(
				"CompareFrequencyProfileTitle" ,
		    	"Comparing frequency of %s %s in %s with respect to %s."
		    )
		);

		sb.append
		(
			WordHoardSettings.getString
			(
				"multipleWordFrequencyProfileTitle4" ,
	    		"  \"%s\" contains a total of %s %s."
			)
		);

		sb.append
		(
			WordHoardSettings.getString
			(
				"multipleWordFrequencyProfileTitle4" ,
	    		"  \"%s\" contains a total of %s %s %s."
			)
		);

		String pluralWordFormStringForDisplay	=
			pluralWordFormString.toLowerCase();

		PrintfFormat titleFormat	= new PrintfFormat( sb.toString() );

		String title	=
			titleFormat.sprintf
			(
				new Object[]
				{
					wordFormString.toLowerCase() ,
					wordToAnalyze ,
					analysisText.toString( useShortWorkTitlesInHeaders ) ,
					referenceText.toString( useShortWorkTitlesInHeaders ) ,
					analysisText.toString( useShortWorkTitlesInHeaders ) ,
					StringUtils.formatNumberWithCommas(
						analysisDistinct ) ,
					pluralWordFormStringForDisplay ,
					StringUtils.formatNumberWithCommas( analysisTotal ) ,
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
					referenceText.toString( useShortWorkTitlesInHeaders ) ,
					StringUtils.formatNumberWithCommas(
						referenceDistinct ) ,
					pluralWordFormStringForDisplay ,
					StringUtils.formatNumberWithCommas( referenceTotal ) ,
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

		PrintfFormat shortTitleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"Comparingssinsands" ,
					"Comparing %s \"%s\" in \"%s\" and \"%s\""
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
						useShortWorkTitlesInWindowTitles ) ,
					referenceText.toString(
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
				new String[]
				{
					wordToAnalyze.getString() ,
					wordToAnalyze.getString()
				}
			);

		if ( results != null )
		{
			resultsTable.setColumnRenderer
			(
				OVERUSECOLUMN ,
				new PlusOrMinusTableCellRenderer( colorCodeOveruseColumn )
			);
		}

		reorderCompareResultsTableColumns( resultsTable );

		return results;
	}

	/**	Reorder columns for display.
	 *
	 *	@param	resultsTable	The results table.
	 *
	 *	<p>
	 *	Static method so it can be used by sibling analyses.
	 *	</p>
	 */

	public static void reorderCompareResultsTableColumns
	(
		JTable resultsTable
	)
	{
		TableColumnModel tableColumnModel	=
			resultsTable.getColumnModel();
/*
		tableColumnModel.moveColumn( 3 , 1 );
		tableColumnModel.moveColumn( 6 , 2 );
		tableColumnModel.moveColumn( 4 , 3 );
		tableColumnModel.moveColumn( 6 , 4 );
*/
		tableColumnModel.moveColumn( 4 , 2 );
		tableColumnModel.moveColumn( 7 , 3 );
		tableColumnModel.moveColumn( 5 , 4 );
		tableColumnModel.moveColumn( 7 , 5 );
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


