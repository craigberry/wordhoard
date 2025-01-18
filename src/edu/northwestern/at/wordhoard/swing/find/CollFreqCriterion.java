package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

public	 class CollFreqCriterion extends CriterionComponent {

	/**	The component. */

	private JPanel panel = new JPanel();

	private	JTextField freqField = new JTextField("", 4);

	private	Corpus corpus = null;

	private JComboBox corpusComboBox = new JComboBox();

	private JComboBox compareComboBox = new JComboBox();

	private	String compare = null;

	/**	The doc freq range */

	private CollectionFrequency collFrequency;

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
		collFrequency = (CollectionFrequency)val;
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		try {
			corpusComboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
			Corpus[] corpora = CachedCollections.getCorpora();
			for (int i = 0; i < corpora.length; i++) {
				Corpus c = corpora[i];
				corpusComboBox.addItem(c);
			}
			compareComboBox.addItem("LT");
			compareComboBox.addItem("LTE");
			compareComboBox.addItem("EQ");
			compareComboBox.addItem("GT");
			compareComboBox.addItem("GTE");
			compare="LT";
		} catch (Exception e) {Err.err(e);}

		corpus = (Corpus)corpusComboBox.getSelectedItem();
		corpusComboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					corpus = (Corpus)corpusComboBox.getSelectedItem();
				}
			}
		);

		compareComboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					compare = (String)compareComboBox.getSelectedItem();
				}
			}
		);
		rebuild();
		return panel;
	}

	/**	Rebuilds the component. */

	private void rebuild () {
/*		panel.add(new JLabel("between"));
		panel.add(startField);
		panel.add(new JLabel("and"));
		panel.add(endField);
		panel.add(new JLabel("in"));
		panel.add(corpusComboBox); */

		panel.add(compareComboBox);
		panel.add(freqField);
		panel.add(new JLabel("in"));
		panel.add(corpusComboBox);

	}

	/**	Gets the value of the component.
	 *
	 *	@return			The document frequency.
	 */

	SearchCriterion getValue () {
		updateCriteria();
		return collFrequency;
	}

	/**	Sets the criteria value.
	 *
	 *	@return			Validation error message, or null if none.
	 */

	String updateCriteria () {
		String freqStr = freqField.getText().trim();
		Integer freq = null;
		if (freqStr.length() > 0) {
			try {
				freq = Integer.valueOf(freqStr);
			} catch (NumberFormatException e) {
				return "Starting coll freq is invalid.";
			}
		}
		if (freq == null) {collFrequency=null;}
		else {
			collFrequency=new CollectionFrequency(freq, compare,corpus);
		}
		return null;
	}


	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */

	String setCriteria (SearchCriteria searchCriteria) {

		String reply = updateCriteria();
		if(reply==null) searchCriteria.add(collFrequency);
		return reply;
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

