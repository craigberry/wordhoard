package edu.northwestern.at.wordhoard.swing.sysinfo;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.systeminformation.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	The system information window.
 */

public class SysInfoWindow extends AbstractWindow
{
	/**	The system information window, or null if none is open. */

	protected static SysInfoWindow sysInfoWindow;

	/**	The system information panel. */

	protected SystemInformationPanel sysInfoPanel;

	/**	Opens or brings to the front the system information window.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *
	 *	@throws	PersistenceException
	 */

	public static void open( AbstractWindow parentWindow )
		throws PersistenceException
	{
		if ( sysInfoWindow == null )
		{
			sysInfoWindow	= new SysInfoWindow( parentWindow );
		}
		else
		{
			sysInfoWindow.setVisible( true );
			sysInfoWindow.toFront();
		}
	}

	/**	Creates a new system information window.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *
	 *	@throws	PersistenceException
	 */

	public SysInfoWindow( AbstractWindow parentWindow )
		throws PersistenceException
	{
		super
		(
			WordHoardSettings.getString
			(
				"SystemInformation" ,
				"System Information"
			) ,
			parentWindow
		);

		enableGetInfoCmd( false );

								//	Create system information panel.

		sysInfoPanel			=
			new SystemInformationPanel()
			{
				public void prependCustomValues()
				{
					systemInformationTable.addSetting
					(
						"WordHoard.version" ,
						WordHoardSettings.getProgramVersion()
					);
				}
			};
								//	Display panel in window.

		DialogPanel dialogPanel	= new DialogPanel();

		dialogPanel.add( sysInfoPanel );

		dialogPanel.addButton
		(
			WordHoardSettings.getString( "ClearCaches" , "Clear Caches" ) ,
			new ActionListener()
			{
				public void actionPerformed( ActionEvent event )
				{
					clearCaches();
				}
			}
		);

		dialogPanel.addButton
		(
			WordHoardSettings.getString( "Refresh" , "Refresh" ) ,
			new ActionListener()
			{
				public void actionPerformed( ActionEvent event )
				{
					sysInfoPanel.refresh();
				}
			}
		);


		setContentPane( dialogPanel );

		pack();

		Dimension screenSize	= getToolkit().getScreenSize();
		Dimension windowSize	= getSize();

		windowSize.height		=
			screenSize.height / 2 -
			( WordHoardSettings.getTopSlop() +
			WordHoardSettings.getBotSlop() );

		setSize( windowSize );

		setLocation( new Point( 3 , WordHoardSettings.getTopSlop() ) );

		WindowPositioning.centerWindowOverWindow( this , parentWindow , 0 );

		setVisible(true);
	}

	/**	Find a printable component.
	 *
	 *	@return		A printable component.
	 */

	protected Component findPrintableComponent()
	{
		return sysInfoPanel.getSystemInformationTable();
	}

	/**	Find a saveable component.
	 *
	 *	@return		A saveable component.  Null=none by default.
	 */

	protected SaveToFile findSaveableComponent() {
		return sysInfoPanel.getSystemInformationTable();
	}

	/**	Clears the database caches and garbage collects memory.
	 */

	protected void clearCaches()
	{
								//	Clear first level cache.

		PersistenceManager.doClear();

								//	Evict second-level cache for each
								//	persistent class.

		for ( int i = 0 ; i < PersistentClasses.persistentClasses.length ; i++ )
		{
			PersistenceManager.doEvict
			(
				PersistentClasses.persistentClasses[ i ]
			);
		}
								//	Schedule garbage collection.

		sysInfoPanel.refresh();
	}

	/**	Handles window dispose events. */

	public void dispose()
	{
		sysInfoWindow	= null;
		super.dispose();
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

