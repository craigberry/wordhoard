package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;

/** Combobox key selection manager which selects items using multiple characters.
 *
 *	<p>
 *	Based upon an example in the Java Developer's Almanac 1.4 by Patrick Chan.
 *	</p>
 */

public class ComboBoxMultipleKeySelectionManager
	implements JComboBox.KeySelectionManager
{
	/**	Time at which last key was typed. */

	protected long lastKeyTime	= 0;

	/**	Accumulates key strokes that are typed less then "keyInterval"
	 *	milliseconds apart.
	 */

	protected String pattern	= "";

	/**	Number of milliseconds under which a new keystroke is assumed
	 *	to be appended to the previous to form a search string.
	 */

	protected long maximumKeyArrivalInterval	= 500;

	/**	Handle selection when a key arrives.
	 *
	 *	@param	aKey	The typed key.
	 *	@param	model	The ComboBoxModel.
	 *
	 *	@return			Index of matching entry, or -1 if none.
	 */

	public int selectionForKey( char aKey , ComboBoxModel model )
	{
								//	Find index of selected item.

		int selectedIndex	= 1;
		Object selected		= model.getSelectedItem();

		if ( selected != null )
		{
			for ( int i = 0 ; i < model.getSize() ; i++ )
			{
				if ( selected.equals( model.getElementAt( i ) ) )
				{
					selectedIndex	= i;
					break;
				}
			}
		}
								//	Get the current time

		long curTime	= System.currentTimeMillis();

								//	If last key was typed less than the
								//	specified interval milliseconds ago,
								//	append this new key to current pattern.

		if ( ( curTime - lastKeyTime ) < maximumKeyArrivalInterval )
		{
			pattern += ( "" + aKey ).toLowerCase();
		}
		else
		{
			pattern = ( "" + aKey ).toLowerCase();
		}
								//	Save current time.

		lastKeyTime	= curTime;

								// Search forward starting at current selection.

		for ( int i = selectedIndex ; i < model.getSize() ; i++ )
		{
			String s	= model.getElementAt( i ).toString().toLowerCase();

			if ( s.startsWith( pattern ) )
			{
				return i;
			}
		}
								//	Search from top to current selection.

		for ( int i = 0 ; i < selectedIndex ; i++ )
		{
			if ( model.getElementAt( i ) != null )
			{
				String s	= model.getElementAt( i ).toString().toLowerCase();

				if ( s.startsWith( pattern ) )
				{
					return i;
				}
			}
		}
								//	Return -1 if match not found.
		return -1;
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

