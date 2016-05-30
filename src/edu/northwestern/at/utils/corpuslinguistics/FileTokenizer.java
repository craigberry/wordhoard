package edu.northwestern.at.utils.corpuslinguistics;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.StringTokenizer;

/**	Tokenize a text file.
 */

public class FileTokenizer
{
	protected BufferedReader input		= null;
	protected StringTokenizer tokenizer	= null;
	protected String nextToken			= null;
	protected Pretokenizer pretokenizer	= null;

	/**	Create file tokenizer.
	 *
	 *	@param	file			Input file.
	 *	@param	encoding		Input file text encoding (e.g., "utf-8").
	 *	@param	pretokenizer	The pretokenizer for each input line.
	 *							DefaultPretokenizer is used if null.
	 *
	 *	@throws IOException
	 *						if input file can't be read.
	 */

	public FileTokenizer
	(
		File file ,
		String encoding ,
		Pretokenizer pretokenizer
	)
		throws IOException
	{
		this.pretokenizer	= pretokenizer;

		String safeEncoding	= ( encoding == null ) ? "" : encoding;

		if ( safeEncoding.length() > 0 )
		{
			input	=
				new BufferedReader(
					new InputStreamReader(
						new FileInputStream( file ) , safeEncoding ) );
		}
		else
		{
			input	=
				new BufferedReader(
					new InputStreamReader( new FileInputStream( file ) ) );
		}

		do
		{
			String line	= input.readLine();

			tokenizer	=
				new StringTokenizer( pretokenizer.pretokenize( line ) );
		}
		while ( !tokenizer.hasMoreTokens() );

		nextToken	= tokenizer.nextToken();
	}

	/**	Create file tokenizer.
	 *
	 *	@param	file		Input file.
	 *
	 *	@throws IOException
	 *						if input file can't be read.
	 */

	public FileTokenizer( File file )
		throws IOException
	{
		this( file , null , new DefaultPretokenizer() );
	}

	/**	Create file tokenizer.
	 *
	 *	@param	fileName	Name of the input file.
	 *	@param	encoding	Encoding (e.g., "utf-8").
	 *
	 *	@throws IOException
	 *						if input file can't be read.
	 */

	public FileTokenizer( String fileName , String encoding )
		throws IOException
	{
		this( new File( fileName ) , encoding , new DefaultPretokenizer() );
	}

	/**	Create file tokenizer.
	 *
	 *	@param	fileName	Name of the input file.
	 *
	 *	@throws IOException
	 *						if input file can't be read.
	 */

	public FileTokenizer( String fileName )
		throws IOException
	{
		this( new File( fileName ) , null , new DefaultPretokenizer() );
	}

	/**	Get the next token.
	 *
	 *	@return		Next available token, or null at the end of the file.
	 */

	public String getNextToken()
	{
		String retval = nextToken;

		if ( tokenizer.hasMoreTokens() )
		{	// more available on this line
			nextToken = tokenizer.nextToken();
		}
		else
		{	// read the next line
			try
			{
				nextToken = null;

				String line = input.readLine();

				while ( ( line != null ) && ( nextToken == null ) )
				{
					if ( line != null )
					{
						tokenizer =
							new StringTokenizer
							(
								pretokenizer.pretokenize( line )
							);

						if ( tokenizer.hasMoreTokens() )
						{
							nextToken = tokenizer.nextToken();
						}
						else
						{
							line = input.readLine();
						}
					}
					else
					{	// end of file reached
						input.close();
					}
				}
			}
			catch ( IOException exc )
			{
				System.err.println( "FileTokenizer: " + exc );
			}
		}

		return retval;
	}

	/**	Check if more tokens are available.
	 *
	 *	@return		true if more tokens are available, false if not.
	 */

	public boolean hasMoreTokens()
	{
		return ( nextToken != null );
	}

	/**	Close input file once tokenization is complete.
	 */

	public void close()
		throws IOException
	{
		input.close();
		input = null;
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

