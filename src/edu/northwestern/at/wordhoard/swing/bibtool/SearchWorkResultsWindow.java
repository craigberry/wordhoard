package edu.northwestern.at.wordhoard.swing.bibtool;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.bibtool.*;
import edu.northwestern.at.wordhoard.swing.work.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

/**	A search results window.
 */

public class SearchWorkResultsWindow extends AbstractWorkPanelWindow {

	/**	Persistence manager. */

	private PersistenceManager pm;

	/**	Search results panel. */

	private SearchWorkResultsPanel panel;

	/**	Creates a new search results window and fills it with query results
	 *
	 *	@param	sq					Search criteria.
	 *
	 *	@param	parentWindow		Parent window.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public SearchWorkResultsWindow (final SearchWorkCriteria sq,
		AbstractWindow parentWindow)
			throws PersistenceException
	{
		super(((sq==null) ? "" : "Search Results for \u201C" + sq.toShortString()) + "\u201D", parentWindow);

		if(sq != null && sq.getCorpus()!=null) {
			setCorpus(sq.getCorpus());
		}

		pm = new PersistenceManager();

		panel = new SearchWorkResultsPanel(pm, sq, this);
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
		//if (parentWindow instanceof GetInfoWindow)
		//	parentWindow = ((GetInfoWindow)parentWindow).getParentWindow();
		positionNextTo(parentWindow);



		setVisible(true);

		if(sq != null) {
			final Thread searchThread = new Thread (
				new Runnable() {
					public void run () {
						try {
							final long startTime = System.currentTimeMillis();
							final java.util.List works = pm.searchWorks(sq);
							if (Thread.interrupted()) return;
							if (Thread.interrupted()) return;
							SwingUtilities.invokeLater(
								new Runnable() {
									public void run () {
										try {
											panel.setHits(works, startTime);
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
									} catch (Exception e) {
									}
								}
							}
						).start();
					}
				}
			);
		}
	}

	public SearchWorkResultsWindow(AbstractWindow parentWindow)
			throws PersistenceException
	{
		this((SearchWorkCriteria)null, parentWindow);
	}

	/**	Sets the works for our panel.
	 *
	 *	@param	works		The list of works
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 *
	 */

	public void setWorks (java.util.List works)
		throws PersistenceException
	{
		panel.setWorks(works);
	}

	/**	Handles "Cut" command.
	 */

	public void handleCutCmd ()
		throws Exception
	{
		panel.cut();
	}

	/**	Handles "Copy" command.
	 *
	 * @throws	Exception	general error.
	 */

	public void handleCOpyCmd ()
		throws Exception
	{
		panel.copy();
	}

	/**	Handles "Paste" command.
	 */

	public void handlePasteCmd ()
		throws Exception
	{
		panel.paste();
	}

	/**	Handles "Save Work Set" command.
	 */

	public void handleSaveWorkSetCmd()
	{
		try
		{
			panel.saveWorkSet();
		}
		catch ( DuplicateWorkSetException e )
		{
			new ErrorMessage
			(
				WordHoardSettings.getString
				(
					"duplicateworkset" ,
					"A work set of that name already exists."
				)
			);
		}
		catch ( Exception e )
		{
			Err.err( e );
		}
	}

	/**	Handles file menu selected.
	 *
	 *
	 *	@throws	Exception	general error.
	 */

	public void handleFileMenuSelected ()
		throws Exception
	{
		saveWorkSetCmd.setEnabled( WordHoardSettings.isLoggedIn() );
		saveWordSetCmd.setEnabled( false );
	}

	/**	Adjusts menu items to reflect logged-in status.
	 *
	 *	<p>
	 *	Enables/disables the "Save Work Set" command in this window.
	 *	</p>
	 */

	public void adjustAccountCommands()
	{
		super.adjustAccountCommands();

		saveWorkSetCmd.setEnabled( WordHoardSettings.isLoggedIn() );
		saveWordSetCmd.setEnabled( false );
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

