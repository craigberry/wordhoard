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
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;

/**	Displays open workset dialog.
 */

public class OpenWorkSetDialog
	extends SetDialog
{
	/**	List holds work sets. */

	protected DefaultListModel listModel	= new DefaultListModel();
	protected XList workSetsListBox			= new XList( listModel );

	/**	Scroll pane for list of worksets. */

	protected XScrollPane scrollPane		=
		new XScrollPane( workSetsListBox );

	/** The work sets to display. */

	protected WorkSet[] workSets			= null;

	/**	Selected work set. */

	protected WorkSet selectedWorkset		= null;

	/**	Open work set dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public OpenWorkSetDialog( String dialogTitle , JFrame parent )
	{
		super
		(
			dialogTitle ,
			parent ,
			WordHoardSettings.getString( "Open" , "Open" ) ,
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

								//	Remove current work set list.
		listModel.clear();

								//	Get work sets.

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
								//	The Open button is enabled when
								//	work sets are selected and disabled
								//	when no work sets are selected.

		ListSelectionListener listSelectionListener =
			new ListSelectionListener()
			{
				public void valueChanged( ListSelectionEvent event )
				{
					okButton.setEnabled(
						!workSetsListBox.isSelectionEmpty() );
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
		int index		= workSetsListBox.getSelectedIndex();
		selectedWorkset	= workSets[ index ];
		dispose();
	};

	/**	Get the selected worksets.
	 *
	 *	@return		Array of WorkSet of selected worksets.
	 */

	public WorkSet getSelectedItem()
	{
		return selectedWorkset;
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


