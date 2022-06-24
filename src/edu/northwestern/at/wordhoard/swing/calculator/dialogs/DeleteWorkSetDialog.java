package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Displays delete workset dialog.
 */

public class DeleteWorkSetDialog
	extends SetDialog
{
	/**	Check box list holds worksets. */

	protected CheckBoxList workSetsListBox	= new CheckBoxList();

	/**	Scroll pane for list of worksets. */

	protected XScrollPane scrollPane		=
		new XScrollPane( workSetsListBox );

	/**	Selected worksets. */

	protected WorkSet[] worksets			= null;

	/**	Delete workset dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public DeleteWorkSetDialog( String dialogTitle , JFrame parent )
	{
		super
		(
			dialogTitle ,
			parent ,
			WordHoardSettings.getString( "Delete" , "Delete" ) ,
			WordHoardSettings.getString( "Cancel" , "Cancel" ) ,
			WordHoardSettings.getString( "Revert" , "Revert" ) ,
			false
		);

		buildDialog();

		okButton.setEnabled( false );

		setInitialFocus( workSetsListBox );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Get work sets.

		WorkSet[] workSets	=
			WorkSetUtils.getWorkSetsForLoggedInUser();

								//	Add them to list box.

		workSetsListBox.clear();
		workSetsListBox.addItems( workSets );

								//	Set renderer for list box.

		workSetsListBox.setCellRenderer(
			new WorkSetsCheckBoxListRenderer() );

								//	Create list selection listener.
								//	The Delete button is enabled when
								//	work sets are selected and disabled
								//	when no work sets are selected.

		ListSelectionListener listSelectionListener =
			new ListSelectionListener()
			{
				public void valueChanged( ListSelectionEvent event )
				{
					okButton.setEnabled( anySelected( workSetsListBox ) );
				}
			};
								//	Set listener for changes to list box.

		workSetsListBox.addListSelectionListener( listSelectionListener );
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		initializeFields();

		dialogFields.addPair
		(
			WordHoardSettings.getString
			(
				"htmlWorksets" ,
				"<html><strong><em>Work sets:</em></strong></html>"
			) ,
			scrollPane
		);
	}

	/**	Handles the OK button. */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		boolean anyChecked	= false;

		SortedArrayList worksetList	= new SortedArrayList();

		for	(	int i = 0 ;
				i < workSetsListBox.getModel().getSize() ;
				i++ )
		{
			boolean checked		= workSetsListBox.isChecked( i );
			anyChecked			= anyChecked || checked;

			if ( checked )
			{
				WorkSet workset	=
					(WorkSet)((CheckBoxListItem)workSetsListBox.getItem( i )).getObject();

				worksetList.add( workset );
			}

			int nWorkSets	= worksetList.size();
			worksets		= new WorkSet[ nWorkSets ];

			worksets		= (WorkSet[])worksetList.toArray( worksets );
		}

		if ( !anyChecked )
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"youDidNotSelectAnyWorkSets",
					"You did not select any worksets." ) );

			worksets	= null;
		}
		else
		{
			cancelled	= false;
			dispose();
		}
	};

	/**	Get the selected worksets.
	 *
	 *	@return		Array of WorkSet of selected worksets.
	 */

	public WorkSet[] getSelectedWorkSets()
	{
		return worksets;
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

