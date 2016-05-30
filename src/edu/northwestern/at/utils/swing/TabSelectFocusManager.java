package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;

/**	Tab select focus manager.
 *
 *	<p>Makes tabbing into any JTextField select the entire field contents.
 */

public class TabSelectFocusManager {

	/**	True to select all text on the next focus owner change event. */
	
	private static boolean selectAll;
	
	/**	Initializes the tab select focus manager. */
	
	public static void init () {
	
		KeyboardFocusManager kfm = 
			KeyboardFocusManager.getCurrentKeyboardFocusManager();
			
		kfm.addKeyEventPostProcessor(
			new KeyEventPostProcessor() {
				public boolean postProcessKeyEvent (KeyEvent e) {
					if (e.getKeyChar() == KeyEvent.VK_TAB &&
						e.getID() == KeyEvent.KEY_RELEASED)
					{
						selectAll = true;
					}
					return false;
				}
			}
		);
		
		kfm.addPropertyChangeListener("focusOwner",
			new PropertyChangeListener() {
				public void propertyChange (PropertyChangeEvent event) {
					if (!selectAll) return;
					Object newValue = event.getNewValue();
					if (newValue == null) return;
					selectAll = false;
					if (!(newValue instanceof JTextField)) return;
					JTextField textField = (JTextField)newValue;
					textField.selectAll();
				}
			}
		);
					
	}
	
	/**	Hides the default no-arg constructor.
	 */
	 
	private TabSelectFocusManager () {
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

