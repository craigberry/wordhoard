package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

import java.io.*;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;

/**	A text location.
 *
 *	<p>A location is a line index plus a character offset within the line.
 *
 *	<p>The line index is -1 if the location is above the first line,
 *	or Integer.MAX_VALUE if the location is below the last line.
 *
 *	<p>The character offset is 0 if the location is above the first line or
 *	below the last line.
 *
 *	<p>The character offset is -1 if the location is to the left of a
 *	line.
 *
 *	<p>The character offset is Integer.MAX_VALUE if the the location is
 *	to the right of a line.
 *
 *	<p>For a line of length n, offset 0 is the location preceding the
 *	first character of the line (the beginning of the line). Offset n is the
 *	location following the last character of the line (the end of the line).
 */
 
@Embeddable
public class TextLocation implements Comparable, Serializable {

	/**	The line index. */
	 
	private int index;
	
	/**	The character offset in the line. */
	 
	private int offset;
	
	/**	Creates a new empty location.
	 */
	 
	public TextLocation () {
	}
	
	/**	Creates a new location.
	 *
	 *	@param	index		Line index.
	 *
	 *	@param	offset		Character offset in line.
	 */
	 
	public TextLocation (int index, int offset) {
		this.index = index;
		this.offset = offset;
	}
	
	/**	Gets the line index.
	 *
	 *	@return		The line index.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	@Access(AccessType.FIELD)
	public int getIndex () {
		return index;
	}
	
	/**	Sets the line index.
	 *
	 *	@param	index		Line index.
	 */
	 
	public void setIndex (int index) {
		this.index = index;
	}
	
	/**	Gets the character offset in the line.
	 *
	 *	@return		The character offset in the line.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	@Access(AccessType.FIELD)
	public int getOffset () {
		return offset;
	}
	
	/**	Sets the character offset in the line.
	 *
	 *	@param	offset		The character offset in the line.
	 */
	 
	public void setOffset (int offset) {
		this.offset = offset;
	}
	
	/**	Returns true if the location is within the text.
	 *
	 *	<p>The location is "within the text" if neither the line index nor
	 *	the character offset is -1 or Integer.MAX_VALUE.
	 *
	 *	@return 	True if location is within the text.
	 */
	 
	@Transient
	public boolean isInText () {
		return index != -1 && index != Integer.MAX_VALUE &&
			offset != -1 && offset != Integer.MAX_VALUE;
	}
		
	/**	Compares this location with another location.
	 *
	 *	@param	o		The other location.
	 *
	 *	@return			A negative integer, zero, or a positive
	 *					integer as this location is less than, equal to,
	 *					or greater than the other location.
	 */
	 
	public int compareTo (Object o) {
		TextLocation other = (TextLocation)o;
		if (index < other.index) return -1;
		if (index > other.index) return +1;
		return offset - other.offset;
	}
	
	/**	Returns true if this location is equal to another location.
	 *
	 *	@param	o		The other location.
	 *
	 *	@return			True if this location equals the other location.
	 */
	 
	public boolean equals (Object o) {
		if (!(o instanceof TextLocation)) return false;
		return compareTo(o) == 0;
	}
	
	/**	Returns a string representation of the location.
	 *
	 *	@return		The string representation.
	 */
	 
	public String toString () {
		return "(" + index + "," + offset + ")";
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

