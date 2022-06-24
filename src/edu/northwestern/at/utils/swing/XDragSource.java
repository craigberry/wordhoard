package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.*;

import edu.northwestern.at.utils.*;

/**	An abstract base class for drag sources.
 *
 *	<p>This class implements both drag gesture and drag source listeners for
 *	any AWT component and handles the details of the source side of
 *	drag and drop operations.
 *
 *	<p>Concrete subclasses must implement the dragGestureRecognized method.
 *	This method should compute both the drag image and the drag data, then
 *	call the startDrag method in this class to start the drag. The startDrag
 *	method computes the proper offset for the drag image and tells the drag
 *	gesture event to start the drag using the image and data supplied by the
 *	subclass.
 *
 *	<p>Empty implementations are provided for the required
 *	drag enter, drag over, drag exit, and drop action changed event handlers.
 *	Subclasses may override these handlers if they wish.
 *
 *	<p>This class has an incestuous relationship with its partner {@link
 *	XDropTarget} class and its subclasses:
 *
 *	<ul>
 *
 *	<li>This class keeps track of the current unique drag in progress that
 *	it originated, if any, and makes both the current drag source component
 *	and the current drag data available to other classes. In particular,
 *	drop targets in the same JVM can use this information during the
 *	drag to determine if individual target items in a drop target component
 *	are valid.
 *
 *	<li>If the underlying OS does not support image dragging, the two classes
 *	cooperate to do it manually. In this case this class turns the image into
 *	a {@link ImageUtils#makeGhost ghost} image and notifies {@link XDropTarget}
 *	that all drop targets must drag the ghost image around as the mouse moves
 *	into and over each target.
 *
 *	<li>This class handles all drag drop end events and notifes {@link
 *	XDropTarget} when each drag and drop operation ends by calling
 *	{@link XDropTarget#stopCurrentTarget XDropTarget.stopCurrentTarget}.
 *
 *	</ul>
 */

abstract public class XDragSource
	implements DragGestureListener, DragSourceListener
{
	/**	True if the underlying system supports image draging. */

	private static boolean dragImageSupported =
		DragSource.isDragImageSupported();

	/**	The current drag source component if a drag is in progress,
	 *	else null.
	 */

	private static Component currentDragSource;

	/**	The current drag data if a drag is in progress, else null. */

	private static Transferable currentDragData;

	/**	The trigger event for the most recent drag, if any. */

	private static InputEvent triggerEvent;

	/**	Gets the current drag source component.
	 *
	 *	@return		The current drag source component if a drag is in
	 *				progress, else null.
	 */

	public static Component getCurrentDragSource () {
		return currentDragSource;
	}

	/**	Gets the current drag data.
	 *
	 *	@return		The current drag data if a drag is in progress,
	 *				else null.
	 */

	public static Transferable getCurrentDragData () {
		return currentDragData;
	}

	/**	Gets the trigger event for the most recent drag.
	 *
	 *	@return		The trigger event.
	 */

	public static InputEvent getTriggerEvent () {
		return triggerEvent;
	}

	/**	The drag source component. */

	private Component component;

	/**	Constructs a new drag source object.
	 *
	 *	<p>A new default drag gesture recognizer is created for the
	 *	component.
	 *
	 *	@param	component		The drag source component.
	 */

	public XDragSource (Component component) {
		this.component = component;
		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(
			component, -1, this);
	}

	/**	Starts a drag.
	 *
	 *	@param	dge				The drag gesture event.
	 *
	 *	@param	bounds			The bounds of the drag image.
	 *
	 *	@param	image			The drag image.
	 *
	 *	@param	transferable	The drag data.
	 *
	 *	@param	cursor			The drag cursor.
	 */

	public void startDrag (DragGestureEvent dge, Rectangle bounds,
		BufferedImage image, Transferable transferable, Cursor cursor )
	{
		triggerEvent = dge.getTriggerEvent();
		Point mouseLocation = dge.getDragOrigin();
		Point offset =
			new Point(bounds.x-mouseLocation.x, bounds.y-mouseLocation.y);
		if (!dragImageSupported)
		{
			if (Env.IS_JAVA_142_OR_LATER)
			{
				ImageUtils.makeGhost(image);
				XDropTarget.setGhost(image, offset);
			}
		} else
		if (Env.MACOSX && Env.IS_JAVA_14_OR_LATER)
			ImageUtils.makeGhost(image);
		currentDragSource = component;
		currentDragData = transferable;
		dge.startDrag(cursor, image, offset,
			transferable, this);
	}

	/**	Starts a drag.
	 *
	 *	@param	dge				The drag gesture event.
	 *
	 *	@param	bounds			The bounds of the drag image.
	 *
	 *	@param	image			The drag image.
	 *
	 *	@param	transferable	The drag data.
	 */

	public void startDrag (DragGestureEvent dge, Rectangle bounds,
		BufferedImage image, Transferable transferable)
	{
		startDrag(dge, bounds, image, transferable, DragSource.DefaultMoveDrop);
	}

	/**	Handles a drag drop end event.
	 *
	 *	<p>Subclasses may override this method, but must remember to
	 *	invoke super.dragDropEnd.
	 *
	 *	@param	dsde		The event.
	 */

	public void dragDropEnd (DragSourceDropEvent dsde) {
		XDropTarget.stopCurrentTarget();
		currentDragSource = null;
		currentDragData = null;
	}

	/**	Handles a drag enter event.
	 *
	 *	@param	dsde		The event.
	 */

	public void dragEnter (DragSourceDragEvent dsde) {
	}

	/**	Handles a drag exit event.
	 *
	 *	@param	dse			The event.
	 */

	public void dragExit (DragSourceEvent dse) {
	}

	/**	Handles a drag over event.
	 *
	 *	@param	dsde		The event.
	 */

	public void dragOver (DragSourceDragEvent dsde) {
	}

	/**	Handles a drop action changed event.
	 *
	 *	@param	dsde		The event.
	 */

	public void dropActionChanged (DragSourceDragEvent dsde) {
	}

	/**	Handles a drag gesture recognized event.
	 *
	 *	@param	dge			The event.
	 */

	abstract public void dragGestureRecognized (DragGestureEvent dge);

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

