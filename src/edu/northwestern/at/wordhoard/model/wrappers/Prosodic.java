package edu.northwestern.at.wordhoard.model.wrappers;

/*	Please see the license information at the end of this file. */

import org.hibernate.query.Query;
import org.hibernate.Session;

import edu.northwestern.at.wordhoard.model.grouping.GroupingObject;
import edu.northwestern.at.wordhoard.model.search.SearchCriterion;
import edu.northwestern.at.wordhoard.model.text.FontInfo;
import edu.northwestern.at.wordhoard.model.text.TextLine;
import edu.northwestern.at.wordhoard.model.text.TextParams;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**	A prosodic attribute wrapper.
 */

@Embeddable
public class Prosodic implements SearchCriterion, GroupingObject {

	/**	Prose. */

	public static final byte PROSE = 0;

	/**	Verse. */

	public static final byte VERSE = 1;

	/**	Prosodic attribute unknown. */

	public static final byte UNKNOWN = 2;

	/**	Number of prosodic attributes. */

	public static final byte NUM_PROSODIC = 3;

	/**	The prosodic attribute. */

	private byte prosodic;

	/**	Creates a new prosodic attribute wrapper.
	 */

	public Prosodic () {
	}

	/**	Creates a new prosodic attribute wrapper.
	 *
	 *	@param	prosodic		The prosodic attribute.
	 */

	public Prosodic (byte prosodic) {
		this.prosodic = prosodic;
	}

	/**	Gets the prosodic attribute.
	 *
	 *	@return		The prosodic attribute.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(name="prosodic_prosodic")
	public byte getProsodic () {
		return prosodic;
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass () {
		return null;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause () {
		return "word.prosodic.prosodic = :prosodic";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setParameter("prosodic", prosodic);
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
		line.appendRun("prose or verse = " + toString(), romanFontInfo);
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "in".
	 */

	public String getReportPhrase () {
		return "in";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		String str = null;
		switch (prosodic) {
			case PROSE:
				str = "prose";
				break;
			case VERSE:
				str = "verse";
				break;
			case UNKNOWN:
				str = "unknown";
				break;
		}
		return new Spelling(str, TextParams.ROMAN);
	}

	/**	Returns a string representation of the prosodic attribute.
	 *
	 *	@return		String representation.
	 */

	public String toString () {
		switch (prosodic) {
			case PROSE: return "prose";
			case VERSE: return "verse";
			case UNKNOWN: return "unknown";
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
		if (obj == null || !(obj instanceof Prosodic)) return false;
		Prosodic other = (Prosodic)obj;
		return prosodic == other.prosodic;
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return prosodic;
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

