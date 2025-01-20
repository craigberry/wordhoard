package edu.northwestern.at.utils.math.statistics;

/*	Please see the license information in the header below. */

/*
 Copyright � 1999 CERN - European Organization for Nuclear Research.
 Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 is hereby granted without fee, provided that the above copyright notice appear in all copies and
 that both that copyright notice and this permission notice appear in supporting documentation.
 CERN makes no representations about the suitability of this software for any purpose.
 It is provided "as is" without expressed or implied warranty.
 */

/*	Moved to Northwestern class hierarchy and modified to use simple array types
 *	instead of Cern list types by Philip R. "Pib" Burns.  2004/06/24 .
 *	Other minor changes added by Pib on 2004/06/25 .
 */

import java.util.*;
import edu.northwestern.at.utils.math.*;
import edu.northwestern.at.utils.math.distributions.*;

/**
 * Basic descriptive statistics.
 *
 * @author peter.gedeck@pharma.Novartis.com
 * @author wolfgang.hoschek@cern.ch
 * @version 0.91, 08-Dec-99
 */

public class Descriptive extends Object
{
	/**
	 * Makes this class non instantiable, but still let's others inherit from it.
	 */

	protected Descriptive()
	{
	}

	/**
	 * Returns the auto-correlation of a data sequence.
	 * @param data input data sequence.
	 * @param lag upper bound of data sequence.
	 * @param mean mean of data sequence.
	 * @param variance variance of data sequence.
	 * @return the auto-correlation of the data sequence.
	 */

	public static double autoCorrelation(double[] data, int lag, double mean, double variance)
	{
		int N = data.length;

		if (lag >= N) throw new IllegalArgumentException("Lag is too large");

		double run = 0;

		for (int i = lag; i < N; ++i)
			run += (data[i] - mean) * (data[i - lag] - mean);

		return (run / (N - lag)) / variance;
	}

	/**
	 * Checks if the given range is within the contained array's bounds.
	 * @param from lower bound of range.
	 * @param to upper boudn of range.
	 * @param theSize size of array.
	 * @throws IndexOutOfBoundsException if <code>to!=from-1 || from&lt;0 || from&gt;to || to&gt;=size()</code>.
	 */

	protected static void checkRangeFromTo(int from, int to, int theSize)
	{
		if (to == from - 1) return;
		if (from < 0 || from > to || to >= theSize)
			throw new IndexOutOfBoundsException("from: " + from + ", to: " + to + ", size=" + theSize);
	}

	/**
	 * Returns the correlation of two data sequences.
	 * That is <code>covariance(data1,data2)/(standardDev1*standardDev2)</code>.
	 * @param data1 first data sequence.
	 * @param standardDev1 standard deviation of first data sequence.
	 * @param data2 second data sequence.
	 * @param standardDev2 standard deviation of second data sequence.
	 * @return the correlation of the two data sequences.
	 */

	public static double correlation(double[] data1, double standardDev1, double[] data2, double standardDev2)
	{
		return covariance(data1, data2) / (standardDev1 * standardDev2);
	}

	/**
	 * Returns the covariance of two data sequences, which is
	 * <code>cov(x,y) = (1/(size()-1)) * Sum((x[i]-mean(x)) * (y[i]-mean(y)))</code>.
	 * See the <A HREF="http://www.cquest.utoronto.ca/geog/ggr270y/notes/not05efg.html"> math definition</A>.
	 * @param data1 first data sequence.
	 * @param data2 second data sequence.
	 * @return the covariance of the two data sequences.
	 */

	public static double covariance(double[] data1, double[] data2)
	{
		int size = data1.length;

		if (size != data2.length || size == 0) throw new IllegalArgumentException();

		double sumx = data1[0], sumy = data2[0], Sxy = 0;

		for (int i = 1; i < size; ++i)
		{
			double x = data1[i];
			double y = data2[i];

			sumx += x;
			Sxy += (x - sumx / (i + 1)) * (y - sumy / i);
			sumy += y;
			// Exercise for the reader: Why does this give us the right answer?
		}
		return Sxy / (size - 1);
	}

	/*
	 * Both covariance versions yield the same results but the one above is faster
	 */

	private static double covariance2(double[] data1, double[] data2)
	{
		int size = data1.length;
		double mean1 = Descriptive.mean(data1);
		double mean2 = Descriptive.mean(data2);
		double covariance = 0.0D;

		for (int i = 0; i < size; i++)
		{
			double x = data1[ i ];
			double y = data2[ i ];

			covariance += (x - mean1) * (y - mean2);
		}

		return covariance / (double) (size - 1);
	}

	/**
	 * Durbin-Watson computation.
	 * @param data input data sequence.
	 * @return Durbin-Watson computation on data sequence.
	 */

	public static double durbinWatson(double[] data)
	{
		int size = data.length;

		if (size < 2) throw new IllegalArgumentException("data sequence must contain at least two values.");

		double run = 0;
		double run_sq = 0;

		run_sq = data[0] * data[0];
		for (int i = 1; i < size; ++i)
		{
			double x = data[i] - data[i - 1];

			run += x * x;
			run_sq += data[i] * data[i];
		}

		return run / run_sq;
	}

	/**
	 * Computes the frequency (number of occurances, count) of each distinct value in the given sorted data.
	 * After this call returns both <code>distinctValues</code> and <code>frequencies</code> have a new size (which is equal for both), which is the number of distinct values in the sorted data.
	 * <p>
	 * Distinct values are filled into <code>distinctValues</code>, starting at index 0.
	 * The frequency of each distinct value is filled into <code>frequencies</code>, starting at index 0.
	 * As a result, the smallest distinct value (and its frequency) can be found at index 0, the second smallest distinct value (and its frequency) at index 1, ..., the largest distinct value (and its frequency) at index <code>distinctValues.size()-1</code>.
	 *
	 * <b>Example:</b>
	 * <br>
	 * <code>elements = (5,6,6,7,8,8) --&gt; distinctValues = (5,6,7,8), frequencies = (1,2,1,2)</code>
	 *
	 * @param sortedData the data; must be sorted ascending.
	 * @param distinctValues a list to be filled with the distinct values; can have any size.
	 * @param frequencies      a list to be filled with the frequencies; can have any size; set this parameter to <code>null</code> to ignore it.
	 */

	public static void frequencies
	(
		double[] sortedData,
		ArrayList distinctValues,
		ArrayList frequencies
	)
	{
		distinctValues.clear();
		if (frequencies != null) frequencies.clear();

		int size = sortedData.length;
		int i = 0;

		while (i < size)
		{
			double element = sortedData[i];
			int cursor = i;

			// determine run length (number of equal elements)
			while (++i < size && sortedData[i] == element);

			int runLength = i - cursor;

			distinctValues.add( Double.valueOf( element ) );
			if (frequencies != null) frequencies.add( Integer.valueOf( runLength ) );
		}
	}

	/**
	 * Returns the geometric mean of a data sequence.
	 * Note that for a geometric mean to be meaningful, the minimum of the data sequence must not be less or equal to zero.
	 * <br>
	 * The geometric mean is given by <code>pow( Product( data[i] ), 1/size)</code>
	 * which is equivalent to <code>Math.exp( Sum( Log(data[i]) ) / size)</code>.
	 * @param size the number of elements in the data sequence.
	 * @param sumOfLogarithms sum of logarithms of the data sequence.
	 * @return geometric mean of the data sequence.
	 */

	public static double geometricMean(int size, double sumOfLogarithms)
	{
		return Math.exp(sumOfLogarithms / size);

		// this version would easily results in overflows
		// return Math.pow(product, 1/size);
	}

	/**
	 * Returns the geometric mean of a data sequence.
	 * Note that for a geometric mean to be meaningful, the minimum of the data sequence must not be less or equal to zero.
	 * <br>
	 * The geometric mean is given by <code>pow( Product( data[i] ), 1/data.length)</code>.
	 * This method tries to avoid overflows at the expense of an equivalent but somewhat slow definition:
	 * <code>geo = Math.exp( Sum( Log(data[i]) ) / data.length)</code>.
	 * @param data input data sequence.
	 * @return the geometric mean of the data sequence.
	 */

	public static double geometricMean(double[] data)
	{
		return geometricMean(data.length, sumOfLogarithms(data, 0, data.length - 1));
	}

	/**
	 * Returns the harmonic mean of a data sequence.
	 *
	 * @param size the number of elements in the data sequence.
	 * @param sumOfInversions <code>Sum( 1.0 / data[i])</code>.
	 * @return the harmonic mean of a data sequence.
	 */

	public static double harmonicMean(int size, double sumOfInversions)
	{
		return size / sumOfInversions;
	}

	/**
	 * Incrementally maintains and updates minimum, maximum, sum and sum of squares of a data sequence.
	 *
	 * Assume we have already recorded some data sequence elements
	 * and know their minimum, maximum, sum and sum of squares.
	 * Assume further, we are to record some more elements
	 * and to derive updated values of minimum, maximum, sum and sum of squares.
	 * <p>
	 * This method computes those updated values without needing to know the already recorded elements.
	 * This is interesting for interactive online monitoring and/or applications that cannot keep the entire huge data sequence in memory.
	 * <p>
	 * <br>Definition of sumOfSquares: <code>sumOfSquares(n) = Sum ( data[i] * data[i] )</code>.
	 *
	 *
	 * @param data the additional elements to be incorporated into min, max, etc.
	 * @param from the index of the first element within <code>data</code> to consider.
	 * @param to the index of the last element within <code>data</code> to consider.
	 * The method incorporates elements <code>data[from], ..., data[to]</code>.
	 * @param inOut the old values in the following format:
	 * <ul>
	 * <li><code>inOut[0]</code> is the old minimum.
	 * <li><code>inOut[1]</code> is the old maximum.
	 * <li><code>inOut[2]</code> is the old sum.
	 * <li><code>inOut[3]</code> is the old sum of squares.
	 * </ul>
	 * If no data sequence elements have so far been recorded set the values as follows
	 * <ul>
	 * <li><code>inOut[0] = Double.POSITIVE_INFINITY</code> as the old minimum.
	 * <li><code>inOut[1] = Double.NEGATIVE_INFINITY</code> as the old maximum.
	 * <li><code>inOut[2] = 0.0</code> as the old sum.
	 * <li><code>inOut[3] = 0.0</code> as the old sum of squares.
	 * </ul>
	 *
	 * <p>
	 * Returns the updated values filled into the <code>inOut</code> array.
	 * </p>
	 */

	public static void incrementalUpdate
	(
		double[] data,
		int from,
		int to,
		double[] inOut
	)
	{
		checkRangeFromTo( from, to, data.length );

		// read current values
		double min = inOut[0];
		double max = inOut[1];
		double sum = inOut[2];
		double sumSquares = inOut[3];

		for (; from <= to; from++)
		{
			double element = data[from];

			sum += element;
			sumSquares += element * element;
			if (element < min) min = element;
			if (element > max) max = element;

			/*
			 double oldDeviation = element - mean;
			 mean += oldDeviation / (N+1);
			 sumSquaredDeviations += (element-mean)*oldDeviation; // cool, huh?
			 */

			/*
			 double oldMean = mean;
			 mean += (element - mean)/(N+1);
			 if (N > 0) {
			 sumSquaredDeviations += (element-mean)*(element-oldMean); // cool, huh?
			 }
			 */

		}

		// store new values
		inOut[0] = min;
		inOut[1] = max;
		inOut[2] = sum;
		inOut[3] = sumSquares;

		// At this point of return the following postcondition holds:
		// data.length-from elements have been consumed by this call.
	}

	/**
	 * Incrementally maintains and updates various sums of powers of the form <code>Sum(data[i]<sup>k</sup>)</code>.
	 *
	 * Assume we have already recorded some data sequence elements <code>data[i]</code>
	 * and know the values of <code>Sum(data[i]<sup>from</sup>), Sum(data[i]<sup>from+1</sup>), ..., Sum(data[i]<sup>to</sup>)</code>.
	 * Assume further, we are to record some more elements
	 * and to derive updated values of these sums.
	 * <p>
	 * This method computes those updated values without needing to know the already recorded elements.
	 * This is interesting for interactive online monitoring and/or applications that cannot keep the entire huge data sequence in memory.
	 * For example, the incremental computation of moments is based upon such sums of powers:
	 * <p>
	 * The moment of <code>k</code>-th order with constant <code>c</code> of a data sequence,
	 * is given by <code>Sum( (data[i]-c)<sup>k</sup> ) / data.length</code>.
	 * It can incrementally be computed by using the equivalent formula
	 * <p>
	 * <code>moment(k,c) = m(k,c) / data.length</code> where
	 * <br><code>m(k,c) = Sum( -1<sup>i</sup> * b(k,i) * c<sup>i</sup> * sumOfPowers(k-i))</code> for <code>i = 0 .. k</code> and
	 * <br><code>b(k,i) = </code>{@link edu.northwestern.at.utils.math.ArithUtils#binomial(long,long) binomial(k,i)} and
	 * <br><code>sumOfPowers(k) = Sum( data[i]<sup>k</sup> )</code>.
	 * <p>
	 * @param data the additional elements to be incorporated into min, max, etc.
	 * @param from the index of the first element within <code>data</code> to consider.
	 * @param to the index of the last element within <code>data</code> to consider.
	 * @param fromSumIndex the old from index.
	 * @param toSumIndex the old to index.
	 * @param sumOfPowers existing sum of powers on input and updated sum of powers
	 * on output.
	 * The method incorporates elements <code>data[from], ..., data[to]</code>.
	 *
	 * <ul>
	 * <li><code>sumOfPowers[0]</code> is the old <code>Sum(data[i]<sup>fromSumIndex</sup>)</code>.
	 * <li><code>sumOfPowers[1]</code> is the old <code>Sum(data[i]<sup>fromSumIndex+1</sup>)</code>.
	 * <li>...
	 * <li><code>sumOfPowers[toSumIndex-fromSumIndex]</code> is the old <code>Sum(data[i]<sup>toSumIndex</sup>)</code>.
	 * </ul>
	 * If no data sequence elements have so far been recorded set all old values of the sums to <code>0.0</code>.
	 *
	 * <p>
	 * Returns the updated values filled into the <code>sumOfPowers</code> array.
	 * </p>
	 */

	public static void incrementalUpdateSumsOfPowers
	(
		double[] data,
		int from,
		int to,
		int fromSumIndex,
		int toSumIndex,
		double[] sumOfPowers
	)
	{
		int size = data.length;
		int lastIndex = toSumIndex - fromSumIndex;

		if (from > size || lastIndex + 1 > sumOfPowers.length) throw new IllegalArgumentException();

		// optimized for common parameters
		if (fromSumIndex == 1)
		{ // handle quicker
			if (toSumIndex == 2)
			{
				double sum = sumOfPowers[0];
				double sumSquares = sumOfPowers[1];

				for (int i = from - 1; ++i <= to;)
				{
					double element = data[i];

					sum += element;
					sumSquares += element * element;
					// if (element < min) min = element;
					// else if (element > max) max = element;
				}
				sumOfPowers[0] += sum;
				sumOfPowers[1] += sumSquares;
				return;
			}
			else if (toSumIndex == 3)
			{
				double sum = sumOfPowers[0];
				double sumSquares = sumOfPowers[1];
				double sum_xxx = sumOfPowers[2];

				for (int i = from - 1; ++i <= to;)
				{
					double element = data[i];

					sum += element;
					sumSquares += element * element;
					sum_xxx += element * element * element;
					// if (element < min) min = element;
					// else if (element > max) max = element;
				}
				sumOfPowers[0] += sum;
				sumOfPowers[1] += sumSquares;
				sumOfPowers[2] += sum_xxx;
				return;
			}
			else if (toSumIndex == 4)
			{ // handle quicker
				double sum = sumOfPowers[0];
				double sumSquares = sumOfPowers[1];
				double sum_xxx = sumOfPowers[2];
				double sum_xxxx = sumOfPowers[3];

				for (int i = from - 1; ++i <= to;)
				{
					double element = data[i];

					sum += element;
					sumSquares += element * element;
					sum_xxx += element * element * element;
					sum_xxxx += element * element * element * element;
					// if (element < min) min = element;
					// else if (element > max) max = element;
				}
				sumOfPowers[0] += sum;
				sumOfPowers[1] += sumSquares;
				sumOfPowers[2] += sum_xxx;
				sumOfPowers[3] += sum_xxxx;
				return;
			}
		}

		if (fromSumIndex == toSumIndex || (fromSumIndex >= -1 && toSumIndex <= 5))
		{ // handle quicker
			for (int i = fromSumIndex; i <= toSumIndex; i++)
			{
				sumOfPowers[i - fromSumIndex] += sumOfPowerDeviations(data, i, 0.0, from, to);
			}
			return;
		}

		// now the most general case:
		// optimized for maximum speed, but still not quite quick

		for (int i = from - 1; ++i <= to;)
		{
			double element = data[i];
			double pow = Math.pow(element, fromSumIndex);

			int j = 0;

			for (int m = lastIndex; --m >= 0;)
			{
				sumOfPowers[j++] += pow;
				pow *= element;
			}
			sumOfPowers[j] += pow;
		}

		// At this point of return the following postcondition holds:
		// data.length-fromIndex elements have been consumed by this call.
	}

	/**
	 * Incrementally maintains and updates sum and sum of squares of a <i>weighted</i> data sequence.
	 *
	 * Assume we have already recorded some data sequence elements
	 * and know their sum and sum of squares.
	 * Assume further, we are to record some more elements
	 * and to derive updated values of sum and sum of squares.
	 * <p>
	 * This method computes those updated values without needing to know the already recorded elements.
	 * This is interesting for interactive online monitoring and/or applications that cannot keep the entire huge data sequence in memory.
	 * <p>
	 * <br>Definition of sum: <code>sum = Sum ( data[i] * weights[i] )</code>.
	 * <br>Definition of sumOfSquares: <code>sumOfSquares = Sum ( data[i] * data[i] * weights[i])</code>.
	 *
	 *
	 * @param data the additional elements to be incorporated into min, max, etc.
	 * @param weights the weight of each element within <code>data</code>.
	 * @param from the index of the first element within <code>data</code> (and <code>weights</code>) to consider.
	 * @param to the index of the last element within <code>data</code> (and <code>weights</code>) to consider.
	 * The method incorporates elements <code>data[from], ..., data[to]</code>.
	 * @param inOut the old values in the following format:
	 * <ul>
	 * <li><code>inOut[0]</code> is the old sum.
	 * <li><code>inOut[1]</code> is the old sum of squares.
	 * </ul>
	 * If no data sequence elements have so far been recorded set the values as follows
	 * <ul>
	 * <li><code>inOut[0] = 0.0</code> as the old sum.
	 * <li><code>inOut[1] = 0.0</code> as the old sum of squares.
	 * </ul>
	 *
	 * <p>
	 * Returns the updated values filled into the <code>inOut</code> array.
	 * </p>
	 */

	public static void incrementalWeightedUpdate
	(
		double[] data,
		double[] weights,
		int from,
		int to,
		double[] inOut
	)
	{
		int dataSize = data.length;

		checkRangeFromTo(from, to, dataSize);

		if ( dataSize != weights.length )
		 throw new IllegalArgumentException(
		 	"from=" + from + ", to=" + to + ", data.length=" + dataSize +
		 		", weights.length=" + weights.length );

		// read current values
		double sum = inOut[0];
		double sumOfSquares = inOut[1];

		for (int i = from - 1; ++i <= to;)
		{
			double element = data[i];
			double weight = weights[i];
			double prod = element * weight;

			sum += prod;
			sumOfSquares += element * prod;
		}

		// store new values
		inOut[0] = sum;
		inOut[1] = sumOfSquares;

		// At this point of return the following postcondition holds:
		// data.length-from elements have been consumed by this call.
	}

	/**
	 * Returns the kurtosis (aka excess) of a data sequence.
	 * @param moment4 the fourth central moment, which is <code>moment(data,4,mean)</code>.
	 * @param standardDeviation the standardDeviation.
	 * @return kurtosis of data sequence.
	 */

	public static double kurtosis(double moment4, double standardDeviation)
	{
		return -3 + moment4 / (standardDeviation * standardDeviation * standardDeviation * standardDeviation);
	}

	/**
	 * Returns the kurtosis (aka excess) of a data sequence, which is <code>-3 + moment(data,4,mean) / standardDeviation<sup>4</sup></code>.
	 * @param data input data sequence.
	 * @param mean mean of data sequence.
	 * @param standardDeviation standard deviation of data sequence.
	 * @return kurtosis of data sequence.
	 */

	public static double kurtosis(double[] data, double mean, double standardDeviation)
	{
		return kurtosis(moment(data, 4, mean), standardDeviation);
	}

	/**
	 * Returns the lag-1 autocorrelation of a dataset;
	 * Note that this method has semantics different from <code>autoCorrelation(..., 1)</code>;
	 * @param data input dataset.
	 * @param mean mean value of dataset.
	 * @return lag-1 autocorrelation of dataset.
	 */

	public static double lag1(double[] data, double mean)
	{
		int size = data.length;
		double r1;
		double q = 0;
		double v = (data[0] - mean) * (data[0] - mean);

		for (int i = 1; i < size; i++)
		{
			double delta0 = (data[i - 1] - mean);
			double delta1 = (data[i] - mean);

			q += (delta0 * delta1 - q) / (i + 1);
			v += (delta1 * delta1 - v) / (i + 1);
		}

		r1 = q / v;
		return r1;
	}

	/**
	 * Returns the largest member of a data sequence.
	 * @param data input data sequence.
	 * @return largest member of data sequence.
	 */

	public static double max(double[] data)
	{
		int size = data.length;

		if (size == 0) throw new IllegalArgumentException();

		double max = data[size - 1];

		for (int i = size - 1; --i >= 0;)
		{
			if (data[i] > max) max = data[i];
		}

		return max;
	}

	/**
	 * Returns the arithmetic mean of a data sequence;
	 * That is <code>Sum( data[i] ) / data.length</code>.
	 * @param data input data sequence.
	 * @return mean of data sequence.
	 */

	public static double mean( double[] data )
	{
		return sum( data ) / data.length;
	}

	/**
	 * Returns the mean deviation of a dataset.
	 * That is <code>Sum (Math.abs(data[i]-mean)) / data.length)</code>.
	 * @param data input data set.
	 * @param mean mean of data set.
	 * @return mean deviation.
	 */

	public static double meanDeviation( double[] data , double mean )
	{
		int size = data.length;
		double sum = 0;

		for (int i = size; --i >= 0;) sum += Math.abs(data[i] - mean);
		return sum / size;
	}

	/**
	 * Returns the median of a sorted data sequence.
	 *
	 * @param sortedData the data sequence; <b>must be sorted ascending</b>.
	 * @return median of sortedData.
	 */

	public static double median( double[] sortedData )
	{
		return quantile( sortedData , 0.5 );

		/*
		 double[] sortedElements = sortedData.elements();
		 int n = sortedData.size();
		 int lhs = (n - 1) / 2 ;
		 int rhs = n / 2 ;

		 if (n == 0) return 0.0 ;

		 double median;
		 if (lhs == rhs) median = sortedElements[lhs] ;
		 else median = (sortedElements[lhs] + sortedElements[rhs])/2.0 ;

		 return median;
		 */
	}

	/**
	 * Returns the smallest member of a data sequence.
	 * @param data input data sequence.
	 * @return minimum value of data sequence.
	 */

	public static double min( double[] data )
	{
		int size = data.length;

		if (size == 0) throw new IllegalArgumentException();

		double min = data[size - 1];

		for (int i = size - 1; --i >= 0;)
		{
			if (data[i] < min) min = data[i];
		}

		return min;
	}

	/**
	 * Returns the moment of <code>k</code>-th order with constant <code>c</code> of a data sequence,
	 * which is <code>Sum( (data[i]-c)<sup>k</sup> ) / data.length</code>.
	 *
	 * @param k order parameter
	 * @param c constant parameter
	 * @param size the number of elements of the data sequence.
	 * @param sumOfPowers <code>sumOfPowers[m] == Sum( data[i]<sup>m</sup>) )</code> for <code>m = 0,1,..,k</code> as returned by method {@link #incrementalUpdateSumsOfPowers(double[],int,int,int,int,double[])}.
	 *			In particular there must hold <code>sumOfPowers.length == k+1</code>.

	 * @return result of the moment calculation
	 */

	public static double moment( int k, double c, int size, double[] sumOfPowers )
	{
		double sum = 0;
		int sign = 1;

		for (int i = 0; i <= k; i++)
		{
			double y;

			if (i == 0) y = 1;
			else if (i == 1) y = c;
			else if (i == 2) y = c * c;
			else if (i == 3) y = c * c * c;
			else y = Math.pow(c, i);
			// sum += sign *
			sum += sign * ArithUtils.binomial(k, i) * y * sumOfPowers[k - i];
			sign = -sign;
		}

		/*
		 for (int i=0; i<=k; i++) {
		 sum += sign * Arithmetic.binomial(k,i) * Math.pow(c, i) * sumOfPowers[k-i];
		 sign = -sign;
		 }
		 */
		return sum / size;
	}

	/**
	 * Returns the moment of <code>k</code>-th order with constant <code>c</code> of a data sequence,
	 * which is <code>Sum( (data[i]-c)<sup>k</sup> ) / data.length</code>.
	 * @param data data sequence parameter
	 * @param k order parameter
	 * @param c constant parameter
	 * @return result of the moment calculation
	 */

	public static double moment( double[] data, int k, double c )
	{
		return sumOfPowerDeviations( data, k, c ) / data.length;
	}

	/**
	 * Returns the pooled mean of two data sequences.
	 * That is <code>(size1 * mean1 + size2 * mean2) / (size1 + size2)</code>.
	 *
	 * @param size1 the number of elements in data sequence 1.
	 * @param mean1 the mean of data sequence 1.
	 * @param size2 the number of elements in data sequence 2.
	 * @param mean2 the mean of data sequence 2.
	 * @return the pooled mean of the two data sequences.
	 */

	public static double pooledMean(int size1, double mean1, int size2, double mean2)
	{
		return (size1 * mean1 + size2 * mean2) / (size1 + size2);
	}

	/**
	 * Returns the pooled variance of two data sequences.
	 * That is <code>(size1 * variance1 + size2 * variance2) / (size1 + size2)</code>;
	 *
	 * @param size1 the number of elements in data sequence 1.
	 * @param variance1 the variance of data sequence 1.
	 * @param size2 the number of elements in data sequence 2.
	 * @param variance2 the variance of data sequence 2.
	 * @return the pooled variance of the two data sequences.
	 */

	public static double pooledVariance(int size1, double variance1, int size2, double variance2)
	{
		return (size1 * variance1 + size2 * variance2) / (size1 + size2);
	}

	/**
	 * Returns the product, which is <code>Prod( data[i] )</code>.
	 * In other words: <code>data[0]*data[1]*...*data[data.length-1]</code>.
	 * This method uses the equivalent definition:
	 * <code>prod = pow( exp( Sum( Log(x[i]) ) / size(), size())</code>.
	 * @param size of sequence.
	 * @param sumOfLogarithms the product calculated by summing logarithms.
	 * @return the product of the data sequence.
	 */

	public static double product(int size, double sumOfLogarithms)
	{
		return Math.pow(Math.exp(sumOfLogarithms / size), size);
	}

	/**
	 * Returns the product of a data sequence, which is <code>Prod( data[i] )</code>.
	 * In other words: <code>data[0]*data[1]*...*data[data.length-1]</code>.
	 * Note that you may easily get numeric overflows.
	 * @param data input data sequence.
	 * @return product of data sequence.
	 */

	public static double product(double[] data)
	{
		int size = data.length;

		double product = 1;

		for (int i = size; --i >= 0;) product *= data[i];

		return product;
	}

	/**
	 * Returns the <code>phi-</code>quantile; that is, an element <code>elem</code> for which holds that <code>phi</code> percent of data elements are less than <code>elem</code>.
	 * The quantile need not necessarily be contained in the data sequence, it can be a linear interpolation.
	 * @param sortedData the data sequence; <b>must be sorted ascending</b>.
	 * @param phi the percentage; must satisfy <code>0 &lt;= phi &lt;= 1</code>.
	 * @return the <code>phi-</code> quantile of sortedData.
	 */

	public static double quantile(double[] sortedData, double phi)
	{
		int n = sortedData.length;

		double index = phi * (n - 1);
		int lhs = (int) index;
		double delta = index - lhs;
		double result;

		if (n == 0) return 0.0;

		if (lhs == n - 1)
		{
			result = sortedData[lhs];
		}
		else
		{
			result = (1 - delta) * sortedData[lhs] + delta * sortedData[lhs + 1];
		}

		return result;
	}

	/**
	 * Returns how many percent of the elements contained in the receiver are <code>&lt;= element</code>.
	 * Does linear interpolation if the element is not contained but lies in between two contained elements.
	 *
	 * @param sortedList the list to be searched (must be sorted ascending).
	 * @param element the element to search for.
	 * @return the percentage <code>phi</code> of elements <code>&lt;= element</code> (<code>0.0 &lt;= phi &lt;= 1.0)</code>.
	 */

	public static double quantileInverse(double[] sortedList, double element)
	{
		return rankInterpolated(sortedList, element) / sortedList.length;
	}

	/**
	 * Returns the quantiles of the specified percentages.
	 * The quantiles need not necessarily be contained in the data sequence, it can be a linear interpolation.
	 * @param sortedData the data sequence; <b>must be sorted ascending</b>.
	 * @param percentages the percentages for which quantiles are to be computed.
	 * Each percentage must be in the interval <code>[0.0,1.0]</code>.
	 * @return the quantiles.
	 */

	public static double[] quantiles(double[] sortedData, double[] percentages)
	{
		int s = percentages.length;

		double[] quantiles = new double[ s ];

		for (int i = 0; i < s; i++)
		{
			quantiles[ i ] = quantile( sortedData , percentages[ i ] );
		}

		return quantiles;
	}

	/**
	 * Returns the linearly interpolated number of elements in a list less or equal to a given element.
	 * The rank is the number of elements &lt;= element.
	 * Ranks are of the form <code>{0, 1, 2,..., sortedList.size()}</code>.
	 * If no element is &lt;= element, then the rank is zero.
	 * If the element lies in between two contained elements, then linear interpolation is used and a non integer value is returned.
	 *
	 * @param sortedList the list to be searched (must be sorted ascending).
	 * @param element the element to search for.
	 * @return the rank of the element.
	 */

	public static double rankInterpolated( double[] sortedList, double element )
	{
		int index = Arrays.binarySearch( sortedList , element );

		if (index >= 0)
		{ // element found
			// skip to the right over multiple occurances of element.
			int to = index + 1;
			int s = sortedList.length;

			while (to < s && sortedList[ to ] == element) to++;
			return to;
		}

		// element not found
		int insertionPoint = -index - 1;

		if (insertionPoint == 0 || insertionPoint == sortedList.length) return insertionPoint;

		double from = sortedList[ insertionPoint - 1 ];
		double to = sortedList[ insertionPoint ];
		double delta = (element - from) / (to - from); // linear interpolation

		return insertionPoint + delta;
	}

	/**
	 * Returns the RMS (Root-Mean-Square) of a data sequence.
	 * That is <code>Math.sqrt(Sum( data[i]*data[i] ) / data.length)</code>.
	 * The RMS of data sequence is the square-root of the mean of the squares of the elements in the data sequence.
	 * It is a measure of the average "size" of the elements of a data sequence.
	 *
	 * @param sumOfSquares <code>sumOfSquares(data) == Sum( data[i]*data[i] )</code> of the data sequence.
	 * @param size the number of elements in the data sequence.
	 * @return Root-Mean-Square of data sequence.
	 */

	public static double rms(int size, double sumOfSquares)
	{
		return Math.sqrt(sumOfSquares / size);
	}

	/**
	 * Returns the sample kurtosis (aka excess) of a data sequence.
	 *
	 * Ref: R.R. Sokal, F.J. Rohlf, Biometry: the principles and practice of statistics
	 * in biological research (W.H. Freeman and Company, New York, 1998, 3rd edition)
	 * p. 114-115.
	 *
	 * @param size the number of elements of the data sequence.
	 * @param moment4 the fourth central moment, which is <code>moment(data,4,mean)</code>.
	 * @param sampleVariance the <b>sample variance</b>.
	 * @return sample kurtosis.
	 */

	public static double sampleKurtosis(int size, double moment4, double sampleVariance)
	{
		int    n = size;
		double s2 = sampleVariance; // (y-ymean)^2/(n-1)
		double m4 = moment4 * n;    // (y-ymean)^4

		return m4 * n * (n + 1) / ((n - 1) * (n - 2) * (n - 3) * s2 * s2)
			- 3.0 * (n - 1) * (n - 1) / ((n - 2) * (n - 3));
	}

	/**
	 * Returns the sample kurtosis (aka excess) of a data sequence.
	 * @param data Input data
	 * @param mean used to calculate moment of input data
	 * @param sampleVariance sample variance of input data
	 * @return sample kurtosis.
	 */

	public static double sampleKurtosis(double[] data, double mean, double sampleVariance)
	{
		return sampleKurtosis(data.length, moment(data, 4, mean), sampleVariance);
	}

	/**
	 * Return the standard error of the sample kurtosis.
	 *
	 * Ref: R.R. Sokal, F.J. Rohlf, Biometry: the principles and practice of statistics
	 * in biological research (W.H. Freeman and Company, New York, 1998, 3rd edition)
	 * p. 138.
	 *
	 * @param size the number of elements of the data sequence.
	 * @return standard error of the sample kurtosis.
	 */

	public static double sampleKurtosisStandardError(int size)
	{
		int n = size;

		return Math.sqrt(24.0 * n * (n - 1) * (n - 1) / ((n - 3) * (n - 2) * (n + 3) * (n + 5)));
	}

	/**
	 * Returns the sample skew of a data sequence.
	 *
	 * Ref: R.R. Sokal, F.J. Rohlf, Biometry: the principles and practice of statistics
	 * in biological research (W.H. Freeman and Company, New York, 1998, 3rd edition)
	 * p. 114-115.
	 *
	 * @param size the number of elements of the data sequence.
	 * @param moment3 the third central moment, which is <code>moment(data,3,mean)</code>.
	 * @param sampleVariance the <b>sample variance</b>.
	 * @return sample skew.
	 */

	public static double sampleSkew(int size, double moment3, double sampleVariance)
	{
		int    n = size;
		double s = Math.sqrt(sampleVariance); // sqrt( (y-ymean)^2/(n-1) )
		double m3 = moment3 * n;    // (y-ymean)^3

		return n * m3 / ((n - 1) * (n - 2) * s * s * s);
	}

	/**
	 * Returns the sample skew of a data sequence.
	 * @param data the input data sequence.
	 * @param mean the mean of the data sequence.
	 * @param sampleVariance the sample variance of the data sequence.
	 * @return the sample skew of the data sequence.
	 */

	public static double sampleSkew(double[] data, double mean, double sampleVariance)
	{
		return sampleSkew(data.length, moment(data, 3, mean), sampleVariance);
	}

	/**
	 * Return the standard error of the sample skew.
	 *
	 * Ref: R.R. Sokal, F.J. Rohlf, Biometry: the principles and practice of statistics
	 * in biological research (W.H. Freeman and Company, New York, 1998, 3rd edition)
	 * p. 138.
	 *
	 * @param size the number of elements of the data sequence.
	 * @return the standard error of the sample skew.
	 */

	public static double sampleSkewStandardError(int size)
	{
		int n = size;

		return Math.sqrt(6.0 * n * (n - 1) / ((n - 2) * (n + 1) * (n + 3)));
	}

	/**
	 * Returns the sample standard deviation.
	 *
	 * Ref: R.R. Sokal, F.J. Rohlf, Biometry: the principles and practice of statistics
	 * in biological research (W.H. Freeman and Company, New York, 1998, 3rd edition)
	 * p. 53.
	 *
	 * @param size the number of elements of the data sequence.
	 * @param sampleVariance the <b>sample variance</b>.
	 * @return the sample standard deviation.
	 */

	public static double sampleStandardDeviation(int size, double sampleVariance)
	{
		double s, Cn;
		int    n = size;

		// The standard deviation calculated as the sqrt of the variance underestimates
		// the unbiased standard deviation.
		s = Math.sqrt(sampleVariance);
		// It needs to be multiplied by this correction factor.
		if (n > 30)
		{
			Cn = 1 + 1.0 / (4 * (n - 1)); // Cn = 1+1/(4*(n-1));
		}
		else
		{
			Cn = Math.sqrt((n - 1) * 0.5) * Gamma.gamma((n - 1) * 0.5) / Gamma.gamma(n * 0.5);
		}
		return Cn * s;
	}

	/**
	 * Returns the sample variance of a data sequence.
	 * That is <code>(sumOfSquares - mean*sum) / (size - 1)</code> with <code>mean = sum/size</code>.
	 *
	 * @param size the number of elements of the data sequence.
	 * @param sum <code>== Sum( data[i] )</code>.
	 * @param sumOfSquares <code>== Sum( data[i]*data[i] )</code>.
	 * @return the sample variance of the data sequence.
	 */

	public static double sampleVariance(int size, double sum, double sumOfSquares)
	{
		double mean = sum / size;

		return (sumOfSquares - mean * sum) / (size - 1);
	}

	/**
	 * Returns the sample variance of a data sequence.
	 * That is <code>Sum ( (data[i]-mean)^2 ) / (data.length-1)</code>.
	 * @param data the input data sequence.
	 * @param mean the mean of the data sequence.
	 * @return the sample variance of the data sequence.
	 */

	public static double sampleVariance(double[] data, double mean)
	{
		int size = data.length;
		double sum = 0;

		// find the sum of the squares
		for (int i = size; --i >= 0;)
		{
			double delta = data[i] - mean;

			sum += delta * delta;
		}

		return sum / (size - 1);
	}

	/**
	 * Returns the sample weighted variance of a data sequence.
	 * That is <code>(sumOfSquaredProducts  -  sumOfProducts * sumOfProducts / sumOfWeights) / (sumOfWeights - 1)</code>.
	 *
	 * @param sumOfWeights <code>== Sum( weights[i] )</code>.
	 * @param sumOfProducts <code>== Sum( data[i] * weights[i] )</code>.
	 * @param sumOfSquaredProducts <code>== Sum( data[i] * data[i] * weights[i] )</code>.
	 * @return the sample weighted variance of the data sequence.
	 */

	public static double sampleWeightedVariance(double sumOfWeights, double sumOfProducts, double sumOfSquaredProducts)
	{
		return (sumOfSquaredProducts - sumOfProducts * sumOfProducts / sumOfWeights) / (sumOfWeights - 1);
	}

	/**
	 * Returns the skew of a data sequence.
	 * @param moment3 the third central moment, which is <code>moment(data,3,mean)</code>.
	 * @param standardDeviation the standardDeviation.
	 * @return the skew of the data sequence.
	 */

	public static double skew(double moment3, double standardDeviation)
	{
		return moment3 / (standardDeviation * standardDeviation * standardDeviation);
	}

	/**
	 * Returns the skew of a data sequence, which is <code>moment(data,3,mean) / standardDeviation<sup>3</sup></code>.
	 * @param data the input data sequence.
	 * @param mean the mean of the data sequence.
	 * @param standardDeviation the standard deviation of the data sequence.
	 * @return the skew of the data sequence.
	 */

	public static double skew(double[] data, double mean, double standardDeviation)
	{
		return skew(moment(data, 3, mean), standardDeviation);
	}

	/**
	 * Splits (partitions) a list into sublists such that each sublist contains the elements with a given range.
	 * <code>splitters=(a,b,c,...,y,z)</code> defines the ranges <code>[-inf,a), [a,b), [b,c), ..., [y,z), [z,inf]</code>.
	 * <p><b>Examples:</b><br>
	 * <ul>
	 * <li>data = (1,2,3,4,5,8,8,8,10,11)</li>
	 * <li>splitters=(2,8) yields 3 bins: (1), (2,3,4,5) (8,8,8,10,11)</li>
	 * <li>splitters=() yields 1 bin: (1,2,3,4,5,8,8,8,10,11)</li>
	 * <li>splitters=(-5) yields 2 bins: (), (1,2,3,4,5,8,8,8,10,11)</li>
	 * <li>splitters=(100) yields 2 bins: (1,2,3,4,5,8,8,8,10,11), ()</li>
	 * </ul>
	 * @param sortedList the list to be partitioned (must be sorted ascending).
	 * @param splitters the points at which the list shall be partitioned (must be sorted ascending).
	 * @return the sublists (an array with <code>length == splitters.size() + 1</code>.
	 * Each sublist is returned sorted ascending.
	 */

	public static ArrayList[] split
	(
		double[] sortedList ,
		double[] splitters
	)
	{
		// assertion: data is sorted ascending.
		// assertion: splitValues is sorted ascending.

		int noOfBins = splitters.length + 1;

		ArrayList[] bins = new ArrayList[ noOfBins ];

		for ( int i = noOfBins; --i >= 0; ) bins[ i ] = new ArrayList();

		int listSize = sortedList.length;
		int nextStart = 0;
		int i = 0;

		while (nextStart < listSize && i < noOfBins - 1)
		{
			double splitValue = splitters[ i ];
			int index = Arrays.binarySearch( sortedList , splitValue );

			if (index < 0)
			{ // splitValue not found
				int insertionPosition = -index - 1;

//				bins[i].addAllOfFromTo(
//					sortedList, nextStart, insertionPosition - 1 );

				for ( int j = nextStart; j < insertionPosition; j++ )
				{
					bins[ i ].add( Double.valueOf( sortedList[ j ] ) );
				}

				nextStart = insertionPosition;
			}
			else
			{ // splitValue found
				// For multiple identical elements ("runs"), binarySearch does not define which of all valid indexes is returned.
				// Thus, skip over to the first element of a run.
				do
				{
					index--;
				} while (index >= 0 && sortedList[ index ] == splitValue);

//				bins[i].addAllOfFromTo(sortedList, nextStart, index);

				for ( int j = nextStart; j <= index; j++ )
				{
					bins[ i ].add( Double.valueOf( sortedList[ j ] ) );
				}

				nextStart = index + 1;
			}
			i++;
		}

		// now fill the remainder

//		bins[noOfBins - 1].addAllOfFromTo(
//			sortedList, nextStart, sortedList.size() - 1);

		for ( int j = nextStart; j < sortedList.length; j++ )
		{
			bins[ noOfBins - 1 ].add( Double.valueOf( sortedList[ j ] ) );
		}

		return bins;
	}

	/**
	 * Returns the standard deviation from a variance.
	 * @param variance variance.
	 * @return the standard deviation.
	 */

	public static double standardDeviation( double variance )
	{
		return Math.sqrt( variance );
	}

	/**
	 * Returns the standard error of a data sequence.
	 * That is <code>Math.sqrt(variance/size)</code>.
	 *
	 * @param size the number of elements in the data sequence.
	 * @param variance the variance of the data sequence.
	 * @return the standard error of the data sequence.
	 */

	public static double standardError( int size , double variance )
	{
		return Math.sqrt( variance / size );
	}

	/**
	 * Modifies a data sequence to be standardized.
	 * Changes each element <code>data[i]</code> as follows: <code>data[i] = (data[i]-mean)/standardDeviation</code>.
	 * @param data the data sequence to be standardized.
	 * @param mean the mean of the data sequence.
	 * @param standardDeviation the standard deviation of the data sequence.
	 */

	public static void standardize
	(
		double[] data,
		double mean,
		double standardDeviation
	)
	{
		for ( int i = data.length; --i >= 0; )
		{
			data[ i ]	= ( data[ i ] - mean) / standardDeviation;
		}
	}

	/**
	 * Returns the sum of a data sequence.
	 * That is <code>Sum( data[i] )</code>.
	 * @param data the input data sequence.
	 * @return the sum of the data sequence.
	 */

	public static double sum( double[] data )
	{
		return sumOfPowerDeviations( data, 1, 0.0 );
	}

	/**
	 * Returns the sum of inversions of a data sequence,
	 * which is <code>Sum( 1.0 / data[i])</code>.
	 * @param data the data sequence.
	 * @param from the index of the first data element (inclusive).
	 * @param to the index of the last data element (inclusive).
	 * @return the sum of inversions of the data sequence.
	 */

	public static double sumOfInversions( double[] data, int from, int to )
	{
		return sumOfPowerDeviations( data, -1, 0.0, from, to );
	}

	/**
	 * Returns the sum of logarithms of a data sequence, which is <code>Sum( Log(data[i])</code>.
	 * @param data the data sequence.
	 * @param from the index of the first data element (inclusive).
	 * @param to the index of the last data element (inclusive).
	 * @return the sum of logarithms of the data sequence.
	 */

	public static double sumOfLogarithms( double[] data, int from, int to )
	{
		double logsum = 0;

		for ( int i = from - 1; ++i <= to; ) logsum += Math.log( data[ i ] );

		return logsum;
	}

	/**	Returns sums of power deviations.
	 *
	 *	@param	data	The data as a double vector.
	 *	@param	k		The exponent.
	 *	@param	c		The central value.
	 *	@return	sum of power deviations.
	 */

	public static double sumOfPowerDeviations( double[] data, int k, double c )
	{
		return sumOfPowerDeviations( data, k, c, 0, data.length - 1 );
	}

	/**	Returns sums of power deviations.
	 *
	 *	@param	data	The data as a double vector.
	 *	@param	k		The exponent.
	 *	@param	c		The central value.
	 *	@param	from	Starting data value index.
	 *	@param	to		Ending data value index.
	 *	@return	sum of power deviations.
	 */

	public static double sumOfPowerDeviations
	(
		final double[] data,
		final int k,
		final double c,
		final int from,
		final int to
	)
	{
		double sum = 0;
		double v;
		int i;

		switch (k)
		{ // optimized for speed
		case -2:
			if (c == 0.0) for (i = from - 1; ++i <= to;)
				{
					v = data[i];
					sum += 1 / (v * v);
				}
			else for (i = from - 1; ++i <= to;)
				{
					v = data[i] - c;
					sum += 1 / (v * v);
				}
			break;

		case -1:
			if (c == 0.0) for (i = from - 1; ++i <= to;) sum += 1 / (data[i]);
			else for (i = from - 1; ++i <= to;) sum += 1 / (data[i] - c);
			break;

		case 0:
			sum += to - from + 1;
			break;

		case 1:
			if (c == 0.0) for (i = from - 1; ++i <= to;) sum += data[i];
			else for (i = from - 1; ++i <= to;) sum += data[i] - c;
			break;

		case 2:
			if (c == 0.0) for (i = from - 1; ++i <= to;)
				{
					v = data[i];
					sum += v * v;
				}
			else for (i = from - 1; ++i <= to;)
				{
					v = data[i] - c;
					sum += v * v;
				}
			break;

		case 3:
			if (c == 0.0) for (i = from - 1; ++i <= to;)
				{
					v = data[i];
					sum += v * v * v;
				}
			else for (i = from - 1; ++i <= to;)
				{
					v = data[i] - c;
					sum += v * v * v;
				}
			break;

		case 4:
			if (c == 0.0) for (i = from - 1; ++i <= to;)
				{
					v = data[i];
					sum += v * v * v * v;
				}
			else for (i = from - 1; ++i <= to;)
				{
					v = data[i] - c;
					sum += v * v * v * v;
				}
			break;

		case 5:
			if (c == 0.0) for (i = from - 1; ++i <= to;)
				{
					v = data[i];
					sum += v * v * v * v * v;
				}
			else for (i = from - 1; ++i <= to;)
				{
					v = data[i] - c;
					sum += v * v * v * v * v;
				}
			break;

		default:
			for (i = from - 1; ++i <= to;) sum += Math.pow(data[i] - c, k);
			break;
		}

		return sum;
	}

	/**
	 * Returns the sum of powers of a data sequence, which is <code>Sum ( data[i]<sup>k</sup> )</code>.
	 * @param data the input data sequence.
	 * @param k the power to which each data element will be raised.
	 * @return the sum of powers of the data sequence.
	 */

	public static double sumOfPowers( double[] data , int k )
	{
		return sumOfPowerDeviations( data, k, 0 );
	}

	/**
	 * Returns the sum of squared mean deviation of a data sequence.
	 * That is <code>variance * (size-1) == Sum( (data[i] - mean)^2 )</code>.
	 *
	 * @param size the number of elements of the data sequence.
	 * @param variance the variance of the data sequence.
	 * @return the sum of squared mean deviation of the data sequence.
	 */

	public static double sumOfSquaredDeviations( int size , double variance )
	{
		return variance * ( size - 1 );
	}

	/**
	 * Returns the sum of squares of a data sequence.
	 * That is <code>Sum ( data[i]*data[i] )</code>.
	 * @param data the input data sequence.
	 * @return the sum of squares of the data sequence.
	 */

	public static double sumOfSquares( double[] data )
	{
		return sumOfPowerDeviations( data, 2, 0.0 );
	}

	/**
	 * Returns the trimmed mean of a sorted data sequence.
	 *
	 * @param sortedData the data sequence; <b>must be sorted ascending</b>.
	 * @param mean the mean of the (full) sorted data sequence.
	 * @param left the number of leading elements to trim.
	 * @param right the number of trailing elements to trim.
	 * @return the trimmed mean of the sorted data sequence.
	 */

	public static double trimmedMean
	(
		double[] sortedData,
		double mean,
		int left,
		int right
	)
	{
		int N = sortedData.length;

		if (N == 0) throw new IllegalArgumentException("Empty data.");
		if (left + right >= N) throw new IllegalArgumentException("Not enough data.");

		int N0 = N;

		for (int i = 0; i < left; ++i)
			mean += (mean - sortedData[i]) / (--N);
		for (int i = 0; i < right; ++i)
			mean += (mean - sortedData[N0 - 1 - i]) / (--N);
		return mean;
	}

	/**
	 * Returns the variance from a standard deviation.
	 * @param standardDeviation the input standard deviation.
	 * @return the variance of the standard deviation.
	 */

	public static double variance( double standardDeviation )
	{
		return standardDeviation * standardDeviation;
	}

	/**
	 * Returns the variance of a data sequence.
	 * That is <code>(sumOfSquares - mean*sum) / size</code> with <code>mean = sum/size</code>.
	 *
	 * @param size the number of elements of the data sequence.
	 * @param sum <code>== Sum( data[i] )</code>.
	 * @param sumOfSquares <code>== Sum( data[i]*data[i] )</code>.
	 * @return the variance of the data sequence.
	 */

	public static double variance( int size, double sum, double sumOfSquares )
	{
		double mean = sum / size;

		return ( sumOfSquares - mean * sum ) / size;
	}

	/**
	 * Returns the weighted mean of a data sequence.
	 * That is <code> Sum (data[i] * weights[i]) / Sum ( weights[i] )</code>.
	 * @param data the input data sequence.
	 * @param weights the array of weights to apply to the mean calculation.
	 * @return the weighted mean of the data sequence.
	 */

	public static double weightedMean( double[] data , double[] weights )
	{
		int size = data.length;

		if ( size != weights.length || size == 0 )
			throw new IllegalArgumentException();

		double sum			= 0.0;
		double weightsSum	= 0.0;

		for ( int i = size; --i >= 0; )
		{
			double w	= weights[ i ];

			sum			+= data[ i ] * w;
			weightsSum	+= w;
		}

		return sum / weightsSum;
	}

	/**
	 * Returns the weighted RMS (Root-Mean-Square) of a data sequence.
	 * That is <code>Sum( data[i] * data[i] * weights[i]) / Sum( data[i] * weights[i] )</code>,
	 * or in other words <code>sumOfProducts / sumOfSquaredProducts</code>.
	 *
	 * @param sumOfProducts <code>== Sum( data[i] * weights[i] )</code>.
	 * @param sumOfSquaredProducts <code>== Sum( data[i] * data[i] * weights[i] )</code>.
	 * @return the weighted RMS (Root-Mean-Square) of a data sequence.
	 */

	public static double weightedRMS
	(
		double sumOfProducts ,
		double sumOfSquaredProducts
	)
	{
		return sumOfProducts / sumOfSquaredProducts;
	}

	/**
	 * Returns the winsorized mean of a sorted data sequence.
	 *
	 * @param sortedData the data sequence; <b>must be sorted ascending</b>.
	 * @param mean the mean of the (full) sorted data sequence.
	 * @param left the number of leading elements to trim.
	 * @param right the number of trailing elements to trim.
	 * @return the winsorized mean of the sorted data sequence.
	 */

	public static double winsorizedMean
	(
		double[] sortedData,
		double mean,
		int left,
		int right
	)
	{
		int N = sortedData.length;

		if (N == 0) throw new IllegalArgumentException("Empty data.");
		if (left + right >= N) throw new IllegalArgumentException("Not enough data.");

		double leftElement = sortedData[left];

		for (int i = 0; i < left; ++i)
			mean += (leftElement - sortedData[i]) / N;

		double rightElement = sortedData[N - 1 - right];

		for (int i = 0; i < right; ++i)
			mean += (rightElement - sortedData[N - 1 - i]) / N;

		return mean;
	}
}
