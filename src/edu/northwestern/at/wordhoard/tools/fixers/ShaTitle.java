package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Fixes Shakespeare titles ("head" elements).
 *
 *	<p>In the XML the title is supposed to be specified by a unique
 *	"head" child element of the "div" element for the act or scene.
 *	There are, however, sometimes bugs where the "head" child is
 *	missing or there is more than one "head" child. If the "head" 
 *	child is missing, we use the "type" and "n" attributes of the
 *	"div" element to reconstruct the title and we set it as the "head"
 *	attribute of the "div" element. If more than one "head"
 *	child is present, we delete all but the last one.
 *
 *	<p>We normalize all act and scene titles to use arabic numerals
 *	instead of roman numerals, to use mixed case instead of upper
 *	case, and to not include periods at the end of the titles.
 *
 *	<p>For the poem "The Rape of Lucrece" we change the title of the
 *	first part from "Introduction" to "Argument".
 */

public class ShaTitle extends Fixer {

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
		NodeList list = bodyEl.getElementsByTagName("div");
		int n = list.getLength();
		
		if (workTag.equals("rap")) {
			Element firstDivEl = (Element)list.item(0);
			Element headEl = (Element)DOMUtils.getChild(firstDivEl, "head");
			String title = DOMUtils.getText(headEl);
			String newTitle = "Argument";
			if (!title.equals(newTitle)) {
				DOMUtils.setText(headEl, newTitle);
				log("ShaTitle", "Title fixed: " + title + " -> " + newTitle);
			}
			return;
		}
		
		for (int i = 0; i < n; i++) {
			Element divEl = (Element)list.item(i);
			ArrayList headChildren = DOMUtils.getChildren(divEl, "head");
			int numHeadChildren = headChildren.size();
			if (numHeadChildren > 1) {
				Element headEl = 
					(Element)headChildren.remove(numHeadChildren-1);
				for (Iterator it = headChildren.iterator(); it.hasNext(); ) {
					Element badChild = (Element)it.next();
					divEl.removeChild(badChild);
				}
				log("ShaTitle", "Multiple head elements: " + 
					DOMUtils.getText(headEl));
				fixTitle(headEl);
			} else if (numHeadChildren == 1) {
				Element headEl = (Element)headChildren.get(0);
				fixTitle(headEl);
			} else {
				String type = divEl.getAttribute("type");
				String num = divEl.getAttribute("n");
				String title = null;
				if (type.equals("act")) {
					title = "Act " + num;
				} else if (type.equals("scene")) {
					title = "Scene " + num;
				} else if (type.equals("induction")) {
					title = "Induction";
				} else if (type.equals("prologue")) {
					title = "Prologue";
				} else if (type.equals("epilogue")) {
					title = "Epilogue";
				} else if (type.equals("chorus")) {
					title = "Chorus " + num;
				}
				divEl.setAttribute("head", title);
				log("ShaTitle", "Missing head element: " + title);
			}
		}
	}
	
	/**	Fixes a title.
	 *
	 *	@param	headEl		Head element.
	 */
	
	private void fixTitle (Element headEl) {
		String title = DOMUtils.getText(headEl);
		String newTitle = title.replaceAll("\\.", "");
		newTitle = newTitle.replaceAll("ACT", "Act");
		newTitle = newTitle.replaceAll("SCENE", "Scene");
		newTitle = newTitle.replaceAll("INDUCTION", "Induction");
		newTitle = newTitle.replaceAll("PROLOGUE", "Prologue");
		newTitle = newTitle.replaceAll("EPILOGUE", "Epilogue");
		newTitle = newTitle.replaceAll("CHORUS", "Chorus");
		newTitle = newTitle.replaceAll("VII$", "7");
		newTitle = newTitle.replaceAll("VI$", "6");
		newTitle = newTitle.replaceAll("IV$", "4");
		newTitle = newTitle.replaceAll("V$", "5");
		newTitle = newTitle.replaceAll("III$", "3");
		newTitle = newTitle.replaceAll("II$", "2");
		newTitle = newTitle.replaceAll("I$", "1");
		if (title.equals(newTitle)) return;
		DOMUtils.setText(headEl, newTitle);
		log("ShaTitle", "Title fixed: " + title + " -> " + newTitle);
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

