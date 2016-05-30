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

/**	An abstract base class for JTree drag source components.
 *
 *	<p>Dragging multiple node selections is supported. This class takes
 *	care of building the drag image. The concrete subclass takes care
 *	of building the drag data via the getTransferable method which it
 *	must implement.
 */

abstract public class TreeDragSource extends XDragSource {

	/**	The tree. */

	private JTree tree;

	/**	Constructs a new tree drag source.
	 *
	 *	@param	tree		The tree.
	 */

	public TreeDragSource (JTree tree) {
		super(tree);
		this.tree = tree;
	}

	/**	Handles drag gesture recognized events.
	 *
	 *	<p>If the drag originated in a non-selected node, that node
	 *	is selected. Otherwise the selection is unchanged.
	 *
	 *	<p>Builds a drag image for all the selected tree nodes,
	 *	asks the subclass to build the drag data, then invokes
	 *	{@link XDragSource#startDrag XDragSource.startDrag} to
	 *	start the drag.
	 *
	 *	@param	dge		The event.
	 */

	public void dragGestureRecognized (DragGestureEvent dge) {
		Point origin = dge.getDragOrigin();
		TreePath originPath = tree.getPathForLocation(origin.x, origin.y);
		if (originPath == null) return;
		if (!tree.isPathSelected(originPath))
			tree.setSelectionPath(originPath);
		TreePath[] selected = tree.getSelectionPaths();
		if (selected == null || selected.length == 0) return;
		Rectangle bounds = null;
		for (int i = 0; i < selected.length; i++) {
			TreePath path = selected[i];
			Rectangle pathBounds = tree.getPathBounds(path);
			if (bounds == null) {
				bounds = pathBounds;
			} else {
				bounds = bounds.union(pathBounds);
			}
		}
		BufferedImage image = new BufferedImage(
			bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D graphics = image.createGraphics();
		graphics.clearRect(0, 0, bounds.width, bounds.height);
		for (int i = 0; i < selected.length; i++) {
			TreePath path = selected[i];
			Rectangle pathBounds = tree.getPathBounds(path);
			Object node = path.getLastPathComponent();
			Component component =
				tree.getCellRenderer().getTreeCellRendererComponent(
					tree, node, false, tree.isExpanded(path),
					tree.getModel().isLeaf(node), 0, false);
			component.setSize(pathBounds.width, pathBounds.height);
			graphics.setTransform(AffineTransform.getTranslateInstance(
				pathBounds.x - bounds.x, pathBounds.y - bounds.y));
			component.paint(graphics);
		}
		graphics.dispose();
		Transferable transferable = getTransferable();
		startDrag(dge, bounds, image, transferable);
	}

	/**	Gets the drag data.
	 *
	 *	@return		The drag data.
	 */

	abstract public Transferable getTransferable();

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

