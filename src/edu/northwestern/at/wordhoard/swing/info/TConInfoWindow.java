package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.swing.find.*;
import edu.northwestern.at.utils.db.*;

/**	Table of contents get info window.
 */

public class TConInfoWindow extends AbstractWindow {

	/**	Font size. */

	private static final int FONT_SIZE = 10;

	/**	Min label width. */

	private static final int MIN_LABEL_WIDTH = 140;

	/**	Max value width. */

	private static final int MAX_VALUE_WIDTH = 200;

	/**	Corpus. */

	private Corpus corpus;

	/**	Work. */

	private Work work;

	/**	Work part. */

	private WorkPart workPart;

	/**	Tabbed pane. */

	private JTabbedPane tabbedPane;

	/**	Creates a new table of contents get info window.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	workPart		Work part, or null if none selected.
	 *
	 *	@param	parentWindow	Parent table of contents window.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public TConInfoWindow (Corpus corpus, WorkPart workPart,
		AbstractWindow parentWindow)
			throws PersistenceException
	{
		super(corpus.getTitle(), parentWindow);
		this.corpus = corpus;
		this.workPart = workPart;
		this.work = workPart == null ? null : workPart.getWork();
		setResizable(false);
		Insets insets = new Insets(0, 5, 0, 5);
		CorpusInfoTab corpusTab = new CorpusInfoTab(corpus, FONT_SIZE,
			insets, MIN_LABEL_WIDTH, MAX_VALUE_WIDTH);
		corpusTab.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		if (work == null) {
			setContentPane(corpusTab);
		} else {
			WorkInfoTab workTab = new WorkInfoTab(work, FONT_SIZE,
				insets, MIN_LABEL_WIDTH, MAX_VALUE_WIDTH);
			workTab.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
			tabbedPane = new JTabbedPane();
			tabbedPane.add("Corpus", corpusTab);
			tabbedPane.add("Work", workTab);
			tabbedPane.setSelectedComponent(workTab);
			if (workPart != null && !workPart.equals(work)) {
				WorkPartInfoTab workPartTab = new WorkPartInfoTab(workPart,
					FONT_SIZE, insets, MIN_LABEL_WIDTH, MAX_VALUE_WIDTH);
				workPartTab.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
				tabbedPane.add("Part", workPartTab);
				tabbedPane.setSelectedComponent(workPartTab);
			}
			tabbedPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
			setContentPane(tabbedPane);
		}
		pack();
		setLocation(new Point(3, WordHoardSettings.getTopSlop()));
		Dimension size = getSize();
		size.width = Math.max(size.width, 270);
		setSize(size);
		positionNextTo(parentWindow);
		setVisible(true);
	}

	/**	Handles the "Find" command.
	 *
	 *	@throws	Exception	general error.
	 */

	public void handleFindWordsCmd ()
		throws Exception
	{
		SearchDefaults defaults = null;
		if (tabbedPane == null) {
			defaults = corpus;
		} else {
			switch (tabbedPane.getSelectedIndex()) {
				case 0: defaults = corpus; break;
				case 1: defaults = work; break;
				case 2: defaults = workPart; break;
			}
		}
		new FindWindow(corpus, defaults, this);
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

