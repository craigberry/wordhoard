package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.print.*;

import edu.northwestern.at.utils.swing.*;

/**	Prints a JTable. */

public class PrintJTable extends PrintableComponent
{
	/**	Table header. */

	protected JTableHeader tableHeader = null;

	/**	Table header size. */

	protected double tableHeaderSize	= 0;

	/** Table title.  May be split into several lines. */

	protected String[] tableTitleLines	= null;

	/**	Table title height. */

	protected double tableTitleSize		= 0;

	/** Create PrintJTable object.
	 *
	 *	@param	table				The table whose contents are to be printed.
	 *	@param	pageFormat			The printer page format.
	 *	@param	headerFooter	    The header and footer for this page.
	 */

	public PrintJTable
	(
		JTable table ,
		PageFormat pageFormat ,
		PrintHeaderFooter headerFooter
	)
	{
		super( table , pageFormat , headerFooter );
	}

	/** Create PrintJTable object.
	 *
	 *	@param	table		The table whose contents are to be printed.
	 *	@param	pageFormat	The printer page format.
	 *
	 *	<p>
	 *	No header, footer, or line numbers are printed.
	 *	</p>
	 */

	public PrintJTable
	(
		JTable table ,
		PageFormat pageFormat
	)
	{
		super( table , pageFormat );
	}

	/** Create PrintJTable object.
	 *
	 *	@param	table		The table whose contents are to be printed.
	 *
	 *	<p>
	 *	No header, footer, or line numbers are printed.
	 *	</p>
	 */

	public PrintJTable
	(
		JTable table
	)
	{
		super( table , PrinterSettings.pageFormat );
	}

    /** Wrap title text.
     *
     *	@param	title	The title text.
     *	@param	pg		The graphics context.
     *
     *	@return			String array of possibly word-wrapped title lines.
     */

    protected String[] wrapTitleLine( String title , Graphics pg )
	{
		Graphics2D pg2D			= (Graphics2D)pg;

								//	Replace hard carriage returns with
								//	two blanks.

		String noCRLFTitle		= title.replace( '\n' , ' ' );

								//	Split line into tokens at blanks.

		String[] titleTokens	= noCRLFTitle.split( " " );
        String titleLine		= "";
		String saveTitleLine	= "";
		ArrayList titleLines	= new ArrayList();

		for ( int i = 0 ; i < titleTokens.length ; i++ )
		{
			saveTitleLine	= titleLine;
			titleLine		= titleLine + titleTokens[ i ] + " ";

			int titleLineWidth	=
				pg2D.getFontMetrics().stringWidth( titleLine );

			if ( titleLineWidth > printerPageWidth )
			{
				titleLines.add( saveTitleLine );
				titleLine	= titleTokens[ i ] + " ";
			}
		}

		if ( titleLine.length() > 0 )
		{
			titleLines.add( titleLine );
		}

		return (String[])titleLines.toArray( new String[]{} );
	}


	/**	Get table title and header sizes.
	 *
	 *	@param	pg	Graphics context for printing.
	 */

	protected void getSizes( Graphics pg )
	{
								// Get scaled size of table headers.

		JTable table	= (JTable)component;
		tableHeader 	= table.getTableHeader();

        tableHeaderSize	= 0;

		if ( tableHeader != null )
		{
			tableHeaderSize =
				( scaleFactor * table.getTableHeader().getHeight() );
		}
								//	If table is an XTable, get
								//	size of table title and
								//	add that to the header size.

		if ( table instanceof XTable )
		{
			String title	=	((XTable)table).getTitle();

			if ( ( title != null ) && ( title.length() > 0 ) )
			{
				tableTitleLines	= wrapTitleLine( title , pg );

				tableTitleSize	=
					( /*scaleFactor * */ tableTitleLines.length *
						headerAndFooter.getHeaderFontSize( pg )
					);
			}
        }
	}

	/** Get header size needed.
	 *
	 *	@param		pg	The graphics context in which to print the header.
	 *
	 *	@return			Vertical size of header.
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
								//	Header size is size of regular header
								//	plus size of title plus size of
								//	table headers.
		getSizes( pg );

		int result =
			super.getHeaderSize( pg ) +
				(int)tableTitleSize +
				(int)tableHeaderSize
				/* + headerAndFooter.getHeaderFontSize( pg ) */;

		return result;
	}

	/**	Print the table title.
	 *
	 *	@param		pg2D				The graphics context into which to
	 *									print the table title, if any.
	 */

	public void printTableTitle( Graphics2D pg2D )
	{
		if ( tableTitleLines != null )
		{
			int fontSize	= headerAndFooter.getHeaderFontSize( pg2D );

			pg2D.translate( 0f , fontSize * 2 );

			pg2D.scale( scaleFactor , scaleFactor );

			for ( int i = 0 ; i < tableTitleLines.length ; i++ )
			{
				pg2D.drawString(
					tableTitleLines[ i ] , 0 , fontSize * ( i + 1 ) );
			}

			pg2D.scale( 1.0 / scaleFactor , 1.0 / scaleFactor );
		}
	}

	/**	Print the column headers.
	 *
	 *	@param		pg2D				The graphics context into which to
	 *									print the column headers, if any.
	 */

	public void printColumnHeaders( Graphics2D pg2D )
	{
								// Print column names, if any.

		final JTableHeader tableHeader	=
			((JTable)component).getTableHeader();

		if ( tableHeader != null )
		{
			if ( tableTitleLines == null )
			{
				pg2D.translate
				(
					0f ,
					2 * headerAndFooter.getHeaderFontSize( pg2D )
				);
			}
			else
			{
				pg2D.translate
				(
					0f ,
					tableTitleLines.length *
						headerAndFooter.getHeaderFontSize( pg2D )
				);
			}

			pg2D.scale( scaleFactor , scaleFactor );

	        tableHeader.print( pg2D );

			pg2D.scale( 1.0 / scaleFactor , 1.0 / scaleFactor );
		}
	}

	/**	Print the column headers.
	 *
	 *	@param		pg2D				The graphics context into which to
	 *									print the column headers, if any.
	 */

	public void printColumnHeadersOld( Graphics2D pg2D )
	{
								//	If the table header is null,
								//	there's nothing to print.

		JTable table				= (JTable)component;

		JTableHeader tableHeader	= table.getTableHeader();

		if ( tableHeader == null ) return;

								//	Position to space between
								//	page header and body of table.

		pg2D.translate(
			0f , 2 * headerAndFooter.getHeaderFontSize( pg2D ) );

								//	Get bold font for column headers.

		Font headerFont	= table.getFont().deriveFont( Font.BOLD );

								//	Scale font as needed.

		double boldScaleFactor	=
			scaleFactor *
				( (double)( table.getFont().getSize2D() /
					headerFont.getSize2D() ) );

		if ( boldScaleFactor != 1.0D )
		{
		    int headerFontSize	=
		    	(int)( headerFont.getSize2D() * boldScaleFactor );

		    headerFont	= headerFont.deriveFont( (float)headerFontSize );
		}

		pg2D.setFont( headerFont );

								//	Get ascent for positioning column
								//	header text.

		FontMetrics fm	= pg2D.getFontMetrics();

		int h			= fm.getAscent();

								//	Get column mode for table and
								//	find number of column headers.

		TableColumnModel colModel	= table.getColumnModel();

		int columns					= colModel.getColumnCount();

								//	The "x" will hold the horizontal
								//	position of the start of each
								//	column header to print.

		int x[]						= new int[ columns ];

		x[ 0 ]						= 0;

								//	Loop over column headers.

		for ( int column = 0 ; column < columns ; column++ )
		{
								//	Get next column header.

			TableColumn tableColumn	= colModel.getColumn( column );

								//	Get the width of this column.

			int width				= tableColumn.getWidth();

								//	Find its starting horizontal print
								//	position.

			if ( ( column + 1 ) < columns )
			{
				x[ column + 1 ]	=
					x[ column ] + (int)( (double)width * scaleFactor );
            }
								//	Get the column header text.

			String headerText	= (String)tableColumn.getIdentifier();

								//	Print the column header text.

			pg2D.drawString( headerText , x[ column ] , h );
		}
    }

	/** Print the header and footer.
	 *
	 *	@param		pg2D				The graphics context into which to
	 *									print the header and footer, if any.
	 */

	public void printHeaderAndFooter( Graphics2D pg2D )
	{
								//	Print regular header and footer.

		super.printHeaderAndFooter( pg2D );

								//  Print title.

		printTableTitle( pg2D );

								//	Print column headers.

		printColumnHeaders( pg2D );
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

