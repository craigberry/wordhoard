package edu.northwestern.at.utils.db.hibernate;

/*	Please see the license information at the end of this file. */

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.hibernate.ScrollableResults;

/**	Wraps a Hibernate ScrollableResults with a standard iterator.
 *
 *	<p>
 *	Hibernate supported cursored result sets using the ScrollableResults
 *	interface.  Cursored results sets do not require that the entire set
 *	of query results be retrieved to memory in one piece.  In many cases,
 *	all one wants is to be able to iterate through the results in the usual
 *	fashion.  This class wraps a Hibernate ScrollableResults with a
 *	standard Java Iterator to support simple sequential access.
 *	</p>
 *
 *	<p>
 *	Example:
 *	</p>
 *
 *	<code>
 *
 *	PersistenceManager pm = new PersistenceManager();
 *	ScrollableResults r = pm.scrollableQuery( ... );
 *
 *	HibernateScrollIterator iterator = new HibernateScrollIterator( r );
 *
 *	while ( iterator.hasNext() )
 *	{
 *		Object o = iterator.next();
 *		...
 *	}
 *
 *	iterator.close();
 *
 *	</code>
 *
 *	<p>
 *	You may call the close() method explicitly as above to release the
 *	ScrollableResults object and its associated database cursor.  These
 *	will also be released implicitly when the HibernateScrollIterator
 *	is finalized.
 *	</p>
 */

public class HibernateScrollIterator implements Iterator
{
	/**	The Hibernate ScrollableResults object through which to iterate.
	 */

	protected ScrollableResults scrollableResults;

	/**	True if the ScrollableResults object has been closed.
	 */

	protected boolean isClosed;

	/**	The next database row to return.
	 */

	protected Object nextRow;

	/**	Wrap a Hibernate ScrollableResults with an iterator.
	 *
	 *	@param	scrollableResults	The Hibernate scrollable results to
	 *								wrap with an iterator.
	 */

	public HibernateScrollIterator( ScrollableResults scrollableResults )
	{
		this.scrollableResults	= scrollableResults;
		this.isClosed			= false;
		this.nextRow			= null;
	}

	/**	Iterator interface method: is there another database row available?
	 *
	 *	@return		true if another database row is available, false otherwise.
	 */

	public boolean hasNext()
	{
		if ( nextRow == null )
		{
			if ( !isClosed )
			{
				if ( scrollableResults.next() )
				{
					nextRow = scrollableResults.get();
				}
				else
				{
					close();
				}
			}
		}

		return ( nextRow != null );
	}

	/**	Iterator interface method: return next row of data.
	 *
	 *	@return		The next database row as an object.
	 */

	public Object next()
	{
		if ( !hasNext() )
		{
			throw new NoSuchElementException();
		}

		Object result = nextRow;

		nextRow = null;

		return result;
	}

	/**	Iterator interdace method; Remove current entry -- Not supported.
	 *
	 *	@throws UnsupportedOperationException always
	 */

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	/**	Close the Hibernate ScrollableResults field.
	 */

	public void close()
	{
		if ( !isClosed )
		{
			if ( scrollableResults != null )
			{
				scrollableResults.close();
			}

			scrollableResults	= null;
		}

		isClosed = true;
	}

	/**	Check if scrollable results closed.
	 * @return	Boolean indicating whether scrollable results are closed.
	 */

	public boolean isClosed()
	{
		return isClosed;
	}

	/**	Handle object destruction.
	 *
	 *	<p>
	 *	Releases the Hibernate ScrollableResults before performing
	 *	the usual finalization.
	 *	</p>
	 */

	protected void finalize()
		throws java.lang.Throwable
	{
		close();
		super.finalize();
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

