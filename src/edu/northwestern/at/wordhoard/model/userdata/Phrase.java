package edu.northwestern.at.wordhoard.model.userdata;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.wordhoard.model.*;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**	A phrase.
 *
 *  <p>
 *	A phrase contains an ordered list of adjacent words.
 *	</p>
 *
 *	@hibernate.class table="wordhoard.phrase"
 *
 */

@Entity
@Table(name = "phrase")
public class Phrase
	implements java.io.Serializable
{
	/**	Unique persistence id (primary key). */

	protected Long id			= null;

	/**	The words in the phrase, in order. */

	protected List<String> wordTags		= new ArrayList<String>();

	/**	The tag of the work in which this phrase occurs.
	 */

	protected String workTag	= null;

	/**	A hash code for the word tags.
	 */

	protected int tagsHashCode	= 0;

	/**	Create an empty phrase.
	 */

	public Phrase()
	{
	}

	/**	Create a phrase with specified words.
	 *
	 *	@param	words		The words in the phrase.
	 */

	public Phrase( Word[] words )
	{
		if ( ( words == null ) || ( words.length == 0 ) ) return;

		String[] wordTags	= new String[ words.length ];

		for ( int i = 0 ; i < words.length ; i++ )
		{
			wordTags[ i ]	= words[ i ].getTag();
		}

		addWordTags( wordTags , words[ 0 ].getWork().getTag() );
	}

	/**	Create a phrase with specified word tags.
	 *
	 *	@param	wordTags	The word tags of the words in the phrase.
	 *	@param	workTag		The work tag for the words.
	 */

	public Phrase( String[] wordTags , String workTag )
	{
		addWordTags( wordTags , workTag );
	}

	/**	Add word tags.
	 *
	 *	@param	wordTags	The word tags of the words in the phrase.
	 *	@param	workTag		The work tag for the words.
	 */

	protected void addWordTags( String[] wordTags , String workTag )
	{
		if ( ( wordTags != null ) && ( wordTags.length > 0 ) )
		{
			tagsHashCode	= 0;

			StringBuffer sb	= new StringBuffer();

			for ( int i = 0 ; i < wordTags.length ; i++ )
			{
				this.wordTags.add( wordTags[ i ] );
				sb.append( wordTags[ i ] );
			}

			tagsHashCode	= sb.toString().hashCode();

			this.workTag	= workTag;
		}
	}

	/**	Gets the unique id.
	 *
	 *	@return			The unique id.
     *
     *	@hibernate.id	generator-class="native" access="field"
	 */

	@Access(AccessType.FIELD)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId()
	{
		return id;
	}

	/**	Gets word tags.
	 *
	 *	@return		The word tags.
	 *
	 *	@hibernate.list name="wordTags" table="phrase_wordtags"
	 *		inverse="false" access="field" lazy="false"
	 *	@hibernate.collection-key column="phraseId"
	 *		foreign-key="phraseid_index"
	 *	@hibernate.collection-index column="word_index"
	 *	@hibernate.collection-element column="wordTag"
	 *		type="java.lang.String"
	 *		length="32"
	 */

	@Access(AccessType.FIELD)
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "phrase_wordtags",
		joinColumns = {
			@JoinColumn(name = "phraseId", foreignKey = @ForeignKey(name = "phraseid_index"))
		}
	)
	@OrderColumn(name = "word_index")
	@Column(name = "wordTag", columnDefinition = "varchar(32)")
	public List<String> getWordTags()
	{
		return wordTags;
	}

	/**	Get work tag.
	 *
	 *	@return		Tag of work in which this phrase occurs.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="workTag" length="32"
	 */

	@Access(AccessType.FIELD)
	@Column(length = 32)
	public String getWorkTag()
	{
		return workTag;
	}

	/**	Get hash code for word tags.
	 *
	 *	@return		Hash code.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="tagsHashCode"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	protected int getTagsHashCode()
	{
		return tagsHashCode;
	}

	/**	Clears the phrase.
	 */

	public void clear()
	{
		wordTags.clear();
		workTag	= null;
	}

	/**	Gets a tag for the phrase.
	 *
	 *	@return		The word tags in the phrase set, in order.
	 */

	@Transient
	public String getTag()
	{
		StringBuffer sb	= new StringBuffer();

		if ( wordTags != null )
		{
			for ( int i = 0 ; i < wordTags.size() ; i++ )
			{
				if ( i > 0 ) sb.append( " " );
				sb.append( (String)wordTags.get( i ) );
			}
		}

		return sb.toString();
	}

	/**	Gets a string representation of the phrase.
	 *
	 *	@return		The word tags in the phrase set.
	 */

	public String toString()
	{
		return getTag();
	}

	/**	Returns true if some other phrase is equal to this one.
	 *
	 *	<p>The two phrases are equal if their word tag lists are equal.</p>
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals( Object obj )
	{
		if ( ( obj == null ) || !( obj instanceof Phrase ) ) return false;

		Phrase other	= (Phrase)obj;

		return
			( hashCode() == other.hashCode() ) &&
			( getTag().equals( other.getTag() ) );
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode()
	{
		return tagsHashCode;
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

