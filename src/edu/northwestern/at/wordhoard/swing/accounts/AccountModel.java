package edu.northwestern.at.wordhoard.swing.accounts;

/*	Please see the license information at the end of this file. */

import javax.swing.table.*;
import java.util.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.server.*;
import edu.northwestern.at.wordhoard.server.model.*;
import edu.northwestern.at.utils.swing.*;

/**	Account model.
 *
 *	<p>Each 
 *	{@link edu.northwestern.at.wordhoard.swing.accounts.ManageAccountsWindow
 *	manage accounts window} has a model which acts as a Swing table model,
 *	manages all communications with the server session, and
 *	maintains the following state information for the window:
 *
 *	<ul>
 *	<li>The list of all current accounts.
 *	<li>The current selection - an array of indexes in the list of the 
 *		selected accounts.
 *	<li>Whether or not we are currently creating a new account.
 *	</ul>
 *
 *	<p>Window components register 
 *	{@link edu.northwestern.at.wordhoard.swing.accounts.AccountListener
 *	listeners} on the model to listen for and react appropriately to changes 
 *	in the state of the window.
 */
 
class AccountModel extends AbstractTableModel {

	/**	Server session. */
	
	private WordHoardSession session;
	
	/**	Parent window. */
	
	private AbstractWindow parentWindow;

	/**	List of accounts. */
	
	private ArrayList accounts;
	
	/**	The selection - an array of indexes of selected accounts. */
	
	private int[] selection;
	
	/**	True if we are currently creating a new account. */
	
	private boolean creatingNewAccount;
	
	/**	Set of listeners. */
	
	private HashSet listeners = new HashSet();
	
	/**	Creates a new account model. 
	 *
	 *	@param	session			Server session.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws Exception	general error.
	 */
	
	AccountModel (WordHoardSession session, AbstractWindow parentWindow)
		throws Exception
	{
		this.session = session;
		this.parentWindow = parentWindow;
		accounts = new ArrayList(session.getAccounts());
	}
	
	/**	Gets the number of rows.
	 *
	 *	@return		The number of rows.
	 */
	
	public int getRowCount () {
		return accounts.size();
	}
	
	/**	Gets the number of columns.
	 *
	 *	@return		The number of columns.
	 */
	
	public int getColumnCount () {
		return 4;
	}
	
	/**	Gets the value of a cell.
	 *
	 *	@param	row		Row index.
	 *
	 *	@param	col		Column index.
	 *
	 *	@return		Value of cell.
	 */
	
	public Object getValueAt (int row, int col) {
		Account account = (Account)accounts.get(row);
		switch (col) {
			case 0: return account.getUsername();
			case 1: return account.getName();
			case 2: return account.getNuAccount() ? "x" : "";
			case 3: return account.getCanManageAccounts() ? "x" : "";
		}
		return null;
	}
	
	/**	Gets the name of a column.
	 *
	 *	@param	col		Column index.
	 *
	 *	@return		Name of column.
	 */
	
	public String getColumnName (int col) {
		switch (col) {
			case 0: return "Username";
			case 1: return "Name";
			case 2: return "NU";
			case 3: return "MA";
		}
		return null;
	}
	
	/**	Initializes the model. */
	
	void init () {
		if (accounts.size() == 0) {
			setSelection(new int[0]);
		} else {
			setSelection(new int[] {0});
		}
	}
	
	/**	Gets the selection.
	 *
	 *	@return		Array of indexes of selected accounts.
	 */
	 
	int[] getSelection () {
		return selection;
	}
	
	/**	Sets the selection.
	 *
	 *	@param	selection		Array of indexes of selected accounts.
	 */
	 
	void setSelection (int[] selection) {
		this.selection = selection;
		creatingNewAccount = creatingNewAccount && selection.length == 0;
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			AccountListener listener = (AccountListener)it.next();
			listener.selectionChanged(selection);
		}
	}
	
	/**	Gets the index of the currently selected account.
	 *
	 *	@return		The index of the currently selected account, or -1
	 *				if the selection is empty or has more than one account.
	 */
	 
	private int getSelectedAccountIndex () {
		if (selection == null || selection.length != 1) return -1;
		return selection[0];
	}
	
	/**	Gets the currently selected account.
	 *
	 *	@return		The currently selected account, or null if the 
	 *				selection is empty or has more than one account.
	 */
	
	Account getSelectedAccount () {
		int i = getSelectedAccountIndex();
		return i == -1 ? null : (Account)accounts.get(i);
	}
	
	/**	Saves an account.
	 *
	 *	@param	username			Username.
	 *	
	 *	@param	name				Name.
	 *
	 *	@param	password			Password. For an existing account, 
	 *								a null or empty value leaves the password 
	 *								unchanged.
	 *
	 *	@param	nuAccount			True if NU account.
	 *
	 *	@param	canManageAccounts	True if user is permitted to manage
	 *								accounts.
	 *
	 *	@throws Exception	general error.
	 */
	 
	void save (String username, String name, String password,
		boolean nuAccount, boolean canManageAccounts)
			throws Exception
	{
		username = username.trim();
		name = name.trim();
		password = password.trim();
		if (creatingNewAccount) {
			// Create a new account.
			Account account = new Account();
			account.setUsername(username);
			account.setName(name);
			account.setPassword(password);
			account.setNuAccount(nuAccount);
			account.setCanManageAccounts(canManageAccounts);
			Long id = session.createOrUpdateAccount(account);
			account.setId(id);
			addAccountToList(account);
		} else {
			// Update an existing account.
			Account oldAccount = getSelectedAccount();
			String oldUsername = oldAccount.getUsername();
			Account account = (Account)oldAccount.clone();
			account.setUsername(username);
			account.setName(name);
			if (password != null && password.length() > 0)
				account.setPassword(password);
			account.setNuAccount(nuAccount);
			account.setCanManageAccounts(canManageAccounts);
			session.createOrUpdateAccount(account);
			int i = getSelectedAccountIndex();
			if (oldUsername.equalsIgnoreCase(username)) {
				accounts.set(i, account);
				fireTableRowsUpdated(i, i);
			} else {
				accounts.remove(i);
				fireTableRowsDeleted(i, i);
				addAccountToList(account);
			}
		}
		creatingNewAccount = false;
	}
	
	/**	Adds an account to the account list.
	 *
	 *	<p>The account is added to the list in the proper position to
	 *	maintain increasing alphabetical order.
	 *
	 *	@param	account		Account to add to list.
	 */
	 
	private void addAccountToList (Account account) {
		String username = account.getUsername();
		int i = 0;
		for (Iterator it = accounts.iterator(); it.hasNext(); ) {
			Account listEl = (Account)it.next();
			int k = username.compareToIgnoreCase(listEl.getUsername());
			if (k <= 0) break;
			i++;
		}
		accounts.add(i, account);
		fireTableRowsInserted(i, i);
		selectAccount(i);
	}
	
	/**	Requests that an account be selected.
	 *
	 *	@param	index			Index of account to be selected.
	 */
	 
	public void selectAccount (int index) {
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			AccountListener listener = (AccountListener)it.next();
			listener.selectAccount(index);
		}
	}
	
	/**	Creates a new account.
	 */
	 
	void createAccount () {
		creatingNewAccount = true;
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			AccountListener listener = (AccountListener)it.next();
			listener.clearSelection();
		}
		setSelection(new int[0]);
	}
	
	/**	Returns true if we are creating a new account.
	 *
	 *	@return			True if creating new account.
	 */
	 
	boolean getCreatingNewAccount () {
		return creatingNewAccount;
	}
	
	/**	Deletes the selected accounts.
	 *
	 *	@throws	Exception	general error.
	 */
	 
	void deleteAccounts () 
		throws Exception
	{
		Arrays.sort(selection);
		int first = selection.length == 0 ? -1 : selection[0];
		for (int k = selection.length-1; k >= 0; k--) {
			int i = selection[k];
			Account account = (Account)accounts.get(i);
			session.deleteAccount(account.getId());
			accounts.remove(i);
			fireTableRowsDeleted(i, i);
		}
		if (first == accounts.size()) first--;
		if (first >= 0) selectAccount(first);
	}
	
	/**	Adds a listener. 
	 *
	 *	@param	listener	Account listener.
	 */
	 
	void addListener (AccountListener listener) {
		listeners.add(listener);
	}
	
	/**	Removes a listener.
	 *
	 *	@param	listener	Account listener.
	 */
	 
	void removeListener (AccountListener listener) {
		listeners.remove(listener);
	}
	
	/**	Handles an exception.
	 *
	 *	@param	e		Exception.
	 */
	 
	void err (Exception e) {
		if (e instanceof WordHoardError) {
			new ErrorMessage(e.getMessage(), parentWindow);
		} else {
			Err.err(e);
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

