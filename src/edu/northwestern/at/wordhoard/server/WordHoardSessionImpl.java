package edu.northwestern.at.wordhoard.server;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.*;
import org.hibernate.exception.*;
import org.hibernate.cfg.*;
import org.hibernate.query.Query;
import org.hibernate.jdbc.ReturningWork;

import edu.northwestern.at.utils.ClassUtils;
import edu.northwestern.at.utils.db.PersistenceException;
import edu.northwestern.at.utils.net.ldap.*;
import edu.northwestern.at.utils.PrintfFormat;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.annotations.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.server.model.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.BadOwnerException;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.UserDataObjectUpdater;
import edu.northwestern.at.wordhoard.swing.WordHoardSettings;

/**	Session remote object implementation.
 */

public class WordHoardSessionImpl extends UnicastRemoteObject
	implements WordHoardSession
{

	/**	Global synchronization lock. */

	private static Object lock = new Object();

	/**	Next available session id. */

	private static long nextSessionId;

	/**	Maps session ids to active sessions. */

	private static Map sessions = new HashMap();

	/**	Hibernate session factory for accounts. */

	private static SessionFactory sessionFactory;

	/**	Hibernate session factory for user data. */

	private static SessionFactory userDataSessionFactory;

	/**	Session id. */

	private Long id;

	/**	The dotted-decimal IP address of the client host. */

	private String host;
	
	/**	The domain name of the client host. */
	
	private String domain;

	/**	Login account, or null if not logged in. */

	private Account loginAccount;

	/**	The date and time the session was created. */

	private Date loginDate = new Date();

	/**	True if session active. */

	private boolean active = true;

	/**	The time the session was last tickled by the client. */

	private long lastTickleTime = loginDate.getTime();

	/**	Creates a new session.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 */

	WordHoardSessionImpl ()
		throws RemoteException
	{
		super(Config.getRmiPort());
		synchronized(lock) {
			id = new Long(nextSessionId++);
			try {
				host = UnicastRemoteObject.getClientHost();
				domain = InetAddress.getByName(host).getCanonicalHostName();
			} catch (ServerNotActiveException e) {
				Logger.log(Logger.ERROR,
					WordHoardSettings.getString("Getclienthost"), e);
			} catch (java.net.UnknownHostException e) {
			}
			sessions.put(id, this);
			Logger.log(this, Logger.INFO,
				WordHoardSettings.getString("Sessionbegin") + host + " (" + domain + ")");
		}
	}

	/**	Ends the session.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 */

	public void endSession ()
		throws RemoteException
	{
		synchronized(lock) {
			active = false;
			sessions.remove(id);
			Logger.log(this, Logger.INFO,
				WordHoardSettings.getString("Sessionend") + host + " (" + domain + ")");
		}
	}

	/**	Tickles the session.
	 *
	 *	<p>Clients should tickle their sessions every 30 minutes. Sessions
	 *	which go untickled for 2 hours are considered to be dead and are
	 *	timed out and terminated.
	 *
	 *	@throws RemoteException	error in remote connection.
	 */

	public void tickle ()
		throws RemoteException
	{
		lastTickleTime = System.currentTimeMillis();
	}

	/**	Logs a message.
	 *
	 *	@param	level		Log message level.
	 *
	 *	@param	msg			Log message.
	 *
	 *	@throws RemoteException	error in remote connection.
	 */

	public void logMessage (int level, String msg)
		throws RemoteException
	{
		StringTokenizer tok = new StringTokenizer(msg, "\n\r");
		StringBuffer buf = new StringBuffer();
		while (tok.hasMoreTokens())
			buf.append(tok.nextToken() + "\n");
		int len = buf.length();
		if (len > 0) buf.setLength(len-1);
		Logger.log(this, level, buf.toString());
	}

	/**	Logs in.
	 *
	 *	<p>Logins are not permitted using the special "system" account.
	 *
	 *	@param	username	Username.
	 *
	 *	@param	password	Password.
	 *
	 *	@return				Account record with password set to null
	 *						if login successful. Null if login unsuccessful.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 */

	public Account login (String username, String password)
		throws RemoteException
	{
		loginAccount = null;
		if (username.equals("system")) return null;
		Session session = sessionFactory.openSession();
		try {
			Query q = session.createQuery(
				"from Account account " +
				"where account.username = :username");
			q.setParameter("username", username);
			loginAccount = (Account)q.uniqueResult();
			boolean loginValid = loginAccount != null;
			if (loginValid) {
				if (loginAccount.getNuAccount()) {
					loginValid = LdapDirectory.authenticateNetid(
						"ldaps://registry.northwestern.edu:636",
						"uid=%1, ou=people, dc=northwestern, dc=edu",
						username, password);
				} else {
					loginValid = loginAccount.passwordIsValid(password);
				}
			}
			if (loginValid) {
				Logger.log(this, Logger.INFO,
					WordHoardSettings.getString("LoginServer"));
				loginAccount.setPassword(null);
				return loginAccount;
			} else {
				Logger.log(this, Logger.INFO,
					WordHoardSettings.getString("Failedlogin") + username);
				loginAccount = null;
				return null;
			}
		} finally {
			session.close();
		}
	}

	/**	Logs out.
	 *
	 *	@throws RemoteException	error in remote connection.
	 */

	public void logout ()
		throws RemoteException
	{
		Logger.log(this, Logger.INFO,
			WordHoardSettings.getString("LogoutServer"));
		loginAccount = null;
	}

	/**	Gets all the accounts.
	 *
	 *	@return			List of all accounts in increasing order by username,
	 *					without the special "system" account.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 */

	public List getAccounts ()
		throws RemoteException, WordHoardError
	{
		if (loginAccount == null || !loginAccount.getCanManageAccounts())
			throw new WordHoardError(
				WordHoardSettings.getString(
					"Youarenotpermittedtomanageaccounts"));
		Session session = sessionFactory.openSession();
		try {
			Query q = session.createQuery(
				"from Account where username != 'system' order by username");
			return q.list();
		} finally {
			session.close();
		}
	}

	/**	Creates or updates an account.
	 *
	 *	@param	account		Account.
	 *
	 *	@return				Id of the account.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 */

	public Long createOrUpdateAccount (Account account)
		throws RemoteException, WordHoardError
	{
		if (loginAccount == null || !loginAccount.getCanManageAccounts())
			throw new WordHoardError(
				WordHoardSettings.getString(
					"Youarenotpermittedtomanageaccounts"));
		String username = account.getUsername();
		if (username == null || username.length() == 0)
			throw new WordHoardError(
				WordHoardSettings.getString("Accountsmusthaveausername"));
		if (!account.getNuAccount() && account.getPassword() == null)
			throw new WordHoardError(
				WordHoardSettings.getString(
					"NonNUaccountsmusthaveapassword"));
		Session session = sessionFactory.openSession();
		try {
			Transaction t = session.beginTransaction();
			session.saveOrUpdate(account);
			t.commit();
			PrintfFormat fmt = new PrintfFormat(
				WordHoardSettings.getString("Savedaccount"));
			String logMessage = fmt.sprintf(
				new Object[]{account.getUsername(), account.getId()});
			Logger.log(this, Logger.INFO, logMessage);
			return account.getId();
		} catch (ConstraintViolationException e) {
			PrintfFormat fmt =
				new PrintfFormat(
					WordHoardSettings.getString(
						"Theusernameisalreadybeingused"));
			String errMsg =
				fmt.sprintf(new String[]{account.getUsername()});
			throw new WordHoardError(errMsg);
		} finally {
			session.close();
		}
	}

	/**	Deletes an account.
	 *
	 *	@param	id		Account id.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 */

	public void deleteAccount (Long id)
		throws RemoteException, WordHoardError
	{
		if (loginAccount == null || !loginAccount.getCanManageAccounts())
			throw new WordHoardError(
				WordHoardSettings.getString(
					"Youarenotpermittedtomanageaccounts"));
		Session session = sessionFactory.openSession();
		try {
			Account account = (Account)session.load(Account.class, id);
			String accountUsername = account.getUsername();
			String sid = account.getId() + "";
			Transaction t = session.beginTransaction();
			session.delete(account);
			t.commit();
			PrintfFormat fmt = new PrintfFormat(
				WordHoardSettings.getString("Deletedaccount"));
			String logMessage = fmt.sprintf(
				new String[]{accountUsername, sid});
			Logger.log(this, Logger.INFO, logMessage);
		} finally {
			session.close();
		}
	}

	/**	Returns a string representation of the session.
	 *
	 *	@return		String representation.
	 */

	public String toString () {
		return id + ": " +
			(loginAccount == null ? "<null>" : loginAccount.getUsername());
	}

	/**	Times out idle sessions.
	 *
	 *	<p>This method wakes up once per minute and checks all active sessions.
	 *	Sessions which have not been tickled by their client for the last
	 *	two hours are terminated.
	 *
	 *	@throws	InterruptedException	thread was interrupted.
	 */

	private static void timeOutIdleSessions ()
		throws InterruptedException
	{
		while (true) {
			Thread.sleep(60*1000);
			synchronized (lock) {
				List sessionsCopy = new ArrayList(sessions.values());
				long curTime = System.currentTimeMillis();
				for (Iterator it = sessionsCopy.iterator(); it.hasNext(); ) {
					WordHoardSessionImpl session =
						(WordHoardSessionImpl)it.next();
					long idleTime = curTime - session.lastTickleTime;
					if (idleTime > 2*60*60*1000L) {
						session.active = false;
						sessions.remove(session.id);
						Logger.log(session, Logger.INFO,
							WordHoardSettings.getString("Sessiontimedout"));
					}
				}
			}
		}
	}

	/**	Initializes the class.
	 *
	 *	@throws	Exception	general error.
	 */

	static void initialize ()
		throws Exception
	{
		//	Get the Hibernate session factory for accounts.

		Configuration cfg = new Configuration();
		cfg.setProperty(
			"hibernate.dialect", "edu.northwestern.at.utils.db.mysql.WordHoardMySQLDialect");
		cfg.setProperty(
			"hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
		cfg.setProperty(
			"hibernate.c3p0.max_size", "100");
		cfg.setProperty(
			"hibernate.c3p0.idle_test_period", "14400");
		cfg.setProperty(
			"hibernate.connection.url",
			Config.getDatabaseURL());
		cfg.setProperty(
			"hibernate.connection.username",
			Config.getDatabaseUsername());
		cfg.setProperty(
			"hibernate.connection.password",
			Config.getDatabasePassword());
		cfg.addClass(Account.class);
		sessionFactory = cfg.buildSessionFactory();

		//	Get the Hibernate session factory for user data.

		Configuration udCfg = new Configuration();
		udCfg.setProperty(
			"hibernate.dialect", "edu.northwestern.at.utils.db.mysql.WordHoardMySQLDialect");
		udCfg.setProperty(
			"hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
		udCfg.setProperty(
			"hibernate.c3p0.max_size", "100");
		udCfg.setProperty(
			"hibernate.c3p0.idle_test_period", "14400");
		udCfg.setProperty(
			"hibernate.connection.url",
			Config.getUserDataDatabaseURL());
		udCfg.setProperty(
			"hibernate.connection.username",
			Config.getUserDataDatabaseUsername());
		udCfg.setProperty(
			"hibernate.connection.password",
			Config.getUserDataDatabasePassword());
		udCfg.addClass(WHQuery.class);
		udCfg.addClass(WordSet.class);
		udCfg.addClass(WordSetTotalWordFormCount.class);
		udCfg.addClass(WordSetWordCount.class);
		udCfg.addClass(AuthoredTextAnnotation.class);
		udCfg.addClass(AnnotationCategory.class);
		udCfg.addClass(TextWrapper.class);
		udCfg.addClass(WorkSet.class);
		udCfg.addClass(UserGroup.class);
		udCfg.addClass(UserGroupPermission.class);
		udCfg.addClass(Phrase.class);
		udCfg.addClass(PhraseSetPhraseCount.class);
		udCfg.addClass(PhraseSetTotalWordFormPhraseCount.class);
		userDataSessionFactory = udCfg.buildSessionFactory();

		//	Start the idle session timeout thread.

		new Thread (
			new Runnable () {
				public void run () {
					try {
						timeOutIdleSessions();
					} catch (InterruptedException e) {
						Logger.log(Logger.ERROR,
							WordHoardSettings.getString(
								"Idlesessiontimeoutthread"), e);
					}
				}
			}
		).start();

		//	Create the initial admin account, if necessary.

		createInitialAccount();
		
		//	Create the special system account, if necessary.
		
		createSystemAccount();
	}

	/**	Creates the initial administrator account, if necessary.
	 *
	 *	<p>If no accounts exist, an initial temporary account is created
	 *	with username "admin" and password "admin".  The account is not
	 *	an NU account, and it has the can manage accounts privilege.
	 *	A second permanent account is created with username "system" and
	 *	password "system".  That account is also not an NU account,
	 *	and it has no special privileges.  The system account is used
	 *	to create the default user data objects.
	 *	</p>
	 *
	 *	@throws Exception	general error.
	 */

	private static void createInitialAccount ()
		throws Exception
	{
		Session session = sessionFactory.openSession();
		try {
			Query q = session.createQuery("select count(*) from Account");
			Long numAccountsLong = (Long)q.uniqueResult();
			long numAccounts = numAccountsLong.longValue();
			if (numAccounts > 0) return;
			Account adminAccount = new Account();
			adminAccount.setUsername("admin");
			adminAccount.setPassword("admin");
			adminAccount.setName("Administrator");
			adminAccount.setNuAccount(false);
			adminAccount.setCanManageAccounts(true);
			Transaction t = session.beginTransaction();
			session.save(adminAccount);
			t.commit();
		} finally {
			session.close();
		}
	}
	
	/**	Creates the special "system" account, if necessary.
	 *
	 *	@throws Exception	general error.
	 */
	 
	private static void createSystemAccount ()
		throws Exception
	{
		Session session = sessionFactory.openSession();
		try {
			Query q = session.createQuery(
				"from Account where username='system'");
			if (q.uniqueResult() != null) return;
			Account systemAccount = new Account();
			systemAccount.setUsername("system");
			systemAccount.setPassword(null);
			systemAccount.setName("System");
			systemAccount.setNuAccount(false);
			systemAccount.setCanManageAccounts(false);
			Transaction t = session.beginTransaction();
			session.save(systemAccount);
			t.commit();
		} finally {
			session.close();
		}
	}

	/**	Creates a user data object.
	 *
	 *	@param	userDataObject	User data object.
	 *
	 *	@return					Id of the object.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 */

	public Long createUserDataObject (UserDataObject userDataObject)
		throws RemoteException, WordHoardError, BadOwnerException,
			PersistenceException
	{
		if (loginAccount == null)
			throw new BadOwnerException(
				WordHoardSettings.getString(
					"Youarenotpermittedtocreatethisobject"));
		Session session = userDataSessionFactory.openSession();
		Transaction t = null;
		Long result	= new Long( -1 );
		try {
			t = session.beginTransaction();
			session.save(userDataObject);
			t.commit();
			result	= userDataObject.getId();
			PrintfFormat fmt = new PrintfFormat(
				WordHoardSettings.getString("Createduserobject"));
			String logMessage = fmt.sprintf(
				new Object[]{ClassUtils.unqualifiedName(
						userDataObject.getClass().getName()),
					userDataObject.getTitle(),
					userDataObject.getId()});
			Logger.log(this, Logger.INFO, logMessage);
		} catch (HibernateException e) {
			e.printStackTrace();
			try {
				if (t != null) t.rollback();
			} catch (Exception ignored) {}
			throw new PersistenceException( e );
		} finally {
			session.close();
		}
		return result;
	}

	/**	Updates a user data object.
	 *
	 *	@param	userDataObject			The user data object to update.
	 *	@param	userDataObjectUpdater	Method which updates fields of
	 *									user data object.
	 *
	 *	@return							true if update succeeds,
	 *									false otherwise.
	 *
	 *	@throws							BadOwnerException if user is not
	 *									logged in or is not the owner of the
	 *									user data object.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 */

	public Long updateUserDataObject (UserDataObject userDataObject,
			UserDataObjectUpdater userDataObjectUpdater)
		throws RemoteException, WordHoardError, BadOwnerException,
			PersistenceException
	{
		if (loginAccount == null)
			throw new BadOwnerException(
				WordHoardSettings.getString(
					"Youarenotpermittedtomodifythisobject"));
		Session session = userDataSessionFactory.openSession();
		Transaction t = null;
		Long result	= new Long( -1 );
		try {
			if (!loginAccount.getUsername().equals(userDataObject.getOwner()) && !loginAccount.getCanManageAccounts())
				throw new BadOwnerException(
					WordHoardSettings.getString(
						"Youarenotpermittedtomodifythisobject"));
			UserDataObject udo =
				(UserDataObject)session.get(
					userDataObject.getClass(), userDataObject.getId());
			t = session.beginTransaction();
			userDataObjectUpdater.update(udo);
			t.commit();
			result	= udo.getId();
			PrintfFormat fmt = new PrintfFormat(
				WordHoardSettings.getString("Modifieduserobject"));
			String logMessage = fmt.sprintf(
				new Object[]{ClassUtils.unqualifiedName(
						udo.getClass().getName()), udo.getTitle(),
						udo.getId()});
			Logger.log(this, Logger.INFO, logMessage);
		} catch (HibernateException e) {
			e.printStackTrace();
			try {
				if (t != null) t.rollback();
			} catch (Exception ignored) {}
			throw new PersistenceException( e );
		} finally {
			session.close();
		}
		return result;
	}

	/**	Deletes a user data object.
	 *
	 *	@param	udoClass				Class of user data object to delete.
	 *	@param	id						ID of user data object to delete.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 */

	public void deleteUserDataObject (Class udoClass, Long id)
		throws RemoteException, WordHoardError, PersistenceException
	{
		if (loginAccount == null)
			throw new WordHoardError(
				WordHoardSettings.getString(
					"Youarenotpermittedtodeletethisobject"));
		Session session = userDataSessionFactory.openSession();
		Transaction t = null;
		try {
			UserDataObject udo = (UserDataObject)session.get(udoClass, id);

			if (!loginAccount.getUsername().equals(udo.getOwner()) && !loginAccount.getCanManageAccounts()) 
				throw new WordHoardError(
					WordHoardSettings.getString(
						"Youarenotpermittedtodeletethisobject"));
			String title = udo.getTitle();
			String sid = udo.getId() + "";
			t = session.beginTransaction();
			session.delete(udo);
			t.commit();
			PrintfFormat fmt = new PrintfFormat(
				WordHoardSettings.getString("Deleteduserobject"));
			String logMessage = fmt.sprintf(
				new String[]{ClassUtils.unqualifiedName(
					udoClass.getName()), title, sid});
			Logger.log(this, Logger.INFO, logMessage);
		} catch (HibernateException e) {
			try {
				if (t != null) t.rollback();
			} catch (Exception ignored) {}
			throw new PersistenceException( e );
		} finally {
			session.close();
		}
	}

	/**	Deletes a word set.
	 *
	 *	@param	wordSet		The word set to delete.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 */

	public void deleteWordSet (WordSet wordSet)
		throws RemoteException, WordHoardError, PersistenceException
	{
		if (loginAccount == null)
			throw new WordHoardError(
				WordHoardSettings.getString(
					"Youarenotpermittedtodeletethiswordset"));
		Session session = userDataSessionFactory.openSession();
		Transaction t = null;
		try {
			WordSet ws =
				(WordSet)session.get(WordSet.class, wordSet.getId());
			if (!loginAccount.getUsername().equals(ws.getOwner()))
				throw new WordHoardError(
					WordHoardSettings.getString(
						"Youarenotpermittedtodeletethiswordset"));
			t = session.beginTransaction();
			String sid	= ws.getId().toString();
			String title = ws.getTitle();
			session.createQuery(
				"delete WordSetWordCount w where w.wordSet=" + sid
			).executeUpdate();
			session.createQuery(
				"delete WordSetTotalWordFormCount w " +
				"where w.wordSet=" + sid
			).executeUpdate();
			session.delete(ws);
			t.commit();
			PrintfFormat fmt = new PrintfFormat(
				WordHoardSettings.getString("Deleteduserobject"));
			String logMessage = fmt.sprintf(
				new String[]{"WordSet", title, sid});
			Logger.log(this, Logger.INFO, logMessage);
		} catch (HibernateException e) {
			try {
				if (t != null) t.rollback();
			} catch (Exception ignored) {}
			throw new PersistenceException( e );
		} finally {
			session.close();
		}
	}

	/**	Insert data via SQL.
	 *
	 *	@param	insertString	The SQL insert string.
	 *	@param	session			Persistence manager session.
	 *
	 *	@return					Count of objects inserted.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 *
	 *	<p>
	 *	This method provides for executing a MySQL batch insert query.
	 *	This is much faster than using standard Hibernate facilities
	 *	for batch inserts.
	 *	</p>
	 */

	protected int insertViaSQL( String insertString , Session session )
		throws PersistenceException
	{
		int result	= 0;
		Statement statement = null;
		try {
			Connection connection = session.doReturningWork(new ReturningWork<Connection>() {
				@Override
				public Connection execute(Connection conn) throws SQLException {
					return conn;
				}
			});
			statement = connection.createStatement();
			result = statement.executeUpdate(insertString);
			statement.close();
		} catch (Exception e) {
			throw new PersistenceException( e );
		} finally {
			try {
				if (statement != null) statement.close();
			} catch (Exception e){}
		}
		return result;
	}

	/**	Performs batch inserts using prepared MySQL insert statements.
	 *
	 *	@param	insertStatements	String array of MySQL insert statements.
	 *
	 *	@return	count of inserts performed.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 */

	public int performBatchInserts (String[] insertStatements)
		throws RemoteException, WordHoardError, PersistenceException
	{
		int result	= 0;
		if (loginAccount == null)
			throw new WordHoardError(
				WordHoardSettings.getString(
					"Youarenotpermittedtoadddata"));
		Session session = userDataSessionFactory.openSession();
		Transaction t	= null;
		try {
			t = session.beginTransaction();
			for (int i = 0; i < insertStatements.length; i++) {
				result	+=
					insertViaSQL(insertStatements[i], session);
			}
			t.commit();
		} catch (HibernateException e) {
			try {
				if (t != null) t.rollback();
			} catch (Exception ignored) {}
			throw new PersistenceException( e );
		} finally {
			session.close();
		}
		return result;
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

