package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import javax.swing.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;

/**	An abstract base class for search criterion components.
 */
 
abstract class CriterionComponent {
		
	/**	Parent find window. */
	
	private FindWindow window;
	
	/**	Parent row. */
	
	private Row row;
	
	/**	The Swing component. */
	
	private JComponent component;
	
	/**	Initializes the component.
	 *
	 *	@param	window			Parent find window.
	 *
	 *	@param	row				Parent row.
	 *
	 *	@param	val				Initial value, or null.
	 *
	 *	@return					The Swing component.
	 *
	 *	@throws	Exception
	 */
	 
	JComponent init (FindWindow window, Row row, SearchCriterion val) 
		throws Exception
	{
		this.window = window;
		this.row = row;
		component = init(val);
		return component;
	}

	/**	Initializes the component.
	 *
	 *	<p>Subclasses must implement this method.
	 *
	 *	@param	val		Initial value, or null.
	 *
	 *	@return 		The component.
	 *
	 *	@throws	Exception
	 */
	 
	abstract JComponent init (SearchCriterion val)
		throws Exception;
	
	/**	Gets the value of the component.
	 *
	 *	<p>Subclasses must implement this method.
	 *
	 *	@return			Value.
	 */
	 
	abstract SearchCriterion getValue ();
	
	/**	Sets the criteria value.
	 *
	 *	<p>Subclasses must implement this method.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */
	 
	abstract String setCriteria (SearchCriteria searchCriteria);
	
	/**	Notifies the parent window that the value has changed.
	 *
	 *	<p>This method must be called by subclasses whenever a
	 *	criteria value changes.
	 *
	 *	@param	oldVal		Old value.
	 *
	 *	@param	newVal		New value.
	 */
	
	void notifyWindow (SearchCriterion oldVal, SearchCriterion newVal) {
		window.handleValueChanged(getClass(), oldVal, newVal);
	}
	
	/** Rebuilds ourself.
	 *
	 *	<p>Subclasses must call this method if they want to rebuild themselves
	 *	in any context other than responding to value change events. The 
	 *	method sends a value changed event back to the calling object with
	 *	all three arguments null (for the class and old and new values). The
	 *	object should respond to this special event by rebuilding itself using its
	 *	current state. This ugly mechanism guarantees that the Swing component
	 *	is properly sized, positioned, revalidated, and repainted.
	 */
	 
	void rebuildSelf () {
		row.handleValueChanged(null, null, null);
	}
	
	/**	Handles a value changed event in some other criterion.
	 *
	 *	<p>Subclasses may override this method if they need to react
	 *	to changes in other criteria. The default implementation does
	 *	nothing.
	 *
	 *	<p>On entry, oldVal != newVal is guaranteed.
	 *
	 *	@param	cls			The class of the criterion that changed.
	 *
	 *	@param	oldVal		Old value. Null if the row has just
	 *						been created.
	 *
	 *	@param	newVal		New value. Null if the row has just been
	 *						deleted. 
	 *
	 *	@throws	Exception
	 */
	 
	void handleValueChanged (Class cls, SearchCriterion oldVal, 
		SearchCriterion newVal)
			throws Exception
	{
	}
	
	/**	Gets the value of another row.
	 *
	 *	@param	cls		Criterion class of the other row.
	 *
	 *	@return			Value, or null if none.
	 */
	
	SearchCriterion getOtherRowValue (Class cls) {
		return window.getValue(cls);
	}
	
	/**	Gets the parent find window.
	 *
	 *	@return		The parent find window.
	 */
	 
	FindWindow getWindow () {
		return window;
	}
	
	/**	Gets the Swing component.
	 *
	 *	@return		The Swing component.
	 */

	JComponent getSwingComponent() {
		return component;
	}
	
	/**	Checks tagging data.
	 *
	 *	@param	corpus		Corpus or null.
	 *
	 *	@param	work		Work or null.
	 *
	 *	@param	workPart	Work part or null.
	 *
	 *	@param	tag			Tag mask to check.
	 *
	 *	@return				"Corpus" if corpus does not have tagging data,
	 *						"Work" if work does not have tagging data,
	 *						"Work part" if work part does not have tagging
	 *						data, or null if all of them have tagging data.
	 */
	 
	String checkTaggingData (Corpus corpus, Work work, WorkPart workPart,
		long tag)
	{
		if (corpus != null && !corpus.getTaggingData().contains(tag))
			return "Corpus";
		if (work != null && !work.getTaggingData().contains(tag))
			return "Work";
		if (workPart != null && !workPart.getTaggingData().contains(tag))
			return "Work part";
		return null;
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

