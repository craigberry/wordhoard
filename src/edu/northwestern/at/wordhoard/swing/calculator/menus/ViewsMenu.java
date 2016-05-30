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
import edu.northwestern.at.utils.sys.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;

/**	WordHoard Calculator Views Menu.
 *
 *	<p>
 *	The Calculator window doesn't use this menu, so it is always
 *	disabled in the Calculator window.
 *	</p>
 */

public class ViewsMenu extends BaseMenu
{
	/**	The views menu items. */

	/**	No line numbers. */

	protected JMenuItem viewsMenuNoLineNumsItem;

	/**	Number every line. */

	protected JMenuItem viewsMenuAllLineNumsItem;

	/**	Number every fifth line. */

	protected JMenuItem viewsMenuFiveLineNumsItem;

	/**	Translations and transliterations. */

	protected JMenuItem viewsMenuTranslationsItem;

	/**	Show or hide annotation markers. */

	protected JMenuItem viewsMenuShowHideAnnotationMarkersItem;

	/**	Show orf hide annotation panel. */

	protected JMenuItem viewsMenuShowHideAnnotationPanelItem;

	/**	Create views menu.
	 */

	public ViewsMenu()
	{
		super
		(
			WordHoardSettings.getString( "viewsMenuName" , "Views" )
		);
	}

	/**	Create views menu.
	 *
	 *	@param	menuBar		The menu bar to which to attach the views menu.
	 */

	public ViewsMenu( JMenuBar menuBar )
	{
		super
		(
			WordHoardSettings.getString( "viewsMenuName" , "Views" ) ,
			menuBar
		);
	}

	/**	Create views menu.
	 *
	 *	@param	parentWindow	Parent window for the menu.
	 */

	public ViewsMenu( AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "viewsMenuName" , "Views" ) ,
			null ,
			parentWindow
		);
	}

	/**	Create views menu.
	 *
	 *	@param	menuBar			The menu bar to which to attach the menu.
	 *	@param	parentWindow	Parent window for the menu.
	 */

	public ViewsMenu( JMenuBar menuBar , AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "viewsMenuName" , "Views" ) ,
			menuBar ,
			parentWindow
		);
	}

	/**	Create the menu items.
	 */

	protected void createMenuItems()
	{
		viewsMenuNoLineNumsItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"viewsMenuNoLineNumsItem" ,
					"No Line Numbers"
				) ,
				BaseMenu.RADIOBUTTONMENUITEM ,
				new GenericActionListener( "noLineNumbers" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_0 ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
				true ,
				false
			);

		viewsMenuAllLineNumsItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"viewsMenuAllLineNumsItem" ,
					"All Lines Numbered"
				) ,
				BaseMenu.RADIOBUTTONMENUITEM ,
				new GenericActionListener( "allLinesNumbered" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_1 ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
				true ,
				false
			);

		viewsMenuFiveLineNumsItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"viewsMenuFiveLineNumsItem" ,
					"Number Every Fifth Line"
				) ,
				BaseMenu.RADIOBUTTONMENUITEM ,
				new GenericActionListener( "numberEveryFifthLine" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_5 ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
				true ,
				false
			);

		ButtonGroup lineNumsGroup	= new ButtonGroup();

		lineNumsGroup.add( viewsMenuNoLineNumsItem );
		lineNumsGroup.add( viewsMenuAllLineNumsItem );
		lineNumsGroup.add( viewsMenuFiveLineNumsItem );

		viewsMenuFiveLineNumsItem.setSelected( true );

		addSeparator();

		viewsMenuTranslationsItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"viewsMenuTranslationsItem" ,
					"Translations, Transliterations, Etc. ..."
				) ,
				new GenericActionListener( "translations" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_T ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
				true ,
				false
			);

		addSeparator();

		viewsMenuShowHideAnnotationMarkersItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"viewsMenuShowHideAnnotationMarkersItem" ,
					"Hide Annotation Markers"
				) ,
				new GenericActionListener( "hideAnnotationMarkers" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_J ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
				true ,
				false
			);

		viewsMenuShowHideAnnotationPanelItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"viewsMenuShowHideAnnotationPanelItem" ,
					"Show Annotation Panel"
				) ,
				new GenericActionListener( "showAnnotationPanel" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_H ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
				true ,
				false
			);


		if ( menuBar != null ) menuBar.add( this );
	}

	/**	Don't show any line numbers.
	 */

	protected void noLineNumbers()
		throws Exception
	{
//		getTableOfContentsWindow().handleLineNumberCmd( 0 );
	}

	/**	Number every line.
	 */

	protected void allLinesNumbered()
		throws Exception
	{
//		getTableOfContentsWindow().handleLineNumberCmd( 1 );
	}

	/**	Number every fifth line.
	 */

	protected void numberEveryFifthLine()
		throws Exception
	{
//		getTableOfContentsWindow().handleLineNumberCmd( 5 );
	}

	/**	Translation and transliterations.
	 */

	protected void translations()
		throws Exception
	{
//		getTableOfContentsWindow().handleTranslationsCmd();
	}

	/**	Hide annotation markers.
	 */

	protected void hideAnnotationMarkers()
		throws Exception
	{
//		getTableOfContentsWindow().handleShowHideAnnotationMarkersCmd();
	}

	/**	Show annotation panel.
	 */

	protected void showAnnotationPanel()
		throws Exception
	{
//		getTableOfContentsWindow().handleShowHideAnnotationPanelCmd();
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

