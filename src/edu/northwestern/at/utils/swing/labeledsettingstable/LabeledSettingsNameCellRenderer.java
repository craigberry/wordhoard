package edu.northwestern.at.utils.swing.labeledsettingstable;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.rmi.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	Table cell renderer class for the settings label.
 *
 *	<p>
 *	The setting name is rendered in bold label text, right-adjusted
 *	in the table cell.
 *	</p>
 */

public class LabeledSettingsNameCellRenderer
	extends XBoldLabel
	implements TableCellRenderer
{
	public LabeledSettingsNameCellRenderer()
	{
		this( null );
	}

	public LabeledSettingsNameCellRenderer( Font font )
	{
		super( "" );

		setHorizontalAlignment( JLabel.RIGHT );
		setVerticalAlignment( JLabel.TOP );

		setBorder( BorderFactory.createEmptyBorder( 1 , 1 , 1 , 1 ) );

		if ( font != null )
		{
			setFont( font );
		}
	}

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
		setOpaque( true );
								// Set foreground and background
								// colors based upon whether the
								// current row is selected or not.
		if ( isSelected )
		{
			setForeground( table.getSelectionForeground() );
			setBackground( table.getSelectionBackground() );
		}
		else
		{
			setForeground( table.getForeground() );
			setBackground( table.getBackground() );
		}
								//	Set text for display.

		setText( (String)value );

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

