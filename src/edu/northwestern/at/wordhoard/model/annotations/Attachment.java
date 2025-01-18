package edu.northwestern.at.wordhoard.model.annotations;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Attachment interface for annotations.
 *
 */
 
public interface Attachment  {
	
	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 */
	 
	public Long getId ();
	
	/**	Gets the annotation category.
	 *
	 *	@return		The annotation category.
	 *
	 */
	
	public AnnotationCategory getCategory ();
	
	/**	Sets the annotation category.
	 *
	 *	@param	category	The annotation category.
	 */
	 
	public void setCategory (AnnotationCategory category);
		
	/**	Gets the text.
	 *
	 *	@return		The text.
	 *
	 */
	 
	public TextWrapped getText ();
		
	/**	Sets the text.
	 *
	 *	@param	text		The text.
	 */
	 
	public void setText (TextWrapped text);
		
		
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two annotations are equal if their ids are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj);
		
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode ();
	
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

