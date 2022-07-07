package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;

/**	Displays a progress dialog for a task running in a SwingWorker thread. */

public abstract class ProgressMonitorDialog extends ProgressDialog
{
	/** Swing Worker instance, used to execute custom function. */

	protected SwingWorker worker		= null;

	/**	Timer used to update progress. */

	protected javax.swing.Timer timer	= null;

	/**	Task to monitor. */

	protected ProgressMonitorTask task	= null;

	/** Create progress monitor dialog.
	 *
	 *  @param	task			The task to monitor.
	 *	@param	parentWindow	The parent window.
	 *	@param	title			Title for progress dialog.
	 *	@param	initLabel		Initial text of label.
	 *	@param	minBar			Minimum value for progress bar.
	 *	@param	maxBar			Maximum value for progress bar.
	 *	@param	allowCancel		Display a cancel button.
	 *	@param	startupDelay	Startup delay.  Dialog will not appear
	 *							until this many milliseconds have passed
	 *							since creation of dialog.
	 */

	public ProgressMonitorDialog
	(
		ProgressMonitorTask task ,
		Window parentWindow ,
		String title ,
		String initLabel ,
		int minBar ,
		int maxBar ,
		boolean allowCancel ,
		long startupDelay
	)
	{
								//	Create progress dialog.
		super
		(
			parentWindow ,
			title ,
			initLabel ,
			minBar ,
			maxBar ,
			allowCancel ,
			startupDelay
		);
		                   		//	Save the task to monitor.
		this.task	= task;

								//	Set indeterminate style progress
								//	display if task not provided.
		if ( task == null )
		{
			setIndeterminate( true );
		}
								//	Create swing worker thread.

		worker = new SwingWorker()
		{
			public Object construct()
			{
				return ProgressMonitorDialog.this.construct();
			}

			public void finished()
			{
				ProgressMonitorDialog.this.setVisible( false );
			}
	    };
	    						//	Create timer.

		ActionListener actionListener	=
			new ActionListener()
			{
				public void actionPerformed( ActionEvent event )
				{
					ProgressMonitorDialog.this.progressUpdate( event );
				}
			};

		timer = new javax.swing.Timer( 100 , actionListener );

		timer.setInitialDelay( 0 );
	}

	/**	Progress update.
	 * @param event The event triggering the update.
	*/

	protected void progressUpdate( ActionEvent event )
	{
		if ( task != null )
		{
			updateProgress( task.getCurrent() , task.getMessage() );
		}
		else
		{
			updateProgress( getCurrentBarValue() , label.getText() );
		}
	}

	/**	Handle dialog cancelled. */

	protected void doCancel()
	{
		super.doCancel();
								//	Interrupt worker thread and
								//	stop timer.
		worker.interrupt();
		timer.stop();
								//	Hide dialog.

		setVisible( false );
	}

	/**	Get worker thread interrupted state.
	 *
	 *	@return		true when worker thread interrupted, false otherwise.
	 */

	public boolean isInterrupted()
	{
		return worker.isInterrupted();
	}

	/**	Display progress dialog and run the worker thread.
	 *
	 *	@return		Value returned by SwingWorker thread.
	 */

	public Object start()
	{
								//	Start swing worker thread.
		worker.start();
								//	Start timer.
		timer.start();
								//	Get result value from swing worker thread.

		Object result	= worker.get();

								//	Stop timer.
		timer.stop();
								//	Hide progress.
		dispose();
								//	Return result.
		return result;
	}

	/**	This method will be executed within separate worker thread.
	 * @return The constructed Object.
	 */

	public abstract Object construct();
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

