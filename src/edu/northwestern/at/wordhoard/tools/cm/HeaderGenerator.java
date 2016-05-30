package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.util.regex.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	Generates the WordHoard header.
 */

public class HeaderGenerator {

	/**	Rules. */

	private Rules rules;

	/**	DOM tree for parsed XML document. */

	private Document document;

	/**	File name. */

	private String fileName;

	/**	WordHoard XML output file writer. */

	private XMLWriter out;

	/**	Authors processor. */

	private static AuthorProcessor authorProcessor;

	/**	Header value map. */

	private Map headerValueMap;

	/**	Authors. */

	private List authors;

	/**	Creates a new header generator.
	 *
	 *	@param	rules				Rules.
	 *
	 *	@param	document			DOM tree for parsed XML document.
	 *
	 *	@param	fileName			File name.
	 *
	 *	@param	authorProcessor		Authors processor.
	 */

	public HeaderGenerator (Rules rules, Document document, String fileName,
		AuthorProcessor authorProcessor)
	{
		this.rules = rules;
		this.document = document;
		this.fileName = fileName;
		this.authorProcessor = authorProcessor;
	}

	/**	Parses the headers.
	 *
	 *	@return		Work tag.
	 */

	public String parseHeaders () {
		headerValueMap = rules.getHeaderValues(document, fileName);
		authors = rules.getAuthors(document, fileName);
		return (String)headerValueMap.get("workTag");
	}

	/**	Writes the publication date.
	 */

	private void writePubDate () {
		String pubDateStart = (String)headerValueMap.get("pubDateStart");
		if (pubDateStart == null) {
			System.out.println("   Pub date: null");
			return;
		}
		String pubDateEnd = (String)headerValueMap.get("pubDateEnd");
		String pubDate = pubDateEnd == null ? pubDateStart :
			(pubDateStart + "-" + pubDateEnd);
		out.writeTextEl("pubDate", pubDate);
		System.out.println("   Pub date: " + pubDate);
	}

	/**	Writes the work title.
	 */

	private void writeTitle () {
		String title = (String)headerValueMap.get("title");
		if (title == null) {
			BuildUtils.emsg("Missing title - title set to 'Untitled'");
			title = "Untitled";
		}
		out.writeTextEl("title", title);
		System.out.println("   Title: " + StringUtils.truncate(title, 50));
	}

	/**	Writes the author(s).
	 */

	private void writeAuthors () {
		if (authors.size() == 0) BuildUtils.emsg("No authors found");
		for (Iterator it = authors.iterator(); it.hasNext(); ) {
			Author author = (Author)it.next();
			out.writeTextEl("author", author.getName());
			System.out.println("   Author: " + author.getName());
			authorProcessor.record(author);
		}
	}

	/**	Writes the responsibility statements.
	 */

	private void writeRespStmts() {
		TitlePageRules titlePageRules = rules.getTitlePageRules();
		if (titlePageRules == null) return;
		titlePageRules.writeRespStmts(out);
	}

	/**	Writes the title statement.
	 */

	private void writeTitleStmt () {
		out.startEl("titleStmt");
		writeTitle();
		writeAuthors();
		writeRespStmts();
		out.endEl("titleStmt");
	}

	/**	Writes the publication statement.
	 */

	private void writePublicationStmt() {
		TitlePageRules titlePageRules = rules.getTitlePageRules();
		if (titlePageRules == null) return;
		titlePageRules.writePublicationStmt(out, document);
	}

	/**	Writes the file description.
	 */

	private void writeFileDesc () {
		out.startEl("fileDesc");
		writeTitleStmt();
		writePublicationStmt();
		out.endEl("fileDesc");
	}

	/**	Writes the WordHoard header.
	 */

	private void writeWordHoardHeader () {
		out.startEl("wordHoardHeader", "corpus", rules.getCorpusTag(), "work",
			(String)headerValueMap.get("workTag"));
		writePubDate();
		Utils.writeTaggingData(out);
		out.endEl("wordHoardHeader");
	}

	/**	Writes the TEI header.
	 */

	private void writeTeiHeader () {
		out.startEl("teiHeader");
		writeFileDesc();
		out.endEl("teiHeader");
	}

	/**	Writes the headers.
	 *
	 *	@param	out					WordHoard XML output file writer.
	 */

	public void writeHeaders (XMLWriter out) {
		this.out = out;
		writeWordHoardHeader();
		writeTeiHeader();
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


