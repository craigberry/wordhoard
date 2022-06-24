package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

/**	Query criterion.
 */

public class QueryCriterion
{
	/**	The select phrase. */

	public String selectPhrase;

	/**	The from phrase. */

	public String fromPhrase;

	/**	The where phrase. */

	public String wherePhrase;

	/**	The where value. */

	public Object whereValue;

	/**	Create query criterion object.
	 *
	 *	@param	selectPhrase	The select phrase.
	 *	@param	fromPhrase		The from phrase.
	 *	@param	wherePhrase		The where phrase.
	 *	@param	whereValue		The where value.
	 */

	public QueryCriterion
	(
		String selectPhrase ,
		String fromPhrase ,
		String wherePhrase ,
		Object whereValue
	)
	{
		this.selectPhrase	= selectPhrase;
		this.fromPhrase		= fromPhrase;
		this.wherePhrase	= wherePhrase;
		this.whereValue		= whereValue;
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

