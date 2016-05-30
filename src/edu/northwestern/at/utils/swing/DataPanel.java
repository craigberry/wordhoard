package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** DataPanel.
 */

public abstract class DataPanel extends JPanel
	implements ComponentListener
{
	protected DataToolBar toolBar;
	protected XScrollPane scrollPane;

	public static int[] dimension =
		new int[] { 400 , 400 };

	public DataPanel()
	{
		setLayout( new BorderLayout() );
		initToolBar();
		init();
	}

	protected void initToolBar()
	{
		toolBar	= new DataToolBar( this );

		add( toolBar , BorderLayout.NORTH );

		toolBar.setFloatable( false );
	}

	protected void init()
	{
		setSize( dimension[ 0 ] , dimension[ 1 ] );

		setPreferredSize( new Dimension( dimension[ 0 ] , dimension[ 1 ] ) );

		addComponentListener( this );
	}

	public void update()
	{
		this.remove( scrollPane );
		toWindow();
		this.updateUI();
	}

	protected abstract void toWindow();

	public abstract void toClipBoard();

	public abstract void toASCIIFile( File file );

	public void componentHidden( ComponentEvent e )
	{
	}

	public void componentMoved( ComponentEvent e )
	{
	}

	public void componentResized( ComponentEvent e )
	{
		dimension =
			new int[]
			{
				(int)( this.getSize().getWidth() ) ,
				(int)( this.getSize().getHeight() )
			};
	}

	public void componentShown( ComponentEvent e )
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

