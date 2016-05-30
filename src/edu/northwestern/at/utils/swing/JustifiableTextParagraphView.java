package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.text.*;
import java.awt.*;
import javax.swing.*;

/**	A paragraph view for styled text which can contain fully justified
 *	lines of text.
 */

public class JustifiableTextParagraphView extends ParagraphView
{
	/** Create a JustifiableParagraphView for a paragraph element.
	 *
	 *	@param	elem	The paragraph text element.
	 *
	 *	<p>
	 *	{@link JustifiableTextFlowStrategy} is used as the
	 *	flow strategy.  Each line of text in the paragraph
	 *	is stored in a {@link JustifiedTextRowView}.
	 *	</p>
	 */

	public JustifiableTextParagraphView( Element elem )
	{
		super( elem );

		strategy = new JustifiableTextFlowStrategy();
	}

    /**	Create a {@link JustifiedTextRowView} to hold a
     *	a row's worth of children in a flow.
     *
     *	@return		The new <code>JustifiedTextRowView</code>.
     */

	protected View createRow()
	{
		Element elem = getElement();

		return new JustifiedTextRowView( elem );
	}

    /**	Fetch the constraining span to flow against for
     *	the given child index.
     *
     *	@param	index	The index of the view being queried.
     *
     *	@return			The constraining span for the given view at
     *					<code>index</code>.
     */

	public int getFlowSpan( int index )
	{
								// Get the flow span from the
								// super class.

		int span = super.getFlowSpan( index );

			                    // If this is the first line in a
			                    // paragraph, honor the first line
			                    // indent attribute if specified.
		if ( index == 0 )
		{
			int firstLineIdent =
				(int)StyleConstants.getFirstLineIndent( this.getAttributes() );

			span -= firstLineIdent;
		}

		return span;
	}

	/**	Perform layout for the minor axis of the box.
	 *
	 *	<p>
	 *	This is the same as the layout for a standard paragraph view
	 *	except that we honor the first line indent attributed.
	 *	</p>
	 *
	 *	@param	targetSpan		The total span given to the view, which
	 *							should be used to layout the children.
	 *
	 *	@param	axis			The axis being layed out.
	 *
	 *	@param	offsets			The offsets from the origin of the view
	 *							for each of the child views.
	 *							This is a return value and is
	 *							filled in here.
	 *
	 *	@param	spans			The span of each child view.
	 *							This is a return
	 *							value and is filled in here.
	 *
	 *	<p>
	 *	The offset and span for each child view is stored in the
	 *	offsets and spans parameters.
	 *	</p>
	 */

	protected void layoutMinorAxis
	(
		int targetSpan,
		int axis,
		int[] offsets,
		int[] spans
	)
	{
								// Let the superclass do the initial layout.

		super.layoutMinorAxis( targetSpan, axis, offsets, spans );

			                    // Adjust for any specified first line indent.

		int firstLineIdent	=
			(int)StyleConstants.getFirstLineIndent( this.getAttributes() );

		offsets[ 0 ] += firstLineIdent;
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

