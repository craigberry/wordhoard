package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.security.*;
import java.awt.event.*;
import javax.swing.*;

/**	Displays toolbar.
 */

public class DataToolBar extends JToolBar
{
	protected JButton buttonPasteToClipboard;
	protected JButton buttonSaveFile;

	private boolean denySaveSecurity;
	private JFileChooser fileChooser;

	private DataPanel dataPanel;

	public DataToolBar( DataPanel dataPanel )
	{
		super();

		this.dataPanel	= dataPanel;

		try
		{
			fileChooser	= new JFileChooser( new File( "." ) );
		}
		catch ( AccessControlException ace )
		{
			denySaveSecurity	= true;
		}

		try
		{
			buttonPasteToClipboard	=
				new JButton
				(
					new ImageIcon
					(
//						PlotPanel.class.getResource
						DataToolBar.class.getResource
						(
//							"edu/northwestern/at/utils/math/plots/resources/toclipboard.png"
							"resources/toclipboard.png"
						)
					)
				);

			buttonPasteToClipboard.setToolTipText(
				"Copy data to ClipBoard" );

			final DataPanel finalDataPanel	= dataPanel;

			buttonPasteToClipboard.addActionListener
			(
				new ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						finalDataPanel.toClipBoard();
					}
				}
			);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		try
		{
			buttonSaveFile	=
				new JButton
				(
					new ImageIcon
					(
//						PlotPanel.class.getResource
						DataToolBar.class.getResource
						(
//							"edu/northwestern/at/utils/math/plots/resources/tofile.png"
							"resources/tofile.png"
						)
					)
				);

			buttonSaveFile.setToolTipText(
				"Save data into ASCII file" );

			buttonSaveFile.addActionListener
			(
				new ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						chooseFile();
					}
				}
			);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}

		if ( buttonSaveFile != null ) add( buttonSaveFile , null );
		if ( buttonPasteToClipboard != null ) add( buttonPasteToClipboard , null );

		if ( !denySaveSecurity && ( fileChooser != null ) )
		{
			fileChooser.addActionListener
			(
				new java.awt.event.ActionListener()
				{
					public void actionPerformed( ActionEvent e )
					{
						saveFile();
					}
				}
			);
		}
		else
		{
			if ( buttonSaveFile != null )
			{
				buttonSaveFile.setEnabled( false );
			}
		}
	}

	void saveFile()
	{
		java.io.File file	= fileChooser.getSelectedFile();

		dataPanel.toASCIIFile( file );
	}

	void chooseFile()
	{
		fileChooser.showSaveDialog( this );
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

