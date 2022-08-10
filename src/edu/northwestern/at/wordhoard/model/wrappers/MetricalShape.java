package edu.northwestern.at.wordhoard.model.wrappers;

/*	Please see the license information at the end of this file. */


import org.hibernate.Session;
import org.hibernate.query.Query;

import edu.northwestern.at.wordhoard.model.PersistentObject;
import edu.northwestern.at.wordhoard.model.grouping.GroupingObject;
import edu.northwestern.at.wordhoard.model.search.SearchCriterion;
import edu.northwestern.at.wordhoard.model.text.FontInfo;
import edu.northwestern.at.wordhoard.model.text.TextLine;
import edu.northwestern.at.wordhoard.model.text.TextParams;

/**	A metrical shape wrapper.
 *
 *	@hibernate.class table="metricalshape"
 */

public class MetricalShape implements PersistentObject, SearchCriterion,
	GroupingObject
{

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	The metrical shape. */

	private String metricalShape;

	/**	Creates a new metrical shape wrapper.
	 */

	public MetricalShape () {
	}

	/**	Creates a new metrical shape wrapper.
	 *
	 *	@param	metricalShape		The metrical shape.
	 */

	public MetricalShape (String metricalShape) {
		this.metricalShape = metricalShape;
	}

	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="native"
	 */

	public Long getId () {
		return id;
	}

	/**	Gets the metrical shape.
	 *
	 *	@return		The metrical shape.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getMetricalShape () {
		return metricalShape;
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
		return "word.metricalShape.metricalShape = :metricalShape";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setParameter("metricalShape", metricalShape);
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
		line.appendRun("metrical shape = " + metricalShape, romanFontInfo);
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "with metrical shape".
	 */

	public String getReportPhrase () {
		return "with metrical shape";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		return new Spelling(metricalShape, TextParams.ROMAN);
	}

	/**	Returns a string representation of the metrical shape.
	 *
	 *	@return		The metrical shape.
	 */

	public String toString () {
		return metricalShape;
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof MetricalShape)) return false;
		MetricalShape other = (MetricalShape)obj;
		return metricalShape.equals(other.metricalShape);
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return metricalShape.hashCode();
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

