package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.northwestern.at.utils.xml.*;

/**	Clones the WordHaord raw data XML files.
 *
 *	<p>This tool is used to make many copies of the WordHoard XML data
 *	to test scaling issues.
 *
 *	<p>Usage: 
 *
 *	<p><code>CloneData inDir outDir nCopies</code>
 *
 *	<p>inDir = Input XML directory path.
 *
 *	<p>outDir = output XML directory path.
 *
 *	<p>nCopies = number of copies to clone.
 *
 *	<p>Before running this tool, the output directory must exist, and
 *	it must contain copies of the "authors.xml" file, the "word-classes.xml"
 *	file, and the "pos" directory.
 *
 *	<p>The tool reads the input "corpora.xml" file and writes the output
 *	"corpora.xml" file containing the requested number of copies of all the
 *	corpora. For example, the "Shakespeare" corpus is cloned as 
 *	"Shakespeare 1", "Shakespeare 2", etc.
 *
 *	<p>The tool reads the input "works" directory and writes the output
 *	"works" directory containing the requested number of copies of all the
 *	works.
 *
 *	<p>The output directory of XML files contains no annotations, spellings,
 *	translations, or Benson glosses.
 *
 *	<p>Tags (ids) for cloned objects are constructed by appending the copy
 *	number. E.g., the tags for the copies of <i>Hamlet</i> are ham-1, ham-2, 
 *	etc. 
 */
 
public class CloneData {

	/**	Input XML directory. */
	
	private static File inDir;
	
	/**	Output XML directory. */
	
	private static File outDir;
	
	/**	Number of copies. */
	
	private static int nCopies;
	
	/**	Input XML document tree. */
	
	private static Document doc;
	
	/**	Output XML file. */
	
	private static PrintWriter out;
	
	/**	Parses the command line arguments.
	 *
	 *	@param	args		Command line arguments.
	 *
	 *	@throws	Exception
	 */
	 
	private static void parseArgs (String args[])
		throws Exception
	{
		if (args.length != 3) {
			System.out.println("Usage: CloneData inDir outDir nCopies");
			System.exit(1);
		}
		inDir = new File(args[0]);
		outDir = new File(args[1]);
		nCopies = Integer.parseInt(args[2]);
	}
	
	/**	Clones one corpus.
	 *
	 *	@param	el		"Corpus" element from input document.
	 *
	 *	@param	k		Copy number.
	 *
	 *	@throws	Exception
	 */
	 
	private static void cloneOneCorpus (Element el, int k)
		throws Exception
	{
		String id = el.getAttribute("id");
		String charset = el.getAttribute("charset");
		String posType = el.getAttribute("posType");
		id = id + "-" + k;
		out.println("\t<corpus id=\"" + id + "\"" +
			" charset=\"" + charset + "\"" +
			" posType=\"" + posType + "\">");
			
		Element titleEl = DOMUtils.getChild(el, "title");
		String title = DOMUtils.getText(titleEl);
		title = title + " " + k;
		out.println("\t\t<title>" + title + "</title>");
		
		out.println("\t\t<taggingData>");
		Element taggingDataEl = DOMUtils.getChild(el, "taggingData");
		NodeList children = taggingDataEl.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (!(child instanceof Element)) continue;
			String name = child.getNodeName();
			out.println("\t\t\t<" + name + "/>");
		}
		out.println("\t\t</taggingData>");
		
		ArrayList tconViewEls = DOMUtils.getChildren(el, "tconview");
		for (Iterator it = tconViewEls.iterator(); it.hasNext(); ) {
			Element tconViewEl = (Element)it.next();
			String type = tconViewEl.getAttribute("type");
			title = tconViewEl.getAttribute("title");
			out.print("\t\t<tconView type=\"" + type + "\"");
			if (title.length() > 0) 
				out.print(" title=\"" + title + "\"");
			out.println(">");
			if (type.equals("list")) {
				ArrayList workEls = DOMUtils.getChildren(tconViewEl, "work");
				for (Iterator it2 = workEls.iterator(); it2.hasNext(); ) {
					Element workEl = (Element)it2.next();
					id = workEl.getAttribute("id");
					id = id + "-" + k;
					out.println("\t\t\t<work id=\"" + id + "\"/>");
				}
			} else if (type.equals("category")) {
				ArrayList catEls = DOMUtils.getChildren(tconViewEl, "category");
				for (Iterator it2 = catEls.iterator(); it2.hasNext(); ) {
					Element catEl = (Element)it2.next();
					title = catEl.getAttribute("title");
					out.println("\t\t\t<category title=\"" + title + "\"/>");
					ArrayList workEls = DOMUtils.getChildren(catEl, "work");
					for (Iterator it3 = workEls.iterator(); it3.hasNext(); ) {
						Element workEl = (Element)it3.next();
						id = workEl.getAttribute("id");
						id = id + "-" + k;
						out.println("\t\t\t\t<work id=\"" + id + "\"/>");
					}
					
					out.println("\t\t\t</category>");
				}
			}
			out.println("\t\t</tconview>");
		}
			
		out.println("\t</corpus>");
	}
	
	/**	Makes one copy of the corpora.
	 *
	 *	@param	k		Copy number.
	 *
	 *	@throws	Exception
	 */
	 
	private static void cloneOneCorpora (int k)
		throws Exception
	{
		Element el = DOMUtils.getDescendant(doc, "WordHoardCorpora");
		ArrayList corpusEls = DOMUtils.getChildren(el, "corpus");
		for (Iterator it = corpusEls.iterator(); it.hasNext(); ) {
			Element corpusEl = (Element)it.next();
			cloneOneCorpus((Element)corpusEl, k);
		}
	}
	
	/**	Clones the corpora.
	 *
	 *	@throws Exception
	 */
	 
	private static void cloneCorpora ()
		throws Exception
	{
		//	Read and parse the input file.
	
		File inFile = new File(inDir, "corpora.xml");
		System.out.println("Cloning " + inFile);
		doc = DOMUtils.parse(inFile);
		
		//	Open the output file.
		
		File outFile = new File(outDir, "corpora.xml");
		FileOutputStream outStream = new FileOutputStream(outFile);
		OutputStreamWriter outWriter = new OutputStreamWriter(outStream, "utf-8");
		BufferedWriter outBuffered = new BufferedWriter(outWriter);
		out = new PrintWriter(outBuffered);
		
		//	Print the first two lines of the XML output file.
		
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		out.println("<WordHoardCorpora>");
		
		//	Clone the corpora XML statements.
		
		for (int k = 1; k <= nCopies; k++) {
			cloneOneCorpora(k);
		}
		
		//	Print the last line of the XML output file and close it. 
		
		out.println("</WordHoardCorpora>");
		out.close();
	}
	
	/**	Deletes a directory and its contents.
	 *
	 *	@param	dir		Directory.
	 *
	 *	@throws	Exception
	 */
	 
	private static void deleteDir (File dir)
		throws Exception
	{
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				deleteDir(file);
			} else {
				file.delete();
			}
		}
		dir.delete();
	}
	
	/**	Clones one work.
	 *
	 *	@param	outFile		Output file.
	 *
	 *	@param	k			Copy number.
	 *
	 *	@throws Exception
	 */
	 
	private static void cloneOneWork (File outFile, int k)
		throws Exception
	{
		System.out.print(" " + k);
	}
	
	/**	Clones the works.
	 *
	 *	@throws Exception
	 */
	 
	private static void cloneWorks ()
		throws Exception
	{
		File inWorksDir = new File(inDir, "works");
		File outWorksDir = new File(outDir, "works");
		deleteDir(outWorksDir);
		outWorksDir.mkdir();
		File[] inSubDirs = inWorksDir.listFiles();
		for (int i = 0; i < inSubDirs.length; i++) {
			File inSubDir = inSubDirs[i];
			if (!inSubDir.isDirectory()) continue;
			String inSubDirName = inSubDir.getName();
			for (int k = 1; k <= nCopies; k++) {
				String outSubDirName = inSubDirName + "-" + k;
				File outSubDir = new File(outWorksDir, outSubDirName);
				outSubDir.mkdir();
			}
			File[] inWorkFiles = inSubDir.listFiles();
			for (int j = 0; j < inWorkFiles.length; j++) {
				File inWorkFile = inWorkFiles[j];
				String inWorkFileName = inWorkFile.getName();
				if (!inWorkFileName.endsWith(".xml")) continue;
				int inWorkFileNameLen = inWorkFileName.length();
				String inWorkFileNamePrefix = inWorkFileName.substring(0, 
					inWorkFileNameLen-4);
				System.out.print("Cloning " + inWorkFile);
				doc = DOMUtils.parse(inWorkFile);
				for (int k = 1; k <= nCopies; k++) {
					String outSubDirName = inSubDirName + "-" + k;
					File outSubDir = new File(outWorksDir, outSubDirName);
					String outWorkFileName = inWorkFileNamePrefix + "-" + k +
						".xml";
					File outWorkFile = new File(outSubDir, outWorkFileName);
					cloneOneWork(outWorkFile, k);
				}
				System.out.println();
			}
		}
	}
 
	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	public static void main (String args[]) {
		try {
			
			parseArgs(args);
			cloneCorpora();
			cloneWorks();
			
		} catch (Exception e) {
		
			e.printStackTrace();
			System.exit(1);
			
		}
	}

	/**	Hides the default no-arg constructor.
	 */
	 
	private CloneData () {
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

