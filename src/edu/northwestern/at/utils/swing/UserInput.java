package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**	An input dialog for a string value which displays a title, prompt, and default.
 *
 *	<p>
 *	Displays a titled modal dialog with a query icon, a prompt string,
 *	an input field, and Cancel and OK buttons.  When OK is pressed
 *	the text entered in the input field is available using the
 *	getInputText() method.  When Cancel is pressed, getInputText() returns
 *	a null value for the input string.
 *	</p>
 *
 *	<p>
 *	<strong>Example:</strong>
 *	</p>
 *
 *	<p>
 *	<code>
 *								// Get user's name.
 *
 *		UserInput userInput =
 *				UserInput uidInput =
 *					new UserInput(
 *						parentFrame,
 *						"Your name" ),
 *						"Please enter your full name:",
 *						"" );
 *
 *								// Get user's input.
 *
 *		String fullName = userInput.getInputText();
 *
 *								// If input is null, Cancel button
 *								// was pressed.  Otherwise the text
 *								// is what the user entered.
 *		if ( fullName == null )
 *		{
 *			... handle name not entered
 *		}
 *		else
 *		{
 *			... handle name entered
 *		}
 *
 *	</code>
 *	</p>
 */

public class UserInput extends ModalDialog
{
	/** Holds input text. */

	private String inputText = new String();

	/**	The PLAF query icon. */

	private static JLabel queryIcon =
		new JLabel( UIManager.getLookAndFeel().getDefaults().getIcon(
			"OptionPane.questionIcon" ) );

	/** Create input dialog.
	 *
	 *	@param	parent			The parent window.
	 *	@param	title			The title for the dialog.
	 *	@param	promptText		The prompt text.
	 *	@param	defaultText		The default input text.
	 */

	public UserInput
	(
		Window parent,
		String title,
		String promptText,
		String defaultText
	)
	{
		super( title );
								// Create box to hold query icon,
								// prompt text, and input text field.

		JPanel box = new JPanel();

		box.setLayout( new BoxLayout( box , BoxLayout.X_AXIS ) );

		box.add( queryIcon );

		box.add( Box.createHorizontalStrut( 10 ) );

								// Holds input.

		final XTextField inputField = new XTextField( 32 );

                                // Initialize to default text if any.

		if ( defaultText == null )
		{
			inputField.setText( "" );
		}
		else
		{
			inputField.setText( defaultText );
		}
								// Add prompt text as label to dialog.

		box.add( new JLabel( promptText ) );

								// Add input text field to dialog.

		box.add( inputField );

								// Add container box to dialog.
		add( box );
								// Add Cancel button to dialog.
		addDefaultButton
		( "Cancel" ,
			new ActionListener()
			{
				public void actionPerformed( ActionEvent event )
				{
								// Input text is null when cancel pressed.

					inputText = null;
					dispose();
				}
			}
		);
								// Add OK button to dialog.
		addButton
		( "OK" ,
			new ActionListener()
			{
				public void actionPerformed( ActionEvent event )
				{
								// Set input text to value entered
								// by user.

					inputText = inputField.getText();
					dispose();
				}
			}
		);
								// Input field gets initial focus.

		setInitialFocus( inputField );

                                // Display the dialog.
		pack();
		show( parent );
	}

	/** Create input dialog.
	 *
	 *	@param	parent			The parent window.
	 *	@param	title			The title for the dialog.
	 *	@param	promptText		The prompt text.
	 */

	public UserInput
	(
		Window parent,
		String title,
		String promptText
	)
	{
		this( parent, title, promptText, "" );
	}

	/** Get input text.
	 *
	 *	@return		Input text if OK pressed, null if Cancel pressed.
	 */

	public String getInputText()
	{
		return inputText;
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

