package edu.northwestern.at.wordhoard.swing.calculator.widgets;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.WordUtils;

/**	A table cell renderer for words which may contain word class tags.
 */

public class WordTableCellRenderer
	extends DefaultTableCellRenderer
{
	/**	True to strip word class tags before displaying word(s).
	 */

	protected boolean stripWordClasses		= true;

	/**	True to strip spellings before displaying word(s).
	 */

	protected boolean stripSpellings		= false;

	/**	Position within table cell for word text.
	 */

	protected int wordTextPosition			= SwingConstants.TRAILING;

	/**	Create word table cell entry renderer.
	 *
	 *	@param	wordTextPosition		Position of word text within table cell.
	 *	@param	stripSpellings			True to strip spellings.
	 *	@param	stripWordClasses		True to strip word class tags.
	 */

	public WordTableCellRenderer
	(
		int wordTextPosition ,
		boolean stripSpellings ,
		boolean stripWordClasses
	)
	{
		this.wordTextPosition	= wordTextPosition;
		this.stripSpellings		= stripSpellings;
		this.stripWordClasses	= stripWordClasses;
	}

	/**	Create word table cell entry renderer.
	 *
	 *	@param	stripSpellings			True to strip spellings.
	 *	@param	stripWordClasses		True to strip word class tags.
	 */

	public WordTableCellRenderer
	(
		boolean stripSpellings ,
		boolean stripWordClasses
	)
	{
		this.stripSpellings		= stripSpellings;
		this.stripWordClasses	= stripWordClasses;
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
								//	Get word text.

		Object formattedValue	= value;

								//	Strip word class tag, if any,
								//	if requested.

		if ( value instanceof String )
		{
			if ( stripWordClasses )
			{
				formattedValue	=
					(Object)WordUtils.stripWordClass( (String)value );
			}
			else if ( stripSpellings )
			{
				formattedValue	=
					(Object)WordUtils.stripSpelling( (String)value );
			}
		}
								//	Set word text position in cell table.

		setHorizontalAlignment( wordTextPosition );

								//	Add some space on the right.

		formattedValue	= formattedValue + StringUtils.dupl( ' ' , 4 );

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


