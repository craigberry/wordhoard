package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.*;

/**	A modal dialog.
 *
 *	<p>This class makes it a bit easier to construct typical Swing
 *	modal dialogs which contain one or more components arranged vertically
 *	and left-aligned, then one or more buttons arranged horizontally
 *	and right-aligned with the other components, all separated by an
 *	appropriate and consistent amount of white space.
 */

public class ModalDialog extends JDialog
{
	/**	Dialog panel. */

	private DialogPanel panel = new DialogPanel();

	/**	The initial focus component, or null if none defined. */

	private JComponent initialFocus;

	/**	True to ensure default button displays in platform specific location.
	 *
	 *	<p>
	 *	Static so it can be set once for all modal dialogs.
	 *	</p>
	 */

	protected static boolean usePlatformPositionForDefaultButton	= true;

	/**	Constructs a new modal dialog.
	 *
	 *	@param	title			Dialog window title.
	 *	@param	parentWindow	Parent window.
	 */

	public ModalDialog (String title, Frame parentWindow) {
		super(getParentWindow(parentWindow));
		common(title);
	}

	/**	Constructs a new modal dialog.
	 *
	 *	@param	title		Dialog window title.
	 */

	public ModalDialog (String title) {
		super(getParentWindow(null));
		common(title);
	}

	/**	Common dialog construction code.
	 *
	 *	@param	title	Dialog title.
	 */

	private void common (String title) {
		setTitle(title);
		setModal(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setContentPane(panel);
		addWindowListener(
			new WindowAdapter() {
				public void windowActivated (WindowEvent event) {
					if (getFocusOwner() == null) {
						if (initialFocus == null) {
							getRootPane().requestFocus();
						} else {
							initialFocus.requestFocus();
						}
					}
				}
			}
		);
	}
	
	/**	Gets a parent window if none supplied.
	 *
	 *	<p>This method is used to work around a bug Apple introduced in Java version 1.5.0_19:
	 *	When a modal dialog with no defined parent ("owner") window is disposed in a program
	 *	with the menu bar at the top of the screen, the menu bar is truncated instead of being
	 *	properly restored. The workaround is to use the active window as the parent if no parent
	 *	is supplied by the caller.
	 *
	 *	<p>This workaround only works if the program's windows are being managed by the
	 *	<code>WindowsMenuManager</code> class.
	 *
	 *	@param	parentWindow	Parent window supplied by caller, or null if none.
	 *
	 *	@return					Parent window supplied by caller, or if null, the active window.
	 */
	 
	private static Frame getParentWindow (Frame parentWindow) {
		if (parentWindow != null) return parentWindow;
		return WindowsMenuManager.getActiveWindow();
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
		panel.add(component, spacing);
	}

	/**	Adds a vertical spacing component.
	 *
	 *	@param	spacing			The number of vertical spacing pixels.
	 */

	public void add (int spacing) {
		panel.add(spacing);
	}

	/**	Adds a body component with zero spacing.
	 *
	 *	@param	component		The component.
	 */

	public void add (JComponent component) {
		panel.add(component);
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
		panel.add(new JLabel(str), spacing);
	}

	/**	Adds a string component with zero spacing.
	 *
	 *	@param	str				The string. This is turned into a JLabel
	 *							component.
	 */

	public void add (String str) {
		panel.add(str);
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
		return panel.addButton(label, actionListener);
	}

	/**	Adds the default button.
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
		JButton button = panel.addButton(label, actionListener);
		getRootPane().setDefaultButton(button);
		return button;
	}

	/**	Sets the intial focus component.
	 *
	 *	@param	component		The component which should get the
	 *							initial keyboard focus.
	 */

	public void setInitialFocus (JComponent component) {
		initialFocus = component;
	}

	/**	Packs and shows the dialog.
	 *
	 *	@param	parent		The parent window, or null if none. The dialog
	 *						is positioned centered over its parent window
	 *						horizontally and positioned with its top
	 *						25 pixels below the top of its parent.
	 */

	public void show (Window parent) {
		fixDefaultButtonPosition();
		pack();
		WindowPositioning.centerWindowOverWindow(this, parent, 25);
		super.setVisible(true);
	}

	/**	Shows or hides the dialog.
	 *
	 *	@param	show 	true to show dialog with no parent window,
	 *					false to hide dialog.
	 */

	public void setVisible (boolean show) {
		if ( show ) {
			show(null);
		} else {
			super.setVisible(show);
		}
	}

	/**	Shows the dialog without packing it.
	 *
	 *	<p>
	 *	The dialog should already have been previously packed.
	 *	</p>
	 */

	public void showUnpacked () {
		fixDefaultButtonPosition();
		super.setVisible(true);
	}

	/**	Shows the dialog without packing it.
	 *
	 *	@param	parent		The parent window, or null if none. The dialog
	 *						is positioned centered over its parent window
	 *						horizontally and positioned with its top
	 *						25 pixels below the top of its parent.
	 *
	 *	<p>
	 *	The dialog should already have been previously packed.
	 *	</p>
	 */

	public void showUnpacked( Window parent )
	{
		fixDefaultButtonPosition();
		WindowPositioning.centerWindowOverWindow( this , parent , 25 );
		super.setVisible(true);
	}

	/**	Paint the dialog immediately.
	 */

	public void paintImmediately()
	{
		panel.paintImmediately( panel.getVisibleRect() );
	}

	/**	Get flag for repositioning default button to platform location.
	 *
	 *	@return		True to use platform position for default button.
	 */

	public boolean getUsePlatformPositionForDefaultButton()
	{
		return usePlatformPositionForDefaultButton;
	}

	/**	Set flag for repositioning default button to platform location.
	 *
	 *	@param	usePlatformPositionForDefaultButton		True to use
	 *													platform position
	 *													for default button.
	 *	<p>
	 *	For Windows, the default button (if any) is moved to the far right.
	 *	For Mac OS X and other systems, the button positions are left unchanged.
	 *	</p>
	 */

	protected void setUsePlatformPositionForDefaultButton
	(
		boolean usePlatformPositionForDefaultButton
	)
	{
		this.usePlatformPositionForDefaultButton	=
			usePlatformPositionForDefaultButton;
	}

	/**	Move the default button to the system-defined position.
	 *
	 *	<p>
	 *	For Windows, the default button (if any) is moved to the far right.
	 *	For Mac OSX, the default button is moved to the far left.
	 *	For other systems, the button positions are left unchanged.
	 *	</p>
	 */

	protected void fixDefaultButtonPosition()
	{
								//	Return if we're not checking for
								//	default button position.

		if ( !usePlatformPositionForDefaultButton ) return;

								//	Return if we're not on Windows OS.

		if ( !Env.WINDOWSOS ) return;

								//	Is there more than one button?
								//	Return if not.

		JPanel buttonPanel		= panel.getButtons();

		Component[] children	= buttonPanel.getComponents();

		int nChildren			= children.length;

		if ( nChildren < 2 ) return;

								//	Is there a default button?  Return if not.

		JButton defaultButton	= getRootPane().getDefaultButton();

		if ( defaultButton == null ) return;

								//	Depending upon platform, find the
								//	component index where the default button
								//	should appear.

		int defaultIndex	= -1;

		for ( int i = nChildren - 1 ; i > 0 ; i-- )
		{
			Component child	= children[ i ];

			if ( child instanceof JButton )
			{
				defaultIndex	= i;
				break;
			}
		}

								//	Quit if we couldn't find the
								//	proper position for the default button.

		if ( defaultIndex < 0 ) return;

								//	Find the default button in the panel
								//	and move it to the proper position.

		boolean swapDone	= false;

		for ( int i = 0 ; i < nChildren ; i++ )
		{
			Component child	= children[ i ];

			if ( child.equals( defaultButton ) )
			{
				Component c					= children[ defaultIndex ];
				children[ defaultIndex ]	= children[ i ];
				children[ i ]				= c;

				swapDone					= true;

				break;
			}
		}

		if ( swapDone )
		{
			buttonPanel.removeAll();

			for ( int i = 0 ; i < nChildren ; i++ )
			{
				buttonPanel.add( children[ i ] );
			}
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

