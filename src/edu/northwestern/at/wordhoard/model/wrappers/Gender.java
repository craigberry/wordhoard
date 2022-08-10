package edu.northwestern.at.wordhoard.model.wrappers;

/*	Please see the license information at the end of this file. */

import org.hibernate.Session;
import org.hibernate.query.Query;

import edu.northwestern.at.wordhoard.model.grouping.GroupingObject;
import edu.northwestern.at.wordhoard.model.search.SearchCriterion;
import edu.northwestern.at.wordhoard.model.speakers.Speaker;
import edu.northwestern.at.wordhoard.model.text.FontInfo;
import edu.northwestern.at.wordhoard.model.text.TextLine;
import edu.northwestern.at.wordhoard.model.text.TextParams;

/**	A gender wrapper.
 */

public class Gender implements SearchCriterion, GroupingObject {

	/**	Male gender. */

	public static final byte MALE = 0;

	/**	Female gender. */

	public static final byte FEMALE = 1;

	/**	Uncertain, mixed, or unknown gender. */

	public static final byte UNCERTAIN_MIXED_OR_UNKNOWN = 2;

	/**	Number of genders. */

	public static final byte NUM_GENDERS = 3;

	/**	The gender. */

	private byte gender;

	/**	Creates a new gender wrapper.
	 */

	public Gender () {
	}

	/**	Creates a new gender wrapper.
	 *
	 *	@param	gender		The gender.
	 */

	public Gender (byte gender) {
		this.gender = gender;
	}

	/**	Gets the gender.
	 *
	 *	@return		The gender.
	 *
	 *	@hibernate.property access="field"
	 */

	public byte getGender () {
		return gender;
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass () {
		return Speaker.class;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause () {
		return "speaker.gender.gender = :gender";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setParameter("gender", gender);
	}

	/**	Appends a description to a text line.
	 *
	 *	@param	line			Text line.
	 *
	 *	@param	romanFontInfo	Roman font info.
	 *
	 *	@param	fontInfo		Array of font info indexed by character
	 *							set.
	 */

	public void appendDescription (TextLine line, FontInfo romanFontInfo,
		FontInfo[] fontInfo)
	{
		line.appendRun("speaker gender = " + toString(), romanFontInfo);
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "spoken by".
	 */

	public String getReportPhrase () {
		return "spoken by";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		String str = null;
		switch (gender) {
			case MALE: str = numHits == 1 ? "a male character" :
				"male characters";
				break;
			case FEMALE: str = numHits == 1 ? "a female character" :
				"female characters";
				break;
			case UNCERTAIN_MIXED_OR_UNKNOWN:
				str = numHits == 1 ?
					"a character with uncertain, mixed, or unknown gender" :
					"characters with uncertain, mixed, or unknown gender";
				break;
		}
		return new Spelling(str, TextParams.ROMAN);
	}

	/**	Returns a string representation of the gender.
	 *
	 *	@return		String representation.
	 */

	public String toString () {
		switch (gender) {
			case MALE: return "male";
			case FEMALE: return "female";
			case UNCERTAIN_MIXED_OR_UNKNOWN:
				return "uncertain, mixed or unknown";
		}
		return null;
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Gender)) return false;
		Gender other = (Gender)obj;
		return gender == other.gender;
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return gender;
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

