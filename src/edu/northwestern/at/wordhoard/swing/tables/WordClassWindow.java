package edu.northwestern.at.wordhoard.swing.tables;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import javax.swing.table.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.find.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.db.*;

import edu.northwestern.at.wordhoard.swing.querytool.*;

/**	The word classes window.
 */

public class WordClassWindow extends AbstractWindow {

	/**	The word classes window, or null if none is open. */

	private static WordClassWindow wordClassWindow;

	/**	Opens or brings to the front the word classes window.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PersistenceException
	 */

	public static void open (AbstractWindow parentWindow)
		throws PersistenceException
	{
		if (wordClassWindow == null) {
			wordClassWindow = new WordClassWindow(parentWindow);
		} else {
			wordClassWindow.setVisible(true);
			wordClassWindow.toFront();
		}
	}

	/**	Array of all the word classes. */

	private WordClass wordClassArray[];

	/**	Table model. */

	private class MyTableModel extends AbstractTableModel {

		public int getRowCount () {
			return wordClassArray.length;
		}

		public int getColumnCount () {
			return 3;
		}

		public Object getValueAt (int row, int col) {
			WordClass wordClass = wordClassArray[row];
			switch (col) {
				case 0: return wordClass.getTag();
				case 1: return wordClass.getDescription();
				case 2: return wordClass.getMajorWordClass();
			}
			return null;
		}

		public String getColumnName (int col) {
			switch (col) {
				case 0: return "Name";
				case 1: return "Description";
				case 2: return "Major Class";
			}
			return null;
		}

	}

	/**	Table cell renderer. */

	private class MyRenderer extends DefaultTableCellRenderer {
		Font font;
		public MyRenderer () {
			super();
			font = getFont();
			font = new Font(font.getName(), font.getStyle(), 10);
		}
		public Component getTableCellRendererComponent (JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int col)
		{
			super.getTableCellRendererComponent(table, value,
				isSelected, false, row, col);
			if (col == 0) {
				this.setHorizontalAlignment(SwingConstants.TRAILING);
				this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
			} else {
				this.setHorizontalAlignment(SwingConstants.LEADING);
				this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
			}
			this.setFont(font);
			return this;
		}
	}

	/**	Table. */

//	private JTable table;
	private XTable table;

	/**	Creates a new word classes window.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PersistenceException
	 */

	public WordClassWindow (AbstractWindow parentWindow)
		throws PersistenceException
	{

		super("Word Classes", parentWindow);
		wordClassArray = CachedCollections.getWordClasses();

		MyTableModel model = new MyTableModel();
		MyRenderer renderer = new MyRenderer();
//		table = new JTable(model);
		table = new XTable(model);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setRowSelectionInterval(0,0);
		table.setGridColor(Color.lightGray);
		table.setShowGrid(true);
		table.setRowHeight(18);

		SearchCriteriaDragMouseHandler mh = new SearchCriteriaDragMouseHandler();
		table.addMouseListener(mh);
		table.addMouseMotionListener(mh);
		table.setTransferHandler(new WordClassTransferHandler());
        table.setDragEnabled(true);

		for (int col = 0; col < model.getColumnCount(); col++) {
			TableColumn column = table.getColumnModel().getColumn(col);
			column.setCellRenderer(renderer);
			Component comp = renderer.getTableCellRendererComponent(
				table, model.getColumnName(col), false, false, 0, col);
			int maxWidth = comp.getPreferredSize().width;
			for (int row = 0; row < model.getRowCount(); row++) {
				comp = renderer.getTableCellRendererComponent(
					table, model.getValueAt(row,col), false, false, row, col);
				int width = comp.getPreferredSize().width;
				if (width > maxWidth) maxWidth = width;
			}
			column.setPreferredWidth(maxWidth + 10);
		}

//		JScrollPane scrollPane = new JScrollPane(
		XScrollPane scrollPane = new XScrollPane(
			table,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JScrollBar hBar = scrollPane.getHorizontalScrollBar();
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		int hBarHeight = hBar.getPreferredSize().height;
		int vBarWidth = vBar.getPreferredSize().width;
		Dimension d = table.getPreferredSize();
		d.width += vBarWidth + 20;
		d.height += hBarHeight + 40;
		scrollPane.setPreferredSize(d);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		JPanel linePanel = new JPanel();
		linePanel.setPreferredSize(new Dimension(1,0));
		linePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.add(linePanel, BorderLayout.SOUTH);
		panel.setBorder(BorderFactory.createEmptyBorder(
			0,0,WordHoardSettings.getGrowSlop(),0));

		setContentPane(panel);

		pack();
		Dimension screenSize = getToolkit().getScreenSize();
		Dimension windowSize = getSize();
		windowSize.height = screenSize.height -
			(WordHoardSettings.getTopSlop() + WordHoardSettings.getBotSlop());
		setSize(windowSize);
		setLocation(new Point(3, WordHoardSettings.getTopSlop()));
		if (parentWindow == null) {
			WindowPositioning.centerWindowOverWindow(this, null, 0);
		} else {
			positionNextTo(parentWindow);
		}
		setVisible(true);

	}

	/**	Handles window dispose events. */

	public void dispose () {
		wordClassWindow = null;
		super.dispose();
	}

	/**	Handles the "Find" command.
	 *
	 *	@throws	Exception
	 */

	public void handleFindWordsCmd ()
		throws Exception
	{
		SearchDefaults defaults = null;
		int row = table.getSelectedRow();
		if (row >= 0) defaults = wordClassArray[row];
		new FindWindow(getCorpus(), defaults, this);
	}

	/**	Find a printable component.
	 *
	 *	@return		A printable component.
	 */

	protected Component findPrintableComponent () {
		return table;
	}

	/**	Find a saveable component.
	 *
	 *	@return		A saveable component.  Null=none by default.
	 */

	protected SaveToFile findSaveableComponent() {
		return table;
	}

		/**	TransferHandler for drag and drop
		 *
		 *
		 */

		public class WordClassTransferHandler extends TransferHandler {
			DataFlavor xferFlavor;
			String xferType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.querytool.SearchCriteriaTransferData";

			/**	Drag icon. */
			private ImageIcon dragicon = null;

			public WordClassTransferHandler() {
				try {
					xferFlavor = new DataFlavor(xferType);
				} catch (ClassNotFoundException e) {
					System.out.println("WordClassTransferHandler: unable to create data flavor");
				}
			}

			public WordClassTransferHandler(String property) {
				try {
					xferFlavor = new DataFlavor(xferType);
				} catch (ClassNotFoundException e) {
					System.out.println("WordClassTransferHandler: unable to create data flavor");
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
			if (c instanceof JTable) {
				try {
					int[] rows = ((JTable)c).getSelectedRows();
					if (rows.length < 0) return null;
					for(int i=0;i<rows.length;i++) {
						WordClass wc = wordClassArray[rows[i]];
						gos.add(wc);
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

