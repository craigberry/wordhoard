package edu.northwestern.at.utils.db.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Observer;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.spi.Configurable;

import edu.northwestern.at.utils.db.jdbc.SimpleConnectionPool;

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
 *	<li>hibernate.connection.driver_class -&gt; driverClassName
 *	<li>hibernate.connection.url -&gt; url
 *	<li>hibernate.connection.username -&gt; username
 *	<li>hibernate.connection.password -&gt; password
 *	<li>hibernate.connection.pool_size -&gt; maxPoolSize
 *	<li>hibernate.connection.autocommit -&gt; autocommit
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

public class SimpleHibernateConnectionProvider implements ConnectionProvider, Configurable {

	/**	JDBC connection pool. */

	private SimpleConnectionPool connectionPool;

	/**	Configures the connection provider.
	 *
	 *	@param	configurationValues	The Hibernate configuration map.
	 *
	 *	@throws	HibernateException	Error creating Hibernate connection pool.
	 */

	@Override
	public void configure(Map configurationValues)
		throws HibernateException
	{
		try {
			Properties poolProperties = new Properties();
			String jdbcDriverClass = (String) configurationValues.get(Environment.DRIVER);
			String jdbcURL = (String) configurationValues.get(Environment.URL);
			String username = (String) configurationValues.get(Environment.USER);
			String password = (String) configurationValues.get(Environment.PASS);
			username = username.replaceAll("\\\\","");
			password = password.replaceAll("\\\\","");
			String autocommit = (String) configurationValues.get(Environment.AUTOCOMMIT);
			String poolSize	= (String) configurationValues.get(Environment.POOL_SIZE);
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
	 *	@throws	SQLException	error from SQL connection
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
	 *	@throws	SQLException	error from SQL connection
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

	@Override
	public boolean isUnwrappableAs(Class unwrapType) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T unwrap(Class<T> unwrapType) {
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

