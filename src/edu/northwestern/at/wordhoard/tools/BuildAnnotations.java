package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.annotations.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

/**	Builds annotations.
 *
 *	<p>Usage:
 *
 *	<p><code>BuildAnnotations in dbname username password</code>
 *
 *	<p>in = Path to an annotation definition XML input file, or a path to a 
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

public class BuildAnnotations {
	
	/**	Input file or directory path. */
	
	private static String inPath;
	
	/**	Database name. */
	
	private static String dbname;
	
	/**	MySQL usename. */
	
	private static String username;
	
	/**	MySQL password. */
	
	private static String password;
	
	/**	Total number of annotations created. */
	
	private static int totalAnnotations;
	
	/**	DOM tree for parsed XML document. */
	
	private static Document document;

	/**	Persistence manager for static object model. */
	
	private static PersistenceManager pm;
	
	/**	Corpus. */
	
	private static Corpus corpus;
	
	/**	Work. */
	
	private static Work work;
	
	/**	Annotation category. */
	
	private static AnnotationCategory category;
	
	/**	Map from line tags to lines for work. */
	
	private static HashMap lineMap;
	
	/**	Map from word tags to words for work. */
	
	private static HashMap wordMap;
	
	/**	Parses command line arguments.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	private static void parseArgs (String[] args) {
		int n = args.length;
		if (n != 4) {
			System.out.println("Usage: BuildAnnotations in dbname username password");
			System.exit(1);
		}
		inPath = args[0];
		dbname = args[1];
		username = args[2];
		password = args[3];
	}
	
	/**	Loads the line map.
	 *
	 *	@throws	Exception
	 */
	 
	private static void loadLineMap ()
		throws Exception
	{
		if (lineMap != null) return;
		System.out.println("Loading line map");
		Collection lines = pm.getLinesInWork(work);
		lineMap = new HashMap();
		for (Iterator it = lines.iterator(); it.hasNext(); ) {
			Line line = (Line)it.next();
			lineMap.put(line.getTag(), line);
		}
	}
	
	/**	Loads the word map.
	 *
	 *	@throws	Exception
	 */
	 
	private static void loadWordMap ()
		throws Exception
	{
		if (wordMap != null) return;
		System.out.println("Loading word map");
		Collection words = pm.getWordsInWork(work);
		wordMap = new HashMap();
		for (Iterator it = words.iterator(); it.hasNext(); ) {
			Word word = (Word)it.next();
			wordMap.put(word.getTag(), word);
		}
	}
	
	/**	A work part and location. */
	
	private static class LocationInfo {
		private WorkPart workPart;
		private TextLocation location;
	}
	
	/**	Gets a start or end location.
	 *
	 *	@param	el		"start" or "end" element.
	 *
	 *	@return			Work part and location, or null if error.
	 *
	 *	@throws	Exception
	 */
	 
	private static LocationInfo getLocation (Element el) 
		throws Exception
	{
		String name = el.getNodeName();
		boolean start = name.equals("start");
		if (el == null) {
			BuildUtils.emsg("Missing required " + name + " element");
			return null;
		}
		String lineTag = el.getAttribute("line");
		String wordTag = el.getAttribute("word");
		String offsetStr = el.getAttribute("offset");
		LocationInfo result = new LocationInfo();
		if (lineTag.length() > 0) {
			loadLineMap();
			Line line = (Line)lineMap.get(lineTag);
			if (line == null) {
				BuildUtils.emsg("There is no line with tag " + lineTag);
				return null;
			}
			result.workPart = line.getWorkPart();
			result.location = start ? line.getLocation().getStart() :
				line.getLocation().getEnd();
		} else if (wordTag.length() > 0) {
			loadWordMap();
			Word word = (Word)wordMap.get(wordTag);
			if (word == null) {
				BuildUtils.emsg("There is no word with tag " + wordTag);
				return null;
			}
			result.workPart = word.getWorkPart();
			result.location = start ? word.getLocation().getStart() :
				word.getLocation().getEnd();
		}
		if (result.location == null) {
			BuildUtils.emsg("Missing required line or word attribute");
			return null;
		}
		if (offsetStr.length() > 0) {
			int offset = 0;
			try {
				offset = Integer.parseInt(offsetStr);
			} catch (NumberFormatException e) {
				BuildUtils.emsg("Invalid offset attribute: " + offsetStr);
				return null;
			}
			result.location.setOffset(result.location.getOffset() + offset);
		}
		return result;
	}
	
	/**	Builds an annotation.
	 *
	 *	@param	el 		"annotation" element.
	 *
	 *	@return			The annotation object, or null if error.
	 *
	 *	@throws Exception
	 */
	
	private static Annotation buildAnnotation (Element el) 
		throws Exception
	{
		LocationInfo startLocation = getLocation(
			DOMUtils.getChild(el, "start"));
		if (startLocation == null) return null;
		LocationInfo endLocation = getLocation(
			DOMUtils.getChild(el, "end"));
		if (endLocation == null) return null;
		TextRange target = new TextRange(startLocation.location, 
			endLocation.location);
		WorkPart workPart = startLocation.workPart;
		
		Text text = new Text();
		text.setCollapseBlankLines(false);
		NodeList children = el.getChildNodes();
		byte charset = corpus.getCharset();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element childEl = (Element)child;
				String name = childEl.getNodeName();
				if (name.equals("p")) {
					TextLine textLine = new BuildParagraph(childEl,
						charset, TextParams.ANNOTATION_FONT_SIZE);
					text.appendLine(textLine);
				}
			}
		}
		text.finalize();
		
		TextAnnotation annotation = new TextAnnotation();
		annotation.setCategory(category);
		annotation.setText(new TextWrapper(text));
		annotation.setWorkPart(workPart);
		annotation.setTarget(target);
		
		return annotation;
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
	
		long startTime = System.currentTimeMillis();

		System.out.println();
		System.out.println("Building annotations from file " +
			file.getPath());
		
		document = DOMUtils.parse(file);
		pm = new PersistenceManager();
		int numAnnotations = 0;

		Element rootEl = DOMUtils.getChild(document, "WordHoardAnnotations");
		if (rootEl == null) {
			BuildUtils.emsg("Missing required WordHoardAnnotations element");
			return;
		}
		String corpusTag = rootEl.getAttribute("corpus");
		if (corpusTag.length() == 0) {
			BuildUtils.emsg("Missing required corpus attribute on " +
				"WordHoardAnnotations element");
			return;
		}
		String workTag = rootEl.getAttribute("work");
		if (workTag.length() == 0) {
			BuildUtils.emsg("Missing required work attribute on " +
				"WordHoardAnnotations element");
			return;
		}
		String categoryName = rootEl.getAttribute("category");
		if (categoryName.length() == 0) {
			BuildUtils.emsg("Missing required category attribute on " +
				"WordHoardAnnotations element");
			return;
		}
		
		corpus = pm.getCorpusByTag(corpusTag);
		if (corpus == null) {
			BuildUtils.emsg("No such corpus: " + corpusTag);
			return;
		}
		
		work = corpus.getWorkByTag(corpusTag + "-" + workTag);
		if (work == null) {
			BuildUtils.emsg("No such work: " + workTag);
			return;
		}
		
		lineMap = null;
		wordMap = null;
				
		category = pm.getAnnotationCategoryByName(categoryName);
		if (category == null) {
			category = new AnnotationCategory();
			category.setName(categoryName);
			pm.save(category);
		}
		
		ArrayList annotationList = new ArrayList();
		ArrayList wrapperList = new ArrayList();
		
		NodeList children = rootEl.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element el = (Element)child;
				String name = el.getNodeName();
				if (name.equals("annotation")) {
					Annotation annotation = buildAnnotation(el);
					if (annotation != null) {
						annotationList.add(annotation);
						wrapperList.add(annotation.getText());
						numAnnotations++;
					}
				} else {
					BuildUtils.emsg("Child element " +
						name + " ignored");
				}
			}
		}
		
		pm.save(wrapperList);
		pm.save(annotationList);
				
		totalAnnotations += numAnnotations;
		long endTime = System.currentTimeMillis();
		System.out.println(
			Formatters.formatIntegerWithCommas(numAnnotations) + 
			(numAnnotations == 1 ? " annotation" : " annotations") +
			" created in " +
			BuildUtils.formatElapsedTime(startTime, endTime));
			
		pm.close();
	}

	/**	Builds a directory of annotations.
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
				System.out.println("Building annotations from directory " + 
					inPath);
			} else {
				System.out.println("Building annotations from file " + 
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
			System.out.println(
				Formatters.formatIntegerWithCommas(totalAnnotations) + 
				" total" +
				(totalAnnotations == 1 ? " annotation" : " annotations") +
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
	 
	private BuildAnnotations () {
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

