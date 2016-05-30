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

/**	Benson panel.
 */
 
class BensonPanel extends JPanel {

	/**	Creates a new Benson panel.
	 *
	 *	@param	bensonLemPos		Benson word form.
	 *
	 *	@param	fontSize			Font size.
	 *
	 *	@param	insets				Insets for labeled column.
	 *
	 *	@param	minLabelWidth		Min label width for labeled column.
	 *
	 *	@param	maxValueWidth		Max value width for labeled column.
	 */

	BensonPanel (BensonLemPos bensonLemPos, int fontSize, 
		Insets insets, int minLabelWidth, int maxValueWidth) 
	{
		BensonLemma bensonLemma = bensonLemPos.getLemma();
		BensonPos bensonPos = bensonLemPos.getPos();
		String definition = bensonLemma.getDefinition();
		String comment = bensonLemma.getComment();
		String oedLemma = bensonLemma.getOedLemma();
	
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		FontManager fontManager = new FontManager();
		Font romanFont = fontManager.getFont(fontSize);

		JLabel bensonLabel = new JLabel("Benson\u2019s Gloss");
		bensonLabel.setFont(romanFont);
		bensonLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		bensonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(bensonLabel);
	
		LabeledColumn bensonCol = 
			new LabeledColumn(insets, romanFont, minLabelWidth, maxValueWidth);
		bensonCol.setBackground(Color.white);
		bensonCol.addPair("Lemma", bensonLemma.getTag());
		if (definition != null) bensonCol.addPair("Definition", definition);
		if (comment != null) bensonCol.addPair("Comment", comment);
		if (oedLemma != null) bensonCol.addPair("OED Lemma", oedLemma);
		if (bensonPos != null) bensonCol.addPair("Part of Speech", 
			bensonPos.getTagWithDescription());
		bensonCol.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(bensonCol);
		
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

