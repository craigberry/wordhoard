package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Removes superfluous "div" elements.
 *
 *	<p>Removes all "div" elements in the body which have no "head" child
 *	or "head" attribute.
 *
 *	<p>For the Chaucer work "The Legend of Good Women", we remove the 
 *	superfluous "div" element with the "head" child
 *	"Legend of Good Women".
 *
 *	<p>For the Chaucer works "Boece" and "Treatise on the Astrolab", we add 
 *	appropriate "head" attributes to the divs with type="div2" instead of 
 *	removing them.
 *
 *	<p>For the Shakespeare work "The Phoenix and Turtle", we add the
 *	"head" attribute "The Phoenix and Turtle" to the top level div and
 *	remove the two second level divs.
 *
 *	<p>For the Shakespeare work "Venus and Adonis", we add the "head"
 *	attribute "Venus and Adonis" to the top level div.
 *
 *	<p>For the Shakespeare work "A Lover's Complaint", we add the "head"
 *	attribute "A Lover's Complaint" to the top level div.
 */

public class NukeDiv extends Fixer {

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
		int numDivsNuked = 0;
		Element bodyEl = DOMUtils.getDescendant(document,
			"TEI.2/text/body");
			
		if (corpusTag.equals("cha") && workTag.equals("boe")) {
			NodeList list = bodyEl.getElementsByTagName("div");
			for (int i = 0; i < list.getLength(); i++) {
				Element divEl = (Element)list.item(i);
				String type = divEl.getAttribute("type");
				if (type != null && type.equals("div2")) {
					String nStr = divEl.getAttribute("n");
					int n = Integer.parseInt(nStr);
					String headStr = null;
					switch (n) {
						case 1: headStr = "Book I"; break;
						case 2: headStr = "Book II"; break;
						case 3: headStr = "Book III"; break;
						case 4: headStr = "Book IV"; break;
						case 5: headStr = "Book V"; break;
					}
					divEl.setAttribute("head", headStr);
				}
			}
			log("NukeDiv", "Book head attributes added");
		}
		
		if (corpusTag.equals("cha") && workTag.equals("ast")) {
			NodeList list = bodyEl.getElementsByTagName("div");
			for (int i = 0; i < list.getLength(); i++) {
				Element divEl = (Element)list.item(i);
				String type = divEl.getAttribute("type");
				if (type != null && type.equals("div2")) {
					String nStr = divEl.getAttribute("n");
					int n = Integer.parseInt(nStr);
					String headStr = null;
					switch (n) {
						case 1: break;
						case 2: headStr = "Part I"; break;
						case 3: headStr = "Part II"; break;
						case 4: headStr = "Supplemental Propositions" +
							" (Apocrypha)"; break;
					}
					if (headStr != null) divEl.setAttribute("head", headStr);
				}
			}
			log("NukeDiv", "Book head attributes added");
		}
			
		if (corpusTag.equals("sha") && workTag.equals("pht")) {
			Element topDivEl = DOMUtils.getChild(bodyEl, "div");
			topDivEl.setAttribute("head", "The Phoenix and Turtle");
			log("NukeDiv", "Head attribute added");
		}
			
		if (corpusTag.equals("sha") && workTag.equals("ven")) {
			Element topDivEl = DOMUtils.getChild(bodyEl, "div");
			topDivEl.setAttribute("head", "Venus and Adonis");
			log("NukeDiv", "Head attribute added");
		}
			
		if (corpusTag.equals("sha") && workTag.equals("lov")) {
			Element topDivEl = DOMUtils.getChild(bodyEl, "div");
			topDivEl.setAttribute("head", "A Lover's Complaint");
			log("NukeDiv", "Head attribute added");
		}

		boolean finished = false;
		while (!finished) {
			NodeList list = bodyEl.getElementsByTagName("div");
			int n = list.getLength();
			finished = true;
			for (int i = 0; i < n; i++) {
				Element divEl = (Element)list.item(i);
				Element headEl = DOMUtils.getChild(divEl, "head");
				String headAttr = divEl.getAttribute("head");
				String headStr = null;
				boolean haveHead = false;
				if (headAttr != null && headAttr.length() > 0) {
					haveHead = true;
					headStr = headAttr;
				} else if (headEl != null) {
					haveHead = true;
					headStr = DOMUtils.getText(headEl);
				}
				if (haveHead) {
					if (corpusTag.equals("cha")) {
						if (workTag.equals("lgw")) {
							haveHead = !headStr.equals("Legend of Good Women");
						}
					} else if (corpusTag.equals("sha")) {
						if (workTag.equals("pht")) {
							haveHead = !headStr.equals("THRENOS");
						}
					}
				}
				if (!haveHead) {
					nukeDiv(divEl);
					numDivsNuked++;
					finished = false;
					break;
				}
			}

		}
		if (numDivsNuked > 0)
			log("NukeDiv", numDivsNuked + " superfluous div element" +
				(numDivsNuked == 1 ? "" : "s") + " removed");
	}
	
	/**	Nukes a div element.
	 *
	 *	@param	divEl		The div element.
	 */
			
	private void nukeDiv (Element divEl) {	
		Node parent = divEl.getParentNode();
		Node nextSibling = divEl.getNextSibling();
		parent.removeChild(divEl);
		while (true) {
			Node child = divEl.getFirstChild();
			if (child == null) break;
			if (nextSibling == null) {
				parent.appendChild(child);
			} else {
				parent.insertBefore(child, nextSibling);
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

