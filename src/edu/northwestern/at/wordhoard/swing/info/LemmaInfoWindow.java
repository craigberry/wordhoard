package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.find.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.utils.db.*;

/**	Lemma get info window.
 */

public class LemmaInfoWindow extends AbstractWindow {

	/**	Font size. */

	private static final int FONT_SIZE = 10;

	/**	Width and height of window contents. */

	private static final int WIDTH = 370;

	private static final int HEIGHT = 420;

	/**	The lemma. */

	private Lemma lemma;

	/**	The tabbed pane. */

	private JTabbedPane tabbedPane;

	/**	The word forms tab. */

	private WordFormsTab forms;

	/**	Creates a new lemma info window.
	 *
	 *	@param	lemma			Lemma.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public LemmaInfoWindow (Lemma lemma, Corpus corpus,
		AbstractWindow parentWindow)
			throws PersistenceException
	{
		super("Lemma \u201C" + lemma.getTag().getString() +
			"\u201D", parentWindow);
		this.lemma = lemma;

		LemmaSummaryTab summary = new LemmaSummaryTab(lemma, corpus,
			FONT_SIZE, WIDTH, this);
		summary.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		Dimension size = summary.getPreferredSize();
		size.height = HEIGHT;

		forms = new WordFormsTab(lemma, corpus, FONT_SIZE,
			size, this);
		forms.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		tabbedPane = new JTabbedPane();
		tabbedPane.add("Summary", summary);
		tabbedPane.add("Word Forms", forms);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(
			0, 0, WordHoardSettings.getGrowSlop(), 0));
		tabbedPane.setForeground(Color.BLACK); // avoid white text w/Java 8 on macOS

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(tabbedPane, BorderLayout.CENTER);

		setContentPane(panel);
		pack();
		setLocation(new Point(3, WordHoardSettings.getTopSlop()));
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
		if (tabbedPane.getSelectedIndex() == 0) {
			defaults = lemma;
		} else {
			defaults = forms.getSearchDefaults();
		}
		new FindWindow(getCorpus(), defaults, this);
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

