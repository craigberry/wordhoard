package edu.northwestern.at.wordhoard.swing.accounts.groups;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;

/**	Group table component.
 */
 
class GroupMembersTable extends JTable {

	/**	Table cell renderer. */

	private class MyRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent (JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int col)
		{
			super.getTableCellRendererComponent(table, value, isSelected, false, row, col);
			setHorizontalAlignment(col <= 1 ? SwingConstants.LEADING : SwingConstants.CENTER);
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			return this;
		}
	}

	/**	Creates a new group table component.
	 *
	 *	@param	model	Group model.
	 */
	 
	GroupMembersTable (final TableModel model) {
		super(model);
		setColumnSelectionAllowed(false);
		setRowSelectionAllowed(true);
		setGridColor(Color.lightGray);
		setShowGrid(true);
		MyRenderer renderer = new MyRenderer();
		for (int col = 0; col < model.getColumnCount(); col++) {
			TableColumn column = getColumnModel().getColumn(col);
			column.setCellRenderer(renderer);
			Component comp = renderer.getTableCellRendererComponent(
				this, model.getColumnName(col), false, false, 0, col);
			int width = comp.getPreferredSize().width;
			String str = null;
			switch (col) {
				case 0: str = "name"; break;
			}
			comp = renderer.getTableCellRendererComponent(
				this, str, false, false, 0, col);
			width = Math.max(width, comp.getPreferredSize().width);
			column.setPreferredWidth(width);
		}
		if (model.getRowCount() > 0) setRowSelectionInterval(0, 0);
/*		getSelectionModel().addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged (ListSelectionEvent event) {
					if (event.getValueIsAdjusting()) return;
					model.setSelection(getSelectedRows());
				}
			}
		);
		model.addListener(
			new GroupMembersAdapter() {
				public void selectGroup (int index) {
					setRowSelectionInterval(index, index);
				}
				public void clearSelection () {
					GroupMembersTable.this.clearSelection();
				}
			}
		); */
	}

	public boolean getScrollableTracksViewportHeight() {
		// fetch the table's parent
		Container viewport = getParent();

		// if the parent is not a viewport, calling this isn't useful
		if (!(viewport instanceof JViewport)) {
			return false;
		}

		// return true if the table's preferred height is smaller
		// than the viewport height, else false
		return getPreferredSize().height < viewport.getHeight();
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

