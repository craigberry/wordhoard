package edu.northwestern.at.wordhoard.model;

import java.io.Serializable;

/*	Please see the license information at the end of this file. */

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.query.Query;

import edu.northwestern.at.utils.Compare;
import edu.northwestern.at.wordhoard.model.grouping.GroupingObject;
import edu.northwestern.at.wordhoard.model.search.SearchCriterion;
import edu.northwestern.at.wordhoard.model.search.SearchDefaults;
import edu.northwestern.at.wordhoard.model.text.FontInfo;
import edu.northwestern.at.wordhoard.model.text.TextLine;
import edu.northwestern.at.wordhoard.model.wrappers.Spelling;

/**	An author.
 *
 *	<p>Each author has the following attributes:
 *
 *	<ul>
 *	<li>A unique persistence id.
 *	<li>The author's full name in English.
 *	<li>The author's full name in his original language.
 *	<li>The birth year, or null if not known.
 *	<li>The death year, or null if not known.
 *	<li>The earliest work year, or null if not known.
 *	<li>The latest work year, or null if not known.
 *	<li>A set of all the author's
 *		{@link edu.northwestern.at.wordhoard.model.Work works}.
 *	</ul>
 *
 *	@hibernate.class table="author"
 */

public class Author implements PersistentObject, SearchDefaults,
	SearchCriterion, GroupingObject, Serializable
{

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	The author's full name in English. */

	private Spelling name;

	/**	Author's full name in original language. */

	private Spelling originalName;

	/**	The author's birth year, or null if unknown. */

	private Integer birthYear;

	/**	The author's death year, or null if unknown. */

	private Integer deathYear;

	/**	The author's earliest work year, or null if unknown. */

	private Integer earliestWorkYear;

	/**	The author's latest work year, or null if unknown. */

	private Integer latestWorkYear;

	/**	The works. */

	private Set works = new HashSet();

	/**	Creates a new author.
	 */

	public Author () {
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

	/**	Gets the author's name.
	 *
	 *	@return		The name.
	 *
	 *	@hibernate.component prefix="name_"
	 */

	public Spelling getName () {
		return name;
	}

	/**	Sets the author's name.
	 *
	 *	@param	name	The author's name.
	 */

	public void setName (Spelling name) {
		this.name = name;
	}

	/**	Gets the author's name in the original language.
	 *
	 *	@return		The name in the original language.
	 *
	 *	@hibernate.component prefix="originalName_"
	 */

	public Spelling getOriginalName () {
		return originalName;
	}

	/**	Sets the author's name in the original language.
	 *
	 *	@param	originalName	The author's name in the original language.
	 */

	public void setOriginalName (Spelling originalName) {
		this.originalName = originalName;
	}

	/**	Gets the author's birth year.
	 *
	 *	@return		The birth year, or null if not available.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="birthYear" index="birthYear_index"
	 */

	public Integer getBirthYear () {
		return birthYear;
	}

	/**	Sets the author's birth year.
	 *
	 *	@param	birthYear	The birth year, or null if not available.
	 */

	public void setBirthYear (Integer birthYear) {
		this.birthYear = birthYear;
	}

	/**	Gets the author's death year.
	 *
	 *	@return		The death year, or null if not available.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="deathYear" index="deathYear_index"
	 */

	public Integer getDeathYear () {
		return deathYear;
	}

	/**	Sets the author's death year.
	 *
	 *	@param	deathYear	The death year, or null if not available.
	 */

	public void setDeathYear (Integer deathYear) {
		this.deathYear = deathYear;
	}

	/**	Gets the author's earliest work year.
	 *
	 *	@return		The earliest work year, or null if not available.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="earliestWorkYear" index="earliestWorkYear_index"
	 */

	public Integer getEarliestWorkYear () {
		return earliestWorkYear;
	}

	/**	Sets the author's earliest work year.
	 *
	 *	@param	earliestWorkYear	The earliest work year, or null if not available.
	 */

	public void setEarliestWorkYear (Integer earliestWorkYear) {
		this.earliestWorkYear = earliestWorkYear;
	}

	/**	Gets the author's latest work year.
	 *
	 *	@return		The latest work year, or null if not available.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="latestWorkYear" index="latestWorkYear_index"
	 */

	public Integer getLatestWorkYear () {
		return latestWorkYear;
	}

	/**	Sets the author's latest work year.
	 *
	 *	@param	latestWorkYear	The latest work year, or null if not available.
	 */

	public void setLatestWorkYear (Integer latestWorkYear) {
		this.latestWorkYear = latestWorkYear;
	}

	/**	Gets the works.
	 *
	 *	@return			The works as an unmodifiable set.
	 *
	 *	@hibernate.set name="works" table="authors_works"
	 *		inverse="false" access="field" lazy="true"
	 *	@hibernate.collection-key column="author_id"
	 *		foreign-key="author_id_index"
	 *	@hibernate.collection-many-to-many column="work_id"
	 *		foreign-key="work_id_index"
	 *		class="edu.northwestern.at.wordhoard.model.Work"
	 */

	public Set getWorks () {
		return Collections.unmodifiableSet(works);
	}

	/**	Adds a work.
	 *
	 *	@param	work		The new work.
	 */

	public void addWork (Work work) {
		works.add(work);
	}

	/**	Removes a work.
	 *
	 *	@param	work		The work.
	 */

	public void removeWork (Work work) {
		works.remove(work);
	}

	/**	Adds a collection of works.
	 *
	 *	@param	works		The new works.
	 */

	public void addWorks (Collection works) {
		if (works == null) return;
		for (Iterator it = works.iterator(); it.hasNext(); )
			this.works.add((Work)it.next());
	}

	/**	Adds an array of works.
	 *
	 *	@param	works		The new works.
	 */

	public void addWorks (Work[] works) {
		if (works == null) return;
		for (int i = 0; i < works.length; i++)
			this.works.add(works[i]);
	}

	/**	Gets the number of works by the author.
	 *
	 *	@return		Number of works.
	 */

	public int getNumWorks () {
		return works.size();
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(Author.class)) {
			return this;
		} else {
			return null;
		}
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass () {
		return Author.class;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause () {
		return "author = :author";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setParameter("author", this);
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
		line.appendRun("author = " + name.getString(), romanFontInfo);
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "in".
	 */

	public String getReportPhrase () {
		return "with author";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		return name;
	}

	/**	Gets a string representation of the author.
	 *
	 *	@return		The author's name.
	 */

	public String toString () {
		return name.getString();
	}

	/**	Gets a detailed string representation of the author.
	 *
	 *	@return		Detailed string representation.
	 */

	public String toStringDetailed () {
		return "Author:\n" +
			"id = \t'" + id + "'\n" +
			"name = \t'" + name + "'\n" +
			"originalName = \t" + originalName + "'\n" +
			"birthYear = \t'" + birthYear + "'\n" +
			"deathYear = \t'" + deathYear + "'\n" +
			"earliestWorkYearear = \t'" + earliestWorkYear + "'\n" +
			"latestWorkYear = \t'" + latestWorkYear + "'\n" +
			"number of works= \t'" + works.size() + "'\n" +
			"\n";
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two authors are equal if their names are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Author)) return false;
		Author other = (Author)obj;
		return Compare.equals(name, other.getName());
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return name.hashCode();
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

