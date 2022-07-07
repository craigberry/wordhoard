package edu.northwestern.at.wordhoard.swing.calculator.cql;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.*;

/** Error for invalid CQL specification.
 */

public class CQLSyntaxError
{
	/**	Constructs and throws InvalidCQLQueryException exception.
	 *
	 *	@param	tokenizer		Parser tokenizer.
	 *	@param	errorSource		Source of error.
	 *	@param	badText			Text at which error occurs.
	 *
	 *	@throws	InvalidCQLQueryException	error in CQL query.
	 */

	public static void badElement
	(
		CQLQueryTokenizer tokenizer ,
		String errorSource ,
		String badText
	)
		throws InvalidCQLQueryException
	{
		String errorMessage	= "";

		if ( tokenizer.ttype >= 0 )
		{
			PrintfFormat format	=
				new PrintfFormat
				(
					"%s: Bad character %c found in query after %s"
				);

			errorMessage	=
				format.sprintf
				(
					new Object[]
					{
						StringUtils.safeString( errorSource ) ,
						new Character( (char)tokenizer.ttype ) ,
						StringUtils.safeString( badText )
					}
				);
		}
		else
		{
			PrintfFormat format	=
				new PrintfFormat
				(
					"%s: Bad element %s found in query after %s"
				);

			errorMessage	=
				format.sprintf
				(
					new Object[]
					{
						StringUtils.safeString( errorSource ) ,
						StringUtils.safeString( tokenizer.sval ) ,
						StringUtils.safeString( badText )
					}
				);
        }

		throw new InvalidCQLQueryException( errorMessage );
	}

	/**	Constructs and throws InvalidCQLQueryException exception.
	 *
	 *	@param	tokenizer		Parser tokenizer.
	 *	@param	errorSource		Source of error.
	 *	@param	badText			Text at which error occurs.
	 *
	 *	@throws	InvalidCQLQueryException	error in CQL query.
	 */

	public static void termEndsTooSoon
	(
		CQLQueryTokenizer tokenizer ,
		String errorSource ,
		String badText
	)
		throws InvalidCQLQueryException
	{
		PrintfFormat format	=
			new PrintfFormat
			(
				"%s: Term ends too soon at %s"
			);

		String errorMessage	=
			format.sprintf
			(
				new Object[]
				{
					StringUtils.safeString( errorSource ) ,
					StringUtils.safeString( badText )
				}
			);

		throw new InvalidCQLQueryException( errorMessage );
	}

	/**	Constructs and throws InvalidCQLQueryException exception.
	 *
	 *	@param	tokenizer		Parser tokenizer.
	 *	@param	errorSource		Source of error.
	 *	@param	badText			Text at which error occurs.
	 *
	 *	@throws	InvalidCQLQueryException	error in CQL query.
	 */

	public static void badTermType
	(
		CQLQueryTokenizer tokenizer ,
		String errorSource ,
		String badText
	)
		throws InvalidCQLQueryException
	{
		PrintfFormat format	=
			new PrintfFormat
			(
				"%s: Bad term type at %s"
			);

		String errorMessage	=
			format.sprintf
			(
				new Object[]
				{
					StringUtils.safeString( errorSource ) ,
					StringUtils.safeString( badText )
				}
			);

		throw new InvalidCQLQueryException( errorMessage );
	}

	/**	Don't allow instantiations but do allow overrides. */

	protected CQLSyntaxError()
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

