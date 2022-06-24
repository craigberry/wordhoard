package edu.northwestern.at.utils.net.ldap;

/*	Please see the license information at the end of this file. */

import java.util.*;

/**	The LDAP directory.
 *
 *	<p>This static class provides utility methods to communicate with the NU
 *	LDAP directory servers.
 *	</p>
 */

public class LdapDirectory
{
	/** Validate a username/password pair using an LDAP directory.
	 *
	 *	@param	ldapURL			The LDAP service URL.
	 *	@param	ldapPrincipal	The LDAP principal string.
	 *	@param	netid			The netid.
	 *	@param	password		The password.
	 *	@return					True if validation successful.
	 */

	public static boolean authenticateNetid
	(
		String ldapURL,
		String ldapPrincipal,
		String netid,
		String password
	)
    {
   		return
  			LdapAuthenticator.authenticate(
   			   	netid , password , ldapURL , ldapPrincipal );
    }

	/**	Gets the LDAP directory information for a netid.
	 *
	 *	@param	ldapURL			The LDAP service URL.
	 *	@param	ldapPrincipal	The LDAP principal string.
	 *	@param	netid			The netid.
	 *
	 *	@return					A mapping from LDAP field names
	 *							to LDAP field values.
	 *
	 *	@throws	LdapException
	 */

	public static Map getInfoForNetid
	(
		String ldapURL,
		String ldapPrincipal,
		String netid
	)
		throws LdapException
	{
		try
		{
			return LdapQueryAttributes.getAttributesMap(
				netid, ldapURL, ldapPrincipal )[ 0 ];
		}
		catch ( javax.naming.NamingException e )
		{
			throw new LdapException( e.getMessage() );
		}
	}

	/** Don't allow instantiation but do allow overrides. */

	protected LdapDirectory()
	{
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

