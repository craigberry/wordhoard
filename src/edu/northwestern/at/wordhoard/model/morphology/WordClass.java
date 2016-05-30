package edu.northwestern.at.wordhoard.model.morphology;

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

/**	A word class.
 *
 *	<p>A word class is a category of words. E.g., "noun", "proper noun",
 *	"verb", "modal verb", "adverb", "adjective", "article", etc. Each
 *	word class has three attributes, all of them strings:
 *
 *	<ul>
 *	<li>A tag for the word class. E.g., "abr" for "abbreviation", "n" for
 *		"noun".
 *	<li>A description of the word class. E.g., "proper adjective".
 *	<li>The {@link edu.northwestern.at.wordhoard.model.wrappers.MajorWordClass
 *		major word class}.
 		E.g., the major word class is "pronoun" for
 *		the word classes "pni" (indefinite pronoun), "pnp" (personal
 *		pronoun), "pnq" (interrogative pronoun), "pnr" (relative pronoun)
 *		and "pnx" (reflexive pronoun).
 *	</ul>
 *
 *	<p>Both English and Greek works and corpora share the same collection
 *	of word classes. This is unlike parts of speech, where we have
 *	different taxonomies for English and Greek.
 *
 *	@hibernate.class table="wordclass"
 */

public class WordClass implements PersistentObject, SearchDefaults,
	SearchCriterion, GroupingObject, Serializable
{

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	The tag. */

	private String tag;

	/**	The description. */

	private String description;

	/**	Major word class. */

	private MajorWordClass majorWordClass;

	/**	Creates a new word class.
	 */

	public WordClass () {
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

	/**	Gets the description.
	 *
	 *	@return		The description.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getDescription () {
		return description;
	}

	/**	Sets the description.
	 *
	 *	@param	description		Description.
	 */

	public void setDescription (String description) {
		this.description = description;
	}

	/**	Gets the major word class.
	 *
	 *	@return		The major word class.
	 *
	 *	@hibernate.component prefix="majorWordClass_"
	 */

	public MajorWordClass getMajorWordClass () {
		return majorWordClass;
	}

	/**	Sets the major word class.
	 *
	 *	@param	majorWordClass		Major word class.
	 */

	public void setMajorWordClass (MajorWordClass majorWordClass) {
		this.majorWordClass = majorWordClass;
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(WordClass.class)) {
			return this;
		} else if (cls.equals(MajorWordClass.class)) {
			return majorWordClass;
		} else {
			return null;
		}
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
//		return "wordPart.lemPos.lemma.wordClass = :wordClass";
		return "lemma.wordClass = :wordClass";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setEntity("wordClass", this);
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
		line.appendRun("word class = " + tag, romanFontInfo);
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "with word class".
	 */

	public String getReportPhrase () {
		return "with word class";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		return new Spelling(tag, TextParams.ROMAN);
	}

	/**	Gets grouping objects.
	 *
	 *	@param	groupBy		Grouping class.
	 *
	 *	@param	list		A list. The grouping objects are appended
	 *						to this list.
	 */

	public void getGroupingObjects (Class groupBy, List list) {
		if (groupBy.equals(MajorWordClass.class)) {
			if (majorWordClass != null) list.add(majorWordClass);
		}
	}

	/**	Gets a string representation of the word class.
	 *
	 *	@return		The tag.
	 */

	public String toString () {
		return tag;
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two word classes are equal if their tags are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof WordClass)) return false;
		WordClass other = (WordClass)obj;
		return Compare.equals(tag, other.getTag());
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return tag.hashCode();
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

