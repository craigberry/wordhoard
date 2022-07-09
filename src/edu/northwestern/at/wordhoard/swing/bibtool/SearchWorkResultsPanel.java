package edu.northwestern.at.wordhoard.swing.bibtool;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import java.awt.datatransfer.*;
import java.awt.dnd.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;

import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.querytool.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.bibtool.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.work.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;


/**	A search results panel.
 */

public class SearchWorkResultsPanel extends JSplitPane
	implements DropTargetListener, ClipboardOwner
{
	/**	Persistence manager. */

	private PersistenceManager pm;

	/**	The matching work occurrences. */

	private java.util.List works;

	/**	The number of hits label. */

	private JLabel numHitsLabel;

	/**	The tree. */

	private JTree tree;

	/**	The tree's root. */

	private	DefaultMutableTreeNode root;

	/**	The tree's model */

	DefaultTreeModel treeModel;


	/** DataFlavor for DnD */
	private	DataFlavor xferFlavor;

	/**	The work panel. */

	private WorkPanel workPanel;

	/**	Currently displayed work. */

	private Work work;

	/**	Currently displayed hit. */

	private Work curHit;

	/**	Tree cell height. */

	private int cellHeight;

	/**	Max tree height. */

	private int maxTreeHeight;

	/**	Parent window. */

	private	AbstractWorkPanelWindow parentWindow = null;

	/**	Grouping options. */

	private GroupingWorkOptions groupingOptions =
		new GroupingWorkOptions(
			GroupingWorkOptions.GROUP_BY_NONE,
			GroupingWorkOptions.ORDER_BY_WORK,
			GroupingWorkOptions.ASCENDING);

	/**	Tree cell renderer. */

	private SearchWorkResultsTreeCellRenderer renderer;

	/** Transfer Handler for cut/copy/past and DragNDrop */

	private	WorkTreeTransferHandler workTreeHandler = null;

	/** file handle for when opening a new WorkBag */
	private	File file = null;

	/**	Creates a new search results panel.
	 *
	 *	@param	pm					Persistence Manager.
	 *
	 *	@param	swq					Search criteria.
	 *
	 *	@param	parentWindow		Parent window.
	 */

	public SearchWorkResultsPanel (PersistenceManager pm,
		SearchWorkCriteria swq, AbstractWorkPanelWindow parentWindow)
	{

		super(JSplitPane.VERTICAL_SPLIT);
		this.parentWindow = parentWindow;
		this.pm = pm;
		Corpus corpus = null;
		JLabel sqLabel = null;

		if(swq != null) {
			corpus = swq.getCorpus();
			sqLabel = new JLabel(swq.toString());
			numHitsLabel = new JLabel("Search in progress");
		} else {
			sqLabel = new JLabel("");
			numHitsLabel = new JLabel("");
		}

		workTreeHandler = new WorkTreeTransferHandler();

		Font font = sqLabel.getFont();
		font = new Font(font.getName(), font.getStyle(), 9);
		sqLabel.setFont(font);
		sqLabel.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
		sqLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		numHitsLabel.setFont(font);
		numHitsLabel.setBorder(BorderFactory.createEmptyBorder(3,5,0,0));
		numHitsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		SearchWorkResultsGroupingOptionsPanel optionsPanel =
			new SearchWorkResultsGroupingOptionsPanel(
				font, groupingOptions, this);
		optionsPanel.setBorder(BorderFactory.createEmptyBorder(3,5,2,0));
		optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Allow multiple selections of visible nodes
		tree = new JTree(new Object[0]);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		tree.setToggleClickCount(1000);
		tree.setTransferHandler(workTreeHandler);
        tree.setDragEnabled(true);

		font = tree.getFont();
		font = new Font(font.getName(), font.getStyle(), 10);
		tree.addTreeSelectionListener(
			new TreeSelectionListener () {
				public void valueChanged (TreeSelectionEvent event) {
					try {
						showSelected();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		TreeDragMouseHandler mh = new TreeDragMouseHandler();
		tree.addMouseListener(mh);
		tree.addMouseMotionListener(mh);

		String localType =
			DataFlavor.javaJVMLocalObjectMimeType +
			";class=edu.northwestern.at.wordhoard.swing.querytool.SearchCriteriaTransferData";
		try {
			xferFlavor = new DataFlavor(localType);
		} catch (ClassNotFoundException e) {
//			System.out.println(
//				"SearchCriteriaTransferHandler:
//				unable to create data flavor, " + e.getMessage());
		}

		DropTarget dropTarget = new DropTarget(tree, this);

		JScrollPane scrollPane = new JScrollPane();
		MyViewport viewport = new MyViewport();
		viewport.setView(tree);
		scrollPane.setViewport(viewport);
		scrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

		renderer = new SearchWorkResultsTreeCellRenderer(font, corpus,
			scrollPane);
		cellHeight = renderer.getHeight();
		tree.setRowHeight(cellHeight);
		tree.setCellRenderer(renderer);

		JPanel hitsPanel = new JPanel();
		hitsPanel.setLayout(new BoxLayout(hitsPanel, BoxLayout.Y_AXIS));
		hitsPanel.add(sqLabel);
		hitsPanel.add(numHitsLabel);
 		hitsPanel.add(optionsPanel);
		hitsPanel.add(Box.createVerticalStrut(2));
		hitsPanel.add(scrollPane);
		hitsPanel.setBorder(BorderFactory.createEmptyBorder(3,0,10,0));

		workPanel = new WorkPanel(pm, parentWindow);
		workPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		workPanel.addKeyListener(
			new KeyAdapter () {
				public void keyPressed (KeyEvent event) {
					try {
						int code = event.getKeyCode();
						if (code == KeyEvent.VK_DOWN ||
							code == KeyEvent.VK_UP ||
							code == KeyEvent.VK_LEFT ||
							code == KeyEvent.VK_RIGHT)
								tree.dispatchEvent(event);
					} catch (Exception e) {
						Err.err(e);
					}
				}
				public void keyReleased(KeyEvent e)
				{
					if	(	( e.getID() == KeyEvent.KEY_RELEASED ) &&
							( ( e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) ||
							  ( e.getKeyCode() == KeyEvent.VK_DELETE ) ) )
					{
						deleteSelectedItems();
					}
				}
			}
		);

		setTopComponent(hitsPanel);
		setBottomComponent(workPanel);

		setDividerLocation(0.45);
		setContinuousLayout(true);
		setResizeWeight(0.45);
	}

	/**	Load a work set.
	 */

	private void loadWorkSet ()
		throws Exception
	{
		OpenWorkSetDialog dialog =
			new OpenWorkSetDialog
			(
				WordHoardSettings.getString
				(
					"SelectWorkSet" ,
					"Select Work Set"
				) ,
				parentWindow
			);

		dialog.show( parentWindow );

		if ( !dialog.getCancelled() )
		{
			final long startTime = System.currentTimeMillis();
			WorkSet ws = dialog.getSelectedItem();
			Work[] works = WorkSetUtils.getWorksOnly(ws);
			if(works != null) {
				ArrayList alist = new ArrayList();
				for ( int i = 0 ; i < works.length ; i++ )
				{
					alist.add(works[i]);
				}
				setHits(alist, startTime);
			} else {
//				System.out.println(
//					getClass().getName() + " loadWorkSet - works is null");
			}
		}
		dialog.dispose();
	}

	/**	Save selected works in a work set.
	 * 
	 * @throws	Exception	general error.
	 */

	public void saveWorkSet()
		throws Exception
	{
		WorkSetUtils.saveWorkSet
		(
			parentWindow ,
			new WorkPartGetter()
			{
				public java.util.List getWorkParts()
				{
					ArrayList alist	= new ArrayList();

					for ( Iterator it = works.iterator() ; it.hasNext(); )
					{
						Object o	= it.next();

						if ( o instanceof Work )
						{
							alist.add( o );
						}
						else
						{
//							System.out.println(
//								getClass().getName() +
//								" saveWorkSet - haveclass" +
//								o.getClass().getName());
						}
					}

					return alist;
				}
			}
		);
	}

	/**	cut action - remove selected items from work bag and copy to pastebuffer
	 *
	 */

	public void cut() {
		try {
			TreePath[] paths = tree.getSelectionPaths();
            if (paths == null || paths.length == 0) {
                return;
            }
            ArrayList alist = new ArrayList(paths.length);
            for (int i = 0; i < paths.length; i++) {
              Object o = ((DefaultMutableTreeNode)paths[i].getLastPathComponent()).getUserObject();
				if (o instanceof Work) {
					alist.add(o);
				} else {
					// MAKE THIS RECURSIVE TO HANDLE N LEVELS
					for (Enumeration e = ((DefaultMutableTreeNode)paths[i].getLastPathComponent()).children() ; e.hasMoreElements() ;) {
						alist.add(((DefaultMutableTreeNode)e.nextElement()).getUserObject());
					}
				}
            }
			if(works==null) {works = new java.util.ArrayList();}
			works.removeAll(alist);
			Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable t = workTreeHandler.createTransferable(tree);
			sysClip.setContents(t,this);
			buildTree();
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + " exception: cut");
		}
	}

	/**	copy action - copy selected items from work bag to pastebuffer
	 *
	 */
	public void copy() {
		try {
			TreePath[] paths = tree.getSelectionPaths();
            if (paths == null || paths.length == 0) {
                return;
            }
            ArrayList alist = new ArrayList(paths.length);
            for (int i = 0; i < paths.length; i++) {
              Object o = ((DefaultMutableTreeNode)paths[i].getLastPathComponent()).getUserObject();
				if (o instanceof Work) {
					alist.add(o);
				} else {
					// MAKE THIS RECURSIVE TO HANDLE N LEVELS
					for (Enumeration e = ((DefaultMutableTreeNode)paths[i].getLastPathComponent()).children() ; e.hasMoreElements() ;) {
						alist.add(((DefaultMutableTreeNode)e.nextElement()).getUserObject());
					}
				}
            }
			Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable t = workTreeHandler.createTransferable(tree);
			sysClip.setContents(t,this);
		} catch (Exception e) {Err.err(e);}
	}

	/**	paste action - copy selected items from pastebufferto work bag
	 *
	 */
	public void paste() {
			try {
				Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable t = sysClip.getContents(this);
				DataFlavor df =
					new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType +
						";class=java.util.ArrayList");
				if(t.isDataFlavorSupported(df)) {
					ArrayList alist =
						(ArrayList) sysClip.getContents(null).getTransferData(df);
					if(works==null) {works = new java.util.ArrayList();}
					for (int i=0; i < alist.size(); i++) {
						if(!works.contains(alist.get(i))){
							works.add(alist.get(i));}
					}
				}
			} catch (Exception e) {Err.err(e);}
			try {
				buildTree();
			} catch (PersistenceException e) {Err.err(e);}
	}

	/**	A viewport that never scrolls horizontally.
	 *
	 *	<p>This is a hack that works around a bug in Swing's JTree class.
	 *	With our custom renderer, without the hack, the up and down arrow
	 *	keys cause the view to scroll left. This hack simply disables all
	 *	horizontal scrolling in the viewport.
	 */

	private static class MyViewport extends JViewport {
		public void setViewPosition (Point p) {
			p.x = 0;
			super.setViewPosition(p);
		}
	}

	/**	Sets the hits.
	 *
	 *	@param	works		The list of matching works from
	 *						the search.
	 *
	 *	@param	startTime	System milliseconds start time of search.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public void setHits (java.util.List works, long startTime)
		throws PersistenceException
	{
		maxTreeHeight = tree.getHeight();
		this.works = works;
		int numHits = works.size();
		String numHitsStr = Formatters.formatIntegerWithCommas(numHits);
		long endTime = System.currentTimeMillis();
		float time = (endTime - startTime) / 1000.0f;
		String timeStr = Formatters.formatFloat(time, 1);
		numHitsLabel.setText(
			numHitsStr + (numHits == 1 ? " work found" : " works found") +
			" in " + timeStr + " seconds");
		buildTree();
	}

	/**	Sets the works for this panel.
	 *
	 *	@param	works		The list of works
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public void setWorks (java.util.List works)
		throws PersistenceException
	{
		maxTreeHeight = tree.getHeight();
		this.works = works;
		int numHits = works.size();
		String numHitsStr = Formatters.formatIntegerWithCommas(numHits);
		numHitsLabel.setText
		(
			numHitsStr + " " +
				( numHits == 1 ?
					WordHoardSettings.getString
					(
						"singularork" ,
						"work"
					)
					:
					WordHoardSettings.getString
					(
						"pluralwork" ,
						"works"
					)
				)
		);

		buildTree();
	}

	/**	Gets the corpus associated with the work in this window.
	 *
	 *	@param	work	The work in this window.
	 *	@return		The corpus associated with this window.
	 */

	public Corpus getCorpus (Work work) { return work.getCorpus();}

	protected void addNodes(Collection c) {
		SearchCriterion sc = null;
		GroupingObject group = null;
		int groupBy = groupingOptions.getGroupBy();
		boolean grouping = (groupBy != GroupingWorkOptions.GROUP_BY_NONE);
		if (grouping) {
			Map newGroupMap = groupingOptions.group(c);
			Set newGroups = newGroupMap.keySet();
			for (Iterator it = newGroups.iterator(); it.hasNext(); ) {
				GroupingObject currentGroup = (GroupingObject)it.next();
				Collection newWorks = (Collection)newGroupMap.get(currentGroup);
				DefaultMutableTreeNode parentNode = getNodeForGroup(currentGroup);

				if(parentNode==null) {
					parentNode= new DefaultMutableTreeNode(currentGroup);
					treeModel.insertNodeInto(parentNode, root, root.getChildCount());

					for (Iterator git = newWorks.iterator(); git.hasNext(); ) {
						sc=(SearchCriterion)git.next();
						works.add(sc);
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(sc);
						treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
						tree.makeVisible(new TreePath(node.getPath()));
					}
				} else {
					for (Iterator git = newWorks.iterator(); git.hasNext(); ) {
						sc=(SearchCriterion)git.next();
						if(!works.contains(sc)) {
							works.add(sc);
							DefaultMutableTreeNode node = new DefaultMutableTreeNode(sc);
							treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
							tree.makeVisible(new TreePath(node.getPath()));
						}
					}
				}
			}
		} else {
			for (Iterator it = c.iterator(); it.hasNext(); ) {
				Work work = (Work)it.next();
				if(!works.contains(work)) { works.add(work);}
//				DefaultMutableTreeNode node = new DefaultMutableTreeNode(work, true);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(work);
				treeModel.insertNodeInto(node, root, root.getChildCount());
				tree.makeVisible(new TreePath(node.getPath()));
			}
		}
	}


	protected Collection getChildUserObjectsForNode(DefaultMutableTreeNode node) {
		ArrayList alist = new ArrayList();
		for (Enumeration e = node.children() ; e.hasMoreElements() ;) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)e.nextElement();
			alist.add(child.getUserObject());
		}
		return alist;
	}

	protected DefaultMutableTreeNode getNodeForGroup(GroupingObject group) {
		DefaultMutableTreeNode notfound = null;
		for (Enumeration e = root.children() ; e.hasMoreElements() ;) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)e.nextElement();
			Object o = child.getUserObject();
			if(o instanceof GroupingObject && o.equals(group)) {return child;}
		}
		return notfound;
	}

	/**	delete action - remove selected items from work bag and copy to pastebuffer
	 *
	 */

	 protected void deleteSelectedItems() {
		try {
			TreePath[] paths = tree.getSelectionPaths();
			if (paths == null || paths.length == 0) {
				return;
			}

			while(paths != null && paths.length != 0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[0].getLastPathComponent();
				Object o = node.getUserObject();

				if(o instanceof Work) {
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
					Object p = parentNode.getUserObject();
					treeModel.removeNodeFromParent(node);
					works.remove(o);
					if((p!=null) && (p instanceof GroupingObject) && (parentNode.getChildCount()==0)) {
						treeModel.removeNodeFromParent(parentNode);
					}

				} else if(o instanceof GroupingObject) {
					// remove it and its children
					treeModel.removeNodeFromParent(node);
					Map map = groupingOptions.group(works);
					Collection items = (Collection)map.get(o);
					works.removeAll((Collection)map.get(o));
				}
				paths = tree.getSelectionPaths();
			}
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + " exception: cut " + e.getMessage());
		}
	}

	/**	Builds or rebuilds the tree of hits.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	private void buildTree ()
		throws PersistenceException
	{
		root = new DefaultMutableTreeNode();
		int groupBy = groupingOptions.getGroupBy();
		int orderBy = groupingOptions.getOrderBy();
		boolean grouping = (groupBy != GroupingWorkOptions.GROUP_BY_NONE);
		renderer.setRenderWorksWithDates(
			groupBy == GroupingWorkOptions.GROUP_BY_NONE &&
			orderBy == GroupingWorkOptions.ORDER_BY_DATE);
		if(works==null) {works = new java.util.ArrayList();}
		int numHits = works.size();
		if (grouping) {
			Map map = groupingOptions.group(works);
			Set groups = map.keySet();
			for (Iterator it = groups.iterator(); it.hasNext(); ) {
				GroupingObject obj = (GroupingObject)it.next();
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(obj);
				ArrayList list = (ArrayList)map.get(obj);
				addWorks(node, list);
				root.add(node);
			}
		} else {
			addWorks(root, groupingOptions.sort(works));
		}

		treeModel = new DefaultTreeModel(root);
		tree.setModel(treeModel);
		if (numHits > 0 &&
			groupBy != GroupingWorkOptions.GROUP_BY_NONE)
				tree.setSelectionRow(0);
		int numRows = 0;
		int numRootChildren = root.getChildCount();
		if (grouping) {
			numRows = numRootChildren + numHits;
		} else {
			numRows = numRootChildren;
		}
		setDividerLocation(0.45);
		int fullHeight = numRows * cellHeight;
		if (fullHeight <= maxTreeHeight) {
			int dividerLocation = getDividerLocation();
			setDividerLocation(dividerLocation -
				(maxTreeHeight-fullHeight) + 10);
			setResizeWeight(0.0);
		} else {
			setResizeWeight(0.45);
		}
	}

	/**	Adds a collection of works to a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	works		List of works.
	 */

	private void addWorks (DefaultMutableTreeNode node,
		Collection items)
	{
		if(items==null) {items = new java.util.ArrayList();}

		for (Iterator it = items.iterator(); it.hasNext(); ) {
			Work work = (Work)it.next();
//			DefaultMutableTreeNode wNode = new DefaultMutableTreeNode(work, true);
			DefaultMutableTreeNode wNode = new DefaultMutableTreeNode(work);
			node.add(wNode);
		//	addWorkParts(wNode,((WorkPart)work).getChildren());
		}
	}

	/**	Adds a collection of workparts to a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	workparts		List of workparts.
	 */

	private void addWorkParts (DefaultMutableTreeNode node,
		Collection items)
	{
		if(items==null) {items = new java.util.ArrayList();}

		for (Iterator it = items.iterator(); it.hasNext(); ) {
			WorkPart workpart = (WorkPart)it.next();
//			DefaultMutableTreeNode wNode = new DefaultMutableTreeNode(workpart, true);
			DefaultMutableTreeNode wNode = new DefaultMutableTreeNode(workpart);
			node.add(wNode);

	/*		for (Iterator wit = workpart.getChildren().iterator(); wit.hasNext(); ) {
				WorkPart wp = (WorkPart)wit.next();
				DefaultMutableTreeNode cNode = new DefaultMutableTreeNode(wp,  wp.hasChildren());
				wNode.add(cNode);
			}*/
		}
	}

	/**	Handles new grouping options.
	 *
	 *	@param	options		New grouping options.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public void handleNewGroupingOptions (GroupingWorkOptions options)
		throws PersistenceException
	{
	//	if (options.equals(groupingOptions)) return;
		groupingOptions = options;
		if (works != null) buildTree();
	}

	/**	Gets the work panel.
	 * @return	The work panel.
	*/

	public WorkPanel getWorkPanel () {
		return workPanel;
	}

	/**	Shows the selected works from the hit list.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 *
	 */

	private void showSelected ()  {
		workPanel.requestFocus();
		TreePath path = tree.getSelectionPath();
		if (path == null) return;
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode)path.getLastPathComponent();
		if (node == null) return;
		Object obj = node.getUserObject();
		if (!(obj instanceof Work)) return;
		Work work = (Work)obj;
		parentWindow.setCorpus(getCorpus(work));
		if (work == null || work == curHit) return;
		curHit = work;
		new Thread (
			new Runnable() {
				public void run () {
					SwingUtilities.invokeLater(new Runnable() {
							public void run () {
								try {
									workPanel.setPart((WorkPart)pm.load(WorkPart.class, curHit.getId()));
								} catch (PersistenceException e) {Err.err(e);}
							}
						}
					);
				}
			}
		).start();
	}


		public void lostOwnership(Clipboard clipboard, Transferable contents) {
//				System.out.println(this.getClass().getName() + ": lostOwnership");
		}



	/**	TransferHandler for drag and drop
	 *
	 *
	 */

	public class WorkTreeTransferHandler extends TransferHandler {
		SearchCriteriaTransferData data;
		DataFlavor xferFlavor;
		String xferType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.querytool.SearchCriteriaTransferData";

		public WorkTreeTransferHandler() {
			try {
				xferFlavor = new DataFlavor(xferType);
			} catch (ClassNotFoundException e) {
				System.out.println("WorkTreeTransferHandler: unable to create data flavor");
			}
		}

		public WorkTreeTransferHandler(String property) {
			try {
				xferFlavor = new DataFlavor(xferType);
			} catch (ClassNotFoundException e) {
				System.out.println("WorkTreeTransferHandler: unable to create data flavor");
			}
		}

		public boolean importData(JComponent c, Transferable t) {
			if (!canImport(c, t.getTransferDataFlavors())) {return false;}

			try {
				if (hasLocalArrayListFlavor(t.getTransferDataFlavors())) {
					data = (SearchCriteriaTransferData)t.getTransferData(xferFlavor);
				} else {
					return false;
				}
			} catch (UnsupportedFlavorException ufe) {
				System.out.println("importData: unsupported data flavor");
				return false;
			} catch (IOException ioe) {
				System.out.println("importData: I/O exception");
				return false;
			}

/*			if(works==null) {works = new java.util.ArrayList();}
			for (int i=0; i < data.size(); i++) {
				if(!works.contains(data.get(i))) {works.add(data.get(i));}
			}
			if (data.size() > 0) {
				try {
					buildTree();
				} catch (PersistenceException e) {Err.err(e);}
			}
			*/
        return true;
    }

    protected void exportDone(JComponent c, Transferable data, int action) {
 /*       if ((action == MOVE) && (indices != null)) {
            DefaultTreeModel model = (DefaultTreeModel)source.getModel();
*/
            //If we are moving items around in the same list, we
            //need to adjust the indices accordingly since those
            //after the insertion point have moved.
 /*           if (addCount > 0) {
                for (int i = 0; i < indices.length; i++) {
                    if (indices[i] > addIndex) {
                        indices[i] += addCount;
                    }
                }
            }
            for (int i = indices.length -1; i >= 0; i--)
                model.remove(indices[i]);
        }
        indices = null;
        addIndex = -1;
        addCount = 0;
*/    }

    private boolean hasLocalArrayListFlavor(DataFlavor[] flavors) {
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

    public boolean canImport(JComponent c, DataFlavor[] flavors) {
        if (hasLocalArrayListFlavor(flavors))  { return true; }
        return false;
    }

    protected Transferable createTransferable(JComponent c) {
		SearchCriteriaTransferData gos = new SearchCriteriaTransferData();
        if (c instanceof JTree) {
			TreePath[] paths =  ((JTree)c).getSelectionPaths();
            if (paths == null || paths.length == 0) {
                return null;
            }

			try {
				for (int i = 0; i < paths.length; i++) {
				  Object o = ((DefaultMutableTreeNode)paths[i].getLastPathComponent()).getUserObject();
					if (o instanceof Work) {
						gos.add(o);
					} else if (o instanceof Author) {
						gos.add(o);
					} else if (o instanceof Corpus) {
						gos.add(o);
					} else if (o instanceof DateGroup) {
						gos.add(new PubYearRange(((DateGroup)o).getStartYear(),((DateGroup)o).getEndYear()) );
//						gos.add(new DateRange(((DateGroup)o).getLabel(),((DateGroup)o).getStartYear(),((DateGroup)o).getEndYear()) );
					} else {					// MAKE THIS RECURSIVE TO HANDLE N LEVELS
						for (Enumeration e = ((DefaultMutableTreeNode)paths[i].getLastPathComponent()).children() ; e.hasMoreElements() ;) {
							gos.add(((DefaultMutableTreeNode)e.nextElement()).getUserObject());
						}
					}
				}
			} catch (Exception e) {Err.err(e);}
			return new SearchCriteriaTransferable(gos);
        }
        return null;
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

/*	public Icon getVisualRepresentation(Transferable t) {
			System.out.println("getVisualRepresentation");
		if(dragicon==null) {
			dragicon = Images.get("icon.gif");
			System.out.println("got dragicon " + dragicon.getIconWidth() + " x " + dragicon.getIconHeight());
		}
		return dragicon;
	}
*/

    public class ArrayListTransferable implements Transferable {
        ArrayList data;

        public ArrayListTransferable(ArrayList alist) {
            data = alist;
        }

        public Object getTransferData(DataFlavor flavor)
                                 throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return data;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { xferFlavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            if (xferFlavor.equals(flavor)) {
                return true;
            }
            return false;
        }
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
				workPanel.requestFocus();
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

  /** DropTargetListener interface method - What we do when drag is released */
  public void drop(DropTargetDropEvent e) {

	System.out.println(getClass().getName() + " drop()");
	SearchCriteriaTransferData data = null;
    try {
      Transferable tr = e.getTransferable();

      //flavor not supported, reject drop
        if (!haslocalFlavor(tr.getTransferDataFlavors()))  {System.out.println("Can't import:" + tr.toString()); e.rejectDrop(); }

      //cast into appropriate data type
		data =  (SearchCriteriaTransferData) tr.getTransferData(xferFlavor );
		// get works
		ArrayList alist = new ArrayList();
		for (Iterator it = data.iterator(); it.hasNext(); ) {
			Object o = it.next();
			if(o instanceof Work) {alist.add(o);}
/*			else if(o instanceof WorkPart) {
				Work wk = ((WorkPart)o).getWork();
				System.out.println(getClass().getName() + " drop - have WorkPart");
				if(!works.contains(wk)) {
					System.out.println(getClass().getName() + " drop - have WorkPart, adding work:" + wk.toString());
					((WorkPart)wk).setActive(false);
					alist.add(wk);
				}*/
			else {System.out.println(getClass().getName() + " drop - haveclass: " + o.getClass().getName());}
		}
		addNodes(alist);
		int action = e.getDropAction();
		boolean copyAction = (action == DnDConstants.ACTION_COPY);

		e.getDropTargetContext().dropComplete(true);
    }
    catch (IOException io) { e.rejectDrop(); }
    catch (UnsupportedFlavorException ufe) {e.rejectDrop();}
  } //end of method

  /** DropTaregetListener interface method */
  public void dragEnter(DropTargetDragEvent e) {
  }

  /** DropTaregetListener interface method */
  public void dragExit(DropTargetEvent e) {
  }

  /** DropTaregetListener interface method */
  public void dragOver(DropTargetDragEvent e) {
		/* ********************** CHANGED ********************** */
    //set cursor location. Needed in setCursor method
    Point cursorLocationBis = e.getLocation();
        TreePath destinationPath = tree.getPathForLocation(cursorLocationBis.x, cursorLocationBis.y);

    	e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE ) ;
  }

private String testDropTarget(TreePath destination, TreePath dropper) {  /** DropTaregetListener interface method */
	return null;
}

  public void dropActionChanged(DropTargetDragEvent e) {
  }

//

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

