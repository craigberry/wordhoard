package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.SwingUtilities;

/**	SwingWorker.
 *
 *	<p>
 *	This is the 3rd version of SwingWorker (also known as
 *	SwingWorker 3), an abstract class that you subclass to
 *	perform GUI-related work in a dedicated thread.  For
 *	instructions on and examples of using this class, see:
 *	</p>
 *
 *	<p>
 *	http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 *	</p>
 *
 *	<p>
 *	Note that the API changed slightly in the 3rd version:
 *	You must now invoke start() on the SwingWorker after
 *	creating it.
 *	</p>
 *
 *	<p>
 *	Minor modifications by Philip R. "Pib" Burns.  2005/1/24.
 *	</p>
 */

public abstract class SwingWorker
{
	private Object value;  // see getValue(), setValue()

	/**
	 * Class to maintain reference to current worker thread
	 * under separate synchronization control.
	 */

	private static class ThreadVar
	{
		private Thread thread;
		ThreadVar(Thread t)
		{
			thread = t;
		}

		synchronized Thread get()
		{
			return thread;
		}

		synchronized void clear()
		{
			thread = null;
		}
	}

	private ThreadVar threadVar;

	/**
	 * Get the value produced by the worker thread, or null if it
	 * hasn't been constructed yet.
	 * @return	Object containing the value.
	 */

	protected synchronized Object getValue()
	{
		return value;
	}

	/**
	 * Set the value produced by worker thread
	 */

	private synchronized void setValue(Object x)
	{
		value = x;
	}

	/**
	 * Compute the value to be returned by the <code>get</code> method.
	 * @return	Object containing the value.
	 */

	public abstract Object construct();

	/**
	 * Called on the event dispatching thread (not on the worker thread)
	 * after the <code>construct</code> method has returned.
	 */

	public void finished()
	{}

	/**
	 * A new method that interrupts the worker thread.  Call this method
	 * to force the worker to stop what it's doing.
	 */

	public void interrupt()
	{
		Thread t = threadVar.get();

		if (t != null)
		{
			t.interrupt();
		}

		threadVar.clear();
	}

	/**
	 * Return the value created by the <code>construct</code> method.
	 * Returns null if either the constructing thread or the current
	 * thread was interrupted before a value was produced.
	 *
	 * @return the value created by the <code>construct</code> method
	 */

	public Object get()
	{
		while (true)
		{
			Thread t = threadVar.get();

			if (t == null)
			{
				return getValue();
			}
			try
			{
				t.join();
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt(); // propagate
				return null;
			}
		}
	}

	/**
	 * Start a thread that will call the <code>construct</code> method
	 * and then exit.
	 */

	public SwingWorker()
	{
		final Runnable doFinished = new Runnable()
			{
				public void run()
				{
					finished();
				}
			};

		Runnable doConstruct = new Runnable()
			{
				public void run()
				{
					try
					{
						setValue(construct());
					}
					finally
					{
						threadVar.clear();
					}

					SwingUtilities.invokeLater(doFinished);
				}
			};

		Thread t = new Thread(doConstruct);

		threadVar = new ThreadVar(t);
	}

	/**
	 * Start the worker thread.
	 */

	public void start()
	{
		Thread t = threadVar.get();

		if (t != null)
		{
			t.start();
		}
	}

	/**
	 * Return the worker thread interrupted status.
	 *
	 * @return 		<code>true</code> when worker has been interrupted,
	 *				<code>false</code> otherwise.
	 */

	public synchronized boolean isInterrupted()
	{
		Thread t = threadVar.get();

		return (null != t ? t.isInterrupted() : true);
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

