package edu.northwestern.at.utils.net.mime;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.*;

/**	SafeFileExtensions.
 *
 *	<p>
 *	A safe file extension is one which can presumably be opened
 *	with minimal danger of viral infection, etc.  This static class
 *	allows querying and modification of a list of safe file
 *	extensions.
 *	</p>
 */

public class SafeFileExtensions
{
	/** Hashmap holding the safe file extensions. */

	private static HashMap safeFileExtensionsMap = new HashMap();

	/** Add extension to safe list.
	 *
	 *	@param	extension		The extension.
	 */

	public static void addExtension( String extension )
	{
		String key = StringUtils.safeString( extension ).toLowerCase();

		if ( key.length() > 0 )
		{
			safeFileExtensionsMap.put( key , key );
		}
	}

	/** Add multiple extensions to safe list.
	 *
	 *	@param	extensions		The extensions.
	 */

	public static void addExtensions( String[] extensions )
	{
		if ( extensions != null )
		{
			for ( int i = 0; i < extensions.length; i++ )
			{
				addExtension( extensions[ i ] );
			}
		}
	}

	/** Remove extension from safe list.
	 *
	 *	@param	extension		The extension
	 */

	public static void removeExtension( String extension )
	{
		String key = StringUtils.safeString( extension ).toLowerCase();

		if ( safeFileExtensionsMap.containsKey( key ) )
		{
			safeFileExtensionsMap.remove( key );
		}
	}

	/** Is extension safe?
	 *
	 *	@return		True if extension on safe list, false otherwise.
	 */

	public static boolean isExtensionSafe( String extension )
	{
		String key = StringUtils.safeString( extension ).toLowerCase();

		return safeFileExtensionsMap.containsKey( key );
	}

	/** Hide no-args constructor. */

	private SafeFileExtensions()
	{
	}

	/** Static initializer defines the default set of safe extensions. */

	static
	{
		addExtensions(
			MimeTypeMapper.getMatchingExtensions( "audio/" ) );

		addExtensions(
			MimeTypeMapper.getMatchingExtensions( "image/" ) );

		addExtensions(
			MimeTypeMapper.getMatchingExtensions( "text/" ) );

		addExtensions(
			MimeTypeMapper.getMatchingExtensions( "video/" ) );

		addExtension( ".stxt" );
		addExtension( ".rm" );
		addExtension( ".doc" );
		addExtension( ".pdf" );
		addExtension( ".ppt" );
		addExtension( ".xls" );
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

