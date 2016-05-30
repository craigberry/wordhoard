package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

/**	Interface for a task which is monitored by a ProgressMonitorDialog.
 */

public interface ProgressMonitorTask
{
	/**	Get task length.
	 *
	 *	@return		Task length in units.
	 *			If -1, task length is currently unknown.
	 */

	public int getLength();

	/**	Get amount of task currently done.
	 *
	 *	@return		Amount of task currently done in units.
	 *			If -1, amount is currently unknown.
	 */

	public int getCurrent();

	/**	Check if task is finished.
	 *
	 *	@return		true if task finished.
	 */

	public boolean isFinished();

	/**	Get most recent task message.
	 *
	 *	@return		Most recent task message.
	 *			Null means no message.
	 */

	public String getMessage();
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

