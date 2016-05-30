package edu.northwestern.at.wordhoard.swing.dialogs;

/*	Please see the license information at the end of this file. */

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.text.*;
import edu.northwestern.at.utils.xml.*;
import edu.northwestern.at.utils.swing.*;

/**	The site selection dialog.
*/

public class SiteDialog extends ModalDialog {

	/**	Database URL. */

	private static String databaseURL;

	/**	Database username. */

	private static String databaseUsername;
	
	/**	Database password. */

	private static String databasePassword;
	
	/**	Server URL. */

	private static String serverURL;
	
	/**	Site id. */
	
	private static String siteId;
	
	/**	Currently selected index in site combo box. */
	
	private static int selected = -1;
	
	/**	Gets the database URL.
	 *
	 *	@return		The database URL.
	 */

	public static String getDatabaseURL () {
		return databaseURL;
	}
	
	/**	Gets the database username.
	 *
	 *	@return		The database username.
	 */
	
	public static String getDatabaseUsername () {
		return databaseUsername;
	}
	
	/**	Gets the database password.
	 *
	 *	@return		The database password.
	 */
	
	public static String getDatabasePassword () {
		return databasePassword;
	}
	
	/**	Gets the server URL.
	 *
	 *	@return		The server URL.
	 */
	
	public static String getServerURL () {
		return serverURL;
	}
	
	/**	Gets the site id.
	 *
	 *	@return		The site id.
	 */
	
	public static String getSiteId () {
		return siteId;
	}
	
	/**	Sets the site id.
	 *
	 *	@param	siteId		The site id.
	 */
	
	public static void setSiteId (String siteId) {
		SiteDialog.siteId = siteId;
	}
	
	/**	Sets the site information.
	 *
	 *	@param	siteEl		Site element.
	 */
	 
	private static void setInfo (Element siteEl) {
		Element databaseEl = DOMUtils.getChild(siteEl, "database");
		Element urlEl = DOMUtils.getChild(databaseEl, "url");
		databaseURL = DOMUtils.getText(urlEl);
		Element usernameEl = DOMUtils.getChild(databaseEl, "username");
		databaseUsername = DOMUtils.getText(usernameEl);
		Element passwordEl = DOMUtils.getChild(databaseEl, "password");
		databasePassword = DOMUtils.getText(passwordEl);
		Element serverEl = DOMUtils.getChild(siteEl, "server");
		serverURL = DOMUtils.getText(serverEl);
	}
	
	/**	Gets the site information.
	 *
	 *	@param	args		Command line arguments.
	 *
	 *	@throws Exception
	 */
	
	public static void getSiteInfo (String[] args) 
		throws Exception
	{
		String sitesURL = null;
		if (args.length > 0) {
			sitesURL = args[0];
		} else {
			sitesURL = WordHoardSettings.getString(
				"SitesURL",
				"http://wordhoard.northwestern.edu/sites.xml");
		}
		URL url = new URL(sitesURL);
		InputStream stream = url.openStream();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(stream);
		Element root = DOMUtils.getChild(doc, "WordHoardSites");
		ArrayList siteEls = DOMUtils.getChildren(root, "site");
		
		if (siteEls.size() == 1) {
			setInfo((Element)siteEls.get(0));
		} else {
			new SiteDialog(siteEls);
		}
		
		if (databaseURL == null) System.exit(0);
	}
	
	/**	Gets a site description.
	 *
	 *	@param	siteEl		Site element.
	 *
	 *	@return				Site description.
	 */
	
	private String getDescription (Element siteEl) {
		String str = DOMUtils.getText(DOMUtils.getChild(siteEl, "description"));
		return str.replaceAll("\\s+", " ");
	}
	
	/**	Creates a new site dialog.
	 *
	 *	@param	siteEls		List of site elements.
	 */
	 
	public SiteDialog (ArrayList siteEls) {
	
		super("Select a site");
		
		int numSites = siteEls.size();
		final Element[] sites = (Element[])siteEls.toArray(new Element[numSites]);
		
		JLabel label = new JLabel("Please select a WordHoard site.");
		add(label, 10);
		
		final JComboBox siteComboBox = new JComboBox();
		for (int i = 0; i < numSites; i++) {
			Element siteEl = sites[i];
			Element nameEl = DOMUtils.getChild(siteEl, "name");
			String name = DOMUtils.getText(nameEl);
			siteComboBox.addItem(name);
			String id = siteEl.getAttribute("id");
			if (id.equals(siteId)) selected = i;
		}
		if (selected == -1) selected = 0;
		siteComboBox.setSelectedIndex(selected);
		add(siteComboBox, 20);

		FontManager fontManager = new FontManager();
		Font romanFont = fontManager.getFont(12);
		final JTextArea descriptionPanel = new JTextArea();
		descriptionPanel.setPreferredSize(new Dimension(450, 100));
		descriptionPanel.setFont(romanFont);
		descriptionPanel.setEditable(false);
		descriptionPanel.setLineWrap(true);
		descriptionPanel.setWrapStyleWord(true);
		descriptionPanel.setBackground(getBackground());
		descriptionPanel.setText(getDescription(sites[selected]));
		add(descriptionPanel, 20);

		siteComboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent e) {
					int index = siteComboBox.getSelectedIndex();
					if (index == selected) return;
					selected = index;
					descriptionPanel.setText(getDescription(sites[selected]));
				}
			}
		);

		addButton("Quit",
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

		addDefaultButton("Connect",
			new ActionListener () {
				public void actionPerformed (ActionEvent event) {
					try {
						Element siteEl = sites[selected];
						setInfo(siteEl);
						siteId = siteEl.getAttribute("id");
						dispose();
					} catch (Exception e) {
						Err.err(e);
					}
				}
			}
		);
		
		setResizable(false);
		show(null);
		
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

