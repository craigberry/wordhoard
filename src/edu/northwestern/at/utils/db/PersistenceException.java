package edu.northwestern.at.utils.db;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

/**	A  persistence exception.
 */

public class PersistenceException extends Exception {

	/**	Creates a new persistence exception.
	 *
	 *	@param	cause	Cause.
	 */

	public PersistenceException (Throwable cause) {
		super("Persistence Error", unwind(cause));
	}

	/**	Unwinds nested hibernate persistence exception causes.
	 *
	 *	@param	cause	Cause.
	 *
	 *	@return		Unwound cause.
	 */

	private static Throwable unwind (Throwable cause) {
		while (cause instanceof PersistenceException)
			cause = cause.getCause();
		return cause;
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

