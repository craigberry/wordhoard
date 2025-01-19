package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;

import edu.northwestern.at.utils.xml.*;

import org.w3c.dom.*;

/**	Work set utilities.
 */

public class WorkSetUtils
{
	/**	Copy UserDataObject array to WorkSet array.
	 *
	 *	@param	udos	Array of user data objects, all actually
	 *					WorkSet objects.
	 *
	 *	@return			Array of WorkSet objects.
	 */

	protected static WorkSet[] udosToWorkSets( UserDataObject[] udos )
	{
		WorkSet[] result;

		if ( udos != null )
		{
			result	= new WorkSet[ udos.length ];

			for ( int i = 0 ; i < udos.length ; i++ )
			{
				result[ i ]	= (WorkSet)udos[ i ];
			}
		}
		else
		{
			result	= new WorkSet[ 0 ];
		}

		return result;
	}

	/**	Add a new work set with specified work parts.
	 *
	 *	@param	title		Title for the new work set.
	 *	@param	description	Description for the new work set.
	 *	@param	webPageURL	Web page URL for the new work set.
	 *	@param	isPublic	True if work set to be public.
	 *	@param	query		CQL query which generates work set.
	 *	@param	workParts	Array of WorkPart entries to add to work set.
	 *
	 *	@return				WorkSet object if work set added, else null.
	 *
	 *	@throws				DuplicateWorkSetException if (title,owner)
	 *						combination already exists.
	 *
	 *	@throws				BadOwnerException if the owner is null or empty.
	 */

	public static WorkSet addWorkSet
	(
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		String query ,
		WorkPart[] workParts
	)
		throws DuplicateWorkSetException, BadOwnerException
	{
								//	Check if logged in.

		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException();
		}
								//	Check if work set of this title already
								//	exists.

		String owner	= WordHoardSettings.getUserID();

		WorkSet workSet	= getWorkSet( title , owner );

								//	Report error if so.

		if ( workSet != null )
		{
			throw new DuplicateWorkSetException();
		}
								//	Find unique work parts.  Will be
								//	null if input work parts array
								//	is null.

		WorkPart[] uniqueWorkParts	=
			WorkUtils.getUniqueWorkParts( workParts );

								//	Create new work set object.
		workSet	=
			new WorkSet
			(
				title ,
				description ,
				webPageURL ,
				owner ,
				isPublic ,
				query ,
				uniqueWorkParts
			);
								//	Persist the work set object.
		Long id	=
			UserDataObjectUtils.createUserDataObject( workSet );

		if ( id.longValue() != -1 )
		{
			workSet	=
				(WorkSet)PersistenceManager.doLoad( WorkSet.class , id );
		}
		else
		{
			workSet	= null;
		}
								//	Return new WorkSet object to caller.
		return workSet;
	}

	/**	Add a new work set with specified work parts.
	 *
	 *	@param	title		Title for the new work set.
	 *	@param	description	Description for the new work set.
	 *	@param	webPageURL	Web page URL for the new work set.
	 *	@param	isPublic	True if work set to be public.
	 *	@param	query		CQL query which generates work set.
	 *	@param	workParts	Collection of WorkPart entries to add to work set.
	 *
	 *	@return				WorkSet object if work set added, else null.
	 *
	 *	@throws				DuplicateWorkSetException if (title,owner)
	 *						combination already exists.
	 *
	 *	@throws				BadOwnerException if the owner is null or empty.
	 */

	public static WorkSet addWorkSet
	(
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		String query ,
		Collection workParts
	)
		throws DuplicateWorkSetException, BadOwnerException
	{
		WorkPart[] workPartsArray	= null;

		if ( workParts != null )
		{
			workPartsArray	=
				(WorkPart[])workParts.toArray( new WorkPart[]{} );
		}

		return addWorkSet
		(
			title ,
			description ,
			webPageURL ,
			isPublic ,
			query ,
			workPartsArray
		);
	}

	/**	Delete a work set.
	 *
	 *	@param	workSet	The work set to delete.
	 *
	 *	@return			true if work set deleted, false otherwise.
	 *
	 *	<p>
	 *	The currently logged in user must be the owner to delete
	 *	a work set.
	 *	</p>
	 */

	public static boolean deleteWorkSet( WorkSet workSet )
	{
		return UserDataObjectUtils.deleteUserDataObject( workSet );
	}

	/**	Delete a work set by title.
	 *
	 *	@param	title	The title of the work set to delete.
	 *
	 *	@return			true if work set deleted, false otherwise.
	 *					If the work set didn't exist, true is returned.
	 */

	public static boolean deleteWorkSet( String title )
	{
		return UserDataObjectUtils.deleteUserDataObject(
			WorkSet.class , title );
	}

	/**	Delete multiple work sets.
	 *
	 *	@param	workSets	The work sets to delete.
	 *
	 *	@return				true if work sets deleted, false otherwise.
	 */

	public static boolean deleteWorkSets
	(
		WorkSet[] workSets
	)
	{
		return UserDataObjectUtils.deleteUserDataObjects( workSets );
	}

	/**	Get a work set by title.
	 *
	 *	@param	owner	The owner for the work set to fetch.
	 *	@param	title	The title of the work set to fetch.
	 *
	 *	@return			The work set with the requested title,
	 *					or null if not found.
	 *
	 *	<p>
	 *	If the owner is not the same as the currently logged in user,
	 *	the work set will only be returned if it is public.
	 *	</p>
	 */

	public static WorkSet getWorkSet( String title , String owner )
	{
		return
			(WorkSet)UserDataObjectUtils.getUserDataObject
			(
				WorkSet.class , title , owner
			);
	}

	/**	Get a work set by title.
	 *
	 *	@param	title	The title of the work set to fetch.
	 *
	 *	@return			The work set with the requested title,
	 *					or null if not found.
	 *
	 *	<p>
	 *	Only public work sets are returned.
	 *	</p>
	 */

	public static WorkSet getWorkSet( String title )
	{
		return
			(WorkSet)UserDataObjectUtils.getUserDataObject(
				WorkSet.class , title );
	}

	/**	Get all available public work sets as an array.
	 *
	 *	@return		All available public work sets as an array of WorkSet.
	 *
	 *	<p>
	 *	If the user is logged in, any private work sets belonging to the
	 *	logged in are also returned.
	 *	</p>
	 */

	public static WorkSet[] getWorkSets()
	{
		return udosToWorkSets
		(
			UserDataObjectUtils.getUserDataObjects( WorkSet.class )
		);
	}

	/**	Get all available work sets for a specified owner as an array.
	 *
	 *	@param		owner	The owner.
	 *
	 *	@return		All available work sets as an array of WorkSet.
	 */

	public static WorkSet[] getWorkSets( String owner )
	{
		return udosToWorkSets
		(
			UserDataObjectUtils.getUserDataObjects
			(
				WorkSet.class ,
				owner ,
				false
			)
		);
	}

	/**	Get work sets for logged in user as an array.
	 *
	 *	@return		All work sets for logged in user as an array of WorkSet.
	 */

	public static WorkSet[] getWorkSetsForLoggedInUser()
	{
		return udosToWorkSets
		(
			UserDataObjectUtils.getUserDataObjectsForLoggedInUser
			(
				WorkSet.class
			)
		);
	}

	/**	Get count of work sets for specified user.
	 *
	 *	@param	owner	The username.
	 *	@return				Count of work sets owned by "owner".
	 */

	public static int getWorkSetsCount( String owner )
	{
		return UserDataObjectUtils.getUserDataObjectsCount
		(
			WorkSet.class ,
			owner ,
			false
		);
	}

	/**	Get count of all available work sets.
	 *
	 *	@return		Count of all available work sets.
	 */

	public static int getWorkSetsCount()
	{
		return UserDataObjectUtils.getUserDataObjectsCount
		(
			WorkSet.class
		);
	}

	/**	Get work parts in a work set as an array.
	 *
	 *	@param	workSet	The work set.
	 *
	 *	@return			All available work parts in the work set
	 *					as an array of WorkPart.
	 *
	 *	<p>
	 *	Returns null if work set is null.
	 *	</p>
	 */

	public static WorkPart[] getWorkParts( WorkSet workSet )
	{
		WorkPart[] result	= null;

		if ( workSet == null ) return result;

		Collection workPartTagsList	= new ArrayList();

		PersistenceManager pm		= null;

		try
		{
			pm	= PMUtils.getPM();

			if ( pm != null )
			{
				if ( !pm.contains( workSet ) )
				{
					workSet	=
						(WorkSet)pm.load( WorkSet.class , workSet.getId() );
            	}

				pm.refresh( workSet );

				workPartTagsList	= workSet.getWorkPartTags();
			}
		}
		catch ( PersistenceException e )
		{
//e.printStackTrace();
		}
		finally
		{
//			PMUtils.closePM( pm );
		}
								//	If we got any work parts, sort them and
								//	return the sorted list as an array
								//	of work parts.

		SortedArrayList sortedWorkParts	=
			new SortedArrayList
			(
				WorkUtils.getWorkPartsByTag( workPartTagsList )
			);

		return (WorkPart[])sortedWorkParts.toArray( new WorkPart[]{} );
	}

	/**	Get works in a work set as an array.
	 *
	 *	@param	workSet	The work set.
	 *
	 *	@return			The works with at least one work part in the
	 *					work set.
	 *
	 *	<p>
	 *	Returns null if work set is null.
	 *	</p>
	 */

	public static Work[] getWorks( WorkSet workSet )
	{
		WorkPart[] workParts		= getWorkParts( workSet );

		Set worksSet	= new HashSet();

		if ( workParts != null )
		{
			for ( int i = 0 ; i < workParts.length ; i++ )
			{
				worksSet.add( workParts[ i ].getWork() );
			}
		}

		int nWorks			= worksSet.size();

		Work[] works		= new Work[ nWorks ];

		Iterator iterator	= worksSet.iterator();
		int i				= 0;

		while ( iterator.hasNext() )
		{
			works[ i++ ]	= (Work)iterator.next();
		}

		return works;
	}

	/**	Get work parts in work set that are works as an array.
	 *
	 *	@param	workSet	The work set.
	 *
	 *	@return			All available work parts that are
	 *					actually works in the work set
	 *					as an array of WorkPart.
	 *
	 *	<p>
	 *	Returns null if work set is null.
	 *	</p>
	 */

	public static Work[] getWorksOnly( WorkSet workSet )
	{
		if ( workSet == null ) return null;

		WorkPart[] workParts	= getWorkParts( workSet );

		ArrayList workList		= new ArrayList();

		for ( int i = 0 ; i < workParts.length ; i++ )
		{
			if ( workParts[ i ].getParent() == null )
			{
				workList.add( workParts[ i ] );
			}
		}

		return (Work[])workList.toArray( new Work[]{} );
	}

	/**	Determine if work set contains only works.
	 *
	 *	@param	workSet	The work set.
	 *
	 *	@return			true if all the work parts in the work set
	 *					are actually works.
	 *
	 *	<p>
	 *	Returns false if work set is null.
	 *	</p>
	 */

	public static boolean containsOnlyWorks( WorkSet workSet )
	{
		if ( workSet == null ) return false;

		boolean result	= true;

		WorkPart[] workParts	= getWorkParts( workSet );

		for ( int i = 0 ; i < workParts.length ; i++ )
		{
			if ( workParts[ i ].getParent() != null )
			{
				result	= false;
				break;
			}
		}

		return result;
	}

	/**	Update a work set.
	 *
	 *	@param	workSet		The work set to update.
	 *	@param	title		Title for the work set.
	 *	@param	description	Description for the work set.
	 *	@param	webPageURL	Web page URL for the work set.
	 *	@param	isPublic	True if work set is public.
	 *	@param	workParts	Array of WorkPart entries for work set.
	 *
	 *	@return				true if update succeed, false otherwise.
	 *
	 *	@throws				BadOwnerException if user is not logged in or
	 *						is not the work set owner .
	 *
	 *	@throws				DuplicateWorkSetException if new (title,owner)
	 *						combination already exists.
	 */

	public static boolean updateWorkSet
	(
		WorkSet workSet ,
		final String title ,
		final String description ,
		final String webPageURL ,
		final boolean isPublic ,
		final WorkPart[] workParts
	)
		throws BadOwnerException, DuplicateWorkSetException
	{
		if	(	UserDataObjectUtils.isDuplicate
				(
					WorkSet.class ,
					title ,
					workSet.getOwner() ,
					workSet.getId()
				)
			)
		{
			throw new DuplicateWorkSetException();
		}

		final String[] workPartTags	= new String[ workParts.length ];

		for ( int i = 0 ; i < workParts.length ; i++ )
		{
			workPartTags[ i ]	= workParts[ i ].getTag();
		}

		return
			UserDataObjectUtils.updateUserDataObject
			(
				workSet ,
				new UserDataObjectUpdater()
				{
					public void update
					(
						UserDataObject userDataObject
					)
					{
						WorkSet workSet	= (WorkSet)userDataObject;

						workSet.setTitle( title );
						workSet.setDescription( description );
						workSet.setWebPageURL( webPageURL );
						workSet.setModificationTime( new Date() );
						workSet.setIsPublic( isPublic );
						workSet.removeWorkParts();
						workSet.addWorkPartTags( workPartTags );
					}
				}
			);
    }

	/**	Update a work set.
	 *
	 *	@param	workSet		The work set to update.
	 *	@param	title		Title for the work set.
	 *	@param	description	Description for the work set.
	 *	@param	webPageURL	Web page URL for the work set.
	 *	@param	isPublic	True if work set is public.
	 *	@param	workParts	Collection of WorkPart entries for work set.
	 *
	 *	@return				true if update succeed, false otherwise.
	 *
	 *	@throws				BadOwnerException if user is not logged in or
	 *						is not the work set owner .
	 *
	 *	@throws				DuplicateWorkSetException if new (title,owner)
	 *						combination already exists.
	 */

	public static boolean updateWorkSet
	(
		WorkSet workSet ,
		String title ,
		String description ,
		String webPageURL ,
		boolean isPublic ,
		Collection workParts
	)
		throws BadOwnerException, DuplicateWorkSetException
	{
		return updateWorkSet
		(
			workSet ,
			title ,
			description ,
			webPageURL ,
			isPublic ,
			(WorkPart[])workParts.toArray( new WorkPart[]{} )
		);
	}

	/**	Import a specified work set from a DOM document.
	 *
	 *	@param	workSetNode		The DOM node which is the root of the
	 *							work set to import.
	 *
	 *	@return					The imported work set, or null if the
	 *							import fails.
	 *
	 *	@throws	BadOwnerException if the user is not logged in.
	 *
	 */

	public static WorkSet importFromDOMDocument
	(
		org.w3c.dom.Node workSetNode
	)
		throws BadOwnerException
	{
		WorkSet result	= null;

           						//	If work set node is null, quit.

		if ( workSetNode == null ) return result;

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
								//	If the word set node is not "workset",
								//	do nothing further.

		if ( !workSetNode.getNodeName().equals( "workset" ) )
		{
			return result;
		}
								//	Create the work set entry.

		result	= new WorkSet( workSetNode , WordHoardSettings.getUserID() );

		return result;
	}

	/**	Import one or more work sets from XML file.
	 *
	 *	@param	importDocument	The DOM document containing the work sets to import.
	 *
	 *	@return					The imported work sets.  May be empty.
	 *
	 *	@throws	BadOwnerException if the user is not logged in.
	 *
	 *	<p>
	 *	Note:	The work sets are not persisted here.  That is the
	 *			responsibility of the caller.
	 *	</p>
	 */

	public static WorkSet[] importWorkSets
	(
		org.w3c.dom.Document importDocument
	)
	{
		WorkSet[] result	= new WorkSet[ 0 ];

								//	Return if document is null.

		if ( importDocument == null ) return result;

								//	Get the root node of the import document.

		org.w3c.dom.Node rootElement	= importDocument.getDocumentElement();

								//	If the root node is not "wordhoard",
								//	this is a bogus document.

		if ( !rootElement.getNodeName().equals( "wordhoard" ) )
		{
			return result;
		}
								//	Get the work set children of the root node.

		ArrayList workSetChildren	=
			DOMUtils.getChildren( rootElement , "workset" );

								//	If there are no work sets just return.

		if	(	( workSetChildren == null ) ||
				( workSetChildren.size() == 0 ) )
		{
			return result;
		}
								//	Holds imported work sets.

		ArrayList workSets	= new ArrayList();

								//	Process each work set.

		for ( int i = 0 ; i < workSetChildren.size() ; i++ )
		{
								//	Get next work set node.

			Node workSetNode	= (Node)workSetChildren.get( i );

								//	Import to work set.

			WorkSet workSet		= importFromDOMDocument( workSetNode );

								//	If imported OK, add to list.

			if ( workSet != null )
			{
				workSets.add( workSet );
			}
		}
								//	Return all imported work sets as array.

		return (WorkSet[])workSets.toArray( new WorkSet[]{} );
	}

	/**	Display dialog for creating/updating a work set.
	 *
	 *	@param	parentWindow	Parent window for dialog.
	 *	@param	workPartGetter	WorkPartGetter to retrieve list of
	 *							work parts to add to work set.
	 *
	 *	@return		The work set.
	 *
	 *	@throws		Exception if something went wrong.
	 */

	public static WorkSet saveWorkSet
	(
		JFrame parentWindow ,
		WorkPartGetter workPartGetter
	)
		throws Exception
	{
		WorkSet result	= null;

								//	Display the work part creation/
								//	editing dialog.

		SaveWorkSetDialog dialog	=
			new SaveWorkSetDialog
			(
				WordHoardSettings.getString
				(
					"SelectWorkSet" ,
					"Select Work Set"
				) ,
				parentWindow
			);

		dialog.show( parentWindow );

								//	If the dialog was not cancelled ...

		if ( !dialog.getCancelled() )
		{
								//	Get the work set settings.

			WorkSet workSet		= dialog.getSelectedItem();
			String title		= dialog.getSetTitle();
			String description	= dialog.getDescription();
			String webPageURL	= dialog.getWebPageURL();
			boolean isPublic	= dialog.getIsPublic();

								//	Get the list of work parts.

			java.util.List workPartList	= workPartGetter.getWorkParts();

								//	Update existing work set if one
								//	was selected in the dialog.

			if ( workSet != null )
			{
				if ( updateWorkSet(
						workSet, title, description, webPageURL,
						isPublic,
						workPartList ) )
				{
					result	= workSet;
				}
			}
			else
			{
								//	Create new work set if existing
								//	work set not selected in the dialog.
				result	=
					addWorkSet(
						title, description, webPageURL, isPublic, "",
						workPartList );
			}

								//	Change parent window's title to
								//	title of work set.

			parentWindow.setTitle( title );
		}

		return result;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected WorkSetUtils()
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

