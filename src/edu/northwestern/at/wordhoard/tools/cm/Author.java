package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	An author.
 */

public class Author {

	/**	Name. */

	private String name;

	/**	Birth year. */

	private String birthYear;

	/**	Death year. */

	private String deathYear;

	/**	Earliest work year. */

	private String earliestWorkYear;

	/**	Latest work year. */

	private String latestWorkYear;

	/**	Creates a new author from a header value map.
	 *
	 *	@param	headerValueMap		Header value map. Maps the names of WordHoard author
	 *								items to their values.
	 */

	public Author (Map headerValueMap) {
		name = (String)headerValueMap.get("authorName");
		birthYear = (String)headerValueMap.get("authorBirthYear");
		deathYear = (String)headerValueMap.get("authorDeathYear");
		earliestWorkYear = (String)headerValueMap.get("authorEarliestWorkYear");
		latestWorkYear = (String)headerValueMap.get("authorLatestWorkYear");
	}

	/**	Creates a new author from an element.
	 *
	 *	@param	el			WordHoard author element.
	 */

	public Author (Element el) {
		name = getField(el, "name");
		birthYear = getField(el, "birthYear");
		deathYear = getField(el, "deathYear");
		earliestWorkYear = getField(el, "earliestWorkYear");
		latestWorkYear = getField(el, "latestWorkYear");
	}

	/**	Creates a new author with just a name.
	 *
	 *	@param	name		Author name.
	 */

	public Author (String name) {
		this.name = name;
	}

	/**	Gets an author field from a WordHoard author element.
	 *
	 *	@param	el			WordHoard author element.
	 *
	 *	@param	childName	Field name.
	 */

	private String getField (Element el, String childName) {
		Element child = DOMUtils.getChild(el, childName);
		if (child == null) return null;
		return DOMUtils.getText(child);
	}

	/**	Gets the author's name.
	 *
	 *	@return		The author's name.
	 */

	public String getName () {
		return name;
	}

	/**	Gets the author's birth year.
	 *
	 *	@return		The author's birth year, or null.
	 */

	public String getBirthYear () {
		return birthYear;
	}

	/**	Gets the author's death year.
	 *
	 *	@return		The author's death year, or null.
	 */

	public String getDeathYear () {
		return deathYear;
	}

	/**	Gets the author's earliest work year.
	 *
	 *	@return		The author's earlies work year, or null.
	 */

	public String getEarliestWorkYear () {
		return earliestWorkYear;
	}

	/**	Gets the author's latest work year.
	 *
	 *	@return		The author's latest work year.
	 */

	public String getLatestWorkYear () {
		return latestWorkYear;
	}

	/**	Writes the author to the WordHoard authors.xml file.
	 *
	 *	@param	out			XMLWriter for WordHoard authors.xml file.
	 */

	public void write (XMLWriter out) {
		out.startEl("author");
		out.writeTextEl("name", name);
		if (birthYear != null)
			out.writeTextEl("birthYear", birthYear);
		if (deathYear != null)
			out.writeTextEl("deathYear", deathYear);
		if (earliestWorkYear != null)
			out.writeTextEl("earliestWorkYear", earliestWorkYear);
		if (latestWorkYear != null)
			out.writeTextEl("latestWorkYear", latestWorkYear);
		out.endEl("author");
	}

	/**	Returns true if this author "contains" some other author.
	 *
	 *	@param	other		The other author.
	 *
	 *	@return				True if the two authors have the same name, and any
	 *						other non-null attributes defined by the other author
	 *						are the same as in this author.
	 */

	public boolean contains (Author other) {
		if (!StringUtils.equals(name, other.name)) return false;
		if (other.birthYear != null &&
			!StringUtils.equals(birthYear, other.birthYear)) return false;
		if (other.deathYear != null &&
			!StringUtils.equals(deathYear, other.deathYear)) return false;
		if (other.earliestWorkYear != null &&
			!StringUtils.equals(earliestWorkYear, other.earliestWorkYear)) return false;
		if (other.latestWorkYear != null &&
			!StringUtils.equals(latestWorkYear, other.latestWorkYear)) return false;
		return true;
	}

	/**	Returns true if this object equals some other object.
	 *
	 *	@param	o		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object o) {
		if (!(o instanceof Author)) return false;
		Author other = (Author)o;
		return
			StringUtils.equals(name, other.name) &&
			StringUtils.equals(birthYear, other.birthYear) &&
			StringUtils.equals(deathYear, other.deathYear) &&
			StringUtils.equals(earliestWorkYear, other.earliestWorkYear) &&
			StringUtils.equals(latestWorkYear, other.latestWorkYear);
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


