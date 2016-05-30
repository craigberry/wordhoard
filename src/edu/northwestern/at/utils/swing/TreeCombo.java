package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

/*	A combobox which displays a drop-down tree instead of a list.
 *
 *	Based upon code originally appearing in Sun's SwingSet demo,
 *	which is covered by the following copyright.
 *
 *	Copyright (c) 1997-1999 by Sun Microsystems, Inc. All Rights Reserved.
 *
 *	Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 *	modify and redistribute this software in source and binary code form,
 *	provided that i) this copyright notice and license appear on all copies of
 *	the software; and ii) Licensee does not utilize the software in a manner
 *	which is disparaging to Sun.
 *
 *	This software is provided "AS IS," without a warranty of any kind. ALL
 *	EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 *	IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 *	NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 *	LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 *	OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 *	LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 *	INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 *	CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 *	OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 *	POSSIBILITY OF SUCH DAMAGES.
 *
 *	This software is not designed or intended for use in on-line control of
 *	aircraft, air traffic, aircraft navigation or aircraft communications; or in
 *	the design, construction, operation or maintenance of any nuclear
 *	facility. Licensee represents and warrants that it will not use or
 *	redistribute the Software for such purposes.
 */

import javax.swing.*;
import javax.swing.tree.*;

public class TreeCombo extends JComboBox
{
	/**	Create a treecombo.
	 */

	public TreeCombo()
	{
		super();
	}

	/**	Create a treecombo.
	 *
	 *	@param	treeModel	The tree model holding the data for the combobox.
	 */

	public TreeCombo( TreeModel treeModel )
	{
		super();
								//	Set model which can map a tree model
								//	to a list model.

		setModel( new TreeToListModel( treeModel ) );

								//	Set the custom list renderer.

		setRenderer( new TreeComboListEntryRenderer( true ) );
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

