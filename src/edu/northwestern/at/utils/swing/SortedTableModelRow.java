package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

/** Simple concrete implementation of sorted table model row.
 *
 *	<p>
 *	Defines a simple concrete implementation of the SortedTableModel.Row
 *	interface.  The unique row ID may be specified as the value of a
 *	column, or as a separate value.  The column values may be any
 *	objects which implement the Comparable interface.
 *	</p>
 */

public class SortedTableModelRow implements SortedTableModel.Row
{
	/**	The data for this row. */

	protected Comparable[] rowData;

	/**	The column number for the unique row identifier.
	 *
	 *	Set negative to use separate uniqueID value.
	 */

	protected int uniqueIDColumn;

	/**	The unique row identifier value if not present in the row data. */

	protected Object uniqueID;

	/**	Create a row from an array of objects.
	 *
	 *	@param	rowData		Array of objects to use as row data.
	 *						The first entry in the row data is assumed
	 *						to contain a unique row identifier.
	 */

	public SortedTableModelRow( Comparable[] rowData )
	{
		this.rowData		= rowData;
		this.uniqueIDColumn	= 0;
		this.uniqueID		= null;
	}

	/**	Create a row from an array of objects.
	 *
	 *	@param	rowData			Array of objects to use as row data.
	 *							The first entry is assumed to be a
	 *							unique row identifier.
	 *
	 *	@param	uniqueIDColumn	Column number of the unique row ID value.
	 */

	public SortedTableModelRow( Comparable[] rowData , int uniqueIDColumn )
	{
		this.rowData		= rowData;
		this.uniqueIDColumn	= uniqueIDColumn;
		this.uniqueID		= null;
	}

	/**	Create a row from an array of objects.
	 *
	 *	@param	rowData		Array of objects to use as row data.
	 *						The first entry is assumed to be a
	 *						unique row identifier.
	 *
	 *	@param	uniqueID	Object containing the unique ID for this row.
	 */

	public SortedTableModelRow( Comparable[] rowData , Object uniqueID )
	{
		this.rowData		= rowData;
		this.uniqueIDColumn	= -1;
		this.uniqueID		= uniqueID;
	}

	/**	Get value in specified column.
	 *
	 *	@param	columnIndex		The column index of the value to return.
	 *
	 *	@return					The data value for the specified
	 *							column, if the column index is valid,
	 *							or null if the column index is invalid.
	 */

	public Object getValue( int columnIndex )
	{
		Object result	= null;

		if ( ( columnIndex >= 0 ) && ( columnIndex < rowData.length ) )
		{
			result	= rowData[ columnIndex ];
		}

		return result;
	}

	/**	Compare value in specified column with same column in another row.
	 *
	 *	@param	other			The other row.
	 *	@param	columnIndex		The index of the column to compare.
	 *
	 *	@return			        < 0: this row's value < other row's value
	 *	        		        = 0: this row's value == other row's value
	 *	         		        > 0: this row's value > other row's value
	 */

	public int compareTo( SortedTableModel.Row other , int columnIndex )
	{
		int result	= -1;

		if ( ( columnIndex >= 0 ) && ( columnIndex < rowData.length ) )
		{
			result	=
				rowData[ columnIndex ].compareTo(
					(Comparable)other.getValue( columnIndex ) );
		}

		return result;
	}

	/**	Get the unique row identifier.
	 *
	 *	@return		The unique row identifier.
	 */

	public Object getUniqueRowID()
	{
		Object result;

		if ( uniqueIDColumn < 0 )
		{
			result	= uniqueID;
		}
		else
		{
			result	= rowData[ uniqueIDColumn ];
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


