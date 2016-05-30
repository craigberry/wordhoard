package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	Displays describe user data object dialog.
 */

public class DescribeUserDataObjectDialog extends SkeletonDialog
{
	/**	The class of user data objects to display.
	 */

	protected Class userDataObjectClass			= null;

	/**	Selected user data object. */

	protected UserDataObject userDataObject		= null;

	/**	Dialog field for user data objects.
	 */

	protected JComboBox userDataObjectChoices	= null;

	/**	Create new dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public DescribeUserDataObjectDialog
	(
		String dialogTitle ,
		JFrame parent ,
		Class userDataObjectClass
	)
	{
		super
		(
			dialogTitle ,
			parent ,
			WordHoardSettings.getString( "Describe" , "Describe" ) ,
			WordHoardSettings.getString( "Cancel" , "Cancel" ) ,
			WordHoardSettings.getString( "Revert" , "Revert" )
		);

		this.userDataObjectClass	= userDataObjectClass;

		buildDialog();
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Create list of objects to describe.

		Object objs[]	= null;

		if ( userDataObjectClass == WorkSet.class )
		{
			objs	= WorkSetUtils.getWorkSets();
		}
		else if ( userDataObjectClass == WordSet.class )
		{
			objs	= WordSetUtils.getWordSets();
		}
		else if ( userDataObjectClass == PhraseSet.class )
		{
			objs	= PhraseSetUtils.getPhraseSets();
		}
/*
		else if ( userDataObjectClass == WHQuery.class )
		{
			objs	= QueryUtils.getQueries();
		}
*/
		userDataObjectChoices	= new JComboBox( objs );

		userDataObjectChoices.setRenderer(
			new UserDataObjectListRenderer() );

		userDataObject	=
			(UserDataObject)userDataObjectChoices.getSelectedItem();
	}

	/**	Add dialog fields.
	 *
	 *	@param	dialogFields	The dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		super.addFields( dialogFields );

								//	Add combo box for objects.
		dialogFields.addPair
		(
			WordHoardSettings.getString
			(
				"Worksets" ,
				"Work sets"
			) ,
			userDataObjectChoices
		);
	}

	/**	Handles the OK button pressed.
	 *
	 *	@param	event	The event.
	 */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		userDataObject	=
			(UserDataObject)userDataObjectChoices.getSelectedItem();

		dispose();
	}

	/**	Get the selected user data object.
	 *
	 *	@return		Selected user data object.
	 */

	public UserDataObject getSelectedUserDataObject()
	{
		return userDataObject;
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

