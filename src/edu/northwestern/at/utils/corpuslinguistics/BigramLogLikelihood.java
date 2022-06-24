package edu.northwestern.at.utils.corpuslinguistics;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.*;

/**	Computes Dunnett's log-likelihood for bigrams.
 */

public class BigramLogLikelihood
{
	/**	Compute one part of log likelihood value.
	 */

	protected static double logLike( double k , double n , double x )
	{
		return
			( k * ArithUtils.safeLog( x ) ) +
			( ( n - k ) * ArithUtils.safeLog( 1.0D - x ) );
	}

	/**	Compute log likelihood value for a bigram.
	 *
	 *	@param	c1			Count of first word in bigram.
	 *	@param	c2			Count of second word in bigram.
	 *	@param	c12			Count of bigram.
	 *	@param	n			Corpus size.
	 *
	 *	@return				The log-likelihood value.
	 */

	public static double calculateLogLikelihood
	(
		double c1 ,
		double c2 ,
		double c12 ,
		double n
	)
	{
		double p	= c2 / n;
		double p1	= c12 / c1;
		double p2	= ( c2 - c12 ) / ( n - c1 );

		double logLikelihood	=
			logLike( c12 , c1 , p ) +
			logLike( c2 - c12 , n - c1 , p ) -
			logLike( c12 , c1 , p1 ) -
			logLike( c2 - c12 , n - c1 , p2 );

		logLikelihood			= -2.0D * logLikelihood;

		return logLikelihood;
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected BigramLogLikelihood()
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

