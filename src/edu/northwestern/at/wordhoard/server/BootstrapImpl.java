package edu.northwestern.at.wordhoard.server;

/*	Please see the license information at the end of this file. */

import java.rmi.*;
import java.rmi.server.*;
import java.net.*;

/**	Server bootstrap remote object implementation.
 */

public class BootstrapImpl extends UnicastRemoteObject
	implements Bootstrap
{
	/**	Creates a new BootstrapImpl object.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 */

	BootstrapImpl ()
		throws RemoteException
	{
		super(Config.getRmiPort());
	}

	/**	Starts a new session.
	 *
	 *	@return			A session object.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 */

	public WordHoardSession startSession ()
		throws RemoteException
	{
		return new WordHoardSessionImpl();
	}

	/**	Shuts down the server.
	 *
	 *  @param	uri		The URI for the server object.
	 *
	 *	<p>Shutdown requests must originate from the local host, or
	 *	they are ignored.
	 *
	 *	@throws	Exception	general error.
	 */

	public void shutdown ( String uri )
		throws Exception
	{
		String myAddress = InetAddress.getLocalHost().getHostAddress();
		String hisAddress = UnicastRemoteObject.getClientHost();
		if (!myAddress.equals(hisAddress)) return;
		Naming.unbind(uri);
		new Thread (
			new Runnable () {
				public void run () {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
					}
					Logger.terminate();
					System.exit(0);
				}
			}
		).start();
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

