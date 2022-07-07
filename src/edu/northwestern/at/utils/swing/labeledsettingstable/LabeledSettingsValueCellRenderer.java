package edu.northwestern.at.utils.swing.labeledsettingstable;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import java.rmi.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	Table cell renderer class for the settings value.
 *
 *	<p>
 *	The message column is rendered as an XTextPane with variable height.
 *	The height is adjusted properly to exactly contain the wrapped
 *	setting value text.
 *	</p>
 */

public class LabeledSettingsValueCellRenderer
	extends XTextPaneTableCellRenderer
	implements TableCellRenderer
{
	/**	Holds saved text pane positions. */

	protected Map savedPaneSettings;

	/**	Font for text pane. */

	protected Font font;

	/**	Create settings value renderer.
	 *
	 *	@param	view				The table for this renderer.
	 *	@param	font				Font for rendered view.
	 *	@param	savedPaneSettings	Map of saved text pane settings.
	 */

	public LabeledSettingsValueCellRenderer
	(
		JTable view ,
		Font font ,
		Map savedPaneSettings
	)
	{
		super( view , font );

		this.savedPaneSettings	= savedPaneSettings;
		this.font				= font;
	}

	/** Paint the table cell.
	 *
	 *	@param	g		The graphics context for the table cell.
	 *
	 *	<p>
	 *	The height of the cell is adjusted to match the height of
	 *	the styled text constructed from the setting value.
	 *	</p>
	 */

	public void paint( Graphics g )
	{
                                // Paint the styled text pane.
		super.paint( g );
								// Save the text pane for later use
								// in handling mouse clicks.

		savedPaneSettings.put
		(
			new Integer( row ) ,
			new LabeledSettingsValueTextPaneSettings( this , font )
		);
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

