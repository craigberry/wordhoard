package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import java.awt.event.*;

import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.lexicon.*;
import edu.northwestern.at.wordhoard.swing.tables.*;
import edu.northwestern.at.wordhoard.swing.tcon.*;

/**	WordHoard Calculator Windows Menu.
 */

public class WindowsMenu extends BaseMenu
{
	/**	The basic windows menu items.  */
	/**	Other items are added dynamically as windows open and close. */

	/**	Close window. */

	protected JMenuItem windowsMenuCloseWindowItem;

	/**	Close all windows. */

	protected JMenuItem windowsMenuCloseAllWindowsItem;

	/**	Table of contents. */

	protected JMenuItem tconMenuItem;

	/**	Word classes. */

	protected JMenuItem wordClassesMenuItem;

	/**	Parts of speech. */

	protected JMenuItem posMenuItem;

	/**	Display calculator window. */

	protected JMenuItem calculatorMenuItem;

	/**	Create windows menu.
	 */

	public WindowsMenu()
	{
		super
		(
			WordHoardSettings.getString( "windowsMenuName" , "Windows" )
		);
	}

	/**	Create windows menu.
	 *
	 *	@param	menuBar		The menu bar to which to attach the windows menu.
	 */

	public WindowsMenu( JMenuBar menuBar )
	{
		super
		(
			WordHoardSettings.getString( "windowsMenuName" , "Windows" ) ,
			menuBar
		);
	}

	/**	Create windows menu.
	 *
	 *	@param	parentWindow	Parent window for the menu.
	 */

	public WindowsMenu( AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "windowsMenuName" , "Windows" ) ,
			null ,
			parentWindow
		);
	}

	/**	Create windows menu.
	 *
	 *	@param	menuBar			The menu bar to which to attach the menu.
	 *	@param	parentWindow	Parent window for the menu.
	 */

	public WindowsMenu( JMenuBar menuBar , AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "windowsMenuName" , "Windows" ) ,
			menuBar ,
			parentWindow
		);
	}

	/**	Create the menu items.
	 */

	protected void createMenuItems()
	{
		windowsMenuCloseWindowItem =
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"windowsMenuCloseWindowItem" ,
					"Close window"
				) ,
				new GenericActionListener( "closeWindow" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_W ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
				true ,
				true
			);

		windowsMenuCloseAllWindowsItem =
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"windowsMenuCloseAllWindowsItem" ,
					"Close all windows"
				) ,
				new GenericActionListener( "closeAllWindows" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_W ,
					Env.MENU_SHORTCUT_SHIFT_KEY_MASK
				) ,
				true ,
				true
			);

		addSeparator();

		tconMenuItem		=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuTconItem" ,
					"Table of Contents"
				) ,
				new GenericActionListener( "tableOfContents" )
			);

		calculatorMenuItem	= new JMenuItem();
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuCalculatorItem" ,
					"WordHoard Calculator"
				) ,
				new GenericActionListener( "wordhoardCalculator" )
			);

		addSeparator();

		wordClassesMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuWordClassesItem" ,
					"Word Classes"
				) ,
				new GenericActionListener( "wordClasses" )
            );

		posMenuItem	= new JMenuItem();
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuPosMenuItem" ,
					"Parts of Speech"
				) ,
				new GenericActionListener( "partsOfSpeech" )
			);

		addSeparator();
								//	Retrieve the corpora and create
								//	a lexicon menu item for each.

		Corpus[] corpora	= CorpusUtils.getCorpora();

		PrintfFormat lexiconFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString( "corpusLexicon" , "%s Lexicon" )
			);

		for ( int i = 0 ; i < corpora.length ; i++ )
		{
			final Corpus corpus	= corpora[ i ];

			addMenuItem
			(
				lexiconFormat.sprintf( corpus.getTitle() ) ,
				new ActionListener()
				{
					public void actionPerformed( ActionEvent event )
					{
						try
						{
							showLexicon( corpus );
						}
						catch ( Exception e )
						{
							Err.err( e );
						}
					}
				}
			);
		}

		addSeparator();

		if ( menuBar != null ) menuBar.add( this );
	}

	/**	Close current window.
	 */

	protected void closeWindow()
	{
		if ( parentWindow != null )
		{
			parentWindow.dispatchEvent
			(
				new WindowEvent
				(
					parentWindow ,
					WindowEvent.WINDOW_CLOSING
				)
			);
		}
	}

	/**	Close all windows.
	 */

	protected void closeAllWindows()
	{
		WindowsMenuManager[] windows =
			WindowsMenuManager.getAllOpenWindows();

		for ( int i = 0 ; i < windows.length ; i++ )
		{
			WindowsMenuManager window = windows[ i ];

			if ( window instanceof TableOfContentsWindow )
			{
				continue;
            }
/*
            if ( window instanceof WordHoardCalculatorWindow )
            {
            	continue;
            }
*/
			window.dispatchEvent
			(
				new WindowEvent
				(
					window,
					WindowEvent.WINDOW_CLOSING
				)
			);
		}
	}

	/**	Display a lexicon for a specified corpus.
	 *
	 *	@param	corpus		The corpus.
	 *
	 *	@throws	Exception	general error.
	 */

	protected void showLexicon( Corpus corpus )
		throws Exception
	{
		LexiconWindow.open( corpus , parentWindow );
	}

	/**	Display table of contents.
	 *
	 *	@throws	Exception	if any exception occurs.
	 */

	protected void tableOfContents()
		throws Exception
	{
		TableOfContentsWindow.open( true );
	}

	/**	Display word classes.
	 *
	 *	@throws	Exception	if any exception occurs.
	 */

	protected void wordClasses()
		throws Exception
	{
		WordClassWindow.open( parentWindow );
	}

	/**	Display parts of speech.
	 *
	 *	@throws	Exception	if any exception occurs.
	 */

	protected void partsOfSpeech()
		throws Exception
	{
		PosWindow.open( parentWindow );
	}

	/**	Display calculator window.
	 */

	protected void wordhoardCalculator()
	{
		WordHoardCalculatorWindow.open( true );
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

