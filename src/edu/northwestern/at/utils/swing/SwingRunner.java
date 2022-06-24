package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;

/* Utility routines for working with threads in Swing code. */

public class SwingRunner
{
	/** Runs and waits for a Runnable on AWT Event Dispatching Thread.
	 *
	 *	@param	runnable	Runnable to execute on AWT Event Dispatching
	 *						thread.
	 *
	 *	<p>
	 *	Runs a runnable and waits for it to complete.
	 *	May be called whether or not the current thread
	 *	is the AWT Event dispatching thread.
	 *	</p>
	 */

	public static void runNow( Runnable runnable )
	{
		if ( runnable == null ) return;

	    try
	    {
			if ( SwingUtilities.isEventDispatchThread() )
			{
				runnable.run();
			}
			else
			{
				SwingUtilities.invokeAndWait( runnable );
			}
		}
		catch ( Exception e )
		{
//			e.printStackTrace();
		}
	}

	/** Runs a Runnable on the AWT Event Dispatching Thread without waiting.
	 *
	 *	@param	runnable	Runnable to execute on AWT Event Dispatching
	 *						thread.
	 *
	 *	<p>
	 *	Runs a runnable.  May be called whether or not the current thread
	 *	is the AWT Event dispatching thread.
	 *	</p>
	 */

	public static void runlater( Runnable runnable )
	{
		if ( runnable == null ) return;

	    try
	    {
			SwingUtilities.invokeAndWait( runnable );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
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

