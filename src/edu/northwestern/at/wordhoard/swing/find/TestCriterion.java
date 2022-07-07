package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.model.search.*;

/**	Test criterion component.
 */
 
class TestCriterion extends CriterionComponent {

	/**	Initializes the component.
	 *
	 *	@return 	The component.
	 *
	 *	@throws	Exception	general error.
	 */
	 
	JComponent init (SearchCriterion val) 
		throws Exception
	{
		JLabel label = new JLabel("This is a test");
		label.setAlignmentY(Component.CENTER_ALIGNMENT);
		return label;
	}
	
	/**	Gets the value of the component.
	 *
	 *	@return			The value.
	 */
	 
	SearchCriterion getValue () {
		return null;
	}

	/**	Sets the value of the component.
	 *
	 *	@param	val		The value.
	 */
	 
	void setValue (SearchCriterion val) {
	}
	
	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */
	 
	String setCriteria (SearchCriteria searchCriteria) {
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

