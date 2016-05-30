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

/**	Displays save work set dialog.
 */

public class SaveWorkSetDialog
	extends SetDialog
{
	/**	List holds work sets. */

	protected DefaultListModel listModel		= new DefaultListModel();
	protected XList workSetsListBox				= new XList( listModel );

	/**	Scroll pane for list of work sets. */

	protected XScrollPane workSetsScrollPane	=
		new XScrollPane( workSetsListBox );

	/**	 Work sets. */

	protected WorkSet[] workSets 				= null;

	/**	Selected work set. */

	protected WorkSet selectedWorkset			= null;

	/**	Save work set dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public SaveWorkSetDialog( String dialogTitle , JFrame parent )
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

		setInitialFocus( workSetsListBox );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Remove current work set list.
		listModel.clear();

								//	Get work sets for currently
								//	logged-in user.

		workSets = WorkSetUtils.getWorkSetsForLoggedInUser();

								//	Add them to list box.

		if ( workSets != null ) workSetsListBox.setListData( workSets );

								//	Set renderer for work sets to edit.

		workSetsListBox.setCellRenderer(
			new WorkSetsListRenderer() );

								//	Single selection in
								//	work sets list box.

		workSetsListBox.setSelectionMode(
			ListSelectionModel.SINGLE_SELECTION );

								//	Create list selection listener.

		ListSelectionListener listSelectionListener =
			new WorkSetsListSelectionListener();

		workSetsListBox.addListSelectionListener( listSelectionListener );
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
				"WorkSets" ,
				"Work sets:"
			) ,
			workSetsScrollPane
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

		int index	= workSetsListBox.getSelectedIndex();

		if ( index >= 0 )
		{
			selectedWorkset	= workSets[ index ];
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

	/**	Get the selected worksets.
	 *
	 *	@return		Array of WorkSet of selected worksets.
	 */

	public WorkSet getSelectedItem()
	{
		return selectedWorkset;
	}

	/**	Handle changes to selected work set. */

	protected class WorkSetsListSelectionListener
		implements ListSelectionListener
	{
		/**	Create listener.
		 */

		public WorkSetsListSelectionListener()
		{
			super();
		}

		/**	Handle change to selection in workSet list.
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
								//	Set the work set fields to match
								//	selected list item.

				WorkSet workSet	= (WorkSet)object;

				titleEditField.setText( workSet.getTitle() );

				descriptionEditField.setText(
					workSet.getDescription() );

				webPageURLEditField.setText( workSet.getWebPageURL() );

				isPublicCheckBox.setSelected( workSet.getIsPublic() );

								//	Change OK button's label to "save"
								//	to reflect fact we are updating
								//	an existing work set.

				okButton.setText
				(
					WordHoardSettings.getString( "Save" , "Save" )
				);
	        }
	        else
	        {
								//	Change OK button's label to "create"
								//	to reflect fact we are creating
								//	a new work set.

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


