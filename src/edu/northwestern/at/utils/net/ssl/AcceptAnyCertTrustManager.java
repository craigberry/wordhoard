package edu.northwestern.at.utils.net.ssl;

/*	Please see the license information at the end of this file. */

import javax.net.ssl.X509TrustManager;
import java.security.cert.*;

/** Implements a relaxed trust manager which accepts any SSL certificate. */

public class AcceptAnyCertTrustManager implements X509TrustManager
{
	/** Create relaxed trust manager.
	 */

	public AcceptAnyCertTrustManager()
	{
	}

	/**	Check if a client certificate is trusted.
	 *
	 *	@param	chain		Peer certificate chain.
	 *	@param	authType	Authentication type based upon the client
	 *						certificate.
	 *
	 *	@throws	IllegalArgumentException
	 *						Not thrown here since we accept anything.
	 *
	 *	@throws	CertificateException
	 *						Not thrown here since we accept anything.
	 */

	public void checkClientTrusted
	(
		X509Certificate chain[] ,
		String authType
	)
		throws CertificateException
	{
		return;
	}

	/**	Check if a server certificate is trusted.
	 *
	 *	@param	chain		Peer certificate chain.
	 *	@param	authType	Authentication type based upon the client
	 *						certificate.
	 *
	 *	@throws	CertificateException
	 *						No thrown here since we accept anything.
	 *
	 *	<p>
	 *	Since we accept any certificate, we just return without checking
	 *	the validity of the certificate in any way.
	 *	</p>
	 */

	public void checkServerTrusted
	(
		X509Certificate chain[] ,
		String authType
	)
		throws CertificateException
	{
								// Certificate is valid, just return.
		return;
	}

	/**	Return valid certificate issuers.
	 *
	 *	@return		An X509 certificate to be accepted.
	 *				Since we accept anything, a default certificate
	 *				is sufficient.
	 */

	public X509Certificate[] getAcceptedIssuers()
	{
		return new X509Certificate[ 0 ];
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

