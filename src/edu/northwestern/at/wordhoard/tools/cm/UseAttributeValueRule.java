package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	A use attribute value work part title rule.
 */

public class UseAttributeValueRule implements WorkPartTitleRule {

	/**	Attribute name. */

	private String name;

	/**	True to capitalize first letter. */

	private boolean capitalizeFirstLetter;

	/**	Creates a use attribute value rule.
	 *
	 *	@param	el		UseAttributeValue element.
	 */

	public UseAttributeValueRule (Element el) {
		Utils.checkAttributeNames(el, new String[] {"name", "capitalizeFirstLetter"});
		Utils.checkNoChildren(el);
		name = el.getAttribute("name");
		capitalizeFirstLetter = Utils.parseBooleanAttribute(el, "capitalizeFirstLetter", false);
	}

	/**	Applies the rule.
	 *
	 *	@param	el			Element.
	 *
	 *	@param	rules		Rules.
	 *
	 *	@return				Work part title, or null if none could be found.
	 */

	public String applyRule (Element el, Rules rules) {
		String result = el.getAttribute(name);
		if (result.length() == 0) return null;
		return capitalizeFirstLetter ? StringUtils.upperCaseFirstChar(result) : result;
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


