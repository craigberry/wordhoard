package edu.northwestern.at.wordhoard.swing.dialogs;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	The go to dialog.
 */

public class GoToDialog extends ModalDialog {

	/**	True if dialog canceled. */

	private boolean canceled  = true;

	/**	The word tag. */

	private String tag;

	/**	Creates a new Go To dialog.
	 *
	 *	@param	str				Initial word tag string for dialog field,
	 *							or null if none.
	 *
	 *	@param	parentWindow	Parent window.
	 */

	public GoToDialog (String str, AbstractWindow parentWindow) {
		super("Go To", parentWindow);
		if (str == null) str = "";
		final JTextField tagField = new JTextField(str, 20);
		tagField.selectAll();
		LabeledColumn col = new LabeledColumn();
		col.addPair("Word tag", tagField);
		add(col);
		addButton("Cancel",
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						canceled = true;
						dispose();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		addDefaultButton("OK",
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						tag = tagField.getText();
						canceled = false;
						dispose();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		setResizable(false);
		setInitialFocus(tagField);
		show(parentWindow);
	}

	/**	Returns true if the dialog was canceled.
	 *
	 *	@return		True if canceled.
	 */

	public boolean canceled () {
		return canceled;
	}

	/**	Returns the word tag.
	 *
	 *	@return		The word tag.
	 */

	public String getTag () {
		return tag;
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

