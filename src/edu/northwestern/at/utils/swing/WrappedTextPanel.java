package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

/**	A panel containing word-wrapped text.
 */

public class WrappedTextPanel extends JPanel {

	/**	Creates a new wrapped text panel.
	 *
	 *	@param	text		Text.
	 *
	 *	@param	font		Font.
	 *
	 *	@param	width		Panel width.
	 */
	 
	public WrappedTextPanel (String text, Font font, int width) {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		FontMetrics metrics = getFontMetrics(font);
		StringTokenizer tok = new StringTokenizer(text, " ");
		int n = tok.countTokens();
		String[] words = new String[n];
		for (int i = 0; i < n; i++) words[i] = tok.nextToken();
		int start = 0;
		while (start < n) {
			int end = start;
			String line = words[start];
			if (metrics.stringWidth(line) < width) {
				while (true) {
					int k = end+1;
					if (k >= n) break;
					String test = line + ' ' + words[k];
					if (metrics.stringWidth(test) >= width) break;
					line = test;
					end = k;
				}
			}
			JLabel label = new JLabel(line);
			label.setFont(font);
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(label);
			start = end+1;
		}		
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

