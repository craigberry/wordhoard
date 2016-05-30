package edu.northwestern.at.wordhoard.model.counts;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.wordhoard.model.*;

/**	Total counts of word forms in a work part.
 *
 *	<p>A total word form count object contains the following fields:</p>
 *
 *	<ul>
 *	<li>A unique integer id for the total word form count.</li>
 *	<li>The word form (see {@link WordForms}).</li>
 *	<li>The {@link WorkPart} to which this total pertains.</li>
 *	<li>The {@link Work} to which this work part belongs.</li>
 *	<li>The total count of the word form in this work part.</li>
 *	</ul>
 *
 *	<p>Note that for LEMMA and WORDCLASS counts, the total count is equal
 *	to the number of word parts in the work part. For the other counts,
 *	the total count is equal to the number of words in the work part.</p>
 *
 *	<p>These count objects are used by the calculator.
 *
 *	@hibernate.class table="totalwordformcount"
 */

public class TotalWordFormCount
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

	/**	The work part.
	 */

	private WorkPart workPart;

	/**	The work to which this work part belongs.
	 */

	private Work work;

	/**	The total number of word form entries in the work part.
	 */

	private int wordFormCount;

	/**	Create an empty work word form count object.
	 */

	public TotalWordFormCount()
	{
	}

	/**	Create a populated total word form count object.
	 */

	public TotalWordFormCount
	(
		int	wordForm ,
		WorkPart workPart ,
		Work work ,
		int wordFormCount
	)
	{
		this.wordForm		= wordForm;
		this.workPart		= workPart;
		this.work			= work;
		this.wordFormCount	= wordFormCount;
	}

	/**	Get the persistence id.
      *
      * @hibernate.id	generator-class="native" access="field"
      */

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

	public WorkPart getWork()
	{
		return work;
	}

	/**	Get the word form count in the work part.
	 *
	 *	@return		The word form count in the work part.
	 *
	 *	@hibernate.property access="field"
	 */

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

