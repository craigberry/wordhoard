package edu.northwestern.at.wordhoard.model.bibtool;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.grouping.*;

/**	A set of grouping options.
 */
 
public class GroupingWorkOptions {

	/**	No grouping option. */
	
	public static final int GROUP_BY_NONE = 0;

	/**	Group by author option. */
	
	public static final int GROUP_BY_AUTHOR = 1;

	/**	Group by year option. */
	
	public static final int GROUP_BY_YEAR = 2;
		
	/**	Group by decade option. */
	
	public static final int GROUP_BY_DECADE = 3;

	/**	Group by quarter century option. */
	
	public static final int GROUP_BY_QCENTURY = 4;

	/**	Group by quarter century option. */
	
	public static final int GROUP_BY_CENTURY = 5;

	/**	Grouping option names. */
	
	public static final String[] GROUP_BY_NAMES =
		new String[] {
			"None",
			"Author",
			"Year",
			"Decade",
			"25 Years",
			"Century",
		};
		
	/**	Order by title. */
	
	public static final int ORDER_BY_WORK = 0;
	
	/**	Order by Author. */
	
	public static final int ORDER_BY_AUTHOR = 1;
	
	/**	Order by date option. */
	
	public static final int ORDER_BY_DATE = 2;

	/**	Order by count - used for group by author. */
	
	public static final int ORDER_BY_COUNT = 3;
		

	/**	Ordering option names. */

	
	public static final String[] ORDER_BY_NAMES =
		new String[] {
			"Title",
			"Author",
			"Date",
			"Count",
		};
		
	/**	Ascending option. */
	
	public static final int ASCENDING = 0;
	
	/**	Descending option. */
	
	public static final int DESCENDING = 1;
	
	/**	Ascending/descending option names. */
	
	public static final String[] UP_DOWN_NAMES =
		new String[] {
			"Ascending",
			"Descending"
		};
	
	/**	Grouping option. */

	private int groupBy;
	
	/**	Ordering option. */
	
	private int orderBy;
	
	/**	Ascending/descending option. */
	
	private int upDown;
	
	/**	Creates a new set of grouping options.
	 *
	 *	@param	groupBy		Grouping option.
	 *
	 *	@param	orderBy		Ordering option.
	 *
	 *	@param	upDown		Ascending/descending option.
	 */
	
	public GroupingWorkOptions (int groupBy, int orderBy, int upDown) {
		this.groupBy = groupBy;
		this.orderBy = orderBy;
		this.upDown = upDown;
	}
	
	/**	Gets the grouping option.
	 *
	 *	@return		Grouping option.
	 */
	 
	public int getGroupBy () {
		return groupBy;
	}
	
	/**	Sets the grouping option.
	 *
	 *	@param	groupBy		Grouping option.
	 */
	 
	public void setGroupBy (int groupBy) {
		this.groupBy = groupBy;
	}
	
	/**	Gets the ordering option.
	 *
	 *	@return		Ordering option.
	 */
	 
	public int getOrderBy () {
		return orderBy;
	}
	
	/**	Sets the ordering option.
	 *
	 *	@param	orderBy		Ordering option.
	 */
	 
	public void setOrderBy (int orderBy) {
		this.orderBy = orderBy;
	}
	
	/**	Gets the ascending/descending option.
	 *
	 *	@return		Ascending/descending option.
	 */
	 
	public int getUpDown () {
		return upDown;
	}
	
	/**	Sets the ascending/descending option.
	 *
	 *	@param	upDown		Ascending/descending option.
	 */
	 
	public void setUpDown (int upDown) {
		this.upDown = upDown;
	}
	
	/**	Prunes leading articles from a string.
	 *
	 *	<p>Removes leading "A", "An", and "The" words from the string.
	 *
	 *	@param	str		String.
	 *
	 *	@return			Pruned string.
	 */
	 
	private String prune (String str) {
		String lowerCaseStr = str.toLowerCase();
		if (lowerCaseStr.startsWith("a ")) {
			return str.substring(2);
		} else if (lowerCaseStr.startsWith("an ")) {
			return str.substring(3);
		} else if (lowerCaseStr.startsWith("the ")) {
			return str.substring(4);
		}
		return str;
	}
	
	/**	Alphabetical order comparator. */
	
	private class AlphabeticalComparator implements Comparator {
		public int compare (Object o1, Object o2) {
			String s1 = o1.toString();
			String s2 = o2.toString();
			if (groupBy == GROUP_BY_NONE) {
				s1 = prune(o1.toString());
				s2 = prune(o2.toString());
			}
			int k = Compare.compareIgnoreCase(s1, s2);
			if (k == 0 && o1 instanceof PersistentObject) {
				PersistentObject x1 = (PersistentObject)o1;
				PersistentObject x2 = (PersistentObject)o2;
				k = Compare.compare(x1.getId(), x2.getId());
			}
			return upDown == ASCENDING ? k : -k;
		}
	}
	
	/**	Alphabetical by author comparator. For now until we have Author in the model just compare */
	
	private class AuthorComparator implements Comparator {
		public int compare (Object o1, Object o2) {
		
		String s1 = null;
		String s2 = null;

		if ((o1 instanceof Work) && (o2 instanceof Work)) {
			Work w1 = (Work)o1;
			Work w2 = (Work)o2;
			Iterator it = w1.getAuthors().iterator();
			s1 = it.hasNext() ? ((Author)it.next()).getName().getString() : null;
			it = w2.getAuthors().iterator();
			s2 = it.hasNext() ? ((Author)it.next()).getName().getString() : null;
		} else if ((o1 instanceof Author) && (o2 instanceof Author)) {
			s1 = ((Author)o1).getName().getString();
			s2 = ((Author)o2).getName().getString();
		}

/*			if (groupBy == GROUP_BY_NONE) {
				s1 = prune(o1.toString());
				s2 = prune(o2.toString());
			}
*/
			int k = Compare.compareIgnoreCase(s1, s2);
			if (k == 0 && o1 instanceof PersistentObject) {
				PersistentObject x1 = (PersistentObject)o1;
				PersistentObject x2 = (PersistentObject)o2;
				k = Compare.compare(x1.getId(), x2.getId());
			}
			return upDown == ASCENDING ? k : -k;
		}
	}


	/**	Frequency comparator. */
	
	private class FrequencyComparator implements Comparator {
		private AlphabeticalComparator alphabeticalComparator = 
			new AlphabeticalComparator();
		private Map map;
		private FrequencyComparator (Map map) {
			this.map = map;
		}
		public int compare (Object o1, Object o2) {
			int c1 = ((ArrayList)map.get(o1)).size();
			int c2 = ((ArrayList)map.get(o2)).size();
			Work w1 = (Work)o1;
			Work w2 = (Work)o2;
			float freq1 = w1.getRelFreq(c1);
			float freq2 = w2.getRelFreq(c2);
			if (freq1 == freq2) {
				return alphabeticalComparator.compare(o1,o2);
			} else if (freq1 < freq2) {
				return upDown == ASCENDING ? -1 : +1;
			} else {
				return upDown == ASCENDING ? +1 : -1;
			}
		}
	}
	
	/**	Count comparator. */
	
	private class CountComparator implements Comparator {
		private AlphabeticalComparator alphabeticalComparator = 
			new AlphabeticalComparator();
		private Map objectMap = null;
		
		public CountComparator(Map objectMap) {
			this.objectMap = objectMap;
		}
		
		public int compare (Object o1, Object o2) {
			int c1 = ((List) objectMap.get(o1)).size();
			int c2 = ((List) objectMap.get(o2)).size();
			
//			int c1 = ((Author)o1).getWorks().size();
//			int c2 = ((Author)o2).getWorks().size();
			int k = c1-c2;
			if (k == 0) {
				return alphabeticalComparator.compare(o1,o2);
			} else {
				return upDown == ASCENDING ? k : -k;
			}
		}
	}
	
	/**	Date comparator for work objects. */
	
	private class DateComparator implements Comparator {
		private AlphabeticalComparator alphabeticalComparator = new AlphabeticalComparator();
		public int compare (Object o1, Object o2) {
			int k = 0;
			
			if ((o1 instanceof Work) && (o2 instanceof Work)) {
				Work w1 = (Work)o1;
				Work w2 = (Work)o2;
				PubYearRange pub1 = w1.getPubDate();
				PubYearRange pub2 = w2.getPubDate();
				Integer start1 = pub1 == null ? null : pub1.getStartYear();
				Integer start2 = pub2 == null ? null : pub2.getStartYear();
				k = Compare.compare(start1, start2);
			} else if ((o1 instanceof DateGroup) && (o2 instanceof DateGroup)) {
				k = Compare.compare(((DateGroup)o1).getStartYear(), ((DateGroup)o2).getStartYear());
			}
			if (k == 0) {
				return alphabeticalComparator.compare(o1,o2);
			} else {
				return upDown == ASCENDING ? k : -k;
			}
		}
	}
	
	/**	Groups a collection of works.
	 *
	 *	<p>The grouping and ordering options must be one of the following
	 *	combinations:
	 *
	 *	<ul>
	 *	<li>GROUP_BY_NONE: ORDER_BY_AUTHOR, 
	 *		ORDER_ALPHABETICALLY, or ORDER_BY_DATE.
	 *	<li>GROUP_BY_AUTHOR: ORDER_BY_COUNT, ORDER_ALPHABETICALLY, ORDER_BY_DATE.
	 *	</ul>
	 *
	 *	@param	works	Collection of works.
	 *
	 *	@return		An ordered map from grouping objects to array lists of 
	 *				works.
	 */
	 
	public Map group (Collection works) {
		final HashMap map = new HashMap();
		int addcount = 0;
			for (Iterator it = works.iterator(); it.hasNext(); ) {
				Work work = (Work)it.next();
				GroupingObject obj = work.getGroupingObject(groupBy);
				ArrayList list = (ArrayList)map.get(obj); 
				if (list == null) {
					list = new ArrayList();
					map.put(obj, list);
				}
				list.add(work);
				addcount++;
			}
			for (Iterator it = map.keySet().iterator(); it.hasNext(); ) {
				GroupingObject obj = (GroupingObject)it.next();
				ArrayList list = (ArrayList)map.get(obj);
			}
		
		Comparator comparator = null;
		switch (orderBy) {
			case ORDER_BY_WORK:
				comparator = new AlphabeticalComparator();
				break;
			case ORDER_BY_AUTHOR:
				comparator = new AuthorComparator();
				break;
			case ORDER_BY_DATE:
				comparator = new DateComparator();
				break;
			case ORDER_BY_COUNT:
				comparator = new CountComparator(map);
				break;
		}
		TreeMap result = new TreeMap(comparator);
		result.putAll(map);
		return result;
	}

	/**	Sort a collection of works.
	 *
	 *
	 *	@param	works	Collection of works.
	 *
	 *	@return		An ordered map of works.
	 */
	 
	public List sort (Collection works) {
		final HashMap map = new HashMap();
		for (Iterator it = works.iterator(); it.hasNext(); ) {
			Work work = (Work)it.next();
			map.put(work, work);
		}
		Comparator comparator = null;
		switch (orderBy) {
			case ORDER_BY_WORK:
				comparator = new AlphabeticalComparator();
				break;
			case ORDER_BY_AUTHOR:
				comparator = new AuthorComparator();
				break;
			case ORDER_BY_DATE:
				comparator = new DateComparator();
				break;
			case ORDER_BY_COUNT:
				comparator = new CountComparator(map);
				break;
		}
		TreeMap tmap = new TreeMap(comparator);
		tmap.putAll(map);
		return new ArrayList(tmap.values());
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two sets of grouping options are equal if all three of their
	 *	component options are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof GroupingWorkOptions)) return false;
		GroupingWorkOptions other = (GroupingWorkOptions)obj;
		return groupBy == other.getGroupBy() &&
			orderBy == other.getOrderBy() &&
			upDown == other.getUpDown();
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

