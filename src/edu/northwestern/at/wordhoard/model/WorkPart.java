package edu.northwestern.at.wordhoard.model;

/*	Please see the license information at the end of this file. */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;

import edu.northwestern.at.utils.Compare;
import edu.northwestern.at.wordhoard.model.grouping.GroupingObject;
import edu.northwestern.at.wordhoard.model.search.SearchCriterion;
import edu.northwestern.at.wordhoard.model.search.SearchDefaults;
import edu.northwestern.at.wordhoard.model.text.FontInfo;
import edu.northwestern.at.wordhoard.model.text.TextLine;
import edu.northwestern.at.wordhoard.model.text.TextParams;
import edu.northwestern.at.wordhoard.model.wrappers.Spelling;
import edu.northwestern.at.wordhoard.model.wrappers.TaggingData;
import edu.northwestern.at.wordhoard.model.wrappers.TextWrapper;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.DiscriminatorType;

/**	A work part.
 *
 *	<p>Works are divided into parts which form a tree.
 *
 *	<p>Here's some examples of how works might be divided into parts:
 *
 *	<ul>
 *	<li>A play might be a list of acts, where each act is a list of scenes.
 *	<li>A novel might be a list of chapters.
 *	<li>A short poem might have only a single part, the entire poem.
 *	<li>An anthology might be a list of contributions.
 *	<li>The Canterbury tales might be grouped by tale, with each tale
 *		containing its prologue (if any), the body of the tale, its
 *		epilogue (if any), etc.
 *	</ul>
 *
 *	<p>Each work part has the following attributes:
 *
 *	<ul>
 *	<li>A unique persistence id.
 *	<li>A tag (see below).
 *	<li>A path tag (see below).
 *	<li>A short title.
 *	<li>A full title.
 *	<li>The types of tagging data.
 *	<li>The parent node, which is null for the work itself (the root of the
 *		tree of parts).
 *	<li>The {@link edu.northwestern.at.wordhoard.model.Work
 *		work} of which this is a part.
 *	<li>An ordered list of child nodes, which is empty for the final "leaf"
 *		parts of the work.
 *	<li>A flag which tells whether the part has any children.
 *	<li>The primary text for the work part, if any.
 *	<li>A map of available translations for the work part, if any.
 *	<li>The ordinal within the work of the work part.
 *	<li>Counts of the number of lines and words in the subtree rooted at
 *		the part.
 *	<li>A flag which tells whether the text for the part has stanza numbers.
 *	</ul>
 *
 *	<p>Note that each work and work part has both a short title and a full
 *	title. The different titles are used in different contexts. For example,
 *	for Shakespeare's Hamlet, the full title is "Hamlet, Prince of Denmark",
 *	while the short title is just "Hamlet". The full title of act 1, scene 2
 *	is "Act 1, Scene 2", while the short title is just "Scene 2".
 *
 *	<p>The tag is required. It permanently and uniquely identifies
 *	the work part across all corpora. It may be used to refer to the work part
 *	from outside the object model. It is guaranteed not to change across
 *	versions of the object model. For example, the tag for Hamlet (the root
 *	of the work part tree for Hamlet) is "sha-ham", and the tag for Hamlet
 *	Act 3, Scene 2 is "sha-ham-3-2".
 *
 *	<p>The path tag is optional but usually present. Path tags are used to
 *	build short "path" strings that identify parts - e.g., "ham.3.2" for
 *	Hamlet Act 3, Scene 2, or "can.mil.p" for the prologue to The Miller's Tale.
 *
 *	@hibernate.class table="workpart"
 *		discriminator-value="0"
 *	@hibernate.discriminator column="is_work" type="int"
 */

@Entity
@Table(name="workpart", indexes =  @Index(name = "tag_index", columnList = "tag"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("0")
@DiscriminatorColumn(name = "is_work", discriminatorType = DiscriminatorType.INTEGER)
public class WorkPart implements PersistentObject, CanCountWords,
	GroupingObject, SearchDefaults, SearchCriterion, CanGetRelFreq, HasTag
{

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	The tag of the work part. */

	private String tag;

	/**	The path tag of the work part. */

	private String pathTag;

	/**	Short title. */

	private String shortTitle;

	/**	Full title. */

	private String fullTitle;

	/**	The types of tagging data supported. */

	private TaggingData taggingData;

	/**	Parent node, or null for the root of the tree (the work). */

	private WorkPart parent;

	/**	The work to which this part belongs. */

	private Work work;

	/**	List of child parts. */

	private List<WorkPart> children = new ArrayList<WorkPart>();	// element type is WorkPart
	
	/**	True if the part has any children. */
	
	private boolean hasChildren;

	/**	The primary text of the work part. */

	private TextWrapper primaryText;

	/**	Map from translation names to translations for the work part. */

	private Map<String, TextWrapper> translations = new HashMap<String, TextWrapper>();

	/**	The ordinal of the part within its work. */

	private int workOrdinal;

	/**	Number of lines in subtree rooted at this part. */

	private int numLines;

	/**	Number of words in subtree rooted at this part. */

	private int numWords;

	/**	True if text has stanza numbers. */

	private boolean hasStanzaNumbers = false;

	/**	Used to mark active state for displaying isolated parts. */

	private boolean isActive = true;

	/**	Creates a new work part.
	 */

	public WorkPart () {
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
	 *	@return		The tag, or null if none.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="tag" index="tag_index"
	 */

	@Access(AccessType.FIELD)
	public String getTag () {
		return tag;
	}

	/**	Sets the tag.
	 *
	 *	@param	tag		The tag, or null if none.
	 */

	public void setTag (String tag) {
		this.tag = tag;
	}

	/**	Gets the path tag.
	 *
	 *	@return		The path tag.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	public String getPathTag () {
		return pathTag;
	}

	/**	Sets the path tag.
	 *
	 *	@param	pathTag		The path tag.
	 */

	public void setPathTag (String pathTag) {
		this.pathTag = pathTag;
	}

	/**	Gets the short title.
	 *
	 *	@return		The short title.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	public String getShortTitle () {
		return shortTitle;
	}

	/**	Sets the short title.
	 *
	 *	@param	shortTitle	The short title.
	 */

	public void setShortTitle (String shortTitle) {
		this.shortTitle = shortTitle;
	}

	/**	Gets the full title.
	 *
	 *	@return		The full title.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	public String getFullTitle () {
		return fullTitle;
	}

	/**	Sets the full title.
	 *
	 *	@param	fullTitle	The full title.
	 */

	public void setFullTitle (String fullTitle) {
		this.fullTitle = fullTitle;
	}

	/**	Gets the tagging data flags.
	 *
	 *	@return		Tagging data flags.
	 *
	 *	@hibernate.component prefix="taggingData_"
	 */

	@Access(AccessType.FIELD)
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "flags", column = @Column(name = "taggingData_flags"))
	})
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

	/**	Gets the parent.
	 *
	 *	@return		The parent work part.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="parent_index"
	 */

	@Access(AccessType.FIELD)
    @ManyToOne
    @JoinColumn(name = "parent", foreignKey = @ForeignKey(name = "parent_index"))
	public WorkPart getParent () {
		return parent;
	}

	/**	Gets the work.
	 *
	 *	@return		The work.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="work_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne
	// @Embedded
	@JoinColumn(name="work", foreignKey = @ForeignKey(name = "work_index"))
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

	/**	Gets the children.
	 *
	 *	@return			The children as an unmodifiable list.
	 *
	 *	@hibernate.list name="children" table="workpart_children"
	 *		access="field" lazy="true"
	 *	@hibernate.collection-key column="parent_id"
	 *	@hibernate.collection-index column="child_index"
	 *	@hibernate.collection-many-to-many column="child_id"
	 *		class="edu.northwestern.at.wordhoard.model.WorkPart"
	 */

	@Access(AccessType.FIELD)
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "workpart_children",
		joinColumns = {@JoinColumn(name = "parent_id")},
		inverseJoinColumns = {@JoinColumn(name = "child_id")}
	)
	@OrderColumn(name = "child_index")
	public List<WorkPart> getChildren () {
		return Collections.unmodifiableList(children);
	}

	/**	Gets a child by short title.
	 *
	 *	@param	title		Title.
	 *
	 *	@return				Child work part with specified short title, or
	 *						null if none.
	 */

	@Transient
	public WorkPart getChildByShortTitle (String title) {
		if (children == null) return null;
		for (Iterator<WorkPart> it = children.iterator(); it.hasNext(); ) {
			WorkPart child = (WorkPart)it.next();
			if (title.equals(child.getShortTitle())) return child;
		}
		return null;
	}

	/**	Returns true if this part has any children.
	 *
	 *	@return		True if part has children.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public boolean getHasChildren () {
		return hasChildren;
	}

	/**	Adds a child.
	 *
	 *	<p>The new child is added at the end of the ordered list
	 *	of children.
	 *
	 *	@param	child		The new child.
	 */

	public void addChild (WorkPart child) {
		children.add(child);
		child.parent = this;
		hasChildren = true;
	}

	/**	Removes a child.
	 *
	 *	@param	child		The child to remove.
	 */

	public void removeChild (WorkPart child) {
		children.remove(child);
	}

	/**	Gets the number of children.
	 *
	 *	@return				The number of children.
	 */

	@Transient
	public int getNumChildren () {
		return children.size();
	}

	/**	Gets the primary text.
	 *
	 *	@return		The primary text, or null if none.
	 *
	 *	@hibernate.many-to-one access="field"
	 *		foreign-key="primaryText_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne
	@JoinColumn(name="primaryText", foreignKey = @ForeignKey(name = "primaryText_index"))
	public TextWrapper getPrimaryText () {
		return primaryText;
	}

	/**	Sets the primary text.
	 *
	 *	@param	primaryText		The primary text for the work part.
	 */

	public void setPrimaryText (TextWrapper primaryText) {
		this.primaryText = primaryText;
	}

	/**	Gets the translation map.
	 *
	 *	@return		A map from translation names (e.g., "English") to
	 *				text wrappers for the translations.
	 *
	 *	@hibernate.map name="translations" table="workpart_translations"
	 *		access="field" lazy="true"
	 *	@hibernate.collection-key column="workPart_id"
	 *	@hibernate.collection-index column="translation_name" type="string"
	 *	@hibernate.collection-many-to-many column="textWrapper_id"
	 *		class="edu.northwestern.at.wordhoard.model.wrappers.TextWrapper"
	 */

	@Access(AccessType.FIELD)
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "workpart_translations",
		joinColumns = {
			@JoinColumn(name = "workPart_id")
		},
		inverseJoinColumns = {
			@JoinColumn(name = "textWrapper_id")
		}
	)
	@MapKeyColumn(name="translation_name")
	public Map<String, TextWrapper> getTranslations () {
		return translations;
	}

	/**	Adds a translation.
	 *
	 *	@param	name			The name of the translation
	 *							(e.g., "English").
	 *
	 *	@param	translation		The text of the translation.
	 */

	public void addTranslation (String name, TextWrapper translation) {
		translations.put(name, translation);
	}

	/**	Gets the available translations.
	 *
	 *	@return		List of all available translations in this
	 *				part or any of its descendants, enumerated in the
	 *				same order as in the owning corpus.
	 */

	@Transient
	public List<String> getAvailableTranslations () {
		ArrayList<String> result = new ArrayList<String>();
		if (translations == null) return result;
		HashSet avail = new HashSet();
		avail.addAll(translations.keySet());
		for (Iterator it = children.iterator(); it.hasNext(); ) {
			WorkPart child = (WorkPart)it.next();
			avail.addAll(child.getAvailableTranslations());
		}
		List<String> corpusAvail = work.getCorpus().getTranslationsAsList();
		for (Iterator<String> it = corpusAvail.iterator(); it.hasNext(); ) {
			String tran = (String)it.next();
			if (avail.contains(tran)) result.add(tran);
		}
		return result;
	}

	/**	Gets the number of work parts in the subtree rooted at this part.
	 *
	 *	@return		Number of work parts in subtree.
	 */

	@Transient
	public int getNumWorkPartsTree () {
		int result = 1;
		for (Iterator<WorkPart> it = children.iterator(); it.hasNext(); ) {
			WorkPart child = (WorkPart)it.next();
			result += child.getNumWorkPartsTree();
		}
		return result;
	}

	/**	Gets the work ordinal.
	 *
	 *	@return		The ordinal of the part within its work.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getWorkOrdinal () {
		return workOrdinal;
	}

	/**	Sets the work ordinal.
	 *
	 *	@param	workOrdinal		The ordinal of the part within its work.
	 */

	public void setWorkOrdinal (int workOrdinal) {
		this.workOrdinal = workOrdinal;
	}

	/**	Gets the number of lines.
	 *
	 *	@return		Number of lines in subtree rooted at this part.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getNumLines () {
		return numLines;
	}

	/**	Sets the number of lines.
	 *
	 *	@param	numLines	Number of lines in subtree rooted at this part.
	 */

	public void setNumLines (int numLines) {
		this.numLines = numLines;
	}

	/**	Gets the number of words.
	 *
	 *	@return		Number of words in subtree rooted at this part.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getNumWords () {
		return numWords;
	}

	/**	Sets the number of words.
	 *
	 *	@param	numWords	Number of words in subtree rooted at this part.
	 */

	public void setNumWords (int numWords) {
		this.numWords = numWords;
	}

	/**	Returns true if the text has stanza numbers.
	 *
	 *	@return		True if the text has stanza numbers.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public boolean getHasStanzaNumbers () {
		return hasStanzaNumbers;
	}

	/**	Sets the has stanza numbers flag.
	 *
	 *	@param	hasStanzaNumbers	True if the text has stanza numbers.
	 */

	public void setHasStanzaNumbers (boolean hasStanzaNumbers) {
		this.hasStanzaNumbers = hasStanzaNumbers;
	}

	/**	Gets the relative frequency of a word count.
	 *
	 *	@param	count		Word count.
	 *
	 *	@return				10,000 times count / number of words in part.
	 */

	@Transient
	public float getRelFreq (int count) {
		return 10000f*count/numWords;
	}

	/**	Gets the first descendant part with text.
	 *
	 *	@return		The first descendant part with text, or null if none.
	 */

	@Transient
	public WorkPart getFirstDescendantWithText () {
		if (primaryText != null) return (WorkPart)this;
		for (Iterator<WorkPart> it = children.iterator(); it.hasNext(); ) {
			WorkPart child = (WorkPart)it.next();
			WorkPart result = child.getFirstDescendantWithText();
			if (result != null) return result;
		}
		return (WorkPart)null;
	}

	/**	Appends all of the descendants which have text to a list.
	 *
	 *	@param	list		The list.
	 */

	public void appendDescendantsWithText (List<WorkPart> list) {
		if (primaryText != null) list.add(this);
		for (Iterator<WorkPart> it = children.iterator(); it.hasNext(); ) {
			WorkPart child = (WorkPart)it.next();
			child.appendDescendantsWithText(list);
		}
	}

	/**	Appends all of the descendants to a list.
	 *
	 *	@param	list		The list.
	 */

	public void appendDescendants (List<WorkPart> list) {
		list.add(this);
		for (Iterator<WorkPart> it = children.iterator(); it.hasNext(); ) {
			WorkPart child = (WorkPart)it.next();
			child.appendDescendants(list);
		}
	}

	/**	Gets the path string for the part.
	 *
	 *	@return		The path string.
	 */

	@Transient
	public String getPath () {
		if (parent == null) return pathTag;
		String parentPath = parent.getPath();
		return pathTag == null ? parentPath : parentPath + "." + pathTag;
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	@Transient
	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(WorkPart.class)) return this;
		if (work == null) return null;
		return work.getSearchDefault(cls);
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	@Transient
	public Class getJoinClass () {
		return null;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	@Transient
	public String getWhereClause () {
		return "(word.workPart = :workPart or " +
			"word.workPart.parent = :workPart or " +
			"word.workPart.parent.parent = :workPart)";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setParameter("workPart", this);
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
		line.appendRun("work part = " + getFullTitle(), romanFontInfo);
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "in".
	 */

	@Transient
	public String getReportPhrase () {
		return "in";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	@Transient
	public Spelling getGroupingSpelling (int numHits) {
		return new Spelling(getFullTitle(), TextParams.ROMAN);
	}

	/**	Gets a string representation of the part.
	 *
	 *	@return			The full title.
	 */

	public String toString () {
		return fullTitle;
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two work parts are equal if their works and their
	 *	work ordinals are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof WorkPart)) return false;
		WorkPart other = (WorkPart)obj;
		return Compare.equals(work, other.getWork()) &&
			workOrdinal == other.getWorkOrdinal();
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return work.hashCode() + workOrdinal;
	}

	/**	Returns active state of item.
	 *
	 *	@return		The isActive.
	 */

	@Transient
	public boolean isActive () {
		return isActive;
	}

	/**	Sets active state of item.
	 *
	 *	@param	isActive	Active state.
	 */

	public void setActive (boolean isActive) {
		this.isActive = isActive;
		for (Iterator<WorkPart> it = children.iterator(); it.hasNext(); ) {
			WorkPart child = (WorkPart)it.next();
			child.setActive(isActive);
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


