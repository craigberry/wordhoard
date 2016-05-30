package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.html.*;

import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;

/**	Displays edit work set dialog.
 */

public class EditWorkSetDialog
	extends NewWorkSetDialog
{
	/**	List holds work sets. */

	protected DefaultListModel listModel		= new DefaultListModel();
	protected XList workSetsListBox				= new XList( listModel );

	/**	Scroll pane for list of worksets. */

	protected XScrollPane workSetsScrollPane	=
		new XScrollPane( workSetsListBox );

	/**	The work set to update. */

	protected static WorkSet workSetToUpdate	= null;

	/**	Work sets list selection listener. */

	protected WorkSetsListSelectionListener workSetsListSelectionListener =
		null;

	/**	Create edit work set dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 */

	public EditWorkSetDialog
	(
		String dialogTitle ,
		JFrame parent
	)
	{
		super( dialogTitle , parent , true );

		this.dialogType	= VIASELECTION;

		buildDialog();

		workSetsScrollPane.setPreferredSize( new Dimension( 400 , 60 ) );

		treeScrollPane.setPreferredSize( new Dimension( 400 , 100 ) );

		workSetsListSelectionListener.enableDialogFields( false );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Work set to update.

		workSetToUpdate		= null;

								//	Remove current work set list.
		listModel.clear();
								//	Get work sets.

		WorkSet[] workSets	=
			WorkSetUtils.getWorkSetsForLoggedInUser();

								//	Add them to list box.

		if ( workSets != null ) workSetsListBox.setListData( workSets );

								//	Set renderer for work sets to edit.

		workSetsListBox.setCellRenderer(
			new WorkSetsListRenderer() );

								//	Single selection in
								//	work sets list box.

		workSetsListBox.setSelectionMode(
			ListSelectionModel.SINGLE_SELECTION );

								//	Listen for changes to list.

		workSetsListSelectionListener	=
			new WorkSetsListSelectionListener( workPartsTree );

		workSetsListBox.addListSelectionListener(
			workSetsListSelectionListener );
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		dialogFields.addPair
		(
			WordHoardSettings.getString
			(
				"WorkSets" ,
				"Work sets:"
			) ,
			workSetsScrollPane
		);

		super.addFields( dialogFields );
	}

	/**	Handles the OK button pressed. */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		if ( workSetsListBox.getSelectedValue() == null )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"youDidNotSelectAWorkSet",
					"You did not select a work set."
				)
			);

			return;
		}
		else
		{
			workSetToUpdate	= (WorkSet)workSetsListBox.getSelectedValue();
		}

		super.handleOKButtonPressed( event );
	}

	/**	Get the selected work set.
	 *
	 *	@return		Work set to update.
	 */

	public WorkSet getWorkSet()
	{
		return workSetToUpdate;
	}

	/**	Get the selected work parts.
	 *
	 *	@return		Array of WorkPart of selected work parts.
	 */

	public WorkPart[] getWorkSetWorkParts()
	{
		return workSetWorkParts;
	}

	/**	Set checked state for work parts in tree.
	 *
	 *	@param	workPartsTree	Tree holding work part as checkable nodes.
	 *	@param	workParts		Collection of work parts for which
	 *							to set the checked/unchecked state.
	 */

	protected void setWorkPartsChecked
	(
		JTree workPartsTree ,
		Collection workParts
	)
	{
								//	Get the root node of the tree.

		TreeNode root = (TreeNode)workPartsTree.getModel().getRoot();

								//	Visit every node in the tree,
								//	setting the checked/unchecked status
								//	of each node depending upon whether
								//	the corresponding work part is in
								//	the work parts list.

		int firstRowSelected	=
			visitAllTreeNodes( workPartsTree , root , workParts );

		if	(	( firstRowSelected < Integer.MAX_VALUE ) &&
				( firstRowSelected >= 0 ) )
		{
			workPartsTree.scrollRowToVisible( firstRowSelected );
		}
	}

	/**	Visit all tree nodes.
	 *
	 *	@param	tree		The tree.
	 *	@param	node		Tree node.
	 *	@param	workParts	The work parts.
	 */

	public int visitAllTreeNodes
	(
		JTree tree ,
		TreeNode node ,
		Collection workParts
	)
	{
								//	Set checked/unchecked state for
								//	this node.

		int result	= processNode( tree , node , workParts );

								//	If this node has children, visit all
								//	of them too.

		if ( node.getChildCount() >= 0 )
		{
			for	(	Enumeration enumeration = node.children() ;
					enumeration.hasMoreElements() ; )
			{
				TreeNode childNode	= (TreeNode)enumeration.nextElement();

				result	=
					Math.min
					(
						result ,
						visitAllTreeNodes( tree , childNode , workParts )
					);
			}
		}

		return result;
	}

	/**	Process a tree node.
	 *
	 *	@param	tree		The tree.
	 *	@param	treeNode	The tree node.
	 *	@param	workParts	The work parts.
	 *
	 *	@return				The row in the tree for this node if this is
	 *						a work/work part.  MAXINT if not a work/work part.
	 */

	public int processNode
	(
		JTree tree ,
		TreeNode treeNode ,
		Collection workParts
	)
	{
		int result				= Integer.MAX_VALUE;

		CheckBoxTreeNode node	= (CheckBoxTreeNode)treeNode;

								//	Get object at this node.

		Object object			= node.getObject();

								//	We only care about works and work parts.

		if ( object instanceof WorkPart )
		{
								//	Get the work part.

			WorkPart workPart	= (WorkPart)object;

								//	See if this work part is in the
								//	list of work parts to select.

			boolean isSelected	= workParts.contains( workPart );

								//	Set the selected status of the work part.

			node.setChecked( isSelected );

								//	Get the tree path to this node.

			TreePath path	= new TreePath( node.getPath() );

								//	Tell tree we changed the node value.

			tree.getModel().valueForPathChanged( path , node );

								//	If the node is selected, make sure
								//	all its ancestors are expanded so that
								//	it is visible.
			if ( isSelected )
			{
				TreePath parentPath	= path.getParentPath();

			    while ( parentPath != null )
			    {
			    	tree.expandPath( parentPath );

					parentPath	= parentPath.getParentPath();
				}

				result	= tree.getRowForPath( path );
			}
		}

		return result;
	}

	/**	Handle changes to selected work set. */

	protected class WorkSetsListSelectionListener
		implements ListSelectionListener
	{
		/**	The work parts tree. */

		protected JTree workPartsTree;

		/**	Create listener.
		 *
		 *	@param	workPartsTree	The work parts tree.
		 */

		public WorkSetsListSelectionListener( JTree workPartsTree )
		{
			super();
			this.workPartsTree	= workPartsTree;
		}

		/**	Enable or disable dialog fields.
		 *
		 *	@param	enable	true to enable dialog fields, false to disable.
		 */

		public void enableDialogFields( boolean enable )
		{
			titleEditField.setEnabled( enable );
            titleLabel.setEnabled( enable );

			descriptionEditField.setEnabled( enable );
        	descriptionLabel.setEnabled( enable );

			webPageURLEditField.setEnabled( enable );
            webPageURLLabel.setEnabled( enable );

            isPublicCheckBox.setEnabled( enable );

            workPartsTreeLabel.setEnabled( enable );

			if ( enable )
			{
				workPartsTreeLabel.setForeground
				(
					Color.BLACK
				);
			}
			else
			{
				workPartsTreeLabel.setForeground
				(
					Color.LIGHT_GRAY
				);
			}

			enableWorkPartsTree( enable );
		}

		/**	Enable or disable the work parts tree.
		 */

		protected void enableWorkPartsTree( boolean enable )
		{
			workPartsTree.setEnabled( enable );
			treeScrollPane.setEnabled( enable );

			for ( int i = 1 ; i < workPartsTree.getRowCount(); i++ )
			{
				TreePath path	= workPartsTree.getPathForRow( i );

				if ( path != null )
				{
					Object node	= path.getLastPathComponent();

					if ( node != null )
					{
						CheckBoxTreeNode checkBoxNode	=
							((CheckBoxTreeNode)node);

						checkBoxNode.setChecked( false );
					}
				}
			}
		}

		/**	Handle change to selection in workSet list.
		 *
		 *	@param	event	The list selection event.
		 */

		public void valueChanged( ListSelectionEvent event )
		{
								//	Get list which fired this event.

			JList list		= (JList)event.getSource();

								//	Get currently selected value.

			Object object	= list.getSelectedValue();

								//	If there is a selected value ...

			if ( object != null )
			{
								//	Enable the dialog fields.

				enableDialogFields( true );

								//	Set the work set title field and
								//	change the list of checked work parts
								//	in the work parts tree to match those
								//	in the selected work set.
				try
				{
					WorkSet workSet	= (WorkSet)object;

					titleEditField.setText( workSet.getTitle() );

					descriptionEditField.setText(
						workSet.getDescription() );

					webPageURLEditField.setText(
						workSet.getWebPageURL() );

					isPublicCheckBox.setSelected( workSet.getIsPublic() );

					setWorkPartsChecked
					(
						workPartsTree ,
						Arrays.asList( WorkSetUtils.getWorkParts( workSet ) )
					);
				}
				catch ( Exception e )
				{
					Err.err( e );
				}
	        }
	        else
	        {
								//	Disable the dialog fields.

				titleEditField.setText( "" );
				descriptionEditField.setText( "" );
				webPageURLEditField.setText( "" );
				isPublicCheckBox.setSelected( false );

				enableDialogFields( false );
	        }
		}
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

