package edu.northwestern.at.utils.taglets;

/*	Please see the license information at the end of this file. */

import com.sun.tools.doclets.Taglet;
import com.sun.javadoc.*;
import java.util.Map;

/** Handles Javadoc generation for Hibernate XDoclet tags.
 *
 *	<p>
 *	This taglet implements the Taglet interface and generates empty
 *	javadoc content.
 *	</p>
 */

public class HibernateTaglet implements Taglet
{
	/**	Tag name. */

	protected String name = "";

	/**	Tag header description. */

	protected String header = "";

	/**	Create taglet.
	 *
	 *	@param	name	Tag name, e.g., hibernate.collection-key
	 *	@param	header	Header description for tag.
	 */

	public HibernateTaglet( String name , String header )
	{
		this.name	= name;
		this.header	= header;
	}

	/**	Return name of this taglet.
	 *
	 *	@return		The taglet name.
	 */

	public String getName()
	{
		return name;
	}

	/**	Return true if taglet can be used in a field.
	 *
	 *	@return		true if taglet can be used in a field.
	 */

	public boolean inField()
	{
		return true;
	}

	/**	Return true if taglet can be used in a constructor.
	 *
	 *	@return		true if taglet can be used in a constructor.
	 */

	public boolean inConstructor()
	{
		return true;
	}

	/**	Return true if taglet can be used in a method.
	 *
	 *	@return		true if taglet can be used in a method.
	 */

	public boolean inMethod()
	{
		return true;
	}

	/**	Return true if taglet can be used in overview section.
	 *
	 *	@return		true if taglet can be used in overview section.
	 */

	public boolean inOverview()
	{
		return true;
	}

	/**	Return true if taglet can be used in a package comment.
	 *
	 *	@return		true if taglet can be used in a package comment.
	 */

	public boolean inPackage()
	{
		return true;
	}

	/**	Return true if taglet can be used in a type.
	 *
	 *	@return		true if taglet can be used in a type.
	 */

	public boolean inType()
	{
		return true;
	}

	/**	Return true if taglet is for an inline tag.
	 *
	 *	@return		true if taglet is for an inline tag.
	 */

	public boolean isInlineTag()
	{
		return false;
	}

	/**	Return blank for tag.
	 *
	 *	@return		Blank string (not empty -- need at least one blank).
	 */

	public String toString( Tag tag )
	{
		return " ";
	}

	/**	Return nothing for multiple tags.
	 *
	 *	@return		Blank string (not empty -- need at least one blank).
	 */

	public String toString( Tag[] tags )
	{
		return " ";
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

