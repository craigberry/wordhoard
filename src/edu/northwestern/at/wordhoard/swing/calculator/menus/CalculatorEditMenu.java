package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	WordHoard Calculator Edit Menu.
 */

public class CalculatorEditMenu extends EditMenu
{
	/**	Create edit menu.
	 *
	 *	@param	menuBar			The menu bar to which to attach this menu.
	 *	@param	parentWindow	The parent window of this menu.
	 */

	public CalculatorEditMenu
	(
		JMenuBar menuBar ,
		AbstractWindow parentWindow
	)
	{
		super( menuBar , parentWindow );
	}

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
	{
								//	Is there pasteable text on
								//	the clipboard?

		boolean pasteableDataAvailable	= clipboardHasPasteableData();

								//	Enable paste menu item if so.

		pasteMenuItem.setEnabled( pasteableDataAvailable );

								//	Determine if there is any
								//	text to set clear and select all
								//	menu items.
								//
								//	Determine if there is any
								//	selected text to set cut, copy,
								//	and unselect menu items.
								//
								//	The process differs depending upon
								//	whether the menu is attached to
								//	the Calculator window or some
								//	other.

		boolean textAvailable			= false;
		boolean selectedTextAvailable	= false;

		JTabbedPane mainTabbedPane		= getMainTabbedPane();

		JTextPane inputTextPane			= getInputTextPane();

		Component component				=
			mainTabbedPane.getSelectedComponent();

		if ( mainTabbedPane.indexOfComponent( component ) == 0 )
		{
			textAvailable			=
				( inputTextPane.getDocument().getLength() > 0 );

			selectedTextAvailable	=
				( inputTextPane.getSelectedText() != null );

			clearMenuItem.setEnabled( textAvailable );
			selectAllMenuItem.setEnabled( textAvailable );

			copyMenuItem.setEnabled( selectedTextAvailable );
			cutMenuItem.setEnabled( selectedTextAvailable );
		}
		else
		{
			ResultsPanel resultsPanel	= getResultsPanel( component );

			clearMenuItem.setEnabled( false );

			if ( resultsPanel != null )
			{
				Object results	= resultsPanel.getResults();

				if ( results instanceof CutCopyPaste )
				{
					setCutCopyPaste( (CutCopyPaste)results );
				}

				if ( results instanceof SelectAll )
				{
					setSelectAll( (SelectAll)results );
				}
			}
		}
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
		cutMenuItem.setEnabled( true );
		copyMenuItem.setEnabled( true );
		pasteMenuItem.setEnabled( true );
		selectAllMenuItem.setEnabled( true );
	}

	/**	Clear text.
	 */

	protected void clear()
	{
		JTabbedPane mainTabbedPane		= getMainTabbedPane();

		JTextPane inputTextPane			=
			getCalculatorWindow().getInputTextPane();

		Component component	= mainTabbedPane.getSelectedComponent();

		if ( mainTabbedPane.indexOfComponent( component ) == 0 )
		{
			inputTextPane.setText( "" );

			try
			{
				getInterpreter().print(
					getInterpreter().eval( "getBshPrompt();" ) );
			}
			catch ( Exception e )
			{
			}
		}
    }

	/**	Do edit menu copy.
	 */

	protected void copy( ActionEvent event )
	{
		JTabbedPane mainTabbedPane		= getMainTabbedPane();

		JTextPane inputTextPane			=
			getCalculatorWindow().getInputTextPane();

		Component component	= mainTabbedPane.getSelectedComponent();

		if ( mainTabbedPane.indexOfComponent( component ) == 0 )
		{
			getConsole().actionPerformed( event );
		}
		else
		{
			ResultsPanel resultsPanel	= getResultsPanel( component );

			if ( resultsPanel != null )
			{
				Object results	= resultsPanel.getResults();

				if ( results instanceof CutCopyPaste )
				{
					CutCopyPaste c	= (CutCopyPaste)results;

					if ( c.isCopyEnabled() ) c.copy();
				}
			}
		}
	}

	/**	Do edit menu cut.
	 */

	protected void cut( ActionEvent event )
	{
		Component component	=
			getMainTabbedPane().getSelectedComponent();

		if ( getMainTabbedPane().indexOfComponent( component ) == 0 )
		{
			getConsole().actionPerformed( event );
		}
		else
		{
			ResultsPanel resultsPanel	=
				getResultsPanel( component );

			if ( resultsPanel != null )
			{
				Object results	= resultsPanel.getResults();

				if ( results instanceof CutCopyPaste )
				{
					CutCopyPaste c	= (CutCopyPaste)results;

					if ( c.isCutEnabled() ) c.cut();
				}
			}
		}
	}

	/**	Do edit menu paste.
	 */

	protected void paste( ActionEvent event )
	{
		Component component	= getMainTabbedPane().getSelectedComponent();

		if ( getMainTabbedPane().indexOfComponent( component ) == 0 )
		{
			getConsole().actionPerformed( event );
		}
		else
		{
			ResultsPanel resultsPanel	=
				getResultsPanel( component );

			if ( resultsPanel != null )
			{
				Object results	= resultsPanel.getResults();

				if ( results instanceof CutCopyPaste )
				{
					CutCopyPaste c	= (CutCopyPaste)results;

					if ( c.isPasteEnabled() ) c.paste();
				}
			}
		}
	}

	/**	Do edit menu select all.
	 */

	protected void selectAll( ActionEvent event )
	{
		Component component	= getMainTabbedPane().getSelectedComponent();

		if ( getMainTabbedPane().indexOfComponent( component ) == 0 )
		{
			getConsole().actionPerformed( event );
		}
		else
		{
			ResultsPanel resultsPanel	=
				getResultsPanel( component );

			if ( resultsPanel != null )
			{
				Object results	= resultsPanel.getResults();

				if ( results instanceof SelectAll )
				{
					SelectAll c	= (SelectAll)results;

					if ( c.isSelectAllEnabled() ) c.selectAll();
				}
			}
		}
	}

	/**	Do edit menu unselect.
	 */

	protected void unselect( ActionEvent event )
	{
		Component component	= getMainTabbedPane().getSelectedComponent();

		if ( getMainTabbedPane().indexOfComponent( component ) == 0 )
		{
			getConsole().actionPerformed( event );
		}
		else
		{
			ResultsPanel resultsPanel	=
				getResultsPanel( component );

			if ( resultsPanel != null )
			{
				Object results	= resultsPanel.getResults();

				if ( results instanceof SelectAll )
				{
					SelectAll c	= (SelectAll)results;

					if ( c.isUnselectEnabled() ) c.unselect();
				}
			}
		}
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

