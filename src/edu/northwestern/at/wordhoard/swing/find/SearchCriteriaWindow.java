package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.dialogs.*;
import edu.northwestern.at.wordhoard.swing.concordance.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.querytool.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;

import edu.northwestern.at.wordhoard.swing.text.*;

/**	A search results window.
 */

public class SearchCriteriaWindow extends FindWindow
	implements ActionListener, WindowFocusListener
{
	/**	Font size. */

	private static final int FONT_SIZE = 12;

	/**	Search criteria panel. */

	private SearchCriteriaPanel panel;

	/**	Search results panel. */

	private SearchCriteriaResultsPanel resultsPanel;

	/**	Persistence manager. */

	private PersistenceManager pm;

	/**	Search thread. */

	private Thread searchThread = null;

	/**	Save word set thread. */

	private Thread saveWordSetThread = null;

	/**	Choose what we're looking for. */

	JComboBox targetComboBox = null;

	/**	Reports number of matching lemmata. */

	JLabel	numHitsLabel = null;

	/**	Result word set. */

	WordSet wordSet	= null;

	/**	Search button initiates lemma search. */

	JButton b =
		new JButton
		(
			WordHoardSettings.getString
			(
				"Search" ,
				"Search"
			)
		);

	/**	Creates a new search results window and fills it with query results
	 *
	 *	@param	parentWindow		Parent window.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public SearchCriteriaWindow( AbstractWindow parentWindow )
		throws PersistenceException
	{
		super("Lemma Search", parentWindow);

		pm = new PersistenceManager();

		FontManager fontManager = new FontManager();
		Font font = fontManager.getFont(FONT_SIZE);

		// main content panel
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setAlignmentX(Component.LEFT_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

		// panel for instructions and result type selection pop=up
		JPanel instructions = new JPanel();
		instructions.setLayout(new BoxLayout(instructions, BoxLayout.Y_AXIS));
		instructions.add(Box.createHorizontalGlue());
		instructions.setAlignmentX(Component.LEFT_ALIGNMENT);
		instructions.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));

		// Instruction text
		StringBuffer buf = new StringBuffer();
		buf.append("Drag any items from the Table of Contents, Lexicon, Parts of Speech, ");
		buf.append("or Word Class tables, or  \"Find Words\", \"Find Works\" search result groupings, ");
		buf.append(" or lemmata from the lower panel into the upper panel.");
		buf.append(" Those values will form search constraints for your lemma search.");
		int newwidth = 570;
		WrappedTextComponent verbiage = new WrappedTextComponent(buf.toString(), font, newwidth);
		verbiage.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
		verbiage.setAlignmentX(Component.LEFT_ALIGNMENT);

		// panel for result type selection pop=up
		JPanel resultTypeChoice = new JPanel();
		resultTypeChoice.setLayout(new BoxLayout(resultTypeChoice, BoxLayout.X_AXIS));
		resultTypeChoice.setAlignmentX(Component.LEFT_ALIGNMENT);
		resultTypeChoice.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));

		numHitsLabel = new JLabel("  ");
		numHitsLabel.setFont(font);
		numHitsLabel.setBorder(BorderFactory.createEmptyBorder(3,5,0,0));
		numHitsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		b.setVerticalTextPosition(AbstractButton.CENTER);
		b.setEnabled(false);
		b.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
		b.addActionListener(new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
							//	Disable search button while
							//	search is in progress.

							b.setEnabled( false );

							//	Clear results.

							resultsPanel.setContents(new ArrayList() , panel );

							//	Run search.

							numHitsLabel.setText( "Searching..." );

							find();

					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);


//		targetComboBox = new JComboBox(font);
		targetComboBox = new JComboBox();
		targetComboBox.addItem("lemmata");
		targetComboBox.addItem("word forms");
		targetComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		Dimension cbSize = targetComboBox.getSize();
		cbSize.width = 20;
		targetComboBox.setMaximumSize(cbSize);
		targetComboBox.setPreferredSize(cbSize);

		panel = new SearchCriteriaPanel(this);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
		panel.setSearchButton(b);

//		JComboBox boolComboBox = new JComboBox(font);
		JComboBox boolComboBox = new JComboBox();
		boolComboBox.addItem("any");
		boolComboBox.addItem("all");
		boolComboBox.addItem("none");
		boolComboBox.setEnabled(false);
		boolComboBox.setSize(boolComboBox.getPreferredSize());
	/*	Dimension bbSize = boolComboBox.getSize();
		bbSize.width = 100;
		boolComboBox.setMaximumSize(bbSize);
		boolComboBox.setPreferredSize(bbSize); */

		panel.setBoolRelationshipCombo(boolComboBox);
		boolComboBox.addActionListener(panel);


//		resultTypeChoice.add(look);
		resultTypeChoice.add(b);
		resultTypeChoice.add(Box.createHorizontalStrut(3));
//		resultTypeChoice.add(boolComboBox);
		resultTypeChoice.add(Box.createHorizontalStrut(10));
		resultTypeChoice.add(numHitsLabel);

//		resultTypeChoice.add(targetComboBox);
//		resultTypeChoice.add(Box.createHorizontalStrut(5));
//		resultTypeChoice.add(b);


	//	targetComboBox.addActionListener(panel);

		enableSelectAllCmd(false);

		JButton c = new JButton("Show Counts");
		c.setVerticalTextPosition(AbstractButton.CENTER);
		c.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
		c.addActionListener(new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						showCounts();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);


		instructions.add(verbiage);
		instructions.add(Box.createVerticalStrut(3));
		instructions.add(resultTypeChoice);
//		instructions.add(Box.createVerticalStrut(3));
//		instructions.add(new JPanel().add(numHitsLabel));

		instructions.add(Box.createVerticalStrut(3));
		JPanel tp = new JPanel();
		TitledBorder tb = new TitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), "Add any of the following criteria:", TitledBorder.LEADING, TitledBorder.BELOW_TOP);

		tp.setLayout(new BoxLayout(tp, BoxLayout.Y_AXIS));
		tp.add(Box.createHorizontalGlue());
		tp.setAlignmentX(Component.LEFT_ALIGNMENT);
		tp.setBorder(tb);

		Row row = new Row(this, new Row[0]);
		row.setMinusEnabled(false);
		row.removeCriterion(WorkCriterion.class);
		row.removeCriterion(WorkPartCriterion.class);
//		Row row = addRow(null);
		tp.add(row);
		instructions.add(Box.createHorizontalStrut(3));
		instructions.add(tp);

//		setContentPane(p);

		resultsPanel = new SearchCriteriaResultsPanel(this);;
		resultsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		resultsPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(panel);
		splitPane.setBottomComponent(resultsPanel);
		splitPane.setDividerLocation(0.25);
		splitPane.setContinuousLayout(true);
		splitPane.setResizeWeight(0.25);
		splitPane.setBorder(null);

		p.setLayout(new BorderLayout());
		p.add(instructions, BorderLayout.NORTH);
		p.add(Box.createVerticalStrut(10));
		p.add(splitPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.add(Box.createHorizontalGlue());
		bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(0,5,0,0));
		bottomPanel.add(Box.createVerticalStrut(5));

		JLabel doubleClick = new JLabel("Double-click on search results for concordance.");
		doubleClick.setAlignmentX(Component.LEFT_ALIGNMENT);
		bottomPanel.add(doubleClick);
		bottomPanel.add(Box.createVerticalStrut(5));

		p.add(bottomPanel,BorderLayout.SOUTH);


		Dimension size = getPreferredSize();
		size.width = 580;
		setSize(size);


		setContentPane(p);
// added

		pack();
		Dimension windowSize = parentWindow.getSize();
		Dimension screenSize = getToolkit().getScreenSize();
		windowSize.height = (screenSize.height) -
			(WordHoardSettings.getTopSlop() + WordHoardSettings.getBotSlop());
		windowSize.width=590;
		setSize(windowSize);
		setLocation(new Point(3, WordHoardSettings.getTopSlop()));
		positionNextTo(getParentWindow());

		addWindowFocusListener(this);
		setVisible(true);

		addWindowListener(
			new WindowAdapter() {
				public void windowClosed (WindowEvent event) {
					new Thread (
						new Runnable() {
							public void run () {

								//	Close search thread.

								try {
									if(searchThread!=null) {
										if(searchThread.isAlive())
												{
												if(!searchThread.isInterrupted()) searchThread.interrupt();
												searchThread.join();
										}
										searchThread=null;
									}
								} catch (Exception e) {Err.err(e);}

								//	Close save word set thread.

								try
								{
									if( saveWordSetThread != null )
									{
										if ( saveWordSetThread.isAlive() )
										{
											if( !saveWordSetThread.isInterrupted() )
											{
												saveWordSetThread.interrupt();
											}

											saveWordSetThread.join();
										}

										saveWordSetThread=null;
									}
								}
								catch ( Exception e )
								{
									Err.err( e );
								}

								closePM( pm );
							}
						}
					).start();
				}
			}
		);
	}

	/**	new action
	 *
	 */
	protected void newWindow() {
		try {
			new SearchCriteriaWindow((AbstractWindow)getParentWindow());
		} catch (PersistenceException e) {Err.err(e);}
	}

    public void actionPerformed(ActionEvent e) {
		String action = (String)e.getActionCommand();
		if (action.equals("quit")) {WordHoard.quit();}
    }

	/**	Handles "Save Word Set" command.
	 */

	public void handleSaveWordSetCmd() {
		try {
			saveWordSet();
		} catch (Exception e) {
			Err.err(e);
		}
	}

	public void handleFindWordsCmd ()
		throws Exception
	{
//		SearchDefaults defaults = workPanel.getWord();
//		if (defaults == null) defaults = workPanel.getWorkPart();
//		new FindWindow(getCorpus(), defaults, this);
		new FindWindow(null, null, this);
	}

	/**	Handles the "Go To" command.
	 *
	 *	@param	str		Ignored.
	 *
	 *	@throws	Exception	general error.
	 */

	public void handleGoToWordCmd (String str)
		throws Exception
	{
//		Word word = workPanel.getWord();
//		String tag = word == null ? null : word.getTag();
//		super.handleGoToWordCmd(tag);
		super.handleGoToWordCmd(null);
	}


	/**	Executes the search.
	 *
	 *	@throws	Exception	general error.
	 */

	private void find ()
		throws Exception
	{
		final SearchCriteriaLemmaSearch sqls = new SearchCriteriaLemmaSearch(panel.getCriteria());
//		final Thread searchThread = new Thread (
		try {
			if(searchThread!=null) {
				if(searchThread.isAlive())
						{
						if(!searchThread.isInterrupted()) searchThread.interrupt();
						searchThread.join();
				}
				searchThread=null;
			}
		} catch (Exception e) {
		}

		searchThread = new Thread (
			new Runnable() {
				public void run () {
					try {
						final long startTime = System.currentTimeMillis();
						final java.util.List searchResults = pm.searchLemmata(sqls);
						PersistenceManager.closePM();
						int numHits = searchResults.size();
						String numHitsStr = Formatters.formatIntegerWithCommas(numHits);

						long endTime = System.currentTimeMillis();
						float time = (endTime - startTime) / 1000.0f;
						String timeStr = Formatters.formatFloat(time, 1);
						numHitsLabel.setText(numHitsStr + (numHits == 1 ? " lemma found" : " lemmata found") + " in " + timeStr + " seconds");

						b.setEnabled( true );

						if (Thread.interrupted()) {
							PersistenceManager.closePM();
							return;
						}

						SwingUtilities.invokeLater(
							new Runnable() {
								public void run () {
									try {
										resultsPanel.setContents(searchResults,panel);
									} catch (Exception e) {
										Err.err(e);
									}
								}
							}
						);
					} catch (PersistenceException e) {
						Err.err(e);
					} finally {
						PersistenceManager.closePM();
					}
				}
			}
		);

		searchThread.setPriority(searchThread.getPriority()-1);
		searchThread.start();
	}

	private void findOLD ()
		throws Exception
	{
		SearchCriteria sq = new SearchCriteria();
		Collection constraints = panel.getCriteria();
		for (Iterator it = constraints.iterator(); it.hasNext(); ) {
			SearchCriterion obj = (SearchCriterion)it.next();
			sq.add((SearchCriterion)obj);
		}

		if (sq.suspicious()) {
			WarningDialog dlog = new WarningDialog();
			dlog.show(this);
			if (dlog.canceled) return;
		}
		new ConcordanceWindow(sq, getParentWindow());
	}



	private void showCounts ()
		throws Exception
	{
		SearchCriteriaLemmaWorkCount sqlwc = new SearchCriteriaLemmaWorkCount("Work");
		Collection constraints = panel.getCriteria();
		for (Iterator it = constraints.iterator(); it.hasNext(); ) {
			SearchCriterion obj = (SearchCriterion)it.next();
			sqlwc.add((SearchCriterion)obj);
		}
//		final PersistenceManager pm = WordHoard.getPm();
		final SearchCriteriaLemmaWorkCount s = sqlwc;
		final Thread searchThread = new Thread (
			new Runnable() {
				public void run () {
					try {
						final long startTime = System.currentTimeMillis();
						final java.util.List searchResults = pm.searchWords(s);
						final ArrayList words = new ArrayList();
						for (Iterator it = searchResults.iterator();
							it.hasNext(); )
						{
								Map m = (Map)it.next();
						}
						if (Thread.interrupted()) return;
					} catch (PersistenceException e) {
						Err.err(e);
					} finally {
						PersistenceManager.closePM();
					}
				}
			}
		);
		searchThread.setPriority(searchThread.getPriority()-1);
		searchThread.start();
	}

	private void createWorkSet ()
		throws Exception
	{
		SearchCriteria sq = new SearchCriteria();
		Collection constraints = panel.getCriteria();
		for (Iterator it = constraints.iterator(); it.hasNext(); ) {
			SearchCriterion obj = (SearchCriterion)it.next();
			sq.add((SearchCriterion)obj);
		}

		if (sq.suspicious()) {
			WarningDialog dlog = new WarningDialog();
			dlog.show(this);
			if (dlog.canceled) return;
		}
		new ConcordanceWindow(sq, getParentWindow());
	}

	private void loadWorkPartSet ()
		throws Exception
	{
		OpenWorkSetDialog dialog = new OpenWorkSetDialog("Select Work Set", getParentWindow());
		dialog.show( this );

		if ( !dialog.getCancelled() )
		{

			WorkSet ws = dialog.getSelectedItem();
			WorkPart[] workparts = WorkSetUtils.getWorkParts(ws);
			if(workparts != null) {
				ArrayList alist = new ArrayList();
				for ( int i = 0 ; i < workparts.length ; i++ )
				{
					alist.add(workparts[i]);
				}
				panel.addNodes(alist);
			} else {
				System.out.println(getClass().getName() + " loadWorkSet - works is null");
			}
		}
		dialog.dispose();
	}

	/**	Close a persistence manager.
	 *
	 *	@param	pm	Persistence manager to close.
	 */

	protected void closePM( PersistenceManager pm )
	{
		try
		{
			if ( pm != null ) pm.close();
			pm = null;
		}
		catch ( Exception e )
		{
		}
	}

	/**	Perform lemma search for creating a word set.
	 *
	 *	@param	pm					Persistence manager for search.
	 *	@param	progressReporter	Progress reporter.
	 *
	 *	@return		List of words matched by search criteria.
	 *
	 *	@throws		Exception	general error.
	 */

	private java.util.List doSearch
	(
		ProgressReporter progressReporter
	)
		throws Exception
	{
								//	Initialize holder for
								//	search criteria.

		SearchCriteria sqt	= new SearchCriteria();

								//	Add each search criterion to search
								//	criteria.

		Collection constraints = panel.getCriteria();

		for	(	Iterator it = constraints.iterator();
				it.hasNext();
			)
		{
			SearchCriterion obj	= (SearchCriterion)it.next();
			sqt.add( (SearchCriterion)obj );
		}
								//	Copy search criteria to final
								//	for use in search thread.

		SearchCriteria sq		= sqt;

								//	Get search start time.

		final long startTime = System.currentTimeMillis();

								//	Update progress report.

		if ( progressReporter != null )
		{
			progressReporter.updateProgress
			(
				WordHoardSettings.getString
				(
					"Findingwords" ,
					"Finding words"
				)
			);

			progressReporter.setIndeterminate( true );
		}
								//	Perform word search.

		java.util.List searchResults = pm.searchWords( sq );

								//	Get words from results.

		ArrayList words	= new ArrayList();

		for	(	Iterator it = searchResults.iterator();
				it.hasNext();
			)
		{
			SearchResult searchResult	= (SearchResult)it.next();
			words.add( searchResult.getWord() );
		}
								//	Return list of words.
		return words;
	}

	/**	Helper method for saving lemma search results as a word set.
	 */

	protected void doSaveWordSet()
	{
								//	Saved word set.
		wordSet	= null;

								//	True to display messages.
								//	We don't do it if the thread
								//	in which this method runs is
								//	interrupted.

		boolean showMessages	= true;

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
		try
		{
			SwingUtilities.invokeLater
			(
				new Runnable()
				{
					public void run()
					{
								//	Disable search button.

						b.setEnabled( false );

								//	Set initial display title.

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
								//	Create the word set.
								//	Search is performed by
								//	"doSearch" above.
			wordSet	=
				WordSetUtils.saveWordSet
				(
					parentWindow,
					new WordGetter()
					{
						public java.util.List getWords( ProgressReporter pr )
							throws Exception
						{
							return doSearch( pr );
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
		catch ( InterruptedException e )
		{
			wordSet			= null;
			showMessages	= false;
		}
		catch ( Exception e )
		{
			wordSet	= null;
			Err.err( e );
		}
		finally
		{
								//	Close thread persistence manager
								//	in case we used it.

			PersistenceManager.closePM();

								//	Report success or failure of save
								//	unless we know thread was interrupted.

			if ( showMessages )
			{
				SwingUtilities.invokeLater
				(
					new Runnable()
					{
						public void run()
						{
							progressReporter.close();

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
								//	Enable search button.

							b.setEnabled( true );
						}
					}
				);
			}
		}
	}

	/**	Save results of lemma search as a word set.
	 */

	private void saveWordSet()
		throws Exception
	{
								//	Error if no search criteria specified.

		if ( panel.getCriteria().size() == 0 )
		{
			WarningDialog wd	= new WarningDialog();
			wd.show( this );
			return;
		}
								//	Set up thread to wrap search
								//	and word set creation.
		String threadName	=
			WordHoardSettings.getString
			(
				"Createwordset" ,
				"Create word set"
			);

		saveWordSetThread	=
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
				saveWordSetThread , awtEventThread.getPriority() - 1 );
		}
								//	Start thread.

		saveWordSetThread.start();
	}

	/**	The warning dialog. */

	private static class WarningDialog extends ModalDialog {
		private boolean canceled = true;
		private WarningDialog() {
			super(null);
			setResizable(false);
			JLabel warningIcon =
				new JLabel(UIManager.getLookAndFeel().getDefaults().getIcon(
					"OptionPane.warningIcon"));
			warningIcon.setAlignmentY(Component.TOP_ALIGNMENT);
			StringBuffer buf = new StringBuffer();
			buf.append("You have not added any search criteria!");
			FontManager fontManager = new FontManager();
			Font font = fontManager.getFont(12);
			WrappedTextComponent textPanel1 =
				new WrappedTextComponent(buf.toString(), font, 250);
			textPanel1.setAlignmentX(Component.LEFT_ALIGNMENT);
			JPanel textBox = new JPanel();
			textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
			textBox.add(textPanel1);
			textBox.setAlignmentY(Component.TOP_ALIGNMENT);
			JPanel box = new JPanel();
			box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
			box.add(warningIcon);
			box.add(Box.createHorizontalStrut(20));
			box.add(textBox);
			add(box);
			addDefaultButton("Ok",
				new ActionListener() {
					public void actionPerformed (ActionEvent event) {
						dispose();
					}
				}
			);
		}
	}

	public void windowGainedFocus(WindowEvent e) {
		saveWordSetCmd.setEnabled( WordHoardSettings.isLoggedIn() );
		saveAsCmd.setEnabled(false);
	}

	public void windowLostFocus(WindowEvent e) {
		saveWordSetCmd.setEnabled(false);
	}

	/**	Handles file menu selected.
	 *
	 *
	 *	@throws	Exception	general error.
	 */

	public void handleFileMenuSelected ()
		throws Exception
	{
		newWorkSetCmd.setEnabled(true);
		openWorkSetCmd.setEnabled(true);
		saveWorkSetCmd.setEnabled(false);
		saveWordSetCmd.setEnabled( WordHoardSettings.isLoggedIn() );
	}

	/**	Adjusts menu items to reflect logged-in status.
	 *
	 *	<p>
	 *	Enables/disables the "Save as Word Set" command in this window.
	 *	</p>
	 */

	public void adjustAccountCommands()
	{
		super.adjustAccountCommands();

		saveWordSetCmd.setEnabled( WordHoardSettings.isLoggedIn() );
	}

	/**	Adds a new row.
	 *
	 *	@param	row		Row after which to add new row, or null
	 *					to add the new row at the end.
	 *
	 *	@return			The new row.
	 */


	public Row addRow (Row row) {

		CriterionComponent cc = row.getCriterionComponent();
		if((cc instanceof WorkCriterion) ||
			(cc instanceof WorkPartCriterion))
//			(cc instanceof WorkSetCriterion) ||
//			(cc instanceof PhraseSetCriterion))
//			(cc instanceof WordSetCriterion))
			{return row;}

		SearchCriterion sc = (SearchCriterion) row.getCriterionComponent().getValue();
		if(sc!=null)
			{
			panel.addCriterion(sc);
			b.setEnabled(true);
		}
		return row;
	}

/*
	private void addCriteria(Row formlet) {
		CriterionComponent criterionComponent = formlet.getCriterionComponent();
		Class cls = criterionComponent.getClass();
		SearchCriterion newVal = criterionComponent.getValue();
	} */

	/**	Adjusts the criteria panel.
	 *
	 *	<p>The panel is revalidated and repainted.
	 *
	 *	<p>The minus button of the first row is enabled iff there is more
	 *	than one row in the panel.
	 *
	 *	<p>The plus buttons in each row are enabled iff there are fewer than
	 *	Row.NUM_CRITERIA rows in the panel.
	 *
	 *	<p>All the row combo boxes are rebuilt.
	 */

	private void adjustCriteriaPanel () {
/*		criteriaPanel.revalidate();
		criteriaPanel.repaint();
		Row[] rows = getRows();
		int numRows = rows.length;
		Row firstRow = rows[0];
		firstRow.setMinusEnabled(numRows > 1);
		for (int i = 0; i < numRows; i++) {
			Row row = rows[i];
			row.setPlusEnabled(numRows < Row.NUM_CRITERIA);
		}
		rebuildRowComboBoxes();
		*/
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

