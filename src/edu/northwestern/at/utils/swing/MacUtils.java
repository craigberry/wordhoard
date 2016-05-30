package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import javax.swing.*;

import com.apple.eawt.*;

import edu.northwestern.at.utils.Env;

/**	Macintosh utilites. */

public class MacUtils
{
	/**	Initializes the Mac OS X environment variables.
	 *
	 *	@param	programName			The program name.
	 *	@param	useScreenMenuBar	True to use the screen menu bar.
	 *
	 *	<p>Sets the following two system properties:</p>
	 *
	 *	<ul>
	 *	<li>com.apple.mrj.application.apple.menu.about.name to programName</li>
	 *	<li>apple.laf.useScreenMenuBar to useScreenMenuBar</li>
	 *	</ul>
	 */

	public static void initMac
	(
		String programName ,
		boolean useScreenMenuBar
	)
	{
		if ( !Env.MACOSX ) return;

								// Set the Mac program name.
		System.setProperty(
			"com.apple.mrj.application.apple.menu.about.name" ,
			programName );

								//	Set use of Mac OS X screen menu bar.
		System.setProperty(
			"apple.laf.useScreenMenuBar" ,
			useScreenMenuBar ? "true" : "false" );
	}

	/**	Initializes the Mac OS X environment application handlers.
	 *
	 *	@param	mainFrame			The main program frame.
	 *
	 *	@param	havePrefs			True to add a Preferences item to 
	 *								the application menu.
	 *
	 *	<p>Registers a quit handler, a prefs handler,
	 *	and an about handler.</p>
	 */

	public static void initMac
	(
		final XFrame mainFrame, boolean havePrefs
	)
	{
		if ( !Env.MACOSX ) return;
		
		Application application = new Application();
		
		if (havePrefs) application.setEnabledPreferencesMenu(true);

		application.addApplicationListener
		(
			new ApplicationAdapter()
			{
				public void handleAbout( ApplicationEvent ev )
				{
					try
					{
						mainFrame.about();
						ev.setHandled( true );
					}
					catch(Exception e) { }
				}

				public void handlePreferences( ApplicationEvent ev )
				{
					mainFrame.prefs();
					ev.setHandled( true );
				}

				public void handleQuit( ApplicationEvent ev )
				{
					mainFrame.quit();
					ev.setHandled( true );
				}
			}
		);
	}

	/** Don't allow instantiation but do allow overrides. */

	protected MacUtils()
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

