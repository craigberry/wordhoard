package edu.northwestern.at.wordhoard.model.text;

/*	Please see the license information at the end of this file. */

/**	Text parameters.
 */

public class TextParams {

	/**	Roman character set. 
	 *
	 *	<p>This character set is used by English text (e.g., the text
	 *	for the Chaucer and Shakespeare corpora, and all work title
	 *	pages).
	 */
	 
	public static final byte ROMAN = 0;
	
	/**	Polytonic Greek character set.
	 *
	 *	<p>This character set is used by Greek text (the text for the
	 *	Early Greek Epic corpus).
	 */
	 
	public static final byte GREEK = 1;
	
	/**	Number of character sets. */
	
	public static final int NUM_CHARSETS = 2;

	/**	The nominal font size. */
	
	public static final byte NOMINAL_FONT_SIZE = 14;
	
	/**	Blank line font size. */
	
	public static final byte BLANK_LINE_FONT_SIZE = 8;
	
	/**	Work title font size (for title pages). */
	
	public static final byte WORK_TITLE_FONT_SIZE = 18;
	
	/**	Author name font size (for title pages). */
	
	public static final byte AUTHOR_FONT_SIZE = 14;
	
	/**	Responsibility section name font size (for title pages). */
	
	public static final byte RESP_NAME_FONT_SIZE = 12;
	
	/**	Responsibility section responsibility font size (for title pages). */
	
	public static final byte RESP_RESP_FONT_SIZE = 12;
	
	/**	Publication statement font size (for title pages). */
	
	public static final byte PUB_STMT_FONT_SIZE = 12;
	
	/**	Cast of characters page title font size. */
	
	public static final byte CAST_TITLE_FONT_SIZE = 14;
	
	/**	Cast of characters page body font size. */
	
	public static final byte CAST_BODY_FONT_SIZE = 14;
	
	/**	Part title font size. */
	
	public static final byte PART_TITLE_FONT_SIZE = 14;
	
	/**	Translated line font size. */
	
	public static final byte TRANSLATED_LINE_FONT_SIZE = 12;
	
	/**	Iliad scholia font size. */
	
	public static final byte ILIAD_SCHOLIA_FONT_SIZE = 14;
	
	/**	Iliad scholia superscript font size. */
	
	public static final byte ILIAD_SCHOLIA_SUPERSCRIPT_FONT_SIZE = 10;
	
	/**	Annotation font size. */
	
	public static final byte ANNOTATION_FONT_SIZE = 14;
	
	/**	Line number font size. */
	
	public static final int LINE_NUMBERS_SIZE = 12;
	
	/**	Cast of characters page indentation. */
	
	public static final int CAST_INDENTATION = 20;
	
	/**	Speech indentation. */
	
	public static final int SPEECH_INDENTATION = 40;
	
	/**	Extra indentation for translated lines. */
	
	public static final int TRANSLATION_INDENTATION = 20;
	
	/**	Indentation for Spenser stanzas. */
	
	public static final int SPENSER_INDENTATION = 15;
	
	/**	The right margin for text which has neither line numbers
	 *	nor marginalia. */
	
	public static final int RIGHT_MARGIN_PLAIN = 440;
	
	/**	The right margin for text which has line numbers. */

	public static final int RIGHT_MARGIN_NUMBERS = 400;
	
	/**	The right margin for annotation text with marginalia. */
	
	public static final int RIGHT_MARGIN_MARGINALIA = 365;
	
	/**	The right margin for the line numbers. */

	public static final int LINE_NUMBERS_RIGHT = 440;
	
	/**	The left margin for annotation marginalia. */
	
	public static final int MARGINALIA_LEFT = 375;
	
	/**	The right margin for annotation marginalia. */
	
	public static final int MARGINALIA_RIGHT = 450;
	
	/**	The marker offset (left margin offset). */
	
	public static final int MARKER_OFFSET = 10;
	
	/**	The expansion factor for the extended style. */
	
	public static final double EXTENDED_EXPANSION_FACTOR = 1.5;

	/**	Hides the default no-arg constructor.
	 */
	 
	private TextParams () {
		throw new UnsupportedOperationException();
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

