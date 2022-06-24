package edu.northwestern.at.wordhoard.swing.querytool;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.db.*;

/**	A lexicon options panel.
 */

class SearchCriteriaResultsOptionsPanel extends JPanel {

	/**	Order by options. */

	static final int ORDER_BY_LEMMA = 0;
	static final int ORDER_BY_FREQUENCY_LEMMA = 1;
	static final int ORDER_BY_CLASS_LEMMA = 2;
	static final int ORDER_BY_CLASS_FREQUENCY_LEMMA = 3;
	static final int ORDER_BY_NUMWORKS_LEMMA = 4;
	static final int ORDER_BY_CLASS_NUMWORKS_LEMMA = 5;

	/**	Parent lexicon panel. */

	private SearchCriteriaResultsPanel parentPanel;

	/**	Major word class combo box. */

	private SmallComboBox majorClassComboBox;

	/**	Order by combo box. */

	private SmallComboBox orderByComboBox;

	/**	Major word classes. */

	private MajorWordClass[] majorWordClasses;

	/**	Creates a new lexicon options panel.
	 *
	 *	@param	font			Font for small combo box controls and lables.
	 *
	 *	@param	parentPanel		Parent lexicon panel.
	 *
	 *	@param	majorClass		Initially selected word class option.
	 *
	 *	@param	orderBy			Initially selected order by option.
	 *
	 *	@throws	PersistenceException
	 */

	SearchCriteriaResultsOptionsPanel (Font font, SearchCriteriaResultsPanel parentPanel,
		String majorClass, int orderBy)
			throws PersistenceException
	{

		this.parentPanel = parentPanel;

		JLabel showLabel =
			WordHoard.getSmallComboBoxLabel("Show: ", font);
		majorClassComboBox = new SmallComboBox(font);
		majorWordClasses = CachedCollections.getMajorWordClasses();
		majorClassComboBox.addItem("All");
		int majorClassIndex = 0;
		for (int i = 0; i < majorWordClasses.length; i++) {
			String str = majorWordClasses[i].getMajorWordClass();
			if (majorClass.equals(str)) majorClassIndex = i+1;
			str = Character.toUpperCase(str.charAt(0)) +
				str.substring(1)  + "s";
			majorClassComboBox.addItem(str);
		}
		majorClassComboBox.setSelectedIndex(majorClassIndex);
		majorClassComboBox.addActionListener(actionListener);

		JLabel orderByLabel =
			WordHoard.getSmallComboBoxLabel("Order by: ", font);
		orderByComboBox = new SmallComboBox(font);
		orderByComboBox.addItem("Lemma");
		orderByComboBox.addItem("Count/Lemma");
		orderByComboBox.addItem("Word class/Lemma");
		orderByComboBox.addItem("Word class/Count/Lemma");
		orderByComboBox.addItem("# Works/Lemma");
		orderByComboBox.addItem("Word class/# Works/Lemma");
		orderByComboBox.setSelectedIndex(orderBy);
		orderByComboBox.addActionListener(actionListener);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(showLabel);
		add(majorClassComboBox);
		add(Box.createHorizontalStrut(15));
		add(orderByLabel);
		add(orderByComboBox);

		Dimension size = getPreferredSize();
		size.width = 100000;
		setMaximumSize(size);

	}

	/**	Action listener for combo box actions.
	 *
	 *	<p>The parent panel's handleOptions method is invoked
	 *	to handle the change in options.
	 */

	private ActionListener actionListener =
		new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				try {
					int majorClassIndex = majorClassComboBox.getSelectedIndex();
					String majorClass = majorClassIndex == 0 ? "All" :
						majorWordClasses[majorClassIndex-1].getMajorWordClass();
					int orderBy = orderByComboBox.getSelectedIndex();
					parentPanel.handleOptions(majorClass, orderBy);
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

