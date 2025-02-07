package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.swing.calculator.*;

/**	Login/logout utilities to support scripts.
 *
 *	<p>
 *	Implements login and logout methods for use in scripts.
 *	Uses the Calculator window facilities to do the actual work.
 *	</p>
 */

public class LoginUtils
{
	/**	Login to WordHoard.
	 */

	public static void login()
	{
		WordHoardCalculatorWindow.getCalculatorWindow().handleLoginCmd();
	}

	/**	Logout from WordHoard.
	 */

	public static void logout()
	{
		WordHoardCalculatorWindow.getCalculatorWindow().handleLogoutCmd();
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected LoginUtils()
	{
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


