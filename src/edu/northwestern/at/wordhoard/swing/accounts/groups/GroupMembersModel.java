package edu.northwestern.at.wordhoard.swing.accounts.groups;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.server.*;
import edu.northwestern.at.wordhoard.server.model.*;
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
 *	<li>The list of all current members.
 *	<li>The current selection - an array of indexes in the list of the 
 *		selected members.
 *	<li>Whether or not we are currently creating a new userGroup.
 *	</ul>
 *
 *	<p>Window components register 
 *	{@link edu.northwestern.at.wordhoard.swing.accounts.groups.GroupListener
 *	listeners} on the model to listen for and react appropriately to changes 
 *	in the state of the window.
 */
 
class GroupMembersModel extends AbstractTableModel {
	
	/**	UserGroup. */
	
	private UserGroup userGroup;

	/**	List of members. */
	
	private ArrayList members;
	
	/**	The selection - an array of indexes of selected members. */
	
	private int[] selection;
	
	/**	True if we are currently creating a new userGroup. */
	
	private boolean creatingNewGroup;
	
	/**	Set of listeners. */
	
	private HashSet listeners = new HashSet();
	
	/**	Creates a new UserGroup members model. 
	 *
	 *	@param	userGroup		UserGroup.
	 *
	 *	@throws Exception
	 */
	
	GroupMembersModel (UserGroup userGroup)
		throws Exception
	{
		this.userGroup = userGroup;
		if(userGroup!=null) members = new ArrayList(userGroup.getMembers());
	}
	
	/**	Sets the UserGroup.
	 *
	 *	@param	userGroup		UserGroup.
	 *
	 */
	
	public void setGroup (UserGroup userGroup) {
		members = new ArrayList (userGroup.getMembers());
		fireTableStructureChanged();
	}
	
	
	/**	Gets the number of rows.
	 *
	 *	@return		The number of rows.
	 */
	
	public int getRowCount () {
		return 1;
//		return (members == null) ? 0 : members.size();
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
		return "hey";
/*
		String member = (String)members.get(row);
		switch (col) {
			case 0: return member;
		}
		return null;
		*/
	}
	
	/**	Gets the name of a column.
	 *
	 *	@param	col		Column index.
	 *
	 *	@return		Name of column.
	 */
	
	public String getColumnName (int col) {
		switch (col) {
			case 0: return "Member";
		}
		return null;
	}
	
	/**	Initializes the model. */
	
	void init () {
		if (members.size() == 0) {
			setSelection(new int[0]);
		} else {
			setSelection(new int[] {0});
		}
	}

	/**	Sets the selection.
	 *
	 *	@param	selection		Array of indexes of selected accounts.
	 */
	 
	void setSelection (int[] selection) {
		this.selection = selection;
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			GroupMembersListener listener = (GroupMembersListener)it.next();
			listener.selectionChanged(selection);
		}
	}

	/**	Adds a listener. 
	 *
	 *	@param	listener	Account listener.
	 */
	 
	void addListener (GroupMembersListener listener) {
		listeners.add(listener);
	}
	
	/**	Removes a listener.
	 *
	 *	@param	listener	Account listener.
	 */
	 
	void removeListener (GroupMembersListener listener) {
		listeners.remove(listener);
	}
	
	/**	Gets the selection.
	 *
	 *	@return		Array of indexes of selected members.
	 */
	 
	int[] getSelection () {
		return selection;
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

