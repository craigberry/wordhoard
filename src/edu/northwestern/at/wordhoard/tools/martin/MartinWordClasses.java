package edu.northwestern.at.wordhoard.tools.martin;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.sql.*;

/**	Builds the word classes.
 *
 *	<p>Usage:
 *
 *	<p><code>MartinWordClasses out</code>
 *
 *	<p>out = Path to word classes XML output file.
 *
 *	<p>The file "misc/martin.properties" specifies the parameters for Martin's 
 *	database.
 */
 
public class MartinWordClasses {
	
	/**	Connection to Martin's database. */
	
	private static Connection martinConnection;
	
	/**	Output file. */
	
	private static PrintWriter out;
	
	/**	Number of word classes. */
	
	private static int numWordClasses;
	
	/**	Writes the word classes file.
	 *
	 *	@throws	Exception
	 */
	 
	private static void writeFile ()
		throws Exception
	{
		PreparedStatement p = martinConnection.prepareStatement(
			"select ABBRWORDCLASS, MAJORCLASS, WORDCLASS " +
			"from wordclass");	
		ResultSet r = p.executeQuery();
		
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		out.println("<WordHoardWordClasses>");
		while (r.next()) {
			String id = r.getString(1);
			String majorClass = r.getString(2);
			String description = r.getString(3);
			if (id == null || id.length() == 0) continue;
			if (majorClass == null || majorClass.length() == 0) continue;
			if (description == null || description.length() == 0) continue;
			if (majorClass.equals("foreign")) majorClass = "foreign word";
			out.println("\t<wordClass id=\"" + id + "\"");
			out.println("\t\tmajorClass=\"" + majorClass + "\">" +
				description + "</wordClass>");
			numWordClasses++;
		}
		p.close();
		out.println("</WordHoardWordClasses>");
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	public static void main (final String args[]) {
	
		try {
			int n = args.length;
			if (n != 1) {
				System.err.println("Usage: MartinWordClasses out");
				System.exit(1);
			}
			String outPath = args[0];
			System.out.println("Creating word class file " + outPath);
			martinConnection = MartinUtils.getConnection();
			out = MartinUtils.openOutputFile(outPath);
			writeFile();
			martinConnection.close();
			out.close();
			System.out.println(numWordClasses + " word classes created");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	
	}
	
	/**	Hides the default no-arg constructor.
	 */
	 
	private MartinWordClasses () {
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

