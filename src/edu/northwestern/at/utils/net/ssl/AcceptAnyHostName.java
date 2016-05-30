package edu.northwestern.at.utils.net.ssl;

/*	Please see the license information at the end of this file. */

import javax.net.ssl.*;

/** Implements a relaxed host name verifier which accepts any
 *	host name in an SSL certificate.
 */

public class AcceptAnyHostName implements HostnameVerifier
{
	/** Create host name validator that accepts any host name.
	 */

	public AcceptAnyHostName()
	{
	}

	/** Verify validity of a host name.
	 *
	 *	@param	hostName	The host name to verify.
	 *	@param	session		The SSL session.
	 *
	 *	@return				Always returns true.
	 */

	public boolean verify
	(
		String hostName ,
		javax.net.ssl.SSLSession session
	)
	{
		return true;
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

