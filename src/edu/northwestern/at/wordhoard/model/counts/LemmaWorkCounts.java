package edu.northwestern.at.wordhoard.model.counts;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
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

/**	Lemma/work counts.
 *
 *	<p>These count objects provide summary statistics for lemma/work
 *	combinations. Given a lemma and a work, we can quickly provide the
 *	following information using a two-column index on work/lemma:
 *
 *	<ul>
 *	<li>The term frequency = the nmber of times the lemma occurs in the
 *		work.
 *	<li>The lemma's rank in the work among all the lemma's with the
 *		same major word class in the work. The rank is specified as a
 *		range.
 *	<li>The total number of distinct lemmas in the work with the same
 *		major word class as this lemma.
 *	</ul>
 *
 *	<p>These objects are used to quickly provide information for get info
 *	windows for words.
 *
 *	@hibernate.class table="lemmaworkcounts"
 */

@Entity
@Table(name="lemmaworkcounts")
public class LemmaWorkCounts implements PersistentObject {

	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	The work. */
	
	private Work work;
	
	/**	The lemma. */
	
	private Lemma lemma;
	
	/**	The term frequency = the number of times the lemma occurs in the 
	 *	work. 
	 */
	
	private int termFreq;
	
	/**	The lower rank of this lemma among all the lemmas with the same
	 *	major word class in the work. */
	
	private int rank1;
	
	/**	The upper rank of this lemma among all the lemmas with the same
	 *	major word class in the work. */
	
	private int rank2;
	
	/**	The number of distinct lemmas in the work with the same major
	 *	word class as this lemma.
	 */
	
	private int numMajorClass;
	
	/**	Creates a new lemma/work counts object.
	 */
	 
	public LemmaWorkCounts () {
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
	
	/**	Gets the work.
	 *
	 *	@return		The work.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="work_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work", foreignKey = @ForeignKey(name = "work_index"))
	public Work getWork () {
		return work;
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
	
	/**	Gets the term frequency.
	 *
	 *	@return		The term frequency = the number of times the lemma occurs
	 *				in the work.
	 *
	 *	@hibernate.property access="field"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public int getTermFreq () {
		return termFreq;
	}
	
	/**	Gets the lower rank.
	 *
	 *	@return		The lower rank of this lemma among all the lemmas with 
	 *				the same major word class in the work.
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
	 *				the same major word class in the work.
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
	 *	@return		The number of distinct lemmas in the work with the same
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
		if (obj == null || !(obj instanceof LemmaWorkCounts)) return false;
		LemmaWorkCounts other = (LemmaWorkCounts)obj;
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

