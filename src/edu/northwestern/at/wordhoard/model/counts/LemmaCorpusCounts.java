package edu.northwestern.at.wordhoard.model.counts;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**	Lemma/corpus counts.
 *
 *	<p>These count objects provide summary statistics for lemma/corpus
 *	combinations. Given a lemma and a corpus, we can quickly provide
 *	the following information using a two-column index on corpus/lemma:
 *
 *	<ul>
 *	<li>The lemma's tag.
 *	<li>The lemma's word class.
 *	<li>The lemma's major word class.
 *	<li>The collection frequency = the number of times the lemma occurs
 *		in the corpus.
 *	<li>The document frequency = the number of works in the corpus in
 *		which the lemma occurs.
 *	<li>The lemma's rank in the corpus among all the lemma's with the
 *		same major word class in the corpus. The rank is specified as a
 *		range.
 *	<li>The total number of distinct lemmas in the corpus with the same
 *		major word class as this lemma.
 *	</ul>
 *
 *	<p>These objects are used to quickly construct get info windows for
 *	words and lemmas and lexicon windows.
 *
 *	@hibernate.class table="lemmacorpuscounts"
 */
 
@Entity
@Table(name="lemmacorpuscounts")
public class LemmaCorpusCounts implements PersistentObject {

	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	The corpus. */
	
	private Corpus corpus;
	
	/**	The lemma. */
	
	private Lemma lemma;
	
	/**	The lemma's tag. */
	
	private Spelling tag;
	
	/**	The lemma's word class. */
	
	private WordClass wordClass;
	
	/**	The lemma's major word class. */
	
	private String majorClass;
	
	/**	The collection frequency = the number of times the lemma occurs in the 
	 *	corpus. 
	 */
	
	private int colFreq;
	
	/**	The document frequency = the number of works in the corpus in which
	 *	the lemma occurs.
	 */
	 
	private int docFreq;
	
	/**	The lower rank of this lemma among all the lemmas with the same
	 *	major word class in the corpus. */
	
	private int rank1;
	
	/**	The upper rank of this lemma among all the lemmas with the same
	 *	major word class in the corpus. */
	
	private int rank2;
	
	/**	The number of distinct lemmas in the corpus with the same major
	 *	word class as this lemma.
	 */
	
	private int numMajorClass;
	
	/**	Creates a new lemma/corpus counts object.
	 */
	 
	public LemmaCorpusCounts () {
	}
	
	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="native"
	 */

	@Access(AccessType.FIELD)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId () {
		return id;
	}
	
	/**	Gets the corpus.
	 *
	 *	@return		The corpus.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="corpus_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "corpus", foreignKey = @ForeignKey(name = "corpus_index"))
	public Corpus getCorpus () {
		return corpus;
	}
	
	/**	Gets the lemma.
	 *
	 *	@return		The lemma.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="lemma_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "lemma", foreignKey = @ForeignKey(name = "lemma_index"))
	public Lemma getLemma () {
		return lemma;
	}
	
	/**	Gets the lemma's tag.
	 *
	 *	@return		The lemma's tag.
	 *
	 *	@hibernate.component prefix="tag_"
	 */

	@Access(AccessType.FIELD)
	@AttributeOverrides( {
		@AttributeOverride(name = "string", column = @Column(name = "tag_string")),
		@AttributeOverride(name = "charset", column = @Column(name = "tag_charset"))
	})
	public Spelling getTag () {
		return tag;
	}
	
	/**	Sets the lemma's tag.
	 *
	 *	<p>Hibernate needs this method.
	 *
	 *	@param	tag		The lemma's tag.
	 */
	 
	private void setTag (Spelling tag) {
		this.tag = tag;
	}
	
	/**	Gets the lemma's word class.
	 *
	 *	@return		The lemma's word class.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="wordClass_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "wordClass", foreignKey = @ForeignKey(name = "wordClass_index"))
	public WordClass getWordClass () {
		return wordClass;
	}
	
	/**	Gets the lemma's major word class.
	 *
	 *	@return		The lemma's major word class.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	public String getMajorClass () {
		return majorClass;
	}
	
	/**	Gets the collection frequency.
	 *
	 *	@return		The collection frequency = the number of times the lemma 
	 *				occurs in the corpus.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getColFreq () {
		return colFreq;
	}
	
	/**	Gets the document frequency.
	 *
	 *	@return		The doucment frequency = the number of works in the corpus
	 *				in which the lemma occurs.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getDocFreq () {
		return docFreq;
	}
	
	/**	Gets the lower rank.
	 *
	 *	@return		The lower rank of this lemma among all the lemmas with 
	 *				the same major word class in the corpus.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getRank1 () {
		return rank1;
	}
	
	/**	Gets the upper rank.
	 *
	 *	@return		The upper rank of this lemma among all the lemmas with 
	 *				the same major word class in the corpus.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getRank2 () {
		return rank2;
	}
	
	/**	Gets the major word class count.
	 *
	 *	@return		The number of distinct lemmas in the corpus with the same
	 *				major word class as this lemma.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getNumMajorClass () {
		return numMajorClass;
	}
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two objects are equal if their ids are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof LemmaCorpusCounts)) return false;
		LemmaCorpusCounts other = (LemmaCorpusCounts)obj;
		return id.equals(other.getId());
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return id.hashCode();
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

