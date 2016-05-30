package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.styledtext.*;

/**	A warning message alert.
 *
 *	<p>The alert includes the PLAF warning icon (or, optionally, an icon
 *	supplied by the caller), a warning message, and
 *	a row of buttons supplied by the caller.</p>
 */

public class WarningMessage extends ModalDialog {

	/**	The PLAF warning icon. */

	private static JLabel warningIcon =
		new JLabel(UIManager.getLookAndFeel().getDefaults().getIcon(
			"OptionPane.warningIcon"));

	/**	Index of the button selected. */

	private int hit = -1;

	/**	List of buttons in left-to-right order. */

	private ArrayList buttons = new ArrayList();

	/**	Constructs a new warning message alert with specified icon.
	 *
	 *	@param	msg		The warning message.
	 *
	 *	@param	icon	The icon wrapped in a JLabel.
	 */

	public WarningMessage (String msg, JLabel icon) {
		super( null );
		JPanel box = new JPanel();
		box.setLayout( new BoxLayout(box, BoxLayout.X_AXIS));
		box.add(icon);
		box.add(Box.createHorizontalStrut(10));
		JComponent component = SwingUtils.buildLabelOrScrollingTextArea(msg);
		box.add(component);
		if (component instanceof JLabel) setResizable(false);
		add(box);
	}

	/**	Constructs a new warning message alert.
	 *
	 *	@param	msg		The warning message.
	 */

	public WarningMessage (String msg) {
		this(msg, warningIcon);
	}


	/**	Constructs a new styled text warning message alert with specified icon.
	 *
	 *	@param	msg		The warning message.
	 *
	 *	@param	icon	The icon wrapped in a JLabel.
	 */

	public WarningMessage (StyledString msg, JLabel icon) {
		super( null );
		JPanel box = new JPanel();
		box.setLayout( new BoxLayout(box, BoxLayout.X_AXIS));
		box.add(icon);
		box.add(Box.createHorizontalStrut(10));
		JComponent styledTextArea =
			SwingUtils.buildScrollingStyledTextArea(msg);
		styledTextArea.setPreferredSize(new Dimension(500, 300));
		box.add(styledTextArea);
		add(box);
	}

	/**	Constructs a new styled text warning message alert.
	 *
	 *	@param	msg		The warning message.
	 */

	public WarningMessage (StyledString msg) {
		this(msg, warningIcon);
	}

	/**	Handles button action events. Gets the index of the button
	 *	selected and disposes the alert.
	 */

	private ActionListener listener =
		new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				JButton button = (JButton)event.getSource();
				for (int i = 0; i < buttons.size(); i++) {
					if (button == (JButton)buttons.get(i)) {
						hit = i;
						break;
					}
				};
				dispose();
			}
		};

	/**	Adds a button to the alert.
	 *
	 *	@param	label			The button label.
	 */

	public void addButton (String label) {
		JButton button = addButton(label, listener);
		buttons.add(button);
	}

	/**	Adds a hidden button to the alert.
	 *
	 *	@param	label			The button label.
	 */

	public void addHiddenButton (String label) {
		JButton button = addButton(label, listener);
		buttons.add(button);
		button.setVisible(false);
	}

	/**	Adds the default button to the alert.
	 *
	 *	@param	label			The button label.
	 */

	public void addDefaultButton (String label) {
		JButton button = addDefaultButton(label, listener);
		buttons.add(button);
	}

	/**	Shows the alert.
	 *
	 *	@param	parent		The parent window, or null if none. The dialog
	 *						is positioned centered over its parent window
	 *						horizontally and positioned with its top
	 *						25 pixels below the top of its parent.
	 *
	 *	@return			The ordinal of the button selected by the user
	 *					(0, 1, 2, ...).
	 */

	public int doit (Window parent) {
		show(parent);
		return hit;
	}

	/**	Shows the alert with no parent window.
	 *
	 *	@return			The ordinal of the button selected by the user
	 *					(0, 1, 2, ...).
	 */

	public int doit () {
		return doit(null);
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

