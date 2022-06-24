package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.swing.icons.*;
import edu.northwestern.at.utils.*;

/**	Sorted table model.
 *
 *	<p>This class extends AbstractTableModel to support sorted tables.
 *
 *	<p>The table model is linked to a single partner JTable table
 *	view. The view is modified to use a custom header cell renderer
 *	which includes a filled triangle pointing up or down to indicate
 *	the current sort column and sort order. Single clicks on sortable
 *	column headers resort on that column in ascending order. Shift-clicks
 *	on sortable column headers resort on that column in descending order.
 *
 *	<p>Note: We only support one view at a time. It would be possible to
 *	support multiple views, each with its own sort state, but we don't
 *	currently need this so we haven't done it.
 */

public class SortedTableModel extends AbstractTableModel {

	/**	Ascending sort order triangle. */

	protected static final Icon upTriangle =
		new FilledTriangleIcon(true, 6);

	/**	Descending sort order triangle. */

	protected static final Icon downTriangle =
		new FilledTriangleIcon(false, 6);

	/**	Background color for column headers.
	 *
	 *	Default is pale blue.
	 */

	protected Color headerBackgroundColor =
		new Color(200, 255, 255);

	/**	Row interface for sorted tables.
	 *
	 *	<p>All row objects in a sorted table must implement this interface. */

	public interface Row {

		/**	Gets the value of a column.
		 *
		 *	@param	columnIndex		Column index.
		 *
		 *	@return					Column value.
		 */

		public Object getValue (int columnIndex);

		/**	Compares this row to another row using a specified column.
		 *
		 *	@param	other			The other row.
		 *
		 *	@param	columnIndex		Column index.
		 *
		 *	@return					< 0 if this row < other row,
		 *							0 if this row = other row,
		 *							> 0 if this row > other row.
		 */

		public int compareTo (Row other, int columnIndex);

		/** Get unique identifier for each row.
		 *
		 *	@return		The unique identifier.
		 *
		 *	<p>
		 *	The unique identifier is used as a secondary key when
		 *	sorting table rows.  The object used for the unique
		 *	identifier must implement the toString() method.
		 *	</p>
		 */

		 public Object getUniqueRowID();
	}

	/**	List of rows in the table. */

	protected ArrayList rows = new ArrayList();

	/**	Column names. */

	protected String[] columnNames;

	/**	Sortable column flags. Column i is sortable if and only if
	 *	sortableColumns[i] is true. May be null to indicate that all
	 *	columns are sortable.
	 */

	protected boolean[] sortableColumns;

	/**	The current sort column. */

	protected int sortColumn;

	/**	The current sort order: True if ascending, false if descending. */

	protected boolean sortAscending;

	/**	The linked JTable view. */

	protected JTable view;

	/**	The view's table header. */

	protected JTableHeader tableHeader;

	/**	The comparator for sorting rows. */

	protected Comparator comparator;

	/**	Constructs a new empty sorted table model.
	 *
	 *	@param	columnNames				Column names.
	 *
	 *	@param	initialSortColumn		The initial sort column.
	 *
	 *	@param	initialSortAscending	The initial sort order.
	 */

	public SortedTableModel (String[] columnNames,
		int initialSortColumn, boolean initialSortAscending)
	{
		super();
		this.columnNames = columnNames;
		sortColumn = initialSortColumn;
		sortAscending = initialSortAscending;
		comparator = defaultComparator;
	}

	/**	A row comparator. Rows are compared using the current sort
	 *	column and then the unique ID if the two rows compare equal
	 *	on the current sort column.
	 */

	protected Comparator defaultComparator =
		new Comparator() {
			public int compare (Object o1, Object o2) {
				Row row1 = (Row)o1;
				Row row2 = (Row)o2;
				int c = row1.compareTo(row2, sortColumn);
				if ( c == 0 ) {
					String name1 = row1.getUniqueRowID().toString();
					String name2 = row2.getUniqueRowID().toString();
					c = StringUtils.compareIgnoreCase( name1 , name2 );
				}
				return sortAscending ? c : -c;
			}
		};

	/**	Renderer for table header cells. This renderer uses a triangle
	 *	pointing up or down to indicate the current sort column and
	 *	sort order.
	 */

	protected TableCellRenderer headerRenderer =
		new TableCellRenderer () {
			public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column)
			{
				JLabel strLabel	= new JLabel( value.toString() );
//				JLabel strLabel;

				column	= view.convertColumnIndexToModel( column );

				if	(	( column == sortColumn ) &&
						( ( sortableColumns == null ) ||
							sortableColumns[ column ] ) )
				{
					StringPlusIcon stringPlusIcon	=
						new StringPlusIcon
						(
							value.toString() ,
							sortAscending ? upTriangle : downTriangle
						);

					stringPlusIcon.addIcon( strLabel );
/*
					strLabel	=
						new JLabel
						(
							value.toString() ,
							sortAscending ? upTriangle : downTriangle ,
							SwingConstants.LEFT
						);
*/
				}
/*
				else
				{
					strLabel	= new JLabel( value.toString() );
				}
*/
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
				panel.add(Box.createHorizontalStrut(3));
				panel.add(strLabel);
				panel.add(Box.createHorizontalGlue());
/*
				if (column == sortColumn) {
					panel.add(sortAscending ? (JComponent)upTriangle :
						(JComponent)downTriangle);
				}
*/
/*
				if (column == sortColumn)
				{
					panel.add
					(
						sortAscending ?
							new JLabel( upTriangle ) :
							new JLabel( downTriangle )
					);
				}
*/
				panel.add(Box.createHorizontalStrut(3));
				panel.setBorder(BorderFactory.createLineBorder(Color.gray));
				panel.setBackground(headerBackgroundColor);
				return panel;
			}
		};

	/**	Set background color for table header.
	 *
	 *	@param	backgroundColor		The background color.
	 */

	public void setHeaderBackground (Color backgroundColor) {
		headerBackgroundColor	= backgroundColor;
	}

	/**	Set comparator for table rows.
	 *
	 *	@param	comparator	The comparator.
	 */

	public void setComparator (Comparator comparator) {
		this.comparator	= comparator;
	}

	/**	Set default comparator for table rows.
	 */

	public void setDefaultComparator () {
		this.comparator	= defaultComparator;
	}

	/**	Handles mouse clicks in the table header. A click resorts the table
	 *	in ascending order on the clicked column. Shift-click resorts the
	 *	table in descending order on the clicked column.
	 */

	protected MouseAdapter headerClick =
		new MouseAdapter() {
			public void mouseClicked (MouseEvent event) {
				int column = view.convertColumnIndexToModel(
					view.getColumnModel().getColumnIndexAtX(
					event.getX()));
				if (column == -1) return;
				if (sortableColumns != null && !sortableColumns[column]) return;
				if (event.getClickCount() == 1) {
					boolean ascending = (event.getModifiersEx() &
						InputEvent.SHIFT_DOWN_MASK) == 0;
					sort(column, ascending);
					tableHeader.repaint();
				}
			}
		};

   	/**	Initializes the column sizes.
   	 *
   	 *	@param	longValues		An array of long values for the cell contents.
   	 *	@param	setMaxWidth		Set maximum cell width to size of long values.
   	 */

	protected void initColumnSizes
	(
		Object[] longValues ,
		boolean setMaxWidth
	)
	{
		initColumnSizes( longValues , setMaxWidth , false );
    }

   	/**	Initializes the column sizes.
   	 *
   	 *	@param	longValues		An array of long values for the cell contents.
   	 *	@param	setMaxWidth		Set maximum cell width to size of long values.
   	 *	@param	setMinWidth		Set minimum cell width to size of long values.
   	 */

	protected void initColumnSizes
	(
		Object[] longValues ,
		boolean setMaxWidth ,
		boolean setMinWidth
	)
	{
		int savedSortColumn	= sortColumn;
		sortColumn			= -1;

		for ( int i = 0 ; i < getColumnCount() ; i++ )
		{
			if ( longValues[ i ] != null )
			{
				TableColumn column	= view.getColumnModel().getColumn(i);

				Component comp		=
					headerRenderer.getTableCellRendererComponent
					(
						null ,
						column.getHeaderValue() ,
						false ,
						false ,
						0 ,
						0
					);

				int headerWidth	=
					comp.getPreferredSize().width + upTriangle.getIconWidth();

				TableCellRenderer renderer	=
					view.getDefaultRenderer( getColumnClass( i ) );

				comp			=
					renderer.getTableCellRendererComponent
					(
						view ,
						longValues[ i ] ,
						false ,
						false ,
						0 ,
						i
					);

				int cellWidth	=
					Math.max( comp.getPreferredSize().width , headerWidth );

				column.setMinWidth( cellWidth );

				if ( setMinWidth )
					column.setPreferredWidth( cellWidth );

				if ( setMaxWidth )
					column.setMaxWidth( cellWidth );
			}
		}

		sortColumn	= savedSortColumn;
    }

	/**	Gets the row count.
	 *
	 *	@return		The row count.
	 */

	public int getRowCount () {
		return rows.size();
	}

	/**	Gets the column count.
	 *
	 *	@return		The column count.
	 */

	public int getColumnCount () {
		return columnNames.length;
	}

	/**	Gets the value of a table cell.
	 *
	 *	@param	rowIndex		Row index.
	 *
	 *	@param	columnIndex		Column index.
	 *
	 *	@return					Cell value.
	 */

	public Object getValueAt (int rowIndex, int columnIndex) {
		Row row = (Row)rows.get(rowIndex);
		return row.getValue(columnIndex);
	}

	/**	Gets a row.
	 *
	 *	@param	rowIndex		Row index.
	 *
	 *	@return					The row.
	 */

	public Row getRow (int rowIndex) {
		return (Row)rows.get(rowIndex);
	}

	/**	Gets a column name.
	 *
	 *	@param	columnIndex		Column index.
	 *
	 *	@return					The column name.
	 */

	public String getColumnName (int columnIndex) {
		return columnNames[columnIndex];
	}

	/**	Gets the column names.
	 *
	 *	@return					Array of column names.
	 */

	public String[] getColumnNames()
	{
		return columnNames;
	}

	/**	Gets the sort column.
	 *
	 *	@return		The current sort column.
	 */

	public int getSortColumn () {
		return sortColumn;
	}

	/**	Gets the sort order.
	 *
	 *	@return		True if ascending, false if descending.
	 */

	public boolean getSortAscending () {
		return sortAscending;
	}

	/**	Resorts the table. */

	public void resort () {
		Collections.sort(rows, comparator);
		fireTableDataChanged();
	}

	/**	Sorts the table.
	 *
	 *	@param	sortColumn		Sort column.
	 *
	 *	@param	sortAscending	Sort order.
	 */

	public void sort (final int sortColumn, final boolean sortAscending) {
		this.sortColumn = sortColumn;
		this.sortAscending = sortAscending;
		resort();
	}

	/**	Sets new table data.
	 *
	 *	<p>The old table rows are discarded, then the new ones are sorted and
	 *	set.
	 *
	 *	@param	collection		Collection of rows.
	 */

	public void setData (Collection collection) {
		rows = new ArrayList(collection);
		resort();
	}

	/**	Adds a row.
	 *
	 *	<p>The row is added in the proper position to maintain the
	 *	current sort order.
	 *
	 *	@param	row		The new row.
	 *					N.B.: make sure your chosen comparator properly
	 *					handles duplicate values in rows.
	 */

	public void add (Row row) {
		int index = Collections.binarySearch(rows, row, comparator);
//		if (index >= 0) return;
//		index = -index - 1;
		if (index < 0) index = -index - 1;
		rows.add(index, row);
		fireTableRowsInserted(index, index);
	}

	/**	Removes a row.
	 *
	 *	@param	row		The row to be removed.
	 *					N.B.: the first row which compares equal
	 *					to the given row is removed.
	 */

	public void remove (Row row) {
		int index = Collections.binarySearch(rows, row, comparator);
		if (index < 0) return;
		rows.remove(index);
		fireTableRowsDeleted(index, index);
	}

	/**	Removes a row.
	 *
	 *	@param	rowIndex	The index of the row to be removed.
	 */

	public void remove (int rowIndex) {
		rows.remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	/**	Sets the view.
   	 *
   	 *	<p>If the longValues parameter is not null, the column preferred
   	 *	widths in the linked JTable view are initialized
   	 *	so that each column is wide enough to accomodate the column name
   	 *	and a sort order triangle indicator, and is also wide enough to
   	 *	accomodate a long cell value.
	 *
	 *	@param	view				The linked view.
	 *
	 *	@param	sortableColumns		Sortable column flags. Column i is sortable
	 *								if and only if sortableColumns[i] is true.
	 *								May be null to indicate that all columns are
	 *								sortable.
   	 *
   	 *	@param	longValues			An array of long values for the cell
   	 *								contents. The i'th element is a long value
   	 *								for column i.
   	 *
   	 *	@param	setMaxWidth			Set the maximum column widths to the
   	 *								sizes derived from the long values.
   	 *
   	 *	@param	setMinWidth			Set the minimum column widths to the
   	 *								sizes derived from the long values.
	 */

	public void setView
	(
		JTable view ,
		boolean[] sortableColumns ,
		Object[] longValues ,
		boolean setMaxWidth ,
		boolean setMinWidth
	)
	{
		this.view				= view;
		this.sortableColumns	= sortableColumns;

		tableHeader				= view.getTableHeader();

		tableHeader.setDefaultRenderer( headerRenderer );

		tableHeader.addMouseListener( headerClick );

		if ( longValues != null )
		{
			initColumnSizes( longValues , setMaxWidth , setMinWidth );
		}
	}

	/**	Sets the view.
   	 *
   	 *	<p>If the longValues parameter is not null, the column preferred
   	 *	widths in the linked JTable view are initialized
   	 *	so that each column is wide enough to accomodate the column name
   	 *	and a sort order triangle indicator, and is also wide enough to
   	 *	accomodate a long cell value.
	 *
	 *	@param	view				The linked view.
	 *
	 *	@param	sortableColumns		Sortable column flags. Column i is sortable
	 *								if and only if sortableColumns[i] is true.
	 *								May be null to indicate that all columns are
	 *								sortable.
   	 *
   	 *	@param	longValues			An array of long values for the cell
   	 *								contents. The i'th element is a long value
   	 *								for column i.
   	 *
   	 *	@param	setMaxWidth			Set the maximum column widths to the
   	 *								sizes derived from the long values.
	 */

	public void setView
	(
		JTable view ,
		boolean[] sortableColumns ,
		Object[] longValues ,
		boolean setMaxWidth
	)
	{
		setView( view , sortableColumns , longValues , setMaxWidth , false );
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

