package edu.northwestern.at.wordhoard.model.userdata;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Count of a single phrase in a {@link PhraseSet}.
 *
 *	<p>
 *	A phrase set phrase count entry contains the following:
 *	</p>
 *
 *	<ul>
 *	<li>A unique integer id for the phrase count.</li>
 *	<li>The phrase text.  May be a spelling, lemma, gender, etc.</li>
 *	<li>The word form for the phrase text (see {@link WordForms}).</li>
 *	<li>The {@link PhraseSet} to which this count pertains.</li>
 *	<li>The tag of the {@link WorkPart} to which this count pertains.</li>
 *	<li>The count of the phrase form in this phrase set in the
 *		specified work part.</li>
 *	</ul>
 *
 *	@hibernate.class  table="phrasesetphrasecount"
 */

public class PhraseSetPhraseCount
	implements java.io.Serializable, PersistentObject
{
	/**	The persistence ID.
	 */

	private Long id;

	/**	The phrase text.
	 */

	private Spelling phraseText;

	/**	The word form type.
	 *
	 *	<p>
	 *	See {@link WordForms}.
	 *	</p>
	 */

	private int wordForm;

	/**	The phrase set in which this word appears.
	 */

	private PhraseSet phraseSet;

	/**	The tag of the work part in which this word appears.
	 */

	private String workPartTag;

	/**	The phrase count in the specified work in the phrase set.
	 */

	private int phraseCount;

	/**	Create an empty phrase count object.
	 */

	public PhraseSetPhraseCount()
	{
	}

	/**	Create a populated phrase count object.
	 */

	public PhraseSetPhraseCount
	(
		Spelling phraseText ,
		int	wordForm ,
		PhraseSet phraseSet ,
		String workPartTag ,
		int phraseCount
	)
	{
		this.phraseText		= phraseText;
		this.wordForm		= wordForm;
		this.phraseSet		= phraseSet;
		this.workPartTag	= workPartTag;
		this.phraseCount	= phraseCount;
	}

	/**	Get the persistence id.
      *
      * @hibernate.id	generator-class="native" access="field"
      */

	public Long getId()
	{
		return id;
	}

	/**	Get the phrase text.
	 *
	 *	@return		The phrase text.
	 *
	 *	@hibernate.component prefix="phraseText_"
	 */

	public Spelling getPhraseText()
	{
		return phraseText;
	}

	/**	Set the phrase text.
	 *
	 *	@param	phraseText	The phrase text.
	 */

	protected void setPhraseText( Spelling phraseText )
	{
		this.phraseText	= phraseText;
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

	/**	Get the phrase set.
	 *
	 *	@return		The phrase set.
	 *
	 *	@hibernate.many-to-one name="phraseSet" access="field"
	 *		foreign-key="phraseSet_index"
	 */

	public PhraseSet getPhraseSet()
	{
		return phraseSet;
	}

	/**	Get the tag of the work part to which this count pertains.
	 *
	 *	@return		The work part tag.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="workPartTag"
	 *		index="workPartTag_index"
	 *		length="32"
	 */

	public String getWorkPartTag()
	{
		return workPartTag;
	}

	/**	Get the phrase count.
	 *
	 *	@return		The phrase count.
	 *
	 *	@hibernate.property name="phraseCount" access="field"
	 */

	public int getPhraseCount()
	{
		return phraseCount;
	}

	/**	Return string form of this entry.
	 *
	 *	@return		The word form text.
	 */

	public String toString()
	{
		return phraseText.getString();
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

