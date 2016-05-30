package edu.northwestern.at.wordhoard.swing.calculator.widgets;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.swing.*;

/**	WordHoard sorted table model.
 */

public class WordHoardSortedTableModel extends SortedTableModel
{
	/**	Background color for table headers.
	 *	Same as JPanel background color.
	 */

	protected static Color panelBackgroundColor	=
		new JPanel().getBackground();

	/**	Constructs a new empty sorted table model.
	 *
	 *	@param	columnNames				Column names.
	 *
	 *	@param	initialSortColumn		The initial sort column.
	 *
	 *	@param	initialSortAscending	The initial sort order.
	 */

	public WordHoardSortedTableModel
	(
		String[] columnNames ,
		int initialSortColumn ,
		boolean initialSortAscending
	)
	{
		super( columnNames , initialSortColumn , initialSortAscending );

		setHeaderBackground( panelBackgroundColor );
	}

	/**	A row comparator.
	 *
	 *	<p>
	 *	Rows are compared using the current sort
	 *	column.  Unlike the base class, the unique ID is not used
	 *	to break ties.
	 *	</p>
	 */

	protected Comparator wordHoardRowComparator =
		new Comparator()
		{
			public int compare( Object o1 , Object o2 )
			{
				Row row1 = (Row)o1;
				Row row2 = (Row)o2;

				int c = row1.compareTo( row2 , sortColumn );

				return sortAscending ? c : -c;
			}
		};

	/**	Set the WordHoard row comparator.
	 */

	public void setWordHoardComparator()
	{
		setComparator( wordHoardRowComparator );
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

