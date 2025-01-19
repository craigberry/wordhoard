package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.annotations.*;
import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.*;

/**	Work set utilities.
 */

public class UserGroupPermissionUtils
{
	/**	Create a new UserGroupPermission.
	 *
	 *	@param	userGroupPermission		The user group permission.
	 *
	 *	@return				UserGroupPermission object if created, else null.
	 *
	 *	@throws				BadOwnerException if the owner is null or empty.
	 */

	public static UserGroupPermission createUserGroupPermission
	(
		UserGroupPermission userGroupPermission
	)
		throws BadOwnerException
	{
		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException();
		}

		Long id	= UserDataObjectUtils.createUserDataObject( userGroupPermission );

		if ( id.longValue() != -1 )
		{
			userGroupPermission = (UserGroupPermission)PersistenceManager.doLoad( UserGroupPermission.class , id );
		}
		else
		{
			userGroupPermission	= null;
		}
								//	Return new UserGroupPermission object to caller.
		return userGroupPermission;
	}


	/**	Update UserGroupPermission.
	 *
	 *	@param	userGroupPermission	The UserGroupPermission object to update.
	 *	@param	permission	The permission to set.
	 *	@return				UserGroupPermission object
	 *
	 *	@throws				BadOwnerException if the owner is null or empty or not admin.
	 */

	public static boolean updateUserGroupPermission ( UserGroupPermission userGroupPermission, final String permission)
		throws BadOwnerException
	{
		if ( !WordHoardSettings.isLoggedIn() || (!userGroupPermission.getOwner().equals(WordHoardSettings.getUserID())  && !WordHoardSettings.getCanManageAccounts()))
		{
			throw new BadOwnerException();
		}

		boolean updateStatus = UserDataObjectUtils.updateUserDataObject(userGroupPermission ,
						new UserDataObjectUpdater()
				{
					public void update
					(
						UserDataObject userDataObject
					)
					{
						UserGroupPermission ata = (UserGroupPermission)userDataObject;
						ata.setPermission(permission);
					}
				}
			);
		return updateStatus;
	}

	/**	Delete a UserGroupPermission.
	 *
	 *	@param	userGroupPermission	The UserGroupPermission to delete.
	 *
	 *	@return			true if work set deleted, false otherwise.
	 *
	 *	<p>
	 *	The currently logged in user must be the owner to delete
	 *	an UserGroupPermission.
	 *	</p>
	 */

	public static boolean deleteUserGroupPermission( UserGroupPermission userGroupPermission )
	{
		if(userGroupPermission.getId() != null) { // is persisted?
			if ( !WordHoardSettings.isLoggedIn() || (!userGroupPermission.getAuthoredTextAnnotation().getOwner().equals(WordHoardSettings.getUserID())  && !WordHoardSettings.getCanManageAccounts()))
			{
				throw new BadOwnerException();
			}
			return UserDataObjectUtils.deleteUserDataObject( userGroupPermission );
		} else {return false;}
	}

	/**	Get all usergroups associated with a specific annotation.
	 *
	 *	@param		annotation	The annotation.
	 *
	 *	@return		All available user groups for the annotation.
	 */

	public static Map getPermissionsForItem( AuthoredTextAnnotation annotation )
	{
		HashMap hm = new HashMap();
	
		if(annotation.getId() != null) { // is persisted?
			String queryString	= "from UserGroupPermission where authoredTextAnnotation=:annotation";

			java.util.List list	= PersistenceManager.doQuery(queryString,
				new String[]{ "annotation"} ,
					new Object[]{ annotation });
					
			if(list != null) {
				Iterator iter = list.iterator();
				while (iter.hasNext()) {
						UserGroupPermission ugp = (UserGroupPermission)iter.next();
						hm.put(ugp.getUserGroup(), ugp);
				}
			}
		}
		return hm;
	}

	/**	Add read permission for group to indicated item.
	 *
	 *	@param		annotation	The AuthoredTextAnnotation made readable
	 *
	 *	@param		userGroup	The UserGroup allowed to read the item
	 *
	 */

	public static void addReadPermission(AuthoredTextAnnotation annotation, UserGroup userGroup)
	{
		// See if we already have this permission
		if((annotation.getId() != null) && (userGroup.getId() != null)) { // is persisted?
			java.util.List list	=
				PersistenceManager.doQuery
				(
					"from UserGroupPermission where authoredTextAnnotation=:annotation AND userGroup=:userGroup AND permission='read'" ,
					new String[]{ "annotation", "userGroup" } ,
					new Object[]{ annotation, userGroup }
				);

			if( ( list == null ) || list.isEmpty()) { createUserGroupPermission(new UserGroupPermission(annotation,userGroup,"read",WordHoardSettings.getUserID()));}
		}
		return;
	}
	

	/**	set permission for group on indicated item.
	 *
	 *	@param		annotation	The AuthoredTextAnnotation
	 *
	 *	@param		userGroup	The UserGroup allowed given permission
	 *
	 *	@param		permission	The permission 
	 *
	 */

	public static void setPermission(AuthoredTextAnnotation annotation, UserGroup userGroup, String permission)
	{
	
		if((annotation.getId() != null) && (userGroup.getId() != null)) { // is persisted?
			if(permission.equals("None")) {deletePermission(annotation,userGroup);}
			else {
				// See if we already have this permission
				java.util.List list	=
					PersistenceManager.doQuery
					(
						"from UserGroupPermission where authoredTextAnnotation=:annotation AND userGroup=:userGroup" ,
						new String[]{ "annotation", "userGroup" } ,
						new Object[]{ annotation, userGroup }
					);
				if( ( list == null ) || list.isEmpty()) { createUserGroupPermission(new UserGroupPermission(annotation,userGroup,permission, WordHoardSettings.getUserID())); }
				else {
					Iterator iter = list.iterator();
					while (iter.hasNext()) {
							UserGroupPermission ugp = (UserGroupPermission)iter.next();
							ugp.setPermission(permission);
							updateUserGroupPermission (ugp, permission);
					}
				}
			}
		}
		return;
	}

	/**	delete permission for group on indicated item.
	 *
	 *	@param		annotation	The AuthoredTextAnnotation
	 *
	 *	@param		userGroup	The UserGroup allowed given permission
	 *
	 */

	public static void deletePermission(AuthoredTextAnnotation annotation, UserGroup userGroup)
	{
		// See if we already have this permission
		if((annotation.getId() != null) && (userGroup.getId() != null)) { // is persisted?
			java.util.List list	=
				PersistenceManager.doQuery
				(
					"from UserGroupPermission where authoredTextAnnotation=:annotation AND userGroup=:userGroup" ,
					new String[]{ "annotation", "userGroup" } ,
					new Object[]{ annotation, userGroup }
				);

			Iterator iter = list.iterator();
			while (iter.hasNext()) {
					UserGroupPermission ugp = (UserGroupPermission)iter.next();
					deleteUserGroupPermission(ugp);
			}
		}
		return;
	}
	
	
	/**	Don't allow instantiation but do allow overrides.
	 */

	protected UserGroupPermissionUtils()
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

