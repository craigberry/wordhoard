package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.event.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.cql.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	Displays create new query dialog.
 */

public class NewQueryDialog extends SetDialog
{
	/**	Query type. */

	protected int queryType		= WHQuery.WORDQUERY;

	/**	Query text. */

	protected String queryText	= "";

	/**	Query text label. */

	protected JLabel queryTextLabel	= new JLabel( "" );

	/**	Query edit field. */

	protected XTextArea queryTextEditField	=
		new XTextArea( queryText , 5 , 50 );

	/**	Scroll pane for list of word sets. */

	protected XScrollPane queryTextFieldScrollPane	=
		new XScrollPane( queryTextEditField );

	/**	Create new Query dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 *	@param	queryType	The query type.
	 */

	public NewQueryDialog
	(
		String dialogTitle ,
		JFrame parent ,
		int queryType
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

		this.queryType	= queryType;

		buildDialog();

		setInitialFocus( titleEditField );
	}

	/**	Create new query dialog.
	 *
	 *	@param	dialogTitle		Title for dialog.
	 *	@param	parent			Parent window for dialog.
	 *	@param	queryType		Query type.
	 *	@param	editingQuery	True if dialog for editing query.
	 */

	protected NewQueryDialog
	(
		String dialogTitle ,
		JFrame parent ,
		int queryType ,
		boolean editingQuery
	)
	{
		super
		(
			dialogTitle ,
			parent ,
			WordHoardSettings.getString
			(
				editingQuery ? "Update" : "Update" ,
				editingQuery ? "Create" : "Create"
			) ,
			WordHoardSettings.getString( "Cancel" , "Cancel" ) ,
			WordHoardSettings.getString( "Revert" , "Revert" ) ,
			editingQuery
		);

		this.queryType	= queryType;

		setInitialFocus( titleEditField );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Set query query edit field.

		queryTextEditField.setText( queryText );
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		super.addFields( dialogFields );

		queryTextLabel	=
			new JLabel
			(
				WordHoardSettings.getString
				(
					"Query" ,
					"Query"
				) + ":"
			);

		dialogFields.addPair( queryTextLabel , queryTextFieldScrollPane );
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

		isPublic	= isPublicCheckBox.isSelected();
		queryText	= StringUtils.trim( queryTextEditField.getText() );

								//	Title must be specified.

		if ( title.length() == 0 )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"youDidNotEnterATitle",
					"You did not enter a title."
				)
			);

			return;
		}
								//	Query text must be specified.

		if ( queryText.length() == 0 )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"youDidNotEnterAQuery",
					"You did not enter a query."
				)
			);

			return;
		}
		else
		{
								//	Check query syntax.
			try
			{
				CQLQuery query	= new CQLQuery( queryText );
			}
			catch ( Exception e )
			{
				new ErrorMessage( e.getMessage() );
				return;
			}
		}

		if ( !editingSet )
		{
			if	(	QueryUtils.getQuery
					(
						title ,
						WordHoardSettings.getUserID() ,
						queryType
					) != null
				)
			{
				new ErrorMessage
				(
					WordHoardSettings.getString
					(
						"Thatqueryalreadyexists",
						"That query already exists."
					)
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

	/**	Get query text.
	 *
	 *	@return		The query text.
	 */

	public String getQueryText()
	{
		return queryText;
	}

	/**	Get query type.
	 *
	 *	@return		The query type.
	 */

	public int getQueryType()
	{
		return queryType;
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

