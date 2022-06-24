package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.*;

/**	Performs search and replace text operations for JTextComponent. */

public class SearchAndReplaceDialog extends ModalDialog
{
	/** Parent component to which this search dialog belongs. */

	protected Object parent;

	/** JTextComponent containing document to search. */

	protected JTextComponent textPane;

	/** Tabbed pane to hold search and replace dialogs. */

	protected JTabbedPane tabbedPane;

	/** Search text field in search pane. */

	protected XTextField searchText1;

	/** Search text field in replace pane. */

	protected XTextField searchText2;

	/** Search text as document. */

	protected Document docSearch;

	/** Replace text as document. */

	protected Document docReplace;

	/** Dialog buttons. */

	protected JButton searchButton;
	protected JButton replaceButton;
	protected JButton replaceAllButton;

	/** Models for options.  Allow automatic sharing of settings
	 *	between search and replace panes.
	 */

	protected ButtonModel modelWord;
	protected ButtonModel modelCase;
	protected ButtonModel modelBackwards;
	protected ButtonModel modelForwards;

	/** Current index of search text. */

	protected int		searchIndex = -1;

	/** True to search up (towards beginning of text). */

	protected boolean	searchBackwards = false;

	/** The search data. */

	protected String	searchData;

	/** Counts depth of nested batch edits. */

	protected int batchEditDepth;

	/** Create search and replace dialog.
	 *
	 *	@param	parent		Parent component of this dialog.
	 *	@param	textPane	JTextComponent which owns this dialog.
	 *	@param	index		0 to show search pane, 1 to show replace pane.
	 */

	public SearchAndReplaceDialog
	(
		Object parent ,
		JTextComponent textPane ,
		int index
	)
	{
		super( Resources.get( "Findandreplace" , "Find and Replace" ) );

								// Save parent and text pane.

		this.parent		= parent;
		this.textPane	= textPane;

								// Not a modal dialog.
		setModal( false );
								// Create Search panel.

		LabeledColumn searchBox = createSearchPanel();

								// Create Replace panel.

		LabeledColumn replaceBox = createReplacePanel();

								// Place Search and Replace panels into a
								// tabbed pane.  This allows easy toggling
								// between search and replace operations.

		tabbedPane = new JTabbedPane();

		tabbedPane.addTab(
			Resources.get( "Find" , "Find" ) , searchBox );

		tabbedPane.addTab(
			Resources.get( "Replace" , "Replace" ) , replaceBox );

		add( tabbedPane );
								// Create buttons.
		createButtons();
								// Add listener to tabbed pane so we can
								// enable/disable buttons when switching
								// between search and replace panels.

		tabbedPane.getModel().addChangeListener( tabPaneListener );

		batchEditDepth = 0;

		pack();
	}

	/**	Tab pane listener. */

	private ChangeListener tabPaneListener =
		new ChangeListener()
		{
			public void stateChanged( ChangeEvent event )
			{
				setSelectedIndex( tabbedPane.getSelectedIndex() );
			}
		};

	/** Create search panel. */

	private LabeledColumn createSearchPanel()
	{
		LabeledColumn searchBox = new LabeledColumn();

		searchText1	= new XTextField( 32 );
		docSearch	= searchText1.getDocument();

		searchBox.addPair(
			Resources.get( "Find" , "Find" ) , searchText1 );
		searchBox.addPair( "" , "" );

		LabeledColumn searchOptions = new LabeledColumn();

		JCheckBox chkWord =
			new JCheckBox(
				Resources.get( "Matchwholewords" , "Match whole words" ) );

		modelWord = chkWord.getModel();

		JCheckBox chkCase	=
			new JCheckBox(
				Resources.get( "Matchcase" , "Match case" ) );

		modelCase			= chkCase.getModel();

		ButtonGroup buttonGroup = new ButtonGroup();

		JRadioButton searchBackwards =
			new JRadioButton(
				Resources.get( "Searchbackwards" , "Search backwards" ) );

		modelBackwards = searchBackwards.getModel();

		JRadioButton searchForwards	=
			new JRadioButton(
				Resources.get( "Searchforwards" , "Search forwards" ) ,
				true );

		modelForwards = searchForwards.getModel();

		buttonGroup.add( searchForwards );
		buttonGroup.add( searchBackwards );

		JPanel optionsPanel = new JPanel( new GridLayout() );

		optionsPanel.setBorder(
			new TitledBorder(
				new EtchedBorder() ,
				Resources.get( "Options" , "Options" ) ) );

		searchOptions.addPair( "" , searchBackwards );
		searchOptions.addPair( "" , searchForwards );
		searchOptions.addPair( "" , chkCase );
		searchOptions.addPair( "" , chkWord );

		optionsPanel.add( searchOptions );

		searchBox.addPair( "" , optionsPanel );

		searchBox.setBorder(
			BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

		return searchBox;
	}

	/** Create replace panel. */

	private LabeledColumn createReplacePanel()
	{
		LabeledColumn replaceBox = new LabeledColumn();

		searchText2	= new XTextField( 32 );
		searchText2.setDocument( docSearch );

		JTextField txtReplace = new XTextField( 32 );
		docReplace = txtReplace.getDocument();

		replaceBox.addPair(
			Resources.get( "Find" ) , searchText2 );

		replaceBox.addPair(
			Resources.get( "Replacewith" , "Replace with" ) , txtReplace );

		LabeledColumn replaceOptions = new LabeledColumn();

		JCheckBox chkWord =
			new JCheckBox(
				Resources.get( "Matchwholewords" , "Match whole words" ) );

		chkWord.setModel( modelWord );

		JCheckBox chkCase	=
			new JCheckBox(
				Resources.get( "Matchcase" , "Match case" ) );

		chkCase.setModel( modelCase );

		ButtonGroup buttonGroup = new ButtonGroup();

		JRadioButton searchBackwards =
			new JRadioButton(
				Resources.get( "Searchbackwards" , "Search backwards" ) );

		searchBackwards.setModel( modelBackwards );

		JRadioButton searchForwards	=
			new JRadioButton(
				Resources.get( "Searchforwards" , "Search forwards" ) ,
				true );

		searchForwards.setModel( modelForwards );

		buttonGroup.add( searchForwards );
		buttonGroup.add( searchBackwards );

		JPanel optionsPanel = new JPanel( new GridLayout() );

		optionsPanel.setBorder(
			new TitledBorder(
				new EtchedBorder() ,
				Resources.get( "Options" , "Options" ) ) );

		replaceOptions.addPair( "" , searchBackwards );
		replaceOptions.addPair( "" , searchForwards );
		replaceOptions.addPair( "" , chkCase );
		replaceOptions.addPair( "" , chkWord );

		optionsPanel.add( replaceOptions );

		replaceBox.addPair( "" , optionsPanel );

		replaceBox.setBorder(
			BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

		return replaceBox;
	}

	/** Create buttons. */

	private void createButtons()
	{
								// Action listeners for buttons.

		ActionListener searchAction =
			new ActionListener()
			{
				public void actionPerformed( ActionEvent event )
				{
					searchNext( false , true );
				}
			};
								// Close button.

		ActionListener CloseAction =
			new ActionListener()
			{
				public void actionPerformed( ActionEvent event )
				{
					setVisible( false );

					textPane.requestFocus();
				}
			};

		ActionListener replaceAction =
			new ActionListener()
			{
				public void actionPerformed( ActionEvent event)
				{
					searchNext( true , true );
				}
			};

		ActionListener replaceAllAction =
			new ActionListener()
			{
				public void actionPerformed( ActionEvent event )
				{
					Runnable runReplaceAll =
						new Runnable()
						{
							public void run()
     						{
								doReplaceAll();
     						}
     					};

					SwingUtilities.invokeLater( runReplaceAll );
				}
			};
        						// Add buttons.
		searchButton =
			addButton(
				Resources.get( "Find" , "Find" ) , searchAction );

		replaceButton =
			addButton(
				Resources.get( "Replace" , "Replace" ) , replaceAction );

		replaceAllButton =
			addButton(
				Resources.get( "ReplaceAll" , "Replace All" ) ,
					replaceAllAction );

		JButton CloseButton =
			addDefaultButton(
				Resources.get( "Close" , "Close" ) , CloseAction );

								// Diable buttons.  They will be
								// enabled when "setSelectedIndex" below
								// is called by the parent.

		searchButton.setVisible( false );
		replaceButton.setVisible( false );
		replaceAllButton.setVisible( false );
	}

	/** Set focus to specified component.
	 *
	 *	@param	component	Component to receive focus.
	 */

	protected void setFocusOn( final Component component )
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					component.requestFocus();
				}
			}
		);
	}

	/** Select Search or Replace dialog pane.
	 *
	 *	@param	index	0 for search, 1 for replace.
	 */

	public void setSelectedIndex( int index )
	{
								// Switch the selected pane,
								// either Search or Replace.

		tabbedPane.setSelectedIndex( index );

                                // Enable and disable buttons as needed.
                                // Replace button disable for Search.
                                // Search and Search Again buttons disabled
                                // for Replace.
		switch ( index )
		{
			case 0 : /* Search */
				replaceButton.setVisible( false );
				replaceAllButton.setVisible( false );
				searchButton.setVisible( true );
				setFocusOn( searchText1 );
				break;

			case 1 : /* Replace */
				searchButton.setVisible( false );
				replaceButton.setVisible( true );
				replaceAllButton.setVisible( true );
				setFocusOn( searchText2 );
				break;
		}
								// Make sure search or replace dialog
								// is visible.
		setVisible( true );
								// No search/replace done yet.
		searchIndex = -1;
	}

	/** Search for next occurrence of matching text.
	 *
	 *	@param	doReplace		True to replace text, false to find text.
	 *	@param	showWarnings	True to display "not found" messages.
	 */

	public int searchNext( boolean doReplace , boolean showWarnings )
	{
								// Get current position in text to search.

		int pos = textPane.getCaretPosition();

								// Determine starting search position
								// and direction of search.

		if ( modelBackwards.isSelected() != searchBackwards )
		{
			searchBackwards	= modelBackwards.isSelected();
			searchIndex	= -1;
		}
        						// Starting new search -- get the text
        						// to search.  The text to search is
        						// determined by the current caret position
        						// in the text, the direction of the search,
        						// and the length of the text.

		if ( searchIndex == -1 )
		{
			try
			{
				Document doc = textPane.getDocument();

				if ( searchBackwards )
					searchData = doc.getText( 0 , pos );
				else
					searchData = doc.getText( pos, doc.getLength() - pos );

				searchIndex = pos;
			}
			catch ( BadLocationException e )
			{
				warning( e.toString() );
				return -1;
			}
		}
									// Pick up string for which to search.
		String key = "";

		try
		{
			key = docSearch.getText( 0 , docSearch.getLength() );
		}
		catch ( BadLocationException e )
		{
		}
                                    // If search string is empty,
                                    // ask for it again.
		if ( key.length() == 0 )
		{
			warning
			(
				Resources.get
				(
					"Pleaseentertexttofind" ,
					"Please enter the text to find."
				)
			);

			return -1;
		}
            					// Convert both text for which to search and
            					// document text to search to lower case if
            					// caseless search.

		if ( !modelCase.isSelected() )
		{
			searchData	= searchData.toLowerCase();
			key			= key.toLowerCase();
		}
                                // When checking for whole word match,
                                // ensure search string does not contain
                                // any word break characters.

		if ( modelWord.isSelected() )
		{
			int badPos = 0;

			for ( int i = 0; i < key.length(); i++ )
			{
				badPos =
					DocumentTokenizer.WORD_SEPARATOR_CHARACTERS_STRING.indexOf(
						key.charAt( i ) );

				if ( badPos >= 0 )
				{
					PrintfFormat fmt	=
						new PrintfFormat
						(
							Resources.get
							(
								"Thesearchtextcontainsanillegalcharacter" ,
								"The search text contains an illegal character '%s'"
							)
						);

					String badChar	=
						DocumentTokenizer.WORD_SEPARATOR_CHARACTERS_STRING.charAt(
							badPos ) + "";


					warning( fmt.sprintf( new Object[]{ badChar } ) );

					return -1;
				}
			}
		}
								// Get replacement string if we're doing
								// replace operation.

		String replacement = "";

		if ( doReplace )
		{
			try
			{
				replacement =
					docReplace.getText( 0 , docReplace.getLength() );
			}
			catch ( BadLocationException e )
			{
			}
		}
								// Initialize starting and ending positions of
								// located search text.
		int start	= -1;
		int finish	= -1;
								// Loop through document text searching for
								// specified search text.
		while ( true )
		{
								// Move starting position for search backwards
								// or forwards depending upon direction of
								// search.

			if ( searchBackwards )
			{
				start = searchData.lastIndexOf( key , pos - 1 );
			}
			else
			{
				start = searchData.indexOf( key , pos - searchIndex );
			}
								// If we end up in front of the document text,
								// the search text was not found.
			if ( start < 0 )
			{
				if ( showWarnings )
				{
					warning
					(
						Resources.get
						(
							"Textnotfound" ,
							"Text not found."
						)
					);
				}

				return 0;
			}
								// Compute position of end of search text.

			finish = start + key.length();

								// If we're to search for whole words only,
								// make sure there is a word separator
								// character at the beginning and end of
								// the located text.

			if ( modelWord.isSelected() )
			{
				boolean textLeftBefore = ( start > 0 );

				boolean separatorBefore = textLeftBefore &&
					DocumentTokenizer.isSeparator(
						searchData.charAt( start - 1 ) );

				boolean textLeftAfter = ( finish < searchData.length() );

				boolean separatorAfter = textLeftAfter &&
					DocumentTokenizer.isSeparator(
						searchData.charAt( finish ) );

								// Not a whole word?

				if ( !separatorBefore || !separatorAfter )
				{
					if ( searchBackwards && textLeftBefore )

								// Can continue searching backwards.
					{
						pos = start;
						continue;
					}
								// Can continue searching forwards.

					if ( !searchBackwards && textLeftAfter )
					{
						pos = finish + 1;
						continue;
					}
								// Found, but not a whole word,
								// and we cannot continue searching
								// in the selected direction.

					if ( showWarnings )
					{
						warning
						(
							Resources.get
							(
								"Textnotfound" ,
								"Text not found."
							)
						);
					}

					return 0;
				}
			}

			break;
		}
								// Fix search index if we're searching forwards.
		if ( !searchBackwards )
		{
			start	+= searchIndex;
			finish	+= searchIndex;
		}
								// If we're replacing the found text ...
		if ( doReplace )
		{
								// If parent object supports edit batch undo,
								// set that now so that all replacements
								// may be undone with a single "undo."

			if ( parent instanceof EditBatch )
			{
				batchEditDepth++;

				if ( batchEditDepth == 1 )
				{
					 ((EditBatch)parent).startEditBatch();
				}
			}
    							// Select the found text in the document.

			setSelection( start, finish, searchBackwards );

								// Replace the selected text with the
								// replacement text.

			if ( parent instanceof SelectedTextReplacer )
			{
				SelectedTextReplacer replacerParent =
					(SelectedTextReplacer)parent;

				replacerParent.replaceSelection( replacement );
			}
			else
			{
				textPane.replaceSelection( replacement );
   			}
                                // Set the selection in the document to the
                                // replacement text.

			setSelection(
				start, start + replacement.length(), searchBackwards );

			searchIndex = -1;
								// Turn off edit batch undo.

			if ( parent instanceof EditBatch )
			{
				batchEditDepth--;

				if ( batchEditDepth <= 0 )
				{
					((EditBatch)parent).endEditBatch();
				}
			}
		}
		else
		{
								// If we're not replacing text, just select the
								// located text in the document.

			setSelection( start, finish, searchBackwards );
        }

		return 1;
	}

	/** Do replace all. */

	public void doReplaceAll()
	{
								// If parent object supports edit batch undo,
								// set that now so that all replacements
								// may be undone with a single "undo."

		if ( parent instanceof EditBatch )
		{
			batchEditDepth++;

			if ( batchEditDepth == 1 )
			{
				 ((EditBatch)parent).startEditBatch();
			}
		}

								// Counts # of replacements done.
		int counter = 0;

								// Continue replacing until no more matches
								// or user stops the process.
		while ( true )
		{
								// Look for next occurrence of search text.

			int result = searchNext( true , false );

			if ( result < 0 )	// error
				return;

			else if ( result == 0 )	// no more
				break;
            					// Increment count of replacements performed.
			counter++;
		}
								// Turn off edit batch undo.

		if ( parent instanceof EditBatch )
		{
			batchEditDepth--;

			if ( batchEditDepth <= 0 )
			{
				((EditBatch)parent).endEditBatch();
			}
		}

		PrintfFormat fmt;

		if ( counter == 1 )
		{
			fmt	=
				new PrintfFormat
				(
					Resources.get
					(
						"replacementperformed" ,
						"%i replacement performed"
					)
                );
		}
		else
		{
			fmt	=
				new PrintfFormat
				(
					Resources.get
					(
						"replacementsperformed" ,
						"%i replacements performed"
					)
                );
		}

		info( fmt.sprintf( new Object[]{ new Integer( counter ) } ) );
	}

	/** Set the selected text interval.
	 *
	 *	@param	selBegin		Beginning offset of selection.
	 *	@param	selEnd			Ending offset of selection.
	 *	@param	selBackwards	True to select backwards, else forwards.
	 */

	public void setSelection(
		int selBegin, int selEnd, boolean selBackwards )
	{
								// Get JTextComponent holding document
								// to search.
		if ( selBackwards )
		{
			textPane.setCaretPosition( selEnd );
			textPane.moveCaretPosition( selBegin );
		}
		else
		{
			textPane.getCaret().setSelectionVisible( true );
			textPane.select( selBegin , selEnd );
			textPane.getCaret().setSelectionVisible( true );
		}
	}

	/** Display warning message.
	 *
	 *	@param	message		The text of the warning message.
	 *
	 */

	protected void warning( String message )
	{
		WarningMessage warningMessage = new WarningMessage( message );
		warningMessage.addButton( Resources.get( "OK" , "OK" ) );
		warningMessage.doit();
	}

	/** Display information message.
	 *
	 *	@param	message		The text of the information message.
	 *
	 */

	protected void info( String message )
	{
		new InformationMessage( message );
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

