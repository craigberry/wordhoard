package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.db.*;

/**	Lemma summary tab.
 */

class LemmaSummaryTab extends JPanel {

	/**	Creates a new lemma info summary tab.
	 *
	 *	@param	lemma			Lemma.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	fontSize		Font size.
	 *
	 *	@param	width			Width of window contents.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws PersistenceException	error in persistence layer.
	 */

	LemmaSummaryTab (final Lemma lemma, final Corpus corpus,
		int fontSize, int width, final AbstractWindow parentWindow)
			throws PersistenceException
	{
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		Spelling spelling = lemma.getSpelling();
		if (spelling != null) {
			BigSpellingPanel bigSpellingPanel = new BigSpellingPanel(
				spelling);
			bigSpellingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(bigSpellingPanel);
			add(Box.createVerticalStrut(10));
		}

		FrequencyPanel frequencyPanel = new FrequencyPanel(lemma, corpus,
			null, fontSize, width);
		frequencyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(frequencyPanel);
		add(Box.createVerticalStrut(10));

		GetConcordancePanel getConcordancePanel = new GetConcordancePanel(
			lemma, null, null, corpus, fontSize, parentWindow);
		getConcordancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(getConcordancePanel);

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

