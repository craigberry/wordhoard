package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**	Fonts information.
 *
 *	<p>
 *	This class centralizes font handling.
 *	</p>
 */

public class Fonts
{
	/** Monospace font name. */

	public static String monospaced = "Monospaced";

	/** Serif font name. */

	public static String serif = "Serif";

	/** SansSerif font name. */

	public static String sansSerif = "SansSerif";

	/** Dialog font name. */

	public static String dialog = "Dialog";

	/** DialogInput font name. */

	public static String dialogInput = "DialogInput";

	/** Hides the default no-arg constructor. */

	private Fonts()
	{
		throw new UnsupportedOperationException();
	}

	/** Enable or disable use of built-in desktop properties.
	 *
	 *	@param	doEnable	True to enable use of built-in desktop
	 *						properties for fonts, etc.  False
	 *						otherwise.
	 *
	 *	<p>
	 *	When running the Windows look and feel under Windows,
	 *	in Java v1.4 or later, the runtime environment by default
	 *	ignores the font mappings present in the font.properties
	 *	file and retrieves these from the Windows operating system.
	 *	Since the default Windows fonts for various types of controls
	 *	are incredibly ugly, this results in an unattractive display.
	 *	To fix this, call this method with "doEnable" set false.
	 *	</p>
	 */

	public static void enableSystemFontSettings( boolean doEnable )
	{
/*
		if ( Env.WINDOWSOS && Env.IS_JAVA_14_OR_LATER )
		{
			javax.swing.LookAndFeel currentLookAndFeel =
				UIManager.getLookAndFeel();

			if (	( currentLookAndFeel != null ) &&
					currentLookAndFeel.isNativeLookAndFeel() )
			{
				UIManager.put(
					"Application.useSystemFontSettings" , new Boolean( doEnable ) );
			}
		}
*/
		UIManager.put(
			"Application.useSystemFontSettings" , new Boolean( doEnable ) );
	}

	/**	Set font for a component and its children.
	 *
	 *	@param	component	The component whose font is to be set.
	 *
	 *	@param	font		The font to set the child components to use.
	 *
	 *	<p>
	 *	If component is a container, all of its subcomponents will also
	 *	have the font changed to "font".
	 *	</p>
	 */

	public static void recursivelySetFonts( Component component , Font font )
	{
								//	Set font in component.

		component.setFont( font );

								//	If the component is a container ...

		if ( component instanceof Container )
		{
			Container container	= (Container)component;

								//	... change the font in each of its
								//	children as well.

			for ( int i = 0 ; i < container.getComponentCount() ; i++ )
			{
				recursivelySetFonts( container.getComponent( i ) , font );
			}
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

