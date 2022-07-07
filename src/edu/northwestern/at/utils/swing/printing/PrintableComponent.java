package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.image.*;


/**	Base class for printing contents of a Component descendant.
 *
 *	<p>
 *	This class provides basic machinery for creating a printing class
 *	for a specified type of component.  Printing will be Pageable
 *	and allow for a progress display.  Optional page headers and footers
 *	can be printed on each page as well.  Any type of component
 *	descending from the AWT type component can be printed as long as
 *	the component knows how to print itself.
 *	</p>
 */

public class PrintableComponent extends Component
	implements Pageable, PrintProgress
{
	/** Class holding information about each printed page. */

	protected class PageState
	{
		/** Index of this page. */

		public int pageIndex;

		/** Starting offset of slice for this page. */

		public int pageOffset;

		/** Length of slice for this page. */

		public int pageLength;

		/** Create page state object.
		 * @param	pageIndex	Index of page.
		 * @param	pageOffset	Starting offset of slice for the page.
		 * @param	pageLength	Length of the page.
		*/

		public PageState
		(
			int pageIndex,
			int pageOffset,
			int pageLength
		)
		{
			this.pageIndex		= pageIndex;
			this.pageOffset		= pageOffset;
			this.pageLength		= pageLength;
		}

		/** Create page state object from another page state object.
		 * @param	otherPageState	Page state to use as a template.
		*/

		public PageState
		(
			PageState otherPageState
		)
		{
			this.pageIndex		= otherPageState.pageIndex;
			this.pageOffset		= otherPageState.pageOffset;
			this.pageLength		= otherPageState.pageLength;
		}
	}

	/** Compoment whose contents are to be printed. */

	protected Component component;

	/** Printer page format. */

	protected PageFormat pageFormat;

	/** The PrintHeaderFooter to hold the header and footer. */

	protected PrintHeaderFooter headerAndFooter;

	/** The number of pages to be printed.  Calculated here. */

	protected int pageCount;

	/** Print progress dialog. */

	protected PrintProgressDialog printProgress;

	/** True if print progress display allowed. */

	protected boolean printProgressAllowed;

	/** Printer job for printing the pages. */

	protected PrinterJob printerJob;

	/** Printer page width specified by printer job. */

	protected int printerPageWidth;

	/** Printer page height specified by printer job. */

	protected int printerPageHeight;

	/** Holds printed page page image. */

	protected BufferedImage pageImage = null;

	/** Current page state. */

	protected PageState pageState;

	/** Holds page state for each printed page. */

	protected HashMap pageStateMap = new HashMap();

	/** Percentage of page height to scan for nice page break point. */

	protected double breakCheckPercentage = 0.20;
//	protected double breakCheckPercentage = 0.0;

	/** Scale factor by which to reduce output to fit printer page. */

	protected double scaleFactor = 1.0;

	/** Create PrintableComponent object.
	 *
	 *	@param	component			Component to print.
	 *	@param	pageFormat			The printer page format.
	 *	@param	headerAndFooter	    The header and footer for this page.
	 */

	public PrintableComponent
	(
		Component component,
		PageFormat pageFormat,
		PrintHeaderFooter headerAndFooter
	)
	{
		this.component		= component;
		this.pageFormat		= pageFormat;

		if ( headerAndFooter != null )
		{
			this.headerAndFooter = headerAndFooter;
		}
		else
		{
			this.headerAndFooter = new PrintHeaderFooter( null, null, null );
		}

		this.printProgress	= null;
		this.printerJob		= null;
		this.pageCount		= -1;
	}

	/** Create PrintableComponent object.
	 *
	 *	@param	component	The component whose contents are to be printed.
	 *	@param	pageFormat	The printer page format.
	 *
	 *	<p>
	 *	No header, footer, or line numbers are printed.
	 *	</p>
	 */

	public PrintableComponent
	(
		Component component,
		PageFormat pageFormat
	)
	{
		this( component, pageFormat, null );
	}

	/** Create PrintableComponent object.
	 *
	 *	@param	component	The component whose contents are to be printed.
	 *
	 *	<p>
	 *	No header, footer, or line numbers are printed.
	 *	</p>
	 */

	public PrintableComponent
	(
		Component component
	)
	{
		this( component, PrinterSettings.pageFormat, null );
	}

	/** Validate component layout.
	 */

	public void validateLayout()
	{
		component.validate();
	}

	/** Print the contents of the JComponent in a separate thread.
	 *
	 *	<p>
	 *	We need to print the component contents in a separate thread so that
	 *	the print progress dialog will work.
	 *	</p>
	 */

	public void printContents()
	{
/*
 		Thread runPrintContents =
 			new Thread("Print contents")
 			{
				public void run()
   				{
					doPrintContents();
   				}
 			};

 		runPrintContents.start();
*/
		doPrintContents();
	}

	/** Print the contents of the JComponent.
	 *
	 *	<p>
	 *	You can call this method instead of printContents() if you
	 *	are setting up your own printing thread.
	 *	</p>
	 */

	public void doPrintContents()
	{
								// Print the component contents.

		PrintUtilities.printComponent( this, "", printerJob, pageFormat, true );
	}

	/** Get header size needed.
	 *
	 *	@param		pg				The graphics context in which to print
	 *								the header.
	 *
	 *	@return						Vertical size of header.
	 *
	 *	<p>
	 *	Normally the header size is just the size of the
	 *	header, if any, plus an extra blank line (in the same font
	 *	size as the header).  For some components the header space may be
	 *	larger.  For example, for a JTable, the header space is increased
	 *	by the amount of space required to print the table column headers.
	 *	</p>
	 */

	public int getHeaderSize( Graphics pg )
	{
		int headerFontSize = headerAndFooter.getHeaderFontSize( pg );

		if ( headerAndFooter.doPrintHeader() )
		{
			return 2 * headerFontSize;
		}
		else
		{
			return 0;
		}
	}

	/** Get footer size needed.
	 *
	 *	@param		pg				The graphics context in which to print
	 *								the header.
	 *
	 *	@return						Vertical size of footer.
	 *
	 *	<p>
	 *	Normally the footer space needed is just the size of the
	 *	footer, if any, plus an extra blank line (in the same font
	 *	size as the header).
	 *	</p>
	 */

	public int getFooterSize( Graphics pg )
	{
		int footerFontSize = headerAndFooter.getFooterFontSize( pg );

		if ( headerAndFooter.doPrintFooter() )
		{
			return 2 * footerFontSize;
		}
		else
		{
			return 0;
		}
	}

	/** Print the header and footer.
	 *
	 *	@param		pg2D				The graphics context into which to
	 *									print the header and footer, if any.
	 */

	public void printHeaderAndFooter( Graphics2D pg2D )
	{
								// Reset clipping region to whole printer page.
		pg2D.setClip
		(
			0,
			0,
			printerPageWidth,
			printerPageHeight
		);
								// Print header and footer.

		int headerSize		= getHeaderSize( pg2D );
		int footerFontSize	= headerAndFooter.getFooterFontSize( pg2D );

		headerAndFooter.printHeaderAndFooter
		(
			pg2D ,
			pageState.pageIndex ,
			printerPageWidth ,
			printerPageHeight - (int)(footerFontSize * 1.5 )
		);
	}

	/** Print one page of component.
	 *
	 *	@param	pg			Graphics context into which to draw page image.
	 *	@param	pageFormat	Contains information about page size and paper
	 *						orientation.
	 *	@param	pageIndex	Which page to print (starts at zero).
	 *
	 *	@return				PAGE_EXISTS if page exists
	 *						NO_SUCH_PAGE if page does not exist
	 *
	 *	<p>
	 *	A printer page size chunk of the component is
	 *	printed for the specified page index.
	 *	</p>
	 */

	public int print( Graphics pg, PageFormat pageFormat, int pageIndex )
	{
								// Cast to Graphics2D for convenience.

		Graphics2D pg2D = (Graphics2D)pg;

								// Leave space for header and footer, if any.

		int reducedPageHeight =
			printerPageHeight - getHeaderSize( pg2D ) - getFooterSize( pg2D );

								// Get scale factor for component.
								// We want to fit the full horizontal width
								// into the available space, which may require
								// a scale down.

		if ( component.getWidth() > 0.0 )
		{
			scaleFactor =
				( ( printerPageWidth + 0.0 ) / component.getWidth() );

								// If component isn't wider than printer
								// page width, don't scale.

			if ( scaleFactor >= 1.0 ) scaleFactor = 1.0;
		}
		else
		{
			scaleFactor = 1.0;
		}
								// Get scaled component height.

		double scaledComponentHeight =
			scaleFactor * component.getPreferredSize().height;

								// See if we already know the page offset
								// and length of the slice to print for
								// this page.

		Integer key = new Integer( pageIndex );

		if ( pageStateMap.containsKey( key ) )
		{
			pageState = (PageState)pageStateMap.get( key );
		}
		else
		{
								// We haven't determined the page offset and
								// length for this page yet.  If we're on the
								// first page, set the starting offset for the
								// first slice to 0.

			if ( pageIndex == 0 )
			{
								// Make sure component layout is valid.

				validateLayout();

								// Default slice for first page.

				pageState = new PageState( 0 , 0 , reducedPageHeight );

								// Determine reduced slice length so that
								// page break is as "nice" as possible.

				pageState.pageLength =
					getSliceLength( breakCheckPercentage , scaleFactor );

								// Store page state for this page.

				pageStateMap.put( new Integer( pageIndex ) , pageState );
			}
								// If we're not on the first page, start this
								// slice where the slice for the previous page
								// left off.
			else
			{
				Integer prevKey	= new Integer( pageIndex - 1 );

				PageState prevPageState	=
					(PageState)pageStateMap.get( prevKey );

				pageState =
					new PageState(
						pageIndex ,
						prevPageState.pageOffset +
							prevPageState.pageLength ,
						reducedPageHeight );

								// Determine reduced slice length so that
								// page break is as "nice" as possible.

				pageState.pageLength =
					getSliceLength( 0.20 , scaleFactor );

								// Store page state for this page.

				pageStateMap.put( new Integer( pageIndex ) , pageState );
			}
		}
								// If starting page offset exceeds height
								// of component, we're done.

		if ( pageState.pageOffset > scaledComponentHeight )
		{
			return NO_SUCH_PAGE;
		}
		else
		{
								// Get header size.

			int headerSize = getHeaderSize( pg2D );

								// Paint component slice into printer graphics
								// buffer.
			printPage(
				pg2D,
				pageState.pageOffset,
				pageState.pageLength,
				headerSize,
				scaleFactor,
				true );
								// Reset clipping region for header and footer.

			pg2D.translate( 0f , (float)pageState.pageOffset );
			pg2D.translate( 0f , -(float)headerSize );

								// Paint header and footer.

			printHeaderAndFooter( pg2D );

								// Update printing progress.

			updateProgress( pageIndex );

			return PAGE_EXISTS;
		}
	}

	/** Calculate count of printed pages.
	 *
	 *	@return		Count of pages to print.
	 */

	public int calculatePageCount()
	{
		if ( pageCount == -1 )
		{
			pageCount =
				PrintUtilities.getPageCount(
					this,
					pageFormat,
					printerPageWidth - 20,
					printerPageHeight ,
					true );
		}
								// Return count of pages to print.
		return pageCount;
	}

	/** Return number of pages to print.
	 *
	 *	@return		Count of pages to print.
	 */

	public int getNumberOfPages()
	{
								// If we haven't calculate the number of
								// pages to print yet, do so now.

		if ( pageCount == -1 )
		{
			pageCount = calculatePageCount();
		}
                                // Return the number of pages to print.
		return pageCount;
	}

	/** Paint page into specified graphics content.
	 *
	 *	@param	pg2D			Graphics buffer into which to print component
	 *							slice.
	 *	@param	pageOffset		Component offset at which to start printing.
	 *	@param	pageLength		Length of component slice to print.
	 *	@param	headerSize		Size of header.
	 *	@param	scaleFactor		Scale factor for printing slice.
	 *	@param	addMargins		True to account for printer page margins.
	 */

	protected void printPage
	(
		Graphics2D pg2D ,
		int pageOffset ,
		int pageLength ,
		int headerSize ,
		double scaleFactor ,
		boolean addMargins
	)
	{
								// Shift origin of the graphics context to
								// account for the page's margins.
		if ( addMargins )
			pg2D.translate(
				pageFormat.getImageableX() ,
				pageFormat.getImageableY() );

								// Get window over slice of component to print.

		pg2D.translate( 0f , (float)headerSize );
		pg2D.translate( 0f , -(float)pageOffset );

								// Set the printer page clipping region.
		pg2D.setClip(
			0,
			(int)Math.ceil( pageOffset ),
		 	printerPageWidth,
			pageLength );
								// Set scale to use when printing.

		pg2D.scale( scaleFactor , scaleFactor );

								// Print component into graphics area.
								// Must be done on Swing thread or horrible
								// problems will occur!
/*
		final Graphics2D finalpg2D = pg2D;

		SwingRunner.runNow
		(
			new Runnable()
			{
				public void run()
				{
					component.print( finalpg2D );
				}
			}
		);
*/
		component.print( pg2D );

								// Reset scale to use when printing.

		pg2D.scale( 1.0 / scaleFactor , 1.0 / scaleFactor );
	}

	/** Set break check percentage.
	 *
	 *	@param	breakCheckPercentage	Percentage of page height
	 *									to check for a clean page break.
	 *
	 *	<p>
	 *	A break check value of 0.20 (20%) work well and is the default.
	 *	A new break check value must be &gt;= 0.0 and &lt; 1.0 .
	 */

	public void setBreakCheckPercentage( double breakCheckPercentage )
	{
		if	(	( breakCheckPercentage >= 0.0 ) &&
				( breakCheckPercentage < 1.0 ) )
			this.breakCheckPercentage = breakCheckPercentage;
	}

	/** Get slice length with nice place to split a page image.
	 *
	 *	@param	percentage	How far up page to look as a percentage
	 *						of printer page height.
	 *
	 *	@param	scaleFactor	If we're scaling the output.
	 *
	 *	@return	The slice length.
	 *
	 *	<p>
	 *	We apply the following heuristics to find a "nice" place to split
	 *	the printed image when mapping the image buffer to a series of pages.
	 *	</p>
 	 *
 	 *	<ol>
 	 *	<li>
 	 *	Starting with the last line of pixels in the slice, we move back
 	 *	in the image slice looking for a blank line -- e.g., one with all
 	 *	pixels set to the background color.  While doing so we keep a tally
 	 *	of the number of pixels which are not equal to the background color
 	 *	(e.g., are set) in each line.  We stop when we find a "blank" line
 	 *	or we have moved up the specified percent of the printer page size.
 	 *	</li>
 	 *
 	 *	<li>
 	 *	If we found a blank line, split the component image at that line.
 	 *	</li>
 	 *
 	 *	<li>
 	 *	If we did not find a blank line, select the line with the least
 	 *	number of set pixels closest to the bottom of the original slice.
 	 *	</li>
 	 *	</ol>
 	 */

 	 protected int getSliceLength( double percentage , double scaleFactor )
 	 {
								// if we're not to scan any lines,
								// just return the end of the page as the
								// break point.

		if ( percentage == 0.0 )
		{
			return pageState.pageLength;
		}
								// Allocate buffered image if we haven't
								// already done so.

		if ( pageImage == null )
		{
			pageImage =
				new BufferedImage(
				 	printerPageWidth,
				 	printerPageHeight,
					BufferedImage.TYPE_INT_RGB );
		}
								// Get the graphics context for the buffer.

		Graphics2D g2D = (Graphics2D)pageImage.getGraphics();

   								// Clear the background to white.

		Color savedColor = g2D.getColor();

		g2D.setColor( Color.white );

		g2D.fillRect(
			0,
			0,
		 	printerPageWidth,
		 	printerPageHeight );

		g2D.setColor( savedColor );

								// Print component slice into buffered image.
		printPage(
			g2D,
			pageState.pageOffset,
			pageState.pageLength,
			0,
			scaleFactor,
			false );
								// Start at the bottom of rendered image
								// (length of page - 1 ) and then work
								// backwards looking for a "blank"
								// row.  Keep track of the row with the
								// smallest number of pixels set.

 	 	int row				= pageState.pageLength - 1;
		int minPixelRow		= row;
		int minPixelCount	= printerPageWidth + 1;
		int howFar			= row - (int)( row * percentage );
		int white			= Color.white.getRGB();

		while ( row >= howFar )
		{
								// Count pixels in this row not set
								// to the backgound color = white.

			int pixelCount = 0;

			for ( int column = 0; column < printerPageWidth; column++ )
			{
				int pixel = pageImage.getRGB( column , row );
				if ( pixel != white ) pixelCount++;
			}
                                // If the pixel count is less
                                // than the previous minimum
                                // pixel count, this row is
                                // the new minimum pixel count row.

			if ( pixelCount < minPixelCount)
			{
				minPixelCount	= pixelCount;
				minPixelRow		= row;
			}
						 		// If the pixel count is zero,
						 		// this row is blank, and we've
						 		// found our split point.

			if ( pixelCount == 0 ) break;

			row--;
		}
					       		// Return index of blank row or one
					       		// with least number of pixels set.
 	 	return minPixelRow;
 	 }

	/** Return printer page format for a given page index.
	 *
	 *	@param		pageIndex	Index of page.
	 *
	 *	@return					PageFormat for specified page.
	 */

	public PageFormat getPageFormat( int pageIndex )
	{
								// All pages use the same page format.
		return pageFormat;
	}

	/** Return printable for given page index.
	 *
	 *	@param		pageIndex	Index of page.
	 *
	 *	@return					Printable for specified page.
	 */

	public Printable getPrintable( int pageIndex )
	{
		return this;
	}

	/** Enable or disable print progress.
	 *
	 *	@param	onOff	True to enable print progress, false to disable.
	 */

	public void setProgress( boolean onOff )
	{
		printProgressAllowed = onOff;
	}

	/** Update printed pages status display with number of pages printed.
	 *
	 *	@param	pagesPrinted	Number of pages printed so far.
	 */

	public void updateProgress( int pagesPrinted )
	{
		if ( ( printProgress == null ) && printProgressAllowed )
		{
								// Create print progress dialog.
			printProgress =
				new PrintProgressDialog( printerJob , pageCount );

			printProgress.setVisible( true );
		}

		if ( printProgressAllowed )
		{
			printProgress.updateProgress( pagesPrinted );
		}
	}

	/** Close print progress dialog. */

	public void closeProgress()
	{
		if ( printProgress != null ) printProgress.close();
		printProgress = null;
	}

	/** Set printer job.
	 *
	 *	@param	printerJob	The printer job used for printing.
	 */

	public void setPrinterJob( PrinterJob printerJob )
	{
		this.printerJob = printerJob;
	}

	/** Set page format.
	 *
	 *	@param	pageFormat	The page format used for printing.
	 */

	public void setPageFormat( PageFormat pageFormat )
	{
		this.pageFormat = pageFormat;

								// Get printer page width and height.

		printerPageWidth	= (int)pageFormat.getImageableWidth();
		printerPageHeight	= (int)pageFormat.getImageableHeight();
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

