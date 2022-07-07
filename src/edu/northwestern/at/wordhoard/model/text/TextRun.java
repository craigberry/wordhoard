package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.io.*;

import edu.northwestern.at.utils.*;

/**	A styled run of text.
 *
 *	<p>A run is a string of text plus character set, font size, and style
 *	attributes.
 *
 *	<p>The style attributes are:
 *
 *	<ul>
 *	<li>Bold.
 *	<li>Italic.
 *	<li>Extended. This style is used by the Iliad scholia, where it is 
 *		called "sperrtext". It is rendered with extra space between characters.
 *	<li>Underline.
 *	<li>Overline. This style is also used by the Iliad scholia, where it is
 *		called "macron". It is rendered with a horizontal line above the run of 
 *		characters.
 *	<li>Superscript. Also used by the Iliad scholia.
 *	<li>Subscript.
 *	<li>Monospaced.
 *	</ul>
 *
 *	<p>Runs can also contain font information, which is set and used in
 *	the client to facilitate word wrapping and presentation. See
 *	{@link edu.northwestern.at.wordhoard.model.text.WrappedText WrappedText} 
 *	and
 *	{@link edu.northwestern.at.wordhoard.swing.text.FontManager 
 *	FontManager} for more details.
 *
 *	<p>Runs can also contain color information, which is also set and used
 *	in the client.
 */

public class TextRun implements Cloneable {

	/**	Bold style mask. */
	
	public static final int BOLD = 0x0001;

	/**	Italic style mask. */
	
	public static final int ITALIC = 0x0002;

	/**	Extended style mask. */
	
	public static final int EXTENDED = 0x0004;

	/**	Underline style mask. */
	
	public static final int UNDERLINE = 0x0008;

	/**	Overline style mask. */
	
	public static final int OVERLINE = 0x0010;

	/**	Superscript style mask. */
	
	public static final int SUPERSCRIPT = 0x0020;

	/**	Subscript style mask. */
	
	public static final int SUBSCRIPT = 0x0040;

	/**	Monospaced style mask. */
	
	public static final int MONOSPACED = 0x0080;

	/**	The text of the run. */

	private String text;
	
	/**	The character set. */
	
	private byte charset;
	
	/**	The font size. */
	
	private byte size;
	
	/**	The style attributes. */
	
	private int style;
	
	/**	Font information for the run, or null if not yet set. 
	 *	(Used only in client - not serialized.) 
	 */
	
	private FontInfo fontInfo;
	
	/**	The color of the run. 
	 *	(Used only in client - not serialized.) 
	 */
	 
	private Color color = Color.black;
	
	/**	The width of the run in pixels, or -1 if not yet set.
	 *	(Used only in client - not serialized.) 
	 */
	
	private int width = -1;
	
	/**	X offset.
	 *	(Used only in client - not serialized.) 
	 */
	 
	private int xOffset = 0;
	
	/**	Y offset.
	 *	(Used only in client - not serialized.) 
	 */
	 
	private int yOffset = 0;
	
	/**	Cached glyph vector, or null if not yet computed.
	 *	(Used only in client - not serialized.)
	 */
	
	private GlyphVector gv;
	
	/**	Extra space beween glyphs for extended style.
	 *	(Used only in client - not serialized.)
	 */
	
	private double extraSpace;
	
	/**	Creates a new run.
	 *
	 *	<p>The new run is plain (no style attributes).
	 *
	 *	@param	text		Text.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	size		Font size.
	 */
	 
	public TextRun (String text, byte charset, byte size) 
	{
		this.text = text;
		this.charset = charset;
		this.size = size;
	}
	
	/**	Creates a new run.
	 *
	 *	@param	text		Text.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	size		Font size.
	 *
	 *	@param	bold		True if bold.
	 *
	 *	@param	italic		True if italic.
	 */
	 
	public TextRun (String text, byte charset, byte size, boolean bold, 
		boolean italic) 
	{
		this(text, charset, size);
		if (bold) style = BOLD;
		if (italic) style |= ITALIC;
	}
	
	/**	Creates a new run.
	 *
	 *	<p>The new run has size 0 and is plain.
	 *
	 *	@param	text		Text.
	 *
	 *	@param	charset		Character set.
	 */
	 
	public TextRun (String text, byte charset) {
		this(text, charset, (byte)0);
	}
	
	/**	Creates a new run with font information.
	 *
	 *	<p>The new run has size 0, is plain, and uses the Roman 
	 *	character set.
	 *
	 *	@param	text		Text.
	 *
	 *	@param	fontInfo	Font information.
	 */
	 
	public TextRun (String text, FontInfo fontInfo) {
		this(text, (byte)0);
		setFontInfo(fontInfo);
	}
	
	/**	Creates a new empty run.
	 */
	 
	public TextRun () {
	}
	
	/**	Gets the text of the run.
	 *
	 *	@return		The text of the run.
	 */
	 
	public String getText () {
		return text;
	}
	
	/**	Sets the text of the run.
	 *
	 *	@param	text	The text of the run.
	 */
	 
	public void setText (String text) {
		this.text = text;
	}
	
	/**	Gets the character set.
	 *
	 *	@return		Character set.
	 */
	 
	public byte getCharset () {
		return charset;
	}
	
	/**	Sets the character set.
	 *
	 *	@param	charset		The character set.
	 */
	 
	public void setCharset (byte charset) {
		this.charset = charset;
	}
	
	/**	Gets the font size. 
	 *
	 *	@return		The font size.
	 */
	 
	public byte getSize () {
		return size;
	}
	
	/**	Sets the font size.
	 *
	 *	@param	size	The font size.
	 */
	 
	public void setSize (byte size) {
		this.size = size;
	}
	
	/**	Returns true if bold.
	 *
	 *	@return		True if bold.
	 */
	 
	public boolean isBold () {
		return (style & BOLD) != 0;
	}
	
	/**	Sets the bold style attribute.
	 */
	 
	public void setBold () {
		style |= BOLD;
	}
	
	/**	Returns true if italic.
	 *
	 *	@return		True if italic.
	 */
	 
	public boolean isItalic () {
		return (style & ITALIC) != 0;
	}
	
	/**	Sets the italic style attribute. 
	 */
	 
	public void setItalic () {
		style |= ITALIC;
	}
	
	/**	Returns true if extended.
	 *
	 *	@return		True if extended.
	 */
	 
	public boolean isExtended () {
		return (style & EXTENDED) != 0;
	}
	
	/**	Sets the extended style attribute. 
	 */
	 
	public void setExtended () {
		style |= EXTENDED;
	}
	
	/**	Returns true if underline.
	 *
	 *	@return		True if underline.
	 */
	 
	public boolean isUnderline () {
		return (style & UNDERLINE) != 0;
	}
	
	/**	Sets the underline style attribute.
	 */
	 
	public void setUnderline () {
		style |= UNDERLINE;
	}
	
	/**	Returns true if overline.
	 *
	 *	@return		True if overline.
	 */
	 
	public boolean isOverline () {
		return (style & OVERLINE) != 0;
	}
	
	/**	Sets the overline style attribute. 
	 */
	 
	public void setOverline () {
		style |= OVERLINE;
	}
	
	/**	Returns true if superscript.
	 *
	 *	@return		True if superscript.
	 */
	 
	public boolean isSuperscript () {
		return (style & SUPERSCRIPT) != 0;
	}
	
	/**	Sets the superscript style attribute. 
	 */
	 
	public void setSuperscript () {
		style |= SUPERSCRIPT;
	}
	
	/**	Returns true if subscript.
	 *
	 *	@return		True if subscript.
	 */
	 
	public boolean isSubscript () {
		return (style & SUBSCRIPT) != 0;
	}
	
	/**	Sets the subscript style attribute. 
	 */
	 
	public void setSubscript () {
		style |= SUBSCRIPT;
	}
	
	/**	Returns true if monospaced.
	 *
	 *	@return		True if monospaced.
	 */
	 
	public boolean isMonospaced () {
		return (style & MONOSPACED) != 0;
	}
	
	/**	Sets the monospaced style attribute. 
	 */
	 
	public void setMonospaced () {
		style |= MONOSPACED;
	}
	
	/**	Returns true if the run is empty.
	 *
	 *	@return		True if run is empty (all whitespace).
	 */
	 
	public boolean isEmpty () {
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (!Character.isWhitespace(c)) return false;
		}
		return true;
	}
	
	/**	Gets the style attributes.
	 *
	 *	@return		Style attributes.
	 */
	 
	public int getStyle () {
		return style;
	}
	
	/**	Sets the style attributes.
	 *
	 *	@param	style	Style attributes.
	 */
	 
	public void setStyle (int style) {
		this.style = style;
	}
	
	/**	Gets the length of the run in characters.
	 *
	 *	@return		Length of the run in characters.
	 */
	 
	public int getLength () {
		return text.length();
	}
	
	/**	Gets the font information for the run.
	 *
	 *	@return		The font information for the run, or null if not yet set.
	 */
	 
	public FontInfo getFontInfo () {
		return fontInfo;
	}
	
	/**	Sets the font information for the run.
	 *
	 *	<p>This method also computes and sets the width of the run.
	 *
	 *	@param	fontInfo	The font information.
	 */
	 
	public void setFontInfo (FontInfo fontInfo) {
		this.fontInfo = fontInfo;
		width = fontInfo.stringWidth(text);
		if (isExtended()) {
			int len = text.length();
			double averageWidth = width/(double)len;
			double extraSpace = averageWidth * 
				(TextParams.EXTENDED_EXPANSION_FACTOR - 1.0);
			double extraWidth = extraSpace * (len-1);
			width += extraWidth;
		} else if (isSuperscript() || isSubscript()) {
			width += 1;
		}
	}
	
	/**	Gets the width of a substring of the run. 
	 *
	 *	@param	start		Start index of substring.
	 *
	 *	@param	end			End index of substring.
	 *
	 *	@return	The width of the substring.
	 */
	 
	public int getSubstringWidth (int start, int end) {
		String str = text.substring(start, end);
		int width = fontInfo.stringWidth(str);
		if (isExtended()) {
			int len = str.length();
			double averageWidth = width/(double)len;
			double extraSpace = averageWidth *
				(TextParams.EXTENDED_EXPANSION_FACTOR - 1.0);
			double extraWidth = extraSpace * (len-1);
			width += extraWidth;
		}
		return width;
	}
	
	/**	Gets the color of the run.
	 *
	 *	@return		The color of the run (default black).
	 */
	 
	public Color getColor () {
		return color;
	}
	
	/**	Sets the color of the run.
	 *
	 *	@param	color		The color of the run.
	 */
	 
	public void setColor (Color color) {
		this.color = color;
	}
	
	/**	Gets the width of the the run in pixels.
	 *
	 *	@return		The width of the run in pixels, or -1 if not yet set.
	 */
	
	public int getWidth () {
		return width;
	}
	
	/**	Sets the X offset of the run.
	 *
	 *	@param	xOffset		Offset from left edge in pixels.
	 */
	 
	public void setXOffset (int xOffset) {
		this.xOffset = xOffset;
	}
	
	/**	Sets the Y offset of the run.
	 *
	 *	@param	yOffset		Offset from baseline in pixels.
	 */
	 
	public void setYOffset (int yOffset) {
		this.yOffset = yOffset;
	}
	
	/**	Clones the run.
	 *
	 *	<p>The clone is shallow - the font information is not cloned.
	 *
	 *	@return		A clone of the run.
	 */
	 
	public Object clone () {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// can't happen.
			throw new InternalError(e.toString());
		}
	}

	/**	Writes the run to an object output stream (serializes the object).
	 *
	 *	@param	out		Object output stream.
	 *
	 *	@throws	IOException	I/O error.
	 */

	public void writeExternal (ObjectOutput out)
		throws IOException
	{
		out.writeUTF(text);
		out.writeByte(charset);
		out.writeByte(size);
		out.writeInt(style);
	}

	/**	Reads the run from an object input stream (deserializes the object).
	 *
	 *	@param	in		Object input stream.
	 *
	 *	@throws	IOException	I/O error.
	 *
	 *	@throws	ClassNotFoundException	class not found.
	 */

	public void readExternal (ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		text = in.readUTF();
		charset = in.readByte();
		size = in.readByte();
		style = in.readInt();
	}
	
	/**	Gets and caches the glyph vector for the run.
	 *
	 *	@param	context		Drawing context.
	 */
	 
	private void getGlyphVector (DrawingContext context) {
		Font font = fontInfo.getFont();
		int len = text.length();
		FontRenderContext frc = context.getFontRenderContext();
		gv = font.createGlyphVector(frc, text);
		if (isExtended()) {
			int normalWidth = fontInfo.stringWidth(text);
			double averageWidth = normalWidth/(double)len;
			extraSpace = averageWidth * 
				(TextParams.EXTENDED_EXPANSION_FACTOR - 1.0);
			double glyphOffset = 0.0;
			for (int glyphIndex = 0; glyphIndex < gv.getNumGlyphs(); 
				glyphIndex++)
			{
				Point2D p = gv.getGlyphPosition(glyphIndex);
				double px = p.getX();
				double py = p.getY();
				p.setLocation(px + glyphOffset, py);
				gv.setGlyphPosition(glyphIndex, p);
				glyphOffset += extraSpace;
			}
		}
	}
	
	/**	Gets the character index of a glyph in the glyph vector.
	 *
	 *	<p>This method works around a bug in Java 1.5 on the Mac. The 
	 *	GlyphVector.getGlyphCharIndex method returns the wrong result. To
	 *	work around the bug, we simply return the glyph index.
	 *
	 *	@param	glyphIndex		Index of the glyph.
	 *
	 *	@return					Character index of the glyph.
	 */
	 
	private int getGlyphCharIndex (int glyphIndex) {
		if (Env.MACOSX) {
			return glyphIndex;
		} else {
			return gv.getGlyphCharIndex(glyphIndex);
		}
	}
	
	/**	Draws the run.
	 *
	 *	<p>The font information must be set.
	 *
	 *	@param	context					Drawing context.
	 *
	 *	@param	x						X coordinate of left edge of run.
	 *
	 *	@param	y						Y coordinate of baseline of run.
	 *
	 *	@param	selStartOffsetInRun		Selection start offset in run.
	 *
	 *	@param	selEndOffsetInRun		Selection end offset in run.
	 */
	
	void draw (DrawingContext context, int x, int y, 
		int selStartOffsetInRun, int selEndOffsetInRun) 
	{
		Graphics2D g = context.getGraphics();
		Font font = fontInfo.getFont();
		int ascent = fontInfo.getAscent();
		int len = text.length();
		g.setFont(font);
		y += yOffset;
		if (gv == null) getGlyphVector(context);
		if (selStartOffsetInRun >= selEndOffsetInRun ||
			selStartOffsetInRun >= len || selEndOffsetInRun <= 0) {
			// No part of the run is selected.
			g.setColor(color);
			g.drawGlyphVector(gv, x + xOffset, y);
		} else if (selStartOffsetInRun <= 0 && selEndOffsetInRun >= len) {
			// The entire run is selected.
			Color selectedColor = context.getSelectedColor();
			Color selectedTextColor = context.getSelectedTextColor();
			int yTop = context.getYTop();
			int lineHeight = context.getLineHeight();
			g.setColor(selectedColor);
			g.fillRect(x, yTop, width, lineHeight);
			g.setColor(selectedTextColor);
			g.drawGlyphVector(gv, x + xOffset, y);
		} else {
			// Part of the run is selected, part is not selected.
			g.setColor(color);
			g.drawGlyphVector(gv, x + xOffset, y);
			Rectangle clip = g.getClipBounds();
			int numGlyphs = gv.getNumGlyphs();
			int selStartGlyphIndex = numGlyphs;
			for (int glyphIndex = 0; glyphIndex < numGlyphs; glyphIndex++) {
				int glyphCharIndex = getGlyphCharIndex(glyphIndex);
				if (glyphCharIndex > selStartOffsetInRun) {
					selStartGlyphIndex = glyphCharIndex - 1;
					break;
				}
			}
			int selEndGlyphIndex = 0;
			for (int glyphIndex = numGlyphs-1; glyphIndex >= 0; glyphIndex--) {
				int glyphCharIndex = getGlyphCharIndex(glyphIndex);
				if (glyphCharIndex < selEndOffsetInRun) {
					selEndGlyphIndex = glyphCharIndex + 1;
					break;
				}
			}
			if (selStartGlyphIndex < selEndGlyphIndex) {
				Point2D selStart = gv.getGlyphPosition(selStartGlyphIndex);
				int xStart = (int)Math.round(selStart.getX());
				int xEnd;
				if (selEndGlyphIndex >= numGlyphs) {
					xEnd = width;
				} else {
					Point2D selEnd = gv.getGlyphPosition(selEndGlyphIndex);
					xEnd = (int)Math.round(selEnd.getX());
				}
				Color selectedColor = context.getSelectedColor();
				Color selectedTextColor = context.getSelectedTextColor();
				int yTop = context.getYTop();
				int lineHeight = context.getLineHeight();
				g.setClip(x + xStart, yTop, xEnd-xStart+xOffset, lineHeight);
				g.setColor(selectedColor);
				g.fillRect(x, yTop, width, lineHeight);
				g.setColor(selectedTextColor);
				g.drawGlyphVector(gv, x + xOffset, y);
				g.setClip(clip.x, clip.y, clip.width, clip.height);
			}
		}
		if (isUnderline()) {
			int yBottom = y + 1;
			g.drawLine(x, yBottom, x+width, yBottom);
		}
		if (isOverline()) {
			int yTop = y - ascent - 1;
			g.drawLine(x, yTop, x+width, yTop);
		}
	}
	
	/**	Converts a point to a character offset.
	 *
	 *	@param	context		Drawing context.
	 *
	 *	@param	x			X coordinate of point in run.
	 *
	 *	@return				Character offset in run.
	 */
	 
	int viewToModel (DrawingContext context, int x) {
		if (gv == null) getGlyphVector(context);
		int numGlyphs = gv.getNumGlyphs();
		int glyphIndex = 0;
		Point2D p = null;
		for (glyphIndex = 0; glyphIndex < numGlyphs; glyphIndex++) {
			p = gv.getGlyphPosition(glyphIndex);
			if (x < (int)Math.round(p.getX())) break;
		}
		if (glyphIndex == 0) return 0;
		glyphIndex--;
		if (isExtended() && glyphIndex < numGlyphs-1) {
			double px = p.getX();
			if (x + extraSpace >= px) glyphIndex++;
		}
		return getGlyphCharIndex(glyphIndex);
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

