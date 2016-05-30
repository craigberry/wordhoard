package edu.northwestern.at.utils.swing.icons;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

/**	Emoticons.
 *
 *	<p>
 *	Emoticons are small icons. The ".gif" files reside in the
 *	"resources/emoticons" subdirectory of the package directory.
 *	</p>
 *
 *	<p>
 *	This static class manages and encapsulates access to the emoticons.
 *	</p>
 */

public class Emoticons
{
	/* Holds emoticons. */

	private static SmallIcons emoticons =
		new SmallIcons
		(
			new String[]
			{					// index
				"smile",		// 0
				"sad",			// 1
				"angry",		// 2
				"sick",			// 3
				"tired",		// 4
				"devil",		// 5
				"biggrin",		// 6
				"tongue",		// 7
				"attention",	// 8
				"alert",		// 9
				"idea",			// 10
				"wink",			// 11
			},
			"edu/northwestern/at/utils/swing/icons/resources/emoticons/"
		);

	/* Text smileys and corresponding icons.
	 *
	 *	<p>Make sure that if a shortcut A is a suffix of a shortcut B
	 *	then B precedes A in the list (e.g., >:) for devil must precede
	 *	:) for smile).
	 */

	public static String[][] smileyFaces =
		new String[][]
		{
			{ ":-)" , "smile" },
			{ ">:)"	, "devil" },
			{ ":)"	, "smile" },
			{ ":-(" , "sad" },
			{ ":("	, "sad" },
			{ ";-)"	, "wink" },
			{ ";)"	, "wink" },
			{ ":-&"	, "angry" },
			{ ":-$"	, "sick" },
			{ "|-)"	, "tired" },
			{ ":-D"	, "biggrin" },
			{ ":-J"	, "tongue" },
			{ "[!]"	, "attention" },
			{ "<!>"	, "alert" },
			{ "C=="	, "idea" },
		};

	/** End characters in smiley face strings.
	 *
	 *	<p>
	 *	Update this to match "smileyFaces" above.
	 *	</p>
	 */

	public static String smileyFacesEndChars = ")(&$DJ]>=";

	/**	Gets an icon by index.
	 *
	 *	@param	index		The index of the icon.
	 *
	 *	@return				The image icon.
	 */

	public static ImageIcon get (int index)	{
		return emoticons.get( index );
	}

	/**	Gets an emoticon by name.
	 *
	 *	@param	name		The emoticon name.
	 *
	 *	@return				The image icon for the emoticon.
	 */

	public static ImageIcon get (String name) {
		return emoticons.get( name );
	}

	/**	Creates and returns a popup menu for all the emoticons.
	 *
	 *	@param	listener	Shared action listener for all the menu items.
	 *
	 *	@param	shortcuts	True to include keyboard shortcuts in menu item
	 *						names.
	 *
	 *	@return				The popup menu.
	 */

	public static JPopupMenu popup (ActionListener listener,
		boolean shortcuts)
	{
		JPopupMenu result = emoticons.popup(
			new int[] {0, 1, 2, 11, 3, 4, 5, 6, 7, 8, 9, 10},
			listener );
		if (shortcuts) {
			int ct = result.getComponentCount();
			int numShortcuts = smileyFaces.length;
			for (int i = 0; i < ct; i++) {
				JMenuItem item = (JMenuItem)result.getComponent(i);
				String name = item.getText();
				StringBuffer buf = new StringBuffer(name);
				boolean first = true;
				for (int j = 0; j < numShortcuts; j++) {
					if (name.equals(smileyFaces[j][1])) {
						if (first) {
							buf.append("   ");
							first = false;
						} else {
							buf.append(" or ");
						}
						buf.append(smileyFaces[j][0]);
					}
				}
				item.setText(buf.toString());
			}
		}
		return result;
	}

	/**	Gets the index of an emoticon.
	 *
	 *	@param	icon		The image icon.
	 *
	 *	@return				The index.
	 */

	public static int indexOf (ImageIcon icon) {
		return emoticons.indexOf( icon );
	}

	/** Hide no-args constructor. */

	private Emoticons(){
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

