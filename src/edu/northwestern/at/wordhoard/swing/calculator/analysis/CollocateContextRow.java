package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.html.*;
import edu.northwestern.at.utils.*;

/** Holds collocate context entries for tabular display. */

public class CollocateContextRow implements SortedTableModel.Row
{
	/**	The unique word tag for this collocate. */

	protected String wordTag;

	/**	The word path in which the collocation occurs. */

	protected String wordPath;

	/**	The collocation context text. */

	protected String contextText;

	/**	The context text as a plain (non-html) string. */

	protected String plainContextText;

	/**	An empty string to handle requests for invalid columns. */

	protected static final String emptyString	= "";

	/**	Row ID.  */

	protected int rowID	= 0;

	/**	Row ID generator base.  */

	protected static int uniqueRowIDBase	= 0;

	/**	Constructs a new row.
	 *
	 *	@param	wordTag			Word tag of collocate.
	 *	@param	wordPath		Word path for collocate.
	 *	@param	contextText		The context text.
	 */

	public CollocateContextRow
	(
		String wordTag ,
		String wordPath ,
		String contextText
	)
	{
		this.wordTag			= wordTag;
		this.wordPath			= wordPath;
		this.contextText		= contextText;

		this.plainContextText	= HTMLUtils.stripHTML( contextText );
		this.rowID				= uniqueRowIDBase++;
	}

	/**	Gets the value of a column.
	 *
	 *	@param	columnIndex		Column index.
	 *
	 *	@return					Column value.
	 */

	public Object getValue( int columnIndex )
	{
		switch ( columnIndex )
		{
			case 0	: return wordPath;
			case 1	: return contextText;
			case 2	: return wordTag;
			default	: return emptyString;
		}
	}

	/**	Gets the value of a column for sorting.
	 *
	 *	@param	columnIndex		Column index.
	 *
	 *	@return					Column value.
	 */

	public Object getSortableValue( int columnIndex )
	{
		switch ( columnIndex )
		{
			case 0	: return wordTag + ":" + wordPath;
			case 1	: return contextText;
			case 2	: return wordTag;
			default	: return emptyString;
		}
	}

	/**	Compares this row to another row using a specified column.
	 *
	 *	@param	obj				The other row.
	 *
	 *	@param	columnIndex		Column index.
	 *
	 *	@return					< 0 if this row < other row,
	 *							0 if this row = other row,
	 *							> 0 if this row > other row.
	 *
	 */

	public int compareTo( SortedTableModel.Row obj , int columnIndex )
	{
		CollocateContextRow other = (CollocateContextRow)obj;

		return
			StringUtils.compareIgnoreCase
			(
				(String)getSortableValue( columnIndex ) ,
				(String)other.getSortableValue( columnIndex )
			);
	}

	/**	Gets the associated unique ID for this row.
	 *
	 *	@return					Unique ID for this row.
	 *							This should be something unique
	 *							to each table row.
	 */

	public Object getUniqueRowID()
	{
		return new Integer( rowID );
	}

	/**	Get word tag for this row.
	 *
	 *	@return		Word tag for this row.
	 */

	public String getWordTag()
	{
		return wordTag;
	}

	/** Display text associated with node.
	 *
	 *	@return		The context text.
	 */

	public String toString()
	{
		return plainContextText;
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

