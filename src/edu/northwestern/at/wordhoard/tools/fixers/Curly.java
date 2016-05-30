package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Fixes curly quote bugs.
 *
 *	<ul>
 *
 *	<li>All white space text following open curly quote characters
 *		is removed.
 *
 *	<li>Open curly quote characters at the ends of lines are replaced
 *		by closing curly quote characters.
 *
 *	<li>Two adjacent open curly quote characters are replaced by
 *		a single open curly quote character.
 *
 *	<li>Two adjacent closing curly quote characters are replaced by
 *		a single closing curly quote character.
 *
 *	</ul>
 */

public class Curly extends Fixer {

	/**	Fixes an XML DOM tree.
	 *
	 *	@param	corpusTag	Corpus tag.
	 *
	 *	@param	workTag		Work tag.
	 *
	 *	@param	document	XML DOM tree.
	 *
	 *	@throws Exception
	 */

	public void fix (String corpusTag, String workTag, Document document) 
		throws Exception
	{
		Element bodyEl = DOMUtils.getDescendant(document,
			"TEI.2/text/body");
		NodeList list = bodyEl.getElementsByTagName("c");
		int n = list.getLength();
		for (int i = 0; i < n; i++) {
			Element el = (Element)list.item(i);
			String text = DOMUtils.getText(el);
			if (text.length() != 1 || text.charAt(0) != '\u201C') continue;
			Node next = el.getNextSibling();
			if (next == null) {
				DOMUtils.setText(el, "\u201D");
				log("Curly", "Open curly quote at end of line changed to " +
				"closed curly quote: " + el.getAttribute("id"));
			} else if (next.getNodeType() == Node.TEXT_NODE) {
				text = next.getNodeValue().trim();
				if (text.length() > 0) continue;
				next.setNodeValue("");
				log("Curly", "White space removed after open curly quote: " +
					el.getAttribute("id"));
			} else if (next.getNodeName().equals("c")) {
				text = DOMUtils.getText(next);
				if (text.length() != 1 || text.charAt(0) != '\u201C') continue;
				DOMUtils.setText(next, "");
				log("Curly", "Double open curly quote replaced by single " +
					"open curly quote: " + el.getAttribute("id"));
			}
		}
		for (int i = 0; i < n; i++) {
			Element el = (Element)list.item(i);
			String text = DOMUtils.getText(el);
			if (text.length() != 1 || text.charAt(0) != '\u201D') continue;
			Node next = el.getNextSibling();
			if (next != null && next.getNodeName().equals("c")) {
				text = DOMUtils.getText(next);
				if (text.length() != 1 || text.charAt(0) != '\u201D') continue;
				DOMUtils.setText(next, "");
				log("Curly", "Double closing curly quote replaced by single " +
					"closing curly quote: " + el.getAttribute("id"));
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

