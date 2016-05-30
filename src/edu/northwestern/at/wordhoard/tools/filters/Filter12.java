package edu.northwestern.at.wordhoard.tools.filters;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import java.sql.*;

import edu.northwestern.at.wordhoard.model.wrappers.*;

import edu.northwestern.at.utils.xml.*;

/**	Adjusts divs.
 *
 *	<p>Usage:
 *
 *	<p><code>Filter12 in out</code>
 *
 *	<p>in = Path to TEI XML input file for a work.
 *
 *	<p>out = Path to TEI XML output file for a work.
 */
 
public class Filter12 {
	
	/**	Input file path. */
	
	private static String inPath;
	
	/**	Output file path. */
	
	private static String outPath;
	
	/**	Output file. */
	
	private static PrintWriter out;
	
	/**	DOM tree for parsed TEI-format XML document. */
	
	private static Document document;
	
	/**	Corpus tag. */
	
	private static String corpusTag;
	
	/**	Work tag. */
	
	private static String workTag;
	
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
		textElements.add("pathTag");
		textElements.add("fullTitle");
		textElements.add("pubDate");
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
			System.err.println("Usage: Filter12 in out");
			System.exit(1);
		}
		inPath = args[0];
		outPath = args[1];
	}
	
	/**	Prints tabs.
	 *
	 *	@param	indentation		Indentation level.
	 */
	 
	private static void tab (int indentation) {
		for (int i = 0; i < indentation; i++) out.print("\t");
	}
	
	/**	Prints a string with indentation.
	 *
	 *	@param	indentation		Indentation level.
	 *
	 *	@param	str				String.
	 */
	 
	private static void print (int indentation, String str) {
		tab(indentation);
		out.println(str);
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
		
		Node parent = el.getParentNode();
		String parentName = parent == null ? "" : parent.getNodeName();
		Element parentEl = (parent instanceof Element) ? (Element)parent : null;
		if (parentName.equals("taggingData")) {
			print(indentation, "<" + name + "/>");
			return;
		}
		
		boolean elHasText = textElements.contains(name);
		if (elHasText && inLine && name.equals("hi")) elHasText = false;
		boolean isLine = name.equals("wordHoardTaggedLine");
		
		boolean isDiv = name.equals("div");
		boolean isCastListDiv = isDiv &&
			el.getAttribute("type").equals("castList");
		
		if (isCastListDiv) {
			el.setAttribute("id", "cast");
			el.setAttribute("indent", "20");
		}
		
		if (name.equals("title") && parentName.equals("div") &&
			parentEl.getAttribute("type").equals("castList"))
				name = "head";
		
		if (corpusTag.equals("spe") && workTag.equals("faq") &&
			name.equals("body"))
				name = "front";
		
		if (!inText) tab(indentation);
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
		
		if (isCastListDiv) {
			print(indentation+1, "<wordHoardHeader>");
				print(indentation+2, "<title>Dramatis Personae</title>");
				print(indentation+2, "<taggingData>");
				print(indentation+2, "</taggingData>");
			print(indentation+1, "</wordHoardHeader>");
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
		
		if (corpusTag.equals("spe") && workTag.equals("faq") &&
			name.equals("front"))
				name = "body";
		
		if (!inText && !elHasText) tab(indentation);
		out.print("</" + name + ">");
		if (!inText) out.println();
		
		if (name.equals("wordHoardHeader") && parentName.equals("div")) {
			boolean partHasText = DOMUtils.nodeHasDescendant(parentEl,
				new String[]{"wordHoardTaggedLine", "p"},
				new String[]{"div"});
			if (partHasText) {
				Element fullTitleEl = DOMUtils.getChild(el, "fullTitle");
				String fullTitle = "";
				if (fullTitleEl != null) {
					fullTitle = DOMUtils.getText(fullTitleEl);
				} else {
					Element titleEl = DOMUtils.getChild(el, "title");
					fullTitle = DOMUtils.getText(titleEl);
					Node pNode = parentEl.getParentNode();
					while (pNode.getNodeName().equals("div")) {
						Element pEl = (Element)pNode;
						Element pHeadEl = DOMUtils.getChild(pEl,
							"wordHoardHeader");
						Element pTitleEl = DOMUtils.getChild(pHeadEl,
							"title");
						String pTitle = DOMUtils.getText(pTitleEl);
						fullTitle = pTitle + ", " + fullTitle;
						pNode = pEl.getParentNode();
					}
				}
				print(indentation, "<head>" + fullTitle + "</head>");
			}
		}
		
		if (corpusTag.equals("spe") && workTag.equals("faq") &&
			name.equals("div") && el.getAttribute("id").equals("ded"))
		{
			print(indentation-1, "</front>");
			print(indentation-1, "<body>");
		}
		
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
		
		//	Get the corpus and work information.
		
		int k = outPath.lastIndexOf('.');
		int j = outPath.lastIndexOf('/', k-1);
		int i = outPath.lastIndexOf('/', j-1);
		corpusTag = outPath.substring(i+1, j);
		workTag = outPath.substring(j+1, k);
			
		//	Read and parse the input file.
		
		document = DOMUtils.parse(inPath);
		
		//	Open the output file.
		
		FileOutputStream fos = new FileOutputStream(outPath);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter bw = new BufferedWriter(osw);
		out = new PrintWriter(bw);
		
		//	Print the XML prologue line.
		
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		
		//	Process the root node.
		
		Node node = DOMUtils.getChild(document, "WordHoardText");
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
	 
	private Filter12 () {
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

