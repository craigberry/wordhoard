package edu.northwestern.at.utils.swing.labeledsettingstable;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.table.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/** Settings table model. */

public class LabeledSettingsTableModel extends DefaultTableModel
{
	/** Create settings table model.
	 * @param	columnNames	Array of column names.
	*/

	public LabeledSettingsTableModel( String[] columnNames )
	{
		super( columnNames , 0 );
	}

	/** Add a setting/value pair to the table.
	 *
	 *	@param	settingName		Name of the setting.
	 *	@param	settingData		Value of the setting.  Must be an
	 *							object type that implements the
	 *							"toString()" method.
	 */

	public void addSetting( String settingName , Object settingData )
	{
								// Add colon to setting name if
								// the setting name is not empty.

		String colonName = StringUtils.safeString( settingName );

		if ( colonName.length() > 0 )
		{
			colonName = colonName + ":";
		}
                                // Add the setting name and value
                                // to the table.

		super.addRow( new Object[]{ colonName , settingData } );
	}

	/** Add labeled setting to table.
	 *
	 *	@param	settingName		Setting name
	 *	@param	settingValue	Setting value
	 */

	public void addSetting( String settingName , int settingValue )
	{
		addSetting
		(
			settingName ,
			Formatters.formatIntegerWithCommas( settingValue )
		);
	}

	/** Add labeled setting to table.
	 *
	 *	@param	settingName		Setting name
	 *	@param	settingValue	Setting value
	 */

	public void addSetting( String settingName , long settingValue )
	{
		addSetting
		(
			settingName ,
			Formatters.formatLongWithCommas( settingValue )
		);
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

