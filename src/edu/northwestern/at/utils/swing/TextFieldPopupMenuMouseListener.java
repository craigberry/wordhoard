package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.datatransfer.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.*;

import edu.northwestern.at.utils.swing.styledtext.*;
import edu.northwestern.at.utils.*;

/** Creates a popup menu for applying edit operations to text fields. */

public class TextFieldPopupMenuMouseListener extends MouseAdapter
{
	/** The popup menu. */

	protected JPopupMenu popup = new JPopupMenu();

	/** Popup menu items.  Replicates standard MS Windows popup for
	 *	text fields. */

	/** Undo */

	protected JMenuItem undoItem;

	/** Cut */

	protected JMenuItem cutItem;

	/** Copy */

	protected JMenuItem copyItem;

	/** Paste */

	protected JMenuItem pasteItem;

	/** Delete */

	protected JMenuItem deleteItem;

	/** Select all */

	protected JMenuItem selectAllItem;

	/** The text component to which to attach the popup menu. */

	protected JTextComponent textComponent;

	/** Saves contents of text fields between editing actions. */

	protected StyledString savedText = new StyledString();

	/** Last popup menu action selected. */

	protected String lastAction = "";

	/** Create the popup menu mouse listener. */

	public TextFieldPopupMenuMouseListener()
	{
								// Create undo action.

		undoItem = popup.add( undoAction );

		popup.addSeparator();

        						// Create cut action.

		cutItem = popup.add( cutAction );

								// Create copy action.

		copyItem = popup.add( copyAction );

								// Create paste action.

		pasteItem = popup.add( pasteAction );

								// Create delete action.

		deleteItem = popup.add( deleteAction );

		popup.addSeparator();

								// Create select all action.

		selectAllItem = popup.add( selectAllAction );
	}

	/** Undo action. */

	protected AbstractAction undoAction =
		new AbstractAction( "Undo" )
		{
			public void actionPerformed( ActionEvent e )
			{
				if ( textComponent instanceof XTextPane )
				{
					XTextPane textPane = (XTextPane)textComponent;

					UndoManager undoManager =
						textPane.getUndoManager();

					if ( undoManager != null )
					{
						undoManager.undo();

						setEnabled( undoManager.canUndo() );
					}
					else
					{
						textPane.setStyledText( savedText );
					}
				}
				else if ( lastAction.compareTo( "" ) != 0 )
				{
					textComponent.setText( "" );
					textComponent.replaceSelection( savedText.str );
				}
			}
		};

	/** Cut action. */

	protected AbstractAction cutAction =
		new AbstractAction( "Cut" )
		{
			public void actionPerformed( ActionEvent e )
			{
				lastAction	= "c";

				saveText();

				textComponent.cut();
			}
		};

	/** Copy action. */

	protected AbstractAction copyAction =
		new AbstractAction( "Copy" )
		{
			public void actionPerformed( ActionEvent e )
			{
				lastAction	= "";

				textComponent.copy();
			}
		};

	/** Paste action. */

	protected AbstractAction pasteAction =
		new AbstractAction( "Paste" )
		{
			public void actionPerformed( ActionEvent e )
			{
				lastAction	= "p";

				saveText();

				textComponent.paste();
			}
		};

	/** Delete action. */

	protected AbstractAction deleteAction =
		new AbstractAction( "Delete" )
		{
			public void actionPerformed( ActionEvent e )
			{
				lastAction	= "d";

        		saveText();

				textComponent.replaceSelection( "" );
			}
		};

	/** Select all action. */

	protected AbstractAction selectAllAction =
		new AbstractAction( "Select All" )
		{
			public void actionPerformed( ActionEvent e )
			{
				lastAction	= "s";

        		saveText();

        		textComponent.selectAll();
			}
		};

	/** Save the current text.
	 *
	 *	<p>
	 *	Always saved as styled text even from a plain text component.
	 *	</p>
	 */

	protected void saveText()
	{
		if ( textComponent instanceof XTextPane )
		{
			savedText	= ((XTextPane)textComponent).getStyledText();
		}
		else
		{
			savedText	=
				new StyledString( textComponent.getText() , null );
		}
	}

	/** Check if mouse event is a popup menu event.
	 *
	 *	@param	event	The mouse event.
	 */

	protected void checkPopup( MouseEvent event )
	{
		if ( event.isPopupTrigger() )
		{
								// If not a text component, do nothing.

			if ( event.getSource() instanceof JTextComponent )
			{
                                // Get the text component.

				textComponent = (JTextComponent)event.getSource();

								//	1.4 preferred method:
								// 	textComponent.requestDefaultFocus();
								// 	textComponent.requestFocusInWindow();

				textComponent.requestFocus();

								// Set status of popup menu items.

				setPopupItemStatus( textComponent );

								// Display the popup.

				popup.show( event.getComponent(), event.getX(), event.getY() );
			}
		}
	}

	/** Handle mouse pressed event.
	 *
	 *	@param	event	The mouse event.
	 */

	public void mousePressed( MouseEvent event )
	{
		checkPopup( event );
	}

	/** Handle mouse released event.
	 *
	 *	@param	event	The mouse event.
	 */

    public void mouseReleased( MouseEvent event )
	{
		checkPopup( event );
	}

	/** Handle mouse clicked event.
	 *
	 *	@param	event	The mouse event.
	 */

	public void mouseClicked( MouseEvent event )
	{
		checkPopup( event );
	}

	/** Set status of popup menu items.
	 * @param textComponent JTextComponent that will have its status set.
	*/

	protected void setPopupItemStatus( JTextComponent textComponent )
	{
		boolean enabled			= textComponent.isEnabled();

		boolean editable		= textComponent.isEditable();

		boolean nonempty		=
			!(	( textComponent.getText() == null ) ||
				( textComponent.getText().equals( "" ) ) );

		boolean anyTextSelected	=
			( textComponent.getSelectedText() != null );

		boolean pasteAvailable	= false;

		undoItem.setEnabled( enabled && editable );

		if ( textComponent instanceof XTextPane )
		{
			XTextPane textPane = (XTextPane)textComponent;

			pasteAvailable =
				textPane.clipboardHasPasteableData();

			if ( textPane.getUndoManager() != null )
				undoItem.setEnabled( textPane.getUndoManager().canUndo() );
		}
		else
		{
			pasteAvailable =
				SystemClipboard.getContents(
					this ).isDataFlavorSupported( DataFlavor.stringFlavor );
        }

		cutItem.setEnabled( enabled && editable && anyTextSelected );
		copyItem.setEnabled( enabled && anyTextSelected );
		pasteItem.setEnabled( enabled && editable && pasteAvailable );
		deleteItem.setEnabled( enabled && editable && anyTextSelected );
		selectAllItem.setEnabled( enabled && nonempty );
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

