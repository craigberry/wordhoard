package edu.northwestern.at.wordhoard.model.annotations;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


/**	An annotation.
 *
 *	<p>An annotation has the following attributes:
 *
 *	<ul>
 *	<li>A unique persistence id.
 *	<li>The annotation's 
 *		{@link edu.northwestern.at.wordhoard.model.annotations.AnnotationCategory
 *		category}.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.wrappers.TextWrapper
 *		text} of the annotation.
 *	</ul>
 *
 *	@hibernate.class table="annotation" discriminator-value=""
 *	@hibernate.discriminator column="type" type="string"
 */
 

@Entity
@Table(name="annotation")
@DiscriminatorValue("")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 255)
public class Annotation implements Attachment, PersistentObject {

	/**	Unique persistence id (primary key). */
	
	protected Long id;
	
	/**	The annotation category. */
	
	protected AnnotationCategory category;
	
	/**	The text. */
	
	protected TextWrapper text;
	
	/**	Creates a new annotation.
	 */
	
	public Annotation () {
	}
	
	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="native"
	 */

	@Access(AccessType.FIELD)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId () {
		return id;
	}
	
	/**	Gets the annotation category.
	 *
	 *	@return		The annotation category.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="category_index"
	 */
	
	@Access(AccessType.FIELD)
	@ManyToOne
	@JoinColumn(name = "category", foreignKey = @ForeignKey(name = "category_index"))
	public AnnotationCategory getCategory () {
		return category;
	}
	
	/**	Sets the annotation category.
	 *
	 *	@param	category	The annotation category.
	 */
	 
	public void setCategory (AnnotationCategory category) {
		this.category = category;
	}
	
	/**	Gets the text.
	 *
	 *	@return		The text.
	 *
	 *	@hibernate.many-to-one access="field" 
	 *		class="edu.northwestern.at.wordhoard.model.wrappers.TextWrapper"
	 *		foreign-key="text_index"
	 */
	 
	@Access(AccessType.FIELD)
	@Embedded
	@ManyToOne(targetEntity = TextWrapper.class)
	@JoinColumn(name = "text", foreignKey = @ForeignKey(name = "text_index"))
	public TextWrapped getText () {
		return text;
	}
	
	/**	Sets the text.
	 *
	 *	@param	text		The text.
	 */
	 
	public void setText (TextWrapped text) {
		this.text = (TextWrapper) text;
	}
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two annotations are equal if their ids are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Annotation)) return false;
		Annotation other = (Annotation)obj;
		return id.equals(other.getId());
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return id.hashCode();
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

