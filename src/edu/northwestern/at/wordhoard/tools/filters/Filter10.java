package edu.northwestern.at.wordhoard.tools.filters;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.sql.*;

import edu.northwestern.at.wordhoard.model.wrappers.*;

import edu.northwestern.at.utils.xml.*;

/**	Adds speaker gender and mortality tags.
 *
 *	<p>Usage:
 *
 *	<p><code>Filter10 in out</code>
 *
 *	<p>in = Path to TEI XML input file for a work.
 *
 *	<p>out = Path to TEI XML output file for a work.
 */
 
public class Filter10 {
	
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
	
	/**	Speaker tags class. */
	
	private static class SpeakerTags {
		private String gender;
		private String mortality;
	}
	
	/**	Shakespeare speaker map. Maps speaker ids to speaker tags. */
	
	private static HashMap speakerMap = new HashMap();
	
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
			System.err.println("Usage: Filter10 in out");
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
		
		boolean isRole = name.equals("role");
		String roleText = null;
		if (isRole) {
			String id = el.getAttribute("id");
			if (corpusTag.equals("sha") && workTag.equals("he5") &&
				id.length() == 8 && id.substring(0,7).equals("Grandpr"))
			{
				id = "Grandpre";
				el.setAttribute("id", id);
			}
			SpeakerTags tags = (SpeakerTags)speakerMap.get(id);
			if (tags != null) {
				el.setAttribute("gender", tags.gender);
				el.setAttribute("mortality", tags.mortality);
			}
			roleText = DOMUtils.getText(el);
			if (roleText == null || roleText.length() == 0)
				roleText = el.getAttribute("rend");
		}
		if (corpusTag.equals("sha") && workTag.equals("he5") &&
			name.equals("sp"))
		{
			String who = el.getAttribute("who");
			if (who.length() == 8 && who.substring(0,7).equals("Grandpr"))
				el.setAttribute("who", "Grandpre");
		}
		
		if (name.equals("div") && corpusTag.equals("cha") &&
			workTag.equals("ast") && el.getAttribute("id").equals("null-p"))
				el.setAttribute("id","p");
				
		if (name.equals("div") && !parentName.equals("front")) {
			boolean partHasSpeech = DOMUtils.nodeHasDescendant(el,
				new String[]{"sp"}, new String[]{"div"});
			if (!partHasSpeech) el.setAttribute("indent", "20");
		}
		
		if (!inText) tab(indentation);
		out.print("<" + name);
		NamedNodeMap attributes = el.getAttributes();
		int numAttributes = attributes.getLength();
		for (int i = 0; i < numAttributes; i++) {
			Node attribute = attributes.item(i);
			String attrName = attribute.getNodeName();
			String attrVal = attribute.getNodeValue();
			if (isRole && attrName.equals("rend")) continue;
			out.print(" " + attrName + "=\"" + attrVal + "\"");
		}
		out.print(">");
		if (!inText && !elHasText) out.println();
		
		if (isRole) {
			out.println(roleText + "</role>");
			return;
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
		
		if (!inText && !elHasText) tab(indentation);
		out.print("</" + name + ">");
		if (!inText) out.println();
		
	}
	
	/**	Creates the speaker map for a Shakespeare work.
	 *
	 *	@throws	Exception
	 */
	
	private static void createShakespeareSpeakerMap ()
		throws Exception
	{
		Class.forName("com.mysql.jdbc.Driver");
		ClassLoader loader = Filter10.class.getClassLoader();
		InputStream in = loader.getResourceAsStream("martin.properties");
		Properties properties = new Properties();
		properties.load(in);
		in.close();
		String url = properties.getProperty("database-url");
		String username = properties.getProperty("database-username");
		String password = properties.getProperty("database-password");
		Connection martinConnection = 
			DriverManager.getConnection(url, username, password);

		PreparedStatement p = martinConnection.prepareStatement(
			"select speaker_id, sex, mortality from more_speakers where idno=?");
		p.setString(1, workTag.equals("pht") ? "phtu" : workTag);
		ResultSet r = p.executeQuery();
		while (r.next()) {
			String id = r.getString(1);
			int gender = r.getInt(2);
			int mortality = r.getInt(3);
			SpeakerTags speakerTags = new SpeakerTags();
			switch (gender) {
				case 0:
					speakerTags.gender = "uncertainMixedOrUnknown";
					break;
				case 1:
					speakerTags.gender = "male";
					break;
				case 2:
					speakerTags.gender = "female";
					break;
				default:
					System.out.println("##### " +
						"Invalid sex for speaker: " + id);
					continue;
			}
			switch (mortality) {
				case 1:
					speakerTags.mortality = "mortal";
					break;
				case 2:
					speakerTags.mortality = "immortalOrSupernatural";
					break;
				case 3:
					speakerTags.mortality = "unknownOrOther";
					break;
				default:
					System.out.println("##### " +
						"Invalid mortality for speaker: " + id);
					continue;
			}
			speakerMap.put(id, speakerTags);
		}

		martinConnection.close();
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
			
		//	Read and parse the input file.
		
		document = DOMUtils.parse(inPath);
		
		//	Open the output file.
		
		FileOutputStream fos = new FileOutputStream(outPath);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter bw = new BufferedWriter(osw);
		out = new PrintWriter(bw);
		
		//	Print the XML prologue line.
		
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		
		//	Create the Shakespeare speaker map.
		
		if (corpusTag.equals("sha")) createShakespeareSpeakerMap();
		
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
	 
	private Filter10 () {
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

