package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.swing.calculator.cql.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;

/**	Displays create new phrase set dialog.
 */

public class NewPhraseSetDialog extends SetDialog
{
	/**	The combo box holding the texts. */

	protected WordCounterTreeCombo analysisTextChoices;

	/**	Analysis text. */

	protected static WordCounter analysisText		= null;

	/**	Phrase query. */

	protected WHQuery phraseSetQuery				= null;

	/**	The combo box holding the queries. */

	protected JComboBox queries;

	/**	Create new PhraseSet dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public NewPhraseSetDialog
	(
		String dialogTitle ,
		JFrame parent
	)
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
	}

	/**	Create new phrase set dialog.
	 *
	 *	@param	dialogTitle			Title for dialog.
	 *	@param	parent				Parent window for dialog.
	 *	@param	editingPhraseSet	True if dialog for editing phrase set.
	 */

	protected NewPhraseSetDialog
	(
		String dialogTitle ,
		JFrame parent ,
		boolean editingPhraseSet
	)
	{
		super
		(
			dialogTitle ,
			parent ,
			WordHoardSettings.getString
			(
				editingPhraseSet ? "Update" : "Update" ,
				editingPhraseSet ? "Create" : "Create"
			) ,
			WordHoardSettings.getString( "Cancel" , "Cancel" ) ,
			WordHoardSettings.getString( "Revert" , "Revert" ) ,
			editingPhraseSet
		);

		setInitialFocus( titleEditField );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

        						//	Analysis text choices.

		analysisTextChoices	=
			new WordCounterTreeCombo(
				true , true , false , true , analysisText );

								//	Query choices.
		queries	=
			new JComboBox( QueryUtils.getQueries( WHQuery.WORDQUERY ) );

								//	Set renderer for queries.

		queries.setRenderer
		(
			new QueriesListRenderer()
		);
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
				"Texts" ,
				"Analysis Texts"
			) ,
			analysisTextChoices
		);

		dialogFields.addPair
		(
			WordHoardSettings.getString
			(
				"Queries" ,
				"Queries"
			) ,
			queries
		);
	}

	/**	Handles the OK button pressed.
	 *
	 *	@param	event	The event.
	 */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		title	=
			StringUtils.trim( titleEditField.getText() );

		description	=
			StringUtils.trim( descriptionEditField.getText() );

		webPageURL		=
			StringUtils.trim( webPageURLEditField.getText() );

		isPublic		= isPublicCheckBox.isSelected();
		analysisText	= (WordCounter)analysisTextChoices.getSelectedWordCounter();
		phraseSetQuery	= (WHQuery)queries.getSelectedItem();
		phraseSetQuery	= QueryUtils.getQuery( phraseSetQuery );

								//	Make sure a phrase set title was entered.

		if ( title.length() == 0 )
		{
			new ErrorMessage(
				WordHoardSettings.getString
				(
					"youDidNotEnterATitle",
					"You did not enter a title." )
				);

			return;
		}
								//	Make sure an analysis text was entered.

		if ( analysisText == null )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"Youmustselecttextfromwhichtoselectphrases" ,
					"You must select a text from which to select phrases"
				)
			);

			return;
		}
								//	Make sure a query was entered.

		if ( phraseSetQuery == null )
		{
			new ErrorMessage(
				WordHoardSettings.getString
				(
					"youDidNotEnterAQuery",
					"You did not select a query." )
				);

			return;
		}
								//	Check the syntactic validity of the
								//	query.

		String errorMessage	= "";

		try
		{
			CQLQuery query	= new CQLQuery( phraseSetQuery.getQueryText() );
		}
		catch ( InvalidCQLQueryException e )
		{
			errorMessage	= e.getMessage();
		}

		if ( errorMessage.length() > 0 )
		{
			PrintfFormat errorFormat	=
				new PrintfFormat
				(
					WordHoardSettings.getString
					(
							"Invalidquery" ,
							"Invalid query: %s"
					)
				);

			new ErrorMessage
			(
				errorFormat.sprintf( new Object[]{ errorMessage } )
			);

			return;
		}
								//	Check if the phrase set already exists.
		if ( !editingSet )
		{
			if	(	PhraseSetUtils.getPhraseSet(
						title , WordHoardSettings.getUserID() ) != null )
			{
				new ErrorMessage
				(
					WordHoardSettings.getString(
						"Thatphrasesetalreadyexists",
						"That phrase set already exists." )
				);
			}
			else
			{
				cancelled	= false;
				dispose();
			}
		}
		else
		{
			cancelled	= false;
			dispose();
		}
	}

	/**	Get analysis text.
	 *
	 *	@return		The analysis text.
	 */

	public WordCounter getAnalysisText()
	{
		return analysisText;
	}

	/**	Get query text.
	 *
	 *	@return		The query text.
	 */

	public String getQueryText()
	{
		return phraseSetQuery.getQueryText();
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

