package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.util.*;

/**	Sorted list model.
 *
 *	<p>This class extends DefaultListModel to support sorted lists.
 *
 *	<p>All elements must implement the Comparable interface.
 */

public class SortedListModel extends DefaultListModel {

	/**	Constructs a new empty sorted list model. */

	public SortedListModel () {
		super();
	}

	/**	Adds an element to the list.
	 *
	 *	<p>The element must be comparable. It is added in the correct
	 *	sorted position in the list. If the element or one equal to it is
	 *	already in the list it is ignored.
	 *
	 *	@param	obj		The element to be added to the list.
	 *
	 *	@return			The index in the list of the added element, or
	 *					-1 if it was not added.
	 */

	public int add (Object obj) {
		Comparable element = (Comparable)obj;
		int i = 0;
		int j = size()-1;
		while (i <= j) {
			int k = (i+j)/2;
			int c = element.compareTo(get(k));
			if (c < 0) {
				j = k-1;
			} else if (c > 0) {
				i = k+1;
			} else {
				return -1;
			}
		}
		super.add(i, element);
		return i;
	}

	/**	Removes an element from the list.
	 *
	 *	@param	obj		The element to be removed from the list.
	 *
	 *	@return			True if the element was found and removed,
	 *					false if it was not found.
	 */

	public boolean removeElement (Object obj) {
		Comparable element = (Comparable)obj;
		int i = 0;
		int j = size()-1;
		while (i <= j) {
			int k = (i+j)/2;
			int c = element.compareTo(get(k));
			if (c < 0) {
				j = k-1;
			} else if (c > 0) {
				i = k+1;
			} else {
				removeElementAt(k);
				return true;
			}
		}
		return false;
	}

	/**	Removes an element from the list.
	 *
	 *	<p>This method is the same as {@link #removeElement removeElement}.
	 *
	 *	@param	obj		The element to be removed from the list.
	 *
	 *	@return			True if the element was found and removed,
	 *					false if it was not found.
	 */

	public boolean remove (Object obj) {
		return removeElement(obj);
	}

	/**	Sets new list data.
	 *
	 *	<p>The list is sorted. The sorted list replaces the previous
	 *	list data.
	 *
	 *	@param	list		The new list data.
	 */

	public void setData (List list) {
		Collections.sort(list);
		clear();
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			super.addElement(it.next());
		}
	}

	/**	Redraws an element after a change.
	 *
	 *	@param	index		Element index.
	 */

	public void redraw (int index) {
		fireContentsChanged(this, index, index);
	}

	/**	Prohibits attempts to add elements at the end of the list.
	 *
	 *	@throws	UnsupportedOperationException	unsupported operation.
	 */

	public void addElement (Object element) {
		throw new UnsupportedOperationException();
	}

	/**	Prohibits attempts to add elements at specific locations.
	 *
	 *	@throws	UnsupportedOperationException	unsupported operation.
	 */

	public void add (int index, Object element) {
		throw new UnsupportedOperationException();
	}

	/**	Prohibits attempts to add elements at specific locations.
	 *
	 *	@throws	UnsupportedOperationException	unsupported operation.
	 */

	public void insertElementAt (Object obj, int index) {
		throw new UnsupportedOperationException();
	}

	/**	Prohibits attempts to set an element.
	 *
	 *	@throws	UnsupportedOperationException	unsupported operation.
	 */

	public Object set (int index, Object element) {
		throw new UnsupportedOperationException();
	}

	/**	Prohibits attempts to set an element.
	 *
	 *	@throws	UnsupportedOperationException	unsupported operation.
	 */

	public void setElementAt (Object obj, int index) {
		throw new UnsupportedOperationException();
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

