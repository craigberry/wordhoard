package edu.northwestern.at.utils.swing.styledtext;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;

/**	A style run.
 *
 *	<p>This lightweight class records style information for a run of
 *	characters in a string.
 *
 *	<ul>
 *	<li>start = starting offset of the run.
 *	<li>end = ending offset of the run.
 *	<li>kind = kind of style (bold, italic, underline, strikethrough,
 *		alignment, font family, font size, color, or emoticon).
 *	<li>param = parameter, if any (left/center/right/justified alignment,
 *		serif/sansserif/monospaced font family, font size, color, or
 *		emoticon).
 *	</ul>
 */

public class StyleRun implements Cloneable {

	/**	Bold style. */

	public static final int BOLD = 0;

	/**	Italic style. */

	public static final int ITALIC = 1;

	/**	Underline style. */

	public static final int UNDERLINE = 2;

	/**	Strikethrough style. */

	public static final int STRIKETHROUGH = 3;

	/**	Alignment style. */

	public static final int ALIGNMENT = 4;

	/**	Left alignment. */

	public static final int ALIGNMENT_LEFT = 0;

	/**	Center alignment. */

	public static final int ALIGNMENT_CENTER = 1;

	/**	Right alignment. */

	public static final int ALIGNMENT_RIGHT = 2;

	/**	Fully justified alignment. */

	public static final int ALIGNMENT_JUSTIFIED = 3;

	/**	Font family style. */

	public static final int FAMILY = 5;

	/**	Serif font family. */

	public static final int FAMILY_SERIF = 0;

	/**	Sans-serif font family. */

	public static final int FAMILY_SANS_SERIF = 1;

	/**	Monospaced font family. */

	public static final int FAMILY_MONOSPACED = 2;

	/**	Font size style. */

	public static final int SIZE = 6;

	/**	Smallest font size. */

	public static final int SIZE_SMALLEST = 0;

	/**	Smaller font size. */

	public static final int SIZE_SMALLER = 1;

	/**	Small font size. */

	public static final int SIZE_SMALL = 2;

	/**	Normal font size. */

	public static final int SIZE_NORMAL = 3;

	/**	Big font size. */

	public static final int SIZE_BIG = 4;

	/**	Bigger font size. */

	public static final int SIZE_BIGGER = 5;

	/**	Biggest font size. */

	public static final int SIZE_BIGGEST = 6;

	/**	Color style. */

	public static final int COLOR = 7;

	/**	Red. */

	public static final int COLOR_RED = 0;

	/**	Blue. */

	public static final int COLOR_BLUE = 1;

	/**	Green. */

	public static final int COLOR_GREEN = 2;

	/**	Black. */

	public static final int COLOR_BLACK = 3;

	/**	Emoticon "style". */

	public static final int EMOTICON = 8;

	/**	Smile emoticon. */

	public static final int EMOTICON_SMILE = 0;

	/**	Sad emoticon. */

	public static final int EMOTICON_SAD = 1;

	/**	Angry emoticon. */

	public static final int EMOTICON_ANGRY = 2;

	/**	Sick emoticon. */

	public static final int EMOTICON_SICK = 3;

	/**	Tired emoticon. */

	public static final int EMOTICON_TIRED = 4;

	/**	Devil emoticon. */

	public static final int EMOTICON_DEVIL = 5;

	/**	Big grin emoticon. */

	public static final int EMOTICON_BIGGRIN = 6;

	/**	Tongue emoticon. */

	public static final int EMOTICON_TONGUE = 7;

	/**	Attention emoticon. */

	public static final int EMOTICON_ATTENTION = 8;

	/**	Alert emoticon. */

	public static final int EMOTICON_ALERT = 9;

	/**	Idea emoticon. */

	public static final int EMOTICON_IDEA = 10;

	/**	Star icon "style". */

	public static final int STARICON = 9;

	/** Star icons for no stars to five stars. */

	public static final int STARICON_ZERO				= 0;
	public static final int STARICON_HALF				= 1;
	public static final int STARICON_ONE				= 2;
	public static final int STARICON_ONE_AND_A_HALF		= 3;
	public static final int STARICON_TWO				= 4;
	public static final int STARICON_TWO_AND_A_HALF		= 5;
	public static final int STARICON_THREE				= 6;
	public static final int STARICON_THREE_AND_A_HALF	= 7;
	public static final int STARICON_FOUR				= 8;
	public static final int STARICON_FOUR_AND_A_HALF	= 9;
	public static final int STARICON_FIVE				= 10;

	/** Subscript style. */

	public static final int SUBSCRIPT = 10;

	/** Superscript style. */

	public static final int SUPERSCRIPT = 11;

	/**	The starting offset in the string of this run. */

	public int start;

	/**	The ending offset in the string of this run. */

	public int end;

	/**	The kind of style. */

	public int kind;

	/**	The style parameter, if any. */

	public int param;

	/**	Constructs a new StyleRun object.
	 *
	 *	@param	start	The starting offset in the string of the run.
	 *
	 *	@param	end		The ending offset in the string of the run.
	 *
	 *	@param	kind	The kind of style.
	 *
	 *	@param	param	The style parameter.
	 */

	public StyleRun (int start, int end, int kind, int param) {
		this.start = start;
		this.end = end;
		this.kind = kind;
		this.param = param;
	}

	/**	Constructs a new StyleRun object with param = 0.
	 *
	 *	@param	start	The starting offset in the string of the run.
	 *
	 *	@param	end		The ending offset in the string of the run.
	 *
	 *	@param	kind	The kind of style.
	 */

	public StyleRun (int start, int end, int kind) {
		this(start, end, kind, 0);
	}

	/**	Compares this style run to some other style run.
	 *
	 *	@param	obj		The other style run.
	 *
	 *	@return			True if the two style runs are equal.
	 */

	public boolean equals (Object obj) {
		if (!(obj instanceof StyleRun)) return false;
		StyleRun other = (StyleRun)obj;
		return start == other.start &&
			end == other.end &&
			kind == other.kind &&
			param == other.param;
	}

	/**	Returns a string representation of the style run.
	 *
	 *	@return		The string representation.
	 */

	public String toString () {
		return start + "-" + end + " " + kind + " " + param;
	}

	/**	Returns a copy of the style run.
	 *
	 *	@return		The copy.
	 */

	public Object clone () {
		return new StyleRun(start, end, kind, param);
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

