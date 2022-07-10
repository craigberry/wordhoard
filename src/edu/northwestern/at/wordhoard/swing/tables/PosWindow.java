package edu.northwestern.at.wordhoard.swing.tables;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.swing.find.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.db.*;

import edu.northwestern.at.wordhoard.swing.querytool.*;
import java.awt.datatransfer.*;

/**	The parts of speech windows.
 */

public class PosWindow extends AbstractWindow {
	
	/**	The  parts of speech window, or null if none is open. */
	
	private static PosWindow posWindow;

	/**	Opens or brings to the front the parts of speech window.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public static void open (AbstractWindow parentWindow)
		throws PersistenceException
	{
		if (posWindow == null) {
			posWindow = new PosWindow(parentWindow);
		} else {
			posWindow.setVisible(true);
			posWindow.toFront();
		}
	}
	
	/**	The language of the current parts of speech shown, or null if all shown. */
	
	private static String language = null;
	
	/**	The index of the column by which the parts of speech are ordered. */
	
	private static int orderingColumn = 0;
	
	/**	True if we have descriptions for the parts of speech. */
	
	private boolean haveDescriptions = false;

	/**	Array of the current parts of speech displayed, in order. */

	private Pos posArray[];

	/**	Table model. */

	private class MyTableModel extends AbstractTableModel {

		public int getRowCount () {
			return posArray.length;
		}

		public int getColumnCount () {
			return haveDescriptions ? 14 : 13;
		}

		public Object getValueAt (int row, int col) {
			Pos pos = posArray[row];
			return getColValue(pos, col);
		}

		public String getColumnName (int col) {
			return getColName(col);
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
			this.setHorizontalAlignment(SwingConstants.LEADING);
			this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
			this.setFont(font);
			return this;
		}
	}

	/**	Table. */

	private XTable table;
	
	/**	Font. */
	
	private Font font;
	
	/**	Status label. */
	
	private JLabel statusLabel = 
		new JLabel("xxxx parts of speech");
	
	/**	Show combo box. */
	
	private SmallComboBox showComboBox;
	
	/**	Order by combo box. */
	
	private SmallComboBox orderByComboBox;
	
	/**	Table model */
	
	private MyTableModel model;
	
	/** Gets the value of part of speech column. 
	 *
	 *	@param	pos		Part of speech.
	 *
	 *	@param	col		Column index.
	 *
	 *	@return			Value of column as a string.
	 */
		
	public static String getColValue (Pos pos, int col) {
		switch (col) {
			case 0: return pos.getTag();
			case 1: 
				WordClass wordClass = pos.getWordClass();
				if (wordClass == null) return "";
				return wordClass.getTag();
			case 2:
				String syntax = pos.getSyntax();
				return syntax == null ? "" : syntax;
			case 3:
				String tense = pos.getTense();
				return tense == null ? "" : tense;
			case 4:
				String mood = pos.getMood();
				return mood == null ? "" : mood;
			case 5:
				String voice = pos.getVoice();
				return voice == null ? "" : voice;
			case 6:
				String xcase = pos.getXcase();
				return xcase == null ? "" : xcase;
			case 7:
				String gender = pos.getGender();
				return gender == null ? "" : gender;
			case 8:
				String person = pos.getPerson();
				return person == null ? "" : person;
			case 9:
				String number = pos.getNumber();
				return number == null ? "" : number;
			case 10:
				String degree = pos.getDegree();
				return degree == null ? "" : degree;
			case 11:
				String negative = pos.getNegative();
				return negative == null ? "" : negative;
			case 12:
				String language = pos.getLanguage();
				return language == null ? "" : language;
			case 13:
				String description = pos.getDescription();
				return description == null ? "" : description;
		}
		return "";
	}
	
	/**	Gets a column name. 
	 *
	 *	@param	col		Column index.
	 *
	 *	@return			Column name.
	 */
	 
	private static String getColName (int col) {
		switch (col) {
			case 0: return "Name";
			case 1: return "Word Class";
			case 2: return "Syntax (used as)";
			case 3: return "Tense";
			case 4: return "Mood";
			case 5: return "Voice";
			case 6: return "Case";
			case 7: return "Gender";
			case 8: return "Person";
			case 9: return "Number";
			case 10: return "Degree";
			case 11: return "Negative";
			case 12: return "Language";
			case 13: return "Description";
		}
		return null;
	}
	
	/**	Gets and orders the parts of speech to be displayed. 
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */
	
	private void getAndOrderPartsOfSpeech () 
		throws PersistenceException
	{
		Pos allPos[] = CachedCollections.getPos();
		haveDescriptions = false;
		for (int i = 0; i < allPos.length; i++) {
			Pos pos = allPos[i];
			String description = pos.getDescription();
			if (description != null && description.length() > 0) {
				haveDescriptions = true;
				break;
			}
		}
		ArrayList posList = new ArrayList();
		for (int i = 0; i < allPos.length; i++) {
			Pos pos = allPos[i];
			if (language == null) {
				posList.add(pos);
			} else if (pos.getLanguage().equals(language)) {
				posList.add(pos);
			}
			posArray = (Pos[])posList.toArray(new Pos[posList.size()]);
		}
		Arrays.sort(posArray,
			new Comparator() {
				public int compare (Object o1, Object o2) {
					Pos pos1 = (Pos)o1;
					Pos pos2 = (Pos)o2;
					String lang1 = pos1.getLanguage();
					String lang2 = pos2.getLanguage();
					int result = lang1.compareTo(lang2);
					if (result != 0) return result;
					if (orderingColumn != 0) {
						String val1 = getColValue(pos1, orderingColumn);
						String val2 = getColValue(pos2, orderingColumn);
						int val1len = val1.length();
						int val2len = val2.length();
						if (val1len == 0) {
							if (val2len > 0) return +1;
						} else if (val2len == 0) {
							return -1;
						} else {
							result = val1.compareTo(val2);
							if (result != 0) return result;
						}
					}
					String tag1 = pos1.getTag();
					String tag2 = pos2.getTag();
					int tag1Numeric = 0;
					int tag2Numeric = 0;
					boolean bothTagsNumeric = true;
					try {
						tag1Numeric = Integer.parseInt(tag1);
					} catch (NumberFormatException e) {
						bothTagsNumeric = false;
					}
					try {
						tag2Numeric = Integer.parseInt(tag2);
					} catch (NumberFormatException e) {
						bothTagsNumeric = false;
					}
					if (bothTagsNumeric) {
						return tag1Numeric - tag2Numeric;
					} else {
						return tag1.compareTo(tag2);
					}
				}
			}
		);
				
		String statusMsg = Formatters.formatIntegerWithCommas(posArray.length);
		if (language != null) statusMsg = statusMsg + " " + language;
		statusMsg = statusMsg + " parts of speech";
		statusLabel.setText(statusMsg);
	}
	
	/**	Creates the options panel. 
	 *
	 *	@return		Options panel.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */
	
	private JPanel createOptionsPanel () 
		throws PersistenceException
	{	
		JLabel showLabel =
			WordHoard.getSmallComboBoxLabel("Show: ", font);
		showComboBox = new SmallComboBox(font);
		showComboBox.addItem("All");
		if (language == null) showComboBox.setSelectedIndex(0);
		HashSet languagesSet = new HashSet();
		Pos allPos[] = CachedCollections.getPos();
		for (int i = 0; i < allPos.length; i++) 
			languagesSet.add(allPos[i].getLanguage());
		String[] languageNames = 
			(String[])languagesSet.toArray(new String[languagesSet.size()]);
		Arrays.sort(languageNames);
		for (int i = 0; i < languageNames.length; i++) {
			String languageName = languageNames[i];
			showComboBox.addItem(languageName);
			if (languageName.equals(language))
				showComboBox.setSelectedIndex(i+1);
		}
		showComboBox.addActionListener(optionsPanelActionListener);
		
		JLabel orderByLabel =
			WordHoard.getSmallComboBoxLabel("Order by: ", font);
		orderByComboBox = new SmallComboBox(font);
		for (int i = 0; i <= 11; i++) {
			orderByComboBox.addItem(getColName(i));
			if (i == orderingColumn) orderByComboBox.setSelectedIndex(i);
		}
		orderByComboBox.addActionListener(optionsPanelActionListener);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(showLabel);
		panel.add(showComboBox);
		panel.add(Box.createHorizontalStrut(15));
		panel.add(orderByLabel);
		panel.add(orderByComboBox);
		Dimension size = panel.getPreferredSize();
		size.width = 100000;
		panel.setMaximumSize(size);
		return panel;
	}
	
	/**	Action listener for options panel combo boxes. */
	
	private ActionListener optionsPanelActionListener =
		new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				String showItem = (String)showComboBox.getSelectedItem();
				if (showItem.equals("All")) {
					language = null;
				} else {
					language = showItem;
				}
				orderingColumn = orderByComboBox.getSelectedIndex();
				try {
					getAndOrderPartsOfSpeech();
					model.fireTableDataChanged();
				} catch (Exception e) {
					Err.err(e);
				}
			}
		};

	/**	Creates a new parts of speech window.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public PosWindow (AbstractWindow parentWindow)
		throws PersistenceException
	{

		super("Parts of Speech", parentWindow);
		getAndOrderPartsOfSpeech();
		
		FontManager fontManager = new FontManager();
		font = fontManager.getFont(10);
		
		statusLabel.setFont(font);
		statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
		
		JPanel optionsPanel = createOptionsPanel();
		optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		optionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 14, 0));

		model = new MyTableModel();
		MyRenderer renderer = new MyRenderer();
		table = new XTable(model);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setRowSelectionInterval(0,0);
		table.setGridColor(Color.lightGray);
		table.setShowGrid(true);
		table.setRowHeight(18);

		table.setTransferHandler(new PosTransferHandler());
        table.setDragEnabled(true);

		SearchCriteriaDragMouseHandler mh = new SearchCriteriaDragMouseHandler();
		table.addMouseListener(mh);
		table.addMouseMotionListener(mh);

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

		XScrollPane scrollPane = new XScrollPane(
			table,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		JScrollBar hBar = scrollPane.getHorizontalScrollBar();
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		int hBarHeight = hBar.getPreferredSize().height;
		int vBarWidth = vBar.getPreferredSize().width;
		Dimension d = table.getPreferredSize();
		d.width += vBarWidth + 20;
		d.height += hBarHeight + 40;
		scrollPane.setPreferredSize(d);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(statusLabel);
		contentPane.add(optionsPanel);
		contentPane.add(scrollPane);

		setContentPane(contentPane);

		pack();
		Dimension screenSize = getToolkit().getScreenSize();
		Dimension windowSize = getSize();
		windowSize.height = screenSize.height -
			(WordHoardSettings.getTopSlop() + WordHoardSettings.getBotSlop());
		if (windowSize.width + 10 > screenSize.width)
			windowSize.width = screenSize.width-10;
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
		posWindow = null;
		super.dispose();
	}

	/**	Handles the "Find" command.
	 *
	 *	@throws	Exception	general error.
	 */

	public void handleFindWordsCmd ()
		throws Exception
	{
		SearchDefaults defaults = null;
		int row = table.getSelectedRow();
		if (row >= 0) defaults = posArray[row];
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

		public class PosTransferHandler extends TransferHandler {
			DataFlavor xferFlavor;
			String xferType = DataFlavor.javaJVMLocalObjectMimeType + ";class=edu.northwestern.at.wordhoard.swing.querytool.SearchCriteriaTransferData";

			/**	Drag icon. */
			private ImageIcon dragicon = null;

			public PosTransferHandler() {
				try {
					xferFlavor = new DataFlavor(xferType);
				} catch (ClassNotFoundException e) {
					System.out.println("PosTransferHandler: unable to create data flavor");
				}
			}

			public PosTransferHandler(String property) {
				try {
					xferFlavor = new DataFlavor(xferType);
				} catch (ClassNotFoundException e) {
					System.out.println("PosTransferHandler: unable to create data flavor");
				}
			}

			public boolean importData(JComponent c, Transferable t) {return true;}

		protected void exportDone(JComponent c, Transferable data, int action) {}

		public boolean canImport(JComponent c, DataFlavor[] flavors) { return false;}

		protected Transferable createTransferable(JComponent c) {
			SearchCriteriaTransferData gos = new SearchCriteriaTransferData();
			if (c instanceof JTable) {
				try {
					int[] rows = ((JTable)c).getSelectedRows();
					if (rows.length < 0) return null;
					for(int i=0;i<rows.length;i++) {
						Pos epos = posArray[rows[i]];
						gos.add(epos);
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

