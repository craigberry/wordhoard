package edu.northwestern.at.wordhoard.model.wrappers;

/*	Please see the license information at the end of this file. */

import org.hibernate.Session;
import org.hibernate.query.Query;

import edu.northwestern.at.wordhoard.model.grouping.GroupingObject;
import edu.northwestern.at.wordhoard.model.morphology.WordPart;
import edu.northwestern.at.wordhoard.model.search.SearchCriterion;
import edu.northwestern.at.wordhoard.model.text.FontInfo;
import edu.northwestern.at.wordhoard.model.text.TextLine;
import edu.northwestern.at.wordhoard.model.text.TextParams;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**	A major word class wrapper.
 */

@Embeddable
public class MajorWordClass implements SearchCriterion, GroupingObject {

	/**	The major word class. */

	private String majorWordClass;

	/**	Creates a new major word class wrapper.
	 */

	public MajorWordClass () {
	}

	/**	Creates a new major word class wrapper.
	 *
	 *	@param	majorWordClass		The major word class.
	 */

	public MajorWordClass (String majorWordClass) {
		this.majorWordClass = majorWordClass;
	}

	/**	Gets the major word class.
	 *
	 *	@return		The major word class.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
    @Column(name="majorWordClass_majorWordClass")
	public String getMajorWordClass () {
		return majorWordClass;
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass () {
		return WordPart.class;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause () {
//		return "wordPart.lemPos.lemma.wordClass.majorWordClass.majorWordClass " +
		return "lemma.wordClass.majorWordClass.majorWordClass " +
			"= :majorWordClass";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setParameter("majorWordClass", majorWordClass);
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
		line.appendRun("major word class = " + majorWordClass, romanFontInfo);
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "with major word class".
	 */

	public String getReportPhrase () {
		return "with major word class";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		return new Spelling(majorWordClass, TextParams.ROMAN);
	}

	/**	Returns a string representation of the major word class.
	 *
	 *	@return		The major word class.
	 */

	public String toString () {
		return majorWordClass;
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof MajorWordClass)) return false;
		MajorWordClass other = (MajorWordClass)obj;
		return majorWordClass.equals(other.majorWordClass);
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return majorWordClass.hashCode();
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

