package edu.northwestern.at.utils.corpuslinguistics;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.*;
import edu.northwestern.at.utils.math.distributions.*;

/**	Computes frequency-based statistics for comparing corpora.
 */

public class Frequency
{
	/**	Compute log-likelihood statistic for comparing frequencies in two corpora.
	 *
	 *	@param	sampleCount		Count of word/lemma appearance in sample.
	 *	@param	refCount		Count of word/lemma appearance in reference
	 *							corpus.
	 *	@param	sampleSize		Total words/lemmas in the sample.
	 *	@param	refSize			Total words/lemmas in reference corpus.
	 *	@param	computeLLSig	Compute significance of log likelihood.

	 *	@return					A double array containing frequency comparison
	 *							statistics.
	 *
	 *	<p>
	 *	The contents of the result array are as follows.
	 *	</p>
	 *	<pre>
	 *	(0)	Count of word/lemma appearance in sample.
	 *  (1)	Percent of word/lemma appearance in sample.
	 *	(2)	Count of word/lemma appearance in reference.
	 *	(3) Percent of word/lemma appearance in reference.
	 *	(4)	Log-likelihood measure.
	 *	(5) Significance of log-likelihood.
	 *	</pre>
	 *
	 *	<p>
	 *	The results of any zero divides are set to zero.
	 *	</p>
	 */

	public static double[] logLikelihoodFrequencyComparison
	(
		int sampleCount ,
		int refCount ,
		int sampleSize ,
		int refSize ,
		boolean computeLLSig
	)
	{
		double result[]	= new double[ 6 ];

		double a		= sampleCount;
		double b		= refCount;
		double c		= sampleSize;
		double d		= refSize;

		double e1		= c * ( a + b ) / ( c + d );
		double e2		= d * ( a + b ) / ( c + d );

		double ae1		= 0.0D;

		if ( e1 != 0.0D )
		{
			ae1	= a / e1;
		}

		double be2		= 0.0D;

		if ( e2 != 0.0D )
		{
			be2	= b / e2;
		}

		double logLike	=
			2.0D * ( ( a * ArithUtils.safeLog( ae1 ) ) +
			( b * ArithUtils.safeLog( be2 ) ) );

		result[ 0 ]	= a;

		if ( c == 0.0D )
		{
			result[ 1 ]	= 0.0D;
		}
		else
		{
			result[ 1 ]	= 100.0D * ( a / c );
		}

		result[ 2 ]	= b;

		if ( d == 0.0D )
		{
			result[ 3 ]	= 0.0D;
		}
		else
		{
			result[ 3 ]	= 100.0D * ( b / d );
		}

		result[ 4 ]	= logLike;
		result[ 5 ]	= 0.0D;

		if ( computeLLSig ) result[ 5 ]	= Sig.chisquare( logLike , 1 );

		return result;
	}

	/**	Compute log-likelihood statistic for comparing frequencies in two corpora.
	 *
	 *	@param	sampleCount		Count of word/lemma appearance in sample.
	 *	@param	refCount		Count of word/lemma appearance in reference
	 *							corpus.
	 *	@param	sampleSize		Total words/lemmas in the sample.
	 *	@param	refSize			Total words/lemmas in reference corpus.

	 *	@return					A double array containing frequency comparison
	 *							statistics.
	 *
	 *	<p>
	 *	The contents of the result array are as follows.
	 *	</p>
	 *	<pre>
	 *	(0)	Count of word/lemma appearance in sample.
	 *  (1)	Percent of word/lemma appearance in sample.
	 *	(2)	Count of word/lemma appearance in reference.
	 *	(3) Percent of word/lemma appearance in reference.
	 *	(4)	Log-likelihood measure.
	 *	(5) Significance of log-likelihood.
	 *	</pre>
	 */

	public static double[] logLikelihoodFrequencyComparison
	(
		int sampleCount ,
		int refCount ,
		int sampleSize ,
		int refSize
	)
	{
		return logLikelihoodFrequencyComparison(
			sampleCount , refCount , sampleSize , refSize , true );
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected Frequency()
	{
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

