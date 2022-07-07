package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.table.*;
import java.util.Vector;
import java.util.Enumeration;

/** XTableColumnModel extends DefaultTableColumnModel with methods
 *	to hide and unhide columns.
 *
 *	<p>
 *	Columns retain their relative positions when hidden and unhidden.
 *  </p>
 *
 *	<p>
 *	Hiding a column fires a columnRemoved event and unhiding a column
 *	fires a columnAdded event, and possibly a columnMoved event as well.
 *	</p>
 *
 *	<p>
 *	The following methods still deal with visible columns only:
 *	getColumnCount(), getColumns(), getColumnIndex(), getColumn() .
 *	Use the overloaded versions of these methods that take a parameter
 *	onlyVisible if you wish to account for hidden columns as well.
 *	</p>
 */

public class XTableColumnModel extends DefaultTableColumnModel
{
	/**	Holds all the column objects, regardless of visibility.
	 */

	protected Vector allTableColumns = new Vector();

	/**
	 * Create an extended table column model.
	 */

	public XTableColumnModel()
	{
		super();
	}

	/**
	 * Sets the visibility of the specified table column.
	 *
	 * @param	column		The column to hide/unhide.
	 * @param	isVisible	True to unhide the column, false to hide.
	 */

	public void setColumnVisible( TableColumn column , boolean isVisible )
	{
		if ( !isVisible )
		{
			super.removeColumn( column );
		}
		else
		{
								// Find the visible index of the column
								// by iterating through the visible columns
								// as well as all columns.  Count visible
								// columns up to the one that is to be
								// unhidden.

			int nVisibleColumns		= tableColumns.size();
			int nInvisibleColumns	= allTableColumns.size();
			int visibleIndex		= 0;

			for	(	int invisibleIndex = 0;
					invisibleIndex < nInvisibleColumns;
					++invisibleIndex )
			{
				TableColumn visibleColumn	=
					( ( visibleIndex < nVisibleColumns ) ?
						(TableColumn)tableColumns.get( visibleIndex ) : null );

				TableColumn testColumn =
					(TableColumn)allTableColumns.get( invisibleIndex );

				if ( testColumn == column )
				{
					if ( visibleColumn != column )
					{
						super.addColumn( column );

						super.moveColumn(
							tableColumns.size() - 1 , visibleIndex );
					}

					return;
				}

				if ( testColumn == visibleColumn )
				{
					++visibleIndex;
				}
			}
		}
	}

	/**
	 * Sets the visibility of the specified table column by index.
	 *
	 * @param	columnIndex		The inde of the column to hide/unhide.
	 * @param	isVisible		True to unhide the column, false to hide.
	 */

	public void setColumnVisible( int columnIndex , boolean isVisible )
	{
		setColumnVisible( getColumn( columnIndex ) , false );
	}

	/**
	 *	Make all columns in this model visible.
	 */

	public void setAllColumnsVisible()
	{
		int columnCount = allTableColumns.size();

		for ( int columnIndex = 0; columnIndex < columnCount; ++columnIndex )
		{
			TableColumn visibleColumn	=
				( ( columnIndex < tableColumns.size() ) ?
					(TableColumn)tableColumns.get( columnIndex ) : null );

			TableColumn invisibleColumn =
				(TableColumn)allTableColumns.get( columnIndex );

			if ( visibleColumn != invisibleColumn )
			{
				super.addColumn( invisibleColumn );
				super.moveColumn( tableColumns.size() - 1 , columnIndex );
			}
		}
	}

	/**
	 *	Maps the index of the column in the table model at
	 *	modelColumnIndex to the TableColumn object.
	 *
	 *	@param	modelColumnIndex	Index of column in table model.
	 *
	 *	@return 					Table column object or null if no such
	 *								column exists in this column model.
	 *
	 *	<p>
	 * 	There may be multiple TableColumn objects showing the same model
	 *	column, though this is uncommon.
	 *	</p>
	 *
	 *	<p>
	 *	This method will always return the first visible or else the first
	 *	invisible column with the specified index.
	 *	</p>
	 */

	public TableColumn getColumnByModelIndex( int modelColumnIndex )
	{
		for	(	int columnIndex = 0;
				columnIndex < allTableColumns.size();
				++columnIndex )
		{
			TableColumn column =
				(TableColumn)allTableColumns.elementAt( columnIndex );

			if ( column.getModelIndex() == modelColumnIndex )
			{
				return column;
			}
		}

		return null;
	}

	/** Checks whether a specified column is currently visible.
	 *
	 *	@param	column		Column to check.
	 *
	 *	@return 			True if specified column is visible, false
	 *						otherwise.
	 */

	public boolean isColumnVisible( TableColumn column )
	{
		return ( tableColumns.indexOf( column ) >= 0 );
	}

	/** Checks whether a specified column is currently visible.
	 *
	 *	@param	columnIndex		Index of column to check.
	 *
	 *	@return 				True if specified column is visible, false
	 *							otherwise.
	 */

	public boolean isColumnVisible( int columnIndex )
	{
		return isColumnVisible( getColumn( columnIndex ) );
	}

	/** Append column to the right of exisiting columns.
	 *
	 *	@param	column		The column to add.
	 *
	 *	@throws	IllegalArgumentException
	 *		if column is null.
	 */

	public void addColumn( TableColumn column )
	{
		allTableColumns.addElement( column );

		super.addColumn( column );
	}

	/** Removes column from the column model.
	 *
	 *	@param	column		Column to be removed.
	 */

	public void removeColumn( TableColumn column )
	{
		int allColumnsIndex = allTableColumns.indexOf( column );

		if ( allColumnsIndex != -1 )
		{
			allTableColumns.removeElementAt( allColumnsIndex );
		}

		super.removeColumn( column );
	}

	/** Moves column.
	 *
	 *	@param	oldIndex	Index of column to move.
	 *
	 *	@param	newIndex	New index for column.
	 *
	 *	@throws IllegalArgumentException
	 *		if either oldIndex or newIndex are not in
	 *			[0, getColumnCount() - 1].
	 */

	public void moveColumn( int oldIndex , int newIndex )
	{
		if	(	( oldIndex < 0 ) ||
				( oldIndex >= getColumnCount() ) ||
				( newIndex < 0 ) ||
				( newIndex >= getColumnCount() ) )
			throw new IllegalArgumentException( "Index out of range" );

		TableColumn fromColumn	=
			(TableColumn)tableColumns.get( oldIndex );

		TableColumn toColumn	=
			(TableColumn)tableColumns.get( newIndex );

		int allColumnsOldIndex = allTableColumns.indexOf( fromColumn );
		int allColumnsNewIndex = allTableColumns.indexOf( toColumn );

		if ( oldIndex != newIndex )
		{
			allTableColumns.removeElementAt( allColumnsOldIndex );

			allTableColumns.insertElementAt(
				fromColumn , allColumnsNewIndex );
		}

		super.moveColumn( oldIndex , newIndex );
	}

	/**
	 *	Returns the total number of columns in this model.
	 *
	 *	@param	onlyVisible		If true, only visible columns are counted.
	 *
	 *	@return					Number of columns.
	 */

	public int getColumnCount( boolean onlyVisible )
	{
		Vector columns = ( onlyVisible ? tableColumns : allTableColumns );

		return columns.size();
	}

	/**
	 *	Returns an enumeration of all the columns in the model.
	 *
	 *	@param	onlyVisible		If true, all invisible columns will be
	 *							excluded from the enumeration.
	 *
	 *	@return 				An enumeration of the columns in the model.
	 */

	public Enumeration getColumns( boolean onlyVisible )
	{
		Vector columns = ( onlyVisible ? tableColumns : allTableColumns );

		return columns.elements();
	}

	/**
	 *	Returns the position of first column matching specified identifier.
	 *
	 *	@param	identifier		Identifier object for which to search.
	 *	@param	onlyVisible		True to search only visible columns.
	 *
	 *	@return					Index of first column matching specified
	 *							identifier.
	 *
	 *	@throws					IllegalArgumentException
	 *								if identifier is null or no column matches.
	 */

	public int getColumnIndex( Object identifier , boolean onlyVisible )
	{
		if ( identifier == null )
		{
			throw new IllegalArgumentException( "Identifier is null" );
		}

		Vector	columns		= ( onlyVisible ? tableColumns : allTableColumns );
		int		columnCount	= columns.size();

		TableColumn column;

		for ( int columnIndex = 0; columnIndex < columnCount; ++columnIndex )
		{
			column = (TableColumn)columns.get( columnIndex );

			if ( identifier.equals( column.getIdentifier() ) )
				return columnIndex;
		}

		throw new IllegalArgumentException( "Identifier not found" );
	}

	/**
	 * Return the TableColumn object for the column at columnIndex.
	 *
	 * @param	columnIndex		Index of the desired column.
	 *
	 * @param	onlyVisible		If true, columnIndex is meant to be
	 *							relative to all visible columns only.
	 *							If false, it is the index in all columns.
	 *
	 * @return					TableColumn object for the column
	 *							at columnIndex.
	 */

	public TableColumn getColumn( int columnIndex , boolean onlyVisible )
	{
		return (TableColumn)tableColumns.elementAt( columnIndex );
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

