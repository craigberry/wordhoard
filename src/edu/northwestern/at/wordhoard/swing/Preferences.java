package edu.northwestern.at.wordhoard.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;

import edu.northwestern.at.wordhoard.model.text.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.wordhoard.swing.tcon.*;
import edu.northwestern.at.wordhoard.swing.dialogs.*;
import edu.northwestern.at.utils.sys.*;

/**	Preferences.
 *
 *	<p>Preferences are stored as a WebStart muffin.
 */
 
 public class Preferences {
 	
 	/**	Writes the preferences. */
 	
 	public static void writePrefs () {
 		String[][] fontPrefs = FontPrefsDialog.getFontPrefs();
 		Properties properties = new Properties();
 		properties.setProperty("romanWorkFont",
 			fontPrefs[FontManager.WORK][TextParams.ROMAN]);
 		properties.setProperty("greekWorkFont",
 			fontPrefs[FontManager.WORK][TextParams.GREEK]);
 		properties.setProperty("romanOtherFont",
 			fontPrefs[FontManager.OTHER][TextParams.ROMAN]);
 		properties.setProperty("greekOtherFont",
 			fontPrefs[FontManager.OTHER][TextParams.GREEK]);
 		String corpusTconTag = TableOfContentsWindow.getCorpusPref();
 		if (corpusTconTag != null)
			properties.setProperty("corpusTconTag", corpusTconTag);
		String siteId = SiteDialog.getSiteId();
		if (siteId != null)
			properties.setProperty("siteId", siteId);
 		WebStart.putMuffin("prefs", properties);
 	}
 	
 	/**	Reads the preferences. */
 	
 	public static void readPrefs () {
 		Properties properties = (Properties)WebStart.getMuffin("prefs");
 		if (properties == null) return;
 		String[][] fontPrefs = FontPrefsDialog.getFontPrefs();
 		fontPrefs[FontManager.WORK][TextParams.ROMAN] = 
 			properties.getProperty("romanWorkFont");
 		fontPrefs[FontManager.WORK][TextParams.GREEK] = 
 			properties.getProperty("greekWorkFont");
 		fontPrefs[FontManager.OTHER][TextParams.ROMAN] = 
 			properties.getProperty("romanOtherFont");
 		fontPrefs[FontManager.OTHER][TextParams.GREEK] = 
 			properties.getProperty("greekOtherFont");
 		FontPrefsDialog.setFontPrefs(fontPrefs);
 		TableOfContentsWindow.setCorpusPref(
 			properties.getProperty("corpusTconTag"));
 		SiteDialog.setSiteId(
 			properties.getProperty("siteId"));
 	}
	
	/**	Hides the default no-arg constructor.
	 */
	 
	private Preferences () {
		throw new UnsupportedOperationException();
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

