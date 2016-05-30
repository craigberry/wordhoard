package edu.northwestern.at.utils.net.ldap;

/*	Please see the license information at the end of this file. */

/**	An LDAP error.
 *
 *	<p>This exception is thrown by the methods in the {@link LdapDirectory}
 *	class if any error is encountered trying to connect to or communicate with
 *	the LDAP server.
 *	</p>
 */

public class LdapException extends Exception
{
	/**	Constructs a new LdapException with an error message.
	 *
	 *	@param	msg		The error message.
	 */

	public LdapException( String msg )
	{
		super( msg );
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

