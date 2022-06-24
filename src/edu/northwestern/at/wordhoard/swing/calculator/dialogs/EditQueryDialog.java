package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Displays edit query dialog.
 */

public class EditQueryDialog
	extends NewQueryDialog
{
	/**	List holds queries. */

	protected DefaultListModel listModel		= new DefaultListModel();
	protected XList queriesListBox				= new XList( listModel );

	/**	Scroll pane for list of queries. */

	protected XScrollPane queriesScrollPane	=
		new XScrollPane( queriesListBox );

	/**	The query to update. */

	protected static WHQuery queryToUpdate	= null;

	/**	Queries list selection listener. */

	protected QueriesListSelectionListener queriesListSelectionListener =
		null;

	/**	Create edit work set dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 *	@param	queryType	The query type.
	 */

	public EditQueryDialog
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
			queryType ,
			true
		);

		buildDialog();

		queriesScrollPane.setPreferredSize( new Dimension( 400 , 100 ) );

		queriesListSelectionListener.enableDialogFields( false );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Word set to update.

		queryToUpdate		= null;

								//	Remove current query list.
		listModel.clear();
								//	Get queries.

		WHQuery[] queries	=
			QueryUtils.getQueriesForLoggedInUser( queryType );

								//	Add them to list box.

		if ( queries != null ) queriesListBox.setListData( queries );

								//	Set renderer for queries to edit.

		queriesListBox.setCellRenderer
		(
			new QueriesListRenderer()
		);
								//	Single selection in
								//	work sets list box.

		queriesListBox.setSelectionMode
		(
			ListSelectionModel.SINGLE_SELECTION
		);
								//	Listen for changes to list.

		queriesListSelectionListener	=
			new QueriesListSelectionListener();

		queriesListBox.addListSelectionListener
		(
			queriesListSelectionListener
		);
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
				"Queries" ,
				"Queries"
			) ,
			queriesScrollPane
		);

		super.addFields( dialogFields );
	}

	/**	Handles the OK button pressed. */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		if ( queriesListBox.getSelectedValue() == null )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"youDidNotSelectAQuery",
					"You did not select a query. "
				)
			);

			return;
		}
		else
		{
			queryToUpdate	= (WHQuery)queriesListBox.getSelectedValue();
		}

		super.handleOKButtonPressed( event );
	}

	/**	Get the selected query.
	 *
	 *	@return		Query to update.
	 */

	public WHQuery getQuery()
	{
		return queryToUpdate;
	}

	/**	Handle changes to selected query. */

	protected class QueriesListSelectionListener
		implements ListSelectionListener
	{
		/**	Create listener.
		 */

		public QueriesListSelectionListener()
		{
			super();
		}

		/**	Enable or disable dialog fields.
		 *
		 *	@param	enable	true to enable dialog fields, false to disable.
		 */

		public void enableDialogFields( boolean enable )
		{
			titleEditField.setEnabled( enable );
            titleLabel.setEnabled( enable );

			descriptionEditField.setEnabled( enable );
        	descriptionLabel.setEnabled( enable );

			webPageURLEditField.setEnabled( enable );
            webPageURLLabel.setEnabled( enable );

            isPublicCheckBox.setEnabled( enable );

			queryTextEditField.setEnabled( enable );
			queryTextLabel.setEnabled( enable );
		}

		/**	Handle change to selection in query list.
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
								//	Enable the dialog fields.

				enableDialogFields( true );

								//	Set the work set title field and
								//	change the list of checked work parts
								//	in the work parts tree to match those
								//	in the selected work set.
				try
				{
					WHQuery query	= (WHQuery)object;
					titleEditField.setText( query.getTitle() );
					descriptionEditField.setText( query.getDescription() );
					webPageURLEditField.setText( query.getWebPageURL() );
					isPublicCheckBox.setSelected( query.getIsPublic() );
					queryTextEditField.setText( query.getQueryText() );
				}
				catch ( Exception e )
				{
					Err.err( e );
				}
	        }
	        else
	        {
								//	Disable the dialog fields.

				titleEditField.setText( "" );
				descriptionEditField.setText( "" );
				webPageURLEditField.setText( "" );
				isPublicCheckBox.setSelected( false );
				queryTextEditField.setText( "" );

				enableDialogFields( false );
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

