package edu.northwestern.at.wordhoard.server;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.WordHoardSettings;

/**	Server log manager.
 *
 *	<p>This static class is a simple wrapper around a Jakarta Commons
 *	Logging log4j logger named "edu.northwestern.at.wordhoard.server".
 *
 *	<p>The file named "log.config" in the server directory is used to
 *	configure the logger.
 */

public class Logger {

	/**	Fatal logging level. */

	static public final int FATAL = 0;

	/**	Error logging level. */

	static public final int ERROR = 1;

	/**	Warn logging level. */

	static public final int WARN = 2;

	/**	Info logging level. */

	static public final int INFO = 3;

	/**	Debug logging level. */

	static public final int DEBUG = 4;

	/**	Server logger. */

	static private org.apache.log4j.Logger logger =
		org.apache.log4j.Logger.getLogger(
			"edu.northwestern.at.wordhoard.server");

	/**	Initializes the logger.
	 *
	 *	@param	message	Initial log message.
	 *
	 *	<p>Reads the "log.config" configuraton file and configures
	 *	the server logger.
	 *
	 *	<p>The configuration file is preprocessed to force the log
	 *	file to be located in the server directory. The server directory
	 *	path is prepended to the log file name.
	 *
	 *	@throws	Exception
	 */

	static void initialize (String message)
		throws FileNotFoundException, IOException
	{
		String logConfigFilePath = Config.getLogConfigFilePath();
		Properties properties = new Properties();
		properties.load(new FileInputStream(logConfigFilePath));
		for (Enumeration enumeration = properties.propertyNames();
			enumeration.hasMoreElements(); )
		{
			String key = (String)enumeration.nextElement();
			if (key.startsWith("log4j.appender") &&
				key.endsWith(".File"))
			{
				String val = properties.getProperty(key);
				val = Config.getPath() + File.separatorChar + val;
				properties.setProperty(key, val);
			}
		}
		PropertyConfigurator.configure(properties);
		logger.info(message);
	}

	/**	Initializes the logger.
	 *
	 *	<p>Reads the "log.config" configuraton file and configures
	 *	the server logger.
	 *
	 *	<p>The configuration file is preprocessed to force the log
	 *	file to be located in the server directory. The server directory
	 *	path is prepended to the log file name.
	 *
	 *	@throws	Exception
	 */

	static void initialize ()
		throws FileNotFoundException, IOException
	{
		initialize(
			WordHoardSettings.getString("Serverstarted") +
			WordHoardSettings.getProgramVersion());
	}

	/**	Terminates the logger.
	 */

	static void terminate () {
		logger.info(
			WordHoardSettings.getString("Serverstopped"));
	}

	/**	Maps a LoggerConstants level to a log4j level.
	 *
	 *	@param	level		LoggerConstants level.
	 *
	 *	@return		log4j level.
	 */

	static Priority mapLevel (int level) {
		switch (level) {
			case FATAL:
				return Level.FATAL;
			case ERROR:
				return Level.ERROR;
			case WARN:
				return Level.WARN;
			case INFO:
				return Level.INFO;
			case DEBUG:
				return Level.DEBUG;
		}
		return Level.ERROR;
	}

	/**	Logs a message.
	 *
	 *	@param	level		Log message level.
	 *
	 *	@param	str			Log message.
	 */

	static void log (int level, String str) {
		logger.log(mapLevel(level), str);
	}

	/**	Logs a message for a session.
	 *
	 *	@param	session		The session.
	 *
	 *	@param	level		Log message level.
	 *
	 *	@param	str			Log message.
	 */

	static void log (WordHoardSessionImpl session, int level, String str) {
		str = WordHoardSettings.getString("Session") + "=" + session +
			", " + str;
		logger.log(mapLevel(level), str);
	}

	/**	Logs an error message with a stack trace.
	 *
	 *	@param	level		Log message level.
	 *
	 *	@param	str			Log message.
	 *
	 *	@param	t			Throwable.
	 */

	static void log (int level, String str, Throwable t) {
		logger.log(mapLevel(level), str, t);
	}

	/**	Logs an error message for a session with a stack trace.
	 *
	 *	@param	session		The session.
	 *
	 *	@param	level		Log message level.
	 *
	 *	@param	str			Log message.
	 *
	 *	@param	t			Throwable.
	 */

	static void log (WordHoardSessionImpl session, int level, String str,
		Throwable t)
	{
		str = WordHoardSettings.getString("Session") + "=" + session +
			", " + str;
		logger.log(mapLevel(level), str, t);
	}

	/** Hides the default no-arg constructor. */

	private Logger () {
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

