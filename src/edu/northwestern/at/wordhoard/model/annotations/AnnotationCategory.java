package edu.northwestern.at.wordhoard.model.annotations;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.utils.*;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**	An annotation category.
 *
 *	<p>An annotation category has the following attribute:
 *
 *	<ul>
 *	<li>A unique persistence id.
 *	<li>The name of the category.
 *	</ul>
 *
 *	@hibernate.class table="annotationcategory"
 */
 
@Entity
@Table(name="annotationcategory")
public class AnnotationCategory implements PersistentObject {

	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	The name. */
	
	private String name;
	
	/**	Creates a new annotation category.
	 */
	
	public AnnotationCategory () {
	}
	
	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="native"
	 */
	 
	@Access(AccessType.FIELD)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	public Long getId () {
		return id;
	}
	
	/**	Gets the name.
	 *
	 *	@return		The name.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	@Access(AccessType.FIELD)
	public String getName () {
		return name;
	}
	
	/**	Sets the name.
	 *
	 *	@param	name		The name.
	 */
	 
	public void setName (String name) {
		this.name = name;
	}
	
	/**	Gets a string representation of the category.
	 *
	 *	@return			The name.
	 */
	 
	public String toString () {
		return name;
	}
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two categories are equal if their names are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof AnnotationCategory)) return false;
		AnnotationCategory other = (AnnotationCategory)obj;
		return Compare.equals(name, other.getName());
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return name.hashCode();
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

