package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.utils.swing.styledtext.*;

/**	A confirmation dialog with yes, no, cancel buttons, or just yes and no buttons.
 *
 *	<p>
 *	<strong>Example:</strong>
 *	</p>
 *
 *	<p>
 *	<code>
 *								// Check if we should save current file
 *								// before exiting program.
 *
 *		switch ( ConfirmYNC.confirmYNC( "Do you want to save the file?" ) )
 *		{
 *								// Cancel button pressed -- don't save,
 *								// don't exit.
 *
 *			case ConfirmYNC.CANCEL:
 *				break;
 *
 *								// Yes button pressed -- save then exit.
 *
 *			case ConfirmYNC.YES:
 *				saveTheFile();
 *				exit();
 *				break;
 *
 *								// No button pressed -- just exit.
 *			case ConfirmYNC.NO:
 *				exit();
 *				break;
 *		};
 *	</code>
 *	</p>
 */

public class ConfirmYNC
{
	/**	The PLAF query icon. */

	private static JLabel queryIcon =
		new JLabel( UIManager.getLookAndFeel().getDefaults().getIcon(
			"OptionPane.questionIcon" ) );

	/** Button indices for checking return values. */

	public static final int CANCEL	= 0;
	public static final int YES		= 1;
	public static final int NO		= 2;

	/**	Constructs and displays a confirmation alert.
	 *
	 *	@param	msg			The confirmation prompt.
	 *
	 *	@param	parent		The parent window, or null if none. The dialog
	 *						is positioned centered over its parent window
	 *						horizontally and positioned with its top
	 *						25 pixels below the top of its parent.
	 *	@return	The confirmation value.
	 */

	public static int confirmYNC( String msg , Window parent )
	{
		WarningMessage dlog = new WarningMessage( msg , queryIcon );

		dlog.addDefaultButton( "Cancel" );
		dlog.addButton( "Yes" );
		dlog.addButton( "No" );

		return dlog.doit( parent );
	}

	/**	Constructs and displays a confirmation dialog with no parent window.
	 *
	 *	@param	msg			The confirmation prompt.
	 *	@return	The confirmation value.
	 */

	public static int confirmYNC( String msg )
	{
		WarningMessage dlog = new WarningMessage( msg , queryIcon );

		dlog.addDefaultButton( "Cancel" );
		dlog.addButton( "Yes" );
		dlog.addButton( "No" );

		return dlog.doit();
	}

	/**	Constructs and displays a confirmation alert.
	 *
	 *	@param	msg			The confirmation prompt.
	 *
	 *	@param	parent		The parent window, or null if none. The dialog
	 *						is positioned centered over its parent window
	 *						horizontally and positioned with its top
	 *						25 pixels below the top of its parent.
	 *	@return	The confirmation value.
	 */

	public static int confirmYN( String msg , Window parent )
	{
		WarningMessage dlog = new WarningMessage( msg , queryIcon );


		dlog.addHiddenButton( "Cancel" );
		dlog.addButton( "Yes" );
		dlog.addDefaultButton( "No" );

		return dlog.doit( parent );
	}

	/**	Constructs and displays a confirmation dialog with no parent window.
	 *
	 *	@param	msg			The confirmation prompt.
	 *	@return	The confirmation value.
	 */

	public static int confirmYN( String msg )
	{
		WarningMessage dlog = new WarningMessage( msg , queryIcon );

		dlog.addHiddenButton( "Cancel" );
		dlog.addButton( "Yes" );
		dlog.addDefaultButton( "No" );

		return dlog.doit();
	}

	/**	Constructs and displays a styled text confirmation alert.
	 *
	 *	@param	msg			The confirmation prompt.
	 *
	 *	@param	parent		The parent window, or null if none. The dialog
	 *						is positioned centered over its parent window
	 *						horizontally and positioned with its top
	 *						25 pixels below the top of its parent.
	 *	@return	The confirmation value.
	 */

	public static int confirmYNC( StyledString msg , Window parent )
	{
		WarningMessage dlog = new WarningMessage( msg , queryIcon );

		dlog.addDefaultButton( "Cancel" );
		dlog.addButton( "Yes" );
		dlog.addButton( "No" );

		return dlog.doit( parent );
	}

	/**	Constructs and displays a styled text confirmation dialog with no parent window.
	 *
	 *	@param	msg			The confirmation prompt.
	 *	@return	The confirmation value.
	 */

	public static int confirmYNC( StyledString msg )
	{
		WarningMessage dlog = new WarningMessage( msg , queryIcon );

		dlog.addDefaultButton( "Cancel" );
		dlog.addButton( "Yes" );
		dlog.addButton( "No" );

		return dlog.doit();
	}

	/**	Constructs and displays a styled text confirmation alert.
	 *
	 *	@param	msg			The confirmation prompt.
	 *
	 *	@param	parent		The parent window, or null if none. The dialog
	 *						is positioned centered over its parent window
	 *						horizontally and positioned with its top
	 *						25 pixels below the top of its parent.
	 *	@return	The confirmation value.
	 */

	public static int confirmYN( StyledString msg , Window parent )
	{
		WarningMessage dlog = new WarningMessage( msg , queryIcon );


		dlog.addHiddenButton( "Cancel" );
		dlog.addButton( "Yes" );
		dlog.addDefaultButton( "No" );

		return dlog.doit( parent );
	}

	/**	Constructs and displays a styled text confirmation dialog with no parent window.
	 *
	 *	@param	msg			The confirmation prompt.
	 *	@return	The confirmation value.
	 */

	public static int confirmYN( StyledString msg )
	{
		WarningMessage dlog = new WarningMessage( msg , queryIcon );

		dlog.addHiddenButton( "Cancel" );
		dlog.addButton( "Yes" );
		dlog.addDefaultButton( "No" );

		return dlog.doit();
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

