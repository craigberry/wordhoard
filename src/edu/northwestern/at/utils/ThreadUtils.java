package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

/**	Thread utilities.
 *
 *	<p>
 *	This static class provides various utility methods for working
 *	with threads.
 *	</p>
 */

public class ThreadUtils
{
	/**	Wait for a thread to finish.
	 *
	 *	@param	thread		The thread to wait for.
	 */

	public static void waitForThread( Thread thread )
	{
		if ( thread == null ) return;

		try
		{
			thread.join();
		}
		catch ( Exception e )
		{
//			e.printStackTrace();
		}

/*
		while ( thread.isAlive() )
		{
			SwingUtils.flushAWTEventQueue();

			try
			{
				Thread.sleep( 100 );
			}
			catch ( InterruptedException e )
			{
			}
		}
*/
	}

	/**	Set thread priority.
	 *
	 *	@param	thread		The thread.
	 *	@param	priority	The new thread priority.
	 */

	public static void setPriority( Thread thread , int priority )
	{
		if ( thread != null )
		{
			try
			{
				thread.setPriority( priority );
			}
			catch ( IllegalArgumentException e )
			{
			}
			catch ( SecurityException e )
			{
			}
		}
	}

	/**	Sleep for specified number of milliseconds.
	 *
	 *	@param	sleepTime		The number of milliseconds yo sleep.
	 */

	public static void sleep( int sleepTime )
	{
		try
		{
			Thread.sleep( sleepTime );
		}
		catch ( InterruptedException e )
		{
		}
	}

	/**	Can't instantiate but can override. */

	protected ThreadUtils()
	{
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

