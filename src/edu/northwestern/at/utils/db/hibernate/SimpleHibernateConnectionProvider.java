package edu.northwestern.at.utils.db.hibernate;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.sql.*;
import java.util.*;

import org.hibernate.cfg.*;
import org.hibernate.connection.*;
import org.hibernate.HibernateException;

import edu.northwestern.at.utils.db.jdbc.*;

/** A simple Hibernate connection provider.
 *
 *	<p>This class wraps a
 *	{@link edu.northwestern.at.utils.db.jdbc.SimpleConnectionPool
 *	SimpleConnectionPool}.
 *
 *	<p>Hibernate configuration properties are used to configure the
 *	SimpleConnectionPool properties as follows:
 *
 *	<ul>
 *	<li>hibernate.connection.driver_class -> driverClassName
 *	<li>hibernate.connection.url -> url
 *	<li>hibernate.connection.username -> username
 *	<li>hibernate.connection.password -> password
 *	<li>hibernate.connection.pool_size -> maxPoolSize
 *	<li>hibernate.connection.autocommit -> autocommit
 *	</ul>
 *
 *	<p>The remaining SimpleConnectionPool properties use their
 *	default values:
 *
 *	<ul>
 *	<li>idleTimeout = 600 seconds (10 minutes).
 *	<li>connectionRetryCount = 5.
 *	<li>connectionRetryInterval = 500 milliseconds (0.5 seconds).
 *	</ul>
 */

public class SimpleHibernateConnectionProvider implements ConnectionProvider {

	/**	JDBC connection pool. */

	private SimpleConnectionPool connectionPool;

	/**	Configures the connection provider.
	 *
	 *	@param	properties	The Hibernate configuration properties.
	 *
	 *	@throws	HibernateException
	 */

	public void configure (Properties properties)
		throws HibernateException
	{
		try {
			Properties poolProperties = new Properties();
			String jdbcDriverClass = properties.getProperty(Environment.DRIVER);
			String jdbcURL = properties.getProperty(Environment.URL);
			String username = properties.getProperty(Environment.USER);
			String password = properties.getProperty(Environment.PASS);
			username = username.replaceAll("\\\\","");
			password = password.replaceAll("\\\\","");
			String autocommit = properties.getProperty(Environment.AUTOCOMMIT);
			String poolSize	= properties.getProperty(Environment.POOL_SIZE);
			poolProperties.put("driverClassName", jdbcDriverClass);
			poolProperties.put("url", jdbcURL);
			poolProperties.put("username", username);
			poolProperties.put("password", password);
			if	(autocommit != null && autocommit.trim().length() > 0){
				poolProperties.put("autocommit", autocommit);
			} else {
				poolProperties.put("autocommit", String.valueOf(Boolean.FALSE));
			}
			if	(poolSize != null && poolSize.trim().length() > 0 &&
				Integer.parseInt(poolSize) > 0)
					poolProperties.put("maxPoolSize", poolSize);
			connectionPool = new SimpleConnectionPool(poolProperties);
		} catch (Exception e) {
			if (connectionPool != null) connectionPool.close();
			connectionPool	= null;
			throw new HibernateException(
				"Could not create a connection pool.", e);
		}
	}

	/**	Gets a connection from the pool.
	 *
	 *	@return		A connection.
	 *
	 *	@throws	SQLException
	 */

	public Connection getConnection ()
		throws SQLException
	{
		return connectionPool.getConnection();
	}

	/**	Releases a connection back to the pool.
	 *
	 *	@param	connection	The connection to return to the pool.
	 *
	 *	@throws	SQLException
	 */

	public void closeConnection (Connection connection)
		throws SQLException
	{
		connectionPool.releaseConnection(connection);
	}

	/**	Closes down the connection provider.
	 */

	public void close () {
		if (connectionPool != null) connectionPool.close();
		connectionPool = null;
	}

	/**	Indicates if aggressive release is supported.
	 *
	 *	@return		false (aggressive release is not supported).
	 */

	public boolean supportsAggressiveRelease() {
		return false;
	}

	/**	Adds an observer.
	 *
	 *	<p>The observer is notified whenever a bad JDBC connection is
	 *	detected and whenever it takes more than one attempt to obtain
	 *	a new connection. See SimpleConnectionPool for more details.
	 *
	 *	@param	o	Observer.
	 */

	public void addObserver (Observer o) {
		connectionPool.addObserver(o);
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

