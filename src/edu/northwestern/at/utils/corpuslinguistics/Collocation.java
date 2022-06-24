package edu.northwestern.at.utils.corpuslinguistics;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.*;

/**	Computes bigram collocation measures.
 */

public class Collocation
{
	/**	Indices of association measures in result array.
	 */

	public static final int DICE		= 0;
	public static final int LOGLIKE		= 1;
	public static final int PHISQUARED	= 2;
	public static final int SMI			= 3;
	public static final int SCP			= 4;
	public static final int T			= 5;
	public static final int Z			= 6;

	/**	Computes collocation measures.
	 *
	 *	@param	sampleCount	Count of collocation appearance in sample.
	 *	@param	refCount	Count of collocation appearance in reference
	 *						corpus.
	 *	@param	sampleSize	Number of words/lemmas in the sample.
	 *	@param	refSize		Number of words/lemmas in reference corpus.
	 *
	 *	@return				A double array containing the following
	 *						measures of collocational association.
	 *
	 *							(0)	Dice coefficient
	 *							(1) Log likelihood
	 *							(2)	Phi squared
	 *							(3)	Specific Mutual information score
	 *							(4)	Symmetric conditional probability
	 *							(5)	z score
	 *              			(6)	t score
	 */

	public static double[] association
	(
		int sampleCount ,
		int refCount ,
		int sampleSize ,
		int refSize
	)
	{
		double result[]	= new double[ Z + 1 ];

		for ( int i = 0 ; i <= Z ; i++ )
		{
			result[ i ]	= 0.0D;
		}
								//	Compute observed and expected
								//	frequencies.

		double observed	= (double)sampleCount;
        double expected	= 0.0D;
		double p		= 0.0D;
		double stdDev	= 0.0D;
		double ominuse	= 0.0D;

		if ( refSize > 0 )
		{
			p			= (double)refCount / (double)refSize;

								//	Compute expected value.

			expected	= p * (double)sampleSize;

                        		//	Compute standard deviation for
                        		//	z score.

			stdDev		= Math.sqrt( expected * ( 1.0D - p ) );

								//	Compute observed minus expected.

			ominuse		= observed - expected;
		}
								//	Compute z score.
		if ( stdDev > 0.0D )
		{
			result[ Z ]	= ominuse / stdDev;
		}
								//	Compute t score.
		if ( observed > 0 )
		{
			result[ T ]	= ominuse / Math.sqrt( observed );
		}
								//	Compute mutual information score.

		if ( expected > 0.0D )
		{
			result[ SMI ]	=
				ArithUtils.safeLog( observed / expected ) / Constants.LN2;
        }
								//	Compute Dice coefficient.

		if ( ( sampleSize + refCount ) > 0 )
		{
			result[ DICE ]	=
				( 2.0D * observed ) / ( sampleSize + refCount );
        }
								//	Compute phi squared.

		if ( ( expected > 0.0D ) && ( refSize > 0 ) )
		{
			result[ PHISQUARED ]	=
				ominuse * ominuse / expected / refSize;
		}
								//	Compute symmetric conditional
								//	probability.

		if ( ( sampleSize * refCount ) > 0 )
		{
			result[ SCP ]	=
				( observed * observed ) /
					( (double)sampleSize * (double)refCount );
		}
								//	Compute log-likelihood.

		result[ LOGLIKE	]	=
			BigramLogLikelihood.calculateLogLikelihood
			(
				(double)sampleSize ,
				(double)refCount ,
				observed ,
				(double)refSize
			);

		return result;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected Collocation()
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


