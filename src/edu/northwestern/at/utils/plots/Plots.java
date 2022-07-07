package edu.northwestern.at.utils.plots;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.io.*;
import javax.swing.*;

import org.krysalis.jcharts.*;
import org.krysalis.jcharts.chartData.*;
import org.krysalis.jcharts.axisChart.*;
import org.krysalis.jcharts.encoders.*;
import org.krysalis.jcharts.types.*;
import org.krysalis.jcharts.properties.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;

/**	Plot utilities.
 *
 *	<p>
 *	This static class provides various utility methods for generating
 *	plots and charts.
 *	</p>
 */

public class Plots
{
	/**	JPEG extensions file filter. */

	protected static FileExtensionFilter jpegFilter	=
			new FileExtensionFilter
			(
				new String[]{ ".jpg" , ".jpeg" },
				"JPEG Files"
			);

	/**	PNG extension file filter. */

	protected static FileExtensionFilter pngFilter	=
			new FileExtensionFilter( ".png" , "PNG Files" );

	/**	SVG extension file filter. */

	protected static FileExtensionFilter svgFilter	=
			new FileExtensionFilter( ".svg" , "SVG Files" );

    /**	Generate a line plot in a JPanel.
     *
     *	@param	panel	JPanel into which to drop plot.
     *	@param	xData	double array of x-axis data.
     *	@param	yData	double array of y-axis data.
     *	@param	width	width of bar chart (pixels).
     *	@param	height	height of bar chart (pixels).
     *	@param	title	plot title.
     *	@param	xTitle	x-axis title.
     *	@param	yTitle	y-axis title.
     */

    public static void linePlot
    (
    	JPanel panel ,
    	double[] xData ,
    	double[] yData ,
     	int width ,
     	int height ,
    	String title ,
    	String xTitle ,
    	String yTitle
    )
    {
    							//	Get panel size.

		Dimension dimension	= panel.getSize();

								//	Create titles for plot.

		DataSeries dataSeries	=
			new DataSeries
			(
//				new String[]{ "a", "b", "c", "d", "e", "f", "g"  } ,
				null ,
				xTitle ,
				yTitle ,
				title
			);
								//	Create data array.

		double[][] data	=	new double[ 2 ][ xData.length ];

		Paint[] paints	=	new Paint[ data.length ];

		for ( int i = 0 ; i < xData.length ; i++ )
		{
			data[ 0 ][ i ]	= xData[ i ];
			data[ 1 ][ i ]	= yData[ i ];
		}

		for ( int i = 0 ; i < data.length ; i++ )
		{
			paints[ i ]		= Color.cyan.brighter();
		}

		LineChartProperties theChartProperties	=
			(LineChartProperties)getChartTypeProperties( /* 1 */ data.length );

								//	Generate axis chart.
		try
		{
			AxisChartDataSet axisChartDataSet		=
				new AxisChartDataSet
				(
					data ,
					/* legendLabels */ null ,
					paints ,
					ChartType.LINE ,
					theChartProperties
				);

			dataSeries.addIAxisPlotDataSet( axisChartDataSet );

			ChartProperties chartProperties		= new ChartProperties();

								//	To make this plot horizontally,
								//	pass true to the AxisProperties
								//	constructor.

//			AxisProperties axisProperties		= new AxisProperties( true );

			AxisProperties axisProperties		= new AxisProperties();

			LegendProperties legendProperties	= new LegendProperties();

			AxisChart chart	=
				new AxisChart
				(
					dataSeries ,
					chartProperties ,
					axisProperties ,
					legendProperties ,
					(int)dimension.getWidth() ,
					(int)dimension.getHeight()
				);

			chart.setGraphics2D( (Graphics2D)panel.getGraphics() );
			chart.render();
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

    /**	Generate a bar chart.
     *
     *	@param	barLabels		labels for x-axis.
     *	@param	barValues			double array of y-axis data.
     *	@param	width			width of bar chart (pixels).
     *	@param	height			height of bar chart (pixels).
     *	@param	title			plot title.
     *	@param	xTitle			x-axis title.
     *	@param	yTitle			y-axis title.
	 *	@param	legendLabel		label for legend.
     *	@param	horizontal		true to display horizontal bar chart.
     *	@param	showBarValues	true to display bar values in bar.
     *
     *	@return					BarChartPanel in which BarChart will be drawn.
     */

    public static BarChartPanel barChart
    (
		String[] barLabels ,
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
		return new BarChartPanel
		(
			barLabels ,
			barValues ,
			width ,
			height ,
			title ,
			xTitle ,
			yTitle ,
			legendLabel ,
			horizontal ,
			showBarValues
		);
    }

    /**	Generate a bar chart in a JPanel.
     *
     *	@param	barData			double array of x-axis data.
     *	@param	barValues		double array of y-axis data.
     *	@param	width			width of bar chart (pixels).
     *	@param	height			height of bar chart (pixels).
     *	@param	title			plot title.
     *	@param	xTitle			x-axis title.
     *	@param	yTitle			y-axis title.
	 *	@param	legendLabel		label for legend.
     *	@param	horizontal		true to display horizontal bar chart.
     *	@param	showBarValues	true to display bar values in bar.
	 *
     *	@return			BarChartPanel in which BarChart will be drawn.
     */

    public static BarChartPanel barChart
    (
    	double[] barData ,
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
								//	Create x-axis labels from data values.

		String[] barLabels	= new String[ barData.length ];

		for ( int i = 0 ; i < barData.length ; i++ )
		{
			barLabels[ i ]	= (new Double( barData[ i ] )).toString();
		}

		return barChart
		(
			barLabels ,
			barValues ,
			width ,
			height ,
			title ,
			xTitle ,
			yTitle ,
			legendLabel ,
			horizontal ,
			showBarValues
		);
    }

	/**	Generate default line plot strokes and shapes.
	 *
	 *	@param	numberOfDataSets	Number of data sets in plot.
	 *	@return	Line chart properties.
	 */

	public static ChartTypeProperties getChartTypeProperties
	(
		int numberOfDataSets
	)
	{
		Stroke[] strokes	= new Stroke[ numberOfDataSets ];

		for( int i = 0 ; i < numberOfDataSets ; i++ )
		{
			strokes[ i ] = LineChartProperties.DEFAULT_LINE_STROKE;
		}

		strokes[ 0 ]	= new BasicStroke( 3.0f );

		Shape[] shapes	= new Shape[ numberOfDataSets ];

		for( int i = 0 ; i < numberOfDataSets ; i++ )
		{
			shapes[ i ]	= PointChartProperties.SHAPE_DIAMOND;
		}

		shapes[ 0 ]	= PointChartProperties.SHAPE_DIAMOND;

		return new LineChartProperties( strokes , shapes );
	}

	/**	Saves the chart to a file in JPEG format.
	 *
	 *	@param	chart		The chart to save.
	 *	@param	fileName	The file name to which to save the plot.
	 *	@param	quality		Number between 0.0 and 1.0 indicating
	 *						compression quality.
	 */

	public static void saveChartToFileAsJPEG
	(
		Chart chart ,
		String fileName ,
		float quality
	)
	{
		OutputStream outputStream			= null;
		BufferedOutputStream bufferedStream	= null;

		try
		{
			outputStream	= new FileOutputStream( new File( fileName ) );
			bufferedStream	= new BufferedOutputStream( outputStream );

			JPEGEncoder.encode( chart , quality , bufferedStream );
		}
		catch ( Exception e )
		{
//			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( bufferedStream != null ) bufferedStream.close();
			}
			catch ( Exception e )
			{
//				e.printStackTrace();
			}
		}
	}

	/**	Saves the chart to a file in JPEG format.
	 *
	 *	@param	chart		The chart to save.
	 *	@param	fileName	The file name to which to save the plot.
	 *
	 *
	 *	<p>
	 *	The JPEG compression quality is set to 1.0f .
	 *	</p>
	 */

	public static void saveChartToFileAsJPEG
	(
		Chart chart ,
		String fileName
	)
	{
		saveChartToFileAsJPEG( chart , fileName , 1.0f );
	}

	/**	Saves the chart to a file in PNG format.
	 *
	 *	@param	chart		The chart to save.
	 *	@param	fileName	The file name to which to save the plot.
	 *
	 */

	public static void saveChartToFileAsPNG
	(
		Chart chart ,
		String fileName
	)
	{
		OutputStream outputStream			= null;
		BufferedOutputStream bufferedStream	= null;

		try
		{
			outputStream	= new FileOutputStream( new File( fileName ) );
			bufferedStream	= new BufferedOutputStream( outputStream );

			PNGEncoder.encode( chart , bufferedStream );
		}
		catch ( Exception e )
		{
//			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( bufferedStream != null ) bufferedStream.close();
			}
			catch ( Exception e )
			{
//				e.printStackTrace();
			}
		}
	}

	/**	Saves the chart to a file in SVG format.
	 *
	 *	@param	chart		The chart to save.
	 *	@param	fileName	The file name to which to save the plot.
	 *
	 */

	public static void saveChartToFileAsSVG
	(
		Chart chart ,
		String fileName
	)
	{
		OutputStream outputStream			= null;
		BufferedOutputStream bufferedStream	= null;

		try
		{
			outputStream	= new FileOutputStream( new File( fileName ) );
			bufferedStream	= new BufferedOutputStream( outputStream );

			SVGEncoder.encode( chart , bufferedStream );
		}
		catch ( Exception e )
		{
//			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( bufferedStream != null ) bufferedStream.close();
			}
			catch ( Exception e )
			{
//				e.printStackTrace();
			}
		}
	}

	/**	Saves the chart to a file.
	 *
	 *	@param	chart		The chart to save.
	 *	@param	fileName	The file name to which to save the plot.
	 *
	 *	<p>
	 *	The filename extension determines the type of output.
	 *	.jpg -&gt; jpeg, .png -&gt; png, .svg -&gt; svg.  If the filename
	 *	does not end in one of these, no file is written.
	 *	</p>
	 */

	public static void saveChartToFile
	(
		Chart chart ,
		String fileName
	)
	{
								//	Get file name extension.

		String extension	=
			FileNameUtils.getFileExtension( fileName , false ).toLowerCase();

								//	Choose output type based upon
								//	extension.

		if ( extension.equals( "jpg" ) || extension.equals( "jpeg" ) )
		{
			saveChartToFileAsJPEG( chart , fileName );
		}
		else if ( extension.equals( "png" ) )
		{
			saveChartToFileAsPNG( chart , fileName );
		}
		else if ( extension.equals( "svg" ) )
		{
			saveChartToFileAsSVG( chart , fileName );
		}
	}

	/**	Saves the chart to a file.  Prompts for a file name.
	 *
	 *	@param	parentWindow	Parent window for file dialog.
	 *	@param	chart			The chart to save.
	 */

	public static void saveChartToFile( Window parentWindow , Chart chart )
	{
		FileDialogs.addFileFilter( svgFilter );
		FileDialogs.addFileFilter( pngFilter );
		FileDialogs.addFileFilter( jpegFilter );

		String[] saveFile	= FileDialogs.save( parentWindow );

		FileDialogs.clearFileFilters();

		if ( saveFile != null )
		{
			File file	= new File( saveFile[ 0 ] , saveFile[ 1 ] );

			Plots.saveChartToFile
			(
				chart ,
				file.getAbsolutePath()
			);
		}
	}

	/** Don't allow instantiation, do allow overrides. */

	protected Plots()
	{
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

