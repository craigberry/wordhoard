package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

/**	CutCopyPaste -- interface for objects implementing clipboard
 *	cut, copy, and paste actions.
 */

public interface CutCopyPaste
{
	/**	Cut to clipboard. */

	public void cut();

	/**	Copy to clipboard. */

	public void copy();

	/**	Paste from clipboard. */

	public void paste();

	/**	Is cut enabled?
	 * @return boolean indicating whether cut is enabled.
	*/

	public boolean isCutEnabled();

	/**	Is copy enabled?
	 * @return boolean indicating whether copy is enabled.
	*/

	public boolean isCopyEnabled();

	/**	Is paste enabled?
	 * @return boolean indicating whether paste is enabled.
	*/

	public boolean isPasteEnabled();

	/**	Is anything selected which can be cut/copied?
	 * @return boolean indicating whether text is selected.
	*/

	public boolean isTextSelected();
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

