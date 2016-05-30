package edu.northwestern.at.utils.net.url;

/*	Please see the license information at the end of this file. */

import java.net.*;

/**	Utility methods for extracting portions of a URL.
 */

public class ParseURL
{
	/**	Create URL from a string.
	 *
	 *	@param 	urlString	URL string to parse.
	 *
	 *	@return 			The URL, or null if urlString
	 *						contains invalid URL specification.
	 */

	public static URL makeURL( String urlString )
	{
		try
		{
			URL url = new URL( urlString );
			return url;
		}
		catch ( Exception e )
		{
			return null;
		}
	}

	/** Get protocol from URL string.
	 *
	 *	@param urlString	URL string.
	 *
	 *	@return 			Protocol for this URL.
	 */

	public static String getProtocol( String urlString )
	{
		String result = "";

		URL url = makeURL( urlString );

		if ( url != null )
		{
			result = url.getProtocol();
        }

		return result;
	}

	/** Get host from URL string.
	 *
	 *	@param urlString	URL string.
	 *
	 *	@return 			Host for this URL.
	 */

	public static String getHost( String urlString )
	{
		String result = "";

		URL url = makeURL( urlString );

		if ( url != null )
		{
			result = url.getHost();
		}

		return result;
	}

	/** Get direcctory from URL.
	 *
	 *	@param urlString	URL string.
	 *
	 *	@return 			Directory for this URL.
	 */

	public static String getDirectory( String urlString )
	{
		String result = "";

		URL url = makeURL( urlString );

		if ( url != null )
		{
			String path = url.getPath();

			result = path.substring( 0 , path.lastIndexOf( "/" ) );
		}

		return result;
	}

	/** Get file from URL.
	 *
	 *	@param urlString	URL string.
	 *
	 *	@return 			File name for this URL.
	 */

	public static String getFile( String urlString )
	{
		String result = "";

		URL url = makeURL( urlString );

		if ( url != null )
		{
			String path = url.getPath();

			result =
				path.substring( path.lastIndexOf( "/" ) + 1 , path.length() );
        }

		return result;
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

