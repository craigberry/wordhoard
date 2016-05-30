package edu.northwestern.at.utils.net.http;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;

/** Methods for working with HTTP protocol connections. */

public class HTTPUtils
{
	/**	Load contents at URL into a string.
	 *
	 *	@param	urlString	The URL in string form.
	 *	@param	encoding	The encoding (e.g, ""UTF-8").
	 *						May be empty or null.
	 *	@param	eolString	String with which to replace end of line
	 *						character(s).  May be empty or null.
	 *
	 *	@return				The contents specified by the URL.
	 *						If urlString is empty or null, an empty string
	 *						is returned.
	 */

	public static String getURLContents
	(
		String urlString ,
		String encoding ,
		String eolString
	)
	{
								//	String empty or null?  Return empty string.

		if	(	( urlString == null ) ||
				( urlString.length() == 0 )
			) return "";
								//	Stream to read data from URL.

		InputStream inputStream	= null;

								//	Holds contents of URL.

		StringBuffer contents	= new StringBuffer();

								//	Holds one line of input.

		String line				= "";

								//	Create the URL.
		try
		{
			URL url		= new URL( urlString );

								//	Open input stream from URL.

			inputStream	= url.openStream();

								//	Convert input stream to buffered
								//	data input stream for reading
								//	lines more easily.

			String safeEncoding	= ( encoding == null ) ? "" : encoding;

			BufferedReader bufferedReader;

			if ( safeEncoding.length() > 0 )
			{
				bufferedReader	=
					new BufferedReader(
						new InputStreamReader(
							inputStream , safeEncoding ) );
			}
			else
			{
				bufferedReader	=
					new BufferedReader(
						new InputStreamReader( inputStream ) );
			}
								//	Read each line of URL input and
								//	append to string buffer.  Append
								//	specified EOL string, if any,
								//	to each line.

			while ( ( line = bufferedReader.readLine() ) != null )
			{
				contents.append( line );

				if ( eolString != null ) contents.append( eolString );
			}
		}
								//	Bad URL.

		catch ( MalformedURLException e )
		{
		}
                                //	Connection problem.

		catch ( IOException e )
		{
		}
								//	Close input stream.
		finally
		{
			try
			{
				if ( inputStream != null ) inputStream.close();
			}
			catch ( IOException e )
			{
			}
		}

		return contents.toString();
	}

	/**	Load contents at URL into a string.
	 *
	 *	@param	url			The URL.
	 *	@param	encoding	The encoding (e.g, ""UTF-8").
	 *						May be empty or null.
	 *	@param	eolString	String with which to replace end of line
	 *						character(s).  May be empty or null.
	 *
	 *	@return				The contents specified by the URL.
	 *						If urlString is empty or null, an empty string
	 *						is returned.
	 */

	public static String getURLContents
	(
		URL url ,
		String encoding ,
		String eolString
	)
	{
		return getURLContents
		(
			( url == null ) ? null : url.toString() ,
			encoding ,
			eolString
		);
    }

	/**	Load contents at URL into a string.
	 *
	 *	@param	urlString	The URL in string form.
	 *	@param	eolString	String with which to replace end of line
	 *						character(s).  May be empty or null.
	 *
	 *	@return				The contents specified by the URL.
	 *						If urlString is empty or null, an empty string
	 *						is returned.
	 */

	public static String getURLContents
	(
		String urlString ,
		String eolString
	)
	{
		return getURLContents( urlString , "" , eolString );
	}

	/**	Load contents at URL into a string.
	 *
	 *	@param	url			The URL.
	 *	@param	eolString	String with which to replace end of line
	 *						character(s).  May be empty or null.
	 *
	 *	@return				The contents specified by the URL.
	 *						If urlString is empty or null, an empty string
	 *						is returned.
	 */

	public static String getURLContents
	(
		URL url ,
		String eolString
	)
	{
		return getURLContents( url , "" , eolString );
	}

	/**	Load contents at URL into a string.
	 *
	 *	@param	urlString	The URL in string form.
	 *
	 *	@return				The contents specified by the URL.
	 *						If urlString is empty or null, an empty string
	 *						is returned.
	 */

	public static String getURLContents( String urlString )
	{
		return getURLContents( urlString , "" , "" );
	}

	/**	Load contents at URL into a string.
	 *
	 *	@param	url			The URL.
	 *
	 *	@return				The contents specified by the URL.
	 *						If url is null, an empty string is returned.
	 */

	public static String getURLContents( URL url )
	{
		return getURLContents( url , "" , "" );
	}

	/** Don't allow instantiation, but do allow overrides. */

	protected HTTPUtils()
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

