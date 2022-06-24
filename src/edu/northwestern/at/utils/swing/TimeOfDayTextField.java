package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;

/**	A text field for inputting the time of day in "hh:mm am" form.
 *
 *	<p>
 *	The time of day may be entered in "hh:mm am" form -- e.g.,
 *	"12:42 am", "6:30 pm" or without the am/pm -- e.g.,
 *	"12:42", "18:30".
 *	</p>
 */

public class TimeOfDayTextField extends XTextField
{
	/** Holds the time of day. */

	protected Date timeOfDay;

	/** Create field with current time of day. */

	public TimeOfDayTextField()
	{
		this( new Date() );
	}

	/** Create field with specified time of day.
	 *
	 *	@param	timeOfDay	Date field containing
	 *						time of day to edit.
	 */

	public TimeOfDayTextField( Date timeOfDay )
	{
		this.timeOfDay = timeOfDay;

		if ( timeOfDay == null )
			this.timeOfDay = new Date();

		setText( DateTimeUtils.formatTime( timeOfDay ) );
	}

	/** Get the input time of day.
	 *
	 *	@return		The time of day as a Date, or null if the
	 *				input text is not a valid time of day.
	 */

	public Date getTime()
	{
		return DateTimeUtils.parseTime( getText() );
	}

	/** set the input time of day.
	 *
	 *	@param	time		The time of day as a Date, or null if the
	 *						input text is not a valid time of day.
	 */

	public void setTime( Date time )
	{
		if ( time != null )
		{
			setText( DateTimeUtils.formatTime( time ) );
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

