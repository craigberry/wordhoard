package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;

/**	A component that displays either a progress bar or a small label. */

public class ProgressBarOrLabel extends JPanel {

	/**	The parent component. */

	private Component parent;

	/**	True if progress bar currently displayed. */

	private boolean progressBarDisplayed = false;

	/**	The progress bar. */

	private JProgressBar bar = new JProgressBar(0,0);

	/**	The label message. */

	private XSmallLabel label;

	/**	Constructs a new ProgressBarOrLabel component.
	 *
	 *	@param	parent		The parent component.
	 *
	 *	@param	msg			The initial message for the label.
	 */

	public ProgressBarOrLabel (Component parent, String msg) {
		this.parent = parent;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		label = new XSmallLabel(msg);
		add(label);
	}

	/**	Sets a new label.
	 *
	 *	@param	msg		The message for the label.
	 */

	public void setLabel (String msg) {
		label.setText(msg);
		if (progressBarDisplayed) {
			remove(0);
			add(label);
			progressBarDisplayed = false;
		}
		parent.validate();
		parent.repaint();
	}

	/**	Sets and initializes the progress bar.
	 *
	 *	@param	min			Minimum value for progress bar.
	 *
	 *	@param	max			Maximum value for progress bar.
	 */

	public void setBar (int min, int max) {
		bar.setMinimum(min);
		bar.setMaximum(max);
		bar.setValue(min);
		if (!progressBarDisplayed) {
			remove(0);
			add(bar);
			progressBarDisplayed = true;
		}
		parent.validate();
		parent.repaint();
	}

	/**	Updates the progress bar.
	 *
	 *	@param	val			New value for progress bar.
	 */

	public void updateBar (int val) {
		bar.setValue(val);
	}

	/**	Set progress bar determinate or indeterminate.
	 *
	 *	@param	isIndeterminate		True to set indeterminate.
	 */

	public void setIndeterminate (boolean isIndeterminate) {
		bar.setIndeterminate(isIndeterminate);
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

