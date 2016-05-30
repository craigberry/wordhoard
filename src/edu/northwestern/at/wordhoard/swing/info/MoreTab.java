package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;
import javax.swing.table.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	Word more tab.
 */

class MoreTab extends JPanel {
	
	/**	Creates a new word more tab.
	 *
	 *	@param	word			Word.
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
	 *	@param	size			Panel size.
	 */

	MoreTab (Word word, final Corpus corpus, int fontSize, 
		Insets insets, int minLabelWidth, int maxValueWidth,
		Dimension size)
	{
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(size);
		
		SpeakerPanel speakerPanel = new SpeakerPanel(word, corpus,
			fontSize, insets, minLabelWidth, maxValueWidth);
		speakerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(speakerPanel);
		add(Box.createVerticalStrut(10));
		
		WordAttributesPanel wordAttributesPanel = 
			new WordAttributesPanel(word, corpus,
				fontSize, insets, minLabelWidth, maxValueWidth);
		wordAttributesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(wordAttributesPanel);
		add(Box.createVerticalStrut(10));
		
		LocationPanel locationPanel = new LocationPanel(word, corpus,
			fontSize, insets, minLabelWidth, maxValueWidth);
		locationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(locationPanel);
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

