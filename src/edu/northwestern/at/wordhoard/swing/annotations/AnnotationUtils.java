package edu.northwestern.at.wordhoard.swing.annotations;

/*	Please see the license information at the end of this file. */

import java.util.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.annotations.*;
import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.*;

/**	Work set utilities.
 */

public class AnnotationUtils
{
	/**	Create a new annotation.
	 *
	 *	@param	annot		The annotation.
	 *
	 *	@return				AuthoredTextAnnotation object if created, else null.
	 *
	 *	@throws				BadOwnerException if the owner is null or empty.
	 */

	public static AuthoredTextAnnotation createAnnotation
	(
		AuthoredTextAnnotation annot
	)
		throws BadOwnerException
	{
		if ( !WordHoardSettings.isLoggedIn() )
		{
			throw new BadOwnerException();
		}

		Long id	= UserDataObjectUtils.createUserDataObject( annot );

		if ( id.longValue() != -1 )
		{
			annot = (AuthoredTextAnnotation)PersistenceManager.doLoad( AuthoredTextAnnotation.class , id );
		}
		else
		{
			annot	= null;
		}
								//	Return new AuthoredTextAnnotation object to caller.
		return annot;
	}


	/**	Update annotation.
	 *
	 *	@param	annot		The annotation.

	 *	@param	text		The string to which annotation contents will be updated.

	 *	@return				Annotation object
	 *
	 *	@throws				BadOwnerException if the owner is null or empty or not admin.
	 */

	public static boolean updateAnnotation ( AuthoredTextAnnotation annot, final String text)
		throws BadOwnerException
	{
		if ( !WordHoardSettings.isLoggedIn() || (!annot.getOwner().equals(WordHoardSettings.getUserID())  && !WordHoardSettings.getCanManageAccounts()))
		{
			throw new BadOwnerException();
		}

		boolean updateStatus = UserDataObjectUtils.updateUserDataObject(annot ,
						new UserDataObjectUpdater()
				{
					public void update
					(
						UserDataObject userDataObject
					)
					{
						AuthoredTextAnnotation ata = (AuthoredTextAnnotation)userDataObject;
						ata.resetText(text);
					}
				}
			);
		return updateStatus;
	}

	/**	Delete a annotation.
	 *
	 *	@param	annotation	The annotation to delete.
	 *
	 *	@return			true if work set deleted, false otherwise.
	 *
	 *	<p>
	 *	The currently logged in user must be the owner to delete
	 *	an annotation.
	 *	</p>
	 */

	public static boolean deleteAnnotation( AuthoredTextAnnotation annotation )
	{
		if ( !WordHoardSettings.isLoggedIn() || (!annotation.getOwner().equals(WordHoardSettings.getUserID())  && !WordHoardSettings.getCanManageAccounts()))
		{
			throw new BadOwnerException();
		}
		return UserDataObjectUtils.deleteUserDataObject( annotation );
	}

	/**	Get all annotations associated with a specific item if public or user logged in.
	 *
	 *	@param		annotates	The tag of the annotated item.
	 *
	 *	@return		All available annotations for the item.
	 */

	public static Collection getAnnotations( String annotates )
	{	
		String queryString;
		java.util.List list;
		
		if(!WordHoardSettings.isLoggedIn()) {
			queryString = "FROM AuthoredTextAnnotation udo WHERE udo.annotates=:annotates AND udo.isPublic=1";
			list = PersistenceManager.doQuery(queryString,
				new String[]{"annotates"},
				new Object[]{annotates});
		} else if(WordHoardSettings.getCanManageAccounts()) { // Allow account managers to see all annotations
			queryString = "FROM AuthoredTextAnnotation udo WHERE udo.annotates=:annotates";
			list = PersistenceManager.doQuery(queryString,
				new String[]{"annotates"},
				new Object[]{annotates});
		} else {
			queryString = "FROM AuthoredTextAnnotation udo WHERE udo.annotates=:annotates AND (udo.isPublic=1 OR udo.owner=:owner OR exists (FROM UserGroupPermission ugp WHERE ugp.authoredTextAnnotation = udo AND :currentUser in elements(ugp.userGroup.members))) ";
			list = PersistenceManager.doQuery(queryString,
				new String[]{"annotates", "owner", "currentUser"},
				new Object[]{annotates, WordHoardSettings.getUserID(), WordHoardSettings.getUserID()});
		}
			
		if(list==null || list.isEmpty()) {
			return list;
		} else {
			SortedArrayList sortedUserDataObjectList = new SortedArrayList( list );
			return sortedUserDataObjectList;
		}
	}
	

	/**	Get lemma annotations for words found in a workpart if public or user logged in.
	 *
	 *	@param		annotates	The tag of the annotated item.
	 *
	 *	@return		All available annotations for the item.
	 */
/*
	public static Collection getLemmaAnnotations( String annotates )
	{
		String queryString	= "from AuthoredTextAnnotation udo WHERE udo.annotates=:annotates";
		if(!WordHoardSettings.isLoggedIn())
			queryString += " AND udo.isPublic=1";

		java.util.List list	= PersistenceManager.doQuery(queryString,
			new String[]{"annotates"},
			new Object[]{annotates});
		SortedArrayList sortedUserDataObjectList = new SortedArrayList( list );
		return sortedUserDataObjectList;
	}
*/
	/**	Copy UserDataObject array to AuthoredTextAnnotation array.
	 *
	 *	@param	udos	Array of user data objects, all actually
	 *					AuthoredTextAnnotation objects.
	 *
	 *	@return			Array of AuthoredTextAnnotation objects.
	 */

/*
	protected static AuthoredTextAnnotation[] udosToAnnotations( UserDataObject[] udos )
	{
		AuthoredTextAnnotation[] result;

		if ( udos != null )
		{
			result	= new AuthoredTextAnnotation[ udos.length ];

			for ( int i = 0 ; i < udos.length ; i++ )
			{
				result[ i ]	= (AuthoredTextAnnotation)udos[ i ];
			}
		}
		else
		{
			result	= new AuthoredTextAnnotation[ 0 ];
		}

		return result;
	}
*/

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected AnnotationUtils()
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

