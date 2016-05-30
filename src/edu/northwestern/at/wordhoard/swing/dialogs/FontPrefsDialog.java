package edu.northwestern.at.wordhoard.swing.dialogs;

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
import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.sys.*;

/**	The font preferences dialog.
 */

public class FontPrefsDialog extends ModalDialog {

	/**	Current font family names, indexed by category and character set. */

	private static String[][] fontFamilyNames =
		new String[FontManager.NUM_CATEGORIES][TextParams.NUM_CHARSETS];

	/**	Mac OS default font family names, indexed by category, then character
	 *	set, then preference order.
	 */

	private static final String[][][] MAC_OS_DEFAULTS =
		new String[][][] {
			// Work
			{
				// Roman
				{"GentiumAlt", "Gentium", "Times", "Serif"},
				// Greek
				{"GentiumAlt", "Gentium", "Times", "Serif"},
			},
			// Other
			{
				// Roman
				{"Lucida Grande", "Serif"},
				// Greek
				{"Lucida Grande", "Serif"},
			},
			// Monospaced
			{
				// Roman
				{"Monospaced"},
				// Greek
				{"Monospaced"},
			},
		};

	/**	Windows OS default font family names, indexed by category, then character
	 *	set, then preference order.
	 */

	private static final String[][][] WIN_OS_DEFAULTS =
		new String[][][] {
			// Work
			{
				// Roman
				{"GentiumAlt", "Gentium", "Serif"},
				// Greek
				{"GentiumAlt", "Gentium", "Palatino Linotype", "Serif"},
			},
			// Other
			{
				// Roman
				{"Tahoma", "Serif"},
				// Greek
				{"Tahoma", "Serif"},
			},
			// Monospaced
			{
				// Roman
				{"Monospaced"},
				// Greek
				{"Monospaced"},
			},
		};

	/**	Other OS default font family names, indexed by category, then character
	 *	set, then preference order.
	 */

	private static final String[][][] OTHER_OS_DEFAULTS =
		new String[][][] {
			// Work
			{
				// Roman
				{"GentiumAlt", "Gentium", "Serif"},
				// Greek
				{"GentiumAlt", "Gentium", "Serif"},
			},
			// Other
			{
				// Roman
				{"GentiumAlt", "Gentium", "SansSerif"},
				// Greek
				{"GentiumAlt", "Gentium", "SansSerif"},
			},
			// Monospaced
			{
				// Roman
				{"Monospaced"},
				// Greek
				{"Monospaced"},
			},
		};

	/**	Default font family names for the current OS. */

	private static final String[][][] defaults;

	static {
		if (Env.MACOSX) {
			defaults = MAC_OS_DEFAULTS;
		} else if (Env.WINDOWSOS) {
			defaults = WIN_OS_DEFAULTS;
		} else {
			defaults = OTHER_OS_DEFAULTS;
		}
	}

	/**	Returns true if a font family exists.
	 *
	 *	@param	family		Font family name.
	 *
	 *	@return				True if font family exists.
	 */

	private static boolean familyExists (String family) {
		if (family == null) return false;
		Font font = new Font(family, Font.PLAIN, 12);
		return family.equals(font.getFamily());
	}

	/**	Initializes a font family.
	 *
	 *	@param	category		Font category.
	 *
	 *	@param	charset			Character set.
	 */

	private static void initFontFamily (int category, int charset) {
		String[] families = defaults[category][charset];
		for (int i = 0; i < families.length; i++) {
			String family = families[i];
			if (familyExists(family)) {
				fontFamilyNames[category][charset] = family;
				return;
			}
		}
		fontFamilyNames[category][charset] = families[families.length-1];
	}

	/**	Initialize the default font families. */

	static {
		for (int i = 0; i < FontManager.NUM_CATEGORIES; i++)
			for (int j = 0; j < TextParams.NUM_CHARSETS; j++)
				initFontFamily(i, j);
	}

	/**	Creates a new font preferences dialog.
	 *
	 *	@param	parentWindow	Parent window.
	 */

	public FontPrefsDialog (AbstractWindow parentWindow) {

		super("Font Preferences", parentWindow);

		GraphicsEnvironment graphEnv =
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		String[] availableFontFamilyNames =
			graphEnv.getAvailableFontFamilyNames();
		TreeSet romanFonts = new TreeSet();
		TreeSet greekFonts = new TreeSet();
		for (int i = 0; i < availableFontFamilyNames.length; i++) {
			String familyName = availableFontFamilyNames[i];
			if (familyName.equals("Default")) continue;
			Font font = new Font(familyName, Font.PLAIN, 12);
			boolean canDisplayRoman =
				font.canDisplayUpTo(CharsUsed.ROMAN) == -1;
			boolean canDisplayGreek =
				font.canDisplayUpTo(CharsUsed.GREEK) == -1;
			if (canDisplayRoman) romanFonts.add(familyName);
			if (canDisplayGreek) greekFonts.add(familyName);
		}

		final JComboBox latinWorkComboBox = new JComboBox();
		final JComboBox greekWorkComboBox = new JComboBox();
		final JComboBox latinOtherComboBox = new JComboBox();
		final JComboBox greekOtherComboBox = new JComboBox();
		latinWorkComboBox.setMaximumRowCount(20);
		greekWorkComboBox.setMaximumRowCount(20);
		latinOtherComboBox.setMaximumRowCount(20);
		greekOtherComboBox.setMaximumRowCount(20);
		for (Iterator it = romanFonts.iterator(); it.hasNext(); ) {
			String fontFamily = (String)it.next();
			latinWorkComboBox.addItem(fontFamily);
			latinOtherComboBox.addItem(fontFamily);
		}
		for (Iterator it = greekFonts.iterator(); it.hasNext(); ) {
			String fontFamily = (String)it.next();
			greekWorkComboBox.addItem(fontFamily);
			greekOtherComboBox.addItem(fontFamily);
		}

		latinWorkComboBox.setSelectedItem(
			fontFamilyNames[FontManager.WORK][TextParams.ROMAN]);
		greekWorkComboBox.setSelectedItem(
			fontFamilyNames[FontManager.WORK][TextParams.GREEK]);
		latinOtherComboBox.setSelectedItem(
			fontFamilyNames[FontManager.OTHER][TextParams.ROMAN]);
		greekOtherComboBox.setSelectedItem(
			fontFamilyNames[FontManager.OTHER][TextParams.GREEK]);

		FontManager fontManager = new FontManager();
		Font romanFont = fontManager.getFont(10);

		StringBuffer buf = new StringBuffer();
		buf.append("The following fonts are used to display the main ");
		buf.append("text of works and annotations in the Roman and ");
		buf.append("Greek alphabets.");
		WrappedTextComponent panel1 =
			new WrappedTextComponent(buf.toString(), romanFont, 350);
		panel1.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
		add(panel1);

		LabeledColumn workCol = new LabeledColumn();
		workCol.addPair("Roman works", latinWorkComboBox);
		workCol.addPair("Greek works", greekWorkComboBox);
		add(workCol);

		buf = new StringBuffer();
		buf.append("The following fonts are used in all other contexts ");
		buf.append("(get info windows, tables, concordances, etc.)");
		WrappedTextComponent panel2 =
			new WrappedTextComponent(buf.toString(), romanFont, 350);
		panel2.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
		add(panel2);

		LabeledColumn otherCol = new LabeledColumn();
		otherCol.addPair("Roman other", latinOtherComboBox);
		otherCol.addPair("Greek other", greekOtherComboBox);
		add(otherCol);

		buf = new StringBuffer();
		buf.append("Font changes are only used in new windows that you open. ");
		buf.append("They do not affect windows that are already open.");
		WrappedTextComponent panel3 =
			new WrappedTextComponent(buf.toString(), romanFont, 350);
		panel3.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
		add(panel3);

		addButton("Cancel",
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

		addDefaultButton("OK",
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						fontFamilyNames[FontManager.WORK][TextParams.ROMAN] =
							(String)latinWorkComboBox.getSelectedItem();
						fontFamilyNames[FontManager.WORK][TextParams.GREEK] =
							(String)greekWorkComboBox.getSelectedItem();
						fontFamilyNames[FontManager.OTHER][TextParams.ROMAN] =
							(String)latinOtherComboBox.getSelectedItem();
						fontFamilyNames[FontManager.OTHER][TextParams.GREEK] =
							(String)greekOtherComboBox.getSelectedItem();
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

	/**	Gets a copy of the font preferences.
	 *
	 *	@return		The font family names, as a two dimensional array of
	 *				strings indexed first by category and then by
	 *				character set.
	 */

	public static String[][] getFontPrefs () {
		String[][] result =
		 	new String[FontManager.NUM_CATEGORIES][TextParams.NUM_CHARSETS];
		for (int i = 0; i < FontManager.NUM_CATEGORIES; i++)
			for (int j = 0; j < TextParams.NUM_CHARSETS; j++)
				result[i][j] = fontFamilyNames[i][j];
		return result;
	}

	/**	Sets the font preferences.
	 *
	 *	@param	prefs	The font family names, as a two dimensional array of
	 *					strings indexed first by category and then by
	 *					character set.
	 */

	public static void setFontPrefs (String[][] prefs) {
		for (int i = 0; i < FontManager.NUM_CATEGORIES; i++) {
			for (int j = 0; j < TextParams.NUM_CHARSETS; j++) {
				String family = prefs[i][j];
				if (familyExists(family)) {
					fontFamilyNames[i][j] = family;
				} else {
					initFontFamily(i, j);
				}
			}
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

