package edu.northwestern.at.utils.swing.labeledsettingstable;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/** Class to save size and position information for the
 *	styled text panes used to display the settings information
 *	in the tree.
 */

public class LabeledSettingsValueTextPaneSettings
{
	XTextPane textPane;
	Point location;
	Dimension size;

	/** Save text pane and settings.
	  *
	  *	@param	textPane	The text pane whose settings are to
	  *						be saved.
	  *	@param	font		Font for text pane.  May be null to use default.
	  */

	public LabeledSettingsValueTextPaneSettings
	(
		XTextPane textPane ,
		Font font
	)
	{
		this.location	= textPane.getLocation();
		this.size		= textPane.getSize();
		this.textPane	= new XTextPane( textPane.getStyledText() );

		if ( font != null )
		{
			this.textPane.setFont( font );
    	}

		this.textPane.setLinks( textPane.getLinks() );
	}

		/** Get settings as a string. */

	public String toString()
	{
		return
			"location=" + location + ", size=" + size + ", " +
			"text=" + textPane.getText();
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

