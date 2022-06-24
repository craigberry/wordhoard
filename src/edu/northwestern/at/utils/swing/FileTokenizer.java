package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.text.*;
import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.FileUtils;

/** Tokenizes text from a text file.
 *
 *	<p>
 *	A token is defined as text between word separator characters.
 *	The separator characters are defined below in the
 *	WORD_SEPARATOR_CHARACTERS array.  The tokenizer keeps track of the
 *	starting and ending position of each token.  This is necessary to
 *	support find/replace, spell checking, etc.
 *	</p>
 *
 *	<p>
 *	FileTokenizer implements the Iterator interface, but the
 *	optional remove() method is left as a no-op since there is no collection
 *	underlying this class.
 *	</p>
 *
 *	<p>
 *	<strong>Example:</strong>
 *	</p>
 *
 *	<p>
 *	<code>
 *		// Tokenize file text and print out list of words.
 *
 *			FileTokenizer tokenizer =
 *				new FileTokenizer( "myfile.txt" );
 *
 *								// While there are more characters
 *								// we haven't looked at ...
 *
 *			while ( tokenizer.hasNext() )
 *			{
 *								// Extract next word in document.
 *
 *				String word = tokenizer.next();
 *
 *								// Print out word and its starting and
 *								// ending positions in the document text.
 *
 *				System.out.println(
 *					word +
 *					" starts at " + tokenizer.getStartPos() +
 *					", ends at " + tokenizer.getEndPos() );
 *			}
 *	</code>
 *	</p>
 */

public class FileTokenizer
	implements Iterator
{
	/** The document to tokenize. */

	protected Document document;

	/** The current document segment. */

	protected Segment segment;

	/** Starting position in document. */

	protected int startPos;

	/** Ending position in document. */

	protected int endPos;

	/** Current position in document. */

	protected int currentPos;

	/** Characters that separate words.
	 *
	 *	<p>
	 *	The single quote is not included as a word separator
	 *	so that contractions can be picked up.  It is up to
	 *	the invoker to remove unwanted single quotes from a token.
	 *	Likewise a "-" is not considered a separator so that
	 *	words containing a dash can be worked with.
	 *	</p>
	 */

	public static final char[] WORD_SEPARATOR_CHARACTERS =
	{	' ', '\t', '\n',
		'\r', '\f', '.', ',', ':', ';', '(', ')', '[', ']', '{',
		'}', '<', '>', '/', '|', '\\', '\"', '?', '!'
	};

	public static final String WORD_SEPARATOR_CHARACTERS_STRING =
		" \t\n\r\f.,:;()[]{}<>/|\\\"?!";

	/** Hash holds separator characters for quick access. */

	protected static HashMap separatorHashMap = new HashMap();

	/** Create document tokenizer.
	 *
	 *	@param	textFileName	Name of text file to tokenize.
	 */

	public FileTokenizer( String textFileName )
		throws IOException, BadLocationException
	{
		String documentText	= FileUtils.readTextFile( textFileName );

		this.document		= new PlainDocument();

		document.insertString( 0 , documentText , null );

		this.segment	= new Segment();

		setPosition( 0 );
	}

	/** Checks if a character is a word separator.
	 *
	 *	@param	ch	The character to check.
	 *
	 *	@return		True if the character is a word separator.
	 *
	 *	<p>
	 *	Tests is a character is a separator by checking if the
	 *	character is a key in the separatorHaspMap map.  If so,
	 *	the character is a separator.
	 *	</p>
	 */

	public static boolean isSeparator( char ch )
	{
		return separatorHashMap.containsKey( new Character( ch ) );
	}

	/** Creates word separator hash map from list of separator characters.
	 *
	 *	<p>
	 *	The separatorHashMap map uses each separator character as both a
	 *	key and the key's value.
	 *	</p>
	 */

	protected static void createSeparatorHashMap()
	{
		for ( int i = 0; i < WORD_SEPARATOR_CHARACTERS.length; i++ )
		{
			Character cCh	= new Character( WORD_SEPARATOR_CHARACTERS[ i ] );
			separatorHashMap.put( cCh  , cCh );
		}
	}

	/** Move to start of next word if current cursor is in the middle
	 *	of a word.
	 */

	public void moveToStartOfWord()
	{
		try
		{
			while ( hasNext() )
			{
				document.getText( currentPos , 1 , segment );

				char ch = segment.array[ segment.offset ];

				if ( isSeparator( ch ) )
				{
					startPos	= currentPos;
					break;
				}

				currentPos++;
			}
		}
		catch ( BadLocationException ex )
		{
			currentPos	= document.getLength();
		}
	}

	/** Check if more characters available in document.
	 *
	 *	@return		True if more characters in document.
	 */

	public boolean hasNext()
	{
		return ( currentPos < document.getLength() );
	}

	/** Get next token in document.
	 *
	 *	@return		Next token in document as a string.
	 */

	public Object next()
	{
		StringBuffer s	= new StringBuffer();

		try
		{
								// Trim leading separators.

			while ( hasNext() )
			{
				document.getText( currentPos , 1 , segment );

				char ch	= segment.array[ segment.offset ];

				if ( !isSeparator( ch ) )
				{
					startPos	= currentPos;
					break;
				}

				currentPos++;
			}
								// Append characters until
								// a separator found.

			while ( hasNext() )
			{
				document.getText( currentPos , 1 , segment );

				char ch = segment.array[ segment.offset ];

				if ( isSeparator( ch ) )
				{
					endPos	= currentPos;
					break;
				}

				s.append( ch );

				currentPos++;
			}
		}
		catch ( BadLocationException ex )
		{
			currentPos	= document.getLength();
		}

		return s.toString();
	}

	/** Removes last element returned by iterator (does nothing). */

	public void remove()
	{
	}

	/** Get starting position in document for tokenization.
	 *
	 *	@return		The starting position.
	 */

	public int getStartPos()
	{
		return startPos;
	}

	/** Get ending position in document for tokenization.
	 *
	 *	@return		The ending position.
	 */

	public int getEndPos()
	{
		return endPos;
	}

	/** Set position in document for tokenization.
	 *
	 *	@param	pos		The position.
	 */

	public void setPosition( int pos )
	{
		startPos	= pos;
		endPos		= pos;
		currentPos	= pos;
	}

	/** Create separator hash map that can be accessed statically. */

	static
	{
		createSeparatorHashMap();
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

