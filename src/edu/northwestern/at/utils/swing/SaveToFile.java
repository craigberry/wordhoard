package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;

/**	SaveToFile -- interface for objects which know how to save
 *	their contents to a file.
 */

public interface SaveToFile
{
	/**	Save to a named file.
	 *
	 *	@param	fileName	Name of file to which to save results.
	 */

	public void saveToFile( String fileName );

	/**	Save to a file using a file dialog.
	 *
	 *	@param	parentWindow	Parent window for file dialog.
	 *
	 *	<p>
	 *	Runs a file dialog to get the name of the file to which to
	 *	save results.
	 *	</p>
	 */

	public void saveToFile( Window parentWindow );
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

