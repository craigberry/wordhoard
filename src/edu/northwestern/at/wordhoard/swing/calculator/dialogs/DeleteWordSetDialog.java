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

/**	Displays delete wordset dialog.
 */

public class DeleteWordSetDialog
	extends SetDialog
{
	/**	Check box list holds word sets. */

	protected CheckBoxList wordSetsListBox	= new CheckBoxList();

	/**	Scroll pane for list of word sets. */

	protected XScrollPane scrollPane		=
		new XScrollPane( wordSetsListBox );

	/**	Selected word sets. */

	protected WordSet[] wordSets			= null;

	/**	Delete wordset dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public DeleteWordSetDialog( String dialogTitle , JFrame parent )
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

		setInitialFocus( wordSetsListBox );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Get word sets.

		WordSet[] wordSets	= WordSetUtils.getWordSetsForLoggedInUser();

								//	Add them to list box.

		wordSetsListBox.clear();
		wordSetsListBox.addItems( wordSets );

								//	Set renderer for list box.

		wordSetsListBox.setCellRenderer(
			new WordSetsCheckBoxListRenderer() );

								//	Create list selection listener.
								//	The Delete button is enabled when
								//	word sets are selected and disabled
								//	when no word sets are selected.


		ListSelectionListener listSelectionListener =
			new ListSelectionListener()
			{
				public void valueChanged( ListSelectionEvent event )
				{
					okButton.setEnabled( anySelected( wordSetsListBox ) );
				}
			};
								//	Set listener for changes to list box.

		wordSetsListBox.addListSelectionListener( listSelectionListener );
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
//		super.addFields( dialogFields );

		initializeFields();

		dialogFields.addPair
		(
			WordHoardSettings.getString
			(
				"htmlWordsets" ,
				"<html><strong><em>Word sets:</em></strong></html>"
			) ,
			scrollPane
		);
	}

	/**	Handles the OK button. */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		boolean anyChecked	= false;

		SortedArrayList wordsetList	= new SortedArrayList();

		for	(	int i = 0 ;
				i < wordSetsListBox.getModel().getSize() ;
				i++ )
		{
			boolean checked		= wordSetsListBox.isChecked( i );
			anyChecked			= anyChecked || checked;

			if ( checked )
			{
				WordSet wordset	=
					(WordSet)((CheckBoxListItem)wordSetsListBox.getItem( i )).getObject();

				wordsetList.add( wordset );
			}

			int nWordSets	= wordsetList.size();
			wordSets		= new WordSet[ nWordSets ];

			wordSets		= (WordSet[])wordsetList.toArray( wordSets );
		}

		if ( !anyChecked )
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"youDidNotSelectAnyWordSets",
					"You did not select any word sets." ) );

			wordSets	= null;
		}
		else
		{
			cancelled	= false;
			dispose();
		}
	};

	/**	Get the selected wordSets.
	 *
	 *	@return		Array of WordSet of selected wordSets.
	 */

	public WordSet[] getSelectedWordSets()
	{
		return wordSets;
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

