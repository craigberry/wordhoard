package edu.northwestern.at.utils.swing.styledtext;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.utils.*;

/** A styled string.
 *
 *	<p>A styled string is a string plus style information. Styled strings
 *	are transferable objects with three flavors: plain text, styled text,
 *	and RTF.
 */

public class StyledString implements Serializable, Transferable, Cloneable {

	/**	Styled string data flavor. */

	public static final DataFlavor STYLED_STRING_FLAVOR =
		new DataFlavor(StyledString.class, "Styled String");

	/**	Richtext format data flavor. */

	public static final DataFlavor RTF_FLAVOR =
		new DataFlavor(
			"text/rtf; class=java.io.InputStream" , "Rich Text Format" );

	/** The string. */

	public String str = null;

	/** The style information. */

	public StyleInfo styleInfo = null;

	/** Constructs a new empty styled string. */

	public StyledString() {
	}

	/** Constructs a new styled string.
	 *
	 *	@param	str			The string.
	 *
	 *	@param	styleInfo	The style information.
	 */

	public StyledString (String str, StyleInfo styleInfo) {
		this.str = str;
		this.styleInfo = styleInfo;
	}

	/** Constructs a new plain text styled string.
	 *
	 *	@param	str			The string.
	 */

	public StyledString (String str) {
		this.str = str;
		this.styleInfo = null;
	}

	/** Constructs a new plain text styled string.
	 *
	 *	@param	strBuffer			The string.
	 */

	public StyledString (StringBuffer strBuffer) {
		if (strBuffer != null )
			this.str = strBuffer.toString();
		else
			this.str = null;
		this.styleInfo = null;
	}

	/**	Trims leading and trailing whitespace.
	 */

	public void trim () {
		StyleRun[] info = styleInfo == null ? null : styleInfo.info;
		if (str == null || info == null) {
			str = StringUtils.trim(str);
			styleInfo = null;
			return;
		}
		int strLen = str.length();
		int trimBegin = 0;
		while (trimBegin < strLen &&
			Character.isWhitespace(str.charAt(trimBegin)))
				trimBegin++;
		int trimEnd = strLen-1;
		while (trimEnd >= trimBegin &&
			Character.isWhitespace(str.charAt(trimEnd)))
				trimEnd--;
		trimEnd++;
		if (trimBegin == 0 && trimEnd == strLen) return;
		if (trimEnd == trimBegin) {
			str = null;
			styleInfo = null;
			return;
		}
		int newLen = trimEnd - trimBegin;
		int newInfoLen = 0;
		for (int i = 0; i < info.length; i++) {
			StyleRun run = info[i];
			run.start = Math.max(0, run.start - trimBegin);
			run.end = Math.min(newLen, run.end - trimBegin);
			run.end = Math.max(0, run.end);
			if (run.end > run.start) newInfoLen++;
		}
		if (newInfoLen < info.length) {
			StyleRun newInfo[] = new StyleRun[newInfoLen];
			int j = 0;
			for (int i = 0; i < info.length; i++) {
				StyleRun run = info[i];
				if (run.start < run.end) {
					newInfo[j] = run;
					j++;
				}
			}
			styleInfo.info = newInfo;
		}
		str = str.substring(trimBegin, trimEnd);
	}

	/**	Compares this styled string to another styled string.
	 *
	 *	@param	obj		The other styled string.
	 *
	 *	@return			True if the two styled strings are equal.
	 */

	public boolean equals (Object obj) {
		if (!(obj instanceof StyledString)) return false;
		StyledString other = (StyledString)obj;
		if (str == null) return other.str == null;
		if (other.str == null) return str == null;
		if (!str.equals(other.str)) return false;
		if (styleInfo == null) return other.styleInfo == null;
		return styleInfo.equals(other.styleInfo);
	}

	/** Returns the length of the string.
	 *
	 *	@return		The length of the plain text.
	 */

	public int length () {
		return str == null ? 0 : str.length();
	}

	/** Returns a plain string representation.
	 *
	 *	@return		The plain string.
	 */

	public String toString () {
		return str;
	}

	/**	Returns the supported data flavors.
	 *
	 *	<p>The supported data flavors are STYLED_STRING_FLAVOR (the full
	 *	styled string), RTF_FLAVOR (the rich text format flavor), and
	 *	DataFlavor.stringFlavor (the plain text string without the style
	 *	information).
	 *
	 *	@return		The array of supported data flavors.
	 */

	public DataFlavor[] getTransferDataFlavors () {
		return new DataFlavor[] {
			STYLED_STRING_FLAVOR,
			RTF_FLAVOR,
			DataFlavor.stringFlavor,
		};
	}

	/**	Returns true if a data flavor is supported.
	 *
	 *	@param	flavor		The data flavor.
	 *
	 *	@return				True if the flavor is supported.
	 */

	public boolean isDataFlavorSupported (DataFlavor flavor) {
		return flavor.equals(STYLED_STRING_FLAVOR) ||
			flavor.equals(DataFlavor.stringFlavor) ||
			flavor.equals(RTF_FLAVOR);
	}

	/**	Gets the transfer data for a flavor.
	 *
	 *	@param	flavor		The data flavor.
	 *
	 *	@return				The transfer data.
	 *
	 *	@throws	UnsupportedFlavorException	The flavor is unsupported.
	 */

	public Object getTransferData (DataFlavor flavor)
		throws UnsupportedFlavorException, IOException
	{
		if (flavor.equals(STYLED_STRING_FLAVOR)) {
			return this;
		} else if (flavor.equals(DataFlavor.stringFlavor)) {
			return str;
		} else if (flavor.equals(RTF_FLAVOR)) {
			byte[] byteArray = StyledStringUtils.toRTF( this ).getBytes();
			return new ByteArrayInputStream( byteArray );
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	/**	Returns a copy of the styled string.
	 *
	 *	@return		The copy.
	 */

	public Object clone () {
		StyleInfo clonedStyleInfo = styleInfo == null ? null :
			(StyleInfo)styleInfo.clone();
		return new StyledString(str, clonedStyleInfo);
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

