package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.swing.*;

/**	Translations panel.
 */
 
class TranslationsPanel extends JPanel {

	/**	Creates a new translations panel.
	 *
	 *	@param	translations	Collection of translation names.
	 *
	 *	@param	fontSize		Font size.
	 *
	 *	@param	insets			Insets for labeled column.
	 *
	 *	@param	minLabelWidth	Min label width for labeled column.
	 *
	 *	@param	maxValueWidth	Max value width for labeled column.
	 */

	TranslationsPanel (Collection translations, int fontSize, 
		Insets insets, int minLabelWidth, int maxValueWidth) 
	{
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		FontManager fontManager = new FontManager();
		Font romanFont = fontManager.getFont(fontSize);

		JLabel label = new JLabel("Translations, Transliterations, Etc.");
		label.setFont(romanFont);
		label.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);
		
		LabeledColumn col = 
			new LabeledColumn(insets, romanFont, 40, maxValueWidth);
		col.setBackground(Color.white);
		
		for (Iterator it = translations.iterator(); it.hasNext(); ) {
			String tran = (String)it.next();
			col.addPair("", tran);
		}
		
		col.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(col);
		
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

