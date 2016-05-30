package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;

import javax.help.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.sys.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;

/**	WordHoard Calculator Author Menu.
 */

public class AuthorMenu extends BaseMenu
{
	/**	The author menu items. */

	protected JMenuItem authorsAuthorMenuItem;

	/**	Create author menu.
	 *
 	 *	@param	parentWindow	The parent window of this menu.
	 */

	public AuthorMenu( AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString
			(
				"authorMenuName" ,
				"Author"
			) ,
			null ,
			parentWindow
		);
	}

	/**	Create author menu.
	 *
	 *	@param	menuBar		The menu bar to which to attach the author menu.
	 */

	public AuthorMenu( JMenuBar menuBar )
	{
		super
		(
			WordHoardSettings.getString( "authorMenuName" , "Author" ) ,
			menuBar
		);
	}

	/**	Create author menu.
	 */

	public AuthorMenu()
	{
		super
		(
			WordHoardSettings.getString
			(
				"authorMenuName" ,
				"Author"
			)
		);
	}

	/**	Create the menu items.
	 */

	protected void createMenuItems()
	{
		authorsAuthorMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"authorsAuthorMenuUItem" ,
					"Authors..."
				) ,
				new GenericActionListener( "showAuthors" )
			);

		if ( menuBar != null ) menuBar.add( this );
	}

	/**	Show authors and their works.
	 */

	protected void showAuthors()
	{
//		new ShowAuthorsDialog( this ).setVisible( true );
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

