package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.utils.*;

/**	Swing Look and Feel.
 *
 *	<p>
 *	Swing allows changing the "skin" or "look and feel" of windows on the fly.
 *	The methods here support a variety of built-in and third-party skins.
 *	</p>
 */

public class LookAndFeel
{
	/* Information about a supported look and feel class. */

	public static class ExtendedLookAndFeelInfo
		implements Comparable
	{
		/*	The look and feel name. */

		public String name;

		/*	The name of the class implementing the look and feel. */

		public String className;

		/*	The name of an optional theme for this class. */

		public String themeName;

		/*	True if the theme name is a file in the lookAndFeelDirectory. */

		public boolean isThemeFile;

		/**	Create look and feel entry.
		 *
		 *	@param	name		Look and feel name.
		 *	@param	className	Implementing class name for look and feel.
		 *	@param	themeName	The name of a theme.
		 *	@param	isThemeFile	true of the theme name is a file
		 *						in the lookAndFeelDirectory.
		 */

		public ExtendedLookAndFeelInfo
		(
			String name,
			String className,
			String themeName,
			boolean isThemeFile
		)
		{
			this.name			= name;
			this.className		= className;
			this.themeName		= themeName;
			this.isThemeFile	= isThemeFile;
		}

		/**	Create look and feel entry.
		 *
		 *	@param	name		Look and feel name.
		 *	@param	className	Implementing class name for look and feel.
		 */

		public ExtendedLookAndFeelInfo
		(
			String name ,
			String className
		)
		{
			this.name			= name;
			this.className		= className;
			this.themeName		= "";
			this.isThemeFile	= false;
		}

		/**	Create look and feel entry.
		 *
		 *	@param	info	Look and feel information object.
		 */

		public ExtendedLookAndFeelInfo
		(
			UIManager.LookAndFeelInfo info
		)
		{
			this.name			= info.getName();
			this.className		= info.getClassName();
			this.themeName		= "";
			this.isThemeFile	= false;
		}

		/**	Return name of look and feel.
		 *
		 *	@return		Name of look and feel.
		 */

		public String toString()
		{
			return name;
		}

		/**	Compare another object to this one.
		 *
		 *	@param	obj		The other object.
		 *
		 *	@return			Value as required by Comparable interface.
		 *
		 *	<p>
		 *	We just compare the names ignoring case.
		 *	</p>
		 */

		public int compareTo( Object obj )
		{
			if	(	( obj == null ) ||
					!(obj instanceof ExtendedLookAndFeelInfo ) )
			{
				return Integer.MIN_VALUE;
			}

			return Compare.compareIgnoreCase(
				this.name , ((ExtendedLookAndFeelInfo)obj).name );
		}

		/**	Return true if another object is the same as this one.
		 *
		 *	@param	obj		The other object.
		 *
		 *	@return			true if the other is the same as this one.
		 */

		public boolean equals( Object obj )
		{
			if	(	( obj == null ) ||
					!( obj instanceof ExtendedLookAndFeelInfo ) )
			{
				return false;
			}

			ExtendedLookAndFeelInfo other	= (ExtendedLookAndFeelInfo)obj;

			return
				Compare.equalsIgnoreCase( this.name , other.name ) &&
				Compare.equals( this.className , other.className ) &&
				Compare.equals( this.themeName , other.themeName ) &&
				( this.isThemeFile == other.isThemeFile );
		}

		/**	Returns a hash code for the object.
 		 *
 		 *	@return		The hash code.
 		 */

		public int hashCode()
		{
			return this.name.hashCode();
		}
	}

	/* List of supported look and feel names.
	 *
     *	<p>
     *	WordHoard by default provides no additional look and feel types
	 *	over and above what comes with Java, which usually includes at
	 *	least Metal and Nimbus.
     *	</p>
	 *	<p>
	 * 	To provide an additional look and feel option, add the relevant
	 *	library to the top-leve lib/ directory, and add an element to the
	 *	lookAndFeels array below in the following format:
	 *
	 *	new ExtendedLookAndFeelInfo(
	 *		"MyLookAndFeel Name",
	 *		"class.path.to.MyLookAndFeel",
	 *		"",
	 *		false )
	 *	</p>
     */

    public static ExtendedLookAndFeelInfo[] lookAndFeels = {};

	/**	Get the class of the currently active look and feel.
	 *
	 *	@return		The class of the currently active look and feel.
	 */

	public static Class getActiveLookAndFeelClass()
	{
		return UIManager.getLookAndFeel().getClass();
	}

	/**	Get the class name of the currently active look and feel.
	 *
	 *	@return		The class name of the currently active look and feel.
	 */

	public static String getActiveLookAndFeel()
	{
		return UIManager.getLookAndFeel().getClass().getName();
	}

	/** Get list of available look and feels.
	 *
	 *	<p>
	 *	The complete list of possible look and feels is pruned to those for
	 *	which the implementing class actually exists.
	 *	</p>
	 *	@return	List of available look and feel options.
	 */

	public static ExtendedLookAndFeelInfo[] getExtendedLookAndFeelInfo()
	{
								//	Holds list of looks and feels we
								//	find.

		SortedArrayList looks = new SortedArrayList();

								//	First pick up the looks and feels
								//	which Java thinks are installed.

		UIManager.LookAndFeelInfo[] installedLookAndFeels	=
			UIManager.getInstalledLookAndFeels();

		for ( int i = 0 ; i < installedLookAndFeels.length; i++ )
		{
			String lafName	=
				installedLookAndFeels[ i ].getName().toLowerCase();

			boolean addLaf	= true;

			if ( !Env.WINDOWSOS )
			{
				addLaf	= ( lafName.indexOf( "windows" ) < 0 );
			}

			if ( addLaf )
			{
				looks.add
				(
					new ExtendedLookAndFeelInfo( installedLookAndFeels[ i ] )
				);
			}
		}
								//	Add other look and feels we know about.

		for ( int i = 0; i < lookAndFeels.length; i++ )
		{
			try
			{
									// Check that the implementing class
									// actually exists.

				java.lang.Class c =
					Class.forName( lookAndFeels[ i ].className );

									//	Ignore any that are already
									//	installed.

				if ( looks.contains( c ) ) continue;

									//	Ignore Windows look and feels
									//	under non-Windows systems.

				if ( !Env.WINDOWSOS )
				{
					if ( lookAndFeels[ i ].name.toLowerCase().indexOf(
						"windows" ) >= 0 )
					{
						continue;
					}
				}
				looks.add( lookAndFeels[ i ] );
			}
			catch ( ClassNotFoundException e )
			{
			}
		}
                                    // Return pruned list of look and
                                    // feel names.

		int numLooks = looks.size();

		ExtendedLookAndFeelInfo[] result = new ExtendedLookAndFeelInfo[ numLooks ];

		for ( int i = 0; i < numLooks; i++ )
		{
			result[ i ] = (ExtendedLookAndFeelInfo)looks.get( i );
		}

		return result;
	}

	/**	Check if Metal look and feel is the current look and feel.
	 *
	 *	@return		true if Metal is the current look and feel.
	 */

	public static boolean isMetal()
	{
		boolean	result	= false;

		try
		{
			result	=
				( getActiveLookAndFeelClass() ==
				javax.swing.plaf.metal.MetalLookAndFeel.class );
		}
		catch ( Exception e )
		{
		}

		return result;
	}

    /**	Sets a new Swing look and feel.
     *
     *	@param	lookAndFeelName		The name of the new look and feel.
     *								This is converted to appropriate
     *								class name to load the selected
     *								look and feel.  If the selected
     *								look and feel is not available,
     *								the default system look and feel
     *								is used instead.
     *
     *	@return						true if selected look and feel set.
     *
     */

	public static boolean setLookAndFeel( String lookAndFeelName )
	{
		boolean result	= false;

		for ( int i = 0 ; i < lookAndFeels.length ; i++ )
		{
			if ( lookAndFeelName.equals( lookAndFeels[ i ].name ) )
			{
				try
				{
									// Set new look and feel.

					setLookAndFeelByClassName( lookAndFeels[ i ].className );

                                    // Propagate it to all existing windows.

					updateLookAndFeel();

					result	= true;
				}
				finally
				{
					break;
				}
			}
		}

		return result;
	}

    /**	Sets Swing look and feel to native look and feel.
     */

	public static void setNativeLookAndFeel()
	{
		setLookAndFeelByClassName
		(
			UIManager.getSystemLookAndFeelClassName() ,
			true
		);
	}

    /**	Sets Swing look and feel to a "nice" look and feel.
     *
     *	<p>
     *	We start by checking if the current look and feel is the
     *	native look and feel.  If not, we leave it alone and do nothing
     *	here.
     *	</p>
     *
     *	<p>
	 *	For Windows,the native look and feel is used but with
     *	font smoothing added.
     *	</p>
     *
     *	<p>
     *	For macOS, the standard native look and feel is always used.
     *	</p>
     *
     *	<p>
     *	For other systems, we get the currently defined look and feel
     *	and see if it is "Metal."  If so, we substitute "Nimbus," but
	 *	otherwise leave the default.
     *	</p>
     */

	public static void setNiceLookAndFeel()
	{
								//	For windows, enable global font
								//	smoothing but with the system
								//	look and feel.
		if ( Env.WINDOWSOS )
		{
			Fonts.enableSystemFontSettings( true );

			XParameters.tableFont	= null;
			XParameters.treeFont	= null;
			XParameters.listFont	= null;

			setLookAndFeelByClassName
			(
				UIManager.getSystemLookAndFeelClassName() ,
				true
			);

		}
								//	Do nothing for Mac OS X.

		else if ( Env.MACOSX )
		{
		}
		else
		{
								//	For other systems, use the
								//	Nimbus look and feel when the
								//	Metal look and feel is in place.
			if ( isMetal() )
			{
				setLookAndFeel( "Nimbus" );
			}
 		}
	}

    /**	Checks if current Look and Feel is system default.
	 * @return	True if look and feel is native, false if not.
     */

	public static boolean isNativeLookAndFeel()
	{
		javax.swing.LookAndFeel currentLookAndFeel =
			UIManager.getLookAndFeel();

		return ( currentLookAndFeel == null ) ?
			false : currentLookAndFeel.isNativeLookAndFeel();
	}

    /**	Sets a new Swing look and feel by class name.
     *
     *	@param	lookAndFeelClassName 	The class name of the new look
     *									and feel.
	 *
     *	@param	updateWindows 			True to update all existing windows
     *									to new look and feel.
     *
     *	@return							True if selected look and feel set.
     */

	public static boolean setLookAndFeelByClassName
	(
		String lookAndFeelClassName ,
		boolean updateWindows
	)
	{
		boolean result	= false;

		UIManager.getLookAndFeelDefaults().put
		(
			"ClassLoader" ,
			LookAndFeel.class.getClassLoader()
		);

		try
		{
			UIManager.setLookAndFeel( lookAndFeelClassName );

			result	= true;

			if ( updateWindows ) updateLookAndFeel();
		}
		catch ( UnsupportedLookAndFeelException ignored )
		{
		}
		catch ( ClassNotFoundException ignored )
		{
		}
		catch ( InstantiationException ignored )
		{
		}
		catch ( IllegalAccessException ignored )
		{
		}

		return result;
	}

    /**	Sets a new Swing look and feel by class name.
     *
     *	@param	lookAndFeelClassName 	Class name of new look and feel.
     *
     *	@return							True if selected look and feel set.
     */

	public static boolean setLookAndFeelByClassName
	(
		String lookAndFeelClassName
	)
	{
		return setLookAndFeelByClassName( lookAndFeelClassName , false );
	}

    /**	Updates look and feel of all existing windows to new look and feel.
     */

	public static void updateLookAndFeel()
	{
		Frame frames[] = Frame.getFrames();

		for ( int i = 0; i < frames.length; i++ )
		{
			updateLookAndFeelOfChildWindows( frames[ i ] );
		}
	}

    /**	Updates look and feel of child windows to new current look and feel.
     *
     *	@param	window 		The parent window whose children should be updated
     *						to the new current look and feel.
     */

	public static void updateLookAndFeelOfChildWindows( Window window )
	{
		SwingUtilities.updateComponentTreeUI( window );

		Window children[] = window.getOwnedWindows();

		for ( int i = 0; i < children.length; i++ )
		{
			updateLookAndFeelOfChildWindows( children[ i ] );
		}
	}

	/**	Changes the current font size for all user interface objects.
	 *
	 *	@param	fontDelta	The number of points by which to increase
	 *						or decrease all font sizes for user interface
	 *						objects.
	 */

	public static void changefontSizes( int fontDelta )
	{
								//	Get all look and feel settings.

  		UIDefaults ui	= UIManager.getLookAndFeelDefaults();

		ArrayList al	= new ArrayList( ui.keySet() );

		float delta		= (float)fontDelta;

		for ( int i = 0 ; i < al.size() ; i++ )
		{
			String key	= al.get( i ).toString();

			if ( key.endsWith( ".font" ) )
			{
				Font font	= ui.getFont( key );

				if ( font != null )
				{
					ui.put
					(
						key ,
						font.deriveFont( font.getSize2D() + delta )
					);
				}
			}
		}

		updateLookAndFeel();
	}

	/**	Enable antialiased font rendering in Java 5 or later. */

	public static void enableAntialiasedFontRendering()
	{
		// set antialiased font rendering.

		String antialiasing	= "swing.aatext";

		if ( System.getProperty( antialiasing ) == null )
		{
			System.setProperty( antialiasing , "true" );
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

