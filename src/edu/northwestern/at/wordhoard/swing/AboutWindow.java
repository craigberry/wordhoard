package edu.northwestern.at.wordhoard.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.db.*;
import edu.northwestern.at.utils.sys.*;

/**	The about window.
 */

public class AboutWindow extends AbstractWindow {

	/**	The about window, or null if none is open. */

	private static AboutWindow aboutWindow;

	/**	Opens or brings to the front the about window.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public static void open (AbstractWindow parentWindow)
		throws PersistenceException
	{
		if (aboutWindow == null) {
			aboutWindow = new AboutWindow(parentWindow);
		} else {
			aboutWindow.setVisible(true);
			aboutWindow.toFront();
		}
	}

	/**	Creates a new about window.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *
	 *	@throws	PersistenceException	error in persistence layer.
	 */

	public AboutWindow (AbstractWindow parentWindow)
		throws PersistenceException
	{
		super
		(
			WordHoardSettings.getString
			(
				"AboutWordHoard" ,
				"About WordHoard"
			) ,
			parentWindow
		);

		setResizable(false);
		enableGetInfoCmd(false);

//		JLabel icon = new JLabel(Images.get("icon.gif"));
		JLabel icon = new JLabel(WordHoardSettings.getProgramIcon());
		icon.setBorder(BorderFactory.createLineBorder(Color.black));
		icon.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel box1 = new JPanel();
		box1.setLayout(new BoxLayout(box1, BoxLayout.Y_AXIS));
		box1.add(Box.createVerticalStrut(10));
		box1.add(icon);
		box1.setAlignmentY(Component.TOP_ALIGNMENT);
/*
		JLabel title =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"WordHoard" ,
					"WordHoard"
				)
			);

		title.setFont(new Font("SansSerif", Font.BOLD, 32));
*/
//		JLabel title = new JLabel(Images.get("wordhoard.gif"));
		JLabel title = new JLabel(WordHoardSettings.getProgramLogo());
		title.setBackground(this.getBackground());
		title.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel subTitle0a =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutsubtitle0a" ,
					"An application for the close reading and"
				)
			);

		Font subTitleFont = new Font("Serif", Font.ITALIC, 12);
		subTitle0a.setFont(subTitleFont);
		subTitle0a.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JLabel subTitle0b =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutsubtitle0b" ,
					"scholarly analysis of deeply tagged texts"
				)
			);

		subTitle0b.setFont(subTitleFont);
		subTitle0b.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel subTitle1 =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutsubtitle1" ,
					"A project developed by the Northwestern University"
				)
			);

		subTitle1.setFont(subTitleFont);
		subTitle1.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel subTitle2	=
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutsubtitle2" ,
					"Departments of English and Classics and NU-IT Academic"
				)
			);

		subTitle2.setFont(subTitleFont);
		subTitle2.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel subTitle3	=
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutsubtitle3" ,
					"Technologies and currently maintained by Craig A. Berry   "
				)
			);

		subTitle3.setFont(subTitleFont);
		subTitle3.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel programVersion =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"WordHoard" ,
					"WordHoard"
				) +
				" " +
				WordHoardSettings.getString
				(
					"aboutversion" ,
					"version"
				) +
				" " +
				WordHoardSettings.getProgramVersion() +
				"   "
			);

		programVersion.setFont(subTitleFont);
		programVersion.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel sponsor	=
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutfacultysponsor" ,
					"Principal Investigator"
				)
			);

		Font headerFont = new Font("SansSerif", Font.BOLD, 18);
		sponsor.setFont(headerFont);
		sponsor.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel martin =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutmartin" ,
					"Martin Mueller, Professor Emeritus of English and Classics"
				)
			);

		Font font = new Font("Serif", Font.PLAIN, 12);
		martin.setFont(font);

		JLabel developers	=
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutdevelopers" ,
					"Developers"
				)
			);

		developers.setFont(headerFont);
		developers.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel bill	=
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutbill" ,
					"William Parod, Project Leader"
				)
			);

		bill.setFont(font);

		JLabel jeff =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutjeff" ,
					"Jeff Cousens, Developer"
				)
			);

		jeff.setFont(font);

		JLabel pib =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutpib" ,
					"Philip R. Burns, Developer"
				)
			);

		pib.setFont(font);

		JLabel john =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutjohn" ,
					"John Norstad, Developer"
				)
			);

		john.setFont(font);

		JLabel craig =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutcraig" ,
					"Craig A. Berry, Developer"
				)
			);

		craig.setFont(font);

		JLabel copyright =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutcopyright" ,
					"Copyright \u00A9 2016-2022, Martin Mueller and Craig A. Berry"
				)
			);

		copyright.setFont(subTitleFont);


		JLabel copyright2 =
			new JLabel
			(
				WordHoardSettings.getString
				(
					"aboutcopyright2" ,
					"Copyright \u00A9 2004, 2006 Northwestern University"
				)
			);

		copyright2.setFont(subTitleFont);

		JLabel moreInfoLabel1 =
			new JLabel(WordHoardSettings.getString(
				"aboutformore", "For more information, please visit "));
		moreInfoLabel1.setFont(subTitleFont);

		LinkLabel moreInfoLabel2 =
			new LinkLabel(WordHoardSettings.getString(
				"aboutwebsite", "WordHoard web site"),
				new Link() {
					public void go () {
						try {
							WebStart.showDocument(
								WordHoardSettings.getWebSiteURL());
						} catch (Exception e) {
							Err.err(e);
						}
					}
				},
				true);
		moreInfoLabel2.setFont(subTitleFont);

		JPanel moreInfo = new JPanel();
		moreInfo.setLayout(new BoxLayout(moreInfo, BoxLayout.X_AXIS));
		moreInfo.add(moreInfoLabel1);
		moreInfo.add(moreInfoLabel2);
		moreInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel box2 = new JPanel();
		box2.setLayout(new BoxLayout(box2, BoxLayout.Y_AXIS));
		box2.add(title);
		box2.add(Box.createVerticalStrut(15));
		box2.add(subTitle0a);
		box2.add(subTitle0b);
		box2.add(Box.createVerticalStrut(10));
		box2.add(subTitle1);
		box2.add(subTitle2);
		box2.add(subTitle3);
		box2.add(Box.createVerticalStrut(10));
		box2.add(programVersion);
		box2.add(Box.createVerticalStrut(25));
		box2.add(sponsor);
		box2.add(Box.createVerticalStrut(8));
		box2.add(martin);
		box2.add(Box.createVerticalStrut(25));
		box2.add(developers);
		box2.add(Box.createVerticalStrut(8));
		box2.add(bill);
		box2.add(jeff);
		box2.add(pib);
		box2.add(john);
		box2.add(craig);
		box2.add(Box.createVerticalStrut(25));
		box2.add(copyright);
		box2.add(copyright2);
		box2.add(Box.createVerticalStrut(2));
		box2.add(moreInfo);
		box2.setAlignmentY(Component.TOP_ALIGNMENT);

		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
		box.add(box1);
		box.add(Box.createHorizontalStrut(40));
		box.add(box2);
		box.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));

		setContentPane(box);
		pack();
		WindowPositioning.centerWindowOverWindow(this, null, 0);
		setVisible(true);

	}

	/**	Find a printable component.
	 *
	 *	@return		A printable component.
	 */

	protected Component findPrintableComponent () {
		return getContentPane();
	}

	/**	Handles window dispose events. */

	public void dispose () {
		aboutWindow = null;
		super.dispose();
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

