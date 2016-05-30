package edu.northwestern.at.wordhoard.tools.martin;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

import org.w3c.dom.*;

import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.wordhoard.tools.PrettyPrint;

/**	Updates the work files.
 *
 *	<p>Usage:
 *
 *	<p><code>UpdateWorkFiles in</code>
 *
 *	<p>in = Path to a work definition XML input file, or a path to a directory 
 *	of such files. If a directory path is specified, all files in the file 
 *	system tree rooted at the directory whose names end in ".xml" are processed.
 *	The lemma and part of speech tags are updated from Martin's database tables.
 *
 *	<p>The file "misc/martin.properties" specifies the parameters for Martin's 
 *	database.
 *
 *	<p>If there is no data for a work in Martin's database tables, the work XML
 *	file is unchanged and an "Unknown work" error message is issued.
 */
 
public class UpdateWorkFiles {
	
	/**	Input file or directory path. */
	
	private static String inPath;
	
	/**	Martin data provider. */
	
	private static MartinProvider provider;
	
	/**	Parses command line arguments.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	private static void parseArgs (String[] args) {
		int n = args.length;
		if (n != 1) {
			System.out.println("Usage: UpdateWorkFiles in");
			System.exit(1);
		}
		inPath = args[0];
	}
	
	/**	Copies a file.
	 *
	 *	@param	inFile		Input file.
	 *
	 *	@param	outFile		Output file.
	 *
	 *	@throws Exception
	 */
	 
	private static void copyFile (File inFile, File outFile) 
		throws Exception
	{
		FileInputStream fis = new FileInputStream(inFile);
		InputStreamReader isr = new InputStreamReader(fis, "utf-8");
		BufferedReader in = new BufferedReader(isr);
		
		FileOutputStream fos = new FileOutputStream(outFile);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter bw = new BufferedWriter(osw);
		PrintWriter out = new PrintWriter(bw);
		
		String line = in.readLine();
		while (line != null) {
			out.println(line);
			line = in.readLine();
		}
			
		in.close();
		out.close();
	}
	
	/**	Updates a work.
	 *
	 *	@param	file		XML file for work.
	 *
	 *	@throws	Exception
	 */
	 
	private static void updateWork (File file)
		throws Exception
	{
		String path = file.getPath();
		System.out.println("Updating " + path);
		
		Document document = DOMUtils.parse(file);
		
		Element headerEl = DOMUtils.getDescendant(document,
			"WordHoardText/wordHoardHeader");
		if (headerEl == null) {
			MartinUtils.emsg("Missing element WordHoardText/wordHoardHeader");
			return;
		}
		String corpusTag = headerEl.getAttribute("corpus");
		String workTag = headerEl.getAttribute("work");
		
		boolean ok = provider.setWork(corpusTag, workTag);
		if (!ok) return;
		
		File tempFile = File.createTempFile("UpdateWorkFiles", "xml");
		PrettyPrint.prettyPrint(document, tempFile, provider);
		copyFile(tempFile, file);
		tempFile.delete();
	}
	
	/**	Updates a directory of works.
	 *
	 *	@param	dir		Directory.
	 *
	 *	@throws Exception
	 */
	 
	private static void updateDir (File dir) 
		throws Exception
	{
		File[] contents = dir.listFiles();
		for (int i = 0; i < contents.length; i++) {
			File file = contents[i];
			if (file.isDirectory()) {
				updateDir(file);
			} else if (file.getName().endsWith(".xml")) {
				updateWork(file);
			}
		}
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	public static void main (final String args[]) {
	
		try {
		
			//	Initialize.
		
			parseArgs(args);
			
			File file = new File(inPath);
			boolean isDir = file.isDirectory();
			if (isDir) {
				System.out.println("Updating works in directory " + inPath);
			} else {
				System.out.println("Updating work in file " + inPath);
			}
			
			//	Get Martin's data.
			
			System.out.println("Reading Martin's data");
			Connection martinConnection = MartinUtils.getConnection();
			provider = new MartinProvider(martinConnection);
			
			//	Update the file or directory.
				
			if (isDir) {
				updateDir(file);
			} else {
				updateWork(file);
			}
			
			//	Finish up.
			
			martinConnection.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	
	}

	/**	Hides the default no-arg constructor.
	 */
	 
	private UpdateWorkFiles () {
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

