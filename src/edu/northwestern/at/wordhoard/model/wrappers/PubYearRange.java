package edu.northwestern.at.wordhoard.model.wrappers;

/*	Please see the license information at the end of this file. */

import org.hibernate.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.utils.*;

/**	A publication year range wrapper.
 */

public class PubYearRange implements SearchCriterion, GroupingObject {

	/**	The start year, or null if none. */

	private Integer startYear;

	/**	The end year, or null if none. */

	private Integer endYear;

	/**	Creates a new publication year range wrapper.
	 */

	public PubYearRange () {
	}

	/**	Creates a new publication year range wrapper.
	 *
	 *	@param	startYear		The start year, or null if none.
	 *
	 *	@param	endYear			The end year, or null if none.
	 */

	public PubYearRange (Integer startYear, Integer endYear) {
		if (startYear == null) {
			startYear = endYear;
		} else if (endYear == null) {
			endYear = startYear;
		}
		this.startYear = startYear;
		this.endYear = endYear;
	}

	/**	Gets the start year.
	 *
	 *	@return		The start year, or null if none.
	 *
	 *	@hibernate.property access="field"
	 */

	public Integer getStartYear () {
		return startYear;
	}

	/**	Gets the end year.
	 *
	 *	@return		The end year, or null if none.
	 *
	 *	@hibernate.property access="field"
	 */

	public Integer getEndYear () {
		return endYear;
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
		return ":startYear <= word.work.pubDate.startYear and " +
			"word.work.pubDate.endYear <= :endYear";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setInteger("startYear", startYear.intValue());
		q.setInteger("endYear", endYear.intValue());
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
		line.appendRun("publication year = " + toString(), romanFontInfo);
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
		return new Spelling(toString(), TextParams.ROMAN);
	}

	/**	Returns a string representation of the publication year range.
	 *
	 *	@return		String representation.
	 */

	public String toString () {
		if (startYear == null) {
			return "none";
		} else {
			if (startYear.equals(endYear)) {
				return startYear.toString();
			} else {
				return startYear.toString() + "-" + endYear.toString();
			}
		}
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof PubYearRange)) return false;
		PubYearRange other = (PubYearRange)obj;
		return (Compare.equals(startYear, other.startYear) &&
			Compare.equals(endYear, other.endYear));
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		if (startYear == null) {
			return endYear == null ? 0 : endYear.hashCode();
		} else {
			return startYear == null ? endYear.hashCode() :
				startYear.hashCode() + endYear.hashCode();
		}
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

