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
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.querytool.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;


/**	The search results tree cell renderer.
 */

public class SearchCriteriaTreeCellRenderer extends DefaultTreeCellRenderer {

	/**	The tree node being rendered. */

	private DefaultMutableTreeNode node;

	/**	The model object being rendered. */

	private Object obj;

	/**	The scroll pane that contains the tree. */

	private JScrollPane scrollPane;

	/**	True if the cell is selected. */

	private boolean selected;

	/**	Primary font. */

	private Font font;

	/**	Primary font metrics. */

	private FontMetrics fontMetrics;

	/**	Cell height. */

	private int height;

	/**	Monospaced path font. */

	private Font pathFont;

	/**	Monospaced path font metrics. */

	private FontMetrics pathFontMetrics;

	/**	Boldface font. */

	private Font boldFont;

	/**	Boldface font metrics. */

	private FontMetrics boldFontMetrics;

	/**	Italics font. */

	private Font italicsFont;

	/**	Italics font metrics. */

	private FontMetrics italicsFontMetrics;

	/**	Baseline y coordinate. */

	private int baseLine;

	/**	Path string width. */

	private int pathStringWidth;

	/**	Creates a new search results tree cell renderer.
	 *
	 *	@param	font				Primary font.
	 *
	 *	@param	scrollPane			The scroll pane.
	 */

	public SearchCriteriaTreeCellRenderer (Font font, JScrollPane scrollPane)
	{
		super();
		this.font = font;
		this.scrollPane = scrollPane;
		fontMetrics = getFontMetrics(font);
		height = fontMetrics.getHeight();
		pathFont = new Font("Monospaced", Font.PLAIN, font.getSize());
		pathFontMetrics = getFontMetrics(pathFont);
		boldFont = new Font(font.getName(), Font.BOLD, font.getSize()+1);
		boldFontMetrics = getFontMetrics(boldFont);
		italicsFont = new Font(font.getName(), Font.ITALIC, font.getSize()-1);
		italicsFontMetrics = getFontMetrics(italicsFont);
		baseLine = fontMetrics.getLeading() + fontMetrics.getAscent();
		int pathCharWidth = pathFontMetrics.stringWidth("x");
	}

	/**	Gets the cell height.
	 *
	 *	@return		The cell height.
	 */

	public int getHeight () {
		return height;
	}

	/**	Returns a component configured to display the work.
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
/*
		if(obj!=null) {
			System.out.println(getClass().getName() + ":getTreeCellRendererComponent " + obj.getClass().getName());
		} else {
			System.out.println(getClass().getName() + ":getTreeCellRendererComponent obj is null!!");
		}
		*/
		setPreferredSize(new Dimension(10000, height));
		return this;
	}

	/**	Paints the component.
	 *
	 *	@param	graphics		Graphics context.
	 */

	public void paint (Graphics graphics)  {
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
		if (obj instanceof SearchCriteriaTypedSet) {
			paintSearchCriteriaTypedSet(g, (SearchCriteriaTypedSet)obj, width);
		} else if (obj instanceof GroupingObject) {
			paintGroupingObject(g, (GroupingObject)obj, width);
		} else if (obj instanceof SpellingWithCollationStrength) {
			paintGroupingObject(g, ((SpellingWithCollationStrength)obj).getSpelling(), width);
		} else if (obj instanceof LemPos) {
			paintGroupingObject(g, ((LemPos)obj).getStandardSpelling(), width);
		} else if (obj instanceof SearchResultLemmaSearch) {
			paintLemmaSearchResult(g, (SearchResultLemmaSearch) obj, width);
		} else if (obj instanceof SearchCriterion) {
			paintSearchCriterion(g, (SearchCriterion)obj, width);
		}
	}

	/**	Paints a SearchCriteriaTypedSet
	 *
	 *	@param	g			Graphic context.
	 *
	 *	@param	SearchCriteriaTypedSet		scts.
	 *
	 *	@param	cellWidth	Cell width.
	 */

	private void paintSearchCriteriaTypedSet (Graphics2D g, 
		SearchCriteriaTypedSet item, int cellWidth) 
	{
		g.setFont(pathFont);
		int itemLeft = 2;
		if(item.getSearchCriterionClassname().equals("Work") || 
			item.getSearchCriterionClassname().equals("CollectionFrequency")  || 
			item.getSearchCriterionClassname().equals("DocFrequency")) 
		{
			String label = item.getSearchCriterionClassname() + " (" + 
				item.getBoolRelationship() + ") ";
			g.drawString(label , itemLeft, baseLine);
			int w = pathFontMetrics.stringWidth(label);
			g.setFont(italicsFont);
//			g.drawString("control-click to change", w, baseLine);
		} else if(item.getSearchCriterionClassname().equals(
			"SpellingWithCollationStrength")) {
			g.drawString("Spelling", itemLeft, baseLine);
		} else {
			g.drawString(item.getSearchCriterionClassname(), itemLeft, 
				baseLine);
		}
	}

	/**	Paints a Grouping Object
	 *
	 *	@param	g			Graphic context.
	 *
	 *	@param	GroupingObject		groupingObject.
	 *
	 *	@param	cellWidth	Cell width.
	 */

	private void paintGroupingObject (Graphics2D g, GroupingObject item, int cellWidth) {
		g.setFont(pathFont);
		String itemString = item.getGroupingSpelling(1).toString();
		int itemLeft = 2;
		g.drawString(itemString, itemLeft, baseLine);
	}

	/**	Paints a general SearchCriterion
	 *
	 *	@param	g			Graphic context.
	 *
	 *	@param	SearchCriterion		searchCriterion.
	 *
	 *	@param	cellWidth	Cell width.
	 */

	private void paintSearchCriterion(Graphics2D g, SearchCriterion item, int cellWidth) {
		g.setFont(pathFont);
		String itemString = item.toString();
		int itemLeft = 2;
		g.drawString(itemString, itemLeft, baseLine);
	}


	/**	Paints a lemma search result
	 *
	 *	@param	g			Graphic context.
	 *
	 *	@param	SearchResultLemmaSearch		search result.
	 *
	 *	@param	cellWidth	Cell width.
	 */

	private void paintLemmaSearchResult (Graphics2D g, SearchResultLemmaSearch item, int cellWidth) {
		g.setFont(pathFont);
		String itemString = item.getLemma().toString() + " (" + item.getCount() + ")" + " (" + item.getDocCount() + ")";
		int itemLeft = 2;
		g.drawString(itemString, itemLeft, baseLine);
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

