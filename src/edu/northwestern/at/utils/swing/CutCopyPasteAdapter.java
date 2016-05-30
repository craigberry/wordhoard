package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.datatransfer.*;
import java.util.*;

import javax.swing.text.*;

import edu.northwestern.at.utils.*;

/**	CutCopyPasteAdapter adds CutCopyPaste interface methods to a text component.
 */

public class CutCopyPasteAdapter
{
	/**	The text component to make CutCopyPaste compatible.
	 */

	protected JTextComponent textComponent;

	/**	Add CutCopyPaste to a JTextComponent.
	 *
	 *	@param	textComponent	A JTextComponent to which to attach
	 *							CutCopyPaste interface methods.
	 */

	public CutCopyPasteAdapter( JTextComponent textComponent )
	{
		this.textComponent	= textComponent;
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
		textComponent.setSelectionStart( 0 );
		textComponent.setSelectionEnd( 0 );
	}

	/**	Cut to clipboard. */

	public void cut()
	{
		textComponent.cut();
	}

	/**	Copy to clipboard. */

	public void copy()
	{
		textComponent.copy();
	}

	/**	Paste from clipboard. */

	public void paste()
	{
		textComponent.paste();
	}

	/**	Is cut enabled? */

	public boolean isCutEnabled()
	{
		return textComponent.isEditable() && isTextSelected();
	}

	/**	Is copy enabled? */

	public boolean isCopyEnabled()
	{
		return isTextSelected();
	}

	/**	Is paste enabled? */

	public boolean isPasteEnabled()
	{
		return textComponent.isEditable();
	}

	/**	Is anything selected which can be cut/copied? */

	public boolean isTextSelected()
	{
		String s	= textComponent.getSelectedText();

		return ( ( s != null ) && ( s.length() > 0 ) );
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

