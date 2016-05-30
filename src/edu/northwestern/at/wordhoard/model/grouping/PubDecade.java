package edu.northwestern.at.wordhoard.model.grouping;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.text.*;

/**	A publication decade grouping object.
 */

public class PubDecade implements GroupingObject {

	/**	The start year of the decade. */

	private int startYear;
	
	/**	Creates a new publication decade grouping object.
	 */
	 
	public PubDecade () {
	}
	
	/**	Creates a new publication decade grouping object.
	 *
	 *	@param	startYear	The start year of the decade.
	 */
	 
	public PubDecade (int startYear) {
		this.startYear = startYear;
	}
	
	/**	Gets the start year of the decade.
	 *
	 *	@return		The start year of the decade.
	 */
	 
	public int getStartYear () {
		return startYear;
	}
	
	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "in".
	 */
	 
	public String getReportPhrase () {
		return "in";
	}
	
	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */
	 
	public Spelling getGroupingSpelling (int numHits) {
		String str = Integer.toString(startYear) + "-" + 
			Integer.toString(startYear+9);
		return new Spelling(str, TextParams.ROMAN);
	}
	
	/**	Returns true if this object is equal to some other object.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof PubDecade)) return false;
		PubDecade other = (PubDecade)obj;
		return startYear == other.startYear;
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return startYear;
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

