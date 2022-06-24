package edu.northwestern.at.wordhoard.swing.concordance;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.utils.db.*;

/**	A concordance grouping options panel.
 */

class GroupingPanel extends JPanel {

	/**	Font. */

	private Font font;

	/**	Search criteria. */

	private SearchCriteria sq;

	/**	Parent concordance panel. */

	private ConcordancePanel parentPanel;

	private Row row;

	/**	Creates a new concordance grouping options panel.
	 *
	 *	@param	font					Font for small combo box controls and
	 *									labels.
	 *
	 *	@param	sq						The search criteria.
	 *
	 *	@param	groupingOptionsArray	Array of initial grouping options.
	 *
	 *	@param	parentPanel				Parent concordance panel.
	 *
	 *	@throws	PersistenceException
	 */

	GroupingPanel (Font font, SearchCriteria sq,
		GroupingOptions[] groupingOptionsArray, ConcordancePanel parentPanel)
			throws PersistenceException
	{
		this.font = font;
		this.sq = sq;
		this.parentPanel = parentPanel;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
		for (int i = 0; i < groupingOptionsArray.length; i++) {
			GroupingOptions groupingOptions = groupingOptionsArray[i];
			row = new Row(font, sq, this, groupingOptions);
			add(row);
		}
		adjust();
		handleNewGroupingOptions();
	}

	/**	Gets the rows.
	 *
	 *	@return			Array of all rows.
	 */

	Row[] getRows () {
		Component[] components = getComponents();
		if (components == null || components.length == 0) {
			return new Row[0];
		} else {
			Row[] result = new Row[components.length];
			for (int i = 0; i < components.length; i++) {
				result[i] = (Row)components[i];
			}
			return result;
		}
	}

	/**	Finds a row component.
	 *
	 *	@param	row		Row component.
	 *
	 *	@return			Index of row in panel, or -1 if not  found.
	 */

	private int findRow (Row row) {
		Row[] rows = getRows();
		for (int i = 0; i < rows.length; i++) {
			if (row == rows[i]) return i;
		}
		return -1;
	}

	/**	Adjusts the panel.
	 *
	 *	<p>The panel is revalidated and repainted.
	 *
	 *	<p>The minus button of the first row is enabled iff there is more
	 *	than one row in the panel.
	 */

	private void adjust () {
		Dimension size = getPreferredSize();
		size.width = 100000;
		setMaximumSize(size);
		revalidate();
		repaint();
		Row[] rows = getRows();
		int numRows = rows.length;
		Row firstRow = rows[0];
		firstRow.setMinusEnabled(numRows > 1);
	}

	/**	Handles new grouping options.
	 *
	 *	@throws	PersistenceException
	 */

	void handleNewGroupingOptions ()
		throws PersistenceException
	{
		Row[] rows = getRows();
		GroupingOptions[] groupingOptionsArray =
			new GroupingOptions[rows.length];
		for (int i = 0; i < rows.length; i++) {
			Row row = rows[i];
			groupingOptionsArray[i] = row.getGroupingOptions();
		}
		parentPanel.handleNewGroupingOptions(groupingOptionsArray);
	}

	/**	Adds a new row.
	 *
	 *	@param	row		Row after which to add new row, or null
	 *					to add the new row at the end.
	 *
	 *	@throws	PersistenceException
	 */

	void addRow (Row row)
		throws PersistenceException
	{
		int index = row == null ? -1 : findRow(row) + 1;
		final Row newRow = new Row(font, sq, this, null);
		add(newRow, index);
		adjust();
		handleNewGroupingOptions();
	}

	/**	Removes a row.
	 *
	 *	@param	row		Row to be removed.
	 *
	 *	@throws	PersistenceException
	 */

	void removeRow (Row row)
		throws PersistenceException
	{
		remove(row);
		adjust();
		handleNewGroupingOptions();
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

