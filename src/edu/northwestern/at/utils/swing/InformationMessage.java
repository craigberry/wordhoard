package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**	An informational message alert.
 *
 *	<p>The alert includes the PLAF information icon, an informative message, and
 *	an "OK" button.</p>
 */

public class InformationMessage extends ModalDialog
{
	/**	The PLAF information icon. */

	private static JLabel infoIcon =
		new JLabel( UIManager.getLookAndFeel().getDefaults().getIcon(
			"OptionPane.informationIcon") );

	/**	Constructs and displays an information message alert.
	 *
	 *	@param	title		Title for message.
	 *
	 *	@param	msg			The informative message to display.
	 *
	 *	@param	parent		The parent window, or null if none. The dialog
	 *						is positioned centered over its parent window
	 *						horizontally and positioned with its top
	 *						25 pixels below the top of its parent.
	 */

	public InformationMessage(String title, String msg, Window parent) {
		super(title);
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
		box.add(infoIcon);
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
		show(parent);
	}

	/**	Constructs and displays an information message alert.
	 *
	 *	@param	msg			The informative message to display.
	 *
	 *	@param	parent		The parent window, or null if none. The dialog
	 *						is positioned centered over its parent window
	 *						horizontally and positioned with its top
	 *						25 pixels below the top of its parent.
	 */

	public InformationMessage(String msg, Window parent) {
		this(null, msg, parent);
	}

	/**	Constructs and displays an informative message alert with no
	 *	parent window.
	 *
	 *	@param	msg			The information message.
	 */

	public InformationMessage (String msg) {
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

