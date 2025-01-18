package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.SortedTableModel;

/** Holds frequency analysis entries for tabular display. */

public class FrequencyAnalysisDataRow implements SortedTableModel.Row
{
	/**	The text item being analyzed. */

	protected String textItem;

	/**	The analysis results for this row. */

	protected Object[] rowData;

	/**	Constructs a new row.
	 *
	 *	@param	textItem	The text item which acts as a label for this row.
	 *	@param	rowData		The analysis results for this row.
	 */

	public FrequencyAnalysisDataRow( String textItem , double[] rowData )
	{
		this.textItem	= textItem;
		this.rowData	= new Double[ rowData.length ];

		for ( int i = 0 ; i < rowData.length ; i++ )
		{
			this.rowData[ i ]	= Double.valueOf( rowData[ i ] );
		}
	}

	/**	Constructs a new row.
	 *
	 *	@param	textItem	The text item which acts as a label for this row.
	 *	@param	textItem2	A secondary text item.
	 *	@param	rowData		The analysis results for this row.
	 */

	public FrequencyAnalysisDataRow
	(
		String textItem ,
		String textItem2 ,
		double[] rowData
	)
	{
		this.textItem		= textItem;
		this.rowData		= new Object[ rowData.length + 1 ];
		this.rowData[ 0 ]	= textItem2;

		for ( int i = 0 ; i < rowData.length ; i++ )
		{
			this.rowData[ i + 1 ]	= Double.valueOf( rowData[ i ] );
		}
	}

	/**	Constructs a new row.
	 *
	 *	@param	textItem	The text item which acts as a label for this row.
	 *	@param	rowData		The analysis results for this row.
	 */

	public FrequencyAnalysisDataRow( String textItem , Object[] rowData )
	{
		this.textItem	= textItem;
		this.rowData	= new Object[ rowData.length ];

		for ( int i = 0 ; i < rowData.length ; i++ )
		{
			this.rowData[ i ]	= rowData[ i ];
		}
	}

	/**	Constructs a new row.
	 *
	 *	@param	textItem	The text item which acts as a label for this row.
	 *	@param	textItem2	A secondary text item.
	 *	@param	rowData		The analysis results for this row.
	 */

	public FrequencyAnalysisDataRow
	(
		String textItem ,
		String textItem2 ,
		Object[] rowData
	)
	{
		this.textItem		= textItem;
		this.rowData		= new Object[ rowData.length + 1 ];
		this.rowData[ 0 ]	= textItem2;

		for ( int i = 0 ; i < rowData.length ; i++ )
		{
			this.rowData[ i + 1 ]	= rowData[ i ];
		}
	}

	/**	Gets the value of a column.
	 *
	 *	@param	columnIndex		Column index.
	 *
	 *	@return					Column value.
	 */

	public Object getValue( int columnIndex )
	{
		if ( columnIndex == 0 )
		{
			return textItem;
		}
		else
		{
			return rowData[ columnIndex - 1 ];
		}
	}

	/**	Compares this row to another row using a specified column.
	 *
	 *	@param	obj				The other row.
	 *
	 *	@param	columnIndex		Column index.
	 *
	 *	@return					&lt; 0 if this row &lt; other row,
	 *							0 if this row = other row,
	 *							&gt; 0 if this row &gt; other row.
	 *
	 */

	public int compareTo( SortedTableModel.Row obj , int columnIndex )
	{
		FrequencyAnalysisDataRow other = (FrequencyAnalysisDataRow)obj;

		Object o1	= getValue( columnIndex );
		Object o2	= other.getValue( columnIndex );

		if ( o1 instanceof String )
		{
			return StringUtils.compareIgnoreCase( (String)o1 , (String)o2  );
		}
		else
		{
			double d1	= ((Double)o1).doubleValue();
			double d2	= ((Double)o2).doubleValue();
			double diff	= 0.0D;

			if ( Double.isNaN( d1 ) )
			{
				if ( !Double.isNaN( d2 ) ) diff = -1.0;
			}
			else if ( Double.isNaN( d2 ) )
			{
				if ( !Double.isNaN( d1 ) ) diff = 1.0;
			}
			else
			{
				diff = d1 - d2;
			}

    		if ( diff == 0.0D ) return 0;
			else if ( diff > 0.0D ) return 1;
			else return -1;
		}
	}

	/**	Gets the associated unique ID for this row.
	 *
	 *	@return					Unique ID for this row.
	 *							This should be something unique
	 *							to each table row.
	 */

	public Object getUniqueRowID()
	{
		return getValue( 0 );
	}

	/** Display text associated with node. */

	public String toString()
	{
		return (String)getValue( 0 );
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

