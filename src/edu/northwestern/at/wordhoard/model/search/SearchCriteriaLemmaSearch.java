package edu.northwestern.at.wordhoard.model.search;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.lang.reflect.*;

import org.hibernate.*;

import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.userdata.*;

/**	A set of word search criteria.
 */

public class SearchCriteriaLemmaSearch extends SearchCriteria {

	/**	What are we searching for? */

	private String searchingFor = null;

	/**	Creates a new empty set of word search criteria.
	 */

	public SearchCriteriaLemmaSearch () {
	}

	/**	Creates a new set of search criteria.
	 *
	 *
	 *	@param	searchingFor	(not implemented) - could be lemma, lempos, work, speaker,....
	 *
	 */

	public SearchCriteriaLemmaSearch (String searchingFor)
	{
		this.searchingFor=searchingFor;
	}

	/**	Creates a new set of search criteria from collection of constraints.
	 *
	 *
	 *	@param	constraints
	 *
	 */

	public SearchCriteriaLemmaSearch (Collection constraints)
	{
		for (Iterator it = constraints.iterator(); it.hasNext(); ) {
			SearchCriterion obj = (SearchCriterion)it.next();
			this.add((SearchCriterion)obj);
		}
	}

	public String getSelectStatement() {
		String stm = "select distinct lemma as lemm";
//		String stm = "select distinct word.wordParts.lemPos.lemma as lemm";
//		String stm = "select distinct word.wordParts.lemPos.lemma as lem, lemmacorpuscounts.colFreq";
		if(searchingFor!=null && searchingFor.equals("word forms")) { stm = "select distinct word.wordParts.lemPos ";}
		return stm;
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
		boolean haveDocCounts = joinClasses.contains(LemmaCorpusCounts.class);
		boolean haveWordSet = joinClasses.contains(WordSet.class);


		//	Build the Hibernate query string.

		MyStringBuffer buf = new MyStringBuffer();

		buf.append(getSelectStatement());
// 1/31		if (haveWordParts) buf.append(", wordPart.partIndex");
//		buf.append(" from Word word, Lemma lem");
		buf.append(" from Word word");
		if (haveDocCounts) buf.append(", LemmaCorpusCounts lemmacorpuscounts");
//		if (haveWordParts) buf.append(", WordPart wps"); // BP 1/31
		if ( haveWordSet ) buf.append( ", WordSet wordSet" );

		buf.append(" inner join word.wordParts as wordPart");
		buf.append(" inner join wordPart.lemPos.lemma as lemma");

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

//			buf.appendWhereClause(" lem.id=lemma ");

//			buf.append(" GROUP By lemma ");


//			buf.appendWhereClause(" exists (FROM Word w WHERE w.wordParts.lemPos.lemma = lemma AND w.work = 429)");
//			buf.appendWhereClause(" exists (FROM Word w WHERE w.wordParts.lemPos.lemma = lemma AND w.work = 242)");
//			buf.appendWhereClause(" exists (FROM Word w WHERE w.wordParts.lemPos.lemma = lemma AND w.work = 1021)");

//			System.out.println("Lemma search HQL:" + buf.toString());

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

		List qList = q.list();

		if(qList.size()==0) {return qList;}

		ArrayList  al = new ArrayList();
		for (Iterator it = qList.iterator(); it.hasNext(); ) {
				Object cols = it.next();
				Lemma lemma = null;
				int count = 1234;
				if(cols.getClass().isArray()) {
					lemma=(Lemma)Array.get(cols, 0);
				} else {
					lemma=(Lemma)cols;
				}
				al.add(lemma);
		}

		String hql = "select lcc.lemma, sum(lcc.colFreq), sum(lcc.docFreq) " +
			" from LemmaCorpusCounts lcc where " +
			" lcc.lemma in (:lemmata) group by lcc.lemma";
		Query qql = session.createQuery(hql);
		qql.setParameterList("lemmata",al);

		List queryList = qql.list();

		//	Build the search result objects. Filter the results for spelling
		//	collation strength if necessary.

		//$$$PIB$$$ Unused???
/*
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
*/
								//	Wrap each search result entry
								//	as a SearchResultLemmaSearch object.

		ArrayList resultList	= new ArrayList();
		Iterator it				= queryList.iterator();

		while ( it.hasNext() )
		{
								//	Get next query result entry.
								//	It is a three item array.

			Object[] cols	= (Object[])it.next();

								//	Lemma is first entry,
								//	collection frequency is second entry,
								//	document frequency is third entry.

			Lemma lemma	= (Lemma)cols[ 0 ];
			int colFreq	= ((Number)cols[ 1 ]).intValue();
			int docFreq	= ((Number)cols[ 2 ]).intValue();

								//	Create SearchResultLemmaSearch object
								//	to hold lemma and frequencies.

			SearchResultLemmaSearch sr	=
				new SearchResultLemmaSearch( lemma , colFreq , docFreq );

								//	Add to list of results.

			resultList.add( sr );
		}
								//	Convert result list to array.

		Object[] resultArray = (Object[])resultList.toArray(
			new Object[resultList.size()]);

/*		Arrays.sort(resultArray,
			new Comparator () {
				public int compare (Object sr1, Object sr2) {

					Object o1= ((SearchResultLemmaSearch)sr1).getLemma();
					Object o2= ((SearchResultLemmaSearch)sr2).getLemma();
					String tag1 = null;
					String tag2 = null;


					if(o1 instanceof Lemma) tag1 = ((Lemma)o1).getSpelling().getString();
					else if(o1 instanceof LemPos) tag1 = ((LemPos)o1).getStandardSpelling().getString();
					else tag1=o1.toString();

					if(o2 instanceof Lemma) tag2 = ((Lemma)o2).getSpelling().getString();
					else if(o2 instanceof LemPos) tag2 = ((LemPos)o2).getStandardSpelling().getString();
					else tag2=o2.toString();


					int k = tag1.compareTo(tag2);
					return k;
				}
			}
		);*/
		//	Return the result.

		return Arrays.asList(resultArray);
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


