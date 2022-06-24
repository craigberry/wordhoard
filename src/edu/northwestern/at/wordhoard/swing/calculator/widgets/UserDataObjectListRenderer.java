package edu.northwestern.at.wordhoard.swing.calculator.widgets;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	Renderer for list containing UserDataObject objects.
 */

public class UserDataObjectListRenderer
	extends DefaultListCellRenderer
{
	/**	Create renderer for user data objects.
	 */

	public UserDataObjectListRenderer()
	{
		super();
	}

	/**	Returns a component configured to display the specified value.
	 *
	 *	@param	list			The JList we're painting.
	 *
	 *	@param	value			The value to be painted.
	 *
	 *	@param	index			The cell's index.
	 *
	 *	@param	isSelected		True if the cell is selected.
	 *
	 *	@param	hasFocus		True if the cell has the focus.
	 *
	 *	@return		A component whose paint() method will render the
	 *				specified value.
	 */

	public Component getListCellRendererComponent
	(
		JList list ,
		Object value ,
		int index ,
		boolean isSelected ,
		boolean hasFocus
	)
	{
		if ( value != null )
		{
			UserDataObject object	= (UserDataObject)value;

			if ( object instanceof CanCountWords )
			{
				value	=
					new WordCounter( (CanCountWords)object ).toHTMLString();
			}
		}

		super.getListCellRendererComponent
		(
			list ,
			value ,
			index ,
			isSelected ,
			hasFocus
		);

		return this;
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


