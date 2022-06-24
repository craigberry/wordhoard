package edu.northwestern.at.utils.swing.printing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.print.*;

import javax.swing.*;
import javax.swing.border.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;

/** Print Preview.
 *
 *	<p>
 *	Creates a list of output page images which can be scrolled through.
 *	Suitable for print previewing documents of moderate size (~100 pages)
 *	as all the preview images are stored in memory.  Each preview image
 *	requires about 90K-100K of memory.  If your JVM has lots of available
 *	memory you can of course preview much larger documents.
 *	</p>
 *
 *	<p>
 *	A toolbar is placed above a scrolling pane which contains the
 *	page preview images.  The toolbar allow for printing the page images,
 *	closing the print preview dialog, choosing the next or previous
 *	page for display, and choosing the scale factor to use when
 *	displaying the page images.  You can also just scroll through the
 *	page images.
 *	</p>
 *
 *	<p>
 *	Based upon sample code written by Matthew Robinson and Pavel Vorobiev.
 *	Uses much less memory and displays preview images much more quickly
 *	than their original code.  Also allows for a specified page format
 *	and provides a repagination dialog.
 *	</p>
 *
 *	<p>
 *	PrintPreview needs to be run from a separate thread so that the
 *	status dialogs will display correctly.  The simplest way to do this
 *	is to use the static printPreview methods in the PrintUtilities class
 *	instead of creating a PrintPreview object directly.
 *  </p>
 */

public class PrintPreview extends ModalDialog
{
	/** Width of a page for printing purposes. */

	protected int printerPageWidth;

	/** Height of a page for printing purposes. */

	protected int printerPageHeight;

	/** The object to be printed. */

	protected Component printableObject;

	/** The page format for printing. */

	protected PageFormat pageFormat;

	/** Holds values for displaying scaled images of individual pages. */

	protected JComboBox scaleComboBox;

	/** Holds list of print preview images for each page to print. */

	protected PreviewContainer previewContainer;

	/**	Holds toolbar of print preview buttons. */

	protected JToolBar toolBar;

	/** Allows scrolling through list of print preview images. */

	protected JScrollPane scroller;

	/** Default page preview scale factor. */

	protected int defaultScale = 75;

	/** Title for printing. */

	protected String title = "";

	/** Create print preview for a printable object.
	 *
	 *	@param	printableObject		The printable object.
	 *								Must implement Printable interface.
	 */

	public PrintPreview( Component printableObject )
	{
		this( printableObject , PrinterSettings.pageFormat , "Print Preview" );
	}

	/** Create print preview for a printable object.
	 *
	 *	@param	printableObject		The printable object.
	 *								Must implement Printable interface.
	 *
	 *	@param	pageFormat			The page format to use when laying
	 *								out the preview.
	 */

	public PrintPreview( Component printableObject , PageFormat pageFormat )
	{
		this( printableObject , pageFormat , "Print Preview" );
	}

	/** Create print preview for a printable object with a specified title.
	 *
	 *	@param	printableObject		The printable object.
	 *								Must implement Printable interface.
	 *
	 *	@param	thePageFormat		The page format to use when laying
	 *								out the preview.
	 *
	 *	@param	title				Title for print preview.
	 */

	public PrintPreview
	(
		Component printableObject ,
		PageFormat thePageFormat ,
		String title
	)
	{
		super( title );

		this.title = title;

		setSize( 600 , 600 );

		this.printableObject = printableObject;

								// Create a printer job with output to
								// the default printer.

		PrinterJob printerJob = PrinterSettings.printerJob;

								// Get the page size for this printer.

		if ( thePageFormat == null )
			pageFormat = PrinterSettings.pageFormat;
		else
			pageFormat = thePageFormat;

		if ( 	( pageFormat.getHeight() == 0 ) ||
				( pageFormat.getWidth() == 0 ) )
		{
			new ErrorMessage
			(
				Resources.get
				(
					"Unabletodeterminedefaultpagesize" ,
					"Unable to determine default page size"
				)
			);

			return;
		}
								// Make component printable if it
								// already isn't.

		if ( this.printableObject instanceof PrintableContents )
		{
			this.printableObject =
				((PrintableContents)this.printableObject).getPrintableComponent(
					title , pageFormat );
		}

		if ( !( this.printableObject instanceof Printable ) )
		{
			this.printableObject =
				new PrintableComponent( printableObject , thePageFormat );
		}
								// Preview is not modal dialog.

		this.setModal( false );

								// Preview is resizeable.

		this.setResizable( true );

								// Quit if null.

		if ( this.printableObject == null ) return;

								// Add buttons for with print, close, next,
								// and previous.  Also add scale combobox
								// for changing the size of print preview
								// images.
								//
								// These are placed in a button bar at the
								// top of the preview pane, instead of the
								// bottom, since most folks expect print
								// preview controls to be at the top.

		toolBar = new JToolBar();

		toolBar.add( createPrintButton() );
		toolBar.add( createCloseButton() );
		toolBar.add( createNextButton() );
		toolBar.add( createPreviousButton() );
		toolBar.addSeparator();
		toolBar.add( createScaleComboBox() );

		add( toolBar );
                                // Create container to hold preview images
                                // for each page.

		previewContainer = new PreviewContainer();

                                // Place contents of print preview
                                // container under control
                                // of the scrolling pane.

		scroller = new JScrollPane( previewContainer,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add( scroller );

		scroller.setPreferredSize( new Dimension( 600 , 600 ) );

								// Inform PrintProgress object about pageFormat.

		if ( this.printableObject instanceof PrintProgress )
		{
			((PrintProgress)this.printableObject).setPageFormat( pageFormat );
		}
                                // Throw away the print preview images when
                                // the print preview is closed.

		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

								//	Create page images and add to
								//	preview display.

 		Thread runCreatePageImages =
 			new Thread( "Print preview" )
 			{
				public void run()
   				{
					getPageImages();
   				}
 			};

 		runCreatePageImages.start();
	}

   	/** Get preview page images.
   	 *
   	 *	<p>
   	 *	Needs to be run in a separate thread so that the
   	 *	status dialogs work properly.
   	 *	</p>
   	 */

   	protected void getPageImages()
   	{
								// Start with default page image scale value.

   		int scale			= defaultScale;

								// Save the printer page size.  We need
								// this to create appropriately size output
								// pages.

   		printerPageWidth	= (int)(pageFormat.getWidth());
   		printerPageHeight	= (int)(pageFormat.getHeight());

   								// Set up the scaling values for the height
   								// and width of the print preview objects
   								// based upon the printer page size
   								// and the selected scale size.

   		int scaledWidth		= (int)( ( printerPageWidth * scale ) / 100 );
   		int scaledHeight	= (int)( ( printerPageHeight * scale ) / 100 );

   								// Counts the number of pages printed.

   		int pageIndex		= 0;

   								// Create a progress dialog.

   		RepaginationProgressDialog progressDialog =
   			new RepaginationProgressDialog();

		progressDialog.setVisible( true );

   								// For each page in the printableObject,
   								// create an image of the page output and
   								// store it in the preview container.
   		try
   		{
   			while ( true )
   			{
   								// Get a buffer big enough
   								// to hold the image for one printer page.

   				BufferedImage pageImage =
   					new BufferedImage(
   						printerPageWidth,
   						printerPageHeight,
   						BufferedImage.TYPE_INT_RGB );

                                   // Get the graphics context for the buffer.

   				Graphics g = pageImage.getGraphics();

   								// Clear the background to white.

   				g.setColor( Color.white );
   				g.fillRect( 0, 0, printerPageWidth, printerPageHeight );

   								// Ask the printable object to "print"
   								// the output image of the next page
   								// to the buffered graphics context.
   								//
   								// Quit if there are no more pages to print.

   				if	( ((Printable)printableObject).print(
   							g , pageFormat, pageIndex ) !=
   								Printable.PAGE_EXISTS )
   					{
   						pageImage = null;
   						break;
   					}
                                   // Add the printed page image
                                   // to the preview container.

   				PagePreview pagePreviewImage =
   					new PagePreview( scaledWidth, scaledHeight, pageImage );

   				previewContainer.add( pagePreviewImage );

                                   // Increment the count of pages printed.
   				pageIndex++;
   									// Update progress dialog.

   				progressDialog.updateProgress( pageIndex );

   									// If cancel button pressed in progress dialog,
   									// throw PrinterAbortException.

				if ( progressDialog.isCancelled() )
				{
					throw new PrinterAbortException(
						"Pagination cancelled, not all pages will be displayed." );
				}
   			}
   		}
   		catch ( PrinterException e )
   		{
   								// $$$PIB$$$ Should probably throw a better
   								// exception.

   			new ErrorMessage
   			(
				Resources.get
   				(
   					"Printpreviewerror" , "Print preview error: "
   				) + e.toString()
   			);
   		}
   		finally
   		{
   								// Close the progress dialog.

   			progressDialog.close();
   		}
								// Display print preview.

		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					setVisible( true );

								// Make sure toolbar doesn't grow vertically.

					Dimension toolbarSize	= toolBar.getSize();

					toolBar.setMaximumSize( toolbarSize );
				}
			}
		);
   	}

	/** Container for array of print preview page images. */

	class PreviewContainer extends JPanel
	{
		/** Horizontal gap between preview images. */

		protected int H_GAP = 16;

		/** Vertical gap between preview images. */

		protected int V_GAP = 10;

		/** Get preferred size for preview container.
		 *
		 *	@return		The preferred size.
		 */

		public Dimension getPreferredSize()
		{
			int n = this.getComponentCount();

			if ( n == 0 )
			{
				return new Dimension( H_GAP , V_GAP );
			}

			Component comp	= this.getComponent( 0 );
			Dimension dc	= comp.getPreferredSize();

			int w = dc.width;
			int h = dc.height;

			Dimension dp = this.getParent().getSize();

			int nCol = Math.max( ( dp.width - H_GAP ) / ( w + H_GAP ) , 1 );
			int nRow = n / nCol;

			if ( ( nRow * nCol ) < n ) nRow++;

			int ww = nCol * ( w + H_GAP ) + H_GAP;
			int hh = nRow * ( h + V_GAP ) + V_GAP;

			Insets ins = this.getInsets();

			return
				new Dimension(
					ww + ins.left + ins.right ,
					hh + ins.top + ins.bottom );
		}

		/** Get maximum size for preview container.
		 *
		 *	@return		The maximum size which is
		 *				always set to the preferred size.
		 */

		public Dimension getMaximumSize()
		{
			return getPreferredSize();
		}

		/** Get minimum size for preview image.
		 *
		 *	@return		The minimum size which is
		 *				always set to the preferred size.
		 */

		public Dimension getMinimumSize()
		{
			return getPreferredSize();
		}

		/** Layout the preview images within the container. */

		public void doLayout()
		{
			Insets ins = this.getInsets();

			int x = ins.left + H_GAP;
			int y = ins.top + V_GAP;

			int n = this.getComponentCount();

			if ( n == 0 ) return;

			Component comp = this.getComponent( 0 );

			Dimension dc = comp.getPreferredSize();

			int w = dc.width;
			int h = dc.height;

			Dimension dp = this.getParent().getSize();

			int nCol = Math.max( ( dp.width - H_GAP ) / ( w + H_GAP ) , 1 );

			int nRow = n / nCol;

			if ( ( nRow * nCol ) < n ) nRow++;

			int index = 0;

			for ( int k = 0; k < nRow; k++ )
			{
				for ( int m = 0; m < nCol; m++ )
				{
					if ( index >= n ) return;

					comp = this.getComponent( index++ );
					comp.setBounds( x, y, w, h );

					x += w + H_GAP;
				}

				y += h + V_GAP;
				x = ins.left + H_GAP;
			}
	    }
	}

	/** Create print button */

	private JButton createPrintButton()
	{
//		JButton button = new JButton( "Print " , new ImageIcon( "print.gif" ) );
		JButton button = new JButton( "Print" );

		ActionListener lst = new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				printPrintable();
			}
		};

		button.addActionListener( lst );
		button.setAlignmentY( 0.5f );
		button.setMargin( new Insets( 2, 6, 2, 6 ) );

		return button;
	}

	/** Print the printable object to the current printer.
	 *
	 *	<p>
	 *	We need to print in a separate thread so that the
	 *	print progress dialog, if any, will work.
	 *	</p>
	 */

	private void printPrintable()
	{
/*
 		Thread runPrintPrintable =
 			new Thread( "Print preview" )
 			{
				public void run()
   				{
					PrintUtilities.printComponent(
						printableObject,
						title,
						null,
						pageFormat,
						true );
   				}
 			};

 		runPrintPrintable.start();
*/
		PrintUtilities.printComponent(
			printableObject,
			title,
			null,
			pageFormat,
			true );
	}

	/** Create close button. */

	private JButton createCloseButton()
	{
		JButton button = new JButton( "Close" );

		ActionListener lst = new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				dispose();
			}
		};

		button.addActionListener( lst );
		button.setAlignmentY( 0.5f );
		button.setMargin( new Insets( 2, 6, 2, 6 ) );

		return button;
	}

	/** Create next button. */

	private JButton createNextButton()
	{
		JButton button = new JButton( "Next" );

		ActionListener lst = new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				JScrollBar scrollBar = scroller.getVerticalScrollBar();

				int curPos	= scrollBar.getValue();

				int nPages	= previewContainer.getComponents().length;

				int inc		=
					( scrollBar.getMaximum() - scrollBar.getMinimum() ) / nPages;

				if ( inc < 1 ) inc = 1;

				if ( ( curPos + inc ) < scrollBar.getMaximum() )
				{
					scrollBar.setValue( curPos + inc );
				}
			}
		};

		button.addActionListener( lst );
		button.setAlignmentY( 0.5f );
		button.setMargin( new Insets( 2, 6, 2, 6 ) );

		return button;
	}

	/** Create previous button. */

	private JButton createPreviousButton()
	{
		JButton button = new JButton( "Previous" );

		ActionListener lst = new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				JScrollBar scrollBar = scroller.getVerticalScrollBar();

				int curPos	= scrollBar.getValue();

				int nPages	= previewContainer.getComponents().length;

				int inc		=
					( scrollBar.getMaximum() - scrollBar.getMinimum() ) / nPages;

				if ( inc < 1 ) inc = 1;

				if ( curPos > 0 )
				{
					scrollBar.setValue( curPos - inc );
				}
			}
		};

		button.addActionListener( lst );
		button.setAlignmentY( 0.5f );
		button.setMargin( new Insets( 2, 6, 2, 6 ) );

		return button;
	}

	/** Create scale combo box.
	 *
	 *	<p>
	 *	The scale combo box contains the value
	 *	by which to scale the full-size
	 *	page preview images for display.
	 *	</p>
	 */

	private JComboBox createScaleComboBox()
	{
		/** Predefined scale values. */

		String[] scaleValues =
			{ "10 %", "25 %", "50 %", "75 %", "100 %" };

								// Populate combo box with
								// predefined values.

		scaleComboBox = new JComboBox( scaleValues );

								// Set default scale value
								// in combo box.

		scaleComboBox.setSelectedItem(
			new Integer( defaultScale ).toString() + " %" );

								// Listen for changes in the
								// selected scaled value.

		ActionListener lst = new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
								// Get new scale value.

				String str = scaleComboBox.getSelectedItem().toString();

                                // Trim any trailing "%" and blanks.

				if ( str.endsWith( "%" ) )
				{
					str = str.substring( 0 , str.length() - 1 );
				}

				str = str.trim();

                                // Try converting scale value to integer.
                                // Use the default scale value if we can't.

				int scale = 0;

				try
				{
					scale = Integer.parseInt( str );
				}
				catch ( NumberFormatException ex )
				{
			    	scale = defaultScale;
				}
                				// Get the scaled printer page sizes
                				// corresponding to the new scale value.

				int scaledWidth		= (int)( printerPageWidth * scale / 100 );
				int scaledHeight	= (int)( printerPageHeight * scale / 100 );

								// Get the list of page preview images.

				Component[] comps = null;

				if ( previewContainer != null )
					comps = previewContainer.getComponents();

								// If no images yet, do nothing.

				if ( comps != null )
				{
								// Otherwise inform each page preview
								// item of the new scaled height and width.

					for ( int iComp = 0; iComp < comps.length; iComp++ )
					{
						if ( !( comps[ iComp ] instanceof PagePreview ) )
							continue;

						PagePreview pagePreview = (PagePreview)comps[ iComp ];

						pagePreview.setScaledSize( scaledWidth , scaledHeight );
					}
                    			// Layout the newly scaled preview images.

					previewContainer.doLayout();

                    			// Let parent windows know we want to be redrawn.
					try
					{
						previewContainer.getParent().validate();
						previewContainer.getParent().getParent().validate();
					}
					catch ( Exception ex2 )
					{
					}
				}
			}
		};
								// Set the maximum size for the combo box
								// to the preferred size.

		scaleComboBox.setMaximumSize( scaleComboBox.getPreferredSize() );

								// The combo box is editable.

		scaleComboBox.setEditable( true );

								// Set the default scale value as the
								// initially selected value.

		scaleComboBox.setSelectedItem(
			new Integer( defaultScale ).toString() + " %" );

        						// Add the listener to the combo box.

		scaleComboBox.addActionListener( lst );

		return scaleComboBox;
	}

	/** Create page preview for one page of output. */

	class PagePreview extends JPanel
	{
		/** Width of the preview image. */

		protected int scaledWidth;

		/** Height of the preview image. */

		protected int scaledHeight;

		/** Original full-size preview image.
		 *
		 *	<p>
		 *	We trade memory for speed.  We could instead ask
		 *	the printable component to rebuild each output page
		 *	into a graphics buffer on the fly, but this tends
		 *	to be very very slow.  Instead we keep the full-size
		 *	page image for each page in memory, stored as a JPEG,
		 *	and scale it on the fly for display.
		 *	</p>
		 *
		 *	<p>
		 *	Storing the image as a jpeg saves enormous amounts
		 *	of memory.  A typical printer page image stored as
		 *	a BufferedImage takes around 2 megabytes of memory,
		 *	while storing the same page as a JPEG typically
		 *	requires only about 90K bytes.
		 *	</p>
		 */

		protected byte[] previewImage;

		/** Create scaled preview image for a single page.
		 *
		 *	@param	scaledWidth		Scaled width for preview image.
		 *	@param	scaledHeight	Scaled height for preview image.
		 *	@param	previewImage	Full size preview image.
		 */

		public PagePreview
		(
			int scaledWidth,
			int scaledHeight,
			BufferedImage previewImage
		)
		{
			this.scaledWidth	= scaledWidth;
			this.scaledHeight	= scaledHeight;

			previewImage.flush();

			this.previewImage = JPEGConverters.toJPEG( previewImage , 0.75f );

								// White background with black border.

			this.setBackground( Color.white );
			this.setBorder( new MatteBorder( 1, 1, 2, 2, Color.black ) );
		}

		/** Resize the scaled preview image.
		 *
		 *	@param	newScaledWidth		New scaled width.
		 *	@param	newScaledHeight		New scaled height.
		 *
		 */

		public void setScaledSize( int newScaledWidth , int newScaledHeight )
		{
								// If the new scale sizes are different
								// from the current ones, save the new sizes
								// repaint the image using the new sizes.

			if (	( newScaledWidth != scaledWidth ) ||
				    ( newScaledHeight != scaledHeight ) )
			{
				scaledWidth		= newScaledWidth;
				scaledHeight	= newScaledHeight;

				this.repaint();
			}
		}

		/** Get preferred size for preview image.
		 *
		 *	@return		The preferred size.
		 */

		public Dimension getPreferredSize()
		{
			Insets ins = this.getInsets();

			return new Dimension(
				scaledWidth + ins.left + ins.right,
				scaledHeight + ins.top + ins.bottom );
		}

		/** Get maximum size for preview image.
		 *
		 *	@return		The maximum size which is
		 *				always set to the preferred size.
		 */

		public Dimension getMaximumSize()
		{
			return getPreferredSize();
		}

		/** Get minimum size for preview image.
		 *
		 *	@return		The minimum size which is
		 *				always set to the preferred size.
		 */

		public Dimension getMinimumSize()
		{
			return getPreferredSize();
		}

		/** Paint preview image.
		 *
		 *	@param		g	Graphics buffer into which to
		 *					paint the scaled preview image.
		 */

		public void paint( Graphics g )
		{
			Graphics2D g2d = (Graphics2D)g;

								// Clear entire preview area to
								// background color.

			g2d.setColor( this.getBackground() );
			g2d.fillRect( 0, 0, this.getWidth(), this.getHeight() );

								// Set the rendering hint for use
								// when drawing the rescaled image.
								// We choose bilinear interpolation.
								// The scaled image is not as nice as
								// using getScaledInstance with
								// SCALE_SMOOTH, but still produces legible
								// print preview images and is
								// is MUCH MUCH faster.
								//
								// Bicubic interpolation works a little
								// bit better but is slower, although still
								// much faster than using SCALE_SMOOTH.

			g2d.setRenderingHint(
				RenderingHints.KEY_INTERPOLATION,
    			RenderingHints.VALUE_INTERPOLATION_BILINEAR );
//    			RenderingHints.VALUE_INTERPOLATION_BICUBIC );

								// Draw the scaled preview image.

			BufferedImage decodedImage	=
				JPEGConverters.fromJPEG( previewImage );

		    g2d.drawImage(
		    	decodedImage,
		    	0, 0, scaledWidth, scaledHeight, null );

								// Paint the border around the image.

			paintBorder( g2d );
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

