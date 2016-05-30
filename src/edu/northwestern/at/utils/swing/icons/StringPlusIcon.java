package edu.northwestern.at.utils.swing.icons;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import javax.swing.*;

/**	A string which knows how to append an icon to its JLabel.
 */

public class StringPlusIcon implements AddIcon {

	/**	The string. */

	protected String value;

	/**	The icon, or null if none. */

	protected Icon icon;

	/**	Constructs a new StringPlusIcon object.
	 *
	 *	@param	value		The string.
	 *
	 *	@param	icon		The icon, or null if none.
	 */

	public StringPlusIcon (String value, Icon icon) {
		this.value = value;
		this.icon = icon;
	}

	/**	Appends the icon to the JLabel.
	 *
	 *	@param	label		The JLabel.
	 */

	public void addIcon (JLabel label) {
		if (icon == null) return;
		label.setIcon(icon);
		label.setHorizontalTextPosition(SwingConstants.LEADING);
	}

	/**	Returns the string.
	 *
	 *	@return		The string.
	 */

	public String toString () {
		return value;
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

