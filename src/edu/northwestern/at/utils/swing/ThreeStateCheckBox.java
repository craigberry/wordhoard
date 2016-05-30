package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import javax.swing.*;

/**	ThreeStateCheckBox implements a three state checkbox.
 *
 *	<p>
 *	A three state check box can take on three values:  true, false,
 *	and null.  The true and false states work like a normal checkbox.
 *	The null states displays as an empty checkbox with a dark gray background.
 *	</p>
 */

public class ThreeStateCheckBox extends JCheckBox
{
	/** Create three state check box. */

	public ThreeStateCheckBox()
	{
		super();

		setModel( new ThreeStateToggleButtonModel() );
	}

	public ThreeStateCheckBox( Action a )
	{
		super( a );

		setModel( new ThreeStateToggleButtonModel() );
	}

	public ThreeStateCheckBox( Icon icon )
	{
		super( icon );

		setModel( new ThreeStateToggleButtonModel() );
	}

	public ThreeStateCheckBox( Icon icon , boolean selected )
	{
		super( icon , selected );

		setModel( new ThreeStateToggleButtonModel() );
	}

	public ThreeStateCheckBox( String text )
	{
		super( text );

		setModel( new ThreeStateToggleButtonModel() );
	}

	public ThreeStateCheckBox( String text, boolean selected )
	{
		super( text , selected );

		setModel( new ThreeStateToggleButtonModel() );
	}

	public ThreeStateCheckBox( String text , Icon icon )
	{
		super( text , icon );

		setModel( new ThreeStateToggleButtonModel() );
	}

	public ThreeStateCheckBox( String text , Icon icon , boolean selected )
	{
		super( text , icon , selected );

		setModel( new ThreeStateToggleButtonModel() );
	}

	/** Paint the check box. */

	public void paintComponent( Graphics g )
	{
		super.paintComponent( g );

								// If we are in the null selection state,
								// paint the interior of the check box grey.
								// Note:  we need to figure out how to
								// determine the exact location and size
								// of the checkbox to paint it correctly.
								// Right now the constants used below
								// work OK for metal and Windows look and feel.

		if ( getSelected() == null )
		{
			g.setColor( Color.gray );

			Rectangle rect = g.getClipBounds();

			g.fillRect( rect.x+5, rect.y+6, 11, 11 );
		}
	}

	/** Is checkbox selected?
	 *
	 *	@return		True, False, or Null.
	 */

	public Boolean getSelected()
	{
		return ((ThreeStateToggleButtonModel)model).getSelected();
	}

	/** Set selection status of checkbox.
	 *
	 *	@param	selected	True, false, or null.
	 */

	public void setSelected( Boolean selected )
	{
		((ThreeStateToggleButtonModel)model).setSelected( selected );
	}

	/** Get model for three state checkbox. */

	private ThreeStateToggleButtonModel myModel()
	{
		return (ThreeStateToggleButtonModel)model;
	}

	/** Three state toggle button model. */

	class ThreeStateToggleButtonModel extends JToggleButton.ToggleButtonModel
	{
		/** Save the selection state. */

		private Boolean selectionState;

		/** Get selected state. */

		public Boolean getSelected()
		{
			return selectionState;
		}

		/** Set selected state. */

		public void setSelected( Boolean selected )
		{
			selectionState = selected;

			super.setSelected(
				selectionState == null ?
					false : selectionState.booleanValue() );
		}

		/** Set selected state. */

		public void setSelected( boolean selected )
		{
								// Currently null?  Make it false. */

			if ( selectionState == null )
			{
				selectionState = new Boolean( true );
				super.setSelected( true );
			}
								// Currently true?  Make it false. */

			else if ( selectionState.booleanValue() && !selected )
			{
				selectionState = new Boolean( false );
				super.setSelected( false );
			}
								// Currently false?  Make it null. */

			else if ( !selectionState.booleanValue() && selected )
			{
				selectionState = null;
				super.setSelected( false );
			}
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

