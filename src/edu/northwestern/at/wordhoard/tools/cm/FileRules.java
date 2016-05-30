package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	File rules.
 */

public class FileRules {

	/**	Map from file names to file rules. */

	private Map fileRules;

	/**	Creates a new files rules.
	 *
	 *	@param	el		FileRules element.
	 */

	public FileRules (Element el) {
		Utils.checkNoAttributes(el);
		fileRules = new HashMap();
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String childName = childEl.getNodeName();
			if (childName.equals("fileRule")) {
				FileRule fileRule = new FileRule(childEl);
				fileRules.put(fileRule.getName(), fileRule);
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of fileRules element");
			}
		}
	}

	/**	Applies the rule.
	 *
	 *	@param	fileName		File name.
	 *
	 *	@return		Header value map. Maps names of WordHoard header items to
	 *				their values, or an empty map if there is not rule for this file name.
	 */

	public Map applyRule (String fileName) {
		FileRule fileRule = (FileRule)fileRules.get(fileName);
		if (fileRule == null) {
			return new HashMap();
		} else {
			return fileRule.applyRule();
		}
	}

	/**	Apply the author rule.
	 *
	 *	@param	fileName		File name.
	 *
	 *	@return		List of authors, or null if there is no rule for this file name.
	 */

	public List applyAuthorRule (String fileName) {
		FileRule fileRule = (FileRule)fileRules.get(fileName);
		if (fileRule == null) return null;
		return fileRule.applyAuthorRule();
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


