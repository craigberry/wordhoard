package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;

/**	Utilities for positioning Swing windows. */

public class WindowPositioning {

	/**	Slop pixels reserved at bottom of window for task bar or dock or
		whatever.
	 */

	final static private int BOTTOM_SLOP = 50;

	/**	Centers one window over another window.
	 *
	 *	<p>This method is typically called after the window to be centered
	 *	has been packed, but before it is shown.
	 *
	 *	@param	window1		The window to be centered.
	 *
	 *	@param	window2		The window over which window1 is to be centered.
	 *						If this window is null, window1 is centered over
	 *						the screen.
	 *
	 *	@param	offset		The vertical offset. Window1 is centered over
	 *						window2 horizontally and positioned this many
	 *						pixels below the top of window2.
	 */

	public static void centerWindowOverWindow (Window window1,
		Window window2, int offset)
	{
		Dimension screenSize = window1.getToolkit().getScreenSize();
		Dimension window1Size = window1.getSize();
		Dimension window2Size;
		Point window2Location;
		int y;
		if (window2 == null) {
			window2Size = screenSize;
			window2Location = new Point(0, 0);
			y = (screenSize.height - window1Size.height) / 2;
		} else {
			window2Size = window2.getSize();
			window2Location = window2.getLocation();
			y = window2Location.y + offset;
		}
		Point window1Location = new Point(
			window2Location.x + (window2Size.width-window1Size.width) /2 ,
			y);
		if (window1Location.x <= 0) {
			window1Location.x = 5;
		} else if (window1Location.x + window1Size.width >= screenSize.width) {
			window1Location.x = screenSize.width - window1Size.width - 5;
		}
		if (window1Location.y + window1Size.height >= screenSize.height -
			BOTTOM_SLOP)
				window1Location.y = screenSize.height - window1Size.height -
					BOTTOM_SLOP - 5;
		window1.setLocation(window1Location);
	}

	/**	Staggers one window over another window.
	 *
	 *	<p>This method is typically called after the window to be staggered
	 *	has been packed, but before it is shown.
	 *
	 *	@param	window1		The window to be staggered.
	 *
	 *	@param	window2		The window over which window1 is to be staggered.
	 *						If this window is null, window1 is positioned
	 *						at the top left of the screen.
	 *
	 *	@param	offset		The offset. Window1 is staggered over
	 *						window2 and positioned this many
	 *						pixels below the top of window2 and to the
	 *						right of window2.
	 */

	public static void staggerWindowOverWindow (Window window1,
		Window window2, int offset)
	{
		Dimension screenSize = window1.getToolkit().getScreenSize();
		Dimension windowSize = window1.getSize();
		Point location = new Point(0, 0);
		if (window2 == null) {
			location.x = 10;
			location.y = 30;
		} else {
			location = window2.getLocation();
			location.x += offset;
			location.y += offset;
			if (location.x + windowSize.width >= screenSize.width ||
				location.y + windowSize.height >= screenSize.height -
					BOTTOM_SLOP)
			{
				location.x = 10;
				location.y = 30;
			}
		}
		window1.setLocation(location);
	}

	/**	Zooms a window to fill the main screen or to a max size.
	 *
	 *	@param	window 	The window.
	 *
	 *	@param	d		The maximum window dimension.
	 */

	public static void zoom (Window window, Dimension d) {
		Dimension size = window.getToolkit().getScreenSize();
		window.setLocation(new Point(10, 30));
		size.width -= 20;
		size.height -= BOTTOM_SLOP + 30;
		size.width = Math.min(size.width, d.width);
		size.height = Math.min(size.height, d.height);
		window.setSize(size);
	}

	/**	Trims a window's size so it fits in the main screen,
	 *	if necessary.
	 *
	 *	<p>The window's content pane must be a JComponent.
	 *
	 *	<p>This method should be called before packing the window.
	 *
	 *	@param	window	The window.
	 */

	public static void trim (JFrame window) {
		Dimension screenSize = window.getToolkit().getScreenSize();
		screenSize.width -= 20;
		screenSize.height -= BOTTOM_SLOP + 70;
		JComponent contentPane = (JComponent)window.getContentPane();
		Dimension d = contentPane.getPreferredSize();
		d.width = Math.min(d.width, screenSize.width);
		d.height = Math.min(d.height, screenSize.height);
		contentPane.setPreferredSize(d);
	}

	/**	Returns true if a window is too big for an 800 by 600 screen.
	 *
	 *	@param	window		The window.
	 *
	 *	@return				True if too big.
	 */

	public static boolean tooBig (JFrame window) {
		Dimension d = window.getSize();
		return d.width > 780 || d.height > 560;
	}

	/** Hides the default no-arg constructor. */

	private WindowPositioning () {
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

