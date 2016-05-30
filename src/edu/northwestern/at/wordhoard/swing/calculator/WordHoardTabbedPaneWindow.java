package edu.northwestern.at.wordhoard.swing.calculator;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;

import java.io.*;
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.border.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.menus.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	A WordHoard window which can dock and undock tabbed panes.
 */

public class WordHoardTabbedPaneWindow extends AbstractWindow
{
	/**	Tabbed pane data for this window.
	 */

	protected TabbedPaneData tabbedPaneData;

	/**	Create dockable tabbed pane window.
	 *
	 *	@param	title			Window title.
	 *	@param	parentWindow	The parent window.
	 *	@param	tabbedPaneData	Tabbed pane data for this window.
	 */

	public WordHoardTabbedPaneWindow
	(
		String title ,
		AbstractWindow parentWindow ,
		TabbedPaneData tabbedPaneData
	)
		throws PersistenceException
	{
		super( title , parentWindow );

		this.tabbedPaneData	= tabbedPaneData;
	}

	/**	Dock this window back into the specified tabbed pane.
	 */

	public void dock()
	{
		dispose();

		JComponent tabComponent	= tabbedPaneData.getComponent();

		tabbedPaneData.getTabbedPane().insertTab
		(
			tabbedPaneData.getTitle() ,
			tabbedPaneData.getIcon() ,
			tabComponent ,
			tabbedPaneData.getToolTip() ,
			Math.min
			(
				tabbedPaneData.getIndex() ,
				tabbedPaneData.getTabbedPane().getTabCount()
			)
		);

		tabComponent.setBorder( tabbedPaneData.getBorder() );

		tabbedPaneData.getTabbedPane().setSelectedComponent( tabComponent );
	}

	/**	Handle a menu selected event.
	 *
	 *	<p>
	 *	Enable or disable the edit menu items depending upon
	 *	the availability of clipboard text or current text selection.
	 *	</p>
	 */

	public void handleEditMenuSelected()
	{
								//	Is there pasteable text on
								//	the clipboard?

		boolean pasteableDataAvailable	= clipboardHasPasteableData();

								//	Enable paste menu item if so.

		pasteCmd.setEnabled( pasteableDataAvailable );

								//	Determine if there is any
								//	text to set clear and select all
								//	menu items.
								//
								//	Determine if there is any
								//	selected text to set cut, copy,
								//	and unselect menu items.
								//

		ResultsPanel resultsPanel		=
			BaseMenu.getResultsPanel( getContentPane() );

		clearCmd.setEnabled( false );

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

	/**	Handles edit menu canceled or deselected.
	 *
	 *	<p>
	 *	When the Edit menu is canceled or deselected, we reenable all
	 *	the commands so that the command key shortcuts will still work.
	 *	</p>
	 */

	public void handleEditMenuCanceledOrDeselected()
	{
		cutCmd.setEnabled( true );
		copyCmd.setEnabled( true );
		pasteCmd.setEnabled( true );
		selectAllCmd.setEnabled( true );
	}

	/**	Do edit menu copy.
	 */

	public void handleCopyCmd()
	{
		ResultsPanel resultsPanel		=
			BaseMenu.getResultsPanel( getContentPane() );

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

	/**	Do edit menu cut.
	 */

	public void handleCutCmd()
	{
		ResultsPanel resultsPanel		=
			BaseMenu.getResultsPanel( getContentPane() );

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

	/**	Do edit menu paste.
	 */

	public void handlePasteCmd()
	{
		ResultsPanel resultsPanel		=
			BaseMenu.getResultsPanel( getContentPane() );

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

	/**	Do edit menu select all.
	 */

	public void handleSelectAllCmd()
	{
		ResultsPanel resultsPanel		=
			BaseMenu.getResultsPanel( getContentPane() );

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

	/**	Do edit menu unselect.
	 */

	public void handleUnselectCmd()
	{
		ResultsPanel resultsPanel		=
			BaseMenu.getResultsPanel( getContentPane() );

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

