package edu.northwestern.at.wordhoard.model;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.mysql.*;

/**	A line.
 *
 *	<p>Each line has the following attributes:
 *
 *	<ul>
 *	<li>A unique persistence id.
 *	<li>The full tag of the line.
 *	<li>The integer line number of the line.
 *	<li>The label for the line number. This is a string suitable for
 *		presentation to the user as the number of the line.
 *	<li>An optional stanza line label, if the line is the first line
 *		of a stanza.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.WorkPart 
 *		work part} in which the line occurs.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.text.TextRange
 *		location} of the line in the text for the work part.
 *	</ul>
 *
 *	<p>As an example from Shakespeare, in "The First Part of King Henry the
 *	Fourth", Act 1, Scene 3, line 22F, the full tag is "sha-1h4103022F", the
 *	integer line number is 22, and the label is "22F".
 *
 *	@hibernate.class table="line"
 */
 
public class Line implements PersistentObject {

	/**	Unique persistence id (primary key). */
	
	private Long id;
	
	/**	The tag. */
	
	private String tag;
	
	/**	The integer line number. */
	
	private int number;
	
	/**	The label. */
	
	private String label;
	
	/**	The stanza label. */
	
	private String stanzaLabel;

	/**	The work part. */
	
	private WorkPart workPart;
	
	/**	The location of the line in the part text. */
	
	private TextRange location;
	
	/**	Creates a new line.
	 */
	
	public Line () {
	}
	
	/**	Gets the unique id.
	 *
	 *	@return		The unique id.
	 *
	 *	@hibernate.id access="field" generator-class="assigned"
	 */
	 
	public Long getId () {
		return id;
	}
	
	/**	Sets the unique id.
	 *
	 *	@param	id		The unique id.
	 */
	 
	public void setId (Long id) {
		this.id = id;
	}
	
	/**	Gets the tag.
	 *
	 *	@return		The tag.
	 *
	 *	@hibernate.property access="field"
	 *	@hibernate.column name="tag" index="tag_index"
	 */
	 
	public String getTag () {
		return tag;
	}
	
	/**	Sets the tag.
	 *
	 *	@param	tag		The tag.
	 */
	 
	public void setTag (String tag) {
		this.tag = tag;
	}
	
	/**	Gets the line number.
	 *
	 *	@return		The line number.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	public int getNumber () {
		return number;
	}
	
	/**	Sets the line number.
	 *
	 *	@param	number		The line number.
	 */
	 
	public void setNumber (int number) {
		this.number = number;
	}
	
	/**	Gets the label.
	 *
	 *	@return		The label.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	public String getLabel () {
		return label;
	}
	
	/**	Sets the label.
	 *
	 *	@param	label		The label.
	 */
	 
	public void setLabel (String label) {
		this.label = label;
	}
	
	/**	Gets the stanza label.
	 *
	 *	@return		The stanza label.
	 *
	 *	@hibernate.property access="field"
	 */
	 
	public String getStanzaLabel () {
		return stanzaLabel;
	}
	
	/**	Sets the stanza label.
	 *
	 *	@param	stanzaLabel		The stanza label.
	 */
	 
	public void setStanzaLabel (String stanzaLabel) {
		this.stanzaLabel = stanzaLabel;
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
	
	/**	Gets the line location.
	 *
	 *	@return		The line location.
	 *
	 *	@hibernate.component prefix="location_"
	 */
	 
	public TextRange getLocation () {
		return location;
	}
	
	/**	Sets the line location.
	 *
	 *	@param	location	The line location in the part text.
	 */
	 
	public void setLocation (TextRange location) {
		this.location = location;
	}
	
	/**	Gets a string representation of the line.
	 *
	 *	@return			The tag.
	 */
	 
	public String toString () {
		return tag;
	}
	
	/**	Exports the object to a MySQL table exporter/importer.
	 *
	 *	@param	exporterImporter	MySQL table exporter/importer.
	 */
	 
	public void export (TableExporterImporter exporterImporter) {
		exporterImporter.print(id);
		exporterImporter.print(tag);
		exporterImporter.print(number);
		exporterImporter.print(label);
		exporterImporter.print(stanzaLabel);
		Long workPartId = workPart == null ? null : workPart.getId();
		exporterImporter.print(workPartId);
		int locationStartIndex = 0;
		int locationStartOffset = 0;
		int locationEndIndex = 0;
		int locationEndOffset = 0;
		if (location != null) {
			TextLocation start = location.getStart();
			TextLocation end = location.getEnd();
			if (start != null) {
				locationStartIndex = start.getIndex();
				locationStartOffset = start.getOffset();
			}
			if (end != null) {
				locationEndIndex = end.getIndex();
				locationEndOffset = end.getOffset();
			}
		}
		exporterImporter.print(locationStartIndex);
		exporterImporter.print(locationStartOffset);
		exporterImporter.print(locationEndIndex);
		exporterImporter.print(locationEndOffset);
		exporterImporter.println();
	}
	
	/**	Returns true if some other object is equal to this one.
	 *
	 *	<p>The two lines are equal if their work parts and tags are equal.
	 *
	 *	@param	obj		The other object.
	 *
	 *	@return			True if this object equals the other object.
	 */
	 
	public boolean equals (Object obj) {
		if (obj == null || !(obj instanceof Line)) return false;
		Line other = (Line)obj;
		return Compare.equals(workPart, other.getWorkPart()) &&
			Compare.equals(tag, other.getTag());
	}
	
	/**	Returns a hash code for the object.
	 *
	 *	@return		The hash code.
	 */
	 
	public int hashCode () {
		return workPart.hashCode() + tag.hashCode();
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

