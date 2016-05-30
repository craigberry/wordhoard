package edu.northwestern.at.wordhoard.model.annotations;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;

/**	A text annotation.
 *
 *	<p>Each text annotation has the following attributes in addition to 
 *	those defined for 
 *	{@link edu.northwestern.at.wordhoard.model.annotations.Annotation
 *	annotations} in general.
 *
 *	<ul>
 *	<li>The {@link edu.northwestern.at.wordhoard.model.WorkPart 
 *		work part} to which the annotation is attached.
 *	<li>The {@link edu.northwestern.at.wordhoard.model.text.TextRange
 *		target} of the annotation - the text range to which it is
 *		attached.
 *	</ul>
 *
 *	@hibernate.subclass discriminator-value="text"
 */
 
public class TextAnnotation extends Annotation implements TextAttachment {
	
	/**	The work part to which this annotation is attached. */
	
	protected WorkPart workPart;
	
	/**	The target of the annotation - the range of text to which
	 *	it is attached. */
	 
	protected TextRange target;
	
	/**	Creates a new text annotation.
	 */
	 
	public TextAnnotation () {
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

