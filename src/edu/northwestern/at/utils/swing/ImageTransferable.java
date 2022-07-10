package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import javax.swing.*;

/**	Transferable for image files.
 *
 *	<p>
 *	Handles the default file types recognized by Java (.gif, .jpeg, .png).
 *	The source and destination fields must be JLabels or subclasses thereof.
 *	</p>
 */

public class ImageTransferable extends TransferHandler
{
	/** Image flavor. */

	private static final DataFlavor flavors[] =
		{ DataFlavor.imageFlavor };

	/** Return COPY action for a component.
	 *
	 *	@param	c	The component.
	 *
	 *	@return		The COPY action.
	 */

	public int getSourceActions( JComponent c )
	{
		return TransferHandler.COPY;
	}

	/** Check if component can import image.
	 *
	 *	@param	component	The component to check.
	 *	@param	flavor		Array of transferable flavors the component accepts.
	 *
	 *	@return				true if component accepts images, false otherwise.
	 */

	public boolean canImport
	(
		JComponent component ,
		DataFlavor flavor[]
	)
	{
		if ( !( component instanceof JLabel ) )
		{
			return false;
		}

		for ( int i = 0 ; i < flavor.length ; i++ )
		{
			for ( int j = 0 ; j < flavors.length ; j++ )
			{
				if ( flavor[ i ].equals( flavors[ j ] ) )
				{
					return true;
				}
			}
		}

		return false;
	}

	/** Create image transferable.
	 *
	 *	@param	component	The component for which to create the transferable.
	 *
	 *	@return				The image transferable, null if the component
	 *						cannot accept images.
	 */

	public Transferable createTransferable( JComponent component )
	{
		if ( component instanceof JLabel )
		{
			JLabel label	= (JLabel)component;
			Icon icon		= label.getIcon();

			if ( icon instanceof ImageIcon )
			{
				final Image image =
					( (ImageIcon)icon ).getImage();

				Transferable transferable =
					new Transferable()
					{
						public Object getTransferData(
							DataFlavor flavor)
						{
							if ( isDataFlavorSupported( flavor ) )
							{
								return image;
							}

							return null;
						}

						public DataFlavor[] getTransferDataFlavors()
						{
							return flavors;
						}

						public boolean isDataFlavorSupported(
							DataFlavor flavor)
						{
							return flavor.equals(
									DataFlavor.imageFlavor );
						}
					};

				return transferable;
			}
		}

		return null;
	}

	/** Import image data from a transferable.
	 *
	 *	@param	component		The component to which to import data.
	 *	@param	transferable	The transferable containing the data to import.
	 *
	 *	@return					true if the data is successfully imported.
	 */

	public boolean importData
	(
		JComponent component ,
		Transferable transferable
	)
	{
		if ( component instanceof JLabel )
		{
			JLabel label = (JLabel)component;

			if ( transferable.isDataFlavorSupported( flavors[ 0 ] ) )
			{
				try
				{
					Image image	=
						(Image)transferable.getTransferData( flavors[ 0 ] );

					ImageIcon icon	= new ImageIcon( image );

					label.setIcon( icon );

					return true;
				}
				catch ( UnsupportedFlavorException ignored )
				{
				}
				catch ( IOException ignored )
				{
				}
			}
		}

		return false;
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

