package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

/**	Parameters for the X classes.
 *
 *	<p>These parameters supply default values for the XList, XTable,
 *	and XTree classes. They may be set at runtime based on the system
 *	and look and feel, or you may use the following default values which
 *	look good on Mac OS X with the Aqua look and feel:
 *
 *	<ul>
 *	<li>Font = the default JLabel font with a point size of 12.
 *	<li>Row height = 16 pixels.
 *	</ul>
 *
 *	<p>If a font parameter is null the system default is used.
 *	If a row height is 0 the system default is used.
 */

public class XParameters {

	/**	The system's JLabel font. */

	static private Font font = (new JLabel()).getFont();

	/**	The default default font. */

	static private Font defaultFont = new Font(font.getName(),
		font.getStyle(), 12);

	/**	The default default row height. */

	static private int defaultRowHeight = 16;

	/**	The default XList font. */

	static public Font listFont = defaultFont;

	/**	The default XList row height. */

	static public int listRowHeight = defaultRowHeight;

	/**	The default XTable font. */

	static public Font tableFont = defaultFont;

	/**	The default XTable row height. */

	static public int tableRowHeight = defaultRowHeight;

	/**	The default XTree font. */

	static public Font treeFont = defaultFont;

	/**	The default XTree row height. */

	static public int treeRowHeight = defaultRowHeight;

	/**	Hides the default no-arg constructor. */

	private XParameters () {
		throw new UnsupportedOperationException();
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

