package edu.northwestern.at.wordhoard.swing.tcon;

/*	Please see the license information at the end of this file. */

import javax.swing.tree.*;

import edu.northwestern.at.utils.StringUtils;
import edu.northwestern.at.wordhoard.model.*;

/**	A table of contents tree node.
 *
 *	<p>There are three types of nodes in table of contents trees:
 *
 *	<ul>
 *
 *	<li>Category nodes. These are high level nodes used to group works
 *	into categories (e.g., genres). They have titles but no associated
 *	works or work parts.
 *
 *	<li>Work part nodes. These are nodes for works and their parts.
 *
 *	<li>Placeholder nodes. As a performance optimization, we use lazy
 *	loading for work part nodes. If a work part has any children, we
 *	initially create a tree node that has only a single "placeholder"
 *	child. We need to do this to make the expansion control appear for
 *	the node in the tree. When and if the user actually expands the node,
 *	we at that point replace the placeholder node by nodes for the work
 *	part's children. A placehoder node is also used as the root of the tree.
 *
 *	</ul>
 */

public class TreeNode extends DefaultMutableTreeNode {

	/**	A category node. */

	public static final int CATEGORY_NODE = 0;

	/**	A work part node. */

	public static final int WORK_PART_NODE = 1;

	/**	A placeholder node. */

	public static final int PLACEHOLDER_NODE = 2;

	/**	Node type. */

	private int nodeType;

	/**	The title of this node if it is a category node. */

	private String categoryTitle;

	/**	The work part for this node if it is a work part node. */

	private WorkPart workPart;

	/**	True if this node is a loaded work part node. */

	private boolean loaded;

	/**	The parent table of contents panel. */

	private TableOfContentsPanel parentPanel;

	/**	Creates a new category node.
	 *
	 *	@param	categoryTitle	Category title.
	 *
	 *	@param	parentPanel		The parent table of contents panel.
	 */

	public TreeNode (String categoryTitle, TableOfContentsPanel parentPanel) {
		super();
		nodeType = CATEGORY_NODE;
		this.categoryTitle = categoryTitle;
		this.parentPanel = parentPanel;
	}

	/**	Creates a new unloaded work part node.
	 *
	 *	@param	workPart		The work part.
	 *
	 *	@param	parentPanel		The parent table of contents panel.
	 */

	public TreeNode (WorkPart workPart, TableOfContentsPanel parentPanel) {
		nodeType = WORK_PART_NODE;
		this.workPart = workPart;
		this.parentPanel = parentPanel;
		this.loaded = false;
	}

	/**	Creates a new placeholder node.
	 *
	 *	@param	parentPanel		The parent table of contents panel.
	 */

	public TreeNode (TableOfContentsPanel parentPanel) {
		nodeType = PLACEHOLDER_NODE;
		this.parentPanel = parentPanel;
	}

	/**	Gets the node type.
	 *
	 *	@return		The node type.
	 */

	public int getNodeType () {
		return nodeType;
	}

	/**	Gets the work part.
	 *
	 *	@return		The work part, or null if category or placeholder node.
	 */

	public WorkPart getWorkPart () {
		return workPart;
	}

	/**	Returns true if the node is loaded.
	 *
	 *	@return		True if node is loaded.
	 */

	public boolean isLoaded () {
		return loaded;
	}

	/**	Sets the node loaded attribute.
	 *
	 *	@param	loaded		True if the node is loaded.
	 */

	public void setLoaded (boolean loaded) {
		this.loaded = loaded;
	}

	/**	Adds a new category node as a child of this node.
	 *
	 *	@param	categoryTitle	Category title.
	 *
	 *	@return		The new category node.
	 */

	public TreeNode addCategoryNode (String categoryTitle) {
		TreeNode categoryNode = new TreeNode(categoryTitle, parentPanel);
		add(categoryNode);
		return categoryNode;
	}

	/**	Adds an unloaded work part node as a child of this node.
	 *
	 *	@param	node	Work part node.
	 */

	public void addUnloadedWorkPartNode (TreeNode node) {
		add(node);
		if (node.workPart.getHasChildren()) {
			TreeNode placeHolder = new TreeNode(parentPanel);
			node.add(placeHolder);
		}
	}

	/**	Creates and adds a new unloaded work part node as a child of
	 *	this node.
	 *
	 *	@param	workPart	Work part.
	 *
	 *	@return		The new work part node.
	 */

	public TreeNode addUnloadedWorkPartNode (WorkPart workPart) {
		TreeNode workPartNode = new TreeNode(workPart, parentPanel);
		addUnloadedWorkPartNode(workPartNode);
		return workPartNode;
	}

	/**	Returns a string representation of the node.
	 *
	 *	@return		The node title: The category title, the work part
	 *				title, or an empty string for placeholder nodes.
	 *				Work parts may have the work part tag appended if
	 *				requested, and works may have the author(s) and
	 *				publication date appended as well.
	 */

	public String toString () {
		switch (nodeType) {
			case CATEGORY_NODE: return categoryTitle;
			case WORK_PART_NODE:
				String title = null;
				if (workPart instanceof Work) {
					Work work = (Work)workPart;
					if (parentPanel.getDisplayDates()) {
						title = work.getFullTitleWithDate();
					} else {
						title = work.getFullTitle();
					}
					if (parentPanel.getDisplayAuthors()) {
						String authors =
							workPart.getWork().getAuthors().toString();
						title =
							title + " (" +
							StringUtils.removeEnclosingBrackets( authors ) +
							")";
					}
				} else {
					title = workPart.getShortTitle();
				}
				if (parentPanel.getDisplayTags()) {
					title = title + " (" + workPart.getTag() + ")";
				}
				return title;

			case PLACEHOLDER_NODE: return "";
		}
		return null;
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

