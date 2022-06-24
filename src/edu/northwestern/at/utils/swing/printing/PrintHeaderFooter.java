package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */

import java.awt.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;

/** Specifies header and footer for printed output.
 *
 *	<p>
 *	A printed page may have an optional header and/or footer.
 *	Optionally labeled page numbers may appear in the header,
 *	footer, or both.
 *	</p>
 */

public class PrintHeaderFooter
{
	/** Header text. */

	protected String header;

	/** Footer text. */

	protected String footer;

	/** Page number label. */

	protected String pageNumberLabel;

	/** Possible page number positions. */

	public static final int HEADER = 0;
	public static final int FOOTER = 1;
	public static final int HEADERANDFOOTER = 2;

	/** Page number position. */

	int pageNumberLocation;

	/** Header font. */

	protected Font headerFont = new Font( Fonts.sansSerif, Font.PLAIN, 12 );

	/** Footer font. */

	protected Font footerFont = new Font( Fonts.sansSerif, Font.PLAIN, 12 );

	/** Create PrintHeaderFooter.
	 *
	 *	@param	header				Header text.  Use null for none.
	 *	@param	footer				Footer text.  Use null for none.
	 *	@param	pageNumberLabel     Label for page numbers (e.g., "Page ").
	 *								Use null for no page numbers,
	 *								empty string to print page numbers
	 *								without a label.
	 */

	public PrintHeaderFooter
	(
		String header,
		String footer,
		String pageNumberLabel
	)
	{
		this.header				= header;
		this.footer				= footer;
		this.pageNumberLabel	= pageNumberLabel;
		this.pageNumberLocation	= HEADER;
	}

	/** See if we're to print page numbers in header.
	 *
	 *	@return		True to print page numbers in header.
	 */

	public boolean doPageNumbersInHeader()
	{
		return ( pageNumberLabel != null ) && ( pageNumberLocation != FOOTER );
	}

	/** See if we're to print page numbers in footer.
	 *
	 *	@return		True to print page numbers in footer.
	 */

	public boolean doPageNumbersInFooter()
	{
		return ( pageNumberLabel != null ) && ( pageNumberLocation != HEADER );
	}

	/** Get header font.
	 *
	 *	@return		Font used to print header.
	 */

	public Font getHeaderFont()
	{
		return headerFont;
	}

	/** Set header font.
	 *
	 *	@param	headerFont	Font used to print header.
	 */

	public void setHeaderFont( Font headerFont )
	{
		this.headerFont = headerFont;
	}

	/** Get footer font.
	 *
	 *	@return		Font used to print footer.
	 */

	public Font getFooterFont()
	{
		return footerFont;
	}

	/** Set footer font.
	 *
	 *	@param	footerFont	Font used to print footer.
	 */

	public void setFooterFont( Font footerFont )
	{
		this.footerFont = footerFont;
	}

	/** Get header font size.
	 *
	 *	@return		Font size of header font.
	 */

	public int getHeaderFontSize()
	{
		return headerFont.getSize();
	}

	/** Get header font size from specified graphics context.
	 *
	 *	@return		Font size of header font in graphics context.
	 *
	 *	<p>
	 *	Note:  sets header font as current font in graphics context.
	 *	</p>
	 */

	public int getHeaderFontSize( Graphics graphics )
	{
		Graphics2D graphics2D = (Graphics2D)graphics;

								// Get header font.

		graphics2D.setFont( headerFont );

                                // Get header font size.

		int headerFontHeight	= graphics2D.getFontMetrics().getHeight();
		int headerFontDescent	= graphics2D.getFontMetrics().getDescent();
		int headerFontSize		= headerFontHeight + headerFontDescent;

		return headerFontSize;
	}

	/** Get footer font size.
	 *
	 *	@return		Font size of footer font.
	 */

	public int getFooterFontSize()
	{
		return footerFont.getSize();
	}

	/** Get footer font size from specified graphics context.
	 *
	 *	@return		Font size of footer font in graphics context.
	 *
	 *	<p>
	 *	Note:  sets footer font as current font in graphics context.
	 *	</p>
	 */

	public int getFooterFontSize( Graphics graphics )
	{
		Graphics2D graphics2D = (Graphics2D)graphics;

								// Get header font.

		graphics2D.setFont( footerFont );

                                // Get header font size.

		int footerFontHeight	= graphics2D.getFontMetrics().getHeight();
		int footerFontDescent	= graphics2D.getFontMetrics().getDescent();
		int footerFontSize		= footerFontHeight + footerFontDescent;

		return footerFontSize;
	}

	/** Get page number location.
	 *
	 *	@return		Location to print page numbers.
	 *				Either HEADER, FOOTER, or HEADERANDFOOTER.
	 */

	public int getPageNumberLocation()
	{
		return pageNumberLocation;
	}

	/** Set page number location.
	 *
	 *	@param	pageNumberLocation		Location to print page numbers.
	 *									Either HEADER, FOOTER, or
	 *									HEADERANDFOOTER.
	 *
	 *	<p>
	 *	If the specified location is invalid, the current setting
	 *	remains unchanged.
	 *	</p>
	 */

	public void setPageNumberLocation( int pageNumberLocation )
	{
		switch ( pageNumberLocation )
		{
			case HEADER:
			case FOOTER:
			case HEADERANDFOOTER:
				this.pageNumberLocation = pageNumberLocation;
			default:
		}
	}

	/** See if header to be printed.
	 *
	 *	@return		True if header or header page numbers to be printed.
	 */

	public boolean doPrintHeader()
	{
		return	( ( header != null ) && ( header.length() > 0 ) ) ||
				doPageNumbersInHeader();

	}

	/** See if footer to be printed.
	 *
	 *	@return		True if footer or footer page numbers to be printed.
	 */

	public boolean doPrintFooter()
	{
		return	( ( footer != null ) && ( footer.length() > 0 ) ) ||
				doPageNumbersInFooter();
	}

	/** Print header and footer into specified graphics context.
	 *
	 *	@param	graphics		Graphics context to print to.
	 *	@param	pageNumber		The page number to print.
	 *	@param	pageWidth		The printer page width.
	 *	@param	pageBottom		Bottom page position used for
	 *							document printing.  Footer prints
	 *							below this.
	 */

	public void printHeaderAndFooter
	(
		Graphics graphics ,
		int pageNumber ,
		int pageWidth ,
		int pageBottom
	)
	{
		Graphics2D graphics2D = (Graphics2D)graphics;

								// Get header font.

		graphics2D.setFont( headerFont );

								// Get header font size.

		int headerFontSize = getHeaderFontSize( graphics );

								//	Get page number string and its width.

		String pageNumberString =
			pageNumberLabel + StringUtils.intToString( pageNumber + 1 );

		int pageNumberWidth		=
			graphics2D.getFontMetrics().stringWidth( pageNumberString );

								//	Make sure the text color is black.
								//	Sometimes it comes in as white,
								//	resulting in blank output.

		graphics2D.setColor( Color.BLACK );

								// Add optional header and page number at top.
								// We must do this here to ensure it isn't
								// erased somewhere in the bowels of Swing.

		if ( ( header != null ) && ( header.length() > 0 ) )
		{
								//	Save the current user clip region,
								//	if any.

			Shape savedClip	= graphics2D.getClip();

								//	Get width of header string.

			int headerWidth	=
				graphics2D.getFontMetrics().stringWidth( header );

								//	If we're printing page numbers in
								//	the header, set up a clipping
								//	region so that the header doesn't
								//	run into the page number.

			if ( doPageNumbersInHeader() )
			{
				if ( headerWidth > ( pageWidth - ( pageNumberWidth * 2 ) ) )
				{
					graphics2D.setClip(
						0 ,
						0 ,
						headerWidth - ( pageNumberWidth * 2 ),
						headerFontSize * 2 );
				}
			}
                                //	Draw the header string.

			graphics2D.drawString(
				header,
				0,
				headerFontSize );

								//	Restore the old user clip region.

			graphics2D.setClip( savedClip );
		}
								// Add page number to header if requested.

		if ( doPageNumbersInHeader() )
		{
			graphics2D.drawString(
				pageNumberString ,
				pageWidth - pageNumberWidth ,
				headerFontSize );
        }
								// Get footer font.

		graphics2D.setFont( footerFont );

								// Get footer font size.

		int footerFontSize		= getFooterFontSize( graphics );

		int footerHeight		= pageBottom + footerFontSize;

		pageNumberWidth			=
			graphics2D.getFontMetrics().stringWidth( pageNumberString );

								// Add optional footer and page number at bottom.

		if ( ( footer != null ) && ( footer.length() > 0 ) )
		{
								//	Save the current user clip region,
								//	if any.

			Shape savedClip	= graphics2D.getClip();

								//	Get width of header string.
			int footerWidth	=
				graphics2D.getFontMetrics().stringWidth( footer );

								//	If we're printing page numbers in
								//	the footer, set up a clipping
								//	region so that the footer doesn't
								//	run into the page number.

			if ( doPageNumbersInFooter() )
			{
				if ( footerWidth > ( pageWidth - ( pageNumberWidth * 2 ) ) )
				{
					graphics2D.setClip(
						0 ,
						0 ,
						footerWidth - ( pageNumberWidth * 2 ),
						footerFontSize * 2 );
				}
			}
								//	Draw the footer string.

			graphics2D.drawString(
				footer,
				0,
				footerHeight );

								//	Restore the old user clip region.

			graphics2D.setClip( savedClip );
		}
								//	Add page number to footer if requested.

		if ( doPageNumbersInFooter() )
		{
			graphics2D.drawString(
				pageNumberString ,
				pageWidth - pageNumberWidth ,
				footerHeight );
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

