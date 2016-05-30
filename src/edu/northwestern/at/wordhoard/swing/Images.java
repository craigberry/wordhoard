package edu.northwestern.at.wordhoard.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.util.*;
import java.net.*;

/**	Images.
 *
 *	<p>This static class caches and makes available all of the images used
 *	by the client, including icons.
 */

public class Images {

	/** The image cache. Maps file names (e.g. "me.gif") to image icons. */

	private static HashMap cache = new HashMap(255);

	/**	Gets an image.
	 *
	 *	@param		name		The image file name (e.g., "me.gif").
	 *
	 *	@return		The image, or null if not found.
	 */

	public static ImageIcon get (String name) {
		ImageIcon result = (ImageIcon)cache.get(name);
		if (result != null) return result;
		String path = 
			"edu/northwestern/at/wordhoard/swing/resources/" + name;
		URL iconURL = 
			Images.class.getClassLoader().getResource(path);
		result = new ImageIcon(iconURL);
		cache.put(name, result);
		return result;
	}

	/** Hides the default no-arg constructor. */

	private Images () {
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

