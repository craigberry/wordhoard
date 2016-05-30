package edu.northwestern.at.wordhoard.model;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.hibernate.*;

import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.bibtool.*;
import edu.northwestern.at.utils.*;

/**	A work.
 *
 *	<p>Each work has the following attributes in addition to those defined
 *	for {@link edu.northwestern.at.wordhoard.model.WorkPart work parts}.
 *
 *	<ul>
 *	<li>The {@link edu.northwestern.at.wordhoard.model.Corpus
 *		corpus} to which the work belongs.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.wrappers.PubYearRange
 *		publication date(s)}, if known.
 *	<li>A set of the work's
 *		{@link edu.northwestern.at.wordhoard.model.speakers.Speaker speakers}.
 *	<li>A set of the work's
 *		{@link edu.northwestern.at.wordhoard.model.Author authors}.
 *	</ul>
 *
 *	@hibernate.subclass discriminator-value="1"
 */

public class Work extends WorkPart implements GroupingObject,
	SearchDefaults, SearchCriterion, CanGetRelFreq, Serializable, HasTag
{

	/**	The corpus to which this work belongs. */

	private Corpus corpus;

	/**	Publication date(s). */

	private PubYearRange pubDate;

	/**	Speakers. */

	private Set speakers = new HashSet();

	/**	Authors. */

	private Set authors = new HashSet();

	/**	Creates a new work.
	 */

	public Work () {
	}

	/**	Gets the corpus.
	 *
	 *	@return			The corpus.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="corpus_index"
	 */

	public Corpus getCorpus () {
		return corpus;
	}

	/**	Sets the corpus.
	 *
	 *	@param	corpus		The corpus.
	 */

	public void setCorpus (Corpus corpus) {
		this.corpus = corpus;
	}

	/**	Gets the publication date(s).
	 *
	 *	@return		The publication date(s), or null if not known.
	 *
	 *	@hibernate.component prefix="pubDate_"
	 */

	public PubYearRange getPubDate () {
		return pubDate;
	}

	/**	Sets the publication date(s).
	 *
	 *	@param	pubDate		The publication date(s), or null if not known.
	 */

	public void setPubDate (PubYearRange pubDate) {
		this.pubDate = pubDate;
	}

	/**	Gets the authors.
	 *
	 *	@return		The authors, as an unmodifiable set.
	 *
	 *	@hibernate.set name="authors" table="authors_works"
	 *		access="field" lazy="true" inverse="true"
	 *	@hibernate.collection-key column="work_id"
	 *	@hibernate.collection-many-to-many
	 *		class="edu.northwestern.at.wordhoard.model.Author"
	 *		column="author_id" foreign-key="author_id_index"
	 */

	public Set getAuthors () {
		return Collections.unmodifiableSet(authors);
	}

	/**	Adds an author.
	 *
	 *	@param	author	Author.
	 */

	public void addAuthor (Author author) {
		authors.add(author);
	}

	/**	Removes an author.
	 *
	 *	@param	author	Author.
	 */

	public void removeAuthor (Author author) {
		authors.remove(author);
	}

	/**	Adds a collection of authors.
	 *
	 *	@param	authors		Authors.
	 */

	public void addAuthors (Collection authors) {
		if (authors == null) return;
		for (Iterator it = authors.iterator(); it.hasNext(); )
			this.authors.add((Author)it.next());
	}

	/**	Adds an array of authors.
	 *
	 *	@param	authors		Authors.
	 */

	public void addAuthors (Author[] authors) {
		if (authors == null) return;
		for (int i = 0; i < authors.length; i++)
			this.authors.add(authors[i]);
	}

	/**	Gets the speakers.
	 *
	 *	@return		The speakers, as an unmodifiable set.
	 *
	 *	@hibernate.set name="speakers" lazy="true" inverse="true" access="field"
	 *	@hibernate.collection-key column="work"
	 *	@hibernate.collection-one-to-many
	 *		class="edu.northwestern.at.wordhoard.model.speakers.Speaker"
	 */

	public Set getSpeakers () {
		return Collections.unmodifiableSet(speakers);
	}

	/**	Adds a speaker.
	 *
	 *	@param	speaker		Speaker.
	 */

	public void addSpeaker (Speaker speaker) {
		speaker.setWork(this);
		speakers.add(speaker);
	}

	/**	Gets all the parts of the work with text.
	 *
	 *	@return		A list of all the parts of the work which have text.
	 */

	public List getPartsWithText () {
		List list = new ArrayList();
		appendDescendantsWithText(list);
		return list;
	}

	/**	Gets all the parts of the work.
	 *
	 *	@return		A list of all the parts of the work.
	 */

	public List getParts () {
		List list = new ArrayList();
		appendDescendants(list);
		return list;
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(Work.class)) {
			return this;
		} else if (cls.equals(PubYearRange.class)) {
			return pubDate;
		} else if (cls.equals(Author.class)) {
			if (authors == null || authors.size() == 0) return null;
			return (Author)authors.iterator().next();
		} else if (cls.equals(Corpus.class)) {
			return corpus;
		} else {
			return null;
		}
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass () {
		return null;
	}

	public Class getLemmaJoinClass () {
		return null;
	}


	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause () {
		return "word.work = :work";
	}

	public String getLemmaWhereClause () {
		return "wordPart.lemPos.lemma = lemma and wordPart.word.work = :work";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setEntity("work", this);
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
		line.appendRun("work = " + getFullTitle(), romanFontInfo);
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		return new Spelling(getShortTitleWithDate(), TextParams.ROMAN);
	}

	/**	Gets the full title with date.
	 *
	 *	@return		Full title with date.
	 */

	public String getFullTitleWithDate () {
		String fullTitle = getFullTitle();
		if (pubDate == null) {
			return fullTitle;
		} else {
			return fullTitle + " (" + pubDate.toString() + ")";
		}
	}

	/**	Gets the short title with date.
	 *
	 *	@return		Short title with date.
	 */

	public String getShortTitleWithDate () {
		String shortTitle = getShortTitle();
		if (pubDate == null) {
			return shortTitle;
		} else {
			return shortTitle + " (" + pubDate.toString() + ")";
		}
	}

	/**	Gets grouping objects.
	 *
	 *	@param	groupBy		Grouping class.
	 *
	 *	@param	list		A list. The grouping objects are appended
	 *						to this list.
	 */

	public void getGroupingObjects (Class groupBy, List list) {
		if (groupBy.equals(Corpus.class)) {
			if (corpus != null) list.add(corpus);
		} else if (groupBy.equals(Author.class)) {
			if (authors != null) {
				for (Iterator it = authors.iterator(); it.hasNext(); ) {
					Author author = (Author)it.next();
					list.add(author);
				}
			}
		} else if (groupBy.equals(PubYearRange.class)) {
			if (pubDate != null) list.add(pubDate);
		} else if (groupBy.equals(PubDecade.class)) {
			if (pubDate != null) {
				Integer startYearInteger = pubDate.getStartYear();
				Integer endYearInteger = pubDate.getEndYear();
				int startYear = startYearInteger == null ?
					endYearInteger.intValue() : startYearInteger.intValue();
				int endYear = endYearInteger == null ?
					startYearInteger.intValue() : endYearInteger.intValue();
				startYear = startYear/10*10;
				while (startYear <= endYear) {
					list.add(new PubDecade(startYear));
					startYear += 10;
				}
			}
		}
	}

// Added by BP 7/27/05

	/**	Gets a grouping object.
	 *
	 *	<p>Returns the work, lemma, part of speech, or spelling,
	 *	given a grouping option.
	 *
	 *	@param	groupingOption		Grouping option.
	 *
	 *	@return			Grouping object.
	 */

	public GroupingObject getGroupingObject (int groupingOption) {
		switch (groupingOption) {
			case GroupingWorkOptions.GROUP_BY_NONE:
				return this;
			case GroupingWorkOptions.GROUP_BY_AUTHOR:
				Iterator it = getAuthors().iterator();
				return it.hasNext() ? (Author)it.next() : null;
			case GroupingWorkOptions.GROUP_BY_YEAR:
			case GroupingWorkOptions.GROUP_BY_DECADE:
			case GroupingWorkOptions.GROUP_BY_QCENTURY:
			case GroupingWorkOptions.GROUP_BY_CENTURY:
				Integer startYear = pubDate == null ?
					null : pubDate.getStartYear();
				Integer endYear = pubDate == null ?
					null : pubDate.getEndYear();
				return DateGroup.getDateGroup(
					groupingOption, startYear, endYear);
		}
		return null;
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two works are equal if their tags are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Work)) return false;
		Work other = (Work)obj;
		return Compare.equals(getTag(), other.getTag());
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return getTag().hashCode();
	}

	/**	Gets a string representation of the work.
	 *
	 *	@return			The short title.
	 */

	public String toString () {
		return getShortTitle();
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

