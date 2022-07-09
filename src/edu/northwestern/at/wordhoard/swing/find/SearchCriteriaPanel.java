package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.plaf.TreeUI;
import java.beans.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.querytool.*;
import edu.northwestern.at.utils.db.*;

/**	A search results panel.
 */

public class SearchCriteriaPanel extends JPanel implements ActionListener, PropertyChangeListener, DropTargetListener, ClipboardOwner  {

	/**	The constraint bag. */

	private ArrayList criteriaGroups;


	private HashMap criteria;

	/**	The tree. */

	private JTree tree;

	/**	The tree's model */

	DefaultTreeModel treeModel;

	/**	The tree's root. */

	private	DefaultMutableTreeNode root;

	/**	Max tree height. */

	private int maxTreeHeight;

	/**	Drag icon. */
//	private static ImageIcon dragicon = null;

	/**	Parent window. */

	private	AbstractWindow parentWindow = null;

	/**	Tree cell renderer. */

	private SearchCriteriaTreeCellRenderer renderer;

	/**	Relationship combobox */

	private JComboBox relationshipCombo;

	/**	Target combobox - looking for wordform or lemmata */

	private JComboBox targetCombo;

	/**	Search Button - enable/disable per criteria exist */

	private JButton searchButton;

	/**	Tree cell height. */

	private int cellHeight;

	/**	focue Owner for Edit Menu handling. */
    private JComponent focusOwner = null;

	/** Transfer Handler for cut/copy/past and DragNDrop */

	private	SearchCriteriaTransferHandler transferHandler = null;

	/** file handle for when opening a new WorkBag */
	private	File file = null;

	/** DataFlavor for DnD */
	private	DataFlavor xferFlavor;

	/**	Creates a new search results panel.
	 *
	 *	@param	parentWindow		Parent window.
	 */

	public SearchCriteriaPanel (AbstractWindow parentWindow)
	{
		super();
		this.parentWindow = parentWindow;

		transferHandler = new SearchCriteriaTransferHandler();
		criteria = new HashMap();

		Font font = new JLabel().getFont();
		font = new Font(font.getName(), font.getStyle(), 9);

		// Allow multiple selections of visible nodes
		tree = new JTree(new Object[0]) {
			public void setUI(TreeUI newUI) {
				super.setUI(newUI);
				TransferHandler handler = getTransferHandler();
				setTransferHandler(null);
				setTransferHandler(handler);
			}
		};

		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		tree.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		tree.setToggleClickCount(1000);
		tree.setTransferHandler(transferHandler);
        tree.setDragEnabled(true);

		SearchCriteriaDragMouseHandler mh = new SearchCriteriaDragMouseHandler();
		tree.addMouseListener(mh);
		tree.addMouseMotionListener(mh);

		root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);
		tree.setModel(treeModel);
		tree.setSelectionRow(0);

		font = tree.getFont();
		font = new Font(font.getName(), font.getStyle(), 10);



		JScrollPane scrollPane = new JScrollPane();
		MyViewport viewport = new MyViewport();
		viewport.setView(tree);
		scrollPane.setViewport(viewport);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

		renderer = new SearchCriteriaTreeCellRenderer(font, scrollPane);
		cellHeight = renderer.getHeight();
		tree.setRowHeight(cellHeight);
		tree.setCellRenderer(renderer);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(scrollPane);
//		DocFreqRangePanel freqPanel = new DocFreqRangePanel();
//		freqPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
//		freqPanel.setBorder(BorderFactory.createLineBorder(Color.black));
//		add(freqPanel);
//		JPanel scp = JPanel();
//		DocFreqRangePanel freqPanel = new DocFreqRangePanel();

		setBorder(BorderFactory.createEmptyBorder(3,0,10,0));

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				if(tree.getSelectionCount()==1) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
					if (node == null) return;

					Object o = node.getUserObject();
					if(o instanceof SearchCriteriaTypedSet && ((SearchCriteriaTypedSet)o).getSearchCriterionClassname().equals("Work")) {
						if(relationshipCombo!=null) {
							relationshipCombo.setSelectedItem(((SearchCriteriaTypedSet)o).getBoolRelationship());
							relationshipCombo.setEnabled(true);
						}
					} else {
						if(relationshipCombo!=null) {relationshipCombo.setEnabled(false);}
					}
				}
			}
		});

//		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
  //      manager.addPropertyChangeListener("permanentFocusOwner", this);


		tree.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e)
			{
				if	(	( e.getID() == KeyEvent.KEY_RELEASED ) &&
						( ( e.getKeyCode() == KeyEvent.VK_BACK_SPACE ) ||
						  ( e.getKeyCode() == KeyEvent.VK_DELETE ) ) )
				{
					deleteSelectedItems();
				}
			}
		});

/*		tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, InputEvent.CTRL_MASK | InputEvent.ALT_MASK), "deletekey");
		tree.getActionMap().put("deletekey", new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
					String action = (String)evt.getActionCommand();
					System.out.println("SearchCriteriaPanel: actionPerformed - " + action);
        }
	});
	*/
		String localType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.querytool.SearchCriteriaTransferData";
		try {
			xferFlavor = new DataFlavor(localType);
		} catch (ClassNotFoundException e) {
			System.out.println("SearchCriteriaTransferHandler: unable to create data flavor, " + e.getMessage());
		}
		DropTarget dropTarget = new DropTarget(tree, this);
	}


	/**	set the relationship inspector
	 * @param	relationshipCombo	The combo box for selecting the relationship of one search criterion to another.
	 *
	 */

	public void setBoolRelationshipCombo(JComboBox relationshipCombo) {
		this.relationshipCombo = relationshipCombo;
	}

	public void setTargetCombo(JComboBox targetCombo) {
		this.targetCombo = targetCombo;
	}

	public void setSearchButton(JButton b) {
		this.searchButton = b;
	}


	/**	set the relationship inspector
	 * @param	relationship	The relationship of one search criterion to another.
	 *
	 */

	public void setBoolRelationship(String relationship) {
		if(tree.getSelectionCount()==1) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node == null) return;
			Object o = node.getUserObject();
			if(o instanceof SearchCriteriaTypedSet) {((SearchCriteriaTypedSet)o).setBoolRelationship(relationship);}
		}
	}


	/**	cut action - remove selected items from work bag and copy to pastebuffer
	 *
	 */

	 protected void deleteSelectedItems() {
		try {
			TreePath[] paths = tree.getSelectionPaths();
			if (paths == null || paths.length == 0) {
				return;
			}

			for (int i = 0; i < paths.length; i++) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
				Object o = node.getUserObject();
				if (o instanceof SearchCriteriaTypedSet) {
					treeModel.removeNodeFromParent(node);
					criteria.remove(((SearchCriteriaTypedSet)o).getSearchCriterionClassname());
				} else if(o instanceof GroupingObject || o instanceof SearchCriterion) {
					// remove it from its parent
					treeModel.removeNodeFromParent(node);
					String classNameKey = getCleanClassName((SearchCriterion)o);
					DefaultMutableTreeNode parentNode = getNodeForType(classNameKey);
					SearchCriteriaTypedSet ts = (SearchCriteriaTypedSet) criteria.get(classNameKey);
					ts.remove((SearchCriterion)o);
					if(parentNode.getChildCount()==0) {
						treeModel.removeNodeFromParent(parentNode);
						criteria.remove(classNameKey);
					}

				} else {
					System.out.println(this.getClass().getName() + " deleteSelectedItems: can't cut " + o.getClass().getName());
				}
			}
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + " exception: cut " + e.getMessage());
		}
		if(root.getChildCount()==0) searchButton.setEnabled(false);
	}

	// methods for handling Edit menu
    public void propertyChange(PropertyChangeEvent e) {
        Object o = e.getNewValue();
        if (o instanceof JComponent) {
            focusOwner = (JComponent)o;
        } else {
            focusOwner = null;
        }
    }


    public void actionPerformed(ActionEvent e) {
 //       if (focusOwner == null)
 //           return;
//		try {
			String action = (String)e.getActionCommand();
				Object src = e.getSource();
				if(src instanceof JComboBox) {
					String item = (String) ((JComboBox)src).getSelectedItem();
					setBoolRelationship(item);
				}
			if(action!=null) {
				if(action.equals("cut")) {cut();}
				else if (action.equals("copy")) {copy();}
				else if (action.equals("paste")) {paste();}
				else if (action.equals("quit")) {WordHoard.quit();}
			}
			//	else if (action.equals("save")) {save();}
			//	else if (action.equals("open")) {open();}
//		} catch (PersistenceException ex) {Err.err(ex);}
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
/*		HashSet cs = new HashSet();
		try {
			TreePath[] paths = tree.getSelectionPaths();
			if (paths == null || paths.length == 0) {
				return;
			}

			for (int i = 0; i < paths.length; i++) {

				Object o = ((DefaultMutableTreeNode)paths[i].getLastPathComponent()).getUserObject();
				if (o instanceof GroupingObject) {
					cs.add((GroupingObject)o);
				}
			}

			criteria.removeAll(cs);
			Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable t = transferHandler.createTransferable(tree);
			sysClip.setContents(t,this);
			buildTree(null);
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + " exception: cut " + e.getMessage());
		}
*/
	}

	/**	copy action - copy selected items from work bag to pastebuffer
	 *
	 */
	protected void copy() {
/*		HashSet cs = new HashSet();
		try {
			TreePath[] paths = tree.getSelectionPaths();
			if (paths == null || paths.length == 0) {
				return;
			}
			for (int i = 0; i < paths.length; i++) {
				Object o = ((DefaultMutableTreeNode)paths[i].getLastPathComponent()).getUserObject();
				if (o instanceof GroupingObject) {
					cs.add((GroupingObject)o);
				}
			}

			Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable t = transferHandler.createTransferable(tree);
			sysClip.setContents(t,this);
		} catch (Exception e) {Err.err(e);}
        return; */
	}

	/**	paste action - copy selected items from pastebufferto work bag
	 *
	 */
	protected void paste() {
/*			try {
				Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
				Transferable t = sysClip.getContents(this);
				DataFlavor df = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";interface=GroupingObject");
				if(t.isDataFlavorSupported(df)) {
					GroupingObject sc = (GroupingObject) sysClip.getContents(null).getTransferData(df);
					criteria.add(sc);
				}
			} catch (Exception e) {Err.err(e);}
			try {
				buildTree(null);
			} catch (PersistenceException e) {Err.err(e);}
*/
	}



	/**	Open from file.
	 *
	 *	@param	works		The list of works
	 *
	 */

/*
	 public void open() {
		JFileChooser fc = new JFileChooser();
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
											new SearchWorkResultsWindow(file, (AbstractWindow) parentWindow);
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

	}
*/
	/** save action -
	 *
	 *
	 */

/*
	 protected void save() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
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
		if(works==null) {works = new java.util.ArrayList();}

		sb.append("<?xml version=\"1.0\"?>\n");
		sb.append("<works>\n");
		for (Iterator it = works.iterator(); it.hasNext(); ) {
			Work work = (Work)it.next();
			sb.append("<work id=\"" + work.getId() + "\">\n");
			for (Iterator ait = work.getAuthors().iterator(); ait.hasNext(); ) {
				Author ath= (Author)ait.next();
				sb.append("<author>" + ath.getName() + "</author>\n");
			}
			sb.append("<title>" + work.getTitle() + "</title>\n");
			sb.append("<earlyDate>" + work.getEarlyDate() + "</earlyDate>\n");
			sb.append("<lateDate>" + work.getLateDate() + "</lateDate>\n");
			sb.append("</work>\n");
		}

		sb.append("</works>\n");
		return sb.toString();
	}
*/

	/**	new action
	 *
	 */
/*
	protected void newWindow() {
		try {
			new SearchWorkResultsWindow((AbstractWindow)parentWindow);
		} catch (PersistenceException e) {Err.err(e);}
	}
*/
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

	/**	Sets the criteria for this panel.
	 *
	 *	@param	criteria		The list of criteria
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public void setCriteria (SearchCriteria criteria) throws PersistenceException
	{
//		maxTreeHeight = tree.getHeight();
//		this.constraints = constraints;
//		buildTree(null);
	}

	/**	Builds or rebuilds the tree of constraints.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	private void buildTree (DefaultMutableTreeNode root) throws PersistenceException
	{
		if(root==null) root = new DefaultMutableTreeNode();

		for (Iterator it = criteria.values().iterator(); it.hasNext(); ) {
			Object obj = it.next();
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(obj);
			addItems(node, (SearchCriteriaTypedSet) obj);
			root.add(node);
		}

		DefaultTreeModel model = new DefaultTreeModel(root);
		tree.setModel(model);
		tree.setSelectionRow(0);
	}

// TEST

/*

public DefaultMutableTreeNode addObject(Object child) {
    DefaultMutableTreeNode parentNode = null;
    TreePath parentPath = tree.getSelectionPath();

    if (parentPath == null) {
        parentNode = rootNode;
    } else {
        parentNode = (DefaultMutableTreeNode)
                     (parentPath.getLastPathComponent());
    }

    return addObject(parentNode, child, true);
}

public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                        Object child,
                                        boolean shouldBeVisible) {
    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
    treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

    //Make sure the user can see the lovely new node.
    if (shouldBeVisible) {
        tree.scrollPathToVisible(new TreePath(childNode.getPath()));
    }
    return childNode;
}
*/
// TEST

// TEST

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


	private DefaultMutableTreeNode getSelectedNode() {
		DefaultMutableTreeNode selectedNode = null;
		TreePath path = tree.getSelectionPath();
		if (path != null) selectedNode = (DefaultMutableTreeNode)path.getLastPathComponent();
		return selectedNode;
	}


	public void addCriterion(SearchCriterion sc) {
		SearchCriteriaTypedSet ts = null;
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


	protected void addNodes(Collection c) {
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
  /** DropTargetListener interface method - What we do when drag is released */
  public void drop(DropTargetDropEvent e) {
	SearchCriteriaTransferData data = null;
    try {
      Transferable tr = e.getTransferable();

      //flavor not supported, reject drop
        if (!haslocalFlavor(tr.getTransferDataFlavors()))  {System.out.println("Can't import:" + tr.toString()); e.rejectDrop(); }

      //cast into appropriate data type
		data =  (SearchCriteriaTransferData) tr.getTransferData(xferFlavor );
		addNodes((Collection)data);
		searchButton.setEnabled(true);
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

	/**	Adds a set of items to a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	items		set of items.
	 */

	private void addItems (DefaultMutableTreeNode node, SearchCriteriaTypedSet items)
	{
		if(items!=null) {
			for (Iterator it = items.getCriteria().iterator(); it.hasNext(); ) {
				Object o = it.next();
				DefaultMutableTreeNode wNode =  new DefaultMutableTreeNode(o, false);
				node.add(wNode);
			}
		}
	}

		public Collection getCriteria() {return criteria.values();}


	/**	Shows the selected works from the hit list.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 *
	 */

	private void showSelected ()  {
/*		workPanel.requestFocus();
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
									workPanel.setPart(pm.getWorkPartById(curHit.getId()));
								} catch (PersistenceException e) {Err.err(e);}
							}
						}
					);
				}
			}
		).start(); */
	}


		public void lostOwnership(Clipboard clipboard, Transferable contents) {
//				System.out.println(this.getClass().getName() + ": lostOwnership");
		}


	protected DefaultMutableTreeNode getNodeForType(String classname) {
		DefaultMutableTreeNode notfound = null;
		for (Enumeration e = root.children() ; e.hasMoreElements() ;) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)e.nextElement();
			Object o = child.getUserObject();
			if(o instanceof SearchCriteriaTypedSet) {
				String c = ((SearchCriteriaTypedSet)o).getSearchCriterionClassname();
				if(c.equals(classname)) {return child;}
			}
		}
		return notfound;
	}

	protected String getCleanClassName(Object o) {
		String className = o.getClass().getName();
		if(className.lastIndexOf(".")!=-1) {className=className.substring(className.lastIndexOf(".")+1);}
		if(className.indexOf("$")!=-1) {className=className.substring(0,className.indexOf("$"));}
		return className;
	}


		/**	TransferHandler for drag and drop
		 *
		 *
		 */

		public class SearchCriteriaTransferHandler extends TransferHandler {
			DataFlavor xferFlavor;
			String localType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.querytool.SearchCriteriaTransferData";
			JTree source = null;


			public SearchCriteriaTransferHandler() {
				try {
					xferFlavor = new DataFlavor(localType);
				} catch (ClassNotFoundException e) {
					System.out.println("SearchCriteriaTransferHandler: unable to create data flavor, " + e.getMessage());
				}
			}

			public SearchCriteriaTransferHandler(String property) {
				try {
					xferFlavor = new DataFlavor(localType);
				} catch (ClassNotFoundException e) {
					System.out.println("SearchCriteriaTransferHandler: unable to create data flavor");
				}
			}

			public boolean importData(JComponent c, Transferable t) {
				SearchCriteriaTransferData data = null;
				if (!canImport(c, t.getTransferDataFlavors())) {System.out.println("Can't import:" + t.toString()); return false;}

				try {
					if (haslocalFlavor(t.getTransferDataFlavors())) {
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

				try {
					SearchCriteriaTypedSet ts = null;
					SearchCriterion sc = null;
					Iterator it = ((SearchCriteriaTransferData)data).iterator();
					if(it.hasNext()) {sc=(SearchCriterion)it.next();}

					if(sc!=null) {
						String classNameKey = getCleanClassName(sc);
						ts = (SearchCriteriaTypedSet) criteria.get(classNameKey);
						if(ts==null) {
							ts = new SearchCriteriaTypedSet(classNameKey);
							criteria.put(classNameKey,ts);
						}

						for (Iterator si = ((SearchCriteriaTransferData)data).iterator(); si.hasNext(); ) {
							Object o = si.next();
							if(o instanceof SearchCriterion) ts.add((SearchCriterion)o);
						}
						buildTree(null);
					}
				} catch (SearchCriteriaClassMismatchException e) {Err.err(e);
				} catch (PersistenceException e) {Err.err(e);}

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

		public boolean canImport(JComponent c, DataFlavor[] flavors) {
			if (haslocalFlavor(flavors))  {return true; }
			 return false;
		}

		protected Transferable createTransferable(JComponent c) {
			if (c instanceof JTree) {
				source = (JTree)c;
				TreePath[] paths = source.getSelectionPaths();
				if (paths == null || paths.length == 0) {
					return null;
				}
				SearchCriteriaTransferData cs = new SearchCriteriaTransferData();
				for (int i = 0; i < paths.length; i++) {
				  Object o = ((DefaultMutableTreeNode)paths[i].getLastPathComponent()).getUserObject();
					if (o instanceof SearchCriterion) {
					//	go=(GroupingObject)o;
						cs.add((SearchCriterion)o);
					}
				}
				return new SearchCriteriaTransferable(cs);
			}
			return null;
		}

		public int getSourceActions(JComponent c) {
			return MOVE;
		}

	/*	public Icon getVisualRepresentation(Transferable t) {
				System.out.println("getVisualRepresentation");
			if(dragicon==null) {
				dragicon = Images.get("icon.gif");
				System.out.println("got dragicon " + dragicon.getIconWidth() + " x " + dragicon.getIconHeight());
			}
			return dragicon;
		}*/
	}

public class SearchCriteriaDragMouseHandler implements MouseListener, MouseMotionListener {
	int mouseButtonDown = MouseEvent.NOBUTTON;
	long mouseButtonDownSince = 0;
	MouseEvent firstMouseEvent = null;

	public void mousePressed(MouseEvent e) {
		firstMouseEvent = e;
	}
	public void mouseReleased(MouseEvent e)  {
		mouseButtonDown = MouseEvent.NOBUTTON;

		DefaultMutableTreeNode node = getSelectedNode();
		if(node!=null) {
			Object o = node.getUserObject();
			if(o instanceof SearchCriteriaTypedSet) {
				if(relationshipCombo!=null) {
					String rel = ((SearchCriteriaTypedSet)o).getBoolRelationship();
					if(e.isControlDown()) {
						if(rel.equals("any")) rel="all";
						else if(rel.equals("all")) rel="none";
						else if(rel.equals("none")) rel="any";
						((SearchCriteriaTypedSet)o).setBoolRelationship(rel);
						tree.repaint();
					}
					relationshipCombo.setSelectedItem(((SearchCriteriaTypedSet)o).getBoolRelationship());
					relationshipCombo.setEnabled(((SearchCriteriaTypedSet)o).getSearchCriterionClassname().equals("Work"));
				}
			} else {
				if(relationshipCombo!=null) {relationshipCombo.setEnabled(false);}
			}
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

