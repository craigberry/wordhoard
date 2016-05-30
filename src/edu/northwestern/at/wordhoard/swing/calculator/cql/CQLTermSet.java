package edu.northwestern.at.wordhoard.swing.calculator.cql;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

/**	A WordHoard Corpus Query Language term set. */

public class CQLTermSet
{
	/**	The search terms to match for a given word. */

	protected Set queryTerms	= new LinkedHashSet();

	/**	Create a search term set. */

	public CQLTermSet()
	{
	}

	/**	Create a new term set from a CQLQueryTokenizer.
	 *
	 *	@param	tokenizer	The CQLQueryTokenizer.  Should be pointing
	 *						at the start of a set of CQLTerm definitions.
	 *
	 *	@throws
	 *			InvalidCQLQueryException if the CQL syntax is bad.
	 */

	public CQLTermSet( CQLQueryTokenizer tokenizer )
		throws InvalidCQLQueryException
	{
								//	Keep getting CQL terms until no more
								//	are available.

		CQLTerm	term	= new CQLTerm( tokenizer );

		while ( true )
		{
								//	Got a valid CQL Term.  Add it to the
								//	term set.  The next character should
								//	be the end of query, a ":" indicating
								//	another query term follows, or a
								//	";" indicating the end of the term set.

			queryTerms.add( term );

			if ( tokenizer.nextToken() == StreamTokenizer.TT_EOF ) break;

			if	(	( tokenizer.ttype == ';' ) ||
					( tokenizer.ttype == '$' ) ||
					( tokenizer.ttype == '|' ) )
			{
				tokenizer.pushBack();
				break;
			}

			if ( tokenizer.ttype != ':' )
			{
				throw new InvalidCQLQueryException(
					"CQLTermSet: Bad character found in CQL Query after " +
					term.toString() );
			}
                				//	Get the next term.

			term	= new CQLTerm( tokenizer );
		}
	}

	/**	Create a query term set from a collection of query terms.
	 *
	 *	@param	queryTerms	The query terms defining the query term set.
	 */

	public CQLTermSet( Collection queryTerms )
	{
		if ( queryTerms != null )
		{
			Iterator iterator	= queryTerms.iterator();

			while ( iterator.hasNext() )
			{
				Object nextTerm	= iterator.next();

				if ( nextTerm instanceof CQLTerm )
				{
					queryTerms.add( (CQLTerm)nextTerm );
				}
			}
		}
	}

	/**	Create a query term set from a collection of query terms.
	 *
	 *	@param	queryTerms	The query terms defining the query term set.
	 */

	public CQLTermSet( CQLTerm[] queryTerms )
	{
		if ( queryTerms != null )
		{
			for ( int i = 0 ; i < queryTerms.length ; i++ )
			{
				this.queryTerms.add( queryTerms[ i ] );
			}
		}
	}

	/**	Get the query terms for this term set.
	 *
	 *	@return		The list of query terms.
	 */

	public Collection getQueryTerms()
	{
		return queryTerms;
	}

	/**	Displayable version of term set.
	 */

	public String toString()
	{
		StringBuffer sb	= new StringBuffer();

		Iterator iterator	= queryTerms.iterator();

		while ( iterator.hasNext() )
		{
			if ( sb.length() > 0 ) sb.append( ":" );

			sb.append( ((CQLTerm)iterator.next()).toString() );
		}

		return sb.toString();
	}

	/**	Get HQL version of query term set.
	 *
	 *	@param	queryField		The name of field to which this term set applies.
	 *	@param	fieldIndexBogus	The index of this field in the select clause.
	 *	@param	doingWords		true if word results, false for work part results.
	 *
	 *	@return					HQL version of query term set for specified field.
	 */

	public String toHQL
	(
		String queryField ,
		int fieldIndexBogus ,
		boolean doingWords
	)
	{
		StringBuffer sb	= new StringBuffer();

		Iterator iterator	= queryTerms.iterator();

		int fieldIndex		= 0;

		while ( iterator.hasNext() )
		{
			String term	=
				((CQLTerm)iterator.next()).toHQL(
					queryField , fieldIndexBogus , doingWords );

			if ( term.length() > 0 )
			{
				if ( fieldIndex > 0 ) sb.append( " and " );

				sb.append( term );
			}

			fieldIndex++;
		}
								//	Enclose generated terms in parentheses.

		if ( sb.length() > 0 )
		{
			sb.insert( 0 , "(" );
			sb.append( ")" );
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

		Iterator iterator	= queryTerms.iterator();

		while ( iterator.hasNext() )
		{
			result	= result || ((CQLTerm)iterator.next()).hasSpeaker();
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

		Iterator iterator	= queryTerms.iterator();

		while ( iterator.hasNext() )
		{
			result	= result || ((CQLTerm)iterator.next()).hasSpeech();
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

		Iterator iterator	= queryTerms.iterator();

		while ( iterator.hasNext() )
		{
			result	= result || ((CQLTerm)iterator.next()).hasWordPart();
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

