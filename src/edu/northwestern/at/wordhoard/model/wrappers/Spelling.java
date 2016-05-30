package edu.northwestern.at.wordhoard.model.wrappers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.text.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.grouping.*;

/**	A spelling.
 *
 *	<p>A spelling is a unicode string of text plus the character
 *	set used by the string.
 */

public class Spelling implements GroupingObject, Serializable, Comparable {

	/**	The string. */

	private String string;

	/**	The character set. */

	private byte charset;

	/**	Creates a new spelling object.
	 */

	public Spelling () {
	}

	/**	Creates a new spelling object.
	 *
	 *	@param	string		String.
	 *
	 *	@param	charset		Character set.
	 */

	public Spelling (String string, byte charset) {
		this.string = string;
		this.charset = charset;
	}

	/**	Creates a new spelling object.
	 *
	 *	@param	string		String.
	 *
	 *	@param	charset		Character set.
	 */

	public Spelling (String string, int charset) {
		this.string = string;
		this.charset = (byte)charset;
	}

	/**	Gets the string.
	 *
	 *	@return		The string.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getString () {
		return string;
	}

	/**	Gets the character set.
	 *
	 *	@return		The character set.
	 *
	 *	@hibernate.property access="field"
	 */

	public byte getCharset () {
		return charset;
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "with spelling".
	 */

	public String getReportPhrase () {
		return "with spelling";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		return this;
	}

	/**	Gets case and diacritical insensitive version of this spelling.
	 *
	 *	@return		The case and diacritical insensitive spelling.
	 */

	public Spelling toInsensitive () {
		return new Spelling(CharsetUtils.translateToInsensitive(string),
			charset );
	}

	/**	Returns a string representation of the object.
	 *
	 *	@return		The string.
	 */

	public String toString () {
		return string;
	}

	/**	Returns true if this object is equal to some other object.
	 *
	 *	<p>The spellings are equal if their strings are identical and
	 *	they have the same character set.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Spelling)) return false;
		Spelling other = (Spelling)obj;
		return string.equals(other.string) &&
			charset == other.charset;
	}

	/**	Returns true if this object is equal to some other object
	 *	in a case insensitive fashion.
	 *
	 *	<p>The spellings are equal if their strings are identical except
	 *	ignoring case and they have the same character set.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object,
	 *					except for case.
	 */

	public boolean equalsIgnoreCase (Object obj) {
		if (obj == null || !(obj instanceof Spelling)) return false;
		Spelling other = (Spelling)obj;
		return string.equalsIgnoreCase( other.string );
	}

	/**	Returns true if this object is equal to some other object
	 *	in a case and diacritical insensitive fashion.
	 *
	 *	<p>The spellings are equal if their strings are identical except
	 *	ignoring case and diacritics and if they have the same character
	 *	set.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object,
	 *					except for case and diacritics.
	 */

	public boolean equalsInsensitive (Object obj) {
		if (obj == null || !(obj instanceof Spelling)) return false;
		Spelling other = (Spelling)obj;
		return CharsetUtils.translateToInsensitive( string ).equals(
			CharsetUtils.translateToInsensitive( other.string ) ) &&
				charset == other.charset;
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return string.hashCode() | charset;
	}

	/**	Implement Comparable interface.
	 *
	 *	@param	obj		Other spelling object to which to compare this object.
	 */

	public int compareTo (Object obj) {
		if (obj == null || !(obj instanceof Spelling)) return Integer.MIN_VALUE;
		int result = Compare.compare(string, ((Spelling)obj).getString());
		if (result == 0)
			result = Compare.compare(charset, ((Spelling)obj).getCharset());
		return result;
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

