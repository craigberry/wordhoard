package edu.northwestern.at.utils.swing.systeminformation;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.labeledsettingstable.*;

/** System information panel.
 *
 *	<p>
 *	Creates a panel containing a labeled settings table displaying
 *	important Java run-time enviroment settings.
 *	</p>
 */

public class SystemInformationPanel extends DialogPanel
{
	/** The table which holds the settings. */

	protected LabeledSettingsTable systemInformationTable;

	/** The settings names. */

	protected String[] infoItems =
	{	/* "wordhoardcalc.version", */
		"java.version",
		"used.memory",
		"free.memory",
		"total.memory",
		"java.threads",
		"server.info",
		"java.vendor",
		"java.vendor.url",
		"java.home",
		"java.class.version",
		"java.class.path",
		"os.name",
		"os.arch",
		"os.version",
		"file.separator",
		"path.separator",
		"line.separator",
		"ui.lookandfeel",
		"user.name",
		"user.home",
		"user.dir",
	};

	/**	Constructs a new system information panel.
	 */

	public SystemInformationPanel()
	{
		super();

		systemInformationTable = new LabeledSettingsTable( new String[]{ "" , "" } );

		XScrollPane settingsScrollPane =
			new XScrollPane
			(
				systemInformationTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
			);

		settingsScrollPane.setAlignmentX( Component.CENTER_ALIGNMENT );
		settingsScrollPane.getViewport().setBackground( Color.white );
		settingsScrollPane.setPreferredSize( new Dimension( 650 , 400 ) );

		settingsScrollPane.setBorder
		(
			BorderFactory.createLineBorder( Color.black )
		);

		add( settingsScrollPane );

		rebuild();
	}

	/**	Rebuilds the information. */

	protected void rebuild()
	{
								//	Get runtime object for memory status.

		Runtime runTime	= Runtime.getRuntime();

		long freeMem	= runTime.freeMemory();
		long totalMem	= runTime.totalMemory();
		long usedMem 	= totalMem - freeMem;

								//	Hook for prepending custom values.

		prependCustomValues();
								//	For each item to add to the
								//	system information display ...

		for ( int i = 0; i < infoItems.length; i++ )
		{
			String value = "";

			if ( infoItems[ i ].equals( "free.memory" ) )
			{
				value = StringUtils.formatNumberWithCommas( freeMem ) + " bytes";
			}
			else if ( infoItems[ i ].equals( "total.memory" ) )
			{
				value = StringUtils.formatNumberWithCommas( totalMem ) + " bytes";
			}
			else if ( infoItems[ i ].equals( "used.memory" ) )
			{
				value = StringUtils.formatNumberWithCommas( usedMem ) + " bytes";
			}
			else if ( infoItems[ i ].equals( "java.threads" ) )
			{
				value = DebugUtils.activeThreads();
			}
			else if ( infoItems[ i ].equals( "ui.lookandfeel" ) )
			{
				value = UIManager.getLookAndFeel().toString();
			}
			else
			{
								// Get a setting value.
				try
				{
					value = System.getProperty( infoItems[ i ] );
				}
				catch ( Exception e )
				{
					value = "?????";
				}
			}
								// Make java vendor url into clickable link.

			if ( infoItems[ i ].equals( "java.vendor.url" ) )
			{
				XTextPane urlPane = new XTextPane();

				urlPane.appendLink(
					value , new WebLink( value ) );

				systemInformationTable.addSetting( infoItems[ i ] , urlPane );
			}
			else
			{
								// Encode line separator as hex string.

				if ( infoItems[ i ].equals( "line.separator" ) )
				{
					value = StringUtils.stringToHexString( value );
				}

				systemInformationTable.addSetting( infoItems[ i ] , value );
			}
		}
								//	Hook for appending custom values.

		appendCustomValues();
	}

	/**	Refreshes the window.
	 *
	 *	<p>The system garbage collector is run before refreshing the window.</p>
	 */

	public void refresh()
	{
		System.gc();
		systemInformationTable.eraseAllSettings();
		rebuild();
	}

	/**	Returns the labeled settings table holding the system information.
	 *
	 *	@return		The labeled settings table of the system information.
	 */

	public LabeledSettingsTable getSystemInformationTable()
	{
		return systemInformationTable;
	}

	/**	Prepend custom values to system information table.
	 */

	public void prependCustomValues()
	{
	}

	/**	Append custom values to system information table.
	 */

	public void appendCustomValues()
	{
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

