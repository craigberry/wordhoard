package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.io.*;

import javax.swing.table.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.html.*;

/**	Saves data stored in a TableModel to a file is several different formats.
 */

public class SaveTableModelData
{
	/**	CSV extension file filter. */

	protected static FileExtensionFilter csvFilter	=
			new FileExtensionFilter( ".csv" , "CSV Files" );

	/**	TAB extension file filter. */

	protected static FileExtensionFilter tabFilter	=
			new FileExtensionFilter( ".tab" , "TAB Files" );

	/**	HTML extension file filter. */

	protected static FileExtensionFilter htmlFilter	=
			new FileExtensionFilter
			(   new String[]{ ".htm" , ".html" } ,
				"HTML Files"
			);

	/**	Clean up a column header.
	 *
	 *	@param	columnHeader	The column header string to clean up.
	 *
	 *	@return					Cleaned up column header string.
	 *
	 *	<p>
	 *	HTML tags are removed, multiple blanks are squeezed to one blank,
	 *	and leading and trailing blanks are removed.
	 *	</p>
	 */

	public static String cleanColumnHeader( String columnHeader )
	{
		String result	= columnHeader;

		result	=
			HTMLUtils.stripHTML( result );

		result	=
			StringUtils.replaceAll( result , "\n" , " " );

		result	=
			StringUtils.replaceAll( result , "\r" , " " );

		result	=
			StringUtils.replaceAll( result , "  " , " " );

		result	=
			StringUtils.trim( result );

		return result;
	}

	/**	Convert contents of a TableModel to comma separated values.
	 *
	 *	@param	tableModel		The TableModel containing the data to
	 *							convert to command separated values.
	 *
	 *	@param	title			Title for table.
	 *
	 *	@param	columnTitles	The column headers.
	 *							May be null to suppress column headers.
	 *
	 *	@return					The comma separated table data.  Each table row
	 *							appears on a new line.
	 */

	public static String tableModelDataToCSV
	(
		TableModel tableModel ,
		String title ,
		String[] columnTitles
	)
	{
		StringBuffer sb	= new StringBuffer();

								//	Output title, if any, as a comment.
/*
		if ( title != null )
		{
			sb.append( ";" );
			sb.append( title );
			sb.append( Env.LINE_SEPARATOR );
		}
*/
								//	Output column headers if any.

		if ( columnTitles != null )
		{
			String columnHeader;

			for ( int column = 0 ; column < columnTitles.length ; column++ )
			{
				if ( column > 0 ) sb.append( "," );

				columnHeader	=
					cleanColumnHeader( columnTitles[ column ] );

				if ( columnHeader.indexOf( "," ) >= 0 )
				{
					sb.append( "\"" );
					sb.append( columnHeader );
					sb.append( "\"" );
				}
				else
				{
					sb.append( columnHeader );
				}
			}

			sb.append( Env.LINE_SEPARATOR );
		}
                                //	Output table data.

		for ( int row = 0 ; row < tableModel.getRowCount() ; row++ )
		{
			for (	int column = 0 ;
					column < tableModel.getColumnCount() ;
					column++
				)
			{
				String value	=
					tableModel.getValueAt( row , column ).toString();

				if ( column > 0 ) sb.append( "," );

				if ( value.indexOf( "," ) >= 0 )
				{
					sb.append( "\"" );
					sb.append( value );
					sb.append( "\"" );
				}
				else
				{
					sb.append( value );
				}
			}

			sb.append( Env.LINE_SEPARATOR );
		}
								//	Return tab separated values as string.

		return sb.toString();
	}

	/**	Convert contents of a TableModel to tab separated values.
	 *
	 *	@param	tableModel		The TableModel containing the data to
	 *							convert to tab separated values.
	 *
	 *	@param	title			Title for table.
	 *
	 *	@param	columnTitles	The column headers.
	 *							May be null to suppress column headers.
	 *
	 *	@return					The tab separated table data.  Each table row
	 *							appears on a new line.
	 */

	public static String tableModelDataToTAB
	(
		TableModel tableModel ,
		String title ,
		String[] columnTitles
	)
	{
		StringBuffer sb	= new StringBuffer();

								//	Output title, if any, as a comment.
/*
		if ( title != null )
		{
			sb.append( ";" );
			sb.append( title );
			sb.append( Env.LINE_SEPARATOR );
		}
*/
								//	Output column headers if any.

		if ( columnTitles != null )
		{
			String columnHeader;

			for ( int column = 0 ; column < columnTitles.length ; column++ )
			{
				if ( column > 0 ) sb.append( "\t" );

				columnHeader	=
					cleanColumnHeader( columnTitles[ column ] );

				sb.append( columnHeader );
			}

			sb.append( Env.LINE_SEPARATOR );
		}
                                //	Output table data.

		for ( int row = 0 ; row < tableModel.getRowCount() ; row++ )
		{
			for (	int column = 0 ;
					column < tableModel.getColumnCount() ;
					column++
				)
			{
				String value	=
					tableModel.getValueAt( row , column ).toString();

				if ( column > 0 ) sb.append( "\t" );
				sb.append( value );
			}

			sb.append( Env.LINE_SEPARATOR );
		}
								//	Return tab separated values as string.

		return sb.toString();
	}

	/**	Convert contents of a TableModel to XHTML.
	 *
	 *	@param	tableModel		The TableModel containing the data to
	 *							convert to XHTML.
	 *
	 *	@param	title			Title for table.
	 *
	 *	@param	columnTitles	The column headers.
	 *							May be null to suppress column headers.
	 *
	 *	@param	border			Border size for table.  Set to 0 for no
	 *							border.
	 *
	 *	@return					The XHTML formatted table data.
	 */

	public static String tableModelDataToXHTML
	(
		TableModel tableModel ,
		String title ,
		String[] columnTitles ,
		int border
	)
	{
								//	Output table header.

		StringBuffer sb	=
			new StringBuffer( "<table border=\"" + border + "1\">" );

		sb.append( Env.LINE_SEPARATOR );

								//	Output title if any.
		if ( title != null )
		{
			sb.append( "   <caption>" );
			sb.append( title );
			sb.append( "</caption>" );
			sb.append( Env.LINE_SEPARATOR );
		}
								//	Output column headers if any.

		if ( columnTitles != null )
		{
			sb.append( "   <tr>" );
			sb.append( Env.LINE_SEPARATOR );

			for ( int column = 0 ; column < columnTitles.length ; column++ )
			{
				String columnTitle	=
					StringUtils.trim( columnTitles[ column ] );

				if	(	columnTitle.startsWith( "<html>" ) ||
				        columnTitle.startsWith( "<HTML>" ) )
				{
					columnTitle	= columnTitle.substring( 6 );
				}

				if	(	columnTitle.endsWith( "</html>" ) ||
				        columnTitle.endsWith( "</HTML>" ) )
				{
					columnTitle	=
						columnTitle.substring(
							0 , columnTitle.length() - 7 );
				}

				sb.append( "      <th>" );
				sb.append( columnTitle );
				sb.append( "</th>" );
				sb.append( Env.LINE_SEPARATOR );
			}

			sb.append( "   </tr>" );
			sb.append( Env.LINE_SEPARATOR );
		}
                                //	Output table data.

		for ( int row = 0 ; row < tableModel.getRowCount() ; row++ )
		{
			sb.append( "   <tr>" );
			sb.append( Env.LINE_SEPARATOR );

			for (	int column = 0 ;
					column < tableModel.getColumnCount() ;
					column++
				)
			{
				String value	=
					tableModel.getValueAt( row , column ).toString();

				if ( value.length() == 0 )
				{
					value	= "&nbsp;";
				}

				sb.append( "      <td>" );
				sb.append( value );
				sb.append( "</td>" );
				sb.append( Env.LINE_SEPARATOR );
			}

			sb.append( "   </tr>" );
			sb.append( Env.LINE_SEPARATOR );
		}
                                //	Finish up table.

		sb.append( "</table>" );
		sb.append( Env.LINE_SEPARATOR );

								//	Return XHTML as string.

		return sb.toString();
	}

	/**	Saves the table model data to a file in CSV format.
	 *
	 *	@param	tableModel		The table model containing the data to save.
	 *	@param	title			The title for the data.
	 *	@param	columnTitles	The column titles.  Null if no titles.
	 *	@param	fileName		The file name to which to save the data.
	 */

	public static void saveTableModelDataToFileAsCSV
	(
		TableModel tableModel ,
		String title ,
		String[] columnTitles ,
		String fileName
	)
	{
		try
		{
			FileUtils.writeTextFile
			(
				fileName ,
				false ,
				tableModelDataToCSV( tableModel , title , columnTitles )
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Saves the table model data to a file in TAB format.
	 *
	 *	@param	tableModel		The table model containing the data to save.
	 *	@param	title			The title for the data.
	 *	@param	columnTitles	The column titles.  Null if no titles.
	 *	@param	fileName		The file name to which to save the data.
	 */

	public static void saveTableModelDataToFileAsTAB
	(
		TableModel tableModel ,
		String title ,
		String[] columnTitles ,
		String fileName
	)
	{
		try
		{
			FileUtils.writeTextFile
			(
				fileName ,
				false ,
				tableModelDataToTAB( tableModel , title , columnTitles )
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Saves the table model data to a file in XHTML format.
	 *
	 *	@param	tableModel		The table model containing the data to save.
	 *	@param	title			The title for the data.
	 *	@param	columnTitles	The column titles.  Null if no titles.
	 *	@param	fileName		The file name to which to save the data.
	 */

	public static void saveTableModelDataToFileAsXHTML
	(
		TableModel tableModel ,
		String title ,
		String[] columnTitles ,
		String fileName
	)
	{
		try
		{
			FileUtils.writeTextFile
			(
				fileName ,
				false ,
				tableModelDataToXHTML( tableModel , title , columnTitles , 0 )
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Saves the table data to a file.
	 *
	 *	@param	tableModel		Table model holding data to save.
	 *	@param	title			The title for the data.  Null if no title.
	 *	@param	columnTitles	The column titles.  Null if no titles.
	 *	@param	fileName		The file name to which to save the data.
	 *
	 *	<p>
	 *	The filename extension determines the type of output.
	 *	.csv -&gt; comma separated values, .tab -&gt; tab separated values,
	 *	and .htm or .html -&gt; xhtml.  If the filename
	 *	does not end in one of these, a comma separated file format
	 *	is used.
	 *	</p>
	 */

	public static void saveTableModelDataToFile
	(
		TableModel tableModel ,
		String title ,
		String[] columnTitles ,
		String fileName
	)
	{
								//	Get file name extension.

		String extension	=
			FileNameUtils.getFileExtension( fileName , false ).toLowerCase();

								//	Choose output type based upon
								//	extension.

		if ( extension.equals( "htm" ) || extension.equals( "html" ) )
		{
			saveTableModelDataToFileAsXHTML(
				tableModel , title , columnTitles , fileName );
		}
		else if ( extension.equals( "tab" ) )
		{
			saveTableModelDataToFileAsTAB(
				tableModel , title , columnTitles , fileName );
		}
		else
		{
			saveTableModelDataToFileAsCSV(
				tableModel , title , columnTitles , fileName );
		}
	}

	/**	Saves the table model data to a file.  Prompts for a file name.
	 *
	 *	@param	parentWindow	Parent window for file dialog.
	 *	@param	tableModel		The table model whose data is to be saved.
	 *	@param	title			The title for the data.
	 *	@param	columnTitles	The column titles.
	 */

	public static void saveTableModelDataToFile
	(
		Window parentWindow ,
		TableModel tableModel ,
		String title ,
		String[] columnTitles

	)
	{
		FileDialogs.addFileFilter( htmlFilter );
		FileDialogs.addFileFilter( tabFilter );
		FileDialogs.addFileFilter( csvFilter );

		String[] saveFile	= FileDialogs.save( parentWindow );

		FileDialogs.clearFileFilters();

		if ( saveFile != null )
		{
			File file	= new File( saveFile[ 0 ] , saveFile[ 1 ] );

			saveTableModelDataToFile
			(
				tableModel ,
				title ,
				columnTitles ,
				file.getAbsolutePath()
			);
		}
	}

	/** Don't allow instantiation but do allow overrides. */

	protected SaveTableModelData()
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


