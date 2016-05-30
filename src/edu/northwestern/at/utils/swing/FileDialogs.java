package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import edu.northwestern.at.utils.*;

/**	File Dialogs.
 *
 *	<p>
 *	Provides a static encapsulation of the Swing and AWT file dialogs for
 *	"open" and "save as" so that they use the same interface.  Typically
 *	the AWT FileDialog is used on MAC OS X while the Swing JFileChooser
 *	is used on Windows.
 *	</p>
 *
 *	<p>
 *	To select a file for input ("open"):
 *	</p>
 *
 *	<p>
 *	<code>
 *	String[] fileToOpen	= FileDialogs.open( "Open file" );
 *
 *	if ( fileName != null )
 *	{
 *		File f	= new File( fileToOpen[ 0 ] , fileToOpen[ 1 ] );
 *
 *		if ( f.exists() )
 *		{
 *			//	Open the file.
 *				...
 *		}
 *	}
 *	</code>
 *	</p>
 *
 *	<p>
 *	To select a file for output ("save" or "save as"):
 *	</p>
 *
 *	<p>
 *	<code>
 *	String[] fileToSave	= FileDialogs.open( "Save file" );
 *
 *	if ( fileName != null )
 *	{
 *		File f	= new File( fileToOpen[ 0 ] , fileToOpen[ 1 ] );
 *
 *		if ( f.exists() )
 *		{
 *			//	Save the file.
 *				...
 *		}
 *	}
 *	</code>
 *	</p>
 *
 *	<p>
 *	FileDialogs remembers the last used open (input) and save (output)
 *	directories.  You may retrieve these using the
 *	<code>getOpenDirectory</code> and <code>getSaveDirectory</code>
 *	methods, and set them using the <code>setOpenDirectory</code> and
 *	<code>setSaveDirectory</code> methods.
 *	</p>
 *
 *	<p>
 *	You may create file filter dialogs based upon file extension using
 *	{@link FileExtensionFilter} and set the open and save dialogs to
 *	use them using <code>addFileFilter</code>.  For example, to add
 *	a filter to allow only files with the jpg, png, and svg extensions to
 *	appear in the file dialogs, use the following:
 *
 *	<p>
 *	FileExtensionFilter	filter	=
 *		new FileExtensionFilter(
 *			new String[]{ "jpg" , "png", "svg" },
 *			new String[]{ "jpeg files" , "png files" , "svg file" } );
 *
 *	FileDialogs.addFileFilter( filter );
 *	</p>
 *
 *	<p>
 *	File filters persist between class to the open or save methods.
 *	To clear the current list of file filters, use:
 *  </p>
 *
 *	<p>
 *	<code>FileDialogs.clearFileFilters();
 *	</p>
 *
 *	<p>
 *	This will reset the file dialogs to display all files.
 *	</p>
 *
 *	<p>
 *	File filters DO NOT WORK with the AWT dialogs under any MS Windows
 *	operating system.  File filters work fine with the Swing dialogs under
 *	Windows.  If you want to use filters with Windows, make sure you use
 *	the Swing dialogs.  The default under Windows is to use the Swing
 *	dialogs.
 *	</p>
 */

public class FileDialogs
{
	/**	True to use Swing JFileChooser dialogs, false to use AWT.
	 *
	 *	<p>
	 *	By default, use AWT for Mac OS X, and Swing for other systems.
	 *	</p>
	 */

	protected static boolean useSwing			= !Env.MACOSX;

	/**	Last directory used for opening a file. */

	protected static String openDirectory		= null;

	/**	Last directory used for saving a file. */

	protected static String saveDirectory		= null;

	/**	AWT file dialog. */

	protected static FileDialog fileDialog		= null;

	/**	Swing file chooser dialog. */

	protected static JFileChooser fileChooser	= null;

	/**	Default open file dialog title. */

	protected static String defaultOpenTitle	= "Open";

	/**	Default save file dialog title. */

	protected static String defaultSaveTitle	= "Save As";

	/**	Filter allowed all files through. */

	protected static FileExtensionFilter allFilesFilter	=
		new FileExtensionFilter( "" , "All Files" );

	/**	Open file dialog type. */

	protected static final int OPEN				= 0;

	/**	Save file dialog type. */

	protected static final int SAVE				= 1;

	/**	Create AWT file dialog.
	 */

	protected static void createFileDialog()
	{
		if ( fileDialog == null )
		{
			fileDialog	= new FileDialog( new DummyFrame() );
		}
	}

	/**	Create Swing file chooser dialog.
	 */

	protected static void createFileChooser()
	{
		if ( fileChooser == null )
		{
			fileChooser	= new JFileChooser();

			if ( Env.WINDOWSOS )
			{
				Font font	=
					new Font( Fonts.sansSerif , Font.PLAIN , 11 );

				Fonts.recursivelySetFonts( fileChooser , font );
			}
		}
	}

	/**	Display AWT file dialog.
	 *
	 *	@param		title			Title for open file dialog.
	 *	@param		startDirectory	Directory whose contents should be
	 *								displayed in dialog.
	 *	@param		dialogType		Type of dialog.
	 *								OPEN for opening a file.
	 *								SAVE for saving a file.
	 *
	 *	@return						String array with two entries.
	 *								[0]	= file directory
	 *								[1]	= file name
	 *
	 *								Null if no file chosen.
	 */

	protected static String[] doAWTFileDialog
	(
		String title ,
		String startDirectory ,
		int dialogType
	)
	{
		String[] result	= null;

							//	Create file dialog if haven't already
							//	done so.  The initial open directory
							//	is set to the user's home directory.

		createFileDialog();

								//	Set dialog for opening or saving a file.

		if ( dialogType == OPEN )
		{
			fileDialog.setMode( FileDialog.LOAD );
		}
		else
		{
			fileDialog.setMode( FileDialog.SAVE );
		}

								//	If it's not the Mac, set the dialog title.

		if ( !Env.MACOSX ) fileDialog.setTitle( title );

								//	Set the directory to start in.

		fileDialog.setDirectory( startDirectory );

								//	Show the dialog.

		fileDialog.setVisible( true );
		fileDialog.toFront();

								//	Get the selected file name.

		String fileName	= fileDialog.getFile();

								//	If the file name is null, no file
								//	was selected.

		if ( fileName != null )
		{
								//	If a file was selected, get the
								//	directory too.

			startDirectory		= fileDialog.getDirectory();

			result				= new String[ 2 ];

			result[ 0 ]			= startDirectory;
			result[ 1 ]			= fileName;
		}

		return result;
	}

	/**	Display Swing file dialog.
	 *
	 *	@param		parentWindow	Parent window for file dialog.
	 *	@param		title			Title for open file dialog.
	 *	@param		startDirectory	Directory whose contents should be
	 *								displayed in dialog.
	 *	@param		dialogType		Type of dialog.
	 *								OPEN for opening a file.
	 *								SAVE for saving a file.
	 *
	 *	@return						String array with two entries.
	 *								[0]	= file directory
	 *								[1]	= file name
	 *
	 *								Null if no file chosen.
	 */

	protected static String[] doSwingFileDialog
	(
		Window parentWindow ,
		String title ,
		String startDirectory ,
		int dialogType
	)
	{
		String[] result	= null;

								//	Create file chooser if we haven't already
								//	done so.  The initial open directory
								//	is set to the user's home directory.
								//	Also set the default font size smaller
								//	so it matches more closely the
								//	appearance of the native dialog
								//	on Windows.

		createFileChooser();
								//	Set the dialog title.

		fileChooser.setDialogTitle( title );

								//	Set the directory to start in.

		fileChooser.setCurrentDirectory( new File( startDirectory ) );

								//	Show an open or a save dialog,
								//	depending upon the dialog type.
		int	chosen;

		if ( dialogType == OPEN )
		{
			chosen	= fileChooser.showOpenDialog( parentWindow );
		}
		else
		{
			chosen	= fileChooser.showSaveDialog( parentWindow );
		}
								//	Get the selected file name if
								//	one was selected.

		if ( chosen == JFileChooser.APPROVE_OPTION )
		{
			File file	= fileChooser.getSelectedFile();

			if ( file != null )
			{
				startDirectory	=
					fileChooser.getCurrentDirectory().getAbsolutePath();

				result		= new String[ 2 ];

				result[ 0 ]	= startDirectory;
				result[ 1 ]	= file.getName();

								//	If we are doing a save file, and
								//	there is a current file filter
								//	selected, and the file name does
								//	not have an extension specified,
								//	add the extension from the file
								//	filter unless the file name
								//	ends with a "." .
								//
								//	Example:
								//
								//		File filter has extension ".htm" .
								//
								//		Entered name	File name result
								//			aaa				aaa.htm
								//			aaa.			aaa.
								//			aaa.bbb			aaa.bbb

				if ( dialogType == SAVE )
				{
					FileFilter fileFilter	=
						fileChooser.getFileFilter();

					if	(	( fileFilter != null ) &&
							( fileFilter instanceof FilterWrapper ) )
					{
						FilterWrapper filterWrapper	=
							(FilterWrapper)fileFilter;

						if ( filterWrapper.filter instanceof FileExtensionFilter )
						{
							FileExtensionFilter extensionFilter	=
								(FileExtensionFilter)filterWrapper.filter;

			                String extension	=
    	    		        	FileNameUtils.getFileExtension(
        			        		result[ 1 ] , true );

		    	            if ( extension.length() == 0 )
        			        {
        			        	extension	=
                					extensionFilter.getExtensions()[ 0 ];

                				if ( extension.indexOf( '.' ) < 0 )
                				{
                					extension	= "." + extension;
                				}

                				result[ 1 ]	= result[ 1 ] + extension;
                			}
                		}
                	}
                }
			}
		}

		return result;
	}

	/**	Display file dialog.
	 *
	 *	@param		parentWindow	Parent window for dialog.
	 *								Only used for Swing JFileChooser.
	 *	@param		title			Title for open file dialog.
	 *	@param		startDirectory	Directory whose contents should be
	 *								displayed in dialog.
	 *	@param		dialogType		Type of dialog.
	 *								OPEN for opening a file.
	 *								SAVE for saving a file.
	 *
	 *	@return						String array with two entries.
	 *								[0]	= file directory
	 *								[1]	= file name
	 *
	 *								Null if no file chosen.
	 */

	protected static String[] doFileDialog
	(
		Window parentWindow ,
		String title ,
		String startDirectory ,
		int dialogType
	)
	{
								//	Select Swing or AWT file dialog.
		if ( useSwing )
		{
			return doSwingFileDialog(
				parentWindow , title , startDirectory , dialogType );
		}
		else
		{
			return doAWTFileDialog(
				title , startDirectory , dialogType );
		}	}
								//	Select Swing or AWT file dialog.

	/**	Display file dialog.
	 *
	 *	@param		title			Title for open file dialog.
	 *	@param		startDirectory	Directory whose contents should be
	 *								displayed in dialog.
	 *	@param		dialogType		Type of dialog.
	 *								OPEN for opening a file.
	 *								SAVE for saving a file.
	 *
	 *	@return						String array with two entries.
	 *								[0]	= file directory
	 *								[1]	= file name
	 *
	 *								Null if no file chosen.
	 */

	protected static String[] doFileDialog
	(
		String title ,
		String startDirectory ,
		int dialogType
	)
	{
		return doFileDialog( null , title , startDirectory , dialogType );
	}

	/**	Display open file dialog.
	 *
	 *	@param		parentWindow	Parent window for dialog.
	 *	@param		title			Title for open file dialog.
	 *
	 *	@return						String array with two entries.
	 *								[0]	= file directory
	 *								[1]	= file name
	 *
	 *								Null if no file chosen.
	 */

	public static String[] open( Window parentWindow , String title )
	{
		if ( openDirectory	== null )
		{
			openDirectory	= System.getProperty( "user.home" );
		}

    	String[] result	=
    		doFileDialog( parentWindow , title , openDirectory , OPEN );

    	if ( result != null )
    	{
    		openDirectory	= result[ 0 ];
    	}

    	return result;
	}

	/**	Display open file dialog.
	 *
	 *	@param		title	Title for open file dialog.
	 *
	 *	@return				String array with two entries.
	 *						[0]	= file directory
	 *						[1]	= file name
	 *
	 *						Null if no file chosen.
	 */

	public static String[] open( String title )
	{
		return open( null , title );
	}

	/**	Display open file dialog.
	 *
	 *	@param				parentWindow	Parent window for dialog.
	 *
	 *	@return				String array with two entries.
	 *						[0]	= file directory
	 *						[1]	= file name
	 *
	 *						Null if no file chosen.
	 */

	public static String[] open( Window parentWindow )
	{
		return open( parentWindow , defaultOpenTitle );
	}

	/**	Display open file dialog.
	 *
	 *	@return				String array with two entries.
	 *						[0]	= file directory
	 *						[1]	= file name
	 *
	 *						Null if no file chosen.
	 */

	public static String[] open()
	{
		return open( defaultOpenTitle );
	}

	/**	Display save file dialog.
	 *
	 *	@param		parentWindow	Parent window for dialog.
	 *	@param		title	Title for open file dialog.
	 *
	 *	@return		String array with two entries.
	 *				[0]	= file directory
	 *				[1]	= file name
	 *
	 *				Null if no file chosen.
	 */

	public static String[] save( Window parentWindow , String title )
	{
		if ( saveDirectory	== null )
		{
			saveDirectory	= System.getProperty( "user.home" );
		}

    	String[] result	=
    		doFileDialog( parentWindow , title , saveDirectory , SAVE );

    	if ( result != null )
    	{
    		saveDirectory	= result[ 0 ];
    	}

    	return result;
	}

	/**	Display save file dialog.
	 *
	 *	@param		parentWindow	Parent window for dialog.
	 *
	 *	@return		String array with two entries.
	 *				[0]	= file directory
	 *				[1]	= file name
	 *
	 *				Null if no file chosen.
	 */

	public static String[] save( Window parentWindow )
	{
		return save( parentWindow , defaultSaveTitle );
	}

	/**	Display save file dialog.
	 *
	 *	@param		title	Title for open file dialog.
	 *
	 *	@return		String array with two entries.
	 *				[0]	= file directory
	 *				[1]	= file name
	 *
	 *				Null if no file chosen.
	 */

	public static String[] save( String title )
	{
		return save( null , title );
	}

	/**	Display save file dialog.
	 *
	 *	@return				String array with two entries.
	 *						[0]	= file directory
	 *						[1]	= file name
	 *
	 *						Null if no file chosen.
	 */

	public static String[] save()
	{
		return save( defaultSaveTitle );
	}

	/**	Get last open directory.
	 *
	 *	@return		The last directory used for opening a file.
	 *				Empty if no directory ever chosen.
	 */

	public static String getOpenDirectory()
	{
		return openDirectory;
	}

	/**	Set last open directory.
	 *
	 *	@param	directory	The name for the directory to be used to select
	 *						a file for opening in the next file dialog.
	 */

	public static void setOpenDirectory( String directory )
	{
		if ( directory != null ) openDirectory	= directory;
	}

	/**	Get last save directory.
	 *
	 *	@return		The last directory used for saving a file.
	 *				Empty if no directory ever chosen.
	 */

	public static String getSaveDirectory()
	{
		return saveDirectory;
	}

	/**	Set last save directory.
	 *
	 *	@param	directory	The name for the directory to be used to select
	 *						a file for opening in the next file dialog.
	 */

	public static void setSaveDirectory( String directory )
	{
		if ( directory != null ) saveDirectory	= directory;
	}

	/**	Add a file filter.
	 *
	 *	@param	fileExtensionFilter		The file filter.
	 *
	 *	<p>
	 *	Note>:  File filters DO NOT WORK with the AWT dialogs under Windows!
	 *	</p>
	 */

	public static void addFileFilter
	(
		FileExtensionFilter fileExtensionFilter
	)
	{
		if ( fileExtensionFilter != null )
		{
			createFileChooser();
			createFileDialog();

			fileChooser.setFileFilter( wrapFilter( fileExtensionFilter ) );
			fileDialog.setFilenameFilter( fileExtensionFilter );
		}
	}

	/**	Clear any existing file name filters.
	 *
	 *	<p>
	 *	The file dialogs will return to displaying all files.
	 *	</p>
	 */

	public static void clearFileFilters()
	{
								//	Throw away the current dialogs.
								//	New dialogs will automatically be
								//	created the	next time they are needed.
		fileChooser	= null;
		fileDialog	= null;
	}

    /**	Wraps an {@link ExtendedFileNameFilter} in a {@link FileFilter}.
     *
     *	@param	filter		The filter.
     *
     *	@return				The filter wrapped as a FileFilter.
     */

    protected static FileFilter wrapFilter
    (
    	ExtendedFileNameFilter filter
    )
   	{
		if ( filter instanceof FileFilter )
		{
			return (FileFilter)filter;
		}
		else
		{
			return new FilterWrapper( filter );
		}
    }

	/**	Don't allow instantiation but do allow overrides. */

	protected FileDialogs()
	{
	}

	/**	Class to wrap a file filter.
	 */

	protected static class FilterWrapper extends FileFilter
	{
		public ExtendedFileNameFilter filter;

		private FilterWrapper()
		{
		}

		public FilterWrapper( ExtendedFileNameFilter filter )
		{
			this.filter = filter;
		}

		public boolean accept( File file )
		{
			File directory	= file.getParentFile();
			String name		= file.getName();

			return filter.accept( directory , name );
		}

		public String getDescription()
		{
			return filter.getDescription();
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

