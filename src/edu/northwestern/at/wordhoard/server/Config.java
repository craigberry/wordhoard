package edu.northwestern.at.wordhoard.server;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

/**	The server configuration manager.
 *
 *	<p>Reads the properties file "wordhoard.config" in the server directory.
 */

class Config {

	/**	Path to the server directory. */

	private static String path;

	/**	Database URL. */

	private static String databaseURL;

	/**	Database username. */

	private static String databaseUsername;

	/**	Database password. */

	private static String databasePassword;

	/** User data database URL. */

	private static String userDataDatabaseURL;

	/**	User data database username. */

	private static String userDataDatabaseUsername;

	/**	User data database password. */

	private static String userDataDatabasePassword;

	/**	RMI registry port. */

	private static int rmiRegistryPort;

	/**	RMI port. */

	private static int rmiPort;

	/**	Reads the server configuration file.
	 *
	 *	@param	path		Path to the server directory.
	 *
	 *	@throws	Exception
	 */

	static void read (String path)
		throws Exception
	{
								//	Get configuration settings.
		Config.path = path;
		path = path + "/wordhoard.config";
		Properties properties = new Properties();
		properties.load(new FileInputStream(path));
		databaseURL = properties.getProperty("database-url");
		databaseUsername = properties.getProperty("database-username");
		databasePassword = properties.getProperty("database-password");
		userDataDatabaseURL = properties.getProperty("userdata-database-url");
		userDataDatabaseUsername = properties.getProperty("userdata-database-username");
		userDataDatabasePassword = properties.getProperty("userdata-database-password");
		String srmiRegistryPort = properties.getProperty("rmiregistry-port");
		rmiRegistryPort = (srmiRegistryPort == null) ? 1099 :
			Integer.parseInt( srmiRegistryPort );
		String srmiPort = properties.getProperty("rmi-port");
		rmiPort = (srmiPort == null) ? 0 : Integer.parseInt(srmiPort);
	}

	/**	Gets the path to the server directory.
	 *
	 *	@return		The path to the server directory.
	 */

	static String getPath () {
		return path;
	}

	/**	Gets the database URL.
	 *
	 *	@return		The database URL.
	 */

	static String getDatabaseURL () {
		return databaseURL;
	}

	/**	Gets the database username.
	 *
	 *	@return		The database username.
	 */

	static String getDatabaseUsername () {
		return databaseUsername;
	}

	/**	Gets the database password.
	 *
	 *	@return		The database password.
	 */

	static String getDatabasePassword () {
		return databasePassword;
	}

	/**	Gets the userDataUserDataDatabase URL.
	 *
	 *	@return		The userDataUserDataDatabase URL.
	 */

	static String getUserDataDatabaseURL () {
		return userDataDatabaseURL;
	}

	/**	Gets the userDataUserDataDatabase username.
	 *
	 *	@return		The userDataUserDataDatabase username.
	 */

	static String getUserDataDatabaseUsername () {
		return userDataDatabaseUsername;
	}

	/**	Gets the userDataUserDataDatabase password.
	 *
	 *	@return		The userDataUserDataDatabase password.
	 */

	static String getUserDataDatabasePassword () {
		return userDataDatabasePassword;
	}

	/**	Gets the log configuration file path.
	 *
	 *	@return		The log configuration file path.
	 */

	static String getLogConfigFilePath () {
		return path + File.separatorChar + "log.config";
	}

	/**	Gets the RMI registry port.
	 *
	 *	@return		The RMI registry port.
	 */

	static int getRmiRegistryPort () {
		return rmiRegistryPort;
	}

	/**	Gets the RMI port.
	 *
	 *	@return		The RMI port.
	 */

	static int getRmiPort () {
		return rmiPort;
	}

	/** Hides the default no-arg constructor. */

	private Config () {
		throw new UnsupportedOperationException();
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

