package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	A file rule.
 */

public class FileRule {

	/**	File name. */

	private String name;

	/**	Header value map. Maps names of WordHoard header items to their values. */

	private Map headerValueMap;

	/**	List of authors. */

	private List authors;

	/**	Creates a new file rule.
	 *
	 *	@param	el		FileRule element.
	 */

	public FileRule (Element el) {
		Utils.checkNoAttributes(el);
		NodeList nodeList = el.getChildNodes();
		authors = new ArrayList();
		headerValueMap = new HashMap();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String childName = childEl.getNodeName();
			if (childName.equals("name")) {
				name = DOMUtils.getText(childEl);
				if (name != null && name.length() == 0) name = null;
			} else if (childName.equals("workTag") ||
				childName.equals("title") ||
				childName.equals("pubDateStart") ||
				childName.equals("pubDateEnd"))
			{
				String val = DOMUtils.getText(childEl);
				if (val != null && val.length() > 0)
					headerValueMap.put(childName, val);
			} else if (childName.equals("author")) {
				String authorName = DOMUtils.getText(childEl);
				if (authorName != null && authorName.length() > 0)
					authors.add(new Author(authorName));
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of fileRule element");
			}
		}
		if (name == null)
			Utils.paramErr("Missing required file name in fileRule element");
		if (authors.size() == 0) authors = null;
	}

	/**	Gets the file name.
	 *
	 *	@return		File name.
	 */

	public String getName () {
		return name;
	}

	/**	Applies the rule.
	 *
	 *	@return		Header value map. Maps names of WordHoard header items to
	 *				their values.
	 */

	public Map applyRule () {
		return headerValueMap;
	}

	/**	Applies the author rule.
	 *
	 *	@return		List of authors, or null if none defined.
	 */

	public List applyAuthorRule () {
		return authors;
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


