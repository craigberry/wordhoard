package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;

/**	A table cell renderer which formats cell entries using PrintFFormat.
 */

public class PrintfFormatTableCellRenderer extends DefaultTableCellRenderer
{
	/**	Format string for this column.
	 */

	protected PrintfFormat format;

	/**	Create table cell entry renderer.
	 *
	 *	@param	formatString	PrintfFormat string for formatting
	 *							a column.
	 */

	public PrintfFormatTableCellRenderer( String formatString )
	{
		super();

		format	= new PrintfFormat( formatString );
	}

	/**	Get the formatting string.
	 *
	 *	@return		The formatting strings.
	 */

	public PrintfFormat getFormat()
	{
		return format;
	}

	/**	Get a renderer for a table entry.
	 */

	public Component getTableCellRendererComponent
	(
		JTable table ,
		Object value ,
		boolean isSelected ,
		boolean hasFocus ,
		int row ,
		int column
	)
	{
								//	Convert object using the
								//	specified format.

		String formattedValue;

		try
		{
			formattedValue	= StringUtils.trim( format.sprintf( value ) );
		}
		catch ( Exception e )
		{
			formattedValue	= value.toString();
		}
								//	Set a border around the displayed value.

		formattedValue	= StringUtils.dupl( ' ' , 4 ) + formattedValue;

								//	Get the default renderer.

		DefaultTableCellRenderer renderer =
			(DefaultTableCellRenderer)
				super.getTableCellRendererComponent
				(
					table ,
					formattedValue ,
					isSelected ,
					hasFocus ,
					row ,
					column
				);
								//	Set foreground and background
								//	colors based upon whether the
								//	current row is selected or not.
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

		return renderer;
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

