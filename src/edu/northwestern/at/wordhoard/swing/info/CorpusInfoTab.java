package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.utils.db.*;

/**	Corpus info tab.
 */

class CorpusInfoTab extends JPanel {

	/**	Creates a new corpus info tab.
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
	 *	@throws PersistenceException	Error in persistence layer.
	 */

	CorpusInfoTab (final Corpus corpus,
		int fontSize, Insets insets, int minLabelWidth, int maxValueWidth)
			throws PersistenceException
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(Color.white);

		CorpusSummaryPanel summaryPanel = new CorpusSummaryPanel(corpus,
			fontSize, insets, minLabelWidth, maxValueWidth);
		summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(summaryPanel);
		add(Box.createVerticalStrut(10));

		TaggingPanel taggingPanel = new TaggingPanel(corpus.getTaggingData(),
			fontSize, insets, minLabelWidth, maxValueWidth);
		taggingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(taggingPanel);

		Collection translations = corpus.getTranslationsAsList();
		if (translations.size() > 0) {
			add(Box.createVerticalStrut(10));
			TranslationsPanel tranPanel = new TranslationsPanel(
				translations, fontSize, insets, minLabelWidth,
				maxValueWidth);
			add(tranPanel);
		}

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

