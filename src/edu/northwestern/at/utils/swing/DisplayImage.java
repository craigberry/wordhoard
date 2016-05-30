package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.awt.print.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/** Displays a titled image in a dialog box with a close button.
 *
 *	<p>
 *	Primarily for debugging.
 *	</p>
 */

public class DisplayImage extends ModalDialog
{
	/** Displays an image.
	 *
	 *	@param	title		Image title.
	 *	@param	image		Image to display.
	 */

	public DisplayImage( String title , Image image )
	{
		super( title );

		setSize( 600 , 450 );
								// Not a modal dialog.

		this.setModal( false );

								// Is resizeable.

		this.setResizable( true );

								// Add a close button.
		addButton
		( "Close" ,
			 new ActionListener()
			{
				public void actionPerformed( ActionEvent e )
				{
					dispose();
				}
			}
		);
								// Create panel to hold image.

		JPanel imagePanel = new JPanel();

		image.flush();
								// Make local copy of original image.

		BufferedImage copyImage =
			new BufferedImage(
				image.getWidth( null ),
				image.getHeight( null ),
				BufferedImage.TYPE_INT_RGB );

		Graphics g = copyImage.getGraphics();

		g.drawImage
		(
			image,
			0,
			0,
			image.getWidth( null ),
			image.getHeight( null ),
			null
		);
								// Wrap copied image in a label.
		JLabel imageLabel =
			new JLabel(
				new ImageIcon( copyImage ) );

								// Add label with image to panel.

		imagePanel.add( imageLabel );

                                // Set up scroller for image.

		JScrollPane scroller = new JScrollPane( imagePanel );
		add( scroller );

		scroller.setPreferredSize( new Dimension( 600 , 450 ) );

                                // Throw away image when closed.

		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

								// Display image.
		setVisible( true );
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

