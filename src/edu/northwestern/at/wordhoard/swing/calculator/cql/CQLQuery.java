package edu.northwestern.at.wordhoard.swing.calculator.cql;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;

/**	A WordHoard Corpus Query Language query. */

public class CQLQuery
{
	/**	Types of query results to obtain.
	 */

	public static final int FULLWORDRESULTS	= 0;
	public static final int WORDRESULTS		= 1;
	public static final int WORKRESULTS		= 2;
	public static final int WORKPARTRESULTS	= 3;

	/**	Full word select clause template.
	 */

	protected static final String fullWordSelectClauseTemplate	=
		"  %w ";

	/**	Word select clause template.
	 */

	protected static final String wordSelectClauseTemplate	=
		"   %w.id, " +
		"   %w.tag, " +
		"   %w.work.id, " +
		"   %w.workPart.id, " +
		"   %w.spellingInsensitive, " +
		"   %w.metricalShape, " +
		"	%w.speech.id, " +
		"   %w.prosodic, " +
		"   wordPart%i.partIndex, " +
		"   wordPart%i.lemPos.lemma.tagInsensitive ";

	/** Start of word from clause.
	 */

	protected static final String startOfWordFromClause =
		"    Word word ";

	/** Start of work from clause.
	 */

	protected static final String startOfWorkFromClause =
		"    Work work, WorkPart workPart ";

	/** Start of work part from clause.
	 */

	protected static final String startOfWorkPartFromClause =
		"    Work work , WorkPart workPart ";

	/** From clause template.
	 */

	protected static final String fromClauseTemplate =
		"    , WordPart wordPart%i ";

	/** From word clause template.
	 */

	protected static final String fromWordClauseTemplate =
		"    , Word word%i ";

	/**	Start of where clause for works.
	 */

	protected static final String worksWhereClause	=
		"	 word.work in (:works) ";

	/**	Start of where clause for work parts.
	 */

	protected static final String workPartsWhereClause	=
		"	 word.workPart in (:workParts) ";

	/**	Start of where clause for word tags.
	 */

	protected static final String wordTagsWhereClause	=
		"	 word.tag in (:wordTags) ";

	/**	Where clause template for words.
	 */

	protected static final String whereClauseWordTemplate	=
		"    wordPart%i.word = %w ";

	/**	Where clause template for works and work parts.
	 */

	protected static final String whereClauseWorkPartTemplate	=
		"    workPart.work=work ";

	/**	Work select clause template.
	 */

	protected static final String workSelectClauseTemplate	=
		"   distinct work ";

	/**	Work part select clause template.
	 */

	protected static final String workPartSelectClauseTemplate	=
		"   distinct workPart ";

	/**	Ordered list of query phrases defining a query. */

	protected List queryPhrases	= new ArrayList();

	/**	Create a query. */

	public CQLQuery()
	{
	}

	/**	Create a query from a collection of query phrases.
	 *
	 *	@param	queryPhrases	The query phrases defining the query.
	 */

	public CQLQuery( List queryPhrases )
	{
		if ( queryPhrases != null )
		{
			Iterator iterator	= queryPhrases.iterator();

			while ( iterator.hasNext() )
			{
				Object nextTerm	= iterator.next();

				if ( nextTerm instanceof CQLPhrase )
				{
					queryPhrases.add( (CQLPhrase)nextTerm );
				}
			}
		}
	}

	/**	Create a query from a collection of query phrases.
	 *
	 *	@param	queryPhrases	The query phrases defining the query.
	 */

	public CQLQuery( CQLPhrase[] queryPhrases )
	{
		if ( queryPhrases != null )
		{
			for ( int i = 0 ; i < queryPhrases.length ; i++ )
			{
				this.queryPhrases.add( queryPhrases[ i ] );
			}
		}
	}

	/**	Create a new query from a CQLQueryTokenizer.
	 *
	 *	@param	tokenizer	The CQLQueryTokenizer.  Should be pointing
	 *						at the start of a set of CQLPhrase definitions.
	 *
	 *	@throws
	 *			InvalidCQLQueryException if the CQL syntax is bad.
	 */

	public CQLQuery( CQLQueryTokenizer tokenizer )
		throws InvalidCQLQueryException
	{
								//	Keep getting CQL phrases until no more
								//	are available.

//		tokenizer.setDebug( true );

		CQLPhrase phrase	= new CQLPhrase( tokenizer );

		while ( true )
		{
								//	Got a valid CQL word.  Add it to the
								//	phrase.  The next character should
								//	be the end of query, a "$" indicating
								//	another word follows, or a
								//	"|" indicating the end of the phrase.

			queryPhrases.add( phrase );

			if ( tokenizer.nextToken() == StreamTokenizer.TT_EOF ) break;

			if ( tokenizer.ttype != '|' )
			{
				throw new InvalidCQLQueryException(
					"Bad character found in CQL Query after " +
					phrase.toString() );
			}
								//	Get the next phrase.

			phrase	= new CQLPhrase( tokenizer );
		}
	}

	/**	Create a new query from a query string.
	 *
	 *	@param	queryText		The query text.
	 *
	 *	@throws
	 *			InvalidCQLQueryException if the CQL syntax is bad.
	 */

	public CQLQuery( String queryText )
		throws InvalidCQLQueryException
	{
		this( new CQLQueryTokenizer( queryText ) );
	}

	/**	Get the query phrases for this phrase.
	 *
	 *	@return		The list of query term sets.
	 */

	public List getQueryPhrases()
	{
		return queryPhrases;
	}

	/**	Get Hibernate HQL version of query.
	 *
	 *	@param	selector			A custom selector to add
	 *								to the "where" clause.
	 *								Examples:	word.workPart in (:workParts)
	 *											word.tag in (:wordTags)
	 *											null or blank adds nothing.
	 *
	 *	@param	resultsType			Type of query results to generate.
	 *									WORDRESULTS		: word-level results
	 *									WORKRESULTS		: distinct works
	 *									WORKPARTRESULTS	: distinct work parts
	 *
	 *	@return	hqlQuery			The series of Hibernate HQL queries
	 *								corresponding to this CQL query.
	 *
	 *	<p>
	 *	Each phrase in the query is mapped to a separate HQL query.
	 *	</p>
	 */

	public String[] getHQL( String selector , int resultsType )
	{
								//	Note if the query results are
								//	words or works/work parts.

		boolean doingWords	=
			( resultsType == WORDRESULTS ) ||
			( resultsType == FULLWORDRESULTS );

								//	Each "phrase" in the query becomes
								//	a separate HQL query.

		String[] hqlQueries	= new String[ queryPhrases.size() ];

								//	Loop over phrases.

		for ( int i = 0 ; i < queryPhrases.size() ; i++ )
		{
			        			//	Next phrase in query.

			CQLPhrase phrase		= (CQLPhrase)queryPhrases.get( i );

								//	Words in query.

			CQLWord[] words			=
				(CQLWord[])phrase.getQueryWords().toArray(
					new CQLWord[]{} );

								//	Create "select" portion of HQL.

			StringBuffer sbQuery	= new StringBuffer( "select " );
			StringBuffer sbWord		= new StringBuffer( "word" );

			String[] queryWords		= new String[ words.length ];

			boolean hasAWordPart	= false;

			for ( int j = 0 ; j < words.length ; j++ )
			{
				hasAWordPart	= hasAWordPart || words[ j ].hasWordPart();
			}

			for ( int j = 0 ; j < words.length ; j++ )
			{
				if ( j > 0 )
				{
					sbQuery.append( ", " );
				}

				queryWords[ j ]	= sbWord.toString();

				String s;

				switch ( resultsType )
				{
					case WORKRESULTS	:
						s = workSelectClauseTemplate;
						break;

					case WORKPARTRESULTS:
						s = workPartSelectClauseTemplate;
						break;

					case FULLWORDRESULTS:
						s = fullWordSelectClauseTemplate;
						break;

					case WORDRESULTS	:
					default				:
						s = wordSelectClauseTemplate;
						break;
				}

				s	= StringUtils.replaceAll(
						s , "%w" , queryWords[ j ] );

				s	= StringUtils.replaceAll( s , "%i" , j + "" );

				sbQuery.append( s );

				sbWord.append( ".next" );
			}
								//	Generate start of "from" clause.

			sbQuery.append( " from " );

			boolean startOfWordFromClauseOutput	= false;

			switch ( resultsType )
			{
				case WORKRESULTS	:
					sbQuery.append( startOfWorkFromClause );

					if ( hasAWordPart )
					{
						sbQuery.append( ", " );
						sbQuery.append( startOfWordFromClause );
						startOfWordFromClauseOutput	= true;
					}

					break;

				case WORKPARTRESULTS:
					sbQuery.append( startOfWorkPartFromClause );

					if ( hasAWordPart )
					{
						sbQuery.append( ", " );
						sbQuery.append( startOfWordFromClause );
						startOfWordFromClauseOutput	= true;
					}

					break;

				case FULLWORDRESULTS:
				case WORDRESULTS	:
				default				:
					sbQuery.append( startOfWordFromClause );
					startOfWordFromClauseOutput	= true;
					break;
			}
								//	Generate "from" clause entries
								//	for each word.

			for ( int j = 0 ; j < words.length ; j++ )
			{
//				if ( words[ j ].hasWordPart() )
				if ( words[ j ].hasWordPart() || doingWords )
				{
					String s =
						StringUtils.replaceAll(
							fromClauseTemplate , "%w" , queryWords[ j ] );

					s		= StringUtils.replaceAll( s , "%i" , j + "" );

					sbQuery.append( s );
				}
			}
								//	Generate start of "where" clause.

			sbQuery.append( " where " );

			boolean needsAnd	= false;

			if ( selector != null )
			{
				sbQuery.append( " " );
				sbQuery.append( selector );
				sbQuery.append( " " );

				needsAnd	= true;
            }

            if ( !doingWords )
            {
            	if ( needsAnd ) sbQuery.append( " and " );
            	sbQuery.append( whereClauseWorkPartTemplate );
				needsAnd	= true;

				if ( startOfWordFromClauseOutput )
				{
        	    	if ( needsAnd ) sbQuery.append( " and " );
					sbQuery.append( " word.work = work " );
					needsAnd	= true;
				}
			}
								//	Create "where" clause items defined
								//	by CQL terms.

			for ( int j = 0 ; j < words.length ; j++ )
			{
								//	Output "where" text for this word.

				if ( words[ j ].hasWordPart() || doingWords )
				{
					String s =
						StringUtils.replaceAll(
							whereClauseWordTemplate ,
							"%w" ,
							queryWords[ j ] );

					s		= StringUtils.replaceAll( s , "%i" , j + "" );

					if ( needsAnd )
					{
						sbQuery.append( " and " );
					}

					sbQuery.append( s );

					needsAnd	= true;
				}
								//	Get HQL query part for current word.

				String wordPart	=
					words[ j ].toHQL( queryWords[ j ] , j , doingWords );

								//	If empty parens, output nothing.

				if ( wordPart.length() > 0 )
				{
								//	Separate word definitions by "and".

					if ( needsAnd ) sbQuery.append( " and " );
					needsAnd	= true;

								//	Enclose each word definition in
								//	parentheses.

					sbQuery.append( "(" );

					sbQuery.append( wordPart );

								//	Add closing parenthesis.

					sbQuery.append( ")" );
				}
			}

			hqlQueries[ i ] 	= sbQuery.toString();
		}

		return hqlQueries;
	}

	/**	Get Hibernate HQL version of query.
	 *
	 *	@return	hqlQuery			The series of Hibernate HQL queries
	 *								corresponding to this CQL query.
	 *								All queries are generated as word
	 *								queries.
	 *
	 *	<p>
	 *	Each phrase in the query is mapped to a separate HQL query.
	 *	</p>
	 */

	public String[] getHQL()
	{
		return getHQL( null , WORDRESULTS );
	}

	/**	Displayable version of query.
	 */

	public String toString()
	{
		StringBuffer sb	= new StringBuffer();

		for ( int i = 0 ; i < queryPhrases.size() ; i++ )
		{
			if ( i > 0 ) sb.append( "|" );

			sb.append( ((CQLPhrase)queryPhrases.get( i )).toString() );
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

