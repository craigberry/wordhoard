package edu.northwestern.at.wordhoard.swing.concordance;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.work.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.printing.*;

/**	A concordance window.
 */

public class ConcordanceWindow extends AbstractWorkPanelWindow {

	/**	Window ordinal. */

	private static int ordinal = 1;

	/**	Persistence manager. */

	private PersistenceManager pm;

	/**	Concordance panel. */

	private ConcordancePanelDnD panel;

	/**	Creates a new concordance window.
	 *
	 *	@param	sq					Search criteria.
	 *
	 *	@param	parentWindow		Parent window.
	 *
	 *	@throws	PersistenceException
	 */

	public ConcordanceWindow (final SearchCriteria sq,
		AbstractWindow parentWindow)
			throws PersistenceException
	{

		super("Search Results " + (ordinal++), parentWindow);

		setCorpus(sq.getCorpus());

		pm = new PersistenceManager();

		panel = new ConcordancePanelDnD(pm, sq, this);
		panel.setBorder(BorderFactory.createEmptyBorder(
			0, 0, 0, 0));

		setContentPane(panel);

		WorkPanel workPanel = panel.getWorkPanel();
		setWorkPanel(workPanel);

		pack();
		Dimension windowSize = getSize();
		Dimension screenSize = getToolkit().getScreenSize();
		windowSize.height = screenSize.height -
			(WordHoardSettings.getTopSlop() + WordHoardSettings.getBotSlop());
		setSize(windowSize);
		setLocation(new Point(3, WordHoardSettings.getTopSlop()));
		positionNextTo(parentWindow);
		setVisible(true);

		final Thread searchThread = new Thread (
			new Runnable() {
				public void run () {
					try {
						final long startTime = System.currentTimeMillis();
						final java.util.List searchResults = pm.searchWords(sq);
						final ArrayList words = new ArrayList();
						for (Iterator it = searchResults.iterator();
							it.hasNext(); )
						{
							SearchResult searchResult = (SearchResult)it.next();
							words.add(searchResult.getWord());
						}
						if (Thread.interrupted()) return;
						pm.preloadConcordanceInfo(words);
						if (Thread.interrupted()) return;
						SwingUtilities.invokeLater(
							new Runnable() {
								public void run () {
									try {
										panel.setHits(searchResults,
											words, startTime);
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
		searchThread.setPriority(searchThread.getPriority()-1);
		searchThread.start();

		addWindowListener(
			new WindowAdapter() {
				public void windowClosed (WindowEvent event) {
					new Thread (
						new Runnable() {
							public void run () {
								try {
									searchThread.interrupt();
									searchThread.join();
								} catch (Exception e) {
								}
								try {
									pm.close();
									pm = null;
								} catch (Exception e) {
								}
							}
						}
					).start();
				}
			}
		);

	}

	/**	Handles file menu selected.
	 *
	 *
	 *	@throws	Exception
	 */

	public void handleFileMenuSelected ()
		throws Exception
	{
		saveWorkSetCmd.setEnabled(false);
		saveWordSetCmd.setEnabled(WordHoardSettings.isLoggedIn());
	}

	/**	Handles "Save Word Set" command.
	 */

	public void handleSaveWordSetCmd() {
		try {
			panel.saveWordSet();
		} catch (Exception e) {
			Err.err(e);
		}
	}

	/**	Adjusts menu items to reflect logged-in status.
	 *
	 *	<p>
	 *	Enables/disables the "Save as Word Set" command in this window.
	 *	</p>
	 */

	public void adjustAccountCommands () {
		super.adjustAccountCommands();
		saveWordSetCmd.setEnabled(WordHoardSettings.isLoggedIn());
	}

	/**	Find a printable component.
	 *
	 *	@return		A printable component.
	 */

	protected Component findPrintableComponent () {
		return this;
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

