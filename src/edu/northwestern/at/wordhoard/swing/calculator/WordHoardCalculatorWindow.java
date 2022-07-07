package edu.northwestern.at.wordhoard.swing.calculator;

/*	Please see the license information at the end of this file. */

import bsh.*;
import bsh.util.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.lang.reflect.*;

import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.printing.*;

import edu.northwestern.at.wordhoard.server.model.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.menus.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;
import edu.northwestern.at.wordhoard.swing.tcon.*;

/**	Main WordHoard calculator window.
 *
 *	<p>
 *	The WordHoard calculator window is a singleton.  Only one may be
 *	open at any time.
 *	</p>
 */

public class WordHoardCalculatorWindow extends AbstractWindow
	implements AdjustAccountCommands
{
	/**	The singleton WordHoard Calculator window.
	 */

	public static WordHoardCalculatorWindow calculatorWindow	= null;

	/**	The main tabbed panel */

	protected WordHoardTabbedPane mainTabbedPane;

	/** The menu bar. */

	protected JMenuBar menuBar;

	/** The file menu. */

	protected FileMenu fileMenu;

	/** The edit menu. */

	protected CalculatorEditMenu editMenu;

	/**	The author menu. */

	protected JMenu authorMenu;

	/**	The sets menu. */

	protected SetsMenu setsMenu;

	/**	The find menu. */

	protected FindMenu findMenu;

	/**	The views menu. */

	protected ViewsMenu viewsMenu;

	/**	The analysis menu. */

	protected AnalysisMenu analysisMenu;

	/**	The windows menu. */

	protected WindowsMenu windowsMenu;

	/** The help menu. */

	protected HelpMenu helpMenu;

	/** The script interpreter. */

	protected static bsh.Interpreter interpreter;

	/** The console input/output area for the interpreter. */

	protected static JConsole console;

	/** The hidden text pane in the console. */

	protected static JTextPane inputTextPane;

	/**	Last output panel number. */

	protected int outputResultsNumber	= 0;

	/**	True if login succeeded. */

	protected boolean loginSucceeded	= false;

	/**	Program version. */

	protected String programVersion;

	/**	Program banner. */

	protected String programBanner;

	/**	Program prompt. */

	protected String programPrompt;

	/**	Opens and/or displays the Calculator window.
	 *
	 *	@param	show	true to show calculator window,
	 *					false to leave display status as is.
	 */

	public static void open( boolean show )
	{
		if ( calculatorWindow == null )
		{
			try
			{
				calculatorWindow	=
					new WordHoardCalculatorWindow
					(
						TableOfContentsWindow.getTableOfContentsWindow()
					);

				if ( show )
				{
					calculatorWindow.setVisible( true );
					calculatorWindow.toFront();
				}
			}
			catch ( Exception e )
			{
				Err.err( e );
			}
		}
		else if ( show )
		{
			calculatorWindow.setVisible( true );
			calculatorWindow.toFront();
		}
	}

	/**	Create WordHoard Calculator window.
	 * 
	 * @param	parentWindow	The parent window.
	 *
	 * @throws	PersistenceException	error in persistence layer.
	 */

	protected WordHoardCalculatorWindow( AbstractWindow parentWindow )
		throws PersistenceException
	{
								//	Set title.
		super
		(
			WordHoardSettings.getString
			(
				"Calculator" ,
				"Calculator"
			),
			parentWindow
		);
	}

	/**	Create the actual window data.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	protected void createWindowData()
		throws PersistenceException
	{
								//	Save settings.

		this.programVersion	= WordHoardSettings.getProgramVersion();
		this.programBanner	= WordHoardSettings.getProgramBanner();
		this.programPrompt	= WordHoardSettings.getProgramPrompt();

										//	Create menu bar.

		menuBar	= createMenuBar();

								// Add menu bar.

		setJMenuBar( menuBar );

								//	Set Windows menu.

		setWindowsMenu( windowsMenu );

								//	Create tabbed panel to
								//	hold interpreter window,
								//	output windows.

		JPanel mainPanel	= new JPanel();

		mainPanel.setLayout( new BorderLayout() );

		mainTabbedPane	= new WordHoardTabbedPane( this );

		mainTabbedPane.setPreferredSize( new Dimension( 600 , 430 ) );

								//	Create dialog panel to hold
								//	the interpreter window.

		DialogPanel panel	= new DialogPanel();

								//	Create script console.

		console				= new JConsole();

		console.setPreferredSize( new Dimension( 560 , 380 ) );

								//	Add script console to panel.

		panel.add( console );

								//	Add panel to tabbed panel.

		mainTabbedPane.add( "Input" , panel );

								//	Extract the JTextEdit field from the
								//	console using reflection.
		try
		{
			Field textField	= console.getClass().getDeclaredField( "text" );

			textField.setAccessible( true );

			inputTextPane	= (JTextPane)textField.get( console );
		}
		catch ( Exception e )
		{
			Err.err( e );
		}
								//	Put tabbed panel in
								//	main panel, and put
								//	the main panel in
								//	a JFrame.  This avoids
								//	weird display artifacts
								//	that otherwise result
								//	from embedding a tabbed pane
								//	directly in a JFrame.

		Color backgroundColor	= mainPanel.getBackground();

		mainPanel.add( mainTabbedPane , BorderLayout.CENTER );

								// Set background color for frame and content.

		setBackground( backgroundColor );

		mainPanel.setBackground( backgroundColor );

								// Put content in JFrame.

		setContentPane( mainPanel );

								//	Start the script interpreter.

		startScriptInterpreter();

								// Set Calculator window size and location.

//	$$$PIB$$$ Using pack hangs on Linux.
//	Base size of calculator window on size of parent window
//	(should be the Table of Contents window).

//		pack();

		setLocation( new Point( 3 , WordHoardSettings.getTopSlop() ) );

		Dimension screenSize	= getToolkit().getScreenSize();

		Dimension windowSize	= new Dimension( 600 , 400 );

		windowSize.height 		=
			screenSize.height -
				( WordHoardSettings.getTopSlop() +
				WordHoardSettings.getBotSlop() );

		setSize( windowSize );

		if ( parentWindow == null)
		{
			WindowPositioning.centerWindowOverWindow( this , null , 0 );
		}
		else
		{
			positionNextTo( parentWindow );
		}
	}

	/**	About box.
	 */

	public void about()
	{
		super.about();
	}

    /** Add a cancel button to a progress panel.
     *
     *	@param	panel			Dialog panel to which to add close button.
     *	@param	progressPanel	Progress panel to which to tie cancel action.
	 *	@return	The button.
     */

	public JButton addACancelButton
	(
		DialogPanel panel ,
		final ProgressPanel progressPanel
	)
	{
								//	Add cancel button.

		return panel.addButton
		(
			WordHoardSettings.getString( "Cancel" , "Cancel" ) ,
			progressPanel.getCancelPressedAction()
		);
	}

    /** Add a close button to a dialog panel.
     *
     *	@param	panel		Dialog panel to which to add close button.
     *	@param	outputTitle	Title of tabbed panel holding dialog panel.
	 *	@return	The button.
     */

	public JButton addACloseButton
	(
		DialogPanel panel ,
		final String outputTitle
	)
	{
								//	Add close button.

		return panel.addButton
		(
			WordHoardSettings.getString( "Close" , "Close" ) ,
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					int tabIndex	=
						mainTabbedPane.indexOfTab( outputTitle );

					if ( tabIndex >= 0 )
					{
						mainTabbedPane.remove( tabIndex );
					}
					else
					{
						Window window	=
							SwingUtils.getParentWindow(
								(JComponent)e.getSource() );

						window.dispatchEvent
						(
							new WindowEvent
							(
								window ,
								WindowEvent.WINDOW_CLOSING
							)
						);
					}
				}
			}
		);
	}

    /** Change cancel button to a close button.
     *
     *	@param	button		The cancel button to change to a close button.
     *	@param	component	Component in tabbed panel holding button.
     */

	protected void doCancelToClose
	(
		JButton button ,
		final Component component
	)
	{
								//	Change button text to "Close."

		button.setText(	WordHoardSettings.getString( "Close" , "Close" ) );

								//	Remove current action listeners.

		ActionListener[] actionListeners	= button.getActionListeners();

		for ( int i = 0 ; i < actionListeners.length ; i++ )
		{
			button.removeActionListener( actionListeners[ i ] );
		}
								//	Add an action listener which removes
								//	the tabbed panel to which the button is
								//	attached.

		button.addActionListener
		(
			new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					int tabIndex	=
						mainTabbedPane.indexOfComponent( component );

					if ( tabIndex >= 0 )
					{
						mainTabbedPane.remove( component );
					}
					else
					{
						Window window	=
							SwingUtils.getParentWindow(
								(JComponent)e.getSource() );

						window.dispatchEvent
						(
							new WindowEvent
							(
								window ,
								WindowEvent.WINDOW_CLOSING
							)
						);
					}
				}
			}
		);
	}

    /** Change cancel button to a close button.
     *
     *	@param	button		The cancel button to change to a close button.
     *	@param	component	Component in tabbed panel holding button.
     */

	public void cancelToClose
	(
		final JButton button ,
		final Component component
	)
	{
		Runnable runnable	=
			new Runnable()
			{
				public void run()
				{
					doCancelToClose( button , component );
				}
			};

	    try
	    {
			SwingUtilities.invokeLater( runnable );
		}
		catch ( Exception e )
		{
//			e.printStackTrace();
		}
	}

	/**	Create a scroll pane around a results component.
	 *
	 *	@param	component	Component to wrap with a scroll pane.
	 *
	 *	@return				A scroll pane wrapping the component.
	 */

	protected XScrollPane getResultsScrollPane( JComponent component )
	{
								//	Create scroll pane.

		XScrollPane scrollPane	=
			new XScrollPane
			(
				component ,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
			);
                                //	Set the scroll pane width match
                                //	the Calculator's console and the
                                //	height to a large number so that
                                //	changing the window size gives most
                                //	of any new space to the scroll pane
                                //	contents.

		Dimension consoleSize		= console.getSize();
		Dimension scrollPaneSize	= scrollPane.getSize();

		scrollPaneSize.width		= consoleSize.width;
		scrollPaneSize.height		=
			Math.max( consoleSize.height , 16384 );

		scrollPane.setPreferredSize( scrollPaneSize );

		return scrollPane;
	}

	/**	Insert a component into the main tabbed pane.
	 *
	 *	@param	title		Title for the component.
	 *	@param	header		Header text for the component.
	 *	@param	results		The component holding the results.
	 *
	 *	<p>
	 *	The title is used for printing only.  The header, if not null or
	 *	empty, is inserted into the output pane, followed by the
	 *	results component.
	 *	</p>
	 *
	 *	<p>
	 *	This method should only be run on the AWT event thread.
	 *	</p>
	 */

	protected void doAddResults
	(
		String title ,
		String header ,
		JComponent results
	)
	{
								//	Do nothing if nothing to display.

		if ( results == null ) return;

								//	Wrap results in scroll pane if
								//	necessary.

		JComponent wrappedResults	= results;

		if	(	( results instanceof JTable ) ||
				( results instanceof JTextPane ) ||
				( results instanceof JTextArea )
			)
		{
			wrappedResults	= getResultsScrollPane( results );
		}
								//	Get name for tabbed pane to hold
								//	the results of this analysis.

		String outputTitle			= getNextOutputWindowTitle();

								//	Results panel holds results.

		ResultsPanel resultsPanel	= new ResultsPanel();

								//	Set title and results.

		resultsPanel.setResultsTitle( title );
		resultsPanel.setResultsHeader( header );
		resultsPanel.setResults( results );

								//	Add header if provided.

		if ( ( header != null ) && ( header.length() > 0 ) )
		{
			WrappedTextPanel headerTextPanel	=
				new WrappedTextPanel( header , results.getFont() , 32767 );

			JPanel headerPanel	= new JPanel();
			headerPanel.setLayout( new BorderLayout() );
			headerPanel.add( headerTextPanel );

			resultsPanel.add( headerPanel );
		}
								//	Add results.

		resultsPanel.add( wrappedResults , 10 );

								//	Add enclosing panel to tabbed pane.

		mainTabbedPane.add( outputTitle , resultsPanel );

								//	Make it the currently selected pane.

		mainTabbedPane.setSelectedIndex(
			mainTabbedPane.indexOfTab( outputTitle ) );

								//	Detach tab.

		mainTabbedPane.undock();
	}

	/**	Insert a component into the main tabbed pane.
	 *
	 *	@param	title			Title for the component.
	 *	@param	header		Header text for the component.
	 *	@param	results		The component holding the results.
	 *
	 *	<p>
	 *	The title is used for printing only.  The header, if not null or
	 *	empty, is inserted into the output pane, followed by the
	 *	results component.
	 *	</p>
	 */

	public void addResults
	(
		final String title ,
		final String header ,
		final JComponent results
	)
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					doAddResults( title , header , results );
				}
			}
		);
	}

	/**	Insert a component into the main tabbed pane.
	 *
	 *	@param	title		Title for the component.
	 *	@param	results	The component holding the results.
	 *
	 *	<p>
	 *	The title is used for printing only.  The results component
	 *	is inserted into a tabbed output pane.
	 *	</p>
	 */

	public void addResults
	(
		String title ,
		JComponent results
	)
	{
		addResults( title , null , results );
	}

	/**	Insert a string into the main tabbed pane.
	 *
	 *	@param	title		Title for the component.
	 *	@param	header		Header text.
	 *	@param	results		The string holding the results.
	 */

	public void addResults( String title , String header , String results )
	{
		addResults( title , header , new XTextArea( results ) );
	}

	/**	Insert contents of a string buffer into the main tabbed pane.
	 *
	 *	@param	title		Title for the component.
	 *	@param	header		Header text.
	 *	@param	results		The string buffer holding the results.
	 */

	public void addResults
	(
		String title ,
		String header ,
		StringBuffer results
	)
	{
		addResults( title , header , results.toString() );
	}

	/**	Insert contents of a string writer into the main tabbed pane.
	 *
	 *	@param	title		Title for the component.
	 *	@param	header		Header text.
	 *	@param	results		The string writer holding the results.
	 */

	public void addResults
	(
		String title ,
		String header ,
		StringWriter results
	)
	{
		addResults( title , header , results.toString() );
	}

	/**	Insert contents of a string into the main tabbed pane.
	 *
	 *	@param	title		Title for the component.
	 *	@param	results		The string holding the results.
	 */

	public void addResults( String title , String results )
	{
		addResults( title , null , results );
	}

	/**	Insert contents of a string buffer into the main tabbed pane.
	 *
	 *	@param	title		Title for the component.
	 *	@param	results	The string buffer holding the results.
	 */

	public void addResults( String title , StringBuffer results )
	{
		addResults( title , null , results );
	}

	/**	Insert contents of a string writer into the main tabbed pane.
	 *
	 *	@param	title		Title for the component.
	 *	@param	results	The string writer holding the results.
	 */

	public void addResults( String title , StringWriter results )
	{
		addResults( title , null , results );
	}

	/**	Close the current thread's persistence manager.
	 */

	public void closePersistenceManager()
	{
		PMUtils.closePM( PMUtils.getPM() );
	}

	/**	Adjusts menu items to reflect logged-in status.
	 *
	 *	<p>
	 *	Enables/disables the "Logout" and "Manage Account" commands and
	 *	adjusts the text of the "Logout" command to read "Logout xxx" when
	 *	the user is logged in with username "xxx".  Also sets the
	 *	availability of the Query and Sets menus, and the Export and
	 *	Import commands of the File menu.
	 *	</p>
	 */

	public void adjustAccountCommands()
	{
		Account account	= WordHoardSettings.getLoginAccount();

		if ( account != null )
		{
			setLoggedIn();
		}
		else
		{
			setLoggedOut();
		}
	}

	/** Create menu bar and menu items.
	 * @return	The menu bar.
	*/

	protected JMenuBar createMenuBar()
	{
								//	Create menu bar.

		menuBar			= new JMenuBar();

								//	Create file menu.

		fileMenu		= new FileMenu( menuBar , this );

								//	Create edit menu.

		editMenu		= new CalculatorEditMenu( menuBar , this );

		editMenu.addMenuListener( editMenu.editMenuListener );

								//	Create sets menu.

		setsMenu		= new SetsMenu( menuBar , this );

		setsMenu.addMenuListener( setsMenu.setsMenuListener );

								//	Create find menu.

		findMenu		= new FindMenu( menuBar , this );

								//	Create views menu.

		viewsMenu		= new ViewsMenu( menuBar , this );

								//	Create analysis menu.

		analysisMenu	= new AnalysisMenu( menuBar , this );

								//	Create windows menu,

		windowsMenu		= new WindowsMenu( menuBar , this );

								//	Create help menu.

		helpMenu		= new HelpMenu( menuBar , this );

		return menuBar;
	}

	/**	Get the interpreter console.
	 *
	 *	@return		Interpreter console.
	 */

	public JConsole getConsole()
	{
		return console;
	}

	/**	Get the edit menu.
	 *
	 *	@return		The edit menu.
	 */

	public EditMenu getEditMenu()
	{
		return editMenu;
	}

	/**	Get the interpreter's input text pane.
	 *
	 *	@return		The intepreter's input text pane.
	 */

	public JTextPane getInputTextPane()
	{
		return inputTextPane;
	}

	/**	Get the interpreter.
	 *
	 *	@return		The main script interpreter.
	 */

	public Interpreter getInterpreter()
	{
		return interpreter;
	}

	/**	Get the main tabbed pane.
	 *
	 *	@return		Calculator main tabbed pane.
	 */

	public WordHoardTabbedPane getMainTabbedPane()
	{
		return mainTabbedPane;
	}

	/**	Get next output panel title.
	 *
	 *	@param		show	True to ensure calculator window is visible.
	 *
	 *	@return		Title for next output window panel.
	 */

	public String getNextOutputWindowTitle( boolean show )
	{
		if ( show ) makeVisible();

		return
			WordHoardSettings.getString
			(
				"outputWindowTitleStem" ,
				"output-"
			) +
			StringUtils.intToString( ++outputResultsNumber );
	}

	/**	Get next output panel title.
	 *
	 *	@return		Title for next output window panel.
	 *
	 *	<p>
	 *	Also ensures calculator window is made visible.
	 *	</p>
	 */

	public String getNextOutputWindowTitle()
	{
		return getNextOutputWindowTitle( true );
	}

	/**	Quit.
	 */

	public void quit()
	{
								//	Make sure user really wants to quit.

		if	(	WordHoardSettings.getWarnWhenQuitting() &&
				(	ConfirmYNC.confirmYN
					(
						WordHoardSettings.getString
						(
							"areYouSureYouWantToQuit" ,
							"Are you sure you want to quit?"
						)
					) == ConfirmYNC.YES
				)
			)
		{
			WordHoard.quit();
		}
		else
		{
			WordHoard.quit();
		}
	}

	/**	Set the busy cursor.
	 */

	public void setBusyCursor()
	{
	}

	/**	Set the default cursor.
	 */

	public void setDefaultCursor()
	{
	}

	/**	Adjust menu items and settings for successful login.
	 */

	public void setLoggedIn()
	{
								//	Login succeeded.

		fileMenu.handleLogin();
		setsMenu.handleLogin();
	}

	/**	Adjust menu items and settings for logout.
	 */

	public void setLoggedOut()
	{
		fileMenu.handleLogout();
		setsMenu.handleLogout();
	}

	/** Perform print preview. */

	public void doPrintPreview()
	{
		Component component			= mainTabbedPane.getSelectedComponent();

		JTextPane inputTextPane		= getInputTextPane();

		if ( mainTabbedPane.indexOfComponent( component ) == 0 )
		{
			doPrintPreview( new PrintJTextPane( inputTextPane ) );
		}
		else if ( component instanceof ResultsPanel )
		{
			((ResultsPanel)component).doPrintPreview();
		}
		else if ( component instanceof Container )
		{
			ResultsPanel resultsPanel	=
				BaseMenu.getResultsPanel( component );

			if ( resultsPanel != null )
			{
				resultsPanel.doPrintPreview();
			}
			else
			{
				PrintUtilities.printPreview
				(
					component ,
					"Results"
				);
			}
		}
		else
		{
			PrintUtilities.printPreview
			(
				component ,
				"Results"
			);
		}
	}

	/**	Print currently selected tabbed panel. */

	public void doPrint()
	{
		Component component			= mainTabbedPane.getSelectedComponent();

		JTextPane inputTextPane		= getInputTextPane();

		if ( mainTabbedPane.indexOfComponent( component ) == 0 )
		{
			doPrint( new PrintJTextPane( inputTextPane ) );
		}
		else if ( component instanceof ResultsPanel )
		{
			((ResultsPanel)component).doPrint();
		}
		else if ( component instanceof Container )
		{
			ResultsPanel resultsPanel	=
				BaseMenu.getResultsPanel( component );

			if ( resultsPanel != null )
			{
				resultsPanel.doPrint();
			}
			else
			{
				PrintUtilities.printComponent
				(
					component ,
					"Results" ,
					null ,
					null ,
					true
				);
			}
		}
		else
		{
			PrintUtilities.printComponent
			(
				component ,
				"Results" ,
				null ,
				null ,
				true
			);
		}
	}

	/**	Handles "Print Preview" command.
	 */

	public void handlePrintPreviewCmd()
	{
		this.doPrintPreview();
	}

	/**	Handles "Print" command.
	 */

	public void handlePrintCmd()
	{
		this.doPrint();
	}

	/**	Handle save as.
	 */

	public void handleSaveAsCmd()
	{
		JTabbedPane mainTabbedPane	= getMainTabbedPane();

		Component component	= mainTabbedPane.getSelectedComponent();

		if ( mainTabbedPane.indexOfComponent( component ) == 0 )
		{
								//	Save input text pane contents to file.

			String text	= inputTextPane.getText();

			FileExtensionFilter	filter	=
				new FileExtensionFilter( ".txt" , "Text Files" );

			FileDialogs.addFileFilter( filter );

			String[] saveFile	= FileDialogs.save( parentWindow );

			FileDialogs.clearFileFilters();

			if ( saveFile != null )
			{
				File file	= new File( saveFile[ 0 ] , saveFile[ 1 ] );

				try
				{
				 	FileUtils.writeTextFile( file , false , text );
				}
				catch ( Exception e )
				{
					new ErrorMessage
					(
						WordHoardSettings.getString
						(
							"Unabletosavetexttofile" ,
							"Unable to save text to file"
						) ,
						parentWindow
					);
				}
			}
		}
		else
		{
								//	Get results component.

			ResultsPanel resultsPanel	=
				BaseMenu.getResultsPanel( component );

								//	Output results from component.

			if ( resultsPanel != null )
			{
				resultsPanel.saveToFile( parentWindow );
			}
			else
			{
				PrintfFormat errorFormat	=
					new PrintfFormat
					(
						WordHoardSettings.getString
						(
							"Dontknowhowtooutputdatafromclass" ,
							"Don't know how to output data from class %s"
						)
					);

				String errorMessage	=
					errorFormat.sprintf
					(
						new Object[]{ component.getClass().getName() }
					);

				new ErrorMessage(
					errorMessage , parentWindow );
			}
		}
	}

	/**	Run a script. */

	public void runScript()
	{
//		makeVisible();
								//	Prompt for the script file name
								//	to execute.

		FileExtensionFilter	filter	=
			new FileExtensionFilter( ".bsh" , "Script Files" );

		FileDialogs.addFileFilter( filter );

		String[] fileToOpen	= FileDialogs.open( this );

		FileDialogs.clearFileFilters();

								//	Quit if no script file name supplied.

		if ( fileToOpen == null )
		{
			return;
		}
                                //	Check that the selected file exists.

		final File f	= new File( fileToOpen[ 0 ] , fileToOpen[ 1 ] );

		if ( f.exists() )
		{
			SwingUtilities.invokeLater
			(
				new Runnable()
				{
					public void run()
					{
								//	Make sure calculator window is open
								//	in case any output from the script
								//	is directed to the interpreter
								//	console.

						open( true );

						String scriptFileName	= f.getAbsolutePath();

								//	Map back slashes in file name to
								//	forward slashes because BeanShell
								//	prefers that.

						final String finalScriptFileName			=
							StringUtils.replaceAll(
								scriptFileName , "\\" , "/" );

								//	Run the selected script on a
								//	separate thread.

						new Thread( "Run script" )
						{
							public void run()
							{
								try
								{
		               				interpreter.source(
		               					finalScriptFileName );
								}
								catch ( Exception e )
								{
									e.printStackTrace();

									String errorMessage	=
										WordHoardSettings.getString
										(
											"Errorinscript" ,
											"Error in script"
										);

									BuildAndShowAlert.buildAndShowAlert
									(
										e,
										errorMessage ,
										null ,
										null ,
										calculatorWindow
									);
								}
							}
						}.start();
					}
				}
			);
		}
	}

	/**	Start the script interpreter thread. */

	public void startScriptInterpreter()
	{
								// Start script interpreter.

		interpreter	= new bsh.Interpreter( console );

		try
		{
			interpreter.eval( "printBanner() {}" );
			interpreter.eval( "getBshPrompt() { return \"" + programPrompt + "\"; }" );
			interpreter.eval( "addClassPath(\"edu.northwestern.at.wordhoard.scripts\")" );

			interpreter.eval( "import edu.northwestern.at.utils.math.*;" );
			interpreter.eval( "import edu.northwestern.at.utils.math.distributions.*;" );
			interpreter.eval( "import edu.northwestern.at.utils.math.matrix.*;" );
			interpreter.eval( "import edu.northwestern.at.utils.math.randomnumbers.*;" );
			interpreter.eval( "import edu.northwestern.at.utils.math.rootfinders.*;" );
			interpreter.eval( "import edu.northwestern.at.utils.math.statistics.*;" );

			interpreter.eval( "import edu.northwestern.at.utils.*;" );
			interpreter.eval( "import edu.northwestern.at.utils.corpuslinguistics.*;" );
			interpreter.eval( "import edu.northwestern.at.utils.corpuslinguistics.stemmer.*;" );
			interpreter.eval( "import edu.northwestern.at.utils.intcollections.*;" );
			interpreter.eval( "import edu.northwestern.at.utils.swing.*;" );

			interpreter.eval( "import edu.northwestern.at.wordhoard.model.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.annotations.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.bibtool.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.counts.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.grouping.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.morphology.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.querytool.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.search.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.speakers.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.tconview.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.text.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.userdata.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.model.wrappers.*;" );

			interpreter.eval( "import edu.northwestern.at.wordhoard.swing.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.swing.calculator.analysis.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.swing.calculator.cql.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;" );
			interpreter.eval( "import edu.northwestern.at.wordhoard.swing.text.*;" );

			interpreter.eval( "importCommands(\"edu/northwestern/at/wordhoard/scripts/\");" );
			interpreter.eval( "importCommands(\"edu/northwestern/at/wordhoard/scripts/math/arithmetic/\");" );
			interpreter.eval( "importCommands(\"edu/northwestern/at/wordhoard/scripts/math/distributions/\")" );
			interpreter.eval( "importCommands(\"edu/northwestern/at/wordhoard/scripts/math/matrix/\")" );
			interpreter.eval( "importCommands(\"edu/northwestern/at/wordhoard/scripts/math/rootfinders\")" );
			interpreter.eval( "importCommands(\"edu/northwestern/at/wordhoard/scripts/math/plotting\")" );
			interpreter.eval( "importCommands(\"edu/northwestern/at/wordhoard/scripts/math/randomnumbers\")" );
			interpreter.eval( "importCommands(\"edu/northwestern/at/wordhoard/scripts/math/\")" );
			interpreter.eval( "importCommands(\"edu/northwestern/at/wordhoard/scripts/utils/\")" );
			interpreter.eval( "importCommands(\"edu/northwestern/at/wordhoard/scripts/utils/corpuslinguistics/\")" );
			interpreter.eval( "importCommands(\"edu/northwestern/at/wordhoard/scripts/wordhoard/\")" );

			console.print
			(
				programBanner,
				new Font( "SansSerif" , Font.BOLD , 12 ),
				new Color( 20 , 100 , 20 )
			);

			console.println();
		}
		catch ( Exception e )
		{
			Err.err( e );
		}

		new Thread( interpreter ).start();
	}

	/**	Run a simple script-aware text editor. */

	public void simpleEditor()
	{
		WordHoardCalcSimpleEditor editor	=
			new WordHoardCalcSimpleEditor( getInterpreter() );

		editor.setSize( new Dimension( 500 , 350 ) );

		WindowPositioning.centerWindowOverWindow( editor , this , 25 );

		editor.setVisible( true );
	}

	/**	Return the singleton calculator window.
	 *
	 *	@return		This calculator window.
	 */

	public static WordHoardCalculatorWindow getCalculatorWindow()
	{
		open( false );

		return calculatorWindow;
	}

	/**	Ensure the calculator window is visible.
	 */

	public void makeVisible()
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					open( true );

					calculatorWindow.setVisible( true );
				}
			}
		);
	}

	/**	Show or hide the Calculator window.
	 *
	 *	@param	show	true to show window, false to hide it.
	 */

	public void setVisible( boolean show )
	{
		adjustAccountCommands();
		super.setVisible( show );
	}

	/**	Handles calculator window dispose events. */

	public void dispose()
	{
		calculatorWindow	= null;
		super.dispose();
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


