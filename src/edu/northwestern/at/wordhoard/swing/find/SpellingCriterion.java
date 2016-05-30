package edu.northwestern.at.wordhoard.swing.find;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.swing.*;

/**	Spelling criterion component.
 */
 
class SpellingCriterion extends CriterionComponent {

	/**	The component. */
	
	private JPanel panel = new JPanel();

	/**	The text field. */
	
	private JTextField textField = new JTextField("", 18);
	
	/**	The primary strength radio button. */
	
	private JRadioButton primaryStrengthRadioButton =
		new JRadioButton("Case and diacritical insensitive");
	
	/**	The secondary strength radio button. */
	
	private JRadioButton secondaryStrengthRadioButton =
		new JRadioButton("Case insensitive, diacritical sensitive");
	
	/**	The tertiary strength radio button. */
	
	private JRadioButton tertiaryStrengthRadioButton =
		new JRadioButton("Case and diacritical sensitive");
	
	/**	Font manager. */
	
	private FontManager fontManager = new FontManager();
	
	/**	Spelling with collation strength criterion. */
	
	private SpellingWithCollationStrength spellingWithCollationStrength;
	
	/**	Corpus, or null if none. */
	
	private Corpus corpus;
	
	/**	Character set. */
	
	private byte charset = -1;

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
		spellingWithCollationStrength = (SpellingWithCollationStrength)val;
		if (spellingWithCollationStrength == null) {
			primaryStrengthRadioButton.setSelected(true);
		} else {
			Spelling spelling = spellingWithCollationStrength.getSpelling();
			textField.setText(spelling.getString());
			int strength = spellingWithCollationStrength.getStrength();
			switch (strength) {
				case Collator.PRIMARY:
					primaryStrengthRadioButton.setSelected(true);
					break;
				case Collator.SECONDARY:
					secondaryStrengthRadioButton.setSelected(true);
					break;
				case Collator.TERTIARY:
					tertiaryStrengthRadioButton.setSelected(true);
					break;
			}
		}
		corpus= (Corpus)getOtherRowValue(CorpusCriterion.class);
		setFontFromCorpus();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setAlignmentY(Component.TOP_ALIGNMENT);
		textField.setAlignmentX(Component.LEFT_ALIGNMENT);
		Dimension size = textField.getPreferredSize();
		textField.setMaximumSize(size);
		textField.setMinimumSize(size);
		textField.addKeyListener(
			new KeyAdapter() {
				public void keyTyped (KeyEvent event) {
					handleKeyTyped(event.getKeyChar());
				}
			}
		);
		ButtonGroup strengthButtonGroup = new ButtonGroup();
		strengthButtonGroup.add(primaryStrengthRadioButton);
		strengthButtonGroup.add(secondaryStrengthRadioButton);
		strengthButtonGroup.add(tertiaryStrengthRadioButton);
		primaryStrengthRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		secondaryStrengthRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		tertiaryStrengthRadioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(textField);
		panel.add(Box.createVerticalStrut(3));
		panel.add(primaryStrengthRadioButton);
		panel.add(secondaryStrengthRadioButton);
		panel.add(tertiaryStrengthRadioButton);
		return panel;
	}
	
	/**	Gets the value of the component.
	 *
	 *	@return			The spelling with collation strength.
	 */
	 
	SearchCriterion getValue () {
		String str = textField.getText().trim();
		if (str.length() == 0) return null;
		str = CharsetUtils.translateTonosToOxia(str);
		int strength;
		if (primaryStrengthRadioButton.isSelected()) {
			strength = Collator.PRIMARY;
		} else if (secondaryStrengthRadioButton.isSelected()) {
			strength = Collator.SECONDARY;
		} else {
			strength = Collator.TERTIARY;
		}
		if (charset == -1) charset = TextParams.ROMAN;
		Spelling spelling = new Spelling(str, charset);
		spellingWithCollationStrength =
			new SpellingWithCollationStrength(spelling, strength);
		return spellingWithCollationStrength;
	}
	
	/**	Sets the criteria value.
	 *
	 *	@param	searchCriteria		Search criteria.
	 *
	 *	@return			Validation error message, or null if none.
	 */
	 
	String setCriteria (SearchCriteria searchCriteria) {
		searchCriteria.add(getValue());
		return null;
	}
	
	/**	Handles a key typed event.
	 *
	 *	@param	c		Character typed.
	 */
	 
	private void handleKeyTyped (char c) {
		if (corpus != null) return;
		boolean isUsedRoman = CharsUsed.isUsedRoman(c);
		boolean isUsedGreek = CharsUsed.isUsedGreek(c);
		if (isUsedGreek && !isUsedRoman) {
			setFont(TextParams.GREEK);
		} else if (isUsedRoman && !isUsedGreek) {
			setFont(TextParams.ROMAN);
		}
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
		if (cls.equals(CorpusCriterion.class)) {
			corpus = (Corpus)newVal;
			setFontFromCorpus();
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

