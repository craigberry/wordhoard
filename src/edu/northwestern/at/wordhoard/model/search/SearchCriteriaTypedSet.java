package edu.northwestern.at.wordhoard.model.search;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.text.*;

import org.hibernate.*;

import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.PMUtils;

/**	A set of word search criteria.
 */

public class SearchCriteriaTypedSet implements SearchCriterion, Serializable {

	/**	List of search criterion. */

	private ArrayList criteriaList = new ArrayList();

	/**	The class of this set of primitives. */

	private String typeClass = null;

	/**	The boolean relation for this set */

	private String booleanRelation = "any";

	/**	Set the boolean relationship for this set.
	 *
	 *	@param	booleanRelation		The boolean relationship for this set.
	 */

	public void	setBoolRelationship (String booleanRelation) {
		if	(	booleanRelation.equals("any") ||
				booleanRelation.equals("all") ||
				booleanRelation.equals("none") ) {
			this.booleanRelation = booleanRelation;
		}
	}

	/**	Get the boolean relationship for this set.
	 *
	 *	@return	relationship		The boolean relationship for this set.
	 */

	public String	getBoolRelationship () {return booleanRelation;}


	/**	Creates a new set of word search criteria of a specific type represented by the class of that type.
	 *
	 *	@param	typeClass		The class of the SearchCriterion for this set
	 */

	public SearchCriteriaTypedSet (String typeClass)
	{
		this.typeClass = typeClass;
	}

	/**	Adds a criterion.
	 *
	 *	@param	criterion		Search criterion.
	 */

	public void add (SearchCriterion criterion) throws SearchCriteriaClassMismatchException {
		if (criterion == null) return;
		if(!getCleanClassName(criterion).equals(typeClass)) {throw new SearchCriteriaClassMismatchException();}
		if(!contains(criterion)) criteriaList.add(criterion);
	}


	/**	Checks for criterion.
	 *
	 *	@param	criterion		Search criterion.
	 *	@return	true of false.
	 */

	public boolean contains (SearchCriterion criterion) { return criteriaList.contains(criterion);}

	protected String getCleanClassName(Object o) {
		String className = o.getClass().getName();
		if(className.lastIndexOf(".")!=-1) {className=className.substring(className.lastIndexOf(".")+1);}
		if(className.indexOf("$")!=-1) {className=className.substring(0,className.indexOf("$"));}
		return className;
	}

	/**	Removes a criterion.
	 *
	 *	@param	criterion		Search criterion.
	 */

	public boolean remove (SearchCriterion criterion) {
		return criteriaList.remove(criterion);
	}

	/**	Get criterion set.
	 *
	 */

	public Collection getCriteria () {
		return (Collection) criteriaList;
	}


	/**	Get item .
	 *
	 */

	public Object get(int index) { return criteriaList.get(index); }

	/**	Get SearchCriterion Class .
	 *
	 */

	public String getSearchCriterionClassname() { return typeClass; }


	/****************************/
	/*	SearchCriterion methods */
	/****************************/

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass () {
//		if(criteriaList.size() > 0) return ((SearchCriterion)criteriaList.get(0)).getJoinClass();
//		else return null;
		Class returnVal = null;
		if(criteriaList.size() > 0) {
			if((booleanRelation.equals("all") || booleanRelation.equals("none")) && ((SearchCriterion)criteriaList.get(0)).getJoinClass()==null) returnVal =  LemmaCorpusCounts.class;
			else if (((SearchCriterion)criteriaList.get(0)).getJoinClass()!=null) returnVal = ((SearchCriterion)criteriaList.get(0)).getJoinClass();
			// WE HAVE TROUBLE HERE - WE NEED TWO JOIN CLASSES!!!
		}
		return returnVal;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause () {
		String exampleWhereClause =
			((SearchCriterion)criteriaList.get(0)).getWhereClause();
		String classWhereClause="";
		String bRel = "or";
		if(booleanRelation.equals("any")) {bRel="or";}
		else if(booleanRelation.equals("all")) {bRel="and";}
		else if(booleanRelation.equals("none")) {bRel="not";}

		if(criteriaList.size() != 0) {
			if(typeClass.equals("PubYearRange")) {
				//:startYear <= word.work.pubDate.startYear and word.work.pubDate.endYear <= :endYear
				for (int i=1;i<= criteriaList.size();i++ ) {
					if(i==1) {classWhereClause="(";} else {classWhereClause += " " + bRel + " ";}
					String dateClause = ((SearchCriterion)criteriaList.get(i-1)).getWhereClause();
					String start = ":startYear" + i;
					String end = ":endYear" + i;
					dateClause=dateClause.replaceAll(":startYear",start);
					dateClause=dateClause.replaceAll(":endYear",end);
					classWhereClause += " (" + dateClause + ") ";
				}
				classWhereClause+=")";
			} else if(typeClass.equals("DocFrequency")) {
				for (int i=1;i<= criteriaList.size();i++ ) {
					if(i==1) {classWhereClause="(";} else {classWhereClause += " " + bRel + " ";}
					String dfClause = ((SearchCriterion)criteriaList.get(i-1)).getWhereClause();
					if(!(((DocFrequency)criteriaList.get(i-1)).getCompare().equals("EQ") && ((DocFrequency)criteriaList.get(i-1)).getFreq().intValue()==0)) {
						String docfreq = ":docfreq" + i;
						dfClause=dfClause.replaceAll(":docfreq",docfreq);
					}
					String corpus = ":doccorpus" + i;
					dfClause=dfClause.replaceAll(":doccorpus",corpus);
					classWhereClause += " (" + dfClause + ") ";
				}
				classWhereClause+=")";
			} else if(typeClass.equals("CollectionFrequency")) {
				for (int i=1;i<= criteriaList.size();i++ ) {
					if(i==1) {classWhereClause="(";} else {classWhereClause += " " + bRel + " ";}
					String dfClause = ((SearchCriterion)criteriaList.get(i-1)).getWhereClause();
					if(!(((CollectionFrequency)criteriaList.get(i-1)).getCompare().equals("EQ") && ((CollectionFrequency)criteriaList.get(i-1)).getFreq().intValue()==0)) {
						String docfreq = ":colfreq" + i;
						dfClause=dfClause.replaceAll(":colfreq",docfreq);
					}
					String corpus = ":colcorpus" + i;
					dfClause=dfClause.replaceAll(":colcorpus",corpus);
					classWhereClause += " (" + dfClause + ") ";
				}
				classWhereClause+=")";
			} else if(typeClass.equals("PubDecade")) {
			//edu.northwestern.at.wordhoard.model.grouping.PubDecade
		//	} else if(typeClass.equals("Spelling")) {
			//edu.northwestern.at.wordhoard.model.wrappers.Spelling
			} else if(typeClass.equals("Gender")) {
			//speaker.gender.gender = :gender
				for (int i=1;i<= criteriaList.size();i++ ) {
					if(i==1) {classWhereClause="(";} else {classWhereClause += " " + bRel + " ";}
					String clause = ((SearchCriterion)criteriaList.get(i-1)).getWhereClause();
					String item = ":gender" + i;
					clause=clause.replaceAll(":gender",item);
					classWhereClause += " (" + clause + ") ";
				}
				classWhereClause+=")";
			} else if(typeClass.equals("Mortality")) {
			// speaker.mortality.mortality = :mortality
				for (int i=1;i<= criteriaList.size();i++ ) {
					if(i==1) {classWhereClause="(";} else {classWhereClause += " " + bRel + " ";}
					String clause = exampleWhereClause;
					String item = ":mortality" + i;
					clause=clause.replaceAll(":mortality",item);
					classWhereClause += " (" + clause + ") ";
				}
				classWhereClause+=")";
			} else if(typeClass.equals("Prosodic")) {
			// word.prosodic.prosodic = :prosodic
				for (int i=1;i<= criteriaList.size();i++ ) {
					if(i==1) {classWhereClause="(";} else {classWhereClause += " " + bRel + " ";}
					String clause = exampleWhereClause;
					String item = ":prosodic" + i;
					clause=clause.replaceAll(":prosodic",item);
					classWhereClause += " (" + clause + ") ";
				}
				classWhereClause+=")";
			} else if(typeClass.equals("WorkSet")) {
				for (int i=1;i<= criteriaList.size();i++ ) {
					if(i==1) {classWhereClause="(";} else {classWhereClause += " " + bRel + " ";}
					String clause = exampleWhereClause;
					String item = ":workSetWorkParts" + i;
					clause=clause.replaceAll(":workSetWorkParts",item);
					classWhereClause += " (" + clause + ") ";
				}
				classWhereClause+=")";
			}
								//	We have one or more WordSets.

			else if( typeClass.equals( "WordSet" ) )
			{
								//	List of word set subclauses is
								//	enclosed in parentheses.

				classWhereClause	= "(";

								//	For each word set ...

				for ( int i = 1 ; i<= criteriaList.size() ; i++ )
				{
								//	Add booleab relation between
								//	word set subclauses.

					if ( i > 1 )
					{
						classWhereClause += " " + bRel + " ";
                    }
								//	Get standard where clause pattern
								//	for a single word set.

					String clause	= exampleWhereClause;

								//	Get query parameter name for this
								//	word set.

					String qpName	= ":wordSet" + i;

								//	Substitute query parameter name
								//	for standard parameter name in
								//	where clause pattern for word set.

					clause			=
						clause.replaceAll( ":wordSet" , qpName );

								//	Enclose this word set subclause
								//	in parentheses.

					classWhereClause += " (" + clause + ") ";
				}
								//	Right paren closes list of
								//	word set subclauses.

				classWhereClause += ")";
			}
			else if(typeClass.equals("PhraseSet")) {
				for (int i=1;i<= criteriaList.size();i++ ) {
					if(i==1) {classWhereClause="(";} else {classWhereClause += " " + bRel + " ";}
					String clause = exampleWhereClause;
					String item = ":phraseSetWordTags" + i;
					clause=clause.replaceAll(":phraseSetWordTags",item);
					classWhereClause += " (" + clause + ") ";
				}
				classWhereClause+=")";
			} else {
				classWhereClause = exampleWhereClause;
				if(classWhereClause.indexOf(":")!=-1) {
					String qual = " in ";

			//		System.out.println(getClass().getName() + " - classWhereClause:" + classWhereClause);

					if(booleanRelation.equals("any")) {
						String wherClauseSubstVar = classWhereClause.substring(classWhereClause.indexOf(":")+1);
						if(wherClauseSubstVar.indexOf(" ")!=-1) wherClauseSubstVar=wherClauseSubstVar.substring(0, wherClauseSubstVar.indexOf(" "));
						String from = " = :" + wherClauseSubstVar;
						String to = " " + qual + " \\(:" + wherClauseSubstVar + "\\)";
						classWhereClause = classWhereClause.replaceAll(from,to);
					} else if(booleanRelation.equals("all") || booleanRelation.equals("none")) {
						String not = booleanRelation.equals("none") ? " NOT " : "";
						String whereClauseSubst = classWhereClause;
						String wherClauseSubstVar = classWhereClause.substring(classWhereClause.indexOf(":")); // get variable name
						if(wherClauseSubstVar.indexOf(" ")!=-1) wherClauseSubstVar=wherClauseSubstVar.substring(0, wherClauseSubstVar.indexOf(" "));
						classWhereClause="";
						if(whereClauseSubst.indexOf("word.")!=-1) {
					//		whereClauseSubst=whereClauseSubst.replaceAll("word.","w.");
							whereClauseSubst=whereClauseSubst.replaceAll("word.","lwc.");
						}

						for (int i=1;i<= criteriaList.size();i++ ) {
							String newVar = ":var" + i;
							String thisClause = whereClauseSubst.replaceAll(wherClauseSubstVar,newVar);
							if(i!=1) classWhereClause += " AND ";
	//						classWhereClause += not + " exists (FROM Word w WHERE w.wordParts.lemPos.lemma = lemma AND " + thisClause + ") ";
							classWhereClause += not + " exists (FROM LemmaWorkCounts lwc WHERE lwc.lemma = lemma AND " + thisClause + ") ";
						}
					} else {
						System.out.println(getClass().getName() + "booleanRelation:" + booleanRelation);
					}

			//		classWhereClause += " AND " + exampleWhereClause;
				}
			}
		}

//		System.out.println(getClass().getName() + "classWhereClause:" + classWhereClause);
		return classWhereClause;
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		if(criteriaList.size() != 0) {

			if(typeClass.equals("Work") && (booleanRelation.equals("all") || booleanRelation.equals("none"))) {
				int i = 1;
				for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
					Object o = it.next();
					String valname = "var" + i++;
					q.setEntity(valname,o);
				}
//			} else if(booleanRelation.equals("any")) {
			} else {
				String classWhereClause = ((SearchCriterion)criteriaList.get(0)).getWhereClause();
				if(typeClass.equals("PubYearRange")) {
					//:startYear <= word.work.pubDate.startYear and word.work.pubDate.endYear <= :endYear
					int i = 1;
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						String start = "startYear" + i;
						String end = "endYear" + i;
						PubYearRange pyr = (PubYearRange)it.next();
						q.setInteger(start,pyr.getStartYear().intValue());
						q.setInteger(end,pyr.getEndYear().intValue());
						i++;
					}
				} else if(typeClass.equals("DocFrequency")) {
					int i = 1;
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						String docfreq = "docfreq" + i;
						String corpus = "doccorpus" + i;
						DocFrequency df = (DocFrequency)it.next();
						if(!(((DocFrequency)criteriaList.get(i-1)).getCompare().equals("EQ") && ((DocFrequency)criteriaList.get(i-1)).getFreq().intValue()==0)) {
							q.setInteger(docfreq,df.getFreq().intValue());
						}
						q.setEntity(corpus,df.getCorpus());
						i++;
					}
				} else if(typeClass.equals("CollectionFrequency")) {
					int i = 1;
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						String colfreq = "colfreq" + i;
						String corpus = "colcorpus" + i;
						CollectionFrequency cf = (CollectionFrequency)it.next();

						if(!(((CollectionFrequency)criteriaList.get(i-1)).getCompare().equals("EQ") && ((CollectionFrequency)criteriaList.get(i-1)).getFreq().intValue()==0)) {
							q.setInteger(colfreq,cf.getFreq().intValue());
						}

						q.setEntity(corpus,cf.getCorpus());
						i++;
					}
				} else if(typeClass.equals("PubDecade")) {
				//edu.northwestern.at.wordhoard.model.grouping.PubDecade
				} else if(typeClass.equals("Spelling")) {
				//edu.northwestern.at.wordhoard.model.wrappers.Spelling
				} else if(typeClass.equals("SpellingWithCollationStrength")) {
					ArrayList cs = new ArrayList();
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						Spelling sp = ((SpellingWithCollationStrength)it.next()).getSpelling();
						String str = sp.getString();
						str = CharsetUtils.translateToInsensitive(str);
						cs.add(str);
					}
					q.setParameterList("spellingInsensitive", cs, Hibernate.STRING);
				} else if(typeClass.equals("MajorWordClass")) {
				// wordPart.lemPos.lemma.wordClass.majorWordClass.majorWordClass = :majorWordClass
					ArrayList cs = new ArrayList();
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						MajorWordClass mwc = (MajorWordClass)it.next();
						cs.add(mwc.getMajorWordClass());
					}
					q.setParameterList("majorWordClass", cs, Hibernate.STRING);
			//		q.setString("majorWordClass", getMajorWordClass);
				} else if(typeClass.equals("SpeakerName")) {
					ArrayList cs = new ArrayList();
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						SpeakerName sp = (SpeakerName)it.next();
						cs.add(sp.getName());
					}
					q.setParameterList("speakerName", cs, Hibernate.STRING);
				} else if(typeClass.equals("Gender")) {
				//speaker.gender.gender = :gender
					int i = 1;
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						String item = "gender" + i;
						Gender g = (Gender)it.next();
						q.setByte(item, g.getGender());
						i++;
					}
				} else if(typeClass.equals("Mortality")) {
				// speaker.mortality.mortality = :mortality
					int i = 1;
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						String item = "mortality" + i;
						Mortality g = (Mortality)it.next();
						q.setByte(item, g.getMortality());
						i++;
					}
				} else if(typeClass.equals("Prosodic")) {
				// word.prosodic.prosodic = :prosodic
					int i = 1;
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						String item = "prosodic" + i;
						Prosodic p = (Prosodic)it.next();
						q.setByte(item, p.getProsodic());
						i++;
					}
				} else if(typeClass.equals("WorkSet")) {
					int i = 1;
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						String item = "workSetWorkParts" + i;
						WorkSet ws = (WorkSet)it.next();

								//	Get work parts corresponding to
								//	work part tags in work set.

						WorkPart[] workParts			=
							WorkUtils.getWorkPartsByTag
							(
								ws.getWorkPartTags() ,
								session
							);

                                //	Expand work parts to include
                                //	all descendents with text.

						q.setParameterList
						(
							item,
							WorkUtils.expandWorkParts( workParts )
						);
						i++;
					}
				} else if(typeClass.equals("WordSet")) {
					int i = 1;
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						String item = "wordSet" + i++;
						WordSet ws = (WordSet)it.next();
						q.setEntity( item , ws );
					}
				} else if(typeClass.equals("PhraseSet")) {
					int i = 1;
					for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
						String item = "phraseSetWordTags" + i;
						PhraseSet ps = (PhraseSet)it.next();
						q.setParameterList( item, ps.getWordTags() );
					}
				} else {
					if(classWhereClause.indexOf(":")!=-1) {
						String wherClauseSubstVar = classWhereClause.substring(classWhereClause.indexOf(":")+1);
						if(wherClauseSubstVar.indexOf(" ")!=-1) wherClauseSubstVar=wherClauseSubstVar.substring(0, wherClauseSubstVar.indexOf(" "));
						q.setParameterList(wherClauseSubstVar,criteriaList);
					}
				}
			}
		}
	}

	/**	Appends a description to a text line.
	 *
	 *	@param	line			Text line.
	 *
	 *	@param	latinFontInfo	Latin font info.
	 *
	 *	@param	fontInfo		Array of font info indexed by character
	 *							set.
	 */

	public void appendDescription (TextLine line, FontInfo latinFontInfo,FontInfo[] fontInfo)
	{
		if(criteriaList.size() != 0) {
			int i = 0;
			line.appendRun(typeClass + "= ", latinFontInfo);
			for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
				if(i++==0) {/*line.appendRun(typeClass + "= ", latinFontInfo);*/} else {line.appendRun(", ", latinFontInfo);}
				Object o = it.next();
				line.appendRun(o.toString(), latinFontInfo);
			}
		}

/*		if(criteriaList.size() != 0) {
			line.appendRun(typeClass, latinFontInfo);
*/
/*	need "hasTag" interface
			for (Iterator it = criteriaList.iterator(); it.hasNext(); ) {
				FontInfo itemFontInfo = fontInfo[item,getTag().getCharset()];
				line.appendRun(item,getTag().getString(), itemFontInfo);
			}
			*/
	}

	/**	Refresh persistent objects in search criteria.
	 *
	 *	@param	session		The persistence manager session.
	 */

	public void refreshPersistentObjects( Session session )
	{
								//	If there are criteria entries ...

		if ( criteriaList.size() > 0 )
		{
								//	... and they are mutable
								//	persistent object ...

			if	(	typeClass.equals( "WorkSet" ) ||
				    typeClass.equals( "WordSet" ) ||
				    typeClass.equals( "PhraseSet" ) )
			{
								//	... create a new list of criteria
								//	in which each entry is a refreshed
								//	copy of the persistent object.

				ArrayList newCriteriaList	= new ArrayList();

				for	(	Iterator iterator = criteriaList.iterator();
						iterator.hasNext();
					)
				{
					PersistentObject obj	=
						(PersistentObject)iterator.next();

					obj	= PMUtils.refreshObject( session , obj );

					newCriteriaList.add( obj );
				}

								//	Set the criteria list to the
								//	new list with the refreshed objects.

				criteriaList	= newCriteriaList;
			}
		}
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


