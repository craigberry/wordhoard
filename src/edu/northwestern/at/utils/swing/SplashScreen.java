package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;

import edu.northwestern.at.utils.*;

/**	Displays a splash screen.
 */

public class SplashScreen extends JFrame
{
	/**	Labels holding splash screen text. */

	protected JLabel label1;
	protected JLabel label2;
	protected JLabel label3;
	
	/**	True when done. */
	
	private boolean done;

	/**	Create a splash screen.
	 *
	 *	@param	title		Title for splash screen.
	 *	@param	loadingText	Text for loading message, e.g., "Loading..." .
	 *	@param	waitText	Text asking user to wait, e.g., "Please wait...".
	 *	@param	icon		Icon for splash screen.
	 */

    public SplashScreen
    (
    	String title ,
    	String loadingText ,
    	String waitText ,
    	ImageIcon icon
    )
    {
    	super(title);
		if ((icon != null) && (icon.getImage() != null)) {
			setIconImage(icon.getImage());
		}
		label1 = new JLabel(loadingText);
		label1.setBorder(BorderFactory.createEmptyBorder(0,0,20,0));
		Font font = label1.getFont();
		font = new Font(font.getName(), font.getStyle(), 18);
		label1.setFont(font);
		label1.setAlignmentX(Component.CENTER_ALIGNMENT);
		label2 = new JLabel(waitText);
		label2.setBorder(BorderFactory.createEmptyBorder(0,0,15,0));
		label2.setAlignmentX(Component.CENTER_ALIGNMENT);
		label3 = new JLabel("0");
		font = new Font(font.getName(), font.getStyle(), 10);
		label3.setFont(font);
		label3.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(label1);
		panel.add(label2);
		panel.add(label3);
		panel.setBorder(BorderFactory.createEmptyBorder(40,40,40,40));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setContentPane(panel);
		pack();

		SwingUtilities.invokeLater
		(
			new Runnable()
			{
				public void run()
				{
					updateContents();
				}
			}
		);
	}

	/**	Update splash screen contents.
	 */

	protected void updateContents()
	{
		WindowPositioning.centerWindowOverWindow(this, null, 0);
		setVisible(true);

		new Thread(
			new Runnable() {
				public void run () {
					long startTime = System.currentTimeMillis();
					while (!done) {
						try {
							Thread.sleep(1000);
							long time = System.currentTimeMillis();
							long seconds = Math.round((time-startTime)/1000.0);
							final String str = Long.toString(seconds);
							SwingUtilities.invokeLater(
								new Runnable() {
									public void run () {
										label3.setText(str);
										label3.paintImmediately(
											label3.getVisibleRect() );
									}
								}
							);
						} catch (Exception e) {
						}
					}
				}
			}
		).start();
	}
	
	/**	Disposes the window.
	 */
	 
	public void dispose () {
		done = true;
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

