package edu.northwestern.at.wordhoard.model.annotations;

import java.io.*;
import java.util.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	A text annotation.
 *
 *	<p>Each text annotation has the following attributes in addition to 
 *	those defined for 
 *	{@link edu.northwestern.at.wordhoard.model.annotations.Annotation
 *	annotations} in general.
 *
 *	@hibernate.class table="authoredtextannotation"
 */
 
public class AuthoredTextAnnotation
	implements 
		PersistentObject,
		Externalizable,
		TextAttachment, 		
		UserDataObject
 {
	
    static final long serialVersionUID = -3623690529322058046L;

	/**	Unique persistence id (primary key). */
	
	protected Long id;
	
	/**	The annotation category. */
	
	protected AnnotationCategory category;
	
	/**	The text body. */
	
	protected String body;

	/**	The text. */
	
	protected MemoryTextWrapper text;

	/**	The work part to which this annotation is attached. */
	
	protected WorkPart workPart;
	
	/**	The target of the annotation - the range of text to which
	 *	it is attached. */
	 
	protected TextRange target;
	
	/**	The title of the annotation. */

	protected String title = "";

	/**	Description of annotation. */

	protected String description;

	/**	Web page URL. */

	protected String webPageURL;

	/**	Original creation date/time. */

	protected Date creationTime;

	/**	Last modification date/time. */

	protected Date modificationTime;


	/**	The author */
	
	private String author;

	/**	The identifier for what it annotates */
	
	private String annotates;

	/**	The type */
	
	private String type;

	/**	The status */
	
	private String status;

	/**	Owner of this annotation. */

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

	public AuthoredTextAnnotation () {
		this.isActive=false;
	}

	public AuthoredTextAnnotation (String owner, WorkPart workPart, TextRange target) {
		this.isActive=false; // set true when made persistent
		this.workPart = workPart;
		this.annotates = workPart.getTag();
		this.target = target;
		this.owner = owner;
		title="";
		body="";
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

	/**	Gets the text body.
	 *
	 *	@return		The body.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="body" length="65536" sql-type="text"
	 */
	 
	public String getBody () {
		return body;
	}

	/**	Gets the text.
	 *
	 *	@return		The text.
	 *
	 */
	 
	public TextWrapped getText () {
		if(text==null) {
			if(body==null) body=""; 
			resetText(body);
		}
		return text;
	}
	
	/**	Gets the annotation category.
	 *
	 *	@return		The annotation category.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="category_index"
	 */
	
	public AnnotationCategory getCategory () {
		return category;
	}
	
	/**	Gets the work part.
	 *
	 *	@return		The work part.
	 *
	 */
	 
	public WorkPart getWorkPart () {
		if(workPart==null && annotates!=null) {workPart = WorkUtils.getWorkPartByTag(annotates);}
		return workPart;
	}

	/**	Gets the target.
	 *
	 *	@return		The target - the range of text to which this
	 *				annotation is attached.
	 *
	 *	@hibernate.component prefix="target_"
	 */
	 
	public TextRange getTarget () {
		return target;
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

	/**	Gets the author.
	 *
	 *	@return		The author.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="author"
	 */
	 
	public String getAuthor () {
		return author;
	}
	
	/**	Gets the annotates.
	 *
	 *	@return		The annotates.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="annotates"
	 */
	 
	public String getAnnotates () {
		return annotates;
	}
	
	/**	Gets the type.
	 *
	 *	@return		The type.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="type"
	 */
	 
	public String getType () {
		return type;
	}
	
	/**	Gets the status.
	 *
	 *	@return		The status.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="status"
	 */
	 
	public String getStatus () {
		return status;
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

	/**	Sets the target.
	 *
	 *	@param	target		The target - the range of text to which this
	 *						annotation is attached.
	 */
	 
	public void setTarget (TextRange target) {
		this.target = target;
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

	/**	Sets the author.
	 *
	 *	@param	author		The author.
	 */
	 
	public void setAuthor (String author) {
		this.author = author;
	}

	/**	Sets the text body.
	 *
	 *	@param	body		The text body.
	 */
	 
	public void setBody (String body) {
		resetText(body);
	}

	
	/**	Sets the text.
	 *
	 *	@param	text		The text.
	 */
	 
	public void setText (TextWrapped text) {
		this.text = (MemoryTextWrapper) text;
		String tbody = getTextAsString();
		this.body= tbody;
	}

	/**	Reset the Text from a String.
	 *
	 *	@param	textString		The string.
	 *
	 */
	 
	public void resetText (String textString) {
		body=textString;
		TextLine line = new TextLine();
		Context context = new Context();
		Text textobj = new Text();
		line = genText(textString,textobj,line,context);
		textobj.copyLine(line);
		textobj.finalize();
		MemoryTextWrapper textWrapper = new MemoryTextWrapper(textobj);
		this.text = textWrapper;
	}


	/**	Gets the text as a String.
	 *
	 *	@return		The text.
	 *
	 */
	 
	public String getTextAsString () {
		String val = null;
		if(text!=null) {
			Text t = text.getText();
			TextLine[] lines = t.getLines();
			
			if(lines.length >0) {
				int endline = lines.length - 1;
				TextLine lastline = lines[endline];
				if(lastline!= null) {
					int endOffset = lastline.getLength();
					TextRange tr = new TextRange(new TextLocation(0,0), new TextLocation(endline,endOffset));
					val = t.getText(tr);
				} 
			}
		}
		return val;
	}

	/**	Text generation context. */
	
	protected class Context implements Cloneable {
		private boolean bold = false;	// True if bold style
		private boolean italic = false;	// True if italic style
		public Object clone () {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				throw new InternalError(e.toString());
			}
		}
	}

	/**	Generates a string of text.
	 *
	 *	@param	str		String of text.
	 */
	 
	protected TextLine genText (String str, Text text, TextLine line, Context context) {
		TextLine tLine = line;
		String[] parts = str.split("\\n");
		byte charset = TextParams.ROMAN;
		byte size = TextParams.ILIAD_SCHOLIA_FONT_SIZE;

		String part = parts[0];
		part = part.replaceAll("\\s+", " ");
		TextRun run = new TextRun(part, charset, size);
		if (context.bold) run.setBold();
		if (context.italic) run.setItalic();
		tLine.appendRun(run);

		if(parts.length > 1) {
			text.copyLine(tLine);
			for(int i =1;i<parts.length;i++) {
				part = parts[i];
				part = part.replaceAll("\\s+", " ");
				tLine = new TextLine();
				run = new TextRun(part, charset, size);
				if (context.bold) run.setBold();
				if (context.italic) run.setItalic();
				tLine.appendRun(run);
				if(i<parts.length -1) {
					text.copyLine(tLine);
				}
			}
		}
		return tLine;
	}
	
	
	/**	Sets the annotation category.
	 *
	 *	@param	category	The annotation category.
	 */
	 
	public void setCategory (AnnotationCategory category) {
		this.category = category;
	}
			
	/**	Sets the work part.
	 *
	 *	@param	workPart	The work part.
	 */
	 
	public void setWorkPart (WorkPart workPart) {
		this.workPart = workPart;
		this.annotates = workPart.getTag();
	}

	/**	Sets the annotates.
	 *
	 *	@param	annotates		The annotates.
	 */
	 
	public void setAnnotates (String annotates) {
		this.annotates = annotates;
	}
	
	/**	Sets the type.
	 *
	 *	@param	type		The type.
	 */
	 
	public void setType (String type) {
		this.type = type;
	}
	
	/**	Sets the status.
	 *
	 *	@param	status		The status.
	 */
	 
	public void setStatus (String status) {
		this.status = status;
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
		if ( ( obj == null ) || !( obj instanceof AuthoredTextAnnotation ) ) return false;

		AuthoredTextAnnotation other = (AuthoredTextAnnotation)obj;

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

		out.writeObject(category);
		out.writeObject(body);
		out.writeObject(target);
		out.writeObject(author);
		out.writeObject(annotates);
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
		isActive			= true;
		
		category            = (AnnotationCategory)in.readObject();
		body				= (String)in.readObject();
		resetText(body);
		target				= (TextRange)in.readObject();
		author				= (String)in.readObject();
		annotates			= (String)in.readObject();
		workPart = WorkUtils.getWorkPartByTag(annotates);
	}

	/**	Add read permission for userGroup
	 *
	 *	@param	userGroup		UserGroup that we're extending read
	 *							privilege to.
	 */

	public void addReadPermission(UserGroup userGroup)
	{
		UserGroupPermissionUtils.addReadPermission(this,userGroup);
	}
	
}
