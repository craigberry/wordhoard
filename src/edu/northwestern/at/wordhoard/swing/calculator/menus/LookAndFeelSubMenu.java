package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.lang.reflect.*;

import javax.help.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.LookAndFeel;
import edu.northwestern.at.utils.sys.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.tcon.*;

/**	WordHoard Calculator Look and Feel Submenu.
 */

public class LookAndFeelSubMenu extends JMenu
{
	/**	Create Look and Feel sub menu.
	 */

	public LookAndFeelSubMenu()
	{
		super
		(
			WordHoardSettings.getString
			(
				"lookAndFeelItem" ,
				"Look and Feel..."
			)
		);

		createSubMenuItems();
	}

	/**	Create the submenu items, which are the available look and feel entries.
	 */

	protected void createSubMenuItems()
	{
								//	Create group for radio button
								//	menu items.

		ButtonGroup buttonGroup	= new ButtonGroup();

								//	No look and feel is currently set.

		LookAndFeelMenuItem currentLookAndFeelMenuItem	= null;

								//	Get the currently enabled look and
								//	feel class name.

		String currentLookAndFeelClassName	=
			LookAndFeel.getActiveLookAndFeel();

								//	Get all available look and feel names.

		LookAndFeel.ExtendedLookAndFeelInfo[] lookAndFeelInfo	=
			LookAndFeel.getExtendedLookAndFeelInfo();

								//	Create submenu items for each
								//	available look and feel.

		for ( int i = 0 ; i < lookAndFeelInfo.length ; i++ )
		{
			LookAndFeelMenuItem	menuItem	=
				new LookAndFeelMenuItem
				(
					lookAndFeelInfo[ i ].name ,
					lookAndFeelInfo[ i ].className
				);

			super.add( menuItem );

			buttonGroup.add( menuItem );

								//	Note if this look and feel
								//	is the currently active one.

			if ( lookAndFeelInfo[ i ].className.equals(
				currentLookAndFeelClassName ) )
			{
				currentLookAndFeelMenuItem	= menuItem;
			}

			menuItem.setEnabled( true );
		}
								//	Select the menu item for the
								//	currently active look and feel.

		if ( currentLookAndFeelMenuItem != null )
		{
			currentLookAndFeelMenuItem.setSelected( true );

								//	Add listener for look and feel
								//	property changes.  When the look and
								//	feel menu selection is changed,
								//	we need to tell Swing to update all its
								//	windows to the newly selected look and
								//	feel.

			UIManager.addPropertyChangeListener
			(
				new PropertyChangeListener()
				{
					public void propertyChange
					(
						PropertyChangeEvent event
					)
					{
						handleLookAndFeelChange( event );
					}
				}
			);
		};
	}

	/**	Handle look and feel change.
	 *
	 *	@param	event	The PropertyChangeEvent for the look and feel.
	 */

	protected void handleLookAndFeelChange
	(
		final PropertyChangeEvent event
	)
	{
		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					String propertyName	= event.getPropertyName();

					if	(	( propertyName != null ) &&
							( propertyName.equals( "lookAndFeel" ) ) )
					{
						String oldLF	= null;

						if ( event.getOldValue() != null )
						{
							oldLF	= event.getOldValue().toString();
						}

						String newLF	= null;

						if ( event.getNewValue() != null )
						{
							newLF	= event.getNewValue().toString();
						}

						if	(	( oldLF != null ) &&
								( newLF != null ) &&
								( !oldLF.equals( newLF ) ) )
						{
							LookAndFeel.updateLookAndFeel();
						}
					}
				}
			}
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

