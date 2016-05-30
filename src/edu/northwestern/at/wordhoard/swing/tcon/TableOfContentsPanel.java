package edu.northwestern.at.wordhoard.swing.tcon;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.tconview.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	A table of contents panel.
 */

public class TableOfContentsPanel extends JPanel {

	/**	The corpus. */

	private Corpus corpus;

	/**	The parent window. */

	private AbstractWindow parentWindow;

	/**	The display work part tags check box. */

	private JCheckBox displayTagsCheckBox = new JCheckBox("Show tags");

	/**	The display authors check box. */

	private JCheckBox displayAuthorsCheckBox = new JCheckBox("Show authors");

	/**	The display dates check box. */

	private JCheckBox displayDatesCheckBox = new JCheckBox("Show dates");

	/**	View info class. */

	private static class ViewInfo {

		/**	The table of contents view. */

		private TconView view;

		/**	The radio button, or null if there is only one view. */

		private JRadioButton radioButton;

		/**	The tree, or null if not yet created. */

		private Tree tree;

		/**	The scroll pane, or null if not yet created. */

		private JScrollPane scrollPane;

	}

	/**	Array of view info. */

	private ViewInfo[] viewInfoArray;

	/**	The index in the array of view info of the information for the
	 *	currently displayed view.
	 */

	private int curView;

	/**	The number of views. */

	private int numViews;

	/**	Creates a new table of contents panel.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	parentWindow	Parent window.
	 */

	public TableOfContentsPanel (Corpus corpus, AbstractWindow parentWindow) {
		this.corpus = corpus;
		this.parentWindow = parentWindow;
		java.util.List viewList = corpus.getTconViews();
		numViews = viewList.size();
		viewInfoArray = new ViewInfo[numViews];
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createHorizontalGlue());
		ButtonGroup buttonGroup = new ButtonGroup();
		int k = 0;
		for (Iterator it = viewList.iterator(); it.hasNext(); ) {
			TconView view = (TconView)it.next();
			ViewInfo info = new ViewInfo();
			info.view = view;
			viewInfoArray[k++] = info;
			String radioButtonLabel = view.getRadioButtonLabel();
		}
		boolean hasMultipleAuthors = (countAuthors(corpus) > 1);
		for (int i = 0; i < numViews; i++) {
			ViewInfo info = viewInfoArray[i];
			TconView view = info.view;
			if (numViews > 1) {
				String label = view.getRadioButtonLabel();
				JRadioButton radioButton = new JRadioButton(label);
				info.radioButton = radioButton;
				radioButton.addActionListener(radioButtonListener);
				buttonPanel.add(radioButton);
				buttonPanel.add(Box.createHorizontalStrut(5));
				buttonGroup.add(radioButton);
				if (i == 0) radioButton.setSelected(true);
			}
			if (i == 0) {
				Tree tree = new Tree(corpus, view, parentWindow, this);
				JScrollPane scrollPane = getScrollPane(tree);
				info.tree = tree;
				info.scrollPane = scrollPane;
			}
		}
		buttonPanel.add(displayTagsCheckBox);
		if (hasMultipleAuthors) {
			buttonPanel.add(Box.createHorizontalStrut(5));
			buttonPanel.add(displayAuthorsCheckBox);
		}
		buttonPanel.add(displayDatesCheckBox);
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
		displayTagsCheckBox.addActionListener(displayTagsListener);
		displayAuthorsCheckBox.addActionListener(displayAuthorsListener);
		displayDatesCheckBox.addActionListener(displayDatesListener);
		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		add(viewInfoArray[0].scrollPane, BorderLayout.CENTER);
		curView = 0;
	}

	/**	Creates a scroll pane for a tree.
	 *
	 *	@param	tree	The tree.
	 *
	 *	@return		A scroll pane containing the tree.
	 */

	private JScrollPane getScrollPane (Tree tree) {
		JScrollPane scrollPane = new JScrollPane();
		MyViewport viewport = new MyViewport();
		viewport.setView(tree);
		scrollPane.setViewport(viewport);
		scrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.setPreferredSize(new Dimension(370, 300));
		return scrollPane;
	}

	/**	A viewport that never scrolls horizontally.
	 *
	 *	<p>This is a hack that works around a bug in Swing's JTree class.
	 *	With our custom renderer, without the hack, the up and down arrow
	 *	keys cause the view to scroll left. This hack simply disables all
	 *	horizontal scrolling in the viewport.
	 */

	private static class MyViewport extends JViewport {
		public void setViewPosition (Point p) {
			p.x = 0;
			super.setViewPosition(p);
		}
	}

	/**	Requests focus. */

	public void requestFocus () {
		Tree tree = viewInfoArray[curView].tree;
		tree.requestFocus();
	}

	/**	Gets the selected work part.
	 *
	 *	@return		The selected work part, or null if none.
	 */

	public WorkPart getSelectedWorkPart () {
		Tree tree = viewInfoArray[curView].tree;
		TreePath path = tree.getSelectionPath();
		if (path == null) return null;
		TreeNode node = (TreeNode)path.getLastPathComponent();
		if (node == null) return null;
		return node.getWorkPart();
	}

	/**	Gets the current tree.
	 *
	 *	@return		The current tree in this panel.
	 */

	public Tree getTree() {
		return viewInfoArray[curView].tree;
	}

	/**	DisplayTags checkbox action listener. */

	private ActionListener displayTagsListener =
		new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				try {
					repaintTree();
				} catch (Exception e) {
					Err.err(e);
				}
			}
		};

	/**	DisplayAuthors checkbox action listener. */

	private ActionListener displayAuthorsListener =
		new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				try {
					repaintTree();
				} catch (Exception e) {
					Err.err(e);
				}
			}
		};

	/**	DisplayDates checkbox action listener. */

	private ActionListener displayDatesListener =
		new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				try {
					repaintTree();
				} catch (Exception e) {
					Err.err(e);
				}
			}
		};

	/**	Returns true if tags should be displayed.
	 *
	 *	@return		True to display tags.
	 */

	public boolean getDisplayTags () {
		return displayTagsCheckBox.isSelected();
	}

	/**	Returns true if authors should be displayed.
	 *
	 *	@return		True to display authors.
	 */

	public boolean getDisplayAuthors () {
		return displayAuthorsCheckBox.isSelected();
	}

	/**	Returns true if publication dates should be displayed.
	 *
	 *	@return		True to display publication dates.
	 */

	public boolean getDisplayDates () {
		TconView view = viewInfoArray[curView].view;
		int viewType = view.getViewType();
		return viewType == TconView.LIST_PUB_YEAR_VIEW_TYPE |
			displayDatesCheckBox.isSelected();
	}

	/**	Counts number of authors in the corpus.
	 *
	 *	@param	corpus	The corpus.
	 *	@return			The number of different authors in the corpus.
	 */

	private int countAuthors (Corpus corpus) {
		Set authors = new HashSet();
		Set works = corpus.getWorks();
		Iterator it = works.iterator();
		while (it.hasNext()) {
			Work work = (Work)it.next();
			authors.add(work.getAuthors());
		}
		return authors.size();
	}

	/**	Repaints the current tree. */

	private void repaintTree () {
		Tree tree = viewInfoArray[curView].tree;
		nodeChanged((TreeNode)tree.getModel().getRoot());
		validate();
		repaint();
		requestFocus();
	}

	/**	Tells a node that it and its children have changed.
	 *
	 *	@param	node		Tree node.
	 */

	private void nodeChanged (TreeNode node) {
		if (node==null) return;
		Tree tree = viewInfoArray[curView].tree;
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		model.nodeChanged(node);
		int childCount = model.getChildCount(node);
		for (int i=0; i < childCount; i++) {
			TreeNode child = (TreeNode)model.getChild(node, i);
			nodeChanged(child);
		}
	}

	/**	Radio button action listener. */

	private ActionListener radioButtonListener =
		new ActionListener() {
			public void actionPerformed (ActionEvent event) {
				try {
					JRadioButton newButton = (JRadioButton)event.getSource();
					int newView = 0;
					for (int i = 0; i < numViews; i++) {
						ViewInfo info = viewInfoArray[i];
						if (info.radioButton == newButton) {
							newView = i;
							break;
						}
					}
					if (newView == curView) return;
					ViewInfo oldInfo = viewInfoArray[curView];
					ViewInfo newInfo = viewInfoArray[newView];
					curView = newView;
					if (newInfo.tree == null) {
						Tree newTree = new Tree(corpus, newInfo.view,
							parentWindow, TableOfContentsPanel.this);
						newInfo.tree = newTree;
						JScrollPane newScrollPane = getScrollPane(newTree);
						newInfo.scrollPane = newScrollPane;
					}
					remove(oldInfo.scrollPane);
					add(newInfo.scrollPane, BorderLayout.CENTER);
					repaintTree();
				} catch (Exception e) {
					Err.err(e);
				}
			}
		};

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

