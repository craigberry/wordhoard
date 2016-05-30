package edu.northwestern.at.wordhoard.swing.calculator.menus;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

import javax.swing.*;
import javax.swing.event.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.LookAndFeel;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;

/**	A look and feel menu item.
 *
 *	Constructs radio button menu item for a look and feel name.
 *	When menu item is selected, the associated look and feel
 *	is enabled.
 */

public class LookAndFeelMenuItem extends JRadioButtonMenuItem
{
	/**	The look and feel class name for this menu item. */

	protected String lookAndFeelClassName;

	/**	Create a look and feel menu item.
	 *
	 *	@param	name		The name of the look and feel menu item.
	 *	@param	className	The look and feel class name.
	 */

	public LookAndFeelMenuItem
	(
		String name ,
		String className
	)
	{
		super( name );

		lookAndFeelClassName	= className;

		addItemListener
		(
			new ItemListener()
			{
				public void itemStateChanged( ItemEvent event )
				{
					if ( event.getStateChange() == ItemEvent.SELECTED )
					{
						if ( !LookAndFeel.setLookAndFeelByClassName(
							lookAndFeelClassName ) )
						{
							PrintfFormat errorMessageFormat	=
								new PrintfFormat
								(
									WordHoardSettings.getString
									(
       	                                "Unabletosetlookandfeel" ,
           	                            "Unable to set \"%s\" look and feel."
									)
								);

							final String errorMessage	=
								errorMessageFormat.sprintf
								(
									new Object[]{ lookAndFeelClassName }
								);

							SwingUtilities.invokeLater
							(
								new Runnable()
								{
									public void run()
									{
										new ErrorMessage
										(
											errorMessage ,
											WordHoardCalculatorWindow.getCalculatorWindow()
										);
									}
								}
							);
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

