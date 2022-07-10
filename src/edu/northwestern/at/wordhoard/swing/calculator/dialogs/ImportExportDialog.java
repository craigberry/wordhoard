package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	Base class for import and export dialogs.
 */

public abstract class ImportExportDialog extends SetDialog
{
	/**	The tree holding the items. */

	protected JTree itemsTree	= new JTree();

	/**	True if items tree created. */

	protected boolean itemsTreeCreated	= false;

	/**	Scroll pane for items tree. */

	protected XScrollPane scrollPane	= new XScrollPane( itemsTree );

	/** Document holding items to import.
	 */

	protected org.w3c.dom.Document importDocument	= null;

	/**	Selected items. */

	protected UserDataObject[] selectedItems	= null;

	/**	Title for items tree.
	 */

	protected String treeTitle					= "";

	/**	Title for root node of items tree.
	 */

	protected String treeRootTitle				= "";

	/**	Check box for automatic renaming of duplicate entries.
	 */

	protected JCheckBox renameDuplicatesCheckBox	= new JCheckBox();

	/**	True to rename duplicates.
	 */

	protected static boolean renameDuplicates		= true;

	/**	Create new export dialog.
	 *
	 *	@param	title		Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 *	@param	document	DOM document if importing, else null.
	 */

	public ImportExportDialog
	(
		String title ,
		JFrame parent ,
		org.w3c.dom.Document document
	)
	{
		super
		(
			title ,
			parent ,
			WordHoardSettings.getString
			(
				( document != null ) ? "Import" : "Export" ,
				"Import"
			) ,
			WordHoardSettings.getString( "Cancel" , "Cancel" ) ,
			WordHoardSettings.getString( "Revert" , "Revert" ) ,
			false
		);

		if ( document != null )
		{
			this.treeTitle	=
				WordHoardSettings.getString
				(
					"Selectitemstoimport" ,
					"Select items to import:"
				);

			this.treeRootTitle	=
				WordHoardSettings.getString
				(
					"itemsavailableforimport" ,
					"<html><strong>Items available for import</strong></html>"
				);

			this.importDocument	= document;
		}
		else
		{
			this.treeTitle	=
				WordHoardSettings.getString
				(
					"Selectitemstoexport" ,
					"Select items to export:"
				);

			this.treeRootTitle	=
				WordHoardSettings.getString
				(
					"itemsavailableforexport" ,
					"<html><strong>Items available for export</strong></html>"
				);
        }

		buildDialog();
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
								//	Create items tree.
		createItemsTree();
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		initializeFields();

		dialogFields.addPair( treeTitle , scrollPane );
	}

	/**	Get phrase sets.
	 *
	 *	@return		The phrase sets.
	 */

	abstract protected PhraseSet[] getPhraseSets();

	/**	Get word sets.
	 *
	 *	@return		The word sets.
	 */

	abstract protected WordSet[] getWordSets();

	/**	Get work sets.
	 *
	 *	@return		The work sets.
	 */

	abstract protected WorkSet[] getWorkSets();

	/**	Get work part queries.
	 *
	 *	@return		The work part queries.
	 */

	abstract protected WHQuery[] getWorkPartQueries();

	/**	Get word queries.
	 *
	 *	@return		The word queries.
	 */

	abstract protected WHQuery[] getWordQueries();

	/**	Builds the export items tree.
	 */

	protected void createItemsTree()
	{
								//	Get list of phrase items.

		int availableSets		= 0;

		PhraseSet[] phraseSets	= getPhraseSets();

        availableSets 			+= phraseSets.length;

								//	Get list of word items.

		WordSet[] wordSets		= getWordSets();

        availableSets 			+= wordSets.length;

								//	Get list of work items.

		WorkSet[] workSets		= getWorkSets();

        availableSets 			+= workSets.length;

								//	Get list of word queries.

		WHQuery[] wordQueries	= getWordQueries();

        availableSets 			+= wordQueries.length;

								//	Get list of work queries.

		WHQuery[] workQueries	= getWorkPartQueries();

        availableSets 			+= workQueries.length;

								//	Create root node of tree.

		CheckBoxTreeNode root	= new CheckBoxTreeNode( treeRootTitle );

								//	Set root node into tree.

		DefaultTreeModel treeModel	=
			(DefaultTreeModel)itemsTree.getModel();

		treeModel.setRoot( root );

								//	Set toggle click count high to prevent
								//	clicking on check boxes from expanding
								//	or contracting a node.

		itemsTree.setToggleClickCount( 100000 );

								//	Add phrase sets.

		if ( phraseSets.length > 0 )
		{
			addNode
			(
				root ,
				WordHoardSettings.getString
				(
					"exportphrasesets" ,
					"Phrase sets"
				) ,
				phraseSets
			);
		}
								//	Add word sets.

		if ( wordSets.length > 0 )
		{
			addNode
			(
				root ,
				WordHoardSettings.getString
				(
					"exportwordsets" ,
					"Word sets"
				) ,
				wordSets
			);
		}
								//	Add work sets.

		if ( workSets.length > 0 )
		{
			addNode
			(
				root ,
				WordHoardSettings.getString
				(
					"exportworksets" ,
					"Work sets"
				) ,
				workSets
			);
		}
								//	Add word queries.

		if ( wordQueries.length > 0 )
		{
			addNode
			(
				root ,
				WordHoardSettings.getString
				(
					"exportwordqueries" ,
					"Word queries"
				) ,
				wordQueries
			);
		}
								//	Add work queries.

		if ( workQueries.length > 0 )
		{
			addNode
			(
				root ,
				WordHoardSettings.getString
				(
					"exportworkqueries" ,
					"Work queries"
				) ,
				workQueries
			);
		}
								//	Add mouse listener to tree nodes.

		itemsTree.addMouseListener( new CheckBoxTreeNodeMouseListener() );

								//	Set cell renderer for tree nodes.

		itemsTree.setCellRenderer
		(
			new ExtendedCheckBoxTreeNodeRenderer
			(
				itemsTree ,
				AnalysisDialog.getUseShortWorkTitlesInDialogs()
			)
		);
								//	Enable root handle.

		itemsTree.setShowsRootHandles( true );

								//	Expand all rows.

		itemsTree.expandRow( 0 );

		for ( int i = 1 ; i < itemsTree.getRowCount(); i++ )
		{
			TreePath path	= itemsTree.getPathForRow( i );

			if ( path != null )
			{
				Object node	= path.getLastPathComponent();

				if ( node != null )
				{
					itemsTree.expandRow( i );
				}
			}
		}

		itemsTreeCreated	= true;
	}

	/**	Handles the OK button pressed.
	 *
	 *	@param	event	The event.
	 */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		ArrayList setList	= new ArrayList();

		for ( int i = 1 ; i < itemsTree.getRowCount(); i++ )
		{
			TreePath path	= itemsTree.getPathForRow( i );

			if ( path != null )
			{
				CheckBoxTreeNode node	=
					(CheckBoxTreeNode)path.getLastPathComponent();

				if ( ( node != null ) && ( node.isChecked() ) )
				{
					Object value	=
						((CheckBoxTreeNode)node).getObject();

					if ( value instanceof UserDataObject )
					{
						setList.add( value );
					}
				}
			}
        }

		if ( setList.size() <= 0 )
		{
			new ErrorMessage(
				WordHoardSettings.getString(
					"Youdidnotselectanythingtoexport" ,
					"You did not select anything to export." ) );

			selectedItems	= null;
		}
		else
		{
			selectedItems	=
				(UserDataObject[])setList.toArray( new UserDataObject[]{} );

			cancelled	= false;
			dispose();
		}
	}

	/**	Add node and its items to tree.
	 *
	 *	@param	parent		Parent node.
	 *	@param	nodeName	Node name to add.
	 *	@param	items		Sets in node.
	 */

	protected void addNode
	(
		CheckBoxTreeNode parent ,
		String nodeName ,
		Object[] items
	)
	{
		CheckBoxTreeNode base	= new CheckBoxTreeNode( nodeName );

		parent.add( base );

		for ( int i = 0 ; i < items.length ; i++ )
		{
			addNode( base , items[ i ] );
    	}
	}

	/**	Add set to a node.
	 *
	 *	@param	parentNode	Parent node.
	 *	@param	set			Set to add.
	 */

	protected void addNode
	(
		CheckBoxTreeNode parentNode ,
		Object set
	)
	{
		CheckBoxTreeNode node	= new CheckBoxTreeNode( set );
		parentNode.add( node );
	}

	/**	Get the selected items.
	 *
	 *	@return		Array of selected items.
	 */

	public UserDataObject[] getSelectedItems()
	{
		return selectedItems;
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


