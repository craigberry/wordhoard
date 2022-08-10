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

/**	A mortality wrapper.
 */

public class Mortality implements SearchCriterion, GroupingObject {

	/**	Mortal. */

	public static final byte MORTAL = 0;

	/**	Immortal or supernatural. */

	public static final byte IMMORTAL_OR_SUPERNATURAL = 1;

	/**	"Other" mortality. */

	public static final byte UNKNOWN_OR_OTHER = 2;

	/**	Number of mortality attributes. */

	public static final byte NUM_MORTALITY = 3;

	/**	The mortality. */

	private byte mortality;

	/**	Creates a new mortality wrapper.
	 */

	public Mortality () {
	}

	/**	Creates a new mortality wrapper.
	 *
	 *	@param	mortality		The mortality.
	 */

	public Mortality (byte mortality) {
		this.mortality = mortality;
	}

	/**	Gets the mortality.
	 *
	 *	@return		The mortality.
	 *
	 *	@hibernate.property access="field"
	 */

	public byte getMortality () {
		return mortality;
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
		return "speaker.mortality.mortality = :mortality";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setParameter("mortality", mortality);
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
		line.appendRun("speaker mortality = " + toString(), romanFontInfo);
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
		switch (mortality) {
			case MORTAL: str = numHits == 1 ? "a mortal character" :
				"mortal characters";
				break;
			case IMMORTAL_OR_SUPERNATURAL:
				str = numHits == 1 ?
					"an immortal or supernatural character" :
					"immortal or supernatural characters";
				break;
			case UNKNOWN_OR_OTHER:
				str = numHits == 1 ?
					"a character with unknown or other mortality" :
					"characters with unknown or other mortality";
				break;
		}
		return new Spelling(str, TextParams.ROMAN);
	}

	/**	Returns a string representation of the mortality.
	 *
	 *	@return		String representation.
	 */

	public String toString () {
		switch (mortality) {
			case MORTAL: return "mortal";
			case IMMORTAL_OR_SUPERNATURAL:
				return "immortal or supernatural";
			case UNKNOWN_OR_OTHER:
				return "unknown or other";
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
		if (obj == null || !(obj instanceof Mortality)) return false;
		Mortality other = (Mortality)obj;
		return mortality == other.mortality;
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return mortality;
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

