package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.io.File;

/**	File name filter for use with both AWT and Swing file dialogs.
 */

public class FileExtensionFilter implements ExtendedFileNameFilter
{
	/**	The filter description. */

	protected String description;

	/**	The allowed file name extensions. */

    protected String extensions[];

	/**	Create a file name extension filter.
	 *
	 *	@param	extensions		Array of allowed extensions.
	 *	@param	description		Filter description.
	 */

    public FileExtensionFilter
    (
    	String extensions[] ,
    	String description
    )
    {
		setExtensions( extensions );
		setDescription( description );
	}

	/**	Create a file name extension filter.
	 *
	 *	@param	extension		Allowed extension.
	 *	@param	description		Filter description.
	 */

	public FileExtensionFilter
	(
		String extension ,
		String description
	)
	{
		setDescription( description );
		setExtensions( new String[]{ extension } );
	}

	/**	Set filter description.
	 *
	 *	@param	description		The filter description.
	 */

	public void setDescription( String description )
	{
		this.description	= description;
	}

	/**	Set the file name extensions allowed by the filter.
	 *
	 *	@param	extensions	The list of file extensions.
	 */

	public void setExtensions( String extensions[] )
	{
		this.extensions = extensions;
	}

	/**	Gets the array of allowed extensions.
	 *
	 *	@return		The array of extensions.
	 */

	public String[] getExtensions()
	{
		return extensions;
	}

	/**	Filter files by list of extensions.
	 *
	 *	@param	directory	Directory holding file to filter.
	 *	@param	name		The file name to filter.
	 *
	 *	@return				true if file is acceptable.
	 *
	 *	<p>
	 *	Directories are always accepted.
	 *	</p>
	 */

	public boolean accept( File directory , String name )
	{
								//	First check if file name
								//	is a directory.  Always accept it
								//	if so.

		File possibleDirectory	= new File( directory , name );

		if	(	( possibleDirectory != null ) &&
				( possibleDirectory.isDirectory() ) ) return true;

								//	No a directory.  Accept the file only
								//	if its extension is on the list of
								//	extensions allowed by this filter.

		for ( int i = 0 ; i < extensions.length ; i++ )
		{
			if ( name.endsWith( extensions[ i ] ) ) return true;
		}

		return false;
	}

	/**	Get filter description.
	 */

	public String getDescription()
	{
		String ret	= description;
		String sep	= " (*";

		for ( int i = 0 ; i < extensions.length ; i++ )
		{
			ret	= ret + sep + extensions[ i ];
			sep	= ", *";
		}

		if ( sep.equals( ", *" ) ) ret += ")";

		return ret;
	}

	/**	Don't allow default instantiation but do allow overrides.
	 */

	protected FileExtensionFilter()
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

