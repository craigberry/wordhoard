package edu.northwestern.at.wordhoard.swing.concordance;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.net.*;
import java.text.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.DuplicateWordSetException;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.WordGetter;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.WordSetUtils;
import edu.northwestern.at.wordhoard.swing.work.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.grouping.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.swing.text.*;

/**	A concordance panel.
 */

class ConcordancePanel extends JPanel {

	/**	Font size. */

	private static final int FONT_SIZE = 10;

	/**	Persistence manager. */

	private PersistenceManager pm;

	/**	Search criteria. */

	private SearchCriteria sq;

	/**	The search results. */

	private java.util.List results;

	/**	The matching words. */

	private java.util.List words;

	/**	The number of hits label. */

	private JLabel numHitsLabel;

	/**	The tree. */

	protected XTree tree;

	/**	The work panel. */

	private WorkPanel workPanel;

	/**	The split pane. */

	private JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	/**	Currently displayed work. */

	private Work work;

	/**	Currently displayed hit. */

	private Word curHit;

	/**	Tree cell height. */

	private int cellHeight;

	/**	Max tree height. */

	private int maxTreeHeight;

	/**	Grouping options. */

	private static GroupingOptions[] groupingOptionsArray =
		new GroupingOptions[] {
			null,
		};

	/**	Tree cell renderer. */

	private Renderer renderer;

	/**	Colocate preloader. */

	private ColocatePreloader colocatePreloader;

	/**	True if preceding word form objects have been preloaded. */

	private boolean precedingWordFormObjectsPreloaded;

	/**	True if following word form objects have been preloaded. */

	private boolean followingWordFormObjectsPreloaded;

	/**	Parent window of this panel. */

	private AbstractWorkPanelWindow parentWindow;

	/**	Word set for results. */

	private WordSet wordSet	= null;

	/**	Creates a new concordance panel.
	 *
	 *	@param	pm					Persistence Manager.
	 *
	 *	@param	sq					Search criteria.
	 *
	 *	@param	parentWindow		Parent window.
	 *
	 *	@throws	PersistenceException
	 */

	ConcordancePanel (PersistenceManager pm,
		SearchCriteria sq, AbstractWorkPanelWindow parentWindow)
			throws PersistenceException
	{

		this.pm = pm;
		this.sq = sq;
		this.parentWindow = parentWindow;

		colocatePreloader = new ColocatePreloader(pm);
		Corpus corpus = sq.getCorpus();

		FontManager fontManager = new FontManager();
		Font romanFont = fontManager.getFont(FONT_SIZE);
		FontInfo[] fontInfo = new FontInfo[TextParams.NUM_CHARSETS];
		for (byte charset = 0; charset < TextParams.NUM_CHARSETS; charset++)
			fontInfo[charset] = fontManager.getFontInfo(charset, FONT_SIZE);

		TextLine line = sq.getDescription(fontInfo);
		Text text = new Text(line);
		WrappedTextComponent sqComponent =
			new WrappedTextComponent(text, 450);
		sqComponent.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
		sqComponent.setAlignmentX(Component.LEFT_ALIGNMENT);

		numHitsLabel = new JLabel("Search in progress");
		numHitsLabel.setFont(romanFont);
		numHitsLabel.setBorder(BorderFactory.createEmptyBorder(3,5,0,0));
		numHitsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		GroupingPanel groupingPanel =
			new GroupingPanel(romanFont, sq, groupingOptionsArray, this);
		groupingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		tree = new XTree(new Object[0]);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(
			TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		tree.setToggleClickCount(1000);
		tree.addTreeSelectionListener(
			new TreeSelectionListener () {
				public void valueChanged (TreeSelectionEvent event) {
					try {
						showSelected();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		tree.addMouseListener(
			new MouseAdapter () {
				public void mouseReleased (MouseEvent event) {
					try {
						workPanel.requestFocus();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		JScrollPane scrollPane = new JScrollPane();
		MyViewport viewport = new MyViewport();
		viewport.setView(tree);
		scrollPane.setViewport(viewport);
		scrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel scrollPanel = new JPanel();
		scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
		scrollPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		scrollPanel.add(scrollPane);
		scrollPanel.add(Box.createVerticalStrut(10));

		renderer = new Renderer(FONT_SIZE, corpus,
			scrollPane, colocatePreloader);
		cellHeight = renderer.getHeight();
		tree.setRowHeight(cellHeight);
		tree.setCellRenderer(renderer);

		JPanel hitsPanel = new JPanel();
		hitsPanel.setLayout(new BoxLayout(hitsPanel, BoxLayout.Y_AXIS));
		hitsPanel.add(Box.createVerticalStrut(3));
		hitsPanel.add(sqComponent);
		hitsPanel.add(numHitsLabel);
		hitsPanel.add(Box.createVerticalStrut(3));
		hitsPanel.add(groupingPanel);
		hitsPanel.add(Box.createVerticalStrut(2));

		workPanel = new WorkPanel(pm, parentWindow);
		workPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		workPanel.addKeyListener(
			new KeyAdapter () {
				public void keyPressed (KeyEvent event) {
					try {
						int code = event.getKeyCode();
						if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_UP ||
							code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT)
								tree.dispatchEvent(event);
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		splitPane.setTopComponent(scrollPanel);
		splitPane.setBottomComponent(workPanel);
		splitPane.setDividerLocation(0.45);
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(0.45);
		splitPane.setBorder(null);

		setLayout(new BorderLayout());
		add(hitsPanel, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);

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

	/**	Sets the hits.
	 *
	 *	@param	results		The list of search results.
	 *
	 *	@param	words		List of search result words.
	 *
	 *	@param	startTime	System milliseconds start time of search.
	 *
	 *	@throws	PersistenceException
	 */

	void setHits (java.util.List results, java.util.List words,
		long startTime)
			throws PersistenceException
	{
		maxTreeHeight = tree.getHeight();
		this.results = results;
		this.words = words;
		int numHits = results.size();
		String numHitsStr = Formatters.formatIntegerWithCommas(numHits);
		long endTime = System.currentTimeMillis();
		float time = (endTime - startTime) / 1000.0f;
		String timeStr = Formatters.formatFloat(time, 1);
		numHitsLabel.setText(
			numHitsStr + (numHits == 1 ? " word found" : " words found") +
			" in " + timeStr + " seconds");
		buildTree();
	}

	/**	Builds or rebuilds the tree of hits.
	 *
	 *	@throws	PersistenceException
	 */

	private void buildTree ()
		throws PersistenceException
	{
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		int numRows = buildSubTree(root, results, 0);
		DefaultTreeModel model = new DefaultTreeModel(root);
		tree.setModel(model);
		expand();
		if (results.size() > 0) tree.setSelectionRow(0);
		splitPane.setDividerLocation(0.45);
		int fullHeight = numRows * cellHeight;
		if (fullHeight <= maxTreeHeight) {
			splitPane.setDividerLocation(fullHeight + 25);
			splitPane.setResizeWeight(0.0);
		} else {
			splitPane.setDividerLocation(maxTreeHeight + 25);
			splitPane.setResizeWeight(0.45);
		}
	}

	/**	Builds a subtree of hits.
	 *
	 *	@param	root		Root of subtree.
	 *
	 *	@param	results		Results for subtree.
	 *
	 *	@param	level		Subtree level.
	 *
	 *	@return				Number of rows in subtree.
	 *
	 *	@throws PersistenceException
	 */

	private int buildSubTree (DefaultMutableTreeNode root,
		java.util.List results, int level)
			throws PersistenceException
	{
		GroupingOptions groupingOptions = groupingOptionsArray[level];
		Class groupBy = groupingOptions.getGroupBy();
		preload(groupBy);
		int numRows = 0;
		if (groupBy == null) {
			addResults(root, results);
			numRows = results.size();
		} else {
			Map map = groupingOptions.group(results);
			Set groups = map.keySet();
			for (Iterator it = groups.iterator(); it.hasNext(); ) {
				GroupingObject obj = (GroupingObject)it.next();
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(obj);
				ArrayList list = (ArrayList)map.get(obj);
				if (level == groupingOptionsArray.length-1) {
					addResults(node, list);
					numRows += list.size();
				} else {
					numRows += buildSubTree(node, list, level+1);
				}
				root.add(node);
				numRows++;
			}
		}
		return numRows;
	}

	/**	Expands tree nodes.
	 *
	 *	<p>All but the final (lowest level) grouping nodes are expanded.
	 */

	private void expand () {
		int maxPathCount = groupingOptionsArray.length;
		int row = 0;
		while (true) {
			int numRows = tree.getRowCount();
			while (row < numRows) {
				if (tree.isCollapsed(row) &&
					tree.getPathForRow(row).getPathCount() <= maxPathCount)
						break;
				row++;
			}
			if (row == numRows) return;
			tree.expandRow(row);
			row++;
		}
	}

	/**	Preloads objects for grouping.
	 *
	 *	<p>This method improves performance by preloading objects needed
	 *	for grouping hits by preceding or following word form.
	 *
	 *	@param	groupBy		Group by option class.
	 *
	 *	@throws	PersistenceException
	 */

	private void preload (Class groupBy)
		throws PersistenceException
	{
		if (groupBy == null) return;
		if (groupBy.equals(PrecedingWordForm.class)) {
			if (precedingWordFormObjectsPreloaded) return;
			pm.preloadAdjacentInfo(words, true);
			precedingWordFormObjectsPreloaded = true;
		} else if (groupBy.equals(FollowingWordForm.class)) {
			if (followingWordFormObjectsPreloaded) return;
			pm.preloadAdjacentInfo(words, false);
			followingWordFormObjectsPreloaded = true;
		}
	}

	/**	Adds a collection of results to a node.
	 *
	 *	@param	node		Node.
	 *
	 *	@param	results		List of search results.
	 */

	private void addResults (DefaultMutableTreeNode node,
		java.util.List results)
	{
		ArrayList words = new ArrayList();
		for (Iterator it = results.iterator(); it.hasNext(); ) {
			SearchResult result = (SearchResult)it.next();
			Word word = result.getWord();
			words.add(word);
			DefaultMutableTreeNode woNode =
				new DefaultMutableTreeNode(word, false);
			node.add(woNode);
		}
		colocatePreloader.add(words);
	}

	/**	Handles new grouping options.
	 *
	 *	@param	options		New grouping options.
	 *
	 *	@throws	PersistenceException
	 */

	void handleNewGroupingOptions (GroupingOptions[] options)
		throws PersistenceException
	{
		groupingOptionsArray = options;
		if (words != null) buildTree();
	}

	/**	Gets the work panel. */

	WorkPanel getWorkPanel () {
		return workPanel;
	}

	/**	Returns the concordance tree.
	 *
	 *	@return		The concordance tree.
	 */

	public XTree getConcordanceTree()
	{
		return tree;
	}

	/**	Shows the selected word from the hit list. */

	private void showSelected () {
		workPanel.requestFocus();
		TreePath path = tree.getSelectionPath();
		if (path == null) return;
		DefaultMutableTreeNode node =
			(DefaultMutableTreeNode)path.getLastPathComponent();
		if (node == null) return;
		Object obj = node.getUserObject();
		if (!(obj instanceof Word)) return;
		Word word = (Word)obj;
		if (word == null || word == curHit) return;
		curHit = word;
		new Thread (
			new Runnable() {
				public void run () {
					SwingUtilities.invokeLater(
						new Runnable() {
							public void run () {
								if (curHit.getWork() != work) {
									work = curHit.getWork();
								}
								workPanel.goTo(curHit);
							}
						}
					);
				}
			}
		).start();
	}

	/**	Helper method for saving search results as a word set.
	 */

	protected void doSaveWordSet()
	{
								//	Set up label field for progress
								//	reporting.

		final ProgressReporter progressReporter	=
			new ProgressLabelAdapter
			(
				numHitsLabel ,
				WordHoardSettings.getString
				(
					"Creatingwordset" ,
					"Creating Word Set"
				) ,
				WordHoardSettings.getString
				(
					"Creatingwordset" ,
					"Creating Word Set"
				) ,
				0,
				100,
				1500
			);
								//	Run search using specified criteria
								//	and save resulting lemmata as words
								//	in a word set.
		wordSet	= null;

		try
		{
								//	Set initial output text.

			SwingUtilities.invokeLater
			(
				new Runnable()
				{
					public void run()
					{
						numHitsLabel.setText
						(
							WordHoardSettings.getString
							(
								"SavingWordSet" ,
								"Saving Word Set"
							)
						);
					}
				}
			);
								//	Create the word set.  We already
								//	have the list of words resulting
								//	from the search.

			wordSet	=
				WordSetUtils.saveWordSet
				(
					parentWindow,
					new WordGetter()
					{
						public java.util.List getWords
						( ProgressReporter pr ) throws Exception
						{
							return words;
						}
					} ,
					progressReporter
				);
		}
		catch ( DuplicateWordSetException e )
		{
			wordSet	= null;

			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"duplicatewordset" ,
					"A word set of that name already exists."
				)
			);
		}
		catch ( Exception e )
		{
			wordSet	= null;
			Err.err( e );
		}
		finally
		{
			PersistenceManager.closePM();

			SwingUtilities.invokeLater
			(
				new Runnable()
				{
					public void run()
					{
						progressReporter.close();

								//	Report success or failure of save.

						if ( wordSet != null )
						{
							numHitsLabel.setText
							(
								WordHoardSettings.getString
								(
									"WordSetSaved" ,
									"Word set saved."
								)
							);
						}
						else
						{
							numHitsLabel.setText
							(
								WordHoardSettings.getString
								(
									"WordSetNotSaved" ,
									"Word set could not be saved."
								)
							);
						}
					}
				}
			);
		}
	}

	/**	Save the selected words as a word set.
	 */

	public void saveWordSet () {

								//	Set up thread to wrap search
								//	and word set creation.
		String threadName	=
			WordHoardSettings.getString
			(
				"Createwordset" ,
				"Create word set"
			);

		Thread thread	=
			new Thread( threadName )
			{
				public void run()
				{
					doSaveWordSet();
				}
			};
								//	Thread runs at lower priority
								//	than AWT event thread to keep
								//	interface responsive.

		Thread awtEventThread	= SwingUtils.getAWTEventThread();

		if ( awtEventThread != null )
		{
			ThreadUtils.setPriority(
				thread , awtEventThread.getPriority() - 1 );
		}
								//	Start thread.
		thread.start();
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

