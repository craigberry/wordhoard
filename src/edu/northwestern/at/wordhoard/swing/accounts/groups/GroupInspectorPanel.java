package edu.northwestern.at.wordhoard.swing.accounts.groups;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.util.*;
import java.io.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

import edu.northwestern.at.wordhoard.model.userdata.UserGroup;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.UserGroupUtils;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.accounts.*;
import edu.northwestern.at.utils.*;

/**	The right panel of the manage groups window.
 */
 
public class GroupInspectorPanel extends JPanel implements DropTargetListener {

	/**	Group model. */
	
	private GroupModel model;
	
	/**	Group being edited, or null if a new group is being created. */
	
	private UserGroup group;
	
	/**	List of members. */
	
	private SortedArrayList members;

	/**	group title field. */
	
	private JTextField titleField = new JTextField(20);
	
	/**	owner field. */
	
	private JTextField ownerField = new JTextField(20);
	
	/** Save button (Create or Update). */
	
	private JButton saveButton;

	/** Members table. */
	
	private GroupMembersTable membersTable;

	/** Members table model. */
	
	private GroupInspectorTableModel membersTableModel;
	
	/**	Dialog panel. */
	
	private DialogPanel dlog = new DialogPanel();

	private DataFlavor xferFlavor;

	/**	Creates a new right panel.
	 *
	 *	@param	model		Group model.
	 */
	 
	public GroupInspectorPanel (final GroupModel model) {
	
		this.model = model;

		membersTableModel = new GroupInspectorTableModel();
		membersTable = new GroupMembersTable(membersTableModel);

		membersTable.setTransferHandler(new UserTransferHandler());
//        membersTable.setDragEnabled(true);

		String xferType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.accounts.UserTransferData";
		try {
			xferFlavor = new DataFlavor(xferType);
 		} catch (Exception e) {Err.err(e);}
		
		JScrollPane scrollPane = new JScrollPane(membersTable,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.getViewport().setBackground(Color.white);
		JScrollBar hBar = scrollPane.getHorizontalScrollBar();
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		int hBarHeight = hBar.getPreferredSize().height;
		int vBarWidth = vBar.getPreferredSize().width;

		Dimension d = membersTable.getPreferredSize();
		d.width += vBarWidth + 20;
		d.height += hBarHeight + 40;
		d.height = 500;
		scrollPane.setPreferredSize(d);
		
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel mp = new JPanel();
		mp.setLayout(new BoxLayout(mp, BoxLayout.Y_AXIS));
		mp.add(Box.createHorizontalGlue());
		mp.setAlignmentX(Component.LEFT_ALIGNMENT);
//		mp.setBorder(new BevelBorder(BevelBorder.LOWERED));

		TitledBorder tb = new TitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), "Selected Group", TitledBorder.CENTER, TitledBorder.ABOVE_TOP);
		mp.setBorder(tb);


		JPanel tp = new JPanel();
		tp.setLayout(new BoxLayout(tp, BoxLayout.X_AXIS));
		tp.setAlignmentX(Component.LEFT_ALIGNMENT);
		tp.add(new JLabel("Name: "));
		tp.add(titleField);
		titleField.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						save();
					} catch (Exception e) {
						model.err(e);
					}
				}
			}
		);

		mp.add(tp);
		mp.add(Box.createVerticalStrut(10));
		mp.add(scrollPane);
		mp.add(Box.createVerticalStrut(2));
		mp.add(new JLabel("Drag users from \"All Users\" to add members."));
	
		dlog.add(mp);
		
		saveButton = dlog.addButton("Save",
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						save();
					} catch (Exception e) {
						model.err(e);
					}
				}
			}
		);
		saveButton.setEnabled(false);
		saveButton.setVisible(false);

		dlog.setMaximumSize(dlog.getPreferredSize());
	
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(dlog);

		model.addListener(
			new GroupAdapter() {
				public void selectionChanged (int[] selection) {
					handleSelectionChanged();
				}
			}
		);
		
		DocumentListener docListener =
			new DocumentListener() {
				public void changedUpdate (DocumentEvent event) {
					adjustSaveButton();
				}
				public void insertUpdate (DocumentEvent event) {
					adjustSaveButton();
				}
				public void removeUpdate (DocumentEvent event) {
					adjustSaveButton();
				}
			};
			
		titleField.getDocument().addDocumentListener(docListener);
//		ownerField.getDocument().addDocumentListener(docListener);
		
		ActionListener actionListener =
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					adjustSaveButton();
				}
			};

		DropTarget dropTarget = new DropTarget(membersTable, this);

		membersTable.addKeyListener(new KeyListener() {
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
	}

	/**	remove selected items from members list
	 *
	 */

	 protected void deleteSelectedItems() {
		try {
			int[] rows = membersTable.getSelectedRows();
			if (rows.length < 0) return;
			for(int i=0;i<rows.length;i++) {
				String username = (String) membersTableModel.getValueAt(rows[i],0);
				group.removeMember(username);
			}
			UserGroupUtils.updateUserGroup(group);
			handleSelectionChanged();
		} catch (Exception e) {
			System.out.println(this.getClass().getName() + " exception: deleteSelectedItems " + e.getMessage());
		}

	}
	
	/**	Saves an group. 
	 *
	 *	@throws Exception	general error.
	 */
	
	private void save () 
		throws Exception
	{
		String name = titleField.getText();
//		String owner = ownerField.getText();
		String owner = WordHoardSettings.getUserID();
		model.save(name, owner);
	}
	
	/**	Handles a selection changed event. */
	
	private void handleSelectionChanged() {
		boolean creatingNewGroup = model.getCreatingNewGroup();
		group = model.getSelectedGroup();
		
		if (creatingNewGroup) {
			titleField.setText("");
			ownerField.setText("");
			saveButton.setText("Create");
			saveButton.setEnabled(false);
			dlog.setVisible(true);
			titleField.requestFocus();
			members = null;
//			membersTableModel.setVisible(false);
			membersTableModel.fireTableDataChanged();
		} else if (group == null) {
			dlog.setVisible(false);
		} else {
			titleField.setText(group.getTitle());
			ownerField.setText(group.getOwner());
			members = new SortedArrayList(group.getMembers());
			membersTableModel.fireTableDataChanged();
			saveButton.setText("Update");
			saveButton.setEnabled(false);
			dlog.setVisible(true);
		}
	}
	
	/**	Adjusts (enables/disables) the Save button.
	 */

	private void adjustSaveButton () {
		if (group == null) {
			saveButton.setEnabled(
				titleField.getText().length() > 0);
		} else {
			saveButton.setEnabled(
				!titleField.getText().equals(group.getTitle()) ||
				!ownerField.getText().equals(group.getOwner()));
		}
	}

	class GroupInspectorTableModel extends AbstractTableModel {

			
		/**	Gets the number of rows.
		 *
		 *	@return		The number of rows.
		 */
		
		public int getRowCount () {
			int count = (group==null || model.getCreatingNewGroup()) ? 0 : group.getMembers().size();
			return (count);
		}
		
		/**	Gets the number of columns.
		 *
		 *	@return		The number of columns.
		 */
		
		public int getColumnCount () {
			return 1;
		}
		
		/**	Gets the value of a cell.
		 *
		 *	@param	row		Row index.
		 *
		 *	@param	col		Column index.
		 *
		 *	@return		Value of cell.
		 */
		
		public Object getValueAt (int row, int col) {
			String member = (String)members.get(row);
			return member;
		}
		
		/**	Gets the name of a column.
		 *
		 *	@param	col		Column index.
		 *
		 *	@return		Name of column.
		 */
		
		public String getColumnName (int col) {
			return "Members";
 		}
		
	}

// support drag and drop

		/**	TransferHandler for drag and drop
		 *
		 *
		 */

		public class UserTransferHandler extends TransferHandler {
			public boolean importData(JComponent c, Transferable t) {

				UserTransferData data = null;
				if (!canImport(c, t.getTransferDataFlavors())) {System.out.println("Can't import:" + t.toString()); return false;}

				try {
					if (haslocalFlavor(t.getTransferDataFlavors())) {
						data = (UserTransferData)t.getTransferData(xferFlavor);
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

			//	try {
			/*
					String username;
					Iterator it = ((UserTransferData)data).iterator();
					ArrayList newmembers = new ArrayList();
					while(it.hasNext()) {
						username=(String)it.next();
						newmembers.add(username);
						System.out.println(getClass().getName() + " importData:" + username);
					}

					group.addMembers(newmembers);
					UserGroupUtils.updateUserGroup(group);
					handleSelectionChanged();
					*/
//				} catch (SearchCriteriaClassMismatchException e) {Err.err(e);
			//	} catch (PersistenceException e) {Err.err(e);}

				return true;
		}

		protected void exportDone(JComponent c, Transferable data, int action) {    }

		public boolean canImport(JComponent c, DataFlavor[] flavors) {
			boolean creatingNewGroup = model.getCreatingNewGroup();
			if (creatingNewGroup)  {return false; }
			if (haslocalFlavor(flavors))  {return true; }
			return false;
		}

		protected Transferable createTransferable(JComponent c) {
			return null;
		}

		public int getSourceActions(JComponent c) {
			return COPY_OR_MOVE;
		}
	}

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

// drop target

	  /** DropTargetListener interface method - What we do when drag is released */
	  public void drop(DropTargetDropEvent e) {
		
		try {
		  Transferable tr = (Transferable) e.getTransferable();

		  //flavor not supported, reject drop
			boolean creatingNewGroup = model.getCreatingNewGroup();
			if (creatingNewGroup || !haslocalFlavor(tr.getTransferDataFlavors()))  {e.rejectDrop(); return;}

		  //cast into appropriate data type

			UserTransferData data = (UserTransferData) tr.getTransferData(xferFlavor);
			String username;
			Iterator it = ((UserTransferData)data).iterator();
			ArrayList newmembers = new ArrayList();
			while(it.hasNext()) {
				username=(String)it.next();
				newmembers.add(username);
			}

			group.addMembers(newmembers);
			UserGroupUtils.updateUserGroup(group);
			handleSelectionChanged();

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
			e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE ) ;
	  }

	  public void dropActionChanged(DropTargetDragEvent e) {
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

