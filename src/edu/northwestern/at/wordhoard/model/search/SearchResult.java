package edu.northwestern.at.wordhoard.model.search;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;

/**	A search result.
 *
 *	<p>A search result is a 
 *	{@link edu.northwestern.at.wordhoard.model.Word word} and the index in
 *	the word of a
 *	{@link edu.northwestern.at.wordhoard.model.morphology.WordPart word part}.
 *
 *	<p>The index of the word part is -1 if the search did not include a
 *	lemma, part of speech, word class, or major word class criteron. In this 
 *	case, no particular part of the word is responsible for the word being a 
 *	result (as in, for exammple, a search by spelling only).
 */
 
public class SearchResult {

	/**	The word. */
	
	private Word word;
	
	/**	The part index in the word, or - 1 if none. */
	
	private int partIndex;
	
	/**	Creates a new search result.
	 *
	 *	@param	word		The word.
	 *
	 *	@param	partIndex	The part index in the word, or -1 if none.
	 */
	 
	public SearchResult (Word word, int partIndex) {
		this.word = word;
		this.partIndex = partIndex;
	}
	
	/**	Gets the word. 
	 *
	 *	@return		The word.
	 */
	 
	public Word getWord () {
		return word;
	}
	
	/**	Gets the part index in the word.
	 *
	 *	@return		The part index in the word, or -1 if none.
	 */
	 
	public int getPartIndex () {
		return partIndex;
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

