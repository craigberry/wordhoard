package edu.northwestern.at.wordhoard.swing.querytool;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;

/**	The search results tree cell renderer.
 */

public class SearchResultsTreeCellRenderer extends DefaultTreeCellRenderer {

	/**	The tree node being rendered. */

	private DefaultMutableTreeNode node;

	/**	The model object being rendered. */

	private Object obj;

	/**	The scroll pane that contains the tree. */

	private JScrollPane scrollPane;

	/**	True if the cell is selected. */

	private boolean selected;

	/**	Font size. */

	private int fontSize;

	/**	Roman font info. */

	private FontInfo romanFontInfo;

	/**	Path font info. */

	private FontInfo pathFontInfo;

	/**	Cell height. */

	private int height;

	/**	Baseline. */

	private int baseline;

	/**	True to render work grouping objects with dates following titles. */

	private boolean renderWorksWithDates;

	/**	Path string width. */

	private int pathStringWidth;

	/**	Colocate preloader. */

	private ColocatePreloader colocatePreloader;

	/**	Font manager. */

	private FontManager fontManager;

	/**	Creates a new search results tree cell renderer.
	 *
	 *	@param	fontSize			Font size.
	 *
	 *	@param	corpus				The corpus.
	 *
	 *	@param	scrollPane			The scroll pane.
	 *
	 *	@param	colocatePreloader	Colocate preloader.
	 */

	public SearchResultsTreeCellRenderer (int fontSize, Corpus corpus,
		JScrollPane scrollPane, ColocatePreloader colocatePreloader)
	{
		this.fontSize = fontSize;
		this.scrollPane = scrollPane;
		this.colocatePreloader = colocatePreloader;
		fontManager = new FontManager();
		romanFontInfo = fontManager.getFontInfo(fontSize);
		pathFontInfo = fontManager.getMonospacedFontInfo(fontSize);
		int pathCharWidth = pathFontInfo.stringWidth("x");
		pathStringWidth = pathCharWidth * (corpus.getMaxWordPathLength() + 2);
		height = romanFontInfo.getHeight();
		baseline = romanFontInfo.getLeading() + romanFontInfo.getAscent();
	}

	/**	Sets the render works with dates option.
	 *
	 *	@param	renderWorksWithDates	True to render work grouping objects
	 *									with dates following titles.
	 */

	public void setRenderWorksWithDates (boolean renderWorksWithDates) {
		this.renderWorksWithDates = renderWorksWithDates;
	}

	/**	Gets the cell height.
	 *
	 *	@return		The cell height.
	 */

	public int getHeight () {
		return height;
	}

	/**	Returns a component configured to display the word.
	 *
	 *	@param	tree			The tree we're painting.
	 *
	 *	@param	value			The value to be painted.
	 *
	 *	@param	selected		True if the cell is selected.
	 *
	 *	@param	expanded		True if the cell is expanded.
	 *
	 *	@param	leaf			True if the cell is a leaf node.
	 *
	 *	@param	row				Row number.
	 *
	 *	@param	hasFocus		True if the cell has the focus.
	 *
	 *	@return		A component whose paint() method will render the
	 *				specified value.
	 */

	public Component getTreeCellRendererComponent (JTree tree, Object value,
		boolean selected, boolean expanded, boolean leaf, int row,
		boolean hasFocus)
	{
		this.selected = selected;
		node = (DefaultMutableTreeNode)value;
		obj = node.getUserObject();
		setPreferredSize(new Dimension(10000, height));
		return this;
	}

	/**	Paints the component.
	 *
	 *	@param	graphics		Graphics context.
	 */

	public void paint (Graphics graphics) {
		if (obj == null) return;
		Graphics2D g = (Graphics2D)graphics;
		Rectangle r = g.getClipBounds();
		int scrollPaneWidth = scrollPane.getWidth();
		int scrollBarWidth = scrollPane.getVerticalScrollBar().getWidth();
		int indent = getX();
		int width = scrollPaneWidth - scrollBarWidth - indent - 6;
		r.width = width;
		g.setClip(r.x, r.y, r.width, r.height);
		Color background = selected ? getBackgroundSelectionColor() :
			Color.white;
		Color foreground = selected ? getTextSelectionColor() :
			Color.black;
		g.setBackground(background);
		g.clearRect(r.x, r.y, r.width, r.height);
		g.setColor(foreground);
		if (obj instanceof Word) {
			paintKWIC(g, (Word)obj, width);
		} else {
			paintGroupingObject(g, (GroupingObject)obj);
		}
	}

	/**	Paints a grouping object.
	 *
	 *	@param	g		Graphics context.
	 *
	 *	@param	obj		Grouping object.
	 */

	private void paintGroupingObject (Graphics2D g, GroupingObject obj) {
		int numHits = node.getChildCount();
		String str = numHits + " match" + (numHits == 1 ? "" : "es");
		if (obj instanceof Work) {
			Work work = (Work)obj;
			float freq = work.getRelFreq(numHits);
			str = str + " (" + Formatters.formatFloat(freq, 2) + ")";
		}
		str = str + " " + obj.getReportPhrase() + " ";
		g.setFont(romanFontInfo.getFont());
		g.drawString(str, 0, baseline);
		int x = romanFontInfo.stringWidth(str);
		byte charset;
		if (renderWorksWithDates) {
			str = ((Work)obj).getShortTitleWithDate();
			charset = TextParams.ROMAN;
		} else {
			Spelling spelling = obj.getGroupingSpelling(numHits);
			str = spelling.getString();
			charset = spelling.getCharset();
		}
		FontInfo fontInfo = fontManager.getFontInfo(
			charset, fontSize);
		g.setFont(fontInfo.getFont());
		g.drawString(str, x, baseline);
	}

	/**	Paints a keyword-in-context (KWIC) cell for a word.
	 *
	 *	@param	g			Graphic context.
	 *
	 *	@param	word		Word.
	 *
	 *	@param	cellWidth	Cell width.
	 */

	private void paintKWIC (Graphics2D g, Word word, int cellWidth) {
		try {
			colocatePreloader.load(word, 10, 30);
		} catch (PersistenceException e) {
			Err.err(e);
		}
		g.setFont(pathFontInfo.getFont());
		g.drawString(word.getPath() + ": ", 0, baseline);
		Spelling wordSpelling = word.getSpelling();
		String wordString = wordSpelling.getString();
		byte wordCharset = wordSpelling.getCharset();
		FontInfo wordFontInfo = fontManager.getFontInfo(
			wordCharset, fontSize);
		FontInfo boldFontInfo = fontManager.getFontInfo(
			wordCharset, Font.BOLD, fontSize);
		int wordWidth = boldFontInfo.stringWidth(wordString);
		int wordLeft = (cellWidth + pathStringWidth - wordWidth)/2;
		int wordRight = wordLeft + wordWidth;
		g.setFont(boldFontInfo.getFont());
		g.drawString(wordString, wordLeft, baseline);
		int prefixLeft = wordLeft;
		Word prev = word;
		FontInfo prevFontInfo = wordFontInfo;
		int wid;
		while (true) {
			String str = prev.getPuncBefore();
			wid = prevFontInfo.stringWidth(str);
			if (prefixLeft - wid < pathStringWidth) break;
			prefixLeft -= wid;
			g.setFont(prevFontInfo.getFont());
			g.drawString(str, prefixLeft, baseline);
			prev = prev.getPrev();
			if (prev == null) break;
			Spelling prevSpelling = prev.getSpelling();
			prevFontInfo = fontManager.getFontInfo(
				prevSpelling.getCharset(), fontSize);
			str = prevSpelling.getString() + prev.getPuncAfter();
			wid = prevFontInfo.stringWidth(str);
			if (prefixLeft - wid < pathStringWidth) break;
			prefixLeft -= wid;
			g.setFont(prevFontInfo.getFont());
			g.drawString(str, prefixLeft, baseline);
		}
		String str = word.getPuncAfter();
		g.setFont(wordFontInfo.getFont());
		g.drawString(str, wordRight, baseline);
		int postfixLeft = wordRight + wordFontInfo.stringWidth(str);
		int postfixRight = postfixLeft;
		Word next = word;
		while (true) {
			next = next.getNext();
			if (next == null) break;
			Spelling nextSpelling = next.getSpelling();
			FontInfo nextFontInfo = fontManager.getFontInfo(
				nextSpelling.getCharset(), fontSize);
			str = next.getPuncBefore() + next.getSpelling();
			wid = nextFontInfo.stringWidth(str);
			if (postfixRight + wid > cellWidth) break;
			g.setFont(nextFontInfo.getFont());
			g.drawString(str, postfixLeft, baseline);
			postfixLeft += wid;
			postfixRight += wid;
			str = next.getPuncAfter();
			wid = wordFontInfo.stringWidth(str);
			int k = str.length()-1;
			while (k >= 0 && str.charAt(k) == ' ') k--;
			int widTrim = nextFontInfo.stringWidth(str.substring(0,k+1));
			if (postfixRight + widTrim > cellWidth) break;
			g.drawString(str, postfixLeft, baseline);
			postfixLeft += wid;
			postfixRight += wid;
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

