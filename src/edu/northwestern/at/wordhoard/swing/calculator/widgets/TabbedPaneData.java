package edu.northwestern.at.wordhoard.swing.calculator.widgets;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.border.*;

/**	Holds information about a tabbed pane.
 */

public class TabbedPaneData
{
	/**	Tabbed pane. */

	protected JTabbedPane tabbedPane;

	/**	Title. */

	protected String title;

	/**	Tab index. */

	protected int index;

	/**	Tab icon. */

	protected Icon icon;

	/**	Tab tool tip. */

	protected String toolTip;

	/**	Tab component. */

	protected JComponent component;

	/**	Tab border. */

	protected Border border;

	/**	Create a tabbed pane data entry.
	 *
	 *	@param	tabbedPane	Tabbed pane.
	 *	@param	title		Tab title.
	 *	@param	index		Tab index.
	 *	@param	icon		Tab icon.
	 *	@param	toolTip		Tab tool tip.
	 *	@param	component	Tab component.
	 *	@param	border		Tab border.
	 */

	 public TabbedPaneData
	 (
	 	JTabbedPane tabbedPane ,
	 	String title ,
	 	int index ,
	 	Icon icon ,
	 	String toolTip ,
	 	JComponent component ,
	 	Border border
	 )
	 {
	 		this.tabbedPane	= tabbedPane;
		 	this.title		= title;
		 	this.index		= index;
	 		this.icon		= icon;
	 		this.toolTip	= toolTip;
	 		this.component	= component;
	 		this.border		= border;
	 }

	/**	Return the tabbed pane.
	 *
	 *	@return		The tabbed pane.
	 */

	public JTabbedPane getTabbedPane()
	{
		return tabbedPane;
	}

	/**	Return the title.
	 *
	 *	@return		The title.
	 */

	public String getTitle()
	{
		return title;
	}

	/**	Return the tab index.
	 *
	 *	@return		The tab index.
	 */

	public int getIndex()
	{
		return index;
	}

	/**	Return the icon.
	 *
	 *	@return		The icon.
	 */

	public Icon getIcon()
	{
		return icon;
	}

	/**	Return the tool tip.
	 *
	 *	@return		The tool tip.
	 */

	public String getToolTip()
	{
		return toolTip;
	}

	/**	Return the component.
	 *
	 *	@return		The component.
	 */

	public JComponent getComponent()
	{
		return component;
	}

	/**	Return the border.
	 *
	 *	@return		The border.
	 */

	public Border getBorder()
	{
		return border;
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

