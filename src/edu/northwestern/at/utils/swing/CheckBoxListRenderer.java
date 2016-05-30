package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/** CheckBoxListRenderer:  List renderer which knows how to draw checkboxes.
 */

public class CheckBoxListRenderer extends JCheckBox
	implements ListCellRenderer
{
		/** Create a renderer.  Set it to be opaque. */

	public CheckBoxListRenderer()
	{
//		setOpaque( true );

//		setBackground( UIManager.getColor( "List.textBackground" ) );
//		setForeground( UIManager.getColor( "List.textForeground" ) );
		setBackground( Color.white );
		setForeground( Color.black );
	}

	/** Create a combobox entry renderer.
	 *
	 *	@param	list			The JList/JCheckBoxList we're painting.
	 *	@param	object			The object returned by
	 *							list.getModel().getElementAt(index).
	 *	@param	index			The cells index.
	 *	@param	isSelected		True if the specified cell was selected.
	 *	@param	cellHasFocus	True if the specified cell has the focus.
	 *
	 *	@return					A component whose paint() method will
	 *							render the specified value.
	 */

	public Component getListCellRendererComponent
	(
		JList list,
		Object object,
		int index,
		boolean isSelected,
		boolean cellHasFocus
	)
	{
		setEnabled( list.isEnabled() );

		setSelected( ((CheckBoxListItem)object).isChecked() );

		setFont( list.getFont() );

		setListItemText( object );

		return this;
	}

	/**	Set the list box display text a value.

	 *
	 *	@param	object	The list box item to display.
	 *
	 *	<p>
	 *	The object's toString() method is used to get the display text.
	 *	</p>
	 */

	public void setListItemText( Object object )
	{
		setText( object.toString() );
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

