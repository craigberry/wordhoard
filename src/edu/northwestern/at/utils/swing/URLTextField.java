package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.dnd.*;
import java.awt.datatransfer.*;

/**	An XTextField which understands drag/drop of URLs.
 */

public class URLTextField extends XTextField
{
	/** Companion text field containing URL name. */

	protected XTextField titleField;

	/** Drop target. */

	protected URLDropTarget dropTarget;

	/** Drag source. */

	protected URLDragSource dragSource;

	/** Create URL text field.
	 *
	 *	@param	columns		Width of input text field.
	 *
	 *	@param	titleField	Companion JTextField which
	 *						holds title for URL.
	 *
	 *	@param	allowDrag	Allow URL to be dragged from this
	 *						component's text.  NOT RECOMMENDED.
	 */

	public URLTextField
	(
		int columns ,
		XTextField titleField ,
		boolean allowDrag
	)
	{
		super( columns );
								// Save companion URL name field.

		this.titleField = titleField;

								// Set up for drag/drop.

		if ( allowDrag ) dragSource = new URLDragSource( this );
		dropTarget = new URLDropTarget( this );
	}

	/** Return URL text.
	 *
	 *	@return		URL text.
	 */

	public String getURL()
	{
		return getText();
	}

	/** Set URL text.
	 *
	 *	@param		urlString	The URL text.  May contain URL title following
	 *							a "\n" .
	 */

	public void setURL( String urlString )
	{
								// Null URL string?  Do nothing.

		if ( urlString == null ) return;

								// Empty URL string?  Set URL text here
								// to zero.

		if ( urlString.length() == 0 )
		{
			setText( "" );
		}
								// URL string not empty?
		else
		{
			String urlTitle = "";

								// See if there is a line feed in
								// the URL string.  If so, assume the
								// URL is the text preceding the
								// line feed, and the text after
								// the line feed is the URL title.
								// Netscape/Mozilla send URLs encoded
								// this way.

			int eolPos = urlString.indexOf( 10 );

			if ( eolPos >= 0 )
			{
				urlTitle	= urlString.substring( eolPos + 1 );
				urlString	= urlString.substring( 0 , eolPos );
			}
                                // Set the editable text field to the
                                // URL string.

			setText( urlString );

								// If a companion title field was provided,
								// set its value to the URL title if it
								// is currently empty.  We don't want to
								// overwrite a title someone may have
								// previously entered.

			if ( titleField != null )
			{
				if ( titleField.getText().length() == 0 )
				{
					titleField.setText( urlTitle );
				}
			}
		}
	}

	/** Return URL title.
	 *
	 *	@return		URL title.
	 */

	public String getURLTitle()
	{
		if ( titleField != null )
		{
			return titleField.getText();
		}
		else
		{
			return "";
		}
	}

	/**	Drop target for URL.
	 */

	class URLDropTarget extends DropTarget
	{
		/** The URL text field to which to drop the URL. */

		protected URLTextField urlTextField;

		/** Create a URL drop target.
		 *
		 *	@param	urlTextField	URL text field into which to
		 *							drop URL.
		 */

		public URLDropTarget( URLTextField urlTextField )
		{
			new DropTarget( urlTextField, -1, this );

			this.urlTextField = urlTextField;
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
						DataFlavor.stringFlavor ) )
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

								// Plain string URL?

				if ( transferable.isDataFlavorSupported(
						DataFlavor.stringFlavor ) )
				{
								// Accept the drop.

					dtde.acceptDrop( DnDConstants.ACTION_COPY );

								// Get the URL string.

					String URLString =
						(String)transferable.getTransferData(
							DataFlavor.stringFlavor);

								// Set it into the parent text field.

					urlTextField.setURL( URLString );

								// Complete the drop.

					dtde.getDropTargetContext().dropComplete( true );
				}
				else
				{
					dtde.rejectDrop();
				}
			}
			catch ( Exception e )
			{
//				e.printStackTrace();
				dtde.rejectDrop();
			}
		}
	}

	/**	Drag source for URL text. */

	class URLDragSource extends DragSource
		implements DragGestureListener, DragSourceListener
	{
		/** Parent component for which dragging is enabled. */

		protected URLTextField urlTextField;

		/** Create URL drag source.
		 *
		 *	@param	urlTextField	The URLTextField containing
		 *							the URL to drag.
		 */

		public URLDragSource( URLTextField urlTextField )
		{
			super();

			this.urlTextField = urlTextField;

			createDefaultDragGestureRecognizer(
				this.urlTextField, DnDConstants.ACTION_COPY, this );
		}

		/**	Handles a drag gesture recognized event.
		 *
		 *	@param	dge			The event.
	 	 */

		public void dragGestureRecognized( DragGestureEvent dge )
		{
								// Get URL text as a transferable.

			URLTransferable transferable =
				new URLTransferable(
					urlTextField.getURL() );

                    			// Start the drag.
			startDrag(
				dge,
				DragSource.DefaultMoveDrop,
				(Transferable)transferable,
				this );
		}

		/*	Handles a drag drop end event.
		 *
		 *	@param	dsde		The event.
		 */

		public void dragDropEnd( DragSourceDropEvent dsde )
		{
		}

		/*	Handles a drag enter event.
		 *
		 *	@param	dsde		The event.
		 */

		public void dragEnter( DragSourceDragEvent dsde )
		{
		}

		/*	Handles dragOver event.
		 *
		 *	@param	dsde	The DragSourceDrag event.
		 */

		public void dragOver( DragSourceDragEvent dsde )
		{
		}

		/*	Handles a drag exit event.
		 *
		 *	@param	dsde		The event.
		 */

		public void dragExit( DragSourceEvent dsde )
		{
		}

		/*	Handles a drag over event.
		 *
		 *	@param	dsde		The event.
		 */

		public void dragOver( DropTargetDragEvent dsde )
		{
		}

		/*	Handles a drop action changed event.
		 *
		 *	@param	dsde		The event.
		 */

		public void dropActionChanged( DragSourceDragEvent event )
		{
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

