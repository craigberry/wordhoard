package edu.northwestern.at.wordhoard.swing.accounts;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.server.model.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	The right panel of the manage accounts window.
 */
 
class RightPanel extends JPanel {

	/**	Account model. */
	
	private AccountModel model;
	
	/**	Account being edited, or null if a new account is being created. */
	
	private Account account;
	
	/**	Username field. */
	
	private JTextField usernameField = new JTextField(20);
	
	/**	Name field. */
	
	private JTextField nameField = new JTextField(20);
	
	/**	Password field. */
	
	private JPasswordField passwordField = new JPasswordField(20);
	
	/**	NU account checkbox. */
	
	private JCheckBox nuCheckBox = new JCheckBox("NU account");
	
	/**	Can manage accounts checkbox. */
	
	private JCheckBox maCheckBox = new JCheckBox("Can manage accounts");
	
	/** Save button (Create or Update). */
	
	private JButton saveButton;
	
	/**	Dialog panel. */
	
	private DialogPanel dlog = new DialogPanel();

	/**	Creates a new right panel.
	 *
	 *	@param	model		Account model.
	 */
	 
	RightPanel (final AccountModel model) {
	
		this.model = model;
		
		LabeledColumn col = new LabeledColumn();
		col.addPair("Username", usernameField);
		col.addPair("Name", nameField);
		col.addPair("Password", passwordField);
	
		dlog.add(col);
		dlog.add(nuCheckBox, 10);
		dlog.add(maCheckBox);
		
		saveButton = dlog.addButton("Save",
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						save();
					} catch (Exception e) {
						model.err(e);
					}
				}
			}
		);
		saveButton.setEnabled(false);

		dlog.setMaximumSize(dlog.getPreferredSize());
	
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(dlog);
		
		model.addListener(
			new AccountAdapter() {
				public void selectionChanged (int[] selection) {
					handleSelectionChanged();
				}
			}
		);
		
		DocumentListener docListener =
			new DocumentListener() {
				public void changedUpdate (DocumentEvent event) {
					adjustSaveButton();
				}
				public void insertUpdate (DocumentEvent event) {
					adjustSaveButton();
				}
				public void removeUpdate (DocumentEvent event) {
					adjustSaveButton();
				}
			};
			
		usernameField.getDocument().addDocumentListener(docListener);
		nameField.getDocument().addDocumentListener(docListener);
		passwordField.getDocument().addDocumentListener(docListener);
		
		ActionListener actionListener =
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					adjustSaveButton();
				}
			};
			
		nuCheckBox.addActionListener(actionListener);
		maCheckBox.addActionListener(actionListener);

	}
	
	/**	Saves an account. 
	 *
	 *	@throws Exception
	 */
	
	private void save () 
		throws Exception
	{
		String username = usernameField.getText();
		String name = nameField.getText();
		String password = new String(passwordField.getPassword());
		boolean nuAccount = nuCheckBox.isSelected();
		boolean canManageAccounts = maCheckBox.isSelected();
		model.save(username, name, password, nuAccount, canManageAccounts);
	}
	
	/**	Handles a selection changed event. */
	
	private void handleSelectionChanged() {
		boolean creatingNewAccount = model.getCreatingNewAccount();
		account = model.getSelectedAccount();
		if (creatingNewAccount) {
			usernameField.setText("");
			nameField.setText("");
			passwordField.setText("");
			nuCheckBox.setSelected(false);
			maCheckBox.setSelected(false);
			saveButton.setText("Create");
			saveButton.setEnabled(false);
			dlog.setVisible(true);
			usernameField.requestFocus();
		} else if (account == null) {
			dlog.setVisible(false);
		} else {
			usernameField.setText(account.getUsername());
			nameField.setText(account.getName());
			passwordField.setText("");
			nuCheckBox.setSelected(account.getNuAccount());
			maCheckBox.setSelected(account.getCanManageAccounts());
			saveButton.setText("Update");
			saveButton.setEnabled(false);
			dlog.setVisible(true);
		}
	}
	
	/**	Adjusts (enables/disables) the Save button.
	 */

	private void adjustSaveButton () {
		if (account == null) {
			saveButton.setEnabled(
				usernameField.getText().length() > 0);
		} else {
			saveButton.setEnabled(
				!usernameField.getText().equals(account.getUsername()) ||
				!nameField.getText().equals(account.getName()) ||
				passwordField.getPassword().length > 0 ||
				nuCheckBox.isSelected() != account.getNuAccount() ||
				maCheckBox.isSelected() != account.getCanManageAccounts());
		}
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

