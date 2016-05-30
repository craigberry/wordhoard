package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.awt.*;

/**	A line of text.
 *
 *	<p>A line is a list of style runs ({@link
 *	edu.northwestern.at.wordhoard.model.text.TextRun} objects)
 *	plus line attributes: the line justification (left, center, or right),
 *	indentation, numeric and string versions of the line number (if any), an optional
 *	stanza label if this is the first line of a stanza, and marginalia
 *	(if any).
 *
 *	<p>Centering and right justification override indentation - the indentation attribute
 *	is used only with left justification.
 *
 *	<p>Marginalia are short pieces of text that are rendered in the margin.
 *	They are of type {@link edu.northwestern.at.wordhoard.model.text.Text Text},
 *	so they may contain multiple lines and multiple runs.
 *	They should not contain line numbers or nested marginalia (if they do, 
 *	they are not rendered).
 *
 *	<p>Lines must be finalized by calling the {@link #finalize finalize}
 *	method before they can be serialized or used. Some methods are
 *	intended for use during construction, and can only be called before
 *	finalization. Other methods are intended for use only after finalization.
 *
 *	<p>Lines have a "marked" attribute that is set and used by the client.
 *	Marked lines are drawn with a marker character in the left margin.
 */

public class TextLine implements Cloneable {

	/**	Left justification. */
	
	public static final byte LEFT = 0;
	
	/**	Center justification. */
	
	public static final byte CENTER = 1;
	
	/**	Right justification. */
	
	public static final byte RIGHT = 2;

	/**	Array of style runs, or null if not yet finalized. */
	
	private TextRun[] runs;
	
	/**	Justification of the line (LEFT, CENTER, or RIGHT). */
	
	private byte justification;
	
	/**	Indentation in pixels of the line. */
	
	private int indentation;
	
	/**	The integer line number. */
	
	private int number;
	
	/**	The line number label. */
	
	private String label;
	
	/**	The stanza label. */
	
	private String stanzaLabel;
	
	/**	Marginalia, or null if none. */
	
	private Text marginalia;
	
	/**	List of style runs, or null if finalized. 
	 *	(Used only during construction - not serialized.) 
	 */
	
	private ArrayList runList = new ArrayList();
	
	/**	The width of the line in pixels, or -1 if not yet set.
	 *	(Used only in client - not serialized.) 
	 */
	
	private int width = -1;
	
	/**	The height of the line in pixels, or -1 if not yet set.
	 *	(Used only in client - not serialized.)
	 */
	 
	private int height = -1;
	
	/**	The leading of the line in pixels, or -1 if not yet set.
	 *	(Used only in client - not serialized.)
	 */
	 
	private int leading = -1;
	
	/**	The ascent of the line in pixels, or -1 if not yet set.
	 *	(Used only in client - not serialized.)
	 */
	 
	private int ascent = -1;
	
	/**	The descent of the line in pixels, or -1 if not yet set.
	 *	(Used only in client - not serialized.)
	 */
	 
	private int descent = -1;
	
	/**	True if the line is marked.
	 *	(Used only in client - not serialized.)
	 */
	 
	private boolean marked = false;
	
	/**	Creates a new line.
	 *
	 *	<p>The new line initially contains no runs or marginalia.
	 *
	 *	@param	justification	Justification (LEFT, CENTER, or RIGHT).
	 *
	 *	@param	indentation		Indentation in pixels.
	 *
	 *	@param	number			Integer line number.
	 *
	 *	@param	label			Line number label.
	 *
	 *	@param	stanzaLabel		Stanza label.
	 */
	 
	public TextLine (byte justification, int indentation, int number, 
		String label, String stanzaLabel) 
	{
		this.justification = justification;
		this.indentation = indentation;
		this.number = number;
		this.label = label;
		this.stanzaLabel = stanzaLabel;
	}
	
	/**	Creates a new line.
	 *
	 *	<p>The new line initially contains no runs, marginalia, or
	 *	stanza label.
	 *
	 *	@param	justification	Justification (LEFT, CENTER, or RIGHT).
	 *
	 *	@param	indentation		Indentation in pixels.
	 *
	 *	@param	number			Integer line number.
	 *
	 *	@param	label			Line number label.
	 */
	 
	public TextLine (byte justification, int indentation, int number, 
		String label) 
	{
		this(justification, indentation, number, label, null);
	}
	
	/**	Creates a new  line.
	 *
	 *	<p>The new line initially contains no runs, line number or label,
	 *	stanza label, or marginalia.
	 *
	 *	@param	justification	Justification (LEFT, CENTER, or RIGHT).
	 *
	 *	@param	indentation		Indentation in pixels.
	 */
	 
	public TextLine (byte justification, int indentation) {
		this(justification, indentation, 0, null);
	}
	
	/**	Creates a new indented line.
	 *
	 *	<p>The new line has no numbers, labels, or marginalia, and initially 
	 *	contains no runs.
	 *
	 *	@param	indentation		Indentation in pixels.
	 */
	 
	public TextLine (int indentation) {
		this(LEFT, indentation, 0, null);
	}
	
	/**	Create a new plain line.
	 *
	 *	<p>The new line has no numbers, labels, or marginalia, is left justified
	 *	with no indentation, and initially contains no runs.
	 */
	 
	public TextLine () {
		this(LEFT, 0, 0, null);
	}
	
	/**	Creates a new line with a single run.
	 *
	 *	<p>The new line has no stanza label or marginalia.
	 *
	 *	@param	text			Text.
	 *
	 *	@param	charset			Character set.
	 *
	 *	@param	size			Font size.
	 *
	 *	@param	bold			True if bold.
	 *
	 *	@param	italic			True if italic.
	 *
	 *	@param	justification	Justification (LEFT, CENTER, or RIGHT).
	 *
	 *	@param	indentation		Indentation in pixels.
	 *
	 *	@param	number			Integer line number.
	 *
	 *	@param	label			Line number label.
	 *
	 *	@param	stanzaLabel		Optional stanza label if first line of
	 *							stanza, else null.
	 */
	 
	public TextLine (String text, byte charset, byte size, boolean bold, 
		boolean italic, byte justification, int indentation, int number, 
		String label, String stanzaLabel) 
	{
		this(justification, indentation, number, label, stanzaLabel);
		appendRun(text, charset, size, bold, italic);
	}
	
	/**	Creates a new line with a single run.
	 *
	 *	<p>The new line has no stanza label or marginalia.
	 *
	 *	@param	text			Text.
	 *
	 *	@param	charset			Character set.
	 *
	 *	@param	size			Font size.
	 *
	 *	@param	bold			True if bold.
	 *
	 *	@param	italic			True if italic.
	 *
	 *	@param	justification	Justification (LEFT, CENTER, or RIGHT).
	 *
	 *	@param	indentation		Indentation in pixels.
	 *
	 *	@param	number			Integer line number.
	 *
	 *	@param	label			Line number label.
	 */
	 
	public TextLine (String text, byte charset, byte size, boolean bold, 
		boolean italic, byte justification, int indentation, int number, 
		String label) 
	{
		this(justification, indentation, number, label);
		appendRun(text, charset, size, bold, italic);
	}
	
	/**	Creates a new line with font information.
	 *
	 *	<p>The line contains a single plain run in the Roman character set,
	 *	is left justified with no indentation, and has no line number, labels, or
	 *	marginalia.
	 *
	 *	<p>The line is finalized, and width and vertical positioning 
	 *	information are computed for the line.
	 *
	 *	@param	str			String.
	 *
	 *	@param	fontInfo	Font info.
	 */

	public TextLine (String str, FontInfo fontInfo) {
		this();
		TextRun run = new TextRun(str, TextParams.ROMAN, (byte)0);
		run.setFontInfo(fontInfo);
		appendRun(run);
		finalize();
		computeWidth();
		computeVerticalPositioningInformation();
	}
	
	/**	Gets the marginalia.
	 *
	 *	@return		The marginalia, or null if none.
	 */
	 
	public Text getMarginalia () {
		return marginalia;
	}
	
	/**	Sets the marginalia.
	 *
	 *	@param	marginalia 		Marginalia.
	 */
	 
	public void setMarginalia (Text marginalia) {
		this.marginalia = marginalia;
	}

	/**	Appends a run to a line.
	 *
	 *	<p>This method can only be used during construction, before the
	 *	line is finalized. If it is called after finalization a null 
	 *	pointer exception is thrown.
	 *
	 *	@param	run				Text run.
	 */
	 
	public void appendRun (TextRun run) {
		runList.add(run);
	}
	
	/**	Appends a run to a line.
	 *
	 *	<p>This method can only be used during construction, before the
	 *	line is finalized. If it is called after finalization a null 
	 *	pointer exception is thrown.
	 *
	 *	@param	text			Text.
	 *
	 *	@param	charset			Character set.
	 *
	 *	@param	size			Font size.
	 *
	 *	@param	bold			True if bold.
	 *
	 *	@param	italic			True if italic.
	 */
	 
	public void appendRun (String text, byte charset, byte size, boolean bold, 
		boolean italic)
	{
		appendRun(new TextRun(text, charset, size, bold, italic));
	}
	
	/**	Appends a run with font information to a line.
	 *
	 *	<p>This method can only be used during construction, before the
	 *	line is finalized. If it is called after finalization a null 
	 *	pointer exception is thrown.
	 *
	 *	@param	text			Text.
	 *
	 *	@param	fontInfo		Font info.
	 */
	 
	public void appendRun (String text, FontInfo fontInfo) {
		runList.add(new TextRun(text, fontInfo));
	}
	
	/**	Finalizes the line.
	 *
	 *	<p>This method must be called before the line can be serialized 
	 *	or used. If the line has already been finalized, this method
	 *	does nothing.
	 */
	 
	public void finalize () {
		if (runs == null)
			runs = (TextRun[])runList.toArray(new TextRun[runList.size()]);
		runList = null;
		if (marginalia != null) marginalia.finalize();
	}
	
	/**	Returns true if the line is empty.
	 *
	 *	<p>This method may be called before or after finalization.
	 *
	 *	@return		True if line is empty.
	 */
	 
	public boolean isEmpty () {
		if (runs == null) {
			for (Iterator it = runList.iterator(); it.hasNext(); ) {
				TextRun run = (TextRun)it.next();
				if (!run.isEmpty()) return false;
			}
			return true;
		} else {
			for (int i = 0; i < runs.length; i++)
				if (!runs[i].isEmpty()) return false;
			return true;
		}
	}
	
	/**	Gets the style runs.
	 *
	 *	@return		The style runs, or null if the line has not yet been
	 *				finalized.
	 */
	 
	public TextRun[] getRuns () {
		return runs;
	}
	
	/**	Returns the line justification.
	 *
	 *	@return		Line justification (LEFT, CENTER, or RIGHT).
	 */
	 
	public byte getJustification () {
		return justification;
	}
	
	/**	Sets the line justification.
	 *
	 *	@param	justification		Line justification (LEFT, CENTER, or RIGHT).
	 */
	 
	public void setJustification (byte justification) {
		this.justification = justification;
	}
	
	/**	Gets the indentation.
	 *
	 *	@return		The indentation in pixels.
	 */
	 
	public int getIndentation () {
		return indentation;
	}
	
	/**	Sets the indentation.
	 *
	 *	@param	indentation		Indentation in pixels.
	 */
	 
	public void setIndentation (int indentation) {
		this.indentation = indentation;
	}
	
	/**	Adds extra indentation to the line.
	 *
	 *	@param	extra		Extra indentation to add to line.
	 */
	 
	public void addIndentation (int extra) {
		indentation += extra;
	}
	
	/**	Gets the integer line number.
	 *
	 *	@return		The integer line number.
	 */
	 
	public int getNumber () {
		return number;
	}
	
	/**	Gets the line number label.
	 *
	 *	@return		The line number label, or null if this line has no
	 *				line number.
	 */
	 
	public String getLabel () {
		return label;
	}
	
	/**	Gets the stanza label.
	 *
	 *	@return		The stanza label, or null.
	 */
	 
	public String getStanzaLabel () {
		return stanzaLabel;
	}
	
	/**	Gets the text of the line.
	 *
	 *	<p>The line must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	@return		Text of the line.
	 */
	 
	public String getText () {
		if (runs.length == 1) return runs[0].getText();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < runs.length; i++) 
			buf.append(runs[i].getText());
		return buf.toString();
	}
	
	/**	Gets the length of the line in characters.
	 *
	 *	<p>The line must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	@return		Length of the line in characters.
	 */
	 
	public int getLength () {
		int length = 0;
		for (int i = 0; i < runs.length; i++)
			length += runs[i].getLength();
		return length;
	}
	
	/**	Gets the width of the the line in pixels.
	 *
	 *	@return		The width of the line in pixels, or -1 if not yet set.
	 */
	
	public int getWidth () {
		return width;
	}
	
	/**	Sets the width of the line in pixels.
	 *
	 *	@param	width	The width of the line in pixels.
	 */
	 
	public void setWidth (int width) {
		this.width = width;
	}
	
	/**	Gets the height of the line in pixels.
	 *
	 *	@return		The height of the line in pixels, or -1 if not yet set.
	 */
	 
	public int getHeight () {
		return height;
	}
	
	/**	Gets the leading of the line in pixels.
	 *
	 *	@return		The leading of the line in pixels, or -1 if not yet set.
	 */
	 
	public int getLeading () {
		return leading;
	}
	
	/**	Gets the ascent of the line in pixels.
	 *
	 *	@return		The ascent of the line in pixels, or -1 if not yet set.
	 */
	 
	public int getAscent () {
		return ascent;
	}
	
	/**	Gets the descent of the line in pixels.
	 *
	 *	@return		The descent of the line in pixels, or -1 if not yet set.
	 */
	 
	public int getDescent () {
		return descent;
	}
	
	/**	Computes the line width.
	 *
	 *	<p>The line must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	<p>The font info must be set for all the runs in the line.
	 */
	 
	public void computeWidth () {
		width = 0;
		for (int i = 0; i < runs.length; i++)
			width += runs[i].getWidth();
		if (marginalia != null) marginalia.computeLineWidths();
	}
	
	/**	Computes the line vertical positioning information.
	 *
	 *	<p>The line must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	<p>The font info must be set for all the runs in the line. The
	 *	leading, ascent, and descent of the line are computed as the 
	 *	maximum of the corresponding values for all the runs in the line.
	 *	The height of the line is computed as the sum of the line's leading,
	 *	ascent, and descent.
	 *
	 *	<p>The leading, ascent, and descent are further adjusted to
	 *	guarentee enough room for any underline, overline, superscript, 
	 *	and/or subscript runs in the line.
	 */
	 
	public void computeVerticalPositioningInformation () {
		leading = 0;
		ascent = 0;
		descent = 0;
		int prevAscent = 0;
		for (int i = 0; i < runs.length; i++) {
			TextRun run = runs[i];
			FontInfo info = run.getFontInfo();
			int runLeading = info.getLeading();
			int runAscent = info.getAscent();
			int runDescent = info.getDescent();
			if (run.isOverline() && runLeading < 2) runLeading = 2;
			if (run.isUnderline() && runDescent < 2) runDescent = 2;
			if (run.isSuperscript()) {
				int yOffset = -prevAscent/2;
				runAscent += prevAscent/2;
				run.setXOffset(1);
				run.setYOffset(yOffset);
			} else if (run.isSubscript()) {
				int yOffset = runAscent/2;
				runDescent += yOffset;
				run.setXOffset(1);
				run.setYOffset(yOffset);
			}
			if (runLeading > leading) leading = runLeading;
			if (runAscent > ascent) ascent = runAscent;
			if (runDescent > descent) descent = runDescent;
			prevAscent = runAscent;
		}
		height = leading + ascent + descent;
		if (marginalia != null) 
			marginalia.computeVerticalPositioningInformation();
	}
	
	/**	Returns true if the line is marked.
	 *
	 *	@return		True if line is marked.
	 */
	 
	public boolean isMarked () {
		return marked;
	}
	
	/**	Sets the marked attribute.
	 *
	 *	@param	marked		True if line is marked.
	 */
	 
	public void setMarked (boolean marked) {
		this.marked = marked;
	}
	
	/**	Clones the line.
	 *
	 *	<p>The line must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	<p>The clone is a deep copy of the line.
	 *
	 *	@return			Deep clone of the line.
	 */
	 
	public Object clone () {
		try {
			TextLine copy = (TextLine)super.clone();
			int numRuns = runs.length;
			copy.runs = new TextRun[numRuns];
			for (int i = 0; i < numRuns; i++) 
				copy.runs[i] = (TextRun)runs[i].clone();
			if (marginalia != null) 
				copy.marginalia = (Text)marginalia.clone();
			return copy;
		} catch (CloneNotSupportedException e) {
			// can't happen.
			throw new InternalError(e.toString());
		}
	}

	/**	Writes the line to an object output stream (serializes the object).
	 *
	 *	<p>The line must be finalized, or a null pointer exception is 
	 *	thrown.
	 *
	 *	@param	out		Object output stream.
	 *
	 *	@throws	IOException
	 */

	public void writeExternal (ObjectOutput out)
		throws IOException
	{
		out.writeByte(justification);
		out.writeInt(indentation);
		out.writeInt(number);
		out.writeUTF(label == null ? "" : label);
		out.writeUTF(stanzaLabel == null ? "" : stanzaLabel);
		out.writeObject(marginalia);
		int numRuns = runs.length;
		out.writeInt(numRuns);
		for (int i = 0; i < runs.length; i++)
			runs[i].writeExternal(out);
	}

	/**	Reads the line from an object input stream (deserializes the object).
	 *
	 *	<p>The deserialized line is finalized.
	 *
	 *	@param	in		Object input stream.
	 *
	 *	@throws	IOException
	 *
	 *	@throws	ClassNotFoundException
	 */

	public void readExternal (ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		justification = in.readByte();
		indentation = in.readInt();
		number = in.readInt();
		label = in.readUTF();
		stanzaLabel = in.readUTF();
		marginalia = (Text)in.readObject();
		if (label.length() == 0) label = null;
		if (stanzaLabel.length() == 0) stanzaLabel = null;
		int numRuns = in.readInt();
		runs = new TextRun[numRuns];
		for (int i = 0; i < numRuns; i++) {
			TextRun run = new TextRun();
			run.readExternal(in);
			runs[i] = run;
		}
		runList = null;
	}
	
	/**	Draws the line.
	 *
	 *	<p>The line must be finalized, the font info must be set for all the
	 *	runs in the line, and the line's width and vertical postioning
	 *	information must be computed.
	 *
	 *	@param	context					Drawing context.
	 *
	 *	@param	x						X coordinate of left edge of line.
	 *
	 *	@param	y						Y coordinate of top of line.
	 *
	 *	@param	selStartOffsetInLine	Selection start offset in line.
	 *
	 *	@param	selEndOffsetInLine		Selection end offset in line.
	 */
	
	void draw (DrawingContext context, int x, int y,
		int selStartOffsetInLine, int selEndOffsetInLine) 
	{
		Graphics2D g = context.getGraphics();
		context.setYTop(y);
		context.setLineHeight(height);
		int xRun = x;
		int baseLine = y + leading + ascent;
		int rightMargin = context.getRightMargin();
		switch (justification) {
			case LEFT:
				xRun += indentation;
				break;
			case CENTER:
				xRun += (rightMargin-width)/2;
				break;
			case RIGHT:
				xRun += rightMargin - width;
				break;
		}
		int runStartOffset = 0;
		for (int i = 0; i < runs.length; i++) {
			TextRun run = runs[i];
			int len = run.getLength();
			int runEndOffset = runStartOffset + len;
			int selStartOffsetInRun = 
				Math.max(0, selStartOffsetInLine - runStartOffset);
			int selEndOffsetInRun =
				Math.min(len, selEndOffsetInLine - runStartOffset);
			run.draw(context, xRun, baseLine, 
				selStartOffsetInRun, selEndOffsetInRun);
			xRun += run.getWidth();
			runStartOffset += len;
		}
		int lineNumberInterval = context.getLineNumberInterval();
		boolean drawLineNumber = lineNumberInterval > 0 && label != null && 
			number % lineNumberInterval == 0;
		boolean drawStanzaNumber = lineNumberInterval == -1 && 
			stanzaLabel != null;
		if (drawLineNumber || drawStanzaNumber) {
			FontInfo lineNumberFontInfo = context.getLineNumberFontInfo();
			Color lineNumberColor = context.getLineNumberColor();
			int lineNumberRightMargin = context.getLineNumberRightMargin();
			g.setFont(lineNumberFontInfo.getFont());
			int labelWidth = lineNumberFontInfo.stringWidth(label);
			g.setColor(lineNumberColor);
			g.drawString(drawLineNumber ? label : stanzaLabel, 
				x + lineNumberRightMargin - labelWidth, 
				baseLine);
		}
		if (marginalia != null) {
			int marginaliaLeftMargin = context.getMarginaliaLeftMargin();
			marginalia.draw(g, x + marginaliaLeftMargin, y);
		}
		if (marked) {
			int markerOffset = context.getMarkerOffset();
			String markerString = context.getMarkerString();
			FontInfo markerFontInfo = context.getMarkerFontInfo();
			Color markerColor = context.getMarkerColor();
			g.setFont(markerFontInfo.getFont());
			g.setColor(markerColor);
			g.drawString(markerString, x - markerOffset, baseLine);
		}
	}
	
	/**	Converts a point to a location and character set.
	 *
	 *	@param	context		Drawing context.
	 *
	 *	@param	lineIndex	Index of line.
	 *
	 *	@param	x			X coordinate of point in line.
	 *
	 *	@return				Location and character set.
	 */
	
	LocationAndCharset viewToModel (DrawingContext context,
		int lineIndex, int x) 
	{
		int rightMargin = context.getRightMargin();
		switch (justification) {
			case LEFT:
				x -= indentation;
				break;
			case CENTER:
				x -= (rightMargin-width)/2;
				break;
			case RIGHT:
				x -= rightMargin-width;
				break;
		}
		if (x < 0) return new LocationAndCharset(lineIndex, -1);
		if (x > width) return new LocationAndCharset(lineIndex, 
			Integer.MAX_VALUE);
		int charOffsetInLine = 0;
		byte charset = TextParams.ROMAN;
		for (int i = 0; i < runs.length; i++) {
			TextRun run = runs[i];
			int runWidth = run.getWidth();
			int len = run.getLength();
			if (x < runWidth) {
				charOffsetInLine += run.viewToModel(context, x);
				charset = run.getCharset();
				break;
			}
			x -= runWidth;
			charOffsetInLine += len;
		}
		return new LocationAndCharset(lineIndex, charOffsetInLine, charset);
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

