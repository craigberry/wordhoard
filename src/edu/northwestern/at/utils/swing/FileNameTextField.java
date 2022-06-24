package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.io.*;

/**	An XTextField which understands dragging of file names from the
 *	system file browser.
 */

public class FileNameTextField extends XTextField
{
	/** Drop target. */

	protected FileNameDropTarget dropTarget;

	/** Create file name text field.
	 *
	 *	@param	columns		Width of input text field.
	 */

	public FileNameTextField( int columns )
	{
		super( columns );
								// Set up for drop.

		dropTarget = new FileNameDropTarget( this );
	}

	/** Return file name.
	 *
	 *	@return		File name.
	 */

	public String getFileName()
	{
		return getText();
	}

	/** Set file name.
	 *
	 *	@param	fileName	Thef ile name.
	 */

	public void setFileName( String fileName )
	{
								// Null file name?  Do nothing.

		if ( fileName == null ) return;

								// Empty file name string?  Set text here
								// to empty string.

		if ( fileName.length() == 0 )
		{
			setText( "" );
		}
								// File name string not empty?
                                // Set the editable text field to the
                                // file name.
		else
		{

			setText( fileName );
		}
	}

	/**	Drop target for file name.
	 */

	class FileNameDropTarget extends DropTarget
	{
		/** The text field to which to drop the file name. */

		protected FileNameTextField fileNameTextField;

		/** Create a file name drop target.
		 *
		 *	@param	fileNameTextField	Text field into which to
		 *								drop file name.
		 */

		public FileNameDropTarget( FileNameTextField fileNameTextField )
		{
			new DropTarget( fileNameTextField, -1, this );

			this.fileNameTextField = fileNameTextField;
		}

		/**	Handles a drag over event.
		 *
		 *	@param	dtde		The event.
		 */

		public void dragOver( DropTargetDragEvent dtde )
		{
								// Did we get a file list?  Reject drop
								// now if not.

			if	(	!dtde.isDataFlavorSupported(
						DataFlavor.javaFileListFlavor ) )
			{
				dtde.rejectDrag();
			}
		}

		/**	Handles drop events.
		 *
	 	 *	@param	dtde		The dtde.
		 */

		public void drop( DropTargetDropEvent dtde )
		{
			try
			{
								// Get dragged data.

				Transferable transferable = dtde.getTransferable();

				DataFlavor[] flavors = transferable.getTransferDataFlavors();

								// Did we get a file list?

				if ( transferable.isDataFlavorSupported(
						DataFlavor.javaFileListFlavor ) )
				{
								// Accept the drop.

					dtde.acceptDrop( DnDConstants.ACTION_COPY );

								// Get the file name.  We use only
								// the first file name in the list.

					java.util.List fileList =
						(java.util.List)transferable.getTransferData(
							DataFlavor.javaFileListFlavor );

					Iterator iterator = fileList.iterator();

								// Count files.  If more than one,
								// report error.

					int fileCount = 0;

					File file = null;

					while ( iterator.hasNext() )
					{
						fileCount++;

                             	// Remember the first file.

						if ( fileCount == 1 )
						{
							file = (File)iterator.next();
						}
						else
						{
							iterator.next();
						}
					}
						        // Error if more than one file.

					if ( fileCount > 1 )
					{
						throw new Exception(
							"You may only drag one file at a time." );
					}
								// Only one file.

					if ( fileCount == 1 )
					{
								// Disallow directory.

						if ( file.isDirectory() )
						{
							throw new Exception(
								"You may not drag a directory." );
						}

								// Set file name into the text field.

						fileNameTextField.setFileName( file.getAbsolutePath() );

								// Complete the drop.

						dtde.getDropTargetContext().dropComplete( true );
					}
					else
					{
						throw new Exception(
							"Not a valid file reference." );
					}
				}
				else
				{
					dtde.rejectDrop();
				}
			}
			catch ( Exception e )
			{
				try
				{
					dtde.rejectDrop();
				}
				catch ( Exception e2 )
				{
								// We don't care if reject drop fails here.
				}

				final String errorMessage = e.getMessage();

				SwingUtilities.invokeLater
				(
					new Runnable()
					{
						public void run()
						{
							new ErrorMessage(
								errorMessage ,
								SwingUtils.getParentWindow(
									fileNameTextField ) );
						}
					}
				);
			}
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

