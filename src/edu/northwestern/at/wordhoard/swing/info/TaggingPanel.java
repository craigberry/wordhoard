package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.swing.*;

/**	Tagging data panel.
 */
 
class TaggingPanel extends JPanel {

	/**	Tagging data. */
	
	private TaggingData taggingData;
	
	/**	Labeled column. */
	
	private LabeledColumn col;

	/**	Creates a new tagging data panel.
	 *
	 *	@param	taggingData		Tagging data.
	 *
	 *	@param	fontSize		Font size.
	 *
	 *	@param	insets			Insets for labeled column.
	 *
	 *	@param	minLabelWidth	Min label width for labeled column.
	 *
	 *	@param	maxValueWidth	Max value width for labeled column.
	 */

	TaggingPanel (TaggingData taggingData, int fontSize, 
		Insets insets, int minLabelWidth, int maxValueWidth) 
	{
		this.taggingData = taggingData;
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		FontManager fontManager = new FontManager();
		Font romanFont = fontManager.getFont(fontSize);

		JLabel label = new JLabel("Tagging Data");
		label.setFont(romanFont);
		label.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);
		
		col = new LabeledColumn(insets, romanFont, minLabelWidth, maxValueWidth);
		col.setBackground(Color.white);

		addPair(TaggingData.LEMMA, "Lemmas");
		addPair(TaggingData.POS, "Parts of speech");
		addPair(TaggingData.WORD_CLASS, "Word classes");
		addPair(TaggingData.SPELLING, "Spellings");
		addPair(TaggingData.SPEAKER, "Speakers");
		addPair(TaggingData.GENDER, "Speaker gender");
		addPair(TaggingData.MORTALITY, "Speaker mortality");
		addPair(TaggingData.PROSODIC, "Prose or verse");
		addPair(TaggingData.METRICAL_SHAPE, "Metrical shapes");
		addPair(TaggingData.PUB_DATES, "Publication dates");
		
		col.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(col);
		
		setMaximumSize(getPreferredSize());
	}
	
	/**	Adds a label/value pair to the column.
	 *
	 *	@param	mask		Tag mask.
	 *
	 *	@param	label		Label.
	 */
	
	private void addPair (long mask, String label) {
		String value = taggingData.contains(mask) ? "yes" : "no";
		col.addPair(label, value);
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

