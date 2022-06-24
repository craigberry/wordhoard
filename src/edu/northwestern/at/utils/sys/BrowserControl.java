package edu.northwestern.at.utils.sys;

/*	Please see the license information at the end of this file. */

import java.security.*;

/**	BrowserControl.
 *
 *	<p>
 *	BrowserControl provides interface to native methods for controlling
 *	a web browser.
 *	</p>
 *
 *	<p>
 *	The native code implements three procedures.
 *	</p>
 *
 *	<ul>
 *
 *		<li>
 *		<p><strong>openURL</strong> opens a (possibly new) browser window to the specified URL.
 *		If no browser window is currently open, as new browser instance should
 *		be initiated.  The parameter "useCurrentBrowserWindow", if true,
 *		should cause the selected URL to be opened in the currently open
 *		and selected browser window.  If false, a new browser window should be
 *		opened to displayed the URL.
 *		</p>
 *		</li>
 *
 *		<li>
 *		<p><strong>getURL</strong> returns the URL of the web page displayed in the
 *		currently selected browser window, if any.
 *		</p>
 *		</li>
 *
 *		<li>
 *		<p><strong>getURLTitle</strong> to return the title of the web page displayed in the
 *		currently selected browser window, if any.
 *		</p>
 *		</li>
 *
 *	</ul>
 *
 *	<p>
 *	On most platforms some form of the Netscape API can be used to control
 *	most browsers.  On Windows, the native library uses DDE (Dynamic Data Exchange)
 *	to start and control the default system web browser using the Netscape API.
 *	The partner windows native library is called "BrowserControl.dll".
 *	</p>
 */

public class BrowserControl
{
	/** Retrieve URL of web page currently being displayed in web browser.
	 *
	 *	@return		The URL.
	 */

	native public static String getURL();

	/** Retrieve title of web page currently being displayed in web browser.
	 *
	 *	@return		The page title.
	 */

	native public static String getURLTitle();

	/** Open a URL in a web browser and optionally use a new browser window.
	 *
	 *	@param	URL						The URL of the web page to open.
	 *	@param	useNewBrowserWindow		True to open URL in a new browser window.
	 *
	 *	<p>
	 *	The web browser will be started if it isn't running already.
	 *	</p>
	 */

	native public static void openURL( String URL , boolean useNewBrowserWindow );

	/** Open a URL in a web browser.
	 *
	 *	@param	URL		The URL of the web page to open.
	 *
	 *	<p>
	 *	The URL will be opened in the current browser window, or a new
	 *	window if the browser isn't started.
	 *	</p>
	 */

	public static void openURL( String URL )
	{
		openURL( URL , false );
	}

	private static class LoadAction implements PrivilegedAction
	{
		public LoadAction(){};

        public Object run()
        {
            System.loadLibrary( "BrowserControl" );
            return null;
        }
    }
								// Loads the native library.
	static
	{
		System.loadLibrary( "BrowserControl" );

//		LoadAction loadAction = new LoadAction();
//		AccessController.doPrivileged( loadAction );
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

