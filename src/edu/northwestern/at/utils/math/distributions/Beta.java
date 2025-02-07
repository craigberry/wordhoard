package edu.northwestern.at.utils.math.distributions;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.*;
import edu.northwestern.at.utils.math.rootfinders.*;

/**	Beta distribution functions.
 */

public class Beta
{
	/**	Log of the Beta distribution.
	 *
	 *	@param	a	First log beta parameter
	 *	@param	b	Second log beta parameter
	 *
	 *	@return		Log of the Beta distribution for specified parameters.
	 *
	 *	<p>
	 *	The log of the beta distribution is calculated from the
	 *	log of the gamma distribution using the following relationship:
	 *	</p>
	 *
	 *	<p>
	 *	logBeta(a,b) = logGamma(a) + logGamma(b) - logGamma( a + b )
	 *	</p>
	 */

	public static double logBeta( double a , double b )
	{
		return
			Gamma.logGamma( a ) +
			Gamma.logGamma( b ) -
			Gamma.logGamma( a + b );
	}

	/**	Beta function.
	 *
	 *	@param	a	First beta parameter
	 *	@param	b	Second beta parameter
	 *
	 *	@return		Beta distribution for specified arguments.
	 *
	 *	<p>
	 *	The beta distribution value is calculated from the
	 *	gamma distribution using the following relationship:
	 *	</p>
	 *
	 *	<p>
	 *	Beta(a,b) = ( Gamma(a) * Gamma(b) ) / Gamma( a + b )
	 *	</p>
	 */

	public static double beta( double a , double b )
		throws ArithmeticException
	{
		double result	= 1.0D;
		double ab		= Gamma.gamma( a + b );

								//	If (a + b ) is zero, return 1.0 as
								//	the value of beta.

		if ( ab == 0.0D ) return result;

								//	Avoid possible overflow by multiplying
								//	dividing gamma(max(a,b)) by
								//	gamma(a+b) and then multiplying
								//	by gamma(min(a,b)) .
		if ( a > b )
		{
			result =	Gamma.gamma( a ) / ab;
			result *=	Gamma.gamma( b );
		}
		else
		{
			result =	Gamma.gamma( b ) / ab;
			result *=	Gamma.gamma( a );
		}

		return result;
	}

	/** Cumulative probability density function for the incomplete beta function.
	 *
	 *	@param	x		Upper percentage point of incomplete beta
	 *					probability density function
	 *	@param	alpha	First shape parameter
	 *	@param	beta	Second shape parameter
	 *	@param	dPrec	Digits of precision desired (1 &lt; dPrec &lt; Constants.MAXPREC)
	 *
	 *	@return			Cumulative probability density function value.
	 *
	 *	@throws			IllegalArgumentException
	 *						if x &lt;= 0 or a &lt;= 0 or b &lt;= 0 .
	 *
	 *	<p>
	 *  The continued fraction expansion as given by
	 *  Abramowitz and Stegun (1964) is used.  This
	 *  method works well unless the minimum of (alpha, beta)
	 *  exceeds about 70000.  For most common values the result
	 *	will be accurate to about 14 decimal digits.
	 *  </p>
	 */

	public static double incompleteBeta
	(
		double x,
		double alpha,
		double beta,
		int	dPrec
	)
		throws IllegalArgumentException
	{
		double	epsz;
		double	a   ;
		double	b   ;
		double	c   ;
		double	f   ;
		double	fx  ;
		double	apb ;
		double	zm  ;
		double	alo ;
		double	ahi ;
		double	blo ;
		double	bhi ;
		double	bod ;
		double	bev ;
		double	zm1 ;
		double	d1  ;
		double	aev ;
		double	aod ;
        double	result;

		int		iter;
        int		maxIter;

		boolean	qSwap;
		boolean	qConv;

								/* Initialize */

		if ( dPrec > Constants.MAXPREC )
		{
			dPrec = Constants.MAXPREC;
		}
		else if ( dPrec <= 0 )
		{
			dPrec = 1;
		}

		epsz	= Math.pow( 10.0D , -dPrec );

		a		= alpha;
		b		= beta;
		qSwap 	= false;
		result	= -1.0D;
		maxIter	= 200;
                                   /* Check arguments */
                                   /* Error if:       */
                                   /*    X <= 0       */
                                   /*    A <= 0       */
                                   /*    B <= 0       */
		if ( x <= 0.0D )
		{
			throw new IllegalArgumentException( "x <= 0.0" );
		}

		if ( a <= 0.0D )
		{
			throw new IllegalArgumentException( "a <= 0.0" );
		}

		if ( b <= 0.0D )
		{
			throw new IllegalArgumentException( "b <= 0.0" );
		}

		result = 1.0D;
                                   /* If X >= 1, return 1.0 as prob */

		if ( x >= 1.0D ) return result;

                                   /* If x > a / ( a + b ) then swap */
                                   /* a, b for more efficient eval.  */

		if ( x > ( a / ( a + b ) ) )
		{
			x      = 1.0 - x;
			a      = beta;
			b      = alpha;
			qSwap  = true;
		};

                                   /* Check for extreme values */

		if ( ( x == a ) || ( x == b ) )
		{
		}
		else if ( a == ( ( b * x ) / ( 1.0 - x ) ) )
		{
		}
		else if ( Math.abs( a - ( x * ( a + b ) ) ) <= epsz )
		{
		}
		else
		{
			c	=
				Gamma.logGamma( a + b ) + a * Math.log( x ) +
					b * Math.log( 1.0 - x ) - Gamma.logGamma( a ) -
					Gamma.logGamma( b ) - Math.log( a - x * ( a + b ) );

			if ( ( c < -36.0D ) && qSwap ) return result;

			result = 0.0D;

			if (  c < -180.0D ) return result;
		}
                                   /*  Set up continued fraction expansion */
                                   /*  evaluation.                         */
		apb	= a + b;
		zm	= 0.0D;
		alo	= 0.0D;
		bod	= 1.0D;
		bev	= 1.0D;
		bhi	= 1.0D;
		blo	= 1.0D;

		ahi	=
			Math.exp(
				Gamma.logGamma( apb ) + a * Math.log( x ) +
					b * Math.log( 1.0D - x ) - Gamma.logGamma( a + 1.0D ) -
					Gamma.logGamma( b ) );

		f      = ahi;
		iter   = 0;
                                   /* Continued fraction loop begins here. */
                                   /* Evaluation continues until maximum   */
                                   /* iterations are exceeded, or          */
                                   /* convergence achieved.                */
		qConv  = false;

		do
		{
			fx	= f;

			zm1	= zm;
			zm	= zm + 1.0D;
			d1	= a + zm + zm1;
			aev	= -( a + zm1 ) * ( apb + zm1 ) * x / d1 / ( d1 - 1.0D );
			aod	= zm * ( b - zm ) * x / d1 / ( d1 + 1.0D );
			alo	= bev * ahi + aev * alo;
			blo	= bev * bhi + aev * blo;
			ahi	= bod * alo + aod * ahi;
			bhi	= bod * blo + aod * bhi;

			if ( Math.abs( bhi ) < Double.MIN_VALUE ) bhi = 0.0D;

			if ( bhi != 0.0D )
			{
				f		= ahi / bhi;
				qConv	= ( Math.abs( ( f - fx ) / f ) < epsz );
			};

			iter++;
		}
		while ( ( iter <= maxIter ) && ( !qConv ) );

                                   /* Arrive here when convergence    */
                                   /* achieved, or maximum iterations */
                                   /* exceeded.                       */
		if ( qSwap )
		{
			result = 1.0D - f;
		}
		else
		{
			result = f;
		}

		return result;
	}

	/** Cumulative probability density function for the incomplete beta function.
	 *
	 *	@param	x		Upper percentage point of incomplete beta
	 *					probability density function
	 *	@param	alpha	First shape parameter
	 *	@param	beta	Second shape parameter
	 *
	 *	@return			Cumulative probability density function value.
	 *
	 *	@throws			IllegalArgumentException
	 *						if x &lt;= 0 or a &lt;= 0 or b &lt;= 0 .
	 *
	 *	<p>
	 *  The continued fraction expansion as given by
	 *  Abramowitz and Stegun (1964) is used.  This
	 *  method works well unless the minimum of (alpha, beta)
	 *  exceeds about 70000.  For most common values the result
	 *	will be accurate to about 14 decimal digits.
	 *  </p>
	 */

	public static double incompleteBeta
	(
		double x,
		double alpha,
		double beta
	)
		throws IllegalArgumentException
	{
		return incompleteBeta( x , alpha , beta , Constants.MAXPREC );
	}

	/** Compute value of inverse incomplete beta distribution.
	 *
	 *	@param	p		Probability value.
	 *	@param	alpha	First shape parameter.
	 *	@param	beta	Second shape parameter.
	 *
	 *	@return			Percentage point of inverse beta distribution.
	 *
	 *	@throws			IllegalArgumentException
	 *						if alpha &lt;= 0 or beta &lt;= 0 or p &lt;= 0 or p &gt;= 1 .
	 */

	public static double incompleteBetaInverse
	(
		final double p,
		final double alpha,
		final double beta
	)
		throws IllegalArgumentException
	{
								/* Check validity of arguments */
		if ( alpha <= 0.0D )
		{
			throw new IllegalArgumentException( "alpha<=0" );
		}

		if ( beta <= 0.0D )
		{
			throw new IllegalArgumentException( "beta<=0" );
		}

		if ( ( p > 1.0D ) || ( p < 0.0D ) )
		{
			throw new IllegalArgumentException( "p < 0 or p > 1" );
		}
								/* Check for P = 0 or 1        */

		if ( ( p == 0.0D ) || ( p == 1.0D ) )
		{
			return 0.0D; /* this is bad */
		}
								/* Set precision for results. */

		double eps			= Math.pow( 10.0D , -2 * Constants.MAXPREC );
		int maxIter			= 100;

								/* Create function for evaluating */
								/* zero root. */

		MonadicFunction function	=
			new MonadicFunction()
			{
				public double f( double x )
				{
					return p - Beta.incompleteBeta( x , alpha , beta );
				}
			};
								/* Set initial bracket for root value. */

		double[] bracket	= new double[]{ eps , 1.0D - eps };

								/* Make sure bracket contains value. */

		if ( BracketRoot.bracketRoot( bracket, function, maxIter, 1.6D ) )
		{
								/* Use Brent's method to search for */
								/* root using incomplete beta CDF function. */

			return Brent.brent
			(
				bracket[ 0 ],
				bracket[ 1 ],
				eps,
				maxIter,
				function
			);
		}
		else
		{
			throw new ArithmeticException( "Unable to bracket value" );
		}
	}

	/**	Make class non-instantiable but inheritable.
	 */

	protected Beta()
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

