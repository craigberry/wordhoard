package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Fixes problems in A Treatise on the Astrolab.
 *
 *	<p>We apply the following fixes to all the "div" elements which
 *	have titles starting with "(":
 *
 *	<ul>
 *
 *	<li>We set custom full titles. E.g., "Part II (14)".
 *
 *	<li>We add the attribute rend="italic" to all lines with line number 0.
 *
 *	<li>In many of the parts, the lines with line number 0 are out of
 *		order. We fix the bad orderings in these cases.
 *
 *	</ul>
 *
 *	<p>This fixer must be run AFTER the NukeDiv and ChaTitle fixers.
 */

public class ChaAst extends Fixer {

	/**	Fixes an XML DOM tree.
	 *
	 *	@param	corpusTag	Corpus tag.
	 *
	 *	@param	workTag		Work tag.
	 *
	 *	@param	document	XML DOM tree.
	 *
	 *	@throws Exception	general error.
	 */

	public void fix (String corpusTag, String workTag, Document document) 
		throws Exception
	{
		if (!corpusTag.equals("cha")) return;
		if (!workTag.equals("ast")) return;
		Element bodyEl = DOMUtils.getDescendant(document,
			"TEI.2/text/body");
		NodeList list = bodyEl.getElementsByTagName("div");
		int n = list.getLength();
		for (int i = 0; i < n; i++) {
			Element divEl = (Element)list.item(i);
			Element headEl = DOMUtils.getChild(divEl, "head");
			if (headEl == null) continue;
			String title = DOMUtils.getText(headEl);
			if (title.charAt(0) != '(') continue;
			addFullTitle(divEl, title);
			fixLines(divEl);
		}
		log("ChaAst", "Full titles set");
		log("ChaAst", "Lines with line number 0 italicized");
	}
	
	/**	Adds a full title to a "div" element.
	 *
	 *	@param	divEl		Div element.
	 *
	 *	@param	title		Short title of element.
	 */
	
	private void addFullTitle (Element divEl, String title) {
		Element parentDivEl = (Element)divEl.getParentNode();
		String parentTitle = parentDivEl.getAttribute("head");
		divEl.setAttribute("fullTitle", parentTitle + " " + title);
	}
	
	/**	Map from div full titles to ordering arrays. */
	
	private static HashMap orderingMap = new HashMap();
	
	static {
		orderingMap.put("Part II (3)", new int[] {4,5,1,0,2,3});
		orderingMap.put("Part II (5)", new int[] {2,0,1});
		orderingMap.put("Part II (14)", new int[] {1,0});
		orderingMap.put("Part II (15)", new int[] {1,0});
		orderingMap.put("Part II (17)", new int[] {4,1,3,0,2});
		orderingMap.put("Part II (23)", new int[] {2,0,1});
		orderingMap.put("Part II (26)", new int[] {1,0});
		orderingMap.put("Part II (29)", new int[] {1,0});
		orderingMap.put("Part II (30)", new int[] {2,0,1});
		orderingMap.put("Part II (34)", new int[] {2,0,1});
		orderingMap.put("Part II (35)", new int[] {2,0,1});
		orderingMap.put("Part II (38)", new int[] {1,0});
	};
	
	/**	Fixes lines with line number 0.
	 *
	 *	@param	divEl		Div element.
	 */
	 
	private void fixLines (Element divEl) {
		ArrayList list = DOMUtils.getChildren(divEl, "l");
		int numLinesZero = 0;
		Element[] linesZero = new Element[10];
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Element lineEl = (Element)it.next();
			String lineNumStr = lineEl.getAttribute("n");
			if (!lineNumStr.equals("0")) break;
			lineEl.setAttribute("rend", "italic");
			linesZero[numLinesZero++] = lineEl;
		}
		if (numLinesZero <= 1) return;
		String fullTitle = divEl.getAttribute("fullTitle");
		int[] ordering = (int[])orderingMap.get(fullTitle);
		if (ordering == null) return;
		if (numLinesZero != ordering.length) {
			log("ChaAst", "Bad ordering length for " + fullTitle);
			return;
		}
		for (int i = 0; i < numLinesZero; i++)
			divEl.removeChild(linesZero[i]);
		Node insertAt = divEl.getFirstChild();
		for (int i = 0; i < numLinesZero; i++)
			divEl.insertBefore(linesZero[ordering[i]], insertAt);
		String str = "Lines with number 0 reordered in " +
			fullTitle + ": ";
		for (int i = 0; i < numLinesZero; i++)
			str = str + ordering[i] + ",";
		str = str.substring(0, str.length()-1);
		log("ChaAst", str);
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

