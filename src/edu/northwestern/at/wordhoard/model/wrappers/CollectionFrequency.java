package edu.northwestern.at.wordhoard.model.wrappers;

/*	Please see the license information at the end of this file. */

import org.hibernate.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.utils.*;

/**	A publication count range wrapper.
 */

public class CollectionFrequency implements SearchCriterion, GroupingObject {

	/**	The count, or null if none. */

	private Integer colfreq;

	/**	The comparison type. */

	private String compare;

	/**	The end count, or null if none. */

	private Integer endCount;

	/**	The corpus in which to compare. */

	private Corpus corpus;


	/**	Creates a new publication count range wrapper.
	 */

	public CollectionFrequency () {
	}

	/**	Creates a new publication count range wrapper.
	 *
	 *	@param	colfreq		The frequency.
	 *	@param	compare		The comparison type.
	 *	@param	corpus		The corpus.
	 */

	public CollectionFrequency (Integer colfreq, String compare, Corpus corpus) {
		this.colfreq = colfreq;
		this.compare = compare;
		this.corpus = corpus;
	}

	/**	Gets the colfreq.
	 *
	 *	@return		The colfreq.
	 *
	 *	@hibernate.property access="field"
	 */

	public Integer getFreq () {
		return colfreq;
	}

	/**	Gets the end count.
	 *
	 *	@return		The end count, or null if none.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getCompare() {
		return compare;
	}


	public Corpus getCorpus () {
		return corpus;
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass () {
		return LemmaCorpusCounts.class;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause () {
		if(compare.equals("LT"))
//			return " (:colfreq > lemmacorpuscounts.colFreq and lemmacorpuscounts.corpus=(:colcorpus)) ";
			return " ( exists (FROM LemmaCorpusCounts lcccf WHERE  lcccf.lemma = lemma and :colfreq > lcccf.colFreq and lcccf.corpus=(:colcorpus)) ) ";

		else if(compare.equals("LTE"))
//			return " (:colfreq >= lemmacorpuscounts.colFreq and lemmacorpuscounts.corpus=(:colcorpus)) ";
			return " ( exists (FROM LemmaCorpusCounts lcccf WHERE  lcccf.lemma = lemma and :colfreq >= lcccf.colFreq and lcccf.corpus=(:colcorpus)) ) ";
		else if(compare.equals("EQ"))
//			return " (:colfreq = lemmacorpuscounts.colFreq and lemmacorpuscounts.corpus=(:colcorpus)) ";
//			return " ( exists (FROM LemmaCorpusCounts lcccf WHERE  lcccf.lemma = lemma and :colfreq = lcccf.colFreq and lcccf.corpus=(:colcorpus)) ) ";


			if(colfreq.intValue()==0) {
				return " ( not exists (FROM LemmaCorpusCounts lcccf WHERE  lcccf.lemma = lemma and lcccf.corpus=(:colcorpus)) ) ";
			} else {
				return " ( exists (FROM LemmaCorpusCounts lcccf WHERE  lcccf.lemma = lemma and :colfreq = lcccf.colFreq and lcccf.corpus=(:colcorpus)) ) ";
			}


		else if(compare.equals("GT"))
//			return " (:colfreq < lemmacorpuscounts.colFreq and lemmacorpuscounts.corpus=(:colcorpus)) ";
			return " ( exists (FROM LemmaCorpusCounts lcccf WHERE  lcccf.lemma = lemma and :colfreq < lcccf.colFreq and lcccf.corpus=(:colcorpus)) ) ";
		else if(compare.equals("GTE"))
//			return " (:colfreq <= lemmacorpuscounts.colFreq and lemmacorpuscounts.corpus=(:colcorpus)) ";
			return " ( exists (FROM LemmaCorpusCounts lcccf WHERE  lcccf.lemma = lemma and :colfreq <= lcccf.colFreq and lcccf.corpus=(:colcorpus)) ) ";
		else return "";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		if(!(compare.equals("EQ") && colfreq.intValue()==0))
			q.setInteger("colfreq", colfreq.intValue());
		q.setEntity("colcorpus", corpus);
	}

	/**	Appends a description to a text line.
	 *
	 *	@param	line			Text line.
	 *
	 *	@param	romanFontInfo	Roman font info.
	 *
	 *	@param	fontInfo		Array of font info indexed by character
	 *							set.
	 */

	public void appendDescription (TextLine line, FontInfo romanFontInfo,
		FontInfo[] fontInfo)
	{
		line.appendRun("collection count = " + toString(), romanFontInfo);
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "in".
	 */

	public String getReportPhrase () {
		return "in";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		return new Spelling(toString(), TextParams.ROMAN);
	}

	/**	Returns a string representation of the publication count range.
	 *
	 *	@return		String representation.
	 */

	public String toString () {
		if (colfreq == null) {
			return "none";
		} else {
			return compare + " " + colfreq + " in " + corpus.toString();
		}
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof CollectionFrequency)) return false;
		CollectionFrequency other = (CollectionFrequency)obj;
		return (Compare.equals(colfreq, other.colfreq) &&
			Compare.equals(compare, other.compare) && Compare.equals(corpus, other.corpus));
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return colfreq.hashCode() + compare.hashCode() + corpus.hashCode();
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

