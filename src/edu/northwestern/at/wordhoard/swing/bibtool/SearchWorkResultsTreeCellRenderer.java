package edu.northwestern.at.wordhoard.swing.bibtool;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.tree.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	The search results tree cell renderer.
 */

public class SearchWorkResultsTreeCellRenderer extends DefaultTreeCellRenderer {

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

	/**	True to render work grouping objects with dates following titles. */

	private boolean renderWorksWithDates;

	/**	Path string width. */

	private int pathStringWidth;

	/**	Creates a new search results tree cell renderer.
	 *
	 *	@param	font				Primary font.
	 *
	 *	@param	corpus				The corpus.
	 *
	 *	@param	scrollPane			The scroll pane.
	 */

	public SearchWorkResultsTreeCellRenderer (Font font, Corpus corpus,
		JScrollPane scrollPane)
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
		italicsFont = new Font(font.getName(), Font.ITALIC, font.getSize()+1);
		italicsFontMetrics = getFontMetrics(italicsFont);
		baseLine = fontMetrics.getLeading() + fontMetrics.getAscent();
		int pathCharWidth = pathFontMetrics.stringWidth("x");
//		pathStringWidth = pathCharWidth * (corpus.getMaxWordPathLength() + 2);
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
		Color background = selected ? getBackgroundSelectionColor() : Color.white;
		Color foreground = selected ? getTextSelectionColor() : Color.black;
		if( obj instanceof WorkPart )
		{
			foreground =
				((WorkPart)obj).isActive() ?
					getTextSelectionColor() :
					Color.lightGray;

			if ( foreground.equals( background ) )
			{
				foreground = Color.darkGray;
			}
		}
		g.setBackground(background);
		g.clearRect(r.x, r.y, r.width, r.height);
		g.setColor(foreground);
		if (obj instanceof Work) {
			paintWork(g, (Work)obj, width);
		} else if (obj instanceof WorkPart) {
			paintWorkPart(g, (WorkPart)obj);
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
		g.setFont(font);
		int numHits = node.getChildCount();
		String name = obj.toString();
		String str = numHits + " match" + (numHits == 1 ? "" : "es");
		str = str + " " + name;
		g.drawString(str, 0, baseLine);
	}

	/**	Paints a WorkPart.
	 *
	 *	@param	g		Graphics context.
	 *
	 *	@param	obj		Grouping object.
	 */

	private void paintWorkPart (Graphics2D g, WorkPart obj) {
		g.setFont(font);
		String name = obj.toString();
		g.drawString(name, 0, baseLine);
	}

	/**	Paints a cell for a work.
	 *
	 *	@param	g			Graphic context.
	 *
	 *	@param	work		Work.
	 *
	 *	@param	cellWidth	Cell width.
	 */

	private void paintWork (Graphics2D g, Work work, int cellWidth) {
		g.setFont(pathFont);
		String author = "";
		Collection authors = work.getAuthors();
		Iterator iterator = authors.iterator();
		while(iterator.hasNext()) {
			author += ((Author)iterator.next()).getName();
		}
		int authorWidth = boldFontMetrics.stringWidth(author);
		int authorLeft = 2;
		int authorRight = authorLeft + authorWidth;
		g.setFont(boldFont);
		g.drawString(author, authorLeft, baseLine);

		String title = work.getFullTitle();
		int titleWidth = italicsFontMetrics.stringWidth(title);
		int titleLeft = 2 + authorRight;
		int titleRight = titleLeft + titleWidth;
		g.setFont(italicsFont);
		g.drawString(title, titleLeft, baseLine);

		String dateString = null;
		PubYearRange pubDate = work.getPubDate();
		Integer earlyDate = pubDate == null ? null : pubDate.getStartYear();
		Integer lateDate = pubDate == null ? null : pubDate.getEndYear();
		if(earlyDate != lateDate) {
			dateString = "(" + earlyDate + "-" + lateDate + ")";
		} else if(earlyDate == null && lateDate == null) {
			dateString = "(Not dated)";
		} else if(earlyDate != null) {
			dateString = "(" + earlyDate + ")";
		} else if(lateDate != null) {
			dateString = "(" + lateDate + ")";
		} else {
				dateString= "(" + earlyDate + "-" + lateDate + ")";
		}

		int dateStringWidth = pathFontMetrics.stringWidth(dateString);
		int dateStringLeft = 2 + titleRight;
		int dateStringRight = dateStringLeft + dateStringWidth;
		g.setFont(pathFont);
		g.drawString(dateString, dateStringLeft, baseLine);
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

