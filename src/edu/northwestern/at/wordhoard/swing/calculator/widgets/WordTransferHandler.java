package edu.northwestern.at.wordhoard.swing.calculator.widgets;

/*	Please see the license information at the end of this file. */

import java.awt.datatransfer.*;
import java.text.*;
import javax.swing.*;

import edu.northwestern.at.utils.*;
import edu.northwestern.at.utils.swing.*;
import edu.northwestern.at.wordhoard.model.search.*;
import edu.northwestern.at.wordhoard.model.wrappers.*;
import edu.northwestern.at.wordhoard.swing.*;
import edu.northwestern.at.wordhoard.swing.calculator.modelutils.*;
import edu.northwestern.at.wordhoard.swing.querytool.*;

/**	TransferHandler for drag and drop of Word information.
 */

public class WordTransferHandler extends TransferHandler
{
	/**	Transfer flavor. */

	DataFlavor xferFlavor;

	/**	Transfer type. */

	String xferType	=
		DataFlavor.javaJVMLocalObjectMimeType +
		";class=" + SearchCriteriaTransferData.class.getName();

	/**	Drag icon. */

	private ImageIcon dragIcon = null;

	/**	Create word transfer handler.
	 */

	public WordTransferHandler()
	{
		try
		{
			xferFlavor	= new DataFlavor( xferType );
		}
		catch ( ClassNotFoundException e )
		{
//			System.out.println(
//				"WordTransferHandler: unable to create data flavor" );
			Err.err( e );
		}
	}

	public WordTransferHandler( String property )
	{
		try
		{
			xferFlavor	= new DataFlavor( xferType );
		}
		catch ( ClassNotFoundException e )
		{
//			System.out.println(
//				"WordTransferHandler: unable to create data flavor" );
			Err.err( e );
		}
	}

	public boolean importData( JComponent c , Transferable t )
	{
		return true;
	}

	protected void exportDone( JComponent c , Transferable data , int action )
	{
	}

	private boolean haslocalFlavor( DataFlavor[] flavors )
	{
		if ( xferFlavor == null )
		{
			return false;
		}

		for ( int i = 0 ; i < flavors.length ; i++ )
		{
			if ( flavors[ i ].equals( xferFlavor ) )
			{
				return true;
			}
		}

		return false;
	}


	public boolean canImport( JComponent c , DataFlavor[] flavors )
	{
		return false;
	}

	protected Transferable createTransferable( JComponent c )
	{
		SearchCriteriaTransferData gos	= new SearchCriteriaTransferData();

		if ( c instanceof JTable )
		{
			try
			{
				int[] rows	= ((JTable)c).getSelectedRows();

				if ( rows.length < 0 ) return null;

				for ( int i = 0 ; i < rows.length; i++ )
				{
					String val	=
						((JTable)c).getValueAt( rows[ i ] , 0 ).toString();

					try
					{
								//	Remove trailing word class and
								//	homonym markers if present.

						val	= WordUtils.stripWordClass( val );

						Spelling s	= WordUtils.getSpellingForString( val );

						gos.add(
							new SpellingWithCollationStrength(
								s , Collator.TERTIARY ) );
					}
					catch ( Exception e )
					{
					}
				}

				return new SearchCriteriaTransferable( gos );
			}
			catch ( Exception e )
			{
				Err.err( e );
			}
		}

		return null;
	}

	public int getSourceActions( JComponent c )
	{
		return COPY_OR_MOVE;
	}

	public Icon getVisualRepresentation( Transferable t )
	{
		if ( dragIcon == null )
		{
			dragIcon	= Images.get( "icon.gif" );
		}

		return dragIcon;
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

