package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.io.*;
import javax.swing.*;

/** Custom file filter.
 *
 *	<p>
 *	Creates a file filter with a description for a list of extensions.
 *	</p>
 *
 *	<p>
 *	<strong>Example:</strong> HTML file filter.
 *	</p>
 *
 *	<p>
 *	<code>
 *		CustomFileFilter htmlFileFilter =
 *			new CustomFileFilter( new String[]{ ".html", ".htm" } ,
 *				"HTML Files" );
 *	</code>
 *	</p>
 */

public class CustomFileFilter
	extends javax.swing.filechooser.FileFilter
{
	/** Acceptable extensions accepted by this filter. */

	private String[] extensions = null;

	/** Description of file types accepted by this filter. */

	private String description = "";

	/** Create HTML file filter.
	 *
	 *	@param	extensions 		Accepted HTML file extentions.
	 */

	public CustomFileFilter( String[] extensions , String description )
	{
		this.extensions		= extensions;
		this.description	= description + " (";

		for ( int i = 0; i < extensions.length; i++ )
		{
			if ( i > 0 ) this.description = this.description + ",";

			this.description = this.description + extensions[ i ];
		}

		this.description = this.description + ")";
	}

	/** Determine if file has acceptable file extension.
	 *
	 *	@param	f	The file to check.
	 *
	 *	@return		True if file has acceptable extension.
	 */

	public boolean accept( File f )
	{
								// File is directory -- return true os
								// contents are displayed.

		if ( f.isDirectory() ) return true;

								// Get file's extension.

		String extension	= getExtension( f );

								// See if the extension appears in list
								// of acceptable extensions.

		for ( int i = 0; i < extensions.length; i++ )
		{
			if ( extension.equals( extensions[ i ] ) ) return true;
		}
    							// File did not have acceptable HTML extension.
		return false;
	}

	/** Return description of filter.
	 *
	 *	@return		The description.
	 */

	public String getDescription()
	{
		return description;
	}

	/** Get file extension in lower case.
	 *
	 *	@param	f		The file whose extension is desired.
	 *
	 *	@return			The file extension in lower case.
	 */

	private String getExtension( File f )
	{
		String fileName	= f.getName();
		String result	= "";

		int i = fileName.lastIndexOf( '.' );

		if ( ( i > 0 ) && ( i < fileName.length() - 1 ) )
		{
			result = fileName.substring( i ).toLowerCase();
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

