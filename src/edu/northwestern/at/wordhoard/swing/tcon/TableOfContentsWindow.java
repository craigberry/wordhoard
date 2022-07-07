package edu.northwestern.at.wordhoard.swing.tcon;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.info.*;
import edu.northwestern.at.wordhoard.swing.find.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.utils.db.*;

/**	The table of contents window.
 */

public class TableOfContentsWindow extends AbstractWindow {

	/**	The table of contents window, or null if none is open. */

	private static TableOfContentsWindow tableOfContentsWindow;

	/**	The tag of the preferred corpus, or null if none. */

	private static String corpusPref;

	/**	The corpora. */

	private Corpus[] corpora;

	/**	The tabbed pane. */

	private JTabbedPane tabbedPane = new JTabbedPane();

	/**	Array of panels. */

	private TableOfContentsPanel[] panels;

	/**	Opens or brings to the front the table of contents window.
	 *
	 *	@param	show	true to show table of contents, false to leave display
	 *					status as is.
	 *
	 *	@throws	PersistenceException	problem with persistence layer.
	 */

	public static void open (boolean show)
		throws PersistenceException
	{
		if (tableOfContentsWindow == null) {
			tableOfContentsWindow = new TableOfContentsWindow();
			if (show) {
				tableOfContentsWindow.setVisible(true);
				tableOfContentsWindow.toFront();
			} else {
				tableOfContentsWindow.handleFirstWindowActivation();
				tableOfContentsWindow.removeTheWindowListener();
			}
		} else if (show) {
			tableOfContentsWindow.setVisible(true);
			tableOfContentsWindow.toFront();
		}
	}

	/**	Opens or brings to the front the table of contents window.
	 *
	 *	@throws	PersistenceException	problem with persistence layer.
	 */

	public static void open ()
		throws PersistenceException
	{
		open(true);
	}

	/**	Creates a new table of contents window.
	 *
	 *	@throws	PersistenceException	problem with persistence layer.
	 */

	public TableOfContentsWindow ()
		throws PersistenceException
	{

		super("Table of Contents", null);
		enableGetInfoCmd(true);
		corpora = CachedCollections.getCorpora();
		tabbedPane = new JTabbedPane();
		panels = new TableOfContentsPanel[corpora.length];

		for (int i = 0; i < corpora.length; i++) {
			Corpus corpus = corpora[i];
			TableOfContentsPanel panel = new TableOfContentsPanel(corpus, this);
			tabbedPane.add(corpus.getTitle(), panel);
			panels[i] = panel;
			if (corpus.getTag().equals(corpusPref)) {
				setCorpus(corpus);
				tabbedPane.setSelectedIndex(i);
			}
		}

		tabbedPane.addChangeListener (
			new ChangeListener() {
				public void stateChanged (ChangeEvent event) {
					try {
						int i = tabbedPane.getSelectedIndex();
						setCorpus(corpora[i]);
						panels[i].requestFocus();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(tabbedPane, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEmptyBorder(
			0, 0, WordHoardSettings.getGrowSlop(), 0));

		setContentPane(panel);

		pack();
		Dimension screenSize = getToolkit().getScreenSize();
		Dimension windowSize = getSize();
		windowSize.width = 500;
		windowSize.height = screenSize.height -
			(WordHoardSettings.getTopSlop() +
			WordHoardSettings.getBotSlop());
		setSize(windowSize);
		setLocation(new Point(3, WordHoardSettings.getTopSlop()));
	}

	/**	Handles the first window activation event.
	 *
	 *	<p>Closes the splash window.
	 */

	public void handleFirstWindowActivation () {
		WordHoard.closeSplashScreen();
		int i = tabbedPane.getSelectedIndex();
		if (i >= 0) panels[i].requestFocus();
	}

	/**	Handles window dispose events. */

	public void dispose () {
		tableOfContentsWindow = null;
		super.dispose();
	}

	/**	Sets the corpus.
	 *
	 *	@param	corpus		Corpus.
	 */

	public void setCorpus (Corpus corpus) {
		super.setCorpus(corpus);
		corpusPref = corpus.getTag();
	}

	/**	Handles the "Get Info" command.
	 *
	 *	@throws	Exception	general error.
	 */

	public void handleGetInfoCmd ()
		throws Exception
	{
		int i = tabbedPane.getSelectedIndex();
		Corpus corpus = corpora[i];
		TableOfContentsPanel panel = panels[i];
		WorkPart workPart = panel.getSelectedWorkPart();
		new TConInfoWindow(corpus, workPart, this);
	}

	/**	Gets the corpus preference.
	 *
	 *	@return		The tag of the preferred corpus.
	 */

	public static String getCorpusPref () {
		return corpusPref;
	}

	/**	Sets the corpus preference.
	 *
	 *	@param	corpusPref		The tag of the preferred corpus.
	 */

	public static void setCorpusPref (String corpusPref) {
		TableOfContentsWindow.corpusPref = corpusPref;
	}

	/**	Handles the "Find" command.
	 *
	 *	@throws	Exception	general error.
	 */

	public void handleFindWordsCmd ()
		throws Exception
	{
		int i = tabbedPane.getSelectedIndex();
		Corpus corpus = corpora[i];
		TableOfContentsPanel panel = panels[i];
		WorkPart workPart = panel.getSelectedWorkPart();
		new FindWindow(corpus, workPart, this);
	}

	/**	Get table of contents window.
	 * @return table of contents window.
	 * @throws PersistenceException problem with persistence layer.
	 */

	public static TableOfContentsWindow getTableOfContentsWindow()
		throws PersistenceException
	{
		if (tableOfContentsWindow == null) {
			tableOfContentsWindow = new TableOfContentsWindow();
		}
		return tableOfContentsWindow;
	}

	/**	Find a printable component.
	 *
	 *	@return		A printable component.
	 */

	protected Component findPrintableComponent () {
		int i = tabbedPane.getSelectedIndex();
		TableOfContentsPanel tocPanel = (TableOfContentsPanel)panels[i];
		return tocPanel.getTree();
	}

	/**	Handles "Print Preview" command.
	 */

	public void handlePrintPreviewCmd() {
		int i = tabbedPane.getSelectedIndex();
		Corpus corpus = corpora[i];
		doPrintPreview(findPrintableComponent(),
			corpus + " " + getTitle());
	}

	/**	Handles "Print" command.
	 */

	public void handlePrintCmd() {
		int i = tabbedPane.getSelectedIndex();
		Corpus corpus = corpora[i];
		doPrint(findPrintableComponent(),
			corpus + " " + getTitle());
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

