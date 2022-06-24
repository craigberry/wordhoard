package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */


/** PrintableContainer -- interface for window or panel components that handle
 *	printing.
 *
 *	<p>
 *	PrintableContainer marks window or panel components which know how to
 *	perform print and print preview for their contents.
 *	</p>
 */

public interface PrintableContainer
{
	/** Page setup.
	 */

	public void doPageSetup();

	/** Print Preview.
	 */

	public void doPrintPreview();

	/** Print.
	 */

	public void doPrint();
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

