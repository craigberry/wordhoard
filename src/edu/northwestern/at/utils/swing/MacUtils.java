package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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

	/** Private utility method for initMac
	 * 
	 * @param mainFrame
	 * @param methodName
	 */
	private static void _invokeHandler
	(
		final XFrame mainFrame, final String methodName
	)
	{
		if (methodName.equals("handleAbout")) {
			mainFrame.about();
		}
		else if (methodName.equals("handlePreferences")) {
			mainFrame.prefs();
		}
		else if (methodName.equals("handleQuitRequestWith")) {
			mainFrame.quit();
		}
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

		// The following mess brought to you by the fact that in Java 8, these methods are only available in
		// com.apple.eawt.Application, but in Java 9+, they are only available in java.awt.Desktop and all of
		// com.apple.eawt has been removed. So use reflection with proxy to acquire at run-time the methods
		// that will work with either Java 8 or Java 9+.
		// Inspired by the solution in:
		//     https://github.com/pgdurand/jGAF/blob/master/src/com/plealog/genericapp/ui/apple/EZAppleConfigurator.java

		try {
			// Conveniently, the 1 in 1.8 is less than 9, so we can always use the first element.
			Integer javaVersion = Integer.parseInt(System.getProperty("java.specification.version").split("\\.")[0]);

			if (javaVersion < 9) {  // methods in com.apple.eawt.Application but not java.awt.desktop
				Object applicationHandler = java.lang.reflect.Proxy.newProxyInstance(
				Proxy.class.getClassLoader(),
				new java.lang.Class[] {
					Class.forName("com.apple.eawt.AboutHandler"),
					Class.forName("com.apple.eawt.PreferencesHandler"),
					Class.forName("com.apple.eawt.QuitHandler") },
				new java.lang.reflect.InvocationHandler() {
					@Override
					public Object invoke(Object proxy,
							java.lang.reflect.Method method,
							Object[] args) throws java.lang.Throwable {
						String methodName = method.getName();
						_invokeHandler(mainFrame, methodName);
						return null;
					}
				});
				// Use the created proxy class as handler for Application.
				Class<?> applicationClass = Class.forName("com.apple.eawt.Application");
				Method method = applicationClass.getMethod("getApplication", (Class[]) null);
				Object application = method.invoke(null);
				method = applicationClass.getMethod("setAboutHandler",
					new Class<?>[] { Class.forName("com.apple.eawt.AboutHandler") });
				method.invoke(application, applicationHandler);
				method = applicationClass.getMethod("setPreferencesHandler",
					new Class<?>[] { Class.forName("com.apple.eawt.PreferencesHandler") });
				method.invoke(application, applicationHandler);
				method = applicationClass.getMethod("setQuitHandler",
					new Class<?>[] { Class.forName("com.apple.eawt.QuitHandler") });
				method.invoke(application, applicationHandler);

			} else { // Java 9+ --  methods in java.awt.Desktop

				Object desktopHandler = java.lang.reflect.Proxy.newProxyInstance(
				Proxy.class.getClassLoader(),
				new java.lang.Class[] {
					Class.forName("java.awt.desktop.AboutHandler"),
					Class.forName("java.awt.desktop.PreferencesHandler"),
					Class.forName("java.awt.desktop.QuitHandler") },
				new java.lang.reflect.InvocationHandler() {
					@Override
					public Object invoke(Object proxy,
						java.lang.reflect.Method method,
						Object[] args) throws java.lang.Throwable {
						String methodName = method.getName();
						_invokeHandler(mainFrame, methodName);
						return null;
					}
				});
				// Use the created proxy class as handler for Desktop.
				Class<?> desktopClass = Class.forName("java.awt.Desktop");
				Method method = desktopClass.getMethod("getDesktop", (Class[]) null);
				Object deskTop = method.invoke(null);
				method = desktopClass.getMethod("setAboutHandler",
					new Class<?>[] { Class.forName("java.awt.desktop.AboutHandler") });
				method.invoke(deskTop, desktopHandler);
				method = desktopClass.getMethod("setPreferencesHandler",
					new Class<?>[] { Class.forName("java.awt.desktop.PreferencesHandler") });
				method.invoke(deskTop, desktopHandler);
				method = desktopClass.getMethod("setQuitHandler",
					new Class<?>[] { Class.forName("java.awt.desktop.QuitHandler") });
				method.invoke(deskTop, desktopHandler);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

