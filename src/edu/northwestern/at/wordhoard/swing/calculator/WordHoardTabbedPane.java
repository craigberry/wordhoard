package edu.northwestern.at.wordhoard.swing.calculator;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;

import javax.help.*;
import javax.swing.*;
import javax.swing.border.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.menus.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	WordHoard tabbed pane.
 *
 *	<p>
 *	Extends JTabbedPane to allow detaching tab pane to a separate window.
 *	</p>
 */

public class WordHoardTabbedPane extends JTabbedPane
{
	/**	Parent window of tabbed pane. */

	protected AbstractWindow parentWindow	= null;

	/**	Create WordHoard tabbed panel.
	 *
	 *	@param	parentWindow	Parent window of this tabbed pane.
	 */

	public WordHoardTabbedPane( AbstractWindow parentWindow )
	{
		super();
								//	Save parent window.

		this.parentWindow	= parentWindow;
	}

	/**	Undock specified tab to a separate window.
	 *
	 *	@param	referenceWindow	Reference/parent window.
	 *	@param	index			Tab index to undock.
	 *
	 *	<p>
	 *	Tab index 0 (the script interpreter console) cannot be undocked.
	 *	</p>
	 */

	public void undock( AbstractWindow referenceWindow , int index )
	{
								//	Do nothing if tab index invalid.

		if ( ( index < 1 ) || ( index >= getTabCount() ) ) return;

								//	Remember the tab index.

		final int tabIndex				= index;

								//	Get the component specified by the
								//	tab index.

		final JComponent tabComponent	=
			(JComponent)getComponentAt( tabIndex );

								//	Icon for tab.

		final Icon icon					= getIconAt( tabIndex );

								//	Get tab title.

		final String title				= getTitleAt( tabIndex );

								//	Get tab tooltip.

		final String toolTip			= getToolTipTextAt( tabIndex );

								//	Get tab component border.

		final Border border				= tabComponent.getBorder();

								//	Remove the component from the tabbed pane.

		removeTabAt( index );

								//	Set the component's preferred size.

		Dimension tabComponentSize	= tabComponent.getSize();

		if	(	( tabComponentSize.height <= 0 ) &&
			    ( tabComponentSize.width  <= 0 ) )
		{
			tabComponentSize	= tabComponent.getPreferredSize();

			if	(	( tabComponentSize.height <= 0 ) &&
				    ( tabComponentSize.width  <= 0 ) )
			{
				tabComponentSize.width	= 650;
				tabComponentSize.height	= 400;
			}
		}

		tabComponent.setPreferredSize( tabComponentSize );

								//	Set the default title for the new window
								//	to be the tab title.

		String windowTitle	= title;

								//	Find the results panel in the
								//	component, if any.

		ResultsPanel resultsPanel		=
			BaseMenu.getResultsPanel( tabComponent );

								//	Use the results title as a window title
								//	if we found a results panel.

		if ( resultsPanel != null )
		{
			windowTitle	= resultsPanel.getResultsTitle().trim();
		}
								//	Create a new window from the former
								//	tab component's data.
		try
		{
			final WordHoardTabbedPaneWindow window	=
				new WordHoardTabbedPaneWindow
				(
					windowTitle ,
					referenceWindow ,
					new TabbedPaneData
					(
						this ,
					 	title ,
	 					index ,
	 					icon ,
	 					toolTip ,
	 					tabComponent ,
	 					border
					)
				);
							//	Add tab component as window contents.

			window.getContentPane().add( tabComponent );

								//	Set a window focus listener to work
								//	around a problem with a new window
								//	losing focus immediately after it is
								//	brought to the foreground the
								//	first time.

			WindowFocusListener windowFocusListener =
				new WindowFocusListener()
				{
					long start;
					long end;

					public void windowGainedFocus( WindowEvent e )
					{
						start	= System.currentTimeMillis();
					}

					public void windowLostFocus( WindowEvent e )
					{
						end		= System.currentTimeMillis();

						long elapsed	= end - start;
						if ( elapsed < 100 ) window.toFront();

						window.removeWindowFocusListener( this );
					}
				};

			window.addWindowFocusListener( windowFocusListener );

							//	Pack and show the new window.

			window.pack();
							//	Set the location of the new window.

			AbstractWindow	refWindow	= referenceWindow;

			if ( refWindow == null )
			{
				refWindow	= parentWindow;

				window.setLocation( parentWindow.getLocation() );

				Dimension parentSize	= refWindow.getSize();
				Dimension windowSize	= window.getSize();

				windowSize.height		= parentSize.height;

				window.setSize( windowSize );
			}
			else
			{
				window.positionNextTo( refWindow );

							//	Make sure it is not too tall.

				if ( window.getSize().height > refWindow.getSize().height )
				{
					Dimension parentSize	= refWindow.getSize();
					Dimension windowSize	= window.getSize();

					windowSize.height		= parentSize.height;

					window.setSize( windowSize );
				}
			}
							//	Show the new window.

			window.setVisible( true );

							//	Set focus to the content pane.

			window.getContentPane().requestFocus();

							//	Bring window to the front.

			window.toFront();
		}
		catch ( Exception e )
		{
		}
	}

	/**	Undock current tab to a separate window.
	 *
	 *	@param	calcWindowVisible	true if calculator window was
	 *								visible when analysis started.
	 *	<p>
	 *	Tab index 0 (the script interpreter console) cannot be undocked.
	 *	</p>
	 */

	public void undock( boolean calcWindowVisible )
	{
		if ( calcWindowVisible )
		{
			undock( parentWindow , getSelectedIndex() );
		}
		else
		{
			undock( null , getSelectedIndex() );
		}
	}

	/**	Undock current tab to a separate window.
	 *
	 *	<p>
	 *	Tab index 0 (the script interpreter console) cannot be undocked.
	 *	</p>
	 */

	public void undock()
	{
//		undock( parentWindow , getSelectedIndex() );
		undock( null , getSelectedIndex() );
	}

	/**	Undock current tab to a separate window using specified reference.
	 *
	 *	@param	referenceWindow	Reference window for position and sizing.
	 *
	 *	<p>
	 *	Tab index 0 (the script interpreter console) cannot be undocked.
	 *	</p>
	 */

	public void undock( AbstractWindow referenceWindow )
	{
		undock( referenceWindow , getSelectedIndex() );
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


