package edu.northwestern.at.utils.swing.html;

/*	Please see the license information at the end of this file. */

import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import java.io.*;
import java.net.*;

/** Strips HTML tags.
 *
 *	<p>
 *	Tags which cause line breaks such as &lt;br&gt;, &lt;p&gt;, etc.
 *	when the HTML is displayed are converted to user-specified line separator
 *	characters.
 *	</p>
 */

public class HTMLStripper extends HTMLEditorKit.ParserCallback
{
	/** Receives stripped text. */

	private Writer out;

	/** Line separator to use when converting line-breaking HTML tags. */

	private String lineSeparator;

	/** Create HTML stripper using default end of line separator.
	 *
	 *	@param	out		Writer to which stripped text will be written.
	 */

	public HTMLStripper( Writer out )
	{
		this( out , System.getProperty( "line.separator" , "\r\n" ) );
	}

	/** Create stripper using default end of line separator.
	 *
	 *	@param	out				Writer to which stripped text will be written.
	 *	@param	lineSeparator	Characters to separate lines.
	 */

	public HTMLStripper( Writer out , String lineSeparator )
	{
		this.out			= out;
		this.lineSeparator	= lineSeparator;
	}

	/** Handles ordinary text in HTML input.
	 *
	 *	@param	text		The text.
	 *	@param	position	Position of the text in the file
	 *						(not used here).
	 *
	 *	<p>
	 *	This is plain text, so it is written as it stands to
	 *	the output.
	 *	</p>
	 */

	public void handleText( char[] text , int position )
	{
		try
		{
			out.write( text );
			out.flush();
		}
		catch ( IOException e )
		{
//			System.err.println( e );
		}
	}

	/** Handles an end tag (&lt;br&gt;, &lt;p&gt;, etc.)
	 *
	 *	@param	tag			The end tag.
	 *	@param	position	Position of the tag in the file
	 *						(not used here).
	 *
	 *	<p>
	 *	For a block type tag (e.g., &lt;p&gt;) two sets of line separator
	 *	characters are written.  For a text break tag
	 *	(e.g., &lt;br&gt;) a single set of line separator characters
	 *	is written.
	 *	</p>
	 */

	public void handleEndTag( HTML.Tag tag , int position )
	{
	    try
	    {
			if ( tag.isBlock() )
			{
				out.write( lineSeparator );
				out.write( lineSeparator );
			}
			else if ( tag.breaksFlow() )
			{
				out.write( lineSeparator );
			}
		}
		catch ( IOException e )
		{
//			System.err.println(e);
		}
	}

	/** Handles simple tags.
	 *
	 *	@param	tag			The simple tag.
	 *	@param	attributes	Tag attributes.
	 *	@param	position	Position of the tag in the file
	 *						(not used here).
	 *
	 *	<p>
	 *	For a block type tag (e.g., &lt;p&gt;) two sets of line separator
	 *	characters are written.  For a text break tag
	 *	(e.g., &lt;br&gt;) a single set of line separator characters
	 *	is written.  For all other tags, a single blank is output.
	 *	</p>
	 */

	public void handleSimpleTag
	(
		HTML.Tag tag,
		MutableAttributeSet attributes,
		int position
	)
	{
		try
		{
			if ( tag.isBlock() )
			{
				out.write( lineSeparator );
				out.write( lineSeparator );
			}

			else if ( tag.breaksFlow() )
			{
				out.write( lineSeparator );
			}

			else
			{
				out.write( ' ' );
			}
		}
		catch ( IOException e )
		{
//			System.err.println( e );
		}
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

