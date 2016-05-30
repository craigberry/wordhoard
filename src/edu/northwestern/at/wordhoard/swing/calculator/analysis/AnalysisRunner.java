package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.swing.LabeledColumn;
import edu.northwestern.at.utils.swing.ProgressReporter;
import edu.northwestern.at.utils.swing.ResultsPanel;
import edu.northwestern.at.wordhoard.swing.*;

/**	Interface implemented by WordHoard analysis classes.
 */

public interface AnalysisRunner
{
	/**	Display the analysis dialog.
	 *
	 *	@param	parentWindow	The parent window for the dialog.
	 *
	 *	@return					true if OK pressed in dialog, false otherwise.
	 */

	public boolean showDialog( JFrame parentWindow );

	/**	Run the analysis.
	 *
	 *	@param	parentWindow	Parent window for dialogs in the analysis.
	 *	@param	progressReporter	Progress display for analysis.
	 */

	public void runAnalysis
	(
		JFrame parentWindow ,
		ProgressReporter progressReporter
	);

	/**	Get chart output.
	 *
	 *	@return		ResultsPanel containing the chart.
	 */

	public ResultsPanel getChart();

	/**	Get cloud output.
	 *
	 *	@return		ResultsPanel containing the cloud.
	 */

	public ResultsPanel getCloud();

	/**	Get results.
	 *
	 *	@return		ResultsPanel containing the analysis results.
	 */

	public ResultsPanel getResults();

	/**	Get context results.
	 *
	 *	@param		parentWindow	Parent window for dialogs in the analysis.
	 *	@param		progressReporter	Progress display for analysis.
	 *
	 *	@return		ResultsPanel containing the context results.
	 */

	public ResultsPanel getContext
	(
		JFrame parentWindow ,
		ProgressReporter progressReporter
	);

	/**	Is chart output available?
	 *
	 *	@return		true if chart output available, false otherwise.
	 */

	public boolean isChartAvailable();

	/**	Is context output available?
	 *
	 *	@return		true if context output available, false otherwise.
	 */

	public boolean isContextAvailable();

	/**	Is cloud output available?
	 *
	 *	@return		true if cloud output available, false otherwise.
	 */

	public boolean isCloudAvailable();

	/**	Is output filter available?
	 *
	 *	@return		true if output filter available, false otherwise.
	 */

	public boolean isFilterAvailable();

	/**	Are result options available?
	 *
	 *	@return		true if result options are available, false otherwise.
	 */

	public boolean areResultOptionsAvailable();

	/**	Get result options.
	 *
	 *	@return		Result options as LabeledColumn.
	 */

	public LabeledColumn getResultOptions();

	/**	Set the context button.
	 *
	 *	@param	contextButton	The context button.
	 */

	public void setContextButton( JButton contextButton );

	/**	Save the results to a file.
	 */

//	public void saveResults();

	/**	Save the chart to a file.
	 */

	public void saveChart();

	/**	Handle selection change in results table.
	 *
	 *	@param	event	Table selection event.
	 */

	public void handleTableSelectionChange( ListSelectionEvent event );
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

