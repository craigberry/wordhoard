package edu.northwestern.at.wordhoard.swing.calculator.widgets;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.tree.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.userdata.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;

/**	A tree combo with mixed text and WordCounter objects as entries.
 */

public class WordCounterTreeCombo
	extends TreeCombo
{
	/**	Root node for tree to display in combo box.
	 */

	protected DefaultMutableTreeNode root	= null;

	/**	Create tree combo box of corpora, phrase sets, works, worksets, and wordsets.
	 *
	 *	@param	includePhraseSets	Include phrase sets.
	 *	@param	includeWorks		Include works.
	 *	@param	includeWordSets		Include word sets.
	 *	@param	includeWorkSets		Include work sets.
	 *	@param	defaultSelection	Object to display as initial selection.
	 */

	public WordCounterTreeCombo
	(
		boolean includePhraseSets ,
		boolean includeWorks ,
		boolean includeWordSets ,
		boolean includeWorkSets ,
		WordCounter defaultSelection
	)
	{
		super();
								//	Get list of corpora.

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
								//	Get list of phrase sets.

//$$$PIB$$$ Restore when phrase sets get implemented.
//		PhraseSet[] phraseSets	= PhraseSetUtils.getPhraseSets();

		PhraseSet[] phraseSets	= new PhraseSet[ 0 ];

		includePhraseSets		=
			includePhraseSets && ( phraseSets.length > 0 );

								//	Get list of work sets.

		WorkSet[] workSets		= WorkSetUtils.getWorkSets();

		includeWorkSets			=
			includeWorkSets && ( workSets.length > 0 );

								//	Get list of word sets.

		WordSet[] wordSets		= WordSetUtils.getWordSets();

		includeWordSets			=
			includeWordSets && ( wordSets.length > 0 );

								//	Root node for texts tree.
		root	=
			new DefaultMutableTreeNode
			(
				WordHoardSettings.getString( "Texts" , "Texts" )
			);

		DefaultMutableTreeNode corporaRoot	= null;

		if ( includeWorks )
		{
			corporaRoot	=
				new DefaultMutableTreeNode
				(
					WordHoardSettings.getString
					(
						"CorporaandWorks" ,
						"Corpora and Works"
					)
				);
		}
		else
		{
			corporaRoot	=
				new DefaultMutableTreeNode
				(
					WordHoardSettings.getString
					(
						"Corpora" ,
						"Corpora"
					)
				);
		}

		DefaultMutableTreeNode phraseSetRoot	=
			new DefaultMutableTreeNode
			(
				WordHoardSettings.getString( "PhraseSets" , "Phrase Sets" )
			);

		DefaultMutableTreeNode wordSetRoot	=
			new DefaultMutableTreeNode
			(
				WordHoardSettings.getString( "WordSets" , "Word Sets" )
			);

		DefaultMutableTreeNode workSetRoot	=
			new DefaultMutableTreeNode
			(
				WordHoardSettings.getString( "WorkSets" , "Work Sets" )
			);

		root.add( corporaRoot );
		if ( includePhraseSets ) root.add( phraseSetRoot );
		if ( includeWordSets ) root.add( wordSetRoot );
		if ( includeWorkSets ) root.add( workSetRoot );

								//	Get all works for each corpus.

		for ( int i = 0 ; i < corpora.length ; i++ )
		{
			Corpus corpus	= corpora[ i ];
			Work[] works	= null;

			if ( includeWorks )
			{
				SortedArrayList	sortedWorkList	=
					new SortedArrayList( corpus.getWorks() );

				works	= (Work[])sortedWorkList.toArray( new Work[]{} );
    		}

			addNodeAndChildren( corporaRoot , corpus , works );
		}
								//	Add work sets.

		if ( includeWorkSets )
		{
			for ( int i = 0 ; i < workSets.length ; i++ )
			{
				addNode( workSetRoot , workSets[ i ] );
			}
		}
								//	Add phrase sets.

		if ( includePhraseSets )
		{
			for ( int i = 0 ; i < phraseSets.length ; i++ )
			{
				addNode( phraseSetRoot , phraseSets[ i ] );
			}
		}
								//	Add word sets.

		if ( includeWordSets )
		{
			for ( int i = 0 ; i < wordSets.length ; i++ )
			{
				addNode( wordSetRoot , wordSets[ i ] );
			}
		}
								//	Create tree model to hold nodes.

		DefaultTreeModel treeModel	= new DefaultTreeModel( root );

								//	Set model which can map a tree model
								//	to a list model.

		setModel( new TreeToListModel( treeModel ) );

								//	Set renderer for tree combo which
								//	knows how to display WordCounter objects.
		setRenderer
		(
			new WordCounterListRenderer
			(
				AnalysisDialog.getUseShortWorkTitlesInDialogs()
			)
		);
								//	Allow picking items with multiple
								//	key strokes.

		setKeySelectionManager( new ComboBoxMultipleKeySelectionManager() );

								//	Set default selection in combo box.

		setTheSelectedItem( defaultSelection );
	}

	/**	Set selected item.
	 *
	 *	@param	defaultSelection	The default selection.
	 */

	protected void setTheSelectedItem( WordCounter defaultSelection )
	{
								//	Set the default selection in the
								//	tree combo box.

		int selectedItemIndex		= -1;
		int firstCanCountWordsIndex	= -1;

		for ( int i = 0 ; i < getItemCount() ; i++ )
		{
			TreeListEntry listEntry		= (TreeListEntry)getItemAt( i );

			Object object				= listEntry.getObject();

			DefaultMutableTreeNode node	= (DefaultMutableTreeNode)object;

			object						= node.getUserObject();

								//	If the default selection is in the
								//	list, make that the item which appears
								//	in the tree combo box.  If the default
								//	selection is not in the list, or there
								//	is no default selection, use the first
								//	item which implements the CanCountWords
								//	interface.

			if ( object instanceof CanCountWords )
			{
				if ( firstCanCountWordsIndex == -1 )
				{
					firstCanCountWordsIndex = i;
				}

				if ( defaultSelection != null )
				{
					try
					{
						if ( object.equals( defaultSelection.getObject() ) )
						{
							selectedItemIndex	= i;
							break;
						}
					}
					catch ( Exception e )
					{
					}
				}
				else
				{
					selectedItemIndex = i;
					break;
				}
			}
		}

		if ( selectedItemIndex == -1 )
		{
			selectedItemIndex	= firstCanCountWordsIndex;
		}
								//	Set selected item.

		setSelectedIndex( selectedItemIndex );
	}

	/**	Create word counter tree combo from existing word counter tree combo.
	 *
	 *	@param	wordCounterTreeCombo	Existing word counter tree
	 *									combo box to clone.
	 *
	 *	@param	defaultSelection		Object to display as initial
	 *									selection.
	 */

	public WordCounterTreeCombo
	(
		WordCounterTreeCombo wordCounterTreeCombo ,
		WordCounter defaultSelection
	)
	{
								//	Clone nodes from source combo box.

		root						=
			wordCounterTreeCombo.getClonedTree();

								//	Create tree model to hold nodes.

		DefaultTreeModel treeModel	= new DefaultTreeModel( root );

								//	Set model which can map a tree model
								//	to a list model.

		setModel( new TreeToListModel( treeModel ) );

								//	Set renderer for tree combo which
								//	knows how to display WordCounter objects.
		setRenderer
		(
			new WordCounterListRenderer
			(
				AnalysisDialog.getUseShortWorkTitlesInDialogs()
			)
		);
								//	Allow picking items with multiple
								//	key strokes.

		setKeySelectionManager( new ComboBoxMultipleKeySelectionManager() );

								//	Set default selection in combo box.

		setTheSelectedItem( defaultSelection );
	}

	/**	Get selected item, if any.
	 *
	 *	@return		The selected item, if any, or null if none.
	 */

	public WordCounter getSelectedWordCounter()
	{
		WordCounter result			= null;

								//	Get selected item from tree combo.
								//	It is a TreeListEntry.

		Object selectedItem			= super.getSelectedItem();

		TreeListEntry listEntry		= (TreeListEntry)selectedItem;

		if ( listEntry == null ) return result;

								//	Get the object in the list entry.
								//	It is a DefaultMutableTreeNode.

		Object selectedObject		= listEntry.getObject();

		DefaultMutableTreeNode node	=
			(DefaultMutableTreeNode)selectedObject;

								//	Get the user object from the tree node.
								//	It is either an object that CanCountWords
								//	or a string.

		Object userObject			= node.getUserObject();

								//	If the user object implements
								//	CanCountWords, return it wrapped in
								//	a WordCounter.  Return null for a string.

		if ( userObject instanceof CanCountWords )
		{
			result	= new WordCounter( (CanCountWords)userObject );
		}

		return result;
	}

	/**	Add node and its children to tree combo.
	 *
	 *	@param	root		Root node.
	 *	@param	parent		Parent node to add to root.
	 *	@param	children	Children of parent to add.  May be null if none.
	 */

	protected void addNodeAndChildren
	(
		DefaultMutableTreeNode root ,
		Object parent ,
		Object[] children
	)
	{
		DefaultMutableTreeNode base	= new DefaultMutableTreeNode( parent );

		root.add( base );

		if ( children != null )
		{
			for ( int i = 0 ; i < children.length ; i++ )
			{
				addNode( base , children[ i ] );
    		}
    	}
	}

	/**	Add node to tree combo.
	 *
	 *	@param	parent		Parent node.
	 *	@param	child		Child to add to parent.
	 */

	protected void addNode
	(
		DefaultMutableTreeNode parent ,
		Object child
	)
	{
		DefaultMutableTreeNode node	=
			new DefaultMutableTreeNode( child );

		parent.add( node );
	}

	/**	Get the root node of the combo box tree.
	 *
	 *	@return		The root of the combo box tree.
	 */

	protected DefaultMutableTreeNode getRoot()
	{
		return root;
	}

	/**	Return a shallow clone of the combo box tree.
	 *
	 *	@return		Shallow clone of the combo box tree.
	 */

	protected DefaultMutableTreeNode getClonedTree()
	{
								//	Create root node for cloned tree.

		DefaultMutableTreeNode clonedRoot	=
			new DefaultMutableTreeNode
			(
				WordHoardSettings.getString( "Texts" , "Texts" )
			);
								//	Iterator over child nodes.

		for ( int i = 0 ; i < root.getChildCount() ; i++ )
		{
								//	Next child node.

			DefaultMutableTreeNode child	=
				(DefaultMutableTreeNode)root.getChildAt( i );

								//	Clone child.

			cloneNode( clonedRoot , child );
		}

		return clonedRoot;
	}

	/**	Clone a node.
	 *	@param	parentOfClonedNode	Parent of the node to clone.
	 *	@param	nodeToClone			The node to clone.
	 */

	protected void cloneNode
	(
		DefaultMutableTreeNode parentOfClonedNode ,
		DefaultMutableTreeNode nodeToClone
	)
	{
		DefaultMutableTreeNode clonedNode;

		Object userObject	= nodeToClone.getUserObject();

		if ( userObject instanceof CanCountWords )
		{
			clonedNode	=
				new DefaultMutableTreeNode
				(
					userObject
				);
		}
		else
		{
			String s	= (String)userObject;
			clonedNode	= new DefaultMutableTreeNode( s );
		}

		if ( nodeToClone.getChildCount() > 0 )
		{
			for ( int i = 0 ; i < nodeToClone.getChildCount() ; i++ )
			{
				cloneNode
				(
					clonedNode ,
					(DefaultMutableTreeNode)nodeToClone.getChildAt( i )
				);
			}
		}

		parentOfClonedNode.add( clonedNode );
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


