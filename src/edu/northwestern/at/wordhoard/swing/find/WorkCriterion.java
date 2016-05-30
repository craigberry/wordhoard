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
import edu.northwestern.at.utils.*;

/**	Work criterion component.
 */
 
class WorkCriterion extends CriterionComponent {

	/**	The component. */
	
	private JPanel panel = new JPanel();

	/**	The combo box component. */
	
	private JComboBox comboBox = new JComboBox();
	
	/**	True to ignore combo box action events. */
	
	private boolean ignoreComboBoxActionEvents;
	
	/**	The work, or null if none. */
	
	private Work work;
	
	/**	The corpus, or null if none. */
	
	private Corpus corpus;

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
		work = (Work)val;
		corpus = (Corpus)getOtherRowValue(CorpusCriterion.class);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentY(Component.CENTER_ALIGNMENT);
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBox.setMaximumRowCount(20);
		rebuild();
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					if (ignoreComboBoxActionEvents) return;
					Work oldWork = work;
					work = (Work)comboBox.getSelectedItem();
					notifyWindow(oldWork, work);
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
		return work;
	}
	
	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */
	 
	String setCriteria (SearchCriteria searchCriteria) {
		searchCriteria.add(work);
		return null;
	}
	
	/**	Rebuilds the component.
	 */
	 
	private void rebuild () {
		ignoreComboBoxActionEvents = true;
		panel.removeAll();
		comboBox.removeAllItems();
		if (corpus == null) {
			work = null;
			panel.add(new JLabel("(Select a corpus first)"));
		} else {
			Collection workCollection = corpus.getWorks();
			Work[] works = (Work[])workCollection.toArray(
				new Work[workCollection.size()]);
			Arrays.sort(works,
				new Comparator() {
					public int compare (Object o1, Object o2) {
						Work w1 = (Work)o1;
						Work w2 = (Work)o2;
						return Compare.compareIgnoreCase(
							w1.getFullTitle(), w2.getFullTitle());
					}
				}
			);
			for (int i = 0; i < works.length; i++) {
				Work w = works[i];
				comboBox.addItem(w);
				if (w.equals(work)) comboBox.setSelectedIndex(i);
			}
			work = (Work)comboBox.getSelectedItem();
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
		Work oldWork = work;
		if (cls.equals(CorpusCriterion.class)) {
			corpus = (Corpus)newVal;
			rebuild();
		}
		notifyWindow(oldWork, work);
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

