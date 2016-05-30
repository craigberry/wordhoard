package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	Builds translations.
 *
 *	<p>Usage:
 *
 *	<p><code>BuildTranslations in dbname username password</code>
 *
 *	<p>in = Path to a translation definition XML input file, or a path to a 
 *	directory of such files. If a directory path is specified, all files in 
 *	the file system tree rooted at the directory whose names end in ".xml" 
 *	are processed.
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */

public class BuildTranslations {
	
	/**	Input file or directory path. */
	
	private static String inPath;
	
	/**	Database name. */
	
	private static String dbname;
	
	/**	MySQL usename. */
	
	private static String username;
	
	/**	MySQL password. */
	
	private static String password;
	
	/**	Number of files built. */
	
	private static int numFiles;
	
	/**	DOM tree for parsed XML document. */
	
	private static Document document;
	
	/**	Persistence manager. */
	
	private static PersistenceManager pm;
	
	/**	Corpus tag. */
	
	private static String corpusTag;
	
	/**	Work tag. */
	
	private static String workTag;
	
	/**	Translation tag. */
	
	private static String translationTag;
	
	/**	Map from line tags to p elements. */
	
	private static HashMap lineTagToElMap;
	
	/**	Set of all line tags found. */
	
	private static HashSet lineTagsFound;
	
	/**	Parses command line arguments.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	private static void parseArgs (String[] args) {
		int n = args.length;
		if (n != 4) {
			System.out.println("Usage: BuildTranslations in dbname username password");
			System.exit(1);
		}
		inPath = args[0];
		dbname = args[1];
		username = args[2];
		password = args[3];
	}
	
	/**	Processes a work part.
	 *
	 *	@param	part		Work part.
	 *
	 *	@throws Exception
	 */
	 
	private static void processPart (WorkPart part) 
		throws Exception
	{
		TextWrapper textWrapper = part.getPrimaryText();
		if (textWrapper == null) return;
		Text primary = textWrapper.getText();
		int n = primary.getNumLines();
		TextLine[] primaryLines = primary.getLines();
		TextLine[] translationLines = new TextLine[n];
		Collection lines = pm.getLinesInWorkPart(part);
		int numTranslatedLines = 0;
		for (Iterator it = lines.iterator(); it.hasNext(); ) {
			Line line = (Line)it.next();
			TextRange location = line.getLocation();
			if (location == null) continue;
			TextLocation start = location.getStart();
			if (start == null) continue;
			TextLocation end = location.getEnd();
			if (end == null) continue;
			int startIndex = start.getIndex();
			int endIndex = end.getIndex();
			String lineTag = line.getTag();
			if (lineTag == null) continue;
			Element el = (Element)lineTagToElMap.get(lineTag);
			if (el == null) continue;
			lineTagsFound.add(lineTag);
			TextLine translationLine = new BuildParagraph(el, 
				TextParams.ROMAN,
				TextParams.TRANSLATED_LINE_FONT_SIZE);
			TextLine primaryLine = primaryLines[endIndex];
			int primaryIndentation = primaryLine.getIndentation();
			translationLine.setIndentation(primaryIndentation +
				TextParams.TRANSLATION_INDENTATION);
			translationLines[endIndex] = translationLine;
			numTranslatedLines++;
		}
		if (numTranslatedLines == 0) return;
		Text translation = new Text();
		translation.setCollapseBlankLines(false);
		for (int i = 0; i < n; i++) {
			TextLine translationLine = translationLines[i];
			if (translationLine == null) {
				translation.appendBlankLine();
			} else {
				translation.appendLine(translationLine);
			}
		}
		translation.finalize();
		pm.begin();
		TextWrapper translationWrapper = new TextWrapper(translation);
		pm.save(translationWrapper);
		part.addTranslation(translationTag, translationWrapper);
		pm.commit();
	}
	
	/**	Builds a file.
	 *
	 *	@param	file		XML file for translation.
	 *
	 *	@throws	Exception
	 */
	 
	private static void buildFile (File file) 
		throws Exception
	{
		//	Initialize.
		
		document = DOMUtils.parse(file);
		pm = new PersistenceManager();

		//	Get the corpus, work, and translation tags.
		
		Element rootEl = DOMUtils.getChild(document, "WordHoardTranslation");
		if (rootEl == null) {
			BuildUtils.emsg(file.getPath() + ": " +
				"Missing required WordHoardTranslation element");
			return;
		}
		corpusTag = rootEl.getAttribute("corpus");
		if (corpusTag.length() == 0) {
			BuildUtils.emsg(file.getPath() + ": " +
				"Missing required corpus attribute in " +
				"WordHoardTranslation element");
			return;
		}
		workTag = rootEl.getAttribute("work");
		if (workTag.length() == 0) {
			BuildUtils.emsg(file.getPath() + ": " +
				"Missing required work attribute in " +
				"WordHoardTranslation element");
			return;
		}
		translationTag = rootEl.getAttribute("type");
		if (translationTag.length() == 0) {
			BuildUtils.emsg(file.getPath() + ": " +
				"Missing required type attribute in " +
				"WordHoardTranslation element");
			return;
		}
		String fullTag = corpusTag + "-" + workTag;
		System.out.println("Building " + fullTag +
			" " + translationTag + " translation from file " + 
			file.getPath());
		Corpus corpus = pm.getCorpusByTag(corpusTag);
		if (corpus == null) {
			BuildUtils.emsg("No such corpus: " + corpusTag);
			return;
		}
		Work work = corpus.getWorkByTag(fullTag);
		if (work == null) {
			BuildUtils.emsg("No such work: " + fullTag);
			return;
		}
			
		//	Build the map from line tags to p elements. 
		
		lineTagToElMap = new HashMap();
		NodeList children = rootEl.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element el = (Element)child;
				String name = el.getNodeName();
				if (name.equals("p")) {
					String lineTag = el.getAttribute("id");
					if (lineTag.length() == 0) {
						BuildUtils.emsg("Missing required id attribute on " +
							"p element");
						continue;
					}
					lineTagToElMap.put(lineTag, el);
				} else {
					BuildUtils.emsg("Child element " +
						name + " ignored");
				}
			}
		}
		
		//	Process the work parts.
		
		lineTagsFound = new HashSet();
		List parts = work.getPartsWithText();
		for (Iterator it = parts.iterator(); it.hasNext(); ) {
			WorkPart part = (WorkPart)it.next();
			processPart(part);
		}
		
		//	Check for bad line tags in the XML file.
		
		for (Iterator it = lineTagToElMap.keySet().iterator(); it.hasNext(); ) {
			String lineTag = (String)it.next();
			if (!lineTagsFound.contains(lineTag)) {
				BuildUtils.emsg("Bad line id skipped: " + lineTag);
			}
		}
		
			
		//	Finish up.
			
		numFiles++;

	}
	
	/**	Builds a directory of translations.
	 *
	 *	@param	dir		Directory.
	 *
	 *	@throws Exception
	 */
	 
	private static void buildDir (File dir) 
		throws Exception
	{
		File[] contents = dir.listFiles();
		for (int i = 0; i < contents.length; i++) {
			File file = contents[i];
			if (file.isDirectory()) {
				buildDir(file);
			} else if (file.getName().endsWith(".xml")) {
				buildFile(file);
			}
		}
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	public static void main  (final String args[]) {
	
		try {
		
			//	Initialize.
		
			long startTime = System.currentTimeMillis();
			parseArgs(args);
			
			File file = new File(inPath);
			boolean isDir = file.isDirectory();
			if (isDir) {
				System.out.println("Building translations from directory " + 
					inPath);
			} else {
				System.out.println("Building translation from file " + 
					inPath);
			}
			
			BuildUtils.initHibernate(dbname, username, password);
				
			//	Build file or directory.
				
			if (isDir) {
				buildDir(file);
			} else {
				buildFile(file);
			}
			
			//	Report final stats.
			
			long endTime = System.currentTimeMillis();
			System.out.println();
			System.out.println(Formatters.formatIntegerWithCommas(numFiles) + 
				(numFiles == 1 ? " translation" : " translations") +
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
	 
	private BuildTranslations () {
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

