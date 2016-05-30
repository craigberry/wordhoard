package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.*;
import java.awt.event.*;
import javax.swing.event.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.swing.dialogs.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	Character set table display.
 *
 *	<p>Usage:
 *
 *	<p><code>CharSetDisplay dbname username password</code></p>
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */

public class CharSetDisplay {

	/**	First 10 lines of Romeo and Juliet, Act 1, Prologue. */

	private static Text romeo;

	/**	First 9 lines of Iliad Book 1. */

	private static Text iliad;

	/**	Font family. */

	private static String fontFamily;

	/**	Main panel. */

	private static JPanel mainPanel = new JPanel();

	/**	Table holding text.
	 */

	private static XTable table;

	/**	Gets the sample text.
	 *
	 *	@param	args		Arguments.
	 *
	 *	@throws	Exception
	 */

	private static void getSampleText (String[] args)
		throws Exception
	{
		BuildUtils.initHibernate(args[0], args[1], args[2]);
		PersistenceManager pm = new PersistenceManager();

		Corpus corpus = pm.getCorpusByTag("sha");
		int n = 20;
		if (corpus != null) {
			Work work = corpus.getWorkByTag("sha-roj");
			if (work != null) {
				java.util.List children = work.getChildren();
				WorkPart act1 = (WorkPart)children.get(2);
				children = act1.getChildren();
				WorkPart prologue = (WorkPart)children.get(0);
				Text text = prologue.getPrimaryText().getText();
				romeo = new Text(true, false);
				TextLine[] lines = text.getLines();
				n = lines.length;
				for (int i = 4; i < n; i++) {
					TextLine line = lines[i];
					line.setIndentation(0);
					romeo.copyLine(line);
				}
				romeo.finalize();
			}
		}

		corpus = pm.getCorpusByTag("ege");
		if (corpus != null) {
			Work work = corpus.getWorkByTag("ege-IL");
			if (work != null) {
				java.util.List children = work.getChildren();
				WorkPart book1 = (WorkPart)children.get(1);
				Text text = book1.getPrimaryText().getText();
				iliad = new Text(true, false);
				TextLine[] lines = text.getLines();
				for (int i = 2; i < n-2; i++) {
					TextLine line = lines[i];
					line.setIndentation(0);
					iliad.copyLine(line);
				}
				iliad.finalize();
			}
		}

		pm.close();
	}

	/**	Creates the sample panel.
	 *
	 *	@throws	Exception
	 */

	private static JComponent createSamplePanel ()
		throws Exception
	{
		FontManager fontManager = new FontManager();
		if (romeo != null) fontManager.initText(romeo);
		if (iliad != null) fontManager.initText(iliad);

		GraphicsEnvironment graphEnv =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] availableFontFamilyNames =
			graphEnv.getAvailableFontFamilyNames();
		TreeSet fonts = new TreeSet();
		for (int i = 0; i < availableFontFamilyNames.length; i++) {
			String familyName = availableFontFamilyNames[i];
			if (familyName.equals("Default")) continue;
			Font font = new Font(familyName, Font.PLAIN, 12);
			boolean canDisplayRoman =
				font.canDisplayUpTo(CharsUsed.ROMAN) == -1;
			boolean canDisplayGreek =
				font.canDisplayUpTo(CharsUsed.GREEK) == -1;
			if (canDisplayRoman && canDisplayGreek) fonts.add(familyName);
		}

		final JComboBox fontComboBox = new JComboBox();
		for (Iterator it = fonts.iterator(); it.hasNext(); ) {
			String fontFamily = (String)it.next();
			fontComboBox.addItem(fontFamily);
		}
		fontComboBox.setMaximumSize(fontComboBox.getPreferredSize());
		fontComboBox.setBackground(Color.white);
		fontComboBox.setSelectedItem(fontFamily);

		fontComboBox.addActionListener(
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						fontFamily = (String)fontComboBox.getSelectedItem();
						String[][] prefs = FontPrefsDialog.getFontPrefs();
						prefs[FontManager.WORK][TextParams.ROMAN] =
							prefs[FontManager.WORK][TextParams.GREEK] =
								fontFamily;
						FontPrefsDialog.setFontPrefs(prefs);
						rebuild();
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
		);

		JPanel fontPanel = new JPanel();
		fontPanel.setLayout(new BoxLayout(fontPanel, BoxLayout.X_AXIS));
		fontPanel.add(Box.createHorizontalGlue());
		fontPanel.add(fontComboBox);
		fontPanel.add(Box.createHorizontalGlue());
		fontPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		fontPanel.setBackground(Color.white);

		WrappedTextComponent romeoComponent = null;
		if (romeo != null) {
			romeoComponent = new WrappedTextComponent(
				romeo, TextParams.RIGHT_MARGIN_NUMBERS);
			romeoComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
			romeoComponent.setBorder(BorderFactory.createEmptyBorder(30,30,50,10));
			romeoComponent.setBackground(Color.white);
		}

		WrappedTextComponent iliadComponent = null;
		if (iliad != null) {
			iliadComponent = new WrappedTextComponent(
				iliad, TextParams.RIGHT_MARGIN_NUMBERS);
			iliadComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
			iliadComponent.setBorder(BorderFactory.createEmptyBorder(30,30,10,10));
			iliadComponent.setBackground(Color.white);
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.white);
		panel.add(Box.createVerticalStrut(20));
		panel.add(fontPanel);
		panel.add(Box.createVerticalStrut(20));
		if (romeoComponent != null) panel.add(romeoComponent);
		if (iliadComponent != null) panel.add(iliadComponent);
		return panel;
	}

	/**	Converts a unicode character code to a four digit hex string.
	 *
	 *	@param	uni		Unicode.
	 *
	 *	@return			Four digit hex string.
	 */

	private static String getHex (int uni) {
			String s = Integer.toString((int)uni, 16);
			String str = "";
			for (int j = 4; j > s.length(); j--) str = str + '0';
			return str + s;
	}

	/**	Converts a unicode character code to a glyph.
	 *
	 *	@param	uni		Unicode.
	 *
	 *	@return			Glyph.
	 */

	private static String getGlyph (int uni) {
		return "" + (char)uni;
	}

	/**	Get the unicode character code for a row.
	 *
	 *	@param	row		Row number.
	 *
	 *	@return			Unicode character code.
	 */

	private static int getUni (int row) {
		row += 0x20;
		if (row < 0x0180) return row;
		row = row - 0x0180 + 0x0370;
		if (row < 0x0400) return row;
		row = row - 0x0400 + 0x1f00;
		return row;
	}

	/**	Table model. */

	private static class MyTableModel extends AbstractTableModel {

		public int getRowCount () {
			return 0x0360;
		}

		public int getColumnCount () {
			return 5;
		}

		public Object getValueAt (int row, int col) {
			int uni = getUni(row);
			boolean used = CharsUsed.isUsedRoman(uni) ||
				CharsUsed.isUsedGreek(uni);
			String glyph = getGlyph(uni);
			String insenStr = CharsetUtils.translateToInsensitive(glyph);
			int insen = (int)insenStr.charAt(0);
			switch (col) {
				case 0: return getHex(uni);
				case 1: return glyph;
				case 2: return CharsetUtils.translateUniToBeta(glyph);
				case 3: return used ? getHex(insen) : "";
				case 4: return used ? insenStr : "";
			}
			return null;
		}

		public String getColumnName (int col) {
			switch (col) {
				case 0: return "Unicode";
				case 1: return "Glyph";
				case 2: return "Beta Code";
				case 3: return "Insen Code";
				case 4: return "Insen Glyph";
			}
			return null;
		}

	}

	/**	Table cell renderer. */

	private static class MyRenderer extends DefaultTableCellRenderer {
		Font font;
		public MyRenderer () {
			super();
			font = new Font(fontFamily, 0, 24);
		}
		public Component getTableCellRendererComponent (JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int col)
		{
			super.getTableCellRendererComponent(table, value,
				isSelected, false, row, col);
			this.setHorizontalAlignment(SwingConstants.LEADING);
			this.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
			this.setFont(font);
			int uni = getUni(row);
			if (CharsUsed.isUsedRoman(uni) || CharsUsed.isUsedGreek(uni)) {
				this.setForeground(Color.black);
			} else {
				this.setForeground(Color.lightGray);
			}
			return this;
		}
	}

	/**	Creates the table panel.
	 *
	 *	@throws Exception
	 */

	private static JComponent createTablePanel ()
		throws Exception
	{
		MyTableModel model = new MyTableModel();
		MyRenderer renderer = new MyRenderer();
//		JTable table = new JTable(model);
		table = new XTable(model);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setGridColor(Color.darkGray);
		table.setShowGrid(true);
		table.setRowHeight(34);
		for (int col = 0; col < model.getColumnCount(); col++) {
			TableColumn column = table.getColumnModel().getColumn(col);
			column.setCellRenderer(renderer);
			column.setPreferredWidth(80);
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
		panel.add(scrollPane);
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		return panel;
	}

	/**	Rebuilds the window.
	 *
	 *	@throws Exception
	 */

	private static void rebuild ()
		throws Exception
	{
		JComponent samplePanel = createSamplePanel();
		samplePanel.setAlignmentY(Component.TOP_ALIGNMENT);

		JComponent tablePanel = createTablePanel();
		tablePanel.setAlignmentY(Component.TOP_ALIGNMENT);

		mainPanel.removeAll();
		mainPanel.add(samplePanel);
		mainPanel.add(tablePanel);

		mainPanel.revalidate();
		mainPanel.repaint();
	}

	/**	Displays the window.
	 *
	 *	@throws	Exception
	 */

	private static void display ()
		throws Exception
	{

		String[][] prefs = FontPrefsDialog.getFontPrefs();
		fontFamily = prefs[FontManager.WORK][TextParams.GREEK];
		prefs[FontManager.WORK][TextParams.ROMAN] = fontFamily;
		FontPrefsDialog.setFontPrefs(prefs);

		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		mainPanel.setBackground(Color.white);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		rebuild();

		JFrame window = new JFrame("WordHoard Character Set");
		window.setContentPane(mainPanel);
		window.setResizable(false);
		window.pack();
		Dimension screenSize = window.getToolkit().getScreenSize();
		Dimension windowSize = window.getSize();
		windowSize.height = screenSize.height - 35;
		window.setSize(windowSize);
		window.setLocation(new Point(20, 24));
		window.setVisible(true);
	}

	/**	Main program.
	 *
	 *	@param	args	Command line arguments.
	 */

	public static void main (String args[]) {
		try {
			getSampleText(args);
			SwingUtilities.invokeLater(
				new Runnable() {
					public void run () {
						try {
							display();
						} catch (Exception e) {
							e.printStackTrace();
							System.exit(1);
						}
					}
				}
			);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**	Find a printable component.
	 *
	 *	@return		A printable component.
	 */

	protected Component findPrintableComponent () {
		return table;
	}

	/**	Hides the default no-arg constructor.
	 */

	private CharSetDisplay () {
		throw new UnsupportedOperationException();
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

