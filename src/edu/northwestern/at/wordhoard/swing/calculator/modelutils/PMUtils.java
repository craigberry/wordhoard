package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */


import edu.northwestern.at.wordhoard.model.PersistenceManager;
import edu.northwestern.at.wordhoard.model.PersistentObject;
import edu.northwestern.at.wordhoard.swing.WordHoard;
import edu.northwestern.at.wordhoard.model.userdata.*;

/**	Persistence Manager utilities.
 */

public class PMUtils
{
	/**	True to always use global WordHoard persistence manager. */

	protected static boolean useWordHoardPM	= false;

	/**	Get persistence manager.
	 *
	 *	@return		A persistence manager.
	 */

	public static PersistenceManager getPM()
	{
		PersistenceManager pm	= WordHoard.getPm();

		if ( !useWordHoardPM )
		{
			try
			{
				pm	= PersistenceManager.getPM();
			}
			catch ( Exception e )
			{
			}
		}

		return pm;
	}

	/**	Close persistence manager.
	 *
	 *	@param	pm	Persistence manager to close.
	 */

	public static void closePM( PersistenceManager pm )
	{
		if ( ( pm != null ) && ( !pm.equals( WordHoard.getPm() ) ) )
		{
			try
			{
				PersistenceManager.closePM( pm );
			}
			catch ( Exception e )
			{
			}
		}
	}

	/**	Refresh a persistent object.
	 *
	 *	@param	session		Session in which to refresh object.
	 *	@param	obj			Persistent object to refresh.
	 *
	 *	@return				Refreshed persistent object.
	 */

	public static PersistentObject refreshObject
	(
		org.hibernate.Session session ,
		PersistentObject obj
	)
	{
								//	If the session contains the object
								//	already, just refresh it.

		if ( session.contains( obj ) )
		{
			session.refresh( obj );
		}
								//	If the session does not contain
								//	the object, load it and then
								//	refresh it (in case some out-of-date
								//	version gets loaded from the cache).
		else
		{
			try
			{
								//	This ugliness is because
								//	Hibernate won't load a proxy
								//	object by ID.  We need to specify
								//	the actual class.  Presumably that
								//	is a Hibernate bug.

				if ( obj instanceof WorkSet )
				{
					obj	=
						(PersistentObject)session.load(
							WorkSet.class , obj.getId() );
            	}
            	else if ( obj instanceof PhraseSet )
            	{
					obj	=
						(PersistentObject)session.load(
							PhraseSet.class , obj.getId() );
            	}
            	else if ( obj instanceof WordSet )
            	{
					obj	=
						(PersistentObject)session.load(
							WordSet.class , obj.getId() );
            	}

				session.refresh( obj );
			}
			catch ( Exception e )
			{
//				e.printStackTrace();
			}
		}

		return obj;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected PMUtils()
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

