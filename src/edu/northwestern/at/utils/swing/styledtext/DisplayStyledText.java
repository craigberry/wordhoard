package edu.northwestern.at.utils.swing.styledtext;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;

import javax.swing.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.printing.*;
import edu.northwestern.at.utils.*;

/**	Displays styled text in a dialog box with print preview, print, and
 *	close buttons.
 */

public class DisplayStyledText extends ModalDialog
{
	/** Displays styled text.
	 *
	 *	@param	title		Title.
	 *	@param	textPane	XTextPane containing styled text to display.
	 *	@param	pageFormat	Page format for printing styled text.
	 *	@param	printerJob	Printer job for printing styled text.
	 */

	public DisplayStyledText
	(
		final String title ,
		final XTextPane textPane ,
		final PageFormat pageFormat ,
		final PrinterJob printerJob
	)
	{
		super( title );
								// Not a modal dialog.

		this.setModal( false );

								// Is resizeable.

		this.setResizable( true );

								// Add a print preview button if we're
								// not running under Mac OSX.
		if ( pageFormat != null )
		{
			if ( !Env.MACOSX )
			{
				addButton
				( "Print Preview" ,
					new ActionListener()
					{
						public void actionPerformed( ActionEvent e )
						{
							PrintUtilities.printPreview(
								textPane , pageFormat , title );
						}
					}
				);
			}
								// Add a print button.
			addButton
			( "Print" ,
				 new ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						try
						{
							Thread runner = new Thread( "Styled text print" )
							{
								public void run()
								{
									PrintUtilities.printComponent
									(
										textPane,
										title,
										printerJob,
										pageFormat ,
										true
									);
								}
							};

							runner.start();
						}
						catch ( Exception ee )
						{
							new ErrorMessage( "Can't print." );
						}
					}
				}
			);
		}
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
                                // Set up scroller for text.

		JScrollPane scroller = new JScrollPane( textPane );
		add( scroller );

		Dimension preferredSize = textPane.getPreferredSize();

		preferredSize.width = 550;

		scroller.setPreferredSize( textPane.getPreferredSize() );

		scroller.setMaximumSize( new Dimension( 600 , 450 ) );

								// Throw ourself away when closed.

		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

								// Display styled text.
		setVisible( true );
	}

	/** Displays styled text.
	 *
	 *	@param	title		Title.
	 *	@param	styledText	Styled text to display.
	 *	@param	pageFormat	Page format for printing styled text.
	 */

	public DisplayStyledText
	(
		String title ,
		StyledString styledText ,
		PageFormat pageFormat,
		PrinterJob printerJob
	)
	{
		this( title , new XTextPane( styledText ) , pageFormat , printerJob );
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

