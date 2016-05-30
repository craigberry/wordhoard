package edu.northwestern.at.utils.tools;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.net.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**	Catalogs XML files.
 *
 *	<p>Usage:
 *
 *	<p><code>CatalogXML dir</code>
 *
 *	<p>dir = path to directory containing XML files and any associated DTD
 *	files.
 *
 *	<p>Each file in the directory with extension ".xml" is scanned. A report
 *	is written to stdout summarizing the XML structure of the files. The
 *	report shows all the possible element paths in a tree format. It's a kind 
 *	of "poor man's DTD."
 */

public class CatalogXML {

	/**	Element info. */

	private static class ElementInfo {
		private String name;
		private ElementInfo parent = null;
		private TreeMap children = new TreeMap();
		private ElementInfo (String name) {
			this.name = name;
		}
	}
	
	/**	Root element. */
	
	private static ElementInfo root = new ElementInfo("root");
	
	/**	Current element name path. */
	
	private static ArrayList curPath = new ArrayList();
	
	/**	Current path length. */
	
	private static int curPathLen = 0;
	
	/**	SAX parser handler class.
	 */

	private static class Handler extends DefaultHandler {
	
		public void startElement (String uri, String localName, String qName,
			Attributes attributes)
		{
			recordElement(localName);
			curPath.add(localName);
			curPathLen++;
		}
		
		public void endElement (String uri, String localName, String qName) {
			curPathLen--;
			curPath.remove(curPathLen);
		}
		
		public void characters (char ch[], int start, int length) {
			for (int i = start; i < start+length; i++) {
				if (!Character.isWhitespace(ch[i])) {
					recordElement("TEXT");
					return;
				}
			}
		}
		
	}
	
	/**	Records a new element.
	 *
	 *	@param	name		Element name.
	 */
	
	private static void recordElement (String name) {
		ElementInfo parent = root;
		for (int i = 0; i < curPathLen; i++) 
			parent = (ElementInfo)parent.children.get(curPath.get(i));
		ElementInfo child = (ElementInfo)parent.children.get(name);
		if (child != null) return;
		child = new ElementInfo(name);
		child.parent = parent;
		parent.children.put(name, child);
	}
	
	/**	Outputs the report section for a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	level		Node level in tree.
	 */
	
	private static void output (ElementInfo node, int level) {
		if (node.name.equals("TEXT")) return;
		for (int i = 0; i < level; i++) System.out.print("  |");
		System.out.print(node.name);
		if (node.parent != null) {
			ElementInfo ancestor = node.parent;
			String path = "";
			while (ancestor != null) {
				path = ancestor.name + "/" + path;
				ancestor = ancestor.parent;
			}
			System.out.print(" [" + path + "]");
		}
		Set children = node.children.keySet();
		if (children.size() > 0) {
			boolean first = true;
			System.out.print(" (");
			for (Iterator it = children.iterator(); it.hasNext(); ) {
				String name = (String)it.next();
				if (!first) System.out.print(" ");
				System.out.print(name);
				first = false;
			}
			System.out.print(")");
		}
		System.out.println();
		for (Iterator it = children.iterator(); it.hasNext(); ) {
			String name = (String)it.next();
			ElementInfo child = (ElementInfo)node.children.get(name);
			output(child, level+1);
		}
	}
	
	/**	Does it.
	 *
	 *	@param	path		Directory path.
	 *
	 *	@throws	Exception
	 */

	private static void doit (String path)
		throws Exception
	{
		File dir = new File(path);
		String fullPath = dir.getAbsolutePath() + "/";
		XMLReader reader = XMLReaderFactory.createXMLReader(
			"org.apache.xerces.parsers.SAXParser");
		Handler handler = new Handler();
		reader.setContentHandler(handler);
		reader.setErrorHandler(handler);

		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (!file.getName().endsWith(".xml")) continue;
			curPath = new ArrayList();
			curPathLen = 0;
			String absolutePath = file.getAbsolutePath();
			URL url = new URL("file://" + absolutePath);
			InputStream in = url.openStream();
			InputSource source = new InputSource(in);
			source.setSystemId(fullPath);
			reader.parse(source);
			in.close();
		}
		
		output(root, 0);
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */

	public static void main (String[] args) {
		try {
			doit(args[0]);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**	Hides the default no-arg constructor.
	 */
	 
	private CatalogXML () {
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

