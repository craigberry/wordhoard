package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	Generates work parts.
 */

public class WorkPartGenerator {

	/**	WordHoard XML output file writer. */

	private XMLWriter out;

	/**	Rules. */

	private Rules rules;

	/**	Map from English pos tags to word class tags. */

	private HashMap posMap;

	/**	List of notes to generate at end of work part. */

	private List noteList = new ArrayList();

	/**	Current note number. */

	private int noteNumber = 0;

	/**	WordHoard tagged line generator. */

	private LineGenerator gen;

	/**	True if at root level. */

	private boolean rootLevel = true;

	/**	Work part level (0 if root level, else 1,2,3,... */

	private int workPartLevel = 0;

	/**	Work part tag, or null if at root level. */

	private String workPartTag = null;

	/**	Child work part number. */

	private int childWorkPartNumber = 0;

	/** Do not create work parts level. */

	private int doNotCreateWorkPartsLevel = 0;

	/**	True if "stranded" context - we do not have a work part into which we can
	 *	generate any text - i.e., we are at the root level or after the first child part.
	 */

	private boolean stranded;

	/**	Element currently being processed. */

	private Element curElement;

	/**	Full work tag. */

	private String fullWorkTag;

	/**	Observer for attempts to write to the output file in stranded contexts. */

	private Observer strandedObserver =
		new Observer() {
			public void update(Observable o, Object arg) {
				BuildUtils.emsg("Invalid occurence of " + curElement.getNodeName() +
					" element in stranded context.");
			}
		};

	/**	Creates a new work part generator.
	 *
	 *	@param	out			WordHoard XML output file writer.
	 *
	 *	@param	rules		Rules.
	 *
	 *	@param	posMap		Map from English pos tags to word class tags.
	 */

	public WorkPartGenerator (XMLWriter out, Rules rules, HashMap posMap,
		String fullWorkTag) {
		this.out = out;
		this.rules = rules;
		this.posMap = posMap;
		this.fullWorkTag = fullWorkTag;
	}

	/**	Writes the WordHoard header for a work part.
	 *
	 *	@param	title		Work part title.
	 *
	 *	@param	pathTag		Path tag.
	 */

	private void writeWordHoardHeader (String title, String pathTag) {

		out.startEl("wordHoardHeader");
		out.writeTextEl("title", title);
		out.writeTextEl("fullTitle", title);
		out.writeTextEl("pathTag", pathTag);
		Utils.writeTaggingData(out);
		out.endEl("wordHoardHeader");
	}

	/**	Sets the stranded state.
	 *
	 *	@param	stranded		True if stranded.
	 */

	private void setStranded (boolean stranded) {
		this.stranded = stranded;
		if (stranded) {
			out.setActive(false);
			out.addInactiveObserver(strandedObserver);
		} else {
			out.setActive(true);
			out.deleteInactiveObservers();
		}
	}

	/**	Processes an element.
	 *
	 *	@param	el			Element.
	 */

	private void processElement (Element el) {
		curElement = el;

		String nodeName = el.getNodeName();
		TextElementRule rule = rules.getTextElementRule(el);

		if (rule == null) {
			BuildUtils.emsg("Unexpected element: " + nodeName);
			return;
		}

		boolean makePart = false;
		boolean interpolate = false;
		byte emitTagName = rule.getEmitTagName();

		boolean doNotCreateWorkParts = rule.getDoNotCreateWorkParts();
		if (doNotCreateWorkParts) doNotCreateWorkPartsLevel++;
		byte createPart = rule.getCreatePart();
		if (doNotCreateWorkPartsLevel > 0) createPart = TextElementRule.NEVER;

		switch (createPart) {
			case TextElementRule.NEVER:
				makePart = false;
				interpolate = false;
				break;
			case TextElementRule.SOMETIMES:
				makePart = stranded;
				interpolate = makePart;
				break;
			case TextElementRule.ALWAYS:
				makePart = true;
				interpolate = false;
				break;
		}

		if (makePart) {
			generateNotes();
			childWorkPartNumber++;
			WorkPartGenerator childGenerator =
				new WorkPartGenerator(out, rules, posMap, fullWorkTag);
			String childPartTag = rootLevel ? Integer.toString(childWorkPartNumber) :
				workPartTag + "-" + childWorkPartNumber;
			String childPathTag = Integer.toString(childWorkPartNumber);
			childGenerator.genPart(el, childPartTag, childPathTag, workPartLevel+1,
				interpolate);
			setStranded(true);
			return;
		}

		if (rule.getFootnote()) {
			noteList.add(el);
			noteNumber++;
			gen.pushStyle(rules.getFootnoteReferenceStyle());
			gen.appendUntaggedWord("[" + noteNumber + "]", false);
			gen.popStyle();
			return;
		}

		if (rule.getParBreak()) {
			gen.parBreak();
		} else if (rule.getLineBreak()) {
			gen.lineBreak();
		}

		boolean styleChange = rule.getStyleChange();
		if (styleChange) gen.pushStyle(rule);

		boolean rend = false;
		if (!rootLevel && !rule.getIgnoreRend()) {
			Style rendStyle = rules.getRendAttributeStyle(el);
			if (rendStyle != null) {
				rend = true;
				gen.pushStyle(rendStyle);
			}
		}

		String genBefore = rule.getGenBefore();
		if (genBefore != null) gen.appendPunctuation(genBefore);

		boolean emittedTagName = false;
		switch (emitTagName) {
			case TextElementRule.NEVER:
				break;
			case TextElementRule.SOMETIMES:
				if (!DOMUtils.nodeHasDescendant(el,
					new String[]{"c","w"}, new String[]{})) {
					gen.startElement(rule.getName());
					emittedTagName = true;
				}
				break;
			case TextElementRule.ALWAYS:
				gen.startElement(rule.getName());
				emittedTagName = true;
				break;
		}

		if (nodeName.equals("w")) {
			gen.processW(el);
		} else if (nodeName.equals("c")) {
			gen.processC(el);
		} else if (nodeName.equals("gap")) {
			gen.processGap(el);
		}

		if (!rule.getIgnoreChildren()) processChildren(el);

		String genAfter = rule.getGenAfter();
		if (genAfter != null) gen.appendPunctuation(genAfter);

		if (rend) gen.popStyle();
		if (styleChange) gen.popStyle();

		if (rule.getParBreak()) {
			gen.parBreak();
		} else if (rule.getLineBreak()) {
			gen.lineBreak();
		}

		if (emittedTagName) {
			gen.endElement(rule.getName());
		}

		if (doNotCreateWorkParts) doNotCreateWorkPartsLevel--;
	}

	/**	Processes the children of an element.
	 *
	 *	@param	el			Element.
	 */

	private void processChildren (Element el) {
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);

								//	Emit non-empty text.

			if (childNode.getNodeType() == Node.TEXT_NODE) {
				String text = childNode.getNodeValue().trim();
				if (text.length() > 0) {
					gen.normalizedText(text);
				}
			}
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			processElement(childEl);
		}
	}

	/**	Generates notes.
	 */

	private void generateNotes () {
		if (noteNumber == 0) return;
		gen.parBreak();
		gen.untaggedLine("____________________");
		for (int i = 0; i < noteNumber; i++) {
			gen.parBreak();
			gen.appendUntaggedWord("[Note " + (i+1) + "]", false);
			gen.parBreak();
			gen.pushStyle(rules.getFootnoteStyle());
			Element el = (Element)noteList.get(i);
			processChildren(el);
			gen.popStyle();
		}
		gen.lineBreak();
		noteNumber = 0;
		noteList = null;
	}

	/**	Generates a work part.
	 *
	 *	@param	el				Element (div, trailer, etc.)
	 *
	 *	@param	workPartTag		Work part tag.
	 *
	 *	@param	pathTag			Work part path tag.
	 *
	 *	@param	workPartLevel	Work part level (1, 2, ...)
	 *
	 *	@param	interpolate		True if interpolating work part.
	 */

	private void genPart (Element el, String workPartTag, String pathTag, int workPartLevel,
		boolean interpolate)
	{
		rootLevel = false;
		setStranded(false);
		gen = new LineGenerator(out, posMap, rules, fullWorkTag);
		this.workPartTag = workPartTag;
		this.workPartLevel = workPartLevel;
		String title = rules.getWorkPartTitle(el);
		for (int i = 0; i < workPartLevel+1; i++) System.out.print("   ");
		System.out.println(StringUtils.truncate(title, 50));
		out.startEl("div", "id", workPartTag);
		gen.incDivCount();
		writeWordHoardHeader(title, pathTag);
		if (interpolate) {
			processElement(el);
		} else {
			processChildren(el);
		}
		out.setActive(true);
		out.deleteInactiveObservers();
		gen.lineBreak();
		generateNotes();
		out.endEl("div");
	}

	/**	Generates the text.
	 *
	 *	@param	el			Root element for the text (e.g., a TEI/text element).
	 */

	public void genText (Element el) {
		System.out.println("   Table of contents");
		out.startEl("text");
		out.startEl("body");
		gen = new LineGenerator(out, posMap, rules, fullWorkTag);
		rootLevel = true;
		setStranded(true);
		processElement(el);
		setStranded(false);
		out.endEl("body");
		out.endEl("text");
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


