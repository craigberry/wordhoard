package edu.northwestern.at.wordhoard.model.userdata;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import org.hibernate.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

import edu.northwestern.at.wordhoard.swing.Err;
import edu.northwestern.at.wordhoard.swing.WordHoard;

/**	A work set.
 *
 *	<p>
 *	A work set contains a list of works and/or work parts.  Work sets
 *	are useful for creating collections of subparts of works.  For example,
 *	a work set might contain all the prologues to Shakespeare plays.
 *	Another might contain just a single Canterbury Tale.
 *	</p>
 *
 *	<ul>
 *	<li>A unique persistence id.</li>
 *	<li>A title.</li>
 *	<li>A description.</li>
 *	<li>A web page URL.</li>
 *	<li>Original creation date/time.</li>
 *	<li>Last modified date/time.</li>
 *	<li>A collection of {@link edu.northwestern.at.wordhoard.model.WorkPart
 *		work part reference tags}.</li>
 *	<li>The user Id of the owner/creator of the work set.</li>
 *	<li>A flag indicating if the work set is public (can be seen by
 *		others) or is private (can only be seen by the owner).</li>
 *	<li>A flag indicating if the work set is active.  An active work set
 *		is fully created, not marked for deletion, and can be used.</li>
 *	</ul>
 *
 *	@hibernate.class table="workset"
 */

public class WorkSet
	implements
		Externalizable,
		CanCountWords,
		PersistentObject,
		SearchCriterion,
		UserDataObject
{
	/**	Serial version UID. */

	protected static final long serialVersionUID	= 6327944002871143511L;

	/**	Unique persistence id (primary key). */

	protected Long id;

	/**	The title of the work set. */

	protected String title;

	/**	Description of work set. */

	protected String description;

	/**	Web page URL. */

	protected String webPageURL;

	/**	Original creation date/time. */

	protected Date creationTime;

	/**	Last modification date/time. */

	protected Date modificationTime;

	/**	Owner of this work set. */

	protected String owner;

	/**	True if public work set (can be seen by other users),
	 *	false if private work set.
	 */

	protected boolean isPublic;

	/**	True if work set is active (available for use).
	 */

	protected boolean isActive;

	/**	The query properties to generate this word set's work part list.
	 */

	protected String query;

	/**	Collection of reference tags of work parts belonging to this work set. */

	protected Collection workPartTags = new HashSet();	// element type is String

	/**	Create an empty work set.
	 */

	public WorkSet()
	{
	}

	/**	Create work set.
	 *
	 *	@param	title		The work set's title.
	 *	@param	description	The work set's description.
	 *	@param	webPageURL	The work set's web page.
	 *	@param	owner		The work set's owner.
	 *	@param	isPublic	True if the work set is public.
	 *	@param	query 		CQL Query which generated the work set.
	 *	@param	workParts	The work parts in the work set as a list.
	 */

	public WorkSet
	(
		String title ,
		String description ,
		String webPageURL ,
		String owner ,
		boolean isPublic ,
		String query ,
		Collection workParts
	)
	{
		this.title				= title;
		this.description		= description;
		this.webPageURL			= webPageURL;
		this.owner				= owner;
		this.isPublic			= isPublic;
		this.isActive			= true;
		this.query				= query;
		this.creationTime		= new Date();
		this.modificationTime	= new Date();

		addWorkParts( workParts );
	}

	/**	Create work set.
	 *
	 *	@param	title		The work set's title.
	 *	@param	description	The work set's description.
	 *	@param	webPageURL	The work set's web page.
	 *	@param	owner		The work set's owner.
	 *	@param	isPublic	True if the work set is public.
	 *	@param	query 		CQL Query which generated the work set.
	 *	@param	workParts	The work parts in the work set as an array.
	 */

	public WorkSet
	(
		String title ,
		String description ,
		String webPageURL ,
		String owner ,
		boolean isPublic ,
		String query ,
		WorkPart[] workParts
	)
	{
		this.title				= title;
		this.description		= description;
		this.webPageURL			= webPageURL;
		this.owner				= owner;
		this.isPublic			= isPublic;
		this.isActive			= true;
		this.query				= query;
		this.creationTime		= new Date();
		this.modificationTime	= new Date();

		addWorkParts( workParts );
	}

	/**	Create work set.
	 *
	 *	@param	title			The work set's title.
	 *	@param	description		The work set's description.
	 *	@param	webPageURL		The work set's web page.
	 *	@param	owner			The work set's owner.
	 *	@param	isPublic		True if the work set is public.
	 *	@param	query 			CQL Query which generated the work set.
	 *							May be null.
	 *	@param	workPartTags	The work part tags as a string array.
	 */

	public WorkSet
	(
		String title ,
		String description ,
		String webPageURL ,
		String owner ,
		boolean isPublic ,
		String query ,
		String[] workPartTags
	)
	{
		this.title				= title;
		this.description		= description;
		this.webPageURL			= webPageURL;
		this.owner				= owner;
		this.isPublic			= isPublic;
		this.isActive			= true;
		this.query				= query;
		this.creationTime		= new Date();
		this.modificationTime	= new Date();

		addWorkPartTags( workPartTags );
	}

	/**	Create a work set from a DOM document node.
	 *
	 *	@param	workSetNode		The root node for the work set.
	 *	@param	owner			The work set's owner.
	 */

	public WorkSet(	org.w3c.dom.Node workSetNode , String owner )
	{
		setFromDOMDocumentNode( workSetNode );

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
	 *	@return		True if the work set is public, false if private.
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

	/**	Get the query.
	 *
	 *	@return		The query for generating this work set.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="query"
	 */

	public String getQuery()
	{
		return query;
	}

	/**	Gets the work part tags.
	 *
	 *	@return			The work part tags as an unmodifiable collection.
	 *
	 *	@hibernate.set table="workset_workparttags"
	 *		access="field" lazy="true"
	 *	@hibernate.collection-key column="workSet"
	 *		foreign-key="workset_index"
	 *	@hibernate.collection-element column="tag"
	 *		type="java.lang.String"
	 *		length="32"
	 */

	public Collection getWorkPartTags()
	{
		return Collections.unmodifiableCollection( workPartTags );
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
	 *	@param	description		The description.
	 */

	public void setDescription( String description )
	{
		this.description	= description;
	}

	/**	Sets the web page URL.
	 *
	 *	@param	webPageURL		The web page URL.
	 */

	public void setWebPageURL( String webPageURL )
	{
		this.webPageURL	= webPageURL;
	}

	/**	Sets the creation date.
	 *
	 *	@param		creationTime	The creation time.
	 */

	public void setCreationTime( Date creationTime )
	{
		this.creationTime	= creationTime;
	}

	/**	Sets the modification date.
	 *
	 *	@param		modificationTime	The modification time.
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
	 *	@param	isPublic	True if the work set is public, false if private.
	 */

	public void setIsPublic( boolean isPublic )
	{
		this.isPublic	= isPublic;
	}

	/**	Set the active flag.
	 *
	 *	@param	isActive	True if the work set is active.
	 */

	public void setIsActive( boolean isActive )
	{
		this.isActive	= isActive;
	}

	/**	Adds a work part.
	 *
	 *	@param	workPart		The new work part.
	 */

	public void addWorkPart( WorkPart workPart )
	{
		if ( workPart != null )
		{
			workPartTags.add( workPart.getTag() );
		}
	}

	/**	Adds work parts from an array.
	 *
	 *	@param	workPartArray		The new work parts as an array.
	 */

	public void addWorkParts( WorkPart[] workPartArray )
	{
		if ( workPartArray != null )
		{
			for ( int i = 0 ; i < workPartArray.length ; i++ )
			{
				workPartTags.add( workPartArray[ i ].getTag() );
			}
		}
	}

	/**	Adds work parts from a collection.
	 *
	 *	@param	workPartCollection		The new work parts as a collection.
	 */

	public void addWorkParts( Collection workPartCollection )
	{
		if ( workPartCollection != null )
		{
			for	(	Iterator iterator = workPartCollection.iterator() ;
					iterator.hasNext() ; )
			{
				WorkPart workPart 	= ( (WorkPart)iterator.next() );
				workPartTags.add( workPart.getTag() );
			}
		}
	}

	/**	Adds work parts from an array of tags.
	 *
	 *	@param	workPartTagArray	The new work part tags as an array.
	 */

	public void addWorkPartTags( String[] workPartTagArray )
	{
		if ( workPartTagArray != null )
		{
			for ( int i = 0 ; i < workPartTagArray.length ; i++ )
			{
				workPartTags.add( workPartTagArray[ i ] );
			}
		}
	}

	/**	Removes a work part.
	 *
	 *	@param	workPart		The work part.
	 */

	public void removeWorkPart( WorkPart workPart )
	{
		if ( workPart != null )
		{
			workPartTags.remove( workPart.getTag() );
		}
	}

	/**	Removes all the work parts.
	 */

	public void removeWorkParts()
	{
		workPartTags.clear();
	}

	/**	Add work set to DOM document.
	 *
	 *	@param	document		DOM document to which to add work set.
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
								//	Create work set element.

			org.w3c.dom.Element dataObjectElement	=
				ExportUtils.addUserDataObjectHeaderToDOM( this , document );

								//	Emit work part tags.

			for	(	Iterator iterator = workPartTags.iterator() ;
					iterator.hasNext() ; )
			{
				String workPartTag	= (String)iterator.next();

				org.w3c.dom.Element workPartElement	=
					document.createElement( "workpart" );

				org.w3c.dom.Attr tagAttribute	=
					document.createAttribute( "tag" );

				tagAttribute.setValue( workPartTag );

				workPartElement.setAttributeNode( tagAttribute );

				dataObjectElement.appendChild( workPartElement );
			}

			result	= true;
		}
		catch ( Exception e )
		{
		}
		 								//	Return success indicator.
		return result;
	}

	/**	Set values from DOM document node.
	 *
	 *	@param	workSetNode		DOM document node with work set settings.
	 *
	 *	@return					true if settings retrieved.
	 */

	public boolean setFromDOMDocumentNode( org.w3c.dom.Node workSetNode )
	{
           						//	If work set node is null, quit.

		if ( workSetNode == null ) return false;

								//	If the work set node is not "workset",
								//	do nothing further.

		if ( !workSetNode.getNodeName().equals( "workset" ) )
		{
			return false;
		}
								//	Get title, isPublic, and query attributes.

		NamedNodeMap attributes	= workSetNode.getAttributes();

		title			=
			attributes.getNamedItem( "title" ).getNodeValue();

		description			=
			attributes.getNamedItem( "description" ).getNodeValue();

		webPageURL			=
			attributes.getNamedItem( "webpageurl" ).getNodeValue();

		isPublic		=
			attributes.getNamedItem( "ispublic" ).getNodeValue().equals( "true" );

		query			=
			attributes.getNamedItem( "query" ).getNodeValue();

								//	Get all the work part tag nodes.

		ArrayList workPartTagNodes	=
			DOMUtils.getChildren( workSetNode , "workpart" );

		for ( int j = 0 ; j < workPartTagNodes.size() ; j++ )
		{
								//	Get next work part tag node.

			Node workPartTagNode	= (Node)workPartTagNodes.get( j );

								//	Get the work part tag string.

			attributes			= workPartTagNode.getAttributes();

			String workPartTag		=
				attributes.getNamedItem( "tag" ).getNodeValue();

								//	Save this work part tag.

			workPartTags.add( workPartTag );
		}

		return true;
	}

	/**	Gets a string representation of the work set.
	 *
	 *	@return		The title.
	 */

	public String toString()
	{
		return title;
	}

	/**	Gets a detailed string representation of the work set.
	 *
	 *	@return		The title.
	 */

	public String toStringDetailed()
	{
		return title;
	}

	/**	Gets a search criterion default value.
	 *
	 *	@param	cls		Model class of search criterion.
	 *
	 *	@return			Default value for search criterion.
	 */

	public SearchCriterion getSearchDefault( Class cls )
	{
		if ( cls.equals( WorkSet.class ) )
		{
			return this;
		}
		else
		{
			return null;
		}
	}

	/**	Gets the join class.
	 *
	 *	@return		The join class, or null if none.
	 */

	public Class getJoinClass()
	{
		return null;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	public String getWhereClause()
	{
		return "word.workPart in (:workSetWorkParts)";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg
	(
		org.hibernate.Query q ,
		org.hibernate.Session session
	)
	{
								//	Get work parts corresponding to
								//	work part tags in work set.

		WorkPart[] workParts	=
			WorkUtils.getWorkPartsByTag(
				this.getWorkPartTags() , session );

                                //	Expand work parts to include
                                //	all descendents with text.
		q.setParameterList
		(
			"workSetWorkParts" ,
			WorkUtils.expandWorkParts( workParts )
		);
	}

	/**	Appends a description to a text line.
	 *
	 *	@param	line			Text line.
	 *
	 *	@param	romanFontInfo	Roman font info.
	 *
	 *	@param	fontInfo		Array of font info indexed by character
	 *							set.
	 */

	public void appendDescription
	(
		TextLine line,
		FontInfo romanFontInfo,
		FontInfo[] fontInfo
	)
	{
		line.appendRun( "work set = " + title , romanFontInfo );
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "in".
	 */

	public String getReportPhrase()
	{
		return "in";
	}

	/**	Gets the spelling of the grouping object.
	 *
	 *	@param	numHits		Number of hits.
	 *
	 *	@return		The spelling of the grouping object.
	 */

	public Spelling getGroupingSpelling( int numHits )
	{
		return new Spelling( getTitle() , TextParams.ROMAN );
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
		if ( ( obj == null ) || !( obj instanceof WorkSet ) ) return false;

		WorkSet other = (WorkSet)obj;

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

	/**	Writes the work set to an object output stream (serializes the object).
	 *
	 *	@param	out		Object output stream.
	 *
	 *	@throws	IOException
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
		out.writeObject( query );
		out.writeObject( workPartTags );
	}

	/**	Reads the work set from an object input stream (deserializes the object).
	 *
	 *	@param	in		Object input stream.
	 *
	 *	@throws	IOException
	 *
	 *	@throws	ClassNotFoundException
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
		query				= (String)in.readObject();
		workPartTags        = (Collection)in.readObject();
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

