package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

/**	Table model that eliminates hidden (zero width) columns.
 */

public class TableModelMapper extends AbstractTableModel
{
	/**	Table being mapped.
	 */

	protected JTable table;

	/**	Map from visible column index to all columns index.
	 */

	protected int[] visibleToAll;

	/**	Create a table model that eliminates hidden columns.
	 *
	 *	@param	table	The table with possibly empty columns.
	 */

	public TableModelMapper( JTable table )
	{
		this.table	= table;
								//	Create map from visible columns
								//	to all all columns.

		ArrayList visibleColumnsList	= new ArrayList();

		for ( int i = 0 ; i < table.getColumnCount() ; i++ )
		{
			TableColumn column =
				table.getColumnModel().getColumn( i );

			if ( column.getPreferredWidth() > 0 )
			{
				visibleColumnsList.add
				(
					Integer.valueOf( i )
				);
			}
		}

		visibleToAll	= new int[ visibleColumnsList.size() ];

		for ( int i = 0 ; i < visibleColumnsList.size() ; i++ )
		{
			visibleToAll[ i ]	=
				((Integer)visibleColumnsList.get( i )).intValue();
		}
	}

	/** Get number of rows in table.
	 *
	 *  @return     The number of rows in the table.
	 */

	public int getRowCount()
	{
		return table.getRowCount();
	}

	/** Get number of columns in table.
	 *
	 *  @return     The number of columns in the table.
	 */

	public int getColumnCount()
	{
		return visibleToAll.length;
	}

	/**	Get table value at a specified row and column.
	 *
	 *	@param	row		The table row.
	 *	@param	column	The table column.
	 *
	 *	@return			The table at the specified row and column
	 *					in then table, or null if none.
	 */

	public Object getValueAt( int row , int column )
	{
		Object result	= null;

		if ( ( row >= 0 ) && ( row <= getRowCount() ) )
		{
			if ( ( column >= 0 ) && ( column <= getColumnCount() ) )
			{
				result	=
					table.getValueAt( row , visibleToAll[ column ] );
			}
		}

		return result;
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


