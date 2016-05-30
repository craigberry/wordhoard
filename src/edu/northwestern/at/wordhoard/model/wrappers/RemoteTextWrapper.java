package edu.northwestern.at.wordhoard.model.wrappers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;

/**	A text wrapper from s remote source.
 *
 *	<p>This class wraps a {@link
 *	edu.northwestern.at.wordhoard.model.text.Text Text} object.
 *
 *	<p>This wraps a text from a remote source, such as an annotation system.
 *
 */
 
public class RemoteTextWrapper implements TextWrapped {

	/**	Unique persistence id. */
	
	private String id;
	
	/**	The text. */
	
	private Text text;
	
	/**	Creates a new text wrapper.
	 */
	 
	public RemoteTextWrapper () {
	}
	
	/**	Creates a new text wrapper.
	 *
	 *	@param	text		Text.
	 */
	 
	public RemoteTextWrapper (Text text) {
		this.text = text;
	}
		

	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 */
	 
	public String getId () {
		return id;
	}

	/**	Gets the text.
	 *
	 *	@return		The text.
	 *
	 */
	 
	public Text getText () {
		return text;
	}
	
	/**	Sets the text.
	 *
	 *	@param	text	The text.
	 */
	 
	public void setText (Text text) {
		this.text = text;
	}
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two work parts are equal if their ids are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof RemoteTextWrapper)) return false;
		RemoteTextWrapper other = (RemoteTextWrapper)obj;
		return id.equals(other.getId());
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return id.hashCode();
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

