package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.util.regex.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	A header rule pattern.
 */

public class HeaderRulePattern {

	/**	The regular expression pattern match string. */

	private String match;

	/**	The compiled regular expression pattern. */

	private Pattern pattern;

	/**	Array of extraction rules. */

	private HeaderExtractionRule[] extractionRules;

	/**	Creates a new header rule pattern.
	 *
	 *	@param	el		HeaderRulePattern element.
	 */

	public HeaderRulePattern (Element el) {
		Utils.checkNoAttributes(el);
		List extractionRulesList = new ArrayList();
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String childName = childEl.getNodeName();
			if (childName.equals("match")) {
				processMatch(childEl);
			} else if (childName.equals("extract")) {
				HeaderExtractionRule headerExtractionRule = new HeaderExtractionRule(childEl);
				extractionRulesList.add(headerExtractionRule);
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of headerRulePattern element");
			}
		}
		extractionRules = (HeaderExtractionRule[])extractionRulesList.toArray(
			new HeaderExtractionRule[extractionRulesList.size()]);
	}

	/**	Processes a match element.
	 *
	 *	@param	el	Match element.
	 */

	private void processMatch (Element el) {
		Utils.checkNoAttributes(el);
		Utils.checkNoChildren(el);
		match = DOMUtils.getText(el);
		pattern = Pattern.compile(match);
	}

	/**	Applies the rule.
	 *
	 *	@param	str					String to be matched.
	 *
	 *	@param	headerValueMap		Header value map. Maps the names of WordHoard header
	 *								items to their values.
	 *
	 *	@return						True if the pattern matched.
	 */

	public boolean applyRule (String str, Map headerValueMap) {
		Matcher matcher = pattern.matcher(str);
		for (int i = 0; i < extractionRules.length; i++) {
			matcher.reset();
			if (!matcher.matches()) return false;
			extractionRules[i].applyRule(matcher, headerValueMap);
		}
		return true;
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


