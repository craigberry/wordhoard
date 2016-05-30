package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.tree.*;
import java.util.*;

/**	Swing tree utilities. */

public class TreeUtils {

	/**	Prunes a subtree.
	 *
	 *	<p>Each element of the intersection of the subtree and the
	 *	node set which is not a descendant of some other element of
	 *	the intersection is added to the result list.
	 *
	 *	@param	model		The tree model.
	 *
	 *	@param	node		The root of the subtree in the model to prune.
	 *
	 *	@param	nodeSet		Set of nodes to prune.
	 *
	 *	@param	result		List of prunced nodes.
	 */

	private static void pruneSubtree (TreeModel model, Object node,
		HashSet nodeSet, ArrayList result)
	{
		for (int i = 0; i < model.getChildCount(node); i++) {
			Object child = model.getChild(node, i);
			if (nodeSet.contains(child)) {
				result.add(child);
			} else {
				pruneSubtree(model, child, nodeSet, result);
			}
		}
	}

	/**	Prunes descendants from a tree path list.
	 *
	 *	<p>Any elements of the list which are descendants of other
	 *	elements of the list are removed.
	 *
	 *	@param	model			A tree model.
	 *
	 *	@param	treePathList	An array of tree paths in the model.
	 *
	 *	@return					A list of the nodes for the pruned list
	 *							(the last path components of the tree paths
	 *							in the pruned list).
	 */

	public static ArrayList pruneDescendantsFromTreePathList (
		TreeModel model, TreePath[] treePathList)
	{
		HashSet nodeSet = new HashSet();
		for (int i = 0; i < treePathList.length; i++)
			nodeSet.add(treePathList[i].getLastPathComponent());
		ArrayList result = new ArrayList();
		pruneSubtree(model, model.getRoot(), nodeSet, result);
		return result;
	}

	/**	Compares two trees.
	 *
	 *	@param		node1		Root of first tree.
	 *
	 *	@param		node2		Root of second tree.
	 *
	 *	@return					True if trees are equal.
	 */

	public static boolean compare (TreeNode node1, TreeNode node2) {
		if (!node1.equals(node2)) return false;
		int numChildren1 = node1.getChildCount();
		int numChildren2 = node2.getChildCount();
		if (numChildren1 != numChildren2) return false;
		for (int i = 0; i < numChildren1; i++) {
			TreeNode child1 = node1.getChildAt(i);
			TreeNode child2 = node2.getChildAt(i);
			if (!compare(child1, child2)) return false;
		}
		return true;
	}

	/**	Gets all the nodes in a tree.
	 *
	 *	@param	node			The root of the tree.
	 *
	 *	@return					A collection of all the tree's nodes.
	 */

	public static Collection getNodes (TreeNode node) {
		HashSet result = new HashSet();
		result.add(node);
		int numChildren = node.getChildCount();
		for (int i = 0; i < numChildren; i++) {
			TreeNode child = node.getChildAt(i);
			Collection childNodes = getNodes(child);
			result.addAll(childNodes);
		}
		return result;
	}

	/**	Dumps a tree to stdout for debugging.
	 *
	 *	@param	node		Root of tree.
	 *
	 *	@param	level		Indentation level.
	 */

	public static void dumpTree (TreeNode node, int level) {
		for (int i = 0; i < level; i++) System.out.print(" ");
		System.out.println(node);
		int numChildren = node.getChildCount();
		for (int i = 0; i < numChildren; i++)
			dumpTree(node.getChildAt(i), level+1);
	}

	/**	Hides the default no-arg constructor. */

	private TreeUtils () {
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

