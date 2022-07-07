package edu.northwestern.at.wordhoard.tools.martin;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.sql.*;

import org.w3c.dom.*;

import edu.northwestern.at.wordhoard.tools.TaggingDataProvider;
import edu.northwestern.at.wordhoard.model.text.*;

/**	A tagging data provider for Martin's database.
 */

public class MartinProvider implements TaggingDataProvider {

	/**	Word info class. */

	private static class WordInfo {
		private String wordTag;
		private String pos;
		private String lemma;
		private String lemma2;
		private String homonym;
		private WordInfo (String wordTag, String pos, String lemma,
			String lemma2, String homonym) {
			this.wordTag = wordTag;
			this.pos = pos;
			this.lemma = lemma;
			this.lemma2 = lemma2;
			this.homonym = homonym;
			if (this.homonym != null) {
				this.homonym = this.homonym.trim();
				if ((this.homonym.length() == 0) ||
					this.homonym.equals("0")) this.homonym = null;
			}
		}
	}

	/**	Connection to Martin's database. */

	private Connection martinConnection;

	/**	Corpus tag. */

	private String corpusTag;

	/**	Work tag. */

	private String workTag;

	/**	True if Spenser's Fairie Queen.
	 *
	 *	<p>The Fairie Queen is so big that we special case it to process
	 *	one book at a time.
	 */

	private boolean faq;

	/**	Current prefix for Fairie Queen. */

	private String faqPrefix;

	/**	Word info map. Maps word tags to word info. */

	private HashMap wordInfoMap;

	/**	Map from full work tags ("corpusTag-workTag") to word tag
	 *	prefix strings.
	 */

	private static HashMap wordTagPrefixMap = new HashMap();

	static {
		wordTagPrefixMap.put("spe-faq", "fq");
		wordTagPrefixMap.put("spe-sc", "sc");
		wordTagPrefixMap.put("spe-amepith", "spm18,spm19");
		wordTagPrefixMap.put("spe-asclor", "spm03");
		wordTagPrefixMap.put("spe-colcl", "spm02");
		wordTagPrefixMap.put("spe-com", "spm09,spm10,spm11,spm12,spm13,spm14,spm15,spm16,spm17");
		wordTagPrefixMap.put("spe-comson", "spm21");
		wordTagPrefixMap.put("spe-daph", "spm01");
		wordTagPrefixMap.put("spe-fh", "spm04,spm05,spm06,spm07");
		wordTagPrefixMap.put("spe-frag", "spm22");
		wordTagPrefixMap.put("spe-proth", "spm20");
		wordTagPrefixMap.put("spe-theatre", "spm08");
		wordTagPrefixMap.put("sha-1h4", "sha-1h4");
		wordTagPrefixMap.put("sha-1h6", "sha-1h6");
		wordTagPrefixMap.put("sha-2h4", "sha-2h4");
		wordTagPrefixMap.put("sha-2h6", "sha-2h6");
		wordTagPrefixMap.put("sha-3h6", "sha-3h6");
		wordTagPrefixMap.put("sha-ant", "sha-ant");
		wordTagPrefixMap.put("sha-aww", "sha-aww");
		wordTagPrefixMap.put("sha-ayl", "sha-ayl");
		wordTagPrefixMap.put("sha-com", "sha-com");
		wordTagPrefixMap.put("sha-cor", "sha-cor");
		wordTagPrefixMap.put("sha-cym", "sha-cym");
		wordTagPrefixMap.put("sha-ham", "sha-ham");
		wordTagPrefixMap.put("sha-he5", "sha-he5");
		wordTagPrefixMap.put("sha-he8", "sha-he8");
		wordTagPrefixMap.put("sha-juc", "sha-juc");
		wordTagPrefixMap.put("sha-kij", "sha-kij");
		wordTagPrefixMap.put("sha-kil", "sha-kil");
		wordTagPrefixMap.put("sha-lll", "sha-lll");
		wordTagPrefixMap.put("sha-lov", "sha-lov");
		wordTagPrefixMap.put("sha-mac", "sha-mac");
		wordTagPrefixMap.put("sha-man", "sha-man");
		wordTagPrefixMap.put("sha-mem", "sha-mem");
		wordTagPrefixMap.put("sha-mev", "sha-mev");
		wordTagPrefixMap.put("sha-mnd", "sha-mnd");
		wordTagPrefixMap.put("sha-mww", "sha-mww");
		wordTagPrefixMap.put("sha-oth", "sha-oth");
		wordTagPrefixMap.put("sha-per", "sha-per");
		wordTagPrefixMap.put("sha-pht", "sha-pht");
		wordTagPrefixMap.put("sha-rap", "sha-rap");
		wordTagPrefixMap.put("sha-ri2", "sha-ri2");
		wordTagPrefixMap.put("sha-ri3", "sha-ri3");
		wordTagPrefixMap.put("sha-roj", "sha-roj");
		wordTagPrefixMap.put("sha-son", "sha-son");
		wordTagPrefixMap.put("sha-tam", "sha-tam");
		wordTagPrefixMap.put("sha-tem", "sha-tem");
		wordTagPrefixMap.put("sha-tgv", "sha-tgv");
		wordTagPrefixMap.put("sha-tim", "sha-tim");
		wordTagPrefixMap.put("sha-tit", "sha-tit");
		wordTagPrefixMap.put("sha-tro", "sha-tro");
		wordTagPrefixMap.put("sha-twn", "sha-twn");
		wordTagPrefixMap.put("sha-ven", "sha-ven");
		wordTagPrefixMap.put("sha-win", "sha-win");
		wordTagPrefixMap.put("cha-aaa", "ch-04");
		wordTagPrefixMap.put("cha-ast", "ch-10");
		wordTagPrefixMap.put("cha-bod", "ch-02");
		wordTagPrefixMap.put("cha-boe", "ch-06");
		wordTagPrefixMap.put("cha-can", "ch-01");
		wordTagPrefixMap.put("cha-hof", "ch-03");
		wordTagPrefixMap.put("cha-lgw", "ch-08");
		wordTagPrefixMap.put("cha-poe", "ch-09");
		wordTagPrefixMap.put("cha-pof", "ch-05");
		wordTagPrefixMap.put("cha-ror", "ch-11");
		wordTagPrefixMap.put("cha-tro", "ch-07");
		wordTagPrefixMap.put("ege-HH", "ege-4");
		wordTagPrefixMap.put("ege-IL", "ege-1");
		wordTagPrefixMap.put("ege-OD", "ege-2");
		wordTagPrefixMap.put("ege-SH", "ege-303");
		wordTagPrefixMap.put("ege-TH", "ege-301");
		wordTagPrefixMap.put("ege-WD", "ege-302");
	}

	/**	Creates a new MartinProvider.
	 *
	 *	@param	martinConnection		Connection to Martin's database.
	 *
	 *	@throws Exception	general error
	 */

	public MartinProvider (Connection martinConnection)
		throws Exception
	{
		this.martinConnection = martinConnection;
	}

	/**	Gets the word info map for a work.
	 *
	 *	@param	prefix		Word tag prefix.
	 *
	 *	@throws	Exception	general error
	 */

	private void getWordInfoMap (String prefix)
		throws Exception
	{
		boolean greek = prefix.startsWith("ege");
		String tableName = greek ? "greek" : "english";
		String [] prefixes = null;
		prefixes = prefix.split(",");

		String sql =
			greek ?
				"select wordoccurrenceid, NUPOS, lemma " +
				"from " + tableName + " where " +
				"wordoccurrenceid like \"" + prefixes[0] + "%\""
			:
				"select wordoccurrenceid, NUPOS, lemma, lemma2, lemma_homonym " +
				"from " + tableName + " where " +
				"wordoccurrenceid like \"" + prefixes[0] + "%\"";

		for (int i = 1 ; i < prefixes.length ; i++) {
			sql = sql + " or wordoccurrenceid like \"" + prefixes[i] + "%\"";
		}

		wordInfoMap = new HashMap();
		Statement s = martinConnection.createStatement();

		ResultSet r = s.executeQuery(sql);
		while (r.next()) {
			String wordTag = r.getString(1);
			String pos = r.getString(2);
			String lemma1 = r.getString(3);
			String lemma2 = greek ? null : r.getString(4);
			String homonym = greek ? null : r.getString(5);
			wordInfoMap.put(wordTag, new WordInfo(wordTag, pos, lemma1, lemma2, homonym));
		}
		s.close();
		prefixes = null;
		sql = null;
	}

	/**	Sets the work.
	 *
	 *	@param	corpusTag	Corpus tag.
	 *
	 *	@param	workTag		Work tag.
	 *
	 *	@return				True if no error, false if error.
	 *
	 *	@throws Exception	general error
	 */

	public boolean setWork (String corpusTag, String workTag)
		throws Exception
	{

		int k = corpusTag.indexOf("|");
		if (k > 0) corpusTag = corpusTag.substring(0,k);
		this.corpusTag = corpusTag;
		this.workTag = workTag;

		String fullTag = corpusTag + "-" + workTag;
		String prefix = (String)wordTagPrefixMap.get(fullTag);
		if (prefix == null) {
			MartinUtils.emsg("Unknown work " + fullTag);
			return false;
		}

		faq = false;
		if (corpusTag.equals("spe") && workTag.equals("faq")) {
			wordInfoMap = null;
			faq = true;
		} else {
			getWordInfoMap(prefix);
		}

		return true;

	}

	/**	Parses a pos.
	 *
	 *	@param	pos			Pos tag(s) in Martin's format.
	 *
	 *	@return				Pos tag(s) in our format.
	 */

	private String parsePos (String pos) {
		return pos.replaceAll("/", "|");
	}

	/**	Parses a lemma.
	 *
	 *	@param	lemma		Lemma tag in Martin's format.
	 *	@param	homonym		Homonym tag in Martin's format.
	 *
	 *	@return				Lemma tag in our format, or null if empty.
	 */

	private String parseLemma (String lemma, String homonym) {
		if (lemma == null || lemma.length() == 0) return null;
		int i = lemma.indexOf('_');
		if (i < 0) return lemma;
		String tag = lemma.substring(0, i);
		int j = lemma.indexOf('(');
		if (j < 0) {
			String wc = lemma.substring(i+1);
			if (homonym != null) {
				return tag + " (" + wc + ") (" + homonym + ")";
			} else {
				return tag + " (" + wc + ")";
			}
		} else {
			String wc = lemma.substring(i+1, j);
			String hom = lemma.substring(j);
			return tag + " (" + wc + ") " + hom;
		}
	}

	/**	Gets the morphology data for a word.
	 *
	 *	@param	el			"w" element.
	 *
	 *	@param	wordTag		Word tag.
	 *
	 *	@return				Morphology tags for the word: An array of two
	 *						strings: The lemma and pos tags. Returns null
	 *						if no data is available or an error was
	 *						reported.
	 *
	 *	@throws Exception	general error
	 */

	public String[] getMorph (Element el, String wordTag)
		throws Exception
	{
		if (faq && (wordInfoMap == null || !wordTag.startsWith(faqPrefix))) {
			faqPrefix = wordTag.substring(0,3);
			getWordInfoMap(faqPrefix);
		}
		StringBuffer lemmaBuf = new StringBuffer();
		StringBuffer posBuf = new StringBuffer();
		WordInfo info = (WordInfo)wordInfoMap.get(wordTag);
		if (info == null) {
			if (!wordTag.startsWith("sha-rapea"))
				MartinUtils.emsg("No morphology tagging data found for word " +
					wordTag);
			return null;
		}
		String pos = parsePos(info.pos);
		if (pos == null || pos.length() == 0) {
			MartinUtils.emsg("Null or empty part of speech for word " + wordTag);
			return null;
		}
		String lemma1 = parseLemma(info.lemma, info.homonym);
		if (lemma1 == null || lemma1.length() == 0) {
			MartinUtils.emsg("Null or empty lemma for word " + wordTag);
			return null;
		}
		String lemma2 = parseLemma(info.lemma2, null);
		String[] morph = new String[2];
		if (lemma2 == null) {
			morph[0] = lemma1;
		} else {
			morph[0] = lemma1 + "|" + lemma2;
		}
		morph[1] = pos;
		return morph;
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

