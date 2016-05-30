package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;

/**	XML fixer abstract base class.
 */
 
public abstract class Fixer {

	/**	True to log messages. */
	
	private boolean logMessages = true;

	/**	Fixes an XML DOM tree.
	 *
	 *	@param	corpusTag	Corpus tag.
	 *
	 *	@param	workTag		Work tag.
	 *
	 *	@param	document	XML DOM tree.
	 *
	 *	@throws Exception
	 */

	public abstract void fix (String corpusTag, String workTag, 
		Document document)
			throws Exception;
		
	/**	Logs a fixer message.
	 *
	 *	@param	name		Fixer name.
	 *
	 *	@param	msg			Message.
	 */
	 
	void log (String name, String msg) {
		if (logMessages) System.out.println("         " + name + ": " + msg);
	}
	
	/**	Enable or disable log messages.
	 *
	 *	@param	enabled		True to enable log messages (the default),
	 *						false to disable them.
	 */
	 
	public void enableLogMessages (boolean enabled) {
		logMessages = enabled;
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

