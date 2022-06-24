package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

/**	Frequency panel.
 */

class FrequencyPanel extends JPanel {

	/**	Creates a new frequency panel.
	 *
	 *	@param	lemma				Lemma.
	 *
	 *	@param	corpus				Corpus.
	 *
	 *	@param	work				Work, or null if none.
	 *
	 *	@param	fontSize			Font size.
	 *
	 *	@param	width				Width.
	 *
	 *	@throws	PersistenceException
	 */

	FrequencyPanel (Lemma lemma, Corpus corpus, Work work,
		int fontSize, int width)
			throws PersistenceException
	{

		PersistenceManager pm = WordHoard.getPm();

		int termFreq = 0;
		float workRelFreq = 0.0f;
		if (work != null) {
			LemmaWorkCounts lemmaWorkCounts =
				pm.getLemmaWorkCounts(lemma, work);
			if (lemmaWorkCounts != null) {
				termFreq = lemmaWorkCounts.getTermFreq();
				workRelFreq = work.getRelFreq(termFreq);
			}
		}

		LemmaCorpusCounts lemmaCorpusCounts =
			pm.getLemmaCorpusCounts(lemma, corpus);
		int colFreq = 0;
		int docFreq = 0;
		int corpusRank1 = 0;
		int corpusRank2 = 0;
		int corpusNumMajorClass = 0;
		if (lemmaCorpusCounts != null) {
			colFreq = lemmaCorpusCounts.getColFreq();
			docFreq = lemmaCorpusCounts.getDocFreq();
			corpusRank1 = lemmaCorpusCounts.getRank1();
			corpusRank2 = lemmaCorpusCounts.getRank2();
			corpusNumMajorClass = lemmaCorpusCounts.getNumMajorClass();
		}
		float corpusRelFreq = corpus.getRelFreq(colFreq);
		int numWorks = corpus.getNumWorks();
		MajorWordClass majorWordClass = lemma.getWordClass().getMajorWordClass();
		String corpusTitle = corpus.getTitle();
		Spelling lemmaSpelling = lemma.getSpelling();

		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		FontManager fontManager = new FontManager();
		FontInfo romanFontInfo = fontManager.getFontInfo(fontSize);
		Font romanFont = romanFontInfo.getFont();

		JLabel freqLabel = new JLabel("Frequency");
		freqLabel.setFont(romanFont);
		freqLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		freqLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(freqLabel);

		TextLine line = new TextLine(30);
		line.appendRun("The " + majorWordClass.toString() + " \u201C", romanFontInfo);
		FontInfo lemmaFontInfo = fontManager.getFontInfo(
			lemmaSpelling.getCharset(), fontSize);
		line.appendRun(lemmaSpelling.getString(), lemmaFontInfo);
		StringBuffer buf = new StringBuffer();
		buf.append("\u201D occurs in ");
		buf.append(Formatters.formatIntegerWithCommas(docFreq));
		buf.append(" of the ");
		buf.append(numWorks);
		buf.append("  works in ");
		buf.append(corpusTitle);
		buf.append(" and has rank ");
		buf.append(Formatters.formatIntegerWithCommas(corpusRank1));
		if (corpusRank1 < corpusRank2) {
			buf.append('-');
			buf.append(Formatters.formatIntegerWithCommas(corpusRank2));
		}
		buf.append(" among the ");
		buf.append(Formatters.formatIntegerWithCommas(corpusNumMajorClass));
		buf.append(' ');
		buf.append(majorWordClass.toString());
		if (corpusNumMajorClass != 1) buf.append('s');
		buf.append(" in the corpus.");
		line.appendRun(buf.toString(), romanFontInfo);
		Text text = new Text(line);
		WrappedTextComponent textComponent =
			new WrappedTextComponent(text, width);
		textComponent.setBackground(Color.white);
		textComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(textComponent);

		String[][] cells;
		int[] columnAlignment;

		if (work == null) {
			cells = new String[][] {
				{
					Formatters.formatIntegerWithCommas(colFreq),
					"Number of occurrences of lemma"
				},
				{
					Formatters.formatFloat(corpusRelFreq, 2),
					"Relative frequency per 10,000 words"
				}
			};
			columnAlignment = new int[] {
				SwingConstants.CENTER,
				SwingConstants.LEFT
			};
		} else {
			cells = new String[][] {
				{
					"Work",
					"Corpus",
					" "
				},
				{
					Formatters.formatIntegerWithCommas(termFreq),
					Formatters.formatIntegerWithCommas(colFreq),
					"Number of occurrences of lemma"
				},
				{
					Formatters.formatFloat(workRelFreq, 2),
					Formatters.formatFloat(corpusRelFreq, 2),
					"Relative frequency per 10,000 words"
				}
			};
			columnAlignment = new int[] {
				SwingConstants.CENTER,
				SwingConstants.CENTER,
				SwingConstants.LEFT
			};
		}

		SimpleTable freqTab = new SimpleTable(cells, columnAlignment,
			romanFont, 10, 0);
		freqTab.setBorder(BorderFactory.createEmptyBorder(8, 30, 0, 0));
		freqTab.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(freqTab);

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

