package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	Builds the word classes.
 *
 *	<p>Usage:
 *
 *	<p><code>BuildWordClasses in dbname username password</code>
 *
 *	<p>in = Path to the word class definition XML input file.
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */
 
public class BuildWordClasses {
	
	/**	MySQL table exporter/importer for word class objects. */
	
	private static TableExporterImporter wordClassTableExporterImporter;
	
	/**	Set of word class tags. */
	
	private static HashSet tags = new HashSet();
	
	/**	Builds a word class.
	 *
	 *	@param	el		"wordClass" element.
	 *
	 *	@throws Exception
	 */
	 
	private static void buildWordClass (Element el)
		throws Exception
	{
		String tag = el.getAttribute("id");
		if (tag.length() == 0) {
			BuildUtils.emsg(
				"Missing required id attribute on wordClass element");
			return;
		}
		
		if (tags.contains(tag)) {
			BuildUtils.emsg(
				"Duplicate id: " + tag);
			return;
		}
		tags.add(tag);
		
		String majorClass = el.getAttribute("majorClass");
		if (majorClass.length() == 0) {
			BuildUtils.emsg(tag + ": Missing required majorClass attribute");
			return;
		}
		
		String description = DOMUtils.getText(el);
		
		wordClassTableExporterImporter.print(tag);
		wordClassTableExporterImporter.print(description);
		wordClassTableExporterImporter.print(majorClass);
		wordClassTableExporterImporter.println();
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	public static void main (final String args[]) {
	
		try {
		
			long startTime = System.currentTimeMillis();
		
			int numArgs = args.length;
			if (numArgs != 4) {
				System.out.println("Usage: BuildWordClasses in dbname username password");
				System.exit(1);
			}
			String in = args[0];
			String dbname = args[1];
			String username = args[2];
			String password = args[3];
			
			System.out.println("Building word classes from file " + in);
			
			Connection c = BuildUtils.getConnection(dbname, username, password);

			Statement s = c.createStatement();
			int n = s.executeUpdate("delete from wordclass");
			if (n > 0) System.out.println(n +
				(n == 1 ? " word class" : " word classes") + " deleted");
			s.close();
			
			String tempDirPath = BuildUtils.createTempDir() + "/";
				
			wordClassTableExporterImporter =
				new TableExporterImporter("wordclass",
					"tag, description, majorWordClass_majorWordClass",
					tempDirPath + "wordClass.txt",
					false);
			
			Document document = DOMUtils.parse(in);
			
			Element el = DOMUtils.getDescendant(document, 
				"WordHoardWordClasses");
			if (el == null) {
				BuildUtils.emsg(
					"Missing required WordHoardWordClasses element");
				return;
			}
			
			NodeList children = el.getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (!(child instanceof Element)) continue;
				String childName = child.getNodeName();
				Element childEl = (Element)child;
				if (childName.equals("wordClass")) {
					buildWordClass(childEl);
				} else {
					BuildUtils.emsg("Illegal element: " + childName);
				}
			}
			
			wordClassTableExporterImporter.close();
			int ct = wordClassTableExporterImporter.importData(c);
			
			c.close();
			
			BuildUtils.deleteTempDir();
			
			long endTime = System.currentTimeMillis();
			System.out.println(Formatters.formatIntegerWithCommas(ct) +
				(ct == 1 ? " word class" : " word classes") +
				" created in " +
				BuildUtils.formatElapsedTime(startTime, endTime));
			BuildUtils.reportNumErrors();
				
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**	Hides the default no-arg constructor.
	 */
	 
	private BuildWordClasses () {
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

