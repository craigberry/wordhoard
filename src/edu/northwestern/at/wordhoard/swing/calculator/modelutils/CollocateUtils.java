package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.model.*;

/**	Collocate utilities.
 */

public class CollocateUtils
{
	/**	Gets colocates for a collection of words.
	 *
	 *	@param	pm			The persistence manager in which to load
	 *						the collocates.  May be null.
	 *
	 *	@param	words		Collection of word occurrences.
	 *
	 *	@param	distance	Max distance.
	 *
	 *	@return		A collection of all the
	 *				{@link edu.northwestern.at.wordhoard.model.Word
	 *				word occurrences} which are within "distance" words
	 *				of one of the specified words.  May be empty.
	 *				Null if the collocates could not be found because
	 *				of a databae problem.
	 */

	public static Collection getColocates
	(
		PersistenceManager pm ,
		Collection words ,
		int distance
	)
	{
		return getColocates( pm , words , distance , distance );
	}

	/**	Gets colocates for a collection of words.
	 *
	 *	@param	pm			The persistence manager in which to load
	 *						the collocates.  May be null.
	 *	@param	words		Collection of word occurrences.
	 *	@param	leftSpan	Max distance to the left.
	 *	@param	rightSpan	Max distance to the right.
	 *
	 *	@return		A collection of all the
	 *				{@link edu.northwestern.at.wordhoard.model.Word
	 *				word occurrences} which are within "leftSpan" to
	 *				"rightSpan" distance of one of the specified words.
	 */

	public static Collection getColocates
	(
		PersistenceManager pm ,
		Collection words ,
		int leftSpan ,
		int rightSpan
	)
	{
		java.util.List result	= new ArrayList();

		if ( ( words != null ) && ( words.size() > 0 ) )
		{
			try
			{
				PersistenceManager thePM	= pm;

				if ( thePM == null ) thePM	= PMUtils.getPM();

				result	=
					thePM.query
					(
						"select colocate from Word colocate, " +
						"Word word where " +
						"word in (:words) and " +
//						PersistenceManager.generateInPhrase(
//							"word" , words ) + " and " +
						"colocate.colocationOrdinal between " +
						"word.colocationOrdinal - :leftSpan and " +
						"word.colocationOrdinal + :rightSpan" ,
						new String[]
						{
							"words" ,
							"leftSpan" ,
							"rightSpan"
						},
						new Object[]
						{
							words ,
							new Integer( leftSpan ) ,
							new Integer( rightSpan )
						}
					);
			}
			catch ( Exception e )
			{
				result	= null;
			}
		}

		return result;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected CollocateUtils()
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

