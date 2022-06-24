package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.print.*;

/**	Prints styled text from a JTextPane. */

public class PrintJTextPane extends PrintableComponent
{
	/** The PrintView to hold printed pages. */

	protected PrintView printView;

	/** Create PrintJTextPane object.
	 *
	 *	@param	textPane			The text pane whose contents are to be printed.
	 *	@param	pageFormat			The printer page format.
	 *	@param	headerFooter	    The header and footer for this page.
	 */

	public PrintJTextPane
	(
		JTextPane textPane,
		PageFormat pageFormat,
		PrintHeaderFooter headerFooter
	)
	{
		super( textPane, pageFormat, headerFooter );

		this.printView		= null;
	}

	/** Create PrintJTextPane object.
	 *
	 *	@param	textPane	The text pane whose contents are to be printed.
	 *	@param	pageFormat	The printer page format.
	 *
	 *	<p>
	 *	No header, footer, or line numbers are printed.
	 *	</p>
	 */

	public PrintJTextPane
	(
		JTextPane textPane,
		PageFormat pageFormat
	)
	{
		this( textPane , pageFormat , null );
	}

	/** Create PrintJTextPane object.
	 *
	 *	@param	textPane	The text pane whose contents are to be printed.
	 *
	 *	<p>
	 *	No header, footer, or line numbers are printed.
	 *	</p>
	 */

	public PrintJTextPane
	(
		JTextPane textPane
	)
	{
		this( textPane , PrinterSettings.pageFormat , null );
	}

	/** Print the contents of the JTextPane.
	 *
	 *	<p>
	 *	You can call this method instead of printContents() if you
	 *	are setting up your own printing thread.
	 *	</p>
	 */

	public void doPrintContents()
	{
								// Save current editable status of text pane
								// to be printed and set the text pane not editable
								// for the duration of the print.  Obviously we
								// don't want someone changing the contents while
								// we're printing them.
/*
		JTextPane textPane = (JTextPane)component;

		boolean savedEditable = textPane.isEditable();

		textPane.setEditable( false );
*/
								// Print the text pane contents.

		super.doPrintContents();

								// Restore editable status of text pane.
/*
		textPane.setEditable( savedEditable );
*/
	}

	/** Print one page of text from document.
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
	 *	Implements the Printable interface for the Swing text component.
	 *	</p>
	 */

	public int print
	(
		Graphics pg,
		PageFormat pageFormat,
		int pageIndex
	)
	{
//		if ( Env.MACOSX ) return super.print( pg, pageFormat, pageIndex );

								// Shift origin of the graphics context to
								// account for the page's margins.
		pg.translate(
			(int)pageFormat.getImageableX(),
			(int)pageFormat.getImageableY() );

								// Get the printer page clipping region.

		pg.setClip( 0, 0, printerPageWidth, printerPageHeight );

								// If printView is null, initialize it.

		if ( printView == null )
		{
			initializePrintView();
		}
								// Render the page and see if there are more
								// pages to render.
		boolean bContinue =
			printView.printPage( pg, printerPageHeight, pageIndex );

								// Garbage collect since rendering a page
								// uses gobs of memory.
		System.gc();
                                // Let invoking print manager know if there
                                // are any more pages to print.
		if ( bContinue )
		{
			updateProgress( pageIndex + 1 );

			return PAGE_EXISTS;
		}
		else
		{
			return NO_SUCH_PAGE;
		}
	}

	/** Initialize the print view stack.
	 */

	public void initializePrintView()
	{
								// Get a print view.
								// Only do this once per print job.

		if ( printView == null )
		{
			JTextPane textPane = (JTextPane)component;

			BasicTextUI btui = (BasicTextUI)textPane.getUI();

                                // Get the root view of the text pane.

			View root = btui.getRootView( textPane );

								// Get the document and its root element.
								// The root element we need for printing
								// differs depending upon which editor kit
								// is in use.

			DefaultStyledDocument doc =
				(DefaultStyledDocument)textPane.getDocument();

			EditorKit editorKit =
				textPane.getEditorKit();

			javax.swing.text.Element topElement	= null;

			if ( editorKit instanceof javax.swing.text.html.HTMLEditorKit )
			{
				topElement =
					doc.getDefaultRootElement().getElement( 1 );

				if ( topElement == null )
					topElement =
						doc.getDefaultRootElement().getElement( 0 );

				if ( topElement == null )
					topElement = doc.getDefaultRootElement();
			}
			else
			{
				topElement = doc.getDefaultRootElement();
			}
								// Create a PrintView of the specified size
								// starting at the root element of the
								// document and text pane.
			printView =
				new PrintView
				(
					topElement,
					root,
					printerPageWidth - 20,
					printerPageHeight ,
					headerAndFooter
				);
		}
	}

	/** Calculate count of printed pages.
	 *
	 *	@return		Count of pages to print.
	 */

	public int calculatePageCount()
	{
//		if ( Env.MACOSX ) return super.calculatePageCount();

       	if ( printView == null )
       	{
								// Initialize print view stack.

			initializePrintView();

								// Calculate the count of pages to print.
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
//		if ( Env.MACOSX ) return super.getNumberOfPages();

								// If we haven't calculate the number of
								// pages to print yet, do so now.

		if ( printView == null )
		{
			pageCount = calculatePageCount();
		}
								// Return the number of pages to print.
		return pageCount;
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

