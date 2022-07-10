package edu.northwestern.at.utils.swing.styledtext;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import javax.swing.text.*;
import javax.swing.text.rtf.*;
import javax.swing.text.html.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.utils.swing.*;

/**	Styled text utilities.
 *
 *	<p>
 *	This static class provides various utility methods for
 *	manipulating styled strings, including reading and writing
 *	styled strings to files.
 *	</p>
 */

public class StyledStringUtils
{
	/** Create styled text string.
	 *
	 *	@param	s			Text to which to apply style.
	 *	@param	styleType	Type of style to apply.
	 *						The available style types are
	 *						defined in {@link StyleRun}.
	 *
	 *	@return				The styled text as a StyledString.
	 */

	static public StyledString makeStyledText( String s , int styleType )
	{
		StyleRun styleRun =
			new StyleRun( 0, s.length() /*- 1*/, styleType );

		ArrayList styleRunArrayList = new ArrayList();

		styleRunArrayList.add( styleRun );

		StyleInfo styleInfo = new StyleInfo( styleRunArrayList );

		StyledString result = new StyledString( s , styleInfo );

		return result;
	}

	/** Create multiply styled text string.
	 *
	 *	@param	s			Text to which to apply style.
	 *	@param	styleTypes	Integer array of styles to apply.
	 *						The available style types are
	 *						defined in {@link StyleRun}.
	 *
	 *	@return				The styled text as a StyledString.
	 */

	static public StyledString makeStyledText( String s , int[][] styleTypes )
	{
		ArrayList styleRunArrayList = new ArrayList();

		for ( int i = 0; i < styleTypes.length; i++ )
		{
			StyleRun styleRun =
				new StyleRun(
					0,
					s.length() /*- 1*/,
					styleTypes[ i ][ 0 ],
					styleTypes[ i ][ 1 ] );

			styleRunArrayList.add( styleRun );
		}

		StyleInfo styleInfo = new StyleInfo( styleRunArrayList );

		StyledString result = new StyledString( s , styleInfo );

		return result;
	}

	/** Create bold string.
	 *
	 *	@param	s			Text to embolden.
	 *
	 *	@return				The emboldened text as a StyledString.
	 */

	static public StyledString emboldenText( String s )
	{
		return makeStyledText( s , StyleRun.BOLD );
	}

	/** Create italicized string.
	 *
	 *	@param	s			Text to italicize.
	 *
	 *	@return				The italicized text as a StyledString.
	 */

	static public StyledString italicizeText( String s )
	{
		return makeStyledText( s , StyleRun.ITALIC );
	}

	/** Create underline string.
	 *
	 *	@param	s			Text to underline
	 *
	 *	@return				The underlined text as a StyledString.
	 */

	static public StyledString underlineText( String s )
	{
		return makeStyledText( s , StyleRun.UNDERLINE );
	}

	/** Get styled string from transferable containing RTF.
	 *
	 *	@param	transferable	The transferable with RTF text.
	 *
	 *	@return					The RTF text as a StyledString.
	 */

	public static StyledString getStyledStringFromRTFTransferable
	(
		Transferable transferable
	)
	{
		StyledString result = new StyledString();

		RTFEditorKit rtfKit = new RTFEditorKit();

		XTextPane rtfPane = new XTextPane();

		DefaultStyledDocument rtfDoc = (DefaultStyledDocument)rtfPane.getDoc();

		ByteArrayOutputStream byteOut = null;

		try
		{
			Reader reader =
				StyledString.RTF_FLAVOR.getReaderForText( transferable );

			rtfKit.read( reader, rtfDoc, 0 );

			byteOut = new ByteArrayOutputStream();

			rtfKit.write( byteOut, rtfDoc, 0, rtfDoc.getLength() );

			result = new StyledString(
				rtfDoc.getText( 0 , rtfDoc.getLength() ),
				rtfPane.getStyleInfo( 0 , rtfDoc.getLength() ) );
		}
		catch( Exception e )
		{
//			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( byteOut != null ) byteOut.close();
			}
			catch ( Exception e )
			{
			}
		}

		return result;
	}

	/** Convert string containing styled text to RTF.
	 *
	 *	@param		styledString	The styled string.
	 *
	 *	@return						RTF text of object.
	 */

	public static String toRTF( StyledString styledString )
	{
		String result = styledString.str;

		RTFEditorKit rtfKit = new RTFEditorKit();

		XTextPane rtfPane = new XTextPane();

		DefaultStyledDocument rtfDoc = (DefaultStyledDocument)rtfPane.getDoc();

		try
		{
			rtfDoc.insertString(
				0 , styledString.str , new SimpleAttributeSet() );

			rtfPane.setStyleInfo( styledString.styleInfo , 0 );

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

			rtfKit.write( byteOut, rtfDoc, 0, rtfDoc.getLength() );

			result = byteOut.toString();
		}
		catch( Exception e )
		{
		}

		return result;
	}

	/** Convert string containing styled text to HTML.
	 *
	 *	@param		styledString	The styled string.
	 *
	 *	@return						HTML text of object.
	 */

	public static String toHTML( StyledString styledString )
	{
		String result			= styledString.str;

		HTMLEditorKit htmlKit	= new HTMLEditorKit();

		XTextPane htmlPane		= new XTextPane();

		DefaultStyledDocument htmlDoc	=
			(DefaultStyledDocument)htmlPane.getDoc();

		ByteArrayOutputStream byteOut = null;

		try
		{
			htmlDoc.insertString(
				0 , styledString.str , new SimpleAttributeSet() );

			htmlPane.setStyleInfo( styledString.styleInfo , 0 );

			byteOut = new ByteArrayOutputStream();

			htmlKit.write( byteOut, htmlDoc, 0, htmlDoc.getLength() );

			result = byteOut.toString();
		}
		catch( Exception e )
		{
		}
		finally
		{
			try
			{
				if ( byteOut != null ) byteOut.close();
			}
			catch ( Exception e )
			{
			}
		}

		return result;
	}

	/** Writes styled text to a file.
	 *
	 *	@param	file			The file to which to write the styled text.
	 *							The styled string is written as a series of
	 *							bytes to the specified file.
	 *
	 *	@param	styledString	The styled string.
	 *
	 *	@return					True if the file was written successfully,
	 *							false otherwise.
	 *
 	 *	<p>
 	 *	The file format is a byte stream with the following
 	 *	consecutive entries.
 	 *	</p>
 	 *
 	 *	<table summary="Table describing writeFile method">
 	 *		<tr>
 	 *			<td>Header marker</td>
 	 *			<td>Four bytes containing "s", "t", "x", "t".</td>
 	 * 		</tr>
 	 *		<tr>
 	 *			<td>Version number</td>
 	 *			<td>Integer containing version number of Styled String
 	 *				file format.
 	 *			</td>
 	 * 		</tr>
 	 *		<tr>
 	 *			<td>Plain text in UTF-8 format</td>
 	 *			<td>String containing the plain unstyled text.</td>
 	 * 		</tr>
 	 *		<tr>
 	 *			<td>Style Information</td>
 	 *			<td>{@link StyleInfo} encoded as series of {@link StyleRun}
 	 *				entries, preceded by the number of entries.</td>
 	 * 		</tr>
 	 *	</table>
	 */

	 public static boolean writeFile( File file , StyledString styledString )
	 {
	 	boolean result = false;

		try
		{
								// Set up a buffered output stream to
								// which to write the styled string.

			FileOutputStream out	= new FileOutputStream( file );
			DataOutputStream dos	= new DataOutputStream( out );

								// Write marker characters so we know we have
								// our type of styled text file.

			byte[] astMarker = new byte[ 4 ];

			astMarker[ 0 ] = 'a';
			astMarker[ 1 ] = 's';
			astMarker[ 2 ] = 't';
			astMarker[ 3 ] = ' ';

			dos.write( astMarker, 0, 4 );

								// Create file format version number.

			dos.writeInt( 0 );

//$$$PIB$$$ Really wanted to use UTF encoding for generality, but
//			the stupid DataOutputStream only provides for UTF strings up
//			65K bytes long.
//								// Write the plain text in UTF format.
//								// This is more portable and automatically
//								// handles the length of the string.
//
//			dos.writeUTF( styledString.str );

								// Get plain text as series of bytes.

			byte[] strBytes = styledString.str.getBytes();

								// Write the plain text length (in bytes).

			dos.writeInt( strBytes.length );

								// Write the plain text as bytes.

			dos.write( strBytes, 0, strBytes.length );

								// Write the style info.

			styledString.styleInfo.writeExternal( dos );

								// Close the output file.
			dos.flush();
			dos.close();

			result = true;
		}
		catch ( Exception e )
		{
		}

		return result;
	 }

	/** Writes styled text to a file.
	 *
	 *	@param	fileName		The file to which to write the styled text.
	 *
	 *	@param	styledString	The styled string is written as an object stream
	 *							to the specified file.
	 *
	 *	@return					True if the file was written successfully,
	 *							false otherwise.
	 */

	 public static boolean writeFile
	 (
	 	String fileName ,
	 	StyledString styledString
	 )
	 {
	 	return writeFile( new File( fileName ) , styledString );
     }

	/** Reads styled text from a file.
	 *
	 *	@param	fileName	The file to read from.
	 *
	 *	@return				The styled string containing the contents
	 *						of the file.  Null if not a valid styled text
	 *						file.
	 */

	public static StyledString readFile( String fileName )
	{
		return readFile( new File( fileName ) );
	}

	/** Reads styled text from a file.
	 *
	 *	@param	file		The file to read from.
	 *
	 *	@return				The styled string containing the contents
	 *						of the file.  Null if not a valid styled text
	 *						file.
	 */

	public static StyledString readFile( File file )
	{
		StyledString result = null;

		try
		{
			FileInputStream in = new FileInputStream( file );

			DataInputStream dis =
				new DataInputStream(
					new FileInputStream( file ) );

								// Read marker characters.
								// If we don't find the magic marker,
								// this is not our type of styled text file.

			byte[] astMarker	= new byte[ 4 ];

			dis.read( astMarker, 0, 4 );

			String astMarkerStr	= new String( astMarker );

			if ( astMarkerStr.equals( "ast " ) || astMarkerStr.equals("stxt") )
			{
//$$$PIB$$$ See comments above in WriteFile concerning UTF.
//
//								// Read plain text as UTF string.
//
//				String str = dis.readUTF();

    							// Read plain text length in bytes.

				int plainTextLength = dis.readInt();

								// Read plain text as bytes.

				byte[] plainTextBytes = new byte[ plainTextLength ];

				dis.read( plainTextBytes, 0, plainTextLength );

								// Convert bytes to string.

				String str = new String( plainTextBytes );

                                // Read style information.

				StyleInfo styleInfo	= new StyleInfo();

				styleInfo.readExternal( dis );

								// Create styled string result.

				result = new StyledString( str , styleInfo );
			}
								// Close input file.
			dis.close();
		}
		catch ( Exception e )
		{
		}

		return result;
	}

	/** Writes styled text to a file in HTML format.
	 *
	 *	@param	file			The file to which to write the styled text
	 *							in HTML format.
	 *
	 *	@param	styledString	The styled text to write.
	 *
	 *	@param	title			The title for the HTML document.
	 *
	 *	@return					True if the file was written successfully,
	 *							false otherwise.
	 */
/*
	 public static boolean writeFileAsHTML
	 (
	 	File file ,
	 	StyledString styledString ,
	 	String title
	 )
	 {
	 	boolean result	= false;

		Writer output	= null;

		try
		{
			output = new BufferedWriter( new FileWriter( file ) );

			XTextPane textPane	= new XTextPane( styledString );

			StyledTextXHTMLWriter htmlWriter	=
				new StyledTextXHTMLWriter(
					output,
            		(StyledDocument)textPane.getDocument() );

			htmlWriter.setTitle( title );

			htmlWriter.write();

			result = true;
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if ( output != null ) output.close();
			}
			catch ( Exception e )
			{
			}
		}

		return result;
	 }
*/
	/** Writes styled text to a file in HTML format.
	 *
	 *	@param	fileName		The name of the file to which to write
	 *							the styled string in HTML format.
	 *
	 *	@param	styledString	The styled text to write.
	 *
	 *	@param	title			The title for the HTML document.
	 *
	 *	@return				True if the file was written successfully,
	 *						false otherwise.
	 */
/*
	 public static boolean writeFileAsHTML
	 (
	 	String fileName ,
	 	StyledString styledString ,
	 	String title
	 )
	 {
	 	return writeFileAsHTML( new File( fileName ) , styledString , title );
     }
*/
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

