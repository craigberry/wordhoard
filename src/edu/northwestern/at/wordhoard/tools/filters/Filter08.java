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

/**	Adds the WordHoardText, wordHoardHeader and wordHoardTaggedLine elements.
 *
 *	<p>Usage:
 *
 *	<p><code>Filter08 in out</code>
 *
 *	<p>in = Path to TEI XML input file for a work.
 *
 *	<p>out = Path to TEI XML output file for a work.
 */
 
public class Filter08 {
	
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
	
	/**	True if Shakespeare poem. */
	
	private static boolean shaPoem;
	
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
	
	/**	Map from Shakespeare work tags to publication years. */
	
	private static HashMap shaPubDates = new HashMap();
	
	/**	Initializes the Shakespeare publication date map. */
	
	static {
		shaPubDates.put("1h6","1589-1590");
		shaPubDates.put("2h6","1590-1591");
		shaPubDates.put("3h6","1590-1591");
		shaPubDates.put("tgv","1592-1594");
		shaPubDates.put("tam","1592-1594");
		shaPubDates.put("tit","1592-1593");
		shaPubDates.put("ri3","1592-1593");
		shaPubDates.put("ven","1592-1593");
		shaPubDates.put("rap","1593-1594");
		shaPubDates.put("com","1592-1594");
		shaPubDates.put("lll","1594-1595");
		shaPubDates.put("kij","1594-1596");
		shaPubDates.put("ri2","1595-1596");
		shaPubDates.put("roj","1595-1596");
		shaPubDates.put("mnd","1595-1596");
		shaPubDates.put("1h4","1596-1597");
		shaPubDates.put("mww","1597-1601");
		shaPubDates.put("2h4","1597-1598");
		shaPubDates.put("man","1598-1599");
		shaPubDates.put("he5","1599");
		shaPubDates.put("juc","1599");
		shaPubDates.put("ayl","1599");
		shaPubDates.put("son","1593-1599");
		shaPubDates.put("ham","1600-1601");
		shaPubDates.put("pht","1601");
		shaPubDates.put("twn","1601-1602");
		shaPubDates.put("tro","1601-1602");
		shaPubDates.put("aww","1602-1603");
		shaPubDates.put("mem","1604");
		shaPubDates.put("oth","1603-1604");
		shaPubDates.put("kil","1605");
		shaPubDates.put("mac","1606");
		shaPubDates.put("ant","1606-1607");
		shaPubDates.put("cor","1607-1608");
		shaPubDates.put("tim","1607-1608");
		shaPubDates.put("per","1607-1608");
		shaPubDates.put("cym","1609-1610");
		shaPubDates.put("lov","1599-1609");
		shaPubDates.put("win","1610-1611");
		shaPubDates.put("tem","1611");
		shaPubDates.put("he8","1612-1613");
		shaPubDates.put("mev","1596-1597");
	}
	
	/**	Map from Chaucer work tags to publication years. */
	
	private static HashMap chaPubDates = new HashMap();
	
	/**	Initializes the Chaucer publication date map. */
	
	static {
		chaPubDates.put("can","1386-1400");
		chaPubDates.put("bod","1368-1370");
		chaPubDates.put("hof","1383-1384");
		chaPubDates.put("aaa","1372-1374");
		chaPubDates.put("pof","1382");
		chaPubDates.put("boe","1380-1387");
		chaPubDates.put("tro","1379-1383");
		chaPubDates.put("lgw","1385-1386");
		chaPubDates.put("poe","1366-1400");
		chaPubDates.put("ast","1387-1400");
		chaPubDates.put("ror","1366-1370");
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
			System.err.println("Usage: Filter08 in out");
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
	
	/**	Prints the work header.
	 *
	 *	@param	indentation		Indentation level.
	 */
	 
	private static void printWorkHeader (int indentation) {
		print(indentation, 
			"<wordHoardHeader corpus=\"" + corpusTag +
			"\" work=\"" + workTag + "\">");
			indentation++;
			if (corpusTag.equals("sha")) {
				String pubDate = (String)shaPubDates.get(workTag);
				print(indentation, "<pubDate>" + pubDate + "</pubDate>");
			} else if (corpusTag.equals("spe")) {
				print(indentation, "<pubDate>1596</pubDate>");
			} else if (corpusTag.equals("cha")) {
				String pubDate = (String)chaPubDates.get(workTag);
				print(indentation, "<pubDate>" + pubDate + "</pubDate>");
			}
			print(indentation, "<taggingData>");
				indentation++;
				print(indentation, "<lemma/>");
				print(indentation, "<pos/>");
				print(indentation, "<wordClass/>");
				print(indentation, "<spelling/>");
				if (corpusTag.equals("sha") && !shaPoem) {
					print(indentation, "<speaker/>");
					print(indentation, "<gender/>");
					print(indentation, "<mortality/>");
				}
				print(indentation, "<prosodic/>");
				print(indentation, "<pubDates/>");
				indentation--;
			print(indentation, "</taggingData>");
			indentation--;
		print(indentation, "</wordHoardHeader>");
	}
	
	/**	Prints the part header.
	 *
	 *	@param	indentation		Indentation level.
	 *
	 *	@param	el				"div" element for part.
	 */
	 
	private static void printPartHeader (int indentation, Element el) {
		String partTag = el.getAttribute("id");
		print(indentation, "<wordHoardHeader>");
			indentation++;
			Element titleEl = DOMUtils.getChild(el, "title");
			String title = DOMUtils.getText(titleEl);
			print(indentation, "<title>" + title + "</title>");
			Element fullTitleEl = DOMUtils.getChild(el, "fullTitle");
			if (fullTitleEl != null) {
				String fullTitle = DOMUtils.getText(fullTitleEl);
				print(indentation, "<fullTitle>" + fullTitle + "</fullTitle>");
			}
			String pathTag = el.getAttribute("pathTag");
			if (pathTag.length() > 0)
				print(indentation, "<pathTag>" + pathTag + "</pathTag>");
			print(indentation, "<taggingData>");
				boolean rapeOfLucreceArgument = 
					corpusTag.equals("sha") &&
					workTag.equals("rap") &&
					partTag.equals("a");
				boolean faqDedication =
					corpusTag.equals("spe") &&
					workTag.equals("faq") &&
					partTag.equals("ded");
				if (!rapeOfLucreceArgument && !faqDedication) {
					indentation++;
					print(indentation, "<lemma/>");
					print(indentation, "<pos/>");
					print(indentation, "<wordClass/>");
					print(indentation, "<spelling/>");
					if (corpusTag.equals("sha") && !shaPoem) {
						print(indentation, "<speaker/>");
						print(indentation, "<gender/>");
						print(indentation, "<mortality/>");
					}
					print(indentation, "<prosodic/>");
					print(indentation, "<pubDates/>");
					indentation--;
				}
			print(indentation, "</taggingData>");
			indentation--;
		print(indentation, "</wordHoardHeader>");
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
		
		if (name.equals("l")) name = "wordHoardTaggedLine";
		if (name.equals("TEI.2")) name = "WordHoardText";
		
		if (!inText) tab(indentation);
		out.print("<" + name);
		NamedNodeMap attributes = el.getAttributes();
		int numAttributes = attributes.getLength();
		for (int i = 0; i < numAttributes; i++) {
			Node attribute = attributes.item(i);
			String attrName = attribute.getNodeName();
			String attrVal = attribute.getNodeValue();
			if (!attrName.equals("pathTag"))
				out.print(" " + attrName + "=\"" + attrVal + "\"");
		}
		out.print(">");
		if (!inText && !elHasText) out.println();
		
		if (name.equals("WordHoardText")) printWorkHeader(indentation+1);
		
		boolean partDiv = name.equals("div") && 
			el.getAttribute("id").length() > 0;
		if (partDiv) printPartHeader(indentation+1, el);
		
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
				if (partDiv) {
					String childName = child.getNodeName();
					if (childName.equals("title") || 
						childName.equals("fullTitle"))
							continue;
				}
				processNode(child, indentation + 1, 
					inText || elHasText,
					inLine || isLine);
			}
		}
		
		if (!inText && !elHasText) tab(indentation);
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
		
		//	Get the corpus and work information.
		
		int k = inPath.lastIndexOf('/');
		int j = inPath.lastIndexOf('/', k-1);
		corpusTag = inPath.substring(j+1, k);
		workTag = inPath.substring(k+1, k+4);
		if (corpusTag.equals("sha")) {
			shaPoem = 
				workTag.equals("son") || 
				workTag.equals("pht") ||
				workTag.equals("ven") ||
				workTag.equals("rap") ||
				workTag.equals("lov");
		}
			
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
	 
	private Filter08 () {
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

