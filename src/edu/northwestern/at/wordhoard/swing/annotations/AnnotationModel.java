package edu.northwestern.at.wordhoard.swing.annotations;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;

import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.wordhoard.model.annotations.*;

/**	An annotation model.
 *
 *	<p>Each
 *	{@link edu.northwestern.at.wordhoard.swing.annotations.AnnotationWindow
 *	annotation window} has a model which maintains the following state
 *	information for the annotations in the window:
 *
 *	<ul>
 *	<li>Whether annotation markers are currently shown or hidden.
 *	<li>Whether the annotation panel is currently shown or hidden.
 *	<li>An array of all the annotations for the window.
 *	<li>The index of the current annotation.
 *	<li>The "no annotations" message. This is the message that appears
 *		at the top of the annotation panel if the window contains no
 *		annotations. E.g., "There are no annotations for this text".
 *	<li>The "extra message". This is the last message that appears
 *		at the top of the annotation panel. E.g., "x of y in line".
 *	</ul>
 *
 *	<p>Window components register
 *	{@link edu.northwestern.at.wordhoard.swing.annotations.AnnotationListener
 *	listeners} on the model to listen for and react appropriately to changes
 *	in the annotation state.
 */

public class AnnotationModel {

	/**	True if markers shown. */

	private static boolean markersShown = true;

	/**	True if panel shown. */

	private boolean panelShown;

	/**	Array of annotations, or null. */

	private Attachment[] annotations;

	/**	Index of current annotation. */

	private int index;

	/**	No annotations message. */

	private String noAnnotationsMessage;

	/**	Extra message, or null. */

	private String extraMessage;

	/**	Set of listeners. */

	private HashSet listeners = new HashSet();

	/**	Creates a new annotation model.
	 */

	public AnnotationModel () {
	}

	/**	Returns true if markers are shown.
	 *
	 *	@return		True if markers shown.
	 */

	public boolean markersShown () {
		return markersShown;
	}

	/**	Shows or hides annnotation markers.
	 *
	 *	@param	show		True to show markers, false to hide them.
	 *
	 *	@throws PersistenceException
	 */

	public void showMarkers (boolean show)
		throws PersistenceException
	{
		if (show == markersShown) return;
		markersShown = show;
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			AnnotationListener listener = (AnnotationListener)it.next();
			listener.markersShownOrHidden(markersShown);
		}
	}

	/**	Returns true if the annotation panel is shown.
	 *
	 *	@return		True if the annotation panel is shown.
	 */

	public boolean panelShown () {
		return panelShown;
	}

	/**	Shows or hides the annotation panel.
	 *
	 *	@param	show		True to show the panel, false to hide it.
	 */

	public void showPanel (boolean show) {
		if (show == panelShown) return;
		panelShown = show;
		HashSet listenersCopy = (HashSet)listeners.clone();
		for (Iterator it = listenersCopy.iterator(); it.hasNext(); ) {
			AnnotationListener listener = (AnnotationListener)it.next();
			listener.panelShownOrHidden(panelShown);
		}
	}

	/**	Gets the annotations.
	 *
	 *	@return		Array of annotations, or null.
	 */

	public Attachment[] getAnnotations () {
		return annotations;
	}

	/**	Sets the annotations.
	 *
	 *	@param	annotations		Array of annotations, or null.
	 */

	public void setAnnotations (Attachment[] annotations) {
		this.annotations = annotations;
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			AnnotationListener listener = (AnnotationListener)it.next();
			listener.arraySet(annotations);
		}
	}

	/**	resets the annotations.
	 */

	public void resetAnnotations () {
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			AnnotationListener listener = (AnnotationListener)it.next();
		//	listener.arraySet(annotations);
			listener.reset();
		}
	}

	/**	Gets the number of annotations.
	 *
	 *	@return 		Number of annotations.
	 */

	public int getNumAnnotations () {
		return annotations == null ? 0 : annotations.length;
	}

	/**	Clears the annotations.
	 */

	public void clear () {
		annotations = null;
		index = 0;
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			AnnotationListener listener = (AnnotationListener)it.next();
			listener.cleared();
		}
	}

	/**	Gets the index of the current annotation.
	 *
	 *	@return		Index of the current annotation.
	 */

	public int getIndex () {
		return index;
	}

	/**	Sets the index of the current annotation.
	 *
	 *	@param	index		Index of the current annotation.
	 */

	public void setIndex (final int index) {
		this.index = index;
		if (!panelShown) return;
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run () {
					Attachment annotation =
						annotations == null || index < 0 ||
							index >= annotations.length ? null :
						annotations[index];
					for (Iterator it = listeners.iterator();
						it.hasNext(); )
					{
						AnnotationListener listener =
							(AnnotationListener)it.next();
						listener.annotationSet(index, annotation);
					}
				}
			}
		);
	}

	/**	Goes to to the next annotation.
	 */

	public void next () {
		if (index < annotations.length-1) setIndex(index+1);
	}

	/**	Goes to the previous annotation.
	 */

	public void prev () {
		if (index > 0) setIndex(index-1);
	}

	/**	Gets the no annotations message.
	 *
	 *	@return		The no annotations message.
	 */

	public String getNoAnnotationsMessage () {
		return noAnnotationsMessage;
	}

	/**	Sets the no annotations message.
	 *
	 *	@param	message		The no annotations message.
	 */

	public void setNoAnnotationsMessage (String message) {
		this.noAnnotationsMessage = message;
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			AnnotationListener listener = (AnnotationListener)it.next();
			listener.noAnnotationsMessageSet(message);
		}
	}

	/**	Gets the extra message.
	 *
	 *	@return		The extra message.
	 */

	public String getExtraMessage () {
		return extraMessage;
	}

	/**	Sets the extra message.
	 *
	 *	@param	message		The extra message.
	 */

	public void setExtraMessage (String message) {
		this.extraMessage = message;
		for (Iterator it = listeners.iterator(); it.hasNext(); ) {
			AnnotationListener listener = (AnnotationListener)it.next();
			listener.extraMessageSet(message);
		}
	}

	/**	Adds a listener.
	 *
	 *	@param	listener	Annotation listener.
	 */

	public void addListener (AnnotationListener listener) {
		listeners.add(listener);
	}

	/**	Removes a listener.
	 *
	 *	@param	listener	Annotation listener.
	 */

	public void removeListener (AnnotationListener listener) {
		listeners.remove(listener);
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

