package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.concordance.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	Get concordance panel.
 */
 
class GetConcordancePanel extends JPanel {

	/**	Creates a new get concordance panel.
	 *
	 *	@param	lemma				Lemma, or null if none.
	 *
	 *	@param	pos					Part of speech, or null if none.
	 *
	 *	@param	spelling			Spelling, or null if none.
	 *
	 *	@param	corpus				Corpus.
	 *
	 *	@param	fontSize			Font size.
	 *
	 *	@param	parentWindow		Parent window.
	 */

	GetConcordancePanel (final Lemma lemma, final Pos pos,
		final Spelling spelling, final Corpus corpus, 
		int fontSize, final AbstractWindow parentWindow) 
	{
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		FontManager fontManager = new FontManager();
		Font romanFont = fontManager.getFont(fontSize);

		JLabel concordLabel = new JLabel("Get Concordance for:");
		concordLabel.setFont(romanFont);
		concordLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		concordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(concordLabel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setBackground(Color.white);
		
		if (lemma != null) {
			
			JButton findSameLemmaButton = new JButton("Lemma");
			findSameLemmaButton.setBackground(Color.white);
			findSameLemmaButton.setFont(romanFont);
			findSameLemmaButton.addActionListener(
				new ActionListener() {
					public void actionPerformed (ActionEvent event) {
						try {
							SearchCriteria sc = new SearchCriteria(corpus, 
								lemma, null, null, 0);
							new ConcordanceWindow(sc, parentWindow);
						} catch (Exception e) {
							Err.err(e);
						}
					}
				}
			);
			findSameLemmaButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			buttonPanel.add(findSameLemmaButton);
			buttonPanel.add(Box.createHorizontalGlue());
		
		}
		
		if (lemma != null && pos != null) {
		
			JButton findSameLemmaPosButton = new JButton("Word Form");
			findSameLemmaPosButton.setBackground(Color.white);
			findSameLemmaPosButton.setFont(romanFont);
			findSameLemmaPosButton.addActionListener(
				new ActionListener() {
					public void actionPerformed (ActionEvent event) {
						try {
							SearchCriteria sc = new SearchCriteria(corpus, 
								lemma, pos, null, 0);
							new ConcordanceWindow(sc, parentWindow);
						} catch (Exception e) {
							Err.err(e);
						}
					}
				}
			);
			findSameLemmaPosButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			buttonPanel.add(findSameLemmaPosButton);
			buttonPanel.add(Box.createHorizontalGlue());
			
		}
		
		if (spelling != null) {
		
			JButton findSameSpellingButton = new JButton("Spelling");
			findSameSpellingButton.setBackground(Color.white);
			findSameSpellingButton.setFont(romanFont);
			findSameSpellingButton.addActionListener(
				new ActionListener() {
					public void actionPerformed (ActionEvent event) {
						try {
							SearchCriteria sc = new SearchCriteria(corpus,
								lemma, pos, spelling, Collator.PRIMARY); 
							new ConcordanceWindow(sc, parentWindow);
						} catch (Exception e) {
							Err.err(e);
						}
					}
				}
			);
			findSameSpellingButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			buttonPanel.add(findSameSpellingButton);
			
		}
		
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(buttonPanel);
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

