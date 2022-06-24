package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.swing.concordance.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

/**	Word forms panel.
 */

class WordFormsPanel extends JPanel {

	/**	Spelling info class. */

	private static class SpellingInfo {
		Spelling spelling;
		int freq;
	}

	/**	Part of speech info class. */

	private static class PosInfo {
		Pos pos;
		String posTag;
		int freq;
		ArrayList spellings = new ArrayList();
	}

	/**	Table row info class. */

	private class Info implements SearchDefaults {
		Pos pos;
		String posTag;
		int posFreq;
		Spelling spelling;
		int spellingFreq;
		public SearchCriterion getSearchDefault (Class cls) {
			if (cls.equals(Lemma.class)) {
				return lemma;
			} else if (cls.equals(Pos.class)) {
				return pos;
			} else if (cls.equals(Spelling.class)) {
				return new SpellingWithCollationStrength(spelling,
					Collator.PRIMARY);
			} else {
				if (lemma == null) return null;
				return lemma.getSearchDefault(cls);
			}
		}
	}

	/**	Font size. */

	private int fontSize;

	/**	Roman font. */

	private Font romanFont;

	/**	Roman font ascent. */

	private int romanFontAscent;

	/**	Array of row info for the table. */

	private Info[] infoArray;

	/**	Number of table rows. */

	private int n;

	/**	Font manager. */

	private FontManager fontManager;

	/**	Table. */

//	private JTable table;
	private XTable table;

	/**	The lemma. */

	private Lemma lemma;

	/**	Table model class. */

	private class MyTableModel extends AbstractTableModel {

		public int getRowCount () {
			return n;
		}

		public int getColumnCount () {
			return 2;
		}

		public Object getValueAt (int row, int col) {
			Info info = infoArray[row];
			switch (col) {
				case 0:
					String prevPosTag = row == 0 ? " " :
						infoArray[row-1].posTag;
					return prevPosTag.equals(info.posTag) ? " " :
						info.posTag + " (" + info.posFreq + ")";
				case 1:
					return info.spelling.getString() +
						" (" + info.spellingFreq + ")";
			}
			return null;
		}

		public String getColumnName (int col) {
			switch (col) {
				case 0: return "Part of Speech";
				case 1: return "Spelling";
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
			Info info = infoArray[row];
			byte spellingCharset = info.spelling.getCharset();
			FontInfo spellingFontInfo = fontManager.getFontInfo(
				spellingCharset, fontSize);
			int spellingFontAscent = spellingFontInfo.getAscent();
			if (col == 0) {
				this.setHorizontalAlignment(SwingConstants.TRAILING);
				int topMargin = spellingFontAscent > romanFontAscent ?
					spellingFontAscent - romanFontAscent : 0;
				this.setBorder(BorderFactory.createEmptyBorder(
					topMargin, 0, 0, 10));
				this.setFont(romanFont);
			} else {
				this.setHorizontalAlignment(SwingConstants.LEADING);
				int topMargin = romanFontAscent > spellingFontAscent ?
					romanFontAscent - spellingFontAscent : 0;
				this.setBorder(BorderFactory.createEmptyBorder(
					topMargin, 10, 0, 5));
				this.setFont(spellingFontInfo.getFont());
			}
			return this;
		}
	}

	/**	Creates a new word forms panel.
	 *
	 *	@param	lemma				Lemma.
	 *
	 *	@param	corpus				Corpus.
	 *
	 *	@param	fontSize			Font size.
	 *
	 *	@param	width				Width for wrapped paragraph.
	 *
	 *	@param	parentWindow		Parent window.
	 *
	 *	@throws	PersistenceException
	 */

	WordFormsPanel (final Lemma lemma, final Corpus corpus,
		int fontSize, int width, final AbstractWindow parentWindow)
			throws PersistenceException
	{
		this.fontSize = fontSize;
		this.lemma = lemma;
		PersistenceManager pm = WordHoard.getPm();
		MajorWordClass majorWordClass = lemma.getWordClass().getMajorWordClass();
		String corpusTitle = corpus.getTitle();
		Spelling lemmaSpelling = lemma.getSpelling();
		byte posType = corpus.getPosType();

		fontManager = new FontManager();
		FontInfo romanFontInfo = fontManager.getFontInfo(fontSize);
		romanFont = romanFontInfo.getFont();
		romanFontAscent = romanFontInfo.getAscent();

		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		TextLine line = new TextLine();
		line.appendRun("The " + majorWordClass.toString() +
			" \u201C", romanFontInfo);
		FontInfo lemmaFontInfo = fontManager.getFontInfo(
			lemmaSpelling.getCharset(), fontSize);
		line.appendRun(lemmaSpelling.getString(), lemmaFontInfo);
		StringBuffer buf = new StringBuffer();
		buf.append("\u201D occurs in ");
		buf.append(corpusTitle);
		buf.append(" with the following parts of speech and spellings. ");
		buf.append("The numbers in parentheses (nn) are counts " );
		buf.append("in the corpus. To get a concordance, double-click the ");
		buf.append("part of speech or spelling. For descriptions of the ");
		buf.append("parts of speech, use the \u201C");
		buf.append(" Parts of Speech\u201D command ");
		buf.append("in the \u201CWindows\u201D menu.");
		line.appendRun(buf.toString(), romanFontInfo);
		Text text = new Text(line);
		WrappedTextComponent textComponent =
			new WrappedTextComponent(text, width);
		textComponent.setBackground(Color.white);
		textComponent.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
		textComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(textComponent);

		Collection wfcCollection = pm.getLemmaPosSpellingCounts(lemma, corpus);
		n = wfcCollection.size();
		HashMap posInfoMap = new HashMap();
		for (Iterator it = wfcCollection.iterator(); it.hasNext(); ) {
			LemmaPosSpellingCounts lps = (LemmaPosSpellingCounts)it.next();
			Pos pos = lps.getPos();
			PosInfo posInfo = (PosInfo)posInfoMap.get(pos);
			if (posInfo == null) {
				posInfo = new PosInfo();
				posInfo.pos = pos;
				posInfo.posTag = pos.getTag();
				posInfoMap.put(pos, posInfo);
			}
			posInfo.freq += lps.getFreq();
			SpellingInfo spellingInfo = new SpellingInfo();
			spellingInfo.spelling = lps.getSpelling();
			spellingInfo.freq = lps.getFreq();
			posInfo.spellings.add(spellingInfo);
		}
		Collection posInfoCollection = posInfoMap.values();
		infoArray = new Info[n];
		int index = 0;
		for (Iterator it1 = posInfoCollection.iterator(); it1.hasNext(); ) {
			PosInfo posInfo = (PosInfo)it1.next();
			for (Iterator it2 = posInfo.spellings.iterator(); it2.hasNext(); ) {
				SpellingInfo spellingInfo = (SpellingInfo)it2.next();
				Info info = new Info();
				info.pos = posInfo.pos;
				info.posTag = posInfo.posTag;
				info.posFreq = posInfo.freq;
				info.spelling = spellingInfo.spelling;
				info.spellingFreq = spellingInfo.freq;
				infoArray[index++] = info;
			}
		}
		Arrays.sort(infoArray,
			new Comparator() {
				public int compare (Object o1, Object o2) {
					Info info1 = (Info)o1;
					Info info2 = (Info)o2;
					if (info1.posFreq < info2.posFreq) return +1;
					if (info1.posFreq > info2.posFreq) return -1;
					int k = info1.posTag.compareTo(info2.posTag);
					if (k < 0) return -1;
					if (k > 0) return +1;
					if (info1.spellingFreq < info2.spellingFreq) return +1;
					if (info1.spellingFreq > info2.spellingFreq) return -1;
					return 0;
				}
			}
		);
		MyTableModel model = new MyTableModel();
		MyRenderer renderer = new MyRenderer();
//		table = new JTable(model);
		table = new XTable(model);
		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setFont(romanFont);
		table.setGridColor(Color.lightGray);
		table.setShowGrid(true);
		table.setRowHeight(18);
		for (int col = 0; col < model.getColumnCount(); col++) {
			TableColumn column = table.getColumnModel().getColumn(col);
			column.setCellRenderer(renderer);
		}
		table.addMouseListener(
			new MouseAdapter() {
				public void mouseClicked (MouseEvent event) {
					if (event.getClickCount() <= 1) return;
					int row = table.getSelectedRow();
					if (row < 0) return;
					int col = table.getSelectedColumn();
					if (col < 0) return;
					try {
						Info info = infoArray[row];
						SearchCriteria sc = col == 0 ?
							new SearchCriteria(corpus, lemma, info.pos,
								null, 0) :
							new SearchCriteria(corpus, lemma, info.pos,
								info.spelling, Collator.SECONDARY);
						new ConcordanceWindow(sc, parentWindow);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
//		JScrollPane scrollPane = new JScrollPane(table);
		XScrollPane scrollPane = new XScrollPane(table);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.getViewport().setBackground(Color.white);
		add(scrollPane);
	}

	/**	Gets the search defaults object.
	 *
	 *	@return		Search defaults object.
	 */

	SearchDefaults getSearchDefaults () {
		int row = table.getSelectedRow();
		if (row < 0) return lemma;
		return infoArray[row];
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
	 *	@return		A saveable component.
	 */

	protected Component findSaveableComponent () {
		return table;
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

