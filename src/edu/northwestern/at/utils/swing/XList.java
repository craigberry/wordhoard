package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.print.*;

import edu.northwestern.at.utils.swing.icons.*;
import edu.northwestern.at.utils.swing.printing.*;
import edu.northwestern.at.utils.*;

/**	A JList with different defaults and behavior.
 *
 *	<ul>
 *	<li>The font is set to XParameters.listFont if not null.
 *	<li>The fixed cell height is set to XParameters.listRowHeight if not 0.
 *	<li>Optional icons may be added to the list cell strings. List elements
 *		must implement the {@link AddIcon} interface.
 *	<li>The renderer never draws focus indicators.
 *	<li>The mouse motion listener is removed to avoid interference with
 *		drag gesture recognizers.
 *	<li>List selection changes are triggered on mouse clicked events rather
 *		than on mouse pressed events, also to avoid interference with
 *		drag gesture recognizers.
 *	</ul>
 *
 *	<p>The constructors are the same as in JList. We did not bother
 *	giving them their own javadoc.
 */

public class XList extends JList implements PrintableContents {

	/**	Default mouse listeners. */

	private MouseListener[] defaultMouseListeners;

	/**	Custom list cell renderer. Never draws focus indicators. */

	private static class Renderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent (JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list, value,
				index, isSelected, false);
			if (value instanceof AddIcon) ((AddIcon)value).addIcon(this);
			return this;
		}
	}

	public XList () {
		super();
		common();
	}

	public XList (ListModel dataModel) {
		super(dataModel);
		common();
	}

	public XList (Object[] listData) {
		super(listData);
		common();
	}

	public XList (Vector listData) {
		super(listData);
		common();
	}

	/**	Sets the default attributes. */

	private void common () {
		setBackground(Color.white);
		if (XParameters.listFont != null) setFont(XParameters.listFont);
		if (XParameters.listRowHeight != 0)
			setFixedCellHeight(XParameters.listRowHeight);
		setCellRenderer(new Renderer());
		MouseMotionListener[] mouseMotionListeners =
			(MouseMotionListener[])getListeners(MouseMotionListener.class);
		for (int i = 0; i < mouseMotionListeners.length; i++)
			removeMouseMotionListener(mouseMotionListeners[i]);
		defaultMouseListeners =
			(MouseListener[])getListeners(MouseListener.class);
		for (int i = 0; i < defaultMouseListeners.length; i++)
			removeMouseListener(defaultMouseListeners[i]);
		addMouseListener(mouseListener);
	}

	/**	Mouse listener interceptor.
	 *
	 *	<p>Modifies the selection on clicked events rather than pressed events.
	 */

	private MouseListener mouseListener =
		new MouseListener() {
			public void mouseClicked (MouseEvent event) {
				Component source = event.getComponent();
				long when = event.getWhen();
				int modifiers = event.getModifiers() |
					InputEvent.BUTTON1_MASK;
				if (Env.IS_JAVA_14_OR_LATER && Env.MACOSX)
					modifiers = modifiers | event.getModifiersEx() | 0x400;
				int x = event.getX();
				int y = event.getY();
				int clickCount = event.getClickCount();
				boolean popupTrigger = event.isPopupTrigger();
				MouseEvent newEvent = new MouseEvent(source,
					MouseEvent.MOUSE_PRESSED, when, modifiers, x, y,
					clickCount, popupTrigger);
				for (int i = 0; i < defaultMouseListeners.length; i++)
					defaultMouseListeners[i].mousePressed(newEvent);
			}
			public void mouseEntered (MouseEvent event) {
			}
			public void mouseExited (MouseEvent event) {
			}
			public void mousePressed (MouseEvent event) {
			}
			public void mouseReleased (MouseEvent event) {
			}
		};

	/** Prints the list.
	 *
	 *	@param	title			Title for output.
	 *	@param	printerJob		The printer job.
	 *	@param	pageFormat		The printer page format.
	 */

	public void printContents
	(
		final String title,
		final PrinterJob printerJob,
		final PageFormat pageFormat
	)
	{
		Thread runner = new Thread("Print list")
		{
			public void run()
			{
				PrintUtilities.printComponent(
					getPrintableComponent( title , pageFormat ),
					title,
					printerJob,
					pageFormat,
					true
				);
			}
		};

		runner.start();
	}

	/** Return printable component.
	 *
	 *	@param		title		Title for printing.
	 *
	 *	@param		pageFormat	Page format for printing.
	 *
	 *	@return					Printable component for XList.
	 */

	public PrintableComponent getPrintableComponent
	(
		String title,
		PageFormat pageFormat
	)
	{
		return
			new PrintableComponent
			(
				this ,
				pageFormat,
				new PrintHeaderFooter(
					title,
					"Printed " +
						DateTimeUtils.formatDateOnAt( new Date() ),
					"Page " )
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

