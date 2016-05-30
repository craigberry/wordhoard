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
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	Builds the authors.
 *
 *	<p>Usage:
 *
 *	<p><code>BuildAuthors in dbname username password</code>
 *
 *	<p>in = Path to the author definition XML input file.
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */
 
public class BuildAuthors {
	
	/**	MySQL table exporter/importer for author objects. */
	
	private static TableExporterImporter authorTableExporterImporter;
	
	/**	Builds an author.
	 *
	 *	@param	el		"author" element.
	 *
	 *	@throws	Exception
	 */
	 
	private static void buildAuthor (Element el) 
		throws Exception
	{
			
		Element nameEl = DOMUtils.getChild(el, "name");
		if (nameEl == null) {
			BuildUtils.emsg("Required name child missing on author element");
			return;
		}
		
		String name = DOMUtils.getText(nameEl);
			
		System.out.println("   " + name);

		Element birthYearEl = DOMUtils.getChild(el, "birthYear");
		Integer birthYear = null;
		if (birthYearEl != null) {
			String birthYearString = DOMUtils.getText(birthYearEl);
			try {
				birthYear = new Integer(birthYearString);
			} catch (NumberFormatException e) {
				BuildUtils.emsg(name + ": Illegal birth year");
				return;
			}
		}

		Element deathYearEl = DOMUtils.getChild(el, "deathYear");
		Integer deathYear = null;
		if (deathYearEl != null) {
			String deathYearString = DOMUtils.getText(deathYearEl);
			try {
				deathYear = new Integer(deathYearString);
			} catch (NumberFormatException e) {
				BuildUtils.emsg(name + ": Illegal death year");
				return;
			}
		}

		Element earliestWorkYearEl = DOMUtils.getChild(el, "earliestWorkYear");
		Integer earliestWorkYear = null;
		if (earliestWorkYearEl != null) {
			String earliestWorkYearString = DOMUtils.getText(earliestWorkYearEl);
			try {
				earliestWorkYear = new Integer(earliestWorkYearString);
			} catch (NumberFormatException e) {
				BuildUtils.emsg(name + ": Illegal earliest work year");
				return;
			}
		}

		Element latestWorkYearEl = DOMUtils.getChild(el, "latestWorkYear");
		Integer latestWorkYear = null;
		if (latestWorkYearEl != null) {
			String latestWorkYearString = DOMUtils.getText(latestWorkYearEl);
			try {
				latestWorkYear = new Integer(latestWorkYearString);
			} catch (NumberFormatException e) {
				BuildUtils.emsg(name + ": Illegal latest work year");
				return;
			}
		}
		
		authorTableExporterImporter.print(name);
		authorTableExporterImporter.print(TextParams.ROMAN);
		authorTableExporterImporter.print(birthYear);
		authorTableExporterImporter.print(deathYear);
		authorTableExporterImporter.print(earliestWorkYear);
		authorTableExporterImporter.print(latestWorkYear);
		authorTableExporterImporter.println();
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
				System.out.println("Usage: BuildAuthors in dbname username password");
				System.exit(1);
			}
			String in = args[0];
			String dbname = args[1];
			String username = args[2];
			String password = args[3];
			
			System.out.println("Building authors from file " + in);
			
			Connection c = BuildUtils.getConnection(dbname, username, password);
				
			Statement s = c.createStatement();
			int n = s.executeUpdate("delete from author");
			if (n > 0) System.out.println(n +
				(n == 1 ? " author" : " authors") + " deleted");
			s.close();
			
			String tempDirPath = BuildUtils.createTempDir() + "/";
			
			authorTableExporterImporter =
				new TableExporterImporter("author",
					"name_string, name_charset, " +
					"birthYear, deathYear, " +
					"earliestWorkYear, latestWorkYear",
					tempDirPath + "author.txt",
					false);
			
			Document document = DOMUtils.parse(in);
			
			Element el = DOMUtils.getDescendant(document, "WordHoardAuthors");
			if (el == null) {
				BuildUtils.emsg("Missing required WordHoardAuthors element");
				return;
			}
			
			NodeList children = el.getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (!(child instanceof Element)) continue;
				String childName = child.getNodeName();
				Element childEl = (Element)child;
				if (childName.equals("author")) {
					buildAuthor(childEl);
				} else {
					BuildUtils.emsg("Illegal element: " + childName);
				}
			}
			
			authorTableExporterImporter.close();
			int ct = authorTableExporterImporter.importData(c);
			
			c.close();
			
			BuildUtils.deleteTempDir();
			
			long endTime = System.currentTimeMillis();
			System.out.println(Formatters.formatIntegerWithCommas(ct) +
				(ct == 1 ? " author" : " authors") +
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
	 
	private BuildAuthors () {
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

