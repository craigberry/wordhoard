package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Author rules.
 */

public class AuthorRules {

	/**	True if each author attribute is in a separate XML element. */

	private boolean separateElementForEachAttribute;

	/**	XML path. */

	private String path;

	/**	Array of header rules. */

	private HeaderRule[] headerRules;

	/**	Creates a new author rules.
	 *
	 *	@param	el		AuthorRules element.
	 */

	public AuthorRules (Element el) {
		Utils.checkAttributeNames(el, new String[]{"separateElementForEachAttribute"});
		separateElementForEachAttribute =
			Utils.parseBooleanAttribute(el, "separateElementForEachAttribute", true);
		List headerRulesList = new ArrayList();
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String childName = childEl.getNodeName();
			if (childName.equals("headerRule")) {
				HeaderRule headerRule = new HeaderRule(childEl);
				headerRulesList.add(headerRule);
			} else if (childName.equals("path")) {
				processPath(childEl);
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of authorRules element");
			}
		}
		headerRules = (HeaderRule[])headerRulesList.toArray(
			new HeaderRule[headerRulesList.size()]);
	}

	/**	Processes a path element.
	 *
	 *	@param	el	Path element.
	 */

	private void processPath (Element el) {
		Utils.checkNoAttributes(el);
		Utils.checkNoChildren(el);
		path = DOMUtils.getText(el);
	}

	/**	Applies the rules.
	 *
	 *	@param	document		XML document.
	 *
	 *	@return					List of Author objects.
	 */

	public List applyRules (Document document) {
		List authors = new ArrayList();
		if (path == null) return authors;
		List rootEls = DOMUtils.getDescendants(document, path);
		if (separateElementForEachAttribute) {
			for (Iterator it = rootEls.iterator(); it.hasNext(); ) {
				Element root = (Element)it.next();
				Map headerValueMap = new HashMap();
				for (int i = 0; i < headerRules.length; i++)
					headerRules[i].applyRule(root, headerValueMap);
				Author author = new Author(headerValueMap);
				authors.add(author);
			}
		} else {
			for (Iterator it = rootEls.iterator(); it.hasNext(); ) {
				Element root = (Element)it.next();
				for (int i = 0; i < headerRules.length; i++)
					headerRules[i].applyAuthorRule(root, authors);
			}
		}
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


