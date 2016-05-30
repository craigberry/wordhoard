package edu.northwestern.at.wordhoard.swing.lexicon;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;
import javax.swing.table.*;
import java.text.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.info.*;
import edu.northwestern.at.wordhoard.swing.querytool.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.db.*;

/**	A lexicon panel.
 */

class LexiconPanel extends JPanel {

	/**	Font size. */

	private static final int FONT_SIZE = 10;

	/**	Table column widths. */

	private static final int[] COL_WIDTHS = new int[] {150, 50, 10, 10};

	/**	Text panel width. */

	private static final int TEXT_PANEL_WIDTH = 450;

	/**	Key type threshold in milliseconds. */

	private static final long KEY_TYPE_THRESHOLD = 1500;

	/**	Word class option. */

	private static String majorClass = "All";

	/**	Order by option. */

	private static int orderBy = LexiconOptionsPanel.ORDER_BY_LEMMA;

	/**	Corpus. */

	private Corpus corpus;

	/**	Persistence manager. */

	private PersistenceManager pm;

	/**	Parent window. */

	private AbstractWindow parentWindow;

	/**	Collator. */

	private Collator collator;

	/**	Font for Roman alphabet. */

	private Font romanFont;

	/**	Roman font ascent. */

	private int romanFontAscent;

	/**	Lexicon item class. */

	protected static class LexiconItem {
		LemmaCorpusCounts counts;
		CollationKey collationKey;
	}

	/**	Lexicon items. */

	LexiconItem[] lexiconItems;

	/**	Table data - sorted subset of all the lexicon items. */

	LexiconItem[] data = new LexiconItem[0];

	/**	Status label. */

	private JLabel statusLabel =
		new JLabel("Loading lexicon ... please be patient");

	/**	Table model class. */

	private class MyTableModel extends AbstractTableModel {

		public int getRowCount () {
			return data.length;
		}

		public int getColumnCount () {
			return 4;
		}

		public Object getValueAt (int row, int col) {
			LemmaCorpusCounts info = data[row].counts;
			switch (col) {
				case 0:
					return info.getTag().getString();
				case 1:
					return info.getWordClass().getMajorWordClass();
				case 2:
					return Formatters.formatIntegerWithCommas(
						info.getColFreq());
				case 3:
					return Formatters.formatIntegerWithCommas(
						info.getDocFreq());
			}
			return null;
		}

		public String getColumnName (int col) {
			switch (col) {
				case 0: return "Lemma";
				case 1: return "Major Word Class";
				case 2: return "Count";
				case 3: return "# Works";
			}
			return null;
		}

	}

	/**	Table cell renderer. */

	private class MyRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent (JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int col)
		{
			super.getTableCellRendererComponent(table, value,
				isSelected, false, row, col);
			LemmaCorpusCounts info = data[row].counts;
			byte tagCharset = info.getTag().getCharset();
			FontInfo tagFontInfo = fontManager.getFontInfo(
				tagCharset, FONT_SIZE);
			int tagFontAscent = tagFontInfo.getAscent();
			if (col == 0) {
				this.setHorizontalAlignment(SwingConstants.TRAILING);
				int topMargin = romanFontAscent > tagFontAscent ?
					romanFontAscent - tagFontAscent : 0;
				this.setBorder(BorderFactory.createEmptyBorder(
					topMargin, 0, 0, 10));
				this.setFont(tagFontInfo.getFont());
			} else {
				int topMargin = tagFontAscent > romanFontAscent ?
					tagFontAscent - romanFontAscent : 0;
				this.setFont(romanFont);
				if (col == 1) {
					this.setHorizontalAlignment(SwingConstants.CENTER);
					this.setBorder(BorderFactory.createEmptyBorder(
						topMargin, 0, 0, 0));
				} else {
					this.setHorizontalAlignment(SwingConstants.LEADING);
					this.setBorder(BorderFactory.createEmptyBorder(
						topMargin, 10, 0, 0));
				}
			}
			return this;
		}
	}

	/**	Table model. */

	private MyTableModel model = new MyTableModel();

	/**	Table. */

//	protected JTable table;
	protected XTable table;

	/**	Scrollpane. */

//	private JScrollPane scrollPane;
	private XScrollPane scrollPane;

	/**	Lemma prefix as typed by user. */

	private String lemmaPrefix;

	/**	System time of last key typed event. */

	private long lastKeyTypedTime = 0;

	/**	Font manager. */

	private FontManager fontManager;

	/**	Creates a new lexicon panel.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	pm				Persistence manager.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PesistenceException
	 */

	LexiconPanel (Corpus corpus, PersistenceManager pm,
		AbstractWindow parentWindow)
			throws PersistenceException
	{

		this.corpus = corpus;
		this.pm = pm;
		this.parentWindow = parentWindow;

		byte charset = corpus.getCharset();
		collator = CharsetUtils.getCollator(charset, Collator.TERTIARY);
		fontManager = new FontManager();
		FontInfo romanFontInfo = fontManager.getFontInfo(FONT_SIZE);
		romanFont = romanFontInfo.getFont();
		romanFontAscent = romanFontInfo.getAscent();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		statusLabel.setFont(romanFont);
		statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(statusLabel);
		add(Box.createVerticalStrut(10));

		LexiconOptionsPanel optionsPanel =
			new LexiconOptionsPanel(romanFont, this, majorClass, orderBy);
		optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(optionsPanel);
		add(Box.createVerticalStrut(14));

		StringBuffer buf = new StringBuffer();
		buf.append("To quickly jump to and select a lemma, type the ");
		buf.append("first few characters of the lemma\u2019s name. ");
		buf.append("To get information ");
		buf.append("about a lemma, select it and press enter/return, or ");
		buf.append("double-click it.");
		WrappedTextComponent textPanel =
			new WrappedTextComponent(buf.toString(),
				romanFont, TEXT_PANEL_WIDTH);
		textPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(textPanel);
		add(Box.createVerticalStrut(14));

		MyRenderer renderer = new MyRenderer();
//		table = new JTable(model);
		table = new XTable(model);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setFont(romanFont);
		table.setGridColor(Color.lightGray);
		table.setShowGrid(true);
		table.setRowHeight(18);

		table.setTransferHandler(new LexiconTransferHandler());
        table.setDragEnabled(true);

		for (int col = 0; col < model.getColumnCount(); col++) {
			TableColumn column = table.getColumnModel().getColumn(col);
			column.setCellRenderer(renderer);
			column.setPreferredWidth(COL_WIDTHS[col]);
			column.setMinWidth(COL_WIDTHS[col]);
		}

		InputMap inputMap = table.getInputMap(
			JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		KeyStroke enterStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		inputMap.put(enterStroke, new Object());
		KeyStroke tabStroke = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
		inputMap.put(tabStroke, new Object());

		table.addKeyListener(
			new KeyAdapter() {
				public void keyTyped (KeyEvent event) {
					try {
						if (event.isShiftDown() || event.isControlDown() ||
							event.isMetaDown()) return;
						handleKeyTyped(event.getKeyChar());
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		TableDragMouseHandler mh = new TableDragMouseHandler();
		table.addMouseListener(mh);
		table.addMouseMotionListener(mh);
/*		table.addMouseListener(
			new MouseAdapter() {
				public void mouseClicked (MouseEvent event) {
					try {
						handleMouseClicked(event);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);*/

//		scrollPane = new JScrollPane(table,
		scrollPane = new XScrollPane(table,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.getViewport().setBackground(Color.white);
		add(scrollPane);

		Dimension size = getPreferredSize();
		size.width += 20;
		setPreferredSize(size);
		setMinimumSize(size);

	}

	/**	Sets the lexicon.
	 *
	 *	@param	lexicon		The lexicon.
	 */

	void setLexicon (LemmaCorpusCounts[] lexicon) {
		lexiconItems = new LexiconItem[lexicon.length];
		for (int i = 0; i < lexicon.length; i++) {
			LemmaCorpusCounts counts = lexicon[i];
			LexiconItem item = new LexiconItem();
			item.counts = counts;
			item.collationKey =
				collator.getCollationKey(counts.getTag().getString());
			lexiconItems[i] = item;
		}
		setData();
	}

	/**	Get the lexicon table.
	 *
	 *	@return		The lexicon table.
	 */

	public JTable getTable() {
		return table;
	}

	/**	Comparator class for order by options. */

	private static class OrderingComparator implements Comparator {

		public int compare (Object o1, Object o2) {
			LexiconItem item1 = (LexiconItem)o1;
			LexiconItem item2 = (LexiconItem)o2;
			if (orderBy == LexiconOptionsPanel.ORDER_BY_CLASS_LEMMA ||
				orderBy == LexiconOptionsPanel.ORDER_BY_CLASS_FREQUENCY_LEMMA ||
				orderBy == LexiconOptionsPanel.ORDER_BY_CLASS_NUMWORKS_LEMMA)
			{
				String class1 = item1.counts.getMajorClass();
				String class2 = item2.counts.getMajorClass();
				int k = class1.compareTo(class2);
				if (k != 0) return k;
			}
			if (orderBy == LexiconOptionsPanel.ORDER_BY_FREQUENCY_LEMMA ||
				orderBy == LexiconOptionsPanel.ORDER_BY_CLASS_FREQUENCY_LEMMA)
			{
				int freq1 = item1.counts.getColFreq();
				int freq2 = item2.counts.getColFreq();
				if (freq1 != freq2) return freq2-freq1;
			}
			if (orderBy == LexiconOptionsPanel.ORDER_BY_NUMWORKS_LEMMA ||
				orderBy == LexiconOptionsPanel.ORDER_BY_CLASS_NUMWORKS_LEMMA)
			{
				int freq1 = item1.counts.getDocFreq();
				int freq2 = item2.counts.getDocFreq();
				if (freq1 != freq2) return freq2-freq1;
			}
			return item1.collationKey.compareTo(item2.collationKey);
		}

	}

	/**	Comparator for order by options. */

	private static OrderingComparator orderingComparator =
		new OrderingComparator();

	/**	Sets the data. */

	private void setData () {
		if (lexiconItems == null) return;
		boolean allmajorClasses = majorClass.equals("All");
		if (allmajorClasses) {
			data = new LexiconItem[lexiconItems.length];
			for (int i = 0; i < lexiconItems.length; i++)
				data[i] = lexiconItems[i];
		} else {
			ArrayList dataList = new ArrayList();
			for (int i = 0; i < lexiconItems.length; i++) {
				LexiconItem item = lexiconItems[i];
				if (majorClass.equals(item.counts.getMajorClass()))
					dataList.add(item);
			}
			data = (LexiconItem[])dataList.toArray(
				new LexiconItem[dataList.size()]);
		}
		Arrays.sort(data, orderingComparator);
		String statusMsg = Formatters.formatIntegerWithCommas(data.length);
		if (allmajorClasses) {
			statusMsg = statusMsg + " lemmas";
		} else {
			statusMsg = statusMsg + " " + majorClass +
				(data.length == 1 ? "" : "s");
		}
		statusMsg = statusMsg + " in lexicon";
		statusLabel.setText(statusMsg);
		model.fireTableDataChanged();
		if (data.length > 0) {
			table.setRowSelectionInterval(0,0);
			scrollPane.getViewport().setViewPosition(new Point(0,0));
		}
	}

	/**	Handles a key typed event.
	 *
	 *	@param	c		Character typed.
	 *
	 *	@throws	PesistenceException
	 */

	private void handleKeyTyped (char c)
		throws PersistenceException
	{
		if (c == '\n') {
			openGetInfoWindow();
			return;
		}
		int startRow = 0;
		if (c == '\t') {
			int row = table.getSelectedRow();
			if (row >= 0) startRow = row+1;
		} else {
			long time = System.currentTimeMillis();
			if (time > lastKeyTypedTime + KEY_TYPE_THRESHOLD) lemmaPrefix = "";
			lemmaPrefix = lemmaPrefix + c;
			lastKeyTypedTime = time;
		}
		int lemmaPrefixLen = lemmaPrefix.length();
		for (int i = startRow; i < data.length; i++) {
			String lemmaTag = data[i].counts.getTag().getString();
			if (lemmaTag.length() < lemmaPrefixLen) continue;
			String lemmaTagSubstring =
				lemmaTag.substring(0, lemmaPrefixLen);
			if (collator.equals(lemmaPrefix, lemmaTagSubstring)) {
				table.setRowSelectionInterval(i, i);
				Rectangle rect = table.getCellRect(i, 0, false);
				JViewport viewport = scrollPane.getViewport();
				Rectangle viewRect = viewport.getViewRect();
				if (viewRect.contains(rect)) break;
				int y = rect.y - viewRect.height/2;
				y = Math.max(y, 0);
				Point p = new Point(0, y);
				viewport.setViewPosition(p);
				break;
			}
		}
	}

	/**	Opens a lemma info window for the selected lemma.
	 *
	 *	@throws	PersistenceException
	 */

	void openGetInfoWindow ()
		throws PersistenceException
	{
		int row = table.getSelectedRow();
		if (row < 0) return;
		LexiconItem item = data[row];
		Lemma lemma = item.counts.getLemma();
		new LemmaInfoWindow(lemma, corpus, parentWindow);
	}

	/**	Handles a mouse clicked event.
	 *
	 *	@param	event		Mouse clicked event.
	 *
	 *	@throws	PesistenceException
	 */

	private void handleMouseClicked (MouseEvent event)
		throws PersistenceException
	{
		if (event.getClickCount() <= 1) return;
		int row = table.getSelectedRow();
		if (row < 0) return;
		LexiconItem item = data[row];
		Lemma lemma = item.counts.getLemma();
		new LemmaInfoWindow(lemma, corpus, parentWindow);
	}

	/**	Handles a change in options event.
	 *
	 *	@param	majorClass		New word class option.
	 *
	 *	@param	orderBy			New ordering option.
	 */

	void handleOptions (String majorClass, int orderBy) {
		if (majorClass == this.majorClass && orderBy == this.orderBy) return;
		this.majorClass = majorClass;
		this.orderBy = orderBy;
		setData();
	}

	/**	Gets search defaults.
	 *
	 *	@return		Search defaults.
	 */

	SearchDefaults getSearchDefaults () {
		int row = table.getSelectedRow();
		if (row < 0) return corpus;
		LexiconItem item = data[row];
		return item.counts.getLemma();
	}

	public class TableDragMouseHandler implements MouseListener, MouseMotionListener {
		MouseEvent firstMouseEvent = null;

		public void mousePressed(MouseEvent e) {
			firstMouseEvent = e;
			e.consume();
		}
		public void mouseReleased(MouseEvent e)  {;}
		public void mouseClicked (MouseEvent event) {
			try {
				handleMouseClicked(event);
			} catch (Exception e) {
				Err.err(e);
			}
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

public class LexiconTransferHandler extends TransferHandler  {

		DataFlavor xferFlavor;
		String xferType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.querytool.SearchCriteriaTransferData";

		/**	Drag icon. */
		private ImageIcon dragicon = null;

		public LexiconTransferHandler() {
			try {
				xferFlavor = new DataFlavor(xferType);
			} catch (ClassNotFoundException e) {
				System.out.println("LexiconTransferHandler: unable to create data flavor");
			}
		}

		public LexiconTransferHandler(String property) {
			try {
				xferFlavor = new DataFlavor(xferType);
			} catch (ClassNotFoundException e) {
				System.out.println("LexiconTransferHandler: unable to create data flavor");
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
					LexiconItem item = data[rows[i]];
					Lemma lemma = item.counts.getLemma();
					gos.add(lemma);
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

