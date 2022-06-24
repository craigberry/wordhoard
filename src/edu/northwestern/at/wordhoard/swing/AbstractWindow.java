package edu.northwestern.at.wordhoard.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.wordhoard.swing.accounts.*;
import edu.northwestern.at.wordhoard.swing.tcon.*;
import edu.northwestern.at.wordhoard.swing.lexicon.*;
import edu.northwestern.at.wordhoard.swing.tables.*;
import edu.northwestern.at.wordhoard.swing.find.*;
import edu.northwestern.at.wordhoard.swing.dialogs.*;
import edu.northwestern.at.wordhoard.swing.work.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.server.model.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.sys.*;
import edu.northwestern.at.wordhoard.swing.bibtool.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.menus.*;

/**	An abstract base class for windows.
 */

public abstract class AbstractWindow extends XFrame
	implements AdjustAccountCommands
{

	/**	The parent window, or null if none. */

	protected AbstractWindow parentWindow;

	/**	The corpus associated with this window, or null if none. */

	private Corpus corpus;

	/**	Menus and menu items. */

	private JMenuItem aboutCmd = new JMenuItem("About WordHoard");
	private JMenuItem fontPrefsCmd = new JMenuItem("Font Preferences ...");
	private JMenuItem lookAndFeelCmd;
	private JMenuItem errorCmd = new JMenuItem("Send Error Report");
	private JMenuItem getInfoCmd = new JMenuItem("Get Info");
	protected JMenuItem annotateCmd = new JMenuItem("Annotate");
	private JMenuItem tconCmd = new JMenuItem("Table of Contents");
	private JMenuItem wordClassesCmd = new JMenuItem("Word Classes");
	private JMenuItem posCmd = new JMenuItem("Parts of Speech");
	private JMenuItem whCalcCmd = new JMenuItem("WordHoard Calculator");
	private JMenuItem loginCmd = new JMenuItem("Login...");
	private JMenuItem logoutCmd = new JMenuItem("Logout");
	private JMenuItem editorCmd = new JMenuItem("Editor");
	private JMenuItem runScriptCmd = new JMenuItem("Run Script");
	private JMenuItem manageAccountsCmd = new JMenuItem("Manage Accounts");
	private JMenuItem quitCmd = new JMenuItem("Quit");

    private	JMenu fileMenu = new JMenu("File");
    protected JMenuItem newWorkSetCmd = new JMenuItem("New Work Set");
    protected JMenuItem openWorkSetCmd = new JMenuItem("Open Work Set...");
    protected JMenuItem saveWorkSetCmd = new JMenuItem("Save Work Set...");
    protected JMenuItem saveWordSetCmd = new JMenuItem("Save as Word Set...");
	protected JMenuItem saveAsCmd = new JMenuItem("Save As...");
	private JMenuItem pageSetupCmd = new JMenuItem("Page Setup...");
	private JMenuItem printPreviewCmd = new JMenuItem("Print Preview...");
	private JMenuItem printCmd = new JMenuItem("Print...");
	private JMenuItem exportCmd = new JMenuItem("Export...");
	private JMenuItem importCmd = new JMenuItem("Import...");

    protected JMenu editMenu = new JMenu("Edit");
   	protected JMenuItem cutCmd = new JMenuItem("Cut");
    protected JMenuItem copyCmd = new JMenuItem("Copy");
    protected JMenuItem pasteCmd = new JMenuItem("Paste");
    protected JMenuItem selectAllCmd = new JMenuItem("Select All");
    protected JMenuItem unselectCmd = new JMenuItem("Unselect");
    protected JMenuItem clearCmd = new JMenuItem("Clear");

	private JMenu findMenu = new JMenu("Find");
	private JMenuItem goToWordCmd = new JMenuItem("Go To Word...");
	private JMenuItem queryToolCmd = new JMenuItem("Find Lemmata...");
	private JMenuItem findWordsCmd = new JMenuItem("Find Words...");
	private JMenuItem findWorksCmd = new JMenuItem("Find Works...");

	private JMenu viewsMenu = new JMenu("Views");
	private JMenuItem noLineNumsCmd =
		new JRadioButtonMenuItem("No Line Numbers");
	private JMenuItem allLineNumsCmd =
		new JRadioButtonMenuItem("All Lines Numbered");
	private JMenuItem fiveLineNumberOrStanzaNumsCmd =
		new JRadioButtonMenuItem("Number Every Fifth Line");
	private JMenuItem translationsCmd =
		new JMenuItem("Translations, Transliterations, Etc. ...");
	private JMenuItem showHideAnnotationMarkersCmd =
		new JMenuItem("Hide Annotation Markers");
	private JMenuItem showHideAnnotationPanelCmd =
		new JMenuItem("Show Annotation Panel");

	private JMenuItem closeCmd = new JMenuItem("Close");
	private JMenuItem closeAllCmd =
		new JMenuItem("Close All");

	private JMenuItem[] lexiconCmds;

								//	Calculator menu items.
	private SetsMenu setsMenu;
	private JMenu analysisMenu;
	private JMenu helpMenu;
	private FileMenu calcFileMenu;

	protected WindowListener windowListener	= null;

	//	True if Views menu item is titled "Number Stanzas". False if
	//	titled "Number Every Fifth Line".

	private boolean numberStanzas = true;

	/**	Create a new abstract window.
	 *
	 *	@param	title			The window title.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *
	 *	@throws	PersistenceException
	 */

	public AbstractWindow (String title, AbstractWindow parentWindow)
		throws PersistenceException
	{

		super(title);
		this.parentWindow = parentWindow;
		if (parentWindow == null) {
			Corpus[] corpora = CachedCollections.getCorpora();
			if (corpora.length > 0) corpus = corpora[0];
		} else {
			corpus = parentWindow.getCorpus();
		}

		createWindowData();
	}

	/**	Create the actual window data.
	 *
	 *	@throws	PersistenceException
	 */

	protected void createWindowData()
		throws PersistenceException
	{
		windowListener =
			new WindowAdapter() {
				public void windowActivated (WindowEvent event) {
					try {
						handleFirstWindowActivation();
						removeWindowListener(this);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			};

		addWindowListener( windowListener );

		aboutCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleAboutCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		fontPrefsCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleFontPrefsCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		errorCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleErrorCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        getInfoCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_I, Env.MENU_SHORTCUT_KEY_MASK));
		getInfoCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleGetInfoCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

//		annotateCmd.setAccelerator(KeyStroke.getKeyStroke(
//			KeyEvent.VK_A, Env.MENU_SHORTCUT_KEY_MASK));

		annotateCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleAnnotateCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		tconCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleTConCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		wordClassesCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleWordClassesCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		posCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handlePosCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		whCalcCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleWhCalcCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		loginCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					handleLoginCmd();
				}
			}
		);

		logoutCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					handleLogoutCmd();
				}
			}
		);

		editorCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					handleEditorCmd();
				}
			}
		);

		runScriptCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					handleRunScriptCmd();
				}
			}
		);

		manageAccountsCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleManageAccountsCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        quitCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_Q, Env.MENU_SHORTCUT_KEY_MASK));
		quitCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					handleQuitCmd();
				}
			}
		);

		newWorkSetCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleNewWorkSetCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		openWorkSetCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleOpenWorkSetCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		saveWorkSetCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleSaveWorkSetCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		saveWordSetCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleSaveWordSetCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		saveAsCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					handleSaveAsCmd();
				}
			}
		);

		pageSetupCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					handlePageSetupCmd();
				}
			}
		);

		printPreviewCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					handlePrintPreviewCmd();
				}
			}
		);

        printCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_P, Env.MENU_SHORTCUT_KEY_MASK));
		printCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					handlePrintCmd();
				}
			}
		);

		exportCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					handleExportCmd();
				}
			}
		);

		importCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					handleImportCmd();
				}
			}
		);

        cutCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_X, Env.MENU_SHORTCUT_KEY_MASK));
        cutCmd.setActionCommand(
        	(String)TransferHandler.getCutAction().getValue(Action.NAME));
		cutCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleCutCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        copyCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_C, Env.MENU_SHORTCUT_KEY_MASK));
        copyCmd.setActionCommand(
        	(String)TransferHandler.getCopyAction().getValue(Action.NAME));
		copyCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleCopyCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        pasteCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_V, Env.MENU_SHORTCUT_KEY_MASK));
        pasteCmd.setActionCommand(
        	(String)TransferHandler.getPasteAction().getValue(Action.NAME));
		pasteCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handlePasteCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        selectAllCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_A, Env.MENU_SHORTCUT_KEY_MASK));
		selectAllCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleSelectAllCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		unselectCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleUnselectCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		clearCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleClearCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        goToWordCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_G, Env.MENU_SHORTCUT_KEY_MASK));
		goToWordCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleGoToWordCmd(null);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        findWordsCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_F, Env.MENU_SHORTCUT_KEY_MASK));
		findWordsCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleFindWordsCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        findWorksCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_L, Env.MENU_SHORTCUT_KEY_MASK));
		findWorksCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleFindWorksCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        queryToolCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_Y, Env.MENU_SHORTCUT_KEY_MASK));
		queryToolCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleQueryToolCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        noLineNumsCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_0, Env.MENU_SHORTCUT_KEY_MASK));
		noLineNumsCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleLineNumberCmd(0);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        allLineNumsCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_1, Env.MENU_SHORTCUT_KEY_MASK));
		allLineNumsCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleLineNumberCmd(1);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        fiveLineNumberOrStanzaNumsCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_5, Env.MENU_SHORTCUT_KEY_MASK));
		fiveLineNumberOrStanzaNumsCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleLineNumberCmd(numberStanzas ? -1 : 5);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        translationsCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_T, Env.MENU_SHORTCUT_KEY_MASK));
		translationsCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleTranslationsCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        showHideAnnotationMarkersCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_J, Env.MENU_SHORTCUT_KEY_MASK));
		showHideAnnotationMarkersCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleShowHideAnnotationMarkersCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

        showHideAnnotationPanelCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_K, Env.MENU_SHORTCUT_KEY_MASK));
		showHideAnnotationPanelCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleShowHideAnnotationPanelCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		ButtonGroup lineNumsGroup = new ButtonGroup();
		lineNumsGroup.add(noLineNumsCmd);
		lineNumsGroup.add(allLineNumsCmd);
		lineNumsGroup.add(fiveLineNumberOrStanzaNumsCmd);
		fiveLineNumberOrStanzaNumsCmd.setSelected(true);

        closeCmd.setAccelerator(KeyStroke.getKeyStroke(
        	KeyEvent.VK_W, Env.MENU_SHORTCUT_KEY_MASK));
		closeCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleCloseCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		closeAllCmd.setAccelerator(KeyStroke.getKeyStroke(
			KeyEvent.VK_W, Env.MENU_SHORTCUT_SHIFT_KEY_MASK));
		closeAllCmd.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						handleCloseAllCmd();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		Corpus[] corpora = CachedCollections.getCorpora();
		lexiconCmds = new JMenuItem[corpora.length];
		for (int i = 0; i < corpora.length; i++) {
			final Corpus corpus = corpora[i];
			JMenuItem lexiconCmd =
				new JMenuItem(corpus.getTitle() + " Lexicon");
			lexiconCmds[i] = lexiconCmd;
			lexiconCmd.addActionListener(
				new ActionListener () {
					public void actionPerformed (ActionEvent event) {
						try {
							handleLexiconCmd(corpus);
						} catch (Exception e) {
							Err.err(e);
						}
					}
				}
			);
		}

		editMenu.addMenuListener(
			new MenuListener() {
				public void menuSelected (MenuEvent event) {
					try {
						handleEditMenuSelected();
					} catch (Exception e) {
						Err.err(e);
					}
				}
				public void menuCanceled (MenuEvent event) {
					try {
						handleEditMenuCanceledOrDeselected();
					} catch (Exception e) {
						Err.err(e);
					}
				}
				public void menuDeselected (MenuEvent event) {
					try {
						handleEditMenuCanceledOrDeselected();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		fileMenu.addMenuListener(
			new MenuListener() {
				public void menuSelected (MenuEvent event) {
					try {
						handleFileMenuSelected();
					} catch (Exception e) {
						Err.err(e);
					}
				}
				public void menuCanceled (MenuEvent event) { }
				public void menuDeselected (MenuEvent event) { }
			}
		);

		enableGetInfoCmd(false);
		enableLineNumberCmds(false);
		enableTranslationsCmd(false);
		enableShowHideAnnotationMarkersCmd(false);
		enableShowHideAnnotationPanelCmd(false);

		if (Env.MACOSX && !WordHoardSettings.getUseScreenMenuBar()) {
			fileMenu.add(aboutCmd);
			fileMenu.addSeparator();
		}
        fileMenu.add(newWorkSetCmd);
        fileMenu.add(openWorkSetCmd);
        fileMenu.add(saveWorkSetCmd);
        fileMenu.add(saveWordSetCmd);
		fileMenu.addSeparator();
		fileMenu.add(saveAsCmd);
		fileMenu.addSeparator();
		fileMenu.add(pageSetupCmd);
		if (!Env.MACOSX) fileMenu.add(printPreviewCmd);
		fileMenu.add(printCmd);
		fileMenu.addSeparator();
		fileMenu.add(exportCmd);
		fileMenu.add(importCmd);
		fileMenu.addSeparator();
		fileMenu.add(editorCmd);
		fileMenu.add(runScriptCmd);
		fileMenu.addSeparator();
		fileMenu.add(fontPrefsCmd);
		if (!Env.MACOSX && !Env.WINDOWSOS) {
			lookAndFeelCmd = new LookAndFeelSubMenu();
			fileMenu.add(lookAndFeelCmd);
		}
		fileMenu.addSeparator();
		fileMenu.add(errorCmd);
		fileMenu.addSeparator();
		fileMenu.add(getInfoCmd);
		fileMenu.add(annotateCmd);
		fileMenu.addSeparator();
		fileMenu.add(loginCmd);
		fileMenu.add(logoutCmd);
		fileMenu.addSeparator();
		fileMenu.add(manageAccountsCmd);
		if (!Env.MACOSX || !WordHoardSettings.getUseScreenMenuBar()) {
			fileMenu.addSeparator();
			fileMenu.add(quitCmd);
		}

		//annotateCmd.setVisible(
		//	WordHoardSettings.getAnnotationWriteServerURL() != null);

								//	Used only to handle Import and Export.

		calcFileMenu = new FileMenu(null, this);

		editMenu.add(cutCmd);
		editMenu.add(copyCmd);
		editMenu.add(pasteCmd);
		editMenu.addSeparator();
		editMenu.add(selectAllCmd);
		editMenu.add(unselectCmd);
		editMenu.addSeparator();
		editMenu.add(clearCmd);

//		editMenu	= new EditMenu( this , null );

		findMenu.add(goToWordCmd);
		findMenu.add(queryToolCmd);
		findMenu.add(findWordsCmd);
		findMenu.add(findWorksCmd);

		viewsMenu.add(noLineNumsCmd);
		viewsMenu.add(allLineNumsCmd);
		viewsMenu.add(fiveLineNumberOrStanzaNumsCmd);
		viewsMenu.addSeparator();
		viewsMenu.add(translationsCmd);
		viewsMenu.addSeparator();
		viewsMenu.add(showHideAnnotationMarkersCmd);
		viewsMenu.add(showHideAnnotationPanelCmd);

		JMenu windowsMenu = getWindowsMenu();
		windowsMenu.add(closeCmd);
		windowsMenu.add(closeAllCmd);
		windowsMenu.addSeparator();
		windowsMenu.add(tconCmd);
		windowsMenu.add(whCalcCmd);
		windowsMenu.addSeparator();
		windowsMenu.add(wordClassesCmd);
		windowsMenu.add(posCmd);
		windowsMenu.addSeparator();

		for (int i = 0; i < lexiconCmds.length; i++) {
			windowsMenu.add(lexiconCmds[i]);
		}

		windowsMenu.addSeparator();

								//	Calculator menus.

		setsMenu		= new SetsMenu(this);

		setsMenu.addMenuListener( setsMenu.setsMenuListener );

		analysisMenu	= new AnalysisMenu(this);
		helpMenu		= new HelpMenu(this);

								//	Add menus to menu bar.

		JMenuBar menuBar = new JMenuBar();

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(setsMenu);
		menuBar.add(findMenu);
		menuBar.add(viewsMenu);
		menuBar.add(analysisMenu);
		menuBar.add(windowsMenu);
		menuBar.add(helpMenu);

								//	Adjust menu items for logged-in status.

		adjustAccountCommands();

								//	Add menu bar to this window.
		setJMenuBar(menuBar);
	}

	/**	Gets the parent window.
	 *
	 *	@return		The parent window, or null if none.
	 */

	public AbstractWindow getParentWindow () {
		return parentWindow;
	}

	/**	Removes the window listener, is any.
	 */

	public void removeTheWindowListener () {
		if (windowListener != null) {
			removeWindowListener(windowListener);
			windowListener = null;
		}
	}

	/**	Gets the corpus associated with this window.
	 *
	 *	@return		The corpus associated with this window, or null if none.
	 */

	public Corpus getCorpus () {
		return corpus;
	}

	/**	Sets the corpus associated with this window.
	 *
	 *	@param	corpus		The corpus associated with this window, or null
	 *						if none.
	 */

	public void setCorpus (Corpus corpus) {
		this.corpus = corpus;
	}

	/**	Gets the work panel associated with this window.
	 *
	 *	<p>Subclasses which contain a work panel should override this
	 *	method to return the work panel. The default return value is
	 *	null.
	 *
	 *	@return		The work panel, or null if none.
	 */

	public WorkPanel getWorkPanel () {
		return null;
	}

	/**	Handles the first window activation event.
	 *
	 *	<p>Subclasses may override this method to request focus or take
	 *	other initialization actions when a window is first created and
	 *	activated. The default action is to do nothing.
	 */

	public void handleFirstWindowActivation () {
	}

	/**	Handles the last window closed.
	 *
	 *	<p>When the last window is closed we quit the program.
	 */

	public void handleLastWindowClosed () {
		WordHoard.quit();
	}

	/**	Handles the "About" command.
	 *
	 *	@throws	Exception
	 */

	public void handleAboutCmd ()
		throws Exception
	{
		AboutWindow.open(this);
	}

	/**	Display "About" box.
	 */

	public void about()
	{
		try
		{
			handleAboutCmd();
		}
		catch( Exception e )
		{
		}
	}

	/**	Handles the "Font Preferences" command.
	 *
	 *	@throws	Exception
	 */

	public void handleFontPrefsCmd ()
		throws Exception
	{
		prefs();
	}

	/**	Route "prefs" to hamdleFontPrefs command.
	 *
	 *	@throws	Exception
	 */

	public void prefs() {
		new FontPrefsDialog(this);
	}

	/**	Sends an error report.
	 *
	 *	@param	word		Word, or null if none.
	 *
	 *	@throws	Exception
	 */

	public void sendErrorReport (Word word)
		throws Exception
	{
		String urlStr = "mailto:martinmueller@northwestern.edu?subject=" +
			"Error%20in%20WordHoard";
		if (word != null)
			urlStr = urlStr + "%20word%20" + word.getTag();
		WebStart.showDocument(urlStr);
	}

	/**	Handles the "Send Error Report" command.
	 *
	 *	@throws	Exception
	 */

	public void handleErrorCmd ()
		throws Exception
	{
		WorkPanel workPanel = getWorkPanel();
		Word word = workPanel == null ? null :
			workPanel.getWord();
		sendErrorReport(word);
	}

	/**	Handles the "Get Info" command.
	 *
	 *	<p>Subclasses may override this method to handle the "Get Info"
	 *	command. The default action is to do nothing.
	 *
	 *	@throws	Exception
	 */

	public void handleGetInfoCmd ()
		throws Exception
	{
	}

	/**	Handles the "Annotate" command.
	 *
	 *	<p>Subclasses may override this method to handle the "Annotate"
	 *	command. The default action is to do nothing.
	 *
	 *	@throws	Exception
	 */

	public void handleAnnotateCmd ()
		throws Exception
	{
	}

	/**	Handles the "Table of Contents" command.
	 *
	 *	@throws	Exception
	 */

	public void handleTConCmd ()
		throws Exception
	{
		TableOfContentsWindow.open();
	}

	/**	Handles the "Word Classes" command.
	 *
	 *	@throws	Exception
	 */

	public void handleWordClassesCmd ()
		throws Exception
	{
		WordClassWindow.open(this);
	}

	/**	Handles the "WordHoard Calculator" command.
	 *
	 *	@throws	Exception
	 */

	public void handleWhCalcCmd ()
		throws Exception
	{
		WordHoardCalculatorWindow.open( true );
	}

	/**	Handles the "Parts of Speech" command.
	 *
	 *	@throws	Exception
	 */

	public void handlePosCmd ()
		throws Exception
	{
		PosWindow.open(this);
	}

	/**	Handles the "Login" command.
	 */

	public void handleLoginCmd () {
		LoginDialog dlog = new LoginDialog(this);
		if (dlog.canceled()) return;
		adjustAllAccountCommands();
		new InformationMessage("You are logged in as " +
			WordHoardSettings.getUserID(), this);
	}

	/**	Handles the "Logout" command.
	 */

	public void handleLogoutCmd () {
		LoginDialog.logout();
		adjustAllAccountCommands();

//
//								Uncomment call below to close
//								Manage Accounts window when current
//								user logs out.
//
//		ManageAccountsWindow.close();
//
		new InformationMessage("You are logged out.", this);
	}

	/**	Handles the "Editor" command.
	 */

	public void handleEditorCmd () {
		WordHoardCalculatorWindow.getCalculatorWindow().simpleEditor();
	}

	/**	Handles the "Run Script" command.
	 */

	public void handleRunScriptCmd () {
		WordHoardCalculatorWindow.getCalculatorWindow().runScript();
	}

	/**	Handles the "Manage Accounts" command.
	 *
	 *	@throws Exception
	 */

	public void handleManageAccountsCmd ()
		throws Exception
	{
		ManageAccountsWindow.open(this);
	}

	/**	Adjusts the account commands in all windows.
	 *
	 *	<p>Enables/disables the "Logout" and "Manage Account" commands in all
	 *	open windows and adjusts the text of the "Logout" command in all open
	 *	windows to read "Logout xxx" when the user is logged in with username
	 *	"xxx".
	 */

	private void adjustAllAccountCommands () {
		WindowsMenuManager[] windows = getAllOpenWindows();
		for (int i = 0; i < windows.length; i++) {
			WindowsMenuManager window = (WindowsMenuManager)windows[i];
			if (window instanceof AdjustAccountCommands) {
				((AdjustAccountCommands)window).adjustAccountCommands();
			}
		}
	}

	/**	Adjusts the account commands in this window.
	 *
	 *	<p>
	 *	Enables/disables the "Logout", "Manage Account", and "Annotate"
	 *	commands and adjusts the text of the "Logout" command to read
	 *	"Logout xxx" when the user is logged in with username "xxx".
	 *	Also sets the availability of the Query and Sets menus, and the
	 *	Export and Import commands of the File menu.
	 *	</p>
	 */

	public void adjustAccountCommands () {
		Account account = WordHoardSettings.getLoginAccount();
		logoutCmd.setText(account == null ? "Logout" :
			"Logout " + account.getUsername());
		logoutCmd.setEnabled(account != null);
		loginCmd.setEnabled(account == null);
		manageAccountsCmd.setEnabled(account != null &&
			account.getCanManageAccounts());
		exportCmd.setEnabled(account != null);
		importCmd.setEnabled(account != null);
		setsMenu.handleLoggedIn(account != null);
		annotateCmd.setEnabled(isAnnotateAvailable() && (account != null));
	}

	/**	Handles the "Quit" command.
	 */

	public void handleQuitCmd () {
		if (WordHoardSettings.getWarnWhenQuitting() &&
			ConfirmYNC.confirmYN( WordHoardSettings.getString(
				"areYouSureYouWantToQuit" ,
				"Are you sure you want to quit?"), this) == ConfirmYNC.YES) {
			WordHoard.quit();
		} else {
			WordHoard.quit();
		}
	}

	/**	Quits the program.
	 */

	public void quit () {
		handleQuitCmd();
	}

	/**	Handles file menu selected.
	 *
	 *
	 *	@throws	Exception
	 */

	public void handleFileMenuSelected ()
		throws Exception
	{
		newWorkSetCmd.setEnabled(true);
		openWorkSetCmd.setEnabled(true);
		saveWorkSetCmd.setEnabled(false);
		saveWordSetCmd.setEnabled(false);
		saveAsCmd.setEnabled(findSaveableComponent() != null);
	}

	/**	Gets the currently focused text component.
	 *
	 *	@return		The currently focused text component, or null if none.
	 */

	private JTextComponent getFocusedTextComponent () {
		KeyboardFocusManager kfm =
			KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Component c = kfm.getPermanentFocusOwner();
		if (c != null && c instanceof JTextComponent &&
			((JTextComponent)c).getTopLevelAncestor() == this)
		{
			return (JTextComponent)c;
		} else {
			return null;
		}
	}

	/**	Handles "New Work Set" command.
	 */

	public void handleNewWorkSetCmd()
	{
		try {
			new WorkSetWindow(
				TableOfContentsWindow.getTableOfContentsWindow());
		} catch (Exception e) {
			Err.err(e);
		}
	}

	/**	Handles "Open Work Set" command.
	 */

	public void handleOpenWorkSetCmd()
	{
		try {
			WorkSetWindow wsw =
				new WorkSetWindow(
					TableOfContentsWindow.getTableOfContentsWindow());
			wsw.loadWorkSet();
		} catch (Exception e) {
			Err.err(e);
		}
	}

	/**	Handles "Save Work Set" command.
	 */

	public void handleSaveWorkSetCmd() {
	}

	/**	Handles "Save Word Set" command.
	 */

	public void handleSaveWordSetCmd() {
	}

	/**	Handles "Save as" command.
	 */

	public void handleSaveAsCmd() {
		SaveToFile saveable	= findSaveableComponent();
		if (saveable != null) {
			((SaveToFile)saveable).saveToFile( this );
		}
	}

	/**	Handles "Page Setup" command.
	 */

	public void handlePageSetupCmd() {
		doPageSetup();
	}

	/**	Handles "Print Preview" command.
	 */

	public void handlePrintPreviewCmd() {
		doPrintPreview(findPrintableComponent(), getTitle());
	}

	/**	Handles "Print" command.
	 */

	public void handlePrintCmd() {
		doPrint(findPrintableComponent(), getTitle());
	}

	/**	Find a printable component.
	 *
	 *	@return		A printable component.
	 */

	protected Component findPrintableComponent() {
		Component result = getContentPane();
		ResultsPanel resultsPanel =
			BaseMenu.getResultsPanel(getContentPane());
		if ((resultsPanel != null) && (resultsPanel.getResults()!= null)) {
			result = (Component)resultsPanel.getResults();
//			result = (Component)resultsPanel;
		}
		return result;
	}

	/**	Find a saveable component.
	 *
	 *	@return		A saveable component.  Null=none by default.
	 */

	protected SaveToFile findSaveableComponent() {
		SaveToFile result= null;
		ResultsPanel resultsPanel		=
			BaseMenu.getResultsPanel(getContentPane());
		if ((resultsPanel != null) && (resultsPanel.getResults()!= null)) {
			Object results = resultsPanel.getResults();
			if ((results != null) && (results instanceof SaveToFile)) {
				result = (SaveToFile)results;
			}
		}
		return result;
	}

	/**	Find an editable component.
	 *
	 *	@return		An editable component.
	 */

	protected Component findEditableComponent() {
		KeyboardFocusManager kfm =
			KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Component c = kfm.getPermanentFocusOwner();
		if ((c != null) && (c instanceof CutCopyPaste) &&
			(c instanceof JComponent) &&
			(SwingUtils.getParentWindow((JComponent)c) == this))
		{
			return c;
		} else {
			return null;
		}
	}

	/**	Find a component allowing text selection.
	 *
	 *	@return		A component allowing text selection.
	 */

	protected Component findSelectableTextComponent() {
		KeyboardFocusManager kfm =
			KeyboardFocusManager.getCurrentKeyboardFocusManager();
		Component c = kfm.getPermanentFocusOwner();
		if ((c != null) && (c instanceof SelectAll) &&
			(c instanceof JComponent) &&
			(SwingUtils.getParentWindow((JComponent)c) == this))
		{
			return c;
		} else {
			return null;
		}
	}

	/**	Handles "Export" command.
	 */

	protected void handleExportCmd() {
		calcFileMenu.doExport();
	}

	/**	Handles "Import" command.
	 */

	protected void handleImportCmd() {
		calcFileMenu.doImport();
	}

	/**	Handles edit menu selected.
	 *
	 *	<p>The default is to enable and disable the commands in the edit
	 *	menu appropriately for a window which contains text components.
	 *	If there is no currently focused text component, we look for a
	 *	focused component that implements the CutCopyPaste and/or
	 *	SelectAll interfaces.  If neither is found, all commands are
	 *	disabled. Otherwise, cut and copy are enabled iff the selection
	 *	is not empty, paste is enabled if the system clipboard contains a
	 *	non-empty text string, and select all is enabled if the text or
	 *	editable component is not empty.</p>
	 *
	 *	<p>Subclasses which handle transfereable data other than text in
	 *	text components should override this method.</p>
	 *
	 *	@throws	Exception
	 */

	public void handleEditMenuSelected ()
		throws Exception
	{
		clearCmd.setEnabled(false);
		JTextComponent textComponent = getFocusedTextComponent();
		if (textComponent == null) {
			cutCmd.setEnabled(false);
			copyCmd.setEnabled(false);
			pasteCmd.setEnabled(false);
			selectAllCmd.setEnabled(false);
			Component component = findEditableComponent();
			if (component != null) {
				if (component instanceof CutCopyPaste) {
					setCutCopyPaste((CutCopyPaste)component);
				}
				if (component instanceof SelectAll) {
					setSelectAll((SelectAll)component);
				}
				if (pasteCmd.isEnabled()) {
					pasteCmd.setEnabled(clipboardHasPasteableData());
				}
			}
		} else {
			boolean selectionNotEmpty =
				textComponent.getSelectionEnd() >
				textComponent.getSelectionStart();
			cutCmd.setEnabled(selectionNotEmpty);
			copyCmd.setEnabled(selectionNotEmpty);
			pasteCmd.setEnabled(clipboardHasPasteableData());
			boolean textNotEmpty = textComponent.getText().length() > 0;
			selectAllCmd.setEnabled(textNotEmpty);
		}
	}

	/**	Handles edit menu canceled or deselected.
	 *
	 *	<p>When the Edit menu is canceled or deselected, we reenable all
	 *	the commands so that the command key shortcuts will still work.
	 */

	public void handleEditMenuCanceledOrDeselected () {
		enableCutCmd(true);
		enableCopyCmd(true);
		enablePasteCmd(true);
		enableSelectAllCmd(true);
	}

	/**	Handles the "Cut" command.
	 *
	 *	<p>The default is to cut the selected text from the currently
	 *	focused editable component, if any.</p>
	 *
	 *	<p>Subclasses which can cut transfereable data other than text in
	 *	text components should override this method.</p>
	 *
	 *	@throws	Exception
	 */

	public void handleCutCmd ()
		throws Exception
	{
		JTextComponent textComponent = getFocusedTextComponent();
		if (textComponent == null) {
			Component component	= findEditableComponent();
			if ((component != null) &&
				(component instanceof CutCopyPaste)) {
				CutCopyPaste c	= (CutCopyPaste)component;
				if ( c.isCutEnabled() ) c.cut();
			}
		} else {
			textComponent.cut();
		}
	}

	/**	Handles the "Copy" command.
	 *
	 *	<p>The default is to copy the selected text from the currently
	 *	focused editable component, if any</p>
	 *
	 *	<p>Subclasses which can copy transfereable data other than text in
	 *	text components should override this method.</p>
	 *
	 *	@throws	Exception
	 */

	public void handleCopyCmd ()
		throws Exception
	{
		JTextComponent textComponent = getFocusedTextComponent();
		if (textComponent == null) {
			Component component	= findEditableComponent();
			if ((component != null) &&
				(component instanceof CutCopyPaste)) {
				CutCopyPaste c	= (CutCopyPaste)component;
				if ( c.isCopyEnabled() ) c.copy();
			}
		} else {
			textComponent.copy();
		}
	}

	/**	Handles the "Paste" command.
	 *
	 *	<p>The default is to paste the system clipboard text into the
	 *	currently focused editable component, if any.</p>
	 *
	 *	<p>Subclasses which can paste transfereable data other than text into
	 *	text components should override this method.</p>
	 *
	 *	@throws	Exception
	 */

	public void handlePasteCmd ()
		throws Exception
	{
		JTextComponent textComponent = getFocusedTextComponent();
		if (textComponent == null) {
			Component component	= findEditableComponent();
			if ((component != null) &&
				(component instanceof CutCopyPaste)) {
				CutCopyPaste c	= (CutCopyPaste)component;
				if ( c.isPasteEnabled() ) c.paste();
			}
		} else {
			textComponent.paste();
		}
	}

	/**	Handles the "Select All" command.
	 *
	 *	<p>The default is to select all of the text in the currently
	 *	focused editable component, if any.</p>
	 *
	 *	<p>Subclasses which select data other than text should override
	 *	this method.</p>
	 *
	 *	@throws	Exception
	 */

	public void handleSelectAllCmd ()
		throws Exception
	{
		JTextComponent textComponent = getFocusedTextComponent();
		if (textComponent == null) {
			Component component	= findSelectableTextComponent();
			if ((component != null) &&
				(component instanceof SelectAll)) {
				SelectAll c	= (SelectAll)component;
				if ( c.isSelectAllEnabled() ) c.selectAll();
			}
		} else {
			textComponent.selectAll();
		}
	}

	/**	Handles the "Unselect" command.
	 *
	 *	<p>The default is to unselect any selected text in the
	 *	currently focused editable component, if any.</p>
	 *
	 *	<p>Subclasses which select data other than text should override
	 *	this method.</p>
	 *
	 *	@throws	Exception
	 */

	public void handleUnselectCmd ()
		throws Exception
	{
		JTextComponent textComponent = getFocusedTextComponent();
		if (textComponent == null) {
			Component component	= findSelectableTextComponent();
			if ((component != null) &&
				(component instanceof SelectAll)) {
				SelectAll c	= (SelectAll)component;
				if ( c.isUnselectEnabled() ) c.unselect();
			}
		} else {
			textComponent.select(
				textComponent.getCaretPosition(),
				textComponent.getCaretPosition());
		}
	}

	/**	Handles the "Clear" command.
	 *
	 *	<p>The default is to do nothing.  A window may choose to clear its
	 * 	display in response to this command.
	 *
	 *	@throws	Exception
	 */

	public void handleClearCmd ()
		throws Exception
	{
	}

	/**	Handles the "Go To Word" command.
	 *
	 *	@param	str		Initial word tag string for dialog field, or null if
	 *					none.
	 *
	 *	@throws	Exception
	 */

	public void handleGoToWordCmd (String str)
		throws Exception
	{
		GoToDialog dlog = new GoToDialog(str, this);
		if (dlog.canceled()) return;
		String tag = dlog.getTag();
		Word word = WordHoard.getPm().getWordByTag(tag);
		if (word == null) {
			new ErrorMessage("No such word: " + tag, this);
			return;
		}
		Work work = word.getWork();
		Corpus newCorpus = work.getCorpus();
		new WorkWindow(newCorpus, work, this).getWorkPanel().goTo(word);
	}

	/**	Handles the "Find Words" command.
	 *
	 *	@throws	Exception
	 */

	public void handleFindWordsCmd ()
		throws Exception
	{
		new FindWindow(corpus, null, this);
	}

	/**	Handles the "Find Works" command.
	 *
	 *	@throws	Exception
	 */

	public void handleFindWorksCmd ()
		throws Exception
	{
		new FindWorkWindow(getCorpus(), this);
	}

	/**	Handles the "Query Tool" command.
	 *
	 *	@throws	Exception
	 */

	public void handleQueryToolCmd ()
		throws Exception
	{
		new SearchCriteriaWindow( this );
	}

	/**	Handles the line number commands.
	 *
	 *	<p>Subclasses may override this method to handle the line number
	 *	commands. The default action is to do nothing.
	 *
	 *	@param	n		0 for no line numbers, 1 to number every line,
	 *					or 5 to number every fifth line.
	 *
	 *	@throws	Exception
	 */

	public void handleLineNumberCmd (int n)
		throws Exception
	{
	}

	/**	Handles the "Translations" command.
	 *
	 *	<p>Subclasses may override this method to handle the "Translations"
	 *	command. The default action is to do nothing.
	 *
	 *	@throws	Exception
	 */

	public void handleTranslationsCmd ()
		throws Exception
	{
	}

	/**	Handles the "Show/Hide Annotation Markers" command.
	 *
	 *	<p>Subclasses may override this method to handle the "Show/Hide
	 *	Annotation Markers" command. The default action is to do nothing.
	 *
	 *	@throws	Exception
	 */

	public void handleShowHideAnnotationMarkersCmd ()
		throws Exception
	{
	}

	/**	Handles the "Show/Hide Annotation Panel" command.
	 *
	 *	<p>Subclasses may override this method to handle the "Show/Hide
	 *	Annotation Panel" command. The default action is to do nothing.
	 *
	 *	@throws	Exception
	 */

	public void handleShowHideAnnotationPanelCmd ()
		throws Exception
	{
	}

	/**	Handles the lexicon commands.
	 *
	 *	@param	corpus		The corpus.
	 *
	 *	@throws Exception
	 */

	public void handleLexiconCmd (Corpus corpus)
		throws Exception
	{
		LexiconWindow.open(corpus, this);
	}

	/**	Handles the "Close" command.
	 *
	 *	@throws	Exception
	 */

	public void handleCloseCmd ()
		throws Exception
	{
		dispatchEvent(new WindowEvent(AbstractWindow.this,
			WindowEvent.WINDOW_CLOSING));
	}

	/**	Handles the "Close All" command.
	 *
	 *	@throws	Exception
	 */

	public void handleCloseAllCmd ()
		throws Exception
	{
		TableOfContentsWindow.open();
		WindowsMenuManager[] windows = WindowsMenuManager.getAllOpenWindows();
		for (int i = 0; i < windows.length; i++) {
			WindowsMenuManager window = windows[i];
			if (window instanceof TableOfContentsWindow) continue;
//			if (window instanceof WordHoardCalculatorWindow) continue;
			window.dispatchEvent(new WindowEvent(window,
				WindowEvent.WINDOW_CLOSING));
		}
	}

	/**	Enables or disables the "Get Info" command.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	public void enableGetInfoCmd (boolean enabled) {
		getInfoCmd.setEnabled(enabled);
	}

	/**	Enables or disables the "Annotate" command.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	public void enableAnnotateCmd (boolean enabled) {
		annotateCmd.setEnabled(enabled);
	}

	/**	Enables or disables the "Cut" commmand.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	public void enableCutCmd (boolean enabled) {
		cutCmd.setEnabled(enabled);
	}

	/**	Enables or disables the "Copy" commmand.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	public void enableCopyCmd (boolean enabled) {
		copyCmd.setEnabled(enabled);
	}

	/**	Enables or disables the "Paste" commmand.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	public void enablePasteCmd (boolean enabled) {
		pasteCmd.setEnabled(enabled);
	}

	/**	Enables or disables the "Select All" commmand.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	public void enableSelectAllCmd (boolean enabled) {
		selectAllCmd.setEnabled(enabled);
	}

	/**	Enables or disables the line number commands.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	public void enableLineNumberCmds (boolean enabled) {
		noLineNumsCmd.setEnabled(enabled);
		allLineNumsCmd.setEnabled(enabled);
		fiveLineNumberOrStanzaNumsCmd.setEnabled(enabled);
	}

	/**	Enables or disables the "Translations, Transliterations, Etc"
	 *	command.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	public void enableTranslationsCmd (boolean enabled) {
		translationsCmd.setEnabled(enabled);
	}

	/**	Enables or disables the "Show/Hide Annotation Markers" command.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	public void enableShowHideAnnotationMarkersCmd (boolean enabled) {
		showHideAnnotationMarkersCmd.setEnabled(enabled);
	}

	/**	Sets the text of the "Show/Hide Annotation Markers" command.
	 *
	 *	@param	markersShown		True if markers shown, in which
	 *								case the command text is set to
	 *								"Hide Annotation Markers". False if
	 *								markers not shown, in which case
	 *								the command text is set to "Show
	 *								Annotation Markers".
	 */

	public void setShowHideAnnotationMarkersCmdText (boolean markersShown) {
		showHideAnnotationMarkersCmd.setText(
			markersShown ? "Hide Annotation Markers" :
				"Show Annotation Markers");
	}

	/**	Enables or disables the "Show/Hide Annotation Panel" command.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	public void enableShowHideAnnotationPanelCmd (boolean enabled) {
		showHideAnnotationPanelCmd.setEnabled(enabled);
	}

	/**	Sets the text of the "Show/Hide Annotation Panel" command.
	 *
	 *	@param	annotationsShown	True if annotations shown, in which
	 *								case the command text is set to
	 *								"Hide Annotation Panel". False if
	 *								annotations not shown, in which case
	 *								the command text is set to "Show
	 *								Annotation Panel".
	 */

	public void setShowHideAnnotationPanelCmdText (boolean annotationsShown) {
		showHideAnnotationPanelCmd.setText(
			annotationsShown ? "Hide Annotation Panel" :
				"Show Annotation Panel");
	}

	/**	Sets the number stanzas option.
	 *
	 *	<p>When the number stanzas option is set, the Views menu item
	 *	reads "Number Stanzas". Otherwise, it reads "Number Every Fifth Line".
	 *
	 *	@param	numberStanzas		True to number stanzas.
	 */

	public void setNumberStanzas (boolean numberStanzas) {
		this.numberStanzas = numberStanzas;
		fiveLineNumberOrStanzaNumsCmd.setText(
			numberStanzas ?
				"Number Stanzas" :
				"Number Every Fifth Line");
	}

	/**	Positions this window next to a parent window.
	 *
	 *	@param	parentWindow		Parent window.
	 */

	public void positionNextTo (AbstractWindow parentWindow) {
		Dimension screenSize = getToolkit().getScreenSize();
		Dimension thisWindowSize = getSize();
		Dimension parentWindowSize = parentWindow.getSize();
		Point thisLocation = getLocation();
		Point parentLocation = parentWindow.getLocation();
		int screenWidth = screenSize.width;
		int thisWidth = thisWindowSize.width;
		int parentWidth = parentWindowSize.width;
		int parentX = parentLocation.x;
		int x;
		boolean roomOnRight =
			parentX + parentWidth + thisWidth + 12 <= screenWidth;
		if (roomOnRight || parentX < screenWidth - parentX - parentWidth) {
			x = Math.min(parentX + parentWidth + 9,
				screenWidth - 3 - thisWidth);
		} else {
			x = Math.max(parentX - 9 - thisWidth, 3);
		}
		if (x < 0) x = 0;
		thisLocation.x = x;
		if (thisLocation.y < 0) thisLocation.y = 0;
		setLocation(thisLocation);
	}

	/** Check if clipboard has pasteable data.
	 *
	 *	@return		true if clipboard has pasteable text.
	 */

	protected boolean clipboardHasPasteableData () {
		boolean result = false;
		Transferable content = SystemClipboard.getContents(null);
		if (content != null) {
			try {
				String str = (String)content.getTransferData(
					DataFlavor.stringFlavor);
				result = str != null && str.length() > 0;
			} catch (Exception e){}
        }
		return result;
	}

	/**	Set menu status from a CutCopyPaste object.
	 *
	 *	@param	copyable	Object implementing the CutCopyPaste interface.
	 *
	 *	<p>
	 *	On output, sets the status of the Cut, Copy, and Paste menu items
	 *	to reflect the status of the cutCopyPaste object.
	 *	</p>
	 */

	 public void setCutCopyPaste (CutCopyPaste copyable) {
		copyCmd.setEnabled(copyable.isCopyEnabled());
		pasteCmd.setEnabled(copyable.isPasteEnabled());
		cutCmd.setEnabled(copyable.isCutEnabled());
	 }

	/**	Set menu status from a SelectAll object.
	 *
	 *	@param	selectAll	Object implementing the SelectAll interface.
	 *
	 *	<p>
	 *	On output, sets the status of the Select All and Unselect menu
	 *	items to reflect the status of the SelectAll object.
	 *	</p>
	 */

	 public void setSelectAll (SelectAll selectAll) {
		selectAllCmd.setEnabled(selectAll.isSelectAllEnabled());
		unselectCmd.setEnabled(selectAll.isUnselectEnabled());
	 }

	/**	Determine is annotate menu item available.
	 *
	 *	@return		true if annotate menu item available.
	 *
	 *	<p>
	 *	Always returns false unless overridden in subclass.
	 *	</p>
	 */

	public boolean isAnnotateAvailable() {
		return false;
	}

	/**	Adjust menu items and settings for successful login.
	 */

	public void setLoggedIn() {
	}

	/**	Adjust menu items and settings for logout.
	 */

	public void setLoggedOut() {
	}

	/**	Disposes the window.
	 *
	 *	<p>
	 *	If the last window open is a hidden Calculator window,
	 *	call handleLastWindowClosed to exit WordHoard.
	 *	</p>
	 */

	public void dispose () {
		super.dispose();
		WindowsMenuManager[] wins = getAllOpenWindows();
		if (wins.length == 1) {
			if (wins[0] instanceof WordHoardCalculatorWindow) {
				if (!wins[0].isVisible()) handleLastWindowClosed();
			}
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


