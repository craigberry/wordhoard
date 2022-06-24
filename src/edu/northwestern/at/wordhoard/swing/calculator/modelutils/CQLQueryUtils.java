package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.cql.*;

/**	CQL Query utilities.
 */

public class CQLQueryUtils
{
	/**	Get work parts via CQL query.
	 *
	 *	@param	queryString		The CQL bibliographic query string.
	 *	@param	workParts		Array of work parts for searching.
	 *
	 *	@return					The corresponding work parts.
	 */

	public static WorkPart[] getWorkPartsViaQuery
	(
		String queryString ,
		WorkPart[] workParts
	)
	{
		CQLQuery cql		= new CQLQuery( queryString );

		String[] hqlStrings;

		if ( workParts == null )
		{
			hqlStrings	=
				cql.getHQL( null , CQLQuery.WORKPARTRESULTS );
		}
		else
		{
			hqlStrings	=
				cql.getHQL(
					"workPart in (:workParts)" , CQLQuery.WORKPARTRESULTS );
        }

		HashSet workPartsSet	= new HashSet();

		for ( int i = 0 ; i < hqlStrings.length ; i++ )
		{
			java.util.List queryResults;

			if ( workParts != null )
			{
				queryResults	=
					PersistenceManager.doQuery
					(
						hqlStrings[ i ] ,
						new String[]{ "workParts" },
						new Object[]{ Arrays.asList( workParts ) }
					);
			}
			else
			{
				queryResults	=
					PersistenceManager.doQuery( hqlStrings[ i ] );
            }

			for	(	Iterator iterator = queryResults.iterator() ;
					iterator.hasNext() ; )
			{
				WorkPart workPart	= (WorkPart)iterator.next();
				workPartsSet.add( workPart );
			}
		}

		WorkPart[] result	= new WorkPart[ workPartsSet.size() ];

		int i				= 0;

		for	(	Iterator iterator = workPartsSet.iterator() ;
				iterator.hasNext() ; )
		{
			WorkPart workPart	= (WorkPart)iterator.next();
			result[ i++ ]		= workPart;
		}

		return result;
	}

	/**	Get work parts via CQL query.
	 *
	 *	@param	query			The CQL bibliographic query.
	 *	@param	workParts		Array of work parts for searching.
	 *
	 *	@return					The corresponding work parts.
	 */

	public static WorkPart[] getWorkPartsViaQuery
	(
		WHQuery query ,
		WorkPart[] workParts
	)
	{
		return getWorkPartsViaQuery( query.getQueryText() , workParts );
	}

	/**	Get work parts via CQL query.
	 *
	 *	@param	query			The CQL bibliographic query.
	 *	@param	canCountWords	CanCountWords object whose work parts
	 *							will be searched.
	 *
	 *	@return					The corresponding work parts.
	 */

	public static WorkPart[] getWorkPartsViaQuery
	(
		WHQuery query ,
		CanCountWords canCountWords
	)
	{
		WorkPart[] workParts	= null;

		if ( canCountWords != null )
		{
			workParts	= new WordCounter( canCountWords ).getWorkParts();
		}

		return getWorkPartsViaQuery( query.getQueryText() , workParts );
	}

	/**	Get work parts via CQL query.
	 *
	 *	@param	queryString		The CQL bibliographic query string.
	 *	@param	canCountWords	CanCountWords object whose work parts
	 *							will be searched.
	 *
	 *	@return					The corresponding work parts.
	 */

	public static WorkPart[] getWorkPartsViaQuery
	(
		String queryString ,
		CanCountWords canCountWords
	)
	{
		WorkPart[] workParts	= null;

		if ( canCountWords != null )
		{
			workParts	= new WordCounter( canCountWords ).getWorkParts();
		}

		return getWorkPartsViaQuery( queryString , workParts );
	}

	/**	Get work parts via CQL query.
	 *
	 *	@param	queryString		The CQL bibliographic query string.
	 *
	 *	@return					The corresponding work parts.
	 */

	public static WorkPart[] getWorkPartsViaQuery( String queryString )
	{
		return getWorkPartsViaQuery( queryString , (WorkPart[])null );
	}

	/**	Get work parts via CQL query.
	 *
	 *	@param	query			The CQL bibliographic query.
	 *
	 *	@return					The corresponding work parts.
	 */

	public static WorkPart[] getWorkPartsViaQuery( WHQuery query )
	{
		return getWorkPartsViaQuery(
			query.getQueryText() , (WorkPart[])null );
	}

	/**	Get works via CQL query.
	 *
	 *	@param	queryString		The CQL bibliographic query string.
	 *	@param	workParts		Array of work parts for searching.
	 *
	 *	@return					The corresponding works.
	 */

	public static Work[] getWorksViaQuery
	(
		String queryString ,
		WorkPart[] workParts
	)
	{
		CQLQuery cql		= new CQLQuery( queryString );

		String[] hqlStrings;

		if ( workParts == null )
		{
			hqlStrings	=
				cql.getHQL( null , CQLQuery.WORKRESULTS );
		}
		else
		{
			hqlStrings	=
				cql.getHQL(
					"workPart in (:workParts)" , CQLQuery.WORKRESULTS );
        }

		HashSet worksSet	= new HashSet();

		for ( int i = 0 ; i < hqlStrings.length ; i++ )
		{
			java.util.List queryResults;

			if ( workParts != null )
			{
				queryResults	=
					PersistenceManager.doQuery
					(
						hqlStrings[ i ] ,
						new String[]{ "workParts" },
						new Object[]{ Arrays.asList( workParts ) }
					);
			}
			else
			{
				queryResults	=
					PersistenceManager.doQuery( hqlStrings[ i ] );
            }

			for	(	Iterator iterator = queryResults.iterator() ;
					iterator.hasNext() ; )
			{
				Work work	= (Work)iterator.next();
				worksSet.add( work );
			}
		}

		Work[] result	= new Work[ worksSet.size() ];

		int i				= 0;

		for	(	Iterator iterator = worksSet.iterator() ;
				iterator.hasNext() ; )
		{
			Work work		= (Work)iterator.next();
			result[ i++ ]	= work;
		}

		return result;
	}

	/**	Get works via CQL query.
	 *
	 *	@param	query			The CQL bibliographic query.
	 *	@param	workParts		Array of work parts for searching.
	 *
	 *	@return					The corresponding work parts.
	 */

	public static Work[] getWorksViaQuery
	(
		WHQuery query ,
		WorkPart[] workParts
	)
	{
		return getWorksViaQuery( query.getQueryText() , workParts );
	}

	/**	Get works via CQL query.
	 *
	 *	@param	query			The CQL bibliographic query.
	 *	@param	canCountWords	CanCountWords object whose work parts
	 *							will be searched.
	 *
	 *	@return					The corresponding work parts.
	 */

	public static Work[] getWorksViaQuery
	(
		WHQuery query ,
		CanCountWords canCountWords
	)
	{
		WorkPart[] workParts	= null;

		if ( canCountWords != null )
		{
			workParts	= new WordCounter( canCountWords ).getWorks();
		}

		return getWorksViaQuery( query.getQueryText() , workParts );
	}

	/**	Get works via CQL query.
	 *
	 *	@param	queryString		The CQL bibliographic query string.
	 *	@param	canCountWords	CanCountWords object whose work parts
	 *							will be searched.
	 *
	 *	@return					The corresponding work parts.
	 */

	public static Work[] getWorksViaQuery
	(
		String queryString ,
		CanCountWords canCountWords
	)
	{
		WorkPart[] workParts	= null;

		if ( canCountWords != null )
		{
			workParts	= new WordCounter( canCountWords ).getWorks();
		}

		return getWorksViaQuery( queryString , workParts );
	}

	/**	Get works via CQL query.
	 *
	 *	@param	queryString		The CQL bibliographic query string.
	 *
	 *	@return					The corresponding works.
	 */

	public static Work[] getWorksViaQuery( String queryString )
	{
		return getWorksViaQuery( queryString , (WorkPart[])null );
	}

	/**	Get works via CQL query.
	 *
	 *	@param	query			The CQL bibliographic query.
	 *
	 *	@return					The corresponding works.
	 */

	public static Work[] getWorksViaQuery( WHQuery query )
	{
		return getWorksViaQuery( query.getQueryText() , (WorkPart[])null );
	}

	/**	Get words via CQL query.
	 *
	 *	@param	queryString		The CQL word query string.
	 *	@param	workParts		Array of work parts for searching.
	 *
	 *	@return					The corresponding words.
	 */

	public static Word[] getWordsViaQuery
	(
		String queryString ,
		WorkPart[] workParts
	)
	{
		CQLQuery cql		= new CQLQuery( queryString );

		String[] hqlStrings;

		if ( workParts == null )
		{
			hqlStrings	=
				cql.getHQL( null , CQLQuery.FULLWORDRESULTS );
		}
		else
		{
			hqlStrings	=
				cql.getHQL(
					"workPart in (:workParts)" , CQLQuery.FULLWORDRESULTS );
        }

		TreeSet wordsSet	= new TreeSet();

		for ( int i = 0 ; i < hqlStrings.length ; i++ )
		{
			java.util.List queryResults;

			if ( workParts != null )
			{
				queryResults	=
					PersistenceManager.doQuery
					(
						hqlStrings[ i ] ,
						new String[]{ "workParts" },
						new Object[]{ Arrays.asList( workParts ) }
					);
			}
			else
			{
				queryResults	=
					PersistenceManager.doQuery( hqlStrings[ i ] );
            }

			for	(	Iterator iterator = queryResults.iterator() ;
					iterator.hasNext() ; )
			{
				Word[] words;
				Object next	= iterator.next();

				try
				{
					Object[] o	= (Object[])next;
					int l		= o.length;
					words		= new Word[ l ];

					for ( int j = 0 ; j < l ; j++ )
					{
						words[ j ]	= (Word)o[ j ];
					}
				}
				catch ( Exception e )
				{
					Object o	= (Object)next;
            		words		= new Word[ 1 ];
            		words[ 0 ]	= (Word)o;
				}

				for ( int j = 0 ; j < words.length ; j++ )
				{
					wordsSet.add( words[ j ] );
				}
			}
		}

		Word[] result	= new Word[ wordsSet.size() ];

		int i			= 0;

		for	(	Iterator iterator = wordsSet.iterator() ;
				iterator.hasNext() ; )
		{
			Word word		= (Word)iterator.next();
			result[ i++ ]	= word;
		}

		return result;
	}

	/**	Get words via CQL query.
	 *
	 *	@param	queryString		The CQL word query string.
	 *	@param	canCountWords	CanCountWords object whose work parts
	 *							will be searched.
	 *
	 *	@return					The corresponding words.
	 */

	public static Word[] getWordsViaQuery
	(
		String queryString ,
		CanCountWords canCountWords
	)
	{
		WorkPart[] workParts	= null;

		if ( canCountWords != null )
		{
			workParts	= new WordCounter( canCountWords ).getWorkParts();
		}

		return getWordsViaQuery( queryString , workParts );
	}

	/**	Get words via CQL query.
	 *
	 *	@param	query			The CQL word query.
	 *	@param	canCountWords	CanCountWords object whose work parts
	 *							will be searched.
	 *
	 *	@return					The corresponding words.
	 */

	public static Word[] getWordsViaQuery
	(
		WHQuery query ,
		CanCountWords canCountWords
	)
	{
		WorkPart[] workParts	= null;

		if ( canCountWords != null )
		{
			workParts	= new WordCounter( canCountWords ).getWorkParts();
		}

		return getWordsViaQuery( query.getQueryText() , workParts );
	}

	/**	Get words via CQL query.
	 *
	 *	@param	query			The CQL word query.
	 *	@param	workParts		Array of work parts for searching.
	 *
	 *	@return					The corresponding words.
	 */

	public static Word[] getWordsViaQuery
	(
		WHQuery query ,
		WorkPart[] workParts
	)
	{
		return getWordsViaQuery( query.getQueryText() , workParts );
	}

	/**	Get words via CQL query.
	 *
	 *	@param	query			The CQL word query.
	 *
	 *	@return					The corresponding words.
	 */

	public static Word[] getWordsViaQuery( WHQuery query )
	{
		return getWordsViaQuery( query.getQueryText() , (WorkPart[])null  );
	}

	/**	Get words via CQL query.
	 *
	 *	@param	queryString		The CQL word query string.
	 *
	 *	@return					The corresponding words.
	 */

	public static Word[] getWordsViaQuery( String queryString )
	{
		return getWordsViaQuery( queryString , (WorkPart[])null  );
	}

	/**	Get multiple word phrases via CQL query.
	 *
	 *	@param	queryString		The CQL word query string.
	 *	@param	workParts		Array of work parts for searching.
	 *
	 *	@return					The corresponding phrases
	 *							as an array of arrays of Word, e.g.,
	 *							Word[][] .
	 */

	public static Word[][] getPhrasesViaQuery
	(
		String queryString ,
		WorkPart[] workParts
	)
	{
		CQLQuery cql		= new CQLQuery( queryString );

		String[] hqlStrings;

		if ( workParts == null )
		{
			hqlStrings	=
				cql.getHQL( null , CQLQuery.FULLWORDRESULTS );
		}
		else
		{
			hqlStrings	=
				cql.getHQL(
					"workPart in (:workParts)" , CQLQuery.FULLWORDRESULTS );
        }

		ArrayList phrases	= new ArrayList();

		for ( int i = 0 ; i < hqlStrings.length ; i++ )
		{
			java.util.List queryResults;

			if ( workParts != null )
			{
				queryResults	=
					PersistenceManager.doQuery
					(
						hqlStrings[ i ] ,
						new String[]{ "workParts" },
						new Object[]{ Arrays.asList( workParts ) }
					);
			}
			else
			{
				queryResults	=
					PersistenceManager.doQuery( hqlStrings[ i ] );
            }

			for	(	Iterator iterator = queryResults.iterator() ;
					iterator.hasNext() ; )
			{
				Word[] words;
				Object next	= iterator.next();

				try
				{
					Object[] o	= (Object[])next;
					int l		= o.length;
					words		= new Word[ l ];

					for ( int j = 0 ; j < l ; j++ )
					{
						words[ j ]	= (Word)o[ j ];
					}
				}
				catch ( Exception e )
				{
					Object o	= (Object)next;
            		words		= new Word[ 1 ];
            		words[ 0 ]	= (Word)o;
				}

				phrases.add( words );
			}
		}

		Word[][] result	= new Word[ phrases.size() ][];

		for	( int i = 0 ; i < phrases.size() ; i++ )
		{
			Word[] words	= (Word[])phrases.get( i );
			result[ i ]		= words;
		}

		return result;
	}

	/**	Get multiple word phrases via CQL query.
	 *
	 *	@param	query			The CQL word query.
	 *	@param	workParts		Array of work parts for searching.
	 *
	 *	@return					The corresponding phrases
	 *							as an array of arrays of Word, e.g.,
	 *							Word[][] .
	 */

	public static Word[][] getPhrasesViaQuery
	(
		WHQuery query ,
		WorkPart[] workParts
	)
	{
		return getPhrasesViaQuery( query.getQueryText() , workParts );
	}

	/**	Get phrases via CQL query.
	 *
	 *	@param	query			The CQL word query.
	 *	@param	canCountWords	CanCountWords object whose work parts
	 *							will be searched.
	 *
	 *	@return					The corresponding phrases
	 *							as an array of arrays of Word, e.g.,
	 *							Word[][] .
	 */

	public static Word[][] getPhrasesViaQuery
	(
		WHQuery query ,
		CanCountWords canCountWords
	)
	{
		WorkPart[] workParts	= null;

		if ( canCountWords != null )
		{
			workParts	= new WordCounter( canCountWords ).getWorkParts();
		}

		return getPhrasesViaQuery( query.getQueryText() , workParts );
	}

	/**	Get multiple word phrases via CQL query.
	 *
	 *	@param	queryString		The CQL word query string.
	 *	@param	canCountWords	CanCountWords object whose work parts
	 *							will be searched.
	 *
	 *	@return					The corresponding phrases
	 *							as an array of arrays of Word, e.g.,
	 *							Word[][] .
	 */

	public static Word[][] getPhrasesViaQuery
	(
		String queryString ,
		CanCountWords canCountWords
	)
	{
		WorkPart[] workParts	= null;

		if ( canCountWords != null )
		{
			workParts	= new WordCounter( canCountWords ).getWorkParts();
		}

		return getPhrasesViaQuery( queryString , workParts );
	}

	/**	Get multiple word phrases via CQL query.
	 *
	 *	@param	queryString		The CQL word query string.
	 *
	 *	@return					The corresponding phrases
	 *							as an array of arrays of Word, e.g.,
	 *							Word[][] .
	 */

	public static Word[][] getPhrasesViaQuery( String queryString )
	{
		return getPhrasesViaQuery( queryString , (WorkPart[])null );
	}

	/**	Get multiple word phrases via CQL query.
	 *
	 *	@param	query		The CQL query.
	 *
	 *	@return				The corresponding phrases
	 *						as an array of arrays of Word, e.g.,
	 *						Word[][] .
	 */

	public static Word[][] getPhrasesViaQuery( WHQuery query )
	{
		return getPhrasesViaQuery( query.getQueryText() , (WorkPart[])null );
	}

	/**	Don't allow instantiation but do allow overrides.
	 */

	protected CQLQueryUtils()
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


