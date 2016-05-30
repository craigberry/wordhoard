package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.text.*;

/**	An extended styled view factory which creates views for
 *	paragraphs allowing full justification of text.
 */

public class XStyledViewFactory implements ViewFactory
{
	/** The parent view factory to use to create views for text elements
	 *	other than paragraph elements.
	 */

	private ViewFactory parentFactory;

	/**	Set the parent view factory.
	 *
	 *	@param	parentFactory	The parent factory.
	 *
	 *	<p>
	 *	The parent factory is used to create views for all text
	 *	elements other than paragraph elements.
	 *	</p>
	 */

	public XStyledViewFactory( ViewFactory parentFactory )
	{
		this.parentFactory	= parentFactory;
	}

	/** Create a view for a text element.
	 *
	 *	@param	elem	The text element for which a view is desired.
	 *
	 *	@return			The view for the text element.
	 *
	 *	<p>
	 *	{@link JustifiableTextParagraphView} is used to create a view
	 *	for a paragraph.  JustifiableTextParagraphView knows how to
	 *	fully justify lines of text.
	 *	The parent factory is used to create views for all other
	 *	text element types.
	 *	</p>
	 */

	public View create( Element elem )
	{
								// Get the name of the type of
								// text element.

		String kind = elem.getName();

								// Is the element a paragraph?
								// Create a JustifiableTextParagraphView
								// which knows how to fully justify text.

		if	(	( kind != null ) &&
				( kind.equals( AbstractDocument.ParagraphElementName ) ) )
		{
			return new JustifiableTextParagraphView( elem );
		}
								// Not a paragraph?  Get the view from
								// the parent factory.

		return parentFactory.create( elem );
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

