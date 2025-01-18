package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Countable word data.
 *
 *	<p>
 *	A countable word data object holds the data values retrieved
 *	using a WordHoard CQL word query.  These values are used to construct
 *	tables of counts for further analysis.  Most of the values come from
 *	the {@link edu.northwestern.at.wordhoard.model.Word} entry except
 *	as noted below.
 *	</p>
 *
 *	<p>
 *	The fields are:
 *	</p>
 *
 *	<ul>
 *	<li>The persistence ID of the word to which this data pertains.
 *		</li>
 *	<li>The permanent tag of the word.
 *		</li>
 *	<li>The case and accent insensitive spelling of the word.
 *		</li>
 *	<li>The metrical shape of the word.
 *		</li>
 *	<li>The combined speaker gender for the word.
 *		See {@link edu.northwestern.at.wordhoard.model.speakers.Speech}.
 *		</li>
 *	<li>The combined speaker mortality for the word.
 *		See {@link edu.northwestern.at.wordhoard.model.speakers.Speech}.
 *		</li>
 *	<li>A prosody value indicating if the word appears in poetry or prose.
 *		</li>
 *	<li>The persistence ID of the work in which this word appears.
 *		</li>
 *	<li>The persistence ID of the work part in which this word appears.
 *		</li>
 *	<li>The word part index for the lemma and word class fields below.
 *		Most words will have only one associated countable word data object.
 *		Words comprised of multiple lemmata will have as many countable
 *		word objects as lemmata.  The WordPartIndex entry (starts at 0)
 *		can be used to distinguish data for the lemma and word class objects
 *		of each word part in the word.
 *		</li>
 *	<li>The case and accent insensitive lemma tag for the word part at
 *		the specified word part index.
 *		</li>
 *	<li>The word class tag for the word part at the specified word part index.
 *		</li>
 *	<li>A word part ordinal combining the word ID and the word part index.
 *		</li>
 *	</ul>
 */

public class CountableWordData implements Comparable
{
	/**	Unique persistence id of the word to which this data pertains.
	 */

	protected Long wordId;

	/**	The unique tag of the word.
	 */

	protected String wordTag;

	/**	The case insensitive spelling.
	 */

	protected Spelling spellingInsensitive;

	/**	The metrical shape of the word.
	 */

	protected MetricalShape metricalShape;

	/**	The gender of the speaker(s).
	 */

	protected Gender gender;

	/**	The mortality of the speaker(s). */

	protected Mortality mortality;

	/**	Is this word in prose or poetry. */

	protected Prosodic prosodic;

	/**	The ID of the work in which this word appears.
	 */

	protected Long workId;

	/**	The work part in which this word appears.
	 */

	protected Long workPartId;

	/**	The word part index for the lemma and word class.
	 */

	protected int wordPartIndex;

	/**	The case insensitive lemma tag for the specified word part.
	 */

	protected Spelling lemmaTagInsensitive;

	/**	The word class tag for the specified word part.
	 */

	protected String wordClassTag;

	/**	The word part ordinal.  Combines the word ID and word part index fields.
	 */

	protected Long wordPartOrdinal;

	/**	Create empty countable word object.
	 */

	public CountableWordData()
	{
	};

	/**	Create countable word data object.
	 *
	 *	@param	wordId					The persistence ID for
	 *									the word whose data
	 *									this is.
	 *	@param	wordTag					The tag of word.
	 *	@param	workId					The persistence ID of the work in
	 *									which this word appears.
	 *	@param	workPartId				The persistence ID of the work part
	 *									in which this word appears.
	 *	@param	spellingInsensitive		The case and accent insensitive
	 *									word spelling.
	 *	@param	metricalShape			The metrical shape of the word.
	 *	@param	gender					The combined speaker gender.
	 *	@param	mortality				The combined speaker mortality.
	 *	@param	prosodic				Indivates if this word appears in
	 *									poetry or prose.
	 *	@param	wordPartIndex			Indicates the part of the word
	 *									to which this data pertains.
	 *	@param	lemmaTagInsensitive		The lemma tag for the part of the
	 *									word part specified by wordPartIndex.
	 */

	public CountableWordData
	(
		Long wordId,
		String wordTag ,
		Long workId ,
		Long workPartId ,
		Spelling spellingInsensitive ,
		MetricalShape metricalShape ,
		Gender gender ,
		Mortality mortality ,
		Prosodic prosodic ,
		Integer wordPartIndex ,
		Spelling lemmaTagInsensitive
	)
	{
		this.wordId					= wordId;
		this.wordTag				= wordTag;
		this.workId					= workId;
		this.workPartId				= workPartId;
		this.spellingInsensitive	= spellingInsensitive;
		this.metricalShape			= metricalShape;
		this.gender					= gender;
		this.mortality				= mortality;
		this.prosodic				= prosodic;
		this.wordPartIndex			= wordPartIndex.intValue();
		this.lemmaTagInsensitive	= lemmaTagInsensitive;
		this.wordPartOrdinal		=
			generateWordPartOrdinal( wordId , this.wordPartIndex );
		this.wordClassTag			=
			StringUtils.trim
			(
				StringUtils.deleteUnparenthesizedText(
					lemmaTagInsensitive.getString() )
			);
	}

	/**	Create countable word data object.
	 *
	 *	@param	countableDataValues		Array of objects whose values
	 *									match in type and order the
	 *									countable word data objects
	 *									as listed in the primary
	 *									constructor above.
	 *
	 *	<p>
	 *	This version of the constructor is convenient for converting
	 *	query results to countable data objects.
	 *	</p>
	 */

	public CountableWordData( Object[] countableDataValues )
	{
		this
		(
			(Long)countableDataValues[ 0 ],
			(String)countableDataValues[ 1 ],
			(Long)countableDataValues[ 2 ],
			(Long)countableDataValues[ 3 ],
			(Spelling)countableDataValues[ 4 ],
			(MetricalShape)countableDataValues[ 5 ],
			(Gender)countableDataValues[ 6 ],
			(Mortality)countableDataValues[ 7 ],
			(Prosodic)countableDataValues[ 8 ],
			(Integer)countableDataValues[ 9 ],
			(Spelling)countableDataValues[ 10 ]
		);
	}

	/**	Gets the Word ID to which this data pertains.
	 *
	 *	@return		The word ID.
	 */

	public Long getWordId()
	{
		return wordId;
	}

	/**	Gets the permanent tag of the word to which this data pertains.
	 *
	 *	@return		The word tag.
	 */

	public String getWordTag()
	{
		return wordTag;
	}

	/**	Gets the Work ID to which this data pertains.
	 *
	 *	@return		The work ID.
	 */

	public Long getWorkId()
	{
		return workId;
	}

	/**	Gets the Work part ID to which this data pertains.
	 *
	 *	@return		The work part ID.
	 */

	public Long getWorkPartId()
	{
		return workPartId;
	}

	/**	Gets the spelling for this word.
	 *
	 *	@return		The case and accent insensitive spelling.
	 */

	public Spelling getSpelling()
	{
		return spellingInsensitive;
	}

	/**	Gets the metrical shape.
	 *
	 *	@return		Metrical shape, or "not specified" if null.
	 */

	public String getMetricalShape()
	{
		String result	= "";

		if ( metricalShape != null )
		{
			result = metricalShape.getMetricalShape();
		}

		if ( result == null )
		{
			result	=
				WordHoardSettings.getString
				(
					"notdefined" ,
					"not defined"
				);
		}

		return result;
	}

	/**	Gets the speaker gender.
	 *
	 *	@return		The speaker gender (M=male, F=female, U=mixed/unknown).
	 */

	public String getGender()
	{
		String result	= "U";

		if ( gender != null )
		{
			switch ( gender.getGender() )
			{
				case Gender.MALE	:
					result = "M";
					break;

				case Gender.FEMALE	:
					result = "F";
					break;

				default				:
			}
		}

		return result;
	}

	/**	Gets the speaker mortality.
	 *
	 *	@return		The speaker mortality (M=mortal, I=immortal, U=mixed/unknown).
	 */

	public String getMortality()
	{
		String result	= "U";

		if ( mortality != null )
		{
			switch ( mortality.getMortality() )
			{
				case Mortality.MORTAL	:
					result = "M";
					break;

				case Mortality.IMMORTAL_OR_SUPERNATURAL	:
					result = "I";
					break;

				default				:
			}
		}

		return result;
	}

	/**	Gets the prosody flag.
	 *
	 *	@return		'y' if word is verse, 'n' if prose, ' ' if not specified.
	 */

	public String getProsodic()
	{
		String result	= "U";

		if ( prosodic != null )
		{
			switch ( prosodic.getProsodic() )
			{
				case Prosodic.PROSE	: result = "N";
				case Prosodic.VERSE	: result = "Y";
				default				: result = "U";
			}
        }

		return result;
	}

	/**	Gets the case and accent insensitive lemma tag.
	 *
	 *	@return		Lemma tag.
	 */

	public Spelling getLemmaTag()
	{
		return lemmaTagInsensitive;
	}

	/**	Gets the word class tag.
	 *
	 *	@return		Word class tag.
	 */

	public String getWordClassTag()
	{
		return wordClassTag;
	}

	/**	Gets the word part index.
	 *
	 *	@return		Word part index.
	 */

	public int getWordPartIndex()
	{
		return wordPartIndex;
	}

	/**	Gets the word part ordinal.
	 *
	 *	@return		Word part ordinal.
	 */

	public Long getWordPartOrdinal()
	{
		return wordPartOrdinal;
	}

	/**	Generates a word part ordinal given a word ID and word part index.
	 *
	 *	@param	wordId			The word ID.
	 *	@param	wordPartIndex	The word part index.
	 *	@return	The word part ordinal.
	 */

	public static Long generateWordPartOrdinal
	(
		Long wordId ,
		int wordPartIndex
	)
	{
		return Long.valueOf( ( wordId.longValue() << 32 ) | wordPartIndex );
	}

	/**	Return insensitive spelling for display purposes.
	 *
	 *	@return		Case and accent insensitive spelling.
	 */

	public String toString()
	{
		return spellingInsensitive.getString();
	}

	/**	Returns true if this object is equal to some other object.
	 *
	 *	<p>The objects are equal if their word part ordinals are equal.</p>
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals( Object obj )
	{
		if	(	( obj == null ) ||
				!( obj instanceof CountableWordData ) ) return false;

		CountableWordData other = (CountableWordData)obj;

		return wordPartOrdinal.equals( other.wordPartOrdinal );
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode()
	{
		return wordPartOrdinal.hashCode();
	}

	/**	Implement Comparable interface.
	 *
	 *	@param	obj		Other spelling object to which to compare this object.
	 */

	public int compareTo( Object obj )
	{
		if	(	( obj == null ) ||
				!( obj instanceof CountableWordData ) )
		{
			return Integer.MIN_VALUE;
		}

		return Compare.compare
		(
			wordPartOrdinal ,
			((CountableWordData)obj).wordPartOrdinal
		);
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

