package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;

/**	Handles mouse events for drag and drop operations on JTable.
 */

public class TableDragMouseHandler
	implements MouseListener, MouseMotionListener
{
	/**	Mouse pressed event which may be initiating a drag.
	 */

	MouseEvent firstMouseEvent	= null;

	/**	Handle mouse pressed event.
	 *
	 *	@param	e	The mouse pressed event.
	 *
	 *	<p>
	 *	Saves the mouse pressed event for later reference.
	 *	The event is also consumed here.
	 *	</p>
	 */

	public void mousePressed( MouseEvent e )
	{
		firstMouseEvent = e;
		e.consume();
	}

	public void mouseReleased( MouseEvent e)
	{
	}

	public void mouseClicked( MouseEvent event )
	{
	}

	public void mouseEntered( MouseEvent e )
	{
	}

	public void mouseExited( MouseEvent e )
	{
	}

	/**	Handle mouse dragged event.
	 *
	 *	@param	e	The mouse dragged event.
	 *
	 *	<p>
	 *	If a previous mouse pressed event occurred,
	 *	we check to see if the position of the drag event
	 *	is at least 5 pixels away in both the horizontal
	 *	and vertical directions.  If so, we determine if
	 *	the drag event is a copy or a move, and initiate
	 *	the drag by creating a transfer handler on the
	 *	selected component.
	 *	</p>
	 */

	public void mouseDragged( MouseEvent e )
	{
								//	No previous mouse pressed?  Ignore
								//	this drag event.

		if ( firstMouseEvent != null )
		{
								//	Consume the mouse dragged event.
			e.consume();

								//	Determine if the drag event is
								//	a copy or a move by examining
								//	the status of the shift and ctrl keys.

			int ctrlMask	= InputEvent.CTRL_DOWN_MASK;
			int action		=
				( ( e.getModifiersEx() & ctrlMask ) == ctrlMask) ?
					TransferHandler.COPY : TransferHandler.MOVE;

								//	See if we are far enough away
								//	from the original mouse pressed
								//	event for this event to be a
								//	real drag.

			int dx	= Math.abs( e.getX() - firstMouseEvent.getX() );
			int dy	= Math.abs( e.getY() - firstMouseEvent.getY() );

			if ( ( dx > 5 ) || ( dy > 5 ) )
			{
								//	If this is a real drag,
								//	get the component from which we
								//	are dragging.  Create a
								//	transfer handler and request
								//	the handler to perform the drag.

				JComponent c			= (JComponent)e.getSource();
				TransferHandler handler	= c.getTransferHandler();

				handler.exportAsDrag( c , firstMouseEvent , action );

								//	Reset the original mouse pressed event
								//	to null.

				firstMouseEvent			= null;
			}
		}
	}

	public void mouseMoved( MouseEvent e )
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

