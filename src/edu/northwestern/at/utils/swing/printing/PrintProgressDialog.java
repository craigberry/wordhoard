package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */

import java.awt.print.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;

/** Print progress dialog.
 *
 *	<p>
 *	Displays a non-modal dialog entitled "Printing" which
 *	displays the progress of a print operation.  A label
 *	displaying the number of pages printed so far takes
 *	the form:  Printing page n of t where "n" is page just
 *	printed and "t" is the total number of pages to print.
 *	A progress bar gives visual feedback of the printing progress.
 *	</p>
 *
 *	<p>
 *	A cancel button allows the print operation to be cancelled.
 *	</p>
 */

public class PrintProgressDialog extends ProgressDialog
{
	/** Printer job for which to display progress. */

	protected PrinterJob printerJob;

	/** How many pages to print.  */

	protected int pageCount;

	/** Create print progress dialog.
	 *
	 *	@param	printerJob	The printer job for which to display
	 *						the progress.
	 *
	 *	@param	pageCount	The number of pages which will be
	 *						printed.
	 */

	public PrintProgressDialog( PrinterJob printerJob , int pageCount )
	{
		super
		(
			"Printing" ,
			"Printing                               " ,
			0 ,
			pageCount ,
			true
		);

		this.printerJob = printerJob;
		this.pageCount	= pageCount;

		updateProgress( 0 );
	}

	/** Action to take when cancel button pressed.
	 */

	protected void doCancel()
	{
		printerJob.cancel();
	}

	/** Update print progress.
	 *
	 *	@param	pageCountPrinted	Number of pages printed,
	 *								starting at 1.
	 */

	public void updateProgress( int pageCountPrinted )
	{
		final int finalPageCountPrinted	= pageCountPrinted;

		if ( finalPageCountPrinted > 0 )
		{
			SwingUtilities.invokeLater
			(
				new Runnable()
				{
					public void run()
					{
						progressBar.setValue( finalPageCountPrinted );

						label.setText(
							"Printing page " + finalPageCountPrinted +
							" of " + pageCount + "     " );

						progressBar.setStringPainted( true );

						progressBar.setVisible( true );
						label.setVisible( true );
					}
				}
			);
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

