package edu.northwestern.at.utils.swing.icons;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

import edu.northwestern.at.utils.*;

/**	SmallIcons.
 *
 *	<p>SmallIcons are small icons. The ".gif" files reside in a
 *	specified subdirectory of the package directory.</p>
 *
 *	<p>The SmallIcons class provides methods to manage and encapsulate
 *	access to collections of small icons.  See the {@link Emoticons} and
 *	{@link StarIcons} classes for examples.</p>
 */

public class SmallIcons {

	/**	Small icon names. */

	protected String[] names = null;

	/** 	Path for icons. */

	protected String path = "";

	/**	Icon cache. */

	protected ImageIcon[] icons = null;

	/** Create small icons accessor.
	 *
	 *	@param	names	Names of icons.
	 *	@param	path	Class path to icons.
	 */

	protected SmallIcons ( String[] names , String path ) {
		this.names = names;
		this.path = path;
		this.icons = new ImageIcon[ this.names.length ];
	}

	/**	Gets an icon by index.
	 *
	 *	@param	index			The index of the icon.
	 *
	 *	@return				The image icon.
	 */

	public ImageIcon get (int index) {
		if (icons[index] != null) return icons[index];
		String thePath =
			path + names[index] + ".gif";
		URL iconURL = SmallIcons.class.getClassLoader().getResource(thePath);
		ImageIcon icon = new ImageIcon(iconURL);
		icons[index] = icon;
		return icon;
	}

	/**	Gets an emoticon by name.
	 *
	 *	@param	name		The emoticon name.
	 *
	 *	@return				The image icon for the emoticon.
	 */

	public ImageIcon get (String name) {
		for (int i = 0; i < names.length; i++) {
			if (name.equalsIgnoreCase(names[i]))
				return get(i);
		}
		return null;
	}

	/**	Creates and returns a popup menu for all the emoticons.
	 *
	 *	@param	listener	Shared action listener for all the menu items.
	 *
	 *	@return				The popup menu.
	 */

	public JPopupMenu popup (ActionListener listener) {
		JPopupMenu result = new JPopupMenu();
		for (int i = 0; i < names.length; i++) {
			String name = names[i];
			ImageIcon icon = get(i);
			JMenuItem item = new JMenuItem(name, icon);
			item.addActionListener(listener);
			result.add(item);
		}
		return result;
	}

	/**	Creates and returns a popup menu for all the emoticons in a
	 *	specified order.
	 *
	 *	@param	order		An array of integer indexes in the order in which
	 *						the emoticons should appear in the menu.
	 *
	 *	@param	listener	Shared action listener for all the menu items.
	 *
	 *	@return				The popup menu.
	 */

	public JPopupMenu popup (int[] order, ActionListener listener) {
		JPopupMenu result = new JPopupMenu();
		for (int j = 0; j < names.length; j++) {
			int i = order[j];
			String name = names[i];
			ImageIcon icon = get(i);
			JMenuItem item = new JMenuItem(name, icon);
			item.addActionListener(listener);
			result.add(item);
		}
		return result;
	}

	/**	Gets the index of an emoticon.
	 *
	 *	@param	icon		The image icon.
	 *
	 *	@return				The index.
	 */

	public int indexOf (ImageIcon icon) {
		for (int i = 0; i < names.length; i++) {
			if (get(i).equals(icon)) return i;
		}
		return -1;
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

