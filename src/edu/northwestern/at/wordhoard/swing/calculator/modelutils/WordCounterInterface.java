package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.lang.reflect.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;

/**	Word form counter interface.
 */

public interface WordCounterInterface
{
	/**	Get count of a word form.
	 *
	 *  @param	word			The word form whose count is desired.
	 *	@param	wordForm		The type of word form as specified
	 *							in {@link WordForms}.
	 *
	 *	@return					The count of times the word form appears.
	 */

	public int getWordFormCount( String word , int wordForm );

	/**	Get all word forms and their counts.
	 *
	 *	@param		wordForm	The type of word form as specified
	 *							in {@link WordForms}.
	 *
	 *	@return					Map containing each word form
	 *							as a key and the count of the word form
	 *							as the value.
	 */

	public Map getWordFormsAndCounts( int wordForm );

	/**	Get total word count for word form type.
	 *
	 *	@param	wordForm		The type of word form as specified
	 *							in {@link WordForms}.
	 *
	 *	@return					The total count of the word form type.
	 */

	public int getTotalWordFormCount( int wordForm );

	/**	Get word form and its counts by year.
	 *
	 *  @param	word			The word form whose count is desired.
	 *	@param	wordForm		The type of word form as specified
	 *							in {@link WordForms}.
	 *
	 *	@return					Two maps.  The first has the year as
	 *							as key and the word count as a value.
	 *							The second has the year as a key and
	 *							the work count as a value.
	 */

	public Map[] getWordFormCountByYear( String word , int wordForm );
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

