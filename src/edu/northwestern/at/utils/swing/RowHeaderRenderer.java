package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.Component;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class RowHeaderRenderer
    extends DefaultTableCellRenderer
    implements ListCellRenderer
{
    protected Border noFocusBorder, focusBorder;

    public RowHeaderRenderer()
    {
        setOpaque(true);
        setBorder(noFocusBorder);
    }
/*
    public void updateUI()
    {
        super.updateUI();

        Border cell = UIManager.getBorder("TableHeader.cellBorder");
        Border focus = UIManager.getBorder("Table.focusCellHighlightBorder");

        focusBorder = new BorderUIResource.CompoundBorderUIResource(cell, focus);

        Insets i = focus.getBorderInsets(this);

        noFocusBorder = new BorderUIResource.CompoundBorderUIResource
             (cell, BorderFactory.createEmptyBorder(i.top, i.left, i.bottom, i.right));

        // Alternatively, if focus shouldn't be supported:

	    focusBorder = noFocusBorder = cell;

        focusBorder = noFocusBorder = null;
    }
*/

    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean selected, boolean focused)
    {
        if (list != null)
        {
            if (selected)
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            setFont(list.getFont());

            setEnabled(list.isEnabled());
        }
        else
        {
            setBackground(UIManager.getColor("TableHeader.background"));
            setForeground(UIManager.getColor("TableHeader.foreground"));
            setFont(UIManager.getFont("TableHeader.font"));
            setEnabled(true);
        }

        if (focused)
            setBorder(focusBorder);
        else
            setBorder(noFocusBorder);

        setValue(value);

        return this;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                       boolean selected, boolean focused, int row, int column)
    {
        if (table != null)
        {
            if (selected)
            {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            else
            {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            setFont(table.getFont());

            setEnabled(table.isEnabled());
        }
        else
        {
            setBackground(UIManager.getColor("TableHeader.background"));
            setForeground(UIManager.getColor("TableHeader.foreground"));
            setFont(UIManager.getFont("TableHeader.font"));
            setEnabled(true);
        }

        if (focused)
            setBorder(focusBorder);
        else
            setBorder(noFocusBorder);

        setValue(value);

        return this;
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

