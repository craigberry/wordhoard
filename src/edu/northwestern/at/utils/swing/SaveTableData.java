package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import edu.northwestern.at.utils.*;

/**	Saves data stored in a JTable to a file is several different formats.
 */

public class SaveTableData extends SaveTableModelData
{
	/**	Get column titles for a table.
	 *
	 *	@param	table	The table whose column titles are desired.
	 *
	 *	@return			String array of column titles.
	 */

	public static String[] getColumnTitles( JTable table )
	{
		String[] columnTitles	= null;

		if ( table != null )
		{
			TableColumnModel colModel	= table.getColumnModel();

			int columns					= colModel.getColumnCount();

			ArrayList titleList			= new ArrayList();

			for ( int column = 0 ; column < columns ; column++ )
			{
								//	Get next column header.

				TableColumn tableColumn	= colModel.getColumn( column );

								//	Get the column header text.
								//	Ignore zero width columns.

				if ( tableColumn.getPreferredWidth() > 0 )
				{
					titleList.add( tableColumn.getIdentifier() );
				}
			}

			columnTitles				=
				(String[])titleList.toArray( new String[]{} );
		}

		return columnTitles;
	}

	/**	Convert contents of a JTable to comma separated values.
	 *
	 *	@param	table					The table containing the data to
	 *									convert to command separated values.
	 *
	 *	@param	title					Title for table.
	 *
	 *	@param	outputColumnTitles		true to output column headers.
	 *
	 *	@return							The comma separated table data.
	 *									Each table row appears on a new line.
	 */

	public static String tableDataToCSV
	(
		JTable table ,
		String title ,
		boolean outputColumnTitles
	)
	{
		String result	= "";

		if ( table != null )
			result	=
				tableModelDataToCSV
				(
//					table.getModel() ,
					new TableModelMapper( table ) ,
					title ,
					outputColumnTitles ? getColumnTitles( table ) : null
				);

		return result;
	}

	/**	Convert contents of a Table to tab separated values.
	 *
	 *	@param	table					The table containing the data to
	 *									convert to command separated values.
	 *
	 *	@param	title					Title for table.
	 *
	 *	@param	outputColumnTitles		true to output column headers.
	 *
	 *	@return							The tab separated table data.
	 *									Each table row appears on a new line.
	 */

	public static String tableDataToTAB
	(
		JTable table ,
		String title ,
		boolean outputColumnTitles
	)
	{
		String result	= "";

		if ( table != null )
			result	=
				tableModelDataToTAB
				(
//					table.getModel() ,
					new TableModelMapper( table ) ,
					title ,
					outputColumnTitles ? getColumnTitles( table ) : null
				);

		return result;
	}

	/**	Convert contents of a Table to XHTML.
	 *
	 *	@param	table					The table containing the data to
	 *									convert to command separated values.
	 *
	 *	@param	title					Title for table.
	 *
	 *	@param	outputColumnTitles		true to output column headers.
	 *
	 *	@param	border					Border size for table.  Set to 0 for
	 *									no border.
	 *
	 *	@return							The XHTML formatted table data.
	 */

	public static String tableDataToXHTML
	(
		JTable table ,
		String title ,
		boolean outputColumnTitles ,
		int border
	)
	{
		String result	= "";

		if ( table != null )
			result	=
				tableModelDataToXHTML
				(
//					table.getModel() ,
					new TableModelMapper( table ) ,
					title ,
					outputColumnTitles ? getColumnTitles( table ) : null ,
					border
				);

		return result;
	}

	/**	Saves the table model data to a file in CSV format.
	 *
	 *	@param	table					The table containing the data to save.
	 *	@param	title					The title for the data.
	 *	@param	outputColumnTitles		true to output column titles.
	 *	@param	fileName				The file name to which to save the data.
	 */

	public static void saveTableDataToFileAsCSV
	(
		JTable table ,
		String title ,
		boolean outputColumnTitles ,
		String fileName
	)
	{
		try
		{
			FileUtils.writeTextFile
			(
				fileName ,
				false ,
				tableDataToCSV( table , title , outputColumnTitles )
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Saves the table model data to a file in TAB format.
	 *
	 *	@param	table					The table containing the data to save.
	 *	@param	title					The title for the data.
	 *	@param	outputColumnTitles		true to output column titles.
	 *	@param	fileName				The file name to which to save the data.
	 */

	public static void saveTableDataToFileAsTAB
	(
		JTable table ,
		String title ,
		boolean outputColumnTitles ,
		String fileName
	)
	{
		try
		{
			FileUtils.writeTextFile
			(
				fileName ,
				false ,
				tableDataToTAB( table , title , outputColumnTitles )
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Saves the table model data to a file in XHTML format.
	 *
	 *	@param	table					The table containing the data to save.
	 *	@param	title					The title for the data.
	 *	@param	outputColumnTitles		true to output column titles.
	 *	@param	fileName				The file name to which to save the data.
	 */

	public static void saveTableDataToFileAsXHTML
	(
		JTable table ,
		String title ,
		boolean outputColumnTitles ,
		String fileName
	)
	{
		try
		{
			FileUtils.writeTextFile
			(
				fileName ,
				false ,
				tableDataToXHTML( table , title , outputColumnTitles , 0 )
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Saves the table data to a file.
	 *
	 *	@param	table				Table holding data to save.
	 *	@param	title				The title for the data.  Null if no title.
	 *	@param	outputColumnTitles	true to output column titles.
	 *	@param	fileName			The file name to which to save the data.
	 *
	 *	<p>
	 *	The filename extension determines the type of output.
	 *	.csv -> comma separated values, .tab -> tab separate values,
	 *	and .htm or .html -> xhtml.  If the filename
	 *	does not end in one of these, a comma separated file format
	 *	is used.
	 *	</p>
	 */

	public static void saveTableDataToFile
	(
		JTable table ,
		String title ,
		boolean outputColumnTitles ,
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
			saveTableDataToFileAsXHTML(
				table , title , outputColumnTitles , fileName );
		}
		else if ( extension.equals( "tab" ) )
		{
			saveTableDataToFileAsTAB(
				table , title , outputColumnTitles , fileName );
		}
		else
		{
			saveTableDataToFileAsCSV(
				table , title , outputColumnTitles , fileName );
		}
	}

	/**	Saves the table model data to a file.  Prompts for a file name.
	 *
	 *	@param	parentWindow			Parent window for file dialog.
	 *	@param	table					The table whose data is to be saved.
	 *	@param	title					The title for the table data.
	 *	@param	outputColumnTitles		true to output column titles.
	 */

	public static void saveTableDataToFile
	(
		Window parentWindow ,
		JTable table ,
		String title ,
		boolean outputColumnTitles
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

			saveTableDataToFile
			(
				table ,
				title ,
				outputColumnTitles ,
				file.getAbsolutePath()
			);
		}
	}

	/** Don't allow instantiation but do allow overrides. */

	protected SaveTableData()
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


