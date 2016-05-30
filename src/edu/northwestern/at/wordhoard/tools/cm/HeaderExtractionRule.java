package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.util.regex.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	A header extraction rule.
 */

public class HeaderExtractionRule {

	/**	Name of the WordHoard item to be extracted (workTag, title, etc.) */

	private String item;

	/**	The extraction rule string. */

	private String rule;

	/**	Creates a new header extraction rule.
	 *
	 *	@param	el		Extract element.
	 */

	public HeaderExtractionRule (Element el) {
		Utils.checkAttributeNames(el, new String[] {"item"});
		Utils.checkNoChildren(el);
		item = el.getAttribute("item");
		rule = DOMUtils.getText(el);
	}

	/**	Applies the rule.
	 *
	 *	@param	matcher				Matcher.
	 *
	 *	@param	headerValueMap		Header value map. Maps the names of WordHoard header
	 *								items to their values.
	 */

	public void applyRule (Matcher matcher, Map headerValueMap) {
		StringBuffer sb = new StringBuffer();
		matcher.appendReplacement(sb, rule);
		String value = sb.toString();
		String prevValue = (String)headerValueMap.get(item);
		if (prevValue != null) {
			BuildUtils.emsg("Multiple values found for WordHoard header item " + item);
			return;
		}
		if (value.length() == 0) return;
		headerValueMap.put(item, value);
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


