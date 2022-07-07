package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Displays list box with ok and cancel buttons.
 */

public class DisplayListBox extends ModalDialog
{
	/**	The list box. */

	protected XList listBox;

	/**	Selected values. */

	protected Object[] selectedValues;

	/**	Minimum selected value index. */

	protected int minSelectionIndex;

	/**	Maximum selected value index. */

	protected int maxSelectionIndex;

	/**	True if dialog cancelled. */

	protected boolean cancelled;

	/** Displays a list box.
	 *	@param	parentWindow		Parent window of list box.
	 *	@param	title				List box title.
	 *	@param	label				List box label text.  May be null.
	 *	@param	entries				Objects to display in list box.
	 *	@param	defaultEntry		Default entry to highlight. Null if none.
	 *	@param	isMultiSelect		True to allow multiple selection.
	 *	@param	isModal				True for modal dialog, false otherwise.
	 *	@param	customRenderer		Custom list renderer.  May be null.
	 *	@param	okButtonText		Text for OK button.
	 *	@param	cancelButtonText	Text for cancel button.
	 */

	public DisplayListBox
	(
		Window parentWindow ,
		String title ,
		JLabel label ,
		Object[] entries ,
		Object defaultEntry ,
		boolean isMultiSelect ,
		boolean isModal ,
		ListCellRenderer customRenderer ,
		String okButtonText ,
		String cancelButtonText
	)
	{
		super( title );
								//	Not cancelled yet.

		cancelled	= false;

								// Set modal dialog status.

		this.setModal( isModal );

								// Allow dialog to resize.

		this.setResizable( true );

								// Add list box label, if any.

		if ( label != null ) add( label );

								//	Create list box.

		listBox	= new XList( entries );

								//	Set selection mode.

		if ( isMultiSelect )
		{
			listBox.setSelectionMode(
				ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
		}
		else
		{
			listBox.setSelectionMode(
				ListSelectionModel.SINGLE_SELECTION );
		}
								//	Nothing selected yet.

		selectedValues		= null;
		minSelectionIndex	= -1;
		maxSelectionIndex	= -1;

                                //	Set up scroller for list box.

		JScrollPane scroller = new JScrollPane( listBox );

								//	Add scroller and list box.
		add( scroller );

								//	Set up default entry for highlighting.

		if ( defaultEntry != null )
		{
			listBox.setSelectedValue( defaultEntry , true );
		}
								//	Set custom list renderer if provided.

		if ( customRenderer != null )
		{
			listBox.setCellRenderer( customRenderer );
		}
								//	Add cancel and OK buttons.

		addButton( cancelButtonText , cancel );
		addButton( okButtonText , ok );

								//	Position dialog.

		WindowPositioning.centerWindowOverWindow( this , parentWindow, 25 );

								//	Display dialog.
		setVisible( true );
	}

	/**	OK button action listener. */

	protected ActionListener ok =
		new ActionListener()
		{
			public void actionPerformed( ActionEvent event )
			{
				handleOKPressed( event );
			}
		};

	/**	Cancel button action listener. */

	protected ActionListener cancel =
		new ActionListener()
		{
			public void actionPerformed( ActionEvent event )
			{
				handleCancelButtonPressed( event );
			}
		};

	/**	Handle OK button pressed.
	 *
	 *	@param	event	The event.
	 */

	protected void handleOKPressed( ActionEvent event )
	{
		selectedValues		= listBox.getSelectedValuesList().toArray();
		minSelectionIndex	= listBox.getMinSelectionIndex();
		maxSelectionIndex	= listBox.getMaxSelectionIndex();

		cancelled			= false;

		dispose();
	}

	/**	Handle Cancel button pressed.
	 *
	 *	@param	event	The event.
	 */

	protected void handleCancelButtonPressed( ActionEvent event )
	{
		selectedValues		= null;
		minSelectionIndex	= -1;
		maxSelectionIndex	= -1;

		cancelled			= true;

		dispose();
	}

	/**	See if dialog was cancelled.
	 *
	 *	@return		true if dialog cancelled.
	 */

	public boolean getCancelled()
	{
		return cancelled;
	}

	/**	Get selected entries from list box.
	 *
	 *	@return		Array of objects selected in list box.
	 *				Empty (length 0) if none selected.
	 */

	public Object[] getSelected()
	{
		return selectedValues;
	}

	/**	Get minimum selected index.
	 *
	 *	@return		The smallest selected object index.
	 */

	public int getMinSelectionIndex()
	{
		return minSelectionIndex;
	}

	/**	Get maximum selected index.
	 *
	 *	@return		The largest selected object index.
	 */

	public int getMaxSelectionIndex()
	{
		return maxSelectionIndex;
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

