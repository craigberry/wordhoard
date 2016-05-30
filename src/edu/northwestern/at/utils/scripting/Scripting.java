package edu.northwestern.at.utils.scripting;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.text.*;
import bsh.*;

public class Scripting
{
	public static String runScript( String scriptName )
		throws TargetError, EvalError, FileNotFoundException, IOException
	{
		bsh.Interpreter interpreter = new bsh.Interpreter();

		interpreter.source( scriptName );

		return interpreter.get( "result" ).toString();
	}

	/** Don't allow instantiation but do allow overrides. */

	protected Scripting()
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

