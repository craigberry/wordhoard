package edu.northwestern.at.utils.sys;

/*	Please see the license information at the end of this file. */

import java.io.*;

/**	Stack trace utilities.
 */

public class StackTraceUtils
{
	/**	Gets a stack trace for an exception.
	 *
	 *	@param e	The exception
	 */

	public static String getStackTrace( Throwable e )
	{
		ByteArrayOutputStream traceByteArrayStream =
			new ByteArrayOutputStream();

		PrintStream tracePrintStream =
			new PrintStream( traceByteArrayStream );

		e.printStackTrace( tracePrintStream );

		tracePrintStream.flush();

		return traceByteArrayStream.toString();
	}

	/**	Gets a stack trace.
	 */

	public static String getStackTrace()
	{
		return getStackTrace( new Throwable() );
	}

	/** Disallow instantiation but allow overrides. */

	protected StackTraceUtils()
	{
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

