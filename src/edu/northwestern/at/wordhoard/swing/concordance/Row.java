package edu.northwestern.at.wordhoard.swing.concordance;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;

/**	One row of the grouping options panel.
 */

class Row extends JPanel {

	/**	Grouping option info. */

	private static class GroupingInfo {
		private String name;
		private Class groupBy;
		private HashSet validOrderBy;
		private int defaultOrderBy;
		private int defaultUpDown;
		private boolean isCriterion;
		private GroupingInfo (String name, Class groupBy,
			HashSet validOrderBy, int defaultOrderBy, int defaultUpDown,
			boolean isCriterion)
		{
			this.name = name;
			this.groupBy = groupBy;
			this.validOrderBy = validOrderBy;
			this.defaultOrderBy = defaultOrderBy;
			this.defaultUpDown = defaultUpDown;
			this.isCriterion = isCriterion;
		}
		public String toString () {
			return name;
		}
	}

	/**	Valid ordering option sets. */

	private static HashSet FREQ_COUNT_ALPH = new HashSet();
	private static HashSet FREQ_COUNT_ALPH_DATE = new HashSet();
	private static HashSet FREQ_COUNT_LOC = new HashSet();
	private static HashSet COUNT_ALPH = new HashSet();
	private static HashSet COUNT_DATE = new HashSet();
	private static HashSet LOC = new HashSet();

	static {
		FREQ_COUNT_ALPH.add(new Integer(
			GroupingOptions.ORDER_BY_FREQUENCY));
		FREQ_COUNT_ALPH.add(new Integer(
			GroupingOptions.ORDER_BY_COUNT));
		FREQ_COUNT_ALPH.add(new Integer(
			GroupingOptions.ORDER_ALPHABETICALLY));

		FREQ_COUNT_ALPH_DATE.add(new Integer(
			GroupingOptions.ORDER_BY_FREQUENCY));
		FREQ_COUNT_ALPH_DATE.add(new Integer(
			GroupingOptions.ORDER_BY_COUNT));
		FREQ_COUNT_ALPH_DATE.add(new Integer(
			GroupingOptions.ORDER_ALPHABETICALLY));
		FREQ_COUNT_ALPH_DATE.add(new Integer(
			GroupingOptions.ORDER_BY_DATE));

		FREQ_COUNT_LOC.add(new Integer(
			GroupingOptions.ORDER_BY_FREQUENCY));
		FREQ_COUNT_LOC.add(new Integer(
			GroupingOptions.ORDER_BY_COUNT));
		FREQ_COUNT_LOC.add(new Integer(
			GroupingOptions.ORDER_BY_LOCATION));

		COUNT_ALPH.add(new Integer(
			GroupingOptions.ORDER_BY_COUNT));
		COUNT_ALPH.add(new Integer(
			GroupingOptions.ORDER_ALPHABETICALLY));

		COUNT_DATE.add(new Integer(
			GroupingOptions.ORDER_BY_COUNT));
		COUNT_DATE.add(new Integer(
			GroupingOptions.ORDER_BY_DATE));

		LOC.add(new Integer(
			GroupingOptions.ORDER_BY_LOCATION));
	}

	/**	Array of grouping option info for the group by combo box. */

	private static final GroupingInfo[] GROUPING_OPTIONS =
		new GroupingInfo[] {
			new GroupingInfo("Corpus",
				Corpus.class,
				FREQ_COUNT_ALPH,
				GroupingOptions.ORDER_BY_FREQUENCY,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Work",
				Work.class,
				FREQ_COUNT_ALPH_DATE,
				GroupingOptions.ORDER_BY_FREQUENCY,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Work part",
				WorkPart.class,
				FREQ_COUNT_LOC,
				GroupingOptions.ORDER_BY_LOCATION,
				GroupingOptions.ASCENDING,
				true),
			new GroupingInfo("Author",
				Author.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Publication date",
				PubYearRange.class,
				COUNT_DATE,
				GroupingOptions.ORDER_BY_DATE,
				GroupingOptions.ASCENDING,
				false),
			new GroupingInfo("Publication decade",
				PubDecade.class,
				COUNT_DATE,
				GroupingOptions.ORDER_BY_DATE,
				GroupingOptions.ASCENDING,
				false),
			new GroupingInfo("Lemma",
				Lemma.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Part of speech",
				Pos.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Spelling",
				Spelling.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				false),
			new GroupingInfo("Major word class",
				MajorWordClass.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Word class",
				WordClass.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Narration or speech",
				Narrative.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Speaker",
				Speaker.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Speaker name",
				SpeakerName.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Speaker gender",
				Gender.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Speaker mortality",
				Mortality.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Prose or verse",
				Prosodic.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Metrical shape",
				MetricalShape.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				true),
			new GroupingInfo("Preceding word form",
				PrecedingWordForm.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				false),
			new GroupingInfo("Following word form",
				FollowingWordForm.class,
				COUNT_ALPH,
				GroupingOptions.ORDER_BY_COUNT,
				GroupingOptions.DESCENDING,
				false),
/*
			new GroupingInfo("WorkSet",
				Work.class,
				FREQ_COUNT_ALPH_DATE,
				GroupingOptions.ORDER_BY_FREQUENCY,
				GroupingOptions.DESCENDING,
				true),
*/
			new GroupingInfo("None",
				null,
				LOC,
				GroupingOptions.ORDER_BY_LOCATION,
				GroupingOptions.ASCENDING,
				false),
		};

	/**	Number of grouping options. */

	private static final int NUM_GROUPING_OPTIONS = GROUPING_OPTIONS.length;

	/**	Current grouping options. */

	private GroupingOptions groupingOptions;

	/**	The parent grouping panel. */

	private GroupingPanel parentPanel;

	/**	Plus button. */

	private PlusOrMinusButton plusButton = new PlusOrMinusButton(true);

	/**	Minus button. */

	private PlusOrMinusButton minusButton = new PlusOrMinusButton(false);

	/**	Group by combo box. */

	private SmallComboBox groupByComboBox;

	/**	Order by combo box. */

	private SmallComboBox orderByComboBox;

	/**	Ascending/descending combo box. */

	private SmallComboBox upDownComboBox;

	/**	Creates a new grouping options row.
	 *
	 *	@param	font				Font for small combo box controls and
	 *								labels.
	 *
	 *	@param	sq					The search criteria.
	 *
	 *	@param	parentPanel			Parent grouping panel.
	 *
	 *	@param	groupingOptions		Initial grouping options, or null.
	 */

	Row (Font font, SearchCriteria sq, final GroupingPanel parentPanel,
		GroupingOptions groupingOptions)
	{
		this.parentPanel = parentPanel;

		JLabel groupByLabel =
			WordHoard.getSmallComboBoxLabel("Group by: ", font);
		groupByComboBox = new SmallComboBox(font);
		for (int i = 0; i < NUM_GROUPING_OPTIONS; i++) {
			GroupingInfo info = GROUPING_OPTIONS[i];
			groupByComboBox.addItem(info);
		}

		boolean haveGroupBy = false;
		boolean groupByChanged = true;

		if (groupingOptions != null) {
			Class groupBy = groupingOptions.getGroupBy();
			for (int i = 0; i < NUM_GROUPING_OPTIONS; i++) {
				GroupingInfo info = GROUPING_OPTIONS[i];
				if (Compare.equals(groupBy, info.groupBy)) {
					if (groupByComboBox.isEnabled(i)) {
						groupByComboBox.setSelectedIndex(i);
						haveGroupBy = true;
						groupByChanged = false;
					}
					break;
				}
			}
		}

		if (!haveGroupBy) {
			Row[] rows = parentPanel.getRows();
			for (int i = 0; i < NUM_GROUPING_OPTIONS; i++) {
				if (groupByComboBox.isEnabled(i)) {
					GroupingInfo info = GROUPING_OPTIONS[i];
					Class groupBy = info.groupBy;
					boolean used = info.isCriterion && sq.contains(groupBy);
					if (used) continue;
					for (int j = 0; j < rows.length; j++) {
						Row row = rows[j];
						GroupingOptions options = row.getGroupingOptions();
						used = Compare.equals(groupBy, options.getGroupBy());
						if (used) break;
					}
					if (!used) {
						groupByComboBox.setSelectedIndex(i);
						groupingOptions = new GroupingOptions(groupBy, 0, 0);
						break;
					}
				}
			}
		}

		this.groupingOptions = groupingOptions;

		groupByComboBox.addActionListener(actionListener);

		JLabel orderByLabel =
			WordHoard.getSmallComboBoxLabel("Order by: ", font);
		orderByComboBox = new SmallComboBox(font);
		for (int i = 0; i < GroupingOptions.ORDER_BY_NAMES.length; i++)
			orderByComboBox.addItem(GroupingOptions.ORDER_BY_NAMES[i]);
		orderByComboBox.setSelectedIndex(groupingOptions.getOrderBy());
		orderByComboBox.addActionListener(actionListener);

		upDownComboBox = new SmallComboBox(font);
		for (int i = 0; i < GroupingOptions.UP_DOWN_NAMES.length; i++)
			upDownComboBox.addItem(GroupingOptions.UP_DOWN_NAMES[i]);
		upDownComboBox.setSelectedIndex(groupingOptions.getUpDown());
		upDownComboBox.addActionListener(actionListener);

		adjust(groupByChanged, false);

		plusButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						parentPanel.addRow(Row.this);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		minusButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						parentPanel.removeRow(Row.this);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(plusButton);
		add(Box.createHorizontalStrut(5));
		add(minusButton);
		add(Box.createHorizontalStrut(5));
		add(groupByLabel);
		add(groupByComboBox);
		add(Box.createHorizontalStrut(5));
		add(orderByLabel);
		add(orderByComboBox);
		add(Box.createHorizontalStrut(5));
		add(upDownComboBox);

		setBorder(BorderFactory.createEmptyBorder(0,0,3,0));

		Dimension size = getPreferredSize();
		size.width = 100000;
		setMaximumSize(size);
	}

	/**	Gets the grouping options.
	 *
	 *	@return		The grouping options.
	 */

	GroupingOptions getGroupingOptions () {
		return groupingOptions;
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

	/**	Adjusts the menus.
	 *
	 *	@param	groupByChanged		True if the group by class changed.
	 *
	 *	@param	orderByChanged		True if the order by class changed.
	 */

	private void adjust (boolean groupByChanged, boolean orderByChanged) {
		Class groupBy = groupingOptions.getGroupBy();
		int orderBy = groupingOptions.getOrderBy();
		GroupingInfo info = null;
		for (int i = 0; i < NUM_GROUPING_OPTIONS; i++) {
			info = GROUPING_OPTIONS[i];
			if (Compare.equals(groupBy, info.groupBy)) break;
		}
		HashSet validOrderBy = info.validOrderBy;
		for (int i = 0; i < GroupingOptions.NUM_ORDER_BY; i++)
			orderByComboBox.setEnabled(i, validOrderBy.contains(new Integer(i)));
		upDownComboBox.setEnabled(GroupingOptions.DESCENDING,
			groupBy != null);
		if (groupByChanged || !validOrderBy.contains(new Integer(orderBy))) {
			setOrderBy(info.defaultOrderBy);
			setUpDown(info.defaultUpDown);
		}
		if (orderByChanged) {
			if (orderBy == GroupingOptions.ORDER_BY_FREQUENCY ||
				orderBy == GroupingOptions.ORDER_BY_COUNT)
			{
				setUpDown(GroupingOptions.DESCENDING);
			} else {
				setUpDown(GroupingOptions.ASCENDING);
			}
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
					GroupingInfo info =
						(GroupingInfo)groupByComboBox.getSelectedItem();
					int newOrderBy = orderByComboBox.getSelectedIndex();
					int newUpDown = upDownComboBox.getSelectedIndex();
					boolean groupByChanged =
						!Compare.equals(info.groupBy,
							groupingOptions.getGroupBy());
					boolean orderByChanged =
						newOrderBy != groupingOptions.getOrderBy();
					boolean upDownChanged =
						newUpDown != groupingOptions.getUpDown();
					if (!groupByChanged && !orderByChanged &&
						!upDownChanged) return;
					groupingOptions =
						new GroupingOptions(info.groupBy, newOrderBy,
							newUpDown);
					adjust(groupByChanged, orderByChanged);
					parentPanel.handleNewGroupingOptions();
				} catch (Exception e) {
					Err.err(e);
				}
			}
		};

	/**	Enables or disables the minus button.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	void setMinusEnabled (boolean enabled) {
		minusButton.setEnabled(enabled);
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

