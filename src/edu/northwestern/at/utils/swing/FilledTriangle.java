package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

/**	Filled triangle Swing components.
 *
 *	<p>These components paint a filled up or down triangle right
 *	justified in their bounds and centered vertically. They are
 *	useful for sorting direction indicators.
 */

public class FilledTriangle extends JComponent {

	/**	True if up, false if down. */

	private boolean up;

	/**	Width in pixels of triangle base. */

	private int width;

	/**	Constructs a new filled triangle component.
	 *
	 *	@param	up		True if up, false if down.
	 *
	 *	@param	width	Width in pixels of triangle base.
	 */

	public FilledTriangle (boolean up, int width) {
		super();
		this.up = up;
		this.width = width;
		setPreferredSize(new Dimension(width+3, width+2));
	}

	/**	Paints the component.
	 *
	 *	@param	g		Graphics environment.
	 */

	public void paintComponent (Graphics g) {
		super.paintComponent(g);
		Dimension d = getSize();
		int w = d.width;
		int h = d.height;
		int y = h/2;
		int z = width/2;
		int[] xPoints = new int[] {w-width-1, w-1, w-z-1};
		int[] yPoints;
		if (up) {
			yPoints = new int[] {y+z, y+z, y-z};
		} else {
			yPoints = new int[] {y-z, y-z, y+z};
		}
		g.fillPolygon(xPoints, yPoints, 3);
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

