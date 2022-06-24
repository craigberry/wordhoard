package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;

/**	Corpus criterion component.
 */
 
class CorpusCriterion extends CriterionComponent {

	/**	The component. */
	
	private JComboBox comboBox = new JComboBox();
	
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
		corpus = (Corpus)val;
		comboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		Corpus[] corpora = CachedCollections.getCorpora();
		for (int i = 0; i < corpora.length; i++) {
			Corpus c = corpora[i];
			comboBox.addItem(c);
			if (c.equals(corpus)) comboBox.setSelectedIndex(i);
		}
		corpus = (Corpus)comboBox.getSelectedItem();
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					Corpus oldCorpus = corpus;
					corpus = (Corpus)comboBox.getSelectedItem();
					notifyWindow(oldCorpus, corpus);
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
		return corpus;
	}
	
	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */
	 
	String setCriteria (SearchCriteria searchCriteria) {
		searchCriteria.add(corpus);
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

