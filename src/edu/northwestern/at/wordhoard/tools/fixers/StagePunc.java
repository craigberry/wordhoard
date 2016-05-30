package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Fixes stage direction punctuation bugs.
 */

public class StagePunc extends Fixer {

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
		NodeList list = bodyEl.getElementsByTagName("stage");
		int n = list.getLength();
		for (int i = 0; i < n; i++) {
			Element stageEl = (Element)list.item(i);
			Element cEl = null;
			Node next = stageEl.getNextSibling();
			while (next != null && next.getNodeType() == Node.TEXT_NODE)
				next = next.getNextSibling();
			if (next == null) {
				Node parent = stageEl.getParentNode();
				String parentTagName = parent.getNodeName();
				if (parentTagName.equals("l") ||
					parentTagName.equals("ab")) 
						next = parent.getNextSibling();
			}
			if (next == null) continue;
			String nextTagName = next.getNodeName();
			if (nextTagName.equals("c")) {
				cEl = (Element)next;
			} else if (nextTagName.equals("l") ||
				nextTagName.equals("ab"))
			{
				Node firstChild = next.getFirstChild();
				if (firstChild != null && 
					firstChild.getNodeName().equals("c"))
						cEl = (Element)firstChild;
			}
			if (cEl == null) continue;
			String text = DOMUtils.getText(cEl);
			if (text.length() != 1) continue;
			char c = text.charAt(0);
			if (c == '\u201C' || c == '\u2018' || c == '(' || c == '[') continue;
			Node parent = cEl.getParentNode();
			parent.removeChild(cEl);
			parent = stageEl.getParentNode();
			String parentTagName = parent.getNodeName();
			if (parentTagName.equals("l") || parentTagName.equals("ab")) {
				parent.insertBefore(cEl, stageEl);
			} else {
				Node prev = stageEl.getPreviousSibling();
				Node last = prev.getLastChild();
				if (last.getNodeType() == Node.TEXT_NODE) {
					String val = last.getNodeValue().trim();
					if (val.length() == 0) prev.removeChild(last);
				}
				prev.appendChild(cEl);
			}
			log("StagePunc", "Stage direction punctuation bug fixed: " +
				cEl.getAttribute("id"));
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

