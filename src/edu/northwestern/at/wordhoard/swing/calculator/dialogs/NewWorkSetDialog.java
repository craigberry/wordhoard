package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.swing.calculator.widgets.*;

/**	Displays create new work set dialog.
 */

public class NewWorkSetDialog extends SetDialog
{
	/**	--- Kinds of New Work Set dialog. --- */

	/**	Select the works/work parts via a query. */

	public static final int VIAQUERY	= 0;

	/**	Select the works/work parts via manual selection. */

	public static final int VIASELECTION	= 1;

	/**	Select the works/work parts from a text object, e.g.,
	 *	corpus, word set, etc.
	 */

	public static final int FROMTEXT	= 2;

	/**	The type of dialog. */

	public int dialogType	= VIAQUERY;

	/**	The tree holding the work parts. */

	protected JTree workPartsTree	= new JTree();

	/**	Label for work parts tree. */

	protected JLabel workPartsTreeLabel	= new JLabel( "" );

	/**	True if work parts tree created. */

	protected boolean workPartsTreeCreated	= false;

	/**	Scroll pane for list of works and workSets. */

	protected XScrollPane treeScrollPane	=
		new XScrollPane( workPartsTree );

	/**	Selected work parts. */

	protected WorkPart[] workSetWorkParts	= null;

	/**	Source text when extracting works/work parts from a text. */

	protected static WordCounter sourceText		= null;

	/**	Dialog field for text from which to extract works or work parts.
	 */

	protected WordCounterTreeCombo sourceTextChoices	= null;

	/**	Work set query text. */

	protected WHQuery workSetQuery						= null;

	/**	The combo box holding the queries. */

	protected JComboBox queries;

								//	Maps work part tags to work parts.

	protected HashMap workTagToWorkMap	= new HashMap();

	/**	Create new workSet dialog.
	 *
	 *	@param	dialogTitle	Title for dialog.
	 *	@param	parent		Parent window for dialog.
	 *	@param	dialogType	Dialog type.
	 */

	public NewWorkSetDialog
	(
		String dialogTitle ,
		JFrame parent ,
		int dialogType
	)
	{
		super
		(
			dialogTitle ,
			parent ,
			WordHoardSettings.getString( "Create" , "Create" ) ,
			WordHoardSettings.getString( "Cancel" , "Cancel" ) ,
			WordHoardSettings.getString( "Revert" , "Revert" ) ,
			false
		);

		this.dialogType	= dialogType;

		treeScrollPane.setPreferredSize( new Dimension( 400 , 140 ) );

		buildDialog();

		setInitialFocus( titleEditField );
	}

	/**	Create new work set dialog.
	 *
	 *	@param	dialogTitle		Title for dialog.
	 *	@param	parent			Parent window for dialog.
	 *	@param	editingWorkSet	True if dialog for editing work set.
	 */

	protected NewWorkSetDialog
	(
		String dialogTitle ,
		JFrame parent ,
		boolean editingWorkSet
	)
	{
		super
		(
			dialogTitle ,
			parent ,
			WordHoardSettings.getString
			(
				editingWorkSet ? "Update" : "Update" ,
				editingWorkSet ? "Create" : "Create"
			) ,
			WordHoardSettings.getString( "Cancel" , "Cancel" ) ,
			WordHoardSettings.getString( "Revert" , "Revert" ) ,
			editingWorkSet
		);

		setInitialFocus( titleEditField );
	}

	/**	Initialize the dialog fields. */

	protected void initializeFields()
	{
		super.initializeFields();

								//	Create work parts tree.

		if ( dialogType == VIASELECTION )
		{
			createWorkPartsTree();
		}
								//	Create source text.

		if ( dialogType	== FROMTEXT )
		{
			sourceTextChoices	=
				new WordCounterTreeCombo(
					true , true , true , true , sourceText );

			WordCounter sourceText	=
				sourceTextChoices.getSelectedWordCounter();
		}

		if ( dialogType == VIAQUERY )
		{
								//	Query choices.
			queries	=
				new JComboBox( QueryUtils.getQueries( WHQuery.WORKPARTQUERY ) );

								//	Set renderer for queries.

			queries.setRenderer
			(
				new QueriesListRenderer()
			);
		}
	}

	/** Adds fields to the dialog.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		super.addFields( dialogFields );

		workPartsTreeLabel	=
			new JLabel
			(
				WordHoardSettings.getString
				(
					"worksAndWorkSetsHTML" ,
					"<html><strong>Corpora</strong>, " +
					"<strong><em>Work sets</em></strong>,<br>Works, " +
					"and Work Parts:</html>"
				) + ":"
            );
								//	Add dialog type dependent fields.
		switch ( dialogType	)
		{

			case VIAQUERY	:
				dialogFields.addPair
				(
					WordHoardSettings.getString
					(
						"Queries" ,
						"Queries"
					) ,
					queries
				);

				break;

			case FROMTEXT	:
				dialogFields.addPair
				(
					WordHoardSettings.getString
					(
						"SourceWorks" ,
						"Source Works"
					) ,
					sourceTextChoices
				);

				break;

//			case VIASELECTION :
			default			:

								//	Add tree of work parts if we're
								//	selection parts manually.

				dialogFields.addPair
				(
					workPartsTreeLabel ,
					treeScrollPane
				);

				break;
		}
	}

	/**	Builds the work part tree.
	 */

	protected void createWorkPartsTree()
	{
		long startTime	= System.currentTimeMillis();

								//	Get work parts.

		long startTime2	= System.currentTimeMillis();

		java.util.List workParts	= null;

		try
		{
			workParts	=
				WordHoard.getPm().query
				(
					"from WorkPart wkp " +
					"left join fetch wkp.children"
				);
		}
		catch ( Exception e )
		{
			Err.err( e );
		}
								// Create map from work tags to works.

		for (	Iterator iterator	= workParts.iterator() ;
				iterator.hasNext() ;
			)
		{
			WorkPart workPart	= (WorkPart)iterator.next();

								//	Work parts with no parents are works.

			if ( workPart.getParent() == null )
			{
				workTagToWorkMap.put( workPart.getTag() , workPart );
			}
		}
								//	Get work sets and their work tags.
								//	This isn't used directly. it helps
								//	speed up subsequent references.

		java.util.List workSetsList	=
			PersistenceManager.doQuery
			(
				"from WorkSet wks " +
				"left join fetch wks.workPartTags "
			);

		long endTime2	= System.currentTimeMillis() - startTime2;

								//	Get corpora.

		Corpus[] corpora		= null;

		try
		{
			corpora	= CachedCollections.getCorpora();

			Arrays.sort( corpora,
				new Comparator()
				{
					public int compare( Object o1 , Object o2 )
					{
						Corpus c1	= (Corpus)o1;
						Corpus c2	= (Corpus)o2;

						return
							Compare.compareIgnoreCase
							(
								c1.getTitle() ,
								c2.getTitle()
							);
					}
				}
			);
		}
		catch ( Exception e )
		{
			Err.err( e );
		}
								//	Get list of work sets.

		WorkSet[] workSets		= WorkSetUtils.getWorkSets();

								//	Create root node of tree.

		CheckBoxTreeNode root	=
			new CheckBoxTreeNode
			(
				WordHoardSettings.getString
				(
					"rootWorkSetNodeLabel" ,
					"<html><strong>Corpora, Work Sets, Works, and " +
					"Work parts</strong></html>"
				)
			);
								//	Set root node into tree.

		DefaultTreeModel treeModel	=
			(DefaultTreeModel)workPartsTree.getModel();

		treeModel.setRoot( root );

								//	Set toggle click count high to prevent
								//	clicking on check boxes from expanding
								//	or contracting a node.

		workPartsTree.setToggleClickCount( 100000 );

								//	Get all works for each corpus.

		for ( int i = 0 ; i < corpora.length ; i++ )
		{
			Corpus corpus	= corpora[ i ];

			SortedArrayList	sortedWorkList	=
				new SortedArrayList( corpus.getWorks() );

			Work[] works	= (Work[])sortedWorkList.toArray( new Work[]{} );

			addCorpusNode( root , corpus , works );
		}
								//	Get all works for each work set.
		if ( !editingSet )
		{
			for ( int i = 0 ; i < workSets.length ; i++ )
			{
				WorkSet workSet	= workSets[ i ];

				addWorkSetNode( root , workSet );
			}
		}
								//	Add mouse listener to tree nodes.

		workPartsTree.addMouseListener(
			new CheckBoxTreeNodeMouseListener() );

								//	Set cell renderer for tree nodes.

		workPartsTree.setCellRenderer
		(
			new ExtendedCheckBoxTreeNodeRenderer
			(
				workPartsTree ,
				AnalysisDialog.getUseShortWorkTitlesInDialogs()
			)
		);
								//	Enable root handle.

		workPartsTree.setShowsRootHandles( true );

								//	Expand all rows for corpora and works,
								//	but not work parts.

		workPartsTree.expandRow( 0 );

		for ( int i = 1 ; i < workPartsTree.getRowCount(); i++ )
		{
			TreePath path	= workPartsTree.getPathForRow( i );

			if ( path != null )
			{
				Object node	= path.getLastPathComponent();

				if ( node != null )
				{
					Object value	= ((CheckBoxTreeNode)node).getObject();

					if	(	( value instanceof Corpus ) ||
							( value instanceof WorkSet ) )
					{
						workPartsTree.expandRow( i );
					}
				}
			}
		}

		workPartsTreeCreated	= true;

		long endTime	= System.currentTimeMillis() - startTime;
	}

	/**	Get work parts from tree.
	 *
	 *	@return		List of work parts selected in tree.
	 */

	protected WorkPart[] getSelectedWorkPartsFromTree()
	{
		ArrayList workPartList	= new ArrayList();

		for ( int i = 1 ; i < workPartsTree.getRowCount(); i++ )
		{
			TreePath path	= workPartsTree.getPathForRow( i );

			if ( path != null )
			{
				CheckBoxTreeNode node	=
					(CheckBoxTreeNode)path.getLastPathComponent();

				CheckBoxTreeNode parentNode	=
					(CheckBoxTreeNode)path.getParentPath().getLastPathComponent();

				if ( ( node != null ) && ( node.isChecked() ) )
				{
					if ( ( parentNode != null ) && ( parentNode.isChecked() ) )
					{
							//	Do nothing if parent node checked.
					}
					else
					{
						Object value		=
							((CheckBoxTreeNode)node).getObject();

						if	( value instanceof Corpus )
						{
							Work[] works	=
								CorpusUtils.getWorks( (Corpus)value );

        					for ( int j = 0 ; j < works.length ; j++ )
							{
								workPartList.add( works[ j ] );
							}
						}
						else if ( value instanceof WorkSet )
						{
							WorkPart[] workParts	=
								WorkSetUtils.getWorkParts( (WorkSet)value );

							for ( int j = 0 ; j < workParts.length ; j++ )
							{
								workPartList.add( workParts[ j ] );
							}
						}
						else
						{
							workPartList.add( value );
						}
					}
				}
			}
		}

		return (WorkPart[])workPartList.toArray( new WorkPart[]{} );
	}

	/**	Handles the OK button pressed.
	 *
	 *	@param	event	The event.
	 */

	protected void handleOKButtonPressed( ActionEvent event )
	{
		title			=
			StringUtils.trim( titleEditField.getText() );

		description		=
			StringUtils.trim( descriptionEditField.getText() );

		webPageURL		=
			StringUtils.trim( webPageURLEditField.getText() );

		isPublic		= isPublicCheckBox.isSelected();

		if ( title.length() == 0 )
		{
			new ErrorMessage(
				WordHoardSettings.getString
				(
					"youDidNotEnterATitle",
					"You did not enter a title." )
				);
		}
		else
		{
			if ( dialogType == FROMTEXT )
			{
				sourceText	=
					(WordCounter)sourceTextChoices.getSelectedWordCounter();

				workSetWorkParts	= sourceText.getWorkParts();
			}
			else if ( dialogType == VIAQUERY )
			{
				workSetQuery		= (WHQuery)queries.getSelectedItem();
				workSetQuery		= QueryUtils.getQuery( workSetQuery );

				try
				{
					workSetWorkParts	=
						CQLQueryUtils.getWorkPartsViaQuery
						(
							workSetQuery.getQueryText()
						);
				}
				catch ( Exception e )
				{
					e.printStackTrace();
					workSetWorkParts	= null;
					new ErrorMessage( e.getMessage() );
				}
			}
			else if ( dialogType == VIASELECTION )
			{
				workSetWorkParts	=
					getSelectedWorkPartsFromTree();
            }

			if ( workSetWorkParts != null )
			{
				if ( workSetWorkParts.length <= 0 )
				{
					new ErrorMessage(
						WordHoardSettings.getString(
							"youDidNotSelectAnyTitles",
							"You did not select any titles." ) );

					workSetWorkParts	= null;
				}
				else
				{
					if ( !editingSet )
					{
						if ( WorkSetUtils.getWorkSet(
							title , WordHoardSettings.getUserID() ) != null )
						{
							new ErrorMessage(
								WordHoardSettings.getString(
									"Thatworksetalreadyexists",
									"That work set already exists." ) );
						}
						else
						{
							cancelled	= false;
							dispose();
						}
					}
					else
					{
						cancelled	= false;
						dispose();
					}
				}
			}
		}
	}

	/**	Add corpus and its works and work parts to tree.
	 *
	 *	@param	root	Root node.
	 *	@param	corpus	Corpus to add.
	 *	@param	works	Works in corpus.
	 */

	protected void addCorpusNode
	(
		CheckBoxTreeNode root ,
		Corpus corpus ,
		Work[] works
	)
	{
		CheckBoxTreeNode base	= new CheckBoxTreeNode( corpus );

		root.add( base );

		for ( int i = 0 ; i < works.length ; i++ )
		{
			addWorkPartNode( base , works[ i ] );
    	}
	}

	/**	Add workset and its works and work parts to tree.
	 *
	 *	@param	root	Root node.
	 *	@param	workSet	Work set to add.
	 */

	protected void addWorkSetNode
	(
		CheckBoxTreeNode root ,
		WorkSet workSet
	)
	{
		CheckBoxTreeNode base	= new CheckBoxTreeNode( workSet );

		root.add( base );

		Collection tags	= workSet.getWorkPartTags();

		SortedArrayList workList	= new SortedArrayList();

		for ( Iterator iterator = tags.iterator() ; iterator.hasNext() ; )
		{
			String tag	= (String)iterator.next();

			if ( workTagToWorkMap.containsKey( tag ) )
			{
				Work work	= (Work)workTagToWorkMap.get( tag );

				workList.add( work );
			}
    	}

    	for ( int i = 0 ; i < workList.size() ; i++ )
    	{
    		Work work	= (Work)workList.get( i );

			addWorkPartNode( base , work );
		}
	}

	/**	Add work part to a node.
	 *
	 *	@param	parentNode	Parent node.
	 *	@param	workPart	Work part to add.
	 */

	protected void addWorkPartNode
	(
		CheckBoxTreeNode parentNode ,
		WorkPart workPart
	)
	{
		CheckBoxTreeNode node	= new CheckBoxTreeNode( workPart );

		parentNode.add( node );

		WorkPart[] childParts	=
			(WorkPart[])workPart.getChildren().toArray( new WorkPart[]{} );

		for ( int i = 0 ; i < childParts.length ; i++ )
		{
			addWorkPartNode( node , childParts[ i ] );
		}
	}

	/**	Get the selected work parts.
	 *
	 *	@return		Array of selected work parts.
	 */

	public WorkPart[] getSelectedWorkParts()
	{
		return workSetWorkParts;
	}

	/**	Get query text.
	 *
	 *	@return		The query text.
	 */

	public String getQueryText()
	{
		return ( workSetQuery != null ) ? workSetQuery.getQueryText() : "";
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


