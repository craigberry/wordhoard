package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.tree.*;

/**	Sorted tree node.
 *
 *	<p>This class extends DefaultMutableTreeNode to support sorted tree
 *	nodes.
 *
 *	<p>The class is abstract. You must subclass it and implement the
 *	compareTo method to compare nodes for sorting.
 */

abstract public class SortedTreeNode extends DefaultMutableTreeNode {

	/**	Compares this node to some other node.
	 *
	 *	@param	otherNode		The other node.
	 *
	 *	@return					&lt; 0 if this &lt; other, 0 if this = other,
	 *							&gt; 0 if this &gt; other.
	 */

	abstract public int compareTo (SortedTreeNode otherNode);

	/**	Adds a new child node, in the proper sorted position.
	 *
	 *	@param	newChild	The new child node.
	 *
	 *	@return				The index in the parent node where the
	 *						new child was inserted.
	 */

	public int add (SortedTreeNode newChild) {
		int i = 0;
		int j = getChildCount() - 1;
		while (i <= j) {
			int k = (i+j)/2;
			SortedTreeNode middleChild = (SortedTreeNode)getChildAt(k);
			int c = newChild.compareTo(middleChild);
			if (c < 0) {
				j = k-1;
			} else if (c > 0) {
				i = k+1;
			} else {
				return -1;
			}
		}
		super.insert(newChild, i);
		return i;
	}

	/**	Removes a child node.
	 *
	 *	@param	aChild		The child node.
	 */

	public void remove (SortedTreeNode aChild) {
		int i = 0;
		int j = getChildCount() - 1;
		while (i <= j) {
			int k = (i+j)/2;
			SortedTreeNode middleChild = (SortedTreeNode)getChildAt(k);
			int c = aChild.compareTo(middleChild);
			if (c < 0) {
				j = k-1;
			} else if (c > 0) {
				i = k+1;
			} else {
				remove(k);
				return;
			}
		}
		return;
	}

	/**	Gets the node count.
	 *
	 *	@return		The total number of nodes in the subtree rooted
	 *				at this node, not counting the node itself.
	 */

	public int getNodeCount () {
		int result = 0;
		for (int i = 0; i < getChildCount(); i++) {
			SortedTreeNode child = (SortedTreeNode)getChildAt(i);
			result += 1 + child.getNodeCount();
		}
		return result;
	}

	/**	Prohibits attempts to add nodes that aren't sortable.
	 *
	 *	@throws	UnsupportedOperationException	unsupported operation.
	 */

	public void add (MutableTreeNode newChild) {
		throw new UnsupportedOperationException();
	}

	/**	Prohibits attempts to remove nodes that aren't sortable.
	 *
	 *	@throws	UnsupportedOperationException	unsupported operation.
	 */

	public void remove (MutableTreeNode aChild) {
		throw new UnsupportedOperationException();
	}

	/**	Prohibits attempts to add children at specific locations.
	 *
	 *	@throws	UnsupportedOperationException	unsupported operation.
	 */

	public void insert (MutableTreeNode newChild, int childIndex) {
		throw new UnsupportedOperationException();
	}

	/**	Prohibits attempts to set a new user object.
	 *
	 *	@throws UnsupportedOperationException	unsupported operation.
	 */

	public void setUserObject (Object userObject) {
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

