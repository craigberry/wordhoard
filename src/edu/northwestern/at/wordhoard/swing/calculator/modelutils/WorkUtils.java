package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import org.hibernate.Query;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;

/**	Work and work part utilities.
 */

public class WorkUtils
{
	/**	Maps work part Ids to work part tags.
	 */

	private static Map workPartIdToWorkPartTag	= new HashMap();

	/**	Maps work part TAGS to work part IDs.
	 */

	private static Map workPartTagToWorkPartId	= new HashMap();

	/**	True if work part maps are created.
	 */

	protected static boolean workPartMapsAvailable	= false;

	/**	Create work part Id to work part tag maps.
	 */

	public static void createWorkPartMaps( Collection workParts )
	{
		for ( 	Iterator iterator = workParts.iterator() ;
				iterator.hasNext() ;
			)
		{
			WorkPart workPart		= (WorkPart)iterator.next();

			workPartIdToWorkPartTag.put(
				workPart.getId() , workPart.getTag() );

			workPartTagToWorkPartId.put(
				workPart.getTag() , workPart.getId() );
		}
								//	Indicate work part maps are available.

		workPartMapsAvailable	= true;
	}

	/**	Get a work by full title.
	 *
	 *	@param	title	The full title of the work to fetch.
	 *
	 *	@return			The work with the requested full title,
	 *					or null if not found.  If more than one
	 *					work has the title, only the first is
	 *					returned.
	 */

	public static Work getWork( String title )
	{
		java.util.List list	=
			PersistenceManager.doQuery
			(
				"from Work work where work.fullTitle = :title" ,
				new String[]{ "title" } ,
				new Object[]{ title }
			);

		return
			( ( list != null ) && ( !list.isEmpty() ) ) ?
				(Work)list.get( 0 ) : null;
	}

	/**	Get a work by short title.
	 *
	 *	@param	title	The short title of the work to fetch.
	 *
	 *	@return			The work with the requested short title,
	 *					or null if not found.  If more than one
	 *					work has the title, only the first is
	 *					returned.
	 */

	public static Work getWorkByShortTitle( String title )
	{
		java.util.List list	=
			PersistenceManager.doQuery
			(
				"from Work work where work.shortTitle = :title" ,
				new String[]{ "title" } ,
				new Object[]{ title }
			);

		return
			( ( list != null ) && ( !list.isEmpty() ) ) ?
				(Work)list.get( 0 ) : null;
	}

	/**	Get a work by tag.
	 *
	 *	@param	tag		The tag of the work to fetch.
	 *
	 *	@return			The work with the requested tag,
	 *					or null if not found.
	 */

	public static Work getWorkByTag( String tag )
	{
		java.util.List list	=
			PersistenceManager.doQuery
			(
				"from Work work where work.tag = :tag" ,
				new String[]{ "tag" } ,
				new Object[]{ tag }
			);

		return
			( ( list != null ) && ( !list.isEmpty() ) ) ?
				(Work)list.get( 0 ) : null;
	}

	/**	Get a work ID by tag.
	 *
	 *	@param	tag		The tag of the work to fetch.
	 *
	 *	@return			The work ID of the work with the requested tag.
	 *					Null if not found.
	 */

	public static Long getWorkIDByTag( String tag )
	{
		Long result	= null;

		if ( !workPartMapsAvailable )
		{
			java.util.List list	=
				PersistenceManager.doQuery
				(
					"from Work work where work.tag = :tag" ,
					new String[]{ "tag" } ,
					new Object[]{ tag }
				);

			Work work	=
				( ( list != null ) && ( !list.isEmpty() ) ) ?
					(Work)list.get( 0 ) : null;

			if ( work != null ) result	= work.getId();
        }
        else
        {
			result	= (Long)workPartTagToWorkPartId.get( tag );
		}

		return result;
	}

	/**	Gets a work part by tag.
	 *
	 *	@param	tag			The tag of the work part to fetch.
	 *
	 *	@return				The work part with the specified tag,
	 *						or null if not found.
	 */

	public static WorkPart getWorkPartByTag( String tag )
	{
		java.util.List list	=
			PersistenceManager.doQuery
			(
				"from WorkPart workPart where workPart.tag = :tag" ,
				new String[]{ "tag" } ,
				new Object[]{ tag }
			);

		return
			( ( list != null ) && ( !list.isEmpty() ) ) ?
				(WorkPart)list.get( 0 ) : null;
	}

	/**	Get a work by Id.
	 *
	 *	@param	workId	The Id of the work to fetch.
	 *
	 *	@return			The work with the requested workId,
	 *					or null if not found.
	 */

	public static Work getWorkById( Long workId )
	{
		java.util.List list	=
			PersistenceManager.doQuery
			(
				"from Work work where work.id = :workId" ,
				new String[]{ "workId" } ,
				new Object[]{ workId }
			);

		return
			( ( list != null ) && ( !list.isEmpty() ) ) ?
				(Work)list.get( 0 ) : null;
	}

	/**	Get array of all available works.
	 *
	 *	@return		Array of Work for all available works.
	 */

	public static Work[] getWorks()
	{
		SortedArrayList workList	= new SortedArrayList();

		java.util.List works		=
			PersistenceManager.doQuery( "from Work" , true );

		if ( works != null )
		{
			for ( Iterator it = works.iterator() ; it.hasNext(); )
			{
				workList.add( it.next() );
			}
		}

		return (Work[])workList.toArray( new Work[]{} );
	}

	/**	Get array of all work parts for a work.
	 *
	 *	@param		work	The work.
	 *
	 *	@return				Array of WorkPart for all work parts in work.
	 *
	 *	<p>
	 *	The returned array does not include the work part entry for the
	 *	work itself.
	 *	</p>
	 */

	public static WorkPart[] getWorkParts( Work work )
	{
		ArrayList workPartsList	= new ArrayList();

		java.util.List workParts		=
			PersistenceManager.doQuery
			(
				"from WorkPart wp where wp.work=:work" ,
				new String[]{ "work" } ,
				new Object[]{ work }
			);

		if ( workParts != null )
		{
			for ( Iterator it = workParts.iterator() ; it.hasNext(); )
			{
				workPartsList.add( it.next() );
			}
		}

		return (WorkPart[])workPartsList.toArray( new WorkPart[]{} );
	}

	/**	Get array of work parts for a list of work part IDs.
	 *
	 *	@param		workPartIds		The work part Ids.
	 *
	 *	@return						Array of WorkPart for the work parts.
	 */

	public static WorkPart[] getWorkPartsById( Long[] workPartIds )
	{
		java.util.List workParts		=
			PersistenceManager.doQuery
			(
				"from WorkPart wp where wp.id in (:workPartIds)" ,
				new String[]{ "workPartIds" } ,
				new Object[]
				{
					Arrays.asList( workPartIds )
				}
			);

		WorkPart[] result	= new WorkPart[ 0 ];

		if ( workParts != null )
		{
			result	= (WorkPart[])workParts.toArray( new WorkPart[]{} );
		}

		return result;
	}

	/**	Get array of work tags for a list of work part IDs.
	 *
	 *	@param		workIds		The work Ids.
	 *
	 *	@return					Array of work tags as strings .
	 */

	public static String[] getWorkTagsById( Collection workIds )
	{
		String[] result;

		if ( !workPartMapsAvailable )
		{
			java.util.List workTags	=
				PersistenceManager.doQuery
				(
					"select wp.tag from WorkPart wp " +
					" where wp.id in (:workIds)" ,
					new String[]{ "workIds" } ,
					new Object[]{ workIds }
				);

			result	= new String[ 0 ];

			if ( workTags != null )
			{
				result	= (String[])workTags.toArray( new String[]{} );
			}
		}
		else
		{
			Long[] workIdsArray	= (Long[])workIds.toArray( new Long[]{} );

			result				= new String[ workIdsArray.length ];

			for ( int i = 0 ; i < workIdsArray.length ; i++ )
			{
				result[ i ]	=
					(String)workPartIdToWorkPartTag.get( workIdsArray[ i ] );
        	}
        }

       	return result;
	}

	/**	Get array of work part tags for a list of work part IDs.
	 *
	 *	@param		workPartIds		The work part Ids.
	 *
	 *	@return						Array of work part tags as strings .
	 */

	public static String[] getWorkPartTagsById( Collection workPartIds )
	{
		java.util.List workPartTags	=
			PersistenceManager.doQuery
			(
				"select wp.tag from WorkPart wp " +
				" where wp.id in (:workPartIds)" ,
				new String[]{ "workPartIds" } ,
				new Object[]{ workPartIds }
			);

		String[] result	= new String[ 0 ];

		if ( workPartTags != null )
		{
			result	= (String[])workPartTags.toArray( new String[]{} );
		}

		return result;
	}

	/**	Get array of work parts for a list of work part tags.
	 *
	 *	@param		workPartTags	The work part tags.
	 *	@param		session			Persistence manager session.
	 *
	 *	@return						Array of WorkPart for the work parts.
	 */

	public static WorkPart[] getWorkPartsByTag
	(
		Collection workPartTags ,
		org.hibernate.Session session
	)
	{
		WorkPart[] workParts	= new WorkPart[ 0 ];

		try
		{
			Query query	=
				session.createQuery
				(
					"select workpart " +
					"from WorkPart workpart " +
					"where workpart.tag in (:tags)"
				);

			query.setParameterList( "tags" , workPartTags );

			Collection qList = query.list();

			if ( qList != null )
			{
				workParts	=
					(WorkPart[])qList.toArray( new WorkPart[]{} );
			}
		}
		catch ( Exception e )
		{
		}

		return workParts;
	}

	/**	Get array of work parts for a list of work part tags.
	 *
	 *	@param		workPartTags	The work part tags.
	 *
	 *	@return						Array of WorkPart for the work parts.
	 */

	public static WorkPart[] getWorkPartsByTag
	(
		Collection workPartTags
	)
	{
		java.util.List workParts		=
			PersistenceManager.doQuery
			(
				"from WorkPart wp where wp.tag in (:workPartTags)" ,
				new String[]{ "workPartTags" } ,
				new Object[]{ workPartTags }
			);

		WorkPart[] result	= new WorkPart[ 0 ];

		if ( workParts != null )
		{
			result	= (WorkPart[])workParts.toArray( new WorkPart[]{} );
		}

		return result;
	}

	/**	Get array of works for a list of work tags.
	 *
	 *	@param		workTags	The work tags.
	 *
	 *	@return					Array of Work for the works.
	 */

	public static Work[] getWorksByTag
	(
		Collection workTags
	)
	{
		java.util.List works		=
			PersistenceManager.doQuery
			(
				"from Work w where w.tag in (:workTags)" ,
				new String[]{ "workTags" } ,
				new Object[]{ workTags }
			);

		Work[] result	= new Work[ 0 ];

		if ( works != null )
		{
			result	= (Work[])works.toArray( new Work[]{} );
		}

		return result;
	}

	/**	Get work part tag for a work part ID.
	 *
	 *	@param		workPartId		The work part Id.
	 *
	 *	@return						Work part tag as string .
	 */

	public static String getWorkPartTagById( Long workPartId )
	{
		String result	= "";

		if ( !workPartMapsAvailable )
		{
			java.util.List workPartTag	=
				PersistenceManager.doQuery
				(
					"select wp.tag from WorkPart wp " +
					" where wp.id=:workPartId" ,
					new String[]{ "workPartId" } ,
					new Object[]{ workPartId }
				);

			if ( workPartTag != null )
			{
				Iterator iterator	= workPartTag.iterator();

				if ( iterator.hasNext() )
				{
					result	= (String)iterator.next();
				}
			}
		}
		else
		{
			result	= (String)workPartIdToWorkPartTag.get( workPartId );
		}

		return result;
	}

	/**	Get array of work parts for a list of work part tags.
	 *
	 *	@param		workPartTags	The work part tags.
	 *
	 *	@return						Array of WorkPart for the work parts.
	 */

	public static WorkPart[] getWorkPartsByTag
	(
		String[] workPartTags
	)
	{
		java.util.List workParts		=
			PersistenceManager.doQuery
			(
				"from WorkPart wp where wp.tag in (:workPartTags)" ,
				new String[]{ "workPartTags" } ,
				new Object[]
				{
					Arrays.asList( workPartTags )
				}
			);

		WorkPart[] result	= new WorkPart[ 0 ];

		if ( workParts != null )
		{
			result	= (WorkPart[])workParts.toArray( new WorkPart[]{} );
		}

		return result;
	}

	/**	Get array of all work parts for all works.
	 *
	 *	@return				Array of WorkPart for all work parts.
	 */

	public static WorkPart[] getWorkParts()
	{
		ArrayList workPartsList		= new ArrayList();

		java.util.List workParts	=
			PersistenceManager.doQuery( "from WorkPart" , true );

		if ( workParts != null )
		{
			for ( Iterator it = workParts.iterator() ; it.hasNext(); )
			{
				workPartsList.add( it.next() );
			}
		}

		return (WorkPart[])workPartsList.toArray( new WorkPart[]{} );
	}

	/**	Get list of unique work parts in a map of work parts.
	 *
	 *	@param		workPartsMap	Map with work part ID of work parts.
	 *
	 *	@return		Array of WorkPart eliminating duplicate
	 *				work parts and parts which are descendants of
	 *				other parts.
	 */

	public static WorkPart[] getUniqueWorkParts( Map workPartsMap )
	{
								//	Return null if work parts map is null.

		if ( workPartsMap == null ) return null;

								//	Go through map of work parts
								//	and find those which have
								//	no ancestors in the list.  Those are
								//	the unique work parts we want.

		ArrayList uniqueWorkParts	= new ArrayList();

		for	(	Iterator iterator = workPartsMap.keySet().iterator() ;
				iterator.hasNext() ; )
		{
			Long workPartId			= (Long)iterator.next();

			WorkPart workPart		=
				(WorkPart)workPartsMap.get( workPartId );

			WorkPart[] ancestors	=
				(WorkPart[])getAncestors( workPart ).toArray(
					new WorkPart[]{} );

			boolean foundAncestor	= false;

			for ( int i = 0 ; i < ancestors.length ; i++ )
			{
				if ( workPartsMap.containsKey(
					ancestors[ i ].getId() ) )
				{
					foundAncestor	= true;
					break;
				}
			}

			if ( !foundAncestor ) uniqueWorkParts.add( workPart );
		}

		return (WorkPart[])uniqueWorkParts.toArray( new WorkPart[]{} );
	}

	/**	Get list of unique work parts in an array of work parts.
	 *
	 *	@param		workParts	Array of work parts.
	 *
	 *	@return		Array of WorkPart eliminating duplicate
	 *				work parts and parts which are descendants of
	 *				other parts.
	 */

	public static WorkPart[] getUniqueWorkParts( WorkPart[] workParts )
	{
								//	Return null if work parts array
								//	is null.

		if ( workParts == null ) return null;

								//	Use a hash map to hold key=work part ID
								//	and value=work part.

		HashMap workPartsMap	= new HashMap();

								//	Add work parts to hash map.

		for ( int i = 0 ; i < workParts.length ; i++ )
		{
			workPartsMap.put( workParts[ i ].getId() , workParts[ i ] );
		}
								//	Get unique work parts from map.

		return getUniqueWorkParts( workPartsMap );
	}

	/**	Get list of unique work parts in a listr of work parts.
	 *
	 *	@param		workParts	List of work parts.
	 *
	 *	@return		Array of WorkPart eliminating duplicate
	 *				work parts and parts which are descendants of
	 *				other parts.
	 */

	public static WorkPart[] getUniqueWorkParts( java.util.List workParts )
	{
								//	Use a hash map to hold key=work part ID
								//	and value=work part.

		HashMap workPartsMap	= new HashMap();

								//	Add work parts to hash map.

		for	 (	Iterator iterator = workParts.iterator() ;
				iterator.hasNext() ; )
		{
			WorkPart workPart	= (WorkPart)iterator.next();

			workPartsMap.put( workPart.getId() , workPart );
		}
								//	Get unique work parts from map.

		return getUniqueWorkParts( workPartsMap );
	}

	/**	Get list of unique work parts from two work part arrays.
	 *
	 *	@param		workParts1	First array of work parts.
	 *	@param		workParts2	Second array of work parts.
	 *
	 *	@return		Array of WorkPart for all work parts in
	 *				workParts1 and workParts2 eliminating duplicate
	 *				work parts.
	 */

	public static WorkPart[] getUniqueWorkParts
	(
		WorkPart[] workParts1 ,
		WorkPart[] workParts2
	)
	{
								//	Use a hash set to hold key=workId
								//	and value=Work.

		HashMap uniqueWorkPartsMap	= new HashMap();

								//	Add work parts from workParts1
								//	to the hash set.

		for ( int i = 0 ; i < workParts1.length ; i++ )
		{
			uniqueWorkPartsMap.put
			(
				workParts1[ i ].getId() ,
				workParts1[ i ]
			);
		}
								//	Add works from workParts2 which are not
								//	already in the hash set.

		for ( int i = 0 ; i < workParts2.length ; i++ )
		{
			Long id	= workParts2[ i ].getId();

			if ( !uniqueWorkPartsMap.containsKey( id ) )
			{
				uniqueWorkPartsMap.put
				(
					id ,
					workParts2[ i ]
				);
			}
		}
								//	Now hash set contains the unique
								//	work parts from both arrays.
								//
								//	Return these as an array of WorkPart objects.

		WorkPart[] result	= new WorkPart[ uniqueWorkPartsMap.size() ];

		int i			= 0;

		for	(	Iterator iterator = uniqueWorkPartsMap.keySet().iterator() ;
				iterator.hasNext() ; )
		{
			Long workId		= (Long)iterator.next();
			result[ i++ ]	= (WorkPart)uniqueWorkPartsMap.get( workId );
		}

		return result;
	}

	/**	Get ancestor work parts.
	 *
	 *	@return			Collection of ancestor work parts.
	 *					Empty (not null) if work part is a work.
	 */

	public static Collection getAncestors( WorkPart workPart )
	{
		ArrayList ancestors	= new ArrayList();

		WorkPart ancestor = workPart.getParent();

		while ( ancestor != null )
		{
			ancestors.add( ancestor );
			ancestor	= ancestor.getParent();
		}

		return ancestors;
	}

	/**	Convert mixed list of works and work parts to lowest level work parts.
	 *
	 *	@param	workParts		The mixed works and work parts.
	 *
	 *	@return					The lowest level work parts as a collection.
	 *							Only those containing text are returned.
	 */

	public static Collection expandWorkParts
	(
		WorkPart[] workParts
	)
	{
		ArrayList workPartsList	= new ArrayList();

		for	( int i = 0 ; i < workParts.length ; i++ )
		{
			workParts[ i ].appendDescendantsWithText( workPartsList );
		}

		HashSet result	= new HashSet();

		for	(	Iterator iterator	= workPartsList.iterator() ;
				iterator.hasNext() ;
			)
		{
			result.add( iterator.next() );
		}

		return result;
	}

	/**	Convert mixed list of works and work parts to lowest level work parts.
	 *
	 *	@param	workParts		The mixed works and work parts.
	 *
	 *	@return					The lowest level work parts as an array.
	 *							Only those containing text are returned.
	 */

	public static WorkPart[] expandWorkPartsToArray
	(
		WorkPart[] workParts
	)
	{
		ArrayList workPartsList	= new ArrayList();

		for	( int i = 0 ; i < workParts.length ; i++ )
		{
			workParts[ i ].appendDescendantsWithText( workPartsList );
		}

		HashSet result	= new HashSet();

		for	(	Iterator iterator	= workPartsList.iterator() ;
				iterator.hasNext() ;
			)
		{
			result.add( iterator.next() );
		}

		return (WorkPart[])result.toArray( new WorkPart[ result.size() ] );
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected WorkUtils()
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


