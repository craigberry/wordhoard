package edu.northwestern.at.wordhoard.swing.calculator.widgets;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	Renderer for tree nodes containing checkboxed WordCounter objects.
 */

public class ExtendedCheckBoxTreeNodeRenderer
	extends CheckBoxTreeNodeRenderer
{
	/**	True to display short work titles. */

	protected boolean useShortWorkTitles	= true;

	/**	Create ExtendedCheckBoxTreeNodeRenderer.
	 *
	 *	@param	tree				Tree whose nodes we are to render,
	 *	@param	useShortWorkTitles	True to use short work titles.
	 */

	public ExtendedCheckBoxTreeNodeRenderer
	(
		JTree tree ,
		boolean useShortWorkTitles
	)
	{
		super( tree );

		this.useShortWorkTitles	= useShortWorkTitles;
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
								//	Determine if the node is selected or not.

		CheckBoxTreeNode nodeValue  =  (CheckBoxTreeNode)value;

		setSelected( nodeValue.isChecked() );

                                //  Get displayable string.

		Object nodeObject	= nodeValue.getObject();

		if ( nodeObject instanceof CanCountWords )
		{
			WordCounter wordCounter	=
				new WordCounter( (CanCountWords)nodeValue.getObject() );

			setText( wordCounter.toHTMLString( useShortWorkTitles ) );
		}
		else
		{
			setText( nodeObject.toString() );
		}
                                //  Set background color.

		if ( tree.isEnabled() )
		{
			setForeground( foregroundColor );

			if ( selected )
			{
				setBackground( selectedColor );
			}
			else
			{
				setBackground( backgroundColor );
			}
		}
		else
		{
			setForeground( Color.LIGHT_GRAY );
		}

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


