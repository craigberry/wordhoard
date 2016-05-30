package edu.northwestern.at.wordhoard.model.speakers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.hibernate.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.utils.*;

/**	A speaker.
 *
 *	<p>Each speaker has the following attributes:
 *
 *	<ul>
 *	<li>A unique persistence id.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.Work
 *		work} in which the speaker occurs.
 *	<li>The tag of the speaker.
 *	<li>The English name of the speaker.
 *	<li>The name of the speaker in the original language. May be null.
 *	<li>A description of the speaker's role. May be null.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.wrappers.Gender gender}
 *		of the speaker.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.wrappers.Mortality
 *		mortality} of the speaker.
 *	</ul>
 *
 *	<p>Note that speakers belong to works. For example, the speaker "Zeus"
 *	in The Iliad is different from the speaker "Zeus" in The Odyssey. Thus,
 *	there is no way to form an identification of speakers across multiple
 *	works. Another example is the speaker "HenryIV" in King Henry the Fourth
 *	parts 1 and 2 in Shakespeare.
 *
 *	<p>This is arguably a bad model for speakers, but for now we have
 *	decided that it's the best way to go given the current state of the
 *	data, time constraints, and the inability of our puny brains to
 *	untangle these kinds of complexities.
 *
 *	@hibernate.class table="speaker"
 */

public class Speaker implements PersistentObject, SearchDefaults,
	SearchCriterion, GroupingObject, Serializable
{

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	Work. */

	private Work work;

	/**	Tag. */

	private String tag;

	/**	English name. */

	private String name;

	/**	Name in original language, or null if none. */

	private Spelling originalName;

	/**	Role description, or null if none. */

	private String description;

	/**	Gender. */

	private Gender gender = new Gender(Gender.UNCERTAIN_MIXED_OR_UNKNOWN);

	/**	Mortality. */

	private Mortality mortality = new Mortality(Mortality.UNKNOWN_OR_OTHER);

	/**	Creates a new speaker.
	 */

	public Speaker () {
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

	/**	Gets the work.
	 *
	 *	@return		The work.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="work_index"
	 */

	public Work getWork () {
		return work;
	}

	/**	Sets the work.
	 *
	 *	@param	work	The work.
	 */

	public void setWork (Work work) {
		this.work = work;
	}

	/**	Gets the tag.
	 *
	 *	@return		The tag.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getTag () {
		return tag;
	}

	/**	Sets the tag.
	 *
	 *	@param	tag		The tag.
	 */

	public void setTag (String tag) {
		this.tag = tag;
	}

	/**	Gets the English name.
	 *
	 *	@return		The English name.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="name" index="name_index"
	 */

	public String getName () {
		return name;
	}

	/**	Sets the English name.
	 *
	 *	@param	name		The English name.
	 */

	public void setName (String name) {
		this.name = name;
	}

	/**	Gets the name in the original language.
	 *
	 *	@return		The name in the original language.
	 *
	 *	@hibernate.component prefix="originalName_"
	 */

	public Spelling getOriginalName () {
		return originalName == null || originalName.getString() == null ?
			null : originalName;
	}

	/**	Sets the name in the original language.
	 *
	 *	@param	originalName	The name in the original language.
	 */

	public void setOriginalName (Spelling originalName) {
		this.originalName = originalName;
	}

	/**	Gets the role description.
	 *
	 *	@return		The role description.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getDescription () {
		return description;
	}

	/**	Sets the role description.
	 *
	 *	@param	description		The role description.
	 */

	public void setDescription (String description) {
		this.description = description;
	}

	/**	Gets the gender.
	 *
	 *	@return		The gender.
	 *
	 *	@hibernate.component prefix="gender_"
	 */

	public Gender getGender () {
		return gender;
	}

	/**	Sets the gender.
	 *
	 *	@param	gender	The gender.
	 */

	public void setGender (Gender gender) {
		this.gender = gender;
	}

	/**	Sets the gender.
	 *
	 *	@param	gender	The gender.
	 */

	public void setGender (byte gender) {
		this.gender = new Gender(gender);
	}

	/**	Gets the mortality.
	 *
	 *	@return		The mortality.
	 *
	 *	@hibernate.component prefix="mortality_"
	 */

	public Mortality getMortality () {
		return mortality;
	}

	/**	Sets the mortality.
	 *
	 *	@param	mortality	The mortality.
	 */

	public void setMortality (Mortality mortality) {
		this.mortality = mortality;
	}

	/**	Sets the mortality.
	 *
	 *	@param	mortality	The mortality.
	 */

	public void setMortality (byte mortality) {
		this.mortality = new Mortality(mortality);
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(Speaker.class)) {
			return this;
		} else if (cls.equals(Gender.class)) {
			return gender;
		} else if (cls.equals(Mortality.class)) {
			return mortality;
		} else if (cls.equals(Narrative.class)) {
			return new Narrative(Narrative.SPEECH);
		} else {
			return null;
		}
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
		return "speaker = :speaker";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setEntity("speaker", this);
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
		line.appendRun("speaker = " + name, romanFontInfo);
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
		String str;
		if (work == null) {
			str = name;
		} else {
			str = name + " in " + work.getShortTitle();
		}
		return new Spelling(str, TextParams.ROMAN);
	}

	/**	Gets grouping objects.
	 *
	 *	@param	groupBy		Grouping class.
	 *
	 *	@param	list		A list. The grouping objects are appended
	 *						to this list.
	 */

	public void getGroupingObjects (Class groupBy, List list) {
		if (groupBy.equals(SpeakerName.class)) {
			if (name != null) list.add(new SpeakerName(name));
		} else if (groupBy.equals(Gender.class)) {
			if (gender != null) list.add(gender);
		} else if (groupBy.equals(Mortality.class)) {
			if (mortality != null) list.add(mortality);
		}
	}

	/**	Gets a string representation of the speaker.
	 *
	 *	@return			The name.
	 */

	public String toString () {
		return name;
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two speakers are equal if their works and names are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Speaker)) return false;
		Speaker other = (Speaker)obj;
		return Compare.equals(work, other.getWork()) &&
			Compare.equals(name, other.getName());
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return work.hashCode() + name.hashCode();
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

