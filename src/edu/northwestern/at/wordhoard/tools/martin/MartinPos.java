package edu.northwestern.at.wordhoard.tools.martin;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

/**	Builds the parts of speech.
 *
 *	<p>Usage:
 *
 *	<p><code>MartinPos out</code>
 *
 *	<p>out = Path to pos XML output file.
 *
 *	<p>The file "misc/martin.properties" specifies the parameters for Martin's 
 *	database.
 */
 
public class MartinPos {
	
	/**	Connection to Martin's database. */
	
	private static Connection martinConnection;
	
	/**	Output file. */
	
	private static PrintWriter out;
	
	/**	Number of pos. */
	
	private static int numPos;
	
	/**	Writes the POS file.
	 *
	 *	@throws	Exception
	 */
	 
	private static void writeFile ()
		throws Exception
	{
		PreparedStatement p = martinConnection.prepareStatement(
			"select NUPOS, SYNTAX, TENSE, MOOD, VOICE, XCASE, GENDER, " +
			"PERSON, NUMBER, DEGREE, NEGATIVE, LANGUAGE, ABBRWORDCLASS " +
			"from pos");
		ResultSet r = p.executeQuery();
		
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		out.println("<WordHoardPos>");
		while (r.next()) {
			String id = r.getString(1);
			String syntax = r.getString(2);
			String tense = r.getString(3);
			String mood = r.getString(4);
			String voice = r.getString(5);
			String xcase = r.getString(6);
			String gender = r.getString(7);
			String person = r.getString(8);
			String number = r.getString(9);
			String degree = r.getString(10);
			String negative = r.getString(11);
			String language = r.getString(12);
			String wordClassTag = r.getString(13);
			out.println("\t<pos id=\"" + id + "\"");
			out.println("\t\tsyntax=\"" + syntax + "\"");
			out.println("\t\ttense=\"" + tense + "\"");
			out.println("\t\tmood=\"" + mood + "\"");
			out.println("\t\tvoice=\"" + voice + "\"");
			out.println("\t\tcase=\"" + xcase + "\"");
			out.println("\t\tgender=\"" + gender + "\"");
			out.println("\t\tperson=\"" + person + "\"");
			out.println("\t\tnumber=\"" + number + "\"");
			out.println("\t\tdegree=\"" + degree + "\"");
			out.println("\t\tnegative=\"" + negative + "\"");
			out.println("\t\tlanguage=\"" + language + "\"");
			out.println("\t\twordClass=\"" + wordClassTag + "\"/>");
			numPos++;
		}
		p.close();
		out.println("</WordHoardPos>");
	}

	/**	The main program.
	 *
	 *	@param	args		Command line arguments.
	 */
	
	public static void main (final String args[]) {
	
		try {
			int n = args.length;
			if (n != 1) {
				System.err.println("Usage: MartinPos out");
				System.exit(1);
			}
			String outPath = args[0];
			System.out.println("Creating POS file " + outPath);
			martinConnection = MartinUtils.getConnection();
			out = MartinUtils.openOutputFile(outPath);
			writeFile();
			out.close();
			System.out.println(numPos + " parts of speech created");
		
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	
	}
	
	/**	Hides the default no-arg constructor.
	 */
	 
	private MartinPos () {
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

