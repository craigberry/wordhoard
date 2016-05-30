package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	A header rule.
 */

public class HeaderRule {

	/**	Separator string for multiple value elements, or null if none. */

	private String sep;

	/**	XML path. */

	private String path;

	/**	Array of header rule patterns. */

	private HeaderRulePattern[] rulePatterns;

	/**	Creates a new header rule.
	 *
	 *	@param	el		HeaderRule element.
	 */

	public HeaderRule (Element el) {
		Utils.checkAttributeNames(el, new String[] {"sep"});
		sep = el.getAttribute("sep");
		if (sep.length() == 0) sep = null;
		List headerRulePatternsList = new ArrayList();
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String childName = childEl.getNodeName();
			if (childName.equals("path")) {
				processPath(childEl);
			} else if (childName.equals("pattern")) {
				HeaderRulePattern headerRulePattern = new HeaderRulePattern(childEl);
				headerRulePatternsList.add(headerRulePattern);
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of headerRule element");
			}
		}
		rulePatterns = (HeaderRulePattern[])headerRulePatternsList.toArray(
			new HeaderRulePattern[headerRulePatternsList.size()]);
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

	/**	Applies the rule.
	 *
	 *	@param	root				Root node.
	 *
	 *	@param	headerValueMap		Header value map. Maps the names of WordHoard header
	 *								items to their values.
	 */

	public void applyRule (Node root, Map headerValueMap) {
		Node node = path.length() == 0 ? root : DOMUtils.getDescendant(root, path);
		if (node == null) return;
		String str = DOMUtils.getText(node);
		str = str.replaceAll("[\\r\\n]+", " ");
		for (int i = 0; i < rulePatterns.length; i++)
			if (rulePatterns[i].applyRule(str, headerValueMap)) break;
	}

	/**	Applies author pattern rules to a string.
	 *
	 *	@param	str				String.
	 *
	 *	@param	authors			Author list.
	 */

	private void applyAuthorPatternRules (String str, List authors) {
		Map headerValueMap = new HashMap();
		for (int i = 0; i < rulePatterns.length; i++)
			if (rulePatterns[i].applyRule(str, headerValueMap)) break;
		String name = (String)headerValueMap.get("authorName");
		if (name == null) return;
		Author author = new Author(headerValueMap);
		authors.add(author);
	}

	/**	Applies an author rule.
	 *
	 *	@param	root				Root node.
	 *
	 *	@param	authors				Author list.
	 */

	public void applyAuthorRule (Node root, List authors) {
		Node node = path.length() == 0 ? root : DOMUtils.getDescendant(root, path);
		if (node == null) return;
		String str = DOMUtils.getText(node);
		str = str.replaceAll("[\\r\\n]+", " ");
		if (sep == null) {
			applyAuthorPatternRules(str, authors);
		} else {
			int pos = 0;
			int len = str.length();
			int sepLen = sep.length();
			while (pos < len) {
				int k = str.indexOf(sep, pos);
				if (k < 0) k = len;
				String subStr = str.substring(pos, k);
				pos = k + sepLen;
				applyAuthorPatternRules(subStr, authors);
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


