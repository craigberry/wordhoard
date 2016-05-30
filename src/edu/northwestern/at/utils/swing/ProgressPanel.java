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

/** Displays progress in a panel.
 *
 * <p>
 * The progress panel an updateable label field for
 * explaining the current progress of the task and a progress bar
 * for visually displaying the current progress of the task.
 * This is essentially the same as a {@link ProgressDialog} but the
 * ProgressPanel can be embedded in another panel or window.
 * </p>
 *
 * <p>
 * A progress panel supports but does not provide a cancel button.
 * You may create a cancel button externally and invoke the progress
 * panel's "doCancel" method as the button press action.
 * Override the "doCancel" method to add any processing you need for
 * a cancel action.
 * </p>
 *
 * <p>
 * The progress panel is updated twice a second using a timer.
 * This ensures correct behavior regardless of the originating thread.
 * </p>
 */

public class ProgressPanel extends JPanel implements ProgressReporter
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

	/** Progress bar indicating how far work has progressed. */

	protected JProgressBar progressBar;

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

	/**	The timer used to update the display a one second intervals. */

	protected Timer timer;

    /**	One second in thousandths of a second. */

	public final static int ONE_SECOND = 1000;

	/** Create progress panel.
	 *
	 * @param	title			Title for progress display.
	 * @param	initLabel		Initial text of label.
	 * @param	minBar			Minimum value for progress bar.
	 * @param	maxBar			Maximum value for progress bar.
	 * @param	startupDelay	Startup delay.  Dialog will not appear
	 *							until this many milliseconds have passed
	 *							since creation of dialog.
	 */

	public ProgressPanel
	(
		String title ,
		String initLabel ,
		int minBar ,
		int maxBar ,
		long startupDelay
	)
	{
		super();
								//	Let the embedded progress report grow
								//	to match the enclosing panel.

		setLayout( new BorderLayout() );

								//	Set the background color to white.

		setBackground( Color.WHITE );

								//	Create dialog panel to hold
								//	progress report fields.

		DialogPanel panel	= new DialogPanel();

								//	Set the background color to white.

		panel.getBody().setBackground( Color.WHITE );
		panel.getButtons().setBackground( Color.WHITE );

								//	Add a little space around the
								//	progress report area.

        panel.setBorder( BorderFactory.createMatteBorder(
        	5 , 5 , 5 , 5 , Color.WHITE ) );

								//	Add progress report panel to
								//	main panel.
		add( panel );
								//	Draw a gray border line around the
								//	progress report area.

		setBorder( BorderFactory.createLineBorder( Color.GRAY ) );

								// Add title.

		titleText			= StringUtils.safeString( title );
		titleLabel			= new JLabel( titleText );

		Font font			= titleLabel.getFont();

		titleLabel.setFont(
			font.deriveFont( font.getStyle() | Font.BOLD ) );

		panel.add( titleLabel );

								// Add progress task label.

		this.labelText		= StringUtils.safeString( initLabel );
		label				= new JLabel( labelText );

		panel.add( label , 20 );

								// Add progress bar.

		progressBar			= new JProgressBar( minBar , maxBar );

		panel.add( progressBar , 20 );

								// Not cancelled yet.
		cancelled = false;
								// Set startup delay to two seconds.

		this.startupDelay	= startupDelay;

								// Set start time to now.

		startupTime			= System.currentTimeMillis();

								//	Set label size.

		Dimension labelSize	=
			new Dimension
			(
				labelWidth ,
				label.getFont().getSize()
			);

		label.setPreferredSize( labelSize );
		label.setMinimumSize( labelSize );

								//	Initialize timer to fire once a
								//	second to update the progress display.
		timer	=
			new Timer
			(
				ONE_SECOND / 2 ,
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

	/** Create progress panel with specified title and initial label.
	 *
	 * @param	title			Title for progress display.
	 * @param	initLabel		Initial text of label.
	 */

	public ProgressPanel( String title , String initLabel )
	{
		this
		(
			title ,
			initLabel ,
			0 ,
			0 ,
			defaultStartupDelay
		);
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
								//	Final update of progress display.
								//	This has to run on the AWT event
								//	thread.

		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					updateProgressDisplay();
				}
			}
		);
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
								//	Set title.

		setTitle( titleText );

								//	Set label text and color.

    	label.setText( labelText );
		label.setForeground( labelColor );

								//	Set maximum value.

		progressBar.setMaximum( maxBar );

								//	Set progress value.

		progressBar.setValue( currentBarValue );
		progressBar.setStringPainted( true );

								//	Set indeterminate mode.

		progressBar.setIndeterminate( nowIndeterminate );

								//	Show or hide progress display.
		if ( nowVisible )
		{
			if ( !isVisible() )
			{
				setVisible
				(
					( System.currentTimeMillis() - startupTime ) >
					startupDelay
				);
			}
		}
		else
		{
			setVisible( false );
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

