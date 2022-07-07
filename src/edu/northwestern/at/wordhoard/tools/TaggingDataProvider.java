package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import org.w3c.dom.*;

/**	A tagging data provider.
 */

public interface TaggingDataProvider {

	/**	Gets the morphology data for a word.
	 *
	 *	@param	el			"w" element.
	 *
	 *	@param	wordTag		Word tag.
	 *
	 *	@return				Morphology tags for the word: An array of two
	 *						strings: The lemma and pos tags. Returns null
	 *						if no data is available or an error was
	 *						reported. The entire array may be null, or any
	 *						element of it may be null.
	 *
	 *	@throws	Exception	general error.
	 */

	public String[] getMorph (Element el, String wordTag)
		throws Exception;

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

