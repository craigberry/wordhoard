package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

import java.io.*;

/**	A text range.
 */
 
public class TextRange implements Serializable {

	/**	The start of the range. */
	
	private TextLocation start;
	
	/**	The end of the range. */
	
	private TextLocation end;
	
	/**	Creates a new empty range.
	 */
	 
	public TextRange () {
	}
	
	/**	Creates a new text range.
	 *
	 *	@param	start	Start of range.
	 *
	 *	@param	end		End of range.
	 */
	
	public TextRange (TextLocation start, TextLocation end) {
		this.start = start;
		this.end = end;
	}
	
	/**	Creates a new text range containing a single location.
	 *
	 *	@param	loc		Start and end location.
	 */
	 
	public TextRange (TextLocation loc) {
		this.start = loc;
		this.end = loc;
	}
	
	/**	Gets the start of the range.
	 *
	 *	@return		Start of range.
	 *
	 *	@hibernate.component prefix="start_"
	 */
	 
	public TextLocation getStart () {
		return start;
	}
	
	/**	Sets the start of the range.
	 *
	 *	@param	start	Start of range.
	 */
	 
	public void setStart (TextLocation start) {
		this.start = start;
	}
	
	/**	Gets the end of the range.
	 *
	 *	@return		End of range.
	 *
	 *	@hibernate.component prefix="end_"
	 */
	 
	public TextLocation getEnd () {
		return end;
	}
	
	/**	Sets the end of the range.
	 *
	 *	@param	end		End of range.
	 */
	 
	public void setEnd (TextLocation end) {
		this.end = end;
	}
	
	/**	Returns true if the range is empty.
	 *
	 *	<p>The range is empty if the start location is greater than or
	 *	equal to the end location.
	 *
	 *	@return		True if range is empty.
	 */
	 
	public boolean isEmpty () {
		return start.compareTo(end) >= 0;
	}
	
	/**	Returns true if this range equals another range.
	 *
	 *	@param	o		The other range.
	 *
	 *	@return			True if the range equals the other range.
	 */
	 
	public boolean equals (Object o) {
		if (!(o instanceof TextRange)) return false;
		TextRange other = (TextRange)o;
		return start.equals(other.start) && end.equals(other.end);
	}
	
	/**	Returns true if this range contains a location.
	 *
	 *	@param	loc		Location.
	 *
	 *	@return			True if range contains location.
	 */
	 
	public boolean contains (TextLocation loc) {
		return start.compareTo(loc) <= 0 && loc.compareTo(end) <= 0;
	}
	
	/**	Returns a string representation of the range.
	 *
	 *	@return		The string representation.
	 */
	 
	public String toString () {
		return start.toString() + "-" + end.toString();
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

