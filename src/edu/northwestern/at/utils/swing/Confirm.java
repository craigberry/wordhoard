package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**	A confirmation dialog.
 */

public class Confirm
{
	/**	The PLAF query icon. */

	private static JLabel queryIcon =
		new JLabel( UIManager.getLookAndFeel().getDefaults().getIcon(
			"OptionPane.questionIcon" ) );

	/**	Constructs and displays a confirmation alert.
	 *
	 *	@param	msg			The confirmation prompt.
	 *
	 *	@param	doitLabel	Label for the "Doit" button.
	 *
	 *	@param	parent		The parent window, or null if none. The dialog
	 *						is positioned centered over its parent window
	 *						horizontally and positioned with its top
	 *						25 pixels below the top of its parent.
	 *
	 *	@return				True if "Doit" button clicked, false if "Cancel".
	 */

	public static boolean confirm( String msg , String doitLabel , Window parent )
	{
		WarningMessage dlog = new WarningMessage( msg , queryIcon );

		dlog.addDefaultButton( "Cancel" );
		dlog.addButton( doitLabel );

		return ( dlog.doit( parent ) == 1 );
	}

	/**	Constructs and displays a confirmation dialog with no parent window.
	 *
	 *	@param	doitLabel	Label for the "Doit" button.
	 *
	 *	@param	msg			The confirmation prompt.
	 *
	 *	@return				True if "Doit" button clicked, false if "Cancel".
	 */

	public static boolean confirm( String msg, String doitLabel )
	{
		WarningMessage dlog = new WarningMessage( msg , queryIcon );

		dlog.addDefaultButton( "Cancel" );
		dlog.addButton( doitLabel );

		return ( dlog.doit() == 1 );
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

