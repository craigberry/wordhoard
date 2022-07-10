package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.swing.*;

/**	Morphology panel.
 */
 
class MorphologyPanel extends JPanel {

	/**	Labeled column. */
	
	private LabeledColumn morphologyCol;
	
	/**	Font size. */
	
	private int fontSize;
	
	/**	Font manager. */
	
	private FontManager fontManager;

	/**	Creates a new morphology panel.
	 *
	 *	@param	wordPart			Word part.
	 *
	 *	@param	fontSize			Font size.
	 *
	 *	@param	insets				Insets for labeled column.
	 *
	 *	@param	minLabelWidth		Min label width for labeled column.
	 *
	 *	@param	maxValueWidth		Max value width for labeled column.
	 */

	MorphologyPanel (WordPart wordPart, int fontSize, 
		Insets insets, int minLabelWidth, int maxValueWidth) 
	{
		this.fontSize = fontSize;
		Word word = wordPart.getWord();
		LemPos lemPos = wordPart.getLemPos();
		Lemma lemma = lemPos == null ? null : lemPos.getLemma();
		Pos pos = lemPos == null ? null : lemPos.getPos();
		Spelling spelling = word.getSpelling();
		WordClass wordClass = lemma == null ? null : lemma.getWordClass();
	
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		fontManager = new FontManager();
		FontInfo romanFontInfo = fontManager.getFontInfo(fontSize);
		Font romanFont = romanFontInfo.getFont();		

		JLabel morphologyLabel = new JLabel("Morphology");
		morphologyLabel.setFont(romanFont);
		morphologyLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		morphologyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(morphologyLabel);
		
		morphologyCol = 
			new LabeledColumn(insets, romanFont, minLabelWidth, maxValueWidth);
		morphologyCol.setBackground(Color.white);
		
		if (lemma != null) addPair("Lemma", lemma.getTag());
		
		if (pos != null) {
			addPair("Part of Speech", pos.getTagWithDescription());
		}
		
		if (wordClass != null) {
			addPair("Major word class", 
				wordClass.getMajorWordClass().toString());
			addPair("Word class", wordClass.getTag());
		}

		if (spelling != null) addPair("Spelling", spelling);
		
		morphologyCol.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(morphologyCol);
			
		Dimension size = getPreferredSize();
		if (size.width < 300) {
			size.width = 300;
			setPreferredSize(size);
		}
		
		setMaximumSize(getPreferredSize());
	}
	
	/**	Adds a label/value pair to the panel. 
	 *
	 *	@param	label		Label.
	 *
	 *	@param	value		Value.
	 */
	
	private void addPair (String label, String value) {
		morphologyCol.addPair(label, value);
	}
	
	/**	Adds a label/value pair to the panel. 
	 *
	 *	@param	label		Label.
	 *
	 *	@param	value		Value.
	 */
	
	private void addPair (String label, Spelling value) {
		Font valueFont = fontManager.getFont(value.getCharset(),
			fontSize);
		morphologyCol.addPair(label, value.toString(), valueFont);
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

