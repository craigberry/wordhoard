package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.text.*;

/**	A JPasswordField with different defaults and behavior.
 *
 *	<ul>
 *	<li>The caret position is reset to 0 whenever new text is set.
 *	</ul>
 *
 *	<p>The constructors are the same as in JPasswordField. We did not bother
 *	giving them their own javadoc.
 */

public class XPasswordField extends JPasswordField {

	public XPasswordField () {
		super();
	}

	public XPasswordField (Document doc, String text, int columns) {
		super(doc, text, columns);
	}

	public XPasswordField (int columns) {
		super(columns);
	}

	public XPasswordField (String text) {
		super(text);
	}

	public XPasswordField (String text, int columns) {
		super(text, columns);
	}

	/**	Sets the password text and resets the caret position to 0.
	 *
	 *	@param	t		The new text for the password field.
	 */

	public void setText (String t) {
		super.setText(t);
		setCaretPosition(0);
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

