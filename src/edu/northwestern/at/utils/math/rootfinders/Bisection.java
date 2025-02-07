package edu.northwestern.at.utils.math.rootfinders;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.math.*;

/** Find roots of equations using Bisection.
 *
 *	<p>
 *	The Method of Bisection is a root-finding method which requires an
 *	initial interval [x0,x1] bracketing a root and that the function be
 *	continuous in that interval.
 *	</p>
 *
 *	<p>
 *	An updated estimate of the root value is computed by
 *	using the midpoint of the two previous values.  Depending
 *	upon the sign of the function at the interval midpoint,
 *	the midpoint replaces either the lower interval value
 *	(if f(midpoint) &lt; 0) or the upper interval value
 *	(if f(midpoint) &gt; 0).  This bisection process halves the
 *	search interval on each iteration
 *	</p>
 *
 *	<p>
 *	If the function whose root is being sought has a derivative
 *	at each point in the interval, the Method of Secants or
 *	Brent's Method is a better choice.
 *	</p>
 */

public class Bisection implements MonadicFunctionRootFinder
{
	/**	Find root using the Method of Bisection.
	 *
	 *	@param	x0						First approximation to root value.
	 *	@param	x1						Second approximation to root value.
	 *	@param	tol						Desired accuracy for root value.
	 *	@param	maxIter					Maximum number of iterations.
	 *	@param	function				Class implementing MonadicFunction
	 *									interface to provide function values.
	 *	@param	convergenceTest			RootFinderConvergenceTest which
 	 *									tests for convergence of the root-finding
 	 *									process.
	 *	@param	iterationInformation	Class implementing
	 *									RootFinderIterationInformation
	 *									for retrieving information about
	 *									each iteration of root finding
	 *									process.  Set to null if you don't
	 *									want this information.
	 *
	 *	@return							Approximation to root of function.
	 *
	 *	@throws							IllegalArgumentException
	 *										if [x0,x1] cannot be expanded
	 *										to bracket a root or function
	 *										is null.
	 *
	 *	<p>
	 *	This implementation always starts by attempting to expand the root
	 *	bracketing interval to enclose a root.
	 *	</p>
	 */

	public static double bisection
	(
		double x0 ,
		double x1 ,
		double tol ,
		int maxIter ,
		MonadicFunction function ,
		RootFinderConvergenceTest convergenceTest ,
		RootFinderIterationInformation iterationInformation
	)
		throws IllegalArgumentException
	{
		/* Calculated value of x at each iteration. */

		double x;

		/* Function value at x0 . */

		double f0;

		/* Function value at x1 . */

		double f1;

		/* Function value at calculated value of x . */

		double fx;

		/* Ratio of function values at two successive approximants. */

		double r;

		/* Root, if within desired tolerance. */

		double root;
								// Make sure function is not null.

		if ( function == null )
		{
			throw new IllegalArgumentException(
				"Function cannot be null" );
		}
								// Set initial function values.

		f0 = function.f( x0 );
		f1 = function.f( x1 );

								// Test if there is a root in the
								// provided interval.
								// For this to be true, the function values
								// at the left and right end of the interval
								// must have different signs.  If the signs
								// are the same, try expanding the interval
								// geometrically and see if we can find a
								// new interval bracketing the root.

		if (	( ( f0 > 0.0 ) && ( f1 > 0.0 ) ) ||
				( ( f0 < 0.0 ) && ( f1 < 0.0 ) ) )
		{
			double[] bracket	= new double[]{ x0 , x1 };

			if ( !BracketRoot.bracketRoot( bracket, function, maxIter, 1.6 ) )
			{
								// Give up if we can't find a new interval
								// bracketing a root.

				throw new IllegalArgumentException(
					"Cannot expand interval [x0,x1] to contain root." );
			}
								// Use new bracketing interval.
			else
			{
				x0	= bracket[ 0 ];
				x1	= bracket[ 1 ];
				f0	= function.f( x0 );
				f1	= function.f( x1 );
			}
        }
								// Begin method of secants loop.
		x	= 0.0D;

		for( int iter = 0; iter < maxIter; iter++ )
		{
								// Compute new approximant at midpoint of
								// previous two approximants.

			x	= ( x0 + x1 ) / 2.0D;
			fx	= function.f( x );

								// Post updated iteration information.

			if ( iterationInformation != null )
			{
				iterationInformation.iterationInformation(
					x , fx , Double.NaN , iter );
			}
								// Check if new approximant is accurate enough.

			if	( convergenceTest.converged( x1 , x0 , fx , tol , tol ) ) break;

								//	Update root estimate if convergence
								//	not yet achieved.

			if ( ( fx * f0 ) > 0.0D )
			{
				x0	= x;
				f0	= fx;
			}
			else
			{
				x1	= x;
				f1	= fx;
			}
		}

		return x;
	}

	/**	Find root using the Method of Bisection.
	 *
	 *	@param	x0						First approximation to root value.
	 *	@param	x1						Second approximation to root value.
	 *	@param	tol						Desired accuracy for root value.
	 *	@param	maxIter					Maximum number of iterations.
	 *	@param	function				Class implementing MonadicFunction
	 *									interface to provide function values.
	 *
	 *	@return							Approximation to root of function.
	 *
	 *	@throws							IllegalArgumentException
	 *										if [x0,x1] cannot be expanded
	 *										to bracket a root or function
	 *										is null.
	 *
	 *	<p>
	 *	This implementation always starts by attempting to expand the root
	 *	bracketing interval to enclose a root.
	 *	</p>
	 */

	public static double bisection
	(
		double x0 ,
		double x1 ,
		double tol ,
		int maxIter ,
		MonadicFunction function
	)
		throws IllegalArgumentException
	{
		return bisection(
			x0 , x1 , tol , maxIter , function ,
			new StandardRootFinderConvergenceTest() ,
			null );
	}

	/**	Find root using the Method of Bisection.
	 *
	 *	@param	x0						First approximation to root value.
	 *	@param	x1						Second approximation to root value.
	 *	@param	function				Class implementing MonadicFunction
	 *									interface to provide function values.
	 *
	 *	@return							Approximation to root of function.
	 *
	 *	@throws							IllegalArgumentException
	 *										if [x0,x1] cannot be expanded
	 *										to bracket a root or function
	 *										is null.
	 *
	 *	<p>
	 *	This implementation always starts by attempting to expand the root
	 *	bracketing interval to enclose a root.  Up to 250 iterations are
	 *	attempted with the convergence tolerance set to Constants.MACHEPS .
	 *	</p>
	 */

	public static double bisection
	(
		double x0 ,
		double x1 ,
		MonadicFunction function
	)
		throws IllegalArgumentException
	{
		return bisection(
			x0 , x1 , Constants.MACHEPS , 250 , function ,
			new StandardRootFinderConvergenceTest() ,
			null );
	}

	/** Implementation for {@link MonadicFunctionRootFinder} interface.
	 */

	public double findRoot
	(
		double x0 ,
		double x1 ,
		double tol ,
		int maxIter ,
		MonadicFunction function ,
		MonadicFunction derivativeFunction ,
		RootFinderConvergenceTest convergenceTest ,
		RootFinderIterationInformation iterationInformation
	)
		throws IllegalArgumentException
	{
		return bisection(
			x0 , x1 , tol , maxIter , function , convergenceTest ,
			iterationInformation );
	}

	/** Constructor if RootFinder interface used.
	 */

	public Bisection()
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

