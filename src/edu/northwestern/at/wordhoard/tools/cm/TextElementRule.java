package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.*;

import edu.northwestern.at.utils.xml.*;

/**	A text element rule.
 */

public class TextElementRule extends Style {

	/**	Never create a work part. */

	public static final byte NEVER = 0;

	/**	Sometimes create a work part, in contexts where one must be interpolated. */

	public static final byte SOMETIMES = 1;

	/**	Always create a work part. */

	public static final byte ALWAYS = 2;

	/**	Element name. */

	private String name;

	/**	True to ignore children. */

	private boolean ignoreChildren;

	/**	True to generate line break before and after. */

	private boolean lineBreak;

	/**	True to generate paragraph break before and after. */

	private boolean parBreak;

	/**	When to create a work part. */

	private byte createPart;

	/**	True to treat as footnote. */

	private boolean footnote;

	/**	True if descendants should never create work parts. */

	private boolean doNotCreateWorkParts;

	/**	True to ignore rend attribute. */

	private boolean ignoreRend;

	/**	String to generate before processing element, or null if none. */

	private String genBefore;

	/**	String to generate after processing element, or null if none. */

	private String genAfter;

	/**	True if style change. */

	private boolean styleChange;

	/** True to emit start and end tags for element. */

	private byte emitTagName;

	/**	Creates a new text element rule.
	 *
	 *	@param	el			TextElementRule element.
	 */

	public TextElementRule (Element el) {
		super(el);
		Utils.checkAttributeNames(el, new String[] {"name", "ignoreChildren", "lineBreak",
			"parBreak", "lineStyle", "indent", "wordStyles", "createPart", "footnote",
			"doNotCreateWorkParts", "ignoreRend", "genBefore", "genAfter"
			, "emitTagName"
			});
		Utils.checkNoChildren(el);
		name = el.getAttribute("name");
		if (name.length() == 0)
			Utils.paramErr("Missing required name attribute on element element");
		ignoreChildren = Utils.parseBooleanAttribute(el, "ignoreChildren", false);
		lineBreak = Utils.parseBooleanAttribute(el, "lineBreak", false);
		parBreak = Utils.parseBooleanAttribute(el, "parBreak", false);
		createPart = parseCreatePartAttribute(el);
		footnote = Utils.parseBooleanAttribute(el, "footnote", false);
		doNotCreateWorkParts = Utils.parseBooleanAttribute(el, "doNotCreateWorkParts", false);
		ignoreRend = Utils.parseBooleanAttribute(el, "ignoreRend", false);
		genBefore = el.getAttribute("genBefore");
		if (genBefore.length() == 0) genBefore = null;
		genAfter = el.getAttribute("genAfter");
		if (genAfter.length() == 0) genAfter = null;
		styleChange = super.styleChange();
		emitTagName = parseEmitTagNameAttribute(el);
	}

	/**	Parses an emitTagName attribute for a parameter file element.
	 *
	 *	@param	el			Element.
	 *
	 *	@return				Create part value.
	 */

	private byte parseEmitTagNameAttribute (Element el) {
		String attrValue = el.getAttribute("emitTagName");
		if (attrValue.length() == 0) return NEVER;
		if (attrValue.equals("never")) return NEVER;
		if (attrValue.equals("sometimes")) return SOMETIMES;
		if (attrValue.equals("always")) return ALWAYS;
		Utils.paramErr("Illegal emit tag name value " + attrValue + " on " +
			el.getNodeName() + " element");
		return NEVER;
	}

	/**	Parses a createPart attribute for a parameter file element.
	 *
	 *	@param	el			Element.
	 *
	 *	@return				Create part value.
	 */

	private byte parseCreatePartAttribute (Element el) {
		String attrValue = el.getAttribute("createPart");
		if (attrValue.length() == 0) return NEVER;
		if (attrValue.equals("never")) return NEVER;
		if (attrValue.equals("sometimes")) return SOMETIMES;
		if (attrValue.equals("always")) return ALWAYS;
		Utils.paramErr("Illegal create part value " + attrValue + " on " +
			el.getNodeName() + " element");
		return NEVER;
	}

	/**	Returns the element name.
	 *
	 *	@return		Element name.
	 */

	public String getName () {
		return name;
	}

	/**	Returns true to ignore children.
	 *
	 *	@return		True to ignore children.
	 */

	public boolean getIgnoreChildren () {
		return ignoreChildren;
	}

	/**	Returns true to generate line break before and after.
	 *
	 *	@return		True to generate line break before and after.
	 */

	public boolean getLineBreak () {
		return lineBreak;
	}

	/**	Returns true to generate paragraph break before and after.
	 *
	 *	@return		True to generate paragraph break before and after.
	 */

	public boolean getParBreak () {
		return parBreak;
	}

	/**	Gets when to create a work part.
	 *
	 *	@return		When to create a work part.
	 */

	public byte getCreatePart () {
		return createPart;
	}

	/**	Returns true to treat as footnote.
	 *
	 *	@return		True to treat as footnote.
	 */

	public boolean getFootnote () {
		return footnote;
	}

	/**	Returns true if descendants should never create work parts.
	 *
	 *	@return		True if descendants should never create work parts.
	 */

	public boolean getDoNotCreateWorkParts () {
		return doNotCreateWorkParts;
	}

	/**	Returns true to ignore rend attribute.
	 *
	 *	@return		True to ignore rend attribute.
	 */

	public boolean getIgnoreRend () {
		return ignoreRend;
	}

	/**	Gets the string to generate before processing the element.
	 *
	 *	@return		String to generate before processing the element,
	 *				or null if none.
	 */

	public String getGenBefore () {
		return genBefore;
	}

	/**	Gets the string to generate after processing the element.
	 *
	 *	@return		String to generate after processing the element,
	 *				null if none.
	 */

	public String getGenAfter () {
		return genAfter;
	}

	/**	Returns true if this elements changes the style.
	 *
	 *	@return		True if style change.
	 */

	public boolean getStyleChange () {
		return styleChange;
	}

	/**	Returns true to generate start and end tags for element.
	 *
	 *	@return		When to generate start and end tags for element.
	 */

	public byte getEmitTagName () {
		return emitTagName;
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


