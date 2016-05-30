package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.Color;

/**	Interface for classes which report progress. */

public interface ProgressReporter
{
	/** See if progress display cancelled.
	 *
	 *	@return		True if cancelled, false otherwise.
	 */

	public boolean isCancelled();

	/** Set the startup time.
	 *
	 *	@param	startupTime		The startup time in milliseconds.
	 */

	public void setStartupTime( long startupTime );

	/** Update progress display.
	 *
	 *	@param	barValue	New value for progress bar.
	 *						If negative, the current value remains unchanged.
	 *
	 *	@param	newLabel	New label text.  If null, the current
	 *						label text is unchanged.
	 */

	public void updateProgress( int barValue , String newLabel );

	/** Update progress display.
	 *
	 *	@param	barValue	New value for progress bar.
	 */

	public void updateProgress( int barValue );

	/** Update progress display.
	 *
	 *	@param	newLabel	New label text.  If null, the current
	 *						label text is unchanged.
	 */

	public void updateProgress( String newLabel );

	/**	Set the title.
	 *
	 *	@param	title	The title.
	 */

	public void setTitle( String title );

	/**	Set the progress label text color.
	 *
	 *	@param	color	Text color for progress label.
	 */

	public void setLabelColor( Color color );

	/**	Set maximum bar value.
	 *
	 *	@param	maxBar	The new maximum bar value.
	 */

	public void setMaximumBarValue( int maxBar );

	/** Get the current bar value.
	 *
	 *	@return		The current bar value.
	 */

	public int getCurrentBarValue();

	/** Get the maximum bar value.
	 *
	 *	@return		The maximum value.
	 */

	public int getMaximumBarValue();

	/**	Set tbe progress bar determinate/indeterminate state.
	 *
	 *	@param	isIndeterminate		true if indeterminate, false otherwise.
	 */

	public void setIndeterminate( boolean isIndeterminate );

	/**	Make the dialog visible or invisble.
	 *
	 *	@param	visible		true to show dialog, false to hide.
	 */

	public void makeVisible( final boolean visible );

	/** Close the progress display. */

	public void close();
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

