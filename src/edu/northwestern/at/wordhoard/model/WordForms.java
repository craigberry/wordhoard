package edu.northwestern.at.wordhoard.model;

/*	Please see the license information at the end of this file. */

/**	Word form constants.
 *
 *	<p>
 *	Defines the types of word occurrence attibutes which WordHoard
 *	can analyze.  At present these include spellings, lemmata,
 *	word class, speaker gender, speaker mortality,
 *	semantic categories, poetry-versus-prose, and metrical shapes.
 *	</p>
 */

public class WordForms
{
	/**	Word forms. */

	public static final int SPELLING			= 0;
	public static final int LEMMA				= 1;
	public static final int WORDCLASS			= 2;
	public static final int SPEAKERGENDER		= 3;
	public static final int SEMANTICCATEGORY	= 4;
	public static final int ISVERSE				= 5;
	public static final int METRICALSHAPE		= 6;
	public static final int SPEAKERMORTALITY	= 7;

	public static final int NUMBEROFWORDFORMS	= 8;

	/**	Can't instantiate but can override. */

	protected WordForms()
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

