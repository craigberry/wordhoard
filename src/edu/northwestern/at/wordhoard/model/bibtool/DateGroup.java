package edu.northwestern.at.wordhoard.model.bibtool;

/*	Please see the license information at the end of this file. */

import java.util.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.*;

/**	An author.
 *
 *	<p>
 *	A DateGroup entry contains the following:
 *	</p>
 *
 *	<ul>
 *	<li>A list of works (see {@link Work}) from the time frame.
 *	A DateGroup may not have any works listed.</li>
 *	</ul>
 *
 *	<p>
 *	The earliest and latest work dates may not match the dates
 *	for the earliest and latest works of an author in the database.
 *	This is because the database may not contain all of the author's works.
 *	</p>
 *
 */

public class DateGroup implements GroupingObject
{

	/**	The date label. */

	private String dateLabel;

	/**	The DateGroup's start year. */

	private Integer startYear;

	/**	The DateGroup's end year. */

	private Integer endYear;

	/**	The dateGroups. */

	private static HashMap dateGroups = new HashMap();	//

	/**	Creates a new entry.
	 * @param groupBy	Grouping option.
	 * @param startYear	Lower bound of date range.
	 * @param endYear	Upper bound of date range
	 * @return List of works in date range.
	 */

	public static DateGroup getDateGroup(int groupBy, Integer startYear, Integer endYear)
	{
	
		int labelStart=0, labelEnd=0;
		int useYear=0;
		String dateKey = null;
		
		if(startYear != null) {
			useYear=startYear.intValue();
		} else if (endYear != null) {
			useYear=endYear.intValue();
		} else {
			useYear=0;
		}

		if(useYear == 0) {
			labelStart=useYear;
			labelEnd = useYear;
			dateKey="(Not dated)";
		} else
			{
			switch (groupBy) {
				case GroupingWorkOptions.GROUP_BY_YEAR:
					labelStart=useYear;
					labelEnd = useYear;
					dateKey=String.valueOf(labelStart);
					break;

				case GroupingWorkOptions.GROUP_BY_DECADE:
					labelStart = useYear - (useYear % 10);
					labelEnd = labelStart + 9;
					dateKey= "(" + labelStart + "-" + labelEnd + ")";
					break;

				case GroupingWorkOptions.GROUP_BY_QCENTURY:
					labelStart = useYear - (useYear % 25);
					labelEnd = labelStart + 24;
					dateKey= "(" + labelStart + "-" + labelEnd + ")";
					break;

				case GroupingWorkOptions.GROUP_BY_CENTURY:
					labelStart = useYear - (useYear % 100);
					labelEnd = labelStart + 99;
					dateKey= "(" + labelStart + "-" + labelEnd + ")";
					break;
			}
		}
		
		if(dateGroups.containsKey(dateKey)) {
			return (DateGroup)dateGroups.get(dateKey);
		} else
			{
			DateGroup dg = new DateGroup(dateKey, new Integer(labelStart), new Integer(labelEnd));
			dateGroups.put(dateKey,dg);
			return dg;
		}
	}


	public DateGroup(String dateLabel, Integer startYear, Integer endYear)
	{
			this.startYear = startYear;
			this.endYear = endYear;
			this.dateLabel=dateLabel;
	}

	/**	Gets the group's label.
	 *
	 *	@return		The dateLabel.
	 *
	 */

	public String getLabel()
	{
		return dateLabel;
	}

	/**	Gets the start year.
	 *
	 *	@return		The start year.
	 *
	 */

	public Integer getStartYear()
	{
		return startYear;
	}

	/**	Gets the end year.
	 *
	 *	@return		The end year.
	 *
	 */

	public Integer getEndYear()
	{
		return endYear;
	}


	/**	Return name for display.
	 *
	 *	@return		Author's name.
	 */

	public String toString()
	{
		return dateLabel;
	}

	/**	Return author's full information for display.
	 * @return	Author detailed information.
	 */

	public String toStringDetailed()
	{
		return "DateRange:\n" +
			"name = \t'" + dateLabel + "'\n" +
			"startYear = \t'" + startYear + "'\n" +
			"endYear = \t'" + endYear + "'\n" +
			"\n";
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
		return new Spelling(dateLabel, TextParams.ROMAN);
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

