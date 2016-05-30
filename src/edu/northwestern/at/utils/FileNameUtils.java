package edu.northwestern.at.utils;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.text.*;

import edu.northwestern.at.utils.net.mime.*;

/**	File name utilities.
 *
 *	<p>
 *	This static class provides various utility methods for manipulating
 *	file and directory names.
 *	</p>
 */

public class FileNameUtils
{
	/** Static mime type mapper. */

	public static MimeTypeMapper mimeTypeMapper = new MimeTypeMapper();

	/**	Strips path from a file name.
	 *
	 *	@param	fileName	File name with possible path.
	 *
	 *	@return				File name stripped of path.
	 */

	public static String stripPathName( String fileName )
	{
		 return new File( fileName ).getName();
	}

	/** Get file extension from file name.
	 *
	 *	@param	fileName	The file name whose extension is wanted.
	 *	@param	keepPeriod	Keep the period in the extension.
	 *
	 *	@return				The extension, if any, with the optional
	 *						leading period.
	 */

	public static String getFileExtension
	(
		String fileName ,
		boolean keepPeriod
	)
	{
								// Strip path from file name.

		String name = stripPathName( StringUtils.safeString( fileName ) );

								// Find trailing period.
								// This marks start of extension.
		String extension;

		int periodPos = name.lastIndexOf( '.' );

		if ( periodPos != -1 )
		{
								// If period found,
								// we have an extension.
								// Keep or delete the leading
								// period as requested.
			if( keepPeriod )
				extension = name.substring( periodPos );
			else
				extension = name.substring( periodPos + 1 );
		}
								// No extension.
		else
		{
			extension = "";
		}

		return extension;
	}

	/** Get MIME type for a filename.
	 *
	 *	@param	fileName	Name of file for which mime type is desired.
	 *
	 *	@return				The mime type, e.g., "text/plain".
	 *
	 *	<p>
	 *	When the file name's extension is not found in the mime types
	 *	hash table, a mime type of "application/octet-stream" is returned.
	 *	</p>
	 */

	public static String getContentTypeFor( String fileName )
	{
		return mimeTypeMapper.getContentTypeFor( fileName );
	}

	/** Checks if a file exists.
	 *
	 *	@param	fileName	The file name to check for existence.
	 *
	 *	@return				True if file exists.
	 */

    public static boolean fileExists( String fileName )
    {
		return ( new File( fileName ) ).exists();
    }

	/** Hide default no-args constructor. */

	private FileNameUtils()
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

