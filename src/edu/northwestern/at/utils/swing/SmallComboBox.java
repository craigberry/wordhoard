package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**	A small combo box.
 *
 *	<p>We use a JLabel plus a JPopupMenu to implement a small combo box
 *	control. The control uses an API similar to JComboBox. We have not
 *	implemented a complete API, just a small subset.
 */
 
public class SmallComboBox extends JPanel {

	/**	The label. */

	private JLabel label = new JLabel("x");
	
	/**	Filled down triangle. */
	
	private FilledTriangle triangle;
	
	/**	Popup menu. */
	
	private JPopupMenu menu = new JPopupMenu();
	
	/**	Font. */
	
	private Font font;
	
	/**	The size of this control. */
	
	private Dimension size;
	
	/**	Triangle width. */
	
	private int triangleWidth;
	
	/**	Currently selected item. */
	
	private Object selectedItem;
	
	/**	Index of the currently selected item. */
	
	private int selectedIndex;
	
	/**	A label used for measuring alternative strings. */
	
	private JLabel measuringLabel = new JLabel("x");
	
	/**	Menu item height. */
	
	private int menuItemHeight;
	
	/**	Action listeners. */
	
	private ArrayList actionListeners = new ArrayList();
	
	/**	Items. */
	
	private ArrayList items = new ArrayList();
	
	/**	Creates a new small combo box.
	 *
	 *	@param	font	Font.
	 */

	public SmallComboBox (Font font) {
		super();
		this.font = font;
		label.setFont(font);
		label.setBorder(BorderFactory.createEmptyBorder(1,2,1,2));
		measuringLabel.setFont(font);
		measuringLabel.setBorder(BorderFactory.createEmptyBorder(1,2,1,2));
		menu.setFont(font);
		FontMetrics metrics = label.getFontMetrics(font);
		int w = metrics.getHeight() - 5;
		if (w % 2 == 1) w++;
		triangle = new FilledTriangle(false, w);
		triangleWidth = triangle.getPreferredSize().width;
		addMouseListener(
			new MouseAdapter() {
				public void mousePressed (MouseEvent event) {
					menu.show(SmallComboBox.this, 0, 0);
				}
			}
		);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(label);
		panel.add(Box.createHorizontalStrut(2));
		panel.add(triangle);
		panel.add(Box.createHorizontalStrut(3));
		panel.setBorder(BorderFactory.createLineBorder(Color.gray));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(panel);
		size = getPreferredSize();
	}
	
	/**	Adds an item to the combo box.
	 *
	 *	@param	obj		The item.
	 */
	
	public void addItem (final Object obj) {
		items.add(obj);
		final int index = menu.getComponentCount();
		final String str = obj.toString();
		JMenuItem item = menu.add(str);
		item.setFont(font);
		item.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					label.setText(str);
					selectedItem = obj;
					selectedIndex = index;
					event = new ActionEvent(SmallComboBox.this,
						selectedIndex, null);
					for (Iterator it = actionListeners.iterator(); 
						it.hasNext(); ) 
					{
						ActionListener listener = (ActionListener)it.next();
						listener.actionPerformed(event);
					}
				}
			}
		);
		measuringLabel.setText(str);
		int labelWidth = measuringLabel.getPreferredSize().width;
		size.width = Math.max(size.width, labelWidth + triangleWidth + 5);
		setPreferredSize(size);
		setMaximumSize(size);
		if (index == 0) {
			label.setText(str);
			selectedItem = obj;
			menuItemHeight = item.getPreferredSize().height;
		}
	}
	
	/**	Gets the index of the currently selected item.
	 *
	 *	@return		Index of currently selected item.
	 */
	
	public int getSelectedIndex () {
		return selectedIndex;
	}
	
	/**	Gets the currently selected item.
	 *
	 *	@return		Currently selected item.
	 */
	
	public Object getSelectedItem () {
		return selectedItem;
	}
	
	/**	Sets the index of the currently selected item.
	 *
	 *	@param	selectedIndex		Index of the currently selected item.
	 */
	 
	public void setSelectedIndex (int selectedIndex) {
		if (selectedIndex < 0 || selectedIndex >= menu.getComponentCount())
			return;
		this.selectedIndex = selectedIndex;
		selectedItem = items.get(selectedIndex);
		String str = selectedItem.toString();
		label.setText(str);
		menu.getSelectionModel().setSelectedIndex(selectedIndex);
	}
	
	/**	Sets the currently selected item.
	 *
	 *	@param	selectedItem		Currently selected item.
	 */
	 
	public void setSelectedItem (Object selectedItem) {
		int index = items.indexOf(selectedItem);
		if (index < 0) return;
		setSelectedIndex(index);
	}
	
	/**	Removes all the items. */
	
	public void removeAllItems () {
		selectedItem = null;
		selectedIndex = 0;
		label.setText("x");
		for (int i = menu.getComponentCount()-1; i >= 0; i--)
			menu.remove(i);
	}
	
	/**	Sets an item enabled or disabled.
	 *
	 *	@param	index		Item index.
	 *
	 *	@param	enabled		True if enabled.
	 */
	 
	public void setEnabled (int index, boolean enabled) {
		JMenuItem menuItem = (JMenuItem)menu.getComponent(index);
		menuItem.setEnabled(enabled);
	}
	
	/**	Sets an item enabled or disabled.
	 *
	 *	@param	item		Item.
	 *
	 *	@param	enabled		True if enabled.
	 */
	 
	public void setEnabled (Object item, boolean enabled) {
		int index = items.indexOf(item);
		if (index < 0) return;
		setEnabled(index, enabled);
	}
	
	/**	Returns true if an item is enabled.
	 *
	 *	@param	index		Item index.
	 *
	 *	@return				True if item is enabled.
	 */
	 
	public boolean isEnabled (int index) {
		JMenuItem menuItem = (JMenuItem)menu.getComponent(index);
		return menuItem.isEnabled();
	}
	
	/**	Returns true if an item is enabled.
	 *
	 *	@param	item		Item.
	 *
	 *	@return				True if item is enabled.
	 */
	 
	public boolean isEnabled (Object item) {
		int index = items.indexOf(item);
		if (index < 0) return false;
		return isEnabled(index);
	}
	
	/**	Adds an action listener.
	 *
	 *	@param	listener	Action listener.
	 */
	
	public void addActionListener (ActionListener listener) {
		actionListeners.add(listener);
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

