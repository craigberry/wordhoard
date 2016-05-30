package edu.northwestern.at.utils.net.ldap;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.ldap.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.net.ssl.*;

/** Validates user credentials against an LDAP server.
 */

public class LdapAuthenticator
{
	/** Validate a username/password pair using an LDAP directory.
	 *
	 *	@param	userName		The user name.
	 *	@param	password		The password.
	 *	@param	ldapURL			The LDAP service URL.
	 *	@param	ldapPrincipal	The LDAP principal string.
	 *	@param	keystore		The path to the SSL keystore if a non-standard
	 *							certificate is used.  Set to the empty
	 *							string if not needed.
	 *	@param	acceptAnyCert	True to accept any certificate as valid.
	 *							The keystore is ignored.
	 *
	 *	@return					True if validation successful.
	 */

    public static boolean authenticate
    (
    	String	userName ,
    	String	password ,
		String	ldapURL ,
		String	ldapPrincipal ,
		String	keystore,
		boolean	acceptAnyCert
	)
    {
    	boolean result = false;

    							//	Reject empty username, password,
    							//	ldap server name, or ldap principal
    							//	right away.

		if (	( userName == null ) ||
				( userName.length() == 0 ) ||
				( password == null ) ||
				( password.length() == 0 ) ||
				( ldapURL.length() == 0 ) ||
				( ldapPrincipal.length() == 0 ) ) return result;

								//	Set up the environment for creating
								//	the initial context.

		Hashtable env = new Hashtable();

								//	Are we to accept any certificate
								//	as valid?  If so, create a custom
								//	socket factory which accepts any
								//	certificate.

		if ( acceptAnyCert )
		{
			env.put
			(
				"java.naming.ldap.factory.socket" ,
				"edu.northwestern.at.utils.net.ssl.AcceptAnyCertSSLSocketFactory"
			);
		}
								//	Add any provided custom keystore
								//	so we can use local certs for SSL.

    	else if ( StringUtils.safeString( keystore ).length() > 0 )
    	{
	    	System.setProperty(
    			"javax.net.ssl.trustStore" ,
    			keystore );
    	};
								//	Add the context factory for LDAP.
		env.put(
			Context.INITIAL_CONTEXT_FACTORY,
    		"com.sun.jndi.ldap.LdapCtxFactory" );

								//	Add LDAP server URL.

		env.put( Context.PROVIDER_URL, ldapURL );

								//	Specify SSL.

		env.put( Context.SECURITY_PROTOCOL, "ssl" );

								//	Substitute user name into principal.

		String userPrincipal =
			StringUtils.replaceAll(
				ldapPrincipal, "%1", userName );

		env.put(
			Context.SECURITY_PRINCIPAL, userPrincipal );

								//	Authenticate using given user name
								//	and password.

		env.put( Context.SECURITY_AUTHENTICATION, "simple" );

		env.put( Context.SECURITY_CREDENTIALS , password );

								//	Create the initial context.
								//	If we can, then the username/password
								//	pair is valid.  If we can't, the
								//	username/password pair is invalid.

		DirContext ctx	= null;

		try
		{
			ctx		= new InitialDirContext( env );
			result	= true;
		}
		catch( javax.naming.NamingException e )
		{
								//	Eat the exception so false is returned.
								//	A naming exception indicates authentication
								//	failed.
//			throw e;
//			e.printStackTrace();
		}
		finally
		{
			if ( ctx != null )
			{
				try
				{
					ctx.close();
				}
				catch ( javax.naming.NamingException e )
				{
				}
			}
		}

    	return result;
    }

	/** Validate a username/password pair using an LDAP directory.
	 *
	 *	@param	userName		The user name.
	 *	@param	password		The password.
	 *	@param	ldapURL			The LDAP service URL.
	 *	@param	ldapPrincipal	The LDAP principal string.
	 *
	 *	@return					True if validation successful.
	 *
	 *	<p>
	 *	This method accepts any SSL certificate as valid.  No keystore
	 *	need be provided.
	 *	</p>
	 */

    public static boolean authenticate
    (
    	String userName ,
    	String password ,
		String ldapURL ,
		String ldapPrincipal
	)
    {
    	return authenticate
    	(
    		userName,
    		password,
    		ldapURL,
    		ldapPrincipal,
    		"",
			true
    	);
	}

	/** Hide constructor. */

	private LdapAuthenticator()
	{
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

