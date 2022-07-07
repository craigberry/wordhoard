package edu.northwestern.at.utils.net.ldap;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.naming.*;
import javax.naming.directory.*;

import edu.northwestern.at.utils.*;

/** Returns LDAP attributes for a search query involving one or more attributes.
 */

public class LdapQueryAttributes
{
	/** Retrieve attributes from an LDAP server.
	 *
	 *	@param	userName				The user name.
	 *	@param	password				The user password.
	 *	@param	ldapURL					The LDAP service URL.
	 *	@param	ldapPrincipal			The LDAP principal string.
	 *	@param	ldapSearchAttributes	The LDAP search attributes.
	 *									May be null.
	 *	@param	keystore				The path to the SSL keystore if a
	 *									non-standard certificate is used.
	 *									Set to the empty string if not needed.
	 *	@param	useSSL					True to use SSL connection.
	 *	@param	acceptAnyCert			True to accept any certificate as valid.
	 *									The keystore is ignored.
	 *
	 *	@return							List of Attributes entries.
	 *									Each entry consists of key/value pairs
	 *									for one matching object in the LDAP
	 *									directory.  May be empty if no
	 *									attributes match or the input is
	 *									invalid.
	 *
	 *	@throws javax.naming.NamingException
	 *							If query fails.  If exception is
	 *							not thrown, query succeeded, but may return
	 *							empty attributes.
	 */

    public static List getAttributes
    (
    	String		userName ,
    	String		password ,
		String		ldapURL ,
		String		ldapPrincipal ,
		Attributes	ldapSearchAttributes ,
		String		keystore ,
		boolean		useSSL ,
		boolean		acceptAnyCert
	)
		throws javax.naming.NamingException
    {
    	ArrayList result	= new ArrayList();

    							//	Reject empty LDAP url or principal.

		if	(	( StringUtils.safeString( ldapURL ).length() == 0 ) ||
				( StringUtils.safeString( ldapPrincipal ).length() == 0 ) )
		{
			return result;
		}
								//	Set up the environment for creating
								//	the initial context.

		Hashtable env = new Hashtable();

								//	Are we using SSL?
		if ( useSSL )
		{
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
	    }
								//	Add the context factory for LDAP.
		env.put(
			Context.INITIAL_CONTEXT_FACTORY,
    		"com.sun.jndi.ldap.LdapCtxFactory" );

								//	Add LDAP server URL.

		env.put( Context.PROVIDER_URL, ldapURL );

								//	Specify SSL as the security protocol
								//	if requested.

		if ( useSSL ) env.put( Context.SECURITY_PROTOCOL, "ssl" );

								//	Substitute user name into principal.

		String userPrincipal =	ldapPrincipal;

		if ( StringUtils.safeString( userName ).length() > 0 )
		{
			userPrincipal	=
				StringUtils.replaceAll( ldapPrincipal, "%1", userName );
		}
								//	If password provided, set it as the
								//	security principal for a simple
								//	authentication.

		if ( StringUtils.safeString( password ).length() > 0 )
		{
			env.put(
				Context.SECURITY_PRINCIPAL , userPrincipal );

								//	Authenticate using given user name
								//	and password.

			env.put( Context.SECURITY_AUTHENTICATION , "simple" );

			env.put( Context.SECURITY_CREDENTIALS , password );
		}
								//	Create the initial LDAP context.
								//	If this succeeds, we can get the
								//	attributes.

		DirContext ctx	= null;

		try
		{
			ctx		= new InitialDirContext( env );

								//	Now get the attributes.
								//
								//	If search attributes are provided,
								//	use those to get matching sets of
								//	attributes.

			if ( ldapSearchAttributes != null )
			{
								//	Search for the specified attributes.

				NamingEnumeration matches	=
					ctx.search( userPrincipal , ldapSearchAttributes );

								//	Each match is of type
								//	javax.naming.directory.SearchResult.
								//	Save the attributes collection for
								//	each match in the result list.

				while ( matches.hasMore() )
				{
					SearchResult searchResult	= (SearchResult)matches.next();

					result.add( searchResult.getAttributes() );
				}
			}
			else
			{
				result.add( ctx.getAttributes( userPrincipal ) );
			}
		}
		catch( javax.naming.NamingException e )
		{
			throw e;
		}
		finally
		{
			if ( ctx != null )
			{
				ctx.close();
			}
		}

    	return result;
    }

	/** Retrieve attributes for an LDAP principal from an LDAP server.
	 *
	 *	@param	userName		The user name.
	 *	@param	ldapURL			The LDAP service URL.
	 *	@param	ldapPrincipal	The LDAP principal string.
	 *
	 *	@return					Attribute collection of key/value pairs
	 *							corresponding to the bind string.  May be
	 *							empty if query returns no results.
	 *
	 *	@throws javax.naming.NamingException
	 *							if authentication fails.  If exception is
	 *							not thrown, authentication succeeded.
	 */

    public static Attributes getAttributes
    (
    	String	userName ,
		String	ldapURL ,
		String	ldapPrincipal
	)
		throws javax.naming.NamingException
    {
		List attributesList	=
			getAttributes
			(
				userName,
				"",
				ldapURL,
				ldapPrincipal,
				null,
				"",
				urlSpecifiesLDAPS( ldapURL ),
				true
			);

		Attributes result	= new BasicAttributes();

		if ( attributesList.size() > 0 )
		{
			result	= (Attributes)attributesList.get( 0 );
		}

		return result;
	}

	/**	Convert LDAP attributes to map.
	 *
	 *	@param	attributes	The LDAP attributes.
	 *
	 *	@return				Map with attribute names as keys and
	 *						attribute values as values.
	 *
	 *	@throws javax.naming.NamingException
	 *						if not thrown, mapping succeeded.
	 */

	public static Map attributesToMap( Attributes attributes )
		throws javax.naming.NamingException
	{
								//	Get line separator character(s) for the
								//	current platform.

		String eolChars	= System.getProperty( "line.separator" );

								//	Create tree map of attribute IDs and
								//	values by enumerating all the attributes.

		Map attributesMap				= new TreeMap();

		NamingEnumeration enumeration	= attributes.getAll();

		while ( enumeration.hasMore() )
		{
								//	Pick up next attribute.

			Attribute attribute	=
				(javax.naming.directory.Attribute)enumeration.next();

								//	Get the attribute value.

			Object attributeValue		= attribute.get();
			Class attributeValueType	= attributeValue.getClass();

								//	If the attribute value is a string,
								//	replace "$" by line separator characters
								//	and "$$" by "$".

			if ( attributeValueType == String.class )
			{
				StringBuffer attributeValueBuffer	=
					new StringBuffer( attributeValue.toString() );

				StringBuffer attributeValueBuffer2	=
					new StringBuffer();

				int valueLength			= attributeValueBuffer.length();
				int valueLengthMinusOne	= valueLength - 1;

				for ( int i = 0; i < valueLength; i++ )
				{
					if ( attributeValueBuffer.charAt( i ) == '$' )
					{
						if (	( i < valueLengthMinusOne ) &&
								( attributeValueBuffer.charAt( i + 1 ) == '$' ) )
						{
							attributeValueBuffer2.append( "$" );
						}
						else
						{
							attributeValueBuffer2.append( eolChars );
						}
					}
					else
					{
						attributeValueBuffer2.append(
							attributeValueBuffer.charAt( i ) );
					}
				}

				attributeValue	= attributeValueBuffer2.toString();
			}
							    //	Store the attribute ID as the hashmap key
							    //	and the attribute value as the hashmap value.

			attributesMap.put( attribute.getID() , attributeValue );
		}

		enumeration.close();

		return attributesMap;
	}

	/** Retrieve attributes for an LDAP principal from an LDAP server as a map.
	 *
	 *	@param	userName		The user name.
	 *	@param	password		The user password.
	 *	@param	ldapURL			The LDAP service URL.
	 *	@param	ldapPrincipal	The LDAP principal string.
	 *	@param	ldapSearchAttributes	The LDAP search attributes.
	 *	@param	keystore		The path to the SSL keystore if a non-standard
	 *							certificate is used.  Set to the empty
	 *							string if not needed.
	 *	@param	useSSL			True to use SSL connection.
	 *	@param	acceptAnyCert	True to accept any certificate as valid.
	 *							The keystore is ignored.
	 *
	 *	@return					Array of Maps, one for each LDAP object's
	 *							attribute key/value pairs.  May be length 0
	 *							if query returns no results.
	 *
	 *	@throws javax.naming.NamingException
	 *							if authentication fails.  If exception is
	 *							not thrown, authentication succeeded.
	 */

    public static Map[] getAttributesMap
    (
    	String		userName ,
    	String		password ,
		String		ldapURL ,
		String		ldapPrincipal ,
		Attributes	ldapSearchAttributes ,
		String		keystore ,
		boolean		useSSL ,
		boolean		acceptAnyCert
	)
		throws javax.naming.NamingException
    {
								//	Get attribute values.

		List attributesList =
			getAttributes
			(
				userName,
				password,
				ldapURL,
				ldapPrincipal,
				ldapSearchAttributes,
				keystore,
				useSSL,
				acceptAnyCert
			);
								//	Convert each attribute's values to a map.

		Map[] result	= new Map[ attributesList.size() ];

		for ( int i = 0 ; i < attributesList.size() ; i++ )
		{
			Attributes attributes	= (Attributes)attributesList.get( i );
			result[ i ]				= attributesToMap( attributes );
		}

		return result;
	}

	/** Retrieve attributes for an LDAP principal from an LDAP server as a map.
	 *
	 *	@param	userName		The user name.
	 *	@param	ldapURL			The LDAP service URL.
	 *	@param	ldapPrincipal	The LDAP principal string.
	 *
	 *	@return					Map of key/value pairs
	 *							corresponding to the bind string.  May be
	 *							empty if query returns no results.
	 *
	 *	@throws javax.naming.NamingException
	 *							if authentication fails.  If exception is
	 *							not thrown, authentication succeeded.
	 */

    public static Map[] getAttributesMap
    (
    	String	userName ,
		String	ldapURL ,
		String	ldapPrincipal
	)
		throws javax.naming.NamingException
    {
		return
			getAttributesMap(
				userName,
				"",
				ldapURL,
				ldapPrincipal,
				null,
				"",
				urlSpecifiesLDAPS( ldapURL ),
				true );
	}

	/** Retrieve attributes from an LDAP server as a map.
	 *
	 *	@param	ldapSearchAttributes		The LDAP search attributes.
	 *	@param	ldapURL						The LDAP service URL.
	 *	@param	ldapPrincipal				The LDAP principal string.
	 *
	 *	@return								Map of key/value pairs
	 *										for the FIRST matching entry
	 *										in the LDAP directory.
	 *
	 *	@throws javax.naming.NamingException
	 *										if LDAP lookup fails.
	 */

    public static Map[] getAttributesMap
    (
    	Attributes	ldapSearchAttributes ,
		String		ldapURL ,
		String		ldapPrincipal
	)
		throws javax.naming.NamingException
    {
		return
			getAttributesMap(
				"",
				"",
				ldapURL,
				ldapPrincipal,
				ldapSearchAttributes,
				"",
				urlSpecifiesLDAPS( ldapURL ),
				true );
	}

	/**	Check if specified URL specifies LDAPS protocol.
	 *
	 *	@param	ldapURL		The LDAP URL.
	 *
	 *	@return				true if URL specifies "ldaps://" protocol.
	 */

	protected static boolean urlSpecifiesLDAPS( String ldapURL )
	{
		boolean result	= false;

		String url	=
			StringUtils.trim( StringUtils.safeString( ldapURL ) ).toLowerCase();

		if ( url.length() > 0 )
		{
        	result	= url.startsWith( "ldaps:" );
		}

		return result;
	}

	/** Hide constructor. */

	private LdapQueryAttributes()
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

