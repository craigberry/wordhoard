package edu.northwestern.at.wordhoard.model.wrappers;

/*	Please see the license information at the end of this file. */

import org.hibernate.*;

import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.grouping.*;

/**	A narrative attribute wrapper.
 */

public class Narrative implements SearchCriterion, GroupingObject {

	/**	Narration. */

	public static final byte NARRATION = 0;

	/**	Speech. */

	public static final byte SPEECH = 1;

	/**	Narration object. */

	public static final Narrative NARRATION_OBJECT =
		new Narrative(NARRATION);

	/**	Speech object. */

	public static final Narrative SPEECH_OBJECT =
		new Narrative(SPEECH);

	/**	Number of narrative attributes. */

	public static final byte NUM_NARRATIVE = 2;

	/**	The narrative attrbute. */

	private byte narrative;

	/**	Creates a new narrative attribute wrapper.
	 *
	 *	@param	narrative		The narrative attribute.
	 */

	public Narrative (byte narrative) {
		this.narrative = narrative;
	}

	/**	Gets the narrative attribute.
	 *
	 *	@return		The narrative attribute.
	 */

	public byte getNarrative () {
		return narrative;
	}

	/**	Returns true if narration.
	 *
	 *	@return		True if narration, false if speech.
	 */

	public boolean isNarration () {
		return narrative == NARRATION;
	}

	/**	Returns true if speech.
	 *
	 *	@return		True if speech, false if narration.
	 */

	public boolean isSpeech () {
		return narrative == SPEECH;
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
		return narrative == NARRATION ? "word.speech = null" :
			"word.speech != null";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
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
		line.appendRun("narration or speech = " + toString(), romanFontInfo);
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "in".
	 */

	public String getReportPhrase () {
		return "in";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		return new Spelling(toString(), TextParams.ROMAN);
	}

	/**	Returns a string representation of the narrative attribute.
	 *
	 *	@return		String representation.
	 */

	public String toString () {
		return narrative == NARRATION ? "narration" : "speech";
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Narrative)) return false;
		Narrative other = (Narrative)obj;
		return narrative == other.narrative;
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return narrative;
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

