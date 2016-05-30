package edu.northwestern.at.wordhoard.swing.accounts.groups;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;

import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.utils.swing.*;

import edu.northwestern.at.wordhoard.model.userdata.*;
import edu.northwestern.at.wordhoard.server.*;
import edu.northwestern.at.wordhoard.server.model.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.UserGroupUtils;


/**	The group panel of the manage groups window.
 */
 
public class GroupPanel extends JPanel {

	/**	True if we are currently creating a new userGroup. */
	
	private boolean creatingNewGroup = false;
	
	/**	Set of listeners. */
	
	private HashSet listeners = new HashSet();
	
	/**	list of groups. */
	
	private	JComboBox comboBox = null;

	/**	Creates a new left panel.
	 *
	 *	@param	model		Group model.
	 */
	 
 public	GroupPanel (final GroupModel model) {
	
/*
		comboBox = new JComboBox();
		comboBox.setAlignmentY(Component.CENTER_ALIGNMENT);
		for (int i=0;i< model.getRowCount();i++ ) {
			comboBox.addItem(model.getValueAt(i,0));
		}
		
		comboBox.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
			//		UserGroup group = (UserGroup)comboBox.getSelectedItem();
					model.selectGroup(comboBox.getSelectedIndex());
				}
			}
		);

*/		
		GroupTable table = new GroupTable(model);
		JScrollPane scrollPane = new JScrollPane(table,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));
		scrollPane.getViewport().setBackground(Color.white);
		JScrollBar hBar = scrollPane.getHorizontalScrollBar();
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		int hBarHeight = hBar.getPreferredSize().height;
		int vBarWidth = vBar.getPreferredSize().width;
		Dimension d = table.getPreferredSize();
		d.width += vBarWidth + 20;
		d.height += hBarHeight + 40;
		scrollPane.setPreferredSize(d);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

		PlusOrMinusButton plusButton = new PlusOrMinusButton(true);
		PlusOrMinusButton minusButton = new PlusOrMinusButton(false);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(plusButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(minusButton);
		buttonPanel.add(Box.createHorizontalStrut(10));
		buttonPanel.add(new JLabel("Add/Delete Group"));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		setLayout(new BorderLayout());

//		add(comboBox, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
//		add(comboBox, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		plusButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						model.createGroup();
					} catch (Exception e) {
						model.err(e);
					}
				}
			}
		);
		
		minusButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						model.deleteGroups();
					} catch (Exception e) {
						model.err(e);
					}
				}
			}
		);
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

