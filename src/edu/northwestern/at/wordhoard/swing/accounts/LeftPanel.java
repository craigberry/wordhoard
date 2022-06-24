package edu.northwestern.at.wordhoard.swing.accounts;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.swing.*;

/**	The left panel of the manage accounts window.
 */
 
class LeftPanel extends JPanel {

	/**	Creates a new left panel.
	 *
	 *	@param	model		Account model.
	 */
	 
	LeftPanel (final AccountModel model) {
	
		AccountTable table = new AccountTable(model);
		
		JScrollPane scrollPane = new JScrollPane(table,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
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
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
		
		plusButton.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					try {
						model.createAccount();
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
						model.deleteAccounts();
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

