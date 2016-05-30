package edu.northwestern.at.wordhoard.swing.calculator.widgets;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.io.StringReader;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.*;

import bsh.*;
import bsh.util.*;

import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.utils.swing.notepad.*;
import edu.northwestern.at.wordhoard.swing.*;

/**	Simple text editor for WordHoardCalc.
 */

public class WordHoardCalcSimpleEditor extends Notepad
{
	/** The script interpreter to which this editor belongs. */

	protected bsh.Interpreter interpreter;

	/**	Create editor.
	 *
	 *	@param	interpreter		The interpreter this editor is for.
	 */

	public WordHoardCalcSimpleEditor( bsh.Interpreter interpreter )
	{
		super();

		this.interpreter	= interpreter;

		if ( interpreter != null )
		{
			getAction( "run" ).setEnabled( true );

			getMenu( "run" ).setVisible( true );
			getMenu( "run" ).setEnabled( true );

			getMenuItem( "run" ).setVisible( true );
			getMenuItem( "run" ).setEnabled( true );

			getToolbarItem( "run" ).setVisible( true );
			getToolbarItem( "run" ).setEnabled( true );
        }
    }

	/**	Handle editor close.
	 */

	protected void doExit( ActionEvent e )
	{
		SwingUtils.closeFrame( getFrame() );
	}

	/**	Evaluate editor contents in script interpreter.
	 */

	protected void doRunAction( ActionEvent e )
	{
		new Thread( "Run script" )
		{
			public void run()
			{
				try
				{
					interpreter.eval( getEditorText() );
				}
				catch ( EvalError ex )
				{
					interpreter.print( ex.toString() );
				}
			}
		}.start();
	}

	/**	Add open file filter for script files.
	 */

	protected void doOpen( ActionEvent e )
	{
		FileExtensionFilter	filter	=
			new FileExtensionFilter( ".bsh" , "Script Files" );

		FileDialogs.addFileFilter( filter );

		super.doOpen( e );

		FileDialogs.clearFileFilters();
	}

	/**	Add save as file filter for script files.
	 */

	protected void doSaveAs( ActionEvent e )
	{
		FileExtensionFilter	filter	=
			new FileExtensionFilter( ".bsh" , "Script Files" );

		FileDialogs.addFileFilter( filter );

		super.doSaveAs( e );

		FileDialogs.clearFileFilters();
	}

	/**	Report an unexpected error.
	 *
	 *	@param	e	The exception.
	 */

	protected void reportUnexpectedError( Exception e )
	{
		Err.err( e );
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

