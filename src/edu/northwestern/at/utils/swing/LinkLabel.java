package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** LinkLabel: a label which optionally contains clickable hypertext.
 *
 *	<p>
 *	<strong>Restriction:</strong>
 *	</p>
 *	<p>
 *	If you add an icon to the label, make sure your text always follows
 *	the icon.  You may adjust the vertical text position as you desire.
 *	</p>
 *	<p>
 *	You may also request that the mouse cursor change to the hand cursor
 *	whenever the mouse passes over clickable text.  Invoke:
 *	</p>
 *	<code>
 *		setChangeCursor( true );
 *	</code>
 */

public class LinkLabel extends JLabel
{
	/** Link to go to when label is clicked. */

	protected Link link = null;

	/** True to change cursor to hand cursor when mouse moves over clickable
	 *	text.
	 */

	protected boolean changeCursor = false;

	/** Initial foreground color. */

	protected Color foregroundColor;

	/** Current cursor. */

	private Cursor currentCursor = Cursors.DEFAULT_CURSOR;

	/** Create an empty linkLabel. */

	public LinkLabel()
	{
		this( "" , null );
	}

	/** Create a linkLabel with label text.
	 *
	 * @param text	Text of label
	 */

	public LinkLabel( String text )
	{
		this( text , null );
	}

	/** Creates a linkLabel with a specified link.
	 *
	 * @param text		Text of label.
	 * @param link		Link.
	 */

	public LinkLabel( String text , Link link )
	{
		this( text, link, false );
	}

	/** Creates a linkLabel with a specified link and cursor change setting.
	 *
	 *	@param	text			Text of label.
	 *	@param	link			Link.
	 *	@param 	cursorChange	True to change cursor when mouse passes over
	 *							link text.
	 */

	public LinkLabel( String text , Link link , boolean cursorChange )
	{
		super( text );

		setLink( link );

		addMouseListener( mouseListener );

		setChangeCursor( cursorChange );

		foregroundColor = getForeground();

		if ( link != null )
			setForeground( Color.blue );
	}

	/**
	 * Sets the link.
	 *
	 * @param	link	Link.
	 */

	public void setLink( Link link )
	{
		this.link  = link;

		if ( link != null )
			setForeground( Color.blue );
		else
			setForeground( foregroundColor );
	}

	/**
	 * Returns the link.
	 *
	 * @return	The link.
	 */

	public Link getLink()
	{
		return link;
	}

	/**
	 * Sets the mouse cursor changeable.
	 *
	 * @param	changeCursor	True to change the mouse cursor to the
	 *							hand cursor when passing over the
	 *							link text.
	 */

	public void setChangeCursor( boolean changeCursor )
	{
		boolean currentChangeCursor = this.changeCursor;

		this.changeCursor  = changeCursor;

		if ( currentChangeCursor != this.changeCursor )
		{
			if ( this.changeCursor )
			{
				addMouseMotionListener( mouseMotionListener );
			}
			else
			{
				removeMouseMotionListener( mouseMotionListener );

				setCursor( Cursors.DEFAULT_CURSOR );
			}
		}
	}

	/**
	 * Returns the current change cursor setting.
	 *
	 * @return	True if the cursor is allowed to change to
	 *			the hand cursor when the mouse passes over the link text.
	 */

	public boolean getChangeCursor()
	{
		return changeCursor;
	}

	/** Is the mouse cursor positioned over the link text?
	 *
	 *	@param	e			The mouse event.
	 *	@return				True if the mouse is positioned over the link text.
	 */

	protected boolean cursorOverLink( MouseEvent e )
	{
		boolean result = false;

								// No link?  Mouse can't be over the link.
		if ( link != null )
		{
								// Get the x-coordinate of the current mouse
								// position relative to the label component.

			Point point		= e.getPoint();

								// Get the text position within the label.

			Rectangle rect	= getTextPosition();

								// If the current mouse position lies within
								// the interval covered by the link text,
								// return true.
			result =
				( 	( point.x >= rect.x ) &&
					( point.x <= ( rect.x + rect.width ) ) ) &&
				( 	( point.y >= ( rect.y - rect.height ) ) &&
					( point.y <= rect.y ) );
        }

		return result;
	}

	/** Is a specified point over the link text?
	 *
	 *	@param	point	The point to check.
	 *	@return			True if the point is over the link text.
	 */

	public boolean pointOverLink( Point point )
	{
		boolean result = false;

								// No link?  Mouse can't be over the link.
		if ( link != null )
		{
								// Get the text position within the label.

			Rectangle rect	= getTextPosition();

								// If the current mouse position lies within
								// the interval covered by the link text,
								// return true.
			result =
				( 	( point.x >= rect.x ) &&
					( point.x <= ( rect.x + rect.width ) ) ) &&
				( 	( point.y >= ( rect.y - rect.height ) ) &&
					( point.y <= rect.y ) );
        }

		return result;
	}

	/**	Paints the label.
	 *
	 *	@param	g	Graphics object for painting
	 *
	 *	<p>
	 *	If the label has a link defined, the link text is
	 *	painted as blue underlined text.
	 *	</p>
	 */

	public void paint( Graphics g )
	{
								// Paint label text.
		super.paint( g );
								// Done if no link.
		if ( link != null )
		{
								// Get position of the text in the label.

			Rectangle rect = getTextPosition();

								// Underline the text.
			g.drawLine(
				rect.x,
				rect.y - 2,
				rect.width + rect.x,
				rect.y - 2 );
		}
	}

	/** Get the position of the text within the label.
	 *
	 *	@return		The position as a Rect.  Note that the vertical text
	 *				position is the bottom of the text, not the top.
	 *				We need the bottom location for underlining.
	 */

	protected Rectangle getTextPosition()
	{
		Rectangle result = new Rectangle();

								// Get size of label text.

		Dimension size	= getSize();

								// Get width and height of label text.

		FontMetrics metrics	= getFontMetrics( getFont() );

		int strWidth		= metrics.stringWidth( getText() );
		int strHeight		= metrics.getHeight();

		result.height		= strHeight;
		result.width		= strWidth;

								// Adjust for icon.  ONLY WORKS IF THE ICON
								// IS LOCATED TO THE LEFT OF THE TEXT!

		Icon icon			= getIcon();
		int iconWidth		= 0;

		if ( icon != null ) iconWidth = icon.getIconWidth() + getIconTextGap();

								// Get location to draw underlined text based
								// upon text location and size, and icon
								// location and size.

		switch ( getHorizontalAlignment() )
		{
			case SwingConstants.LEFT:
			case SwingConstants.LEADING:
				result.x = iconWidth;
				break;

			case SwingConstants.CENTER:
				result.x = ( iconWidth + size.width - strWidth ) / 2;
				break;

			case SwingConstants.RIGHT:
			case SwingConstants.TRAILING:
				result.x = size.width - strWidth;
				break;
		}

//		switch ( getVerticalTextPosition() )
		switch ( getVerticalAlignment() )
		{
			case SwingConstants.TOP:
				result.y = strHeight;
				break;

			case SwingConstants.CENTER:
				result.y = ( strHeight + size.height ) / 2;
				break;

			case SwingConstants.BOTTOM:
				result.y = size.height;
				break;
		}

		return result;
	}

	/** Mouse listener.
	 *
	 *	<p>
	 *	When the mouse is positioned over the link text, change the
	 *	cursor to a hand cursor.  When the link is clicked, go to
	 *	the specified link position.
	 *	</p>
	 */

	protected MouseListener mouseListener =
		new MouseAdapter()
		{
			public void mouseClicked( MouseEvent event )
			{
				if ( cursorOverLink( event ) ) link.go();
			}
		};

	/** Change cursor.
	 *
	 *	@param	cursor		The new cursor.
	 */

	protected void changeCursor( Cursor cursor )
	{
		currentCursor = cursor;

		setCursor( cursor );
	}

	/** Mouse motion listener.
	 *
	 *	<p>
	 *	When the mouse is positioned over the link text, change the
	 *	cursor to a hand cursor.  When the mouse is not position over
	 *	the link text, use the default cursor.
	 *	</p>
	 */

	protected MouseMotionListener mouseMotionListener =
		new MouseMotionAdapter()
		{
			public void mouseMoved( MouseEvent event )
			{
				if ( cursorOverLink( event ) )
				{
					changeCursor( Cursors.HAND_CURSOR );
				}
				else
				{
					changeCursor( Cursors.DEFAULT_CURSOR );
				}
			}
		};

	/** Get the current cursor.
	 *
	 *	@return		The current cursor.
	 */

	public Cursor getCurrentCursor()
	{
		return currentCursor;
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

