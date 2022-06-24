package edu.northwestern.at.wordhoard.model.tconview;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	A corpus table of contents view.
 *
 *	<p>Each corpus has one or more table of contents views. There are five
 *	types of views:
 *
 *	<ul>
 *	<li>A list of works in alphabetical order by work tag. This is the
 *		default view if no other views are defined.
 *	<li>A list of works in increasing order by publication year.
 *	<li>A list of works in a specified order defined by the corpus
 *		implementor.
 *	<li>An ordered list of categories, each of which contains an ordered
 *		list of works, specified by the corpus implementor. For example,
 *		the "By Genre" view of the Shakespeare table of contents is this
 *		type of view.
 *	<li>Author categories in alphabetical order by author name, each containing
 *		the works by the author in order by publication year.
 *	</ul>
 *
 *	<p>Each table of contents view has an optional label for the radio button
 *	in the table of contents window. Radio buttons are used and labels should
 *	be specified if a corpus has more than one table of contents view.
 *
 *	@hibernate.class table="tconview"
 */
 
public class TconView {

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	A list of works in alphabetical order by tag (the default view). */

	public static final int LIST_TAG_VIEW_TYPE = 0;
	
	/**	A list of works in increasing order by publication year. */
	
	public static final int LIST_PUB_YEAR_VIEW_TYPE = 1;
	
	/**	A list of works in a specified order. */
	
	public static final int LIST_VIEW_TYPE = 2;
	
	/**	A list of categories, each of which contains a list of works. */
	
	public static final int CATEGORY_VIEW_TYPE = 3;
	
	/**	By author, then by pub year. */
	
	public static final int BY_AUTHOR_VIEW_TYPE = 4;
	
	/**	The type of the view. */
	
	private int viewType;

	/**	Radio button label, or null if none. */
	
	private String radioButtonLabel;
	
	/**	List of work tags if the view type is LIST_VIEW_TYPE, else an
		empty list. */
	
	private List workTags = new ArrayList();
	
	/**	List of categories if the view type is CATEGORY_VIEW_TYPE, else an
		empty list. */
	
	private List categories = new ArrayList();

	/**	Creates a new table of contents view.
	 */

	public TconView () {
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
	
	/**	Gets the type of the view.
	 *
	 *	@return		The type of the view.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	public int getViewType () {
		return viewType;
	}
	
	/**	Sets the type of the view. 
	 *
	 *	@param	viewType	The type of the view.
	 */
	 
	public void setViewType (int viewType) {
		this.viewType = viewType;
	}
	
	/**	Gets the radio button label.
	 *
	 *	@return		The radio button label, or null if none.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	public String getRadioButtonLabel () {
		return radioButtonLabel;
	}
	
	/**	Sets the radio button label.
	 *
	 *	@param	radioButtonLabel	The radio button label, or null if none.
	 */
	 
	public void setRadioButtonLabel (String radioButtonLabel) {
		this.radioButtonLabel = radioButtonLabel;
	}

	/**	Gets the list of work tags.
	 *
	 *	@return			The list of work tags as an unmodifiable list.
	 *					The list is empty if the view type is not 
	 *					LIST_VIEW_TYPE.
	 *
	 *	@hibernate.list access="field" lazy="true" table="tconview_worktags"
	 *	@hibernate.collection-key column="tconview"
	 *	@hibernate.collection-index column="tconview_index"
	 *	@hibernate.collection-element type="java.lang.String" length="32"
	 *		column="worktag"
	 */

	public List getWorkTags () {
		return Collections.unmodifiableList(workTags);
	}
	
	/**	Adds a work tag.
	 *
	 *	<p>The new work tag is added to the ned of the ordered list of
	 *	work tags.
	 *
	 *	@param	workTag		Work tag.
	 */
	 
	public void addWorkTag (String workTag) {
		workTags.add(workTag);
	}

	/**	Gets the list of categories.
	 *
	 *	@return			The list of categories as an unmodifiable list.
	 *					The list is empty if the view type is not
	 *					CATEGORY_VIEW_TYPE.
	 *
	 *	@hibernate.list access="field" lazy="true" table="tconview_categories"
	 *	@hibernate.collection-key column="tconview"
	 *	@hibernate.collection-index column="tconview_index"
	 *	@hibernate.collection-many-to-many column="category"
	 *		class="edu.northwestern.at.wordhoard.model.tconview.TconCategory"
	 */

	public List getCategories () {
		return Collections.unmodifiableList(categories);
	}
	
	/**	Adds a category.
	 *
	 *	<p>The new category is added to the end of the ordered list of
	 *	categories.
	 *
	 *	@param	category		Category.
	 */
	 
	public void addCategory (TconCategory category) {
		categories.add(category);
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

