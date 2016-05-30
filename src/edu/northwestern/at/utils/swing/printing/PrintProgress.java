package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */

import java.awt.print.*;

/** PrintProgress -- interface for print progress updates.
 *
 *	<p>
 *	PrintProgress extends the standard Printable interface
 *	with methods to support setting the printer job,
 *	page format, and print progress display.
 *	</p>
 */

public interface PrintProgress extends Printable
{
	/** Set printer job.
	 *
	 *	@param	printerJob	The printer job used for printing.
	 */

	public void setPrinterJob( PrinterJob printerJob );

	/** Set page format.
	 *
	 *	@param	pageFormat	The page format used for printing.
	 */

	public void setPageFormat( PageFormat pageFormat );

	/** Enable or disable print progress.
	 *
	 *	@param	onOff	True to enable print progress, false to disable.
	 */

	public void setProgress( boolean onOff );

	/** Update the print progress display with pages printed.
	 *
	 *	@param	pagesPrinted	The number of pages printed.
	 */

	public void updateProgress( int pagesPrinted );

	/** Close progress dialog. */

	public void closeProgress();
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

