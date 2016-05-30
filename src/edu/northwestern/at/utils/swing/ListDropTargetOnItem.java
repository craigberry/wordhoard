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

/**	An abstract base class for JList drop targets where the targets
 *	are the list elements.
 *
 *	<p>This base class is appropriate for use in situations where drops
 *	are <b>on</b> elements rather than <b>between</b> them.
 *
 *	<p>As the user moves the mouse over the list, the cell under the
 *	mouse is hilighted by outlining it and painting over it with translucent
 *	green. Only "valid" target cells are hilighted. The concrete subclass
 *	must implement the targetIsValid method to make the decision about which
 *	cells are valid targets.
 *
 *	<p>Thanks to the superclass {@link XDropTarget} lists autoscroll during
 *	drags.
 */

abstract public class ListDropTargetOnItem extends XDropTarget {

	/**	The list. */

	private JList list;

	/**	The scroll pane if the list is scrollable, else null. */

	private JScrollPane scrollPane;

	/**	The current target list index, or < 0 if none. */

	private int targetIndex = -1;

	/**	True if the current target is valid and should be hilighted. */

	private boolean targetIsValid;

	/**	The current hilighted list index, or < 0 if none. */

	private int hilightIndex = -1;

	/**	Constructs a new list drop target.
	 *
	 *	@param	list		The list.
	 *
	 *	@param	scrollPane	The scroll pane if the list is scrollable,
	 *						else null.
	 */

	public ListDropTargetOnItem (JList list, JScrollPane scrollPane) {
		super(list, scrollPane);
		this.list = list;
		this.scrollPane = scrollPane;
		ListCellRenderer hisRenderer = list.getCellRenderer();
		list.setCellRenderer(new MyRenderer(hisRenderer));
	}

	/**	The cell component interceptor.
	 *
	 *	<p>We intercept all cell components. When a cell is painted, if
	 *	it is the cell for the current hilight index, we hilight it by
	 *	outlining the cell and painting over it with translucent green.
	 */

	private class MyCellComponent extends Component {
		private Component hisComponent;
		private boolean hilight;
		private MyCellComponent (Component hisComponent, boolean hilight) {
			this.hisComponent = hisComponent;
			this.hilight = hilight;
		}
		public void paint (Graphics g) {
			Dimension size = getSize();
			hisComponent.setSize(size);
			hisComponent.paint(g);
			if (!hilight) return;
			g.drawRect(0, 0, size.width-1, size.height-1);
			if (g instanceof Graphics2D)
				((Graphics2D)g).setComposite(
					AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
			g.setColor(Color.green);
			g.fillRect(0, 0, size.width, size.height);
		}
		public Dimension getPreferredSize () {
			return hisComponent.getPreferredSize();
		}
	}

	/**	The renderer interceptor.
	 *
	 *	<p>We intercept the lists's cell renderer. All components returned
	 *	by the renderer are wrapped in our cell component iterceptor.
	 *
	 *	<p>Yes, it's a hack.
	 */

	private class MyRenderer implements ListCellRenderer {
		private ListCellRenderer hisRenderer;
		private MyRenderer (ListCellRenderer hisRenderer) {
			this.hisRenderer = hisRenderer;
		}
		public Component getListCellRendererComponent (JList list,
			Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			Component hisComponent =
				hisRenderer.getListCellRendererComponent(
					list, value, index, isSelected, cellHasFocus);
			boolean hilight = index == hilightIndex && targetIsValid;
			return new MyCellComponent(hisComponent, hilight);
		}
	}

	/**	Repaints the target cell. */

	private void repaintTarget () {
		if (targetIndex < 0) return;
		Rectangle targetBounds = list.getCellBounds(targetIndex, targetIndex);
		if (targetBounds == null) return;
		list.repaint(targetBounds);
	}

	/**	Gets the scroll increment for autoscrolling.
	 *
	 *	@param	up		True if scrolling up, false if scrolling down.
	 */

	public int getScrollIncrement (boolean up) {
		return list.getScrollableUnitIncrement(
			scrollPane.getViewport().getBounds(),
			SwingConstants.VERTICAL,
			up ? -1 : +1);
	}

	/**	Stops a drag. */

	public void stopDrag () {
		super.stopDrag();
		hilightIndex = -1;
		repaintTarget();
	}

	/**	Handles drag exit events.
	 *
	 *	@param	dte		The event.
	 */

	public void dragExit (DropTargetEvent dte) {
		super.dragExit(dte);
		hilightIndex = -1;
		repaintTarget();
		targetIndex = -1;
	}

	/**	Handles drag over events.
	 *
	 *	@param	dtde	The event.
	 */

	public void dragOver (DropTargetDragEvent dtde) {
		super.dragOver(dtde);
		Point mouseLocation = dtde.getLocation();
		int newTargetIndex = list.locationToIndex(mouseLocation);
		boolean newTargetIsValid;
		if (newTargetIndex < 0) {
			if (targetIndex < 0) return;
			newTargetIsValid = false;
		} else {
			if (newTargetIndex == targetIndex) return;
			newTargetIsValid = targetIsValid(newTargetIndex,
				dtde.getCurrentDataFlavorsAsList());
		}
		hilightIndex = -1;
		repaintTarget();
		targetIndex = newTargetIndex;
		targetIsValid = newTargetIsValid;
		if (targetIsValid) {
			hilightIndex = targetIndex;
			repaintTarget();
		}
	}

	/**	Handles drop events.
	 *
	 *	@param	dtde	The event.
	 */

	public void drop (DropTargetDropEvent dtde) {
		hilightIndex = -1;
		repaintTarget();
		if (!targetIsValid || targetIndex < 0) {
			dtde.rejectDrop();
			return;
		}
		dtde.acceptDrop(dtde.getDropAction());
		acceptDrop(targetIndex, dtde);
	}

	/**	Returns true if a target cell is valid.
	 *
	 *	@param	targetIndex		The target cell index.
	 *
	 *	@param	flavors			A list of the data flavors in the drag source.
	 *
	 *	@return					True if target cell is valid, false otherwise.
	 */

	abstract public boolean targetIsValid (int targetIndex,
		java.util.List flavors);

	/**	Accepts a drop on a target.
	 *
	 *	@param	targetIndex		The target cell index on which the drop occurred.
	 *
	 *	@param	dtde			The drop target drop event.
	 */

	abstract public void acceptDrop (int targetIndex,
		DropTargetDropEvent dtde);

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

