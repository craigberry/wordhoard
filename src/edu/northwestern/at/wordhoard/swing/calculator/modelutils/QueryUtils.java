package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;

import org.w3c.dom.*;

/**	Query utilities.
 */

public class QueryUtils
{
	/**	Copy UserDataObject array to WHQuery array.
	 *
	 *	@param	udos		Array of user data objects, all actually
	 *						WHQuery objects.
	 *
	 *	@return				Array of WHQuery objects.
	 */

	protected static WHQuery[] udosToQueries( UserDataObject[] udos )
	{
		WHQuery[] result	= new WHQuery[ udos.length ];

		for ( int i = 0 ; i < udos.length ; i++ )
		{
			result[ i ]	= (WHQuery)udos[ i ];
		}

		return result;
	}

	/**	Add a new query.
	 *
	 *	@param	title		Title for the new query.
	 *	@param	description	Description for the new query.
	 *	@param	webPageURL	Web page URL for the new query.
	 *	@param	isPublic	True if query to be public.
	 *	@param	queryType	The type of query.
	 *	@param	queryText	The text of the query.
	 *
	 *	@return				Query object if query added, else null.
	 *
	 *	@throws				DuplicateQueryException if
	 *						(title,owner,queryType)
	 *						combination already exists.
	 *
	 *	@throws				BadOwnerException if the owner null or empty.
	 */

	public static WHQuery addQuery
	(
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		int queryType ,
		String queryText
	)
		throws DuplicateQueryException, BadOwnerException
	{
								//	Check if owner is valid.

		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException();
		}
								//	Check if query of this title already
								//	exists.  Don't allow duplication
								//	across query type either.

		WHQuery query	= getQuery( title , WordHoardSettings.getUserID() );

								//	Report error if so.

		if ( query != null )
		{
			throw new DuplicateQueryException();
		}
								//	Create new query object.
		query	=
			new WHQuery
			(
				title ,
				description ,
				webPageURL ,
				WordHoardSettings.getUserID() ,
				isPublic ,
				queryType ,
				queryText
			);
								//	Persist the query.
		Long id	=
			UserDataObjectUtils.createUserDataObject( query );

		if ( id.longValue() != -1 )
		{
			query	= (WHQuery)PersistenceManager.doLoad( WHQuery.class , id );
		}
								//	Return the newly persisted Query object.
		return query;
	}

	/**	Delete a query.
	 *
	 *	@param	query	The query to delete.
	 *
	 *	@return			true if query deleted, false otherwise.
	 *
	 *	<p>
	 *	A query may only be deleted by its owner.
	 *	</p>
	 */

	public static boolean deleteQuery( WHQuery query )
	{
		return UserDataObjectUtils.deleteUserDataObject( query );
	}

	/**	Delete a query by title.
	 *
	 *	@param	title		The title of the query to delete.
	 *	@param	queryType	Query type to retrieve.
	 *
	 *	@return				true if query deleted, false otherwise.
	 *						If the query didn't exist, true is returned.
	 */

	public static boolean deleteQuery
	(
		String title ,
		int queryType
	)
	{
		boolean result	=	true;

		WHQuery	query	=
			getQuery
			(
				title ,
				WordHoardSettings.getUserID() ,
				queryType
			);

		if ( query != null )
		{
			result	= deleteQuery( query );
		}

		return result;
	}

	/**	Delete multiple queries.
	 *
	 *	@param	queries		The queries to delete.
	 *
	 *	@return				true if queries deleted, false otherwise.
	 */

	public static boolean deleteQueries
	(
		WHQuery[] queries
	)
	{
		return UserDataObjectUtils.deleteUserDataObjects( queries );
	}

	/**	Get a query by title.
	 *
	 *	@param	title		The title of the query to fetch.
	 *	@param	owner		The owner of the query to fetch.
	 *	@param	queryType	Query type to retrieve.
	 *
	 *	@return				The query of the specified type with the requested
	 *						title, or null if not found.
	 */

	public static WHQuery getQuery
	(
		String title ,
		String owner ,
		int queryType
	)
	{
		return
			(WHQuery)UserDataObjectUtils.getUserDataObject
			(
				WHQuery.class ,
				title ,
				owner ,
				new String[]{ "queryType" } ,
				new Object[]{ new Integer( queryType ) }
			);
	}

	/**	Get a query by title.
	 *
	 *	@param	title		The title of the query to fetch.
	 *	@param	owner		The owner of the query to fetch.
	 *
	 *	@return				The query with the requested title,
	 *						or null if not found.
	 */

	public static WHQuery getQuery
	(
		String title ,
		String owner
	)
	{
		return
			(WHQuery)UserDataObjectUtils.getUserDataObject
			(
				WHQuery.class , title , owner
			);
	}

	/**	Get a query by title.
	 *
	 *	@param	title		The title of the query to fetch.
	 *	@param	queryType	Query type to retrieve.
	 *
	 *	@return			The query of the requested type with the requested
	 *					title, or null if not found.
	 */

	public static WHQuery getQuery( String title , int queryType )
	{
		return
			(WHQuery)UserDataObjectUtils.getUserDataObject
			(
				WHQuery.class ,
				title ,
				null ,
				new String[]{ "queryType" } ,
				new Object[]{ new Integer( queryType ) }
			);
	}

	/**	Get a query, loading it to current persistence manager if needed.
	 *
	 *	@param	query	The query.
	 *
	 *	@return			The query loaded into the current persistence
	 *					manager context, or null if not found.
	 */

	public static WHQuery getQuery( WHQuery query )
	{
		WHQuery result	= null;

		if ( query != null )
		{
			result	=
				(WHQuery)PersistenceManager.doLoad(
					WHQuery.class , query.getId() );
		}

		return result;
	}

	/**	Get all available public queries as an array.
	 *
	 *	@param		queryType		Query type to retrieve.
	 *
	 *	@return		All available queries as an array of Query.
	 */

	public static WHQuery[] getQueries( int queryType )
	{
		String owner				= WordHoardSettings.getUserID();
		java.util.List queryList	= new ArrayList();

		if ( WordHoardSettings.isEmptyUserID( owner ) )
		{
			queryList	=
				PersistenceManager.doQuery
				(
					"from WHQuery qu where qu.isPublic=1 " +
					"and queryType=:queryType",
					new String[]{ "queryType" } ,
					new Object[]{ new Integer( queryType ) } ,
					false
				);
		}
		else
		{
			queryList	=
				PersistenceManager.doQuery
				(
					"from WHQuery qu where queryType=:queryType " +
					"and (qu.isPublic=1 or qu.owner=:owner)" ,
					new String[]{ "owner" , "queryType" } ,
					new Object[]
					{
						owner ,
						new Integer( queryType )
					} ,
					false
				);
		}

		SortedArrayList sortedQueryList =
			new SortedArrayList( queryList );

		return (WHQuery[])sortedQueryList.toArray( new WHQuery[]{} );
	}

	/**	Get all available queries for a specified owner as an array.
	 *
	 *	@param		owner			Owner of queries to retrieve.
	 *	@param		queryType		Query type to retrieve.
	 *	@return						All available queries as an array of
	 *								WHQuery.  Null if owner is null or
	 *								empty.
	 */

	public static WHQuery[] getQueries
	(
		String owner ,
		int queryType
	)
	{
								//	Is owner null?  Return null.

		if ( WordHoardSettings.isEmptyUserID( owner ) ) return null;

        String queryString			=
			"from WHQuery qu where qu.owner = :owner " +
			"and qu.queryType=:queryType ";

								//	If owner is currently logged in,
								//	return both public and private
								//	queries.  Otherwise, return only
								//	public queries.

		if ( !WordHoardSettings.isCurrentUser( owner ) )
		{
			queryString	= queryString + "and qu.isPublic=1";
		}

		java.util.List queryList	=
			PersistenceManager.doQuery
			(
				queryString ,
				new String[]{ "owner" , "queryType" } ,
				new Object[]{ owner , new Integer( queryType ) } ,
				false
			);

		SortedArrayList sortedQueryList =
			new SortedArrayList( queryList );

		return (WHQuery[])sortedQueryList.toArray( new WHQuery[]{} );
	}

	/**	Get all available queries for the logged-in user.
	 *
	 *	@param		queryType	Query type to retrieve.
	 *
	 *	@return		All available queries of the specified type for the
	 *				logged-in user.  Returns null if the user is not logged
	 *				in.
	 */

	public static WHQuery[] getQueriesForLoggedInUser( int queryType )
	{
		return
			getQueries( WordHoardSettings.getUserID() , queryType );
	}

	/**	Get count of queries for a user.
	 *
	 *	@param		owner	The owner.
	 *
	 *	@param		queryType	Query type to count.
	 *
	 *	@return				Count of queries owned by "owner".
	 */

	public static int getQueriesCount( String owner , int queryType )
	{
		return getQueries( owner , queryType ).length;
	}

	/**	Check for a duplicate query.
	 *
	 *	@param	queryType	The query type.
	 *	@param	title		The query title.
	 *	@param	owner		The query owner.
	 *	@param	id			The query ID.
	 *
	 *	@return				true if a query of the specified type
	 *						with the specified title and owner already
	 *						exists, and has an object ID different
	 *						from the specified ID.
	 */

	public static boolean isDuplicate
	(
		int queryType ,
		String title ,
		String owner ,
		Long id
	)
	{
		WHQuery query	=
			getQuery( title , owner , queryType );

		return ( ( query != null ) && ( !query.getId().equals( id ) ) );
	}

	/**	Update a query.
	 *
	 *	@param	query		The query to update.
	 *	@param	title		Title for the query.
	 *	@param	description	Description for the query.
	 *	@param	webPageURL	Web page URL for the query.
	 *	@param	isPublic	True if query is public.
	 *	@param	queryType	The type of query.
	 *	@param	queryText	The text of the query.
	 *
	 *	@return				true if update succeed, false otherwise.
	 */

	public static boolean updateQuery
	(
		WHQuery query ,
		final String title ,
		final String description ,
		final String webPageURL ,
		final boolean isPublic ,
		final int queryType ,
		final String queryText
	)
		throws DuplicateQueryException, BadOwnerException
	{
		boolean result			= false;

		if ( query == null ) return result;

		if	(	isDuplicate
				(
					queryType ,
					title ,
					query.getOwner() ,
					query.getId()
				)
			)
		{
			throw new DuplicateQueryException();
		}

		return
			UserDataObjectUtils.updateUserDataObject
			(
				query ,
				new UserDataObjectUpdater()
				{
					public void update
					(
						UserDataObject userDataObject
					)
					{
						WHQuery query	= (WHQuery)userDataObject;

						query.setTitle( title );
						query.setDescription( description );
						query.setWebPageURL( webPageURL );
						query.setModificationTime( new Date() );
						query.setIsPublic( isPublic );
						query.setQueryType( queryType );
						query.setQueryText( queryText );
					}
				}
			);
	}

	/**	Import one or more queries from XML file.
	 *
	 *	@param	importDocument	The DOM document containing the queries
	 *							to import.
	 *
	 *	@param	queryType		The type of the queries.
	 *
	 *	@return					The imported queries.  May be empty.
	 *
	 *	<p>
	 *	Note:	The queries are not persisted here.  That is the
	 *			responsibility of the caller.
	 *	</p>
	 */

	public static WHQuery[] importQueries
	(
		org.w3c.dom.Document importDocument ,
		int queryType
	)
	{
		WHQuery[] result	= new WHQuery[ 0 ];

								//	Return if document is null.

		if ( importDocument == null ) return result;

								//	Get the root node of the import document.

		org.w3c.dom.Node rootElement	=
			importDocument.getDocumentElement();

								//	If the root node is not "wordhoard",
								//	this is a bogus document.

		if ( !rootElement.getNodeName().equals( "wordhoard" ) )
		{
			return result;
		}
								//	Get the query children of the root node.

		ArrayList queryChildren	=
			DOMUtils.getChildren( rootElement , "whquery" );

								//	If there are no work sets just return.

		if	(	( queryChildren == null ) ||
				( queryChildren.size() == 0 ) )
		{
			return result;
		}
								//	Holds imported work sets.

		ArrayList queries	= new ArrayList();

								//	Process each work set.

		for ( int i = 0 ; i < queryChildren.size() ; i++ )
		{
								//	Get next work set node.

			Node queryNode	= (Node)queryChildren.get( i );

								//	Import to work set.

			WHQuery query		= importFromDOMDocument( queryNode );

								//	If imported OK, add to list.

			if ( ( query != null ) && ( query.getQueryType() == queryType ) )
			{
				queries.add( query );
			}
		}
								//	Return all imported work sets as array.

		return (WHQuery[])queries.toArray( new WHQuery[]{} );
	}

	/**	Import a specified query by name from a DOM document.
	 *
	 *	@param	queryNode		The DOM node which is the root of the
	 *							query to import.
	 *
	 *	@return					The imported query, or null if the
	 *							import fails.
	 */

	public static WHQuery importFromDOMDocument
	(
		org.w3c.dom.Node queryNode
	)
		throws BadOwnerException
	{
		WHQuery result	= null;

           						//	If work set node is null, quit.

		if ( queryNode == null ) return result;

								//	If the user is not logged in,
								//	throw a BadOwnerException.

		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException
			(
				WordHoardSettings.getString
				(
					"Notloggedin" ,
					"Not logged in"
				)
			);
		}
								//	If the word set node is not "whquery",
								//	do nothing further.

		if ( !queryNode.getNodeName().equals( "whquery" ) )
		{
			return result;
		}
								//	Create the query entry.

		result	= new WHQuery( queryNode , WordHoardSettings.getUserID() );

		return result;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected QueryUtils()
	{
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

