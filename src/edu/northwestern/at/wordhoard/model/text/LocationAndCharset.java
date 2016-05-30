package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

/**	A text location and character set.
 */
 
public class LocationAndCharset {

	/**	The location. */
	
	private TextLocation location;
	
	/**	The character set. */
	
	private byte charset;
	
	/**	Creates a new location and character set.
	 *
	 *	@param	index		Line index.
	 *
	 *	@param	offset		Character offset in line.
	 *
	 *	@param	charset		Character set.
	 */
	 
	public LocationAndCharset (int index, int offset, byte charset) {
		this.location = new TextLocation(index, offset);
		this.charset = charset;
	}
	
	/**	Creates a new Roman character set location.
	 *
	 *	@param	index		Line index.
	 *
	 *	@param	offset		Character offset in line.
	 */
	 
	public LocationAndCharset (int index, int offset) {
		this.location = new TextLocation(index, offset);
		this.charset = TextParams.ROMAN;;
	}
	
	/**	Gets the location.
	 *
	 *	@return		Location.
	 */
	 
	public TextLocation getLocation () {
		return location;
	}
	
	/**	Gets the character set.
	 *
	 *	@return		Characer set.
	 */
	 
	public byte getCharset () {
		return charset;
	}
	
	/**	Returns a string representation of the object.
	 *
	 *	@return		The string representation.
	 */
	 
	public String toString () {
		return location.toString() + "(" + charset + ")";
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

