package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.labeledsettingstable.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	Describes the contents of a word set, work set, or phrase set.
 */

public class DescribeUserDataObject
{
	/**	Describe a user data object.
	 *
	 *	@param	userDataObject	The user data object to describe.
	 *
	 *	@return					A table with the object's settings and values.
	 *							Null if the input object is null.
	 */

	public static XTable describe( UserDataObject userDataObject )
	{
								//	Return null if user data object is null.

		if ( userDataObject == null ) return null;

								//	Create labeled settings table to hold
								//	the description.

		LabeledSettingsTable settingsTable	=
			new LabeledSettingsTable
			(
				new String[]
				{
					WordHoardSettings.getString
					(
						"Setting" ,
						"Setting"
					) ,
					WordHoardSettings.getString
					(
						"Value" ,
						"Value"
					)
				}
			);
                                //	Get label for object type.

		String objectType	= "Unknown";

		if ( userDataObject instanceof WordSet )
		{
			objectType	= "Wordset";
		}
		else if ( userDataObject instanceof WorkSet )
		{
			objectType	= "Workset";
		}
		else if ( userDataObject instanceof PhraseSet )
		{
			objectType	= "Phraseset";
		}

		objectType	=
			WordHoardSettings.getString( objectType , objectType );

								//	Add values for common settings.

		addSetting(
			settingsTable , "Type" , objectType );

		addSetting(
			settingsTable , "Title" , userDataObject.getTitle() );

		addSetting(
			settingsTable , "Owner" , userDataObject.getOwner() );

		addSetting(
			settingsTable , "Public" ,
			StringUtils.yesNo( userDataObject.getIsPublic() ) );

		addSetting(
			settingsTable , "Created" , userDataObject.getCreationTime() );

		addSetting(
			settingsTable , "Modified"  , userDataObject.getModificationTime() );

		String description	= userDataObject.getDescription();

		if ( description == null ) description = "";

		addSetting( settingsTable , "Description" , description );

		XTextPane urlLabel	= new XTextPane();

		String webPageURL	= userDataObject.getWebPageURL();

		if ( webPageURL == null ) webPageURL = "";

		if ( webPageURL.length() > 0 )
		{
			urlLabel.appendLink( webPageURL , new WebLink( webPageURL ) );
		}

		addSetting( settingsTable , "Webpage" , urlLabel );

								//	Add values specific to each
								//	type of user data object.

		if ( userDataObject instanceof PhraseSet )
		{
			PhraseSet phraseSet	= (PhraseSet)userDataObject;

			addSetting( settingsTable , "Phrasecount" , "" );

			addSetting( settingsTable , "Wordcount" ,
				phraseSet.getWordTags().size() );

			addSetting( settingsTable , "Workcount" ,
				phraseSet.getWorkTags().size() );

			addSetting( settingsTable , "Workpartcount" ,
				phraseSet.getWorkPartTags().size() );
		}
		else if ( userDataObject instanceof WordSet )
		{
			WordSet wordSet	= (WordSet)userDataObject;

			addSetting( settingsTable , "Wordcount" ,
				wordSet.getWordTags().size() );

			addSetting( settingsTable , "Workcount" ,
				wordSet.getWorkTags().size() );

			addSetting( settingsTable , "Workpartcount" ,
				wordSet.getWorkPartTags().size() );
		}
		else if ( userDataObject instanceof WorkSet )
		{
			WorkSet workSet	= (WorkSet)userDataObject;

			addSetting( settingsTable , "Workcount" ,
				WorkSetUtils.getWorks( workSet ).length );

			addSetting( settingsTable , "Workpartcount" ,
				WorkSetUtils.getWorkParts( workSet ).length );
		}

		return settingsTable;
	}

	/**	Add a setting and its value to the settings table.
	 *
	 *	@param	table		The settings table.
	 *
	 *	@param	refLabel	Reference name for setting.
	 *						Will be looked up in WordHoardSettings.
	 *
	 *	@param	value		The setting's value.
	 */

	protected static void addSetting
	(
		LabeledSettingsTable table ,
		String refLabel ,
		Object value
	)
	{
		String settingName	=
			WordHoardSettings.getString( refLabel , "" );

		table.addSetting( settingName , value );
	}

	/**	Add a setting and its value to the settings table.
	 *
	 *	@param	table		The settings table.
	 *
	 *	@param	refLabel	Reference name for setting.
	 *						Will be looked up in WordHoardSettings.
	 *
	 *	@param	value		The setting's value.
	 */

	protected static void addSetting
	(
		LabeledSettingsTable table ,
		String refLabel ,
		int value
	)
	{
		String settingName	=
			WordHoardSettings.getString( refLabel , "" );

		table.addSetting( settingName , value );
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

