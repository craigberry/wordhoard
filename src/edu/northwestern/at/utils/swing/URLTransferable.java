package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import java.io.IOException;
import java.io.File;

import java.util.Vector;
import javax.swing.tree.*;

/** Implements a transferable class for URLs. */

public class URLTransferable implements Transferable
{
	/** The URL in string format. */

	protected String URLString;

	/** String data flavor index. */

	private final static int STRING_URL	= 0;

	/** The data flavors supported by this transferable. */

	private static DataFlavor[] flavors =
	{
		DataFlavor.stringFlavor
	};

	/** Create URL transferable.
	 *
	 *	@param	URLString	The URL as a string.
	 */

	public URLTransferable( String URLString )
	{
		this.URLString	= URLString;
	}

	/* Return flavors in which URL can be provided.
	 *
	 *	@return		Array of types.
	 */

	public synchronized DataFlavor[] getTransferDataFlavors()
	{
		DataFlavor[] copyFlavors = new DataFlavor[ flavors.length ];

		for ( int i = 0; i < flavors.length; i++ )
		{
			copyFlavors[ i ] = flavors[ i ];
		}

		return copyFlavors;
	}

	/* Is flavor supported by this transferable?
	 *
	 *	@param	flavor 	DataFlavor to check.
	 *
	 *	@return			True if data flavor supported.
	 */

	public boolean isDataFlavorSupported( DataFlavor flavor )
	{
		for ( int i = 0; i < flavors.length; i++ )
		{
			if ( flavors[ i ].equals( flavor ) )
			{
				return true;
			}
		}

		return false;
	}

	/** Return URL for bookmark in string format.
	 *
	 *	@param	flavor		Data flavor.
	 *
	 *	@return				URL in string format followed by "\n"
	 *						followed by URL title.
	 */

	public synchronized Object getTransferData( DataFlavor flavor )
		throws UnsupportedFlavorException, IOException
	{
		if ( flavor.equals( flavors[ STRING_URL ] ) )
		{
			return URLString;
		}
		else
		{
			throw new UnsupportedFlavorException( flavor );
		}
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

