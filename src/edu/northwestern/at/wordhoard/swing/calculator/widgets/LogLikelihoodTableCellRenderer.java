package edu.northwestern.at.wordhoard.swing.calculator.widgets;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.*;

import edu.northwestern.at.utils.math.distributions.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	A table cell renderer which formats cell entries using PrintFFormat.
 */

public class LogLikelihoodTableCellRenderer
	extends PrintfFormatTableCellRenderer
{
	/**	--- Types of adjustments to chi-square percentage points.
	 */

	/**	No adjustment. */

	public static final int NONE		= 0;

	/**	Bonferroni adjustment. */

	public static final int BONFERRONI	= 1;

	/**	Sidak adjustment. */

	public static final int SIDAK		= 2;

	/**	True to mark significant values of the log-likelihood.
	 */

	protected boolean markSignificant	= true;

	/**	Chi-square percentage point for .05 with 1 df.
	 */

	protected double chisqinv05;

	/**	Chi-square percentage point for .01 with 1 df.
	 */

	protected double chisqinv01;

	/**	Chi-square percentage point for .001 with 1 df.
	 */

	protected double chisqinv001;

	/**	Chi-square percentage point for .0001 with 1 df.
	 */

	protected double chisqinv0001;

	/**	Create log-likelihood table cell entry renderer.
	 *
	 *	@param	formatString		PrintfFormat string for formatting
	 *								the log-likelihood.
	 *
	 *	@param	markSignificant		True to mark statistically significant
	 *								values of log-likelihood.
	 *
	 *	@param	adjustment			Type of adjustment to use for
	 *								significance levels.
	 *
	 *	@param	numberOfComparisons	Number of chi-square values to adjust
	 *								significance levels.
	 */

	public LogLikelihoodTableCellRenderer
	(
		String formatString ,
		boolean markSignificant ,
		int adjustment ,
		int numberOfComparisons
	)
	{
		super( formatString );

								//	Remember if we're marking
                        		//	significant values.

		this.markSignificant	= markSignificant;

								//	Get chi-square percentage points
								//	for comparison with log-likelihood values.
								//	Adjust them for number of comparisons
								//	if requested.

		double p05		= 0.05D;
		double p01		= 0.01D;
		double p001		= 0.001D;
		double p0001	= 0.0001D;

		switch ( adjustment )
		{
			case BONFERRONI :
				p05		= Sig.bonferroni( p05 , numberOfComparisons );
				p01		= Sig.bonferroni( p01 , numberOfComparisons );
				p001	= Sig.bonferroni( p001 , numberOfComparisons );
				p0001	= Sig.bonferroni( p0001 , numberOfComparisons );

				break;

			case SIDAK :
				p05		= Sig.sidak( p05 , numberOfComparisons );
				p01		= Sig.sidak( p01 , numberOfComparisons );
				p001	= Sig.sidak( p001 , numberOfComparisons );
				p0001	= Sig.sidak( p0001 , numberOfComparisons );

				break;

			default:
				break;
		}

		chisqinv05		= Sig.chisquareInverse( p05 , 1 );
		chisqinv01		= Sig.chisquareInverse( p01 , 1 );
		chisqinv001		= Sig.chisquareInverse( p001 , 1 );
		chisqinv0001	= Sig.chisquareInverse( p0001 , 1 );
	}

	/**	Create log-likelihood table cell entry renderer.
	 *
	 *	@param	formatString		PrintfFormat string for formatting
	 *								the log-likelihood.
	 *
	 *	@param	markSignificant		True to mark statistically significant
	 *								values of log-likelihood.
	 */

	public LogLikelihoodTableCellRenderer
	(
		String formatString ,
		boolean markSignificant
	)
	{
		this( formatString , markSignificant , NONE , 0 );
	}

	/**	Get a renderer for a table entry.
	 */

	public Component getTableCellRendererComponent
	(
		JTable table ,
		Object value ,
		boolean isSelected ,
		boolean hasFocus ,
		int row ,
		int column
	)
	{
								//	Convert log-likelihood using the
								//	specified format.

		Double doubleValue	= (Double)value;
		double loglike		= doubleValue.doubleValue();

		String formattedValue	=
			StringUtils.trim( format.sprintf( doubleValue ) );

								//	If we're to mark the statistically
								//	significant values of the log-likelihood,
								//	get the significance.

		if ( markSignificant )
		{
								//	Add trailing asterisks based upon
								//	significance level.

			if ( loglike > chisqinv0001 )
			{
				formattedValue	= formattedValue + " ****";
			}
			else if ( loglike > chisqinv001 )
			{
				formattedValue	= formattedValue + " *** ";
			}
			else if ( loglike > chisqinv01 )
			{
				formattedValue	= formattedValue + " **  ";
			}
			else if ( loglike > chisqinv05 )
			{
				formattedValue	= formattedValue + " *   ";
			}
		}
								//	Create the table cell renderer
								//	for displaying the log-likelihood value.

		DefaultTableCellRenderer renderer =
			(DefaultTableCellRenderer)
				super.getTableCellRendererComponent
				(
					table ,
					formattedValue ,
					isSelected ,
					hasFocus ,
					row ,
					column
				);

		return renderer;
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

