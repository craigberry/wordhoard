package edu.northwestern.at.wordhoard.model;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.hibernate.*;

import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.tconview.*;
import edu.northwestern.at.utils.*;

/**	A corpus.
 *
 *	<p>Each corpus has the following attributes:
 *
 *	<ul>
 *	<li>A unique persistence id.
 *	<li>The corpus tag (e.g., "sha" for Shakespeare).
 *	<li>A title.
 *	<li>The character set.
 *	<li>The part of speech taxonomy.
 *	<li>The types of tagging data supported.
 *	<li>A set of {@link edu.northwestern.at.wordhoard.model.Work
 *		works}.
 *	<li>Counts of the number of work parts, lines, and words in the corpus.
 *	<li>The maximum length of the word paths for the corpus.
 *	<li>A string enumerating the translations available for the corpus,
 *		if any.
 *	<li>An optional description of the translations for presentation in the
 *		translations dialog.
 *	<li>An ordered list of table of contents views.
 *	<li>An ordinal that determines the order in which the corpora are 
 *		displayed in the table of contents window.
 *	</ul>
 *
 *	@hibernate.class table="corpus"
 */

public class Corpus implements PersistentObject, CanCountWords,
	GroupingObject, SearchDefaults, SearchCriterion,
	CanGetRelFreq, Serializable, HasTag
{

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	The tag of the corpus. */

	private String tag;

	/**	The title of the corpus. */

	private String title;

	/**	The character set used by the corpus. */

	private byte charset;

	/**	The part of speech taxonomy used by the corpus. */

	private byte posType;

	/**	The types of tagging data supported. */

	private TaggingData taggingData;

	/**	Set of works belonging to this corpus. */

	private Set works = new HashSet();

	/**	Number of work parts in corpus. */

	private int numWorkParts;

	/**	Number of lines in corpus. */

	private int numLines;

	/**	Number of words in corpus. */

	private int numWords;

	/**	Maximum word path length. */

	private int maxWordPathLength;

	/**	Translations. */

	private String translations;
	
	/**	Translations description. */
	
	private String tranDescription;
	
	/**	List of table of contents views. */
	
	private List tconViews = new ArrayList();
	
	/**	Ordinal. */
	
	private int ordinal;

	/**	Creates a new corpus.
	 */

	public Corpus () {
	}

	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="assigned"
	 */

	public Long getId () {
		return id;
	}

	/**	Gets the tag.
	 *
	 *	@return		The tag.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="tag" index="tag_index"
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

	/**	Gets the title.
	 *
	 *	@return		The title.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getTitle () {
		return title;
	}

	/**	Sets the title.
	 *
	 *	@param	title		The title.
	 */

	public void setTitle (String title) {
		this.title = title;
	}

	/**	Gets the character set used by the corpus.
	 *
	 *	@return		TextParams.ROMAN or TextParams.GREEK.
	 *
	 *	@hibernate.property access="field"
	 */

	public byte getCharset () {
		return charset;
	}

	/**	Sets the character set used by the corpus.
	 *
	 *	@param	charset		TextParams.ROMAN or TextParams.GREEK.
	 */

	public void setCharset (byte charset) {
		this.charset = charset;
	}

	/**	Gets the part of speech taxonomy used by the corpus.
	 *
	 *	@return		Pos.ENGLISH or Pos.GREEK.
	 *
	 *	@hibernate.property access="field"
	 */

	public byte getPosType () {
		return posType;
	}

	/**	Sets the part of speech taxonomy used by the corpus.
	 *
	 *	@param	posType		Pos.ENGLISH or Pos.GREEK.
	 */

	public void setPosType (byte posType) {
		this.posType = posType;
	}

	/**	Gets the tagging data flags.
	 *
	 *	@return		Tagging data flags.
	 *
	 *	@hibernate.component prefix="taggingData_"
	 */

	public TaggingData getTaggingData () {
		return taggingData;
	}

	/**	Sets the tagging data flags.
	 *
	 *	@param	taggingData		Tagging data flags.
	 */

	public void setTaggingData (TaggingData taggingData) {
		this.taggingData = taggingData;
	}

	/**	Gets the works.
	 *
	 *	@return			The works as an unmodifiable set.
	 *
	 *	@hibernate.set name="works" inverse="true" access="field" lazy="true"
	 *	@hibernate.collection-key column="corpus"
	 *	@hibernate.collection-one-to-many
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
		work.setCorpus(this);
		works.add(work);
	}

	/**	Removes a work.
	 *
	 *	@param	work		The work.
	 */

	public void removeWork (Work work) {
		works.remove(work);
		work.setCorpus(null);
	}

	/**	Gets the number of works in the corpus.
	 *
	 *	@return		Number of works.
	 */

	public int getNumWorks () {
		return works.size();
	}

	/**	Gets the number of work parts in the corpus.
	 *
	 *	@return		Number of work parts.
	 *
	 *	@hibernate.property access="field"
	 */

	public int getNumWorkParts () {
		return numWorkParts;
	}

	/**	Sets the number of work parts in the corpus.
	 *
	 *	@param	numWorkParts	Number of work parts.
	 */

	public void setNumWorkParts (int numWorkParts) {
		this.numWorkParts = numWorkParts;
	}

	/**	Gets the number of lines in the corpus.
	 *
	 *	@return		Number of lines.
	 *
	 *	@hibernate.property access="field"
	 */

	public int getNumLines () {
		return numLines;
	}

	/**	Sets the number of lines in the corpus.
	 *
	 *	@param	numLines	Number of lines.
	 */

	public void setNumLines (int numLines) {
		this.numLines = numLines;
	}

	/**	Gets the number of words in the corpus.
	 *
	 *	@return		Number of words.
	 *
	 *	@hibernate.property access="field"
	 */

	public int getNumWords () {
		return numWords;
	}

	/**	Sets the number of words in the corpus.
	 *
	 *	@param	numWords	Number of words.
	 */

	public void setNumWords (int numWords) {
		this.numWords = numWords;
	}

	/**	Gets the maximum word path length.
	 *
	 *	@return		Maximum word path length.
	 *
	 *	@hibernate.property access="field"
	 */

	public int getMaxWordPathLength () {
		return maxWordPathLength;
	}

	/**	Sets the maximum word path length.
	 *
	 *	@param	maxWordPathLength	Maximum word path length.
	 */

	public void setMaxWordPathLength (int maxWordPathLength) {
		this.maxWordPathLength = maxWordPathLength;
	}

	/**	Gets the translations.
	 *
	 *	<p>Returns a comma-delimited string listing all the available
	 *	translations for the corpus. E.g., "English,German". If no
	 *	translations are available, returns null.
	 *
	 *	@return		Available translations, or null if none.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getTranslations () {
		return translations;
	}

	/**	Returns true if the corpus has available translations.
	 *
	 *	@return			True if corpus has available translations.
	 */

	public boolean hasTranslations () {
		return translations != null && translations.length() > 0;
	}

	/**	Gets the translations as a list.
	 *
	 *	@return		Available translations as a list of strings.
	 */

	public List getTranslationsAsList () {
		ArrayList result = new ArrayList();
		if (translations == null) return result;
		StringTokenizer tok = new StringTokenizer(translations, ",");
		while (tok.hasMoreTokens()) result.add(tok.nextToken());
		return result;
	}

	/**	Sets the translations.
	 *
	 *	@param	translations	Available translations as a comma-
	 *							delimited string, or null if none.
	 */

	public void setTranslations (String translations) {
		this.translations = translations;
	}
	
	/**	Gets the translations description.
	 *
	 *	@return		The translations description, or null if none.
	 *
	 *	@hibernate.property access="field" length="10000"
	 */
	 
	public String getTranDescription () {
		return tranDescription;
	}
	
	/**	Sets the translations description.
	 *
	 *	@param	tranDescription		Translations description, or null if none.
	 */
	 
	public void setTranDescription (String tranDescription) {
		this.tranDescription = tranDescription;
	}

	/**	Gets the list of table of contents views.
	 *
	 *	@return			The list of table of contents view as an unmodifiable 
	 *					list.
	 *
	 *	@hibernate.list access="field" lazy="true" table="corpus_tconviews"
	 *	@hibernate.collection-key column="corpus"
	 *	@hibernate.collection-index column="corpus_index"
	 *	@hibernate.collection-many-to-many column="tconview"
	 *		class="edu.northwestern.at.wordhoard.model.tconview.TconView"
	 */

	public List getTconViews () {
		return Collections.unmodifiableList(tconViews);
	}
	
	/**	Adds a table of contents view.
	 *
	 *	<p>The new view is added to the end of the ordered list of
	 *	views.
	 *
	 *	@param	tconView		Table of contents view.
	 */
	 
	public void addTconView (TconView tconView) {
		tconViews.add(tconView);
	}
	
	/**	Gets the ordinal.
	 *
	 *	@return		Ordinal.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	public int getOrdinal () {
		return ordinal;
	}

	/**	Gets a map from work tags to works.
	 *
	 *	@return		A map from work tags to works.
	 */

	public Map getWorkMap () {
		HashMap map = new HashMap();
		for (Iterator it = works.iterator(); it.hasNext(); ) {
			Work work = (Work)it.next();
			map.put(work.getTag(), work);
		}
		return map;
	}

	/**	Gets the relative frequency of a word count in the corpus.
	 *
	 *	@param	count		Word count.
	 *
	 *	@return				10,000 times count / number of words in corpus.
	 */

	public float getRelFreq (int count) {
		return 10000f*count/numWords;
	}

	/**	Gets a work by tag.
	 *
	 *	@param	tag		Work tag.
	 *
	 *	@return			Work, or null if none found.
	 */

	public Work getWorkByTag (String tag) {
		for (Iterator it = works.iterator(); it.hasNext(); ) {
			Work work = (Work)it.next();
			if (tag.equals(work.getTag())) return work;
		}
		return null;
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(Corpus.class)) {
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
		return null;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause () {
		return "word.work.corpus = :corpus";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setEntity("corpus", this);
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
		line.appendRun("corpus = " + title, romanFontInfo);
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
		return new Spelling(getTitle(), TextParams.ROMAN);
	}

	/**	Gets a string representation of the corpus.
	 *
	 *	@return		The title.
	 */

	public String toString () {
		return title;
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two corpora are equal if their tags are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Corpus)) return false;
		Corpus other = (Corpus)obj;
		return Compare.equals(tag, other.getTag());
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return tag == null ? 0 : tag.hashCode();
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

