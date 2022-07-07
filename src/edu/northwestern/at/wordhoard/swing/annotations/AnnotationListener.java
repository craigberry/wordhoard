package edu.northwestern.at.wordhoard.swing.annotations;

/*	Please see the license information at the end of this file. */

import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.model.annotations.*;

/**	An annotation listener.
 *
 *	<p>The listener interface for receiving annotation model state change
 *	events. See
 *	{@link edu.northwestern.at.wordhoard.swing.annotations.AnnotationModel
 *	AnnotationModel} for details.
 */

public interface AnnotationListener {

	/**	Invoked when annotation markers are shown or hidden.
	 *
	 *	@param	shown		True if markers shown, false if hidden.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public void markersShownOrHidden (boolean shown)
		throws PersistenceException;

	/**	Invoked when the annotation panel is shown or hidden.
	 *
	 *	@param	shown		True if panel shown, false if hidden.
	 */

	public void panelShownOrHidden (boolean shown);

	/**	Invoked when the annotation array is set.
	 *
	 *	@param	annotations		Array of annotations.
	 */

	public void arraySet (Attachment[] annotations);

	/**	Invoked when the annotations are cleared.
	 */

	public void cleared ();


	/**	Invoked when an annotation is edited or deleted.
	 */

	public void reset ();

	/**	Invoked when a new annotation is set.
	 *
	 *	<p>This method is invoked only if the annotation panel is shown.
	 *
	 *	@param	index			Index of annotation.
	 *
	 *	@param	annotation		Attachment.
	 */

	public void annotationSet (int index, Attachment annotation);

	/**	Invoked when the no annotations message is set.
	 *
	 *	@param	message		The no annotations message.
	 */

	public void noAnnotationsMessageSet (String message);

	/**	Invoked when the extra annotation message is set.
	 *
	 *	@param	message		The extra annotation message.
	 */

	public void extraMessageSet (String message);

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

