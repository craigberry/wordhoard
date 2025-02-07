package edu.northwestern.at.wordhoard.model.userdata;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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

/**	Count of a single word in a {@link WordSet}.
 *
 *	<p>
 *	A word set word count entry contains the following:
 *	</p>
 *
 *	<ul>
 *	<li>A unique integer id for the word count.</li>
 *	<li>The word text.  May be a spelling, lemma, gender, etc.</li>
 *	<li>The word form (see {@link WordForms}).</li>
 *	<li>The {@link WordSet} to which this count pertains.</li>
 *	<li>The tag of the {@link WorkPart} to which this count pertains.</li>
 *	<li>The count of the word form in this word set in the specified
 *		work part.</li>
 *	</ul>
 *
 *	@hibernate.class  table="wordhoard.wordsetwordcount"
 */

@Entity
@Table(name = "wordsetwordcount",
	indexes = {
		@Index(name = "wordcount_wordForm_index", columnList = "wordForm"),
		@Index(name = "workPartTag_index", columnList = "workPartTag")
	}
)
public class WordSetWordCount
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

	/**	The word set in which this word appears.
	 */

	private WordSet wordSet;

	/**	The tag of the work part in which this word appears.
	 */

	private String workPartTag;

	/**	The word count in the specified work in the word set.
	 */

	private int wordCount;

	/**	Create an empty word form count object.
	 */

	public WordSetWordCount()
	{
	}

	/**	Create a populated word form count object.
	 * @param	word		The word text.
	 * @param	wordForm	The word form.
	 * @param	wordSet		The word set in which this word appears.
	 * @param	workPartTag	The tag of the work part in which this word appears.
	 * @param	wordCount	The word count in the specified work in the word set.
	 */

	public WordSetWordCount
	(
		Spelling word ,
		int	wordForm ,
		WordSet wordSet ,
		String workPartTag ,
		int wordCount
	)
	{
		this.word			= word;
		this.wordForm		= wordForm;
		this.wordSet		= wordSet;
		this.workPartTag	= workPartTag;
		this.wordCount		= wordCount;
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

	/**	Get the word text.
	 *
	 *	@return		The word text.
	 *
	 *	@hibernate.component prefix="word_"
	 */

	@Access(AccessType.FIELD)
	@AttributeOverrides({
		@AttributeOverride(name = "string", column = @Column(name = "word_string")),
		@AttributeOverride(name = "charset", column = @Column(name = "word_charset"))
	})
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
	 *	@hibernate.column name="wordForm" index="wordcount_wordForm_index"
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
	 *		foreign-key="wordSet_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wordSet", foreignKey = @ForeignKey(name = "wordSet_index"))
	public WordSet getWordSet()
	{
		return wordSet;
	}

	/**	Get the tag of the work part to which this count pertains.
	 *
	 *	@return		The work part tag.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="workPartTag" index="workPartTag_index"
	 *		length="32"
	 */

	@Access(AccessType.FIELD)
	@Column(length = 32)
	public String getWorkPartTag()
	{
		return workPartTag;
	}

	/**	Get the word count in the word set.
	 *
	 *	@return		The word count in the word set.
	 *
	 *	@hibernate.property name="wordCount" access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
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

