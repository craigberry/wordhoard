package edu.northwestern.at.wordhoard.swing;

/*	Please see the license information at the end of this file. */

import java.io.InputStream;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.sys.*;
import edu.northwestern.at.wordhoard.server.model.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.dialogs.*;

/** Global settings for WordHoard.
 *
 *	<p>
 *	This class holds the static values of global settings used
 *	by many other WordHoard classes.  This includes the global
 *	string resource bundle and the current user information,
 *	including the user ID of the currently logged in user.  All of these
 *	settings are stored in protected variables to allow access but disallow
 *	changes once they are initialized at start up.
 *	</p>
 *
 *	<p>
 *	The WordHoard resource bundle contains definitions for all the
 *	string constants used in WordHoardCalc.  Once the resource strings have
 *	been initialized in the initializeSettings method you can access the
 *	strings by calling getString:
 *	</p>
 *
 *	<p>
 *	<code>
 *	String s	= WordHoardSettings.getString( "mystring" , "my string" );
 *	</code>
 *	</p>
 *
 *	<p>
 *	To get the userID of the currently logged in user, use
 *	</p>
 *
 *	<p>
 *	<code>
 *	String userID	= WordHoardSettings.getUserID();
 *	</code>
 *	</p>
 *
 *	<p>
 *	The userID is null if the user is not logged in.
 *	</p>
 */

public class WordHoardSettings
{
	/**	The resource strings. */

	protected static ResourceBundle resourceBundle	= null;

	/** The program name. */

	protected static String programTitle;

	/**	The program version. */

	protected static String programVersion;

	/**	The program banner (title and version number) */

	protected static String programBanner;

	/** The program web site. */

	protected static String programWebSite;

	/** The annotation server read base url (defunct) */

	//protected static String annotationReadBase;

	/** The annotation server write base url (defunct) */

	//protected static String annotationWriteBase;

	/**	The script interpreter prompt. */

	protected static String programPrompt;

	/**	The WordHoard logo file name. */

	protected static String programLogoFileName;

	/**	The WordHoard logo. */

	protected static ImageIcon programLogo	= null;

	/**	The WordHoard large icon file name. */

	protected static String programIconFileName;

	/**	The WordHoard large icon. */

	protected static ImageIcon programIcon	= null;

	/**	The WordHoard small icon file name. */

	protected static String programSmallIconFileName;

	/**	The WordHoard small icon. */

	protected static ImageIcon programSmallIcon	= null;

	/**	The WebStart code base.  Actual code base set by WebStart,
	 *	or from hibernate.properties if not running under WebStart.
	 */

	protected static String programCodebase;

	/**	Top window slop. */

	protected static int topSlop;

	/**	Bottom window slop. */

	protected static int botSlop;

	/**	Grow box slop. */

	protected static int growSlop;

	/**	True to issue warning when quitting WordHoard.
	 */

	protected static boolean warnWhenQuitting	= false;

	/**	True if a WordHoard build program is running and
	 *	needs to access the main WordHoard database directly.
	 */

	protected static boolean buildProgramRunning	= false;

	/**	Resource bundle path/name. */

	protected static String resourceName	=
		"edu.northwestern.at.wordhoard.resources.wh";
		
	/**	True to use screen menu bar on Mac OS. */
	
	protected static boolean useScreenMenuBar;

	/**	Initialize WordHoard settings.
	 *
	 *	@param	noAWT	Avoid any class that might require AWT or Swing.
	 */

	public static void initializeSettings( boolean noAWT )
	{
								//	Get resource strings.
		try
		{
			resourceBundle	=
				ResourceBundle.getBundle( resourceName );
		}
		catch ( MissingResourceException mre )
		{
			System.err.println( resourceName + ".properties not found" );
			System.exit( 0 );
		}
								//	Get program title and banner.
		programTitle	=
			WordHoardSettings.getString( "programTitle" , programTitle );
			
		programWebSite  =
			WordHoardSettings.getString("programWebSite", programWebSite);

		programVersion	=
			WordHoardSettings.getString( "programVersion" , programVersion );

		programBanner	=
			WordHoardSettings.getString( "programBanner" , programBanner );


								//	Get program prompt.
		programPrompt	=
			WordHoardSettings.getString( "programPrompt" , programPrompt );

								//	Get program logo file name.

		programLogoFileName	=
			WordHoardSettings.getString(
				"programLogoFileName" , programLogoFileName );

								//	Get program icon file name.

		programIconFileName	=
			WordHoardSettings.getString(
				"programIconFileName" , programIconFileName );

								//	Get program small icon file name.

		programSmallIconFileName	=
			WordHoardSettings.getString(
				"programSmallIconFileName" , programSmallIconFileName );

								//	Load images if AWT use allowed.
		if ( !noAWT )
		{
								//	Get program logo image.

			programLogo	= Images.get( programLogoFileName );

								//	Get program icon image.

			programIcon	= Images.get( programIconFileName );

								//	Get program small icon image.

			programSmallIcon	= Images.get( programSmallIconFileName );

								//	Initialize Mac OS X menu bar and
								//	program title.
			if ( Env.MACOSX )
			{
				String javaVersion = System.getProperty("java.version");
				useScreenMenuBar =
					javaVersion.compareTo("1.5.0_06") >= 0;
				MacUtils.initMac( programTitle , useScreenMenuBar );
			}
								//	Set small program icon as default
								//	image for all AbstractWindow instances.

			if ( programSmallIcon != null )
			{
				AbstractWindow.setDefaultImage(
					programSmallIcon.getImage() );
			}
		}
								//	Set web start code base.

		WebStart.setCodebase( programCodebase );

								//	Set window slop.

		topSlop		= 2;
		botSlop		= 30;
		growSlop	= 0;

		if ( !noAWT )
		{
			if ( Env.MACOSX )
			{
				topSlop		= 24;
				botSlop		= 2;
				growSlop	= 14;
			}
		}
	}

	/**	Get string from ResourceBundle.  If no string is found, a default
	 *  string is used.
	 *
	 *	@param	resourceName	Name of resource to retrieve.
	 *	@param	defaultValue	Default value for resource.
	 *
	 *	@return   				String value from resource bundle if
	 *							resourceName found there, otherwise
	 *							defaultValue.
	 *
	 *	<p>
	 *	Underline "_" characters are replaced by spaces.
	 *	</p>
	 */

	public static String getString
	(
		String resourceName ,
		String defaultValue
	)
	{
		String result;

		try
		{
			result	= resourceBundle.getString( resourceName );
		}
		catch ( MissingResourceException e )
		{
			result	= defaultValue;
		}

		result	= result.replace( '_' , ' ' );

		return result;
	}

	/**	Get string from ResourceBundle.  If no string is found, an empty
	 *  string is returned.
	 *
	 *	@param	resourceName	Name of resource to retrieve.
	 *
	 *	@return   				String value from resource bundle if
	 *							resourceName found there, otherwise
	 *							empty string.
	 *
	 *	<p>
	 *	Underline "_" characters are replaced by spaces.
	 *	</p>
	 */

	public static String getString( String resourceName )
	{
		String result;

		try
		{
			result	= resourceBundle.getString( resourceName );
		}
		catch ( MissingResourceException e )
		{
			result	= "";
		}

		result	= result.replace( '_' , ' ' );

		return result;
	}

	/**	Parse ResourceBundle for a String array.
	 *
	 *	@param	resourceName	Name of resource.
	 *	@param  defaults		Array of default string values.
	 *	@return					Array of strings if resource name found
	 *							in resources, otherwise default values.
	 */

	public static String[] getStrings
	(
		String resourceName,
		String[] defaults
	)
	{
		String[] result;

		try
		{
			result = tokenize( resourceBundle.getString( resourceName ) );
		}
		catch ( MissingResourceException e )
		{
			result	= defaults;
		}

		return result;
	}

	/**	Gets the login account.
	 *
	 *	@return		Login account, or null if none.
	 */

	public static Account getLoginAccount()
	{
		return LoginDialog.getLoginAccount();
	}

	/**	Get the user ID.
	 *
	 *	@return		The user ID of the currently logged in user.
	 *				Null if the user is not logged in.
	 *				The value is stored in the Account object in
	 *				LoginDialog .
	 *
	 *	<p>
	 *	If "buildProgramRunning" is true, return "system"
	 *	as the user ID.  This is for use by build programs only.
	 *	</p>
	 */

	public static String getUserID()
	{
		String result	= null;

		if ( buildProgramRunning )
		{
			result	= "system";
		}
		else
		{
			Account account = WordHoardSettings.getLoginAccount();

			if ( account != null )
			{
				result	= account.getUsername();
			}
    	}

		return result;
	}

	/**	Get the user's name.
	 *
	 *	@return		The name of the currently logged in user.
	 *				Null if the user is not logged in.
	 *				The value is stored in the Account object in
	 *				LoginDialog .
	 */

	public static String getName()
	{
		String result	= null;

		if ( buildProgramRunning )
		{
			result	= "system";
		}
		else
		{
			Account account = WordHoardSettings.getLoginAccount();

			if ( account != null )
			{
				result	= account.getName();
			}
		}

		return result;
	}

	/**	Check if a user ID is for the currently logged in user.
	 *
	 *	@param	userID	The user ID to check.
	 *
	 *	@return			true if the user ID matches that of the
	 *					currently logged-in user.
	 */

	public static boolean isCurrentUser( String userID )
	{
		String currentUserID	= getUserID();

		return
			(	( userID != null ) &&
				( currentUserID != null ) &&
				( userID.equals( currentUserID ) ) );
	}

	/**	Check if user logged in.
	 *
	 *	@return		True if user currently logged in.
	 */

	public static boolean isLoggedIn()
	{
		return ( getUserID() != null );
	}

	/**	Check for null or empty user ID.
	 *
	 *	@param	userID	User ID to check.
	 *
	 *	@return			true if userID is null or empty.
	 */

	public static boolean isEmptyUserID( String userID )
	{
		boolean result	= true;

		if ( userID != null )
		{
			result	= ( userID.trim().length() == 0 );
		}

		return result;
	}

	/**	Get the user's account manager status.
	 *
	 *	@return		true if the user has account manamagement priviledges,
	 *				false otherwise.
	 *				The value is stored in the Account object in
	 *				LoginDialog .
	 */

	public static boolean getCanManageAccounts()
	{
		boolean result	= false;

		Account account = WordHoardSettings.getLoginAccount();

		if ( account != null )
		{
			result	= account.getCanManageAccounts();
		}

		return result;
	}

	/**	Get the program banner.
	 *
	 *	@return		The program banner.
	 */

	public static String getProgramBanner()
	{
		return programBanner;
	}

	/**	Get the program prompt.
	 *
	 *	@return		The program prompt.
	 */

	public static String getProgramPrompt()
	{
		return programPrompt;
	}

	/**	Get the program title.
	 *
	 *	@return		The program title.
	 */

	public static String getProgramTitle()
	{
		return programTitle;
	}

	/**	Get the program logo.
	 *
	 *	@return		The program logo.
	 */

	public static ImageIcon getProgramLogo()
	{
		return programLogo;
	}

	/**	Get the program icon.
	 *
	 *	@return		The program icon.
	 */

	public static ImageIcon getProgramIcon()
	{
		return programIcon;
	}

	/**	Get the small program icon.
	 *
	 *	@return		The small program icon.
	 */

	public static ImageIcon getProgramSmallIcon()
	{
		return programSmallIcon;
	}

	/**	Get the program version.
	 *
	 *	@return		The program version strings (e.g., "0.55").
	 */

	public static String getProgramVersion()
	{
		return programVersion;
	}

	/**	Get the anotation read server URL.
	 *
	 *	<p>(Defunct.)
	 *
	 *	@return		The annotation read server URL.  Null if none.
	 */

/*
	public static String getAnnotationReadServerURL()
	{
		return annotationReadBase;
	}
*/

	/**	Get the anotation write server URL.
	 *
	 *	<p>(Defunct.)
	 *
	 *	@return		The annotation write server URL.  Null if none.
	 */

/*
	public static String getAnnotationWriteServerURL()
	{
		return annotationWriteBase;
	}
*/

	/**	Split string into a series of substrings on whitespace boundries.
	 *
	 *	@param	input	Input string.
	 *	@return			The array of strings after splitting input.
	 *
	 *	<p>
	 *	This is useful for retrieving an array of strings from the
	 *	resource file.  Underline "_" characters are replaced by spaces.
	 *	</p>
	 */

	public static String[] tokenize( String input )
	{
		Vector v			= new Vector();
		StringTokenizer t	= new StringTokenizer( input );
		String result[];
		String s;

		while ( t.hasMoreTokens() )
		{
			v.addElement( t.nextToken() );
		}

		result	= new String[ v.size() ];

		for ( int i = 0 ; i < result.length ; i++ )
		{
			s			= (String)v.elementAt( i );
			result[ i ]	= s.replace( '_' , ' ' );
		}

		return result;
	}

 	/**	Save preferences. */

 	public static void savePreferences()
 	{
		Properties preferences	= AnalysisDialog.savePreferences();
 		WebStart.putMuffin( "calcprefs" , preferences );
 	}

 	/**	Read preferences. */

 	public static void loadPreferences()
 	{
 		Properties preferences	=	null;

 		try
 		{
 			preferences	= (Properties)WebStart.getMuffin( "calcprefs" );
        }
        catch ( Exception e )
        {
//        	e.printStackTrace();
        }

 		if ( preferences == null ) return;

		AnalysisDialog.loadPreferences( preferences );
 	}

	/**	Get the WordHoard web site URL.
	 *
	 *	@return		The WordHoard web site URL.
	 */

	public static String getWebSiteURL ()
	{
		return programWebSite;
	}

	/**	Gets the top window slop.
	 *
	 *	@return		The top window slop.
	 */

	public static int getTopSlop()
	{
		return topSlop;
	}

	/**	Gets the bottom window slop.
	 *
	 *	@return		The bottom window slop.
	 */

	public static int getBotSlop()
	{
		return botSlop;
	}

	/**	Gets the grow box slop.
	 *
	 *	@return		The grow box slop.
	 */

	public static int getGrowSlop()
	{
		return growSlop;
	}

	/**	Get "warn when quitting" flag.
	 *
	 *	@return		true to issue warning when quitting WordHoard.
	 */

	public static boolean getWarnWhenQuitting()
	{
		return warnWhenQuitting;
	}

	/** Enable or disable build program running.
	 *
	 *	@param	isBuild		Set to true to use direct database access for
	 *						create/modify/update oprations instead of going
	 *						through WordHoard server.  Set to false for
	 *						normal WordHoard client use.
	 */

	public static void setBuildProgramRunning( boolean isBuild )
	{
		buildProgramRunning	= isBuild;
	}

	/**	Get flag telling if build program is running or not.
	 *
	 *	@return		true if a build program is running.
	 */

	public static boolean getBuildProgramRunning()
	{
		return buildProgramRunning;
	}
	
	/**	Returns true to use the screen menu bar on Mac OS X.
	 *
	 *	@return		True to use the screen menu bar on Mac OS X.
	 */
	 
	public static boolean getUseScreenMenuBar () {
		return useScreenMenuBar;
	}

	/**	Can't instantiate but can override. */

	protected WordHoardSettings()
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

