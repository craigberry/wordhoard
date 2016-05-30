package edu.northwestern.at.wordhoard.swing.dialogs;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.rmi.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.server.*;
import edu.northwestern.at.wordhoard.server.model.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	The login dialog.
 */

public class LoginDialog extends ModalDialog {

	/**	Login account, or null if none. */

	private static Account account;

	/**	True if dialog canceled. */

	private boolean canceled  = true;

	/**	Creates a new login dialog.
	 *
	 *	@param	parentWindow	Parent window.
	 */

	public LoginDialog (Frame parentWindow) {
		super("Login", parentWindow);
		final JTextField usernameField = new JTextField("", 20);
		final JPasswordField passwordField = new JPasswordField("", 20);
		LabeledColumn col = new LabeledColumn();
		col.addPair("Username", usernameField);
		col.addPair("Password", passwordField);
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
		addDefaultButton("Login",
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						String username = usernameField.getText();
						String password =
							new String(passwordField.getPassword());
						boolean loginSuccessful =
							login(username, password, LoginDialog.this,
								false);
						if (!loginSuccessful) return;
						canceled = false;
						dispose();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		setResizable(false);
		setInitialFocus(usernameField);
		show(parentWindow);
	}

	/**	Logs in.
	 *
	 *	@param	username		Username.
	 *
	 *	@param	password		Password.
	 *
	 *	@param	parentDialog	Parent dialog.  Null if none.
	 *
	 *	@param	quiet			If true, do not display error messages.
	 *
	 *	@return		True if login successful.
	 */

	public static boolean login (String username, String password,
		JDialog parentDialog, boolean quiet) {
		try {
			account = null;
			WordHoardSession session = WordHoard.getSession();
			if (session == null) throw new RemoteException();
			account = session.login(username, password);
			if (account == null) {
				if (!quiet) new ErrorMessage(
					"Invalid username or password.",
					parentDialog);
				return false;
			} else {
				return true;
			}
		} catch (RemoteException e) {
			account = null;
			if (!quiet) new ErrorMessage(
				"Could not connect to WordHoard server.",
				parentDialog);
			return false;
		}
	}

	/**	Logs in.
	 *
	 *	@param	username		Username.
	 *
	 *	@param	password		Password.
	 *
	 *	@return		True if login successful.
	 */

	public static boolean login (String username, String password) {
		return login(username, password, null, false);
	}

	/**	Returns true if the dialog was canceled.
	 *
	 *	@return		True if canceled.
	 */

	public boolean canceled () {
		return canceled;
	}

	/**	Gets the login account.
	 *
	 *	@return		Login account, or null if none.
	 */

	public static Account getLoginAccount () {
		return account;
	}

	/**	Sets the login account.
	 *
	 *	@param	account		Login account, or null if none.
	 */

	public static void setLoginAccount (Account account) {
		LoginDialog.account = account;
	}

	/**	Logs out.
	 */

	public static void logout () {
		try {
			WordHoardSession session = WordHoard.getSession();
			if (session == null) throw new RemoteException();
			session.logout();
		} catch (RemoteException e) {
			new ErrorMessage("Could not connect to WordHoard server.");
		}
		account = null;
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

