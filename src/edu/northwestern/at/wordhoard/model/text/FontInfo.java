package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

import java.awt.*;

/**	Font information.
 */
 
public interface FontInfo {

	/**	Gets the font.
	 *
	 *	@return		The font.
	 */
	 
	public Font getFont ();
	
	/**	Gets the line height.
	 *
	 *	@return		The line height in pixels.
	 */
	
	public int getHeight ();
	
	/**	Gets the leading.
	 *
	 *	@return		The leading.
	 */
	 
	public int getLeading ();
	
	/**	Gets the ascent. 
	 *
	 *	@return		The ascent in pixels.
	 */
	
	public int getAscent ();
	
	/**	Gets the descent.
	 *
	 *	@return		The descent in pixels.
	 */
	 
	public int getDescent ();
	
	/**	Gets the width of a string.
	 *
	 *	@param	str		String.
	 *
	 *	@return			Width of the string in pixels.
	 */
	
	public int stringWidth (String str);
	
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

