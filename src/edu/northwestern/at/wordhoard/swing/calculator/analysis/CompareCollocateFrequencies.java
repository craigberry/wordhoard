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

/**	Compares a single word form's frequency between two sets of works.
 */

public class CompareCollocateFrequencies
	extends CollocateFrequencies
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

	public CompareCollocateFrequencies()
	{
		super
		(
			AnalysisDialog.COMPARESINGLECOLLOCATEFREQUENCIES
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

		TreeMap analysisCounts	= counts[ 0 ];

								//	Get collocation counts in
								//	reference text.
		counts	=
			getCollocateCounts
			(
				referenceText ,
				wordToAnalyze ,
				progressReporter ,
				WordHoardSettings.getString
				(
					"Findingcollocatesinreference" ,
					"Finding collocates in reference"
				)
			);

								//	If operation cancelled while getting
								//	collocate counts, quit.
		if ( counts == null )
		{
			closeProgressReporter();
			return;
		}

		TreeMap referenceCounts	= counts[ 0 ];

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
				WordHoardSettings.getString
				(
					"coltitleCollocate" ,
					"<html><br>Collocate</html>"
				) ,
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
//				" ",
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

		model				= new WordHoardSortedTableModel( columnNames , 0 , true );

								//	Compute collocation statistics.

		Set keyset			= analysisCounts.keySet();

		String maxLabel		= "";
		String maxLabel2	= "";
		int maxLabelWidth	= 0;
		int maxLabelWidth2	= 0;
		int collocatesDone	= 0;

		for	( Iterator iterator	= keyset.iterator(); iterator.hasNext(); )
		{
								//	Get next collocate.

			Spelling collocate	= (Spelling)iterator.next();

								//	Get count of word form in analysis text.

			Integer wordCount		=
				(Integer)analysisCounts.get( collocate );

			if ( wordCount	== null ) wordCount	= Integer.valueOf( 0 );

								//	Get count of word form in reference text.

			Integer referenceWordCount		=
				(Integer)referenceCounts.get( collocate );

			if ( referenceWordCount	== null )
			{
				referenceWordCount	= Integer.valueOf( 0 );
			}

			if ( collocate.getString().length() > maxLabelWidth )
			{
				maxLabel		= collocate.getString();
				maxLabelWidth	= maxLabel.length();
			}

			String wordClass	=
				WordUtils.stripSpelling( collocate.getString() );

			if ( wordClass.length() > maxLabelWidth2 )
			{
				maxLabel2		= wordClass;
				maxLabelWidth2	= maxLabel2.length();
			}

			CompareMultipleWordFrequencies.doFreq
			(
				collocate ,
				wordForm ,
				wordCount.intValue() ,
				analysisTotalCount ,
				referenceWordCount.intValue() ,
				refTotalCount ,
				percentReportMethod ,
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
						"Computingcollocatefrequencies" ,
						"Computing collocate frequencies"
					)
				);

				if ( progressReporter.isCancelled() ) break;
			}
		}
										//	Close progress dialog.

		boolean cancelled	= closeProgressReporter();

										//	Display the results.
		resultsPanel	=
			cancelled ?
				null :
				generateResults(
					model , maxLabel , maxLabel2 + "...." );
	}

	/**	Displays results of frequency analysis in a sorted table.
	 *
	 *	@param	model		The model containing the analysis results.
	 *	@param	maxLabel    Maximum size for word column.
	 *	@param	maxLabel2	Maximum size for word class column.
	 */

	protected ResultsPanel generateResults
	(
		WordHoardSortedTableModel model ,
		String maxLabel ,
		String maxLabel2
	)
	{
								//	Get title for results.

		PrintfFormat titleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString(
					"ComparesinglewordcollocatefrequenciesTitle" ,
			    	"Collocation analysis of %s (%s) in %s with respect to %s" )
			);

		String title=
			titleFormat.sprintf
			(
				new Object[]
				{
					wordToAnalyze ,
					wordFormString.toLowerCase() ,
					analysisText.toString( useShortWorkTitlesInHeaders ) ,
					referenceText.toString( useShortWorkTitlesInHeaders )
				}
			);

		PrintfFormat shortTitleFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"CompareCollocateFrequenciesinsands" ,
			    	"Comparing Collocate Frequencies"
			    )
			);

		String shortTitle	=
			shortTitleFormat.sprintf
			(
				new Object[]
				{
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
					getDoubleFormat( 2 )
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
		LabeledColumn resultOptions	= new LabeledColumn();

		resultOptions.addPair
		(
			"" ,
			createCompressValueRangeInTagCloudsCheckBox()
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
				"Tagclouddisplayofloglikelihoodmeasures" ,
				"Tag cloud display of log-likelihood measures"
			);

		PrintfFormat header1Format	=
			new PrintfFormat
			(
				WordHoardSettings.getString(
					"ComparesinglewordcollocatefrequenciesTitle" ,
			    	"Collocation analysis of %s (%s) in %s with respect to %s" )
			);

		String header1	=
			header1Format.sprintf
			(
				new Object[]
				{
					wordToAnalyze ,
					wordFormString.toLowerCase() ,
					analysisText.toString( useShortWorkTitlesInHeaders ) ,
					referenceText.toString( useShortWorkTitlesInHeaders )
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


