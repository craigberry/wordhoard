package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.styledtext.*;

/**	The system clipboard.
 *
 *	<p>On Mac OS X the system clipboard is evidently incapable of containing
 *	custom Java objects. This static class hacks around this deficiency by
 *	keeping a parallel private Mac OS X clipboard.
 */

public class SystemClipboard {

	/**	True if running on Mac OS X. */

	private static final boolean MACOSX =
		System.getProperty("os.name").equals("Mac OS X");

	/**	The system clipboard. */

	private static final Clipboard clipboard =
		Toolkit.getDefaultToolkit().getSystemClipboard();

	/**	Private clipboard for Mac OS X. */

	private static final Clipboard macPrivateClipboard =
		new Clipboard("Mac private");

	/**	Sets the contents of the system clipboard.
	 *
	 *	@param	contents	The transferable object representing the
	 *						clipboard content.
	 *
	 *	@param	owner		The object which owns the clipboard content.
	 */

	public static void setContents (Transferable contents,
		ClipboardOwner owner)
	{
		clipboard.setContents(contents, owner);
		if (MACOSX) macPrivateClipboard.setContents(contents, owner);
	}

	/**	Gets the contents of the system clipboard.
	 *
	 *	@param	requestor	The object requesting the clipboard data (not used).
	 *
	 *	@return				The current transferable object on the clipboard.
	 */

	public static Transferable getContents (Object requestor) {
		Transferable content = clipboard.getContents(requestor);
		if (MACOSX) {
			Transferable macPrivateContent =
				macPrivateClipboard.getContents(requestor);
			if (content != null && macPrivateContent != null) {
					try {
						String contentString =
							(String)content.getTransferData(
								DataFlavor.stringFlavor);
						String macPrivateContentString =
							(String)macPrivateContent.getTransferData(
								DataFlavor.stringFlavor);
						if (StringUtils.equals(contentString,
							macPrivateContentString))
								content = macPrivateContent;
					} catch (Exception e) {
					}
				}
			}
		return content;
	}

	/** Don't allow instantiation, do allow overrides. */

	protected SystemClipboard()
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

