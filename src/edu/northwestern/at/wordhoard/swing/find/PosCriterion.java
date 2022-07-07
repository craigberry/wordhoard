package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;

/**	Part of speech criterion component.
 */

class PosCriterion extends CriterionComponent {

	/**	The component. */

	private JPanel panel = new JPanel();

	/**	The combo box component. */

	private JComboBox comboBox = new JComboBox();

	/**	True to ignore combo box action events. */

	private boolean ignoreComboBoxActionEvents;

	/**	The English radio button. */

	private JRadioButton englishButton = new JRadioButton("English");

	/**	The Greek radio button. */

	private JRadioButton greekButton = new JRadioButton("Greek");

	/**	The part of speech, or null if none. */

	private Pos pos;

	/**	Part of speech type. */

	private byte posType;

	/**	Corpus, or null if none. */

	private Corpus corpus;

	/**	Lemma, or null if none. */

	private Lemma lemma;

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
		pos = (Pos)val;
		corpus = (Corpus)getOtherRowValue(CorpusCriterion.class);
		lemma = (Lemma)getOtherRowValue(LemmaCriterion.class);
		posType = Pos.ENGLISH;
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBox.setMaximumRowCount(20);
		englishButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		greekButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		ButtonGroup group = new ButtonGroup();
		group.add(englishButton);
		group.add(greekButton);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(englishButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(greekButton);
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(comboBox);
		panel.add(buttonPanel);
		resetEnglishGreekStatus();
		rebuildComboBox();
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					if (ignoreComboBoxActionEvents) return;
					Pos oldPos = pos;
					pos = (Pos)comboBox.getSelectedItem();
					notifyWindow(oldPos, pos);
				}
			}
		);
		englishButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					if (posType == Pos.ENGLISH) return;
					posType = Pos.ENGLISH;
					Pos oldPos = pos;
					try {
						rebuildSelf();
						notifyWindow(oldPos, pos);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		greekButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					if (posType == Pos.GREEK) return;
					posType = Pos.GREEK;
					Pos oldPos = pos;
					try {
						rebuildSelf();
						notifyWindow(oldPos, pos);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		return panel;
	}

	/**	Gets the value of the component.
	 *
	 *	@return			The corpus.
	 */

	SearchCriterion getValue () {
		return pos;
	}

	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */

	String setCriteria (SearchCriteria searchCriteria) {
		searchCriteria.add(pos);
		return null;
	}

	/**	Resets the English/Greek status.
	 */

	private void resetEnglishGreekStatus () {
		if (corpus != null) posType = corpus.getPosType();
		if (posType == Pos.ENGLISH) {
			englishButton.setSelected(true);
		} else {
			greekButton.setSelected(true);
		}
		englishButton.setEnabled(corpus == null);
		greekButton.setEnabled(corpus == null);
	}

	/**	Rebuilds the combo box.
	 *
	 *	@throws PersistenceException	error in persistence layer.
	 */

	private void rebuildComboBox ()
		throws PersistenceException
	{
		ignoreComboBoxActionEvents = true;
		comboBox.removeAllItems();
		Pos[] partsOfSpeech = CachedCollections.getPos();
		//WordClass wordClass = lemma == null ? null : lemma.getWordClass();
		int j = 0;
		for (int i = 0; i < partsOfSpeech.length; i++) {
			Pos p = partsOfSpeech[i];
			String language = p.getLanguage();
			if (language == null) continue;
			if (posType == Pos.ENGLISH) {
				if (!language.equals("english")) continue;
			} else {
				if (!language.equals("greek")) continue;
			}
			//WordClass posWordClass = p.getWordClass();
			//if (wordClass != null && posWordClass != null &&
			//	!wordClass.equals(posWordClass))
			//		continue;
			comboBox.addItem(p);
			if (p.equals(pos)) comboBox.setSelectedIndex(j);
			j++;
		}
		pos = (Pos)comboBox.getSelectedItem();
		ignoreComboBoxActionEvents = false;
	}

	/**	Handles a value changed event in some other criterion.
	 *
	 *	@param	cls			The class of the criterion that changed,
	 *						or null to rebuild self.
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
		if (cls == null) {
			rebuildComboBox();
			return;
		}
		Pos oldPos = pos;
		if (cls.equals(CorpusCriterion.class)) {
			corpus = (Corpus)newVal;
			resetEnglishGreekStatus();
			rebuildComboBox();
		} else if (cls.equals(LemmaCriterion.class)) {
			lemma = (Lemma)newVal;
			rebuildComboBox();
		}
		notifyWindow(oldPos, pos);
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

