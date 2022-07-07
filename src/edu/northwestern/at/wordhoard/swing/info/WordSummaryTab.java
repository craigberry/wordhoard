package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.db.*;

/**	Word summary tab.
 */

class WordSummaryTab extends JPanel {

	/**	Creates a new get info summary panel.
	 *
	 *	@param	wordPart		Word part.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	fontSize		Font size.
	 *
	 *	@param	insets			Insets for labeled columns.
	 *
	 *	@param	minLabelWidth	Min label width for labeled columns.
	 *
	 *	@param	maxValueWidth	Max value width for labeled columns.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws PersistenceException	error in persistence layer.
	 */

	WordSummaryTab (final WordPart wordPart, final Corpus corpus,
		int fontSize, Insets insets, int minLabelWidth, int maxValueWidth,
		final AbstractWindow parentWindow)
			throws PersistenceException
	{
		Word word = wordPart.getWord();
		Work work = word.getWork();
		Spelling spelling = word.getSpelling();
		LemPos lemPos = wordPart.getLemPos();
		Lemma lemma = lemPos == null ? null : lemPos.getLemma();
		Pos pos = lemPos == null ? null : lemPos.getPos();
		BensonLemPos bensonLemPos = wordPart.getBensonLemPos();

		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		FontManager fontManager = new FontManager();
		if (spelling != null) {
			BigSpellingPanel bigSpellingPanel = new BigSpellingPanel(
				spelling);
			bigSpellingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(bigSpellingPanel);
			java.util.List wordParts = word.getWordParts();
			int numWordParts = wordParts.size();
			if (numWordParts > 1) {
				add(Box.createVerticalStrut(4));
				JLabel partNumLabel = new JLabel(
					"Compound word part " +
					(wordPart.getPartIndex() + 1) +
					" of " + numWordParts);
				Font font = fontManager.getFont(14);
				partNumLabel.setFont(font);
				partNumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
				add(partNumLabel);
			}
			add(Box.createVerticalStrut(10));
		}

		if (bensonLemPos != null) {
			BensonPanel bensonPanel = new BensonPanel(bensonLemPos,
				fontSize, insets, minLabelWidth, maxValueWidth);
			bensonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(bensonPanel);
			add(Box.createVerticalStrut(10));
		}

		MorphologyPanel morphologyPanel = new MorphologyPanel(wordPart,
			fontSize, insets, minLabelWidth, maxValueWidth);
		morphologyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(morphologyPanel);
		add(Box.createVerticalStrut(10));

		if (lemma != null) {
			FrequencyPanel frequencyPanel = new FrequencyPanel(lemma, corpus,
				work, fontSize, minLabelWidth + maxValueWidth);
			frequencyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(frequencyPanel);
			add(Box.createVerticalStrut(10));
		}

		GetConcordancePanel getConcordancePanel = new GetConcordancePanel(
			lemma, pos, spelling, corpus, fontSize, parentWindow);
		getConcordancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(getConcordancePanel);

		setMaximumSize(getPreferredSize());

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

