package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.*;

/**	A text field for inputting a timer value as "days hh:mm:ss".
 *
 *	<p>
 *	<strong>Examples:</strong>
 *	</p>
 *
 *	<p><strong>Just days -- here, 120 days.</strong></p>
 *
 *	<ul>
 *		<li>120</li>
 *	</ul>
 *
 *	<p><strong>Just hours, minutes, seconds.</strong></p>
 *	<p>
 *	Hours, minutes, and seconds must be entered as "0" or "00"
 *	if needed to distinguish, for example, 5 hours from 5 minutes
 *	from 5 seconds.
 *	</p>
 *
 *	<ul>
 *		<li>05:20:15</li>
 *		<li>5:20:15</li>
 *		<li>5:20:00</li>
 *		<li>5:00:00</li>
 *		<li>0:5:00</li>
 *		<li>0:0:05</li>
 *	</ul>
 *
 *	<p><strong>Both days and hours, minutes, seconds.</strong></p>
 *
 *	<ul>
 *		<li>120 5:20:15</li>
 *		<li>120 5:20:00</li>
 *		<li>120 00:05:00</li>
 *	</ul>
 *
 */

public class TimerTextField extends XTextField
{
	/** Holds the timer value. */

	protected long timerValue = 0;

	/** True to show zero fields. */

	protected boolean showZeroFields = true;

	/** Create field with 0 timer value. */

	public TimerTextField()
	{
	}

	/** Create field with specified timer value.
	 *
	 *	@param	timerValue	long field containing
	 *						timer value to edit.
	 */

	public TimerTextField( long timerValue )
	{
		this.timerValue = timerValue;

		setText( DateTimeUtils.formatTimer( timerValue , showZeroFields ) );
	}

	/** Create field with specified timer value and zero field setting.
	 *
	 *	@param	timerValue		long field containing
	 *							timer value to edit.
	 *	@param	showZeroFields	True to show explicit zero values.
	 */

	public TimerTextField( long timerValue , boolean showZeroFields )
	{
		this.timerValue		= timerValue;
		this.showZeroFields	= showZeroFields;

		setText( DateTimeUtils.formatTimer( timerValue , showZeroFields ) );
	}

	/** Get the input timer value.
	 *
	 *	@return		The timer a long, or null if the
	 *				input text is not a valid timer definition.
	 */

	public long getTimerValue()
	{
		return DateTimeUtils.parseTimer( getText() );
	}

	/** Get the days value from the timer.
	 *
	 *	@return		Days specified by timer.
	 */

	public int getDays()
	{
		return (int)( timerValue / ( 1000L * DateTimeUtils.SECONDS_PER_DAY ) );
	}

	/** Get the HH:MM:SS value from the timer.
	 *
	 *	@return		HH:MM:SS specified by timer in milliseconds.
	 */

	public long getHHMMSS()
	{
		long seconds, days, hours, minutes;

		seconds		= timerValue / 1000L;

		days		= seconds / DateTimeUtils.SECONDS_PER_DAY;
		seconds		= seconds - ( days * DateTimeUtils.SECONDS_PER_DAY );

		hours		= seconds / DateTimeUtils.SECONDS_PER_HOUR;
		seconds		= seconds - ( hours * DateTimeUtils.SECONDS_PER_HOUR );

		minutes		= seconds / DateTimeUtils.SECONDS_PER_MINUTE;
		seconds 	= seconds - ( minutes * DateTimeUtils.SECONDS_PER_MINUTE );

		return DateTimeUtils.makeTimerValue( 0L, hours, minutes, seconds );
	}

	/** Set the input timer value.
	 *
	 *	@param	timerValue		The timer value as a long.
	 */

	public void setTimerValue( long timerValue )
	{
		setText( DateTimeUtils.formatTimer( timerValue , showZeroFields ) );
	}

	/** Set the input timer value.
	 *
	 *	@param	timerValue		The timer value as a long.
	 *	@param	showZeroFields	True to show explicit zero values.
	 */

	public void setTimerValue( long timerValue , boolean showZeroFields )
	{
		setText( DateTimeUtils.formatTimer( timerValue , showZeroFields ) );
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

