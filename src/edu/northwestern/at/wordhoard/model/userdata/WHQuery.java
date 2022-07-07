package edu.northwestern.at.wordhoard.model.userdata;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

import org.w3c.dom.*;

/**	A WordHoard query.
 *
 *	<p>
 *	A query contains a WordHoard Corpus Query Language specification.
 *	At present, WordHoard allows for two types of saved queries:  word-based
 *	queries for constructing word sets and phrase sets, and bibliographic-based
 *	queries for constructing work sets.
 *	</p>
 *
 *	<p>
 *	Each query object contains the following fields.
 *	</p>
 *
 *	<ul>
 *	<li>A unique persistence id.</li>
 *	<li>A title.</li>
 *	<li>A description.</li>
 *	<li>A web page URL.</li>
 *	<li>Original creation date/time.</li>
 *	<li>Last modified date/time.</li>
 *	<li>The user Id of the owner/creator of the query.</li>
 *	<li>A flag indicating if the query is public (can be seen by
 *		others) or is private (can only be seen by the owner).</li>
 *	<li>A flag indicating if the query is active.  An active query
 *		is fully created, not marked for deletion, and can be used.</li>
 *	<li>The query type.</li>
 *	<li>The query text in WordHoard Corpus Query Language format.</li>
 *	</ul>
 *
 *	@hibernate.class  table="query"
 */

public class WHQuery
	implements
		Externalizable,
		PersistentObject,
		UserDataObject
{
	/**	Serial version UID. */

	protected static final long serialVersionUID = -7146031338264291538L;

	/**	-- Query types. -- */

	/**	Word query. */

	public static final int WORDQUERY		= 0;

	/**	Work/Work part query. */

	public static final int WORKPARTQUERY	= 1;

	/**	Unique persistence id (primary key). */

	protected Long id;

	/**	The title of the query. */

	protected String title;

	/**	Description of query. */

	protected String description;

	/**	Web page URL. */

	protected String webPageURL;

	/**	Original creation date/time. */

	protected Date creationTime;

	/**	Last modification date/time. */

	protected Date modificationTime;

	/**	Owner of this query. */

	protected String owner;

	/**	true if public query (can be seen by other users),
	 *	false if private query.
	 */

	protected boolean isPublic;

	/**	True if work set is active (available for use).
	 */

	protected boolean isActive;

	/**	Type of query.
	 */

	protected int queryType;

	/**	Query text in WordHoard Corpus Query Language format. */

	protected String queryText;

	/**	Create an empty query.
	 */

	public WHQuery()
	{
	}

	/**	Create a query with a specified name.
	 *
	 *	@param	title		The query's title.
	 *	@param	description	The query's description.
	 *	@param	webPageURL	The query's web page URL.
	 *	@param	owner		The query's owner.
	 *	@param	isPublic	True if the query is public.
	 *	@param	queryType	The query type.
	 *	@param	queryText	The query text.
	 */

	public WHQuery
	(
		String title ,
		String description ,
		String webPageURL ,
		String owner ,
		boolean isPublic ,
		int queryType ,
		String queryText
	)
	{
		this.title				= title;
		this.description		= description;
		this.webPageURL			= webPageURL;
		this.owner				= owner;
		this.isPublic			= isPublic;
		this.isActive			= true;
		this.queryType			= queryType;
		this.queryText			= queryText;

		this.creationTime		= new Date();
		this.modificationTime	= new Date();
	}

	/**	Create a query from a DOM document node.
	 *
	 *	@param	queryNode		The root node for the query.
	 *	@param	owner			The work set's owner.
	 */

	public WHQuery(	org.w3c.dom.Node queryNode , String owner )
	{
		setFromDOMDocumentNode( queryNode );

		this.owner				= owner;
		this.creationTime		= new Date();
		this.modificationTime	= new Date();
		this.isActive			= true;
	}

	/**	Gets the unique id.
	 *
	 *	@return			The unique id.
     *
     *	@hibernate.id	generator-class="native" access="field"
	 */

	public Long getId()
	{
		return id;
	}

	/**	Gets the title.
	 *
	 *	@return		The title.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="title" index="title_index"
	 */

	public String getTitle()
	{
		return title;
	}

	/**	Gets the description.
	 *
	 *	@return		The description.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="description" sql-type="text" length="65536"
	 */

	public String getDescription()
	{
		return description;
	}

	/**	Gets the web page URL.
	 *
	 *	@return		The web page URL.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="webPageURL"
	 */

	public String getWebPageURL()
	{
		return webPageURL;
	}

	/**	Gets the creation date.
	 *
	 *	@return		The creation date.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="creationTime"
	 */

	public Date getCreationTime()
	{
		return creationTime;
	}

	/**	Gets the modification date.
	 *
	 *	@return		The modification date.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="modificationTime"
	 */

	public Date getModificationTime()
	{
		return modificationTime;
	}

	/**	Get the owner.
	 *
	 *	@return		The owner.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="owner" index="owner_index"
	 */

	public String getOwner()
	{
		return owner;
	}

	/**	Get the public flag.
	 *
	 *	@return		True if the query is public, false if private.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="isPublic" index="isPublic_index"
	 */

	public boolean getIsPublic()
	{
		return isPublic;
	}

	/**	Get the active flag.
	 *
	 *	@return		True if the work set is active.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="isActive" index="isActive_index"
	 */

	public boolean getIsActive()
	{
		return isActive;
	}

	/**	Get the query type.
	 *
	 *	@return		The query type.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="queryType" index="queryType_index"
	 */

	public int getQueryType()
	{
		return queryType;
	}

	/**	Get the query text.
	 *
	 *	@return		The query text.
	 *
	 *	@hibernate.property access="field"
	 */

	public String getQueryText()
	{
		return queryText;
	}

	/**	Return the query text.
	 *
	 *	@return		The query text.
	 *
	 *	<p>
	 *	For UserDataObject interface.
	 *	</p>
	 */

	public String getQuery()
	{
		return queryText;
	}

	/**	Set the persistence id.
	 *
	 *	@param	id		The persistence ID.
	 */

    private void setId( Long id )
    {
        this.id	= id;
    }

	/**	Sets the title.
	 *
	 *	@param	title		The title.
	 */

	public void setTitle( String title )
	{
		this.title	= title;
	}

	/**	Sets the description.
	 *
	 *	@param	description	The description.
	 */

	public void setDescription( String description )
	{
		this.description	= description;
	}

	/**	Sets the web page URL.
	 *
	 *	@param	webPageURL	The web page URL.
	 */

	public void setWebPageURL( String webPageURL )
	{
		this.webPageURL	= webPageURL;
	}

	/**	Sets the creation time.
	 *
	 *	@param	creationTime	The creation time.
	 */

	public void setCreationTime( Date creationTime )
	{
		this.creationTime	= creationTime;
	}

	/**	Sets the modification time.
	 *
	 *	@param	modificationTime	The modification time.
	 */

	public void setModificationTime( Date modificationTime )
	{
		this.modificationTime	= modificationTime;
	}

	/**	Set the owner.
	 *
	 *	@param	owner	The owner.
	 */

	public void setOwner( String owner )
	{
		this.owner	= owner;
	}

	/**	Set the public flag.
	 *
	 *	@param	isPublic	True if the query is public, false if private.
	 */

	public void setIsPublic( boolean isPublic )
	{
		this.isPublic	= isPublic;
	}

	/**	Set the active flag.
	 *
	 *	@param	isActive	True if the query is active.
	 */

	public void setIsActive( boolean isActive )
	{
		this.isActive	= isActive;
	}

	/**	Set the query type.
	 *
	 *	@param	queryType	The query type.
	 */

	public void setQueryType( int queryType )
	{
		this.queryType	= queryType;
	}

	/**	Set the query text.
	 *
	 *	@param	queryText	The query text.
	 */

	public void setQueryText( String queryText )
	{
		this.queryText	= queryText;
	}

	/**	Add query to DOM document.
	 *
	 *	@param	document		DOM document to which to add query.
	 *							Must not be null.  In most cases,
	 *							this document should have a "wordhoard"
	 *							node as the root element.
	 *
	 *	@return					true if DOM addition successful, false otherwise.
	 */

	public boolean addToDOMDocument
	(
		org.w3c.dom.Document document
	)
	{
		boolean result	= false;

								//	Indicate failure if document is null.

		if ( document == null ) return result;

		try
		{
								//	Add common header.

			org.w3c.dom.Element queryElement	=
				ExportUtils.addUserDataObjectHeaderToDOM( this , document );

								//	Add query text attribute.

			org.w3c.dom.Attr queryTextAttribute	=
				document.createAttribute( "querytext" );

			queryTextAttribute.setValue( getQueryText() );

			queryElement.setAttributeNode( queryTextAttribute );

								//	Add query type attribute.

			org.w3c.dom.Attr queryTypeAttribute	=
				document.createAttribute( "querytype" );

			String queryTypeString	= "word";

			if ( getQueryType() == WORKPARTQUERY )
			{
				queryTypeString	= "workpart";
			}

			queryTypeAttribute.setValue( queryTypeString );

			queryElement.setAttributeNode( queryTypeAttribute );

			result	= true;
		}
		catch ( Exception e )
		{
		}
		 								//	Return success indicator.
		return result;
	}

	/**	Set query settings from a DOM document node.
	 *
	 *	@param	queryNode		The DOM node which is the root of the
	 *							query to get.
	 *
	 *	@return					true if settings retrieved.
	 */

	public boolean setFromDOMDocumentNode
	(
		org.w3c.dom.Node queryNode
	)
	{
           						//	If work set node is null, quit.

		if ( queryNode == null ) return false;

								//	If the word set node is not "whquery",
								//	do nothing further.

		if ( !queryNode.getNodeName().equals( "whquery" ) )
		{
			return false;
		}
								//	Get title, isPublic, and query attributes.

		NamedNodeMap attributes	= queryNode.getAttributes();

		title			=
			attributes.getNamedItem( "title" ).getNodeValue();

		description			=
			attributes.getNamedItem( "description" ).getNodeValue();

		webPageURL			=
			attributes.getNamedItem( "webpageurl" ).getNodeValue();

		isPublic		=
			attributes.getNamedItem( "ispublic" ).getNodeValue().equals( "true" );

		String queryTypeString		=
			attributes.getNamedItem( "querytype" ).getNodeValue();

		queryType	= WHQuery.WORDQUERY;

		if ( queryTypeString.equals( "workpart" ) )
		{
			queryType = WHQuery.WORKPARTQUERY;
        }

		queryText			=
			attributes.getNamedItem( "querytext" ).getNodeValue();

		return true;
	}

	/**	Gets a string representation of the query.
	 *
	 *	@return		The title.
	 */

	public String toString()
	{
		return title;
	}

	/**	Gets a detailed string representation of the query.
	 *
	 *	@return		The title.
	 */

	public String toStringDetailed()
	{
		return title;
	}

	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>
	 *	The two objects are equal if their ids are equal.
	 *  </p>
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */

	public boolean equals( Object obj )
	{
		if ( ( obj == null ) || !( obj instanceof WHQuery ) ) return false;

		WHQuery other = (WHQuery)obj;

		return id.equals( other.getId() );
	}

	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */

	public int hashCode()
	{
		return id.hashCode();
	}

	/**	Writes the query to an object output stream (serializes the object).
	 *
	 *	@param	out		Object output stream.
	 *
	 *	@throws	IOException	I/O error.
	 */

	public void writeExternal( ObjectOutput out )
		throws IOException
	{
		out.writeObject( id );
		out.writeObject( title );
		out.writeObject( description );
		out.writeObject( webPageURL );
		out.writeObject( creationTime );
		out.writeObject( modificationTime );
		out.writeObject( owner );
		out.writeBoolean( isPublic );
		out.writeBoolean( isActive );
		out.writeInt( queryType );
		out.writeObject( queryText );
	}

	/**	Reads the query from an object input stream (deserializes the object).
	 *
	 *	@param	in		Object input stream.
	 *
	 *	@throws	IOException	I/O error.
	 *
	 *	@throws	ClassNotFoundException	class not found.
	 */

	public void readExternal( ObjectInput in )
		throws IOException, ClassNotFoundException
	{
		id					= (Long)in.readObject();
		title				= (String)in.readObject();
		description			= (String)in.readObject();
		webPageURL			= (String)in.readObject();
		creationTime		= (Date)in.readObject();
		modificationTime	= (Date)in.readObject();
		owner				= (String)in.readObject();
		isPublic    		= in.readBoolean();
		isActive            = in.readBoolean();
		queryType			= in.readInt();
		queryText			= (String)in.readObject();
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

