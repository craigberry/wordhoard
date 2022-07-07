package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;

import javax.swing.*;

import edu.northwestern.at.utils.swing.printing.*;

/**	An extended version of a JFrame.
 *
 *	<p>
 *	An XFrame knows how to print itself or its child windows,
 *	and adds a few support routines for windows positioning,
 *	termination, and about box display; and can keep track
 *	of sibling frames.
 *	</p>
 */

public class XFrame extends WindowsMenuManager
	implements PrintableContainer
{
	/**	The About box. */

	protected JFrame aboutBox;

	/**	The preferences dialog. */

	protected JFrame prefsWindow;

	/**	The default image icon.  Null to use Java default. */

	private static Image defaultIconImage	= null;

	/**	Create untitled XFrame. */

	public XFrame()
	{
		super( "" );
		common();
	}

	/**	Create titled XFrame.
	 *
	 *	@param	title	The frame's title.
	 */

	public XFrame( String title )
	{
		super( title );
		common();
	}

	/**	Perform common initialization tasks for all constructors.
	 */

	protected void common()
	{
								//	No about box or prefs dialog by default.

		aboutBox	= null;
		prefsWindow	= null;
								//	Set default icon image if provided.

		if ( defaultIconImage != null )
		{
			setIconImage( defaultIconImage );
		};
	}

	/** Perform page setup.   Override this if necessary in subclasses. */

	public void doPageSetup()
	{
		PrinterSettings.doPageSetup();
	}

	/** Perform print preview.   Override this if necessary in subclasses. */

	public void doPrintPreview()
	{
		try
		{
			JRootPane rootPane = getRootPane();

			Component component = SwingUtils.getFocusedComponent( rootPane );

			if ( component != null )
			{
				doPrintPreview( component , "Print Preview" );
			}
		}
		catch ( Exception e )
		{
		}
	}

	/** Perform print preview.   Override this if necessary in subclasses.
	 * @param	component	Component to print.
	*/

	public void doPrintPreview( Component component )
	{
		try
		{
			if ( component != null )
			{
				PrintUtilities.printPreview(
					component ,
					PrinterSettings.pageFormat ,
					"Print Preview" );
			}
		}
		catch ( Exception e )
		{
		}
	}

	/** Perform print preview.   Override this if necessary in subclasses.
	 * @param	component	Component to print.
	 * @param	title	Title to print.
	*/

	public void doPrintPreview( Component component , String title )
	{
		try
		{
			if ( component != null )
			{
				PrintUtilities.printPreview(
					component ,
					PrinterSettings.pageFormat ,
					title );
			}
		}
		catch ( Exception e )
		{
		}
	}

	/** Do printing.  Override this if necessary in subclasses. */

	public void doPrint()
	{
		try
		{
			JRootPane rootPane = getRootPane();

			Component component = SwingUtils.getFocusedComponent( rootPane );

			if ( component != null )
			{
				doPrint( component , "" );
			}
		}
		catch ( Exception e )
		{
		}
	}

	/** Do printing.  Override this if necessary in subclasses.
	 * @param	component	Component to print.
	*/

	public void doPrint( Component component )
	{
		doPrint( component , "" );
	}

	/** Do printing.  Override this if necessary in subclasses.
	 * @param	component	Component to print.
	 * @param	title	Title to print.
	*/

	public void doPrint( Component component , String title )
	{
		try
		{
			if ( component != null )
			{
				final Component componentToPrint	= component;
				final String	titleToPrint	    = title;

				Thread runner = new Thread("Printing")
				{
					public void run()
					{
						PrintUtilities.printComponent(
							componentToPrint,
							titleToPrint,
							PrinterSettings.printerJob,
							PrinterSettings.pageFormat ,
							true
						);
					}
				};

				runner.start();
			}
		}
		catch ( Exception e )
		{
		}
	}

	/**	Packs an XFrame and staggers it over its parent frame.
	 *
	 *	@param	parent			The parent window.
	 *
	 */

	public void pack( Window parent )
	{
		super.pack();

		if ( parent == null )
		{
			WindowPositioning.centerWindowOverWindow( this , null , 0 );
		}
		else
		{
			WindowPositioning.staggerWindowOverWindow( this , parent , 25 );
		}
	}

	/**	Packs an XFrame with no parent.
	 */

	public void pack()
	{
		pack( null );
	}

	/**	Display about box, if any.
	 */

	public void about()
	{
		if ( aboutBox != null )
		{
			WindowPositioning.centerWindowOverWindow(
				aboutBox , this , 25 );

			aboutBox.setVisible( true );
			aboutBox.toFront();
		}
	}

	/**	Display preferences dialog, if any.
	 */

	public void prefs()
	{
		if ( prefsWindow != null )
		{
			WindowPositioning.centerWindowOverWindow(
				prefsWindow , this , 25 );

			prefsWindow.setVisible( true );
			prefsWindow.toFront();
		}
	}

	/**	Quit program.
	 *
	 *	<p>
	 *	Does a System.exit( 0 ).  You may want to override this to ask
	 *	for confirmation before quitting.
	 *	</p>
	 */

	public void quit()
	{
		System.exit( 0 );
	}

	/**	Set the about box.
	 *
	 *	@param	aboutBox	The about box window.
	 */

	public void setAboutBox( JFrame aboutBox )
	{
		this.aboutBox	= aboutBox;
	}

	/**	Set the preferences dialog.
	 *
	 *	@param	prefsWindow		The preferences window.
	 */

	public void setPrefsWindow( JFrame prefsWindow )
	{
		this.prefsWindow	= prefsWindow;
	}

	/**	Set default image.
	 *
	 *	@param	image	The image to use as a window icon.
	 */

	public static void setDefaultImage( Image image )
	{
		defaultIconImage = image;
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

