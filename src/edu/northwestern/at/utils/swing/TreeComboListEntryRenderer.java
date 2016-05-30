package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import java.util.Vector;
import java.awt.*;
import javax.swing.plaf.*;
import javax.swing.tree.*;

/*	Custom rendered for a tree combo box.
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

public class TreeComboListEntryRenderer
	extends JLabel
	implements ListCellRenderer
{
	/**	Text offset for each level of the tree. */

	protected static final int OFFSET = 16;

	/**	Empty border for each node. */

	protected static Border emptyBorder = new EmptyBorder( 0 , 0 , 0 , 0 );

	/**	Default leaf node icon. */

	protected static ImageIcon leafIcon;

	/**	Default non-leaf node icon. */

	protected static ImageIcon nodeIcon;

	/**	True if root node is displayed, false otherwise. */

	protected boolean isRootVisible	= true;

	/**	Create renderer.
	 *
	 *	@param	isRootVisible	true if root node should be displayed.
	 */

	public TreeComboListEntryRenderer( boolean isRootVisible )
	{
		super();

		setOpaque( true );

		this.isRootVisible	= isRootVisible;
	}

	/**	Set colors for an entry.
	 *
	 *	@param	isSelected	True if entry is selected.
	 */

	public void setColors( boolean isSelected )
	{
								//	Set entry colors depending upon whether
								//	the entry is selected or not.
		if ( isSelected )
		{
			setBackground(
				UIManager.getColor( "ComboBox.selectionBackground" ) );

			setForeground(
				UIManager.getColor( "ComboBox.selectionForeground" ) );
		}
		else
		{
			setBackground(
				UIManager.getColor( "ComboBox.background" ) );

			setForeground(
				UIManager.getColor( "ComboBox.foreground" ) );
		}
	}

	/**	Get renderer.
	 *
	 *	@param	listbox		The list box whose entries are being rendered.
	 *	@param	value		The value to be rendered.
	 *	@param	index		The index of the entry being rendered.
	 *	@param	isSelected	True if entry is selected.
	 *	@param	hasFocus	True if entry has focus.
	 *
	 *	@return				The renderer component.
	 */

	public Component getListCellRendererComponent
	(
		JList listbox ,
		Object value ,
		int index ,
		boolean isSelected ,
		boolean hasFocus
	)
	{
								//	Get the entry to render.

		TreeListEntry listEntry = (TreeListEntry)value;

								//	Nothing to display yet.
		setIcon( null );
		setText( "" );
								//	If root, and we're not displaying the
								//	root, return null.  This will prevent
								//	the root from being displayed.

		if ( !isRootVisible && ( index == 0 ) )
		{
			return null;
		}
								//	Otherwise, if list entry is not null,
								//	get the text to display.

		if ( listEntry != null )
		{
								//	Not null.  Get the text from the
								//	object, and set the icon depending
								//	upon whether is a leaf node or not.
			Border border;

			setText( listEntry.getObject().toString() );
			setIcon( listEntry.getIsNode() ? nodeIcon : leafIcon );

								//	Set a border for the entry display to
								//	get the icon and text properly indented.

			if ( index != -1 )
			{
				int theOffset;

				if ( isRootVisible )
				{
					theOffset	=	OFFSET * listEntry.getLevel();
				}
				else
				{
					theOffset	=	OFFSET * ( listEntry.getLevel() - 1 );
				}

				border =
					new EmptyBorder( 0 , theOffset , 0 , 0 );
			}
			else
				border = emptyBorder;

			setBorder( border );

								//	Set entry colors depending upon whether
								//	the entry is selected or not.

			setColors( isSelected );
		}

		return this;
	}

	/**	Static initializer. */

	static
	{
		leafIcon	=
			new ImageIcon
			(
				TreeComboListEntryRenderer.class.getResource
				(
					"/edu/northwestern/at/utils/swing/resources/page.gif"
				) ,
				"Document"
			);

		nodeIcon	=
			new ImageIcon
			(
				TreeComboListEntryRenderer.class.getResource
				(
					"/edu/northwestern/at/utils/swing/resources/folder.gif"
				) ,
				"Folder"
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

