package edu.northwestern.at.wordhoard.tools.cm;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;

import edu.northwestern.at.wordhoard.tools.*;

/**	Writes a WordHoard XML file.
 */

public class XMLWriter {

	/** XML output file PrintWriter. */

	private PrintWriter out;

	/**	Indentation level. */

	private int indent;

	/**	True if writer is active. */

	private boolean active = true;

	/**	Element name pushdown stack. */

	private String[] elStack = new String[100];

	/**	Inactive observable. */

	private Observable inactiveObservable =
		new Observable() {
			public void notifyObservers () {
				setChanged();
				super.notifyObservers();
			}
		};

	/**	Creates a new XMLWriter.
	 *
	 *	@param	file	XML output file.
	 *
	 *	@throws	Exception
	 */

	public XMLWriter (File file)
		throws Exception
	{
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
		BufferedWriter bw = new BufferedWriter(osw);
		out = new PrintWriter(bw);
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
	}

	/**	Closes the XMLWriter.
	 *
	 *	@throws	Exception
	 */

	public void close ()
		throws Exception
	{
		out.close();
		if (indent != 0)
			BuildUtils.emsg("WordHoard XML output file error: Unclosed elements " +
				"at end of file");
	}

	/**	Sets the writer active or inactive.
	 *
	 *	<p>If the writer is set inactive, any attempt to write to the file is
	 *	intercepted and ignored, and any inactive observers are notified.
	 *
	 *	@param	active		True if active, false if inactive.
	 */

	public void setActive (boolean active) {
		this.active = active;
	}

	/**	Gets the writer active status.
	 *
	 *	@return		True if writer active.
	 */

	public boolean getActive () {
		return active;
	}

	/**	Adds an inactive observer.
	 *
	 *	<p>The observer is notified whenever an attempt is made to write to the file
	 *	when the writer is inactive.
	 *
	 *	@param	o	Observer.
	 */

	public void addInactiveObserver (Observer o) {
		inactiveObservable.addObserver(o);
	}

	/**	Deletes all inactive observers.
	 */

	public void deleteInactiveObservers () {
		inactiveObservable.deleteObservers();
	}

	/**	Escapes special XML characters.
	 *
	 *	@param	str		String.
	 *
	 *	@return			String with special characters escaped.
	 */

	public static String escapeXML (String str) {
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		return str;
	}

	/**	Writes a string.
	 *
	 *	@param	str		String.
	 */

	public void writeString (String str) {
		if (!active) {
			inactiveObservable.notifyObservers();
			return;
		}
		for (int i = 0; i < indent; i++) out.print('\t');
		out.println(str);
	}

	/**	Writes an escaped string.
	 *
	 *	@param	str		String.
	 */

	public void writeEscapedString (String str) {
		writeString(escapeXML(str));
	}

	/**	Starts an element.
	 *
	 *	@param	name	Element name.
	 *
	 *	@param	attrs	Attributes string, or null if none.
	 */

	private void startEl (String name, String attrs) {
		if (attrs == null) {
			writeString("<" + name + ">");
		} else {
			writeString("<" + name + " " + attrs + ">");
		}
		if (active) elStack[indent] = name;
		indent++;
	}

	/**	Starts an element.
	 *
	 *	@param	name	Element name.
	 */

	public void startEl (String name) {
		startEl(name, null);
	}

	/**	Starts an element.
	 *
	 *	@param	name	Element name.
	 *
	 *	@param	a1		Attribute name.
	 *
	 *	@param	v1		Attribute value.
	 */

	public void startEl (String name, String a1, String v1) {
		v1 = escapeXML(v1);
		startEl(name, a1 + "=\"" + v1 + "\"");
	}

	/**	Starts an element.
	 *
	 *	@param	name	Element name.
	 *
	 *	@param	a1		First attribute name.
	 *
	 *	@param	v1		First attribute value.
	 *
	 *	@param	a2		Second attribute name.
	 *
	 *	@param	v2		Second attribute value.
	 */

	public void startEl (String name, String a1, String v1, String a2, String v2) {
		v1 = escapeXML(v1);
		v2 = escapeXML(v2);
		startEl(name, a1 + "=\"" + v1 + "\" " + a2 + "=\"" + v2 + "\"");
	}

	/**	Starts an element.
	 *
	 *	@param	name	Element name.
	 *
	 *	@param	a1		First attribute name.
	 *
	 *	@param	v1		First attribute value.
	 *
	 *	@param	a2		Second attribute name.
	 *
	 *	@param	v2		Second attribute value.
	 *
	 *	@param	a3		Third attribute name.
	 *
	 *	@param	v3		Third attribute value.
	 */

	public void startEl (String name, String a1, String v1, String a2,
		String v2, String a3, String v3) {
		v1 = escapeXML(v1);
		v2 = escapeXML(v2);
		v3 = escapeXML(v3);
		startEl(name, a1 + "=\"" + v1 + "\" " + a2 + "=\"" + v2 + "\" " +
			a3 + "=\"" + v3 + "\"" );
	}

	/**	Starts an element.
	 *
	 *	@param	name	Element name.
	 *
	 *	@param	a1		First attribute name.
	 *
	 *	@param	v1		First attribute value.
	 *
	 *	@param	a2		Second attribute name.
	 *
	 *	@param	v2		Second attribute value.
	 *
	 *	@param	a3		Third attribute name.
	 *
	 *	@param	v3		Third attribute value.
	 *
	 *	@param	a4		Fourth attribute name.
	 *
	 *	@param	v4		Fourth attribute value.
	 */

	public void startEl (String name, String a1, String v1, String a2,
		String v2, String a3, String v3, String a4, String v4) {
		v1 = escapeXML(v1);
		v2 = escapeXML(v2);
		v3 = escapeXML(v3);
		startEl(name, a1 + "=\"" + v1 + "\" " + a2 + "=\"" + v2 + "\" " +
			a3 + "=\"" + v3 + "\" " + a4 + "=\"" + v4 + "\"" );
	}

	/**	Ends an element.
	 *
	 *	@param	name	Element name.
	 */

	public void endEl (String name) {
		indent--;
		writeString("</" + name + ">");
		if (active && !elStack[indent].equals(name))
			BuildUtils.emsg("WordHoard XML output file error: Element " + elStack[indent] +
				" has incorrect matching end tag " + name);
	}

	/**	Writes a text element.
	 *
	 *	@param	name	Element name.
	 *
	 *	@param	str		Text contents of element.
	 */

	public void writeTextEl (String name, String str) {
		str = escapeXML(str);
		writeString("<" + name + ">" + str + "</" + name + ">");
	}

	/**	Writes a text element.
	 *
	 *	@param	name	Element name.
	 *
	 *	@param	a1		First attribute name.
	 *
	 *	@param	v1		First attribute value.
	 *
	 *	@param	str		Text contents of element.
	 */

	public void writeTextEl (String name, String a1, String v1, String str) {
		v1 = escapeXML(v1);
		str = escapeXML(str);
		writeString("<" + name + " " + a1 + "=\"" + v1 + "\">" + str + "</" + name + ">");
	}

	/**	Writes a text element.
	 *
	 *	@param	name	Element name.
	 *
	 *	@param	a1		First attribute name.
	 *
	 *	@param	v1		First attribute value.
	 *
	 *	@param	a2		Second attribute name.
	 *
	 *	@param	v2		Second attribute value.
	 *
	 *	@param	str		Text contents of element.
	 */

	public void writeTextEl (String name, String a1, String v1, String a2, String v2,
		String str)
	{
		v1 = escapeXML(v1);
		v2 = escapeXML(v2);
		str = escapeXML(str);
		writeString("<" + name + " " + a1 + "=\"" + v1 + "\" " + a2 + "=\"" + v2 +
			"\">" + str + "</" + name + ">");
	}

	/**	Writes a text element.
	 *
	 *	@param	name	Element name.
	 *
	 *	@param	a1		First attribute name.
	 *
	 *	@param	v1		First attribute value.
	 *
	 *	@param	a2		Second attribute name.
	 *
	 *	@param	v2		Second attribute value.
	 *
	 *	@param	a3		Second attribute name.
	 *
	 *	@param	v3		Second attribute value.
	 *
	 *	@param	str		Text contents of element.
	 */

	public void writeTextEl (String name, String a1, String v1, String a2, String v2,
		String a3, String v3, String str)
	{
		v1 = escapeXML(v1);
		v2 = escapeXML(v2);
		v3 = escapeXML(v3);
		str = escapeXML(str);
		writeString("<" + name + " " + a1 + "=\"" + v1 + "\" " + a2 + "=\"" + v2 +
			"\" " + a3 + "=\"" + v3 + "\">" + str + "</" + name + ">");
	}

	/**	Writes a text element.
	 *
	 *	@param	name	Element name.
	 *
	 *	@param	a1		First attribute name.
	 *
	 *	@param	v1		First attribute value.
	 *
	 *	@param	a2		Second attribute name.
	 *
	 *	@param	v2		Second attribute value.
	 *
	 *	@param	a3		Second attribute name.
	 *
	 *	@param	v3		Second attribute value.
	 *
	 *	@param	str		Text contents of element.
	 */

	public void writeTextEl (String name, String a1, String v1, String a2, String v2,
		String a3, String v3, String a4, String v4, String str)
	{
		v1 = escapeXML(v1);
		v2 = escapeXML(v2);
		v3 = escapeXML(v3);
		v4 = escapeXML(v4);
		str = escapeXML(str);
		writeString("<" + name + " " + a1 + "=\"" + v1 + "\" " +
			a2 + "=\"" + v2 + "\" " +
			a3 + "=\"" + v3 + "\" " +
			a4 + "=\"" + v4 + "\">" + str +
			"</" + name + ">");
	}

	/**	Writes an empty element.
	 *
	 *	@param	name	Element name.
	 */

	public void writeEmptyEl (String name) {
		writeString("<" + name + "/>");
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


