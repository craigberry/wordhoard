package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;

import javax.help.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.LookAndFeel;
import edu.northwestern.at.utils.sys.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.dialogs.*;

/**	WordHoard Calculator Edit Menu.
 */

public class EditMenu extends BaseMenu
{
	/**	The edit menu items. */

	/**	Copy selected contents to clipboard. */

	protected JMenuItem copyMenuItem;

	/**	Cut selected contents to clipboard. */

	protected JMenuItem cutMenuItem;

	/**	Paste clipboard contents. */

	protected JMenuItem pasteMenuItem;

	/**	Select all available text. */

	protected JMenuItem selectAllMenuItem;

	/**	Deselect any currently selected text. */

	protected JMenuItem unselectMenuItem;

	/**	Clear current display. */

	protected JMenuItem clearMenuItem;

	/**	Create edit menu.
	 *
	 *	@param	parentWindow	The parent window of this menu.
	 */

	public EditMenu( AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "editMenuName" , "Edit" )
		);

		this.parentWindow	= parentWindow;
	}

	/**	Create edit menu.
	 *
	 *	@param	menuBar			The menu bar to which to attach this menu.
	 *	@param	parentWindow	The parent window of this menu.
	 */

	public EditMenu( JMenuBar menuBar , AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "editMenuName" , "Edit" ) ,
			menuBar
		);

		this.parentWindow	= parentWindow;
	}

	/**	Create the menu items.
	 */

	protected void createMenuItems()
	{
								// Cut
		cutMenuItem		=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"editMenuCutItem" ,
					"Cut"
				) ,
				new GenericActionListener( "cut" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_X ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
				true ,
				false
			);
								// Copy
		copyMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"editMenuCopyItem" ,
					"Copy"
				) ,
				new GenericActionListener( "copy" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_C ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
				true ,
				false
			);
								// Paste
		pasteMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"editMenuPasteItem" ,
					"Paste"
				) ,
				new GenericActionListener( "paste" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_V ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
                true ,
                false
			);

		addSeparator();

								// Select all
		selectAllMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"editMenuSelectAllItem" ,
					"Select all"
				) ,
				new GenericActionListener( "selectAll" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_A ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
				true ,
				false
			);
								// Unselect
		unselectMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"editMenuUnselectItem" ,
					"Unselect"
				) ,
				new GenericActionListener( "unselect" )
			);

		unselectMenuItem.setEnabled( false );

		addSeparator();

								// Clear
		clearMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"editMenuClearItem" ,
					"Clear"
				) ,
				new GenericActionListener( "clear" )
			);

		clearMenuItem.setEnabled( false );

								//	Adding edit menu listener here fails.
								//	Adding in parent window succeeds.

//		addMenuListener( editMenuListener );

		if ( menuBar != null ) menuBar.add( this );
	}

	/** Edit menu listener. */

	public MenuListener editMenuListener =
		new MenuListener()
		{
			public void menuCanceled( MenuEvent menuEvent )
			{
				handleEditMenuCanceledOrDeselected();
			}

			public void menuDeselected( MenuEvent menuEvent )
			{
				handleEditMenuCanceledOrDeselected();
			}

			public void menuSelected( MenuEvent menuEvent )
			{
				try
				{
					handleEditMenuSelected( menuEvent );
				}
				catch ( Exception e )
				{
					Err.err( e );
				}
			}
		};

	/**	Handle a menu selected event.
	 *
	 *	@param	menuEvent	Menu event.
	 *
	 *	<p>
	 *	Enable or disable the edit menu items depending upon
	 *	the availability of clipboard text or current text selection.
	 *	</p>
	 */

	protected void handleEditMenuSelected( MenuEvent menuEvent )
		throws Exception
	{
		((AbstractWindow)parentWindow).handleEditMenuSelected();
	}

	/**	Handles edit menu canceled or deselected.
	 *
	 *	<p>
	 *	When the Edit menu is canceled or deselected, we reenable all
	 *	the commands so that the command key shortcuts will still work.
	 *	</p>
	 */

	public void handleEditMenuCanceledOrDeselected()
	{
		((AbstractWindow)parentWindow).handleEditMenuCanceledOrDeselected();
	}

	/** Check if clipboard has pasteable data.
	 *
	 *	@return		true if clipboard has pasteable text.
	 */

	protected boolean clipboardHasPasteableData()
	{
		boolean result = false;

		Transferable content	= SystemClipboard.getContents( null );

		if ( content != null )
		{
			try
			{
				result =
					content.isDataFlavorSupported( DataFlavor.stringFlavor);
			}
			catch ( Exception e )
			{
			}
        }

		return result;
	}

	/**	Clear text.
	 */

	protected void clear()
		throws Exception
	{
		((AbstractWindow)parentWindow).handleClearCmd();
    }

	/**	Do edit menu copy.
	 */

	protected void copy( ActionEvent event )
		throws Exception
	{
		((AbstractWindow)parentWindow).handleCopyCmd();
	}

	/**	Do edit menu cut.
	 */

	protected void cut( ActionEvent event )
		throws Exception
	{
		((AbstractWindow)parentWindow).handleCutCmd();
	}

	/**	Do edit menu paste.
	 */

	protected void paste( ActionEvent event )
		throws Exception
	{
		((AbstractWindow)parentWindow).handlePasteCmd();
	}

	/**	Do edit menu select all.
	 */

	protected void selectAll( ActionEvent event )
		throws Exception
	{
		((AbstractWindow)parentWindow).handleSelectAllCmd();
	}

	/**	Do edit menu unselect.
	 */

	protected void unselect( ActionEvent event )
		throws Exception
	{
		((AbstractWindow)parentWindow).handleUnselectCmd();
	}

	/**	Set menu status from a CutCopyPaste object.
	 *
	 *	@param	copyable	Object implementing the CutCopyPaste interface.
	 *
	 *	<p>
	 *	On output, sets the status of the Cut, Copy, and Paste menu items
	 *	to reflect the status of the cutCopyPaste object.
	 *	</p>
	 */

	 public void setCutCopyPaste( CutCopyPaste copyable )
	 {
		copyMenuItem.setEnabled( copyable.isCopyEnabled() );
		pasteMenuItem.setEnabled( copyable.isPasteEnabled() );
		cutMenuItem.setEnabled( copyable.isCutEnabled() );
	 }

	/**	Set menu status from a SelectAll object.
	 *
	 *	@param	selectAll	Object implementing the SelectAll interface.
	 *
	 *	<p>
	 *	On output, sets the status of the Select All and Unselect menu
	 *	items to reflect the status of the SelectAll object.
	 *	</p>
	 */

	 public void setSelectAll( SelectAll selectAll )
	 {
		selectAllMenuItem.setEnabled( selectAll.isSelectAllEnabled() );
		unselectMenuItem.setEnabled( selectAll.isUnselectEnabled() );
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

