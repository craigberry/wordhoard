package edu.northwestern.at.wordhoard.model.morphology;

/*	Please see the license information at the end of this file. */

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.query.Query;

import edu.northwestern.at.utils.Compare;
import edu.northwestern.at.wordhoard.model.PersistentObject;
import edu.northwestern.at.wordhoard.model.grouping.GroupingObject;
import edu.northwestern.at.wordhoard.model.search.SearchCriterion;
import edu.northwestern.at.wordhoard.model.search.SearchDefaults;
import edu.northwestern.at.wordhoard.model.text.FontInfo;
import edu.northwestern.at.wordhoard.model.text.TextLine;
import edu.northwestern.at.wordhoard.model.text.TextParams;
import edu.northwestern.at.wordhoard.model.wrappers.Spelling;

/**	A part of speech.
 *
 *	<p>Parts of speech have the following attributes:
 *
 *	<ul>
 *	<li>A tag.
 *	<li>An optional description of the part of speech.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.morphology.WordClass
 *		word class} of the part of speech. This attribute is optional - it may
 *		be null.
 *	<li>Syntax: null or the syntax ("used as" attribute) of the part of speech.
 *	<li>Tense: null, pres, imperf, fut, aor, perf, plup, futperf, or past.
 *	<li>Mood: null, ind, subj, opt, impt, inf, or ppl.
 *	<li>Voice: null, act, mp, mid, or pass.
 *	<li>Case: null, nom, gen, dat, acc, voc, adv, loc, ge, subj, or obj.
 *	<li>Gender: null, masc, fem, neut, or mf.
 *	<li>Person: null, 1st, 2nd, or 3rd.
 *	<li>Number: null, sg, dual, or pl.
 *	<li>Degree: null, comp, or sup.
 *	<li>Negative: null, no, or not.
 *	<li>Language: english or greek.
 *	</ul>
 *
 *	@hibernate.class table="pos"
 */

public class Pos implements GroupingObject, PersistentObject,
	SearchDefaults, SearchCriterion, Serializable
{

	/**	English part of speech taxonomy. */

	public static final byte ENGLISH = 0;

	/**	Greek part of speech taxonomy. */

	public static final byte GREEK = 1;

	/**	Unique persistence id (primary key). */

	private Long id;

	/**	Tag. */

	private String tag;

	/**	Description. */

	private String description;

	/**	Word class. */

	private WordClass wordClass;
	
	/**	Syntax. */
	
	private String syntax;

	/**	Tense. */
	
	private String tense;
	
	/**	Mood. */
	
	private String mood;
	
	/**	Voice. */
	
	private String voice;
	
	/**	Case. */
	
	private String xcase;
	
	/**	Gender. */
	
	private String gender;
	
	/**	Person. */
	
	private String person;
	
	/**	Number. */
	
	private String number;
	
	/**	Degree. */
	
	private String degree;
	
	/**	Negative. */
	
	private String negative;
	
	/**	Language. */
	
	private String language;

	/**	Creates a new part of speech.
	 */

	public Pos () {
	}

	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="native"
	 */

	public Long getId () {
		return id;
	}

	/**	Gets the tag.
	 *
	 *	@return		The tag.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getTag () {
		return tag;
	}

	/**	Sets the tag.
	 *
	 *	@param	tag		The tag.
	 */

	public void setTag (String tag) {
		this.tag = tag;
	}

	/**	Gets the description.
	 *
	 *	@return		The description, or null if none.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getDescription () {
		return description;
	}

	/**	Sets the description.
	 *
	 *	@param	description 	Description.
	 */

	public void setDescription (String description) {
		this.description = description;
	}

	/**	Gets the word class.
	 *
	 *	@return		The word class. Null for Greek parts of speech.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="wordClass_index"
	 */

	public WordClass getWordClass () {
		return wordClass;
	}

	/**	Sets the word class.
	 *
	 *	@param	wordClass	Word class.
	 */

	public void setWordClass (WordClass wordClass) {
		this.wordClass = wordClass;
	}
	
	/**	Gets the syntax.
	 *
	 *	@return		The syntax.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getSyntax () {
		return syntax;
	}
	
	/**	Sets the syntax.
	 *
	 *	@param	syntax		Syntax.
	 */
	 
	public void setSyntax (String syntax) {
		this.syntax = syntax;
	}
	
	/**	Gets the tense.
	 *
	 *	@return		The tense.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getTense () {
		return tense;
	}
	
	/**	Sets the tense.
	 *
	 *	@param	tense	The tense.
	 */
	 
	public void setTense (String tense) {
		this.tense = tense;
	}
	
	/**	Gets the mood.
	 *
	 *	@return		The mood.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getMood () {
		return mood;
	}
	
	/**	Sets the mood.
	 *
	 *	@param	mood	The mood.
	 */
	 
	public void setMood (String mood) {
		this.mood = mood;
	}
	
	/**	Gets the voice.
	 *
	 *	@return		The voice.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getVoice () {
		return voice;
	}
	
	/**	Sets the voice.
	 *
	 *	@param	voice	The voice.
	 */
	 
	public void setVoice (String voice) {
		this.voice = voice;
	}
	
	/**	Gets the case.
	 *
	 *	@return		The case.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getXcase () {
		return xcase;
	}
	
	/**	Sets the case.
	 *
	 *	@param	xcase	The case.
	 */
	 
	public void setCase (String xcase) {
		this.xcase = xcase;
	}
	
	/**	Gets the gender.
	 *
	 *	@return		The gender.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getGender () {
		return gender;
	}
	
	/**	Sets the gender.
	 *
	 *	@param	gender	The gender.
	 */
	 
	public void setGender (String gender) {
		this.gender = gender;
	}
	
	/**	Gets the person.
	 *
	 *	@return		The person.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getPerson () {
		return person;
	}
	
	/**	Sets the person.
	 *
	 *	@param	person	The person.
	 */
	 
	public void setPerson (String person) {
		this.person = person;
	}
	
	/**	Gets the number.
	 *
	 *	@return		The number.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getNumber () {
		return number;
	}
	
	/**	Sets the number.
	 *
	 *	@param	number	The number.
	 */
	 
	public void setNumber (String number) {
		this.number = number;
	}
	
	/**	Gets the degree.
	 *
	 *	@return		The degree.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getDegree () {
		return degree;
	}
	
	/**	Sets the degree.
	 *
	 *	@param	degree	The degree.
	 */
	 
	public void setDegree (String degree) {
		this.degree = degree;
	}
	
	/**	Gets the negative attribute.
	 *
	 *	@return		The negative attribute.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getNegative () {
		return negative;
	}
	
	/**	Sets the negative attribute.
	 *
	 *	@param	negative	The negative attribute.
	 */
	 
	public void setNegative (String negative) {
		this.negative = negative;
	}
	
	/**	Gets the language.
	 *
	 *	@return		The language.
	 *
	 *	@hibernate.property access="field"
	 */
	
	public String getLanguage () {
		return language;
	}
	
	/**	Sets the language.
	 *
	 *	@param	language	The language.
	 */
	 
	public void setLanguage (String language) {
		this.language = language;
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	public SearchCriterion getSearchDefault (Class cls) {
		if (cls.equals(Pos.class)) {
			return this;
		} else if (cls.equals(WordClass.class)) {
			return wordClass;
		} else {
			return null;
		}
	}

	/**	Gets the tag with the description (if available).
	 *
	 *	@return		Tag (description).
	 */

	public String getTagWithDescription () {
		return (description == null || description.length() == 0) ? tag :
			(tag + " (" + description + ")");
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "with part of speech".
	 */

	public String getReportPhrase () {
		return "with part of speech";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling (int numHits) {
		return new Spelling(tag, TextParams.ROMAN);
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass () {
		return WordPart.class;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause () {
		return "wordPart.lemPos.pos = :pos";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session) {
		q.setParameter("pos", this);
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
		line.appendRun("pos = " + tag, romanFontInfo);
	}

	/**	Gets a string representation of the part of speech.
	 *
	 *	@return		The tag.
	 */

	public String toString () {
		return tag;
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two parts of speech are equal if their classes and
	 *	tags are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Pos)) return false;
		Pos other = (Pos)obj;
		return Compare.equals(getClass(), other.getClass()) &&
			Compare.equals(tag, other.getTag());
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode () {
		return tag.hashCode();
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

