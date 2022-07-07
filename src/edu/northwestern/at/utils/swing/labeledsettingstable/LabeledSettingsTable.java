package edu.northwestern.at.utils.swing.labeledsettingstable;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.rmi.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	A two-column table for displaying labeled settings.
 *
 *	<p>
 *	This class subclasses JTable to provide a two-column
 *	table.  The first column is a label, and the second
 *	column is a value.
 *	</p>
 */

public class LabeledSettingsTable extends XTable
{
	/** The table model for the settings. */

	protected LabeledSettingsTableModel tableModel;

	/**	The setting name cell renderer. */

	protected LabeledSettingsNameCellRenderer settingNameCellRenderer;

	/**	The setting value cell renderer. */

	protected LabeledSettingsValueCellRenderer settingValueCellRenderer;

	/** Remembers setting value text pane settings.
	 *
	 *	<p>
	 *	Entries are of type "LabeledSettingsValueTextPaneSettings" above.
	 *	</p>
	 */

	protected HashMap savedPaneSettings;

	/**	Maximum name column width for display. */

	protected int maxWidthNameCol	= 0;

	/**	Maximum value column width for display. */

	protected int maxWidthValueCol	= 0;

	/** Create labeled settings table.
	 * @param	columnNames	Array of column names.
	*/

	public LabeledSettingsTable( String[] columnNames )
	{
		super();
								//	Create hash map to hold text panes
								//	which display values.

		savedPaneSettings	= new HashMap();

								//	Get table model.

		tableModel			= new LabeledSettingsTableModel( columnNames );

		setModel( tableModel );

								//	Get column model.

		DefaultTableColumnModel tableColumnModel =
			(DefaultTableColumnModel)getColumnModel();

								//	Get sans serif fonts for cell renderers.

		Font font					= getFont();

		Font labelFont				=
			new Font( Fonts.sansSerif , Font.BOLD , font.getSize() );

		Font valueFont				=
			new Font( Fonts.sansSerif , Font.PLAIN , font.getSize() );

								//	Set cell renderers.

		settingNameCellRenderer		=
			new LabeledSettingsNameCellRenderer( labelFont );

		settingValueCellRenderer	=
			new LabeledSettingsValueCellRenderer
			(
				this ,
				valueFont ,
				savedPaneSettings
			);

		setColumnRenderer( 0 , settingNameCellRenderer );
		setColumnRenderer( 1 , settingValueCellRenderer );

								//	If column names both empty, don't display
								//	a column header line.

		String labelColumnName		=
			StringUtils.safeString( columnNames[ 0 ] );

		String settingColumnName	=
			StringUtils.safeString( columnNames[ 1 ] );

		if	(	( labelColumnName.length() == 0 ) &&
				( settingColumnName.length() == 0 ) )
		{
			setTableHeader( null );
		}
								//	Add a few pixels of spacing between
								//	columns.

		tableColumnModel.setColumnMargin( 10 );

								//	Neither rows nor columns may be selected.

		setRowSelectionAllowed( false );

		setColumnSelectionAllowed( false );

								//	Add mouse listener and mouse motion listener
								//	to pass on mouse clicks and moves to text
								//	panes used for displaying the values.

		addMouseListener( settingsMouseListener );
		addMouseMotionListener( settingsMouseMotionListener );
	}

	/**	Updates the maximum column widths.
	 *
	 *	@param	row		Update maximum columns widths with values in this row.
	 */

	protected void doUpdateColumnWidths( int row )
	{
		Object o		= tableModel.getValueAt( row , 0 );

		Component comp	=
			settingNameCellRenderer.getTableCellRendererComponent
			(
				this , o , false , false , row , 0
			);

		int width	= comp.getPreferredSize().width + 10;

		if ( width > maxWidthNameCol ) maxWidthNameCol = width;

		o		= tableModel.getValueAt( row , 1 );

		comp	=
			settingValueCellRenderer.getTableCellRendererComponent
			(
				this , o , false , false , row , 1
			);

		width	= comp.getPreferredSize().width + 10;

		if ( width > maxWidthValueCol ) maxWidthValueCol = width;

		TableColumn column	= getColumnModel().getColumn( 0 );

		column.setPreferredWidth( maxWidthNameCol );
		column.setMinWidth( maxWidthNameCol );
		column.setMaxWidth( maxWidthNameCol );

		column				= getColumnModel().getColumn( 1 );

		column.setPreferredWidth( maxWidthValueCol );
	}

	/** Override standard method to disallow cell editing.
	 */

    public boolean isCellEditable( int row , int column )
	{
		return false;
	}

	/**	Internal set width.  Ensures set done on AWT event thread.
	 */

	protected void updateColumnWidths()
	{
		final int lastRow	= tableModel.getRowCount() - 1;

		if ( lastRow < 0 ) return;

		if ( SwingUtilities.isEventDispatchThread() )
		{
			doUpdateColumnWidths( lastRow );
		}
		else
		{
			SwingUtilities.invokeLater
			(
				new Runnable()
				{
					public void run()
					{
						doUpdateColumnWidths( lastRow );
					}
				}
			);
		}
	}

	/** Add labeled setting to table.
	 *
	 *	@param	settingName		Setting name
	 *	@param	settingValue	Setting value
	 */

	public void addSetting( String settingName , Object settingValue )
	{
		tableModel.addSetting( settingName , settingValue );

		updateColumnWidths();
	}

	/** Add labeled setting to table.
	 *
	 *	@param	settingName		Setting name
	 *	@param	settingValue	Setting value
	 */

	public void addSetting( String settingName , int settingValue )
	{
		tableModel.addSetting
		(
			settingName ,
			Formatters.formatIntegerWithCommas( settingValue )
		);

		updateColumnWidths();
	}

	/** Add labeled setting to table.
	 *
	 *	@param	settingName		Setting name
	 *	@param	settingValue	Setting value
	 */

	public void addSetting( String settingName , long settingValue )
	{
		tableModel.addSetting
		(
			settingName ,
			Formatters.formatLongWithCommas( settingValue )
		);

		updateColumnWidths();
	}

	/** Add labeled setting to table.
	 *
	 *	@param	settingName		Setting name
	 *	@param	settingValue	Setting value
	 */

	public void addSetting( String settingName , boolean settingValue )
	{
		String settingValueString = StringUtils.yesNo( settingValue );

		tableModel.addSetting( settingName , settingValueString );

		updateColumnWidths();
	}

	/** Erases current settings display. */

	public void eraseAllSettings()
	{
								// Clear the table of settings.

		tableModel.setRowCount( 0 );

								//	Reset columns widths to zero.

		maxWidthNameCol		= 0;
		maxWidthValueCol	= 0;
	}

	/** Forward a mouse event from the table to the component
	 *	rendering a table cell.
	 *
	 *	@param	e	The mouse event.
	 */

	protected void forwardEventToXTextPane( MouseEvent e )
	{
		TableColumnModel columnModel = getColumnModel();

								// Determine which column the mouse
								// was clicked on.

		int column	= columnModel.getColumnIndexAtX( e.getX() );

								// We are only interested in forwarding
								// mouse clicks on the second column
								// which contains the text panes for
								// setting values.

		if ( column == 1 )
		{
								// Figure out which row the
								// mouse was clicked on.

			int row = rowAtPoint( e.getPoint() );

								// Get the renderer for the table cell
								// at that row and column.

			Object value;

			try
			{
				value = getValueAt( row , column );
			}
			catch ( ArrayIndexOutOfBoundsException e2 )
			{
				return;
			}
								// If it's not an XTextPane, ignore
								// the mouse click.

			if ( value instanceof XTextPane )
			{
								// Otherwise get the saved text pane
								// which rendered the cell which was
								// clicked on.  The problem is that
								// this text pane will now have
								// coordinates off-screen, and a bogus
								// size.  We must restore the size of
								// the text pane to that which it had
								// when it was painted.

				LabeledSettingsValueTextPaneSettings oldSettings =
					(LabeledSettingsValueTextPaneSettings)savedPaneSettings.get(
						new Integer( row ) );

				if ( oldSettings == null ) return;

								// Set the text pane to its last
								// previously painted size.

				oldSettings.textPane.setSize(
					oldSettings.size.width , oldSettings.size.height );

								// Now we must adjust the location of
								// the mouse click to account for the
								// off-screen location of the saved
								// text pane.
								//
								// First get the text pane's current
								// off-screen location.

				Point currentTextPaneLocation =
					oldSettings.textPane.getLocation();

								// Now get the text pane's location
								// when it was last painted.

				Point oldTextPaneLocation =
					oldSettings.location;

								// Get the mouse event.

				MouseEvent mouseEvent = (MouseEvent)e;

								// Adjust the mouse event so it fits
								// within the boundaries of the previously
								// painted location for the text pane.

				mouseEvent.translatePoint
				(
					-oldTextPaneLocation.x ,
					-oldTextPaneLocation.y
				);
								// Now adjust the mouse event so it fits
								// within the text pane's current
								// off-screen location.

				mouseEvent.translatePoint
				(
					currentTextPaneLocation.x ,
					currentTextPaneLocation.y
				);
								// Send the fixed up mouse event to
								// the text pane rendering the specified
								// table cell.
				try
				{
					oldSettings.textPane.dispatchEvent( mouseEvent );
				}
				catch ( IllegalArgumentException ee )
				{
//					ee.printStackTrace();
				}
								// Need to repaint or things don't display
								// correctly.
				repaint();
								// Set cursor to the current text pane cursor.

				Cursor cursor = oldSettings.textPane.getCurrentCursor();

				LabeledSettingsTable.this.setCursor( cursor );
			}
			else
			{
				repaint();
								// Make sure the default cursor is displayed.

				Cursor cursor = LabeledSettingsTable.this.getCursor();

				if ( cursor != Cursors.DEFAULT_CURSOR )
					LabeledSettingsTable.this.setCursor(
						Cursors.DEFAULT_CURSOR );
			}
		}
		else
		{
								// Make sure the default cursor is displayed.

			Cursor cursor = LabeledSettingsTable.this.getCursor();

			if ( cursor != Cursors.DEFAULT_CURSOR )
				LabeledSettingsTable.this.setCursor(
					Cursors.DEFAULT_CURSOR );
		}
	}

	/** Mouse listener for the table.
	 *
	 *	<p>
	 *	This passes on mouse clicks to the table cells.  This allows
	 *	links embedded in settings values XTextPanes to activate correctly.
	 *	</p>
	 */

	MouseListener settingsMouseListener =
		new MouseAdapter()
		{
			/** Pass on click to table cell. */

			public void mouseClicked( MouseEvent e )
			{
				forwardEventToXTextPane( e );
			}
		};

	/** Mouse motion listener for the table.
	 *
	 *	<p>
	 *	This passes on mouse moves to the table cells.  This allows
	 *	links embedded in settings values XTextPanes to change the cursor
	 *	when a link is entered.
	 *	</p>
	 */

	MouseMotionListener settingsMouseMotionListener =
		new MouseMotionAdapter()
		{
			/** Pass on mouseMoved event. */

			public void mouseMoved( MouseEvent e )
			{
				forwardEventToXTextPane( e );
			}
		};
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

