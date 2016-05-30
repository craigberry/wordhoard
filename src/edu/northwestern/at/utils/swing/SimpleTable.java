package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

/**	A simple table of strings.
 */

public class SimpleTable extends JPanel {

	/**	Creates a new table of strings.
	 *
	 *	@param	cells		Matrix of strings to format as a table.
	 *
	 *	@param	colAlign	Array of SwingConstants column alignment options.
	 *
	 *	@param	font		Font.
	 *
	 *	@param	colSpace	Spacing between columns.
	 *
	 *	@param	rowSpace	Spacing between rows.
	 */

	public SimpleTable (String[][] cells, int[] colAlign, Font font,
		int colSpace, int rowSpace)
	{
		int nrow = cells.length;
		int ncol = cells[0].length;
		JLabel[][] labels = new JLabel[nrow][ncol];
		int[] maxColWid = new int[ncol];
		for (int i = 0; i < nrow; i++) {
			for (int j = 0; j < ncol; j++) {
				JLabel label = new JLabel(cells[i][j]);
				label.setFont(font);
				label.setHorizontalAlignment(colAlign[j]);
				labels[i][j] = label;
				int wid = label.getPreferredSize().width;
				maxColWid[j] = Math.max(maxColWid[j], wid);
			}
		}
		int height = labels[0][0].getPreferredSize().height;
		for (int j = 0; j < ncol; j++) {
			Dimension dim = new Dimension(maxColWid[j], height);
			for (int i = 0; i < nrow; i++) {
				JLabel label = labels[i][j];
				label.setPreferredSize(dim);
				label.setMinimumSize(dim);
				label.setMaximumSize(dim);
			}
		}
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for (int i = 0; i < nrow; i++) {
			JPanel row = new JPanel();
			row.setBackground(Color.white);
			row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
			for (int j = 0; j < ncol; j++) {
				row.add(labels[i][j]);
				if (colSpace > 0 && j < ncol-1)
					row.add(Box.createHorizontalStrut(colSpace));
			}
			row.setAlignmentX(Component.LEFT_ALIGNMENT);
			row.setMaximumSize(row.getPreferredSize());
			add(row);
			if (rowSpace > 0 && i < nrow-1)
				add(Box.createVerticalStrut(rowSpace));
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

