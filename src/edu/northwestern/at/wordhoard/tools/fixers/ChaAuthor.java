package edu.northwestern.at.wordhoard.tools.fixers;

/*	Please see the license information at the end of this file. */

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Fixes the Chaucer "author" element.
 *
 *	<p>Makes sure the author is "Geoffrey Chaucer".
 */

public class ChaAuthor extends Fixer {

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
		Element authorEl = DOMUtils.getDescendant(document,
			"TEI.2/teiHeader/fileDesc/titleStmt/author");
		String author = DOMUtils.getText(authorEl);
		if (author.equals("Geoffrey Chaucer")) return;
		DOMUtils.setText(authorEl, "Geoffrey Chaucer");
		log("ChaAuthor", "Author changed to 'Geoffrey Chaucer'");
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

