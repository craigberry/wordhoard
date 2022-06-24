package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	WordSet criterion component.
 */

class WordSetCriterion extends CriterionComponent {

	/**	The component. */

	private JComboBox comboBox = new JComboBox();

	/**	The word set, or null if none. */

	private WordSet wordSet;

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
		wordSet = (WordSet)val;
		comboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		WordSet[] wordSets = WordSetUtils.getWordSets();
		for (int i = 0; i < wordSets.length; i++) {
			WordSet ws = wordSets[i];
			comboBox.addItem(ws);
			if (ws.equals(wordSet)) comboBox.setSelectedIndex(i);
		}
		wordSet = (WordSet)comboBox.getSelectedItem();
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					WordSet oldWordSet = wordSet;
					wordSet = (WordSet)comboBox.getSelectedItem();
					notifyWindow(oldWordSet, wordSet);
				}
			}
		);
		return comboBox;
	}

	/**	Gets the value of the component.
	 *
	 *	@return			The corpus.
	 */

	SearchCriterion getValue () {
		return wordSet;
	}

	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */

	String setCriteria (SearchCriteria searchCriteria) {
		searchCriteria.add(wordSet);
		return null;
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

