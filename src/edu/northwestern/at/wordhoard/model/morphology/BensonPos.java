package edu.northwestern.at.wordhoard.model.morphology;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	A Benson part of speech.
 *
 *	<p>Benson parts of speech have the following attributes:
 *
 *	<ul>
 *	<li>A tag.
 *	<li>A description.
 *	</ul>
 *
 *	@hibernate.class table="bensonpos"
 */
 
public class BensonPos implements PersistentObject {

	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	The tag. */

	private String tag;
	
	/**	The description. */

	private String description;
	
	/**	Creates a new Benson part of speech.
	 */
	
	public BensonPos () {
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
	 *	@param	description		The description.
	 */
	 
	public void setDescription (String description) {
		this.description = description;
	}
	
	/**	Gets the tag with the description (if available).
	 *
	 *	@return		Tag (description).
	 */
	 
	public String getTagWithDescription () {
		return description == null ? tag :
			(tag + " (" + description + ")");
	}
	
	/**	Gets a string representation of the Benson part of speech.
	 *
	 *	@return		The tag.
	 */
	 
	public String toString () {
		return tag;
	}
	
	/**	Exports the object to a MySQL table exporter/importer.
	 *
	 *	@param	exporterImporter	MySQL table exporter/importer.
	 */
	 
	public void export (TableExporterImporter exporterImporter) {
		exporterImporter.print(id);
		exporterImporter.print(tag);
		exporterImporter.print(description);
		exporterImporter.println();
	}
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two Benson parts of speech are equal if their tags are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof BensonPos)) return false;
		BensonPos other = (BensonPos)obj;
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

