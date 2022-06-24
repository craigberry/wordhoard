package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

/** Defines an entry for a CheckBoxList. */

public class CheckBoxListItem
{
	/** The item. */

	private Object obj;

	/** True if the item is checked. */

	private boolean checked;

	/** Create a check box list item from an object.
	 *
	 *	@param	obj			List item.
	 *	@param	checked		True if list item checked.
	 */

	public CheckBoxListItem( Object obj , boolean checked )
	{
		this.obj		= obj;
		this.checked	= checked;
	}

	/** Create a check box list item from an object.
	 *
	 *	@param	obj		List item.
	 *
	 *	<p>
	 *	The item is initially set to not checked.
	 *	</p>
	 */

	public CheckBoxListItem( Object obj )
	{
		this.obj		= obj;
		this.checked	= false;
	}

	/** Set checked status of item.
	 *
	 *	@param	checked	True to select item,
	 *						false to unselect.
	 *
	 */

	public void setChecked( boolean checked )
	{
		this.checked	= checked;
	}

	/** Is item checked?
	 *
	 *	@return		True if item checked.
	 */

	public boolean isChecked()
	{
		return checked;
	}

	/** Get object.
	 *
	 *	@return		The item.
	 */

	public Object getObject()
	{
		return obj;
	}

	/** Get object text.
	 *
	 *	@return		The object text.
	 */

	public String getText()
	{
		return obj.toString();
	}

	/** Get item text for display conversions.
	 *
	 *	@return		The item text.
	 */

	public String toString()
	{
		return obj.toString();
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

