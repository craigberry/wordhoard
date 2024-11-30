package edu.northwestern.at.wordhoard.model.morphology;

/*	Please see the license information at the end of this file. */

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import edu.northwestern.at.utils.Compare;
import edu.northwestern.at.utils.db.mysql.TableExporterImporter;
import edu.northwestern.at.wordhoard.model.PersistentObject;
import edu.northwestern.at.wordhoard.model.grouping.GroupingObject;
import edu.northwestern.at.wordhoard.model.search.SearchCriterion;
import edu.northwestern.at.wordhoard.model.search.SearchDefaults;
import edu.northwestern.at.wordhoard.model.text.FontInfo;
import edu.northwestern.at.wordhoard.model.text.TextLine;
import edu.northwestern.at.wordhoard.model.wrappers.Spelling;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**	A lemma.
 *
 *	<p>A lemma is a "head word". Each lemma has the following attributes:
 *
 *	<ul>
 *	<li>A tag formed from the spelling of the lemma, the word class
 *		of the lemma, and for homonyms a disambiguating number. For example,
 *		"love (n)" and "love (v)" are the noun "love" and verb "love"
 *		respectively. "lie (v) (1)" and "lie (v) (2)" are the lemmas
 *		for the verbs "lie" (to recline) and "lie" (to tell a falsehood).
 *	<li>A copy of the tag with all diacriticals removed and mapped to lower
 *		case.
 *	<li>The spelling of the lemma. E.g., "love" or "lie".
 *	<li>The homonym number. 0 if there are no homonyms (the common case).
 *	<li>The {@link edu.northwestern.at.wordhoard.model.morphology.WordClass
 *		word class} of the lemma.
 *	</ul>
 *
 *	@hibernate.class table="lemma"
 */

@Entity
@Table(name="lemma")
public class Lemma implements GroupingObject, PersistentObject,
	SearchDefaults, SearchCriterion, Serializable
{

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	The tag. */

	private Spelling tag;

	/**	The tag with all diacriticals removed and mapped to lower case. */

	private Spelling tagInsensitive;

	/**	The spelling. */

	private Spelling spelling;

	/**	The homonym. */

	private int homonym;

	/**	The word class. */

	private WordClass wordClass;

	/**	Creates a new lemma.
	 */

	public Lemma () {
	}

	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="assigned"
	 */

	@Access(AccessType.FIELD)
	@Id
	public Long getId () {
		return id;
	}

	/**	Sets the unique id.
	 *
	 *	@param	id		The unique id.
	 */

	public void setId (Long id) {
		this.id = id;
	}

	/**	Gets the tag.
	 *
	 *	@return		The tag.
	 *
	 *	@hibernate.component prefix="tag_"
	 */

	@Access(AccessType.FIELD)
	@AttributeOverrides({
		@AttributeOverride(name = "string", column = @Column(name = "tag_string")),
		@AttributeOverride(name = "charset", column = @Column(name = "tag_charset"))
	})
	public Spelling getTag () {
		return tag;
	}

	/**	Sets the tag.
	 *
	 *	@param	tag		The tag.
	 */

	public void setTag (Spelling tag) {
		this.tag = tag;
	}

	/**	Gets the insensitive tag.
	 *
	 *	@return		The tag with all diacriticals removed and mapped to
	 *				lower case.
	 *
	 *	@hibernate.component prefix="tagInsensitive_"
	 */

	@Access(AccessType.FIELD)
	@AttributeOverrides({
		@AttributeOverride(name = "string", column = @Column(name = "tagInsensitive_string")),
		@AttributeOverride(name = "charset", column = @Column(name = "tagInsensitive_charset"))
	})
	public Spelling getTagInsensitive () {
		return tagInsensitive;
	}

	/**	Sets the insensitive tag.
	 *
	 *	@param	tagInsensitive		The insensitive tag.
	 */

	public void setTagInsensitive (Spelling tagInsensitive) {
		this.tagInsensitive = tagInsensitive;
	}

	/**	Gets the spelling.
	 *
	 *	@return		The spelling of the lemma.
	 *
	 *	@hibernate.component prefix="spelling_"
	 */

	@Access(AccessType.FIELD)
	@AttributeOverrides({
		@AttributeOverride(name = "string", column = @Column(name = "spelling_string")),
		@AttributeOverride(name = "charset", column = @Column(name = "spelling_charset"))
	})
	public Spelling getSpelling () {
		return spelling;
	}

	/**	Sets the spelling.
	 *
	 *	@param	spelling	The spelling of the lemma.
	 */

	public void setSpelling (Spelling spelling) {
		this.spelling = spelling;
	}

	/**	Gets the homonym.
	 *
	 *	@return		The homonym.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getHomonym () {
		return homonym;
	}

	/**	Sets the homonym.
	 *
	 *	@param	homonym		The homonym.
	 */

	public void setHomonym (int homonym) {
		this.homonym = homonym;
	}

	/**	Gets the word class.
	 *
	 *	@return		The word class.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="wordClass_index"
	 */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="wordClass", foreignKey = @ForeignKey(name = "wordClass_index"))
	public WordClass getWordClass () {
		return wordClass;
	}

	/**	Sets the word class.
	 *
	 *	@param	wordClass		Word class.
	 */

	public void setWordClass (WordClass wordClass) {
		this.wordClass = wordClass;
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	@Transient
	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(Lemma.class)) {
			return this;
		} else if (cls.equals(WordClass.class)) {
			return wordClass;
		} else {
			if (wordClass == null) return null;
			return wordClass.getSearchDefault(cls);
		}
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "with lemma".
	 */

	@Transient
	public String getReportPhrase () {
		return "with lemma";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		return tag;
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	@Transient
	public Class getJoinClass () {
		return WordPart.class;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	@Transient
	public String getWhereClause () {
//		return "wordPart.lemPos.lemma = :lemma";
		return "lemma = :lemma";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setParameter("lemma", this);
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
		line.appendRun("lemma = ", romanFontInfo);
		FontInfo lemmaFontInfo = fontInfo[tag.getCharset()];
		line.appendRun(tag.getString(), lemmaFontInfo);
	}

	/**	Gets grouping objects.
	 *
	 *	@param	groupBy		Grouping class.
	 *
	 *	@param	list		A list. The grouping objects are appended
	 *						to this list.
	 */

	public void getGroupingObjects (Class groupBy, List list) {
		if (groupBy.equals(WordClass.class)) {
			if (wordClass != null) list.add(wordClass);
		} else {
			if (wordClass != null)
				wordClass.getGroupingObjects(groupBy, list);
		}
	}

	/**	Gets a string representation of the lemma.
	 *
	 *	@return		The tag.
	 */

	public String toString () {
		return tag.getString();
	}

	/**	Exports the object to a MySQL table exporter/importer.
	 *
	 *	@param	exporterImporter	MySQL table exporter/importer.
	 */

	public void export (TableExporterImporter exporterImporter) {
		exporterImporter.print(id);
		String tagString = null;
		byte tagCharset = 0;
		if (tag != null) {
			tagString = tag.getString();
			tagCharset = tag.getCharset();
		}
		exporterImporter.print(tagString);
		exporterImporter.print(tagCharset);
		String tagInsensitiveString = null;
		byte tagInsensitiveCharset = 0;
		if (tagInsensitive != null) {
			tagInsensitiveString = tagInsensitive.getString();
			tagInsensitiveCharset = tagInsensitive.getCharset();
		}
		exporterImporter.print(tagInsensitiveString);
		exporterImporter.print(tagInsensitiveCharset);
		String spellingString = null;
		byte spellingCharset = 0;
		if (spelling != null) {
			spellingString = spelling.getString();
			spellingCharset = spelling.getCharset();
		}
		exporterImporter.print(spellingString);
		exporterImporter.print(spellingCharset);
		exporterImporter.print(homonym);
		Long wordClassId = wordClass == null ? null : wordClass.getId();
		exporterImporter.print(wordClassId);
		exporterImporter.println();
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two lemmas are equal if their tags are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Lemma)) return false;
		Lemma other = (Lemma)obj;
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

