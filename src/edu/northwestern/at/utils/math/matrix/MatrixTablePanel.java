package edu.northwestern.at.utils.math.matrix;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.table.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;

/**	Display a matrix with a toolbar.
 */

public class MatrixTablePanel extends DataPanel
{
	protected XTable table;
//	protected JTable table;
	protected XTable rowLabelsTable;
	protected TableModel model;
	protected Matrix matrix;
	protected boolean viewColumnHeaders	= false;
	protected boolean viewRowLabels	= false;
	protected String[] columnLabels;
	protected String[] rowLabels;
	protected static String defaultFormatString	= "%26.18g";
	protected String formatString			= "%26.18g";

	public MatrixTablePanel( Matrix matrix , String formatString )
	{
		super();

		this.matrix			= matrix;
		this.formatString	= formatString;

		columnLabels		= new String[ matrix.columns() ];

		setModel();
		toWindow();
	}

	public MatrixTablePanel( Matrix matrix )
	{
		this( matrix , defaultFormatString );
	}

	public MatrixTablePanel( double[][] matrixData )
	{
		this( MatrixFactory.createMatrix( matrixData ) );
	}

	public void setFormatString( String formatString )
	{
		this.formatString	= formatString;

		update();

		MatrixEntryRenderer	renderer	=
			new MatrixEntryRenderer( formatString , false );

		table.setDefaultRenderer( Object.class , renderer );

		setWidth();
	}

	public void toClipBoard()
	{
		try
		{
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				new StringSelection( MatrixToString.toString( matrix ) ),
				null);
		}
		catch ( IllegalStateException e )
		{
			JOptionPane.showConfirmDialog
			(	null,
				"Copy to clipboard failed : " +
					e.getMessage(),
				"Error",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE
			);
		}
	}

	public String toString()
	{
		return matrix.toString();
	}

	public void toASCIIFile( File file )
	{
		try
		{
			FileUtils.writeTextFile(
				file , false , MatrixToString.toString( matrix ) );
		}
		catch ( Exception e )
		{
		}
	}

	private void setModel()
	{
		Double[][] array	= new Double[ matrix.rows() ][ matrix.columns() ];

		for ( int i = 0 ; i < array.length ; i++ )
		{
			for ( int j = 0 ; j < array[ i ].length ; j++ )
			{
				array[ i ][ j ]	= new Double( matrix.get( i + 1 , j + 1 ) );
			}
		}

		model = new DefaultTableModel( array , columnLabels );
	}

	public void setColumnLabels( String[] h )
	{
		if ( h == null )
		{
			viewColumnHeaders	= false;
		}
		else
		{
			if ( h.length != matrix.columns() )
			{
				throw new IllegalArgumentException(
					"Headers of the table must have " +
						matrix.columns() + " elements." );
			}

			columnLabels = h;

			viewColumnHeaders	= true;
		}

		update();

		setWidth();
	}

	public void setRowLabels( String[] rowLabels )
	{
		if ( rowLabels == null )
		{
			viewRowLabels	= false;
			rowLabelsTable	= null;

	        scrollPane.setRowHeaderView( rowLabelsTable );
		}
		else
		{
			if ( rowLabels.length != matrix.rows() )
			{
				throw new IllegalArgumentException(
					"Row labels of the table must have " +
						matrix.rows() + " elements." );
			}

			this.rowLabels	= rowLabels;

			viewRowLabels	= true;
		}

		update();

		DefaultTableModel rowLabelsTableModel	=
			new DefaultTableModel( 0 , 1 );

        for ( int i = 0 ; i < rowLabels.length ; i++ )
        {
            rowLabelsTableModel.addRow(
            	new Object[]{ rowLabels[ i ] } );
		}

		XTable rowLabelsTable	= new XTable( rowLabelsTableModel );

		javax.swing.LookAndFeel.installColorsAndFont
		(
			rowLabelsTable ,
			"TableHeader.background",
			"TableHeader.foreground",
			"TableHeader.font"
		);

		rowLabelsTable.setIntercellSpacing( new Dimension( 0 , 0 ) );

		RowHeaderRenderer renderer	= new RowHeaderRenderer();

		rowLabelsTable.setDefaultRenderer( Object.class , renderer );

		int maxLabelWidth		= 0;

		for ( int row = 0 ; row < rowLabels.length ; row++ )
		{
			Object o	= rowLabelsTableModel.getValueAt( row , 0 );

			Component comp =
				renderer.getTableCellRendererComponent(
					rowLabelsTable , o , false , false , row , 0 );

			int width	= comp.getPreferredSize().width;

			if ( width > maxLabelWidth ) maxLabelWidth = width;
		}

		maxLabelWidth += 10;

		TableColumn column	=
			rowLabelsTable.getColumnModel().getColumn( 0 );

		column.setPreferredWidth( maxLabelWidth );
		column.setMinWidth( maxLabelWidth );
		column.setMaxWidth( maxLabelWidth );

		Dimension d	= rowLabelsTable.getPreferredScrollableViewportSize();

		d.width		= rowLabelsTable.getPreferredSize().width;

		rowLabelsTable.setPreferredScrollableViewportSize( d );

		rowLabelsTable.setRowHeight( table.getRowHeight() );

		rowLabelsTable.setAutoscrolls( false );

		scrollPane.setRowHeaderView( rowLabelsTable );
	}

	public void update()
	{
		setModel();

		super.update();
	}

	public void setMatrix( Matrix matrix )
	{
		this.matrix	= matrix;

		columnLabels		= new String[ matrix.columns() ];

		update();
	}

	protected void toWindow()
	{
		table		= new XTable( model );
//		table		= new JTable( model );
//		rowLabel	= new XTable( rowLabelData );

		if ( !viewColumnHeaders )
		{
			table.setTableHeader( null );
		}

//		table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
		table.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		table.setCellSelectionEnabled( true );

		MatrixEntryRenderer	renderer	=
			new MatrixEntryRenderer( formatString , false );

		table.setDefaultRenderer( Object.class , renderer );

		setWidth();

		scrollPane = new XScrollPane( table );

		scrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

		scrollPane.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );

		scrollPane.setPreferredSize( getSize() );
		scrollPane.setSize( getSize() );

		add( scrollPane , BorderLayout.CENTER );
	}

	/**	Computes and sets the column widths.
	 *
	 *	<p>Call this method after adding all the setting rows.
	 */

	public void setWidth()
	{
		int numRows			= model.getRowCount();
		int numColumns		= table.getColumnModel().getColumnCount();
		int maxEntryWidth	= 0;

		MatrixEntryRenderer renderer	=
			new MatrixEntryRenderer( formatString , false );

		if ( viewColumnHeaders )
		{
			for ( int col = 0 ; col < numColumns ; col++ )
			{
				Component comp =
					renderer.getTableCellRendererComponent(
						table , columnLabels[ col ] , false , false , 0 , col );

				int width	= comp.getPreferredSize().width;

				if ( width > maxEntryWidth ) maxEntryWidth = width;
			}
		}

		for ( int row = 0 ; row < numRows ; row++ )
		{
			for ( int col = 0 ; col < numColumns ; col++ )
			{
				Object o	= model.getValueAt( row , col );

				Component comp =
					renderer.getTableCellRendererComponent(
						table , o , false , false , row , col );

				int width	= comp.getPreferredSize().width;

				if ( width > maxEntryWidth ) maxEntryWidth = width;
			}
		}

		maxEntryWidth += 10;

		for ( int col = 0 ; col < numColumns ; col++ )
		{
			TableColumn column	=
				table.getColumnModel().getColumn( col );

			column.setPreferredWidth( maxEntryWidth );
			column.setMinWidth( maxEntryWidth );
			column.setMaxWidth( maxEntryWidth );
		}
	}

	public Matrix getMatrix()
	{
		return matrix;
	}

	public JTable getTable()
	{
		return table;
	}

	/**	Define a cell renderer for matrix entries.
	 */

	protected class MatrixEntryRenderer
		extends DefaultTableCellRenderer
	{
		/**	Formatting string for matrix entries.
		 */

		protected PrintfFormat format;

		/**	True if debugging enabled.
		 */

		protected boolean debug	= false;

		/**	Create matrix entry renderer.
		 *
		 *	@param	formatString	The PrintfFormat string for formatting
		 *							each matrix entry.
		 *	@param	debug	boolean to enable debugging.
		 */

		public MatrixEntryRenderer( String formatString , boolean debug )
		{
			super();

			this.format	= new PrintfFormat( formatString );
			this.debug	= debug;
		}

		/**	Get a matrix entry renderer for a matrix entry.
		 */

		public Component getTableCellRendererComponent
		(
			JTable table ,
			Object value ,
			boolean isSelected ,
			boolean hasFocus ,
			int row ,
			int column
		)
		{
								//	Convert number using the
								//	specified format.

			String formattedValue;

			if ( value instanceof Double )
			{
				formattedValue	=
					StringUtils.trim(
						format.sprintf( ((Double)value).doubleValue() ) );
			}
			else
			{
				formattedValue	= value.toString();
			}

			if ( debug )
			{
				System.out.println(
					"row=" + row + ", column=" + column + ", value=" +
					formattedValue );
			}

			DefaultTableCellRenderer renderer =
				(DefaultTableCellRenderer)
					super.getTableCellRendererComponent
					(
						table ,
						formattedValue ,
						isSelected ,
						hasFocus ,
						row ,
						column
					);
								// Set foreground and background
								// colors based upon whether the
								// current row is selected or not.
			if ( isSelected )
			{
				setForeground( table.getSelectionForeground() );
				setBackground( table.getSelectionBackground() );
			}
			else
			{
				setForeground( table.getForeground() );
				setBackground( table.getBackground() );
			}

			return renderer;
		}
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

