package edu.northwestern.at.wordhoard.swing.text;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.swing.dialogs.*;

/**	Font manager.
 *
 *	<p>For each character set, three font family categories are used:
 *
 *	<ul>
 *	<li>Work: This family is used in work panels to display the
 *		text of work parts. This is typically a serif font.
 *	<li>Other: This family is used in all other contexts which
 *		require a non-monospaced font - get info windows, tables, concordances,
 *		etc. This is typically a sans-serif font.
 *	<li>Monospaced: A family for contexts which need a monospaced font (e.g.,
 *		the word paths in concordances).
 *	</ul>
 *
 *	<p>Windows should acquire a font manager in their constructor and use it
 *	to get all needed font and font information. Each new font manager gets a
 *	copy of the current user font family preferences. If the user changes these
 *	preferences, the changes only affect new windows, not windows that are
 *	already open.
 */
 
public class FontManager {

	/**	Work font category. */
	
	public static final int WORK = 0;
	
	/**	Other font category. */
	
	public static final int OTHER = 1;
	
	/**	Monospaced font category. */
	
	public static final int MONOSPACED = 2;
	
	/**	Number of font categories. */
	
	public static final int NUM_CATEGORIES = 3;
	
	/**	A cache key. */
	
	private static class CacheKey {
		private String family;
		private int style;
		private int size;
		private CacheKey (String family, int style, int size) {
			this.family = family;
			this.style = style;
			this.size = size;
		}
		public int hashCode () {
			return family.hashCode() + style + size;
		}
		public boolean equals (Object o) {
			CacheKey other = (CacheKey)o;
			return family.equals(other.family) &&
				style == other.style && size == other.size;
		}
	}
	
	/**	The cache - maps cache keys to font info objects. */
	
	private static HashMap cache = new HashMap();
	
	/**	Font family names, indexed by category and character set. */
	
	private String[][] fontFamilyNames = 
		new String[NUM_CATEGORIES][TextParams.NUM_CHARSETS];
		
	/**	Creates a new font manager. */
	
	public FontManager () {
		fontFamilyNames = FontPrefsDialog.getFontPrefs();
	}
	
	/**	Gets font info.
	 *
	 *	@param	family		Family.
	 *
	 *	@param	style		Style.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font info.
	 */
	 
	private static FontInfo getFontInfo (String family, int style, int size) {
		CacheKey key = new CacheKey(family, style, size);
		FontInfo info = (FontInfo)cache.get(key);
		if (info != null) return info;
		info = new FontInfoImpl(family, style, size);
		cache.put(key, info);
		return info;
	}
	
	/**	Gets "work" font info.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	style		Style.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font info.
	 */
	
	public FontInfo getWorkFontInfo (byte charset, int style, 
		int size) 
	{
		String family = fontFamilyNames[WORK][charset];
		return getFontInfo(family, style, size);
	}
	
	/**	Gets plain "work" font info.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font info.
	 */
	 
	public FontInfo getWorkFontInfo (byte charset, int size) {
		return getWorkFontInfo(charset, Font.PLAIN, size);
	}
	
	/**	Gets "other" font info.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	style		Style.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font info.
	 */
	
	public FontInfo getFontInfo (byte charset, int style, 
		int size) 
	{
		String family = fontFamilyNames[OTHER][charset];
		return getFontInfo(family, style, size);
	}
	
	/**	Gets plain "other" font info.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font info.
	 */
	 
	public FontInfo getFontInfo (byte charset, int size) {
		return getFontInfo(charset, Font.PLAIN, size);
	}
	
	/**	Gets plain Roman "other" font info.
	 *
	 *	@param	size		Size.
	 *	@return plain Roman font.
	 */
	 
	public FontInfo getFontInfo (int size) {
		return getFontInfo(TextParams.ROMAN, Font.PLAIN, size);
	}
	
	/**	Gets monospaced font info.
	 *
	 *	@param	style		Style.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font info.
	 */
	
	public FontInfo getMonospacedFontInfo (int style, int size) {
		String family = fontFamilyNames[MONOSPACED][TextParams.ROMAN];
		return getFontInfo(family, style, size);
	}
	
	/**	Gets plain monospaced font info.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font info.
	 */
	 
	public FontInfo getMonospacedFontInfo (int size) {
		return getMonospacedFontInfo(Font.PLAIN, size);
	}
	
	/**	Gets a "work" font.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	style		Style.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font.
	 */
	
	public Font getWorkFont (byte charset, int style, int size) {
		return getWorkFontInfo(charset, style, size).getFont();
	}
	
	/**	Gets a plain "work" font.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font.
	 */
	 
	public Font getWorkFont (byte charset, int size) {
		return getWorkFont(charset, Font.PLAIN, size);
	}
	
	/**	Gets an "other" font.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	style		Style.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font.
	 */
	
	public Font getFont (byte charset, int style, int size) {
		return getFontInfo(charset, style, size).getFont();
	}
	
	/**	Gets a plain "other" font.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font.
	 */
	 
	public Font getFont (byte charset, int size) {
		return getFont(charset, Font.PLAIN, size);
	}
	
	/**	Gets a plain Roman "other" font.
	 *
	 *	@param	size	Size.
	 *	@return	plain Roman font.
	 */
	 
	public Font getFont (int size) {
		return getFont(TextParams.ROMAN, Font.PLAIN, size);
	}
	
	/**	Gets a monospaced font.
	 *
	 *	@param	style		Style.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font.
	 */
	
	public Font getMonospacedFont (int style, int size) {
		return getMonospacedFontInfo(style, size).getFont();
	}
	
	/**	Gets a plain monospaced font.
	 *
	 *	@param	size		Size.
	 *
	 *	@return				Font.
	 */
	 
	public Font getMonospacedFont (int size) {
		return getMonospacedFont(Font.PLAIN, size);
	}
	
	/**	Initializes font information for a line.
	 *
	 *	<p>The font information is set for all the runs in the line
	 *	and the width is set for the line.
	 *
	 *	@param	line		Line.
	 */
	 
	private void initLine (TextLine line) {
		TextRun[] runs = line.getRuns();
		for (int j = 0; j < runs.length; j++) {
			TextRun run = runs[j];
			byte charset = run.getCharset();
			boolean bold = run.isBold();
			boolean italic = run.isItalic();
			boolean monospaced = run.isMonospaced();
			int style = Font.PLAIN;
			if (bold) style |= Font.BOLD;
			if (italic) style |= Font.ITALIC;
			int size = run.getSize();
			FontInfo info;
			if (monospaced) {
				info = getMonospacedFontInfo(style, size);
			} else {
				info = getWorkFontInfo(charset, style, size);
			}
			run.setFontInfo(info);
		}
		line.computeWidth();
	}
	
	/**	Initializes font information for text.
	 *
	 *	<p>The font information is set for all the runs and widths are set
	 *	for all the lines in the text.
	 *
	 *	@param	text	Text.
	 */
	 
	public void initText (Text text) {
		TextLine[] lines = text.getLines();
		for (int i = 0; i < lines.length; i++) {
			TextLine line = lines[i];
			initLine(line);
			Text marginalia = line.getMarginalia();
			if (marginalia != null) initText(marginalia);
		}
	}
	
	/**	Gets the nominal line height.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@return			Nominal line height in pixels (the height of a line
	 *					in the nominal font size).
	 */
	 
	public int getNominalLineHeight (byte charset) {
		FontInfo info = getWorkFontInfo(
			charset, TextParams.NOMINAL_FONT_SIZE);
		return info.getHeight();
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

