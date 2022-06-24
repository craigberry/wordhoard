package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;

/**	An unexpected error message alert.
 *
 *	<p>This alert is used to report "unexpected" Java exceptions. It contains
 *	the stop icon, the title "Unexpected Error", an optional additional message
 *	supplied by the caller, and a full stack trace in a scrolling text area.
 *	The alert can be used to catch and report exceptions even in threads other
 *	than the AWT event dispatching thread.
 *
 *	<p>You may supply extra verbiage to be included in the report before the
 *	stack trace.
 */

public class UnexpectedErrorMessage {

	/**	Extra verbiage, if any. */

	private static String extraVerbiage;

	/**	Sets the extra verbiage.
	 *
	 *	@param	str		Extra verbiage.
	 */

	public static void setExtraVerbiage (String str) {
		extraVerbiage = str;
	}

	/**	Constructs and displays an unexpected error message alert.
	 *
	 *	@param	t				The throwable.
	 *
	 *	@param	msg				The error message, or null if none.
	 *
	 *	@param	parentWindow	The parent window, or null if none. The dialog
	 *							is positioned centered over its parent window
	 *							horizontally and positioned with its top
	 *							25 pixels below the top of its parent.
	 */

	public UnexpectedErrorMessage (final Throwable t, final String msg,
		final Frame parentWindow)
	{
		BuildAndShowAlert.buildAndShowAlert(t, "Unexpected Error", msg,
			extraVerbiage, parentWindow);
	}

	/**	Constructs and displays an unexpected error message alert
	 *	with no parent window.
	 *
	 *	@param	t			The throwable.
	 *
	 *	@param	msg			The error message.
	 */

	public UnexpectedErrorMessage (Throwable t, String msg) {
		this(t, msg, null);
	}

	/**	Constructs and displays an unexpected error message alert
	 *	with no parent window or message.
	 *
	 *	@param	t			The throwable.
	 */

	public UnexpectedErrorMessage (Throwable t) {
		this(t, null, null);
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


