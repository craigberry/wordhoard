package edu.northwestern.at.wordhoard.model.querytool;

/*	Please see the license information at the end of this file. */

import java.io.*;

/**	.
 *
 *	<p>
 *	A DateRange entry contains the following:
 *	</p>
 *
 *	<p>
 *	The earliest and latest work dates may not match the dates
 *	for the earliest and latest works of an author in the database.
 *	This is because the database may not contain all of the author's works.
 *	</p>
 *
 */

public class DateRange implements Serializable
{

	/**	The date label. */

	private String dateLabel;

	/**	The DateRange's start year. */

	private Integer startYear;

	/**	The DateRange's end year. */

	private Integer endYear;


	/**	Creates a new entry.
	 * @param	startYear	Start year of date range.
	 * @param	endYear		End year of date range.
	 */

	public DateRange(Integer startYear, Integer endYear)
	{
		this.startYear = startYear;
		this.endYear = endYear;

		if(startYear==endYear) { dateLabel=String.valueOf(startYear);
		} else { dateLabel = "(" + startYear + "-" + endYear + ")";}
	}


	public DateRange(String dateLabel, Integer startYear, Integer endYear)
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


	/**	Return label for display.
	 *
	 *	@return		DateRange's label
	 */

	public String toString()
	{
		return dateLabel;
	}

	
	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "in".
	 */
	 
	public String getReportPhrase () {
		return "in";
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

