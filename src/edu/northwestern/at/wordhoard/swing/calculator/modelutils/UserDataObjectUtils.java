package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.cql.*;

import edu.northwestern.at.utils.xml.*;

import org.w3c.dom.*;

/**	User data object utilities.
 *
 *	<p>
 *	Class has package scope.
 *	</p>
 */

public class UserDataObjectUtils
{
	/**	Get Hibernate object name.
	 *
	 *	@param	udoClass	The class for the user data object.
	 *
	 *	@return				The corresponding Hibernate object name.
	 */

	protected static String getObjectClassName( Class udoClass )
	{
		String className	=
			ClassUtils.unqualifiedName( udoClass.getName() );

		return className;
	}

	/**	Check for a duplicate user data object.
	 *
	 *	@param	udoClass	The object's class.
	 *	@param	title		The object's title.
	 *	@param	owner		The object's owner.
	 *	@param	id			The object's ID.
	 *
	 *	@return				true if an object of the class "udoClass"
	 *						with the specified title and owner already
	 *						exists, and has an object ID different
	 *						from the specified ID.
	 */

	public static boolean isDuplicate
	(
		Class udoClass ,
		String title ,
		String owner ,
		Long id
	)
	{
		UserDataObject udo	=
			getUserDataObject( udoClass , title , owner );

		return ( ( udo != null ) && ( !udo.getId().equals( id ) ) );
	}

	/**	Delete a user data object.
	 *
	 *	@param	userDataObject	The user data object to delete.
	 *
	 *	@return					true if object deleted, false otherwise.
	 *
	 *	<p>
	 *	The currently logged in user must be the owner to delete
	 *	a user data object.
	 *	</p>
	 */

	public static boolean deleteUserDataObject
	(
		UserDataObject userDataObject
	)
	{
		boolean result	= false;

		if ( userDataObject != null )
		{
			try
			{
				PersistenceManager pm	= PMUtils.getPM();

				pm.evict( userDataObject );

				if ( WordHoardSettings.getBuildProgramRunning() )
				{
					pm.delete( userDataObject );
				}
				else
				{
					WordHoard.getSession().deleteUserDataObject
					(
						userDataObject.getClass() ,
						userDataObject.getId()
					);
				}

				result = true;
			}
			catch ( Exception e )
			{
			}
		}

		return result;
	}

	/**	Delete a user data object by title.
	 *
	 *	@param	udoClass	The user data object class.
	 *	@param	title		The title of the user data object to delete.
	 *
	 *	@return				true if user data object deleted, else false.
	 *						If the user data object didn't exist,
	 *						true is returned.
	 */

	public static boolean deleteUserDataObject
	(
		Class udoClass ,
		String title
	)
	{
		boolean result	= true;

		UserDataObject userDataObject	=
			getUserDataObject(
				udoClass , title , WordHoardSettings.getUserID() );

		if ( userDataObject != null )
		{
			result	= deleteUserDataObject( userDataObject );
		}

		return result;
	}

	/**	Delete multiple user data objects.
	 *
	 *	@param	userDataObjects	The user data objects to delete.
	 *
	 *	@return					true if user data objects deleted,
	 *							false otherwise.
	 */

	public static boolean deleteUserDataObjects
	(
		UserDataObject[] userDataObjects
	)
	{
		boolean result	= true;

		if ( userDataObjects != null )
		{
			for ( int i = 0 ; i < userDataObjects.length ; i++ )
			{
				result	=
					result & deleteUserDataObject( userDataObjects[ i ] );
			}
		}

		return result;
	}

	/**	Get user data objects.
	 *
	 *	@param	udoClass			Class of user data object to return.
	 *	@param	owner				The owner for the user data object
	 *								to fetch.
	 *	@param	loggedInUserOnly	Fetch objects only for the
	 *								logged-in user.
	 *	@param	title				Title of the user data object to fetch.
	 *	@param	qNames				Extra query names.
	 *	@param	qValues				Extra query values.
	 *
	 *	@return						The user data object(s) matching the
	 *								given attributes.  There should be only
	 *								one if a title is provided.  May be
	 *								null or empty if no objects are
	 *								available matching the selected
	 *								criteria.
	 *
	 *	<p>
	 *	If the owner is not the same as the currently logged in user,
	 *	the user data objects will only be returned if they are public.
	 *	</p>
	 */

	public static UserDataObject[] getUserDataObjects
	(
		Class	udoClass ,
		String	title ,
		String	owner ,
		boolean loggedInUserOnly ,
		String[] qNames ,
		Object[] qValues
	)
	{
								//	Get logged-in user.

		String currentUser		= WordHoardSettings.getUserID();

		boolean notLoggedIn		= WordHoardSettings.isEmptyUserID( currentUser );
		boolean noOwner			= WordHoardSettings.isEmptyUserID( owner );
		boolean ownerIsLoggedIn	=
			!notLoggedIn && !noOwner && ( currentUser.equals( owner ) );

								//	If we are to return objects for the
								//	logged-in user, but the user is not
								//	logged in, we cannot possibly have
								//	any results.  Likewise if the specified
								//	owner is the not the currently
								//	logged-in user.

		if ( loggedInUserOnly && ( notLoggedIn || !ownerIsLoggedIn ) )
		{
			return null;
		}
								//	Accumulate query names and values.

		ArrayList queryNames	= new ArrayList();
		ArrayList queryValues	= new ArrayList();

								//	Initialize query string.
								//	Only return active objects.

		String queryString	=
			"from " + getObjectClassName( udoClass ) +
			" udo where udo.isActive=1";

								//	Add title to query string if
								//	specified.

		if ( title != null )
		{
			queryNames.add( "title" );
			queryValues.add( title );

			queryString	= queryString + " and title=:title";
        }
        						//	Add extra names to query string if
        						//	specified.

		if ( qNames != null )
		{
			for ( int i = 0 ; i < qNames.length ; i++ )
			{
				queryNames.add( qNames[ i ] );
				queryValues.add( qValues[ i ] );

				queryString	=
					queryString + " and " + qNames[ i ] +
						"=:" + qNames[ i ];
			}
		}
                                //	Add owner to query string
                                //	if specified.  If the owner is
                                //	the currently logged in user,
                                //	return the private as well as the
                                //	public objects.

								//	If an owner is not specified ...

		if ( noOwner )
		{
								//	If not logged in ..

			if ( notLoggedIn )
			{
								//	Return only public objects.

				queryString	= queryString + " and udo.isPublic=1";
			}
			else
			{
								//	Currently logged in.
								//	Add the logged in user to the query.
								//	and retrieve both public and private
								//	objects for the logged in user.
								//	If we are not restricting object
								//	retrieval or those for the logged in
								//	user, get all public objects too.

				queryNames.add( "owner" );
				queryValues.add( currentUser );

				if ( loggedInUserOnly )
				{
					queryString	= queryString + " and udo.owner=:owner";
				}
				else
				{
					queryString	=
						queryString +
							" and (udo.isPublic=1 or udo.owner=:owner)";
				}
			}
		}
		else
		{
								//	An owner is specified.
								//	Add to the owner name and value
								//	to the query values.

			queryNames.add( "owner" );
			queryValues.add( owner );

								//	If the owner is the currently
								//	logged in user, return private
								//	objects of the logged in user as
								//	well as either the public objects
								//	for the currently logged in user
								//	or all users.

			if ( ownerIsLoggedIn )
			{
				if ( loggedInUserOnly )
				{
					queryString	= queryString + " and udo.owner=:owner";
				}
				else
				{
					queryString	=
						queryString +
							" and (udo.isPublic=1 or udo.owner=:owner)";
				}
			}
			else
			{
								//	If the requested owner is not
								//	the currently logged in user,
								//	return only the public objects
								//	for the specified user.

				queryString	=
					queryString +
					" and udo.owner=:owner and udo.isPublic=1";

								//	We should not get here.

				if ( loggedInUserOnly ) return null;
			}
		}
								//	Get query results.

		java.util.List list	=
			PersistenceManager.doQuery
			(
				queryString ,
				(String[])queryNames.toArray( new String[]{} ) ,
				(Object[])queryValues.toArray( new Object[]{} )
			);
								//	Sort results.

		SortedArrayList sortedUserDataObjectList =
			new SortedArrayList( list );

								//	Return results as an array.
		return
			(UserDataObject[])sortedUserDataObjectList.toArray(
				new UserDataObject[]{} );
	}

	/**	Get a user data object by title.
	 *
	 *	@param	udoClass	Class of user data object to return.
	 *	@param	owner		The owner for the user data object to fetch.
	 *	@param	title		The title of the user data object to fetch.
	 *	@param	qNames		Extra query names.
	 *	@param	qValues		Extra query values.
	 *
	 *	@return				The user data object with the requested title,
	 *						or null if not found.
	 *
	 *	<p>
	 *	If the owner is not the same as the currently logged in user,
	 *	the user data object will only be returned if it is public.
	 *	</p>
	 */

	public static UserDataObject getUserDataObject
	(
		Class	udoClass ,
		String	title ,
		String	owner ,
		String[] qNames ,
		Object[] qValues
	)
	{
		UserDataObject[] udos	=
			getUserDataObjects(
				udoClass , title , owner , false , qNames , qValues );

		return ( ( udos.length > 0 ) ? udos[ 0 ] : null );
	}

	/**	Get a user data object by title.
	 *
	 *	@param	udoClass	Class of user data object to return.
	 *	@param	owner		The owner for the user data object to fetch.
	 *	@param	title		The title of the user data object to fetch.
	 *
	 *	@return				The user data object with the requested title,
	 *						or null if not found.
	 *
	 *	<p>
	 *	If the owner is not the same as the currently logged in user,
	 *	the user data object will only be returned if it is public.
	 *	</p>
	 */

	public static UserDataObject getUserDataObject
	(
		Class	udoClass ,
		String	title ,
		String	owner
	)
	{
		return getUserDataObject( udoClass , title , owner , null , null );
	}

	/**	Get a user data object by title.
	 *
	 *	@param	udoClass	Class of user data object to return.
	 *	@param	title		The title of the user data object to fetch.
	 *
	 *	@return				The user data object with the requested title,
	 *						or null if not found.
	 *
	 *	<p>
	 *	Only public user data objects are returned.
	 *	</p>
	 */

	public static UserDataObject getUserDataObject
	(
		Class udoClass ,
		String title
	)
	{
		UserDataObject[] udos	=
			getUserDataObjects(
				udoClass , title , null , false , null , null );

		return ( ( udos.length > 0 ) ? udos[ 0 ] : null );
	}

	/**	Get all available public user data objects as an array.
	 *
	 *	@param	udoClass	The name of the user data object type.
	 *
	 *	@return				All available public user data objects as array.
	 *
	 *	<p>
	 *	If the user is logged in, any private user data objects belonging to the
	 *	logged in user are also returned.
	 *	</p>
	 */

	public static UserDataObject[] getUserDataObjects
	(
		Class udoClass ,
		String[] qNames ,
		Object[] qValues
	)
	{
		return getUserDataObjects(
			udoClass , null , null , false , qNames , qValues );
	}

	/**	Get all available public user data objects as an array.
	 *
	 *	@param	udoClass	The name of the user data object type.
	 *
	 *	@return				All available public user data objects as array.
	 *
	 *	<p>
	 *	If the user is logged in, any private user data objects belonging
	 *	to the logged in user are also returned.
	 *	</p>
	 */

	public static UserDataObject[] getUserDataObjects( Class udoClass )
	{
		return getUserDataObjects(
			udoClass , null , null , false , null , null );
	}

	/**	Get all available user data objects for specified owner as an array.
	 *
	 *	@param		udoClass			Name of the user data object type.
	 *	@param		owner				Owner for the user data objects
	 *									to fetch.
	 *	@param		onlyLoggedInUser	True to get only private objects.
	 *
	 *	@return							All available objects as array.
	 */

	public static UserDataObject[] getUserDataObjects
	(
		Class udoClass ,
		String owner ,
		boolean onlyLoggedInUser
	)
	{
		return getUserDataObjects(
			udoClass , null , owner, onlyLoggedInUser , null , null );
	}

	/**	Get all available user data objects for a specified owner as an array.
	 *
	 *	@param		udoClass	The name of the user data object type.
	 *	@param		owner		The owner.
	 *
	 *	@return					All available objects as array of
	 *							UserDataObject.
	 */

	public static UserDataObject[] getUserDataObjects
	(
		Class udoClass ,
		String owner
	)
	{
		return getUserDataObjects(
			udoClass , null , owner , false , null , null );
	}

	/**	Get all available user data objects for logged in user.
	 *
	 *	@param		udoClass	The name of the user data object type.
	 *
	 *	@return					All available objects for logged in user
	 *							as array of UserDataObject.
	 */

	public static UserDataObject[] getUserDataObjectsForLoggedInUser
	(
		Class udoClass
	)
	{
//		return getUserDataObjects(
//			udoClass , null , null , true , null , null );

								//	Get logged-in user.

		String currentUser		= WordHoardSettings.getUserID();

								//	If not logged in, return null.

		if ( WordHoardSettings.isEmptyUserID( currentUser ) ) return null;

		String queryString	=
			"from " + getObjectClassName( udoClass ) +
			" udo where udo.isActive=1 and owner=:owner";

		java.util.List list	=
			PersistenceManager.doQuery
			(
				queryString ,
				new String[]{ "owner" } ,
				new Object[]{ currentUser }
			);
								//	Sort results.

		SortedArrayList sortedUserDataObjectList =
			new SortedArrayList( list );

								//	Return results as an array.
		return
			(UserDataObject[])sortedUserDataObjectList.toArray(
				new UserDataObject[]{} );
	}

	/**	Get count of user data objects for a user.
	 *
	 *	@param		udoClass		The name of the user data object type.
	 *	@param		owner			The owner.
	 *	@param		onlyLoggedInUser		True to count only private objects.
	 *
	 *	@return						Count of user data objects owned
	 *								by "owner".
	 */

	public static int getUserDataObjectsCount
	(
		Class udoClass ,
		String owner ,
		boolean onlyLoggedInUser
	)
	{
		if ( WordHoardSettings.isEmptyUserID( owner ) ) return 0;

        String queryString	= "";

		if ( onlyLoggedInUser && WordHoardSettings.isCurrentUser( owner ) )
		{
			queryString	=
				"from " + getObjectClassName( udoClass )  + " udo " +
				"where udo.owner=:owner and " +
				"udo.isPublic=0 and udo.isActive=1";
		}
		else
		{
			queryString	=
				"from " + getObjectClassName( udoClass )  +
				" udo where udo.owner=:owner and " +
				"udo.isActive=1";

			if ( !WordHoardSettings.isCurrentUser( owner ) )
			{
				queryString	= queryString + " and udo.isPublic=1";
			}
		}

		return
			PersistenceManager.doCountQuery
			(
				queryString ,
				new String[]{ "owner" } ,
				new Object[]{ owner }
			);
	}

	/**	Get count of user data objects for specified user.
	 *
	 *	@param		udoClass	The name of the user data object type.
	 *	@param		owner		The owner.
	 *
	 *	@return					Count of user data objects owned by "owner".
	 */

	public static int getUserDataObjectsCount
	(
		Class udoClass ,
		String owner
	)
	{
		return getUserDataObjectsCount( udoClass , owner , false );
	}

	/**	Get count of user data objects for a user.
	 *
	 *	@param	udoClass			The name of the user data object type.
	 *	@param	loggedInUserOnly	Fetch objects only for the
	 *								logged-in user.
	 *
	 *	@return						Count of user data objects.
	 */

	public static int getUserDataObjectsCount
	(
		Class udoClass ,
		boolean loggedInUserOnly
	)
	{
		int result			= 0;

		String currentUser	= WordHoardSettings.getUserID();

		if	(	loggedInUserOnly &&
				WordHoardSettings.isEmptyUserID( currentUser ) ) return result;

		String queryString	= "";

		if ( loggedInUserOnly )
		{
			queryString	=
				"from " + getObjectClassName( udoClass ) + " udo " +
				"where udo.isActive=1 and udo.owner=:owner";

			result	=
				PersistenceManager.doCountQuery
				(
					queryString ,
					new String[]{ "owner" } ,
					new Object[]{ currentUser }
				);
		}
		else
		{
			queryString	=
				"from " + getObjectClassName( udoClass ) + " udo " +
				" where udo.isActive=1 and udo.isPublic=1";

			result	=
				PersistenceManager.doCountQuery
				(
					queryString ,
					null ,
					null
				);
		}

		return result;
	}

	/**	Get count of all available public user data objects.
	 *
	 *	@param	udoClass	The name of the user data object type.
	 *
	 *	@return				Count of all available public user data objects.
	 *
	 *	<p>
	 *	If the user is logged in, any private user data objects belonging
	 *	to the logged in user are also counted.
	 *	</p>
	 */

	public static int getUserDataObjectsCount
	(
		Class udoClass
	)
	{
		return getUserDataObjectsCount( udoClass , false );
	}

	/**	Create a user data object.
	 *
	 *	@param	userDataObject			The user data object to create.
	 *									user data object.
	 *
	 *	@return							Object of created object.
	 *									-1 if creation fails.
	 *
	 *	@throws							BadOwnerException if user is not
	 *									logged in.
	 */

	public static Long createUserDataObject
	(
		UserDataObject userDataObject
	)
		throws BadOwnerException
	{
		Long result	= new Long( -1 );

		if ( userDataObject != null )
		{
			try
			{
				if ( WordHoardSettings.getBuildProgramRunning() )
				{
					if ( PersistenceManager.doSave( userDataObject ) )
					{
						result	= userDataObject.getId();
					}
				}
				else
				{
					result	=
						WordHoard.getSession().createUserDataObject
						(
							userDataObject
						);
				}

				PersistenceManager pm	= PMUtils.getPM();

				if ( pm != null )
				{
					userDataObject	=
						(UserDataObject)pm.get(
							userDataObject.getClass() , result );
				}
			}
			catch ( Exception e )
			{
				result	= new Long( -1 );
//				e.printStackTrace();
			}
		}

		return result;
	}

	/**	Update a user data object.
	 *
	 *	@param	userDataObject			The user data object to update.
	 *	@param	userDataObjectUpdater	Method which updates fields of
	 *									user data object.
	 *
	 *	@return							true if update succeeds,
	 *									false otherwise.
	 *
	 *	@throws							BadOwnerException if user is not
	 *									logged in or is not the owner of the
	 *									user data object.
	 */

	public static boolean updateUserDataObject
	(
		UserDataObject userDataObject ,
		UserDataObjectUpdater userDataObjectUpdater
	)
		throws BadOwnerException
	{
		boolean result	= false;

		if ( userDataObject != null )
		{
			try
			{
				if ( WordHoardSettings.getBuildProgramRunning() )
				{
					PersistenceManager pm	= PMUtils.getPM();

					try
					{
						pm.begin();

						if ( userDataObjectUpdater != null )
						{
							userDataObjectUpdater.update( userDataObject );
						}

						pm.commit();

						result	= true;
					}
					catch ( PersistenceException e2 )
					{
						try
						{
							pm.rollback();
						}
						catch ( Exception e3 )
						{
						}
					}
					finally
					{
//						PMUtils.closePM( pm );
					}
				}
				else
				{
					WordHoard.getSession().updateUserDataObject
					(
						userDataObject ,
						userDataObjectUpdater
					);

					PersistenceManager pm	= PMUtils.getPM();

					pm.evict( userDataObject );

					userDataObject	=
						(UserDataObject)pm.get(
							userDataObject.getClass() ,
							userDataObject.getId() );

					pm.refresh( userDataObject );
				}

				result = true;
			}
			catch ( Exception e )
			{
//				e.printStackTrace();
				result	= false;
			}
		}

		return result;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected UserDataObjectUtils()
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

