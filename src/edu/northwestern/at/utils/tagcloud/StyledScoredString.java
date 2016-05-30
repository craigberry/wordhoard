package edu.northwestern.at.utils.tagcloud;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.*;

/**	Scored string with color and style information.
 */

public class StyledScoredString extends ScoredString
{
	/**	Color associated with string. */

	protected String color;

	/**	Style associated with string. */

	protected TagCloudTextStyle style;

	/**	Is string active in display. */

	protected boolean isActive;

	/**	Normal (plain) text style. */

	protected TagCloudTextStyle defaultStyle	=
		new TagCloudTextStyle( false , false );

	/**	Create scored string.
	 *
	 *	@param	string	String.
	 *	@param	score	Score.
	 *	@param	color	Color.
	 *	@param	style	Style.
	 */

	public StyledScoredString
	(
		String string ,
		double score ,
		String color ,
		TagCloudTextStyle style
	)
	{
		super( string , score );

		this.color		= color;
		this.style		= style;
		this.isActive	= true;
	}

	/**	Create scored string.
	 *
	 *	@param	string	String.
	 *	@param	score	Score.
	 *	@param	color	Color.
	 */

	public StyledScoredString
	(
		String string ,
		double score ,
		String color
	)
	{
		super( string , score );

		this.color		= color;
		this.style		= defaultStyle;
		this.isActive	= true;
	}

	/**	Create scored string.
	 *
	 *	@param	string	String.
	 *	@param	score	Score.
	 */

	public StyledScoredString
	(
		String string ,
		double score
	)
	{
		super( string , score );

		this.color		= "black";
		this.style		= defaultStyle;
		this.isActive	= true;
	}

	/**	Get color.
	 *
	 *	@return		The color.
	 */

	public String getColor()
	{
		return color;
	}

	/**	Set color.
	 *
	 *	@param	color	The color.
	 */

	public void setColor( String color )
	{
		this.color	= color;
	}

	/**	Get style.
	 *
	 *	@return		The style
	 */

	public TagCloudTextStyle getStyle()
	{
		return style;
	}

	/**	Set style.
	 *
	 *	@param	style	The style.
	 */

	public void setStyle( TagCloudTextStyle style )
	{
		this.style	= style;
	}

	/**	Get active status.
	 *
	 *	@return		If tag is active.
	 */

	public boolean getIsActive()
	{
		return isActive;
	}

	/**	Set active status.
	 *
	 *	@param	isActive	True if tag is active, false otherwise.
	 */

	public void setIsActive( boolean isActive )
	{
		this.isActive	= isActive;
	}

	/**	Generate displayable string.
	 *
	 *	@return		String followed by score in parentheses.
	 */

	public String toString()
	{
		String result	= super.toString();

		result			=
			result + ", " + color + ", " + style.toString() + ", ";

		if ( isActive )
		{
			result	= result + "active";
		}
		else
		{
			result	= result + "inactive";
		}

		return result;
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


