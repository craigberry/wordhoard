package edu.northwestern.at.wordhoard.model.userdata;

import java.io.*;
import java.util.*;

import org.hibernate.annotations.ListIndexBase;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.annotations.*;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**	A permission on an object for a UserGroup.
 *
 *
 *	@hibernate.class table="wordhoard.usergrouppermission"
 */

@Entity
@Table(name = "usergrouppermission",
	indexes = {
		@Index(name = "permission_isPublic_index", columnList = "isPublic"),
		@Index(name = "permission_isActive_index", columnList = "isActive"),
	}
)
public class UserGroupPermission
	implements 
		PersistentObject,
		Externalizable,
		UserDataObject
 {
	
	static final long serialVersionUID = -3623690529322058046L;

	/**	Unique persistence id (primary key). */
	
	protected Long id;
		
	/**	The title of the permission. */

	protected String title = "";

	/**	Description of the group. */

	protected String description;

	/**	Web page URL. */

	protected String webPageURL;

	/**	Original creation date/time. */

	protected Date creationTime;

	/**	Last modification date/time. */

	protected Date modificationTime;

	/**	Group given permission on the object.
	 *
	 */

	protected UserGroup userGroup;


	/** The object being controlled.
	 *
	 */

	protected AuthoredTextAnnotation authoredTextAnnotation;


	/**	permission string.
	 *
	 */

	protected String permission;

/** These items should be refactored out of UserObject at some point. They don't really belong in this class 
	*	Owner of this permission. */

	protected String owner;

	/**	True if public (can be seen by other users),
	 *	false if private word set.
	 */

	protected boolean isPublic;

	/**	True if permission is active (available for use).
	 */

	protected boolean isActive;

	/**	The query  - part of UserData interface which should probably be factored out
	 */

	protected String query;

	public UserGroupPermission () {
		this.isActive=true;
	}

	public UserGroupPermission (AuthoredTextAnnotation authoredTextAnnotation, UserGroup userGroup, String permission, String owner) {
		this.isActive=true;
		this.authoredTextAnnotation=authoredTextAnnotation;
		this.userGroup = userGroup;
		this.permission = permission;
		this.owner = owner;
	}

	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="native"
	 */

	@Access(AccessType.FIELD)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId () {
		return id;
	}
	
	/**	Gets the title.
	 *
	 *	@return		The title.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="title"
	 */

	@Access(AccessType.FIELD)
	public String getTitle()
	{
		return title;
	}

	/**	Gets the description.
	 *
	 *	@return		The description.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="description"
	 */

	@Access(AccessType.FIELD)
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

	@Access(AccessType.FIELD)
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

	@Access(AccessType.FIELD)
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

	@Access(AccessType.FIELD)
	public Date getModificationTime()
	{
		return modificationTime;
	}
	
	/**	Get the owner.
	 *
	 *	@return		The owner.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="owner" index="wordset_owner_index"
	 */

	@Access(AccessType.FIELD)
	public String getOwner()
	{
		return owner;
	}

	/**	Get the public flag.
	 *
	 *	@return		True if the word set is public, false if private.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="isPublic" index="wordset_isPublic_index"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public boolean getIsPublic()
	{
		return isPublic;
	}

	/**	Get the active flag.
	 *
	 *	@return		True if the word set is active.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="isActive" index="isActive_index"
	 */

	@Access(AccessType.FIELD)
	@Column(nullable = true)
	public boolean getIsActive()
	{
		return isActive;
	}

	/**	Get the query.
	 *
	 *	@return		The query for generating this word set.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="query"
	 */

	@Access(AccessType.FIELD)
	public String getQuery()
	{
		return query;
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
	 *	@param	isPublic	True if the word set is public, false if private.
	 */

	public void setIsPublic( boolean isPublic )
	{
		this.isPublic	= isPublic;
	}

	/**	Set the active flag.
	 *
	 *	@param	isActive	True if the word set is active.
	 */

	public void setIsActive( boolean isActive )
	{
		this.isActive	= isActive;
	}

	/**	Set the query.
	 *
	 *	@param	query	The query for generating this word set.
	 */

	public void setQuery( String query )
	{
		this.query	= query;
	}

	/**	Set values from DOM document node.
	 *
	 *	@param	wordSetNode		DOM document node with word set settings.
	 *
	 *	@return					true if settings retrieved.
	 */

	public boolean setFromDOMDocumentNode( org.w3c.dom.Node wordSetNode )
	{
           						//	If word set node is null, quit.
		return true;
	}

	/**	Add word set to DOM document.
	 *
	 *	@param	document		DOM document to which to add word set.
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
		return result;
	}

	/**	Gets a string representation of the word set.
	 *
	 *	@return		The title.
	 */

	public String toString()
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
		if ( ( obj == null ) || !( obj instanceof UserGroup ) ) return false;

		UserGroup other = (UserGroup)obj;

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

	/**	Writes the permission to an object output stream (serializes the object).
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
		out.writeObject( permission );
		out.writeObject( userGroup );
		out.writeObject( authoredTextAnnotation );
	}

	/**	Reads the permission from an object input stream (deserializes the object).
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
		permission			= (String)in.readObject();
		userGroup			= (UserGroup)in.readObject();
		authoredTextAnnotation			= (AuthoredTextAnnotation)in.readObject();
	}

	/**	Get the permission.
	 *
	 *	@return			The permission 
	 *
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="permission"
	 */

	public String getPermission()
	{
		return permission;
	}


	/**	set the permission.
	 *
	 *	@param	permission	The permission.
	 */

	public void setPermission(String permission)
	{
		this.permission = permission;
	}

	/**	Get the group.
	 *
	 *	@return			The group extending permission to
	 *
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="usergroup_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userGroup")
	public UserGroup getUserGroup()
	{
		return userGroup;
	}

	/**	set the group.
	 *
	 *	@param	userGroup	The group.
	 */

	public void setUserGroup(UserGroup userGroup)
	{
		this.userGroup = userGroup;
	}

	/**	Get the conrolled item.
	 *
	 *	@return			The item being controlled
	 *
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="userdata_index"
	 */

	@Access(AccessType.FIELD)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "authoredTextAnnotation")
	public AuthoredTextAnnotation getAuthoredTextAnnotation()
	{
		return authoredTextAnnotation;
	}

	/**	set the authoredTextAnnotation.
	 *
	 *	@param	authoredTextAnnotation	The object being controlled.
	 */

	public void setAuthoredTextAnnotation(AuthoredTextAnnotation authoredTextAnnotation)
	{
		this.authoredTextAnnotation = authoredTextAnnotation;
	}
}
