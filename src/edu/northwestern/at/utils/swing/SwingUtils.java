package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.styledtext.*;

/**	Miscellaneous Swing utilities.
 *
 *	<p>
 *	This static class provides various Swing utility methods.
 *	</p>
 */

public class SwingUtils
{
	/**	The AWT event thread.  Set in first call to getAWTEventThread below.
	 */

	public static Thread awtEventThread	= null;

	/**	Builds either a JLabel or a scrolling text area containing a string.
	 *
	 *	<p>If the string 72 characters or less and does not contain a newline,
	 *	it is wrapped in a JLabel and the JLabel is returned. Otherwise, it is
	 *	wrapped in a non-editable text area with 6 rows, 72 columns, and a
	 *	scroll bar and the JScrollPane is returned.
	 *
	 *	@param	str		The string.
	 *	@return	The text area as a JComponent.
	 */

	public static JComponent buildLabelOrScrollingTextArea( String str )
	{
		if ( str == null )
		{
			return new JLabel();
		}
		else if ( ( str.length() <= 72 ) && ( str.indexOf( '\n' ) < 0 ) )
		{
			return new JLabel( str );
		}
		else
		{
			XTextArea textArea	= new XTextArea( str , 6 , 72 );

			textArea.setEditable( false );

			return textArea.getScrollPane();
		}
	}

	/**	Builds a scrolling styled text area containing a string.
	 *
	 *	@param	str		The styled string.
	 *	@return	The text area as a JComponent.
	 */

	public static JComponent buildScrollingStyledTextArea( StyledString str )
	{
		XTextPane textPane		= new XTextPane(str);

		XScrollPane scrollPane	=
			new XScrollPane
			(
				textPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
			);

		textPane.setMargin( new Insets( 5 , 5 , 5 , 5 ) );
		textPane.setEditable( false );
		textPane.setCaretPosition( 0 );

		return scrollPane;
	}

	/**	Close a frame.
	 *
	 *	@param	frame	The frame (or JFrame) to close.
	 */

	public static void closeFrame( final Frame frame )
	{
		if ( frame != null )
		{
			Runnable runCloseFrame	=
				new Runnable()
				{
					public void run()
					{
						frame.setVisible( false );
						frame.dispose();
//						frame	= null;
   					}
				};

			SwingUtilities.invokeLater( runCloseFrame );
		}
	}

	/** Quits application when main JFrame is closed. */

	public static class ExitListener extends WindowAdapter
	{
		public void windowClosing( WindowEvent event )
		{
		    System.exit( 0 );
		}
	}

	/**	Flush the system event queue.
	 */

	public static void flushAWTEventQueue()
	{
		SwingRunner.runNow
		(
			new Runnable()
			{
				public void run()
				{
				}
			}
		);
	}

	/**	Get the AWT (and Swing) event thread.
	 *
	 *	@return		The AWT event thread, or null if not available.
	 */

	public static Thread getAWTEventThread()
	{
		if ( awtEventThread == null )
		{
			try
			{
				SwingRunner.runNow
				(
					new Runnable()
					{
						public void run()
						{
							awtEventThread	= Thread.currentThread();
						}
					}
				);
			}
			catch ( Exception e )
			{
			}
		}

		return awtEventThread;
	}

	/** Get currently focused component given parent container.
	 *
	 *	@param	parent	Parent container.
	 *
	 *	@return			The child component of parent with focus,
	 *					or null if none has the focus.
	 */

	public static Component getFocusedComponent( Container parent )
	{
		if ( parent == null ) return null;

		if ( parent.hasFocus() )
		{
			return parent;
		}

		Component [] comps = parent.getComponents();

		for ( int i = 0; i < comps.length; i++ )
		{
			if ( comps[ i ] instanceof Container )
			{
				Component c = getFocusedComponent( (Container)( comps[ i ] ) );
				if ( c != null ) return c;
			}

			if ( comps[ i ].hasFocus() )
			{
				return comps[ i ];
			}
		}

		return null;
	}

	/** Get parent window for Swing component.
	 *
	 *	@param	c	The component.
	 *	@return		Parent window, or null if none.
	 */

	public static Window getParentWindow( JComponent c )
	{
		Window result = null;

		if ( c != null )
		{
			JRootPane rootPane = c.getRootPane();

			if ( rootPane != null )
			{
				Frame frame	= (Frame)rootPane.getParent();
				result		= (Window)frame;
			}
		}

		return result;
	}

	/** Opens a container (JPanel, etc.) in a Swing frame.
	 *
	 *	@param	frame				The frame to hold the content.
	 *								If null, a new XFrame is allocated.
	 *	@param	content				Content to place in JFrame.
	 *	@param	menuBar				Menu bar for JFrame.
	 *	@param	width				Width of JFrame.
	 *	@param	height				Height of JFrame.
	 *	@param	title				Title for JFrame.
	 *	@param	backgroundColor		Background color for JFrame and content.
	 *	@param	exitListener		Listener for frame exit.
	 *
	 *	@return						The frame as an Object.
	 */

//	public static XFrame openInSwingFrame
	public static Object openInSwingFrame
	(
		JFrame frame ,
		Container content ,
		JMenuBar menuBar ,
		int width ,
		int height ,
		String title ,
		Color backgroundColor ,
		WindowAdapter exitListener
	)
	{
								// Allocate frame if not specified.
		if ( frame == null )
		{
			frame = new XFrame();
		}
								// Set frame title.

		frame.setTitle( title );

								// Set background color for frame and content.

		frame.setBackground( backgroundColor );

		if ( content != null ) content.setBackground( backgroundColor );

								// Set frame size.

		frame.setSize( width , height );

								// Add menu bar.

		if ( menuBar != null ) frame.setJMenuBar( menuBar );

								// Put content in JFrame.

		if ( content != null ) frame.setContentPane( content );

								// Add exit listener if specified.

		if ( exitListener != null )
			frame.addWindowListener( exitListener );

								// Center the frame on the screen.

		WindowPositioning.centerWindowOverWindow( frame , null , 0 );

								// Make frame visible.

		frame.setVisible( true );

		return frame;
	}

	/** Opens a container (JPanel, etc.) in a JFrame.
	 *
	 *	@param	content				Content to place in JFrame.
	 *	@param	menuBar				Menu bar for JFrame.
	 *	@param	width				Width of JFrame.
	 *	@param	height				Height of JFrame.
	 *	@param	title				Title for JFrame.
	 *	@param	backgroundColor		Background color for JFrame and content.
	 *	@param	exitListener		Listener for frame exit.
	 *	@return	The JFrame.
	 */

	public static JFrame openInJFrame
	(
		Container content,
		JMenuBar menuBar,
		int width,
		int height,
		String title,
		Color backgroundColor,
		WindowAdapter exitListener
	)
	{
		JFrame frame	= new JFrame();

		return (JFrame)openInSwingFrame
		(
			frame ,
			content ,
			menuBar ,
			width ,
			height ,
			title ,
			backgroundColor ,
			exitListener
		);
	}

	/** Opens a container (JPanel, etc.) in an XFrame.
	 *
	 *	@param	content				Content to place in JFrame.
	 *	@param	menuBar				Menu bar for JFrame.
	 *	@param	width				Width of JFrame.
	 *	@param	height				Height of JFrame.
	 *	@param	title				Title for JFrame.
	 *	@param	backgroundColor		Background color for JFrame and content.
	 *	@param	exitListener		Listener for frame exit.
	 *
	 *	@return						The frame.
	 */

	public static XFrame openInXFrame
	(
		Container content,
		JMenuBar menuBar,
		int width,
		int height,
		String title,
		Color backgroundColor,
		WindowAdapter exitListener
	)
	{
		return (XFrame)openInSwingFrame
		(
			new XFrame() ,
			content ,
			menuBar ,
			width ,
			height ,
			title ,
			backgroundColor ,
			exitListener
		);
	}

	protected class ExtendedEventQueue extends EventQueue
	{
		public void dispatchEvent( AWTEvent event )
		{
			super.dispatchEvent( event );
		}
	}

	/**	Can't instantiate but can override. */

	protected SwingUtils()
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

