package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	A use first child work part title rule.
 */

public class UseFirstChildRule implements WorkPartTitleRule {

	/**	Element name. */

	private String name;

	/**	Rules. */

	private Rules rules;

	/**	String buffer for building title. */

	private StringBuffer buf;

	/**	Previous word ordinal. */

	private String prevWordOrd;

	/**	Creates a use first child rule.
	 *
	 *	@param	el		UseFirstChild element.
	 */

	public UseFirstChildRule (Element el) {
		Utils.checkAttributeNames(el, new String[] {"name"});
		Utils.checkNoChildren(el);
		name = el.getAttribute("name");
	}

	/**	Builds the work part title.
	 *
	 *	@param	el			Element.
	 *
	 *	@param	buf			String buffer to which work part title text is appended.
	 */

	private void buildWorkPartTitle (Element el) {
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String nodeName = childEl.getNodeName();
			if (nodeName.equals("w")) {
				String ord = childEl.getAttribute("ord");
				if (ord.equals(prevWordOrd)) continue;
				prevWordOrd = ord;
				String str = childEl.getAttribute("tok");
				if (str.length() == 0) {
					Node textNode = childEl.getFirstChild();
					if (textNode == null) continue;
					str = textNode.getNodeValue();
				}
				buf.append(str);
			} else if (nodeName.equals("c")) {
				Node textNode = childEl.getFirstChild();
				if (textNode == null) continue;
				buf.append(textNode.getNodeValue());
			} else {
				TextElementRule rule = rules.getTextElementRule(childEl);
				if (rule == null) continue;
				if (rule.getFootnote()) continue;
				if (rule.getIgnoreChildren()) continue;
				buildWorkPartTitle(childEl);
			}
		}
	}

	/**	Applies the rule.
	 *
	 *	@param	el			Element.
	 *
	 *	@param	rules		Rules.
	 *
	 *	@return				Work part title, or null if none could be found.
	 */

	public String applyRule (Element el, Rules rules) {
		Element child = DOMUtils.getChild(el, name);
		if (child == null) return null;
		buf = new StringBuffer();
		this.rules = rules;
		prevWordOrd = "";
		buildWorkPartTitle(child);
		String result = buf.toString().trim();
		return result.length() > 0 ? result : null;
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


