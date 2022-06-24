package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.swing.*;

/**	One row of the search criteria panel.
 */

class Row extends JPanel {

	/**	A criterion. */

	private static class Criterion {

		/**	Nname for combo box. */

		private String name;

		/**	Component class. */

		private Class cls;

		/**	Model class. */

		private Class modelCls;

		/**	Creates a new criterion.
		 *
		 *	@param	name		Name for combo box.
		 *
		 *	@param	cls			Component class.
		 *
		 *	@param	modelCls	Model class.
		 */

		private Criterion (String name, Class cls, Class modelCls) {
			this.name = name;
			this.cls = cls;
			this.modelCls = modelCls;
		}

		/**	Returns a string representation of the criterion.
		 *
		 *	@return		The name for the combo box.
		 */

		 public String toString () {
		 	return name;
		 }

	}

	/**	Array of criteria. */

	private static final Criterion[] CRITERIA =
		new Criterion[] {
			new Criterion("Corpus", CorpusCriterion.class,
				Corpus.class),
			new Criterion("Lemma", LemmaCriterion.class,
				Lemma.class),
			new Criterion("Part of speech", PosCriterion.class,
				Pos.class),
			new Criterion("Spelling", SpellingCriterion.class,
				Spelling.class),
			new Criterion("Major word class", MajorWordClassCriterion.class,
				MajorWordClass.class),
			new Criterion("Word class", WordClassCriterion.class,
				WordClass.class),
			new Criterion("Work", WorkCriterion.class,
				Work.class),
			new Criterion("Work part", WorkPartCriterion.class,
				WorkPart.class),
			new Criterion("Author", AuthorCriterion.class,
				Author.class),
			new Criterion("Publication year", PubYearCriterion.class,
				PubYearRange.class),
			new Criterion("Narration or speech", NarrativeCriterion.class,
				Narrative.class),
			new Criterion("Speaker", SpeakerCriterion.class,
				Speaker.class),
			new Criterion("Speaker gender", GenderCriterion.class,
				Gender.class),
			new Criterion("Speaker mortality", MortalityCriterion.class,
				Mortality.class),
			new Criterion("Prose or verse", ProsodicCriterion.class,
				Prosodic.class),
			new Criterion("Metrical shape", MetricalShapeCriterion.class,
				MetricalShape.class),
//			new Criterion("Phrase set", PhraseSetCriterion.class,
//				PhraseSet.class),
			new Criterion("Word set", WordSetCriterion.class,
				WordSet.class),
			new Criterion("Work set", WorkSetCriterion.class,
				WorkSet.class),
			new Criterion("Document freq", DocFreqCriterion.class,
				DocFrequency.class),
			new Criterion("Collection freq", CollFreqCriterion.class,
				CollectionFrequency.class),
		};

	/**	The number of criteria. */

	static final int NUM_CRITERIA = CRITERIA.length;

	/**	Criteria combo box width. */

	private static final int COMBO_BOX_WIDTH;

	static {
		JComboBox comboBox = new JComboBox();
		for (int i = 0; i < NUM_CRITERIA; i++)
			comboBox.addItem(CRITERIA[i]);
		comboBox.setSelectedItem(CRITERIA[0]);
		COMBO_BOX_WIDTH = comboBox.getPreferredSize().width;
	}

	/**	Plus button. */

	private PlusOrMinusButton plusButton = new PlusOrMinusButton(true);

	/**	Minus button. */

	private PlusOrMinusButton minusButton = new PlusOrMinusButton(false);

	/**	True to ignore combo box action events. */

	private boolean ignoreComboBoxEvents;

	/**	Parent find window. */

	private FindWindow window;

	/**	Currently selected criterion index, or -1 if not yet assigned. */

	private int index = -1;

	/**	Current criterion component, or null if none. */

	private CriterionComponent criterionComponent = null;

	/**	Combo box. */

	private JComboBox comboBox;

	/**	Box containing the plus and minus buttons and the combo box. */

	private JPanel box;

	/**	Combo box cell renderer. */

	private ListCellRenderer comboBoxCellRenderer =
		new DefaultListCellRenderer() {
			public Component getListCellRendererComponent (JList list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index,
					isSelected, cellHasFocus);
				setBorder(BorderFactory.createEmptyBorder(0,0,0,5));
				setHorizontalAlignment(SwingConstants.TRAILING);
				return this;
			}
		};

	/**	Creates a new search criteria row component.
	 *
	 *	@param	window		Parent find window.
	 *
	 *	@param	rows		Array of other rows.
	 */

	Row (final FindWindow window, Row[] rows) {
		this.window = window;
		plusButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					window.addRow(Row.this);
				}
			}
		);
		minusButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					window.removeRow(Row.this);
				}
			}
		);
		comboBox = new JComboBox();
		comboBox.setMaximumRowCount(20);
		comboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		comboBox.setRenderer(comboBoxCellRenderer);
		rebuildComboBox(rows);
		Dimension size = comboBox.getPreferredSize();
		size.width = COMBO_BOX_WIDTH;
		comboBox.setPreferredSize(size);
		comboBox.setMaximumSize(size);
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					handleComboBoxAction();
				}
			}
		);
		box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
		box.add(plusButton);
		box.add(Box.createHorizontalStrut(5));
		box.add(minusButton);
		box.add(Box.createHorizontalStrut(10));
		box.add(comboBox);
		box.add(Box.createHorizontalStrut(10));
		box.add(new JLabel("is"));
		box.add(Box.createHorizontalStrut(10));
		box.setMaximumSize(box.getPreferredSize());
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(box);
		rebuildCriterionComponent();
		setAlignmentX(Component.LEFT_ALIGNMENT);
	}

	/**	Rebuilds the criterion component. */

	private void rebuildCriterionComponent () {
		try {
			boolean isOldComponent = getComponentCount() > 1;
			if (isOldComponent) {
				remove(1);
				Class oldCls = criterionComponent.getClass();
				SearchCriterion oldValue = criterionComponent.getValue();
				window.handleValueChanged(oldCls, oldValue, null);
			}
			Criterion criterion = CRITERIA[index];
			Class cls = criterion.cls;
			Class modelCls = criterion.modelCls;
			SearchCriterion initialValue = null;
			if (cls.equals(CorpusCriterion.class)) {
				initialValue = window.getCorpus();
			} else {
				SearchDefaults defaults = window.getDefaults();
				if (defaults != null) {
					initialValue = defaults.getSearchDefault(criterion.modelCls);
				}
			}
			criterionComponent = (CriterionComponent)cls.newInstance();
			JComponent component = criterionComponent.init(window, this, initialValue);
			component.setMaximumSize(component.getPreferredSize());
			add(component);
			box.setAlignmentY(component.getAlignmentY());
			revalidate();
			setBorder(BorderFactory.createEmptyBorder(0,0,10,5));
			setMaximumSize(getPreferredSize());
			repaint();
			if (isOldComponent) {
				Class newCls = criterionComponent.getClass();
				SearchCriterion newValue = criterionComponent.getValue();
				window.handleValueChanged(newCls, null, newValue);
			}
		} catch (Exception e) {
			Err.err(e);
		}
	}

	/**	Handles a combo box action event. */

	private void handleComboBoxAction () {
		if (ignoreComboBoxEvents) return;
		Criterion criterion = (Criterion)comboBox.getSelectedItem();
		for (int newIndex = 0; newIndex < NUM_CRITERIA; newIndex++) {
			if (criterion.equals(CRITERIA[newIndex])) {
				if (index == newIndex) return;
				index = newIndex;
				rebuildCriterionComponent();
				window.rebuildRowComboBoxes();
			}
		}
	}

	/**	Enables or disables the plus button.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	void setPlusEnabled (boolean enabled) {
		plusButton.setEnabled(enabled);
	}


	/**	Enables or disables the minus button.
	 *
	 *	@param	enabled		True to enable, false to disable.
	 */

	void setMinusEnabled (boolean enabled) {
		minusButton.setEnabled(enabled);
	}

	/**	Removes a criterion if present.
	 *
	 *	@param	criterion	The criterion to remove.
	 */

	public void removeCriterion (Class criterion) {
		for (int i = 0; i < comboBox.getItemCount(); i++) {
			Object o = comboBox.getItemAt(i);
			if(((Criterion)o).cls.equals(criterion)) {
				comboBox.removeItemAt(i);
				return;
			}
		}
	}


	/**	Rebuilds the combo box based on class
	 *
	 *	<p>The combo box is rebuilt so that it contains only items for
	 *	classes in the passed array
	 *
	 *	@param	classes	All rows.
	 */

/*
	static void populateComboBox (Collection classes) {
		comboBox.removeAllItems();
		for (int i = 0; i < NUM_CRITERIA; i++) {
			Criterion criterion = CRITERIA[i];
			if(classes.contains(criterion.cls)) comboBox.addItem(criterion);
		}
	}*/

	/**	Rebuilds the combo box.
	 *
	 *	<p>The combo box is rebuilt so that it contains only items not
	 *	already present in other rows. The currently selected item is
	 *	not changed.
	 *
	 *	@param	rows	All rows.
	 */

	void rebuildComboBox (Row[] rows) {
		ignoreComboBoxEvents = true;
		comboBox.removeAllItems();
		boolean[] indexUsed = new boolean[NUM_CRITERIA];
		for (int i = 0; i < rows.length; i++)
			indexUsed[rows[i].index] = true;
		if (index == -1) {
			for (index = 0; index < NUM_CRITERIA; index++)
				if (!indexUsed[index]) break;
		}
		for (int i = 0; i < NUM_CRITERIA; i++) {
			Criterion criterion = CRITERIA[i];
			if (indexUsed[i] && i != index) continue;
			comboBox.addItem(criterion);
			if (i == index) comboBox.setSelectedItem(criterion);
		}
		ignoreComboBoxEvents = false;
	}

	/**	Handles a value changed event in a criterion.
	 *
	 *	@param	cls			The class of the criterion that changed, or
	 *						null to change self.
	 *
	 *	@param	oldVal		Old value. Null if the row has just
	 *						been created.
	 *
	 *	@param	newVal		New value. Null if the row has just been
	 *						deleted.
	 */

	void handleValueChanged (Class cls, SearchCriterion oldVal,
		SearchCriterion newVal)
	{
		if (CRITERIA[index].cls.equals(cls)) return;
		JComponent component = criterionComponent.getSwingComponent();
		Dimension small = new Dimension(10, 10);
		Dimension big = new Dimension(1000, 1000);
		setMinimumSize(small);
		setMaximumSize(big);
		component.setMinimumSize(small);
		component.setMaximumSize(big);
		try {
			criterionComponent.handleValueChanged(cls, oldVal, newVal);
		} catch (Exception e) {
			Err.err(e);
		}
		component.setMaximumSize(component.getPreferredSize());
		component.setMinimumSize(component.getPreferredSize());
		setMaximumSize(getPreferredSize());
		setMinimumSize(getPreferredSize());
		revalidate();
		component.revalidate();
		component.repaint();
	}

	/**	Gets the criterion component.
	 *
	 *	@return		The criterion component.
	 */

	CriterionComponent getCriterionComponent () {
		return criterionComponent;
	}

	/**	Gets the box height.
	 *
	 *	@return		The height of the box that contains the plus and minus
	 *				buttons and combo box.
	 */

	int getBoxHeight () {
		return box.getPreferredSize().height;
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

