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

/**	Displays delete query dialog.
 */

public class DeleteQueryDialog
	extends SetDialog
{
	/**	Type of queries to delete. */

	protected int queryType					= WHQuery.WORDQUERY;

	/**	Check box list holds queries. */

	protected CheckBoxList queriesListBox	= new CheckBoxList();

	/**	Scroll pane for list of queries. */

	protected XScrollPane scrollPane		=
		new XScrollPane( queriesListBox );

	/**	Selected queries. */

	protected WHQuery[] queries				= null;

	/**	Delete workset dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	queryType	The query type to delete.
	 *	@param	parent		Parent window for dialog.
	 */

	public DeleteQueryDialog
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
			WordHoardSettings.getString( "Delete" , "Delete" ) ,
			WordHoardSettings.getString( "Cancel" , "Cancel" ) ,
			WordHoardSettings.getString( "Revert" , "Revert" ) ,
			false
		);

		this.queryType	= queryType;

		buildDialog();

		okButton.setEnabled( false );

		setInitialFocus( queriesListBox );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Get queries.

		WHQuery[] queries	=
			QueryUtils.getQueriesForLoggedInUser( queryType );

								//	Add them to list box.

		queriesListBox.clear();
		queriesListBox.addItems( queries );

								//	Set renderer for list box.

		queriesListBox.setCellRenderer(
			new QueriesCheckBoxListRenderer() );

								//	Create list selection listener.
								//	The Delete button is enabled when
								//	queries are selected and disabled
								//	when no queries are selected.

		ListSelectionListener listSelectionListener =
			new ListSelectionListener()
			{
				public void valueChanged( ListSelectionEvent event )
				{
					okButton.setEnabled( anySelected( queriesListBox ) );
				}
			};
								//	Set listener for changes to list box.

		queriesListBox.addListSelectionListener( listSelectionListener );
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
				"Queries" ,
				"Queries"
			) ,
			scrollPane
		);
	}

	/**	Handles the OK button. */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		boolean anyChecked	= false;

		SortedArrayList queryList	= new SortedArrayList();

		for	(	int i = 0 ;
				i < queriesListBox.getModel().getSize() ;
				i++ )
		{
			boolean checked		= queriesListBox.isChecked( i );
			anyChecked			= anyChecked || checked;

			if ( checked )
			{
				WHQuery query	=
					(WHQuery)((CheckBoxListItem)queriesListBox.getItem( i )).getObject();

				queryList.add( query );
			}

			int nQueries	= queryList.size();
			queries		= new WHQuery[ nQueries ];

			queries		= (WHQuery[])queryList.toArray( queries );
		}

		if ( !anyChecked )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"youDidNotSelectAnyQueries",
					"You did not select any queries."
				)
			);

			queries	= null;
		}
		else
		{
			cancelled	= false;
			dispose();
		}
	};

	/**	Get the selected queries.
	 *
	 *	@return		Array of Query of selected queries.
	 */

	public WHQuery[] getSelectedQueries()
	{
		return queries;
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

