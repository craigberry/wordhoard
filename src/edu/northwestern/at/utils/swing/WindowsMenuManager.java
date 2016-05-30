package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;

import edu.northwestern.at.utils.*;

/**	An abstract window base class that manages Windows menus.
 *
 *	<p>This class keeps track of all open windows. Each window has
 *	a "Windows" menu that lists all the windows in order by title.
 *	Selecting a window title from the menu brings the window to the
 *	front.
 */

public abstract class WindowsMenuManager extends JFrame {

	/**	Next available window ordinal. We attach a unique ordinal to each
	 *	window and use it to disambiguate windows with identical titles.
	 */

	private static int nextOrdinal = 0;

	/**	A comparator for sorting windows in case-insensitive increasing
	 *	alphabetical order by window title, then by ordinal.
	 */

	private static Comparator windowComparator =
		new Comparator () {
			public int compare (Object o1, Object o2) {
				WindowsMenuManager w1 = (WindowsMenuManager)o1;
				WindowsMenuManager w2 = (WindowsMenuManager)o2;
				int result = Compare.compareIgnoreCase(w1.getTitle(),
					w2.getTitle());
				if (result != 0) return result;
				return Compare.compare(w1.ordinal, w2.ordinal);
			}
		};

	/**	Set of all open windows maintained in order by title. */

	private static TreeSet windows = new TreeSet(windowComparator);
	
	/**	Active window, or null if none. */
	
	private static WindowsMenuManager activeWindow = null;

	/**	The window ordinal. */

	private int ordinal;

	/**	The Windows menu. */

	private JMenu windowsMenu = new JMenu("Windows");

	/** The index in the Windows menu of the first window in the list =
	 *  the number of fixed items at the beginning of the menu.
	 */

	private int firstWindowIndex;

	/**	Constructs a new WindowsMenuManager window.
	 *
	 *	@param	title		Window title.
	 */

	public WindowsMenuManager (String title) {
		super(title);
		ordinal = nextOrdinal++;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	/**	Sets the menu bar.
	 *
	 *	@param	menuBar		The menu bar.
	 */

	public void setJMenuBar (JMenuBar menuBar) {
		firstWindowIndex = windowsMenu.getItemCount();
		super.setJMenuBar(menuBar);
	}

	/**	Show or hide the window.
	 *
	 *	@param	show	true to show window, false to hide it.
	 */

	public void setVisible(boolean show) {
		if (show) {
			windows.add(this);
			rebuildAll();
		}
		super.setVisible(show);
	}

	/**	Disposes the window.
	 */

	public void dispose () {
		windows.remove(this);
		rebuildAll();
		super.dispose();
		if (windows.size() == 0) handleLastWindowClosed();
	}

	/**	Sets the window title.
	 *
	 *	@param	title		The new window title.
	 */

	public void setTitle (String title) {
		windows.remove(this);
		super.setTitle(title);
		windows.add(this);
		rebuildAll();
	}

	/**	Handles the last window closed.
	 *
	 *	<p>Subclasses may override this method to take whatever action
	 *	they deem appropriate when the last window is closed (e.g., quit
	 *	the program). The default action is to do nothing.
	 */

	public void handleLastWindowClosed() {
	}

	/**	Gets the Windows menu.
	 *
	 *	@return		The Windows menu.
	 */

	public JMenu getWindowsMenu () {
		return windowsMenu;
	}

	/**	Sets the windows menu.
	 *
	 *	@param	windowsMenu	The windows menu.
	 */

	public void setWindowsMenu (JMenu windowsMenu) {
		this.windowsMenu = windowsMenu;
		setJMenuBar(getJMenuBar());
	}

	/**	Gets all the open windows.
	 *
	 *	@return		Array of all open windows.
	 */

	public static WindowsMenuManager[] getAllOpenWindows () {
		return (WindowsMenuManager[])windows.toArray(
			new WindowsMenuManager[windows.size()]);
	}
	
	/**	Gets the active window.
	 *
	 *	@return		Active window, or null if none.
	 */
	 
	public static WindowsMenuManager getActiveWindow () {
		return activeWindow;
	}

	/**	Handles the bring this window to front command. */

	private ActionListener bringWindowToFront =
		new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				WindowsMenuManager.this.setVisible(true);
				WindowsMenuManager.this.toFront();
			}
		};

	/**	Rebuilds the windows menu.
	 */

	private void rebuild () {
		while (windowsMenu.getItemCount() > firstWindowIndex)
			windowsMenu.remove(firstWindowIndex);
		for (Iterator it = windows.iterator(); it.hasNext(); ) {
			WindowsMenuManager window = (WindowsMenuManager)it.next();
			JMenuItem item = new JMenuItem(window.getTitle());
			if (window == this) {
				item.setEnabled(false);
				activeWindow = window;
			}
			item.addActionListener(window.bringWindowToFront);
			windowsMenu.add(item);
		}
	}

	/**	Rebuilds all the windows menus.
	 */

	private static void rebuildAll () {
		for (Iterator it = windows.iterator(); it.hasNext(); ) {
			WindowsMenuManager window = (WindowsMenuManager)it.next();
			window.rebuild();
		}
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

