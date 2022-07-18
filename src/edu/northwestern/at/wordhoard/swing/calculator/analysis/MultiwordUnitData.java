package edu.northwestern.at.wordhoard.swing.calculator.analysis;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.utils.math.*;
import edu.northwestern.at.utils.corpuslinguistics.*;

/**	Multiword unit data.
 *
 *	<p>
 *	Holds data on counts and association measure values for one
 *	multiword unit.
 *	</p>
 */

public class MultiwordUnitData
{
	protected String mwu;
	protected int mwuCount;
	protected int mwuLength;
	protected String[] words;
	protected int[] wordCounts;

	protected double dice;
	protected double logLikelihood;
	protected double phiSquared;
	protected double scp;
	protected double si;

	protected double sigLogLikelihood;

	protected NGramExtractor[] extractors;

	protected String leftSuccessorPattern;
	protected String rightSuccessorPattern;

	protected int totalWordCount;
	protected Map wordCountMap;

	public MultiwordUnitData
	(
		String mwu ,
		Map wordCountMap ,
		int totalWordCount ,
		NGramExtractor[] extractors
	)
	{
		this.mwu					= mwu;
		this.wordCountMap			= wordCountMap;
		this.totalWordCount			= totalWordCount;
		this.extractors				= extractors;

		leftSuccessorPattern		= "\t" + mwu;
		rightSuccessorPattern		= mwu + "\t";

		words						= NGramExtractor.splitNGramIntoWords( mwu );

		this.mwuLength				= words.length;

		NGramExtractor extractor	= extractors[ words.length - 1 ];

		this.mwuCount				= extractor.getNGramCount( mwu );

		calculateAssociationMeasures();
	}

	/**	Get the multiword unit text.
	 *
	 *	@return		The multiword unit text.
	 */

	public String getMWUText()
	{
		return mwu;
	}

	/**	Get the count for this multiword unit text.
	 *
	 *	@return		Count of appearances of this multiword unit.
	 */

	public int getMWUTextCount()
	{
		return mwuCount;
	}

	/**	Get the number of words in this multiword unit.
	 *
	 *	@return		Number of words in this multiword unit.
	 */

	public int getMWUTextLength()
	{
		return mwuLength;
	}

	/**	Get the words in this multiword unit.
	 *
	 *	@return		Words in this multiword unit.
	 */

	public String[] getWords()
	{
		return words;
	}

	/**	Get the count for each word in this multiword unit.
	 *
	 *	@return		Count for each word in this multiword unit.
	 */

	public int[] getWordCounts()
	{
		return wordCounts;
	}

	/**	Get the left antecedent of the current multiword unit.
	 *
	 *	@return		The left antecedent as a string.
	 */

	public String leftAntecedent()
	{
		StringBuffer sb			= new StringBuffer( words.length * 10 );

		for ( int i = 0 ; i < ( words.length - 1 ) ; i++ )
		{
			if ( i > 0 ) sb	= sb.append( "\t" );
    		sb.append( words[ i ] );
		}

		return sb.toString();
	}

	/**	Get the right antecedent of the current multiword unit.
	 *
	 *	@return		The right antecedent as a string.
	 */

	public String rightAntecedent()
	{
		StringBuffer sb			= new StringBuffer( words.length * 10 );

		for ( int i = 1 ; i < words.length ; i++ )
		{
			if ( i > 1 ) sb	= sb.append( "\t" );
    		sb.append( words[ i ] );
		}

		return sb.toString();
	}

	/**	Get the successors of the current multiword unit.
	 *
	 *	@return		The successors as an array of strings.
	 */

	public String[] successors()
	{
		NGramExtractor extractor	= extractors[ words.length ];

		ArrayList successorList		= new ArrayList();

		String[] ngramsp1			= extractor.getNGrams();

    	for ( int i = 0 ; i < ngramsp1.length ; i++ )
		{
			if ( ngramsp1[ i ].startsWith( rightSuccessorPattern ) )
			{
				successorList.add( ngramsp1[ i ] );
			}
			else if ( ngramsp1[ i ].endsWith( leftSuccessorPattern ) )
			{
				successorList.add( ngramsp1[ i ] );
			}
		}

		return (String[])successorList.toArray( new String[]{} );
	}

	/**	Get the left successors of the current multiword unit.
	 *
	 *	@return		The left successors as an array of strings.
	 */

	public String[] leftSuccessors()
	{
		NGramExtractor extractor	= extractors[ words.length ];

		ArrayList successorList		= new ArrayList();

		SortedMap successorMap		= extractor.getNGramMap();

		Iterator iterator			= successorMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			String potentialSuccessor	= (String)iterator.next();

			if ( potentialSuccessor.endsWith( leftSuccessorPattern ) )
			{
				successorList.add( potentialSuccessor );
			}
		}

		return (String[])successorList.toArray( new String[]{} );
	}

	/**	Get the right successors of the current multiword unit.
	 *
	 *	@return		The right successors as an array of strings.
	 */

	public String[] rightSuccessors()
	{
		NGramExtractor extractor	= extractors[ words.length ];

		ArrayList successorList		= new ArrayList();

		SortedMap successorMap		=
			extractor.getNGramMap().tailMap( rightSuccessorPattern );

		Iterator iterator	= successorMap.keySet().iterator();

		while ( iterator.hasNext() )
		{
			String potentialSuccessor	= (String)iterator.next();

			if ( potentialSuccessor.startsWith( rightSuccessorPattern ) )
			{
				successorList.add( potentialSuccessor );
			}
			else
			{
				break;
			}
		}

		return (String[])successorList.toArray( new String[]{} );
	}

	/**	Calculate fair probability for the left hand side of a pseudo-bigram.
	 *
	 *	@return				Fair probability for left hand side of
	 *						pseudo-bigram.
	 */

	public double getAvx()
	{
								//	Compute count for left-hand portion of
								//	pseudo-bigram.

		double avx					= getWordCount( words[ 0 ] );

		StringBuffer sb;;

		for ( int i = 1 ; i <= words.length - 2 ; i++ )
		{
			sb	= new StringBuffer( words.length * 10 );

			int k	= -1;

			for ( int j = 0 ; j <= i ; j++ )
			{
			    if ( j > 0 ) sb	= sb.append( "\t" );

				sb	= sb.append( words[ j ] );
				k++;
			}

			NGramExtractor extractormi	= extractors[ k ];

			avx	+= extractormi.getNGramCount( sb.toString() );
		}

		return avx / ( words.length - 1 );
	}

	/**	Calculate fair probability for the right hand side of a pseudo-bigram.
	 *
	 *	@return				Fair probability for right hand side of
	 *						pseudo-bigram.
	 */

	public double getAvy()
	{
								//	Compute count for right-hand portion of
								//	pseudo-bigram.

		double avy					=
			getWordCount( words[ words.length - 1 ] );

		int k						= words.length - 2;

		StringBuffer sb;

		for ( int i = 1 ; i < words.length - 1 ; i++ )
		{
			sb	= new StringBuffer( words.length * 10 );

			for ( int j = i ; j < words.length ; j++ )
			{
			    if ( sb.length() > 0 ) sb	= sb.append( "\t" );
				sb	= sb.append( words[ j ] );
			}

			NGramExtractor extractormi	= extractors[ k-- ];

			avy	+= extractormi.getNGramCount( sb.toString() );
		}

		return avy / ( words.length - 1 );
	}

	/**	Get the fair dispersion point normalization.
	 *
	 *	@return			Fair dispersion point normalization.
	 */

	protected double getAvp()
	{
		int n		= words.length;
		double avp	= 0.0D;

		for ( int i = 0 ; i < ( n - 1 ) ; i++ )
		{
			avp	+= prob( words , 0 , i ) * prob( words , i + 1 , n - 1 );
		}

		return avp / ( n - 1 );
	}

	/**	Get the fair dispersion point normalization.
	 *
	 *	@return			Fair dispersion point normalization.
	 */

	protected double getAvp2()
	{
		int n		= words.length;
		double avp2	= 0.0D;

		for ( int i = 0 ; i < ( n - 1 ) ; i++ )
		{
			avp2	+=
				freq( words , 0 , i ) * freq( words , i + 1 , n - 1 );
		}

		return avp2 / ( n - 1 );
	}

	/**	Calculate the association measures.
	 */

	public void calculateAssociationMeasures()
	{
								//	Get average frequency values for
								//	left and right hand portions of a
								//	real or pseudo-bigram.

		double avx	= getAvx();
		double avy	= getAvy();

								//	Get average probability for
								//	pseudo-bigram.

		double avp	= getAvp();
		double avp2	= getAvp2();

								//	Log-likelihood.

		logLikelihood			=
			BigramLogLikelihood.calculateLogLikelihood
			(
				avx ,
				avy ,
				(double)mwuCount ,
				totalWordCount
			);
								//	Dice.

		this.dice			=
			( 2.0D * freq( words , 0 , words.length - 1 ) ) / ( avx + avy );

		double probWords	= prob( words , 0 , words.length - 1 );

		double scpValue		= ( probWords * probWords ) / avp;

								//	SCP.

		this.scp			= Math.max( Math.min( scpValue , 1.0D ) , 0.0D );

								//	SI.

		this.si				= ArithUtils.log2( probWords / avp );

								//	Phi Squared.

		this.phiSquared		= 0.0D;
		double freq			= mwuCount;

		double n			= totalWordCount;

		double numerator	= freq * n - avp2;
		numerator			= numerator * numerator;
		double denominator	= avp2 * ( n - avx ) * ( n - avy );

		if ( denominator != 0.0D )
		{
			this.phiSquared	= numerator / denominator;
		}
	}

	/**	Calculate the probability for a portion of a set of words.
	 *
	 *	@param	words	The words.
	 *	@param	i1		Starting index.
	 *	@param	i2		Ending index.
	 *
	 *	@return			Probability from word counts.
	 *
	 *	<p>
	 *	We use the maximum likelihood estimate of the probability,
	 *	which is just the number of times the word appears divided
	 *	by the number of words.  For ngrams, we divide the number
	 *	of times the ngram appears by the total number of ngrams
	 *	containing the same number of words.
	 *	</p>
	 *
	 */

	public double prob( String[] words , int i1 , int i2 )
	{
		StringBuffer sb	= new StringBuffer( words.length * 10 );

		int k = 0;

		for ( int i = i1 ; i <= i2 ; i++ )
		{
			if ( sb.length() > 0 ) sb.append( "\t" );
			sb.append( words[ i ] );
			k++;
		}

		double result;

		if ( k == 1 )
		{
			result	=
				(double)getWordCount( sb.toString() ) /
				(double)totalWordCount;
		}
		else
		{
			result	=
				(double)extractors[ k - 1 ].getNGramCount( sb.toString() ) /
				(double)extractors[ k - 1 ].getNumberOfNGrams();
		}

		return result;
	}

	/**	Calculate the frequency for a portion of a set of words.
	 *
	 *	@param	words	The words.
	 *	@param	i1		Starting index.
	 *	@param	i2		Ending index.
	 *
	 *	@return			Frequency from ngram frequencies.
	 */

	public double freq( String[] words , int i1 , int i2 )
	{
		StringBuffer sb	= new StringBuffer( words.length * 10 );

		int k = 0;

		for ( int i = i1 ; i <= i2 ; i++ )
		{
			if ( sb.length() > 0 ) sb.append( "\t" );
			sb.append( words[ i ] );
			k++;
		}

		double result;

		if ( k == 1 )
		{
			result	= getWordCount( sb.toString() );
		}
		else
		{
			result	=
				(double)extractors[ k - 1 ].getNGramCount( sb.toString() );
		}

		return result;
	}

	/**	Return the Dice coefficient.
	 *
	 *	@return		The Dice coefficient.
	 */

	public double getDice()
	{
		return dice;
	}

	/**	Return log likelihood.
	 *
	 *	@return		log likelihood.
	 */

	public double getLogLikelihood()
	{
		return logLikelihood;
	}

	/**	Return phi squared.
	 *
	 *	@return		phi squared.
	 */

	public double getPhiSquared()
	{
		return phiSquared;
	}

	/**	Return the symmetric conditional probability.
	 *
	 *	@return		The symmetric conditional probability.
	 */

	public double getSCP()
	{
		return scp;
	}

	/**	Return the specific mutual information.
	 *
	 *	@return		The specific mutual information.
	 */

	public double getSI()
	{
		return si;
	}

	/**	Return significance of log likelihood.
	 *
	 *	@return		significance of log likelihood.
	 */

	public double getSigLogLikelihood()
	{
		return sigLogLikelihood;
	}

	/**	Get count for a specific word from the count map.
	 *
	 *	@param	word		The word text.
	 *
	 *	@return				The count for the specified word.
	 *						0 if the word does not occur.
	 */

	public int getWordCount( String word )
	{
		int result	= 0;

		if ( wordCountMap.containsKey( word ) )
		{
			result	= ((Integer)wordCountMap.get( word )).intValue();
		}

		return result;
	}

	/**	Return mwu as a displayable string.
	 *
	 *	@return		The mwu as a displayable string.
	 */

	public String toString()
	{
		return mwu.replaceAll( "\t" , " " );
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

