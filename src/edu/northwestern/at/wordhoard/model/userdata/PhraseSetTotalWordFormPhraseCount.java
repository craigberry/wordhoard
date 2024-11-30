package edu.northwestern.at.wordhoard.model.userdata;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**	Total counts of phrase forms in a phrase set.
 *
 *	<p>
 *	A phrase set total phrase form count entry contains the following:
 *	</p>
 *
 *	<ul>
 *	<li>A unique integer id for the total phrase form count.</li>
 *	<li>The word form (see {@link WordForms}) for the phrase.</li>
 *	<li>The {@link PhraseSet} to which this total pertains.</li>
 *	<li>The tag of the {@link WorkPart} to which this total pertains.</li>
 *	<li>The total count of the word form for the work part in this
 *		phrase set.</li>
 *	</ul>
 *
 *	@hibernate.class  table="wordhoard.phrasesettotalwordformcount"
 */

@Entity
@Table(name = "phrasesettotalwordformcount",
	indexes = {
		@Index(name = "wordForm_index", columnList = "wordForm"),
		@Index(name = "workPartTag_index", columnList = "workPartTag")
	}
)
public class PhraseSetTotalWordFormPhraseCount
	implements java.io.Serializable, PersistentObject
{
	/**	The persistence ID.
	 */

	private Long id;

	/**	The word form.
	 *
	 *	<p>
	 *	See {@link WordForms}.
	 *	</p>
	 */

	private int wordForm;

	/**	The phrase set.
	 */

	private PhraseSet phraseSet;

	/**	The tag of the work part to which this work part belongs.
	 */

	private String workPartTag;

	/**	The total number of phrase entries of the specified word form type
	 *	in the phrase set for the specified work part.
	 */

	private int wordFormCount;

	/**	Create an empty phrase set word form count object.
	 */

	public PhraseSetTotalWordFormPhraseCount()
	{
	}

	/**	Create a populated total word form count object.
	 *	@param	wordForm	The word form.
	 *	@param	phraseSet	The phrase set.
	 *	@param	workPartTag	The tag of the work part to which this work part belongs.
	 *	@param	wordFormCount	The total number of phrase entries.
	 */

	public PhraseSetTotalWordFormPhraseCount
	(
		int	wordForm ,
		PhraseSet phraseSet ,
		String workPartTag ,
		int wordFormCount
	)
	{
		this.wordForm		= wordForm;
		this.phraseSet		= phraseSet;
		this.workPartTag	= workPartTag;
		this.wordFormCount	= wordFormCount;
	}

	/**	Get the persistence id.
      *
      * @hibernate.id	generator-class="native" access="field"
      */

	@Access(AccessType.FIELD)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId()
	{
		return id;
	}

	/**	Get the word form.
	 *
	 *	@return		The word form type.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="wordForm" index="wordForm_index"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getWordForm()
	{
		return wordForm;
	}

	/**	Get the phrase set.
	 *
	 *	@return		The phrase set.
	 *
	 *	@hibernate.many-to-one name="phraseSet" access="field"
	 *		foreign-key="phrase_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "phraseSet", foreignKey = @ForeignKey(name = "phrase_index"))
	public PhraseSet getPhraseSet()
	{
		return phraseSet;
	}

	/**	Get the work.
	 *
	 *	@return		The work tag.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="workPartTag"
	 *		index="workPartTag_index"
	 *		length="32"
	 */

	@Access(AccessType.FIELD)
	@Column(length = 32)
	public String getWorkPartTag()
	{
		return workPartTag;
	}

	/**	Get the word form count in the phrase set.
	 *
	 *	@return		The word form count in the phrase set.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getWordFormCount()
	{
		return wordFormCount;
	}

	/**	Return string form of this entry.
	 *
	 *	@return		The word form text.
	 */

	public String toString()
	{
		return new Integer( wordFormCount ).toString();
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

