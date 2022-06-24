package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import java.awt.print.*;

import edu.northwestern.at.utils.swing.printing.*;
import edu.northwestern.at.utils.*;

/**	A JTextArea with different defaults and behavior.
 *
 *	<ul>
 *	<li>Word style line wrapping is set by default.
 *	<li>The text area is given a two pixel border on all four sides.
 *	<li>A convenience method attaches a vertical scroll bar.
 *	<li>Tabbing is disabled - tab advances to the next focus component.
 *	<li>The caret position is reset to 0 whenever new text is set.
 *	</ul>
 *
 *	<p>The constructors are the same as in JTextArea. We did not bother
 *	giving them their own javadoc.
 */

public class XTextArea extends JTextArea
	implements	PrintableContents, ClipboardHasPasteableData, CutCopyPaste,
				SaveToFile, SelectAll
{
	/**	Scroll pane, or null if no scroll bar attached. */

	private JScrollPane scrollPane;

	/**	True if tabbing enabled. */

	private boolean tabsEnabled = false;

	/**	Default forward focus traversable keys. */

	private Set defaultForwardFocusTraversalKeys;

	/**	Default backward focus traversable keys. */

	private Set defaultBackwardFocusTraversalKeys;

	public XTextArea () {
		super();
		common();
	}

	public XTextArea (Document doc) {
		super(doc);
		common();
	}

	public XTextArea (Document doc, String text,
		int rows, int columns)
	{
		super(doc, text, rows, columns);
		common();
	}

	public XTextArea (int rows, int columns) {
		super(rows, columns);
		common();
	}

	public XTextArea (String text) {
		super(text);
		common();
	}

	public XTextArea (String text, int rows, int columns) {
		super(text, rows, columns);
		common();
	}

	/**	Sets default attributes: word style line wrap and a 2 pixel
		border.
	 */

	private void common () {
		JTextField textField = new JTextField();
		setFont( textField.getFont() );
		setLineWrap(true);
		setWrapStyleWord(true);
		setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		addMouseListener( new TextFieldPopupMenuMouseListener() );
		defaultBackwardFocusTraversalKeys =
			getFocusTraversalKeys(
				KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);
		defaultForwardFocusTraversalKeys =
			getFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
	}

	/**	Attaches a scroll bar to the text area and returns the scroll pane.
	 *
	 *	<p>The scroll bar policy is set to vertical scroll bar always and
	 *	horizontal scroll bar never.
	 *
	 *	@return		The scroll pane.
	 */

	public JScrollPane getScrollPane () {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(this);
			scrollPane.setVerticalScrollBarPolicy(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setHorizontalScrollBarPolicy(
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}
		return scrollPane;
	}

	/**	Enables or disables tabs.
	 *
	 *	<p>When tabs are enabled they are entered as regular characters
	 *	into the text in the field. When they are disabled a tab advances
	 *	to the next focus component. Tabs are disabled by default.
	 *
	 *	@param	tabsEnabled		True to enable tabs.
	 */

	public void setTabsEnabled (boolean tabsEnabled) {
		this.tabsEnabled = tabsEnabled;
		if (tabsEnabled) {
		    setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
			  null);
	    	setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
			  null);
		} else {
		    setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
			  defaultForwardFocusTraversalKeys);
	    	setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
			  defaultBackwardFocusTraversalKeys);
		}
	}

	/**	Sets the area text and resets the caret position to 0.
	 *
	 *	@param	t		The new text for the area.
	 */

	public void setText (String t) {
		super.setText(t);
		setCaretPosition(0);
	}

	/** Prints the XTextArea contents.
	 *
	 *	@param	title			Title for output.
	 *	@param	printerJob		The printer job.
	 *	@param	pageFormat		The printer page format.
	 */

	public void printContents
	(
		final String title,
		final PrinterJob printerJob,
		final PageFormat pageFormat
	)
	{
		Thread runner = new Thread("Print text area")
		{
			public void run()
			{
				PrintUtilities.printComponent(
					getPrintableComponent( title , pageFormat ),
					title,
					printerJob,
					pageFormat,
					true
				);
			}
		};

		runner.start();
	}

	/** Return printable component.
	 *
	 *	@param		title		Title for printing.
	 *
	 *	@param		pageFormat	Page format for printing.
	 *
	 *	@return					Printable component for XTextArea.
	 */

	public PrintableComponent getPrintableComponent
	(
		String title,
		PageFormat pageFormat
	)
	{
		return
			new PrintableComponent
			(
				this ,
				pageFormat,
				new PrintHeaderFooter(
					title,
					"Printed " +
						DateTimeUtils.formatDateOnAt( new Date() ),
					"Page " )
			);
	}

	/**	Scrolls to the beginning of the document.
	 */

	public void scrollToBeginning () {
		setCaretPosition(0);
		moveCaretPosition(0);
	}

	/**	Scrolls to the end of the document.
	 */

	public void scrollToEnd () {
		int len = getDocument().getLength();
		setCaretPosition(len);
		moveCaretPosition(len);
	}

	/**	Scrolls to the end of the document for sure.
	 *
	 *	<p>The Swing text package is very ornery when it comes to programmatic
	 *	scrolling. This "for sure" method starts a thread that wakes up every
	 *	tenth of a second for two seconds and makes sure that the text pane is
	 *	scrolled to the end.
	 */

	public void scrollToEndForSure () {
		scrollToEnd();
		new Thread(
			new Runnable() {
				public void run () {
					for (int i = 0; i < 20; i++) {
						try {
							Thread.sleep(100);
							SwingUtilities.invokeLater(
								new Runnable() {
									public void run () {
										scrollToEnd();
									}
								}
							);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			},
			"Scroll text area to end for sure"
		).start();
	}

	/** Check if clipboard has pasteable data.
	 *
	 *	@return		true if clipboard has pasteable text recognized
	 *				by XTextArea.
	 */

	public boolean clipboardHasPasteableData()
	{
		boolean result = false;

		Transferable content	= SystemClipboard.getContents( this );

		if ( content != null )
		{
			try
			{
				result =
					( content.isDataFlavorSupported(
						DataFlavor.stringFlavor ) );
			}
			catch ( Exception e )
			{
			}
        }

		return result;
	}

	/** Clear selection. */

	public void clearSelection()
	{
		setSelectionStart( 0 );
		setSelectionEnd( 0 );
	}

	/**	Cut to clipboard. */

	public void cut()
	{
		super.cut();
	}

	/**	Copy to clipboard. */

	public void copy()
	{
		super.copy();
	}

	/**	Paste from clipboard. */

	public void paste()
	{
		super.paste();
	}

	/**	Is cut enabled? */

	public boolean isCutEnabled()
	{
		return isEditable() && isTextSelected();
	}

	/**	Is copy enabled? */

	public boolean isCopyEnabled()
	{
		return isTextSelected();
	}

	/**	Is paste enabled? */

	public boolean isPasteEnabled()
	{
		return isEditable();
	}

	/**	Is anything selected which can be cut/copied? */

	public boolean isTextSelected()
	{
		String s	= getSelectedText();

		return ( ( s != null ) && ( s.length() > 0 ) );
	}

	/**	Save to a named file.
	 *
	 *	@param	fileName	Name of file to which to save results.
	 */

	public void saveToFile( String fileName )
	{
		try
		{
			FileUtils.writeTextFile
			(
				fileName ,
				false ,
				getText()
			);
		}
		catch ( Exception e )
		{
		}
	}

	/**	Save to a file using a file dialog.
	 *
	 *	@param	parentWindow	Parent window for file dialog.
	 *
	 *	<p>
	 *	Runs a file dialog to get the name of the file to which to
	 *	save results.
	 *	</p>
	 */

	public void saveToFile( Window parentWindow )
	{
		FileExtensionFilter	filter	=
			new FileExtensionFilter( ".txt" , "Text Files" );

		FileDialogs.addFileFilter( filter );

		String[] saveFile	= FileDialogs.save( parentWindow );

		FileDialogs.clearFileFilters();

		if ( saveFile != null )
		{
			File file	= new File( saveFile[ 0 ] , saveFile[ 1 ] );

			try
			{
			 	FileUtils.writeTextFile( file , false , getText() );
			}
			catch ( Exception e )
			{
			}
		}
	}

	/**	Selects all text.
	 */

	public void selectAll()
	{
		if ( isSelectAllEnabled() )
		{
			getCaret().setSelectionVisible( true );
			select( 0 , getDocument().getLength() );
			getCaret().setSelectionVisible( true );
		}
	}

	/**	Checks if "select all" enabled.
	 *
	 *	@return		returns true if select all enabled.
	 */

	public boolean isSelectAllEnabled()
	{
		return ( getDocument().getLength() > 0 );
	}

	/** Unselect selection. */

	public void unselect()
	{
		if ( isUnselectEnabled() )
		{
			select( getCaretPosition() , getCaretPosition() );
		}
	}

	/**	Is unselect enabled? */

	public boolean isUnselectEnabled()
	{
		return isTextSelected();
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

