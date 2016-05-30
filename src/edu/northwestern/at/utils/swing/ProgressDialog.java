package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.print.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;

import javax.swing.*;

import edu.northwestern.at.utils.*;

/** Displays a progress dialog.
 *
 * <p>
 * The progress dialog has a title, an updateable label field for
 * explaining the current progress of the task, a progress bar
 * for visually displaying the current progress of the task,
 * and an optional cancel button for cancelling the task.
 * </p>
 *
 * <p>
 * By default the cancel button does nothing except to close the
 * status dialog.  Override the "doCancel" method to add
 * any processing you need for a cancel action.
 * </p>
 *
 * <p>
 * The progress dialog should always be attached to a process
 * running in a separate thread.  The progress dialog will
 * not be updated correctly by Swing unless it is executing
 * in a separate thread.
 * </p>
 */

public class ProgressDialog extends ModalDialog implements ProgressReporter
{
	/** Minimum value for progress bar. */

	protected int minBar;

	/** Maximum value for progress bar. */

	protected int maxBar;

	/** Label for explanatory message. */

	protected JLabel label;

	/**	The initial value of the explanatory message. */

	protected String initLabel;

	/** Progress bar indicating how far work has progressed. */

	protected JProgressBar progressBar;

	/** Current progress bar value. */

	protected int currentBarValue;

	/** True if progress dialog cancelled. */

	protected boolean cancelled = false;

	/** Default startup delay = 1.5 seconds. */

	protected static long defaultStartupDelay = 1500;

	/** How many milliseconds to wait before displaying dialog. */

	protected long startupDelay;

	/** Startup time for dialog. */

	protected long startupTime;

	/**	Parent window for progress dialog. */

	protected Window parentWindow;

	/**	Nice wide label size. */

	protected int labelWidth	= 300;

	/** Create progress dialog.
	 *
	 * @param	parentWindow	The parent window.
	 * @param	title			Title for progress dialog.
	 * @param	initLabel		Initial text of label.
	 * @param	minBar			Minimum value for progress bar.
	 * @param	maxBar			Maximum value for progress bar.
	 * @param	allowCancel		Display a cancel button.
	 * @param	startupDelay	Startup delay.  Dialog will not appear
	 *							until this many milliseconds have passed
	 *							since creation of dialog.
	 */

	public ProgressDialog
	(
		Window parentWindow ,
		String title ,
		String initLabel ,
		int minBar ,
		int maxBar ,
		boolean allowCancel ,
		long startupDelay
	)
	{
		super( title );

		this.parentWindow	= parentWindow;

								// Add label to dialog.

		this.initLabel	= initLabel;

		label			=
			new XSmallLabel( StringUtils.safeString( initLabel ) );

		add( label );
								// Add progress bar to dialog.

		progressBar	= new JProgressBar( minBar , maxBar );

		add( progressBar , 20 );

								// Add Cancel button to dialog.
		cancelled = false;

		if ( allowCancel )
		{
			addDefaultButton( "Cancel" , cancelPressed );
		}
								// Ignore attempts to close dialog.

		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );

								// Set startup delay to two seconds.

		this.startupDelay = startupDelay;

								// Set start time to now.

		startupTime = System.currentTimeMillis();

								//	Set label size.

		Dimension labelSize	=
			new Dimension
			(
				labelWidth ,
				label.getFont().getSize()
			);

		label.setPreferredSize( labelSize );
		label.setMinimumSize( labelSize );

                                // Pack the dialog.
		pack();

        						// Not a modal dialog.
		setModal( false );
								// Not resizable.
		setResizable( false );
	}

	/** Create progress dialog.
	 *
	 * @param	title			Title for progress dialog.
	 * @param	initLabel		Initial text of label.
	 * @param	minBar			Minimum value for progress bar.
	 * @param	maxBar			Maximum value for progress bar.
	 * @param	allowCancel		Display a cancel button.
	 */

	public ProgressDialog
	(
		String title ,
		String initLabel ,
		int minBar ,
		int maxBar ,
		boolean allowCancel ,
		long startupDelay
	)
	{
		this
		(
			null ,
			title ,
			initLabel ,
			minBar ,
			maxBar ,
			allowCancel ,
			startupDelay
		);
	}

	/** Create progress dialog.
	 *
	 * @param	title			Title for progress dialog.
	 * @param	initLabel		Initial text of label.
	 * @param	minBar			Minimum value for progress bar.
	 * @param	maxBar			Maximum value for progress bar.
	 * @param	allowCancel		Display a cancel button.
	 */

	public ProgressDialog
	(
		String title ,
		String initLabel ,
		int minBar ,
		int maxBar ,
		boolean allowCancel
	)
	{
		this
		(
			null ,
			title ,
			initLabel ,
			minBar ,
			maxBar ,
			allowCancel ,
			defaultStartupDelay
		);
	}

	/** Create progress dialog.
	 *
	 * @param	parentWindow	The parent window.
	 * @param	title			Title for progress dialog.
	 * @param	initLabel		Initial text of label.
	 * @param	minBar			Minimum value for progress bar.
	 * @param	maxBar			Maximum value for progress bar.
	 * @param	allowCancel		Display a cancel button.
	 */

	public ProgressDialog
	(
		Window parentWindow ,
		String title ,
		String initLabel ,
		int minBar ,
		int maxBar ,
		boolean allowCancel
	)
	{
		this
		(
			parentWindow ,
			title ,
			initLabel ,
			minBar ,
			maxBar ,
			allowCancel ,
			defaultStartupDelay
		);
	}

	/**	Set the title.
	 *
	 *	@param	title	The title.
	 */

	public void setTitle( String title )
	{
		super.setTitle( title );
	}

	/** Handle Cancel button pressed. */

	protected ActionListener cancelPressed =
		new ActionListener()
		{
			public void actionPerformed( ActionEvent event )
			{
				doCancel();
				dispose();
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

	/** See if progress dialog cancelled.
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
	 *	@param	barValue	New value for progress bar.
	 *						If negative, the current value remains unchanged.
	 *
	 *	@param	newLabel	New label text.  If null, the current
	 *						label text is unchanged.
	 */

	public void updateProgress( int barValue , String newLabel )
	{
		if ( ( System.currentTimeMillis() - startupTime ) > startupDelay )
		{
			final int finalBarValue		= barValue;
			final String finalNewLabel	= newLabel;

			SwingUtilities.invokeLater
			(
				new Runnable()
				{
					public void run()
					{
						if ( !isVisible() )
						{
							WindowPositioning.centerWindowOverWindow(
								ProgressDialog.this , parentWindow , 25 );

							setVisible( true );
						}

						if ( finalBarValue >= 0 )
						{
							if ( currentBarValue != finalBarValue )
							{
								progressBar.setValue( finalBarValue );
							}

							currentBarValue = finalBarValue;
						}

						if ( finalNewLabel != null )
						{
							String newLabelText	=
								StringUtils.safeString( finalNewLabel );

							if ( !label.getText().equals( newLabelText ) )
							{
								label.setText( newLabelText );
								label.revalidate();
								label.repaint();
							}
						}

						progressBar.setStringPainted( true );
						ProgressDialog.this.paintImmediately();
					}
				}
			);
		}
	}

	/** Update progress display.
	 *
	 *	@param	barValue	New value for progress bar.
	 */

	public void updateProgress( int barValue )
	{
		updateProgress( barValue , null );
	}

	/** Update progress display.
	 *
	 *	@param	newLabel	New label text.  If null, the current
	 *						label text is unchanged.
	 */

	public void updateProgress( String newLabel )
	{
		updateProgress( -1 , newLabel );
	}

	/**	Set the progress label text color.
	 *
	 *	@param	color	Text color for progress label.
	 */

	public void setLabelColor( Color color )
	{
		final Color finalColor	= color;

		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					label.setForeground( finalColor );
					label.revalidate();
					label.repaint();
				}
			}
		);
	}

	/**	Set maximum bar value.
	 *
	 *	@param	maxBar	The new maximum bar value.
	 */

	public void setMaximumBarValue( int maxBar )
	{
		this.maxBar	= maxBar;

		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					progressBar.setMaximum( ProgressDialog.this.maxBar );
				}
			}
		);
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
		final boolean finalIsIndeterminate	= isIndeterminate;

		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					progressBar.setIndeterminate( finalIsIndeterminate );
				}
			}
		);
	}

	/**	Make the dialog visible or invisble.
	 *
	 *	@param	visible		true to show dialog, false to hide.
	 */

	public void makeVisible( final boolean visible )
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					if ( visible )
					{
						if ( !isVisible() )
						{
							WindowPositioning.centerWindowOverWindow(
								ProgressDialog.this , parentWindow , 25 );

							setVisible( true );
						}
					}
					else
					{
						setVisible( false );
					}
				}
			}
		);
	}

	/** Close the progress dialog. */

	public void close()
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					dispose();
				}
			}
		);
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

