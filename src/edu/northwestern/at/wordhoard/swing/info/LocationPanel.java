package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	Location panel.
 */
 
class LocationPanel extends JPanel {

	/**	Creates a new location panel.
	 *
	 *	@param	word			Word occurrence.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	fontSize		Font size.
	 *
	 *	@param	insets			Insets for labeled column.
	 *
	 *	@param	minLabelWidth	Min label width for labeled column.
	 *
	 *	@param	maxValueWidth	Max value width for labeled column.
	 */

	LocationPanel (Word word, Corpus corpus, int fontSize, 
		Insets insets, int minLabelWidth, int maxValueWidth) 
	{
		Work work = word.getWork();
		WorkPart part = word.getWorkPart();
		Line line = word.getLine();
		String path = word.getPath();
		String tag = word.getTag();
	
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		FontManager fontManager = new FontManager();
		Font romanFont = fontManager.getFont(fontSize);

		JLabel locationLabel = new JLabel("Location");
		locationLabel.setFont(romanFont);
		locationLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		locationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(locationLabel);
		
		LabeledColumn locationCol = 
			new LabeledColumn(insets, romanFont, minLabelWidth, maxValueWidth);
		locationCol.setBackground(Color.white);
		if (corpus != null) locationCol.addPair("Corpus", corpus.getTitle());
		locationCol.addPair("Work", work.getShortTitle());
		locationCol.addPair("Part", part.getFullTitle());
		if (line != null) locationCol.addPair("Line", line.getLabel());
		locationCol.addPair("Path", path);
		JLabel tagLabel = new JLabel(tag);
		tagLabel.setFont(romanFont);
		locationCol.addPair("Tag", tagLabel);
		locationCol.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(locationCol);
		
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

