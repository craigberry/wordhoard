package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.print.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.sys.*;

/** Wraps a JLabel with a progress reporter.
 */

public class ProgressLabelAdapter implements ProgressReporter
{
	/** Minimum value for progress bar. */

	protected int minBar;

	/** Maximum value for progress bar. */

	protected int maxBar;

	/**	Title. */

	protected JLabel titleLabel;

	/**	The current value of the title. */

	protected String titleText;

	/** Label for explanatory message. */

	protected JLabel label;

	/**	The current value of the explanatory message. */

	protected String labelText;

	/**	The label color. */

	protected Color labelColor;

	/** Label displaying how far work has progressed. */

	protected JLabel progressLabel;

	/** Current progress bar value. */

	protected int currentBarValue;

	/** True if progress panel cancelled. */

	protected boolean cancelled = false;

	/** Default startup delay = 1.5 seconds. */

	protected static long defaultStartupDelay = 1500;

	/** How many milliseconds to wait before displaying dialog. */

	protected long startupDelay;

	/** Startup time for dialog. */

	protected long startupTime;

	/**	Nice wide label size. */

	protected int labelWidth			= 300;

	/**	True if the progress display is visible. */

	protected boolean nowVisible		= true;

	/**	True if the progress display is indeterminate. */

	protected boolean nowIndeterminate	= false;

	/**	Spin count for indeterminate mode. */

	protected int spinCount				= 0;

	/**	Display characters for indeterminate mode. */

	protected final static String[]	spinChars	=
		new String[]{ "|" , "/" , "-" , "\\" };

	/**	The timer used to update the display a one second intervals. */

	protected Timer timer;

    /**	One second in thousandths of a second. */

	public final static int ONE_SECOND = 1000;

	/** Create progress label adapter.
	 *
	 * @param	progressLabel   Label wrapped for displaying progress.
	 * @param	title			Title for progress display.
	 * @param	initLabel		Initial text of label.
	 * @param	minBar			Minimum value for progress bar.
	 * @param	maxBar			Maximum value for progress bar.
	 * @param	startupDelay	Startup delay.  Dialog will not appear
	 *							until this many milliseconds have passed
	 *							since creation of dialog.
	 */

	public ProgressLabelAdapter
	(
	    JLabel progressLabel ,
		String title ,
		String initLabel ,
		int minBar ,
		int maxBar ,
		long startupDelay
	)
	{
								//	Save label to display progress.

		this.progressLabel	= progressLabel;

								// Gert title.

		titleText			= StringUtils.safeString( title );

								//	Get min, max values for progress.

		this.minBar			= minBar;
		this.maxBar			= maxBar;

								// Add progress task label.

		this.labelText		= StringUtils.safeString( initLabel );

								// Not cancelled yet.
		cancelled = false;
								// Set startup delay to two seconds.

		this.startupDelay	= startupDelay;

								// Set start time to now.

		startupTime			= System.currentTimeMillis();

								//	Initialize timer to fire once a
								//	second to update the progress display.
		timer	=
			new Timer
			(
				ONE_SECOND / 4 ,
				new ActionListener()
				{
					public void actionPerformed( ActionEvent event )
					{
						updateProgressDisplay();
					}
				}
			);

		timer.start();
	}

	/**	Set the title.
	 *
	 *	@param	title	The title.
	 */

	public void setTitle( String title )
	{
		titleText	= title;
	}

	/** Handle Cancel button pressed. */

	protected ActionListener cancelPressed =
		new ActionListener()
		{
			public void actionPerformed( ActionEvent event )
			{
				doCancel();
			}
		};

	/** Action to take when cancel button pressed.
	 *
	 *	<p>
	 *	Override this in derived subclasses to take a specified action
	 *	when the cancel button is pressed.  The only required action
	 *	is to set the class variable "cancelled" to true.
	 *	</p>
	 */

	protected void doCancel()
	{
		cancelled = true;
	}

	/** Return cancel action listener.
	 *
	 *	@return		Action listener for cancel button.
	 *
	 *	<p>
	 *	Use to create a cancel button externally.
	 *	</p>
	 */

	public ActionListener getCancelPressedAction()
	{
		return cancelPressed;
	}

	/** Action to take when panel closed.
	 *
	 *	<p>
	 *	Override this in derived subclasses to take a specified action
	 *	when the panel is closed.
	 *	</p>
	 */

	protected void doClose()
	{
	}

	/** See if progress panel cancelled.
	 *
	 *	@return		True if cancelled, false otherwise.
	 */

	public boolean isCancelled()
	{
		return cancelled;
	}

	/** Set the startup time.
	 *
	 *	@param	startupTime		The startup time in milliseconds.
	 */

	public void setStartupTime( long startupTime )
	{
		this.startupTime = startupTime;
	}

	/** Update progress display.
	 *
	 *	@param	newBarValue		New value for progress bar.
	 *							If negative, the current value remains
	 *							unchanged.
	 *
	 *	@param	newLabelText	New label text.  If null, the current
	 *							label text is unchanged.
	 */

	public void updateProgress( int newBarValue , String newLabelText )
	{
		if ( newBarValue >= 0 ) currentBarValue	= newBarValue;
		if ( newLabelText != null ) labelText	= newLabelText;
	}

	/** Update progress display.
	 *
	 *	@param	newBarValue	New value for progress bar.
	 */

	public void updateProgress( int newBarValue )
	{
		updateProgress( newBarValue , null );
	}

	/** Update progress display.
	 *
	 *	@param	newLabelText	New label text.  If null, the current
	 *							label text is unchanged.
	 */

	public void updateProgress( String newLabelText )
	{
		updateProgress( -1 , newLabelText );
	}

	/**	Set the progress label text color.
	 *
	 *	@param	color	Text color for progress label.
	 */

	public void setLabelColor( Color color )
	{
		labelColor	= color;
	}

	/**	Set maximum bar value.
	 *
	 *	@param	maxBar	The new maximum bar value.
	 */

	public void setMaximumBarValue( int maxBar )
	{
		this.maxBar	= maxBar;
	}

	/** Get the current bar value.
	 *
	 *	@return		The current bar value.
	 */

	public int getCurrentBarValue()
	{
		return currentBarValue;
	}

	/** Get the maximum bar value.
	 *
	 *	@return		The maximum value.
	 */

	public int getMaximumBarValue()
	{
		return maxBar;
	}

	/**	Set tbe progress bar determinate/indeterminate state.
	 *
	 *	@param	isIndeterminate		true if indeterminate, false otherwise.
	 */

	public void setIndeterminate( boolean isIndeterminate )
	{
		nowIndeterminate	= isIndeterminate;
	}

	/**	Make the dialog visible or invisble.
	 *
	 *	@param	visible		true to show dialog, false to hide.
	 */

	public void makeVisible( final boolean visible )
	{
		nowVisible	= true;
	}

	/**	Close. */

	public void close()
	{
								//	Stop the timer.
		timer.stop();
								//	Perform any other needed close
								//	actions.
    	doClose();
	}

	/**	Updates the progress display.
	 *
	 *	<p>
	 *	UpdateProgress is guaranteed to run on the AWT event thread since
	 *	it is called from the timer's actionPerformed handler method.
	 *	</p>
	 */

	protected void updateProgressDisplay()
	{
		String progressText;

		if ( !nowIndeterminate )
		{
			double progressValue	= 0.0D;

			try
			{
				progressValue	= currentBarValue / ( maxBar - minBar );
			}
			catch ( Exception e )
			{
			}

			progressText	=
				(int)( progressValue * 100.0 + 0.5 ) + "%";
		}
		else
		{
			progressText	= spinChars[ spinCount++ % 4 ];
		}

		progressText	= progressText + " " + labelText;

								//	Set label text and color.

    	progressLabel.setText( progressText );
		progressLabel.setForeground( labelColor );

		progressLabel.revalidate();
		progressLabel.repaint();
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

