package edu.northwestern.at.wordhoard.model.userdata;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import edu.northwestern.at.utils.xml.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;

import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**	A word set.
 *
 *  <p>
 *	A word set contains a list of word occurrences.  Typically these
 *	come from various works and do not necessarily appear sequentially.
 *	For example, a word set might contain all the words used by female
 *	speakers in Shakespeare comedies.
 *	</p>
 *
 *	<p>
 *	Each word set has the following attributes:
 *	</p>
 *
 *	<ul>
 *	<li>A unique persistence id.</li>
 *	<li>A title.</li>
 *	<li>A description.</li>
 *	<li>A web page URL.</li>
 *	<li>Original creation date/time.</li>
 *	<li>Last modified date/time.</li>
 *	<li>A collection of the unique work tags.
 *		</li>
 *	<li>The user Id of the owner/creator of the word set.</li>
 *	<li>A flag indicating if the word set is public (can be seen by
 *		others) or is private (can only be seen by the owner).</li>
 *	<li>A flag indicating if the work set is active.  An active word set
 *		is fully created, not marked for deletion, and can be used.</li>
 *	<li>A string containing query information for generating the
 *		list of words in the word set.</li>
 *	<li>A hash set of the reference tags for works containing at least one word
 *		in the word set.
 *		</li>
 *	<li>A hash set of the reference tags for work parts containing at least
 *		one word in the word set.
 *		</li>
 *	</ul>
 *
 *	@hibernate.class  table="wordhoard.wordset"
 *		discriminator-value="1"
 *	@hibernate.discriminator column="is_wordset" type="int"
 */

@Entity
@Table(name = "wordset",
	indexes = {
		@Index(name = "wordset_title_index", columnList = "title"),
		@Index(name = "wordset_owner_index", columnList =  "owner"),
		@Index(name = "wordset_isPublic_index", columnList = "isPublic"),
		@Index(name = "wordset_isActive_index", columnList = "isActive")
	}
)
@DiscriminatorValue("1")
@DiscriminatorColumn(name = "is_wordset", discriminatorType = DiscriminatorType.INTEGER)
public class WordSet
	implements
		Externalizable,
		CanCountWords,
		PersistentObject,
		SearchCriterion,
		UserDataObject
{
	/**	Serial version UID. */

	protected static final long serialVersionUID	= 7988862983221001929L;

	/**	Unique persistence id (primary key). */

	protected Long id;

	/**	The title of the word set. */

	protected String title;

	/**	Description of word set. */

	protected String description;

	/**	Web page URL. */

	protected String webPageURL;

	/**	Original creation date/time. */

	protected Date creationTime;

	/**	Last modification date/time. */

	protected Date modificationTime;

	/**	Collection of words belonging to this word set.
	 *
	 *	<p>
	 *	Element type is String.  The strings are the unique word tags.
	 *	</p>
	 */

	protected Collection<String> wordTags		= new HashSet<String>();

	/**	Owner of this word set. */

	protected String owner;

	/**	True if public word set (can be seen by other users),
	 *	false if private word set.
	 */

	protected boolean isPublic;

	/**	True if work set is active (available for use).
	 */

	protected boolean isActive;

	/**	The query which generates this word set's word list.
	 */

	protected String query;

	/**	Collection of works for words.  Element type is String.
	 *	The elements are the work part reference tags.
	 */

	protected Collection<String> workTags		= new HashSet<String>();

	/**	Collection of work parts for words.  Element type is String.
	 *	The elements are the work part reference tags.
	 */

	protected Collection<String> workPartTags	= new HashSet<String>();

	/**	Create an empty word set.
	 */

	public WordSet()
	{
	}

	/**	Create an empty word set with a specified name.
	 *
	 *	@param	title		The word set's title.
	 *	@param	description	The word set's description.
	 *	@param	webPageURL	The word set's web page URL.
	 *	@param	owner		The word set's owner.
	 *	@param	isPublic	True if the word set is public.
	 */

	public WordSet
	(
		String title ,
		String description ,
		String webPageURL ,
		String owner ,
		boolean isPublic
	)
	{
		this( title , description , webPageURL , owner , isPublic , "" );
	}

	/**	Create an empty word set with a specified name and query string.
	 *
	 *	@param	title		The word set's title.
	 *	@param	description	The word set's description.
	 *	@param	webPageURL	The word set's web page URL.
	 *	@param	owner		The word set's owner.
	 *	@param	isPublic	True if the word set is public.
	 *	@param	query		Query properties for generating this word set.
	 */

	public WordSet
	(
		String title ,
		String description ,
		String webPageURL ,
		String owner ,
		boolean isPublic ,
		String query
	)
	{
		this.title				= title;
		this.description		= description;
		this.webPageURL			= webPageURL;
		this.owner				= owner;
		this.isPublic			= isPublic;
		this.isActive			= false;
		this.query  		    = query;
		this.creationTime		= new Date();
		this.modificationTime	= new Date();
	}

	/**	Create a word set from a DOM document node.
	 *
	 *	@param	wordSetNode		The root node for the word set.
	 *	@param	owner			The word set's owner.
	 */

	public WordSet(	org.w3c.dom.Node wordSetNode , String owner )
	{
		setFromDOMDocumentNode( wordSetNode );

		this.owner				= owner;
		this.creationTime		= new Date();
		this.modificationTime	= new Date();
		this.isActive			= false;
	}

	/**	Gets the unique id.
	 *
	 *	@return			The unique id.
     *
     *	@hibernate.id	generator-class="native" access="field"
	 */

	@Access(AccessType.FIELD)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId()
	{
		return id;
	}

	/**	Gets the title.
	 *
	 *	@return		The title.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="title" index="wordset_title_index"
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
	 *	@hibernate.column name="description" length="65536" sql-type="text"
	 */

	@Access(AccessType.FIELD)
	@Column(columnDefinition = "text", length = 65536)
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

	/**	Get the word tags.
	 *
	 *	@return			The word tags as an unmodifiable collection.
	 *
	 *	@hibernate.set table="wordset_wordtags"
	 *		access="field" lazy="true"
	 *	@hibernate.collection-key column="wordSet"
	 *		foreign-key="wordset_wordTags_index"
	 *	@hibernate.collection-element column="wordTag"
	 *		type="java.lang.String"
	 *		length="32"
	 */

	@Access(AccessType.FIELD)
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "wordset_wordtags",
		joinColumns = {
			@JoinColumn(name = "wordSet",
						foreignKey = @ForeignKey(name = "wordset_wordTags_index"))
		}
	)
	@Column(name = "wordTag", columnDefinition = "varchar(32)")
	public Collection<String> getWordTags()
	{
		return Collections.unmodifiableCollection( wordTags );
	}

	/**	Gets the work tags.
	 *
	 *	@return			The work tags as an unmodifiable collection.
	 *
	 *	@hibernate.set table="wordset_worktags"
	 *		access="field" lazy="true"
	 *	@hibernate.collection-key column="wordSet"
	 *		foreign-key="wordset_workTags_index"
	 *	@hibernate.collection-element column="tag"
	 *		type="java.lang.String"
	 *		length="32"
	 */

	@Access(AccessType.FIELD)
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "wordset_worktags",
		joinColumns = {
			@JoinColumn(name = "wordSet",
						foreignKey = @ForeignKey(name = "wordset_workTags_index"))
		}
	)
	@Column(name = "tag", columnDefinition = "varchar(32)")
	public Collection<String> getWorkTags()
	{
		return Collections.unmodifiableCollection( workTags );
	}

	/**	Get the work part tags.
	 *
	 *	@return			The work part tags as an unmodifiable collection.
	 *
	 *	@hibernate.set table="wordset_workparttags"
	 *		access="field" lazy="true"
	 *	@hibernate.collection-key column="wordSet"
	 *		foreign-key="wordset_workPartTags_index"
	 *	@hibernate.collection-element column="tag"
	 *		type="java.lang.String"
	 *		length="32"
	 */

	@Access(AccessType.FIELD)
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "wordset_workparttags",
		joinColumns = {
			@JoinColumn(name = "wordSet",
						foreignKey = @ForeignKey(name = "wordset_workPartTags_index")
			)
		}
	)
	@Column(name = "tag", columnDefinition = "varchar(32)")
	public Collection<String> getWorkPartTags()
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

	/**	Adds a word Id from a Word object.
	 *
	 *	@param	word		The new word.
	 *
	 *	<p>
	 *	The work Ids are updated as needed.
	 *	</p>
	 */

	public void addWord( Word word )
	{
		wordTags.add( word.getTag() );

		workTags.add( word.getWork().getTag() );
		workPartTags.add( word.getWorkPart().getTag() );
	}

	/**	Adds words from an array of Word.
	 *
	 *	@param	wordArray		The new words an array.
	 *
	 *	<p>
	 *	The work and work part collections are also updated as needed.
	 *	</p>
	 */

	public void addWords( Word[] wordArray )
	{
		if ( wordArray != null )
		{
			for ( int i = 0 ; i < wordArray.length ; i++ )
			{
				wordTags.add( wordArray[ i ].getTag() );

				workTags.add( wordArray[ i ].getWork().getTag() );
				workPartTags.add( wordArray[ i ].getWorkPart().getTag() );
			}
		}
	}

	/**	Adds words from a collection of Word.
	 *
	 *	@param	wordCollection		The new words as a collection.
	 *
	 *	<p>
	 *	The work and work part collection are also updated as needed.
	 *	</p>
	 */

	public void addWords( Collection<Word> wordCollection )
	{
		if ( wordCollection != null )
		{
			for	(	Iterator iterator = wordCollection.iterator() ;
					iterator.hasNext() ; )
			{
				Word word 	= (Word)iterator.next();

				wordTags.add( word.getTag() );

				workTags.add( word.getWork().getTag() );
				workPartTags.add( word.getWorkPart().getTag() );
			}
		}
	}

	/**	Adds word tags.
	 *
	 *	@param	wordTagsArray	String array of word tags.
	 */

	public void addWordTags( String[] wordTagsArray )
	{
		if ( wordTagsArray != null )
		{
			for ( int i = 0 ; i < wordTagsArray.length ; i++ )
			{
				wordTags.add( wordTagsArray[ i ] );
			}
		}
	}

	/**	Adds word tags.
	 *
	 *	@param	wordTags	Collection of word tags.
	 */

	public void addWordTags( Collection wordTags )
	{
		if ( wordTags != null )
		{
			for (	Iterator iterator	= wordTags.iterator() ;
					iterator.hasNext(); )
			{
				wordTags.add( (String)iterator.next() );
			}
		}
	}

	/**	Adds work tags.
	 *
	 *	@param	workTagsArray	String array of work tags.
	 */

	public void addWorkTags( String[] workTagsArray )
	{
		if ( workTagsArray != null )
		{
			for ( int i = 0 ; i < workTagsArray.length ; i++ )
			{
				workTags.add( workTagsArray[ i ] );
			}
		}
	}

	/**	Adds work part tags.
	 *
	 *	@param	workPartTagsArray	String array of work part tags.
	 */

	public void addWorkPartTags( String[] workPartTagsArray )
	{
		if ( workPartTagsArray != null )
		{
			for ( int i = 0 ; i < workPartTagsArray.length ; i++ )
			{
				workPartTags.add( workPartTagsArray[ i ] );
			}
		}
	}

	/**	Removes all the words.
	 */

	public void removeWords()
	{
		wordTags.clear();
		workTags.clear();
		workPartTags.clear();
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

								//	Indicate failure if document is null.

		if ( document == null ) return result;

		try
		{
			org.w3c.dom.Element dataObjectElement	=
				ExportUtils.addUserDataObjectHeaderToDOM( this , document );

								//	Emit work tags.

			for	(	Iterator iterator = workTags.iterator() ;
					iterator.hasNext() ; )
			{
				String tag	= (String)iterator.next();

				org.w3c.dom.Element workElement	=
					document.createElement( "work" );

				org.w3c.dom.Attr tagAttribute	=
					document.createAttribute( "tag" );

				tagAttribute.setValue( tag );

				workElement.setAttributeNode( tagAttribute );

				dataObjectElement.appendChild( workElement );
			}
								//	Emit work part tags.

			for	(	Iterator iterator = workPartTags.iterator() ;
					iterator.hasNext() ; )
			{
				String tag	= (String)iterator.next();

				org.w3c.dom.Element workPartElement	=
					document.createElement( "workpart" );

				org.w3c.dom.Attr tagAttribute	=
					document.createAttribute( "tag" );

				tagAttribute.setValue( tag );

				workPartElement.setAttributeNode( tagAttribute );

				dataObjectElement.appendChild( workPartElement );
			}
								//	Emit word tags.

			for	(	Iterator iterator = wordTags.iterator() ;
					iterator.hasNext() ; )
			{
				String tag	= (String)iterator.next();

				org.w3c.dom.Element wordElement	=
					document.createElement( "word" );

				org.w3c.dom.Attr tagAttribute	=
					document.createAttribute( "tag" );

				tagAttribute.setValue( tag );

				wordElement.setAttributeNode( tagAttribute );

				dataObjectElement.appendChild( wordElement );
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
	 *	@param	wordSetNode		DOM document node with word set settings.
	 *
	 *	@return					true if settings retrieved.
	 */

	public boolean setFromDOMDocumentNode( org.w3c.dom.Node wordSetNode )
	{
           						//	If word set node is null, quit.

		if ( wordSetNode == null ) return false;

								//	If the word set node is not "wordset",
								//	do nothing further.

		if ( !wordSetNode.getNodeName().equals( "wordset" ) )
		{
			return false;
		}
								//	Get title, isPublic, and query attributes.

		NamedNodeMap attributes	= wordSetNode.getAttributes();

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

								//	Get all the work tag nodes.

		ArrayList workTagNodes	=
			DOMUtils.getChildren( wordSetNode , "work" );

		for ( int j = 0 ; j < workTagNodes.size() ; j++ )
		{
								//	Get next work tag node.

			Node workTagNode	= (Node)workTagNodes.get( j );

								//	Get the work tag string.

			attributes			= workTagNode.getAttributes();

			String workTag		=
				attributes.getNamedItem( "tag" ).getNodeValue();

								//	Save this word part tag.

			workTags.add( workTag );
		}
								//	Get all the work part tag nodes.

		ArrayList workPartTagNodes	=
			DOMUtils.getChildren( wordSetNode , "workpart" );

		for ( int j = 0 ; j < workPartTagNodes.size() ; j++ )
		{
								//	Get next work part tag node.

			Node workPartTagNode	= (Node)workPartTagNodes.get( j );

								//	Get the work part tag string.

			attributes				= workPartTagNode.getAttributes();

			String workPartTag		=
				attributes.getNamedItem( "tag" ).getNodeValue();

								//	Save this word part tag.

			workPartTags.add( workPartTag );
		}
								//	Get all the word tag nodes.

		ArrayList wordTagNodes	=
			DOMUtils.getChildren( wordSetNode , "word" );

		for ( int j = 0 ; j < wordTagNodes.size() ; j++ )
		{
								//	Get next word part tag node.

			Node wordTagNode	= (Node)wordTagNodes.get( j );

								//	Get the word part tag string.

			attributes			= wordTagNode.getAttributes();

			String wordTag		=
				attributes.getNamedItem( "tag" ).getNodeValue();

								//	Save this word part tag.

			wordTags.add( wordTag );
		}

		return true;
	}

	/**	Gets a string representation of the word set.
	 *
	 *	@return		The title.
	 */

	public String toString()
	{
		return title;
	}

	/**	Gets a detailed string representation of the word set.
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

	@Transient
	public SearchCriterion getSearchDefault( Class<?> cls )
	{
		if ( cls.equals( WordSet.class ) )
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

	@Transient
	public Class<?> getJoinClass()
	{
		return WordSet.class;
	}

	/**	Gets the Hibernate where clause.
	 *
	 *	@return		The Hibernate where clause.
	 */

	@Transient
	public String getWhereClause()
	{
		return "wordSet=:wordSet and word.tag in elements(wordSet.wordTags)";
	}

	/**	Sets the Hibernate query argument.
	 *
	 *	@param	q		Hibernate query.
	 *	@param	session	Hibernate session.
	 */

	public void setArg
	(
		Query q ,
		Session session
	)
	{
		q.setParameter( "wordSet" , this );
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
		line.appendRun( "word set = " + title , romanFontInfo );
	}

	/**	Gets the report phrase.
	 *
	 *	@return		The report phrase "in".
	 */

	@Transient
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

	@Transient
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
		if ( ( obj == null ) || !( obj instanceof WordSet ) ) return false;

		WordSet other = (WordSet)obj;

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

	/**	Writes the word set to an object output stream (serializes the object).
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
		out.writeObject( query );
		out.writeObject( workPartTags );
		out.writeObject( workTags );
		out.writeObject( wordTags );
	}

	/**	Reads the word set from an object input stream (deserializes the object).
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
		query				= (String)in.readObject();
		workPartTags		= (Collection)in.readObject();
		workTags			= (Collection)in.readObject();
		wordTags			= (Collection)in.readObject();
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


