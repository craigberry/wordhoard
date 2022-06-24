package edu.northwestern.at.utils.tagcloud;

/*	Please see the license information at the end of this file. */

/**	Text styles for tag cloud.
 *
 *	<p>
 *	Defines text styles which can be plain, bold, italic, or bold
 *	and italic.
 *	</p>
 */

public class TagCloudTextStyle
{
	/**	Is text bold. */

	protected boolean isBold;

	/**	Is text italic. */

	protected boolean isItalic;

	/**	Create text style.
	 *
	 *	@param	isBold		True if text is bold.
	 *	@param	isItalic	True if text is italic.
	 *
	 *	<p>
	 *	Text may be both bold and italic.
	 *	</p>
	 */

	public TagCloudTextStyle( boolean isBold , boolean isItalic )
	{
		this.isBold		= isBold;
		this.isItalic	= isItalic;
	}

	/**	Is style plain normal?
	 *
	 *	@return		true if text is plain normal text.
	 */

	public boolean isNormal()
	{
		return !( isBold || isItalic );
	}

	/**	Is style bold?
	 *
	 *	@return		true if text is bold text.
	 */

	public boolean isBold()
	{
		return isBold;
	}

	/**	Is style italics?
	 *
	 *	@return		true if text is italic.
	 */

	public boolean isItalic()
	{
		return isItalic;
	}

	/**	Get style as string.
	 *
	 *	@return			The style as a string.
	 */

	public String toString()
	{
		String result	= "";

		if ( isBold )
		{
			result	= "bold";
		}

		if ( isItalic )
		{
			if ( result.length() > 0 )
			{
				result	+= " ";
			}

			result	+= "italic";
		}

		if ( result.length() == 0 )
		{
			result	= "normal";
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


