package edu.northwestern.at.utils.net.ssl;

/*	Please see the license information at the end of this file. */

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.ssl.*;
import javax.net.SocketFactory;

/** Implements a TLS/SSL socket factory which accepts any TLS/SSL certificate
 *	as valid.
 */

public class AcceptAnyCertSSLSocketFactory extends SSLSocketFactory
{
	/** SSL socket factory we use to get sockets. */

	private SSLSocketFactory factory = null;

	/** Create socket factory which will accept any certificate.
	 */

	public AcceptAnyCertSSLSocketFactory()
	{
		try
		{
								// Get an SSL/TLS context.

			SSLContext sslcontext = SSLContext.getInstance( "TLS" );

								// Set certificate trust manager to
								// our class which accepts any SSL
								// certificate.
			sslcontext.init
			(
				null,
				new TrustManager[]
				{
					new AcceptAnyCertTrustManager()
				},
				new java.security.SecureRandom()
			);
								// Create socket factory whose
								// sockets accept any SSL certificate.

			factory = (SSLSocketFactory)sslcontext.getSocketFactory();

		}
		catch( Exception ex )
		{
//			ex.printStackTrace();
		}
	}

	/** The remaining methods are simple overrides of the standard
	 *	socket factory methods.
	 */

	public static SocketFactory getDefault()
	{
		return new AcceptAnyCertSSLSocketFactory();
	}

	/* */

	public Socket createSocket
	(
		Socket socket,
		String s,
		int i, boolean flag
	)
		throws IOException
	{
		return factory.createSocket( socket , s , i , flag );
	}

	/* */

	public Socket createSocket
	(
		InetAddress inaddr,
		int i,
		InetAddress inaddr1,
		int j
	)
		throws IOException
	{
		return factory.createSocket( inaddr , i , inaddr1 , j );
	}

	/* */

	public Socket createSocket
	(
		InetAddress inaddr ,
		int i
	)
		throws IOException
	{
		return factory.createSocket( inaddr , i );
	}

	/* */

	public Socket createSocket
	(
		String s,
		int i,
		InetAddress inaddr,
		int j
	)
		throws IOException
	{
		return factory.createSocket( s, i, inaddr, j );
	}

	/* */

	public Socket createSocket( String s , int i )
		throws IOException
	{
		return factory.createSocket( s , i );
	}

	/* */

	public String[] getDefaultCipherSuites()
	{
		return factory.getSupportedCipherSuites();
	}

	/* */

	public String[] getSupportedCipherSuites()
	{
		return factory.getSupportedCipherSuites();
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

