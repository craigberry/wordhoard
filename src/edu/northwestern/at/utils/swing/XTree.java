package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import edu.northwestern.at.utils.swing.icons.*;
import edu.northwestern.at.utils.swing.printing.*;
import edu.northwestern.at.utils.*;

/**	A JTree with different defaults and behavior.
 *
 *	<ul>
 *	<li>The font is set to XParameters.treeFont if not null.
 *	<li>The row height is set to XParameters.treeRowHeight if not 0.
 *	<li>The root is not visible.
 *	<li>Root nodes have handles.
 *	<li>The renderer displays standard prefix JTree open folder, closed
 *		folder, and leaf icons by default, but they may be turned off and
 *		instead optional postfix icons may be supplied by the tree elements,
 *		which must implement the {@link AddIcon} interface.
 *	<li>The renderer never draws focus indicators.
 *	<li>The return key input system action, if any, is disabled. (In the
 *		Mac OS X Aqua LAF pressing return normally toggles a node's
 *		expanded/collapsed state.)
 *	<li>The toggle click count is set to 1000, disabling toggling node
 *		expanded/collapsed state on double-clicks.
 *	<li>The node expanded/collapsed states can be saved and restored,
 *		provided the tree model is DefaultTreeModel or a subclass.
 *	<li>Node selection changes are triggered on mouse clicked events rather
 *		that on mouse pressed events, to avoid interference with drag
 *		gesture recognizers.
 *	<li>A title may be associated with the table for printing and
 *		file saving purposes.</li>
 *	<li>The tree contents can be printed.</li>
 *	<li>The tree contents can be saved to a file.</li>
 *	</ul>
 *
 *	<p>The constructors are the same as in JTree. We did not bother
 *	giving them their own javadoc.
 */

public class XTree extends JTree
	implements PrintableContents, SaveToFile, TreeIconSetter {

	/**	Node expanded/collapsed state = set of all expanded nodes. */

	private Collection expansionState = null;

	/**	Default tree model listeners. */

	private TreeModelListener[] defaultTreeModelListeners;

	/**	Default mouse listeners. */

	private MouseListener[] defaultMouseListeners;

	/**	True if mouse pressed event deferred until mouse click time. */

	private boolean pressedEventDeferred;

	/**	True if standard prefix JTree open folder, closed folder, and
	 *	leaf icons are displayed.
	 */

	private boolean standardIcons = true;

	/** Tree cell renderer. */

	private TreeCellRenderer renderer;

	/**	Tree title. */

	protected String title	= null;

	/**	Custom tree cell renderer class. Never draws focus indicators. */

	protected class Renderer
		implements TreeCellRenderer, TreeIconSetter {

		private DefaultTreeCellRenderer rend = new DefaultTreeCellRenderer();

		/** Get the background selection color for the renderer.
		 *
		 *	@return		The background selection color.
		 */

		private Color backgroundSelectionColor =
			rend.getBackgroundSelectionColor();

		/** Get the text selection color for the renderer.
		 *
		 *	@return		The text selection color.
		 */

		private Color textSelectionColor =
			rend.getTextSelectionColor();


		/** Set the leaf icon for the tree display.
		 *
		 *	@param	newIcon		The new leaf icon.
		 */

		public void setLeafIcon( ImageIcon newIcon )
		{
			rend.setLeafIcon( newIcon );
		}

		/** Set the open folder icon for the tree display.
	 	 *
	 	 * @param	newIcon		The new open folder icon.
	 	 */

		public void setOpenIcon( ImageIcon newIcon )
		{
			rend.setOpenIcon( newIcon );
		}

		/** Set the closed folder icon for the tree display.
		 *
	 	 *	@param	newIcon		The new closed folder icon.
	 	 */

		public void setClosedIcon( ImageIcon newIcon )
		{
			rend.setClosedIcon( newIcon );
		}

		/** Get the tree cell renderer component. */

		public Component getTreeCellRendererComponent (JTree tree,
			Object value, final boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus)
		{
			JLabel label;
			if (standardIcons) {
				label = (JLabel)rend.getTreeCellRendererComponent(tree,
					value, sel, expanded, leaf, row, false);
			} else {
				label = new JLabel(value.toString()) {
					public void paint (Graphics g) {
						if (sel) {
							g.setColor(backgroundSelectionColor);
							g.fillRect(0, 0, getWidth(), getHeight());
							this.setForeground(textSelectionColor);
						}
						super.paint(g);
					}
				};
				if (value instanceof AddIcon) ((AddIcon)value).addIcon(label);
			}

			label.setFont(XTree.this.getFont());

			if ( label instanceof DefaultTreeCellRenderer )
			{
				((DefaultTreeCellRenderer)label).setBackgroundNonSelectionColor(
					getBackground() );
			}

			return label;
		}
	}

	public XTree () {
		super();
		common();
	}

	public XTree (Hashtable value) {
		super(value);
		common();
	}

	public XTree (Object[] value) {
		super(value);
		common();
	}

	public XTree (TreeModel newModel) {
		super(newModel);
		common();
	}

	public XTree (TreeNode root) {
		super(root);
		common();
	}

	public XTree (TreeNode root, boolean asksAllowsChildren) {
		super(root, asksAllowsChildren);
		common();
	}

	public XTree (Vector value) {
		super(value);
		common();
	}

	/**	Sets the default attributes. */

	private void common () {
		if (XParameters.treeFont != null) setFont(XParameters.treeFont);
		if (XParameters.treeRowHeight != 0)
			setRowHeight(XParameters.treeRowHeight);
		setRootVisible(false);
		setShowsRootHandles(true);
		renderer = new Renderer();
		setCellRenderer(renderer);
		getInputMap().put(KeyStroke.getKeyStroke(10, 0), "none");
		getInputMap().put(KeyStroke.getKeyStroke('\n'), "none");
		setToggleClickCount(1000);
		TreeModel model = getModel();
		if (model != null && model instanceof DefaultTreeModel) {
			interceptModelEvents((DefaultTreeModel)model);
			addTreeExpansionListener(treeExpansionListener);
		}
		defaultMouseListeners =
			(MouseListener[])getListeners(MouseListener.class);
		for (int i = 0; i < defaultMouseListeners.length; i++)
			removeMouseListener(defaultMouseListeners[i]);
		addMouseListener(mouseListener);
	}

	/**	Sets the standard icons option.
	 *
	 *	@param	standardIcons	True if the tree uses standard prefix JTree
	 *							open folder, closed folder, and leaf icons
	 *							(the default).  To use non-standard
	 *							icons, see the setLeafIcon, setOpenIcon,
	 *							and setClosedIcon methods of XTree.
	 *
	 *							False to use no leading icons and optional
	 *							trailing icons.
	 */

	public void setStandardIcons (boolean standardIcons) {
		this.standardIcons = standardIcons;
	}

	/**	Adds a tree model listener to intercept model events.
	 *
	 *	@param	model		The default tree model to be intercepted.
	 */

	private void interceptModelEvents (DefaultTreeModel model) {
		defaultTreeModelListeners =
			(TreeModelListener[])model.getListeners(TreeModelListener.class);
		for (int i = 0; i < defaultTreeModelListeners.length; i++)
			model.removeTreeModelListener(defaultTreeModelListeners[i]);
		model.addTreeModelListener(treeModelListener);
	}

	/**	Sets a new tree model.
	 *
	 *	@param	newModel	New default tree model.
	 */

	public void setModel (DefaultTreeModel newModel) {
		TreeModel oldModel = getModel();
		if (oldModel != null)
			oldModel.removeTreeModelListener(treeModelListener);
		super.setModel(newModel);
		interceptModelEvents(newModel);
	}

	/**	Sets (restores) the node expanded/collapsed state.
	 *
	 *	@param	expansionState		Set of all expanded nodes, or null
	 *								to disable saved/restored expansion
	 *								states.
	 */

	public void setExpansionState (Collection expansionState) {
		this.expansionState = expansionState;
	}

	/**	Gets (saves) the node expanded/collapsed state.
	 *
	 *	@return		Set of all expanded nodes, or null if saved/restored
	 *				expansion states are disabled.
	 */

	public Collection getExpansionState () {
		return expansionState;
	}

	/**	Expands all the nodes. */

	public void expandAll () {
		for (int i = 0; i < getRowCount(); i++) expandRow(i);
	}

	/**	Collapses all the nodes. */

	public void collapseAll () {
		for (int i = 0; i < getRowCount(); i++) collapseRow(i);
	}

	/**	Collapses all nodes except for the root node. */

	public void collapseAllButRoot () {
		for (int i = 1; i < getRowCount(); i++) collapseRow(i);
	}

	/**	Tree expansion listener.
	 *
	 *	<p>Keeps track of expanded/collapsed nodes and restores the
	 *	expansion state of subnodes when parent nodes are expanded.
	 */

	private TreeExpansionListener treeExpansionListener =

		new TreeExpansionListener() {

			public void treeCollapsed (TreeExpansionEvent event) {
				if (expansionState == null) return;
				TreePath path = event.getPath();
				Object node = path.getLastPathComponent();
				expansionState.remove(node);
			}

			public void treeExpanded (TreeExpansionEvent event) {
				if (expansionState == null) return;
				TreeModel model = getModel();
				if (model == null) return;
				TreePath path = event.getPath();
				Object node = path.getLastPathComponent();
				expansionState.add(node);
				int childCount = model.getChildCount(node);
				for (int i = 0; i < childCount; i++) {
					Object childNode = model.getChild(node, i);
					TreePath childPath = path.pathByAddingChild(childNode);
					boolean childExpanded = isExpanded(childPath);
					if (!childExpanded && expansionState.contains(childNode))
						expandPath(childPath);
				}
			}

		};

	/**	Tree model listener interceptor.
	 *
	 *	<p>This listener intercepts all tree model events. The saved
	 *	default listeners are first invoked to handle the events
	 *	normally. Then our intercept listener does post-processing
	 *	of the nodes inserted and structure changed events to make sure
	 *	that node expanded/collapsed states are restored properly.
	 */

	private TreeModelListener treeModelListener =

		new TreeModelListener() {

			public void treeNodesChanged (TreeModelEvent event) {
				for (int i = 0; i < defaultTreeModelListeners.length; i++)
					defaultTreeModelListeners[i].treeNodesChanged(event);
			}

			public void treeNodesInserted (TreeModelEvent event) {
				for (int i = 0; i < defaultTreeModelListeners.length; i++)
					defaultTreeModelListeners[i].treeNodesInserted(event);
				if (expansionState == null) return;
				TreeModel model = getModel();
				if (model == null) return;
				TreePath path = event.getTreePath();
				Object node = path.getLastPathComponent();
				boolean nodeExpanded = isExpanded(path);
				if (!nodeExpanded) {
					if (expansionState.contains(node)) {
						expandPath(path);
					} else {
						return;
					}
				}
				int childIndices[] = event.getChildIndices();
				for (int i = 0; i < childIndices.length; i++) {
					int childIndex = childIndices[i];
					Object childNode = model.getChild(node, childIndex);
					TreePath childPath = path.pathByAddingChild(childNode);
					boolean childExpanded = isExpanded(childPath);
					if (!childExpanded && expansionState.contains(childNode))
						expandPath(childPath);
				}
			}

			public void treeNodesRemoved (TreeModelEvent event) {
				for (int i = 0; i < defaultTreeModelListeners.length; i++)
					defaultTreeModelListeners[i].treeNodesRemoved(event);
			}

			public void treeStructureChanged (TreeModelEvent event) {
				for (int i = 0; i < defaultTreeModelListeners.length; i++)
					defaultTreeModelListeners[i].treeStructureChanged(event);
				if (expansionState == null) return;
				TreeModel model = getModel();
				if (model == null) return;
				TreePath path = event.getTreePath();
				Object node = path.getLastPathComponent();
				boolean nodeExpanded = isExpanded(path);
				if (!nodeExpanded) {
					if (expansionState.contains(node)) {
						expandPath(path);
					} else {
						return;
					}
				}
				int childCount = model.getChildCount(node);
				for (int i = 0; i < childCount; i++) {
					Object childNode = model.getChild(node, i);
					TreePath childPath = path.pathByAddingChild(childNode);
					boolean childExpanded = isExpanded(childPath);
					if (!childExpanded && expansionState.contains(childNode)) {
						expandPath(childPath);
					}
				}
			}

		};

	/**	Returns true if a mouse event occurred inside a path bounds
	 *	(as opposed to inside a collapse/expand control).
	 *
	 *	@param		The mouse event.
	 *
	 *	@return		True if inside a path bounds.
	 */

	private boolean eventIsInBounds (MouseEvent event) {
		Point point = event.getPoint();
		TreePath path = getPathForLocation(point.x, point.y);
		if (path == null) return false;
		Rectangle bounds = getPathBounds(path);
		if (bounds == null) return false;
		return bounds.contains(point);
	}

	/**	Mouse listener interceptor.
	 *
	 *	<p>Modifies the selection on clicked events rather than pressed events.
	 */

	private MouseListener mouseListener =
		new MouseListener() {
			public void mouseClicked (MouseEvent event) {
				if (pressedEventDeferred) {
					Component source = event.getComponent();
					long when = event.getWhen();
					int modifiers = event.getModifiersEx() |
						InputEvent.BUTTON1_DOWN_MASK;
					int x = event.getX();
					int y = event.getY();
					int clickCount = event.getClickCount();
					boolean popupTrigger = event.isPopupTrigger();
					event = new MouseEvent(source, MouseEvent.MOUSE_PRESSED,
						when, modifiers, x, y, clickCount, popupTrigger);
					for (int i = 0; i < defaultMouseListeners.length; i++)
						defaultMouseListeners[i].mousePressed(event);
				} else {
					for (int i = 0; i < defaultMouseListeners.length; i++)
						defaultMouseListeners[i].mouseClicked(event);
				}
			}
			public void mouseEntered (MouseEvent event) {
				for (int i = 0; i < defaultMouseListeners.length; i++)
					defaultMouseListeners[i].mouseEntered(event);
			}
			public void mouseExited (MouseEvent event) {
				for (int i = 0; i < defaultMouseListeners.length; i++)
					defaultMouseListeners[i].mouseExited(event);
			}
			public void mousePressed (MouseEvent event) {
				TreePath path = getClosestPathForLocation(event.getX(), event.getY());
				if (path != null) setSelectionPath(path);
				pressedEventDeferred = eventIsInBounds(event);
				if (pressedEventDeferred) return;
				for (int i = 0; i < defaultMouseListeners.length; i++)
					defaultMouseListeners[i].mousePressed(event);
			}
			public void mouseReleased (MouseEvent event) {
				if (pressedEventDeferred) return;
				for (int i = 0; i < defaultMouseListeners.length; i++)
					defaultMouseListeners[i].mouseReleased(event);
			}
		};

	/** Set the leaf icon for the tree display.
	 *
	 *	@param	newIcon		The new leaf icon.
	 */

	public void setLeafIcon( ImageIcon newIcon )
	{
		if ( renderer instanceof TreeIconSetter )
		{
			((TreeIconSetter)renderer).setLeafIcon( newIcon );
		}
	}

	/** Set the open folder icon for the tree display.
	 *
	 *	@param	newIcon		The new open folder icon.
	 */

	public void setOpenIcon( ImageIcon newIcon )
	{
		if ( renderer instanceof TreeIconSetter )
		{
			((TreeIconSetter)renderer).setOpenIcon( newIcon );
		}
	}

	/** Set the closed folder icon for the tree display.
	 *
	 *	@param	newIcon		The new closed folder icon.
	 */

	public void setClosedIcon( ImageIcon newIcon )
	{
		if ( renderer instanceof TreeIconSetter )
		{
			((TreeIconSetter)renderer).setClosedIcon( newIcon );
		}
	}

	/** Set the cell renderer.
	 *
	 *	@param	cellRenderer	Cell renderer for tree.
	 */

	public void setCellRenderer( TreeCellRenderer cellRenderer )
	{
		super.setCellRenderer( cellRenderer );

		renderer = cellRenderer;
	}

	/**	Set the tree title.
	 *
	 *	@param	title	The tree title.
	 *
	 *	<p>
	 *	The title is used for printing and file saving purposes.
	 *	</p>
	 */

	public void setTitle( String title )
	{
		this.title	= title;
	}

	/**	Get the tree title.
	 *
	 *	@return		The tree title.
	 */

	public String getTitle()
	{
		return title;
	}

	/** Prints the tree.
	 *
	 *	@param	title			Title for output.
	 *	@param	printerJob		The printer job.
	 *	@param	pageFormat		The printer page format.
	 */

	public void printContents
	(
		final String title,
		final PrinterJob printerJob,
		final PageFormat pageFormat
	)
	{
		Thread runner = new Thread( "Print tree" )
		{
			public void run()
			{
				PrintUtilities.printComponent(
					getPrintableComponent( title , pageFormat ),
					title,
					printerJob,
					pageFormat,
					true
				);
			}
		};

		runner.start();
	}

	/** Return printable component.
	 *
	 *	@param		title		Title for printing.
	 *
	 *	@param		pageFormat	Page format for printing.
	 *
	 *	@return					Printable component for XTree.
	 */

	public PrintableComponent getPrintableComponent
	(
		String title,
		PageFormat pageFormat
	)
	{
		return
			new PrintableComponent
			(
				this ,
				pageFormat,
				new PrintHeaderFooter(
					title,
					"Printed " +
						DateTimeUtils.formatDateOnAt( new Date() ),
					"Page " )
			);
	}

	/**	Save to a named file.
	 *
	 *	@param	fileName	Name of file to which to save results.
	 */

	public void saveToFile( String fileName )
	{
		SaveTreeData.saveTreeDataToFile( this , title , fileName );
	}

	/**	Save to a file using a file dialog.
	 *
	 *	@param	parentWindow	Parent window for file dialog.
	 *
	 *	<p>
	 *	Runs a file dialog to get the name of the file to which to
	 *	save results.
	 *	</p>
	 */

	public void saveToFile( Window parentWindow )
	{
		SaveTreeData.saveTreeDataToFile
		(
			parentWindow ,
			this ,
			title
		);
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

