package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.bibtool.*;
import edu.northwestern.at.wordhoard.swing.calculator.analysis.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	WordHoard Calculator Sets Menu.
 */

public class SetsMenu extends BaseMenu
{
	/**	The sets menu items. */

	/**	The work set submenu. */

	protected JMenu workMenu;

	/**	The work set submenu items. */

	protected JMenu workMenuNewWorkSetMenu;
	protected JMenuItem workMenuEditWorkSetMenuItem;
	protected JMenuItem workMenuDeleteWorkSetMenuItem;

	/**	The new work set subsubmenu items. */

	protected JMenuItem workMenuNewWorkSetViaWorkSetPanelMenuItem;
	protected JMenuItem workMenuNewWorkSetViaWorkPartSelectionMenuItem;
	protected JMenuItem workMenuNewWorkSetFromExistingTextMenuItem;
	protected JMenuItem workMenuNewWorkSetViaQueryMenuItem;
	protected JMenuItem workMenuDescribeWorkSetMenuItem;

	/**	The word set submenu. */

	protected JMenu wordMenu;

	/**	The word set submenu items. */

	protected JMenuItem wordMenuNewWordSetMenuItem;
	protected JMenuItem wordMenuEditWordSetMenuItem;
	protected JMenuItem wordMenuDeleteWordSetMenuItem;
	protected JMenuItem wordMenuDescribeWordSetMenuItem;

	/**	The phrase set submenu. */

	protected JMenu phraseMenu;

	/**	The phrase set submenu items. */

	protected JMenuItem phraseMenuNewPhraseSetMenuItem;
	protected JMenuItem phraseMenuEditPhraseSetMenuItem;
	protected JMenuItem phraseMenuDeletePhraseSetMenuItem;
	protected JMenuItem phraseMenuDescribePhraseSetMenuItem;

	/**	The query submenu.
	 */

	protected QueryMenu queryMenu;

	/**	Create sets menu.
	 */

	public SetsMenu()
	{
		super
		(
			WordHoardSettings.getString( "setsMenuName" , "Sets" )
		);
	}

	/**	Create sets menu.
	 *
	 *	@param	menuBar		The menu bar to which to attach the sets menu.
	 */

	public SetsMenu( JMenuBar menuBar )
	{
		super
		(
			WordHoardSettings.getString( "setsMenuName" , "Sets" ) ,
			menuBar
		);
	}

	/**	Create sets menu.
	 *
	 *	@param	parentWindow	Parent window for the menu.
	 */

	public SetsMenu( AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "setsMenuName" , "Sets" ) ,
			parentWindow
		);
	}

	/**	Create sets menu.
	 *
	 *	@param	menuBar			The menu bar to which to attach the menu.
	 *	@param	parentWindow	Parent window for the menu.
	 */

	public SetsMenu( JMenuBar menuBar , AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "setsMenuName" , "Sets" ) ,
			menuBar ,
			parentWindow
		);
	}

	/** Sets menu listener.
	 *
	 *	<p>
	 *	Enables and disables Sets submenu items to reflect logged-in user
	 *	status.
	 *	</p>
	 */

	public MenuListener setsMenuListener =
		new MenuListener()
		{
			public void menuCanceled( MenuEvent menuEvent )
			{
			}

			public void menuDeselected( MenuEvent menuEvent )
			{
			}

			public void menuSelected( MenuEvent menuEvent )
			{
								//	Do nothing if Sets menu not active.

				if ( !workMenu.isEnabled() ) return;

								//	Get user ID for logged-in user.
								//	May be null if user is not logged in.

				String userID	= WordHoardSettings.getUserID();

								//	Get counts of word sets, work sets,
								//	and phrase sets.

				int wordSetsCount	=
					WordSetUtils.getWordSetsCount();

				int workSetsCount	=
					WorkSetUtils.getWorkSetsCount();

//				int phraseSetsCount	=
//					PhraseSetUtils.getPhraseSetsCount();

				int phraseSetsCount	= 0;

								//	Get counts of word sets, work sets,
								//	and phrase sets owned by currently
								//	logged-in user.

				int ownerWordSetsCount	=
					WordSetUtils.getWordSetsCount( userID );

				int ownerWorkSetsCount	=
					WorkSetUtils.getWorkSetsCount( userID );

//				int ownerPhraseSetsCount	=
//					PhraseSetUtils.getPhraseSetsCount( userID );

				int ownerPhraseSetsCount	= 0;

								//	Enable or disable edit and delete
								//	menu entries depending upon availability
								//	of sets owned by logged-in user.

				workMenuEditWorkSetMenuItem.setEnabled(
					ownerWorkSetsCount > 0 );

				workMenuDeleteWorkSetMenuItem.setEnabled(
					ownerWorkSetsCount > 0 );

				workMenuDescribeWorkSetMenuItem.setEnabled(
					workSetsCount > 0 );

				wordMenuEditWordSetMenuItem.setEnabled(
					ownerWordSetsCount > 0 );

				wordMenuDeleteWordSetMenuItem.setEnabled(
					ownerWordSetsCount > 0 );

				wordMenuDescribeWordSetMenuItem.setEnabled(
					wordSetsCount > 0 );

				phraseMenuEditPhraseSetMenuItem.setEnabled(
					ownerPhraseSetsCount > 0 );

				phraseMenuDeletePhraseSetMenuItem.setEnabled(
					ownerPhraseSetsCount > 0 );

				phraseMenuDescribePhraseSetMenuItem.setEnabled(
					phraseSetsCount > 0 );

				queryMenu.setQueryMenuItemsAvailability();
			}
		};

	/**	Create the menu items.
	 */

	protected void createMenuItems()
	{
								//	Create query submenu.

		queryMenu		= new QueryMenu();

		this.add( queryMenu );

								//	Create phrase submenu.

		phraseMenu		= makePhraseMenu( this );

								//	Create word submenu.

		wordMenu		= makeWordMenu( this );

								//	Create work submenu.

		workMenu		= makeWorkMenu( this );

								//	Add menu listener so we can
								//	enable/disable individual menu items.
								//
								//	Adding listener here doesn't work.
								//	Adding in WordHoardCalculatorWindow
								//	does work.

//		addMenuListener( setsMenuListener );

								//	Enable menu if UserID defined.

//		boolean userIDDefined	=
//			( WordHoardSettings.getUserID() != null );

//		setEnabled( userIDDefined );

		if ( menuBar != null ) menuBar.add( this );
	}

	/**	Add work submenu.
	 * @param	menuBar	The menu bar to which the submenu will be added.
	 * @return	The work submenu.
	*/

	protected JMenu makeWorkMenu( JMenuItem menuBar )
	{
		JMenu workMenu	=
			new JMenu
			(
				WordHoardSettings.getString
				(
					"workMenuName" ,
					"Work"
				)
			);
								//	New work set parent menu item.

		workMenuNewWorkSetMenu	=
			new JMenu
			(
				WordHoardSettings.getString
				(
					"workMenuNewWorkSetItem" ,
					"New work set"
				)
			);

		workMenu.add( workMenuNewWorkSetMenu );

								//	New work set via work part selection.

		workMenuNewWorkSetViaWorkPartSelectionMenuItem		=
			addMenuItem
			(
				workMenuNewWorkSetMenu ,
				WordHoardSettings.getString
				(
					"workMenuNewWorkSetUsingWorkPartSelectionItem" ,
					"Using work part selection..."
				) ,
				new GenericActionListener
				(
					"newWorkSetUsingWorkPartSelection"
				)
			);
								// New work set from work parts in existing text.

		workMenuNewWorkSetFromExistingTextMenuItem		=
			addMenuItem
			(
				workMenuNewWorkSetMenu ,
				WordHoardSettings.getString
				(
					"workMenuNewWorkSetFromExistingTextItem" ,
					"Using work parts in text..."
				) ,
				new GenericActionListener
				(
					"newWorkSetFromText"
				)
			);
								// New work set using query.

		workMenuNewWorkSetViaQueryMenuItem	=
			addMenuItem
			(
				workMenuNewWorkSetMenu ,
				WordHoardSettings.getString
				(
					"workMenuNewWorkSetUsingQueryItem" ,
					"Using a query..."
				) ,
				new GenericActionListener
				(
					"newWorkSetUsingQuery"
				)
			);
								// New work set using work set panel.

		workMenuNewWorkSetViaWorkSetPanelMenuItem		=
			addMenuItem
			(
				workMenuNewWorkSetMenu ,
				WordHoardSettings.getString
				(
					"workMenuNewWorkSetViaWorkSetPanelItem" ,
					"Using work set window..."
				) ,
				new GenericActionListener
				(
					"newWorkSetUsingWorkSetPanel"
				)
			);
								// Edit

		workMenuEditWorkSetMenuItem	=
			addMenuItem
			(
				workMenu ,
				WordHoardSettings.getString
				(
					"workMenuEditWorkSetItem" ,
					"Edit work set..."
				) ,
				new GenericActionListener( "editWorkSet" )
			);

								// Delete

		workMenuDeleteWorkSetMenuItem	=
			addMenuItem
			(
				workMenu ,
				WordHoardSettings.getString
				(
					"workMenuDeleteWorkSetItem" ,
					"Delete work set..."
				) ,
				new GenericActionListener( "deleteWorkSet" )
			);

		menuBar.add( workMenu );

								// Describe

		workMenuDescribeWorkSetMenuItem	=
			addMenuItem
			(
				workMenu ,
				WordHoardSettings.getString
				(
					"workMenuDescribeWorkSetItem" ,
					"Describe work set..."
				) ,
				new GenericActionListener( "describeWorkSet" )
			);
								//	Add works submenu to sets menu.

		menuBar.add( workMenu );

								//	Enable menu if UserID defined.

		boolean userIDDefined	=
			( WordHoardSettings.getUserID() != null );

		workMenu.setEnabled( userIDDefined );

		return workMenu;
	}

	/**	Add word submenu.
	 * @param	menuBar	The menu bar to which the submenu will be added.
	 * @return	The word submenu.
	*/

	protected JMenu makeWordMenu( JMenuItem menuBar )
	{
		JMenu wordMenu	=
			new JMenu
			(
				WordHoardSettings.getString
				(
					"wordMenuName" ,
					"Word"
				)
			);
								// New

		wordMenuNewWordSetMenuItem		=
			addMenuItem
			(
				wordMenu ,
				WordHoardSettings.getString
				(
					"wordMenuNewWorkSetItem" ,
					"New word set..."
				) ,
				new GenericActionListener( "newWordSet" )
			);
								// Edit

		wordMenuEditWordSetMenuItem	=
			addMenuItem
			(
				wordMenu ,
				WordHoardSettings.getString
				(
					"wordMenuEditWorkSetItem" ,
					"Edit word set..."
				) ,
				new GenericActionListener( "editWordSet" )
			);
								// Delete

		wordMenuDeleteWordSetMenuItem	=
			addMenuItem
			(
				wordMenu ,
				WordHoardSettings.getString
				(
					"wordMenuDeleteWordSetItem" ,
					"Delete word set..."
				) ,
				new GenericActionListener( "deleteWordSet" )
			);
								// Describe

		wordMenuDescribeWordSetMenuItem	=
			addMenuItem
			(
				wordMenu ,
				WordHoardSettings.getString
				(
					"wordMenuDescribeWordSetItem" ,
					"Describe word set..."
				) ,
				new GenericActionListener( "describeWordSet" )
			);
								//	Add word submenu to sets menu.

		menuBar.add( wordMenu );

								//	Enable menu if UserID defined.

		boolean userIDDefined	=
			( WordHoardSettings.getUserID() != null );

		wordMenu.setEnabled( userIDDefined );

		return wordMenu;
	}

	/**	Add phrase submenu.
	 * @param	menuBar	The menu bar to which the submenu will be added.
	 * @return	The phrase submenu.
	*/

	protected JMenu makePhraseMenu( JMenuItem menuBar )
	{
		JMenu phraseMenu	=
			new JMenu
			(
				WordHoardSettings.getString
				(
					"phraseMenuName" ,
					"Phrase"
				)
			);
								// New

		phraseMenuNewPhraseSetMenuItem		=
			addMenuItem
			(
				phraseMenu ,
				WordHoardSettings.getString
				(
					"phraseMenuNewPhraseSetItem" ,
					"New phrase set..."
				) ,
				new GenericActionListener( "newPhraseSet" )
			);
								// Edit

		phraseMenuEditPhraseSetMenuItem	=
			addMenuItem
			(
				phraseMenu ,
				WordHoardSettings.getString
				(
					"phraseMenuEditPhraseSetItem" ,
					"Edit phrase set..."
				) ,
				new GenericActionListener( "editPhraseSet" )
			);
								// Delete

		phraseMenuDeletePhraseSetMenuItem	=
			addMenuItem
			(
				phraseMenu ,
				WordHoardSettings.getString
				(
					"phraseMenuDeletePhraseSetItem" ,
					"Delete phrase set..."
				) ,
				new GenericActionListener( "deletePhraseSet" )
			);
								// Describe

		phraseMenuDescribePhraseSetMenuItem	=
			addMenuItem
			(
				phraseMenu ,
				WordHoardSettings.getString
				(
					"phraseMenuDescribePhraseSetItem" ,
					"Describe phrase set..."
				) ,
				new GenericActionListener( "describePhraseSet" )
			);
								//	Add phrase submenu to sets menu.

		menuBar.add( phraseMenu );

								//	Enable menu if UserID defined.

		phraseMenu.setEnabled( false );
		phraseMenu.setVisible( false );

		return phraseMenu;
	}

	/**	Handle menu changes when logging in.
	 */

	public void handleLogin()
	{
								//	Enable Sets submenus.

		queryMenu.setEnabled( true );
		workMenu.setEnabled( true );
		wordMenu.setEnabled( true );
//		phraseMenu.setEnabled( true );
	}

	/**	Handle menu changes when logging out.
	 */

	public void handleLogout()
	{
		                        //	Disable Sets submenus.

		queryMenu.setEnabled( false );
		workMenu.setEnabled( false );
		wordMenu.setEnabled( false );
		phraseMenu.setEnabled( false );
	}

	/**	Create a new work set.
	 * @param newWorkSetDialogType	The type of the work set.
	*/

	protected void newWorkSet( int newWorkSetDialogType )
	{
		NewWorkSetDialog dialog	=
			new NewWorkSetDialog
			(
				WordHoardSettings.getString
				(
					"createWorkSetDialogTitle" ,
					"Create Work Set"
				) ,
				getCalculatorWindow() ,
				newWorkSetDialogType
			);

		dialog.show( getCalculatorWindow() );

		if ( !dialog.getCancelled() )
		{
			WorkSet workSet	= null;

			try
			{
				workSet	=
					WorkSetUtils.addWorkSet
					(
						dialog.getSetTitle() ,
						dialog.getDescription() ,
						dialog.getWebPageURL() ,
						dialog.getIsPublic() ,
						dialog.getQueryText() ,
						dialog.getSelectedWorkParts()
					);

				if ( workSet != null )
				{
					new InformationMessage(
						WordHoardSettings.getString(
							"workSetCreated" , "Work set created." ) );
				}
				else
				{
					new ErrorMessage(
						WordHoardSettings.getString(
							"workSetNotCreated" ,
							"Work set could not be created." ) );
				}
			}
			catch ( DuplicateWorkSetException e )
			{
				new ErrorMessage(
					WordHoardSettings.getString(
						"duplicateworkset" ,
						"A work set of that name already exists." ) );
			}
		}
	}

	/**	Create a new work set panel for drag and drop.
	 * @throws	Exception	general error.
	*/

	protected void newWorkSetUsingWorkSetPanel() throws Exception
	{
		new WorkSetWindow( parentWindow );
    }

	/**	Create a new work set via work part selection. */

	protected void newWorkSetUsingWorkPartSelection()
	{
		newWorkSet( NewWorkSetDialog.VIASELECTION );
    }

	/**	Create a new work set using a query. */

	protected void newWorkSetUsingQuery()
	{
		newWorkSet( NewWorkSetDialog.VIAQUERY );
    }

	/**	Create a new work set from work parts in a text. */

	protected void newWorkSetFromText()
	{
		newWorkSet( NewWorkSetDialog.FROMTEXT );
    }

	/**	Edit existing work set. */

	protected void editWorkSet()
	{
		WorkSet[] workSets	=
			WorkSetUtils.getWorkSetsForLoggedInUser();

		if ( ( workSets == null ) || ( workSets.length == 0 ) )
		{
			new ErrorMessage(
				WordHoardSettings.getString
				(
					"Noworksetstoedit" ,
					"There are no work sets you can edit." )
				);

			return;
		}

		EditWorkSetDialog dialog	=
			new EditWorkSetDialog
			(
				WordHoardSettings.getString
				(
					"editWorkSetDialogTitle" ,
					"Edit Work Set"
				) ,
				getCalculatorWindow()
			);

		dialog.show( getCalculatorWindow() );

		if ( !dialog.getCancelled() )
		{
			try
			{
				if ( WorkSetUtils.updateWorkSet
				(
					dialog.getWorkSet() ,
					dialog.getSetTitle() ,
					dialog.getDescription() ,
					dialog.getWebPageURL() ,
					dialog.getIsPublic() ,
					dialog.getWorkSetWorkParts() )
				)
				{
					new InformationMessage(
						WordHoardSettings.getString(
							"workSetUpdated" , "Work set updated." ) );
				}
				else
				{
					new ErrorMessage(
						WordHoardSettings.getString(
							"workSetNotUpdated" ,
							"Work set could not be updated." ) );
				}
			}
			catch ( DuplicateWorkSetException e )
			{
				new ErrorMessage(
					WordHoardSettings.getString(
						"duplicateworkset" ,
						"A work set of that name already exists." ) );
			}
		}
	}

	/**	Delete a work set. */

	protected void deleteWorkSet()
	{
		WorkSet[] workSets	=
			WorkSetUtils.getWorkSetsForLoggedInUser();

		if ( ( workSets == null ) || ( workSets.length == 0 ) )
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"Noworksetstodelete" ,
					"There are no work sets you can delete." ) );

			return;
		}

		DeleteWorkSetDialog dialog =
			new DeleteWorkSetDialog
			(
				WordHoardSettings.getString
				(
					"deleteWorkSetsDialogTitle" ,
					"Delete Work Sets"
				) ,
				getCalculatorWindow()
			);

		dialog.show( getCalculatorWindow() );

		if ( !dialog.getCancelled() )
		{
			WorkSet[] workSetsToDelete	= dialog.getSelectedWorkSets();
			int countToDelete			= workSetsToDelete.length;

			if ( WorkSetUtils.deleteWorkSets( workSetsToDelete ) )
			{
				if ( countToDelete == 1 )
				{
					new InformationMessage(
						WordHoardSettings.getString(
							"workSetDeleted" , "Work set deleted." ) );
				}
				else
				{
					new InformationMessage(
						WordHoardSettings.getString(
							"workSetsDeleted" , "Work sets deleted." ) );
				}
			}
			else
			{
				if ( countToDelete == 1 )
				{
					new ErrorMessage(
						WordHoardSettings.getString(
							"workSetCouldNotBeDeleted" ,
							"Work set could not be deleted." ) );
				}
				else
				{
					new ErrorMessage(
						WordHoardSettings.getString(
							"workSetsCouldNotBeDeleted" ,
							"Work sets could not be deleted." ) );
				}
			}
		}
	}

	/**	Helper for describing a user data object.
	 *
	 *	@param	dialog				User data object to describe.
	 *	@param	outputResults	Progress reporter and close button.
	 */

	protected void doDescribeUserDataObject
	(
		DescribeUserDataObjectDialog dialog ,
		OutputResults outputResults
	)
	{
								//	Note if calculator window
								//	is open already.

		final boolean isCalculatorWindowVisible	=
			getCalculatorWindow().isVisible();

								//	Get the output panel and close button.

		final DialogPanel panel		= outputResults.getOutputPanel();
		final JButton closeButton	= outputResults.getCloseButton();

								//	Get the progress reporter.

		ProgressReporter progressReporter	=
			outputResults.getProgressReporter();

								//	Enable the busy cursor.
		setBusyCursor();

		long startTime	= System.currentTimeMillis();

								//	Get object.

		final UserDataObject userDataObject	=
			dialog.getSelectedUserDataObject();

								//	Get settings for object into table.

		final XTable settingsTable	=
			DescribeUserDataObject.describe( userDataObject );

		long endTime	=
			( System.currentTimeMillis() - startTime + 999 ) / 1000;

								//	Enable the default cursor.
		setDefaultCursor();
            					//	Close persistence manager for
            					//	this thread.

   		closePersistenceManager();

        						//	Close progress reporter.

		progressReporter.close();

								//	Update the display on the AWT event
								//	thread or bad things will happen.

		Runnable runnable	=
			new Runnable()
			{
				public void run()
				{
								//	Hide the close button.

					closeButton.setVisible( false );

								//	Create results panel.

					ResultsPanel resultsPanel	= new ResultsPanel();

								//	Set results title.

					resultsPanel.setResultsTitle
					(
						"Description of " + userDataObject.getTitle()
					);
								//	Set results header.

					resultsPanel.setResultsHeader
					(
						"Description of " + userDataObject.getTitle()
					);

								//	Wrap settings table in a scroll pane.

					XScrollPane scrollPane	=
						new XScrollPane
						(
							settingsTable ,
							JScrollPane.VERTICAL_SCROLLBAR_ALWAYS ,
							JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
						);

					scrollPane.setPreferredSize
					(
						new Dimension( 600 , 800 )
					);

							//	Add title to output pane.

					XTextArea titleTextArea	=
						new XTextArea( resultsPanel.getResultsTitle() );

					titleTextArea.setBackground( panel.getBackground() );
					titleTextArea.setFont( settingsTable.getFont() );

					JPanel titlePanel	= new JPanel();

					titlePanel.setLayout( new BorderLayout() );
					titlePanel.add( titleTextArea );

							//	Add results to results pane.

					resultsPanel.setResults( scrollPane );

					resultsPanel.add( titlePanel );
					resultsPanel.add( scrollPane , 10 );

					resultsPanel.validate();

							//	Add results pane to output pane.

					panel.getBody().removeAll();
					panel.add( resultsPanel );
					panel.revalidate();

								//	Enable/disable cut/copy/paste as
								//	needed.

					if ( resultsPanel.getResults() instanceof CutCopyPaste )
					{
						CutCopyPaste copyable	=
							(CutCopyPaste)resultsPanel.getResults();

						getEditMenu().setCutCopyPaste( copyable );
					}
								//	Move results to new window.

					getMainTabbedPane().undock( isCalculatorWindowVisible );

								//	Make sure the main pane gets redrawn
								//	to remove any artifacts left over from
								//	the dialog.

					getMainTabbedPane().paintImmediately(
						getMainTabbedPane().getVisibleRect() );

					setDefaultCursor();

								//	Close calculator window if it
								//	was not already open when
								//	analysis started.

					if ( !isCalculatorWindowVisible )
					{
						getCalculatorWindow().setVisible( false );
					}
				}
			};

		SwingUtilities.invokeLater( runnable );
	}

	/**	Describe a work set. */

	protected void describeWorkSet()
	{
								//	Create describe dialog.

		DescribeUserDataObjectDialog dialog	=
			new DescribeUserDataObjectDialog
			(
				WordHoardSettings.getString
				(
					"describeWorkSetDialogTitle" ,
					"Describe Work Set"
				) ,
				getCalculatorWindow() ,
				WorkSet.class
			);
								//	Display dialog.

		dialog.show( getCalculatorWindow() );

		getMainTabbedPane().paintImmediately(
			getMainTabbedPane().getVisibleRect() );

								//	If the dialog was not cancelled ...

		if ( !dialog.getCancelled() )
		{
			final DescribeUserDataObjectDialog finalDialog	= dialog;

								//	Create an output panel to hold
								//	the description.

			final OutputResults outputResults	=
            	createProgressPanel
            	(
					WordHoardSettings.getString
					(
						"DescribeWorkSet" ,
						"Describe work set"
					) ,
					WordHoardSettings.getString
					(
						"Retrievingworksetinfo" ,
						"Retrieving work set information"
					)
            	);
                                //	Get the description on a separate thread.
			Thread runner =
				new Thread
				(
					WordHoardSettings.getString
					(
						"DescribeWorkSet" ,
						"Describe work set"
					)
				)
				{
					public void run()
					{
						doDescribeUserDataObject( finalDialog , outputResults );
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

	/**	Helper for creating a word set.
	 *
	 *	@param	dialog				New word set dialog.
	 *	@param	outputResults	Progress reporter and close button.
	 *	@return	The new word set.
	 */

	protected WordSet doNewWordSet
	(
		NewWordSetDialog dialog ,
		OutputResults outputResults
	)
	{
		WordSet wordSet	= null;

								//	Get the progress reporter.

		ProgressReporter progressReporter	=
			outputResults.getProgressReporter();

								//	Enable the busy cursor.
		setBusyCursor();

		long startTime	= System.currentTimeMillis();

								//	Try creating the word set.
		try
		{
			wordSet	=
				WordSetUtils.addWordSetUsingQuery
				(
					dialog.getSetTitle() ,
					dialog.getDescription() ,
					dialog.getWebPageURL() ,
					dialog.getIsPublic() ,
					dialog.getAnalysisText() ,
					dialog.getQueryText() ,
					getCalculatorWindow() ,
					progressReporter
				);
		}
		catch ( DuplicateWordSetException e )
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"duplicatewordset" ,
					"A word set of that name already exists." ) );
		}

		long endTime	=
			( System.currentTimeMillis() - startTime + 999 ) / 1000;

								//	Enable the default cursor.
		setDefaultCursor();
            					//	Close persistence manager for this thread.

   		closePersistenceManager();

								//	Enable the close button.

		if ( outputResults.getCloseButton() != null )
		{
			cancelToClose
			(
				outputResults.getCloseButton() ,
				outputResults.getOutputPanel()
			);
		}
								//	Report if word set created.

		if ( wordSet != null )
		{
			String formatString	= "";

			if ( endTime == 1 )
			{
				formatString	=
					WordHoardSettings.getString
					(
						"wordSetCreatedinnsecond" ,
						"Word set created in %s second"
					);
			}
			else
			{
				formatString	=
					WordHoardSettings.getString
					(
						"wordSetCreatedinnseconds" ,
						"Word set created in %s seconds"
					);
			}

			progressReporter.updateProgress
			(
				new PrintfFormat( formatString ).sprintf(
					new Object[]{
						Formatters.formatLongWithCommas( endTime ) } )
			);

			progressReporter.setLabelColor( Color.GREEN );
		}
		else
		{
			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"wordSetNotCreated" ,
					"Word set could not be created."
				)
			);

			progressReporter.setLabelColor( Color.RED );
		}
								//	Close the progress display.

		progressReporter.close();

		return wordSet;
	}

	/**	Create a new word set. */

	protected void newWordSet()
	{
		NewWordSetDialog dialog	=
			new NewWordSetDialog
			(
				WordHoardSettings.getString
				(
					"createWordSetDialogTitle" ,
					"Create Word Set"
				) ,
				getCalculatorWindow()
			);

		dialog.show( getCalculatorWindow() );

		getMainTabbedPane().paintImmediately(
			getMainTabbedPane().getVisibleRect() );

		if ( !dialog.getCancelled() )
		{
			final NewWordSetDialog finalDialog	= dialog;

			final OutputResults outputResults		=
            	createProgressPanel
            	(
					WordHoardSettings.getString
					(
						"CreatingWordSet" ,
						"Creating Word Set"
					) ,
					WordHoardSettings.getString
					(
						"Retrievingworks" ,
						"Retrieving works"
					)
            	);

			Thread runner = new Thread( "New Word Set" )
			{
				public void run()
				{
					doNewWordSet( finalDialog , outputResults );
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

	/**	Helper for editing a word set.
	 *
	 *	@param	dialog	Edit word set dialog.
	 */

	protected void doEditWordSet( EditWordSetDialog dialog )
	{
		setBusyCursor();

		try
		{
			boolean updatedOK	=
				WordSetUtils.updateWordSet
				(
					dialog.getWordSet() ,
					dialog.getSetTitle() ,
					dialog.getDescription() ,
					dialog.getWebPageURL() ,
					dialog.getIsPublic()
				);

			setDefaultCursor();

			if ( updatedOK )
			{
				new InformationMessage(
					WordHoardSettings.getString(
						"wordSetUpdated" , "Word set updated." ) );
			}
			else
			{
				new ErrorMessage(
					WordHoardSettings.getString(
						"wordSetNotUpdated" ,
						"Word set could not be updated." ) );
			}
		}
		catch ( DuplicateWordSetException e )
		{
			setDefaultCursor();

			new ErrorMessage(
				WordHoardSettings.getString(
					"duplicatewordset" ,
					"A word set of that title already exists." ) );
		}
	}

	/**	Edit existing word set. */

	protected void editWordSet()
	{
		WordSet[] wordSets	=
			WordSetUtils.getWordSetsForLoggedInUser();

		if ( ( wordSets == null ) || ( wordSets.length == 0 ) )
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"Nowordsetstoedit" ,
					"There are no word sets you can edit." ) );

			return;
		}

		EditWordSetDialog dialog	=
			new EditWordSetDialog
			(
				WordHoardSettings.getString
				(
					"editWordSetDialogTitle" ,
					"Edit Word Set"
				) ,
				getCalculatorWindow()
			);

		dialog.show( getCalculatorWindow() );

		getMainTabbedPane().paintImmediately(
			getMainTabbedPane().getVisibleRect() );

		if ( !dialog.getCancelled() )
		{
			doEditWordSet( dialog );
		}
	}

	/**	Helper for deleting a word set.
	 *
	 *	@param	dialog				Delete word set dialog.
	 *	@param	outputResults	Progress reporter and close button.
	 */

	protected void doDeleteWordSet
	(
		DeleteWordSetDialog dialog ,
		OutputResults outputResults
	)
	{
								//	Enable the busy cursor.
		setBusyCursor();
								//	Get word sets to delete.

		WordSet[] wordSetsToDelete	= dialog.getSelectedWordSets();

								//	Get number of word sets to delete.

		int countToDelete			= wordSetsToDelete.length;

								//	Get the progress reporter.

		ProgressReporter progressReporter	=
			outputResults.getProgressReporter();

                    			//	Try deleting the word sets.

		boolean wordSetsDeleted		=
			WordSetUtils.deleteWordSets( wordSetsToDelete , progressReporter );

								//	Enable the default cursor.
		setDefaultCursor();
            					//	Close persistence manager for this thread.

   		closePersistenceManager();

								//	Set cancel to close button.

		if ( outputResults.getCloseButton() != null )
		{
			cancelToClose
			(
				outputResults.getCloseButton() ,
				outputResults.getOutputPanel()
			);
		}

								//	Report if word sets deleted.

		if ( wordSetsDeleted )
		{
			if ( countToDelete == 1 )
			{
				progressReporter.updateProgress
				(
					WordHoardSettings.getString
					(
						"wordSetDeleted" ,
						"Word set deleted."
					)
				);
			}
			else
			{
				progressReporter.updateProgress
				(
					WordHoardSettings.getString
					(
						"wordSetsDeleted" ,
						"Word sets deleted."
					)
				);
			}

			progressReporter.setLabelColor( Color.GREEN );
		}
		else
		{
			if ( countToDelete == 1 )
			{
				progressReporter.updateProgress
				(
					WordHoardSettings.getString
					(
						"wordSetCouldNotBeDeleted" ,
						"Word set could not be deleted."
					)
				);
			}
			else
			{
				progressReporter.updateProgress
				(
					WordHoardSettings.getString
					(
						"wordSetsCouldNotBeDeleted" ,
						"Word sets could not be deleted."
					)
				);
			}

			progressReporter.setLabelColor( Color.RED );
		}
								//	Close the progress display.

		progressReporter.close();
	}

	/**	Delete a word set. */

	protected void deleteWordSet()
	{
		WordSet[] wordSets	=
			WordSetUtils.getWordSetsForLoggedInUser();

		if ( ( wordSets == null ) || ( wordSets.length == 0 ) )
		{
			new ErrorMessage(
				WordHoardSettings.getString
				(
					"Nowordsetstodelete" ,
					"There are no word sets you can delete." )
				);

			return;
		}

		DeleteWordSetDialog dialog =
			new DeleteWordSetDialog
			(
				WordHoardSettings.getString
				(
					"deleteWordSetsDialogTitle" ,
					"Delete Word Sets"
				) ,
				getCalculatorWindow()
			);

		getMainTabbedPane().paintImmediately(
			getMainTabbedPane().getVisibleRect() );

		dialog.show( getCalculatorWindow() );

		if ( !dialog.getCancelled() )
		{
			final DeleteWordSetDialog finalDialog	= dialog;

            final OutputResults outputResults	=
            	createProgressPanel
            	(
					WordHoardSettings.getString
					(
						"DeletingWordSet" ,
						"Deleting Word Set"
					) ,
					WordHoardSettings.getString
					(
						"Deleting" ,
						"Deleting"
					)
				);

			Thread runner = new Thread( "Delete Word Set" )
			{
				public void run()
				{
					doDeleteWordSet( finalDialog , outputResults );
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

	/**	Describe a word set. */

	protected void describeWordSet()
	{
								//	Create describe dialog.

		DescribeUserDataObjectDialog dialog	=
			new DescribeUserDataObjectDialog
			(
				WordHoardSettings.getString
				(
					"describeWordSetDialogTitle" ,
					"Describe Word Set"
				) ,
				getCalculatorWindow() ,
				WordSet.class
			);
								//	Display dialog.

		dialog.show( getCalculatorWindow() );

		getMainTabbedPane().paintImmediately(
			getMainTabbedPane().getVisibleRect() );

								//	If the dialog was not cancelled ...

		if ( !dialog.getCancelled() )
		{
			final DescribeUserDataObjectDialog finalDialog	= dialog;

								//	Create an output panel to hold
								//	the description.

			final OutputResults outputResults	=
            	createProgressPanel
            	(
					WordHoardSettings.getString
					(
						"DescribeWordSet" ,
						"Describe word set"
					) ,
					WordHoardSettings.getString
					(
						"Retrievingwordsetinfo" ,
						"Retrieving word set information"
					)
            	);
                                //	Get the description on a separate thread.
			Thread runner =
				new Thread
				(
					WordHoardSettings.getString
					(
						"DescribeWordSet" ,
						"Describe word set"
					)
				)
				{
					public void run()
					{
						doDescribeUserDataObject( finalDialog , outputResults );
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

	/**	Helper for creating a phrase set.
	 *
	 *	@param	dialog	New phrase set dialog.
	 *	@return	The new phrase set.
	 */

	protected PhraseSet doNewPhraseSet( NewPhraseSetDialog dialog )
	{
		PhraseSet phraseSet	= null;

								//	Enable the busy cursor.
		setBusyCursor();
								//	Try creating the phrase set.
		try
		{
			phraseSet	=
				PhraseSetUtils.addPhraseSet
				(
					dialog.getSetTitle() ,
					dialog.getDescription() ,
					dialog.getWebPageURL() ,
					WordHoardSettings.getUserID() ,
					dialog.getIsPublic() ,
					dialog.getAnalysisText() ,
					dialog.getQueryText() ,
					getCalculatorWindow()
				);
		}
		catch ( DuplicatePhraseSetException e )
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"duplicatephraseset" ,
					"A phrase set of that name already exists." ) );
		}
								//	Enable the default cursor.
		setDefaultCursor();
            					//	Close persistence manager for this thread.

   		closePersistenceManager();

								//	Report if phrase set created.

		if ( phraseSet != null )
		{
			new InformationMessage(
				WordHoardSettings.getString(
					"phraseSetCreated" , "Phrase set created." ) );
		}
		else
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"phraseSetNotCreated" ,
					"Phrase set could not be created." ) );
		}

		return phraseSet;
	}

	/**	Create a new phrase set. */

	protected void newPhraseSet()
	{
		NewPhraseSetDialog dialog	=
			new NewPhraseSetDialog
			(
				WordHoardSettings.getString
				(
					"createPhraseSetDialogTitle" ,
					"Create Phrase Set"
				) ,
				getCalculatorWindow()
			);

		dialog.show( getCalculatorWindow() );

		getMainTabbedPane().paintImmediately(
			getMainTabbedPane().getVisibleRect() );

		if ( !dialog.getCancelled() )
		{
			final NewPhraseSetDialog finalDialog	= dialog;

			Thread runner = new Thread( "New Phrase Set" )
			{
				public void run()
				{
					doNewPhraseSet( finalDialog );
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

	/**	Helper for editing a phrase set.
	 *
	 *	@param	dialog	Edit phrase set dialog.
	 */

	protected void doEditPhraseSet( EditPhraseSetDialog dialog )
	{
								//	Enable the busy cursor.
		setBusyCursor();

		boolean updatedOK	=
			PhraseSetUtils.updatePhraseSet
			(
				dialog.getPhraseSet() ,
				dialog.getSetTitle() ,
				dialog.getDescription() ,
				dialog.getWebPageURL() ,
				dialog.getIsPublic()
			);
								//	Enable normal cursor.
		setDefaultCursor();

		if ( updatedOK )
		{
			new InformationMessage(
				WordHoardSettings.getString(
					"phraseSetUpdated" , "Phrase set updated." ) );
		}
		else
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"phraseSetNotUpdated" ,
					"Phrase set could not be updated." ) );
		}
	}

	/**	Edit existing phrase set. */

	protected void editPhraseSet()
	{
		PhraseSet[] phraseSets	=
			PhraseSetUtils.getPhraseSetsForLoggedInUser();

		if ( ( phraseSets == null ) || ( phraseSets.length == 0 ) )
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"Nophrasesetstoedit" ,
					"There are no phrase sets you can edit." ) );

			return;
		}

		EditPhraseSetDialog dialog	=
			new EditPhraseSetDialog
			(
				WordHoardSettings.getString
				(
					"editPhraseSetDialogTitle" ,
					"Edit Phrase Set"
				) ,
				getCalculatorWindow()
			);

		dialog.show( getCalculatorWindow() );

		getMainTabbedPane().paintImmediately(
			getMainTabbedPane().getVisibleRect() );

		if ( !dialog.getCancelled() )
		{
			doEditPhraseSet( dialog );
		}
	}

	/**	Helper for deleting a phrase set.
	 *
	 *	@param	dialog				Delete word set dialog.
	 *	@param	outputResults	Progress reporter and close button.
	 */

	protected void doDeletePhraseSet
	(
		DeletePhraseSetDialog dialog ,
		OutputResults outputResults
	)
	{
								//	Enable the busy cursor.
		setBusyCursor();
								//	Get phrase sets to delete.

		PhraseSet[] phraseSetsToDelete	= dialog.getSelectedPhraseSets();

								//	Get number of phrase sets to delete.

		int countToDelete			= phraseSetsToDelete.length;

								//	Get the progress reporter.

		ProgressReporter progressReporter	=
			outputResults.getProgressReporter();

                    			//	Try deleting the phrase sets.

		boolean phraseSetsDeleted		=
			WordSetUtils.deleteWordSets(
				phraseSetsToDelete , progressReporter );

								//	Enable the default cursor.
		setDefaultCursor();
            					//	Close persistence manager for this
            					//	thread.

   		closePersistenceManager();

								//	Change cancel button to close button.

		if ( outputResults.getCloseButton() != null )
		{
			cancelToClose
			(
				outputResults.getCloseButton() ,
				outputResults.getOutputPanel()
			);
		}

								//	Report if phrase sets deleted.

		if ( phraseSetsDeleted )
		{
			if ( countToDelete == 1 )
			{
				progressReporter.updateProgress
				(
					WordHoardSettings.getString
					(
						"phraseSetDeleted" ,
						"Phrase set deleted."
					)
				);
			}
			else
			{
				progressReporter.updateProgress
				(
					WordHoardSettings.getString
					(
						"phraseSetsDeleted" ,
						"Phrase sets deleted."
					)
				);
			}

			progressReporter.setLabelColor( Color.GREEN );
		}
		else
		{
			if ( countToDelete == 1 )
			{
				progressReporter.updateProgress
				(
					WordHoardSettings.getString
					(
						"phraseSetCouldNotBeDeleted" ,
						"Phrase set could not be deleted."
					)
				);
			}
			else
			{
				progressReporter.updateProgress
				(
					WordHoardSettings.getString
					(
						"phraseSetsCouldNotBeDeleted" ,
						"Phrase sets could not be deleted."
					)
				);
			}

			progressReporter.setLabelColor( Color.RED );
		}
								//	Close the progress display.

		progressReporter.close();
	}

	/**	Delete a phrase set. */

	protected void deletePhraseSet()
	{
		PhraseSet[] phraseSets	=
			PhraseSetUtils.getPhraseSetsForLoggedInUser();

		if ( ( phraseSets == null ) || ( phraseSets.length == 0 ) )
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"Nophrasesetstodelete" ,
					"There are no phrase sets you can delete." ) );

			return;
		}

		DeletePhraseSetDialog dialog =
			new DeletePhraseSetDialog
			(
				WordHoardSettings.getString
				(
					"deletePhraseSetsDialogTitle" ,
					"Delete Phrase Sets"
				) ,
				getCalculatorWindow()
			);

		getMainTabbedPane().paintImmediately(
			getMainTabbedPane().getVisibleRect() );

		dialog.show( getCalculatorWindow() );

		if ( !dialog.getCancelled() )
		{
			final DeletePhraseSetDialog finalDialog	= dialog;

            final OutputResults outputResults	=
            	createProgressPanel
            	(
					WordHoardSettings.getString
					(
						"DeletingPhraseSet" ,
						"Deleting Phrase Set"
					) ,
					WordHoardSettings.getString
					(
						"Deleting" ,
						"Deleting"
					)
				);

			Thread runner = new Thread( "Delete Phrase Set" )
			{
				public void run()
				{
					doDeletePhraseSet( finalDialog , outputResults );
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

	/**	Describe a phrase set. */

	protected void describePhraseSet()
	{
		notYetImplemented();
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

