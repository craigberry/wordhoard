package edu.northwestern.at.utils.swing.icons;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

/**	StarIcons.
 *
 *	<p>Star Icons are icons showing five stars partially or fully
 *	filled in.  Star Icons reside in the "resources/staricons"
 *	subdirectory of the package directory.
 *	</p>
 *
 *	<p>This static class manages and encapsulates access to the star icons.</p>
 */

public class StarIcons
{
	/* Holds star icons. */

	private static SmallIcons starIcons =
		new SmallIcons
		(
			new String[]
			{
				"stars-0-0",
				"stars-0-5",
				"stars-1-0",
				"stars-1-5",
				"stars-2-0",
				"stars-2-5",
				"stars-3-0",
				"stars-3-5",
				"stars-4-0",
				"stars-4-5",
				"stars-5-0"
			},
			"edu/northwestern/at/utils/swing/icons/resources/staricons/"
		);


	/**	Gets an icon by index.
	 *
	 *	@param	index		The index of the icon.
	 *
	 *	@return				The image icon.
	 */

	public static ImageIcon get (int index)	{
		return starIcons.get( index );
	}

	/**	Gets an icon by name.
	 *
	 *	@param	name		The icon name.
	 *
	 *	@return				The image icon.
	 */

	public static ImageIcon get (String name) {
		return starIcons.get( name );
	}

	/**	Creates and returns a popup menu for all the star icons.
	 *
	 *	@param	listener	Shared action listener for all the menu items.
	 *
	 *	@return				The popup menu.
	 */

	public static JPopupMenu popup (ActionListener listener) {
		return starIcons.popup( listener );
	}

	/**	Gets the index of an emoticon.
	 *
	 *	@param	icon		The image icon.
	 *
	 *	@return				The index.
	 */

	public static int indexOf (ImageIcon icon) {
		return starIcons.indexOf( icon );
	}

	/** Get icons as list of JLabels.
	 *
	 *	@param		getAll	True to get all star icons, false to get
	 *						only the ones for whole numbers.
	 *
	 *	@return				Star icons corresponding to whole numbers
	 *						as JLabels.
	 */

	public static JLabel[] getLabels( boolean getAll )
	{
//$$$PIB$$$ Needs work.
		JLabel[] result;

		if ( getAll )
		{
			result = new JLabel[ 11 ];

			for ( int i = 0; i <= 10; i++ )
			{
				result[ i ] = new JLabel( starIcons.get( i ) );
			}
		}
		else
		{
			result = new JLabel[ 6 ];


			int j = 0;

			for ( int i = 0; i <= 10; i = i + 2 )
			{
				result[ j++ ] = new JLabel( StarIcons.get( i ) );
			}
		}

		return result;
	}

	/** Get icons.
	 *
	 *	@param		getAll	True to get all star icons, false to get
	 *						only the ones for whole numbers.
	 *
	 *	@return				Star icons.
	 */

	public static ImageIcon[] getIcons( boolean getAll )
	{
//$$$PIB$$$ Needs work.
		ImageIcon[] result;

		if ( getAll )
		{
			result = new ImageIcon[ 11 ];

			for ( int i = 0; i <= 10; i++ )
			{
				result[ i ] = starIcons.get( i );
			}
		}
		else
		{
			result = new ImageIcon[ 6 ];


			int j = 0;

			for ( int i = 0; i <= 10; i = i + 2 )
			{
				result[ j++ ] = StarIcons.get( i );
			}
		}

		return result;
	}

	/** Hide no-args constructor. */

	private StarIcons(){
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

