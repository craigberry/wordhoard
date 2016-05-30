package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

import javax.help.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.systeminformation.*;
import edu.northwestern.at.utils.sys.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.sysinfo.*;

/**	WordHoard Calculator Help Menu.
 */

public class HelpMenu extends BaseMenu
{
	/**	The help menu items. */

	/**	About box. */

	protected JMenuItem aboutMenuItem;

	/**	System information window. */

	protected JMenuItem systemInformationMenuItem;

	/**	WordHoard web page. */

	protected JMenuItem wordHoardWebPageMenuItem;

	/**	Create help menu.
	 */

	public HelpMenu()
	{
		super
		(
			WordHoardSettings.getString( "helpMenuName" , "Help" )
		);
	}

	/**	Create help menu.
	 *
	 *	@param	menuBar		The menu bar to which to attach the help menu.
	 */

	public HelpMenu( JMenuBar menuBar )
	{
		super
		(
			WordHoardSettings.getString( "helpMenuName" , "Help" ) ,
			menuBar
		);
	}

	/**	Create help menu.
	 *
	 *	@param	parentWindow	The parent window for the menu.
	 */

	public HelpMenu( AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "helpMenuName" , "Help" ) ,
			parentWindow
		);
	}

	/**	Create help menu.
	 *
	 *	@param	menuBar			The menu bar to which to attach the menu.
	 *	@param	parentWindow	The parent window for the menu.
	 */

	public HelpMenu( JMenuBar menuBar , AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "helpMenuName" , "Help" ) ,
			menuBar ,
			parentWindow
		);
	}

	/**	Create the menu items.
	 */

	protected void createMenuItems()
	{
		wordHoardWebPageMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"helpMenuWordHoardWebPageItem" ,
					"WordHoard User Manual"
				) ,
				new GenericActionListener( "displayWordHoardWebPage" )
			);

		aboutMenuItem	= null;

		if ( !Env.MACOSX )
		{
			aboutMenuItem =
				addMenuItem
				(
					WordHoardSettings.getString(
						"helpMenuAboutItem" , "About" ) ,
					new GenericActionListener( "about" )
				);
		}

		systemInformationMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"helpMenuSystemInformationItem" ,
					"System Information"
				) ,
				new GenericActionListener( "systemInformation" )
			);

		if ( menuBar != null ) menuBar.add( this );
	}

	/**	About box.
	 */

	public void about()
	{
		try
		{
			AboutWindow.open( parentWindow );
		}
		catch ( Exception e )
		{
			Err.err( e );
		}
	}

	/**	System information.
	 */

	protected void systemInformation()
	{
		try
		{
			SysInfoWindow.open( parentWindow );
		}
		catch ( Exception e )
		{
			Err.err( e );
		}
	}

	/**	Display WordHoard web page.
	 */

	protected void displayWordHoardWebPage()
	{
		WebStart.showDocument( WordHoardSettings.getWebSiteURL() );
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

