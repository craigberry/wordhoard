package edu.northwestern.at.wordhoard.model.morphology;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	A word part.
 *
 *	<p>A word part specifies the word form (lempos) of a part of a 
 *	{@link edu.northwestern.at.wordhoard.model.Word word occurrence}. Most
 *	word occurrences have only one part. Compound words have more than one
 *	part. For example, the first word of Hamlet is the contraction "Who's".
 *	This word has two parts. The first part is the pronoun "who" with part of
 *	speech "pnq", and the second part is the verb "be" with part of speech
 *	"vp-3sg.pr".
 *
 *	<p>Word parts have the following attributes:
 *
 *	<ul>
 *	<li>The tag of the word to which this word part belongs.
 *	<li>The index of this part in the word to which it belongs.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.Word word} to which
 *		this word part belongs.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.WorkPart work part}
 *		which contains this word part.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.morphology.LemPos
 *		word form} of the part.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.morphology.BensonLemPos
 *		Benson word form} of the part (if any - for Chaucer).
 *	</ul>
 *
 *	@hibernate.class table="wordpart"
 */
 
public class WordPart implements PersistentObject, SearchDefaults {

	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	Tag. */

	private String tag;
	
	/**	Part index. */

	private int partIndex;
	
	/**	Word. */
	
	private Word word;
	
	/**	Work part. */
	
	private WorkPart workPart;
	
	/**	Word form. */
	
	private LemPos lemPos;
	
	/**	Benson lempos. */
	
	private BensonLemPos bensonLemPos;
	
	/**	Creates a new word part.
	 */
	
	public WordPart () {
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
	
	/**	Gets the part index.
	 *
	 *	@return		The part index.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public int getPartIndex () {
		return partIndex;
	}
	
	/**	Sets the part index.
	 *
	 *	@param	partIndex		The part index.
	 */
	 
	public void setPartIndex (int partIndex) {
		this.partIndex = partIndex;
	}
	
	/**	Gets the word.
	 *
	 *	@return		The word.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="word_index"
	 */
	 
	public Word getWord () {
		return word;
	}
	
	/**	Sets the word.
	 *
	 *	@param	word		The word.
	 */
	 
	public void setWord (Word word) {
		this.word = word;
	}
	
	/**	Gets the work part.
	 *
	 *	@return		The work part.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="workPart_index"
	 */
	 
	public WorkPart getWorkPart () {
		return workPart;
	}
	
	/**	Sets the work part.
	 *
	 *	@param	workPart		The work part.
	 */
	 
	public void setWorkPart (WorkPart workPart) {
		this.workPart = workPart;
	}
	
	/**	Gets the lempos.
	 *
	 *	@return		The lempos.
	 *
	 *	@hibernate.many-to-one access="field"  foreign-key="lemPos_index"
	 */
	
	public LemPos getLemPos () {
		return lemPos;
	}
	
	/**	Sets the lempos.
	 *
	 *	@param	lemPos		The lempos.
	 */
	 
	public void setLemPos (LemPos lemPos) {
		this.lemPos = lemPos;
	}
	
	/**	Gets the Benson lempos.
	 *
	 *	@return		The Benson lempos.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="bensonLemPos_index"
	 */
	 
	public BensonLemPos getBensonLemPos () {
		return bensonLemPos;
	}
	
	/**	Sets the Benson lempos.
	 *
	 *	@param	bensonLemPos		The Benson lempos.
	 */
	 
	public void setBensonLemPos (BensonLemPos bensonLemPos) {
		this.bensonLemPos = bensonLemPos;
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */
	
	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(Lemma.class) || cls.equals(Pos.class) ||
			cls.equals(MajorWordClass.class) ||
			cls.equals(WordClass.class))
		{
			if (lemPos == null) return null;
			return lemPos.getSearchDefault(cls);
		} else {
			if (word == null) return null;
			return word.getSearchDefault(cls);
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
		if (lemPos != null) lemPos.getGroupingObjects(groupBy, list);
	}
	
	/**	Gets a string representation of the word part.
	 *
	 *	@return		The tag plus the part index.
	 */
	 
	public String toString () {
		return tag + " " + partIndex;
	}
	
	/**	Exports the object to a MySQL table exporter/importer.
	 *
	 *	@param	exporterImporter	MySQL table exporter/importer.
	 */
	 
	public void export (TableExporterImporter exporterImporter) {
		exporterImporter.print(id);
		exporterImporter.print(tag);
		exporterImporter.print(partIndex);
		Long wordId = word == null ? null : word.getId();
		exporterImporter.print(wordId);
		Long workPartId = workPart == null ? null : workPart.getId();
		exporterImporter.print(workPartId);
		Long lemPosId = lemPos == null ? null : lemPos.getId();
		exporterImporter.print(lemPosId);
		Long bensonLemPosId = bensonLemPos == null ? null : 
			bensonLemPos.getId();
		exporterImporter.print(bensonLemPosId);
		exporterImporter.println();
	}
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two word parts are equal if their words and part indexes 
	 *	are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof WordPart)) return false;
		WordPart other = (WordPart)obj;
		return Compare.equals(word, other.getWord()) &&
			partIndex == other.getPartIndex();
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return word.hashCode() + partIndex;
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

