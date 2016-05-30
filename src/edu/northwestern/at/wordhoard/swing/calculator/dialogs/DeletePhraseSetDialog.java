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

/**	Displays delete phraseSet dialog.
 */

public class DeletePhraseSetDialog
	extends SetDialog
{
	/**	Check box list holds phrase sets. */

	protected CheckBoxList phraseSetsListBox	= new CheckBoxList();

	/**	Scroll pane for list of phrase sets. */

	protected XScrollPane scrollPane		=
		new XScrollPane( phraseSetsListBox );

	/**	Selected phrase sets. */

	protected PhraseSet[] phraseSets		= null;

	/**	Delete phrase set dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public DeletePhraseSetDialog( String dialogTitle , JFrame parent )
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

		setInitialFocus( phraseSetsListBox );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Get phrase sets.

		PhraseSet[] phraseSets	= PhraseSetUtils.getPhraseSets();

								//	Add them to list box.

		phraseSetsListBox.clear();
		phraseSetsListBox.addItems( phraseSets );

								//	Set renderer for list box.

		phraseSetsListBox.setCellRenderer(
			new PhraseSetsCheckBoxListRenderer() );

								//	Create list selection listener.
								//	The Delete button is enabled when
								//	phrase sets are selected and disabled
								//	when no phrase sets are selected,

		ListSelectionListener listSelectionListener =
			new ListSelectionListener()
			{
				public void valueChanged( ListSelectionEvent event )
				{
					okButton.setEnabled( anySelected( phraseSetsListBox ) );
				}
			};
								//	Set listener for changes to list box.

		phraseSetsListBox.addListSelectionListener( listSelectionListener );
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		super.addFields( dialogFields );

		dialogFields.addPair
		(
			WordHoardSettings.getString
			(
				"htmlPhrasesets" ,
				"<html><strong><em>Phrase sets:</em></strong></html>"
			) ,
			scrollPane
		);
	}

	/**	Handles the OK button. */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		boolean anyChecked	= false;

		SortedArrayList phraseSetList	= new SortedArrayList();

		for	(	int i = 0 ;
				i < phraseSetsListBox.getModel().getSize() ;
				i++ )
		{
			boolean checked		= phraseSetsListBox.isChecked( i );
			anyChecked			= anyChecked || checked;

			if ( checked )
			{
				PhraseSet phraseSet	=
					(PhraseSet)((CheckBoxListItem)phraseSetsListBox.getItem( i )).getObject();

				phraseSetList.add( phraseSet );
			}

			int nPhraseSets	= phraseSetList.size();
			phraseSets		= new PhraseSet[ nPhraseSets ];

			phraseSets		= (PhraseSet[])phraseSetList.toArray( phraseSets );
		}

		if ( !anyChecked )
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"youDidNotSelectAnyPhraseSets",
					"You did not select any word sets." ) );

			phraseSets	= null;
		}
		else
		{
			cancelled	= false;
			dispose();
		}
	};

	/**	Get the selected phraseSets.
	 *
	 *	@return		Array of PhraseSet of selected phraseSets.
	 */

	public PhraseSet[] getSelectedPhraseSets()
	{
		return phraseSets;
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

