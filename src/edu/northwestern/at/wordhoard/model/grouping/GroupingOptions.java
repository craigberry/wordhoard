package edu.northwestern.at.wordhoard.model.grouping;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.*;

/**	A set of grouping options.
 */
 
public class GroupingOptions {
		
	/**	Order by frequency option. */
	
	public static final int ORDER_BY_FREQUENCY = 0;
	
	/**	Order by count (number of matches) option. */
	
	public static final int ORDER_BY_COUNT = 1;
	
	/**	Order alphapetically option. */
	
	public static final int ORDER_ALPHABETICALLY = 2;
	
	/**	Order by date option. */
	
	public static final int ORDER_BY_DATE = 3;
	
	/**	Order by word tag option. */
	
	public static final int ORDER_BY_LOCATION = 4;
	
	/**	Number of order by options. */
	
	public static final int NUM_ORDER_BY = 5;
	
	/**	Ordering option names. */
	
	public static final String[] ORDER_BY_NAMES =
		new String[] {
			"Frequency",
			"Count",
			"Alphabetically",
			"Date",
			"Location",
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
	
	/**	Grouping class, or null if no grouping. */

	private Class groupBy;
	
	/**	Ordering option. */
	
	private int orderBy;
	
	/**	Ascending/descending option. */
	
	private int upDown;
	
	/**	Creates a new set of grouping options.
	 *
	 *	@param	groupBy		Grouping class.
	 *
	 *	@param	orderBy		Ordering option.
	 *
	 *	@param	upDown		Ascending/descending option.
	 */
	
	public GroupingOptions (Class groupBy, int orderBy, int upDown) {
		this.groupBy = groupBy;
		this.orderBy = orderBy;
		this.upDown = upDown;
	}
	
	/**	Gets the grouping option.
	 *
	 *	@return		Grouping class.
	 */
	 
	public Class getGroupBy () {
		return groupBy;
	}
	
	/**	Sets the grouping option.
	 *
	 *	@param	groupBy		Grouping class.
	 */
	 
	public void setGroupBy (Class groupBy) {
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
			GroupingObject g1 = (GroupingObject)o1;
			GroupingObject g2 = (GroupingObject)o2;
			String s1 = g1.getGroupingSpelling(2).getString();
			String s2 = g2.getGroupingSpelling(2).getString();
			if (groupBy.equals(Work.class)) {
				s1 = prune(s1);
				s2 = prune(s2);
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
	
	/**	Frequency comparator for CanGetRelFreq objects. 
	*	None of the above objects sort to the end of the list.
	 */
	
	private class FrequencyComparator implements Comparator {
		private AlphabeticalComparator alphabeticalComparator = 
			new AlphabeticalComparator();
		private Map map;
		private FrequencyComparator (Map map) {
			this.map = map;
		}
		public int compare (Object o1, Object o2) {
			int k = 0;
			if ((o1 instanceof CanGetRelFreq) && 
				(o2 instanceof CanGetRelFreq)) 
			{
				int c1 = ((ArrayList)map.get(o1)).size();
				int c2 = ((ArrayList)map.get(o2)).size();
				CanGetRelFreq x1 = (CanGetRelFreq)o1;
				CanGetRelFreq x2 = (CanGetRelFreq)o2;
				float freq1 = x1.getRelFreq(c1);
				float freq2 = x2.getRelFreq(c2);
				if (freq1 == freq2) {
					k = alphabeticalComparator.compare(o1,o2);
				} else if (freq1 < freq2) {
					k = -1;
				} else {
					k = +1;
				}
			} else if (o1 instanceof CanGetRelFreq) {
				k = -1;
			} else if (o2 instanceof CanGetRelFreq) {
				k = +1;
			} else {
				k = 0;
			}
			return upDown == ASCENDING ? k : -k;
		}
	}
	
	/**	Count comparator. */
	
	private class CountComparator implements Comparator {
		private AlphabeticalComparator alphabeticalComparator = 
			new AlphabeticalComparator();
		private Map map;
		private CountComparator (Map map) {
			this.map = map;
		}
		public int compare (Object o1, Object o2) {
			int c1 = ((ArrayList)map.get(o1)).size();
			int c2 = ((ArrayList)map.get(o2)).size();
			int k = c1-c2;
			if (k == 0) {
				return alphabeticalComparator.compare(o1,o2);
			} else {
				return upDown == ASCENDING ? k : -k;
			}
		}
	}
	
	/**	Date comparator for Work objects. None of the above
	 *	objects sort to the end of the list.
	 */
	
	private class WorkDateComparator implements Comparator {
		private AlphabeticalComparator alphabeticalComparator = 
			new AlphabeticalComparator();
		public int compare (Object o1, Object o2) {
			int k = 0;
			if ((o1 instanceof Work) && (o2 instanceof Work)) {
				Work w1 = (Work)o1;
				Work w2 = (Work)o2;
				PubYearRange pub1 = w1.getPubDate();
				PubYearRange pub2 = w2.getPubDate();
				Integer y1 = pub1 == null ? null : pub1.getStartYear();
				Integer y2 = pub2 == null ? null : pub2.getStartYear();
				k = Compare.compare(y1, y2);
				if (k == 0)  k = alphabeticalComparator.compare(o1,o2);
			} else if (o1 instanceof Work) {
				k = -1;
			} else if (o2 instanceof Work) {
				k = +1;
			} else {
				k = 0;
			}
			return upDown == ASCENDING ? k : -k;
		}
	}
	
	/**	Date comparator for PubYearRange objects. None of the above
	 *	objects sort to the end of the list.
	 */
	
	private class PubYearRangeDateComparator implements Comparator {
		private AlphabeticalComparator alphabeticalComparator = 
			new AlphabeticalComparator();
		public int compare (Object o1, Object o2) {
			int k = 0;
			if ((o1 instanceof PubYearRange) && (o2 instanceof PubYearRange)) {
				PubYearRange pub1 = (PubYearRange)o1;
				PubYearRange pub2 = (PubYearRange)o2;
				Integer y1 = pub1 == null ? null : pub1.getStartYear();
				Integer y2 = pub2 == null ? null : pub2.getStartYear();
				k = Compare.compare(y1, y2);
				if (k == 0)  k = alphabeticalComparator.compare(o1,o2);
			} else if (o1 instanceof PubYearRange) {
				k = -1;
			} else if (o2 instanceof PubYearRange) {
				k = +1;
			} else {
				k = 0;
			}
			return upDown == ASCENDING ? k : -k;
		}
	}
	
	/**	Date comparator for PubDecade objects. None of the above
	 *	objects sort to the end of the list.
	 */
	
	private class PubDecadeDateComparator implements Comparator {
		private AlphabeticalComparator alphabeticalComparator = 
			new AlphabeticalComparator();
		public int compare (Object o1, Object o2) {
			int k = 0;
			if ((o1 instanceof PubDecade) && (o2 instanceof PubDecade)) {
				PubDecade dec1 = (PubDecade)o1;
				PubDecade dec2 = (PubDecade)o2;
				int y1 = dec1.getStartYear();
				int y2 = dec2.getStartYear();
				k = y1-y2;
				if (k == 0)  k = alphabeticalComparator.compare(o1,o2);
			} else if (o1 instanceof PubDecade) {
				k = -1;
			} else if (o2 instanceof PubDecade) {
				k = +1;
			} else {
				k = 0;
			}
			return upDown == ASCENDING ? k : -k;
		}
	}
	
	/**	Location comparator for WorkPart objects. None of the above
	 *	objects sort to the end of the list.
	 */
	 
	private class WorkPartLocationComparator implements Comparator {
		private AlphabeticalComparator alphabeticalComparator = 
			new AlphabeticalComparator();
		public int compare (Object o1, Object o2) {
			int k = 0;
			if ((o1 instanceof WorkPart) && (o2 instanceof WorkPart)) {
				WorkPart part1 = (WorkPart)o1;
				WorkPart part2 = (WorkPart)o2;
				k = alphabeticalComparator.compare(part1.getWork(), 
					part2.getWork());
				if (k == 0)  k = part1.getWorkOrdinal() - 
					part2.getWorkOrdinal();
			} else if (o1 instanceof PubDecade) {
				k = -1;
			} else if (o2 instanceof PubDecade) {
				k = +1;
			} else {
				k = 0;
			}
			return upDown == ASCENDING ? k : -k;
		}
	}
	
	/**	Groups a collection of search results.
	 *
	 *	<p>The groupBy class must not be null.
	 *
	 *	<p>If the orderBy option is ORDER_BY_FREQUENCY, the groupBy class must 
	 *	implement the CanGetRelFreq interface.
	 *
	 *	<p>If the orderBy option is ORDER_BY_LOCATION, the groupBy class must
	 *	be WorkPart.class.
	 *
	 *	<p>If the orderBy option is ORDER_BY_DATE, the groupBy class must
	 *	be Work.class, PubYearRange.class, or PubDecade.class.
	 *
	 *	@param	results		Collection of search results.
	 *
	 *	@return				An ordered map from grouping objects to 
	 *						array lists of search results.
	 */
	 
	public Map group (Collection results) {
		final HashMap map = new HashMap();
		for (Iterator it = results.iterator(); it.hasNext(); ) {
			SearchResult result = (SearchResult)it.next();
			Word word = result.getWord();
			int partIndex = result.getPartIndex();
			List groupingObjects = 
				word.getGroupingObjects(groupBy, partIndex);
			for (Iterator it2 = groupingObjects.iterator(); 
				it2.hasNext(); ) 
			{
				GroupingObject obj = (GroupingObject)it2.next();
				ArrayList list = (ArrayList)map.get(obj);
				if (list == null) {
					list = new ArrayList();
					map.put(obj, list);
				}
				list.add(result);
			}
		}
		Comparator comparator = null;
		switch (orderBy) {
			case ORDER_BY_FREQUENCY:
				comparator = new FrequencyComparator(map);
				break;
			case ORDER_BY_COUNT:
				comparator = new CountComparator(map);
				break;
			case ORDER_ALPHABETICALLY:
				comparator = new AlphabeticalComparator();
				break;
			case ORDER_BY_DATE:
				if (groupBy.equals(Work.class)) {
					comparator = new WorkDateComparator();
				} else if (groupBy.equals(PubYearRange.class)) {
					comparator = new PubYearRangeDateComparator();
				} else if (groupBy.equals(PubDecade.class)) {
					comparator = new PubDecadeDateComparator();
				}
				break;
			case ORDER_BY_LOCATION:
				comparator = new WorkPartLocationComparator();
				break;
		}
		TreeMap result = new TreeMap(comparator);
		result.putAll(map);
		return result;
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
		if (obj == null || !(obj instanceof GroupingOptions)) return false;
		GroupingOptions other = (GroupingOptions)obj;
		return Compare.equals(groupBy, other.getGroupBy()) &&
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

