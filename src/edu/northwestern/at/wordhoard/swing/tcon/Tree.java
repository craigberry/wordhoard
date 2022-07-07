package edu.northwestern.at.wordhoard.swing.tcon;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.tconview.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.work.*;
import edu.northwestern.at.wordhoard.swing.querytool.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.db.*;

/**	A table of contents tree.
 */

public class Tree extends XTree {

	/**	The corpus. */

	private Corpus corpus;

	/**	The parent window. */

	private AbstractWindow parentWindow;
	
	/**	The parent table of contents panel. */
	
	private TableOfContentsPanel parentPanel;

	/**	The root of the tree. */

	private TreeNode root;

	/**	The tree model. */

	private DefaultTreeModel model;
	
	/**	Creates a new table of contents tree.
	 *
	 *	@param	corpus			The corpus.
	 *
	 *	@param	view			Table of contents view.
	 *
	 *	@param	parentWindow	The parent window.
	 *
	 *	@param	parentPanel		The parent table of contents panel.
	 */
	 
	public Tree (Corpus corpus, TconView view, 
		AbstractWindow parentWindow, TableOfContentsPanel parentPanel)
	{

		super();
		this.corpus = corpus;
		this.parentWindow = parentWindow;
		this.parentPanel = parentPanel;

		root = new TreeNode(parentPanel);
		build(view);
		model = new DefaultTreeModel(root);
		setModel(model);

		setRootVisible(false);

		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(null);
		renderer.setOpenIcon(null);
		renderer.setClosedIcon(null);
		setCellRenderer(renderer);
		setShowsRootHandles(true);
		getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);
		setToggleClickCount(1000);
		setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		Font font = getFont();
		FontMetrics fontMetrics = getFontMetrics(font);
		setRowHeight(fontMetrics.getHeight());

		setTransferHandler(new TOCTreeTransferHandler());
        setDragEnabled(true);

		TreeDragMouseHandler mh = new TreeDragMouseHandler();
		addMouseListener(mh);
		addMouseMotionListener(mh);

		//	Get rid of any bindings for the space bar (e.g., on Windows).

		InputMap inputMap = getInputMap();
		KeyStroke[] keys = inputMap.allKeys();
		Object dummyAction = new Object();
		for (int i = 0; i < keys.length; i++) {
			KeyStroke key = keys[i];
			int keyCode = key.getKeyCode();
			if (keyCode == KeyEvent.VK_SPACE) inputMap.put(key, dummyAction);
		}

		addKeyListener(
			new KeyAdapter() {
				public void keyTyped (KeyEvent event) {
					try {
						char keyChar = event.getKeyChar();
						if (keyChar == '\n' || keyChar == ' ') {
							openSelected();
						}
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		addTreeExpansionListener(
			new TreeExpansionListener() {
				public void treeCollapsed (TreeExpansionEvent event) {
				}
				public void treeExpanded (TreeExpansionEvent event) {
					try {
						handleTreeExpansionEvent(event);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		init(view);

	}
	
	/**	Builds the tree.
	 *
	 *	@param	view		Table of contents view.
	 */
	 
	private void build (TconView view) {
		Collection worksCollection = corpus.getWorks();
		Work[] works = (Work[])worksCollection.toArray(
			new Work[worksCollection.size()]);
		int viewType = view.getViewType();
		switch (viewType) {
			case TconView.LIST_TAG_VIEW_TYPE:
				buildTagView(works);
				break;
			case TconView.LIST_PUB_YEAR_VIEW_TYPE:
				buildPubYearView(works);
				break;
			case TconView.LIST_VIEW_TYPE:
				buildListView(view.getWorkTags());
				break;
			case TconView.CATEGORY_VIEW_TYPE:
				buildCategoryView(view.getCategories());
				break;
			case TconView.BY_AUTHOR_VIEW_TYPE:
				buildByAuthorView(works);
				break;
		}
	}
	
	/**	Builds a tree view in alphabetical order by work tag.
	 *
	 *	@param	works		Array of works.
	 */
	 
	private void buildTagView (Work[] works) {
		Arrays.sort(works,
			new Comparator () {
				public int compare (Object o1, Object o2) {
					Work w1 = (Work)o1;
					Work w2 = (Work)o2;
					return w1.getTag().compareTo(w2.getTag());
				}
			}
		);
		addWorks(root, works);
	}
	
	/**	Builds a tree view in increasing order by publication year.
	 *
	 *	@param	works		Array of works.
	 */
	private void buildPubYearView (Work[] works) {
		Arrays.sort(works,
			new Comparator () {
				public int compare (Object o1, Object o2) {
					Work w1 = (Work)o1;
					Work w2 = (Work)o2;
					PubYearRange date1 = w1.getPubDate();
					PubYearRange date2 = w2.getPubDate();
					Integer year1 = date1 == null ? null : 
						date1.getStartYear();
					Integer year2 = date2 == null ? null :
						date2.getStartYear();
					if (year1 == null) {
						return year2 == null ? 0 : -1;
					} else if (year2 == null) {
						return +1;
					}
					return year1.intValue() - year2.intValue();
				}
			}
		);
		addWorks(root, works);
	}
	
	/**	Builds a list tree view.
	 *
	 *	@param	workTags		List of work tags.
	 */
	 
	private void buildListView (java.util.List workTags) {
		addWorksByTag(root, (String[])workTags.toArray(
			new String[workTags.size()]));
	}
	
	/**	Builds a category tree view.
	 *
	 *	@param	categories		List of categories.
	 */
	 
	private void buildCategoryView (java.util.List categories) {
		for (Iterator it = categories.iterator(); it.hasNext(); ) {
			TconCategory category = (TconCategory)it.next();
			String title = category.getTitle();
			java.util.List workTags = category.getWorkTags();
			TreeNode catNode = new TreeNode(title, parentPanel);
			int numCatNodes = addWorksByTag(catNode,
				(String[])workTags.toArray(new String[workTags.size()]));
			if (numCatNodes > 0) root.add(catNode);
		}
	}
	
	/**	Builds a by author tree view.
	 *
	 *	@apram	works		Array of works.
	 */
	 
	private void buildByAuthorView (Work[] works) {
		TreeMap map = new TreeMap(
			new Comparator () {
				public int compare (Object o1, Object o2) {
					Author author1 = (Author)o1;
					Author author2 = (Author)o2;
					Spelling name1 = author1.getName();
					Spelling name2 = author2.getName();
					if (name1 == null) return -1;
					if (name2 == null) return +1;
					return Compare.compareIgnoreCase(name1.getString(), name2.getString());
				}
			}
		);
		for (int i = 0; i < works.length; i++) {
			Work work = works[i];
			Set authors = work.getAuthors();
			for (Iterator it = authors.iterator(); it.hasNext(); ) {
				Author author = (Author)it.next();
				TreeSet set = (TreeSet)map.get(author);
				if (set == null) {
					set = new TreeSet(
						new Comparator () {
							public int compare (Object o1, Object o2) {
								Work w1 = (Work)o1;
								Work w2 = (Work)o2;
								PubYearRange date1 = w1.getPubDate();
								PubYearRange date2 = w2.getPubDate();
								Integer year1 = date1 == null ? null : 
									date1.getStartYear();
								Integer year2 = date2 == null ? null :
									date2.getStartYear();
								if (year1 == null) {
									return -1;
								} else if (year2 == null) {
									return +1;
								}
								int y1 = year1.intValue();
								int y2 = year2.intValue();
								if (y1 <= y2) {
									return -1;
								} else  {
									return +1;
								}
							}
						}
					);
					map.put(author, set);
				}
				set.add(work);
			}
		}
		for (Iterator it1 = map.keySet().iterator(); it1.hasNext(); ) {
			Author author = (Author)it1.next();
			Set set = (Set)map.get(author);
			String authorName = author.getName().getString();
			TreeNode catNode = new TreeNode(authorName, parentPanel);
			for (Iterator it2 = set.iterator(); it2.hasNext(); ) {
				Work work = (Work)it2.next();
				catNode.addUnloadedWorkPartNode(work);
			}
			root.add(catNode);
		}
	}

	/**	Initializes the tree.
	 *
	 *	@param	view		Table of contents view.
	 */

	private void init (TconView view) {
		int viewType = view.getViewType();
		for (int i = getRowCount()-1; i >= 0; i--) {
			TreePath path = getPathForRow(i);
			if (path == null) continue;
			TreeNode node = (TreeNode)path.getLastPathComponent();
			if (node == null) continue;
			if (node.getNodeType() == TreeNode.CATEGORY_NODE) {
				if (viewType == TconView.CATEGORY_VIEW_TYPE) {
					expandRow(i);
				} else if (viewType == TconView.BY_AUTHOR_VIEW_TYPE) {
					collapseRow(i);
				}
			}
		}
		setSelectionRow(0);
	}

	/** Adds works to a parent node.
	 *
	 *	@param	parent		Parent tree node.
	 *
	 *	@param	works		Array of works.
	 */

	private void addWorks (TreeNode parent, Work[] works) {
		for (int i = 0; i < works.length; i++) {
			Work work = works[i];
			parent.addUnloadedWorkPartNode(work);
		}
	}

	/**	Adds works to a parent node by tag.
	 *
	 *	@param	parent		Parent tree node.
	 *
	 *	@param	tags		Array of work tags.
	 *
	 *	@return		Number of works added.
	 */

	private int addWorksByTag (TreeNode parent, String[] tags) {
		Map map = corpus.getWorkMap();
		int n = 0;
		for (int i = 0; i < tags.length; i++) {
			String tag = tags[i];
			Work work = (Work)map.get(tag);
			if (work != null) {
				parent.addUnloadedWorkPartNode(work);
				n++;
			}
		}
		return n;
	}

	/**	Opens the selected work part.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	private void openSelected ()
		throws PersistenceException
	{
		TreePath path = getSelectionPath();
		if (path == null) return;
		TreeNode node = (TreeNode)path.getLastPathComponent();
		if (node == null) return;
		WorkPart workPart = node.getWorkPart();
		if (workPart == null) return;
		new WorkWindow(corpus, workPart, parentWindow);
	}

	/**	Handles a tree expansion event.
	 *
	 *	@param	event		The event.
	 */

	private void handleTreeExpansionEvent (TreeExpansionEvent event) {
		TreePath path = event.getPath();
		if (path == null) return;
		TreeNode node = (TreeNode)path.getLastPathComponent();
		if (node == null) return;
		WorkPart workPart = node.getWorkPart();
		if (workPart == null) return;
		if (node.isLoaded()) return;
		TreeNode placeHolder = (TreeNode)node.getFirstChild();
		model.removeNodeFromParent(placeHolder);
		java.util.List children = workPart.getChildren();
		int index = 0;
		for (Iterator it = children.iterator(); it.hasNext(); ) {
			WorkPart childPart = (WorkPart)it.next();
			TreeNode childNode =new TreeNode(childPart, parentPanel);
			model.insertNodeInto(childNode, node, index++);
			if (childPart.getHasChildren()) {
				placeHolder = new TreeNode(parentPanel);
				childNode.add(placeHolder);
			}
		}
		node.setLoaded(true);
		expandPath(path);
	}

	/**	Gets the corpus.
	 *
	 *	@return		The corpus.
	 */

	private Corpus getCorpus () {
		return corpus;
	}

	/**	Gets the tree root.
	 *
	 *	@return		The tree root.
	 */

	private TreeNode getRoot () {
		return root;
	}

		/**	TransferHandler for drag and drop
		 *
		 *
		 */

		protected class TOCTreeTransferHandler extends TransferHandler {
			SearchCriteriaTransferData data;
			DataFlavor xferFlavor;
			String xferType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.querytool.SearchCriteriaTransferData";

			/**	Drag icon. */
			ImageIcon dragicon = null;

			public TOCTreeTransferHandler() {
				try {
					xferFlavor = new DataFlavor(xferType);
				} catch (ClassNotFoundException e) {
					System.out.println("TOCTreeTransferHandler: unable to create data flavor");
				}
			}

			public TOCTreeTransferHandler(String property) {
				try {
					xferFlavor = new DataFlavor(xferType);
				} catch (ClassNotFoundException e) {
					System.out.println("TOCTreeTransferHandler: unable to create data flavor");
				}
			}

			public boolean importData(JComponent c, Transferable t) {return true;}

		protected void exportDone(JComponent c, Transferable data, int action) {}

		private boolean haslocalFlavor(DataFlavor[] flavors) {
			if (xferFlavor == null) {
				return false;
			}

			for (int i = 0; i < flavors.length; i++) {
				if (flavors[i].equals(xferFlavor)) {
					return true;
				}
			}
			return false;
		}

		public boolean canImport(JComponent c, DataFlavor[] flavors) { return false;}

		protected Transferable createTransferable(JComponent c) {
			SearchCriteriaTransferData gos = new SearchCriteriaTransferData();
			if (c instanceof JTree) {

				TreePath[] paths =  ((JTree)c).getSelectionPaths();
				if (paths == null || paths.length == 0) {
					return null;
				}
				try {
					for (int i = 0; i < paths.length; i++) {
						TreeNode node = (TreeNode)paths[i].getLastPathComponent();
						if(node.getNodeType()==TreeNode.WORK_PART_NODE) {
							SearchCriterion wp = node.getWorkPart();
							gos.add(wp);
						} else if(node.getNodeType()==TreeNode.CATEGORY_NODE) {
							for (Enumeration e = node.children() ; e.hasMoreElements() ;) {
								TreeNode child = (TreeNode)e.nextElement();
								if(child.getNodeType()==TreeNode.WORK_PART_NODE) {
									SearchCriterion wp = child.getWorkPart();
									gos.add(wp);
								}
							}
						}
					}
					return new SearchCriteriaTransferable(gos);
				} catch (Exception e) {Err.err(e);}
			}
			return null;
		}

		public int getSourceActions(JComponent c) {
			return COPY_OR_MOVE;
		}

		public Icon getVisualRepresentation(Transferable t) {
			if(dragicon==null) {
				dragicon = Images.get("icon.gif");
			}
			return dragicon;
		}
	}

	public class TreeDragMouseHandler implements MouseListener, MouseMotionListener {
		MouseEvent firstMouseEvent = null;

		public void mousePressed(MouseEvent e) {
			firstMouseEvent = e;
			e.consume();
		}
		public void mouseReleased (MouseEvent event) {
			try {
				if (event.getClickCount() > 1) openSelected();
			} catch (Exception e) {
				Err.err(e);
			}
		}
		public void mouseClicked(MouseEvent e)  {;}
		public void mouseEntered(MouseEvent e)  {;}
		public void mouseExited(MouseEvent e)  {;}

		public void mouseDragged(MouseEvent e) {
			if (firstMouseEvent != null) {
				e.consume();

				int ctrlMask = InputEvent.CTRL_DOWN_MASK;
				int action = ((e.getModifiersEx() & ctrlMask) == ctrlMask) ?
					  TransferHandler.COPY : TransferHandler.MOVE;

				int dx = Math.abs(e.getX() - firstMouseEvent.getX());
				int dy = Math.abs(e.getY() - firstMouseEvent.getY());
				if (dx > 5 || dy > 5) {
					JComponent c = (JComponent)e.getSource();
					TransferHandler handler = c.getTransferHandler();
					handler.exportAsDrag(c, firstMouseEvent, action);
					firstMouseEvent = null;
				}
			}
		}
		public void mouseMoved(MouseEvent e) {;}
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

