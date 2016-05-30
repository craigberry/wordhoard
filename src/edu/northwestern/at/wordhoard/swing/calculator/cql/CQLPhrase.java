package edu.northwestern.at.wordhoard.swing.calculator.cql;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

/**	A WordHoard Corpus Query Language phrase. */

public class CQLPhrase
{
	/**	Ordered list of query words defining a query phrase. */

	protected List queryWords	= new ArrayList();

	/**	Create a query word. */

	public CQLPhrase()
	{
	}

	/**	Create a query phrase from a collection of query words.
	 *
	 *	@param	queryWords	The query words defining the phrase.
	 */

	public CQLPhrase( List queryWords )
	{
		if ( queryWords != null )
		{
			Iterator iterator	= queryWords.iterator();

			while ( iterator.hasNext() )
			{
				Object nextTerm	= iterator.next();

				if ( nextTerm instanceof CQLWord )
				{
					queryWords.add( (CQLWord)nextTerm );
				}
			}
		}
	}

	/**	Create a query phrase from a collection of query words.
	 *
	 *	@param	queryWords	The query words defining the query phrase.
	 */

	public CQLPhrase( CQLWord[] queryWords )
	{
		if ( queryWords != null )
		{
			for ( int i = 0 ; i < queryWords.length ; i++ )
			{
				this.queryWords.add( queryWords[ i ] );
			}
		}
	}

	/**	Create a new phrase from a CQLQueryTokenizer.
	 *
	 *	@param	tokenizer	The CQLQueryTokenizer.  Should be pointing
	 *						at the start of a set of CQLWord definitions.
	 *
	 *	@throws
	 *			InvalidCQLQueryException if the CQL syntax is bad.
	 */

	public CQLPhrase( CQLQueryTokenizer tokenizer )
		throws InvalidCQLQueryException
	{
								//	Keep getting CQL words until no more
								//	are available.

		CQLWord word	= new CQLWord( tokenizer );

		while ( true )
		{
								//	Got a valid CQL word.  Add it to the
								//	phrase.  The next character should
								//	be the end of query, a "$" indicating
								//	another word follows, or a
								//	"|" indicating the end of the phrase.

			queryWords.add( word );

			if ( tokenizer.nextToken() == StreamTokenizer.TT_EOF ) break;

			if ( tokenizer.ttype == '|' )
			{
				tokenizer.pushBack();
				break;
			}

			if ( tokenizer.ttype != '$' )
			{
				CQLSyntaxError.badElement
				(
					tokenizer ,
					"CQL Phrase" ,
					word.toString()
				);
			}
								//	Get the next word.

			word	= new CQLWord( tokenizer );
		}
	}

	/**	Get the query words for this phrase.
	 *
	 *	@return		The list of query term sets.
	 */

	public List getQueryWords()
	{
		return queryWords;
	}

	/**	Displayable version of phrase.
	 */

	public String toString()
	{
		StringBuffer sb	= new StringBuffer();

		for ( int i = 0 ; i < queryWords.size() ; i++ )
		{
			if ( i > 0 ) sb.append( "$" );

			sb.append( ((CQLWord)queryWords.get( i )).toString() );
		}

		return sb.toString();
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

