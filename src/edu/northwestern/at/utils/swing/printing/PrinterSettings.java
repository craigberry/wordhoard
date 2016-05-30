package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */

import java.awt.print.*;

/** Printer Settings.
 *
 *	<p>
 *	Provides default PageFormat and PrinterJob objects for use
 *	by other classes in this print package.
 *	</p>
 */

public class PrinterSettings
{
	/** The global printer job. */

	public static PrinterJob printerJob	= PrinterJob.getPrinterJob();

	/** The global printer page format. */

	public static PageFormat pageFormat	= printerJob.defaultPage();

	/** Run printer page setup dialog. */

	public static void doPageSetup()
	{
		try
		{
			pageFormat	= printerJob.pageDialog( pageFormat );
		}
		catch ( Exception e )
		{
		}
	}

	/**	Can't instantiate but can override. */

	protected PrinterSettings()
	{
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

