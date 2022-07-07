package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

/**
 *	Defines a rectangle in terms of its coordinates.
 */

public class LocationRectangle
{
	/** (x,y) coordinates of upper left-hand corner. */

	public int x1;
	public int y1;

	/** (x,y) coordinates of lower right-hand corner. */

	public int x2;
	public int y2;

	/** Create an empty location rectangle.
	 */

	public LocationRectangle()
	{
		x1 = 0;
		y1 = 0;
		x2 = 0;
		y2 = 0;
	}

	/** Create a location rectangle from specified coordinates.
	 *
	 *	@param	x1	Upper left x coordinate.
	 *	@param	y1	Upper left y coordinate.
	 *	@param	x2	Lower right x coordinate.
	 *	@param	y2	Lower right y coordinate.
	 */

	public LocationRectangle( int x1, int y1, int x2, int y2 )
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/** Create a location rectangle from another location rectangle.
	 *
	 *	@param	locationRectangle	Location rectangle from which to
	 *								copy settings.
	 */

	public LocationRectangle( LocationRectangle locationRectangle )
	{
		this.x1 = locationRectangle.x1;
		this.y1 = locationRectangle.y1;
		this.x2 = locationRectangle.x2;
		this.y2 = locationRectangle.y2;
	}

	/** Check if another LocationRectangle is equal to this.
	 *
	 *	@param	locationRectangle	The other location rectangle.
	 *	@return True if equal, false if not.
	 */

	public boolean equals( LocationRectangle locationRectangle )
	{
		if ( locationRectangle == null ) return false;

		return (	( locationRectangle.x1 == x1 ) &&
				    ( locationRectangle.y1 == y1 ) &&
				    ( locationRectangle.x2 == x2 ) &&
				    ( locationRectangle.y2 == y2 ) );
	}

	/** Display form of location rectangle.
	 *
	 *	@return		Displayable string with location coordinates.
	 */

	public String toString()
	{
		return "[x1=" + x1 + ",y1=" + y1 + ",x2=" + x2 + ",y2=" + y2 + "]";
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

