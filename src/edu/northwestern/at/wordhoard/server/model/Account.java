package edu.northwestern.at.wordhoard.server.model;

/*	Please see the license information at the end of this file. */

import java.io.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.crypto.*;

/**	An account.
 *
 *	<p>Each account has the following attributes:
 *
 *	<ul>
 *	<li>A unique persistence id.
 *	<li>The username. For NU accounts this is the user's netid.
 *	<li>The password. Null and unused for NU accounts. Encrypted for non-NU
 *		accounts.
 *	<li>The user's name.
 *	<li>Whether or not the account is an NU account.
 *	<li>Whether or not the account is permitted to manage other accounts.
 *	</ul>
 *
 *	@hibernate.class table="account"
 */
 
 public class Account implements Serializable, Cloneable {
 
	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	Username. */
	
	private String username;
	
	/**	Encrypted password for non-NU accounts. */
	
	private String password;
	
	/**	Name. */
	
	private String name;
	
	/**	True if NU account. */
	
	private boolean nuAccount;
	
	/**	True if can manage other accounts. */
	
	private boolean canManageAccounts;
	
	/**	Creates a new account.
	 */
	
	public Account () {
	}
	
	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="native"
	 */
	 
	public Long getId () {
		return id;
	}
	
	/**	Sets the unique id.
	 *
	 *	@param	id		The unique id.
	 */
	 
	public void setId (Long id) {
		this.id = id;
	}
	
	/**	Gets the username.
	 *
	 *	@return		The username.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="username" unique="true"
	 */
	 
	public String getUsername () {
		return username;
	}
	
	/**	Sets the username.
	 *
	 *	@param	username		The username.
	 */
	 
	public void setUsername (String username) {
		this.username = username;
	}
	
	/**	Gets the encrypted password.
	 *
	 *	@return		The encrypted password.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	public String getPassword () {
		return password;
	}
	
	/**	Sets the password.
	 *
	 *	<p>If the parameter is null or empty, null is stored, otherwise the 
	 *	password is 
	 *	{@link edu.northwestern.at.utils.crypto.Crypto#encryptPassword 
	 *	encrypted} and then stored.
	 *
	 *	@param	password		The password.
	 */
	 
	public void setPassword (String password) {
		if (password == null || password.length() == 0) {
			this.password = null;
		} else {
			this.password = Crypto.encryptPassword(password);
		}
	}

	/**	Validates a password.
	 *
	 *	@param	password	The unencrypted password.
	 *
	 *	@return				True if the password is valid - when encrypted 
	 *						it matches the stored encrypted password. Returns
	 *						false if either the parameter or the stored
	 *						password is null.
	 */

	public boolean passwordIsValid (String password) {
		if (password == null || password.length() == 0 || this.password == null) return false;
		return this.password.equals(Crypto.encryptPassword(password));
	}
	
	/**	Gets the name.
	 *
	 *	@return		The name.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	public String getName () {
		return name;
	}
	
	/**	Sets the name.
	 *
	 *	@param	name		The name.
	 */
	 
	public void setName (String name) {
		this.name = name;
	}
	
	/**	Returns true if the account is an NU account.
	 *
	 *	@return		True if NU account.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	public boolean getNuAccount () {
		return nuAccount;
	}
	
	/**	Sets the NU account attribute.
	 *
	 *	@param	nuAccount		True if NU account.
	 */
	 
	public void setNuAccount (boolean nuAccount) {
		this.nuAccount = nuAccount;
	}
	
	/**	Returns true if the account can manage other accounts.
	 *
	 *	@return		True if can manage other accounts.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	public boolean getCanManageAccounts () {
		return canManageAccounts;
	}
	
	/**	Sets the can manage other accounts attribute.
	 *
	 *	@param	canManageAccounts		True if can manage other accounts.
	 */
	 
	public void setCanManageAccounts (boolean canManageAccounts) {
		this.canManageAccounts = canManageAccounts;
	}
	
	/**	Gets a string representation of the account.
	 *
	 *	@return			The username.
	 */
	 
	public String toString () {
		return username;
	}
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two accounts are equal if their usernames are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Account)) return false;
		Account other = (Account)obj;
		return Compare.equals(username, other.getUsername());
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return username.hashCode();
	}
	
	/**	Clones the account.
	 *
	 *	@return		A clone of the account.
	 */
	 
	public Object clone () {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// can't happen.
			throw new InternalError(e.toString());
		}
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

