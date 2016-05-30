package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	Pretty-prints a work XML document file.
 *
 *	<p>Usage:
 *
 *	<p><code>PrettyPrint in out</code>
 *
 *	<p>in = Path to XML input file for a work.
 *
 *	<p>out = Path to pretty-printed XML output file for the work.
 */
 
public class PrettyPrint {

	/**	Alignment spacing for w and punc elements. */
	
	private static final int SPACING = 40;
	
	/**	Output file. */
	
	private static PrintWriter out;
	
	/**	Tagging data provider, or null if none. */
	
	private static TaggingDataProvider provider;
	
	/**	Set of all text-bearing element names. */
	
	private static HashSet textElements = new HashSet();
	
	static {
		textElements.add("title");
		textElements.add("shortTitle");
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
	
	/**	Maps an element name to an array of its attribute names, in
	 *	the order in which they should be printed.
	 */
	 
	private static HashMap attributeMap = new HashMap();
	
	static {
		attributeMap.put("wordHoardHeader",
			new String[] {"corpus", "work", "prosodic"});
		attributeMap.put("div",
			new String[] {"id", "type", "numberingStyle", "indent",
				"rend"});
		attributeMap.put("lg",
			new String[] {"type", "n", "rend"});
		attributeMap.put("sp",
			new String[] {"who", "rend"});
		attributeMap.put("wordHoardTaggedLine",
			new String[] {"id", "n", "label", "prosodic", "align", 
				"rend"});
		attributeMap.put("p",
			new String[] {"id", "n", "label", "align", "rend"});
		attributeMap.put("head",
			new String[] {"id", "n", "label", "align", "rend"});
		attributeMap.put("w",
			new String[] {"id", "lemma", "pos", "prosodic", 
				"metricalShape", "bensonGloss"});
		attributeMap.put("hi",
			new String[] {"rend"});
		attributeMap.put("castItem",
			new String[] {"type", "rend"});
		attributeMap.put("role",
			new String[] {"id", "gender", "mortality", "originalName"});
		attributeMap.put("castGroup",
			new String[] {"rend"});
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
		
		if (!inText) tab(indentation);
		out.print("<" + name);
		NamedNodeMap attributes = el.getAttributes();
		String[] attrNames = (String[])attributeMap.get(name);
		if (name.equals("w")) {
			boolean firstAttr = true;
			int len = 0;
			String id = null;
			String[] wordPartVals = null;
			for (int i = 0; i < attrNames.length; i++) {
				String attrName = attrNames[i];
				String attrVal = null;
				if (provider != null) {
					if (attrName.equals("id")) {
						attrVal = el.getAttribute("id");
						id = attrVal;
						try {
							wordPartVals = provider.getMorph(el, id);
						} catch (Exception e) {
							e.printStackTrace();
							System.exit(1);
						}
					} else if (attrName.equals("lemma") &&
						wordPartVals != null) 
					{
						attrVal = wordPartVals[0];
					} else if (attrName.equals("pos") &&
						wordPartVals != null)
					{
						attrVal = wordPartVals[1];
					}
				}
				if (attrVal == null) attrVal = el.getAttribute(attrName);
				if (attrVal.length() > 0) {
					if (!firstAttr) {
						out.println();
						tab(indentation);
						out.print("  ");
					}
					String str = " " + attrName + "=\"" + attrVal + "\"";
					out.print(str);
					len = 2 + str.length();
					firstAttr = false;
				}
			}
			for (int i = 0; i < SPACING-len; i++) out.print(" ");
		} else if (name.equals("punc")) {
			for (int i = 0; i < SPACING-5; i++) out.print(" ");
		} else {
			if (attrNames != null) {
				for (int i = 0; i < attrNames.length; i++) {
					String attrName = attrNames[i];
					String attrVal = null;
					if (name.equals("role")) {
						String speakerId = el.getAttribute("id");
					}
					if (attrVal == null) attrVal = el.getAttribute(attrName);
					if (attrVal.length() > 0)
						out.print(" " + attrName + "=\"" + attrVal + "\"");
				}
			}
		}
		out.print(">");
		if (!inText && !elHasText) out.println();
		
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
		
		if (!inText && !elHasText) tab(indentation);
		out.print("</" + name + ">");
		if (!inText) out.println();

	}

	/**	Pretty prints a work XML document.
	 *
	 *	@param	document			Work XML document.
	 *
	 *	@param	file				Output file.
	 *
	 *	@param	provider			Tagging data provider, or null if none.
	 *
	 *	@throws Exception
	 */

	public static void prettyPrint (Document document, File file,
		TaggingDataProvider provider) 
			throws Exception
	{
	
		PrettyPrint.provider = provider;
		
		//	Open the output file.
		
		FileOutputStream fos = new FileOutputStream(file);
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
			if (args.length != 2) {
				System.err.println("Usage: PrettyPrint in out");
				System.exit(1);
			}
			String inPath = args[0];
			String outPath = args[1];
			Document document = DOMUtils.parse(inPath);
			File outFile = new File(outPath);
			prettyPrint(document, outFile, null);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**	Hides the default no-arg constructor.
	 */
	 
	private PrettyPrint () {
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

