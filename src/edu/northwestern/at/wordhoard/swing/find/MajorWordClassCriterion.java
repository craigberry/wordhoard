package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;

/**	Major word class criterion component.
 */

class MajorWordClassCriterion extends CriterionComponent {

	/**	The component. */

	private JPanel panel = new JPanel();

	/**	The combo box component. */

	private JComboBox comboBox = new JComboBox();

	/**	True to ignore combo box action events. */

	private boolean ignoreComboBoxActionEvents;

	/**	The major word class, or null if none. */

	private MajorWordClass majorWordClass;

	/**	The lemma, or null if none. */

	private Lemma lemma;

	/**	The part of speech, or null if none. */

	private Pos pos;

	/**	Initializes the component.
	 *
	 *	@param	val		Initial value, or null.
	 *
	 *	@return 		The component.
	 *
	 *	@throws	Exception
	 */

	JComponent init (SearchCriterion val)
		throws Exception
	{
		majorWordClass = (MajorWordClass)val;
		lemma = (Lemma)getOtherRowValue(LemmaCriterion.class);
		pos = (Pos)getOtherRowValue(PosCriterion.class);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentY(Component.CENTER_ALIGNMENT);
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBox.setMaximumRowCount(20);
		rebuild();
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					if (ignoreComboBoxActionEvents) return;
					MajorWordClass oldMajorWordClass = majorWordClass;
					majorWordClass = (MajorWordClass)comboBox.getSelectedItem();
					notifyWindow(oldMajorWordClass, majorWordClass);
				}
			}
		);
		return panel;
	}

	/**	Gets the value of the component.
	 *
	 *	@return			The word class.
	 */

	SearchCriterion getValue () {
		return majorWordClass;
	}

	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */

	String setCriteria (SearchCriteria searchCriteria) {
		searchCriteria.add(majorWordClass);
		return null;
	}

	/**	Rebuilds the component.
	 *
	 *	@throws PersistenceException
	 */

	private void rebuild ()
		throws PersistenceException
	{
		ignoreComboBoxActionEvents = true;
		panel.removeAll();
		comboBox.removeAllItems();
		WordClass singleWordClass = null;
		if (lemma != null) {
			singleWordClass = lemma.getWordClass();
		} else if (pos != null) {
			singleWordClass = pos.getWordClass();
		}
		if (singleWordClass != null) {
			majorWordClass =
				singleWordClass.getMajorWordClass();
			panel.add(new JLabel(majorWordClass.toString()));
		} else {
			MajorWordClass[] majorWordClasses =
				CachedCollections.getMajorWordClasses();
			for (int i = 0; i < majorWordClasses.length; i++) {
				MajorWordClass mwc = majorWordClasses[i];
				comboBox.addItem(mwc);
				if (mwc.equals(majorWordClass)) comboBox.setSelectedIndex(i);
			}
			majorWordClass = (MajorWordClass)comboBox.getSelectedItem();
			panel.add(comboBox);
		}
		ignoreComboBoxActionEvents = false;
	}

	/**	Handles a value changed event in some other criterion.
	 *
	 *	@param	cls			The class of the criterion that changed.
	 *
	 *	@param	oldVal		Old value.
	 *
	 *	@param	newVal		New value.
	 *
	 *	@throws	Exception
	 */

	void handleValueChanged (Class cls, SearchCriterion oldVal,
		SearchCriterion newVal)
			throws Exception
	{
		MajorWordClass oldMajorWordClass = majorWordClass;
		if (cls.equals(LemmaCriterion.class)) {
			lemma = (Lemma)newVal;
			rebuild();
		} else if (cls.equals(PosCriterion.class)) {
			pos = (Pos)newVal;
			rebuild();
		}
		notifyWindow(oldMajorWordClass, majorWordClass);
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

