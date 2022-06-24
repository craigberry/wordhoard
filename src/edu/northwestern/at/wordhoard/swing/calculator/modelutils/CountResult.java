package edu.northwestern.at.wordhoard.swing.calculator.modelutils;

/*	Please see the license information at the end of this file. */


import edu.northwestern.at.wordhoard.model.wrappers.*;

/**	Count result.
 */

public class CountResult
{
	/**	The word. */

	public Spelling word;

	/**	The word count. */

	public int count;

	/**	The work part. */

	public Long workPart;

	/**	The work. */

	public Long work;

	/**	The work tag. */

	public String workTag;

	/**	The work or work part count. */

	public int workCount;

	/**	Create empty count result object.
	 */

	public CountResult()
	{
	}

	/**	Create count result object.
	 *
	 *	@param	word	The word.
	 *	@param	count	The count of the word.
	 */

	public CountResult( Spelling word , int count )
	{
		this.word		= word;
		this.count		= count;
		this.workPart	= null;
		this.work		= null;
		this.workTag	= "";
		this.workCount	= 0;
	}

	/**	Create count result object.
	 *
	 *	@param	word		The word.
	 *	@param	count		The count of the word.
	 *	@param	work		The work.
	 */

	public CountResult( Spelling word , int count , Long work )
	{
		this.word		= word;
		this.count		= count;
		this.workPart	= null;
		this.work		= work;
		this.workTag	= "";
		this.workCount	= 1;
	}

	/**	Create count result object.
	 *
	 *	@param	word		The word.
	 *	@param	count		The count of the word.
	 *	@param	workTag		The work tag.
	 */

	public CountResult( Spelling word , int count , String workTag )
	{
		this.word		= word;
		this.count		= count;
		this.workPart	= null;
		this.work		= null;
		this.workTag	= workTag;
		this.workCount	= 1;
	}

	/**	Create count result object.
	 *
	 *	@param	word		The word.
	 *	@param	count		The count of the word.
	 *	@param	workCount	The work count.
	 */

	public CountResult( Spelling word , int count , int workCount )
	{
		this.word		= word;
		this.count		= count;
		this.work		= null;
		this.workTag	= "";
		this.workPart	= null;
		this.workCount	= workCount;
	}

	/**	Create count result object.
	 *
	 *	@param	word		The word string.
	 *	@param	count		The count of the word.
	 *	@param	workCount	The work count.
	 */

	public CountResult( String word , int count , int workCount )
	{
		this.word		= WordUtils.getSpellingForString( word );
		this.count		= count;
		this.workPart	= null;
		this.work		= null;
		this.workTag	= "";
		this.workCount	= workCount;
	}

	/**	Create count result object.
	 *
	 *	@param	word		The word.
	 *	@param	count		The count of the word.
	 *	@param	workPart	The work part.
	 *	@param	work		The work.
	 */

	public CountResult
	(
		Spelling word ,
		int count ,
		Long workPart ,
		Long work
	)
	{
		this.word		= word;
		this.count		= count;
		this.workPart	= workPart;
		this.work		= work;
		this.workTag	= "";
		this.workCount	= 1;
	}

	/**	Create count result object.
	 *
	 *	@param	word	The word string.
	 *	@param	count	The count of the word.
	 */

	public CountResult( String word , byte charset , int count )
	{
		this.word		= new Spelling( word , charset );
		this.count		= count;
		this.workPart	= null;
		this.work		= null;
		this.workTag	= "";
		this.workCount	= 0;
	}

	/**	Create count result object.
	 *
	 *	@param	word	The word string.
	 *	@param	count	The count of the word.
	 */

	public CountResult( String word , byte charset , long count )
	{
		this.word		= new Spelling( word , charset );
		this.count		= (int)count;
		this.workPart	= null;
		this.work		= null;
		this.workTag	= "";
		this.workCount	= 0;
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

