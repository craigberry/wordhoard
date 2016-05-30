package edu.northwestern.at.wordhoard.swing;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.swing.*;
import java.rmi.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.sys.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.server.*;

/**	Error handling.
 */

public class Err {

	/**	Handles an error.
	 *
	 *	<p>An unexpected error message is issued and the error is logged
	 *	on the server, with stack traces.
	 *
	 *	@param	t		Throwable.
	 */

	public static void err (Throwable t) {
		t.printStackTrace();
		new UnexpectedErrorMessage(t);
		log(t);
	}

	/**	Logs the error on the server.
	 *
	 *	@param	t		Throwable.
	 */

	private static void log (Throwable t) {
		WordHoardSession session = WordHoard.getSession();
		if (session == null) return;
		String stackTrace = StackTraceUtils.getStackTrace(t);
		StringBuffer buf = new StringBuffer();
		buf.append("Unexpected error in client\n");
		buf.append("WordHoard version = " +
			WordHoardSettings.getProgramVersion() + "\n");
		buf.append("OS name = " + System.getProperty("os.name") + "\n");
		buf.append("OS version = " + System.getProperty("os.version") + "\n");
		buf.append("Java version = " + System.getProperty("java.version") + "\n\n");
		buf.append(stackTrace);
		try {
			session.logMessage(Logger.ERROR, buf.toString());
		} catch (Exception e) {
			// ignore
		}
	}

	/**	Hides the default no-arg constructor.
	 */

	private Err () {
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

