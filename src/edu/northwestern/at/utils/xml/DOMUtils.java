package edu.northwestern.at.utils.xml;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.net.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**	XML DOM utilities.
 */

public class DOMUtils {

	/**	Parses an XML file.
	 *
	 *	@param	file		File.
	 *
	 *	@return				DOM document.
	 *
	 *	@throws	Exception
	 */

	public static Document parse (File file)
		throws IOException, ParserConfigurationException, SAXException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(file);
	}

	/**	Parses an XML file.
	 *
	 *	@param	path		File path.
	 *
	 *	@return				DOM document.
	 *
	 *	@throws	Exception
	 */

	public static Document parse (String path)
		throws IOException, ParserConfigurationException, SAXException
	{
		return parse(new File(path));
	}

	/**	Parses XML document from URL.
	 *
	 *	@param	url		URL.
	 *
	 *	@return				DOM document.
	 *
	 *	@throws	Exception
	 */

	public static Document parse (URL url)
		throws IOException, ParserConfigurationException, SAXException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(url.openStream());
	}

	/**	Gets a child element of a node by name.
	 *
	 *	@param	node	Node.
	 *
	 *	@param	name	Name.
	 *
	 *	@return			First child element with given tag name, or
	 *					null if none found.
	 */

	public static Element getChild (Node node, String name) {
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			if (child.getNodeName().equals(name)) return (Element)child;
		}
		return null;
	}

	/**	Gets the last child element of a node by name.
	 *
	 *	@param	node	Node.
	 *
	 *	@param	name	Name.
	 *
	 *	@return			Last child element with given tag name, or
	 *					null if none found.
	 */

	public static Element getLastChild (Node node, String name) {
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = numChildren-1; i >= 0; i--) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			if (child.getNodeName().equals(name)) return (Element)child;
		}
		return null;
	}

	/**	Gets a child element of a node by name and attribute value.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	name		Name.
	 *
	 *	@param	attrName	Attribute name.
	 *
	 *	@param	attrValue	Attribute value.
	 *
	 *	@return				First child element with given tag name and
	 *						given attribute value, or null if none found.
	 */

	public static Element getChild (Node node, String name,
		String attrName, String attrValue)
	{
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			if (child.getNodeName().equals(name)) {
				Element el = (Element)child;
				if (attrValue.equals(el.getAttribute(attrName))) return el;
			}
		}
		return null;
	}

	/**	Gets text for a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@return				Value of first child text node, or the empty
	 *						string if none found, with leading and trailing 
	 *						white space trimmed.
	 */

	public static String getText (Node node) {
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE)
				return child.getNodeValue().trim();
		}
		return "";
	}

	/**	Sets text for a node.
	 *
	 *	<p>Sets the value of the first child text node, if any.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	text		New text for the node.
	 */

	public static void setText (Node node, String text) {
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE) {
				child.setNodeValue(text);
				return;
			}
		}
	}


	/**	Gets all the text for a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@return				Values of each child text node concateneated
	 *						together in order, with leading and trailing
	 *						white space trimmed.
	 */

	public static String getAllText (Node node) {
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		String result = "";
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE)
				result = result + child.getNodeValue();
		}
		return result.trim();
	}

	/**	Gets the child elements of a node by name.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	name		Name.
	 *
	 *	@return				All of the child elements of the node with
	 *						the given tag name, in order.
	 */

	public static ArrayList getChildren (Node node, String name) {
		ArrayList result = new ArrayList();
		NodeList children = node.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() != Node.ELEMENT_NODE) continue;
			if (child.getNodeName().equals(name))
				result.add(child);
		}
		return result;
	}
	
	/**	Gets the child elements of a node by name and attribute value.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	name		Name.
	 *
	 *	@param	attrName	Attribute name.
	 *
	 *	@param	attrValue	Attribute value.
	 *
	 *	@return				All of the child elements of the node with
	 *						the given tag name which have the given
	 *						attribute value, in order.
	 */
	 
	public static ArrayList getChildren (Node node, String name,
		String attrName, String attrValue)
	{
		ArrayList children = getChildren(node, name);
		ArrayList result = new ArrayList();
		for (Iterator it = children.iterator(); it.hasNext(); ) {
			Element el = (Element)it.next();
			if (el.getAttribute(attrName).equals(attrValue))
				result.add(el);
		}
		return result;
	}

	/**	Gets a descendant element of a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	path		Path to descendant, using tag names of child
	 *						elements separated by "/".
	 *
	 *	@return				First descendant element, or null if none found.
	 */

	public static Element getDescendant (Node node, String path) {
		StringTokenizer tok = new StringTokenizer(path, "/");
		while (tok.hasMoreTokens()) {
			node = getChild(node, tok.nextToken());
			if (node == null) return null;
		}
		return (Element)node;
	}
	
	/**	Gets descendant elements of a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	path		Path to descendants, using tag names of child
	 *						elements separated by "/".
	 *
	 *	@return				A list of descendant elements.
	 */
	
	public static ArrayList getDescendants (Node node, String path) {
		int k = path.indexOf('/');
		String name = k < 0 ? path : path.substring(0, k);
		ArrayList children = getChildren(node, name);
		if (k < 0) return children;
		ArrayList result = new ArrayList();
		String remaining = path.substring(k+1);
		for (Iterator it = children.iterator(); it.hasNext(); ) {
			Element child = (Element)it.next();
			result.addAll(getDescendants(child, remaining));
		}
		return result;
	}

	/**	Creates a new empty DOM document.
	 *
	 *	@return		New empty DOM document.
	 *
	 *	@throws	ParserConfigurationException
	 */

	public static Document newDocument ()
		throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.newDocument();
	}

	/**	Saves a DOM document to an XML file in utf-8.
	 *
	 *	@param	document	DOM document.
	 *	@param	path		Output file path.
	 *
	 *	@throws	TransformerException, IOException
	 */

	public static void save (Document document, String path)
		throws TransformerException, IOException
	{
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(document);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		PrintWriter pw = new PrintWriter(new FileOutputStream(path));
		StreamResult destination = new StreamResult(pw);
		transformer.transform(source, destination);
		pw.close();
	}
	
	/**	Checks to see if a node has a child of some name.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	name		Name.
	 *
	 *	@return				True if node has a child with the given name.
	 */
	 
	public static boolean nodeHasChild (Node node, String name) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String childName = child.getNodeName();
			if (childName.equals(name)) return true;
		}
		return false;
	}
	
	/**	Checks to see if a node has a given name or has a child of the 
	 *	given name.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	name		Name.
	 *
	 *	@return				True if node has a child with the given name.
	 */
	 
	public static boolean nodeIsOrHasChild (Node node, String name) {
		String nodeName = node.getNodeName();
		if (nodeName.equals(name)) return true;
		return nodeHasChild(node, name);
	}
	
	/**	Checks to see if a node has a descendant of some name.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	name		Name.
	 *
	 *	@return				True if node has a descendant with the given name.
	 */
	 
	public static boolean nodeHasDescendant (Node node, String name) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String childName = child.getNodeName();
			if (childName.equals(name)) return true;
			if (nodeHasDescendant(child, name)) return true;
		}
		return false;
	}
	
	/**	Checks to see if a node has a given name or has a descendant of the
	 *	given name.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	name		Name.
	 *
	 *	@return				True if node has or has a descendant with the given name.
	 */
	 
	public static boolean nodeIsOrHasDescendant (Node node, String name) {
		String nodeName = node.getNodeName();
		if (nodeName.equals(name)) return true;
		return nodeHasDescendant(node, name);
	}
	 
	/**	Checks to see if a node has a descendant node of some name, other
	 *	than those in children of some other name.
	 *
	 *	@param	node		Node
	 *
	 *	@param	names1		Array of names to include.
	 *
	 *	@param	names2		Array of names to exclude.
	 *
	 *	@return				True if the node contains a descendant in names1,
	 *						but not in children in names2.
	 */
	
	public static boolean nodeHasDescendant (Node node, String[] names1,
		String[] names2) 
	{
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String childName = child.getNodeName();
			for (int j = 0; j < names1.length; j++)
				if (childName.equals(names1[j])) return true;
			boolean isInNames2 = false;
			for (int j = 0; j < names2.length; j++) {
				if (childName.equals(names2[j])) {
					isInNames2 = true;
					break;
				}
			}
			if (isInNames2) continue;
			if (nodeHasDescendant(child, names1, names2)) return true;
		}
		return false;
	}

	/**	Hides the default no-arg constructor.
	 */

	private DOMUtils () {
		throw new UnsupportedOperationException();
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

