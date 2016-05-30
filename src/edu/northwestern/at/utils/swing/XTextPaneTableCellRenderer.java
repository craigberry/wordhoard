package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.rmi.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.swing.styledtext.*;

/**	Table cell renderer class for styled text.
 *
 *	<p>
 *	The message column is rendered as an XTextPane with variable height.
 *	The height is adjusted properly to exactly contain the wrapped
 *	setting value text.  Links are also properly maintained.
 *	</p>
 */

public class XTextPaneTableCellRenderer extends XTextPane
	implements TableCellRenderer
{
	/** The table to which this renderer is attached. */

	protected JTable view;

	/** The table row of the current cell to render. */

	protected int row;

	/** The styled text string to display. */

	protected StyledString styledValue;

	/** The links in the styled text. */

	protected XTextPane.LinkInfo[] links;

	/**	The font for the styled text. */

	protected Font font;

	/** Create the renderer.
	 *
	 *	@param	view	The JTable to which this renderer is attached.
	 */

	public XTextPaneTableCellRenderer( JTable view , Font font )
	{
		this.view	= view;
		this.font	= font;

		setBorder( 1 );
	}

	/** Return renderer.
	 *
	 *	@return		The XTextPane-based tree cell renderer.
	 */

	public Component getTableCellRendererComponent
	(
		JTable table,
		Object value,
		boolean isSelected,
		boolean hasFocus,
		int row,
		int column
	)
	{
								// Remember the table row so we can
								// adjust the height as necessary
								// to match the height of the styled text.
		this.row	= row;
		this.links	= null;
								// Set foreground and background
								// colors based upon whether the
								// current row is selected or not.
		if ( isSelected )
		{
			setForeground( table.getSelectionForeground() );
			setBackground( table.getSelectionBackground() );
		}
		else
		{
			setForeground( table.getForeground() );
			setBackground( table.getBackground() );
		}
								// Set opaque so text doesn't vanish.
		setOpaque( true );
								// Null value?  Display blank.
		if ( value == null )
		{
			styledValue = new StyledString();
		}
								// Styled string?  Display that.

		else if ( value instanceof StyledString )
		{
			styledValue = (StyledString)value;
		}
								// XTextPane?  Extract styled text
								// and links and display those.

		else if ( value instanceof XTextPane )
		{
			styledValue =
				(StyledString)((XTextPane)value).getStyledText();

			links = ((XTextPane)value).getLinks();
		}
								// Anything else -- assume toString()
								// gets a reasonable display value.
		else
		{
			styledValue =
				new StyledString( value.toString() , null );
		}
								// Set the styled text into the
								// text pane.

		setStyledText( styledValue );

								//	Set font if given.
		if ( font != null )
		{
			setFont( font );
		}
								// Set any specified links.

		if ( links != null ) setLinks( links );

		return this;
	}

	/** Paint the table cell.
	 *
	 *	@param	g		The graphics context for the table cell.
	 *
	 *	<p>
	 *	The height of the cell is adjusted to match the height of
	 *	the styled text constructed from the setting value.
	 *	</p>
	 */

	public void paint( Graphics g )
	{
								// Set the styled text into the
								// text pane.  Yes, it's redundant,
								// but needed for java 1.3 and/or
								// MacOSX.

		setStyledText( styledValue );

		if ( font != null )
		{
			setFont( font );
		}
								// Set any specified links.

		if ( links != null ) setLinks( links );

								// Revise the row height to match
								// the size of the line wrapped
								// styled text.

		int height = (int)getPreferredSize().getHeight();

		if ( height != view.getRowHeight( row ) )
		{
			view.setRowHeight( row , height );
		}
                                // Paint the styled text pane.
		super.paint( g );
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

