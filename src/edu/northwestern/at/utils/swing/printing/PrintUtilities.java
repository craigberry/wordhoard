package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.awt.print.*;

import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;

/** Print Utilities.
 *
 *	<p>
 *	This class collects together a variety of methods useful when printing.
 *	These include the printPreview method which performs a print
 *	preview, and the printComponent method which prints any
 *	Swing component.  If the component defines the Printable, Pageable,
 *	or PrintProgress interface, PrintComponent uses that to print the
 *	component.  If the component does not define any type of printable
 *	interface, a PrintAdapter is used to "paint" a snapshot of the component
 *	to the printer.
 *	</p>
 */

public class PrintUtilities
{
	/**	Printer exception. */

	private static PrinterException printerException;

	/** Calculate number of pages required to print a printable object.
	 *
	 *	@param	printable			The printable object.
	 *								Must implement Printable interface.
	 *
	 *	@param	pageFormat			The page format to use when laying
	 *								out the pages.
	 *
	 *	@param	wPage				Page width, which must be less than
	 *								or equal to the printer page width
	 *								specified by the pageFormat.
	 *
	 *	@param	hPage				Page height, which must be less than
	 *								or equal to the printer page height
	 *								specified by the pageFormat.
	 *
	 *	@param	showProgress		true to display print progress dialog.
	 *
	 *	@return	The number of pages required to print the printable object.
	 *
	 *	<p>
	 *	The printable's "print" method is called to "print" each page
	 *	into a buffered image which is the same size as the specified
	 *	printer page (e.g., width=wPage, height=hPage).  The number of
	 *	pages is determined by the highest page index for which the printable
	 *	object returns "Printable.PAGE_EXISTS".
	 *	</p>
	 *
	 *	<p>
	 *	Normally the width and height of the printer page is provided by
	 *	the pageFormat object.  However, for some systems (e.g., Windows)
	 *	it may be necessary to restrict the page size to be be smaller
	 *	than that indicated by pageFormat in order to prevent clipping.
	 *	</p>
	 */

	public static int getPageCount
	(
		Printable printable,
		PageFormat pageFormat,
		int wPage,
		int hPage,
		boolean showProgress
	)
	{
								// Make sure specified print height and width
								// are less than or equal to height and width
								// specified by pageFormat.

		wPage = Math.min( wPage , (int)(pageFormat.getWidth()) );
		hPage = Math.min( hPage , (int)(pageFormat.getHeight()) );

								// Counts the number of pages to print.
		int pageIndex = 0;
                    			// Set up a repagination dialog.

		RepaginationProgressDialog progressDialog = null;

		if ( showProgress )
		{
			progressDialog = new RepaginationProgressDialog();
			progressDialog.setVisible( true );
		}
								// Get a buffer big enough to hold the image
								// for a page.

		BufferedImage pageImage =
			new BufferedImage(
				wPage,
				hPage,
				BufferedImage.TYPE_INT_RGB );

                                // Get the graphics context for the buffer.

		Graphics g = pageImage.getGraphics();

								// "Print" each page to buffer and count number
								// of pages.
		try
		{
			while ( true )
			{
								// Ask the printable object to print the image
								// of the next page to the buffered graphics
								// context.
								//
								// Quit if there are no more pages to print.

				if	( printable.print( g , pageFormat, pageIndex ) !=
						Printable.PAGE_EXISTS )
					{
						break;
					}
								// Increment the count of printable pages.

					pageIndex++;

								// Update progress display.

					if ( progressDialog != null )
					{
						progressDialog.updateProgress( pageIndex );

						if ( progressDialog.isCancelled() )
						{
							throw new PrinterAbortException(
								"Pagination cancelled, not all pages will be printed." );
						}
					}
			}
		}
		catch ( PrinterException e )
		{
								// Dispose of progress dialog.
			if ( showProgress )
			{
				progressDialog.close();
				progressDialog = null;
			}
								// $$$PIB$$$ Should probably throw a better
								// exception.
			new ErrorMessage(
				Resources.get
				(
					"Paginationerror" ,
					"Pagination error: "
				) + e.toString() );

								// Return -1 as # of pages when pagination
								// cancelled.
			pageIndex = -1;
		}
								// Dispose of graphics buffer.
		pageImage = null;
								// Dispose of progress dialog.

		if ( progressDialog != null )
		{
			progressDialog.close();
			progressDialog = null;
		}
								// Return count of pages to print.
		return pageIndex;
	}

	/** Calculate number of pages required to print a printable object.
	 *
	 *	@param	printable			The printable object.
	 *								Must implement Printable interface.
	 *
	 *	@param	pageFormat			The page format to use when laying
	 *								out the pages.
	 *
	 *	@return	The number of pages required to print the printable object.
	 */

	public static int getPageCount
	(
		Printable printable,
		PageFormat pageFormat
	)
	{
		return getPageCount(
			printable,
			pageFormat,
			(int)(pageFormat.getWidth()),
			(int)(pageFormat.getHeight()),
			true );
	}

	/** Enable or disable double buffering for a component to be printed.
	 *
	 *	@param	component	The component.
	 *	@param	onOff		True to enable double buffering, false to disable.
	 *
	 *	<p>
	 *	Disabling double buffering while printing a component improves
	 *	print speed and quality for some versions of Java.
	 *	</p>
	 */

	public static void setDoubleBuffering( Component component , boolean onOff)
	{
		try
		{
			RepaintManager.currentManager( component ).setDoubleBufferingEnabled( onOff );
		}
		catch( Exception e )
		{
		}
	}

	/** Get scaling factor for a component or view.
	 *
	 *	@param	viewWidth			Width of component/view.
	 *
	 *	@param	viewHeight			Height of component/view.
	 *
	 *	@param	printerPageWidth	Width of printable printer page area.
	 *
	 *	@param	printerPageHeight	Height of printable printer page area.
	 *								Note: this is typically smaller than
	 *								the actual printer page size because
	 *								some space may be taken up by the header
	 *								and/or footer.
	 *
	 *	@return						The scaling factor.
	 *								= 1.0 if no scaling required.
	 *								&lt; 1.0 if scaling required.
	 *
	 *	<p>
	 *	The scaling factor is the value to by which multiply the view size
	 *	so that the printed version of the view fits within the
	 *	confines of a single printed page.  Images are typical examples
	 *	of views whose dimensions may exceed the printer page size.
	 *	The scaling factor calculated here provides the value by which
	 *	to resize the view to fit the printer page size.
	 *	</p>
	 *
	 *	<p>
	 *	A scaling factor &gt; 1.0 is never return since such a scale
	 *	factor means no scaling is needed.
	 *	</p>
	 */

	public static double getViewScaleFactor
	(
		int viewWidth ,
		int viewHeight ,
		int printerPageWidth ,
		int printerPageHeight
	)
	{
								// Compute the scaling factors
								// relative to the print page
								// width and height.

		double wScale	= ( printerPageWidth + 0.0 ) / viewWidth;
		double hScale	= ( printerPageHeight + 0.0 ) / viewHeight;

		double result 	= hScale;

								// Take the smaller of the
								// two scale values as the one to
								// use.

		if ( wScale < hScale ) result = wScale;

								// If no scaling needed, return scale
								// value of 1.0 .
		if ( result >= 1.0 )
		{
			result = 1.0;
		}
								// Reduce size a bit further.  90% seems
								// to work well on Windows.
		else
		{
			result = result * .90;
        }

		return result;
	}

	/** Print a component.
	 *
	 *	@param	componentToPrint	The component to print.
	 *
	 *	@param	title				The page title for the output.
	 *
	 *	@param	printerJob			The printer job to use.
	 *								If null, PrinterSettings.printerJob is used.
	 *
	 *	@param	pageFormat			The page format to use.
	 *								If null, PrinterSettings.pageFormat is used.
	 *
	 *	@param	showPrinterDialog	True to show a printer dialog.
	 */

	public static synchronized void printComponent
	(
		Component	componentToPrint,
		String		title,
		PrinterJob	printerJob,
		PageFormat	pageFormat,
		boolean		showPrinterDialog
	)
	{
								// If component null do nothing.

		if ( componentToPrint == null ) return;

		Component component = componentToPrint;

								// Assume print fails.

		boolean printedOK = false;

		try
		{
								// Set up a printer job.

			if	( printerJob == null )
			{
				printerJob	= PrinterSettings.printerJob;
				pageFormat	= PrinterSettings.pageFormat;
			}
								// If no page setup yet performed, get default
								// page setup for the current printer.

			if ( pageFormat == null )
			{
				pageFormat = printerJob.defaultPage();
            }
								// If component to print is of type
								// PrintableContents, get the associated
								// printable component.

			if ( componentToPrint instanceof PrintableContents )
			{
				component =
					((PrintableContents)componentToPrint).getPrintableComponent(
						title , pageFormat );
			}
			else
			{                   // Check if the component is printable.
								// If not, create a printable wrapper.

				boolean isPrintable =
					( componentToPrint instanceof Pageable ) ||
					( componentToPrint instanceof Printable ) ||
					( componentToPrint instanceof PrintProgress );

				if ( !isPrintable )
				{

					component =
						new PrintableComponent
						(
							componentToPrint,
							pageFormat ,
							new PrintHeaderFooter
							(
								title ,
								"Printed " +
									DateTimeUtils.formatDateOnAt( new Date() ),
								"Page "
							)
						);
				}
			}
								// Disable print progress display.

			if ( component instanceof PrintProgress )
			{
				((PrintProgress)component).setPrinterJob( printerJob );
				((PrintProgress)component).setPageFormat( pageFormat );
				((PrintProgress)component).setProgress( false );
			}
								// If printable is pageable, set up a book
								// to hold the pages.

			if ( component instanceof Pageable )
			{
            					// Create book to hold printable page images.

				Book book = new Book();

								// Calculate number of pages to print.

				int pageCount = ((Pageable)component).getNumberOfPages();

								// Indicate page images will come from this
								// component.

				try
				{
					book.append(
						(Printable)component , pageFormat , pageCount );
				}
				catch ( Exception e )
				{
				}

				printerJob.setPageable( book );
			}
			else if ( component instanceof Printable )
			{
				printerJob.setPrintable( (Printable)component , pageFormat );
			}
                                // Display the system-dependent printer dialog
                                // and see if printing is OK to do.

			if ( showPrinterDialog )
			{
				if ( !printerJob.printDialog() ) return;
			}
								// Change cursor to wait cursor.

			componentToPrint.setCursor(
				Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );

								// Disable double buffering for printable
								// component.

			PrintUtilities.setDoubleBuffering( componentToPrint , false );

								// Enable print progress.

			if ( component instanceof PrintProgress )
			{
				((PrintProgress)component).setPrinterJob( printerJob );
				((PrintProgress)component).setPageFormat( pageFormat );
				((PrintProgress)component).setProgress( !Env.MACOSX );
			}
                                // Print the document.

			if ( Env.MACOSX )
			{
				//	On Mac OS X printing we have to run the print job on the
				//	AWT event thread. If we don't do this, the print job hangs
				//	and we have to force quit the program.

				printerException					= null;

				final PrinterJob finalPrinterJob	= printerJob;

				SwingRunner.runNow
				(
					new Runnable()
					{
						public void run()
						{
							try
							{
								finalPrinterJob.print();
							}
							catch ( PrinterException e )
							{
								printerException = e;
							}
						}
					}
				);

				if ( printerException != null ) throw( printerException );
			}
			else
			{
				printerJob.print();
			}
                                // Printing went OK.

			printedOK = true;
		}

		catch ( PrinterException e )
		{

                                // Printing failed or was cancelled by user.
			printedOK = false;
		}

		finally
		{
								// Change back to normal cursor.

			componentToPrint.setCursor(
				Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );

								// Enable double buffering for printable
								// component.

			PrintUtilities.setDoubleBuffering( componentToPrint , true );

								// Dispose of print progress dialog.

			if ( component instanceof PrintProgress )
				((PrintProgress)component).closeProgress();
		}
								// Show informative or error message.
		if ( printedOK )
		{
//			if ( !Env.MACOSX )
//				new InformationMessage( "Printing completed successfully." );
		}
		else
		{
			new ErrorMessage( "Printing aborted." );
		}
	}

	/** Create print preview for a printable object.
	 *
	 *	@param	printableObject		The printable object.
	 *								Must implement Printable interface.
	 */

	public static void printPreview( Component printableObject )
	{
		PrintUtilities.printPreview(
			printableObject , PrinterSettings.pageFormat , "Print Preview" );
	}

	/** Create print preview for a printable object.
	 *
	 *	@param	printableObject		The printable object.
	 *								Must implement Printable interface.
	 *
	 *	@param	pageFormat			The page format to use when laying
	 *								out the preview.
	 */

	public static void printPreview
	(
		Component printableObject ,
		PageFormat pageFormat
	)
	{
		PrintUtilities.printPreview(
			printableObject , pageFormat , "Print Preview" );
	}

	/** Create print preview for a printable object with a specified title.
	 *
	 *	@param	printableObject		The printable object.
	 *								Must implement Printable interface.
	 *
	 *	@param	title				Title for print preview.
	 */

	public static void printPreview
	(
		final Component printableObject ,
		final String title
	)
	{
		doPrintPreview( printableObject , PrinterSettings.pageFormat , title );
	}

	/** Create print preview for a printable object with a specified title.
	 *
	 *	@param	printableObject		The printable object.
	 *								Must implement Printable interface.
	 *
	 *	@param	pageFormat			The page format to use when laying
	 *								out the preview.
	 *
	 *	@param	title				Title for print preview.
	 */

	public static void printPreview
	(
		final Component printableObject ,
		final PageFormat pageFormat ,
		final String title
	)
	{
/*
 		Thread runner = new Thread( "Print preview" )
        {
			public void run()
			{
				doPrintPreview( printableObject, pageFormat, title );
			}
		};

		runner.start();
*/
		doPrintPreview( printableObject, pageFormat, title );
	}

	/** Helper for printPreview.
	 * @param printableObject The printable object.
	 * @param pageFormat The page format.
	 * @param title The title.
	*/

	protected static void doPrintPreview
	(
		final Component printableObject ,
		final PageFormat pageFormat ,
		final String title
	)
	{
		new PrintPreview( printableObject, pageFormat, title );
	}

	/** Can't instantiate but can override. */

	protected PrintUtilities()
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

