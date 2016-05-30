package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;

/**	Displays save word set dialog.
 */

public class SaveWordSetDialog
	extends SetDialog
{
	/**	List holds word sets. */

	protected DefaultListModel listModel		= new DefaultListModel();
	protected XList wordSetsListBox				= new XList( listModel );

	/**	Scroll pane for list of wordsets. */

	protected XScrollPane wordSetsScrollPane	=
		new XScrollPane( wordSetsListBox );

	/**	 wordsets. */

	protected WordSet[] wordSets 				= null;

	/**	Selected wordset. */

	protected WordSet selectedWordset			= null;

	/**	save wordset dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public SaveWordSetDialog( String dialogTitle , JFrame parent )
	{
		super
		(
			dialogTitle ,
			parent ,
			WordHoardSettings.getString( "Create" , "Create" ) ,
			WordHoardSettings.getString( "Cancel" , "Cancel" ) ,
			WordHoardSettings.getString( "Revert" , "Revert" ) ,
			false
		);

		buildDialog();

		okButton.setEnabled( true );

		setInitialFocus( wordSetsListBox );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Remove current word set list.
		listModel.clear();

								//	Get word sets.

		wordSets = WordSetUtils.getWordSetsForLoggedInUser();

								//	Add them to list box.

		if ( wordSets != null ) wordSetsListBox.setListData( wordSets );

								//	Set renderer for word sets to edit.

		wordSetsListBox.setCellRenderer(
			new WordSetsListRenderer() );

								//	Single selection in
								//	word sets list box.

		wordSetsListBox.setSelectionMode(
			ListSelectionModel.SINGLE_SELECTION );

								//	Create list selection listener.

		ListSelectionListener listSelectionListener =
			new WordSetsListSelectionListener();

		wordSetsListBox.addListSelectionListener( listSelectionListener );
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

	/**	Handles the OK button pressed.
	 *
	 *	@param	event	The event.
	 */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		title		= StringUtils.trim( titleEditField.getText() );
		description	= StringUtils.trim( descriptionEditField.getText() );
		webPageURL	= StringUtils.trim( webPageURLEditField.getText() );
		isPublic	= isPublicCheckBox.isSelected();

		int index	= wordSetsListBox.getSelectedIndex();

		if ( index >= 0 )
		{
			selectedWordset	= wordSets[ index ];
		}

		if ( title.length() == 0 )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"youDidNotEnterATitle",
					"You did not enter a title"
				)
			);
		}
		else
		{
			dispose();
		}
	}

	/**	Get the selected wordsets.
	 *
	 *	@return		Array of WordSet of selected wordsets.
	 */

	public WordSet getSelectedItem()
	{
		return selectedWordset;
	}

	/**	Handle changes to selected word set. */

	protected class WordSetsListSelectionListener
		implements ListSelectionListener
	{
		/**	Create listener.
		 */

		public WordSetsListSelectionListener()
		{
			super();
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
								//	Set the word set fields to match
								//	selected list item.

				WordSet wordSet	= (WordSet)object;

				titleEditField.setText( wordSet.getTitle() );

				descriptionEditField.setText(
					wordSet.getDescription() );

				webPageURLEditField.setText( wordSet.getWebPageURL() );

				isPublicCheckBox.setSelected( wordSet.getIsPublic() );

								//	Change OK button's label to "save"
								//	to reflect fact we are updating
								//	an existing word set.

				okButton.setText
				(
					WordHoardSettings.getString( "Save" , "Save" )
				);
	        }
	        else
	        {
								//	Change OK button's label to "create"
								//	to reflect fact we are creating
								//	a new word set.

				okButton.setText
				(
					WordHoardSettings.getString( "Create" , "Create" )
				);
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

