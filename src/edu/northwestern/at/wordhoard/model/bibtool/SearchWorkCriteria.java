package edu.northwestern.at.wordhoard.model.bibtool;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;

/**	Search criteria.
 *
 *	<p>A search criteria object specifies the criteria for a word occurrence
 *	search. It has the following components:
 *
 *	<ul>
 *	<li>The {@link edu.northwestern.at.wordhoard.model.Corpus corpus} to
 *		search.
 *	<li>An optional {@link edu.northwestern.at.wordhoard.model.Author 
 *		author} to search for.
 *	<li>An optional title to search for.
 *	<li>A case sensitivity option for spelling searches.
 *	</ul>
 */
 
public class SearchWorkCriteria {

	/**	The corpus to be searched. */

	private Corpus corpus;
	
	/**	The author, or null. */

	private String authorname;
			
	/**	The title, or null. */
	
	private String title;

	/**	The start year, or null. */
	
	private String yearStart;

	/**	The end year, or null. */
	
	private String yearEnd;

	/**	True if case sensitive. */
	
	private boolean caseSensitive;
	
	/**	Creates a new search criteria.
	 *
	 *	@param	corpus			The corpus to be searched.
	 *
	 *	@param	authorname		The Author, or null.
	 *
	 *	@param	title			The title, or null.
	 *
	 *	@param	caseSensitive	True if case sensitive.
	 */
	
	public SearchWorkCriteria (Corpus corpus, String authorname, 
		String title, String yearStart, String yearEnd, boolean caseSensitive)
	{
		this.corpus = corpus;
		this.authorname = authorname;
		this.title = title;
		this.yearStart = yearStart;
		this.yearEnd = yearEnd;
		this.caseSensitive = caseSensitive;
	}
	
	/**	Gets the corpus.
	 *
	 *	@return		The corpus.
	 */
	 
	public Corpus getCorpus () {
		return corpus;
	}
	
	/**	Gets the author.
	 *
	 *	@return		The authorname, or null.
	 */
	 
	public String getAuthorName() {
		return authorname;
	}
	
	/**	Gets the title.
	 *
	 *	@return		The title, or null.
	 */
	 
	public String getTitle() {
		return title;
	}
	
	/**	Gets the year Start.
	 *
	 *	@return		The yearStart, or null.
	 */
	 
	public String getYearStart() {
		return yearStart;
	}

	/**	Gets the year end.
	 *
	 *	@return		The yearEnd, or null.
	 */
	 
	public String getYearEnd() {
		return yearEnd;
	}

	/**	Gets the case sensitive option.
	 *
	 *	@return		The case sensitive option.
	 */
	 
	public boolean getCaseSensitive () {
		return caseSensitive;
	}
	
	/**	Returns a full string representation of the search criteria.
	 *
	 *	@return		The string representation.
	 */
	
	public String toString () {
		StringBuffer buf = new StringBuffer();
		if(corpus!=null) buf.append("corpus = " + corpus);
		if (authorname != null) {
			if(buf.length()!=0) buf.append(", ");
			 buf.append("author = " + authorname);
		}
		if (title != null && !title.equals("")) {
			if(buf.length()!=0) buf.append(", ");
			buf.append("title = " + title);
		/*	if (caseSensitive) {
				buf.append(" (case sensitive)");
			} else {
				buf.append(" (case insensitive)");
			} */
		}

		if((yearStart!=null && !yearStart.equals(""))  && (yearEnd!=null && !yearEnd.equals(""))) {
			if(buf.length()!=0) buf.append(", ");
			buf.append("between " + yearStart + " and " + yearEnd);
		} else if((yearStart==null || yearStart.equals(""))  && (yearEnd!=null && !yearEnd.equals(""))) {
			if(buf.length()!=0) buf.append(", ");
			buf.append("before " + yearEnd);
		} else if((yearStart!=null && !yearStart.equals(""))  && (yearEnd==null || yearEnd.equals(""))) {
			if(buf.length()!=0) buf.append(", ");
			buf.append("after " + yearStart);
		}
		
		return buf.toString();
	}
	
	/**	Returns a short string representation of the search criteria.
	 *
	 *	@return		The short string representation.
	 */
	 
	public String toShortString () {
		if (authorname == null) {
			return title;
		} else if (title == null) {
			return authorname.toString();
		} else {
			return authorname + " / " + title;
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

