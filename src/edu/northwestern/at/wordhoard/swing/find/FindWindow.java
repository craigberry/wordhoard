package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.swing.concordance.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.swing.*;

/**	A find window.
 */

public class FindWindow extends AbstractWindow {

	/**	Font size. */

	private static final int FONT_SIZE = 12;

	/**	SearchDefaults object, or null. */

	private SearchDefaults defaults;

	/**	Criteria panel. */

	private JPanel criteriaPanel = new JPanel();

	/**	Creates a new find window.
	 *
	 *	@param	corpus			The corpus or null.
	 *
	 *	@param	defaults		SearchDefaults object, or null.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	PersistenceException
	 */

	public FindWindow (Corpus corpus, SearchDefaults defaults,
		AbstractWindow parentWindow)
			throws PersistenceException
	{
		super("Find Words", parentWindow);
		setCorpus(corpus);
		this.defaults = defaults;

		FontManager fontManager = new FontManager();
		Font romanFont = fontManager.getFont(FONT_SIZE);

		StringBuffer buf = new StringBuffer();
		buf.append("Search for words which ");
		buf.append("satisfy all of the following criteria. ");
		buf.append("Use the plus and minus buttons to add and ");
		buf.append("remove criteria.");
		WrappedTextComponent verbiage = new WrappedTextComponent(buf.toString(),
			romanFont, 500);

		criteriaPanel.setLayout(new BoxLayout(criteriaPanel, BoxLayout.Y_AXIS));
		criteriaPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));

		Row row = addRow(null);
		row = addRow(null);

		JScrollPane scrollPane = new JScrollPane(criteriaPanel,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		Dimension size = scrollPane.getPreferredSize();
		size.height = 300;
		size.width = 640;
		scrollPane.setPreferredSize(size);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);

		DialogPanel dlogPanel = new DialogPanel();
		dlogPanel.add(3);
		dlogPanel.add(verbiage);
		dlogPanel.add(18);
		dlogPanel.add(scrollPane);

		JButton cancelButton = dlogPanel.addButton("Cancel",
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

		JButton findButton = dlogPanel.addButton("Find",
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

		pack();
		setLocation(new Point(3, WordHoardSettings.getTopSlop()));
		positionNextTo(parentWindow);
		setVisible(true);

	}

	public FindWindow (AbstractWindow parentWindow) throws PersistenceException {
				super("Find Words", parentWindow);
	}

	public FindWindow (String title, AbstractWindow parentWindow) throws PersistenceException {
				super(title, parentWindow);
	}

	/**	Executes the search.
	 *
	 *	@throws	Exception
	 */

	private void find ()
		throws Exception
	{
		Row[] rows = getRows();
		SearchCriteria sq = new SearchCriteria();
		for (int i = 0; i < rows.length; i++) {
			Row row = rows[i];
			CriterionComponent criterionComponent = row.getCriterionComponent();
		//	System.out.println(getClass().getName() + " row :" + criterionComponent.getClass().getName());
			String errMsg = criterionComponent.setCriteria(sq);
			if (errMsg != null) {
				if (errMsg.length() > 0)
					new ErrorMessage(errMsg, this);
				return;
			}
		}
		if (sq.suspicious()) {
			WarningDialog dlog = new WarningDialog();
			dlog.show(this);
			if (dlog.canceled) return;
		}
		dispose();
		new ConcordanceWindow(sq, getParentWindow());
	}

	/**	The warning dialog. */

	private static class WarningDialog extends ModalDialog {
		private boolean canceled = true;
		private WarningDialog() {
			super(null);
			setResizable(false);
			JLabel warningIcon =
				new JLabel(UIManager.getLookAndFeel().getDefaults().getIcon(
					"OptionPane.warningIcon"));
			warningIcon.setAlignmentY(Component.TOP_ALIGNMENT);
			StringBuffer buf = new StringBuffer();
			buf.append("This search is likely to take a long time ");
			buf.append("and produce a large number of results. ");
			buf.append("Are you certain you want to continue?");
			FontManager fontManager = new FontManager();
			Font font = fontManager.getFont(12);
			WrappedTextComponent textPanel1 =
				new WrappedTextComponent(buf.toString(), font, 250);
			textPanel1.setAlignmentX(Component.LEFT_ALIGNMENT);
			buf = new StringBuffer();
			buf.append("(To avoid this warning, add ");
			buf.append("a lemma criterion and/or a spelling criterion.)");
			font = fontManager.getFont(10);
			WrappedTextComponent textPanel2 =
				new WrappedTextComponent(buf.toString(), font, 250);
			textPanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
			JPanel textBox = new JPanel();
			textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));
			textBox.add(textPanel1);
			textBox.add(Box.createVerticalStrut(10));
			textBox.add(textPanel2);
			textBox.setAlignmentY(Component.TOP_ALIGNMENT);
			JPanel box = new JPanel();
			box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
			box.add(warningIcon);
			box.add(Box.createHorizontalStrut(20));
			box.add(textBox);
			add(box);
			addDefaultButton("Cancel",
				new ActionListener() {
					public void actionPerformed (ActionEvent event) {
						canceled = true;
						dispose();
					}
				}
			);
			addButton("Continue",
				new ActionListener() {
					public void actionPerformed (ActionEvent event) {
						canceled = false;
						dispose();
					}
				}
			);
		}
	}

	/**	Gets the rows.
	 *
	 *	@return			Array of all rows.
	 */

	Row[] getRows () {
		Component[] components = criteriaPanel.getComponents();
		if (components == null || components.length == 0) {
			return new Row[0];
		} else {
			Row[] result = new Row[components.length];
			for (int i = 0; i < components.length; i++) {
				result[i] = (Row)components[i];
			}
			return result;
		}
	}

	/**	Finds a row component.
	 *
	 *	@param	row		Row component.
	 *
	 *	@return			Index of row in criteria panel,
	 *					or -1 if not  found.
	 */

	private int findRow (Row row) {
		Row[] rows = getRows();
		for (int i = 0; i < rows.length; i++) {
			if (row == rows[i]) return i;
		}
		return -1;
	}

	/**	Adds a new row.
	 *
	 *	@param	row		Row after which to add new row, or null
	 *					to add the new row at the end.
	 *
	 *	@return			The new row.
	 */

	public Row addRow (Row row) {
		int index = row == null ? -1 : findRow(row) + 1;
		final Row newRow = new Row(this, getRows());
		criteriaPanel.add(newRow, index);
		CriterionComponent criterionComponent = newRow.getCriterionComponent();
		Class cls = criterionComponent.getClass();
		SearchCriterion newVal = criterionComponent.getValue();
		handleValueChanged(cls, null, newVal);
		adjustCriteriaPanel();
		return newRow;
	}

	/**	Removes a row.
	 *
	 *	@param	row		Row to be removed.
	 */

	void removeRow (Row row) {
		criteriaPanel.remove(row);
		CriterionComponent criterionComponent = row.getCriterionComponent();
		Class cls = criterionComponent.getClass();
		SearchCriterion oldVal = criterionComponent.getValue();
		handleValueChanged(cls, oldVal, null);
		adjustCriteriaPanel();
	}

	/**	Handles a value changed event.
	 *
	 *	@param	cls			The class of the criterion.
	 *
	 *	@param	oldVal		The old value.
	 *
	 *	@param	newVal		The new value.
	 *
	 *	@throws	Exception
	 */

	void handleValueChanged (Class cls, SearchCriterion oldVal,
		SearchCriterion newVal)
	{
		if (Compare.equals(oldVal, newVal)) return;
		if (cls.equals(CorpusCriterion.class)) setCorpus((Corpus)newVal);
		Row[] rows = getRows();
		for (int i = 0; i < rows.length; i++) {
			Row row = rows[i];
			row.handleValueChanged(cls, oldVal, newVal);
		}
	}

	/**	Adjusts the criteria panel.
	 *
	 *	<p>The panel is revalidated and repainted.
	 *
	 *	<p>The minus button of the first row is enabled iff there is more
	 *	than one row in the panel.
	 *
	 *	<p>The plus buttons in each row are enabled iff there are fewer than
	 *	Row.NUM_CRITERIA rows in the panel.
	 *
	 *	<p>All the row combo boxes are rebuilt.
	 */

	private void adjustCriteriaPanel () {
		criteriaPanel.revalidate();
		criteriaPanel.repaint();
		Row[] rows = getRows();
		int numRows = rows.length;
		Row firstRow = rows[0];
		firstRow.setMinusEnabled(numRows > 1);
		for (int i = 0; i < numRows; i++) {
			Row row = rows[i];
			row.setPlusEnabled(numRows < Row.NUM_CRITERIA);
		}
		rebuildRowComboBoxes();
	}

	/**	Rebuilds all the row combo boxes.
	 */

	void rebuildRowComboBoxes () {
		Row[] rows = getRows();
		for (int i = 0; i < rows.length; i++) {
			Row row = rows[i];
			row.rebuildComboBox(rows);
		}
	}

	/**	Gets the value of a criterion.
	 *
	 *	@param	cls		Criterion class.
	 *
	 *	@return 		Value, or null if none.
	 */

	SearchCriterion getValue (Class cls) {
		Row[] rows = getRows();
		for (int i = 0; i < rows.length; i++) {
			Row row = rows[i];
			CriterionComponent criterionComponent = row.getCriterionComponent();
			if (criterionComponent.getClass().equals(cls))
				return criterionComponent.getValue();
		}
		return null;
	}

	/**	Gets the defaults.
	 *
	 *	@return		SearchDefaults object, or null.
	 */

	SearchDefaults getDefaults () {
		return defaults;
	}

	/**	Handles the "Find" command.
	 *
	 *	@throws	Exception
	 */

	public void handleFindWordsCmd ()
		throws Exception
	{
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

