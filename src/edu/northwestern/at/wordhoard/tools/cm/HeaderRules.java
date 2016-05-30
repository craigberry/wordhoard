package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Header rules.
 */

public class HeaderRules {

	/**	Array of header rules. */

	private HeaderRule[] headerRules;

	/**	Creates a new header rules.
	 *
	 *	@param	el		HeaderRules element.
	 */

	public HeaderRules (Element el) {
		Utils.checkNoAttributes(el);
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
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of headerRules element");
			}
		}
		headerRules = (HeaderRule[])headerRulesList.toArray(
			new HeaderRule[headerRulesList.size()]);
	}

	/**	Applies the rules.
	 *
	 *	@param	document		XML document.
	 *
	 *	@return					Header value map. Maps the names of WordHoard header
	 *							items to their values.
	 */

	public Map applyRules (Document document) {
		HashMap headerValueMap = new HashMap();
		for (int i = 0; i < headerRules.length; i++)
			headerRules[i].applyRule(document, headerValueMap);
		return headerValueMap;
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


