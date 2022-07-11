package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.swing.find.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

/**	Word get info window.
 */

public class WordInfoWindow extends AbstractWindow {

	/**	Font size. */

	private static final int FONT_SIZE = 10;

	/**	Window staggering offset. */

	private static final int STAGGERING_OFFSET = 25;

	/**	Opens word get info windows.
	 *
	 *	@param	word			Word.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public static void open (Word word, AbstractWindow parentWindow)
		throws PersistenceException
	{
		if (word == null) {
			new ErrorMessage("No information available.", parentWindow);
			return;
		}
		java.util.List wordParts = word.getWordParts();
		if (wordParts == null || wordParts.size() == 0) {
			new ErrorMessage("No information available.", parentWindow);
			return;
		}
		WordPart[] partsArray = (WordPart[])wordParts.toArray(
			new WordPart[wordParts.size()]);
		int numParts = partsArray.length;
		WordInfoWindow[] windows = new WordInfoWindow[numParts];
		for (int i = numParts-1; i >= 0; i--) {
			WordPart wordPart = partsArray[i];
			String title = 	"Word \u201C" + word.getSpelling() + "\u201D";
			if (numParts > 1) {
				LemPos lemPos = wordPart.getLemPos();
				if (lemPos != null) {
					Lemma lemma = lemPos.getLemma();
					if (lemma != null) {
						Spelling lemmaSpelling = lemma.getSpelling();
						if (lemmaSpelling != null) {
							String lemmaString = lemmaSpelling.getString();
							if (lemmaString != null) {
								title = title + " (" + lemmaString + ")";
							}
						}
					}
				}
			}
			windows[i] = new WordInfoWindow(wordPart, title, parentWindow);
		}
		int maxHeight = 0;
		int maxWidth = 0;
		int stagger = 0;
		for (int i = numParts-1; i >= 0; i--) {
			WordInfoWindow window = windows[i];
			Dimension size = window.getSize();
			maxHeight = Math.max(maxHeight, size.height);
			maxWidth = Math.max(maxWidth, stagger + size.width);
			stagger += STAGGERING_OFFSET;
		}
		Dimension screenSize = windows[0].getToolkit().getScreenSize();
		Dimension parentWindowSize = parentWindow.getSize();
		Point parentLocation = parentWindow.getLocation();
		int screenWidth = screenSize.width;
		int parentWidth = parentWindowSize.width;
		int parentX = parentLocation.x;
		boolean roomOnRight =
			parentX + parentWidth + maxWidth + 12 <= screenWidth;
		int x;
		if (roomOnRight) {
			x = parentX + parentWidth + 9;
		} else {
			x = Math.max(parentX - 9 - maxWidth, 3);
		}
		int y = WordHoardSettings.getTopSlop();
		stagger = 0;
		for (int i = numParts-1; i >= 0; i--) {
			WordInfoWindow window = windows[i];
			window.setLocation(new Point(x + stagger, y + stagger));
			Dimension size = window.getSize();
			size.height = maxHeight;
			window.setSize(size);
			window.setVisible(true);
			stagger += STAGGERING_OFFSET;
		}
	}

	/**	The word part. */

	private WordPart wordPart;

	/**	The word. */

	private Word word;

	/**	The tabbed pane. */

	private JTabbedPane tabbedPane;

	/**	The word forms tab. */

	private WordFormsTab forms;

	/**	Creates a new word get info window.
	 *
	 *	@param	wordPart		Word part.
	 *
	 *	@param	title			Window title.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	private WordInfoWindow (final WordPart wordPart, String title,
		AbstractWindow parentWindow)
			throws PersistenceException
	{

		super(title, parentWindow);
		this.wordPart = wordPart;
		word = wordPart.getWord();
		Corpus corpus = word.getWork().getCorpus();
		LemPos lemPos = wordPart.getLemPos();
		Lemma lemma = lemPos == null ? null : lemPos.getLemma();

		FontManager fontManager = new FontManager();
		FontInfo info = fontManager.getFontInfo(FONT_SIZE);
		int minLabelWidth = info.stringWidth("Syntax (used as)  ") + 20;
		int maxValueWidth = Math.round(minLabelWidth * 2.5f);

		Insets insets = new Insets(0, 5, 0, 5);

		WordSummaryTab summary = new WordSummaryTab(wordPart, corpus,
			FONT_SIZE, insets, minLabelWidth, maxValueWidth, this);
		summary.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		Dimension size = summary.getPreferredSize();

		forms = new WordFormsTab(lemma, corpus,
			FONT_SIZE, size, this);
		forms.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		MoreTab more = new MoreTab(word, corpus,
			FONT_SIZE, insets, minLabelWidth, maxValueWidth, size);
		more.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		tabbedPane = new JTabbedPane();
		tabbedPane.add("Summary", summary);
		tabbedPane.add("Word Forms", forms);
		tabbedPane.add("More", more);
		tabbedPane.setForeground(Color.BLACK); // avoid white text w/Java 8 on macOS

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(tabbedPane, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createEmptyBorder(
			0, 0, WordHoardSettings.getGrowSlop(), 0));

		setContentPane(panel);
		pack();

	}

	/**	Handles the "Find" command.
	 *
	 *	@throws	Exception	general error.
	 */

	public void handleFindWordsCmd ()
		throws Exception
	{
		SearchDefaults defaults = null;
		if (tabbedPane.getSelectedIndex() == 1) {
			defaults = forms.getSearchDefaults();
		} else {
			defaults = wordPart;
		}
		new FindWindow(getCorpus(), defaults, this);
	}

	/**	Handles the "Go To" command.
	 *
	 *	@param	str		Ignored.
	 *
	 *	@throws Exception	general error.
	 */

	public void handleGoToWordCmd (String str)
		throws Exception
	{
		super.handleGoToWordCmd(word.getTag());
	}

	/**	Handles the "Send Error Report" command.
	 *
	 *	@throws	Exception	general error.
	 */

	public void handleErrorCmd ()
		throws Exception
	{
		sendErrorReport(word);
	}

	/**	Find a printable component.
	 *
	 *	@return		A printable component.
	 */

	protected Component findPrintableComponent () {
		return tabbedPane.getSelectedComponent();
	}

	/**	Handles "Print Preview" command.
	 */

	public void handlePrintPreviewCmd() {
		doPrintPreview(findPrintableComponent(), getTitle());
	}

	/**	Handles "Print" command.
	 */

	public void handlePrintCmd() {
		doPrint(findPrintableComponent(), getTitle());
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

