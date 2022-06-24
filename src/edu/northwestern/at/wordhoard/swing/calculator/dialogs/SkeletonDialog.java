package edu.northwestern.at.wordhoard.swing.calculator.dialogs;

/*	Please see the license information at the end of this file. */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import edu.northwestern.at.utils.swing.*;

/**	Base class providing a skeleton for most WordHoard calculator dialogs..
 */

public class SkeletonDialog extends ModalDialog
{
	/**	Parent window for dialog. */

	protected Window parentWindow		= null;

	/**	Default button, usually OK or Delete. */

	protected JButton okButton			= null;

	/**	Cancel button, usually Cancel ro Close. */

	protected JButton cancelButton		= null;

	/** Revert button, usually Revert. */

	protected JButton revertButton		= null;

	/**	OK button name. */

	protected String okButtonName		= "OK";

	/**	Cancel/close button name. */

	protected String cancelButtonName	= "Cancel";

	/**	Revert button name. */

	protected String revertButtonName	= "Revert";

	/**	True if dialog cancelled. */

	protected boolean cancelled			= false;

	/**	Create new dialog.
	 *
	 *	@param	dialogTitle			Title for dialog.
	 *	@param	parentWindow		Parent window for dialog.
	 *	@param	okButtonName		The OK button name.
	 *	@param	cancelButtonName	The Cancel button name.
	 *	@param	revertButtonName	The Revert button name.
	 */

	public SkeletonDialog
	(
		String dialogTitle ,
		Frame parentWindow ,
		String okButtonName ,
		String cancelButtonName ,
		String revertButtonName
	)
	{
		super( dialogTitle , parentWindow );

		this.parentWindow		= parentWindow;
    	this.okButtonName		= okButtonName;
    	this.cancelButtonName	= cancelButtonName;
    	this.revertButtonName	= revertButtonName;

								//	Selecting close button is the
								//	same as cancelling the dialog.

		setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );

		addWindowListener
		(
			new WindowAdapter()
			{
				public void windowClosing( WindowEvent event )
				{
					SwingUtilities.invokeLater
					(
						new Runnable()
						{
							public void run()
							{
								SkeletonDialog.this.handleCancelButtonPressed( null );
							}
						}
					);
				}
			}
		);
	}

	/**	Process revert button pressed.
	 *
	 *	@param	event	The event.
	 *
	 *	<p>
	 *	Allows overriding the Revert button handling from a subclass.
	 *	</p>
	 */

	protected void handleRevertButtonPressed( ActionEvent event )
	{
		initializeFields();
	}

	/**	Handles the Revert button. */

	protected ActionListener revert =
		new ActionListener()
		{
			public void actionPerformed( ActionEvent event )
			{
				handleRevertButtonPressed( event );
			}
		};

	/**	Process cancel button pressed.
	 *
	 *	@param	event	The event.
	 *
	 *	<p>
	 *	Allows overriding the Close/Cancel button handling from a subclass.
	 *	</p>
	 */

	protected void handleCancelButtonPressed( ActionEvent event )
	{
		cancelled	= true;
		dispose();
	}

	/**	Cancel button action listener.
     */

	protected ActionListener cancel =
		new ActionListener()
		{
			public void actionPerformed( ActionEvent event )
			{
				handleCancelButtonPressed( event );
			}
		};

	/**	Handles the OK button pressed.  Override in subclasses.
	 *
	 *	@param	event	The event.
	 */

	protected void handleOKButtonPressed( ActionEvent event )
	{
	}

	/**	OK button action listener. */

	protected ActionListener ok =
		new ActionListener()
		{
			public void actionPerformed( ActionEvent event )
			{
				handleOKButtonPressed( event );
			}
		};

	/**	Build the dialog. */

	protected void buildDialog()
	{
		LabeledColumn dialogFields	= new LabeledColumn();

		addFields( dialogFields );

		dialogFields.setBorder
		(
			BorderFactory.createEmptyBorder( 5 , 5 , 5 , 5 )
		);

		add( dialogFields );

		revertButton	= addButton( revertButtonName , revert );

		cancelButton	= addDefaultButton( cancelButtonName , cancel );

		okButton		= addButton( okButtonName , ok );

		setResizable( true );
	}

	/**	Initialize the dialog fields.  Override in subclasses. */

	protected void initializeFields()
	{
	}

	/** Adds fields to the dialog.  Override in subclasses.
	 *
	 *	@param	dialogFields	The component holding the dialog fields.
	 */

	protected void addFields( LabeledColumn dialogFields )
	{
		initializeFields();
	}

	/**	Enable all dialog fields.
	 */

	protected void enableAllDialogFields()
	{
	}

	/**	Disable all dialog fields.
	 */

	protected void disableAllDialogFields()
	{
	}

	/**	Get dialog cancelled flag.
	 *
	 *	@return		true if dialog cancelled, false otherwise.
	 */

	public boolean getCancelled()
	{
		return cancelled;
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

