package edu.northwestern.at.wordhoard.model.counts;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Counts for work parts.
 *
 *	<p>These count objects provide summary statistics at the level of work
 *	parts. Given a work part, we can quickly provide fequency counts for
 *	spellings, lemmas, word classes, speaker gender and mortality attributes, 
 *	prose vs. verse, and metrical shapes. We use a number of indices on this
 *	table to optimize different kinds of queries.</p>
 *
 *	<p>
 *	A word count object contains the following fields:
 *	</p>
 *
 *	<ul>
 *	<li>A unique integer id for the word count.</li>
 *	<li>The word text.  May be a spelling, lemma, gender, etc. See below.</li>
 *	<li>The word form (see {@link WordForms}).</li>
 *	<li>The {@link WorkPart} to which this count pertains.</li>
 *	<li>The {@link Work} to which this work part belongs.</li>
 *	<li>The count of the word form in this work part.</li>
 *	</ul>
 *
 *	<p>
 *	The text of the count object has the following form, depending on the
 *	kind of word form being counted.
 *	</p>
 *
 *	<ul>
 *	<li>wordForm = WordForms.SPELLING: "xxx (yyy)" where "xxx" is the
 *		case-insensitive spelling of the word, and "yyy" is a word class.
 *		E.g., "blood (n)". For words with more than one part, the word classes
 *		of each part are listed separated by hyphens. E.g., "'tis (pnp-vp)".
 *		For words with homonyms, the homonym number of the word's lemma is
 *		listed along with the word class. E.g., "lye (v 2)".</li>
 *	<li>wordForm = WordForms.LEMMA: "xxx (yyy)" where "xxx" is the spelling of
 *		the lemma and "yyy" is the word class of the lemma. E.g., "dread (v)".
 *		For lemmas with homonyms, the homonym number is also listed. E.g.,
 *		"temple (n) (2)". This is the lemma's "tag".</li>
 *	<li>wordForm = WordForms.WORDCLASS: The word class. E.g., "pnp".</li>
 *	<li>wordForm = WordForms.SPEAKERGENDER: "M" for male, "F" for female,
 *		or "U" for uncertain, mixed, or unknown and words which are spoken
 *		by the narrator.</li>
 *	<li>wordForm = WordForms.SPEAKERMORTALITY: "I" for immortal, "M" for
 *		mortal, or "U" for unknown or other and words which are spoken by
 *		the narrator.</li>
 *	<li>wordForm = WordForms.SEMANTICCATEGORY: Currently unused.</li>
 *	<li>wordForm = WordForms.ISVERSE: "Y" for verse, "N" for prose, or "U"
 *		for unknown.</li>
 *	<li>wordForm = WordForms.METRICALSHAPE: The metrical shape, or the
 *		empty string to count up words without defined metrical shapes.</li>
 *	</ul>
 *
 *	<p>The LEMMA and WORDCLASS counters are counts over word parts. The other
 *	forms are all counts over words.</p>
 *
 *	<p>Counts for work parts include their subparts. E.g., the counts for
 *	Act 2 of <i>Hamlet</i> sum up the counts for all the scenes in the act, and
 *	the counts for the work <i>Hamlet</i> sum up the counts for the entire
 *	work.</p>
 *
 *	<p>These count objects are used by the calculator.
 *
 *	@hibernate.class  table="wordcount"
 */

public class WordCount
	implements java.io.Serializable, PersistentObject
{
	/**	The persistence ID.
	 */

	private Long id;

	/**	The word text.
	 */

	private Spelling word;

	/**	The word form type.
	 *
	 *	<p>
	 *	See {@link WordForms}.
	 *	</p>
	 */

	private int wordForm;

	/**	The work part in which this word form appears.
	 */

	private WorkPart workPart;

	/**	The work to which this work part belongs.
	 */

	private Work work;

	/**	The word count in the work part.
	 */

	private int wordCount;

	/**	Create an empty word form count object.
	 */

	public WordCount()
	{
	}

	/**	Create a populated word form count object.
	 * @param	word		The word spelling.
	 * @param	wordForm	The word form type.
	 * @param	workPart	The work part in which the word form appears.
	 * @param	work		The work to which the work part belongs.
	 * @param	wordCount	The word count.
	 */

	public WordCount
	(
		Spelling word ,
		int	wordForm ,
		WorkPart workPart ,
		Work work ,
		int wordCount
	)
	{
		this.word		= word;
		this.wordForm	= wordForm;
		this.workPart	= workPart;
		this.work		= work;
		this.wordCount	= wordCount;
	}

	/**	Get the persistence id.
      *
      * @hibernate.id	generator-class="native" access="field"
      */

	public Long getId()
	{
		return id;
	}

	/**	Get the word text.
	 *
	 *	@return		The word text.
	 *
	 *	@hibernate.component prefix="word_"
	 */

	public Spelling getWord()
	{
		return word;
	}

	/**	Set the word text.
	 *
	 *	@param	word	The word text.
	 */

	protected void setWord( Spelling word )
	{
		this.word	= word;
	}

	/**	Get the word form.
	 *
	 *	@return		The word form type.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="wordForm" index="wordForm_index"
	 */

	public int getWordForm()
	{
		return wordForm;
	}

	/**	Get the work part.
	 *
	 *	@return		The work part.
	 *
	 *	@hibernate.many-to-one name="workPart" access="field"
	 *		foreign-key="workPart_index"
	 */

	public WorkPart getWorkPart()
	{
		return workPart;
	}

	/**	Get the work.
	 *
	 *	@return		The work.
	 *
	 *	@hibernate.many-to-one name="work" access="field"
	 *		foreign-key="work_index"
	 */

	public Work getWork()
	{
		return work;
	}

	/**	Get the word count in the work part.
	 *
	 *	@return		The word count in the work part.
	 *
	 *	@hibernate.property access="field"
	 */

	public int getWordCount()
	{
		return wordCount;
	}

	/**	Return string form of this entry.
	 *
	 *	@return		The word form text.
	 */

	public String toString()
	{
		return word.getString();
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

