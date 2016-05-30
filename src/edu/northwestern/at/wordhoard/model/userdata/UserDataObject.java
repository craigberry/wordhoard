package edu.northwestern.at.wordhoard.model.userdata;

/*	Please see the license information at the end of this file. */

import java.util.Date;

import org.w3c.dom.*;

/**	A WordHoard user data object.
 */

public interface UserDataObject
{
	/**	Get the ID.
	 *
	 *	@return		The unique object ID.
	 */

	public Long getId();

	/**	Gets the title.
	 *
	 *	@return		The title.
	 */

	public String getTitle();

	/**	Sets the title.
	 *
	 *	@param	title	The title.
	 */

	public void setTitle( String title );

	/**	Gets the description.
	 *
	 *	@return		The description.
	 */

	public String getDescription();

	/**	Sets the description.
	 *
	 *	@param	description	The description.
	 */

	public void setDescription( String description );

	/**	Gets the web page URL.
	 *
	 *	@return		The web page URL.
	 */

	public String getWebPageURL();

	/**	Sets the web page URL.
	 *
	 *	@param	webPageURL	The web page URL.
	 */

	public void setWebPageURL( String webPageURL );

	/**	Gets the creation date/time.
	 *
	 *	@return		The creation date/time.
	 */

	public Date getCreationTime();

	/**	Sets the creation date/time.
	 *
	 *	@param	creationTime	The creation date/time.
	 */

	public void setCreationTime( Date creationTime );

	/**	Gets the modification date/time.
	 *
	 *	@return		The modification date/time.
	 */

	public Date getModificationTime();

	/**	Sets the modification date/time.
	 *
	 *	@param	modificationTime	The modification date/time.
	 */

	public void setModificationTime( Date modificationTime );

	/**	Gets the owner.
	 *
	 *	@return		The owner.
	 */

	public String getOwner();

	/**	Gets the "is public" flag.
	 *
	 *	@return		True if the word set is public, false if private.
	 */

	public boolean getIsPublic();

	/**	Gets the "is active" flag.
	 *
	 *	@return		True if the word set is available for use.
	 *
	 *	<p>
	 *	The "is active" flag is false until a user object is fully
	 *	created and not in the process of being deleted.
	 *	</p>
	 */

	public boolean getIsActive();

	/**	Gets the generating query.
	 *
	 *	@return		The query for generating this object.
	 */

	public String getQuery();

	/**	Add to DOM document.
	 *
	 *	@param	document		DOM document to which to add this object.
	 *							Must not be null.  In most cases,
	 *							this document should have a "wordhoard"
	 *							node as the root element.
	 *
	 *	@return					true if DOM addition successful, false otherwise.
	 */

	public boolean addToDOMDocument( org.w3c.dom.Document document );

	/**	Set values from DOM document node.
	 *
	 *	@param	domNode			DOM document node from which to initialize
	 *							query.
	 *
	 *	@return					true if settings retrieved.
	 */

	public boolean setFromDOMDocumentNode( org.w3c.dom.Node domNode );
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

