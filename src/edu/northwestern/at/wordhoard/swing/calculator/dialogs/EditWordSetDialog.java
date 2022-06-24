package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Displays edit word set dialog.
 */

public class EditWordSetDialog
	extends NewWordSetDialog
{
	/**	List holds word sets. */

	protected DefaultListModel listModel		= new DefaultListModel();
	protected XList wordSetsListBox				= new XList( listModel );

	/**	Scroll pane for list of word sets. */

	protected XScrollPane wordSetsScrollPane	=
		new XScrollPane( wordSetsListBox );

	/**	The word set to update. */

	protected static WordSet wordSetToUpdate	= null;

	/**	Word sets list selection listener. */

	protected WordSetsListSelectionListener wordSetsListSelectionListener =
		null;

	/**	Create edit work set dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public EditWordSetDialog
	(
		String dialogTitle ,
		JFrame parent
	)
	{
		super
		(
			dialogTitle ,
			parent ,
			true
		);

		buildDialog();

		wordSetsScrollPane.setPreferredSize( new Dimension( 400 , 100 ) );

		wordSetsListSelectionListener.enableDialogFields( false );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Word set to update.

		wordSetToUpdate		= null;

								//	Remove current word set list.
		listModel.clear();
								//	Get word sets.

		WordSet[] wordSets	= WordSetUtils.getWordSetsForLoggedInUser();

								//	Add them to list box.

		if ( wordSets != null ) wordSetsListBox.setListData( wordSets );

								//	Set renderer for word sets to edit.

		wordSetsListBox.setCellRenderer
		(
			new WordSetsListRenderer()
		);
								//	Single selection in
								//	work sets list box.

		wordSetsListBox.setSelectionMode
		(
			ListSelectionModel.SINGLE_SELECTION
		);
								//	Listen for changes to list.

		wordSetsListSelectionListener	=
			new WordSetsListSelectionListener();

		wordSetsListBox.addListSelectionListener(
			wordSetsListSelectionListener );
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		dialogFields.addPair
		(
			WordHoardSettings.getString
			(
				"WordSets" ,
				"Word sets:"
			) ,
			wordSetsScrollPane
		);

		super.addFields( dialogFields );
	}

	/**	Handles the OK button pressed. */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		if ( wordSetsListBox.getSelectedValue() == null )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"youDidNotSelectAWordSet",
					"You did not select a word set. "
				)
			);

			return;
		}
		else
		{
			wordSetToUpdate	= (WordSet)wordSetsListBox.getSelectedValue();
		}

		super.handleOKButtonPressed( event );
	}

	/**	Get the selected work set.
	 *
	 *	@return		Work set to update.
	 */

	public WordSet getWordSet()
	{
		return wordSetToUpdate;
	}

	/**	Handle changes to selected work set. */

	protected class WordSetsListSelectionListener
		implements ListSelectionListener
	{
		/**	Create listener.
		 */

		public WordSetsListSelectionListener()
		{
			super();
		}

		/**	Enable or disable dialog fields.
		 *
		 *	@param	enable	true to enable dialog fields, false to disable.
		 */

		public void enableDialogFields( boolean enable )
		{
			titleEditField.setEnabled( enable );
            titleLabel.setEnabled( enable );

			descriptionEditField.setEnabled( enable );
        	descriptionLabel.setEnabled( enable );

			webPageURLEditField.setEnabled( enable );
            webPageURLLabel.setEnabled( enable );

            isPublicCheckBox.setEnabled( enable );
		}

		/**	Handle change to selection in wordSet list.
		 *
		 *	@param	event	The list selection event.
		 */

		public void valueChanged( ListSelectionEvent event )
		{
								//	Get list which fired this event.

			JList list		= (JList)event.getSource();

								//	Get currently selected value.

			Object object	= list.getSelectedValue();

								//	If there is a selected value ...

			if ( object != null )
			{
								//	Enable the dialog fields.

				enableDialogFields( true );

								//	Set the work set title field and
								//	change the list of checked work parts
								//	in the work parts tree to match those
								//	in the selected work set.
				try
				{
					WordSet wordSet	= (WordSet)object;
					titleEditField.setText( wordSet.getTitle() );
					descriptionEditField.setText( wordSet.getDescription() );
					webPageURLEditField.setText( wordSet.getWebPageURL() );
					isPublicCheckBox.setSelected( wordSet.getIsPublic() );
				}
				catch ( Exception e )
				{
					Err.err( e );
				}
	        }
	        else
	        {
								//	Disable the dialog fields.

				titleEditField.setText( "" );
				descriptionEditField.setText( "" );
				webPageURLEditField.setText( "" );
				isPublicCheckBox.setSelected( false );

				enableDialogFields( false );
	        }
		}
	}

	/**	Renderer for list with word sets as entries.
	 */

	protected class WordSetsListRenderer
		extends DefaultListCellRenderer
	{
		/**	Returns a component configured to display the specified value.
		 *
		 *	@param	list			The JList we're painting.
		 *
		 *	@param	value			The value to be painted.
		 *
		 *	@param	index			The cell's index.
		 *
		 *	@param	isSelected		True if the cell is selected.
		 *
		 *	@param	cellHasFocus	True if the cell has the focus.
		 *
		 *	@return		A component whose paint() method will render the
		 *				specified value.
		 */

		public Component getListCellRendererComponent
		(
			JList list ,
			Object value ,
			int index ,
			boolean isSelected ,
			boolean cellHasFocus
		)
		{
			super.getListCellRendererComponent
			(
				list ,
				value ,
				index ,
				isSelected ,
				cellHasFocus
			);

			WordSet wordSet	= (WordSet)value;

			setText( new WordCounter( wordSet ).toHTMLString() );

			return this;
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

