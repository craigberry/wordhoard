package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.utils.*;

/**	A plus or minus button.
 */

public class PlusOrMinusButton extends JButton {

	/**	Plus icon. */
	
	private static ImageIcon plusIcon;
	
	/**	Minus icon. */
	
	private static ImageIcon minusIcon;
	
	static {
		ClassLoader loader = PlusOrMinusButton.class.getClassLoader();
		plusIcon = new ImageIcon(loader.getResource(
			"edu/northwestern/at/utils/swing/resources/plus.gif"));
		minusIcon = new ImageIcon(loader.getResource(
			"edu/northwestern/at/utils/swing/resources/minus.gif"));
	}
	
	/** Creates a new plus or minus button. 
	 *
	 *	@param	plus		True for plus, false for minus.
	 */
	
	public PlusOrMinusButton (boolean plus) {
		super(plus ? plusIcon : minusIcon);
		setFocusable(false);
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

