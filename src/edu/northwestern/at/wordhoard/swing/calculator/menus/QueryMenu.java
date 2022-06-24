package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	WordHoard Calculator Query Menu.
 */

public class QueryMenu extends BaseMenu
{
	/**	The query menu items. */

	/**	Create new word query. */

	protected JMenuItem queryMenuNewWordQueryMenuItem;

	/**	Edit word query. */

	protected JMenuItem queryMenuEditWordQueryMenuItem;

    /**	Delete word query. */

	protected JMenuItem queryMenuDeleteWordQueryMenuItem;

	/**	Create new bibliographic query. */

	protected JMenuItem queryMenuNewBiblioQueryMenuItem;

    /**	Edit bibliographic query. */

	protected JMenuItem queryMenuEditBiblioQueryMenuItem;

    /**	Delete bibliographic query. */

	protected JMenuItem queryMenuDeleteBiblioQueryMenuItem;

	/**	Create query menu.
	 */

	public QueryMenu()
	{
		super
		(
			WordHoardSettings.getString( "queryMenuName" , "Query" )
		);
	}

	/**	Create query menu.
	 *
	 *	@param	menuBar		The menu bar to which to attach the query menu.
	 */

	public QueryMenu( JMenuBar menuBar )
	{
		super
		(
			WordHoardSettings.getString( "queryMenuName" , "Query" ) ,
			menuBar
		);
	}

	/**	Create query menu.
	 *
	 *	@param	parentWindow	Parent window for the menu.
	 */

	public QueryMenu( AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "queryMenuName" , "Query" ) ,
			null ,
			parentWindow
		);
	}

	/**	Create query menu.
	 *
	 *	@param	menuBar			The menu bar to which to attach the menu.
	 *	@param	parentWindow	Parent window for the menu.
	 */

	public QueryMenu( JMenuBar menuBar , AbstractWindow parentWindow )
	{
		super
		(
			WordHoardSettings.getString( "queryMenuName" , "Query" ) ,
			menuBar ,
			parentWindow
		);
	}

	/** Query menu listener.
	 *
	 *	<p>
	 *	Enables and disables Query menu items to reflect logged-in user
	 *	status.
	 *	</p>
	 */

	public MenuListener queryMenuListener =
		new MenuListener()
		{
			public void menuCanceled( MenuEvent menuEvent )
			{
			}

			public void menuDeselected( MenuEvent menuEvent )
			{
			}

			public void menuSelected( MenuEvent menuEvent )
			{
				setQueryMenuItemsAvailability();
			}
		};

	/**	Create the menu items.
	 */

	protected void createMenuItems()
	{
								// New word query

		queryMenuNewWordQueryMenuItem		=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"queryMenuNewWordQueryItem" ,
					"New word query..."
				) ,
				new GenericActionListener( "newWordQuery" )
			);

								// Edit word query

		queryMenuEditWordQueryMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"queryMenuEditWordQueryItem" ,
					"Edit word query..."
				) ,
				new GenericActionListener( "editWordQuery" )
			);

								// Delete word query

		queryMenuDeleteWordQueryMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"queryMenuDeleteWordQueryItem" ,
					"Delete word query..."
				) ,
				new GenericActionListener( "deleteWordQuery" )
			);
//
		addSeparator();
//
								// New bibliographic query

		queryMenuNewBiblioQueryMenuItem		=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"queryMenuNewBiblioQueryItem" ,
					"New bibliographic query..."
				) ,
				new GenericActionListener( "newBiblioQuery" )
			);
								// Edit bibliographic query

		queryMenuEditBiblioQueryMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"queryMenuEditBiblioQueryItem" ,
					"Edit bibliographic query..."
				) ,
				new GenericActionListener( "editBiblioQuery" )
			);
								// Delete bibliographic query

		queryMenuDeleteBiblioQueryMenuItem	=
			addMenuItem
			(
				WordHoardSettings.getString
				(
					"queryMenuDeleteBiblioQueryItem" ,
					"Delete bibliographic query..."
				) ,
				new GenericActionListener( "deleteBiblioQuery" )
			);

		if ( menuBar != null ) menuBar.add( this );

								//	Adding query menu listener here fails.
								//	Adding in WordHoardCalculatorWindow
								//	succeeds.

//		addMenuListener( queryMenuListener );
	}

	/**	Set availability of query menu items.
	 */

	public void setQueryMenuItemsAvailability()
	{
								//	Get user ID for logged-in user.
								//	May be null if user is not logged in.

		String userID	= WordHoardSettings.getUserID();

		if ( userID != null )
		{
								//	Get count of queries.

			int wordQueriesCount	=
				QueryUtils.getQueriesCount( userID , WHQuery.WORDQUERY );

			int biblioQueriesCount	=
				QueryUtils.getQueriesCount( userID , WHQuery.WORKPARTQUERY );

								//	Enable or disable edit and delete
								//	menu entries depending upon availability
								//	of queries owned by logged-in user.

			queryMenuNewWordQueryMenuItem.setEnabled( true );

			queryMenuNewBiblioQueryMenuItem.setEnabled( true );

			queryMenuEditWordQueryMenuItem.setEnabled(
				wordQueriesCount > 0 );

			queryMenuDeleteWordQueryMenuItem.setEnabled(
				wordQueriesCount > 0 );

			queryMenuEditBiblioQueryMenuItem.setEnabled(
				biblioQueriesCount > 0 );

			queryMenuDeleteBiblioQueryMenuItem.setEnabled(
				biblioQueriesCount > 0 );
		}
		else
		{
			queryMenuNewWordQueryMenuItem.setEnabled( false );
			queryMenuEditWordQueryMenuItem.setEnabled( false );
			queryMenuDeleteWordQueryMenuItem.setEnabled( false );
			queryMenuNewBiblioQueryMenuItem.setEnabled( false );
			queryMenuEditBiblioQueryMenuItem.setEnabled( false );
			queryMenuDeleteBiblioQueryMenuItem.setEnabled( false );
		}
	}

	/**	Handle menu changes when logging in.
	 */

	public void handleLogin()
	{
		setQueryMenuItemsAvailability();
	}

	/**	Handle menu changes when logging out.
	 */

	public void handleLogout()
	{
		setQueryMenuItemsAvailability();
	}

	/**	Helper for create new query.
	 *
	 *	@param	dialog	The query dialog.
	 */

	protected WHQuery doCreateQuery( NewQueryDialog dialog )
	{
		WHQuery query	= null;
								//	Enable the busy cursor.
		setBusyCursor();
								//	Try creating the query.
		try
		{
			query	=
				QueryUtils.addQuery
				(
					dialog.getSetTitle() ,
					dialog.getDescription() ,
					dialog.getWebPageURL() ,
					dialog.getIsPublic() ,
					dialog.getQueryType() ,
					dialog.getQueryText()
				);
		}
		catch ( DuplicateQueryException e )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"Thatqueryalreadyexists" ,
					"That query already exists."
				)
			);
		}
								//	Enable the default cursor.
		setDefaultCursor();
            					//	Close persistence manager for this thread.

   		closePersistenceManager();

								//	Report if query created.

		if ( query != null )
		{
			new InformationMessage
			(
				WordHoardSettings.getString
				(
					"queryCreated" ,
					"Query created."
				)
			);
		}
		else
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"queryNotCreated" ,
					"Query could not be created."
				)
			);
		}

		return query;
	}

	/**	Helper for new query.
	 *
	 *	@param	dialog	The query dialog.
	 */

	protected void doNewQuery( NewQueryDialog dialog )
	{
		dialog.show( getCalculatorWindow() );

		getMainTabbedPane().paintImmediately(
			getMainTabbedPane().getVisibleRect() );

		if ( !dialog.getCancelled() )
		{
			final NewQueryDialog finalDialog	= dialog;

			Thread runner = new Thread( "New Query" )
			{
				public void run()
				{
					doCreateQuery( finalDialog );
				}
			};

			Thread awtEventThread	= SwingUtils.getAWTEventThread();

			if ( awtEventThread != null )
			{
				ThreadUtils.setPriority(
					runner , awtEventThread.getPriority() - 1 );
			}

			runner.start();
		}
	}

	/**	Create a new word query. */

	protected void newWordQuery()
	{
		NewQueryDialog dialog	=
			new NewQueryDialog
			(
				WordHoardSettings.getString
				(
					"createWordQueryDialogTitle" ,
					"Create Word Query"
				) ,
				getCalculatorWindow() ,
				WHQuery.WORDQUERY
			);

		doNewQuery( dialog );
	}

	/**	Create a new bibliographic query. */

	protected void newBiblioQuery()
	{
		NewQueryDialog dialog	=
			new NewQueryDialog
			(
				WordHoardSettings.getString
				(
					"createBiblioQueryDialogTitle" ,
					"Create Bibliographic Query"
				) ,
				getCalculatorWindow() ,
				WHQuery.WORKPARTQUERY
			);

		doNewQuery( dialog );
	}

	/**	Helper for editing a word query.
	 *
	 *	@param	dialog	Edit word query dialog.
	 */

	protected void doEditQuery( EditQueryDialog dialog )
	{
								//	Enable the busy cursor.
		setBusyCursor();

		boolean updatedOK	=
			QueryUtils.updateQuery
			(
				dialog.getQuery() ,
				dialog.getSetTitle() ,
				dialog.getDescription() ,
				dialog.getWebPageURL() ,
				dialog.getIsPublic() ,
				dialog.getQueryType() ,
				dialog.getQueryText()
			);
								//	Enable normal cursor.
		setDefaultCursor();

		if ( updatedOK )
		{
			new InformationMessage
			(
				WordHoardSettings.getString
				(
					"queryUpdated" ,
					"Query updated."
				)
			);
		}
		else
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"queryNotUpdated" ,
					"Query could not be updated."
				)
			);
		}
	}

	/**	Edit existing word query. */

	protected void editWordQuery()
	{
		WHQuery[] queries	= QueryUtils.getQueries( WHQuery.WORDQUERY );

		if ( ( queries == null ) || ( queries.length == 0 ) )
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"Nowordqueriestoedit" ,
					"There are no word queries you can edit." ) );

			return;
		}

		EditQueryDialog dialog	=
			new EditQueryDialog
			(
				WordHoardSettings.getString
				(
					"editWordQueryDialogTitle" ,
					"Edit Word Query"
				) ,
				getCalculatorWindow() ,
				WHQuery.WORDQUERY
			);

		dialog.show( getCalculatorWindow() );

		getMainTabbedPane().paintImmediately(
			getMainTabbedPane().getVisibleRect() );

		if ( !dialog.getCancelled() )
		{
			doEditQuery( dialog );
		}
	}

	/**	Edit existing bibliographic query. */

	protected void editBiblioQuery()
	{
		WHQuery[] queries	= QueryUtils.getQueries( WHQuery.WORKPARTQUERY );

		if ( ( queries == null ) || ( queries.length == 0 ) )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"Nobiblioqueriestoedit" ,
					"There are no bibliographic queries you can edit."
				)
			);

			return;
		}

		EditQueryDialog dialog	=
			new EditQueryDialog
			(
				WordHoardSettings.getString
				(
					"editBiblioQueryDialogTitle" ,
					"Edit Bibliographic Query"
				) ,
				getCalculatorWindow() ,
				WHQuery.WORKPARTQUERY
			);

		dialog.show( getCalculatorWindow() );

		getMainTabbedPane().paintImmediately(
			getMainTabbedPane().getVisibleRect() );

		if ( !dialog.getCancelled() )
		{
			doEditQuery( dialog );
		}
	}

	/**	Helper for deleting queries.
	 *
	 *	@param	queries		Queries to delete.
	 */

	protected void doDeleteQuery( WHQuery[] queries )
	{
		int countToDelete	= queries.length;

		if ( QueryUtils.deleteQueries( queries ) )
		{
			if ( countToDelete == 1 )
			{
				new InformationMessage
				(
					WordHoardSettings.getString
					(
						"queryDeleted" ,
						"Query deleted."
					)
				);
			}
			else
			{
				new InformationMessage
				(
					WordHoardSettings.getString
					(
						"queriesDeleted" ,
						"Queries deleted."
					)
				);
			}
		}
		else
		{
			if ( countToDelete == 1 )
			{
				new ErrorMessage
				(
					WordHoardSettings.getString
					(
						"queryCouldNotBeDeleted" ,
						"Query could not be deleted."
					)
				);
			}
			else
			{
				new ErrorMessage
				(
					WordHoardSettings.getString
					(
						"queriesCouldNotBeDeleted" ,
						"Queries could not be deleted."
					)
				);
			}
		}
	}

	/**	Delete word queries. */

	protected void deleteWordQuery()
	{
		WHQuery[] queries	= QueryUtils.getQueries( WHQuery.WORDQUERY );

		if ( ( queries == null ) || ( queries.length == 0 ) )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"Nowordqueriestodelete" ,
					"There are no word queries you can delete."
				)
			);

			return;
		}

		DeleteQueryDialog dialog =
			new DeleteQueryDialog
			(
				WordHoardSettings.getString
				(
					"deleteWordQueryDialogTitle" ,
					"Delete Word Queries"
				) ,
				getCalculatorWindow() ,
				WHQuery.WORDQUERY
			);

		dialog.show( getCalculatorWindow() );

		if ( !dialog.getCancelled() )
		{
			doDeleteQuery( dialog.getSelectedQueries() );
		}
	}

	/**	Delete bibliographic queries. */

	protected void deleteBiblioQuery()
	{
		WHQuery[] queries	= QueryUtils.getQueries( WHQuery.WORKPARTQUERY );

		if ( ( queries == null ) || ( queries.length == 0 ) )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"Nobiblioqueriestodelete" ,
					"There are no bibliographic queries you can delete."
				)
			);

			return;
		}

		DeleteQueryDialog dialog =
			new DeleteQueryDialog
			(
				WordHoardSettings.getString
				(
					"deleteBiblioQueryDialogTitle" ,
					"Delete Bibliographic Queries"
				) ,
				getCalculatorWindow() ,
				WHQuery.WORKPARTQUERY
			);

		dialog.show( getCalculatorWindow() );

		if ( !dialog.getCancelled() )
		{
			doDeleteQuery( dialog.getSelectedQueries() );
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

