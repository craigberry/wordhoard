package edu.northwestern.at.wordhoard.model.wrappers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;

/**	An interface for a text wrapper.
 *
 *	<p>Classes implementing this interface wrap a {@link
 *	edu.northwestern.at.wordhoard.model.text.Text Text} object.
 *
 */
 
public interface TextWrapped {

	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 */
	 
//	public Long getId ();
	
	/**	Gets the text.
	 *
	 *	@return		The text.
	 *
	 */
	 
	public Text getText ();
		
	/**	Sets the text.
	 *
	 *	@param	text	The text.
	 */
	 
	public void setText (Text text);
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two work parts are equal if their ids are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) ;
		
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode ();

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

