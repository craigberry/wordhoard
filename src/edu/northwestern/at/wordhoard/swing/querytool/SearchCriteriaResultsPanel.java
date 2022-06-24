package edu.northwestern.at.wordhoard.swing.querytool;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

import javax.swing.table.*;

import java.awt.datatransfer.*;

import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.concordance.*;
import edu.northwestern.at.wordhoard.swing.find.*;
import edu.northwestern.at.wordhoard.swing.info.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;

/**	A search results panel.
 */

public class SearchCriteriaResultsPanel extends JPanel {

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

	private static int orderBy = SearchCriteriaResultsOptionsPanel.ORDER_BY_LEMMA;

	/**	Parent window. */

	private AbstractWindow parentWindow;

	/**	Collator. */

	private Collator collator;

	/**	Font for Latin alphabet. */

	private Font latinFont;

	/**	Latin font ascent. */

	private int latinFontAscent;

	/**	Search Result item class. */

	protected static class SearchResultItem {
		SearchResultLemmaSearch searchResult;
		CollationKey collationKey;
	}

	/**	Search Result items. */

	SearchResultItem[] searchResultItems;

	/**	Table data - sorted subset of all the searchResult items. */

	SearchResultItem[] data = new SearchResultItem[0];

	/**	Status label. */

	private JLabel statusLabel =
		new JLabel("Loading search results ... please be patient");

	/**	Table model class. */

	private class MyTableModel extends AbstractTableModel {

		public int getRowCount () {
			return data.length;
		}

		public int getColumnCount () {
			return 4;
		}

		public Object getValueAt (int row, int col) {
			SearchResultLemmaSearch item = data[row].searchResult;
			switch (col) {
				case 0:
					return item.getLemma().getTag().getString();
				case 1:
					return item.getLemma().getWordClass().getMajorWordClass();
				case 2:
					return Formatters.formatIntegerWithCommas(
						item.getCount());
				case 3:
					return Formatters.formatIntegerWithCommas(
						item.getDocCount());
			}
			return null;
		}

		public String getColumnName (int col) {
			switch (col) {
				case 0: return "Lemma";
				case 1: return "Major Word Class";
				case 2: return "Total Count";
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
			SearchResultLemmaSearch info = data[row].searchResult;
			byte tagCharset = info.getTag().getCharset();
			FontInfo tagFontInfo = fontManager.getFontInfo(
				tagCharset, FONT_SIZE);
			int tagFontAscent = tagFontInfo.getAscent();
			if (col == 0) {
				this.setHorizontalAlignment(SwingConstants.TRAILING);
				int topMargin = latinFontAscent > tagFontAscent ?
					latinFontAscent - tagFontAscent : 0;
				this.setBorder(BorderFactory.createEmptyBorder(
					topMargin, 0, 0, 10));
				this.setFont(tagFontInfo.getFont());
			} else {
				int topMargin = tagFontAscent > latinFontAscent ?
					tagFontAscent - latinFontAscent : 0;
				this.setFont(latinFont);
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

	protected XTable table;

	/**	Scrollpane. */

	private XScrollPane scrollPane;

	/**	Lemma prefix as typed by user. */

	private String lemmaPrefix;

	/**	System time of last key typed event. */

	private long lastKeyTypedTime = 0;

	/**	Font manager. */

	private FontManager fontManager;

	/**	Creates a new search results panel.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PesistenceException
	 */

	public SearchCriteriaResultsPanel (AbstractWindow parentWindow) throws PersistenceException  {
		super();
		this.parentWindow = parentWindow;
		System.out.println("xxx");

//		byte charset = corpus.getCharset();
//		collator = CharsetUtils.getCollator(charset, Collator.TERTIARY);
		collator = CharsetUtils.getCollator(TextParams.ROMAN, Collator.TERTIARY);

		fontManager = new FontManager();
		FontInfo latinFontInfo = fontManager.getFontInfo(FONT_SIZE);
		latinFont = latinFontInfo.getFont();
		latinFontAscent = latinFontInfo.getAscent();

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		statusLabel.setFont(latinFont);
		statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
	//	add(statusLabel);
		add(Box.createVerticalStrut(10));

		SearchCriteriaResultsOptionsPanel optionsPanel =
			new SearchCriteriaResultsOptionsPanel(latinFont, this, majorClass, orderBy);
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
				latinFont, TEXT_PANEL_WIDTH);
		textPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	//	add(textPanel);
	//	add(Box.createVerticalStrut(14));

		MyRenderer renderer = new MyRenderer();
//		table = new JTable(model);
		table = new XTable(model);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setFont(latinFont);
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

/*
		table.addKeyListener(
			new KeyAdapter() {
				public void keyTyped (KeyEvent event) {
					try {
						System.out.println("yyy");
						if (event.isShiftDown() || event.isControlDown() ||
							event.isMetaDown()) return;
						handleKeyTyped(event.getKeyChar());
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
*/

		TableDragMouseHandler mh = new TableDragMouseHandler();
		table.addMouseListener(mh);
		table.addMouseMotionListener(mh);
		
/*		
		table.addMouseListener(
			new MouseAdapter() {
				public void mouseClicked (MouseEvent event) {
					try {
						handleMouseClicked(event);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
*/

		scrollPane = new XScrollPane(table,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.getViewport().setBackground(Color.white);
		add(scrollPane);

		Dimension size = getPreferredSize();
		size.width += 20;
		size.height=200;
		setPreferredSize(size);
		setMinimumSize(size);

	}

	/**	Sets the search results.
	 *
	 *	@param	searchResults		The search results.
	 */

	public void setContents(java.util.List searchResults, SearchCriteriaPanel criteriaPanel) {
		searchResultItems = new SearchResultItem[searchResults.size()];
		int i = 0;
		for (Iterator it = searchResults.iterator(); it.hasNext(); ) {
			SearchResultLemmaSearch o = (SearchResultLemmaSearch)it.next();
			SearchResultItem item = new SearchResultItem();
			item.searchResult = o;
			item.collationKey = collator.getCollationKey(o.getTag().getString());
			searchResultItems[i++] = item;
		}
		setData();
	}

/*
	void setSearchResults (SearchResultLemmaSearch[] searchResults) {
		searchResultItems = new SearchResultItem[searchResults.length];
		for (int i = 0; i < searchResults.length; i++) {
			SearchResultLemmaSearch searchResult = searchResults[i];
			SearchResultItem item = new SearchResultItem();
			item.searchResult = searchResult;
			item.collationKey =
				collator.getCollationKey(searchResult.getTag().getString());
			searchResultItems[i] = item;
		}
		setData();
	}
*/
	/**	Get the search results table.
	 *
	 *	@return		The search results table.
	 */

	public JTable getTable() {
		return table;
	}

	/**	Comparator class for order by options. */

	private static class OrderingComparator implements Comparator {

		public int compare (Object o1, Object o2) {
			SearchResultItem item1 = (SearchResultItem)o1;
			SearchResultItem item2 = (SearchResultItem)o2;
			if (orderBy == SearchCriteriaResultsOptionsPanel.ORDER_BY_CLASS_LEMMA ||
				orderBy == SearchCriteriaResultsOptionsPanel.ORDER_BY_CLASS_FREQUENCY_LEMMA ||
				orderBy == SearchCriteriaResultsOptionsPanel.ORDER_BY_CLASS_NUMWORKS_LEMMA)
			{
				String class1 = item1.searchResult.getLemma().getWordClass().getMajorWordClass().getMajorWordClass();
				String class2 = item2.searchResult.getLemma().getWordClass().getMajorWordClass().getMajorWordClass();
				int k = class1.compareTo(class2);
				if (k != 0) return k;
			}
			if (orderBy == SearchCriteriaResultsOptionsPanel.ORDER_BY_FREQUENCY_LEMMA ||
				orderBy == SearchCriteriaResultsOptionsPanel.ORDER_BY_CLASS_FREQUENCY_LEMMA)
			{
				int freq1 = item1.searchResult.getCount();
				int freq2 = item2.searchResult.getCount();
				if (freq1 != freq2) return freq2-freq1;
			}
			if (orderBy == SearchCriteriaResultsOptionsPanel.ORDER_BY_NUMWORKS_LEMMA ||
				orderBy == SearchCriteriaResultsOptionsPanel.ORDER_BY_CLASS_NUMWORKS_LEMMA)
			{
				int freq1 = item1.searchResult.getDocCount();
				int freq2 = item2.searchResult.getDocCount();
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
		if (searchResultItems == null) return;
		boolean allmajorClasses = majorClass.equals("All");
		if (allmajorClasses) {
			data = new SearchResultItem[searchResultItems.length];
			for (int i = 0; i < searchResultItems.length; i++)
				data[i] = searchResultItems[i];
		} else {
			ArrayList dataList = new ArrayList();
			for (int i = 0; i < searchResultItems.length; i++) {
				SearchResultItem item = searchResultItems[i];
				if (majorClass.equals(item.searchResult.getLemma().getWordClass().getMajorWordClass().getMajorWordClass()))
					dataList.add(item);
			}
			data = (SearchResultItem[])dataList.toArray(
				new SearchResultItem[dataList.size()]);
		}
		Arrays.sort(data, orderingComparator);
		String statusMsg = Formatters.formatIntegerWithCommas(data.length);
		if (allmajorClasses) {
			statusMsg = statusMsg + " lemmas";
		} else {
			statusMsg = statusMsg + " " + majorClass +
				(data.length == 1 ? "" : "s");
		}
		statusMsg = statusMsg + " in search results";
		statusLabel.setText(statusMsg);
		model.fireTableDataChanged();
		if (data.length > 0) {
			table.setRowSelectionInterval(0,0);
			scrollPane.getViewport().setViewPosition(new Point(0,0));
		}
	}

	/**	Handles a key typed event.
	 *
	 *	<p>(Not used.)
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
		for (int i = startRow; i < data.length; i++) {
			String lemmaTag = data[i].searchResult.getTag().getString();
			String lemmaTagSubstring =
				lemmaTag.substring(0, lemmaPrefix.length());
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
		SearchResultItem item = data[row];
		Lemma lemma = item.searchResult.getLemma();
		//	Bogus: Cannot pass null for corpus to LemmaInfoWindow constructor.
		new LemmaInfoWindow(lemma, null, parentWindow);
	}

	/**	Handles a mouse clicked event.
	 *
	 *	<p>(Not used.)
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
		SearchResultItem item = data[row];
		Lemma lemma = item.searchResult.getLemma();
		//	Bogus: Cannot pass null for corpus to LemmaInfoWindow constructor.
		new LemmaInfoWindow(lemma, null, parentWindow);
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
		if (row < 0) return null;
		SearchResultItem item = data[row];
		return item.searchResult.getLemma();
	}

	public class TableDragMouseHandler implements MouseListener, MouseMotionListener {
		MouseEvent firstMouseEvent = null;

		public void mousePressed(MouseEvent e) {
			firstMouseEvent = e;
			e.consume();
		}
		public void mouseReleased (MouseEvent event) {
			try {
				if (event.getClickCount() > 1) openConcordance();
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
					SearchResultItem item = data[rows[i]];
					Lemma lemma = item.searchResult.getLemma();
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

	public void openConcordance() {
		SearchCriteria sq = new SearchCriteria();
		try {
			SearchCriteriaTypedSet ts = new SearchCriteriaTypedSet("Lemma");
			int rows[] = table.getSelectedRows();
			if (rows.length < 0) return;
			for(int i=0;i<rows.length;i++) {
				SearchResultLemmaSearch item = data[rows[i]].searchResult;
				Lemma lemma = item.getLemma();
				ts.add(lemma);
			}
			sq.add(ts);
			new ConcordanceWindow(sq, parentWindow);
		} catch (Exception e) {Err.err(e);}
		return;
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

