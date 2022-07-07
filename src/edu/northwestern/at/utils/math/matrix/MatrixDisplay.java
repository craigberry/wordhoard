package edu.northwestern.at.utils.math.matrix;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.swing.*;

/**	Displays matrix entries in a GUI table.
 */

public class MatrixDisplay
{
	/**	Default matrix entry format string.
	 */

	protected static String defaultFormatString	= "%26.18g";

	/** Displays matrix entries in a GUI table.
	 *
	 *	@param	matrix			The matrix to display.
	 *	@param	title			Title for matrix.
	 *	@param	rowNames		Row names.
	 *	@param	columnNames		Column names.
	 *	@param	formatString	PrintFformat format string for all entries.
	 *	@return XFrame containing the matrix.
	 */

	public static XFrame displayMatrix
	(
		Matrix matrix ,
		String title ,
		String[] rowNames ,
		String[] columnNames ,
		String formatString
	)
	{
		MatrixTablePanel tablePanel	=
			new MatrixTablePanel( matrix , formatString );

		tablePanel.setColumnLabels( columnNames );
  		tablePanel.setRowLabels( rowNames );

		return
			(XFrame)SwingUtils.openInSwingFrame
			(
				new XFrame() ,
				tablePanel ,
				null ,
				400 ,
				400 ,
				title ,
				tablePanel.getBackground() ,
				null
			);
	}

	/** Displays matrix entries in a GUI table.
	 *
	 *	@param	matrix			The matrix to display.
	 *	@param	title			Title for matrix.
	 *	@param	rowNames		Row names.
	 *	@param	columnNames		Column names.
	 *	@return XFrame containing the matrix.
	 */

	public static XFrame displayMatrix
	(
		Matrix matrix ,
		String title ,
		String[] rowNames ,
		String[] columnNames
	)
	{
		return
			displayMatrix(
				matrix ,
				title ,
				rowNames ,
				columnNames ,
				defaultFormatString );
	}

	/** Displays matrix entries in a GUI table.
	 *
	 *	@param	matrix			The matrix to display.
	 *	@param	title			Title for matrix.
	 *	@param	formatString	PrintFformat format string for all entries.
	 *	@return XFrame containing the matrix.
	 */

	public static XFrame displayMatrix
	(
		Matrix matrix ,
		String title ,
		String formatString
	)
	{
		return
			displayMatrix(
				matrix ,
				title ,
				generateRowLabels( matrix ) ,
				generateColumnLabels( matrix ) ,
				formatString );
	}

	/** Displays matrix entries in a GUI table.
	 *
	 *	@param	matrix			The matrix to display.
	 *	@param	title			Title for matrix.
	 *	@return XFrame containing the matrix.
	 */

	public static XFrame displayMatrix
	(
		Matrix matrix ,
		String title
	)
	{
		return
			displayMatrix(
				matrix ,
				title ,
				generateRowLabels( matrix ) ,
				generateColumnLabels( matrix ) ,
				defaultFormatString );
	}

	/** Displays matrix entries in a GUI table.
	 *
	 *	@param	matrix			The matrix to display.
	 *	@return XFrame containing the matrix.
	 */

	public static XFrame displayMatrix
	(
		Matrix matrix
	)
	{
		return
			displayMatrix(
				matrix ,
				generateTitle( matrix ) ,
				generateRowLabels( matrix ) ,
				generateColumnLabels( matrix ) ,
				defaultFormatString );
	}

	/** Displays matrix entries in a GUI table.
	 *
	 *	@param	matrix			The matrix to display.
	 *
	 *	@return					Matrix title.
	 *
	 *	<p>
	 *	The title takes the form:
	 *	</p>
	 *
	 *	<p>
	 *	r x c matrix
	 *	</p>
	 *
	 *	<p>
	 *	where "r" is the number of rows in the matrix and
	 *	"c" is the number of columns in the matrix.
	 *	</p>
	 */

	public static String generateTitle( Matrix matrix )
	{
		return matrix.rows() + " x " + matrix.columns() + " matrix";
	}

	/** Generate row labels.
	 *
	 *	@param	matrix			The matrix for which to create labels.
	 *
	 *	@return					String array of row labels.
	 */

	public static String[] generateRowLabels( Matrix matrix )
	{
		String[] result	= new String[ matrix.rows() ];

		for ( int i = 0 ; i < result.length ; i++ )
		{
			result[ i ]	= "Row " + ( i + 1 );
		}

		return result;
	}

	/** Generate column labels.
	 *
	 *	@param	matrix			The matrix for which to create labels.
	 *
	 *	@return					String array of column labels.
	 */

	public static String[] generateColumnLabels( Matrix matrix )
	{
		String[] result	= new String[ matrix.columns() ];

		for ( int i = 0 ; i < result.length ; i++ )
		{
			result[ i ]	= "Column " + ( i + 1 );
		}

		return result;
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

