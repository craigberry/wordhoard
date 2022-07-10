package edu.northwestern.at.wordhoard.swing.accounts.groups;

/*	Please see the license information at the end of this file. */

import javax.swing.table.*;
import java.util.*;

import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.server.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.UserGroupUtils;

/**	Group model.
 *
 *	<p>Each 
 *	{@link edu.northwestern.at.wordhoard.swing.accounts.groups.GroupPanel
 *	group panel} has a model which acts as a Swing table model,
 *	manages all communications with the server session, and
 *	maintains the following state information for the panel:
 *
 *	<ul>
 *	<li>The list of all current groups.
 *	<li>The current selection - an array of indexes in the list of the 
 *		selected groups.
 *	<li>Whether or not we are currently creating a new userGroup.
 *	</ul>
 *
 *	<p>Window components register 
 *	{@link edu.northwestern.at.wordhoard.swing.accounts.groups.GroupListener
 *	listeners} on the model to listen for and react appropriately to changes 
 *	in the state of the window.
 */
 
public class GroupModel extends AbstractTableModel {

	/**	Server session. */
	
	private WordHoardSession session;
	
	/**	Parent window. */
	
	private AbstractWindow parentWindow;

	/**	List of groups. */
	
	private ArrayList groups;
	
	/**	The selection - an array of indexes of selected groups. */
	
	private int[] selection;
	
	/**	True if we are currently creating a new userGroup. */
	
	private boolean creatingNewGroup = false;
	
	/**	Set of listeners. */
	
	private HashSet listeners = new HashSet();
	
	/**	Creates a new userGroup model. 
	 *
	 *	@param	session			Server session.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws Exception	general error.
	 */
	
	public GroupModel (WordHoardSession session, AbstractWindow parentWindow)
		throws Exception
	{
		this.session = session;
		this.parentWindow = parentWindow;
		groups = new ArrayList(UserGroupUtils.getUserGroups());
	}
	
	/**	Gets the number of rows.
	 *
	 *	@return		The number of rows.
	 */
	
	public int getRowCount () {
		return groups.size();
	}
	
	/**	Gets the number of columns.
	 *
	 *	@return		The number of columns.
	 */
	
	public int getColumnCount () {
		return 1;
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
		UserGroup userGroup = (UserGroup)groups.get(row);
		switch (col) {
		//	case 0: return userGroup.getTitle();
			case 0: return userGroup;
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
			case 0: return "Name";
		}
		return null;
	}
	
	/**	Initializes the model. */
	
	public void init () {
		if (groups.size() == 0) {
			setSelection(new int[0]);
		} else {
			setSelection(new int[] {0});
		}
	}
	
	/**	Gets the selection.
	 *
	 *	@return		Array of indexes of selected groups.
	 */
	 
	int[] getSelection () {
		return selection;
	}
	
	/**	Sets the selection.
	 *
	 *	@param	selection		Array of indexes of selected groups.
	 */
	 
	void setSelection (int[] selection) {
		this.selection = selection;
		creatingNewGroup = creatingNewGroup && selection.length == 0;
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			GroupListener listener = (GroupListener)it.next();
			listener.selectionChanged(selection);
		}
	}
	
	/**	Gets the index of the currently selected userGroup.
	 *
	 *	@return		The index of the currently selected userGroup, or -1
	 *				if the selection is empty or has more than one userGroup.
	 */
	 
	private int getSelectedGroupIndex () {
		if (selection == null || selection.length != 1) return -1;
		return selection[0];
	}
	
	/**	Gets the currently selected userGroup.
	 *
	 *	@return		The currently selected userGroup, or null if the 
	 *				selection is empty or has more than one userGroup.
	 */
	
	UserGroup getSelectedGroup () {
		int i = getSelectedGroupIndex();
		return i == -1 ? null : (UserGroup)groups.get(i);
	}
	
	/**	Saves a userGroup.
	 *
	 *	@param	name				Name.
	 *
	 *	@param	owner				User name for owner 
	 *
	 *	@throws Exception	general error.
	 */
	 
	void save (String name, String owner)
			throws Exception
	{
		name = name.trim();
		owner = owner.trim();
		if (creatingNewGroup) {
			// Create a new userGroup.

			UserGroup userGroup = new UserGroup(name,owner);
			userGroup = UserGroupUtils.createUserGroup(userGroup);
			addGroupToList(userGroup);
		} else {
			// Update an existing userGroup.
			UserGroup userGroup = getSelectedGroup();
			String oldTitle = userGroup.getTitle();
			userGroup.setTitle(name);
			userGroup.setOwner(owner);
			UserGroupUtils.updateUserGroup(userGroup);
			int i = getSelectedGroupIndex();
			if (oldTitle.equalsIgnoreCase(name)) {
				groups.set(i, userGroup);
				fireTableRowsUpdated(i, i);
			} else {
				groups.remove(i);
				fireTableRowsDeleted(i, i);
				addGroupToList(userGroup);
			}
		}
		creatingNewGroup = false;
	}
	
	/**	Adds an userGroup to the userGroup list.
	 *
	 *	<p>The userGroup is added to the list in the proper position to
	 *	maintain increasing alphabetical order.
	 *
	 *	@param	userGroup		Group to add to list.
	 */
	 
	private void addGroupToList (UserGroup userGroup) {
		String title = userGroup.getTitle();
		int i = 0;
		for (Iterator it = groups.iterator(); it.hasNext(); ) {
			UserGroup listEl = (UserGroup)it.next();
			int k = title.compareToIgnoreCase(listEl.getTitle());
			if (k <= 0) break;
			i++;
		}
		groups.add(i, userGroup);
		fireTableRowsInserted(i, i);
		selectGroup(i);
	}
	
	/**	Requests that an userGroup be selected.
	 *
	 *	@param	index			Index of userGroup to be selected.
	 */
	 
	public void selectGroup (int index) {
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			GroupListener listener = (GroupListener)it.next();
			listener.selectGroup(index);
		}
	}
	
	/**	Creates a new userGroup.
	 */
	 
	void createGroup () {
		creatingNewGroup = true;
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			GroupListener listener = (GroupListener)it.next();
			listener.clearSelection();
		}
		setSelection(new int[0]);
	}
	
	/**	Returns true if we are creating a new userGroup.
	 *
	 *	@return			True if creating new userGroup.
	 */
	 
	boolean getCreatingNewGroup () {
		return creatingNewGroup;
	}
	
	/**	Deletes the selected groups.
	 *
	 *	@throws	Exception	general error.
	 */
	 
	void deleteGroups () 
		throws Exception
	{

		Arrays.sort(selection);
		int first = selection.length == 0 ? -1 : selection[0];
		for (int k = selection.length-1; k >= 0; k--) {
			int i = selection[k];
			UserGroup userGroup = (UserGroup)groups.get(i);
			UserGroupUtils.deleteUserGroup(userGroup);
			groups.remove(i);
			fireTableRowsDeleted(i, i);
		}
		if (first == groups.size()) first--;
		if (first >= 0) selectGroup(first);
	}
	
	/**	Adds a listener. 
	 *
	 *	@param	listener	Group listener.
	 */
	 
	void addListener (GroupListener listener) {
		listeners.add(listener);
	}
	
	/**	Removes a listener.
	 *
	 *	@param	listener	Group listener.
	 */
	 
	void removeListener (GroupListener listener) {
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

