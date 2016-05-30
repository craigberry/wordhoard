package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.*;

import javax.swing.*;

import edu.northwestern.at.utils.swing.printing.*;

/**	Extended JScrollPane.
 *
 *	<p>
 *	Subclasses JScrollPane to delegate drag and drop events to associated
 *	view component, if the view supports drag and drop; to support
 *	printing of the component being scrolled; and to pass on cut/copy/paste
 *	and select all events to the component being scrolled.
 *	</p>
 */

public class XScrollPane extends JScrollPane
	implements	DropTargetListener,
				PrintableContents,
				SaveToFile,
				SelectAll
{
	protected DropTargetListener dropTargetListener = null;
	protected DropTarget dropTarget;

	/** Create empty scroll pane without associated view. */

	public XScrollPane()
	{
		super();
	}

	/** Create scroll pane with associated view.
	 *
	 *	@param	view		The associated view component being scrolled.
	 */

	public XScrollPane( Component view )
	{
		super( view );
								// If viewport component being scrolled
								// is a drop target listener,
								// remember that so we can delegate
								// drop events we receive to it.

		if ( view instanceof DropTargetListener )
		{
			dropTargetListener	= (DropTargetListener)view;
			dropTarget			= new DropTarget( this , this );
		}
	}

	/** Create scroll pane with associated view and scroll bar policies.
	 *
	 *	@param	view		The associated view component being scrolled.
	 *	@param	vsbPolicy	Specifies the vertical scrollbar policy.
	 *	@param	hsbPolicy	Specifies the horizontal scrollbar policy.
	 */

	public XScrollPane( Component view, int vsbPolicy, int hsbPolicy )
	{
		super( view, vsbPolicy, hsbPolicy );

		if ( view instanceof DropTargetListener )
		{
			dropTargetListener	= (DropTargetListener)view;
			dropTarget			= new DropTarget( this , this );
		}
	}

	/** Create scroll pane with scroll bar policies.
	 *
	 *	@param	vsbPolicy	Specifies the vertical scrollbar policy.
	 *	@param	hsbPolicy	Specifies the horizontal scrollbar policy.
	 */

	public XScrollPane( int vsbPolicy , int hsbPolicy )
	{
		super( vsbPolicy , hsbPolicy );
	}

	/**	Ensure enclosed XTable is repainted when scroll pane is repainted.
	 */

	public void paint( Graphics g )
	{
		super.paint( g );

		Component view	= getViewport().getView();

		if ( view instanceof XTable )
		{
			view.repaint();
		}
	}

	/** Handle drag enter event.
	 *
	 *	@param	dtde	The drag enter event.
	 */

	public void dragEnter( DropTargetDragEvent dtde )
	{
		if ( dropTargetListener != null )
		{
			dropTargetListener.dragEnter( dtde );
		}
	}

	/** Handle drag over event.
	 *
	 *	@param	dtde	The drag over event.
	 */

	public void dragOver( DropTargetDragEvent dtde )
	{
		if ( dropTargetListener != null )
		{
			dropTargetListener.dragOver( dtde );
		}
	}

	/** Handle drop action changed event.
	 *
	 *	@param	dtde	The drop action changes  event.
	 */

	public void dropActionChanged( DropTargetDragEvent dtde )
	{
		if ( dropTargetListener != null )
		{
			dropTargetListener.dropActionChanged( dtde );
		}
	}

	/** Handle drag exit event.
	 *
	 *	@param	dte		The drag exit event.
	 */

	public void dragExit( DropTargetEvent dte )
	{
		if ( dropTargetListener != null )
		{
			dropTargetListener.dragExit( dte );
		}
	}

	public void drop( DropTargetDropEvent dtde )
	{
								// Pass drop to associated view.

		if ( dropTargetListener != null )
		{
			dropTargetListener.drop( dtde );
		}
	}

	/** Set drop target listener.
	 *
	 *	@param	dropTargetListener		The delegate drop target listener.
	 */

	public void setDropTargetListener( DropTargetListener dropTargetListener )
	{
		this.dropTargetListener = dropTargetListener;
		this.dropTarget			= new DropTarget( this , this );
	}

	/** Return printable component.
	 *
	 *	@param		title		Title for printing.
	 *	@param		pageFormat	Page format for printing.
	 *
	 *	@return					Printable component.
	 *
	 *	<p>
	 *	The printable component is the component wrapped by this scroll pane.
	 *	</p>
	 */

	public PrintableComponent getPrintableComponent
	(
		String title,
		PageFormat pageFormat
	)
	{
		PrintableComponent result	= null;

								//	Get component wrapped by the
								//	scroll pane.

		Component component	= getViewport().getView();

								//	If the wrapped components knows
								//	how to print itself, return it
								//	directly, otherwise wrap it with
								//	a PrintableComponent.

		if ( component instanceof PrintableContents )
		{
			result	=
				((PrintableContents)component).getPrintableComponent(
					title , pageFormat );
		}
		else
		{
        	result	= new PrintableComponent( getViewport().getView() );
        }

        return result;
	}

	/**	Selects all text in the enclosed view.
	 */

	public void selectAll()
	{
		Component view	= getViewport().getView();

		if ( view instanceof SelectAll )
		{
			((SelectAll)view).selectAll();
		}
	}

	/**	Checks if "select all" enabled.
	 *
	 *	@return		returns true if select all enabled.
	 */

	public boolean isSelectAllEnabled()
	{
		boolean result	= false;

		Component view	= getViewport().getView();

		if ( view instanceof SelectAll )
		{
			result	= ((SelectAll)view).isSelectAllEnabled();
		}

		return result;
	}

	/**	Selects all text in the enclosed view.
	 */

	public void unselect()
	{
		Component view	= getViewport().getView();

		if ( view instanceof SelectAll )
		{
			((SelectAll)view).unselect();
		}
	}

	/**	Checks if "select all" enabled.
	 *
	 *	@return		returns true if select all enabled.
	 */

	public boolean isUnselectEnabled()
	{
		boolean result	= false;

		Component view	= getViewport().getView();

		if ( view instanceof SelectAll )
		{
			result	= ((SelectAll)view).isUnselectEnabled();
		}

		return result;
	}

	/**	Save data in scrolled component a file.
	 *
	 *	@param	fileName	Name of file to save results to.
	 *
	 *	<p>
	 *	Can save any data where the scrolled component implements
	 *	the SaveToFile interface.  Override this if you need other types
	 *	of saves.
	 *	</p>
	 */

	public void saveToFile( String fileName )
	{
		Component results	= getViewport().getView();

		if ( results instanceof SaveToFile )
		{
			((SaveToFile)results).saveToFile( fileName );
		}
	}

	/**	Save data in scrolled component.
	 *
	 *	@param	parentWindow	Parent window for file dialog.
	 *
	 *	<p>
	 *	Runs a file dialog to get the name of the file to which to
	 *	save the results.
	 *	</p>
	 *
	 *	<p>
	 *	Can save any data where the scrolled component implements
	 *	the SaveToFile interface.  Override this if you need other types
	 *	of saves.
	 *	</p>
	 */

	public void saveToFile( Window parentWindow )
	{
		Component results	= getViewport().getView();

		if ( results instanceof SaveToFile )
		{
			((SaveToFile)results).saveToFile( parentWindow );
		}
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

