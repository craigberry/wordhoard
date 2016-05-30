package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;

/** Methods for input key handling. */

public class InputKeys
{
	/**	Return KeyStroke for specified key character.
	 *
	 *	@param	keyName		The accelerator key name.
	 *
	 *	@return				The KeyEvent for this key name.
	 *
	 *	<p>
	 *	Only parly implemented!
	 *	</p>
	 *
	 */

	public static int getKeyEvent( String keyName )
	{
		int result	= KeyEvent.CHAR_UNDEFINED;

		if ( ( keyName == null ) || ( keyName.length() == 0 ) )
		{
			return result;
		}

		switch ( keyName.charAt( 0 ) )
		{
			case 'A'	:	result	= KeyEvent.VK_A;
							break;

			case 'B'	:	result	= KeyEvent.VK_B;
							break;

			case 'C'	:	result	= KeyEvent.VK_C;
							break;

			case 'D'	:	result	= KeyEvent.VK_D;
							break;

			case 'E'	:	result	= KeyEvent.VK_E;
							break;

			case 'F'	:	result	= KeyEvent.VK_F;
							break;

			case 'G'	:	result	= KeyEvent.VK_G;
							break;

			case 'H'	:	result	= KeyEvent.VK_H;
							break;

			case 'I'	:	result	= KeyEvent.VK_I;
							break;

			case 'J'	:	result	= KeyEvent.VK_J;
							break;

			case 'K'	:	result	= KeyEvent.VK_K;
							break;

			case 'L'	:	result	= KeyEvent.VK_L;
							break;

			case 'M'	:	result	= KeyEvent.VK_M;
							break;

			case 'N'	:	result	= KeyEvent.VK_N;
							break;

			case 'O'	:	result	= KeyEvent.VK_O;
							break;

			case 'P'	:	result	= KeyEvent.VK_P;
							break;

			case 'Q'	:	result	= KeyEvent.VK_Q;
							break;

			case 'R'	:	result	= KeyEvent.VK_R;
							break;

			case 'S'	:	result	= KeyEvent.VK_S;
							break;

			case 'T'	:	result	= KeyEvent.VK_T;
							break;

			case 'U'	:	result	= KeyEvent.VK_U;
							break;

			case 'V'	:	result	= KeyEvent.VK_V;
							break;

			case 'W'	:	result	= KeyEvent.VK_W;
							break;

			case 'X'	:	result	= KeyEvent.VK_X;
							break;

			case 'Y'	:	result	= KeyEvent.VK_Y;
							break;

			case 'Z'	:	result	= KeyEvent.VK_Z;
							break;

			case '0'	:	result	= KeyEvent.VK_0;
							break;

			case '1'	:	result	= KeyEvent.VK_1;
							break;

			case '2'	:	result	= KeyEvent.VK_2;
							break;

			case '3'	:	result	= KeyEvent.VK_3;
							break;

			case '4'	:	result	= KeyEvent.VK_4;
							break;

			case '5'	:	result	= KeyEvent.VK_5;
							break;

			case '6'	:	result	= KeyEvent.VK_6;
							break;

			case '7'	:	result	= KeyEvent.VK_7;
							break;

			case '8'	:	result	= KeyEvent.VK_8;
							break;

			case '9'	:	result	= KeyEvent.VK_9;
		}

		return result;
	}

	/**	Simulate a key event for a component.
	 *
	 *	@param	keyEvent	The key event.
	 *	@param	component	The component to receive the event.
	 *
	 *	@throws	Exception	On any kind of exception.
	 *
	 *	<p>
	 *	A component will ignore events unless it appears to be focused.
	 *	We set the the private field "focusManagerIsDispatching" in
	 *	AWTEvent to true using reflection to allow the key event to take
	 *	effect.
	 *	</p>
	 */

	public static void simulateKeyEvent
	(
		KeyEvent keyEvent ,
		Component component
	)
		throws Exception
	{
		Field[] fields	= AWTEvent.class.getDeclaredFields();

		for ( int i = 0 ; i < fields.length ; i++ )
		{
			if ( fields[ i ].getName().equals( "focusManagerIsDispatching" ) )
			{
				fields[ i ].setAccessible( true );
				fields[ i ].set( keyEvent , Boolean.TRUE );

				component.dispatchEvent( keyEvent );

				return;
			}
		}
	}

	/**	Don't allow instantiation but do allow overrides. */

	protected InputKeys()
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

