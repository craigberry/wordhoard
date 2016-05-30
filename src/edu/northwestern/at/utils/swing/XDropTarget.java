package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.tree.*;

/**	An abstract base class for drop targets.
 *
 *	<p>This class implements a drop target listener for any AWT component.
 *	It handles the details of image dragging if the underlying OS does not
 *	support it and the details of autoscrolling scrollable targets.
 *
 *	<p>Concrete subclasses must implement the getScrollIncrement and drop
 *	methods.
 *
 *	<p>Autoscrolling is enabled only if the component is scrollable, and
 *	only if the drag and drop operation was started by the partner
 *	{@link XDragSource} class within the same JVM. We have implemented our
 *	own autoscrolling and do not use Swing's built-in facilities. Our
 *	autoscrolling starts when the user drags the mouse above or below the
 *	scrolling component, not when he moves the mouse near the top or bottom
 *	and pauses.
 *
 *	<p>This class has an incestuous relationship with its partner
 *	{@link XDragSource} class. See that class overview for more details.
 */

abstract public class XDropTarget implements DropTargetListener {

	/**	The current active drop target, or null if none. */

	static private XDropTarget currentTarget;

	/**	The current ghost image for image dragging if the underlying OS
	 *	does not support image dragging itself. Null if the underlying OS
	 *	is doing the image dragging or if there is no currently active
	 *	drop target.
	 */

	static private BufferedImage ghostImage;

	/**	The offset for ghost image dragging. */

	static private Point ghostOffset;

	/**	The bounds rectangle in which the last ghost image was drawn. */

	static private Rectangle ghostRect = new Rectangle();

	/**	Stops the current target at the end of a drag and drop operation.
	 *
	 *	<p>This method is invoked by {@link XDragSource#dragDropEnd
	 *	XDragSouce.dragDropEnd} when a drag and drop operation ends
	 *	(when the user releases the mouse button).
	 *
	 *	<p>Autoscrolling and ghost image dragging are both terminated
	 *	for the currently active drop target, if any.
	 */

	static public void stopCurrentTarget () {
		if (currentTarget != null) currentTarget.stopDrag();
		ghostImage = null;
	}

	/**	Sets the ghost image for manual image dragging.
	 *
	 *	<p>This method is invoked by {@link XDragSource#startDrag
	 *	XDragSource.startDrag} if the underlying OS does not support
	 *	image dragging. In this case this class takes care of manually
	 *	dragging the ghost image as the mouse enters and moves over
	 *	drop targets.
	 *
	 *	@param	ghostImage		The ghost image.
	 *
	 *	@param	ghostOffset		The image offset.
	 */

	static public void setGhost (BufferedImage ghostImage,
		Point ghostOffset)
	{
		XDropTarget.ghostImage = ghostImage;
		XDropTarget.ghostOffset = ghostOffset;
	}

	/**	The drop target component. */

	private JComponent component;

	/**	The scroll pane if the target component is scrollable, or null
	 *	if it is not scrollable.
	 */

	private JScrollPane scrollPane;

	/**	True when auto-scrolling in the up direction, false when
	 *	auto-scrolling in the down direction.
	 */

	private boolean scrollUp;

	/**	The last y coordinate of the mouse withing the drop target
	 *	component.
	 */

	private int lastY;

	/**	Constructs a new drop target.
	 *
	 *	@param	component		The component.
	 *
	 *	@param	scrollPane		The scroll pane if the component is
	 *							scrollable, else null.
	 */

	public XDropTarget (JComponent component, JScrollPane scrollPane) {
		this.component = component;
		this.scrollPane = scrollPane;
		new DropTarget(component, -1, this);
	}

	/**	Draws the ghost image.
	 *
	 *	@param	mouseLocation	The current mouse location.
	 */

	private void drawGhostImage (Point mouseLocation) {
		if (ghostImage == null) return;
		if (ghostRect == null ) return;
		Graphics2D graphics = (Graphics2D)component.getGraphics();
		ghostRect.setRect(mouseLocation.x + ghostOffset.x,
			mouseLocation.y + ghostOffset.y,
			ghostImage.getWidth(),
			ghostImage.getHeight());
		graphics.drawImage(ghostImage,
			AffineTransform.getTranslateInstance(ghostRect.x, ghostRect.y),
				null);
	}

	/**	Clears the ghost image. */

	private void clearGhostImage () {
		if (ghostImage == null) return;
		if (ghostRect == null ) return;
		component.paintImmediately(ghostRect);
	}

	/**	Stops the current drag and drop operation. Autoscrolling is
	 *	stopped and the ghost image is cleared.
	 *
	 *	<p>Subclasses may override this method, but they must remember
	 *	to invoke super.stopDrag.
	 */

	public void stopDrag () {
		autoScrollTimer.stop();
		clearGhostImage();
	}

	/**	The autoscroll timer task.
	 *
	 *	<p>Wakes up every tenth of a second and scrolls the target
	 *	component up or down one increment.
	 */

	private javax.swing.Timer autoScrollTimer = new javax.swing.Timer(100,
		new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				if (scrollPane == null ||
					XDragSource.getCurrentDragSource() == null) return;
				JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
				int min = scrollBar.getMinimum();
				int max = scrollBar.getMaximum();
				int value = scrollBar.getValue();
				int scrollIncrement = getScrollIncrement(scrollUp);
				if (scrollUp) scrollIncrement = -scrollIncrement;
				value += scrollIncrement;
				if (value < min) {
					value = min;
				} else if (value > max) {
					value = max;
				}
				scrollBar.setValue(value);
			}
		}
	);

	/**	Handles drag enter events.
	 *
	 *	<p>Any previously active drop target is stopped, the autoscroll
	 *	timer is stopped, and the y coordinate of the mouse location
	 *	is recorded.
	 *
	 *	<p>Subclasses often override this method, but must remember to
	 *	invoke super.dragEnter.
	 *
	 *	@param	dtde		The event.
	 */

	public void dragEnter (DropTargetDragEvent dtde) {
		if (currentTarget != this) {
			if (currentTarget != null) currentTarget.stopDrag();
			currentTarget = this;
		}
		autoScrollTimer.stop();
		Point mouseLocation = dtde.getLocation();
		lastY = mouseLocation.y;
	}

	/**	Handles drag exit events.
	 *
	 *	<p>The ghost image is cleared. If the last known y coordinate
	 *	of the mouse is within 20 pixels of the top or bottom of the
	 *	component's viewport the autoscrolling timer is started.
	 *
	 *	<p>Subclasses may override this method, but must remember to
	 *	invoke super.dragExit.
	 *
	 *	@param	dte			The event.
	 */

	public void dragExit (DropTargetEvent dte) {
		clearGhostImage();
		if (scrollPane == null ||
			XDragSource.getCurrentDragSource() == null) return;
		int value = scrollPane.getVerticalScrollBar().getValue();
		int height = scrollPane.getViewport().getHeight();
		int distanceFromTop = lastY - value;
		int distanceFromBottom = value + height - lastY;
		int threshold = Math.min(20, height/2);
		Rectangle visibleRect = scrollPane.getViewport().getBounds();
		if (distanceFromTop < threshold) {
			scrollUp = true;
			autoScrollTimer.restart();
		} else if (distanceFromBottom < threshold) {
			scrollUp = false;
			autoScrollTimer.restart();
		}
	}

	/**	Handles drag over events.
	 *
	 *	<p>Any previously active different drop target is stopped,
	 *	the autoscroll timer is stopped, the y coordinate of the
	 *	mouse location is recorded, and the ghost image is cleared and
	 *	redrawn.
	 *
	 *	<p>Subclasses may override this method, but must remember to
	 *	invoke super.dragOver.
	 *
	 *	@param	dtde		The event.
	 */

	public void dragOver (DropTargetDragEvent dtde) {
		dragEnter(dtde);
		clearGhostImage();
		drawGhostImage(dtde.getLocation());
	}

	/**	Handles drop action changed events.
	 *
	 *	<p>The implementation does nothing. Subclasses may override this
	 *	method if they wish.
	 *
	 *	@param	dtde		The event.
	 */

	public void dropActionChanged (DropTargetDragEvent dtde) {
	}

	/**	Gets the scroll increment.
	 *
	 *	@param	up			True if scrolling up, false if down.
	 */

	abstract public int getScrollIncrement (boolean up);

	/**	Handles drop events.
	 *
	 *	@param	dtde		The event.
	 */

	abstract public void drop (DropTargetDropEvent dtde);

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

