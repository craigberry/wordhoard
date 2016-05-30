package edu.northwestern.at.wordhoard.swing.calculator.widgets;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.WordUtils;
import edu.northwestern.at.wordhoard.swing.*;

/**	A table cell renderer which displays a plus or minus for a number.
 */

public class PlusOrMinusTableCellRenderer
	extends DefaultTableCellRenderer
{
	/**	True to use color coding for +/- entries.
	 *	Green is used for +.
	 *	Red is used for -.
	 */

	protected boolean useColorCoding	= false;

	/**	Create plus or minus table cell entry renderer.
	 *
	 *	@param	useColorCoding	Use color coding (green for plus,
	 *							red for minus)
	 */

	public PlusOrMinusTableCellRenderer( boolean useColorCoding )
	{
		super();

		this.useColorCoding	= useColorCoding;
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
								//	Get object value.

		Object formattedValue	= value;

								//	If not a number, just pass
								//	it through to the default
								//	renderer.  Otherwise, set the
								//	result to a "+" if the number
								//	is positive, " " if the number
								//	is zero, and "-" if the number
								//	is positive.

		if ( formattedValue	instanceof Number )
		{
			double doubleValue	= ((Number)formattedValue).doubleValue();

			if ( doubleValue > 0.0D )
			{
				if ( useColorCoding )
				{
					formattedValue	=
						new String(
							"<html><strong><font color=\"green\">&nbsp;+" +
							"</font></strong></html>" );
				}
				else
				{
					formattedValue	= "  +";
				}
			}
			else if ( doubleValue < 0.0D )
			{
				if ( useColorCoding )
				{
					formattedValue	=
						new String(
							"<html><strong><font color=\"red\">&nbsp;-" +
							"</font></strong></html>" );
				}
				else
				{
					formattedValue	= "  -";
				}
			}
			else
			{
				formattedValue	= new String( " " );
			}
		}
								//	Create the table cell renderer
								//	for displaying the log-likelihood value.

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

