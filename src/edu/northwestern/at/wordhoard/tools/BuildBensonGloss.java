package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

import org.w3c.dom.*;

import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	Builds the Benson glosses.
 *
 *	<p>Usage:
 *
 *	<p><code>BuildBensonGloss in dbname username password</code>
 *
 *	<p>in = Path to the Benson gloss definition XML input file.
 *
 *	<p>dbname = Database name.
 *
 *	<p>username = MySQL username.
 *
 *	<p>password = MySQL password.
 */
 
public class BuildBensonGloss {
	
	/**	MySQL table exporter/importer for BensonPos objects. */
	
	private static TableExporterImporter posTableExporterImporter;
	
	/**	MySQL table exporter/importer for BensonLemma objects. */
	
	private static TableExporterImporter lemmaTableExporterImporter;
	
	/**	MySQL table exporter/importer for BensonLemPos objects. */
	
	private static TableExporterImporter lemPosTableExporterImporter;
	
	/**	Parsed XML document. */
	
	private static Document document;
	
	/**	Builds one BensonPos object.
	 *
	 *	@param	el		"pos" element.
	 *
	 *	@throws Exception
	 */
	 
	private static void buildOnePos (Element el)
		throws Exception
	{
		String idStr = el.getAttribute("id");
		if (idStr.length() == 0) {
			BuildUtils.emsg("Missing id attribute");
			return;
		}
		Long id = null;
		try {
			id = new Long(idStr);
		} catch (NumberFormatException e) {
			BuildUtils.emsg("Invalid id attribute: " + idStr);
			return;
		}
		
		String tag = el.getAttribute("tag");
		if (tag.length() == 0) {
			BuildUtils.emsg("Missing tag attribute on pos with id = " +
				idStr);
			return;
		}
		
		String description = DOMUtils.getText(el);
		
		BensonPos pos = new BensonPos();
		pos.setId(id);
		pos.setTag(tag);
		pos.setDescription(description);
		pos.export(posTableExporterImporter);
	}
	
	/**	Builds the BensonPos objects.
	 *
	 *	@throws Exception
	 */
	 
	private static void buildPos ()
		throws Exception
	{
		System.out.println("Building BensonPos objects");
		Element el = DOMUtils.getDescendant(document, 
			"WordHoardBensonGlosses/bensonPartsOfSpeech");
		if (el == null) {
			BuildUtils.emsg(
				"Missing required " +
				"WordHoardBensonGlosses/bensonPartsOfSpeech element");
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
				buildOnePos(childEl);
			} else {
				BuildUtils.emsg("Illegal element: " + childName);
			}
		}
	}
	
	/**	Builds one BensonLemma object.
	 *
	 *	@param	el		"lemma" element.
	 *
	 *	@throws Exception
	 */
	 
	private static void buildOneLemma (Element el)
		throws Exception
	{
		String idStr = el.getAttribute("id");
		if (idStr.length() == 0) {
			BuildUtils.emsg("Missing id attribute");
			return;
		}
		Long id = null;
		try {
			id = new Long(idStr);
		} catch (NumberFormatException e) {
			BuildUtils.emsg("Invalid id attribute: " + idStr);
			return;
		}

		String homonymStr = el.getAttribute("homonym");
		int homonym = 0;
		if (homonymStr.length() > 0) {
			try {
				homonym = Integer.parseInt(homonymStr);
			} catch (NumberFormatException e) {
				BuildUtils.emsg("Invalid homonym attribute " + 
					homonymStr + " for lemma id " + idStr);
				return;
			}
		}
		
		Element wordEl = DOMUtils.getChild(el, "word");
		if (wordEl == null) {
			BuildUtils.emsg("Missing word child for lemma id " + idStr);
			return;
		}
		String word = DOMUtils.getText(wordEl);
		if (word == null || word.length() == 0) {
			BuildUtils.emsg("Missing word for lemma id " + idStr);
			return;
		}
		
		Element wordClassEl = DOMUtils.getChild(el, "word");
		if (wordClassEl == null) {
			BuildUtils.emsg("Missing wordClass child for lemma id " + idStr);
			return;
		}
		String wordClass = DOMUtils.getText(wordClassEl);
		if (wordClass == null || wordClass.length() == 0) {
			BuildUtils.emsg("Missing wordClass for lemma id " + idStr);
			return;
		}
		
		Element definitionEl = DOMUtils.getChild(el, "definition");
		String definition = definitionEl == null ? "" : 
			DOMUtils.getText(definitionEl);
		
		Element commentEl = DOMUtils.getChild(el, "comment");
		String comment = commentEl == null ? "" : 
			DOMUtils.getText(commentEl);
		
		Element oedLemmaEl = DOMUtils.getChild(el, "oedLemma");
		String oedLemma = oedLemmaEl == null ? "" : 
			DOMUtils.getText(oedLemmaEl);
		
		BensonLemma lemma = new BensonLemma();
		lemma.setId(id);
		lemma.setHomonym(homonym);
		lemma.setWord(word);
		lemma.setWordClass(wordClass);
		lemma.setDefinition(definition);
		lemma.setComment(comment);
		lemma.setOedLemma(oedLemma);
		lemma.export(lemmaTableExporterImporter);
			
	}
	
	/**	Builds the BensonLemma objects.
	 *
	 *	@throws Exception
	 */
	 
	private static void buildLemma ()
		throws Exception
	{
		System.out.println("Building BensonLemma objects");
		Element el = DOMUtils.getDescendant(document, 
			"WordHoardBensonGlosses/bensonLemmas");
		if (el == null) {
			BuildUtils.emsg(
				"Missing required " +
				"WordHoardBensonGlosses/bensonLemmas element");
			return;
		}
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (!(child instanceof Element)) continue;
			String childName = child.getNodeName();
			Element childEl = (Element)child;
			if (childName.equals("lemma")) {
				buildOneLemma(childEl);
			} else {
				BuildUtils.emsg("Illegal element: " + childName);
			}
		}
	}
	
	/**	Builds one BensonLemPos object.
	 *
	 *	@param	el		"lemPos" element.
	 *
	 *	@throws Exception
	 */
	 
	private static void buildOneLemPos (Element el)
		throws Exception
	{
		String idStr = el.getAttribute("id");
		if (idStr.length() == 0) {
			BuildUtils.emsg("Missing id attribute");
			return;
		}
		Long id = null;
		try {
			id = new Long(idStr);
		} catch (NumberFormatException e) {
			BuildUtils.emsg("Invalid id attribute: " + idStr);
			return;
		}
		
		String lemmaIdStr = el.getAttribute("lemma");
		if (lemmaIdStr.length() == 0) {
			BuildUtils.emsg("Missing lemma attribute");
			return;
		}
		Long lemmaId = null;
		try {
			lemmaId = new Long(lemmaIdStr);
		} catch (NumberFormatException e) {
			BuildUtils.emsg("Invalid lemma attribute " + lemmaIdStr +
				" for id " + idStr);
			return;
		}
		
		String posIdStr = el.getAttribute("pos");
		if (posIdStr.length() == 0) {
			BuildUtils.emsg("Missing pos attribute");
			return;
		}
		Long posId = null;
		try {
			posId = new Long(posIdStr);
		} catch (NumberFormatException e) {
			BuildUtils.emsg("Invalid pos attribute " + posIdStr +
				" for id " + idStr);
			return;
		}
		
		BensonLemma lemma = new BensonLemma();
		lemma.setId(lemmaId);
		BensonPos pos = new BensonPos();
		pos.setId(posId);
		
		BensonLemPos lemPos = new BensonLemPos();
		lemPos.setId(id);
		lemPos.setLemma(lemma);
		lemPos.setPos(pos);
		lemPos.export(lemPosTableExporterImporter);
	}
	
	/**	Builds the BensonLemPos objects.
	 *
	 *	@throws Exception
	 */
	 
	private static void buildLemPos ()
		throws Exception
	{
		System.out.println("Building BensonLemPos objects");
		Element el = DOMUtils.getDescendant(document, 
			"WordHoardBensonGlosses/bensonLemPos");
		if (el == null) {
			BuildUtils.emsg(
				"Missing required " +
				"WordHoardBensonGlosses/bensonLemPos element");
			return;
		}
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			if (!(child instanceof Element)) continue;
			String childName = child.getNodeName();
			Element childEl = (Element)child;
			if (childName.equals("lemPos")) {
				buildOneLemPos(childEl);
			} else {
				BuildUtils.emsg("Illegal element: " + childName);
			}
		}
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
				System.out.println("Usage: BuildBensonGloss in dbname username password");
				System.exit(1);
			}
			String in = args[0];
			String dbname = args[1];
			String username = args[2];
			String password = args[3];
			
			System.out.println("Building Benson glosses from file " + 
				in);
			
			Connection c = BuildUtils.getConnection(dbname, username, password);
				
			Statement s = c.createStatement();
			int n = s.executeUpdate("delete from bensonpos");
			if (n > 0) System.out.println(
				Formatters.formatIntegerWithCommas(n) + 
				" BensonPos " +
				(n == 1 ? "object" : "objects") +
				" deleted");
			n = s.executeUpdate("delete from bensonlemma");
			if (n > 0) System.out.println(
				Formatters.formatIntegerWithCommas(n) + 
				" BensonLemma " +
				(n == 1 ? "object" : "objects") +
				" deleted");
			n = s.executeUpdate("delete from bensonlempos");
			if (n > 0) System.out.println(
				Formatters.formatIntegerWithCommas(n) + 
				" BensonLemPos " +
				(n == 1 ? "object" : "objects") +
				" deleted");
			s.close();
				
			String tempDirPath = BuildUtils.createTempDir() + "/";
			posTableExporterImporter =
				new TableExporterImporter("bensonpos", 
					null, 
					tempDirPath + "bensonPos.txt", 
					false);
			lemmaTableExporterImporter =
				new TableExporterImporter("bensonlemma", 
					null, 
					tempDirPath + "bensonLemma.txt", 
					false);
			lemPosTableExporterImporter =
				new TableExporterImporter("bensonlempos", 
					null, 
					tempDirPath + "bensonLemPos.txt", 
					false);
			
			document = DOMUtils.parse(in);
			
			buildPos();
			buildLemma();
			buildLemPos();
			
			posTableExporterImporter.close();
			int posCt = posTableExporterImporter.importData(c);
			lemmaTableExporterImporter.close();
			int lemmaCt = lemmaTableExporterImporter.importData(c);
			lemPosTableExporterImporter.close();
			int lemPosCt = lemPosTableExporterImporter.importData(c);
			
			c.close();
			
			BuildUtils.deleteTempDir();
			
			long endTime = System.currentTimeMillis();
			System.out.println(Formatters.formatIntegerWithCommas(posCt) +
				" BensonPos" +
				(posCt == 1 ? " object" : " objects") +
				" created");
			System.out.println(Formatters.formatIntegerWithCommas(lemmaCt) +
				" BensonLemma" +
				(lemmaCt == 1 ? " object" : " objects") +
				" created");
			System.out.println(Formatters.formatIntegerWithCommas(lemPosCt) +
				" BensonLemPos" +
				(lemPosCt == 1 ? " object" : " objects") +
				" created");
			System.out.println("Time: " + 
				BuildUtils.formatElapsedTime(startTime, endTime));
			BuildUtils.reportNumErrors();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**	Hides the default no-arg constructor.
	 */
	 
	private BuildBensonGloss () {
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

