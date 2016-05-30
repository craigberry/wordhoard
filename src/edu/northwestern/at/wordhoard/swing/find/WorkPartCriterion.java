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

/**	Work part criterion component.
 */
 
class WorkPartCriterion extends CriterionComponent {

	/**	The component. */
	
	private JPanel panel = new JPanel();

	/**	The combo box component. */
	
	private JComboBox comboBox = new JComboBox();
	
	/**	True to ignore combo box action events. */
	
	private boolean ignoreComboBoxActionEvents;
	
	/** The work part, or null. */
	
	private WorkPart workPart;
	
	/**	The work, or null if none. */
	
	private Work work;
	
	/**	The corpus, or null if none. */
	
	private Corpus corpus;
	
	/**	A combo box item. */
	
	private static class Item {
		private WorkPart workPart;
		private int level;
		private Item (WorkPart workPart, int level) {
			this.workPart = workPart;
			this.level = level;
		}
		public String toString () {
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < level; i++) buf.append("   ");
			buf.append(workPart.getFullTitle());
			return buf.toString();
		}
	}

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
		workPart = (WorkPart)val;
		corpus = (Corpus)getOtherRowValue(CorpusCriterion.class);
		work = (Work)getOtherRowValue(WorkCriterion.class);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentY(Component.CENTER_ALIGNMENT);
		comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		comboBox.setMaximumRowCount(20);
		rebuild();
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					if (ignoreComboBoxActionEvents) return;
					WorkPart oldWorkPart = workPart;
					Item item = (Item)comboBox.getSelectedItem();
					workPart = item.workPart;
					notifyWindow(oldWorkPart, workPart);
				}
			}
		);
		return panel;
	}
	
	/**	Gets the value of the component.
	 *
	 *	@return			The work part.
	 */
	 
	SearchCriterion getValue () {
		return workPart;
	}
	
	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */
	 
	String setCriteria (SearchCriteria searchCriteria) {
		searchCriteria.add(workPart);
		return null;
	}
	
	/**	Appends children of a work part which have tagging data to a
	 *	list of items.
	 *
	 *	@param	workPart		Work part.
	 *
	 *	@param	list			List of items.
	 *
	 *	@param	level			Level.
	 */
	
	private void appendChildren (WorkPart workPart, ArrayList list, 
		int level) 
	{
		if (!workPart.getTaggingData().contains(TaggingData.SPELLING))
			return;
		for (Iterator it = workPart.getChildren().iterator(); it.hasNext(); ) {
			WorkPart child = (WorkPart)it.next();
			if (child.getTaggingData().contains(TaggingData.SPELLING)) {
				list.add(new Item(child, level));
				appendChildren(child, list, level+1);
			}
		}
	}
	
	/**	Rebuilds the component.
	 */
	 
	private void rebuild () {
		ignoreComboBoxActionEvents = true;
		panel.removeAll();
		comboBox.removeAllItems();
		if (corpus == null || work == null) {
			workPart = null;
			panel.add(new JLabel("(Select a corpus and work first)"));
		} else {
			ArrayList list = new ArrayList();
			appendChildren(work, list, 0);
			int numItems = list.size();
			if (numItems == 0) {
				workPart = null;
				panel.add(new JLabel(
					"(This work has no searchable parts)"));
			} else if (numItems == 1) {
				Item item = (Item)list.get(0);
				workPart = item.workPart;
				panel.add(new JLabel(workPart.getFullTitle()));
			} else {
				for (Iterator it = list.iterator(); it.hasNext(); ) {
					Item item = (Item)it.next();
					comboBox.addItem(item);
					if (item.workPart.equals(workPart))
						comboBox.setSelectedItem(item);
				}
				Item item = (Item)comboBox.getSelectedItem();
				workPart = item.workPart;
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
	 *	@throws	Exception
	 */
	 
	void handleValueChanged (Class cls, SearchCriterion oldVal, 
		SearchCriterion newVal)
			throws Exception
	{
		WorkPart oldWorkPart = workPart;
		if (cls.equals(CorpusCriterion.class)) {
			corpus = (Corpus)newVal;
			rebuild();
		} else if (cls.equals(WorkCriterion.class)) {
			work = (Work)newVal;
			rebuild();
		}
		notifyWindow(oldWorkPart, workPart);
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

