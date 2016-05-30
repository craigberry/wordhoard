package edu.northwestern.at.wordhoard.swing.accounts;

/*	Please see the license information at the end of this file. */

/**	An account listener.
 *
 *	<p>The listener interface for receiving account model state change 
 *	events. See
 *	{@link edu.northwestern.at.wordhoard.swing.accounts.AccountModel
 *	AccountModel} for details.
 */

interface AccountListener {

	/**	Invoked when the selection changes.
	 *
	 *	@param	selection		Array of indexes of selected accounts.
	 */
	 
	public void selectionChanged (int[] selection);
	
	/**	Requests that an account be selected.
	 *
	 *	@param	index			Index of account to be selected.
	 */
	 
	public void selectAccount (int index);
	
	/**	Requests that the selection be cleared.
	 */
	 
	public void clearSelection ();

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

