package edu.northwestern.at.wordhoard.swing.calculator.cql;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	A WordHoard Corpus Query Language term.
 *
 *	<p>
 *	The general form of a query term is:
 *	</p>
 *
 *	<blockquote>
 *	<p>
 *	[!]func({text})
 *	</p>
 *	</blockquote>
 *
 *  <p>
 *	<strong>!</strong>means "do not match the text defined by this term."
 *	By default, the text will be matched.
 *	</p>
 *
 *  <p>
 *	<strong>func</strong> is the term type.  Example: spe means spelling.
 *	</p>
 *
 *  <p>
 *	<strong>text</strong> is the term text.  If enclosed in braces, it
 *	is assumed to be a regular expression.  The term text need not be
 *	enclosed in quotes unless it contains blanks or special characters.
 *	</p>
 *
 *  <p>
 *	Examples:
 *	</p>
 *
 *	<ul>
 *	<li>spe(think) means match the spelling "think".</li>
 *	<li>lem("think (v)") means match the lemma "think (v)".</li>
 *	<li>pos(v) means match a verb.</li>
 *	<li>!spe({^bu}) means match all words except those starting with "bu".</li>
 *	</ul>
 */

public class CQLTerm
{
	/**	The query term types. */

	public static final int SPELLING				= 0;
	public static final int LEMMA					= 1;
	public static final int POS						= 2;
	public static final int WORDCLASS				= 3;
	public static final int MAJORWORDCLASS			= 4;
	public static final int SPEAKER					= 5;
	public static final int SPEAKERGENDER			= 6;
	public static final int SEMANTICTAGS			= 7;
	public static final int ISVERSE					= 8;
	public static final int METRICALSHAPE			= 9;
	public static final int SPEAKERMORTALITY		= 10;
	public static final int WORDPARTCOUNT			= 11;
	public static final int AUTHORNAME				= 12;
	public static final int AUTHORCONTEMPORARY		= 13;
	public static final int CORPUSTAG				= 14;
	public static final int CORPUSTITLE				= 15;
	public static final int WORKFULLTITLE			= 16;
	public static final int WORKSHORTTITLE			= 17;
	public static final int WORKTAG					= 18;
	public static final int WORKPARTFULLTITLE		= 19;
	public static final int WORKPARTSHORTTITLE		= 20;
	public static final int WORKPARTTAG				= 21;
	public static final int PUBLICATIONYEAREARLY	= 22;
	public static final int PUBLICATIONYEARLATE		= 23;
	public static final int NARRATIVE				= 24;

	/**	The query term text. */

	protected String termText	= "";

	/**	The type of query term. */

	protected int termType		= SPELLING;

	/**	True to match the query text, false to not match it. */

	protected boolean matchTerm	= true;

	/**	True if query text is a regular expression. */

	protected boolean isRegExp	= false;

	/**	Maps term types to CQL names.
	 */

	protected static final HashMap elementsToNames;

	/**	Maps term types to HQL names.
	 */

	protected static final HashMap elementsToHQLNames;

	/**	Maps CQL names to term types.
	 */

	protected static final HashMap namesToElements;

	/**	True if element value can contain operator.
	 */

	protected static final HashMap allowsOperators;

	/**	Object model data values. */

	protected static String	gender_male			=
		Gender.MALE + "";

	protected static String	gender_female		=
		Gender.FEMALE + "";

	protected static String	gender_uncertain	=
		Gender.UNCERTAIN_MIXED_OR_UNKNOWN + "";

	protected static String	mortality_mortal	=
		Mortality.MORTAL + "";

	protected static String	mortality_immortal	=
		Mortality.IMMORTAL_OR_SUPERNATURAL + "";

	protected static String	mortality_uncertain	=
		Mortality.UNKNOWN_OR_OTHER + "";

	protected static String	prosodic_prose	=
		Prosodic.PROSE + "";

	protected static String	prosodic_verse	=
		Prosodic.VERSE + "";

	protected static String	prosodic_unknown	=
		Prosodic.UNKNOWN + "";

	/**	Create new term type.
	 */

	public CQLTerm()
	{
	}

	/**	Create a new term type.
	 *
	 *	@param	termText	The query term text.
	 *	@param	termType	The query term type.
	 *	@param	matchTerm	True to match term, false to not match.
	 *	@param	isRegExp	True if term text is regular expression.
	 */

	public CQLTerm
	(
		String termText ,
		int termType ,
		boolean matchTerm ,
		boolean isRegExp
	)
	{
		this.termText	= termText;
		this.termType 	= termType;
		this.matchTerm 	= matchTerm;
		this.isRegExp	= isRegExp;
	}

	/**	Create a new term type from a CQLQueryTokenizer.
	 *
	 *	@param	tokenizer	The CQLQueryTokenizer.  Should be pointing
	 *						at the start of a CQLTerm definition.
	 *
	 *	@throws
	 *			InvalidCQLQueryException if the CQL term syntax is bad.
	 */

	public CQLTerm( CQLQueryTokenizer tokenizer )
		throws InvalidCQLQueryException
	{
								//	Initialize default.  A null termText
								//	will indicate we did not find a
								//	CQL term definition.

		termText	= null;
								//	If next token is end of file,
								//	there is no term to extract.

		if ( tokenizer.nextToken() == StreamTokenizer.TT_EOF ) return;

								//	Is the token a "!"?
								//	Set the "don't match" flag and pick
								//	up the next token.

		if ( tokenizer.ttype == '!' )
		{
			matchTerm	= false;
			if ( tokenizer.nextToken() == StreamTokenizer.TT_EOF ) return;
		}
								//	The token should be a term type
								//	or a spelling.  We distinguish these
								//	two cases as follows.  If this token
								//	is quoted text, it is match text.
								//	If this token is not quoted, we look
								//	at the next  token.  If it is a "(",
								//	this token is a term type name.
								//	If the next token is not a "(",
								//	this token is match text.

		String sval	= tokenizer.sval;

								//	First look for quoted text.  We have
								//	match text if so, and the type is
								//	spelling.

		if ( ( tokenizer.ttype == '\'' ) || ( tokenizer.ttype == '"' ) )
		{
			termText	= sval;
			termType	= SPELLING;
		}
								//	If we have a word, could be either
								//	a term type or match text.

		else if ( tokenizer.ttype == StreamTokenizer.TT_WORD )
		{
								//	Save the current word.

			String savedText	= sval;

								//	Pick up the next token.  If it is the
								//	end of the text, the current word
								//	is spelling match text.

			if ( tokenizer.nextToken() == StreamTokenizer.TT_EOF )
			{
				termText	= sval;
				termType	= SPELLING;
				return;
			}
								//	Next token is left paren.  The
								//	previous word should be a term type.
								//	See if the word appears in the list
								//	of valid term types.  Error if not.

			else if ( tokenizer.ttype == '(' )
			{
				Integer termTypeI	=
					(Integer)namesToElements.get( savedText );

				if ( termTypeI == null )
				{
					CQLSyntaxError.badTermType
					(
						tokenizer ,
						"CQL Term" ,
						savedText
					);
				}
				else
				{
					termType	= termTypeI.intValue();
				}
								//	The match text should come next.
								//	Error if we hit the end of the input.

				if ( tokenizer.nextToken() == StreamTokenizer.TT_EOF )
				{
					CQLSyntaxError.termEndsTooSoon
					(
						tokenizer ,
						"CQL Term" ,
						tokenizer.sval
					);
				}
								//	We should have quoted text, a plain word,
								//	or a right parenthesis.  A right parenthesis
								//	indicates empty match text.

				if ( tokenizer.ttype == StreamTokenizer.TT_WORD )
				{
					termText	= tokenizer.sval;
					tokenizer.nextToken();
				}
				else if ( tokenizer.ttype == StreamTokenizer.TT_NUMBER )
				{
					termText	= new Double( tokenizer.nval ).toString();
					tokenizer.nextToken();
				}
				else if	(	( tokenizer.ttype == '\'' ) ||
							( tokenizer.ttype == '"' ) )
				{
					termText	= tokenizer.sval;
					tokenizer.nextToken();
				}

				if ( tokenizer.ttype != ')' )
				{
					CQLSyntaxError.termEndsTooSoon
					(
						tokenizer ,
						"CQL Term" ,
						termText
					);
				}
				                //	See if text is regular expression.

				if ( termText != null )
				{
					int l		= termText.length() - 1;

					isRegExp	=
						( termText.charAt( 0 ) == '{' ) &&
						( termText.charAt( l ) == '}' );

					if ( isRegExp )
					{
						termText	= termText.substring( 1 , l );
					}
				}
			}
								//	Perform any needed fixups to the
								//	term text.

			validateTermText( tokenizer );
		}
								//	We found something bogus.
		else
		{
			CQLSyntaxError.badTermType
			(
				tokenizer ,
				"CQL Term" ,
				sval
			);
		}
	}

	/**	Fix up query arguments as needed to match database values.
	 *
	 *	@param	tokenizer	 CQLQueryTokenizer.
	 *
	 *	@throws
	 *			InvalidCQLQueryException if the CQL term syntax is bad.
	 */

	protected void validateTermText( CQLQueryTokenizer tokenizer )
	{
		switch ( termType )
		{
			case SPEAKERGENDER 	:
				if ( termText.length() == 0 )
				{
					CQLSyntaxError.badTermType
					(
						tokenizer ,
						"CQL Term" ,
						termText
					);
				}

				if ( isRegExp )
				{
					termText	=
						StringUtils.replaceAll(
							termText , "m" , gender_male );

					termText	=
						StringUtils.replaceAll(
							termText , "M" , gender_male );

					termText	=
						StringUtils.replaceAll(
							termText , "f" , gender_female );

					termText	=
						StringUtils.replaceAll(
							termText , "F" , gender_female );

					termText	=
						StringUtils.replaceAll(
							termText , "u" ,  gender_uncertain + "" );

					termText	=
						StringUtils.replaceAll(
							termText , "U" ,  gender_uncertain + "" );
				}
				else
				{
					String termTextLower	= termText.toLowerCase();

					if ( termTextLower.equals( "m" ) )
					{
						termText	= gender_male;
					}
					else if ( termTextLower.equals( "f" ) )
					{
						termText	= gender_female;
					}
					else if ( termTextLower.equals( "u" ) )
					{
						termText	= gender_uncertain;
					}
					else
					{
						CQLSyntaxError.badTermType
						(
							tokenizer ,
							"CQL Term" ,
							termText
						);
					}
                }

				break;

			case SPEAKERMORTALITY :
				if ( termText.length() == 0 )
				{
					CQLSyntaxError.badTermType
					(
						tokenizer ,
						"CQL Term" ,
						termText
					);
				}

				if ( isRegExp )
				{
					termText	=
						StringUtils.replaceAll(
							termText , "i" , mortality_immortal );

					termText	=
						StringUtils.replaceAll(
							termText , "I" , mortality_immortal );

					termText	=
						StringUtils.replaceAll(
							termText , "s" , mortality_immortal );

					termText	=
						StringUtils.replaceAll(
							termText , "S" , mortality_immortal );

					termText	=
						StringUtils.replaceAll(
							termText , "m" ,  mortality_mortal );

					termText	=
						StringUtils.replaceAll(
							termText , "M" ,  mortality_mortal );

					termText	=
						StringUtils.replaceAll(
							termText , "u" , mortality_uncertain );

					termText	=
						StringUtils.replaceAll(
							termText , "U" ,  mortality_uncertain );
				}
				else
				{
					String termTextLower	= termText.toLowerCase();

					if ( termTextLower.equals( "m" ) )
					{
						termText	= Mortality.MORTAL + "";
					}
					else if ( termTextLower.equals( "i" ) )
					{
						termText	= Mortality.IMMORTAL_OR_SUPERNATURAL + "";
					}
					else if ( termTextLower.equals( "s" ) )
					{
						termText	= Mortality.IMMORTAL_OR_SUPERNATURAL + "";
					}
					else if ( termTextLower.equals( "u" ) )
					{
						termText	= Mortality.UNKNOWN_OR_OTHER + "";
					}
					else
					{
						CQLSyntaxError.badTermType
						(
							tokenizer ,
							"CQL Term" ,
							termText
						);
					}
                }

				break;

			case ISVERSE	:
				if ( termText.length() == 0 )
				{
					CQLSyntaxError.badTermType
					(
						tokenizer ,
						"CQL Term" ,
						termText
					);
				}

				if ( isRegExp )
				{
					termText	=
						StringUtils.replaceAll(
							termText , "y" , prosodic_verse );

					termText	=
						StringUtils.replaceAll(
							termText , "Y" , prosodic_verse );

					termText	=
						StringUtils.replaceAll(
							termText , "n" , prosodic_prose );

					termText	=
						StringUtils.replaceAll(
							termText , "N" , prosodic_prose );

					termText	=
						StringUtils.replaceAll(
							termText , "u" , prosodic_unknown );

					termText	=
						StringUtils.replaceAll(
							termText , "U" ,  prosodic_unknown );
				}
				else
				{
					String termTextLower	= termText.toLowerCase();

					if ( termTextLower.equals( "y" ) )
					{
						termText	= prosodic_verse;
					}
					else if ( termTextLower.equals( "n" ) )
					{
						termText	= prosodic_prose;
					}
					else if ( termTextLower.equals( "u" ) )
					{
						termText	= prosodic_unknown;
					}
					else
					{
						CQLSyntaxError.badTermType
						(
							tokenizer ,
							"CQL Term" ,
							termText
						);
					}
                }

				break;

			case NARRATIVE	:
				if ( termText.length() == 0 )
				{
					CQLSyntaxError.badTermType
					(
						tokenizer ,
						"CQL Term" ,
						termText
					);
				}

				if ( isRegExp )
				{
					termText	=
						StringUtils.replaceAll(
							termText , "Y" , "y" );

					termText	=
						StringUtils.replaceAll(
							termText , "N" , "n" );
				}
				else
				{
					String termTextLower	= termText.toLowerCase();

					if ( termTextLower.equals( "y" ) )
					{
					}
					else if ( termTextLower.equals( "n" ) )
					{
					}
					else
					{
						CQLSyntaxError.badTermType
						(
							tokenizer ,
							"CQL Term" ,
							termText
						);
					}
                }

                if ( termText.indexOf( "y" ) >= 0 )
                {
                	termText	= " = null";
                }
                else
                {
                	termText	= " != null";
                }

				break;
		}
	}

	/**	Get term type name.
	 *
	 *	@param	termType	The term type.
	 *
	 *	@return				Name of the term type as a string.
	 *						Empty string if term type unknown.
	 */

	protected String getTermTypeName( int termType )
	{
		String result	=
			(String)elementsToNames.get( new Integer( termType ) );

		if ( result == null ) result = "";

		return result;
	}

	/**	Get string version of query term for display.
	 *
	 *	@return		String version of query term.
	 */

	public String toString()
	{
	 	StringBuffer sb	= new StringBuffer();

	 	if ( !matchTerm ) sb.append( "!" );

	 	sb.append( getTermTypeName( termType ) );

	 	sb.append( "(" );

	 	sb.append( termText );

	 	sb.append( ")" );

	 	return sb.toString();
	}

	/**	Get HQL version of query term.
	 *
	 *	@param	queryField	The name of field to which this query term applies.
	 *	@param	fieldIndex	The index of this field in the select clause.
	 *	@param	doingWords	true if word results, false for work part results.
	 *
	 *	@return				HQL version of query term for specified field.
	 */

	public String toHQL
	(
		String queryField ,
		int fieldIndex ,
		boolean doingWords
	)
	{
		if ( ( termText == null ) || ( termText.length() == 0 ) ) return "";

		String hqlName	=
			(String)elementsToHQLNames.get( new Integer( termType ) );

		if ( doingWords )
		{
			hqlName	=
				StringUtils.replaceAll(
					hqlName , "%p" , "%w." );
		}
		else
		{
			hqlName	=
				StringUtils.replaceAll(
					hqlName , "%p" , "" );
		}

		if ( ( hqlName.indexOf( "%w" ) ) >= 0 )
		{
			hqlName	=
				StringUtils.replaceAll( hqlName , "%w" , queryField );
		}

		if ( ( hqlName.indexOf( "%i" ) ) >= 0 )
		{
			hqlName	=
				StringUtils.replaceAll( hqlName , "%i" , fieldIndex + "" );
		}

		boolean allowsOp	=
			((Boolean)allowsOperators.get(
				new Integer( termType ) ) ).booleanValue();

		StringBuffer sb	= new StringBuffer();

		if (!allowsOp && isRegExp && termType != POS) {
			if ( !matchTerm ) sb.append( "(not " );
			sb.append( "(regexp_like(" );
		}
		else {
			sb.append( "(" );
		}
		sb.append( hqlName );

		if ( allowsOp )
		{
			sb.append( termText );
		}
		else
		{
			String theTermText	= termText;

			if ( isRegExp )
			{
				if ( termType == POS )
				{
					RegExpCollectionFilter filter	=
						new RegExpCollectionFilter( termText );

					Collection posCol	= new ArrayList();

					try
					{
//						posCol	=
//							Arrays.asList( CachedCollections.getPos() );
						posCol	=
							PersistenceManager.getPM().getAllPos();
					}
					catch ( Exception e )
					{
					}

System.err.println( "posCol has " + posCol.size() + " entries." );

					Collection filteredCol	= filter.filter( posCol );

System.err.println( "filteredCol has " + filteredCol.size() + " entries." );

					if ( matchTerm )
					{
        				sb.append( " in (" );
					}
					else
					{
        				sb.append( " not in (" );
					}

					boolean first	= true;

					for	(	Iterator iterator	= filteredCol.iterator() ;
							iterator.hasNext() ;
						)
					{
						if ( !first )
						{
							sb.append( "," );
						}

						first	= false;

						sb.append( "'" );
						sb.append( iterator.next().toString() );
						sb.append( "'" );
					}

       				sb.append( ")" );

       				theTermText	= null;
				}
				else
				{
					// regexp_like
					sb.append( ", " );
				}
			}
			else
			{
		        if ( matchTerm )
				{
        			sb.append( "=" );
        		}
        		else
        		{
        			sb.append( "<>" );
      	  		}
			}

			if ( theTermText != null )
			{
				sb.append( "'" );
				sb.append( theTermText );
				sb.append( "'" );
			}
		}

		sb.append( ")" );

		// regexp_like
		if (!allowsOp && isRegExp && termType != POS) {
			sb.append( " <> 0)");
		}

		return sb.toString();
    }

	/**	Determine if query term refers to an author.
	 *
	 *	@return		true if a constituent term refers to an author.
	 */

	public boolean hasAuthor()
	{
		return
			( termType == AUTHORNAME ) ||
			( termType == AUTHORCONTEMPORARY );
	}

	/**	Determine if query term refers to a speaker.
	 *
	 *	@return		true if a constituent term refers to a speaker.
	 */

	public boolean hasSpeaker()
	{
		return ( termType == SPEAKER );
	}

	/**	Determine if query term refers to a speech.
	 *
	 *	@return		true if a constituent term refers to a speech.
	 */

	public boolean hasSpeech()
	{
		return
			( termType == SPEAKERGENDER ) ||
			( termType == SPEAKERMORTALITY ) ||
			( termType == NARRATIVE );
	}

	/**	Determine if query term refers to a word part.
	 *
	 *	@return		true if a constituent term refers to a word part.
	 */

	public boolean hasWordPart()
	{
		return
			( termType == SPELLING ) ||
			( termType == LEMMA ) ||
			( termType == POS ) ||
			( termType == WORDCLASS ) ||
			( termType == MAJORWORDCLASS ) ||
			( termType == SEMANTICTAGS ) ||
			hasSpeaker() ||
			hasSpeech();
	}

	/**	Determine if query term refers to a work.
	 *
	 *	@return		true if a constituent term refers to a work.
	 */

	public boolean hasWork()
	{
		return
			( termType == WORKFULLTITLE ) ||
			( termType == WORKSHORTTITLE ) ||
			( termType == WORKTAG );
	}

	/**	Determine if query term refers to a work part.
	 *
	 *	@return		true if a constituent term refers to a work part.
	 */

	public boolean hasWorkPart()
	{
		return
			( termType == WORKPARTFULLTITLE ) ||
			( termType == WORKPARTSHORTTITLE ) ||
			( termType == WORKPARTTAG );
	}

	/**	Check if this query term is equal to another.
	 *
	 *	@return		True if the query terms are equal, false otherwise.
	 *
	 *	<p>
	 *	The query terms are equal when the term text, term type,
	 *	match term, and is regular expression fields are the same
	 *	in both query terms.
	 *	</p>
	 */

	public boolean equals( Object other )
	{
	 	boolean result	= false;

	 	if ( other instanceof CQLTerm )
	 	{
	 		CQLTerm otherTerm	= (CQLTerm)other;

			result	=
				( this.termText		== otherTerm.termText ) &&
				( this.termType 	== otherTerm.termType ) &&
				( this.matchTerm 	== otherTerm.matchTerm ) &&
				( this.isRegExp		== otherTerm.isRegExp );
	 	}

	 	return result;
	}

	/**	Hash code for query term.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode()
	{
	 	return toString().hashCode();
	}

	/**	Static initializer for query term data.
	 */

	static
	{
		namesToElements		= new HashMap();
		elementsToNames		= new HashMap();
		elementsToHQLNames	= new HashMap();
		allowsOperators		= new HashMap();
//
		namesToElements.put( "spe" , new Integer( SPELLING ) );
		namesToElements.put( "lem" , new Integer( LEMMA ) );
		namesToElements.put( "pos" , new Integer( POS ) );
		namesToElements.put( "wc" , new Integer( WORDCLASS ) );
		namesToElements.put( "mwc" , new Integer( MAJORWORDCLASS ) );
		namesToElements.put( "spg" , new Integer( SPEAKERGENDER ) );
		namesToElements.put( "spk" , new Integer( SPEAKER ) );
		namesToElements.put( "sem" , new Integer( SEMANTICTAGS ) );
		namesToElements.put( "ver" , new Integer( ISVERSE ) );
		namesToElements.put( "met" , new Integer( METRICALSHAPE ) );
		namesToElements.put( "spm" , new Integer( SPEAKERMORTALITY ) );
		namesToElements.put( "wdpc" , new Integer( WORDPARTCOUNT ) );
		namesToElements.put( "aun" , new Integer( AUTHORNAME ) );
		namesToElements.put( "auc" , new Integer( AUTHORCONTEMPORARY ) );
		namesToElements.put( "cot" , new Integer( CORPUSTITLE ) );
		namesToElements.put( "cog" , new Integer( CORPUSTAG ) );
		namesToElements.put( "wkt" , new Integer( WORKFULLTITLE ) );
		namesToElements.put( "wkst" , new Integer( WORKSHORTTITLE ) );
		namesToElements.put( "wpt" , new Integer( WORKPARTFULLTITLE ) );
		namesToElements.put( "wpst" , new Integer( WORKPARTSHORTTITLE ) );
		namesToElements.put( "wkg" , new Integer( WORKTAG ) );
		namesToElements.put( "wpg" , new Integer( WORKPARTTAG ) );
		namesToElements.put( "pye" , new Integer( PUBLICATIONYEAREARLY ) );
		namesToElements.put( "pyl" , new Integer( PUBLICATIONYEARLATE ) );
		namesToElements.put( "nar" , new Integer( NARRATIVE ) );
//
		elementsToNames.put( new Integer( SPELLING ) , "spe" );
		elementsToNames.put( new Integer( LEMMA ) , "lem" );
		elementsToNames.put( new Integer( WORDPARTCOUNT ) , "wdpc" );
		elementsToNames.put( new Integer( POS ) , "pos" );
		elementsToNames.put( new Integer( WORDCLASS ) , "wc" );
		elementsToNames.put( new Integer( MAJORWORDCLASS ) , "mwc" );
		elementsToNames.put( new Integer( SPEAKERGENDER ) , "spg" );
		elementsToNames.put( new Integer( SPEAKER ) , "spk" );
		elementsToNames.put( new Integer( SEMANTICTAGS ) , "sem" );
		elementsToNames.put( new Integer( ISVERSE ) , "ver" );
		elementsToNames.put( new Integer( METRICALSHAPE ) , "met" );
		elementsToNames.put( new Integer( SPEAKERMORTALITY ) , "spm" );
		elementsToNames.put( new Integer( AUTHORNAME ) , "aun" );
		elementsToNames.put( new Integer( AUTHORCONTEMPORARY ) , "auc" );
		elementsToNames.put( new Integer( CORPUSTITLE ) , "cot" );
		elementsToNames.put( new Integer( CORPUSTAG ) , "cog" );
		elementsToNames.put( new Integer( WORKFULLTITLE ) , "wkt" );
		elementsToNames.put( new Integer( WORKSHORTTITLE ) , "wkst" );
		elementsToNames.put( new Integer( WORKPARTFULLTITLE ) , "wpt" );
		elementsToNames.put( new Integer( WORKPARTSHORTTITLE ) , "wpst" );
		elementsToNames.put( new Integer( WORKTAG ) , "wkg" );
		elementsToNames.put( new Integer( WORKPARTTAG ) , "wpg" );
		elementsToNames.put( new Integer( PUBLICATIONYEAREARLY ) , "pye" );
		elementsToNames.put( new Integer( PUBLICATIONYEARLATE ) , "pyl" );
		elementsToNames.put( new Integer( NARRATIVE ) , "nar" );
//
		elementsToHQLNames.put(
			new Integer( SPELLING ) ,
			"%w.spellingInsensitive.string" );

		elementsToHQLNames.put(
			new Integer( LEMMA ) ,
			"wordPart%i.lemPos.lemma.tagInsensitive.string" );

		elementsToHQLNames.put(
			new Integer( POS ) ,
			"wordPart%i.lemPos.pos.tag" );

		elementsToHQLNames.put(
			new Integer( WORDCLASS ) ,
			"wordPart%i.lemPos.lemma.wordClass.tag" );

		elementsToHQLNames.put
		(
			new Integer( MAJORWORDCLASS ) ,
			"wordPart%i.lemPos.lemma.wordClass.majorWordClass.majorWordClass"
		);

		elementsToHQLNames.put(
			new Integer( SPEAKER ) ,
			"%w.speech.speakers.name" );

		elementsToHQLNames.put(
			new Integer( SPEAKERGENDER ) ,
			"%w.speech.gender" );

		elementsToHQLNames.put(
			new Integer( SEMANTICTAGS ) ,
			"wordPart%i.semanticTags" );

		elementsToHQLNames.put(
			new Integer( ISVERSE ) ,
			"%w.prosodic" );

		elementsToHQLNames.put(
			new Integer( METRICALSHAPE ) ,
			"%w.metricalShape" );

		elementsToHQLNames.put(
			new Integer( SPEAKERMORTALITY ) ,
			"%w.speech.mortality" );

		elementsToHQLNames.put(
			new Integer( NARRATIVE ) ,
			"%w.speech" );

		elementsToHQLNames.put(
			new Integer( WORDPARTCOUNT ) ,
			"size(%w.wordParts)" );

		elementsToHQLNames.put(
			new Integer( AUTHORNAME ) ,
			"%pwork.authors.name.string" );

//		elementsToHQLNames.put(
//			new Integer( AUTHORCONTEMPORARY ) ,
//			"%pwork.authors.name.string" );

		elementsToHQLNames.put(
			new Integer( CORPUSTITLE ) ,
			"%pwork.corpus.title" );

		elementsToHQLNames.put(
			new Integer( CORPUSTAG ) ,
			"%pwork.corpus.tag" );

		elementsToHQLNames.put(
			new Integer( WORKFULLTITLE ) ,
			"%pwork.fullTitle" );

		elementsToHQLNames.put(
			new Integer( WORKSHORTTITLE ) ,
			"%pwork.shortTitle" );

		elementsToHQLNames.put(
			new Integer( WORKTAG ) ,
			"%pwork.tag" );

		elementsToHQLNames.put(
			new Integer( WORKPARTFULLTITLE ) ,
			"%pworkPart.fullTitle" );

		elementsToHQLNames.put(
			new Integer( WORKPARTSHORTTITLE ) ,
			"%pworkPart.shortTitle" );

		elementsToHQLNames.put(
			new Integer( WORKPARTTAG ) ,
			"%pworkPart.tag" );

		elementsToHQLNames.put(
			new Integer( PUBLICATIONYEAREARLY ) ,
			"%pwork.pubDate.startYear >= " );

		elementsToHQLNames.put(
			new Integer( PUBLICATIONYEARLATE ) ,
			"%pwork.pubDate.endYear <= " );
//
		allowsOperators.put(
			new Integer( SPELLING ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( LEMMA ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( WORDPARTCOUNT ) , new Boolean( true ) );

		allowsOperators.put(
			new Integer( POS ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( WORDCLASS ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( MAJORWORDCLASS ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( SPEAKERGENDER ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( SPEAKER ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( SEMANTICTAGS ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( ISVERSE ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( METRICALSHAPE ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( SPEAKERMORTALITY ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( AUTHORNAME ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( AUTHORCONTEMPORARY ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( CORPUSTITLE ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( CORPUSTAG ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( WORKFULLTITLE ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( WORKSHORTTITLE ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( WORKPARTFULLTITLE ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( WORKPARTSHORTTITLE ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( WORKTAG ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( WORKPARTTAG ) , new Boolean( false ) );

		allowsOperators.put(
			new Integer( PUBLICATIONYEAREARLY ) , new Boolean( true ) );

		allowsOperators.put(
			new Integer( PUBLICATIONYEARLATE ) , new Boolean( true ) );

		allowsOperators.put(
			new Integer( NARRATIVE ) , new Boolean( true ) );
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


