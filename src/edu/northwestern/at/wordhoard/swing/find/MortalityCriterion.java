package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

/**	Speaker mortality criterion component.
 */

class MortalityCriterion extends CriterionComponent {

	/**	The component. */

	private JPanel panel = new JPanel();

	/**	The combo box component. */

	private JComboBox comboBox = new JComboBox();

	/**	True to ignore combo box action events. */

	private boolean ignoreComboBoxActionEvents;

	/**	The mortality, or null. */

	private Mortality mortality;

	/**	The corpus, or null if none. */

	private Corpus corpus;

	/**	The work, or null if none. */

	private Work work;

	/**	The work part, or null if none. */

	private WorkPart workPart;

	/**	The narrative attribute, or null if none. */

	private Narrative narrative;

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
		mortality = (Mortality)val;
		corpus = (Corpus)getOtherRowValue(CorpusCriterion.class);
		work = (Work)getOtherRowValue(WorkCriterion.class);
		workPart = (WorkPart)getOtherRowValue(WorkPartCriterion.class);
		narrative = (Narrative)getOtherRowValue(NarrativeCriterion.class);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentY(Component.CENTER_ALIGNMENT);
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBox.setMaximumRowCount(20);
		rebuild();
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					if (ignoreComboBoxActionEvents) return;
					Mortality oldMortality = mortality;
					mortality = (Mortality)comboBox.getSelectedItem();
					notifyWindow(oldMortality, mortality);
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
		return mortality;
	}

	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */

	String setCriteria (SearchCriteria searchCriteria) {
		searchCriteria.add(mortality);
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
		String badTagString = checkTaggingData(corpus, work, workPart,
			TaggingData.MORTALITY);
		if (badTagString != null) {
			mortality = null;
			panel.add(new JLabel("(" + badTagString +
				" does not have speaker mortality data)"));
		} else if (narrative != null && !narrative.isSpeech()) {
			mortality = null;
			panel.add(new JLabel(
				"(There are no speakers in narration)"));
		} else {
			for (byte mor = 0; mor < Mortality.NUM_MORTALITY; mor++) {
				Mortality m = new Mortality(mor);
				comboBox.addItem(m);
				if (m.equals(mortality)) comboBox.setSelectedItem(m);
			}
			mortality = (Mortality)comboBox.getSelectedItem();
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
		Mortality oldMortality = mortality;
		if (cls.equals(CorpusCriterion.class)) {
			corpus = (Corpus)newVal;
			rebuild();
		} else if (cls.equals(WorkCriterion.class)) {
			work = (Work)newVal;
			rebuild();
		} else if (cls.equals(WorkPartCriterion.class)) {
			workPart = (WorkPart)newVal;
			rebuild();
		} else if (cls.equals(NarrativeCriterion.class)) {
			narrative = (Narrative)newVal;
			rebuild();
		}
		notifyWindow(oldMortality, mortality);
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

