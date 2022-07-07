package edu.northwestern.at.wordhoard.model.search;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.text.*;

import org.hibernate.*;

import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	A set of word search criteria.
 */

public class SearchCriteria {

	/**	List of search criterion. */

	protected ArrayList criteriaList = new ArrayList();

	/**	Creates a new empty set of word search criteria.
	 */

	public SearchCriteria () {
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

	public SearchCriteria (Corpus corpus, Lemma lemma,
		Pos pos, Spelling spelling, int strength)
	{
		if (corpus != null) add(corpus);
		if (lemma != null) add(lemma);
		if (pos != null) add(pos);
		if (spelling != null)
			add(new SpellingWithCollationStrength(spelling, strength));
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

	protected static class MyStringBuffer {
		protected StringBuffer buf = new StringBuffer();
		protected boolean firstWhereClause = true;
		protected void append (String str) {
			buf.append(str);
		}
		protected void appendWhereClause (String str) {
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
	 *	@throws	PersistenceException	error in persistence layer.
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
		boolean haveWordSet = joinClasses.contains(WordSet.class);
		boolean haveSpeakers = joinClasses.contains(Speaker.class);
		boolean haveAuthors = joinClasses.contains(Author.class);
		boolean haveDocCounts = joinClasses.contains(LemmaCorpusCounts.class);

		//	Build the Hibernate query string.

		MyStringBuffer buf = new MyStringBuffer();

		buf.append("select distinct word");
		if (haveWordParts) buf.append(", wordPart.partIndex");
		buf.append(" from Word word");
		if (haveDocCounts) buf.append(", LemmaCorpusCounts lemmacorpuscounts");
		if ( haveWordSet ) buf.append( ", WordSet wordSet" );

		if (haveWordParts || haveDocCounts) {
				buf.append(" inner join word.wordParts as wordPart");
				buf.append(" inner join wordPart.lemPos.lemma as lemma");
		}
		if (haveSpeakers)
			buf.append(" inner join word.speech.speakers as speaker");
		if (haveAuthors)
			buf.append(" inner join word.work.authors as author");

		buf.append(" where ");
		if (haveDocCounts)
			buf.appendWhereClause(" lemmacorpuscounts.lemma=lemma");

		for (int i = 0; i < numCriteria; i++) {
			SearchCriterion criterion = criteria[i];
			String whereClause = criterion.getWhereClause();
			buf.appendWhereClause(whereClause);
		}

		//	Create the query and set the arguments.

		//System.out.println("SearchCriteria HQL:" + buf.toString());

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

		List queryList = q.list();

		//	Build the search result objects. Filter the results for spelling
		//	collation strength if necessary.

		String spellingString = null;
		Collator collator = null;
		for (int i = 0; i < numCriteria; i++) {
			SearchCriterion criterion = criteria[i];
			if (criterion instanceof SpellingWithCollationStrength) {
				SpellingWithCollationStrength sp =
					(SpellingWithCollationStrength)criterion;
				Spelling spelling = sp.getSpelling();
				int strength = sp.getStrength();
				spellingString = spelling.getString();
				byte charset = spelling.getCharset();
				collator = CharsetUtils.getCollator(charset, strength);
				break;
			}
		}
		ArrayList resultList = new ArrayList();
		int i = 0;
		for (Iterator it = queryList.iterator(); it.hasNext(); ) {
			Word word;
			int partIndex;
			if (haveWordParts) {
				Object[] pair = (Object[])it.next();
				word = (Word)pair[0];
				partIndex = ((Integer)pair[1]).intValue();
			} else {
				word = (Word)it.next();
				partIndex = -1;
			}
			if (collator == null ||
				match(word.getSpelling().getString(), spellingString,
					true, collator))
						resultList.add(new SearchResult(word, partIndex));
		}

		//	Sort the results. We sort ourselves because this appears to be
		//	faster than using an "order by" clause in the query.

		SearchResult[] resultArray = (SearchResult[])resultList.toArray(
			new SearchResult[resultList.size()]);
		Arrays.sort(resultArray,
			new Comparator () {
				public int compare (Object o1, Object o2) {
					SearchResult r1 = (SearchResult)o1;
					SearchResult r2 = (SearchResult)o2;
					Word w1 = r1.getWord();
					Word w2 = r2.getWord();
					String workTag1 = w1.getWorkTag();
					String workTag2 = w2.getWorkTag();
					int k = workTag1.compareTo(workTag2);
					if (k != 0) return k;
					int workOrdinal1 = w1.getWorkOrdinal();
					int workOrdinal2 = w2.getWorkOrdinal();
					return workOrdinal1 - workOrdinal2;
				}
			}
		);

		//	Return the result.

		return Arrays.asList(resultArray);

	}

	/**	Matches a string against a pattern.
	 *
	 *	@param	str			String.
	 *
	 *	@param	pat			Pattern with optional '*' match-any wild card
	 *						characters.
	 *
	 *	@param	anchored	True if the whole string must match the pattern.
	 *						False if any terminating substring of the string
	 *						may match the pattern.
	 *
	 *	@param	collator	Collator for comparing strings without wild card
	 *						characters.
	 *
	 *	@return				True if the string matches the pattern.
	 */

	private boolean match (String str, String pat, boolean anchored,
		Collator collator)
	{
		int strLen = str.length();
		int patLen = pat.length();
		if (patLen == 0) return anchored ? strLen == 0 : true;
		if (pat.charAt(0) == '*') {
			return match(str, pat.substring(1), false, collator);
		} else {
			int k = pat.indexOf('*');
			if (k < 0) k = patLen;
			String patPrefix = pat.substring(0, k);
			int j = 0;
			while (j + k <= strLen) {
				if (collator.equals(str.substring(j, j+k), patPrefix) &&
					match(str.substring(j+k), pat.substring(k),
						true, collator))
							return true;
				if (anchored) return false;
				j++;
			}
			return false;
		}
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
	 *
	 *	@return	true if the criteria are "suspicious".
	 */

	public boolean suspicious () {
		for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
			SearchCriterion criterion = (SearchCriterion)it.next();
			if (criterion instanceof Lemma) return false;
			if (criterion instanceof SpellingWithCollationStrength)
				return false;
			if ((criterion instanceof SearchCriteriaTypedSet) &&
				(((SearchCriteriaTypedSet)criterion).get(0) instanceof Lemma))
					return false;
			if ((criterion instanceof SearchCriteriaTypedSet) &&
				(((SearchCriteriaTypedSet)criterion).get(0) instanceof
				SpellingWithCollationStrength))
					return false;
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

