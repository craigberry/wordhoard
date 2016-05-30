package edu.northwestern.at.wordhoard.swing.info;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.morphology.*;
import edu.northwestern.at.wordhoard.model.counts.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.model.speakers.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;

/**	Speaker panel.
 */
 
class SpeakerPanel extends JPanel {

	/**	Creates a new speaker panel.
	 *
	 *	@param	word			Word occurrence.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	fontSize		Font size.
	 *
	 *	@param	insets			Insets for labeled column.
	 *
	 *	@param	minLabelWidth	Min label width for labeled column.
	 *
	 *	@param	maxValueWidth	Max value width for labeled column.
	 */

	SpeakerPanel (Word word, Corpus corpus, int fontSize, 
		Insets insets, int minLabelWidth, int maxValueWidth) 
	{	
		Speech speech = word.getSpeech();
	
		setBackground(Color.white);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		FontManager fontManager = new FontManager();
		FontInfo romanFontInfo = fontManager.getFontInfo(fontSize);
		Font romanFont = romanFontInfo.getFont();
		
		Collection speakers = speech == null ? null : speech.getSpeakers();
		int numSpeakers = speakers == null ? 0 : speakers.size();
		
		JLabel speakersLabel = new JLabel(numSpeakers <= 1 ?
			"Speaker" : "Speakers");
		speakersLabel.setFont(romanFont);
		speakersLabel.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
		speakersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(speakersLabel);
		
		if (numSpeakers == 0) {
		
			// Special case code requested by Martin. For The Odyssey books 9-12, we use
			// "Spoken by the poet or by Odysseus as narrator." For all other work parts,
			// we use "Spoken by the narrator or poet."
		
			WorkPart workPart = word.getWorkPart();
			String workPartTag = workPart.getTag();
			String noSpeakersStr = null;
			if (workPartTag.equals("ege-OD-9") || 
				workPartTag.equals("ege-OD-10") ||
				workPartTag.equals("ege-OD-11") ||
				workPartTag.equals("ege-OD-12"))
			{
				noSpeakersStr = "Spoken by the poet or by Odysseus as narrator.";
			} else {
				noSpeakersStr = "Spoken by the narrator or poet.";
			}
		
			JLabel noSpeakersLabel = new JLabel(noSpeakersStr);
			noSpeakersLabel.setFont(romanFont);
			noSpeakersLabel.setBorder(BorderFactory.createEmptyBorder(
				0,65,0,0));
			noSpeakersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(noSpeakersLabel);
		
		} else {
		
			LabeledColumn col = 
				new LabeledColumn(insets, romanFont, minLabelWidth, maxValueWidth);
			col.setBackground(Color.white);
			
			boolean firstSpeaker = true;
			for (Iterator it = speakers.iterator(); it.hasNext(); ) {
				if (!firstSpeaker) {
					col.addPair(" ", " ");
				}
				Speaker speaker = (Speaker)it.next();
				String name = speaker.getName();
				Spelling originalName = speaker.getOriginalName();
				String description = speaker.getDescription();
				Gender gender = speaker.getGender();
				Mortality mortality = speaker.getMortality();
				if (name != null || originalName != null) {
					TextLine line = new TextLine();
					if (name != null) {
						line.appendRun(name, romanFontInfo);
						if (originalName != null) {
							line.appendRun(" (", romanFontInfo);
							FontInfo fontInfo = fontManager.getFontInfo(
								originalName.getCharset(), fontSize);
							line.appendRun(originalName.getString(), fontInfo);
							line.appendRun(")", romanFontInfo);
						}
					} else {
						FontInfo fontInfo = fontManager.getFontInfo(
							originalName.getCharset(), fontSize);
						line.appendRun(originalName.getString(), fontInfo);
					}
					Text text = new Text(line);
					WrappedTextComponent textComponent =
						new WrappedTextComponent(text, maxValueWidth);
					col.addPair("Name", textComponent);
				}
				if (description != null) {
					col.addPair("Description", description);
				}
				col.addPair("Gender", gender.toString());
				col.addPair("Mortality", mortality.toString());
				firstSpeaker = false;
			}
			
			col.setAlignmentX(Component.LEFT_ALIGNMENT);
			add(col);
			
		}
		
		setMaximumSize(getPreferredSize());
	
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

