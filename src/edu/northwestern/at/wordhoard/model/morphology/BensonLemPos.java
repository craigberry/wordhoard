package edu.northwestern.at.wordhoard.model.morphology;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	A Benson word form (lemma and part of speech).
 *
 *	<p>Benson word forms have the following attributes:
 *
 *	<ul>
 *	<li>The {@link edu.northwestern.at.wordhoard.model.morphology.BensonLemma
 *		Benson lemma}.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.morphology.BensonPos
 *		Benson part of speech}.
 *	</ul>
 *
 *	@hibernate.class table="bensonlempos"
 */
 
public class BensonLemPos implements PersistentObject {

	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	Benson lemma. */

	private BensonLemma lemma;
	
	/**	Benson part of speech. */

	private BensonPos pos;
	
	/**	Creates a new BensonLemPos object.
	 */
	
	public BensonLemPos () {
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
	
	/**	Gets the lemma.
	 *
	 *	@return		The Lemma.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="lemma_index"
	 */
	
	public BensonLemma getLemma () {
		return lemma;
	}
	
	/**	Sets the lemma.
	 *
	 *	@param	lemma		The lemma.
	 */
	 
	public void setLemma (BensonLemma lemma) {
		this.lemma = lemma;
	}
	
	/**	Gets the part of speech.
	 *
	 *	@return		The part of speech.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="pos_index"
	 */
	
	public BensonPos getPos () {
		return pos;
	}
	
	/**	Sets the part of speech.
	 *
	 *	@param	pos		The part of speech.
	 */
	 
	public void setPos (BensonPos pos) {
		this.pos = pos;
	}
	
	/**	Gets a string representation of the BensonLemPos.
	 *
	 *	@return		The id.
	 */
	 
	public String toString () {
		return id.toString();
	}
	
	/**	Exports the object to a MySQL table exporter/importer.
	 *
	 *	@param	exporterImporter	MySQL table exporter/importer.
	 */
	 
	public void export (TableExporterImporter exporterImporter) {
		exporterImporter.print(id);
		Long lemmaId = lemma == null ? null : lemma.getId();
		exporterImporter.print(lemmaId);
		Long posId = pos == null ? null : pos.getId();
		exporterImporter.print(posId);
		exporterImporter.println();
	}
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two BensonLemPos objects are equal if their lemmas and their
	 *	parts of seppech are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof BensonLemPos)) return false;
		BensonLemPos other = (BensonLemPos)obj;
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

