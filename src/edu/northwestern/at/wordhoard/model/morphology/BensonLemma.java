package edu.northwestern.at.wordhoard.model.morphology;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	A Benson lemma.
 *
 *	<p>Benson lemmas have the following attributes:
 *
 *	<ul>
 *	<li>The word.
 *	<li>The word class.
 *	<li>A homonym number.
 *	<li>A definition.
 *	<li>A comment.
 *	<li>The OED lemma.
 *	</ul>
 *
 *	@hibernate.class table="bensonlemma"
 */
 
public class BensonLemma implements PersistentObject {

	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	The word. */

	private String word;
	
	/**	The word class. */

	private String wordClass;
	
	/**	Homonym. */

	private int homonym;
	
	/**	Definition. */

	private String definition;
	
	/**	Comment. */

	private String comment;
	
	/**	OED lemma. */

	private String oedLemma;
	
	/**	Creates a new Benson lemma.
	 */
	
	public BensonLemma () {
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
	
	/**	Gets the word.
	 *
	 *	@return		The word.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getWord () {
		return word;
	}
	
	/**	Sets the word.
	 *
	 *	@param	word		The word.
	 */
	 
	public void setWord (String word) {
		this.word = word;
	}
	
	/**	Gets the word class.
	 *
	 *	@return		The word class.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getWordClass () {
		return wordClass;
	}
	
	/**	Sets the word class.
	 *
	 *	@param	wordClass		The word class.
	 */
	 
	public void setWordClass (String wordClass) {
		this.wordClass = wordClass;
	}
	
	/**	Gets the homonym.
	 *
	 *	@return		The homonym.
	 *
	 *	@hibernate.property access="field"
	 */
	
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
	
	/**	Gets the definition.
	 *
	 *	@return		The definition, with any trailing "," character removed.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getDefinition () {
		if (definition == null) return null;
		int len = definition.length();
		if (len == 0) return null;
		if (definition.charAt(len-1) == ',') {
			return definition.substring(0, len-1);
		} else {
			return definition;
		}
	}
	
	/**	Sets the definition.
	 *
	 *	@param	definition		The definition.
	 */
	 
	public void setDefinition (String definition) {
		this.definition = definition;
	}
	
	/**	Gets the comment.
	 *
	 *	@return		The comment.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getComment () {
		return comment;
	}
	
	/**	Sets the comment.
	 *
	 *	@param	comment		The comment.
	 */
	 
	public void setComment (String comment) {
		this.comment = comment;
	}
	
	/**	Gets the OED lemma.
	 *
	 *	@return		The OED lemma.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getOedLemma () {
		return oedLemma;
	}
	
	/**	Sets the OED lemma.
	 *
	 *	@param	oedLemma		The OED lemma.
	 */
	 
	public void setOedLemma (String oedLemma) {
		this.oedLemma = oedLemma;
	}
	
	/**	Gets the tag.
	 *
	 *	<p>The tag is: word (wordClass) (homonym)
	 *
	 *	@return		The tag.
	 */
	 
	public String getTag () {
		String tag = word;
		if (wordClass != null) tag = tag + " (" + wordClass + ")";
		if (homonym > 0) tag = tag + " (" + homonym + ")";
		return tag;
	}
	
	/**	Gets a string representation of the Benson lemma.
	 *
	 *	@return		The tag (same as getTag).
	 */
	 
	public String toString () {
		return getTag();
	}
	
	/**	Exports the object to a MySQL table exporter/importer.
	 *
	 *	@param	exporterImporter	MySQL table exporter/importer.
	 */
	 
	public void export (TableExporterImporter exporterImporter) {
		exporterImporter.print(id);
		exporterImporter.print(word);
		exporterImporter.print(wordClass);
		exporterImporter.print(homonym);
		exporterImporter.print(definition);
		exporterImporter.print(comment);
		exporterImporter.print(oedLemma);
		exporterImporter.println();
	}
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two Benson lemmas are equal if their tags are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof BensonLemma)) return false;
		BensonLemma other = (BensonLemma)obj;
		return Compare.equals(getTag(), other.getTag());
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return getTag().hashCode();
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

