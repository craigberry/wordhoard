package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import java.awt.event.*;

import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	WordHoard Calculator Find Menu.
 */

public class FindMenu extends BaseMenu
{
	/**	The find menu items. */

	/**	Go to a specified word. */

	protected JMenuItem findMenuGoToWordItem;

	/**	Find lemmata query tool. */

	protected JMenuItem findMenuQueryToolItem;

	/**	Find words. */

	protected JMenuItem findMenuFindWordsItem;

	/**	Find works. */

	protected JMenuItem findMenuFindWorksItem;

	/**	Create find menu.
	 */

	public FindMenu()
	{
		super
		(
			WordHoardSettings.getString( "findMenuName" , "Find" )
		);
	}

	/**	Create find menu.
	 *
	 *	@param	menuBar		The menu bar to which to attach the find menu.
	 */

	public FindMenu( JMenuBar menuBar )
	{
		super
		(
			WordHoardSettings.getString( "findMenuName" , "Find" ) ,
			menuBar
		);
	}

	/**	Create find menu.
	 *
	 *	@param	parentWindow	The parent window for the menu.
	 */

	public FindMenu( AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "findMenuName" , "Find" ) ,
			parentWindow
		);
	}

	/**	Create find menu.
	 *
	 *	@param	menuBar			The menu bar to which to attach the menu.
	 *	@param	parentWindow	The parent window for the menu.
	 */

	public FindMenu( JMenuBar menuBar , AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "findMenuName" , "Find" ) ,
			menuBar ,
			parentWindow
		);
	}

	/**	Create the menu items.
	 */

	protected void createMenuItems()
	{
		findMenuGoToWordItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"findMenuGoToWordItem" ,
					"Go To Word..."
				) ,
				new GenericActionListener( "goToWord" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_G ,
					Env.MENU_SHORTCUT_KEY_MASK
				)
			);

		findMenuQueryToolItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"findMenuQueryToolItem" ,
					"Find Lemmata..."
				) ,
				new GenericActionListener( "queryTool" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_Y ,
					Env.MENU_SHORTCUT_KEY_MASK
				)
			);

		findMenuFindWordsItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"findMenuFindWordsItem" ,
					"Find Words..."
				) ,
				new GenericActionListener( "findWords" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_F ,
					Env.MENU_SHORTCUT_KEY_MASK
				)
			);

		findMenuFindWorksItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"findMenuFindWorksItem" ,
					"Find Works..."
				) ,
				new GenericActionListener( "findWorks" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_L ,
					Env.MENU_SHORTCUT_KEY_MASK
				)
			);


		if ( menuBar != null ) menuBar.add( this );
	}

	/**	Go to a word.
	 */

	protected void goToWord()
		throws Exception
	{
		parentWindow.handleGoToWordCmd( null );
	}

	/**	Find words.
	 */

	protected void findWords()
		throws Exception
	{
		parentWindow.handleFindWordsCmd();
	}

	/**	Find works.
	 */

	protected void findWorks()
		throws Exception
	{
		parentWindow.handleFindWorksCmd();
	}

	/**	Query tool.
	 */

	protected void queryTool()
		throws Exception
	{
		parentWindow.handleQueryToolCmd();
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

