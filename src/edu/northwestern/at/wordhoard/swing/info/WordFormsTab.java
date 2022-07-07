package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.utils.db.*;

/**	Word forms tab.
 */

class WordFormsTab extends JPanel {

	/**	The lemma. */

	private Lemma lemma;

	/**	Word forms panel. */

	private WordFormsPanel wordFormsPanel;

	/**	Creates a new get info word forms tab.
	 *
	 *	@param	lemma			Lemma.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	fontSize		Font size.
	 *
	 *	@param	size			Panel size.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws PersistenceException	error in persistence layer.
	 */

	WordFormsTab (Lemma lemma, final Corpus corpus,
		int fontSize, Dimension size, final AbstractWindow parentWindow)
			throws PersistenceException
	{
		this.lemma = lemma;
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(size);

		if (lemma == null) return;
		wordFormsPanel = new WordFormsPanel(lemma, corpus,
			fontSize, size.width-40, parentWindow);
		wordFormsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(wordFormsPanel);
	}

	/**	Gets the search defaults object.
	 *
	 *	@return		Search defaults object.
	 */

	SearchDefaults getSearchDefaults () {
		if (wordFormsPanel == null) return lemma;
		return wordFormsPanel.getSearchDefaults();
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

