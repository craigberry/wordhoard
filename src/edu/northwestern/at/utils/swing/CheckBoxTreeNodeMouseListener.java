package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.util.*;

/**	Handles mouse clicks for tree containing CheckBoxTreeNode objects.
 */

public class CheckBoxTreeNodeMouseListener extends MouseAdapter
{
	public void mousePressed( MouseEvent e )
	{
								//	Get the tree.

		JTree tree = (JTree)e.getSource();

								//	Get the node.

		CheckBoxTreeNode node	=
			(CheckBoxTreeNode)tree.getLastSelectedPathComponent();

								//	Set checked status of this node and
								//	its children.

		if	( node != null )
		{
								//	If the mouse was pressed outside
								//	the checkbox or its label, the
								//	row will be -1.

			if ( tree.getRowForLocation( e.getX() , e.getY() ) >= 0 )
			{
								//	Select or unselect this node
								//	and its children.

				boolean nodeChecked	= !node.isChecked();

				selectChildNodes( node , nodeChecked );

								//	If this node is being unselected,
								//	make sure its parent(s) are also
								//	unselected as necessary.

				TreePath parentPath	=
					tree.getSelectionPath().getParentPath();

				unselectParentNodes( parentPath , nodeChecked );

								//	Eat this mouse event.

				e.consume();
								//	Revalidate and repaint the tree so
								//	the selection changes appear properly.

				tree.revalidate();
				tree.repaint();
			}
		}
	}

	/**	Select or unselect a node and all of its children.
	 *
	 *	@param	node		The node.
	 *	@param	isSelected	True if node selected (checked), else false.
	 */

	protected void selectChildNodes
	(
		CheckBoxTreeNode node ,
		boolean isSelected
	)
	{
								//	Set state of current node.

		node.setChecked( isSelected );

								//	Get child nodes.

        Enumeration children	= node.children();

								//	If there are any, set the selection
								//	(checked) status to that of the parent
								//	node.

		while ( children.hasMoreElements() )
		{
			CheckBoxTreeNode childNode	=
				(CheckBoxTreeNode)children.nextElement();

			selectChildNodes( childNode , isSelected );
		}
	}

	/**	Unselect parents of a node.
	 *
	 *	@param	parentPath	The parent path.
	 *	@param	isSelected	True if node selected (checked), else false.
	 *
	 *	<p>
	 *	If a node is being unselected, make sure the parent node(s), if any,
	 *	are also unselected.
	 *	</p>
	 */

	protected void unselectParentNodes
	(
		TreePath parentPath ,
		boolean isSelected
	)
	{
		if ( !isSelected )
		{
			while ( parentPath != null )
			{
				CheckBoxTreeNode parentNode	=
					(CheckBoxTreeNode)parentPath.getLastPathComponent();

				parentNode.setChecked( isSelected );

				parentPath	= parentPath.getParentPath();
			}
		}
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

