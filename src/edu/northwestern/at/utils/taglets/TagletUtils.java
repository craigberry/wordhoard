package edu.northwestern.at.utils.taglets;

/*	Please see the license information at the end of this file. */

import com.sun.tools.doclets.Taglet;
import java.util.Map;

/**	Utilities for use in writing custom Javadoc taglets.
 */

class TagletUtils
{
	/**	Register a taglet.
	 *
	 *	@param	tagletMap		The map to which to register this taglet.
     *	@param	taglet			The taglet to register.
     *
     *	<p>
     *	Any existing taglet of the same name is replaced in the taglet map.
     *	</p>
     */

	public static void register( Map tagletMap , Taglet taglet )
	{
								//	If either tagletMap or taglet is null,
								//	do nothing.

		if ( ( tagletMap == null ) || ( taglet == null ) ) return;

								//	Get name of taglet to add.

		String tagletName	= taglet.getName();

								//	Do nothing if name is empty.

		if ( ( tagletName == null ) || ( tagletName.length() <= 0 ) ) return;

								//	See if taglet of this name already
								//	exists in the target map.

		Object t	= tagletMap.get( tagletName );

								//	If so, remove it.
		if ( t != null )
		{
			tagletMap.remove( tagletName );
		}
								//	Add taglet to map.

		tagletMap.put( tagletName , taglet );
	}

	/** Don't allow instantiation, but do allow overrides. */

	protected TagletUtils()
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

