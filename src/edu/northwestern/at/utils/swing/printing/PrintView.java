package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */

import javax.swing.text.*;

import java.awt.*;

import java.util.*;

/** Arranges Swing text component page images within a rectangular area for printing.
 *
 *	<p>
 *	Swing text components such as JTextPane maintain document text as a series
 *	of paragraphs.  Corresponding to each paragraph is a View which may be rendered
 *	onto a graphics context.  The paragraph view typically contains lines of styled text
 *	stored as child row views, or components such as labels and images.
 *	</p>
 *
 *	<p>
 *	To print the text from a text component, we create a vertical stack of the paragraph
 *	views in the document, asking each to reformat itself to the provided printer page
 *	dimensions.  We try to fit as many paragraph views as possible on a
 *	single printer page.  When a paragraph view won't fit within the remaining
 *	space on the page, we print as many of its child row views as will fit
 *	on the current page, and then we print the rest on the next page.  We maintain
 *	state for each page to remember which view and how much of it was printed on a
 *	page, so that the next time we are called to print another page, we know where
 *	to start.
 *	</p>
 *
 *	<p>
 *	Some objects (such as images) may be too large horizontally or vertically to fit
 *	on a single printer page.  We handle this by scaling the object to fit within the
 *	confines of a single printer page.  Some alternative ways to handle oversized
 *	objects like images, which are not implemented here, include just not printing
 *	the oversized object at all; clipping the object in its original size to fit
 *	within the page boundaries; combining clipping/arbitrary splitting across pages;
 *	etc.
 *	</p>
 */

public class PrintView extends BoxView
{
	/** Index of first view to be rendered on current page. */

	protected int firstOnPage = 0;

	/** Offset into first view to be rendered on current page. */

	protected int firstOnPageOffset = -1;

	/** Class holding information about last view on a printed page. */

	protected class PageState
	{
		/** Index of last view to be rendered on current page. */

		public int lastOnPage;

		/** Length into last view to be rendered on current page. */

		public int lastOnPageLength;

		/** Create page state object.
		 * @param lastOnPage Index of last view to be rendered on current page.
		 * @param lastOnPageLength Length into last view to be rendered on current page.
		*/

		public PageState
		(
			int lastOnPage,
			int lastOnPageLength
		)
		{
			this.lastOnPage			= lastOnPage;
			this.lastOnPageLength	= lastOnPageLength;
		}

		/** Create page state object from another page state object.
		 * @param otherPageState Page stae object to use as a template.
		*/

		public PageState
		(
			PageState otherPageState
		)
		{
			this.lastOnPage			= otherPageState.lastOnPage;
			this.lastOnPageLength	= otherPageState.lastOnPageLength;
		}
	}

	/** Current page state. */

	protected PageState pageState;

	/** Hashmap holds page state for each printed page. */

	protected HashMap pageStateMap = new HashMap();

	/** Index of current page. */

	protected int currentPageIndex = 0;

	/** Header and footer. */

	protected PrintHeaderFooter headerAndFooter;

	/** Create PrintView.
	 *
	 *	@param	elem				Root element of document to print
	 *	@param	root    			Root view of document to print
	 *	@param	width				Printing width
	 *	@param	height				Print height
	 *	@param	headerAndFooter		Specifies header and footer for each page
	 */

	public PrintView
	(
		javax.swing.text.Element elem,
		View root,
		int width,
		int height,
		PrintHeaderFooter headerAndFooter
	)
	{
								// Use vertical axis for
								// format/break operations.

		super( elem , Y_AXIS );

								// The document's root view
								// is the parent for this view.
		setParent( root );
								// Set view size.

		this.setSize( width , height );

								// Layout child views based
								// upon specified width and height.

		layout( width , height );

								// Save header and footer.

		this.headerAndFooter = headerAndFooter;
	}

	/** Create PrintView.
	 *
	 *	@param	elem	Root element of document to print
	 *	@param	root    Root view of document to print
	 *	@param	width	Printing width
	 *	@param	height	Print height
	 */

	public PrintView
	(
		javax.swing.text.Element elem,
		View root,
		int width,
		int height
	)
	{
		this(	elem, root, width, height,
				new PrintHeaderFooter( null , null , null ) );
	}

	/** Paint child view row using a specified scale factor.
	 *
	 *	@param	graphics2D	Graphics context in which to render a page.
	 *	@param	rect		Page location and unscaled view size.
	 *	@param	view		View to print.
	 *	@param	scaleFactor	Scale factor to use when printing.
	 */

	public void printChildView
	(
		final Graphics2D graphics2D,
		Rectangle rect,
		final View view,
		double scaleFactor
	)
	{
								// Set scale to use when printing view.

		graphics2D.scale( scaleFactor , scaleFactor );

								// Get scaled coordinates at which
								// to print view.

		rect.x = (int)( rect.x / scaleFactor );
		rect.y = (int)( rect.y / scaleFactor );

								// Print the scaled view.

		final Rectangle paintRect = new Rectangle( rect );
/*
		SwingRunner.runNow
		(
			new Runnable()
			{
				public void run()
				{
					view.paint( graphics2D , paintRect );
				}
			}
		);
*/
		view.paint( graphics2D , paintRect );

								// Restore original scaling.

		graphics2D.scale( 1.0 / scaleFactor , 1.0 / scaleFactor );
	}

	/** Get scaled vertical span used by view.
	 *
	 *	@param	view				The view whose vertical span is desired.
	 *	@param	printerPageWidth	Width of printable printer page area.
	 *	@param	printerPageHeight	Height of printable printer page area.
	 *
	 *	@return			The vertical span.  For most views this is
	 *					just the usual maximum vertical span.  For views
	 *					which need to be scaled to fit the printer page,
	 *					this is the usual vertical span multiplied by
	 *					the scale factor for the view.
	 */

	public int getScaledVerticalSpan
	(
		View view ,
		int printerPageWidth ,
		int printerPageHeight
	)
	{
		return (int)( 	view.getMaximumSpan( Y_AXIS ) *
						PrintUtilities.getViewScaleFactor(
							(int)view.getMaximumSpan( X_AXIS ) ,
							(int)view.getMaximumSpan( Y_AXIS ) ,
							printerPageWidth ,
							printerPageHeight ) );
	}

	/** Print one page.
	 *
	 *	@param	graphics	Graphics context in which to render a page.
	 *	@param	pageHeight	The page height.
	 *	@param	pageIndex	Index of page to render.
	 *
	 *	@return				True if given page is rendered successfully,
	 *						False if end of document reached.
	 */

	public boolean printPage( Graphics graphics, int pageHeight, int pageIndex )
	{
								// Start rendering the next view
								// after the last one rendered on the
								// previous page.  If there is no previous page,
								// we're on the first page.

		int prevPage	= pageIndex - 1;

		Integer key = Integer.valueOf( prevPage );

		if ( pageStateMap.containsKey( key ) )
		{
			pageState = (PageState)pageStateMap.get( Integer.valueOf( prevPage ) );
		}
		else
		{
								// We're on the first page.

			pageState	= new PageState( -1 , -1 );
		}
								// Get the first view to render on this page.

		if ( pageState.lastOnPageLength == -1 )
		{
			firstOnPage			= pageState.lastOnPage + 1;
			firstOnPageOffset	= -1;
		}
		else
		{
			firstOnPage			= pageState.lastOnPage;
			firstOnPageOffset	= pageState.lastOnPageLength + 1;
		}
								// If we're past the number of views in
								// the document, we're done printing.

		if ( firstOnPage >= getViewCount() )
			return false;
								// Get graphics 2D context.

		Graphics2D graphics2D = (Graphics2D)graphics;

                                // Remember the new current page index.

		currentPageIndex = pageIndex;

								// Create new page state entry for this page.

		pageState = new PageState( pageState );

								// Figure amount of space needed to print header and/or
								// footer lines.

		int headerFontSize		= headerAndFooter.getHeaderFontSize( graphics );
		int footerFontSize		= headerAndFooter.getFooterFontSize( graphics );

								// Remember if we're going to print a header
								// and/or page number at the top of the page.
								// We need to allow space for the header.

		boolean doHeader = headerAndFooter.doPrintHeader();

								// If we're going to print a footer and/or page
								// number at the bottom of the page, shorten the
								// amount of space available for printing
								// the document text.

		int reducedPageHeight = pageHeight;

		if ( headerAndFooter.doPrintFooter() )
		{
			reducedPageHeight = pageHeight - 2 * footerFontSize;
		}
								// Set top and bottom coordinates
								// of page being rendered relative
								// to the document.

		int yMin = getOffset( Y_AXIS , firstOnPage );
		int yMax = yMin + reducedPageHeight;

								// "rect" holds coordinates of
								// location of associated child view
								// is placed in document (not on
								// current page).

		Rectangle rect = new Rectangle();

								// True if we're working on first view on this page.

		boolean doingFirstView = true;

								// Height of lines already printed from first view
								// on previous page(s).  Also totals
								// space used by large views which have been
								// scaled to fit the printer page.

		int linesDoneHeight = 0;

								// If we have lines left over from a view
								// on the previous page, see how many we
								// can fit on this page.

		if ( firstOnPageOffset != -1 )
		{
			doingFirstView = false;

								// Get the view with left over lines.

			View paragraphView = getView( firstOnPage );

								// Get coordinates of location in the document
								// of the view with left over lines.

			rect.x		= getOffset( X_AXIS , firstOnPage );
			rect.y		= getOffset( Y_AXIS , firstOnPage );

								// Leave space if we're going to print a header.

			if ( doHeader ) rect.y = rect.y + 2 * headerFontSize;

			rect.width	= getSpan( X_AXIS , firstOnPage );
			rect.height	= getSpan( Y_AXIS , firstOnPage );

                				// How much space is left for print on this page?

   			int roomLeft		= yMax - rect.y;

								// Get total number of rows in this view.

			int paragraphLines	= paragraphView.getViewCount();

								// Figure number of rows left to print.

			int linesLeft		= paragraphLines - firstOnPageOffset;

								// Offset view by yMin so position is relative
								// to current page instead of whole document.

			rect.y				-= yMin;

								// Calculate vertical space for rows of
								// this view already printed on
								// previous page(s).

			linesDoneHeight 	= 0;

			for ( int i = 0; i < firstOnPageOffset; i++ )
			{
				View childView	= paragraphView.getView( i );

				linesDoneHeight =
					linesDoneHeight +
						getScaledVerticalSpan( childView , getWidth() , yMax - yMin );
			}
								// Print the left over lines from this view which
								// fit on this page.

			for ( int i = firstOnPageOffset; i < paragraphLines; i++ )
			{
								// Get next row to print in this view.

				final View childView = paragraphView.getView( i );

                                // Get its print size.

				int maxSpan =
					getScaledVerticalSpan(
						childView , getWidth() , yMax - yMin );

								// See if we have enough room to print the row on
								// the current page.

				roomLeft = roomLeft - maxSpan;

								// If we have enough room, print it, and
								// reduce count of lines left to print from
								// the view.

				if ( roomLeft > 0 )
				{
					firstOnPageOffset = i;
					linesLeft--;

					final Graphics finalgraphics = graphics;
					final Rectangle paintRect = new Rectangle( rect );
/*
					SwingRunner.runNow
					(
						new Runnable()
						{
							public void run()
							{
								childView.paint( finalgraphics , paintRect );
							}
						}
					);
*/
					childView.paint( finalgraphics , paintRect );

					rect.y = rect.y + maxSpan;
				}
				else
				{
					break;
				}
			}
								// If not all of the left over lines fit on this page,
								// set up to print the rest on subsequent page(s).

			if ( linesLeft > 0 )
			{
				pageState.lastOnPage		= firstOnPage;
				pageState.lastOnPageLength	= firstOnPageOffset;

								// Print the header and the footer, if any.

				headerAndFooter.printHeaderAndFooter(
					graphics , pageIndex , getWidth(), reducedPageHeight );

								// Save the last view and length for this page.

				pageStateMap.put( Integer.valueOf( pageIndex ) , pageState );

				return true;
			}
								// If we printed all the left over lines,
								// set up to check if any more views can be
								// printed on this page.
			else
			{
				firstOnPage			= firstOnPage + 1;
				firstOnPageOffset	= -1;
			}
		}
								// Examine each child view from the
								// first to the last to see which
								// will fit on the current page.

		for ( int currentView = firstOnPage; currentView < getViewCount(); currentView++ )
		{
								// Get coordinates of location
								// of a view in the document.

			rect.x = getOffset( X_AXIS , currentView );

			if ( doingFirstView || ( currentView > firstOnPage ) )
			{
				rect.y = getOffset( Y_AXIS , currentView );

								// Leave space if we're going to print
								// a header.

				if ( doHeader ) rect.y = rect.y + 2 * headerFontSize;

                                // Adjust height to account for lines
                                // printed on previous pages.

				rect.y = rect.y - linesDoneHeight;
			}
			else
			{
				rect.y = rect.y + yMin;
			}
								// Get printing size of this view.

			rect.width	= getSpan( X_AXIS , currentView );
			rect.height	= getSpan( Y_AXIS , currentView );

			View paragraphView = getView( currentView );

								// See if we can render this view on
								// this page.  We can do this if its
								// height added to the current page
								// offset doesn't exceed the page length
								// available for document printing.

       		if ( ( rect.y + rect.height ) > yMax )
       		{
								// We cannot render all of this view on this page.
								// See how much room is left on this page.

       			int roomLeft = yMax - rect.y;

								// Offset view by yMin so position is relative
								// to current page instead of whole document.

				rect.y -= yMin;

								// Will be set true below if we need break out of row view loop.

				boolean mustBreak = false;

								// Now see how many rows of the view will fit on
								// this page.  If none, just go to the next page,
								// otherwise set up to render as many lines as will
								// fit on this page.  The remaining lines will be
								// placed on the subsequent page(s).

				for ( int i = 0; i < paragraphView.getViewCount(); i++ )
				{
								// Get next row in this view.

					final View childView	= paragraphView.getView( i );

								// Get how much vertical space it needs.

					int maxSpan	=
						getScaledVerticalSpan( childView , getWidth() , yMax - yMin );

					int width =
						(int)childView.getMaximumSpan( X_AXIS );

								// If the height for this row view exceeds
								// the printer page height, we need to rescale
								// the view to fit within the confines of a printer page.

					double scaleFactor =
						PrintUtilities.getViewScaleFactor(
							width , maxSpan , getWidth() , yMax - yMin );

					if ( scaleFactor < 1.0 )
					{
						int newHeight	= (int)Math.ceil( scaleFactor * rect.height );

								// See if we can print the scaled view on the current page.

						if ( ( rect.y + newHeight ) > yMax )
						{
								// Not enough room to print the scaled view on the current page.
								// If we haven't printed any rows at all from the current view,
								// set up to print the current view starting at the top of
								// the next page.

							if ( i == 0 )
							{
								pageState.lastOnPage		= currentView - 1;
								pageState.lastOnPageLength	= -1;
							}
							else
							{
								pageState.lastOnPage		= currentView;
								pageState.lastOnPageLength	= i - 1;
							}
								// Set mustBreak true to terminate printing on this
								// page and move to the next.

							mustBreak = true;
							break;
						}
						else
						{
								// Render the scaled view.

							printChildView( graphics2D, rect, childView, scaleFactor );

								// Account for the difference in the vertical space
								// for the scaled and unscaled view.  This ensures
								// we don't get bogus extra blank lines when printing
								// following views on this printer page.

							linesDoneHeight = 	linesDoneHeight +
													rect.height -
													(int)( rect.height * scaleFactor );

								// Indicate this was last view printed on this page.

							pageState.lastOnPage		= currentView;
							pageState.lastOnPageLength	= -1;

							continue;
						}
					}
								// The scaled row does not exceed the capacity of a printer page.
								// See if there is room for the scaled row on this page.

					roomLeft = roomLeft - maxSpan;

								// If so, print it, and remember that it
								// is, so far, the last row in the view
								// we've printed.

					if ( roomLeft >= 0 )
					{
						pageState.lastOnPageLength	= i;
						pageState.lastOnPage		= currentView;

						final Graphics finalgraphics = graphics;
						final Rectangle paintRect = new Rectangle( rect );
/*
						SwingRunner.runNow
						(
							new Runnable()
							{
								public void run()
								{
									childView.paint( finalgraphics , paintRect );
								}
							}
						);
*/
						childView.paint( finalgraphics , paintRect );

						rect.y = rect.y + maxSpan;

								// Remember if we printed the entire scaled view
								// on this page.

						if ( ( i + 1 ) == paragraphView.getViewCount() )
						{
							pageState.lastOnPageLength = -1;
						}
					}
					else
					{
								// Remember the current view as the last we printed
								// on this page, but we weren't able to print all of it.

						pageState.lastOnPage = currentView;
						break;
					}
				}
								// If there is room left on the page, and we don't
								// need to go to a new page to print an oversized item,
								// try to print more child views on this page.

       			if ( ( roomLeft >= 0 ) && ( !mustBreak ) )
       			{
       				continue;
       			}
       			else
       			{
       				break;
       			}
			}
			else
			{
								// The whole view fits on the current page.
								// Offset view by yMin so position is relative
								// to current page instead of whole document.

				pageState.lastOnPage		= currentView;
				pageState.lastOnPageLength	= -1;

				rect.y						-= yMin;

								// Render the whole view.

				final Graphics finalgraphics = graphics;
				final Rectangle paintRect = new Rectangle( rect );
				final int finalCurrentView = currentView;
/*
				SwingRunner.runNow
				(
					new Runnable()
					{
						public void run()
						{
							paintChild( finalgraphics , paintRect , finalCurrentView );
						}
					}
				);
*/
				paintChild( finalgraphics , paintRect , finalCurrentView );
			}
		}
								// Print the header and footer, if any.

		headerAndFooter.printHeaderAndFooter(
			graphics , pageIndex , getWidth() , reducedPageHeight );

								// Save updated state for this printer page.

		pageStateMap.put( Integer.valueOf( pageIndex ) , pageState );

		return true;
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

