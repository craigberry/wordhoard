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

/**	Lemma/Pos/Spelling counts.
 *
 *	<p>These count objects provide summary statistics for lemma, part of speech,
 *	and spelling combinations, at the corpus, work, and work part levels.
 *
 *	@hibernate.class table="lemmaposspellingcounts"
 */
 
@Entity
@Table(name="lemmaposspellingcounts")
public class LemmaPosSpellingCounts implements PersistentObject {

	/**	Corpus count kind. */
	
	public static final byte CORPUS_COUNT = 0;
	
	/**	Work count kind. */
	
	public static final byte WORK_COUNT = 1;
	
	/**	Work part count kind. */
	
	public static final byte WORK_PART_COUNT = 2;

	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	The kind of count object (corpus, work, or work part). */
	
	private byte kind;
	
	/**	The corpus. */
	
	private Corpus corpus;
	
	/**	The work, or null if corpus count. */
	
	private Work work;
	
	/**	The work part, or null if corpus count or work count. */
	
	private WorkPart workPart;
	
	/**	The lemma. */
	
	private Lemma lemma;
	
	/**	The part of speech. */
	
	private Pos pos;
	
	/**	The spelling. */
	
	private Spelling spelling;
	
	/**	The frequency. */
	
	private int freq;
	
	/**	The frequency for first word parts. */
	
	private int freqFirstWordPart;
	
	/**	Creates a new lemma/pos/spelling counts object.
	 */
	 
	public LemmaPosSpellingCounts () {
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
	
	/**	Gets the count kind.
	 *
	 *	@return		The count kind: CORPUS_COUNT, WORK_COUNT, or WORK_PART_COUNT.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public byte getKind () {
		return kind;
	}
	
	/**	Gets the corpus.
	 *
	 *	@return		The corpus.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="corpus_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "corpus", foreignKey = @ForeignKey(name = "corpus_index"))
	public Corpus getCorpus () {
		return corpus;
	}
	
	/**	Gets the work.
	 *
	 *	@return		The work, or null if corpus count.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="work_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work", foreignKey = @ForeignKey(name = "work_index"))
	public Work getWork () {
		return work;
	}
	
	/**	Gets the work part.
	 *
	 *	@return		The work part, or null if corpus count or work count.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="workPart_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "workPart", foreignKey = @ForeignKey(name = "workPart_index"))
	public WorkPart getWorkPart () {
		return workPart;
	}
	
	/**	Gets the lemma.
	 *
	 *	@return		The lemma.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="lemma_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "lemma", foreignKey = @ForeignKey(name = "lemma_index"))
	public Lemma getLemma () {
		return lemma;
	}
	
	/**	Gets the part of speech.
	 *
	 *	@return		The part of speech.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="pos_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pos", foreignKey = @ForeignKey(name = "pos_index"))
	public Pos getPos () {
		return pos;
	}
	
	/**	Gets the spelling. 
	 *
	 *	@return		The spelling.
	 *
	 *	@hibernate.component prefix="spelling_"
	 */

	@Access(AccessType.FIELD)
	@AttributeOverrides({
		@AttributeOverride(name = "string", column = @Column(name = "spelling_string")),
		@AttributeOverride(name = "charset", column = @Column(name = "spelling_charset"))
	})
	public Spelling getSpelling () {
		return spelling;
	}
	
	/**	Sets the spelling.
	 *
	 *	<p>Hibernate needs this method.
	 *
	 *	@param	spelling		The spelling.
	 */
	 
	private void setSpelling (Spelling spelling) {
		this.spelling = spelling;
	}
	
	/**	Gets the frequency.
	 *
	 *	@return		The frequency = the number of times the lemma/pos/spelliing combination
	 *				occurs in the corpus, work, or work part, that is, the number of word parts
	 *				in the container with the combination.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getFreq () {
		return freq;
	}
	
	/**	Gets the frequency for first word parts.
	 *
	 *	@return		The number if first word parts in the corpus, work, or work part
	 *				with the lemma/pos/spelling combination.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getFreqFirstWordPart () {
		return freqFirstWordPart;
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
		if (obj == null || !(obj instanceof LemmaPosSpellingCounts)) return false;
		LemmaPosSpellingCounts other = (LemmaPosSpellingCounts)obj;
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

