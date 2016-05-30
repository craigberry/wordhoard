package edu.northwestern.at.wordhoard.model.userdata;

import java.io.*;
import java.util.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	A group of users.
 *
 *
 *	@hibernate.class table="usergroup"
 */
 
public class UserGroup
	implements 
		PersistentObject,
		Externalizable,
		UserDataObject
 {
	
    static final long serialVersionUID = -3623690529322058046L;

	/**	Unique persistence id (primary key). */
	
	protected Long id;
		
	/**	The title of the group. */

	protected String title = "";

	/**	Description of the group. */

	protected String description;

	/**	Web page URL. */

	protected String webPageURL;

	/**	Original creation date/time. */

	protected Date creationTime;

	/**	Last modification date/time. */

	protected Date modificationTime;

	/**	Collection of member userids belonging to this group.
	 *
	 *	<p>
	 *	Element type is String.  The strings are the unique userids.
	 *	</p>
	 */

	protected Collection members		= new HashSet();

	/**	Collection of userids that can manage this group.
	 *
	 *	<p>
	 *	Element type is String.  The strings are the unique userids.
	 *	</p>
	 */

	protected Collection admins		= new HashSet();

	/**	Owner of this group. */

	protected String owner;

	/**	True if public word set (can be seen by other users),
	 *	false if private word set.
	 */

	protected boolean isPublic;

	/**	True if annotation is active (available for use).
	 */

	protected boolean isActive;

	/**	The query  - part of UserData interface which should probably be factored out
	 */

	protected String query;

	public UserGroup () {
		this.isActive=true;
	}

	public UserGroup (String title, String owner) {
		this.isActive=true;
		this.title = title;
		this.owner = owner;
	}

	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="native"
	 */
	 
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
	 *	@hibernate.column name="owner" index="wordset_owner_index"
	 */

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

	/**	Writes the annotation to an object output stream (serializes the object).
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
		out.writeObject( members );
		out.writeObject( admins );
	}

	/**	Reads the annotation from an object input stream (deserializes the object).
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
		members				= (Collection)in.readObject();
		admins				= (Collection)in.readObject();
	}

	/**	Get the group member ids.
	 *
	 *	@return			The members as an unmodifiable collection.
	 *
	 *	@hibernate.set table="usergroup_members"
	 *		access="field" lazy="true"
	 *	@hibernate.collection-key column="usergroup"
	 *		foreign-key="usergroup_members_index"
	 *	@hibernate.collection-element column="member"
	 *		type="java.lang.String"
	 *		length="32"
	 */

	public Collection getMembers()
	{
		return Collections.unmodifiableCollection( members );
	}

	/**	Adds members from a collection of member ids.
	 *
	 *	@param	memberCollection		The new members as a collection.
	 *
	 */

	public void addMembers( Collection memberCollection )
	{
		if ( memberCollection != null )
		{
			members.addAll(memberCollection);
		}
	}

	/**	Adds a member.
	 *
	 *	@param	member		The new member userid.
	 *
	 */

	public void addMember( String member)
	{
		if ( member != null )
		{
			members.add(member);
		}
	}

	/**	Removes members passed as collection.
	 */

	public void removeMembers( Collection memberCollection )
	{
		if ( memberCollection != null )
		{
			members.removeAll(memberCollection);
		}
	}

	/**	Removes all members.
	 */

	public void removeAllMembers() { members.clear(); }

	/**	Removes a member.
	 *
	 *	@param	member		The new member userid.
	 *
	 */

	public void removeMember( String member)
	{
		if ( member != null )
		{
			members.remove(member);
		}
	}


/** administrator methods */

	/**	Get the group admin ids.
	 *
	 *	@return			The admins as an unmodifiable collection.
	 *
	 *	@hibernate.set table="usergroup_admins"
	 *		access="field" lazy="true"
	 *	@hibernate.collection-key column="usergroup"
	 *		foreign-key="usergroup_admins_index"
	 *	@hibernate.collection-element column="admin"
	 *		type="java.lang.String"
	 *		length="32"
	 */

	public Collection getAdmins()
	{
		return Collections.unmodifiableCollection( admins );
	}

	/**	Adds admins from a collection of admin ids.
	 *
	 *	@param	adminCollection		The new admins as a collection.
	 *
	 */

	public void addAdmins( Collection adminCollection )
	{
		if ( adminCollection != null )
		{
			admins.addAll(adminCollection);
		}
	}

	/**	Adds a admin.
	 *
	 *	@param	admin		The new admin userid.
	 *
	 */

	public void addAdmin( String admin)
	{
		if ( admin != null )
		{
			admins.add(admin);
		}
	}

	/**	Removes admins passed as collection.
	 */

	public void removeAdmins( Collection adminCollection )
	{
		if ( adminCollection != null )
		{
			admins.removeAll(adminCollection);
		}
	}

	/**	Removes a admin.
	 *
	 *	@param	admin		The new admin userid.
	 *
	 */

	public void removeAdmin( String admin)
	{
		if ( admin != null )
		{
			admins.remove(admin);
		}
	}

}
