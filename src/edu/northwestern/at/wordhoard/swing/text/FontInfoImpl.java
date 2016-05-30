package edu.northwestern.at.wordhoard.swing.text;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.text.*;

/**	Font information.
 *
 *	<p>On the Mac we give the "Times" font two pixels of additional spacing
 *	between lines.
 */
 
public class FontInfoImpl implements FontInfo {
	
	/**	A component for getting font metrics info. */
	
	private static JComponent component = new JPanel();

	/**	Font. */
	
	private Font font;

	/**	Font metrics. */

	private FontMetrics metrics;
	
	/**	Line height. */
	
	private int height;
	
	/**	Leading. */
	
	private int leading;
	
	/**	Ascent. */

	private int ascent;
	
	/**	Descent. */
	
	private int descent;
	
	/**	Creates a new font information object.
	 *
	 *	@param	font		Font.
	 */
	
	public FontInfoImpl (Font font) {
		this.font = font;
		metrics = component.getFontMetrics(font);
		height = metrics.getHeight();
		leading = metrics.getLeading();
		ascent = metrics.getAscent();
		descent = metrics.getDescent();
		if (Env.MACOSX && font.getFamily().equals("Times")) {
			height += 2;
			leading += 2;
		}
	}
	
	/**	Creates a new font information object.
	 *
	 *	@param	family		Family.
	 *
	 *	@param	style		Style.
	 *
	 *	@param	size		Size.
	 */
	
	public FontInfoImpl (String family, int style, int size) {
		this(new Font(family, style, size));
	}
	
	/**	Gets the font.
	 *
	 *	@return		The font.
	 */
	 
	public Font getFont () {
		return font;
	}
	
	/**	Gets the line height.
	 *
	 *	@return		The line height in pixels.
	 */
	
	public int getHeight () {
		return height;
	}
	
	/**	Gets the leading. 
	 *
	 *	@return		The leading in pixels.
	 */
	
	public int getLeading () {
		return leading;
	}
	
	/**	Gets the ascent. 
	 *
	 *	@return		The ascent in pixels.
	 */
	
	public int getAscent () {
		return ascent;
	}
	
	/**	Gets the descent. 
	 *
	 *	@return		The descent in pixels.
	 */
	
	public int getDescent () {
		return descent;
	}
	
	/**	Gets the width of a string.
	 *
	 *	@param	str		String.
	 *
	 *	@return			Width of the string in pixels.
	 */
	
	public int stringWidth (String str) {
		return metrics.stringWidth(str);
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

