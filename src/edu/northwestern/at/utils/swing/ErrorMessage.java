package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**	An error message alert.
 *
 *	<p>The alert includes the PLAF error icon, an error message, and
 *	an "OK" button.
 */

public class ErrorMessage extends ModalDialog {

	/**	The PLAF error icon. */

	private static JLabel errorIcon =
		new JLabel(UIManager.getLookAndFeel().getDefaults().getIcon(
			"OptionPane.errorIcon"));

	/**	Constructs and displays an error message alert.
	 *
	 *	@param	msg		The error message.
	 *
	 *	@param	parent		The parent window, or null if none. The dialog
	 *						is positioned centered over its parent window
	 *						horizontally and positioned with its top
	 *						just below the bottom of its parent.
	 */

	public ErrorMessage (String msg, Window parent) {
		super(null);
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
		box.add(errorIcon);
		box.add(Box.createHorizontalStrut(10));
		JComponent component = SwingUtils.buildLabelOrScrollingTextArea(msg);
		box.add(component);
		if (component instanceof JLabel) setResizable(false);
		add(box);
		addDefaultButton("OK",
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					dispose();
				}
			}
		);
		// To get the error dialog on top we have to ignore the actual parent
		// and create a new one, but we do position relative to the actual parent,
		// if any.
		JFrame fakeParent = new JFrame();
		int x, y;
		if (parent != null) {
			x = parent.getX() + parent.getWidth() / 2;
			y = parent.getY() + parent.getHeight() - 5;
		}
		else {
			// No parent, so relative to screen.
			GraphicsConfiguration config = fakeParent.getGraphicsConfiguration();
			Rectangle bounds = config.getBounds();
			Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);
			x = bounds.x + bounds.width / 2 - insets.right - fakeParent.getWidth();
			y = bounds.y + insets.top + 25;
		}
		fakeParent.setLocation(x, y);
		show(fakeParent);
	}

	/**	Constructs and displays an error message alert with no parent window.
	 *
	 *	@param	msg			The error message.
	 */

	public ErrorMessage (String msg) {
		this(msg, null);
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

