package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.tree.*;

import edu.northwestern.at.utils.*;

/**	Saves data stored in a JTree to a file is several different formats.
 */

public class SaveTreeData
{
	/**	TXT extension file filter. */

	protected static FileExtensionFilter txtFilter	=
		new FileExtensionFilter( ".txt" , "Text Files" );

	/**	Convert contents of a JTree to text file.
	 *
	 *	@param	tree					The tree containing the data to
	 *									convert to command separated values.
	 *
	 *	@param	title					Title for tree.
	 *
	 *	@return							The tab separated tree data.
	 *									Each tree row appears on a new line.
	 */

	public static String treeDataToText
	(
		JTree tree ,
		String title
	)
	{
		String result	= "";

		if ( tree != null )
		{
			StringBuffer sb	= new StringBuffer();

			for ( int i = 0 ; i < tree.getRowCount() ; i++ )
			{
				TreePath treePath		= tree.getPathForRow( i );

				Object[] pathElements	= treePath.getPath();

				for ( int j = 0 ; j < ( pathElements.length - 1 ) ; j++ )
				{
					sb.append( "\t" );
				}

				sb.append( pathElements[ pathElements.length - 1 ].toString() );

				sb.append( Env.LINE_SEPARATOR );
			}

			result	= sb.toString();
		}

		return result;
	}

	/**	Saves the tree data to a file.
	 *
	 *	@param	tree				Tree holding data to save.
	 *	@param	title				The title for the data.  Null if no title.
	 *	@param	fileName			The file name to which to save the data.
	 */

	public static void saveTreeDataToFile
	(
		JTree tree ,
		String title ,
		String fileName
	)
	{
		try
		{
			FileUtils.writeTextFile
			(
				fileName ,
				false ,
				treeDataToText( tree , title )
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Saves the tree model data to a file.  Prompts for a file name.
	 *
	 *	@param	parentWindow			Parent window for file dialog.
	 *	@param	tree					The tree whose data is to be saved.
	 *	@param	title					The title for the tree data.
	 */

	public static void saveTreeDataToFile
	(
		Window parentWindow ,
		JTree tree ,
		String title
	)
	{
		FileDialogs.addFileFilter( txtFilter );

		String[] saveFile	= FileDialogs.save( parentWindow );

		FileDialogs.clearFileFilters();

		if ( saveFile != null )
		{
			File file	= new File( saveFile[ 0 ] , saveFile[ 1 ] );

			saveTreeDataToFile
			(
				tree ,
				title ,
				file.getAbsolutePath()
			);
		}
	}

	/** Don't allow instantiation but do allow overrides. */

	protected SaveTreeData()
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

