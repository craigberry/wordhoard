package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

/**	CalculateCounts pre-processor.
 *
 *	<p>This tool reads a tab-delimited text file which contains work part ids
 *	in the first field on each line. It copies the input file to an output
 *	file, replacing each work part id field with three fields containing the
 *	corpus id, work id, and work part id in that order.
 *
 *	<p>Usage:
 *
 *	<p><code>CalculateCountsPreProcesor dbname username password inPath outPath</code>
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 *
 *	<p>inPath = Path to input file.
 *
 *	<p>outPath = Path to output file.
 */

public class CalculateCountsPreProcessor {

	/**	Map from work part ids to work ids. */

	private static HashMap workMap = new HashMap();

	/**	Map from work ids to corpus ids. */

	private static HashMap corpusMap = new HashMap();

	/**	Reads the work parts from the database.
	 *
	 *	@throws	Exception
	 */

	private static void readWorkParts (String dbName, String username, String password)
		throws Exception
	{
		Connection c = BuildUtils.getConnection(dbName, username, password);
		Statement s = c.createStatement();
		ResultSet r = s.executeQuery(
			"select id, work, corpus from workpart");
		while (r.next()) {
			Long id = (Long)r.getObject(1);
			Long workId = (Long)r.getObject(2);
			Long corpusId = (Long)r.getObject(3);
			workMap.put(id, workId);
			if (corpusId != null) corpusMap.put(id, corpusId);
		}
		c.close();
	}

	/**	Preprocesses a file.
	 *
	 *	@param	inPath		Path to input file.
	 *
	 *	@param	outPath		Path to output file.
	 *
	 *	@throws Exception
	 */

	private static void preProcessFile (String inPath, String outPath)
		throws Exception
	{
		File inFile = new File(inPath);
		FileInputStream inStream = new FileInputStream(inFile);
		InputStreamReader inReader = new InputStreamReader(inStream, "utf-8");
		BufferedReader in = new BufferedReader(inReader);

		File outFile = new File(outPath);
		FileOutputStream outStream = new FileOutputStream(outFile);
		OutputStreamWriter outWriter = new OutputStreamWriter(outStream, "utf-8");
		BufferedWriter outBuffered = new BufferedWriter(outWriter);
		PrintWriter out = new PrintWriter(outBuffered);

		String line;
		int lineNum = 0;
		while ((line = in.readLine()) != null) {
			lineNum++;
			int pos = line.indexOf('\t');
			String partIdStr = line.substring(0, pos);
			Long partId = new Long(partIdStr);
			Long workId = (Long)workMap.get(partId);
			if (workId == null) {
				System.out.println("Bad work id at line " + lineNum);
				System.exit(1);
			}
			Long corpusId = (Long)corpusMap.get(workId);
			if (corpusId == null) {
				System.out.println("#### " +
					"Bad corpus id at line " + lineNum);
				System.exit(1);
			}
			out.println(corpusId + "\t" + workId + "\t" + line);
		}
		in.close();
		out.close();
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */

	public static void main  (final String args[]) {

		try {

			if (args.length != 5) {
				System.out.println(
					"Usage: CalculateCountsPreProcessor dbname username password inPath outPath");
				System.exit(1);
			}
			readWorkParts(args[0], args[1], args[2]);
			preProcessFile(args[3], args[4]);

		} catch (Exception e) {

			e.printStackTrace();
			System.exit(1);

		}

	}

	/**	Hides the default no-arg constructor.
	 */

	private CalculateCountsPreProcessor () {
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

