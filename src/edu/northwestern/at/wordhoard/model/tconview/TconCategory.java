package edu.northwestern.at.wordhoard.model.tconview;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

/**	A corpus table of contents category.
 *
 *	<p>A category is used to group together works in a table of contents
 *	view for a corpus. For example, in the Shakespeare "By Genre" view, 
 *	"Comedies" is a category.
 *
 *	<p>Each category has a title and an ordered list of work tags for the
 *	works in the category.
 *
 *	@hibernate.class table="tconcategory"
 */
 
public class TconCategory {

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	The category title. */
	
	private String title;
	
	/**	List of work tags for the works in the category. */
	
	private List workTags = new ArrayList();

	/**	Creates a new table of contents category.
	 */

	public TconCategory () {
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
	
	/**	Gets the category title.
	 *
	 *	@return		The category title.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	public String getTitle () {
		return title;
	}
	
	/**	Sets the category title.
	 *
	 *	@param	title	The category title.
	 */
	 
	public void setTitle (String title) {
		this.title = title;
	}

	/**	Gets the list of work tags.
	 *
	 *	@return			The list of work tags as an unmodifiable list.
	 *
	 *	@hibernate.list access="field" lazy="true" table="tconcategory_worktags"
	 *	@hibernate.collection-key column="tconcategory"
	 *	@hibernate.collection-index column="tconcategory_index"
	 *	@hibernate.collection-element type="java.lang.String" length="32"
	 *		column="worktag"
	 */

	public List getWorkTags () {
		return Collections.unmodifiableList(workTags);
	}
	
	/**	Adds a work tag.
	 *
	 *	<p>The new work tag is added to the end of the ordered list of
	 *	work tags.
	 *
	 *	@param	workTag		Work tag.
	 */
	 
	public void addWorkTag (String workTag) {
		workTags.add(workTag);
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

