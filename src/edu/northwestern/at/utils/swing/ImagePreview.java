package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.beans.*;

/** Image previewer accessory for file chooser dialogs.
 *
 *	<p>
 *	Adds an image preview to a file chooser dialog.
 *	Slightly modified from sample code in Sun's Java Tutorial.
 *	</p>
 */

public class ImagePreview extends JComponent
	implements PropertyChangeListener
{
	/** Holds thumbnail image of selected file. */

	ImageIcon thumbnail = null;

	/** The selected file. */

	File file = null;

	/** Height and width in pixels of thumbnail preview area. */

	static final int previewAreaWidth = 100;
	static final int previewAreaHeight = 50;

	/** Images more than this number of pixels wide will be scaled down. */

	static final int previewAreaThreshhold = 90;

	/** Create an image preview accessory for a file chooser dialog.
	 *
	 *	@param	fileChooser		The file chooser to which to add the
	 *							image preview.
	 */

	public ImagePreview( JFileChooser fileChooser )
	{
								// Thumbnail preview region is 100 x 50 pixels.

		setPreferredSize( new Dimension( previewAreaWidth , previewAreaHeight ) );

								// Listen for changes to file chooser dialog.
								// We do this so we can change the thumbnail
								// when the selected file changes.

		fileChooser.addPropertyChangeListener( this );
	}

	/** Create a thumbnail for the currently selected image. */

	public void loadImage()
	{
								// Just return if no file selected.

		if ( file == null ) return;

                                // Try loading the image file.

		ImageIcon tmpIcon = new ImageIcon( file.getPath() );

                                // If the image size exceeds the alloted thumbnail
                                // size, scale the image down to the thumbnail size.
                                // If the image size is already smaller than the
                                // thumbnail size, just display it as is.

		if ( tmpIcon.getIconWidth() > previewAreaThreshhold )
		{
			thumbnail =
				new ImageIcon(
					tmpIcon.getImage().getScaledInstance(
						previewAreaThreshhold, -1, Image.SCALE_DEFAULT ) );
		}
		else
		{
			thumbnail = tmpIcon;
		}
	}

	/** Change thumbnail image when new file selected.
	 *
	 *	@param e	The change event.
	 */

	public void propertyChange( PropertyChangeEvent e )
	{
								// Get the change event.

		String prop = e.getPropertyName();

                                // If it's a file selection change,
                                // get the name of the newly selected file.

		if ( prop.equals( JFileChooser.SELECTED_FILE_CHANGED_PROPERTY ) )
		{
			file = (File)e.getNewValue();

								// If the image preview area is visible,
								// create and display a thumbnail of the
								// newly selected file.

			if ( this.isShowing() )
			{
				loadImage();
				this.repaint();
			}
		}
	}

	/** Paint the image preview.
	 *
	 *	@param	g	The graphics context for the image preview.
	 */

	public void paintComponent( Graphics g )
	{
								// Load thumbnail if we haven't already.

		if ( thumbnail == null ) loadImage();

								// Draw the thumbnail image centered in the
								// image preview area of the file chooser dialog.

		if ( thumbnail != null )
		{
			int x = ( this.getWidth() / 2 ) - ( thumbnail.getIconWidth() / 2 );
			int y = ( this.getHeight() / 2 ) - ( thumbnail.getIconHeight() / 2 );

			if ( y < 0 ) y = 0;
			if ( x < 5 ) x = 5;

			thumbnail.paintIcon( this, g, x, y );
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

