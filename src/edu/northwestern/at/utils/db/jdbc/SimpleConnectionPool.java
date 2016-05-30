package edu.northwestern.at.utils.db.jdbc;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.sql.*;
import java.util.*;

import org.hibernate.util.*;

/**	A simple JDBC connection pool.
 *
 *	<p>Pool parameters are specified via a Properties object passed to
 *	the constructor. The following properties are used:
 *
 *	<ul>
 *	<li>driverClassName = JDBC driver class name. Required.
 *	<li>url = Database URL. Required.
 *	<li>username = Username. Required.
 *	<li>password = Password. Required.
 *	<li>maxPoolSize = maximum pool size (the maximum number of connections
 *		that can be checked out of the pool concurrently). Default = 5.
 *	<li>idleTimeout = Idle connection timeout in seconds. Default = 600
 *		(10 minutes).
 *	<li>autocommit = true if connections should be initialized to autocommit,
 *		else false. Default = false.
 *	<li>connectionRetryCount = number of times to repeat attempts to obtain
 *		a connection. Default = 5.
 *	<li>connectrionRetryInterval = amount of time to wait in between attempts
 *		to obtain a connection after failures, in milliseconds. Default =
 *		500 (0.5 seconds).
 *	</ul>
 *
 *	<p>The pool starts out empty (no connections). When a connection is
 *	requested from the pool, if maxPoolSize connections have already been
 *	checked out, an exception is thrown. Otherwise, if the pool is not
 *	empty, the most recently used connection is checked out from the pool,
 *	tested, and returned. If the pool is empty, a new connection is created
 *	and returned.
 *
 *	<p>Connections are tested when they are checked out. If a connection
 *	is bad, it is closed and a new one is created. The test query statement
 *	is "select 1".
 *
 *	<p>Simple connection pools are observable. Observers are notified
 *	whenever a bad connection is detected in the test and whenever it takes
 *	more than one try to obtain a new connection. The parameter passed to the
 *	observer is an array of objects of length 2. For a bad connection, the
 *	first element is the Integer 1 and the second element is the exception
 *	describing the error, if any. For more than one try, the first element
 *	is the Integer 2 and the second element is the number of tries.
 *
 *	<p>The pool is examined every minute. Any connections in the pool which
 *	have not been used for idleTimout seconds are closed and removed from the
 *	pool.
 *
 *	<p>Simple connection pools are thread-safe.
 */

public class SimpleConnectionPool extends Observable {

	/**	Default maximum pool size. */

	private static final int DEFAULT_MAX_POOL_SIZE = 10;

	/**	Default idle timeout. */

	private static final int DEFAULT_IDLE_TIMEOUT = 600;

	/**	Default connection retry count. */

	private static final int DEFAULT_CONNECTION_RETRY_COUNT = 5;

	/**	Default connection retry interval in milliseconds. */

	private static final int DEFAULT_CONNECTION_RETRY_INTERVAL = 500;

	/**	Properties for the pool. */

	private Properties poolProperties;

	/**	Max pool size. */

	private int maxPoolSize;

	/**	Idle connection timeout in seconds. */

	private int idleTimeout;

	/**	Connection retry count. */

	private int connectionRetryCount;

	/**	Connection retry interval in milliseconds. */

	private int connectionRetryInterval;

	/**	Treeset of pooled connections, ordered by last checkin time. */

	private TreeSet connections = null;

	/**	Number of checked out connections. */

	private int numCheckedOut;

	/**	Idle connection timeout thread. */

	private Thread idleTimeoutThread;

	/**	Enable debugging output to console. */

	private boolean debug	= false;

	/**	Pooled connection info class. */

	private static class ConnectionInfo implements Comparable {

		/**	The connection. */

		private Connection connection;

		/**	Last checkin time. */

		private long lastCheckin;

		/**	Creates a pooled connection info object.
		 *
		 *	@param	connection		The connection.
		 */

		private ConnectionInfo (Connection connection) {
			this.connection = connection;
			this.lastCheckin = System.currentTimeMillis();
		}

		/**	Compares this object with another object.
		 *
		 *	@param	o		The object to be compared.
		 *
		 *	@return		A negative integer, zero, or a positive integer
		 *				as this object is less than, equal to, or greater
		 *				than the specified object. ConnectionInfo objects
		 *				are ordered by last checkin time.
		 */

		public int compareTo (Object o) {
			ConnectionInfo other = (ConnectionInfo)o;
			if (lastCheckin < other.lastCheckin) {
				return -1;
			} else if (lastCheckin > other.lastCheckin) {
				return +1;
			} else {
				return 0;
			}
		}
	}

	/**	Creates a connection pool.
	 *
	 *	@param	poolProperties	Properties for pool.
	 *
	 *	@throws	SQLException
	 */

	public SimpleConnectionPool (Properties poolProperties)
		throws SQLException
	{
		this.poolProperties	= poolProperties;
		connections	= new TreeSet();
		numCheckedOut = 0;
		maxPoolSize	= PropertiesHelper.getInt("maxPoolSize",
				poolProperties, DEFAULT_MAX_POOL_SIZE);
		if (maxPoolSize <= 0) maxPoolSize = DEFAULT_MAX_POOL_SIZE;
		idleTimeout	= PropertiesHelper.getInt("idleTimeout",
				poolProperties, DEFAULT_IDLE_TIMEOUT);
		if (idleTimeout <= 0) idleTimeout = DEFAULT_IDLE_TIMEOUT;
		connectionRetryCount = PropertiesHelper.getInt("connectionRetryCount",
			poolProperties, DEFAULT_CONNECTION_RETRY_COUNT);
		if (connectionRetryCount <= 0)
			connectionRetryCount = DEFAULT_CONNECTION_RETRY_COUNT;
		connectionRetryInterval =
			PropertiesHelper.getInt("connectionRetryInterval",
			poolProperties, DEFAULT_CONNECTION_RETRY_INTERVAL);
		if (connectionRetryInterval <= 0)
			connectionRetryInterval = DEFAULT_CONNECTION_RETRY_INTERVAL;
		String driverClassName = poolProperties.getProperty("driverClassName");
		try {
			Class.forName(driverClassName);
    	} catch (Exception e) {
    		throw new SQLException(
    			"JDBC driver \"" + driverClassName + "\" not found." );
		}
		idleTimeoutThread = new Thread(
			new Runnable () {
				public void run () {
					try {
						while (!Thread.interrupted()) {
							Thread.sleep(60000);
							timeoutIdleConnections();
						}
					} catch (InterruptedException e) {
					}
				}
			}
		);
		idleTimeoutThread.setDaemon(true);
		idleTimeoutThread.start();
	}

	/**	Creates a connection.
	 *
	 *	@return		A connection.
	 *
	 *	@throws	SQLException
	 */

	private Connection createConnection()
		throws SQLException
	{
		String url= poolProperties.getProperty("url");
		String username	= poolProperties.getProperty("username");
		String password	= poolProperties.getProperty("password");
		boolean autocommit	= PropertiesHelper.getBoolean(
			"autocommit" , poolProperties , false);
		int numTries = 0;
		while (numTries < connectionRetryCount) {
			try {
				Connection connection= DriverManager.getConnection(url, username,
					password);
				connection.setAutoCommit(autocommit);
				if (numTries > 0) {
					setChanged();
					notifyObservers(
						new Object[] {
							new Integer(2),
							new Integer(numTries+1)
						}
					);
				}
				return connection;
			} catch (Exception e) {
			}
			numTries++;
			try {
				Thread.sleep(connectionRetryInterval);
			} catch (Exception e) {
			}
		}
		throw new SQLException("Unable to obtain connection after " +
			connectionRetryCount + " tries");
	}

	/**	Tests a connection to see if it is alive.
	 *
	 *	@param		connection		The connection to test.
	 *
	 *	@return 	True if the connection is alive.
	 */

	private boolean alive (Connection connection) {
		boolean result = false;
		ResultSet resultSet	= null;
		Statement statement	= null;
		Exception exception = null;
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery("select 1");
			result = resultSet.next();
		} catch (Exception e) {
			exception = e;
		} finally {
			try {
				if (resultSet != null) resultSet.close();
				if (statement != null) statement.close();
			} catch (Exception e) {
			}
		}
		if (!result) {
			setChanged();
			notifyObservers(
				new Object[] {
					new Integer(1),
					exception
				}
			);
		}
		return result;
	}

	/**	Closes a connection.
	 *
	 *	@param	connection	The connection to close.
	 */

	private void closeConnection (Connection connection) {
		try {
			connection.close();
		} catch (Exception e) {
		}
	}

	/**	Times out idle connections.
	 */

	private synchronized void timeoutIdleConnections () {
		long timeThreshold = System.currentTimeMillis() - 1000 * idleTimeout;
		for (Iterator it = connections.iterator(); it.hasNext(); ) {
			ConnectionInfo info = (ConnectionInfo)it.next();
			if (info.lastCheckin < timeThreshold) {
				closeConnection(info.connection);
				it.remove();
			}
		}
	}

	/**	Gets a connection from the pool.
	 *
	 *	@return		A connection.
	 *
	 *	@throws	SQLException
	 */

	public synchronized Connection getConnection ()
		throws SQLException
	{
		if (numCheckedOut >= maxPoolSize)
			throw new SQLException("Max connections alread checked out." +
				" (max = " + maxPoolSize + ")");
		Connection connection = null;
		if (connections.size() == 0) {
			connection = createConnection();
		} else {
			ConnectionInfo info = (ConnectionInfo)connections.last();
			connections.remove(info);
			connection = info.connection;
			if (!alive(connection)) {
				closeConnection(connection);
				connection = createConnection();
			}
		}
		numCheckedOut++;

		if (debug) {
			System.out.println(
				"SimpleConnectionPool: getConnection: numCheckedOut=" +
					numCheckedOut);
		}

		return connection;
	}

	/**	Releases a connection back to the pool.
	 *
	 *	@param	connection	The connection to return to the pool.
	 */

	public synchronized void releaseConnection (Connection connection) {
		connections.add(new ConnectionInfo(connection));
		numCheckedOut--;

		if (debug) {
			System.out.println(
				"SimpleConnectionPool: releaseConnection: numCheckedOut=" +
					numCheckedOut);
		}
	}

	/**	Closes the pool.
	 *
	 *	<p>The pool may not be reused after it is closed. All connections
	 *	are closed.
	 */

	public synchronized void close() {
		for (Iterator it = connections.iterator(); it.hasNext(); ) {
			ConnectionInfo info = (ConnectionInfo)it.next();
			closeConnection(info.connection);
		}
		connections = null;
		idleTimeoutThread.interrupt();
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

