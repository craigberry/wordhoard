package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

/**	A JLabel with a bold font. */

public class XBoldLabel extends JLabel
{
	/**	The text font. */

	private static final Font FONT = new Font(Fonts.serif, Font.BOLD, 14);

	/**	Constructs a new bold text label.
	 *
	 *	@param	text		Label text.
	 */

	public XBoldLabel( String text )
	{
		super( text );

		setFont( FONT );
	}

	/**	Constructs a new bold text label.
	 *
	 *	@param	text				Label text.
	 *	@param	horizontalPosition	Horizontal position of text with respect to icon.
	 */

	public XBoldLabel( String text, int horizontalPosition )
	{
		this( text, null, horizontalPosition );
	}

	/**	Constructs a new bold text label.
	 *
	 *	@param	text				Label text.
	 *	@param	icon				Label icon.
	 *	@param	horizontalPosition	horizontal position of text with respect to icon.
	 */

	public XBoldLabel( String text, Icon icon, int horizontalPosition )
	{
		super( text, icon, horizontalPosition );

		setFont( FONT );
	}

	/** Get string width of current label text with current font setting.
	 *
	 *	@return		String width of current label text.
	 */

	public int getStringWidth()
	{
		FontMetrics metrics	= getFontMetrics( getFont() );
		return metrics.stringWidth( getText() );
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

