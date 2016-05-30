package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	Rules.
 */

public class Rules {

	/**	Corpus tag. */

	private String corpusTag;

	/**	Title page rules. */

	private TitlePageRules titlePageRules;

	/**	File rules. */

	private FileRules fileRules;

	/**	Header rules. */

	private HeaderRules headerRules;

	/**	Author rules. */

	private AuthorRules authorRules;

	/**	Path to text. */

	private String textPath;

	/**	A map from text element names to text element rules. */

	private Map textElementRules;

	/**	Array of work part title rules. */

	private WorkPartTitleRule[] workPartTitleRules;

	/**	Array of rend attribute rules. */

	private RendAttributeRule[] rendAttributeRules;

	/**	Footnote reference style. */

	private Style footnoteReferenceStyle =
		new Style(Style.NO_CHANGE, Style.NO_CHANGE, Style.SUPERSCRIPT);

	/**	Footnote style. */

	private Style footnoteStyle = new Style(Style.LEFT, 20, Style.NO_CHANGE);

	/**	Creates a new rules object.
	 *
	 *	@param	path	Path to XML parameters file.
	 *
	 *	@throws Exception
	 */

	public Rules (String path)
		throws Exception
	{
		File file = new File(path);
		Document doc = DOMUtils.parse(file);
		Element el = DOMUtils.getDescendant(doc, "ConvertMorphRules");
		if (el == null) Utils.paramErr("Missing required ConvertMorphRules root element");
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String childName = childEl.getNodeName();
			if (childName.equals("corpusTag")) {
				processCorpusTag(childEl);
			} else if (childName.equals("titlePageRules")) {
				titlePageRules = new TitlePageRules(childEl);
			} else if (childName.equals("fileRules")) {
				fileRules = new FileRules(childEl);
			} else if (childName.equals("headerRules")) {
				headerRules = new HeaderRules(childEl);
			} else if (childName.equals("authorRules")) {
				authorRules = new AuthorRules(childEl);
			} else if (childName.equals("textPath")) {
				processTextPath(childEl);
			} else if (childName.equals("textElementRules")) {
				processTextElementRules(childEl);
			} else if (childName.equals("workPartTitleRules")) {
				processWorkPartTitleRules(childEl);
			} else if (childName.equals("rendAttributeRules")) {
				processRendAttributeRules(childEl);
			} else if (childName.equals("footnoteRules")) {
				processFootnoteRules(childEl);
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of ConverMorphRules element");
			}
		}
		if (corpusTag == null)
			Utils.paramErr("Missing required corpusTag element");
		if (textPath == null)
			Utils.paramErr("Missing required textPath element");
		if (textElementRules == null)
			Utils.paramErr("Missing required textElementRules element");
		if (workPartTitleRules == null)
			Utils.paramErr("Missing required workPartTitleRules element");
	}

	/**	Processes a corpusTag element.
	 *
	 *	@param	el	CorpusTag element.
	 */

	private void processCorpusTag (Element el) {
		Utils.checkNoAttributes(el);
		Utils.checkNoChildren(el);
		corpusTag = DOMUtils.getText(el);
	}

	/**	Processes a textPath element.
	 *
	 *	@param	el	TextPath element.
	 */

	private void processTextPath (Element el) {
		Utils.checkNoAttributes(el);
		Utils.checkNoChildren(el);
		textPath = DOMUtils.getText(el);
	}

	/**	Processes a textElementsRules element.
	 *
	 *	@param	el		TextElementRules element.
	 */

	private void processTextElementRules (Element el) {
		Utils.checkNoAttributes(el);
		textElementRules = new HashMap();
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String childName = childEl.getNodeName();
			if (childName.equals("textElementRule")) {
				TextElementRule textElementRule = new TextElementRule(childEl);
				textElementRules.put(textElementRule.getName(), textElementRule);
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of textElementRules element");
			}
		}
	}

	/**	Processes a workPartTitleRules element.
	 *
	 *	@param	el		WorkPartTitleRules element.
	 */

	private void processWorkPartTitleRules (Element el) {
		Utils.checkNoAttributes(el);
		List workPartTitleRulesList = new ArrayList();
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String childName = childEl.getNodeName();
			WorkPartTitleRule workPartTitleRule = null;
			if (childName.equals("useFirstChild")) {
				workPartTitleRule = new UseFirstChildRule(childEl);
			} else if (childName.equals("useAttributeValue")) {
				workPartTitleRule = new UseAttributeValueRule(childEl);
			} else if (childName.equals("useAttributeValuePair")) {
				workPartTitleRule = new UseAttributeValuePairRule(childEl);
			} else if (childName.equals("useElementName")) {
				workPartTitleRule = new UseElementNameRule(childEl);
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of workPartTitleRules element");
			}
			if (workPartTitleRule != null)
				workPartTitleRulesList.add(workPartTitleRule);
		}
		workPartTitleRules = (WorkPartTitleRule[])workPartTitleRulesList.toArray(
			new WorkPartTitleRule[workPartTitleRulesList.size()]);
	}

	/**	Processes a rendAttributeRules element.
	 *
	 *	@param	el		RendAttributeRules element.
	 */

	private void processRendAttributeRules (Element el) {
		Utils.checkNoAttributes(el);
		List rendAttributeRulesList = new ArrayList();
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String childName = childEl.getNodeName();
			if (childName.equals("rendAttributeRule")) {
				RendAttributeRule rendAttributeRule = new RendAttributeRule(childEl);
				rendAttributeRulesList.add(rendAttributeRule);
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of rendAttributeRules element");
			}
		}
		rendAttributeRules = (RendAttributeRule[])rendAttributeRulesList.toArray(
			new RendAttributeRule[rendAttributeRulesList.size()]);
	}

	/**	Processes a footnoteRefStyle element.
	 *
	 *	@param	el		FootnoteRefStyle element.
	 */

	private void processFootnoteRefStyle (Element el) {
		Utils.checkAttributeNames(el, new String[] {"lineStyle", "indent", "wordStyles"});
		Utils.checkNoChildren(el);
		footnoteReferenceStyle = new Style(el);
	}

	/**	Processes a footnoteStyle element.
	 *
	 *	@param	el		FootnoteStyle element.
	 */

	private void processFootnoteStyle (Element el) {
		Utils.checkAttributeNames(el, new String[] {"lineStyle", "indent", "wordStyles"});
		Utils.checkNoChildren(el);
		footnoteStyle = new Style(el);
	}

	/**	Processes a footnoteRules element.
	 *
	 *	@param	el		FootnoteRules element.
	 */

	private void processFootnoteRules (Element el) {
		Utils.checkNoAttributes(el);
		NodeList nodeList = el.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if (childNode.getNodeType() != Node.ELEMENT_NODE) continue;
			Element childEl = (Element)childNode;
			String childName = childEl.getNodeName();
			if (childName.equals("footnoteRefStyle")) {
				processFootnoteRefStyle(childEl);
			} else if (childName.equals("footnoteStyle")) {
				processFootnoteStyle(childEl);
			} else {
				Utils.paramErr("Illegal child element " + childName +
					" of footnoteRules element");
			}
		}
	}

	/**	Gets the corpus tag.
	 *
	 *	@return		The corpus tag.
	 */

	public String getCorpusTag () {
		return corpusTag;
	}

	/**	Gets the title page rules.
	 *
	 *	@return		The title page rules.
	 */

	public TitlePageRules getTitlePageRules () {
		return titlePageRules;
	}

	/**	Gets the work header values.
	 *
	 *	@param	document	XML document.
	 *
	 *	@param	fileName	File name.
	 *
	 *	@return				Header value map. Maps the names of WordHoard header
	 *						items to their values.
	 */

	public Map getHeaderValues (Document document, String fileName) {
		Map result = null;
		if (headerRules != null) result = headerRules.applyRules(document);
		if (fileRules != null) result.putAll(fileRules.applyRule(fileName));
		if (result == null) result = new HashMap();
		return result;
	}

	/**	Gets the authors.
	 *
	 *	@param	document	XML document.
	 *
	 *	@param	fileName	File name.
	 *
	 *	@return				List of authors.
	 */

	public List getAuthors (Document document, String fileName) {
		List result = null;
		if (fileRules != null) result = fileRules.applyAuthorRule(fileName);
		if (result == null && authorRules != null) result = authorRules.applyRules(document);
		if (result == null) result = new ArrayList();
		return result;
	}

	/**	Gets the text path.
	 *
	 *	@return		The text path.
	 */

	public String getTextPath () {
		return textPath;
	}

	/**	Gets a text element rule.
	 *
	 *	@param	el		Element.
	 *
	 *	@return			Text element rule, or null if none.
	 */

	public TextElementRule getTextElementRule (Element el) {
		String name = el.getNodeName();
		return (TextElementRule)textElementRules.get(name);
	}

	/**	Gets a work part title.
	 *
	 *	@param	el		Element.
	 *
	 *	@return			Work part title.
	 */

	public String getWorkPartTitle (Element el) {
		String title = null;
		for (int i = 0; i < workPartTitleRules.length; i++) {
			title = workPartTitleRules[i].applyRule(el, this);
			if (title != null) break;
		}
		if (title == null) {
			BuildUtils.emsg("Unable to find work part tile, title set to 'Untitled'");
			title = "Untitled";
		} else {
			title = title.replace('_', ' ');
			title = title.replaceAll("¶", "");
		}
		return title;
	}

	/**	Gets the style for a rend attribute.
	 *
	 *	@param	el		Element.
	 *
	 *	@return			Style, or null if none.
	 */

	public Style getRendAttributeStyle (Element el) {
		if (rendAttributeRules == null) return null;
		for (int i = 0; i < rendAttributeRules.length; i++) {
			Style style = rendAttributeRules[i].applyRule(el);
			if (style != null) return style;
		}
		return null;
	}

	/**	Gets the footnote reference style.
	 *
	 *	@return		The footnote reference style.
	 */

	public Style getFootnoteReferenceStyle () {
		return footnoteReferenceStyle;
	}

	/**	Gets the footnote style.
	 *
	 *	@return		The footnote style.
	 */

	public Style getFootnoteStyle () {
		return footnoteStyle;
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


