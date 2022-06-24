package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	Work summary panel.
 */
 
class WorkSummaryPanel extends JPanel {

	/**	Font size. */
	
	private int fontSize;
	
	/**	Max value width. */
	
	private int maxValueWidth;
	
	/**	Font manager. */
	
	private FontManager fontManager;

	/**	Roman font info. */
	
	private FontInfo romanFontInfo;
	
	/**	Roman font. */
	
	private Font romanFont;

	/**	Creates a new work summary panel.
	 *
	 *	@param	work			Work.
	 *
	 *	@param	fontSize		Font size.
	 *
	 *	@param	insets			Insets for labeled column.
	 *
	 *	@param	minLabelWidth	Min label width for labeled column.
	 *
	 *	@param	maxValueWidth	Max value width for labeled column.
	 */

	WorkSummaryPanel (Work work, int fontSize, 
		Insets insets, int minLabelWidth, int maxValueWidth) 
	{
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		this.fontSize = fontSize;
		this.maxValueWidth = maxValueWidth;
		fontManager = new FontManager();
		romanFontInfo = fontManager.getFontInfo(fontSize);
		romanFont = romanFontInfo.getFont();

		JLabel label = new JLabel("Summary and Counts");
		label.setFont(romanFont);
		label.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(label);
		
		LabeledColumn col = 
			new LabeledColumn(insets, romanFont, minLabelWidth, maxValueWidth);
		col.setBackground(Color.white);
		
		col.addPair("Full work title", work.getFullTitle());
		col.addPair("Short work title", work.getShortTitle());
		Collection authors = work.getAuthors();
		if (authors != null) addAuthors(col, authors);
		PubYearRange pubDate = work.getPubDate();
		if (pubDate != null) col.addPair("Date", pubDate.toString());
		int numWorkParts = work.getNumWorkPartsTree();
		col.addPair("Work parts",
			Formatters.formatIntegerWithCommas(numWorkParts));
		int numLines = work.getNumLines();
		col.addPair("Lines", 
			Formatters.formatIntegerWithCommas(numLines));
		int numWords = work.getNumWords();
		col.addPair("Words", 
			Formatters.formatIntegerWithCommas(numWords));
		
		col.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(col);
		
		setMaximumSize(getPreferredSize());
	}
	
	/**	Adds authors to a labeled column.
	 *
	 *	@param	col			Labeled column.
	 *
	 *	@param	authors		Authors.
	 */
	
	private void addAuthors (LabeledColumn col, Collection authors) {
		int numAuthors = authors.size();
		if (numAuthors == 0) return;
		JLabel label = new JLabel(numAuthors == 1 ? "Author:" : "Authors:");
		label.setFont(romanFont);
		JPanel labelBox = new JPanel();
		labelBox.setLayout(new BoxLayout(labelBox, BoxLayout.Y_AXIS));
		labelBox.add(label);
		labelBox.setBackground(Color.white);
		for (Iterator it = authors.iterator(); it.hasNext(); ) {
			Author author = (Author)it.next();
			Spelling name = author.getName();
			Spelling originalName = author.getOriginalName();
			Integer birthYear = author.getBirthYear();
			Integer deathYear = author.getDeathYear();
			Integer earliestWorkYear = author.getEarliestWorkYear();
			Integer latestWorkYear = author.getLatestWorkYear();
			TextLine line = new TextLine();
			if (name == null) {
				FontInfo fontInfo = fontManager.getFontInfo(
					originalName.getCharset(), fontSize);
				line.appendRun(originalName.getString(), fontInfo);
			} else {
				FontInfo fontInfo = fontManager.getFontInfo(
					name.getCharset(), fontSize);
				line.appendRun(name.getString(), fontInfo);
				if (originalName != null) {
					line.appendRun(" (", romanFontInfo);
					fontInfo = fontManager.getFontInfo(
						originalName.getCharset(), fontSize);
					line.appendRun(originalName.getString(), fontInfo);
					line.appendRun(")", romanFontInfo);
				}
			}
			if (birthYear == null) {
				if (deathYear == null) {
				} else {
					line.appendRun(" (-" + deathYear + ")",
						romanFontInfo);
				}
			} else {
				if (deathYear == null) {
					line.appendRun(" (" + birthYear + "-)",
						romanFontInfo);
				} else {
					line.appendRun(" (" + birthYear + "-" + deathYear + ")",
						romanFontInfo);
				}
			}
			if (earliestWorkYear != null)
				line.appendRun(" (Earliest work " + earliestWorkYear + ")",
					romanFontInfo);
			if (latestWorkYear != null)
				line.appendRun(" (Latest work " + latestWorkYear + ")",
					romanFontInfo);
			Text text = new Text(line);
			WrappedTextComponent textComponent = 
				new WrappedTextComponent(text, maxValueWidth);
			if (label == null) {
				col.addPair(" ", textComponent);
			} else {
				Dimension labelSize = label.getPreferredSize();
				Dimension textComponentSize = textComponent.getPreferredSize();
				if (labelSize.height < textComponentSize.height) 
					labelBox.add(Box.createVerticalStrut(
						textComponentSize.height - labelSize.height));
				col.addPair(labelBox, textComponent);
				label = null;
			}
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

