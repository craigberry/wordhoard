package edu.northwestern.at.utils.swing.notepad;

/*	Please see the license information in the header below. */

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.beans.*;
import java.io.*;
import java.net.URL;
import java.util.*;

import javax.swing.text.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	Simple text editor.
 *
 *	<p>
 *	Modified from Sun's Notepad sample, the usage license for which
 *	follows.  The author of Sun's version is Timothy Prinzing.
 *	Modifications by Philip R. Burns.
 *	</p>
 *
 *	<p>
 *	Copyright (c) 2003 Sun Microsystems, Inc. All  Rights Reserved.
 *	</p>
 *
 *	<p>
 *	Redistribution and use in source and binary forms, with or without
 *	modification, are permitted provided that the following conditions
 *	are met:
 *	</p>
 *
 *	<p>
 *	-Redistributions of source code must retain the above copyright
 *	notice, this list of conditions and the following disclaimer.
 *	</p>
 *
 *	<p>
 *	-Redistribution in binary form must reproduct the above copyright
 *	notice, this list of conditions and the following disclaimer in
 *	the documentation and/or other materials provided with the distribution.
 *	</p>
 *
 *	<p>
 *	Neither the name of Sun Microsystems, Inc. or the names of contributors
 *	may be used to endorse or promote products derived from this software
 *	without specific prior written permission.
 *	</p>
 *
 *	<p>
 *	This software is provided "AS IS," without a warranty of any kind. ALL
 *	EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 *	ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 *	OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT
 *	BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT
 *	OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR ITS
 *	DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 *	REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 *	INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 *	OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE, EVEN
 *	IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *	</p>
 *
 *	<p>
 *	You acknowledge that Software is not designed, licensed or intended for
 *	use in the design, construction, operation or maintenance of any nuclear
 *	facility.
 *	</p>
 */

public class Notepad
	extends XFrame
	implements EditBatch, CutCopyPaste, SelectAll
{
	/**	True if editor is readonly (e.g., acting as a viewer only). */

	protected boolean readOnly	= false;

	/**	True if the document text has been modified. */

	protected boolean documentModified	= false;

	/**	Current file being edited. */

	protected File currentFile	= null;

	/**	Default title for new file. */

	protected String newTitle	= "(Untitled)";

	/**	Current title for editor. */

	protected String title		= "(Untitled)";

	/**	The document listener. */

	protected MyDocumentListener documentListener;

	/** The compound undo edit batch manager. */

	protected CompoundEdit undoableEditBatch = null;

	/** True if in the midst of creating a compound edit batch. */

	protected boolean doingEditBatch = false;

	/**	Content pane for editor. */

	protected JPanel contentPanel	= null;

	/**	The text component holding the text being edited. */

	protected JTextArea editor;

	/**	Hash table of menu actions. */

	protected Hashtable commands;

	/**	Hash table containing menu items. */

	protected Hashtable menuItems;

	/**	Hash table containing menus. */

	protected Hashtable menus;

	/**	Hash table containing toolbar buttons. */

	protected Hashtable toolbarButtons;

	/**	The main menu. */

	private JMenuBar menubar;

	/**	The tool bar. */

	private JToolBar toolbar;

	/**	The status bar. */

	private JComponent statusBar;

	/**	Displays current line. */

	private JLabel lineLabel;

	/**	Displays current column. */

	private JLabel columnLabel;

	/**	Listener for the undoable edits to the current document.
	 */

	protected UndoableEditListener undoHandler = new UndoHandler();

	/** UndoManager to which we add edits. */

	protected UndoManager undo = new UndoManager();

	/** Clipboard. */

	protected ClipboardOwner defaultClipboardOwner =
		new ClipboardObserver();

	/**	Suffix applied to the key used in resource file
	 *	lookups for an image.
	 */

	public static final String imageSuffix = "Image";

	/**	Suffix applied to the key used in resource file
	 *	lookups for a label.
	 */

	public static final String labelSuffix = "Label";

	/**	Suffix applied to the key used in resource file
	 *	lookups for an action.
	 */

	public static final String actionSuffix = "Action";

	/**	Suffix applied to the key used in resource file
	 *	lookups for tooltip text.
	 */

	public static final String tipSuffix	= "Tooltip";

	/**	Suffix applied to the key word in resource file
	 *	lookups for setting item visible (=1) or invisible(=0).
	 */

	public static final String visibleSuffix	= "Visible";

	/**	Suffix applied to the key word in resource file
	 *	lookups for getting accelerator key name.
	 */

	public static final String acceleratorSuffix	= "Accelerator";

	/**	Tab size in columns.
	 */

	public static final int tabSize	= 4;

	/**	The font.
	 */

	public static final Font font	= new Font( "monospaced" , Font.PLAIN, 12 );

	/**	Width of a single character in the font.
	 */

	public static int fontCharWidth;

	/** The editor resource strings. */

	protected static ResourceBundle resources;

	/**	Load resource strings. */

	static
	{
		try
		{
			resources =
				ResourceBundle.getBundle(
					"edu.northwestern.at.utils.swing.notepad.resources.Notepad",
					Locale.getDefault() );
		}
		catch ( MissingResourceException mre )
		{
			System.err.println( "Notepad properties not found" );
//			System.exit( 1 );
		}
	}

	/**	Create Notepad editor.
	 *
	 *	@param	readOnly	True if editor is read-only, e.g.,
	 *						acting as a viewer.
	 */

	public Notepad( boolean readOnly )
	{
		super( "" );

		this.readOnly	= readOnly;

		contentPanel	= new JPanel( true );

		contentPanel.setBorder( BorderFactory.createEtchedBorder() );
		contentPanel.setLayout( new BorderLayout() );

								//	Create the embedded JTextComponent.

		editor = createEditor();

								//	Add undo listener.

		editor.getDocument().addUndoableEditListener( undoHandler );

								// Add document listener.

		createDocumentListener();

								// Listen for changes to caret position.

		createCaretListener();

								//	Install commands.

		commands = new Hashtable();

		Action[] actions = getActions();

		for ( int i = 0 ; i < actions.length ; i++ )
		{
			Action a	= actions[ i ];

			commands.put( a.getValue( Action.NAME ) , a );
		}
								//	Wrap editor with a scroll bar pane.

		JScrollPane scroller	=
			new JScrollPane
			(
				editor ,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
			);

							//	Create menu bar.

		menuItems		= new Hashtable();
		menus			= new Hashtable();

		menubar			= createMenubar();

  		setJMenuBar( menubar );

							//	Lay out editor window components.
							//	Menu and toolbar at top.
							//	Editor in middle.
							//	Status bar at bottom.

		JPanel panel	= new JPanel();

		panel.setLayout( new BorderLayout() );

		toolbarButtons	= new Hashtable();

		toolbar			= createToolbar();

		panel.add( "North" , toolbar );
		panel.add( "Center" , scroller );

		contentPanel.add( "Center" , panel );

		statusBar	= createStatusBar();

		contentPanel.add( "South" , statusBar );

		getContentPane().add( contentPanel );

								//	Initialize menu items state.

		enableMenuItems( false );
		enableEditMenuItems( false );

    							// Initialize clipboard.
		initializeClipboard();
	}

	/**	Create Notepad editor.
	 */

	public Notepad()
	{
		this( false );
	}

	/**	Fetch the list of actions supported by this editor.
	 *
	 *	@return	Array of editor actions.
	 *
	 *	<p>
	 *	It is implemented to return the list
	 *	of actions supported by the embedded JTextComponent
	 *	augmented with the actions defined locally.
	 *	</p>
	 */

	public Action[] getActions()
	{
		return
			TextAction.augmentList( editor.getActions() , defaultActions );
	}

	/**	Get title.
	 *
	 *	@return		The title.
	 */

	public String getTitle()
	{
		return title;
	}

	/**	Create a document listener.
	 */

	protected void createDocumentListener()
	{
		documentListener	= new MyDocumentListener();

		editor.getDocument().addDocumentListener( documentListener );
	}

	/**	Create an XTextArea to hold the document text.
	 *	@return	The created XTextArea.
	 */

	protected JTextArea createEditor()
	{
								//	Create editor.

		XTextArea c = new XTextArea();

								//	Set default font.

		c.setFont( font );

								//	Get width for a single character.

		fontCharWidth	= c.getFontMetrics( font ).charWidth( 'w' );

								//	Enable tabs.

		c.setTabsEnabled( true );

								//	Set tabs every four characters.

		c.setTabSize( tabSize );

								//	Set text to be read-only if requested.

		c.setEditable( !readOnly );

								//	Allow text dragging.

		c.setDragEnabled( true );

								//	Disable word wrap.

		c.setLineWrap( false );

		return c;
	}

	/**	Return the editor contained in this panel.
	 * @return	The editor.
	 */

	protected JTextArea getEditor()
	{
		return editor;
	}

	/**	Return the editor text as a string.
	 * @return	The editor text.
	 */

	protected String getEditorText()
	{
		return editor.getText();
	}

	/**	Find the parent frame for displaying a file-chooser dialog.
	 *	@return	The parent frame.
	 */

	protected Frame getFrame()
	{
		return this;
	}

	/**	Create a menu item.
	 *
	 *	@param	cmd		The name of the menu command.
	 *	@return	Resulting menu item.
	 *
	 *	<p>
	 *	This is the hook through which all menu items are
	 *	created.  It registers the result with the menuitem
	 *	hashtable so that it can be fetched with getMenuItem().
	 *	</p>
	 */

	protected JMenuItem createMenuItem( String cmd )
	{
		JMenuItem mi	=
			new JMenuItem( getResourceString( cmd + labelSuffix ) );

		URL url			= getResource( cmd + imageSuffix );

		if ( url != null )
		{
			mi.setHorizontalTextPosition( JButton.RIGHT );
			mi.setIcon( new ImageIcon( url ) );
		}

		String astr		= getResourceString( cmd + actionSuffix );

		if ( astr == null )
		{
			astr = cmd;
		}

		mi.setActionCommand( astr );

		Action a	= getAction( astr );

		if ( a != null )
		{
			mi.addActionListener( a );

			a.addPropertyChangeListener( createActionChangeListener( mi ) );

			mi.setEnabled( a.isEnabled() );

		}
		else
		{
			mi.setEnabled( false );
		}

		astr	= getResourceString( cmd + acceleratorSuffix );

		if ( astr != null )
		{
			int keyEvent = InputKeys.getKeyEvent( astr );

			if ( keyEvent != KeyEvent.CHAR_UNDEFINED )
			{
				mi.setAccelerator(
					KeyStroke.getKeyStroke(
						keyEvent ,
						Env.MENU_SHORTCUT_KEY_MASK ) );
			}
		}

		astr	= getResourceString( cmd + visibleSuffix );

		if ( astr != null )
		{
			mi.setVisible( astr.equals( "1" ) );
		}

		menuItems.put( cmd , mi );

		return mi;
	}

	/**	Return menu item for a specified command.
	 *
	 *	@param	cmd		Name of the action.
	 *
	 *	@return			Menu item created for the given command or null
	 *					if not created.
	 */

	protected JMenuItem getMenuItem( String cmd )
	{
		return (JMenuItem)menuItems.get( cmd );
	}

	/**	Return top-level menu.
	 *
	 *	@param	menuName	Name of the menu.
	 *
	 *	@return				Menu for given name or null	if not created.
	 */

	protected JMenuItem getMenu( String menuName )
	{
		return (JMenu)menus.get( menuName );
	}

	/**	Return action for a specified command.
	 *
	 *	@param	cmd		Name of the action.
	 *
	 *	@return			Action created for the given command or null
	 *					if not created.
	 */

	protected Action getAction( String cmd )
	{
		return (Action)commands.get( cmd );
	}

	/**	Return resource string by name.
	 *
	 *	@param	nm		Name of resource string.
	 *
	 *	@return			The resource string identified by "nm",
	 *					or null if none.
	 */

	protected String getResourceString( String nm )
	{
		String str;

		try
		{
			str	= resources.getString( nm );
		}
		catch ( MissingResourceException mre )
		{
			str	= null;
		}

		return str;
	}

	/**	Return resource URL by name.
	 *
	 *	@param	key		Key for resource.
	 *
	 *	@return			The resource URL identified by "key",
	 *					or null if none.
	 */

	protected URL getResource( String key )
	{
		String name	= getResourceString( key );

		if ( name != null )
		{
			URL url	= this.getClass().getResource( name );

			return url;
		}

		return null;
	}

	/**	Return the toolbar.
	 *
	 *	@return		The toolbar.
	 */

	protected Container getToolbar()
	{
		return toolbar;
	}

	/**	Return the menu bar.
	 *
	 *	@return		The menu bar.
	 */

	protected JMenuBar getMenubar()
	{
		return menubar;
	}

	/**	Create a status bar.
	 *
	 *	@return		The status bar.
	 */

	protected StatusBar createStatusBar()
	{
		return new StatusBar();
	}

	/**	Reset undo manager.
	 */

	protected void resetUndoManager()
	{
		endEditBatch();

		undo.discardAllEdits();

		undoAction.update();
		redoAction.update();
	}

	/**	Create the toolbar.
	 *
	 *	@return		The toolbar.
	 *
	 *	<p>
	 * 	By default this reads the resource file for the definition
	 *	of the toolbar.
	 *	</p>
	 */

	protected JToolBar createToolbar()
	{
		JToolBar toolbar	= new JToolBar();

		toolbar.setFloatable( false );

		String[] toolKeys	=
			StringUtils.makeTokenArray(
				getResourceString(
					readOnly ? "readonlytoolbar" : "toolbar" ) );

		for ( int i = 0 ; i < toolKeys.length ; i++ )
		{
			if ( toolKeys[ i ].equals("-") )
			{
				toolbar.addSeparator();
			}
			else
			{
				Component button	= createTool( toolKeys[ i ] );

				toolbar.add( button );

				toolbarButtons.put( toolKeys[ i ] , button );
			}
		}

		return toolbar;
	}

	/**	Create a toolbar item by name.
	 *
	 *	@param	key		The name of the toolbar item to create.
	 *
	 *	@return			The created toolbar item component.
	 */

	protected Component createTool( String key )
	{
		return createToolbarButton( key );
	}

	/**	Get a toolbar item by name.
	 *
	 *	@param	key		The name of the toolbar item to retrieve.
	 *
	 *	@return			The toolbar item component, or null if not found.
	 */

	protected Component getToolbarItem( String key )
	{
		return (Component)toolbarButtons.get( key );
	}

	/**	Create a button for the toolbar.
	 *
	 *	@param	key		The name of the button to create.
	 *
	 *	@return			The created button.
	 *
	 *	<p>
	 *	By default this
	 *	will load an image resource.  The image filename is relative to
	 *	the classpath (including the '.' directory if its a part of the
	 *	classpath), and may either be in a JAR file or a separate file.
	 *	</p>
	 */

	protected JButton createToolbarButton( String key )
	{
		URL url	= getResource( key + imageSuffix );

		JButton b	=
			new JButton( new ImageIcon( url ) )
			{
				public float getAlignmentY()
				{
					return 0.5f;
				}
			};

		b.setRequestFocusEnabled( false );

		b.setMargin( new Insets( 1 , 1 , 1 , 1 ) );

		String astr	= getResourceString( key + actionSuffix );

		if ( astr == null )
		{
			astr	= key;
		}

		Action a	= getAction( astr );

		if ( a != null )
		{
			b.setActionCommand( astr );
			b.addActionListener( a );
		}
		else
		{
			b.setEnabled( false );
		}

		astr	= getResourceString( key + visibleSuffix );

		if ( astr != null )
		{
			b.setVisible( astr.equals( "1" ) );
		}

		String tip	= getResourceString( key + tipSuffix );

		if ( tip != null )
		{
			b.setToolTipText( tip );
		}

		return b;
	}

	/**	Create the menubar for the app.
	 *
	 *	@return		The toolbar.
	 *
	 *	<p>
	 *	By default this pulls the
	 *	definition of the menu from the associated resource file.
	 *	</p>
	 */

	protected JMenuBar createMenubar()
	{
		JMenuBar menuBar	= new JMenuBar();

		String[] menuKeys	=
			StringUtils.makeTokenArray(
				getResourceString(
					readOnly ? "readonlymenubar" : "menubar" ) );

		for ( int i = 0 ; i < menuKeys.length ; i++ )
		{
			JMenu menuItem	= createMenu( menuKeys[ i ] );

			if ( menuItem != null )
			{
				menuBar.add( menuItem );
				menus.put( menuKeys[ i ] , menuItem );
			}
		}

		return menuBar;
	}

	/**	Create an editor menu.
	 *
	 *	@param	key	The key to the menu definition in the resource file.
	 *	@return		The menu.
	 *
	 *	<p>
	 *	By default this pulls the
	 *	definition of the menu from the associated resource file.
	 *	</p>
	 */

	protected JMenu createMenu( String key )
	{
		String[] itemKeys	=
			StringUtils.makeTokenArray(
				getResourceString(
					readOnly ? "readonly" + key : key ) );

		JMenu menu	= new JMenu( getResourceString( key + "Label" ) );

		for ( int i = 0 ; i < itemKeys.length ; i++ )
		{
			if ( itemKeys[ i ].equals( "-" ) )
			{
				menu.addSeparator();
			}
			else
			{
				JMenuItem menuItem	= createMenuItem( itemKeys[ i ] );

				menu.add( menuItem );
			}
		}

		String astr	= getResourceString( key + visibleSuffix );

		if ( astr != null )
		{
			menu.setVisible( astr.equals( "1" ) );
		}

		return menu;
	}

	// Yarked from JMenu, ideally this would be public.

	protected PropertyChangeListener createActionChangeListener( JMenuItem b )
	{
		return new ActionChangedListener( b );
	}

	// Yarked from JMenu, ideally this would be public.

	protected class ActionChangedListener implements PropertyChangeListener
	{
		JMenuItem menuItem;

		ActionChangedListener( JMenuItem menuItem )
		{
			super();

			this.menuItem = menuItem;
		}

		public void propertyChange( PropertyChangeEvent e )
		{
			String propertyName	= e.getPropertyName();

			if ( e.getPropertyName().equals( Action.NAME ) )
			{
				String text	= (String)e.getNewValue();

				menuItem.setText( text );
			}
			else if ( propertyName.equals( "enabled" ) )
			{
				Boolean enabledState	= (Boolean)e.getNewValue();

				menuItem.setEnabled( enabledState.booleanValue() );
			}
		}
	}

	protected class UndoHandler implements UndoableEditListener
	{
	 	/**	Messaged when the Document has created an edit, the edit is
		 *	added to <code>undo</code>, an instance of UndoManager.
		 */

		public void undoableEditHappened( UndoableEditEvent e )
		{
			if ( doingEditBatch )
			{
				undoableEditBatch.addEdit( e.getEdit() );
			}
			else
			{
				undo.addEdit( e.getEdit() );
			}

			undo.addEdit( e.getEdit() );

			undoAction.update();
			redoAction.update();
		}
	}

	/**	Create status bar for displaying row/column and progress.
	 */

	protected class StatusBar extends JComponent
	{
		public StatusBar()
		{
			super();
			setLayout( new BoxLayout( this , BoxLayout.X_AXIS ) );

			lineLabel	= new JLabel( "1" );
			columnLabel	= new JLabel( "1" );

			add( new JLabel( "L: " ) );
			add( lineLabel );
			add( new JLabel( " | " ) );

			add( new JLabel( "C: " ) );
			add( columnLabel );
			add( new JLabel( " | " ) );
		}

		public void paint( Graphics g )
		{
			super.paint( g );
		}
	}

	// --- action implementations -----------------------------------

	protected NewAction newAction				= new NewAction();
	protected OpenAction openAction				= new OpenAction();
	protected SaveAction saveAction				= new SaveAction();
	protected SaveAsAction saveAsAction			= new SaveAsAction();
	protected PageSetupAction pageSetupAction	= new PageSetupAction();
	protected PrintAction printAction			= new PrintAction();
	protected ExitAction exitAction				= new ExitAction();

	protected CopyAction copyAction				= new CopyAction();
	protected CutAction cutAction				= new CutAction();
	protected PasteAction pasteAction			= new PasteAction();
	protected SelectAllAction selectAllAction	= new SelectAllAction();
	protected UndoAction undoAction				= new UndoAction();
	protected RedoAction redoAction				= new RedoAction();

	protected FindAction findAction				= new FindAction();
	protected ReplaceAction replaceAction		= new ReplaceAction();

	protected RunAction runAction				= new RunAction();

	/**	Actions defined by the Notepad class.
	 */

	protected Action[] defaultActions =
	{
		newAction ,
		openAction ,
		saveAction ,
		saveAsAction ,
		pageSetupAction ,
		printAction ,
		exitAction ,

		copyAction ,
		cutAction ,
		pasteAction ,
		selectAllAction ,
		undoAction ,
		redoAction ,
		runAction ,

		findAction ,
		replaceAction ,

		runAction
	};

	/**	Copy to clipboard action. */

	protected class CopyAction extends AbstractAction
	{
		public CopyAction()
		{
			super( "copy" );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			doCopy( e );
		}
	}

	/**	Handle Copy action.
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doCopy( ActionEvent e )
	{
		editor.copy();
	}

	/**	Cut to clipboard action. */

	protected class CutAction extends AbstractAction
	{
		public CutAction()
		{
			super( "cut" );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			doCut( e );
		}
	}

	/**	Handle Cut action.
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doCut( ActionEvent e )
	{
		editor.cut();
	}

	/**	Paste from clipboard action. */

	protected class PasteAction extends AbstractAction
	{
		public PasteAction()
		{
			super( "paste" );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			doPaste( e );
		}
	}

	/**	Handle Paste action.
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doPaste( ActionEvent e )
	{
		editor.paste();
	}

	/**	Handles Undo action.
	 */

	protected class UndoAction extends AbstractAction
	{
		public UndoAction()
		{
			super( "undo" );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			try
			{
				undo.undo();
			}
			catch ( CannotUndoException ex )
			{
				System.out.println( "Unable to undo: " + ex );
				reportUnexpectedError( ex );
			}

			update();

			redoAction.update();
		}

		protected void update()
		{
			if ( undo.canUndo() )
			{
				setEnabled( true );
				putValue( Action.NAME , undo.getUndoPresentationName() );
			}
			else
			{
				setEnabled( false );
				putValue( Action.NAME , "undo" );
			}
		}
	}

	/**	Handles Redo action.
	 */

	protected class RedoAction extends AbstractAction
	{
		public RedoAction()
		{
			super( "redo" );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			try
			{
				undo.redo();
			}
			catch ( CannotRedoException ex )
			{
				System.out.println("Unable to redo: " + ex);
				reportUnexpectedError( ex );
			}

			update();
			undoAction.update();
		}

		protected void update()
		{
			if ( undo.canRedo() )
			{
				setEnabled( true );
				putValue( Action.NAME , undo.getRedoPresentationName() );
			}
			else
			{
				setEnabled( false );
				putValue( Action.NAME , "redo" );
			}
		}
	}

	/**	Handles "select all" action.
	 */

	protected class SelectAllAction extends AbstractAction
	{
		SelectAllAction()
		{
			super( "selectall" );
			setEnabled( false );
		}

		SelectAllAction( String nm )
		{
			super( nm );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			doSelectAll( e );
		}
	}

	/**	Performs "select all" action.
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doSelectAll( ActionEvent e )
	{
		selectAll();
	}

	/**	Selects all text.
	 */

	public void selectAll()
	{
		if ( isSelectAllEnabled() )
		{
			editor.getCaret().setSelectionVisible( true );
			editor.select( 0 , editor.getDocument().getLength() );
			editor.getCaret().setSelectionVisible( true );
		}
	}

	/**	Checks if "select all" enabled.
	 *
	 *	@return		returns true if select all enabled.
	 */

	public boolean isSelectAllEnabled()
	{
		return true;
	}

	/**	Open file action.
	 */

	protected class OpenAction extends NewAction
	{
		OpenAction()
		{
			super( "open" );
		}

		public void actionPerformed( ActionEvent e )
		{
			doOpen( e );
		}
	}

	/** Performs open file action.
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doOpen( ActionEvent e )
	{
		if ( !documentModified || readOnly )
		{
			String[] fileToOpen	= FileDialogs.open( this );

			if ( fileToOpen == null )
			{
				return;
			}

			File f	= new File( fileToOpen[ 0 ] , fileToOpen[ 1 ] );

			if ( f.exists() )
			{
				Document oldDoc	= getEditor().getDocument();

				if ( oldDoc != null )
					oldDoc.removeUndoableEditListener( undoHandler );

				getEditor().setDocument( new PlainDocument() );

				getEditor().getDocument().putProperty
				(
					PlainDocument.tabSizeAttribute ,
					Integer.valueOf( tabSize )
				);

				title	= f.toString();
				setTitle( title );

				currentFile		= f;

				Thread loader	= new FileLoader( f , editor.getDocument() );
				loader.start();
			}
		}
		else
		{
			switch( showNotSavedDialog( "Open" ) )
			{
				case ConfirmYNC.YES:
					doSave( e );
					doOpen( e );
					break;

				case ConfirmYNC.NO:
					documentModified = false;
					doOpen( e );
					break;

				case ConfirmYNC.CANCEL:
					break;
			}
		}
	}

	/** New action.
	 */

	protected class NewAction extends AbstractAction
	{
		NewAction()
		{
			super( "new" );
			setEnabled( true );
		}

		NewAction( String nm )
		{
			super( nm );
			setEnabled( true );
		}

		public void actionPerformed( ActionEvent e )
		{
			doNew( e );
		}
	}

	/**	Performs new action.
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doNew( ActionEvent e )
	{
		if ( !documentModified || readOnly )
		{
			Document oldDoc	= getEditor().getDocument();

			if ( oldDoc != null )
				oldDoc.removeUndoableEditListener( undoHandler );

			getEditor().setDocument( new PlainDocument() );
			getEditor().getDocument().addUndoableEditListener( undoHandler );

			resetUndoManager();
		}
		else
		{
			switch( showNotSavedDialog( "New Document" ) )
			{
				case ConfirmYNC.YES:
					doSave( e );
					doNew( e );
					break;

				case ConfirmYNC.NO:
					documentModified = false;
					doNew( e );
					break;

				case ConfirmYNC.CANCEL:
					break;
			}
		}

		setTitle( newTitle );
		currentFile	= null;
	}

	/**	Save file action.
	 */

	protected class SaveAction extends AbstractAction
	{
		SaveAction()
		{
			super( "save" );
			setEnabled( false );
		}

		SaveAction( String nm )
		{
			super( nm );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			doSave( e );
		}
	}

	/**	Performs save file action.
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doSave( ActionEvent e )
	{
		if ( currentFile == null )
		{
			doSaveAs( e );
		}
		else
		{
			Thread saver	=
				new FileSaver( currentFile , editor.getDocument() );

			saver.start();
		}
	}

	/**	Save as action.
	 */

	protected class SaveAsAction extends AbstractAction
	{
		SaveAsAction()
		{
			super( "saveas" );
			setEnabled( false );
		}

		SaveAsAction( String nm )
		{
			super( nm );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			doSaveAs( e );
		}
	}

	/**	Performs save as action.
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doSaveAs( ActionEvent e )
	{
		String fileToSaveTo[]	= FileDialogs.save( this );

		if ( fileToSaveTo == null )
		{
			return;
		}

		currentFile		= new File( fileToSaveTo[ 0 ] , fileToSaveTo[ 1 ] );

		Thread saver	= new FileSaver( currentFile , editor.getDocument() );
		saver.start();

		title	= currentFile.toString();
		setTitle( title );
	}

	/**	Run action.
	 */

	protected class RunAction extends AbstractAction
	{
		RunAction()
		{
			super( "run" );
		}

		RunAction( String nm )
		{
			super( nm );
		}

		public void actionPerformed( ActionEvent e )
		{
			doRunAction( e );
		}
	}

	/** Run script, etc.
	 *
	 *	<p>
	 *	Override this to implement script execution, etc.
	 *	Default here is to do nothing.
	 *	</p>
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doRunAction( ActionEvent e )
	{
	}

	/**	Exit command.
	 */

	protected class ExitAction extends AbstractAction
	{
		ExitAction()
		{
			super( "exit" );
		}

		public void actionPerformed( ActionEvent e )
		{
			doExit( e );
		}
	}

	/**	Handle exit request.
	 *
	 *	<p>
	 *	Override this to add extra end-of-editing checks.
	 *	</p>
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doExit( ActionEvent e )
	{
		if ( !documentModified || readOnly )
		{
			reallyExit( e );
		}
		else
		{
			switch( showNotSavedDialog( "Exit" ) )
			{
				case ConfirmYNC.YES:
					doSave( e );
					doExit( e );
					break;

				case ConfirmYNC.NO:
					documentModified = false;
					doExit( e );
					break;

				case ConfirmYNC.CANCEL:
					break;
			}
		}
	}

	/** Confirm closing with unsaved changes.
	 * @param title Title of confirmation dialog.
	 * @return Dialog confirmation choice.
	*/

	protected int showNotSavedDialog( String title )
	{
		return
			ConfirmYNC.confirmYNC(
				"Document has not been saved. \nSave it now?" , this );
	}

	/**	Exit editor.
	 *
	 *	<p>
	 *	Override this to change end-of-editing behavior.
	 *	Called at the end of doExit() .
	 *	</p>
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void reallyExit( ActionEvent e )
	{
	}

	/** Create find action. */

	protected class FindAction extends AbstractAction
	{
		FindAction()
		{
			super( "find" );
			setEnabled( false );
		}

		FindAction( String nm )
		{
			super( nm );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			doFind( e );
		}
	}

	/**	Do find.
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doFind( ActionEvent e )
	{
		SearchAndReplaceDialog searchDialog =
			new SearchAndReplaceDialog( Notepad.this , editor , 0 );

		searchDialog.setSelectedIndex( 0 );

		searchDialog.setVisible( true );
	}

	/** Create replace action. */

	protected class ReplaceAction extends AbstractAction
	{
		ReplaceAction()
		{
			super( "replace" );
			setEnabled( false );
		}

		ReplaceAction( String nm )
		{
			super( nm );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			doReplace( e );
		}
	};

	/**	Do replace.
	 * @param	e	The ActionEvent that triggered invocation.
	 */

	protected void doReplace( ActionEvent e )
	{
		SearchAndReplaceDialog searchDialog =
			new SearchAndReplaceDialog( Notepad.this , editor , 0 );

		searchDialog.setSelectedIndex( 1 );

		searchDialog.setVisible( true );
	}

	/** Create page setup action. */

	protected class PageSetupAction extends AbstractAction
	{
		PageSetupAction()
		{
			super( "pagesetup" );
			setEnabled( false );
		}

		PageSetupAction( String nm )
		{
			super( nm );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			doPageSetup( e );
		}
	};

	/**	Printer page setup.
	 * @param	e	The ActionEvent that triggered invocation.
	*/

	protected void doPageSetup( ActionEvent e )
	{
		super.doPageSetup();
	}

	/** Create print action. */

	protected class PrintAction extends AbstractAction
	{
		PrintAction()
		{
			super( "print" );
			setEnabled( false );
		}

		PrintAction( String nm )
		{
			super( nm );
			setEnabled( false );
		}

		public void actionPerformed( ActionEvent e )
		{
			doPrint( e );
		}
	};

	/**	Print the text.
	 * @param	e	The ActionEvent that triggered invocation.
	*/

	protected void doPrint( ActionEvent e )
	{
		super.doPrint( editor , title );
	}

	/**	Thread to load a file into the text storage model
	 */

	protected class FileLoader extends Thread
	{
		/** The document into which to load the file's text. */

		Document doc;

		/**	The file from which to load text. */

		File f;

		/**	Create file loader.
		 *
		 *	@param	f		The file from which to read.
		 *	@param	doc		The document into which to read the file's text.
		 */

		FileLoader( File f , Document doc )
		{
			setPriority( 4 );

			this.f		= f;
			this.doc	= doc;
		}

		public void run()
		{
			try
			{
								//	Initialize the statusbar.

				JProgressBar progress	= new JProgressBar();

				progress.setMinimum( 0 );
				progress.setMaximum( (int)f.length() );
				progress.setString( null );
				progress.setStringPainted( true );

				statusBar.add( progress );
				statusBar.revalidate();

								//	Try to start reading.

				Reader in	= new FileReader( f );
				char[] buff	= new char[ 16384 ];
				int nch;

				while ( ( nch = in.read( buff , 0 , buff.length ) ) != -1 )
				{
					doc.insertString(
						doc.getLength() , new String( buff , 0 , nch ) , null );

					progress.setValue( progress.getValue() + nch );
				}
				in.close();
								//	Remove progress bar.

				statusBar.remove( progress );
				statusBar.revalidate();

								//	Clear all pending undo actions.

				resetUndoManager();

								//	Add undo listener.

				doc.addUndoableEditListener( undoHandler );

								// Add document listener.

				doc.addDocumentListener( new MyDocumentListener() );

								//	Enable menu items
								//	if document is not empty.

				enableMenuItems( doc.getLength() > 0 );

								//	Nothing selected yet do disable
								//	cut/copy .

				enableEditMenuItems( false );

								//	Document not modified yet.

				documentModified	= false;
			}
			catch ( IOException e )
			{
				System.err.println( e.toString() );
			}
			catch ( BadLocationException e )
			{
				System.err.println( e.getMessage() );
			}
		}
	}

	/**	Thread to save document to a text file.
	 */

	protected class FileSaver extends Thread
	{
		/** The document to save. */

		Document doc;

		/**	The file to which to write the document text. */

		File f;

		/**	Create file saver.
		 *
		 *	@param	f		The file to which to write.
		 *	@param	doc		The document whose text should be written.
		 */

		FileSaver( File f , Document doc )
		{
			setPriority( 4 );

			this.f		= f;
			this.doc	= doc;
		}

		public void run()
		{
			BufferedWriter out	= null;

			try
			{
								//	Get the document text.

				int documentLength	= doc.getLength();
				String documentText	= doc.getText( 0 , documentLength );

								//	Initialize the statusbar.

				JProgressBar progress	= new JProgressBar();

				progress.setMinimum( 0 );
				progress.setMaximum( documentLength );
				progress.setString( null );
				progress.setStringPainted( true );

				statusBar.add( progress );
				statusBar.revalidate();

								//	Try to write the text.

				out	= new BufferedWriter( new FileWriter( f ) );

				int segmentLength	= 16384;

				int nSegs			=
					( documentLength + segmentLength - 1 ) / segmentLength;

				int nch				= 0;
				int offset			= 0;

				for ( int iSeg	= 0 ; iSeg < nSegs ; iSeg++ )
				{
					offset	= iSeg * segmentLength;

					nch		=
						Math.max
						(
							0 ,
							Math.min
							(
								documentLength - offset ,
								segmentLength
							)
						);

					if ( nch > 0 )
					{
						out.write(
							documentText, offset , nch );
        			}

					progress.setValue( progress.getValue() + nch );
				}
								//	Remove progress bar.

				statusBar.remove( progress );
				statusBar.revalidate();

								//	Document not modified after save.

				documentModified	= false;
			}
			catch ( IOException e )
			{
				System.err.println( e.toString() );
			}
			catch ( BadLocationException e )
			{
				System.err.println( e.getMessage() );
			}
			finally
			{
				if ( out != null )
				{
					try
					{
						out.flush();
						out.close();
					}
					catch ( Exception e )
					{
					}
				}
			}
		}
	}

	/** Listens for changes to document being edited. */

	protected class MyDocumentListener extends CustomDocumentListener
	{
		protected boolean isRealChange( DocumentEvent e )
		{
			enableMenuItems( editor.getDocument().getLength() > 0 );

			boolean result		= super.isRealChange( e );

			documentModified	= documentModified || result;

			return result;
		}
	}

	/** Start undoable edit batch.  Implements EditBatch interface. */

	public void startEditBatch()
	{
		endEditBatch();

		undoableEditBatch	= new CompoundEdit();
		doingEditBatch		= true;
	}

	/** End undoable edit batch.  Implements EditBatch interface. */

	public void endEditBatch()
	{
		if (	( undoableEditBatch != null ) &&
				undoableEditBatch.isInProgress() )
		{
			undoableEditBatch.end();
			undo.addEdit( undoableEditBatch );

			undoAction.update();
			redoAction.update();
		}

		undoableEditBatch	= null;
		doingEditBatch		= false;
	}

	/**	Enable or disable an action.
	 *
	 *	@param	actionName	Name of the action to enable or disable.
	 *	@param	enabled		True to enable action, false otherwise.
	 *
	 *	<p>
	 *	Menu items and toolbar buttons associated with this action
	 *	are also enabled or disabled.
	 *	</p>
	 */

	protected void setActionState( String actionName , boolean enabled )
	{
		Action action	= (Action)commands.get( actionName );

		if ( action != null )
		{
			action.setEnabled( enabled );

			JMenuItem menuItem	= (JMenuItem)menuItems.get( actionName );

			if ( menuItem != null ) menuItem.setEnabled( enabled );

			JButton button		= (JButton)toolbarButtons.get( actionName );

			if ( button != null ) button.setEnabled( enabled );
		}
	}

	/**	Enables/disables menu items depending upon document contents.
	 *
	 *	@param	docHasText		True if document being edited has text.
	 */

	protected void enableMenuItems( boolean docHasText )
	{
		setActionState( "pagesetup"	, docHasText );
		setActionState( "print"		, docHasText );
		setActionState( "replace"	, docHasText );
		setActionState( "find"		, docHasText );
		setActionState( "save"		, docHasText );
		setActionState( "saveas"	, docHasText );
		setActionState( "selectall"	, docHasText );
	}

	/**	Enables/disables edit menu items depending upon whether text is selected.
	 *
	 *	@param	textSelected	True if there is a selection in effect.
	 */

	protected void enableEditMenuItems( boolean textSelected )
	{
		setActionState( "cut"	, textSelected );
		setActionState( "copy"	, textSelected );
	}

	/** Create caret listener to track cursor movements. */

	protected void createCaretListener()
	{
		editor.addCaretListener
		(
			new CaretListener()
			{
				public void caretUpdate( CaretEvent event )
				{
					handleCaretEvent( event );
				}
			}
		);
	}

	/** Handles caret change events.
	 *
	 *  @param	event	The caret event.
	 */

	protected void handleCaretEvent( CaretEvent event )
	{
								//	See if any text selected.
								//  dot will equal mark if not.

		int dot		= event.getDot();
		int mark	= event.getMark();

								//	Enable or disable menu items to match
								//	selection status.

		enableEditMenuItems( dot != mark );

								//	Update row and column values in
								//	status bar.
/*
								//	This commented out code does not handle
								//	tabs correctly.
		try
		{
			Element root	= getEditor().getDocument().getDefaultRootElement();
			int line		= root.getElementIndex( dot );
			int column		= dot - root.getElement( line ).getStartOffset();

			lineLabel.setText( ( line + 1 ) +  "" );
			columnLabel.setText( ( column + 1 ) + "" );
		}
		catch ( Exception e )
		{
		}
*/
		int line	= 1;
		int column	= 1;

		try
		{
			Rectangle current	= editor.modelToView( dot );
			Rectangle firstChar	= editor.modelToView( 0 );

			if ( current != null )
			{
				line	= ( ( current.y - firstChar.y ) / current.height ) + 1;
				column	= ( ( current.x - firstChar.x ) / fontCharWidth ) + 1;
        	}

			lineLabel.setText( line + "" );
			columnLabel.setText( column + "" );
		}
		catch( BadLocationException ble )
		{
		}
	}

	/**	Report an unexpected error.
	 *
	 *	@param	e	The exception.
	 *
	 *	<p>
	 *	Does nothing by default.  Override in subclasses as needed.
	 *	</p>
	 */

	protected void reportUnexpectedError( Exception e )
	{
	}

	/** Check if clipboard has pasteable data.
	 *
	 *	@return		true if clipboard has pasteable text.
	 */

	public boolean clipboardHasPasteableData()
	{
		boolean result	= true;

		if ( editor instanceof ClipboardHasPasteableData )
		{
			result	=
				((ClipboardHasPasteableData)editor).clipboardHasPasteableData();
		}

		return result;
    }

	/** Initialize clipboard.
	 */

	public void initializeClipboard()
	{
		Transferable contents	= SystemClipboard.getContents( this );

		if ( contents != null )
		{
			SystemClipboard.setContents( contents , defaultClipboardOwner );
		}
		else
		{
			SystemClipboard.setContents(
				new StringSelection( "" ) , defaultClipboardOwner );
		}
	}

    /** Clipboard observer.  Enable "paste" if clipboard has content.
     *
     *	<p>
     *	It appears "lostOwnership" is called before the clipboard data
     *	actually changes, so we wait half a second before checking for
     *	new clipboard data.
     *	</p>
     */

	class ClipboardObserver implements ClipboardOwner
	{
		public void lostOwnership( Clipboard clipboard , Transferable contents )
	    {
			new Thread
			(
				new Runnable()
				{
					public void run()
					{
						try
						{
							Thread.sleep( 500 );
						}
						catch ( Exception e )
						{
						}

			        	setActionState(
			        		"paste" , clipboardHasPasteableData() );

				        initializeClipboard();
					}
				}
			).start();
		}
	}

	/**	Cut to clipboard. */

	public void cut()
	{
		editor.cut();
	}

	/**	Copy to clipboard. */

	public void copy()
	{
		editor.copy();
	}

	/**	Paste from clipboard. */

	public void paste()
	{
		editor.paste();
	}

	/**	Is cut enabled? */

	public boolean isCutEnabled()
	{
		return !readOnly;
	}

	/**	Is copy enabled? */

	public boolean isCopyEnabled()
	{
		return true;
	}

	/**	Is paste enabled? */

	public boolean isPasteEnabled()
	{
		return !readOnly;
	}

	/**	Is anything selected which can be cut/copied? */

	public boolean isTextSelected()
	{
		String s	= editor.getSelectedText();

		return ( ( s != null ) && ( s.length() > 0 ) );
	}

	/** Unselect selection. */

	public void unselect()
	{
		if ( isUnselectEnabled() )
		{
			editor.select(
				editor.getCaretPosition() , editor.getCaretPosition() );
		}
	}

	/**	Is unselect enabled? */

	public boolean isUnselectEnabled()
	{
		return isTextSelected();
	}
}
