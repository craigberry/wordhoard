package edu.northwestern.at.wordhoard.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.io.*;
import java.lang.*;
import java.rmi.*;
import java.util.*;

import org.hibernate.connection.ConnectionProvider;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.hibernate.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.LookAndFeel;
import edu.northwestern.at.utils.sys.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.server.*;
import edu.northwestern.at.wordhoard.server.model.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.analysis.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;
import edu.northwestern.at.wordhoard.swing.tcon.*;
import edu.northwestern.at.wordhoard.swing.work.*;
import edu.northwestern.at.wordhoard.swing.dialogs.*;

/**	WordHoard client program.
 *
 *	<p>Usage:
 *
 *	<p><code>WordHoard [sitesURL]</code>
 *
 *	<p>sites = URL of sites XML config file. The default is
 *	https://wordhoard.northwestern.edu/sites.xml.
 */

public class WordHoard {

	/**	The primary persistence manager. */

	private static PersistenceManager pm;

	/**	The server session, or null if none. */

	private static WordHoardSession session	= null;

	/**	The splash screen, or null if the splash screen is not open. */

	private static edu.northwestern.at.utils.swing.SplashScreen splashScreen = null;

	/**	True if shutting down. */

	private static boolean shuttingDown = false;

	/**	Gets the primary persistence manager.
	 *
	 *	@return		The the primary persistence manager.
	 */

	public static PersistenceManager getPm() {
		return pm;
	}

	/**	Gets the server session.
	 *
	 *	@return		The server session object, or null if none.
	 */

	public static WordHoardSession getSession() {
		if (session == null) initializeServerSession();
		return session;
	}

	/**	Creates a new label for a small combo box.
	 *
	 *	<p>Applies the proper vertical offset to to the label to adjust for
	 *	the operating system.</p>
	 *
	 *	@param	str		Label.
	 *
	 *	@param	font	Font.
	 */

	public static JLabel getSmallComboBoxLabel( String str , Font font) {
		JLabel label = new JLabel(str);
		label.setFont(font);
		if (Env.MACOSX) {
			label.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		}
		return label;
	}

	/**	Handles initialization errors.
	 *
	 *	@param	e	Exception.
	 */

	private static void handleInitError (final Exception e) {
		e.printStackTrace();
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					closeSplashScreen();
					new ErrorMessage(
						WordHoardSettings.getString(
							"UnableToConnectToWordHoardDatabase" ,
							"Unable to connect to WordHoard database."
						)
					);
					System.exit(0);
				}
			}
		);
	}

	/**	Initializes the server session.
	 */

	private static void initializeServerSession () {
		try {
			Bootstrap bootstrap	= (Bootstrap)Naming.lookup(SiteDialog.getServerURL());
			session	= bootstrap.startSession();
			if (session == null) return;
			new Thread (
				new Runnable() {
					public void run() {
						while (true) {
							try {
								Thread.sleep( 30 * 60 * 1000 );
								session.tickle();
							} catch ( Exception e ) {
							}
						}
					}
				}
			).start();
		} catch (Exception e) {
			// ignore failures to connect to server.
		}
	}

	/**	Closes the splash screen. */

	public static void closeSplashScreen () {
		if (splashScreen == null) return;
		splashScreen.dispose();
		splashScreen = null;
	}

	/**	Set the extra verbiage for unexpected error messages. */

	private static void setUnexpectedErrorVerbiage () {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		String s = "";
		do {
			i++;
			s = WordHoardSettings.getString(
					"unexpectedError" + StringUtils.intToString(i),
					""
				);
			if (s.length() > 0) buf.append( s );
		}
		while (s.length() > 0);
		PrintfFormat unexpectedErrorFormat	= new PrintfFormat(buf);
		String commandKey = Env.WINDOWSOS ? "Control" : "Command";
		String extraVerbiage =
			unexpectedErrorFormat.sprintf(
				new Object[] {
					commandKey,
					commandKey,
					WordHoardSettings.getProgramVersion(),
					System.getProperty("os.name"),
					System.getProperty("os.version"),
					System.getProperty("java.version")
			 	}
			);
		UnexpectedErrorMessage.setExtraVerbiage(extraVerbiage);
	}

	/**	Shutdown.
	 */

	private static void shutdown() {
		try {
			pm.close();
			if (session != null) session.endSession();
		} catch ( Exception e ) {
		}
		WordHoardCalculatorWindow calcWindow = WordHoardCalculatorWindow.getCalculatorWindow();
		if (calcWindow != null) {
			calcWindow.closePersistenceManager();
			WordHoardSettings.savePreferences();
		}
		WindowsMenuManager[] openWindows = WindowsMenuManager.getAllOpenWindows();
		for (int i = 0 ; i < openWindows.length ; i++) {
			try {
				openWindows[i].dispose();
			} catch ( Exception e ) {
			}
		}
		Preferences.writePrefs();
	}

	/**	Quit.
	 */

	public static void quit() {
		if (shuttingDown) return;
		shuttingDown = true;
		shutdown();
		System.exit( 0 );
	}

	/**	Initializes the JDBC connection observer.
	 */

	private static void initializeConnectionObserver () {
		ConnectionProvider connectionProvider = pm.getConnectionProvider();
		if (!( connectionProvider instanceof SimpleHibernateConnectionProvider)) return;
		SimpleHibernateConnectionProvider provider =
			(SimpleHibernateConnectionProvider)connectionProvider;
		provider.addObserver (
			new Observer() {
				public void update(Observable o , Object arg) {
					if (session == null) return;
					Object[] argArray = (Object[])arg;
					int kind = ((Integer)argArray[0]).intValue();
					String msg = null;
					switch (kind) {
						case 1:
							msg =
								WordHoardSettings.getString(
									"BadclientJDBCconnection" ,
									"Bad client JDBC connection"
								);
    						break;
						case 2:
							PrintfFormat fmt =
								new PrintfFormat(
									WordHoardSettings.getString(
										"ntriestogetnewclientJDBCconnection" ,
										"%i tries to get new client JDBC connection"
									)
								);
							msg	= fmt.sprintf(new Object[] {argArray[1]});
							break;
					}
					try {
						session.logMessage(Logger.ERROR, msg);
						session.logMessage(
							Logger.ERROR,
							WordHoardSettings.getString(
								"BadclientJDBCconnection" ,
								"Bad client JDBC connection"
							)
						);
					} catch (Exception e) {
					}
				}
			}
		);
	}

	/**	Initialization part 1.
	 *
	 *	<p>Runs on the Swing thread.
	 *
	 *	@param	args		Command line arguments.
	 *
	 *	@throws	Exception
	 */

	private static void init1 (String[] args)
		throws Exception
	{
		WordHoardSettings.initializeSettings(false);
		Preferences.readPrefs();
		LookAndFeel.enableAntialiasedFontRendering();
		LookAndFeel.setNiceLookAndFeel();
		SiteDialog.getSiteInfo(args);
		splashScreen =
			new edu.northwestern.at.utils.swing.SplashScreen(
				WordHoardSettings.getString(
					"WordHoard" ,
					"WordHoard"
				),
				WordHoardSettings.getString(
					"WordHoardisloading" ,
					"WordHoard is loading..."
				),
				WordHoardSettings.getString(
					"Pleasebepatient" ,
					"Please be patient"
				) ,
				null
			);
		new Thread() {
			public void run () {
				try {
					init2();
				} catch (Exception e) {
					handleInitError(e);
				}
			}
		}.start();
	}

	/**	Initialization part 2.
	 *
	 *	<p>Runs on a non-Swing thread.
	 *
	 *	@throws	Exception
	 */

	private static void init2 ()
		throws Exception
	{
		PersistenceManager.init(
			SiteDialog.getDatabaseURL(),
			SiteDialog.getDatabaseUsername(),
			SiteDialog.getDatabasePassword(),
			null,
			PersistentClasses.persistentClasses,
			false
		);
		pm	= new PersistenceManager();
		initializeConnectionObserver();
		CachedCollections.getCorpora();
		initializeServerSession();
		WordHoardSettings.loadPreferences();
		FileDialogs.setOpenDirectory((new File(".")).getAbsolutePath());
		FileDialogs.setSaveDirectory(FileDialogs.getOpenDirectory());
		setUnexpectedErrorVerbiage();
		PersistenceManager.enableStackTraces(true);
		System.setSecurityManager(null);
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					try {
						init3();
					} catch (Exception e) {
						handleInitError(e);
					}
				}
			}
		);
	}

	/**	Initialization part 3.
	 *
	 *	<p>Runs on the Swing thread.
	 *
	 *	@throws	Exception
	 */

	private static void init3 ()
		throws Exception
	{
		WordHoardCalculatorWindow.open(false);
		TabSelectFocusManager.init();
		TableOfContentsWindow.open(true);
		if ( Env.MACOSX ) {
			MacUtils.initMac(
				TableOfContentsWindow.getTableOfContentsWindow(),
				true);
		}
	}

	/** Main program.
	 *
	 *	@param	args		Command line arguments.
	 */

	public static void main (final String[] args) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run () {
					try {
						init1(args);
					} catch (Exception e) {
						handleInitError(e);
					}
				}
			}
		);
	}

	/**	Hides the default no-arg constructor.
	 */

	private WordHoard() {
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

