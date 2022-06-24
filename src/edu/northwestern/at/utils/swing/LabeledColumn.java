package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import java.awt.*;

/**	A labeled column of Swing components.
 *
 *	<p>Using gridbag layouts to create labeled columns of components with
 *	proper alignment is one of the Swing programmer's less pleasant
 *	jobs. This class makes it easier.
 */

public class LabeledColumn extends JPanel {

	/**	Column 1 constraints (the column of labels). */

	private GridBagConstraints c1 = new GridBagConstraints();

	/**	Column 2 constraints (the column of components). */

	private GridBagConstraints c2 = new GridBagConstraints();

	/**	Column 2 constraints (the column of components). */

	private static GridBagConstraints c2a = new GridBagConstraints();

	/**	The much-maligned gridbag layout. */

	private GridBagLayout gridbag = new GridBagLayout();

	/**	Font. */

	private Font font;

	/**	Minimum label width. */

	private int minLabelWidth;

	/**	Maximum value width. */

	private int maxValueWidth;

	/**	Constructs a new labeled column panel.
	 *
	 *	@param	insets			Insets for components, or null to use the
	 *							default value of (5,5,5,5).
	 *
	 *	@param	font			Font for strings, or null to use the default
	 *							settings for JLabel components.
	 *
	 *	@param	minLabelWidth	Minimum width for label column, or 0 if
	 *							no minimum width.
	 *
	 *	@param	maxValueWidth	Maximum width for value column, or 0 if none.
	 *							Value strings longer than the maximum width
	 *							are word wrapped.
	 */

	public LabeledColumn (Insets insets, Font font, int minLabelWidth,
		int maxValueWidth)
	{
		super();
		this.font = font;
		this.minLabelWidth = minLabelWidth;
		this.maxValueWidth = maxValueWidth;
		if (insets == null) insets = new Insets(5, 5, 5, 5);
		initConstraints(insets);
		setLayout(gridbag);
	}

	/**	Constructs a new labeled column panel.
	 *
	 *	<p>The default insets (5,5,5,5) and the default JLabel font
	 *	are used, with no minimum label width or maximum value width.
	 */

	public LabeledColumn () {
		this(null, null, 0, 0);
	}

	/**	Initializes the constraints. */

	private void initConstraints (Insets insets) {

		c1.anchor = GridBagConstraints.EAST;
		c1.gridwidth = GridBagConstraints.RELATIVE;
		c1.fill = GridBagConstraints.NONE;
		c1.weightx = 0.0;
		c1.insets = insets;

		c2.anchor = GridBagConstraints.EAST;
		c2.gridwidth = GridBagConstraints.REMAINDER;
		c2.fill = GridBagConstraints.HORIZONTAL;
		c2.weightx = 1.0;
		c2.insets = insets;

		c2a.anchor = GridBagConstraints.EAST;
		c2a.gridwidth = GridBagConstraints.REMAINDER;
		c2a.fill = GridBagConstraints.BOTH;
		c2a.weightx = 1.0;
		c2a.weighty = 1.0;
		c2a.insets = new Insets(5, 5, 5, 5);

	}

	/** Adds a component to the panel.
	 *
	 *	@param	component	The component.
	 *
	 *	<p>
	 *	If the component appears to be a multiline
	 *	text field, its weights are set so that it
	 *	grows horizontally and vertically as much
	 *	as possible when the window is resized.
	 *	</p>
	 */

	protected void addComponent( JComponent component )
	{
		if ( component == null ) return;

		if (	( component instanceof JTextArea ) ||
			    ( component instanceof JTextPane ) ||
			    ( component instanceof JScrollPane ) ||
//			    ( component instanceof StyledTextEditor ) ||
//			    ( component instanceof FlaggedTextArea ) ||
//			    ( component instanceof FlaggedStyledTextEditor ) ||
			    ( component instanceof JTabbedPane )
			)
		{
			gridbag.setConstraints( component , c2a );
		}
		else
		{
			gridbag.setConstraints( component , c2 );
		}

		add( component );
	}

	/**	Adds a pair of components to the panel.
	 *
	 *	@param	label		Label component.
	 *
	 *	@param	value		Value component.
	 */

	public void addPair (JComponent label, JComponent value) {
		int labelLeftMargin = 0;
		int labelTopMargin = 0;
		int valueTopMargin = 0;
		if (label instanceof JLabel && value instanceof JLabel) {
			FontMetrics labelMetrics = getFontMetrics(label.getFont());
			FontMetrics valueMetrics = getFontMetrics(value.getFont());
			int labelAscent = labelMetrics.getAscent();
			int valueAscent = valueMetrics.getAscent();
			if (labelAscent > valueAscent) {
				valueTopMargin = labelAscent - valueAscent;
			} else if (valueAscent > labelAscent) {
				labelTopMargin = valueAscent - labelAscent;
			}
		}
		if (minLabelWidth > 0) {
			int width = label.getPreferredSize().width;
			int leftMargin = minLabelWidth-width;
			if (leftMargin > 0) labelLeftMargin = leftMargin;
		}
		if (labelTopMargin > 0 || labelLeftMargin > 0)
			label.setBorder(BorderFactory.createEmptyBorder(
				labelTopMargin, labelLeftMargin, 0, 0));
		gridbag.setConstraints(label, c1);
		add(label);
		if (valueTopMargin > 0)
			value.setBorder(BorderFactory.createEmptyBorder(
				valueTopMargin, 0, 0, 0));
		gridbag.setConstraints(value, c2);
//		add(value);
		addComponent(value);
	}

	/**	Adds a labeled component to the panel.
	 *
	 *	@param	label		The label.
	 *
	 *	@param	component	The component.
	 */

	public void addPair (String label, JComponent component) {
		boolean labelIsEmpty = label == null ? true :
			label.trim().length() == 0;
		JLabel lab = new JLabel(labelIsEmpty ? "" : label + ":");
		if (font != null) lab.setFont(font);
		addPair(lab, component);
	}

	/**	Adds a labeled string to the panel.
	 *
	 *	@param	label		The label.
	 *
	 *	@param	value		The string value. This is turned into one or
	 *						more JLabel components.
	 *
	 *	@param	valueFont	The font for the value string, or null to use
	 *						the same font as the label.
	 */

	public void addPair (String label, String value, Font valueFont) {
		int len = value.length();
		if (len == 0) {
			value = " ";
			len = 1;
		}
		while (len > 0) {
			JLabel valueLabel = new JLabel(value);
			if (valueFont != null) {
				valueLabel.setFont(valueFont);
			} else if (font != null) {
				valueLabel.setFont(font);
			}
			if (maxValueWidth > 0) {
				int width = valueLabel.getPreferredSize().width;
				int index = len;
				while (width > maxValueWidth) {
					index = value.lastIndexOf(' ', index-1);
					if (index == -1) break;
					String str = value.substring(0, index);
					valueLabel.setText(str);
					width = valueLabel.getPreferredSize().width;
				}
				if (index == -1 || index >= len-2) {
					len = 0;
				} else {
					value = value.substring(index+1);
					len = value.length();
				}
			} else {
				len = 0;
			}
			addPair(label, valueLabel);
			label = null;
		}
	}

	/**	Adds a labeled string to the panel.
	 *
	 *	@param	label		The label.
	 *
	 *	@param	value		The string value. This is turned into one or
	 *						more JLabel components.
	 */

	public void addPair (String label, String value) {
		addPair(label, value, null);
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

