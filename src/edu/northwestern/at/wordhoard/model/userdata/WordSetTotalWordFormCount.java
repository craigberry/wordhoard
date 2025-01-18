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

/**	Total counts of word forms in a word set.
 *
 *	<p>
 *	A word set total word form count entry contains the following:
 *	</p>
 *
 *	<ul>
 *	<li>A unique integer id for the total word form count.</li>
 *	<li>The word form (see {@link WordForms}).</li>
 *	<li>The {@link WordSet} to which this total pertains.</li>
 *	<li>The tag of the {@link WorkPart} to which this total pertains.</li>
 *	<li>The total count of the word form for the work in this word set.</li>
 *	</ul>
 *
 *	@hibernate.class  table="wordhoard.wordsettotalwordformcount"
 */

@Entity
@Table(	name = "wordsettotalwordformcount",
		indexes = {
			@Index(name = "wordForm_index", columnList = "wordForm"),
			@Index(name = "workPartTag_index", columnList = "workPartTag")
		}
)
public class WordSetTotalWordFormCount
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

	/**	The word set.
	 */

	private WordSet wordSet;

	/**	The tag of the work part to which this work part belongs.
	 */

	private String workPartTag;

	/**	The total number of word form entries in the word set.
	 */

	private int wordFormCount;

	/**	Create an empty work word form count object.
	 */

	public WordSetTotalWordFormCount()
	{
	}

	/**	Create a populated total word form count object.
	 * 
	 * @param	wordForm	The word form.
	 * @param	wordSet		The word set.
	 * @param	workPartTag	The tag of the work part to which this work part belongs.
	 * @param	wordFormCount	The total number of word form entries in the word set.
	 */

	public WordSetTotalWordFormCount
	(
		int	wordForm ,
		WordSet wordSet ,
		String workPartTag ,
		int wordFormCount
	)
	{
		this.wordForm		= wordForm;
		this.wordSet		= wordSet;
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

	/**	Get the word set.
	 *
	 *	@return		The word set.
	 *
	 *	@hibernate.many-to-one name="wordSet" access="field"
	 *		foreign-key="word_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wordSet", foreignKey = @ForeignKey(name = "word_index"))
	public WordSet getWordSet()
	{
		return wordSet;
	}

	/**	Get the work.
	 *
	 *	@return		The work tag.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="workPartTag" index="workPartTag_index"
	 *		length="32"
	 */

	@Access(AccessType.FIELD)
	@Column(name = "workPartTag", length = 32)
	public String getWorkPartTag()
	{
		return workPartTag;
	}

	/**	Get the word form count in the word set.
	 *
	 *	@return		The word form count in the word set.
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
		return Integer.valueOf( wordFormCount ).toString();
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

