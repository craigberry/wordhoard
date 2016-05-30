package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	PhraseSet criterion component.
 */

class PhraseSetCriterion extends CriterionComponent {

	/**	The component. */

	private JComboBox comboBox = new JComboBox();

	/**	The phrase set, or null if none. */

	private PhraseSet phraseSet;

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
		phraseSet = (PhraseSet)val;
		comboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		PhraseSet[] phraseSets = PhraseSetUtils.getPhraseSets();
		for (int i = 0; i < phraseSets.length; i++) {
			PhraseSet ps = phraseSets[i];
			comboBox.addItem(ps);
			if (ps.equals(phraseSet)) comboBox.setSelectedIndex(i);
		}
		phraseSet = (PhraseSet)comboBox.getSelectedItem();
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					PhraseSet oldPhraseSet = phraseSet;
					phraseSet = (PhraseSet)comboBox.getSelectedItem();
					notifyWindow(oldPhraseSet, phraseSet);
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
		return phraseSet;
	}

	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */

	String setCriteria (SearchCriteria searchCriteria) {
		searchCriteria.add(phraseSet);
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

