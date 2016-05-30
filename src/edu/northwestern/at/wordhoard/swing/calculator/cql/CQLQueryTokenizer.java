package edu.northwestern.at.wordhoard.swing.calculator.cql;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

/**	A tokenizer for parsing a WordHoard Corpus Query Language query. */

public class CQLQueryTokenizer extends StreamTokenizer
{
	/**	True to write debugging information to System.out, false otherwise. */

//	protected boolean debug	= true;
	protected boolean debug	= false;

	/**	Create a CQL query tokenizer.
	 *
	 *	@param	queryString	The query string.
	 */

	public CQLQueryTokenizer( String queryString )
	{
								//	Enable a stream tokenizer over the
								//	input query string.

		super( new BufferedReader( new StringReader( queryString ) ) );

								//	End of lines are not significant and
								//	are treated as ordinary whitespace.

		eolIsSignificant( false );
	}

	/**	Get next token.
	 *
	 *	@return		The token type.  I/O errors return TT_EOF, indicating
	 *				the end of file.
	 */

	 public int nextToken()
	 {
	 	int result;

	 	try
	 	{
	 		result	= super.nextToken();
	 	}
	 	catch ( IOException ioe )
	 	{
	 		result	= StreamTokenizer.TT_EOF;
	 	}
								//	Display debugging information if
								//	requested.
		if ( debug )
		{
			System.out.println( this.toString() );
		}

	 	return result;
	 }

	/**	Set debug flag.
	 *
	 *	@param	debug 	True to write debugging information, false otherwise.
	 */

	public void setDebug( boolean debug )
	{
		this.debug	= debug;
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

