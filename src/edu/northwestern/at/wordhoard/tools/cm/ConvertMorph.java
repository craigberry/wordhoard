package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	Converts MorphAdorner files to WordHoard files.
 *
 *	<p>Usage:
 *
 *	<p><code>ConvertMorph in rules data</code>
 *
 *	<p>in = Path to a MorphAdorner XML output file or a directory of such files. If a directory
 *	path is specified, all files in the directory whose names end in ".xml" are processed.
 *
 *	<p>rules = Path to a ConvertMorph XML rules file.
 *
 *	<p>data = Path to WordHoard data directory.
 */

public class ConvertMorph {

	/**	Input file or directory path. */

	private static String inPath;

	/**	Rules file path. */

	private static String rulesPath;

	/**	WordHoard data directory path. */

	private static String dataPath;

	/**	WordHoard data directory. */

	private static File dataDir;

	/**	WordHoard work directory. */

	private static File workDir;

	/**	Rules. */

	private static Rules rules;

	/**	Author processor. */

	private static AuthorProcessor authorProcessor;

	/**	Map from English pos tags to word class tags. */

	private static HashMap posMap = new HashMap();

	/**	Number of works built. */

	private static int numWorks;

	/**	Set of work tags processed. */

	private static HashSet workTags = new HashSet();

	/**	Number of words generated so far. */

	private static int numWordsSoFar = 0;

	/**	Parses command line arguments.
	 *
	 *	@param	args		Command line arguments.
	 */

	private static void parseArgs (String[] args) {
		int n = args.length;
		if (n != 3) {
			System.out.println("Usage: ConvertMorph in rules data");
			System.exit(1);
		}
		inPath = args[0];
		rulesPath = args[1];
		dataPath = args[2];
	}

	/**	Reads the pos to word class map.
	 *
	 *	@throws	Exception
	 */

	private static void readPosMap ()
		throws Exception
	{
		File file = new File(dataDir, "pos.xml");
		Document doc = DOMUtils.parse(file);
		Element el = DOMUtils.getDescendant(doc, "WordHoardPos");
		List list = DOMUtils.getChildren(el, "pos");
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Element posEl = (Element)it.next();
			if (posEl.getAttribute("language").equals("english"))
				posMap.put(posEl.getAttribute("id"),
					posEl.getAttribute("wordClass"));
		}
	}

	/**	Writes the output file.
	 *
	 *	@param	document			DOM tree for parsed XML docuemnt.
	 *
	 *	@param	out					XML output file writer.
	 *
	 *	@param	headerGenerator		Header generator.
	 *
	 *	@param	fullWorkTag			Full work tag for document.
	 *
	 *	@throws Exception
	 */

	private static void writeOutputFile (Document document, XMLWriter out,
		HeaderGenerator headerGenerator, String fullWorkTag)
			throws Exception
	{
		out.startEl("WordHoardText");

		headerGenerator.writeHeaders(out);
		LineGenerator.resetDivCount();

		String textPath = rules.getTextPath();
		Element textEl = DOMUtils.getDescendant(document, textPath);
		if (textEl == null) {
			BuildUtils.emsg("Missing required " + textPath + " element");
		} else {
			WorkPartGenerator textGenerator =
				new WorkPartGenerator(out, rules, posMap, fullWorkTag);
			textGenerator.genText(textEl);
		}

		out.endEl("WordHoardText");
	}

	/**	Converts a file.
	 *
	 *	@param	inFile		MorphAdorner XML input file.
	 *
	 *	@throws	Exception
	 */

	private static void convertFile (File inFile)
		throws Exception
	{
		String fileName = inFile.getName();
		System.out.println();
		System.out.println("Converting " + fileName);
		Document document = DOMUtils.parse(inFile);

		HeaderGenerator headerGenerator = new HeaderGenerator(rules, document, fileName,
			authorProcessor);
		String workTag = headerGenerator.parseHeaders();
		if (workTag == null) {
			BuildUtils.emsg("Missing or invalid work tag - file skipped");
			return;
		}
		if (workTags.contains(workTag)) {
			BuildUtils.emsg("Duplicate work tag " + workTag + " - file skipped");
			return;
		}
		workTags.add(workTag);
		String fullWorkTag = rules.getCorpusTag() + "-" + workTag;
		System.out.println("   Full work tag: " + fullWorkTag);
		File outFile = new File(workDir, workTag + ".xml");
		System.out.println("   Output file: " + outFile.getName());
		XMLWriter out = new XMLWriter(outFile);
		writeOutputFile(document, out, headerGenerator, fullWorkTag);
		out.close();
		numWorks++;
		int numWords = LineGenerator.getNumWords();
		int numWordsThisFile = numWords - numWordsSoFar;
		numWordsSoFar = numWords;
		System.out.println("   " + Formatters.formatIntegerWithCommas(numWordsThisFile) +
			" words generated");
	}

	/**	Converts a directory of works.
	 *
	 *	@param	dir		Directory.
	 *
	 *	@throws Exception
	 */

	private static void convertDir (File dir)
		throws Exception
	{
		File[] contents = dir.listFiles();
		for (int i = 0; i < contents.length; i++) {
			File file = contents[i];
			String name = file.getName().toLowerCase();
			if (!name.endsWith(".xml")) continue;
			convertFile(file);
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

			System.out.println("Building works from " + inPath);
			System.out.println("Writing data to directory " + dataPath);

			rules = new Rules(rulesPath);

			File inFileOrDir = new File(inPath);
			dataDir = new File(dataPath);
			workDir = new File(dataDir, "works/" + rules.getCorpusTag());

			authorProcessor = new AuthorProcessor(dataDir);
			authorProcessor.read();
			readPosMap();

			if (inFileOrDir.isDirectory()) {
				convertDir(inFileOrDir);
			} else {
				convertFile(inFileOrDir);
			}

			//	Write the authors.

			authorProcessor.write();

			//	Report final stats.

			long endTime = System.currentTimeMillis();
			System.out.println();
			System.out.println(Formatters.formatIntegerWithCommas(numWorks) +
				(numWorks == 1 ? " work" : " works") +
				" converted in " +
				BuildUtils.formatElapsedTime(startTime, endTime));
			int numBadContractions = LineGenerator.getNumBadContractions();
			if (numBadContractions > 0)
				System.out.println(numBadContractions +
					(numBadContractions == 1 ? " bad contraction" : " bad contractions"));
			int numWords = LineGenerator.getNumWords();
			System.out.println(Formatters.formatIntegerWithCommas(numWords) +
				" total words generated");
			BuildUtils.reportNumErrors();

		} catch (Exception e) {

			e.printStackTrace();
			System.exit(1);

		}

	}

	/**	Hides the default no-arg constructor.
	 */

	private ConvertMorph () {
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

