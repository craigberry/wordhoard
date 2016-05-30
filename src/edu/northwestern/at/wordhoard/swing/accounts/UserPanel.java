package edu.northwestern.at.wordhoard.swing.accounts;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;
import javax.swing.border.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.accounts.*;
import edu.northwestern.at.utils.swing.*;

/**	The user panel of the manage groups window.
 */
 
class UserPanel extends JPanel {

	final AccountModel model;
	
	/**	Creates a new left panel.
	 *
	 *	@param	model		User model.
	 */
	 
	UserPanel (final AccountModel model) {
		this.model=model;

		AccountTable table = new AccountTable(model);

		TableDragMouseHandler mh = new TableDragMouseHandler();
		table.addMouseListener(mh);
		table.addMouseMotionListener(mh);

		table.setTransferHandler(new UserTransferHandler());
        table.setDragEnabled(true);
		
		JScrollPane scrollPane = new JScrollPane(table,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.getViewport().setBackground(Color.white);
		JScrollBar hBar = scrollPane.getHorizontalScrollBar();
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		int hBarHeight = hBar.getPreferredSize().height;
		int vBarWidth = vBar.getPreferredSize().width;
		Dimension d = table.getPreferredSize();
		d.width += vBarWidth + 20;
		d.height += hBarHeight + 40;
		scrollPane.setPreferredSize(d);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);


	}

// support drag and drop

		public class TableDragMouseHandler implements MouseListener, MouseMotionListener {
			MouseEvent firstMouseEvent = null;

			public void mousePressed(MouseEvent e) {
				firstMouseEvent = e;
				e.consume();
			}
			public void mouseReleased(MouseEvent e)  {;}
			public void mouseClicked (MouseEvent event) {
			/*	try {
					handleMouseClicked(event);
				} catch (Exception e) {
					Err.err(e);
				} */
			}
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

	public class UserTransferHandler extends TransferHandler  {

			DataFlavor xferFlavor;
			String xferType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.accounts.UserTransferData";

			/**	Drag icon. */
			private ImageIcon dragicon = null;

			public UserTransferHandler() {
				try {
					xferFlavor = new DataFlavor(xferType);
				} catch (ClassNotFoundException e) {
					System.out.println("UserTransferHandler: unable to create data flavor");
				}
			}

			public UserTransferHandler(String property) {
				try {
					xferFlavor = new DataFlavor(xferType);
				} catch (ClassNotFoundException e) {
					System.out.println("UserTransferHandler: unable to create data flavor");
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
		
			UserTransferData gos = new UserTransferData();
			if (c instanceof JTable) {
				try {
					int[] rows = ((JTable)c).getSelectedRows();
					if (rows.length < 0) return null;
					for(int i=0;i<rows.length;i++) {
						String username = (String) model.getValueAt(rows[i],0);
						gos.add(username);
					}
					return new UserTransferable(gos);
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

