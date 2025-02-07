package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;

/**	Environment information.
 */

public class Env {

	/** The operating system. */

	public static final String OSNAME =
		System.getProperty("os.name");

	/**	True if running on Mac OS X. */

	public static final boolean MACOSX =
		System.getProperty("os.name").equals("Mac OS X");

	/**	True if running on some version of MS Windows. */

	public static final boolean WINDOWSOS =
		System.getProperty("os.name").toLowerCase().startsWith("windows");

	/**	Menu shortcut key mask. */

	public static final int MENU_SHORTCUT_KEY_MASK =
		Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

	/**	Menu shortcut key mask with shift key. */

	public static final int MENU_SHORTCUT_SHIFT_KEY_MASK =
		MENU_SHORTCUT_KEY_MASK + InputEvent.SHIFT_DOWN_MASK;

	/** Line separator. */

	public static final String LINE_SEPARATOR =
		System.getProperty("line.separator");

	/** Don't allow instantiation, do allow overrides. */

	protected Env()
	{
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

