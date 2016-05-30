package edu.northwestern.at.wordhoard.model.search;

/*	Please see the license information at the end of this file. */

import java.text.*;

import org.hibernate.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	A spelling with a collation strength search criterion.
 */

public class SpellingWithCollationStrength implements SearchCriterion {

	/**	The spelling.
	 *
	 *	<p>The spelling string may contain '*' match-any wild card
	 *	characters. Each run of '*' characters is replaced by a single
	 *	'*' character.
	 */

	private Spelling spelling;

	/**	The collation strength. */

	private int strength;

	/**	Creates a new spelling with collation strength object.
	 *
	 *	<p>The collation strengths are:
	 *
	 *	<ul>
	 *	<li>Collator.PRIMARY: Case and diacritical insensitive.
	 *	<li>Collator.SECONDARY: Case insensitive, diacritical sensitive.
	 *	<li>Collator.TERTIARY: Case and diacritical sensitive.
	 *	</ul>
	 *
	 *	@param	spelling	Spelling. Each run of wild card '*' characters is
	 *						replaced by a single '*' character.
	 *
	 *	@param	strength	Collation strength.
	 */

	public SpellingWithCollationStrength (Spelling spelling, int strength) {
		String str = spelling.getString();
		byte charset = spelling.getCharset();
		str = str.replaceAll("(\\*)+", "*");
		this.spelling = new Spelling(str, charset);
		this.strength = strength;
	}

	/**	Gets the spelling.
	 *
	 *	@return 	The spelling.
	 */

	public Spelling getSpelling () {
		return spelling;
	}

	/**	Gets the collation strength.
	 *
	 *	@return		The collation strength.
	 */

	public int getStrength () {
		return strength;
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass () {
		return null;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause () {
		if (spelling.getString().indexOf('*') >= 0) {
			return "word.spellingInsensitive.string like :spellingInsensitive";
		} else {
			return "word.spellingInsensitive.string = :spellingInsensitive";
		}
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		String str = spelling.getString();
		str = CharsetUtils.translateToInsensitive(str);
		str = str.replace('*', '%');
		q.setString("spellingInsensitive", str);
	}

	/**	Appends a description to a text line.
	 *
	 *	@param	line			Text line.
	 *
	 *	@param	romanFontInfo	Roman font info.
	 *
	 *	@param	fontInfo		Array of font info indexed by character
	 *							set.
	 */

	public void appendDescription (TextLine line, FontInfo romanFontInfo,
		FontInfo[] fontInfo)
	{
		line.appendRun("spelling = ", romanFontInfo);
		FontInfo spellingFontInfo = fontInfo[spelling.getCharset()];
		line.appendRun(spelling.getString(), spellingFontInfo);
		String strengthString = null;
		switch (strength) {
			case Collator.PRIMARY:
				strengthString =
					" (case and diacritical insensitive)";
				break;
			case Collator.SECONDARY:
				strengthString =
					" (case insensitive, diacritical sensitive)";
				break;
			case Collator.TERTIARY:
				strengthString =
					" (case and diacritical sensitive)";
				break;
		};
		line.appendRun(strengthString, romanFontInfo);
	}

	/**	Returns true if this object is equal to some other object.
	 *
	 *	<p>The SpellingWithCollationStrength are equal if their strings are identical and
	 *	they have the same character set and their collation strength is the same.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof SpellingWithCollationStrength)) return false;
		SpellingWithCollationStrength other = (SpellingWithCollationStrength)obj;
		return spelling.equals(other.getSpelling()) && strength == other.getStrength();
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return spelling.hashCode() | strength;
	}

	/**	Implement Comparable interface.
	 *
	 *	@param	obj		Other spelling object to which to compare this object.
	 */

	public int compareTo (Object obj) {
		if (obj == null || !(obj instanceof SpellingWithCollationStrength)) return Integer.MIN_VALUE;
		int result = Compare.compare(spelling.getString(), ((SpellingWithCollationStrength)obj).getSpelling().getString());
		if (result == 0)
			result = Compare.compare(strength, ((SpellingWithCollationStrength)obj).getStrength());
		return result;
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

