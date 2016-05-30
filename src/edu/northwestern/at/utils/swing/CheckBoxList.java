package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

/** CheckBoxList:  a JCheckBoxList with checkboxes as entries.
 */

public class CheckBoxList extends JList
{
	/* List model. */

	DefaultListModel model = new DefaultListModel();

	/* Creates an empty CheckBoxList.
	 */

	public CheckBoxList()
	{
		common();
	}

	/* Creates a CheckBoxList from an array of objects.
	 *
	 *	@param	objects		The objects.
	 */

	public CheckBoxList( Object[] objects )
	{
  		super();
		common();
		setListData( objects );
	}

	/** Add list data.
	 *
	 *	@param	objects		Array of objects to add to list box.
	 *						If null, the list will be cleared.
	 */

	public void setListData( Object[] objects )
	{
		if ( objects != null )
		{
			for ( int i = 0 ; i < objects.length ; i++ )
			{
				addItem( objects[ i ] );
			}
		}
		else
		{
			model.clear();
		}
	}

	/** Convert array of objects to a CheckBoxListItem array.
	 *
	 *	@param	objects		Array of objects.
	 *
	 *	@return				The objects converted to an array of
	 *						CheckBoxListItems.
	 */

	protected static CheckBoxListItem[] createData( Object[] objects )
	{
		int n = objects.length;

		CheckBoxListItem[] items	= new CheckBoxListItem[ n ];

		for ( int i = 0; i < n; i++ )
		{
			items[ i ]	= new CheckBoxListItem( objects[ i ] );
		}

		return items;
	}

	/** Convert object to a CheckBoxListItem.
	 *
	 *	@param	object	The object.
	 *
	 *	@return			The object converted to a CheckBoxListItem.
	 */

	protected static CheckBoxListItem createData( Object object )
	{
		return new CheckBoxListItem( object );
	}

	/** Convert object and checked status to a CheckBoxListItem.
	 *
	 *	@param	object		The object.
	 *	@param	checked		True if checked.
	 *
	 *	@return				The object and checked status are
	 *						converted to CheckBoxListItem.
	 */

	protected static CheckBoxListItem createData
	(
		Object object ,
		boolean checked
	)
	{
		return new CheckBoxListItem( object , checked );
	}

	/** Common code for CheckBoxList creation.
	 */

	private void common()
	{
								// Set model.
		setModel( model );
								// Add custom combobox renderer.

		setCellRenderer( new CheckBoxListRenderer() );

								// Add new mouse listener so we can
								// capture clicks on checkboxes.

		addMouseListener( mouseListener );
	}

	/** Is specific list entry checked?
	 *
	 *	@param	index	Index of item to check.
	 *
	 *	@return			True if item selected.
	 */

	public boolean isChecked( int index )
	{
		boolean result = false;

		if ( ( index >= 0 ) && ( index < model.getSize() ) )
		{
			Object c = model.getElementAt( index );

			if ( c instanceof CheckBoxListItem )
			{
				result = ((CheckBoxListItem)c).isChecked();
			}
		}

		return result;
	}

	/** Set specific entry checked.
	 *
	 *	@param	index	Index of item for which to set checked state.
	 *
	 *	@param	checked	True to set item checked, false for unchecked.
	 */

	public void setChecked( int index , boolean checked )
	{
		if ( ( index >= 0 ) && ( index < model.getSize() ) )
		{
			Object c = model.getElementAt( index );

			if ( c instanceof CheckBoxListItem )
			{
				((CheckBoxListItem)c).setChecked( checked );

				fireSelectionValueChanged( index , index , false );

				repaint();
			}
		}
	}

	/** Add object to list box.
	 *
	 *	@param	object	The object to add.
	 */

	public void addItem( Object object )
	{
		model.addElement( createData( object ) );
	}

	/** Add object to list box and set its checked state.
	 *
	 *	@param	object		The object to add.
	 *	@param	checked		True if object to be checked.
	 */

	public void addItem( Object object , boolean checked )
	{
		model.addElement( createData( object , checked ) );
	}

	/** Add objects to list box.
	 *
	 *	@param	objects	The objects to add.
	 */

	public void addItems( Object[] objects )
	{
		for ( int i = 0 ; i < objects.length ; i++ )
		{
			addItem( objects[ i ] );
		}
	}

	/** Add objects to list box and set checked state.
	 *
	 *	@param	objects		The objects to add.
	 *	@param	checked		True if objects to be checked.
	 */

	public void addItems( Object[] objects , boolean checked )
	{
		for ( int i = 0 ; i < objects.length ; i++ )
		{
			addItem( objects[ i ] , checked );
		}
	}

	/** Clear contents of list. */

	public void clear()
	{
		model.clear();
	}

	/** Get text of specific list entry.
	 *
	 *	@param	index	Index of item for which text is wanted.
	 *
	 *	@return			The text.
	 */

	public String getItemText( int index )
	{
		String result = "";

		if ( ( index >= 0 ) && ( index < model.getSize() ) )
		{
			Object c = model.getElementAt( index );

			if ( c instanceof CheckBoxListItem )
			{
				result = ((CheckBoxListItem)c).getText();
			}
		}

		return result;
	}

	/** Get specific list entry.
	 *
	 *	@param	index	Index of item for which object is wanted.
	 *
	 *	@return			The object.
	 */

	public Object getItem( int index )
	{
		Object result = null;

		if ( ( index >= 0 ) && ( index < model.getSize() ) )
		{
			result	= model.getElementAt( index );
		}

		return result;
	}

	/** Mouse listener.  Changes list item checked status on clicks. */

	MouseAdapter mouseListener =
	(
		new MouseAdapter()
		{
			public void mouseClicked( MouseEvent e )
			{
				int index =
					CheckBoxList.this.locationToIndex( e.getPoint() );

				CheckBoxListItem item =
					(CheckBoxListItem)CheckBoxList.this.model.getElementAt(
						index );

				item.setChecked( !item.isChecked() );

				fireSelectionValueChanged( index , index , false );

				Rectangle rect =
					CheckBoxList.this.getCellBounds( index , index );

				CheckBoxList.this.repaint( rect );
			}
		}
	);
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

