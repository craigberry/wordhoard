package edu.northwestern.at.wordhoard.swing.bibtool;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.net.*;
import java.text.*;
import javax.swing.filechooser.*;

import java.beans.*;

import java.awt.datatransfer.*;
import java.awt.dnd.*;

import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.helpers.DefaultHandler;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.querytool.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.querytool.*;
import edu.northwestern.at.wordhoard.model.bibtool.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.work.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.concordance.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;


/**	A search results panel.
 */

public class WorkSetPanel extends JPanel implements ActionListener, PropertyChangeListener, DropTargetListener, ClipboardOwner  {

	/**	Persistence manager. */

	private PersistenceManager pm;

	/**	The work parts. */

	private HashSet workparts;

	/**	The "wrapper" work parts. Parts that just wrap lower level parts that are part of the work structure. */

	private HashSet workpartwrappers;


	/**	Maps objects to nodes for easy insert */

	private HashMap nodeMap;

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

	private WorkPart curHit;

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

	private WorkSetTreeCellRenderer renderer;

	/**	focue Owner for Edit Menu handling. */
    private JComponent focusOwner = null;

	/** Transfer Handler for cut/copy/past and DragNDrop */

	private	WorkTreeTransferHandler workTreeHandler = null;

	/** file handle for when opening a new WorkBag */
	private	File file = null;

	/**	Creates a new search results panel.
	 *
	 *	@param	parentWindow		Parent window.
	 */

	public WorkSetPanel (PersistenceManager pm, AbstractWorkPanelWindow parentWindow)
	{

		super();
		this.parentWindow = parentWindow;
		this.pm = pm;

		workparts = new HashSet();

		workpartwrappers = new HashSet();
		nodeMap = new HashMap();

		workTreeHandler = new WorkTreeTransferHandler();

		JLabel wsLabel = new JLabel("Drag in works(parts) from TOC and search results.");
		wsLabel.setBorder(BorderFactory.createEmptyBorder(3,5,2,0));
		wsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		wsLabel.setForeground(Color.darkGray);

		Font font = wsLabel.getFont();
		font = new Font(font.getName(), font.getStyle(), 9);
		WorkSetGroupingOptionsPanel optionsPanel = new WorkSetGroupingOptionsPanel(font, groupingOptions, this);
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
/*		tree.addTreeSelectionListener(
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
*/
		tree.addTreeWillExpandListener(
			new TreeWillExpandListener () {
				public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException	{;}

				public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException	{
					try {
						DefaultMutableTreeNode theNode = (DefaultMutableTreeNode)event.getPath().getLastPathComponent();
						Object o = theNode.getUserObject();
						if(!workpartwrappers.contains(o)) {
							 for (Enumeration e = theNode.children(); e.hasMoreElements() ;) {
								DefaultMutableTreeNode cn = (DefaultMutableTreeNode)e.nextElement();
								WorkPart workpart = (WorkPart)cn.getUserObject();
								if(!workpartwrappers.contains(workpart) && workpart.getHasChildren() && (cn.getChildCount()==0)) {
									addWorkParts(cn,workpart.getChildren());
								}
							}
						}
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		TreeDragMouseHandler mh = new TreeDragMouseHandler();
		tree.addMouseListener(mh);
		tree.addMouseMotionListener(mh);

		String localType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.querytool.SearchCriteriaTransferData";
		try {
			xferFlavor = new DataFlavor(localType);
		} catch (ClassNotFoundException e) {
			System.out.println("SearchCriteriaTransferHandler: unable to create data flavor, " + e.getMessage());
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

		renderer = new WorkSetTreeCellRenderer(font, scrollPane);
		cellHeight = renderer.getHeight();
		tree.setRowHeight(cellHeight);
		tree.setCellRenderer(renderer);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(scrollPane);
		add(wsLabel);
		setBorder(BorderFactory.createEmptyBorder(3,0,10,0));

		tree.addKeyListener(
			new KeyAdapter () {
				public void keyPressed (KeyEvent event) {}
				public void keyReleased(KeyEvent e)
				{
					if	(	( e.getID() == KeyEvent.KEY_RELEASED ) &&
							(	( e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) ||
								( e.getKeyCode() == KeyEvent.VK_DELETE ) ) )
					{
						deleteSelectedItems();
					}
				}
			}
		);

		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addPropertyChangeListener("permanentFocusOwner", this);
	}

	public void loadWorkSet() throws Exception {
		OpenWorkSetDialog dialog = new OpenWorkSetDialog("Select Work Set", parentWindow);
		dialog.show( parentWindow );

		if ( !dialog.getCancelled() )
		{
			final long startTime = System.currentTimeMillis();
			WorkSet ws = dialog.getSelectedItem();
			WorkPart[] workparts = WorkSetUtils.getWorkParts(ws);
			if(workparts != null) {
				HashSet aset = new HashSet();
				for ( int i = 0 ; i < workparts.length ; i++ )
				{
					aset.add(workparts[i]);
				}
				loadWorkSetTree(aset);
				parentWindow.setTitle(ws.getTitle());
			}
		}

	}

	public void saveWorkSet ()
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

					Object root = treeModel.getRoot();

					walk( treeModel, root, alist );

					return alist;
				}
			}
		);
	}

	  protected void walk(TreeModel model, Object o, ArrayList alist){
		int  cc;
		cc = model.getChildCount(o);
		for( int i=0; i < cc; i++) {
		  Object child = model.getChild(o, i );
		  if((child instanceof WorkPartTreeNode) && !((WorkPartTreeNode)child).isWrapper()) alist.add(((WorkPartTreeNode)child).getUserObject());
		  if (!model.isLeaf(child)) walk(model,child,alist);
		 }
	   }


	// methods for handling Edit menu
    public void propertyChange(PropertyChangeEvent e) {
	//	System.out.println(this.getClass().getName() + " propertyChange:" + e.toString());
        Object o = e.getNewValue();
        if (o instanceof JComponent) {
            focusOwner = (JComponent)o;
        } else {
            focusOwner = null;
        }
    }


    public void actionPerformed(ActionEvent e) {
        if (focusOwner == null)
            return;
		try {
			String action = (String)e.getActionCommand();
			if(action.equals("cut")) {cut();}
	//		else if (action.equals("copy")) {copy();}
	//		else if (action.equals("paste")) {paste();}
	//		else if (action.equals("quit")) {WordHoard.quit();}
			else if (action.equals("new")) {new WorkSetWindow((AbstractWorkPanelWindow)parentWindow);}
			else if (action.equals("save")) {save();}
			else if (action.equals("open")) {open();}
		} catch (PersistenceException ex) {Err.err(ex);}
    }


/*     public void actionPerformed(ActionEvent e) {
        if (focusOwner == null)
            return;
        String action = (String)e.getActionCommand();
        Action a = focusOwner.getActionMap().get(action);
        if (a != null) {
            a.actionPerformed(new ActionEvent(focusOwner,
                                              ActionEvent.ACTION_PERFORMED,
                                              null));
        }
    }
	*/

	/**	cut action - remove selected items from work bag and copy to pastebuffer
	 *
	 */
	protected void cut() {
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
			if(workparts==null) {workparts = new HashSet();}
			workparts.removeAll(alist);
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
	protected void copy() {
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
	protected void paste() {
			try {
				Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable t = sysClip.getContents(this);
				DataFlavor df = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=java.util.ArrayList");
				if(t.isDataFlavorSupported(df)) {
					ArrayList alist = (ArrayList) sysClip.getContents(null).getTransferData(df);
					if(workparts==null) {workparts = new HashSet();}
					for (int i=0; i < alist.size(); i++) {
						if(!workparts.contains(alist.get(i))) {workparts.add(alist.get(i));}
					}
				}
			} catch (Exception e) {Err.err(e);}
			try {
				buildTree();
			} catch (PersistenceException e) {Err.err(e);}
	}


	/**	Open from file.
	 *
	 */

	 public void open() {
/*		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			try {
				new Thread (
					new Runnable() {
						public void run () {
							SwingUtilities.invokeLater(new Runnable() {
									public void run () {
										try {
											new WorkSetWindow(file, (AbstractWindow) parentWindow);
										} catch (PersistenceException e) {Err.err(e);}
									}
								}
							);
						}
					}
				).start();
			} catch (Exception e) {Err.err(e);}
		} else {
				System.out.println("Open command cancelled by user.");
		}
*/
	}

	/** save action -
	 *
	 *
	 */

	 protected void save() {
		String[] fileToSaveTo	= FileDialogs.save( this.parentWindow );

		if (fileToSaveTo != null) {
			File file = new File( fileToSaveTo[ 0 ] , fileToSaveTo[ 1 ] );
			try {
				Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file),"UTF8"));
				out.write(xmlToStringBuffer());
				out.close();
			} catch (Exception e) {Err.err(e);}
			//this is where a real application would save the file.
			System.out.println("Saving: " + file.getName());
		} else {
				System.out.println("Save command cancelled by user.");
		}
	}

	protected String xmlToStringBuffer() {
		StringBuffer sb = new StringBuffer();
		if(workparts==null) {workparts = new HashSet();}

		sb.append("<?xml version=\"1.0\"?>\n");
		sb.append("<works>\n");
		for (Iterator it = workparts.iterator(); it.hasNext(); ) {
			Work work = (Work)it.next();
			sb.append("<work id=\"" + work.getId() + "\">\n");
			for (Iterator ait = work.getAuthors().iterator(); ait.hasNext(); ) {
				Author ath= (Author)ait.next();
				sb.append("<author>" + ath.getName() + "</author>\n");
			}
			sb.append("<title>" + work.getFullTitle() + "</title>\n");
			PubYearRange pubDate = work.getPubDate();
			Integer startYear = pubDate == null ? null : pubDate.getStartYear();
			Integer endYear = pubDate == null ? null : pubDate.getEndYear();
			sb.append("<earlyDate>" + startYear + "</earlyDate>\n");
			sb.append("<lateDate>" + endYear + "</lateDate>\n");
			sb.append("</work>\n");
		}

		sb.append("</works>\n");
		return sb.toString();
	}

	/**	new action
	 *
	 */
	protected void newWindow() {
		try {
			new WorkSetWindow((AbstractWindow)parentWindow);
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
	 *	@param	workparts	Hash set of matching work parts from
	 *						the search.
	 *
	 *	@param	startTime	System milliseconds start time of search.
	 *
	 *	@throws	PersistenceException
	 */

	public void setHits (HashSet workparts, long startTime)
		throws PersistenceException
	{
		maxTreeHeight = tree.getHeight();
		this.workparts = workparts;
		int numHits = workparts.size();
		String numHitsStr = Formatters.formatIntegerWithCommas(numHits);
		long endTime = System.currentTimeMillis();
		float time = (endTime - startTime) / 1000.0f;
		String timeStr = Formatters.formatFloat(time, 1);
//		numHitsLabel.setText(numHitsStr + (numHits == 1 ? " work found" : " works found") + " in " + timeStr + " seconds");
		buildTree();
	}

	/**	Sets the work parts for this panel.
	 *
	 *	@param	workparts	Hash set of work parts.
	 *
	 *	@throws	PersistenceException
	 */

	public void setWorks (HashSet workparts)
		throws PersistenceException
	{
		maxTreeHeight = tree.getHeight();
		this.workparts = workparts;
		int numHits = workparts.size();
		String numHitsStr = Formatters.formatIntegerWithCommas(numHits);
		numHitsLabel.setText(numHitsStr + (numHits == 1 ? " work" : " works"));
		buildTree();
	}

	/**	Gets the corpus associated with the work  in this window.
	 *
	 *	@return		The corpus associated with this window.
	 */

	public Corpus getCorpus (Work work) { return work.getCorpus();}

	/**	Adds nodes to the tree for this panel.
	 *
	 */

/*	protected void addNodes(Collection c) {
		SearchCriterion sc = null;
		SearchCriteriaTypedSet ts = null;
		for(Iterator it=c.iterator();it.hasNext();) {
			sc=(SearchCriterion)it.next();
			if(sc instanceof SearchCriteriaTypedSet) {
				addNodes(((SearchCriteriaTypedSet)sc).getCriteria());
			} else {
				String classNameKey = getCleanClassName(sc);
				DefaultMutableTreeNode parentNode = getNodeForType(classNameKey);
				if(parentNode==null) {
					ts = new SearchCriteriaTypedSet(classNameKey);
					criteria.put(classNameKey,ts);
					parentNode= new DefaultMutableTreeNode(ts);
					treeModel.insertNodeInto(parentNode, root, root.getChildCount());
				} else {
					ts = (SearchCriteriaTypedSet) parentNode.getUserObject();
				}
				if(!ts.contains((SearchCriterion)sc)) {
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(sc);
					treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
					try {ts.add((SearchCriterion)sc);} catch (SearchCriteriaClassMismatchException ex) {Err.err(ex);}
					tree.makeVisible(new TreePath(node.getPath()));
				} else
					{
					tree.expandPath(new TreePath(parentNode.getPath()));
				}
			}
		}
	}

*/
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
						workparts.add(sc);
						WorkPartTreeNode node = new WorkPartTreeNode(sc);
						treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
						tree.makeVisible(new TreePath(node.getPath()));
					}
				} else {
					for (Iterator git = newWorks.iterator(); git.hasNext(); ) {
						sc=(SearchCriterion)git.next();
						if(!workparts.contains(sc)) {
							workparts.add(sc);
							WorkPartTreeNode node = new WorkPartTreeNode(sc);
							treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
							tree.makeVisible(new TreePath(node.getPath()));
						}
					}
				}
			}
		} else {
			for (Iterator it = c.iterator(); it.hasNext(); ) {
				WorkPart workpart = (WorkPart)it.next();
				if(!workparts.contains(work)) { workparts.add(work);}
				WorkPartTreeNode node = new WorkPartTreeNode(work);
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

				if(o instanceof WorkPart) {
					if(workpartwrappers.contains(o)) {
						workpartwrappers.remove(o);
					}

					convertParentToWrapper((WorkPartTreeNode)node,(WorkPart)o);
					removeChildrenFromSet((WorkPartTreeNode)node,(WorkPart)o);
					deleteItemAndEmptyAncestors((WorkPartTreeNode)node,(WorkPart)o);

				} else if(o instanceof GroupingObject) {
					// remove it and its children
					treeModel.removeNodeFromParent(node);
					Map map = groupingOptions.group(workparts);
					Collection items = (Collection)map.get(o);
					workparts.removeAll((Collection)map.get(o));
				}
				paths = tree.getSelectionPaths();
			}
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + " exception: cut " + e.getMessage());
		}
	}

	/**	Builds or rebuilds the tree of hits.
	 *
	 *	@throws	PersistenceException
	 */

	private void buildTree ()
		throws PersistenceException
	{
		root = new WorkPartTreeNode();
		int groupBy = groupingOptions.getGroupBy();
		int orderBy = groupingOptions.getOrderBy();
		boolean grouping = (groupBy != GroupingWorkOptions.GROUP_BY_NONE);
		renderer.setRenderWorksWithDates(
			groupBy == GroupingWorkOptions.GROUP_BY_NONE &&
			orderBy == GroupingWorkOptions.ORDER_BY_DATE);
		if(workparts==null) {workparts = new HashSet();}
		int numHits = workparts.size();
		if (grouping) {
			Map map = groupingOptions.group(workparts);
			Set groups = map.keySet();
			for (Iterator it = groups.iterator(); it.hasNext(); ) {
				GroupingObject obj = (GroupingObject)it.next();
				WorkPartTreeNode node = new WorkPartTreeNode(obj);
				ArrayList list = (ArrayList)map.get(obj);
				addWorks(node, list);
				root.add(node);
			}
		} else {
			addWorks(root, groupingOptions.sort(workparts));
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
/*		setDividerLocation(0.45);
		int fullHeight = numRows * cellHeight;
		if (fullHeight <= maxTreeHeight) {
			int dividerLocation = getDividerLocation();
			setDividerLocation(dividerLocation -
				(maxTreeHeight-fullHeight) + 10);
			setResizeWeight(0.0);
		} else {
			setResizeWeight(0.45);
		}
		*/
	/*
		int fullHeight = numRows * cellHeight;
		if (fullHeight <= maxTreeHeight) {
			int dividerLocation = getDividerLocation();
			setDividerLocation(dividerLocation -
				(maxTreeHeight-fullHeight) + 10);
			setResizeWeight(0.0);
		} else {
			setResizeWeight(0.45);
		}


		*/

	}

	/**	Adds a collection of workparts to a node.
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
			WorkPartTreeNode wNode = new WorkPartTreeNode(work);
			node.add(wNode);
			addWorkParts(wNode,((WorkPart)work).getChildren());
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
			WorkPartTreeNode wNode = new WorkPartTreeNode(workpart);
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
	 *	@throws	PersistenceException
	 */

	public void handleNewGroupingOptions (GroupingWorkOptions options)
		throws PersistenceException
	{
	//	if (options.equals(groupingOptions)) return;
		groupingOptions = options;
		if (workparts != null) buildTree();
	}

	/**	Gets the work panel. */

	public WorkPanel getWorkPanel () {
		return workPanel;
	}

	/**	Shows the selected works from the hit list.
	 *
	 *	@throws	PersistenceException
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
		if (!(obj instanceof WorkPart)) return;
		WorkPart workpart = (WorkPart)obj;
//		parentWindow.setCorpus(getCorpus(work));
		if (workpart == null || workpart == curHit) return;
		curHit = workpart;
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

	/**	Opens the selected work part.
	 *
	 *	@throws	PersistenceException
	 */

	private void openSelected ()
		throws PersistenceException
	{
		TreePath path = tree.getSelectionPath();
		if (path == null) return;
		TreeNode node = (TreeNode)path.getLastPathComponent();
		if (node == null) return;
		if(node instanceof WorkPartTreeNode) {
			WorkPart workPart = (WorkPart)((WorkPartTreeNode)node).getUserObject();
			if (workPart == null) return;
			new WorkWindow(workPart.getWork().getCorpus(), workPart, parentWindow);
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


/*
public class WorkTreeTransferActionListener implements ActionListener, PropertyChangeListener {
    private JComponent focusOwner = null;

    public WorkTreeTransferActionListener() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addPropertyChangeListener("permanentFocusOwner", this);
    }

    public void propertyChange(PropertyChangeEvent e) {
        Object o = e.getNewValue();
        if (o instanceof JComponent) {
            focusOwner = (JComponent)o;
        } else {
            focusOwner = null;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (focusOwner == null)
            return;
        String action = (String)e.getActionCommand();
        Action a = focusOwner.getActionMap().get(action);
        if (a != null) {
            a.actionPerformed(new ActionEvent(focusOwner,
                                              ActionEvent.ACTION_PERFORMED,
                                              null));
        }
    }
}	*/


	protected DefaultMutableTreeNode findNodeForItem(Object item) {
		DefaultMutableTreeNode notfound = null;
		/* for (Enumeration e = root.children() ; e.hasMoreElements() ;) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)e.nextElement();
			Object o = child.getUserObject();
			if(o instanceof GroupingObject && o.equals(group)) {return child;}
		} */
		return notfound;
	}

	protected void addChildrenFromSet(DefaultMutableTreeNode node, WorkPart wpart) {
		for (Iterator it = workparts.iterator(); it.hasNext(); ) {
			WorkPart currentSetWorkPart = (WorkPart)it.next();
			WorkPart wparent = currentSetWorkPart.getParent();
			if(wparent!=null && wparent.equals(wpart)) {
				if(workpartwrappers.contains(currentSetWorkPart)) {
					WorkPartTreeNode nnode = new WorkPartTreeNode(currentSetWorkPart);
					nnode.setIsWrapper(true);
					treeModel.insertNodeInto(nnode, node, node.getChildCount());
					tree.makeVisible(new TreePath(nnode.getPath()));
				} else {
					WorkPartTreeNode nnode = new WorkPartTreeNode(currentSetWorkPart);
					treeModel.insertNodeInto(nnode, node, node.getChildCount());
					tree.makeVisible(new TreePath(nnode.getPath()));
				}
			}
		}
	}

	protected void removeChildrenFromSet(WorkPartTreeNode node, WorkPart wpart) {

		 for (Enumeration e = node.children(); e.hasMoreElements() ;) {
			WorkPartTreeNode cn = (WorkPartTreeNode)e.nextElement();
			WorkPart workpart = (WorkPart)cn.getUserObject();
			if(workpartwrappers.contains(workpart)) {
					workpartwrappers.remove(workpart);
			}

			if(workparts.contains(workpart)) {
					workparts.remove(workpart);
			}

			if(cn.getChildCount()>0) removeChildrenFromSet(cn,workpart);
			treeModel.removeNodeFromParent(cn);
		}
	}

	protected void deleteItemAndEmptyAncestors(WorkPartTreeNode node, WorkPart wpart) {
		TreeNode parentNode = node.getParent();
		treeModel.removeNodeFromParent(node);
		if(workparts.contains(wpart)) workparts.remove(wpart);

		if((parentNode instanceof WorkPartTreeNode) && (parentNode.getChildCount()==0)) {
			WorkPart wp = (WorkPart)((WorkPartTreeNode)parentNode).getUserObject();
			if(workpartwrappers.contains(wp)) {
				workpartwrappers.remove(wp);
			}
			if(workparts.contains(wp)) {
				workparts.remove(wp);
			}
			if(nodeMap.containsKey(wp)) {
				nodeMap.remove(wp);
			}
			deleteItemAndEmptyAncestors((WorkPartTreeNode)parentNode,wp);
		}
	}


	protected void convertParentToWrapper(WorkPartTreeNode node, WorkPart wpart) {
		TreeNode parentNode = node.getParent();
		if(parentNode instanceof WorkPartTreeNode) {
			WorkPart wp = (WorkPart)((WorkPartTreeNode)parentNode).getUserObject();
			if(!workparts.contains(wp)) workparts.add(wp);
			if(!workpartwrappers.contains(wp)) workpartwrappers.add(wp);
			((WorkPartTreeNode)parentNode).setIsWrapper(true);
			convertParentToWrapper((WorkPartTreeNode)parentNode,wp);
		}
	}

	protected int getWorkPartIndex(DefaultMutableTreeNode parent, DefaultMutableTreeNode child) {
		int i = 0;
		int childOrdinal = ((WorkPart)child.getUserObject()).getWorkOrdinal();
		for(i=0;i<parent.getChildCount();i++) {
			WorkPart wp = (WorkPart)((DefaultMutableTreeNode)parent.getChildAt(i)).getUserObject();
			if(childOrdinal < wp.getWorkOrdinal()) break;
		}
		return i;
	}

	protected void loadWorkSetTree(Collection worksetCollection) {
		for (Iterator it = worksetCollection.iterator(); it.hasNext(); ) {
			Object o = it.next();
			if(root ==null) {
				root = new DefaultMutableTreeNode();
				treeModel = new DefaultTreeModel(root);
				tree.setModel(treeModel);
			}
			DefaultMutableTreeNode parentNode = root;

			if((o instanceof Work) && !workparts.contains(o)) {
					workparts.add(o);
					WorkPartTreeNode node = new WorkPartTreeNode(o); // should be special wrapper node
					treeModel.insertNodeInto(node, parentNode, parentNode.getChildCount());
					addWorkParts(node,((Work)o).getChildren());
					tree.makeVisible(new TreePath(node.getPath()));
			} else if((o instanceof WorkPart)  && !workparts.contains(o)) {

				// Get its ancestors in order
				Stack ancestors = new Stack();
				WorkPart currentAncestor = ((WorkPart)o).getParent();
				while(currentAncestor!=null) {
					ancestors.push(currentAncestor);
					if(currentAncestor instanceof Work) {
						currentAncestor=null;
					} else {
						currentAncestor = currentAncestor.getParent();
					}
				}

				boolean isPresent = false;

				// Let's see if we already have the ancestors we need. If not create wrappers and attach. Let's start at highest level.

				while(!ancestors.empty()) {
					currentAncestor = (WorkPart) ancestors.pop();
					if(workparts.contains(currentAncestor) && !workpartwrappers.contains(currentAncestor)) {
						// already have it for real. So also have any children including the dropped node - we're done
						isPresent=true;
						break;
					} else if(workpartwrappers.contains(currentAncestor)) { // might have it, keep looking
						parentNode = findNodeForObject(currentAncestor);
						continue;
					} else { // don't have it yet
						// Add a wrapper level
						workparts.add(currentAncestor);
						workpartwrappers.add(currentAncestor);
						WorkPartTreeNode node = new WorkPartTreeNode(currentAncestor); // should be special wrapper node

						node.setIsWrapper(true);
						nodeMap.put(currentAncestor,node);
						treeModel.insertNodeInto(node, parentNode, getWorkPartIndex(parentNode,node));
						tree.makeVisible(new TreePath(node.getPath()));
						parentNode=node;
					}
				}
				if(!isPresent) {
					workparts.add(o);
					WorkPartTreeNode node = new WorkPartTreeNode(o); // should be special wrapper node
					treeModel.insertNodeInto(node, parentNode,  getWorkPartIndex(parentNode,node));

					if(parentNode instanceof WorkPartTreeNode) checkCompleteness(o,(WorkPartTreeNode)parentNode);
					if(o instanceof WorkPart) {
						WorkPart aParent = ((WorkPart)o).getParent();
						if(aParent!=null && (aParent instanceof WorkPart)) {
							if((parentNode.getChildCount()==aParent.getNumChildren()) && ((WorkPartTreeNode)parentNode).isWrapper()) {
								workpartwrappers.remove(aParent);
								((WorkPartTreeNode)parentNode).setIsWrapper(false);
							}
						}
					}
					nodeMap.put(o,node);
					addWorkParts(node,((WorkPart)o).getChildren());
					tree.makeVisible(new TreePath(node.getPath()));
				}
			}
		}
	}



	protected void checkCompleteness(Object o, WorkPartTreeNode pnode) {
		if(o instanceof WorkPart) {
			WorkPart aParent = ((WorkPart)o).getParent();
			if(aParent!=null && (aParent instanceof WorkPart)) {
				if((pnode.getChildCount()==aParent.getNumChildren()) && ((WorkPartTreeNode)pnode).isWrapper()) {
					workpartwrappers.remove(aParent);
					((WorkPartTreeNode)pnode).setIsWrapper(false);
				}
				TreeNode ppnode = pnode.getParent();
				if(ppnode instanceof WorkPartTreeNode) checkCompleteness(aParent,(WorkPartTreeNode)ppnode);
			}
		}
	}

  /** DropTargetListener interface method - What we do when drag is released */
  public void drop(DropTargetDropEvent e) {
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
			alist.add(o);
		}
		loadWorkSetTree(alist);
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

	protected String getCleanClassName(Object o) {
		String className = o.getClass().getName();
		if(className.lastIndexOf(".")!=-1) {className=className.substring(className.lastIndexOf(".")+1);}
		if(className.indexOf("$")!=-1) {className=className.substring(0,className.indexOf("$"));}
		return className;
	}

	public DefaultMutableTreeNode findNodeForObject(WorkPart o) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
		DefaultMutableTreeNode node = null;

		for (Enumeration e = root.breadthFirstEnumeration(); e.hasMoreElements();)
		{
			DefaultMutableTreeNode current = (DefaultMutableTreeNode)e.nextElement();
			WorkPart wp = (WorkPart)current.getUserObject();
			if (o.equals(wp))
			{
				node = current;
				break;
			}
		}

		return node;
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

