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

/**	Trims white space.
 *
 *	<p>Trims leading and trailing white space.
 *
 *	<p>Usage:
 *
 *	<p><code>Filter04 in out</code>
 *
 *	<p>in = Path to TEI XML input file for a work.
 *
 *	<p>out = Path to TEI XML output file for a work.
 *
 *	<p>Leading and trailing white space is trimmed in all text elements which
 *	are not inside "l" elements or children of "p" elements.
 *
 *	<p>For tagged lines ("l" elements), leading white space is timmed from
 *	initial punctuation, and trailing white space is trimmed from final
 *	punctuaton, and any resulting empty "punc" elements are eliminated.
 *
 *	<p>For paragraphs ("p" elements), leading white space is trimmed from
 *	the first text child, and trailing white space is trimmed from the last
 *	text child.
 */
 
public class Filter04 {
	
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
			System.err.println("Usage: Filter04 in out");
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
	 *	@param	inLine			True if inside an "l" element.
	 */
	
	private static void processNode (Node node, int indentation, 
		boolean inText, boolean inLine) 
	{
	
		Element el = (Element)node;
		String name = el.getNodeName();
		
		if (name.equals("p")) {
			int numChildren = el.getChildNodes().getLength();
			if (numChildren == 0) {
				for (int i = 0; i < indentation; i++) out.print("\t");
				out.println("<p/>");
				return;
			}
		}
		
		boolean elHasText = textElements.contains(name);
		boolean isLine = name.equals("l");
		boolean isParagraph = name.equals("p");
		
		if (!inText) 
			for (int i = 0; i < indentation; i++) out.print("\t");
		out.print("<" + name);
		NamedNodeMap attributes = el.getAttributes();
		int numAttributes = attributes.getLength();
		for (int i = 0; i < numAttributes; i++) {
			Node attribute = attributes.item(i);
			String attrName = attribute.getNodeName();
			String attrVal = attribute.getNodeValue();
			out.print(" " + attrName + "=\"" + attrVal + "\"");
		}
		out.print(">");
		if (!inText && !elHasText) out.println();

		NodeList childrenNodeList = el.getChildNodes();
		int numChildren = childrenNodeList.getLength();
		
		Node[] children = new Node[numChildren];
		for (int i = 0; i < numChildren; i++)
			children[i] = childrenNodeList.item(i);
			
		if (isLine) {
			for (int i = 0; i < numChildren; i++) {
				Node child = children[i];
				if (child.getNodeType() == Node.TEXT_NODE) continue;
				if (child.getNodeName().equals("punc")) {
					Node textNode = child.getFirstChild();
					String str = textNode.getNodeValue();
					str = str.replaceAll("^\\s+", "");
					textNode.setNodeValue(str);
					if (str.length() > 0) break;
					children[i] = null;
				} else if (!child.getNodeName().equals("stage")) {
					break;
				}
			}
			for (int i = numChildren-1; i >= 0; i--) {
				Node child = children[i];
				if (child.getNodeType() == Node.TEXT_NODE) continue;
				if (child.getNodeName().equals("punc")) {
					Node textNode = child.getFirstChild();
					String str = textNode.getNodeValue();
					str = str.replaceAll("\\s+$", "");
					textNode.setNodeValue(str);
					if (str.length() > 0) break;
					children[i] = null;
				} else if (!child.getNodeName().equals("stage")) {
					break;
				}
			}
		}
		
		if (isParagraph && numChildren > 0) {
			Node firstChild = children[0];
			if (firstChild.getNodeType() == Node.TEXT_NODE) {
				String str = firstChild.getNodeValue();
				str = str.replaceAll("^\\s*", "");
				firstChild.setNodeValue(str);
			}
			Node lastChild = children[numChildren-1];
			if (lastChild.getNodeType() == Node.TEXT_NODE) {
				String str = lastChild.getNodeValue();
				str = str.replaceAll("\\s+$", "");
				lastChild.setNodeValue(str);
			}
		}
		
		for (int i = 0; i < numChildren; i++) {
			Node child = children[i];
			if (child == null) continue;
			if (child.getNodeType() == Node.TEXT_NODE) {
				if (elHasText) {
					String str = child.getNodeValue();
					if (!inLine && !isParagraph) {
						str = str.trim();
					}
					str = str.replaceAll("&", "&amp;");
					out.print(str);
				}
			} else {
				processNode(child, indentation + 1, 
					inText || elHasText, inLine || isLine);
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
	 
	private Filter04 () {
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

