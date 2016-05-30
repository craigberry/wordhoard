package edu.northwestern.at.utils.swing.icons;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

/**	Filled triangle icon.
 *
 *	<p>
 *	Creates an iconic filled up or down triangle right
 *	justified in its bounds and centered vertically.
 *	Useful for sorting direction indicators.
 *	</p>
 */

public class FilledTriangleIcon extends JComponent implements Icon
{
	/**	True if up, false if down. */

	private boolean up;

	/**	Width in pixels of triangle base. */

	private int width;

	/**	Constructs a new filled triangle component.
	 *
	 *	@param	up		True if up, false if down.
	 *
	 *	@param	width	Width in pixels of triangle base.
	 */

	public FilledTriangleIcon( boolean up , int width )
	{
		super();

		this.up		= up;
		this.width	= width;

		setPreferredSize( new Dimension( width + 2 , width + 2 ) );
	}

	/**	Gets the icon height (size).
	 *
	 *	@return		The icon height.
	 */

	public int getIconHeight()
	{
		return width + 2;
	}

	/**	Gets the icon width (size).
	 *
	 *	@return		The icon width.
	 */

	public int getIconWidth()
	{
		return width + 2;
	}

	/**	Paints the triangle.
	 *
	 *	@param	c	Component.
	 *
	 *	@param	g	Graphics environment.
	 *
	 *	@param	x	X coordinate of icon.
	 *
	 *	@param	y	Y coordinate of icon.
	 */

	public void paintIcon( Component c , Graphics g , int x , int y )
	{
		Dimension d	= c.getSize();
		int w		= d.width;
		int h		= d.height;
		int yy		= h / 2;
		int z		= width / 2;

		int[] xPoints	= new int[]{ w - width , w , w - z };

		int[] yPoints;

		if ( up )
		{
			yPoints	=
				new int[]
				{
					yy + z ,
					yy + z ,
					yy - z
				};
		}
		else
		{
			yPoints	=
				new int[]
				{
					yy - z + 1 ,
					yy - z + 1 ,
					yy + z + 1
				};
		}

		g.fillPolygon( xPoints , yPoints , 3 );
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

