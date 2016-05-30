package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**	A dialog panel.
 *
 *	<p>This class makes it a bit easier to construct typical Swing
 *	dialogs which contain one or more components arranged vertically
 *	and left-aligned, then one or more buttons arranged horizontally
 *	and right-aligned with the other components, all separated and
 *	enclosed by an appropriate and consistent amount of white space.
 *	</p>
 */

public class DialogPanel extends JPanel {

	/**	The body panel of vertically arranged components with left-justified
	 *	alignment.
	 */

	protected JPanel body = new JPanel();

	/**	The button panel of horizontally arranged buttons. */

	protected JPanel buttons = new JPanel();

	/**	The number of buttons added to the dialog so far. */

	protected int numButtons = 0;

   	/**	Constructs a new dialog panel.
	 */

	public DialogPanel () {
		super();
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		buttons.add(Box.createHorizontalGlue());
		buttons.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		setLayout(new BorderLayout());
		super.add(body, BorderLayout.CENTER);
		super.add(buttons, BorderLayout.SOUTH);
		setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	}

	/**	Adds a vertical spacing component.
	 *
	 *	@param	spacing			The number of vertical spacing pixels.
	 */

	public void add (int spacing) {
		body.add(Box.createVerticalStrut(spacing));
	}

	/**	Adds a body component.
	 *
	 *	@param	component		The component.
	 *
	 *	@param	spacing			The number of vertical spacing pixels
	 *							between this component and the previous
	 *							component.
	 */

	public void add (JComponent component, int spacing) {
		if (spacing > 0)
			body.add(Box.createVerticalStrut(spacing));
		component.setAlignmentX(Component.LEFT_ALIGNMENT);
		body.add(component);
	}

	/**	Adds a body component with zero spacing.
	 *
	 *	@param	component		The component.
	 */

	public void add (JComponent component) {
		add(component, 0);
	}

	/**	Adds a string component.
	 *
	 *	@param	str				The string. This is turned into a JLabel
	 *							component.
	 *
	 *	@param	spacing			The number of vertical spacing pixels
	 *							between this component and the previous
	 *							component.
	 */

	public void add (String str, int spacing) {
		add(new JLabel(str), spacing);
	}

	/**	Adds a string component with zero spacing.
	 *
	 *	@param	str				The string. This is turned into a JLabel
	 *							component.
	 */

	public void add (String str) {
		add(str, 0);
	}

	/**	Adds a button.
	 *
	 *	@param	label			The button label.
	 *
	 *	@param	actionListener	The ActionListener for the button.
	 *
	 *	@return					The button.
	 */

	public JButton addButton (String label,
		ActionListener actionListener)
	{
		if (numButtons > 0) buttons.add(Box.createHorizontalStrut(10));
		JButton button = new JButton(label);
		buttons.add(button);
		button.addActionListener(actionListener);
		numButtons++;
		return button;
	}

	/**	Adds a default button.
	 *
	 *	@param	label			The button label.
	 *
	 *	@param	actionListener	The ActionListener for the button.
	 *
	 *	@return					The button.
	 */

	public JButton addDefaultButton (String label,
		ActionListener actionListener)
	{
		JButton button = addButton( label , actionListener );
		getRootPane().setDefaultButton( button );
		return button;
	}

	/**	Adds a small button.
	 *
	 *	@param	label			The button label.
	 *
	 *	@param	actionListener	The ActionListener for the button.
	 *
	 *	@return					The button.
	 */

	public JButton addSmallButton (String label,
		ActionListener actionListener)
	{
		if (numButtons > 0) buttons.add(Box.createHorizontalStrut(10));
		JButton button = new JButton(label);
		Font font = button.getFont();
		button.setFont(new Font(font.getName(), font.getStyle(),
			font.getSize()-2));
		buttons.add(button);
		button.addActionListener(actionListener);
		numButtons++;
		return button;
	}

	/**	Return the body panel.
	 *
	 *	@return		The body panel.
	 */

	public JPanel getBody()
	{
		return body;
	}

	/**	Return the button panel.
	 *
	 *	@return		The button panel.
	 */

	public JPanel getButtons()
	{
		return buttons;
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

