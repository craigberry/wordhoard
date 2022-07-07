package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Adds extra div's for the Canterbury Tales.
 *
 *	<p>We gather together the various parts of each tale into a group.
 *	For example, for the Miller's tale, we create a new div element that
 *	defines a part titled "The Miller" which contains "Miller's Prologue" 
 *	and "Miller's Tale".
 *
 *	<p>This fixer must be run AFTER the NukeDiv fixer.
 */

public class ChaCan extends Fixer {

	/**	Part combination class. */

	private static class Combination {
		private String groupTitle;
		private int[] parts;
		private Combination (String groupTitle, int[] parts) {
			this.groupTitle = groupTitle;
			this.parts = parts;
		}
	}
	
	/**	Array of part combinations. */
	
	private static Combination[] combos =
		new Combination[] {
			new Combination("The Knight", new int[] {1}),
			new Combination("The Miller", new int[] {2,3}),
			new Combination("The Reeve", new int[] {4,5}),
			new Combination("The Cook", new int[] {6,7}),
			new Combination("The Man of Law", new int[] {8,9,10}),
			new Combination("The Wife of Bath", new int[] {11,12}),
			new Combination("The Friar", new int[] {13,14}),
			new Combination("The Summoner", new int[] {15,16}),
			new Combination("The Clerk", new int[] {17,18}),
			new Combination("The Merchant", new int[] {19,20,21}),
			new Combination("The Squire", new int[] {22,23}),
			new Combination("The Franklin", new int[] {24,25}),
			new Combination("The Physician", new int[] {26}),
			new Combination("The Pardoner", new int[] {27,28,29}),
			new Combination("The Shipman", new int[] {30}),
			new Combination("The Prioress", new int[] {31,32}),
			new Combination("Sir Thopas", new int[] {33,34}),
			new Combination("Melibee", new int[] {35}),
			new Combination("The Monk", new int[] {36,37}),
			new Combination("The Nun's Priest", new int[] {38,39,40}),
			new Combination("The Second Nun", new int[] {41,42}),
			new Combination("The Canon's Yeoman", new int[] {43,44}),
			new Combination("The Manciple", new int[] {45,46}),
			new Combination("The Parson", new int[] {47,48}),
		};

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
		if (!workTag.equals("can")) return;
		Element bodyEl = DOMUtils.getDescendant(document,
			"TEI.2/text/body");
		ArrayList list = DOMUtils.getChildren(bodyEl, "div");
		int n = list.size();
		Element divs[] = new Element[n];
		for (int i = 0; i < n; i++) divs[i] = (Element)list.get(i);
		for (int i = 1; i < n; i++) bodyEl.removeChild(divs[i]);
		for (int i = 0; i < combos.length; i++) {
			Combination combo = combos[i];
			Element newDivEl = (Element)divs[combo.parts[0]].cloneNode(false);
			for (int j = 0; j < combo.parts.length; j++) {
				int k = combo.parts[j];
				Element divEl = divs[k];
				newDivEl.appendChild(divEl);
				Element headEl = DOMUtils.getChild(divEl, "head");
				String title = DOMUtils.getText(headEl);
				newDivEl.setAttribute("head", combo.groupTitle);
				divEl.setAttribute("fullTitle", title);
			}
			bodyEl.appendChild(newDivEl);
		}
		log("ChaCan", "Extra divs added");
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

