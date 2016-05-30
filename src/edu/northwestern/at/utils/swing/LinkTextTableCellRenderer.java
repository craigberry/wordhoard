package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.rmi.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**	Table cell renderer class for text with support for underlined link.
 *
 *	<p>
 *	Table cell renderer class with options for the horizontal and vertical
 *	alignment of the component LinkLabel and an option to render the label
 *	using the link style (blue and underlined).
 *	</p>
 */

public class LinkTextTableCellRenderer extends LinkLabel
	implements TableCellRenderer
{
	/** Create renderer.
	 *
	 *	@param	horizontalAlignment		Horizontal alignment for text.
	 *	@param	verticalAlignment		Vertical alignment for text.
	 *	@param	link					The associate link or null if none.
	 */

	public LinkTextTableCellRenderer
	(
		int horizontalAlignment,
		int verticalAlignment,
		Link link
	)
	{
		setHorizontalAlignment( horizontalAlignment );
		setVerticalAlignment( verticalAlignment );
		setLink( link );
	}

	/** Return renderer. */

	public Component getTableCellRendererComponent
	(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column
	)
	{
		if ( isSelected )
		{
			setForeground( table.getSelectionForeground() );
			setBackground( table.getSelectionBackground() );

			if ( getLink() != null ) setForeground( Color.white );
		}
		else
		{
			setForeground( table.getForeground() );
			setBackground( table.getBackground() );

			if ( getLink() != null ) setForeground( Color.blue );
		}

		setOpaque( true );

		if ( value == null )
		{
			setText( "" );
		}
		else
		{
			setText( value.toString() );
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

