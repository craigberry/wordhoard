package edu.northwestern.at.utils.plots;

/*	Please see the license information at the end of this file. */

import java.awt.*;

import org.krysalis.jcharts.chartData.interfaces.*;
import org.krysalis.jcharts.axisChart.*;
import org.krysalis.jcharts.properties.*;

import edu.northwestern.at.utils.swing.*;

/**	Axis chart which can save itself to a file.
 */

public class SaveableAxisChart extends AxisChart
	implements SaveToFile
{
	/**	Create SaveableAxisChart.  Constructor same as AxisChart.
	 *
	 *	@param iAxisDataSeries		Data series.
	 *	@param chartProperties		Chart properties.
	 *	@param axisProperties		Axis properties.
	 *	@param legendProperties 	Legend properties.
	 *	@param pixelWidth			Width of chart in pixels.
	 *	@param pixelHeight			Height of chart in pixels.
	 */

	public SaveableAxisChart
	(
		IAxisDataSeries iAxisDataSeries ,
		ChartProperties chartProperties ,
		AxisProperties axisProperties ,
		LegendProperties legendProperties ,
		int pixelWidth ,
		int pixelHeight
	)
    {
    	super
    	(
    		iAxisDataSeries ,
    		chartProperties ,
    		axisProperties ,
    		legendProperties ,
    		pixelWidth ,
    		pixelHeight
    	);
    }

	/**	Save chart to a file.
	 *
	 *	@param	fileName	Name of file to which to save chart.
	 */

	public void saveToFile( String fileName )
	{
		Plots.saveChartToFile( this , fileName );
	}

	/**	Save chart to a file.
	 *
	 *	@param	parentWindow	Parent window for file dialog.
	 *
	 *	<p>
	 *	Runs a file dialog to get the name of the file to which to
	 *	save the chart.
	 *	</p>
	 */

	public void saveToFile( Window parentWindow )
	{
		Plots.saveChartToFile( parentWindow , this );
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

