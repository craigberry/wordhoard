package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	Static utilities.
 */

public class Utils {

	/**	An empty String array. */

	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**	Issues a fatal parameter file parsing error message and exits.
	 *
	 *	@param	msg		Message.
	 */

	public static void paramErr (String msg) {
		System.out.println("Fatal error in ConvertMorph parameter file");
		System.out.println(msg);
		System.exit(1);
	}

	/**	Checks attribute names for a parameter file element.
	 *
	 *	@param	el		Element.
	 *
	 *	@param	vals	Array of legal values.
	 */

	public static void checkAttributeNames (Element el, String[] vals) {
		NamedNodeMap atts = el.getAttributes();
		for (int i = 0; i < atts.getLength(); i++) {
			Node node = atts.item(i);
			String name = node.getNodeName();
			boolean ok = false;
			for (int j = 0; j < vals.length; j++) {
				if (name.equals(vals[j])) {
					ok = true;
					break;
				}
			}
			if (!ok) paramErr("Illegal attribute " + name + " in " +
				el.getNodeName() + " element");
		}
	}

	/**	Checks that a parameter file element has no attributes.
	 *
	 *	@param	el		Element
	 */

	public static void checkNoAttributes (Element el) {
		checkAttributeNames(el, EMPTY_STRING_ARRAY);
	}

	/**	Checks that a parameter file element has no children.
	 *
	 *	@param	el		Element
	 */

	public static void checkNoChildren (Element el) {
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE)
				paramErr("Illegal child element " + child.getNodeName() +
					" of " + el.getNodeName() + " element");
		}
	}

	/**	Parses a boolean attribute for a parameter file element.
	 *
	 *	@param	el			Element.
	 *
	 *	@param	name		Attribute name.
	 *
	 *	@param	def			Default value.
	 *
	 *	@return				Attribute value.
	 */

	public static boolean parseBooleanAttribute (Element el, String name, boolean def) {
		String attrVal = el.getAttribute(name);
		if (attrVal.length() == 0) return def;
		try {
			return Boolean.parseBoolean(attrVal);
		} catch (NumberFormatException e) {
			paramErr("Illegal boolean value " + attrVal + " for " + name + " attribute in " +
				el.getNodeName() + " element");
			return def;
		}
	}

	/**	Parses an integer attribute for a parameter file element.
	 *
	 *	@param	el			Element.
	 *
	 *	@param	name		Attribute name.
	 *
	 *	@param	def			Default value.
	 *
	 *	@return				Attribute value.
	 */

	public static int parseIntegerAttribute (Element el, String name, int def) {
		String attrVal = el.getAttribute(name);
		if (attrVal.length() == 0) return def;
		try {
			return Integer.parseInt(attrVal);
		} catch (NumberFormatException e) {
			paramErr("Illegal integer value " + attrVal + " for " + name + " attribute in " +
				el.getNodeName() + " element");
			return def;
		}
	}

	/**	Writes WordHoard tagging data flags.
	 *
	 *	@param	out		WordHoard XML output file writer.
	 */

	public static void writeTaggingData (XMLWriter out) {
		out.startEl("taggingData");
		out.writeEmptyEl("lemma");
		out.writeEmptyEl("pos");
		out.writeEmptyEl("wordClass");
		out.writeEmptyEl("spelling");
		out.writeEmptyEl("pubDates");
		out.writeEmptyEl("prosodic");
		out.endEl("taggingData");
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


