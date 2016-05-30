package edu.northwestern.at.wordhoard.model.grouping;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.text.*;

/**	A preceding word form grouping object.
 */

public class PrecedingWordForm implements GroupingObject {

	/**	The word form, or null if none. */
	
	private LemPos lemPos;
	
	/**	Creates a new preceding word form object.
	 *
	 *	@param	lemPos		The word form, or null if none.
	 */
	 
	public PrecedingWordForm (LemPos lemPos) {
		this.lemPos = lemPos;
	}
	
	/**	Gets the report phrase.
	 *
	 *	@return		"preceded by".
	 */
	 
	public String getReportPhrase () {
		return "preceded by";
	}
	
	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */
	 
	public Spelling getGroupingSpelling (int numHits) {
		return lemPos == null ? new Spelling("<none>", TextParams.ROMAN) : 
			lemPos.getGroupingSpelling(numHits);
	}
	
	/**	Returns true if this object is equal to some other object.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof PrecedingWordForm)) return false;
		PrecedingWordForm other = (PrecedingWordForm)obj;
		if (lemPos == null) return other.lemPos == null;
		return lemPos.equals(other.lemPos);
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return lemPos == null ? 0 : lemPos.hashCode();
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

