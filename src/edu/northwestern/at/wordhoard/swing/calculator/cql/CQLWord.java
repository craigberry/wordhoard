package edu.northwestern.at.wordhoard.swing.calculator.cql;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

/**	A WordHoard Corpus Query Language word position. */

public class CQLWord
{
	/**	The query term sets for matching for a given word position. */

	protected Set queryTermSets	= new LinkedHashSet();

	/**	Create a query word. */

	public CQLWord()
	{
	}

	/**	Create a query word from a collection of query term sets.
	 *
	 *	@param	queryTermSets	The query term sets defining
	 *							the query word.
	 */

	public CQLWord( Collection queryTermSets )
	{
		if ( queryTermSets != null )
		{
			Iterator iterator	= queryTermSets.iterator();

			while ( iterator.hasNext() )
			{
				Object nextTerm	= iterator.next();

				if ( nextTerm instanceof CQLTermSet )
				{
					queryTermSets.add( (CQLTermSet)nextTerm );
				}
			}
		}
	}

	/**	Create a query word from a collection of query term sets.
	 *
	 *	@param	queryTermSets	The query term sets defining the
	 *							query word.
	 */

	public CQLWord( CQLTermSet[] queryTermSets )
	{
		if ( queryTermSets != null )
		{
			for ( int i = 0 ; i < queryTermSets.length ; i++ )
			{
				this.queryTermSets.add( queryTermSets[ i ] );
			}
		}
	}

	/**	Create a new word from a CQLQueryTokenizer.
	 *
	 *	@param	tokenizer	The CQLQueryTokenizer.  Should be pointing
	 *						at the start of a set of CQLTerm definitions.
	 *
	 *	@throws
	 *			InvalidCQLQueryException if the CQL syntax is bad.
	 */

	public CQLWord( CQLQueryTokenizer tokenizer )
		throws InvalidCQLQueryException
	{
								//	Keep getting CQL term sets until no more
								//	are available.

		CQLTermSet termSet	= new CQLTermSet( tokenizer );

		while ( true )
		{
								//	Got a valid CQL Term set.  Add it to the
								//	word.  The next character should
								//	be the end of query, a ";" indicating
								//	another term set follows, or a
								//	"$" indicating the end of the word.

			queryTermSets.add( termSet );

			if ( tokenizer.nextToken() == StreamTokenizer.TT_EOF ) break;

			if ( ( tokenizer.ttype == '$' ) || ( tokenizer.ttype == '|' ) )
			{
				tokenizer.pushBack();
				break;
			}

			if ( tokenizer.ttype != ';' )
			{
				CQLSyntaxError.badElement
				(
					tokenizer ,
					"CQL Word" ,
					termSet.toString()
				);
			}
								//	Get the next term set.

			termSet	= new CQLTermSet( tokenizer );
		}
	}

	/**	Get the query term set for this word.
	 *
	 *	@return		The list of query term sets.
	 */

	public Collection getQueryTermSets()
	{
		return queryTermSets;
	}

	/**	Get HQL version of query term set.
	 *
	 *	@param	queryField	The name of field to which this word applies.
	 *	@param	fieldIndex	The index of this field in the select clause.
	 *	@param	doingWords	true if word results, false for work part results.
	 *
	 *	@return				HQL version of query word for specified field.
	 */

	public String toHQL
	(
		String queryField ,
		int fieldIndex ,
		boolean doingWords
	)
	{
		StringBuffer sb	= new StringBuffer();

		Iterator iterator	= queryTermSets.iterator();

		boolean firstTime	= true;

		while ( iterator.hasNext() )
		{
			String word	=
				((CQLTermSet)iterator.next()).toHQL(
					queryField , fieldIndex , doingWords );

			if ( word.length() > 0 )
			{
				if ( !firstTime ) sb.append( " or " );

    	        firstTime	= false;

				sb.append( word );
			}
		}
								//	Enclose generated word in parentheses.

		if ( sb.length() > 0 )
		{
			sb.insert( 0 , "(" );
			sb.append( ")" );
		}

		return sb.toString();
    }

	/**	Get string version of query word for display.
	 *
	 *	@return		String version of query word.
	 */

	public String toString()
	{
		StringBuffer sb	= new StringBuffer();

		Iterator iterator	= queryTermSets.iterator();

		while ( iterator.hasNext() )
		{
			if ( sb.length() > 0 ) sb.append( ";" );

			sb.append( ((CQLTermSet)iterator.next()).toString() );
		}

		return sb.toString();
	}

	/**	Determine if any constituent query term refers to a speaker.
	 *
	 *	@return		true if a constituent term refers to a speaker.
	 */

	public boolean hasSpeaker()
	{
		boolean result	= false;

		Iterator iterator	= queryTermSets.iterator();

		while ( iterator.hasNext() )
		{
			result	= result || ((CQLTermSet)iterator.next()).hasSpeaker();
			if ( result ) break;
		}

		return result;
	}

	/**	Determine if any constituent query term refers to a speech.
	 *
	 *	@return		true if a constituent term refers to a speech.
	 */

	public boolean hasSpeech()
	{
		boolean result	= false;

		Iterator iterator	= queryTermSets.iterator();

		while ( iterator.hasNext() )
		{
			result	= result || ((CQLTermSet)iterator.next()).hasSpeech();
			if ( result ) break;
		}

		return result;
	}

	/**	Determine if any constituent query term refers to a word part.
	 *
	 *	@return		true if a constituent term refers to a word part.
	 */

	public boolean hasWordPart()
	{
		boolean result	= false;

		Iterator iterator	= queryTermSets.iterator();

		while ( iterator.hasNext() )
		{
			result	= result || ((CQLTermSet)iterator.next()).hasWordPart();
			if ( result ) break;
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

