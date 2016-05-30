package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Fixes cast list elements.
 *
 *	<p>There are so many problems with cast lists that the only real solution
 *	is to go through all of them by hand and fix the source to generate
 *	reasonable Dramatis Personae pages.
 *
 *	<p>We do what we can here to clean up the most egregious messes.
 *
 *	<ul>
 *	<li>A number of basic typos are fixed. E.g., "Groomsetc." ->"Grooms etc."
 *	<li>If there is more than one "castList" child element of 
 *		"TEI.2/text/front/div/", we issue a warning message that only the
 *		first one is used.
 *	<li>We issue a warning for "castGroup" elements which have no "head"
 *		child element.
 *	<li>Cast group titles have their first characters mapped to upper case
 *		if necessary, and trailing periods are removed unless the last word
 *		is "etc."
 *	<li>Roles have their first characters mapped to upper case if necessary,
 *		and trailing periods are removed unless the last word is "etc."
 *	<li>Leading commas and space characters are removed from role descriptions.
 *	<li>Multiple runs of space characters are replaced by a single space
 *		character in role descriptions.
 *	<li>Multiple role descriptions are combined into a single role description.
 *	<li>Trailing periods are removed from role descriptions unless the last word
 *		is "etc."
 *	<li>If a cast item has a role description but no role, the first character
 *		of the role description is mapped to upper case if necessary.
 *	<li>We issue a warning for cast items which have neither a role nor a role
 *		description.
 *	<li>Several cast items with type "list" are hacked to produce something
 *		that is at least readable. In some cases, an existing role description
 *		is changed. In other cases, where there is no role description,
 *		a "roleDesc" attribute is added to the "castItem" element.
 *	</ul>
 */

public class CastList extends Fixer {

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
			new StringPair("^C.$", "etc."),
			new StringPair("^c.$", "etc."),
			new StringPair(" C.$", " etc."),
			new StringPair(" c.$", " etc."),
			new StringPair("Groomsetc.", "Grooms etc."),
			new StringPair("AEmilius", "Aemilius"),
			new StringPair("VIOLENTA", "Violenta"),
			new StringPair("fellowin", "fellow in"),
			new StringPair("friendto", "friend to"),
			new StringPair("VARRIUS", "Varrius"),
			new StringPair("Senators Citizens Guards Attendants", 
				"Senators, Citizens, Guards, and Attendants"),
			new StringPair("Nymphs Reapers Other", 
				"Nymphs, Reapers, and Other"),
			new StringPair("Senators Tribunes Officers Soldiers and Attendants", 
				"Senators, Tribunes, Officers, Soldiers, and Attendants"),
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
		Element frontEl = DOMUtils.getDescendant(document,
			"TEI.2/text/front");
		if (frontEl == null) return;
		Element divEl = DOMUtils.getChild(frontEl, "div");
		if (divEl == null) return;
		String typeAttr = divEl.getAttribute("type");
		if (typeAttr == null || !typeAttr.equals("castList")) return;
		
		ArrayList castListElList = DOMUtils.getChildren(divEl, "castList");
		int numCastListEl = castListElList.size();
		if (numCastListEl == 0) return;
		if (numCastListEl > 1)
			log("CastList", "More than one cast list - " +
				"only first one is used for Dramatis Personae page");
		Element castListEl = (Element)castListElList.get(0);

		NodeList list = castListEl.getElementsByTagName("castItem");
		int n = list.getLength();
		for (int i = 0; i < n; i++) {
			Element el = (Element)list.item(i);
			fixCastItem(el, workTag);
		}

		list = castListEl.getElementsByTagName("castGroup");
		n = list.getLength();
		for (int i = 0; i < n; i++) {
			Element el = (Element)list.item(i);
			fixCastGroup(el);
		}
	}
	
	/**	Fixes a cast item element.
	 *
	 *	@param	el			"castItem" element.
	 *
	 *	@param	workTag		Work tag.
	 */
	
	private void fixCastItem (Element el, String workTag) {
		String type = el.getAttribute("type");
		boolean isList = type.equals("list");
		ArrayList roleList = DOMUtils.getChildren(el, "role");
		int numRole = roleList.size();
		String role = null;
		if (!isList || numRole <= 1) {
			Element roleEl = type.equals("list") ? null :
				DOMUtils.getChild(el, "role");
			role = roleEl == null ? null : 
				DOMUtils.getText(roleEl);
			if (role != null) {
				String newRole = fixCastLine(role, true);
				if (!role.equals(newRole)) {
					DOMUtils.setText(roleEl, newRole);
					log("CastList", "Role fixed: " + role + " -> " + newRole);
				}
			}
		}
		ArrayList roleDescList = DOMUtils.getChildren(el, "roleDesc");
		String description = null;
		int numRoleDesc = 0;
		for (Iterator it = roleDescList.iterator(); it.hasNext(); ) {
			Element roleDescEl = (Element)it.next();
			String roleDesc = DOMUtils.getText(roleDescEl);
			int len = roleDesc.length();
			int i = 0;
			while (i < len) {
				char c = roleDesc.charAt(i);
				if (c != ',' && !Character.isSpaceChar(c)) break;
				i++;
			}
			roleDesc = roleDesc.substring(i);
			if (description == null) {
				description = roleDesc;
			} else {
				description = description + " " + roleDesc;
			}
			numRoleDesc++;
		}
		if (description != null)
			description = description.replaceAll("  ", " ");
		description = fixCastLine(description, role == null);
		if (isList && numRole > 1) {
			if (workTag.equals("ant") && description.equals("Officers")) {
				description = "Officers, Soldiers, Messengers, and Attendants";
			} else if (workTag.equals("juc")) {
				if (description.equals("Tribunes")) {
					description = "Flavius and Marullus, tribunes";
				} else if (description.equals("A poet")) {
					description = "Cinna and another poet";
				}
			} else if (workTag.equals("mem") && numRoleDesc == 0) {
				description = "Servant, Messenger, Lords, Officers, " +
					"Citizens, Boy, and Attendants";
			} else if (workTag.equals("mev") && numRoleDesc == 0) {
				description = "Magnificoes of Venice, Clerk, " +
					"Officers of the Court of Justice, " +
					"Gaoler, Servant to Portia, and other Attendants";
			} else if (workTag.equals("tim") && description.equals("In the mask")) {
				description = "Cupid and Amazons, in the mask";
			} else if (workTag.equals("tit") && description.equals("Romans")) {
				description = "A Captain, Tribune, Messenger, and Clown";
			}
		}
		if (numRoleDesc > 0) {
			Element firstRoleDescEl = (Element)roleDescList.get(0);
			if (numRoleDesc == 1) {
				String firstRoleDesc = DOMUtils.getText(firstRoleDescEl);
				if (!description.equals(firstRoleDesc)) {
					DOMUtils.setText(firstRoleDescEl, description);
					log("CastList", "Role description fixed: " + firstRoleDesc +
						" -> " + description);
				}
			} else {
				DOMUtils.setText(firstRoleDescEl, description);
				Iterator it = roleDescList.iterator();
				it.next();
				while (it.hasNext()) el.removeChild((Element)it.next());
				log("CastList", "Role descriptions combined: " +
					description);
			}
		} else if (description != null) {
			el.setAttribute("roleDesc", description);
			log("CastList", "roleDesc attribute added: " + description);
		}
		if (role == null && description == null) {
			log("CastList", "Empty cast item found");
		}
	}
	
	/**	Fixes a cast group element.
	 *
	 *	@param	el		"castGroup" element.
	 */
	
	private void fixCastGroup (Element el) {
		Element castGroupHeadEl = DOMUtils.getChild(el, "head");
		if (castGroupHeadEl == null) {
			log("CastList", "Cast group with no head element");
			return;
		}
		String title = DOMUtils.getText(castGroupHeadEl);
		String newTitle = fixCastLine(title, true);
		if (!title.equals(newTitle)) {
			DOMUtils.setText(castGroupHeadEl, newTitle);
			log("CastList", "Cast group title fixed: " + title + 
				" -> " + newTitle);
		}
	}
	
	/**	Fixes a cast group or item line.
	 *
	 *	@param	str		String.
	 *
	 *	@param	upcase	True to map the first character to upper case.
	 *
	 *	@return			Fixed string.
	 */
	
	private String fixCastLine (String str, boolean upcase) {
		if (str == null) return null;
		for (int i = 0; i < pairs.length; i++) {
			StringPair pair = pairs[i];
			str = str.replaceAll(pair.search, pair.replace);
		}
		int len = str.length();
		if (len == 0) return str;
		if (upcase) {
			char c = str.charAt(0);
			if (Character.isLetter(c)) {
				c = Character.toUpperCase(c);
				if (len == 1) {
					str = Character.toString(c);
				} else {
					str = c + str.substring(1);
				}
			}
		}
		char c = str.charAt(len-1);
		if (c == '.') {
			if (len < 4 || !str.substring(len-4).equalsIgnoreCase("etc."))
				str = str.substring(0, len-1);
		}
		return str;
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

