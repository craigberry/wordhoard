package edu.northwestern.at.wordhoard.server;

/*	Please see the license information at the end of this file. */

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.registry.*;

import edu.northwestern.at.wordhoard.swing.WordHoardSettings;

/**	The server main program.
 *
 *	<p><code>Server (start path | stop path)</code>
 *
 *	<p>The main program is used to both start and stop the WordHoard
 *	server.
 *
 *	<p>The first argument is required and specifies the operation: start
 *	or stop.
 *
 *	<p>The second argument is required and specifies
 *	the path to the server directory. This directory contains the server
 *	configuration file "wordhoard.config" and the log configuration file
 *	"log.config". The server log file is also written to this directory.
 *
 *	<p>On startup the server creates a {@link Bootstrap} object and
 *	registers it in the RMI registry using the bind name "WordHoard". On
 *	shutdown this registry entry is removed.
 *
 *	<p>Shutdown requests must originate from the local host, or
 *	they are ignored.
 */

public class Server {

	/**	Starts up the server.
	 *
	 *	@param	path		Path to server directory.
	 */

	private static void startup (String path) {
		try {
			WordHoardSettings.initializeSettings(true);
			Config.read(path);
			Logger.initialize();
			WordHoardSessionImpl.initialize();
			Bootstrap bootstrap = new BootstrapImpl();
			Registry registry = LocateRegistry.createRegistry(
				Config.getRmiRegistryPort());
			registry.rebind("WordHoard", bootstrap);
			System.out.println(
				WordHoardSettings.getString("WordHoardserverstarted"));
		} catch (Exception e) {
			System.out.println(
				WordHoardSettings.getString("WordHoardserverstartupfailure"));
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**	Shuts down the server.
	 *
	 *	<p>Shutdown requests must originate from the local host, or
	 *	they are ignored.
	 */

	private static void shutdown (String path) {
		try {
			WordHoardSettings.initializeSettings(true);
			Config.read(path);
			Logger.initialize(
				WordHoardSettings.getString("Servershuttingdown"));
			String uri =
				"//localhost:" + Config.getRmiRegistryPort() + "/WordHoard";
			Bootstrap bootstrap = (Bootstrap)Naming.lookup( uri );
			bootstrap.shutdown( uri );
			System.out.println(
				WordHoardSettings.getString("WordHoardserverstopped"));
		} catch (NotBoundException e) {
			e.printStackTrace();
			System.out.println(
				WordHoardSettings.getString(
					"TheWordHoardserverwasnotrunning"));
		} catch (Exception e) {
			Logger.log(
				Logger.ERROR, WordHoardSettings.getString("Shutdown"), e);
			Logger.terminate();
		}
	}

	/**	Main program.
	 *
	 *	@param	args	Command-line arguments.
	 */

	public static void main (String args[]) {
		if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
			startup(args[1]);
		} else if (args.length == 2 && args[0].equalsIgnoreCase("stop")) {
			shutdown(args[1]);
		} else {
			System.out.println(WordHoardSettings.getString("ServerUsage"));
			System.exit(1);
		}
	}

	/** Hides the default no-arg constructor. */

	private Server () {
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

