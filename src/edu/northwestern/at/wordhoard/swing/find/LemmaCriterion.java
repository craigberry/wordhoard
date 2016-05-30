package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.swing.*;

/**	Lemma criterion component.
 */

class LemmaCriterion extends CriterionComponent {

	/**	The component. */

	private JPanel panel = new JPanel();

	/**	The text field component. */

	private JTextField textField = new JTextField("", 18);

	/**	The complete button. */

	private JButton completeButton = new JButton("Complete");

	/**	The lemma, or null if none. */

	private Lemma lemma;

	/**	Font manager. */

	private FontManager fontManager = new FontManager();

	/**	Corpus, or null if none. */

	private Corpus corpus;

	/**	Character set. */

	private byte charset = -1;

	/**	True to ignore document events. */

	private boolean ignoreDocumentEvents;

	/**	Initializes the component.
	 *
	 *	@param	val		Initial value, or null.
	 *
	 *	@return 		The component.
	 *
	 *	@throws	Exception
	 */

	JComponent init (SearchCriterion val)
		throws Exception
	{
		lemma = (Lemma)val;
		corpus = (Corpus)getOtherRowValue(CorpusCriterion.class);
		setFontFromCorpus();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentY(Component.CENTER_ALIGNMENT);
		textField.setAlignmentY(Component.CENTER_ALIGNMENT);
		Dimension size = textField.getPreferredSize();
		textField.setMaximumSize(size);
		textField.setMinimumSize(size);
		if (val != null) {
			Spelling tag = lemma.getTag();
			byte charset = tag.getCharset();
			String string = tag.getString();
			setFont(charset);
			textField.setText(string);
		}
		textField.addKeyListener(
			new KeyAdapter() {
				public void keyTyped (KeyEvent event) {
					handleKeyTyped(event.getKeyChar());
				}
			}
		);
		textField.getDocument().addDocumentListener(
			new DocumentListener() {
				public void changedUpdate (DocumentEvent event) {
					handleDocumentEvent();
				}
				public void insertUpdate (DocumentEvent event) {
					handleDocumentEvent();
				}
				public void removeUpdate (DocumentEvent event) {
					handleDocumentEvent();
				}
			}
		);
		completeButton.setAlignmentY(Component.CENTER_ALIGNMENT);
		Font font = completeButton.getFont();
		font = new Font(font.getName(), font.getStyle(), font.getSize()-4);
		completeButton.setFont(font);
		completeButton.setEnabled(false);
		completeButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						completeLemma();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		panel.add(textField);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(completeButton);
		return panel;
	}

	/**	Gets the value of the component.
	 *
	 *	@return			Lemma, or null if none.
	 */

	SearchCriterion getValue () {
		if (lemma == null) return null;
		String fieldText = textField.getText().trim();
		String lemmaText = lemma.getTag().getString();
		if (!lemmaText.equals(fieldText)) {
			lemma = null;
			completeButton.setEnabled(fieldText.length() > 0);
		}
		return lemma;
	}

	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */

	String setCriteria (SearchCriteria searchCriteria) {
		getValue();
		if (lemma == null) {
			try {
				completeLemma();
			} catch (Exception e) {
				Err.err(e);
			}
			if (lemma == null) {
				String fieldText = textField.getText().trim();
				if (fieldText.length() == 0)
					return "You did not enter a valid lemma";
				return "";
			}
		}
		searchCriteria.add(lemma);
		return null;
	}

	/**	Handles a key typed event.
	 *
	 *	@param	c		Character typed.
	 */

	private void handleKeyTyped (char c) {
		if (corpus == null) {
			boolean isUsedRoman = CharsUsed.isUsedRoman(c);
			boolean isUsedGreek = CharsUsed.isUsedGreek(c);
			if (isUsedGreek && !isUsedRoman) {
				setFont(TextParams.GREEK);
			} else if (isUsedRoman && !isUsedGreek) {
				setFont(TextParams.ROMAN);
			}
		}
	}

	/**	Handles a document event.
	 */

	private void handleDocumentEvent () {
		if (ignoreDocumentEvents) return;
		String fieldText = textField.getText().trim();
		if (lemma != null) {
			String lemmaText = lemma.getTag().getString();
			if (lemmaText.equals(fieldText)) return;
		}
		completeButton.setEnabled(fieldText.length() > 0);
		Lemma oldLemma = lemma;
		lemma = null;
		notifyWindow(oldLemma, lemma);
	}

	/**	Sets the font of the text field to match the corpus character set.
	 */

	private void setFontFromCorpus () {
		setFont(corpus == null ? TextParams.ROMAN : corpus.getCharset());
	}

	/**	Sets the font of the text field.
	 *
	 *	@param	charset		Character set.
	 */

	private void setFont (byte charset) {
		if (charset == this.charset) return;
		this.charset = charset;
		Font font = fontManager.getFont(charset, 12);
		textField.setFont(font);
	}

	/**	Completes the lemma.
	 *
	 *	@throws	Exception
	 */

	private void completeLemma ()
		throws Exception
	{
		Lemma oldLemma = lemma;
		String lemmaTag = textField.getText().trim();
		int len = lemmaTag.length();
		if (len == 0) {
			lemma = null;
		} else {
			lemmaTag = CharsetUtils.translateTonosToOxia(lemmaTag);
			PersistenceManager pm = WordHoard.getPm();
			Collection lemmaCollection =
				pm.findLemmasByTagPrefix(lemmaTag, corpus);
			int numLemmas = lemmaCollection.size();
			if (numLemmas == 0) {
				lemma = null;
				String errMsg;
				if (corpus == null ) {
					errMsg = "There is no lemma that starts with \"" +
						lemmaTag + "\"";
				} else {
					errMsg = "There is no lemma in the corpus that starts " +
						"with \"" + lemmaTag + "\"";
				}
				new ErrorMessage(errMsg, getWindow());
			} else {
				Lemma firstLemma =
					(Lemma)lemmaCollection.iterator().next();
				if (numLemmas == 1) {
					lemma = firstLemma;
				} else {
					byte charset = firstLemma.getSpelling().getCharset();
					Font font = fontManager.getFont(charset, 10);
					WhichLemmaDialog dlog =
						new WhichLemmaDialog(lemmaCollection, font);
					dlog.show(getWindow());
					lemma = dlog.lemma;
				}
			}
		}
		completeButton.setEnabled(lemma == null && len > 0);
		if (lemma != null) {
			ignoreDocumentEvents = true;
			textField.setText(lemma.getTag().getString());
			ignoreDocumentEvents = false;
		}
		notifyWindow(oldLemma, lemma);
	}

	/**	Handles a value changed event in some other criterion.
	 *
	 *	@param	cls			The class of the criterion that changed.
	 *
	 *	@param	oldVal		Old value. Null if the row has just
	 *						been created.
	 *
	 *	@param	newVal		New value. Null if the row has just been
	 *						deleted.
	 *
	 *	@throws	Exception
	 */

	void handleValueChanged (Class cls, SearchCriterion oldVal,
		SearchCriterion newVal)
			throws Exception
	{
		if (cls.equals(CorpusCriterion.class)) {
			corpus = (Corpus)newVal;
			setFontFromCorpus();
		}
	}

	/**	Which lemma? dialog. */

	private static class WhichLemmaDialog extends ModalDialog {

		private Lemma lemma = null;

		private WhichLemmaDialog (Collection lemmaCollection, Font font) {
			super("Which Lemma?");
			setResizable(false);
			final Lemma[] lemmas = (Lemma[])lemmaCollection.toArray(
				new Lemma[lemmaCollection.size()]);
			final JList list = new JList(lemmas);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setSelectedIndex(0);
			list.setFont(font);
			setInitialFocus(list);
			list.addMouseListener(
				new MouseAdapter() {
					public void mouseClicked (MouseEvent event) {
						if (event.getClickCount() <= 1) return;
						int k = list.getSelectedIndex();
						if (k < 0) return;
						lemma = lemmas[k];
						dispose();
					}
				}
			);
			JScrollPane scrollPane = new JScrollPane(list,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			add(scrollPane);
			addButton("Cancel",
				new ActionListener() {
					public void actionPerformed (ActionEvent event) {
						dispose();
					}
				}
			);
			addDefaultButton("OK",
				new ActionListener() {
					public void actionPerformed (ActionEvent event) {
						int k = list.getSelectedIndex();
						if (k >= 0) lemma = lemmas[k];
						dispose();
					}
				}
			);
		}

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

