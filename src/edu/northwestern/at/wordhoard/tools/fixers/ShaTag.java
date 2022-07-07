package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Fixes Shakespeare tags.
 *
 *	<p>Adds "tag" and "pathTag" attributes to the "div" elements for 
 *	Shakespeare works.
 *
 *	<p>This fixer must be run AFTER the NukeDiv and ShaTitle fixers.
 */

public class ShaTag extends Fixer {

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
		Element bodyEl = DOMUtils.getDescendant(document,
			"TEI.2/text/body");
		NodeList list = bodyEl.getElementsByTagName("div");
		int n = list.getLength();
		String actTag = null;
		for (int i = 0; i < n; i++) {
			Element divEl = (Element)list.item(i);
			String title = divEl.getAttribute("head");
			if (title == null || title.length() == 0) {
				Element headEl = DOMUtils.getChild(divEl, "head");
				title = DOMUtils.getText(headEl);
			}
			String pathTag = null;
			String tag = null;
			if (workTag.equals("son")) {
				pathTag = title.indexOf('-') >= 0 ? null : title;
				tag = title;
			} else if (workTag.equals("rap")) {
				if (title.equals("Argument")) {
					pathTag = "a";
					tag = "a";
				} else {
					pathTag = null;
					tag = "body";
				}
			} else if (workTag.equals("pht")) {
				pathTag = null;
				tag = "body";
			} else if (workTag.equals("ven")) {
				pathTag = null;
				tag = "body";
			} else if (workTag.equals("lov")) {
				pathTag = null;
				tag = "body";
			} else {
				pathTag = playPathTag(title);
				if (title.startsWith("Act")) {
					tag = pathTag;
					actTag = pathTag;
				} else {
					tag = actTag + "-" + pathTag;
				}
			}
			if (pathTag != null) divEl.setAttribute("pathTag", pathTag);
			divEl.setAttribute("tag", tag);
		}
		log("ShaTag", "Tags set on div elements");
	}
	
	/**	Gets a play part path tag.
	 *
	 *	@param	title		Part title.
	 *
	 *	@return				Path tag.
	 */
	
	private String playPathTag (String title) {
		if (title.startsWith("Act") || title.startsWith("Scene")) {
			int k = title.indexOf(' ');
			return title.substring(k+1);
		} else if (title.equals("Prologue")) {
			return "p";
		} else if (title.equals("Epilogue")) {
			return "e";
		} else if (title.equals("Induction")) {
			return "i";
		} else if (title.startsWith("Chorus")) {
			int k = title.indexOf(' ');
			return "c" + title.substring(k+1);
		} else {
			log("ShaTag", "Error: unknown title: " + title);
		}
		return null;
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

