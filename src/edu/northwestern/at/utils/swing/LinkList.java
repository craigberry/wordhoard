package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** LinkList:  a list containing clickable linked items.
 */

public class LinkList extends XList
{
	/** Create link list.
	 *
	 *	@param 	labels		Text for each list entry.
	 *	@param	links		Links for each list entry.
	 */

	public LinkList( String[] labels , Link[] links )
	{
		super();

		LinkLabel[] linkLabels = new LinkLabel[ labels.length ];

		for ( int i = 0; i < labels.length; i++ )
		{
			linkLabels[ i ] =
				new LinkLabel( labels[ i ] , links[ i ] );
		}

		setListData( linkLabels );

								// Add custom combobox renderer.

		setCellRenderer( new LinkListRenderer() );

								// Add mouse motion listener.

		addMouseMotionListener( mouseMotionListener );

								// Add new mouse listener.

		addMouseListener( mouseListener );
	}

	/** Mouse listener.  Changes list item checked status on clicks. */

	protected MouseAdapter mouseListener =
	(
		new MouseAdapter()
		{
			public void mouseClicked( MouseEvent e )
			{
				forwardEventToListCells( e );
			}
		}
	);

	/** Mouse motion listener for the list.
	 *
	 *	<p>
	 *	This passes on mouse moves to the list cells.
	 *	</p>
	 */

	protected MouseMotionListener mouseMotionListener =
		new MouseMotionAdapter()
		{
			/** Pass on mouseMoved event. */

    		public void mouseMoved( MouseEvent e )
			{
				forwardEventToListCells( e );
			}
		};

	/** Process a mouse move event from the list to the component
	 *	rendering a list cell.
	 *
	 *	@param	e				The mouse event.
	 */

	protected void forwardEventToListCells( MouseEvent e )
	{
							// Translate the mouse move point to
							// the coordinates for the workLinkLabel.
							// We do this by subtracting out the
							// heights of all previous rows from the
							// y coordinate of the mouse move point.

		Point point = e.getPoint();

		int index = locationToIndex( point );

		point.y = point.y - index * getFixedCellHeight();

							// If the mouse move point is over the
							// link text, set the hand cursor.
							// If the mouse move point is not over the
							// link text, set the default cursor.

		LinkLabel linkLabel =
			(LinkLabel)getModel().getElementAt( index );

		if ( linkLabel.pointOverLink( point ) )
		{
			setCursor( Cursors.HAND_CURSOR );
		}
		else
		{
			Cursor cursor = getCursor();

			if ( cursor != Cursors.DEFAULT_CURSOR )
				setCursor( Cursors.DEFAULT_CURSOR );
		}

		MouseEvent mouseEvent = e;

		mouseEvent.translatePoint( 0 , -index * getFixedCellHeight() );

        try
		{
			linkLabel.dispatchEvent( mouseEvent );
		}
		catch ( IllegalArgumentException ee )
		{
		}
	}

	private class LinkListRenderer
		extends LinkLabel implements ListCellRenderer
	{
		/** Create a renderer.  Set it to be opaque. */

		public LinkListRenderer()
		{
			setOpaque( true );

			setBackground( UIManager.getColor( "List.textBackground" ) );
			setForeground( UIManager.getColor( "List.textForeground" ) );
		}

		/** Create a link list entry renderer.
		 *
		 *	@param	list			The JList/JCheckBoxList we're painting.
		 *	@param	value			The value returned by
		 *							list.getModel().getElementAt(index).
		 *	@param	index			The cells index.
		 *	@param	isSelected		True if the specified cell was selected.
		 *	@param	cellHasFocus	True if the specified cell has the focus.
		 *	@return					A component whose paint() method will
		 *							render the specified value.
		 */

		public Component getListCellRendererComponent
		(	JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus
		)
		{
			return (Component)value;
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

