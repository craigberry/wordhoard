package edu.northwestern.at.utils.swing;

/*	Please see the license information at the end of this file. */

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.undo.*;
import javax.swing.plaf.basic.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.awt.Cursor;
import java.io.*;
import java.util.*;
import java.net.*;

/** Cooperating input field and list.
 *
 *	<p>
 *	An input list consists of a JTextField and an XList
 *	operating in tandem.  As text is typed into the input field,
 *	the current selection in the list is changed to match to the
 *	input field and list.
 *
 *	<p>
 *	<strong>Example:</strong>
 *	</p>
 *
 *	<p>
 *	<code>
 *		JTextField inputField = new JTextField( 32 );
 *		InputList inputList = new InputList( inputField );
 *	</code>
 *	</p>
 *
 *	<p>
 *	This ties together inputField and inputList.  Changes to
 *	one field are reflected in the other.
 *	</p>
 */

public class InputList extends XList
{
	/** Associated text field holds input text. */

	protected JTextField inputField;

	/** Create input list.
	 *
	 *	@param inputField		Holds current user input.
	 */

	public InputList( JTextField inputField )
	{
		super();

		this.inputField = inputField;

								// Only one item can be selected in the
								// list at a time.

		getSelectionModel().setSelectionMode(
			ListSelectionModel.SINGLE_SELECTION );

								// Listen for changes to input field.

		this.inputField.getDocument().addDocumentListener( documentListener );

								// Listen for changes to list.

		addListSelectionListener( listSelectionListener );

								// List font should match input field font.

		setFont( inputField.getFont() );

								// Room for displaying up to five
								// list entries at once.

		setVisibleRowCount( 5 );
	}

	/** Append text entries to input list.
	 *
	 *	@param	listEntries		List of text entries to add.
	 */

	public void appendListEntries( String [] listEntries )
	{
								// Clear input field.

		inputField.setText( "" );

								// Get list model.

		DefaultListModel listModel = new DefaultListModel();

								// Add entries.

		for ( int i = 0 ; i < listEntries.length ; i++ )
		{
			listModel.addElement( listEntries[ i ] );
		}

		setModel( listModel );

                                // Select first list entry.

		if ( listModel.getSize() > 0 )
			setSelectedIndex( 0 );
	}

	/** Get input text field.
	 *
	 *	@return		Input text field.
	 */

	public String getInputText()
	{
		return inputField.getText();
	}

	/** Handle selection change in list. */

	protected ListSelectionListener listSelectionListener =
		new ListSelectionListener()
		{
			public void valueChanged( ListSelectionEvent event )
			{
				Object obj = getSelectedValue();

				if ( obj != null )
				{
								// If we come here as the result of a list
								// selection change caused by a change
								// to the inputField contents, we will get an
								// IllegalStateException.
								// We can ignore that since the inputField is
								// already set to match the selected list
								// element in this case.
					try
					{
						String s = obj.toString();

						inputField.setText( s );

						inputField.setCaretPosition( s.length() );
					}
					catch ( IllegalStateException e )
					{
					}
		        }
			}
		};

	/** Look for changes to input text field. */

	protected DocumentListener documentListener =
		new DocumentListener()
		{
			public void insertUpdate( DocumentEvent e )
			{
				handleInputFieldChange();
			}

			public void removeUpdate( DocumentEvent e )
			{
				handleInputFieldChange();
			}

			public void changedUpdate( DocumentEvent e )
			{
				handleInputFieldChange();
			}
		};

	/** Handle change to input text field.
	 *
	 *	<p>
	 *	As the input field changes as the result of user input,
	 *	find the nearest matching list entry and make it the
	 *	current selected list element.
	 *	</p>
	 */

	public void handleInputFieldChange()
	{
		ListModel model = getModel();

								// Get input typed by user.

		String key = inputField.getText().toLowerCase();

								// Scroll suggestions list to matching
								// entry, if any.

		for ( int i = 0 ; i < model.getSize() ; i++ )
		{
			String data = (String)model.getElementAt( i );

			if ( data.toLowerCase().startsWith( key ) )
			{
				setSelectedValue( data , true );
				break;
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

