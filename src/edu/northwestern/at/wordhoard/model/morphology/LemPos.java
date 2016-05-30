package edu.northwestern.at.wordhoard.model.morphology;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	A word form (lemma and part of speech).
 *
 *	<p>Word forms have the following attributes:
 *
 *	<ul>
 *	<li>The standard spelling of the word form.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.morphology.Lemma
 *		lemma}.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.morphology.Pos
 *		part of speech}.
 *	</ul>
 *
 *	@hibernate.class table="lempos"
 */
 
public class LemPos implements PersistentObject, SearchDefaults {

	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	Standard spelling. */
	
	private Spelling standardSpelling;
	
	/**	Lemma. */
	
	private Lemma lemma;
	
	/**	Part of speech. */
	
	private Pos pos;
	
	/**	Creates a new LemPos object.
	 */
	
	public LemPos () {
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
	
	/**	Gets the standard spelling.
	 *
	 *	@return		The standard spelling.
	 *
	 *	@hibernate.component prefix="standardSpelling_"
	 */
	
	public Spelling getStandardSpelling () {
		return standardSpelling;
	}
	
	/**	Sets the standard spelling.
	 *
	 *	@param	standardSpelling	The standard spelling.
	 */
	 
	public void setStandardSpelling (Spelling standardSpelling) {
		this.standardSpelling = standardSpelling;
	}
	
	/**	Gets the lemma.
	 *
	 *	@return		The lemma.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="lemma_index"
	 */
	
	public Lemma getLemma () {
		return lemma;
	}
	
	/**	Sets the lemma.
	 *
	 *	@param	lemma		The lemma.
	 */
	 
	public void setLemma (Lemma lemma) {
		this.lemma = lemma;
	}
	
	/**	Gets the part of speech.
	 *
	 *	@return		The part of speech.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="pos_index"
	 */
	
	public Pos getPos () {
		return pos;
	}
	
	/**	Sets the part of speech.
	 *
	 *	@param	pos		The part of speech.
	 */
	 
	public void setPos (Pos pos) {
		this.pos = pos;
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */
	
	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(Lemma.class)) {
			return lemma;
		} else if (cls.equals(Pos.class)) {
			return pos;
		} else {
			if (lemma == null) return null;
			return lemma.getSearchDefault(cls);
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
		if (groupBy.equals(Lemma.class)) {
			if (lemma != null) list.add(lemma);
		} else if (groupBy.equals(Pos.class)) {
			if (pos != null) list.add(pos);
		} else if (groupBy.equals(WordClass.class) ||
			groupBy.equals(MajorWordClass.class))
		{
			if (lemma != null) 
				lemma.getGroupingObjects(groupBy, list);
		}
	}
	
	/**	Gets the spelling of the LemPos.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the LemPos.
	 */
	 
	public Spelling getGroupingSpelling (int numHits) {
		String str = toString();
		byte charset = lemma == null ? TextParams.ROMAN :
			lemma.getSpelling().getCharset();
		return new Spelling(str, charset);
	}
	
	/**	Gets a string representation of the LemPos.
	 *
	 *	@return		The id.
	 */
	 
	public String toString () {
		if (lemma == null) {
			return pos.toString();
		} else {
			String result = "\u201C" + lemma.getSpelling().getString() + 
				"\u201D";
			if (pos != null) result = result + " (" + pos.getTag() + ")";
			return result;
		}
	}
	
	/**	Exports the object to a MySQL table exporter/importer.
	 *
	 *	@param	exporterImporter	MySQL table exporter/importer.
	 */
	 
	public void export (TableExporterImporter exporterImporter) {
		exporterImporter.print(id);
		String standardSpellingString = null;
		byte standardSpellingCharset = 0;
		if (standardSpelling != null) {
			standardSpellingString = standardSpelling.getString();
			standardSpellingCharset = standardSpelling.getCharset();
		}
		exporterImporter.print(standardSpellingString);
		exporterImporter.print(standardSpellingCharset);
		Long lemmaId = lemma == null ? null : lemma.getId();
		exporterImporter.print(lemmaId);
		Long posId = pos == null ? null : pos.getId();
		exporterImporter.print(posId);
		exporterImporter.println();
	}
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two LemPos objects are equal if their lemmas and 
	 *	parts of speech are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof LemPos)) return false;
		LemPos other = (LemPos)obj;
		return Compare.equals(lemma, other.getLemma()) &&
			Compare.equals(pos, other.getPos());
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return (lemma == null ? 0 : lemma.hashCode()) +
			(pos == null ? 0 : pos.hashCode());
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

