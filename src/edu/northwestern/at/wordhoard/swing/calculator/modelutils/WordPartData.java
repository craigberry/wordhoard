package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Word part data.
 *
 *	<p>
 *	A word part data object holds the data values for a word and its
 *	words parts.  It is intended primarily for use in WordHoard scripts.
 *	</p>
 *
 *	<p>
 *	The fields are:
 *	</p>
 *
 *	<ul>
 *	<li>The word to which this data pertains.
 *		</li>
 *	<li>The number of word parts.
 *		</li>
 *	<li>The speech, if any, in which this word occurs..
 *		</li>
 *	<li>The combined speaker gender for the word.
 *		See {@link edu.northwestern.at.wordhoard.model.speakers.Speech}.
 *		</li>
 *	<li>The combined speaker mortality for the word.
 *		See {@link edu.northwestern.at.wordhoard.model.speakers.Speech}.
 *		</li>
 *	<li>The lemPos entries, in word part order, for the word.
 *		</li>
 *	<li>The lemmata, in word part order, for the word.
 *		</li>
 *	<li>The parts of speech, in word part order, for the word.
 *		</li>
 *	<li>The word classes, in word part order, for the word.
 *		</li>
 *	</ul>
 */

public class WordPartData
{
	/**	The word to which this data pertains.
	 */

	protected Word word;

	/**	The number of word parts.
	 */

	protected int wordPartCount	= 0;

	/**	The speech in which this word appears.
	 */

	protected Speech speech;

	/**	The gender of the speaker(s).
	 */

	protected Gender gender;

	/**	The mortality of the speaker(s).
	 */

	protected Mortality mortality;

	/**	The lempos entries.
	 */

	protected ArrayList lemPos;

	/**	The lemmata.
	 */

	protected ArrayList lemmata;

	/**	The parts of speech.
	 */

	protected ArrayList pos;

	/**	The word classes.
	 */

	protected ArrayList wordClasses;

	/**	Create empty word part data object.
	 */

	public WordPartData()
	{
	}

	/**	Create word part data object.
	 *
	 *	@param	word					The word whose data this is.
	 *	@param	gender					The combined speaker gender.
	 *	@param	mortality				The combined speaker mortality.
	 *	@param	lemPos					The lemPos entries.
	 *	@param	lemmata					The lemmata.
	 *	@param	pos						The parts of speech.
	 *	@param	wordClasses				The word classes.
	 */

	public WordPartData
	(
		Word word ,
		Gender gender ,
		Mortality mortality ,
		LemPos[] lemPos ,
		Lemma[] lemmata ,
		Pos[] pos ,
		WordClass[] wordClasses
	)
	{
		this.lemmata			= new ArrayList();
		this.lemPos				= new ArrayList();
		this.pos				= new ArrayList();
		this.wordClasses		= new ArrayList();

		this.word				= word;
		this.gender				= gender;
		this.mortality			= mortality;

		this.lemmata.addAll( Arrays.asList( lemmata ) );
		this.lemPos.addAll( Arrays.asList( lemPos ) );
		this.pos.addAll( Arrays.asList( pos ) );
		this.wordClasses.addAll( Arrays.asList( wordClasses ) );

		this.wordPartCount		= this.lemmata.size();
	}

	/**	Create word part data object.
	 *
	 *	@param	word					The word whose data this is.
	 *	@param	gender					The combined speaker gender.
	 *	@param	mortality				The combined speaker mortality.
	 *	@param	lemPos					The lempos.
	 *	@param	lemma					The lemma.
	 *	@param	pos						The part of speech.
	 *	@param	wordClass				The word class.
	 */

	public WordPartData
	(
		Word word ,
		Gender gender ,
		Mortality mortality ,
		LemPos lemPos ,
		Lemma lemma ,
		Pos pos ,
		WordClass wordClass
	)
	{
		this.word			= word;
		this.wordPartCount	= 1;
		this.gender			= gender;
		this.mortality		= mortality;

		this.lemmata		= new ArrayList();
		this.lemmata.add( lemma );

		this.lemPos			= new ArrayList();
		this.lemPos.add( lemPos );

		this.pos			= new ArrayList();
		this.pos.add( pos );

		this.wordClasses	= new ArrayList();
		this.wordClasses.add( wordClass );
	}

	/**	Gets the Word to which this data pertains.
	 *
	 *	@return		The word.
	 */

	public Word getWord()
	{
		return word;
	}

	/**	Gets the word part count.
	 *
	 *	@return		Word part count.
	 */

	public int getWordPartCount()
	{
		return wordPartCount;
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

	/**	Gets the lemPos entries.
	 *
	 *	@return		LemPos entries.
	 */

	public LemPos[] getLemPos()
	{
		return (LemPos[])lemPos.toArray( new LemPos[]{} );
	}

	/**	Gets the lemmata.
	 *
	 *	@return		Lemmata.
	 */

	public Lemma[] getLemmata()
	{
		return (Lemma[])lemmata.toArray( new Lemma[]{} );
	}

	/**	Gets the parts of speech.
	 *
	 *	@return		Parts of speech.
	 */

	public Pos[] getPos()
	{
		return (Pos[])pos.toArray( new Pos[]{} );
	}

	/**	Gets the word classes.
	 *
	 *	@return		Word classes.
	 */

	public WordClass[] getWordClasses()
	{
		return (WordClass[])wordClasses.toArray( new WordClass[]{} );
	}

	/**	Append data to the existing word part data.
	 *
	 *	@param	lemPos		The new lemPos.
	 *	@param	lemma		The new lemma.
	 *	@param	pos			The new part of speech.
	 *	@param	wordClass	The new word class.
	 *
	 *	<p>
	 *	Note:  the updated data is appended to the end of existing entries.
	 *	The calling method must ensure the data is appended in the
	 *	proper order.
	 *	</p>
	 */

	public void append
	(
		LemPos lemPos ,
		Lemma lemma ,
		Pos pos ,
		WordClass wordClass
	)
	{
		if ( this.lemPos == null ) this.lemPos	= new ArrayList();
		this.lemPos.add( lemPos );

		if ( lemmata == null ) lemmata	= new ArrayList();
		lemmata.add( lemma );

		if ( this.pos == null ) this.pos	= new ArrayList();
		this.pos.add( pos );

		if ( wordClasses == null ) wordClasses	= new ArrayList();
		wordClasses.add( wordClass );

		wordPartCount	= lemmata.size();
	}

	/**	Return word tag for display purposes.
	 *
	 *	@return		Word tag.
	 */

	public String toString()
	{
		return word.getTag();
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
				!( obj instanceof WordPartData ) ) return false;

		WordPartData other = (WordPartData)obj;

		return word.equals( other.getWord() );
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode()
	{
		return word.hashCode();
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


