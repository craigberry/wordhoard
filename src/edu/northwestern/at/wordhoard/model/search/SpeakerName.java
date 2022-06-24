package edu.northwestern.at.wordhoard.model.search;

/*	Please see the license information at the end of this file. */

import org.hibernate.*;

import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.*;

/**	A speaker name search criterion.
 */

public class SpeakerName implements SearchCriterion, GroupingObject {

	/**	The name. */

	private String name;

	/**	Creates a new speaker name object.
	 */

	public SpeakerName () {
	}

	/**	Creates a new speaker name object.
	 *
	 *	@param	name		Name.
	 */

	public SpeakerName (String name) {
		this.name = name;
	}

	/**	Gets the name.
	 *
	 *	@return 	The name.
	 */

	public String getName () {
		return name;
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
		return "speaker.name = :speakerName";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setString("speakerName", name);
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
		line.appendRun("speaker name = " + name, romanFontInfo);
	}

	/**	Gets the report phrase.
	 *
	 *	@return		"spoken by".
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
		return new Spelling(name, TextParams.ROMAN);
	}

	/**	Returns true if this object is equal to some other object.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof SpeakerName)) return false;
		SpeakerName other = (SpeakerName)obj;
		return Compare.equals(name, other.name);
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return name == null ? 0 : name.hashCode();
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

