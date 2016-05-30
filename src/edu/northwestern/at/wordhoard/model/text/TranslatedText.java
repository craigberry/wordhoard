package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.awt.*;

/**	Text with translations.
 *
 *	<p>This class takes primary text plus one or more text translations
 *	and merges them together into a text object which has the translated
 *	lines immediately following each primary text line. This is like
 *	a "perfect shuffle" of multiple decks of cards into one big deck of
 *	cards.
 *
 *	<p>Each translation text object must have exactly the same number of
 *	lines as the primary text object, in one-to-one correspondence.
 */
 
public class TranslatedText extends Text {

	/**	The primary text. */
	
	private Text primaryText;
	
	/**	Array mapping translated text line indexes to primary text line
	 *	indexes. The value is -1 for non-primary lines.
	 */
	 
	private int[] viewToModelLineIndexes;
	
	/**	Array mapping primary text line indexes to translated text line
	 *	indexes.
	 */
	 
	private int[] modelToViewLineIndexes;
	
	/**	Creates new translated text.
	 *
	 *	@param	primaryText		Primary text.
	 *
	 *	@param	translations	Translations.
	 *
	 *	@throws	Exception		Translation has wrong number of lines.
	 */
	 
	public TranslatedText (Text primaryText, Text[] translations) 
		throws Exception
	{
		super(primaryText.hasLineNumbers(), primaryText.hasMarginalia());
		this.primaryText = primaryText;
		setCollapseBlankLines(false);
		int numTranslations = translations.length;
		int numTexts = numTranslations+1;
		TextLine[][] lines = new TextLine[numTexts][];
		lines[0] = primaryText.getLines();
		int numLines = lines[0].length;
		viewToModelLineIndexes = new int[numTexts*numLines];
		modelToViewLineIndexes = new int[numLines];
		int translatedLineIndex = 0;
		for (int i = 0; i < numTranslations; i++) {
			Text translation = translations[i];
			TextLine[] translationLines = translation.getLines();
			if (translationLines.length != numLines)
				throw new Exception(
					"Translation " + i + " has wrong number of lines");
			lines[i+1] = translationLines;
		}
		for (int i = 0; i < numLines; i++) {
			for (int j = 0; j < numTexts; j++) {
				TextLine line = lines[j][i];
				if (j == 0 || !line.isEmpty()) {
					copyLine(line);
					if (j == 0) {
						modelToViewLineIndexes[i] = translatedLineIndex;
						viewToModelLineIndexes[translatedLineIndex] = i;
					} else {
						viewToModelLineIndexes[translatedLineIndex] = -1;
						TextRun[] runs = line.getRuns();
						for (int k = 0; k < runs.length; k++) {
							TextRun run = runs[k];
							run.setColor(Color.gray);
						}
					}
					translatedLineIndex++;
				}	
			}
		}
		finalize();
		for (int i = translatedLineIndex; 
			i < viewToModelLineIndexes.length; 
			i++)
				viewToModelLineIndexes[i] = -1;
	}
	
	/**	Converts a location from derived to base coordinates.
	 *
	 *	<p>If the location is in a translated line, the returned base
	 *	location is at the end of the earliest preceding primary line.
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
		if (index < 0 || index >= viewToModelLineIndexes.length) {
			index = -1;
		} else {
			int modelIndex = viewToModelLineIndexes[index];
			if (modelIndex != -1) {
				index = modelIndex;
			} else {
				while (index > 0) {
					index--;
					modelIndex = viewToModelLineIndexes[index];
					if (modelIndex != -1) break;
				}
				index = modelIndex;
				offset = Integer.MAX_VALUE;
			}
		}
		if (index == -1) offset = 0;
		TextLocation primaryLocation = new TextLocation(index, offset);
		return primaryText.derivedToBase(primaryLocation);
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
		loc = primaryText.baseToDerived(loc);
		int index = loc.getIndex();
		int offset = loc.getOffset();
		if (index < 0 || index >= modelToViewLineIndexes.length) {
			index = -1;
		} else {
			index = modelToViewLineIndexes[index];
		}
		if (index == -1) offset = 0;
		return new TextLocation(index, offset);
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

