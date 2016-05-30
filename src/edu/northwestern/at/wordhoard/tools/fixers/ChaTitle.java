package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Fixes Chaucer titles ("head" elements).
 */

public class ChaTitle extends Fixer {

	/**	A pair of search/replace strings. */

	private static class StringPair {
		private String search;
		private String replace;
		private StringPair (String search, String replace) {
			this.search = search;
			this.replace = replace;
		}
	}
	
	/**	Array of search/replace string pairs. */
	
	private static StringPair pairs[] =
		new StringPair[] {
			new StringPair("Paroner", "Pardoner"),
			new StringPair("Legend of Good Women, ", ""),
			new StringPair("Treatise on the Astrolabe, Part .*\\(", "("),
			new StringPair("Treatise on the Astrolabe, ", ""),
			new StringPair("Boece, Bk .*\\(m(.*)\\)", "Metrum $1"),
			new StringPair("Boece, Bk .*\\(pr(.*)\\)", "Prosa $1"),
			new StringPair("Romaunt of the Rose, ", ""),
			new StringPair("Troilus Book", "Book"),
		};

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
		Element titleStmtEl = DOMUtils.getDescendant(document, 
			"TEI.2/teiHeader/fileDesc/titleStmt");
		Element titleEl = 
			DOMUtils.getChild(titleStmtEl, "title", "type", "subordinate");
		String title = DOMUtils.getText(titleEl);
		String newTitle = title.replaceAll("Parliment", "Parliament");
		if (!title.equals(newTitle)) {
			DOMUtils.setText(titleEl, newTitle);
			log("ChaTitle", "Work title fixed: " + title + " -> " + newTitle);
		}
	
		int astrolabNum = 41;
		Element bodyEl = DOMUtils.getDescendant(document,
			"TEI.2/text/body");
		NodeList list = bodyEl.getElementsByTagName("head");
		int n = list.getLength();
		for (int i = 0; i < n; i++) {
			Element el = (Element)list.item(i);
			String text = DOMUtils.getText(el);
			String newText = text;
			if (text.startsWith("Supplemental Propositions to the Astrolabe")) {
				newText = "(" + astrolabNum + ")";
				astrolabNum++;
			} else {
				for (int j = 0; j < pairs.length; j++) {
					StringPair pair = pairs[j];
					newText = newText.replaceAll(pair.search, pair.replace);
				}
			}
			if (!newText.equals(text)) {
				DOMUtils.setText(el, newText);
				log("ChaTitle", "Head el fixed: " + text + " -> " + newText);
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

