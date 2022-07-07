package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Fixes for the Shakespeare sonnets.
 *
 *	<p>We group the sonnets into parts containing 20 sonnets each.
 *
 *	<p>We set the full title of each sonnet to "Sonnet nnn".
 */

public class ShaSon extends Fixer {

	/**	Fixes an XML DOM tree.
	 *
	 *	@param	corpusTag	Corpus tag.
	 *
	 *	@param	workTag		Work tag.
	 *
	 *	@param	document	XML DOM tree.
	 *
	 *	@throws Exception	general error
	 */

	public void fix (String corpusTag, String workTag, Document document) 
		throws Exception
	{
		if (!corpusTag.equals("sha")) return;
		if (!workTag.equals("son")) return;
		Element bodyEl = DOMUtils.getDescendant(document,
			"TEI.2/text/body");
		ArrayList list = DOMUtils.getChildren(bodyEl, "div");
		int n = list.size();
		Element divs[] = (Element[])list.toArray(new Element[n]);
		for (int i = 0; i < n; i++) bodyEl.removeChild(divs[i]);
		for (int i = 1; i <= 141; i += 20) {
			Element newDivEl = (Element)divs[0].cloneNode(false);
			int j = Math.min(i+19, n);
			String title = i + "-" + j;
			newDivEl.setAttribute("head", title);
			newDivEl.setAttribute("fullTitle", title);
			bodyEl.appendChild(newDivEl);
			for (int k = i; k <= j; k++) {
				Element divEl = divs[k-1];
				Element headEl = DOMUtils.getChild(divEl, "head");
				int x = Integer.parseInt(DOMUtils.getText(headEl));
				if (x != k) log("ShaSon", "Error: bad head el " + k + " " + x);
				String fullTitle = "Sonnet " + k;
				divEl.setAttribute("fullTitle", fullTitle);
				newDivEl.appendChild(divEl);
			}
		}
		log("ShaSon", "Sonnet fixes applied");
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

