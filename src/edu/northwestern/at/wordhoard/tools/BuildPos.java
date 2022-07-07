package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.sql.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	Builds the parts of speech.
 *
 *	<p>Usage:
 *
 *	<p><code>BuildkPos in dbname username password</code>
 *
 *	<p>in = Path to the parts of speech definition XML input file.
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */
 
public class BuildPos {
	
	/**	MySQL table exporter/importer for pos objects. */
	
	private static TableExporterImporter posTableExporterImporter;

	/**	Map from word class tags to Hibernate ids. */
	
	private static Map wordClassMap;
	
	/**	Set of pos tags. */
	
	private static HashSet tags = new HashSet();
	
	/**	Sets of valid category values. */
	
	private static HashSet validTenses = new HashSet();
	private static HashSet validMoods = new HashSet();
	private static HashSet validVoices = new HashSet();
	private static HashSet validCases = new HashSet();
	private static HashSet validGenders = new HashSet();
	private static HashSet validPersons = new HashSet();
	private static HashSet validNumbers = new HashSet();
	private static HashSet validDegrees = new HashSet();
	private static HashSet validNegatives = new HashSet();
	private static HashSet validLanguages = new HashSet();
		
	static {
	
		validTenses.add("");
		validTenses.add("pres");
		validTenses.add("imperf");
		validTenses.add("fut");
		validTenses.add("aor");
		validTenses.add("perf");
		validTenses.add("plup");
		validTenses.add("futperf");
		validTenses.add("past");
		
		validMoods.add("");
		validMoods.add("ind");
		validMoods.add("subj");
		validMoods.add("opt");
		validMoods.add("impt");
		validMoods.add("inf");
		validMoods.add("ppl");
		
		validVoices.add("");
		validVoices.add("act");
		validVoices.add("mp");
		validVoices.add("mid");
		validVoices.add("pass");
		
		validCases.add("");
		validCases.add("nom");
		validCases.add("gen");
		validCases.add("dat");
		validCases.add("acc");
		validCases.add("voc");
		validCases.add("adv");
		validCases.add("loc");
		validCases.add("obj");
		validCases.add("subj");
		
		validGenders.add("");
		validGenders.add("masc");
		validGenders.add("fem");
		validGenders.add("neut");
		validGenders.add("mf");
		
		validPersons.add("");
		validPersons.add("1st");
		validPersons.add("2nd");
		validPersons.add("3rd");
		
		validNumbers.add("");
		validNumbers.add("sg");
		validNumbers.add("dual");
		validNumbers.add("pl");
		
		validDegrees.add("");
		validDegrees.add("comp");
		validDegrees.add("sup");
		
		validNegatives.add("");
		validNegatives.add("no");
		validNegatives.add("nor");
		validNegatives.add("not");
		validNegatives.add("un");
		
		validLanguages.add("english");
		validLanguages.add("greek");

	}
	
	/**	Gets a Greek id digit.
	 *
	 *	@param	el		"pos" element.
	 *
	 *	@param	attr	Attribute name.
	 *
	 *	@param	vals	Array of attribute values in order 1, 2, 3, ...
	 *
	 *	@return		Digit for attribute value.
	 *
	 *	@throws Exception	general error.
	 */
	 
	public static int getDig (Element el, String attr, String[] vals)
		throws Exception
	{
		String val = el.getAttribute(attr);
		if (val.equals("")) return 0;
		int n = vals.length;
		for (int i = 0; i < n; i++) {
			if (val.equals(vals[i])) return i+1;
		}
		BuildUtils.emsg("Unknown attribute value: " + attr + "=\"" +
			val + "\"");
		return 0;
	}
	
	/**	Checks numeric Greek ids.
	 *
	 *	<p>This method checks all the Greek parts of speech to make sure
	 *	there are no errors in the numeric encoding of the category values
	 *	in the tags.
	 *
	 *	@param	list	Greek parts of speech list.
	 *
	 *	@throws Exception	general error.
	 */
	 
	public static void checkIds (ArrayList list)
		throws Exception
	{
		for (Iterator it = list.iterator(); it.hasNext(); ) {
			Element el = (Element)it.next();
			int[] digits = new int[7];
			digits[0] = getDig(el, 
				"tense", 
				new String[] {"pres", "imperf", "fut", "aor", "perf", "plup", "futperf"});
			digits[1] = getDig(el,
				"mood",
				new String[] {"ind", "subj", "opt", "impt", "inf", "ppl"});
			digits[2] = getDig(el,
				"voice",
				new String[] {"act", "mp", "mid", "pass"});
			digits[3] = getDig(el,
				"case",
				new String[] {"nom", "gen", "dat", "acc", "voc", "loc", "adv"});
			digits[4] = getDig(el,
				"gender",
				new String[] {"masc", "fem", "neut", "mf"});
			digits[5] = getDig(el,
				"person",
				new String[] {"1st", "2nd", "3rd"});
			digits[6] = getDig(el,
				"number",
				new String[] {"sg", "dual", "pl"});
			String id = "";
			boolean allZerosSoFar = true;
			for (int i = 0; i < 7; i++) {
				int dig = digits[i];
				if (dig == 0 && allZerosSoFar) continue;
				allZerosSoFar = false;
				id = id + dig;
			}
			if (id.equals("")) id = "0";
			String idAttr = el.getAttribute("id");
			if (!id.equals(idAttr)) 
				BuildUtils.emsg("Bad pos id " + idAttr + 
					", id does not match category values");
		}
	}
	
	/**	Builds a part of speech.
	 *
	 *	@param	el		"pos" element.
	 *
	 *	@throws Exception
	 */
	 
	private static void buildPos (Element el)
		throws Exception
	{
		String tag = el.getAttribute("id");
		if (tag.length() == 0) {
			BuildUtils.emsg("Missing required id attribute on pos element");
			return;
		}
		
		if (tags.contains(tag)) {
			BuildUtils.emsg("Duplicate id: " + tag);
			return;
		}
		tags.add(tag);
		
		String description = DOMUtils.getText(el);
		
		String wordClassTag = el.getAttribute("wordClass");
		Long wordClassId = (Long)wordClassMap.get(wordClassTag);

		String syntax = el.getAttribute("syntax");
		String tense = el.getAttribute("tense");
		String mood = el.getAttribute("mood");
		String voice = el.getAttribute("voice");
		String xcase = el.getAttribute("case");
		String gender = el.getAttribute("gender");
		String person = el.getAttribute("person");
		String number = el.getAttribute("number");
		String degree = el.getAttribute("degree");
		String negative = el.getAttribute("negative");
		String language = el.getAttribute("language");
		
		if (wordClassId == null) {
			if (language.equals("english"))
				BuildUtils.emsg("Missing or invalid word class for english pos " + tag);
		} else {
			if (language.equals("greek"))
				BuildUtils.emsg("Unexpected word class specified for greek pos " + tag);
		}
		
		if (!validTenses.contains(tense))
			BuildUtils.emsg("Invalid tense for pos " + tag + ": " + tense);
		if (!validMoods.contains(mood))
			BuildUtils.emsg("Invalid mood for pos " + tag + ": " + mood);
		if (!validVoices.contains(voice))
			BuildUtils.emsg("Invalid voice for pos " + tag + ": " + voice);
		if (!validCases.contains(xcase))
			BuildUtils.emsg("Invalid case for pos " + tag + ": " + xcase);
		if (!validGenders.contains(gender))
			BuildUtils.emsg("Invalid gender for pos " + tag + ": " + gender);
		if (!validPersons.contains(person))
			BuildUtils.emsg("Invalid person for pos " + tag + ": " + person);
		if (!validNumbers.contains(number))
			BuildUtils.emsg("Invalid number for pos " + tag + ": " + number);
		if (!validDegrees.contains(degree))
			BuildUtils.emsg("Invalid degree for pos " + tag + ": " + degree);
		if (!validNegatives.contains(negative))
			BuildUtils.emsg("Invalid negative for pos " + tag + ": " + negative);
		if (!validLanguages.contains(language))
			BuildUtils.emsg("Invalid language for pos " + tag + ": " + language);
		
		posTableExporterImporter.print(tag);
		posTableExporterImporter.print(description);
		posTableExporterImporter.print(wordClassId);
		posTableExporterImporter.print(syntax);
		posTableExporterImporter.print(tense);
		posTableExporterImporter.print(mood);
		posTableExporterImporter.print(voice);
		posTableExporterImporter.print(xcase);
		posTableExporterImporter.print(gender);
		posTableExporterImporter.print(person);
		posTableExporterImporter.print(number);
		posTableExporterImporter.print(degree);
		posTableExporterImporter.print(negative);
		posTableExporterImporter.print(language);
		posTableExporterImporter.println();
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
				System.out.println("Usage: BuildPos in dbname username password");
				System.exit(1);
			}
			String in = args[0];
			String dbname = args[1];
			String username = args[2];
			String password = args[3];
			
			System.out.println("Building parts of speech from file " + 
				in);
			
			Connection c = BuildUtils.getConnection(dbname, username, password);
				
			Statement s = c.createStatement();
			int n = s.executeUpdate("delete from pos");
			if (n > 0) System.out.println(n +
				(n == 1 ? " part" : " parts") +
				" of speech deleted");
			s.close();
				
			String tempDirPath = BuildUtils.createTempDir() + "/";
				
			posTableExporterImporter =
				new TableExporterImporter("pos", 
					"tag, description, wordClass, syntax, tense, mood, voice, " +
					"xcase, gender, person, number, degree, negative, language",
					tempDirPath + "pos.txt",
					false);
			
			wordClassMap = BuildUtils.getWordClassMap(c);
			if (wordClassMap.size() == 0) {
				BuildUtils.emsg("No word classes found");
				return;
			}
			
			Document document = DOMUtils.parse(in);
			
			Element el = DOMUtils.getDescendant(document, 
				"WordHoardPos");
			if (el == null) {
				BuildUtils.emsg("Missing required WordHoardPos element");
				return;
			}
			
			NodeList children = el.getChildNodes();
			int numChildren = children.getLength();
			for (int i = 0; i < numChildren; i++) {
				Node child = children.item(i);
				if (!(child instanceof Element)) continue;
				String childName = child.getNodeName();
				Element childEl = (Element)child;
				if (childName.equals("pos")) {
					buildPos(childEl);
				} else {
					BuildUtils.emsg("Illegal element: " + childName);
				}
			}
			
			posTableExporterImporter.close();
			int ct = posTableExporterImporter.importData(c);
			
			c.close();

			BuildUtils.deleteTempDir();
			
			checkIds(DOMUtils.getChildren(el, "pos", "language", "greek"));
			
			long endTime = System.currentTimeMillis();
			System.out.println(Formatters.formatIntegerWithCommas(ct) + 
				(ct == 1 ? " part" : " parts") +
				" of speech" +
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
	 
	private BuildPos () {
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

