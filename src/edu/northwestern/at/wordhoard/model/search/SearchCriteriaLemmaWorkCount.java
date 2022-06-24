package edu.northwestern.at.wordhoard.model.search;

/*	Please see the license information at the end of this file. */

import java.util.*;

import org.hibernate.*;

import edu.northwestern.at.utils.db.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	A set of word search criteria.
 */

public class SearchCriteriaLemmaWorkCount extends SearchCriteria {

	/**	List of search criterion. */

	private ArrayList criteriaList = new ArrayList();

	/**	GroupBy Class Name. */

	private String groupByClassname = "Work";

	/**	Creates a new empty set of word search criteria.
	 */

	public SearchCriteriaLemmaWorkCount () {
	}

	/**	Creates a new set of word search criteria.
	 *
	 *	<p>The collation strengths for spelling searches are:
	 *
	 *	<ul>
	 *	<li>Collator.PRIMARY: Case and diacritical insensitive.
	 *	<li>Collator.SECONDARY: Case insensitive, diacritical sensitive.
	 *	<li>Collator.TERTIARY: Case and diacritical sensitive.
	 *	</ul>
	 *
	 *	@param	corpus			The corpus to be searched, or null.
	 *
	 *	@param	lemma			The lemma, or null.
	 *
	 *	@param	pos				The part of speech, or null.
	 *
	 *	@param	spelling		The spelling, or null.
	 *
	 *	@param	strength		Collation strength for spelling searches.
	 */

	public SearchCriteriaLemmaWorkCount (Corpus corpus, Lemma lemma,
		Pos pos, Spelling spelling, int strength)
	{
		if (corpus != null) add(corpus);
		if (lemma != null) add(lemma);
		if (pos != null) add(pos);
		if (spelling != null)
			add(new SpellingWithCollationStrength(spelling, strength));
	}


	/**	Creates a new set of freq search criteria.
	 *
	 *	@param	groupByClassname		The group by class.
	 */

	public SearchCriteriaLemmaWorkCount (String groupByClassname)
	{
		this.groupByClassname = groupByClassname;
	}


	/**	Adds a criterion.
	 *
	 *	@param	criterion		Search criterion.
	 */

	public void add (SearchCriterion criterion) {
		if (criterion == null) return;
		criteriaList.add(criterion);
	}

	/**	Gets the corpus.
	 *
	 *	@return		The corpus, or null if the set of criteria does not
	 *				include a corpus criterion.
	 */

	public Corpus getCorpus () {
		for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
			SearchCriterion criterion = (SearchCriterion)it.next();
			if (criterion instanceof Corpus) return (Corpus)criterion;
		}
		return null;
	}

	/**	Returns true if the critera contains a criterion class.
	 *
	 *	@param	cls		Criterion class.
	 *
	 *	@return			True if the criteria contains  a criterion of the
	 *					specified class.
	 */

	public boolean contains (Class cls) {
		for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
			SearchCriterion criterion = (SearchCriterion)it.next();
			if (cls.equals(criterion.getClass())) return true;
		}
		return false;
	}

	/**	A string buffer that knows how to append Hibernate where clauses
	 *	separated by the word "and".
	 */

	private static class MyStringBuffer {
		private StringBuffer buf = new StringBuffer();
		private boolean firstWhereClause = true;
		private void append (String str) {
			buf.append(str);
		}
		private void appendWhereClause (String str) {
			if (!firstWhereClause) append(" and ");
			firstWhereClause = false;
			buf.append(str);
		}
		public String toString () {
			return buf.toString();
		}
	}

	/**	Executes the query.
	 *
	 *	@param	session		Hibernate session.
	 *
	 *	@return		A list of all the
	 *				{@link edu.northwestern.at.wordhoard.model.search.SearchResult
	 *				search results} which match the search criteria,
	 *				ordered by location (by work tag, then by ordinal
	 *				within work).
	 *
	 *	@throws	PersistenceException
	 */

	public List search (Session session)
		throws PersistenceException
	{
		int numCriteria = criteriaList.size();
		SearchCriterion[] criteria = (SearchCriterion[])criteriaList.toArray(
			new SearchCriterion[numCriteria]);

		//	Get the set of join classes.

		HashSet joinClasses = new HashSet();
		for (int i = 0; i < numCriteria; i++) {
			SearchCriterion criterion = criteria[i];
			Class joinClass = criterion.getJoinClass();
			if (joinClass != null) joinClasses.add(joinClass);
		}
		boolean haveWordParts = joinClasses.contains(WordPart.class);
		boolean haveSpeakers = joinClasses.contains(Speaker.class);
		boolean haveAuthors = joinClasses.contains(Author.class);
		boolean haveWordSet = joinClasses.contains(WordSet.class);

		//	Build the Hibernate query string.

		MyStringBuffer buf = new MyStringBuffer();

		buf.append("SELECT new map(" + groupByClassname + " as group , sum(lemmaworkcounts.termFreq) as freq) ");
		buf.append(" from Word word, LemmaWorkCounts lemmaworkcounts, " + groupByClassname );

		if ( haveWordSet ) buf.append( ", WordSet wordSet" );

		if (haveWordParts)
			buf.append(" inner join word.wordParts as wordPart");
		if (haveSpeakers)
			buf.append(" inner join word.speech.speakers as speaker");
		if (haveAuthors)
			buf.append(" inner join word.work.authors as author");

			buf.append(" inner join word.work as work");
			buf.append(" inner join wordPart.lemPos.lemma as lemma");

		buf.append(" where ");

		for (int i = 0; i < numCriteria; i++) {
			SearchCriterion criterion = criteria[i];
			String whereClause = criterion.getWhereClause();
			buf.appendWhereClause(whereClause);
		}
			System.out.println(getClass().getName() + " HQL:" + buf.toString());

		//	Create the query and set the arguments.

		Query q = session.createQuery(buf.toString());
		for (int i = 0; i < numCriteria; i++) {
			SearchCriterion criterion = criteria[i];
			if (criterion instanceof WorkSet) {
				session.refresh((WorkSet)criterion);
			} else if (criterion instanceof WordSet) {
				session.refresh((WordSet)criterion);
			} else if (criterion instanceof PhraseSet) {
				session.refresh((PhraseSet)criterion);
			} else if (criterion instanceof SearchCriteriaTypedSet) {
				((SearchCriteriaTypedSet)criterion).refreshPersistentObjects(
					session);
			}
			criterion.setArg(q,session);
		}

		//	Execute the query.

			System.out.println(getClass().getName() + " Execute the query.");
		List queryList = q.list();
		Iterator it = queryList.iterator();
		while (it.hasNext()) {
			Map m = (Map)it.next();
			System.out.println(m.get("group") + ":" + m.get("freq"));
		}

		return queryList;

	}

	/**	Gets a description of the search criteria.
	 *
	 *	@param	fontInfo	Array of font info indexed by character set.
	 *
	 *	@return				Full description of the search criteria, as a
	 *						multlingual text line of comma-separated clauses.
	 */

	public TextLine getDescription (FontInfo[] fontInfo) {
		FontInfo romanFontInfo = fontInfo[TextParams.ROMAN];
		TextLine line = new TextLine();
		boolean firstClause = true;
		for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
			if (!firstClause) line.appendRun(", ", romanFontInfo);
			firstClause = false;
			SearchCriterion criterion = (SearchCriterion)it.next();
			criterion.appendDescription(line, romanFontInfo, fontInfo);
		}
		return line;
	}

	/**	Returns true if the criteria are "suspicious".
	 *
	 *	<p>A set of search criteria is "suspicious" if it contains neither a
	 *	lemma criterion nor a spelling criterion. Such a search is likely to
	 *	take a long time and produce a large number of results.
	 */

	public boolean suspicious () {
		for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
			SearchCriterion criterion = (SearchCriterion)it.next();
			if (criterion instanceof Lemma) return false;
			if (criterion instanceof SpellingWithCollationStrength) return false;
			if ((criterion instanceof SearchCriteriaTypedSet) && (((SearchCriteriaTypedSet)criterion).get(0) instanceof Lemma)) return false;
			if ((criterion instanceof SearchCriteriaTypedSet) && (((SearchCriteriaTypedSet)criterion).get(0) instanceof SpellingWithCollationStrength)) return false;
		}
		return true;
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

