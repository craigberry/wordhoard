package edu.northwestern.at.wordhoard.swing.lexicon;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.find.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.db.*;

/**	A lexicon window.
 */

public class LexiconWindow extends AbstractWindow {

	/**	Map from corpora to open lexicon windows. */

	private static HashMap windowMap = new HashMap();

	/**	Opens or brings to the front a lexicon window.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PersistenceException.
	 */

	public static void open (Corpus corpus, AbstractWindow parentWindow)
		throws PersistenceException
	{
		LexiconWindow window = (LexiconWindow)windowMap.get(corpus);
		if (window == null) {
			window = new LexiconWindow(corpus, parentWindow);
			windowMap.put(corpus, window);
		} else {
			window.setVisible(true);
			window.toFront();
		}
	}

	/**	Corpus. */

	private Corpus corpus;

	/**	Persistence manager. */

	private PersistenceManager pm;

	/**	Lexicon panel. */

	private LexiconPanel lexiconPanel;

	/**	Creates a new lexicon window.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PersistenceException.
	 */

	private LexiconWindow (final Corpus corpus, AbstractWindow parentWindow)
		throws PersistenceException
	{
		super(corpus.getTitle() + " Lexicon", parentWindow);
		this.corpus = corpus;
		setCorpus(corpus);

		pm = new PersistenceManager();

		enableGetInfoCmd(true);

		lexiconPanel = new LexiconPanel(corpus, pm, this);
		lexiconPanel.setBorder(BorderFactory.createEmptyBorder(
			10, 10, 10 + WordHoardSettings.getGrowSlop(), 10));
		setContentPane(lexiconPanel);

		pack();
		Dimension screenSize = getToolkit().getScreenSize();
		Dimension windowSize = getSize();
		windowSize.height = screenSize.height -
			(WordHoardSettings.getTopSlop() + WordHoardSettings.getBotSlop());
		setSize(windowSize);
		setLocation(new Point(3, WordHoardSettings.getTopSlop()));
		if (parentWindow == null) {
			WindowPositioning.centerWindowOverWindow(this, null, 0);
		} else {
			positionNextTo(parentWindow);
		}
		setVisible(true);

		final Thread thread = new Thread (
			new Runnable() {
				public void run () {
					try {
						CachedCollections.getMajorWordClasses();
						if (Thread.interrupted()) return;
						final LemmaCorpusCounts[] lexicon =
							CachedCollections.getLexicon(corpus);
						if (Thread.interrupted()) return;
						SwingUtilities.invokeLater(
							new Runnable() {
								public void run () {
									try {
										lexiconPanel.setLexicon(lexicon);
									} catch (Exception e) {
										Err.err(e);
									}
								}
							}
						);
					} catch (PersistenceException e) {
						Err.err(e);
					}
				}
			}
		);
		thread.setPriority(thread.getPriority()-1);
		thread.start();

		addWindowListener(
			new WindowAdapter() {
				public void windowClosed (WindowEvent event) {
					new Thread (
						new Runnable() {
							public void run () {
								try {
									thread.interrupt();
									thread.join();
								} catch (Exception e) {
								}
								try {
									pm.close();
								} catch (Exception e) {
								}
							}
						}
					).start();
				}
			}
		);

	}

	/**	Handles window dispose events. */

	public void dispose () {
		windowMap.remove(corpus);
		super.dispose();
	}

	/**	Handles the "Get Info" command.
	 *
	 *	@throws	Exception
	 */

	public void handleGetInfoCmd ()
		throws Exception
	{
		lexiconPanel.openGetInfoWindow();
	}

	/**	Handles the "Find" command.
	 *
	 *	@throws	Exception
	 */

	public void handleFindWordsCmd ()
		throws Exception
	{
		SearchDefaults defaults = lexiconPanel.getSearchDefaults();
		new FindWindow(getCorpus(), defaults, this);
	}

	/**	Find a printable component.
	 *
	 *	@return		A printable component.
	 */

	protected Component findPrintableComponent () {
		return lexiconPanel.getTable();
	}

	/**	Find a saveable component.
	 *
	 *	@return		A saveable component.  Null=none by default.
	 */

	protected SaveToFile findSaveableComponent() {
		return (XTable)lexiconPanel.getTable();
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

