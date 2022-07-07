package edu.northwestern.at.utils.swing.systeminformation;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.swing.*;

/** System information window.
 *
 *	<p>
 *	Displays important Java run-time enviroment settings.  This is a
 *	singleton class.
 *	</p>
 */

public class SystemInformationWindow extends XFrame
{
	/** The panel which holds the settings. */

	protected SystemInformationPanel systemInformationPanel;

	/**	The system information window, or null if none is open. */

	protected static SystemInformationWindow systemInformationWindow;

	/**	Opens or brings to the front the about window.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *	@param	title			Title.
	 *	@param	refreshName		Refresh button name.
	 *	@param	closeName		Close button name.
	 */

	public static void open
	(
		Frame parentWindow ,
		String title ,
		String refreshName ,
		String closeName
	)
	{
		if ( systemInformationWindow == null )
		{
			systemInformationWindow =
				new SystemInformationWindow
				(
					parentWindow ,
					title ,
					refreshName ,
					closeName
				);
		}
		else
		{
			systemInformationWindow.setVisible( true );
			systemInformationWindow.toFront();
		}
	}

	/**	Constructs a new system information window.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *	@param	title			Title.
	 *	@param	refreshName		Refresh button name.
	 *	@param	closeName		Close button name.
	 */

	protected SystemInformationWindow
	(
		Frame parentWindow ,
		String title ,
		String refreshName ,
		String closeName
	)
	{
		super( title );

		systemInformationPanel	= new SystemInformationPanel();

		if ( parentWindow != null)
		{
			setIconImage( parentWindow.getIconImage() );
		}

		getContentPane().add( systemInformationPanel );

		systemInformationPanel.addButton
		(
			refreshName ,
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					performRefreshAction( e );
				};
			}
		);

		systemInformationPanel.addDefaultButton
		(
			closeName ,
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					performCloseAction( e );
				};
			}
		);

		pack( parentWindow );

		setVisible( true );
	}

	/**	Perform refresh action.
	 * @param	e	The ActionEvent that triggered the invocation.
	 */

	protected void performRefreshAction( ActionEvent e )
	{
		refresh();
	}

	/**	Perform close action.
	 * @param	e	The ActionEvent that triggered the invocation.
	 */

	protected void performCloseAction( ActionEvent e )
	{
		setVisible( false );
	}

	/**	Refreshes the window.
	 *
	 *	<p>The system garbage collector is run before refreshing the window.</p>
	 */

	public void refresh()
	{
		systemInformationPanel.refresh();
	}

	/**	Get the system information window.
	 *
	 *	@return		The singleton system information window.
	 */

	public static SystemInformationWindow getSystemInformationWindow()
	{
		return systemInformationWindow;
	}

	/**	Get the system information panel.
	 *
	 *	@return		The system information panel for the singleton window.
	 */

	public SystemInformationPanel getSystemInformationPanel()
	{
		return systemInformationPanel;
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

