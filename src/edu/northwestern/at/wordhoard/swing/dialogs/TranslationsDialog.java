package edu.northwestern.at.wordhoard.swing.dialogs;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.event.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.model.*;
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.swing.*;

/**	The translations dialog.
 */

public class TranslationsDialog extends ModalDialog {

	/**	Map from corpora to lists of selected translation names. */

	private static Map selectedTranslationsMap = new HashMap();

	/**	True if dialog canceled. */

	private boolean canceled  = true;

	/**	Creates a new translations dialog.
	 *
	 *	@param	corpus			Corpus.
	 *
	 *	@param	parentWindow	Parent window.
	 *
	 *	@throws	Exception
	 */

	public TranslationsDialog (final Corpus corpus,
		AbstractWindow parentWindow)
			throws Exception
	{

		super("Translations, Transliterations, Etc.", parentWindow);

		FontManager fontManager = new FontManager();
		FontInfo romanFontInfo = fontManager.getFontInfo(10);
		
		String tranDescription = corpus.getTranDescription();
		tranDescription = tranDescription + "\n";
		if (tranDescription != null) {
			Text text = new Text();
			int pos = 0;
			int len = tranDescription.length();
			while (pos < len) {
				int k = tranDescription.indexOf('\n', pos);
				if (k < 0) break;
				String str = tranDescription.substring(pos, k);
				text.copyLine(str, romanFontInfo);
				pos = k+1;
			}
			text.finalize();
			WrappedTextComponent textComponent =
				new WrappedTextComponent(text, 380);
			textComponent.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
			add(textComponent);
		}
		
		/*
		String pathToVerbiage =
			"edu/northwestern/at/wordhoard/swing/resources/" +
			corpus.getTag() + "-trandlog.txt";
		ClassLoader classLoader =
			TranslationsDialog.class.getClassLoader();
		InputStream inputStream =
			classLoader.getResourceAsStream(pathToVerbiage);
		if (inputStream != null) {
			BufferedReader in =
				new BufferedReader(new InputStreamReader(inputStream));
			Text text = new Text();
			String str;
			while ((str = in.readLine()) != null) {
				text.copyLine(str, romanFontInfo);
			}
			in.close();
			text.finalize();
			WrappedTextComponent textComponent =
				new WrappedTextComponent(text, 380);
			textComponent.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
			add(textComponent);
		}
		*/

		ArrayList selectedTranslations =
			(ArrayList)selectedTranslationsMap.get(corpus);
		if (selectedTranslations == null)
			selectedTranslations = new ArrayList();

		String availableTranslationsString = corpus.getTranslations();
		StringTokenizer tok =
			new StringTokenizer(availableTranslationsString, ",");
		final int numAvailableTranslations = tok.countTokens();
		final JCheckBox[] checkBoxes = new JCheckBox[numAvailableTranslations];
		for (int i = 0; i < numAvailableTranslations; i++) {
			String translation = tok.nextToken();
			JCheckBox checkBox = new JCheckBox(translation);
			checkBox.setSelected(selectedTranslations.contains(translation));
			checkBoxes[i] = checkBox;
			add(checkBox);
		}

		addButton("Cancel",
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						canceled = true;
						dispose();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		addDefaultButton("OK",
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						canceled = false;
						ArrayList selectedTranslations = new ArrayList();
						for (int i = 0; i < numAvailableTranslations; i++) {
							JCheckBox checkBox = checkBoxes[i];
							if (checkBox.isSelected())
								selectedTranslations.add(checkBox.getText());
						}
						if (selectedTranslations.size() == 0) {
							selectedTranslationsMap.remove(corpus);
						} else {
							selectedTranslationsMap.put(corpus,
								selectedTranslations);
						}
						dispose();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);

		setResizable(false);
		show(parentWindow);

	}

	/**	Returns true if the dialog was canceled.
	 *
	 *	@return		True if canceled.
	 */

	public boolean canceled () {
		return canceled;
	}

	/**	Gets the selected translations for a corpus.
	 *
	 *	@param	corpus		Corpus.
	 *
	 *	@return				List of selected translation names, or null
	 *						if none.
	 */

	public static java.util.List getSelectedTranslations (Corpus corpus) {
		ArrayList selectedTranslations =
			(ArrayList)selectedTranslationsMap.get(corpus);
		if (selectedTranslations == null) return null;
		if (selectedTranslations.size() == 0) return null;
		return selectedTranslations;
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

