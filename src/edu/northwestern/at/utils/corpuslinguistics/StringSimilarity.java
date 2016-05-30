package edu.northwestern.at.utils.corpuslinguistics;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

/**	Interface defining a method for computing string similarity.
 *
 *	<p>
 *	The similarity values computed by concrete implementations
 *	of this interface must take on values from 0.0 (no similarity)
 *	to 1.0 (perfect similarity).
 *	</p>
 */

public interface StringSimilarity
{
	/**	Compute similarity between two strings.
	 *
	 *	@param	s1	First string.
	 *	@param	s2	Second string.
	 *
	 *	@return		Similarity as a value from
	 *			0.0 (no similarity) to 1.0
	 *			(perfect similarity).
	 */

	public double similarity( String s1 , String s2 );
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

