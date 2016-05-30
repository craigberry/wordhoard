package edu.northwestern.at.wordhoard.tools.filters;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.northwestern.at.utils.xml.*;

/**	Rationalizes titles.
 *
 *	<p>Usage:
 *
 *	<p><code>Filter07 in out</code>
 *
 *	<p>in = Path to TEI XML input file for a work.
 *
 *	<p>out = Path to TEI XML output file for a work.
 *
 *	<p>In "div" elements, "head" and "fullTitle" attributes are
 *	replaced by "title" and "fullTitle" children.
 *
 *	<p>In "div" and "castGroup" elements, the first "head" child, if
 *	any, is replaced by a "title" child.
 *
 *	<p>We nuke the type="subordinate" attribute on work titles.
 */
 
public class Filter07 {
	
	/**	Input file path. */
	
	private static String inPath;
	
	/**	Output file path. */
	
	private static String outPath;
	
	/**	Output file. */
	
	private static PrintWriter out;
	
	/**	DOM tree for parsed TEI-format XML document. */
	
	private static Document document;
	
	/**	Set of all text-bearing element names. */
	
	private static HashSet textElements = new HashSet();
	
	/**	Initializes the set of text-bearing element names. */
	
	static {
		textElements.add("title");
		textElements.add("author");
		textElements.add("name");
		textElements.add("resp");
		textElements.add("head");
		textElements.add("role");
		textElements.add("roleDesc");
		textElements.add("speaker");
		textElements.add("p");
		textElements.add("stage");
		textElements.add("w");
		textElements.add("hi");
		textElements.add("punc");
	}
	
	/**	Parses command line arguments.
	 *
	 *	@param	args		Command line arguments.
	 *
	 *	@throws Exception
	 */
	
	private static void parseArgs (String[] args) 
		throws Exception
	{
		int n = args.length;
		if (n != 2) {
			System.err.println("Usage: Filter07 in out");
			System.exit(1);
		}
		inPath = args[0];
		outPath = args[1];
	}
	
	/**	Processes a node.
	 *
	 *	@param	node			Node.
	 *
	 *	@param	indentation		Indentation level.
	 *
	 *	@param	inText			True if we are inside an element which
	 *							has text descendants.
	 *
	 *	@param	inLine			True if we are inside a tagged line.
	 */
	
	private static void processNode (Node node, int indentation, 
		boolean inText, boolean inLine) 
	{
	
		Element el = (Element)node;
		String name = el.getNodeName();

		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		
		if (name.equals("p") && numChildren == 0) {
			for (int i = 0; i < indentation; i++) out.print("\t");
			out.println("<p/>");
			return;
		}
		
		boolean elHasText = textElements.contains(name);
		if (elHasText && inLine && name.equals("hi")) elHasText = false;
		boolean isLine = name.equals("l");
		
		String title = null;
		String fullTitle = null;
		if (name.equals("div")) {
			title = el.getAttribute("head");
			if (title.length() == 0) title = null;
			fullTitle = el.getAttribute("fullTitle");
			if (fullTitle.length() == 0) fullTitle = null;
		}
		
		if (name.equals("head")) {
			Element parent = (Element)el.getParentNode();
			String parentName = parent.getNodeName();
			if (parentName.equals("div") || parentName.equals("castGroup")) {
				Node prev = el.getPreviousSibling();
				while (prev != null) {
					if (prev.getNodeName().equals("head")) break;
					prev = prev.getPreviousSibling();
				}
				if (prev == null) name = "title";
				if (prev != null) System.err.println(outPath);
			}
		}
		
		if (!inText) 
			for (int i = 0; i < indentation; i++) out.print("\t");
		out.print("<" + name);
		NamedNodeMap attributes = el.getAttributes();
		int numAttributes = attributes.getLength();
		for (int i = 0; i < numAttributes; i++) {
			Node attribute = attributes.item(i);
			String attrName = attribute.getNodeName();
			String attrVal = attribute.getNodeValue();
			if (attrName.equals("type") && attrVal.equals("subordinate"))
				continue;
			if (attrName.equals("head") || attrName.equals("fullTitle")) 
				continue;
			out.print(" " + attrName + "=\"" + attrVal + "\"");
		}
		out.print(">");
		if (!inText && !elHasText) out.println();
		
		if (title != null) {
			for (int i = 0; i <= indentation; i++) out.print("\t");
			out.println("<title>" + title + "</title>");
		}
		if (fullTitle != null) {
			for (int i = 0; i <= indentation; i++) out.print("\t");
			out.println("<fullTitle>" + fullTitle + "</fullTitle>");
		}
		
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child == null) continue;
			if (child.getNodeType() == Node.TEXT_NODE) {
				if (elHasText) {
					String str = child.getNodeValue();
					str = str.replaceAll("&", "&amp;");
					out.print(str);
				}
			} else {
				processNode(child, indentation + 1, 
					inText || elHasText,
					inLine || isLine);
			}
		}
		
		if (!inText && !elHasText) 
			for (int i = 0; i < indentation; i++) out.print("\t");
		out.print("</" + name + ">");
		if (!inText) out.println();
		
	}

	/**	Filters the text.
	 *
	 *	@param	args		Command line arguments.
	 *
	 *	@throws Exception
	 */
	 
	private static void filterText (String args[])
		throws Exception
	{
	
		//	Parse the arguments.
	
		parseArgs(args);
		System.out.println("Filtering " + inPath + " -> " + outPath);
			
		//	Read and parse the input file.
		
		document = DOMUtils.parse(inPath);
		
		//	Open the output file.
		
		FileOutputStream fos = new FileOutputStream(outPath);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter bw = new BufferedWriter(osw);
		out = new PrintWriter(bw);
		
		//	Print the XML prologue line.
		
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		
		//	Process the root TEI.2 node.
		
		Node node = DOMUtils.getChild(document, "TEI.2");
		processNode(node, 0, false, false);
		
		//	Close the output file.
		
		out.close();
		
	}
 
	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	public static void main (String args[]) {
		try {
			filterText(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**	Hides the default no-arg constructor.
	 */
	 
	private Filter07 () {
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

