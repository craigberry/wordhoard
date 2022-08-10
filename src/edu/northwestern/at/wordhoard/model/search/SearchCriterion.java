package edu.northwestern.at.wordhoard.model.search;

/*	Please see the license information at the end of this file. */

import org.hibernate.Session;
import org.hibernate.query.Query;

import edu.northwestern.at.wordhoard.model.text.FontInfo;
import edu.northwestern.at.wordhoard.model.text.TextLine;

/**	A word search criterion.
 */

public interface SearchCriterion {

	/**	Gets the join class.
	 *
	 *	<p>Search criteria that involve word parts, speakers, or
	 *	authors require a join in the HIbernate query. A criterion
	 *	that requires such a join must return the class of the join
	 *	(WordPart.class, Speaker.class, or Author.class).
	 *
	 *	<p>For example, the Lemma class returns WordPart.class,
	 *	the Gender class returns Speaker.class, and the Work class
	 *	returns null.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass ();

	/**	Gets the Hibernate where clause.
	 *
	 *	<p>All criteria may use the variable name "word".
	 *
	 *	<p>The argument should be specified as a colon followed by
	 *	the lower case argument name. For example, a prosodic
	 *	criterion returns "word.prosodic = :prosodic". A corpus
	 *	criterion returns "word.work.corpus = :corpus".
	 *
	 *	<p>Criteria that require a join may use the variable
	 *	names "wordPart", "speaker", and "author" in the where
	 *	clause.
	 *
	 *	<p>For example, a speaker gender criterion returns
	 *	"speaker.gender = :gender", a part of speech criterion
	 *	returns "wordPart.lemma.pos = :pos", and a work criterion
	 *	returns "word.work = :work".
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause ();

	/**	Sets the Hibernate query argument.
	 *
	 *	<p>For example, a corpus criterion calls
	 *	q.setEntity("corpus", this), a speaker name criterion calls
	 *	q.setString("name", name), and a gender criterion calls
	 *	q.setByte("gender", gender).
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg (Query q, Session session);

	/**	Appends a description to a text line.
	 *
	 *	<p>The description should be in the form "name = value". For
	 *	example, a corpus criterion appends "corpus = xxx" where "xxx"
	 *	is the corpus title.
	 *
	 *	@param	line			Text line.
	 *
	 *	@param	romanFontInfo	Roman font info.
	 *
	 *	@param	fontInfo		Array of font info indexed by character
	 *							set.
	 */

	public void appendDescription (TextLine line, FontInfo romanFontInfo,
		FontInfo[] fontInfo);

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

