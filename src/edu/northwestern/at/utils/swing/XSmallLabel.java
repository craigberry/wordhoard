package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

/**	A JLabel with a smaller font size (2 points smaller than the default. */

public class XSmallLabel extends JLabel
{
	/**	Constructs a new small label.
	 *
	 *	@param	text		Label text.
	 */

	public XSmallLabel( String text )
	{
		super( text );

		Font font	= getFont();

		setFont(
			new Font( font.getName() , font.getStyle(), font.getSize() - 2 ) );
	}

	/**	Constructs a new small label.
	 *
	 *	@param	text				Label text.
	 *	@param	icon				Label icon.
	 *	@param	horizontalPosition	Horizontal position of text with respect to icon.
	 */

	public XSmallLabel( String text , Icon icon , int horizontalPosition )
	{
		super( text , icon , horizontalPosition );

		Font font	= getFont();

		setFont( new Font(
			font.getName() , font.getStyle() , font.getSize() - 2 ) );
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

