package edu.northwestern.at.wordhoard.tools.filters;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.wordhoard.tools.*;
import edu.northwestern.at.wordhoard.tools.fixers.*;

/**	Applies fixers.
 *
 *	<p>Runs the fixers, pretty-prints the output, eliminates unused elements 
 *	and attributes, eliminates punctuation elements, and fixes title page text.
 *
 *	<p>Usage:
 *
 *	<p><code>Filter01 in out corpus-tag work-tag [fixers]</code>
 *
 *	<p>in = Path to TEI XML input file for a work.
 *
 *	<p>out = Path to TEI XML output file for a work.
 *
 *	<p>corpus-tag = Corpus tag (e.g., "sha" for Shakespeare).
 *
 *	<p>work-tag = Work tag (e.g., "ham" for Hamlet).
 *
 *	<p>fixers = Optional comma-separated list of XML "fixers". These are
 *	   the unqualified names of classes in the package 
 *	   "edu.northwestern.at.wordhoard.tools.fixers". The fixers are
 *	   executed in turn to fix problems and irregularites in the parsed
 *	   XML DOM tree to transform it into normalized form.
 *
 *	<p>A report is written to stdout which contains detailed messages
 *	from the fixers.
 *
 *	<p>The following transformations are performed:
 *
 *	<ol>
 *
 *	<li>The specified fixers are run, if any.
 *
 *	<li>The output is pretty-printed. Tab characters are used to indent
 *	lines. Elements which contain text descendants (e.g., "title", "head", 
 *	and "l") are output on a single line.
 *
 *	<li>Unused elements and attributes are elminated. This includes "title"
 *	elements with a "type" attribute other than "subordinate".
 *
 *	<li>The "c", "seg", and "gap" punctuation elements are removed and
 *	replaced by just the punctuation text they contain.
 *
 *	<li>Title page text in responsibility and publication statements is
 *	cleaned up. All runs of white space are replaced by a single space
 *	character, including embedded new line characters. ") )" is replaced 
 *	by ")". "Larry D Benson" is replaced by "Larry D. Benson".
 *
 *	</ol>
 */

public class Filter01 {
	
	/**	Input file path. */
	
	private static String inPath;
	
	/**	Output file path. */
	
	private static String outPath;

	/**	Corpus tag. */
	
	private static String corpusTag;
	
	/**	Work tag. */
	
	private static String workTag;
	
	/**	Fixers. */
	
	private static String[] fixers;
	
	/**	Output file. */
	
	private static PrintWriter out;
	
	/**	DOM tree for parsed TEI-format XML document. */
	
	private static Document document;
	
	/**	Element information class. */
	
	private static class ElementInfo {
	
		/**	Element name. */
	
		private String name;
		
		/**	True if the element has text descendants. */
		
		private boolean hasText;
		
		/**	Array of attributes we use, or null. */
		
		private String[] attributes = null;
		
		/**	Set of children we use. */
		
		private HashSet children = new HashSet();
	}
	
	/**	Map from element names to element information. */
	
	private static HashMap elementInfoMap = new HashMap();
	
	/**	Defines an element.
	 *
	 *	@param	name		Element name.
	 *
	 *	@param	hasText		True if element has text descendants.
	 *
	 *	@param	attributes	Array of attributes we use, or null.
	 *
	 *	@param	children	Array of children we use, or null.
	 */
	
	private static void defineElement (String name, boolean hasText,
		String[] attributes, String[] children)
	{
		ElementInfo info = new ElementInfo();
		info.name = name;
		info.hasText = hasText;
		info.attributes = attributes;
		if (children != null)
			for (int i = 0; i < children.length; i++)
				info.children.add(children[i]);
		elementInfoMap.put(name, info);
	}
	
	/**	Creates all of the element definitions. */
	
	static {
		defineElement("TEI.2",
			false,
			null, 
			new String[] {"teiHeader", "text"});
		defineElement("teiHeader",
			false,
			null,
			new String[] {"fileDesc"});
		defineElement("fileDesc",
			false,
			null,
			new String[] {"titleStmt", "publicationStmt"});
		defineElement("titleStmt",
			false,
			null,
			new String[] {"title", "author", "respStmt"});
		defineElement("title",
			true,
			new String[] {"type"},
			null);
		defineElement("author",
			true,
			null,
			null);
		defineElement("respStmt",
			false,
			null,
			new String[] {"name", "resp"});
		defineElement("name",
			true,
			null,
			null);
		defineElement("resp",
			true,
			null,
			null);
		defineElement("publicationStmt",
			false,
			null,
			new String[] {"p"});
		defineElement("text",
			false,
			null, 
			new String[] {"front", "body"});
		defineElement("front",
			false,
			null,
			new String[] {"div"});
		defineElement("body", 
			false,
			null, 
			new String[] {"div"});
		defineElement("div",
			false,
			new String[] {"type", "tag", "pathTag", "head", "fullTitle"},
			new String[] {"div", "head", "castList", "p", "l", "ab", 
				"lg", "sp", "stage"});
		defineElement("head",
			true,
			null,
			null);
		defineElement("castList",
			false,
			null,
			new String[] {"castItem", "castGroup"});
		defineElement("castItem",
			false,
			new String[] {"type", "roleDesc"},
			new String[] {"role", "roleDesc"});
		defineElement("role",
			true,
			new String[] {"id", "rend"},
			null);
		defineElement("roleDesc",
			true,
			null,
			null);
		defineElement("castGroup",
			false,
			null,
			new String[] {"head", "castItem"});
		defineElement("sp",
			false,
			new String[] {"who"},
			new String[] {"speaker", "p", "l", "ab", "stage", "lg"});
		defineElement("speaker",
			true,
			null,
			null);
		defineElement("lg",
			false,
			new String[] {"type", "n", "rend"},
			new String[] {"p", "l", "ab", "head", "lg"});
		defineElement("p",
			true,
			new String[] {"id", "rend", "align", "n"},
			new String[] {"w", "c", "hi", "seg", "gap", "stage", "title"});
		defineElement("l",
			true,
			new String[] {"id", "rend", "align", "n"},
			new String[] {"w", "c", "hi", "seg", "gap", "stage", "title"});
		defineElement("ab",
			true,
			new String[] {"id", "rend", "align", "n"},
			new String[] {"w", "c", "hi", "seg", "gap", "stage", "title"});
		defineElement("stage",
			true,
			null,
			null);
		defineElement("w",
			true,
			new String[] {"id"},
			null);
		defineElement("c",
			true,
			null,
			null);
		defineElement("seg",
			true,
			null,
			null);
		defineElement("gap",
			true,
			null,
			null);
		defineElement("hi",
			true,
			new String[] {"rend"},
			new String[] {"w", "c", "hi", "seg", "gap", "stage"});
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
		if (n < 4 || n > 5) {
			System.err.println("Usage: Filter01 corpus-tag work-tag " +
				"in out [fixers]");
			System.exit(1);
		}
		inPath = args[0];
		outPath = args[1];
		corpusTag = args[2];
		workTag = args[3];
		if (n == 5) {
			StringTokenizer tok = new StringTokenizer(args[4], ",");
			fixers = new String[tok.countTokens()];
			int i = 0;
			while (tok.hasMoreTokens()) {
				fixers[i++] = tok.nextToken();
			}
		}
	}
	
	/**	Runs the XML fixers.
	 *
	 *	@throws	Exception
	 */
	 
	private static void runFixers ()
		throws Exception
	{
		if (fixers == null) return;
		System.out.println("   Running fixers");
		for (int i = 0; i < fixers.length; i++) {
			String fixerName = fixers[i];
			Class fixerClass = Class.forName(
				"edu.northwestern.at.wordhoard.tools.fixers." +
				fixerName);
			Fixer fixer = (Fixer)fixerClass.newInstance();
			fixer.fix(corpusTag, workTag, document);
		}
	}
	
	/**	Processes a node.
	 *
	 *	@param	node			Node.
	 *
	 *	@param	indentation		Indentation level.
	 *
	 *	@param	inText			True if we are inside an element which
	 *							has text descendants.
	 */
	
	private static void processNode (Node node, int indentation, 
		boolean inText) 
	{
	
		Element el = (Element)node;
		String name = el.getNodeName();
				
		boolean isPunctuationEl =
			name.equals("c") || name.equals("seg") || name.equals("gap");
		if (isPunctuationEl) {
			String str = DOMUtils.getText(el);
			out.print(str);
			return;
		}
		
		ElementInfo info = (ElementInfo)elementInfoMap.get(name);
		Node parentNode = el.getParentNode();
		String parentName = parentNode == null ? "" : parentNode.getNodeName();
		Node grandParentNode = parentNode == null ? null :
			parentNode.getParentNode();
		String grandParentName = grandParentNode == null ? "" :
			grandParentNode.getNodeName();
		
		if (name.equals("title")) {
			String type = el.getAttribute("type");
			if (type.length() > 0 && !type.equals("subordinate")) return;
		}
		
		boolean editTitlePageText = 
			(name.equals("name") && parentName.equals("respStmt")) ||
			(name.equals("resp") && parentName.equals("respStmt")) ||
			(name.equals("p") && parentName.equals("publicationStmt")) ||
			(name.equals("title") && parentName.equals("p") &&
				grandParentName.equals("publicationStmt"));
		
		if (!inText) 
			for (int i = 0; i < indentation; i++) out.print("\t");
		out.print("<" + name);
		if (info.attributes != null) {
			for (int i = 0; i < info.attributes.length; i++) {
				String attr = info.attributes[i];
				String val = el.getAttribute(attr);
				if (name.equals("div") && attr.equals("type") &&
					!parentName.equals("front")) continue;
				if (val != null && val.length() > 0) 
					out.print(" " + attr + "=\"" + val + "\"");
			}
		}
		out.print(">");
		if (!inText && !info.hasText) out.println();
		
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE && info.hasText) {
				String str = child.getNodeValue();
				if (editTitlePageText) {
					str = str.replaceAll("\\s+", " ");
					str = str.replaceAll("\\) \\)", "\\)");
					str = str.replaceAll("Larry D Benson", "Larry D. Benson");
				}
				str = str.replaceAll("&", "&amp;");
				out.print(str);
			} else if (info.children.contains(child.getNodeName())) {
				processNode(child, indentation + 1, inText || info.hasText);
			}
		}
		
		if (!inText && !info.hasText) 
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
		
		//	Run the fixers.
		
		runFixers();
		
		//	Open the output file.
		
		FileOutputStream fos = new FileOutputStream(outPath);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter bw = new BufferedWriter(osw);
		out = new PrintWriter(bw);
		
		//	Print the XML prologue line.
		
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		
		//	Process the root TEI.2 node.
		
		Node node = DOMUtils.getChild(document, "TEI.2");
		processNode(node, 0, false);
		
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
	 
	private Filter01 () {
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

