package edu.northwestern.at.utils.swing.icons;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

/**	Red bullet icon. */

public class RedBulletIcon implements Icon {

	/**	Bullet size. */

	private int size;

	/**	Constructs a new icon.
	 * @param	size	The size of the icon.
	*/

	public RedBulletIcon (int size) {
		this.size = size;
	}

	/**	Gets the icon height (size).
	 *
	 *	@return		The icon height.
	 */

	public int getIconHeight () {
		return size;
	}

	/**	Gets the icon width (size).
	 *
	 *	@return		The icon width.
	 */

	public int getIconWidth () {
		return size;
	}

	/**	Paints the icon.
	 *
	 *	@param	c	Component.
	 *
	 *	@param	g	Graphics environment.
	 *
	 *	@param	x	X coordinate of icon.
	 *
	 *	@param	y	Y coordinate of icon.
	 */

	public void paintIcon (Component c, Graphics g, int x, int y) {
		Color savedColor = g.getColor();
		g.setColor(Color.red);
		g.fillOval(x, y, size, size);
		g.setColor(savedColor);
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

