package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import edu.northwestern.at.utils.sys.*;

/**	Builds an later message dialog with a traceback and informative text.
 *
 *	<p>This alert is used to report Java exceptions. It contains
 *	the stop icon, a title, an optional additional message
 *	supplied by the caller, optional text to display before the
 *	stack trace, and a full stack trace in a scrolling text area.
 *	</p>
 */

public class BuildAndShowAlert {

	/**	The PLAF error icon. */

	private static JLabel errorIcon =
		new JLabel(UIManager.getLookAndFeel().getDefaults().getIcon(
			"OptionPane.errorIcon"));

	/**	Builds and shows the alert.
	 *
	 *	<p>Must be called on the AWT event dispatching thread.</p>
	 *
	 *	@param	t				The throwable.
	 *
	 *	@param	title			The title.
	 *
	 *	@param	msg				The error message, or null if none.
	 *
	 *	@param	extraVerbiage	The extra verbiage, or null if none.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 */

	protected static void doBuildAndShowAlert (Throwable t,
		String title, String msg, String extraVerbiage,
		Frame parentWindow)
	{
		String stackTrace = new Date() + "\n" +
			StackTraceUtils.getStackTrace(t);

		String text = extraVerbiage == null ? stackTrace :
			extraVerbiage + "\n" + stackTrace;

		final ModalDialog dlog = new ModalDialog(null,parentWindow);
		dlog.setResizable(true);
//		JLabel titleLabel = new JLabel(title);
//		titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		dlog.setTitle(title);
		JTextArea stackTraceTextArea = new JTextArea(text, 20, 110);
		stackTraceTextArea.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
		stackTraceTextArea.setLineWrap(true);
		stackTraceTextArea.setFont(
			new Font("Monospaced", Font.PLAIN, 11));
		JScrollPane stackTraceScrollPane =
			new JScrollPane(stackTraceTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		stackTraceScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		JPanel box1 = new JPanel();
		box1.setLayout(new BoxLayout(box1, BoxLayout.Y_AXIS));
//		box1.add(titleLabel);
		if (msg != null) {
			JLabel msgLabel = new JLabel(msg);
			msgLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
			box1.add(Box.createVerticalStrut(15));
			box1.add(msgLabel);
		}
		box1.add(Box.createVerticalStrut(15));
		box1.add(stackTraceScrollPane);
		JPanel box2 = new JPanel();
		box2.setLayout(new BoxLayout(box2, BoxLayout.X_AXIS));
		errorIcon.setAlignmentY(Component.TOP_ALIGNMENT);
		box2.add(errorIcon);
		box2.add(Box.createHorizontalStrut(20));
		box1.setAlignmentY(Component.TOP_ALIGNMENT);
		box2.add(box1);
		dlog.add(box2);
		dlog.addDefaultButton("OK",
			new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					dlog.dispose();
				}
			}
		);
		dlog.show(parentWindow);
	}

	/**	Builds and shows the alert.
	 *
	 *	<p>Must be called on the AWT event dispatching thread.</p>
	 *
	 *	@param	t				The throwable.
	 *
	 *	@param	title			The title.
	 *
	 *	@param	msg				The error message, or null if none.
	 *
	 *	@param	extraVerbiage	The extra verbiage, or null if none.
	 *
	 *	@param	parentWindow	The parent window, or null if none.
	 */

	public static void buildAndShowAlert (final Throwable t,
		final String title, final String msg,
		final String extraVerbiage, final Frame parentWindow)
	{
		if (SwingUtilities.isEventDispatchThread()) {
			doBuildAndShowAlert(t, title, msg, extraVerbiage, parentWindow);
		} else {
			SwingUtilities.invokeLater(
				new Runnable() {
					public void run () {
						doBuildAndShowAlert(t, title, msg,
							extraVerbiage, parentWindow);
					}
				}
			);
		}
	}

	/** Don't allow instantiation, do allow overrides. */

	protected BuildAndShowAlert()
	{
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



