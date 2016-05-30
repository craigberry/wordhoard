package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.text.*;
import java.awt.*;
import javax.swing.*;

/**	A variant of a {@link BoxView} which knows how to lay out
 *	fully justified styled text.
 */

public class JustifiedTextRowView extends BoxView
{
	/** The row number being worked on.
	 */

	private int rowNumber = 0;

	/** Create a justified text row view.
	 *
	 *	@param	elem	The text line element for which to
	 *					create a view.
	 */

	JustifiedTextRowView( Element elem )
	{
		super( elem , View.X_AXIS );
	}

    /**	Calculates the size requirements for the minor axis.
     *
     *	@param	axis	The axis being studied.
     *
     *	@param	r		The <code>SizeRequirements</code> object;
     *					if <code>null</code> one will be created.
     *
     *	@return			The newly initialized <code>SizeRequirements</code>
     *					object.
     *
     *	@see			javax.swing.SizeRequirements
     */

	protected SizeRequirements calculateMinorAxisRequirements
	(
		int axis,
		SizeRequirements r
	)
	{
		return baselineRequirements( axis , r );
	}

	/**	Check if child view contains a paddable space.
	 *
	 *	@param	view	The view.
	 *
	 *	@return			= 0: child does not have a paddable space.
	 *					= 1: child has a trailing paddable space.
	 *					= -1: child has a leading paddable space.
	 */

	protected static int containsPaddableSpace( View view )
	{
		int	result		= 0;

		if ( view instanceof LabelView )
		{
			String text	= getDocumentTextForView( view );

			if ( text.length() > 0 )
			{
				if 	( text.charAt( 0 ) == ' ' )
				{
					result = -1;
				}
				else if ( text.charAt( text.length() - 1 ) == ' ' )
				{
					result = 1;
				}
			}
        }
        else
        {
        	result = -1;
        }

		return result;
	}

    /**	Determines the desired alignment for this view along an
     *	axis.
     *
     *	@param	axis	May be either <code>View.X_AXIS</code>
     *					or <code>View.Y_AXIS</code> .
     *
     *	@return			The desired alignment.
     *
     *					0.0 for left and fully justified alignment.
     *					0.5 for center alignment.
     *					1.0 for right alignment.
     *
     *	@exception		IllegalArgumentException for an invalid axis .
     */

	public float getAlignment( int axis )
	{
		if ( axis == View.X_AXIS )
		{
			AttributeSet attr	= getAttributes();
			int justification	= StyleConstants.getAlignment( attr );

			switch ( justification )
			{
				case StyleConstants.ALIGN_LEFT:
				case StyleConstants.ALIGN_JUSTIFIED:
					return 0.0f;

				case StyleConstants.ALIGN_RIGHT:
					return 1.0f;

				case StyleConstants.ALIGN_CENTER:
					return 0.5f;
			}
		}

		return super.getAlignment( axis );
	}

	/** Get attributes for this view.
	 *
	 *	@return		The attributes.  May be null.
	 */

	public AttributeSet getAttributes()
	{
		View p = getParent();

		return ( p != null ) ? p.getAttributes() : null;
	}

	/**	Get end of portion of the model for which this view is
     *	responsible.
     *
     *	@return		The ending offset into the model >= 0 .
     */

	public int getEndOffset()
	{
		int offs	= 0;
		int n		= getViewCount();

		for ( int i = 0; i < n; i++ )
		{
			View v = getView( i );

			offs = Math.max( offs , v.getEndOffset() );
		}

		return offs;
	}

	/** Override to make getLeftInset public.
	 */

	public short getLeftInset()
	{
		return super.getLeftInset();
	}

	/**	Determine maximum span for this view along an axis.
	 *
	 *	@param	axis	Either <code>View.X_AXIS</code> or
	 *					<code>View.Y_AXIS</code> .
	 *
	 *	@return			Maximum span the view can be rendered into.
	 */

	public float getMaximumSpan( int axis )
	{
		if ( axis == View.X_AXIS )
		{
			AttributeSet attr = getAttributes();

			if	(	( 	attr == null ) ||
					( 	StyleConstants.getAlignment( attr ) !=
						StyleConstants.ALIGN_JUSTIFIED ) )
			{
				return super.getMaximumSpan( axis );
			}
			else
			{
				return this.getParent().getMaximumSpan( axis );
			}
		}
		else
		{
			return super.getMaximumSpan( axis );
		}
	}

	/**	Determine minimum span for this view along an axis.
	 *
	 *	@param	axis	Either <code>View.X_AXIS</code> or
	 *					<code>View.Y_AXIS</code> .
	 *
	 *	@return			Minimum span the view can be rendered into.
	 */

	public float getMinimumSpan( int axis )
	{
		if ( axis == View.X_AXIS )
		{
			AttributeSet attr = getAttributes();

			if	(	(	attr == null ) ||
					( 	StyleConstants.getAlignment( attr ) !=
						StyleConstants.ALIGN_JUSTIFIED ) )
			{
				return super.getMinimumSpan( axis );
			}
			else
			{
				return this.getParent().getMinimumSpan( axis );
			}
		}
		else
		{
			return super.getMinimumSpan( axis );
		}
	}

	/**	Determine distribution of pad pixels for text justification.
	 *
	 *	@param	space				The number of pad pixels required
	 *								to justify a text line.
	 *
	 *	@param	viewsWithBlanks		The number of views with paddable
	 *								blanks to which we can allocate
	 *								pad pixels.
	 *
	 *	@param	direction			= 0:	Pad lefthand spaces
	 *									    more heavily
	 *								= 1:	Pad righthand spaces
	 *										more heavily.
	 *
	 *	@return						An array of containing the
	 *								number of pad pixels to add to
	 *								each view with paddable blanks.
	 *
	 *	<p>
	 *	Successive lines of displayed text alternate between
	 *	direction=0 and direction=1 .  This helps to even out the
	 *	appearance of the padding by having it on the left end on one
	 *	line and the right end on the next.
	 *	</p>
	 */

	protected int[] getPadPixels
	(
		int space ,
		int viewsWithBlanks ,
		int direction
	)
	{
								// Allocate vector of size
								// equal to the number of views
								// with paddable blanks.

		int[] result	= new int[ viewsWithBlanks ];

								// If no paddable blanks, return.

		if ( viewsWithBlanks == 0 )
		{
			return result;
		}
                                // Divide the number of pad pixels
                                // by the number of paddable views.
                                // This is how many pixels each
                                // view will get.  Spread the
                                // remainder of the pad pixels
                                // as evenly as possible over
                                // the views.

		int pixelsEachView	= space / viewsWithBlanks;
		int extraPixels		= space % viewsWithBlanks;

		for ( int i = 0; i < viewsWithBlanks; i++ )
		{
			result[ i ]	= pixelsEachView;
		}
								// If the direction is 0,
								// add extra padding pixels
								// to the left-most blanks
								// in the text line.
								// Otherwise add the extra
								// padding pixels to the
								// right-most blanks.

		if ( extraPixels > 0 )
		{
			if ( direction == 0 )
			{
				for ( int i = 0; i < viewsWithBlanks; i++ )
				{
					if ( extraPixels > 0 )
					{
						result[ i ]++;
						extraPixels--;
					}
				}
			}
			else
			{
				for ( int i = ( viewsWithBlanks - 1 ); i >= 0; i-- )
				{
					if ( extraPixels > 0 )
					{
						result[ i ]++;
						extraPixels--;
					}
				}
			}
        }

		return result;
	}

	/**	Determine preferred span for this view along an axis.
	 *
	 *	@param	axis	Either <code>View.X_AXIS</code> or
	 *					<code>View.Y_AXIS</code> .
	 *
	 *	@return			Preferred span the view can be rendered into.
	 */

	public float getPreferredSpan( int axis )
	{
		if ( axis == View.X_AXIS )
		{
			AttributeSet attr = getAttributes();

			if	(	StyleConstants.getAlignment( attr ) !=
					StyleConstants.ALIGN_JUSTIFIED )
			{
				return super.getPreferredSpan( axis );
			}
			else
			{
				return this.getParent().getPreferredSpan( axis );
			}
		}
		else
		{
			return super.getPreferredSpan( axis );
		}
	}

	/** Override to make getRightInset public.
	 */

	public short getRightInset()
	{
		return super.getRightInset();
	}

	/**	Get row number for this view.
	 *
	 *	@return		Row number of this view.
	 */

	public int getRowNumber()
	{
		return rowNumber;
	}

	/**	Get start of portion of the model for which this view is
     *	responsible.
     *
     *	@return		The starting offset into the model >= 0 .
     */

	public int getStartOffset()
	{
		int offs	= Integer.MAX_VALUE;
		int n		= getViewCount();

		for ( int i = 0; i < n; i++ )
		{
			View v	= getView( i );

			offs	= Math.min( offs , v.getStartOffset() );
		}

		return offs;
	}

	/** Override to make getTopInset public.
	 */

	public short getTopInset()
	{
		return super.getTopInset();
	}

    /**	Get child view index representing the given position in
     *	the model.
     *
     *	@param	pos		The position >= 0 .
     *
     *	@return			Index of the view at the given position, or
     *					-1 if no view is at that position.
     */

	protected int getViewIndexAtPosition( int pos )
	{
								// If the given position is outside
								// the starting and ending offsets,
								// it is not in a view.

		if ( ( pos < getStartOffset() ) || ( pos >= getEndOffset() ) )
		{
			return -1;
        }
                                // Loop over each view and see
                                // if the position lies within it.

		for ( int counter = getViewCount() - 1; counter >= 0; counter-- )
		{
			View view = getView( counter );

			if ( pos >= view.getStartOffset() && pos < view.getEndOffset() )
			{
				return counter;
			}
		}

		return -1;
	}

	/**	Get document text contained in a view.
	 *
	 *	@param	view	The view.
	 *
	 *	@return			The document text.  Empty if none.
	 */

	protected static String getDocumentTextForView( View view )
	{
		String result	= "";

		int	startOffset	= view.getStartOffset();
		int	len			= view.getEndOffset() - startOffset;

		try
		{
			result =
				view.getDocument().getText(
					 startOffset , len );
		}
		catch ( Exception e )
		{
		}

		return result;
	}

	/**	Perform layout for the major axis of the box.
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
	 *
	 *	<p>
	 *	This is where the actual text justification occurs.
	 *	Left, center, and right alignment are handled by the superclass
	 *	in the usual fashion.  The superclass is set up to treat
	 *	full alignment the same as left alignment.
	 *	</p>
	 *
	 *	<p>
	 *	Once the text has been left aligned, this method determines
	 *	how many extra pixels (if any) are needed to fully justify
	 *	each line.  Those pixels are distributed as evenly as possible
	 *	to the existing blank spaces in the text.  This results in
	 *	fully justified text.
	 *	</p>
	 */

	protected void layoutMajorAxis
	(
		int targetSpan,
		int axis,
		int[] offsets,
		int[] spans
	)
	{
								// Handle left, center, and right alignment.
								// Full justification starts out as
								// left aligned.

		super.layoutMajorAxis( targetSpan, axis, offsets, spans );

								// Quit if nothing else to do.

		if ( getRowNumber() == 0 ) return;

								// See if we're doing full justification.
								// If not, we're done, so just return.

		AttributeSet attr = getAttributes();

		if ( ( StyleConstants.getAlignment( attr ) !=
			StyleConstants.ALIGN_JUSTIFIED ) &&
			( axis != View.X_AXIS ) )
		{
			return;
		}
								// Get the number of individual label views
								// comprising the text for this line.

		int viewCount	= offsets.length;

								// No views?  Just return.

		if ( viewCount == 0 ) return;

								// The last view in the line, if a label
								// view, may contain bogus trailing spaces
								// that we need to trim before doing
								// anything further.

		View view		= getView( viewCount - 1 );

		if ( view instanceof LabelView )
		{
								// Get the document text for the
								// final view in the line.

			String viewText				= getDocumentTextForView( view );
			int	viewTextLength			= viewText.length();
			int	viewTextTrimmedLength	= viewText.length();

								// Backtrack from the end until we find a
								// non-blank character.  It is possible
								// that the entire string is blanks.

			while	(	( viewTextTrimmedLength > 0 ) &&
						( viewText.charAt( viewTextTrimmedLength - 1 ) == ' ' ) )
			{
				viewTextTrimmedLength--;
			}
								// Calculate the number of trailing blanks.

			int trailingBlanks	= ( viewTextLength - viewTextTrimmedLength );

								// If there are one or more trailing blanks,
								// get a fontmetrics item and reduce the
								// span needed to display this view
								// by the number of trailing blanks times
								// the width of a blank character in the
								// label view's font.

			if ( trailingBlanks > 0 )
			{
				Font font				= ((LabelView)view).getFont();
				FontMetrics fontMetrics	= view.getGraphics().getFontMetrics( font );
				int blankCharWidth		= fontMetrics.charWidth( ' ' );
				spans[ viewCount - 1 ]	-= blankCharWidth;
			}
		}
								// Figure out the total length of the
								// text as displayed in pixels by adding
								// up the pixel spans for each individual
								// label view.
								//
								// Also determine how many views in this line
								// contain a space at the beginning or the
								// end of the line.  Those are the only ones
								// we can modify by adding pad pixels.

		int span			= 0;
		int viewsWithSpaces	= 0;
/*
		System.out.println( "" );
		System.out.println( "Row number: " + this.getRowNumber() );

		for ( int i = 0; i < viewCount; i++ )
		{
			span			+= spans[ i ];

			view			= getView( i );
			String kind		= "";

			if ( view instanceof LabelView )
			{
				kind	= "LabelView";
        	}
        	else if ( view instanceof IconView )
        	{
				kind	= "OtherView";
        	}
        	else
        	{
        		kind	= "IconView ";
			}

			String viewText	= getDocumentTextForView( view );

			if 	( ( spans[ i ] > 0 ) && ( containsPaddableSpace( view ) != 0 ) )
			{
				viewsWithSpaces++;
			}

			System.out.println(
				"   " + i + ": " + kind + ": " + spans[ i ] + ": " +
				containsPaddableSpace( view ) + ": " +
				"<" + viewText + ">" );
		}
*/
		for ( int i = 0; i < viewCount; i++ )
		{
			span	+= spans[ i ];

			if 	(	( spans[ i ] > 0 ) &&
					( containsPaddableSpace( getView( i ) ) != 0 ) )
			{
				viewsWithSpaces++;
			}
		}
								// Get the starting offset and
								// length of the text for this line.

		int startOffset	= getStartOffset();
		int len			= getEndOffset() - startOffset;

								// Get text for this line.

		String context	= "";

		try
		{
			context =
				getElement().getDocument().getText( startOffset , len );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
								// Determine the number of pad pixels we
								// have to add to fully justify the text.
								// This is the difference between the
								// width of the display area in pixels
								// and the total of the widths of the
								// individual views in this line in pixels.

		int pixelsToAdd = targetSpan - span;

								// If this is the first line, we may need to
								// indent the first view on the line.  In that
								// case, reduce the number of fill pixels
								// needed to align the text by the size of
								// the indent.

		if ( this.getRowNumber() == 1 )
		{
			int firstLineIndent	=
				(int)StyleConstants.getFirstLineIndent(
					getAttributes() );

			pixelsToAdd -= firstLineIndent;
		}
                                // Determine the number of pad pixels
                                // to be added to each label view.  We try
                                // to spread the fill pixels as evenly as
                                // possible over the label views.
		int[] padPixels	=
			getPadPixels(
				pixelsToAdd , viewsWithSpaces , this.getRowNumber() % 2 );
/*
		System.out.println( "" );
		System.out.println( "ViewsWithSpaces: " + viewsWithSpaces );

		for ( int i = 0; i < viewsWithSpaces; i++ )
		{
			System.out.println( i + ":" + spaces[ i ] );
		}
*/
								// For each view to which we can add
								// fill pixels (starts or ends with a blank),
								// move the text over by the number of pad
								// pixels.
		int j			= 0;
		int shift		= 0;

		for ( int i = 1; i < viewCount; i++ )
		{
			offsets[ i ]	+= shift;

			int padSpot		= containsPaddableSpace( getView( i ) );

       		if	(	( padSpot != 0 ) &&
       				( spans[ i ] > 0 ) &&
       				( j < viewsWithSpaces ) )
			{
				offsets[ i ]	+= padPixels[ j ];
				spans[ i - 1 ]	+= padPixels[ j ];
				shift			+= padPixels[ j ];
				j++;
			}
		}
/*
		if ( j != viewsWithSpaces )
		{
			System.out.println( "*** j=" + j + ", should be " + viewsWithSpaces );
		}
*/
	}

	/**	Perform layout for the minor axis of the box.
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
	 *	The offset and span for each child view is stored in
	 *	the offsets and spans parameters.
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
		baselineLayout( targetSpan , axis , offsets , spans );
	}

	/** Load child views.  No-op here.
	 */

	protected void loadChildren( ViewFactory f )
	{
	}

    /**	Map from document model coordinate space
     *	to coordinate space of the view mapped to it.
     *
     *	@param	pos		The position to convert >= 0.
     *
     *	@param	a		The allocated region to render into.
     *
     *	@param	b		The position bias value of either
     *					<code>Position.Bias.Forward</code> or
     *					<code>Position.Bias.Backward</code> .
     *
     *	@return			The bounding box of the given position.
     *
     *	@exception		BadLocationException
     *					If the given position does
     *					not represent a valid location in the
     *					associated document.
     */

	public Shape modelToView
	(
		int pos,
		Shape a,
		Position.Bias b
	)
		throws BadLocationException
	{
		Rectangle r	= a.getBounds();
		View view	= getViewAtPosition( pos , r );

		if ( ( view != null ) && ( !view.getElement().isLeaf() ) )
		{
				// Don't adjust the height if the view represents a branch.

			return super.modelToView( pos , a , b );
		}

		r			= a.getBounds();
		int height	= r.height;
		int y		= r.y;

		Shape loc	= super.modelToView( pos , a , b );

		r			= loc.getBounds();
		r.height	= height;
		r.y			= y;

		return r;
	}

	/** Override to make setInsets public.
	 */

	public void setInsets
	(
		short topInset,
		short leftInset,
		short bottomInset,
		short rightInset
	)
	{
		super.setInsets( topInset, leftInset, bottomInset, rightInset );
	}

	/**	Set row number of this view.
	 *
	 *	@param	rowNumber	The row number of this view.
	 */

	public void setRowNumber( int rowNumber )
	{
		this.rowNumber = rowNumber;
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

