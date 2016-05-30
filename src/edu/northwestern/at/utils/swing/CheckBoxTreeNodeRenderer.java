package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/**	Tree cell renderer which displays a checkbox.
 */

public class CheckBoxTreeNodeRenderer extends JCheckBox
	implements TreeCellRenderer
{
	/**	Background color to match that of the tree. */

	protected Color backgroundColor;

	/**	Selected node color to match that of the tree. */

	protected Color selectedColor;

	/**	Foreground color to match that of the tree. */

	protected Color foregroundColor;

    /**	Create a check box node renderer.
     *
     *	@param	tree	The tree to which this renderer is to be attached.
     */

	public CheckBoxTreeNodeRenderer( JTree tree )
	{
		super();

		foregroundColor	= tree.getForeground();
		backgroundColor	= tree.getBackground();
//		selectedColor	= tree.getSelectedColor();
		selectedColor	= tree.getBackground();
	}

	/**	Get a check box tree cell renderer.
	 */

	public Component getTreeCellRendererComponent
	(
		JTree tree ,
		Object value ,
		boolean selected ,
		boolean expanded ,
		boolean leaf ,
		int row ,
		boolean hasFocus
	)
	{
								//	Get the node value.

		CheckBoxTreeNode node	= (CheckBoxTreeNode)value;

								//	Set the display text for this value.

		setText( node.getText() );

								//	Determine if the node is selected or not.

		setSelected( node.isChecked() );

		if ( selected )
			setBackground( selectedColor );
		else
			setBackground( backgroundColor );

		return this;
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

