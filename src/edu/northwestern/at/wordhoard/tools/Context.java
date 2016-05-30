package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import org.w3c.dom.Element;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Parsing context for building works.
 *
 *	<p>All of the variables which can vary during the parsing of a
 *	work are gathered together into a parsing context, which is passed
 *	as a parameter to all of the parsing methods as we recursively
 *	descend the DOM tree for the work. Each parsing method clones the
 *	context to make any necessary context changes during the processing
 *	of its element in the DOM tree.
 */

public class Context implements Cloneable {

	/**	Character set. */

	private byte charset;

	/**	Current work part. */

	private WorkPart workPart;

	/**	Current work part level. */

	private int partLevel = 1;

	/**	Current text object under construction. */

	private Text text;

	/**	Current text line under construction. */

	private TextLine textLine;

	/**	List of word objects under construction for part. */

	private ArrayList wordList;

	/**	List of line objects under construction for part. */

	private ArrayList lineList;

	/**	List of speech objects under construction for part. */

	private ArrayList speechList;

	/**	Current speech, or null if none. */

	private Speech speech;

	/**	True if the current work part is using stanza numbering. */

	private boolean stanzaNumbering;

	/**	True if the current work part is using lemma tagging. */

	private boolean lemmaTagging;

	/**	Current stanza number. */

	private String stanzaNumber;

	/**	True if first line of stanza. */

	private boolean firstLineOfStanza;

	/**	True if we are inside a tagged line. */

	private boolean inTaggedLine;

	/**	Line object for current line. */

	private Line line;

	/**	Set of line indexes which contain words. */

	private HashSet linesWithWords = new HashSet();

	/**	String buffer of characters accumulated in current run. */

	private StringBuffer buf = new StringBuffer();

	/**	Position in current line (character index in line). */

	private int pos = 0;

	/**	Current run of untagged text (punctuation or text in "p"
		elements).
	*/

	private String untaggedText = "";

	/**	Previous word on line, or null if none. */

	private Word prev = null;

	/**	Justification of the current line (TextLine.LEFT, TextLine.CENTER, or
		TextLine.RIGHT. */

	private byte justification;

	/**	Indentation for the current line. */

	private int indentation;

	/**	Font size. */

	private byte size = TextParams.NOMINAL_FONT_SIZE;

	/**	Style. */

	private int style;

	/**	Current prosodic attribute. */

	private Prosodic prosodic;

	/**	Clones the context. */

	public Object clone () {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

	/**	Sets the character set.
	 *
	 *	@param	charset		Character set.
	 */

	public void setCharset (byte charset) {
		this.charset = charset;
	}

	/**	Gets the work part.
	 *
	 *	@return		The work part.
	 */

	public WorkPart getWorkPart () {
		return workPart;
	}

	/**	Sets the work part.
	 *
	 *	@param	workPart	The work part.
	 */

	public void setWorkPart (WorkPart workPart) {
		this.workPart = workPart;
	}

	/**	Starts a new work part.
	 *
	 *	@param	workPart	The work part.
	 *
	 *	@param	headerEl	Header element for part.
	 */

	public void startWorkPart (WorkPart workPart, Element headerEl) {
		setWorkPart(workPart);
		partLevel++;
		startNewText(true);
		wordList = new ArrayList();
		lineList = new ArrayList();
		speechList = new ArrayList();
		setProsodic(headerEl);
	}

	/**	Gets the work part level.
	 *
	 *	@return		The current work part level.
	 */

	public int getPartLevel () {
		return partLevel;
	}

	/**	Starts new text.
	 *
	 *	@param	lineNumbers		True if the text has line numbers.
	 */

	public void startNewText (boolean lineNumbers) {
		text = new Text(lineNumbers, false);
	}

	/**	Finalizes the text.
	 *
	 *	@return		A TextWrapper object containing the finalized text.
	 */

	public TextWrapper finalizeText () {
		text.finalize();
		return new TextWrapper(text);
	}

	/**	Gets the word list.
	 *
	 *	@return		The word list.
	 */

	public ArrayList getWordList() {
		return wordList;
	}

	/**	Adds a word to the word list.
	 *
	 *	@param	word		Word.
	 */

	public void addWord (Word word) {
		wordList.add(word);
	}

	/**	Gets the line list.
	 *
	 *	@return		The line list.
	 */

	public ArrayList getLineList() {
		return lineList;
	}

	/**	Gets the speech list.
	 *
	 *	@return		The speech list.
	 */

	public ArrayList getSpeechList() {
		return speechList;
	}

	/**	Adds a speech to the speech list.
	 *
	 *	@param	speech		Speech.
	 */

	public void addSpeech (Speech speech) {
		speechList.add(speech);
	}

	/**	Gets the current speech.
	 *
	 *	@return		Current speech, or null if none.
	 */

	public Speech getSpeech () {
		return speech;
	}

	/**	Sets the current speech.
	 *
	 *	@param	speech		Current speech, or null if none.
	 */

	public void setSpeech (Speech speech) {
		this.speech = speech;
	}

	/**	Gets the stanza numbering attribute.
	 *
	 *	@return		True if the work part uses stanza numbering.
	 */

	public boolean getStanzaNumbering () {
		return stanzaNumbering;
	}

	/**	Sets the stanza numbering attribute.
	 *
	 *	@param	stanzaNumbering		True if the work part uses stanza
	 *								numbering.
	 */

	public void setStanzaNumbering (boolean stanzaNumbering) {
		this.stanzaNumbering = stanzaNumbering;
	}

	/**	Gets the lemma tagging attribute.
	 *
	 *	@return		True if the work part uses lemma tagging.
	 */

	public boolean getLemmaTagging () {
		return lemmaTagging;
	}

	/**	Sets the lemma tagging attribute.
	 *
	 *	@param	lemmaTagging		True if the work part uses lemma tagging.
	 */

	public void setLemmaTagging (boolean lemmaTagging) {
		this.lemmaTagging = lemmaTagging;
	}

	/**	Gets the stanza number.
	 *
	 *	@return		The stanza number as a string, or null if none.
	 */

	public String getStanzaNumber () {
		return stanzaNumber;
	}

	/**	Sets the stanza number.
	 *
	 *	@param	stanzaNumber	The stanza number as a string, or null
	 *							if none.
	 */

	public void setStanzaNumber (String stanzaNumber) {
		this.stanzaNumber = stanzaNumber;
	}

	/**	Gets the first line of stanza attribute.
	 *
	 *	@return		True if first line of stanza.
	 */

	public boolean getFirstLineOfStanza () {
		return firstLineOfStanza;
	}

	/**	Sets the first line of stanza attribute.
	 *
	 *	@param	firstLineOfStanza	True if first line of stanza.
	 */

	public void setFirstLineOfStanza (boolean firstLineOfStanza) {
		this.firstLineOfStanza = firstLineOfStanza;
	}

	/**	Gets the tagged line attribute.
	 *
	 *	@return		True if in tagged line.
	 */

	public boolean getInTaggedLine () {
		return inTaggedLine;
	}

	/**	Sets the tagged line attribute.
	 *
	 *	@param	inTaggedLine	True if in tagged line.
	 */

	public void setInTaggedLine (boolean inTaggedLine) {
		this.inTaggedLine = inTaggedLine;
	}

	/**	Indents or undents subsequent lines.
	 *
	 *	@param indentation		The additional indentation for subsequent
	 *							lines. A negative value undents.
	 */

	public void indent (int indentation) {
		this.indentation += indentation;
	}

	/**	Gets the current line object.
	 *
	 *	@return		The current line object.
	 */

	public Line getLine () {
		return line;
	}

	/**	Sets the line justification.
	 *
	 *	@param	justification	Line justification (TextLine.LEFT, TextLine.CENTER,
	 *							or TextLine.RIGHT).
	 */

	public void setJustification (byte justification) {
		this.justification = justification;
	}

	/**	Sets the line object.
	 *
	 *	<p>The line object is also added to the line list if it is not
	 *	null.
	 *
	 *	@param	line		Line object, or null if none.
	 */

	public void setLine (Line line) {
		this.line = line;
		if (line != null) lineList.add(line);
	}

	/**	Starts a new text line.
	 */

	public void startLine () {
		int number = line == null ? 0 : line.getNumber();
		String label = line == null ? null : line.getLabel();
		String stanzaLabel = line == null ? null : line.getStanzaLabel();
		textLine = new TextLine(justification, indentation, number,
			label, stanzaLabel);
		untaggedText = "";
	}

	/**	Ends the current text line.
	 */

	public void endLine () {

		flushRun();
		text.appendLine(textLine);
		textLine = null;
		pos = 0;

		//	Process punctuation following the last word.

		if (prev != null) {
			String punc = untaggedText;
			int k = punc.length()-1;
			while (k >= 0 && punc.charAt(k) == ' ') k--;
			prev.setPuncAfter(punc.substring(0, k+1) + "/");
		}
	}

	/**	Appends a word.
	 *
	 *	<p>The returned word object has the spelling, work part, line,
	 *	and location attributes set. This class also takes care of
	 *	setting the puncBefore and puncAfter attributes. All other
	 *	attributes must be set by the caller.
	 *
	 *	@param	str		The word.
	 *
	 *	@return			A new word object for the word.
	 */

	public Word appendWord (String str) {

		int len = str.length();
		Word word = new Word();

		//	Set the spelling, part and line.

		word.setSpelling(new Spelling(str, charset));
		word.setWorkPart(workPart);
		word.setLine(line);

		//	Set the word location.

		int lineIndex = text.getNumLines();
		TextLocation start = new TextLocation(lineIndex, pos);
		TextLocation end = new TextLocation(lineIndex, pos+len);
		word.setLocation(new TextRange(start, end));
		linesWithWords.add(new Integer(lineIndex));

		//	Process punctuation.

		String punc = untaggedText;
		if (prev == null) {
			word.setPuncBefore(punc);
		} else {
			int puncLen = punc.length();
			int lastSpace = punc.lastIndexOf(' ');
			if (lastSpace < 0 || lastSpace == puncLen-1) {
				prev.setPuncAfter(punc);
			} else {
				prev.setPuncAfter(punc.substring(0, lastSpace+1));
				word.setPuncBefore(punc.substring(lastSpace+1));
			}
		}
		prev = word;
		untaggedText = "";

		buf.append(str);
		pos += len;

		return word;

	}

	/**	Appends untagged text to the current line.
	 *
	 *	@param	str			String of untagged text.
	 */

	public void appendText (String str) {
		untaggedText = untaggedText + str;
		buf.append(str);
		pos += str.length();
	}

	/**	Flushes a style run.
	 */

	public void flushRun () {
		if (buf.length() == 0) return;
		byte runSize = size;
		if ((style & TextRun.SUBSCRIPT) != 0 ||
			(style & TextRun.SUPERSCRIPT) != 0)
				runSize = (byte)(size * 0.75);
		TextRun run = new TextRun(buf.toString(), charset, runSize);
		run.setStyle(style);
		if (textLine == null) {
			startLine();
		}
		textLine.appendRun(run);
		buf.setLength(0);
	}

	/**	Sets the font size.
	 *
	 *	@param	size		Font size.
	 */

	public void setSize (byte size) {
		this.size = size;
	}

	/**	Gets the style.
	 *
	 *	@return		The style.
	 */

	public int getStyle () {
		return style;
	}

	/**	Sets the style.
	 *
	 *	@param	style	The style.
	 */

	public void setStyle (int style) {
		this.style = style;
	}

	/**	Sets a style.
	 *
	 *	@param	mask		Style mask.
	 *
	 *	@param	set			True to set the style, false to clear the style.
	 */

	public void setStyle (int mask, boolean set) {
		flushRun();
		if (set) {
			style |= mask;
		} else {
			style &= ~mask;
		}
	}

	/**	Sets the normal style.
	 */

	public void setNormal () {
		flushRun();
		style = 0;
	}

	/**	Sets the bold style.
	 *
	 *	@param	bold		The bold style.
	 */

	public void setBold (boolean bold) {
		setStyle(TextRun.BOLD, bold);
	}

	/**	Sets the italic style.
	 *
	 *	@param	italic		The italic style.
	 */

	public void setItalic (boolean italic) {
		setStyle(TextRun.ITALIC, italic);
	}

	/**	Appends a blank line. */

	public void appendBlankLine () {
		if (text.getNumLines() > 0)
			text.appendBlankLine();
	}

	/**	Sets the line location.
	 */

	public void setLineLocation () {
		if (line == null) return;
		int lineStartIndex = Integer.MAX_VALUE;
		int lineEndIndex = -1;
		for (Iterator it = linesWithWords.iterator(); it.hasNext(); ) {
			int lineIndex = ((Integer)it.next()).intValue();
			lineStartIndex = Math.min(lineStartIndex, lineIndex);
			lineEndIndex = Math.max(lineEndIndex, lineIndex);
		}
		if (lineStartIndex == Integer.MAX_VALUE) {
			lineStartIndex = text.getNumLines()-1;
			lineEndIndex = lineStartIndex;
		}
		TextLocation lineStart = new TextLocation(lineStartIndex, 0);
		TextLocation lineEnd =
			new TextLocation(lineEndIndex, Integer.MAX_VALUE);
		TextRange range = new TextRange(lineStart, lineEnd);
		line.setLocation(range);
		linesWithWords.clear();
	}

	/**	Gets the prosodic attribute.
	 *
	 *	@return		The prosodic attribute.
	 */

	public Prosodic getProsodic () {
		return prosodic;
	}

	/**	Sets the prosodic attribute.
	 *
	 *	@param	el		Element with "prosodic" attribute.
	 */

	public void setProsodic (Element el) {
		String prosodicStr = el.getAttribute("prosodic");
		if (prosodicStr.equals("")) return;
		if (prosodicStr.equals("prose")) {
			prosodic = new Prosodic(Prosodic.PROSE);
		} else if (prosodicStr.equals("verse")) {
			prosodic = new Prosodic(Prosodic.VERSE);
		} else {
			prosodic = new Prosodic(Prosodic.UNKNOWN);
			if (!prosodicStr.equals("unknown"))
				System.out.println("##### " +
					"Invalid prosodic attribute: " + prosodicStr);
		}
	}

	/**	Sets the prosodic attribute.
	 *
	 *	@param	prosodic	Prosodic attribute.
	 */

	public void setProsodic (Prosodic prosodic) {
		this.prosodic = prosodic;
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

