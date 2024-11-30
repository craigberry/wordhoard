package edu.northwestern.at.wordhoard.model.grouping;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.wrappers.*;
import jakarta.persistence.Transient;

/**	A grouping object. 
 *
 *	<p>Grouping objects are used to aggregate (group) search results.
 *
 *	<p>Grouping objects are used as map keys, so they must implement
 *	the equals and hashCode methods.
 */

public interface GroupingObject {

	/**	Gets the report phrase.
	 *
	 *	<p>This phrase is used in search result reports. E.g., for works
	 *	the phrase is "in", while for lemmas the phrase is "with lemma".
	 *
	 *	@return		The report phrase.
	 */

	@Transient
	public String getReportPhrase ();
	
	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits. The method may use this 
	 *						parameter if it needs to return a singular
	 *						(numHits = 1) versus a plural (numHit &gt; 1) form.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	@Transient
	public Spelling getGroupingSpelling (int numHits);
	
	/**	Returns true if this object equals some other object.
	 *
	 *	<p>Grouping objects must implement the equals method.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj);
	
	/**	Returns a hash code for the object.
	 *
	 *	<p>Grouping objects must implement the hashCode method.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode ();

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

