package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.net.URL;
import java.io.*;
import java.lang.reflect.*;

import javax.help.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.printing.*;
import edu.northwestern.at.utils.sys.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.server.model.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.accounts.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.dialogs.*;

import org.w3c.dom.Document;

/**	WordHoard Calculator Files Menu.
 */

public class FileMenu extends BaseMenu
{
	/**	The file menu items. */

	/**	About box. */

	protected JMenuItem aboutMenuItem;

	/**	New work set. */

    protected JMenuItem newWorkSetMenuItem;

	/**	Oprn work set. */

    protected JMenuItem openWorkSetMenuItem;

	/**	Save work set. */

    protected JMenuItem saveWorkSetMenuItem;

	/**	Save as word set. */

    protected JMenuItem saveAsWordSetMenuItem;

	/**	Save current item as file. */

	protected JMenuItem saveAsMenuItem;

	/**	Printer page setup. */

	protected JMenuItem pageSetupMenuItem;

	/**	Print preview. */

	protected JMenuItem printPreviewMenuItem;

	/**	Print. */

	protected JMenuItem printMenuItem;

	/**	Export user data objects. */

	protected JMenuItem exportMenuItem;

	/**	Import user data objects. */

	protected JMenuItem importMenuItem;

	/**	Font preferences. */

	protected JMenuItem fontPreferencesMenuItem;

	/**	Look and feel preference. */

	protected JMenuItem lookAndFeelMenuItem;

	/**	Send an error report. */

	protected JMenuItem sendErrorReportMenuItem;

	/**	Get info. */

	protected JMenuItem getInfoMenuItem;

	/**	Annotate. */

	protected JMenuItem annotateMenuItem;

	/**	Login to WordHoard server. */

	protected JMenuItem loginMenuItem;

	/**	Logout from WordHoard server. */

	protected JMenuItem logoutMenuItem;

	/**	Open notepad editor. */

	protected JMenuItem editorMenuItem;

	/**	Run a script. */

	protected JMenuItem runScriptMenuItem;

	/**	Manage accounts. */

	protected JMenuItem manageAccountsMenuItem;

	/**	Quit. */

	protected JMenuItem quitMenuItem;

	/**	Create file menu.
	 *
	 *	@param	menuBar			The menu bar to which to attach the menu.
	 *	@param	parentWindow	Parent window for menu bar.
	 */

	public FileMenu( JMenuBar menuBar , AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "fileMenuName" , "File" ) ,
			menuBar ,
			parentWindow
		);
	}

	/**	Create the menu items.
	 */

	protected void createMenuItems()
	{
		boolean userIDDefined	=
			( WordHoardSettings.getUserID() != null );

								//	Add "About WordHoard" for Mac OS X.
								//	About goes in the Help menu for other
								//	systems.

		aboutMenuItem	= null;

		if ( Env.MACOSX && !WordHoardSettings.getUseScreenMenuBar() )
		{
			aboutMenuItem =
				addMenuItem
				(
					WordHoardSettings.getString
					(
						"helpMenuAboutItem" ,
						"About"
					) ,
					new GenericActionListener( "about" )
				);

			addSeparator();
		}

		newWorkSetMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"fileMenuNewWorkSetItem" ,
					"New Work Set"
				) ,
				new GenericActionListener( "newWorkSet" ) ,
				null ,
				true ,
				true
			);

		openWorkSetMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"fileMenuOpenWorkSetItem" ,
					"Open Work Set..."
				) ,
				new GenericActionListener( "openWorkSet" ) ,
				null ,
				true ,
				true
			);

		saveWorkSetMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"fileMenuSaveWorkSetItem" ,
					"Save Work Set..."
				) ,
				new GenericActionListener( "saveWorkSet" ) ,
				null ,
				true ,
				false
			);

		saveAsWordSetMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"fileMenuSaveAsWordSetItem" ,
					"Save as Word Set..."
				) ,
				new GenericActionListener( "saveAsWordSet" ) ,
				null ,
				true ,
				false
			);

		addSeparator();

		saveAsMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"fileMenuSaveAsItem" ,
					"Save as..."
				) ,
				new GenericActionListener( "saveAs" )
			);

		addSeparator();

		pageSetupMenuItem	=
			addMenuItem(
				WordHoardSettings.getString(
					"fileMenuPageSetupItem" , "Page setup..." ) ,
				new GenericActionListener( "doPageSetup" ) );

		printPreviewMenuItem	=
			addMenuItem(
				WordHoardSettings.getString(
					"fileMenuPrintPreviewItem" , "Print preview..." ) ,
				new GenericActionListener( "doPrintPreview" ) );

								//	Print preview is not displayed on
								//  Mac OS X.

		if ( Env.MACOSX ) printPreviewMenuItem.setVisible( false );

		printMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"fileMenuPrintItem" ,
					"Print..."
				) ,
				new GenericActionListener( "doPrint" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_P ,
					Env.MENU_SHORTCUT_KEY_MASK
				)
			);

								//	Import and export for user-defined objects.
		addSeparator();

		exportMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString(
					"fileMenuExportItem" , "Export..." ) ,
				new GenericActionListener( "doExport" )
			);

		exportMenuItem.setEnabled( userIDDefined );

		importMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString(
					"fileMenuImportItem" , "Import..." ) ,
				new GenericActionListener( "doImport" )
			);

		importMenuItem.setEnabled( userIDDefined );

		addSeparator();
								//	Editor.
		editorMenuItem =
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuEditorItem" ,
					"Editor"
				) ,
				new GenericActionListener( "simpleEditor" )
			);
                                //	Run a script.
		runScriptMenuItem =
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuRunScriptItem" ,
					"Run Script..."
				) ,
				new GenericActionListener( "runScript" )
			);

		addSeparator();
								//	Font preferences.

		boolean needSeparator	= false;

		if ( !Env.MACOSX )
		{
			fontPreferencesMenuItem	=
				addMenuItem
				(
					WordHoardSettings.getString
					(
						"wordhoardMenuFontPreferencesItem" ,
						"Font preferences..."
					) ,
					new GenericActionListener( "doFontPreferences" )
				);

				needSeparator	= true;
			}
								//	Look and feel preferences.
								//	Not displayed on the Mac or Windows.

		if ( !Env.MACOSX && !Env.WINDOWSOS )
		{
			lookAndFeelMenuItem	= new LookAndFeelSubMenu();

			add( lookAndFeelMenuItem );

			needSeparator	= true;
		}

		if ( needSeparator ) addSeparator();

								//	Send error report.

		sendErrorReportMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuSendErrorReportItem" ,
					"Send error report..."
				) ,
				new GenericActionListener( "doSendErrorReport" )
			);

		addSeparator();
		                    	//	Get word info menu item.

		getInfoMenuItem			=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuGetInfo" ,
					"Get Info"
				) ,
				new GenericActionListener( "getInfo" ) ,
				KeyStroke.getKeyStroke
				(
					KeyEvent.VK_I ,
					Env.MENU_SHORTCUT_KEY_MASK
				) ,
				true ,
				false
			);
		                    	//	Annotate.

		annotateMenuItem			=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuAnnotate" ,
					"Annotate"
				) ,
				new GenericActionListener( "annotate" ) ,
				null ,
				true,
//				( WordHoardSettings.getAnnotationWriteServerURL() != null ) ,
				false
			);

		addSeparator();
								//	Login and Logout.
		loginMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuLoginItem" ,
					"Login..."
				) ,
				new GenericActionListener( "login" )
			);

		PrintfFormat logoutMenuItemFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuLogoutItem" ,
					"Logout %s"
				)
			);

		String logoutMenuItemText	=
			logoutMenuItemFormat.sprintf
			(
				userIDDefined ?
					WordHoardSettings.getUserID() : ""
			);

		logoutMenuItem	=
			addMenuItem(
				logoutMenuItemText ,
				new GenericActionListener( "logout" ) );

//		loginMenuItem.setVisible( !userIDDefined );
		loginMenuItem.setVisible( true );
		loginMenuItem.setEnabled( !userIDDefined );

//		logoutMenuItem.setVisible( userIDDefined );
		logoutMenuItem.setVisible( true );
		logoutMenuItem.setEnabled( userIDDefined );

								//	Manage accounts.

		addSeparator();

		manageAccountsMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuManageAccountsItem" ,
					"Manage Accounts"
				) ,
				new GenericActionListener( "manageAccounts" )
			);

		manageAccountsMenuItem.setEnabled( false );

								//	Exit is not displayed on Mac OS X.
								//	The main application name menu has
								//	the quit item instead.

								//	$$$PIB$$$ Added back for all systems
								//	for consistency with other windows.

		if (!Env.MACOSX || !WordHoardSettings.getUseScreenMenuBar()) {
	
			addSeparator();
	
			quitMenuItem	=
				addMenuItem
				(
					WordHoardSettings.getString
					(
						"wordhoardMenuQuitItem" ,
						"Exit"
					) ,
					new GenericActionListener( "quit" ) ,
					KeyStroke.getKeyStroke
					(
						KeyEvent.VK_Q ,
						Env.MENU_SHORTCUT_KEY_MASK
					)
				);
	
			quitMenuItem.setVisible( true );
		
		}

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

	/** Perform print preview. */

	public void doPageSetup()
	{
		if ( parentWindow != null ) parentWindow.handlePageSetupCmd();
	}

	/** Perform print preview. */

	public void doPrintPreview()
	{
		if ( parentWindow != null ) parentWindow.handlePrintPreviewCmd();
	}

	/**	Print currently selected tabbed panel. */

	public void doPrint()
	{
		if ( parentWindow != null ) parentWindow.handlePrintCmd();
	}

	/**	Export selected objects to XML file.
	 *
	 *	@param	exportDialog	The export dialog.
	 *	@param	outputResults		The output panel.
	 */

	public void doExportThread
	(
		final ExportDialog exportDialog ,
		OutputResults outputResults
	)
	{
								//	Get the output panel and close button.

		final DialogPanel panel		= outputResults.getOutputPanel();
		final JButton closeButton	= outputResults.getCloseButton();

								//	Get the progress reporter.

		ProgressReporter progressReporter	=
			outputResults.getProgressReporter();

								//	Enable the busy cursor.
		setBusyCursor();
								//	Get the selected items.

		UserDataObject[] objectsToExport	=
			exportDialog.getSelectedItems();

								//	Create boolean result array.

		boolean[] exportedOK		= new boolean[ objectsToExport.length ];

								//	Export the objects.

		boolean exportSucceeded		= false;
		String exportErrorMessage	= "";

		try
		{
			exportSucceeded	=
				ExportUtils.exportObjects
				(
					objectsToExport ,
					parentWindow ,
					exportedOK ,
					progressReporter
				);
        }
        catch ( ExportException e )
        {
        	exportErrorMessage	= e.getMessage();
        }
								//	Enable the default cursor.
		setDefaultCursor();
            					//	Close persistence manager for thread.

   		getCalculatorWindow().closePersistenceManager();

								//	Enable the close button.

   		cancelToClose( closeButton , panel );

   								//	Report on success or failure of export.

		if ( exportSucceeded )
		{
			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Exportsucceeded" ,
					"Export succeeded."
				)
			);

			progressReporter.setLabelColor( Color.GREEN );
		}
		else
		{
			progressReporter.updateProgress
			(
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"Exportfailed" ,
						"Export failed: %s"
					)
				).sprintf( exportErrorMessage )
			);

			progressReporter.setLabelColor( Color.RED );
		}
								//	Close progress reporter.

		progressReporter.close();
    }

	/**	Display export dialog.
	 */

	public void doExport()
	{
		ExportDialog dialog	=
			new ExportDialog
			(
				WordHoardSettings.getString
				(
					"exportDialogTitle" ,
					"Export To XML"
				) ,
				parentWindow
			);

		dialog.show( parentWindow );

		if ( !dialog.getCancelled() )
		{
			final ExportDialog finalDialog	= dialog;

			final OutputResults outputResults	=
            	createProgressPanel
            	(
					WordHoardSettings.getString
					(
						"Exporting" ,
						"Exporting"
					) ,
					WordHoardSettings.getString
					(
						"Exportingdot" ,
						"Exporting ..."
					)
            	);

			Thread runner = new Thread( "Export" )
			{
				public void run()
				{
					doExportThread( finalDialog , outputResults );
				}
			};

			Thread awtEventThread	= SwingUtils.getAWTEventThread();

			if ( awtEventThread != null )
			{
				ThreadUtils.setPriority(
					runner , awtEventThread.getPriority() - 1 );
			}

			runner.start();
		}
	}

	/**	Import selected objects from XML file.
	 *
	 *	@param	importDialog		The import dialog.
	 *	@param	outputResults		The output panel.
	 */

	public void doImportThread
	(
		final ImportDialog importDialog ,
		OutputResults outputResults
	)
	{
								//	Get the output panel and close button.

		final DialogPanel panel		= outputResults.getOutputPanel();
		final JButton closeButton	= outputResults.getCloseButton();

								//	Get the progress reporter.

		ProgressReporter progressReporter	=
			outputResults.getProgressReporter();

								//	Enable the busy cursor.
		setBusyCursor();
								//	Get the selected items.

		UserDataObject[] objectsToImport	=
			importDialog.getSelectedItems();

								//	Should we should rename duplicate
								//	import items?

		boolean renameDuplicates	= importDialog.getRenameDuplicates();

								//	Create boolean result array.

		boolean[] importOK		= new boolean[ objectsToImport.length ];

								//	Create array holding current object
								//	names.  We need this to detect objects
								//	renamed during the import process.

		String[] currentNames	= new String[ objectsToImport.length ];

		for ( int i = 0 ; i < objectsToImport.length ; i++ )
		{
			currentNames[ i ]	= objectsToImport[ i ].getTitle();
		}
								//	import the objects.

		boolean importSucceeded		= false;
		String importErrorMessage	= "";

		try
		{
			importSucceeded	=
				ImportUtils.importObjects
				(
					objectsToImport ,
					importOK ,
					renameDuplicates ,
					progressReporter
				);
        }
        catch ( ExportException e )
        {
        	importErrorMessage	= e.getMessage();
        }
								//	Enable the default cursor.
		setDefaultCursor();
            					//	Close persistence manager for thread.

   		getCalculatorWindow().closePersistenceManager();

								//	Close progress reporter.

		progressReporter.close();

   								//	Pick up lists of items imported
   								//	successfully and not imported.

		StringBuffer sbOK		= new StringBuffer();
		StringBuffer sbNotOK	= new StringBuffer();

		sbOK.append
		(
			WordHoardSettings.getString
			(
				"Thefollowingitemsweresuccessfullyimported" ,
				"The following items were successfully imported."
			)
		);

		sbOK.append( "\n" );

		sbNotOK.append
		(
			WordHoardSettings.getString
			(
				"Thefollowingitemscouldnotbeimported" ,
				"The following items could not be imported."
			)
		);

		sbNotOK.append( "\n" );

		PrintfFormat asNewNameFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"asnewname" ,
					" as \"%s\""
				)
			);

		int okCount		= 0;
		int badCount	= 0;

		for ( int i = 0 ; i < objectsToImport.length ; i++ )
		{
			String objectName	= objectsToImport[ i ].getTitle();

			if ( importOK[ i ] )
			{
				okCount++;

				sbOK.append( "     " );
				sbOK.append( currentNames[ i ] );

				if ( !objectName.equals( currentNames[ i ] ) )
				{
					sbOK.append( " " );
					sbOK.append( asNewNameFormat.sprintf( objectName ) );
				}

				sbOK.append( "\n" );
			}
			else
			{
				badCount++;

				sbOK.append( "     " );
				sbNotOK.append( objectName );

				sbNotOK.append( "\n" );
			}
		}

		StringBuffer sb	= new StringBuffer();

		if ( okCount > 0 ) sb.append( sbOK );

		if ( badCount > 0 )
		{
			if ( okCount > 0 ) sb.append( "\n" );

			sb.append( sbNotOK );
		}

		final String importResults	= sb.toString();

								//	Display import results.

		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
								//	Enable the close button.


					cancelToClose( closeButton , panel );

								//	Create results panel to hold
								//	import report.

					ResultsPanel resultsPanel	= new ResultsPanel();

					String importReport	=
						WordHoardSettings.getString
						(
							"Importreport" ,
							"Import report"
						);

					resultsPanel.setResultsHeader( importReport );
					resultsPanel.setResultsTitle( importReport );
					resultsPanel.setResults( importResults );

					XTextArea titleTextArea	= new XTextArea( importReport );
					titleTextArea.setBackground( panel.getBackground() );

					JPanel titlePanel	= new JPanel();
					titlePanel.setLayout( new BorderLayout() );
					titlePanel.add( titleTextArea );

					resultsPanel.add( titlePanel );

					XScrollPane scrollPane	=
						new XScrollPane
						(
							new XTextArea( importResults ) ,
							JScrollPane.VERTICAL_SCROLLBAR_ALWAYS ,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
						);

					resultsPanel.add( scrollPane , 10 );

					scrollPane.setPreferredSize(
						new Dimension( 600 , 800 ) );

					panel.getBody().removeAll();
					panel.add( resultsPanel );
					panel.revalidate();
					panel.paintImmediately( panel.getVisibleRect() );
				}
			}
		);
	}

	/**	Display import dialog.
	 */

	public void doImport()
	{
								//	Get name of import file.
								//	Quit if not given.

		String importFileName	=
			ImportUtils.getImportFileName( parentWindow );

		if ( importFileName == null ) return;

		JTabbedPane mainTabbedPane	=
			getMainTabbedPane();

		mainTabbedPane.paintImmediately( mainTabbedPane.getVisibleRect() );

								//	Parse the import XML file.

		ProgressDialog progressDialog	=
			progressDialog	=
				new ProgressDialog
				(
					parentWindow ,
					WordHoardSettings.getString
					(
						"Reading" ,
						"Reading"
					) ,
					WordHoardSettings.getString
					(
						"Readingimportfile" ,
						"Reading import file"
					) ,
					0 ,
					0 ,
					true ,
					2
				);

		org.w3c.dom.Document document	=
			ImportUtils.getImportDocument( importFileName );

		progressDialog.close();

        						//	Report error if unable to parse
        						//	input file.

		if ( document == null  )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"Unabletoparseimportfile" ,
					"Unable to parse import file."
				)
			);

			return;
		}
								//	If we got import file name,
								//	display dialog to allow selection
								//	of items to import.
		ImportDialog dialog	=
			new ImportDialog
			(
				WordHoardSettings.getString
				(
					"importDialogTitle" ,
					"Import from XML"
				) ,
				parentWindow ,
				document );

		dialog.show( parentWindow );

		if ( !dialog.getCancelled() )
		{
			final ImportDialog finalDialog	= dialog;

			final OutputResults outputResults	=
            	createProgressPanel
            	(
					WordHoardSettings.getString
					(
						"Importing" ,
						"Importing"
					) ,
					WordHoardSettings.getString
					(
						"Importingdot" ,
						"Importing ..."
					)
            	);

			Thread runner = new Thread( "Import" )
			{
				public void run()
				{
					doImportThread( finalDialog , outputResults );
				}
			};

			Thread awtEventThread	= SwingUtils.getAWTEventThread();

			if ( awtEventThread != null )
			{
				ThreadUtils.setPriority(
					runner , awtEventThread.getPriority() - 1 );
			}

			runner.start();
		}
	}

	/**	Handle menu changes when logging in.
	 */

	public void handleLogin()
	{
		Account account = WordHoardSettings.getLoginAccount();

		manageAccountsMenuItem.setEnabled
		(
			( account != null ) && account.getCanManageAccounts()
		);
								//	Enable logout menu item.

		loginMenuItem.setEnabled( false );

		logoutMenuItem.setText( getLogoutMenuItemText() );
		logoutMenuItem.setEnabled( true );

								//	Enable import and export menu items.

		importMenuItem.setEnabled( true );
		exportMenuItem.setEnabled( true );

								//	Enable annotate menu item.

		annotateMenuItem.setEnabled
		(
			( account != null ) &&
			parentWindow.isAnnotateAvailable()
		);
	}

	/**	Handle menu changes when logging out.
	 */

	public void handleLogout()
	{
								//	Disable logout menu item.

		logoutMenuItem.setText( getLogoutMenuItemText() );
		logoutMenuItem.setEnabled( false );

								//	Enable the login menu item.

		loginMenuItem.setEnabled( true );

		manageAccountsMenuItem.setEnabled( false );

								//	Disable import and export menu items.

		importMenuItem.setEnabled( false );
		exportMenuItem.setEnabled( false );

								//	Disable annotate menu item.

		annotateMenuItem.setEnabled( false );
	}

	/**	New work set. */

	protected void newWorkSet()
	{
		if ( parentWindow != null ) parentWindow.handleNewWorkSetCmd();
	}

	/**	Open work set. */

	protected void openWorkSet()
	{
		if ( parentWindow != null ) parentWindow.handleOpenWorkSetCmd();
	}

	/**	Save work set. */

	protected void saveWorkSet()
	{
		if ( parentWindow != null ) parentWindow.handleSaveWorkSetCmd();
	}

	/**	Save as word set. */

	protected void saveAsWordSet()
	{
		if ( parentWindow != null ) parentWindow.handleSaveWordSetCmd();
	}

	/**	Do file menu save as.
	 */

	protected void saveAs()
	{
		if ( parentWindow != null ) parentWindow.handleSaveAsCmd();
	}

	/**	Adjusts the account commands in all Reader windows.
	 *
	 *	<p>
	 *	Enables/disables the "Logout" and "Manage Account" commands in all
	 *	open windows and adjusts the text of the "Logout" command in all open
	 *	windows to read "Logout xxx" when the user is logged in with username
	 *	"xxx".
	 *	</p>
	 */

	protected void adjustAllAccountCommands()
	{
		if ( parentWindow == null ) return;

		WindowsMenuManager[] windows	=
			parentWindow.getAllOpenWindows();

		for ( int i = 0 ; i < windows.length ; i++ )
		{
			WindowsMenuManager window	= (WindowsMenuManager)windows[ i ];

			if ( window instanceof AbstractWindow )
			{
				((AbstractWindow)window).adjustAccountCommands();
			}
		}

		if ( WordHoardSettings.getLoginAccount() != null )
		{
			parentWindow.setLoggedIn();
		}
		else
		{
			parentWindow.setLoggedOut();
		}
	}

	/**	Login to WordHoard.
	 */

	protected void login()
	{
								//	Display user ID and password dialog.

		LoginDialog loginDialog	= new LoginDialog( parentWindow );

								//	If the dialog was not cancelled,
								//	get the user ID and password.

		if ( !loginDialog.canceled() )
		{
    							//	Set settings for successful login.

			parentWindow.setLoggedIn();

			adjustAllAccountCommands();

								//	Report successful login.

			PrintfFormat loggedInFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
						"Youareloggedin" ,
						"You are logged in as %s."
					)
				);

			String loggedInMessage	=
				loggedInFormat.sprintf
				(
					new Object[]{ WordHoardSettings.getUserID() }
				);

			new InformationMessage( loggedInMessage , parentWindow );
		}
	}

	/**	Logout from WordHoard. */

	protected void logout()
	{
		LoginDialog.logout();
    							//	Set settings for logout.

		parentWindow.setLoggedOut();

		adjustAllAccountCommands();

								//	Report logout.
		new InformationMessage
		(
			WordHoardSettings.getString
			(
				"Youareloggedout" ,
				"You are logged out."
			) ,
			parentWindow
		);
	}

	/**	Display font preferences dialog.
	 */

	protected void doFontPreferences()
	{
		new FontPrefsDialog( null );
	}

	/**	Manage accounts.
	 */

	protected void manageAccounts()
		throws Exception
	{
		ManageAccountsWindow.open( parentWindow );
	}

	/**	Annotate.
	 */

	protected void annotate()
		throws Exception
	{
		if ( parentWindow != null )
		{
			parentWindow.handleAnnotateCmd();
		}
	}

	/**	Run a simple text editor. */

	protected void simpleEditor()
	{
		if ( parentWindow != null ) parentWindow.handleEditorCmd();
	}

	/**	Run a script. */

	protected void runScript()
	{
		if ( parentWindow != null ) parentWindow.handleRunScriptCmd();
	}

	/**	Send error report.
	 */

	protected void doSendErrorReport()
	{
		WebStart.showDocument
		(
			WordHoardSettings.getString
			(
				"sendErrorURLString" ,
				"mailto:martinmueller@northwestern.edu?" +
				"subject=Error%20in%20WordHoard"
			)
		);
	}

	/**	Get string for Logout menu item.
	 *
	 *	@return		The text for the Logout menu item.
	 */

	protected String getLogoutMenuItemText()
	{
		boolean userIDDefined	=
			( WordHoardSettings.getUserID() != null );

		PrintfFormat logoutMenuItemFormat	=
			new PrintfFormat
			(
				WordHoardSettings.getString
				(
					"wordhoardMenuLogoutItem" ,
					"Logout %s"
				)
			);

		String logoutMenuItemText	=
			logoutMenuItemFormat.sprintf
			(
				userIDDefined ?
					WordHoardSettings.getUserID() : ""
			);

		return logoutMenuItemText;
	}

	/**	Quit.
	 */

	protected void quit()
	{
		if ( parentWindow == null )
		{
			WordHoard.quit();
		}
		else
		{
			parentWindow.quit();
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

