package edu.northwestern.at.wordhoard.swing.bibtool;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.bibtool.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.utils.swing.*;

/**	A search results grouping options panel.
 */

public class WorkSetGroupingOptionsPanel extends JPanel {

	/**	The corpus. */

	private Corpus corpus;

	/**	Current grouping options. */

	private GroupingWorkOptions groupingOptions;

	/**	The parent search results panel. */

	private WorkSetPanel parentPanel;

	/**	Group by combo box. */

	private SmallComboBox groupByComboBox;

	/**	Order by combo box. */

	private SmallComboBox orderByComboBox;

	/**	Ascending/descending combo box. */

	private SmallComboBox upDownComboBox;

	/**	Creates a new search results grouping options panel.
	 *
	 *	@param	font				Font for small combo box controls and
	 *								labels.
	 *
	 *	@param	groupingOptions		Initial grouping options.
	 *
	 *	@param	parentPanel			Parent search results panel.
	 */

	public WorkSetGroupingOptionsPanel (Font font,
		GroupingWorkOptions groupingOptions,
		WorkSetPanel parentPanel)
	{

		super();
		this.groupingOptions = groupingOptions;
		this.parentPanel = parentPanel;

		JLabel groupByLabel =
			WordHoard.getSmallComboBoxLabel("Group by: ", font);
		groupByComboBox = new SmallComboBox(font);
		for (int i = 0; i < GroupingWorkOptions.GROUP_BY_NAMES.length; i++)
			groupByComboBox.addItem(GroupingWorkOptions.GROUP_BY_NAMES[i]);
		groupByComboBox.setSelectedIndex(groupingOptions.getGroupBy());
		groupByComboBox.addActionListener(actionListener);

		JLabel orderByLabel =
			WordHoard.getSmallComboBoxLabel("Order by: ", font);
		orderByComboBox = new SmallComboBox(font);
		for (int i = 0; i < GroupingWorkOptions.ORDER_BY_NAMES.length; i++)
			orderByComboBox.addItem(GroupingWorkOptions.ORDER_BY_NAMES[i]);
		orderByComboBox.setSelectedIndex(groupingOptions.getOrderBy());
		orderByComboBox.addActionListener(actionListener);

		upDownComboBox = new SmallComboBox(font);
		for (int i = 0; i < GroupingWorkOptions.UP_DOWN_NAMES.length; i++)
			upDownComboBox.addItem(GroupingWorkOptions.UP_DOWN_NAMES[i]);
		upDownComboBox.setSelectedIndex(groupingOptions.getUpDown());
		upDownComboBox.addActionListener(actionListener);

		adjust();

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(groupByLabel);
		add(groupByComboBox);
		add(Box.createHorizontalStrut(15));
		add(orderByLabel);
		add(orderByComboBox);
		add(Box.createHorizontalStrut(15));
		add(upDownComboBox);

		Dimension size = getPreferredSize();
		size.width = 100000;
		setMaximumSize(size);
	}

	/**	Sets the order by option.
	 *
	 *	@param	orderBy		New order by option.
	 */

	private void setOrderBy (int orderBy) {
		groupingOptions.setOrderBy(orderBy);
		orderByComboBox.setSelectedIndex(orderBy);
	}

	/**	Sets the ascending/descending option.
	 *
	 *	@param	upDown		New ascending/descending option.
	 */

	private void setUpDown (int upDown) {
		groupingOptions.setUpDown(upDown);
		upDownComboBox.setSelectedIndex(upDown);
	}

	/**	Adjusts the menus (enables/disables options)
	 *
	 *	@param	groupBy				New group by option.
	 *
	 *	@param	allWorksHaveDates	True if corpus supports dates.
	 */

	private void adjustMenus (int groupBy, boolean allWorksHaveDates) {
		switch (groupBy) {
			case GroupingWorkOptions.GROUP_BY_NONE:
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_WORK,true);
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_AUTHOR,true);
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_DATE,true);
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_COUNT,false);
				break;
			case GroupingWorkOptions.GROUP_BY_AUTHOR:
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_WORK,false);
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_AUTHOR,true);
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_DATE,false);
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_COUNT,true);
				break;
			case GroupingWorkOptions.GROUP_BY_YEAR:
			case GroupingWorkOptions.GROUP_BY_DECADE:
			case GroupingWorkOptions.GROUP_BY_QCENTURY:
			case GroupingWorkOptions.GROUP_BY_CENTURY:
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_WORK,false);
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_AUTHOR,false);
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_DATE,true);
				orderByComboBox.setEnabled(GroupingWorkOptions.ORDER_BY_COUNT,true);
				break;
		}
		upDownComboBox.setEnabled(GroupingWorkOptions.ASCENDING,true);
	}

	/**	Adjusts the panel after a change in grouping options.
	 */


	private void adjust () {
		int groupBy = groupingOptions.getGroupBy();
		int orderBy = groupingOptions.getOrderBy();
		int upDown = groupingOptions.getUpDown();
//		boolean allWorksHaveDates = corpus.allWorksHaveDates();
//BP		adjustMenus(groupBy, allWorksHaveDates);
		adjustMenus(groupBy, true);
		switch (groupBy) {
			case GroupingWorkOptions.GROUP_BY_NONE:
				if (orderBy ==  GroupingWorkOptions.ORDER_BY_COUNT)
						setOrderBy(orderBy = GroupingWorkOptions.ORDER_BY_WORK);
				break;
			case GroupingWorkOptions.GROUP_BY_AUTHOR:
				if (orderBy == GroupingWorkOptions.ORDER_BY_WORK || orderBy == GroupingWorkOptions.ORDER_BY_DATE)
						setOrderBy(orderBy = GroupingWorkOptions.ORDER_BY_AUTHOR);
				break;
			case GroupingWorkOptions.GROUP_BY_YEAR:
			case GroupingWorkOptions.GROUP_BY_DECADE:
			case GroupingWorkOptions.GROUP_BY_QCENTURY:
			case GroupingWorkOptions.GROUP_BY_CENTURY:
				if (orderBy == GroupingWorkOptions.ORDER_BY_WORK || orderBy == GroupingWorkOptions.ORDER_BY_AUTHOR)
						setOrderBy(orderBy = GroupingWorkOptions.ORDER_BY_DATE);
				break;
		}
	}

	/**	Action listener for combo box actions.
	 *
	 *	<p>The parent panel's handleNewGroupingOptions method is invoked
	 *	to handle the change in grouping options.
	 */

	private ActionListener actionListener =
		new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				try {
					groupingOptions =
						new GroupingWorkOptions(
							groupByComboBox.getSelectedIndex(),
							orderByComboBox.getSelectedIndex(),
							upDownComboBox.getSelectedIndex());
					adjust();
					parentPanel.handleNewGroupingOptions(groupingOptions);
				} catch (Exception e) {
					Err.err(e);
				}
			}
		};

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

