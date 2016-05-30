package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;

/**	Word class utilities.
 */

public class WordClassUtils
{
	/**	See if specified word class exists.
	 *
	 *	@param	wordClassText			The word class text.
	 *
	 *	@return							true if specified word class exists,
	 *									false otherwise.
	 */

	public static boolean wordClassExists( String wordClassText )
	{
		boolean result	= false;

		java.util.List wordClassCount	=
			PersistenceManager.doQuery
			(
				"select count(*) from WordClass wc " +
				"where wc.tag=:tag" ,
				new String[]{ "tag" } ,
				new Object[]{ wordClassText }
			);

		if ( wordClassCount != null )
		{
			Iterator iterator	= wordClassCount.iterator();
			Long count			= (Long)iterator.next();
			result				= ( count.longValue() > 0 );
		}

		return result;
	}

	/**	Get list of word class values.
	 *
	 *	@return		All word classes as array of Strings.
	 */

	public static String[] getWordClasses()
	{
		java.util.List wordClasses	=
			PersistenceManager.doQuery
			(
				"select distinct wc.tag from WordClass wc" , true
			);

		SortedArrayList wordClassesList	= new SortedArrayList();

		if ( wordClasses != null )
		{
			Iterator iterator	= wordClasses.iterator();

			while ( iterator.hasNext() )
			{
				wordClassesList.add( (String)iterator.next() );
			}
		}

		return (String[])wordClassesList.toArray( new String[]{} );
	}

	/**	Finds word classes matching an initial string of characters.
	 *
	 *	@param		initialString 	The initial word class text string.
	 *
	 *	@return		An array of matching word classes
	 *				whose tags begin with the specified text.
	 *				Null if none.
	 */

	public static String[] getWordClassesByInitialString
	(
		String initialString
	)
	{
		String[] result	= null;

		String lowercaseInitialString	= initialString.toLowerCase() + "%";

		java.util.List wordClassesList	=
			PersistenceManager.doQuery
			(
				"from WordClass wc where wc.tag like :initialString " +
				"order by wc.tag" ,
				new String[]{ "initialString" } ,
				new Object[]{ lowercaseInitialString }
			);

		if ( wordClassesList != null )
		{
			result	= (String[])wordClassesList.toArray( new String[]{} );
		}

		return result;
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

