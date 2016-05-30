package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	Title page rules.
 */

public class TitlePageRules {

	/**	TitlePageRules element. */

	Element el;

	/**	Creates a new title page rules object.
	 *
	 *	@param	el		TitlePageRules element. */

	public TitlePageRules (Element el) {
		Utils.checkNoAttributes(el);
		this.el = el;
	}

	/**	Writes the responsibility statements.
	 *
	 *	@param	out		WordHoard XML output file writer.
	 */

	public void writeRespStmts (XMLWriter out) {
		List respList = DOMUtils.getChildren(el, "respStmt");
		for (Iterator it = respList.iterator(); it.hasNext(); ) {
			out.startEl("respStmt");
			Element respStmtEl = (Element)it.next();
			Element nameEl = DOMUtils.getChild(respStmtEl, "name");
			if (nameEl != null) {
				String name = DOMUtils.getText(nameEl);
				out.writeTextEl("name", name);
			}
			Element respEl = DOMUtils.getChild(respStmtEl, "resp");
			if (respEl != null) {
				String resp = DOMUtils.getText(respEl);
				out.writeTextEl("resp", resp);
			}
			out.endEl("respStmt");
		}
	}

	/**	Processes a node in a publication statement.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	buf			String buffer.
	 */

	private void processPubNode (Node node, StringBuffer buf) {
		short nodeType = node.getNodeType();
		if (nodeType == Node.TEXT_NODE) {
			buf.append(node.getNodeValue());
		} else if (nodeType == Node.ELEMENT_NODE) {
			Element el = (Element)node;
			String name = el.getNodeName();
			buf.append("<" + name);
			NamedNodeMap atts = el.getAttributes();
			for (int i = 0; i < atts.getLength(); i++) {
				Node attNode = atts.item(i);
				String attName = attNode.getNodeName();
				String attValue = attNode.getNodeValue();
				buf.append(" " + attName + "=\"" + attValue + "\"");
			}
			buf.append(">");
			NodeList children = el.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				processPubNode(child, buf);
			}
			buf.append("</" + name + ">");
		}
	}

	/**	Processes a node in a publication statement.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	buf			String buffer.
	 */

	private void processOrigPubNode (Node node, StringBuffer buf) {
		short nodeType = node.getNodeType();
		if (nodeType == Node.TEXT_NODE) {
			buf.append(XMLWriter.escapeXML(node.getNodeValue().trim()));
		} else if (nodeType == Node.ELEMENT_NODE) {
			Element el = (Element)node;
			String name = el.getNodeName();
			NamedNodeMap atts = el.getAttributes();
			if (!name.equals("p")) {
				buf.append("<p>");
			}
			for (int i = 0; i < atts.getLength(); i++) {
				Node attNode = atts.item(i);
				String attName = attNode.getNodeName();
				String attValue = attNode.getNodeValue();
				buf.append( attValue.toUpperCase() + ": " );
			}
			NodeList children = el.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				processOrigPubNode(child, buf);
			}
			if (!name.equals("p")) {
				buf.append("</p>");
			}
		}
	}

	/**	Writes the publication statement.
	 *
	 *	@param	out			WordHoard XML output file writer.
	 *	@param	document	Parsed document.
	 */

	public void writePublicationStmt (XMLWriter out, Document document) {
		Element publicationStmtEl = DOMUtils.getChild(el, "publicationStmt");
		if (publicationStmtEl == null) return;
		out.startEl("publicationStmt");
		List pList = DOMUtils.getChildren(publicationStmtEl, "p");
		for (Iterator it = pList.iterator(); it.hasNext(); ) {
			Element pEl = (Element)it.next();
			StringBuffer buf = new StringBuffer();
			processPubNode(pEl, buf);
			out.writeString(buf.toString());
		}
		if (document != null) {
			Element pStmtEl = DOMUtils.getDescendant(document,
				"TEI/teiHeader/fileDesc/publicationStmt");
			NodeList children = pStmtEl.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				StringBuffer buf = new StringBuffer();
				processOrigPubNode(child, buf);
				out.writeString(buf.toString());
			}
		}
		out.endEl("publicationStmt");
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


