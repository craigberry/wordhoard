package edu.northwestern.at.wordhoard.server;

/*	Please see the license information at the end of this file. */

import java.rmi.*;
import java.util.*;


import edu.northwestern.at.utils.db.PersistenceException;
import edu.northwestern.at.wordhoard.model.userdata.UserDataObject;
import edu.northwestern.at.wordhoard.model.userdata.WordSet;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.BadOwnerException;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.UserDataObjectUpdater;
import edu.northwestern.at.wordhoard.server.model.*;

/**	Session remote object.
 */

public interface WordHoardSession extends Remote {

	/**	Ends the session.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 */

	public void endSession ()
		throws RemoteException;

	/**	Tickles the session.
	 *
	 *	<p>Clients should tickle their sessions every 30 minutes. Sessions
	 *	which go untickled for 2 hours are considered to be dead and are
	 *	timed out and terminated.
	 *
	 *	@throws RemoteException	error in remote connection.
	 */

	public void tickle ()
		throws RemoteException;

	/**	Logs a message.
	 *
	 *	@param	level		Log message level.
	 *
	 *	@param	msg			Log message.
	 *
	 *	@throws RemoteException	error in remote connection.
	 */

	public void logMessage (int level, String msg)
		throws RemoteException;

	/**	Logs in.
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
		throws RemoteException;

	/**	Logs out.
	 *
	 *	@throws RemoteException	error in remote connection.
	 */

	public void logout ()
		throws RemoteException;

	/**	Gets all the accounts.
	 *
	 *	@return			List of all accounts in increasing order by username.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 */

	public List getAccounts ()
		throws RemoteException, WordHoardError;

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
		throws RemoteException, WordHoardError;

	/**	Deletes an account.
	 *
	 *	@param	id		Account id.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 */

	public void deleteAccount (Long id)
		throws RemoteException, WordHoardError;

	/**	Creates a user data object.
	 *
	 *	@param	userDataObject			User data object.
	 *
	 *	@return							Id of the object.
	 *
	 *	@throws							BadOwnerException if user is not
	 *									logged in or is not the owner of the
	 *									user data object.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Long createUserDataObject (UserDataObject userDataObject)
		throws RemoteException, WordHoardError, BadOwnerException,
			PersistenceException;

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
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public Long updateUserDataObject (UserDataObject userDataObject,
			UserDataObjectUpdater userDataObjectUpdater)
		throws RemoteException, WordHoardError, BadOwnerException,
			PersistenceException;

	/**	Deletes a user data object.
	 *
	 *	@param	udoClass	Class of user data object to delete.
	 *	@param	id			ID of user data object to delete.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public void deleteUserDataObject (Class udoClass, Long id)
		throws RemoteException, WordHoardError, PersistenceException;

	/**	Deletes a word set.
	 *
	 *	@param	wordSet		Word set to delete.
	 *
	 *	@throws	RemoteException	error in remote connection.
	 *
	 *	@throws	WordHoardError	WordHoard-specific error.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public void deleteWordSet(WordSet wordSet)
		throws RemoteException, WordHoardError, PersistenceException;

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

	public int performBatchInserts (String[] insertStatements )
		throws RemoteException, WordHoardError, PersistenceException;
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

