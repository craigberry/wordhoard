package edu.northwestern.at.wordhoard.tools;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.utils.xml.*;

/**	Builds styled paragraphs.
 */
 
public class BuildParagraph extends TextLine {

	/**	Character set. */
	
	private byte charset;
	
	/**	Font size. */
	
	private byte size;
	
	/**	Map from "rend" attribute values to style masks. */
	
	private static HashMap rendMap = new HashMap();
	
	static {
		rendMap.put("bold", new Integer(TextRun.BOLD));
		rendMap.put("italic", new Integer(TextRun.ITALIC));
		rendMap.put("extended", new Integer(TextRun.EXTENDED));
		rendMap.put("sperrtext", new Integer(TextRun.EXTENDED));
		rendMap.put("underline", new Integer(TextRun.UNDERLINE));
		rendMap.put("overline", new Integer(TextRun.OVERLINE));
		rendMap.put("macron", new Integer(TextRun.OVERLINE));
		rendMap.put("superscript", new Integer(TextRun.SUPERSCRIPT));
		rendMap.put("subscript", new Integer(TextRun.SUBSCRIPT));
		rendMap.put("monospaced", new Integer(TextRun.MONOSPACED));
	}
	
	/**	Marginalia, or null if none. */
	
	private Text marginaliaText;
	
	/**	Error class. */
	
	public static class Error extends Exception {
	
		/** Creates a new error.
		 *
		 *	@param	msg		Error message.
		 */
		 
		 public Error (String msg) {
			super(msg);
		 }
	
	}
	
	/**	Style context class. */

	private class StyleContext implements Cloneable {
		
		/**	Style. */
		
		private int style = 0;
		
		/**	Sets the normal style.
		 */
		
		public void setNormal () {
			style = 0;
		}
	
		/**	Sets a style.
		 *
		 *	@param	mask		Style mask.
		 */
		 
		public void setStyle (int mask) {
			style |= mask;
		}
		
		/**	Appends text.
		 *
		 *	@param	text		Text.
		 */
		
		private void appendText (String text) {
			TextRun run = new TextRun();
			run.setCharset(charset);
			byte runSize = size;
			if ((style & TextRun.SUBSCRIPT) != 0 ||
				(style & TextRun.SUPERSCRIPT) != 0)
					runSize = (byte)(size * 0.75);
			run.setSize(runSize);
			run.setStyle(style);
			run.setText(text);
			appendRun(run);
		}
	
		/**	Clones the context. */
		
		public Object clone () {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError(e.toString());
			}
		}
		
	}

	/**	Creates a new paragraph.
	 *
	 *	@param	el			"p" or "seg" XML element.
	 *
	 *	@param	charset		Character set.
	 *
	 *	@param	size		Font size.
	 *
	 *	@throws Error	general error.
	 */
	 
	public BuildParagraph (Element el, byte charset, byte size) 
		throws Error
	{
		super();
		this.charset = charset;
		this.size = size;
		StyleContext context = new StyleContext();
		boolean isSeg = el.getNodeName().equals("seg");
		String rendAttr = el.getAttribute("rend");
		if (!isSeg && rendAttr.length() > 0) processRend(context, el);
		processAlign(el);
		processIndent(el);
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.TEXT_NODE) {
				String str = child.getNodeValue();
				context.appendText(str);
			} else if (type == Node.ELEMENT_NODE) {
				el = (Element)child;
				String name = el.getNodeName();
				if (name.equals("hi")) {
					processHi(context, el);
				} else if (name.equals("title")) {
					processTitle(context, el);
				} else if (!isSeg && name.equals("seg")) {
					processSeg(context, el);
				} else {
					throw new Error("p element invalid child: " + name);
				}
			}
		}
		setMarginalia(marginaliaText);
		finalize();
	}
	
	/**	Processes a rend attribute.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			Element.
	 *
	 *	@throws	Error
	 */
	 
	private void processRend (StyleContext context, Element el)
		throws Error
	{
		String rend = el.getAttribute("rend");
		if (rend.equals("normal") || rend.equals("roman") ||
			rend.equals("plain")) 
		{
			context.setNormal();
		} else {
			Integer maskInteger = (Integer)rendMap.get(rend);
			if (maskInteger == null) {
				throw new Error("Invalid rend attribute value: " + rend);
			} else {
				context.setStyle(maskInteger.intValue());
			}
		}
	}
	
	/**	Processes an align attribute.
	 *
	 *	@param	el			Element.
	 *
	 *	@throws	Error
	 */
	 
	private void processAlign (Element el)
		throws Error
	{
		String align = el.getAttribute("align");
		if (align == null || align.length() == 0) return;
		if (align.equals("left")) {
			return;
		} else if (align.equals("center")) {
			setJustification(TextLine.CENTER);
		} else {
			throw new Error("Invalid align attribute value: " + align);
		}
	}
	
	/**	Processes an indent attribute.
	 *
	 *	@param	el			Element.
	 *
	 *	@throws	Error
	 */
	 
	private void processIndent (Element el)
		throws Error
	{
		String indentStr = el.getAttribute("indent");
		if (indentStr == null || indentStr.length() == 0) return;
		try {
			int indent = Integer.parseInt(indentStr);
			setIndentation(indent);
		} catch (NumberFormatException e) {
			throw new Error("Illegal value for indent attribute: " + 
				indentStr);
		}
	}
	
	/**	Processes an hi element.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"hi" or "title" element.
	 *
	 *	@throws Error
	 */
	 
	private void processHi (StyleContext context, Element el)
		throws Error
	{
		context = (StyleContext)context.clone();	
		processRend(context, el);
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.TEXT_NODE) {
				String str = child.getNodeValue();
				context.appendText(str);
			} else if (type == Node.ELEMENT_NODE) {
				el = (Element)child;
				String name = el.getNodeName();
				if (name.equals("hi")) {
					processHi(context, el);
				} else if (name.equals("title")) {
					processTitle(context, el);
				} else {
					throw new Error("hi element invalid child: " + name);
				}
			}
		}
	}
	
	/**	Processes a title element.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"title" element.
	 *
	 *	@throws	Error
	 */
	 
	private void processTitle (StyleContext context, Element el)
		throws Error
	{
		context = (StyleContext)context.clone();
		context.setStyle(TextRun.ITALIC);
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.TEXT_NODE) {
				String str = child.getNodeValue();
				context.appendText(str);
			} else if (type == Node.ELEMENT_NODE) {
				el = (Element)child;
				String name = el.getNodeName();
				if (name.equals("hi")) {
					processHi(context, el);
				} else if (name.equals("title")) {
					processTitle(context, el);
				} else {
					throw new Error("title element invalid child: " + name);
				}
			}
		}
	}
	
	/**	Processes a seg element.
	 *
	 *	@param	context		Parsing context.
	 *
	 *	@param	el			"seg" element.
	 *
	 *	@throws Error
	 */
	 
	private void processSeg (StyleContext context, Element el)
		throws Error
	{
		String typeAttr = el.getAttribute("type");
		if (typeAttr.length() == 0) 
			throw new Error("seg element missing required type attribute");
		if (!typeAttr.equals("marginalia"))
			throw new Error("seg element invalid type attribute: " +
				typeAttr);
		if (marginaliaText == null) marginaliaText = new Text();
		TextLine marginaliaLine = new BuildParagraph(el, charset, size);
		marginaliaText.copyLine(marginaliaLine);
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

