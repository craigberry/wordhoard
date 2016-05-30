package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.awt.*;

/**	Wrapped text.
 *
 *	<p>In the persistent object model, lines are not word-wrapped to the
 *	right margin. While most lines are short and fit within the margin
 *	without wrapping, this is not always the case. Examples of long lines
 *	include title page publication statements, many of the stage directions
 *	in Shakespeare, and lines in Chaucer's "Melibee" Canterbury Tale.
 *
 *	<p>This class performs word wrapping to break long lines into separate
 *	lines for presentation. It also provides methods for converting text
 *	locations and ranges between wrapped coordinates (view coordinates) and
 *	unwrapped coordinates (model coordinates).
 *
 *	<p>Marginalia, if any, are also word wrapped.
 *
 *	<p>Word wrapping is done in the client, not in the build programs.
 *	On different operating systems using different fonts, line breaks may
 *	occur at different locations in wrapped lines. 
 */
 
public class WrappedText extends Text {

	/**	The unwrapped text. */
	
	private Text unwrappedText;
	
	/**	Array of coordinate mapping information for the wrapped lines.
	 *
	 *	<p>If k is the line index of a wrapped line, then coordInfo[k]
	 *	is the location in the unwrapped text of the first character of
	 *	the wrapped line. 
	 *
	 *	<p>For example, wrapped line number 143 might start at character 
	 *	79 of unwrapped line number 129. In this case we would have
	 *	coordInfo[143] = new TextLocation(129, 79).
	 */
	
	private TextLocation[] coordInfo;
	
	/**	Creates new wrapped text.
	 *
	 *	<p>On entry, the font information must be set for all the runs
	 *	in the unwrapped text, and the widths must be set for all the 
	 *	runs and lines in the unwrapped text. See
	 *	{@link edu.northwestern.at.wordhoard.swing.text.FontManager#initText
	 *	FontManager.initText}.
	 *
	 *	<p>On exit, the font information, width information, and vertical 
	 *	positioning information are set for all the runs and lines in the
	 *	wrapped text.
	 *
	 *	@param	unwrappedText		Unwrapped text.
	 *
	 *	@param	rightMargin			Right margin in pixels.
	 *
	 *	@param	marginaliaWidth		Width in pixels of marginalia, or 0
	 *								if text does not have marginalia.
	 *
	 *	@throws	Exception			Word longer than right margin.
	 */
	
	public WrappedText (Text unwrappedText, int rightMargin,
		int marginaliaWidth) 
			throws Exception
	{
		super(unwrappedText.hasLineNumbers(), unwrappedText.hasMarginalia());
		this.unwrappedText = unwrappedText;
		TextLine[] lines = unwrappedText.getLines();
		ArrayList coordInfoList = new ArrayList();
		for (int i = 0; i < lines.length; i++) {
			TextLine line = lines[i];
			line = (TextLine)line.clone();
			int indentation = line.getIndentation();
			boolean leftJustified = line.getJustification() == TextLine.LEFT;
			int wrapWidth = rightMargin;
			if (leftJustified) wrapWidth -= indentation;
			if (marginaliaWidth == 0 && line.getWidth() <= wrapWidth) {
				copyLine(line);
				TextLocation loc = new TextLocation(i, 0);
				coordInfoList.add(loc);
			} else {
				wrapLine(line, i, wrapWidth, marginaliaWidth, coordInfoList);
			}
		}
		finalize();
		computeVerticalPositioningInformation();
		coordInfo = (TextLocation[])coordInfoList.toArray(
			new TextLocation[coordInfoList.size()]);
	}
	
	/**	Creates new wrapped text without marginalia.
	 *
	 *	<p>On entry, the font information must be set for all the runs
	 *	in the unwrapped text, and the widths must be set for all the 
	 *	runs and lines in the unwrapped text. See
	 *	{@link edu.northwestern.at.wordhoard.swing.text.FontManager#initText
	 *	FontManager.initText}.
	 *
	 *	<p>On exit, the font information, width information, and vertical 
	 *	positioning information are set for all the runs and lines in the
	 *	wrapped text.
	 *
	 *	@param	unwrappedText		Unwrapped text.
	 *
	 *	@param	rightMargin			Right margin in pixels.
	 *
	 *	@throws	Exception			Word longer than right margin.
	 */
	
	public WrappedText (Text unwrappedText, int rightMargin) 
		throws Exception
	{
		this(unwrappedText, rightMargin, 0);
	}
	
	/**	Wraps one line.
	 *
	 *	@param	line				Line.
	 *
	 *	@param	index				Index of line in unwrapped text.
	 *
	 *	@param	wrapWidth			Max width in pixels for wrapped lines.
	 *
	 *	@param	marginaliaWidth		Width in pixels of marginalia, or 0
	 *								if text does not have marginalia.
	 *
	 *	@param	coordInfoList		List of coordinate mapping information.
	 *
	 *	@throws	Exception			Word longer than right margin.
	 */
	 
	private void wrapLine (TextLine line, int index, int wrapWidth,
		int marginaliaWidth, ArrayList coordInfoList) 
			throws Exception
	{
		if (marginaliaWidth > 0) {
			Text unwrappedMarginalia = line.getMarginalia();
			if (unwrappedMarginalia != null) {
				Text wrappedMarginalia = 
					new WrappedText(unwrappedMarginalia, marginaliaWidth);
				line.setMarginalia(wrappedMarginalia);
			}
		}
		String str = line.getText();
		char[] c = str.toCharArray();
		int len = c.length;
		if (len == 0) {
			copyLine(line);
			TextLocation loc = new TextLocation(index, 0);
			coordInfoList.add(loc);
			return;
		}
		TextRun[] runs = line.getRuns();
		byte justification = line.getJustification();
		int indentation = line.getIndentation();
		int number = line.getNumber();
		String label = line.getLabel();
		String stanzaLabel = line.getStanzaLabel();
		Text marginalia = line.getMarginalia();
		int start = 0;
		while (start < len) {
			int pos = start;
			int brk = pos;
			int lineWidth = 0;
			while (pos < len) {
				while (pos < len && c[pos] == ' ') pos++;
				while (pos < len && c[pos] != ' ') pos++;
				int width = getWidth(runs, start, pos);
				if (width >= wrapWidth) break;
				brk = pos;
			}
			if (brk == start) {
				pos = start;
				brk = pos;
				lineWidth = 0;
				while (pos < len) {
					pos++;
					int width = getWidth(runs, start, pos);
					if (width >= wrapWidth) break;
					brk = pos;
				}
			}
			if (brk == start) throw new Exception(
				"Error in text: character longer than right margin!");
			TextLine newLine = new TextLine(justification, indentation,
				number, label, stanzaLabel);
			newLine.setMarginalia(marginalia);
			appendRuns(newLine, runs, start, brk);
			copyLine(newLine);
			TextLocation loc = new TextLocation(index, start);
			coordInfoList.add(loc);
			number = 0;
			label = null;
			marginalia = null;
			start = brk;
			while (start < len && c[start] == ' ') start++;
		}
	}
	
	/**	Gets the width of a substring of a line.
	 *
	 *	@param	runs		Array of runs in line.
	 *
	 *	@param	start		Starting character offset in line.
	 *
	 *	@param	end			Ending character offset in line.
	 *
	 *	@return				Width of substring in pixels.
	 */
	
	private int getWidth (TextRun[] runs, int start, int end) {
		int i = 0;
		while (true) {
			TextRun run = runs[i];
			int len = run.getLength();
			if (start < len) break;
			start -= len;
			end -= len;
			i++;
		}
		int width = 0;
		while (end > 0) {
			TextRun run = runs[i];
			int len = run.getLength();
			int k = end;
			if (len < k) k = len;
			if (start == 0 && k == len) {
				width += run.getWidth();
			} else {
				width += run.getSubstringWidth(start, k);
			}
			start = 0;
			end -= len;
			i++;
		}
		return width;
	}
	
	/**	Appends runs for a substring to a new wrapped line.
	 *
	 *	@param	line	New wrapped line.
	 *
	 *	@param	runs	Array of runs in unwrapped line.
	 *
	 *	@param	start	Starting character offset in unwrapped line.
	 *
	 *	@param	end		Ending character offset in unwrapped line.
	 */
	
	private void appendRuns (TextLine line, TextRun[] runs, int start,
		int end)
	{
		int i = 0;
		while (true) {
			TextRun run = runs[i];
			int len = run.getLength();
			if (start < len) break;
			start -= len;
			end -= len;
			i++;
		}
		int lineWidth = 0;
		while (end > 0) {
			TextRun run = runs[i];
			int len = run.getLength();
			int k = end;
			if (len < k) k = len;
			if (start == 0 && k == len) {
				line.appendRun(run);
				lineWidth += run.getWidth();
			} else {
				String str = run.getText();
				str = str.substring(start, k);
				byte charset = run.getCharset();
				byte size = run.getSize();
				int style = run.getStyle();
				FontInfo fontInfo = run.getFontInfo();
				Color color = run.getColor();
				TextRun newRun = new TextRun(str, charset, size);
				newRun.setStyle(style);
				newRun.setFontInfo(fontInfo);
				newRun.setColor(color);
				line.appendRun(newRun);
				lineWidth += newRun.getWidth();
			}
			start = 0;
			end -= len;
			i++;
		}
		line.setWidth(lineWidth);
	}
	
	/**	Converts a location from derived to base coordinates.
	 *
	 *	<p>See the package documentation for more details on derived
	 *	and base coordinate systems.
	 *
	 *	@param	loc		Text location in derived coordinates.
	 *
	 *	@return			Text location in base coordinates.
	 */
	 
	public TextLocation derivedToBase (TextLocation loc) {
		int index = loc.getIndex();
		int offset = loc.getOffset();
		if (index < 0 || index >= coordInfo.length) {
			index = -1;
			offset = 0;
		} else {
			TextLocation lineStart = coordInfo[index];
			index = lineStart.getIndex();
			offset = lineStart.getOffset() + offset;
		}
		TextLocation unwrappedLoc = new TextLocation(index, offset);
		return unwrappedText.derivedToBase(unwrappedLoc);
	}
	
	/**	Converts a location from base to derived coordinates.
	 *
	 *	<p>See the package documentation for more details on derived
	 *	and base coordinate systems.
	 *
	 *	@param	loc		Text location in base cooordinates.
	 *
	 *	@return			Text location in derived coordinates.
	 */
	 
	public TextLocation baseToDerived (TextLocation loc) {
		loc = unwrappedText.baseToDerived(loc);
		int k = Arrays.binarySearch(coordInfo, loc);
		if (k < 0) k = -k-2;
		TextLocation lineStart = coordInfo[k];
		return new TextLocation(k, loc.getOffset()-lineStart.getOffset());
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

