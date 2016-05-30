package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	A rend attribute rule.
 */

public class RendAttributeRule {

	/**	Attribute name. */

	private String attrName;

	/**	A map from attribute values to Style objects. */

	private Map rendAttrValueMap = new HashMap();

	/**	Creates a new rend attribute rule.
	 *
	 *	@param	el		RendAttributeRule element.
	 */

	public RendAttributeRule (Element el) {
		Utils.checkAttributeNames(el, new String[] {"attrName"});
		attrName = el.getAttribute("attrName");
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String childName = childEl.getNodeName();
			if (childName.equals("rendAttributeMapping")) {
				processRendAttributeMapping(childEl);
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of rendAttributeRule element");
			}
		}
	}

	/**	Processes a rendAttributeMapping element.
	 *
	 *	@param	el		RendAttributeMapping element.
	 */

	private void processRendAttributeMapping (Element el) {
		Utils.checkAttributeNames(el, new String[] {"value", "lineStyle",
			"indent", "wordStyles"});
		Utils.checkNoChildren(el);
		String value = el.getAttribute("value");
		if (value.length() == 0)
			Utils.paramErr("Missing required value attribute on rendAttributeMapping element");
		Style style = new Style(el);
		rendAttrValueMap.put(value, style);
	}

	/**	Applies the rule.
	 *
	 *	@param	el		Element.
	 *
	 *	@return			Style, or null if none.
	 */

	public Style applyRule (Element el) {
		String val = el.getAttribute(attrName);
		if (val.length() == 0) return null;
		Style style = (Style)rendAttrValueMap.get(val);
		if (style == null)
			BuildUtils.emsg("Unexpected rend attribute value on " + el.getNodeName() +
				" element: " + val);
		return style;
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


