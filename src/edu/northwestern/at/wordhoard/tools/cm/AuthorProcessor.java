package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	Processes authors.
 */

public class AuthorProcessor {

	/**	WordHoard data directory. */

	private File dataDir;

	/**	Map from author names to author objects. */

	private TreeMap authorMap = new TreeMap();

	/**	Creates a new authors processer.
	 *
	 *	@param	dataDir		WordHoard data directory.
	 */

	public AuthorProcessor (File dataDir) {
		this.dataDir = dataDir;
	}

	/**	Reads the authors.
	 *
	 *	@throws Exception
	 */

	public void read ()
		throws Exception
	{
		File file = new File(dataDir, "authors.xml");
		Document doc = DOMUtils.parse(file);
		Element el = DOMUtils.getDescendant(doc, "WordHoardAuthors");
		List list = DOMUtils.getChildren(el, "author");
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Element authorEl = (Element)it.next();
			Author author = new Author(authorEl);
			authorMap.put(author.getName(), author);
		}
	}

	/**	Records an author.
	 *
	 *	@param	author		Author.
	 */

	public void record (Author author)
	{
		String name = author.getName();
		Author oldAuthor = (Author)authorMap.get(name);
		if (oldAuthor == null) {
			authorMap.put(name, author);
		} else if (!oldAuthor.contains(author)) {
			BuildUtils.emsg("Conflicting attributes for author: " + name);
		}
	}

	/**	Writes the authors.
	 *
	 *	@throws	Exception
	 */

	public void write ()
		throws Exception
	{
		File file = new File(dataDir, "authors.xml");
		XMLWriter out = new XMLWriter(file);
		out.startEl("WordHoardAuthors");
		for (Iterator it = authorMap.values().iterator(); it.hasNext(); ) {
			Author author = (Author)it.next();
			author.write(out);
		}
		out.endEl("WordHoardAuthors");
		out.close();
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


