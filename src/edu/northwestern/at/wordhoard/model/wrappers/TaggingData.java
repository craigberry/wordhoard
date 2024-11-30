package edu.northwestern.at.wordhoard.model.wrappers;

/*	Please see the license information at the end of this file. */

import java.io.*;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Embeddable;

/**	Tagging data flags.
 *
 *	<p>A set of tagging data flags is a long integer which uses bits to record
 *	which kinds of tagging data are available in a corpus, work, or work part.
 */
 
@Embeddable
public class TaggingData implements Serializable {

	/**	Lemma tagging mask. */
	
	public static final long LEMMA = 0x00000001;
	
	/**	Part of speech tagging mask. */
	
	public static final long POS = 0x00000002;
	
	/**	Word class tagging mask. */
	
	public static final long WORD_CLASS = 0x00000004;
	
	/**	Spelling tagging mask. */
	
	public static final long SPELLING = 0x00000008;
	
	/**	Speaker tagging mask. */
	
	public static final long SPEAKER = 0x00000010;
	
	/**	Speaker gender tagging mask. */
	
	public static final long GENDER = 0x00000020;
	
	/**	Speaker mortality tagging mask. */
	
	public static final long MORTALITY = 0x00000040;
	
	/**	Prosodic tagging mask. */
	
	public static final long PROSODIC = 0x00000080;
	
	/**	Metrical shape tagging mask. */
	
	public static final long METRICAL_SHAPE = 0x00000100;
	
	/**	Publication dates tagging mask. */
	
	public static final long PUB_DATES = 0x00000200;

	/**	Flags. */
	
	private long flags;
	
	/**	Creates a new tagging data object.
	 */
	 
	public TaggingData () {
	}
	
	/**	Creates a new tagging data object.
	 *
	 *	@param	flags		The flags.
	 */
	 
	public TaggingData (long flags) {
		this.flags = flags;
	}
	
	/**	Gets the flags.
	 *
	 *	@return		The flags.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	@Access(AccessType.FIELD)
	public long getFlags () {
		return flags;
	}
	
	/**	Sets the flags.
	 *
	 *	@param	flags	The flags.
	 */
	 
	public void setFlags (long flags) {
		this.flags = flags;
	}
	
	/**	Returns true if tagging data is present.
	 *
	 *	@param	mask		Tag mask (LEMMA, POS, etc.).
	 *
	 *	@return		True if flag is set.
	 */
	 
	public boolean contains (long mask) {
		return (flags & mask) != 0;
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

