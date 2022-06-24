package edu.northwestern.at.utils.db.mysql;

/*	Please see the license information at the end of this file. */


/**	A MySQL insert statement generator.
 *
 *	<p>
 *	This class provides for constructing a MySQL-specific SQL Insert
 *	statement that takes multiple value lists in one insert.  This kind of
 *	insert typically runs an order of magnitude faster than individual inserts.
 *	</p>
 */

public class MySQLInsertGenerator
{
	/**	The name of the database table into which to insert the data rows.
	 */

	protected String tableName;

	/**	The name of the column fields, in order, for each data row.
	 */

	protected String[] fieldNames;

	/**	True if a field is a number, false otherwise.  Use to determine
	 *	how to format data values in the generated insert statement.
	 */

	protected boolean[] isNumeric;

	/**	The string buffer in which to build the insert statement.
	 */

	protected StringBuffer insertBuffer;

	/**	Create a MySQL insert generator.
	 *
	 *	@param	tableName	The database table name to receive the data.
	 *	@param	fieldNames	The name of the column fields, in order, for
	 *						each data row.
	 *	@param	isNumeric	True if the associated field is numeric, false
	 *						for a string.
	 */

	public MySQLInsertGenerator
	(
		String tableName ,
		String[] fieldNames ,
		boolean[] isNumeric
	)
	{
		this.tableName		= tableName;
		this.fieldNames		= fieldNames;
		this.isNumeric		= isNumeric;

		this.insertBuffer	= new StringBuffer();
	}

	/**	Escapes single quotes in a data value.
	 *
	 *	@param	value	The value to escape.
	 *
	 *	@return			The value with single quotes escaped using \' .
	 */

	protected String escapeSingleQuotes( String value )
	{
		String result	= value;

		if ( result.indexOf( "'" ) >= 0 )
		{
			StringBuffer sb	= new StringBuffer();

			for ( int i = 0 ; i < value.length() ; i++ )
			{
				char ch	= result.charAt( i );

				if ( ch == '\'' )
				{
					sb.append( "\\'" );
				}
				else
				{
					sb.append( ch );
				}
			}

			result	= sb.toString();
		}

		return result;
	}

	/**	Add a row of data.
	 *
	 *	@param	rowData		An Object[] array containing the data values.
	 *
	 *	<p>
	 *	Each row value must have a proper toString() method defined.
	 *	The number of row values must match the number of field names
	 *	passed in the contructor.  If there are two many values, the
	 *	extra values are ignored.  If there are too few values, database
	 *	null values are added.
	 *	</p>
	 */

	public void addRow( Object[] rowData )
	{
								//	If the buffer is empty,
								//	generator the initial part of the
								//	insert statement.

		if ( insertBuffer.length() == 0 )
		{
			insertBuffer.append( "insert into " );
			insertBuffer.append( tableName );
			insertBuffer.append( "(" );

			for ( int i = 0 ; i < fieldNames.length ; i++ )
			{
				if ( i > 0 ) insertBuffer.append( ", " );
				insertBuffer.append( fieldNames[ i ] );
			}

			insertBuffer.append( ") values (" );
		}
		else
		{
			insertBuffer.append( ", (" );
		}
								//	Append data values for this row
								//	to the insert statement.

		for	(	int i = 0 ;
				i < Math.min( rowData.length , fieldNames.length ) ;
				i++
			)
		{
			if ( i > 0 )
			{
				insertBuffer.append( "," );
			}

			if ( rowData[ i ] == null )
			{
				insertBuffer.append( "NULL" );
			}
			else
			{
				String rowValue	= rowData[ i ].toString();

				if ( !isNumeric[ i ] )
				{
					insertBuffer.append( "'" );
					rowValue	= escapeSingleQuotes( rowValue );
				}

				insertBuffer.append( rowValue );

				if ( !isNumeric[ i ] )
				{
					insertBuffer.append( "'" );
				}
			}
		}
								//	Fill out too-short row data
								//	with database nulls.  The extra data at
								//	the end of too-long rows will be ignored.

		for	( int i = rowData.length ; i < fieldNames.length ; i++ )
		{
			if ( i > 0 )
			{
				insertBuffer.append( "," );
			}

			insertBuffer.append( "NULL," );
		}

		insertBuffer.append( ")" );
	}

	/**	Get the insert statement.
	 *
	 *	@return		The completed insert statement.
	 *
	 *	<p>
	 *	The string buffer used to build the insert statement is emptied.
	 *	Any subsequent calls to addRow will start a new insert statement
	 *	with the same table name and field names are defined in the
	 *	constructor call.
	 *	</p>
	 */

	public String getInsert()
	{
		String result	= insertBuffer.toString();

		insertBuffer	= new StringBuffer();

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

