package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

/**	An abstract base class for JTree drop targets where the targets
 *	are the nodes.
 *
 *	<p>This base class is appropriate for use in situations where
 *	drops are <b>on</b> nodes rather than <b>between</b> them.
 *
 *	<p>As the user moves the mouse over the tree, the node under the
 *	mouse is hilighted by outlining it and painting over it with translucent
 *	green. Only "valid" target nodes are hilighted. The concrete subclass
 *	must implement the targetIsValid method to make the decision about which
 *	nodes are valid targets.
 *
 *	<p>The concrete subclass must also implement the acceptDrop method to
 *	accept or reject a drop on a node.
 *
 *	<p>This class implements "hovering". If the user pauses over a node
 *	for two seconds, the node is toggled - it is expanded if it was
 *	collapsed, and it is collapsed if it was expanded.
 *
 *	<p>Thanks to the superclass {@link XDropTarget} trees also autoscroll
 *	during drags.
 */

abstract public class TreeDropTargetOnItem extends XDropTarget {

	/**	The tree. */

	protected JTree tree;

	/**	The scroll pane if the tree is scrollable, else null. */

	private JScrollPane scrollPane;

	/**	The current target path, or null if none. */

	protected TreePath targetPath;

	/**	True if the current target is valid and should be hilighted. */

	protected boolean targetIsValid;

	/**	The current hilighted row, or -1 if none. */

	protected int hilightRow = -1;

	/**	Constructs a new tree drop target.
	 *
	 *	@param	tree		The tree.
	 *
	 *	@param	scrollPane	The scroll pane if the tree is scrollable,
	 *						else null.
	 */

	public TreeDropTargetOnItem (JTree tree, JScrollPane scrollPane) {
		super(tree, scrollPane);
		this.tree = tree;
		this.scrollPane = scrollPane;
		TreeCellRenderer hisRenderer = tree.getCellRenderer();
		tree.setCellRenderer(new MyRenderer(hisRenderer));
	}

	/**	The node component interceptor.
	 *
	 *	<p>We intercept all node components. When a node is painted, if
	 *	it is the node for the current hilight row, we hilight it by
	 *	outlining the row and painting over it with translucent green.
	 */

	private class MyNodeComponent extends Component {
		private Component hisComponent;
		private boolean hilight;
		private MyNodeComponent (Component hisComponent, boolean hilight) {
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
	 *	<p>We intercept the tree's cell renderer. All components returned
	 *	by the renderer are wrapped in our node component iterceptor.
	 *
	 *	<p>Yes, it's a hack.
	 */

	public class MyRenderer implements TreeCellRenderer {
		private TreeCellRenderer hisRenderer;
		private MyRenderer (TreeCellRenderer hisRenderer) {
			this.hisRenderer = hisRenderer;
		}

		public Component getTreeCellRendererComponent (JTree tree,
			Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus)
		{
			Component hisComponent =
				hisRenderer.getTreeCellRendererComponent(
					tree, value, sel, expanded, leaf, row, hasFocus);
			boolean hilight = row == hilightRow && targetIsValid;
			return new MyNodeComponent(hisComponent, hilight);
		}
	}

	/**	Repaints the target node. */

	protected void repaintTarget () {
		if (targetPath == null) return;
		Rectangle targetBounds = tree.getPathBounds(targetPath);
		if (targetBounds == null) return;
		tree.repaint(targetBounds);
	}

	/**	The hover timer.
	 *
	 *	<p>This timer fires if the user hovers over a node for two
	 *	seconds. It toggles the node, expanding it if it was collapsed,
	 *	and collapsing it if it was expanded.
	 */

	protected javax.swing.Timer hoverTimer = new javax.swing.Timer(2000,
		new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				if (targetPath == null) return;
				if (tree.isExpanded(targetPath)) {
					tree.collapsePath(targetPath);
				} else {
					tree.expandPath(targetPath);
				}
			}
		}
	);

	/**	Gets the scroll increment for autoscrolling.
	 *
	 *	@param	up		True if scrolling up, false if scrolling down.
	 */

	public int getScrollIncrement (boolean up) {
		return tree.getScrollableUnitIncrement(
			scrollPane.getViewport().getBounds(),
			SwingConstants.VERTICAL,
			up ? -1 : +1);
	}

	/**	Stops a drag. */

	public void stopDrag () {
		super.stopDrag();
		hoverTimer.stop();
		hilightRow = -1;
		repaintTarget();
	}

	/**	Handles drag exit events.
	 *
	 *	@param	dte		The event.
	 */

	public void dragExit (DropTargetEvent dte) {
		super.dragExit(dte);
		hilightRow = -1;
		repaintTarget();
		targetPath = null;
		hoverTimer.stop();
	}

	/**	Handles drag over events.
	 *
	 *	@param	dtde	The event.
	 */

	public void dragOver (DropTargetDragEvent dtde) {
		super.dragOver(dtde);
		Point mouseLocation = dtde.getLocation();
		TreePath newTargetPath =
			tree.getPathForLocation(mouseLocation.x, mouseLocation.y);
		boolean newTargetIsValid;
		if (newTargetPath == null) {
			if (targetPath == null) return;
			newTargetIsValid = false;
		} else {
			if (newTargetPath.equals(targetPath)) return;
			newTargetIsValid = targetIsValid(newTargetPath,
				dtde.getCurrentDataFlavorsAsList());
		}
		hilightRow = -1;
		repaintTarget();
		targetPath = newTargetPath;
		targetIsValid = newTargetIsValid;
		if (targetIsValid) {
			hilightRow = tree.getRowForPath(targetPath);
			repaintTarget();
		}
		hoverTimer.restart();
	}

	/**	Handles drop events.
	 *
	 *	@param	dtde	The event.
	 */

	public void drop (DropTargetDropEvent dtde) {
		hoverTimer.stop();
		hilightRow = -1;
		repaintTarget();
		if (!targetIsValid || targetPath == null) {
			dtde.rejectDrop();
			return;
		}
		dtde.acceptDrop(dtde.getDropAction());
		acceptDrop(targetPath, dtde);
	}

	/**	Returns true if a target node is valid.
	 *
	 *	@param	path		The target path.
	 *
	 *	@param	flavors		A list of the data flavors in the drag source.
	 *
	 *	@return				True if target node is valid, false otherwise.
	 */

	abstract public boolean targetIsValid (TreePath path,
		java.util.List flavors);

	/**	Accepts a drop on a target.
	 *
	 *	@param	path			The target path on which the drop occurred.
	 *
	 *	@param	dtde			The drop target drop event.
	 */

	abstract public void acceptDrop (TreePath path,
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

