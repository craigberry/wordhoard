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
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

/**	Speaker criterion component.
 */

class SpeakerCriterion extends CriterionComponent {

	/**	The component. */

	private JPanel panel = new JPanel();

	/**	The combo box component. */

	private JComboBox comboBox = new JComboBox();

	/**	True to ignore combo box action events. */

	private boolean ignoreComboBoxActionEvents;

	/**	The text field component. */

	private JTextField textField = new JTextField("", 18);

	/**	The complete button. */

	private JButton completeButton = new JButton("Complete");

	/** The speaker or the speaker name, or null. */

	private SearchCriterion speaker;

	/**	The work, or null if none. */

	private Work work;

	/**	The work part, or null if none. */

	private WorkPart workPart;

	/**	The corpus, or null if none. */

	private Corpus corpus;

	/**	The narrative attribute, or null if none. */

	private Narrative narrative;

	/**	True to ignore document events. */

	private boolean ignoreDocumentEvents;

	/**	Font manager. */

	private FontManager fontManager;

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
		speaker = val;
		corpus = (Corpus)getOtherRowValue(CorpusCriterion.class);
		work = (Work)getOtherRowValue(WorkCriterion.class);
		workPart = (WorkPart)getOtherRowValue(WorkPartCriterion.class);
		narrative = (Narrative)getOtherRowValue(NarrativeCriterion.class);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.setAlignmentY(Component.CENTER_ALIGNMENT);
		comboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		comboBox.setMaximumRowCount(20);
		textField.setAlignmentY(Component.CENTER_ALIGNMENT);
		fontManager = new FontManager();
		Font font = fontManager.getFont(12);
		textField.setFont(font);
		Dimension size = textField.getPreferredSize();
		textField.setMaximumSize(size);
		textField.setMinimumSize(size);
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
		font = completeButton.getFont();
		font = new Font(font.getName(), font.getStyle(), font.getSize()-4);
		completeButton.setFont(font);
		completeButton.setEnabled(false);
		completeButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						completeSpeakerName();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		rebuild();
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					if (ignoreComboBoxActionEvents) return;
					SearchCriterion oldSpeaker = speaker;
					speaker = (Speaker)comboBox.getSelectedItem();
					notifyWindow(oldSpeaker, speaker);
				}
			}
		);
		return panel;
	}

	/**	Gets the value of the component.
	 *
	 *	@return			The speaker or the speaker name.
	 */

	SearchCriterion getValue () {
		return speaker;
	}

	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */

	String setCriteria (SearchCriteria searchCriteria) {
		if (work == null) {
			if (speaker == null) {
				try {
					completeSpeakerName();
				} catch (Exception e) {
					Err.err(e);
				}
			}
		}
		searchCriteria.add(speaker);
		return null;
	}

	/**	Rebuilds the component.
	 *
	 *	@throws	PersistenceException
	 */

	private void rebuild ()
		throws PersistenceException
	{
		ignoreComboBoxActionEvents = true;
		panel.removeAll();
		comboBox.removeAllItems();
		String badTagString = checkTaggingData(corpus, work, workPart,
			TaggingData.SPEAKER);
		if (badTagString != null) {
			speaker = null;
			panel.add(new JLabel("(" + badTagString +
				" does not have speaker data)"));
		} else if (narrative != null && !narrative.isSpeech()) {
			speaker = null;
			panel.add(new JLabel(
				"(There are no speakers in narration)"));
		} else {
			if (work == null) {
				panel.add(textField);
				panel.add(Box.createVerticalStrut(3));
				panel.add(Box.createHorizontalStrut(5));
				panel.add(completeButton);
				if (speaker != null) {
					if (speaker instanceof SpeakerName) {
						SpeakerName spkName = (SpeakerName)speaker;
						textField.setText(spkName.getName());
					} else {
						Speaker spk = (Speaker)speaker;
						textField.setText(spk.getName());
					}
				}
			} else {
				Collection speakCollection = work.getSpeakers();
				Speaker[] speakers = (Speaker[])speakCollection.toArray(
					new Speaker[speakCollection.size()]);
				Arrays.sort(speakers,
					new Comparator () {
						public int compare (Object o1, Object o2) {
							Speaker s1 = (Speaker)o1;
							Speaker s2 = (Speaker)o2;
							String name1 = s1.getName();
							String name2 = s2.getName();
							return Compare.compareIgnoreCase(name1, name2);
						}
					}
				);
				int numSpeakers = speakers.length;
				if (numSpeakers == 0) {
					speaker = null;
					panel.add(new JLabel("(Work has no speakers)"));
				} else if (numSpeakers == 1) {
					speaker = speakers[0];
					panel.add(new JLabel(speakers[0].getName()));
				} else {
					for (int i = 0; i < numSpeakers; i++) {
						Speaker spk = speakers[i];
						comboBox.addItem(spk);
						if (speaker == null) {
							if (Compare.equalsIgnoreCase(spk.getName(),
								textField.getText().trim()))
									comboBox.setSelectedItem(spk);
						} else {
							if (speaker instanceof Speaker) {
								if (spk.equals(speaker))
									comboBox.setSelectedItem(spk);
							} else {
								SpeakerName spkName = (SpeakerName)speaker;
								if (Compare.equalsIgnoreCase(spk.getName(),
									spkName.getName()))
										comboBox.setSelectedItem(spk);
							}
						}
					}
					speaker = (SearchCriterion)comboBox.getSelectedItem();
					panel.add(comboBox);
				}
			}
		}
		ignoreComboBoxActionEvents = false;
	}

	/**	Handles a document event.
	 */

	private void handleDocumentEvent () {
		if (ignoreDocumentEvents) return;
		String fieldText = textField.getText().trim();
		if (speaker != null && speaker instanceof SpeakerName) {
			SpeakerName spkName = (SpeakerName)speaker;
			String speakerText = spkName.getName();
			if (speakerText.equals(fieldText)) return;
		}
		completeButton.setEnabled(fieldText.length() > 0);
		SearchCriterion oldSpeaker = speaker;
		speaker = null;
		notifyWindow(oldSpeaker, speaker);
	}

	/**	Completes the speaker name.
	 *
	 *	@throws	Exception
	 */

	private void completeSpeakerName ()
		throws Exception
	{
		SearchCriterion oldSpeaker = speaker;
		String prefix = textField.getText().trim();
		int len = prefix.length();
		if (len == 0) {
			speaker = null;
		} else {
			PersistenceManager pm = WordHoard.getPm();
			Collection nameCollection =
				pm.findSpeakerNamesbyPrefix(prefix, corpus);
			int numNames = nameCollection.size();
			if (numNames == 0) {
				speaker = null;
				new ErrorMessage(
					"There is no speaker name that starts with \"" +
						prefix + "\"",
					getWindow());
			} else {
				String firstName =
					(String)nameCollection.iterator().next();
				if (numNames == 1) {
					speaker = new SpeakerName(firstName);
				} else {
					Font font = fontManager.getFont(10);
					WhichSpeakerDialog dlog =
						new WhichSpeakerDialog(nameCollection, font);
					dlog.show(getWindow());
					speaker = new SpeakerName(dlog.name);
				}
			}
		}
		completeButton.setEnabled(speaker == null && len > 0);
		if (speaker != null) {
			ignoreDocumentEvents = true;
			SpeakerName spkName = (SpeakerName)speaker;
			textField.setText(spkName.getName());
			ignoreDocumentEvents = false;
		}
		notifyWindow(oldSpeaker, speaker);
	}

	/**	Handles a value changed event in some other criterion.
	 *
	 *	@param	cls			The class of the criterion that changed.
	 *
	 *	@param	oldVal		Old value.
	 *
	 *	@param	newVal		New value.
	 *
	 *	@throws	Exception
	 */

	void handleValueChanged (Class cls, SearchCriterion oldVal,
		SearchCriterion newVal)
			throws Exception
	{
		SearchCriterion oldSpeaker = speaker;
		if (cls.equals(CorpusCriterion.class)) {
			corpus = (Corpus)newVal;
			rebuild();
		} else if (cls.equals(WorkCriterion.class)) {
			work = (Work)newVal;
			rebuild();
		} else if (cls.equals(WorkPartCriterion.class)) {
			workPart = (WorkPart)newVal;
			rebuild();
		} else if (cls.equals(NarrativeCriterion.class)) {
			narrative = (Narrative)newVal;
			rebuild();
		}
		notifyWindow(oldSpeaker, speaker);
	}

	/**	Which speaker? dialog. */

	private static class WhichSpeakerDialog extends ModalDialog {

		private String name = null;

		private WhichSpeakerDialog (Collection nameCollection, Font font) {
			super("Which Speaker?");
			setResizable(false);
			final String[] names = (String[])nameCollection.toArray(
				new String[nameCollection.size()]);
			final JList list = new JList(names);
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
						name = names[k];
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
						if (k >= 0) name = names[k];
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

