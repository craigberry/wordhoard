package edu.northwestern.at.wordhoard.swing.accounts;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.wordhoard.swing.accounts.groups.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.server.*;
import edu.northwestern.at.utils.swing.*;
import javax.swing.border.*;

/**	The manage accounts window.
 */

public class ManageAccountsWindow extends AbstractWindow {

	/**	The manage accounts window, or null if none is open. */

	private static ManageAccountsWindow manageAccountsWindow;

	/**	Opens or brings to the front the manage accounts window.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *
	 *	@throws	Exception	general error.
	 */

	public static void open (AbstractWindow parentWindow)
		throws Exception
	{
		if (manageAccountsWindow == null) {
			WordHoardSession session = WordHoard.getSession();
			if (session == null) {
				new ErrorMessage("Could not connect to WordHoard server.",
					parentWindow);
				return;
			}
			try {
				manageAccountsWindow =
					new ManageAccountsWindow(parentWindow, session);
			} catch (WordHoardError e) {
				new ErrorMessage(e.getMessage(), parentWindow);
			}
		} else {
			manageAccountsWindow.setVisible(true);
			manageAccountsWindow.toFront();
		}
	}

	/**	Close the manage accounts window.
	 */

	public static void close () {
		if (manageAccountsWindow != null) {
			manageAccountsWindow.dispose();
		}
	}

	/**	Creates a new manage accounts window.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 *
	 *	@param	session			Server session.
	 *
	 *	@throws	Exception	general error.
	 */

	public ManageAccountsWindow (AbstractWindow parentWindow,
		WordHoardSession session)
			throws Exception
	{

		super("Manage Accounts", parentWindow);

		enableGetInfoCmd(false);

		// create account pane
		final AccountModel model = new AccountModel(session, this);

		LeftPanel leftPanel = new LeftPanel(model);
		leftPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		leftPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		RightPanel rightPanel = new RightPanel(model);
		rightPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		rightPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
			leftPanel, rightPanel);
		splitPane.setDividerLocation(leftPanel.getPreferredSize().width);
		splitPane.setResizeWeight(1.0);
		splitPane.setContinuousLayout(true);


		// create groups pane

		final GroupModel groupModel = new GroupModel(session, this);
	
		edu.northwestern.at.wordhoard.swing.accounts.groups.GroupPanel groupPanel = new edu.northwestern.at.wordhoard.swing.accounts.groups.GroupPanel(groupModel);
		TitledBorder gt = new TitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), "All Groups", TitledBorder.CENTER, TitledBorder.ABOVE_TOP);
		groupPanel.setBorder(gt);
		groupPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		UserPanel userPanel = new UserPanel(model);
		TitledBorder ut = new TitledBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED), "All Users", TitledBorder.CENTER, TitledBorder.ABOVE_TOP);
		userPanel.setBorder(ut);
		userPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, groupPanel, userPanel);
		leftSplitPane.setDividerLocation(0.40);
		leftSplitPane.setResizeWeight(1.0);
		leftSplitPane.setContinuousLayout(true);

		GroupInspectorPanel groupInspectorPanel = new GroupInspectorPanel(groupModel);
		groupInspectorPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		groupInspectorPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		JSplitPane vertSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, groupInspectorPanel);
		vertSplitPane.setDividerLocation(leftSplitPane.getPreferredSize().width);
		vertSplitPane.setResizeWeight(1.0);
		vertSplitPane.setContinuousLayout(true);
		
		// create tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Accounts", splitPane);		
		tabbedPane.addTab("Groups", vertSplitPane);		
		setContentPane(tabbedPane);
		pack();
		model.init();
		groupModel.init();
		setLocation(new Point(3, WordHoardSettings.getTopSlop()));
		Dimension size = getSize();
		size.height = 600;
		setSize(size);
		positionNextTo(parentWindow);
		setVisible(true);

	}

	/**	Handles window dispose events. */

	public void dispose () {
		manageAccountsWindow = null;
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

