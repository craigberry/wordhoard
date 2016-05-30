package edu.northwestern.at.wordhoard.model.grouping;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.utils.*;

/**	A "none of the above" grouping object.
 */

public class NoneOfTheAbove implements GroupingObject {

	/**	Map from group by classes to none of the above objects. */
	
	private static HashMap map = new HashMap();
	
	/**	Gets a none of the above grouping object.
	 *
	 *	@param	groupBy		Group by class.
	 *
	 *	@param	str			Grouping spelling string.
	 *
	 *	@return				None of the above grouping object.
	 */
	 
	public static NoneOfTheAbove get (Class groupBy, String str) {
		NoneOfTheAbove result = (NoneOfTheAbove)map.get(groupBy);
		if (result == null || 
			!Compare.equals(str, result.groupingSpelling.getString()))
		{
			result = new NoneOfTheAbove();
			try {
				GroupingObject obj = (GroupingObject)groupBy.newInstance();
				result.reportPhrase = obj.getReportPhrase();
			} catch (Exception e) {
				result.reportPhrase = "in";
			}
			result.groupingSpelling = new Spelling(str, TextParams.ROMAN);
			map.put(groupBy, result);
		}
		return result;
	}
	
	/**	Gets a none of the above grouping object.
	 *
	 *	<p>The grouping spelling is "<none>".
	 *
	 *	@param	groupBy		Group by class.
	 *
	 *	@return				None of the above grouping object.
	 */
	 
	public static NoneOfTheAbove get (Class groupBy) {
		return get(groupBy, "<none>");
	}

	/**	Report phrase. */
	
	private String reportPhrase;
	
	/**	Spelling. */
	
	private Spelling groupingSpelling;
	
	/** Creates a new "none of the above" grouping object.
	 */
	 
	private NoneOfTheAbove () {
	}
	
	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase.
	 */
	 
	public String getReportPhrase () {
		return reportPhrase;
	}
	
	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */
	 
	public Spelling getGroupingSpelling (int numHits) {
		return groupingSpelling;
	}
	
	/**	Returns true if this object is equal to some other object.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof NoneOfTheAbove)) return false;
		NoneOfTheAbove other = (NoneOfTheAbove)obj;
		return Compare.equals(reportPhrase, other.reportPhrase) &&
			Compare.equals(groupingSpelling, other.groupingSpelling);
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		if (reportPhrase == null) {
			return groupingSpelling == null ? 0 : groupingSpelling.hashCode();
		} else {
			int code = reportPhrase.hashCode();
			return groupingSpelling == null ? code :
				code + groupingSpelling.hashCode();
		}
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

