package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Publication year criterion component.
 */
 
class PubYearCriterion extends CriterionComponent {

	/**	The component. */
	
	private JPanel panel = new JPanel();

	/**	The start year text field. */
	
	private JTextField startField = new JTextField("", 4);
	
	/**	The end year text field. */
	
	private JTextField endField = new JTextField("", 4);
	
	/**	The publication year range. */
	
	private PubYearRange pubYearRange;
	
	/**	The corpus, or null. */
	
	private Corpus corpus;
	
	/**	The work, or null. */
	
	private Work work;

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
		pubYearRange = (PubYearRange)val;
		corpus = (Corpus)getOtherRowValue(CorpusCriterion.class);
		work = (Work)getOtherRowValue(WorkCriterion.class);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentY(Component.CENTER_ALIGNMENT);
		rebuild();
		return panel;
	}
	
	/**	Gets the value of the component.
	 *
	 *	@return			The work part.
	 */
	 
	SearchCriterion getValue () {
		if(pubYearRange==null) {
			String startStr = startField.getText().trim();
			String endStr = endField.getText().trim();
			Integer startYear = null;
			Integer endYear = null;
			if (startStr.length() > 0) {
				try {
					startYear = new Integer(startStr);
				} catch (NumberFormatException e) {
					return pubYearRange;
				}
			}
			if (endStr.length() > 0) {
				try {
					endYear = new Integer(endStr);
				} catch (NumberFormatException e) {
					return pubYearRange;
				}
			}
			if (startYear == null && endYear == null) {
				pubYearRange = null;
			} else if (startYear != null && endYear != null &&
				startYear.compareTo(endYear) > 0) 
			{
				return pubYearRange;
			} else {
				pubYearRange = new PubYearRange(startYear, endYear);
			}
		}
		return pubYearRange;
	}
	
	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */
	 
	String setCriteria (SearchCriteria searchCriteria) {
		String startStr = startField.getText().trim();
		String endStr = endField.getText().trim();
		Integer startYear = null;
		Integer endYear = null;
		if (startStr.length() > 0) {
			try {
				startYear = new Integer(startStr);
			} catch (NumberFormatException e) {
				return "Starting publication year is invalid.";
			}
		}
		if (endStr.length() > 0) {
			try {
				endYear = new Integer(endStr);
			} catch (NumberFormatException e) {
				return "Ending publication year is invalid.";
			}
		}
		if (startYear == null && endYear == null) {
			pubYearRange = null;
		} else if (startYear != null && endYear != null &&
			startYear.compareTo(endYear) > 0) 
		{
			return "Starting pub year is > ending pub year.";
		} else {
			pubYearRange = new PubYearRange(startYear, endYear);
		}
		searchCriteria.add(pubYearRange);
		return null;
	}
	
	/**	Rebuilds the component. */
	
	private void rebuild () {
		panel.removeAll();
		if (corpus != null && 
			!corpus.getTaggingData().contains(TaggingData.PUB_DATES))
		{
			pubYearRange = null;
			panel.add(new JLabel("(Corpus does not have publication dates)"));
		} else if (work != null &&
			!work.getTaggingData().contains(TaggingData.PUB_DATES))
		{
			pubYearRange = null;
			panel.add(new JLabel("(Work does not have publication dates)"));
		} else {
			panel.add(new JLabel("between  "));
			panel.add(startField);
			panel.add(new JLabel("  and  "));
			panel.add(endField);
			if (pubYearRange != null) {
				Integer startYear = pubYearRange.getStartYear();
				Integer endYear = pubYearRange.getEndYear();
				if (startYear != null) 
					startField.setText(startYear.toString());
				if (endYear != null) 
					endField.setText(endYear.toString());
			}
		}
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
		PubYearRange oldPubYearRange = pubYearRange;
		if (cls.equals(CorpusCriterion.class)) {
			corpus = (Corpus)newVal;
			rebuild();
		} else if (cls.equals(WorkCriterion.class)) {
			work = (Work)newVal;
			rebuild();
		}
		notifyWindow(oldPubYearRange, pubYearRange);
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

