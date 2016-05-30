package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.font.*;
import javax.swing.*;

/**	A text drawing context.
 */
 
public class DrawingContext {

	/**	Graphics environment. */

	private Graphics2D g;
	
	/**	Font render context. */
	
	private FontRenderContext frc;
	
	/**	Right margin. */
	
	private int rightMargin;
	
	/**	True if this text has line numbers. */
	
	private boolean hasLineNumbers;
	
	/**	Line number interval. */
	
	private int lineNumberInterval;
	
	/**	Line number font info. */
	
	private FontInfo lineNumberFontInfo;
	
	/**	Line number right margin. */
	
	private int lineNumberRightMargin;
	
	/**	Line number color. */
	
	private Color lineNumberColor;
	
	/**	True if this text has marginalia. */
	
	private boolean hasMarginalia;
	
	/**	Marginalia left margin. */
	
	private int marginaliaLeftMargin;
	
	/**	Marginalia right margin. */
	
	private int marginaliaRightMargin;
	
	/**	Marker offset from left margin. */
	
	private int markerOffset;
	
	/**	Marker string. */
	
	private String markerString;
	
	/**	Marker font info. */
	
	private FontInfo markerFontInfo;
	
	/**	Marker color. */
	
	private Color markerColor;
	
	/**	Selected text background color. */
	
	private Color selectedColor;
	
	/**	Selected text color. */
	
	private Color selectedTextColor;
	
	/**	Y coordinate of top of current line. */
	
	private int yTop;
	
	/**	Height of current line. */
	
	private int lineHeight;
	
	/**	Creates a new drawing context.
	 *
	 *	@param	rightMargin				Right margin.
	 */
	
	public DrawingContext (int rightMargin)
	{
		this.rightMargin = rightMargin;
		JEditorPane c = new JEditorPane();
		this.selectedColor = c.getSelectionColor();
		this.selectedTextColor = c.getSelectedTextColor();
	}
	
	/**	Sets line number information.
	 *
	 *	@param	lineNumberInterval		Line number interval.
	 *
	 *	@param	lineNumberFontInfo		Line number font info. May be null
	 *									if no line numbers will be drawn.
	 *
	 *	@param	lineNumberRightMargin	Line number right margin. Ignored
	 *									if line numbers are not drawn.
	 *
	 *	@param	lineNumberColor			Line number color. May be null
	 *									if no line numbers will be drawn.
	 */
	 
	public void setLineNumbers (int lineNumberInterval, 
		FontInfo lineNumberFontInfo, int lineNumberRightMargin, 
		Color lineNumberColor)
	{
		this.hasLineNumbers = true;
		this.lineNumberInterval = lineNumberInterval;
		this.lineNumberFontInfo = lineNumberFontInfo;
		this.lineNumberRightMargin = lineNumberRightMargin;
		this.lineNumberColor = lineNumberColor;
	}
	
	/**	Sets marginalia margins.
	 *
	 *	@param	marginaliaLeftMargin	Marginalia left margin.
	 *
	 *	@param	marginaliaRightMargin	Marginalia right margin.
	 */
	 
	public void setMarginaliaMargins (int marginaliaLeftMargin,
		int marginaliaRightMargin)
	{
		this.hasMarginalia = true;
		this.marginaliaLeftMargin = marginaliaLeftMargin;
		this.marginaliaRightMargin = marginaliaRightMargin;
	}
	
	/**	Sets marker information.
	 *
	 *	@param	markerOffset			Marker offset from left margin.
	 *
	 *	@param	markerString			Marker string.
	 *
	 *	@param	markerFontInfo			Marker font info.
	 *
	 *	@param	markerColor				Marker color.
	 */
	 
	public void setMarkerInformation (int markerOffset, String markerString,
		FontInfo markerFontInfo, Color markerColor)
	{
		this.markerOffset = markerOffset;
		this.markerString = markerString;
		this.markerFontInfo = markerFontInfo;
		this.markerColor = markerColor;
	}
	
	/**	Gets the graphics environment.
	 *
	 *	@return			The graphics environment.
	 */
	
	public Graphics2D getGraphics () {
		return g;
	}
	
	/**	Sets the graphics environment.
	 *
	 *	@param	g		The graphics environment.
	 */
	
	public void setGraphics (Graphics2D g) {
		this.g = g;
		frc = g.getFontRenderContext();
	}
	
	/**	Gets the font render context.
	 *
	 *	@return		The font render context.
	 */
	
	public FontRenderContext getFontRenderContext () {
		return frc;
	}
	
	/**	Gets the right margin.
	 *
	 *	@return		The right margin.
	 */
	
	public int getRightMargin () {
		return rightMargin;
	}
	
	/**	Returns true if this text has line numbers.
	 */
	 
	public boolean hasLineNumbers () {
		return hasLineNumbers;
	}
	
	/**	Gets the line number interval.
	 *
	 *	@return		The line number interval.
	 */

	public int getLineNumberInterval () {
		return lineNumberInterval;
	}
	
	/**	Sets the line number interval.
	 *
	 *	@param	lineNumberInterval		The line number interval.
	 */
	
	public void setLineNumberInterval (int lineNumberInterval) {
		this.lineNumberInterval = lineNumberInterval;
	}
	
	/**	Gets the line number font info.
	 *
	 *	@return		The line number font info.
	 */
	
	public FontInfo getLineNumberFontInfo () {
		return lineNumberFontInfo;
	}
	
	/**	Gets the line number right margin.
	 *
	 *	@return		The line number right margin.
	 */
	
	public int getLineNumberRightMargin () {
		return lineNumberRightMargin;
	}
	
	/**	Gets the line number color.
	 *
	 *	@return		The line number color.
	 */
	
	public Color getLineNumberColor () {
		return lineNumberColor;
	}
	
	/**	Returns true if this text has marginalia.
	 */
	 
	public boolean hasMarginalia () {
		return hasMarginalia;
	}
	
	/**	Gets the marginalia left margin.
	 *
	 *	@return		The marginalia left margin.
	 */
	
	public int getMarginaliaLeftMargin () {
		return marginaliaLeftMargin;
	}
	
	/**	Gets the marginalia right margin.
	 *
	 *	@return		The marginalia right margin.
	 */
	
	public int getMarginaliaRightMargin () {
		return marginaliaRightMargin;
	}
	
	/**	Gets the marker offset.
	 *
	 *	@return		Marker offset from left margin.
	 */
	 
	public int getMarkerOffset () {
		return markerOffset;
	}
	
	/**	Gets the marker string.
	 *
	 *	@return		Marker string.
	 */
	 
	public String getMarkerString () {
		return markerString;
	}
	
	/**	Gets the marker font info.
	 *
	 *	@return		Marker font info.
	 */
	 
	public FontInfo getMarkerFontInfo () {
		return markerFontInfo;
	}
	
	/**	Gets the marker color.
	 *
	 *	@return		Marker color.
	 */
	 
	public Color getMarkerColor () {
		return markerColor;
	}
	
	/**	Gets the selected text background color.
	 *
	 *	@return			The selected text background color.
	 */
	
	public Color getSelectedColor () {
		return selectedColor;
	}
	
	/**	Gets the selected text color.
	 *
	 *	@return			The selected text color.
	 */
	
	public Color getSelectedTextColor () {
		return selectedTextColor;
	}
	
	/**	Gets the Y coordinate of the top of the current line.
	 *
	 *	@return		Y coordinate of the top of the current line.
	 */
	 
	public int getYTop () {
		return yTop;
	}
	
	/**	Sets the Y coordinate of the top of the current line.
	 *
	 *	@param	yTop	Y coordinate of the top of the current line.
	 */
	 
	public void setYTop (int yTop) {
		this.yTop = yTop;
	}
	
	/**	Gets the height of the current line.
	 *
	 *	@return		Heigth of the current line.
	 */
	 
	public int getLineHeight () {
		return lineHeight;
	}
	
	/**	Sets the height of the current line.
	 *
	 *	@param	lineHeight	Height of the current line.
	 */
	 
	public void setLineHeight (int lineHeight) {
		this.lineHeight = lineHeight;
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

