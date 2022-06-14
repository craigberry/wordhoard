package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.utils.swing.icons.*;
import edu.northwestern.at.utils.swing.printing.*;
import edu.northwestern.at.utils.*;

/**	A JTable with different defaults and behavior.
 *
 *	<ul>
 *	<li>The font is set to XParameters.tableFont if not null.</li>
 *	<li>The row height is set to XParameters.listRowHeight if not 0.
 *	<li>Optional icons may be added to the table cell strings. Table
 *		elements must implement the {@link AddIcon} interface.</li>
 *	<li>The renderer does not draw focus indicators for the Object and String
 *		column types.</li>
 *	<li>Selection changes are triggered on mouse clicked events rather
 *		that on mouse pressed events, to avoid interference with drag
 *		gesture recognizers.</li>
 *	<li>Neither vertical nor horizontal lines are drawn between cells.</li>
 *	<li>We fix a Swing bug: When new rows are inserted they should not be
 *		selected.</li>
 *	<li>Clipboard cut, copy, and paste are enabled.</li>
 *	<li>Alternate rows may be colored with stripes.</li>
 *	<li>A title may be associated with the table for printing and
 *		file saving purposes.</li>
 *	<li>The table data may be saved to a file.</li>
 *	</ul>
 *
 *	<p>The constructors are the same as in JTable. We did not bother
 *	giving them their own javadoc.</p>
 */

public class XTable extends JTable
	implements PrintableContents, CutCopyPaste, SaveToFile, SelectAll
{
	/**	Default mouse listeners. */

	private MouseListener[] defaultMouseListeners;

	/**	Custom table cell renderer.  Never draws focus indicators. */

	private static class Renderer extends DefaultTableCellRenderer
	{
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
			DefaultTableCellRenderer renderer =
				(DefaultTableCellRenderer)
					super.getTableCellRendererComponent(
						table , value , isSelected , false , row , column );

			if ( value instanceof AddIcon )
			{
				((AddIcon)value).addIcon( renderer );
			}
			else
			{
				renderer.setIcon( null );
			}

			return renderer;
		}
	}

	/**	True to enabled striping alternate rows in table. */

	protected boolean stripeRows	= true;

	/**	True to striped "empty" rows after end of table rows proper. */

	protected boolean stripeEmptyRows	= true;

	/**	Even row color when striping enabled.  Default is white. */

	protected Color evenRowColor	= Color.white;

	/**	Odd row color when striping enabled.  Default is a light gray. */

	protected Color oddRowColor	= new Color( 225 , 225 , 225 );

	/**	Table title. */

	protected String title	= null;

	/**	True to copy column headers along with data to clipboard.
	 */

	protected boolean copyColumnHeaders	= true;

	/**	Create extended table.
	 */

	public XTable()
	{
		super();
		common();
	}

	public XTable( int numRows , int numColumns )
	{
		super( numRows , numColumns );
		common();
	}

	public XTable
	(
		Object[][] rowData ,
		Object[] columnNames
	)
	{
		super( rowData , columnNames );
		common();
	}

	public XTable( TableModel dm )
	{
		super( dm );
		common();
	}

	public XTable( TableModel dm , TableColumnModel cm )
	{
		super();
		setColumnModel( cm );
		setModel( dm );
		common();
	}

	public XTable
	(
		TableModel dm ,
		TableColumnModel cm ,
		ListSelectionModel sm
	)
	{
		super();
		setColumnModel( cm );
		setModel( dm );
		setSelectionModel( sm );
		common();
	}

	public XTable( Vector rowData , Vector columnNames )
	{
		super( rowData , columnNames );
		common();
	}

	/**	Sets the default attributes. */

	private void common()
	{
		if ( XParameters.tableFont != null ) setFont( XParameters.tableFont );

		if ( XParameters.tableRowHeight != 0 )
			setRowHeight( XParameters.tableRowHeight );

		Renderer renderer	= new Renderer();

		setDefaultRenderer( Object.class , renderer );
		setDefaultRenderer( String.class , renderer );

		setShowHorizontalLines( false );
		setShowVerticalLines( false );

		ToolTipManager.sharedInstance().unregisterComponent( this );
		ToolTipManager.sharedInstance().unregisterComponent( getTableHeader() );

		defaultMouseListeners =
			(MouseListener[])getListeners( MouseListener.class );

		for ( int i = 0 ; i < defaultMouseListeners.length ; i++ )
		{
			removeMouseListener( defaultMouseListeners[ i ] );
		}

		addMouseListener( mouseListener );

								// Setup for copy and paste.
		KeyStroke copyKey =
			KeyStroke.getKeyStroke(
				KeyEvent.VK_C, Env.MENU_SHORTCUT_KEY_MASK );

		KeyStroke pasteKey =
			KeyStroke.getKeyStroke(
				KeyEvent.VK_V, Env.MENU_SHORTCUT_KEY_MASK );

		registerKeyboardAction(
			copyAction, "Copy", copyKey, JComponent.WHEN_FOCUSED );

		registerKeyboardAction(
			pasteAction, "Paste", pasteKey, JComponent.WHEN_FOCUSED );
	}

	/**	Mouse listener interceptor.
	 *
	 *	<p>Modifies the selection on clicked events rather than pressed events.
	 */

	protected MouseListener mouseListener =
		new MouseListener()
		{
			public void mouseClicked( MouseEvent event )
			{
				Component source	= event.getComponent();
				long when			= event.getWhen();
				int modifiers		= event.getModifiersEx() |
					InputEvent.BUTTON1_DOWN_MASK;
				int x					= event.getX();
				int y					= event.getY();
				int clickCount			= event.getClickCount();
				boolean popupTrigger	= event.isPopupTrigger();

				MouseEvent newEvent		=
					new MouseEvent
					(
						source ,
						MouseEvent.MOUSE_PRESSED ,
						when ,
						modifiers ,
						x ,
						y ,
						clickCount ,
						popupTrigger
					);
								//	Row to which this event applies.

				int row	= rowAtPoint( event.getPoint() );

				for ( int i = 0 ; i < defaultMouseListeners.length ; i++)
				{
					defaultMouseListeners[ i ].mousePressed( newEvent );
				}
								//	Unselect row on mouse click +
								//	control key pressed.

				if ( isUnselectEnabled() && event.isControlDown() )
				{
					if ( isRowSelected( row ) )
					{
						removeRowSelectionInterval( row , row );
					}
					else
					{
						addRowSelectionInterval( row , row );
					}
				}
			}

			public void mouseEntered( MouseEvent event )
			{
			}

			public void mouseExited( MouseEvent event )
			{
			}

			public void mousePressed( MouseEvent event )
			{
			}

			public void mouseReleased( MouseEvent event )
			{
			}
		};

	/**	Handles a table changed event.
	 *
	 *	<p>We override this method to fix a Swing bug: Newly inserted rows
	 *	should not be selected.
	 *
	 *	@param	e		The table model event.
	 */

	public void tableChanged( TableModelEvent e )
	{
		super.tableChanged( e );

		if ( e.getType() == TableModelEvent.INSERT )
		{
			int row1	= e.getFirstRow();
			int row2	= e.getLastRow();

			if ( ( row1 < 0 ) || ( row2 < 0 ) || ( row1 > row2 ) ) return;

			removeRowSelectionInterval( row1 , row2 );
		}
	}

	/**	Sets the table cell renderer for a column.
	 *
	 *	@param	columnNumber		Column number.
	 *
	 *	@param	renderer			Renderer.
	 */

	public void setColumnRenderer
	(
		int columnNumber,
		TableCellRenderer renderer
	)
	{
		TableColumn column =
			getColumnModel().getColumn( columnNumber );

		column.setCellRenderer( renderer );
	}

	/**	Sets the width of a column to accomodate a long value.
	 *
	 *	<p>
	 *	Both the preferred and the maximum width are set. This
	 *	method is used to set the width of the setting name column.
	 *	</p>
	 *
	 *	@param	columnNumber		Column number.
	 *
	 *	@param	longValue			The long value.
	 *
	 *	@param	setMaxSize			Fix the column size to the long value's
	 *								size.
	 */

	public void setColumnWidth
	(
		int columnNumber ,
		Object longValue ,
		boolean setMaxSize
	)
	{
		TableColumn column =
			getColumnModel().getColumn( columnNumber );

		TableCellRenderer renderer = column.getCellRenderer();

		if ( renderer == null )
		{
			renderer =
				getDefaultRenderer(
					getModel().getColumnClass( columnNumber ) );
        }

		Component comp =
			renderer.getTableCellRendererComponent(
				this, longValue, false, false, 0, columnNumber );

		int colWidth = comp.getPreferredSize().width + 20;

		column.setPreferredWidth( colWidth );

		if ( setMaxSize ) column.setMaxWidth( colWidth );
	}

	/**	Sets the width of a column to accomodate a long value.
	 *
	 *	<p>
	 *	Both the preferred and the maximum width are set. This
	 *	method is used to set the width of the setting name column.
	 *	</p>
	 *
	 *	@param	columnNumber		Column number.
	 *
	 *	@param	longValue			The long value.
	 */

	public void setColumnWidth
	(
		int columnNumber ,
		Object longValue
	)
	{
		setColumnWidth( columnNumber, longValue, true );
	}

	/** Hides a column by setting its width to zero.
	 *
	 *	@param	columnNumber	The column to hide.
	 */

	public void hideColumn( int columnNumber )
	{
		TableColumnModel columnModel = getColumnModel();

		if ( columnModel instanceof XTableColumnModel )
		{
			((XTableColumnModel)columnModel).setColumnVisible(
				columnNumber , false );
		}
		else
		{
			TableColumn column =
				getColumnModel().getColumn( columnNumber );

			column.setMaxWidth( 0 );
			column.setMinWidth( 0 );
			column.setPreferredWidth( 0 );
		}
	}

	/** Prints the table.
	 *
	 *	@param	title			Title for output.
	 *	@param	printerJob		The printer job.
	 *	@param	pageFormat		The printer page format.
	 */

	public void printContents
	(
		final String title,
		final PrinterJob printerJob,
		final PageFormat pageFormat
	)
	{
		Thread runner = new Thread( "Print Table" )
		{
			public void run()
			{
				PrintUtilities.printComponent(
					getPrintableComponent( title , pageFormat ),
					title,
					printerJob,
					pageFormat ,
					true
				);
			}
		};

		runner.start();
	}

	/** Prints the table.
	 */

	public void printContents()
	{
		Thread runner = new Thread( "Print Table" )
		{
			public void run()
			{
				PrintUtilities.printComponent(
					getPrintableComponent
					(
						title ,
						PrinterSettings.pageFormat
					),
					title,
					PrinterSettings.printerJob,
					PrinterSettings.pageFormat ,
					true
				);
			}
		};

		runner.start();
	}

	/** Return printable component.
	 *
	 *	@param		title		Title for printing.
	 *
	 *	@param		pageFormat	Page format for printing.
	 *
	 *	@return					Printable component for XTable.
	 */

	public PrintableComponent getPrintableComponent
	(
		String title,
		PageFormat pageFormat
	)
	{
		return
			new PrintJTable
			(
				this ,
				pageFormat,
				new PrintHeaderFooter(
					title,
					"Printed " +
						DateTimeUtils.formatDateOnAt( new Date() ),
					"Page " )
			);
	}

    /** Clipboard cut. No-op. */

    public void cut()
    {
    }

    /** Clipboard copy. */

    public void copy()
    {
    							// Create string buffer to hold
    							// data to be copied to clipboard.

		StringBuffer stringBuffer	= new StringBuffer();

								// Get counts of selected rows and columns.

		int numCols = getSelectedColumnCount();
		int numRows = getSelectedRowCount();

								// Get the selected rows and columns.

		int[] rowsSelected = getSelectedRows();
		int[] colsSelected = getSelectedColumns();

								// If column selection is not enabled,
								// but there are rows selected, assume
								// we want to select all the columns
								// for each selected row.

		if ( !getColumnSelectionAllowed() )
		{
			numCols			= getColumnCount();

			colsSelected	= new int[ numCols ];

			for ( int i = 0 ; i < numCols ; i++ )
			{
				colsSelected[ i ]	= i;
			}
		}
								// Make sure they are contiguous by
								// checking that the difference in the
								// starting and ending rows and columns
								// match the number of selected rows
								// and columns.

		if	(	!( ( ( numRows - 1 ) ==
					rowsSelected[ rowsSelected.length - 1 ] -
						rowsSelected[ 0 ] ) &&
					numRows == rowsSelected.length) &&
				( ( numCols - 1 ) ==
					colsSelected[ colsSelected.length - 1 ] -
						colsSelected[ 0 ] &&
					numCols == colsSelected.length ) )
		{
			new ErrorMessage( "Invalid copy selection." );
			return;
		}
								// Output column headers for the
								// selected columns as tab separated values.

		if ( copyColumnHeaders )
		{
			TableColumnModel colModel	= getColumnModel();

			for ( int j = 0 ; j < numCols ; j++ )
			{
				TableColumn tableColumn	=
					colModel.getColumn( colsSelected[ j ] );

								//	Get the column header text.

				String columnTitle	= (String)tableColumn.getIdentifier();

                      			//	Strip HTML, etc.

				columnTitle			=
//					SaveTableModelData.cleanColumnHeader( columnTitle );
					SaveTableData.cleanColumnHeader( columnTitle );

				stringBuffer.append( columnTitle );

				if ( j < ( numCols - 1  ) ) stringBuffer.append( "\t" );
			}

			stringBuffer.append( "\n" );
		}
								// Output the contents of the selected
								// cells as tab separated values.
								// Rows are separated by an end of line.

		for ( int i = 0 ; i < numRows ; i++ )
		{
			for ( int j = 0 ; j < numCols ; j++ )
			{
				stringBuffer.append(
					getValueAt( rowsSelected[ i ] , colsSelected[ j ] ) );

				if ( j < ( numCols - 1  ) ) stringBuffer.append( "\t" );
			}

			stringBuffer.append( "\n" );
		}
								// Create transferable from the
								// tab-separated data fields.

		StringSelection stringSelection  =
			new StringSelection( stringBuffer.toString() );

								// Put data onto clipboard.

		SystemClipboard.setContents( stringSelection , stringSelection );
    }

    /** Clipboard paste. */

    public void paste()
    {
								// Get the starting row and column
								// at which to paste the data.

		int startRow = ( getSelectedRows() )[ 0 ];
		int startCol = ( getSelectedColumns() )[ 0 ];

		try
		{
								// Get the data from the clipboard.

			String pastedString =
				(String)( SystemClipboard.getContents( this ).getTransferData(
					DataFlavor.stringFlavor ) );

								// Split the data by lines.
								// Each line represents a new row of
								// cell data to paste.

			StringTokenizer st1 =
				new StringTokenizer( pastedString , "\n" );

			for ( int i = 0; st1.hasMoreTokens(); i++ )
			{
				String rowString = st1.nextToken();

								// Within a row, each cell's data
								// is separated by a tab character.

				StringTokenizer st2 =
					new StringTokenizer( rowString , "\t" );

				for ( int j = 0; st2.hasMoreTokens(); j++ )
				{
								// Get cell's value.

					String value = (String)st2.nextToken();

								// If it fits within the current confines
								// of the table, store the value.  We do not
								// change the size of the table to accommodate
								// values which would lie outside the table
								// bounds.

					if	(	( ( startRow + i ) < getRowCount() ) &&
							( ( startCol + j ) < getColumnCount() ) )
						setValueAt( value, startRow + i, startCol + j );
				}
			}
		}
		catch ( Exception e )
		{
//			e.printStackTrace();
		}
    }

	/** Copy action. */

	private Action copyAction =
		new AbstractAction()
		{
			public void actionPerformed( ActionEvent event )
			{
				copy();
			}
		};

	/**	Gets the copy action.
	 *
	 *	@return		The copy action.
	 */

	public Action getCopyAction()
	{
		return copyAction;
	}

	/** Paste action. */

	private Action pasteAction =
		new AbstractAction()
		{
			public void actionPerformed( ActionEvent event )
			{
				paste();
			}
		};

	/**	Gets the paste action.
	 *
	 *	@return		The paste action.
	 */

	public Action getPasteAction()
	{
		return pasteAction;
	}

	/**	Is cut enabled?
	 *
	 *	@return		false since cut is not enabled.
	 */

	public boolean isCutEnabled()
	{
		return false;
	}

	/**	Is copy enabled?
	 *
	 *	@return		true since copy is enabled.
	 */

	public boolean isCopyEnabled()
	{
		return isTextSelected();
	}

	/**	Is paste enabled?
	 *
	 *	@return		 Returns false.
	 */

	public boolean isPasteEnabled()
	{
		return false;
	}

	/**	Paints empty table rows to support striped output.
	 *
	 *	@param	graphics	The graphics object on which to display the table.
	 */

	public void paint( Graphics graphics )
	{
		super.paint( graphics );

		if ( stripeEmptyRows ) paintEmptyRows( graphics );
	}

	/**	Paint background of implied empty rows when table rows do not
	 *	fill the visible area.
	 *
	 *	@param	graphics	The graphics context for the table.
	 */

	protected void paintEmptyRows( Graphics graphics )
	{
								//	Get number of rows in table.

		int rowCount		= getRowCount();

								//	Get current clipping bounds.

		Rectangle clip		= graphics.getClipBounds();

							    //	Get sum of row heights.

		int sumRowHeights	= 0;

		for ( int i = 0 ; i < rowCount ; i++ )
		{
			sumRowHeights	+= getRowHeight( i );
		}

								//	If the rows do not fill the current
								//	clipping area, add enough stripes
								//	of height "rowHeight" to fill in the
								//	blank area.

		if ( sumRowHeights < clip.height )
		{
			for ( int i = rowCount ; i <= clip.height / rowHeight; ++i )
			{
                graphics.setColor( getColorForRow( i ) );

                graphics.fillRect
                (
                	clip.x , sumRowHeights , clip.width , rowHeight
                );

                sumRowHeights += rowHeight;
            }
        }
    }

	/**	Causes table to expand to fill all its available space.
	 */

	public boolean getScrollableTracksViewportHeight()
	{
		if ( getParent() instanceof JViewport )
		{
			JViewport parent	= (JViewport)getParent();
			return ( parent.getHeight() > getPreferredSize().height );
        }

        return false;
    }

	/**	Determines if horizontal scroll bar required.
	 *
	 *	@return		true if horizontal scrollbar needed.
     */

	public boolean getScrollableTracksViewportWidth()
	{
		if ( autoResizeMode != AUTO_RESIZE_OFF )
		{
			if ( getParent() instanceof JViewport )
			{
				return
					( ((JViewport)getParent()).getWidth() >
						getPreferredSize().width );
			}
		}

		return false;
	}

	/**	Set header foreground color.
	 *
	 *	@param	foregroundColor		The header foreground color.
	 */

	public void setHeaderForeground( Color foregroundColor )
	{
		if ( getTableHeader() != null )
		{
			getTableHeader().setForeground( foregroundColor );
		}
	}

	/**	Set header background color.
	 *
	 *	@param	backgroundColor		The header background color.
	 */

	public void setHeaderBackground( Color backgroundColor )
	{
		if ( getTableHeader() != null )
		{
			getTableHeader().setBackground( backgroundColor );
		}
	}

	/**	Get striping status for the table.
	 *
	 *	@return		true if alternate rows are to be striped when displayed.
	 */

	public boolean getStriped()
	{
		return stripeRows;
	}

	/**	Enable or disable striping for the table.
	 *
	 *	@param	stripeRows		true to enable alternate rows
	 *								to be striped, false to leave the
	 *								rows unstriped.
	 */

	public void setStriped( boolean stripeRows )
	{
		this.stripeRows	= stripeRows;
	}

	/**	Get empty row striping status for the table.
	 *
	 *	@return		true if empty trailing rows are to be striped.
	 */

	public boolean getStripeEmptyRows()
	{
		return stripeEmptyRows;
	}

	/**	Enable or disable empty row striping for the table.
	 *
	 *	@param	stripeEmptyRows		true to enable striping empty rows
	 *								following the table rows proper.
	 */

	public void setStripeEmptyRows( boolean stripeEmptyRows )
	{
		this.stripeEmptyRows	= stripeEmptyRows;
	}

	/**	Set the stripe colors.
	 *
	 *	@param	evenRowColor	The color for even rows.
	 *	@param	oddRowColor	The color for odd rows.
	 *
	 *	<p>
	 *	Selected rows are not striped.
	 *	</p>
	 */

	public void setStripeColors( Color evenRowColor , Color oddRowColor )
	{
		this.evenRowColor	= evenRowColor;
		this.oddRowColor	= oddRowColor;
	}

	/**	Get background color for a given row.
	 *
	 *	@param	row 	The row.
	 *
	 *	@return			The background color for the row.
     */

	public Color getColorForRow( int row )
	{
		if ( stripeRows )
		{
	        return ( ( row % 2 ) == 0 ) ? evenRowColor : oddRowColor;
		}
		else
		{
			return getBackground();
 		}
    }

	/**	Paint alternate rows in different colors.
	 *
	 *	@param	renderer	The parent renderer.
	 *	@param	row			The row for the cell to paint.
	 *	@param	column		The column for the cell to paint.
	 */

	public Component prepareRenderer
	(
		TableCellRenderer renderer ,
		int row ,
		int column
	)
	{
		Component c	= super.prepareRenderer( renderer , row , column );

		if ( !isCellSelected( row , column ) )
		{
			c.setBackground( getColorForRow( row ) );
			c.setForeground( UIManager.getColor( "Table.foreground" ) );
		}
		else
		{
			c.setBackground(
				UIManager.getColor( "Table.selectionBackground" ) );

			c.setForeground(
				UIManager.getColor( "Table.selectionForeground" ) );
		}

		return c;
	}

	/**	Is anything selected which can be cut/copied? */

	public boolean isTextSelected()
	{
		return ( getSelectedRowCount() > 0 );
	};

	/**	Set the table title.
	 *
	 *	@param	title	The table title.
	 *
	 *	<p>
	 *	The title is used for printing and file saving purposes.
	 *	</p>
	 */

	public void setTitle( String title )
	{
		this.title	= title;
	}

	/**	Get the table title.
	 *
	 *	@return		The table title.
	 */

	public String getTitle()
	{
		return title;
	}

	/**	Save table data to a named file.
	 *
	 *	@param	fileName	Name of file to which to save results.
	 */

	public void saveToFile( String fileName )
	{
		SaveTableData.saveTableDataToFile( this , title , true , fileName );
	}

	/**	Save table data to a file using a file dialog.
	 *
	 *	@param	parentWindow	Parent window for file dialog.
	 *
	 *	<p>
	 *	Runs a file dialog to get the name of the file to which to
	 *	save results.
	 *	</p>
	 */

	public void saveToFile( Window parentWindow )
	{
		SaveTableData.saveTableDataToFile
		(
			parentWindow ,
			this ,
			title ,
			true
		);
	}

	/**	Enable or disable copying column headers to clipboard.
	 *
	 *	@param	copyColumnHeaders	true to copy column headers to clipboard.
	 */

	public void setCopyColumnHeaders( boolean copyColumnHeaders )
	{
		this.copyColumnHeaders	= copyColumnHeaders;
	}

	/**	Get status of copying column headers to clipboard.
	 *
	 *	@return		true if copying column headers to clipboard is enabled.
	 */

	public boolean getCopyColumnHeaders()
	{
		return copyColumnHeaders;
	}

	/**	Checks if "select all" enabled.
	 *
	 *	@return		returns true if select all enabled.
	 */

	public boolean isSelectAllEnabled()
	{
		return true;
	}

	/** Unselect selection. */

	public void unselect()
	{
		if ( isUnselectEnabled() )
		{
			getSelectionModel().clearSelection();
		}
	}

	/**	Is unselect enabled? */

	public boolean isUnselectEnabled()
	{
		return !getSelectionModel().isSelectionEmpty();
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

