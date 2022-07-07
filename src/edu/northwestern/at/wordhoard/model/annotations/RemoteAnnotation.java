package edu.northwestern.at.wordhoard.model.annotations;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import java.util.Date;
import edu.northwestern.at.utils.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.northwestern.at.utils.xml.*;

/**	A remote annotation.
 *
 *	<p>A remote annotation has the following attributes:
 *
 *	<ul>
 *	<li>A unique persistence id.
 *	<li>The annotation's 
 *		{@link edu.northwestern.at.wordhoard.model.annotations.AnnotationCategory
 *		category}.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.wrappers.MemoryTextWrapper
 *		text} of the annotation.
 *	</ul>
 *
 */
 
public class RemoteAnnotation implements TextAttachment {

	/**	The global identifier */
	
	private String identifier;

	/**	The userid */
	
	private String userID;

	/**	The author */
	
	private String author;

	/**	The identifier for what it annotates */
	
	private String annotates;

	/**	The type */
	
	private String type;

	/**	The status */
	
	private String status;

	/**	The creation date */
	
	private Date created;

	/**	The last modified date */
	
	private Date modified;

	/**	The work part to which this annotation is attached. */
	
	private WorkPart workPart;
	
	/**	The target of the annotation - the range of text to which
	 *	it is attached. */
	 
	private TextRange target;
	
	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	The annotation category. */
	
	private AnnotationCategory category;
	
	/**	The text. */
	
	private MemoryTextWrapper text;
	
	/**	Creates a new annotation.
	 */
	
	public RemoteAnnotation () {
		id=null;
	}

	/**	Creates a new annotation from DOM.
	 * @param	el	DOM element.
	 */
	
	public RemoteAnnotation (Element el) {
		NodeList list = el.getElementsByTagName("id");
		Element itemEl = (Element)list.item(0);

		NodeList children = itemEl.getChildNodes();
		Node child = children.item(0);
		String str = child.getNodeValue();
		setIdentifier(str);
		id=new Long(str);
		
		list = el.getElementsByTagName("type");
		itemEl = (Element)list.item(0);
		children = itemEl.getChildNodes();
		child = children.item(0);
		str = child.getNodeValue();
		setType(str);

		list = el.getElementsByTagName("status");
		itemEl = (Element)list.item(0);
		children = itemEl.getChildNodes();
		child = children.item(0);
		str = child.getNodeValue();
		setStatus(str);

		list = el.getElementsByTagName("author");
		itemEl = (Element)list.item(0);
		children = itemEl.getChildNodes();
		child = children.item(0);
		str = child.getNodeValue();
		setAuthor(str);

		list = el.getElementsByTagName("userid");
		itemEl = (Element)list.item(0);
		children = itemEl.getChildNodes();
		child = children.item(0);
		str = child.getNodeValue();
		setUserID(str);

		list = el.getElementsByTagName("annotates");
		itemEl = (Element)list.item(0);
		children = itemEl.getChildNodes();
		child = children.item(0);
		str = child.getNodeValue();
		setAnnotates(str);
		
		list = el.getElementsByTagName("context");
		itemEl = (Element)list.item(0);
		processContextEl(itemEl,this);

		list = el.getElementsByTagName("body");
		itemEl = (Element)list.item(0);
		processBodyEl(itemEl,this);
	}

	
	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 */
	 
	public Long getId () {
		return id;
	}
	
	/**	Gets the annotation category.
	 *
	 *	@return		The annotation category.
	 *
	 */
	
	public AnnotationCategory getCategory () {
		return category;
	}
	
	/**	Sets the annotation category.
	 *
	 *	@param	category	The annotation category.
	 */
	 
	public void setCategory (AnnotationCategory category) {
		this.category = category;
	}
	
	/**	Gets the text.
	 *
	 *	@return		The text.
	 *
	 */
	 
	public TextWrapped getText () {
		return text;
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
			
			int endline = lines.length - 1;
			TextLine lastline = lines[endline];
			if(lastline!= null) {
				int endOffset = lastline.getLength();
				TextRange tr = new TextRange(new TextLocation(0,0), new TextLocation(endline,endOffset));
				val = t.getText(tr);
			} 
		}
		return val;
	}
	

	/**	Reset the Text from a String.
	 *
	 *	@param	textString		The string.
	 *
	 */
	 
	public void resetText (String textString) {

		TextLine line = new TextLine();
		Context context = new Context();
		Text text = new Text();
		line = genText(textString,text,line,context);
		text.copyLine(line);
		text.finalize();
		MemoryTextWrapper textWrapper = new MemoryTextWrapper(text);
		setText(textWrapper);
	}

	
	/**	Sets the text.
	 *
	 *	@param	text		The text.
	 */
	 
	public void setText (TextWrapped text) {
		this.text = (MemoryTextWrapper)text;
	}

	/**	Gets the identifier.
	 *
	 *	@return		The identifier.
	 *
	 */
	 
	public String getIdentifier () {
		return identifier;
	}
	
	/**	Sets the identifier.
	 *
	 *	@param	identifier		The identifier.
	 */
	 
	public void setIdentifier (String identifier) {
		this.identifier = identifier;
	}
	

	/**	Gets the userID.
	 *
	 *	@return		The userID.
	 *
	 */
	 
	public String getUserID () {
		return userID;
	}
	
	/**	Sets the userID.
	 *
	 *	@param	userID		The userID.
	 */
	 
	public void setUserID (String userID) {
		this.userID = userID;
	}
	


	/**	Gets the author.
	 *
	 *	@return		The author.
	 *
	 */
	 
	public String getAuthor () {
		return author;
	}
	
	/**	Sets the author.
	 *
	 *	@param	author		The author.
	 */
	 
	public void setAuthor (String author) {
		this.author = author;
	}
	
	/**	Gets the annotates.
	 *
	 *	@return		The annotates.
	 *
	 */
	 
	public String getAnnotates () {
		return annotates;
	}
	
	/**	Sets the annotates.
	 *
	 *	@param	annotates		The annotates.
	 */
	 
	public void setAnnotates (String annotates) {
		this.annotates = annotates;
	}
	
	/**	Gets the type.
	 *
	 *	@return		The type.
	 *
	 */
	 
	public String getType () {
		return type;
	}
	
	/**	Sets the type.
	 *
	 *	@param	type		The type.
	 */
	 
	public void setType (String type) {
		this.type = type;
	}
	
	/**	Gets the status.
	 *
	 *	@return		The status.
	 *
	 */
	 
	public String getStatus () {
		return status;
	}
	
	/**	Sets the status.
	 *
	 *	@param	status		The status.
	 */
	 
	public void setStatus (String status) {
		this.status = status;
	}

	/**	Gets the creation date.
	 *
	 *	@return		The created date.
	 *
	 */
	 
	public Date getCreatedDate() {
		return created;
	}
	
	/**	Sets the creation date.
	 *
	 *	@param	created		The created date.
	 */
	 
	public void setCreatedDate (Date created) {
		this.created = created;
	}
		

	/**	Gets the modified date.
	 *
	 *	@return		The modified date.
	 *
	 */
	 
	public Date getModifiedDate() {
		return modified;
	}
	
	/**	Sets the modified date.
	 *
	 *	@param	modified		The modified date.
	 */
	 
	public void setModifiedDate (Date modified) {
		this.modified = modified;
	}
		
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two annotations are equal if their ids are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof RemoteAnnotation)) return false;
		RemoteAnnotation other = (RemoteAnnotation)obj;
		return id.equals(other.getId());
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return id.hashCode();
	}

	/**	Gets the work part.
	 *
	 *	@return		The work part.
	 *
	 *	@hibernate.many-to-one access="field" foreign-key="workPart_index"
	 */
	 
	public WorkPart getWorkPart () {
		return workPart;
	}
	
	/**	Sets the work part.
	 *
	 *	@param	workPart	The work part.
	 */
	 
	public void setWorkPart (WorkPart workPart) {
		this.workPart = workPart;
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
	
	/**	Sets the target.
	 *
	 *	@param	target		The target - the range of text to which this
	 *						annotation is attached.
	 */
	 
	public void setTarget (TextRange target) {
		this.target = target;
	}

/** service methods for handling DOM source */

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

	protected void processContextEl(Element el, RemoteAnnotation annot) {
		try {

			Element valEl = DOMUtils.getDescendant(el,"start/line");
			NodeList children = valEl.getChildNodes();
			Node child = children.item(0);
			String str = child.getNodeValue();
			int startLine = Integer.parseInt(str);

			valEl = DOMUtils.getDescendant(el,"start/offset");
			children = valEl.getChildNodes();
			child = children.item(0);
			str = child.getNodeValue();
			int startOffset = Integer.parseInt(str);


			valEl = DOMUtils.getDescendant(el,"end/line");
			children = valEl.getChildNodes();
			child = children.item(0);
			str = child.getNodeValue();
			int endLine = Integer.parseInt(str);


			valEl = DOMUtils.getDescendant(el,"end/offset");
			children = valEl.getChildNodes();
			child = children.item(0);
			str = child.getNodeValue();
			int endOffset = Integer.parseInt(str);

//			System.out.println("Textrange:" + startLine + "," + startOffset + "," + endLine + "," + endOffset);
			TextRange target = new TextRange(new TextLocation(startLine,startOffset), new TextLocation(endLine,endOffset));
			annot.setTarget(target);
		} catch (NumberFormatException e) {
			System.out.println("Bad line/offset number" + annot.getIdentifier());
		}
	}

	protected void processBodyEl(Element el, RemoteAnnotation annot) {
		TextLine line = new TextLine();
		Context context = new Context();
		Text text = new Text();
		line = processChildren(el,text,line,context);
		
		text.copyLine(line);
		text.finalize();
		MemoryTextWrapper textWrapper = new MemoryTextWrapper(text);
		annot.setText(textWrapper);
	}

	/**	Processes the children of an element.
	 *
	 *	@param	el		The element.
	 *	@param	text	List of TextLine objects.
	 *	@param	line	Line  which will be processed.
	 *	@param	context	Text generation context.
	 *	@return	Resulting TextLine object.
	 */
	 
	protected TextLine processChildren (Element el, Text text, TextLine line, Context context) {
		TextLine tLine = line;
		NodeList children = el.getChildNodes();
		int numChildren = children.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node child = children.item(i);
			int type = child.getNodeType();
			if (type == Node.TEXT_NODE) {
				tLine = processTextNode(child, text, line,context);
			} else if (type == Node.ELEMENT_NODE) {
				Element childEl = (Element)child;
				String name = childEl.getNodeName();
				if (name.equals("i")) {
					tLine = processItalicEl(childEl,text,line,context);
				} else if (name.equals("b")) {
					tLine = processBoldEl(childEl,text,line,context);
				} else {
					System.out.println("##### " +
						"Invalid element: " + name);
				}
			} else {
				System.out.println("##### " +
					"Invalid node type: " + type);
			}
		}
		return tLine;
	}

	/**	Processes a text node.
	 *
	 *	@param	node	Text node.
	 *	@param	text	List of TextLine objects.
	 *	@param	line	Line to which node will be appended.
	 *	@param	context	Text generation context.
	 *	@return	Resulting TextLine object.
	 */
	 
	protected TextLine processTextNode (Node node, Text text, TextLine line, Context context) {
		String str = node.getNodeValue();
		return genText(str,text,line,context);
	}

	/**	Generates a string of text.
	 *
	 *	@param	str		String of text.
	 *	@param	text	List of TextLine objects.
	 *	@param	line	Line to which string will be appended.
	 *	@param	context	Text generation context.
	 *	@return	Resulting TextLine object.
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
	
	/**	Processes an "i" element.
	 *
	 *	@param	el		italics element.
	 *	@param	text	List of TextLine objects.
	 *	@param	line	Line into which element will be processed.
	 *	@param	context	Text generation context.
	 *	@return	Resulting TextLine object.
	 */
	 
	protected TextLine processItalicEl (Element el, Text text, TextLine line, Context context) {
	
		Context newContext = (Context)context.clone();
		newContext.italic = true;
		return processChildren(el,text,line,newContext);
	}

	/**	Processes a "b" element.
	 *
	 *	@param	el		bold element.
	 *	@param	text	List of TextLine objects.
	 *	@param	line	Line into which element will be processed.
	 *	@param	context	Text generation context.
	 *	@return	Resulting TextLine object.
	 */
	 
	protected TextLine processBoldEl (Element el, Text text, TextLine line, Context context) {
	
		Context newContext = (Context)context.clone();
		newContext.bold = true;
		return processChildren(el,text,line,newContext);
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

