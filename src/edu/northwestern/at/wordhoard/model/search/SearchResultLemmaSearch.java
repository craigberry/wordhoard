package edu.northwestern.at.wordhoard.model.search;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.morphology.*;

/**	A search result.
 *
 *	<p>A search result is a
 *	{@link edu.northwestern.at.wordhoard.model.morphology.Lemma lemma} and its count.
 *
 */

public class SearchResultLemmaSearch {

	/**	The lemma. */

	private Lemma lemma;

	/**	The count */

	private int count;

	/**	The doc count */

	private int docCount;

	/**	Creates a new search result.
	 *
	 *	@param	lemma		The lemma.
	 *
	 *	@param	count	The count.
	 */

	public SearchResultLemmaSearch (Lemma lemma, int count, int docCount) {
		this.lemma = lemma;
		this.count = count;
		this.docCount = docCount;
	}

	/**	Gets the lemma.
	 *
	 *	@return		The lemma.
	 */

	public Lemma getLemma () {
		return lemma;
	}

	/**	Gets the tag.
	 *
	 *	@return		The tag.
	 *
	 */
	
	public Spelling getTag () {
		return lemma.getTag();
	}

	/**	Gets the count.
	 *
	 *	@return		count of this lemma in all corpora.
	 */

	public int getCount () {
		return count;
	}

	/**	Gets the doc count.
	 *
	 *	@return		count of docs in which this lemma is present.
	 */

	public int getDocCount () {
		return docCount;
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

