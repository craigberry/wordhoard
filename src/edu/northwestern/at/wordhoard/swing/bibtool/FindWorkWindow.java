package edu.northwestern.at.wordhoard.swing.bibtool;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.bibtool.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

/**	A Find window.
 */

public class FindWorkWindow extends AbstractWindow {

	/**	The corpus combo box. */

	private JComboBox corpusComboBox = new JComboBox();

	/**	True if the author check box is selected. */

	private static boolean authorChecked = true;

	/**	True if the spelling check box is selected. */

	private static boolean titleChecked = false;

	/**	True if the case sensitive check box is selected. */

	private static boolean caseChecked = false;

	/**	The author label. */

	private JLabel authorLabel = new JLabel("Author: ");
	private JCheckBox authorCheckBox = new JCheckBox("Author: ");

	/**	The author text field. */

	private JTextField authorTextField = new JTextField("", 20);

	/**	The title check box. */

	private JCheckBox titleCheckBox = new JCheckBox("Title: ");
	private JLabel titleLabel = new JLabel("Title: ");

	/**	The title text field. */

	private JTextField titleTextField = new JTextField("", 20);

	/**	The date text field. */

	private JTextField yearStartTextField = new JTextField("", 8);
	private JTextField yearEndTextField = new JTextField("", 8);
	private JLabel yearLabel = new JLabel("Years: ");
	private JLabel yearToLabel = new JLabel(" to ");

	/**	The case sensitive check box. */

	private JCheckBox caseCheckBox = new JCheckBox("Case sensitive");

	/**	The Cancel button. */

	private JButton cancelButton;

	/**	The Find button. */

	private JButton findButton;

	/**	Creates a new Find window.
	 *
	 *	@param	corpus		The corpus.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public FindWorkWindow (final Corpus corpus,
		final AbstractWindow parentWindow)
			throws PersistenceException

	{
		super("Find Works", parentWindow);

		authorTextField.setEnabled(true);
		caseCheckBox.setSelected(caseChecked);
		titleTextField.setEnabled(true);
		caseCheckBox.setEnabled(false);

		JPanel authorPanel = new JPanel();
		authorPanel.setLayout(new BoxLayout(authorPanel, BoxLayout.X_AXIS));
		authorPanel.add(authorLabel);
		authorPanel.add(authorTextField);
		authorPanel.add(Box.createHorizontalStrut(10));
		authorPanel.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));

		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
		titlePanel.add(titleLabel);
		titlePanel.add(titleTextField);
		titlePanel.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));

		JPanel yearPanel = new JPanel();
		yearPanel.setLayout(new BoxLayout(yearPanel, BoxLayout.X_AXIS));
		yearPanel.add(yearLabel);
		yearPanel.add(yearStartTextField);
		yearPanel.add(yearToLabel);
		yearPanel.add(yearEndTextField);
		yearPanel.setBorder(BorderFactory.createEmptyBorder(20,0,0,0));

		JPanel casePanel = new JPanel();
		casePanel.setLayout(new BoxLayout(casePanel, BoxLayout.X_AXIS));
		casePanel.add(caseCheckBox);

		DialogPanel dlogPanel = new DialogPanel();
		dlogPanel.add(authorPanel);
		dlogPanel.add(titlePanel);
		dlogPanel.add(yearPanel);
		dlogPanel.add(casePanel);

		cancelButton = dlogPanel.addButton("Cancel",
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						dispose();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		findButton = dlogPanel.addButton("Find",
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						find();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		getRootPane().setDefaultButton(findButton);

		setContentPane(dlogPanel);
		setResizable(false);

		addWindowListener(
			new WindowAdapter() {
				public void windowActivated (WindowEvent event) {
					authorTextField.selectAll();
					authorTextField.requestFocus();
					removeWindowListener(this);
				}
			}
		);

		pack();
		WindowPositioning.centerWindowOverWindow(this, parentWindow, 25);
		setVisible(true);
	}

	/**	Author text field input verifier. */

	private class AuthorInputVerifier extends InputVerifier {
		public boolean verify (JComponent input) {
/*			try {
				rebuildPosMenuToMatchAuthor();
			} catch (PersistenceException e) {
				Err.err(e);
			} */
			return true;
		}
	}


	/**	Executes the search.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	private void find ()
		throws PersistenceException
	{
		caseChecked = caseCheckBox.isSelected();
		Corpus corpus = (Corpus)corpusComboBox.getSelectedItem();
		String title = titleTextField.getText().trim();
		String authorname = authorTextField.getText().trim();
		String yearStart = yearStartTextField.getText().trim();
		String yearEnd = yearEndTextField.getText().trim();
		SearchWorkCriteria swq = new SearchWorkCriteria(corpus, authorname, title, yearStart, yearEnd, caseChecked);
		new SearchWorkResultsWindow(swq, getParentWindow());
		dispose();
	}

/*	private void findOLD ()
		throws PersistenceException
	{
		authorChecked = authorCheckBox.isSelected();
		titleChecked = titleCheckBox.isSelected();
		caseChecked = caseCheckBox.isSelected();

		String title = null;
		if (titleChecked) {
			title = titleTextField.getText().trim();
			if (title.length() == 0) {
				new ErrorMessage(
					"The title option is checked, but you did not " +
					"type a word.", this);
			}
		}

		String authorname = null;
		if (authorChecked) {
			authorname = authorTextField.getText().trim();
			if (authorname.length() == 0) {
				new ErrorMessage(
					"The author option is checked, but you did not " +
					"type a word.", this);
			}
		}

		Corpus corpus = (Corpus)corpusComboBox.getSelectedItem();

		SearchWorkCriteria swq = new SearchWorkCriteria(corpus, authorname, title, caseChecked);

		new SearchWorkResultsWindow(swq, getParentWindow());

		dispose();
	}
	*/

	/**	Which author? dialog. */

	private static class WhichAuthorDialog extends ModalDialog {

		private Author author = null;

		private WhichAuthorDialog (Collection authorCollection) {
			super("Which Author?");
			setResizable(false);
			final Author[] authors = (Author[])authorCollection.toArray(
				new Author[authorCollection.size()]);
			final JList list = new JList(authors);
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setSelectedIndex(0);
			setInitialFocus(list);
			list.addMouseListener(
				new MouseAdapter() {
					public void mouseClicked (MouseEvent event) {
						if (event.getClickCount() <= 1) return;
						int k = list.getSelectedIndex();
						if (k < 0) return;
						author = authors[k];
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
			addDefaultButton("Find",
				new ActionListener() {
					public void actionPerformed (ActionEvent event) {
						int k = list.getSelectedIndex();
						if (k >= 0) author = authors[k];
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

