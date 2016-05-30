package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

/*	Maps a tree model to a list model.
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
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.Vector;
import java.awt.*;
import javax.swing.tree.*;

public class TreeToListModel
	extends AbstractListModel
	implements ComboBoxModel, TreeModelListener
{
	/**	The tree model whose entries will be mapped to list entries. */

	protected TreeModel source	= null;

	/**	True if list is currently invalid. */

	protected boolean invalid	= true;

	/**	Currently selected list value. */

	protected Object currentValue;

	/**	Cache of list entries. */

	protected Vector cache = new Vector();

	/**	Create tree to list model mapper.
	 *
	 *	@param	treeModel	The tree model to be mapped to a list model.
	 */

	public TreeToListModel( TreeModel treeModel )
	{
		source	= treeModel;

		treeModel.addTreeModelListener( this );
	}

	/**	----- ComboBoxModel interface methods. ----- */

	/**	Get the selected item.
	 *
	 *	@return		Selected item or null if no item selected.
	 */

	public Object getSelectedItem()
	{
		return currentValue;
	}

	/**	Set the selected item.
	 *
	 *	@param	anItem	The item to make the selected item.
	 *					if null, the selection is cleared.
	 */

	public void setSelectedItem( Object anItem )
	{
		currentValue	= anItem;

		fireContentsChanged( this , -1 , -1 );
	}

	/**	Return size of the list.
	 *
	 *	@return		Size of the list.
	 */

	public int getSize()
	{
		validate();
		return cache.size();
	}

	/**	Get specified list element.
	 *
	 *	@param	index	Index of element to return.
	 *
	 *	@return			The specified list element, or null if index is bad.
	 */

	public Object getElementAt( int index )
	{
		return cache.elementAt( index );
	}

	/**	----- TreeModelListener interface methods. ----- */

	/**	Called after a node (or a set of siblings) has changed in some way.
	 *
	 *	@param	e	The tree model event.
	 */

	public void treeNodesChanged( TreeModelEvent e )
	{
		invalid = true;
	}

	/**	Called after nodes have been inserted into the tree.
	 *
	 *	@param	e	The tree model event.
	 */

	public void treeNodesInserted( TreeModelEvent e )
	{
		invalid = true;
	}

	/**	Called after nodes have been removed from the tree.
	 *
	 *	@param	e	The tree model event.
	 */

	public void treeNodesRemoved( TreeModelEvent e )
	{
		invalid = true;
	}

	/**	Called after the tree has drastically changed structure.
	 *
	 *	@param	e	The tree model event.
	 */

	public void treeStructureChanged( TreeModelEvent e )
	{
		invalid = true;
	}

	/**	Validate the tree/list.
	 */

	protected void validate()
	{
		if ( invalid )
		{
			cache	= new Vector();

			cacheTree( source.getRoot() , 0 );

			if ( cache.size() > 0 )
				currentValue	= cache.elementAt( 0 );

			invalid	= false;

			fireContentsChanged( this , 0 , 0 );
		}
	}

	/**	Add object to list/tree cache.
	 *
	 *	@param	object	Object to add.
	 *	@param	level	Tree depth.
	 */

	protected void cacheTree( Object object , int level )
	{
		if ( source.isLeaf( object ) )
		{
			addListEntry( object , level , false );
		}
		else
		{
			int c	= source.getChildCount( object );

			int i;

			Object child;

			addListEntry( object , level , true );

			level++;

			for ( i = 0 ; i < c ; i++ )
			{
				child	= source.getChild( object , i );
				cacheTree( child , level );
			}

			level--;
		}
	}

	/**	Add an entry to the tree/list.
	 *
	 *	@param	object	Object to add.
	 *	@param	level	Depth in the tree.
	 *	@param	isNode	True if not a leaf node.
	 */

	protected void addListEntry( Object object , int level , boolean isNode )
	{
		cache.addElement( new TreeListEntry( object , level , isNode ) );
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

