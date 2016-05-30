package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.StringUtils;
import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	Generates WordHoard tagged lines (paragraphs).
 */

public class LineGenerator {

	/**	Rules. */

	private Rules rules;

	/**	WordHoard XML output file writer. */

	private XMLWriter out;

	/**	Map from English pos tags to word class tags. */

	private Map posToWordClassMap;

	/**	Style push-down stack. */

	private Style[] styleStack = new Style[100];

	/**	Index in style push-down stack of current style. */

	private int styleStackIndex = 0;

	/**	Token class for recording words and punctuation. */

	private static class Token {
		private boolean isVerse;		// True if in verse, false if prose
		private boolean isPunc;			// True if punctuation, false if word
		private String id;				// Word id (if word)
		private String spelling;		// Spelling
		private String lemma;			// Lemma(s) (if word)
		private String pos;				// Part(s) of speech (if word)
		private Style style;			// Style
	}

	/**	List of tokens for current line. */

	private List tokenList = new ArrayList();

	/**	True if last line generated was a blank line. */

	private boolean lastLineWasBlank = false;

	/**	Previous word ordinal. */

	private String prevWordOrd = "";

	/** Div count.  Must be static! */

	private static int divCount = 0;

	/** Line within div count. */

	private int lineCount = 0;

	/**	Full work tag. */

	private String fullWorkTag;

	/**	Number of bad contractions encountered. */

	private static int numBadContractions;

	/**	Number of words generated. */

	private static int numWords;

	/**	Creates a new line generator.
	 *
	 *	@param	out					WordHoard XML output file writer.
	 *
	 *	@param	posToWordClassMap	Map from pos tags to word class tags.
	 *
	 *	@param	rules				Rules.
	 *
	 *	@param	fullWorkTag			Full work tag for line IDs.
	 */

	public LineGenerator (XMLWriter out, Map posToWordClassMap,
		Rules rules, String fullWorkTag) {
		this.out = out;
		this.posToWordClassMap = posToWordClassMap;
		this.rules = rules;
		this.fullWorkTag = fullWorkTag;
		styleStack[0] = new Style(Style.LEFT, 0, 0);
	}

	/**	Adds a style and pushes it onto the style stack.
	 *
	 *	<p>The specified style is added to the current style on the top of the
	 *	style stack, and the result is pushed onto the style stack.
	 *
	 *	<p>The indentation level and word styles are cumulative. For example,
	 *	suppose the current top style is indented 10 pixels and is bold, and a
	 *	style specifying an indentation of 5 pixels and italic is pushed. The
	 *	new style is indented 15 pixels and is both bold and italic.
	 *
	 *	@param	style		Style.
	 */

	public void pushStyle (Style style) {
		Style oldStyle = styleStack[styleStackIndex];
		Style newStyle = (Style)oldStyle.clone();
		newStyle.add(style);
		styleStack[++styleStackIndex] = newStyle;
	}

	/**	Pops the style stack.
	 */

	public void popStyle () {
		styleStackIndex--;
	}

	/**	Reset div count to zero.
	 */

	public static void resetDivCount() {
		divCount = 0;
	}

	/**	Increment div count.
	 */

	public void incDivCount() {
		divCount++;
		lineCount = 0;
	}

	/**	Maps MorphAdorner lemma and pos attributes to WordHoard attributes.
	 *
	 *	@param	tok		Token.
	 */

	private void mapLemmaAndPos (Token tok) {
		String id = tok.id;
		String lemma = tok.lemma;
		String pos = tok.pos;
		StringTokenizer posTok = new StringTokenizer(pos, "|");
		StringTokenizer lemmaTok = new StringTokenizer(lemma, "|");
		int numPos = posTok.countTokens();
		int numLemma = lemmaTok.countTokens();
		if (numPos != numLemma) {
			BuildUtils.emsg("Number of lemmas and parts of speech not equal for word: " +
				id + ", lemma=" + lemma + ", pos=" + pos);
			numBadContractions++;
			tok.lemma = "[bad-contraction] (zz)";
			tok.pos = "zz";
			return;
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < numPos; i++) {
			if (i > 0) buf.append("|");
			String p = posTok.nextToken();
			String l = lemmaTok.nextToken();
			String wc = (String)posToWordClassMap.get(p);
			if (wc == null) {
				BuildUtils.emsg("Unknown part of speech " + p + " for word: " + id);
				tok.lemma = "[bad-pos] (zz)";
				tok.pos = "zz";
				return;
			} else {
				l = l.replace(' ', '-');
				buf.append(l + " (" + wc + ")");
			}
		}
		tok.lemma = buf.toString();
	}

	/**	Processes a MorphAdorner w element.
	 *
	 *	<p>MorphAdorner sometimes emits multiple "w" elements for a single word, with
	 *	the same id. This typically happens with words marked up with multiple styles.
	 *	For WordHoard, we discard all but the first occurence of words tagged with the
	 *	same id.
	 *
	 *	<p>All lemmas are mapped to lower case, to avoid having multiple WordHoard lemmas
	 *	which are really the same, differing only in case.
	 *
	 *	@param	el		MorphAdorner w element.
	 */

	public void processW (Element el) {
		String ord = el.getAttribute("ord");
		if (ord.equals(prevWordOrd)) return;
		prevWordOrd = ord;
		String id = el.getAttribute("xml:id");
//		String spelling = el.getAttribute("tok");
		String spelling = el.getAttribute("spe");
		if (spelling.length() == 0) {
			Node firstChild = el.getFirstChild();
			if (firstChild == null) return;
			spelling = firstChild.getNodeValue();
		}
		String ms = el.getAttribute("ms");
		if ((ms != null) && ms.equals("side")) {
			appendUntaggedWord(spelling, hasAncestor(el, "l"));
			return;
		}
		String pos = el.getAttribute("pos");
		String lemma = el.getAttribute("lem");
		lemma = lemma.toLowerCase();
		boolean isPunc = pos.length() == 0 || !Character.isLetter(pos.charAt(0));
		Token tok = new Token();
		tok.isVerse = hasAncestor(el, "l");
		tok.isPunc = isPunc;
		tok.id = id;
		tok.spelling = spelling;
		tok.lemma = lemma;
		tok.pos = pos;
		tok.style = styleStack[styleStackIndex];
		if (!isPunc) mapLemmaAndPos(tok);
		tokenList.add(tok);
	}

	/**	Processes a MorphAdorner c element.
	 *
	 *	<p>Space characters at the beginning of lines are discarded.
	 *
	 *	@param	el		MorphAdorner c element.
	 */

	public void processC (Element el) {
		Node firstChild = el.getFirstChild();
		if (firstChild == null) return;
		String str = firstChild.getNodeValue();
		appendPunctuation(str);
	}

	/**	Processes a gap element.
	 *
	 *	@param	el		MorphAdorner c element.
	 */

	public void processGap (Element el) {
		String str = "";
		String extent = el.getAttribute("extent");
		String reason = el.getAttribute("reason");
		if ((extent != null) && (extent.length() > 0)) {
			String unit = el.getAttribute("unit");
			if (unit.equals("word")) {
				int intExtent = Integer.parseInt(extent);
				for (int i = 0; i < intExtent; i++) {
					str += "{...}";
				}
			} else if (unit.equals("span")) {
				str += "{missing span}";
			} else {
				if (extent.equals("1")) {
					str += "{missing " + extent + " " + unit + "}";
				} else {
					str += "{missing " + extent + " " + unit + "s}";
				}
			}
		} else {
			if ((reason != null) && (reason.length() > 0)) {
				str += "{missing text (" + reason + ")";
			} else {
				str += "{missing text of indeterminate length}";
			}
		}
		appendUntaggedWord(str, hasAncestor(el, "l"));
	}

	/**	Emit start tag for element.
	 *
	 *	@param	name	The element name.
	 */

	public void startElement (String name) {
		out.startEl(name);
	}

	/**	Emit end tag for element.
	 *
	 *	@param	name	The element name.
	 */

	public void endElement (String name) {
		out.endEl(name);
	}

	/**	Appends an untagged word.
	 *
	 *	@param	str			Word.
	 *	@param	isVerse		True if word in verse.
	 */

	public void appendUntaggedWord (String str, boolean isVerse) {
		Token tok = new Token();
		tok.isVerse = isVerse;
		tok.isPunc = false;
		tok.id = "untagged";
		tok.spelling = str;
		tok.lemma = null;
		tok.pos = null;
		tok.style = styleStack[styleStackIndex];
		tokenList.add(tok);
	}

	/**	Appends punctuation.
	 *
	 *	<p>Space characters at the beginning of lines are discarded.
	 *
	 *	@param	str		Punctuation
	 */

	public void appendPunctuation (String str) {
		if (tokenList.isEmpty() && str.equals(" ")) return;
		Token tok = new Token();
		tok.isPunc = true;
		tok.isVerse = false;
		tok.spelling = str;
		tok.style = styleStack[styleStackIndex];
		tokenList.add(tok);
	}

	/**	Generates hi elements.
	 *
	 *	@param	style		Style.
	 *
	 *	@return				Number of hi elements generated.
	 */

	private int genHi (Style style) {
		int wordStyles = style.getWordStyles();
		if (wordStyles == 0) return 0;
		int mask = 1;
		int nStyles = 0;
		for (int i = 0; i < Style.STYLE_NAMES.length; i++) {
			if ((wordStyles & mask) != 0) {
				String styleName = Style.STYLE_NAMES[i];
				out.startEl("hi", "rend", styleName);
				nStyles++;
			}
			mask = mask << 1;
		}
		return nStyles;
	}

	/**	Generates hi elements into string buffer.
	 *
	 *	@param	style		Style.
	 *	@param	sb			String buffer to receive hi elements.
	 *
	 *	@return				Number of hi elements generated.
	 */

	private int genHi (Style style, StringBuffer sb) {
		int wordStyles = style.getWordStyles();
		if (wordStyles == 0) return 0;
		int mask = 1;
		int nStyles = 0;
		for (int i = 0; i < Style.STYLE_NAMES.length; i++) {
			if ((wordStyles & mask) != 0) {
				String styleName = Style.STYLE_NAMES[i];
				sb.append("<hi rend=\"");
				sb.append(styleName);
				sb.append("\">");
				nStyles++;
			}
			mask = mask << 1;
		}
		return nStyles;
	}

	/**	Get prosodic display value.
	 *
	 *	@param	isVerse		True if verse, false otherwise.
	 *
	 *	@return				Returns "verse" if isVerse is true and
	 *						"prose" otherwise.
	 */

	private String getProsodic (boolean isVerse) {
		return isVerse ? "verse": "prose";
	}

	/**	Generates a line break.
	 */

	public void lineBreak () {

		if (tokenList.isEmpty()) return;

		if (!out.getActive()) {
			BuildUtils.emsg("Attempt to generate tagged line in stranded context");
			tokenList.clear();
			return;
		}

		Token tok = (Token)tokenList.get(0);
		Style style = tok.style;
/*
		if ((tokenList.size() == 1) && tok.isPunc && (tok.spelling.equals(" "))) {
			tokenList.clear();
			lastLineWasBlank = false;
			return;
		}
*/
		boolean allUntagged = true;
		boolean isVerse = true;
		for (Iterator it = tokenList.iterator(); it.hasNext(); ) {
			tok = (Token)it.next();
			if (!tok.isPunc) {
				isVerse = isVerse && tok.isVerse;
				allUntagged = allUntagged && tok.id.equals("untagged");
			}
		}

		if (allUntagged) {
			StringBuffer sb = new StringBuffer();
			switch (style.getLineStyle()) {
				case Style.LEFT:
					int indent = style.getIndent();
					if (indent == 0) {
						out.startEl("p");
					} else {
						out.startEl("p", "indent",
							Integer.toString(style.getIndent()));
					}
					break;
				case Style.CENTER:
					out.startEl("p", "align", "center");
					break;
				case Style.RIGHT:
					out.startEl("p", "align", "right");
					break;
			}

			for (Iterator it = tokenList.iterator(); it.hasNext(); ) {
				tok = (Token)it.next();
				int nStyles = genHi(tok.style, sb);
				sb.append(XMLWriter.escapeXML(tok.spelling));
				for (int i = 0; i < nStyles; i++) {
					sb.append("</hi>");
				}
			}
			out.writeString(sb.toString());
			out.endEl("p");
			lastLineWasBlank = false;
			tokenList.clear();
			return;
		}

		lineCount++;
		String lineID = fullWorkTag +
			StringUtils.intToStringWithZeroFill(divCount, 3) +
			StringUtils.intToStringWithZeroFill(lineCount, 5);
		String n = StringUtils.intToString(lineCount);

		switch (style.getLineStyle()) {
			case Style.LEFT:
				int indent = style.getIndent();
				if (indent == 0) {
					out.startEl("wordHoardTaggedLine", "id", lineID,
					"n", n, "prosodic", getProsodic(isVerse));
				} else {
					out.startEl("wordHoardTaggedLine", "id", lineID,
						"n", n,
						"indent", Integer.toString(style.getIndent()),
						"prosodic", getProsodic(isVerse));
				}
				break;
			case Style.CENTER:
				out.startEl("wordHoardTaggedLine", "id", lineID, "n", n,
					"align", "center",
					"prosodic", getProsodic(isVerse));
				break;
			case Style.RIGHT:
				out.startEl("wordHoardTaggedLine", "id", lineID, "n", n,
					"align", "right", "prosodic", getProsodic(isVerse));
				break;
		}

		for (Iterator it = tokenList.iterator(); it.hasNext(); ) {
			tok = (Token)it.next();
			int nStyles = genHi(tok.style);
			if (tok.isPunc) {
				out.writeTextEl("punc", tok.spelling);
			} else if (tok.lemma == null || tok.lemma.length() == 0) {
				out.writeTextEl("w", "id", tok.id, tok.spelling);
				numWords++;
			} else if (tok.isVerse && isVerse) {
				out.writeTextEl("w", "id", tok.id, "lemma", tok.lemma,
					"pos", tok.pos, tok.spelling);
				numWords++;
			} else if (!tok.isVerse && !isVerse) {
				out.writeTextEl("w", "id", tok.id, "lemma", tok.lemma,
					"pos", tok.pos, tok.spelling);
				numWords++;
			} else {
				out.writeTextEl("w", "id", tok.id, "lemma", tok.lemma,
					"pos", tok.pos,
					"prosodic", getProsodic(tok.isVerse), tok.spelling);
				numWords++;
			}
			for (int i = 0; i < nStyles; i++) out.endEl("hi");
		}

		out.endEl("wordHoardTaggedLine");
		tokenList.clear();
		lastLineWasBlank = false;
	}

	/**	Generates a paragraph break.
	 */

	public void parBreak () {
		lineBreak();
		if (!lastLineWasBlank) out.writeEmptyEl("p");
		lastLineWasBlank = true;
	}

	/**	Generates an untagged line.
	 *
	 *	@param	str		Text for line.
	 */

	public void untaggedLine (String str) {
		int indent = styleStack[styleStackIndex].getIndent();
		out.writeTextEl("p", "indent", Integer.toString(indent), str);
		lastLineWasBlank = false;
	}

	/**	Generates normalized plain text.
	 *
	 *	@param	str		Text to generate.
	 */

	public void normalizedText (String str) {
		out.writeEscapedString(str.replaceAll( "(\\s+)" , " " ).trim());
		lastLineWasBlank = false;
	}

	/**	See if ancestor of specified node exists with specified name.
	 *
	 *	@param	node	Node for which to find ancestor.
	 *	@param	name	Name of ancestral element to find.
	 *
	 *	@return			True if ancestral node with name "name" found,
	 *					false otherwise.
	 */

	private boolean hasAncestor (Node node , String name) {
		Node p = node;
		while ((p = p.getParentNode()) != null) {
			if (p.getNodeName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**	Gets the number of bad contractions.
	 *
	 *	@return		Number of bad contractions.
	 */

	public static int getNumBadContractions () {
		return numBadContractions;
	}

	/**	Gets the number of words generated.
	 *
	 *	@return		Number of words generated.
	 */

	public static int getNumWords () {
		return numWords;
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

