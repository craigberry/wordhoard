package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.db.*;

/**	Word class criterion component.
 */

class WordClassCriterion extends CriterionComponent {

	/**	The component. */

	private JPanel panel = new JPanel();

	/**	The combo box component. */

	private JComboBox comboBox = new JComboBox();

	/**	True to ignore combo box action events. */

	private boolean ignoreComboBoxActionEvents;

	/**	The word class, or null if none. */

	private WordClass wordClass;

	/**	The lemma, or null if none. */

	private Lemma lemma;

	/**	The part of speech, or null if none. */

	private Pos pos;

	/**	The major word class, or null if none. */

	private MajorWordClass majorWordClass;

	/**	Initializes the component.
	 *
	 *	@param	val		Initial value, or null.
	 *
	 *	@return 		The component.
	 *
	 *	@throws	Exception	general error.
	 */

	JComponent init (SearchCriterion val)
		throws Exception
	{
		wordClass = (WordClass)val;
		lemma = (Lemma)getOtherRowValue(LemmaCriterion.class);
		pos = (Pos)getOtherRowValue(PosCriterion.class);
		majorWordClass =
			(MajorWordClass)getOtherRowValue(MajorWordClassCriterion.class);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentY(Component.CENTER_ALIGNMENT);
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBox.setMaximumRowCount(20);
		rebuild();
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					if (ignoreComboBoxActionEvents) return;
					WordClass oldWordClass = wordClass;
					wordClass = (WordClass)comboBox.getSelectedItem();
					notifyWindow(oldWordClass, wordClass);
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
		return wordClass;
	}

	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */

	String setCriteria (SearchCriteria searchCriteria) {
		searchCriteria.add(wordClass);
		return null;
	}

	/**	Rebuilds the component.
	 *
	 *	@throws PersistenceException	error in persistence layer.
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
			wordClass = singleWordClass;
			panel.add(new JLabel(singleWordClass.getTag()));
		} else {
			WordClass[] wordClasses = CachedCollections.getWordClasses();
			int j = 0;
			for (int i = 0; i < wordClasses.length; i++) {
				WordClass wc = wordClasses[i];
				if (majorWordClass == null ||
					majorWordClass.equals(wc.getMajorWordClass()))
				{
					comboBox.addItem(wc);
					if (wc.equals(wordClass)) comboBox.setSelectedIndex(j);
					j++;
				}
			}
			wordClass = (WordClass)comboBox.getSelectedItem();
			if (comboBox.getItemCount() == 1) {
				panel.add(new JLabel(wordClass.getTag()));
			} else {
				panel.add(comboBox);
			}
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
	 *	@throws	Exception	general error.
	 */

	void handleValueChanged (Class cls, SearchCriterion oldVal,
		SearchCriterion newVal)
			throws Exception
	{
		WordClass oldWordClass = wordClass;
		if (cls.equals(LemmaCriterion.class)) {
			lemma = (Lemma)newVal;
			rebuild();
		} else if (cls.equals(PosCriterion.class)) {
			pos = (Pos)newVal;
			rebuild();
		} else if (cls.equals(MajorWordClassCriterion.class)) {
			majorWordClass = (MajorWordClass)newVal;
			rebuild();
		}
		notifyWindow(oldWordClass, wordClass);
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

