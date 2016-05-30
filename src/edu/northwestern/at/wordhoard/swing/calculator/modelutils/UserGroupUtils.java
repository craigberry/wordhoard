package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.*;

import edu.northwestern.at.utils.xml.*;

import org.w3c.dom.*;

/**	Work set utilities.
 */

public class UserGroupUtils
{
	/**	Create a new UserGroup.
	 *
	 *	@param	userGroup	The group.
	 *
	 *	@return				UserGroup object if created, else null.
	 *
	 *	@throws				BadOwnerException if the owner is null or empty.
	 */

	public static UserGroup createUserGroup
	(
		UserGroup userGroup
	)
		throws BadOwnerException
	{
		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException();
		}

		Long id	= UserDataObjectUtils.createUserDataObject( userGroup );

		if ( id.longValue() != -1 )
		{
			userGroup = (UserGroup)PersistenceManager.doLoad( UserGroup.class , id );
		}
		else
		{
			userGroup	= null;
		}
								//	Return new UserGroup object to caller.
		return userGroup;
	}


	/**	Update UserGroup.
	 *
	 *	@return				UserGroup object
	 *
	 *	@throws				BadOwnerException if the owner is null or empty or not admin.
	 */

	public static boolean updateUserGroup ( UserGroup userGroup)
		throws BadOwnerException
	{
		final String title = userGroup.getTitle();
		final String owner = userGroup.getOwner();
		final Collection members = userGroup.getMembers();
		
		if ( !WordHoardSettings.isLoggedIn() || (!userGroup.getOwner().equals(WordHoardSettings.getUserID())  && !WordHoardSettings.getCanManageAccounts()))
		{
			throw new BadOwnerException();
		}

		boolean updateStatus = UserDataObjectUtils.updateUserDataObject(userGroup ,
						new UserDataObjectUpdater()
				{
					public void update
					(
						UserDataObject userDataObject
					)
					{
						UserGroup ata = (UserGroup)userDataObject;
						ata.setTitle(title);
						ata.setOwner(owner);
						ata.removeAllMembers();
						ata.addMembers(members);
					}
				}
			);
		return updateStatus;
	}

	/**	Delete a UserGroup.
	 *
	 *	@param	userGroup	The UserGroup to delete.
	 *
	 *	@return			true if work set deleted, false otherwise.
	 *
	 *	<p>
	 *	The currently logged in user must be the owner to delete
	 *	an UserGroup.
	 *	</p>
	 */

	public static boolean deleteUserGroup( UserGroup userGroup )
	{
		if ( !WordHoardSettings.isLoggedIn() || (!userGroup.getOwner().equals(WordHoardSettings.getUserID())  && !WordHoardSettings.getCanManageAccounts()))
		{
			throw new BadOwnerException();
		}
		return UserDataObjectUtils.deleteUserDataObject( userGroup );
	}

	/**	Get all usergroups associated with a specific user.
	 *
	 *	@param		member	The tag of the user group member.
	 *
	 *	@return		All available user groups for the member.
	 */

	public static Collection getUserGroups( String member )
	{
		String queryString	= "from UserGroup where :member in elements(members)";

		java.util.List list	= PersistenceManager.doQuery(queryString,
			new String[]{ "member" } ,
			new Object[]{ member });
		
		SortedArrayList sortedUserDataObjectList = new SortedArrayList( list );
		return sortedUserDataObjectList;
	}

	/**	Get all usergroups.
	 *
	 *	@return		All available user groups.
	 */

	public static Collection getUserGroups()
	{
		String queryString	= "FROM UserGroup";

		java.util.List list	= PersistenceManager.doQuery(queryString, null, null);
		
		SortedArrayList sortedUserDataObjectList = new SortedArrayList( list );
		return sortedUserDataObjectList;
	}
	
	/**	Don't allow instantiation but do allow overrides.
	 */

	protected UserGroupUtils()
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

