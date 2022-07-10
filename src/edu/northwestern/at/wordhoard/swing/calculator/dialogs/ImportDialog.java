package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.event.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Displays import dialog.
 */

public class ImportDialog extends ImportExportDialog
{
	/**	Create new import dialog.
	 *
	 *	@param	title		Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 *	@param	importDocument	The document to import.
	 */

	public ImportDialog
	(
		String title ,
		JFrame parent ,
		org.w3c.dom.Document importDocument
	)
	{
		super( title , parent , importDocument );
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		super.addFields( dialogFields );

		String renameDuplicatesString	=
			WordHoardSettings.getString
			(
				"Renameeduplicateentries" ,
				"Rename duplicate import entries"
			);

		renameDuplicatesCheckBox.setText( renameDuplicatesString );
		renameDuplicatesCheckBox.setSelected( renameDuplicates );

		dialogFields.addPair
		(
			"" ,
			renameDuplicatesCheckBox
		);
	}

	/**	Handles the OK button pressed.
	 *
	 *	@param	event	The event.
	 */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		renameDuplicates	= renameDuplicatesCheckBox.isSelected();

		super.handleOKButtonPressed( event );
	}

	/**	Get phrase sets from DOM document.
	 *
	 *	@return		The phrase sets.
	 */

	protected PhraseSet[] getPhraseSets()
	{
		return new PhraseSet[ 0 ];
	}

	/**	Get word sets from DOM document.
	 *
	 *	@return		The word sets.
	 */

	protected WordSet[] getWordSets()
	{
		return WordSetUtils.importWordSets( importDocument );
	}

	/**	Get work sets from DOM document.
	 *
	 *	@return		The work sets.
	 */

	protected WorkSet[] getWorkSets()
	{
		return WorkSetUtils.importWorkSets( importDocument );
	}

	/**	Get work part queries.
	 *
	 *	@return		The work part queries.
	 */

	protected WHQuery[] getWorkPartQueries()
	{
		return QueryUtils.importQueries
		(
			importDocument ,
			WHQuery.WORKPARTQUERY
		);
	}

	/**	Get word queries.
	 *
	 *	@return		The word queries.
	 */

	protected WHQuery[] getWordQueries()
	{
		return QueryUtils.importQueries
		(
			importDocument ,
			WHQuery.WORDQUERY
		);
	}

	/**	Get the rename duplicate entries flag.
	 *
	 *	@return		true to rename duplicate entries while importing.
	 */

	public boolean getRenameDuplicates()
	{
		return renameDuplicates;
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

