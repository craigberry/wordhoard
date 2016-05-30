package edu.northwestern.at.utils.swing.html;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.net.*;
import javax.swing.text.html.*;

/** Utility methods for working with HTML documents. */

public class HTMLUtils
{
	public static String stripHTML( String htmlText )
	{
								// Stripped HTML text will be written
								// into stringWriter.

		StringWriter stringWriter = new StringWriter( htmlText.length() );

		try
		{
								// Get HTML parser instance.

			HTMLParserGetter kit		= new HTMLParserGetter();
			HTMLEditorKit.Parser parser	= kit.getParser();

                                // Set HTML parser callback which is
                                // called for each type of HTML tag.
                                // Line-breaking tags are replaced with
                                // a blank.


			HTMLEditorKit.ParserCallback callback =
				new HTMLStripper
				(
					stringWriter ,
					System.getProperty( "line.separator" , " " )
				);

								// Open input stream over html text.

			StringReader stringReader = new StringReader( htmlText );

								// Parse the URL contents.

			parser.parse( stringReader , callback , false );
		}
		catch ( IOException e )
		{
//			System.err.println( e );
		}

		return stringWriter.toString();
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

