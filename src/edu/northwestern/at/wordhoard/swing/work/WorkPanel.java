package edu.northwestern.at.wordhoard.swing.work;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.swing.annotations.*;
import edu.northwestern.at.wordhoard.swing.calculator.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.model.annotations.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

/**	A word-aware work display and navigation panel.
 */

public class WorkPanel extends JPanel {

	/**	Persistence manager. */

	private PersistenceManager pm;

	/**	Parent window. */

	private AbstractWorkPanelWindow parentWindow;

	/**	The work being displayed. */

	private Work work;

	/**	The work parts. */

	private WorkPart parts[];

	/**	The number of parts. */

	private int numParts;

	/**	The part currently being displayed. */

	private int curPart = 0;

	/**	Work title label. */

	private JLabel titleLabel;

	/**	The part selection combo box. */

	private JComboBox combo;

	/**	True to ignore combo box action events. */

	private boolean ignoreComboActionEvents = false;

	/**	The left button control. */

	private JButton leftButton;

	/**	The right button control. */

	private JButton rightButton;

	/**	The work part text component. */

	private PartTextComponent textComponent;

	/**	The scroll pane. */

	private JScrollPane scrollPane;

	/**	The footer label. */

	private JLabel footerLabel = new JLabel(" ");

	/**	Page load start time. */

	private long startTime;

	/**	Font manager. */

	private FontManager fontManager;
	
	/**	Set of works for which parts have been preloaded. */
	
	private static Set preloadedWorks = new HashSet();

	/**	Creates a new work panel.
	 *
	 *	@param	pm				Persistence manager.
	 *
	 *	@param	parentWindow	Parent window.
	 */

	public WorkPanel (PersistenceManager pm,
		final AbstractWorkPanelWindow parentWindow)
	{

		super();

		this.pm = pm;
		this.parentWindow = parentWindow;

		fontManager = new FontManager();

		titleLabel = new JLabel();
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		combo = new JComboBox();
		combo.setEnabled(false);
		combo.setMaximumRowCount(30);
		combo.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						if (ignoreComboActionEvents) return;
						int newPart = combo.getSelectedIndex();
						if (newPart == curPart) return;
						curPart = newPart;
						if (curPart >= 0) {
							textComponent.setPart(parts[curPart], null);
						}
						leftButton.setEnabled(curPart > 0);
						rightButton.setEnabled(curPart+1 < numParts);
						textComponent.requestFocus();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		ImageIcon leftButtonIcon = Images.get("left.gif");
		leftButton = new JButton(leftButtonIcon);
		leftButton.setEnabled(false);
		leftButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						if (curPart > 0) combo.setSelectedIndex(curPart-1);
						textComponent.requestFocus();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		leftButton.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));

		ImageIcon rightButtonIcon = Images.get("right.gif");
		rightButton = new JButton(rightButtonIcon);
		rightButton.setEnabled(false);
		rightButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						if (curPart+1 < numParts)
							combo.setSelectedIndex(curPart+1);
						textComponent.requestFocus();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		rightButton.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));

		JPanel controls = new JPanel();
		controls.setLayout(new BoxLayout(controls, BoxLayout.X_AXIS));
		controls.add(leftButton);
		controls.add(Box.createHorizontalGlue());
		controls.add(combo);
		controls.add(Box.createHorizontalGlue());
		controls.add(rightButton);

		textComponent = new PartTextComponent(parentWindow);
		textComponent.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));

		textComponent.addKeyListener(
			new KeyAdapter() {
				public void keyPressed (KeyEvent event) {
					try {
						int code = event.getKeyCode();
						if (code == KeyEvent.VK_SPACE) {
							scrollDown();
						} else if (code == KeyEvent.VK_ENTER) {
							parentWindow.handleGetInfoCmd();
						}
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		textComponent.addMouseListener(
			new MouseAdapter() {
				public void mousePressed (MouseEvent event) {
					textComponent.requestFocus();
				}
			}
		);

		addMouseListener(
			new MouseAdapter() {
				public void mousePressed (MouseEvent event) {
					textComponent.requestFocus();
				}
			}
		);

		textComponent.addSelectionObserver(
			new Observer () {
				public void update (Observable o, Object arg) {
					showFooterInfo();
				}
			}
		);

		scrollPane = new JScrollPane(
			textComponent,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(500, 600));
		scrollPane.getViewport().setBackground(Color.white);

		int growSlop = WordHoardSettings.getGrowSlop();
		JPanel footerPanel = new JPanel();
		footerPanel.setLayout(
			new BoxLayout(footerPanel, BoxLayout.X_AXIS));
		footerPanel.add(Box.createHorizontalStrut(5));
		footerPanel.add(footerLabel);
		footerPanel.add(Box.createHorizontalGlue());
		if (growSlop > 0)
			footerPanel.add(Box.createHorizontalStrut(growSlop));
		int topMargin = Env.MACOSX ? 1 : 2;
		int botMargin = Env.MACOSX ? 2 : 0;
		footerPanel.setBorder(
			BorderFactory.createEmptyBorder(topMargin,0,botMargin,0));
		Dimension size = footerPanel.getPreferredSize();
		size.width = 10000;
		footerPanel.setMaximumSize(size);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(titleLabel);
		add(Box.createVerticalStrut(10));
		add(controls);
		add(Box.createVerticalStrut(10));
		add(scrollPane);
		add(footerPanel);

	}

	/**	reset
	 *
	 *	@param	range				The text range to scroll to in base
	 *								coordinates, or null if none.
	 */

	public void resetPart (final TextRange range) {
		textComponent.resetPart(range);
	}

	/**	reset
	 *
	 */

	public void reset () {
		textComponent.resetPart(null);
	}

	/**	Preloads the parts of a work.
	 *
	 *	@param	pm			Persistence manager.
	 *
	 *	@param	work		Work.
	 */
	 
	private static void preloadParts (PersistenceManager pm, Work work) {
		if (preloadedWorks.contains(work)) return;
		try {
			pm.getWorkParts(work);
			preloadedWorks.add(work);
		} catch (Exception e) {
		}
	}

	/**	Sets the work part.
	 *
	 *	@param	part	The work part.
	 *
	 *	@param	word	The word in the part to scroll to, or
	 *					null if none.
	 */

	private void setPart (WorkPart part, Word word) {
		Work newWork = part.getWork();

		if (!newWork.equals(work)) {
			work = newWork;
			preloadParts(pm, work);
			titleLabel.setText(work.getFullTitle());
			java.util.List partsList = work.getPartsWithText();
			numParts = partsList.size();
			parts = (WorkPart[])partsList.toArray(new WorkPart[numParts]);
			ignoreComboActionEvents = true;
			combo.removeAllItems();
			for (int i = 0; i < numParts; i++) combo.addItem(parts[i]);
			combo.setMaximumSize(combo.getPreferredSize());
			ignoreComboActionEvents = false;
			combo.setEnabled(true);
			leftButton.setEnabled(false);
			rightButton.setEnabled(numParts > 1);
		}

		part = part.getFirstDescendantWithText();
		curPart = 0;
		if (part != null) {
			for (int i = 0; i < numParts; i++) {
				if (part.equals(parts[i])) {
					curPart = i;
					ignoreComboActionEvents = true;
					combo.setSelectedIndex(i);
					ignoreComboActionEvents = false;
					leftButton.setEnabled(curPart > 0);
					rightButton.setEnabled(curPart+1 < numParts);
					break;
				}
			}
		}

		WorkPart workPart = parts[curPart];
		textComponent.setPart(workPart, word);
		Corpus corpus = workPart.getWork().getCorpus();
		parentWindow.enableTranslationsCmd(corpus.hasTranslations());

	}

	/**	Sets the work part.
	 *
	 *	@param	part	The work part.
	 */

	public void setPart (WorkPart part) {
		setPart(part, null);
	}

	/**	Goes to a word.
	 *
	 *	<p>Loads a new part if necessary, then scrolls to and selects
	 *	the word.
	 *
	 *	@param	word		The word.
	 */

	public void goTo (Word word) {
		WorkPart part = word.getWorkPart();
		if (part == null) return;
		setPart(part, word);
	}

	/**	Gets the work part.
	 *
	 *	@return		The work part, or null if none.
	 */

	public WorkPart getWorkPart () {
		if (curPart < 0 || parts == null) return null;
		return parts[curPart];
	}

	/**	Gets the work.
	 *
	 *	@return		The work, or null if none.
	 */

	public Work getWork () {
		return work;
	}

	/**	Scrolls down one block, advancing to the next page if at the end
	 *	of the current page.
	 */

	private void scrollDown () {
		JViewport viewport = scrollPane.getViewport();
		Rectangle r = viewport.getViewRect();
		Dimension d = viewport.getViewSize();
		if (r.y >= d.height - r.height) {
			rightButton.doClick();
		} else {
			int y = r.y + textComponent.getScrollableBlockIncrement(r,
				SwingConstants.VERTICAL, 1);
			viewport.setViewPosition(new Point(0, y));
		}
	}

	/**	Gets the word for the selected text.
	 *
	 *	@return		The word for the currently selected text,
	 *				or null if none.
	 */

	public Word getWord () {
		if (parts == null) return null;
		TextRange selection = textComponent.getSelection();
		if (selection == null) return null;
		TextLocation selStart = selection.getStart();
		TextLocation selEnd = selection.getEnd();
		int lineIndexStart = selStart.getIndex();
		int startOffset = selStart.getOffset();
		int lineIndexEnd = selEnd.getIndex();
		int endOffset = selEnd.getOffset();
		if (lineIndexStart != lineIndexEnd) return null;
		if (startOffset >= endOffset) return null;
		WorkPart part = parts[curPart];
		try {
			return pm.findWord(part, lineIndexStart, startOffset, endOffset);
		} catch (PersistenceException e) {
			Err.err(e);
			return null;
		}
	}

	/**	Gives keyboard focus to the text component.
	 */

	public void requestFocus () {
		textComponent.requestFocus();
	}

	/**	Adds a key listener to the text component.
	 *
	 *	@param	listener	Key listener.
	 */

	public void addKeyListener (KeyListener listener) {
		textComponent.addKeyListener(listener);
	}

	/**	Sets the line number interval.
	 *
	 *	<p>The line number interval controls the display of line numbers.
	 *	0 = no line numbers, 1 = every line numbered, n = every n'th line
	 *	numbered, -1 = stanza numbers.
	 *
	 *	@param	lineNumberInterval		The new line number interval.
	 */

	public void setLineNumberInterval (int lineNumberInterval) {
		textComponent.setLineNumberInterval(lineNumberInterval);
	}

	/**	Changes the translations.
	 *
	 *	@throws	Exception
	 */

	public void changeTranslations ()
		throws Exception
	{
		textComponent.changeTranslations();
	}

	/**	Shows the footer info for the currently selected word.
	 */

	private void showFooterInfo () {
		footerLabel.setText(" ");
		Word word = getWord();
		if (word == null) return;
		Spelling info = word.getBriefDescription();
		if (info == null) return;
		Font font = fontManager.getFont(info.getCharset(), 10);
		footerLabel.setFont(font);
		footerLabel.setText(info.getString());
	}

	/**	Handles the "Copy" command.
	 *
	 *	@throws	Exception
	 */

	public void handleCopyCmd ()
		throws Exception
	{
		textComponent.copy();
	}

	/**	Handles the "Select All" command.
	 *
	 *	@throws	Exception
	 */

	public void handleSelectAllCmd ()
		throws Exception
	{
		textComponent.selectAll();
	}

	/**	Gets the selection.
	 *
	 *	@return		The selection.
	 */

	public TextRange getSelection () {
		return textComponent.getSelection();
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

