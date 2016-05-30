package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/** Listens for changes to document being edited. */

public class CustomDocumentListener implements DocumentListener
{
	/** Check if event really does something.
	 *
	 *	@param	e		An event to check.
	 *
	 *	@return			True if event actually did something
	 *					to the document.
	 *
	 *	<p>
	 *	For some reason, the document listener can get events
	 *	saying something changed, but in fact these events record
	 *	no real change to the document.  To prevent the document from
	 *	being marked dirty unnecessarily, we check the "toString()"
	 *	of the event.  A bogus event returns "[]" as its toString(),
	 *	while a real event will have a much lengthier toString()
	 *	with information about the nature of the change, what
	 *	was changed, etc.
	 *	</p>
	 */

	protected boolean isRealChange( DocumentEvent e )
	{
		return ( !e.toString().equals( "[]" ) );
	}

	public void insertUpdate( DocumentEvent e )
	{
		isRealChange( e );
	}

	public void removeUpdate( DocumentEvent e )
	{
		isRealChange( e );
	}

	public void changedUpdate( DocumentEvent e )
	{
		isRealChange( e );
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

