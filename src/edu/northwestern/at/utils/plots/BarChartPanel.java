package edu.northwestern.at.utils.plots;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.*;
import javax.swing.*;

import org.krysalis.jcharts.*;
import org.krysalis.jcharts.chartData.*;
import org.krysalis.jcharts.axisChart.customRenderers.axisValue.renderers.*;
import org.krysalis.jcharts.types.*;
import org.krysalis.jcharts.properties.*;
import org.krysalis.jcharts.properties.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.printing.*;

/**	Generates a bar chart.
 */

public class BarChartPanel
	extends ResultsPanel
	implements PrintableContents
{
	/**	The bar chart.
	 */

	protected SaveableAxisChart chart			= null;

	/**	The data series.
	 */

	protected DataSeries dataSeries				= null;

	/**	The chart properties.
	 */

	protected ChartProperties chartProperties	= null;

	/**	The axis properties.
	 */

	protected AxisProperties axisProperties		= null;

	/**	The legend properties.
	 */

	protected LegendProperties legendProperties	= null;

	/**	Saves panel dimensions for optimizing paint method.
	 */

	protected Dimension panelDimension			= null;

	/**	Holds rendered chart image.
	 */

	protected JLabel imageLabel					= new JLabel();

	/**	True if bars are horizontal.
	 */

	protected boolean horizontal				= false;

	/**	Scrollpane around bar chart.
	 */

	protected XScrollPane scrollPane			= null;

	/**	Approximate height of bar chart area.
	 */

	protected int barArea						= 0;

    /**	Generate a bar chart in a JPanel.
     *
     *	@param	xAxisLabels		labels for x-axis.
     *	@param	barValues		double array of y-axis data.
     *	@param	width			plot width.
     *	@param	height			plot height.
     *	@param	title			plot title.
     *	@param	xTitle			x-axis title.
     *	@param	yTitle			y-axis title.
     *	@param	horizontal		true to display chart horizontally.
     *	@param	showBarValues	true to display values within each bar.
     */

    public BarChartPanel
    (
		String[] xAxisLabels ,
    	double[] barValues ,
    	int width ,
    	int height ,
    	String title ,
    	String xTitle ,
    	String yTitle ,
    	String legendLabel ,
    	boolean horizontal ,
    	boolean showBarValues
    )
    {
								//	Remember if bars drawn horizontally.

		this.horizontal	= horizontal;

								//	Create titles for plot.
		dataSeries	=
			new DataSeries
			(
				xAxisLabels ,
				xTitle ,
				yTitle ,
				title
			);
								//	Create data array from
								//	bar values.

		double[][] data	=	new double[ 1 ][ barValues.length ];

		for ( int i = 0 ; i < barValues.length ; i++ )
		{
			data[ 0 ][ i ]	= barValues[ i ];
		}
								//	Set color for each bar.

		Paint[] paints	=	new Paint[ data.length ];

		for ( int i = 0 ; i < data.length ; i++ )
		{
			paints[ i ]		= Color.cyan.brighter();
		}
								//	Get default bar chart
								//	plot properties.

		BarChartProperties theChartProperties	=
			new BarChartProperties();

								//	Display bar values if requested.

		if ( showBarValues )
		{
			ValueLabelRenderer valueLabelRenderer	=
				new ValueLabelRenderer( false , false , true , -1 );

			valueLabelRenderer.setValueLabelPosition(
				ValueLabelPosition.ON_TOP );

			valueLabelRenderer.useVerticalLabels( false );

			theChartProperties.addPostRenderEventListener(
				 valueLabelRenderer );
		}

    	try
    	{
								//	Generate axis chart
								//	data set.

			AxisChartDataSet axisChartDataSet		=
				new AxisChartDataSet
				(
					data ,
					new String[]{ legendLabel },
					paints ,
					ChartType.BAR ,
					theChartProperties
				);

								//	Add data set to
								//	out data series.

			dataSeries.addIAxisPlotDataSet( axisChartDataSet );

								//	Get default chart properties.

			chartProperties		= new ChartProperties();

								//	Add a border around the chart.

			chartProperties.setBorderStroke(
				ChartStroke.DEFAULT_CHART_OUTLINE );

								//	Get default axis properties and
								//	set horizontal or vertical display.

			axisProperties		= new AxisProperties( horizontal );
/*
								//	Change axis font.

			axisProperties.getYAxisProperties().setScaleChartFont
			(
				new ChartFont
				(
					new Font
					(
						"Tahoma" ,
						Font.PLAIN ,
						6
					) ,
					Color.red
				)
			);
*/
								//	Get y axis properties.

			AxisTypeProperties yAxisProperties	=
				axisProperties.getYAxisProperties();

					            //	Get font for y axis labels.
			Font font	=
				yAxisProperties.getScaleChartFont().getFont();

								//	Find approximate total height for
								//	bar chart bars.
			barArea	=
				(int)( barValues.length *
					( font.getSize() +
						yAxisProperties.getPaddingBetweenAxisLabels() ) );

								//	Get default legend properties.

			legendProperties	= new LegendProperties();
        }
		catch( ChartDataException chartDataException )
		{
			chartDataException.printStackTrace();
		}
    							//	Set bar chart panel size.

		Dimension panelDimension	= new Dimension( width , height );

		setPreferredSize( panelDimension );

								//	For horizontal bar charts, add a scroll
								//	pane to wrap the chart image.

		setBackground( Color.white );
		getBody().setBackground( Color.white );
		getButtons().setBackground( Color.white );

		if ( horizontal )
		{
			scrollPane	= new XScrollPane( imageLabel );
			add( scrollPane );
		}
		else
		{
			add( imageLabel );
		}
								//	Add a listener so we can redraw the
								//	chart when this panel changes size.
		addComponentListener
		(
			new ComponentAdapter()
			{
				public void componentResized( ComponentEvent e )
				{
					super.componentResized( e );
					renderChart();
				}

			}
		);
    }

    /**	Render the chart.
     */

	public void renderChart()
	{
								//	Get size of body panel which holds
								//	the chart.

		Dimension dimension	= getBody().getSize();

								//	Get adjusted height for drawing
								//	bar chart.

		int hheight	= Math.max( barArea , (int)dimension.getHeight() );

								//	If we have not yet generated the chart,
								//	or the enclosing panel changed size,
								//	generate the chart now.

		if	(	( chart == null ) ||
		        ( dimension != panelDimension ) )
		{
								//	Don't generate the chart if the width
								//	is zero.  This can happen during
								//	component initialization.

			if ( (int)dimension.getWidth() > 0 )
			{
								//	Generate the chart.
				chart	=
					new SaveableAxisChart
					(
						dataSeries ,
						chartProperties ,
						axisProperties ,
						legendProperties ,
						horizontal ?
							(int)(dimension.getWidth() * 0.975 ) :
							(int)dimension.getWidth() ,
						horizontal ?
							hheight :
							(int)dimension.getHeight()
					);
			}
								//	Remember the update panel size.

			panelDimension	= dimension;

								//	If we generated the chart,
								//	render it into a buffered image.
								//	This gets around a variety of problems
								//	with rendering the image directly
								//	into the panel's graphics buffer.

			if ( chart != null )
			{
								//	Save the chart into the results.

				setResults( chart );

								//	Don't render the chart if the
								//	width is zero.

				if ( (int)dimension.getWidth() > 0 )
				{
					BufferedImage copyImage =
						new BufferedImage
						(
							horizontal ?
								(int)(dimension.getWidth() * 0.975 ) :
								(int)dimension.getWidth() ,
							horizontal ?
								hheight :
								(int)dimension.getHeight() ,
							BufferedImage.TYPE_INT_RGB
						);
								//	Get graphics area for buffered image.

					Graphics g = copyImage.getGraphics();
								//	Set as the drawing area for the chart.

					chart.setGraphics2D( (Graphics2D)g );

					try
					{
								//	Render the chart into the buffered image.

						chart.render();

								//	Set the buffered image into the
								//	image label for display.

						imageLabel.setIcon( new ImageIcon( copyImage ) );

								//	Make sure the scroll pane knows
								//	it must change.

						if ( scrollPane != null ) scrollPane.invalidate();
					}
					catch( ChartDataException chartDataException )
					{
						chartDataException.printStackTrace();
					}
					catch( PropertyException propertyException )
					{
						propertyException.printStackTrace();
					}
				}
			}
		}
	}

	/**	Paint component.
	 *
	 *	@param	graphics	The graphics object for this component.
	 *
	 *	<p>
	 *	Overridden to ensure the chart gets created and drawn.
	 *	</p>
	 */

	public void paint( Graphics graphics )
	{
		super.paint( graphics );

								//	If the chart doesn't exist yet,
								//	schedule its creation for sometime
								//	after this paint request completes.
		if ( chart == null )
		{
			SwingUtilities.invokeLater
			(
				new Runnable()
				{
					public void run()
					{
						renderChart();
					}
				}
			);
		}
	}

	/**	Return the chart object.
	 *
	 *	@return		The bar chart object.
	 *				Null if the chart has not yet been drawn.
	 */

	public Chart getChart()
	{
		return chart;
	}

	/**	Return the results object.
	 *
	 *	@return		The bar chart object.
	 *				Null if the chart has not yet been drawn.
	 */

	public Object getResults()
	{
		return chart;
	}

	/** Return printable component.
	 *
	 *	@param		title		Title for printing.
	 *
	 *	@param		pageFormat	Page format for printing.
	 *
	 *	@return					Printable component.
	 */

	public PrintableComponent getPrintableComponent
	(
		String title,
		PageFormat pageFormat
	)
	{
		return
			new PrintableComponent
			(
				imageLabel ,
				pageFormat,
				new PrintHeaderFooter
				(
					title,
					"Printed " +
						DateTimeUtils.formatDateOnAt( new Date() ),
					"Page "
				)
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

