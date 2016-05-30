package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Text formatting style information.
 *
 *	<p>A style object records the following information about line and word text
 *	formatting style attributes:
 *
 *	<ul>
 *
 *	<li>lineStyle = the line justification: LEFT, CENTER, RIGHT, or NO_CHANGE.
 *
 *	<li>indent = the line indentation in pixels, or NO_CHANGE.
 *		Indentation is used only with left justified lines.
 *
 *	<li>wordStyles = word styles, as an integer bit mask, or NO_CHANGE.
 *
 *	</ul>
 *
 *	<p>The NO_CHANGE value for these three attributes is used when adding styles.
 *	See the description of the add method for details.
 */

public class Style implements Cloneable {

	/**	No change in style.
	 *
	 *	<p>This constant is used with all kinds of formatting styles - line
	 *	styles, indentation styles, and word styles.
	 */

	public static final int NO_CHANGE = -1;

	/**	Left alignment line style. */

	public static final int LEFT = 0;

	/**	Center alignment line style. */

	public static final int CENTER = 1;

	/**	Right alignment line style. */

	public static final int RIGHT = 2;

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

	/**	Plain style mask. */

	public static final int PLAIN = 0x0100;

	/** Array of WordHoard style names, in the order of their style mask
	 *	bit postions.
	 */

	public static final String[] STYLE_NAMES =
		new String[] {
			"bold",
			"italic",
			"extended",
			"underline",
			"overline",
			"superscript",
			"subscript",
			"monospaced",
			"plain",
		};

	/**	Line style. */

	private int lineStyle;

	/**	Indentation. */

	private int indent;

	/**	Word styles */

	private int wordStyles;

	/**	Creates a style.
	 *
	 *	@param	lineStyle		Line style.
	 *
	 *	@param	indent			Indentation.
	 *
	 *	@param	wordStyles		Word styles.
	 */

	public Style (int lineStyle, int indent, int wordStyles) {
		this.lineStyle = lineStyle;
		this.indent = indent;
		this.wordStyles = wordStyles;
	}

	/**	Parses a line style attribute for a parameter file element.
	 *
	 *	@param	el			Element.
	 *
	 *	@return				Line style.
	 */

	private int parseLineStyleAttribute (Element el) {
		String attrValue = el.getAttribute("lineStyle");
		if (attrValue.length() == 0) return NO_CHANGE;
		if (attrValue.equals("left")) return LEFT;
		if (attrValue.equals("center")) return CENTER;
		if (attrValue.equals("right")) return RIGHT;
		Utils.paramErr("Illegal line style value " + attrValue + " in " +
			el.getNodeName() + " element");
		return NO_CHANGE;
	}

	/**	Parses a word styles attribute for a parameter file element.
	 *
	 *	@param	el			Element.
	 *
	 *	@return				Word styles.
	 */

	private int parseWordStylesAttribute (Element el) {
		String attrValue = el.getAttribute("wordStyles");
		if (attrValue.length() == 0) return NO_CHANGE;
		StringTokenizer tok = new StringTokenizer(attrValue, ",");
		int result = 0;
		while (tok.hasMoreTokens()) {
			String str = tok.nextToken();
			boolean foundIt = false;
			for (int i = 0; i < STYLE_NAMES.length; i++) {
				if (str.equals(STYLE_NAMES[i])) {
					result |= 1 << i;
					foundIt = true;
					break;
				}
			}
			if (!foundIt) Utils.paramErr("Illegal word style value " + str + " in " +
				el.getNodeName() + " element");
		}
		return result;
	}

	/**	Creates a style from a parameters XML element.
	 *
	 *	@param	el				Element.
	 */

	public Style (Element el) {
		lineStyle = parseLineStyleAttribute(el);
		indent = Utils.parseIntegerAttribute(el, "indent", NO_CHANGE);
		wordStyles = parseWordStylesAttribute(el);
	}

	/**	Gets the line style.
	 *
	 *	@return		The line style.
	 */

	public int getLineStyle () {
		return lineStyle;
	}

	/**	Sets the line style.
	 *
	 *	@param	lineStyle	The line style.
	 */

	public void setLineStyle (int lineStyle) {
		this.lineStyle = lineStyle;
	}

	/**	Gets the indentation.
	 *
	 *	@return		Indentation.
	 */

	public int getIndent () {
		return indent;
	}

	/**	Sets the indentation.
	 *
	 *	@param	indent		Indentation.
	 */

	public void setIndent (int indent) {
		this.indent = indent;
	}

	/**	Gets the word styles.
	 *
	 *	@return		Word styles.
	 */

	public int getWordStyles () {
		return wordStyles;
	}

	/**	Sets the word styles.
	 *
	 *	@param	wordStyles	Word styles.
	 */

	public void setWordStyles (int wordStyles) {
		this.wordStyles = wordStyles;
	}

	/**	Adds a style to this style.
	 *
	 *	<p>If A and B are two styles, A.add(B) is constructed as follows:
	 *
	 *	<p>If B.lineStyle == NO_CHANGE, the result line style is A.lineStyle,
	 *	otherwise it is B.lineStyle.
	 *
	 *	<p>If B.indent == NO_CHANGE, the result indentation is A.indent,
	 *	otherwise it is A.indent + B.indent. Notice that the indentation is
	 *	cumulative.
	 *
	 *	<p>If B.wordStyles == NO_CHANGE, the result word styles is A.wordStyles.
	 *	If B.wordStyles == PLAIN, the result word styles is PLAIN.
	 *	Otherwise it is A.wordStyles | B.wordStyles, the logical OR of the two
	 *	bit masks. Notice that in the last case, word styles, like indentation, are
	 *	cumulative.
	 *
	 *	@param	style	The style to add.
	 */

	public void add (Style style) {
		if (style.lineStyle != NO_CHANGE) lineStyle = style.lineStyle;
		if (style.indent != NO_CHANGE) {
			indent += style.indent;
			if (indent < 0) indent = 0;
		}
		if (style.wordStyles == PLAIN) {
			wordStyles = PLAIN;
		} else if (style.wordStyles != NO_CHANGE) {
			wordStyles |= style.wordStyles;
		}
	}

	/**	Returns true if the style changes.
	 *
	 *	@return		True if the style changes.
	 */

	public boolean styleChange () {
		return lineStyle != NO_CHANGE ||
			indent != NO_CHANGE ||
			wordStyles != NO_CHANGE;
	}

	/**	Clones the style.
	 *
	 *	@return		Cloned style.
	 */

	public Object clone () {
		try {
			return (Style)super.clone();
		} catch (CloneNotSupportedException e) {
			// Can't happen.
			throw new InternalError(e.toString());
		}
	}

	/**	Converts the style to a string.
	 *
	 *	@return			Style converted to a string.
	 */

	public String toString () {
		StringBuffer buf = new StringBuffer();
		buf.append("[lineStyle=");
		switch (lineStyle) {
			case NO_CHANGE:
				buf.append("nochange");
				break;
			case LEFT:
				buf.append("left");
				break;
			case CENTER:
				buf.append("center");
				break;
			case RIGHT:
				buf.append("right");
				break;
		}
		buf.append(", indent=");
		if (indent == NO_CHANGE) {
			buf.append("nochange");
		} else {
			buf.append(indent);
		}
		buf.append(", wordStyles=");
		if (wordStyles == NO_CHANGE) {
			buf.append("nochange");
		} else {
			boolean first = true;
			for (int i = 0; i < STYLE_NAMES.length; i++) {
				int mask = 1 << i;
				if ((mask & wordStyles) != 0) {
					if (!first) buf.append("+");
					first = false;
					buf.append(STYLE_NAMES[i]);
				}
			}
		}
		buf.append("]");
		return buf.toString();
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


