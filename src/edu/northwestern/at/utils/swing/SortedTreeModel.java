package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;

/**	Sorted tree model.
 *
 *	<p>This class extends DefaultTreeModel to support sorted trees.
 */

public class SortedTreeModel extends DefaultTreeModel {

	/**	Creates a tree in which any node can have children.
	 *
	 *	@param	root	The root of the tree.
	 */

	public SortedTreeModel (SortedTreeNode root) {
		super(root);
	}

	/**	Creates a tree specifying whether any node can have children, or
	 *	whether only certain nodes can have children.
	 *
	 *	@param	root				The root of the tree.
	 *
	 *	@param	asksAllowsChildren	See Sun's javadoc.
	 */

	public SortedTreeModel (SortedTreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
	}

	/**	Adds a new child node, in the proper sorted position.
	 *
	 *	@param	newChild	The new child node.
	 *
	 *	@param	parent		The parent node.
	 */

	public void insertNodeInto (SortedTreeNode newChild,
		SortedTreeNode parent)
	{
		int i = parent.add(newChild);
		int[] childIndices = new int[] {i};
		nodesWereInserted(parent, childIndices);
	}

	/**	Gets the node count.
	 *
	 *	@return		The total number of nodes in the tree, not counting
	 *				the root node.
	 */

	public int getNodeCount () {
		SortedTreeNode root = (SortedTreeNode)getRoot();
		return root.getNodeCount();
	}

	/**	Prohibits attempts to add children at specific locations.
	 *
	 *	@throws	UnsupportedOperationException
	 */

	public void insertNodeInto (MutableTreeNode newChild,
		MutableTreeNode parent, int index)
	{
		throw new UnsupportedOperationException();
	}

	/**	Prohibits attempts to set a new node value.
	 *
	 *	@throws UnsupportedOperationException
	 */

	public void valueForPathChanged (TreePath path, Object newValue) {
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

