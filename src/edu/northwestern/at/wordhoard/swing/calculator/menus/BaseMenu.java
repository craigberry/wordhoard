package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import bsh.*;
import bsh.util.*;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.text.*;

import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.tcon.*;

public class BaseMenu extends JMenu
{
	/**	The menu name.
	 */

	protected String menuName	= "";

	/**	The menu bar to which this menu belongs.
	 */

	protected JMenuBar menuBar	= null;

	/**	The window to which this menu is attached. */

	protected AbstractWindow parentWindow	= null;

	/**	Menu item types supported here. */

	public static final int	TEXTMENUITEM		= 0;
	public static final int RADIOBUTTONMENUITEM	= 1;

	/**	Create a menu.
	 *
	 *	@param	menuName		The menu name.
	 *	@param	menuBar			The menu bar to which to attach this menu.
	 *	@param	parentWindow	The parent window of this menu.
	 */

	public BaseMenu
	(
		String menuName ,
		JMenuBar menuBar ,
		AbstractWindow parentWindow
	)
	{
		super( menuName );

		this.menuName		= menuName;
		this.menuBar		= menuBar;
		this.parentWindow	= parentWindow;

		createMenuItems();
	}

	/**	Create a menu.
	 *
	 *	@param	menuName		The menu name.
	 *	@param	menuBar			The menu bar to which to attach this menu.
	 */

	public BaseMenu( String menuName , JMenuBar menuBar )
	{
		super( menuName );

		this.menuName		= menuName;
		this.menuBar		= menuBar;
		this.parentWindow	= null;

		createMenuItems();
	}

	/**	Create a menu.
	 *
	 *	@param	menuName		The menu name.
	 *	@param	parentWindow	Parent window for menu.
	 */

	public BaseMenu( String menuName , AbstractWindow parentWindow )
	{
		super( menuName );

		this.menuName		= menuName;
		this.menuBar		= null;
		this.parentWindow	= parentWindow;

		createMenuItems();
	}

	/**	Create a menu.
	 *
	 *	@param	menuName	The menu name.
	 */

	public BaseMenu( String menuName )
	{
		super( menuName );

		this.menuName		= menuName;
		this.menuBar		= null;
		this.parentWindow	= null;

		createMenuItems();
	}

	/**	Create the menu items.  Override in subclasses.
	 */

	protected void createMenuItems()
	{
	}

	/** Add a menu item to a menu.
	 *
	 *	@param	menu				Menu to which to add menu item.
	 *	@param	menuItemName		The name of the menu item.
	 *	@param	menuItemType		Menu item type.
	 *								= TEXTMENUITEM: ordinary menu item
	 *								= RADIOBUTTONMENUITEM: radio button
	 *	@param	actionListener		The action listener for the menu item.
	 *	@param	acceleratorKey		Accelerator for menu item.
	 *	@param	visible				True if menu item is initially visible.
	 *	@param	enabled				True if menu item is initially enabled.
	 *
	 *	@return						JMenuItem for the menu item.
	 */

	protected JMenuItem addMenuItem
	(
		JMenu menu ,
		String menuItemName ,
		int menuItemType ,
		ActionListener actionListener ,
		KeyStroke acceleratorKey ,
		boolean visible ,
		boolean enabled
	)
	{
		JMenuItem menuItem;

		switch ( menuItemType )
		{
			case RADIOBUTTONMENUITEM	:
				menuItem	= new JRadioButtonMenuItem( menuItemName );
				break;

			default:
				menuItem	= new JMenuItem( menuItemName );
        }

		if ( actionListener != null )
		{
			menuItem.addActionListener( actionListener );
		}

		if ( acceleratorKey != null )
		{
			menuItem.setAccelerator( acceleratorKey );
		}

		menuItem.setVisible( visible );
		menuItem.setEnabled( enabled );

		if ( menu != null ) menu.add( menuItem );

		return menuItem;
	}

	/** Add a menu item to a menu.
	 *
	 *	@param	menu				Menu to which to add menu item.
	 *	@param	menuItemName		The name of the menu item.
	 *	@param	actionListener		The action listener for the menu item.
	 *	@param	acceleratorKey		Accelerator for menu item.
	 *	@param	visible				True if menu item is initially visible.
	 *	@param	enabled				True if menu item is initially enabled.
	 *
	 *	@return						JMenuItem for the menu item.
	 */

	protected JMenuItem addMenuItem
	(
		JMenu menu ,
		String menuItemName ,
		ActionListener actionListener ,
		KeyStroke acceleratorKey ,
		boolean visible ,
		boolean enabled
	)
	{
		return
			addMenuItem
			(
				menu ,
				menuItemName ,
				TEXTMENUITEM ,
				actionListener ,
				acceleratorKey ,
				visible ,
				enabled
			);
	}

	/** Add a menu item to a menu.
	 *
	 *	@param	menuItemName		The name of the menu item.
	 *	@param	menuItemType		Menu item type.
	 *								= TEXTMENUITEM: ordinary menu item
	 *								= RADIOBUTTONMENUITEM: radio button
	 *	@param	actionListener		The action listener for the menu item.
	 *	@param	acceleratorKey		Accelerator for menu item.
	 *	@param	visible				True if menu item is initially visible.
	 *	@param	enabled				True if menu item is initially enabled.
	 *
	 *	@return						JMenuItem for the menu item.
	 */

	protected JMenuItem addMenuItem
	(
		String menuItemName ,
		int menuItemType ,
		ActionListener actionListener ,
		KeyStroke acceleratorKey ,
		boolean visible ,
		boolean enabled
	)
	{
		return
			addMenuItem
			(
				this ,
				menuItemName ,
				menuItemType ,
				actionListener ,
				acceleratorKey ,
				visible ,
				enabled
			);
	}

	/** Add a menu item to a menu.
	 *
	 *	@param	menuItemName		The name of the menu item.
	 *	@param	actionListener		The action listener for the menu item.
	 *	@param	acceleratorKey		Accelerator for menu item.
	 *	@param	visible				True if menu item is initially visible.
	 *	@param	enabled				True if menu item is initially enabled.
	 *
	 *	@return						JMenuItem for the menu item.
	 */

	protected JMenuItem addMenuItem
	(
		String menuItemName ,
		ActionListener actionListener ,
		KeyStroke acceleratorKey ,
		boolean visible ,
		boolean enabled
	)
	{
		return
			addMenuItem
			(
				this ,
				menuItemName ,
				TEXTMENUITEM ,
				actionListener ,
				acceleratorKey ,
				visible ,
				enabled
			);
	}

	/** Add a menu item to a menu.
	 *
	 *	@param	menu				Menu to which to add menu item.
	 *	@param	menuItemName	The name of the menu item.
	 *	@param	actionListener	The action listener for the menu item.
	 *	@param	acceleratorKey	Accelerator key for menu item.
	 *
	 *	@return					JMenuItem for the menu item.
	 */

	protected JMenuItem addMenuItem
	(
		JMenu menu ,
		String menuItemName ,
		ActionListener actionListener ,
		KeyStroke acceleratorKey
	)
	{
		return addMenuItem
		(
			menu ,
			menuItemName ,
			TEXTMENUITEM ,
			actionListener ,
			acceleratorKey ,
			true ,
			true
		);
	}

	/** Add a menu item to a menu.
	 *
	 *	@param	menuItemName	The name of the menu item.
	 *	@param	actionListener	The action listener for the menu item.
	 *	@param	acceleratorKey	Accelerator key for menu item.
	 *
	 *	@return					JMenuItem for the menu item.
	 */

	protected JMenuItem addMenuItem
	(
		String menuItemName ,
		ActionListener actionListener ,
		KeyStroke acceleratorKey
	)
	{
		return addMenuItem
		(
			this ,
			menuItemName ,
			TEXTMENUITEM ,
			actionListener ,
			acceleratorKey ,
			true ,
			true
		);
	}

	/** Add a menu item to a menu.
	 *
	 *	@param	menu			Menu to which to add menu item.
	 *	@param	menuItemName	The name of the menu item.
	 *	@param	actionListener	The action listener for the menu item.
	 *
	 *	@return					JMenuItem for the menu item.
	 */

	protected JMenuItem addMenuItem
	(
		JMenu menu ,
		String menuItemName ,
		ActionListener actionListener
	)
	{
		return addMenuItem
		(
			menu ,
			menuItemName ,
			TEXTMENUITEM ,
			actionListener ,
			null ,
			true ,
			true
		);
	}

	/** Add a menu item to a menu.
	 *
	 *	@param	menuItemName	The name of the menu item.
	 *	@param	actionListener	The action listener for the menu item.
	 *
	 *	@return					JMenuItem for the menu item.
	 */

	protected JMenuItem addMenuItem
	(
		String menuItemName ,
		ActionListener actionListener
	)
	{
		return addMenuItem
		(
			this ,
			menuItemName ,
			TEXTMENUITEM ,
			actionListener ,
			null ,
			true ,
			true
		);
	}

	/**	Close the current thread's persistence manager.
	 */

	protected void closePersistenceManager()
	{
		getCalculatorWindow().closePersistenceManager();
	}

	/**	Create a progress panel for displaying a progress report.
	 *
	 *	@param		title		Title for the progress report.
	 *	@param		initLabel	The initial label for the progress report.
	 *
	 *	@return		OutputResults object with progress panel and
	 *				close button.
	 */

	protected OutputResults createProgressPanel
	(
		String title ,
		String initLabel
	)
	{
								//	Get name for tabbed pane to hold
								//	the results of this analysis.

		String outputTitle			= getNextOutputWindowTitle();

								//	Create a panel to hold progress panel.

		DialogPanel panel			= new DialogPanel();

		ProgressPanel progressPanel	= new ProgressPanel( title , initLabel );

		panel.add( progressPanel );

								//	Add cancel button.  Will be changed
								//	to a close button when the analysis
								//	completes.
/*
		JButton closeButton	= addACloseButton( panel , outputTitle );

		closeButton.setEnabled( false );
*/
		JButton closeButton	= addACancelButton( panel , progressPanel );

								//	Add panel to tabbed pane.

		WordHoardTabbedPane mainTabbedPane	= getMainTabbedPane();

		mainTabbedPane.add( outputTitle , panel );

								//	Make it the currently selected pane.

		mainTabbedPane.setSelectedIndex(
			mainTabbedPane.indexOfTab( outputTitle ) );

								//	Make sure the main pane gets redrawn.

		mainTabbedPane.paintImmediately( mainTabbedPane.getVisibleRect() );

		return new OutputResults( panel , progressPanel , closeButton );
	}

	/**	Display error message dialog.
	 *
	 *	@param	errorMessage		The error message text.
	 */

	protected void displayErrorMessage( final String errorMessage )
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					new ErrorMessage
					(
						errorMessage ,
						getCalculatorWindow()
					);
				}
			}
		);
	}

	/**	Get Calculator's main window.
	 *
	 *	@return		The Calculator's main window.
	 */

	protected WordHoardCalculatorWindow getCalculatorWindow()
	{
		return WordHoardCalculatorWindow.getCalculatorWindow();
	}

	/**	Get the interpreter console.
	 *
	 *	@return		Interpreter console.
	 */

	public JConsole getConsole()
	{
		return getCalculatorWindow().getConsole();
	}

	/**	Get the edit menu.
	 *
	 *	@return		The edit menu.
	 */

	public EditMenu getEditMenu()
	{
		return getCalculatorWindow().getEditMenu();
	}

	/**	Gets the currently focused text component.
	 *
	 *	@param	window		The window in which to locate the text
	 *						component.
	 *
	 *	@return				The currently focused text component,
	 *						or null if none.
	 */

	protected JTextComponent getFocusedTextComponent( Window window )
	{
		JTextComponent result		= null;

		KeyboardFocusManager kfm	=
			KeyboardFocusManager.getCurrentKeyboardFocusManager();

		Component c	= kfm.getPermanentFocusOwner();

		if	(	( c != null ) &&
				( c instanceof JTextComponent ) &&
				( (JTextComponent)c).getTopLevelAncestor() == window )
		{
			result	= (JTextComponent)c;
		}

		return result;
	}

	/**	Get the interpreter's input text pane.
	 *
	 *	@return		The intepreter's input text pane.
	 */

	public JTextPane getInputTextPane()
	{
		return getCalculatorWindow().getInputTextPane();
	}

	/**	Get the interpreter.
	 *
	 *	@return		The main script interpreter.
	 */

	public Interpreter getInterpreter()
	{
		return getCalculatorWindow().getInterpreter();
	}

	/**	Get Calculator's main tabbed pane.
	 *
	 *	@return		The Calculator's main tabbed pane.
	 */

	protected WordHoardTabbedPane getMainTabbedPane()
	{
		return getCalculatorWindow().getMainTabbedPane();
	}

	/**	Get next output panel title.
	 *
	 *	@param		show	True to ensure calculator window is visible.
	 *
	 *	@return		Title for next output window panel.
	 */

	protected String getNextOutputWindowTitle( boolean show )
	{
		return getCalculatorWindow().getNextOutputWindowTitle( show );
	}

	/**	Get next output panel title.
	 *
	 *	@return		Title for next output window panel.
	 *
	 *	<p>
	 *	Calculator window is also made visible.
	 *	</p>
	 */

	protected String getNextOutputWindowTitle()
	{
		return getNextOutputWindowTitle( true );
	}

	/**	Extract results panel from body of a dialog panel.
	 */

	public static ResultsPanel getResultsPanel( Component component )
	{
		ResultsPanel result	= null;

		if ( component instanceof ResultsPanel )
		{
			result	= (ResultsPanel)component;
		}
		else if ( component instanceof DialogPanel )
		{
			DialogPanel dialogPanel	= (DialogPanel)component;

			Component[] components	= dialogPanel.getBody().getComponents();

			for ( int i = 0 ; i < components.length ; i++ )
			{
				if ( components[ i ] instanceof ResultsPanel )
				{
					result	= (ResultsPanel)components[ i ];
					break;
				}
				else
				{
					result	= getResultsPanel( components[ i ] );
					if ( result != null ) break;
				}
			}
		}
		else if ( component instanceof Container )
		{
			Container container	= (Container)component;

			Component[] components	= container.getComponents();

			for ( int i = 0 ; i < components.length ; i++ )
			{
				if ( components[ i ] instanceof ResultsPanel )
				{
					result	= (ResultsPanel)components[ i ];
					break;
				}
				else
				{
					result	= getResultsPanel( components[ i ] );
					if ( result != null ) break;
				}
			}
		}

		return result;
	}

	/**	Get Reader's table of contents window.
	 */

	protected TableOfContentsWindow getTableOfContentsWindow()
		throws Exception
	{
		return TableOfContentsWindow.getTableOfContentsWindow();
	}

	/**	Handle menu changes when logging in.  Override in subclasses.
	 */

	public void handleLogin()
	{
	}

	/**	Handle menu changes when logging out.  Override in subclasses.
	 */

	public void handleLogout()
	{
	}

	/**	Handle menu changes when logged-in status changes.
	 *
	 *	@param	loggedIn	true if logged in, false otherwise.
	 */

	public void handleLoggedIn( boolean loggedIn )
	{
		if ( loggedIn )
		{
			handleLogin();
		}
		else
		{
			handleLogout();
		}
	}

	/**	Perform an action.
	 *
	 *	@param	event			The action event.
	 *	@param	methodName		The name of the method to invoke.
	 *
	 *	<p>
	 *	Calls the specified method using reflection on the Swing
	 *	event thread.  This ensures that an action handler for a
	 *	menu item does not end up stacked deeply which can cause
	 *	problems when the action handler takes a long time to execute.
	 *	The method called must either take no argument or a single
	 *	ActionEvent argument.
	 *	</p>
	 */

	protected void performAction
	(
		final ActionEvent event ,
		final String methodName
	)
	{
								//	Get class containing method to invoke.

		final Class actionClass		= this.getClass();

								//	Get instance of class.

		final Object actionInstance	= this;

								//	Create runnable which will be passed
								//	to AWT event thread for execution.

		final Runnable runnable =
			new Runnable()
			{
				public void run()
   				{
   								//	Try calling specified method
   								//	with no arguments first.

   					Object[] argVals	= new Object[]{};
					Class[]	argTypes	= new Class[]{};

					boolean	calledOK	= false;

   					try
   					{
   						Method method	=
   							actionClass.getDeclaredMethod
   							(
   								methodName ,
   								argTypes
   							);

						if ( method != null )
						{
							method.invoke( actionInstance , argVals );
							calledOK	= true;
						}
					}
								//	No such method?  Try again by
								//	passing our ActionEvent parameter.

					catch ( NoSuchMethodException e )
					{
	   					argVals		= new Object[]{ event };
						argTypes	= new Class[]{ ActionEvent.class };

	   					try
   						{
   							Method method	=
   								actionClass.getDeclaredMethod(
   									methodName , argTypes );

							if ( method != null )
							{
								method.invoke( actionInstance , argVals );
							}
						}
								//	Display and log any error.

						catch ( Exception e3 )
						{
							Err.err( e3 );
						}
					}
								//	Display and log any other errors.

					catch ( Exception e2 )
					{
						Err.err( e2 );
					}
   				}
   			};
								//	Schedule the event handler for
								//	later execution.  Meanwhile this
								//	original event handler returns,
								//	avoiding problems with deeply nested
								//	event handler calls.

		SwingUtilities.invokeLater( runnable );
	}

	/**	Perform an action.
	 *
	 *	@param	event			The action event.
	 *	@param	actionListener	The action listener.
	 *
	 *	<p>
	 *	Calls the specified action method in the actionListener.
	 *	</p>
	 */

	protected void performAction
	(
		final ActionEvent event ,
		final ActionListener actionListener
	)
	{
		final Runnable runnable =
			new Runnable()
			{
				public void run()
   				{
   					try
   					{
						actionListener.actionPerformed( event );
					}
					catch ( Exception e )
					{
						Err.err( e );
					}
				}
			};

		SwingUtilities.invokeLater( runnable );
	}

	/**	Set the busy cursor.
	 */

	protected void setBusyCursor()
	{
		getCalculatorWindow().setBusyCursor();
	}

	/**	Set the default cursor.
	 */

	protected void setDefaultCursor()
	{
		getCalculatorWindow().setDefaultCursor();
	}

	/**	Generic action listener that invokes action methods using reflection.
	 */

	protected class GenericActionListener
		implements ActionListener
	{
		/**	Action method name.
		 */

		protected String actionMethodName;

		/**	Create generic action listener.
		 *
		 *	@param	actionMethodName	The action method name.
		 */

		GenericActionListener( String actionMethodName )
		{
			this.actionMethodName	= actionMethodName;
		}

		/**	Create generic action listener.
		 *
		 *	@param	actionMethod	The action method.
		 */

		GenericActionListener( Method actionMethod )
		{
			this.actionMethodName	= actionMethod.getName();
		}

		/**	Perform action.
		 *
		 *	@param	event	Event to perform.
		 */

		public void actionPerformed( ActionEvent event )
		{
			BaseMenu.this.performAction
			(
				event ,
				actionMethodName
			);
		}
	}

	/**	Display "not yet implemented" message box.
	 */

	protected void notYetImplemented()
	{
		new InformationMessage
		(
			WordHoardSettings.getString
			(
				"NotYetImplemented" ,
				"Not yet implemented."
			) ,
			getCalculatorWindow()
		);
	}

    /** Add a close button to a dialog panel.
     *
     *	@param	panel		Dialog panel to which to add close button.
     *	@param	outputTitle	Title of tabbed panel holding dialog panel.
     */

	protected JButton addACloseButton
	(
		DialogPanel panel ,
		final String outputTitle
	)
	{
		return getCalculatorWindow().addACloseButton( panel , outputTitle );
	}

    /** Add a cancel button to a progress panel.
     *
     *	@param	panel			Dialog panel to which to add close button.
     *	@param	progressPanel	Progress panel to which to tie cancel action.
     */

	public JButton addACancelButton
	(
		DialogPanel panel ,
		final ProgressPanel progressPanel
	)
	{
								//	Add cancel button.

		return getCalculatorWindow().addACancelButton( panel , progressPanel );
	}

    /** Change cancel button to a close button.
     *
     *	@param	button		The cancel button to change to a close button.
     *	@param	component	Component in tabbed panel holding button.
     */

	public void cancelToClose
	(
		JButton button ,
		final Component component
	)
	{
		getCalculatorWindow().cancelToClose( button , component );
	}

	/**	Holds output results.
	 */

	protected class OutputResults
	{
		/**	The output panel.
		 */

		protected DialogPanel outputPanel;

		/**	The progress reporter.
		 */

		protected ProgressPanel progressReporter;

		/**	The close button.
		 */

		protected JButton closeButton;

		/**	Create an OutputResults object.
		 *
		 *	@param	outputPanel			The output panel.
		 *	@param	progressReporter	The progress reported object.
		 *	@param	closeButton			The close button.
		 */

		protected OutputResults
		(
			DialogPanel outputPanel ,
			ProgressPanel progressReporter ,
			JButton closeButton
		)
		{
			this.outputPanel		= outputPanel;
			this.progressReporter	= progressReporter;
			this.closeButton		= closeButton;
		}

		/**	Get the output panel.
		 *
		 *	@return		The output panel.
		 */

		protected DialogPanel getOutputPanel()
		{
			return outputPanel;
		}

		/**	Get the progress reporter.
		 *
		 *	@return		The progress reporter.
		 */

		protected ProgressPanel getProgressReporter()
		{
			return progressReporter;
		}

		/**	Get the close button.
		 *
		 *	@return		The close button.
		 */

		protected JButton getCloseButton()
		{
			return closeButton;
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

